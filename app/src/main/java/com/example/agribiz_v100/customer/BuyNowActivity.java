package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.agribiz_v100.OrdersActivity;
import com.example.agribiz_v100.PendingViewActivity;
import com.example.agribiz_v100.R;
import com.google.android.material.appbar.MaterialToolbar;

public class BuyNowActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;
    TextView purchase_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);

        topAppBar = findViewById(R.id.topAppBar);
        purchase_tv = findViewById(R.id.purchase_tv);

        topAppBar.setNavigationOnClickListener(v -> finish());

        purchase_tv.setOnClickListener(v -> {
            Intent i = new Intent(this, PendingViewActivity.class);
            startActivity(i);
        });
    }
}