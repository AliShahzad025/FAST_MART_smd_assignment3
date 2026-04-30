// FILE: app/src/main/java/com/example/fastmart/AddProductActivity.java
package com.example.fastmart;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

/**
 * AddProductActivity allows sellers to list new products on FastMart.
 * It uses a hardcoded list of placeholder image URLs for simplicity.
 */
public class AddProductActivity extends AppCompatActivity {

    private ImageView ivProductPreview;
    private TextInputEditText etName, etType, etPrice, etDesc;
    private MaterialButton btnAdd;
    
    private String selectedImageUrl;
    private SessionManager sessionManager;
    private DatabaseReference mDatabase;

    // Hardcoded placeholder image URLs from Picsum
    private final String[] placeholderImages = {
        "https://picsum.photos/id/1/600/400",
        "https://picsum.photos/id/2/600/400",
        "https://picsum.photos/id/10/600/400",
        "https://picsum.photos/id/20/600/400",
        "https://picsum.photos/id/26/600/400",
        "https://picsum.photos/id/30/600/400",
        "https://picsum.photos/id/42/600/400",
        "https://picsum.photos/id/48/600/400"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase and Session
        mDatabase = FirebaseDatabase.getInstance().getReference("products");
        sessionManager = new SessionManager(this);

        // Bind Views
        ivProductPreview = findViewById(R.id.ivProductPreview);
        etName = findViewById(R.id.etProductName);
        etType = findViewById(R.id.etProductType);
        etPrice = findViewById(R.id.etProductPrice);
        etDesc = findViewById(R.id.etProductDesc);
        btnAdd = findViewById(R.id.btnAddProduct);

        // Assign a random image on start
        randomizeImage();

        // Allow user to click image to change it
        ivProductPreview.setOnClickListener(v -> randomizeImage());

        btnAdd.setOnClickListener(v -> validateAndUpload());
    }

    private void randomizeImage() {
        int index = new Random().nextInt(placeholderImages.length);
        selectedImageUrl = placeholderImages[index];
        Glide.with(this).load(selectedImageUrl).into(ivProductPreview);
    }

    private void validateAndUpload() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String sellerId = sessionManager.getUserId();

        // Generate unique key using Firebase push()
        String productId = mDatabase.push().getKey();

        if (productId != null) {
            ProductModel product = new ProductModel(
                productId,
                name,
                type,
                price,
                desc,
                selectedImageUrl,
                sellerId
            );

            // Upload to Realtime Database
            mDatabase.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }
}