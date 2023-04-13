package android.hardware.soundtrigger.V2_3;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class AudioCapabilities {
    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("ECHO_CANCELLATION");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("NOISE_SUPPRESSION");
            i2 |= 2;
        }
        if (i != i2) {
            arrayList.add("0x" + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
