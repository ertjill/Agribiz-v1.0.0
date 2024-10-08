package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class ProfileManagement {

    private static final String TAG = "ProfileManagement";

    public static Task<Void> createAccountProfile(UserModel user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(user.getUserID())
                .set(user);
    }

    public static StorageTask<UploadTask.TaskSnapshot> updateImage(Activity activity, Uri imageURI, FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference imageProfile = db.collection("users").document(user.getUid());
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Uploading image, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return StorageManagement.uploadImage("profile", imageURI, user.getUid())
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task1) {
                                    if (task1.isSuccessful()) {
                                        Uri image = task1.getResult();
                                        Log.d(TAG, image.toString());
                                        imageProfile.update("userImage", image.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task2) {
                                                if (task2.isSuccessful()) {
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(image)
                                                            .build();
                                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task3) {
                                                            if (task3.isSuccessful()) {
                                                                AuthValidation.successToast(activity, "Successfully updated profile").show();
                                                            } else {
                                                                //AuthValidation.failedToast(activity, "Failed to update profile.").show();
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                                alert.setTitle("Update Profile:");
                                                                alert.setMessage(task3.getException().getMessage());
                                                                alert.setPositiveButton("Ok", null);
                                                                alert.show();
                                                            }
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                                else{
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                    alert.setTitle("Update Profile");
                                                    alert.setMessage(task2.getException().getMessage());
                                                    alert.setPositiveButton("Okay", null);
                                                    alert.show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        //AuthValidation.failedToast(activity, task.getException().getMessage()).show();
                                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                        alert.setTitle("Update Profile");
                                        alert.setMessage(task1.getException().getMessage());
                                        alert.setPositiveButton("Okay", null);
                                        alert.show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            //AuthValidation.failedToast(activity, "Failed to update profile").show();
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("Update Profile");
                            alert.setMessage(task.getException().getMessage());
                            alert.setPositiveButton("Okay", null);
                            alert.show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    public static Task<Void> addAddress(Activity activity, LocationModel location, FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference locationRef = db.collection("users").document(user.getUid());
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                int i = 0;
                DocumentSnapshot snapshot = transaction.get(locationRef);
                List<LocationModel> address = (List<LocationModel>) snapshot.getData().get("userLocation");
                if (address != null)
                    i = address.size();
                if (i < 3) {
                    Log.d("ads", "added location");
                    locationRef.update("userLocation", FieldValue.arrayUnion(location));
                } else {
                    throw new FirebaseFirestoreException("Address exceeds limit",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AuthValidation.successToast(activity, "Successfully added address");
                    Log.d("ads", "Successfully added address");
                } else {
                    AuthValidation.failedToast(activity, task.getException().getMessage());
                    Log.d("ads", task.getException().getMessage());
                }
            }
        });
    }

    public static Task<Void> deleteAddress(LocationModel location, FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference locationRef = db.collection("users").document(user.getUid());
        return locationRef.update("userLocation", FieldValue.arrayRemove(location));
    }

    public static DocumentReference getAddress(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference locationRef = db.collection("users").document(userID);
        return locationRef;
    }

    public static Task<DocumentSnapshot> getUserProfile(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(userId).get();
    }
}
