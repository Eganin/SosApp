package com.softdesign.sosapplication.utils.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softdesign.sosapplication.R;
import com.softdesign.sosapplication.mvp.contacts.ContactModel;
import com.softdesign.sosapplication.mvp.contacts.ContactPresenter;
import com.softdesign.sosapplication.mvp.contacts.ContactView;
import com.softdesign.sosapplication.utils.common.ConstantManager;
import com.softdesign.sosapplication.utils.managers.DataManager;

import java.util.Set;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Set<String> contacts;
    private Context context;
    private ContactView view;

    public ContactAdapter(Set<String> contacts, Context context , ContactView view ) {
        this.contacts = contacts;
        this.context = context;
        this.view=view;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        ContactViewHolder viewHolder = new ContactViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        String phoneAndName = "";
        int counter = -1;
        for (String el : contacts) {
            counter++;
            if (counter == position) {
                phoneAndName = el;
            }
        }

        holder.nameContact.setText(phoneAndName);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView nameContact;
        public LinearLayout contactLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameContact = itemView.findViewById(R.id.contactName);
            this.contactLayout = itemView.findViewById(R.id.contactLayout);

            this.contactLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            view.showDialog(ConstantManager.DIALOG_EXIT);
            boolean flag = DataManager.getInstance().getPreferenceManager()
                    .loadBooleanShowDialogContact();
            if(flag){
                addContact();
            }
        }

        public void addContact() {
            int position = getAdapterPosition();
            int counter = -1;
            for (String contact : contacts) {
                counter++;
                if (counter == position) {
                    DataManager.getInstance().getPreferenceManager().saveSizeContacts(contacts.size());
                    DataManager.getInstance().getPreferenceManager().saveContact(counter, contact);
                }
            }
        }



    }
}
