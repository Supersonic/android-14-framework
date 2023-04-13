package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class SscMode {
    public static final byte MODE_1 = 1;
    public static final byte MODE_2 = 2;
    public static final byte MODE_3 = 3;

    public static final String toString(byte o) {
        if (o == 1) {
            return "MODE_1";
        }
        if (o == 2) {
            return "MODE_2";
        }
        if (o == 3) {
            return "MODE_3";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & 1) == 1) {
            list.add("MODE_1");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("MODE_2");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("MODE_3");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
