package android.hardware.contexthub.V1_2;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class Setting {
    public static final byte AIRPLANE_MODE = 2;
    public static final byte LOCATION = 0;
    public static final byte MICROPHONE = 3;
    public static final byte WIFI_AVAILABLE = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "LOCATION";
        }
        if (o == 1) {
            return "WIFI_AVAILABLE";
        }
        if (o == 2) {
            return "AIRPLANE_MODE";
        }
        if (o == 3) {
            return "MICROPHONE";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("LOCATION");
        if ((o & 1) == 1) {
            list.add("WIFI_AVAILABLE");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("AIRPLANE_MODE");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("MICROPHONE");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
