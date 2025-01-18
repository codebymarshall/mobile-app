package com.vacationtracker.mobile_app.auth;

import com.google.firebase.auth.FirebaseUser;

public interface AuthStateListener {
    void onAuthSuccess(FirebaseUser user);
    void onAuthFailed(String error);
} 