package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.practice.myapplication.model.Preferences;
import com.practice.myapplication.R;

public class DashboardActivity extends AppCompatActivity {

    Button btn_logout;
    FirebaseAuth firebaseAuth;
    Preferences preferences;
    CardView cv_menu_one, cv_menu_two;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Dashboard");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);

        btn_logout = findViewById(R.id.btn_logout);
        cv_menu_one = findViewById(R.id.cv_menu_one);
        cv_menu_two = findViewById(R.id.cv_menu_two);

        progressDialog = new ProgressDialog(this);

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
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (account != null) {
                    Log.d("sign in client", "yes");
                    signOut();
                } else {
                    Log.d("fb", "yes");
                    firebaseAuth.signOut();
                    checkUserStatus();
                }
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null && preferences.getStatus(getApplicationContext()) && mGoogleSignInClient == null) {
            preferences.setStatus(getApplicationContext(), false);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void signOut() {
        progressDialog.setMessage("Signing you out...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("test", "Tst");
                        preferences.setStatus(getApplicationContext(), false);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }
                });
    }
}