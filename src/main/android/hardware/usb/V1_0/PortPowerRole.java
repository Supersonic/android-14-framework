package android.hardware.usb.V1_0;
/* loaded from: classes.dex */
public final class PortPowerRole {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == 1) {
            return "SOURCE";
        }
        if (i == 2) {
            return "SINK";
        }
        if (i == 3) {
            return "NUM_POWER_ROLES";
        }
        return "0x" + Integer.toHexString(i);
    }
}
