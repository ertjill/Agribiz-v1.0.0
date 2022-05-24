package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class AssistanceModel implements Serializable {

    private String assistanceUserID;
    private String assistanceID;
    private String assistanceType;
    private String assistanceAmountEquipment;
    private String assistanceDescription;
    private String assistanceStatus;
    private String assistanceRepayDate;
    private Timestamp assistanceDateRequested;

    public AssistanceModel() {

    }

    public AssistanceModel(String assistanceUserID, String assistanceID, String assistanceType, String assistanceAmountEquipment, String assistanceDescription, String assistanceStatus, String assistanceRepayDate, Timestamp assistanceDateRequested) {
        this.assistanceUserID =assistanceUserID;
        this.assistanceID = assistanceID;
        this.assistanceType = assistanceType;
        this.assistanceAmountEquipment = assistanceAmountEquipment;
        this.assistanceDescription = assistanceDescription;
        this.assistanceStatus = assistanceStatus;
        this.assistanceRepayDate = assistanceRepayDate;
        this.assistanceDateRequested = assistanceDateRequested;
    }

    public String getAssistanceUserID() {
        return assistanceUserID;
    }

    public void setAssistanceUserID(String assistanceUserID) {
        this.assistanceUserID = assistanceUserID;
    }

    public String getAssistanceID() {
        return assistanceID;
    }

    public void setAssistanceID(String assistanceID) {
        this.assistanceID = assistanceID;
    }

    public String getAssistanceType() {
        return assistanceType;
    }

    public void setAssistanceType(String assistanceType) {
        this.assistanceType = assistanceType;
    }

    public String getAssistanceAmountEquipment() {
        return assistanceAmountEquipment;
    }

    public void setAssistanceAmountEquipment(String assistanceAmountEquipment) {
        this.assistanceAmountEquipment = assistanceAmountEquipment;
    }

    public String getAssistanceDescription() {
        return assistanceDescription;
    }

    public void setAssistanceDescription(String assistanceDescription) {
        this.assistanceDescription = assistanceDescription;
    }

    public String getAssistanceStatus() {
        return assistanceStatus;
    }

    public void setAssistanceStatus(String assistanceStatus) {
        this.assistanceStatus = assistanceStatus;
    }

    public String getAssistanceRepayDate() {
        return assistanceRepayDate;
    }

    public void setAssistanceRepayDate(String assistanceRepayDate) {
        this.assistanceRepayDate = assistanceRepayDate;
    }

    public Timestamp getAssistanceDateRequested() {
        return assistanceDateRequested;
    }

    public void setAssistanceDateRequested(Timestamp assistanceDateRequested) {
        this.assistanceDateRequested = assistanceDateRequested;
    }
}
