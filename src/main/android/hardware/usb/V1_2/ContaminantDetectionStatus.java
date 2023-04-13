package android.hardware.usb.V1_2;
/* loaded from: classes.dex */
public final class ContaminantDetectionStatus {
    public static final String toString(int i) {
        if (i == 0) {
            return "NOT_SUPPORTED";
        }
        if (i == 1) {
            return "DISABLED";
        }
        if (i == 2) {
            return "NOT_DETECTED";
        }
        if (i == 3) {
            return "DETECTED";
        }
        return "0x" + Integer.toHexString(i);
    }
}
