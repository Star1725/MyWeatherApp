package com.geekbrains.myweatherapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherapp.City;
import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.Logger;
import com.geekbrains.myweatherapp.MainActivity;
import com.geekbrains.myweatherapp.MyApp;
import com.geekbrains.myweatherapp.MyThermometer;
import com.geekbrains.myweatherapp.adapters.MyRVAdapterHorizontal;
import com.geekbrains.myweatherapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class FragmentShowWeatherInCity extends Fragment {

    private Context context;

    private City currentCity;
    private static String currentUnitTemp;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private TextView tvPressureCites;
    private TextView tvHumidityCites;
    private MyRVAdapterHorizontal myRVAdapterHorizontal;
    private RecyclerView rvTempHourHorizontal;
    private TextView tvCurrentDate;
    private MyThermometer myThermometer;

    @NonNull
    public FragmentShowWeatherInCity create(City city){
        FragmentShowWeatherInCity fragmentShowWeatherInCity = new FragmentShowWeatherInCity();

        if (city != null){
            currentCity = city;
        } else {
            currentCity = null;
        }

        return fragmentShowWeatherInCity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.VERBOSE){
            Log.v(Logger.TAG, getClass().getSimpleName() + " onCreateView()");
        }
        context = inflater.getContext();
        return inflater.inflate(R.layout.fragment_show_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Logger.VERBOSE){
            Log.v(Logger.TAG, getClass().getSimpleName() + " onViewCreated():");
        }
        City citySaved = null;
        if (savedInstanceState != null){
            citySaved = savedInstanceState.getParcelable(Constants.CITY_EXTRA);
        }

        imageViewWeatherCites = view.findViewById(R.id.imageView);
        tvNameCites = view.findViewById(R.id.tvNameCites);
        tvTemperatureCites = view.findViewById(R.id.tvTemperature);
        tvPressureCites = view.findViewById(R.id.textView_pressure);
        tvHumidityCites = view.findViewById(R.id.textView_humidity);

        myThermometer = view.findViewById(R.id.myThermometer);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);

        //WorkNetHandler.registerObserverCallback(this);//регистрируемся на прослушивание результатов запросов к серверу для погоды на конкретный город

        rvTempHourHorizontal = view.findViewById(R.id.rv_temp_for_hour);

        if (currentCity == null || currentCity.equals(citySaved)){
            showWeatherInCity(citySaved);
        } else {
            showWeatherInCity(currentCity);
        }
    }

    public void showWeatherInCity(City city) {
        currentUnitTemp = MyApp.getINSTANCE().getUnitTemp();

        if (city != null){
            if (Logger.VERBOSE){
                Log.v(Logger.TAG, getClass().getSimpleName() + " showWeatherInCity(): city = " + city.getName());
            }
            currentCity = city;
            ((AppCompatActivity) tvNameCites.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCurrentDate.setText(new java.text.SimpleDateFormat("dd.MM.yyyy").format(new Date (city.getDt()*1000)));
                    tvNameCites.setText(city.getName());
                    tvPressureCites.setText(String.valueOf(Math.round(city.getPressure()/1.33)));
                    tvHumidityCites.setText(String.valueOf(city.getHumidity()));

                    LinearLayoutManager llmHorizontal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    myRVAdapterHorizontal = new MyRVAdapterHorizontal(city);
                    imageViewWeatherCites.setImageResource(MyApp.getINSTANCE().getMapImages().get(city.getIcon()));
                    //Picasso.get().load(Constants.START_URL_FOR_DOWNLOAD_ICON + city.getIcon() + Constants.END_URL_FOR_DOWNLOAD_ICON).into(imageViewWeatherCites);
                    if (!MainActivity.orientationIsLand){
                        myThermometer.setLevelTemp((int)Math.round(city.getCurrentTemp()));
                        myThermometer.invalidate();
                        rvTempHourHorizontal.setLayoutManager(llmHorizontal);
                        rvTempHourHorizontal.setAdapter(myRVAdapterHorizontal);
                        rvTempHourHorizontal.scrollToPosition(getCurrentHour(city));
                    } else {
                        tvTemperatureCites.setText(String.format("%d %s", Math.round(city.getCurrentTemp()), currentUnitTemp));
                    }
                }
            });
        }
    }

    private int getCurrentHour(City city){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(city.getDt()*1000);
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (Logger.VERBOSE && currentCity != null) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onSaveInstanceState(): city = " + currentCity.getName());
        }
        saveInstanceState.putParcelable(Constants.CITY_EXTRA, currentCity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onResume()");
        }
        if (!currentUnitTemp.equals(MyApp.getINSTANCE().getUnitTemp())){
            currentUnitTemp = MyApp.getINSTANCE().getUnitTemp();
            showWeatherInCity(currentCity);
        }
    }
}
