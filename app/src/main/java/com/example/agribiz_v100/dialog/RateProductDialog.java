package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class RateProductDialog {
    Dialog dialog;
    ImageView star1_iv,star2_iv,star3_iv,star4_iv,star5_iv;
    Button cancel_btn,submit_btn;
    EditText textArea_information;
    int rate = 0;
    public RateProductDialog(Activity activity, OrderProductModel orderProductModel) {
        this.dialog = new Dialog(activity);
        this.dialog.setContentView(R.layout.rate_layout);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setCancelable(false);
        star1_iv = dialog.findViewById(R.id.star1_iv);
        star1_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1_iv.setImageResource(R.drawable.ic_baseline_star);
                star2_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star3_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star4_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                rate =1;
            }
        });
        star2_iv = dialog.findViewById(R.id.star2_iv);
        star2_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1_iv.setImageResource(R.drawable.ic_baseline_star);
                star2_iv.setImageResource(R.drawable.ic_baseline_star);
                star3_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star4_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                rate=2;
            }
        });
        star3_iv = dialog.findViewById(R.id.star3_iv);
        star3_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1_iv.setImageResource(R.drawable.ic_baseline_star);
                star2_iv.setImageResource(R.drawable.ic_baseline_star);
                star3_iv.setImageResource(R.drawable.ic_baseline_star);
                star4_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                rate=3;
            }
        });
        star4_iv = dialog.findViewById(R.id.star4_iv);
        star4_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1_iv.setImageResource(R.drawable.ic_baseline_star);
                star2_iv.setImageResource(R.drawable.ic_baseline_star);
                star3_iv.setImageResource(R.drawable.ic_baseline_star);
                star4_iv.setImageResource(R.drawable.ic_baseline_star);
                star5_iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                rate=4;
            }
        });
        star5_iv = dialog.findViewById(R.id.star5_iv);
        star5_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1_iv.setImageResource(R.drawable.ic_baseline_star);
                star2_iv.setImageResource(R.drawable.ic_baseline_star);
                star3_iv.setImageResource(R.drawable.ic_baseline_star);
                star4_iv.setImageResource(R.drawable.ic_baseline_star);
                star5_iv.setImageResource(R.drawable.ic_baseline_star);
                rate=5;
            }
        });
        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submit_btn = dialog.findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit rating
                String feedback = textArea_information.getText().toString();
                orderProductModel.setRated(true);
                orderProductModel.setProductRating(rate);
                ProductManagement.rateProduct(activity,orderProductModel,feedback)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AuthValidation.successToast(activity,"Rating Submitted" ).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                AuthValidation.failedToast(activity,e.getLocalizedMessage() ).show();
                                dialog.dismiss();
                            }
                        });
            }
        });
        textArea_information = dialog.findViewById(R.id.textArea_information);
    }
    public void showDialog(){
        dialog.show();
    }
}
