package com.android.server.broadcastradio.aidl;
/* loaded from: classes.dex */
public final class Utils {

    /* loaded from: classes.dex */
    public enum FrequencyBand {
        UNKNOWN,
        FM,
        AM_LW,
        AM_MW,
        AM_SW
    }

    public static FrequencyBand getBand(int i) {
        if (i < 30) {
            return FrequencyBand.UNKNOWN;
        }
        if (i < 500) {
            return FrequencyBand.AM_LW;
        }
        if (i < 1705) {
            return FrequencyBand.AM_MW;
        }
        if (i < 30000) {
            return FrequencyBand.AM_SW;
        }
        if (i < 60000) {
            return FrequencyBand.UNKNOWN;
        }
        if (i < 110000) {
            return FrequencyBand.FM;
        }
        return FrequencyBand.UNKNOWN;
    }
}
