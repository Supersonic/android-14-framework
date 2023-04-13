package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class SliceStatus {
    public static final byte ALLOWED = 2;
    public static final byte CONFIGURED = 1;
    public static final byte DEFAULT_CONFIGURED = 5;
    public static final byte REJECTED_NOT_AVAILABLE_IN_PLMN = 3;
    public static final byte REJECTED_NOT_AVAILABLE_IN_REG_AREA = 4;
    public static final byte UNKNOWN = 0;

    public static final String toString(byte o) {
        if (o == 0) {
            return "UNKNOWN";
        }
        if (o == 1) {
            return "CONFIGURED";
        }
        if (o == 2) {
            return "ALLOWED";
        }
        if (o == 3) {
            return "REJECTED_NOT_AVAILABLE_IN_PLMN";
        }
        if (o == 4) {
            return "REJECTED_NOT_AVAILABLE_IN_REG_AREA";
        }
        if (o == 5) {
            return "DEFAULT_CONFIGURED";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("UNKNOWN");
        if ((o & 1) == 1) {
            list.add("CONFIGURED");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("ALLOWED");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("REJECTED_NOT_AVAILABLE_IN_PLMN");
            flipped = (byte) (flipped | 3);
        }
        if ((o & 4) == 4) {
            list.add("REJECTED_NOT_AVAILABLE_IN_REG_AREA");
            flipped = (byte) (flipped | 4);
        }
        if ((o & 5) == 5) {
            list.add("DEFAULT_CONFIGURED");
            flipped = (byte) (flipped | 5);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
