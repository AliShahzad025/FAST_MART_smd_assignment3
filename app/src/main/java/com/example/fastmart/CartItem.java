package com.example.fastmart;

public class CartItem {
    private String id;
    private String name;
    private double price;
    private int qty;
    private String imageUrl;

    public CartItem() {}

    public CartItem(String id, String name, double price, int qty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQty() { return qty; }
    public String getImageUrl() { return imageUrl; }
    public void setQty(int qty) { this.qty = qty; }
}