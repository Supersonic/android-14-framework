package android.hardware.radio.V1_1;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class RadioAccessNetworks {
    public static final int EUTRAN = 3;
    public static final int GERAN = 1;
    public static final int UTRAN = 2;

    public static final String toString(int o) {
        if (o == 1) {
            return "GERAN";
        }
        if (o == 2) {
            return "UTRAN";
        }
        if (o == 3) {
            return "EUTRAN";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add("GERAN");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("UTRAN");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("EUTRAN");
            flipped |= 3;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
