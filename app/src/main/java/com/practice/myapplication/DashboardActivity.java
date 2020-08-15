package com.practice.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    Button btn_logout;
    FirebaseAuth firebaseAuth;
    Preferences preferences;
    CardView cv_menu_one, cv_menu_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Dashboard");

        btn_logout = findViewById(R.id.btn_logout);
        cv_menu_one = findViewById(R.id.cv_menu_one);
        cv_menu_two = findViewById(R.id.cv_menu_two);

        firebaseAuth = FirebaseAuth.getInstance();
        preferences = new Preferences();

        cv_menu_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        cv_menu_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null && preferences.getStatus(getApplicationContext())) {
            preferences.setStatus(getApplicationContext(), false);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }
}