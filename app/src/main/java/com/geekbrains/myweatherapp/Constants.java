package com.geekbrains.myweatherapp;

class Constants {
    static final String CITY_EXTRA = "city_extra";
    static final String INFO = "info";

    static final String NAME_SHARED_PREFERENCE = "MyAppPreference";
    static final String IS_LIGHT_THEME = "IsLightTheme";
    static final String IS_UNIT_C = "IsUnitC";

//наборы статико для запросов
    static final String END_FOR_ALL_URL = "&units=metric&appid=";
    static final String START_FOR_URL_WEATHER = "https://api.openweathermap.org/data/2.5/weather?";
    static final String START_FOR_URL_ONECALL = "https://api.openweathermap.org/data/2.5/onecall?";
    static final String MIDDLE_FOR_URL_ONECALL = "&exclude=current,minutely,daily";
    static final String START_FOR_URL_ONECALL_TIMEMACHINE = "https://api.openweathermap.org/data/2.5/onecall/timemachine?";
    static final String MIDDLE_FOR_URL_ONECALL_TIMEMACHINE = "&dt=";
    static final String COORD_LAT = "lat=";
    static final String COORD_LON = "&lon=";
    static final String ID_CITY = "id=";

    static final String FAIL_CONNECTION = "fail connection";
    static final String CONNECTION = "connection";
}
