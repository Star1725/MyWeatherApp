package com.geekbrains.myweatherapp.model.entity;

import java.util.List;

import lombok.Getter;

@Getter
public class Hourly {
    private long dt;
    private double temp;
    private int pressure;
    private int humidity;
    private int clouds;
    private double wind_speed;
    private int wind_deg;
    private Weather[] weather;
}
