package com.geekbrains.myweatherapp;

import android.app.Application;

import lombok.Getter;

@Getter
public class MyApp extends Application {
    private static MyApp INSTANCE;
    Storage storage = new Storage();

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }
}
