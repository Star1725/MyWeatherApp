package com.geekbrains.myweatherapp;

class Constants {
    static final String CITY_EXTRA = "city_extra";
    static final String CITIES_EXTRA = "cities_extra";
    static final String INFO = "info";

    static final String NAME_SHARED_PREFERENCE = "MyAppPreference";
    static final String IS_LIGHT_THEME = "IsLightTheme";
    static final String IS_UNIT_C = "IsUnitC";

//наборы статиков для запросов
    //https://api.openweathermap.org/data/2.5/weather?id={city id}&appid={API key}
    static final String START_FOR_URL_WEATHER = "https://api.openweathermap.org/data/2.5/weather?";
    //URL = START_FOR_URL_WEATHER + ID_CITY + APPID + your key

    //https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={part}&units=metric&appid={API key}
    static final String START_FOR_URL_ONECALL = "https://api.openweathermap.org/data/2.5/onecall?";
    //URL = START_FOR_URL_ONECALL + COORD_LAT + lat + COORD_LON + lon + EXUCLUDE + UNITS + APPID + your key

    //https://api.openweathermap.org/data/2.5/onecall/timemachine?lat={lat}&lon={lon}&dt={time}&appid={API key}
    static final String START_FOR_URL_ONECALL_TIMEMACHINE = "https://api.openweathermap.org/data/2.5/onecall/timemachine?";
    //URL = START_FOR_URL_ONECALL_TIMEMACHINE + COORD_LAT + lat + COORD_LON + lon + CURRENT_DATE_UTC + currentDateUTC + UNITS + APPID + your key

    static final String COORD_LAT = "lat=";
    static final String COORD_LON = "&lon=";
    static final String EXUCLUDE = "&exclude=current,minutely,daily";
    static final String CURRENT_DATE_UTC = "&dt=";
    static final String ID_CITY = "id=";
    static final String APPID = "&appid=";
    static final String UNITS = "&units=metric";

    //http://openweathermap.org/img/wn/10d@2x.png
    static final String START_URL_FOR_DOWNLOAD_ICON = "http://openweathermap.org/img/wn/";
    static final String END_URL_FOR_DOWNLOAD_ICON = "@2x.png";
    //URL = START_URL_FOR_DOWNLOAD_ICON + icon + END_URL_FOR_DOWNLOAD_ICON (icon - String)



    static final String FAIL_CONNECTION = "fail connection";
    static final String CONNECTION = "connection";
}
