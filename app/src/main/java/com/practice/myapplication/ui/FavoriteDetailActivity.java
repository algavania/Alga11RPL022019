package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.myapplication.R;
import com.practice.myapplication.RealmHelper;
import com.practice.myapplication.model.ItemProperty;
import com.practice.myapplication.model.Preferences;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FavoriteDetailActivity extends AppCompatActivity {

    Realm realm;
    RealmHelper realmHelper;
    Preferences preferences;
    Bundle extras;
    int id;
    String imageUrl, title, description, releaseDate, voteAverage;
    TextView tv_detailTitle, tv_releaseDate, tv_rating, tv_description;
    ImageView img_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = new Preferences();

        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

        realmHelper = new RealmHelper(realm);

        tv_detailTitle = findViewById(R.id.tv_favoriteDetailTitle);
        tv_releaseDate = findViewById(R.id.tv_favoriteDetailDate);
        tv_rating = findViewById(R.id.tv_favoriteDetailVote);
        tv_description = findViewById(R.id.tv_favoriteDetailDescription);
        img_poster = findViewById(R.id.img_favoriteDetailImage);

        extras = getIntent().getExtras();

        if (extras != null) {
            title = extras.getString("title");
            description = extras.getString("description");
            voteAverage = extras.getString("vote");
            releaseDate = extras.getString("date");
            imageUrl = extras.getString("imageUrl");
            id = extras.getInt("id");
            getSupportActionBar().setTitle(title);
        }

        tv_detailTitle.setText(title);
        tv_description.setText(description);
        tv_rating.setText("Rating: " + voteAverage + " of 10");
        tv_releaseDate.setText("Release: " + releaseDate);
        Picasso.get().load(imageUrl).placeholder(R.drawable.icon).fit().into(img_poster);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}