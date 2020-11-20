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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FragmentChoiceCity extends Fragment {
//интерфейс для подписчиков на фрагмент ////////////////////////////////////////////////////////////
    public void setCallback(OnSelectedCityListener callback) {
        this.callback = callback;
    }

    OnSelectedCityListener callback;

    public interface OnSelectedCityListener{
        void onCitySelected(City city);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<City> cityList;
    private MyRVAdapter myRVAdapter;
    private RecyclerView rvSites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onCreateView");
        }
        return inflater.inflate(R.layout.fragment_choice_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Logger.VERBOSE) {
            Log.d(Logger.TAG, this.getClass().getSimpleName() + " onViewCreated");
        }
//RecyclerView необходим менеджер компоновки для управления позиционированием своих элементов
        rvSites = view.findViewById(R.id.recyclerView_cities);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvSites.setLayoutManager(llm);
//определяем данные для заполнения rvSites
        cityList = new ArrayList<>();
        String[] nameCites = getResources().getStringArray(R.array.name_city);
        int[] tempCites = getResources().getIntArray(R.array.temps);
        int[] imageIDs = {R.drawable.ic_rain_svg, R.drawable.ic_moon_svg, R.drawable.ic_sun_svg};
        int[] tempHour = getResources().getIntArray(R.array.temps_for_hour);
        ArrayList<Integer> listTempHour = new ArrayList<>();
        for (int i = 0; i < tempHour.length ; i++) {
            listTempHour.add(tempHour[i]);
        }
        Random randomImage = new Random();
        for (int i = 0; i < nameCites.length; i++){
            cityList.add(new City(nameCites[i], tempCites[i], listTempHour, imageIDs[randomImage.nextInt(imageIDs.length)]));
        }

//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
        myRVAdapter = new MyRVAdapter(cityList);
        rvSites.setAdapter(myRVAdapter);
//автозаполнение
        AutoCompleteTextView myAutoCompleteTextView = view.findViewById(R.id.autoCompleteTextView_for_cache);
        myAutoCompleteTextView.setFreezesText(false);
        myAutoCompleteTextView.setAdapter( new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, nameCites));
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

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
        boolean orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (Logger.VERBOSE){
            Log.d(Logger.TAG, getClass().getSimpleName() + " onActivityCreated(): savedInstanceState = " + (savedInstanceState != null) + " orientationIsLand = " + orientationIsLand);
        }
        if (orientationIsLand){
            assert getFragmentManager() != null;
            Fragment weatherInCity = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(weatherInCity instanceof FragmentShowWeatherInCity)){
                weatherInCity = new FragmentShowWeatherInCity();
            }

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.fragment_container, weatherInCity);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //обновление MyRv при изменении градусов на форенгейты и обратно
        myRVAdapter.notifyDataSetChanged();
    }
}
