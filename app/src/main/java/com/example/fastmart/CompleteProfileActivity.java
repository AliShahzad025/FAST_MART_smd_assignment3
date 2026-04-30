package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * CompleteProfileActivity handles capturing additional user details 
 * after the initial authentication and stores them in Firebase Realtime DB.
 */
public class CompleteProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etCountry, etAddress;
    private Spinner spinnerAccountType;
    private RadioGroup rgGender;
    private CheckBox cbTerms;
    private MaterialButton btnSaveProfile;
    private ProgressBar progressBar;

    private String uid, email;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        // Get the uid and email passed from AuthActivity
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etCountry = findViewById(R.id.etCountry);
        etAddress = findViewById(R.id.etAddress);
        spinnerAccountType = findViewById(R.id.spinnerAccountType);
        rgGender = findViewById(R.id.rgGender);
        cbTerms = findViewById(R.id.cbTerms);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        progressBar = findViewById(R.id.progressBar);

        // Populate the Account Type Spinner
        String[] accountTypes = {"Buyer", "Seller"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, accountTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String accountType = spinnerAccountType.getSelectedItem().toString();
        
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = (selectedGenderId == R.id.rbMale) ? "Male" : "Female";

        // Basic Validation
        if (name.isEmpty() || phone.isEmpty() || country.isEmpty() || address.isEmpty() || selectedGenderId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "You must agree to the Terms of Service", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create the data model
        UserModel user = new UserModel(uid, name, email, address, gender, "+1" + phone, country, accountType);

        // Save to Firebase Realtime Database under "users/{uid}"
        mDatabase.child("users").child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    updateSessionAndNavigate(user);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompleteProfileActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Persists user state locally and navigates to the appropriate dashboard.
     */
    private void updateSessionAndNavigate(UserModel user) {
        SharedPreferences sharedPref = getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userId", user.getUid());
        editor.putString("userName", user.getName());
        editor.putBoolean("isLoggedIn", true);
        editor.putString("accountType", user.getAccountType());
        editor.apply();

        // Navigate based on account type
        Intent intent;
        if ("Seller".equalsIgnoreCase(user.getAccountType())) {
            intent = new Intent(this, SellerHomeActivity.class);
        } else {
            intent = new Intent(this, BuyerHomeActivity.class);
        }
        startActivity(intent);
        finish(); // Finish this activity so user cannot return to profile completion
    }
}