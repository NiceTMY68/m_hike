package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HikeDetailsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Hike hike;
    private long hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);

        dbHelper = new DatabaseHelper(this);
        hikeId = getIntent().getLongExtra("hike_id", -1);

        if (hikeId == -1) {
            Toast.makeText(this, "Error loading hike", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHikeDetails();

        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnAddObservation = findViewById(R.id.btnAddObservation);
        Button btnViewObservations = findViewById(R.id.btnViewObservations);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(HikeDetailsActivity.this, EditHikeActivity.class);
            intent.putExtra("hike_id", hikeId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        btnAddObservation.setOnClickListener(v -> {
            Intent intent = new Intent(HikeDetailsActivity.this, AddObservationActivity.class);
            intent.putExtra("hike_id", hikeId);
            startActivity(intent);
        });

        btnViewObservations.setOnClickListener(v -> {
            Intent intent = new Intent(HikeDetailsActivity.this, ViewObservationsActivity.class);
            intent.putExtra("hike_id", hikeId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHikeDetails();
    }

    private void loadHikeDetails() {
        hike = dbHelper.getHike(hikeId);
        if (hike == null) {
            Toast.makeText(this, "Hike not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tvName);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvParking = findViewById(R.id.tvParking);
        TextView tvLength = findViewById(R.id.tvLength);
        TextView tvDifficulty = findViewById(R.id.tvDifficulty);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvWeather = findViewById(R.id.tvWeather);
        TextView tvDuration = findViewById(R.id.tvDuration);

        tvName.setText("Name: " + hike.getName());
        tvLocation.setText("Location: " + hike.getLocation());
        tvDate.setText("Date: " + hike.getDate());
        tvParking.setText("Parking: " + hike.getParkingAvailable());
        tvLength.setText("Length: " + hike.getLength() + " km");
        tvDifficulty.setText("Difficulty: " + hike.getDifficulty());
        tvDescription.setText("Description: " + (hike.getDescription() != null && !hike.getDescription().isEmpty() ? hike.getDescription() : "N/A"));
        tvWeather.setText("Weather: " + (hike.getWeather() != null && !hike.getWeather().isEmpty() ? hike.getWeather() : "N/A"));
        tvDuration.setText("Estimated Duration: " + (hike.getEstimatedDuration() != null && !hike.getEstimatedDuration().isEmpty() ? hike.getEstimatedDuration() + " hours" : "N/A"));
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Hike")
                .setMessage("Are you sure you want to delete this hike? All observations will also be deleted.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteHike(hikeId);
                    Toast.makeText(this, "Hike deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

