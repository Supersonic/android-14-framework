package android.hardware.radio.V1_0;

import android.service.timezone.TimeZoneProviderService;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class CdmaSmsErrorClass {
    public static final int ERROR = 1;
    public static final int NO_ERROR = 0;

    public static final String toString(int o) {
        if (o == 0) {
            return "NO_ERROR";
        }
        if (o == 1) {
            return TimeZoneProviderService.TEST_COMMAND_RESULT_ERROR_KEY;
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("NO_ERROR");
        if ((o & 1) == 1) {
            list.add(TimeZoneProviderService.TEST_COMMAND_RESULT_ERROR_KEY);
            flipped = 0 | 1;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
