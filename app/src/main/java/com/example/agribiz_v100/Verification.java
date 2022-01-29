package com.example.agribiz_v100;

import android.text.TextUtils;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verification {

    public static boolean verifyPhone(String no) {
        Pattern p = Pattern.compile("^(09|\\+639)\\d{9}$");
        if (no == null) {
            return false;
        }
        Matcher m = p.matcher(no);
        return (m.matches());
    }

    public static boolean verifyUsername(String name) {

        // Regex to check valid username.
        String regex = "^[A-Za-z]\\w{5,29}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (name.equals("")) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    public static boolean verifyPassword(String password)
    {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=()])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    public static boolean verifyEmail(String email) {


        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean verifyConfirmPassword(String password, String cpassword) {
        return password.equals(cpassword);
    }

    public static boolean verifyPorductName(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText()))
        {
            textInputLayout.setError("Add Product Name!");
            return false;
        }
        textInputLayout.setError("");
        return true;
    }
    public static boolean verifyPorductDescription(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText()))
        {
            textInputLayout.setError("Add Product Description!");
            return false;
        }
        textInputLayout.setError("");
        return true;
    }
    public static boolean verifyPorductCategory(TextInputLayout textInputLayout, String text){
        if(TextUtils.isEmpty(text))
        {
            textInputLayout.setError("Add Product Category!");
            return false;
        }
        textInputLayout.setError("");
        return true;
    }
    public static boolean verifyPorductStocks(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText())){
            textInputLayout.setError("Add Product Stocks Value");
            return false;
        }
        try{
            Integer.parseInt(textInputLayout.getEditText().getText().toString());
            textInputLayout.setError("");
            return true;
        }
        catch (NumberFormatException e){
            textInputLayout.setError("Input Valid Stocks Integer Value");
            return false;
        }

    }
    public static boolean verifyPorductPrice(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText())){
            textInputLayout.setError("Add Product Price Value");
            return false;
        }
        try{
            Double.parseDouble(textInputLayout.getEditText().getText().toString());
            textInputLayout.setError("");
            return true;
        }
        catch (NumberFormatException e){
            textInputLayout.setError("Input Valid Price Value");
            return false;
        }
    }
    public static boolean verifyPorductQuantity(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText())){
            textInputLayout.setError("Add Product Quantity Value");
            return false;
        }
        try{
            Integer.parseInt(textInputLayout.getEditText().getText().toString());
            textInputLayout.setError("");
            return true;
        }
        catch (NumberFormatException e){
            textInputLayout.setError("Input Valid Quantity Integer Value");
            return false;
        }
    }
    public static boolean verifyPorductUnit(TextInputLayout textInputLayout, String text){
        if(TextUtils.isEmpty(text))
        {
            textInputLayout.setError("Add Product Unit!");
        }
        textInputLayout.setError("");
        return true;
    }


}
