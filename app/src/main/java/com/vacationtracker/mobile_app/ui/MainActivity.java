package com.vacationtracker.mobile_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.database.Repository;
import com.vacationtracker.mobile_app.entities.Excursion;
import com.vacationtracker.mobile_app.entities.Vacation;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Repository repository;
    private TextView viewDataText;
    private TextView createDataText;
    private Button viewDataButton1;
    private Button viewDataButton2;
    private Button createDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize repository
        repository = new Repository(getApplication());

        // Initialize UI elements
        viewDataText = findViewById(R.id.viewDataText);
        createDataText = findViewById(R.id.createDataText);
        viewDataButton1 = findViewById(R.id.viewDataButton1);
        viewDataButton2 = findViewById(R.id.viewDataButton2);
        createDataButton = findViewById(R.id.createDataButton);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up button click listeners
        viewDataButton1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VacationList.class);
            startActivity(intent);
        });

        viewDataButton2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExcursionList.class);
            startActivity(intent);
        });

        createDataButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VacationDetails.class);
            startActivity(intent);
        });

        // Observe data changes
        repository.getAllVacation().observe(this, vacations -> updateUI());
        repository.getAllExcursion().observe(this, excursions -> updateUI());
    }

    private void updateUI() {
        repository.getAllVacation().observe(this, vacations -> {
            repository.getAllExcursion().observe(this, excursions -> {
                boolean hasVacations = vacations != null && !vacations.isEmpty();
                boolean hasExcursions = excursions != null && !excursions.isEmpty();

                runOnUiThread(() -> {
                    if (hasVacations || hasExcursions) {
                        // Show/hide view data elements based on specific data presence
                        viewDataText.setVisibility(hasVacations || hasExcursions ? View.VISIBLE : View.GONE);
                        viewDataButton1.setVisibility(hasVacations ? View.VISIBLE : View.GONE);
                        viewDataButton2.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
                        
                        // Hide create data elements
                        createDataText.setVisibility(View.GONE);
                        createDataButton.setVisibility(View.GONE);
                    } else {
                        // Hide all view data elements
                        viewDataText.setVisibility(View.GONE);
                        viewDataButton1.setVisibility(View.GONE);
                        viewDataButton2.setVisibility(View.GONE);
                        
                        // Show create data elements
                        createDataText.setVisibility(View.VISIBLE);
                        createDataButton.setVisibility(View.VISIBLE);
                    }
                });
            });
        });
    }
}
