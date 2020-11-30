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
    private static MyApp INSTANCE;
    private Storage storage = new Storage();
    private String unitTemp;


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (isLightTheme()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        unitTemp = getUnit();

    }

    //чтение настроек темы
    protected boolean isLightTheme(){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        return preferences.getBoolean(Constants.IS_LIGHT_THEME, true);
    }
    //чтение настроек единиц измерения температуры
    protected String getUnit(){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(Constants.IS_UNIT_C, "\u2103");
    }

    //сохранение настироек темы
    protected void setLightTheme(boolean isLightTheme){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.IS_LIGHT_THEME, isLightTheme);
        editor.apply();
    }
    //сохранение настироек единиц измерения температуры
    protected void setUnit(String UnitTemp){
        SharedPreferences preferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.IS_UNIT_C, UnitTemp);
        editor.apply();
    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }

    private int IDdefaultCity = 524894;

}
