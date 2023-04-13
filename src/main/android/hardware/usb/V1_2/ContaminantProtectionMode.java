package android.hardware.usb.V1_2;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class ContaminantProtectionMode {
    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("NONE");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("FORCE_SINK");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("FORCE_SOURCE");
            i2 |= 2;
        }
        if ((i & 4) == 4) {
            arrayList.add("FORCE_DISABLE");
            i2 |= 4;
        }
        if (i != i2) {
            arrayList.add("0x" + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
