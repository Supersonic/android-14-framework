package android.hardware.radio.config.V1_0;
/* loaded from: classes.dex */
public final class SlotState {
    public static final String toString(int i) {
        if (i == 0) {
            return "INACTIVE";
        }
        if (i == 1) {
            return "ACTIVE";
        }
        return "0x" + Integer.toHexString(i);
    }
}
