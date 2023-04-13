package android.hardware.radio.V1_6;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class SliceServiceType {
    public static final byte EMBB = 1;
    public static final byte MIOT = 3;
    public static final byte NONE = 0;
    public static final byte URLLC = 2;

    public static final String toString(byte o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 1) {
            return "EMBB";
        }
        if (o == 2) {
            return "URLLC";
        }
        if (o == 3) {
            return "MIOT";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 1) == 1) {
            list.add("EMBB");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("URLLC");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("MIOT");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
