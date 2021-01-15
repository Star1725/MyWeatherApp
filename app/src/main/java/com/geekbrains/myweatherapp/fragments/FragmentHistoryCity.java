package com.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherapp.MainActivity;
import com.geekbrains.myweatherapp.MyApp;
import com.geekbrains.myweatherapp.dao.HistoryDao;
import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.Constants;
import com.geekbrains.myweatherapp.Logger;
import com.geekbrains.myweatherapp.R;
import com.geekbrains.myweatherapp.adapters.MyRVAdapter;
import com.geekbrains.myweatherapp.model.entity.HistorySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FragmentHistoryCity extends Fragment {
//интерфейс для подписчиков на фрагмент ////////////////////////////////////////////////////////////
    public void setCallback(OnActionInFragmentShowHistory callback) {
        this.callback = callback;
    }
    public OnActionInFragmentShowHistory callback;
    public interface OnActionInFragmentShowHistory{
        void onCitySelected(City city);
        void clearHistory();
    }

    private MyRVAdapter myRVAdapter;
    private RecyclerView rvSites;
    private HistorySource historySource;

    public static FragmentHistoryCity create(Set<City> set){
        Bundle bundle =new Bundle();
        bundle.putParcelableArrayList(Constants.CITIES_HISTORY, new ArrayList<>(set));

        FragmentHistoryCity fragmentHistoryCity = new FragmentHistoryCity();
        fragmentHistoryCity.setArguments(bundle);

        return fragmentHistoryCity;
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

        rvSites = view.findViewById(R.id.recyclerView_cities);

        Button btnClearHistory = view.findViewById(R.id.button_clear_history);
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historySource.removeCities();
                myRVAdapter.notifyDataSetChanged();
                callback.clearHistory();
            }
        });

        Bundle args = getArguments();
        if (args != null){
            ArrayList<City> cities = getArguments().getParcelableArrayList(Constants.CITIES_HISTORY);
            showListCities(cities);
        }
    }

    public void showListCities(List<City> cities){
        if (cities != null){

            ((AppCompatActivity) rvSites.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayoutManager llm = new LinearLayoutManager((AppCompatActivity) rvSites.getContext());
                    rvSites.setLayoutManager(llm);
//создаём наш костумный адаптер, передаём ему данные и устанавливаем его для нашего rvSites
                    HistoryDao historyDao = MyApp
                            .getINSTANCE()
                            .getHistoryDao();
                    historySource = MainActivity.historySource;
                    myRVAdapter = new MyRVAdapter(cities, historySource);
                    rvSites.setAdapter(myRVAdapter);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //обновление MyRv при изменении градусов на форенгейты и обратно
        if (myRVAdapter != null){
            myRVAdapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){
//        super .onSaveInstanceState(saveInstanceState);
//        saveInstanceState.putParcelableArrayList(Constants.SET_HISTORY, new ArrayList<>(citiesSet));
//    }
}
