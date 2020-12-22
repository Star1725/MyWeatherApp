package com.geekbrains.myweatherapp;

import com.geekbrains.myweatherapp.interfaces.OpenWeather;
import com.geekbrains.myweatherapp.model.CurrentWeatherRequest;
import com.geekbrains.myweatherapp.model.HistoryWeatherRequest;
import com.geekbrains.myweatherapp.services.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public WorkRetrofitHandler(){
        initRetrofit();
    }

    private static void initRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                // Базовая часть адреса
                .baseUrl ( "http://api.openweathermap.org/" )
// Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory ( GsonConverterFactory. create ())
                .build ();
// Создаём объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }

    public void getCityListForIDs(int[] ids){
        cityList = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            cityList.add(requestForWeatherInCityRetrofit(ids[i], Constants.UNITS, BuildConfig.WEATHER_API_KEY, false));
        }
        notifyCallBacksWeatherInListCities(cityList, Constants.CONNECTION);
    }

    public void getCityForID(int id){
        city = requestForWeatherInCityRetrofit(id, Constants.UNITS, BuildConfig.WEATHER_API_KEY, true);
        notifyCallBacksWeatherInCity(city, Constants.CONNECTION);
    }

    private City requestForWeatherInCityRetrofit(int cityId, String units, String keyApi, boolean history){
        openWeather.loadWeatherInCity(cityId, units, keyApi).enqueue(new Callback<CurrentWeatherRequest>() {
            @Override
            public void onResponse(Call<CurrentWeatherRequest> call, Response<CurrentWeatherRequest> response) {
                if (response != null){
                    double lat = response.body().getCoord().getLat();
                    double lon = response.body().getCoord().getLon();
                    long currentDateUTC = response.body().getDt();

                    List<Double> tepmForHours = new ArrayList<>();
                    if (history){
                        tepmForHours =  requestForWeatherInListCitiesHistoryRetrofit(lat, lon, currentDateUTC, Constants.UNITS, BuildConfig.WEATHER_API_KEY);

                        tepmForHours = requestForWeatherInListCitiesRetrofit(lat, lon, Constants.EXUCLUDE, Constants.UNITS, BuildConfig.WEATHER_API_KEY, tepmForHours);
                    }

                    int id = response.body().getId();
                    String cityName = response.body().getName();
                    double currentTemp = response.body().getMain().getTemp();
                    String weatherIcon = response.body().getWeather()[0].getIcon();
                    long currentDateCity = currentDateUTC + response.body().getTimezone();
                    int currentPressure = response.body().getMain().getPressure();
                    int currentHumidity = response.body().getMain().getHumidity();

                    if (history){
                        city = new City(id, cityName, 0, currentTemp, 0, 0, null, weatherIcon, R.drawable.ic_sun_svg);
                    } else {
                        city = new City(id, cityName, currentDateCity, currentTemp, currentPressure, currentHumidity, tepmForHours, weatherIcon, R.drawable.ic_sun_svg);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherRequest> call, Throwable t) {
                notifyCallBacksWeatherInCity(null, Constants.FAIL_CONNECTION);
            }
        });
        return city;
    }

    private List<Double> requestForWeatherInListCitiesRetrofit(double lat, double lon, String exclude, String units, String keyApi, List<Double> tepmForHours){
        openWeather.loadWeatherInListCities(lat, lon, exclude, units, keyApi).enqueue(new Callback<HistoryWeatherRequest>() {
            @Override
            public void onResponse(Call<HistoryWeatherRequest> call, Response<HistoryWeatherRequest> response) {
                if (response != null){
                    if (response != null){
                        List<Double> temps = new ArrayList<>();
                        for (int i = 0; i < 25 - tepmForHours.size(); i++) {
                            tepmForHours.add(response.body().getHourly()[i].getTemp());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryWeatherRequest> call, Throwable t) {
                notifyCallBacksWeatherInListCities(null, Constants.FAIL_CONNECTION);
            }
        });
        return tepmForHours;
    }

    private List<Double> requestForWeatherInListCitiesHistoryRetrofit(double lat, double lon, long currentDateUTC, String units, String keyApi){

        List<Double> tepmForHours = new CopyOnWriteArrayList<>();

        openWeather.loadWeatherInListCitiesHistory(lat, lon, currentDateUTC, units, keyApi).enqueue(new Callback<HistoryWeatherRequest>() {
            @Override
            public void onResponse(Call<HistoryWeatherRequest> call, Response<HistoryWeatherRequest> response) {
                if (response != null){
                    List<Double> temps = new ArrayList<>();
                    for (int i = 0; i < response.body().getHourly().length; i++) {
                        tepmForHours.add(response.body().getHourly()[i].getTemp());
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryWeatherRequest> call, Throwable t) {
                notifyCallBacksWeatherInListCities(null, Constants.FAIL_CONNECTION);
            }
        });
        return tepmForHours;
    }
}
