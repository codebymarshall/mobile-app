package com.vacationtracker.mobile.ui;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.vacationtracker.mobile.R;


public class Login extends AppCompatActivity{
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Make status bar transparent
        Window window = getWindow();
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        // Initialize video background
        setupVideoBackground();

        // Add button click listener
        findViewById(R.id.button3).setOnClickListener(view -> {
            navigateToMainActivity();
        });

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish(); // Prevent returning to login screen with back button
    }
} 