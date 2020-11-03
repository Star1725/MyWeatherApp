package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CitesActivity extends AppCompatActivity {
    private List<City> cityList;
    private static final String TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitys);
        Log.d(TAG, this.getClass().getSimpleName() + " onCreate");
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
//    @Override
//    protected void onStart() {
//        super .onStart();
//        Log.d(TAG, this.getClass().getSimpleName() + " onStart()");
//    }
//    @Override
//    protected void onRestoreInstanceState(Bundle saveInstanceState){
//        super .onRestoreInstanceState(saveInstanceState);
//        Log.d(TAG, this.getClass().getSimpleName() + " Повторный запуск!! onRestoreInstanceState()");
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, this.getClass().getSimpleName() + " onResume()");
//    }
//    @Override
//    protected void onPause() {
//        super .onPause();
//        Log.d(TAG, this.getClass().getSimpleName() + " onPause()");
//    }
//    @Override
//    protected void onSaveInstanceState(Bundle saveInstanceState){
//        super .onSaveInstanceState(saveInstanceState);
//        Log.d(TAG, this.getClass().getSimpleName() + " onSaveInstanceState()");
//    }
//    @Override
//    protected void onStop() {
//        super .onStop();
//        Log.d(TAG, this.getClass().getSimpleName() + "onStop()");
//    }
//    @Override
//    protected void onRestart() {
//        super .onRestart();
//        Log.d(TAG, this.getClass().getSimpleName() + "onRestart()");
//    }
//    @Override
//    protected void onDestroy() {
//        super .onDestroy();
//        Log.d(TAG, this.getClass().getSimpleName() + "onDestroy()");
//    }
}
