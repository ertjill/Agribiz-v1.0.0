package com.example.agribiz_v100.responses;

public class AuthResponse {

    public static String getAuthError(String err) {

        String result;

        switch (err) {
            case "ERROR_INVALID_CUSTOM_TOKEN":
                result = "The custom token format is incorrect. Please check the documentation.";
                break;
            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                result = "The custom token corresponds to a different audience.";
                break;
            case "ERROR_INVALID_CREDENTIAL":
                result = "The supplied auth credential is malformed or has expired.";
                break;
            case "ERROR_INVALID_EMAIL":
                result = "The email address is badly formatted.";
                break;
            case "ERROR_WRONG_PASSWORD":
                result = "The password is invalid or the user does not have a password.";
                break;
            case "ERROR_USER_MISMATCH":
                result = "The supplied credentials do not correspond to the previously signed in user.";
                break;
            case "ERROR_REQUIRES_RECENT_LOGIN":
                result = "This operation is sensitive and requires recent authentication. Log in again before retrying this request.";
                break;
            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                result = "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.";
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                result = "The email address is already in use by another account.";
                break;
            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                result = "This credential is already associated with a different user account.";
                break;
            case "ERROR_USER_DISABLED":
                result = "The user account has been disabled by an administrator.";
                break;
            case "ERROR_USER_TOKEN_EXPIRED":
                result = "The user\\'s credential is no longer valid. The user must sign in again.";
                break;
            case "ERROR_USER_NOT_FOUND":
                result = "There is no user record corresponding to this identifier. The user may have been deleted.";
                break;
            case "ERROR_INVALID_USER_TOKEN":
                result = "The user must sign in again. The user\\'s credential is no longer valid.";
                break;
            case "ERROR_OPERATION_NOT_ALLOWED":
                result = "This operation is not allowed. You must enable this service in the console.";
                break;
            case "ERROR_WEAK_PASSWORD":
                result = "The given password is invalid.";
                break;
            default:
                result = "Unknown error";
        }
        return result;
    }
}
