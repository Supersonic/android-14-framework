package android.hardware.broadcastradio.V2_0;
/* loaded from: classes.dex */
public final class Result {
    public static final String toString(int i) {
        if (i == 0) {
            return "OK";
        }
        if (i == 1) {
            return "UNKNOWN_ERROR";
        }
        if (i == 2) {
            return "INTERNAL_ERROR";
        }
        if (i == 3) {
            return "INVALID_ARGUMENTS";
        }
        if (i == 4) {
            return "INVALID_STATE";
        }
        if (i == 5) {
            return "NOT_SUPPORTED";
        }
        if (i == 6) {
            return "TIMEOUT";
        }
        return "0x" + Integer.toHexString(i);
    }
}
