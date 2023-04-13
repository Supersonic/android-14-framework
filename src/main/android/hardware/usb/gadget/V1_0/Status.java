package android.hardware.usb.gadget.V1_0;

import android.service.timezone.TimeZoneProviderService;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class Status {
    public static final int CONFIGURATION_NOT_SUPPORTED = 4;
    public static final int ERROR = 1;
    public static final int FUNCTIONS_APPLIED = 2;
    public static final int FUNCTIONS_NOT_APPLIED = 3;
    public static final int SUCCESS = 0;

    public static final String toString(int o) {
        if (o == 0) {
            return TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY;
        }
        if (o == 1) {
            return TimeZoneProviderService.TEST_COMMAND_RESULT_ERROR_KEY;
        }
        if (o == 2) {
            return "FUNCTIONS_APPLIED";
        }
        if (o == 3) {
            return "FUNCTIONS_NOT_APPLIED";
        }
        if (o == 4) {
            return "CONFIGURATION_NOT_SUPPORTED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY);
        if ((o & 1) == 1) {
            list.add(TimeZoneProviderService.TEST_COMMAND_RESULT_ERROR_KEY);
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("FUNCTIONS_APPLIED");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("FUNCTIONS_NOT_APPLIED");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("CONFIGURATION_NOT_SUPPORTED");
            flipped |= 4;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
