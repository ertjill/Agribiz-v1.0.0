package com.example.agribiz_v100.validation;

import android.text.TextUtils;

public class DonateValidation {

    public static String validateShortMessage(String shortMessage) {
        if(TextUtils.isEmpty(shortMessage)){
            return "Message cannot be blank.";
        }
        else if(shortMessage.length() < 4){
            return "Message must have at least have 4 characters";
        }
        return "";
    }

    public static String validateAmount(String amt){
        if(TextUtils.isEmpty(amt)){
            return "Amount is required.";
        }
        try {
            Double.parseDouble(amt);
        }
        catch (Exception e){
            return "Amount must be a valid positive numeric value";
        }
        if(Double.parseDouble(amt) < 0){
            return "Invalid amount value";
        }
        return "";
    }
}
