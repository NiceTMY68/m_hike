package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddHike = findViewById(R.id.btnAddHike);
        Button btnViewHikes = findViewById(R.id.btnViewHikes);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnAddHike.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);
            startActivity(intent);
        });

        btnViewHikes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewHikesActivity.class);
            startActivity(intent);
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
}