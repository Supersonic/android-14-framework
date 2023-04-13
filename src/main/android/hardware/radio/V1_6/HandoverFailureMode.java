package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class HandoverFailureMode {
    public static final byte DO_FALLBACK = 1;
    public static final byte LEGACY = 0;
    public static final byte NO_FALLBACK_RETRY_HANDOVER = 2;
    public static final byte NO_FALLBACK_RETRY_SETUP_NORMAL = 3;

    public static final String toString(byte o) {
        if (o == 0) {
            return "LEGACY";
        }
        if (o == 1) {
            return "DO_FALLBACK";
        }
        if (o == 2) {
            return "NO_FALLBACK_RETRY_HANDOVER";
        }
        if (o == 3) {
            return "NO_FALLBACK_RETRY_SETUP_NORMAL";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("LEGACY");
        if ((o & 1) == 1) {
            list.add("DO_FALLBACK");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("NO_FALLBACK_RETRY_HANDOVER");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("NO_FALLBACK_RETRY_SETUP_NORMAL");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
