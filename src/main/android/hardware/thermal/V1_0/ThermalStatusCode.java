package android.hardware.thermal.V1_0;

import android.service.timezone.TimeZoneProviderService;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class ThermalStatusCode {
    public static final int FAILURE = 1;
    public static final int SUCCESS = 0;

    public static final String toString(int o) {
        if (o == 0) {
            return TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY;
        }
        if (o == 1) {
            return "FAILURE";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY);
        if ((o & 1) == 1) {
            list.add("FAILURE");
            flipped = 0 | 1;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
