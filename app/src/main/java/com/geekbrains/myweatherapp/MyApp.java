package com.geekbrains.myweatherapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.geekbrains.myweatherapp.dao.HistoryDao;
import com.geekbrains.myweatherapp.database.HistoryDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyApp extends Application {

    private static MyApp INSTANCE;
    private Storage storage = new Storage();
    private String unitTemp;
    private HashMap<String, Integer> mapImages;

    private HistoryDatabase historyDatabase;


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
        mapImages = initMap();

        historyDatabase = HistoryDatabase.createDB();

    }

    // Получаем EducationDao для составления запросов
    public HistoryDao getHistoryDao() {
        return historyDatabase.getHistoryDao();
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

    private HashMap<String, Integer> initMap(){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("01d", R.drawable.ic_01d);
        map.put("01n", R.drawable.ic_01n);
        map.put("02d", R.drawable.ic_02d);
        map.put("02n", R.drawable.ic_02n);
        map.put("03d", R.drawable.ic_03d);
        map.put("03n", R.drawable.ic_03d);
        map.put("04d", R.drawable.ic_04d);
        map.put("04n", R.drawable.ic_04d);
        map.put("09d", R.drawable.ic_09d);
        map.put("09n", R.drawable.ic_09d);
        map.put("10d", R.drawable.ic_10d);
        map.put("10n", R.drawable.ic_10n);
        map.put("11d", R.drawable.ic_11d);
        map.put("11n", R.drawable.ic_11d);
        map.put("13d", R.drawable.ic_13d);
        map.put("13n", R.drawable.ic_13d);
        map.put("50d", R.drawable.ic_50d);
        map.put("50n", R.drawable.ic_50d);
        return map;
    }

}
