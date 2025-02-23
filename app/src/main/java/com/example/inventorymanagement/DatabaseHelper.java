package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "InventoryManager";
    private static final int DATABASE_VERSION = 2;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";


    // Inventory Table
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_QUANTITY = "quantity";
    private static final String COLUMN_ITEM_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PHONE_NUMBER + " TEXT" // New column
            + ")";

    private static final String CREATE_TABLE_INVENTORY =
            "CREATE TABLE " + TABLE_INVENTORY + "(" +
                    COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ITEM_NAME + " TEXT," +
                    COLUMN_ITEM_QUANTITY + " INTEGER," +
                    COLUMN_ITEM_DESCRIPTION + " TEXT" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_INVENTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    public long insertUser(User user) {
        if (user == null || user.username == null || user.username.isEmpty() || user.password == null || user.password.isEmpty()) {
            throw new IllegalArgumentException("User object or its fields cannot be null or empty.");
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, user.username);
            values.put(COLUMN_PASSWORD, user.password);
            values.put(COLUMN_PHONE_NUMBER, user.phoneNumber);
            return db.insert(TABLE_USERS, null, values);
        }
    }

    public User getUserByUsername(String username) {
        User user = null;

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(
                     TABLE_USERS,
                     new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONE_NUMBER},
                     COLUMN_USERNAME + " =?",
                     new String[]{username},
                     null,
                     null,
                     null
             )) {

            Log.d("DatabaseHelper", "Database Path: " + db.getPath());

            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID);
                int usernameColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USERNAME);
                int passwordColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
                int phoneNumberColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER);

                int id = cursor.getInt(idColumnIndex);
                String retrievedUsername = cursor.getString(usernameColumnIndex);
                String password = cursor.getString(passwordColumnIndex);
                String phoneNumber = cursor.getString(phoneNumberColumnIndex);

                user = new User(id, retrievedUsername, password, phoneNumber);

                Log.d("DatabaseHelper", "Retrieved User: " + user.username + ", " + user.password + ", Phone Number: " + user.phoneNumber);
            } else {
                Log.w("DatabaseHelper", "No user found with username: " + username);
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error retrieving user by username", e);
            throw e;
        }

        return user;
    }

    public long insertInventoryItem(Item item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, item.getName());
            values.put(COLUMN_ITEM_QUANTITY, item.getQuantity());
            values.put(COLUMN_ITEM_DESCRIPTION, item.getDescription());
            return db.insert(TABLE_INVENTORY, null, values);
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error inserting inventory item", e);
            return -1; // Return -1 to indicate an error
        }
    }

    public List<Item> getAllInventoryItems() {
        List<Item> items = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME);
                int quantityIndex = cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESCRIPTION);

                do {
                    Item item = new Item(
                            cursor.getString(nameIndex),
                            cursor.getString(descriptionIndex),
                            cursor.getInt(quantityIndex)
                    );
                    item.setId(cursor.getInt(idIndex));
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error retrieving all inventory items", e);
        }
        return items;
    }

    public void updateInventoryItem(Item item) {
        if (item.getId() < 1) {
            throw new IllegalArgumentException("Item ID must be set for update operation.");
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, item.getName());
            values.put(COLUMN_ITEM_QUANTITY, item.getQuantity());
            values.put(COLUMN_ITEM_DESCRIPTION, item.getDescription());

            String whereClause = COLUMN_ITEM_ID + " =?";
            String whereArgs = String.valueOf(item.getId());

            int rowsAffected = db.update(TABLE_INVENTORY, values, whereClause, new String[]{whereArgs});

            if (rowsAffected > 0) {
                Log.d("Database Update", "Item updated successfully. Rows affected: " + rowsAffected);
            } else {
                Log.w("Database Update", "Item update failed or no item found with ID: " + item.getId());
                // Consider throwing an exception here if the update is critical
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error updating inventory item", e);
            throw e; // Re-throw the exception after logging
        }
    }

    public void deleteInventoryItem(int itemId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsAffected = db.delete(TABLE_INVENTORY, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
            if (rowsAffected > 0) {
                Log.d("Database Delete", "Item deleted successfully. Rows affected: " + rowsAffected);
            } else {
                Log.w("Database Delete", "Item delete failed or no item found with ID: " + itemId);
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error deleting inventory item", e);
            throw e;
        }
    }
}