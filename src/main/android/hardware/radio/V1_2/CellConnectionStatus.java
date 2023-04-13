package android.hardware.radio.V1_2;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class CellConnectionStatus {
    public static final int NONE = 0;
    public static final int PRIMARY_SERVING = 1;
    public static final int SECONDARY_SERVING = 2;

    public static final String toString(int o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 1) {
            return "PRIMARY_SERVING";
        }
        if (o == 2) {
            return "SECONDARY_SERVING";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 1) == 1) {
            list.add("PRIMARY_SERVING");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("SECONDARY_SERVING");
            flipped |= 2;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
