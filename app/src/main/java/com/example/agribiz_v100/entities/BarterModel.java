package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class BarterModel implements Serializable {

    private String barterUserId;
    private String barterId;
    private List<String> barterImage;
    private String barterName;
    private String barterCondition;
    private int barterQuantity;
    private String barterDescription;
    private Timestamp barterDateUploaded;

    public BarterModel() {

    }

    public BarterModel(String barterUserId, String barterId, List<String> barterImage, String barterName, String barterCondition, int barterQuantity, String barterDescription, Timestamp barterDateUploaded) {
        this.barterUserId = barterUserId;
        this.barterId = barterId;
        this.barterImage = barterImage;
        this.barterName = barterName;
        this.barterCondition = barterCondition;
        this.barterQuantity = barterQuantity;
        this.barterDescription = barterDescription;
        this.barterDateUploaded = barterDateUploaded;
    }

    public String getBarterUserId() {
        return barterUserId;
    }

    public void setBarterUserId(String barterUserId) {
        this.barterUserId = barterUserId;
    }

    public String getBarterId() {
        return barterId;
    }

    public void setBarterId(String barterId) {
        this.barterId = barterId;
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

    public Timestamp getBarterDateUploaded() {
        return barterDateUploaded;
    }

    public void setBarterDateUploaded(Timestamp barterDateUploaded) {
        this.barterDateUploaded = barterDateUploaded;
    }
}
