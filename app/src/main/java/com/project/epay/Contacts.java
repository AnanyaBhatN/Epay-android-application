package com.project.epay;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    private RecyclerView recyclerContacts;
    private ContactsAdapter adapter;
    private List<Contact> contactList;         // All contacts
    private List<Contact> filteredList;        // Filtered contacts
    private DatabaseReference databaseRef;
    private AutoCompleteTextView autoSelectContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        recyclerContacts = findViewById(R.id.recycler_contacts);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));

        autoSelectContact = findViewById(R.id.auto_select_contact);

        contactList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new ContactsAdapter(this, filteredList);
        recyclerContacts.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        fetchContacts();

        setupSearchFilter();
    }

    private void setupSearchFilter() {
        autoSelectContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                filterContacts(query.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
                if (rootSnapshot.hasChild("contacts") && rootSnapshot.child("contacts").hasChild("user123")) {
                    DataSnapshot userContacts = rootSnapshot.child("contacts").child("user123");

                    contactList.clear();

                    for (DataSnapshot contactSnapshot : userContacts.getChildren()) {
                        String name = contactSnapshot.child("contactName").getValue(String.class);
                        String phone = contactSnapshot.child("contactPhone").getValue(String.class);

                        if (name != null && phone != null) {
                            String initial = name.substring(0, 1).toUpperCase();
                            int color = 0xFF81C784;
                            contactList.add(new Contact(name, initial, color, phone));
                        }
                    }

                    // ✅ Sort contacts alphabetically by name
                    contactList.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));

                    filteredList.clear();
                    filteredList.addAll(contactList);

                    adapter.notifyDataSetChanged();

                    Log.d("FirebaseDebug", "Loaded " + contactList.size() + " contacts (sorted)");
                } else {
                    Log.d("FirebaseDebug", "contacts/user123 path not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDebug", "Error reading database", error.toException());
            }
        });
    }
}
