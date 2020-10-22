package com.softdesign.sosapplication.mvp.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.utils.adapters.ContactAdapter;
import com.softdesign.sosapplication.utils.common.ConstantManager;

import java.util.Arrays;
import java.util.List;

public class ContactView extends AppCompatActivity {

    private ContactPresenter presenter;
    private CoordinatorLayout coordinatorLayout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_view);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(ContactView.this);
        List<String> contacts = getContacts();
        initRecyclerView(contacts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantManager.REQUEST_CODE_WORK_CONTACTS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    presenter.getAllContacts();
                } else {
                    showSnackBarPermission("Для работы приложения необходимы разрешения");
                    return;
                }
        }
    }

    private void initRecyclerView(List<String> contacts) {
        recyclerView = findViewById(R.id.contactsRecyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new ContactAdapter(contacts , getApplicationContext());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void init() {
        ContactModel model = new ContactModel();
        presenter = new ContactPresenter(model);
        coordinatorLayout = findViewById(R.id.coordinator);
    }

    private List<String> getContacts() {
        int permissionStatusWriteContacts = ContextCompat.checkSelfPermission(ContactView.this,
                Manifest.permission.WRITE_CONTACTS);

        int permissionStatusReadContacts = ContextCompat.checkSelfPermission(ContactView.this,
                Manifest.permission.READ_CONTACTS);

        if (permissionStatusReadContacts == PackageManager.PERMISSION_GRANTED &&
                permissionStatusWriteContacts == PackageManager.PERMISSION_GRANTED) {
            List<String> listContacts = presenter.getAllContacts();

            return listContacts;
        } else {
            ActivityCompat.requestPermissions(ContactView.this, new String[]{
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_CONTACTS},
                    ConstantManager.REQUEST_CODE_WORK_CONTACTS);
        }

        return Arrays.asList();
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
}
