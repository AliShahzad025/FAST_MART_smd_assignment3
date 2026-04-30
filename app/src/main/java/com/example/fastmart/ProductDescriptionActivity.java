// FILE: app/src/main/java/com/example/fastmart/ProductDescriptionActivity.java
package com.example.fastmart;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

/**
 * ProductDescriptionActivity displays detailed information about a single product.
 * It allows users to add items to their local cart or favourites list.
 */
public class ProductDescriptionActivity extends AppCompatActivity {

    private ImageView ivProductLarge;
    private TextView tvProductName, tvProductType, tvProductPrice, tvProductDescription;
    private MaterialButton btnAddToFav, btnBuyNow;

    private ProductDetailViewModel viewModel;
    private DatabaseHelper dbHelper;
    private ProductModel currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Initialize UI components
        ivProductLarge = findViewById(R.id.ivProductLarge);
        tvProductName = findViewById(R.id.tvProductNameDetail);
        tvProductType = findViewById(R.id.tvProductTypeDetail);
        tvProductPrice = findViewById(R.id.tvProductPriceDetail);
        tvProductDescription = findViewById(R.id.tvProductDescriptionDetail);
        btnAddToFav = findViewById(R.id.btnAddToFav);
        btnBuyNow = findViewById(R.id.btnBuyNowDetail);

        dbHelper = new DatabaseHelper(this);

        // Get productId from Intent
        String productId = getIntent().getStringExtra("productId");

        // MVVM: Initialize ViewModel and observe data
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        
        if (productId != null) {
            viewModel.loadProduct(productId);
        }

        viewModel.product.observe(this, product -> {
            if (product != null) {
                currentProduct = product;
                displayProductDetails(product);
            }
        });

        // Logic for Favourites (SQLite)
        btnAddToFav.setOnClickListener(v -> {
            if (currentProduct != null) {
                dbHelper.addFavourite(currentProduct);
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        });

        // Logic for Cart (SQLite)
        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null) {
                dbHelper.insertOrUpdate(currentProduct);
                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Populates the UI with product information.
     */
    private void displayProductDetails(ProductModel product) {
        tvProductName.setText(product.getName());
        tvProductType.setText(product.getType());
        tvProductPrice.setText("$" + product.getPrice());
        tvProductDescription.setText(product.getDescription());

        // Load image using Glide
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProductLarge);
    }
}