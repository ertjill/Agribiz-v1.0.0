package com.example.agribiz_v100.farmer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.agribiz_v100.R;
import com.google.android.material.appbar.MaterialToolbar;

public class FarmerMessagesActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_messages);

        reference();

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void reference() {
        topAppBar = findViewById(R.id.topAppBar);
    }
}