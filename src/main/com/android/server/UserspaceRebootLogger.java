package com.android.server;

import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class UserspaceRebootLogger {
    public static void noteUserspaceRebootWasRequested() {
        if (!PowerManager.isRebootingUserspaceSupportedImpl()) {
            Slog.wtf("UserspaceRebootLogger", "noteUserspaceRebootWasRequested: Userspace reboot is not supported.");
            return;
        }
        SystemProperties.set("persist.sys.userspace_reboot.log.should_log", "1");
        SystemProperties.set("sys.userspace_reboot.log.last_started", String.valueOf(SystemClock.elapsedRealtime()));
    }

    public static void noteUserspaceRebootSuccess() {
        if (!PowerManager.isRebootingUserspaceSupportedImpl()) {
            Slog.wtf("UserspaceRebootLogger", "noteUserspaceRebootSuccess: Userspace reboot is not supported.");
        } else {
            SystemProperties.set("sys.userspace_reboot.log.last_finished", String.valueOf(SystemClock.elapsedRealtime()));
        }
    }

    public static boolean shouldLogUserspaceRebootEvent() {
        if (PowerManager.isRebootingUserspaceSupportedImpl()) {
            return SystemProperties.getBoolean("persist.sys.userspace_reboot.log.should_log", false);
        }
        return false;
    }

    public static void logEventAsync(boolean z, Executor executor) {
        if (!PowerManager.isRebootingUserspaceSupportedImpl()) {
            Slog.wtf("UserspaceRebootLogger", "logEventAsync: Userspace reboot is not supported.");
            return;
        }
        final int computeOutcome = computeOutcome();
        final long j = computeOutcome == 1 ? SystemProperties.getLong("sys.userspace_reboot.log.last_finished", 0L) - SystemProperties.getLong("sys.userspace_reboot.log.last_started", 0L) : 0L;
        final int i = z ? 1 : 2;
        executor.execute(new Runnable() { // from class: com.android.server.UserspaceRebootLogger$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UserspaceRebootLogger.lambda$logEventAsync$0(computeOutcome, j, i);
            }
        });
    }

    public static /* synthetic */ void lambda$logEventAsync$0(int i, long j, int i2) {
        Slog.i("UserspaceRebootLogger", "Logging UserspaceRebootReported atom: { outcome: " + i + " durationMillis: " + j + " encryptionState: " + i2 + " }");
        FrameworkStatsLog.write((int) FrameworkStatsLog.USERSPACE_REBOOT_REPORTED, i, j, i2);
        SystemProperties.set("persist.sys.userspace_reboot.log.should_log", "");
    }

    public static int computeOutcome() {
        if (SystemProperties.getLong("sys.userspace_reboot.log.last_started", -1L) != -1) {
            return 1;
        }
        String emptyIfNull = TextUtils.emptyIfNull(SystemProperties.get("sys.boot.reason.last", ""));
        if (emptyIfNull.startsWith("reboot,")) {
            emptyIfNull = emptyIfNull.substring(6);
        }
        if (emptyIfNull.startsWith("userspace_failed,watchdog_fork") || emptyIfNull.startsWith("userspace_failed,shutdown_aborted")) {
            return 2;
        }
        if (emptyIfNull.startsWith("mount_userdata_failed") || emptyIfNull.startsWith("userspace_failed,init_user0") || emptyIfNull.startsWith("userspace_failed,enablefilecrypto")) {
            return 3;
        }
        return emptyIfNull.startsWith("userspace_failed,watchdog_triggered") ? 4 : 0;
    }
}
