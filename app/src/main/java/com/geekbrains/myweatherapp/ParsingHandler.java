package com.geekbrains.myweatherapp;

import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;
import com.google.gson.Gson;

import java.util.List;

public class ParsingHandler {

    Gson gson = new Gson();
    private City city;
    private List<City> cities;

    private CurrentWeatherRequest currentWeatherRequest;

    public City getWeatherForSelectedCity(String source){

        currentWeatherRequest = gson.fromJson(source, CurrentWeatherRequest.class);

        int id = currentWeatherRequest.getId();
        String cityName = currentWeatherRequest.getName();
        double lat = currentWeatherRequest.getCoord().getLat();
        double lon = currentWeatherRequest.getCoord().getLon();
        long currentDateUTC = currentWeatherRequest.getDt();
        long currentDateCity = currentDateUTC + currentWeatherRequest.getTimezone();
        double currentTemp = currentWeatherRequest.getMain().getTemp();
        int currentPressure = currentWeatherRequest.getMain().getPressure();
        int currentHumidity = currentWeatherRequest.getMain().getHumidity();
        String weatherIcon = currentWeatherRequest.getWeather()[0].getIcon();

        return new City(id, cityName, currentDateCity, currentTemp, currentPressure, currentHumidity, null, weatherIcon, R.drawable.ic_sun_svg);
    }
}
