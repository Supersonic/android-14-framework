package android.hardware.usb.V1_0;
/* loaded from: classes.dex */
public final class PortMode {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == 1) {
            return "UFP";
        }
        if (i == 2) {
            return "DFP";
        }
        if (i == 3) {
            return "DRP";
        }
        if (i == 4) {
            return "NUM_MODES";
        }
        return "0x" + Integer.toHexString(i);
    }
}
