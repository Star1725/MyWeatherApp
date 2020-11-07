package com.geekbrains.myweatherapp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Storage {
    private String unitTemp = "\u2103";
    private boolean lightTheme = true;
    private boolean unitC = true;

    private City defaultCity = new City("Moscow", 15, R.drawable.ic_sun_svg);
}
