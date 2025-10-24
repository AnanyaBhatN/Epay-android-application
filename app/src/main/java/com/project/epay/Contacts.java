package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    private RecyclerView recyclerContacts;
    private ContactsAdapter adapter;
    private List<Contact> contactList;
    private List<Contact> filteredList;
    private AutoCompleteTextView autoSelectContact;
    private String email, emailKey; // actual email & sanitized key
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        recyclerContacts = findViewById(R.id.recycler_contacts);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        autoSelectContact = findViewById(R.id.auto_select_contact);

        // Get logged-in user info from intent
        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        if (email == null || email.isEmpty() || emailKey == null || emailKey.isEmpty()) {
            finish();
            return;
        }

        contactList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new ContactsAdapter(this, filteredList, (contact) -> {
            // On contact click → open AmountActivity with both email and emailKey
            Intent intent = new Intent(Contacts.this, AmountActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("phone", contact.getPhone());
            intent.putExtra("email", email);       // pass actual email
            intent.putExtra("emailKey", emailKey); // pass sanitized emailKey
            startActivity(intent);
        });

        recyclerContacts.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        fetchContacts();
        setupSearchFilter();

        // Home button
        ImageView btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(Contacts.this, DashboardActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
            finish();
        });
    }

    private void setupSearchFilter() {
        autoSelectContact.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                filterContacts(query.toString());
            }
        });
    }

    private void filterContacts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(contact);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchContacts() {
        // Fetch contacts from user123
        databaseRef.child("contacts").child("user123")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        contactList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                                String name = contactSnapshot.child("contactName").getValue(String.class);
                                String phone = contactSnapshot.child("contactPhone").getValue(String.class);
                                if (name != null && phone != null) {
                                    String initial = name.substring(0, 1).toUpperCase();
                                    contactList.add(new Contact(name, initial, 0xFF81C784, phone));
                                }
                            }
                            contactList.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
                            filteredList.clear();
                            filteredList.addAll(contactList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("CONTACTS_DEBUG", "No contacts found under user123!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CONTACTS_DEBUG", "Error reading database", error.toException());
                    }
                });
    }
}
