package android.hardware.biometrics.face.V1_0;
/* loaded from: classes.dex */
public final class Status {
    public static final String toString(int i) {
        if (i == 0) {
            return "OK";
        }
        if (i == 1) {
            return "ILLEGAL_ARGUMENT";
        }
        if (i == 2) {
            return "OPERATION_NOT_SUPPORTED";
        }
        if (i == 3) {
            return "INTERNAL_ERROR";
        }
        if (i == 4) {
            return "NOT_ENROLLED";
        }
        return "0x" + Integer.toHexString(i);
    }
}
