package com.softdesign.sosapplication.mvp.map;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.mvp.contacts.ContactView;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.managers.DataManager;
import com.softdesign.sosapplication.utils.managers.PreferenceManager;
import com.yandex.mapkit.Animation;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;

import java.util.List;
import java.util.Random;


public class MapPresenter {

    private MapYandexView view;

    public MapPresenter() {

    }

    public void attachView(MapYandexView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void loadYandexMap(Point currentLocation) {
        try {
            view.mapView.getMap().move(
                    new CameraPosition(currentLocation, 17.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 2),
                    null
            );
            final CameraListener cameraListener = new CameraListener() {
                @Override
                public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition,
                                                    @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

                }
            };
            view.mapView.getMap().addCameraListener(cameraListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + view.getPackageName()));

        view.startActivityForResult(appSettingsIntent, 222);
    }

    public void addContact() {
        Intent intent = new Intent(view, ContactView.class);
        view.startActivity(intent);
    }

    public void sosMailingContacts(double latinude, double longitude) {

        PreferenceManager preferenceManager = DataManager.getInstance().getPreferenceManager();
        int sizeContacts = preferenceManager.loadSizeContact();
        for (int i = 0; i <= sizeContacts; i++) {
            String contact = preferenceManager.loadContact(i);

            if (!contact.equals("UNKNOWN")) {
                String[] info = contact.trim().split(":");
                String name = info[0];
                String phone = "smsto:" + info[1];
                String message = name + "," + "я попал в беду " + String.format("мои координаты : %f , %f", latinude, longitude);

                SmsManager.getDefault().sendTextMessage(phone, null, message,
                        null, null);
            }
        }

    }


}



