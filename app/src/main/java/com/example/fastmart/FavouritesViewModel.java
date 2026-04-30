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
 * FavouritesViewModel manages user's favourite products using local SQLite storage.
 */
public class FavouritesViewModel extends AndroidViewModel {
    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<FavouriteItem>> _favourites = new MutableLiveData<>();
    public final LiveData<List<FavouriteItem>> favourites = _favourites;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
    }

    public void loadFavourites() {
        List<FavouriteItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVOURITES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                FavouriteItem item = new FavouriteItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                );
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        _favourites.setValue(list);
    }

    public void addFavourite(ProductModel product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", product.getId());
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        // values.put("imageUrl", product.getImageUrl());

        db.insertWithOnConflict(DatabaseHelper.TABLE_FAVOURITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        loadFavourites();
    }

    public void deleteFavourite(String productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_FAVOURITES, "id=?", new String[]{productId});
        loadFavourites();
    }
}