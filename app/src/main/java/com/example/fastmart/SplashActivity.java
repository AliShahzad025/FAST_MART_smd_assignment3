package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity serves as the entry point of the application.
 * It handles session-based routing to either the Buyer/Seller home or the onboarding screen.
 */
public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager to check login state
        sessionManager = new SessionManager(this);

        // Logic: If already logged in, skip the splash screen and go to the appropriate dashboard
        if (sessionManager.isLoggedIn()) {
            routeToHome(sessionManager.getAccountType());
            return;
        }

        // Otherwise, set the UI for the Splash screen
        setContentView(R.layout.activity_splash);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> {
            // Navigate to Authentication screen
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        });
    }

    /**
     * Routes the user to either BuyerHomeActivity or SellerHomeActivity based on accountType.
     */
    private void routeToHome(String accountType) {
        Intent intent;
        if ("Seller".equalsIgnoreCase(accountType)) {
            intent = new Intent(this, SellerHomeActivity.class);
        } else {
            // Default to Buyer if not explicitly Seller or if "Buyer"
            intent = new Intent(this, BuyerHomeActivity.class);
        }
        startActivity(intent);
        finish(); // Close SplashActivity so user can't go back to it
    }
}

/**
 * Helper class to manage user session using SharedPreferences.
 */
class SessionManager {
    private static final String PREF_NAME = "FastMartPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ACCOUNT_TYPE = "accountType";
    
    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getAccountType() {
        return sharedPreferences.getString(KEY_ACCOUNT_TYPE, "Buyer");
    }
}