package android.hardware.usb.V1_1;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class PortMode_1_1 {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == 1) {
            return "UFP";
        }
        if (i == 2) {
            return "DFP";
        }
        if (i == 3) {
            return "DRP";
        }
        if (i == 4) {
            return "NUM_MODES";
        }
        if (i == 4) {
            return "AUDIO_ACCESSORY";
        }
        if (i == 8) {
            return "DEBUG_ACCESSORY";
        }
        if (i == 16) {
            return "NUM_MODES_1_1";
        }
        return "0x" + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("NONE");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("UFP");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("DFP");
            i2 |= 2;
        }
        if ((i & 3) == 3) {
            arrayList.add("DRP");
            i2 |= 3;
        }
        int i3 = i & 4;
        if (i3 == 4) {
            arrayList.add("NUM_MODES");
            i2 |= 4;
        }
        if (i3 == 4) {
            arrayList.add("AUDIO_ACCESSORY");
            i2 |= 4;
        }
        if ((i & 8) == 8) {
            arrayList.add("DEBUG_ACCESSORY");
            i2 |= 8;
        }
        if ((i & 16) == 16) {
            arrayList.add("NUM_MODES_1_1");
            i2 |= 16;
        }
        if (i != i2) {
            arrayList.add("0x" + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
