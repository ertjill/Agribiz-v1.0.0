package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class BarterModel implements Serializable {

    private String barterUserId;
    private String barterType;
    private String barterCustomerId;
    private String barterId;
    private List<String> barterImage;
    private String barterName;
    private String barterCondition;
    private int barterQuantity;
    private String barterDescription;
    private String barterStatus;
    private Timestamp barterDateUploaded;


    public BarterModel() {

    }

    public BarterModel(String barterUserId, String barterType, String barterCustomerId, String barterId, List<String> barterImage, String barterName, String barterCondition, int barterQuantity, String barterDescription, String barterStatus, Timestamp barterDateUploaded) {
        this.barterUserId = barterUserId;
        this.barterType = barterType;
        this.barterCustomerId = barterCustomerId;
        this.barterId = barterId;
        this.barterImage = barterImage;
        this.barterName = barterName;
        this.barterCondition = barterCondition;
        this.barterQuantity = barterQuantity;
        this.barterDescription = barterDescription;
        this.barterStatus = barterStatus;
        this.barterDateUploaded = barterDateUploaded;
    }

    public String getBarterUserId() {
        return barterUserId;
    }

    public void setBarterUserId(String barterUserId) {
        this.barterUserId = barterUserId;
    }

    public String getBarterCustomerId() {
        return barterCustomerId;
    }

    public void setBarterCustomerId(String barterCustomerId) {
        this.barterCustomerId = barterCustomerId;
    }

    public String getBarterId() {
        return barterId;
    }

    public void setBarterId(String barterId) {
        this.barterId = barterId;
    }

    public String getBarterType() {
        return barterType;
    }

    public void setBarterType(String barterType) {
        this.barterType = barterType;
    }

    public List<String> getBarterImage() {
        return barterImage;
    }

    public void setBarterImage(List<String> barterImage) {
        this.barterImage = barterImage;
    }

    public String getBarterName() {
        return barterName;
    }

    public void setBarterName(String barterName) {
        this.barterName = barterName;
    }

    public String getBarterCondition() {
        return barterCondition;
    }

    public void setBarterCondition(String barterCondition) {
        this.barterCondition = barterCondition;
    }

    public int getBarterQuantity() {
        return barterQuantity;
    }

    public void setBarterQuantity(int barterQuantity) {
        this.barterQuantity = barterQuantity;
    }

    public String getBarterDescription() {
        return barterDescription;
    }

    public void setBarterDescription(String barterDescription) {
        this.barterDescription = barterDescription;
    }

    public String getBarterStatus() {
        return barterStatus;
    }

    public void setBarterStatus(String barterStatus) {
        this.barterStatus = barterStatus;
    }

    public Timestamp getBarterDateUploaded() {
        return barterDateUploaded;
    }

    public void setBarterDateUploaded(Timestamp barterDateUploaded) {
        this.barterDateUploaded = barterDateUploaded;
    }
}
