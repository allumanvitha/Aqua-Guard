package com.aquaguard.domain.model;

public class Device {
    private final String deviceId;
    private final String deviceName;
    private final String ownerUid;
    private final long registeredAt;
    private final String firmwareVersion;
    private final String status;
    private final int batteryLevel;

    public Device(String deviceId, String deviceName, String ownerUid, long registeredAt, String firmwareVersion, String status, int batteryLevel) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.ownerUid = ownerUid;
        this.registeredAt = registeredAt;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.batteryLevel = batteryLevel;
    }

    public String getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public String getOwnerUid() { return ownerUid; }
    public long getRegisteredAt() { return registeredAt; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public String getStatus() { return status; }
    public int getBatteryLevel() { return batteryLevel; }
    public String getName() { return deviceName; }
    public boolean isOnline() { return "ONLINE".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status); }
}
