// FILE: app/src/main/java/com/example/fastmart/HomeFragment.java
package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * HomeFragment displays the product grid for buyers.
 */
public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private ProductViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        rvProducts = view.findViewById(R.id.rvProducts);

        // Setup Greeting from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("userName", "User");
        tvGreeting.setText("Hello " + name);

        // Setup RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), new ArrayList<>());
        rvProducts.setAdapter(adapter);

        // MVVM: Observe ProductViewModel
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter = new ProductAdapter(getContext(), products);
                rvProducts.setAdapter(adapter);
            }
        });

        // Load data
        viewModel.loadAllProducts();

        return view;
    }
}