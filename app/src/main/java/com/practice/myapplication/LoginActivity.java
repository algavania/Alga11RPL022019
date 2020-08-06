package com.practice.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText txt_login_username, txt_login_password;
    Button btn_login;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        txt_login_username = findViewById(R.id.txt_login_username);
        txt_login_password = findViewById(R.id.txt_login_password);
        btn_login = findViewById(R.id.btn_login);

        preferences = new Preferences();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                How to login:
//                Username: user
//                Password: password
                if (!txt_login_password.getText().toString().equals("password") && !txt_login_username.getText().toString().equals("user")) {
                    txt_login_username.setError("Wrong username");
                    txt_login_password.setError("Wrong password");
                } else if (!txt_login_username.getText().toString().equals("user") && txt_login_password.getText().toString().equals("password")) {
                    txt_login_username.setError("Wrong username");
                } else if (!txt_login_password.getText().toString().equals("password") && txt_login_username.getText().toString().equals("user")) {
                    txt_login_password.setError("Wrong password");
                } else {
                    preferences.setLogin(getApplicationContext(), true);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}