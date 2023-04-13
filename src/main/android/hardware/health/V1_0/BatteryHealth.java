package android.hardware.health.V1_0;
/* loaded from: classes.dex */
public final class BatteryHealth {
    public static final String toString(int i) {
        if (i == 1) {
            return "UNKNOWN";
        }
        if (i == 2) {
            return "GOOD";
        }
        if (i == 3) {
            return "OVERHEAT";
        }
        if (i == 4) {
            return "DEAD";
        }
        if (i == 5) {
            return "OVER_VOLTAGE";
        }
        if (i == 6) {
            return "UNSPECIFIED_FAILURE";
        }
        if (i == 7) {
            return "COLD";
        }
        return "0x" + Integer.toHexString(i);
    }
}
