package android.hardware.radio.V1_0;

import android.security.keystore.KeyProperties;
import android.service.timezone.TimeZoneProviderService;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class RadioCapabilityStatus {
    public static final int FAIL = 2;
    public static final int NONE = 0;
    public static final int SUCCESS = 1;

    public static final String toString(int o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 1) {
            return TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY;
        }
        if (o == 2) {
            return "FAIL";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 1) == 1) {
            list.add(TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY);
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("FAIL");
            flipped |= 2;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
