package com.android.server.power;

import android.app.ActivityManagerInternal;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.app.admin.SecurityLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemVibrator;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.TimingsTraceLog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.RescueParty;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
/* loaded from: classes2.dex */
public final class ShutdownThread extends Thread {
    public static String mReason = null;
    public static boolean mReboot = false;
    public static boolean mRebootHasProgressBar = false;
    public static boolean mRebootSafeMode = false;
    public static AlertDialog sConfirmDialog = null;
    public static boolean sIsStarted = false;
    public boolean mActionDone;
    public final Object mActionDoneSync = new Object();
    public Context mContext;
    public PowerManager.WakeLock mCpuWakeLock;
    public Handler mHandler;
    public PowerManager mPowerManager;
    public ProgressDialog mProgressDialog;
    public PowerManager.WakeLock mScreenWakeLock;
    public static final Object sIsStartedGuard = new Object();
    public static final ShutdownThread sInstance = new ShutdownThread();
    public static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    public static final ArrayMap<String, Long> TRON_METRICS = new ArrayMap<>();
    public static String METRIC_SYSTEM_SERVER = "shutdown_system_server";
    public static String METRIC_SEND_BROADCAST = "shutdown_send_shutdown_broadcast";
    public static String METRIC_AM = "shutdown_activity_manager";
    public static String METRIC_PM = "shutdown_package_manager";
    public static String METRIC_RADIOS = "shutdown_radios";
    public static String METRIC_RADIO = "shutdown_radio";
    public static String METRIC_SHUTDOWN_TIME_START = "begin_shutdown";

    public static void shutdown(Context context, String str, boolean z) {
        mReboot = false;
        mRebootSafeMode = false;
        mReason = str;
        shutdownInner(context, z);
    }

    public static void shutdownInner(final Context context, boolean z) {
        context.assertRuntimeOverlayThemable();
        synchronized (sIsStartedGuard) {
            if (sIsStarted) {
                return;
            }
            ShutdownCheckPoints.recordCheckPoint(null);
            int i = mRebootSafeMode ? 17041381 : context.getResources().getInteger(17694869) == 2 ? 17041533 : 17041532;
            if (z) {
                CloseDialogReceiver closeDialogReceiver = new CloseDialogReceiver(context);
                AlertDialog alertDialog = sConfirmDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                AlertDialog create = new AlertDialog.Builder(context).setTitle(mRebootSafeMode ? 17041382 : 17041362).setMessage(i).setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.android.server.power.ShutdownThread.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        ShutdownThread.beginShutdownSequence(context);
                    }
                }).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).create();
                sConfirmDialog = create;
                closeDialogReceiver.dialog = create;
                create.setOnDismissListener(closeDialogReceiver);
                sConfirmDialog.getWindow().setType(2009);
                sConfirmDialog.show();
                return;
            }
            beginShutdownSequence(context);
        }
    }

    /* loaded from: classes2.dex */
    public static class CloseDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        public Dialog dialog;
        public Context mContext;

        public CloseDialogReceiver(Context context) {
            this.mContext = context;
            context.registerReceiver(this, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"), 2);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.dialog.cancel();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            this.mContext.unregisterReceiver(this);
        }
    }

    public static void reboot(Context context, String str, boolean z) {
        mReboot = true;
        mRebootSafeMode = false;
        mRebootHasProgressBar = false;
        mReason = str;
        shutdownInner(context, z);
    }

    public static void rebootSafeMode(Context context, boolean z) {
        if (((UserManager) context.getSystemService("user")).hasUserRestriction("no_safe_boot")) {
            return;
        }
        mReboot = true;
        mRebootSafeMode = true;
        mRebootHasProgressBar = false;
        mReason = null;
        shutdownInner(context, z);
    }

    public static ProgressDialog showShutdownDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        String str = mReason;
        if (str != null && str.startsWith("recovery-update")) {
            mRebootHasProgressBar = RecoverySystem.UNCRYPT_PACKAGE_FILE.exists() && !RecoverySystem.BLOCK_MAP_FILE.exists();
            progressDialog.setTitle(context.getText(17041388));
            if (mRebootHasProgressBar) {
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressNumberFormat(null);
                progressDialog.setProgressStyle(1);
                progressDialog.setMessage(context.getText(17041386));
            } else if (showSysuiReboot()) {
                return null;
            } else {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(context.getText(17041387));
            }
        } else {
            String str2 = mReason;
            if (str2 != null && str2.equals("recovery")) {
                if (RescueParty.isAttemptingFactoryReset()) {
                    progressDialog.setTitle(context.getText(17041362));
                    progressDialog.setMessage(context.getText(17041534));
                    progressDialog.setIndeterminate(true);
                } else if (showSysuiReboot()) {
                    return null;
                } else {
                    progressDialog.setTitle(context.getText(17041384));
                    progressDialog.setMessage(context.getText(17041383));
                    progressDialog.setIndeterminate(true);
                }
            } else if (showSysuiReboot()) {
                return null;
            } else {
                progressDialog.setTitle(context.getText(17041362));
                progressDialog.setMessage(context.getText(17041534));
                progressDialog.setIndeterminate(true);
            }
        }
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setType(2009);
        progressDialog.show();
        return progressDialog;
    }

    public static boolean showSysuiReboot() {
        try {
            return ((StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class)).showShutdownUi(mReboot, mReason);
        } catch (Exception unused) {
            return false;
        }
    }

    public static void beginShutdownSequence(Context context) {
        synchronized (sIsStartedGuard) {
            if (sIsStarted) {
                return;
            }
            sIsStarted = true;
            ShutdownThread shutdownThread = sInstance;
            shutdownThread.mProgressDialog = showShutdownDialog(context);
            shutdownThread.mContext = context;
            PowerManager powerManager = (PowerManager) context.getSystemService("power");
            shutdownThread.mPowerManager = powerManager;
            shutdownThread.mCpuWakeLock = null;
            try {
                PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(1, "ShutdownThread-cpu");
                shutdownThread.mCpuWakeLock = newWakeLock;
                newWakeLock.setReferenceCounted(false);
                shutdownThread.mCpuWakeLock.acquire();
            } catch (SecurityException e) {
                Log.w("ShutdownThread", "No permission to acquire wake lock", e);
                sInstance.mCpuWakeLock = null;
            }
            ShutdownThread shutdownThread2 = sInstance;
            shutdownThread2.mScreenWakeLock = null;
            if (shutdownThread2.mPowerManager.isScreenOn()) {
                try {
                    PowerManager.WakeLock newWakeLock2 = shutdownThread2.mPowerManager.newWakeLock(26, "ShutdownThread-screen");
                    shutdownThread2.mScreenWakeLock = newWakeLock2;
                    newWakeLock2.setReferenceCounted(false);
                    shutdownThread2.mScreenWakeLock.acquire();
                } catch (SecurityException e2) {
                    Log.w("ShutdownThread", "No permission to acquire wake lock", e2);
                    sInstance.mScreenWakeLock = null;
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210010, new Object[0]);
            }
            ShutdownThread shutdownThread3 = sInstance;
            shutdownThread3.mHandler = new Handler() { // from class: com.android.server.power.ShutdownThread.2
            };
            shutdownThread3.start();
        }
    }

    public void actionDone() {
        synchronized (this.mActionDoneSync) {
            this.mActionDone = true;
            this.mActionDoneSync.notifyAll();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        TimingsTraceLog newTimingsLog = newTimingsLog();
        newTimingsLog.traceBegin("SystemServerShutdown");
        metricShutdownStart();
        metricStarted(METRIC_SYSTEM_SERVER);
        Thread newDumpThread = ShutdownCheckPoints.newDumpThread(new File("/data/system/shutdown-checkpoints/checkpoints"));
        newDumpThread.start();
        StringBuilder sb = new StringBuilder();
        sb.append(mReboot ? "1" : "0");
        String str = mReason;
        if (str == null) {
            str = "";
        }
        sb.append(str);
        SystemProperties.set("sys.shutdown.requested", sb.toString());
        if (mRebootSafeMode) {
            SystemProperties.set("persist.sys.safemode", "1");
        }
        newTimingsLog.traceBegin("DumpPreRebootInfo");
        try {
            Slog.i("ShutdownThread", "Logging pre-reboot information...");
            PreRebootLogger.log(this.mContext);
        } catch (Exception e) {
            Slog.e("ShutdownThread", "Failed to log pre-reboot information", e);
        }
        newTimingsLog.traceEnd();
        metricStarted(METRIC_SEND_BROADCAST);
        newTimingsLog.traceBegin("SendShutdownBroadcast");
        Log.i("ShutdownThread", "Sending shutdown broadcast...");
        this.mActionDone = false;
        Intent intent = new Intent("android.intent.action.ACTION_SHUTDOWN");
        intent.addFlags(1342177280);
        ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).broadcastIntentWithCallback(intent, new IIntentReceiver.Stub() { // from class: com.android.server.power.ShutdownThread.3
            public void performReceive(Intent intent2, int i, String str2, Bundle bundle, boolean z, boolean z2, int i2) {
                Handler handler = ShutdownThread.this.mHandler;
                final ShutdownThread shutdownThread = ShutdownThread.this;
                handler.post(new Runnable() { // from class: com.android.server.power.ShutdownThread$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ShutdownThread.this.actionDone();
                    }
                });
            }
        }, (String[]) null, -1, (int[]) null, (BiFunction) null, (Bundle) null);
        long elapsedRealtime = SystemClock.elapsedRealtime() + 10000;
        synchronized (this.mActionDoneSync) {
            while (true) {
                if (this.mActionDone) {
                    break;
                }
                long elapsedRealtime2 = elapsedRealtime - SystemClock.elapsedRealtime();
                if (elapsedRealtime2 <= 0) {
                    Log.w("ShutdownThread", "Shutdown broadcast timed out");
                    break;
                }
                if (mRebootHasProgressBar) {
                    sInstance.setRebootProgress((int) ((((10000 - elapsedRealtime2) * 1.0d) * 2.0d) / 10000.0d), null);
                }
                try {
                    this.mActionDoneSync.wait(Math.min(elapsedRealtime2, 500L));
                } catch (InterruptedException unused) {
                }
            }
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(2, null);
        }
        newTimingsLog.traceEnd();
        metricEnded(METRIC_SEND_BROADCAST);
        Log.i("ShutdownThread", "Shutting down activity manager...");
        newTimingsLog.traceBegin("ShutdownActivityManager");
        metricStarted(METRIC_AM);
        IActivityManager asInterface = IActivityManager.Stub.asInterface(ServiceManager.checkService("activity"));
        if (asInterface != null) {
            try {
                asInterface.shutdown((int) FrameworkStatsLog.WIFI_BYTES_TRANSFER);
            } catch (RemoteException unused2) {
            }
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(4, null);
        }
        newTimingsLog.traceEnd();
        metricEnded(METRIC_AM);
        Log.i("ShutdownThread", "Shutting down package manager...");
        newTimingsLog.traceBegin("ShutdownPackageManager");
        metricStarted(METRIC_PM);
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        if (packageManagerInternal != null) {
            packageManagerInternal.shutdown();
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(6, null);
        }
        newTimingsLog.traceEnd();
        metricEnded(METRIC_PM);
        newTimingsLog.traceBegin("ShutdownRadios");
        metricStarted(METRIC_RADIOS);
        shutdownRadios(12000);
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(18, null);
        }
        newTimingsLog.traceEnd();
        metricEnded(METRIC_RADIOS);
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(20, null);
            uncrypt();
        }
        newTimingsLog.traceBegin("ShutdownCheckPointsDumpWait");
        try {
            newDumpThread.join(10000L);
        } catch (InterruptedException unused3) {
        }
        newTimingsLog.traceEnd();
        newTimingsLog.traceEnd();
        metricEnded(METRIC_SYSTEM_SERVER);
        saveMetrics(mReboot, mReason);
        rebootOrShutdown(this.mContext, mReboot, mReason);
    }

    public static TimingsTraceLog newTimingsLog() {
        return new TimingsTraceLog("ShutdownTiming", 524288L);
    }

    public static void metricStarted(String str) {
        ArrayMap<String, Long> arrayMap = TRON_METRICS;
        synchronized (arrayMap) {
            arrayMap.put(str, Long.valueOf(SystemClock.elapsedRealtime() * (-1)));
        }
    }

    public static void metricEnded(String str) {
        ArrayMap<String, Long> arrayMap = TRON_METRICS;
        synchronized (arrayMap) {
            arrayMap.put(str, Long.valueOf(SystemClock.elapsedRealtime() + arrayMap.get(str).longValue()));
        }
    }

    public static void metricShutdownStart() {
        ArrayMap<String, Long> arrayMap = TRON_METRICS;
        synchronized (arrayMap) {
            arrayMap.put(METRIC_SHUTDOWN_TIME_START, Long.valueOf(System.currentTimeMillis()));
        }
    }

    public final void setRebootProgress(final int i, final CharSequence charSequence) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.power.ShutdownThread.4
            @Override // java.lang.Runnable
            public void run() {
                if (ShutdownThread.this.mProgressDialog != null) {
                    ShutdownThread.this.mProgressDialog.setProgress(i);
                    if (charSequence != null) {
                        ShutdownThread.this.mProgressDialog.setMessage(charSequence);
                    }
                }
            }
        });
    }

    public final void shutdownRadios(final int i) {
        long j = i;
        final long elapsedRealtime = SystemClock.elapsedRealtime() + j;
        final boolean[] zArr = new boolean[1];
        Thread thread = new Thread() { // from class: com.android.server.power.ShutdownThread.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                TimingsTraceLog newTimingsLog = ShutdownThread.newTimingsLog();
                TelephonyManager telephonyManager = (TelephonyManager) ShutdownThread.this.mContext.getSystemService(TelephonyManager.class);
                boolean z = telephonyManager == null || !telephonyManager.isAnyRadioPoweredOn();
                if (!z) {
                    Log.w("ShutdownThread", "Turning off cellular radios...");
                    ShutdownThread.metricStarted(ShutdownThread.METRIC_RADIO);
                    telephonyManager.shutdownAllRadios();
                }
                Log.i("ShutdownThread", "Waiting for Radio...");
                long j2 = elapsedRealtime;
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                while (true) {
                    long j3 = j2 - elapsedRealtime2;
                    if (j3 <= 0) {
                        return;
                    }
                    if (ShutdownThread.mRebootHasProgressBar) {
                        int i2 = i;
                        ShutdownThread.sInstance.setRebootProgress(((int) ((((i2 - j3) * 1.0d) * 12.0d) / i2)) + 6, null);
                    }
                    if (!z && (!telephonyManager.isAnyRadioPoweredOn())) {
                        Log.i("ShutdownThread", "Radio turned off.");
                        ShutdownThread.metricEnded(ShutdownThread.METRIC_RADIO);
                        newTimingsLog.logDuration("ShutdownRadio", ((Long) ShutdownThread.TRON_METRICS.get(ShutdownThread.METRIC_RADIO)).longValue());
                    }
                    if (z) {
                        Log.i("ShutdownThread", "Radio shutdown complete.");
                        zArr[0] = true;
                        return;
                    }
                    SystemClock.sleep(100L);
                    j2 = elapsedRealtime;
                    elapsedRealtime2 = SystemClock.elapsedRealtime();
                }
            }
        };
        thread.start();
        try {
            thread.join(j);
        } catch (InterruptedException unused) {
        }
        if (zArr[0]) {
            return;
        }
        Log.w("ShutdownThread", "Timed out waiting for Radio shutdown.");
    }

    public static void rebootOrShutdown(Context context, boolean z, String str) {
        if (z) {
            Log.i("ShutdownThread", "Rebooting, reason: " + str);
            PowerManagerService.lowLevelReboot(str);
            Log.e("ShutdownThread", "Reboot failed, will attempt shutdown instead");
            str = null;
        } else if (context != null) {
            try {
                new SystemVibrator(context).vibrate(500L, VIBRATION_ATTRIBUTES);
            } catch (Exception e) {
                Log.w("ShutdownThread", "Failed to vibrate during shutdown.", e);
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException unused) {
            }
        }
        Log.i("ShutdownThread", "Performing low-level shutdown...");
        PowerManagerService.lowLevelShutdown(str);
    }

    public static void saveMetrics(boolean z, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("reboot:");
        sb.append(z ? "y" : "n");
        sb.append(",");
        sb.append("reason:");
        sb.append(str);
        int size = TRON_METRICS.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            ArrayMap<String, Long> arrayMap = TRON_METRICS;
            String keyAt = arrayMap.keyAt(i);
            long longValue = arrayMap.valueAt(i).longValue();
            if (longValue < 0) {
                Log.e("ShutdownThread", "metricEnded wasn't called for " + keyAt);
            } else {
                sb.append(',');
                sb.append(keyAt);
                sb.append(':');
                sb.append(longValue);
            }
        }
        File file = new File("/data/system/shutdown-metrics.tmp");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            z2 = true;
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("ShutdownThread", "Cannot save shutdown metrics", e);
        }
        if (z2) {
            file.renameTo(new File("/data/system/shutdown-metrics.txt"));
        }
    }

    public final void uncrypt() {
        Log.i("ShutdownThread", "Calling uncrypt and monitoring the progress...");
        final RecoverySystem.ProgressListener progressListener = new RecoverySystem.ProgressListener() { // from class: com.android.server.power.ShutdownThread.6
            @Override // android.os.RecoverySystem.ProgressListener
            public void onProgress(int i) {
                if (i >= 0 && i < 100) {
                    CharSequence text = ShutdownThread.this.mContext.getText(17041385);
                    ShutdownThread.sInstance.setRebootProgress(((int) ((i * 80.0d) / 100.0d)) + 20, text);
                } else if (i == 100) {
                    ShutdownThread.sInstance.setRebootProgress(i, ShutdownThread.this.mContext.getText(17041387));
                }
            }
        };
        final boolean[] zArr = {false};
        Thread thread = new Thread() { // from class: com.android.server.power.ShutdownThread.7
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                RecoverySystem recoverySystem = (RecoverySystem) ShutdownThread.this.mContext.getSystemService("recovery");
                try {
                    RecoverySystem.processPackage(ShutdownThread.this.mContext, new File(FileUtils.readTextFile(RecoverySystem.UNCRYPT_PACKAGE_FILE, 0, null)), progressListener);
                } catch (IOException e) {
                    Log.e("ShutdownThread", "Error uncrypting file", e);
                }
                zArr[0] = true;
            }
        };
        thread.start();
        try {
            thread.join(900000L);
        } catch (InterruptedException unused) {
        }
        if (zArr[0]) {
            return;
        }
        Log.w("ShutdownThread", "Timed out waiting for uncrypt.");
        try {
            FileUtils.stringToFile(RecoverySystem.UNCRYPT_STATUS_FILE, String.format("uncrypt_time: %d\nuncrypt_error: %d\n", 900, 100));
        } catch (IOException e) {
            Log.e("ShutdownThread", "Failed to write timeout message to uncrypt status", e);
        }
    }
}
