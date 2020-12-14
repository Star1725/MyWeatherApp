package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.geekbrains.myweatherapp.fragments.FragmentChoiceCity;
import com.geekbrains.myweatherapp.fragments.FragmentHistoryCity;
import com.geekbrains.myweatherapp.fragments.FragmentShowWeatherInCity;
import com.geekbrains.myweatherapp.fragments.MyDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class MainActivity extends AppCompatActivity implements FragmentChoiceCity.OnSelectedCityListener,
        WorkNetHandler.ResultRequestCallback,
        NavigationView.OnNavigationItemSelectedListener, FragmentHistoryCity.OnSelectedCityListener {
    private final static int REQUEST_CODE = 1;

    public static boolean orientationIsLand;
    private FragmentChoiceCity fragmentChoiceCity;
    static List<City> cityList;
    private FragmentHistoryCity fragmentHistoryCity;
    static Set<City> historyCitiesSet;
    private FragmentShowWeatherInCity fragmentShowWeatherInCity;
    private City currentCity;
    private static WorkNetHandler workNetHandler = new WorkNetHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orientationIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);

        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCreate: orientationIsLand = " + orientationIsLand + "\n" +
                    "fragmentShowWeatherInCity = " + (fragmentShowWeatherInCity != null) + "\n" +
                    "fragmentChoiceCity = " + (fragmentChoiceCity != null));
        }
        WorkNetHandler.registerObserverCallback(this);//подписываемся на ответы от сервера
        if (savedInstanceState == null){
            //делаем запросы на сервер, чтобы получить погоду в дефотном городе и список городов с текущими температурами
            workNetHandler.getCityWithWeather(MyApp.getINSTANCE().getIDdefaultCity());
            workNetHandler.getListCitiesWithTemp(Arrays.stream(getResources().getIntArray(R.array.id_city)).boxed().collect(Collectors.toList()));
            historyCitiesSet = new LinkedHashSet<>();
        } else {
            currentCity = savedInstanceState.getParcelable(Constants.CITY_EXTRA);
            historyCitiesSet = new LinkedHashSet<>(Objects.requireNonNull(savedInstanceState.getParcelableArrayList(Constants.SET_HISTORY)));
        }
        //в портретной ориентации начальный фрагмент - fragmentShowWeatherInCity
        if (!orientationIsLand) {
            if (fragmentShowWeatherInCity == null){
                fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();
        //в горизонтальной ориентации начальный экран - два фрагмента (FragmentShowWeatherInCity - динамический и FragmentChoiceCity - статический)
        } else {
            //находим динамический контейнер
            Fragment weatherInCity = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            //создаём fragmentShowWeatherInCity
            if (fragmentShowWeatherInCity == null) {
                fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
            }
            //проверяем динамический контейнер, если он пустой или содержит другой фрагмент
            if (!(weatherInCity instanceof FragmentShowWeatherInCity)){
                weatherInCity = fragmentShowWeatherInCity;
            }
            //через транзакцию заменяем фрагмент
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, weatherInCity);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            //для статического фрагмента загружаем список городов
            if (fragmentChoiceCity != null){
                fragmentChoiceCity.showListCities(cityList);
            }
        }
    }

    private Toolbar initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

//подписка на фрагменты ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof FragmentChoiceCity) {
            fragmentChoiceCity = (FragmentChoiceCity) fragment;
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentChoiceCity");
            }
            fragmentChoiceCity.setCallback(this);
        } else if (fragment instanceof FragmentShowWeatherInCity){
            fragmentShowWeatherInCity = (FragmentShowWeatherInCity) fragment;
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentShowWeatherInCity");
            }
        } else if (fragment instanceof FragmentHistoryCity) {
            fragmentHistoryCity = (FragmentHistoryCity) fragment;
            if (Logger.VERBOSE) {
                Log.v(Logger.TAG, this.getClass().getSimpleName() + " onAttachFragment(): подписка на fragmentChoiceCity");
            }
            fragmentHistoryCity.setCallback(this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CITY_EXTRA, currentCity);
        outState.putParcelableArrayList(Constants.SET_HISTORY, new ArrayList<>(historyCitiesSet));
    }
////// методы меню//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (orientationIsLand){
            getMenuInflater().inflate(R.menu.menu_main_land, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem search = menu.findItem(R.id.action_search);

            final SearchView searchView = (SearchView) search.getActionView();

            final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchAutoComplete.setBackgroundColor(Color.BLUE);
            searchAutoComplete.setTextColor(Color.GREEN);
            searchAutoComplete.setDropDownBackgroundResource(android.R.color.holo_blue_light);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    try {
                        City city = cityList.stream().filter(city1 -> city1.getName().equals(query)).findAny().get();
                        workNetHandler.getCityWithWeather(city.getId());
                        historyCitiesSet.add(city);
                    } catch (NoSuchElementException e){
                        showDialog(getString(R.string.faild_search), getString(R.string.error));
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<String> nameCity = cityList.stream().map(City::getName).collect(Collectors.toList());
                    nameCity.stream().filter(name -> name.toLowerCase().startsWith(newText.toLowerCase())).collect(Collectors.toList());

                    ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(searchView.getContext(), android.R.layout.simple_dropdown_item_1line, nameCity);
                    searchAutoComplete.setAdapter(newsAdapter);
                    return true;
                }
            });

            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                    String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                    searchAutoComplete.setText("" + queryString);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent1);
                return true;
            case R.id.info:
//                DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(getString(R.string.about));
//                dialogFragmentInfo.show(getSupportFragmentManager(), "dialogInfo" );
                showDialog(getString(R.string.about), getString(R.string.information));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

///////боковое меню//////////////////////////////////////////////////////////////////////////////////////
    private void initDrawer(Toolbar toolbar){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this , drawer, toolbar, R.string.navigation_drawer_open ,
                R.string.navigation_drawer_close );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_weather_in_city:
                Fragment weatherFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(weatherFragment instanceof FragmentShowWeatherInCity)){
                    if (fragmentShowWeatherInCity == null) {
                        fragmentShowWeatherInCity = new FragmentShowWeatherInCity();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();
                }
                break;
            case R.id.nav_cities:
                Fragment weatherFragment2 = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(weatherFragment2 instanceof FragmentChoiceCity)){
                    fragmentChoiceCity = FragmentChoiceCity.create(cityList);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentChoiceCity).addToBackStack("").commit();
                }
                break;
            case R.id.nav_history:
                Fragment weatherFragment1 = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(weatherFragment1 instanceof FragmentHistoryCity)){
                    fragmentHistoryCity = FragmentHistoryCity.create(historyCitiesSet);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentHistoryCity).addToBackStack("").commit();
                }
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//обработка выбора города в fragmentChoiceCity или fragmentHistoryCity//////////////////////////////
    @Override
    public void onCitySelected(City city) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " onCitySelected(): city = " + city.getName());
        }
        if (orientationIsLand) {
                workNetHandler.getCityWithWeather(city.getId());
        } else {
            workNetHandler.getCityWithWeather(city.getId());
            fragmentShowWeatherInCity.create(null);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentShowWeatherInCity).addToBackStack("").commit();
        }
        historyCitiesSet.add(city);
    }

    //методы, которые срабатывают, когда приходят данные с сервера
    @Override
    public void callingBackCity(City city, String status) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackCity(): " + status + " " + (city != null));
        }
        if (status.equals(Constants.FAIL_CONNECTION)){
            showDialog(status, getString(R.string.error));
        } else if(city != null){
            currentCity = city;
            if (fragmentShowWeatherInCity.isResumed()){
                fragmentShowWeatherInCity.showWeatherInCity(city);
            }
        }
    }

    @Override
    public void callingBackListCity(List<City> cityList, String status) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackListCity(): " + status + " " + (cityList != null));
        }
        if (status.equals(Constants.FAIL_CONNECTION)){
            showDialog(status, getString(R.string.error));
        } else if(cityList != null){
            MainActivity.cityList = cityList;
            if (fragmentChoiceCity != null && fragmentChoiceCity.isResumed()){
                fragmentChoiceCity.showListCities(cityList);
            }
        }
    }

    private void showDialog(String message, String type){
//        DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(message);
//        dialogFragmentInfo.show(getSupportFragmentManager(), "dialogError" );
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(type)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
