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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barter_product, container, false);
        addBarterDialog = new AddBarterDialog(getActivity(), this);
        add_barter_ib = view.findViewById(R.id.add_barter_ib);
        farmer_barter_lv = view.findViewById(R.id.farmer_barter_lv);
        no_barter_product_ll = view.findViewById(R.id.no_barter_product_ll);

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

    //int i = 0;
    public void displayBarterItems() {
        registration = BarterManagement.getBarteredItems(user.getUid(), "Open")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        barterItems.clear();
                        if (!value.isEmpty())
                            for (DocumentSnapshot ds : value) {
                                barterItems.add(ds.toObject(BarterModel.class));

                            }
                        barterAdapter.notifyDataSetChanged();
                    }
                });
    }

//    public class BarterAdapter extends BaseAdapter {
//
//        List<BarterModel> barterItems;
//        Context context;
//        LayoutInflater inflater;
//        public BarterAdapter(Context context,List<BarterModel> barterItems) {
//            this.barterItems=barterItems;
//            this.context = context;
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return barterItems.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int pos, View view, ViewGroup viewGroup) {
//
//            if (inflater == null) {
//                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            }
//
//            if (view == null) {
//                view = inflater.inflate(R.layout.barter_product_list, null);
//            }
//
//            TextView barterName_tv = view.findViewById(R.id.barterName_tv);
//            TextView barterCondition_tv = view.findViewById(R.id.barterCondition_tv);
//            TextView barterQuantity_tv = view.findViewById(R.id.barterQuantity_tv);
//            TextView barterDesc_tv = view.findViewById(R.id.barterDesc_tv);
//
//            String name = barterItems.get(pos).getBarterName();
//            String condition = "Item condition: " + barterItems.get(pos).getBarterCondition();
//            String quantity = "Quantity: " + barterItems.get(pos).getBarterQuantity();
//            String desc = "Description: " + barterItems.get(pos).getBarterDescription();
//
//            barterName_tv.setText(name);
//            barterCondition_tv.setText(condition);
//            barterQuantity_tv.setText(quantity);
//            barterDesc_tv.setText(desc);
//
//            return view;
//        }
//    }
}