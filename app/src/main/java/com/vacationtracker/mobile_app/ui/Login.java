package com.vacationtracker.mobile_app.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;

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
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize video background
        setupVideoBackground();

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

    private void setupVideoBackground() {
        videoView = findViewById(R.id.videoView);
        
        // Set up OnPreparedListener for video scaling
        videoView.setOnPreparedListener(mp -> {
            // Get video dimensions
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            
            // Get screen dimensions
            float screenWidth = videoView.getWidth();
            float screenHeight = videoView.getHeight();
            
            // Calculate scale factors
            float scaleX = screenWidth / videoWidth;
            float scaleY = screenHeight / videoHeight;
            float scale = Math.max(scaleX, scaleY);
            
            // Calculate new dimensions that maintain aspect ratio
            int newWidth = (int) (videoWidth * scale);
            int newHeight = (int) (videoHeight * scale);
            
            // Calculate translation to center the video
            int xOffset = (int) ((newWidth - screenWidth) / 2);
            int yOffset = (int) ((newHeight - screenHeight) / 2);
            
            // Set the new dimensions and position
            android.view.ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
            layoutParams.width = newWidth;
            layoutParams.height = newHeight;
            videoView.setLayoutParams(layoutParams);
            
            // Adjust position to center
            videoView.setTranslationX(-xOffset);
            videoView.setTranslationY(-yOffset);
        });
        
        // Create the video URI
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.output;
        Uri uri = Uri.parse(videoPath);
        
        videoView.setVideoURI(uri);
        videoView.start();
        
        // Loop the video
        videoView.setOnCompletionListener(mp -> videoView.start());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
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