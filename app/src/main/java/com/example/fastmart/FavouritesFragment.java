// FILE: app/src/main/java/com/example/fastmart/FavouritesFragment.java
package com.example.fastmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * FavouritesFragment displays products saved by the user to their local SQLite favourites list.
 */
public class FavouritesFragment extends Fragment implements FavouritesAdapter.OnFavActionListener {

    private RecyclerView rvFav;
    private TextView tvEmpty;
    private FavouritesAdapter adapter;
    private FavouritesViewModel viewModel;
    private CartRepository cartRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        rvFav = view.findViewById(R.id.rvFavourites);
        tvEmpty = view.findViewById(R.id.tvEmptyFav);
        
        rvFav.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRepository = new CartRepository(requireContext());

        // MVVM Setup: Observe the FavouritesViewModel for immediate UI updates
        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        viewModel.favourites.observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.loadFavourites();

        return view;
    }

    private void updateUI(List<FavouriteItem> items) {
        if (items == null || items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFav.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFav.setVisibility(View.VISIBLE);
            adapter = new FavouritesAdapter(requireContext(), items, this);
            rvFav.setAdapter(adapter);
        }
    }

    @Override
    public void onDelete(String productId) {
        // Confirmation dialog before deleting from favourites
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Favourite")
                .setMessage("Do you want to delete this product from favourites?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.deleteFavourite(productId);
                    Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onAddToCart(FavouriteItem item) {
        // Convert FavouriteItem to ProductModel to satisfy CartRepository's insert method
        ProductModel product = new ProductModel();
        product.setProductId(item.getId());
        product.setName(item.getName());
        product.setPrice(item.getPrice());
        product.setImageUrl(item.getImageUrl());

        cartRepository.insertOrUpdate(product);
        Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
    }
}