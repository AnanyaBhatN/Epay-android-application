package com.project.epay;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OfferAdapter offerAdapter;
    private List<Offer> allOffersList;
    private List<Offer> filteredOffersList;
    private ImageView ivBack;
    private ImageView ivHome; // <-- ADD THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // --- UPDATE THIS SECTION ---
        ivBack = findViewById(R.id.imgBack);
        ivHome = findViewById(R.id.imgClose ); // <-- ADD THIS

        ivBack.setOnClickListener(v -> {
            finish(); // Go back to the previous screen
        });

        // Add a click listener for the home icon
        ivHome.setOnClickListener(v -> {
            Intent intent = new Intent(OffersActivity.this, OfferMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        // -------------------------

        loadAllOffers();
        filteredOffersList = new ArrayList<>();

        ArrayList<String> selectedPreferences = getIntent().getStringArrayListExtra("SELECTED_PREFERENCES");

        if (selectedPreferences != null && !selectedPreferences.isEmpty()) {
            for (Offer offer : allOffersList) {
                if (selectedPreferences.contains(offer.getCategory())) {
                    filteredOffersList.add(offer);
                }
            }
        } else {
            filteredOffersList.addAll(allOffersList);
        }

        recyclerView = findViewById(R.id.recycler_view_offers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerAdapter = new OfferAdapter(filteredOffersList, this::showOfferDetailsDialog);
        recyclerView.setAdapter(offerAdapter);
    }

    // ... (rest of the file is the same)
    private void loadAllOffers() {
        allOffersList = new ArrayList<>();
        allOffersList.add(new Offer("Beauty & Wellness", "Beauty & Wellness", "Get upto $200 cashback", "BEAUTY200"));
        allOffersList.add(new Offer("Fashion", "Fashion", "Upto 20% off", "FASHION20"));
        allOffersList.add(new Offer("Fashion", "Fashion Reward", "Get upto ₹100 cashback", "REWARD100"));
        allOffersList.add(new Offer("Health", "Health", "Flat 10% off on medicine", "HEALTH10"));
        allOffersList.add(new Offer("Daily / Needs", "Zomato", "Get upto ₹100 cashback", "ZOMATO100"));
        allOffersList.add(new Offer("Daily / Needs", "Boat Earphones", "Get upto ₹200 cashback", "BOAT200"));
    }

    private void showOfferDetailsDialog(final Offer offer) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_offer_details);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogDescription = dialog.findViewById(R.id.dialog_description);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnCheck = dialog.findViewById(R.id.btn_check);
        dialogTitle.setText(offer.getTitle());
        dialogDescription.setText(offer.getDescription());
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnCheck.setOnClickListener(v -> {
            dialog.dismiss();
            showRedeemedDialog(offer);
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void showRedeemedDialog(Offer offer) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_redemeed);
        TextView tvRedeemCode = dialog.findViewById(R.id.tv_redeem_code);
        Button btnClose = dialog.findViewById(R.id.btn_close);
        tvRedeemCode.setText(offer.getCode());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}