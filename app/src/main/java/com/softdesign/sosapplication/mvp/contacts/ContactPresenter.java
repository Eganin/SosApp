package com.softdesign.sosapplication.mvp.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;

import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.managers.DataManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactPresenter {
    private final ContactModel model;
    private ContactView view;

    public ContactPresenter(ContactModel model) {
        this.model = model;
    }

    public void attachView(ContactView view) {

        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }


    public Set<String> getAllContacts() {

        Set<String> setPhones = new HashSet<String>();

        Cursor cursor1 = view.getContentResolver().query(ContactsContract.
                        CommonDataKinds.Phone.CONTENT_URI, new String[]
                        {ContactsContract.CommonDataKinds.Phone._ID,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        if (cursor1.getCount() > 0) {
            while (cursor1.moveToNext()) {
                setPhones.add(cursor1.getString(1) + ":"
                        + cursor1.getString(2));
            }
        } else {
            view.showSnackBar("У вас нет контактов");
        }

        return setPhones;
    }

    public void openSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + view.getPackageName()));

        view.startActivityForResult(appSettingsIntent, 222);
    }

    public void saveBooleanShowDialogContact(boolean flag){
        DataManager.getInstance().getPreferenceManager().saveBooleanShowDialogContact(flag);
    }

}
