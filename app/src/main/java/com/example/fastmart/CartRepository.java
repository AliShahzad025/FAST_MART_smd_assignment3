// FILE: app/src/main/java/com/example/fastmart/CartRepository.java
package com.example.fastmart;

import android.content.Context;
import java.util.List;

/**
 * CartRepository provides a clean API for the UI to interact with the local Cart database.
 */
public class CartRepository {
    private DatabaseHelper dbHelper;

    public CartRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<CartItem> getAllItems() {
        return dbHelper.getAllCartItems();
    }

    public void updateQuantity(int cartId, int qty) {
        dbHelper.updateQuantity(cartId, qty);
    }

    public void deleteItem(int cartId) {
        dbHelper.deleteCartItem(cartId);
    }

    public double getTotalPrice() {
        return dbHelper.getTotalPrice();
    }

    public void clearCart() {
        dbHelper.clearCart();
    }
}