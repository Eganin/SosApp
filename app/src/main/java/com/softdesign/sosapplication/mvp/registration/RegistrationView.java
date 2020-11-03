package com.softdesign.sosapplication.mvp.registration;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.softdesign.sosapplication.R;

public class RegistrationView extends AppCompatActivity {

    private RegistrationPresenter presenter;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText numberUserEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_view);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }
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
        numberUserEditText = findViewById(R.id.registrationNumberUser);

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
        String numberUser = numberUserEditText.getText().toString();
        return new String[]{name, email, password,numberUser};
    }
}
