package com.android.internal.telephony;
/* loaded from: classes.dex */
public final class SomeArgs {
    private static SomeArgs sPool;
    private static Object sPoolLock = new Object();
    private static int sPoolSize;
    public Object arg1;
    public Object arg2;
    public Object arg3;
    public Object arg4;
    public Object arg5;
    public Object arg6;
    public Object arg7;
    public int argi1;
    public int argi2;
    public int argi3;
    public int argi4;
    public int argi5;
    public int argi6;
    public long argl1;
    public long argl2;
    private boolean mInPool;
    private SomeArgs mNext;
    int mWaitState = 0;

    private SomeArgs() {
    }

    public static SomeArgs obtain() {
        synchronized (sPoolLock) {
            int i = sPoolSize;
            if (i > 0) {
                SomeArgs someArgs = sPool;
                sPool = someArgs.mNext;
                someArgs.mNext = null;
                someArgs.mInPool = false;
                sPoolSize = i - 1;
                return someArgs;
            }
            return new SomeArgs();
        }
    }

    public void complete() {
        synchronized (this) {
            if (this.mWaitState != 1) {
                throw new IllegalStateException("Not waiting");
            }
            this.mWaitState = 2;
            notifyAll();
        }
    }

    public void recycle() {
        if (this.mInPool) {
            throw new IllegalStateException("Already recycled.");
        }
        if (this.mWaitState != 0) {
            return;
        }
        synchronized (sPoolLock) {
            clear();
            int i = sPoolSize;
            if (i < 10) {
                this.mNext = sPool;
                this.mInPool = true;
                sPool = this;
                sPoolSize = i + 1;
            }
        }
    }

    private void clear() {
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
        this.arg4 = null;
        this.arg5 = null;
        this.arg6 = null;
        this.arg7 = null;
        this.argi1 = 0;
        this.argi2 = 0;
        this.argi3 = 0;
        this.argi4 = 0;
        this.argi5 = 0;
        this.argi6 = 0;
        this.argl1 = 0L;
        this.argl2 = 0L;
    }
}
