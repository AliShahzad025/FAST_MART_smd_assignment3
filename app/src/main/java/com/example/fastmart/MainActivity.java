/*
 * AndroidManifest.xml reminder:
 * MainActivity must have the LAUNCHER intent-filter.
 * SplashActivity, AuthActivity, BuyerHomeActivity, SellerHomeActivity have NO intent-filter.
 */

package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * MainActivity is the app's entry point (LAUNCHER).
 * It handles global theme settings and session-based routing.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SessionManager to read local SharedPreferences
        SessionManager sessionManager = new SessionManager(this);

        // 1. Global Theme Check: Apply dark or light mode based on saved preference
        if (sessionManager.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        // Show NO UI - the background color is defined in activity_main.xml for consistency
        setContentView(R.layout.activity_main);

        // 2. Routing Logic: Check login state and account type
        if (sessionManager.isLoggedIn()) {
            String accountType = sessionManager.getAccountType();
            
            if ("Seller".equalsIgnoreCase(accountType)) {
                // Route to Seller Dashboard
                startActivity(new Intent(MainActivity.this, SellerHomeActivity.class));
            } else {
                // Route to Buyer Home (default)
                startActivity(new Intent(MainActivity.this, BuyerHomeActivity.class));
            }
        } else {
            // Not logged in - route to the Welcome/Splash screen
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
        }

        // 3. Prevent user from returning to this activity by finishing it immediately
        finish();
    }
}