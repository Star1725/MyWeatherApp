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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class WorkNetHandler {
    interface ResultRequestCallback {
        void callingBackCity(City city, String status);
        void callingBackListCity(List<City> cityList, String status);
    }

    private static ArrayList<ResultRequestCallback> callbacks = new ArrayList<>();
    public static void registerObserverCallback(ResultRequestCallback callback){
        callbacks.add(callback);
    }

    void notifyCallBacks(City city, List<City> cityList, String status){
        for (ResultRequestCallback callback : callbacks) {
            callback.callingBackCity(city, status);
            callback.callingBackListCity(cityList, status);

        }
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

                        long currentDateCity = (historyWeatherRequest1.getHourly()[0].getDt() + historyWeatherRequest1.getTimezone_offset());
                        long currentDateUTC = (historyWeatherRequest1.getHourly()[0].getDt());
                        String timeZone = historyWeatherRequest1.getTimezone();
                        double currentTemp = historyWeatherRequest1.getHourly()[0].getTemp();
                        int currentPressure = historyWeatherRequest1.getHourly()[0].getPressure();
                        int currentHumidity = historyWeatherRequest1.getHourly()[0].getHumidity();
                        if (Logger.VERBOSE){
                            Log.v(Logger.TAG, this.getClass().getSimpleName() + " getCityWithWeather():" + "\n" +
                                    "   cityName = " + cityName + "\n" +
                                    "   timeZone = " + timeZone + "\n" +
                                    "   Timezone_offset = " + historyWeatherRequest1.getTimezone_offset() + "\n" +
                                    "   currentDateCity = " + new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(currentDateCity*1000))
                            );
                        }


                        URL uri3 = new URL(Constants.START_FOR_URL_ONECALL_TIMEMACHINE + Constants.COORD_LAT + lat + Constants.COORD_LON + lon  + Constants.MIDDLE_FOR_URL_ONECALL_TIMEMACHINE + currentDateUTC + Constants.END_FOR_ALL_URL + BuildConfig.WEATHER_API_KEY);
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

                        City city = new City(0, cityName, currentDateCity, currentTemp, currentPressure, currentHumidity, tempForDate, R.drawable.ic_sun_svg);
                        notifyCallBacks(city, null,"OK");
                    } catch (IOException e) {
                        Log. e ( Logger.TAG , Constants.FAIL_CONNECTION, e);
                        printException(e);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                CountDownLatch countDownLatch = new CountDownLatch(idCities.size());
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
                                    countDownLatch.countDown();
                                } catch (IOException e) {
                                    Log.e(Logger.TAG, Constants.FAIL_CONNECTION, e);
                                    printException(e);
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
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Logger.VERBOSE){
                    Log.v(Logger.TAG, this.getClass().getSimpleName() + " getListCitiesWithTemp(): cities.get(0).getName()" + cities.get(0).getName());
                }
                notifyCallBacks(null, cities, "OK");
            }
        }).start();
    }

    private void printException(IOException e) {
        notifyCallBacks(null, null, Constants.FAIL_CONNECTION);
        e.printStackTrace();
    }

    private String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
