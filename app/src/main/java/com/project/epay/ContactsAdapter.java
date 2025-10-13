package com.project.epay;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private Context context;
    private List<Contact> contactList;

    public ContactsAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // Use getter methods instead of direct access
        holder.txtInitial.setText(contact.getInitial());
        holder.txtName.setText(contact.getName() + "\n" + contact.getPhone());
        holder.cardInitial.setCardBackgroundColor(contact.getColor());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AmountActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("phone", contact.getPhone());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txtInitial, txtName;
        CardView cardInitial;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInitial = itemView.findViewById(R.id.txtInitial);
            txtName = itemView.findViewById(R.id.txtName);
            cardInitial = itemView.findViewById(R.id.cardInitial);
        }
    }
}
