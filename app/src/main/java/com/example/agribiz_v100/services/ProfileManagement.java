package com.example.agribiz_v100.services;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;

public class ProfileManagement {

    private static final String TAG = "ProfileManagement";

    public static Task<Void> createAccountProfile(UserModel user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(user.getUserID())
        .set(user);
    }

    public static void updateImage(Activity activity, Uri imageURI, String userID) {
        StorageManagement.uploadImage("profile", imageURI, userID).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    AuthValidation.successToast(activity, "Successfully updated profile").show();
                    //Toast.makeText(activity, "Successfully updated profile", Toast.LENGTH_SHORT).show();
                }
                else {
                    AuthValidation.failedToast(activity, "Failed to update profile").show();
                }
            }
        });
    }

    public static void addAddress(LocationModel locationModel) {

    }

    public static void updateAddress(LocationModel locationModel) {

    }

    public static void deleteAddress() {

    }
}
