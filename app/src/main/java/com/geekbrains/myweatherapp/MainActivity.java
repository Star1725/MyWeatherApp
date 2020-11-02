package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "myLog";

    private boolean isUnit_F;
    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private Button buttonChoiceCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, this.getClass().getSimpleName() + " onCreate");

        buttonChoiceCity = findViewById(R.id.button_choice_city);
        imageViewWeatherCites = findViewById(R.id.imageView);
        tvNameCites = findViewById(R.id.tvNameCites);
        tvTemperatureCites = findViewById(R.id.tvTemperature);

        tvTemperatureCites.setText("0");
        tvNameCites.setText(R.string.moscow);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_choice_city:
                        Intent intent = new Intent(MainActivity.this, CitesActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        buttonChoiceCity.setOnClickListener(onClickListener);
    }
// методы меню/////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
/////////////////////////////////////////////////
    public void onClick(View view) {
        TextView tvUnit = findViewById(R.id.tvUnits);

        switch (view.getId()){
            case R.id.buttonUnit:
                if (isUnit_F) {
                    tvUnit.setText(R.string.unit_C);
                    isUnit_F = false;
                } else {
                    tvUnit.setText(R.string.unit_F);
                    isUnit_F = true;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, this.getClass().getSimpleName() + " onResume()");
        Intent intent = getIntent();

        tvNameCites.setText(intent.getStringExtra(" citesName"));
        tvTemperatureCites.setText(intent.getStringExtra(" citesTemp"));
        imageViewWeatherCites.setImageResource(intent.getIntExtra(" citesWeather", R.drawable.ic_sun_svg));

    }

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
