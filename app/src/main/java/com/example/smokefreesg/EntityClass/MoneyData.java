package com.example.smokefreesg.EntityClass;

public class MoneyData {

    private int weekNumber;
    private float totalAmount;

    public MoneyData (float totalAmount, int weekNumber) {
        this.weekNumber = weekNumber;
        this.totalAmount = totalAmount;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public int getWeekNumber() {
        return weekNumber;
    }
}
