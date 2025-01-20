package com.vacationtracker.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vacationtracker.mobile.R;
import com.vacationtracker.mobile.database.Repository;
import com.vacationtracker.mobile.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private Button viewExcursionsButton;
    private Button createExcursionButton;
    private FloatingActionButton createVacationButton;
    private VacationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        repository = new Repository(getApplication());

        // Initialize UI elements
        viewExcursionsButton = findViewById(R.id.viewExcursions);
        createExcursionButton = findViewById(R.id.createExcursion);
        createVacationButton = findViewById(R.id.floatingActionButton);

        createVacationButton.setOnClickListener(view -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });

        viewExcursionsButton.setOnClickListener(view -> {
            Intent intent = new Intent(VacationList.this, ExcursionList.class);
            startActivity(intent);
        });

        createExcursionButton.setOnClickListener(view -> {
            Intent intent = new Intent(VacationList.this, ExcursionDetails.class);
            startActivity(intent);
        });

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new VacationAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe data changes
        repository.getAllVacation().observe(this, vacations -> {
            adapter.setVacations(vacations);
            
            // Update UI based on vacation data
            if (vacations == null || vacations.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                createVacationButton.setVisibility(View.VISIBLE);
                viewExcursionsButton.setVisibility(View.GONE);
                createExcursionButton.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                createVacationButton.setVisibility(View.VISIBLE);
                
                // Log vacation data
                for (Vacation vacation : vacations) {
                    Log.d("VacationList", "Vacation ID: " + vacation.getVacationID() + 
                        ", Name: " + vacation.getVacationName());
                }
            }
        });

        repository.getAllExcursion().observe(this, excursions -> {
            boolean hasExcursions = excursions != null && !excursions.isEmpty();
            viewExcursionsButton.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
            createExcursionButton.setVisibility(hasExcursions ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            Intent intent = new Intent(VacationList.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
