package com.vacationtracker.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vacationtracker.mobile.R;
import com.vacationtracker.mobile.database.Repository;
import com.vacationtracker.mobile.entities.Excursion;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ExcursionList extends AppCompatActivity {
    private Repository repository;
    private ExcursionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());

        ExtendedFloatingActionButton fab = findViewById(R.id.floatingActionButton4);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ExcursionList.this, ExcursionDetails.class);
            startActivity(intent);
        });

        findViewById(R.id.viewVacations).setOnClickListener(view -> {
            Intent intent = new Intent(ExcursionList.this, VacationList.class);
            startActivity(intent);
        });

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe excursion data changes
        repository.getAllExcursion().observe(this, excursions -> {
            for (Excursion excursion : excursions) {
                Log.d("ExcursionList", "Excursion ID: " + excursion.getExcursionID() + ", Name: " + excursion.getExcursionName());
            }
            adapter.setExcursions(excursions);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}