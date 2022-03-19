package com.example.agribiz_v100.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.Verification;
import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class AuthManagement {

    // Class properties
    private static final String TAG = "Authentication";
    private FirebaseAuth mAuth; // Firebase authentication module access
    //

    public void registerAccount() {

    }

    public void verifyPhoneNumber() {

    }

    public void createAccountProfile() {

    }

    public Task<AuthResult> loginAccount(String email, String password, Activity activity) {
        // Initialization of Firebase authentication object to be accessible
        mAuth = FirebaseAuth.getInstance();
        // Holds a task that returns AuthResult, which is a FirebaseAuth object
        Task<AuthResult> loginTask = mAuth.signInWithEmailAndPassword(email, password);
        return loginTask;
    }

    public void resetPassword() {

    }

    public void updatePassword() {

    }

    public void updateEmail() {

    }

    public void updatePhoneNumber() {

    }

    public void logoutAccount() {

    }

}
