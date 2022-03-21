package com.example.agribiz_v100.controller;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.google.firebase.auth.FirebaseUser;

public class AuthController {

    public static void loginNavigation(FirebaseUser user, Activity activity) {
        
        if (user != null) {
            if (!TextUtils.isEmpty(user.getDisplayName()) && user.getDisplayName() != null) {
                char c = user.getDisplayName().charAt(user.getDisplayName().length() - 1);
                if (c == 'c') {
                    activity.startActivity(new Intent(activity.getApplicationContext(), CustomerMainActivity.class));
                    activity.finish();
                } else if (c == 'f') {
                    activity.startActivity(new Intent(activity.getApplicationContext(), FarmerMainActivity.class));
                    activity.finish();
                } else {
                    activity.startActivity(new Intent(activity.getApplicationContext(), AgrovitMainActivity.class));
                    activity.finish();
                }
            }
        }
    }
}
