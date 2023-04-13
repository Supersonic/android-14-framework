package android.hardware.radio.V1_5;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class Domain {

    /* renamed from: CS */
    public static final int f184CS = 1;

    /* renamed from: PS */
    public static final int f185PS = 2;

    public static final String toString(int o) {
        if (o == 1) {
            return "CS";
        }
        if (o == 2) {
            return "PS";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add("CS");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("PS");
            flipped |= 2;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
