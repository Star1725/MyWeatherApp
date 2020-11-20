package com.geekbrains.myweatherapp;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

@Getter
public class MyApp extends Application {
    private static final String TAG = "myLog";
    private static MyApp INSTANCE;
    private Storage storage = new Storage();

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }

    private City defaultCity = new City("Moscow", 15, new ArrayList<>(Arrays.asList(0, 2, 4, 8, 10, 12, 14, 16, 18, 20, 22)), R.drawable.ic_sun_svg);

}
