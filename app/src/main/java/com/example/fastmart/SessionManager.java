package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles all SharedPreferences operations for the app.
 */
public class SessionManager {
    private static final String PREF_NAME = "FastMartPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_ACCOUNT_TYPE = "accountType";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_THEME = "theme";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String uid, String name, String accountType) {
        editor.putString(KEY_USER_ID, uid);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_ACCOUNT_TYPE, accountType);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getAccountType() {
        return sharedPreferences.getString(KEY_ACCOUNT_TYPE, "Buyer");
    }

    public void saveTheme(boolean isDark) {
        editor.putString(KEY_THEME, isDark ? "dark" : "light");
        editor.apply();
    }

    public boolean isDarkTheme() {
        return "dark".equalsIgnoreCase(sharedPreferences.getString(KEY_THEME, "light"));
    }
}