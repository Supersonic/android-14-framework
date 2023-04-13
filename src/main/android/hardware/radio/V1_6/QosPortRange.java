package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class QosPortRange {
    public static final short MAX = -1;
    public static final short MIN = 20;

    public static final String toString(short o) {
        if (o == 20) {
            return "MIN";
        }
        if (o == -1) {
            return "MAX";
        }
        return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
    }

    public static final String dumpBitfield(short o) {
        ArrayList<String> list = new ArrayList<>();
        short flipped = 0;
        if ((o & 20) == 20) {
            list.add("MIN");
            flipped = (short) (0 | 20);
        }
        if ((o & (-1)) == -1) {
            list.add("MAX");
            flipped = (short) (flipped | (-1));
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
