package com.example.agribiz_v100.farmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.BarterProductAdapter;
import com.example.agribiz_v100.dialog.AddBarterDialog;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BarterProduct extends Fragment {

    AddBarterDialog addBarterDialog;

    ImageButton add_barter_ib;
    LinearLayout no_barter_product_ll;
    ListView farmer_barter_lv;

    SparseArray<BarterModel> barterItems;
    BarterProductAdapter barterProductAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentSnapshot last = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barter_product, container, false);

        addBarterDialog = new AddBarterDialog(getActivity(), this);
        addBarterDialog.buildDialog();

        barterItems = new SparseArray<>();
        barterProductAdapter = new BarterProductAdapter(getContext(), barterItems);

        add_barter_ib = view.findViewById(R.id.add_barter_ib);
        no_barter_product_ll = view.findViewById(R.id.no_barter_product_ll);
        farmer_barter_lv = view.findViewById(R.id.farmer_barter_lv);

        farmer_barter_lv.setAdapter(barterProductAdapter);
        displayBarterItems();

        add_barter_ib.setOnClickListener(v -> addBarterDialog.showDialog());

        addBarterDialog.createListener(new AddBarterDialog.AddBarterDialogCallback() {
            @Override
            public void addOnDocumentAddedListener(boolean isAdded) {
                Log.d("tag", "hereee");
                displayBarterItems();
            }
        });

        return view;
    }

    int i = 0;

    public void displayBarterItems() {
        BarterManagement.getBarterItems(user.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("data", "Processing successful task");
                    if (task.getResult().getDocuments().size() > 0) {
                        Log.d("tag", "Processing document size");
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            BarterModel item = document.toObject(BarterModel.class);
                            barterItems.append(i++, item);
                            Log.d("data", "Getting results");
                        }
                        barterProductAdapter.notifyDataSetChanged();
                        last = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                    }
                }
            }
        });
    }
}