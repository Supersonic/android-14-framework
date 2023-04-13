package com.android.internal.view;

import android.p008os.Looper;
/* loaded from: classes2.dex */
public class WindowManagerPolicyThread {
    static Looper mLooper;
    static Thread mThread;

    public static void set(Thread thread, Looper looper) {
        mThread = thread;
        mLooper = looper;
    }

    public static Thread getThread() {
        return mThread;
    }

    public static Looper getLooper() {
        return mLooper;
    }
}
