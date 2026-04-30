package com.example.fastmart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

/**
 * FavouritesViewModel manages user's favourite products using the FavouritesRepository.
 */
public class FavouritesViewModel extends AndroidViewModel {
    private final FavouritesRepository repository;
    private final MutableLiveData<List<FavouriteItem>> _favourites = new MutableLiveData<>();
    public final LiveData<List<FavouriteItem>> favourites = _favourites;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repository = new FavouritesRepository(application);
    }

    public void loadFavourites() {
        _favourites.setValue(repository.getAllFavourites());
    }

    public void deleteFavourite(String productId) {
        repository.deleteFavourite(productId);
        loadFavourites();
    }
}