package com.project.epay;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
        fetchRechargeTransactions(emailKey);
    }

    /** 🔹 Step 1: Fetch Recharges */
    private void fetchRechargeTransactions(String emailKey) {
        DatabaseReference rechargeRef = FirebaseDatabase.getInstance()
                .getReference("Recharges")
                .child(emailKey);

        rechargeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        if (!ds.child("operator").exists()) continue;

                        String id = ds.getKey();
                        String amountStr = ds.child("amount").getValue(String.class);
                        String mobileNumber = ds.child("mobileNumber").getValue(String.class);
                        String operator = ds.child("operator").getValue(String.class);
                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);

                        Log.d("TXN_RECHARGE", "id=" + id +
                                ", amount=" + amountStr +
                                ", mobile=" + mobileNumber +
                                ", operator=" + operator +
                                ", dateTime=" + dateTime +
                                ", status=" + status
                        );

                        if (id == null || amountStr == null || dateTime == null) {
                            Log.e("TXN_SKIP", "Skipping invalid recharge: " + id);
                            continue;
                        }

                        int amount;
                        try { amount = Integer.parseInt(amountStr); } catch (Exception e) { amount = 0; }

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

                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing recharge", e);
                    }
                }

                fetchSendMoneyTransactions(emailKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Failed to fetch recharges: " + error.getMessage());
                Toast.makeText(TransactionHistory.this, "Failed to load recharges", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🔹 Step 2: Fetch SendMoney */
    private void fetchSendMoneyTransactions(String emailKey) {
        DatabaseReference sendMoneyRef = FirebaseDatabase.getInstance()
                .getReference("SendMoney")
                .child(emailKey);

        sendMoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String id = ds.getKey();
                        String amountStr = ds.child("amount").getValue(String.class);
                        String toName = ds.child("toName").getValue(String.class);
                        String toPhone = ds.child("toPhone").getValue(String.class);
                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String typeStr = ds.child("type").getValue(String.class);

                        Log.d("TXN_SEND", "id=" + id +
                                ", amount=" + amountStr +
                                ", toName=" + toName +
                                ", toPhone=" + toPhone +
                                ", dateTime=" + dateTime +
                                ", type=" + typeStr
                        );

                        if (id == null || amountStr == null || dateTime == null) {
                            Log.e("TXN_SKIP", "Skipping invalid SendMoney: " + id);
                            continue;
                        }

                        int amount;
                        try { amount = Integer.parseInt(amountStr); } catch (Exception e) { amount = 0; }

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

                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing SendMoney", e);
                    }
                }

                fetchTransactionsModule(emailKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Failed to fetch SendMoney: " + error.getMessage());
                Toast.makeText(TransactionHistory.this, "Failed to load sent money", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🔹 Step 3: Fetch transactions table */
    private void fetchTransactionsModule(String emailKey) {
        DatabaseReference txnRef = FirebaseDatabase.getInstance()
                .getReference("transactions")
                .child(emailKey);

        txnRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String id = ds.getKey();
                        String typeStr = ds.child("type").getValue(String.class);
                        String amountStr = ds.child("amount").getValue(String.class);
                        String dateTime = ds.child("dateTime").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);
                        String recipientName = ds.child("recipientName").getValue(String.class);
                        String recipientUpi = ds.child("recipientUpi").getValue(String.class);

                        Log.d("TXN_FETCH", "id=" + id +
                                ", type=" + typeStr +
                                ", amount=" + amountStr +
                                ", dateTime=" + dateTime +
                                ", status=" + status +
                                ", recipientName=" + recipientName +
                                ", recipientUpi=" + recipientUpi
                        );

                        if (id == null || amountStr == null || dateTime == null) {
                            Log.e("TXN_SKIP", "Skipping invalid transaction: " + id);
                            continue;
                        }

                        int amount;
                        try { amount = Integer.parseInt(amountStr); } catch (Exception e) { amount = 0; }

                        Transaction.Type type;
                        String counterpart;

                        if ("Sent".equalsIgnoreCase(typeStr)) {
                            type = Transaction.Type.PAID;
                            counterpart = firstNonEmpty(recipientName, recipientUpi, "Unknown");
                        } else if ("Received".equalsIgnoreCase(typeStr)) {
                            type = Transaction.Type.RECEIVED;
                            // Use your own emailKey as sender if senderName not available
                            counterpart = firstNonEmpty(ds.child("senderName").getValue(String.class),
                                    ds.child("senderUpi").getValue(String.class),
                                    "Unknown");
                        } else {
                            type = Transaction.Type.OTHER;
                            counterpart = "Unknown";
                        }

                        transactionList.add(new Transaction(
                                id,
                                type,
                                counterpart,
                                amount,
                                dateTime,
                                status != null ? status : "Success",
                                "UPI Payment"
                        ));

                    } catch (Exception e) {
                        Log.e("TXN_ERROR", "Error parsing transaction", e);
                    }
                }

                sortTransactions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TXN_ERROR", "Firebase fetch cancelled: " + error.getMessage());
                Toast.makeText(TransactionHistory.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🔹 Step 4: Sort transactions by latest first */
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

    /** ✅ Helper: first non-empty string */
    private String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return "Unknown";
    }
}
