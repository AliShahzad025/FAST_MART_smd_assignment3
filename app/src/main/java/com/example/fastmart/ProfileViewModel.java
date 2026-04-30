package com.example.fastmart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * ProfileViewModel handles saving user profile data to Firebase.
 */
public class ProfileViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<Boolean> _saveSuccess = new MutableLiveData<>();
    public LiveData<Boolean> saveSuccess = _saveSuccess;

    public ProfileViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveProfile(String uid, UserModel user) {
        mDatabase.child("users").child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    _saveSuccess.setValue(task.isSuccessful());
                });
    }
}