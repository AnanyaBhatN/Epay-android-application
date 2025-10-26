package com.project.epay;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private ImageView ivBack;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        ivBack = findViewById(R.id.ivBack);
        ImageView btnHome = findViewById(R.id.btn_home); // make sure btn_home ID matches your XML

        ivBack.setOnClickListener(v -> navigateToDashboard());
        btnHome.setOnClickListener(v -> navigateToDashboard());
        recyclerView = findViewById(R.id.rvTransactions);
        ivBack = findViewById(R.id.ivBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        emailKey = getIntent().getStringExtra("emailKey");
        if (emailKey == null || emailKey.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        emailKey = emailKey.replace(".", "_");

        // Start fetching data
        fetchRecharges(emailKey);
    }
    private void navigateToDashboard() {
        Intent intent = new Intent(TransactionHistory.this, DashboardActivity.class);
        intent.putExtra("emailKey", emailKey); // pass user email
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    /** 🔹 Step 1: Fetch Recharges */
    private void fetchRecharges(String emailKey) {
        DatabaseReference rechargeRef = FirebaseDatabase.getInstance()
                .getReference("Recharges")
                .child(emailKey);

        rechargeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TXN_DEBUG", "Recharges snapshot exists? " + snapshot.exists());
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        if (!ds.child("operator").exists()) continue;

                        String id = ds.getKey();
                        String amountStr = ds.child("amount").getValue(String.class);
                        String mobileNumber = ds.child("mobileNumber").getValue(String.class);
                        String operator = ds.child("operator").getValue(String.class);
                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);

                        int amount = 0;
                        try { amount = Integer.parseInt(amountStr); } catch (Exception ignored) {}

                        Transaction txn = new Transaction(
                                id,
                                Transaction.Type.RECHARGE,
                                operator != null ? operator : mobileNumber,
                                amount,
                                dateTime,
                                status != null ? status : "Success",
                                "Mobile Recharge"
                        );

                        transactionList.add(txn);
                        Log.d("TXN_DEBUG", "Added Recharge: " + txn.getCounterpart() + ", " + txn.getAmount());
                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing recharge", e);
                    }
                }
                fetchSendMoney(emailKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Failed to fetch recharges: " + error.getMessage());
            }
        });
    }

    /** 🔹 Step 2: Fetch SendMoney */
    private void fetchSendMoney(String emailKey) {
        DatabaseReference sendMoneyRef = FirebaseDatabase.getInstance()
                .getReference("SendMoney")
                .child(emailKey);

        sendMoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TXN_DEBUG", "SendMoney snapshot exists? " + snapshot.exists());
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String id = ds.getKey();
                        Object amtObj = ds.child("amount").getValue();
                        int amount = getAmount(amtObj);
                        String toName = ds.child("toName").getValue(String.class);
                        String toPhone = ds.child("toPhone").getValue(String.class);
                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String typeStr = ds.child("type").getValue(String.class);

                        Transaction txn = new Transaction(
                                id,
                                Transaction.Type.SENDMONEY,
                                firstNonEmpty(toName, toPhone, "Unknown"),
                                amount,
                                dateTime,
                                "Success",
                                typeStr != null ? typeStr : "UPI Payment"
                        );

                        transactionList.add(txn);
                        Log.d("TXN_DEBUG", "Added SendMoney: " + txn.getCounterpart() + ", " + txn.getAmount());
                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing SendMoney", e);
                    }
                }
                fetchTransactions(emailKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Failed to fetch SendMoney: " + error.getMessage());
            }
        });
    }

    /** 🔹 Step 3: Fetch transactions */
    private void fetchTransactions(String emailKey) {
        DatabaseReference txnRef = FirebaseDatabase.getInstance()
                .getReference("transactions")
                .child(emailKey);

        txnRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TXN_DEBUG", "Transactions snapshot exists? " + snapshot.exists());
                Log.d("TXN_DEBUG", "Children count: " + snapshot.getChildrenCount());

                if (!snapshot.exists()) return;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String id = ds.getKey();
                        String type = ds.child("type").getValue(String.class);
                        Object amtObj = ds.child("amount").getValue();
                        int amount = getAmount(amtObj);

                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);
                        String recipientName = ds.child("recipientName").getValue(String.class);
                        String recipientUpi = ds.child("recipientUpi").getValue(String.class);
                        String senderName = ds.child("senderName").getValue(String.class);
                        String senderUpi = ds.child("senderUpi").getValue(String.class);

                        String counterpart;
                        Transaction.Type txnType;
                        if ("Sent".equalsIgnoreCase(type)) {
                            txnType = Transaction.Type.PAID;
                            counterpart = firstNonEmpty(recipientName, recipientUpi, "Unknown");
                        } else if ("Received".equalsIgnoreCase(type)) {
                            txnType = Transaction.Type.RECEIVED;
                            counterpart = firstNonEmpty(senderName, senderUpi, "Unknown");
                        } else {
                            txnType = Transaction.Type.OTHER;
                            counterpart = "Unknown";
                        }

                        Transaction txn = new Transaction(
                                id,
                                txnType,
                                counterpart,
                                amount,
                                dateTime,
                                status != null ? status : "Success",
                                "UPI Payment"
                        );

                        transactionList.add(txn);
                        Log.d("TXN_DEBUG", "Added Transaction: " + txn.getCounterpart() + ", " + txn.getAmount());
                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing transaction", e);
                    }
                }

                sortTransactions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Error fetching transactions: " + error.getMessage());
            }
        });
    }

    /** 🔹 Step 4: Sort transactions */
    private void sortTransactions() {
        Collections.sort(transactionList, new Comparator<Transaction>() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public int compare(Transaction t1, Transaction t2) {
                try {
                    Date d1 = sdf.parse(t1.getDateTime());
                    Date d2 = sdf.parse(t2.getDateTime());
                    return d2.compareTo(d1); // latest first
                } catch (ParseException e) {
                    Log.e("TXN_ERROR", "Date parse error", e);
                    return 0;
                }
            }
        });

        adapter.notifyDataSetChanged();

        if (transactionList.isEmpty()) {
            Toast.makeText(this, "No transactions found", Toast.LENGTH_SHORT).show();
        }
    }

    /** Helper: first non-empty string */
    private String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return "Unknown";
    }

    /** Helper: convert Firebase amount to int */
    private int getAmount(Object amtObj) {
        int amount = 0;
        if (amtObj instanceof Long) amount = ((Long) amtObj).intValue();
        else if (amtObj instanceof Double) amount = ((Double) amtObj).intValue();
        else if (amtObj instanceof String) {
            try { amount = Integer.parseInt((String) amtObj); } catch (Exception ignored) {}
        }
        return amount;
    }
}
