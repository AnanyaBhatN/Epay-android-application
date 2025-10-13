package com.project.epay;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    private RecyclerView recyclerContacts;
    private ContactsAdapter adapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // ensure layout name matches

        // Initialize RecyclerView
        recyclerContacts = findViewById(R.id.recycler_contacts);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));

        // Create sample contacts
        contactList = new ArrayList<>();
        contactList.add(new Contact("Alice", "A", 0xFFE57373, "9876543210"));
        contactList.add(new Contact("Bob", "B", 0xFF64B5F6, "9123456780"));
        contactList.add(new Contact("Charlie", "C", 0xFF81C784, "9988776655"));
        contactList.add(new Contact("David", "D", 0xFFFFB74D, "9012345678"));
        contactList.add(new Contact("Eve", "E", 0xFFBA68C8, "9876501234"));
        contactList.add(new Contact("Frank", "F", 0xFFFF8A65, "9123409876"));
        contactList.add(new Contact("Grace", "G", 0xFFA1887F, "9988112233"));
        contactList.add(new Contact("Hank", "H", 0xFF4DB6AC, "9001122334"));
        contactList.add(new Contact("Ivy", "I", 0xFFDCE775, "9112233445"));
        contactList.add(new Contact("Jack", "J", 0xFF90A4AE, "9223344556"));

        // Initialize adapter
        adapter = new ContactsAdapter(this, contactList);
        recyclerContacts.setAdapter(adapter);
    }
}
