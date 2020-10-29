package com.softdesign.sosapplication.mvp.registration;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.softdesign.sosapplication.R;

public class RegistrationView extends AppCompatActivity {

    private RegistrationPresenter presenter;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

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
        nameEditText = findViewById(R.id.registrationName);
        emailEditText = findViewById(R.id.registrationEmail);
        passwordEditText = findViewById(R.id.registrationPassword);

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

    public String[] infoRegistrationUser() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        return new String[]{name, email, password};
    }
}
