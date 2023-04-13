package com.android.server.backup.restore;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class RestoreEngine {
    public final AtomicBoolean mRunning = new AtomicBoolean(false);
    public final AtomicInteger mResult = new AtomicInteger(0);

    public boolean isRunning() {
        return this.mRunning.get();
    }

    public void setRunning(boolean z) {
        synchronized (this.mRunning) {
            this.mRunning.set(z);
            this.mRunning.notifyAll();
        }
    }

    public int waitForResult() {
        synchronized (this.mRunning) {
            while (isRunning()) {
                try {
                    this.mRunning.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return getResult();
    }

    public int getResult() {
        return this.mResult.get();
    }

    public void setResult(int i) {
        this.mResult.set(i);
    }
}
