package com.android.internal.telephony.satellite;

import android.content.Context;
import android.telephony.Rlog;
/* loaded from: classes.dex */
public class SatelliteSessionController {
    private static SatelliteSessionController sInstance;
    private final Context mContext;

    public static SatelliteSessionController getInstance() {
        if (sInstance == null) {
            loge("SatelliteSessionController was not yet initialized.");
        }
        return sInstance;
    }

    public static SatelliteSessionController make(Context context) {
        if (sInstance == null) {
            sInstance = new SatelliteSessionController(context);
        }
        return sInstance;
    }

    private SatelliteSessionController(Context context) {
        this.mContext = context;
    }

    private static void loge(String str) {
        Rlog.e("SatelliteSessionController", str);
    }
}
