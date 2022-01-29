package com.example.agribiz_v100;

public class ProductItem {
    private String productId;
    private String productFarmId;
    private String productName;
    private Double productPrice;
    private String productUnit;
    private int productQuantity;
    private String productImage;
    private String productDescription;
    private String productCategory;

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

    public String getProductImage() {
        return productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public ProductItem(String productId, String productFarmId, String productName, Double productPrice, String productUnit, int productQuantity, String productImage, String productDescription, String productCategory) {
        this.productId = productId;
        this.productFarmId = productFarmId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productUnit = productUnit;
        this.productQuantity = productQuantity;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
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
