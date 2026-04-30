// FILE: app/src/main/java/com/example/fastmart/AccountBuyerFragment.java
package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * AccountBuyerFragment displays the personal details of the logged-in buyer.
 * It also provides functionality to log out and clear user data.
 */
public class AccountBuyerFragment extends Fragment {

    private TextView tvDispName, tvDispAddress, tvDispCountry, tvDispDob, tvDispGender, tvDispPhone;
    private MaterialButton btnLogout;
    private AccountViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_buyer, container, false);

        // Initialize UI components
        tvDispName = view.findViewById(R.id.tvDispName);
        tvDispAddress = view.findViewById(R.id.tvDispAddress);
        tvDispCountry = view.findViewById(R.id.tvDispCountry);
        tvDispDob = view.findViewById(R.id.tvDispDob);
        tvDispGender = view.findViewById(R.id.tvDispGender);
        tvDispPhone = view.findViewById(R.id.tvDispPhone);
        btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(requireContext());

        // MVVM Setup: Observe AccountViewModel for user profile data
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        
        String userId = sessionManager.getUserId();
        if (!userId.isEmpty()) {
            viewModel.loadUser(userId);
        }

        viewModel.userDetails.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvDispName.setText(user.getName());
                tvDispAddress.setText(user.getAddress());
                tvDispCountry.setText(user.getCountry());
                tvDispDob.setText(user.getDob());
                tvDispGender.setText(user.getGender());
                tvDispPhone.setText(user.getPhoneNumber());
            }
        });

        btnLogout.setOnClickListener(v -> performLogout(userId));

        return view;
    }

    /**
     * Handles user logout: sign out from Firebase, clear local sessions, 
     * remove remote data, and redirect to Splash.
     */
    private void performLogout(String userId) {
        // 1. Firebase Sign Out
        FirebaseAuth.getInstance().signOut();

        // 2. Remove user data from Realtime Database as per requirement
        FirebaseDatabase.getInstance().getReference("users").child(userId).removeValue()
                .addOnCompleteListener(task -> {
                    // 3. Clear all SharedPreferences
                    sessionManager.clearSession();

                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // 4. Navigate to SplashActivity and clear backstack
                    Intent intent = new Intent(requireActivity(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                });
    }
}