package com.aquaguard.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_usage")
public class UsageEntity {
    @PrimaryKey
    @NonNull
    private final String id; // Format: deviceId_date
    private final String deviceId;
    private final String date;
    private final float totalLiters;
    private final float waterSavedLiters;

    public UsageEntity(@NonNull String id, String deviceId, String date, float totalLiters, float waterSavedLiters) {
        this.id = id;
        this.deviceId = deviceId;
        this.date = date;
        this.totalLiters = totalLiters;
        this.waterSavedLiters = waterSavedLiters;
    }

    @NonNull
    public String getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getDate() { return date; }
    public float getTotalLiters() { return totalLiters; }
    public float getWaterSavedLiters() { return waterSavedLiters; }
}
