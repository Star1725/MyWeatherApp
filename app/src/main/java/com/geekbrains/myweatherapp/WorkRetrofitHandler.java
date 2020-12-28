package com.geekbrains.myweatherapp;

import android.util.Log;

import com.geekbrains.myweatherapp.interfaces.OpenWeather;
import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.model.CurrentWeatherInListCitiesRequest;
import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;
import com.geekbrains.myweatherapp.model.HistoryWeatherRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WorkRetrofitHandler {


    public interface CallbackForWorkRetrofitHandler {
        void callingBackWeatherInCity(City city, String status);
        void callingBackWeatherInListCities(List<City> cityList, String status);
    }

    private static final ArrayList<CallbackForWorkRetrofitHandler> callbacks = new ArrayList<>();

    public static void registerObserverCallback(CallbackForWorkRetrofitHandler callback){
        callbacks.add(callback);
    }

    private void notifyCallBacksWeatherInCity(City city, String status){
        for (CallbackForWorkRetrofitHandler callback : callbacks) {
            callback.callingBackWeatherInCity(city, status);
        }
    }

    private void notifyCallBacksWeatherInListCities(List<City> cityList, String status){
        for (CallbackForWorkRetrofitHandler callback : callbacks) {
            callback.callingBackWeatherInListCities(cityList, status);
        }
    }

    private static OpenWeather openWeather;
    private City city;
    private List<City> cityList;
    private List<Double> tepmForHours;

    public WorkRetrofitHandler(){
        initRetrofit();
    }

    private void initRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                // Базовая часть адреса
                .baseUrl ( "https://api.openweathermap.org/" )
// Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory ( GsonConverterFactory. create ())
                .build ();

// Создаём объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }


    public void requestForWeatherInCityRetrofit(int cityId){
        openWeather.loadWeatherInCity(cityId, Constants.UNITS_FOR_RETROFIT, BuildConfig.WEATHER_API_KEY).enqueue(new Callback<CurrentWeatherRequest>() {
            @Override
            public void onResponse(Call<CurrentWeatherRequest> call, Response<CurrentWeatherRequest> response) {
                if (response != null){

                    int id = response.body().getId();
                    String cityName = response.body().getName();
                    double currentTemp = response.body().getMain().getTemp();
                    String weatherIcon = response.body().getWeather()[0].getIcon();
                    double lat = response.body().getCoord().getLat();
                    double lon = response.body().getCoord().getLon();
                    long currentDateUTC = response.body().getDt();
                    long currentDateCity = currentDateUTC + response.body().getTimezone();
                    int currentPressure = response.body().getMain().getPressure();
                    int currentHumidity = response.body().getMain().getHumidity();

                    city = new City(id, cityName, currentDateCity, currentTemp, currentPressure, currentHumidity, tepmForHours, weatherIcon, R.drawable.ic_sun_svg);

                    requestForWeatherInInCityHistoryRetrofit(lat, lon, currentDateUTC);

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherRequest> call, Throwable t) {
                if (Logger.VERBOSE) {
                    Log.v(Logger.TAG, this.getClass().getSimpleName() + " onFailure(): Throwable t = " + t.getMessage());
                }
                notifyCallBacksWeatherInCity(null, Constants.FAIL_CONNECTION);
            }
        });
    }

    private void requestForWeatherInInCityHistoryRetrofit(double lat, double lon, long currentDateUTC){

        tepmForHours = new ArrayList<>();

        openWeather.loadWeatherInCityHistory(lat, lon, currentDateUTC, Constants.UNITS_FOR_RETROFIT, BuildConfig.WEATHER_API_KEY).enqueue(new Callback<HistoryWeatherRequest>() {
            @Override
            public void onResponse(Call<HistoryWeatherRequest> call, Response<HistoryWeatherRequest> response) {
                if (response != null){
                    for (int i = 0; i < response.body().getHourly().length; i++) {
                        tepmForHours.add(response.body().getHourly()[i].getTemp());

                    }
                    requestForWeatherInCityForecastRetrofit(lat, lon);
                }
            }

            @Override
            public void onFailure(Call<HistoryWeatherRequest> call, Throwable t) {
                notifyCallBacksWeatherInCity(null, Constants.FAIL_CONNECTION);
            }
        });
    }


    private void requestForWeatherInCityForecastRetrofit(double lat, double lon){
        openWeather.loadWeatherInCityForecast(lat, lon, Constants.EXUCLUDE_FOR_RETROFIT, Constants.UNITS_FOR_RETROFIT, BuildConfig.WEATHER_API_KEY).enqueue(new Callback<HistoryWeatherRequest>() {
            @Override
            public void onResponse(Call<HistoryWeatherRequest> call, Response<HistoryWeatherRequest> response) {
                if (response != null){
                    List<Double> temps = new ArrayList<>();
                    for (int i = 0; i < 25 - tepmForHours.size(); i++) {
                        tepmForHours.add(response.body().getHourly()[i].getTemp());
                    }
                }
                city.setTempForDate(tepmForHours);
                notifyCallBacksWeatherInCity(city, Constants.CONNECTION);
            }

            @Override
            public void onFailure(Call<HistoryWeatherRequest> call, Throwable t) {
                if (Logger.VERBOSE) {
                    Log.v(Logger.TAG, this.getClass().getSimpleName() + " onFailure(): Throwable t = " + t.getMessage());
                }
                notifyCallBacksWeatherInCity(null, Constants.FAIL_CONNECTION);
            }
        });
    }

    public void requestForWeatherInListCitiesRetrofit(int[] ids){

        cityList = new ArrayList<>();
        StringBuilder stringIds = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            if (i == ids.length - 1){
                stringIds.append(ids[i]);
            } else {
                stringIds.append(ids[i]).append(",");
            }
        }

        openWeather.loadWeatherInCitiesList(Constants.UNITS_FOR_RETROFIT, BuildConfig.WEATHER_API_KEY, stringIds.toString()).enqueue(new Callback<CurrentWeatherInListCitiesRequest>() {
            @Override
            public void onResponse(Call<CurrentWeatherInListCitiesRequest> call, Response<CurrentWeatherInListCitiesRequest> response) {
                if (response != null){

                    int id;
                    String cityName;
                    double currentTemp;
                    String weatherIcon;

                    for (int i = 0; i < response.body().getList().length; i++) {

                        id = response.body().getList()[i].getId();
                        cityName = response.body().getList()[i].getName();
                        currentTemp = response.body().getList()[i].getMain().getTemp();
                        weatherIcon = response.body().getList()[i].getWeather()[0].getIcon();

                        cityList.add(new City(id, cityName, 0, currentTemp, 0, 0, null, weatherIcon, R.drawable.ic_sun_svg));
                    }
                    notifyCallBacksWeatherInListCities(cityList, Constants.CONNECTION);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherInListCitiesRequest> call, Throwable t) {
                notifyCallBacksWeatherInListCities(null, Constants.FAIL_CONNECTION);
            }
        });
    }
}
