package com.geekbrains.myweatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "myLog";
    public final static String INSTANCE_KEY_UNIT_TEMP = "INSTANCE_KEY_UNIT_TEMP";
    private final static int REQUEST_CODE = 1;
    private City currentCity;

    private ImageView imageViewWeatherCites;
    private TextView tvNameCites;
    private TextView tvTemperatureCites;
    private TextView tvUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onCreate");
        }

        Button buttonChoiceCity = findViewById(R.id.button_choice_city);
        Button buttonInfoCity = findViewById(R.id.button_info_city);
        imageViewWeatherCites = findViewById(R.id.imageView);
        tvNameCites = findViewById(R.id.tvNameCites);
        tvTemperatureCites = findViewById(R.id.tvTemperature);
        tvUnit = findViewById(R.id.tvUnits);

        TextView tvCurrentDate = findViewById(R.id.tv_current_date);
        Calendar calendar = Calendar.getInstance();
        tvCurrentDate.setText(getDate(calendar));

        currentCity = MyApp.getINSTANCE().getStorage().getDefaultCity();
        settingViews(currentCity);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_choice_city:
                        Intent intent = new Intent(MainActivity.this, CitesActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;
                    case R.id.button_info_city:
                        String url = "https://yandex.ru/pogoda/" + currentCity.getName();
                        if (Constants.FLAG_TURN_ON_VERBOSE) {
                            Log.d(TAG, this.getClass().getSimpleName() + " onClick(): url = " + url);
                        }
                        Uri uri = Uri.parse(url);
                        Intent intentGET = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intentGET);
                        break;
                }
            }
        };

        buttonChoiceCity.setOnClickListener(onClickListener);
        buttonInfoCity.setOnClickListener(onClickListener);
    }

    private String getDate(Calendar calendar){
        return "today " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onActivityResult()");
            Log.d(TAG, this.getClass().getSimpleName() + " requestCode = " + requestCode);
        }
        if (resultCode == RESULT_OK){
            assert data != null;
            currentCity = data.getParcelableExtra(Constants.CITY_EXTRA);
            settingViews(currentCity);
        }
    }

    private void settingViews(City city) {
        tvNameCites.setText(city.getName());
        tvTemperatureCites.setText(String.valueOf(city.getTemp()));
        imageViewWeatherCites.setImageResource(city.getImageWeatherID());
        tvUnit.setText(MyApp.getINSTANCE().getStorage().getUnitTemp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onResume()");
        }
    }
    @Override
    protected void onStart() {
        super .onStart();
        if (currentCity != null){
            settingViews(currentCity);
        }
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onStart()");
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle saveInstanceState){
        super .onRestoreInstanceState(saveInstanceState);
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " Повторный запуск!! onRestoreInstanceState()");
        }
        //tvUnit.setText(saveInstanceState.getCharSequence(INSTANCE_KEY_UNIT_TEMP));
        currentCity = saveInstanceState.getParcelable(Constants.CITY_EXTRA);
    }
    @Override
    protected void onPause() {
        super .onPause();
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onPause()");
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super .onSaveInstanceState(saveInstanceState);
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onSaveInstanceState()");
        }
        //saveInstanceState.putString(INSTANCE_KEY_UNIT_TEMP, tvUnit.getText().toString());
        saveInstanceState.putParcelable(Constants.CITY_EXTRA, currentCity);
    }
    @Override
    protected void onStop() {
        super .onStop();
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onStop()");
        }
    }
    @Override
    protected void onRestart() {
        super .onRestart();
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onRestart()");
        }
    }
    @Override
    protected void onDestroy() {
        super .onDestroy();
        if (Constants.FLAG_TURN_ON_VERBOSE) {
            Log.d(TAG, this.getClass().getSimpleName() + " onDestroy()");
        }
    }
}
