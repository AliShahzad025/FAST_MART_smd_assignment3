// FILE: app/src/main/java/com/example/fastmart/SellerHomeActivity.java
package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

/**
 * SellerHomeActivity provides the main navigation for Sellers using a Navigation Drawer.
 */
public class SellerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Hamburger icon setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_home, R.string.nav_home);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Populate Drawer Header
        TextView tvName = navigationView.getHeaderView(0).findViewById(R.id.tvDrawerName);
        TextView tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tvDrawerEmail);
        tvName.setText(sessionManager.getUserName());
        tvEmail.setText("Seller Account");

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.seller_fragment_container, new SellerHomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_seller_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_seller_home) {
            loadFragment(new SellerHomeFragment());
        } else if (id == R.id.nav_seller_orders) {
            // loadFragment(new OrderHistoryFragment());
        } else if (id == R.id.nav_seller_account) {
            // loadFragment(new SellerAccountFragment());
        } else if (id == R.id.nav_light_theme) {
            applyTheme(false);
        } else if (id == R.id.nav_dark_theme) {
            applyTheme(true);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.seller_fragment_container, fragment)
                .commit();
    }

    private void applyTheme(boolean isDark) {
        sessionManager.saveTheme(isDark);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate(); // Recreate activity to apply changes immediately
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}