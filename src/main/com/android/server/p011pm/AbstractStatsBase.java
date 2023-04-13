package com.android.server.p011pm;

import android.os.Environment;
import android.os.SystemClock;
import android.util.AtomicFile;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/* renamed from: com.android.server.pm.AbstractStatsBase */
/* loaded from: classes2.dex */
public abstract class AbstractStatsBase<T> {
    public final String mBackgroundThreadName;
    public final String mFileName;
    public final boolean mLock;
    public final Object mFileLock = new Object();
    public final AtomicLong mLastTimeWritten = new AtomicLong(0);
    public final AtomicBoolean mBackgroundWriteRunning = new AtomicBoolean(false);

    public abstract void readInternal(T t);

    public abstract void writeInternal(T t);

    public AbstractStatsBase(String str, String str2, boolean z) {
        this.mFileName = str;
        this.mBackgroundThreadName = str2;
        this.mLock = z;
    }

    public AtomicFile getFile() {
        return new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), this.mFileName));
    }

    public void writeNow(T t) {
        writeImpl(t);
        this.mLastTimeWritten.set(SystemClock.elapsedRealtime());
    }

    public boolean maybeWriteAsync(final T t) {
        if (SystemClock.elapsedRealtime() - this.mLastTimeWritten.get() >= 1800000 && this.mBackgroundWriteRunning.compareAndSet(false, true)) {
            new Thread(this.mBackgroundThreadName) { // from class: com.android.server.pm.AbstractStatsBase.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        AbstractStatsBase.this.writeImpl(t);
                        AbstractStatsBase.this.mLastTimeWritten.set(SystemClock.elapsedRealtime());
                    } finally {
                        AbstractStatsBase.this.mBackgroundWriteRunning.set(false);
                    }
                }
            }.start();
            return true;
        }
        return false;
    }

    public final void writeImpl(T t) {
        if (this.mLock) {
            synchronized (t) {
                synchronized (this.mFileLock) {
                    writeInternal(t);
                }
            }
            return;
        }
        synchronized (this.mFileLock) {
            writeInternal(t);
        }
    }

    public void read(T t) {
        if (this.mLock) {
            synchronized (t) {
                synchronized (this.mFileLock) {
                    readInternal(t);
                }
            }
        } else {
            synchronized (this.mFileLock) {
                readInternal(t);
            }
        }
        this.mLastTimeWritten.set(SystemClock.elapsedRealtime());
    }
}
