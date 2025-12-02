package com.example.m_hike;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditHikeActivity extends AppCompatActivity {
    private EditText etName, etLocation, etDate, etLength, etDescription, etWeather, etDuration;
    private RadioGroup rgParking;
    private Spinner spinnerDifficulty;
    private DatabaseHelper dbHelper;
    private Hike hike;
    private long hikeId;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        dbHelper = new DatabaseHelper(this);
        hikeId = getIntent().getLongExtra("hike_id", -1);

        if (hikeId == -1) {
            Toast.makeText(this, "Error loading hike", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        etWeather = findViewById(R.id.etWeather);
        etDuration = findViewById(R.id.etDuration);
        rgParking = findViewById(R.id.rgParking);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);

        // Setup difficulty spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());

        loadHikeData();

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setText("Update Hike");
        btnSubmit.setOnClickListener(v -> validateAndConfirm());
    }

    private void loadHikeData() {
        hike = dbHelper.getHike(hikeId);
        if (hike == null) {
            Toast.makeText(this, "Hike not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(hike.getName());
        etLocation.setText(hike.getLocation());
        etDate.setText(hike.getDate());
        etLength.setText(hike.getLength());
        if (hike.getDescription() != null) {
            etDescription.setText(hike.getDescription());
        }
        if (hike.getWeather() != null) {
            etWeather.setText(hike.getWeather());
        }
        if (hike.getEstimatedDuration() != null) {
            etDuration.setText(hike.getEstimatedDuration());
        }

        // Set parking radio button
        if (hike.getParkingAvailable().equalsIgnoreCase("Yes")) {
            rgParking.check(R.id.rbYes);
        } else {
            rgParking.check(R.id.rbNo);
        }

        // Set difficulty spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerDifficulty.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(hike.getDifficulty())) {
                spinnerDifficulty.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void validateAndConfirm() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String weather = etWeather.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        int selectedParkingId = rgParking.getCheckedRadioButtonId();
        if (selectedParkingId == -1) {
            Toast.makeText(this, "Please select parking availability", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedParking = findViewById(selectedParkingId);
        String parking = selectedParking.getText().toString();

        if (TextUtils.isEmpty(length)) {
            etLength.setError("Length is required");
            etLength.requestFocus();
            return;
        }

        if (spinnerDifficulty.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select difficulty level", Toast.LENGTH_SHORT).show();
            return;
        }
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        // Show confirmation dialog
        showConfirmationDialog(name, location, date, parking, length, difficulty, description, weather, duration);
    }

    private void showConfirmationDialog(String name, String location, String date, String parking,
                                       String length, String difficulty, String description,
                                       String weather, String duration) {
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(name).append("\n");
        details.append("Location: ").append(location).append("\n");
        details.append("Date: ").append(date).append("\n");
        details.append("Parking: ").append(parking).append("\n");
        details.append("Length: ").append(length).append("\n");
        details.append("Difficulty: ").append(difficulty).append("\n");
        if (!description.isEmpty()) {
            details.append("Description: ").append(description).append("\n");
        }
        if (!weather.isEmpty()) {
            details.append("Weather: ").append(weather).append("\n");
        }
        if (!duration.isEmpty()) {
            details.append("Estimated Duration: ").append(duration).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Hike Details")
                .setMessage(details.toString())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    updateHike(name, location, date, parking, length, difficulty, description, weather, duration);
                })
                .setNegativeButton("Edit", (dialog, which) -> {
                    // User wants to go back and edit
                })
                .show();
    }

    private void updateHike(String name, String location, String date, String parking,
                           String length, String difficulty, String description,
                           String weather, String duration) {
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParkingAvailable(parking);
        hike.setLength(length);
        hike.setDifficulty(difficulty);
        hike.setDescription(description);
        hike.setWeather(weather);
        hike.setEstimatedDuration(duration);

        int result = dbHelper.updateHike(hike);
        if (result > 0) {
            Toast.makeText(this, "Hike updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating hike", Toast.LENGTH_SHORT).show();
        }
    }
}

