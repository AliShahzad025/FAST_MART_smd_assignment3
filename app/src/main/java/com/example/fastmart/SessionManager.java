package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FastMartPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ACCOUNT_TYPE = "accountType";
    private static final String KEY_THEME = "theme";

    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getAccountType() {
        return sharedPreferences.getString(KEY_ACCOUNT_TYPE, "Buyer");
    }

    public boolean isDarkTheme() {
        return "dark".equalsIgnoreCase(sharedPreferences.getString(KEY_THEME, "light"));
    }
}