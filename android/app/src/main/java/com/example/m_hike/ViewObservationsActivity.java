package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewObservationsActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private long hikeId;
    private List<Observation> observations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_observations);

        dbHelper = new DatabaseHelper(this);
        hikeId = getIntent().getLongExtra("hike_id", -1);

        if (hikeId == -1) {
            Toast.makeText(this, "Error: Hike ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listView = findViewById(R.id.listViewObservations);
        loadObservations();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Observation selectedObs = observations.get(position);
            showObservationOptions(selectedObs);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObservations();
    }

    private void loadObservations() {
        observations = dbHelper.getObservationsByHikeId(hikeId);
        if (observations.isEmpty()) {
            Toast.makeText(this, "No observations found", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Observation> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, observations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                Observation obs = observations.get(position);
                text1.setText(obs.getObservation());
                text2.setText("Time: " + obs.getTime());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void showObservationOptions(Observation observation) {
        String[] options = {"View Details", "Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle("Observation Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showObservationDetails(observation);
                            break;
                        case 1:
                            editObservation(observation);
                            break;
                        case 2:
                            deleteObservation(observation);
                            break;
                    }
                })
                .show();
    }

    private void showObservationDetails(Observation observation) {
        StringBuilder details = new StringBuilder();
        details.append("Observation: ").append(observation.getObservation()).append("\n\n");
        details.append("Time: ").append(observation.getTime()).append("\n\n");
        if (observation.getAdditionalComments() != null && !observation.getAdditionalComments().isEmpty()) {
            details.append("Comments: ").append(observation.getAdditionalComments());
        } else {
            details.append("Comments: N/A");
        }

        new AlertDialog.Builder(this)
                .setTitle("Observation Details")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void editObservation(Observation observation) {
        Intent intent = new Intent(ViewObservationsActivity.this, EditObservationActivity.class);
        intent.putExtra("observation_id", observation.getId());
        startActivity(intent);
    }

    private void deleteObservation(Observation observation) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Observation")
                .setMessage("Are you sure you want to delete this observation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteObservation(observation.getId());
                    Toast.makeText(this, "Observation deleted", Toast.LENGTH_SHORT).show();
                    loadObservations();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

