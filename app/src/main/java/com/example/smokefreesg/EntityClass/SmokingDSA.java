package com.example.smokefreesg.EntityClass;

import java.util.ArrayList;
import java.util.HashMap;

public class SmokingDSA {
    private double latitude;
    private double longitude;
    private String locationName;
    private ArrayList<String> reportArray;
    private String verified;

    public SmokingDSA(double latitude, double longitude, String locationName, String verify) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.reportArray = new ArrayList<String>();
        this.verified = verify;
    }

    public HashMap<String, Object> dsaToDb() {
        HashMap<String, Object> smokingPoint = new HashMap<>();
        smokingPoint.put("latitude", this.latitude);
        smokingPoint.put("longitude", this.longitude);
        smokingPoint.put("name", this.locationName);
        smokingPoint.put("report", this.reportArray);
        smokingPoint.put("verified", this.verified);
        return smokingPoint;
    }
}
