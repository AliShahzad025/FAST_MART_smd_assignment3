package com.example.fastmart;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderViewModel handles fetching order history for the current user from Firebase.
 */
public class OrderViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<List<OrderModel>> _orders = new MutableLiveData<>();
    public LiveData<List<OrderModel>> orders = _orders;

    public OrderViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("orders");
    }

    /**
     * Loads all orders from Firebase.
     * In a real app, this would be filtered by userId.
     */
    public void loadAllOrders() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<OrderModel> orderList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    OrderModel order = ds.getValue(OrderModel.class);
                    if (order != null) orderList.add(order);
                }
                _orders.setValue(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}