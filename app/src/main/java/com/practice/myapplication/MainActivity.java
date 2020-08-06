package com.practice.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<ItemProperty> arrayList;
    Button btn_logout;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addData();
        recyclerView = findViewById(R.id.recycler_view);
        btn_logout = findViewById(R.id.btn_logout);
        adapter = new ItemAdapter(arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        preferences = new Preferences();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setLogin(getApplicationContext(), false);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(), "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
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