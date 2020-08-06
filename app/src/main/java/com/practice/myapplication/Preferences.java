package com.practice.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    String PREFERENCE = "pref";
    SharedPreferences.Editor editor;

    public boolean getLogin(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean("login", false);
    }

    public void setLogin(Context context, boolean login) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("login", login);
        editor.apply();
    }

}
