// FILE: app/src/main/java/com/example/fastmart/CartAdapter.java
package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Adapter for displaying items in the Shopping Cart.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private CartRepository repository;
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartList, OnCartChangedListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.repository = new CartRepository(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        int cartId = Integer.parseInt(item.getId());

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("$" + item.getPrice());
        holder.tvQty.setText(String.valueOf(item.getQty()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivProduct);

        // Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            repository.updateQuantity(cartId, item.getQty() + 1);
            listener.onCartUpdated();
        });

        // Decrease quantity
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQty() > 1) {
                repository.updateQuantity(cartId, item.getQty() - 1);
            } else {
                repository.deleteItem(cartId);
            }
            listener.onCartUpdated();
        });

        // Delete item via menu
        holder.btnMenu.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Item")
                    .setMessage("Delete this item from your cart?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        repository.deleteItem(cartId);
                        listener.onCartUpdated();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQty;
        ImageButton btnPlus, btnMinus, btnMenu;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivCartProduct);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnMenu = itemView.findViewById(R.id.btnCartMenu);
        }
    }
}