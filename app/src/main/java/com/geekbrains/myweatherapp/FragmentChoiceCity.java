package com.geekbrains.myweatherapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class FragmentChoiceCity extends Fragment implements ResultRequestCallback{
//интерфейс для подписчиков на фрагмент ////////////////////////////////////////////////////////////
    public void setCallback(OnSelectedCityListener callback) {
        this.callback = callback;
    }
    OnSelectedCityListener callback;
    public interface OnSelectedCityListener{
        void onCitySelected(City city);
    }

    @Override
    public void callingBackCity(City city, String status) {

    }

    @Override
    public void callingBackArrayCities(List<City> cities, String status) {
        if (cities == null){
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackArrayCities(): " + status);
            }
            DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(status);
            dialogFragmentInfo.show(getActivity().getSupportFragmentManager(), "dialogError" );
        } else {
            cityList = cities;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showListCities(cities);
                }
            });
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<City> cityList;
    private MyRVAdapter myRVAdapter;
    private RecyclerView rvSites;
    private TextInputEditText choiceCityName;

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
        WorkNetHandler.registerObserverCallback(this);
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        rvSites = view.findViewById(R.id.recyclerView_cities);
        choiceCityName = view.findViewById(R.id.choice_city_name);

        showListCities(cityList);
    }

    private void showListCities(List<City> cities){
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvSites.setLayoutManager(llm);
//определяем данные для заполнения rvSites
        if (cities == null){
            cities = new ArrayList<City>(Collections.singleton(new City(0, "?", 0, 0, 0, 0, null, R.drawable.ic_sun_svg)));
        }

//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
        myRVAdapter = new MyRVAdapter(cities);
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
                MyRVAdapter localAdapter = new MyRVAdapter(cityList.stream().filter(city -> city.getName().toLowerCase().startsWith(s.toString().toLowerCase())).collect(Collectors.toList()));
                rvSites.setAdapter(localAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //обновление MyRv при изменении градусов на форенгейты и обратно
        myRVAdapter.notifyDataSetChanged();
    }
}
