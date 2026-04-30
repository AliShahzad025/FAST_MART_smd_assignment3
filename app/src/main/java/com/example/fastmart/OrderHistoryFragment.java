// FILE: app/src/main/java/com/example/fastmart/OrderHistoryFragment.java
package com.example.fastmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderHistoryFragment displays a list of past orders from Firebase.
 * For MVP, it loads orders associated with the current user or all orders.
 */
public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView tvEmpty;
    private OrderAdapter adapter;
    private List<OrderModel> orderList;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        rvOrders = view.findViewById(R.id.rvOrderHistory);
        tvEmpty = view.findViewById(R.id.tvEmptyOrders);
        sessionManager = new SessionManager(requireContext());

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(requireContext(), orderList);
        rvOrders.setAdapter(adapter);

        loadOrders();

        return view;
    }

    /**
     * Fetches orders from Firebase Realtime Database.
     * Logic: If Buyer, fetch from orders/{uid}. If Seller, fetch all orders for MVP.
     */
    private void loadOrders() {
        String uid = sessionManager.getUserId();
        String accountType = sessionManager.getAccountType();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");

        if ("Buyer".equalsIgnoreCase(accountType)) {
            // Buyers see only their own orders
            ref.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrderModel order = ds.getValue(OrderModel.class);
                        if (order != null) orderList.add(order);
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else {
            // Sellers see all orders for MVP (simplification)
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    // orders/{userUid}/{orderId}
                    for (DataSnapshot userNode : snapshot.getChildren()) {
                        for (DataSnapshot orderNode : userNode.getChildren()) {
                            OrderModel order = orderNode.getValue(OrderModel.class);
                            if (order != null) orderList.add(order);
                        }
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void updateUI() {
        if (orderList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}