package com.softdesign.sosapplication.utils.services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.mvp.map.MapPresenter;
import com.softdesign.sosapplication.mvp.map.MapYandexView;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.managers.DataManager;

import java.util.ArrayList;
import java.util.List;

public class AcelerometrService extends Service {

    private SensorManager sensorManager;
    private Sensor sensorAccel;

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

        /*
        регистрируем прослушиватель на датчикики
         */
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }


    private void showInfo(double result) {
        if (result < 2.0) {
            sendNotification();
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

    public void sendNotification() {

        Intent trueResultIntent = new Intent(this, MapYandexView.class);
        trueResultIntent.putExtra(ConstantManager.CONDITION_USER_FROM_DIALOG, true);
        PendingIntent trueResultPendingIntent = PendingIntent.getActivity(this, 0, trueResultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent falseResultIntent = new Intent(this, MapYandexView.class);
        falseResultIntent.putExtra(ConstantManager.CONDITION_USER_FROM_DIALOG, false);
        PendingIntent falseResultPendingIntent = PendingIntent.getActivity(this, 0, falseResultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel";
        int NOTIFICATIONS_ID = 1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setContentTitle("Уведовлемение")
                .setContentText("С вами все в порядке?")
                .setVibrate(new long[]{0, 2000, 1100, 1000})
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(R.drawable.ic_baseline_check_24, "Все хорошо", trueResultPendingIntent)
                .addAction(R.drawable.ic_baseline_close_24, "Нет", falseResultPendingIntent);

        // заапускаем таймер
        startTimer();
        // запускаем увкдовлемение
        notificationManager.notify(NOTIFICATIONS_ID, builder.build());


    }

    public static void startTimer() {
        long timeSeconds = DataManager.getInstance().getPreferenceManager().loadMinutesSendSignalSOS();
        final CountDownTimer timer = new CountDownTimer(timeSeconds * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                try {
                    MapPresenter presenter = new MapPresenter();
                    List<Double> location = MapYandexView.getCurrentLocation();
                    presenter.sosMailingContacts(location.get(0), location.get(1));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}

