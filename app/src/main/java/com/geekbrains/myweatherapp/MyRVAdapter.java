package com.geekbrains.myweatherapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;

/*адаптер для RecyclerView, наследуемся от RecyclerView.Adapter. Этот адаптер представляет шаблон проектирования viewholder,
подразумевающий использование пользовательского класса, который расширяет RecyclerView.ViewHolder.
Эта паттерн сводит к минимуму количество обращений к дорогостоящему в плане ресурсов методу findViewById.*/

@AllArgsConstructor
public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.CitesViewHolder> {

    private static final String TAG = "myLog";
    private static boolean FLAG_TURN_ON_LOG = true;

    private List<City> cites;
    //Внутри конструктора нашего кастомного ViewHolder, инициализируем View, входящие в RecyclerView.
    public static class CitesViewHolder extends RecyclerView.ViewHolder {
        private TextView citesName;
        private TextView citesTemp;
        private TextView unit;
        private ImageView citesWeather;
        public CitesViewHolder(@NonNull View itemView) {
            super(itemView);
            citesName = (TextView) itemView.findViewById(R.id.tv_name_sity);
            citesTemp = (TextView) itemView.findViewById(R.id.tv_temp);
            unit = (TextView) itemView.findViewById(R.id.tv_unit);
            citesWeather = (ImageView) itemView.findViewById(R.id.iv_weather_in_cites);
        }
    }
    /*метод вызывается, когда кастомный ViewHolder должен быть инициализирован.
    Мы указываем макет для каждого элемента RecyclerView.
    Затем LayoutInflater заполняет макет, и передает его в конструктор ViewHolder.*/
    @NonNull
    @Override
    public CitesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_sity_temp, parent, false);
        CitesViewHolder citesViewHolder = new CitesViewHolder(v);
        return citesViewHolder;
    }
    /*onBindViewHolder определяет содержание каждого элемента из RecyclerView.
     Этот метод очень похож на метод getView  элемента адаптера ListView.
     Здесь нужно установить значения полей имя города, температура города, единица измерения температуры, картинку погоды в городе.
     */
    @Override
    public void onBindViewHolder(@NonNull CitesViewHolder citesViewHolder, int position) {
        citesViewHolder.citesName.setText(cites.get(position).getName());
        citesViewHolder.citesTemp.setText(String.valueOf(cites.get(position).getTemp()));
        citesViewHolder.unit.setText(MyApp.getINSTANCE().getStorage().getUnitTemp());
        citesViewHolder.citesWeather.setImageResource(cites.get(position).getImageWeatherID());

        final int localPos = position;

        citesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FLAG_TURN_ON_LOG) {
                    Log.d(TAG, this.getClass().getSimpleName() + "onClick: send " + cites.get(localPos).getName() + " for intent");
                }
                Intent intentResult = new Intent();
                intentResult.putExtra(Constants.CITY_EXTRA, cites.get(localPos));
                ((Activity)v.getContext()).setResult(Activity.RESULT_OK, intentResult);
                ((Activity)v.getContext()).finish();
            }
        });
    }
    //метод вернет количество элементов, присутствующих в данных
    @Override
    public int getItemCount() {
        return cites.size();
    }
}
