package com.example.agribiz_v100.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable, Parcelable {
    //Product Properties
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

    //Product optional properties
    //private Double productOldPrice;
    //private int productDiscount;

    public ProductModel() {
    }

    public ProductModel(String productId, String productUserId, String productName, String productDescription, List<String> productImage, String productCategory, Double productPrice, String productUnit, int productQuantity, int productStocks, int productSold, int productRating, int productNoCustomerRate, Timestamp productDateUploaded, Double productShippingFee, Double productOldPrice, int productSalesTo) {
        this.productId = productId;
        this.productUserId = productUserId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productImage = productImage;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productUnit = productUnit;
        this.productQuantity = productQuantity;
        this.productStocks = productStocks;
        this.productSold = productSold;
        this.productRating = productRating;
        this.productNoCustomerRate = productNoCustomerRate;
        this.productDateUploaded = productDateUploaded;
    }

    protected ProductModel(Parcel in) {
        productId = in.readString();
        productUserId = in.readString();
        productName = in.readString();
        productDescription = in.readString();
        productImage = in.createStringArrayList();
        productCategory = in.readString();
        if (in.readByte() == 0) {
            productPrice = null;
        } else {
            productPrice = in.readDouble();
        }
        productUnit = in.readString();
        productQuantity = in.readInt();
        productStocks = in.readInt();
        productSold = in.readInt();
        productRating = in.readInt();
        productNoCustomerRate = in.readInt();
        productDateUploaded = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductUserId() {
        return productUserId;
    }

    public void setProductUserId(String productUserId) {
        this.productUserId = productUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
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

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
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

    public int getProductNoCustomerRate() {
        return productNoCustomerRate;
    }

    public void setProductNoCustomerRate(int productNoCustomerRate) {
        this.productNoCustomerRate = productNoCustomerRate;
    }

    public Timestamp getProductDateUploaded() {
        return productDateUploaded;
    }

    public void setProductDateUploaded(Timestamp productDateUploaded) {
        this.productDateUploaded = productDateUploaded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productUserId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeStringList(productImage);
        dest.writeString(productCategory);
        if (productPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(productPrice);
        }
        dest.writeString(productUnit);
        dest.writeInt(productQuantity);
        dest.writeInt(productStocks);
        dest.writeInt(productSold);
        dest.writeInt(productRating);
        dest.writeInt(productNoCustomerRate);
        dest.writeParcelable(productDateUploaded, flags);
    }
}
