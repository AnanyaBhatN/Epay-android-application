////package com.example.transactionhistory;
////
////import android.os.Bundle;
////import android.widget.ImageView;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class TransactionHistoryActivity extends AppCompatActivity {
////
////    private RecyclerView rvTransactions;
////    private TransactionAdapter adapter;
////    private List<Transaction> transactionList;
////    private ImageView ivBack;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_transaction_history);
////
////        rvTransactions = findViewById(R.id.rvTransactions);
////        ivBack = findViewById(R.id.ivBack);
////
////        transactionList = new ArrayList<>();
////        adapter = new TransactionAdapter(this, transactionList);
////
////        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
////        rvTransactions.setAdapter(adapter);
////
////        ivBack.setOnClickListener(v -> finish());
////
////        fetchTransactions();
////    }
////
////    private void fetchTransactions() {
////        DatabaseReference database = FirebaseDatabase.getInstance("https://epay-f3cf2-default-rtdb.asia-southeast1.firebasedatabase.app")
////                .getReference("recharges");
////
////        database.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                transactionList.clear();
////                for (DataSnapshot child : snapshot.getChildren()) {
////                    String id = child.getKey();
////                    String amountStr = child.child("amount").getValue(String.class);
////                    String mobileNumber = child.child("mobileNumber").getValue(String.class);
////                    String operator = child.child("operator").getValue(String.class);
////                    String dateTime = child.child("dateTime").getValue(String.class);
////
////                    int amount = 0;
////                    try {
////                        amount = Integer.parseInt(amountStr);
////                    } catch (Exception e) {}
////
////                    Transaction transaction = new Transaction(
////                            id,
////                            Transaction.Type.RECHARGE,
////                            operator != null ? operator : mobileNumber,
////                            amount,
////                            dateTime,
////                            "Success",
////                            "Mobile Recharge"
////                    );
////
////                    transactionList.add(transaction);
////                }
////                adapter.notifyDataSetChanged();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////            }
////        });
////    }
////}
//
//package com.project.epay;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//public class TransactionHistoryActivity extends AppCompatActivity {
//
//    private RecyclerView rvTransactions;
//    private TransactionAdapter adapter;
//    private List<Transaction> transactionList;
//    private ImageView ivBack;
//    private FirebaseUser currentUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_transaction_history);
//
//        rvTransactions = findViewById(R.id.rvTransactions);
//        ivBack = findViewById(R.id.ivBack);
//
//        transactionList = new ArrayList<>();
//        adapter = new TransactionAdapter(this, transactionList);
//
//        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
//        rvTransactions.setAdapter(adapter);
//
//        ivBack.setOnClickListener(v -> finish());
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            String emailKey = currentUser.getEmail().replace(".", "_");
//            fetchAllTransactions(emailKey);
//        } else {
//            // for testing
//            fetchAllTransactions("kotainprathiksha@gmail_com");
//        }
//
//    }
//
//    private void fetchAllTransactions(String uid) {
//        // 1️⃣ Fetch Recharge transactions
//        DatabaseReference rechargeRef = FirebaseDatabase.getInstance()
//                .getReference("recharges").child(uid);
//
//        rechargeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    String id = child.getKey();
//                    String amountStr = child.child("amount").getValue(String.class);
//                    String mobileNumber = child.child("mobileNumber").getValue(String.class);
//                    String operator = child.child("operator").getValue(String.class);
//                    String dateTime = child.child("dateTime").getValue(String.class);
//
//                    int amount = 0;
//                    try { amount = Integer.parseInt(amountStr); } catch (Exception e) {}
//
//                    Transaction transaction = new Transaction(
//                            id,
//                            Transaction.Type.RECHARGE,
//                            operator != null ? operator : mobileNumber,
//                            amount,
//                            dateTime,
//                            "Success",
//                            "Mobile Recharge"
//                    );
//                    transactionList.add(transaction);
//                }
//                fetchSendMoneyTransactions(uid); // After recharge, fetch Send Money
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private void fetchSendMoneyTransactions(String uid) {
//        DatabaseReference sendMoneyRef = FirebaseDatabase.getInstance()
//                .getReference("SendMoney").child(uid);
//
//        sendMoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    String id = child.getKey();
//                    String amountStr = child.child("amount").getValue(String.class);
//                    String toName = child.child("toName").getValue(String.class);
//                    String toPhone = child.child("toPhone").getValue(String.class);
//                    String dateTime = child.child("dateTime").getValue(String.class);
//                    String typeStr = child.child("type").getValue(String.class);
//
//                    int amount = 0;
//                    try { amount = Integer.parseInt(amountStr); } catch (Exception e) {}
//
//                    Transaction transaction = new Transaction(
//                            id,
//                            Transaction.Type.SENDMONEY,
//                            toName != null ? toName : toPhone,
//                            amount,
//                            dateTime,
//                            "Success",
//                            typeStr != null ? typeStr : "UPI Payment"
//                    );
//                    transactionList.add(transaction);
//                }
//
//                // Sort by dateTime descending
//                Collections.sort(transactionList, new Comparator<Transaction>() {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    @Override
//                    public int compare(Transaction t1, Transaction t2) {
//                        try {
//                            Date d1 = sdf.parse(t1.getDate() + " " + t1.getTime());
//                            Date d2 = sdf.parse(t2.getDate() + " " + t2.getTime());
//                            return d2.compareTo(d1); // latest first
//                        } catch (ParseException e) {
//                            return 0;
//                        }
//                    }
//                });
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//}
//


package com.project.epay;

import android.os.Bundle;
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

public class TransactionHistoryActivity extends AppCompatActivity {

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

        // Fetch unified Transactions node
        fetchAllTransactions(emailKey);
    }

    private void fetchAllTransactions(String emailKey) {
        DatabaseReference txnRef = FirebaseDatabase.getInstance()
                .getReference("Transactions")
                .child(emailKey);

        txnRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String typeStr = ds.child("type").getValue(String.class);
                    String amountStr = ds.child("amount").getValue(String.class);
                    String dateTime = ds.child("dateTime").getValue(String.class);

                    String toName = ds.child("toName").getValue(String.class);
                    String toMobile = ds.child("toMobile").getValue(String.class);
                    String fromName = ds.child("fromName").getValue(String.class);
                    String fromEmail = ds.child("fromEmail").getValue(String.class);

                    int amount = 0;
                    try { amount = Integer.parseInt(amountStr); } catch (Exception e) {}

                    Transaction.Type type;
                    if ("Received".equalsIgnoreCase(typeStr)) {
                        type = Transaction.Type.RECEIVED;
                    } else if ("Sent".equalsIgnoreCase(typeStr)) {
                        type = Transaction.Type.PAID;
                    } else {
                        type = Transaction.Type.OTHER;
                    }

                    String counterpart;
                    if (type == Transaction.Type.RECEIVED) {
                        counterpart = (fromName != null && !fromName.isEmpty())
                                ? fromName
                                : (fromEmail != null ? fromEmail : "Unknown");
                    } else {
                        counterpart = (toName != null && !toName.isEmpty())
                                ? toName
                                : (toMobile != null ? toMobile : "Unknown");
                    }

                    Transaction txn = new Transaction(
                            id,
                            type,
                            counterpart,
                            amount,
                            dateTime,
                            "Success",
                            "UPI Payment"
                    );

                    transactionList.add(txn);
                }

                // Sort transactions by date (descending)
                Collections.sort(transactionList, new Comparator<Transaction>() {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    @Override
                    public int compare(Transaction t1, Transaction t2) {
                        try {
                            Date d1 = sdf.parse(t1.getDate() + " " + t1.getTime());
                            Date d2 = sdf.parse(t2.getDate() + " " + t2.getTime());
                            return d2.compareTo(d1);
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });

                adapter.notifyDataSetChanged();

                if (transactionList.isEmpty()) {
                    Toast.makeText(TransactionHistoryActivity.this, "No transactions found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

















