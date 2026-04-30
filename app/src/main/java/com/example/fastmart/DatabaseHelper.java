package com.example.fastmart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FastMart.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CART = "cart";
    public static final String TABLE_FAVOURITES = "favourites";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CART + " (id TEXT PRIMARY KEY, name TEXT, price REAL, qty INTEGER, imageUrl TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " (id TEXT PRIMARY KEY, name TEXT, price REAL, imageUrl TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }
}