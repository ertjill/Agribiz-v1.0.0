package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class OrderProductModel extends BasketProductModel implements Serializable {
    String orderID;
    Timestamp orderDate;
    String orderStatus="pending";
    LocationModel location;

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

    public OrderProductModel(int productBasketQuantity, Timestamp productDateAdded, boolean checked, String orderID, Timestamp orderDate, String orderStatus, LocationModel location) {
        super(productBasketQuantity, productDateAdded, checked);
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.location = location;
    }

    public OrderProductModel(String productId, String productUserId, String productName, String productDescription, List<String> productImage, String productCategory, Double productPrice, String productUnit, int productQuantity, int productStocks, int productSold, int productRating, int productNoCustomerRate, Timestamp productDateUploaded, Double productShippingFee, Double productOldPrice, int productSalesTo, int productBasketQuantity, Timestamp productDateAdded, boolean checked, String orderID, Timestamp orderDate, String orderStatus, LocationModel location) {
        super(productId, productUserId, productName, productDescription, productImage, productCategory, productPrice, productUnit, productQuantity, productStocks, productSold, productRating, productNoCustomerRate, productDateUploaded, productShippingFee, productOldPrice, productSalesTo, productBasketQuantity, productDateAdded, checked);
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.location = location;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }
}
