package android.hardware.radio.V1_0;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class IndicationFilter {
    public static final int ALL = 7;
    public static final int DATA_CALL_DORMANCY_CHANGED = 4;
    public static final int FULL_NETWORK_STATE = 2;
    public static final int NONE = 0;
    public static final int SIGNAL_STRENGTH = 1;

    public static final String toString(int o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 1) {
            return "SIGNAL_STRENGTH";
        }
        if (o == 2) {
            return "FULL_NETWORK_STATE";
        }
        if (o == 4) {
            return "DATA_CALL_DORMANCY_CHANGED";
        }
        if (o == 7) {
            return "ALL";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 1) == 1) {
            list.add("SIGNAL_STRENGTH");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("FULL_NETWORK_STATE");
            flipped |= 2;
        }
        if ((o & 4) == 4) {
            list.add("DATA_CALL_DORMANCY_CHANGED");
            flipped |= 4;
        }
        if ((o & 7) == 7) {
            list.add("ALL");
            flipped |= 7;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
