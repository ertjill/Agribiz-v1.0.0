package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.location.BrgyManagement;
import com.example.agribiz_v100.location.CityManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class AddressActivity extends AppCompatActivity {
    AutoCompleteTextView municipality_act, barangay_act;
    CityManagement cities;
    BrgyManagement barangay;
    List<JSONObject> citiesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        municipality_act = findViewById(R.id.municipality_act);
        barangay_act = findViewById(R.id.barangay_act);
        String id = "";
        cities = new CityManagement(new CityManagement.AsyncResponse() {
            @Override
            public void processFinish(List<JSONObject> citiesList, List<String> citiesNames) {

                ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown_item, citiesNames);
                municipality_act.setAdapter(mAdapter);

            }

        });



        municipality_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("tag","imong ge click kay index : "+i);
                try {
                    Log.d("tag",cities.citiesList.get(i).getString("code"));
                    barangay = new BrgyManagement(cities.citiesList.get(i).getString("code"),new BrgyManagement.AsyncResponse() {
                        @Override
                        public void processFinish(List<JSONObject> brgyList, List<String> brgyNames) {
                            ArrayAdapter<String> bAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, brgyNames);
                            barangay_act.setAdapter(bAdapter);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


}