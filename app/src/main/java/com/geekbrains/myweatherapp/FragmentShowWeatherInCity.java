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

public class FragmentShowWeatherInCity extends Fragment {
    private boolean orientationIsLand;
    private City currentCity;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private TextView tvUnit;

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

        Button buttonInfoCity = view.findViewById(R.id.button_info_city);
        imageViewWeatherCites = view.findViewById(R.id.imageView);
        tvNameCites = view.findViewById(R.id.tvNameCites);
        tvTemperatureCites = view.findViewById(R.id.tvTemperature);
        tvUnit = view.findViewById(R.id.tvUnits);

        TextView tvCurrentDate = view.findViewById(R.id.tv_current_date);
        Calendar calendar = Calendar.getInstance();
        tvCurrentDate.setText(getDate(calendar));

//        currentCity = MyApp.getINSTANCE().getDefaultCity();
//        settingViews(currentCity);

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
    }

    private void showWeatherInCity(City city) {
        tvNameCites.setText(city.getName());
        tvTemperatureCites.setText(String.valueOf(city.getTemp()));
        imageViewWeatherCites.setImageResource(city.getImageWeatherID());
        tvUnit.setText(MyApp.getINSTANCE().getStorage().getUnitTemp());

        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " showImagesCountry(): orientationIsLand = " + orientationIsLand);
        }
        if (orientationIsLand) {
            FragmentChoiceCity choiceCity = (FragmentChoiceCity) getFragmentManager().findFragmentById(R.id.cities);

            if (choiceCity == null) ;
            choiceCity = new FragmentChoiceCity();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.cities, choiceCity);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }

    private String getDate(Calendar calendar){
        return "today " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    @Override
    public void onStart() {
        super .onStart();
        if (currentCity != null){
            showWeatherInCity(currentCity);
        }
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onStart()");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onSaveInstanceState()");
        }
        saveInstanceState.putParcelable(Constants.CITY_EXTRA, currentCity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (savedInstanceState != null){
            if (Logger.VERBOSE){
                Log.d(Logger.TAG, getClass().getSimpleName() + " onActivityCreated(): savedInstanceState = " + (savedInstanceState != null) + " orientationIsLand = " + orientationIsLand);
            }
            currentCity = savedInstanceState.getParcelable(Constants.CITY_EXTRA);
        } else {
            if (Logger.VERBOSE){
                Log.d(Logger.TAG, getClass().getSimpleName() + " onActivityCreated(): savedInstanceState = " + (savedInstanceState != null) + " orientationIsLand = " + orientationIsLand);
            }
            currentCity = MyApp.getINSTANCE().getDefaultCity();
        }
    }
}
