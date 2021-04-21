package com.example.smokefreesg.EntityClass;

public class UserAccount {
    String userName;
    String userEmail;
    Integer packCost;
    Integer packSticksNum;
    Integer goalCigNum;
    Integer goalSpend;
    Integer cigsLeft;
    Integer userPoints;


    public UserAccount (String userName, String userEmail, Integer packCost,Integer packSticksNum, Integer goalCigNum, Integer goalSpend, Integer cigsLeft,Integer userPoints){

        this.userName = userName;
        this.userEmail = userEmail;
        this.packCost = packCost;
        this.packSticksNum = packSticksNum;
        this.goalCigNum = goalCigNum;
        this.goalSpend = goalSpend;
        this.cigsLeft = cigsLeft;
        this.userPoints = userPoints;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getPackCost() { return packCost; }

    public Integer getPackSticksNum() { return packSticksNum; }

    public Integer getGoalCigNum() { return goalCigNum; }

    public Integer getGoalSpend() { return goalSpend; }

    public Integer getCigsLeft() { return cigsLeft; }

    public Integer getUserPoints() {
        return userPoints;
    }
}
