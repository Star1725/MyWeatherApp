package com.geekbrains.myweatherapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyApp extends Application {
    private static final String TAG = "myLog";
    private static MyApp INSTANCE;
    private Storage storage = new Storage();
    private boolean lightTheme = true;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (isLightTheme()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

    //чтение настроек темы
    protected boolean isLightTheme(){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        return preferences.getBoolean(Constants.IS_LIGHT_THEME, true);
    }
    //сохранение настироек
    protected void setLightTheme(boolean isLightTheme){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.IS_LIGHT_THEME, isLightTheme);
        editor.apply();
    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }

    private City defaultCity = new City("Moscow", 15, new ArrayList<>(Arrays.asList(0, 3, 6, 9, 12, 15, 18, 21)), R.drawable.ic_sun_svg);

}
