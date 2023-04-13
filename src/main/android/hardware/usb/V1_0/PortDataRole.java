package android.hardware.usb.V1_0;
/* loaded from: classes.dex */
public final class PortDataRole {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == 1) {
            return "HOST";
        }
        if (i == 2) {
            return "DEVICE";
        }
        if (i == 3) {
            return "NUM_DATA_ROLES";
        }
        return "0x" + Integer.toHexString(i);
    }
}
