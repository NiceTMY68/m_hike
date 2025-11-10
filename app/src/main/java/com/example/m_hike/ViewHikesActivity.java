package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewHikesActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private List<Hike> hikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hikes);

        listView = findViewById(R.id.listViewHikes);
        dbHelper = new DatabaseHelper(this);

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> showResetConfirmation());

        loadHikes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHikes();
    }

    private void loadHikes() {
        hikes = dbHelper.getAllHikes();
        if (hikes.isEmpty()) {
            Toast.makeText(this, "No hikes found", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Hike> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, hikes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Hike selectedHike = hikes.get(position);
            Intent intent = new Intent(ViewHikesActivity.this, HikeDetailsActivity.class);
            intent.putExtra("hike_id", selectedHike.getId());
            startActivity(intent);
        });
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Database")
                .setMessage("Are you sure you want to delete all hikes? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteAllHikes();
                    Toast.makeText(this, "All hikes deleted", Toast.LENGTH_SHORT).show();
                    loadHikes();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

