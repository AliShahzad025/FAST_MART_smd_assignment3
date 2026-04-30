package com.example.fastmart;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * CartViewModel manages the shopping cart using local SQLite storage.
 */
public class CartViewModel extends AndroidViewModel {
    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<CartItem>> _cartItems = new MutableLiveData<>();
    public final LiveData<List<CartItem>> cartItems = _cartItems;

    public CartViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
    }

    public void loadCart() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("qty")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                );
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        _cartItems.setValue(list);
    }

    public void addToCart(ProductModel product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", product.getId());
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("qty", 1);
        // values.put("imageUrl", product.getImageUrl()); // Assuming imageUrl exists

        db.insertWithOnConflict(DatabaseHelper.TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        loadCart();
    }

    public void updateQty(String cartId, int qty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("qty", qty);
        db.update(DatabaseHelper.TABLE_CART, values, "id=?", new String[]{cartId});
        loadCart();
    }

    public void deleteItem(String cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, "id=?", new String[]{cartId});
        loadCart();
    }

    public double getTotal() {
        double total = 0;
        if (_cartItems.getValue() != null) {
            for (CartItem item : _cartItems.getValue()) {
                total += item.getPrice() * item.getQty();
            }
        }
        return total;
    }
}