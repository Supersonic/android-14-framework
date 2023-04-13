package com.android.server.hdmi;

import android.os.Binder;
/* loaded from: classes.dex */
public class WorkSourceUidPreservingRunnable implements Runnable {
    public Runnable mRunnable;
    public int mUid = Binder.getCallingWorkSourceUid();

    public WorkSourceUidPreservingRunnable(Runnable runnable) {
        this.mRunnable = runnable;
    }

    @Override // java.lang.Runnable
    public void run() {
        long callingWorkSourceUid = Binder.setCallingWorkSourceUid(this.mUid);
        try {
            this.mRunnable.run();
        } finally {
            Binder.restoreCallingWorkSource(callingWorkSourceUid);
        }
    }
}
