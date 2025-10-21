package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BankSelectionActivity extends AppCompatActivity {
    public static final String EXTRA_BANK_NAME = "extra_bank_name";

    TextView tvSelected;
    String selectedBank = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_selection);

        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgClose = findViewById(R.id.imgClose);

        imgBack.setOnClickListener(v -> finish());
        imgClose.setOnClickListener(v -> finishAffinity());

        tvSelected = findViewById(R.id.tvSelected);

        ImageView btnSBI = findViewById(R.id.imgSBI);
        ImageView btnBOB = findViewById(R.id.imgBOB);
        ImageView btnUnion = findViewById(R.id.imgUnion);
        ImageView btnKotak = findViewById(R.id.imgKotak);
        ImageView btnCanara = findViewById(R.id.imgCanara);
        ImageView btnAxis = findViewById(R.id.imgAxis);

        View.OnClickListener bankClick = v -> {
            if (v.getId() == R.id.imgSBI) selectedBank = "SBI";
            else if (v.getId() == R.id.imgBOB) selectedBank = "BOB";
            else if (v.getId() == R.id.imgUnion) selectedBank = "Union";
            else if (v.getId() == R.id.imgKotak) selectedBank = "Kotak";
            else if (v.getId() == R.id.imgCanara) selectedBank = "Canara";
            else if (v.getId() == R.id.imgAxis) selectedBank = "Axis";

            tvSelected.setText("Selected Bank: " + selectedBank);

            Intent i = new Intent(BankSelectionActivity.this, BankDetailsActivity.class);
            i.putExtra(EXTRA_BANK_NAME, selectedBank);
            startActivity(i);
        };

        btnSBI.setOnClickListener(bankClick);
        btnBOB.setOnClickListener(bankClick);
        btnUnion.setOnClickListener(bankClick);
        btnKotak.setOnClickListener(bankClick);
        btnCanara.setOnClickListener(bankClick);
        btnAxis.setOnClickListener(bankClick);
    }
}