package com.softdesign.sosapplication.mvp.map;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.softdesign.sosapplication.mvp.contacts.ContactView;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;


public class MapPresenter {

    private MapYandexView view;
    private MapModel model;
    private Context context;

    public MapPresenter(MapModel model) {
        this.model = model;
    }


    public void attachView(MapYandexView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void loadYandexMap() {
        view.mapView.getMap().move(
                new CameraPosition(view.TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );
        final CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition,
                                                @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

            }
        };
        view.mapView.getMap().addCameraListener(cameraListener);
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

    public void sosMailingContacts() {

    }


}
