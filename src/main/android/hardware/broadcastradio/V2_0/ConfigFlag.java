package android.hardware.broadcastradio.V2_0;
/* loaded from: classes.dex */
public final class ConfigFlag {
    public static final String toString(int i) {
        if (i == 1) {
            return "FORCE_MONO";
        }
        if (i == 2) {
            return "FORCE_ANALOG";
        }
        if (i == 3) {
            return "FORCE_DIGITAL";
        }
        if (i == 4) {
            return "RDS_AF";
        }
        if (i == 5) {
            return "RDS_REG";
        }
        if (i == 6) {
            return "DAB_DAB_LINKING";
        }
        if (i == 7) {
            return "DAB_FM_LINKING";
        }
        if (i == 8) {
            return "DAB_DAB_SOFT_LINKING";
        }
        if (i == 9) {
            return "DAB_FM_SOFT_LINKING";
        }
        return "0x" + Integer.toHexString(i);
    }
}
