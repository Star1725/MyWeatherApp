package com.geekbrains.myweatherapp.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class List {
    @SerializedName("weather")
    @Expose
    private Weather[] weather ;

    @SerializedName("main")
    @Expose
    private Main main ;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private Integer id;
}
