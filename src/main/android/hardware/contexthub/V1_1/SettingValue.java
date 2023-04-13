package android.hardware.contexthub.V1_1;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class SettingValue {
    public static final byte DISABLED = 0;
    public static final byte ENABLED = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "DISABLED";
        }
        if (o == 1) {
            return "ENABLED";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("DISABLED");
        if ((o & 1) == 1) {
            list.add("ENABLED");
            flipped = (byte) (0 | 1);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
