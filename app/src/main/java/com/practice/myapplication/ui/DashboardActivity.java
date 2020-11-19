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
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    Button btn_logout;
    FirebaseAuth firebaseAuth;
    Preferences preferences;
    CardView cv_menu_one, cv_menu_two;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    GoogleSignInAccount account;
    TextView tv_userName;
    FirebaseUser user;
    AccessToken accessToken;
    boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Dashboard");

        accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);

        btn_logout = findViewById(R.id.btn_logout);
        cv_menu_one = findViewById(R.id.cv_menu_one);
        cv_menu_two = findViewById(R.id.cv_menu_two);
        tv_userName = findViewById(R.id.tv_userName);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        preferences = new Preferences();

        if (account != null) {
            String personName = account.getDisplayName();
            tv_userName.setText("Welcome, "+personName);
        } else if (user != null){
            String email = user.getEmail();
            tv_userName.setText("Welcome, "+email);
        } else if (isLoggedIn) {
            facebookLogin();
        }

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
                    signOut();
                } else if (user != null) {
                    firebaseAuth.signOut();
                    checkUserStatus();
                } else if (isLoggedIn) {
                    LoginManager.getInstance().logOut();
                    checkUserStatus();
                }
            }
        });
    }

    private void checkUserStatus() {
            preferences.setStatus(getApplicationContext(), false);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
    }

    private void signOut() {
        progressDialog.setMessage("Signing you out...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        preferences.setStatus(getApplicationContext(), false);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }
                });
    }

    private void facebookLogin() {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        if (object != null) {
                            Log.d("test", object.toString());
                            try {
                                String name = object.getString("name");
                                tv_userName.setText("Welcome, "+name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("test", "null");
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

}