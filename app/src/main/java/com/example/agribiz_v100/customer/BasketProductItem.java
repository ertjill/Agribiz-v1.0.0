package com.example.agribiz_v100.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.agribiz_v100.ProductItem;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class BasketProductItem extends ProductItem implements Parcelable {
    private int productBasketQuantity;
    private boolean checked=false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public BasketProductItem(QueryDocumentSnapshot product) {
        super(product);
        this.productBasketQuantity=Integer.parseInt(product.getData().get("productBasketQuantity").toString());
    }

    public int getProductBasketQuantity() {
        return productBasketQuantity;
    }

    public void setProductBasketQuantity(int productBasketQuantity) {
        this.productBasketQuantity = productBasketQuantity;
    }
    public Map<String,Object> getProductMap(){
        Map<String,Object> prod = new HashMap<>();
        prod.put("productId",getProductId());
        prod.put("productName",getProductName());
        prod.put("productPrice",getProductPrice());
        prod.put("productUnit",getProductUnit());
        prod.put("productQuantity",getProductQuantity());
        prod.put("productImage",getProductImage());
        prod.put("productDescription",getProductDescription());
        prod.put("productCategory",getProductCategory());
        prod.put("productRating",getProductRating());
        prod.put("productStocks",getProductStocks());
        prod.put("productSold",getProductSold());
        prod.put("productUserId",getProductUserId());
        prod.put("productFarmName",getProductFarmName());
        prod.put("productFarmImage",getProductFarmImage());
        prod.put("productFarmLocation",getProductFarmLocation());
        prod.put("productShippingFee",getProductShippingFee());
        prod.put("productBasketQuantity",getProductBasketQuantity());
        return prod;
    }
}
