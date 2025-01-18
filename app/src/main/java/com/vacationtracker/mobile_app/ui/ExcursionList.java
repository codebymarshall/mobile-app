package com.vacationtracker.mobile_app.ui;

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

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.database.Repository;
import com.vacationtracker.mobile_app.entities.Excursion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExcursionList extends AppCompatActivity {
    private Repository repository;

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

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ExcursionList.this, ExcursionDetails.class);
            startActivity(intent);
        });

        findViewById(R.id.button_to_vacation_list).setOnClickListener(view -> {
            Intent intent = new Intent(ExcursionList.this, VacationList.class);
            startActivity(intent);
        });

        System.out.println(getIntent().getStringExtra("test"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
           Intent intent=new Intent(ExcursionList.this, MainActivity.class);
           startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (repository == null) {
            repository = new Repository(getApplication());
        }

        List<Excursion> allExcursions = repository.getAllExcursion();
        for (Excursion excursion : allExcursions) {
            Log.d("ExcursionList", "Excursion ID: " + excursion.getExcursionID() + ", Name: " + excursion.getExcursionName());
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        if (recyclerView.getAdapter() == null) {
            final ExcursionNameAdapter excursionNameAdapter = new ExcursionNameAdapter(this);
            recyclerView.setAdapter(excursionNameAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        ExcursionNameAdapter adapter = (ExcursionNameAdapter) recyclerView.getAdapter();
        adapter.setExcursions(allExcursions);
        adapter.notifyDataSetChanged();
    }
}