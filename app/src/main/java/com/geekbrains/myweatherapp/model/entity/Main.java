package com.geekbrains.myweatherapp.model.entity;

import lombok.Getter;

@Getter
public class Main {
    private double temp;
    private int pressure;
    private int humidity;
    private double temp_min;
    private double temp_max;
}
