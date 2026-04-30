package com.example.fastmart;

/**
 * Model class for User data stored in Firebase Realtime Database.
 */
public class User {
    private String uid;
    private String name;
    private String email;
    private String accountType;
    private boolean isNewUser;

    public User() {
        // Required empty constructor for Firebase
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public boolean isNewUser() { return isNewUser; }
    public void setNewUser(boolean newUser) { isNewUser = newUser; }
}