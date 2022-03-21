package com.example.agribiz_v100.entities;

import java.io.Serializable;

public class LocationModel implements Serializable {

    private String userCity;
    private String userMunicipality;
    private String userBarangay;
    private String userStreet;
    private String userSpecificAddress;

    public LocationModel () {

    }

    public LocationModel(String userCity, String userMunicipality, String userBarangay,
                         String userStreet, String userSpecificAddress) {
        this.userCity = userCity;
        this.userMunicipality = userMunicipality;
        this.userBarangay = userBarangay;
        this.userStreet = userStreet;
        this.userSpecificAddress = userSpecificAddress;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
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

    public String getUserStreet() {
        return userStreet;
    }

    public void setUserStreet(String userStreet) {
        this.userStreet = userStreet;
    }

    public String getUserSpecificAddress() {
        return userSpecificAddress;
    }

    public void setUserSpecificAddress(String userSpecificAddress) {
        this.userSpecificAddress = userSpecificAddress;
    }
}
