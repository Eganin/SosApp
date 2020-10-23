package com.softdesign.sosapplication.mvp.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.services.AcelerometrService;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;


public class MapYandexView extends AppCompatActivity {

    public MapView mapView;
    private MapPresenter presenter;
    private CoordinatorLayout coordinatorLayout;

    private static final String API_KEY = "e471b509-7c28-4a88-8ce1-e39dadfb211b";
    public  Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;// для доступа к настройкам
    private LocationRequest locationRequest; // дял сохранения запроса
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback; // для событий определения местоположения
    private Location currentLocation; // хранение широты и долготы


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactoryInit();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                startLocationUpdate();
            }
        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        clickerFloatingButton();
        startServices();
        getConditionUser();
        initLocationClient();
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

            case ConstantManager.REQUEST_LOCATION_PERMISSION:
                if(grantResults.length> 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startLocationUpdate();
                }else{
                    showSnackBarPermission("Для работы приложения необходимы разрешения");
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantManager.CHECK_SETTINGS_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("MainActivity", "User agreed permission");
                        startLocationUpdate();
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.d("MainActivity", "User not agreed permission");
                        updateLocationUI();
                        break;
                }

                break;
        }
    }


    private void startServices() {
        // start service acelerometr check
        Intent intentAcelerometry = new Intent(MapYandexView.this, AcelerometrService.class);
        startService(intentAcelerometry);


    }

    private void getConditionUser() {
        Intent currentIntent = getIntent();
        boolean currentConditionUser = currentIntent.getBooleanExtra(
                ConstantManager.CONDITION_USER_FROM_DIALOG, true);

        if (!currentConditionUser) {
            showDialog(ConstantManager.DIALOG_SOS_EXIT);
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

    protected Dialog onCreateDialog(int id) {
        if (id == ConstantManager.DIALOG_SOS_EXIT) {
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

    private void initLocationClient() {
        fusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(MapYandexView.this);
        settingsClient = LocationServices.getSettingsClient(MapYandexView.this);

        buildLocationRequest();
        buildLocationCallBack();
        buildLocationSettingsRequest();
    }

    private void buildLocationRequest() {
        // создание запроса
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);// отправляем запрос каждые 10 секунд
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);// с высокой точностью запрос
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // получаем последнее метополжение
                currentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void startLocationUpdate(){
        // проверка настроек
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(MapYandexView.this,
                        new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                // запрашиваем обновление локации
                                if (ActivityCompat.checkSelfPermission(MapYandexView.this,
                                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(MapYandexView.this,
                                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }
                                fusedLocationProviderClient.requestLocationUpdates(
                                        locationRequest,
                                        locationCallback,
                                        Looper.myLooper()
                                );
                                updateLocationUI();
                            }
                        })
                .addOnFailureListener(MapYandexView.this,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                int statusCode = ((ApiException) e).getStatusCode();

                                switch (statusCode) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException resolvableApiException =
                                                    (ResolvableApiException) e;

                                            resolvableApiException.startResolutionForResult(
                                                    MapYandexView.this,
                                                    ConstantManager.CHECK_SETTINGS_CODE

                                            );

                                        } catch (IntentSender.SendIntentException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;

                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        break;
                                }

                                updateLocationUI();
                            }
                        });
    }

    private void updateLocationUI() {
        if(currentLocation != null){
            System.out.println(currentLocation.getLatitude()+ " " +currentLocation.getLongitude());
            TARGET_LOCATION = new Point(currentLocation.getLatitude() , currentLocation.getLongitude());
        }

    }



}