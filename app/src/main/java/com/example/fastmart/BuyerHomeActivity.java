// FILE: app/src/main/java/com/example/fastmart/BuyerHomeActivity.java
package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * BuyerHomeActivity manages the primary navigation for the buyer role.
 */
public class BuyerHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FloatingActionButton fabChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_home);

        bottomNav = findViewById(R.id.bottomNav);
        fabChat = findViewById(R.id.fabChat);

        // Load HomeFragment by default
        loadFragment(new HomeFragment());

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_cart) {
                // fragment = new CartFragment();
            } else if (itemId == R.id.nav_favourites) {
                // fragment = new FavouritesFragment();
            } else if (itemId == R.id.nav_account) {
                // fragment = new AccountFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

        // FAB Chat Logic
        fabChat.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Chat")
                    .setMessage("Would you like to chat with a seller?")
                    .setPositiveButton("Chat now", (dialog, which) -> {
                        startActivity(new Intent(BuyerHomeActivity.this, ChatActivity.class));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}