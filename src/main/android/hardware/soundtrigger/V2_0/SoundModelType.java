package android.hardware.soundtrigger.V2_0;
/* loaded from: classes.dex */
public final class SoundModelType {
    public static final String toString(int i) {
        if (i == -1) {
            return "UNKNOWN";
        }
        if (i == 0) {
            return "KEYPHRASE";
        }
        if (i == 1) {
            return "GENERIC";
        }
        return "0x" + Integer.toHexString(i);
    }
}
