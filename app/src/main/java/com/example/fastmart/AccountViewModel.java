package com.example.fastmart;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * AccountViewModel fetches and manages the logged-in user's profile details.
 */
public class AccountViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<UserModel> _userDetails = new MutableLiveData<>();
    public LiveData<UserModel> userDetails = _userDetails;

    public AccountViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    /**
     * Loads user profile data from Firebase Realtime DB.
     * @param uid The unique ID of the user.
     */
    public void loadUser(String uid) {
        mDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                _userDetails.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}