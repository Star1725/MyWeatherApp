package com.geekbrains.myweatherapp;

import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;

import java.util.ArrayList;
import java.util.List;

interface ResultRequestCallback {
    void callingBackCity(City city, String status);
    void callingBackArrayCities(List<City> cities, String status);
}
