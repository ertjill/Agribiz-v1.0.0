package com.example.agribiz_v100.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.agribiz_v100.ProductItem;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductModel {
    private String productId;
    private String productName;
    private Double productPrice;
    private String productUnit;
    private int productQuantity;
    private ArrayList<String> productImage;
    private String productDescription;
    private String productCategory;
    private String productCoverImage;

    private int productRating = 0;
    private int productStocks = 0;
    private int productSold = 0;

    private String productFarmId;
    private String productFarmImage;
    private String productFarmName;
    private String productFarmLocation;

    //extensions
    private Double productShippingFee;
    private Double productOldPrice;
    private int productSalesTo;

    public ProductModel(String productId, String productName, Double productPrice, String productUnit, int productQuantity, ArrayList<String> productImage, String productDescription, String productCategory, int productStocks) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productUnit = productUnit;
        this.productQuantity = productQuantity;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productStocks = productStocks;
    }

    public ProductModel() {
    }

    public String getProductCoverImage() {
        return productCoverImage;
    }

    public void setProductCoverImage(String productCoverImage) {
        this.productCoverImage = productCoverImage;
    }

    public Map<String, Object> getProductMap() {
        Map<String, Object> prod = new HashMap<>();
        prod.put("productId", getProductId());
        prod.put("productName", getProductName());
        prod.put("productPrice", getProductPrice());
        prod.put("productUnit", getProductUnit());
        prod.put("productQuantity", getProductQuantity());
        prod.put("productImage", getProductImage());
        prod.put("productDescription", getProductDescription());
        prod.put("productCategory", getProductCategory());
        prod.put("productRating", getProductRating());
        prod.put("productStocks", getProductStocks());
        prod.put("productSold", getProductSold());
        prod.put("productFarmId", getProductFarmId());
        prod.put("productFarmName", getProductFarmName());
        prod.put("productFarmImage", getProductFarmImage());
        prod.put("productFarmLocation", getProductFarmLocation());
        prod.put("productShippingFee", getProductShippingFee());
        return prod;
    }

    public Double getProductShippingFee() {
        return productShippingFee;
    }

    public void setProductShippingFee(Double productShippingFee) {
        this.productShippingFee = productShippingFee;
    }

    public ArrayList<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductFarmLocation() {
        return productFarmLocation;
    }

    public void setProductFarmLocation(String productFarmLocation) {
        this.productFarmLocation = productFarmLocation;
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
        return productFarmImage;
    }

    public void setProductFarmImage(String productFarmImage) {
        this.productFarmImage = productFarmImage;
    }

    public String getProductFarmName() {
        return productFarmName;
    }

    public void setProductFarmName(String productFarmName) {
        this.productFarmName = productFarmName;
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

    public ProductModel(QueryDocumentSnapshot product) {
        this.productImage = new ArrayList<>();
        this.productId = product.getId();
        this.productFarmId = product.getData().get("productFarmId").toString();
        this.productName = product.getData().get("productName").toString();
        this.productPrice = (Double) product.getData().get("productPrice");
        this.productUnit = product.getData().get("productUnit").toString();
        this.productQuantity = Integer.parseInt(product.getData().get("productQuantity").toString());
        this.productImage.add(product.getData().get("productImage").toString());
        this.productDescription = product.getData().get("productDescription").toString();
        this.productCategory = product.getData().get("productCategory").toString();
        this.productStocks = Integer.parseInt(product.getData().get("productStocks").toString());
        this.productCoverImage = product.getData().get("productImage") + "";
        //this.productSold = Integer.parseInt(product.getData().get("productSold").toString());
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductFarmId() {
        return productFarmId;
    }

    public void setProductFarmId(String productFarmId) {
        this.productFarmId = productFarmId;
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


}
