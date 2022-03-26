package com.example.agribiz_v100.entities;

import java.io.Serializable;

public class LocationModel implements Serializable {

    private String userFullName;
    private String userPhoneNumber;
    private String userRegion;
    private String userProvince;
    private String userMunicipality;
    private String userBarangay;
    private String userZipCode;
    private String userSpecificAddress;

    public LocationModel() {

    }

    public LocationModel(String userFullName, String userPhoneNumber, String userRegion, String userProvince, String userMunicipality, String userBarangay, String userZipCode, String userSpecificAddress) {
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.userRegion = userRegion;
        this.userProvince = userProvince;
        this.userMunicipality = userMunicipality;
        this.userBarangay = userBarangay;
        this.userZipCode = userZipCode;
        this.userSpecificAddress = userSpecificAddress;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(String userRegion) {
        this.userRegion = userRegion;
    }

    public String getUserProvince() {
        return userProvince;
    }

    public void setUserProvince(String userProvince) {
        this.userProvince = userProvince;
    }

    public String getUserMunicipality() {
        return userMunicipality;
    }

    public void setUserMunicipality(String userMunicipality) {
        this.userMunicipality = userMunicipality;
    }

    public String getUserBarangay() {
        return userBarangay;
    }

    public void setUserBarangay(String userBarangay) {
        this.userBarangay = userBarangay;
    }

    public String getUserZipCode() {
        return userZipCode;
    }

    public void setUserZipCode(String userZipCode) {
        this.userZipCode = userZipCode;
    }

    public String getUserSpecificAddress() {
        return userSpecificAddress;
    }

    public void setUserSpecificAddress(String userSpecificAddress) {
        this.userSpecificAddress = userSpecificAddress;
    }
}
