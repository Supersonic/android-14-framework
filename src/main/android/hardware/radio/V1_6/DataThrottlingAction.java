package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class DataThrottlingAction {
    public static final byte HOLD = 3;
    public static final byte NO_DATA_THROTTLING = 0;
    public static final byte THROTTLE_ANCHOR_CARRIER = 2;
    public static final byte THROTTLE_SECONDARY_CARRIER = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "NO_DATA_THROTTLING";
        }
        if (o == 1) {
            return "THROTTLE_SECONDARY_CARRIER";
        }
        if (o == 2) {
            return "THROTTLE_ANCHOR_CARRIER";
        }
        if (o == 3) {
            return "HOLD";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("NO_DATA_THROTTLING");
        if ((o & 1) == 1) {
            list.add("THROTTLE_SECONDARY_CARRIER");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("THROTTLE_ANCHOR_CARRIER");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("HOLD");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
