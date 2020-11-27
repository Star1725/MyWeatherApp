package com.geekbrains.myweatherapp;

import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;

interface ResultRequestCallback {
    void callingBack(City city, String status);
}
