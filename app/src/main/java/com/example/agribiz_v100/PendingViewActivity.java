package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.agribiz_v100.customer.Store;

public class PendingViewActivity extends AppCompatActivity {

    Button store_btn, my_orders_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_view);
        store_btn = findViewById(R.id.store_btn);
        my_orders_btn = findViewById(R.id.my_orders_btn);

        store_btn.setOnClickListener(v -> {
            // Intent i = new Intent(getApplicationContext(), Store.class);
            //startActivity(i);
        });

        my_orders_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersActivity.class));
        });
    }
}