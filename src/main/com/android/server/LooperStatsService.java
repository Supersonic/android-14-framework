package com.android.server;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.util.NetworkConstants;
import android.os.Binder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.KeyValueListParser;
import android.util.Slog;
import com.android.internal.os.AppIdToPackageMap;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.CachedDeviceState;
import com.android.internal.os.LooperStats;
import com.android.internal.util.DumpUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
/* loaded from: classes.dex */
public class LooperStatsService extends Binder {
    public final Context mContext;
    public boolean mEnabled;
    public boolean mIgnoreBatteryStatus;
    public final LooperStats mStats;
    public boolean mTrackScreenInteractive;

    public LooperStatsService(Context context, LooperStats looperStats) {
        this.mEnabled = false;
        this.mTrackScreenInteractive = false;
        this.mIgnoreBatteryStatus = false;
        this.mContext = context;
        this.mStats = looperStats;
    }

    public final void initFromSettings() {
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(Settings.Global.getString(this.mContext.getContentResolver(), "looper_stats"));
        } catch (IllegalArgumentException e) {
            Slog.e("LooperStatsService", "Bad looper_stats settings", e);
        }
        setSamplingInterval(keyValueListParser.getInt("sampling_interval", 1000));
        setTrackScreenInteractive(keyValueListParser.getBoolean("track_screen_state", false));
        setIgnoreBatteryStatus(keyValueListParser.getBoolean("ignore_battery_status", false));
        setEnabled(SystemProperties.getBoolean("debug.sys.looper_stats_enabled", keyValueListParser.getBoolean("enabled", true)));
    }

    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new LooperShellCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "LooperStatsService", printWriter)) {
            AppIdToPackageMap snapshot = AppIdToPackageMap.getSnapshot();
            printWriter.print("Start time: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mStats.getStartTimeMillis()));
            printWriter.print("On battery time (ms): ");
            printWriter.println(this.mStats.getBatteryTimeMillis());
            List entries = this.mStats.getEntries();
            entries.sort(Comparator.comparing(new Function() { // from class: com.android.server.LooperStatsService$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$dump$0;
                    lambda$dump$0 = LooperStatsService.lambda$dump$0((LooperStats.ExportedEntry) obj);
                    return lambda$dump$0;
                }
            }).thenComparing(new Function() { // from class: com.android.server.LooperStatsService$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((LooperStats.ExportedEntry) obj).threadName;
                    return str;
                }
            }).thenComparing(new Function() { // from class: com.android.server.LooperStatsService$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((LooperStats.ExportedEntry) obj).handlerClassName;
                    return str;
                }
            }).thenComparing(new Function() { // from class: com.android.server.LooperStatsService$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((LooperStats.ExportedEntry) obj).messageName;
                    return str;
                }
            }));
            printWriter.println(String.join(",", Arrays.asList("work_source_uid", "thread_name", "handler_class", "message_name", "is_interactive", "message_count", "recorded_message_count", "total_latency_micros", "max_latency_micros", "total_cpu_micros", "max_cpu_micros", "recorded_delay_message_count", "total_delay_millis", "max_delay_millis", "exception_count")));
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                LooperStats.ExportedEntry exportedEntry = (LooperStats.ExportedEntry) it.next();
                if (!exportedEntry.messageName.startsWith("__DEBUG_")) {
                    printWriter.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", snapshot.mapUid(exportedEntry.workSourceUid), exportedEntry.threadName, exportedEntry.handlerClassName, exportedEntry.messageName, Boolean.valueOf(exportedEntry.isInteractive), Long.valueOf(exportedEntry.messageCount), Long.valueOf(exportedEntry.recordedMessageCount), Long.valueOf(exportedEntry.totalLatencyMicros), Long.valueOf(exportedEntry.maxLatencyMicros), Long.valueOf(exportedEntry.cpuUsageMicros), Long.valueOf(exportedEntry.maxCpuUsageMicros), Long.valueOf(exportedEntry.recordedDelayMessageCount), Long.valueOf(exportedEntry.delayMillis), Long.valueOf(exportedEntry.maxDelayMillis), Long.valueOf(exportedEntry.exceptionCount));
                    it = it;
                }
            }
        }
    }

    public static /* synthetic */ Integer lambda$dump$0(LooperStats.ExportedEntry exportedEntry) {
        return Integer.valueOf(exportedEntry.workSourceUid);
    }

    public final void setEnabled(boolean z) {
        if (this.mEnabled != z) {
            this.mEnabled = z;
            this.mStats.reset();
            this.mStats.setAddDebugEntries(z);
            Looper.setObserver(z ? this.mStats : null);
        }
    }

    public final void setTrackScreenInteractive(boolean z) {
        if (this.mTrackScreenInteractive != z) {
            this.mTrackScreenInteractive = z;
            this.mStats.reset();
        }
    }

    public final void setIgnoreBatteryStatus(boolean z) {
        if (this.mIgnoreBatteryStatus != z) {
            this.mStats.setIgnoreBatteryStatus(z);
            this.mIgnoreBatteryStatus = z;
            this.mStats.reset();
        }
    }

    public final void setSamplingInterval(int i) {
        if (i > 0) {
            this.mStats.setSamplingInterval(i);
            return;
        }
        Slog.w("LooperStatsService", "Ignored invalid sampling interval (value must be positive): " + i);
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public final LooperStatsService mService;
        public final SettingsObserver mSettingsObserver;
        public final LooperStats mStats;

        public Lifecycle(Context context) {
            super(context);
            LooperStats looperStats = new LooperStats(1000, (int) NetworkConstants.ETHER_MTU);
            this.mStats = looperStats;
            LooperStatsService looperStatsService = new LooperStatsService(getContext(), looperStats);
            this.mService = looperStatsService;
            this.mSettingsObserver = new SettingsObserver(looperStatsService);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishLocalService(LooperStats.class, this.mStats);
            publishBinderService("looper_stats", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (500 == i) {
                this.mService.initFromSettings();
                getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("looper_stats"), false, this.mSettingsObserver, 0);
                this.mStats.setDeviceState((CachedDeviceState.Readonly) getLocalService(CachedDeviceState.Readonly.class));
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SettingsObserver extends ContentObserver {
        public final LooperStatsService mService;

        public SettingsObserver(LooperStatsService looperStatsService) {
            super(BackgroundThread.getHandler());
            this.mService = looperStatsService;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            this.mService.initFromSettings();
        }
    }

    /* loaded from: classes.dex */
    public class LooperShellCommand extends ShellCommand {
        public LooperShellCommand() {
        }

        public int onCommand(String str) {
            if ("enable".equals(str)) {
                LooperStatsService.this.setEnabled(true);
                return 0;
            } else if ("disable".equals(str)) {
                LooperStatsService.this.setEnabled(false);
                return 0;
            } else if ("reset".equals(str)) {
                LooperStatsService.this.mStats.reset();
                return 0;
            } else if ("sampling_interval".equals(str)) {
                LooperStatsService.this.setSamplingInterval(Integer.parseUnsignedInt(getNextArgRequired()));
                return 0;
            } else {
                return handleDefaultCommands(str);
            }
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("looper_stats commands:");
            outPrintWriter.println("  enable: Enable collecting stats.");
            outPrintWriter.println("  disable: Disable collecting stats.");
            outPrintWriter.println("  sampling_interval: Change the sampling interval.");
            outPrintWriter.println("  reset: Reset stats.");
        }
    }
}
