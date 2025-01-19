package com.vacationtracker.mobile_app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements AuthStateListener {
    private AuthManager authManager;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);
        authManager.setAuthStateListener(this);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up sign-in button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> authManager.signIn(signInLauncher));

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
        if (authManager.isUserSignedIn()) {
            navigateToMainActivity();
        }
        Button button=findViewById(R.id.button3);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onAuthSuccess(FirebaseUser user) {
        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    @Override
    public void onAuthFailed(String error) {
        Toast.makeText(this, "Authentication failed: " + error, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish(); // Prevent returning to login screen with back button
    }
} 