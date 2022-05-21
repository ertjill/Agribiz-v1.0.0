package com.example.agribiz_v100.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.BarterAdapter;
import com.example.agribiz_v100.dialog.AddBarterDialog;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BarterAddItem extends Fragment {

    ImageButton add_barter_ib;
    AddBarterDialog addBarterDialog;

    LinearLayout no_barter_product_ll;

    ListView farmer_barter_lv;
    List<BarterModel> barterItems;
    BarterAdapter barterAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListenerRegistration registration;
    List<String> id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barter_product, container, false);
        addBarterDialog = new AddBarterDialog(getActivity(), this);
        add_barter_ib = view.findViewById(R.id.add_barter_ib);
        farmer_barter_lv = view.findViewById(R.id.farmer_barter_lv);
        no_barter_product_ll = view.findViewById(R.id.no_barter_product_ll);
        id = new ArrayList<>();
        barterItems = new ArrayList<>();
        barterAdapter = new BarterAdapter(getContext(), barterItems);
        addBarterDialog.buildDialog();
        farmer_barter_lv.setAdapter(barterAdapter);
        farmer_barter_lv.setEmptyView(no_barter_product_ll);
        add_barter_ib.setOnClickListener(v -> addBarterDialog.showDialog());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Removes listener being tracked
        registration.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Display assistance lists
        displayBarterItems();
    }

    public void displayBarterItems() {
        registration = BarterManagement.getBarteredItems(user.getUid(), "Open")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        barterItems.clear();
                        id.clear();
                        if (!value.isEmpty())
                            for (DocumentSnapshot ds : value) {
                                barterItems.add(ds.toObject(BarterModel.class));
                                id.add(ds.getId());
                            }
                        barterAdapter.notifyDataSetChanged();
                    }
                });
    }

}