package com.softdesign.sosapplication.utils.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.mvp.map.MapYandexView;
import com.softdesign.sosapplication.utils.services.AcelerometrService;


public class Application extends android.app.Application {

    public static SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.this);

    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


}
