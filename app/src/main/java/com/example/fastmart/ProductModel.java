// FILE: app/src/main/java/com/example/fastmart/ProductModel.java
package com.example.fastmart;

import java.io.Serializable;

/**
 * ProductModel represents a single item in the store.
 */
public class ProductModel implements Serializable {
    private String productId;
    private String name;
    private String type; // Category or model number
    private double price;
    private String description;
    private String imageUrl;
    private String sellerId;

    public ProductModel() {
        // Required for Firebase
    }

    public ProductModel(String productId, String name, String type, double price, String description, String imageUrl, String sellerId) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
}