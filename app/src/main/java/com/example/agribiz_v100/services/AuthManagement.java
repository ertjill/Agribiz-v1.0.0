package com.example.agribiz_v100.services;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.controller.AuthController;
import com.example.agribiz_v100.entities.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class AuthManagement {

    // Class properties
    private static final String TAG = "AuthManagement";
    // Firebase authentication module access
    private final FirebaseAuth mAuth;
    private String codeBySystem;
    private String userPassword;
    private UserModel user;
    Activity activity;

    // Constructor
    public AuthManagement(Activity activity) {
        // Initialization of Firebase authentication object to be accessible
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
    }

    public void registerAccount(UserModel user, String password) {
        // Get filled-out information from Signup fields
        this.user = user;
        this.userPassword = password;
        // For user verification
        verifyPhoneNumber(user.getUserPhoneNumber());
        Log.d(TAG,"Register account method");
    }

    public void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity)              // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d(TAG,"Verify phone number method");
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                // Run when Firebase send an OTP code
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                    Log.d(TAG,"On code sent method");
                }
                // Once code is on process for verification
                // Will only function if the device used is also the device that received the OTP code by Firebase
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    // Fragile codes, do not touch, do not modify something
                    // Get sent OTP code received by the device's phone number
                    // String code = phoneAuthCredential.getSmsCode();
                    // Checks if code is valid or not empty
                    // if (!TextUtils.isEmpty(code)) {
                        // Process verification
                        //verifyCode(code);
                    // }
                    // Log.d(TAG,"On verification complete method");
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.d(TAG,"On verification failed", e);
                }
    };

    private void verifyCode(String code) {
        // Verify code sent by Firebase if it will match to the code inputted by user
        // then create account linked with the phone number
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        // User is log on with the phone number authentication account
        signInWithCredential(credential);
        Log.d(TAG,"Verify code method");
    }

    public Task<AuthResult> createEmailAccount(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        //
        createEmailAccount(user.getUserEmail(), userPassword)
        .addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                AuthController.loginNavigation(mAuth.getCurrentUser(), activity);
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success");

                if (mAuth.getCurrentUser() != null) {
                    // Link email/password account to phone auth credential
                    mAuth.getCurrentUser().linkWithCredential(phoneAuthCredential);
                    Log.d(TAG, "linkWithCredential:success");
                    // Prepare user object
                    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                    //
                    String defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/agribiz-12cc6.appspot.com/o/profile%2F272229741_475164050669220_5648552245273002941_n.png?alt=media&token=781589bc-71bd-4b66-a647-59c0bff5f9e5";
                    Uri uri = Uri.parse(defaultProfileImage);

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user.getUserDisplayName())
                            .setPhotoUri(uri)
                            .build();

                    if (fuser != null) {
                        fuser.updateProfile(profileUpdates).addOnCompleteListener(task11 -> {
                            if (task11.isSuccessful()) {
                                Log.d(TAG, "User profile updated");
                                user.setUserID(fuser.getUid());
                                user.setUserIsActive(true);

                                if (fuser.getPhotoUrl() != null)
                                user.setUserImage(fuser.getPhotoUrl().toString());

                                // Create account...
                                ProfileManagement pm = new ProfileManagement();
                                pm.createAccountProfile(user);
                                AuthController.loginNavigation(fuser, activity);
                            }
                            else {
                                deleteAccount(mAuth.getCurrentUser());
                                Toast.makeText(activity, "Failed to create account.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(activity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteAccount(FirebaseUser user) {
        user.delete()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User account deleted.");
            }
        });
    }

    public Task<AuthResult> loginAccount(String email, String password) {
        // Holds a task that returns AuthResult, which is a FirebaseAuth object
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void verifyReceivedCode(String code) {
        verifyCode(code);
    }

    public static Task<Void> resetPassword(String email) {
        return FirebaseAuth.getInstance().sendPasswordResetEmail(email);
    }

    public void updatePassword() {

    }

    public void updateEmail() {

    }

    public void updatePhoneNumber() {

    }

    public static void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
    }

}
