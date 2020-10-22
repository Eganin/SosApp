package com.softdesign.sosapplication.mvp.map;


import android.content.Context;


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

    }
}
