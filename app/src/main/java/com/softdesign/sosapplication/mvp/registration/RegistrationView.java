package com.softdesign.sosapplication.mvp.registration;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.softdesign.sosapplication.R;

public class RegistrationView extends AppCompatActivity {

    private RegistrationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_view);
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
        presenter.attachView(RegistrationView.this);
    }

    private void init() {
        RegistrationModel model = new RegistrationModel();
        presenter = new RegistrationPresenter(model);
        presenter.attachView(RegistrationView.this);

        findViewById(R.id.floatingActionButtonRegistration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handlerFloatButton();
            }
        });

        findViewById(R.id.registrationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handlerRegistrationButton();
            }
        });
    }
}
