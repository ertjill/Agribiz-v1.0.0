package com.example.agribiz_v100.validation;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.Util;

public class AuthValidation {

    public String validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Required email";
        }
        else if (!isValidEmail(email)) {
            return "Invalid email. Please provide a proper formatted email address.";
        }
        else {
            return "";
        }
    }

    private boolean isValidEmail(String email) {
        // Regular expression (regex) to check valid email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
        // Compile the regex
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public String validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Required password";
        }
        else if (!isValidPassword(password)) {
            return "Invalid password.\nPassword must contain at least one UPPERCASE, lowercase, and a number.";
        }
        else {
            return "";
        }
    }

    private boolean isValidPassword(String password) {
        // Regular expression (regex) to check valid password
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=\\S+$).{8,20}$";
        // Compile the regex
        Pattern p = Pattern.compile(regex);
        // Pattern class contains matcher() method to find matching between given password
        Matcher m = p.matcher(password);
        // Return if the password matched the regex
        return m.matches();
    }


}
