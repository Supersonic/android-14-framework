package android.hardware.radio.V1_5;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class PrlIndicator {
    public static final int IN_PRL = 1;
    public static final int NOT_IN_PRL = 0;
    public static final int NOT_REGISTERED = -1;

    public static final String toString(int o) {
        if (o == -1) {
            return "NOT_REGISTERED";
        }
        if (o == 0) {
            return "NOT_IN_PRL";
        }
        if (o == 1) {
            return "IN_PRL";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & (-1)) == -1) {
            list.add("NOT_REGISTERED");
            flipped = 0 | (-1);
        }
        list.add("NOT_IN_PRL");
        if ((o & 1) == 1) {
            list.add("IN_PRL");
            flipped |= 1;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
