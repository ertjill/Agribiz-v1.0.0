package com.example.agribiz_v100.farmer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.ImagePickerActivity;
import com.github.dhaval2404.imagepicker.ImagePickerFileProvider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.Function;

import javax.annotation.Nullable;

public class MyProduct extends Fragment {
    String TAG ="MyProduct";
    ListView farmer_product_lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<FarmerProductItem> productItems;
    FarmerProductAdapter farmerProductAdapter;
    LinearLayout no_product_ll;
    ImageButton add_product_ib;
    Dialog addProductDialog;
    Dialog addProductPhotoDialog;
    ImageView product_image_iv;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ImageView real_product_image;
    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        addProductDialog = new Dialog(getContext());
        addProductPhotoDialog=new Dialog(getContext());
        farmer_product_lv = view.findViewById(R.id.farmer_product_lv);
        no_product_ll=view.findViewById(R.id.no_product_ll);
        add_product_ib=view.findViewById(R.id.add_product_ib);
        productItems = new ArrayList<>();
        real_product_image = addProductDialog.findViewById(R.id.real_product_image);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== Activity.RESULT_OK && result.getData() !=null){
//                    Bundle bundle = result.getData().getExtras();
//                    Bitmap bitmap =(Bitmap) bundle.get("data");
//                    product_image_iv.setVisibility(View.GONE);
//                    real_product_image.setVisibility(View.VISIBLE);
//                    real_product_image.setImageBitmap(bitmap);
//                    product_image_iv.setImageBitmap(bitmap);
                    product_image_iv.setImageURI(result.getData().getData());
                    Log.d("Upload",result.getResultCode()+"");
                }
            }
        });

        db.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New city: " + dc.getDocument().getId());
                                productItems.add(new FarmerProductItem(dc.getDocument().getId(),
                                        dc.getDocument().getData().get("productFarmId").toString(),
                                        dc.getDocument().getData().get("productName").toString(),
                                        Double.parseDouble(dc.getDocument().getData().get("productPrice").toString()),
                                        dc.getDocument().getData().get("productUnit").toString(),
                                        dc.getDocument().getData().get("productImage").toString(),
                                        Integer.parseInt(dc.getDocument().getData().get("productStocks").toString()),
                                        dc.getDocument().getData().get("productDescription").toString(),"Fruit"));

                                Log.d(TAG, "Number of Item: "+productItems.size());

                            }

                        }
                        if(productItems.isEmpty()){
                            no_product_ll.setVisibility(View.VISIBLE);
                            farmer_product_lv.setVisibility(View.GONE);
                        }
                        else{
                            no_product_ll.setVisibility(View.GONE);
                            farmer_product_lv.setVisibility(View.VISIBLE);
                        }
                        farmerProductAdapter.notifyDataSetChanged();
                    }
                });
        farmerProductAdapter = new FarmerProductAdapter(getContext(),productItems);
        farmer_product_lv.setAdapter(farmerProductAdapter);

        add_product_ib.setOnClickListener(v -> {

            String category[] = {"Fruits","Vegetables","Livestocks","Poultry","Fertilizer"};
            String unit[] = {"kg","g","ml","pcs","L"};
            addProductDialog.setContentView(R.layout.farmer_add_new_product_dialog);
            addProductDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addProductDialog.setCancelable(false);
            Button cancel_btn=addProductDialog.findViewById(R.id.cancel_btn);
            product_image_iv = addProductDialog.findViewById(R.id.product_image_iv);

            AutoCompleteTextView productCategory_at=addProductDialog.findViewById(R.id.productCategory_at);
            ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(getContext(),R.layout.dropdown_item,category);
            productCategory_at.setAdapter(productCategoryAdapter);
            productCategory_at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Selected : "+item, Toast.LENGTH_SHORT).show();
                }
            });
            AutoCompleteTextView productUnit_at=addProductDialog.findViewById(R.id.productUnit_at);
            ArrayAdapter<String> productUnitAdapter = new ArrayAdapter<String>(getContext(),R.layout.dropdown_item,unit);
            productUnit_at.setAdapter(productUnitAdapter);
            productUnit_at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Selected : "+item, Toast.LENGTH_SHORT).show();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductDialog.cancel();
                }
            });

            addProductDialog.show();

            product_image_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //addProductPhotoDialog.show();
//                    ImagePicker.Companion.with(getActivity())
//                            .cropSquare()	    			//Crop image(Optional), Check Customization for more option
//                            //.compress(1024)			//Final image size will be less than 1 MB(Optional)
//                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                            .start();
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(intent.resolveActivity(getContext().getPackageManager())!=null){
                        activityResultLauncher.launch(intent);
                    }
                    else {
                        Toast.makeText(getActivity(), "There is no app to support this action", Toast.LENGTH_SHORT).show();
                    }	//Final image resolution will be less than 1080 x 1080(Optional));

                }
            });




        });

        return view;
    }

    public class FarmerProductAdapter extends BaseAdapter {

        Context context;
        ArrayList<FarmerProductItem> productList;
        LayoutInflater layoutInflater;

        public FarmerProductAdapter(Context context, ArrayList<FarmerProductItem> productList) {
            this.context = context;
            this.productList = productList;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(layoutInflater==null){
                layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            }

            if(convertView==null){
                convertView=layoutInflater.inflate(R.layout.farmer_product_list_card,null);
            }

            ImageView product_image_iv = convertView.findViewById(R.id.product_image_iv);
            TextView product_name_unit = convertView.findViewById(R.id.product_name_unit);
            TextView product_stocks = convertView.findViewById(R.id.product_stocks);
            TextView product_sold=convertView.findViewById(R.id.product_sold);
            TextView product_category=convertView.findViewById(R.id.product_category);

            TextView product_price = convertView.findViewById(R.id.product_price);


            Glide.with(context)
                    .load(productList.get(position).getProductImage())
                    .into(product_image_iv);
            product_name_unit.setText(productList.get(position).getProductName()+"per "+productList.get(position).getProductUnit());
            product_stocks.setText("Stocks: "+productList.get(position).getProductStocks());
            product_sold.setText("Sold: "+productList.get(position).getProductSold());
            product_category.setText("Category: "+productList.get(position).getProductCategory());
            product_price.setText("Price: Php "+productList.get(position).getProductPrice());
            return convertView;
        }
    }

}