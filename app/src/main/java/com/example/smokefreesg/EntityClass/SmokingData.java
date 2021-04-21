package com.example.smokefreesg.EntityClass;

public class SmokingData {

    private int weekNumber;
    private int noOfCigs;

    public SmokingData ( int noOfCigs, int weekNumber) {
        this.noOfCigs = noOfCigs;
        this.weekNumber = weekNumber;
    }

    public int getNoOfCigs() {
        return noOfCigs;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

}
