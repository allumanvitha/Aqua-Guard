package com.aquaguard.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alerts")
public class AlertEntity {
    @PrimaryKey
    @NonNull
    private final String alertId;
    private final String deviceId;
    private final String type;
    private final String severity;
    private final String message;
    private final long timestamp;
    private final boolean resolved;

    public AlertEntity(@NonNull String alertId, String deviceId, String type, String severity, String message, long timestamp, boolean resolved) {
        this.alertId = alertId;
        this.deviceId = deviceId;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.resolved = resolved;
    }

    @NonNull
    public String getAlertId() { return alertId; }
    public String getDeviceId() { return deviceId; }
    public String getType() { return type; }
    public String getSeverity() { return severity; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isResolved() { return resolved; }
}
