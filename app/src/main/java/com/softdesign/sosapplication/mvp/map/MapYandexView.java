package com.softdesign.sosapplication.mvp.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.common.Application;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.services.AcelerometrService;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;

import java.util.Timer;


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
        // start service acelerometr check
        Intent intent = new Intent(MapYandexView.this , AcelerometrService.class);
        startService(intent);
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

            case ConstantManager.REQUEST_CODE_SEND_SMS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialog(ConstantManager.DIALOG_SOS_EXIT);
                } else {
                    showSnackBarPermission("Для работы приложения необходимы разрешения");
                }
        }
    }


    private void MapKitFactoryInit() {
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

    private void clickerFloatingButton() {
        findViewById(R.id.addContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addContact();
            }
        });

        findViewById(R.id.SOSButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapYandexView.this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    showDialog(ConstantManager.DIALOG_SOS_EXIT);
                } else {
                    ActivityCompat.requestPermissions(MapYandexView.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            ConstantManager.REQUEST_CODE_SEND_SMS);
                }
            }
        });
    }

    private void viewMap() {

        int permissionStatus = ContextCompat.checkSelfPermission(MapYandexView.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // перемещение камеры по координатам
            presenter.attachView(MapYandexView.this);
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

    protected Dialog onCreateDialog(int id){
        if(id == ConstantManager.DIALOG_SOS_EXIT){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Послать сигнал SOS?");
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.sosMailingContacts();
                }
            });

            adb.setNegativeButton(R.string.cancel_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return adb.create();
        }

        return super.onCreateDialog(id);
    }



}