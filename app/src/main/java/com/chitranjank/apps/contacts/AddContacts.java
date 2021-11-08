package com.chitranjank.apps.contacts;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class AddContacts extends AppCompatActivity {
    private EditText nameEDT, phoneEDT, phoneEDT2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add New Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        nameEDT = findViewById(R.id.add_name);
        phoneEDT = findViewById(R.id.add_number);
        phoneEDT2 = findViewById(R.id.add_number2);

        Button saveContact = findViewById(R.id.save_done);

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number1 = phoneEDT.getText().toString().trim();
                String number2 = phoneEDT2.getText().toString().trim();
                String name = nameEDT.getText().toString();

                if (name.trim().isEmpty()) {
                    name = "Unknown";
                }

                if (!number1.isEmpty() || !number2.isEmpty()) {
                    addContact(number1, number2, name);
                    nameEDT.setText("");
                    phoneEDT.setText("");
                    phoneEDT2.setText("");
                } else {
                    Toast.makeText(AddContacts.this, "Number field can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addContact(String mobile1, String mobile2, String name) {
        ArrayList<ContentProviderOperation> contact = new ArrayList<ContentProviderOperation>();
        contact.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // names
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .build());
        // Contact No Mobile
        if (!mobile1.trim().isEmpty()) {
            contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile1)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        if (!mobile2.trim().isEmpty()) {
            contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile2)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, contact);
            Toast.makeText(AddContacts.this, "Contact Saved", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(AddContacts.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
        super.onBackPressed();
    }
}