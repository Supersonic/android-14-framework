package com.android.server;

import android.app.IActivityController;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hidl.manager.V1_0.IServiceManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceDebugInfo;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.sysprop.WatchdogProperties;
import android.util.Dumpable;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.criticalevents.CriticalEventLog;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.TraceErrorLogger;
import com.android.server.p014wm.SurfaceAnimationThread;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class Watchdog implements Dumpable {
    public static Watchdog sWatchdog;
    public ActivityManagerService mActivity;
    public boolean mAllowRestart;
    public IActivityController mController;
    public final ArrayList<HandlerCheckerAndTimeout> mHandlerCheckers;
    public final List<Integer> mInterestingJavaPids;
    public final Object mLock = new Object();
    public final HandlerChecker mMonitorChecker;
    public final Thread mThread;
    public final TraceErrorLogger mTraceErrorLogger;
    public volatile long mWatchdogTimeoutMillis;
    public static final String[] NATIVE_STACKS_OF_INTEREST = {"/system/bin/audioserver", "/system/bin/cameraserver", "/system/bin/drmserver", "/system/bin/keystore2", "/system/bin/mediadrmserver", "/system/bin/mediaserver", "/system/bin/netd", "/system/bin/sdcard", "/system/bin/surfaceflinger", "/system/bin/vold", "media.extractor", "media.metrics", "media.codec", "media.swcodec", "media.transcoding", "com.android.bluetooth", "/apex/com.android.os.statsd/bin/statsd"};
    public static final List<String> HAL_INTERFACES_OF_INTEREST = Arrays.asList("android.hardware.audio@4.0::IDevicesFactory", "android.hardware.audio@5.0::IDevicesFactory", "android.hardware.audio@6.0::IDevicesFactory", "android.hardware.audio@7.0::IDevicesFactory", "android.hardware.biometrics.face@1.0::IBiometricsFace", "android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint", "android.hardware.bluetooth@1.0::IBluetoothHci", "android.hardware.camera.provider@2.4::ICameraProvider", "android.hardware.gnss@1.0::IGnss", "android.hardware.graphics.allocator@2.0::IAllocator", "android.hardware.graphics.composer@2.1::IComposer", "android.hardware.health@2.0::IHealth", "android.hardware.light@2.0::ILight", "android.hardware.media.c2@1.0::IComponentStore", "android.hardware.media.omx@1.0::IOmx", "android.hardware.media.omx@1.0::IOmxStore", "android.hardware.neuralnetworks@1.0::IDevice", "android.hardware.power@1.0::IPower", "android.hardware.power.stats@1.0::IPowerStats", "android.hardware.sensors@1.0::ISensors", "android.hardware.sensors@2.0::ISensors", "android.hardware.sensors@2.1::ISensors", "android.hardware.vibrator@1.0::IVibrator", "android.hardware.vr@1.0::IVr", "android.system.suspend@1.0::ISystemSuspend");
    public static final String[] AIDL_INTERFACE_PREFIXES_OF_INTEREST = {"android.hardware.audio.core.IModule/", "android.hardware.audio.core.IConfig/", "android.hardware.biometrics.face.IFace/", "android.hardware.biometrics.fingerprint.IFingerprint/", "android.hardware.bluetooth.IBluetoothHci/", "android.hardware.camera.provider.ICameraProvider/", "android.hardware.gnss.IGnss/", "android.hardware.graphics.allocator.IAllocator/", "android.hardware.graphics.composer3.IComposer/", "android.hardware.health.IHealth/", "android.hardware.input.processor.IInputProcessor/", "android.hardware.light.ILights/", "android.hardware.neuralnetworks.IDevice/", "android.hardware.power.IPower/", "android.hardware.power.stats.IPowerStats/", "android.hardware.sensors.ISensors/", "android.hardware.vibrator.IVibrator/", "android.hardware.vibrator.IVibratorManager/", "android.system.suspend.ISystemSuspend/"};

    /* loaded from: classes.dex */
    public interface Monitor {
        void monitor();
    }

    /* loaded from: classes.dex */
    public static final class HandlerCheckerAndTimeout {
        public final Optional<Long> mCustomTimeoutMillis;
        public final HandlerChecker mHandler;

        public HandlerCheckerAndTimeout(HandlerChecker handlerChecker, Optional<Long> optional) {
            this.mHandler = handlerChecker;
            this.mCustomTimeoutMillis = optional;
        }

        public HandlerChecker checker() {
            return this.mHandler;
        }

        public Optional<Long> customTimeoutMillis() {
            return this.mCustomTimeoutMillis;
        }

        public static HandlerCheckerAndTimeout withDefaultTimeout(HandlerChecker handlerChecker) {
            return new HandlerCheckerAndTimeout(handlerChecker, Optional.empty());
        }

        public static HandlerCheckerAndTimeout withCustomTimeout(HandlerChecker handlerChecker, long j) {
            return new HandlerCheckerAndTimeout(handlerChecker, Optional.of(Long.valueOf(j)));
        }
    }

    /* loaded from: classes.dex */
    public final class HandlerChecker implements Runnable {
        public Monitor mCurrentMonitor;
        public final Handler mHandler;
        public final String mName;
        public int mPauseCount;
        public long mStartTimeMillis;
        public long mWaitMaxMillis;
        public final ArrayList<Monitor> mMonitors = new ArrayList<>();
        public final ArrayList<Monitor> mMonitorQueue = new ArrayList<>();
        public boolean mCompleted = true;

        public HandlerChecker(Handler handler, String str) {
            this.mHandler = handler;
            this.mName = str;
        }

        public void addMonitorLocked(Monitor monitor) {
            this.mMonitorQueue.add(monitor);
        }

        public void scheduleCheckLocked(long j) {
            this.mWaitMaxMillis = j;
            if (this.mCompleted) {
                this.mMonitors.addAll(this.mMonitorQueue);
                this.mMonitorQueue.clear();
            }
            if ((this.mMonitors.size() == 0 && this.mHandler.getLooper().getQueue().isPolling()) || this.mPauseCount > 0) {
                this.mCompleted = true;
            } else if (this.mCompleted) {
                this.mCompleted = false;
                this.mCurrentMonitor = null;
                this.mStartTimeMillis = SystemClock.uptimeMillis();
                this.mHandler.postAtFrontOfQueue(this);
            }
        }

        public int getCompletionStateLocked() {
            if (this.mCompleted) {
                return 0;
            }
            long uptimeMillis = SystemClock.uptimeMillis() - this.mStartTimeMillis;
            long j = this.mWaitMaxMillis;
            if (uptimeMillis < j / 2) {
                return 1;
            }
            return uptimeMillis < j ? 2 : 3;
        }

        public Thread getThread() {
            return this.mHandler.getLooper().getThread();
        }

        public String describeBlockedStateLocked() {
            String str;
            if (this.mCurrentMonitor == null) {
                str = "Blocked in handler on ";
            } else {
                str = "Blocked in monitor " + this.mCurrentMonitor.getClass().getName();
            }
            return str + " on " + this.mName + " (" + getThread().getName() + ") for " + ((SystemClock.uptimeMillis() - this.mStartTimeMillis) / 1000) + "s";
        }

        @Override // java.lang.Runnable
        public void run() {
            Monitor monitor;
            int size = this.mMonitors.size();
            for (int i = 0; i < size; i++) {
                synchronized (Watchdog.this.mLock) {
                    monitor = this.mMonitors.get(i);
                    this.mCurrentMonitor = monitor;
                }
                monitor.monitor();
            }
            synchronized (Watchdog.this.mLock) {
                this.mCompleted = true;
                this.mCurrentMonitor = null;
            }
        }

        public void pauseLocked(String str) {
            this.mPauseCount++;
            this.mCompleted = true;
            Slog.i("Watchdog", "Pausing HandlerChecker: " + this.mName + " for reason: " + str + ". Pause count: " + this.mPauseCount);
        }

        public void resumeLocked(String str) {
            int i = this.mPauseCount;
            if (i > 0) {
                this.mPauseCount = i - 1;
                Slog.i("Watchdog", "Resuming HandlerChecker: " + this.mName + " for reason: " + str + ". Pause count: " + this.mPauseCount);
                return;
            }
            Slog.wtf("Watchdog", "Already resumed HandlerChecker: " + this.mName);
        }
    }

    /* loaded from: classes.dex */
    public final class RebootRequestReceiver extends BroadcastReceiver {
        public RebootRequestReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("nowait", 0) != 0) {
                Watchdog.this.rebootSystem("Received ACTION_REBOOT broadcast");
                return;
            }
            Slog.w("Watchdog", "Unsupported ACTION_REBOOT broadcast: " + intent);
        }
    }

    /* loaded from: classes.dex */
    public static final class BinderThreadMonitor implements Monitor {
        public BinderThreadMonitor() {
        }

        @Override // com.android.server.Watchdog.Monitor
        public void monitor() {
            Binder.blockUntilThreadAvailable();
        }
    }

    public static Watchdog getInstance() {
        if (sWatchdog == null) {
            sWatchdog = new Watchdog();
        }
        return sWatchdog;
    }

    public Watchdog() {
        ArrayList<HandlerCheckerAndTimeout> arrayList = new ArrayList<>();
        this.mHandlerCheckers = arrayList;
        this.mAllowRestart = true;
        this.mWatchdogTimeoutMillis = 60000L;
        ArrayList arrayList2 = new ArrayList();
        this.mInterestingJavaPids = arrayList2;
        this.mThread = new Thread(new Runnable() { // from class: com.android.server.Watchdog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Watchdog.this.run();
            }
        }, "watchdog");
        ServiceThread serviceThread = new ServiceThread("watchdog.monitor", 0, true);
        serviceThread.start();
        HandlerChecker handlerChecker = new HandlerChecker(new Handler(serviceThread.getLooper()), "monitor thread");
        this.mMonitorChecker = handlerChecker;
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(handlerChecker));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(FgThread.getHandler(), "foreground thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(new Handler(Looper.getMainLooper()), "main thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(UiThread.getHandler(), "ui thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(IoThread.getHandler(), "i/o thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(DisplayThread.getHandler(), "display thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(AnimationThread.getHandler(), "animation thread")));
        arrayList.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(SurfaceAnimationThread.getHandler(), "surface animation thread")));
        addMonitor(new BinderThreadMonitor());
        arrayList2.add(Integer.valueOf(Process.myPid()));
        this.mTraceErrorLogger = new TraceErrorLogger();
    }

    public void start() {
        this.mThread.start();
    }

    public void init(Context context, ActivityManagerService activityManagerService) {
        this.mActivity = activityManagerService;
        context.registerReceiver(new RebootRequestReceiver(), new IntentFilter("android.intent.action.REBOOT"), "android.permission.REBOOT", null);
    }

    /* loaded from: classes.dex */
    public static class SettingsObserver extends ContentObserver {
        public final Context mContext;
        public final Uri mUri;
        public final Watchdog mWatchdog;

        public SettingsObserver(Context context, Watchdog watchdog) {
            super(BackgroundThread.getHandler());
            this.mUri = Settings.Global.getUriFor("system_server_watchdog_timeout_ms");
            this.mContext = context;
            this.mWatchdog = watchdog;
            onChange();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.mUri.equals(uri)) {
                onChange();
            }
        }

        public void onChange() {
            try {
                this.mWatchdog.updateWatchdogTimeout(Settings.Global.getLong(this.mContext.getContentResolver(), "system_server_watchdog_timeout_ms", 60000L));
            } catch (RuntimeException e) {
                Slog.e("Watchdog", "Exception while reading settings " + e.getMessage(), e);
            }
        }
    }

    public void registerSettingsObserver(Context context) {
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("system_server_watchdog_timeout_ms"), false, new SettingsObserver(context, this), 0);
    }

    public void updateWatchdogTimeout(long j) {
        if (j <= 20000) {
            j = 20001;
        }
        this.mWatchdogTimeoutMillis = j;
        Slog.i("Watchdog", "Watchdog timeout updated to " + this.mWatchdogTimeoutMillis + " millis");
    }

    public static boolean isInterestingJavaProcess(String str) {
        return str.equals(StorageManagerService.sMediaStoreAuthorityProcessName) || str.equals("com.android.phone");
    }

    public void processStarted(String str, int i) {
        if (isInterestingJavaProcess(str)) {
            Slog.i("Watchdog", "Interesting Java process " + str + " started. Pid " + i);
            synchronized (this.mLock) {
                this.mInterestingJavaPids.add(Integer.valueOf(i));
            }
        }
    }

    public void processDied(String str, int i) {
        if (isInterestingJavaProcess(str)) {
            Slog.i("Watchdog", "Interesting Java process " + str + " died. Pid " + i);
            synchronized (this.mLock) {
                this.mInterestingJavaPids.remove(Integer.valueOf(i));
            }
        }
    }

    public void setActivityController(IActivityController iActivityController) {
        synchronized (this.mLock) {
            this.mController = iActivityController;
        }
    }

    public void setAllowRestart(boolean z) {
        synchronized (this.mLock) {
            this.mAllowRestart = z;
        }
    }

    public void addMonitor(Monitor monitor) {
        synchronized (this.mLock) {
            this.mMonitorChecker.addMonitorLocked(monitor);
        }
    }

    public void addThread(Handler handler) {
        synchronized (this.mLock) {
            this.mHandlerCheckers.add(HandlerCheckerAndTimeout.withDefaultTimeout(new HandlerChecker(handler, handler.getLooper().getThread().getName())));
        }
    }

    public void addThread(Handler handler, long j) {
        synchronized (this.mLock) {
            this.mHandlerCheckers.add(HandlerCheckerAndTimeout.withCustomTimeout(new HandlerChecker(handler, handler.getLooper().getThread().getName()), j));
        }
    }

    public void pauseWatchingCurrentThread(String str) {
        synchronized (this.mLock) {
            Iterator<HandlerCheckerAndTimeout> it = this.mHandlerCheckers.iterator();
            while (it.hasNext()) {
                HandlerChecker checker = it.next().checker();
                if (Thread.currentThread().equals(checker.getThread())) {
                    checker.pauseLocked(str);
                }
            }
        }
    }

    public void resumeWatchingCurrentThread(String str) {
        synchronized (this.mLock) {
            Iterator<HandlerCheckerAndTimeout> it = this.mHandlerCheckers.iterator();
            while (it.hasNext()) {
                HandlerChecker checker = it.next().checker();
                if (Thread.currentThread().equals(checker.getThread())) {
                    checker.resumeLocked(str);
                }
            }
        }
    }

    public void rebootSystem(String str) {
        Slog.i("Watchdog", "Rebooting system because: " + str);
        try {
            ServiceManager.getService("power").reboot(false, str, false);
        } catch (RemoteException unused) {
        }
    }

    public final int evaluateCheckerCompletionLocked() {
        int i = 0;
        for (int i2 = 0; i2 < this.mHandlerCheckers.size(); i2++) {
            i = Math.max(i, this.mHandlerCheckers.get(i2).checker().getCompletionStateLocked());
        }
        return i;
    }

    public final ArrayList<HandlerChecker> getCheckersWithStateLocked(int i) {
        ArrayList<HandlerChecker> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < this.mHandlerCheckers.size(); i2++) {
            HandlerChecker checker = this.mHandlerCheckers.get(i2).checker();
            if (checker.getCompletionStateLocked() == i) {
                arrayList.add(checker);
            }
        }
        return arrayList;
    }

    public final String describeCheckersLocked(List<HandlerChecker> list) {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < list.size(); i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i).describeBlockedStateLocked());
        }
        return sb.toString();
    }

    public static void addInterestingHidlPids(HashSet<Integer> hashSet) {
        try {
            Iterator<IServiceManager.InstanceDebugInfo> it = IServiceManager.getService().debugDump().iterator();
            while (it.hasNext()) {
                IServiceManager.InstanceDebugInfo next = it.next();
                if (next.pid != -1 && HAL_INTERFACES_OF_INTEREST.contains(next.interfaceName)) {
                    hashSet.add(Integer.valueOf(next.pid));
                }
            }
        } catch (RemoteException e) {
            Log.w("Watchdog", e);
        }
    }

    public static void addInterestingAidlPids(HashSet<Integer> hashSet) {
        ServiceDebugInfo[] serviceDebugInfo = ServiceManager.getServiceDebugInfo();
        if (serviceDebugInfo == null) {
            return;
        }
        for (ServiceDebugInfo serviceDebugInfo2 : serviceDebugInfo) {
            for (String str : AIDL_INTERFACE_PREFIXES_OF_INTEREST) {
                if (serviceDebugInfo2.name.startsWith(str)) {
                    hashSet.add(Integer.valueOf(serviceDebugInfo2.debugPid));
                }
            }
        }
    }

    public static ArrayList<Integer> getInterestingNativePids() {
        HashSet hashSet = new HashSet();
        addInterestingAidlPids(hashSet);
        addInterestingHidlPids(hashSet);
        int[] pidsForCommands = Process.getPidsForCommands(NATIVE_STACKS_OF_INTEREST);
        if (pidsForCommands != null) {
            for (int i : pidsForCommands) {
                hashSet.add(Integer.valueOf(i));
            }
        }
        return new ArrayList<>(hashSet);
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00b6, code lost:
        logWatchog(r4, r2, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b9, code lost:
        if (r4 == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00bb, code lost:
        r1 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00be, code lost:
        r3 = r13.mLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00c0, code lost:
        monitor-enter(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00c1, code lost:
        r4 = r13.mController;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00c3, code lost:
        monitor-exit(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00c4, code lost:
        if (r4 == null) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00c6, code lost:
        android.util.Slog.i("Watchdog", "Reporting stuck state to activity controller");
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00cd, code lost:
        android.os.Binder.setDumpDisabled("Service dumps disabled due to hung system process.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00d6, code lost:
        if (r4.systemNotResponding(r2) < 0) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00d8, code lost:
        android.util.Slog.i("Watchdog", "Activity controller requested to coninue to wait");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void run() {
        char c;
        ArrayList<Integer> arrayList;
        boolean z;
        boolean z2;
        ArrayList<HandlerChecker> arrayList2;
        String str;
        while (true) {
            boolean z3 = false;
            while (true) {
                Collections.emptyList();
                long j = this.mWatchdogTimeoutMillis;
                long j2 = j / 2;
                synchronized (this.mLock) {
                    for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
                        HandlerCheckerAndTimeout handlerCheckerAndTimeout = this.mHandlerCheckers.get(i);
                        handlerCheckerAndTimeout.checker().scheduleCheckLocked(handlerCheckerAndTimeout.customTimeoutMillis().orElse(Long.valueOf(Build.HW_TIMEOUT_MULTIPLIER * j)).longValue());
                    }
                    long uptimeMillis = SystemClock.uptimeMillis();
                    c = 0;
                    for (long j3 = j2; j3 > 0; j3 = j2 - (SystemClock.uptimeMillis() - uptimeMillis)) {
                        if (Debug.isDebuggerConnected()) {
                            c = 2;
                        }
                        try {
                            this.mLock.wait(j3);
                        } catch (InterruptedException e) {
                            Log.wtf("Watchdog", e);
                        }
                        if (Debug.isDebuggerConnected()) {
                            c = 2;
                        }
                    }
                    int evaluateCheckerCompletionLocked = evaluateCheckerCompletionLocked();
                    if (evaluateCheckerCompletionLocked != 0) {
                        boolean z4 = true;
                        if (evaluateCheckerCompletionLocked != 1) {
                            if (evaluateCheckerCompletionLocked != 2) {
                                ArrayList<HandlerChecker> checkersWithStateLocked = getCheckersWithStateLocked(3);
                                String describeCheckersLocked = describeCheckersLocked(checkersWithStateLocked);
                                boolean z5 = this.mAllowRestart;
                                arrayList = new ArrayList<>(this.mInterestingJavaPids);
                                z = z5;
                                z2 = false;
                                z4 = z3;
                                arrayList2 = checkersWithStateLocked;
                                str = describeCheckersLocked;
                            } else if (!z3) {
                                Slog.i("Watchdog", "WAITED_HALF");
                                arrayList2 = getCheckersWithStateLocked(2);
                                str = describeCheckersLocked(arrayList2);
                                z = true;
                                arrayList = new ArrayList<>(this.mInterestingJavaPids);
                                z2 = true;
                            }
                        }
                    }
                }
                break;
            }
        }
        if (Debug.isDebuggerConnected()) {
            c = 2;
        }
        if (c >= 2) {
            Slog.w("Watchdog", "Debugger connected: Watchdog is *not* killing the system process");
        } else if (c > 0) {
            Slog.w("Watchdog", "Debugger was connected: Watchdog is *not* killing the system process");
        } else if (!z) {
            Slog.w("Watchdog", "Restart not allowed: Watchdog is *not* killing the system process");
        } else {
            Slog.w("Watchdog", "*** WATCHDOG KILLING SYSTEM PROCESS: " + str);
            WatchdogDiagnostics.diagnoseCheckers(arrayList2);
            Slog.w("Watchdog", "*** GOODBYE!");
            if (!Build.IS_USER && isCrashLoopFound() && !WatchdogProperties.should_ignore_fatal_count().orElse(Boolean.FALSE).booleanValue()) {
                breakCrashLoop();
            }
            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }

    public final void logWatchog(boolean z, String str, ArrayList<Integer> arrayList) {
        String str2;
        String logLinesForSystemServerTraceFile = CriticalEventLog.getInstance().logLinesForSystemServerTraceFile();
        final UUID generateErrorId = this.mTraceErrorLogger.generateErrorId();
        if (this.mTraceErrorLogger.isAddErrorIdEnabled()) {
            this.mTraceErrorLogger.addErrorIdToTrace("system_server", generateErrorId);
            this.mTraceErrorLogger.addSubjectToTrace(str, generateErrorId);
        }
        if (z) {
            CriticalEventLog.getInstance().logHalfWatchdog(str);
            FrameworkStatsLog.write(FrameworkStatsLog.SYSTEM_SERVER_PRE_WATCHDOG_OCCURRED);
            str2 = "pre_watchdog";
        } else {
            CriticalEventLog.getInstance().logWatchdog(str, generateErrorId);
            EventLog.writeEvent(2802, str);
            FrameworkStatsLog.write(185, str);
            str2 = "watchdog";
        }
        final String str3 = str2;
        long uptimeMillis = SystemClock.uptimeMillis();
        final StringBuilder sb = new StringBuilder();
        sb.append(ResourcePressureUtil.currentPsiState());
        ProcessCpuTracker processCpuTracker = new ProcessCpuTracker(false);
        StringWriter stringWriter = new StringWriter();
        final File dumpStackTraces = ActivityManagerService.dumpStackTraces(arrayList, processCpuTracker, new SparseBooleanArray(), CompletableFuture.completedFuture(getInterestingNativePids()), stringWriter, str, logLinesForSystemServerTraceFile, new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), null);
        SystemClock.sleep(5000L);
        processCpuTracker.update();
        sb.append(processCpuTracker.printCurrentState(uptimeMillis));
        sb.append(stringWriter.getBuffer());
        if (!z) {
            doSysRq('w');
            doSysRq('l');
        }
        Thread thread = new Thread("watchdogWriteToDropbox") { // from class: com.android.server.Watchdog.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (Watchdog.this.mActivity != null) {
                    Watchdog.this.mActivity.addErrorToDropBox(str3, null, "system_server", null, null, null, null, sb.toString(), dumpStackTraces, null, null, null, generateErrorId);
                }
            }
        };
        thread.start();
        try {
            thread.join(2000L);
        } catch (InterruptedException unused) {
        }
    }

    public final void doSysRq(char c) {
        try {
            FileWriter fileWriter = new FileWriter("/proc/sysrq-trigger");
            fileWriter.write(c);
            fileWriter.close();
        } catch (IOException e) {
            Slog.w("Watchdog", "Failed to write to /proc/sysrq-trigger", e);
        }
    }

    public final void resetTimeoutHistory() {
        writeTimeoutHistory(new ArrayList());
    }

    public final void writeTimeoutHistory(Iterable<String> iterable) {
        String join = String.join(",", iterable);
        try {
            FileWriter fileWriter = new FileWriter("/data/system/watchdog-timeout-history.txt");
            fileWriter.write(SystemProperties.get("ro.boottime.zygote"));
            fileWriter.write(XmlUtils.STRING_ARRAY_SEPARATOR);
            fileWriter.write(join);
            fileWriter.close();
        } catch (IOException e) {
            Slog.e("Watchdog", "Failed to write file /data/system/watchdog-timeout-history.txt", e);
        }
    }

    public final String[] readTimeoutHistory() {
        String[] strArr = new String[0];
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/data/system/watchdog-timeout-history.txt"));
            try {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    String[] split = readLine.trim().split(XmlUtils.STRING_ARRAY_SEPARATOR);
                    String str = split.length >= 1 ? split[0] : "";
                    String str2 = split.length >= 2 ? split[1] : "";
                    if (!SystemProperties.get("ro.boottime.zygote").equals(str) || str2.isEmpty()) {
                        bufferedReader.close();
                        return strArr;
                    }
                    String[] split2 = str2.split(",");
                    bufferedReader.close();
                    return split2;
                }
                bufferedReader.close();
                return strArr;
            } catch (Throwable th) {
                try {
                    bufferedReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (FileNotFoundException unused) {
            return strArr;
        } catch (IOException e) {
            Slog.e("Watchdog", "Failed to read file /data/system/watchdog-timeout-history.txt", e);
            return strArr;
        }
    }

    public final boolean hasActiveUsbConnection() {
        try {
            return "CONFIGURED".equals(FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 128, null).trim());
        } catch (IOException e) {
            Slog.w("Watchdog", "Failed to determine if device was on USB", e);
            return false;
        }
    }

    public final boolean isCrashLoopFound() {
        int intValue = WatchdogProperties.fatal_count().orElse(0).intValue();
        long millis = TimeUnit.SECONDS.toMillis(WatchdogProperties.fatal_window_seconds().orElse(0).intValue());
        if (intValue == 0 || millis == 0) {
            if (intValue != millis) {
                Slog.w("Watchdog", String.format("sysprops '%s' and '%s' should be set or unset together", "framework_watchdog.fatal_count", "framework_watchdog.fatal_window.second"));
            }
            return false;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        String[] readTimeoutHistory = readTimeoutHistory();
        ArrayList arrayList = new ArrayList(Arrays.asList((String[]) Arrays.copyOfRange(readTimeoutHistory, Math.max(0, (readTimeoutHistory.length - intValue) - 1), readTimeoutHistory.length)));
        arrayList.add(String.valueOf(elapsedRealtime));
        writeTimeoutHistory(arrayList);
        if (hasActiveUsbConnection()) {
            return false;
        }
        try {
            return arrayList.size() >= intValue && elapsedRealtime - Long.parseLong((String) arrayList.get(0)) < millis;
        } catch (NumberFormatException e) {
            Slog.w("Watchdog", "Failed to parseLong " + ((String) arrayList.get(0)), e);
            resetTimeoutHistory();
            return false;
        }
    }

    public final void breakCrashLoop() {
        try {
            FileWriter fileWriter = new FileWriter("/dev/kmsg_debug", true);
            fileWriter.append((CharSequence) "Fatal reset to escape the system_server crashing loop\n");
            fileWriter.close();
        } catch (IOException e) {
            Slog.w("Watchdog", "Failed to append to kmsg", e);
        }
        doSysRq('c');
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        printWriter.print("WatchdogTimeoutMillis=");
        printWriter.println(this.mWatchdogTimeoutMillis);
    }
}
