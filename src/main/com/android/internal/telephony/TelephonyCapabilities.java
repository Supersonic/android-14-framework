package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class TelephonyCapabilities {
    public static boolean canDistinguishDialingAndConnected(int i) {
        return i == 1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static boolean supportsAdn(int i) {
        return i == 1;
    }

    private TelephonyCapabilities() {
    }

    public static boolean supportsEcm(Phone phone) {
        Rlog.d("TelephonyCapabilities", "supportsEcm: Phone type = " + phone.getPhoneType() + " Ims Phone = " + phone.getImsPhone());
        return phone.getPhoneType() == 2 || phone.getImsPhone() != null;
    }

    public static boolean supportsOtasp(Phone phone) {
        return phone.getPhoneType() == 2;
    }

    public static boolean supportsVoiceMessageCount(Phone phone) {
        return phone.getVoiceMessageCount() != -1;
    }

    public static boolean supportsNetworkSelection(Phone phone) {
        return phone.getPhoneType() == 1;
    }

    public static int getDeviceIdLabel(Phone phone) {
        if (phone.getPhoneType() == 1) {
            return 17040461;
        }
        if (phone.getPhoneType() == 2) {
            return 17040760;
        }
        Rlog.w("TelephonyCapabilities", "getDeviceIdLabel: no known label for phone " + phone.getPhoneName());
        return 0;
    }

    public static boolean supportsConferenceCallManagement(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3;
    }

    public static boolean supportsHoldAndUnhold(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3 || phone.getPhoneType() == 5;
    }

    public static boolean supportsAnswerAndHold(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3;
    }
}
