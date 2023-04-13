package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class PbReceivedStatus {
    public static final byte PB_RECEIVED_ABORT = 3;
    public static final byte PB_RECEIVED_ERROR = 2;
    public static final byte PB_RECEIVED_FINAL = 4;
    public static final byte PB_RECEIVED_OK = 1;

    public static final String toString(byte o) {
        if (o == 1) {
            return "PB_RECEIVED_OK";
        }
        if (o == 2) {
            return "PB_RECEIVED_ERROR";
        }
        if (o == 3) {
            return "PB_RECEIVED_ABORT";
        }
        if (o == 4) {
            return "PB_RECEIVED_FINAL";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & 1) == 1) {
            list.add("PB_RECEIVED_OK");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("PB_RECEIVED_ERROR");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("PB_RECEIVED_ABORT");
            flipped = (byte) (flipped | 3);
        }
        if ((o & 4) == 4) {
            list.add("PB_RECEIVED_FINAL");
            flipped = (byte) (flipped | 4);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
