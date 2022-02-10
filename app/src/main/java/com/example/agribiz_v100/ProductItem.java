package com.example.agribiz_v100;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductItem implements Parcelable{
    //Product needed information
    private String productId;
    private String productUserId;
    private String productName;
    private String productDescription;
    private List<String> productImage;
    private String productCategory;
    private Double productPrice;
    private String productUnit;
    private int productQuantity;
    private int productStocks=0;
    private int productSold=0;
    private int productRating=0;
    private int productNoCustomerRate=0;
    private Timestamp productDateUploaded;
    private String productUserImage;
    private String productUserName;
    private String productUserLocation;

    //extensions
    private Double productShippingFee;
    private Double productOldPrice;
    private int productSalesTo;

    public ProductItem(){

    }
    protected ProductItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        if (in.readByte() == 0) {
            productPrice = null;
        } else {
            productPrice = in.readDouble();
        }
        productUnit = in.readString();
        productQuantity = in.readInt();
        productImage = in.createStringArrayList();
        productDescription = in.readString();
        productCategory = in.readString();
        productRating = in.readInt();
        productStocks = in.readInt();
        productSold = in.readInt();
        productUserId = in.readString();
        productUserImage = in.readString();
        productUserName = in.readString();
        productUserLocation = in.readString();
        if (in.readByte() == 0) {
            productShippingFee = null;
        } else {
            productShippingFee = in.readDouble();
        }
        if (in.readByte() == 0) {
            productOldPrice = null;
        } else {
            productOldPrice = in.readDouble();
        }
        productSalesTo = in.readInt();
    }

    public static final Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };


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
        prod.put("productUserName",getProductFarmName());
        prod.put("productUserImage",getProductFarmImage());
        prod.put("productUserLocation",getProductFarmLocation());
        prod.put("productShippingFee",getProductShippingFee());
        return prod;
    }
    public Double getProductShippingFee() {
        return productShippingFee;
    }

    public void setProductShippingFee(Double productShippingFee) {
        this.productShippingFee = productShippingFee;
    }

    public List<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductFarmLocation() {
        return productUserLocation;
    }

    public void setProductFarmLocation(String productUserLocation) {
        this.productUserLocation = productUserLocation;
    }

    public Double getProductOldPrice() {
        return productOldPrice;
    }

    public void setProductOldPrice(Double productOldPrice) {
        this.productOldPrice = productOldPrice;
    }

    public int getProductSalesTo() {
        return productSalesTo;
    }

    public void setProductSalesTo(int productSalesTo) {
        this.productSalesTo = productSalesTo;
    }


    public String getProductFarmImage() {
        return productUserImage;
    }

    public void setProductFarmImage(String productUserImage) {
        this.productUserImage = productUserImage;
    }

    public String getProductFarmName() {
        return productUserName;
    }

    public void setProductFarmName(String productUserName) {
        this.productUserName = productUserName;
    }

    public int getProductStocks() {
        return productStocks;
    }

    public void setProductStocks(int productStocks) {
        this.productStocks = productStocks;
    }

    public int getProductSold() {
        return productSold;
    }

    public void setProductSold(int productSold) {
        this.productSold = productSold;
    }

    public int getProductRating() {
        return productRating;
    }

    public void setProductRating(int productRating) {
        this.productRating = productRating;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }


    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public ProductItem( QueryDocumentSnapshot product ){
        this.productImage = new ArrayList<>();
        this.productId = product.getId();
        this.productUserId = product.getData().get("productUserId").toString();
        this.productName = product.getData().get("productName").toString();
        this.productPrice = (Double) product.getData().get("productPrice");
        this.productUnit = product.getData().get("productUnit").toString();
        this.productQuantity = Integer.parseInt(product.getData().get("productQuantity").toString());
        this.productImage = (ArrayList<String>) product.getData().get("productImage");
        this.productDescription = product.getData().get("productDescription").toString();
        this.productCategory = product.getData().get("productCategory").toString();
        this.productStocks = Integer.parseInt(product.getData().get("productStocks").toString());
        //this.productSold = Integer.parseInt(product.getData().get("productSold").toString());
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductUserId() {
        return productUserId;
    }

    public void setProductFarmId(String productUserId) {
        this.productUserId = productUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        if (productPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(productPrice);
        }
        dest.writeString(productUnit);
        dest.writeInt(productQuantity);
        dest.writeStringList(productImage);
        dest.writeString(productDescription);
        dest.writeString(productCategory);
        dest.writeInt(productRating);
        dest.writeInt(productStocks);
        dest.writeInt(productSold);
        dest.writeString(productUserId);
        dest.writeString(productUserImage);
        dest.writeString(productUserName);
        dest.writeString(productUserLocation);
        if (productShippingFee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(productShippingFee);
        }
        if (productOldPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(productOldPrice);
        }
        dest.writeInt(productSalesTo);
    }
}
