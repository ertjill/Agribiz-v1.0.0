package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.AddressItemAdapter;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAddressesActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;
    Button add_new_address_btn;
    ListView address_item_list;
    TextView add_fullName_tv, add_phoneNum_tv, full_address_tv;

    AddressItemAdapter addressItemAdapter;
    SparseArray<LocationModel> addressItems;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        // Get references of widgets from XML layout
        references();
        addressItems = new SparseArray<>();
        addressItemAdapter = new AddressItemAdapter(this, getApplicationContext(), addressItems);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        address_item_list.setAdapter(addressItemAdapter);
        add_new_address_btn.setOnClickListener(view -> startActivity(new Intent(this, AddressActivity.class)));


    }

    private void references() {
        topAppBar = findViewById(R.id.topAppBar);
        add_new_address_btn = findViewById(R.id.add_new_address_btn);
        address_item_list = findViewById(R.id.address_item_list);
        add_fullName_tv = findViewById(R.id.add_fullName_tv);
        add_phoneNum_tv = findViewById(R.id.add_phoneNum_tv);
        full_address_tv = findViewById(R.id.full_address_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressList();
    }

    protected void getAddressList() {

        ProfileManagement.getAddress(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<Object> list;
                    list = (ArrayList<Object>) doc.get("userLocation");
//                    Map<String, String> m = (Map<String, String>) list.get(0);
                    int count = 0;
                    if (list !=null)
                        for (Object map : list) {
                            Map<String, String> loc = (Map<String, String>) map;
                            LocationModel locationModel = new LocationModel();
                            locationModel.setUserFullName(loc.get("userFullName"));
                            locationModel.setUserPhoneNumber(loc.get("userPhoneNumber"));
                            locationModel.setUserRegion(loc.get("userRegion"));
                            locationModel.setUserProvince(loc.get("userProvince"));
                            locationModel.setUserMunicipality(loc.get("userMunicipality"));
                            locationModel.setUserBarangay(loc.get("userBarangay"));
                            locationModel.setUserZipCode(loc.get("userZipCode"));
                            locationModel.setUserSpecificAddress(loc.get("userSpecificAddress"));
                            addressItems.append(count++, locationModel);
                        }
                    addressItemAdapter.notifyDataSetChanged();
                } else {
                    AuthValidation.failedToast(getApplicationContext(), task.getException().getMessage());
                }
            }
        });
    }
}