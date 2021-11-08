package com.chitranjank.apps.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chitranjank.apps.contacts.Adapters.ContactContents;
import com.chitranjank.apps.contacts.Adapters.ListAdapters;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CallPerson extends AppCompatActivity {
    List<ContactContents> list, searchList;
    ListView listView;
    ListAdapters adapter;

    EditText editText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_person);

        listView = findViewById(R.id.call_hist);
        editText = findViewById(R.id.call_n);

        ImageView callBtn = findViewById(R.id.call_to_number);

        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Call History");

        list = new ArrayList<>();
        searchList = new ArrayList<>();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList.clear();
                for (ContactContents c : list) {
                    if (c.getpNumber().contains(s.toString().toLowerCase()) || c.getpName().contains(s.toString().toLowerCase())) {
                        searchList.add(c);
                    }
                }
                adapter = new ListAdapters(searchList, CallPerson.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_CALL_LOG)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        get_call_history();
                        adapter = new ListAdapters(list, CallPerson.this);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(CallPerson.this, "Permission denied, please allow!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_dialog(position);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().trim().isEmpty()) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + editText.getText().toString().trim()));
                    startActivity(intentCall);
                    editText.setText("");
                } else {
                    Toast.makeText(CallPerson.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void get_call_history() {

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
            Date callDayTime = new Date(Long.parseLong(callDate));
            int callDuration = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)));

            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            if (number.length() > 9) {
                list.add(new ContactContents(null, name, number, dir, callDayTime.toString(), createTimer(callDuration)));
            }
        }
        cursor.close();
    }

    public String createTimer(int duration) {

        String timerDuration = "";
        int min = duration / 60;
        int sec = min % 60;

        timerDuration += min + ":";

        timerDuration += sec;

        return timerDuration;
    }

    private void show_dialog(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.promt_dialog);
        dialog.show();

        Button call, sms, delete;
        TextView number;

        call = dialog.findViewById(R.id.call_d);
        sms = dialog.findViewById(R.id.sms_d);
        number = dialog.findViewById(R.id.number_d);
        delete = dialog.findViewById(R.id.delete_d);

        if (editText.getText().toString().trim().isEmpty()) {
            number.setText(list.get(position).getpNumber());
        } else {
            number.setText(searchList.get(position).getpNumber());
        }

        String numberSMS = number.getText().toString().trim();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!numberSMS.trim().isEmpty()) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + numberSMS));
                    startActivity(intentCall);
                } else {
                    Toast.makeText(CallPerson.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(CallPerson.this, SendText.class)
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

    public void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String[] args = new String[]{String.valueOf(getContactID(contactHelper, number))};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(CallPerson.this, "Contact deleted", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}