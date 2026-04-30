// FILE: app/src/main/java/com/example/fastmart/SellerHomeFragment.java
package com.example.fastmart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * SellerHomeFragment displays the seller's own inventory.
 */
public class SellerHomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private SellerProductAdapter adapter;
    private SellerProductViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);

        sessionManager = new SessionManager(requireContext());
        TextView tvGreeting = view.findViewById(R.id.tvSellerGreeting);
        tvGreeting.setText("Hello " + sessionManager.getUserName());

        rvProducts = view.findViewById(R.id.rvSellerProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        adapter = new SellerProductAdapter(getContext(), new ArrayList<>());
        rvProducts.setAdapter(adapter);

        // FAB to add new product
        FloatingActionButton fab = view.findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddProductActivity.class)));

        // MVVM: Load seller-specific products
        viewModel = new ViewModelProvider(this).get(SellerProductViewModel.class);
        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.updateList(products);
            }
        });

        viewModel.loadSellerProducts(sessionManager.getUserId());

        return view;
    }

    /**
     * Internal Adapter for Seller Products using item_seller_product layout.
     */
    private class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.ViewHolder> {
        private Context context;
        private List<ProductModel> list;

        public SellerProductAdapter(Context context, List<ProductModel> list) {
            this.context = context;
            this.list = list;
        }

        public void updateList(List<ProductModel> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_seller_product, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductModel p = list.get(position);
            holder.name.setText(p.getName());
            holder.price.setText("$" + p.getPrice());
            Glide.with(context).load(p.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDescriptionActivity.class);
                intent.putExtra("productId", p.getProductId());
                intent.putExtra("viewType", "seller"); // To hide buy buttons in description
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView name, price;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.ivSellerProductImage);
                name = itemView.findViewById(R.id.tvSellerProductName);
                price = itemView.findViewById(R.id.tvSellerProductPrice);
            }
        }
    }
}