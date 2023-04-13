package com.android.server.location.gnss;

import android.content.Context;
import android.os.Looper;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public abstract class NetworkTimeHelper {

    /* loaded from: classes.dex */
    public interface InjectTimeCallback {
        void injectTime(long j, long j2, int i);
    }

    public abstract void demandUtcTimeInjection();

    public abstract void dump(PrintWriter printWriter);

    public abstract void onNetworkAvailable();

    public abstract void setPeriodicTimeInjectionMode(boolean z);

    public static NetworkTimeHelper create(Context context, Looper looper, InjectTimeCallback injectTimeCallback) {
        return new NtpNetworkTimeHelper(context, looper, injectTimeCallback);
    }
}
