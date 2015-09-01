package com.example.katotakashi.getheatbeat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView acceleroTextView;
    private TextView hbTextView;
    private TextView mClockView;
    private float x,y,z;
    private final float GAIN = 0.9f;
    private  float hbBpm;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mStepCountSensor;
    private Sensor mStepDetectSensor;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        acceleroTextView = (TextView) findViewById(R.id.acceleroText);
        hbTextView = (TextView) findViewById(R.id.hbText);

        mClockView = (TextView) findViewById(R.id.clock);
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*SENSOR_DELAY_FASTEST 最高速でのセンサ読み出し
        SENSOR_DELAY_GAME	高速ゲーム向け
        SENSOR_DELAY_NORMAL 通常モード
        SENSOR_DELAY_UI 低速。ユーザインターフェイス向け*/
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        センサー値取得時の処理
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = (x * GAIN + event.values[0] * (1 - GAIN));
            y = (y * GAIN + event.values[1] * (1 - GAIN));
            z = (z * GAIN + event.values[2] * (1 - GAIN));

            if (acceleroTextView != null)
                acceleroTextView.setText(String.format("加速度\nX : %f\nY : %f\nZ : %f\n", x, y, z));
                Log.i("加速度センサー：", String.format("X : %f\nY : %f\nZ : %f\n", x, y, z));
        }

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            Log.i("心拍数：", String.valueOf(event.values[0]));
            hbBpm = event.values[0];

            if (hbTextView != null)
                hbTextView.setText(String.format("心拍数\nbpm : %f", hbBpm));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            acceleroTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            acceleroTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
