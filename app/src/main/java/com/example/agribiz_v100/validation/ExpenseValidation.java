package com.example.agribiz_v100.validation;

import android.text.TextUtils;

public class ExpenseValidation {

    public static String validateExpenseName(String expenseName) {
        if(TextUtils.isEmpty(expenseName)){
            return "Expense must have a name";
        }
        else if(expenseName.length() < 3){
            return "Expense name must have at least have 3 characters";
        }
        return "";
    }

    public static String validateExpenseCost(String cost){
        if(TextUtils.isEmpty(cost)){
            return "Add an amount";
        }
        try {
            Double.parseDouble(cost);
        }
        catch (Exception e){
            return "Total cost must be a valid positive numeric value";
        }
        if(Double.parseDouble(cost) < 0){
            return "Invalid cost value";
        }
        return "";
    }
}
