// FILE: app/src/main/java/com/example/fastmart/OrderAdapter.java
package com.example.fastmart;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying the list of orders in the history screen.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orderList;

    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvBuyerName.setText("Buyer: " + order.getBuyerName());
        
        // Format Total Price
        holder.tvTotal.setText(String.format("$%.2f", order.getTotalPrice()));

        // Format Date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(order.getTimestamp())));

        // Join products list into a single multiline string
        if (order.getProducts() != null) {
            String productSummary = TextUtils.join("\n", order.getProducts());
            holder.tvProducts.setText(productSummary);
        } else {
            holder.tvProducts.setText("No details available.");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuyerName, tvDate, tvProducts, tvTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBuyerName = itemView.findViewById(R.id.tvOrderBuyerName);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvProducts = itemView.findViewById(R.id.tvOrderProducts);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}