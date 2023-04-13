package android.hardware.health.V2_1;
/* loaded from: classes.dex */
public final class BatteryCapacityLevel {
    public static final String toString(int i) {
        if (i == -1) {
            return "UNSUPPORTED";
        }
        if (i == 0) {
            return "UNKNOWN";
        }
        if (i == 1) {
            return "CRITICAL";
        }
        if (i == 2) {
            return "LOW";
        }
        if (i == 3) {
            return "NORMAL";
        }
        if (i == 4) {
            return "HIGH";
        }
        if (i == 5) {
            return "FULL";
        }
        return "0x" + Integer.toHexString(i);
    }
}
