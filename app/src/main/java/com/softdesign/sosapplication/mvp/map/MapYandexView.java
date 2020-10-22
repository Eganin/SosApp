package com.softdesign.sosapplication.mvp.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;


public class MapYandexView extends AppCompatActivity {

    public MapView mapView;
    private MapPresenter presenter;
    private CoordinatorLayout coordinatorLayout;

    private static final String API_KEY = "e471b509-7c28-4a88-8ce1-e39dadfb211b";
    public static final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactoryInit();
        setContentView(R.layout.map_view);
        init();
        clickerFloatingButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(MapYandexView.this);
        presenter.loadYandexMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantManager.REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewMap();
                } else {
                    showSnackBarPermission("Для работы приложения необходимы разрешения");
                }
        }
    }

    private void MapKitFactoryInit(){
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(MapYandexView.this);
    }

    private void init() {
        coordinatorLayout = findViewById(R.id.coordinator_main_layout);
        mapView = findViewById(R.id.mapView);
        MapModel model = new MapModel();
        presenter = new MapPresenter(model);
        presenter.attachView(MapYandexView.this);

        viewMap();
    }

    private void clickerFloatingButton(){
        findViewById(R.id.addContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.SOSButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void viewMap() {

        int permissionStatus = ContextCompat.checkSelfPermission(MapYandexView.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // перемещение камеры по координатам
            presenter.loadYandexMap();
        } else {
            ActivityCompat.requestPermissions(MapYandexView.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    ConstantManager.REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

    }

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackBarPermission(String text) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings_permission, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.openSettings();
                    }
                }).show();
    }




}