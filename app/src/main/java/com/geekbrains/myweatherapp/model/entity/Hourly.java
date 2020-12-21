package com.geekbrains.myweatherapp.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

@Getter
public class Hourly {
    @SerializedName("dt")
    @Expose
    private long dt;

    @SerializedName("temp")
    @Expose
    private double temp;

    @SerializedName("pressure")
    @Expose
    private int pressure;

    @SerializedName("humidity")
    @Expose
    private int humidity;

    @SerializedName("clouds")
    @Expose
    private int clouds;

    @SerializedName("wind_speed")
    @Expose
    private double wind_speed;

    @SerializedName("wind_deg")
    @Expose
    private int wind_deg;

    @SerializedName("weather")
    @Expose
    private Weather[] weather;
}
