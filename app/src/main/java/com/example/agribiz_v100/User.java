package com.example.agribiz_v100;

import android.util.SparseArray;

public class User {
    private String userId;
    private String userName;
    private String userType;
    private String userEmail;
    private String userPhoneNo;
    private String userStatus;
    private SparseArray<String> userLocation;

    public User() {

    }

    public User(String userId, String userName, String userType, String userEmail, String userPhoneNo, String userStatus, SparseArray<String> userLocation) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.userEmail = userEmail;
        this.userPhoneNo = userPhoneNo;
        this.userStatus = userStatus;
        this.userLocation = userLocation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public SparseArray<String> getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(SparseArray<String> userLocation) {
        this.userLocation = userLocation;
    }
}
