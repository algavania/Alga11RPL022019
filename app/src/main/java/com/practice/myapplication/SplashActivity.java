package com.practice.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        preferences = new Preferences();
        /****** Create Thread that will sleep for 3 seconds****/
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 3 seconds
                    sleep(3*1000);
                    if (preferences.getLogin(getApplicationContext())) {
                        Intent i=new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                    } else {
                        Intent i=new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);
                    }
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }
}