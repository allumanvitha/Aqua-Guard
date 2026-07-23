package com.aquaguard.domain.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String uid;
    private final String email;
    private final String displayName;
    private final String photoUrl;
    private final int familyMembers;
    private final int dailyTargetLiters;
    private final List<String> connectedDevices;

    public User(String uid, String email, String displayName, String photoUrl, int familyMembers, int dailyTargetLiters, List<String> connectedDevices) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.familyMembers = familyMembers;
        this.dailyTargetLiters = dailyTargetLiters;
        this.connectedDevices = connectedDevices != null ? connectedDevices : new ArrayList<>();
    }

    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public int getFamilyMembers() { return familyMembers; }
    public int getDailyTargetLiters() { return dailyTargetLiters; }
    public List<String> getConnectedDevices() { return connectedDevices; }
    public String getName() { return displayName; }
    public boolean isAdmin() { return "admin@aquaguard.com".equalsIgnoreCase(email); }
}
