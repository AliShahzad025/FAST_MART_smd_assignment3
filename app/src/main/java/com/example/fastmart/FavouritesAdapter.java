// FILE: app/src/main/java/com/example/fastmart/FavouritesAdapter.java
package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.List;

/**
 * Adapter for Favourites list with callbacks for cart addition and deletion.
 */
public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private Context context;
    private List<FavouriteItem> favList;
    private OnFavActionListener listener;

    public interface OnFavActionListener {
        void onDelete(String productId);
        void onAddToCart(FavouriteItem item);
    }

    public FavouritesAdapter(Context context, List<FavouriteItem> favList, OnFavActionListener listener) {
        this.context = context;
        this.favList = favList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        FavouriteItem item = favList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));
        
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivItem);

        // Three-dot menu for deletion
        holder.btnMenu.setOnClickListener(v -> listener.onDelete(item.getId()));
        
        // Cart icon for adding to cart
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCart(item));
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvName, tvPrice;
        ImageButton btnMenu;
        MaterialButton btnAddToCart;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivFavItem);
            tvName = itemView.findViewById(R.id.tvFavItemName);
            tvPrice = itemView.findViewById(R.id.tvFavItemPrice);
            btnMenu = itemView.findViewById(R.id.btnFavMenu);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCartFav);
        }
    }
}