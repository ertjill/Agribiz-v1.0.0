package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.agribiz_v100.R;
import com.google.android.material.appbar.MaterialToolbar;

public class MessagesActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        reference();

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void reference() {
        topAppBar = findViewById(R.id.topAppBar);
    }
}