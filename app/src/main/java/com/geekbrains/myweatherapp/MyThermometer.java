package com.geekbrains.myweatherapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class MyThermometer extends View {

    private static final int LEVEL_TEMP_FULL = 40;
    // Цвет термометра
    private int thermometerColor = Color.GRAY;
    //Цвет фона термометра
    private int fieldThermColor = Color.WHITE;
    // Цвет уровня температуры
    private int positiveTempColor = Color.RED;
    private int negativeTempColor = Color.BLUE;
    // Цвет уровня температуры
    private int pointZeroColor = Color.BLACK;

    // Изображение термометра
    private RectF thermometerRectangle = new RectF();
    // Изображение фона термометра
    private RectF fieldThermRectangle = new RectF();
    // Изображение уровня температуры
    private Rect tempRectangle = new Rect();
    // Изображение "нуля"
    private Rect pointZeroRectangle = new Rect();

    //"Краска" термометра
    private Paint thermometerPaint ;
    //"Краска" фона термометра
    private Paint fieldThermPaint ;
    // "Краска" положительной температуры
    private Paint positiveTempPaint ;
    // "Краска" отрицательной температуры
    private Paint negativeTemPaint ;
    // "Краска" "нуля"
    private Paint pointZeroPaint ;

    // Ширина элемента
    private int width = 0 ;
    // Высота элемента
    private int height = 0;

    public int getLevelTemp() {
        return levelTemp;
    }

    public void setLevelTemp(int levelTemp) {
        this.levelTemp = levelTemp;
    }

    // Уровень temp
    private int levelTemp = 0;

    // Константы
    // Отступ элементов
    private final static int padding = 6 ;
    // Скругление углов термометра
    private final static int round = 150 ;

    public MyThermometer(Context context) {
        super(context);
        init();
    }

    public MyThermometer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public MyThermometer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    public MyThermometer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    /**
     * Инициализация атрибутов пользовательского элемента из xml
     */
    private void initAttr(Context context, AttributeSet attrs) {
        // При помощи этого метода получаем доступ к набору атрибутов.
        // На выходе - массив со значениями
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MyThermometer, 0, 0);
        // Чтобы получить какое-либо значение из этого массива,
        // надо вызвать соответствующий метод и передать в этот метод имя
        // ресурса, указанного в файле определения атрибутов (attrs.xml)
        thermometerColor = typedArray.getColor(R.styleable.MyThermometer_thermometer_color, Color.GRAY);
        fieldThermColor = typedArray.getColor(R.styleable.MyThermometer_field_thermometer_color, Color.WHITE);
        positiveTempColor = typedArray.getColor(R.styleable.MyThermometer_positive_temp_color, Color.RED);
        negativeTempColor = typedArray.getColor(R.styleable.MyThermometer_negative_temp_color, Color.BLUE);
        pointZeroColor = typedArray.getColor(R.styleable.MyThermometer_point_zero_color, Color.BLACK);
        levelTemp = typedArray.getInteger(R.styleable.MyThermometer_level_temp, 100);
        typedArray.recycle();
    }
    /**
     * Начальная инициализация полей класса
     */
    private void init() {
        thermometerPaint = new Paint();
        thermometerPaint.setAntiAlias(true);
        thermometerPaint.setColor(thermometerColor);
        thermometerPaint.setStyle(Paint.Style.FILL);

        fieldThermPaint = new Paint();
        fieldThermPaint.setAntiAlias(true);
        fieldThermPaint.setColor(fieldThermColor);
        fieldThermPaint.setStyle(Paint.Style.FILL);

        positiveTempPaint = new Paint();
        positiveTempPaint.setColor(positiveTempColor);
        positiveTempPaint.setStyle(Paint.Style.FILL);

        negativeTemPaint = new Paint();
        negativeTemPaint.setColor(negativeTempColor);
        negativeTemPaint.setStyle(Paint.Style.FILL);

        pointZeroPaint = new Paint();
        pointZeroPaint.setColor(pointZeroColor);
        pointZeroPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Получаем реальные ширину и высоту
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        // Отрисовка термометра
        thermometerRectangle.set(padding,
                padding,
                width - padding,
                height - padding);
        // Отрисовка фона термометра
        fieldThermRectangle.set(2 * padding,
                2 * padding,
                width - 2 * padding,
                height - 2 * padding);
        //отрисовка метки "нуля"
        pointZeroRectangle.set((int)(width/3),
                (int)(height/2 + padding/2),
                width - (int)(width/3),
                height - (int)(height/2 + padding/2));
        // Отрисовка температуры
        //уровень "0"
        int positZero = (int)Math.round(height/2);
        //уровень температуры в пикс.
        int currentTempPix = (int)(height/2/LEVEL_TEMP_FULL) * (levelTemp);
        tempRectangle.set((int)(0.45 * width),
                positZero - currentTempPix,
                width - (int)(0.45 * width),
                height - positZero);
    }

    /**
     * Вызывается, когда надо нарисовать элемент
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //отрисовываем термометр
        canvas.drawRoundRect(thermometerRectangle, round, round, thermometerPaint);
        //отрисовываем фон термометра
        canvas.drawRoundRect(fieldThermRectangle, round, round, fieldThermPaint);
        //отрисовываем метку "нуля"
        canvas.drawRect(pointZeroRectangle, pointZeroPaint);
        pointZeroPaint.setTextSize(40);
        canvas.drawText("0", 3 * width/4, height/2 + pointZeroPaint.getTextSize()/2, pointZeroPaint);

        //отрисовываем уровень температуры
        //уровень температуры в пикс.
        int currentTempPix = (int)(height/2/LEVEL_TEMP_FULL) * (levelTemp);
        //уровень "0"
        int positZero = (int)Math.round(height/2);
        tempRectangle.set((int)(0.45 * width),
                positZero - currentTempPix,
                width - (int)(0.45 * width),
                height - positZero);
        // Условие отрисовки (положительная  или отрицательная) и уровня температуры
        if (levelTemp > 0) {
            canvas.drawRect(tempRectangle, positiveTempPaint);
        } else {
            canvas.drawRect(tempRectangle, negativeTemPaint);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }
}
