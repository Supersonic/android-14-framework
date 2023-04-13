package com.android.server.p006am;

import com.android.internal.annotations.GuardedBy;
/* renamed from: com.android.server.am.LowMemDetector */
/* loaded from: classes.dex */
public final class LowMemDetector {
    public final ActivityManagerService mAm;
    public boolean mAvailable;
    public final LowMemThread mLowMemThread;
    public final Object mPressureStateLock = new Object();
    @GuardedBy({"mPressureStateLock"})
    public int mPressureState = 0;

    private native int init();

    /* JADX INFO: Access modifiers changed from: private */
    public native int waitForPressure();

    public LowMemDetector(ActivityManagerService activityManagerService) {
        this.mAm = activityManagerService;
        LowMemThread lowMemThread = new LowMemThread();
        this.mLowMemThread = lowMemThread;
        if (init() != 0) {
            this.mAvailable = false;
            return;
        }
        this.mAvailable = true;
        lowMemThread.start();
    }

    public boolean isAvailable() {
        return this.mAvailable;
    }

    public int getMemFactor() {
        int i;
        synchronized (this.mPressureStateLock) {
            i = this.mPressureState;
        }
        return i;
    }

    /* renamed from: com.android.server.am.LowMemDetector$LowMemThread */
    /* loaded from: classes.dex */
    public final class LowMemThread extends Thread {
        public LowMemThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                int waitForPressure = LowMemDetector.this.waitForPressure();
                if (waitForPressure == -1) {
                    LowMemDetector.this.mAvailable = false;
                    return;
                }
                synchronized (LowMemDetector.this.mPressureStateLock) {
                    LowMemDetector.this.mPressureState = waitForPressure;
                }
            }
        }
    }
}
