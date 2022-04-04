package com.example.agribiz_v100.services;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageManagement {

    public static UploadTask uploadImage(String path, Uri imageURI, String userID) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child(path + "/" + userID);
        UploadTask uploadTask = pathRef.putFile(imageURI);
        return uploadTask;
    }
}
