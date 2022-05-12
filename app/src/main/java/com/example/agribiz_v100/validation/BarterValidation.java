package com.example.agribiz_v100.validation;

import android.text.TextUtils;

import java.util.List;

public class BarterValidation {

//    public static String validateImages(List<String> images){
//        if(images==null) {
//            return "Add product image(s)";
//        }
//        return "";
//    }

    public static String validateItemName(String name){
        if(TextUtils.isEmpty(name)){
            return "Item name is required.";
        }
        else if(name.length() < 3){
            return "Item name must at least have 3 characters";
        }
        return "";
    }

    public static String validateCondition(String condition){
        if(TextUtils.isEmpty(condition)){
            return "Please select item condition.";
        }
        return "";
    }

    public static String validateQuantity(String quantity){
        if(TextUtils.isEmpty(quantity)){
            return "Item quantity is required.";
        }
        try {
            Integer.parseInt(quantity);
        }
        catch (Exception e){
            return "Quantity must be a valid positive numeric value";
        }
        if(Integer.parseInt(quantity) < 0){
            return "Invalid quantity value";
        }
        return "";
    }

    public static String validateDesc(String desc){
        if(TextUtils.isEmpty(desc)){
            return "Description is required.";
        }
        else if(desc.length() < 3){
            return "Item description must have at least 3 characters";
        }
        return "";
    }
}
