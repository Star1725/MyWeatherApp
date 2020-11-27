package com.geekbrains.myweatherapp.model;

import com.geekbrains.myweatherapp.model.entity.Clouds;
import com.geekbrains.myweatherapp.model.entity.Coord;
import com.geekbrains.myweatherapp.model.entity.Main;
import com.geekbrains.myweatherapp.model.entity.Weather;
import com.geekbrains.myweatherapp.model.entity.Wind;

import lombok.Getter;

@Getter
public class CurrentWeatherRequest {
    private Coord coord ;
    private Weather[] weather ;
    private Main main ;
    private Wind wind ;
    private Clouds clouds ;
    private String name;
    private int id;
}
