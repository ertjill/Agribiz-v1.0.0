package com.example.agribiz_v100.customer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.BarterAdapter;
import com.example.agribiz_v100.adapter.NotOpenBarterAdapter;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BarterFragment extends Fragment {
    ListView customer_barter_sent;
    LinearLayout no_product_ll;
    List<BarterModel> barterModelList;
    List<String> id;
    BarterAdapter barterAdapter;
    NotOpenBarterAdapter notOpenBarterAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListenerRegistration registration;
    String title;
    String status;

    public BarterFragment(String title, String status) {
        this.title = title;
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barter_sent, container, false);
        customer_barter_sent = view.findViewById(R.id.customer_barter_sent);
        no_product_ll = view.findViewById(R.id.no_product_ll);
        barterModelList = new ArrayList<>();
        id = new ArrayList<>();

        if (status.equals("Pending")) {
            barterAdapter = new BarterAdapter(getContext(), barterModelList);
            customer_barter_sent.setAdapter(barterAdapter);
        }
        else {
            notOpenBarterAdapter = new NotOpenBarterAdapter(getContext(), barterModelList);
            customer_barter_sent.setAdapter(notOpenBarterAdapter);
        }


        customer_barter_sent.setEmptyView(no_product_ll);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayBarterRequest();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    public void displayBarterRequest() {
        registration = BarterManagement.getBarteredItems(user.getUid(), status).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                barterModelList.clear();
                id.clear();
                Log.d("BarterSentFragment", "here");
                if (!value.isEmpty()) {
                    Log.d("BarterSentFragment", "here not empty");
                    for (DocumentSnapshot ds : value) {
                        Log.d("BarterSentFragment", ds.getString("barterName"));
                        barterModelList.add(ds.toObject(BarterModel.class));
                    }
                }
                if (status.equals("Pending"))
                    barterAdapter.notifyDataSetChanged();
                else
                    notOpenBarterAdapter.notifyDataSetChanged();
            }
        });


    }
}