package android.hardware.gnss.measurement_corrections.V1_0;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class GnssSingleSatCorrectionFlags {
    public static final short HAS_EXCESS_PATH_LENGTH = 2;
    public static final short HAS_EXCESS_PATH_LENGTH_UNC = 4;
    public static final short HAS_REFLECTING_PLANE = 8;
    public static final short HAS_SAT_IS_LOS_PROBABILITY = 1;

    public static final String toString(short o) {
        if (o == 1) {
            return "HAS_SAT_IS_LOS_PROBABILITY";
        }
        if (o == 2) {
            return "HAS_EXCESS_PATH_LENGTH";
        }
        if (o == 4) {
            return "HAS_EXCESS_PATH_LENGTH_UNC";
        }
        if (o == 8) {
            return "HAS_REFLECTING_PLANE";
        }
        return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
    }

    public static final String dumpBitfield(short o) {
        ArrayList<String> list = new ArrayList<>();
        short flipped = 0;
        if ((o & 1) == 1) {
            list.add("HAS_SAT_IS_LOS_PROBABILITY");
            flipped = (short) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("HAS_EXCESS_PATH_LENGTH");
            flipped = (short) (flipped | 2);
        }
        if ((o & 4) == 4) {
            list.add("HAS_EXCESS_PATH_LENGTH_UNC");
            flipped = (short) (flipped | 4);
        }
        if ((o & 8) == 8) {
            list.add("HAS_REFLECTING_PLANE");
            flipped = (short) (flipped | 8);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
