package android.hardware.contexthub.V1_1;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class Setting {
    public static final byte LOCATION = 0;

    public static final String toString(byte o) {
        if (o == 0) {
            return "LOCATION";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        list.add("LOCATION");
        if (o != 0) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~0) & o))));
        }
        return String.join(" | ", list);
    }
}
