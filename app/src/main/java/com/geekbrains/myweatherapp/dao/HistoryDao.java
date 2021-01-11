package com.geekbrains.myweatherapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.geekbrains.myweatherapp.model.City;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCity(City city);

    @Query("SELECT * FROM City")
    List<City> getAllCities();

    @Query("DELETE FROM City")
    void deleteAllCities();

    //Получаем количество записей в таблице
    @Query ( "SELECT COUNT() FROM city" )
    long getCountCities();
}
