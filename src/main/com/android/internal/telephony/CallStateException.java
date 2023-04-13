package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class CallStateException extends Exception {
    public static final int ERROR_ALREADY_DIALING = 3;
    public static final int ERROR_CALLING_DISABLED = 5;
    public static final int ERROR_CALL_RINGING = 4;
    public static final int ERROR_FDN_BLOCKED = 8;
    public static final int ERROR_INVALID = -1;
    public static final int ERROR_OTASP_PROVISIONING_IN_PROCESS = 7;
    public static final int ERROR_OUT_OF_SERVICE = 1;
    public static final int ERROR_POWER_OFF = 2;
    public static final int ERROR_TOO_MANY_CALLS = 6;
    private int mError;

    public CallStateException() {
        this.mError = -1;
    }

    @UnsupportedAppUsage
    public CallStateException(String str) {
        super(str);
        this.mError = -1;
    }

    public CallStateException(int i, String str) {
        super(str);
        this.mError = i;
    }

    public int getError() {
        return this.mError;
    }
}
