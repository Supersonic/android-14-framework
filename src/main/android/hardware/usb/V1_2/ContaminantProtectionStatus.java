package android.hardware.usb.V1_2;
/* loaded from: classes.dex */
public final class ContaminantProtectionStatus {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == 1) {
            return "FORCE_SINK";
        }
        if (i == 2) {
            return "FORCE_SOURCE";
        }
        if (i == 4) {
            return "FORCE_DISABLE";
        }
        if (i == 8) {
            return "DISABLED";
        }
        return "0x" + Integer.toHexString(i);
    }
}
