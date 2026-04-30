// FILE: app/src/main/java/com/example/fastmart/OrderModel.java
package com.example.fastmart;

import java.util.List;

/**
 * OrderModel represents a completed purchase for display in history.
 */
public class OrderModel {
    private String orderId;
    private String buyerName;
    private String buyerUid;
    private List<String> products; // Formatted as "ProductName xQty - $Price"
    private double totalPrice;
    private long timestamp;

    public OrderModel() {
        // Required for Firebase
    }

    public OrderModel(String orderId, String buyerName, String buyerUid, List<String> products, double totalPrice, long timestamp) {
        this.orderId = orderId;
        this.buyerName = buyerName;
        this.buyerUid = buyerUid;
        this.products = products;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerUid() { return buyerUid; }
    public void setBuyerUid(String buyerUid) { this.buyerUid = buyerUid; }

    public List<String> getProducts() { return products; }
    public void setProducts(List<String> products) { this.products = products; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}