package com.example.inventorymanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * The primary activity of the Inventory Management application.
 * <p>
 * This activity serves as the main entry point for the user and provides the following key functionalities:
 * <ul>
 *   <li><b>Displays Inventory List:</b> Shows a categorized list of all inventory items fetched from the database in a RecyclerView.</li>
 *   <li><b>Item Details:</b> Enables users to view detailed information about a specific item by tapping on it in the list.</li>
 *   <li><b>Add Item:</b> Allows users to navigate to the Add/Edit Item screen to create new inventory items.</li>
 *   <li><b>Database Interaction:</b>  Manages the interaction with the local database to retrieve inventory data.</li>
 *   <li><b>SMS Permission Handling:</b> Checks and requests the SEND_SMS permission and redirects to SMSPermissionActivity if permission is not granted.</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewInventory;
    private InventoryAdapter inventoryAdapter;
    private DatabaseHelper dbHelper;

    /**
     * Called when the activity is first created. This method initializes the activity's UI,
     * loads inventory data, sets up the RecyclerView, and configures event listeners.
     * <p>
     *   <b>Initialization Steps:</b>
     * </p>
     * <ol>
     *   <li><b>Inflate UI:</b> Sets the activity's layout using {@link #setContentView(int)}.</li>
     *   <li><b>Find UI Elements:</b> Locates and initializes the {@link RecyclerView} for displaying inventory and the "Add Item" {@link Button} using {@link #findViewById(int)}.</li>
     *   <li><b>Load Inventory Data:</b> Retrieves the inventory data from the database using a {@link DatabaseHelper}.</li>
     *   <li><b>Configure RecyclerView:</b> Sets up the {@link RecyclerView} with a {@link GridLayoutManager} to display items in a grid and an {@link InventoryAdapter} to populate the grid with inventory data.</li>
     *   <li><b>Set Item Click Listener:</b> Attaches a click listener to the {@link InventoryAdapter}. When an item is clicked, it navigates to the {@link ItemDetailsActivity}, passing the selected {@link Item} as an extra.</li>
     *   <li><b>Set "Add Item" Button Click Listener:</b>  Attaches a click listener to the "Add Item" button. Clicking the button navigates to the {@link AddEditItemActivity}.</li>
     *   <li><b>Check SMS Permission:</b> Verifies if the {@link Manifest.permission#SEND_SMS} permission is granted. If not, it redirects the user to the {@link SMSPermissionActivity} to request the permission.</li>
     *   <li><b>Database Resource Management:</b> Uses try-with-resources to ensure the {@link DatabaseHelper} is automatically closed, even if errors occur.</li>
     * </ol>
     * <p>
     *   <b>Error Handling:</b> Any exceptions that occur during database operations are caught and logged using {@link Log}.
     * </p>
     * <p>
     *   This method is the primary setup point for the activity, ensuring the UI is correctly displayed, data is loaded, and user interactions are handled.
     * </p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           <b>Note */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        Button buttonAddItem = findViewById(R.id.buttonAddItem);
        dbHelper = new DatabaseHelper(this);

        // Set up the RecyclerView
        setupRecyclerView();

        buttonAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });

        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, launch SMSPermissionActivity
            Intent intent = new Intent(this, SMSPermissionActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data when the activity is resumed
        refreshInventoryList();
    }
    private void setupRecyclerView() {
        // Set up the RecyclerView with GridLayoutManager
        int numberOfColumns = 2;
        recyclerViewInventory.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        inventoryAdapter = new InventoryAdapter(getAllInventoryItems());
        recyclerViewInventory.setAdapter(inventoryAdapter);

        // Handle item clicks
        inventoryAdapter.setOnItemClickListener(item -> {
            Log.d("MainActivity", "Item clicked: " + item.getName());
            Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
            intent.putExtra("ITEM", item);
            Log.d("MainActivity", "Starting ItemDetailsActivity");
            startActivity(intent);
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void refreshInventoryList() {
        // Fetch inventory data from the database
        List<Item> inventoryList = getAllInventoryItems();

        // Update the adapter's data directly
        if (inventoryAdapter != null) {
            inventoryAdapter.setInventoryList(inventoryList); // Add this method to InventoryAdapter
            inventoryAdapter.notifyDataSetChanged();
        }
    }
    private List<Item> getAllInventoryItems() {
        try {
            return dbHelper.getAllInventoryItems();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error during database operation", e);
            return null;
        }
    }
}