package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class VopsIndicator {
    public static final byte VOPS_NOT_SUPPORTED = 0;
    public static final byte VOPS_OVER_3GPP = 1;
    public static final byte VOPS_OVER_NON_3GPP = 2;

    public static final String toString(byte o) {
        if (o == 0) {
            return "VOPS_NOT_SUPPORTED";
        }
        if (o == 1) {
            return "VOPS_OVER_3GPP";
        }
        if (o == 2) {
            return "VOPS_OVER_NON_3GPP";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("VOPS_NOT_SUPPORTED");
        if ((o & 1) == 1) {
            list.add("VOPS_OVER_3GPP");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("VOPS_OVER_NON_3GPP");
            flipped = (byte) (flipped | 2);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
