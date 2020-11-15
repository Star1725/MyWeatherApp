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

import java.util.Calendar;

import lombok.Setter;

public class FragmentShowWeatherInCity extends Fragment {
    private boolean orientationIsLand;

    public static void setCurrentCity(City currentCity) {
        FragmentShowWeatherInCity.currentCity = currentCity;
    }

    private static City currentCity;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private TextView tvUnit;

    @NonNull
    static FragmentShowWeatherInCity create(City city){
        FragmentShowWeatherInCity fragmentShowWeatherInCity = new FragmentShowWeatherInCity();

        if (city != null){
            currentCity = city;
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
        tvUnit = view.findViewById(R.id.tvUnits);

        TextView tvCurrentDate = view.findViewById(R.id.tv_current_date);
        Calendar calendar = Calendar.getInstance();
        tvCurrentDate.setText(getDate(calendar));

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
        if (city == null) city = MyApp.getINSTANCE().getDefaultCity();
        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " showWeatherInCity(): city = " + city.getName());
        }
        tvNameCites.setText(city.getName());
        tvTemperatureCites.setText(String.valueOf(city.getTemp()));
        imageViewWeatherCites.setImageResource(city.getImageWeatherID());
        tvUnit.setText(MyApp.getINSTANCE().getStorage().getUnitTemp());
    }

    private String getDate(Calendar calendar){
        return "today " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onSaveInstanceState(): city = " + currentCity.getName());
        }
        saveInstanceState.putParcelable(Constants.CITY_EXTRA, currentCity);
    }
}
