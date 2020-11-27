package com.geekbrains.myweatherapp;

import android.util.Log;

import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;
import com.geekbrains.myweatherapp.model.HistoryWeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class WorkNetHandler {

    private static ResultRequestCallback resultRequestCallback;
    public static void registerObserverCallback(ResultRequestCallback result){
        resultRequestCallback = result;
    }

    private CurrentWeatherRequest currentWeatherRequest;
    private HistoryWeatherRequest historyWeatherRequest1;
    private HistoryWeatherRequest historyWeatherRequest2;

    private City city;


    public City getCityWithWeather(int idCity){

        if (Logger.VERBOSE){
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getWeather(): idCity = " + idCity);
        }
        try {
            final URL uri1 = new URL(Constants.START_FOR_URL_WEATHER + Constants.ID_CITY + idCity + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpsURLConnection httpsURLConnection = null;
                    try {
                        httpsURLConnection = (HttpsURLConnection)uri1.openConnection();
                        httpsURLConnection.setRequestMethod("GET");
                        httpsURLConnection.setReadTimeout(10000);
                        BufferedReader in = null;
                        in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        String result = getLines(in);
                        in.close();
                        Gson gson = new Gson();
                        currentWeatherRequest = gson.fromJson(result, CurrentWeatherRequest.class);
                        String cityName = currentWeatherRequest.getName();
                        double lat = currentWeatherRequest.getCoord().getLat();
                        double lon = currentWeatherRequest.getCoord().getLon();
                        if (Logger.VERBOSE) {
                            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getWeather(): current weather in city = " + currentWeatherRequest.getName() + "\n" +
                                    "getWeather(): current coord for city: lat = " + lat + "\n" +
                                    "getWeather(): current coord for city: lon = " + lon
                            );
                        }

                        httpsURLConnection.disconnect();

                        final URL uri2 = new URL(Constants.START_FOR_URL_ONECALL + Constants.COORD_LAT + lat + Constants.COORD_LON + lon + Constants.MIDDLE_FOR_URL_ONECALL + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);
                        httpsURLConnection = (HttpsURLConnection)uri2.openConnection();
                        httpsURLConnection.setRequestMethod("GET");
                        httpsURLConnection.setReadTimeout(10000);
                        in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        result = getLines(in);
                        historyWeatherRequest1 = gson.fromJson(result, HistoryWeatherRequest.class);

                        long currentDate = (historyWeatherRequest1.getHourly()[0].getDt() - historyWeatherRequest1.getTimezone_offset())*1000;
                        double currentTemp = historyWeatherRequest1.getHourly()[0].getTemp();
                        int currentPressure = historyWeatherRequest1.getHourly()[0].getPressure();
                        int currentHumidity = historyWeatherRequest1.getHourly()[0].getHumidity();
                        if (Logger.VERBOSE) {
                            for (int i = 0; i < historyWeatherRequest1.getHourly().length; i++) {
                                Log.v(Logger.TAG, this.getClass().getSimpleName() +
                                        " getWeather(): current time in city = " +
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date ((historyWeatherRequest1.getHourly()[i].getDt() + historyWeatherRequest1.getTimezone_offset())*1000)) + " temp = " + historyWeatherRequest1.getHourly()[i].getTemp());
                            }
                            Log.v(Logger.TAG, this.getClass().getSimpleName() +
                                    " getWeather(): historyWeatherRequest.getHourly().length = " + historyWeatherRequest1.getHourly().length);
                        }

                        URL uri3 = new URL(Constants.START_FOR_URL_ONECALL_TIMEMACHINE + Constants.COORD_LAT + lat + Constants.COORD_LON + lon  + Constants.MIDDLE_FOR_URL_ONECALL_TIMEMACHINE + currentDate/1000 + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);

                        httpsURLConnection.disconnect();

                        httpsURLConnection = (HttpsURLConnection)uri3.openConnection();
                        httpsURLConnection.setRequestMethod("GET");
                        httpsURLConnection.setReadTimeout(10000);
                        in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        result = getLines(in);
                        historyWeatherRequest2 = gson.fromJson(result, HistoryWeatherRequest.class);
                        if (Logger.VERBOSE) {
                            for (int i = 0; i < historyWeatherRequest2.getHourly().length; i++) {
                                Log.v(Logger.TAG, this.getClass().getSimpleName() +
                                        " getWeather(): время до in city = " +
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date ((historyWeatherRequest2.getHourly()[i].getDt() + historyWeatherRequest2.getTimezone_offset())*1000)) + " temp = " + historyWeatherRequest2.getHourly()[i].getTemp());
                            }
                            Log.v(Logger.TAG, this.getClass().getSimpleName() +
                                    " getWeather(): historyWeatherRequest.getHourly().length = " + historyWeatherRequest2.getHourly().length);
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
                        List<Double> tempForDate = new ArrayList<>();
                        for (int i = 0; i < historyWeatherRequest2.getHourly().length - 1; i++) {
                            tempForDate.add(historyWeatherRequest2.getHourly()[i].getTemp());
                        }
                        for (int i = 0; i < 24 - historyWeatherRequest2.getHourly().length - 1; i++) {
                            tempForDate.add(historyWeatherRequest1.getHourly()[i].getTemp());
                        }

                        city = new City(cityName, currentDate, currentTemp, currentPressure, currentHumidity, tempForDate, R.drawable.ic_sun_svg);
                        resultRequestCallback.callingBack(city, "OK");
                    } catch (IOException e) {
                        Log. e ( Logger.TAG , "Fail connection" , e);
                        e.printStackTrace();
                    } finally {
                        if (httpsURLConnection != null) {
                            httpsURLConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return city;
    }

    private String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
