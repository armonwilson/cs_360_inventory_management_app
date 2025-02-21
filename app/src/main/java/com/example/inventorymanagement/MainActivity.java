package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        Button buttonAddItem = findViewById(R.id.buttonAddItem);

        // Initialize the inventory list (replace with database later)
        List<Item> inventoryList = new ArrayList<>();
        inventoryList.add(new Item("Item 1", "Description 1", 10));
        inventoryList.add(new Item("Item 2", "Description 2", 20));
        inventoryList.add(new Item("Item 3", "Description 3", 30));

        // Set up the RecyclerView
        recyclerViewInventory.setLayoutManager(new LinearLayoutManager(this));
        InventoryAdapter inventoryAdapter = new InventoryAdapter(inventoryList);
        recyclerViewInventory.setAdapter(inventoryAdapter);

        buttonAddItem.setOnClickListener(v -> {
            // Handle add item button click
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });
    }
}