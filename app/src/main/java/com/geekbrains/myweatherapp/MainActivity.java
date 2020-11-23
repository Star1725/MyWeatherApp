package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Calendar;

import lombok.Getter;

@Getter
public class MainActivity extends AppCompatActivity implements FragmentChoiceCity.OnSelectedCityListener{
    private final static int REQUEST_CODE = 1;
    private City currentCity;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private TextView tvUnit;
    private boolean orientationIsLand;
    private FragmentChoiceCity fragmentChoiceCity;
    private FragmentShowWeatherInCity fragmentShowWeatherInCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientationIsLand = " + orientationIsLand);
        }

        if (!orientationIsLand) {
            if (fragmentShowWeatherInCity == null){
                if (Logger.VERBOSE) {
                    Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientation - no Land(): fragmentShowWeatherInCity == null");
                }
                fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).commit();
            } else {
                if (Logger.VERBOSE) {
                    Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientation - no Land(): fragmentShowWeatherInCity != null");
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).commit();
            }
        } else {
            Fragment weatherInCity = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(weatherInCity instanceof FragmentShowWeatherInCity)){
                if (Logger.VERBOSE) {
                    Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientation - Land(): weatherInCity != fragmentShowWeatherInCity");
                }
                weatherInCity = fragmentShowWeatherInCity;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, weatherInCity);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            } else {
                if (Logger.VERBOSE) {
                    Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientation - Land(): weatherInCity = fragmentShowWeatherInCity");
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, weatherInCity);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }


        }
    }
//подписка на фрагменты ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof FragmentChoiceCity) {
            fragmentChoiceCity = (FragmentChoiceCity) fragment;
            if (Logger.VERBOSE) {
                Log.d(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentChoiceCity");
            }
            fragmentChoiceCity.setCallback(this);
        } else if (fragment instanceof FragmentShowWeatherInCity){
            fragmentShowWeatherInCity = (FragmentShowWeatherInCity) fragment;
            if (Logger.VERBOSE) {
                Log.d(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentShowWeatherInCity");
            }
        }
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
                fragmentChoiceCity = new FragmentChoiceCity();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentChoiceCity).addToBackStack("").commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCitySelected(City city) {
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCitySelected(): city = " + city.getName());
        }
        if (orientationIsLand) {
            fragmentShowWeatherInCity = (FragmentShowWeatherInCity) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragmentShowWeatherInCity != null){
                fragmentShowWeatherInCity.showWeatherInCity(city);
            }
        } else {
            fragmentShowWeatherInCity.create(city);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();

        }
    }
}
