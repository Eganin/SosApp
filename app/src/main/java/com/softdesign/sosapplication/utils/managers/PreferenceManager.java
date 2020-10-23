package com.softdesign.sosapplication.utils.managers;

import android.content.SharedPreferences;

import com.softdesign.sosapplication.utils.common.Application;
import com.softdesign.sosapplication.utils.common.ConstantManager;

public class PreferenceManager {

    private SharedPreferences sharedPreferences;

    public PreferenceManager() {
        this.sharedPreferences = Application.getSharedPreferences();
    }

    public void saveContact(int position, String contact) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstantManager.SAVE_CONTACT_CONSTANT + position, contact);

        editor.apply();
    }

    public void saveSizeContacts(int sizeContacts) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ConstantManager.SIZE_CONTACT_CONSTANT, sizeContacts);

        editor.apply();
    }

    public int loadSizeContact() {
        int size = sharedPreferences.getInt(ConstantManager.SIZE_CONTACT_CONSTANT, 0);

        return size;
    }

    public String loadContact(int position) {
        return sharedPreferences.getString(ConstantManager.SAVE_CONTACT_CONSTANT + position, "UNKNOWN");
    }

    public void saveBooleanShowDialogContact(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ConstantManager.SAVE_SHOW_CONTACT_DIALOG, flag);

        editor.apply();
    }

    public boolean loadBooleanShowDialogContact() {
        return sharedPreferences.getBoolean(ConstantManager.SAVE_SHOW_CONTACT_DIALOG, false);
    }
}
