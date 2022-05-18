package com.example.agribiz_v100.entities;

import com.google.firebase.Timestamp;

public class ExpenseModel {
    String expenseId;
    String expenseUserId;
    String expenseName;
    double expenseCost;
    Timestamp expenseDateAdded;

    public ExpenseModel(){

    }

    public ExpenseModel(String expenseId, String expenseUserId, String expenseName, double expenseCost, Timestamp expenseDateAdded) {
        this.expenseId = expenseId;
        this.expenseUserId = expenseUserId;
        this.expenseName = expenseName;
        this.expenseCost = expenseCost;
        this.expenseDateAdded = expenseDateAdded;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseUserId() {
        return expenseUserId;
    }

    public void setExpenseUserId(String expenseUserId) {
        this.expenseUserId = expenseUserId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getExpenseCost() {
        return expenseCost;
    }

    public void setExpenseCost(double expenseCost) {
        this.expenseCost = expenseCost;
    }

    public Timestamp getExpenseDateAdded() {
        return expenseDateAdded;
    }

    public void setExpenseDateAdded(Timestamp epenseDateAdded) {
        this.expenseDateAdded = epenseDateAdded;
    }
}

