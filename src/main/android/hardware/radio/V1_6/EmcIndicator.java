package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class EmcIndicator {
    public static final byte EMC_BOTH_NR_EUTRA_CONNECTED_TO_5GCN = 3;
    public static final byte EMC_EUTRA_CONNECTED_TO_5GCN = 2;
    public static final byte EMC_NOT_SUPPORTED = 0;
    public static final byte EMC_NR_CONNECTED_TO_5GCN = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "EMC_NOT_SUPPORTED";
        }
        if (o == 1) {
            return "EMC_NR_CONNECTED_TO_5GCN";
        }
        if (o == 2) {
            return "EMC_EUTRA_CONNECTED_TO_5GCN";
        }
        if (o == 3) {
            return "EMC_BOTH_NR_EUTRA_CONNECTED_TO_5GCN";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("EMC_NOT_SUPPORTED");
        if ((o & 1) == 1) {
            list.add("EMC_NR_CONNECTED_TO_5GCN");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("EMC_EUTRA_CONNECTED_TO_5GCN");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("EMC_BOTH_NR_EUTRA_CONNECTED_TO_5GCN");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
