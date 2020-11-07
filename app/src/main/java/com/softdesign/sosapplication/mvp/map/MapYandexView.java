package com.softdesign.sosapplication.mvp.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.managers.DataManager;
import com.softdesign.sosapplication.utils.managers.PreferenceManager;
import com.softdesign.sosapplication.utils.network.API;
import com.softdesign.sosapplication.utils.services.AcelerometrService;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import kotlin.reflect.jvm.internal.impl.load.java.Constant;


public class MapYandexView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public MapView mapView;
    private MapPresenter presenter;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final String API_KEY = "e471b509-7c28-4a88-8ce1-e39dadfb211b";
    public Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;// для доступа к настройкам
    private LocationRequest locationRequest; // дял сохранения запроса
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback; // для событий определения местоположения
    private static Location currentLocation; // хранение широты и долготы

    private boolean isRoadMap;
    private String isRoadUser = DataManager.getInstance().getPreferenceManager().loadIsRoad();

    private List<List<Double>> listCoordinatsDefaultUser = new ArrayList<>();

    private static Date date;
    ;


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
        restartUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartUI();
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
        visibleImageCoordinats();
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyUI();
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
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdate();
                } else {
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
                        startLocationUpdate();
                        break;

                    case Activity.RESULT_CANCELED:
                        updateLocationUI();
                        break;
                }

                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.roadMap:
                if (!isRoadMap) {
                    isRoadMap = true;
                    isRoadUser = "YES";
                    DataManager.getInstance().getPreferenceManager().saveIsRoad(isRoadUser);
                    showDialog(ConstantManager.DIALOG_PATH_START
                    );
                } else {
                    isRoadMap = false;
                    DataManager.getInstance().getPreferenceManager().saveSizeCoordinats(listCoordinatsDefaultUser.size());
                    showDialog(ConstantManager.DIALOG_PATH_DEFAULT_USER);

                }
                break;

            case R.id.settings:
                presenter.openSettings();
                break;

            case R.id.perediocNotification:
                showDialog(ConstantManager.DIALOG_TIME_MINUTES_NOTIFICATION);
                break;

            case R.id.time_sos_signal_menu:
                showDialog(ConstantManager.DIALOG_TIME_SOS_SIGNAL_FROM_SERVICE);
                break;
        }

        return true;
    }


    private void saveDefaultPathUser() {
        PreferenceManager preferenceManager = DataManager.getInstance().getPreferenceManager();
        preferenceManager.saveSizeContacts(listCoordinatsDefaultUser.size());
        for (int i = 0; i < +listCoordinatsDefaultUser.size(); i++) {
            preferenceManager.saveDefaultCoordinatUser(listCoordinatsDefaultUser.get(i), i);
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
        drawerLayout = findViewById(R.id.drawerLayoutMain);
        mapView = findViewById(R.id.mapView);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        presenter = new MapPresenter();
        presenter.attachView(MapYandexView.this);
        date = new Date();
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
            presenter.loadYandexMap(TARGET_LOCATION);
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

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == ConstantManager.DIALOG_SOS_EXIT || id == ConstantManager.DIALOG_PATH_SOS) {
            final AlertDialog.Builder adb = new AlertDialog.Builder(MapYandexView.this);
            long timeSeconds = DataManager.getInstance().getPreferenceManager().loadMinutesSendSignalSOS();
            final CountDownTimer timer = new CountDownTimer(timeSeconds*60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    try {
                        presenter.sosMailingContacts(currentLocation.getLatitude(), currentLocation.getLongitude());
                        adb.create().cancel();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            adb.setTitle("Послать сигнал SOS?");
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timer.cancel();
                    try {
                        presenter.sosMailingContacts(currentLocation.getLatitude(), currentLocation.getLongitude());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            });

            adb.setNegativeButton(R.string.cancel_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timer.cancel();
                    dialog.cancel();
                }
            });

            return adb.create();
        } else if (id == ConstantManager.DIALOG_PATH_DEFAULT_USER) {
            AlertDialog.Builder adb = new AlertDialog.Builder(MapYandexView.this);
            adb.setTitle("Ваш обычный путь записан");
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveDefaultPathUser();
                }
            });

            adb.setNegativeButton(R.string.cancel_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return adb.create();
        } else if (id == ConstantManager.DIALOG_PATH_START) {
            AlertDialog.Builder adb = new AlertDialog.Builder(MapYandexView.this);
            adb.setTitle("Запись обчного пути");
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return adb.create();
        } else if (id == ConstantManager.DIALOG_TIME_MINUTES_NOTIFICATION) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View promt = inflater.inflate(R.layout.promt, null);

            AlertDialog.Builder adb = new AlertDialog.Builder(MapYandexView.this);
            adb.setView(promt);
            final EditText editText = promt.findViewById(R.id.edit_text_time);
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int resultMinutes = Integer.parseInt(editText.getText().toString());
                    DataManager.getInstance().getPreferenceManager().savePeriodicityMinutesNotifications(resultMinutes);
                }
            });

            adb.setNegativeButton(R.string.cancel_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return adb.create();
        }else if(id == ConstantManager.DIALOG_TIME_SOS_SIGNAL_FROM_SERVICE){
            LayoutInflater infalter = LayoutInflater.from(this);
            View promtDialog = infalter.inflate(R.layout.promt_sos_signal,null);

            AlertDialog.Builder adb = new AlertDialog.Builder(MapYandexView.this);
            adb.setView(promtDialog);
            final EditText editText = promtDialog.findViewById(R.id.edit_text_dialog_sos);
            adb.setPositiveButton(R.string.yes_add_contact, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int resultMinutes = Integer.parseInt(editText.getText().toString());
                    DataManager.getInstance().getPreferenceManager().saveMinutesSendSignalSOS(resultMinutes);
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

    private void startLocationUpdate() {
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
        if (currentLocation != null) {
            Point currentPoint = new Point(currentLocation.getLatitude(), currentLocation.getLongitude());
            double currentLatitude = currentPoint.getLatitude();
            double currentLongitude = currentPoint.getLongitude();
            if (isRoadMap && isRoadUser.equals("YES")) {
                listCoordinatsDefaultUser.add(Arrays.<Double>asList(currentLatitude, currentLongitude));
            } else if (!isRoadUser.equals("NO")) {
                equalsCoordiants(currentLatitude, currentLongitude);
            }
            presenter.loadYandexMap(currentPoint);
        }

    }


    private void equalsCoordiants(double currentLatitude, double currentLongitude) {
        PreferenceManager currentPreferenceManager = DataManager.getInstance().getPreferenceManager();
        int size = currentPreferenceManager.loadSizeCoordinats();
        Random random = new Random();
        int position = random.nextInt(size) + 1;
        List<Double> coords = currentPreferenceManager.loadDefaultCoordinatsUser(position);
        double latitude = coords.get(0);
        double longitude = coords.get(1);
        if (!equalsDouble(latitude, longitude, currentLatitude, currentLongitude)) {
            repeatNotification(date);

        }
    }

    private boolean equalsDouble(double firstLat, double firstLon, double secondLat, double secondLon) {
        final double r = 6371;
        double sin1 = Math.sin((firstLat - secondLat) / 2);
        double sin2 = Math.sin((firstLon - secondLon) / 2);
        double result = 2 * r * Math.asin(Math.sqrt(sin1 * sin1 + sin2 * sin2 * Math.cos(firstLat) * Math.cos(secondLat)));
        if (result > 50) {
            return false;
        } else {
            return true;
        }
    }

    private void sendNotification() {

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

        AcelerometrService.startTimer();
        // запускаем увкдовлемение
        notificationManager.notify(NOTIFICATIONS_ID, builder.build());
    }

    private void repeatNotification(Date dateNew) {
        if ((new Date().getMinutes() - dateNew.getMinutes()) >= DataManager.getInstance()
                .getPreferenceManager().loadPeriodicityMinutesNotifications()) {
            sendNotification();
            date = new Date();
        }
    }

    private void visibleImageCoordinats() {
        final ImageView imageView = findViewById(R.id.coordinat_image_view);
        Handler handler = new Handler();
        handler.postDelayed(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {

                                imageView.setVisibility(View.VISIBLE);

                            }
                        });
                    }
                }), 4000);

    }

    private void sendSosSignal(Date date) {

    }

    private void restartUI() {
        presenter.attachView(MapYandexView.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startLocationUpdate();
            }
        }).start();
    }

    private void destroyUI() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    public static List<Double> getCurrentLocation() {
        return Arrays.asList(currentLocation.getLatitude(), currentLocation.getLongitude());
    }
}