package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class QosFilterDirection {
    public static final byte BIDIRECTIONAL = 2;
    public static final byte DOWNLINK = 0;
    public static final byte UPLINK = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "DOWNLINK";
        }
        if (o == 1) {
            return "UPLINK";
        }
        if (o == 2) {
            return "BIDIRECTIONAL";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("DOWNLINK");
        if ((o & 1) == 1) {
            list.add("UPLINK");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("BIDIRECTIONAL");
            flipped = (byte) (flipped | 2);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
