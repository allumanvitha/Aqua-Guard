package com.aquaguard.domain.model;

public class Alert {
    private final String alertId;
    private final String deviceId;
    private final AlertType type;
    private final AlertSeverity severity;
    private final String message;
    private final long timestamp;
    private final boolean resolved;

    public Alert(String alertId, String deviceId, AlertType type, AlertSeverity severity, String message, long timestamp, boolean resolved) {
        this.alertId = alertId;
        this.deviceId = deviceId;
        this.type = type != null ? type : AlertType.SENSOR_FAILURE;
        this.severity = severity != null ? severity : AlertSeverity.INFO;
        this.message = message;
        this.timestamp = timestamp;
        this.resolved = resolved;
    }

    public Alert copyWithAlertId(String newAlertId) {
        return new Alert(newAlertId, this.deviceId, this.type, this.severity, this.message, this.timestamp, this.resolved);
    }

    public String getAlertId() { return alertId; }
    public String getDeviceId() { return deviceId; }
    public AlertType getType() { return type; }
    public AlertSeverity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isResolved() { return resolved; }

    public String getTitle() {
        return type != null ? type.name().replace("_", " ") : "ALERT";
    }

    public boolean isActive() {
        return !resolved;
    }

    public enum AlertType {
        LEAK_DETECTED,
        OVERFLOW_PREVENTED,
        EXCESSIVE_USAGE,
        DEVICE_OFFLINE,
        SENSOR_FAILURE
    }

    public enum AlertSeverity {
        CRITICAL,
        WARNING,
        INFO
    }
}
