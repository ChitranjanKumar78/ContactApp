package com.chitranjank.apps.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chitranjank.apps.contacts.Adapters.ListAdapters;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

public class SendText extends AppCompatActivity {
    private EditText receiverNumber, smsText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Send SMS");

        receiverNumber = findViewById(R.id.sms_number);
        smsText = findViewById(R.id.sms_text);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Send SMS");

        Intent intent = getIntent();

        receiverNumber.setText(intent.getStringExtra("n"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sms_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.send_text_sms) {
            send_sms();
            return true;
        }
        return false;
    }

    private void send_sms() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (!receiverNumber.getText().toString().trim().isEmpty()) {
                            String no = receiverNumber.getText().toString();
                            String msg = smsText.getText().toString();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(no, null, msg, pi, null);
                            Toast.makeText(SendText.this, "SMS sent", Toast.LENGTH_SHORT).show();

                            receiverNumber.setText("");
                            smsText.setText("");

                        } else {
                            Toast.makeText(SendText.this, "Please enter receiver number!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(SendText.this, "Permission denied, please allow!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}