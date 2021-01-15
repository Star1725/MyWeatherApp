package com.geekbrains.myweatherapp.model.entity;

import com.geekbrains.myweatherapp.dao.HistoryDao;
import com.geekbrains.myweatherapp.model.City;

import java.util.List;

public class HistorySource {

    private final HistoryDao historyDao;

    public HistorySource(HistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    private List<City> cities;

    public List<City>getCities(){
        if (cities == null){
            loadCities();
        }
        return cities;
    }

    public void loadCities(){
        cities = historyDao.getAllCities();
    }

    public void addCity(City city){
        historyDao.insertCity(city);
        loadCities();
    }

    public void removeCities(){
        historyDao.deleteAllCities();
        loadCities();
    }

    public long getCountCities(){
        return historyDao.getCountCities();
    }
}
