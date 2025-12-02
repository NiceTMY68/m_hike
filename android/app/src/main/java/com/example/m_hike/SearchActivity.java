package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearchName, etSearchLocation, etSearchLength, etSearchDate;
    private TextInputLayout tilSearchLocation, tilSearchLength, tilSearchDate;
    private RadioGroup rgSearchType;
    private ListView listViewResults;
    private DatabaseHelper dbHelper;
    private List<Hike> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);

        etSearchName = findViewById(R.id.etSearchName);
        etSearchLocation = findViewById(R.id.etSearchLocation);
        etSearchLength = findViewById(R.id.etSearchLength);
        etSearchDate = findViewById(R.id.etSearchDate);
        tilSearchLocation = findViewById(R.id.tilSearchLocation);
        tilSearchLength = findViewById(R.id.tilSearchLength);
        tilSearchDate = findViewById(R.id.tilSearchDate);
        rgSearchType = findViewById(R.id.rgSearchType);
        listViewResults = findViewById(R.id.listViewResults);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> performSearch());

        // Show/hide fields based on search type
        rgSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSimple) {
                // Simple search - show only name field
                tilSearchLocation.setVisibility(View.GONE);
                tilSearchLength.setVisibility(View.GONE);
                tilSearchDate.setVisibility(View.GONE);
            } else {
                // Advanced search - show all fields
                tilSearchLocation.setVisibility(View.VISIBLE);
                tilSearchLength.setVisibility(View.VISIBLE);
                tilSearchDate.setVisibility(View.VISIBLE);
            }
        });

        // Default to simple search
        rgSearchType.check(R.id.rbSimple);
    }

    private void performSearch() {
        int selectedId = rgSearchType.getCheckedRadioButtonId();
        
        if (selectedId == R.id.rbSimple) {
            // Simple search by name
            String name = etSearchName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name to search", Toast.LENGTH_SHORT).show();
                return;
            }
            searchResults = dbHelper.searchHikesByName(name);
        } else {
            // Advanced search
            String name = etSearchName.getText().toString().trim();
            String location = etSearchLocation.getText().toString().trim();
            String length = etSearchLength.getText().toString().trim();
            String date = etSearchDate.getText().toString().trim();

            if (name.isEmpty() && location.isEmpty() && length.isEmpty() && date.isEmpty()) {
                Toast.makeText(this, "Please enter at least one search criteria", Toast.LENGTH_SHORT).show();
                return;
            }

            searchResults = dbHelper.advancedSearch(
                    name.isEmpty() ? null : name,
                    location.isEmpty() ? null : location,
                    length.isEmpty() ? null : length,
                    date.isEmpty() ? null : date
            );
        }

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No hikes found", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Hike> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, searchResults);
        listViewResults.setAdapter(adapter);

        listViewResults.setOnItemClickListener((parent, view, position, id) -> {
            Hike selectedHike = searchResults.get(position);
            Intent intent = new Intent(SearchActivity.this, HikeDetailsActivity.class);
            intent.putExtra("hike_id", selectedHike.getId());
            startActivity(intent);
        });
    }
}

