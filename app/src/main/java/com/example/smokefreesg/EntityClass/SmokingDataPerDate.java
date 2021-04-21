package com.example.smokefreesg.EntityClass;

public class SmokingDataPerDate {

    private int noOfCigs;
    private boolean goalCheck;

    public SmokingDataPerDate ( int noOfCigs, boolean goalCheck) {
        this.noOfCigs = noOfCigs;
        this.goalCheck =goalCheck;
    }

    public int getNoOfCigs() {
        return noOfCigs;
    }

    public boolean isGoalCheck() {
        return goalCheck;
    }
}
