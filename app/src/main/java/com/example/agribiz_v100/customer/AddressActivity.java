package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.location.BrgyManagement;
import com.example.agribiz_v100.location.CityManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.List;

public class AddressActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    LocationModel locationModel;
    CityManagement cities;
    BrgyManagement barangay;

    MaterialToolbar topAppBar;
    EditText full_name_et, phone_number_et, region_et, province_et, postal_code_et, specificAddress_et;
    AutoCompleteTextView municipality_act, barangay_act;
    Button add_address_btn;

    List<JSONObject> citiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        locationModel = new LocationModel();

        municipality_act = findViewById(R.id.municipality_act);
        barangay_act = findViewById(R.id.barangay_act);
        topAppBar = findViewById(R.id.topAppBar);
        add_address_btn = findViewById(R.id.add_address_btn);

        String id = "";

        topAppBar.setNavigationOnClickListener(v -> finish());

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
                barangay_act.setText("");
                barangay_act.setEnabled(false);
                try {
                    Log.d("tag",cities.citiesList.get(i).getString("code"));
                    barangay = new BrgyManagement(cities.citiesList.get(i).getString("code"),new BrgyManagement.AsyncResponse() {
                        @Override
                        public void processFinish(List<JSONObject> brgyList, List<String> brgyNames) {
                            ArrayAdapter<String> bAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, brgyNames);
                            barangay_act.setAdapter(bAdapter);
                            barangay_act.setEnabled(true);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Calls references method that initializes each ID;
        references();

        add_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = full_name_et.getText().toString();
                String phoneNumber = phone_number_et.getText().toString();
                String region = region_et.getText().toString();
                String province = province_et.getText().toString();
                String municipality = municipality_act.getText().toString();
                String barangay = barangay_act.getText().toString();
                String zipCode = postal_code_et.getText().toString();
                String specificAdd = specificAddress_et.getText().toString();

                if (TextUtils.isEmpty(fullName)) {
                    full_name_et.setError("This field cannot be empty");
                }
                else if (TextUtils.isEmpty(phoneNumber)) {
                    phone_number_et.setError("This field cannot be empty");
                }
                else if (TextUtils.isEmpty(municipality)) {
                    municipality_act.setError("This field cannot be empty");
                }
                else if (TextUtils.isEmpty(barangay)) {
                    barangay_act.setError("This field cannot be empty");
                }
                else if (TextUtils.isEmpty(zipCode)) {
                    postal_code_et.setError("This field cannot be empty");
                }
                else if (TextUtils.isEmpty(specificAdd)) {
                    specificAddress_et.setError("This field cannot be empty");
                }
                else {
                    full_name_et.setError(null);
                    locationModel.setUserFullName(fullName);
                    phone_number_et.setError(null);
                    locationModel.setUserPhoneNumber(phoneNumber);
                    municipality_act.setError(null);

                    locationModel.setUserRegion(region);
                    locationModel.setUserProvince(province);

                    locationModel.setUserMunicipality(municipality);
                    barangay_act.setError(null);
                    locationModel.setUserBarangay(barangay);
                    postal_code_et.setError(null);
                    locationModel.setUserZipCode(zipCode);
                    specificAddress_et.setError(null);
                    locationModel.setUserSpecificAddress(specificAdd);

                    ProfileManagement.addAddress(AddressActivity.this, locationModel, user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                AuthValidation.successToast(AddressActivity.this, "Successfully added address").show();
                                clearText();
                                full_name_et.setFocusable(false);
                                phone_number_et.setFocusable(false);
                                municipality_act.setFocusable(false);
                                barangay_act.setFocusable(false);
                                postal_code_et.setFocusable(false);
                                specificAddress_et.setFocusable(false);
                            }
                            else {
                                AuthValidation.failedToast(AddressActivity.this, task.getException().getMessage()).show();
                                Log.d("tag",task.getException().getLocalizedMessage());
                                task.getException().printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void references() {
        full_name_et = findViewById(R.id.full_name_et);
        phone_number_et = findViewById(R.id.phone_number_et);
        region_et = findViewById(R.id.region_et);
        province_et = findViewById(R.id.province_et);
        municipality_act = findViewById(R.id.municipality_act);
        barangay_act = findViewById(R.id.barangay_act);
        postal_code_et = findViewById(R.id.postal_code_et);
        specificAddress_et = findViewById(R.id.specificAddress_et);
    }

    private void clearText() {
        full_name_et.getText().clear();
        phone_number_et.getText().clear();
        municipality_act.getText().clear();
        barangay_act.getText().clear();
        postal_code_et.getText().clear();
        specificAddress_et.getText().clear();
    }
}