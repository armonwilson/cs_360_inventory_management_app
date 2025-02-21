package com.example.inventorymanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        findViewById(R.id.editTextItemName);
        findViewById(R.id.editTextItemDescription);
        findViewById(R.id.editTextItemQuantity);
        Button buttonSaveItem = findViewById(R.id.buttonSaveItem);

        buttonSaveItem.setOnClickListener(v -> {
            // Handle saving the item
        });
    }
}