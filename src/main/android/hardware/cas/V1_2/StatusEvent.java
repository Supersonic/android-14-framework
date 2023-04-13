package android.hardware.cas.V1_2;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class StatusEvent {
    public static final byte PLUGIN_PHYSICAL_MODULE_CHANGED = 0;
    public static final byte PLUGIN_SESSION_NUMBER_CHANGED = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "PLUGIN_PHYSICAL_MODULE_CHANGED";
        }
        if (o == 1) {
            return "PLUGIN_SESSION_NUMBER_CHANGED";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("PLUGIN_PHYSICAL_MODULE_CHANGED");
        if ((o & 1) == 1) {
            list.add("PLUGIN_SESSION_NUMBER_CHANGED");
            flipped = (byte) (0 | 1);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
