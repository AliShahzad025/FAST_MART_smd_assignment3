package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * AuthActivity handles Login and Signup using Firebase Auth and Realtime Database.
 */
public class AuthActivity extends AppCompatActivity {

    private LinearLayout llLogin, llSignup;
    private TextView tvTabLogin, tvTabSignup;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private TextInputEditText etSignupEmail, etSignupPassword, etSignupVerifyPassword;
    private MaterialButton btnLogin, btnSignup;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Views
        llLogin = findViewById(R.id.llLogin);
        llSignup = findViewById(R.id.llSignup);
        tvTabLogin = findViewById(R.id.tvTabLogin);
        tvTabSignup = findViewById(R.id.tvTabSignup);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etSignupVerifyPassword = findViewById(R.id.etSignupVerifyPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);

        // Tab Switching Logic
        tvTabLogin.setOnClickListener(v -> toggleTab(true));
        tvTabSignup.setOnClickListener(v -> toggleTab(false));

        // Login Logic
        btnLogin.setOnClickListener(v -> performLogin());

        // Signup Logic
        btnSignup.setOnClickListener(v -> performSignup());
    }

    private void toggleTab(boolean isLogin) {
        if (isLogin) {
            llLogin.setVisibility(View.VISIBLE);
            llSignup.setVisibility(View.GONE);
            tvTabLogin.setTextColor(getResources().getColor(R.color.black));
            tvTabSignup.setTextColor(getResources().getColor(R.color.colorTextSecondary));
        } else {
            llLogin.setVisibility(View.GONE);
            llSignup.setVisibility(View.VISIBLE);
            tvTabLogin.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            tvTabSignup.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void performLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        fetchUserData(user.getUid());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AuthActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchUserData(String uid) {
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        saveSession(user);
                        navigateToHome(user.getAccountType());
                    }
                } else {
                    // This might happen if user didn't complete profile
                    Intent intent = new Intent(AuthActivity.this, CompleteProfileActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AuthActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSignup() {
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String verifyPass = etSignupVerifyPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || verifyPass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(verifyPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(AuthActivity.this, CompleteProfileActivity.class);
                        intent.putExtra("uid", user.getUid());
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveSession(User user) {
        SharedPreferences sharedPref = getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userId", user.getUid());
        editor.putString("userName", user.getName());
        editor.putBoolean("isLoggedIn", true);
        editor.putString("accountType", user.getAccountType());
        editor.apply();
    }

    private void navigateToHome(String accountType) {
        Intent intent;
        if ("Seller".equalsIgnoreCase(accountType)) {
            intent = new Intent(this, SellerHomeActivity.class);
        } else {
            intent = new Intent(this, BuyerHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}