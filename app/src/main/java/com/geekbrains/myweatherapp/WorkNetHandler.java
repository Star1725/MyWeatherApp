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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class WorkNetHandler {

    private static ResultRequestCallback resultRequestCallback;
    public static void registerObserverCallback(ResultRequestCallback result){
        resultRequestCallback = result;
    }

    public void getCityWithWeather(int idCity){
        if (Logger.VERBOSE){
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getCityWithWeather(): idCity = " + idCity);
        }
        final CurrentWeatherRequest[] currentWeatherRequest = new CurrentWeatherRequest[1];
        try {
            final URL uri1 = new URL(Constants.START_FOR_URL_WEATHER + Constants.ID_CITY + idCity + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpsURLConnection httpsURLConnection = null;
                    HistoryWeatherRequest historyWeatherRequest1;
                    HistoryWeatherRequest historyWeatherRequest2;
                    CurrentWeatherRequest currentWeatherRequest;
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

                        URL uri2 = new URL(Constants.START_FOR_URL_ONECALL + Constants.COORD_LAT + lat + Constants.COORD_LON + lon + Constants.MIDDLE_FOR_URL_ONECALL + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);
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

                        URL uri3 = new URL(Constants.START_FOR_URL_ONECALL_TIMEMACHINE + Constants.COORD_LAT + lat + Constants.COORD_LON + lon  + Constants.MIDDLE_FOR_URL_ONECALL_TIMEMACHINE + currentDate/1000 + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);
                        httpsURLConnection = (HttpsURLConnection)uri3.openConnection();
                        httpsURLConnection.setRequestMethod("GET");
                        httpsURLConnection.setReadTimeout(5000);
                        in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        result = getLines(in);
                        historyWeatherRequest2 = gson.fromJson(result, HistoryWeatherRequest.class);

                        //из двух запросов формируем список температуры за текущий день
                        List<Double> tempForDate = new ArrayList<>();
                        for (int i = 0; i < historyWeatherRequest2.getHourly().length; i++) {
                            tempForDate.add(historyWeatherRequest2.getHourly()[i].getTemp());
                        }
                        for (int i = 1; i < 25 - historyWeatherRequest2.getHourly().length; i++) {
                            tempForDate.add(historyWeatherRequest1.getHourly()[i].getTemp());
                        }

                        City city = new City(0, cityName, currentDate, currentTemp, currentPressure, currentHumidity, tempForDate, R.drawable.ic_sun_svg);
                        resultRequestCallback.callingBackCity(city, "OK");
                    } catch (IOException e) {
                        Log. e ( Logger.TAG , Constants.FAIL_CONNECTION, e);
                        resultRequestCallback.callingBackCity(null, Constants.FAIL_CONNECTION);
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
    }

    public void getListCitiesWithTemp(List<Integer> idCities){
        if (Logger.VERBOSE){
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getListCitiesWithTemp()");
        }
        List<City> cities = new ArrayList<>();
        for (int i = 0; i < idCities.size(); i++) {
            try {
                final URL uri1 = new URL(Constants.START_FOR_URL_WEATHER + Constants.ID_CITY + idCities.get(i) + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpsURLConnection httpsURLConnection = null;
                        CurrentWeatherRequest currentWeatherRequest;
                        try {
                            httpsURLConnection = (HttpsURLConnection) uri1.openConnection();
                            httpsURLConnection.setRequestMethod("GET");
                            httpsURLConnection.setReadTimeout(10000);
                            BufferedReader in = null;
                            in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                            String result = getLines(in);
                            in.close();
                            Gson gson = new Gson();
                            currentWeatherRequest = gson.fromJson(result, CurrentWeatherRequest.class);
                            String cityName = currentWeatherRequest.getName();
                            int id = currentWeatherRequest.getId();
                            double currentTemp = currentWeatherRequest.getMain().getTemp();

                            cities.add(new City(id, cityName, 0, currentTemp, 0, 0, null, R.drawable.ic_sun_svg));

                            resultRequestCallback.callingBackArrayCities(cities, "OK");

                        } catch (IOException e) {
                            Log.e(Logger.TAG, Constants.FAIL_CONNECTION, e);
                            resultRequestCallback.callingBackArrayCities(null, Constants.FAIL_CONNECTION);
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
        }
    }

    private String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
