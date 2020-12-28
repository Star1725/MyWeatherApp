package com.geekbrains.myweatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.versionedparcelable.NonParcelField;

import com.geekbrains.myweatherapp.Constants;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity(indices = { @Index(value = {"name"})})
public class City implements Parcelable {
    private int id;

    // @PrimaryKey - указывает на ключевую запись,
// autoGenerate = true - автоматическая генерация ключа
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.COLUMN_ID)
    @NonParcelField
    public long idDB;

    @ColumnInfo(name = Constants.COLUMN_CITY_NAME)
    private String name;

    @ColumnInfo(name = Constants.COLUMN_LONG_DT)
    private long dt;


    private double currentTemp;
    private int pressure;
    private int humidity;
    private List<Double> TempForDate;
    private String icon;

    @ColumnInfo(name = Constants.COLUMN_WEATHER_ID)
    private int imageWeatherID;

    protected City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        dt = in.readLong();
        currentTemp = in.readDouble();
        pressure = in.readInt();
        humidity = in.readInt();
        TempForDate = (List<Double>) in.readSerializable();
        icon = name = in.readString();
        imageWeatherID = in.readInt();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(dt);
        dest.writeDouble(currentTemp);
        dest.writeInt(pressure);
        dest.writeInt(humidity);
        dest.writeSerializable((Serializable) TempForDate);
        dest.writeString(icon);
        dest.writeInt(imageWeatherID);
    }
}
