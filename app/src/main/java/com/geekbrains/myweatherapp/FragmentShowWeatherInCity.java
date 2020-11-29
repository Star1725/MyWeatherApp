package com.geekbrains.myweatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        tvCurrentDate = view.findViewById(R.id.tv_current_date);

        //WorkNetHandler.registerObserverCallback(this);//регистрируемся на прослушивание результатов запросов к серверу для погоды на конкретный город

        rvTempHourHorizontal = view.findViewById(R.id.rv_temp_for_hour);

        if (currentCity == null || currentCity.equals(citySaved)){
            showWeatherInCity(citySaved);
        } else {
            showWeatherInCity(currentCity);
        }
    }

    void showWeatherInCity(City city) {
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
                    imageViewWeatherCites.setImageResource(city.getImageWeatherID());
                    if (!MainActivity.orientationIsLand){
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
        if (Logger.VERBOSE) {
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

//    @Override
//    public void callingBackCity(City city, String status) {
//        if (city == null){
//            if (Logger.VERBOSE) {
//                Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBack(): " + status);
//            }
//            DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(status);
//            dialogFragmentInfo.show(getActivity().getSupportFragmentManager(), "dialogError" );
//        } else {
//            currentCity = city;
//            showWeatherInCity(city);
//        }
//    }
//
//    @Override
//    public void callingBackArrayCities(List<City> cities, String status) {
//
//    }

//    @Override
//    public void callingBackData(City city, List<City> cityList, String status) {
//        if (city == null){
//            if (Logger.VERBOSE) {
//                Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackData(): " + status);
//            }
//            DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(status);
//            dialogFragmentInfo.show(getActivity().getSupportFragmentManager(), "dialogError" );
//        } else {
//            currentCity = city;
//            showWeatherInCity(city);
//        }
//    }
}
