package android.hardware.health.V2_0;
/* loaded from: classes.dex */
public final class Result {
    public static final String toString(int i) {
        if (i == 0) {
            return "SUCCESS";
        }
        if (i == 1) {
            return "NOT_SUPPORTED";
        }
        if (i == 2) {
            return "UNKNOWN";
        }
        if (i == 3) {
            return "NOT_FOUND";
        }
        if (i == 4) {
            return "CALLBACK_DIED";
        }
        return "0x" + Integer.toHexString(i);
    }
}
