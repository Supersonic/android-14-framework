package com.android.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;
/* loaded from: classes.dex */
public class ServiceThread extends HandlerThread {
    public final boolean mAllowIo;

    public ServiceThread(String str, int i, boolean z) {
        super(str, i);
        this.mAllowIo = z;
    }

    @Override // android.os.HandlerThread, java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setCanSelfBackground(false);
        if (!this.mAllowIo) {
            StrictMode.initThreadDefaults(null);
        }
        super.run();
    }

    public static Handler makeSharedHandler(Looper looper) {
        return new Handler(looper, null, false, true);
    }
}
