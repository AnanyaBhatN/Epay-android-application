package com.project.epay;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private final List<Offer> offerList;
    private final OnOfferClickListener listener;
    private final Map<String, Integer> categoryColors;

    public interface OnOfferClickListener {
        void onOfferClick(Offer offer);
    }

    public OfferAdapter(List<Offer> offerList, OnOfferClickListener listener) {
        this.offerList = offerList;
        this.listener = listener;

        categoryColors = new HashMap<>();
        categoryColors.put("Beauty & Wellness", Color.parseColor("#00BCD4")); // Cyan
        categoryColors.put("Fashion", Color.parseColor("#9C27B0"));           // Purple
        categoryColors.put("Health", Color.parseColor("#4CAF50"));            // Green
        categoryColors.put("Daily / Needs", Color.parseColor("#FF9800"));     // Orange
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.bind(offer, listener);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvOfferTitle, tvOfferDescription;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_icon);
            tvOfferTitle = itemView.findViewById(R.id.tv_offer_title);
            tvOfferDescription = itemView.findViewById(R.id.tv_offer_description);
        }

        public void bind(final Offer offer, final OnOfferClickListener listener) {
            tvOfferTitle.setText(offer.getTitle());
            tvOfferDescription.setText(offer.getDescription());
            tvIcon.setText(String.valueOf(offer.getTitle().charAt(0)));

            int color = categoryColors.getOrDefault(offer.getCategory(), Color.GRAY);
            GradientDrawable background = (GradientDrawable) tvIcon.getBackground();
            background.setColor(color);

            itemView.setOnClickListener(v -> listener.onOfferClick(offer));
        }
    }
}