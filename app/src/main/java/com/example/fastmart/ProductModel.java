package com.example.fastmart;

public class ProductModel {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private String sellerId;

    public ProductModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getSellerId() { return sellerId; }
}