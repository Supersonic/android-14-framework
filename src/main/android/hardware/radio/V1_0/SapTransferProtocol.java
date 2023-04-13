package android.hardware.radio.V1_0;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class SapTransferProtocol {

    /* renamed from: T0 */
    public static final int f169T0 = 0;

    /* renamed from: T1 */
    public static final int f170T1 = 1;

    public static final String toString(int o) {
        if (o == 0) {
            return "T0";
        }
        if (o == 1) {
            return "T1";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("T0");
        if ((o & 1) == 1) {
            list.add("T1");
            flipped = 0 | 1;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
