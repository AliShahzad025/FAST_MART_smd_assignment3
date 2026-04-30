// FILE: app/src/main/java/com/example/fastmart/FavouritesRepository.java
package com.example.fastmart;

import android.content.Context;
import java.util.List;

/**
 * FavouritesRepository serves as the data source for favourite products,
 * interfacing with the local SQLite DatabaseHelper.
 */
public class FavouritesRepository {
    private DatabaseHelper dbHelper;

    public FavouritesRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<FavouriteItem> getAllFavourites() {
        return dbHelper.getAllFavourites();
    }

    public void addFavourite(ProductModel product) {
        dbHelper.addFavourite(product);
    }

    public void deleteFavourite(String productId) {
        dbHelper.deleteFavourite(productId);
    }

    public boolean isFavourite(String productId) {
        return dbHelper.isFavourite(productId);
    }
}