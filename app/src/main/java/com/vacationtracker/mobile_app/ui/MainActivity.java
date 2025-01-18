package com.vacationtracker.mobile_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vacationtracker.mobile_app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button=findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,VacationList.class);
            startActivity(intent);
        });
        Button button2=findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,ExcursionList.class);
            startActivity(intent);
        });
    }
}
