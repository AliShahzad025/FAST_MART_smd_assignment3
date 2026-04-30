package com.example.fastmart;

import java.util.List;

public class OrderModel {
    private String orderId;
    private String buyerId;
    private List<CartItem> items;
    private double totalAmount;
    private String status;
    private long timestamp;

    public OrderModel() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}