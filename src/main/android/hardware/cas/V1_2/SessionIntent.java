package android.hardware.cas.V1_2;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class SessionIntent {
    public static final int LIVE = 0;
    public static final int PLAYBACK = 1;
    public static final int RECORD = 2;
    public static final int TIMESHIFT = 3;

    public static final String toString(int o) {
        if (o == 0) {
            return "LIVE";
        }
        if (o == 1) {
            return "PLAYBACK";
        }
        if (o == 2) {
            return "RECORD";
        }
        if (o == 3) {
            return "TIMESHIFT";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("LIVE");
        if ((o & 1) == 1) {
            list.add("PLAYBACK");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("RECORD");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("TIMESHIFT");
            flipped |= 3;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
