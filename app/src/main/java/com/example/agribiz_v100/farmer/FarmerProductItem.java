package com.example.agribiz_v100.farmer;

import com.example.agribiz_v100.ProductItem;

public class FarmerProductItem extends ProductItem {
    private int productStocks;
    private int productSold=0;

    public int getProductSold() {
        return productSold;
    }

    public void setProductSold(int productSold) {
        this.productSold = productSold;
    }
    public FarmerProductItem(String productId, String productFarmId, String productName, Double productPrice, String productUnit, String productImage, int productStocks, String productDescription, String productCategory, int productQuantity) {
        super(productId, productFarmId, productName, productPrice, productUnit, productQuantity, productImage, productDescription, productCategory);
        this.productStocks=productStocks;
    }

    public int getProductStocks() {
        return productStocks;
    }

    public void setProductStocks(int productStocks) {
        this.productStocks = productStocks;
    }
}