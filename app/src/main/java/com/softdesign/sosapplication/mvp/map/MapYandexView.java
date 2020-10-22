package com.softdesign.sosapplication.mvp.map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.softdesign.sosapplication.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;


public class MapYandexView extends AppCompatActivity {

    public MapView mapView;
    private MapPresenter presenter;

    private static final String API_KEY = "e471b509-7c28-4a88-8ce1-e39dadfb211b";
    private static final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mapView.onStop();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    private void init() {
        MapModel model = new MapModel();
        presenter = new MapPresenter(model);
        presenter.attachView(MapYandexView.this);

        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(MapYandexView.this);

        mapView = findViewById(R.id.mapView);
        // перемещение камеры по координатам
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null
        );

        mapView.getMap().addCameraListener(cameraListener);
    }

    private final CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

        }
    };

}