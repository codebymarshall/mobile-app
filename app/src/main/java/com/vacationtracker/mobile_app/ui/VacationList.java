package com.vacationtracker.mobile_app.ui;

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

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.database.Repository;
import com.vacationtracker.mobile_app.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private Button viewExcursionsButton;
    private Button createExcursionButton;
    private FloatingActionButton createVacationButton;

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

        // Update UI based on data availability
        updateUI();
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

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // Update RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        List<Vacation> allVacations = repository.getAllVacation();
        
        if (recyclerView.getAdapter() == null) {
            final VacationAdapter vacationAdapter = new VacationAdapter(this);
            recyclerView.setAdapter(vacationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        VacationAdapter adapter = (VacationAdapter) recyclerView.getAdapter();
        adapter.setVacations(allVacations);
        adapter.notifyDataSetChanged();

        // Update button visibility based on excursions
        boolean hasExcursions = !repository.getAllExcursion().isEmpty();
        
        // Show viewExcursions button only if there are excursions
        viewExcursionsButton.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
        
        // Show createExcursion button only if there are NO excursions
        createExcursionButton.setVisibility(hasExcursions ? View.GONE : View.VISIBLE);

        // Log for debugging
        for (Vacation vacation : allVacations) {
            Log.d("VacationList", "Vacation ID: " + vacation.getVacationID() + ", Name: " + vacation.getVacationName());
        }
    }
}
