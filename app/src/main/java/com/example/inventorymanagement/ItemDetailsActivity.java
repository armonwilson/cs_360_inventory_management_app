package com.example.inventorymanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        TextView textViewItemName = findViewById(R.id.textViewItemName);
        TextView textViewItemDescription = findViewById(R.id.textViewItemDescription);
        TextView textViewItemQuantity = findViewById(R.id.textViewItemQuantity);
        Button buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        Button buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonRemoveItem = findViewById(R.id.buttonRemoveItem);

        //Set some initial data (replace with actual data later)
        textViewItemName.setText("Example Item");
        textViewItemDescription.setText("This is an example item description.");
        textViewItemQuantity.setText("Quantity: 10");

        buttonIncreaseQuantity.setOnClickListener(v -> {
            // Handle increasing quantity
        });

        buttonDecreaseQuantity.setOnClickListener(v -> {
            // Handle decreasing quantity
        });

        buttonRemoveItem.setOnClickListener(v -> {
            // Handle removing the item
        });
    }
}