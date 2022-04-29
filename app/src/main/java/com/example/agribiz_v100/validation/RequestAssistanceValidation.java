package com.example.agribiz_v100.validation;

import android.text.TextUtils;

public class RequestAssistanceValidation {

    public static String validateRequestAmount(String a) {
        if(TextUtils.isEmpty(a)){
            return "Amount is required";
        }
        try {
            Integer.parseInt(a);
        }
        catch (Exception e){
            return "Amount must be a valid positive numeric value";
        }
        if(Integer.parseInt(a) < 0){
            return "Invalid amount value";
        }
        return "";
    }

    public static String validateRequestDescription(String d) {
        if(TextUtils.isEmpty(d)){
            return "Description is required";
        }
        else if(d.length() < 3){
            return "Description must have at least have 3 characters";
        }
        return "";
    }
}
