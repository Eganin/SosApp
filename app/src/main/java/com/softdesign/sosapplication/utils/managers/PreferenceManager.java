package com.softdesign.sosapplication.utils.managers;

import android.content.SharedPreferences;

import com.softdesign.sosapplication.utils.common.Application;

public class PreferenceManager {

    private SharedPreferences sharedPreferences;

    public PreferenceManager(){
        this.sharedPreferences = Application.getSharedPreferences();
    }
}
