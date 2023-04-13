package com.android.server.devicepolicy;

import android.app.admin.SecurityLog;
/* loaded from: classes.dex */
public class CryptoTestHelper {
    private static native int runSelfTest();

    public static void runAndLogSelfTest() {
        SecurityLog.writeEvent(210031, new Object[]{Integer.valueOf(runSelfTest())});
    }
}
