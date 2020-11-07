package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CitesActivity extends AppCompatActivity {
    private static boolean FLAG_TURN_ON_LOG = true;
    private static final String TAG = "myLog";

    private List<City> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cites);
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onCreate");
        }
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        final RecyclerView rvSites = (RecyclerView)findViewById(R.id.recyclerView_sitys);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvSites.setLayoutManager(llm);
//определяем данные для заполнения rvSites
        cityList = new ArrayList<>();
        final String[] nameCites = getResources().getStringArray(R.array.name_city);
        int[] tempCites = getResources().getIntArray(R.array.temps);
        int[] imageIDs = {R.drawable.ic_rain_svg, R.drawable.ic_moon_svg, R.drawable.ic_sun_svg};
        Random randomImage = new Random();
        for (int i = 0; i < nameCites.length; i++){
            cityList.add(new City(nameCites[i], tempCites[i], imageIDs[randomImage.nextInt(imageIDs.length)]));
        }
//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
        MyRVAdapter myRVAdapter = new MyRVAdapter(cityList);
        rvSites.setAdapter(myRVAdapter);
//автозаполнение
        AutoCompleteTextView myAutoCompleteTextView = findViewById(R.id.autoCompleteTextView_for_cearch);
        myAutoCompleteTextView.setAdapter( new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nameCites));
        //динамически изменяем rvCites через myAutoCompleteTextView
        myAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (FLAG_TURN_ON_LOG) {
                    Log.d(TAG, this.getClass().getSimpleName() + " onCreate() - myAutoCompleteTextView: s = " + s.toString());
                }
                MyRVAdapter localAdapter = new MyRVAdapter(cityList.stream().filter(city -> city.getName().toLowerCase().contains(s.toString().toLowerCase())).collect(Collectors.toList()));
                rvSites.setAdapter(localAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super .onStart();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onStart()");
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super .onRestoreInstanceState(saveInstanceState);
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " Повторный запуск!! onRestoreInstanceState()");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onResume()");
        }
    }
    @Override
    protected void onPause() {
        super .onPause();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onPause()");
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onSaveInstanceState()");
        }
    }
    @Override
    protected void onStop() {
        super .onStop();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onStop()");
        }
    }
    @Override
    protected void onRestart() {
        super .onRestart();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + " onRestart()");
        }
    }
    @Override
    protected void onDestroy() {
        super .onDestroy();
        if (FLAG_TURN_ON_LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + "onDestroy()");
        }
    }
}
