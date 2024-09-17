package com.geekbrains.myweatherapp.model;

import com.geekbrains.myweatherapp.model.entity.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class CurrentWeatherInListCitiesRequest {
    @SerializedName("list")
    @Expose
    private List[] list;
}
