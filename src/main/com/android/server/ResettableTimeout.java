package com.android.server;

import android.p008os.ConditionVariable;
import android.p008os.SystemClock;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public abstract class ResettableTimeout {
    private ConditionVariable mLock = new ConditionVariable();
    private volatile long mOffAt;
    private volatile boolean mOffCalled;
    private Thread mThread;

    public abstract void off();

    /* renamed from: on */
    public abstract void m11on(boolean z);

    ResettableTimeout() {
    }

    /* renamed from: go */
    public void m12go(long milliseconds) {
        boolean alreadyOn;
        synchronized (this) {
            this.mOffAt = SystemClock.uptimeMillis() + milliseconds;
            Thread thread = this.mThread;
            if (thread == null) {
                alreadyOn = false;
                this.mLock.close();
                C4500T c4500t = new C4500T();
                this.mThread = c4500t;
                c4500t.start();
                this.mLock.block();
                this.mOffCalled = false;
            } else {
                thread.interrupt();
                alreadyOn = true;
            }
            m11on(alreadyOn);
        }
    }

    public void cancel() {
        synchronized (this) {
            this.mOffAt = 0L;
            Thread thread = this.mThread;
            if (thread != null) {
                thread.interrupt();
                this.mThread = null;
            }
            if (!this.mOffCalled) {
                this.mOffCalled = true;
                off();
            }
        }
    }

    /* renamed from: com.android.server.ResettableTimeout$T */
    /* loaded from: classes5.dex */
    private class C4500T extends Thread {
        private C4500T() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            long diff;
            ResettableTimeout.this.mLock.open();
            while (true) {
                synchronized (this) {
                    diff = ResettableTimeout.this.mOffAt - SystemClock.uptimeMillis();
                    if (diff <= 0) {
                        ResettableTimeout.this.mOffCalled = true;
                        ResettableTimeout.this.off();
                        ResettableTimeout.this.mThread = null;
                        return;
                    }
                }
                try {
                    sleep(diff);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
