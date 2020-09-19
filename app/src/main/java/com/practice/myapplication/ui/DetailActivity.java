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

public class DetailActivity extends AppCompatActivity {

    Realm realm;
    RealmHelper realmHelper;
    Preferences preferences;
    Bundle extras;
    int id;
    String imageUrl, title, description, releaseDate, voteAverage;
    boolean isFavorite;
    TextView tv_detailTitle, tv_releaseDate, tv_rating, tv_description, tv_favorite;
    ImageView img_poster;
    Button btn_detailFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = new Preferences();

        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

        realmHelper = new RealmHelper(realm);

        tv_detailTitle = findViewById(R.id.tv_detailTitle);
        tv_releaseDate = findViewById(R.id.tv_detailDate);
        tv_rating = findViewById(R.id.tv_detailVote);
        tv_description = findViewById(R.id.tv_detailDescription);
        tv_favorite = findViewById(R.id.tv_detailFavorite);
        img_poster = findViewById(R.id.img_detailImage);
        btn_detailFavorite = findViewById(R.id.btn_detailFavorite);

        extras = getIntent().getExtras();

        if (extras != null) {
            title = extras.getString("title");
            description = extras.getString("description");
            voteAverage = extras.getString("vote");
            releaseDate = extras.getString("date");
            imageUrl = extras.getString("imageUrl");
            isFavorite = extras.getBoolean("favorite");
            id = extras.getInt("id");
            getSupportActionBar().setTitle(title);
        }

        tv_detailTitle.setText(title);
        tv_description.setText(description);
        tv_rating.setText("Rating: " + voteAverage + " of 10");
        tv_releaseDate.setText("Release: " + releaseDate);
        Picasso.get().load(imageUrl).placeholder(R.drawable.icon).fit().into(img_poster);
        if (isFavorite) {
            tv_favorite.setText("This is your favorite movie!");
            btn_detailFavorite.setText("Remove from Favorites");
        } else {
            tv_favorite.setText("Not your favorite movie");
            btn_detailFavorite.setText("Add to Favorites");
        }

        btn_detailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemProperty itemProperty = new ItemProperty(id, imageUrl, title, description, releaseDate, voteAverage, isFavorite);
                if (isFavorite) {
                    Log.d("Delete", "yes");
                    realmHelper.delete(itemProperty);
                    btn_detailFavorite.setText("Add to Favorites");
                    tv_favorite.setText("Not your favorite movie");
                    isFavorite = false;
                    Toast.makeText(getApplicationContext(), "Movie has been removed from favorites.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Save", "yes");
                    realmHelper.save(itemProperty);
                    btn_detailFavorite.setText("Remove from Favorites");
                    tv_favorite.setText("This is your favorite movie!");
                    isFavorite = true;
                    Toast.makeText(getApplicationContext(), "Movie has been added to favorites.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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