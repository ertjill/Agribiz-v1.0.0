package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class BasketProductModel extends ProductModel implements Serializable {
    private int productBasketQuantity;
    private Timestamp productDateAdded;
    private String customerId;
    private boolean checked=false;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Timestamp getProductDateAdded() {
        return productDateAdded;
    }

    public void setProductDateAdded(Timestamp productDateAdded) {
        this.productDateAdded = productDateAdded;
    }

    public BasketProductModel() {

    }

    public BasketProductModel(int productBasketQuantity, Timestamp productDateAdded, String customerId, boolean checked) {
        this.productBasketQuantity = productBasketQuantity;
        this.productDateAdded = productDateAdded;
        this.customerId = customerId;
        this.checked = checked;
    }

    public BasketProductModel(String productId, String productUserId, String productName, String productDescription, List<String> productImage, String productCategory, Double productPrice, String productUnit, int productQuantity, int productStocks, int productSold, int productRating, int productNoCustomerRate, Timestamp productDateUploaded, Double productShippingFee, Double productOldPrice, int productSalesTo, int productBasketQuantity, Timestamp productDateAdded, String customerId, boolean checked) {
        super(productId, productUserId, productName, productDescription, productImage, productCategory, productPrice, productUnit, productQuantity, productStocks, productSold, productRating, productNoCustomerRate, productDateUploaded, productShippingFee, productOldPrice, productSalesTo);
        this.productBasketQuantity = productBasketQuantity;
        this.productDateAdded = productDateAdded;
        this.customerId = customerId;
        this.checked = checked;
    }

    public int getProductBasketQuantity() {
        return productBasketQuantity;
    }

    public void setProductBasketQuantity(int productBasketQuantity) {
        this.productBasketQuantity = productBasketQuantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
