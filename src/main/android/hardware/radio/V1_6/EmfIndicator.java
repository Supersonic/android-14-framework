package android.hardware.radio.V1_6;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class EmfIndicator {
    public static final byte EMF_BOTH_NR_EUTRA_CONNECTED_TO_5GCN = 3;
    public static final byte EMF_EUTRA_CONNECTED_TO_5GCN = 2;
    public static final byte EMF_NOT_SUPPORTED = 0;
    public static final byte EMF_NR_CONNECTED_TO_5GCN = 1;

    public static final String toString(byte o) {
        if (o == 0) {
            return "EMF_NOT_SUPPORTED";
        }
        if (o == 1) {
            return "EMF_NR_CONNECTED_TO_5GCN";
        }
        if (o == 2) {
            return "EMF_EUTRA_CONNECTED_TO_5GCN";
        }
        if (o == 3) {
            return "EMF_BOTH_NR_EUTRA_CONNECTED_TO_5GCN";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        list.add("EMF_NOT_SUPPORTED");
        if ((o & 1) == 1) {
            list.add("EMF_NR_CONNECTED_TO_5GCN");
            flipped = (byte) (0 | 1);
        }
        if ((o & 2) == 2) {
            list.add("EMF_EUTRA_CONNECTED_TO_5GCN");
            flipped = (byte) (flipped | 2);
        }
        if ((o & 3) == 3) {
            list.add("EMF_BOTH_NR_EUTRA_CONNECTED_TO_5GCN");
            flipped = (byte) (flipped | 3);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
