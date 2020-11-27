package com.geekbrains.myweatherapp.model;

import com.geekbrains.myweatherapp.model.entity.Hourly;

import lombok.Getter;

@Getter
public class HistoryWeatherRequest {
    private Hourly[] hourly;
    private String timezone;
    private int timezone_offset;
}
