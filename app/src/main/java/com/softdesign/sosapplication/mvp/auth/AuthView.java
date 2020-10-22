package com.softdesign.sosapplication.mvp.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.softdesign.sosapplication.R;

public class AuthView extends AppCompatActivity {

    private AuthPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachView();
    }

    private void init() {
        AuthModel model = new AuthModel();
        presenter = new AuthPresenter(model);
        presenter.attachView(AuthView.this);


        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handlerFloatButton();
            }
        });

        findViewById(R.id.remeberPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handlerRecyclerButton();
            }
        });

        findViewById(R.id.authButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handlerAuthButton();
            }
        });
    }
}