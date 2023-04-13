package com.android.server.location.gnss;

import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class TimeDetectorNetworkTimeHelper extends NetworkTimeHelper {
    public static final boolean DEBUG = Log.isLoggable("TDNetworkTimeHelper", 3);
    @VisibleForTesting
    static final int MAX_NETWORK_TIME_AGE_MILLIS = 86400000;

    public static boolean isInUse() {
        return false;
    }
}
