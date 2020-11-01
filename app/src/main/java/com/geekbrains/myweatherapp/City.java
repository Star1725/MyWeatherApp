package com.geekbrains.myweatherapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class City {
    private String name;
    private int temp;
    private int imageWeatherID;
}
