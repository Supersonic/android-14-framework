package android.hardware.usb.V1_0;
/* loaded from: classes.dex */
public final class PortRoleType {
    public static final String toString(int i) {
        if (i == 0) {
            return "DATA_ROLE";
        }
        if (i == 1) {
            return "POWER_ROLE";
        }
        if (i == 2) {
            return "MODE";
        }
        return "0x" + Integer.toHexString(i);
    }
}
