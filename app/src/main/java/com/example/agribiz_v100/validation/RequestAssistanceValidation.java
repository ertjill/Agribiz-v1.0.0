package com.example.agribiz_v100.validation;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;

public class RequestAssistanceValidation {

    public static String validateRequestAmount(String a) {
        if(TextUtils.isEmpty(a)){
            return "Amount is required.";
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

    public static String validateRequestEquipment(String b) {
        if(TextUtils.isEmpty(b)){
            return "Equipment is required.";
        }
        else if(b.length() < 3){
            return "Equipment must have at least have 3 characters";
        }
        return "";
    }

    public static String validateRequestDescription(String d) {
        if(TextUtils.isEmpty(d)){
            return "Purpose for this request is required.";
        }
        else if(d.length() < 3){
            return "Purpose must have at least have 3 characters";
        }
        return "";
    }

    public static boolean hasPassedOneYear(Date date) {
        long currentDate = Calendar.getInstance().getTimeInMillis();
        long dateToEvaluate = date.getTime();
        long days = (currentDate - dateToEvaluate) / (1000 * 60 * 60 * 24);
        // 1000 => milliseconds to seconds
        // 60 => seconds to minutes
        // 60 => minutes to hours
        // 24 => hours to days
        return days >= 365;
    }

    public static String validateDate(String date) {
        if(TextUtils.isEmpty(date)){
            return "Date is required.";
        }
        return "";
    }
}
