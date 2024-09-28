package com.geekbrains.myweatherapp.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.MyApp;
import com.geekbrains.myweatherapp.dao.HistoryDao;
import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.model.DateConverter;

@Database(entities = {City.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class HistoryDatabase extends RoomDatabase {

    public static HistoryDatabase createDB(){
        return Room.databaseBuilder(MyApp.getINSTANCE(), HistoryDatabase.class, Constants.DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public abstract HistoryDao getHistoryDao();
}
