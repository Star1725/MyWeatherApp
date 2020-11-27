package com.geekbrains.myweatherapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class City implements Parcelable {
    private String name;
    private long dt;
    private double currentTemp;
    private int pressure;
    private int humidity;
    private List<Double> TempForDate;
    private int imageWeatherID;

    protected City(Parcel in) {
        name = in.readString();
        dt = in.readLong();
        currentTemp = in.readDouble();
        pressure = in.readInt();
        humidity = in.readInt();
        TempForDate = (List<Double>) in.readSerializable();
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
        dest.writeString(name);
        dest.writeLong(dt);
        dest.writeDouble(currentTemp);
        dest.writeInt(pressure);
        dest.writeInt(humidity);
        dest.writeSerializable((Serializable) TempForDate);
        dest.writeInt(imageWeatherID);
    }
}
