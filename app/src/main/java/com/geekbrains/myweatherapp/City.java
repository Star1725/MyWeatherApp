package com.geekbrains.myweatherapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class City implements Parcelable {
    private String name;
    private int temp;
    private ArrayList<Integer> TempForDay;
    private int imageWeatherID;

    protected City(Parcel in) {
        name = in.readString();
        temp = in.readInt();
        TempForDay = (ArrayList<Integer>) in.readSerializable();
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
        dest.writeInt(temp);
        dest.writeSerializable(TempForDay);
        dest.writeInt(imageWeatherID);
    }
}
