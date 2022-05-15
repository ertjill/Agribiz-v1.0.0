package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class DonateModel implements Serializable {

    private String donateUserId;
    private String donateID;
    private String donateFarmerName;
    private String donateAmount;
    private String donateMessage;
    private String donateUrl;
    private Timestamp donateDateUploaded;

    public DonateModel() {

    }

    public DonateModel(String donateUserId, String donateID, String donateFarmerName, String donateAmount, String donateMessage, String donateUrl, Timestamp donateDateUploaded) {
        this.donateUserId = donateUserId;
        this.donateID = donateID;
        this.donateFarmerName = donateFarmerName;
        this.donateAmount = donateAmount;
        this.donateMessage = donateMessage;
        this.donateUrl = donateUrl;
        this.donateDateUploaded = donateDateUploaded;
    }

    public String getDonateUserId() {
        return donateUserId;
    }

    public void setDonateUserId(String donateUserId) {
        this.donateUserId = donateUserId;
    }

    public String getDonateID() {
        return donateID;
    }

    public void setDonateID(String donateID) {
        this.donateID = donateID;
    }

    public String getDonateFarmerName() {
        return donateFarmerName;
    }

    public void setDonateFarmerName(String donateFarmerName) {
        this.donateFarmerName = donateFarmerName;
    }

    public String getDonateAmount() {
        return donateAmount;
    }

    public void setDonateAmount(String donateAmount) {
        this.donateAmount = donateAmount;
    }

    public String getDonateMessage() {
        return donateMessage;
    }

    public void setDonateMessage(String donateMessage) {
        this.donateMessage = donateMessage;
    }

    public String getDonateUrl() {
        return donateUrl;
    }

    public void setDonateUrl(String donateUrl) {
        this.donateUrl = donateUrl;
    }

    public Timestamp getDonateDateUploaded() {
        return donateDateUploaded;
    }

    public void setDonateDateUploaded(Timestamp donateDateUploaded) {
        this.donateDateUploaded = donateDateUploaded;
    }
}
