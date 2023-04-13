package com.android.server;

import android.app.ActivityThread;
import android.app.AppCompatCallbacks;
import android.app.ApplicationErrorReport;
import android.app.ContextImpl;
import android.app.INotificationManager;
import android.app.SystemServiceRegistry;
import android.app.admin.DevicePolicySafetyChecker;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteCompatibilityWalFlags;
import android.database.sqlite.SQLiteGlobal;
import android.graphics.GraphicsStatsService;
import android.graphics.Typeface;
import android.hardware.display.DisplayManagerInternal;
import android.location.ICountryDetector;
import android.net.ConnectivityManager;
import android.net.ConnectivityModuleConnector;
import android.net.NetworkStackClient;
import android.os.ArtModuleServiceManager;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.FactoryTest;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.IIncidentManager;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.IStorageManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Dumpable;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.Pair;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.policy.AttributeCache;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.LockSettingsInternal;
import com.android.server.BinderCallsStatsService;
import com.android.server.LooperStatsService;
import com.android.server.NetworkScoreService;
import com.android.server.TelephonyRegistry;
import com.android.server.ambientcontext.AmbientContextManagerService;
import com.android.server.appbinding.AppBindingService;
import com.android.server.art.ArtModuleServiceInitializer;
import com.android.server.art.DexUseManagerLocal;
import com.android.server.attention.AttentionManagerService;
import com.android.server.audio.AudioService;
import com.android.server.biometrics.AuthService;
import com.android.server.biometrics.BiometricService;
import com.android.server.biometrics.sensors.face.FaceService;
import com.android.server.biometrics.sensors.fingerprint.FingerprintService;
import com.android.server.biometrics.sensors.iris.IrisService;
import com.android.server.broadcastradio.BroadcastRadioService;
import com.android.server.camera.CameraServiceProxy;
import com.android.server.clipboard.ClipboardService;
import com.android.server.compat.PlatformCompat;
import com.android.server.compat.PlatformCompatNative;
import com.android.server.connectivity.IpConnectivityMetrics;
import com.android.server.connectivity.PacProxyService;
import com.android.server.contentcapture.ContentCaptureManagerInternal;
import com.android.server.coverage.CoverageService;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.devicestate.DeviceStateManagerService;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.dreams.DreamManagerService;
import com.android.server.emergency.EmergencyAffordanceService;
import com.android.server.gpu.GpuService;
import com.android.server.grammaticalinflection.GrammaticalInflectionService;
import com.android.server.graphics.fonts.FontManagerService;
import com.android.server.hdmi.HdmiControlService;
import com.android.server.incident.IncidentCompanionService;
import com.android.server.input.InputManagerService;
import com.android.server.inputmethod.InputMethodManagerService;
import com.android.server.integrity.AppIntegrityManagerService;
import com.android.server.lights.LightsService;
import com.android.server.locales.LocaleManagerService;
import com.android.server.location.LocationManagerService;
import com.android.server.location.altitude.AltitudeService;
import com.android.server.logcat.LogcatManagerService;
import com.android.server.media.MediaRouterService;
import com.android.server.media.metrics.MediaMetricsManagerService;
import com.android.server.media.projection.MediaProjectionManagerService;
import com.android.server.net.NetworkManagementService;
import com.android.server.net.NetworkPolicyManagerService;
import com.android.server.net.watchlist.NetworkWatchlistService;
import com.android.server.notification.NotificationManagerService;
import com.android.server.oemlock.OemLockService;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p008om.OverlayManagerService;
import com.android.server.p009os.BugreportManagerService;
import com.android.server.p009os.DeviceIdentifiersPolicyService;
import com.android.server.p009os.NativeTombstoneManagerService;
import com.android.server.p009os.SchedulingPolicyService;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.ApexSystemServiceInfo;
import com.android.server.p011pm.BackgroundInstallControlService;
import com.android.server.p011pm.CrossProfileAppsService;
import com.android.server.p011pm.DataLoaderManagerService;
import com.android.server.p011pm.DexOptHelper;
import com.android.server.p011pm.DynamicCodeLoggingService;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.LauncherAppsService;
import com.android.server.p011pm.OtaDexoptService;
import com.android.server.p011pm.PackageManagerService;
import com.android.server.p011pm.ShortcutService;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.dex.OdsignStatsLogger;
import com.android.server.p011pm.verify.domain.DomainVerificationService;
import com.android.server.p012tv.TvInputManagerService;
import com.android.server.p012tv.TvRemoteService;
import com.android.server.p012tv.interactive.TvInteractiveAppManagerService;
import com.android.server.p012tv.tunerresourcemanager.TunerResourceManagerService;
import com.android.server.p013vr.VrManagerService;
import com.android.server.p014wm.ActivityTaskManagerService;
import com.android.server.p014wm.WindowManagerGlobalLock;
import com.android.server.p014wm.WindowManagerService;
import com.android.server.people.PeopleService;
import com.android.server.permission.access.AccessCheckingService;
import com.android.server.policy.AppOpsPolicy;
import com.android.server.policy.PermissionPolicyService;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.policy.role.RoleServicePlatformHelperImpl;
import com.android.server.power.PowerManagerService;
import com.android.server.power.ShutdownThread;
import com.android.server.power.ThermalManagerService;
import com.android.server.power.hint.HintManagerService;
import com.android.server.powerstats.PowerStatsService;
import com.android.server.profcollect.ProfcollectForwardingService;
import com.android.server.recoverysystem.RecoverySystemService;
import com.android.server.resources.ResourcesManagerService;
import com.android.server.restrictions.RestrictionsManagerService;
import com.android.server.role.RoleServicePlatformHelper;
import com.android.server.rotationresolver.RotationResolverManagerService;
import com.android.server.security.AttestationVerificationManagerService;
import com.android.server.security.FileIntegrityService;
import com.android.server.security.KeyAttestationApplicationIdProviderService;
import com.android.server.security.KeyChainSystemService;
import com.android.server.security.rkp.RemoteProvisioningService;
import com.android.server.sensorprivacy.SensorPrivacyService;
import com.android.server.sensors.SensorService;
import com.android.server.signedconfig.SignedConfigService;
import com.android.server.soundtrigger.SoundTriggerService;
import com.android.server.soundtrigger_middleware.SoundTriggerMiddlewareService;
import com.android.server.statusbar.StatusBarManagerService;
import com.android.server.storage.DeviceStorageMonitorService;
import com.android.server.telecom.TelecomLoaderService;
import com.android.server.testharness.TestHarnessModeService;
import com.android.server.textclassifier.TextClassificationManagerService;
import com.android.server.textservices.TextServicesManagerService;
import com.android.server.timedetector.NetworkTimeUpdateService;
import com.android.server.tracing.TracingServiceProxy;
import com.android.server.trust.TrustManagerService;
import com.android.server.twilight.TwilightService;
import com.android.server.uri.UriGrantsManagerService;
import com.android.server.usage.UsageStatsService;
import com.android.server.utils.TimingsTraceAndSlog;
import com.android.server.vibrator.VibratorManagerService;
import com.android.server.wearable.WearableSensingManagerService;
import com.android.server.webkit.WebViewUpdateService;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
/* loaded from: classes.dex */
public final class SystemServer implements Dumpable {
    public static final File HEAP_DUMP_PATH = new File("/data/system/heapdump/");
    public static LinkedList<Pair<String, ApplicationErrorReport.CrashInfo>> sPendingWtfs;
    public ActivityManagerService mActivityManagerService;
    public ContentResolver mContentResolver;
    public DataLoaderManagerService mDataLoaderManagerService;
    public DisplayManagerService mDisplayManagerService;
    public EntropyMixer mEntropyMixer;
    public boolean mFirstBoot;
    public PackageManager mPackageManager;
    public PackageManagerService mPackageManagerService;
    public PowerManagerService mPowerManagerService;
    public final boolean mRuntimeRestart;
    public final long mRuntimeStartElapsedTime;
    public final long mRuntimeStartUptime;
    public final int mStartCount;
    public Context mSystemContext;
    public SystemServiceManager mSystemServiceManager;
    public WebViewUpdateService mWebViewUpdateService;
    public WindowManagerGlobalLock mWindowManagerGlobalLock;
    public Future<?> mZygotePreload;
    public long mIncrementalServiceHandle = 0;
    public final SystemServerDumper mDumper = new SystemServerDumper();
    public final int mFactoryTestMode = FactoryTest.getMode();

    private static native void fdtrackAbort();

    private static native void initZygoteChildHeapProfiling();

    private static native void setIncrementalServiceSystemReady(long j);

    private static native void startHidlServices();

    private static native void startISensorManagerService();

    private static native void startIStatsService();

    private static native long startIncrementalService();

    private static native void startMemtrackProxyService();

    public static int getMaxFd() {
        FileDescriptor fileDescriptor = null;
        try {
            try {
                fileDescriptor = Os.open("/dev/null", OsConstants.O_RDONLY | OsConstants.O_CLOEXEC, 0);
                int int$ = fileDescriptor.getInt$();
                try {
                    Os.close(fileDescriptor);
                    return int$;
                } catch (ErrnoException e) {
                    throw new RuntimeException(e);
                }
            } catch (ErrnoException e2) {
                Slog.e("System", "Failed to get maximum fd: " + e2);
                if (fileDescriptor != null) {
                    try {
                        Os.close(fileDescriptor);
                        return Integer.MAX_VALUE;
                    } catch (ErrnoException e3) {
                        throw new RuntimeException(e3);
                    }
                }
                return Integer.MAX_VALUE;
            }
        } catch (Throwable th) {
            if (fileDescriptor != null) {
                try {
                    Os.close(fileDescriptor);
                } catch (ErrnoException e4) {
                    throw new RuntimeException(e4);
                }
            }
            throw th;
        }
    }

    public static void dumpHprof() {
        File[] listFiles;
        TreeSet treeSet = new TreeSet();
        for (File file : HEAP_DUMP_PATH.listFiles()) {
            if (file.isFile() && file.getName().startsWith("fdtrack-")) {
                treeSet.add(file);
            }
        }
        if (treeSet.size() >= 2) {
            treeSet.pollLast();
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                File file2 = (File) it.next();
                if (!file2.delete()) {
                    Slog.w("System", "Failed to clean up hprof " + file2);
                }
            }
        }
        try {
            Debug.dumpHprofData("/data/system/heapdump/fdtrack-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".hprof");
        } catch (IOException e) {
            Slog.e("System", "Failed to dump fdtrack hprof", e);
        }
    }

    public static void spawnFdLeakCheckThread() {
        final int i = SystemProperties.getInt("persist.sys.debug.fdtrack_enable_threshold", 1024);
        final int i2 = SystemProperties.getInt("persist.sys.debug.fdtrack_abort_threshold", (int) IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
        final int i3 = SystemProperties.getInt("persist.sys.debug.fdtrack_interval", 120);
        new Thread(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SystemServer.lambda$spawnFdLeakCheckThread$0(i, i2, i3);
            }
        }).start();
    }

    public static /* synthetic */ void lambda$spawnFdLeakCheckThread$0(int i, int i2, int i3) {
        boolean z = false;
        long j = 0;
        while (true) {
            int maxFd = getMaxFd();
            if (maxFd > i) {
                System.gc();
                System.runFinalization();
                maxFd = getMaxFd();
            }
            if (maxFd > i && !z) {
                Slog.i("System", "fdtrack enable threshold reached, enabling");
                FrameworkStatsLog.write(364, 2, maxFd);
                System.loadLibrary("fdtrack");
                z = true;
            } else if (maxFd > i2) {
                Slog.i("System", "fdtrack abort threshold reached, dumping and aborting");
                FrameworkStatsLog.write(364, 3, maxFd);
                dumpHprof();
                fdtrackAbort();
            } else {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (elapsedRealtime > j) {
                    long j2 = elapsedRealtime + ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
                    FrameworkStatsLog.write(364, z ? 2 : 1, maxFd);
                    j = j2;
                }
            }
            try {
                Thread.sleep(i3 * 1000);
            } catch (InterruptedException unused) {
            }
        }
    }

    public static void main(String[] strArr) {
        new SystemServer().run();
    }

    public SystemServer() {
        int i = SystemProperties.getInt("sys.system_server.start_count", 0) + 1;
        this.mStartCount = i;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mRuntimeStartElapsedTime = elapsedRealtime;
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mRuntimeStartUptime = uptimeMillis;
        Process.setStartTimes(elapsedRealtime, uptimeMillis, elapsedRealtime, uptimeMillis);
        this.mRuntimeRestart = i > 1;
    }

    public String getDumpableName() {
        return SystemServer.class.getSimpleName();
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        printWriter.printf("Runtime restart: %b\n", Boolean.valueOf(this.mRuntimeRestart));
        printWriter.printf("Start count: %d\n", Integer.valueOf(this.mStartCount));
        printWriter.print("Runtime start-up time: ");
        TimeUtils.formatDuration(this.mRuntimeStartUptime, printWriter);
        printWriter.println();
        printWriter.print("Runtime start-elapsed time: ");
        TimeUtils.formatDuration(this.mRuntimeStartElapsedTime, printWriter);
        printWriter.println();
    }

    /* loaded from: classes.dex */
    public final class SystemServerDumper extends Binder {
        @GuardedBy({"mDumpables"})
        public final ArrayMap<String, Dumpable> mDumpables;

        public SystemServerDumper() {
            this.mDumpables = new ArrayMap<>(4);
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            boolean z = strArr != null && strArr.length > 0;
            synchronized (this.mDumpables) {
                if (z) {
                    try {
                        if ("--list".equals(strArr[0])) {
                            int size = this.mDumpables.size();
                            for (int i = 0; i < size; i++) {
                                printWriter.println(this.mDumpables.keyAt(i));
                            }
                            return;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                if (z && "--name".equals(strArr[0])) {
                    if (strArr.length < 2) {
                        printWriter.println("Must pass at least one argument to --name");
                        return;
                    }
                    String str = strArr[1];
                    Dumpable dumpable = this.mDumpables.get(str);
                    if (dumpable == null) {
                        printWriter.printf("No dummpable named %s\n", str);
                        return;
                    }
                    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
                    dumpable.dump(indentingPrintWriter, (String[]) Arrays.copyOfRange(strArr, 2, strArr.length));
                    indentingPrintWriter.close();
                    return;
                }
                int size2 = this.mDumpables.size();
                IndentingPrintWriter indentingPrintWriter2 = new IndentingPrintWriter(printWriter, "  ");
                for (int i2 = 0; i2 < size2; i2++) {
                    Dumpable valueAt = this.mDumpables.valueAt(i2);
                    indentingPrintWriter2.printf("%s:\n", new Object[]{valueAt.getDumpableName()});
                    indentingPrintWriter2.increaseIndent();
                    valueAt.dump(indentingPrintWriter2, strArr);
                    indentingPrintWriter2.decreaseIndent();
                    indentingPrintWriter2.println();
                }
                indentingPrintWriter2.close();
            }
        }

        public final void addDumpable(Dumpable dumpable) {
            synchronized (this.mDumpables) {
                this.mDumpables.put(dumpable.getDumpableName(), dumpable);
            }
        }
    }

    public final void run() {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        try {
            timingsTraceAndSlog.traceBegin("InitBeforeStartServices");
            SystemProperties.set("sys.system_server.start_count", String.valueOf(this.mStartCount));
            SystemProperties.set("sys.system_server.start_elapsed", String.valueOf(this.mRuntimeStartElapsedTime));
            SystemProperties.set("sys.system_server.start_uptime", String.valueOf(this.mRuntimeStartUptime));
            EventLog.writeEvent(3011, Integer.valueOf(this.mStartCount), Long.valueOf(this.mRuntimeStartUptime), Long.valueOf(this.mRuntimeStartElapsedTime));
            SystemTimeZone.initializeTimeZoneSettingsIfRequired();
            if (!SystemProperties.get("persist.sys.language").isEmpty()) {
                SystemProperties.set("persist.sys.locale", Locale.getDefault().toLanguageTag());
                SystemProperties.set("persist.sys.language", "");
                SystemProperties.set("persist.sys.country", "");
                SystemProperties.set("persist.sys.localevar", "");
            }
            Binder.setWarnOnBlocking(true);
            PackageItemInfo.forceSafeLabels();
            SQLiteGlobal.sDefaultSyncMode = "FULL";
            SQLiteCompatibilityWalFlags.init((String) null);
            Slog.i("SystemServer", "Entered the Android system server!");
            long elapsedRealtime = SystemClock.elapsedRealtime();
            EventLog.writeEvent(3010, elapsedRealtime);
            if (!this.mRuntimeRestart) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 19, elapsedRealtime);
            }
            SystemProperties.set("persist.sys.dalvik.vm.lib.2", VMRuntime.getRuntime().vmLibrary());
            VMRuntime.getRuntime().clearGrowthLimit();
            Build.ensureFingerprintProperty();
            Environment.setUserRequired(true);
            BaseBundle.setShouldDefuse(true);
            Parcel.setStackTraceParceling(true);
            BinderInternal.disableBackgroundScheduling(true);
            BinderInternal.setMaxThreads(31);
            Process.setThreadPriority(-2);
            Process.setCanSelfBackground(false);
            Looper.prepareMainLooper();
            Looper.getMainLooper().setSlowLogThresholdMs(100L, 200L);
            SystemServiceRegistry.sEnableServiceNotFoundWtf = true;
            System.loadLibrary("android_servers");
            initZygoteChildHeapProfiling();
            if (Build.IS_DEBUGGABLE) {
                spawnFdLeakCheckThread();
            }
            performPendingShutdown();
            createSystemContext();
            ActivityThread.initializeMainlineModules();
            ServiceManager.addService("system_server_dumper", this.mDumper);
            this.mDumper.addDumpable(this);
            SystemServiceManager systemServiceManager = new SystemServiceManager(this.mSystemContext);
            this.mSystemServiceManager = systemServiceManager;
            systemServiceManager.setStartInfo(this.mRuntimeRestart, this.mRuntimeStartElapsedTime, this.mRuntimeStartUptime);
            this.mDumper.addDumpable(this.mSystemServiceManager);
            LocalServices.addService(SystemServiceManager.class, this.mSystemServiceManager);
            this.mDumper.addDumpable(SystemServerInitThreadPool.start());
            Typeface.loadPreinstalledSystemFontMap();
            if (Build.IS_DEBUGGABLE) {
                String str = SystemProperties.get("persist.sys.dalvik.jvmtiagent");
                if (!str.isEmpty()) {
                    int indexOf = str.indexOf(61);
                    try {
                        Debug.attachJvmtiAgent(str.substring(0, indexOf), str.substring(indexOf + 1, str.length()), null);
                    } catch (Exception unused) {
                        Slog.e("System", "*************************************************");
                        Slog.e("System", "********** Failed to load jvmti plugin: " + str);
                    }
                }
            }
            timingsTraceAndSlog.traceEnd();
            RuntimeInit.setDefaultApplicationWtfHandler(new RuntimeInit.ApplicationWtfHandler() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda0
                public final boolean handleApplicationWtf(IBinder iBinder, String str2, boolean z, ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo, int i) {
                    boolean handleEarlySystemWtf;
                    handleEarlySystemWtf = SystemServer.handleEarlySystemWtf(iBinder, str2, z, parcelableCrashInfo, i);
                    return handleEarlySystemWtf;
                }
            });
            try {
                timingsTraceAndSlog.traceBegin("StartServices");
                startBootstrapServices(timingsTraceAndSlog);
                startCoreServices(timingsTraceAndSlog);
                startOtherServices(timingsTraceAndSlog);
                startApexServices(timingsTraceAndSlog);
                updateWatchdogTimeout(timingsTraceAndSlog);
                timingsTraceAndSlog.traceEnd();
                StrictMode.initVmDefaults(null);
                if (!this.mRuntimeRestart && !isFirstBootOrUpgrade()) {
                    long elapsedRealtime2 = SystemClock.elapsedRealtime();
                    FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 20, elapsedRealtime2);
                    if (elapsedRealtime2 > 60000) {
                        Slog.wtf("SystemServerTiming", "SystemServer init took too long. uptimeMillis=" + elapsedRealtime2);
                    }
                }
                Looper.loop();
                throw new RuntimeException("Main thread loop unexpectedly exited");
            } finally {
            }
        } finally {
        }
    }

    public final boolean isFirstBootOrUpgrade() {
        return this.mPackageManagerService.isFirstBoot() || this.mPackageManagerService.isDeviceUpgrading();
    }

    public final void reportWtf(String str, Throwable th) {
        Slog.w("SystemServer", "***********************************************");
        Slog.wtf("SystemServer", "BOOT FAILURE " + str, th);
    }

    public final void performPendingShutdown() {
        String str = SystemProperties.get("sys.shutdown.requested", "");
        if (str == null || str.length() <= 0) {
            return;
        }
        final boolean z = str.charAt(0) == '1';
        String str2 = null;
        final String substring = str.length() > 1 ? str.substring(1, str.length()) : null;
        if (substring != null && substring.startsWith("recovery-update")) {
            File file = new File("/cache/recovery/uncrypt_file");
            if (file.exists()) {
                try {
                    str2 = FileUtils.readTextFile(file, 0, null);
                } catch (IOException e) {
                    Slog.e("SystemServer", "Error reading uncrypt package file", e);
                }
                if (str2 != null && str2.startsWith("/data") && !new File("/cache/recovery/block.map").exists()) {
                    Slog.e("SystemServer", "Can't find block map file, uncrypt failed or unexpected runtime restart?");
                    return;
                }
            }
        }
        Message obtain = Message.obtain(UiThread.getHandler(), new Runnable() { // from class: com.android.server.SystemServer.1
            @Override // java.lang.Runnable
            public void run() {
                ShutdownThread.rebootOrShutdown(null, z, substring);
            }
        });
        obtain.setAsynchronous(true);
        UiThread.getHandler().sendMessage(obtain);
    }

    public final void createSystemContext() {
        ActivityThread systemMain = ActivityThread.systemMain();
        ContextImpl systemContext = systemMain.getSystemContext();
        this.mSystemContext = systemContext;
        systemContext.setTheme(16974843);
        systemMain.getSystemUiContext().setTheme(16974843);
    }

    /* JADX WARN: Type inference failed for: r3v4, types: [com.android.server.compat.PlatformCompat, android.os.IBinder] */
    public final void startBootstrapServices(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("startBootstrapServices");
        timingsTraceAndSlog.traceBegin("ArtModuleServiceInitializer");
        ArtModuleServiceInitializer.setArtModuleServiceManager(new ArtModuleServiceManager());
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartWatchdog");
        Watchdog watchdog = Watchdog.getInstance();
        watchdog.start();
        this.mDumper.addDumpable(watchdog);
        timingsTraceAndSlog.traceEnd();
        Slog.i("SystemServer", "Reading configuration...");
        timingsTraceAndSlog.traceBegin("ReadingSystemConfig");
        SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SystemConfig.getInstance();
            }
        }, "ReadingSystemConfig");
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("PlatformCompat");
        ?? platformCompat = new PlatformCompat(this.mSystemContext);
        ServiceManager.addService("platform_compat", (IBinder) platformCompat);
        ServiceManager.addService("platform_compat_native", new PlatformCompatNative(platformCompat));
        AppCompatCallbacks.install(new long[0]);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartFileIntegrityService");
        this.mSystemServiceManager.startService(FileIntegrityService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartInstaller");
        Installer installer = (Installer) this.mSystemServiceManager.startService(Installer.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("DeviceIdentifiersPolicyService");
        this.mSystemServiceManager.startService(DeviceIdentifiersPolicyService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("UriGrantsManagerService");
        this.mSystemServiceManager.startService(UriGrantsManagerService.Lifecycle.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartPowerStatsService");
        this.mSystemServiceManager.startService(PowerStatsService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartIStatsService");
        startIStatsService();
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MemtrackProxyService");
        startMemtrackProxyService();
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartAccessCheckingService");
        this.mSystemServiceManager.startService(AccessCheckingService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartActivityManager");
        ActivityTaskManagerService service = ((ActivityTaskManagerService.Lifecycle) this.mSystemServiceManager.startService(ActivityTaskManagerService.Lifecycle.class)).getService();
        ActivityManagerService startService = ActivityManagerService.Lifecycle.startService(this.mSystemServiceManager, service);
        this.mActivityManagerService = startService;
        startService.setSystemServiceManager(this.mSystemServiceManager);
        this.mActivityManagerService.setInstaller(installer);
        this.mWindowManagerGlobalLock = service.getGlobalLock();
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartDataLoaderManagerService");
        this.mDataLoaderManagerService = (DataLoaderManagerService) this.mSystemServiceManager.startService(DataLoaderManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartIncrementalService");
        this.mIncrementalServiceHandle = startIncrementalService();
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartPowerManager");
        this.mPowerManagerService = (PowerManagerService) this.mSystemServiceManager.startService(PowerManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartThermalManager");
        this.mSystemServiceManager.startService(ThermalManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartHintManager");
        this.mSystemServiceManager.startService(HintManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("InitPowerManagement");
        this.mActivityManagerService.initPowerManagement();
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartRecoverySystemService");
        this.mSystemServiceManager.startService(RecoverySystemService.Lifecycle.class);
        timingsTraceAndSlog.traceEnd();
        RescueParty.registerHealthObserver(this.mSystemContext);
        PackageWatchdog.getInstance(this.mSystemContext).noteBoot();
        timingsTraceAndSlog.traceBegin("StartLightsService");
        this.mSystemServiceManager.startService(LightsService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartDisplayOffloadService");
        if (SystemProperties.getBoolean("config.enable_display_offload", false)) {
            this.mSystemServiceManager.startService("com.android.clockwork.displayoffload.DisplayOffloadService");
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartSidekickService");
        if (SystemProperties.getBoolean("config.enable_sidekick_graphics", false)) {
            this.mSystemServiceManager.startService("com.google.android.clockwork.sidekick.SidekickService");
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartDisplayManager");
        this.mDisplayManagerService = (DisplayManagerService) this.mSystemServiceManager.startService(DisplayManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("WaitForDisplay");
        this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 100);
        timingsTraceAndSlog.traceEnd();
        if (!this.mRuntimeRestart) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 14, SystemClock.elapsedRealtime());
        }
        timingsTraceAndSlog.traceBegin("StartDomainVerificationService");
        DomainVerificationService domainVerificationService = new DomainVerificationService(this.mSystemContext, SystemConfig.getInstance(), platformCompat);
        this.mSystemServiceManager.startService(domainVerificationService);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartPackageManagerService");
        try {
            Watchdog.getInstance().pauseWatchingCurrentThread("packagemanagermain");
            this.mPackageManagerService = PackageManagerService.main(this.mSystemContext, installer, domainVerificationService, this.mFactoryTestMode != 0);
            Watchdog.getInstance().resumeWatchingCurrentThread("packagemanagermain");
            this.mFirstBoot = this.mPackageManagerService.isFirstBoot();
            this.mPackageManager = this.mSystemContext.getPackageManager();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("DexUseManagerLocal");
            LocalManagerRegistry.addManager(DexUseManagerLocal.class, DexUseManagerLocal.createInstance(this.mSystemContext));
            timingsTraceAndSlog.traceEnd();
            if (!this.mRuntimeRestart && !isFirstBootOrUpgrade()) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 15, SystemClock.elapsedRealtime());
            }
            if (!SystemProperties.getBoolean("config.disable_otadexopt", false)) {
                timingsTraceAndSlog.traceBegin("StartOtaDexOptService");
                try {
                    Watchdog.getInstance().pauseWatchingCurrentThread("moveab");
                    OtaDexoptService.main(this.mSystemContext, this.mPackageManagerService);
                } finally {
                    try {
                    } finally {
                    }
                }
            }
            timingsTraceAndSlog.traceBegin("StartUserManagerService");
            this.mSystemServiceManager.startService(UserManagerService.LifeCycle.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("InitAttributerCache");
            AttributeCache.init(this.mSystemContext);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("SetSystemProcess");
            this.mActivityManagerService.setSystemProcess();
            timingsTraceAndSlog.traceEnd();
            platformCompat.registerPackageReceiver(this.mSystemContext);
            timingsTraceAndSlog.traceBegin("InitWatchdog");
            watchdog.init(this.mSystemContext, this.mActivityManagerService);
            timingsTraceAndSlog.traceEnd();
            this.mDisplayManagerService.setupSchedulerPolicies();
            timingsTraceAndSlog.traceBegin("StartOverlayManagerService");
            this.mSystemServiceManager.startService(new OverlayManagerService(this.mSystemContext));
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartResourcesManagerService");
            ResourcesManagerService resourcesManagerService = new ResourcesManagerService(this.mSystemContext);
            resourcesManagerService.setActivityManagerService(this.mActivityManagerService);
            this.mSystemServiceManager.startService(resourcesManagerService);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartSensorPrivacyService");
            this.mSystemServiceManager.startService(new SensorPrivacyService(this.mSystemContext));
            timingsTraceAndSlog.traceEnd();
            if (SystemProperties.getInt("persist.sys.displayinset.top", 0) > 0) {
                this.mActivityManagerService.updateSystemUiContext();
                ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).onOverlayChanged();
            }
            timingsTraceAndSlog.traceBegin("StartSensorService");
            this.mSystemServiceManager.startService(SensorService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceEnd();
        } catch (Throwable th) {
            Watchdog.getInstance().resumeWatchingCurrentThread("packagemanagermain");
            throw th;
        }
    }

    public final void startCoreServices(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("startCoreServices");
        timingsTraceAndSlog.traceBegin("StartSystemConfigService");
        this.mSystemServiceManager.startService(SystemConfigService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartBatteryService");
        this.mSystemServiceManager.startService(BatteryService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartUsageService");
        this.mSystemServiceManager.startService(UsageStatsService.class);
        this.mActivityManagerService.setUsageStatsManager((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class));
        timingsTraceAndSlog.traceEnd();
        if (this.mPackageManager.hasSystemFeature("android.software.webview")) {
            timingsTraceAndSlog.traceBegin("StartWebViewUpdateService");
            this.mWebViewUpdateService = (WebViewUpdateService) this.mSystemServiceManager.startService(WebViewUpdateService.class);
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceBegin("StartCachedDeviceStateService");
        this.mSystemServiceManager.startService(CachedDeviceStateService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartBinderCallsStatsService");
        this.mSystemServiceManager.startService(BinderCallsStatsService.LifeCycle.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartLooperStatsService");
        this.mSystemServiceManager.startService(LooperStatsService.Lifecycle.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartRollbackManagerService");
        this.mSystemServiceManager.startService("com.android.server.rollback.RollbackManagerService");
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartNativeTombstoneManagerService");
        this.mSystemServiceManager.startService(NativeTombstoneManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartBugreportManagerService");
        this.mSystemServiceManager.startService(BugreportManagerService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("GpuService");
        this.mSystemServiceManager.startService(GpuService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartRemoteProvisioningService");
        this.mSystemServiceManager.startService(RemoteProvisioningService.class);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceEnd();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(181:561|562|216|(0)(0)|219|(0)(0)|222|(0)(0)|225|226|227|228|229|230|231|232|233|(2:234|235)|236|(0)|239|240|241|242|243|244|(0)|247|(0)|250|(0)|253|(0)|256|(0)|259|260|261|262|263|264|265|266|267|268|269|270|271|272|273|274|275|276|277|278|279|280|281|282|283|284|(2:285|286)|287|288|289|290|(2:291|292)|293|294|295|296|297|298|299|(0)|(0)|316|(0)(0)|319|(0)(0)|322|(0)|325|(0)|(0)|336|(0)|339|340|341|342|(0)|479|(0)|355|356|357|358|(0)|361|(0)|364|(0)|475|368|(0)|371|(0)|374|375|376|377|378|379|380|(0)|468|389|390|391|392|(0)|395|(0)|398|(0)|401|(0)|404|(0)|458|408|(0)|457|412|(0)|415|(0)|418|(0)|421|422|423|424|425|426|(0)|(0)|(0)|433|(0)|(0)|450|92|(0)|95|(0)|98|(0)|101|(0)|104|(0)(0)|107|(0)|110|(0)|116|(0)(0)|119|(0)|121|(0)|124|125|126|127|128|129|130|10d1) */
    /* JADX WARN: Can't wrap try/catch for region: R(182:561|562|216|(0)(0)|219|(0)(0)|222|(0)(0)|225|226|227|228|229|230|231|232|233|234|235|236|(0)|239|240|241|242|243|244|(0)|247|(0)|250|(0)|253|(0)|256|(0)|259|260|261|262|263|264|265|266|267|268|269|270|271|272|273|274|275|276|277|278|279|280|281|282|283|284|(2:285|286)|287|288|289|290|(2:291|292)|293|294|295|296|297|298|299|(0)|(0)|316|(0)(0)|319|(0)(0)|322|(0)|325|(0)|(0)|336|(0)|339|340|341|342|(0)|479|(0)|355|356|357|358|(0)|361|(0)|364|(0)|475|368|(0)|371|(0)|374|375|376|377|378|379|380|(0)|468|389|390|391|392|(0)|395|(0)|398|(0)|401|(0)|404|(0)|458|408|(0)|457|412|(0)|415|(0)|418|(0)|421|422|423|424|425|426|(0)|(0)|(0)|433|(0)|(0)|450|92|(0)|95|(0)|98|(0)|101|(0)|104|(0)(0)|107|(0)|110|(0)|116|(0)(0)|119|(0)|121|(0)|124|125|126|127|128|129|130|10d1) */
    /* JADX WARN: Can't wrap try/catch for region: R(80:8|9|(1:598)|15|(1:17)|18|(1:20)|21|(1:23)(1:597)|24|(1:27)|28|(1:30)(2:593|(1:595)(1:596))|31|(1:35)|36|37|(1:39)(2:589|(1:591))|40|(6:42|(1:44)(2:52|53)|45|46|47|48)|57|(2:58|59)|60|(7:64|65|66|67|68|69|70)|77|(2:78|79)|80|(2:81|82)|83|(2:84|85)|86|(2:87|88)|89|(1:91)(165:195|196|197|198|199|(1:201)|202|(1:565)|206|207|208|(1:210)|212|213|214|215|216|(1:218)(1:559)|219|(1:221)(1:558)|222|(1:224)(1:557)|225|226|227|228|229|230|231|232|233|234|235|236|(1:238)|239|240|241|242|243|244|(1:246)|247|(1:249)|250|(1:252)|253|(1:255)|256|(1:258)|259|260|261|262|263|264|265|266|267|268|269|270|271|272|273|274|275|276|277|278|279|280|281|282|283|284|285|286|287|288|289|290|291|292|293|294|295|296|297|298|299|(4:301|302|303|304)|(4:309|310|311|312)|316|(1:318)(1:494)|319|(1:321)(6:482|483|484|485|486|487)|322|(1:324)|325|(1:327)|(4:329|330|331|332)|336|(1:338)|339|340|341|342|(1:479)|(4:348|349|350|351)|355|356|357|358|(1:360)|361|(1:363)|364|(1:475)|368|(1:370)|371|(1:373)|374|375|376|377|378|379|380|(1:468)(6:383|384|385|386|387|388)|389|390|391|392|(1:394)|395|(1:397)|398|(1:400)|401|(1:403)|404|(1:458)|408|(1:457)|412|(1:414)|415|(1:417)|418|(1:420)|421|422|423|424|425|426|(1:428)|(1:430)|(1:432)|433|(4:435|436|437|438)|(4:443|444|445|446)|450)|92|(1:94)|95|(1:97)|98|(1:100)|101|(1:103)|104|(1:106)(1:194)|107|(1:109)|110|(2:112|(1:114)(1:115))|116|(1:118)(1:193)|119|(2:188|189)|121|(1:123)|124|125|126|127|128|129|130|10d1|(1:138)|139|(1:141)|142|(2:143|144)|145|(6:147|148|149|150|151|152)|157|158|(1:160)|161|(1:163)|164|(1:166)|167|(2:168|169)|170|171) */
    /* JADX WARN: Can't wrap try/catch for region: R(82:8|9|(1:598)|15|(1:17)|18|(1:20)|21|(1:23)(1:597)|24|(1:27)|28|(1:30)(2:593|(1:595)(1:596))|31|(1:35)|36|37|(1:39)(2:589|(1:591))|40|(6:42|(1:44)(2:52|53)|45|46|47|48)|57|(2:58|59)|60|(7:64|65|66|67|68|69|70)|77|78|79|80|(2:81|82)|83|(2:84|85)|86|(2:87|88)|89|(1:91)(165:195|196|197|198|199|(1:201)|202|(1:565)|206|207|208|(1:210)|212|213|214|215|216|(1:218)(1:559)|219|(1:221)(1:558)|222|(1:224)(1:557)|225|226|227|228|229|230|231|232|233|234|235|236|(1:238)|239|240|241|242|243|244|(1:246)|247|(1:249)|250|(1:252)|253|(1:255)|256|(1:258)|259|260|261|262|263|264|265|266|267|268|269|270|271|272|273|274|275|276|277|278|279|280|281|282|283|284|285|286|287|288|289|290|291|292|293|294|295|296|297|298|299|(4:301|302|303|304)|(4:309|310|311|312)|316|(1:318)(1:494)|319|(1:321)(6:482|483|484|485|486|487)|322|(1:324)|325|(1:327)|(4:329|330|331|332)|336|(1:338)|339|340|341|342|(1:479)|(4:348|349|350|351)|355|356|357|358|(1:360)|361|(1:363)|364|(1:475)|368|(1:370)|371|(1:373)|374|375|376|377|378|379|380|(1:468)(6:383|384|385|386|387|388)|389|390|391|392|(1:394)|395|(1:397)|398|(1:400)|401|(1:403)|404|(1:458)|408|(1:457)|412|(1:414)|415|(1:417)|418|(1:420)|421|422|423|424|425|426|(1:428)|(1:430)|(1:432)|433|(4:435|436|437|438)|(4:443|444|445|446)|450)|92|(1:94)|95|(1:97)|98|(1:100)|101|(1:103)|104|(1:106)(1:194)|107|(1:109)|110|(2:112|(1:114)(1:115))|116|(1:118)(1:193)|119|(2:188|189)|121|(1:123)|124|125|126|127|128|129|130|10d1|(1:138)|139|(1:141)|142|143|144|145|(6:147|148|149|150|151|152)|157|158|(1:160)|161|(1:163)|164|(1:166)|167|(2:168|169)|170|171) */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x06c3, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x06c7, code lost:
        reportWtf("starting NetworkPolicy Service", r0);
        r2 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x07af, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x07b3, code lost:
        reportWtf("starting VPN Manager Service", r0);
        r8 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x07ce, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x07cf, code lost:
        r21 = r1;
        r9 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x07d6, code lost:
        reportWtf("starting VCN Management Service", r0);
        r9 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:409:0x0d60, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:0x0d64, code lost:
        reportWtf("starting MediaRouterService", r0);
        r2 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:478:0x10b1, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:479:0x10b2, code lost:
        reportWtf("making Window Manager Service ready", r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:483:0x10c5, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:484:0x10c6, code lost:
        reportWtf("RegisterLogMteState", r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:106:0x04aa  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x04bb  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x057c  */
    /* JADX WARN: Removed duplicated region for block: B:136:0x058c  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x05c0  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x05d0  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x05e0  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x05f0  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0681  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x06dc  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x070a  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x0727  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0744  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0761  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x08e3  */
    /* JADX WARN: Removed duplicated region for block: B:267:0x08fc  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x0920  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x0930  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x094d  */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0957  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x09b1  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x09d1  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x09e2  */
    /* JADX WARN: Removed duplicated region for block: B:305:0x0a04  */
    /* JADX WARN: Removed duplicated region for block: B:312:0x0a34  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x0a51  */
    /* JADX WARN: Removed duplicated region for block: B:330:0x0a8a  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0adf  */
    /* JADX WARN: Removed duplicated region for block: B:336:0x0af8  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x0b3c  */
    /* JADX WARN: Removed duplicated region for block: B:344:0x0b64  */
    /* JADX WARN: Removed duplicated region for block: B:357:0x0baa A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:376:0x0c25  */
    /* JADX WARN: Removed duplicated region for block: B:379:0x0c41  */
    /* JADX WARN: Removed duplicated region for block: B:382:0x0c69  */
    /* JADX WARN: Removed duplicated region for block: B:385:0x0caf  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x0cc8  */
    /* JADX WARN: Removed duplicated region for block: B:393:0x0ceb  */
    /* JADX WARN: Removed duplicated region for block: B:398:0x0d0e  */
    /* JADX WARN: Removed duplicated region for block: B:401:0x0d27  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x0d40  */
    /* JADX WARN: Removed duplicated region for block: B:416:0x0d87  */
    /* JADX WARN: Removed duplicated region for block: B:418:0x0d9b  */
    /* JADX WARN: Removed duplicated region for block: B:420:0x0dac  */
    /* JADX WARN: Removed duplicated region for block: B:423:0x0dde  */
    /* JADX WARN: Removed duplicated region for block: B:430:0x0df4  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0e80  */
    /* JADX WARN: Removed duplicated region for block: B:442:0x0ee4  */
    /* JADX WARN: Removed duplicated region for block: B:445:0x0eff  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0f8c  */
    /* JADX WARN: Removed duplicated region for block: B:451:0x0f9b  */
    /* JADX WARN: Removed duplicated region for block: B:452:0x0fb0  */
    /* JADX WARN: Removed duplicated region for block: B:455:0x0fbc  */
    /* JADX WARN: Removed duplicated region for block: B:458:0x0fd5  */
    /* JADX WARN: Removed duplicated region for block: B:464:0x1000  */
    /* JADX WARN: Removed duplicated region for block: B:465:0x1010  */
    /* JADX WARN: Removed duplicated region for block: B:474:0x108e  */
    /* JADX WARN: Removed duplicated region for block: B:538:0x10d2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:590:0x105b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v15, types: [com.android.server.am.ActivityManagerService] */
    /* JADX WARN: Type inference failed for: r0v395, types: [com.android.server.statusbar.StatusBarManagerService, android.os.IBinder] */
    /* JADX WARN: Type inference failed for: r10v3, types: [android.os.IBinder, com.android.server.wm.WindowManagerService] */
    /* JADX WARN: Type inference failed for: r11v0, types: [com.android.server.input.InputManagerService, android.os.IBinder] */
    /* JADX WARN: Type inference failed for: r12v0, types: [com.android.server.TelephonyRegistry, android.os.IBinder] */
    /* JADX WARN: Type inference failed for: r22v3 */
    /* JADX WARN: Type inference failed for: r22v4 */
    /* JADX WARN: Type inference failed for: r22v7 */
    /* JADX WARN: Type inference failed for: r22v8 */
    /* JADX WARN: Type inference failed for: r2v31 */
    /* JADX WARN: Type inference failed for: r2v32 */
    /* JADX WARN: Type inference failed for: r2v34, types: [com.android.server.media.MediaRouterService, android.os.IBinder] */
    /* JADX WARN: Type inference failed for: r2v47, types: [android.os.IBinder, com.android.server.net.NetworkPolicyManagerService] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r8v65 */
    /* JADX WARN: Type inference failed for: r8v66 */
    /* JADX WARN: Type inference failed for: r8v69, types: [com.android.server.VpnManagerService, android.os.IBinder] */
    /* JADX WARN: Type inference failed for: r9v34, types: [com.android.server.VcnManagementService, android.os.IBinder] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void startOtherServices(final TimingsTraceAndSlog timingsTraceAndSlog) {
        ILockSettings iLockSettings;
        DevicePolicyManagerService.Lifecycle lifecycle;
        ILockSettings iLockSettings2;
        INetworkManagementService iNetworkManagementService;
        ?? r2;
        NetworkPolicyManagerService networkPolicyManagerService;
        ?? r8;
        INetworkManagementService iNetworkManagementService2;
        Object obj;
        ICountryDetector.Stub stub;
        Object obj2;
        NetworkPolicyManagerService networkPolicyManagerService2;
        NetworkTimeUpdateService networkTimeUpdateService;
        ?? r22;
        boolean hasSystemFeature;
        boolean hasSystemFeature2;
        boolean hasSystemFeature3;
        MediaRouterService mediaRouterService;
        final DevicePolicyManagerService.Lifecycle lifecycle2;
        VpnManagerService vpnManagerService;
        ILockSettings iLockSettings3;
        VcnManagementService vcnManagementService;
        final INetworkManagementService iNetworkManagementService3;
        NetworkTimeUpdateService networkTimeUpdateService2;
        ICountryDetector.Stub countryDetectorService;
        MmsServiceBroker mmsServiceBroker;
        final HsumBootUserInitializer createInstance;
        int i;
        timingsTraceAndSlog.traceBegin("startOtherServices");
        this.mSystemServiceManager.updateOtherServicesStartIndex();
        final Context context = this.mSystemContext;
        boolean z = SystemProperties.getBoolean("config.disable_systemtextclassifier", false);
        boolean z2 = SystemProperties.getBoolean("config.disable_networktime", false);
        boolean z3 = SystemProperties.getBoolean("config.disable_cameraservice", false);
        boolean equals = SystemProperties.get("ro.boot.qemu").equals("1");
        final boolean hasSystemFeature4 = context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        boolean hasSystemFeature5 = context.getPackageManager().hasSystemFeature("org.chromium.arc");
        context.getPackageManager().hasSystemFeature("android.software.leanback");
        boolean hasSystemFeature6 = context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance");
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.crash_system", false)) {
            throw new RuntimeException();
        }
        try {
            this.mZygotePreload = SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SystemServer.lambda$startOtherServices$1();
                }
            }, "SecondaryZygotePreload");
            timingsTraceAndSlog.traceBegin("StartKeyAttestationApplicationIdProviderService");
            ServiceManager.addService("sec_key_att_app_id_provider", new KeyAttestationApplicationIdProviderService(context));
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartKeyChainSystemService");
            this.mSystemServiceManager.startService(KeyChainSystemService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartBinaryTransparencyService");
            this.mSystemServiceManager.startService(BinaryTransparencyService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartSchedulingPolicyService");
            ServiceManager.addService("scheduling_policy", new SchedulingPolicyService());
            timingsTraceAndSlog.traceEnd();
            if (this.mPackageManager.hasSystemFeature("android.hardware.microphone") || this.mPackageManager.hasSystemFeature("android.software.telecom") || this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                timingsTraceAndSlog.traceBegin("StartTelecomLoaderService");
                this.mSystemServiceManager.startService(TelecomLoaderService.class);
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartTelephonyRegistry");
            final ?? telephonyRegistry = new TelephonyRegistry(context, new TelephonyRegistry.ConfigurationProvider());
            ServiceManager.addService("telephony.registry", (IBinder) telephonyRegistry);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartEntropyMixer");
            this.mEntropyMixer = new EntropyMixer(context);
            timingsTraceAndSlog.traceEnd();
            this.mContentResolver = context.getContentResolver();
            timingsTraceAndSlog.traceBegin("StartAccountManagerService");
            this.mSystemServiceManager.startService("com.android.server.accounts.AccountManagerService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartContentService");
            this.mSystemServiceManager.startService("com.android.server.content.ContentService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("InstallSystemProviders");
            this.mActivityManagerService.getContentProviderHelper().installSystemProviders();
            this.mSystemServiceManager.startService("com.android.server.deviceconfig.DeviceConfigInit$Lifecycle");
            SQLiteCompatibilityWalFlags.reset();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartDropBoxManager");
            this.mSystemServiceManager.startService(DropBoxManagerService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartRoleManagerService");
            LocalManagerRegistry.addManager(RoleServicePlatformHelper.class, new RoleServicePlatformHelperImpl(this.mSystemContext));
            this.mSystemServiceManager.startService("com.android.role.RoleService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartVibratorManagerService");
            this.mSystemServiceManager.startService(VibratorManagerService.Lifecycle.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartDynamicSystemService");
            ServiceManager.addService("dynamic_system", new DynamicSystemService(context));
            timingsTraceAndSlog.traceEnd();
            if (!hasSystemFeature4) {
                timingsTraceAndSlog.traceBegin("StartConsumerIrService");
                ServiceManager.addService("consumer_ir", new ConsumerIrService(context));
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartResourceEconomy");
            this.mSystemServiceManager.startService("com.android.server.tare.InternalResourceService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartAlarmManagerService");
            this.mSystemServiceManager.startService("com.android.server.alarm.AlarmManagerService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartInputManagerService");
            final ?? inputManagerService = new InputManagerService(context);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("DeviceStateManagerService");
            this.mSystemServiceManager.startService(DeviceStateManagerService.class);
            timingsTraceAndSlog.traceEnd();
            if (!z3) {
                timingsTraceAndSlog.traceBegin("StartCameraServiceProxy");
                this.mSystemServiceManager.startService(CameraServiceProxy.class);
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartWindowManagerService");
            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 200);
            ?? main = WindowManagerService.main(context, inputManagerService, !this.mFirstBoot, new PhoneWindowManager(), this.mActivityManagerService.mActivityTaskManager);
            ServiceManager.addService("window", (IBinder) main, false, 17);
            ServiceManager.addService("input", (IBinder) inputManagerService, false, 1);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("SetWindowManagerService");
            this.mActivityManagerService.setWindowManager(main);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("WindowManagerServiceOnInitReady");
            main.onInitReady();
            timingsTraceAndSlog.traceEnd();
            SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SystemServer.lambda$startOtherServices$2();
                }
            }, "StartISensorManagerService");
            SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SystemServer.lambda$startOtherServices$3();
                }
            }, "StartHidlServices");
            if (!hasSystemFeature4 && hasSystemFeature6) {
                timingsTraceAndSlog.traceBegin("StartVrManagerService");
                this.mSystemServiceManager.startService(VrManagerService.class);
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartInputManager");
            inputManagerService.setWindowManagerCallbacks(main.getInputManagerCallback());
            inputManagerService.start();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("DisplayManagerWindowManagerAndInputReady");
            this.mDisplayManagerService.windowManagerAndInputReady();
            timingsTraceAndSlog.traceEnd();
            if (this.mFactoryTestMode == 1) {
                Slog.i("SystemServer", "No Bluetooth Service (factory test)");
            } else if (!context.getPackageManager().hasSystemFeature("android.hardware.bluetooth")) {
                Slog.i("SystemServer", "No Bluetooth Service (Bluetooth Hardware Not Present)");
            } else {
                timingsTraceAndSlog.traceBegin("StartBluetoothService");
                this.mSystemServiceManager.startServiceFromJar("com.android.server.bluetooth.BluetoothService", "/apex/com.android.btservices/javalib/service-bluetooth.jar");
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin(IpConnectivityMetrics.TAG);
            this.mSystemServiceManager.startService("com.android.server.connectivity.IpConnectivityMetrics");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("NetworkWatchlistService");
            this.mSystemServiceManager.startService(NetworkWatchlistService.Lifecycle.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("PinnerService");
            this.mSystemServiceManager.startService(PinnerService.class);
            timingsTraceAndSlog.traceEnd();
            if (Build.IS_DEBUGGABLE && ProfcollectForwardingService.enabled()) {
                timingsTraceAndSlog.traceBegin("ProfcollectForwardingService");
                this.mSystemServiceManager.startService(ProfcollectForwardingService.class);
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("SignedConfigService");
            SignedConfigService.registerUpdateReceiver(this.mSystemContext);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("AppIntegrityService");
            this.mSystemServiceManager.startService(AppIntegrityManagerService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartLogcatManager");
            this.mSystemServiceManager.startService(LogcatManagerService.class);
            timingsTraceAndSlog.traceEnd();
            final boolean detectSafeMode = main.detectSafeMode();
            if (detectSafeMode) {
                Settings.Global.putInt(context.getContentResolver(), "airplane_mode_on", 1);
            } else if (context.getResources().getBoolean(17891377)) {
                Settings.Global.putInt(context.getContentResolver(), "airplane_mode_on", 0);
            }
            if (this.mFactoryTestMode != 1) {
                timingsTraceAndSlog.traceBegin("StartInputMethodManagerLifecycle");
                String string = context.getResources().getString(17039919);
                if (string.isEmpty()) {
                    this.mSystemServiceManager.startService(InputMethodManagerService.Lifecycle.class);
                } else {
                    try {
                        Slog.i("SystemServer", "Starting custom IMMS: " + string);
                        this.mSystemServiceManager.startService(string);
                    } catch (Throwable th) {
                        reportWtf("starting " + string, th);
                    }
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartAccessibilityManagerService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.accessibility.AccessibilityManagerService$Lifecycle");
                } catch (Throwable th2) {
                    reportWtf("starting Accessibility Manager", th2);
                }
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("MakeDisplayReady");
            try {
                main.displayReady();
            } catch (Throwable th3) {
                reportWtf("making display ready", th3);
            }
            timingsTraceAndSlog.traceEnd();
            if (this.mFactoryTestMode != 1 && !"0".equals(SystemProperties.get("system_init.startmountservice"))) {
                timingsTraceAndSlog.traceBegin("StartStorageManagerService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.StorageManagerService$Lifecycle");
                    IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
                } catch (Throwable th4) {
                    reportWtf("starting StorageManagerService", th4);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartStorageStatsService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.usage.StorageStatsService$Lifecycle");
                } catch (Throwable th5) {
                    reportWtf("starting StorageStatsService", th5);
                }
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartUiModeManager");
            this.mSystemServiceManager.startService(UiModeManagerService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartLocaleManagerService");
            try {
                this.mSystemServiceManager.startService(LocaleManagerService.class);
            } catch (Throwable th6) {
                reportWtf("starting LocaleManagerService service", th6);
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartGrammarInflectionService");
            try {
                this.mSystemServiceManager.startService(GrammaticalInflectionService.class);
            } catch (Throwable th7) {
                reportWtf("starting GrammarInflectionService service", th7);
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("ArtManagerLocal");
            DexOptHelper.initializeArtManagerLocal(context, this.mPackageManagerService);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("UpdatePackagesIfNeeded");
            try {
                Watchdog.getInstance().pauseWatchingCurrentThread("dexopt");
                this.mPackageManagerService.updatePackagesIfNeeded();
            } finally {
                try {
                    Watchdog.getInstance().resumeWatchingCurrentThread("dexopt");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("PerformFstrimIfNeeded");
                    this.mPackageManagerService.performFstrimIfNeeded();
                    timingsTraceAndSlog.traceEnd();
                    if (this.mFactoryTestMode != 1) {
                    }
                    timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                    this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                    timingsTraceAndSlog.traceEnd();
                    if (hasSystemFeature4) {
                    }
                    if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                    }
                    if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                    }
                    timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                    this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                    this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                    this.mSystemServiceManager.startService(IncidentCompanionService.class);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                    this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                    this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                    this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                    if (detectSafeMode) {
                    }
                    if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                    }
                    if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                    }
                    if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                    }
                    if (!deviceHasConfigString(context, 17039905)) {
                    }
                    timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                    this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartClipboardService");
                    this.mSystemServiceManager.startService(ClipboardService.class);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("AppServiceManager");
                    this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                    this.mSystemServiceManager.startService(TracingServiceProxy.class);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                    if (iLockSettings3 != null) {
                    }
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                    this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                    timingsTraceAndSlog.traceEnd();
                    createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                    if (createInstance != null) {
                    }
                    timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                    this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                    main.systemReady();
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                    LogMteState.register(context);
                    timingsTraceAndSlog.traceEnd();
                    synchronized (SystemService.class) {
                    }
                } catch (Throwable th8) {
                }
            }
            Watchdog.getInstance().resumeWatchingCurrentThread("dexopt");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("PerformFstrimIfNeeded");
            try {
                this.mPackageManagerService.performFstrimIfNeeded();
            } catch (Throwable th9) {
                reportWtf("performing fstrim", th9);
            }
            timingsTraceAndSlog.traceEnd();
            if (this.mFactoryTestMode != 1) {
                lifecycle2 = null;
                iLockSettings3 = null;
                iNetworkManagementService3 = null;
                vpnManagerService = null;
                vcnManagementService = null;
                networkTimeUpdateService2 = null;
                obj2 = null;
                networkPolicyManagerService2 = null;
                mediaRouterService = null;
            } else {
                timingsTraceAndSlog.traceBegin("StartLockSettingsService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.locksettings.LockSettingsService$Lifecycle");
                    iLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
                } catch (Throwable th10) {
                    reportWtf("starting LockSettingsService service", th10);
                    iLockSettings = null;
                }
                timingsTraceAndSlog.traceEnd();
                boolean z4 = !SystemProperties.get("ro.frp.pst").equals("");
                if (z4) {
                    timingsTraceAndSlog.traceBegin("StartPersistentDataBlock");
                    this.mSystemServiceManager.startService(PersistentDataBlockService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartTestHarnessMode");
                this.mSystemServiceManager.startService(TestHarnessModeService.class);
                timingsTraceAndSlog.traceEnd();
                if (z4 || OemLockService.isHalPresent()) {
                    timingsTraceAndSlog.traceBegin("StartOemLockService");
                    this.mSystemServiceManager.startService(OemLockService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartDeviceIdleController");
                this.mSystemServiceManager.startService("com.android.server.DeviceIdleController");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartDevicePolicyManager");
                DevicePolicyManagerService.Lifecycle lifecycle3 = (DevicePolicyManagerService.Lifecycle) this.mSystemServiceManager.startService(DevicePolicyManagerService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartStatusBarManagerService");
                try {
                    ?? statusBarManagerService = new StatusBarManagerService(context);
                    if (!hasSystemFeature4) {
                        statusBarManagerService.publishGlobalActionsProvider();
                    }
                    lifecycle = lifecycle3;
                    iLockSettings2 = iLockSettings;
                    try {
                        ServiceManager.addService("statusbar", (IBinder) statusBarManagerService, false, 20);
                    } catch (Throwable th11) {
                        th = th11;
                        reportWtf("starting StatusBarManagerService", th);
                        timingsTraceAndSlog.traceEnd();
                        if (!deviceHasConfigString(context, 17039888)) {
                        }
                        startContentCaptureService(context, timingsTraceAndSlog);
                        startAttentionService(context, timingsTraceAndSlog);
                        startRotationResolverService(context, timingsTraceAndSlog);
                        startSystemCaptionsManagerService(context, timingsTraceAndSlog);
                        startTextToSpeechManagerService(context, timingsTraceAndSlog);
                        startAmbientContextService(timingsTraceAndSlog);
                        startWearableSensingService(timingsTraceAndSlog);
                        timingsTraceAndSlog.traceBegin("StartSpeechRecognitionManagerService");
                        this.mSystemServiceManager.startService("com.android.server.speech.SpeechRecognitionManagerService");
                        timingsTraceAndSlog.traceEnd();
                        if (!deviceHasConfigString(context, 17039871)) {
                        }
                        if (!deviceHasConfigString(context, 17039879)) {
                        }
                        timingsTraceAndSlog.traceBegin("StartSearchUiService");
                        this.mSystemServiceManager.startService("com.android.server.searchui.SearchUiManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartSmartspaceService");
                        this.mSystemServiceManager.startService("com.android.server.smartspace.SmartspaceManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("InitConnectivityModuleConnector");
                        ConnectivityModuleConnector.getInstance().init(context);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("InitNetworkStackClient");
                        NetworkStackClient.getInstance().init();
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartNetworkManagementService");
                        iNetworkManagementService = NetworkManagementService.create(context);
                        try {
                            ServiceManager.addService("network_management", iNetworkManagementService);
                        } catch (Throwable th12) {
                            th = th12;
                            reportWtf("starting NetworkManagement Service", th);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartFontManagerService");
                            this.mSystemServiceManager.startService(new FontManagerService.Lifecycle(context, detectSafeMode));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTextServicesManager");
                            this.mSystemServiceManager.startService(TextServicesManagerService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!z) {
                            }
                            timingsTraceAndSlog.traceBegin("StartNetworkScoreService");
                            this.mSystemServiceManager.startService(NetworkScoreService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartNetworkStatsService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.NetworkStatsServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartNetworkPolicyManagerService");
                            r2 = new NetworkPolicyManagerService(context, this.mActivityManagerService, iNetworkManagementService);
                            ServiceManager.addService("netpolicy", (IBinder) r2);
                            networkPolicyManagerService = r2;
                            timingsTraceAndSlog.traceEnd();
                            if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.aware")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.lowpan")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartPacProxyService");
                            ServiceManager.addService("pac_proxy", new PacProxyService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartConnectivityService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.ConnectivityServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                            networkPolicyManagerService.bindConnectivityManager();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVpnManagerService");
                            r8 = VpnManagerService.create(context);
                            ServiceManager.addService("vpn_management", (IBinder) r8);
                            VpnManagerService vpnManagerService2 = r8;
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVcnManagementService");
                            ?? create = VcnManagementService.create(context);
                            ServiceManager.addService("vcn_management", (IBinder) create);
                            iNetworkManagementService2 = iNetworkManagementService;
                            VcnManagementService vcnManagementService2 = create;
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSystemUpdateManagerService");
                            ServiceManager.addService("system_update", new SystemUpdateManagerService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartUpdateLockService");
                            ServiceManager.addService("updatelock", new UpdateLockService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartNotificationManager");
                            this.mSystemServiceManager.startService(NotificationManagerService.class);
                            SystemNotificationChannels.removeDeprecated(context);
                            SystemNotificationChannels.createAll(context);
                            INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDeviceMonitor");
                            this.mSystemServiceManager.startService(DeviceStorageMonitorService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTimeDetectorService");
                            this.mSystemServiceManager.startService("com.android.server.timedetector.TimeDetectorService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLocationManagerService");
                            this.mSystemServiceManager.startService(LocationManagerService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCountryDetectorService");
                            countryDetectorService = new CountryDetectorService(context);
                            ServiceManager.addService("country_detector", countryDetectorService);
                            stub = countryDetectorService;
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTimeZoneDetectorService");
                            obj2 = stub;
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.TimeZoneDetectorService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAltitudeService");
                            this.mSystemServiceManager.startService(AltitudeService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLocationTimeZoneManagerService");
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.location.LocationTimeZoneManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (context.getResources().getBoolean(17891653)) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (!context.getResources().getBoolean(17891671)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartWallpaperEffectsGenerationService");
                            this.mSystemServiceManager.startService("com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAudioService");
                            if (hasSystemFeature5) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                            this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDockObserver");
                            this.mSystemServiceManager.startService(DockObserver.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAdbService");
                            this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartUsbService");
                            this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                            ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartColorDisplay");
                            this.mSystemServiceManager.startService(ColorDisplayService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartJobScheduler");
                            this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                            this.mSystemServiceManager.startService(SoundTriggerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTrustManager");
                            this.mSystemServiceManager.startService(TrustManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                            this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                            this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                            this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                            timingsTraceAndSlog.traceEnd();
                            if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSensorNotification");
                            this.mSystemServiceManager.startService(SensorNotificationService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                            ServiceManager.addService("diskstats", new DiskStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RuntimeService");
                            ServiceManager.addService("runtime", new RuntimeService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            networkTimeUpdateService = null;
                            timingsTraceAndSlog.traceBegin("CertBlacklister");
                            new CertBlacklister(context);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                            this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                            this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDreamManager");
                            this.mSystemServiceManager.startService(DreamManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                            ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (CoverageService.ENABLED) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                            this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                            this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                            this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                            this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInputManager");
                            this.mSystemServiceManager.startService(TvInputManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                            r22 = new MediaRouterService(context);
                            ServiceManager.addService("media_router", (IBinder) r22);
                            MediaRouterService mediaRouterService2 = r22;
                            timingsTraceAndSlog.traceEnd();
                            hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                            hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                            hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                            if (hasSystemFeature) {
                            }
                            if (hasSystemFeature2) {
                            }
                            if (hasSystemFeature3) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBiometricService");
                            this.mSystemServiceManager.startService(BiometricService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAuthService");
                            this.mSystemServiceManager.startService(AuthService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                            this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                            this.mSystemServiceManager.startService(LauncherAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                            this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartPeopleService");
                            this.mSystemServiceManager.startService(PeopleService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                            this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                            this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                            timingsTraceAndSlog.traceEnd();
                            mediaRouterService = mediaRouterService2;
                            lifecycle2 = lifecycle;
                            vpnManagerService = vpnManagerService2;
                            iLockSettings3 = iLockSettings2;
                            vcnManagementService = vcnManagementService2;
                            iNetworkManagementService3 = iNetworkManagementService2;
                            networkTimeUpdateService2 = networkTimeUpdateService;
                            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                            this.mSystemServiceManager.startService(IncidentCompanionService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (detectSafeMode) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                            }
                            if (!deviceHasConfigString(context, 17039905)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartClipboardService");
                            this.mSystemServiceManager.startService(ClipboardService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AppServiceManager");
                            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                            this.mSystemServiceManager.startService(TracingServiceProxy.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                            if (iLockSettings3 != null) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                            timingsTraceAndSlog.traceEnd();
                            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                            if (createInstance != null) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                            main.systemReady();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                            LogMteState.register(context);
                            timingsTraceAndSlog.traceEnd();
                            synchronized (SystemService.class) {
                            }
                        }
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartFontManagerService");
                        this.mSystemServiceManager.startService(new FontManagerService.Lifecycle(context, detectSafeMode));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartTextServicesManager");
                        this.mSystemServiceManager.startService(TextServicesManagerService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        if (!z) {
                        }
                        timingsTraceAndSlog.traceBegin("StartNetworkScoreService");
                        this.mSystemServiceManager.startService(NetworkScoreService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartNetworkStatsService");
                        this.mSystemServiceManager.startServiceFromJar("com.android.server.NetworkStatsServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartNetworkPolicyManagerService");
                        r2 = new NetworkPolicyManagerService(context, this.mActivityManagerService, iNetworkManagementService);
                        ServiceManager.addService("netpolicy", (IBinder) r2);
                        networkPolicyManagerService = r2;
                        timingsTraceAndSlog.traceEnd();
                        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
                        }
                        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt")) {
                        }
                        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.aware")) {
                        }
                        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct")) {
                        }
                        if (context.getPackageManager().hasSystemFeature("android.hardware.lowpan")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartPacProxyService");
                        ServiceManager.addService("pac_proxy", new PacProxyService(context));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartConnectivityService");
                        this.mSystemServiceManager.startServiceFromJar("com.android.server.ConnectivityServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                        networkPolicyManagerService.bindConnectivityManager();
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartVpnManagerService");
                        r8 = VpnManagerService.create(context);
                        ServiceManager.addService("vpn_management", (IBinder) r8);
                        VpnManagerService vpnManagerService22 = r8;
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartVcnManagementService");
                        ?? create2 = VcnManagementService.create(context);
                        ServiceManager.addService("vcn_management", (IBinder) create2);
                        iNetworkManagementService2 = iNetworkManagementService;
                        VcnManagementService vcnManagementService22 = create2;
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartSystemUpdateManagerService");
                        ServiceManager.addService("system_update", new SystemUpdateManagerService(context));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartUpdateLockService");
                        ServiceManager.addService("updatelock", new UpdateLockService(context));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartNotificationManager");
                        this.mSystemServiceManager.startService(NotificationManagerService.class);
                        SystemNotificationChannels.removeDeprecated(context);
                        SystemNotificationChannels.createAll(context);
                        INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartDeviceMonitor");
                        this.mSystemServiceManager.startService(DeviceStorageMonitorService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartTimeDetectorService");
                        this.mSystemServiceManager.startService("com.android.server.timedetector.TimeDetectorService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartLocationManagerService");
                        this.mSystemServiceManager.startService(LocationManagerService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartCountryDetectorService");
                        countryDetectorService = new CountryDetectorService(context);
                        try {
                            ServiceManager.addService("country_detector", countryDetectorService);
                            stub = countryDetectorService;
                        } catch (Throwable th13) {
                            th = th13;
                            obj = countryDetectorService;
                            reportWtf("starting Country Detector", th);
                            stub = obj;
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTimeZoneDetectorService");
                            obj2 = stub;
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.TimeZoneDetectorService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAltitudeService");
                            this.mSystemServiceManager.startService(AltitudeService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLocationTimeZoneManagerService");
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.location.LocationTimeZoneManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (context.getResources().getBoolean(17891653)) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (!context.getResources().getBoolean(17891671)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartWallpaperEffectsGenerationService");
                            this.mSystemServiceManager.startService("com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAudioService");
                            if (hasSystemFeature5) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                            this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDockObserver");
                            this.mSystemServiceManager.startService(DockObserver.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAdbService");
                            this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartUsbService");
                            this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                            ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartColorDisplay");
                            this.mSystemServiceManager.startService(ColorDisplayService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartJobScheduler");
                            this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                            this.mSystemServiceManager.startService(SoundTriggerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTrustManager");
                            this.mSystemServiceManager.startService(TrustManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                            this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                            this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                            this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                            timingsTraceAndSlog.traceEnd();
                            if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSensorNotification");
                            this.mSystemServiceManager.startService(SensorNotificationService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                            ServiceManager.addService("diskstats", new DiskStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RuntimeService");
                            ServiceManager.addService("runtime", new RuntimeService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            networkTimeUpdateService = null;
                            timingsTraceAndSlog.traceBegin("CertBlacklister");
                            new CertBlacklister(context);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                            this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                            this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDreamManager");
                            this.mSystemServiceManager.startService(DreamManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                            ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (CoverageService.ENABLED) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                            this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                            this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                            this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                            this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInputManager");
                            this.mSystemServiceManager.startService(TvInputManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                            r22 = new MediaRouterService(context);
                            ServiceManager.addService("media_router", (IBinder) r22);
                            MediaRouterService mediaRouterService22 = r22;
                            timingsTraceAndSlog.traceEnd();
                            hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                            hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                            hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                            if (hasSystemFeature) {
                            }
                            if (hasSystemFeature2) {
                            }
                            if (hasSystemFeature3) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBiometricService");
                            this.mSystemServiceManager.startService(BiometricService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAuthService");
                            this.mSystemServiceManager.startService(AuthService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                            this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                            this.mSystemServiceManager.startService(LauncherAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                            this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartPeopleService");
                            this.mSystemServiceManager.startService(PeopleService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                            this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                            this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                            timingsTraceAndSlog.traceEnd();
                            mediaRouterService = mediaRouterService22;
                            lifecycle2 = lifecycle;
                            vpnManagerService = vpnManagerService22;
                            iLockSettings3 = iLockSettings2;
                            vcnManagementService = vcnManagementService22;
                            iNetworkManagementService3 = iNetworkManagementService2;
                            networkTimeUpdateService2 = networkTimeUpdateService;
                            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                            this.mSystemServiceManager.startService(IncidentCompanionService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (detectSafeMode) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                            }
                            if (!deviceHasConfigString(context, 17039905)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartClipboardService");
                            this.mSystemServiceManager.startService(ClipboardService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AppServiceManager");
                            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                            this.mSystemServiceManager.startService(TracingServiceProxy.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                            if (iLockSettings3 != null) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                            timingsTraceAndSlog.traceEnd();
                            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                            if (createInstance != null) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                            main.systemReady();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                            LogMteState.register(context);
                            timingsTraceAndSlog.traceEnd();
                            synchronized (SystemService.class) {
                            }
                        }
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartTimeZoneDetectorService");
                        obj2 = stub;
                        try {
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.TimeZoneDetectorService$Lifecycle");
                        } catch (Throwable th14) {
                            th = th14;
                            reportWtf("starting TimeZoneDetectorService service", th);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAltitudeService");
                            this.mSystemServiceManager.startService(AltitudeService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLocationTimeZoneManagerService");
                            this.mSystemServiceManager.startService("com.android.server.timezonedetector.location.LocationTimeZoneManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (context.getResources().getBoolean(17891653)) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (!context.getResources().getBoolean(17891671)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartWallpaperEffectsGenerationService");
                            this.mSystemServiceManager.startService("com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAudioService");
                            if (hasSystemFeature5) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                            this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDockObserver");
                            this.mSystemServiceManager.startService(DockObserver.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAdbService");
                            this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartUsbService");
                            this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                            ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartColorDisplay");
                            this.mSystemServiceManager.startService(ColorDisplayService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartJobScheduler");
                            this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                            this.mSystemServiceManager.startService(SoundTriggerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTrustManager");
                            this.mSystemServiceManager.startService(TrustManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                            this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                            this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                            this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                            timingsTraceAndSlog.traceEnd();
                            if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSensorNotification");
                            this.mSystemServiceManager.startService(SensorNotificationService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                            ServiceManager.addService("diskstats", new DiskStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RuntimeService");
                            ServiceManager.addService("runtime", new RuntimeService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            networkTimeUpdateService = null;
                            timingsTraceAndSlog.traceBegin("CertBlacklister");
                            new CertBlacklister(context);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                            this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                            this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDreamManager");
                            this.mSystemServiceManager.startService(DreamManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                            ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (CoverageService.ENABLED) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                            this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                            this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                            this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                            this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInputManager");
                            this.mSystemServiceManager.startService(TvInputManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                            r22 = new MediaRouterService(context);
                            ServiceManager.addService("media_router", (IBinder) r22);
                            MediaRouterService mediaRouterService222 = r22;
                            timingsTraceAndSlog.traceEnd();
                            hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                            hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                            hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                            if (hasSystemFeature) {
                            }
                            if (hasSystemFeature2) {
                            }
                            if (hasSystemFeature3) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBiometricService");
                            this.mSystemServiceManager.startService(BiometricService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAuthService");
                            this.mSystemServiceManager.startService(AuthService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                            this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                            this.mSystemServiceManager.startService(LauncherAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                            this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartPeopleService");
                            this.mSystemServiceManager.startService(PeopleService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                            this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                            this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                            timingsTraceAndSlog.traceEnd();
                            mediaRouterService = mediaRouterService222;
                            lifecycle2 = lifecycle;
                            vpnManagerService = vpnManagerService22;
                            iLockSettings3 = iLockSettings2;
                            vcnManagementService = vcnManagementService22;
                            iNetworkManagementService3 = iNetworkManagementService2;
                            networkTimeUpdateService2 = networkTimeUpdateService;
                            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                            this.mSystemServiceManager.startService(IncidentCompanionService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (detectSafeMode) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                            }
                            if (!deviceHasConfigString(context, 17039905)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartClipboardService");
                            this.mSystemServiceManager.startService(ClipboardService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AppServiceManager");
                            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                            this.mSystemServiceManager.startService(TracingServiceProxy.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                            if (iLockSettings3 != null) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                            timingsTraceAndSlog.traceEnd();
                            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                            if (createInstance != null) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                            main.systemReady();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                            LogMteState.register(context);
                            timingsTraceAndSlog.traceEnd();
                            synchronized (SystemService.class) {
                            }
                        }
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartAltitudeService");
                        this.mSystemServiceManager.startService(AltitudeService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartLocationTimeZoneManagerService");
                        this.mSystemServiceManager.startService("com.android.server.timezonedetector.location.LocationTimeZoneManagerService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        if (context.getResources().getBoolean(17891653)) {
                        }
                        if (!hasSystemFeature4) {
                        }
                        if (!context.getResources().getBoolean(17891671)) {
                        }
                        timingsTraceAndSlog.traceBegin("StartWallpaperEffectsGenerationService");
                        this.mSystemServiceManager.startService("com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartAudioService");
                        if (hasSystemFeature5) {
                        }
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                        this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartDockObserver");
                        this.mSystemServiceManager.startService(DockObserver.class);
                        timingsTraceAndSlog.traceEnd();
                        if (hasSystemFeature4) {
                        }
                        if (!hasSystemFeature4) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartAdbService");
                        this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartUsbService");
                        this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        if (!hasSystemFeature4) {
                        }
                        timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                        ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                        timingsTraceAndSlog.traceEnd();
                        if (!hasSystemFeature4) {
                        }
                        timingsTraceAndSlog.traceBegin("StartColorDisplay");
                        this.mSystemServiceManager.startService(ColorDisplayService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartJobScheduler");
                        this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                        this.mSystemServiceManager.startService(SoundTriggerService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartTrustManager");
                        this.mSystemServiceManager.startService(TrustManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                        }
                        if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                        this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                        this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                        this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                        timingsTraceAndSlog.traceEnd();
                        if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                        }
                        timingsTraceAndSlog.traceBegin("StartSensorNotification");
                        this.mSystemServiceManager.startService(SensorNotificationService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                        ServiceManager.addService("diskstats", new DiskStatsService(context));
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("RuntimeService");
                        ServiceManager.addService("runtime", new RuntimeService(context));
                        timingsTraceAndSlog.traceEnd();
                        if (hasSystemFeature4) {
                        }
                        networkTimeUpdateService = null;
                        timingsTraceAndSlog.traceBegin("CertBlacklister");
                        new CertBlacklister(context);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                        this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                        this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartDreamManager");
                        this.mSystemServiceManager.startService(DreamManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                        ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                        timingsTraceAndSlog.traceEnd();
                        if (CoverageService.ENABLED) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                        this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                        this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                        this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                        }
                        if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                        this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartTvInputManager");
                        this.mSystemServiceManager.startService(TvInputManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                        r22 = new MediaRouterService(context);
                        ServiceManager.addService("media_router", (IBinder) r22);
                        MediaRouterService mediaRouterService2222 = r22;
                        timingsTraceAndSlog.traceEnd();
                        hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                        hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                        hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                        if (hasSystemFeature) {
                        }
                        if (hasSystemFeature2) {
                        }
                        if (hasSystemFeature3) {
                        }
                        timingsTraceAndSlog.traceBegin("StartBiometricService");
                        this.mSystemServiceManager.startService(BiometricService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartAuthService");
                        this.mSystemServiceManager.startService(AuthService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (!hasSystemFeature4) {
                        }
                        if (!hasSystemFeature4) {
                        }
                        timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                        this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                        this.mSystemServiceManager.startService(LauncherAppsService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                        this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartPeopleService");
                        this.mSystemServiceManager.startService(PeopleService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                        this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                        this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                        timingsTraceAndSlog.traceEnd();
                        mediaRouterService = mediaRouterService2222;
                        lifecycle2 = lifecycle;
                        vpnManagerService = vpnManagerService22;
                        iLockSettings3 = iLockSettings2;
                        vcnManagementService = vcnManagementService22;
                        iNetworkManagementService3 = iNetworkManagementService2;
                        networkTimeUpdateService2 = networkTimeUpdateService;
                        timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                        this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                        timingsTraceAndSlog.traceEnd();
                        if (hasSystemFeature4) {
                        }
                        if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                        }
                        if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                        }
                        timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                        this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                        this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                        this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                        this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                        this.mSystemServiceManager.startService(IncidentCompanionService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                        this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                        this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                        this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                        timingsTraceAndSlog.traceEnd();
                        if (detectSafeMode) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                        }
                        if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                        }
                        if (!deviceHasConfigString(context, 17039905)) {
                        }
                        timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                        this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartClipboardService");
                        this.mSystemServiceManager.startService(ClipboardService.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("AppServiceManager");
                        this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                        this.mSystemServiceManager.startService(TracingServiceProxy.class);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                        if (iLockSettings3 != null) {
                        }
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                        this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                        timingsTraceAndSlog.traceEnd();
                        createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                        if (createInstance != null) {
                        }
                        timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                        this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                        main.systemReady();
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                        LogMteState.register(context);
                        timingsTraceAndSlog.traceEnd();
                        synchronized (SystemService.class) {
                        }
                    }
                } catch (Throwable th15) {
                    th = th15;
                    lifecycle = lifecycle3;
                    iLockSettings2 = iLockSettings;
                }
                timingsTraceAndSlog.traceEnd();
                if (!deviceHasConfigString(context, 17039888)) {
                    timingsTraceAndSlog.traceBegin("StartMusicRecognitionManagerService");
                    this.mSystemServiceManager.startService("com.android.server.musicrecognition.MusicRecognitionManagerService");
                    timingsTraceAndSlog.traceEnd();
                } else {
                    Slog.d("SystemServer", "MusicRecognitionManagerService not defined by OEM or disabled by flag");
                }
                startContentCaptureService(context, timingsTraceAndSlog);
                startAttentionService(context, timingsTraceAndSlog);
                startRotationResolverService(context, timingsTraceAndSlog);
                startSystemCaptionsManagerService(context, timingsTraceAndSlog);
                startTextToSpeechManagerService(context, timingsTraceAndSlog);
                startAmbientContextService(timingsTraceAndSlog);
                startWearableSensingService(timingsTraceAndSlog);
                timingsTraceAndSlog.traceBegin("StartSpeechRecognitionManagerService");
                this.mSystemServiceManager.startService("com.android.server.speech.SpeechRecognitionManagerService");
                timingsTraceAndSlog.traceEnd();
                if (!deviceHasConfigString(context, 17039871)) {
                    timingsTraceAndSlog.traceBegin("StartAppPredictionService");
                    this.mSystemServiceManager.startService("com.android.server.appprediction.AppPredictionManagerService");
                    timingsTraceAndSlog.traceEnd();
                } else {
                    Slog.d("SystemServer", "AppPredictionService not defined by OEM");
                }
                if (!deviceHasConfigString(context, 17039879)) {
                    timingsTraceAndSlog.traceBegin("StartContentSuggestionsService");
                    this.mSystemServiceManager.startService("com.android.server.contentsuggestions.ContentSuggestionsManagerService");
                    timingsTraceAndSlog.traceEnd();
                } else {
                    Slog.d("SystemServer", "ContentSuggestionsService not defined by OEM");
                }
                timingsTraceAndSlog.traceBegin("StartSearchUiService");
                this.mSystemServiceManager.startService("com.android.server.searchui.SearchUiManagerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartSmartspaceService");
                this.mSystemServiceManager.startService("com.android.server.smartspace.SmartspaceManagerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("InitConnectivityModuleConnector");
                try {
                    ConnectivityModuleConnector.getInstance().init(context);
                } catch (Throwable th16) {
                    reportWtf("initializing ConnectivityModuleConnector", th16);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("InitNetworkStackClient");
                try {
                    NetworkStackClient.getInstance().init();
                } catch (Throwable th17) {
                    reportWtf("initializing NetworkStackClient", th17);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartNetworkManagementService");
                try {
                    iNetworkManagementService = NetworkManagementService.create(context);
                    ServiceManager.addService("network_management", iNetworkManagementService);
                } catch (Throwable th18) {
                    th = th18;
                    iNetworkManagementService = null;
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartFontManagerService");
                this.mSystemServiceManager.startService(new FontManagerService.Lifecycle(context, detectSafeMode));
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartTextServicesManager");
                this.mSystemServiceManager.startService(TextServicesManagerService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                if (!z) {
                    timingsTraceAndSlog.traceBegin("StartTextClassificationManagerService");
                    this.mSystemServiceManager.startService(TextClassificationManagerService.Lifecycle.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartNetworkScoreService");
                this.mSystemServiceManager.startService(NetworkScoreService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartNetworkStatsService");
                this.mSystemServiceManager.startServiceFromJar("com.android.server.NetworkStatsServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartNetworkPolicyManagerService");
                try {
                    r2 = new NetworkPolicyManagerService(context, this.mActivityManagerService, iNetworkManagementService);
                    ServiceManager.addService("netpolicy", (IBinder) r2);
                    networkPolicyManagerService = r2;
                } catch (Throwable th19) {
                    th = th19;
                    r2 = 0;
                }
                timingsTraceAndSlog.traceEnd();
                if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
                    timingsTraceAndSlog.traceBegin("StartWifi");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.wifi.WifiService", "/apex/com.android.wifi/javalib/service-wifi.jar");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartWifiScanning");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.wifi.scanner.WifiScanningService", "/apex/com.android.wifi/javalib/service-wifi.jar");
                    timingsTraceAndSlog.traceEnd();
                }
                if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.rtt")) {
                    timingsTraceAndSlog.traceBegin("StartRttService");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.wifi.rtt.RttService", "/apex/com.android.wifi/javalib/service-wifi.jar");
                    timingsTraceAndSlog.traceEnd();
                }
                if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.aware")) {
                    timingsTraceAndSlog.traceBegin("StartWifiAware");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.wifi.aware.WifiAwareService", "/apex/com.android.wifi/javalib/service-wifi.jar");
                    timingsTraceAndSlog.traceEnd();
                }
                if (context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct")) {
                    timingsTraceAndSlog.traceBegin("StartWifiP2P");
                    this.mSystemServiceManager.startServiceFromJar("com.android.server.wifi.p2p.WifiP2pService", "/apex/com.android.wifi/javalib/service-wifi.jar");
                    timingsTraceAndSlog.traceEnd();
                }
                if (context.getPackageManager().hasSystemFeature("android.hardware.lowpan")) {
                    timingsTraceAndSlog.traceBegin("StartLowpan");
                    this.mSystemServiceManager.startService("com.android.server.lowpan.LowpanService");
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartPacProxyService");
                try {
                    ServiceManager.addService("pac_proxy", new PacProxyService(context));
                } catch (Throwable th20) {
                    reportWtf("starting PacProxyService", th20);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartConnectivityService");
                this.mSystemServiceManager.startServiceFromJar("com.android.server.ConnectivityServiceInitializer", "/apex/com.android.tethering/javalib/service-connectivity.jar");
                networkPolicyManagerService.bindConnectivityManager();
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartVpnManagerService");
                try {
                    r8 = VpnManagerService.create(context);
                    ServiceManager.addService("vpn_management", (IBinder) r8);
                    VpnManagerService vpnManagerService222 = r8;
                } catch (Throwable th21) {
                    th = th21;
                    r8 = 0;
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartVcnManagementService");
                try {
                    ?? create22 = VcnManagementService.create(context);
                    ServiceManager.addService("vcn_management", (IBinder) create22);
                    iNetworkManagementService2 = iNetworkManagementService;
                    VcnManagementService vcnManagementService222 = create22;
                } catch (Throwable th22) {
                    th = th22;
                    iNetworkManagementService2 = iNetworkManagementService;
                    VcnManagementService vcnManagementService3 = null;
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartSystemUpdateManagerService");
                try {
                    ServiceManager.addService("system_update", new SystemUpdateManagerService(context));
                } catch (Throwable th23) {
                    reportWtf("starting SystemUpdateManagerService", th23);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartUpdateLockService");
                try {
                    ServiceManager.addService("updatelock", new UpdateLockService(context));
                } catch (Throwable th24) {
                    reportWtf("starting UpdateLockService", th24);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartNotificationManager");
                this.mSystemServiceManager.startService(NotificationManagerService.class);
                SystemNotificationChannels.removeDeprecated(context);
                SystemNotificationChannels.createAll(context);
                INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartDeviceMonitor");
                this.mSystemServiceManager.startService(DeviceStorageMonitorService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartTimeDetectorService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.timedetector.TimeDetectorService$Lifecycle");
                } catch (Throwable th25) {
                    reportWtf("starting TimeDetectorService service", th25);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartLocationManagerService");
                this.mSystemServiceManager.startService(LocationManagerService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartCountryDetectorService");
                try {
                    countryDetectorService = new CountryDetectorService(context);
                    ServiceManager.addService("country_detector", countryDetectorService);
                    stub = countryDetectorService;
                } catch (Throwable th26) {
                    th = th26;
                    obj = null;
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartTimeZoneDetectorService");
                try {
                    obj2 = stub;
                    this.mSystemServiceManager.startService("com.android.server.timezonedetector.TimeZoneDetectorService$Lifecycle");
                } catch (Throwable th27) {
                    th = th27;
                    obj2 = stub;
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartAltitudeService");
                try {
                    this.mSystemServiceManager.startService(AltitudeService.Lifecycle.class);
                } catch (Throwable th28) {
                    reportWtf("starting AltitudeService service", th28);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartLocationTimeZoneManagerService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.timezonedetector.location.LocationTimeZoneManagerService$Lifecycle");
                } catch (Throwable th29) {
                    reportWtf("starting LocationTimeZoneManagerService service", th29);
                }
                timingsTraceAndSlog.traceEnd();
                if (context.getResources().getBoolean(17891653)) {
                    timingsTraceAndSlog.traceBegin("StartGnssTimeUpdateService");
                    try {
                        this.mSystemServiceManager.startService("com.android.server.timedetector.GnssTimeUpdateService$Lifecycle");
                    } catch (Throwable th30) {
                        reportWtf("starting GnssTimeUpdateService service", th30);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartSearchManagerService");
                    try {
                        this.mSystemServiceManager.startService("com.android.server.search.SearchManagerService$Lifecycle");
                    } catch (Throwable th31) {
                        reportWtf("starting Search Service", th31);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                if (!context.getResources().getBoolean(17891671)) {
                    timingsTraceAndSlog.traceBegin("StartWallpaperManagerService");
                    this.mSystemServiceManager.startService("com.android.server.wallpaper.WallpaperManagerService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                } else {
                    Slog.i("SystemServer", "Wallpaper service disabled by config");
                }
                timingsTraceAndSlog.traceBegin("StartWallpaperEffectsGenerationService");
                this.mSystemServiceManager.startService("com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartAudioService");
                if (hasSystemFeature5) {
                    this.mSystemServiceManager.startService(AudioService.Lifecycle.class);
                    networkPolicyManagerService2 = networkPolicyManagerService;
                } else {
                    String string2 = context.getResources().getString(17039915);
                    try {
                        SystemServiceManager systemServiceManager = this.mSystemServiceManager;
                        StringBuilder sb = new StringBuilder();
                        sb.append(string2);
                        networkPolicyManagerService2 = networkPolicyManagerService;
                        try {
                            sb.append("$Lifecycle");
                            systemServiceManager.startService(sb.toString());
                        } catch (Throwable th32) {
                            th = th32;
                            reportWtf("starting " + string2, th);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                            this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDockObserver");
                            this.mSystemServiceManager.startService(DockObserver.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAdbService");
                            this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartUsbService");
                            this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                            ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartColorDisplay");
                            this.mSystemServiceManager.startService(ColorDisplayService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartJobScheduler");
                            this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                            this.mSystemServiceManager.startService(SoundTriggerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartTrustManager");
                            this.mSystemServiceManager.startService(TrustManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                            this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                            this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                            this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                            timingsTraceAndSlog.traceEnd();
                            if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSensorNotification");
                            this.mSystemServiceManager.startService(SensorNotificationService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                            ServiceManager.addService("diskstats", new DiskStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RuntimeService");
                            ServiceManager.addService("runtime", new RuntimeService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            networkTimeUpdateService = null;
                            timingsTraceAndSlog.traceBegin("CertBlacklister");
                            new CertBlacklister(context);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                            this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                            this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDreamManager");
                            this.mSystemServiceManager.startService(DreamManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                            ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (CoverageService.ENABLED) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                            this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                            this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                            this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                            this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInputManager");
                            this.mSystemServiceManager.startService(TvInputManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                            r22 = new MediaRouterService(context);
                            ServiceManager.addService("media_router", (IBinder) r22);
                            MediaRouterService mediaRouterService22222 = r22;
                            timingsTraceAndSlog.traceEnd();
                            hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                            hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                            hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                            if (hasSystemFeature) {
                            }
                            if (hasSystemFeature2) {
                            }
                            if (hasSystemFeature3) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBiometricService");
                            this.mSystemServiceManager.startService(BiometricService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAuthService");
                            this.mSystemServiceManager.startService(AuthService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                            this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                            this.mSystemServiceManager.startService(LauncherAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                            this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartPeopleService");
                            this.mSystemServiceManager.startService(PeopleService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                            this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                            this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                            timingsTraceAndSlog.traceEnd();
                            mediaRouterService = mediaRouterService22222;
                            lifecycle2 = lifecycle;
                            vpnManagerService = vpnManagerService222;
                            iLockSettings3 = iLockSettings2;
                            vcnManagementService = vcnManagementService222;
                            iNetworkManagementService3 = iNetworkManagementService2;
                            networkTimeUpdateService2 = networkTimeUpdateService;
                            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                            this.mSystemServiceManager.startService(IncidentCompanionService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (detectSafeMode) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                            }
                            if (!deviceHasConfigString(context, 17039905)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartClipboardService");
                            this.mSystemServiceManager.startService(ClipboardService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AppServiceManager");
                            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                            this.mSystemServiceManager.startService(TracingServiceProxy.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                            if (iLockSettings3 != null) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                            timingsTraceAndSlog.traceEnd();
                            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                            if (createInstance != null) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                            main.systemReady();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                            LogMteState.register(context);
                            timingsTraceAndSlog.traceEnd();
                            synchronized (SystemService.class) {
                            }
                        }
                    } catch (Throwable th33) {
                        th = th33;
                        networkPolicyManagerService2 = networkPolicyManagerService;
                    }
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartSoundTriggerMiddlewareService");
                this.mSystemServiceManager.startService(SoundTriggerMiddlewareService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                if (this.mPackageManager.hasSystemFeature("android.hardware.broadcastradio")) {
                    timingsTraceAndSlog.traceBegin("StartBroadcastRadioService");
                    this.mSystemServiceManager.startService(BroadcastRadioService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartDockObserver");
                this.mSystemServiceManager.startService(DockObserver.class);
                timingsTraceAndSlog.traceEnd();
                if (hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartThermalObserver");
                    this.mSystemServiceManager.startService("com.android.clockwork.ThermalObserver");
                    timingsTraceAndSlog.traceEnd();
                }
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartWiredAccessoryManager");
                    try {
                        inputManagerService.setWiredAccessoryCallbacks(new WiredAccessoryManager(context, inputManagerService));
                    } catch (Throwable th34) {
                        reportWtf("starting WiredAccessoryManager", th34);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mPackageManager.hasSystemFeature("android.software.midi")) {
                    timingsTraceAndSlog.traceBegin("StartMidiManager");
                    this.mSystemServiceManager.startService("com.android.server.midi.MidiService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartAdbService");
                try {
                    this.mSystemServiceManager.startService("com.android.server.adb.AdbService$Lifecycle");
                } catch (Throwable unused) {
                    Slog.e("SystemServer", "Failure starting AdbService");
                }
                timingsTraceAndSlog.traceEnd();
                if (!this.mPackageManager.hasSystemFeature("android.hardware.usb.host") || this.mPackageManager.hasSystemFeature("android.hardware.usb.accessory") || equals) {
                    timingsTraceAndSlog.traceBegin("StartUsbService");
                    this.mSystemServiceManager.startService("com.android.server.usb.UsbService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                }
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartSerialService");
                    try {
                        ServiceManager.addService("serial", new SerialService(context));
                    } catch (Throwable th35) {
                        Slog.e("SystemServer", "Failure starting SerialService", th35);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartHardwarePropertiesManagerService");
                try {
                    ServiceManager.addService("hardware_properties", new HardwarePropertiesManagerService(context));
                } catch (Throwable th36) {
                    Slog.e("SystemServer", "Failure starting HardwarePropertiesManagerService", th36);
                }
                timingsTraceAndSlog.traceEnd();
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartTwilightService");
                    this.mSystemServiceManager.startService(TwilightService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartColorDisplay");
                this.mSystemServiceManager.startService(ColorDisplayService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartJobScheduler");
                this.mSystemServiceManager.startService("com.android.server.job.JobSchedulerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartSoundTrigger");
                this.mSystemServiceManager.startService(SoundTriggerService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartTrustManager");
                this.mSystemServiceManager.startService(TrustManagerService.class);
                timingsTraceAndSlog.traceEnd();
                if (this.mPackageManager.hasSystemFeature("android.software.backup")) {
                    timingsTraceAndSlog.traceBegin("StartBackupManager");
                    this.mSystemServiceManager.startService("com.android.server.backup.BackupManagerService$Lifecycle");
                    timingsTraceAndSlog.traceEnd();
                }
                if (!this.mPackageManager.hasSystemFeature("android.software.app_widgets") || context.getResources().getBoolean(17891643)) {
                    timingsTraceAndSlog.traceBegin("StartAppWidgetService");
                    this.mSystemServiceManager.startService("com.android.server.appwidget.AppWidgetService");
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartVoiceRecognitionManager");
                this.mSystemServiceManager.startService("com.android.server.voiceinteraction.VoiceInteractionManagerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartAppHibernationService");
                this.mSystemServiceManager.startService("com.android.server.apphibernation.AppHibernationService");
                timingsTraceAndSlog.traceEnd();
                if (GestureLauncherService.isGestureLauncherEnabled(context.getResources())) {
                    timingsTraceAndSlog.traceBegin("StartGestureLauncher");
                    this.mSystemServiceManager.startService(GestureLauncherService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartSensorNotification");
                this.mSystemServiceManager.startService(SensorNotificationService.class);
                timingsTraceAndSlog.traceEnd();
                if (this.mPackageManager.hasSystemFeature("android.hardware.context_hub")) {
                    timingsTraceAndSlog.traceBegin("StartContextHubSystemService");
                    this.mSystemServiceManager.startService(ContextHubSystemService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartDiskStatsService");
                try {
                    ServiceManager.addService("diskstats", new DiskStatsService(context));
                } catch (Throwable th37) {
                    reportWtf("starting DiskStats Service", th37);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("RuntimeService");
                try {
                    ServiceManager.addService("runtime", new RuntimeService(context));
                } catch (Throwable th38) {
                    reportWtf("starting RuntimeService", th38);
                }
                timingsTraceAndSlog.traceEnd();
                if (!hasSystemFeature4 || z2) {
                    networkTimeUpdateService = null;
                } else {
                    timingsTraceAndSlog.traceBegin("StartNetworkTimeUpdateService");
                    try {
                        networkTimeUpdateService = new NetworkTimeUpdateService(context);
                        try {
                            ServiceManager.addService("network_time_update_service", networkTimeUpdateService);
                        } catch (Throwable th39) {
                            th = th39;
                            reportWtf("starting NetworkTimeUpdate service", th);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("CertBlacklister");
                            new CertBlacklister(context);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                            this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                            this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartDreamManager");
                            this.mSystemServiceManager.startService(DreamManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                            ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                            timingsTraceAndSlog.traceEnd();
                            if (CoverageService.ENABLED) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                            this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                            this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                            this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                            this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!this.mPackageManager.hasSystemFeature("android.software.live_tv")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartTvInputManager");
                            this.mSystemServiceManager.startService(TvInputManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                            r22 = new MediaRouterService(context);
                            ServiceManager.addService("media_router", (IBinder) r22);
                            MediaRouterService mediaRouterService222222 = r22;
                            timingsTraceAndSlog.traceEnd();
                            hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                            hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                            hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                            if (hasSystemFeature) {
                            }
                            if (hasSystemFeature2) {
                            }
                            if (hasSystemFeature3) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBiometricService");
                            this.mSystemServiceManager.startService(BiometricService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAuthService");
                            this.mSystemServiceManager.startService(AuthService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (!hasSystemFeature4) {
                            }
                            if (!hasSystemFeature4) {
                            }
                            timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                            this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                            this.mSystemServiceManager.startService(LauncherAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                            this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartPeopleService");
                            this.mSystemServiceManager.startService(PeopleService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                            this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                            this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                            timingsTraceAndSlog.traceEnd();
                            mediaRouterService = mediaRouterService222222;
                            lifecycle2 = lifecycle;
                            vpnManagerService = vpnManagerService222;
                            iLockSettings3 = iLockSettings2;
                            vcnManagementService = vcnManagementService222;
                            iNetworkManagementService3 = iNetworkManagementService2;
                            networkTimeUpdateService2 = networkTimeUpdateService;
                            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
                            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
                            timingsTraceAndSlog.traceEnd();
                            if (hasSystemFeature4) {
                            }
                            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                            }
                            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
                            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
                            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
                            this.mSystemServiceManager.startService(IncidentCompanionService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
                            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
                            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
                            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
                            timingsTraceAndSlog.traceEnd();
                            if (detectSafeMode) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                            }
                            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                            }
                            if (!deviceHasConfigString(context, 17039905)) {
                            }
                            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
                            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartClipboardService");
                            this.mSystemServiceManager.startService(ClipboardService.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("AppServiceManager");
                            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
                            this.mSystemServiceManager.startService(TracingServiceProxy.class);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
                            if (iLockSettings3 != null) {
                            }
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
                            timingsTraceAndSlog.traceEnd();
                            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
                            if (createInstance != null) {
                            }
                            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
                            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
                            main.systemReady();
                            timingsTraceAndSlog.traceEnd();
                            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
                            LogMteState.register(context);
                            timingsTraceAndSlog.traceEnd();
                            synchronized (SystemService.class) {
                            }
                        }
                    } catch (Throwable th40) {
                        th = th40;
                        networkTimeUpdateService = null;
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("CertBlacklister");
                try {
                    new CertBlacklister(context);
                } catch (Throwable th41) {
                    reportWtf("starting CertBlacklister", th41);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartEmergencyAffordanceService");
                this.mSystemServiceManager.startService(EmergencyAffordanceService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("startBlobStoreManagerService");
                this.mSystemServiceManager.startService("com.android.server.blob.BlobStoreManagerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartDreamManager");
                this.mSystemServiceManager.startService(DreamManagerService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("AddGraphicsStatsService");
                ServiceManager.addService("graphicsstats", new GraphicsStatsService(context));
                timingsTraceAndSlog.traceEnd();
                if (CoverageService.ENABLED) {
                    timingsTraceAndSlog.traceBegin("AddCoverageService");
                    ServiceManager.addService("coverage", new CoverageService());
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mPackageManager.hasSystemFeature("android.software.print")) {
                    timingsTraceAndSlog.traceBegin("StartPrintManager");
                    this.mSystemServiceManager.startService("com.android.server.print.PrintManagerService");
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartAttestationVerificationService");
                this.mSystemServiceManager.startService(AttestationVerificationManagerService.class);
                timingsTraceAndSlog.traceEnd();
                if (this.mPackageManager.hasSystemFeature("android.software.companion_device_setup")) {
                    timingsTraceAndSlog.traceBegin("StartCompanionDeviceManager");
                    this.mSystemServiceManager.startService("com.android.server.companion.CompanionDeviceManagerService");
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("StartVirtualDeviceManager");
                    this.mSystemServiceManager.startService("com.android.server.companion.virtual.VirtualDeviceManagerService");
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartRestrictionManager");
                this.mSystemServiceManager.startService(RestrictionsManagerService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartMediaSessionService");
                this.mSystemServiceManager.startService("com.android.server.media.MediaSessionService");
                timingsTraceAndSlog.traceEnd();
                if (this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
                    timingsTraceAndSlog.traceBegin("StartHdmiControlService");
                    this.mSystemServiceManager.startService(HdmiControlService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (!this.mPackageManager.hasSystemFeature("android.software.live_tv") || this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                    timingsTraceAndSlog.traceBegin("StartTvInteractiveAppManager");
                    this.mSystemServiceManager.startService(TvInteractiveAppManagerService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (!this.mPackageManager.hasSystemFeature("android.software.live_tv") || this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                    timingsTraceAndSlog.traceBegin("StartTvInputManager");
                    this.mSystemServiceManager.startService(TvInputManagerService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mPackageManager.hasSystemFeature("android.hardware.tv.tuner")) {
                    timingsTraceAndSlog.traceBegin("StartTunerResourceManager");
                    this.mSystemServiceManager.startService(TunerResourceManagerService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
                    timingsTraceAndSlog.traceBegin("StartMediaResourceMonitor");
                    this.mSystemServiceManager.startService("com.android.server.media.MediaResourceMonitorService");
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mPackageManager.hasSystemFeature("android.software.leanback")) {
                    timingsTraceAndSlog.traceBegin("StartTvRemoteService");
                    this.mSystemServiceManager.startService(TvRemoteService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartMediaRouterService");
                try {
                    r22 = new MediaRouterService(context);
                    ServiceManager.addService("media_router", (IBinder) r22);
                    MediaRouterService mediaRouterService2222222 = r22;
                } catch (Throwable th42) {
                    th = th42;
                    r22 = 0;
                }
                timingsTraceAndSlog.traceEnd();
                hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face");
                hasSystemFeature2 = this.mPackageManager.hasSystemFeature("android.hardware.biometrics.iris");
                hasSystemFeature3 = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
                if (hasSystemFeature) {
                    timingsTraceAndSlog.traceBegin("StartFaceSensor");
                    FaceService faceService = (FaceService) this.mSystemServiceManager.startService(FaceService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (hasSystemFeature2) {
                    timingsTraceAndSlog.traceBegin("StartIrisSensor");
                    this.mSystemServiceManager.startService(IrisService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                if (hasSystemFeature3) {
                    timingsTraceAndSlog.traceBegin("StartFingerprintSensor");
                    FingerprintService fingerprintService = (FingerprintService) this.mSystemServiceManager.startService(FingerprintService.class);
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartBiometricService");
                this.mSystemServiceManager.startService(BiometricService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartAuthService");
                this.mSystemServiceManager.startService(AuthService.class);
                timingsTraceAndSlog.traceEnd();
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartDynamicCodeLoggingService");
                    try {
                        DynamicCodeLoggingService.schedule(context);
                    } catch (Throwable th43) {
                        reportWtf("starting DynamicCodeLoggingService", th43);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                if (!hasSystemFeature4) {
                    timingsTraceAndSlog.traceBegin("StartPruneInstantAppsJobService");
                    try {
                        PruneInstantAppsJobService.schedule(context);
                    } catch (Throwable th44) {
                        reportWtf("StartPruneInstantAppsJobService", th44);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                timingsTraceAndSlog.traceBegin("StartShortcutServiceLifecycle");
                this.mSystemServiceManager.startService(ShortcutService.Lifecycle.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartLauncherAppsService");
                this.mSystemServiceManager.startService(LauncherAppsService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartCrossProfileAppsService");
                this.mSystemServiceManager.startService(CrossProfileAppsService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartPeopleService");
                this.mSystemServiceManager.startService(PeopleService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartMediaMetricsManager");
                this.mSystemServiceManager.startService(MediaMetricsManagerService.class);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartBackgroundInstallControlService");
                this.mSystemServiceManager.startService(BackgroundInstallControlService.class);
                timingsTraceAndSlog.traceEnd();
                mediaRouterService = mediaRouterService2222222;
                lifecycle2 = lifecycle;
                vpnManagerService = vpnManagerService222;
                iLockSettings3 = iLockSettings2;
                vcnManagementService = vcnManagementService222;
                iNetworkManagementService3 = iNetworkManagementService2;
                networkTimeUpdateService2 = networkTimeUpdateService;
            }
            timingsTraceAndSlog.traceBegin("StartMediaProjectionManager");
            this.mSystemServiceManager.startService(MediaProjectionManagerService.class);
            timingsTraceAndSlog.traceEnd();
            if (hasSystemFeature4) {
                timingsTraceAndSlog.traceBegin("StartWearPowerService");
                this.mSystemServiceManager.startService("com.android.clockwork.power.WearPowerService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartHealthService");
                this.mSystemServiceManager.startService("com.android.clockwork.healthservices.HealthService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartWearConnectivityService");
                this.mSystemServiceManager.startService("com.android.clockwork.connectivity.WearConnectivityService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartWearDisplayService");
                this.mSystemServiceManager.startService("com.android.clockwork.display.WearDisplayService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartWearTimeService");
                this.mSystemServiceManager.startService("com.android.clockwork.time.WearTimeService");
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("StartWearGlobalActionsService");
                this.mSystemServiceManager.startService("com.android.clockwork.globalactions.GlobalActionsService");
                timingsTraceAndSlog.traceEnd();
            }
            if (!this.mPackageManager.hasSystemFeature("android.software.slices_disabled")) {
                timingsTraceAndSlog.traceBegin("StartSliceManagerService");
                this.mSystemServiceManager.startService("com.android.server.slice.SliceManagerService$Lifecycle");
                timingsTraceAndSlog.traceEnd();
            }
            if (context.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
                timingsTraceAndSlog.traceBegin("StartIoTSystemService");
                this.mSystemServiceManager.startService("com.android.things.server.IoTSystemService");
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartStatsCompanion");
            this.mSystemServiceManager.startServiceFromJar("com.android.server.stats.StatsCompanion$Lifecycle", "/apex/com.android.os.statsd/javalib/service-statsd.jar");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartRebootReadinessManagerService");
            this.mSystemServiceManager.startServiceFromJar("com.android.server.scheduling.RebootReadinessManagerService$Lifecycle", "/apex/com.android.scheduling/javalib/service-scheduling.jar");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartStatsPullAtomService");
            this.mSystemServiceManager.startService("com.android.server.stats.pull.StatsPullAtomService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StatsBootstrapAtomService");
            this.mSystemServiceManager.startService("com.android.server.stats.bootstrap.StatsBootstrapAtomService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartIncidentCompanionService");
            this.mSystemServiceManager.startService(IncidentCompanionService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StarSdkSandboxManagerService");
            this.mSystemServiceManager.startService("com.android.server.sdksandbox.SdkSandboxManagerService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartAdServicesManagerService");
            this.mSystemServiceManager.startService("com.android.server.adservices.AdServicesManagerService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartOnDevicePersonalizationSystemService");
            this.mSystemServiceManager.startService("com.android.server.ondevicepersonalization.OnDevicePersonalizationSystemService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            if (detectSafeMode) {
                this.mActivityManagerService.enterSafeMode();
            }
            if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
                mmsServiceBroker = null;
            } else {
                timingsTraceAndSlog.traceBegin("StartMmsService");
                timingsTraceAndSlog.traceEnd();
                mmsServiceBroker = (MmsServiceBroker) this.mSystemServiceManager.startService(MmsServiceBroker.class);
            }
            if (this.mPackageManager.hasSystemFeature("android.software.autofill")) {
                timingsTraceAndSlog.traceBegin("StartAutoFillService");
                this.mSystemServiceManager.startService("com.android.server.autofill.AutofillManagerService");
                timingsTraceAndSlog.traceEnd();
            }
            if (this.mPackageManager.hasSystemFeature("android.software.credentials")) {
                if (DeviceConfig.getBoolean("credential_manager", "enable_credential_manager", true)) {
                    timingsTraceAndSlog.traceBegin("StartCredentialManagerService");
                    this.mSystemServiceManager.startService("com.android.server.credentials.CredentialManagerService");
                    timingsTraceAndSlog.traceEnd();
                } else {
                    Slog.d("SystemServer", "CredentialManager disabled.");
                }
            }
            if (!deviceHasConfigString(context, 17039905)) {
                timingsTraceAndSlog.traceBegin("StartTranslationManagerService");
                this.mSystemServiceManager.startService("com.android.server.translation.TranslationManagerService");
                timingsTraceAndSlog.traceEnd();
            } else {
                Slog.d("SystemServer", "TranslationService not defined by OEM");
            }
            timingsTraceAndSlog.traceBegin("StartSelectionToolbarManagerService");
            this.mSystemServiceManager.startService("com.android.server.selectiontoolbar.SelectionToolbarManagerService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartClipboardService");
            this.mSystemServiceManager.startService(ClipboardService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("AppServiceManager");
            this.mSystemServiceManager.startService(AppBindingService.Lifecycle.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("startTracingServiceProxy");
            this.mSystemServiceManager.startService(TracingServiceProxy.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("MakeLockSettingsServiceReady");
            if (iLockSettings3 != null) {
                try {
                    iLockSettings3.systemReady();
                } catch (Throwable th45) {
                    reportWtf("making Lock Settings Service ready", th45);
                }
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartBootPhaseLockSettingsReady");
            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_LOCK_SETTINGS_READY);
            timingsTraceAndSlog.traceEnd();
            createInstance = HsumBootUserInitializer.createInstance(this.mActivityManagerService, this.mContentResolver, context.getResources().getBoolean(17891710));
            if (createInstance != null) {
                timingsTraceAndSlog.traceBegin("HsumBootUserInitializer.init");
                createInstance.init(timingsTraceAndSlog);
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartBootPhaseSystemServicesReady");
            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 500);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("MakeWindowManagerServiceReady");
            main.systemReady();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("RegisterLogMteState");
            LogMteState.register(context);
            timingsTraceAndSlog.traceEnd();
            synchronized (SystemService.class) {
                LinkedList<Pair<String, ApplicationErrorReport.CrashInfo>> linkedList = sPendingWtfs;
                if (linkedList != null) {
                    this.mActivityManagerService.schedulePendingSystemServerWtfs(linkedList);
                    sPendingWtfs = null;
                }
            }
            if (detectSafeMode) {
                this.mActivityManagerService.showSafeModeOverlay();
            }
            Configuration computeNewConfiguration = main.computeNewConfiguration(0);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            context.getDisplay().getMetrics(displayMetrics);
            context.getResources().updateConfiguration(computeNewConfiguration, displayMetrics);
            Resources.Theme theme = context.getTheme();
            if (theme.getChangingConfigurations() != 0) {
                theme.rebase();
            }
            timingsTraceAndSlog.traceBegin("StartPermissionPolicyService");
            this.mSystemServiceManager.startService(PermissionPolicyService.class);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("MakePackageManagerServiceReady");
            this.mPackageManagerService.systemReady();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("MakeDisplayManagerServiceReady");
            try {
                this.mDisplayManagerService.systemReady(detectSafeMode);
            } catch (Throwable th46) {
                reportWtf("making Display Manager Service ready", th46);
            }
            timingsTraceAndSlog.traceEnd();
            this.mSystemServiceManager.setSafeMode(detectSafeMode);
            timingsTraceAndSlog.traceBegin("StartDeviceSpecificServices");
            String[] stringArray = this.mSystemContext.getResources().getStringArray(17236026);
            int length = stringArray.length;
            int i2 = 0;
            while (i2 < length) {
                String str = stringArray[i2];
                StringBuilder sb2 = new StringBuilder();
                String[] strArr = stringArray;
                sb2.append("StartDeviceSpecificServices ");
                sb2.append(str);
                timingsTraceAndSlog.traceBegin(sb2.toString());
                try {
                    this.mSystemServiceManager.startService(str);
                    i = length;
                } catch (Throwable th47) {
                    StringBuilder sb3 = new StringBuilder();
                    i = length;
                    sb3.append("starting ");
                    sb3.append(str);
                    reportWtf(sb3.toString(), th47);
                }
                timingsTraceAndSlog.traceEnd();
                i2++;
                stringArray = strArr;
                length = i;
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("GameManagerService");
            this.mSystemServiceManager.startService("com.android.server.app.GameManagerService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            if (context.getPackageManager().hasSystemFeature("android.hardware.uwb")) {
                timingsTraceAndSlog.traceBegin("UwbService");
                this.mSystemServiceManager.startServiceFromJar("com.android.server.uwb.UwbService", "/apex/com.android.uwb/javalib/service-uwb.jar");
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartBootPhaseDeviceSpecificServicesReady");
            this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_DEVICE_SPECIFIC_SERVICES_READY);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartSafetyCenterService");
            this.mSystemServiceManager.startService("com.android.safetycenter.SafetyCenterService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("AppSearchModule");
            this.mSystemServiceManager.startService("com.android.server.appsearch.AppSearchModule$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            if (SystemProperties.getBoolean("ro.config.isolated_compilation_enabled", false)) {
                timingsTraceAndSlog.traceBegin("IsolatedCompilationService");
                this.mSystemServiceManager.startService("com.android.server.compos.IsolatedCompilationService");
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("StartMediaCommunicationService");
            this.mSystemServiceManager.startService("com.android.server.media.MediaCommunicationService");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("AppCompatOverridesService");
            this.mSystemServiceManager.startService("com.android.server.compat.overrides.AppCompatOverridesService$Lifecycle");
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("HealthConnectManagerService");
            this.mSystemServiceManager.startService("com.android.server.healthconnect.HealthConnectManagerService");
            timingsTraceAndSlog.traceEnd();
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            final NetworkPolicyManagerService networkPolicyManagerService3 = networkPolicyManagerService2;
            final VpnManagerService vpnManagerService3 = vpnManagerService;
            final VcnManagementService vcnManagementService4 = vcnManagementService;
            final CountryDetectorService countryDetectorService2 = obj2;
            final NetworkTimeUpdateService networkTimeUpdateService3 = networkTimeUpdateService2;
            final MediaRouterService mediaRouterService3 = mediaRouterService;
            final MmsServiceBroker mmsServiceBroker2 = mmsServiceBroker;
            this.mActivityManagerService.systemReady(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    SystemServer.this.lambda$startOtherServices$6(timingsTraceAndSlog, lifecycle2, hasSystemFeature4, context, detectSafeMode, connectivityManager, iNetworkManagementService3, networkPolicyManagerService3, vpnManagerService3, vcnManagementService4, createInstance, countryDetectorService2, networkTimeUpdateService3, inputManagerService, telephonyRegistry, mediaRouterService3, mmsServiceBroker2);
                }
            }, timingsTraceAndSlog);
            timingsTraceAndSlog.traceBegin("LockSettingsThirdPartyAppsStarted");
            LockSettingsInternal lockSettingsInternal = (LockSettingsInternal) LocalServices.getService(LockSettingsInternal.class);
            if (lockSettingsInternal != null) {
                lockSettingsInternal.onThirdPartyAppsStarted();
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("StartSystemUI");
            try {
                startSystemUi(context, main);
            } catch (Throwable th48) {
                reportWtf("starting System UI", th48);
            }
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceEnd();
        } catch (Throwable th49) {
            Slog.e("System", "******************************************");
            Slog.e("System", "************ Failure starting core service");
            throw th49;
        }
    }

    public static /* synthetic */ void lambda$startOtherServices$1() {
        try {
            Slog.i("SystemServer", "SecondaryZygotePreload");
            TimingsTraceAndSlog newAsyncLog = TimingsTraceAndSlog.newAsyncLog();
            newAsyncLog.traceBegin("SecondaryZygotePreload");
            String[] strArr = Build.SUPPORTED_32_BIT_ABIS;
            if (strArr.length > 0 && !Process.ZYGOTE_PROCESS.preloadDefault(strArr[0])) {
                Slog.e("SystemServer", "Unable to preload default resources for secondary");
            }
            newAsyncLog.traceEnd();
        } catch (Exception e) {
            Slog.e("SystemServer", "Exception preloading default resources", e);
        }
    }

    public static /* synthetic */ void lambda$startOtherServices$2() {
        TimingsTraceAndSlog newAsyncLog = TimingsTraceAndSlog.newAsyncLog();
        newAsyncLog.traceBegin("StartISensorManagerService");
        startISensorManagerService();
        newAsyncLog.traceEnd();
    }

    public static /* synthetic */ void lambda$startOtherServices$3() {
        TimingsTraceAndSlog newAsyncLog = TimingsTraceAndSlog.newAsyncLog();
        newAsyncLog.traceBegin("StartHidlServices");
        startHidlServices();
        newAsyncLog.traceEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOtherServices$6(TimingsTraceAndSlog timingsTraceAndSlog, DevicePolicyManagerService.Lifecycle lifecycle, boolean z, Context context, boolean z2, ConnectivityManager connectivityManager, NetworkManagementService networkManagementService, NetworkPolicyManagerService networkPolicyManagerService, VpnManagerService vpnManagerService, VcnManagementService vcnManagementService, HsumBootUserInitializer hsumBootUserInitializer, CountryDetectorService countryDetectorService, NetworkTimeUpdateService networkTimeUpdateService, InputManagerService inputManagerService, TelephonyRegistry telephonyRegistry, MediaRouterService mediaRouterService, MmsServiceBroker mmsServiceBroker) {
        Slog.i("SystemServer", "Making services ready");
        timingsTraceAndSlog.traceBegin("StartActivityManagerReadyPhase");
        this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, SystemService.PHASE_ACTIVITY_MANAGER_READY);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartObservingNativeCrashes");
        try {
            this.mActivityManagerService.startObservingNativeCrashes();
        } catch (Throwable th) {
            reportWtf("observing native crashes", th);
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("RegisterAppOpsPolicy");
        try {
            this.mActivityManagerService.setAppOpsPolicy(new AppOpsPolicy(this.mSystemContext));
        } catch (Throwable th2) {
            reportWtf("registering app ops policy", th2);
        }
        timingsTraceAndSlog.traceEnd();
        Future<?> submit = this.mWebViewUpdateService != null ? SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                SystemServer.this.lambda$startOtherServices$4();
            }
        }, "WebViewFactoryPreparation") : null;
        boolean hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.type.automotive");
        if (hasSystemFeature) {
            timingsTraceAndSlog.traceBegin("StartCarServiceHelperService");
            Dumpable startService = this.mSystemServiceManager.startService("com.android.internal.car.CarServiceHelperService");
            if (startService instanceof Dumpable) {
                this.mDumper.addDumpable(startService);
            }
            if (startService instanceof DevicePolicySafetyChecker) {
                lifecycle.setDevicePolicySafetyChecker((DevicePolicySafetyChecker) startService);
            }
            timingsTraceAndSlog.traceEnd();
        }
        if (z) {
            timingsTraceAndSlog.traceBegin("StartWearService");
            String string = context.getString(17040025);
            if (!TextUtils.isEmpty(string)) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
                if (unflattenFromString != null) {
                    Intent intent = new Intent();
                    intent.setComponent(unflattenFromString);
                    intent.addFlags(256);
                    context.startServiceAsUser(intent, UserHandle.SYSTEM);
                } else {
                    Slog.d("SystemServer", "Null wear service component name.");
                }
            }
            timingsTraceAndSlog.traceEnd();
        }
        if (z2) {
            timingsTraceAndSlog.traceBegin("EnableAirplaneModeInSafeMode");
            try {
                connectivityManager.setAirplaneMode(true);
            } catch (Throwable th3) {
                reportWtf("enabling Airplane Mode during Safe Mode bootup", th3);
            }
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceBegin("MakeNetworkManagementServiceReady");
        if (networkManagementService != null) {
            try {
                networkManagementService.systemReady();
            } catch (Throwable th4) {
                reportWtf("making Network Managment Service ready", th4);
            }
        }
        CountDownLatch networkScoreAndNetworkManagementServiceReady = networkPolicyManagerService != null ? networkPolicyManagerService.networkScoreAndNetworkManagementServiceReady() : null;
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeConnectivityServiceReady");
        if (connectivityManager != null) {
            try {
                connectivityManager.systemReady();
            } catch (Throwable th5) {
                reportWtf("making Connectivity Service ready", th5);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeVpnManagerServiceReady");
        if (vpnManagerService != null) {
            try {
                vpnManagerService.systemReady();
            } catch (Throwable th6) {
                reportWtf("making VpnManagerService ready", th6);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeVcnManagementServiceReady");
        if (vcnManagementService != null) {
            try {
                vcnManagementService.systemReady();
            } catch (Throwable th7) {
                reportWtf("making VcnManagementService ready", th7);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeNetworkPolicyServiceReady");
        if (networkPolicyManagerService != null) {
            try {
                networkPolicyManagerService.systemReady(networkScoreAndNetworkManagementServiceReady);
            } catch (Throwable th8) {
                reportWtf("making Network Policy Service ready", th8);
            }
        }
        timingsTraceAndSlog.traceEnd();
        this.mPackageManagerService.waitForAppDataPrepared();
        timingsTraceAndSlog.traceBegin("PhaseThirdPartyAppsCanStart");
        if (submit != null) {
            ConcurrentUtils.waitForFutureNoInterrupt(submit, "WebViewFactoryPreparation");
        }
        this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 600);
        timingsTraceAndSlog.traceEnd();
        if (hsumBootUserInitializer != null && !hasSystemFeature) {
            timingsTraceAndSlog.traceBegin("HsumBootUserInitializer.systemRunning");
            hsumBootUserInitializer.systemRunning(timingsTraceAndSlog);
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceBegin("StartNetworkStack");
        try {
            NetworkStackClient.getInstance().start();
        } catch (Throwable th9) {
            reportWtf("starting Network Stack", th9);
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("StartTethering");
        try {
            ConnectivityModuleConnector.getInstance().startModuleService("android.net.ITetheringConnector", "android.permission.MAINLINE_NETWORK_STACK", new ConnectivityModuleConnector.ModuleServiceCallback() { // from class: com.android.server.SystemServer$$ExternalSyntheticLambda8
                @Override // android.net.ConnectivityModuleConnector.ModuleServiceCallback
                public final void onModuleServiceConnected(IBinder iBinder) {
                    ServiceManager.addService("tethering", iBinder, false, 6);
                }
            });
        } catch (Throwable th10) {
            reportWtf("starting Tethering", th10);
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeCountryDetectionServiceReady");
        if (countryDetectorService != null) {
            try {
                countryDetectorService.systemRunning();
            } catch (Throwable th11) {
                reportWtf("Notifying CountryDetectorService running", th11);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeNetworkTimeUpdateReady");
        if (networkTimeUpdateService != null) {
            try {
                networkTimeUpdateService.systemRunning();
            } catch (Throwable th12) {
                reportWtf("Notifying NetworkTimeService running", th12);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeInputManagerServiceReady");
        if (inputManagerService != null) {
            try {
                inputManagerService.systemRunning();
            } catch (Throwable th13) {
                reportWtf("Notifying InputManagerService running", th13);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeTelephonyRegistryReady");
        if (telephonyRegistry != null) {
            try {
                telephonyRegistry.systemRunning();
            } catch (Throwable th14) {
                reportWtf("Notifying TelephonyRegistry running", th14);
            }
        }
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("MakeMediaRouterServiceReady");
        if (mediaRouterService != null) {
            try {
                mediaRouterService.systemRunning();
            } catch (Throwable th15) {
                reportWtf("Notifying MediaRouterService running", th15);
            }
        }
        timingsTraceAndSlog.traceEnd();
        if (this.mPackageManager.hasSystemFeature("android.hardware.telephony")) {
            timingsTraceAndSlog.traceBegin("MakeMmsServiceReady");
            if (mmsServiceBroker != null) {
                try {
                    mmsServiceBroker.systemRunning();
                } catch (Throwable th16) {
                    reportWtf("Notifying MmsService running", th16);
                }
            }
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceBegin("IncidentDaemonReady");
        try {
            IIncidentManager asInterface = IIncidentManager.Stub.asInterface(ServiceManager.getService("incident"));
            if (asInterface != null) {
                asInterface.systemRunning();
            }
        } catch (Throwable th17) {
            reportWtf("Notifying incident daemon running", th17);
        }
        timingsTraceAndSlog.traceEnd();
        if (this.mIncrementalServiceHandle != 0) {
            timingsTraceAndSlog.traceBegin("MakeIncrementalServiceReady");
            setIncrementalServiceSystemReady(this.mIncrementalServiceHandle);
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceBegin("OdsignStatsLogger");
        try {
            OdsignStatsLogger.triggerStatsWrite();
        } catch (Throwable th18) {
            reportWtf("Triggering OdsignStatsLogger", th18);
        }
        timingsTraceAndSlog.traceEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOtherServices$4() {
        Slog.i("SystemServer", "WebViewFactoryPreparation");
        TimingsTraceAndSlog newAsyncLog = TimingsTraceAndSlog.newAsyncLog();
        newAsyncLog.traceBegin("WebViewFactoryPreparation");
        ConcurrentUtils.waitForFutureNoInterrupt(this.mZygotePreload, "Zygote preload");
        this.mZygotePreload = null;
        this.mWebViewUpdateService.prepareWebViewInSystemServer();
        newAsyncLog.traceEnd();
    }

    public final void startApexServices(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("startApexServices");
        for (ApexSystemServiceInfo apexSystemServiceInfo : ApexManager.getInstance().getApexSystemServices()) {
            String name = apexSystemServiceInfo.getName();
            String jarPath = apexSystemServiceInfo.getJarPath();
            timingsTraceAndSlog.traceBegin("starting " + name);
            if (TextUtils.isEmpty(jarPath)) {
                this.mSystemServiceManager.startService(name);
            } else {
                this.mSystemServiceManager.startServiceFromJar(name, jarPath);
            }
            timingsTraceAndSlog.traceEnd();
        }
        this.mSystemServiceManager.sealStartedServices();
        timingsTraceAndSlog.traceEnd();
    }

    public final void updateWatchdogTimeout(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("UpdateWatchdogTimeout");
        Watchdog.getInstance().registerSettingsObserver(this.mSystemContext);
        timingsTraceAndSlog.traceEnd();
    }

    public final boolean deviceHasConfigString(Context context, int i) {
        return !TextUtils.isEmpty(context.getString(i));
    }

    public final void startSystemCaptionsManagerService(Context context, TimingsTraceAndSlog timingsTraceAndSlog) {
        if (!deviceHasConfigString(context, 17039903)) {
            Slog.d("SystemServer", "SystemCaptionsManagerService disabled because resource is not overlaid");
            return;
        }
        timingsTraceAndSlog.traceBegin("StartSystemCaptionsManagerService");
        this.mSystemServiceManager.startService("com.android.server.systemcaptions.SystemCaptionsManagerService");
        timingsTraceAndSlog.traceEnd();
    }

    public final void startTextToSpeechManagerService(Context context, TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("StartTextToSpeechManagerService");
        this.mSystemServiceManager.startService("com.android.server.texttospeech.TextToSpeechManagerService");
        timingsTraceAndSlog.traceEnd();
    }

    public final void startContentCaptureService(Context context, TimingsTraceAndSlog timingsTraceAndSlog) {
        boolean z;
        ActivityManagerService activityManagerService;
        String property = DeviceConfig.getProperty("content_capture", "service_explicitly_enabled");
        if (property == null || property.equalsIgnoreCase("default")) {
            z = false;
        } else {
            z = Boolean.parseBoolean(property);
            if (z) {
                Slog.d("SystemServer", "ContentCaptureService explicitly enabled by DeviceConfig");
            } else {
                Slog.d("SystemServer", "ContentCaptureService explicitly disabled by DeviceConfig");
                return;
            }
        }
        if (!z && !deviceHasConfigString(context, 17039878)) {
            Slog.d("SystemServer", "ContentCaptureService disabled because resource is not overlaid");
            return;
        }
        timingsTraceAndSlog.traceBegin("StartContentCaptureService");
        this.mSystemServiceManager.startService("com.android.server.contentcapture.ContentCaptureManagerService");
        ContentCaptureManagerInternal contentCaptureManagerInternal = (ContentCaptureManagerInternal) LocalServices.getService(ContentCaptureManagerInternal.class);
        if (contentCaptureManagerInternal != null && (activityManagerService = this.mActivityManagerService) != null) {
            activityManagerService.setContentCaptureManager(contentCaptureManagerInternal);
        }
        timingsTraceAndSlog.traceEnd();
    }

    public final void startAttentionService(Context context, TimingsTraceAndSlog timingsTraceAndSlog) {
        if (!AttentionManagerService.isServiceConfigured(context)) {
            Slog.d("SystemServer", "AttentionService is not configured on this device");
            return;
        }
        timingsTraceAndSlog.traceBegin("StartAttentionManagerService");
        this.mSystemServiceManager.startService(AttentionManagerService.class);
        timingsTraceAndSlog.traceEnd();
    }

    public final void startRotationResolverService(Context context, TimingsTraceAndSlog timingsTraceAndSlog) {
        if (!RotationResolverManagerService.isServiceConfigured(context)) {
            Slog.d("SystemServer", "RotationResolverService is not configured on this device");
            return;
        }
        timingsTraceAndSlog.traceBegin("StartRotationResolverService");
        this.mSystemServiceManager.startService(RotationResolverManagerService.class);
        timingsTraceAndSlog.traceEnd();
    }

    public final void startAmbientContextService(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("StartAmbientContextService");
        this.mSystemServiceManager.startService(AmbientContextManagerService.class);
        timingsTraceAndSlog.traceEnd();
    }

    public final void startWearableSensingService(TimingsTraceAndSlog timingsTraceAndSlog) {
        timingsTraceAndSlog.traceBegin("startWearableSensingService");
        this.mSystemServiceManager.startService(WearableSensingManagerService.class);
        timingsTraceAndSlog.traceEnd();
    }

    public static void startSystemUi(Context context, WindowManagerService windowManagerService) {
        Intent intent = new Intent();
        intent.setComponent(((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getSystemUiServiceComponent());
        intent.addFlags(256);
        context.startServiceAsUser(intent, UserHandle.SYSTEM);
        windowManagerService.onSystemUiStarted();
    }

    public static boolean handleEarlySystemWtf(IBinder iBinder, String str, boolean z, ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo, int i) {
        int myPid = Process.myPid();
        com.android.server.p006am.EventLogTags.writeAmWtf(UserHandle.getUserId(1000), myPid, "system_server", -1, str, parcelableCrashInfo.exceptionMessage);
        FrameworkStatsLog.write(80, 1000, str, "system_server", myPid, 3);
        synchronized (SystemServer.class) {
            if (sPendingWtfs == null) {
                sPendingWtfs = new LinkedList<>();
            }
            sPendingWtfs.add(new Pair<>(str, parcelableCrashInfo));
        }
        return false;
    }
}
