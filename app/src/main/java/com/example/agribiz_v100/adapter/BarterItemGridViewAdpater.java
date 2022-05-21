package com.example.agribiz_v100.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.ViewAllProductsActivity;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.ProfileManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class BarterItemGridViewAdpater extends RecyclerView.Adapter<BarterItemGridViewAdpater.ViewHolder> {
    Context context;
    List<BarterModel> barterItems;
    LayoutInflater layoutInflater;

    public BarterItemGridViewAdpater(Context context, List<BarterModel> barterItems) {
        this.context = context;
        this.barterItems = barterItems;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BarterItemGridViewAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.barter_item_card_layout, parent, false);
        return new BarterItemGridViewAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarterItemGridViewAdpater.ViewHolder holder, int position) {
        Glide.with(context)
                .load(barterItems.get(position).getBarterImage().get(0))
                .into(holder.item_image_iv);

        ProfileManagement.getUserProfile(barterItems.get(position).getBarterUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(context)
                        .load(documentSnapshot.getString("userImage"))
                        .into(holder.farmer_image_iv);
            }
        });

        holder.item_text.setText(barterItems.get(position).getBarterName());
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return barterItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image_iv, farmer_image_iv;
        TextView item_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image_iv = itemView.findViewById(R.id.item_image_iv);
            farmer_image_iv = itemView.findViewById(R.id.farmer_image_iv);

            item_text = itemView.findViewById(R.id.item_text);
        }
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (layoutInflater == null) {
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        }
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.barter_item_card_layout, null);
//        }
//        ImageView item_image_iv, farmer_image_iv;
//        TextView item_text;
//
//        item_image_iv = convertView.findViewById(R.id.item_image_iv);
//        farmer_image_iv = convertView.findViewById(R.id.farmer_image_iv);
//
//        item_text = convertView.findViewById(R.id.item_text);
//
//        Glide.with(context)
//                .load(barterItems.get(position).getBarterImage().get(0))
//                .into(item_image_iv);
//
//        ProfileManagement.getUserProfile(barterItems.get(position).getBarterUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Glide.with(context)
//                        .load(documentSnapshot.getString("userImage"))
//                        .into(item_image_iv);
//            }
//        });
//
//        item_text.setText(barterItems.get(position).getBarterName());
//
//        return convertView;
//    }
}
