package com.practice.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<ItemProperty> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Product List");

        addData();
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ItemAdapter(arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "Item " + (position + 1) + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addData() {
        arrayList = new ArrayList<>();
        arrayList.add(new ItemProperty(R.drawable.icon, "This is text one"));
        arrayList.add(new ItemProperty(R.drawable.icon, "This is text two"));
        arrayList.add(new ItemProperty(R.drawable.icon, "This is text three"));
        arrayList.add(new ItemProperty(R.drawable.icon, "This is text four"));
        arrayList.add(new ItemProperty(R.drawable.icon, "This is text five"));
    }
}