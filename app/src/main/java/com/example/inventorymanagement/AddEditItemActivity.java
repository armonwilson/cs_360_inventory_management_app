package com.example.inventorymanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Activity for adding new items or editing existing items in the inventory.
 * <p>
 * This activity provides a user interface for entering the name, description, and quantity of an inventory item.
 * It allows the user to save the entered information to the database.
 * Additionally, it checks for low inventory levels and sends an SMS notification if any item's quantity
 * falls below a predefined threshold.
 * </p>
 */
public class AddEditItemActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private EditText editTextItemQuantity;

    /**
     * Checks if the inventory is low based on a predefined threshold.
     * <p>
     * This method iterates through all inventory items and checks if any item's
     * quantity is below the defined low inventory threshold. The threshold is
     * currently set to 10.
     *
     * @return {@code true} if at least one item's quantity is below the low
     *         inventory threshold, {@code false} otherwise.
     *
     * @implNote This method utilizes a `DatabaseHelper` to retrieve the list of
     *           inventory items. It employs a try-with-resources block to ensure
     *           proper closure of the `DatabaseHelper` connection.
     */
    private boolean isInventoryLow() {
        int lowInventoryThreshold = 10; // Set your threshold here

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            List<Item> items = dbHelper.getAllInventoryItems();
            for (Item item: items) {
                if (item.getQuantity() < lowInventoryThreshold) {
                    return true; // If any item is below the threshold
                }
            }
        }
        return false; // If no items are below the threshold
    }

    /**
     * Sends an SMS notification to a predefined phone number.
     * <p>
     * This method utilizes the SmsManager to send a text message to a specific phone number.
     * It also displays a Toast message indicating whether the SMS was sent successfully or if an error occurred.
     *
     * @param message The content of the SMS message to be sent.
     *
     * @throws SecurityException if the app does not have the SEND_SMS permission.
     * @throws IllegalArgumentException if the message is null or empty.
     *
     * <p>
     * Note: This method uses a hardcoded phone number ("1234567890"). In a real application, you should replace this with a dynamically retrieved or user-configurable phone number.
     * Also, ensure that your application has the SEND_SMS permission declared in the AndroidManifest.xml file:
     *  <uses-permission android:name="android.permission.SEND_SMS" />
     *
     *  You may also require runtime permission request for Android versions above Marshmallow.
     */
    private void sendSMSNotification(String message) {
        String phoneNumber = "1234567890"; // Replace with dynamic phone number lookup

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS notification sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error sending SMS notification", Toast.LENGTH_SHORT).show();
            Log.e("SMS Error", "Error sending SMS notification", e);
        }
    }

    /**
     -     * Called when the activity is first created. This method performs the following setup:
     -     *
     -     * <ol>
     -     *   <li><b>Initializes the activity's layout:</b> Sets the content view to {@code R.layout.activity_add_edit_item}.</li>
     -     *   <li><b>Instantiates the DatabaseHelper:</b> Creates an instance of {@link DatabaseHelper} to interact with the database.</li>
     -     *   <li><b>Initializes UI Elements:</b> Finds and assigns references to the {@link EditText} fields for item name,
     -     *       description, and quantity, as well as the {@link Button} for saving the item.</li>
     -     *   <li><b>Sets up the Save Button Listener:</b> Configures an {@link android.view.View.OnClickListener} for the save button.
     -     *       When clicked, it performs the following:
     -     *     <ul>
     -     *       <li>Retrieves the text from the input fields.</li>
     -     *       <li>Validates that all fields are filled. If not, displays a {@link Toast} message.</li>
     -     *       <li>Parses the quantity string to an integer.</li>
     -     *       <li>Creates a new {@link Item} object.</li>
     -     *       <li>Inserts the new item into the database using {@link DatabaseHelper#insertInventoryItem(Item)}.</li>
     -     *       <li>Displays a success or error {@link Toast} message based on the result of the database insertion.</li>
     -     *       <li>If successful, finishes the activity, returning to the previous activity.</li>
     -     *     </ul>
     -     *   </li>
     -     *   <li><b>Checks for SMS Permission:</b> Verifies if the application has been granted the {@link Manifest.permission#SEND_SMS} permission.</li>
     -     *   <li><b>Triggers Low Inventory Check and Notification (if permission granted):</b> If SMS permission is granted, it calls {@link #isInventoryLow()} to check if any items are low in stock. If so, it calls {@link #sendSMSNotification(String)} to send an SMS notification.</li>
     -     * </ol>
     -     *
     -     * @param savedInstanceState If the */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextItemQuantity = findViewById(R.id.editTextItemQuantity);
        Button buttonSaveItem = findViewById(R.id.buttonSaveItem);

        buttonSaveItem.setOnClickListener(v -> {
            String name = editTextItemName.getText().toString();
            String description = editTextItemDescription.getText().toString();
            String quantityString = editTextItemQuantity.getText().toString();

            if (name.isEmpty() || description.isEmpty() || quantityString.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityString);

            Item newItem = new Item(name, description, quantity);
            newItem.setDescription(description);
            long itemId = dbHelper.insertInventoryItem(newItem);

            if (itemId != -1) {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and return to MainActivity
            } else {
                Toast.makeText(this, "Error adding item", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, check inventory and send notifications
            if (isInventoryLow()) {
                String message = "Low inventory alert!"; // Customize the message
                sendSMSNotification(message);
            }
        }
    }
}