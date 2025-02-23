package com.example.inventorymanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** @noinspection FieldCanBeLocal*/
public class ItemDetailsActivity extends AppCompatActivity {

    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private TextView textViewItemQuantity;
    private Button buttonIncreaseQuantity;
    private Button buttonDecreaseQuantity;
    private Button buttonRemoveItem;
    private DatabaseHelper dbHelper;
    private Item item;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static final String DEFAULT_PHONE_NUMBER = "+15551234567"; // Consider making this configurable
    private static final String TAG = "ItemDetailsActivity"; // Tag for logging


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Initialize UI elements
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewItemQuantity = findViewById(R.id.textViewItemQuantity);
        buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        buttonRemoveItem = findViewById(R.id.buttonDeleteItem);
        dbHelper = new DatabaseHelper(this);

        // Get the Item object from the intent
        item = getIntent().getParcelableExtra("ITEM");

        if (item != null) {
            // Update UI elements with item details
            textViewItemName.setText(item.getName());
            textViewItemDescription.setText(item.getDescription());
            textViewItemQuantity.setText("Quantity: " + item.getQuantity());

            buttonIncreaseQuantity.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                dbHelper.updateInventoryItem(item);
                textViewItemQuantity.setText("Quantity: " + item.getQuantity());
                checkQuantityAndSendSMS(item);
            });

            buttonDecreaseQuantity.setOnClickListener(v -> {
                if (item.getQuantity() > 0) {
                    item.setQuantity(item.getQuantity() - 1);
                    dbHelper.updateInventoryItem(item);
                    textViewItemQuantity.setText("Quantity: " + item.getQuantity());
                    checkQuantityAndSendSMS(item);
                }
            });

            buttonRemoveItem.setOnClickListener(v -> {
                dbHelper.deleteInventoryItem(item.getId());
                finish(); // Close the activity after deleting
            });
        } else {
            // Handle the case where the item is not found
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkQuantityAndSendSMS(Item item) {
        if (item.getQuantity() < 10) {
            sendSMS(item);
        }
    }

    private void sendSMS(Item item) {
        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestSMSPermission();
        } else {
            // Permission is granted, send SMS
            try {
                SmsManager smsManager = SmsManager.getDefault();
                // Replace with the actual phone number
                String message = "Your item " + item.getName() + " is below 10. Current quantity: " + item.getQuantity();
                smsManager.sendTextMessage(DEFAULT_PHONE_NUMBER, null, message, null, null);
                Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to send SMS", e); // Corrected logging
            }
        }
    }

    private void requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            // Show an explanation to the user *asynchronously*
            new AlertDialog.Builder(this)
                    .setTitle("SMS Permission Needed")
                    .setMessage("This app needs the SMS permission to send you low stock alerts.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(ItemDetailsActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                sendSMS(item);
            } else {
                // Permission denied
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_LONG).show();
                // Optionally, show a dialog to explain why the permission is needed and how to enable it in settings
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("SMS Permission Denied")
                .setMessage("SMS permission is required for low stock alerts. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}