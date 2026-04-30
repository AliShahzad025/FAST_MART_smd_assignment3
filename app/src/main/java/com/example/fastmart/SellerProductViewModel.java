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
 * SellerProductViewModel filters products belonging to a specific seller.
 */
public class SellerProductViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<List<ProductModel>> _products = new MutableLiveData<>();
    public LiveData<List<ProductModel>> products = _products;

    public SellerProductViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
    }

    /**
     * Loads products uploaded by a specific seller.
     */
    public void loadSellerProducts(String sellerId) {
        mDatabase.orderByChild("sellerId").equalTo(sellerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ProductModel> productList = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ProductModel p = ds.getValue(ProductModel.class);
                            if (p != null) productList.add(p);
                        }
                        _products.setValue(productList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}