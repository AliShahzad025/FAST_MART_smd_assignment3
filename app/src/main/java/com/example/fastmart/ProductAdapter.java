// FILE: app/src/main/java/com/example/fastmart/ProductAdapter.java
package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Adapter for displaying products in a grid.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productList;
    private DatabaseHelper dbHelper;

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);
        
        holder.tvName.setText(product.getName());
        holder.tvType.setText(product.getType());
        holder.tvPrice.setText("$" + product.getPrice());

        // Load image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivProduct);

        // Set initial favourite state from SQLite
        boolean isFav = dbHelper.isFavourite(product.getProductId());
        holder.btnFav.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        // Toggle Favourite
        holder.btnFav.setOnClickListener(v -> {
            if (dbHelper.isFavourite(product.getProductId())) {
                dbHelper.deleteFavourite(product.getProductId());
                holder.btnFav.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                dbHelper.addFavourite(product);
                holder.btnFav.setImageResource(android.R.drawable.btn_star_big_on);
            }
        });

        // Navigate to Description
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDescriptionActivity.class);
            intent.putExtra("productId", product.getProductId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        ImageButton btnFav;
        TextView tvName, tvType, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            btnFav = itemView.findViewById(R.id.btnFavourite);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvType = itemView.findViewById(R.id.tvProductType);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}