package com.example.m_hike;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditObservationActivity extends AppCompatActivity {
    private EditText etObservation, etComments;
    private TextView tvTime;
    private DatabaseHelper dbHelper;
    private Observation observation;
    private long observationId;
    private DateTimePickerHelper dateTimePickerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        dbHelper = new DatabaseHelper(this);
        observationId = getIntent().getLongExtra("observation_id", -1);

        if (observationId == -1) {
            Toast.makeText(this, "Error: Observation ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dateTimePickerHelper = new DateTimePickerHelper();

        etObservation = findViewById(R.id.etObservation);
        etComments = findViewById(R.id.etComments);
        tvTime = findViewById(R.id.tvTime);

        loadObservationData();

        tvTime.setOnClickListener(v -> dateTimePickerHelper.showDateTimePicker(this, tvTime));

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setText("Update Observation");
        btnSubmit.setOnClickListener(v -> updateObservation());
    }

    private void loadObservationData() {
        observation = dbHelper.getObservation(observationId);
        if (observation == null) {
            Toast.makeText(this, "Observation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etObservation.setText(observation.getObservation());
        tvTime.setText(observation.getTime());
        dateTimePickerHelper.setDateTime(observation.getTime());
        if (observation.getAdditionalComments() != null) {
            etComments.setText(observation.getAdditionalComments());
        }
    }

    private void updateObservation() {
        String observationText = etObservation.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        if (TextUtils.isEmpty(observationText)) {
            etObservation.setError("Observation is required");
            etObservation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Time is required", Toast.LENGTH_SHORT).show();
            return;
        }

        observation.setObservation(observationText);
        observation.setTime(time);
        observation.setAdditionalComments(comments);

        int result = dbHelper.updateObservation(observation);
        if (result > 0) {
            Toast.makeText(this, "Observation updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating observation", Toast.LENGTH_SHORT).show();
        }
    }
}

