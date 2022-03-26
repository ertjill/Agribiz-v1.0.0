package com.example.agribiz_v100.services;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.customer.Profile;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.context.AttributeContext;

import java.io.File;
import java.net.URI;

public class ProfileManagement {

    private static final String TAG = "ProfileManagement";

    public static Task<Void> createAccountProfile(UserModel user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(user.getUserID())
        .set(user);
    }

    public static StorageTask<UploadTask.TaskSnapshot> updateImage(Activity activity, Uri imageURI, FirebaseUser user)  {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference imageProfile = db.collection("users").document(user.getUid());

        return StorageManagement.uploadImage("profile", imageURI, user.getUid())
            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri image = task.getResult();
                                    Log.d(TAG, image.toString());
                                    imageProfile.update("userImage", image.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setPhotoUri(image)
                                                        .build();
                                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            AuthValidation.successToast(activity, "Successfully updated profile").show();
                                                        } else {
                                                            AuthValidation.failedToast(activity, "Failed to update profile.");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    AuthValidation.failedToast(activity, "Failed to update profile.");
                                }
                            }
                        });
                    } else {
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
