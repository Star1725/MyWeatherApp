package com.geekbrains.myweatherapp;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;

/*адаптер для RecyclerView, наследуемся от RecyclerView.Adapter. Этот адаптер представляет шаблон проектирования viewHolder,
подразумевающий использование пользовательского класса, который расширяет RecyclerView.ViewHolder.
Эта паттерн сводит к минимуму количество обращений к дорогостоящему в плане ресурсов методу findViewById.*/
@AllArgsConstructor
public class MyRVAdapterHorizontal extends RecyclerView.Adapter<MyRVAdapterHorizontal.TempHourViewHolder> {
    String currentUnitTemp = MyApp.getINSTANCE().getStorage().getUnitTemp();
    private List<Integer> listTempHour;
    private List<Integer> hour = Arrays.asList(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22);

    public MyRVAdapterHorizontal( List tempForDay) {
        listTempHour = tempForDay;
    }

    //Внутри конструктора нашего кастомного ViewHolder, инициализируем View, входящие в RecyclerView.
    public static class TempHourViewHolder extends RecyclerView.ViewHolder {
        private TextView Temp;
        private TextView Hour;

        public TempHourViewHolder(@NonNull View itemView) {
            super(itemView);
            Temp = itemView.findViewById(R.id.tv_temp_for_hour);
            Hour = itemView.findViewById(R.id.tv_hour);
        }
    }
    /*метод вызывается, когда кастомный ViewHolder должен быть инициализирован.
    Мы указываем макет для каждого элемента RecyclerView.
    Затем LayoutInflater заполняет макет, и передает его в конструктор ViewHolder.*/
    @NonNull
    @Override
    public TempHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_rv_temp_hour, parent, false);
        return new TempHourViewHolder(v);
    }
    /*onBindViewHolder определяет содержание каждого элемента из RecyclerView.
     Этот метод очень похож на метод getView  элемента адаптера ListView.
     Здесь нужно установить значения полей имя города, температура города, единица измерения температуры, картинку погоды в городе.
     */
    @Override
    public void onBindViewHolder(@NonNull TempHourViewHolder tempHourViewHolder, int position) {
        currentUnitTemp = MyApp.getINSTANCE().getStorage().getUnitTemp();
        tempHourViewHolder.Temp.setText(String.format("%s %s",String.valueOf(listTempHour.get(position)), currentUnitTemp));
        tempHourViewHolder.Hour.setText(String.format("%d:00",hour.get(position)));
    }
    //метод вернет количество элементов, присутствующих в данных
    @Override
    public int getItemCount() {
        return listTempHour.size();
    }
}
