package com.android.internal.telephony.emergency;
/* loaded from: classes.dex */
public class EmergencyConstants {
    public static final int MODE_EMERGENCY_CALLBACK = 3;
    public static final int MODE_EMERGENCY_NONE = 0;
    public static final int MODE_EMERGENCY_WLAN = 2;
    public static final int MODE_EMERGENCY_WWAN = 1;

    public static String emergencyModeToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "UNKNOWN(" + i + ")";
                    }
                    return "CALLBACK";
                }
                return "WLAN";
            }
            return "WWAN";
        }
        return "NONE";
    }
}
