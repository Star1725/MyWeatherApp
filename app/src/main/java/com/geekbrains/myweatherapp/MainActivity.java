package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class MainActivity extends AppCompatActivity implements FragmentChoiceCity.OnSelectedCityListener, WorkNetHandler.ResultRequestCallback{
    private final static int REQUEST_CODE = 1;

    static boolean orientationIsLand;
    private FragmentChoiceCity fragmentChoiceCity;
    private List<City> cityList;
    private FragmentShowWeatherInCity fragmentShowWeatherInCity;
    private City currentCity;
    private static WorkNetHandler workNetHandler = new WorkNetHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientationIsLand = " + orientationIsLand);
        }
        WorkNetHandler.registerObserverCallback(this);
        if (currentCity == null){
            workNetHandler.getCityWithWeather(MyApp.getINSTANCE().getIDdefaultCity());
        } else {
            workNetHandler.getCityWithWeather(currentCity.getId());
        }
        workNetHandler.getListCitiesWithTemp(Arrays.stream(getResources().getIntArray(R.array.id_city)).boxed().collect(Collectors.toList()));
        if (!orientationIsLand) {

            if (fragmentShowWeatherInCity == null){
                fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();

        } else {
            Fragment weatherInCity = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragmentShowWeatherInCity == null) {
                fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
            }
            if (!(weatherInCity instanceof FragmentShowWeatherInCity)){
                weatherInCity = fragmentShowWeatherInCity;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, weatherInCity);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }
//подписка на фрагменты ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof FragmentChoiceCity) {
            fragmentChoiceCity = (FragmentChoiceCity) fragment;
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentChoiceCity");
            }
            fragmentChoiceCity.setCallback(this);
        } else if (fragment instanceof FragmentShowWeatherInCity){
            fragmentShowWeatherInCity = (FragmentShowWeatherInCity) fragment;
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentShowWeatherInCity");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    // методы меню//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (orientationIsLand){
            getMenuInflater().inflate(R.menu.menu_main_land, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent1);
                return true;
            case R.id.choices_city:
                fragmentChoiceCity = FragmentChoiceCity.create(cityList);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentChoiceCity).addToBackStack("").commit();
                return true;
            case R.id.info:
                DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(getString(R.string.about));
                dialogFragmentInfo.show(getSupportFragmentManager(), "dialogInfo" );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCitySelected(City city) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCitySelected(): city = " + city.getName());
        }
        if (orientationIsLand) {
            fragmentShowWeatherInCity = (FragmentShowWeatherInCity) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragmentShowWeatherInCity != null){
                workNetHandler.getCityWithWeather(city.getId());
                //fragmentShowWeatherInCity.showWeatherInCity(city);
                showSnackbar(city, "Вы выбрали ");
            }
        } else {
            workNetHandler.getCityWithWeather(city.getId());
            fragmentShowWeatherInCity.create(null);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();
            showSnackbar(city, "Вы выбрали ");
        }
    }

    private void showSnackbar(City city, String msg) {
        Snackbar.make(findViewById(R.id.fragment_container), msg + city.getName(), Snackbar.LENGTH_LONG).setDuration(3000).show();
    }

    @Override
    public void callingBackCity(City city, String status) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackCity(): " + status + " " + (city != null));
        }
        if (status.equals(Constants.FAIL_CONNECTION)){
            showDialogError(status);
        } else if(city != null){
            currentCity = city;
            if (fragmentShowWeatherInCity.isResumed()){
                fragmentShowWeatherInCity.showWeatherInCity(city);
            }
        }
    }

    @Override
    public void callingBackListCity(List<City> cityList, String status) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackListCity(): " + status + " " + (cityList != null));
        }
        if (status.equals(Constants.FAIL_CONNECTION)){
            showDialogError(status);
        } else if(cityList != null){
            this.cityList = cityList;
            if (fragmentChoiceCity != null && fragmentChoiceCity.isResumed()){
                fragmentChoiceCity.showListCities(cityList);
            }
        }
    }

    private void showDialogError(String status){
        DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(status);
        dialogFragmentInfo.show(getSupportFragmentManager(), "dialogError" );
    }
}
