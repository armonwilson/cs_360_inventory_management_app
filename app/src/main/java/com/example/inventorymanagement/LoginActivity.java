package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The LoginActivity class is responsible for handling user login and account creation.
 * It provides a user interface for entering username and password, and interacts with
 * the DatabaseHelper to authenticate users and create new accounts.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;

    /**
     * Called when the activity is first created. This is where all of your normal
     * static set up: create views, bind data to lists, etc.  This method also handles user login
     * and account creation logic.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            try (DatabaseHelper dbHelper = new DatabaseHelper(v.getContext())) { // Use v.getContext() here
                // Retrieve the user from the database
                User user = dbHelper.getUserByUsername(username);

                if (user!= null && user.password.equals(password)) {
                    // Successful login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Invalid credentials
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCreateAccount.setOnClickListener(view -> {
            // Inflate the pop-up view layout
            View popupView = getLayoutInflater().inflate(R.layout.activity_create_account, null);

            // Get references to the EditText fields and the Create Account button in the pop-up view
            EditText editTextNewUsername = popupView.findViewById(R.id.editTextNewUsername);
            EditText editTextNewPassword = popupView.findViewById(R.id.editTextNewPassword);
            EditText editTextNewPhoneNumber = popupView.findViewById(R.id.editTextNewPhoneNumber);
            Button buttonCreateAccountInPopup = popupView.findViewById(R.id.buttonCreateAccountInPopup);

            // Create an AlertDialog.Builder to build the pop-up view
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setView(popupView);
            AlertDialog dialog = builder.create();

            // Set OnClickListener for the Create Account button in the pop-up view
            buttonCreateAccountInPopup.setOnClickListener(v -> {
                String newUsername = editTextNewUsername.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String phoneNumber = editTextNewPhoneNumber.getText().toString();

                // --- Validation ---

                // Check if username or password is empty
                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- Account Creation ---
                try (DatabaseHelper dbHelper = new DatabaseHelper(view.getContext())) {
                    // Create a new User object
                    User newUser = new User(0, newUsername, newPassword, phoneNumber); // 0 for auto-generated ID

                    // Insert the user into the database
                    long userId = dbHelper.insertUser(newUser);

                    if (userId!= -1) {
                        Toast.makeText(LoginActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                    }
                }

                // Close the pop-up view
                dialog.dismiss();
            });

            // Show the pop-up view
            dialog.show();
        });
    }
}