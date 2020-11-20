package com.geekbrains.myweatherapp;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import lombok.Setter;

public class FragmentShowWeatherInCity extends Fragment {
    private boolean orientationIsLand;

    public static void setCurrentCity(City currentCity) {
        FragmentShowWeatherInCity.currentCity = currentCity;
    }

    private static City currentCity;
    private static String currentUnitTemp;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private MyRVAdapterHorizontal myRVAdapterHorizontal;
    private RecyclerView rvTempHourHorizontal;

    @NonNull
    static FragmentShowWeatherInCity create(City city){
        FragmentShowWeatherInCity fragmentShowWeatherInCity = new FragmentShowWeatherInCity();

        if (city != null){
            currentCity = city;
        } else {
            currentCity = city = MyApp.getINSTANCE().getDefaultCity();
        }

        if (Logger.VERBOSE){
            Log.d(Logger.TAG, FragmentShowWeatherInCity.class.getSimpleName() +  " create(): city = " + city.getName());
        }
        return fragmentShowWeatherInCity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " onCreateView()");
        }
        return inflater.inflate(R.layout.fragment_show_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " onViewCreated():");
        }
        City citySaved = null;
        if (savedInstanceState != null){
            citySaved = savedInstanceState.getParcelable(Constants.CITY_EXTRA);
            if (Logger.VERBOSE){
                Log.d(Logger.TAG, getClass().getSimpleName() + "                 citySaved = " + citySaved.getName());
            }
        }

        Button buttonInfoCity = view.findViewById(R.id.button_info_city);
        imageViewWeatherCites = view.findViewById(R.id.imageView);
        tvNameCites = view.findViewById(R.id.tvNameCites);
        tvTemperatureCites = view.findViewById(R.id.tvTemperature);

        TextView tvCurrentDate = view.findViewById(R.id.tv_current_date);
        //TextView tvCurrentTime = view.findViewById(R.id.tv_current_time);
        Calendar calendar;
        calendar = Calendar.getInstance(TimeZone.getTimeZone(""));
        tvCurrentDate.setText(getDate(calendar));
        //tvCurrentTime.setText(getTime(calendar));
        rvTempHourHorizontal = view.findViewById(R.id.rv_temp_for_hour);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_info_city:
                        String url = "https://yandex.ru/pogoda/" + currentCity.getName();
                        if (Logger.VERBOSE) {
                            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onClick(): url = " + url);
                        }
                        Uri uri = Uri.parse(url);
                        Intent intentGET = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intentGET);
                        break;
                }
            }
        };
        buttonInfoCity.setOnClickListener(onClickListener);

        if (currentCity == null || currentCity.equals(citySaved)){
            showWeatherInCity(citySaved);
        } else {
            showWeatherInCity(currentCity);
        }
    }

    void showWeatherInCity(City city) {
        boolean orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (city == null){
            currentCity = city = MyApp.getINSTANCE().getDefaultCity();
        }
        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " showWeatherInCity(): city = " + city.getName());
        }
        setCurrentCity(city);
        tvNameCites.setText(city.getName());
        currentUnitTemp = MyApp.getINSTANCE().getStorage().getUnitTemp();

        LinearLayoutManager llmHorizontal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        myRVAdapterHorizontal = new MyRVAdapterHorizontal(currentCity);
        imageViewWeatherCites.setImageResource(city.getImageWeatherID());
        if (!orientationIsLand){
            rvTempHourHorizontal.setLayoutManager(llmHorizontal);
            rvTempHourHorizontal.setAdapter(myRVAdapterHorizontal);
            rvTempHourHorizontal.scrollToPosition(6);
        } else {
            tvTemperatureCites.setText(String.format("%d %s", city.getTemp(), currentUnitTemp));
        }
    }

    private String getDate(Calendar calendar){
        return "today " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    private int getTime(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0) return 0;
        else return 1;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onSaveInstanceState(): city = " + currentCity.getName());
        }
        saveInstanceState.putParcelable(Constants.CITY_EXTRA, currentCity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onResume()");
        }
        if (!currentUnitTemp.equals(MyApp.getINSTANCE().getStorage().getUnitTemp())){
            currentUnitTemp = MyApp.getINSTANCE().getStorage().getUnitTemp();
            showWeatherInCity(currentCity);
        }
    }
}
