package com.example.fastmart;

/**
 * UserModel represents the user data structure in Firebase Realtime Database.
 */
public class UserModel {
    private String uid;
    private String name;
    private String email;
    private String address;
    private String gender;
    private String phoneNumber;
    private String country;
    private String accountType;

    // Default constructor required for Firebase
    public UserModel() {}

    public UserModel(String uid, String name, String email, String address, String gender, 
                     String phoneNumber, String country, String accountType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.accountType = accountType;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}