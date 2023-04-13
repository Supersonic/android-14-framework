package android.hardware.usb.gadget.V1_2;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class UsbSpeed {
    public static final int FULLSPEED = 1;
    public static final int HIGHSPEED = 2;
    public static final int LOWSPEED = 0;
    public static final int RESERVED_SPEED = 64;
    public static final int SUPERSPEED = 3;
    public static final int SUPERSPEED_10Gb = 4;
    public static final int SUPERSPEED_20Gb = 5;
    public static final int UNKNOWN = -1;
    public static final int USB4_GEN2_10Gb = 6;
    public static final int USB4_GEN2_20Gb = 7;
    public static final int USB4_GEN3_20Gb = 8;
    public static final int USB4_GEN3_40Gb = 9;

    public static final String toString(int o) {
        if (o == -1) {
            return "UNKNOWN";
        }
        if (o == 0) {
            return "LOWSPEED";
        }
        if (o == 1) {
            return "FULLSPEED";
        }
        if (o == 2) {
            return "HIGHSPEED";
        }
        if (o == 3) {
            return "SUPERSPEED";
        }
        if (o == 4) {
            return "SUPERSPEED_10Gb";
        }
        if (o == 5) {
            return "SUPERSPEED_20Gb";
        }
        if (o == 6) {
            return "USB4_GEN2_10Gb";
        }
        if (o == 7) {
            return "USB4_GEN2_20Gb";
        }
        if (o == 8) {
            return "USB4_GEN3_20Gb";
        }
        if (o == 9) {
            return "USB4_GEN3_40Gb";
        }
        if (o == 64) {
            return "RESERVED_SPEED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & (-1)) == -1) {
            list.add("UNKNOWN");
            flipped = 0 | (-1);
        }
        list.add("LOWSPEED");
        if ((o & 1) == 1) {
            list.add("FULLSPEED");
            flipped |= 1;
        }
        if ((o & 2) == 2) {
            list.add("HIGHSPEED");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("SUPERSPEED");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("SUPERSPEED_10Gb");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("SUPERSPEED_20Gb");
            flipped |= 5;
        }
        if ((o & 6) == 6) {
            list.add("USB4_GEN2_10Gb");
            flipped |= 6;
        }
        if ((o & 7) == 7) {
            list.add("USB4_GEN2_20Gb");
            flipped |= 7;
        }
        if ((o & 8) == 8) {
            list.add("USB4_GEN3_20Gb");
            flipped |= 8;
        }
        if ((o & 9) == 9) {
            list.add("USB4_GEN3_40Gb");
            flipped |= 9;
        }
        if ((o & 64) == 64) {
            list.add("RESERVED_SPEED");
            flipped |= 64;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
