package com.chitranjank.apps.contacts;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chitranjank.apps.contacts.Adapters.ContactContents;
import com.chitranjank.apps.contacts.Adapters.ListAdapters;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ExtendedFloatingActionButton aFab;
    FloatingActionButton addP, callP, sendText;

    List<ContactContents> list, searchList;
    ListAdapters adapter;
    boolean isFabShow;
    ListView listView;
    EditText editText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.contacts_listview);

        list = new ArrayList<>();
        searchList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        editText = findViewById(R.id.search_bar_edt);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList.clear();
                for (ContactContents c : list) {
                    if (c.getpNumber().contains(s.toString()) || c.getpName().toLowerCase().contains(s.toString().toLowerCase())) {
                        searchList.add(c);
                    }
                }
                adapter = new ListAdapters(searchList, MainActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        aFab = findViewById(R.id.e_fab);
        addP = findViewById(R.id.add_person);
        callP = findViewById(R.id.add_call);
        sendText = findViewById(R.id.send_text);

        addP.hide();
        callP.hide();
        sendText.hide();

        aFab.shrink();

        isFabShow = false;

        aFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFabShow) {
                    addP.show();
                    callP.show();
                    sendText.show();
                    isFabShow = true;
                } else {
                    addP.hide();
                    callP.hide();
                    sendText.hide();
                    isFabShow = false;
                }
            }
        });

        addP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddContacts.class));
            }
        });
        callP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CallPerson.class));
            }
        });
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendText.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_dialog(position);
            }
        });

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.WRITE_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            get_contacts_from_phone();
                            adapter = new ListAdapters(list, MainActivity.this);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, "Please allow all permissions!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    private void show_dialog(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.promt_dialog);
        dialog.show();

        Button call, sms, delete;
        TextView number;

        call = dialog.findViewById(R.id.call_d);
        sms = dialog.findViewById(R.id.sms_d);
        delete = dialog.findViewById(R.id.delete_d);
        number = dialog.findViewById(R.id.number_d);

        String numberSMS;

        if (editText.getText().toString().trim().isEmpty()) {
            numberSMS = list.get(position).getpNumber();
        } else {
            numberSMS = searchList.get(position).getpNumber();
        }

        number.setText(numberSMS);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!list.get(position).getpNumber().trim().isEmpty()) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + numberSMS));
                    startActivity(intentCall);
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, SendText.class)
                        .putExtra("n", numberSMS));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ContentResolver contentResolver = getContentResolver();
                deleteContact(contentResolver, numberSMS);
            }
        });

    }

    private void get_contacts_from_phone() {

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            list.add(new ContactContents(id, name, number, null, null, null));
        }
        cursor.close();

    }

    public void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String[] args = new String[]{String.valueOf(getContactID(contactHelper, number))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(MainActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        try (Cursor cursor = contactHelper.query(contactUri, projection, null, null, null)) {
            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}