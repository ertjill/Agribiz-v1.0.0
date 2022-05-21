package com.example.agribiz_v100.entities;

import java.io.Serializable;
import java.util.List;

public class ChatThreadUserModel implements Serializable {
    private String userEmail;
    private String userID;
    private String userImage;
    private String userDisplayName;
    private String userFirstName;
    private String userLastName;
    private String userMiddleName;
    private String userPhoneNumber;
    private String userType;
    private String userStatus;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public ChatThreadUserModel() {
    }

    public ChatThreadUserModel(String userEmail, String userID, String userImage, String userDisplayName, String userFirstName, String userLastName, String userMiddleName, String userPhoneNumber, boolean userIsActive, String userType, String userStatus) {
        this.userEmail = userEmail;
        this.userID = userID;
        this.userImage = userImage;
        this.userDisplayName = userDisplayName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userMiddleName = userMiddleName;
        this.userPhoneNumber = userPhoneNumber;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserMiddleName() {
        return userMiddleName;
    }

    public void setUserMiddleName(String userMiddleName) {
        this.userMiddleName = userMiddleName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
