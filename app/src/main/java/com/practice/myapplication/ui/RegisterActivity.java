package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.practice.myapplication.R;

public class RegisterActivity extends AppCompatActivity {

    EditText txt_register_username, txt_register_password;
    Button btn_register;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String email, password;
    TextView tv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        txt_register_password = findViewById(R.id.txt_register_password);
        txt_register_username = findViewById(R.id.txt_register_username);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        txt_register_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    registerUser();
                }
                return false;
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        email = txt_register_username.getText().toString();
        password = txt_register_password.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isEmpty()) {
            txt_register_username.setError("Invalid Email");
            txt_register_password.setError("Invalid Password");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !password.isEmpty()) {
            txt_register_username.setError("Invalid Email");
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isEmpty()) {
            txt_register_password.setError("Invalid Password");
        } else if (password.length() < 6) {
            txt_register_password.setError("Password length must be 6 characters or more");
        } else {
            progressDialog.setMessage("We're registering you...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "You're registered!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Sorry, we're not able to registered you", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                            txt_register_password.setText("");
                            txt_register_username.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}