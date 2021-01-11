package com.geekbrains.myweatherapp.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherapp.model.City;
import com.geekbrains.myweatherapp.MyApp;
import com.geekbrains.myweatherapp.R;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;

/*адаптер для RecyclerView, наследуемся от RecyclerView.Adapter. Этот адаптер представляет шаблон проектирования viewHolder,
подразумевающий использование пользовательского класса, который расширяет RecyclerView.ViewHolder.
Эта паттерн сводит к минимуму количество обращений к дорогостоящему в плане ресурсов методу findViewById.*/
@AllArgsConstructor
public class MyRVAdapterHorizontal extends RecyclerView.Adapter<MyRVAdapterHorizontal.TempHourViewHolder> {
    String currentUnitTemp = MyApp.getINSTANCE().getUnitTemp();
    private List<Integer> tempForDate;
    private List<Integer> hour = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23);

    public MyRVAdapterHorizontal(City city) {
        tempForDate = city.getTempForDate();
    }

    //Внутри конструктора нашего кастомного ViewHolder, инициализируем View, входящие в RecyclerView.
    public static class TempHourViewHolder extends RecyclerView.ViewHolder {
        private TextView Temp;
        private TextView Hour;
        private LinearLayout llTempHour;

        public TempHourViewHolder(@NonNull View itemView) {
            super(itemView);
            Temp = itemView.findViewById(R.id.tv_temp_for_hour);
            Hour = itemView.findViewById(R.id.tv_hour);
            llTempHour = itemView.findViewById(R.id.linearLayout_temp_hour);
        }
    }
    /*метод вызывается, когда кастомный ViewHolder должен быть инициализирован.
    Мы указываем макет для каждого элемента RecyclerView.
    Затем LayoutInflater заполняет макет, и передает его в конструктор ViewHolder.*/
    @NonNull
    @Override
    public TempHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_rv_temp_hour, parent, false);
        v.getLayoutParams().width = Resources.getSystem().getDisplayMetrics().widthPixels/3;
        v.requestLayout();
        return new TempHourViewHolder(v);
    }
    /*onBindViewHolder определяет содержание каждого элемента из RecyclerView.
     Этот метод очень похож на метод getView  элемента адаптера ListView.
     Здесь нужно установить значения полей имя города, температура города, единица измерения температуры, картинку погоды в городе.
     */
    @Override
    public void onBindViewHolder(@NonNull TempHourViewHolder tempHourViewHolder, int position) {
        currentUnitTemp = MyApp.getINSTANCE().getUnitTemp();
        tempHourViewHolder.Hour.setText(String.format("%d:00",hour.get(position)));
        tempHourViewHolder.Temp.setText(String.format("%d %s", tempForDate.get(position), currentUnitTemp));

    }
    //метод вернет количество элементов, присутствующих в данных
    @Override
    public int getItemCount() {
        return hour.size();
    }
}
