package com.example.agribiz_v100.customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.agribiz_v100.BarteredItemViewActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.BarterItemGridViewAdpater;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BarterBrowserFragment extends Fragment {

    RecyclerView open_barter_rv;
    BarterItemGridViewAdpater barterItemGridViewAdpater;
    List<BarterModel> barterModelList;
    LinearLayout no_product_ll;

    ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barter_browser, container, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        no_product_ll = view.findViewById(R.id.no_product_ll);
        open_barter_rv = view.findViewById(R.id.open_barter_rv);
        open_barter_rv.setHasFixedSize(true);
        open_barter_rv.setLayoutManager(gridLayoutManager);
        barterModelList = new ArrayList<>();
        barterItemGridViewAdpater = new BarterItemGridViewAdpater(getContext(), barterModelList);

        open_barter_rv.setAdapter(barterItemGridViewAdpater);
        open_barter_rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                View ChildView = rv.findChildViewUnder(e.getX(), e.getY());
                if (ChildView != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(ChildView);
                    Intent intent = new Intent(getContext(), BarteredItemViewActivity.class);
                    intent.putExtra("barterId", barterModelList.get(position).getBarterId());
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return view;

    }

    public void getBateredItems() {
        registration = BarterManagement.getBarteredItemsByType("f", "Open").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                barterModelList.clear();
                if (!value.isEmpty()) {
                    no_product_ll.setVisibility(View.GONE);
                    open_barter_rv.setVisibility(View.VISIBLE);
                    for (DocumentSnapshot ds : value) {
                        barterModelList.add(ds.toObject(BarterModel.class));
                    }
                    barterItemGridViewAdpater.notifyDataSetChanged();
                } else {
                    no_product_ll.setVisibility(View.VISIBLE);
                    open_barter_rv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBateredItems();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }
}