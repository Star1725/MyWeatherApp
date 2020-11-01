package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        Intent intent = getIntent();

        tvNameCites.setText(intent.getStringExtra("citesName"));
        tvTemperatureCites.setText(intent.getStringExtra("citesTemp"));
        imageViewWeatherCites.setImageResource(intent.getIntExtra("citesWeather", R.drawable.ic_sun_svg));

    }
}
