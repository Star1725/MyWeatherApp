package com.geekbrains.myweatherapp.model;

import com.geekbrains.myweatherapp.model.entity.Clouds;
import com.geekbrains.myweatherapp.model.entity.Coord;
import com.geekbrains.myweatherapp.model.entity.Main;
import com.geekbrains.myweatherapp.model.entity.Weather;
import com.geekbrains.myweatherapp.model.entity.Wind;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class CurrentWeatherRequest {
    @SerializedName("coord")
    @Expose
    private Coord coord ;

    @SerializedName("weather")
    @Expose
    private Weather[] weather ;

    @SerializedName("main")
    @Expose
    private Main main ;

    @SerializedName("wind")
    @Expose
    private Wind wind ;

    @SerializedName("clouds")
    @Expose
    private Clouds clouds ;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("dt")
    @Expose
    private long dt;

    @SerializedName("timezone")
    @Expose
    private int timezone;
}
