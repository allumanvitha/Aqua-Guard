package com.aquaguard.domain.model;

public class WaterReading {
    private final float flowRate;
    private final int waterLevelPct;
    private final boolean leakDetected;
    private final boolean valveOpen;
    private final boolean autoMode;
    private final long lastSeen;

    public WaterReading(float flowRate, int waterLevelPct, boolean leakDetected, boolean valveOpen, boolean autoMode, long lastSeen) {
        this.flowRate = flowRate;
        this.waterLevelPct = waterLevelPct;
        this.leakDetected = leakDetected;
        this.valveOpen = valveOpen;
        this.autoMode = autoMode;
        this.lastSeen = lastSeen;
    }

    public float getFlowRate() { return flowRate; }
    public int getWaterLevelPct() { return waterLevelPct; }
    public boolean isLeakDetected() { return leakDetected; }
    public boolean isValveOpen() { return valveOpen; }
    public boolean isAutoMode() { return autoMode; }
    public long getLastSeen() { return lastSeen; }
}
