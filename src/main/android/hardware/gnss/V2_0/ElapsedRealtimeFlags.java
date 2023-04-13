package android.hardware.gnss.V2_0;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class ElapsedRealtimeFlags {
    public static final short HAS_TIMESTAMP_NS = 1;
    public static final short HAS_TIME_UNCERTAINTY_NS = 2;

    public static final String toString(short o) {
        if (o == 1) {
            return "HAS_TIMESTAMP_NS";
        }
        if (o == 2) {
            return "HAS_TIME_UNCERTAINTY_NS";
        }
        return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
    }

    public static final String dumpBitfield(short o) {
        ArrayList<String> list = new ArrayList<>();
        short flipped = 0;
        if ((o & 1) == 1) {
            list.add("HAS_TIMESTAMP_NS");
            flipped = (short) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("HAS_TIME_UNCERTAINTY_NS");
            flipped = (short) (flipped | 2);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
