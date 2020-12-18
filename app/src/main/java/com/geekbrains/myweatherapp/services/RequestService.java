package com.geekbrains.myweatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.MyApp;
import com.geekbrains.myweatherapp.R;
import com.geekbrains.myweatherapp.WorkNetHandler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class RequestService extends Service {

    // Для связывания Activity и сервиса
    private final IBinder binder = new ServiceBinder();

    private final WorkNetHandler workNetHandler = new WorkNetHandler();


    @Override
    public IBinder onBind(Intent intent) {
        getWeatherForCity(intent.getIntExtra(Constants.ID_CITY_EXTRA, 0));
        getWeatherForListCities(intent.getIntArrayExtra(Constants.ID_CITIES_EXTRA));
        return binder;
    }


    private void getWeatherForCity(int idCity) {
        //делаем запросы на сервер, чтобы получить погоду в дефотном городе
        workNetHandler.getCityWithWeather(idCity);
    }

    private void getWeatherForListCities(int[] idCities) {
        //делаем запросы на сервер, чтобы получить список городов с текущими температурами
        workNetHandler.getListCitiesWithTemp(idCities);
    }

    // Класс связи между клиентом и сервисом(это целый фреймворк для межпроцессорного взаимодействия)
    public class ServiceBinder extends Binder {
        RequestService getService() {
            return RequestService.this ;
        }

        public void getWeatherForCity(int idCity){
            getService().getWeatherForCity(idCity);
        }

        public void getWeatherForListCities(int[] idCities) {
            getService().getWeatherForListCities(idCities);
        }
    }
}