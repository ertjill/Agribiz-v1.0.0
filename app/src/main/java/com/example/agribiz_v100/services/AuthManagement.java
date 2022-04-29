package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.controller.AuthController;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        Log.d(TAG, "Register account method");
    }

    public void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity)              // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d(TAG, "Verify phone number method");
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                // Run when Firebase send an OTP code
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                    Log.d(TAG, "On code sent method");
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
                    Log.d(TAG, "On verification failed", e);
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("Verify Phone Number:");
                    alert.setMessage(e.getMessage());
                    alert.setPositiveButton("Ok", null);
                    alert.show();
                }
            };

    public Task<AuthResult> verifyCode(String code) {
        // Verify code sent by Firebase if it will match to the code inputted by user
        // then create account linked with the phone number
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        // User is log on with the phone number authentication account
        Log.d(TAG, "Verify code method");
        return signInWithCredential(credential);

    }

    public Task<AuthResult> createEmailAccount(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    private Task<AuthResult> signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        //Create credential using phone number
        //if phone credential is successfully created create email and password credential
        //link phone credential to email credential
        //else delete created credentials
        return mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task1) {
                if (task1.isSuccessful()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getUserEmail(), userPassword);
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task2) {
                            if (task2.isSuccessful()) {
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
                                            ProfileManagement.createAccountProfile(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        AuthController.loginNavigation(fuser, activity);
                                                        AuthValidation.successToast(activity, "Account successfully created");
                                                    } else {
                                                        deleteAccount(fuser);
                                                        AuthValidation.failedToast(activity, "Failed to create account.");
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                        alert.setTitle("Invalid Credential");
                                                        alert.setMessage(task.getException().getLocalizedMessage());
                                                        alert.setPositiveButton("OK", null);
                                                        alert.show();
                                                    }
                                                }
                                            });
                                        } else {
                                            deleteAccount(mAuth.getCurrentUser());
                                            Toast.makeText(activity, "Failed to create account.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                logoutAccount();
                                Log.d(TAG, task2.getException().getMessage());
                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                alert.setTitle("Invalid Credential");
                                alert.setMessage(task2.getException().getLocalizedMessage());
                                alert.setPositiveButton("OK", null);
                                alert.show();
                            }
                        }
                    });

                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("Invalid OTP Code");
                    alert.setMessage(task1.getException().getLocalizedMessage());
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
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

    public Task<Void> updateUsername(String username) {
        FirebaseUser user = mAuth.getCurrentUser();
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating Username, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        return user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference usernameRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                    usernameRef.update("userDisplayName", mAuth.getCurrentUser().getDisplayName())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        AuthValidation.successToast(activity, "Successfully updated username");
                                    } else {
                                        AuthValidation.failedToast(activity, "Failed to update username");
                                    }
                                }
                            });
                } else {
                    AuthValidation.failedToast(activity, "Failed to update email");
                }
                progressDialog.dismiss();
            }
        });

    }

    public Task<Void> updatePassword(String password) {
        return mAuth.getCurrentUser().updatePassword(password);
    }

    public Task<Void> updateEmail(String email) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating Email, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference emailRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                    emailRef.update("userEmail", email);
                } else {
                    AuthValidation.failedToast(activity, "Failed to update email");
                }
                progressDialog.dismiss();
            }
        });
    }

    // Update phone number
    public Task<Void> updatePhoneNumber(String code) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating Phone Number, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("PhoneNumber", "Verifying " + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);

        Log.d("PhoneNumber", "Update phone number credential " + code);
        // Stop here...
        return mAuth.getCurrentUser().updatePhoneNumber(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference mobileRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                            mobileRef.update("userPhoneNumber", mAuth.getCurrentUser().getPhoneNumber())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if (task1.isSuccessful()) {
                                                AuthValidation.successToast(activity, "Successfully updated phone number");
                                            } else {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                alert.setTitle("Update Phone Number:");
                                                alert.setMessage(task1.getException().getMessage());
                                                alert.setPositiveButton("Ok", null);
                                                alert.show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("Update Phone Number:");
                            alert.setMessage(task.getException().getMessage());
                            alert.setPositiveButton("Ok", null);
                            alert.show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public static void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
    }

}
