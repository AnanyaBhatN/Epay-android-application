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
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private final Context context;
    private List<Contact> contactList;
    private final Random random = new Random();

    public ContactsAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
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

        // Apply gradient background dynamically
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                getRandomGradientColors(contact.getName())
        );
        gradient.setCornerRadius(1000f); // fully round shape
        holder.cardInitial.setBackground(gradient);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AmountActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("phone", contact.getPhone());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    public void updateList(List<Contact> updatedList) {
        this.contactList = updatedList;
        notifyDataSetChanged();
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

    // 🎨 Random gradient color generator
    private int[] getRandomGradientColors(String name) {
        int[][] gradientSets = {
                {0xFFE57373, 0xFFF06292}, // red-pink
                {0xFF64B5F6, 0xFF4DD0E1}, // blue-cyan
                {0xFF81C784, 0xFFAED581}, // green-lime
                {0xFFFFB74D, 0xFFFFCC80}, // orange-yellow
                {0xFFBA68C8, 0xFF9575CD}, // purple-violet
                {0xFFA1887F, 0xFFD7CCC8}, // brown-grey
                {0xFF90A4AE, 0xFFB0BEC5}  // blue-grey
        };

        // same gradient for same name (consistent look)
        int index = Math.abs(name.hashCode()) % gradientSets.length;
        return gradientSets[index];
    }
}
