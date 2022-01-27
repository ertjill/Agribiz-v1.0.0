package com.example.agribiz_v100.customer;

public class BasketHeader {
    String farmId;
    String farmName;
    boolean checked=false;

    public BasketHeader(String farmId, String farmName) {
        this.farmId = farmId;
        this.farmName = farmName;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
