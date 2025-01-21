package com.vacationtracker.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vacationtracker.mobile.R;
import com.vacationtracker.mobile.database.Repository;

public class MainActivity extends AppCompatActivity {
    private Repository repository;
    private View viewDataCard;
    private View createDataCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize repository
        repository = new Repository(getApplication());

        // Initialize UI elements
        viewDataCard = findViewById(R.id.viewDataCard);
        createDataCard = findViewById(R.id.createDataCard);

        // Set up edge-to-edge content
        View mainContent = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Set up button click listeners
        findViewById(R.id.viewDataButton1).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, VacationList.class));
        });

        findViewById(R.id.viewDataButton2).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ExcursionList.class));
        });

        findViewById(R.id.createDataButton).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, VacationDetails.class));
        });

        // Observe data changes
        observeData();
    }

    private void observeData() {
        repository.getAllVacation().observe(this, vacations -> {
            repository.getAllExcursion().observe(this, excursions -> {
                boolean hasVacations = vacations != null && !vacations.isEmpty();
                boolean hasExcursions = excursions != null && !excursions.isEmpty();

                runOnUiThread(() -> {
                    viewDataCard.setVisibility(hasVacations || hasExcursions ? View.VISIBLE : View.GONE);
                    createDataCard.setVisibility(hasVacations || hasExcursions ? View.GONE : View.VISIBLE);
                });
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData(); // Refresh UI when returning to this activity
    }
}
