package com.practice.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.practice.myapplication.RealmHelper;
import com.practice.myapplication.adapter.ItemAdapter;
import com.practice.myapplication.model.ItemProperty;
import com.practice.myapplication.R;
import com.practice.myapplication.model.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<ItemProperty> arrayList;
    final String API_KEY = "a008cc3d5b543d48437262c1d83800ec";
    String imageUrl, title, description, releaseDate, voteAverage;
    ProgressBar progressBar;
    Realm realm;
    RealmHelper realmHelper;
    Preferences preferences;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Movie List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = new Preferences();

        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        realmHelper = new RealmHelper(realm);

        AndroidNetworking.initialize(getApplicationContext());

        addData();
    }

    private void addData() {
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        AndroidNetworking.get("https://api.themoviedb.org/3/movie/popular")
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("language", "en-US")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", "yes");
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray resultArray = response.getJSONArray("results");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultObj = resultArray.getJSONObject(i);
                                title = resultObj.getString("title");
                                description = resultObj.getString("overview");
                                imageUrl = "https://image.tmdb.org/t/p/w500/".concat(resultObj.getString("poster_path"));
                                voteAverage = resultObj.getString("vote_average");
                                releaseDate = resultObj.getString("release_date");
                                arrayList.add(new ItemProperty(i, imageUrl, title, description, releaseDate, voteAverage, false));
                                final RealmResults<ItemProperty> model = realm.where(ItemProperty.class).equalTo("description", description).findAll();
                                if (!model.isEmpty()) {
                                    arrayList.get(i).setFavorite(true);
                                }
                            }
                            recyclerView = findViewById(R.id.recycler_view);
                            adapter = new ItemAdapter(MainActivity.this, arrayList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setItemViewCacheSize(20);
                            recyclerView.setDrawingCacheEnabled(true);
                            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                    intent.putExtra("title", arrayList.get(position).getTitle());
                                    intent.putExtra("id", arrayList.get(position).getId());
                                    intent.putExtra("description", arrayList.get(position).getDescription());
                                    intent.putExtra("imageUrl", arrayList.get(position).getImageUrl());
                                    intent.putExtra("vote", arrayList.get(position).getVoteAverage());
                                    intent.putExtra("date", arrayList.get(position).getReleaseDate());
                                    intent.putExtra("favorite", arrayList.get(position).getFavorite());
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary));
                            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(false);
                                            arrayList.clear();
                                            addData();
                                        }
                                    }, 2000);
                                }
                            });
                        } catch (Exception e) {
                            Log.d("Error: ", e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Something error", Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.btn_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    adapter.getFilter().filter(s);
                } catch (Exception e) {
                    Log.d("error", ""+e.toString());
                }
                return false;
            }
        });
        return true;
    }
}