package com.example.inventorymanagement;

/**
 * Represents a user in the system.
 * <p>
 * This class stores the basic information about a user, including their unique
 * identifier, username, and password.
 * </p>
 * @noinspection CanBeFinal
 */
public class User {
    public int id;
    public String username;
    public String password;
    public String phoneNumber;

    /**
     * Constructs a new User object.
     *
     * @param id       The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param phoneNumber The phone number of the user.
     */
    public User(int id, String username, String password, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
