package com.softdesign.sosapplication.mvp.registration;

import android.content.Intent;

import com.softdesign.sosapplication.mvp.auth.AuthView;
import com.softdesign.sosapplication.mvp.map.MapYandexView;

public class RegistrationPresenter {

    private RegistrationView view;
    private final RegistrationModel model;

    public RegistrationPresenter(RegistrationModel model) {
        this.model = model;
    }

    public void attachView(RegistrationView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void handlerFloatButton() {
        Intent intent = new Intent(view, AuthView.class);
        view.startActivity(intent);
    }


    public void handlerRegistrationButton() {
        Intent intent = new Intent(view, MapYandexView.class);
        view.startActivity(intent);
    }
}
