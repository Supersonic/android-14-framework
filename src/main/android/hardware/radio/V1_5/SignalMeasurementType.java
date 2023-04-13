package android.hardware.radio.V1_5;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class SignalMeasurementType {
    public static final int RSCP = 2;
    public static final int RSRP = 3;
    public static final int RSRQ = 4;
    public static final int RSSI = 1;
    public static final int RSSNR = 5;
    public static final int SSRSRP = 6;
    public static final int SSRSRQ = 7;
    public static final int SSSINR = 8;

    public static final String toString(int o) {
        if (o == 1) {
            return "RSSI";
        }
        if (o == 2) {
            return "RSCP";
        }
        if (o == 3) {
            return "RSRP";
        }
        if (o == 4) {
            return "RSRQ";
        }
        if (o == 5) {
            return "RSSNR";
        }
        if (o == 6) {
            return "SSRSRP";
        }
        if (o == 7) {
            return "SSRSRQ";
        }
        if (o == 8) {
            return "SSSINR";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add("RSSI");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("RSCP");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("RSRP");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("RSRQ");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("RSSNR");
            flipped |= 5;
        }
        if ((o & 6) == 6) {
            list.add("SSRSRP");
            flipped |= 6;
        }
        if ((o & 7) == 7) {
            list.add("SSRSRQ");
            flipped |= 7;
        }
        if ((o & 8) == 8) {
            list.add("SSSINR");
            flipped |= 8;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
