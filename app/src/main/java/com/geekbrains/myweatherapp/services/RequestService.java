package com.geekbrains.myweatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.WorkNetHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.geekbrains.myweatherapp.Constants.IS_SAVED_INSTANCE_STATE;

public class RequestService extends Service {

    public interface CallbackForRequestService {
        void callingBackWeatherInCity(City city, String status);
        void callingBackWeatherInListCities(List<City> cityList, String status);
    }

    private static final ArrayList<CallbackForRequestService> callbacks = new ArrayList<>();

    public static void registerObserverCallback(CallbackForRequestService callback){
        callbacks.add(callback);
    }

    private void notifyCallBacksWeatherInCity(City city, String status){
        for (CallbackForRequestService callback : callbacks) {
            callback.callingBackWeatherInCity(city, status);
        }
    }

    private void notifyCallBacksWeatherInListCities(List<City> cityList, String status){
        for (CallbackForRequestService callback : callbacks) {
            callback.callingBackWeatherInListCities(cityList, status);
        }
    }

    // Для связывания Activity и сервиса
    private final IBinder binder = new ServiceBinder();

    private final WorkNetHandler workNetHandler = new WorkNetHandler();

    private ExecutorService es;

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getBooleanExtra(IS_SAVED_INSTANCE_STATE, true)){
            getWeatherInCity(intent.getIntExtra(Constants.ID_CITY_EXTRA, 0), true);
            getWeatherInListCities(intent.getIntArrayExtra(Constants.ID_CITIES_EXTRA), false);
        }
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null){
            es.shutdown();
        }
    }

    void getWeatherInCity(int idCity, boolean allWeather){
        es = Executors.newFixedThreadPool(1);
        Future<City> cityFuture = es.submit(new MyCall(idCity, allWeather));

        try {
            notifyCallBacksWeatherInCity(cityFuture.get(), Constants.CONNECTION);
        } catch (ExecutionException | InterruptedException e) {
            notifyCallBacksWeatherInCity(null, Constants.FAIL_CONNECTION);
            e.printStackTrace();
        }

        es.shutdown();
    }

    void getWeatherInListCities(int[] idCities, boolean allWeather){
        es = Executors.newFixedThreadPool(4);

       List<MyCall> myCalls = new ArrayList<>();
        for (int i = 0; i < idCities.length; i++){
            myCalls.add(new MyCall(idCities[i], allWeather));
        }

        try {
            List<Future<City>> futureList = es.invokeAll(myCalls);
            List<City> cityList = new ArrayList<>();
            for (Future<City> cityFuture : futureList) {
                cityList.add(cityFuture.get());
            }

            notifyCallBacksWeatherInListCities(cityList, Constants.CONNECTION);
        } catch (InterruptedException | ExecutionException e) {
            notifyCallBacksWeatherInListCities(null, Constants.FAIL_CONNECTION);
            e.printStackTrace();
        }

        es.shutdown();
    }
//колобэл, который выполняет запросы
    class MyCall implements Callable<City> {

        int idCities;
        boolean allWeather;

        public MyCall(int idCities, boolean allWeather) {
            this.allWeather = allWeather;
            this.idCities = idCities;
        }

        @Override
        public City call() throws Exception {
            return workNetHandler.getWeatherInCity(idCities, allWeather);
        }
    }

// Класс связи между клиентом и сервисом(это целый фреймворк для межпроцессорного взаимодействия)
    public class ServiceBinder extends Binder {
        RequestService getService() {
            return RequestService.this ;
        }

        public void getWeatherInCity(int idCity, boolean allWeather){
            getService().getWeatherInCity(idCity, allWeather);
        }

        public void getWeatherInListCities(int[] idCities, boolean allWeather){
            getService().getWeatherInListCities(idCities, allWeather);
        }
    }
}