package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.example.agribiz_v100.farmer.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity...";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //FirebaseAuth.getInstance().signOut();
        startHeavyProcessing();
    }

    private void startHeavyProcessing() {
        new LongOperation().execute();
    }

    private class LongOperation extends AsyncTask<String, Void, Bundle> {
        @Override
        protected Bundle doInBackground(String... params) {
            Bundle bundle = new Bundle();
//            char userType = 'd';
//            if (user != null) {
//                userType = user.getDisplayName().charAt(user.getDisplayName().length() - 1);
//            }
//
//            switch (userType) {
//                case 'c':
//                    bundle.putParcelable("user", user);
//                    break;
//                case 'f':
//                    bundle.putParcelable("user", user);
//                    break;
//
//                case 'a':
//                    bundle.putParcelable("user", user);
//                    break;
//            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            char userType = 'd';
            if (user != null)
                userType = user.getDisplayName().charAt(user.getDisplayName().length() - 1);

            switch (userType) {
                case 'c':
                    Intent intent = new Intent(getBaseContext(), CustomerMainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case 'f':
                    Intent intent1 = new Intent(getBaseContext(), FarmerMainActivity.class);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    finish();
                    break;
                case 'a':
                    Intent intent2 = new Intent(getBaseContext(), AgrovitMainActivity.class);
                    intent2.putExtras(bundle);
                    startActivity(intent2);
                    finish();
                    break;
                default:
                    Intent intent3 = new Intent(getBaseContext(), OnBoard.class);
                    intent3.putExtras(bundle);
                    startActivity(intent3);
                    finish();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}