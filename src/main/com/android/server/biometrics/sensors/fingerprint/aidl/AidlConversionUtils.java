package com.android.server.biometrics.sensors.fingerprint.aidl;
/* loaded from: classes.dex */
public final class AidlConversionUtils {
    public static int toFrameworkAcquiredInfo(byte b) {
        if (b == 0) {
            return 8;
        }
        if (b == 1) {
            return 0;
        }
        if (b == 2) {
            return 1;
        }
        if (b == 3) {
            return 2;
        }
        if (b == 4) {
            return 3;
        }
        if (b == 5) {
            return 4;
        }
        if (b == 6) {
            return 5;
        }
        if (b == 7) {
            return 6;
        }
        if (b == 8) {
            return 7;
        }
        if (b == 9) {
            return 8;
        }
        if (b == 10) {
            return 10;
        }
        if (b == 11) {
            return 9;
        }
        return (b != 12 && b == 14) ? 11 : 8;
    }

    public static int toFrameworkError(byte b) {
        if (b == 0) {
            return 17;
        }
        if (b == 1) {
            return 1;
        }
        if (b == 2) {
            return 2;
        }
        if (b == 3) {
            return 3;
        }
        if (b == 4) {
            return 4;
        }
        if (b == 5) {
            return 5;
        }
        if (b == 6) {
            return 6;
        }
        if (b == 7) {
            return 8;
        }
        if (b == 8) {
            return 18;
        }
        return b == 9 ? 19 : 17;
    }
}
