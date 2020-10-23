package com.softdesign.sosapplication.mvp.auth;

import android.content.Intent;

import com.softdesign.sosapplication.mvp.map.MapYandexView;
import com.softdesign.sosapplication.mvp.registration.RegistrationView;

public class AuthPresenter {

    private AuthView view;
    private final AuthModel model;

    public AuthPresenter(AuthModel model) {
        this.model = model;
    }

    public void attachView(AuthView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void handlerFloatButton() {
        Intent intent = new Intent(view, RegistrationView.class);
        view.startActivity(intent);
    }


    public void handlerAuthButton() {
        Intent intent = new Intent(view, MapYandexView.class);
        view.startActivity(intent);
    }
}
