package com.softdesign.sosapplication.utils.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class AcelerometrService extends Service {

    private SensorManager sensorManager;
    private Sensor sensorAccel;
    private Sensor sensorLinAccel;
    private Sensor sensorGravity;

    public AcelerometrService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // получаем доступ ко всем сепвисам-датчикам
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // получаем доступ к определенным датчикам
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        /*
        регистрируем прослушиватель на датчикики
         */
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }



    private void showInfo(double result) {
        if(result <2.0){
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    List<Float> listValues = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        listValues.add(event.values[i]);
                    }
                    double resultFail = Math.sqrt(Math.pow(listValues.get(0), 2)
                            + Math.pow(listValues.get(1), 2) + Math.pow(listValues.get(2), 2));

                    showInfo(resultFail);

                    break;

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
