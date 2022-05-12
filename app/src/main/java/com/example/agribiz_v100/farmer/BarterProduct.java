package com.example.agribiz_v100.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.AddBarterDialog;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BarterProduct extends Fragment {

    ImageButton add_barter_ib;
    AddBarterDialog addBarterDialog;

    LinearLayout no_barter_product_ll;

    ListView farmer_barter_lv;
    SparseArray<BarterModel> barterItems;
    BarterAdapter barterAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentSnapshot last = null;
    ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barter_product, container, false);
        add_barter_ib = view.findViewById(R.id.add_barter_ib);
        farmer_barter_lv = view.findViewById(R.id.farmer_barter_lv);

        addBarterDialog = new AddBarterDialog(getActivity(), this);
        addBarterDialog.buildDialog();

        barterItems = new SparseArray<>();
        barterAdapter = new BarterAdapter(getContext(), barterItems);
        farmer_barter_lv.setAdapter(barterAdapter);

        displayBarterItems();

        no_barter_product_ll = view.findViewById(R.id.no_barter_product_ll);

        add_barter_ib.setOnClickListener(v -> addBarterDialog.showDialog());

        addBarterDialog.createListener(new AddBarterDialog.AddBarterDialogCallback() {
            @Override
            public void addOnDocumentAddedListener(boolean isAdded) {
                Log.d("BarterManagement", "Inside barter dialog callback");
                displayBarterItems();
            }
        });

        return view;
    }

    int i = 0;
    public void displayBarterItems() {
        Log.d("BarterManagement", "Displaying barter items...");
//        registration = BarterManagement.getBarterItems(user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                barterItems.clear();
//                for (QueryDocumentSnapshot doc : value) {
//                    Log.d("BarterManagement", user.getUid() + " inside for each");
//                    BarterModel bm = doc.toObject(BarterModel.class);
//                    barterItems.add(bm);
//                    barterAdapter.notifyDataSetChanged();
//                }
//            }
//        });

        BarterManagement.getBarterItems(last, user.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("BarterManagement", last != null ? last.getData().toString() : "null");
                    Log.d("BarterManagement", "Here inside isSuccessful");
                    if (task.getResult().getDocuments().size() > 0) {
                        Log.d("BarterManagement", "Inside greater than zero");
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            BarterModel bm = doc.toObject(BarterModel.class);
                            barterItems.append(i++, bm);
                        }
                        barterAdapter.notifyDataSetChanged();
                        last = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                    }
                }
            }
        });
    }

    public class BarterAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        SparseArray<BarterModel> barterItems;

        public BarterAdapter(Context context, SparseArray<BarterModel> barterItems) {
            this.context = context;
            this.barterItems = barterItems;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return barterItems.size();
        }

        @Override
        public BarterModel getItem(int i) {
            return barterItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            }

            if (view == null) {
                view = inflater.inflate(R.layout.farmer_barter_product_list, null);
            }

            TextView barterName_tv = view.findViewById(R.id.barterName_tv);
            TextView barterCondition_tv = view.findViewById(R.id.barterCondition_tv);
            TextView barterQuantity_tv = view.findViewById(R.id.barterQuantity_tv);
            TextView barterDesc_tv = view.findViewById(R.id.barterDesc_tv);

            String name = barterItems.get(i).getBarterName();
            String condition = "Item condition: " + barterItems.get(i).getBarterCondition();
            String quantity = "Quantity: " + barterItems.get(i).getBarterQuantity();
            String desc = "Description: " + barterItems.get(i).getBarterDescription();

            barterName_tv.setText(name);
            barterCondition_tv.setText(condition);
            barterQuantity_tv.setText(quantity);
            barterDesc_tv.setText(desc);

            return view;
        }
    }
}