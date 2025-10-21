package com.project.epay;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;

public class OfferMainActivity extends AppCompatActivity {

    private CheckBox cbBeauty, cbDaily, cbFashion, cbHealth;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.off_activity);

        // --- CODE TO SET ICON COLORS ---
        TextView tvIconReward1 = findViewById(R.id.tv_icon_reward1);
        TextView tvIconReward2 = findViewById(R.id.tv_icon_reward2);

        GradientDrawable background1 = (GradientDrawable) tvIconReward1.getBackground();
        background1.setColor(Color.parseColor("#F44336")); // Red for Zomato

        GradientDrawable background2 = (GradientDrawable) tvIconReward2.getBackground();
        background2.setColor(Color.parseColor("#FF9800")); // Orange for Boat

        // --- NEW: CODE TO HANDLE REWARD CARD CLICKS ---
        CardView cardReward1 = findViewById(R.id.card_reward1);
        CardView cardReward2 = findViewById(R.id.card_reward2);

        // Create Offer objects for the static rewards
        Offer zomatoOffer = new Offer("Daily / Needs", "Zomato", "Get upto ₹100 cashback", "ZOMATO100");
        Offer boatOffer = new Offer("Daily / Needs", "Boat Earphones", "Get upto ₹200 cashback", "BOAT200");

        // Set click listeners
        cardReward1.setOnClickListener(v -> showOfferDetailsDialog(zomatoOffer));
        cardReward2.setOnClickListener(v -> showOfferDetailsDialog(boatOffer));


        // --- PREFERENCES LOGIC (Unchanged) ---
        cbBeauty = findViewById(R.id.cb_beauty);
        cbDaily = findViewById(R.id.cb_daily);
        cbFashion = findViewById(R.id.cb_fashion);
        cbHealth = findViewById(R.id.cb_health);
        btnNext = findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {
            ArrayList<String> selectedPreferences = new ArrayList<>();
            if (cbBeauty.isChecked()) {
                selectedPreferences.add("Beauty & Wellness");
            }
            if (cbDaily.isChecked()) {
                selectedPreferences.add("Daily / Needs");
            }
            if (cbFashion.isChecked()) {
                selectedPreferences.add("Fashion");
            }
            if (cbHealth.isChecked()) {
                selectedPreferences.add("Health");
            }

            Intent intent = new Intent(OfferMainActivity.this, OffersActivity.class);
            intent.putStringArrayListExtra("SELECTED_PREFERENCES", selectedPreferences);
            startActivity(intent);
        });
    }

    // --- NEW: DIALOG-HANDLING METHODS (Copied from OffersActivity) ---
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