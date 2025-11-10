package com.example.m_hike;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddObservationActivity extends AppCompatActivity {
    private EditText etObservation, etComments;
    private TextView tvTime;
    private DatabaseHelper dbHelper;
    private long hikeId;
    private DateTimePickerHelper dateTimePickerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        dbHelper = new DatabaseHelper(this);
        hikeId = getIntent().getLongExtra("hike_id", -1);

        if (hikeId == -1) {
            Toast.makeText(this, "Error: Hike ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dateTimePickerHelper = new DateTimePickerHelper();

        etObservation = findViewById(R.id.etObservation);
        etComments = findViewById(R.id.etComments);
        tvTime = findViewById(R.id.tvTime);

        // Set default time to current date and time
        tvTime.setText(dateTimePickerHelper.getCurrentDateTime());

        tvTime.setOnClickListener(v -> dateTimePickerHelper.showDateTimePicker(this, tvTime));

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> saveObservation());
    }

    private void saveObservation() {
        String observation = etObservation.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        if (TextUtils.isEmpty(observation)) {
            etObservation.setError("Observation is required");
            etObservation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Time is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Observation obs = new Observation(hikeId, observation, time, comments);
        long id = dbHelper.addObservation(obs);
        if (id > 0) {
            Toast.makeText(this, "Observation added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding observation", Toast.LENGTH_SHORT).show();
        }
    }
}

