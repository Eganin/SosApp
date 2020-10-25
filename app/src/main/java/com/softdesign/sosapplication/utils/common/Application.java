package com.softdesign.sosapplication.utils.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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
