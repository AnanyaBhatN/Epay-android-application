package com.project.epay;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    private final Context context;
    private List<Contact> contactList;
    private final OnContactClickListener listener;

    public ContactsAdapter(Context context, List<Contact> contactList, OnContactClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.txtInitial.setText(contact.getInitial());
        holder.txtName.setText(contact.getName() + "\n" + contact.getPhone());

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                getRandomGradientColors(contact.getName())
        );
        gradient.setCornerRadius(1000f);
        holder.cardInitial.setBackground(gradient);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txtInitial, txtName;
        CardView cardInitial;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInitial = itemView.findViewById(R.id.txtInitial);
            txtName = itemView.findViewById(R.id.txtName);
            cardInitial = itemView.findViewById(R.id.cardInitial);
        }
    }

    private int[] getRandomGradientColors(String name) {
        int[][] gradientSets = {
                {0xFFE57373, 0xFFF06292}, {0xFF64B5F6, 0xFF4DD0E1},
                {0xFF81C784, 0xFFAED581}, {0xFFFFB74D, 0xFFFFCC80},
                {0xFFBA68C8, 0xFF9575CD}, {0xFFA1887F, 0xFFD7CCC8},
                {0xFF90A4AE, 0xFFB0BEC5}
        };
        int index = Math.abs(name.hashCode()) % gradientSets.length;
        return gradientSets[index];
    }
}
