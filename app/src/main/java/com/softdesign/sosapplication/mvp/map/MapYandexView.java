package com.softdesign.sosapplication.mvp.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;

import java.security.Permission;


public class MapYandexView extends AppCompatActivity {

    public MapView mapView;
    private MapPresenter presenter;
    private CoordinatorLayout coordinatorLayout;

    private static final String API_KEY = "e471b509-7c28-4a88-8ce1-e39dadfb211b";
    private static final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(MapYandexView.this);
        setContentView(R.layout.map_view);
        init();
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

    private void init() {
        coordinatorLayout = findViewById(R.id.coordinator_main_layout);
        mapView = findViewById(R.id.mapView);
        MapModel model = new MapModel();
        presenter = new MapPresenter(model);
        presenter.attachView(MapYandexView.this);

        viewMap();
    }

    private void viewMap() {

        int permissionStatus = ContextCompat.checkSelfPermission(MapYandexView.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // перемещение камеры по координатам
            mapView.getMap().move(
                    new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 0),
                    null
            );
            mapView.getMap().addCameraListener(cameraListener);
        } else {
            ActivityCompat.requestPermissions(MapYandexView.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    ConstantManager.REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

    }




    private final CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition,
                                            @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

        }
    };

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackBarPermission(String text) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings_permission, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettings();
                    }
                }).show();
    }

    private void openSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, 222);
    }


}