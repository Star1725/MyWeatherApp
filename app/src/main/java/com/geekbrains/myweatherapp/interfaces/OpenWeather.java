package com.geekbrains.myweatherapp.interfaces;

import com.geekbrains.myweatherapp.model.CurrentWeatherInListCitiesRequest;
import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;
import com.geekbrains.myweatherapp.model.HistoryWeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET( "data/2.5/weather")
    Call<CurrentWeatherRequest> loadWeatherInCity (@Query( "id" ) int cityId ,
                                             @Query( "units" ) String units ,
                                             @Query ( "appid" ) String keyApi );

    @GET( "data/2.5/onecall")
    Call<HistoryWeatherRequest> loadWeatherInCityForecast (@Query( "lat" ) double lat ,
                                                         @Query( "lon" ) double lon ,
                                                         @Query( "exclude" ) String exclude ,
                                                         @Query( "units" ) String units ,
                                                         @Query ( "appid" ) String keyApi );

    @GET( "data/2.5/onecall/timemachine")
    Call<HistoryWeatherRequest> loadWeatherInCityHistory (@Query( "lat" ) double lat ,
                                                         @Query( "lon" ) double lon ,
                                                         @Query( "dt" ) long dt ,
                                                         @Query( "units" ) String units ,
                                                         @Query ( "appid" ) String keyApi );

    @GET( "data/2.5/group")
    Call<CurrentWeatherInListCitiesRequest> loadWeatherInCitiesList (@Query( "id" ) int[] ids ,
                                                                     @Query( "units" ) String units ,
                                                                     @Query ( "appid" ) String keyApi );
}
