package com.geekbrains.myweatherapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FragmentChoiceCity extends Fragment {

    private List<City> cityList;
    private boolean orientationIsLand;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choice_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onViewCreated");
        }
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        final RecyclerView rvSites = view.findViewById(R.id.recyclerView_cities);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
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
//автозаполнение
        AutoCompleteTextView myAutoCompleteTextView = view.findViewById(R.id.autoCompleteTextView_for_cearch);
        myAutoCompleteTextView.setFreezesText(false);
        myAutoCompleteTextView.setAdapter( new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, nameCites));
        //динамически изменяем rvCites через myAutoCompleteTextView
        myAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Logger.VERBOSE) {
                    Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreate() - myAutoCompleteTextView: s = " + s.toString());
                }
                MyRVAdapter localAdapter = new MyRVAdapter(cityList.stream().filter(city -> city.getName().toLowerCase().startsWith(s.toString().toLowerCase())).collect(Collectors.toList()));
                rvSites.setAdapter(localAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (orientationIsLand && (getContext() instanceof MainActivity)){
            if (Logger.VERBOSE){
                Log.d(Logger.TAG, getClass().getSimpleName() + " onActivityCreated(): savedInstanceState = " + (savedInstanceState != null) + " orientationIsLand = " + orientationIsLand);
            }
            FragmentShowWeatherInCity weatherInCity = (FragmentShowWeatherInCity) getFragmentManager().findFragmentById(R.id.fragment_weather_in_city);
            Log.d(Logger.TAG, "weatherInCity = " + (weatherInCity != null));
            if (weatherInCity == null){
                weatherInCity = new FragmentShowWeatherInCity();
            }

            weatherInCity = FragmentShowWeatherInCity.create();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_weather_in_city, weatherInCity);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {
            if (Logger.VERBOSE){
                Log.d(Logger.TAG, getClass().getSimpleName() + " onActivityCreated(): savedInstanceState = " + (savedInstanceState != null) + " orientationIsLand = " + orientationIsLand);
            }

        }
    }
}
