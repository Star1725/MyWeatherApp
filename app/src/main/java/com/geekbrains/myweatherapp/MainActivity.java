package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.geekbrains.myweatherapp.fragments.FragmentChoiceCity;
import com.geekbrains.myweatherapp.fragments.FragmentShowWeatherInCity;
import com.geekbrains.myweatherapp.fragments.MyDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class MainActivity extends AppCompatActivity implements FragmentChoiceCity.OnSelectedCityListener,
        WorkNetHandler.ResultRequestCallback,
        NavigationView.OnNavigationItemSelectedListener{
    private final static int REQUEST_CODE = 1;

    public static boolean orientationIsLand;
    private FragmentChoiceCity fragmentChoiceCity;
    static List<City> cityList;
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
        } else {
            currentCity = savedInstanceState.getParcelable(Constants.CITY_EXTRA);
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
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CITY_EXTRA, currentCity);
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
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Snackbar.make(searchView, query, Snackbar.LENGTH_LONG).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Snackbar.make(searchView, newText, Snackbar.LENGTH_SHORT).show();
                    return true;
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
            case R.id.choices_city:
                Fragment weatherFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(weatherFragment instanceof FragmentChoiceCity)){
                    fragmentChoiceCity = FragmentChoiceCity.create(cityList);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentChoiceCity).addToBackStack("").commit();
                }
                return true;
            case R.id.info:
                DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(getString(R.string.about));
                dialogFragmentInfo.show(getSupportFragmentManager(), "dialogInfo" );
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
            case R.id.nav_gallery:

                return true;
            case R.id.nav_home:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_share:

                break;
            case R.id.nav_send:

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
/////////////////////////////////////////////////////////////////////////////////////////////////////
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
            Snackbar.make(findViewById(R.id.fragment_container), R.string.dialog_snackbar + city.getName(), Snackbar.LENGTH_LONG).setDuration(3000).show();
        }
    }

    @Override
    public void callingBackCity(City city, String status) {
        if (Logger.VERBOSE) {
            Log.v(Logger.TAG, this.getClass().getSimpleName() + " callingBackCity(): " + status + " " + (city != null));
        }
        if (status.equals(Constants.FAIL_CONNECTION)){
            showDialogError(status);
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
            showDialogError(status);
        } else if(cityList != null){
            MainActivity.cityList = cityList;
            if (fragmentChoiceCity != null && fragmentChoiceCity.isResumed()){
                fragmentChoiceCity.showListCities(cityList);
            }
        }
    }

    private void showDialogError(String status){
        DialogFragment dialogFragmentInfo = MyDialogFragment.newInstance(status);
        dialogFragmentInfo.show(getSupportFragmentManager(), "dialogError" );
    }
}
