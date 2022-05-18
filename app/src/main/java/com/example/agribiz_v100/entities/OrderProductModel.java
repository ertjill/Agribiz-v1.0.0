package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class OrderProductModel implements Serializable {
    String orderID;
    Timestamp orderDate;
    String orderStatus="pending";
    Object location;

    String productUserId;
    String customerId;
    String productId;
    String productName;
    List<String> productImage;
    String productDescription;
    String productCategory;
    Double productPrice;
    String productUnit;
    int productQuantity;
    int productBasketQuantity;
    int productRating;
    boolean isRated=false;

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderProductModel(){
    }

    public OrderProductModel(String orderID, Timestamp orderDate, String orderStatus, Object location, String productUserId, String customerId, String productId, String productName, List<String> productImage, String productDescription, String productCategory, Double productPrice, String productUnit, int productQuantity, int productBasketQuantity, int productRating, boolean isRated) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.location = location;
        this.productUserId = productUserId;
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productUnit = productUnit;
        this.productQuantity = productQuantity;
        this.productBasketQuantity = productBasketQuantity;
        this.productRating = productRating;
        this.isRated = isRated;
    }

    public String getProductUserId() {
        return productUserId;
    }

    public void setProductUserId(String productUserId) {
        this.productUserId = productUserId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public int getProductBasketQuantity() {
        return productBasketQuantity;
    }

    public void setProductBasketQuantity(int productBasketQuantity) {
        this.productBasketQuantity = productBasketQuantity;
    }

    public int getProductRating() {
        return productRating;
    }

    public void setProductRating(int productRating) {
        this.productRating = productRating;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }
}
