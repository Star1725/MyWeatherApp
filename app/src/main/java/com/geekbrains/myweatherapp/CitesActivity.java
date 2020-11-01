package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CitesActivity extends AppCompatActivity {
    private List<City> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitys);
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        RecyclerView rvSites = (RecyclerView)findViewById(R.id.recyclerView_sitys);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvSites.setLayoutManager(llm);
//определяем данные для заполнения rvSites
        cityList = new ArrayList<>();
        String[] nameCites = getResources().getStringArray(R.array.name_city);
        int[] tempCites = getResources().getIntArray(R.array.temps);
        int[] imageIDs = {R.drawable.ic_rain_svg, R.drawable.ic_moon_svg, R.drawable.ic_sun_svg};
        Random randomImage = new Random();
        for (int i = 0; i < nameCites.length; i++){
            cityList.add(new City(nameCites[i], tempCites[i], imageIDs[randomImage.nextInt(imageIDs.length)]));
        }
//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
        MyRVAdapter myRVAdapter = new MyRVAdapter(cityList);
        rvSites.setAdapter(myRVAdapter);

    }
}
