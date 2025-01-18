package com.vacationtracker.mobile_app.auth;

import android.content.Context;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthManager {
    private static AuthManager instance;
    private final GoogleSignInClient googleSignInClient;
    private final FirebaseAuth firebaseAuth;
    private AuthStateListener authStateListener;

    private AuthManager(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        launcher.launch(signInIntent);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            if (authStateListener != null) {
                authStateListener.onAuthFailed(e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && authStateListener != null) {
                        authStateListener.onAuthSuccess(firebaseAuth.getCurrentUser());
                    } else if (authStateListener != null) {
                        authStateListener.onAuthFailed(task.getException().getMessage());
                    }
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
        googleSignInClient.signOut();
    }

    public void setAuthStateListener(AuthStateListener listener) {
        this.authStateListener = listener;
    }

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
} 