package com.softdesign.sosapplication.utils.managers;

public class DataManager {

    private static DataManager INSTANCE = null;
    private PreferenceManager preferenceManager;

    private DataManager() {
        this.preferenceManager = new PreferenceManager();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }

        return INSTANCE;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }


}
