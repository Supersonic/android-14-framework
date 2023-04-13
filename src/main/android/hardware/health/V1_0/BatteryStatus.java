package android.hardware.health.V1_0;
/* loaded from: classes.dex */
public final class BatteryStatus {
    public static final String toString(int i) {
        if (i == 1) {
            return "UNKNOWN";
        }
        if (i == 2) {
            return "CHARGING";
        }
        if (i == 3) {
            return "DISCHARGING";
        }
        if (i == 4) {
            return "NOT_CHARGING";
        }
        if (i == 5) {
            return "FULL";
        }
        return "0x" + Integer.toHexString(i);
    }
}
