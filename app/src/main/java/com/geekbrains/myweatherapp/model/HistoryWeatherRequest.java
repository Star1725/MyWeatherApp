package com.geekbrains.myweatherapp.model;

import com.geekbrains.myweatherapp.model.entity.Hourly;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class HistoryWeatherRequest {
    @SerializedName("hourly")
    @Expose
    private Hourly[] hourly;

    @SerializedName("timezone")
    @Expose
    private String timezone;

    @SerializedName("timezone_offset")
    @Expose
    private int timezone_offset;
}
