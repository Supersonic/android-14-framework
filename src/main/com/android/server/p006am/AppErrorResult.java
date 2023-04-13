package com.android.server.p006am;
/* renamed from: com.android.server.am.AppErrorResult */
/* loaded from: classes.dex */
public final class AppErrorResult {
    public boolean mHasResult = false;
    public int mResult;

    public void set(int i) {
        synchronized (this) {
            this.mHasResult = true;
            this.mResult = i;
            notifyAll();
        }
    }

    public int get() {
        synchronized (this) {
            while (!this.mHasResult) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return this.mResult;
    }
}
