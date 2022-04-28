package com.example.agribiz_v100.validation;

import android.text.TextUtils;

public class RequestAssistanceValidation {

    public static String validateRequestDescription(String d) {
        if(TextUtils.isEmpty(d)){
            return "Enter request purpose";
        }
        else if(d.length() < 3){
            return "Description must have at least have 3 characters";
        }
        return "";
    }
}
