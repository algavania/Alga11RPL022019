package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.practice.myapplication.model.Preferences;
import com.practice.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    EditText txt_login_username, txt_login_password;
    Button btn_login;
    TextView tv_register;
    String email, password;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        txt_login_username = findViewById(R.id.txt_login_username);
        txt_login_password = findViewById(R.id.txt_login_password);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);

        preferences = new Preferences();

        Log.d("Pref: ", "" + preferences.getStatus(getApplicationContext()));


        if (preferences.getStatus(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            preferences.setStatus(getApplicationContext(), true);
        }
        progressDialog = new ProgressDialog(this);

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        txt_login_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    loginUser();
                }
                return false;
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        // Email: test@gmail.com
        // Password: password
        email = txt_login_username.getText().toString();
        password = txt_login_password.getText().toString();
        if (password.isEmpty() && email.isEmpty()) {
            txt_login_username.setError("Fill the username");
            txt_login_password.setError("Fill the password");
        } else if (!email.isEmpty() && password.isEmpty()) {
            txt_login_username.setError("Fill the password");
        } else if (!password.isEmpty() && email.isEmpty()) {
            txt_login_password.setError("Fill the username");
        } else if (password.length() < 6) {
            txt_login_password.setError("Password must be at least 6 characters");
        } else {
            progressDialog.setMessage("Signing you in...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                preferences.setStatus(getApplicationContext(), true);
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}