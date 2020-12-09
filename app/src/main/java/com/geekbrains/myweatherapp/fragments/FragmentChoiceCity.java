package com.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherapp.City;
import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.Logger;
import com.geekbrains.myweatherapp.adapters.MyRVAdapter;
import com.geekbrains.myweatherapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentChoiceCity extends Fragment {
//интерфейс для подписчиков на фрагмент ////////////////////////////////////////////////////////////
    public void setCallback(OnSelectedCityListener callback) {
        this.callback = callback;
    }
    public OnSelectedCityListener callback;
    public interface OnSelectedCityListener{
        void onCitySelected(City city);
    }

    private static List<City> cities;
    private MyRVAdapter myRVAdapter;
    private RecyclerView rvSites;
    private TextInputEditText choiceCityName;

    public static FragmentChoiceCity create(List<City> cityList){
        if (cityList == null){
            cities = new ArrayList<City>(Collections.singleton(new City(0, "?", 0, 0, 0, 0, null, "?", R.drawable.ic_sun_svg)));
        } else {
            cities = cityList;
        }
        return new FragmentChoiceCity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCreateView");
        }
        return inflater.inflate(R.layout.fragment_choice_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onViewCreated");
        }
        if (cities == null){
            cities = new ArrayList<>();
        }

        if (savedInstanceState != null){
            cities = savedInstanceState.getParcelableArrayList(Constants.CITIES_EXTRA);
        }
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        rvSites = view.findViewById(R.id.recyclerView_cities);
        choiceCityName = view.findViewById(R.id.choice_city_name);

        showListCities(cities);
    }

    public void showListCities(List<City> cities){
        if (cities == null){
            cities = new ArrayList<City>(Collections.singleton(new City(0, "?", 0, 0, 0, 0, null, "?", R.drawable.ic_sun_svg)));
        }

        List<City> finalCities = cities;
        ((AppCompatActivity) rvSites.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager llm = new LinearLayoutManager((AppCompatActivity) rvSites.getContext());
                rvSites.setLayoutManager(llm);
//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
                myRVAdapter = new MyRVAdapter(finalCities);
                rvSites.setAdapter(myRVAdapter);

                choiceCityName.setFreezesText(false);
                choiceCityName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (Logger.VERBOSE) {
                            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCreate() - myAutoCompleteTextView: s = " + s.toString());
                        }
                        MyRVAdapter localAdapter = new MyRVAdapter(finalCities.stream().filter(city -> city.getName().toLowerCase().startsWith(s.toString().toLowerCase())).collect(Collectors.toList()));
                        rvSites.setAdapter(localAdapter);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //обновление MyRv при изменении градусов на форенгейты и обратно
        myRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelableArrayList(Constants.CITIES_EXTRA, new ArrayList<>(cities));

    }
}
