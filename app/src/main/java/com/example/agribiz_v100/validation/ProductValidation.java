package com.example.agribiz_v100.validation;

import android.text.TextUtils;

import java.util.List;

public class ProductValidation {

    public static String validateImages(List<String> images){
        if(images==null) {
            return "Add product image(s)";
        }
        return "";
    }

    public static String validateProductName(String name){
        if(TextUtils.isEmpty(name)){
            return "Enter product name";
        }
        else if(name.length()<3){
            return "Product name must at least have 3 characters";
        }
        return "";
    }

    public static String validateDesc(String desc){
        if(TextUtils.isEmpty(desc)){
            return "Add product description";
        }
        else if(desc.length()<3){
            return "Product description must have at least 3 characters";
        }
        return "";
    }

    public static String validateCategory(String categ){
        if(TextUtils.isEmpty(categ)){
            return "Select Product Category";
        }
        return "";
    }

    public static String validateStocks(String stocks){

        if(TextUtils.isEmpty(stocks)){
            return "Add product stocks";
        }
        try {
            Integer.parseInt(stocks);
        }
        catch (Exception e){
            return "Stocks must be a valid positive numeric value";
        }
        if(Integer.parseInt(stocks)<0){
            return "Invalid stocks value";
        }
        return "";
    }
    public static String validatePrice(String price){
        if(TextUtils.isEmpty(price)){
            return "Add product price";
        }
        try {
            Double.parseDouble(price);
        }
        catch (Exception e){
            return "Price must be a valid positive numeric value";
        }
        if(Double.parseDouble(price)<0){
            return "Invalid price value";
        }
        return "";
    }
    public static String validateQuantity(String quantity){
        if(TextUtils.isEmpty(quantity)){
            return "Add product price";
        }
        try {
            Integer.parseInt(quantity);
        }
        catch (Exception e){
            return "Quantity must be a valid positive numeric value";
        }
        if(Integer.parseInt(quantity)<0){
            return "Invalid quantity value";
        }
        return "";
    }
    public static String validateUnit(String unit){
        if(TextUtils.isEmpty(unit)){
            return "Select Product Unit";
        }
        return "";
    }
}
