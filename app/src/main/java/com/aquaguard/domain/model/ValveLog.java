package com.aquaguard.domain.model;

public class ValveLog {
    private final String logId;
    private final String deviceId;
    private final String triggeredBy;
    private final String action;
    private final String reason;
    private final long timestamp;

    public ValveLog(String logId, String deviceId, String triggeredBy, String action, String reason, long timestamp) {
        this.logId = logId != null ? logId : "";
        this.deviceId = deviceId != null ? deviceId : "";
        this.triggeredBy = triggeredBy != null ? triggeredBy : "";
        this.action = action != null ? action : "";
        this.reason = reason != null ? reason : "";
        this.timestamp = timestamp;
    }

    public String getLogId() { return logId; }
    public String getDeviceId() { return deviceId; }
    public String getTriggeredBy() { return triggeredBy; }
    public String getAction() { return action; }
    public String getReason() { return reason; }
    public long getTimestamp() { return timestamp; }
}
