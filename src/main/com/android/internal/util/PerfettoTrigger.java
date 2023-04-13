package com.android.internal.util;

import android.p008os.SystemClock;
import android.util.Log;
import android.util.SparseLongArray;
import java.io.IOException;
/* loaded from: classes3.dex */
public class PerfettoTrigger {
    private static final String TAG = "PerfettoTrigger";
    private static final long THROTTLE_MILLIS = 300000;
    private static final String TRIGGER_COMMAND = "/system/bin/trigger_perfetto";
    private static final SparseLongArray sLastInvocationPerTrigger = new SparseLongArray(100);
    private static final Object sLock = new Object();

    public static void trigger(String triggerName) {
        synchronized (sLock) {
            SparseLongArray sparseLongArray = sLastInvocationPerTrigger;
            long lastTrigger = sparseLongArray.get(triggerName.hashCode());
            long sinceLastTrigger = SystemClock.elapsedRealtime() - lastTrigger;
            if (sinceLastTrigger < 300000) {
                Log.m106v(TAG, "Not triggering " + triggerName + " - not enough time since last trigger");
                return;
            }
            sparseLongArray.put(triggerName.hashCode(), SystemClock.elapsedRealtime());
            try {
                ProcessBuilder pb = new ProcessBuilder(TRIGGER_COMMAND, triggerName);
                Log.m106v(TAG, "Triggering " + String.join(" ", pb.command()));
                pb.start();
            } catch (IOException e) {
                Log.m103w(TAG, "Failed to trigger " + triggerName, e);
            }
        }
    }
}
