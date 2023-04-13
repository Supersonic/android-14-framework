package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class NrDualConnectivityState {
    public static final byte DISABLE = 2;
    public static final byte DISABLE_IMMEDIATE = 3;
    public static final byte ENABLE = 1;

    public static final String toString(byte o) {
        if (o == 1) {
            return "ENABLE";
        }
        if (o == 2) {
            return "DISABLE";
        }
        if (o == 3) {
            return "DISABLE_IMMEDIATE";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & 1) == 1) {
            list.add("ENABLE");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("DISABLE");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("DISABLE_IMMEDIATE");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
