package com.android.server.display.config;
/* loaded from: classes.dex */
public enum ThermalStatus {
    none("none"),
    light("light"),
    moderate("moderate"),
    severe("severe"),
    critical("critical"),
    emergency("emergency"),
    shutdown("shutdown");
    
    private final String rawName;

    ThermalStatus(String str) {
        this.rawName = str;
    }

    public String getRawName() {
        return this.rawName;
    }

    public static ThermalStatus fromString(String str) {
        ThermalStatus[] values;
        for (ThermalStatus thermalStatus : values()) {
            if (thermalStatus.getRawName().equals(str)) {
                return thermalStatus;
            }
        }
        throw new IllegalArgumentException(str);
    }
}
