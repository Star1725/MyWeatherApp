package com.geekbrains.myweatherapp;

import android.app.Application;
import android.util.Log;

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
        Log.d(TAG, this.getClass().getSimpleName() + " onCreate() " + INSTANCE.getStorage().getUnitTemp());



    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }
}
