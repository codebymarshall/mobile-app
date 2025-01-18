package com.vacationtracker.mobile_app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.auth.AuthManager;
import com.vacationtracker.mobile_app.auth.AuthStateListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements AuthStateListener {
    private AuthManager authManager;
    private ActivityResultLauncher<Intent> signInLauncher;

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

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);
        authManager.setAuthStateListener(this);

        // Register activity result launcher
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    authManager.handleSignInResult(task);
                }
            }
        );

        // Check if user is already signed in
        if (!authManager.isUserSignedIn()) {
            showSignInUI();
        } else {
            showMainUI();
        }
    }

    private void showSignInUI() {
        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> 
            authManager.signIn(signInLauncher)
        );
    }

    private void showMainUI() {
        setContentView(R.layout.activity_main);
        // Your existing MainActivity UI setup code
    }

    @Override
    public void onAuthSuccess(FirebaseUser user) {
        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        showMainUI();
    }

    @Override
    public void onAuthFailed(String error) {
        Toast.makeText(this, "Authentication failed: " + error, Toast.LENGTH_SHORT).show();
    }
}
