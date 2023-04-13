package android.hardware.radio.V1_5;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class AddressProperty {
    public static final int DEPRECATED = 32;
    public static final int NONE = 0;

    public static final String toString(int o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 32) {
            return "DEPRECATED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 32) == 32) {
            list.add("DEPRECATED");
            flipped = 0 | 32;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
