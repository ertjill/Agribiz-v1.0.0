package com.example.agribiz_v100.entities;

import java.io.Serializable;

public class AssistanceModel implements Serializable {

    private String assistanceID;
    private String assistanceType;
    private String assistanceAmount;
    private String assistanceDescription;

    public AssistanceModel() {
    }

    public AssistanceModel(String assistanceID, String assistanceType, String assistanceAmount, String assistanceDescription) {
        this.assistanceID = assistanceID;
        this.assistanceType = assistanceType;
        this.assistanceAmount = assistanceAmount;
        this.assistanceDescription = assistanceDescription;
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

    public String getAssistanceAmount() {
        return assistanceAmount;
    }

    public void setAssistanceAmount(String assistanceAmount) {
        this.assistanceAmount = assistanceAmount;
    }

    public String getAssistanceDescription() {
        return assistanceDescription;
    }

    public void setAssistanceDescription(String assistanceDescription) {
        this.assistanceDescription = assistanceDescription;
    }
}
