package com.softdesign.sosapplication.utils.managers;

import android.content.SharedPreferences;

import com.softdesign.sosapplication.utils.common.Application;
import com.softdesign.sosapplication.utils.common.ConstantManager;

import java.util.Arrays;
import java.util.List;

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

    public void saveDefaultCoordinatUser(List<Double> coordinat, int counter) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(ConstantManager.SAVE_COORDINAT_ONE + counter, Float.parseFloat(String.valueOf(coordinat.get(0))));
        editor.putFloat(ConstantManager.SAVE_COORDINAT_TWO + counter, Float.parseFloat(String.valueOf(coordinat.get(1))));

        editor.apply();
    }

    public List<Double> loadDefaultCoordinatsUser(int position) {
        double latitude = sharedPreferences.getFloat(ConstantManager.SAVE_COORDINAT_ONE + position, 0);
        double longitude = sharedPreferences.getFloat(ConstantManager.SAVE_COORDINAT_TWO + position, 0);

        return Arrays.asList(latitude, longitude);
    }

    public void saveSizeCoordinats(int size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ConstantManager.SAVE_SIZE_COORDINATS, size);

        editor.apply();
    }

    public int loadSizeCoordinats() {
        return sharedPreferences.getInt(ConstantManager.SAVE_SIZE_COORDINATS, 0);
    }
}
