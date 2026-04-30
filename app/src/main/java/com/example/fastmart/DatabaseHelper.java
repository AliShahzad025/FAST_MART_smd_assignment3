// FILE: app/src/main/java/com/example/fastmart/DatabaseHelper.java
package com.example.fastmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages local storage for the Shopping Cart and Favourites list using SQLite.
 * This class provides CRUD operations for both tables.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fastmart.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_CART = "cart";
    public static final String TABLE_FAVOURITES = "favourites";

    // Column Names
    public static final String COL_CART_ID = "cartId";
    public static final String COL_FAV_ID = "favId";
    public static final String COL_PRODUCT_ID = "productId";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE_URL = "imageUrl";
    public static final String COL_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table 1 - cart
        String CREATE_CART_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_ID + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE_URL + " TEXT, " +
                COL_QUANTITY + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_CART_TABLE);

        // Create Table 2 - favourites (productId is UNIQUE)
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVOURITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_ID + " TEXT UNIQUE, " +
                COL_NAME + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // DROP TABLE IF EXISTS, then recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }

    // --- CART METHODS ---

    /**
     * Inserts a product into the cart or updates quantity if it already exists.
     */
    public void insertOrUpdate(ProductModel product) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if product already exists in cart using its productId
        Cursor cursor = db.query(TABLE_CART, new String[]{COL_CART_ID, COL_QUANTITY}, 
                COL_PRODUCT_ID + "=?", new String[]{product.getId()}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Product exists, increment quantity
            int idIndex = cursor.getColumnIndex(COL_CART_ID);
            int qtyIndex = cursor.getColumnIndex(COL_QUANTITY);
            if (idIndex != -1 && qtyIndex != -1) {
                int cartId = cursor.getInt(idIndex);
                int currentQty = cursor.getInt(qtyIndex);
                updateQuantity(cartId, currentQty + 1);
            }
            cursor.close();
        } else {
            // New product, insert into cart
            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_ID, product.getId());
            values.put(COL_NAME, product.getName());
            values.put(COL_PRICE, product.getPrice());
            // values.put(COL_IMAGE_URL, product.getImageUrl()); // Add if available in model
            values.put(COL_QUANTITY, 1);
            db.insert(TABLE_CART, null, values);
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Updates quantity of an item using SQL UPDATE.
     */
    public void updateQuantity(int cartId, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, qty);
        // SQL: UPDATE cart SET quantity = ? WHERE cartId = ?
        db.update(TABLE_CART, values, COL_CART_ID + "=?", new String[]{String.valueOf(cartId)});
    }

    /**
     * Deletes an item from the cart using SQL DELETE.
     */
    public void deleteItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // SQL: DELETE FROM cart WHERE cartId = ?
        db.delete(TABLE_CART, COL_CART_ID + "=?", new String[]{String.valueOf(cartId)});
    }

    /**
     * Fetches all items currently in the cart.
     */
    public List<CartItem> getAllCartItems() {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                int pidIdx = cursor.getColumnIndex(COL_PRODUCT_ID);
                int nameIdx = cursor.getColumnIndex(COL_NAME);
                int priceIdx = cursor.getColumnIndex(COL_PRICE);
                int qtyIdx = cursor.getColumnIndex(COL_QUANTITY);
                int imgIdx = cursor.getColumnIndex(COL_IMAGE_URL);

                CartItem item = new CartItem(
                        pidIdx != -1 ? cursor.getString(pidIdx) : "",
                        nameIdx != -1 ? cursor.getString(nameIdx) : "",
                        priceIdx != -1 ? cursor.getDouble(priceIdx) : 0.0,
                        qtyIdx != -1 ? cursor.getInt(qtyIdx) : 1,
                        imgIdx != -1 ? cursor.getString(imgIdx) : ""
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    /**
     * Calculates total price of all items in cart using SQL SUM().
     */
    public double getTotalPrice() {
        SQLiteDatabase db = this.getReadableDatabase();
        // SQL: SELECT SUM(price * quantity) FROM cart
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_CART, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    /**
     * Clears all items from the cart.
     */
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }

    // --- FAVOURITES METHODS ---

    /**
     * Adds a product to favourites using INSERT OR IGNORE to prevent duplicates.
     */
    public void addFavourite(ProductModel product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_ID, product.getId());
        values.put(COL_NAME, product.getName());
        values.put(COL_PRICE, product.getPrice());
        // values.put(COL_IMAGE_URL, product.getImageUrl());
        db.insertWithOnConflict(TABLE_FAVOURITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a favourite using its productId using SQL DELETE.
     */
    public void deleteFavourite(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // SQL: DELETE FROM favourites WHERE productId = ?
        db.delete(TABLE_FAVOURITES, COL_PRODUCT_ID + "=?", new String[]{productId});
    }

    /**
     * Fetches all products marked as favourites.
     */
    public List<FavouriteItem> getAllFavourites() {
        List<FavouriteItem> favs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVOURITES, null);

        if (cursor.moveToFirst()) {
            do {
                int pidIdx = cursor.getColumnIndex(COL_PRODUCT_ID);
                int nameIdx = cursor.getColumnIndex(COL_NAME);
                int priceIdx = cursor.getColumnIndex(COL_PRICE);
                int imgIdx = cursor.getColumnIndex(COL_IMAGE_URL);

                FavouriteItem item = new FavouriteItem(
                        pidIdx != -1 ? cursor.getString(pidIdx) : "",
                        nameIdx != -1 ? cursor.getString(nameIdx) : "",
                        priceIdx != -1 ? cursor.getDouble(priceIdx) : 0.0,
                        imgIdx != -1 ? cursor.getString(imgIdx) : ""
                );
                favs.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favs;
    }

    /**
     * Checks if a product exists in the favourites list.
     */
    public boolean isFavourite(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{COL_FAV_ID}, 
                COL_PRODUCT_ID + "=?", new String[]{productId}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }
}