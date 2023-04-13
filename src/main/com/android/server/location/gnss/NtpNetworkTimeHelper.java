package com.android.server.location.gnss;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import android.util.NtpTrustedTime;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.location.gnss.NetworkTimeHelper;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class NtpNetworkTimeHelper extends NetworkTimeHelper {
    public static final boolean DEBUG = Log.isLoggable("NtpNetworkTimeHelper", 3);
    @VisibleForTesting
    static final long NTP_INTERVAL = 86400000;
    @VisibleForTesting
    static final long RETRY_INTERVAL = 300000;
    public final NetworkTimeHelper.InjectTimeCallback mCallback;
    public final ConnectivityManager mConnMgr;
    public final LocalLog mDumpLog;
    public final Handler mHandler;
    @GuardedBy({"this"})
    public int mInjectNtpTimeState;
    @GuardedBy({"this"})
    public final ExponentialBackOff mNtpBackOff;
    public final NtpTrustedTime mNtpTime;
    @GuardedBy({"this"})
    public boolean mPeriodicTimeInjection;
    public final PowerManager.WakeLock mWakeLock;

    @VisibleForTesting
    public NtpNetworkTimeHelper(Context context, Looper looper, NetworkTimeHelper.InjectTimeCallback injectTimeCallback, NtpTrustedTime ntpTrustedTime) {
        this.mDumpLog = new LocalLog(10, false);
        this.mNtpBackOff = new ExponentialBackOff(300000L, BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        this.mInjectNtpTimeState = 0;
        this.mConnMgr = (ConnectivityManager) context.getSystemService("connectivity");
        this.mCallback = injectTimeCallback;
        this.mNtpTime = ntpTrustedTime;
        this.mHandler = new Handler(looper);
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "NtpTimeHelper");
    }

    public NtpNetworkTimeHelper(Context context, Looper looper, NetworkTimeHelper.InjectTimeCallback injectTimeCallback) {
        this(context, looper, injectTimeCallback, NtpTrustedTime.getInstance(context));
    }

    @Override // com.android.server.location.gnss.NetworkTimeHelper
    public synchronized void setPeriodicTimeInjectionMode(boolean z) {
        if (z) {
            this.mPeriodicTimeInjection = true;
        }
    }

    @Override // com.android.server.location.gnss.NetworkTimeHelper
    public void demandUtcTimeInjection() {
        lambda$blockingGetNtpTimeAndInject$0("demandUtcTimeInjection");
    }

    @Override // com.android.server.location.gnss.NetworkTimeHelper
    public synchronized void onNetworkAvailable() {
        if (this.mInjectNtpTimeState == 0) {
            lambda$blockingGetNtpTimeAndInject$0("onNetworkAvailable");
        }
    }

    @Override // com.android.server.location.gnss.NetworkTimeHelper
    public void dump(PrintWriter printWriter) {
        printWriter.println("NtpNetworkTimeHelper:");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        synchronized (this) {
            indentingPrintWriter.println("mInjectNtpTimeState=" + this.mInjectNtpTimeState);
            indentingPrintWriter.println("mPeriodicTimeInjection=" + this.mPeriodicTimeInjection);
            indentingPrintWriter.println("mNtpBackOff=" + this.mNtpBackOff);
        }
        indentingPrintWriter.println("Debug log:");
        indentingPrintWriter.increaseIndent();
        this.mDumpLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("NtpTrustedTime:");
        indentingPrintWriter.increaseIndent();
        this.mNtpTime.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }

    public final boolean isNetworkConnected() {
        NetworkInfo activeNetworkInfo = this.mConnMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /* renamed from: retrieveAndInjectNtpTime */
    public final synchronized void lambda$blockingGetNtpTimeAndInject$0(String str) {
        if (this.mInjectNtpTimeState == 1) {
            return;
        }
        if (!isNetworkConnected()) {
            maybeInjectCachedNtpTime(str + "[Network not connected]");
            this.mInjectNtpTimeState = 0;
            return;
        }
        this.mInjectNtpTimeState = 1;
        this.mWakeLock.acquire(60000L);
        new Thread(new Runnable() { // from class: com.android.server.location.gnss.NtpNetworkTimeHelper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NtpNetworkTimeHelper.this.blockingGetNtpTimeAndInject();
            }
        }).start();
    }

    public final void blockingGetNtpTimeAndInject() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        NtpTrustedTime.TimeResult cachedTimeResult = this.mNtpTime.getCachedTimeResult();
        long j = 86400000;
        boolean forceRefresh = (cachedTimeResult == null || cachedTimeResult.getAgeMillis() >= 86400000) ? this.mNtpTime.forceRefresh() : true;
        synchronized (this) {
            this.mInjectNtpTimeState = 2;
            if (maybeInjectCachedNtpTime("blockingGetNtpTimeAndInject:, debugId=" + elapsedRealtime + ", refreshSuccess=" + forceRefresh)) {
                this.mNtpBackOff.reset();
            } else {
                logWarn("maybeInjectCachedNtpTime() returned false");
                j = this.mNtpBackOff.nextBackoffMillis();
            }
            if (this.mPeriodicTimeInjection || !forceRefresh) {
                logDebug("blockingGetNtpTimeAndInject: Scheduling later NTP retrieval, debugId=" + elapsedRealtime + ", mPeriodicTimeInjection=" + this.mPeriodicTimeInjection + ", refreshSuccess=" + forceRefresh + ", delayMillis=" + j);
                StringBuilder sb = new StringBuilder();
                sb.append("scheduled: debugId=");
                sb.append(elapsedRealtime);
                final String sb2 = sb.toString();
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.location.gnss.NtpNetworkTimeHelper$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        NtpNetworkTimeHelper.this.lambda$blockingGetNtpTimeAndInject$0(sb2);
                    }
                }, j);
            }
        }
        this.mWakeLock.release();
    }

    public final synchronized boolean maybeInjectCachedNtpTime(String str) {
        NtpTrustedTime.TimeResult cachedTimeResult = this.mNtpTime.getCachedTimeResult();
        if (cachedTimeResult != null && cachedTimeResult.getAgeMillis() < 86400000) {
            final long timeMillis = cachedTimeResult.getTimeMillis();
            long currentTimeMillis = System.currentTimeMillis();
            logDebug("maybeInjectCachedNtpTime: Injecting latest NTP time, reason=" + str + ", ntpResult=" + cachedTimeResult + ", System time offset millis=" + (timeMillis - currentTimeMillis));
            final long elapsedRealtimeMillis = cachedTimeResult.getElapsedRealtimeMillis();
            final int uncertaintyMillis = cachedTimeResult.getUncertaintyMillis();
            this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.NtpNetworkTimeHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NtpNetworkTimeHelper.this.lambda$maybeInjectCachedNtpTime$1(timeMillis, elapsedRealtimeMillis, uncertaintyMillis);
                }
            });
            return true;
        }
        logDebug("maybeInjectCachedNtpTime: Not injecting latest NTP time, reason=" + str + ", ntpResult=" + cachedTimeResult);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeInjectCachedNtpTime$1(long j, long j2, int i) {
        this.mCallback.injectTime(j, j2, i);
    }

    public final void logWarn(String str) {
        this.mDumpLog.log(str);
        Log.e("NtpNetworkTimeHelper", str);
    }

    public final void logDebug(String str) {
        this.mDumpLog.log(str);
        if (DEBUG) {
            Log.d("NtpNetworkTimeHelper", str);
        }
    }
}
