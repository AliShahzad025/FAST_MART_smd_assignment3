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

public class ProductViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<List<ProductModel>> _products = new MutableLiveData<>();
    public LiveData<List<ProductModel>> products = _products;

    public ProductViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
    }

    public void loadAllProducts() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ProductModel> productList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductModel product = ds.getValue(ProductModel.class);
                    if (product != null) productList.add(product);
                }
                _products.setValue(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}