package com.example.fastmart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

/**
 * AuthViewModel handles authentication logic using Firebase Auth.
 */
public class AuthViewModel extends ViewModel {
    private FirebaseAuth mAuth;
    private MutableLiveData<Boolean> _loginSuccess = new MutableLiveData<>();
    public LiveData<Boolean> loginSuccess = _loginSuccess;

    private MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public AuthViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _loginSuccess.setValue(true);
                    } else {
                        _errorMessage.setValue(task.getException() != null ? task.getException().getMessage() : "Login Failed");
                        _loginSuccess.setValue(false);
                    }
                });
    }

    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _loginSuccess.setValue(true);
                    } else {
                        _errorMessage.setValue(task.getException() != null ? task.getException().getMessage() : "Signup Failed");
                        _loginSuccess.setValue(false);
                    }
                });
    }
}