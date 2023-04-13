package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class PublicKeyType {
    public static final byte EPDG = 1;
    public static final byte WLAN = 2;

    public static final String toString(byte o) {
        if (o == 1) {
            return "EPDG";
        }
        if (o == 2) {
            return "WLAN";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & 1) == 1) {
            list.add("EPDG");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("WLAN");
            flipped = (byte) (flipped | 2);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
