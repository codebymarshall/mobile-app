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
import com.vacationtracker.mobile_app.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
private Repository repository;
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

        FloatingActionButton fab=findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent intent=new Intent (VacationList.this,VacationDetails.class);
            startActivity(intent);
        });

        findViewById(R.id.button_to_excursion_list).setOnClickListener(view -> {
            Intent intent = new Intent(VacationList.this, ExcursionList.class);
            startActivity(intent);
        });

        System.out.println(getIntent().getStringExtra("test"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            this.finish();
           Intent intent=new Intent(VacationList.this, MainActivity.class);
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

    List<Vacation> allVacations = repository.getAllVacation();
    for (Vacation vacation : allVacations) {
        Log.d("VacationList", "Vacation ID: " + vacation.getVacationID() + ", Name: " + vacation.getVacationName());
    }
    RecyclerView recyclerView = findViewById(R.id.recyclerview);

    if (recyclerView.getAdapter() == null) {
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    VacationAdapter adapter = (VacationAdapter) recyclerView.getAdapter();
    adapter.setVacations(allVacations);
    adapter.notifyDataSetChanged();
}

}
