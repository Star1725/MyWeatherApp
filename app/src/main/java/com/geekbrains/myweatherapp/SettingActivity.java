package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "myLog";

    private RadioButton radioButtonLight;
    private RadioButton radioButtonDark;
    private RadioButton radioButtonC;
    private RadioButton radioButtonF;
    private Spinner spinnerCites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (Logger.VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onCreate");
        }
        radioButtonLight = findViewById(R.id.radioButton_light_theme);
        radioButtonDark = findViewById(R.id.radioButton_dark_theme);
        radioButtonC = findViewById(R.id.radioButton_celsius);
        radioButtonF = findViewById(R.id.radioButton_fahrenheit);
        spinnerCites = findViewById(R.id.spinnerCites);

        //создаём adapter для Spinner и настраиваем список
        ArrayAdapter<?> adapterForSpinner = ArrayAdapter.createFromResource(this, R.array.name_city, R.layout.layout_for_spiner_cites);
        adapterForSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCites.setAdapter(adapterForSpinner);

        radioButtonLight.setChecked(MyApp.getINSTANCE().isLightTheme());
        radioButtonDark.setChecked(!MyApp.getINSTANCE().isLightTheme());
        if (MyApp.getINSTANCE().getStorage().isUnitC()){radioButtonC.setChecked(true);}
        else {radioButtonF.setChecked(true);}

        RadioGroup radioGroupTheme = findViewById(R.id.radioGroup_theme);
        radioGroupTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_dark_theme:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        MyApp.getINSTANCE().setLightTheme(false);
                        break;
                    case R.id.radioButton_light_theme:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        MyApp.getINSTANCE().setLightTheme(true);
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
                        MyApp.getINSTANCE().getStorage().setUnitTemp(String.valueOf(getText(R.string.unit_C)));
                        MyApp.getINSTANCE().getStorage().setUnitC(true);
                        break;
                    case R.id.radioButton_fahrenheit:
                        MyApp.getINSTANCE().getStorage().setUnitTemp(String.valueOf(getText(R.string.unit_F)));
                        MyApp.getINSTANCE().getStorage().setUnitC(false);
                        break;
                }
            }
        });
    }
}
