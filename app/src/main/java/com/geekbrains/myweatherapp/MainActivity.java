package com.geekbrains.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private boolean isUnit_F;
    private boolean isImageSun;
    private ImageView imageViewWeather;
    private TextView tvTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("myLog", "onCreate");

        imageViewWeather = findViewById(R.id.imageView);
        final SeekBar seekBarTemp = findViewById(R.id.seekBarTemp);
        seekBarTemp.setOnSeekBarChangeListener(this);

        tvTemperature = (TextView)findViewById(R.id.tvTemperature);
        tvTemperature.setText("0");
    }

    public void onClick(View view) {
        TextView tvUnit = findViewById(R.id.tvUnits);

        switch (view.getId()){
            case R.id.buttonUnit:
                Log.d("myLog", "isUnit_F: " + isUnit_F);
                if (isUnit_F) {
                    tvUnit.setText(R.string.unit_C);
                    isUnit_F = false;
                } else {
                    tvUnit.setText(R.string.unit_F);
                    isUnit_F = true;
                }
                break;
            case R.id.buttonImage:
                Log.d("myLog", "isUnit_F: " + isImageSun);
                if (isImageSun) {
                    imageViewWeather.setImageResource(R.drawable.ic_weather_sunny_black_48dp);
                    isImageSun = false;
                } else {
                    imageViewWeather.setImageResource(R.drawable.ic_weather_pouring_black_48dp);
                    isImageSun = true;
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        tvTemperature.setText(String.valueOf(seekBar.getProgress()));
    }
}
