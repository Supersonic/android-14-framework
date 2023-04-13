package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class QosFlowIdRange {
    public static final byte MAX = 63;
    public static final byte MIN = 1;

    public static final String toString(byte o) {
        if (o == 1) {
            return "MIN";
        }
        if (o == 63) {
            return "MAX";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & 1) == 1) {
            list.add("MIN");
            flipped = (byte) (0 | 1);
        }
        if ((o & 63) == 63) {
            list.add("MAX");
            flipped = (byte) (flipped | 63);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
