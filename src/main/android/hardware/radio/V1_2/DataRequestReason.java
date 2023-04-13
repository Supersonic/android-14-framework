package android.hardware.radio.V1_2;

import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class DataRequestReason {
    public static final int HANDOVER = 3;
    public static final int NORMAL = 1;
    public static final int SHUTDOWN = 2;

    public static final String toString(int o) {
        if (o == 1) {
            return SQLiteDatabase.SYNC_MODE_NORMAL;
        }
        if (o == 2) {
            return "SHUTDOWN";
        }
        if (o == 3) {
            return "HANDOVER";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add(SQLiteDatabase.SYNC_MODE_NORMAL);
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("SHUTDOWN");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("HANDOVER");
            flipped |= 3;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
