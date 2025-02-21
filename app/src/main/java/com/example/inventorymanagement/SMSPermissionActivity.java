package com.example.inventorymanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inventorymanagement.R;

public class SMSPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        Button buttonGrantPermission = findViewById(R.id.buttonGrantPermission);
        Button buttonDenyPermission = findViewById(R.id.buttonDenyPermission);

        buttonGrantPermission.setOnClickListener(v -> requestSMSPermission());

        buttonDenyPermission.setOnClickListener(v -> {
            Toast.makeText(SMSPermissionActivity.this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            // Continue without SMS functionality
            finish(); // Or navigate to the next screen
        });
    }

    private void requestSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            Toast.makeText(this, "SMS permission already granted", Toast.LENGTH_SHORT).show();
            finish(); // Or navigate to the next screen
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
                // Proceed with SMS functionality
                finish(); // Or navigate to the next screen
            } else {
                // Permission denied
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
                // Continue without SMS functionality
                finish(); // Or navigate to the next screen
            }
        }
    }
}