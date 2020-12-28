package com.geekbrains.myweatherapp;

import android.util.Log;

import com.geekbrains.myweatherapp.model.City;
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

    //запрос на погоды в городе по ID
    public City getWeatherInCity(int idCity, boolean allWeather) throws IOException {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getDataForIntermediateRequest(): idCity = " + idCity);
        }

        try {
            URL uri1 = new URL(Constants.START_FOR_URL_WEATHER +
                    Constants.ID_CITY + idCity +
                    Constants.UNITS +
                    Constants.APPID +
                    BuildConfig.WEATHER_API_KEY);

            HttpsURLConnection httpsURLConnection = null;

            httpsURLConnection = (HttpsURLConnection) uri1.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String result = getLines(in);
            in.close();
            Gson gson = new Gson();
            CurrentWeatherRequest currentWeatherRequest = gson.fromJson(result, CurrentWeatherRequest.class);
            int id = currentWeatherRequest.getId();
            String cityName = currentWeatherRequest.getName();
            double currentTemp = currentWeatherRequest.getMain().getTemp();
            String weatherIcon = currentWeatherRequest.getWeather()[0].getIcon();
            if (allWeather){
                double lat = currentWeatherRequest.getCoord().getLat();
                double lon = currentWeatherRequest.getCoord().getLon();
                long currentDateUTC = currentWeatherRequest.getDt();
                long currentDateCity = currentDateUTC + currentWeatherRequest.getTimezone();
                int currentPressure = currentWeatherRequest.getMain().getPressure();
                int currentHumidity = currentWeatherRequest.getMain().getHumidity();
                ArrayList<Double> tempForDate = (ArrayList<Double>) getHistoryWeatherRequest(lat, lon, currentDateUTC, true);
                ArrayList<Double> listForecast = (ArrayList<Double>) getHistoryWeatherRequest(lat, lon, 0, false);
                for (int i = 1; i < 25 - tempForDate.size(); i++) {
                    tempForDate.add(listForecast.get(i));
                }

                disconnectURL(httpsURLConnection);

                return new City(id, cityName, currentDateCity, currentTemp, currentPressure, currentHumidity, tempForDate, weatherIcon, R.drawable.ic_sun_svg);
            } else {
                disconnectURL(httpsURLConnection);

                return new City(id, cityName, 0, currentTemp, 0, 0, null, weatherIcon, R.drawable.ic_sun_svg);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void disconnectURL(HttpsURLConnection httpsURLConnection) {
        if (httpsURLConnection != null) {
            httpsURLConnection.disconnect();
        }
    }

    //запрос на получение почасовой температуры за текущий день. flagHistorical = false - ONECALL - после текущего часа, flagHistorical = true - ONECALL_TIMEMACHINE - до текущего часа
    private List<Double> getHistoryWeatherRequest(double lat, double lon, long currentDateUTC, boolean flagHistory) throws IOException {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getHistoryWeatherRequest()");
        }
        String result;
        URL uri;
        HistoryWeatherRequest historyWeatherRequest;
        try {
            if (flagHistory){
                uri = new URL(Constants.START_FOR_URL_ONECALL +
                        Constants.COORD_LAT + lat +
                        Constants.COORD_LON + lon +
                        Constants.EXUCLUDE +
                        Constants.UNITS +
                        Constants.APPID + BuildConfig.WEATHER_API_KEY);
            } else {
                uri = new URL(Constants.START_FOR_URL_ONECALL_TIMEMACHINE +
                        Constants.COORD_LAT + lat +
                        Constants.COORD_LON + lon  +
                        Constants.CURRENT_DATE_UTC + currentDateUTC +
                        Constants.UNITS +
                        Constants.APPID + BuildConfig.WEATHER_API_KEY);
            }

            HttpsURLConnection httpsURLConnection = null;
            httpsURLConnection = (HttpsURLConnection)uri.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            result = getLines(in);
            in.close();
            Gson gson = new Gson();
            historyWeatherRequest = gson.fromJson(result, HistoryWeatherRequest.class);
            List<Double> listTemp = new ArrayList<>();
            for (int i = 0; i < historyWeatherRequest.getHourly().length; i++) {
                listTemp.add(historyWeatherRequest.getHourly()[i].getTemp());
            }
            httpsURLConnection.disconnect();
            return listTemp;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
