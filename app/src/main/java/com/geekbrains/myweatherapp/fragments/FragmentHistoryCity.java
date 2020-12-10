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
import com.geekbrains.myweatherapp.R;
import com.geekbrains.myweatherapp.adapters.MyRVAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FragmentHistoryCity extends Fragment {
//интерфейс для подписчиков на фрагмент ////////////////////////////////////////////////////////////
    public void setCallback(OnSelectedCityListener callback) {
        this.callback = callback;
    }
    public OnSelectedCityListener callback;
    public interface OnSelectedCityListener{
        void onCitySelected(City city);
    }

    private static List<City> cities;
    private static Set<City> citiesSet;
    private MyRVAdapter myRVAdapter;
    private RecyclerView rvSites;

    public static FragmentHistoryCity create(Set<City> set){

        citiesSet = set;

        return new FragmentHistoryCity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCreateView");
        }
        return inflater.inflate(R.layout.fragment_history_cities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        rvSites = view.findViewById(R.id.recyclerView_cities);

        showListCities(citiesSet);
    }

    public void showListCities(Set<City> cities){
        if (cities != null){
            List<City> finalCities = new ArrayList<>(cities);
            ((AppCompatActivity) rvSites.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayoutManager llm = new LinearLayoutManager((AppCompatActivity) rvSites.getContext());
                    rvSites.setLayoutManager(llm);
//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
                    myRVAdapter = new MyRVAdapter(finalCities);
                    rvSites.setAdapter(myRVAdapter);
                }
            });
        }
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
        saveInstanceState.putParcelable(Constants.SET_HISTORY, (Parcelable) citiesSet);
    }
}
