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

/**
 * ProductDetailViewModel fetches a specific product's details from Firebase.
 */
public class ProductDetailViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<ProductModel> _product = new MutableLiveData<>();
    public LiveData<ProductModel> product = _product;

    public ProductDetailViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
    }

    /**
     * Loads a single product from Firebase by its ID.
     */
    public void loadProduct(String productId) {
        mDatabase.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel p = snapshot.getValue(ProductModel.class);
                _product.setValue(p);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}