// FILE: app/src/main/java/com/example/fastmart/CartFragment.java
package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CartFragment manages the checkout process and local cart display.
 */
public class CartFragment extends Fragment implements CartAdapter.OnCartChangedListener {

    private RecyclerView rvCart;
    private TextView tvTotalPrice;
    private MaterialButton btnCheckout;
    private CartAdapter adapter;
    private CartRepository repository;
    private SharedPreferences sharedPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        repository = new CartRepository(requireContext());
        sharedPrefs = requireActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshCart();

        btnCheckout.setOnClickListener(v -> performCheckout());

        return view;
    }

    /**
     * Reloads items from SQLite and updates the UI.
     */
    private void refreshCart() {
        List<CartItem> items = repository.getAllItems();
        adapter = new CartAdapter(getContext(), items, this);
        rvCart.setAdapter(adapter);
        
        double total = repository.getTotalPrice();
        tvTotalPrice.setText(String.format("$%.2f", total));
    }

    @Override
    public void onCartUpdated() {
        refreshCart();
    }

    /**
     * Handles the checkout logic: SMS, Firebase Order, and Local Clear.
     */
    private void performCheckout() {
        List<CartItem> items = repository.getAllItems();
        if (items.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String buyerUid = sharedPrefs.getString("userId", "");
        String buyerName = sharedPrefs.getString("userName", "User");
        double total = repository.getTotalPrice();

        // 1. Build Order Summary for SMS
        StringBuilder summary = new StringBuilder("New Order from " + buyerName + ":\n");
        for (CartItem item : items) {
            summary.append(item.getName()).append(" x").append(item.getQty()).append("\n");
        }
        summary.append("Total: $").append(String.format("%.2f", total));

        // 2. Send SMS (Hardcoded test number)
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+1234567890", null, summary.toString(), null, null);
        } catch (Exception e) {
            Toast.makeText(getContext(), "SMS failed (check permissions)", Toast.LENGTH_SHORT).show();
        }

        // 3. Store Order in Firebase
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(buyerUid);
        String orderId = ordersRef.push().getKey();
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        orderData.put("items", items);
        orderData.put("totalPrice", total);
        orderData.put("timestamp", System.currentTimeMillis());
        orderData.put("buyerName", buyerName);

        if (orderId != null) {
            ordersRef.child(orderId).setValue(orderData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 4. Clear local SQLite cart
                    repository.clearCart();
                    refreshCart();
                    Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}