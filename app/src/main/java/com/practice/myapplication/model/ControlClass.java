package com.practice.myapplication.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.practice.myapplication.ui.DashboardActivity;
import com.practice.myapplication.ui.LoginActivity;

public class ControlClass extends AppCompatActivity {

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences();

        if (preferences.getStatus(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            Log.d("Dashboard", "yes");
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Log.d("Login", "yes");
        }
        finish();
    }
}