package com.geekbrains.myweatherapp;

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
    private List<City> cites;
    private static final String TAG = "myLog";
    //Внутри конструктора нашего кастомного ViewHolder, инициализируем View, входящие в RecyclerView.
    public static class CitesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llCity;
        private TextView citesName;
        private TextView citesTemp;
        private TextView unit;
        private ImageView citesWeather;
        public CitesViewHolder(@NonNull View itemView) {
            super(itemView);
            llCity = (LinearLayout) itemView.findViewById(R.id.linearLayout_city);
            citesName = (TextView) itemView.findViewById(R.id.tv_name_sity);
            citesTemp = (TextView) itemView.findViewById(R.id.tv_temp);
            unit = (TextView) itemView.findViewById(R.id.tv_unit);
            citesWeather = (ImageView) itemView.findViewById(R.id.iv_weather_in_cites);

            //llCity.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Intent intent = new Intent(llCity.getContext(), MainActivity.class);
//            intent.putExtra("citesName", citesName.getText().toString());
//            intent.putExtra("citesTemp", citesTemp.getText().toString());
//            intent.putExtra("citesWeather", citesWeather.getResources().getIdentifier()
//            llCity.getContext().startActivity(intent);
//        }
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
        citesViewHolder.unit.setText(R.string.unit_C);
        citesViewHolder.citesWeather.setImageResource(cites.get(position).getImageWeatherID());

        final int localPos = position;

        citesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("citesName", cites.get(localPos).getName());
            intent.putExtra("citesTemp", String.valueOf(cites.get(localPos).getTemp()));
            intent.putExtra("citesWeather", cites.get(localPos).getImageWeatherID());
                Log.d(TAG, this.getClass().getSimpleName() + "onClick: send intent: ");
                Log.d(TAG, this.getClass().getSimpleName() + "                     - citesName " + cites.get(localPos).getName());
                Log.d(TAG, this.getClass().getSimpleName() + "                     - citesTemp " + cites.get(localPos).getTemp());
                Log.d(TAG, this.getClass().getSimpleName() + "                     - citesWeather " + cites.get(localPos).getImageWeatherID());
            v.getContext().startActivity(intent);
            }
        });
    }
    //метод вернет количество элементов, присутствующих в данных
    @Override
    public int getItemCount() {
        return cites.size();
    }
}
