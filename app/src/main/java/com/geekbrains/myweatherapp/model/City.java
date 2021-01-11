package com.geekbrains.myweatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.versionedparcelable.NonParcelField;

import com.geekbrains.myweatherapp.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity(indices = { @Index(value = { Constants.COLUMN_CITY_NAME})})
@Getter
@Setter
public class City implements Parcelable {

    @NonParcelField
    @PrimaryKey(autoGenerate = true)// @PrimaryKey - указывает на ключевую запись,
    @ColumnInfo(name = Constants.COLUMN_ID)// autoGenerate = true - автоматическая генерация ключа
    public long idDB;

    @ColumnInfo(name = Constants.COLUMN_CITY_NAME)
    private String name;

    @ColumnInfo(name = Constants.COLUMN_LONG_DT)
    private long dt;

    @ColumnInfo(name = Constants.COLUMN_TEMPERATURE)
    private int currentTemp;

    @ColumnInfo(name = Constants.COLUMN_WEATHER_ICON)
    private String icon;

    public City(long idDB, String name, long dt, int currentTemp, String icon) {
    }

//поля, игнорируемые БД
    @Ignore
    private int id;
    @Ignore
    private int pressure;
    @Ignore
    private int humidity;
    @Ignore
    private List<Integer> tempForDate;
    @Ignore
    private int imageWeatherID;

    @Ignore
    protected City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        dt = in.readLong();
        currentTemp = in.readInt();
        pressure = in.readInt();
        humidity = in.readInt();
        tempForDate = (List<Integer>) in.readSerializable();
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

    @Ignore
    public City(int id, String cityName, long currentDateCity, int currentTemp, int currentPressure, int currentHumidity, List<Integer> tepmForHours, String weatherIcon, int ic_sun_svg) {
        this.id = id;
        name = cityName;
        dt = currentDateCity;
        this.currentTemp = currentTemp;
        pressure = currentPressure;
        humidity = currentHumidity;
        tempForDate = tepmForHours;
        icon = weatherIcon;
        imageWeatherID = ic_sun_svg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(dt);
        dest.writeInt(currentTemp);
        dest.writeInt(pressure);
        dest.writeInt(humidity);
        dest.writeSerializable((Serializable) tempForDate);
        dest.writeString(icon);
        dest.writeInt(imageWeatherID);
    }
}
