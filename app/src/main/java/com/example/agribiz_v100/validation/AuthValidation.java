package com.example.agribiz_v100.validation;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthValidation {

    public static String validateEmail(String email) {
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

    public static boolean isValidEmail(String email) {
        // Regular expression (regex) to check valid email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
        // Compile the regex
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public static String validatePassword(String password) {
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

    private static boolean isValidPassword(String password) {
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

    public static boolean validatePhoneNumber(String no) {
        Pattern p = Pattern.compile("^(09|\\+639)\\d{9}$");
        if (no == null) {
            return false;
        }
        Matcher m = p.matcher(no);
        return (m.matches());
    }

    public static Toast successToast(Context context, String message){
        Toast successAddProductToast = new Toast(context);
        View successToast = LayoutInflater.from(context).inflate(R.layout.success_toast, null);
        TextView success_message_tv = successToast.findViewById(R.id.success_message_tv);
        success_message_tv.setText(message);
        successAddProductToast.setView(successToast);
        successAddProductToast.setDuration(Toast.LENGTH_LONG);
        successAddProductToast.setGravity(Gravity.CENTER, 0, 0);

        return successAddProductToast;
    }

}
