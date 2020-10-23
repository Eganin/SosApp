package com.softdesign.sosapplication.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softdesign.sosapplication.R;

import java.util.Set;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Set<String> contacts;
    private Context context;

    public ContactAdapter(Set<String> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
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
            int position = getAdapterPosition();
            int counter = -1;
            for (String el : contacts) {
                counter++;
                if (counter == position) {

                }
            }
        }
    }
}
