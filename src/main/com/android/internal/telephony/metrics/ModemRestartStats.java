package com.android.internal.telephony.metrics;

import android.os.Build;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class ModemRestartStats {
    private static final String TAG = "ModemRestartStats";

    private static String nullToEmpty(String str) {
        return str != null ? str : PhoneConfigurationManager.SSSS;
    }

    private ModemRestartStats() {
    }

    public static void onModemRestart(String str) {
        String truncateString = truncateString(str, 100);
        String truncateString2 = truncateString(Build.getRadioVersion(), 100);
        int carrierId = getCarrierId();
        String str2 = TAG;
        Rlog.d(str2, "Modem restart (carrier=" + carrierId + "): " + truncateString);
        TelephonyStatsLog.write(312, truncateString2, truncateString, carrierId);
    }

    private static String truncateString(String str, int i) {
        String nullToEmpty = nullToEmpty(str);
        return nullToEmpty.length() > i ? nullToEmpty.substring(0, i) : nullToEmpty;
    }

    private static int getCarrierId() {
        int i = -1;
        try {
            int i2 = -1;
            for (Phone phone : PhoneFactory.getPhones()) {
                try {
                    i2 = phone.getCarrierId();
                    if (i2 != -1) {
                        return i2;
                    }
                } catch (IllegalStateException unused) {
                    i = i2;
                    return i;
                }
            }
            return i2;
        } catch (IllegalStateException unused2) {
        }
    }
}
