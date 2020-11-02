package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.d(TAG, this.getClass().getSimpleName() + " onCreate");

        RadioGroup radioGroupTheme = findViewById(R.id.radioGroup_theme);
        radioGroupTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_dark_theme:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case R.id.radioButton_light_theme:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                }
            }
        });

        RadioGroup radioGroupTempUnits = findViewById(R.id.radioGroup_temp_units);
        radioGroupTempUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_celsius:

                        break;
                    case R.id.radioButton_fahrenheit:

                        break;
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super .onStart();
        Log.d(TAG, this.getClass().getSimpleName() + " onStart()");
    }
    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super .onRestoreInstanceState(saveInstanceState);
        Log.d(TAG, this.getClass().getSimpleName() + " Повторный запуск!! onRestoreInstanceState()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, this.getClass().getSimpleName() + " onResume()");
    }
    @Override
    protected void onPause() {
        super .onPause();
        Log.d(TAG, this.getClass().getSimpleName() + " onPause()");
    }
    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        Log.d(TAG, this.getClass().getSimpleName() + " onSaveInstanceState()");
    }
    @Override
    protected void onStop() {
        super .onStop();
        Log.d(TAG, this.getClass().getSimpleName() + " onStop()");
    }
    @Override
    protected void onRestart() {
        super .onRestart();
        Log.d(TAG, this.getClass().getSimpleName() + " onRestart()");
    }
    @Override
    protected void onDestroy() {
        super .onDestroy();
        Log.d(TAG, this.getClass().getSimpleName() + " onDestroy()");
    }
}
