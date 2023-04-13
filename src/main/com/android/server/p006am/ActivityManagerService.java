package com.android.server.p006am;

import android.annotation.PermissionMethod;
import android.annotation.PermissionName;
import android.app.ActivityClient;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.ActivityThread;
import android.app.AnrController;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.app.ApplicationErrorReport;
import android.app.ApplicationExitInfo;
import android.app.ApplicationStartInfo;
import android.app.BackgroundStartPrivileges;
import android.app.BroadcastOptions;
import android.app.ContentProviderHolder;
import android.app.ContextImpl;
import android.app.ForegroundServiceDelegationOptions;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.IApplicationStartInfoCompleteListener;
import android.app.IApplicationThread;
import android.app.IForegroundServiceObserver;
import android.app.IInstrumentationWatcher;
import android.app.INotificationManager;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.ITaskStackListener;
import android.app.IUiAutomationConnection;
import android.app.IUidObserver;
import android.app.IUnsafeIntentStrictModeCallback;
import android.app.IUserSwitchObserver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntentStats;
import android.app.ProcessMemoryState;
import android.app.ProfilerInfo;
import android.app.ServiceStartNotAllowedException;
import android.app.SyncNotedAppOp;
import android.app.WaitResult;
import android.app.assist.ActivityId;
import android.app.backup.IBackupManager;
import android.app.compat.CompatChanges;
import android.app.usage.UsageStatsManagerInternal;
import android.appwidget.AppWidgetManagerInternal;
import android.content.AttributionSource;
import android.content.AutofillOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.LocusId;
import android.content.ServiceConnection;
import android.content.p000pm.PackageManagerInternal;
import android.content.p000pm.TestUtilityService;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityPresentationInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IncrementalStatesInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProcessInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ProviderInfoList;
import android.content.pm.ResolveInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.UserInfo;
import android.content.pm.VersionedPackage;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.net.INetd;
import android.net.Uri;
import android.os.AppZygote;
import android.os.Binder;
import android.os.BinderProxy;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.FactoryTest;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.IDeviceIdentifiersPolicyService;
import android.os.IPermissionController;
import android.os.IProcessInfoService;
import android.os.IProgressListener;
import android.os.InputConstants;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PowerExemptionManager;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SharedMemory;
import android.os.ShellCallback;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.os.incremental.IIncrementalService;
import android.os.incremental.IncrementalManager;
import android.os.incremental.IncrementalMetrics;
import android.os.storage.StorageManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.sysprop.InitProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.FeatureFlagUtils;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.Log;
import android.util.LogWriter;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.autofill.AutofillManagerInternal;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsActiveCallback;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.SystemUserHomeActivity;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.content.InstallLocationUtils;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BinderCallHeavyHitterWatcher;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.BinderTransactionNameResolver;
import com.android.internal.os.ByteTransferPipe;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.os.SomeArgs;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.os.TransferPipe;
import com.android.internal.os.Zygote;
import com.android.internal.os.anr.AnrLatencyTracker;
import com.android.internal.policy.AttributeCache;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.MemInfoReader;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.HeptFunction;
import com.android.internal.util.function.HexFunction;
import com.android.internal.util.function.QuadFunction;
import com.android.internal.util.function.QuintFunction;
import com.android.internal.util.function.UndecFunction;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AlarmManagerInternal;
import com.android.server.BootReceiver;
import com.android.server.DeviceIdleInternal;
import com.android.server.DisplayThread;
import com.android.server.IntentResolver;
import com.android.server.IoThread;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.PackageWatchdog;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.ThreadPriorityBooster;
import com.android.server.UiThread;
import com.android.server.UserspaceRebootLogger;
import com.android.server.Watchdog;
import com.android.server.appop.AppOpsService;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
import com.android.server.compat.PlatformCompat;
import com.android.server.contentcapture.ContentCaptureManagerInternal;
import com.android.server.criticalevents.CriticalEventLog;
import com.android.server.firewall.IntentFirewall;
import com.android.server.graphics.fonts.FontManagerInternal;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.net.NetworkManagementInternal;
import com.android.server.net.NetworkPolicyManagerInternal;
import com.android.server.p006am.ActiveServices;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.AppNotRespondingDialog;
import com.android.server.p006am.ComponentAliasResolver;
import com.android.server.p006am.DropboxRateLimiter;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p006am.ProcessList;
import com.android.server.p009os.NativeTombstoneManager;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.parsing.ParsingPackageUtils;
import com.android.server.p011pm.snapshot.PackageDataSnapshot;
import com.android.server.p013vr.VrManagerInternal;
import com.android.server.p014wm.ActivityMetricsLaunchObserver;
import com.android.server.p014wm.ActivityServiceConnectionsHolder;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerService;
import com.android.server.p014wm.ProtoLogCache;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.p014wm.WindowManagerService;
import com.android.server.p014wm.WindowProcessController;
import com.android.server.power.stats.BatteryStatsImpl;
import com.android.server.sdksandbox.SdkSandboxManagerLocal;
import com.android.server.uri.GrantUri;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.utils.PriorityDump;
import com.android.server.utils.Slogf;
import com.android.server.utils.TimingsTraceAndSlog;
import dalvik.annotation.optimization.NeverCompile;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import libcore.util.EmptyArray;
/* renamed from: com.android.server.am.ActivityManagerService */
/* loaded from: classes.dex */
public class ActivityManagerService extends IActivityManager.Stub implements Watchdog.Monitor, BatteryStatsImpl.BatteryCallback, ActivityManagerGlobalLock {
    public static final int BROADCAST_BG_TIMEOUT;
    public static final int BROADCAST_FG_TIMEOUT;
    public static final long[] DUMP_MEM_BUCKETS;
    public static final int[] DUMP_MEM_OOM_ADJ;
    public static final String[] DUMP_MEM_OOM_COMPACT_LABEL;
    public static final String[] DUMP_MEM_OOM_LABEL;
    public static final String[] EMPTY_STRING_ARRAY;
    public static final FgsTempAllowListItem FAKE_TEMP_ALLOW_LIST_ITEM;
    public static final int MY_PID;
    public static final int NATIVE_DUMP_TIMEOUT_MS;
    public static final int PROC_START_TIMEOUT;
    @GuardedBy({"sActiveProcessInfoSelfLocked"})
    public static final SparseArray<ProcessInfo> sActiveProcessInfoSelfLocked;
    @GuardedBy({"ActivityManagerService.class"})
    public static SimpleDateFormat sAnrFileDateFormat;
    public static final HostingRecord sNullHostingRecord;
    public static ThreadPriorityBooster sProcThreadPriorityBooster;
    public static String sTheRealBuildSerial;
    public static ThreadPriorityBooster sThreadPriorityBooster;
    @GuardedBy({"mActiveCameraUids"})
    public final IntArray mActiveCameraUids;
    @CompositeRWLock({"this", "mProcLock"})
    public final ArrayList<ActiveInstrumentation> mActiveInstrumentation;
    public final ActivityMetricsLaunchObserver mActivityLaunchObserver;
    @VisibleForTesting
    public ActivityTaskManagerService mActivityTaskManager;
    @GuardedBy({"this"})
    public ArrayMap<String, PackageAssociationInfo> mAllowedAssociations;
    @GuardedBy({"mAlreadyLoggedViolatedStacks"})
    public final HashSet<Integer> mAlreadyLoggedViolatedStacks;
    @GuardedBy({"this"})
    public boolean mAlwaysFinishActivities;
    public final AnrHelper mAnrHelper;
    public ArrayMap<String, IBinder> mAppBindArgs;
    public final AppErrors mAppErrors;
    public AppOpsManager mAppOpsManager;
    public final AppOpsService mAppOpsService;
    public final AppProfiler mAppProfiler;
    public final AppRestrictionController mAppRestrictionController;
    @GuardedBy({"this"})
    public final SparseArray<ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>>> mAssociations;
    @VisibleForTesting
    public ActivityTaskManagerInternal mAtmInternal;
    @CompositeRWLock({"this", "mProcLock"})
    public int[] mBackgroundAppIdAllowlist;
    @GuardedBy({"this"})
    public ArraySet<String> mBackgroundLaunchBroadcasts;
    @GuardedBy({"this"})
    public final SparseArray<BackupRecord> mBackupTargets;
    public final BatteryStatsService mBatteryStatsService;
    public final CopyOnWriteArrayList<ActivityManagerInternal.BindServiceEventListener> mBindServiceEventListeners;
    public volatile boolean mBinderTransactionTrackingEnabled;
    @GuardedBy({"this"})
    public boolean mBootAnimationComplete;
    public int mBootPhase;
    public volatile boolean mBooted;
    public volatile boolean mBooting;
    public final CopyOnWriteArrayList<ActivityManagerInternal.BroadcastEventListener> mBroadcastEventListeners;
    public final BroadcastQueue[] mBroadcastQueues;
    @GuardedBy({"this"})
    public boolean mCallFinishBooting;
    public final Map<Integer, Set<Integer>> mCompanionAppUidsMap;
    @GuardedBy({"this"})
    public final ComponentAliasResolver mComponentAliasResolver;
    public ActivityManagerConstants mConstants;
    public volatile ContentCaptureManagerInternal mContentCaptureService;
    public final Context mContext;
    public CoreSettingsObserver mCoreSettingsObserver;
    public final ContentProviderHelper mCpHelper;
    @GuardedBy({"this"})
    public BroadcastStats mCurBroadcastStats;
    @GuardedBy({"mOomAdjObserverLock"})
    public OomAdjObserver mCurOomAdjObserver;
    @GuardedBy({"mOomAdjObserverLock"})
    public int mCurOomAdjUid;
    public final Object mCurResumedAppLock;
    @GuardedBy({"mCurResumedAppLock"})
    public String mCurResumedPackage;
    @GuardedBy({"mCurResumedAppLock"})
    public int mCurResumedUid;
    @GuardedBy({"this"})
    public String mDebugApp;
    @GuardedBy({"this"})
    public boolean mDebugTransient;
    @GuardedBy({"mDeliveryGroupPolicyIgnoredActions"})
    public final ArraySet<String> mDeliveryGroupPolicyIgnoredActions;
    @CompositeRWLock({"this", "mProcLock"})
    public int[] mDeviceIdleAllowlist;
    @CompositeRWLock({"this", "mProcLock"})
    public int[] mDeviceIdleExceptIdleAllowlist;
    @CompositeRWLock({"this", "mProcLock"})
    public int[] mDeviceIdleTempAllowlist;
    public volatile int mDeviceOwnerUid;
    public final DropboxRateLimiter mDropboxRateLimiter;
    public final boolean mEnableModernQueue;
    public final boolean mEnableOffloadQueue;
    public final int mFactoryTest;
    @CompositeRWLock({"this", "mProcLock"})
    public final FgsTempAllowList<FgsTempAllowListItem> mFgsStartTempAllowList;
    public final FgsTempAllowList<String> mFgsWhileInUseTempAllowList;
    @CompositeRWLock({"this", "mProcLock"})
    public boolean mForceBackgroundCheck;
    @GuardedBy({"this"})
    public final ProcessMap<ArrayList<ProcessRecord>> mForegroundPackages;
    @GuardedBy({"this"})
    public final ArrayList<ActivityManagerInternal.ForegroundServiceStateListener> mForegroundServiceStateListeners;
    public final GetBackgroundStartPrivilegesFunctor mGetBackgroundStartPrivilegesFunctor;
    public final ActivityManagerGlobalLock mGlobalLock;
    public final MainHandler mHandler;
    @VisibleForTesting
    public final ServiceThread mHandlerThread;
    public final HiddenApiSettings mHiddenApiBlacklist;
    @GuardedBy({"this"})
    public final SparseArray<ImportanceToken> mImportantProcesses;
    public final Injector mInjector;
    public Installer mInstaller;
    public final InstrumentationReporter mInstrumentationReporter;
    public final IntentFirewall mIntentFirewall;
    @VisibleForTesting
    public final ActivityManagerInternal mInternal;
    public ArrayMap<String, IBinder> mIsolatedAppBindArgs;
    @GuardedBy({"mProcLock"})
    public long mLastBinderHeavyHitterAutoSamplerStart;
    @GuardedBy({"this"})
    public BroadcastStats mLastBroadcastStats;
    @GuardedBy({"mProcLock"})
    public long mLastIdleTime;
    @GuardedBy({"mProcLock"})
    public long mLastPowerCheckUptime;
    @GuardedBy({"mProcLock"})
    public ParcelFileDescriptor[] mLifeMonitorFds;
    public DeviceIdleInternal mLocalDeviceIdleController;
    public PowerManagerInternal mLocalPowerManager;
    @GuardedBy({"this"})
    public String mNativeDebuggingApp;
    public volatile IUidObserver mNetworkPolicyUidObserver;
    public volatile boolean mOnBattery;
    public final Object mOomAdjObserverLock;
    public OomAdjProfiler mOomAdjProfiler;
    public OomAdjuster mOomAdjuster;
    @GuardedBy({"this"})
    public String mOrigDebugApp;
    @GuardedBy({"this"})
    public boolean mOrigWaitForDebugger;
    public PackageManagerInternal mPackageManagerInt;
    public final PackageWatchdog mPackageWatchdog;
    @VisibleForTesting
    public final PendingIntentController mPendingIntentController;
    public final PendingStartActivityUids mPendingStartActivityUids;
    @CompositeRWLock({"this", "mProcLock"})
    public final PendingTempAllowlists mPendingTempAllowlist;
    public PermissionManagerServiceInternal mPermissionManagerInt;
    @GuardedBy({"this"})
    public final ArrayList<ProcessRecord> mPersistentStartingProcesses;
    public final PhantomProcessList mPhantomProcessList;
    public final PidMap mPidsSelfLocked;
    public final PlatformCompat mPlatformCompat;
    public final PriorityDump.PriorityDumper mPriorityDumper;
    public final ActivityManagerGlobalLock mProcLock;
    public final ProcessList.ProcStartHandler mProcStartHandler;
    public final ServiceThread mProcStartHandlerThread;
    public final ProcessList mProcessList;
    public final ProcessStatsService mProcessStats;
    @GuardedBy({"this"})
    public final ArrayList<ProcessRecord> mProcessesOnHold;
    public volatile boolean mProcessesReady;
    public ArraySet<Integer> mProfileOwnerUids;
    public final IntentResolver<BroadcastFilter, BroadcastFilter> mReceiverResolver;
    @GuardedBy({"this"})
    public final HashMap<IBinder, ReceiverList> mRegisteredReceivers;
    @GuardedBy({"this"})
    public boolean mSafeMode;
    public final ActiveServices mServices;
    @GuardedBy({"this"})
    public final SparseArray<ArrayMap<String, ArrayList<Intent>>> mStickyBroadcasts;
    @GuardedBy({"this"})
    public final SparseArray<IUnsafeIntentStrictModeCallback> mStrictModeCallbacks;
    @GuardedBy({"this"})
    public boolean mSuspendUponWait;
    public volatile boolean mSystemReady;
    public SystemServiceManager mSystemServiceManager;
    public final ActivityThread mSystemThread;
    public TestUtilityService mTestUtilityService;
    public TraceErrorLogger mTraceErrorLogger;
    @GuardedBy({"mProcLock"})
    public String mTrackAllocationApp;
    public boolean mTrackingAssociations;
    public UriGrantsManagerInternal mUgmInternal;
    public final Context mUiContext;
    public final Handler mUiHandler;
    @GuardedBy({"mUidNetworkBlockedReasons"})
    public final SparseIntArray mUidNetworkBlockedReasons;
    public final UidObserverController mUidObserverController;
    public volatile UsageStatsManagerInternal mUsageStatsService;
    public final boolean mUseFifoUiScheduling;
    public final UserController mUserController;
    public volatile boolean mUserIsMonkey;
    public volatile ActivityManagerInternal.VoiceInteractionManagerProvider mVoiceInteractionManagerProvider;
    @GuardedBy({"this"})
    public boolean mWaitForDebugger;
    public AtomicInteger mWakefulness;
    @VisibleForTesting
    public WindowManagerService mWindowManager;
    public WindowManagerInternal mWmInternal;

    /* renamed from: com.android.server.am.ActivityManagerService$OomAdjObserver */
    /* loaded from: classes.dex */
    public interface OomAdjObserver {
        void onOomAdjMessage(String str);
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ProcessChangeItem */
    /* loaded from: classes.dex */
    public static final class ProcessChangeItem {
        public int capability;
        public int changes;
        public boolean foregroundActivities;
        public int foregroundServiceTypes;
        public int pid;
        public int uid;
    }

    public final boolean isOnFgOffloadQueue(int i) {
        return (i & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
    }

    static {
        int i = Build.HW_TIMEOUT_MULTIPLIER;
        PROC_START_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        BROADCAST_FG_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        BROADCAST_BG_TIMEOUT = i * 60000;
        MY_PID = Process.myPid();
        EMPTY_STRING_ARRAY = new String[0];
        NATIVE_DUMP_TIMEOUT_MS = Build.HW_TIMEOUT_MULTIPLIER * 2000;
        sThreadPriorityBooster = new ThreadPriorityBooster(-2, 7);
        sProcThreadPriorityBooster = new ThreadPriorityBooster(-2, 6);
        sActiveProcessInfoSelfLocked = new SparseArray<>();
        FAKE_TEMP_ALLOW_LIST_ITEM = new FgsTempAllowListItem(Long.MAX_VALUE, 300, "", -1);
        sTheRealBuildSerial = "unknown";
        sNullHostingRecord = new HostingRecord("");
        DUMP_MEM_BUCKETS = new long[]{5120, 7168, 10240, 15360, 20480, 30720, 40960, 81920, 122880, 163840, 204800, 256000, 307200, 358400, 409600, 512000, 614400, 819200, 1048576, 2097152, 5242880, 10485760, 20971520};
        DUMP_MEM_OOM_ADJ = new int[]{-1000, -900, -800, -700, 0, 100, 200, 250, 225, 300, FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, 500, 600, 700, 800, 900};
        DUMP_MEM_OOM_LABEL = new String[]{"Native", "System", "Persistent", "Persistent Service", "Foreground", "Visible", "Perceptible", "Perceptible Low", "Perceptible Medium", "Backup", "Heavy Weight", "A Services", "Home", "Previous", "B Services", "Cached"};
        DUMP_MEM_OOM_COMPACT_LABEL = new String[]{"native", "sys", "pers", "persvc", "fore", "vis", "percept", "perceptl", "perceptm", "backup", "heavy", "servicea", "home", "prev", "serviceb", "cached"};
    }

    public BroadcastQueue broadcastQueueForIntent(Intent intent) {
        return broadcastQueueForFlags(intent.getFlags(), intent);
    }

    public BroadcastQueue broadcastQueueForFlags(int i) {
        return broadcastQueueForFlags(i, null);
    }

    public BroadcastQueue broadcastQueueForFlags(int i, Object obj) {
        if (this.mEnableModernQueue) {
            return this.mBroadcastQueues[0];
        }
        if (isOnFgOffloadQueue(i)) {
            return this.mBroadcastQueues[3];
        }
        if (isOnBgOffloadQueue(i)) {
            return this.mBroadcastQueues[2];
        }
        if ((i & 268435456) != 0) {
            return this.mBroadcastQueues[0];
        }
        return this.mBroadcastQueues[1];
    }

    public static void boostPriorityForLockedSection() {
        sThreadPriorityBooster.boost();
    }

    public static void resetPriorityAfterLockedSection() {
        sThreadPriorityBooster.reset();
    }

    public static void boostPriorityForProcLockedSection() {
        sProcThreadPriorityBooster.boost();
    }

    public static void resetPriorityAfterProcLockedSection() {
        sProcThreadPriorityBooster.reset();
    }

    /* renamed from: com.android.server.am.ActivityManagerService$PackageAssociationInfo */
    /* loaded from: classes.dex */
    public final class PackageAssociationInfo {
        public final ArraySet<String> mAllowedPackageAssociations;
        public boolean mIsDebuggable;
        public final String mSourcePackage;

        public PackageAssociationInfo(String str, ArraySet<String> arraySet, boolean z) {
            this.mSourcePackage = str;
            this.mAllowedPackageAssociations = arraySet;
            this.mIsDebuggable = z;
        }

        public boolean isPackageAssociationAllowed(String str) {
            return this.mIsDebuggable || this.mAllowedPackageAssociations.contains(str);
        }

        public boolean isDebuggable() {
            return this.mIsDebuggable;
        }

        public void setDebuggable(boolean z) {
            this.mIsDebuggable = z;
        }

        public ArraySet<String> getAllowedPackageAssociations() {
            return this.mAllowedPackageAssociations;
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$PidMap */
    /* loaded from: classes.dex */
    public static final class PidMap {
        public final SparseArray<ProcessRecord> mPidMap = new SparseArray<>();

        public ProcessRecord get(int i) {
            return this.mPidMap.get(i);
        }

        public int size() {
            return this.mPidMap.size();
        }

        public ProcessRecord valueAt(int i) {
            return this.mPidMap.valueAt(i);
        }

        public int keyAt(int i) {
            return this.mPidMap.keyAt(i);
        }

        public int indexOfKey(int i) {
            return this.mPidMap.indexOfKey(i);
        }

        public void doAddInternal(int i, ProcessRecord processRecord) {
            this.mPidMap.put(i, processRecord);
        }

        public boolean doRemoveInternal(int i, ProcessRecord processRecord) {
            ProcessRecord processRecord2 = this.mPidMap.get(i);
            if (processRecord2 == null || processRecord2.getStartSeq() != processRecord.getStartSeq()) {
                return false;
            }
            this.mPidMap.remove(i);
            return true;
        }
    }

    @GuardedBy({"this"})
    public void addPidLocked(ProcessRecord processRecord) {
        int pid = processRecord.getPid();
        synchronized (this.mPidsSelfLocked) {
            this.mPidsSelfLocked.doAddInternal(pid, processRecord);
        }
        SparseArray<ProcessInfo> sparseArray = sActiveProcessInfoSelfLocked;
        synchronized (sparseArray) {
            ProcessInfo processInfo = processRecord.processInfo;
            if (processInfo != null) {
                sparseArray.put(pid, processInfo);
            } else {
                sparseArray.remove(pid);
            }
        }
        this.mAtmInternal.onProcessMapped(pid, processRecord.getWindowProcessController());
    }

    @GuardedBy({"this"})
    public boolean removePidLocked(int i, ProcessRecord processRecord) {
        boolean doRemoveInternal;
        synchronized (this.mPidsSelfLocked) {
            doRemoveInternal = this.mPidsSelfLocked.doRemoveInternal(i, processRecord);
        }
        if (doRemoveInternal) {
            SparseArray<ProcessInfo> sparseArray = sActiveProcessInfoSelfLocked;
            synchronized (sparseArray) {
                sparseArray.remove(i);
            }
            this.mAtmInternal.onProcessUnMapped(i);
        }
        return doRemoveInternal;
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ImportanceToken */
    /* loaded from: classes.dex */
    public abstract class ImportanceToken implements IBinder.DeathRecipient {
        public final int pid;
        public final String reason;
        public final IBinder token;

        public ImportanceToken(int i, IBinder iBinder, String str) {
            this.pid = i;
            this.token = iBinder;
            this.reason = str;
        }

        public String toString() {
            return "ImportanceToken { " + Integer.toHexString(System.identityHashCode(this)) + " " + this.reason + " " + this.pid + " " + this.token + " }";
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.pid);
            IBinder iBinder = this.token;
            if (iBinder != null) {
                protoOutputStream.write(1138166333442L, iBinder.toString());
            }
            protoOutputStream.write(1138166333443L, this.reason);
            protoOutputStream.end(start);
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$Association */
    /* loaded from: classes.dex */
    public static final class Association {
        public int mCount;
        public long mLastStateUptime;
        public int mNesting;
        public final String mSourceProcess;
        public final int mSourceUid;
        public long mStartTime;
        public final ComponentName mTargetComponent;
        public final String mTargetProcess;
        public final int mTargetUid;
        public long mTime;
        public int mLastState = 21;
        public long[] mStateTimes = new long[21];

        public Association(int i, String str, int i2, ComponentName componentName, String str2) {
            this.mSourceUid = i;
            this.mSourceProcess = str;
            this.mTargetUid = i2;
            this.mTargetComponent = componentName;
            this.mTargetProcess = str2;
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$PendingTempAllowlist */
    /* loaded from: classes.dex */
    public static final class PendingTempAllowlist {
        public final int callingUid;
        public final long duration;
        public final int reasonCode;
        public final String tag;
        public final int targetUid;
        public final int type;

        public PendingTempAllowlist(int i, long j, int i2, String str, int i3, int i4) {
            this.targetUid = i;
            this.duration = j;
            this.tag = str;
            this.type = i3;
            this.reasonCode = i2;
            this.callingUid = i4;
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.targetUid);
            protoOutputStream.write(1112396529666L, this.duration);
            protoOutputStream.write(1138166333443L, this.tag);
            protoOutputStream.write(1120986464260L, this.type);
            protoOutputStream.write(1120986464261L, this.reasonCode);
            protoOutputStream.write(1120986464262L, this.callingUid);
            protoOutputStream.end(start);
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$FgsTempAllowListItem */
    /* loaded from: classes.dex */
    public static final class FgsTempAllowListItem {
        public final int mCallingUid;
        public final long mDuration;
        public final String mReason;
        public final int mReasonCode;

        public FgsTempAllowListItem(long j, int i, String str, int i2) {
            this.mDuration = j;
            this.mReasonCode = i;
            this.mReason = str;
            this.mCallingUid = i2;
        }

        public void dump(PrintWriter printWriter) {
            printWriter.print(" duration=" + this.mDuration + " callingUid=" + UserHandle.formatUid(this.mCallingUid) + " reasonCode=" + PowerExemptionManager.reasonCodeToString(this.mReasonCode) + " reason=" + this.mReason);
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$AppDeathRecipient */
    /* loaded from: classes.dex */
    public final class AppDeathRecipient implements IBinder.DeathRecipient {
        public final ProcessRecord mApp;
        public final IApplicationThread mAppThread;
        public final int mPid;

        public AppDeathRecipient(ProcessRecord processRecord, int i, IApplicationThread iApplicationThread) {
            this.mApp = processRecord;
            this.mPid = i;
            this.mAppThread = iApplicationThread;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.appDiedLocked(this.mApp, this.mPid, this.mAppThread, true, null);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$UiHandler */
    /* loaded from: classes.dex */
    public final class UiHandler extends Handler {
        public UiHandler() {
            super(UiThread.get().getLooper(), null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                ActivityManagerService.this.mAppErrors.handleShowAppErrorUi(message);
                ActivityManagerService.this.ensureBootCompleted();
            } else if (i == 2) {
                ActivityManagerService.this.mAppErrors.handleShowAnrUi(message);
                ActivityManagerService.this.ensureBootCompleted();
            } else if (i == 6) {
                synchronized (ActivityManagerService.this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ProcessRecord processRecord = (ProcessRecord) message.obj;
                        if (message.arg1 != 0) {
                            if (!processRecord.hasWaitedForDebugger()) {
                                processRecord.mErrorState.getDialogController().showDebugWaitingDialogs();
                                processRecord.setWaitedForDebugger(true);
                            }
                        } else {
                            processRecord.mErrorState.getDialogController().clearWaitingDialog();
                        }
                    } finally {
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            } else if (i != 26) {
                if (i == 68) {
                    ActivityManagerService.this.pushTempAllowlist();
                } else if (i == 70) {
                    ActivityManagerService.this.dispatchOomAdjObserver((String) message.obj);
                } else if (i == 31) {
                    ActivityManagerService.this.mProcessList.dispatchProcessesChanged();
                } else if (i != 32) {
                } else {
                    ActivityManagerService.this.mProcessList.dispatchProcessDied(message.arg1, message.arg2);
                }
            } else {
                HashMap hashMap = (HashMap) message.obj;
                synchronized (ActivityManagerService.this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ProcessRecord processRecord2 = (ProcessRecord) hashMap.get("app");
                        if (processRecord2 == null) {
                            Slog.e("ActivityManager", "App not found when showing strict mode dialog.");
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                        } else if (processRecord2.mErrorState.getDialogController().hasViolationDialogs()) {
                            Slog.e("ActivityManager", "App already has strict mode dialog: " + processRecord2);
                        } else {
                            AppErrorResult appErrorResult = (AppErrorResult) hashMap.get("result");
                            if (ActivityManagerService.this.mAtmInternal.showStrictModeViolationDialog()) {
                                processRecord2.mErrorState.getDialogController().showViolationDialogs(appErrorResult);
                            } else {
                                appErrorResult.set(0);
                            }
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            ActivityManagerService.this.ensureBootCompleted();
                        }
                    } finally {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                    }
                }
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$MainHandler */
    /* loaded from: classes.dex */
    public final class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(final Message message) {
            int i = message.what;
            if (i == 5) {
                synchronized (ActivityManagerService.this) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ActivityManagerService.this.mAppProfiler.performAppGcsIfAppropriateLocked();
                    } finally {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                return;
            }
            if (i == 20) {
                ProcessRecord processRecord = (ProcessRecord) message.obj;
                synchronized (ActivityManagerService.this) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ActivityManagerService.this.handleProcessStartOrKillTimeoutLocked(processRecord, false);
                    } finally {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } else if (i == 22) {
                synchronized (ActivityManagerService.this) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        int i2 = message.arg1;
                        int i3 = message.arg2;
                        SomeArgs someArgs = (SomeArgs) message.obj;
                        String str = (String) someArgs.arg1;
                        String str2 = (String) someArgs.arg2;
                        int intValue = ((Integer) someArgs.arg3).intValue();
                        someArgs.recycle();
                        ActivityManagerService.this.forceStopPackageLocked(str, i2, false, false, true, false, false, i3, str2, intValue);
                    } finally {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } else if (i == 33) {
                final ArrayList arrayList = (ArrayList) message.obj;
                new Thread() { // from class: com.android.server.am.ActivityManagerService.MainHandler.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        ActivityManagerService.this.mAppProfiler.reportMemUsage(arrayList);
                    }
                }.start();
            } else if (i == 41) {
                synchronized (ActivityManagerService.this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ActivityManagerService.this.mProcessList.updateAllTimePrefsLOSP(message.arg1);
                    } finally {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            } else if (i == 63) {
                synchronized (ActivityManagerService.this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ActivityManagerService.this.mProcessList.handleAllTrustStorageUpdateLOSP();
                    } finally {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            } else if (i == 69) {
                SomeArgs someArgs2 = (SomeArgs) message.obj;
                ActivityManagerService.this.mServices.serviceForegroundCrash((ProcessRecord) someArgs2.arg1, (String) someArgs2.arg2, (ComponentName) someArgs2.arg3);
                someArgs2.recycle();
            } else if (i == 12) {
                ActivityManagerService.this.mServices.serviceTimeout((ProcessRecord) message.obj);
            } else if (i == 13) {
                synchronized (ActivityManagerService.this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ActivityManagerService.this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$MainHandler$$ExternalSyntheticLambda0
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                ActivityManagerService.MainHandler.lambda$handleMessage$0((ProcessRecord) obj);
                            }
                        });
                    } finally {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            } else if (i == 66) {
                ActivityManagerService.this.mServices.serviceForegroundTimeout((ServiceRecord) message.obj);
            } else if (i == 67) {
                SomeArgs someArgs3 = (SomeArgs) message.obj;
                ActivityManagerService.this.mServices.serviceForegroundTimeoutANR((ProcessRecord) someArgs3.arg1, (TimeoutRecord) someArgs3.arg2);
                someArgs3.recycle();
            } else {
                switch (i) {
                    case 27:
                        ActivityManagerService.this.checkExcessivePowerUsage();
                        removeMessages(27);
                        sendMessageDelayed(obtainMessage(27), ActivityManagerService.this.mConstants.POWER_CHECK_INTERVAL);
                        return;
                    case 28:
                        synchronized (ActivityManagerService.this.mProcLock) {
                            try {
                                ActivityManagerService.boostPriorityForProcLockedSection();
                                ActivityManagerService.this.mProcessList.clearAllDnsCacheLOSP();
                            } finally {
                            }
                        }
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        return;
                    case 29:
                        ActivityManagerService.this.mProcessList.setAllHttpProxy();
                        return;
                    default:
                        switch (i) {
                            case 49:
                                int i4 = message.arg1;
                                byte[] bArr = (byte[]) message.obj;
                                synchronized (ActivityManagerService.this.mProcLock) {
                                    try {
                                        ActivityManagerService.boostPriorityForProcLockedSection();
                                        synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                                            for (int i5 = 0; i5 < ActivityManagerService.this.mPidsSelfLocked.size(); i5++) {
                                                ProcessRecord valueAt = ActivityManagerService.this.mPidsSelfLocked.valueAt(i5);
                                                IApplicationThread thread = valueAt.getThread();
                                                if (valueAt.uid == i4 && thread != null) {
                                                    try {
                                                        thread.notifyCleartextNetwork(bArr);
                                                    } catch (RemoteException unused) {
                                                    }
                                                }
                                            }
                                        }
                                    } finally {
                                    }
                                }
                                ActivityManagerService.resetPriorityAfterProcLockedSection();
                                return;
                            case 50:
                                ActivityManagerService.this.mAppProfiler.handlePostDumpHeapNotification();
                                return;
                            case 51:
                                ActivityManagerService.this.mAppProfiler.handleAbortDumpHeap((String) message.obj);
                                return;
                            default:
                                switch (i) {
                                    case 56:
                                        try {
                                            ((IUiAutomationConnection) message.obj).shutdown();
                                        } catch (RemoteException unused2) {
                                            Slog.w("ActivityManager", "Error shutting down UiAutomationConnection");
                                        }
                                        ActivityManagerService.this.mUserIsMonkey = false;
                                        return;
                                    case 57:
                                        ProcessRecord processRecord2 = (ProcessRecord) message.obj;
                                        synchronized (ActivityManagerService.this) {
                                            try {
                                                ActivityManagerService.boostPriorityForLockedSection();
                                                ActivityManagerService.this.mCpHelper.processContentProviderPublishTimedOutLocked(processRecord2);
                                            } finally {
                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                            }
                                        }
                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                        return;
                                    case 58:
                                        ActivityManagerService.this.idleUids();
                                        return;
                                    default:
                                        switch (i) {
                                            case 71:
                                                synchronized (ActivityManagerService.this) {
                                                    try {
                                                        ActivityManagerService.boostPriorityForLockedSection();
                                                        ActivityManagerService.this.mProcessList.killAppZygoteIfNeededLocked((AppZygote) message.obj, false);
                                                    } finally {
                                                    }
                                                }
                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                return;
                                            case 72:
                                                ActivityManagerService.this.handleBinderHeavyHitterAutoSamplerTimeOut();
                                                return;
                                            case 73:
                                                synchronized (ActivityManagerService.this) {
                                                    try {
                                                        ActivityManagerService.boostPriorityForLockedSection();
                                                        ((ContentProviderRecord) message.obj).onProviderPublishStatusLocked(false);
                                                    } finally {
                                                    }
                                                }
                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                return;
                                            case 74:
                                                ActivityManagerService.this.mBroadcastEventListeners.forEach(new Consumer() { // from class: com.android.server.am.ActivityManagerService$MainHandler$$ExternalSyntheticLambda1
                                                    @Override // java.util.function.Consumer
                                                    public final void accept(Object obj) {
                                                        ActivityManagerService.MainHandler.lambda$handleMessage$1(message, (ActivityManagerInternal.BroadcastEventListener) obj);
                                                    }
                                                });
                                                return;
                                            case 75:
                                                ActivityManagerService.this.mBindServiceEventListeners.forEach(new Consumer() { // from class: com.android.server.am.ActivityManagerService$MainHandler$$ExternalSyntheticLambda2
                                                    @Override // java.util.function.Consumer
                                                    public final void accept(Object obj) {
                                                        ActivityManagerService.MainHandler.lambda$handleMessage$2(message, (ActivityManagerInternal.BindServiceEventListener) obj);
                                                    }
                                                });
                                                return;
                                            case 76:
                                                ActivityManagerService.this.mServices.onShortFgsTimeout((ServiceRecord) message.obj);
                                                return;
                                            case 77:
                                                ActivityManagerService.this.mServices.onShortFgsProcstateTimeout((ServiceRecord) message.obj);
                                                return;
                                            case 78:
                                                ActivityManagerService.this.mServices.onShortFgsAnrTimeout((ServiceRecord) message.obj);
                                                return;
                                            default:
                                                return;
                                        }
                                }
                        }
                }
            }
        }

        public static /* synthetic */ void lambda$handleMessage$0(ProcessRecord processRecord) {
            IApplicationThread thread = processRecord.getThread();
            if (thread != null) {
                try {
                    thread.updateTimeZone();
                } catch (RemoteException unused) {
                    Slog.w("ActivityManager", "Failed to update time zone for: " + processRecord.info.processName);
                }
            }
        }

        public static /* synthetic */ void lambda$handleMessage$1(Message message, ActivityManagerInternal.BroadcastEventListener broadcastEventListener) {
            broadcastEventListener.onSendingBroadcast((String) message.obj, message.arg1);
        }

        public static /* synthetic */ void lambda$handleMessage$2(Message message, ActivityManagerInternal.BindServiceEventListener bindServiceEventListener) {
            bindServiceEventListener.onBindingService((String) message.obj, message.arg1);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setSystemProcess() {
        try {
            ServiceManager.addService("activity", this, true, 21);
            ServiceManager.addService("procstats", this.mProcessStats);
            ServiceManager.addService("meminfo", new MemBinder(this), false, 2);
            ServiceManager.addService("gfxinfo", new GraphicsBinder(this));
            ServiceManager.addService("dbinfo", new DbBinder(this));
            this.mAppProfiler.setCpuInfoService();
            ServiceManager.addService("permission", new PermissionController(this));
            ServiceManager.addService("processinfo", new ProcessInfoService(this));
            ServiceManager.addService("cacheinfo", new CacheBinder(this));
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(PackageManagerShellCommandDataLoader.PACKAGE, 1049600);
            this.mSystemThread.installSystemApplicationInfo(applicationInfo, getClass().getClassLoader());
            synchronized (this) {
                boostPriorityForLockedSection();
                ProcessRecord newProcessRecordLocked = this.mProcessList.newProcessRecordLocked(applicationInfo, applicationInfo.processName, false, 0, false, 0, null, new HostingRecord("system"));
                newProcessRecordLocked.setPersistent(true);
                newProcessRecordLocked.setPid(MY_PID);
                newProcessRecordLocked.mState.setMaxAdj(-900);
                newProcessRecordLocked.makeActive(this.mSystemThread.getApplicationThread(), this.mProcessStats);
                newProcessRecordLocked.mProfile.addHostingComponentType(1);
                addPidLocked(newProcessRecordLocked);
                updateLruProcessLocked(newProcessRecordLocked, false, null);
                updateOomAdjLocked(0);
            }
            resetPriorityAfterLockedSection();
            this.mAppOpsService.startWatchingMode(63, null, new IAppOpsCallback.Stub() { // from class: com.android.server.am.ActivityManagerService.4
                public void opChanged(int i, int i2, String str) {
                    if (i != 63 || str == null || ActivityManagerService.this.getAppOpsManager().checkOpNoThrow(i, i2, str) == 0) {
                        return;
                    }
                    ActivityManagerService.this.runInBackgroundDisabled(i2);
                }
            });
            this.mAppOpsService.startWatchingActive(new int[]{26}, new IAppOpsActiveCallback.Stub() { // from class: com.android.server.am.ActivityManagerService.5
                public void opActiveChanged(int i, int i2, String str, String str2, boolean z, int i3, int i4) {
                    ActivityManagerService.this.cameraActiveChanged(i2, z);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unable to find android system package", e);
        }
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mWindowManager = windowManagerService;
                this.mWmInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
                this.mActivityTaskManager.setWindowManager(windowManagerService);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setUsageStatsManager(UsageStatsManagerInternal usageStatsManagerInternal) {
        this.mUsageStatsService = usageStatsManagerInternal;
        this.mActivityTaskManager.setUsageStatsManager(usageStatsManagerInternal);
    }

    public void setContentCaptureManager(ContentCaptureManagerInternal contentCaptureManagerInternal) {
        this.mContentCaptureService = contentCaptureManagerInternal;
    }

    public void startObservingNativeCrashes() {
        new NativeCrashListener(this).start();
    }

    public void setAppOpsPolicy(AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate) {
        this.mAppOpsService.setAppOpsPolicy(checkOpsDelegate);
    }

    public final void setVoiceInteractionManagerProvider(ActivityManagerInternal.VoiceInteractionManagerProvider voiceInteractionManagerProvider) {
        this.mVoiceInteractionManagerProvider = voiceInteractionManagerProvider;
    }

    /* renamed from: com.android.server.am.ActivityManagerService$MemBinder */
    /* loaded from: classes.dex */
    public static class MemBinder extends Binder {
        public ActivityManagerService mActivityManagerService;
        public final PriorityDump.PriorityDumper mPriorityDumper = new PriorityDump.PriorityDumper() { // from class: com.android.server.am.ActivityManagerService.MemBinder.1
            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpHigh(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                dump(fileDescriptor, printWriter, new String[]{"-a"}, z);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                MemBinder.this.mActivityManagerService.dumpApplicationMemoryUsage(fileDescriptor, printWriter, "  ", strArr, false, null, z);
            }
        };

        public MemBinder(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            try {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(false);
                if (DumpUtils.checkDumpAndUsageStatsPermission(this.mActivityManagerService.mContext, "meminfo", printWriter)) {
                    PriorityDump.dump(this.mPriorityDumper, fileDescriptor, printWriter, strArr);
                }
            } finally {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(true);
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$GraphicsBinder */
    /* loaded from: classes.dex */
    public static class GraphicsBinder extends Binder {
        public ActivityManagerService mActivityManagerService;

        public GraphicsBinder(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            try {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(false);
                if (DumpUtils.checkDumpAndUsageStatsPermission(this.mActivityManagerService.mContext, "gfxinfo", printWriter)) {
                    this.mActivityManagerService.dumpGraphicsHardwareUsage(fileDescriptor, printWriter, strArr);
                }
            } finally {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(true);
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$DbBinder */
    /* loaded from: classes.dex */
    public static class DbBinder extends Binder {
        public ActivityManagerService mActivityManagerService;

        public DbBinder(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            try {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(false);
                if (DumpUtils.checkDumpAndUsageStatsPermission(this.mActivityManagerService.mContext, "dbinfo", printWriter)) {
                    this.mActivityManagerService.dumpDbInfo(fileDescriptor, printWriter, strArr);
                }
            } finally {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(true);
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$CacheBinder */
    /* loaded from: classes.dex */
    public static class CacheBinder extends Binder {
        public ActivityManagerService mActivityManagerService;

        public CacheBinder(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            try {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(false);
                if (DumpUtils.checkDumpAndUsageStatsPermission(this.mActivityManagerService.mContext, "cacheinfo", printWriter)) {
                    this.mActivityManagerService.dumpBinderCacheContents(fileDescriptor, printWriter, strArr);
                }
            } finally {
                this.mActivityManagerService.mOomAdjuster.mCachedAppOptimizer.enableFreezer(true);
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$Lifecycle */
    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public static ActivityTaskManagerService sAtm;
        public final ActivityManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new ActivityManagerService(context, sAtm);
        }

        public static ActivityManagerService startService(SystemServiceManager systemServiceManager, ActivityTaskManagerService activityTaskManagerService) {
            sAtm = activityTaskManagerService;
            return ((Lifecycle) systemServiceManager.startService(Lifecycle.class)).getService();
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            this.mService.start();
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            ActivityManagerService activityManagerService = this.mService;
            activityManagerService.mBootPhase = i;
            if (i == 500) {
                activityManagerService.mBatteryStatsService.systemServicesReady();
                this.mService.mServices.systemServicesReady();
            } else if (i == 550) {
                activityManagerService.startBroadcastObservers();
            } else if (i == 600) {
                activityManagerService.mPackageWatchdog.onPackagesReady();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            this.mService.mBatteryStatsService.onCleanupUser(targetUser.getUserIdentifier());
        }

        public ActivityManagerService getService() {
            return this.mService;
        }
    }

    public final void maybeLogUserspaceRebootEvent() {
        int currentUserId;
        if (UserspaceRebootLogger.shouldLogUserspaceRebootEvent() && (currentUserId = this.mUserController.getCurrentUserId()) == 0) {
            UserspaceRebootLogger.logEventAsync(StorageManager.isUserKeyUnlocked(currentUserId), BackgroundThread.getExecutor());
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$HiddenApiSettings */
    /* loaded from: classes.dex */
    public static class HiddenApiSettings extends ContentObserver implements DeviceConfig.OnPropertiesChangedListener {
        public boolean mBlacklistDisabled;
        public final Context mContext;
        public List<String> mExemptions;
        public String mExemptionsStr;
        public int mLogSampleRate;
        public int mPolicy;
        public int mStatslogSampleRate;

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            int i = properties.getInt("hidden_api_access_log_sampling_rate", 0);
            if (i < 0 || i > 65536) {
                i = -1;
            }
            if (i != -1 && i != this.mLogSampleRate) {
                this.mLogSampleRate = i;
                Process.ZYGOTE_PROCESS.setHiddenApiAccessLogSampleRate(i);
            }
            int i2 = properties.getInt("hidden_api_access_statslog_sampling_rate", 0);
            if (i2 < 0 || i2 > 65536) {
                i2 = -1;
            }
            if (i2 == -1 || i2 == this.mStatslogSampleRate) {
                return;
            }
            this.mStatslogSampleRate = i2;
            Process.ZYGOTE_PROCESS.setHiddenApiAccessStatslogSampleRate(i2);
        }

        public HiddenApiSettings(Handler handler, Context context) {
            super(handler);
            this.mExemptions = Collections.emptyList();
            this.mLogSampleRate = -1;
            this.mStatslogSampleRate = -1;
            this.mPolicy = -1;
            this.mContext = context;
        }

        public void registerObserver() {
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("hidden_api_blacklist_exemptions"), false, this);
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("hidden_api_policy"), false, this);
            DeviceConfig.addOnPropertiesChangedListener("app_compat", this.mContext.getMainExecutor(), this);
            update();
        }

        public final void update() {
            List<String> asList;
            String string = Settings.Global.getString(this.mContext.getContentResolver(), "hidden_api_blacklist_exemptions");
            if (!TextUtils.equals(string, this.mExemptionsStr)) {
                this.mExemptionsStr = string;
                if ("*".equals(string)) {
                    this.mBlacklistDisabled = true;
                    this.mExemptions = Collections.emptyList();
                } else {
                    this.mBlacklistDisabled = false;
                    if (TextUtils.isEmpty(string)) {
                        asList = Collections.emptyList();
                    } else {
                        asList = Arrays.asList(string.split(","));
                    }
                    this.mExemptions = asList;
                }
                if (!Process.ZYGOTE_PROCESS.setApiDenylistExemptions(this.mExemptions)) {
                    Slog.e("ActivityManager", "Failed to set API blacklist exemptions!");
                    this.mExemptions = Collections.emptyList();
                }
            }
            this.mPolicy = getValidEnforcementPolicy("hidden_api_policy");
        }

        public final int getValidEnforcementPolicy(String str) {
            int i = Settings.Global.getInt(this.mContext.getContentResolver(), str, -1);
            if (ApplicationInfo.isValidHiddenApiEnforcementPolicy(i)) {
                return i;
            }
            return -1;
        }

        public boolean isDisabled() {
            return this.mBlacklistDisabled;
        }

        public int getPolicy() {
            return this.mPolicy;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            update();
        }
    }

    public AppOpsManager getAppOpsManager() {
        if (this.mAppOpsManager == null) {
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        }
        return this.mAppOpsManager;
    }

    @VisibleForTesting
    public ActivityManagerService(Injector injector, ServiceThread serviceThread) {
        this.mInstrumentationReporter = new InstrumentationReporter();
        this.mActiveInstrumentation = new ArrayList<>();
        this.mOomAdjProfiler = new OomAdjProfiler();
        this.mGlobalLock = this;
        this.mProcLock = new ActivityManagerProcLock();
        this.mStrictModeCallbacks = new SparseArray<>();
        this.mDeviceOwnerUid = -1;
        this.mCompanionAppUidsMap = new ArrayMap();
        this.mProfileOwnerUids = null;
        this.mDeliveryGroupPolicyIgnoredActions = new ArraySet<>();
        this.mActiveCameraUids = new IntArray(4);
        this.mPriorityDumper = new PriorityDump.PriorityDumper() { // from class: com.android.server.am.ActivityManagerService.1
            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpCritical(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                if (z) {
                    return;
                }
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"activities"}, z);
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"service", "all-platform-critical"}, z);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpNormal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"-a", "--normal-priority"}, z);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, strArr, z);
            }
        };
        this.mBackgroundAppIdAllowlist = new int[]{1002};
        this.mPidsSelfLocked = new PidMap();
        this.mImportantProcesses = new SparseArray<>();
        this.mProcessesOnHold = new ArrayList<>();
        this.mPersistentStartingProcesses = new ArrayList<>();
        this.mActivityLaunchObserver = new ActivityMetricsLaunchObserver() { // from class: com.android.server.am.ActivityManagerService.2
            @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
            public void onActivityLaunched(long j, ComponentName componentName, int i) {
                ActivityManagerService.this.mAppProfiler.onActivityLaunched();
            }
        };
        this.mBinderTransactionTrackingEnabled = false;
        this.mAlreadyLoggedViolatedStacks = new HashSet<>();
        this.mRegisteredReceivers = new HashMap<>();
        this.mReceiverResolver = new IntentResolver<BroadcastFilter, BroadcastFilter>() { // from class: com.android.server.am.ActivityManagerService.3
            @Override // com.android.server.IntentResolver
            public IntentFilter getIntentFilter(BroadcastFilter broadcastFilter) {
                return broadcastFilter;
            }

            @Override // com.android.server.IntentResolver
            public boolean allowFilterResult(BroadcastFilter broadcastFilter, List<BroadcastFilter> list) {
                IBinder asBinder = broadcastFilter.receiverList.receiver.asBinder();
                for (int size = list.size() - 1; size >= 0; size--) {
                    if (list.get(size).receiverList.receiver.asBinder() == asBinder) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.android.server.IntentResolver
            public BroadcastFilter newResult(Computer computer, BroadcastFilter broadcastFilter, int i, int i2, long j) {
                int i3;
                if (i2 == -1 || (i3 = broadcastFilter.owningUserId) == -1 || i2 == i3) {
                    return (BroadcastFilter) super.newResult(computer, (Computer) broadcastFilter, i, i2, j);
                }
                return null;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.IntentResolver
            public BroadcastFilter[] newArray(int i) {
                return new BroadcastFilter[i];
            }

            @Override // com.android.server.IntentResolver
            public boolean isPackageForFilter(String str, BroadcastFilter broadcastFilter) {
                return str.equals(broadcastFilter.packageName);
            }
        };
        this.mStickyBroadcasts = new SparseArray<>();
        this.mAssociations = new SparseArray<>();
        this.mBackupTargets = new SparseArray<>();
        this.mDeviceIdleAllowlist = new int[0];
        this.mDeviceIdleExceptIdleAllowlist = new int[0];
        this.mDeviceIdleTempAllowlist = new int[0];
        this.mPendingTempAllowlist = new PendingTempAllowlists(this);
        this.mFgsStartTempAllowList = new FgsTempAllowList<>();
        this.mFgsWhileInUseTempAllowList = new FgsTempAllowList<>();
        this.mProcessesReady = false;
        this.mSystemReady = false;
        this.mOnBattery = false;
        this.mBooting = false;
        this.mCallFinishBooting = false;
        this.mBootAnimationComplete = false;
        this.mWakefulness = new AtomicInteger(1);
        this.mLastIdleTime = SystemClock.uptimeMillis();
        this.mCurResumedPackage = null;
        this.mCurResumedUid = -1;
        this.mCurResumedAppLock = new Object();
        this.mForegroundPackages = new ProcessMap<>();
        this.mForegroundServiceStateListeners = new ArrayList<>();
        this.mBroadcastEventListeners = new CopyOnWriteArrayList<>();
        this.mBindServiceEventListeners = new CopyOnWriteArrayList<>();
        this.mDebugApp = null;
        this.mWaitForDebugger = false;
        this.mSuspendUponWait = false;
        this.mDebugTransient = false;
        this.mOrigDebugApp = null;
        this.mOrigWaitForDebugger = false;
        this.mAlwaysFinishActivities = false;
        this.mTrackAllocationApp = null;
        this.mNativeDebuggingApp = null;
        this.mOomAdjObserverLock = new Object();
        this.mAnrHelper = new AnrHelper(this);
        this.mBooted = false;
        this.mUidNetworkBlockedReasons = new SparseIntArray();
        this.mLastBinderHeavyHitterAutoSamplerStart = 0L;
        this.mGetBackgroundStartPrivilegesFunctor = new GetBackgroundStartPrivilegesFunctor();
        this.mDropboxRateLimiter = new DropboxRateLimiter();
        this.mInjector = injector;
        Context context = injector.getContext();
        this.mContext = context;
        this.mUiContext = null;
        this.mAppErrors = null;
        this.mPackageWatchdog = null;
        this.mAppOpsService = injector.getAppOpsService(null, null, null);
        this.mBatteryStatsService = injector.getBatteryStatsService();
        MainHandler mainHandler = new MainHandler(serviceThread.getLooper());
        this.mHandler = mainHandler;
        this.mHandlerThread = serviceThread;
        this.mConstants = new ActivityManagerConstants(context, this, mainHandler);
        ActiveUids activeUids = new ActiveUids(this, false);
        this.mPlatformCompat = null;
        ProcessList processList = injector.getProcessList(this);
        this.mProcessList = processList;
        processList.init(this, activeUids, null);
        this.mAppProfiler = new AppProfiler(this, BackgroundThread.getHandler().getLooper(), null);
        this.mPhantomProcessList = new PhantomProcessList(this);
        this.mOomAdjuster = new OomAdjuster(this, processList, activeUids, serviceThread);
        this.mIntentFirewall = null;
        this.mProcessStats = new ProcessStatsService(this, context.getCacheDir());
        this.mCpHelper = new ContentProviderHelper(this, false);
        this.mServices = injector.getActiveServices(this);
        this.mSystemThread = null;
        Handler uiHandler = injector.getUiHandler(null);
        this.mUiHandler = uiHandler;
        this.mUidObserverController = new UidObserverController(uiHandler);
        UserController userController = new UserController(this);
        this.mUserController = userController;
        injector.mUserController = userController;
        this.mPendingIntentController = new PendingIntentController(serviceThread.getLooper(), userController, this.mConstants);
        this.mAppRestrictionController = new AppRestrictionController(context, this);
        this.mProcStartHandlerThread = null;
        this.mProcStartHandler = null;
        this.mHiddenApiBlacklist = null;
        this.mFactoryTest = 0;
        this.mUgmInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
        this.mInternal = new LocalService();
        this.mPendingStartActivityUids = new PendingStartActivityUids();
        this.mUseFifoUiScheduling = false;
        this.mEnableOffloadQueue = false;
        this.mEnableModernQueue = false;
        this.mBroadcastQueues = new BroadcastQueue[0];
        this.mComponentAliasResolver = new ComponentAliasResolver(this);
    }

    public ActivityManagerService(Context context, ActivityTaskManagerService activityTaskManagerService) {
        Handler handler;
        MainHandler mainHandler;
        this.mInstrumentationReporter = new InstrumentationReporter();
        this.mActiveInstrumentation = new ArrayList<>();
        this.mOomAdjProfiler = new OomAdjProfiler();
        this.mGlobalLock = this;
        this.mProcLock = new ActivityManagerProcLock();
        this.mStrictModeCallbacks = new SparseArray<>();
        this.mDeviceOwnerUid = -1;
        this.mCompanionAppUidsMap = new ArrayMap();
        this.mProfileOwnerUids = null;
        this.mDeliveryGroupPolicyIgnoredActions = new ArraySet<>();
        this.mActiveCameraUids = new IntArray(4);
        this.mPriorityDumper = new PriorityDump.PriorityDumper() { // from class: com.android.server.am.ActivityManagerService.1
            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpCritical(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                if (z) {
                    return;
                }
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"activities"}, z);
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"service", "all-platform-critical"}, z);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpNormal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"-a", "--normal-priority"}, z);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                ActivityManagerService.this.doDump(fileDescriptor, printWriter, strArr, z);
            }
        };
        this.mBackgroundAppIdAllowlist = new int[]{1002};
        this.mPidsSelfLocked = new PidMap();
        this.mImportantProcesses = new SparseArray<>();
        this.mProcessesOnHold = new ArrayList<>();
        this.mPersistentStartingProcesses = new ArrayList<>();
        this.mActivityLaunchObserver = new ActivityMetricsLaunchObserver() { // from class: com.android.server.am.ActivityManagerService.2
            @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
            public void onActivityLaunched(long j, ComponentName componentName, int i) {
                ActivityManagerService.this.mAppProfiler.onActivityLaunched();
            }
        };
        this.mBinderTransactionTrackingEnabled = false;
        this.mAlreadyLoggedViolatedStacks = new HashSet<>();
        this.mRegisteredReceivers = new HashMap<>();
        this.mReceiverResolver = new IntentResolver<BroadcastFilter, BroadcastFilter>() { // from class: com.android.server.am.ActivityManagerService.3
            @Override // com.android.server.IntentResolver
            public IntentFilter getIntentFilter(BroadcastFilter broadcastFilter) {
                return broadcastFilter;
            }

            @Override // com.android.server.IntentResolver
            public boolean allowFilterResult(BroadcastFilter broadcastFilter, List<BroadcastFilter> list) {
                IBinder asBinder = broadcastFilter.receiverList.receiver.asBinder();
                for (int size = list.size() - 1; size >= 0; size--) {
                    if (list.get(size).receiverList.receiver.asBinder() == asBinder) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.android.server.IntentResolver
            public BroadcastFilter newResult(Computer computer, BroadcastFilter broadcastFilter, int i, int i2, long j) {
                int i3;
                if (i2 == -1 || (i3 = broadcastFilter.owningUserId) == -1 || i2 == i3) {
                    return (BroadcastFilter) super.newResult(computer, (Computer) broadcastFilter, i, i2, j);
                }
                return null;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.IntentResolver
            public BroadcastFilter[] newArray(int i) {
                return new BroadcastFilter[i];
            }

            @Override // com.android.server.IntentResolver
            public boolean isPackageForFilter(String str, BroadcastFilter broadcastFilter) {
                return str.equals(broadcastFilter.packageName);
            }
        };
        this.mStickyBroadcasts = new SparseArray<>();
        this.mAssociations = new SparseArray<>();
        this.mBackupTargets = new SparseArray<>();
        this.mDeviceIdleAllowlist = new int[0];
        this.mDeviceIdleExceptIdleAllowlist = new int[0];
        this.mDeviceIdleTempAllowlist = new int[0];
        this.mPendingTempAllowlist = new PendingTempAllowlists(this);
        this.mFgsStartTempAllowList = new FgsTempAllowList<>();
        this.mFgsWhileInUseTempAllowList = new FgsTempAllowList<>();
        this.mProcessesReady = false;
        this.mSystemReady = false;
        this.mOnBattery = false;
        this.mBooting = false;
        this.mCallFinishBooting = false;
        this.mBootAnimationComplete = false;
        this.mWakefulness = new AtomicInteger(1);
        this.mLastIdleTime = SystemClock.uptimeMillis();
        this.mCurResumedPackage = null;
        this.mCurResumedUid = -1;
        this.mCurResumedAppLock = new Object();
        this.mForegroundPackages = new ProcessMap<>();
        this.mForegroundServiceStateListeners = new ArrayList<>();
        this.mBroadcastEventListeners = new CopyOnWriteArrayList<>();
        this.mBindServiceEventListeners = new CopyOnWriteArrayList<>();
        this.mDebugApp = null;
        this.mWaitForDebugger = false;
        this.mSuspendUponWait = false;
        this.mDebugTransient = false;
        this.mOrigDebugApp = null;
        this.mOrigWaitForDebugger = false;
        this.mAlwaysFinishActivities = false;
        this.mTrackAllocationApp = null;
        this.mNativeDebuggingApp = null;
        this.mOomAdjObserverLock = new Object();
        this.mAnrHelper = new AnrHelper(this);
        this.mBooted = false;
        this.mUidNetworkBlockedReasons = new SparseIntArray();
        this.mLastBinderHeavyHitterAutoSamplerStart = 0L;
        this.mGetBackgroundStartPrivilegesFunctor = new GetBackgroundStartPrivilegesFunctor();
        this.mDropboxRateLimiter = new DropboxRateLimiter();
        LockGuard.installLock(this, 7);
        Injector injector = new Injector(context);
        this.mInjector = injector;
        this.mContext = context;
        this.mFactoryTest = FactoryTest.getMode();
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        this.mSystemThread = currentActivityThread;
        ContextImpl systemUiContext = currentActivityThread.getSystemUiContext();
        this.mUiContext = systemUiContext;
        Slog.i("ActivityManager", "Memory class: " + ActivityManager.staticGetMemoryClass());
        ServiceThread serviceThread = new ServiceThread("ActivityManager", -2, false);
        this.mHandlerThread = serviceThread;
        serviceThread.start();
        MainHandler mainHandler2 = new MainHandler(serviceThread.getLooper());
        this.mHandler = mainHandler2;
        Handler uiHandler = injector.getUiHandler(this);
        this.mUiHandler = uiHandler;
        ServiceThread serviceThread2 = new ServiceThread("ActivityManager:procStart", -2, false);
        this.mProcStartHandlerThread = serviceThread2;
        serviceThread2.start();
        this.mProcStartHandler = new ProcessList.ProcStartHandler(this, serviceThread2.getLooper());
        this.mConstants = new ActivityManagerConstants(context, this, mainHandler2);
        ActiveUids activeUids = new ActiveUids(this, true);
        PlatformCompat platformCompat = (PlatformCompat) ServiceManager.getService("platform_compat");
        this.mPlatformCompat = platformCompat;
        ProcessList processList = injector.getProcessList(this);
        this.mProcessList = processList;
        processList.init(this, activeUids, platformCompat);
        this.mAppProfiler = new AppProfiler(this, BackgroundThread.getHandler().getLooper(), new LowMemDetector(this));
        this.mPhantomProcessList = new PhantomProcessList(this);
        this.mOomAdjuster = new OomAdjuster(this, processList, activeUids);
        BroadcastConstants broadcastConstants = new BroadcastConstants("bcast_fg_constants");
        broadcastConstants.TIMEOUT = BROADCAST_FG_TIMEOUT;
        BroadcastConstants broadcastConstants2 = new BroadcastConstants("bcast_bg_constants");
        int i = BROADCAST_BG_TIMEOUT;
        broadcastConstants2.TIMEOUT = i;
        BroadcastConstants broadcastConstants3 = new BroadcastConstants("bcast_offload_constants");
        broadcastConstants3.TIMEOUT = i;
        broadcastConstants3.SLOW_TIME = 2147483647L;
        this.mEnableOffloadQueue = SystemProperties.getBoolean("persist.device_config.activity_manager_native_boot.offload_queue_enabled", true);
        boolean z = broadcastConstants.MODERN_QUEUE_ENABLED;
        this.mEnableModernQueue = z;
        if (z) {
            this.mBroadcastQueues = r0;
            BroadcastQueue[] broadcastQueueArr = {new BroadcastQueueModernImpl(this, mainHandler2, broadcastConstants, broadcastConstants2)};
            handler = uiHandler;
            mainHandler = mainHandler2;
        } else {
            this.mBroadcastQueues = r0;
            handler = uiHandler;
            mainHandler = mainHandler2;
            BroadcastQueue[] broadcastQueueArr2 = {new BroadcastQueueImpl(this, mainHandler2, "foreground", broadcastConstants, false, 2), new BroadcastQueueImpl(this, mainHandler, "background", broadcastConstants2, true, 0), new BroadcastQueueImpl(this, mainHandler, "offload_bg", broadcastConstants3, true, 0), new BroadcastQueueImpl(this, mainHandler, "offload_fg", broadcastConstants, true, 0)};
        }
        this.mServices = new ActiveServices(this);
        this.mCpHelper = new ContentProviderHelper(this, true);
        PackageWatchdog packageWatchdog = PackageWatchdog.getInstance(systemUiContext);
        this.mPackageWatchdog = packageWatchdog;
        this.mAppErrors = new AppErrors(systemUiContext, this, packageWatchdog);
        this.mUidObserverController = new UidObserverController(handler);
        File ensureSystemDir = SystemServiceManager.ensureSystemDir();
        BackgroundThread.get();
        BatteryStatsService batteryStatsService = new BatteryStatsService(context, ensureSystemDir, BackgroundThread.getHandler());
        this.mBatteryStatsService = batteryStatsService;
        batteryStatsService.getActiveStatistics().readLocked();
        batteryStatsService.scheduleWriteToDisk();
        this.mOnBattery = batteryStatsService.getActiveStatistics().getIsOnBattery();
        batteryStatsService.getActiveStatistics().setCallback(this);
        this.mOomAdjProfiler.batteryPowerChanged(this.mOnBattery);
        this.mProcessStats = new ProcessStatsService(this, new File(ensureSystemDir, "procstats"));
        File file = new File(ensureSystemDir, "appops_accesses.xml");
        File file2 = new File(ensureSystemDir, "appops.xml");
        MainHandler mainHandler3 = mainHandler;
        this.mAppOpsService = injector.getAppOpsService(file, file2, mainHandler3);
        this.mUgmInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
        UserController userController = new UserController(this);
        this.mUserController = userController;
        injector.mUserController = userController;
        PendingIntentController pendingIntentController = new PendingIntentController(serviceThread.getLooper(), userController, this.mConstants);
        this.mPendingIntentController = pendingIntentController;
        this.mAppRestrictionController = new AppRestrictionController(context, this);
        this.mUseFifoUiScheduling = SystemProperties.getInt("sys.use_fifo_ui", 0) != 0;
        this.mTrackingAssociations = "1".equals(SystemProperties.get("debug.track-associations"));
        IntentFirewall intentFirewall = new IntentFirewall(new IntentFirewallInterface(), mainHandler3);
        this.mIntentFirewall = intentFirewall;
        this.mActivityTaskManager = activityTaskManagerService;
        activityTaskManagerService.initialize(intentFirewall, pendingIntentController, DisplayThread.get().getLooper());
        this.mAtmInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mHiddenApiBlacklist = new HiddenApiSettings(mainHandler3, context);
        Watchdog.getInstance().addMonitor(this);
        Watchdog.getInstance().addThread(mainHandler3);
        updateOomAdjLocked(0);
        try {
            Process.setThreadGroupAndCpuset(BackgroundThread.get().getThreadId(), 2);
            Process.setThreadGroupAndCpuset(this.mOomAdjuster.mCachedAppOptimizer.mCachedAppOptimizerThread.getThreadId(), 2);
        } catch (Exception unused) {
            Slog.w("ActivityManager", "Setting background thread cpuset failed");
        }
        this.mInternal = new LocalService();
        this.mPendingStartActivityUids = new PendingStartActivityUids();
        this.mTraceErrorLogger = new TraceErrorLogger();
        this.mComponentAliasResolver = new ComponentAliasResolver(this);
    }

    public void setSystemServiceManager(SystemServiceManager systemServiceManager) {
        this.mSystemServiceManager = systemServiceManager;
    }

    public void setInstaller(Installer installer) {
        this.mInstaller = installer;
    }

    public final void start() {
        this.mBatteryStatsService.publish();
        this.mAppOpsService.publish();
        this.mProcessStats.publish();
        Slog.d("AppOps", "AppOpsService published");
        LocalServices.addService(ActivityManagerInternal.class, this.mInternal);
        LocalManagerRegistry.addManager(ActivityManagerLocal.class, this.mInternal);
        this.mActivityTaskManager.onActivityManagerInternalAdded();
        this.mPendingIntentController.onActivityManagerInternalAdded();
        this.mAppProfiler.onActivityManagerInternalAdded();
        CriticalEventLog.init();
    }

    public void initPowerManagement() {
        this.mActivityTaskManager.onInitPowerManagement();
        this.mBatteryStatsService.initPowerManagement();
        this.mLocalPowerManager = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
    }

    public final ArraySet<String> getBackgroundLaunchBroadcasts() {
        if (this.mBackgroundLaunchBroadcasts == null) {
            this.mBackgroundLaunchBroadcasts = SystemConfig.getInstance().getAllowImplicitBroadcasts();
        }
        return this.mBackgroundLaunchBroadcasts;
    }

    public void requireAllowedAssociationsLocked(String str) {
        ensureAllowedAssociations();
        if (this.mAllowedAssociations.get(str) == null) {
            this.mAllowedAssociations.put(str, new PackageAssociationInfo(str, new ArraySet(), false));
        }
    }

    public boolean validateAssociationAllowedLocked(String str, int i, String str2, int i2) {
        ensureAllowedAssociations();
        if (i != i2 && UserHandle.getAppId(i) != 1000 && UserHandle.getAppId(i2) != 1000) {
            PackageAssociationInfo packageAssociationInfo = this.mAllowedAssociations.get(str);
            if (packageAssociationInfo != null && !packageAssociationInfo.isPackageAssociationAllowed(str2)) {
                return false;
            }
            PackageAssociationInfo packageAssociationInfo2 = this.mAllowedAssociations.get(str2);
            if (packageAssociationInfo2 != null && !packageAssociationInfo2.isPackageAssociationAllowed(str)) {
                return false;
            }
        }
        return true;
    }

    public final void ensureAllowedAssociations() {
        boolean z;
        ApplicationInfo applicationInfo;
        if (this.mAllowedAssociations == null) {
            ArrayMap<String, ArraySet<String>> allowedAssociations = SystemConfig.getInstance().getAllowedAssociations();
            this.mAllowedAssociations = new ArrayMap<>(allowedAssociations.size());
            getPackageManagerInternal();
            for (int i = 0; i < allowedAssociations.size(); i++) {
                String keyAt = allowedAssociations.keyAt(i);
                ArraySet<String> valueAt = allowedAssociations.valueAt(i);
                try {
                    applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(keyAt, 131072L, 0);
                } catch (RemoteException unused) {
                }
                if (applicationInfo != null && (applicationInfo.flags & 2) != 0) {
                    z = true;
                    this.mAllowedAssociations.put(keyAt, new PackageAssociationInfo(keyAt, valueAt, z));
                }
                z = false;
                this.mAllowedAssociations.put(keyAt, new PackageAssociationInfo(keyAt, valueAt, z));
            }
        }
    }

    public final void updateAssociationForApp(ApplicationInfo applicationInfo) {
        ensureAllowedAssociations();
        PackageAssociationInfo packageAssociationInfo = this.mAllowedAssociations.get(applicationInfo.packageName);
        if (packageAssociationInfo != null) {
            packageAssociationInfo.setDebuggable((applicationInfo.flags & 2) != 0);
        }
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        int i3;
        if (i == 1599295570) {
            ArrayList arrayList = new ArrayList();
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    ArrayMap map = this.mProcessList.getProcessNamesLOSP().getMap();
                    int size = map.size();
                    for (int i4 = 0; i4 < size; i4++) {
                        SparseArray sparseArray = (SparseArray) map.valueAt(i4);
                        int size2 = sparseArray.size();
                        for (int i5 = 0; i5 < size2; i5++) {
                            IApplicationThread thread = ((ProcessRecord) sparseArray.valueAt(i5)).getThread();
                            if (thread != null) {
                                arrayList.add(thread.asBinder());
                            }
                        }
                    }
                } catch (Throwable th) {
                    resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterProcLockedSection();
            int size3 = arrayList.size();
            for (i3 = 0; i3 < size3; i3++) {
                Parcel obtain = Parcel.obtain();
                try {
                    ((IBinder) arrayList.get(i3)).transact(1599295570, obtain, null, 1);
                } catch (RemoteException unused) {
                }
                obtain.recycle();
            }
        }
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException) && !(e instanceof IllegalArgumentException) && !(e instanceof IllegalStateException)) {
                Slog.wtf("ActivityManager", "Activity Manager Crash. UID:" + Binder.getCallingUid() + " PID:" + Binder.getCallingPid() + " TRANS:" + i, e);
            }
            throw e;
        }
    }

    public void updateCpuStats() {
        this.mAppProfiler.updateCpuStats();
    }

    public void updateCpuStatsNow() {
        this.mAppProfiler.updateCpuStatsNow();
    }

    @Override // com.android.server.power.stats.BatteryStatsImpl.BatteryCallback
    public void batteryNeedsCpuUpdate() {
        updateCpuStatsNow();
    }

    @Override // com.android.server.power.stats.BatteryStatsImpl.BatteryCallback
    public void batteryPowerChanged(boolean z) {
        updateCpuStatsNow();
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mOnBattery = z;
                this.mOomAdjProfiler.batteryPowerChanged(z);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    @Override // com.android.server.power.stats.BatteryStatsImpl.BatteryCallback
    public void batteryStatsReset() {
        this.mOomAdjProfiler.reset();
    }

    @Override // com.android.server.power.stats.BatteryStatsImpl.BatteryCallback
    public void batterySendBroadcast(Intent intent) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                broadcastIntentLocked(null, null, null, intent, null, null, 0, null, null, null, null, null, -1, null, false, false, -1, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final ArrayMap<String, IBinder> getCommonServicesLocked(boolean z) {
        if (z) {
            if (this.mIsolatedAppBindArgs == null) {
                ArrayMap<String, IBinder> arrayMap = new ArrayMap<>(1);
                this.mIsolatedAppBindArgs = arrayMap;
                addServiceToMap(arrayMap, "package");
                addServiceToMap(this.mIsolatedAppBindArgs, "permissionmgr");
            }
            return this.mIsolatedAppBindArgs;
        }
        if (this.mAppBindArgs == null) {
            ArrayMap<String, IBinder> arrayMap2 = new ArrayMap<>();
            this.mAppBindArgs = arrayMap2;
            addServiceToMap(arrayMap2, "package");
            addServiceToMap(this.mAppBindArgs, "permissionmgr");
            addServiceToMap(this.mAppBindArgs, "window");
            addServiceToMap(this.mAppBindArgs, "alarm");
            addServiceToMap(this.mAppBindArgs, "display");
            addServiceToMap(this.mAppBindArgs, "network_management");
            addServiceToMap(this.mAppBindArgs, "connectivity");
            addServiceToMap(this.mAppBindArgs, "accessibility");
            addServiceToMap(this.mAppBindArgs, "input_method");
            addServiceToMap(this.mAppBindArgs, "input");
            addServiceToMap(this.mAppBindArgs, "graphicsstats");
            addServiceToMap(this.mAppBindArgs, "appops");
            addServiceToMap(this.mAppBindArgs, "content");
            addServiceToMap(this.mAppBindArgs, "jobscheduler");
            addServiceToMap(this.mAppBindArgs, "notification");
            addServiceToMap(this.mAppBindArgs, "vibrator");
            addServiceToMap(this.mAppBindArgs, "account");
            addServiceToMap(this.mAppBindArgs, "power");
            addServiceToMap(this.mAppBindArgs, "user");
            addServiceToMap(this.mAppBindArgs, "mount");
            addServiceToMap(this.mAppBindArgs, "platform_compat");
        }
        return this.mAppBindArgs;
    }

    public static void addServiceToMap(ArrayMap<String, IBinder> arrayMap, String str) {
        IBinder service = ServiceManager.getService(str);
        if (service != null) {
            arrayMap.put(str, service);
        }
    }

    public void setFocusedRootTask(int i) {
        this.mActivityTaskManager.setFocusedRootTask(i);
    }

    public void registerTaskStackListener(ITaskStackListener iTaskStackListener) {
        this.mActivityTaskManager.registerTaskStackListener(iTaskStackListener);
    }

    public void unregisterTaskStackListener(ITaskStackListener iTaskStackListener) {
        this.mActivityTaskManager.unregisterTaskStackListener(iTaskStackListener);
    }

    @GuardedBy({"this"})
    public final void updateLruProcessLocked(ProcessRecord processRecord, boolean z, ProcessRecord processRecord2) {
        this.mProcessList.updateLruProcessLocked(processRecord, z, processRecord2);
    }

    @GuardedBy({"this"})
    public final void removeLruProcessLocked(ProcessRecord processRecord) {
        this.mProcessList.removeLruProcessLocked(processRecord);
    }

    @GuardedBy({"this"})
    public final ProcessRecord getProcessRecordLocked(String str, int i) {
        return this.mProcessList.getProcessRecordLocked(str, i);
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public final ProcessMap<ProcessRecord> getProcessNamesLOSP() {
        return this.mProcessList.getProcessNamesLOSP();
    }

    public void notifyPackageUse(String str, int i) {
        getPackageManagerInternal().notifyPackageUse(str, i);
    }

    public boolean startIsolatedProcess(String str, String[] strArr, String str2, String str3, int i, Runnable runnable) {
        boolean z;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ApplicationInfo applicationInfo = new ApplicationInfo();
                applicationInfo.uid = 1000;
                applicationInfo.processName = str2;
                applicationInfo.className = str;
                applicationInfo.packageName = PackageManagerShellCommandDataLoader.PACKAGE;
                applicationInfo.seInfoUser = ":complete";
                applicationInfo.targetSdkVersion = Build.VERSION.SDK_INT;
                z = this.mProcessList.startProcessLocked(str2, applicationInfo, false, 0, sNullHostingRecord, 0, true, true, i, false, 0, null, str3, str, strArr, runnable) != null;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return z;
    }

    @GuardedBy({"this"})
    public final ProcessRecord startSdkSandboxProcessLocked(String str, ApplicationInfo applicationInfo, boolean z, int i, HostingRecord hostingRecord, int i2, int i3, String str2) {
        return this.mProcessList.startProcessLocked(str, applicationInfo, z, i, hostingRecord, i2, false, false, 0, true, i3, str2, null, null, null, null);
    }

    @GuardedBy({"this"})
    public final ProcessRecord startProcessLocked(String str, ApplicationInfo applicationInfo, boolean z, int i, HostingRecord hostingRecord, int i2, boolean z2, boolean z3) {
        return this.mProcessList.startProcessLocked(str, applicationInfo, z, i, hostingRecord, i2, z2, z3, 0, false, 0, null, null, null, null, null);
    }

    public boolean isAllowedWhileBooting(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 8) != 0;
    }

    public void updateBatteryStats(ComponentName componentName, int i, int i2, boolean z) {
        this.mBatteryStatsService.updateBatteryStatsOnActivityUsage(componentName.getPackageName(), componentName.getShortClassName(), i, i2, z);
    }

    public void updateActivityUsageStats(ComponentName componentName, int i, int i2, IBinder iBinder, ComponentName componentName2, ActivityId activityId) {
        if (this.mUsageStatsService != null) {
            this.mUsageStatsService.reportEvent(componentName, i, i2, iBinder.hashCode(), componentName2);
            if (i2 == 1) {
                this.mUsageStatsService.reportEvent(componentName.getPackageName(), i, 31);
            }
        }
        ContentCaptureManagerInternal contentCaptureManagerInternal = this.mContentCaptureService;
        if (contentCaptureManagerInternal != null && (i2 == 2 || i2 == 1 || i2 == 23 || i2 == 24)) {
            contentCaptureManagerInternal.notifyActivityEvent(i, componentName, i2, activityId);
        }
        if (this.mVoiceInteractionManagerProvider == null || i2 != 24) {
            return;
        }
        this.mVoiceInteractionManagerProvider.notifyActivityDestroyed(iBinder);
    }

    public void updateForegroundServiceUsageStats(ComponentName componentName, int i, boolean z) {
        if (this.mUsageStatsService != null) {
            this.mUsageStatsService.reportEvent(componentName, i, z ? 19 : 20, 0, null);
        }
    }

    public CompatibilityInfo compatibilityInfoForPackage(ApplicationInfo applicationInfo) {
        return this.mAtmInternal.compatibilityInfoForPackage(applicationInfo);
    }

    public void enforceNotIsolatedCaller(String str) {
        if (UserHandle.isIsolated(Binder.getCallingUid())) {
            throw new SecurityException("Isolated process not allowed to call " + str);
        }
    }

    public void enforceNotIsolatedOrSdkSandboxCaller(String str) {
        enforceNotIsolatedCaller(str);
        if (Process.isSdkSandboxUid(Binder.getCallingUid())) {
            throw new SecurityException("SDK sandbox process not allowed to call " + str);
        }
    }

    public final void enforceAllowedToStartOrBindServiceIfSdkSandbox(Intent intent) {
        if (Process.isSdkSandboxUid(Binder.getCallingUid())) {
            SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
            if (sdkSandboxManagerLocal != null) {
                sdkSandboxManagerLocal.enforceAllowedToStartOrBindService(intent);
                return;
            }
            throw new IllegalStateException("SdkSandboxManagerLocal not found when checking whether SDK sandbox uid may start or bind to a service.");
        }
    }

    public void setPackageScreenCompatMode(String str, int i) {
        this.mActivityTaskManager.setPackageScreenCompatMode(str, i);
    }

    public final boolean hasUsageStatsPermission(String str, int i, int i2) {
        int opMode = this.mAppOpsService.noteOperation(43, i, str, null, false, "", false).getOpMode();
        return opMode == 3 ? checkPermission("android.permission.PACKAGE_USAGE_STATS", i2, i) == 0 : opMode == 0;
    }

    public final boolean hasUsageStatsPermission(String str) {
        return hasUsageStatsPermission(str, Binder.getCallingUid(), Binder.getCallingPid());
    }

    public int getPackageProcessState(final String str, String str2) {
        if (!hasUsageStatsPermission(str2)) {
            enforceCallingPermission("android.permission.PACKAGE_USAGE_STATS", "getPackageProcessState");
        }
        final int[] iArr = {20};
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda7
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.lambda$getPackageProcessState$0(iArr, str, (ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return iArr[0];
    }

    public static /* synthetic */ void lambda$getPackageProcessState$0(int[] iArr, String str, ProcessRecord processRecord) {
        if (iArr[0] > processRecord.mState.getSetProcState()) {
            if (processRecord.getPkgList().containsKey(str) || (processRecord.getPkgDeps() != null && processRecord.getPkgDeps().contains(str))) {
                iArr[0] = processRecord.mState.getSetProcState();
            }
        }
    }

    public boolean setProcessMemoryTrimLevel(String str, int i, int i2) throws RemoteException {
        if (!isCallerShell()) {
            throw new SecurityException("Only shell can call it");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord findProcessLOSP = findProcessLOSP(str, i, "setProcessMemoryTrimLevel");
                if (findProcessLOSP == null) {
                    throw new IllegalArgumentException("Unknown process: " + str);
                }
                IApplicationThread thread = findProcessLOSP.getThread();
                if (thread == null) {
                    throw new IllegalArgumentException("Process has no app thread");
                }
                if (findProcessLOSP.mProfile.getTrimMemoryLevel() >= i2) {
                    throw new IllegalArgumentException("Unable to set a higher trim level than current level");
                }
                if (i2 >= 20 && findProcessLOSP.mState.getCurProcState() <= 6) {
                    throw new IllegalArgumentException("Unable to set a background trim level on a foreground process");
                }
                thread.scheduleTrimMemory(i2);
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    findProcessLOSP.mProfile.setTrimMemoryLevel(i2);
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return true;
    }

    public void dispatchOomAdjObserver(String str) {
        OomAdjObserver oomAdjObserver;
        synchronized (this.mOomAdjObserverLock) {
            oomAdjObserver = this.mCurOomAdjObserver;
        }
        if (oomAdjObserver != null) {
            oomAdjObserver.onOomAdjMessage(str);
        }
    }

    public void setOomAdjObserver(int i, OomAdjObserver oomAdjObserver) {
        synchronized (this.mOomAdjObserverLock) {
            this.mCurOomAdjUid = i;
            this.mCurOomAdjObserver = oomAdjObserver;
        }
    }

    public void clearOomAdjObserver() {
        synchronized (this.mOomAdjObserverLock) {
            this.mCurOomAdjUid = -1;
            this.mCurOomAdjObserver = null;
        }
    }

    public void reportUidInfoMessageLocked(String str, String str2, int i) {
        Slog.i("ActivityManager", str2);
        synchronized (this.mOomAdjObserverLock) {
            if (this.mCurOomAdjObserver != null && i == this.mCurOomAdjUid) {
                this.mUiHandler.obtainMessage(70, str2).sendToTarget();
            }
        }
    }

    @Deprecated
    public int startActivity(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) {
        return this.mActivityTaskManager.startActivity(iApplicationThread, str, null, intent, str2, iBinder, str3, i, i2, profilerInfo, bundle);
    }

    public int startActivityWithFeature(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) {
        return this.mActivityTaskManager.startActivity(iApplicationThread, str, str2, intent, str3, iBinder, str4, i, i2, profilerInfo, bundle);
    }

    @Deprecated
    public final int startActivityAsUser(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) {
        return startActivityAsUserWithFeature(iApplicationThread, str, null, intent, str2, iBinder, str3, i, i2, profilerInfo, bundle, i3);
    }

    public final int startActivityAsUserWithFeature(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) {
        return this.mActivityTaskManager.startActivityAsUser(iApplicationThread, str, str2, intent, str3, iBinder, str4, i, i2, profilerInfo, bundle, i3);
    }

    public WaitResult startActivityAndWait(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) {
        return this.mActivityTaskManager.startActivityAndWait(iApplicationThread, str, str2, intent, str3, iBinder, str4, i, i2, profilerInfo, bundle, i3);
    }

    public final int startActivityFromRecents(int i, Bundle bundle) {
        return this.mActivityTaskManager.startActivityFromRecents(i, bundle);
    }

    public final boolean finishActivity(IBinder iBinder, int i, Intent intent, int i2) {
        return ActivityClient.getInstance().finishActivity(iBinder, i, intent, i2);
    }

    public void setRequestedOrientation(IBinder iBinder, int i) {
        ActivityClient.getInstance().setRequestedOrientation(iBinder, i);
    }

    public final void finishHeavyWeightApp() {
        if (checkCallingPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            String str = "Permission Denial: finishHeavyWeightApp() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.FORCE_STOP_PACKAGES";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        this.mAtmInternal.finishHeavyWeightApp();
    }

    public void crashApplicationWithType(int i, int i2, String str, int i3, String str2, boolean z, int i4) {
        crashApplicationWithTypeWithExtras(i, i2, str, i3, str2, z, i4, null);
    }

    public void crashApplicationWithTypeWithExtras(int i, int i2, String str, int i3, String str2, boolean z, int i4, Bundle bundle) {
        if (checkCallingPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            String str3 = "Permission Denial: crashApplication() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.FORCE_STOP_PACKAGES";
            Slog.w("ActivityManager", str3);
            throw new SecurityException(str3);
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mAppErrors.scheduleAppCrashLocked(i, i2, str, i3, str2, z, i4, bundle);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @GuardedBy({"this"})
    public final void handleAppDiedLocked(final ProcessRecord processRecord, int i, boolean z, boolean z2, boolean z3) {
        if (!cleanUpApplicationRecordLocked(processRecord, i, z, z2, -1, false, z3) && !z) {
            removeLruProcessLocked(processRecord);
            if (i > 0) {
                ProcessList.remove(i);
            }
        }
        this.mAppProfiler.onAppDiedLocked(processRecord);
        this.mAtmInternal.handleAppDied(processRecord.getWindowProcessController(), z, new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$handleAppDiedLocked$1(processRecord);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleAppDiedLocked$1(ProcessRecord processRecord) {
        Slog.w("ActivityManager", "Crash of app " + processRecord.processName + " running instrumentation " + processRecord.getActiveInstrumentation().mClass);
        Bundle bundle = new Bundle();
        bundle.putString("shortMsg", "Process crashed.");
        finishInstrumentationLocked(processRecord, 0, bundle);
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public ProcessRecord getRecordForAppLOSP(IApplicationThread iApplicationThread) {
        if (iApplicationThread == null) {
            return null;
        }
        return getRecordForAppLOSP(iApplicationThread.asBinder());
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public ProcessRecord getRecordForAppLOSP(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        ProcessRecord lRURecordForAppLOSP = this.mProcessList.getLRURecordForAppLOSP(iBinder);
        if (lRURecordForAppLOSP != null) {
            return lRURecordForAppLOSP;
        }
        ArrayMap map = this.mProcessList.getProcessNamesLOSP().getMap();
        for (int size = map.size() - 1; size >= 0; size--) {
            SparseArray sparseArray = (SparseArray) map.valueAt(size);
            for (int size2 = sparseArray.size() - 1; size2 >= 0; size2--) {
                ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(size2);
                IApplicationThread thread = processRecord.getThread();
                if (thread != null && thread.asBinder() == iBinder) {
                    if (!processRecord.isPendingFinishAttach()) {
                        Slog.wtf("ActivityManager", "getRecordForApp: exists in name list but not in LRU list: " + processRecord);
                    }
                    return processRecord;
                }
            }
        }
        return null;
    }

    @GuardedBy({"this"})
    public final void appDiedLocked(ProcessRecord processRecord, String str) {
        appDiedLocked(processRecord, processRecord.getPid(), processRecord.getThread(), false, str);
    }

    @GuardedBy({"this"})
    public final void appDiedLocked(ProcessRecord processRecord, int i, IApplicationThread iApplicationThread, boolean z, String str) {
        ProcessRecord processRecord2;
        IApplicationThread thread;
        synchronized (this.mPidsSelfLocked) {
            processRecord2 = this.mPidsSelfLocked.get(i);
        }
        if (processRecord2 != processRecord) {
            if (z && this.mProcessList.handleDyingAppDeathLocked(processRecord, i)) {
                return;
            }
            Slog.w("ActivityManager", "Spurious death for " + processRecord + ", curProc for " + i + ": " + processRecord2);
            return;
        }
        this.mBatteryStatsService.noteProcessDied(processRecord.info.uid, i);
        boolean z2 = false;
        if (!processRecord.isKilled()) {
            if (!z) {
                Process.killProcessQuiet(i);
                this.mProcessList.noteAppKill(processRecord, 13, 0, str);
            }
            ProcessList.killProcessGroup(processRecord.uid, i);
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    processRecord.setKilled(true);
                } catch (Throwable th) {
                    resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterProcLockedSection();
        }
        int setAdj = processRecord.mState.getSetAdj();
        int setProcState = processRecord.mState.getSetProcState();
        if (processRecord.getPid() == i && (thread = processRecord.getThread()) != null && thread.asBinder() == iApplicationThread.asBinder()) {
            boolean z3 = processRecord.getActiveInstrumentation() == null;
            if (!processRecord.isKilledByAm()) {
                reportUidInfoMessageLocked("ActivityManager", "Process " + processRecord.processName + " (pid " + i + ") has died: " + ProcessList.makeOomAdjString(setAdj, true) + " " + ProcessList.makeProcStateString(setProcState), processRecord.info.uid);
                this.mAppProfiler.setAllowLowerMemLevelLocked(true);
                z2 = z3;
            } else {
                this.mAppProfiler.setAllowLowerMemLevelLocked(false);
            }
            EventLogTags.writeAmProcDied(processRecord.userId, i, processRecord.processName, setAdj, setProcState);
            handleAppDiedLocked(processRecord, i, false, true, z);
            if (z3) {
                updateOomAdjLocked(12);
            }
            if (z2) {
                this.mAppProfiler.doLowMemReportIfNeededLocked(processRecord);
            }
        } else if (processRecord.getPid() != i) {
            reportUidInfoMessageLocked("ActivityManager", "Process " + processRecord.processName + " (pid " + i + ") has died and restarted (pid " + processRecord.getPid() + ").", processRecord.info.uid);
            EventLogTags.writeAmProcDied(processRecord.userId, processRecord.getPid(), processRecord.processName, setAdj, setProcState);
        }
        if (MemoryStatUtil.hasMemcg()) {
            return;
        }
        FrameworkStatsLog.write(65, SystemClock.elapsedRealtime());
    }

    public static File dumpStackTraces(ArrayList<Integer> arrayList, ProcessCpuTracker processCpuTracker, SparseBooleanArray sparseBooleanArray, Future<ArrayList<Integer>> future, StringWriter stringWriter, String str, String str2, Executor executor, AnrLatencyTracker anrLatencyTracker) {
        return dumpStackTraces(arrayList, processCpuTracker, sparseBooleanArray, future, stringWriter, null, str, str2, executor, anrLatencyTracker);
    }

    public static File dumpStackTraces(ArrayList<Integer> arrayList, final ProcessCpuTracker processCpuTracker, final SparseBooleanArray sparseBooleanArray, Future<ArrayList<Integer>> future, StringWriter stringWriter, AtomicLong atomicLong, String str, String str2, Executor executor, final AnrLatencyTracker anrLatencyTracker) {
        String str3;
        if (anrLatencyTracker != null) {
            try {
                anrLatencyTracker.dumpStackTracesStarted();
            } catch (Throwable th) {
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpStackTracesEnded();
                }
                throw th;
            }
        }
        Slog.i("ActivityManager", "dumpStackTraces pids=" + sparseBooleanArray);
        Supplier supplier = processCpuTracker != null ? new Supplier() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda17
            @Override // java.util.function.Supplier
            public final Object get() {
                ArrayList extraPids;
                extraPids = ActivityManagerService.getExtraPids(processCpuTracker, sparseBooleanArray, anrLatencyTracker);
                return extraPids;
            }
        } : null;
        CompletableFuture supplyAsync = supplier != null ? CompletableFuture.supplyAsync(supplier, executor) : null;
        File file = new File("/data/anr");
        try {
            File createAnrDumpFile = createAnrDumpFile(file);
            if (str != null || str2 != null) {
                String absolutePath = createAnrDumpFile.getAbsolutePath();
                StringBuilder sb = new StringBuilder();
                if (str != null) {
                    str3 = "Subject: " + str + "\n\n";
                } else {
                    str3 = "";
                }
                sb.append(str3);
                if (str2 == null) {
                    str2 = "";
                }
                sb.append(str2);
                appendtoANRFile(absolutePath, sb.toString());
            }
            long dumpStackTraces = dumpStackTraces(createAnrDumpFile.getAbsolutePath(), arrayList, future, supplyAsync, anrLatencyTracker);
            if (atomicLong != null) {
                atomicLong.set(dumpStackTraces);
            }
            maybePruneOldTraces(file);
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpStackTracesEnded();
            }
            return createAnrDumpFile;
        } catch (IOException e) {
            Slog.w("ActivityManager", "Exception creating ANR dump file:", e);
            if (stringWriter != null) {
                stringWriter.append("----- Exception creating ANR dump file -----\n");
                e.printStackTrace(new PrintWriter(stringWriter));
            }
            if (anrLatencyTracker != null) {
                anrLatencyTracker.anrSkippedDumpStackTraces();
            }
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpStackTracesEnded();
            }
            return null;
        }
    }

    public static ArrayList<Integer> getExtraPids(ProcessCpuTracker processCpuTracker, SparseBooleanArray sparseBooleanArray, AnrLatencyTracker anrLatencyTracker) {
        if (anrLatencyTracker != null) {
            anrLatencyTracker.processCpuTrackerMethodsCalled();
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        processCpuTracker.init();
        try {
            Thread.sleep(200L);
        } catch (InterruptedException unused) {
        }
        processCpuTracker.update();
        int countWorkingStats = processCpuTracker.countWorkingStats();
        for (int i = 0; i < countWorkingStats && arrayList.size() < 2; i++) {
            ProcessCpuTracker.Stats workingStats = processCpuTracker.getWorkingStats(i);
            if (sparseBooleanArray.indexOfKey(workingStats.pid) >= 0) {
                arrayList.add(Integer.valueOf(workingStats.pid));
            } else {
                Slog.i("ActivityManager", "Skipping next CPU consuming process, not a java proc: " + workingStats.pid);
            }
        }
        if (anrLatencyTracker != null) {
            anrLatencyTracker.processCpuTrackerMethodsReturned();
        }
        return arrayList;
    }

    public static synchronized File createAnrDumpFile(File file) throws IOException {
        File file2;
        synchronized (ActivityManagerService.class) {
            boostPriorityForLockedSection();
            if (sAnrFileDateFormat == null) {
                sAnrFileDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            }
            String format = sAnrFileDateFormat.format(new Date());
            file2 = new File(file, "anr_" + format);
            if (file2.createNewFile()) {
                FileUtils.setPermissions(file2.getAbsolutePath(), FrameworkStatsLog.NON_A11Y_TOOL_SERVICE_WARNING_REPORT, -1, -1);
                resetPriorityAfterLockedSection();
            } else {
                throw new IOException("Unable to create ANR dump file: createNewFile failed");
            }
        }
        return file2;
    }

    public static void maybePruneOldTraces(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        int i = SystemProperties.getInt("tombstoned.max_anr_count", 64);
        long currentTimeMillis = System.currentTimeMillis();
        try {
            Arrays.sort(listFiles, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda22
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((File) obj).lastModified();
                }
            }).reversed());
            for (int i2 = 0; i2 < listFiles.length; i2++) {
                if ((i2 > i || currentTimeMillis - listFiles[i2].lastModified() > BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) && !listFiles[i2].delete()) {
                    Slog.w("ActivityManager", "Unable to prune stale trace file: " + listFiles[i2]);
                }
            }
        } catch (IllegalArgumentException e) {
            Slog.w("ActivityManager", "tombstone modification times changed while sorting; not pruning", e);
        }
    }

    public static long dumpJavaTracesTombstoned(int i, String str, long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int writeUptimeStartHeaderForPid = writeUptimeStartHeaderForPid(i, str);
        boolean dumpJavaBacktraceToFileTimeout = Debug.dumpJavaBacktraceToFileTimeout(i, str, (int) (j / 1000));
        if (dumpJavaBacktraceToFileTimeout) {
            try {
            } catch (Exception e) {
                Slog.w("ActivityManager", "Unable to get ANR file size", e);
            }
            if (new File(str).length() - writeUptimeStartHeaderForPid < 100) {
                Slog.w("ActivityManager", "Successfully created Java ANR file is empty!");
                dumpJavaBacktraceToFileTimeout = false;
            }
        }
        if (!dumpJavaBacktraceToFileTimeout) {
            Slog.w("ActivityManager", "Dumping Java threads failed, initiating native stack dump.");
            if (!Debug.dumpNativeBacktraceToFileTimeout(i, str, NATIVE_DUMP_TIMEOUT_MS / 1000)) {
                Slog.w("ActivityManager", "Native stack dump failed!");
            }
        }
        return SystemClock.elapsedRealtime() - elapsedRealtime;
    }

    public static int appendtoANRFile(String str, String str2) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str, true);
            byte[] bytes = str2.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(bytes);
            int length = bytes.length;
            fileOutputStream.close();
            return length;
        } catch (IOException e) {
            Slog.w("ActivityManager", "Exception writing to ANR dump file:", e);
            return 0;
        }
    }

    public static int writeUptimeStartHeaderForPid(int i, String str) {
        return appendtoANRFile(str, "----- dumping pid: " + i + " at " + SystemClock.uptimeMillis() + "\n");
    }

    public static long dumpStackTraces(String str, ArrayList<Integer> arrayList, Future<ArrayList<Integer>> future, Future<ArrayList<Integer>> future2, AnrLatencyTracker anrLatencyTracker) {
        Slog.i("ActivityManager", "Dumping to " + str);
        long j = (long) (Build.HW_TIMEOUT_MULTIPLIER * 20000);
        long j2 = -1;
        if (arrayList != null) {
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingFirstPidsStarted();
            }
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                int intValue = arrayList.get(i).intValue();
                boolean z = i == 0 && MY_PID != intValue;
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidStarted(intValue);
                }
                Slog.i("ActivityManager", "Collecting stacks for pid " + intValue);
                long dumpJavaTracesTombstoned = dumpJavaTracesTombstoned(intValue, str, j);
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidEnded();
                }
                j -= dumpJavaTracesTombstoned;
                if (j <= 0) {
                    Slog.e("ActivityManager", "Aborting stack trace dump (current firstPid=" + intValue + "); deadline exceeded.");
                    return j2;
                }
                if (z) {
                    j2 = new File(str).length();
                    if (anrLatencyTracker != null) {
                        appendtoANRFile(str, anrLatencyTracker.dumpAsCommaSeparatedArrayWithHeader());
                    }
                }
                i++;
            }
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingFirstPidsEnded();
            }
        }
        ArrayList<Integer> collectPids = collectPids(future, "native pids");
        Slog.i("ActivityManager", "dumpStackTraces nativepids=" + collectPids);
        if (collectPids != null) {
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingNativePidsStarted();
            }
            Iterator<Integer> it = collectPids.iterator();
            while (it.hasNext()) {
                int intValue2 = it.next().intValue();
                Slog.i("ActivityManager", "Collecting stacks for native pid " + intValue2);
                long min = Math.min((long) NATIVE_DUMP_TIMEOUT_MS, j);
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidStarted(intValue2);
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                Debug.dumpNativeBacktraceToFileTimeout(intValue2, str, (int) (min / 1000));
                long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidEnded();
                }
                j -= elapsedRealtime2;
                if (j <= 0) {
                    Slog.e("ActivityManager", "Aborting stack trace dump (current native pid=" + intValue2 + "); deadline exceeded.");
                    return j2;
                }
            }
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingNativePidsEnded();
            }
        }
        ArrayList<Integer> collectPids2 = collectPids(future2, "extra pids");
        if (future2 != null) {
            try {
                collectPids2 = future2.get();
            } catch (InterruptedException e) {
                Slog.w("ActivityManager", "Interrupted while collecting extra pids", e);
            } catch (ExecutionException e2) {
                Slog.w("ActivityManager", "Failed to collect extra pids", e2.getCause());
            }
        }
        Slog.i("ActivityManager", "dumpStackTraces extraPids=" + collectPids2);
        if (collectPids2 != null) {
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingExtraPidsStarted();
            }
            Iterator<Integer> it2 = collectPids2.iterator();
            while (it2.hasNext()) {
                int intValue3 = it2.next().intValue();
                Slog.i("ActivityManager", "Collecting stacks for extra pid " + intValue3);
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidStarted(intValue3);
                }
                long dumpJavaTracesTombstoned2 = dumpJavaTracesTombstoned(intValue3, str, j);
                if (anrLatencyTracker != null) {
                    anrLatencyTracker.dumpingPidEnded();
                }
                j -= dumpJavaTracesTombstoned2;
                if (j <= 0) {
                    Slog.e("ActivityManager", "Aborting stack trace dump (current extra pid=" + intValue3 + "); deadline exceeded.");
                    return j2;
                }
            }
            if (anrLatencyTracker != null) {
                anrLatencyTracker.dumpingExtraPidsEnded();
            }
        }
        appendtoANRFile(str, "----- dumping ended at " + SystemClock.uptimeMillis() + "\n");
        Slog.i("ActivityManager", "Done dumping");
        return j2;
    }

    public static ArrayList<Integer> collectPids(Future<ArrayList<Integer>> future, String str) {
        if (future == null) {
            return null;
        }
        try {
            return future.get();
        } catch (InterruptedException e) {
            Slog.w("ActivityManager", "Interrupted while collecting " + str, e);
            return null;
        } catch (ExecutionException e2) {
            Slog.w("ActivityManager", "Failed to collect " + str, e2.getCause());
            return null;
        }
    }

    public boolean clearApplicationUserData(String str, boolean z, final IPackageDataObserver iPackageDataObserver, int i) {
        long j;
        boolean z2;
        enforceNotIsolatedCaller("clearApplicationUserData");
        final int callingUid = Binder.getCallingUid();
        final int callingPid = Binder.getCallingPid();
        final int handleIncomingUser = this.mUserController.handleIncomingUser(callingPid, callingUid, i, false, 2, "clearApplicationUserData", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IPackageManager packageManager = AppGlobals.getPackageManager();
            if (!getPackageManagerInternal().isPackageDataProtected(handleIncomingUser, str)) {
                z2 = true;
            } else if (ActivityManager.checkUidPermission("android.permission.MANAGE_USERS", callingUid) == 0) {
                throw new SecurityException("Cannot clear data for a protected package: " + str);
            } else {
                z2 = false;
            }
            ApplicationInfo applicationInfo = null;
            if (z2) {
                try {
                    applicationInfo = packageManager.getApplicationInfo(str, 8192L, handleIncomingUser);
                } catch (RemoteException unused) {
                }
                z2 = (applicationInfo != null && applicationInfo.uid == callingUid) || checkComponentPermission("android.permission.CLEAR_APP_USER_DATA", callingPid, callingUid, -1, true) == 0;
            }
            final ApplicationInfo applicationInfo2 = applicationInfo;
            try {
                if (!z2) {
                    throw new SecurityException("PID " + callingPid + " does not have permission android.permission.CLEAR_APP_USER_DATA to clear data of package " + str);
                }
                boolean hasInstantApplicationMetadata = getPackageManagerInternal().hasInstantApplicationMetadata(str, handleIncomingUser);
                boolean z3 = applicationInfo2 == null && !hasInstantApplicationMetadata;
                boolean z4 = (applicationInfo2 != null && applicationInfo2.isInstantApp()) || hasInstantApplicationMetadata;
                boolean z5 = checkComponentPermission("android.permission.ACCESS_INSTANT_APPS", callingPid, callingUid, -1, true) == 0;
                if (!z3 && (!z4 || z5)) {
                    synchronized (this) {
                        boostPriorityForLockedSection();
                        if (applicationInfo2 != null) {
                            forceStopPackageLocked(str, applicationInfo2.uid, "clear data");
                            this.mAtmInternal.removeRecentTasksByPackageName(str, handleIncomingUser);
                        }
                    }
                    resetPriorityAfterLockedSection();
                    final boolean z6 = z4;
                    try {
                        packageManager.clearApplicationUserData(str, new IPackageDataObserver.Stub() { // from class: com.android.server.am.ActivityManagerService.6
                            public void onRemoveCompleted(String str2, boolean z7) throws RemoteException {
                                if (applicationInfo2 != null) {
                                    synchronized (ActivityManagerService.this) {
                                        try {
                                            ActivityManagerService.boostPriorityForLockedSection();
                                            ActivityManagerService.this.finishForceStopPackageLocked(str2, applicationInfo2.uid);
                                        } catch (Throwable th) {
                                            ActivityManagerService.resetPriorityAfterLockedSection();
                                            throw th;
                                        }
                                    }
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                }
                                if (z7) {
                                    Intent intent = new Intent("android.intent.action.PACKAGE_DATA_CLEARED", Uri.fromParts("package", str2, null));
                                    intent.addFlags(83886080);
                                    ApplicationInfo applicationInfo3 = applicationInfo2;
                                    intent.putExtra("android.intent.extra.UID", applicationInfo3 != null ? applicationInfo3.uid : -1);
                                    intent.putExtra("android.intent.extra.user_handle", handleIncomingUser);
                                    if (z6) {
                                        intent.putExtra("android.intent.extra.PACKAGE_NAME", str2);
                                    }
                                    ActivityManagerService.this.broadcastIntentInPackage(PackageManagerShellCommandDataLoader.PACKAGE, null, 1000, callingUid, callingPid, intent, null, null, null, 0, null, null, z6 ? "android.permission.ACCESS_INSTANT_APPS" : null, null, false, false, handleIncomingUser, BackgroundStartPrivileges.NONE, ActivityManagerService.this.mPackageManagerInt.getVisibilityAllowList(str2, handleIncomingUser));
                                }
                                IPackageDataObserver iPackageDataObserver2 = iPackageDataObserver;
                                if (iPackageDataObserver2 != null) {
                                    iPackageDataObserver2.onRemoveCompleted(str2, z7);
                                }
                            }
                        }, handleIncomingUser);
                        if (applicationInfo2 != null) {
                            if (!z) {
                                this.mUgmInternal.removeUriPermissionsForPackage(str, handleIncomingUser, true, false);
                                INotificationManager service = NotificationManager.getService();
                                int i2 = applicationInfo2.uid;
                                service.clearData(str, i2, callingUid == i2);
                            }
                            ((JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class)).cancelJobsForUid(applicationInfo2.uid, true, 13, 8, "clear data");
                            ((AlarmManagerInternal) LocalServices.getService(AlarmManagerInternal.class)).removeAlarmsForUid(applicationInfo2.uid);
                        }
                    } catch (RemoteException unused2) {
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
                j = clearCallingIdentity;
                Slog.w("ActivityManager", "Invalid packageName: " + str);
                if (iPackageDataObserver != null) {
                    try {
                        iPackageDataObserver.onRemoveCompleted(str, false);
                    } catch (RemoteException unused3) {
                        Slog.i("ActivityManager", "Observer no longer exists.");
                    }
                }
                Binder.restoreCallingIdentity(j);
                return false;
            } catch (Throwable th) {
                th = th;
                Binder.restoreCallingIdentity(j);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            j = clearCallingIdentity;
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:58:? -> B:41:0x00da). Please submit an issue!!! */
    public void killBackgroundProcesses(String str, int i) {
        int i2;
        ActivityManagerGlobalLock activityManagerGlobalLock;
        int i3;
        int i4;
        if (checkCallingPermission("android.permission.KILL_BACKGROUND_PROCESSES") != 0 && checkCallingPermission("android.permission.RESTART_PACKAGES") != 0) {
            String str2 = "Permission Denial: killBackgroundProcesses() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.KILL_BACKGROUND_PROCESSES";
            Slog.w("ActivityManager", str2);
            throw new SecurityException(str2);
        }
        boolean z = checkCallingPermission("android.permission.KILL_ALL_BACKGROUND_PROCESSES") == 0;
        int callingUid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(callingUid);
        int[] expandUserId = this.mUserController.expandUserId(this.mUserController.handleIncomingUser(Binder.getCallingPid(), callingUid, i, true, 2, "killBackgroundProcesses", null));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IPackageManager packageManager = AppGlobals.getPackageManager();
            int length = expandUserId.length;
            int i5 = 0;
            while (i5 < length) {
                int i6 = expandUserId[i5];
                try {
                    i2 = UserHandle.getAppId(packageManager.getPackageUid(str, 268435456L, i6));
                } catch (RemoteException unused) {
                    i2 = -1;
                }
                if (i2 == -1 || (!z && i2 != appId)) {
                    Slog.w("ActivityManager", "Invalid packageName: " + str);
                    return;
                }
                synchronized (this) {
                    boostPriorityForLockedSection();
                    ActivityManagerGlobalLock activityManagerGlobalLock2 = this.mProcLock;
                    synchronized (activityManagerGlobalLock2) {
                        try {
                            boostPriorityForProcLockedSection();
                            activityManagerGlobalLock = activityManagerGlobalLock2;
                            i3 = i5;
                            i4 = appId;
                        } catch (Throwable th) {
                            th = th;
                            activityManagerGlobalLock = activityManagerGlobalLock2;
                            resetPriorityAfterProcLockedSection();
                            throw th;
                        }
                        try {
                            this.mProcessList.killPackageProcessesLSP(str, i2, i6, 500, 10, 24, "kill background");
                            resetPriorityAfterProcLockedSection();
                        } catch (Throwable th2) {
                            th = th2;
                            resetPriorityAfterProcLockedSection();
                            throw th;
                        }
                    }
                }
                resetPriorityAfterLockedSection();
                i5 = i3 + 1;
                appId = i4;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void killAllBackgroundProcesses() {
        if (checkCallingPermission("android.permission.KILL_ALL_BACKGROUND_PROCESSES") != 0) {
            String str = "Permission Denial: killAllBackgroundProcesses() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.KILL_ALL_BACKGROUND_PROCESSES";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                this.mAppProfiler.setAllowLowerMemLevelLocked(true);
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.killPackageProcessesLSP(null, -1, -1, 900, 10, 24, "kill all background");
                }
                resetPriorityAfterProcLockedSection();
                this.mAppProfiler.doLowMemReportIfNeededLocked(null);
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void killAllBackgroundProcessesExcept(int i, int i2) {
        if (checkCallingPermission("android.permission.KILL_ALL_BACKGROUND_PROCESSES") != 0) {
            String str = "Permission Denial: killAllBackgroundProcessesExcept() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.KILL_ALL_BACKGROUND_PROCESSES";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    try {
                        boostPriorityForProcLockedSection();
                        this.mProcessList.killAllBackgroundProcessesExceptLSP(i, i2);
                    } catch (Throwable th) {
                        resetPriorityAfterProcLockedSection();
                        throw th;
                    }
                }
                resetPriorityAfterProcLockedSection();
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void stopAppForUser(String str, int i) {
        if (checkCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS") != 0) {
            String str2 = "Permission Denial: stopAppForUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.MANAGE_ACTIVITY_TASKS";
            Slog.w("ActivityManager", str2);
            throw new SecurityException(str2);
        }
        int handleIncomingUser = this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, 2, "stopAppForUser", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            stopAppForUserInternal(str, handleIncomingUser);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean registerForegroundServiceObserver(IForegroundServiceObserver iForegroundServiceObserver) {
        boolean registerForegroundServiceObserverLocked;
        int callingUid = Binder.getCallingUid();
        int checkCallingPermission = checkCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS");
        int checkCallingPermission2 = checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
        if (checkCallingPermission != 0 || checkCallingPermission2 != 0) {
            String str = "Permission Denial: registerForegroundServiceObserver() from pid=" + Binder.getCallingPid() + ", uid=" + callingUid + " requires android.permission.MANAGE_ACTIVITY_TASKS and android.permission.INTERACT_ACROSS_USERS_FULL";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                registerForegroundServiceObserverLocked = this.mServices.registerForegroundServiceObserverLocked(callingUid, iForegroundServiceObserver);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return registerForegroundServiceObserverLocked;
    }

    public void forceStopPackage(String str, int i) {
        int i2;
        if (checkCallingPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            String str2 = "Permission Denial: forceStopPackage() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.FORCE_STOP_PACKAGES";
            Slog.w("ActivityManager", str2);
            throw new SecurityException(str2);
        }
        int callingPid = Binder.getCallingPid();
        int handleIncomingUser = this.mUserController.handleIncomingUser(callingPid, Binder.getCallingUid(), i, true, 2, "forceStopPackage", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IPackageManager packageManager = AppGlobals.getPackageManager();
            synchronized (this) {
                boostPriorityForLockedSection();
                int i3 = -1;
                int[] users = handleIncomingUser == -1 ? this.mUserController.getUsers() : new int[]{handleIncomingUser};
                int length = users.length;
                int i4 = 0;
                while (i4 < length) {
                    int i5 = users[i4];
                    if (getPackageManagerInternal().isPackageStateProtected(str, i5)) {
                        Slog.w("ActivityManager", "Ignoring request to force stop protected package " + str + " u" + i5);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    try {
                        i2 = packageManager.getPackageUid(str, 268435456L, i5);
                    } catch (RemoteException unused) {
                        i2 = i3;
                    }
                    if (i2 == i3) {
                        Slog.w("ActivityManager", "Invalid packageName: " + str);
                    } else {
                        try {
                            packageManager.setPackageStoppedState(str, true, i5);
                        } catch (RemoteException unused2) {
                        } catch (IllegalArgumentException e) {
                            Slog.w("ActivityManager", "Failed trying to unstop package " + str + ": " + e);
                        }
                        if (this.mUserController.isUserRunning(i5, 0)) {
                            forceStopPackageLocked(str, i2, "from pid " + callingPid);
                            finishForceStopPackageLocked(str, i2);
                        }
                    }
                    i4++;
                    i3 = -1;
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addPackageDependency(String str) {
        ProcessRecord processRecord;
        if (Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        int callingUid = Binder.getCallingUid();
        if (getPackageManagerInternal().filterAppAccess(str, callingUid, UserHandle.getUserId(callingUid))) {
            Slog.w("ActivityManager", "Failed trying to add dependency on non-existing package: " + str);
            return;
        }
        synchronized (this.mPidsSelfLocked) {
            processRecord = this.mPidsSelfLocked.get(Binder.getCallingPid());
        }
        if (processRecord != null) {
            ArraySet<String> pkgDeps = processRecord.getPkgDeps();
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    synchronized (this.mProcLock) {
                        boostPriorityForProcLockedSection();
                        if (pkgDeps == null) {
                            pkgDeps = new ArraySet<>(1);
                            processRecord.setPkgDeps(pkgDeps);
                        }
                        pkgDeps.add(str);
                    }
                    resetPriorityAfterProcLockedSection();
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public void killApplication(String str, int i, int i2, String str2, int i3) {
        if (str == null) {
            return;
        }
        if (i < 0) {
            Slog.w("ActivityManager", "Invalid appid specified for pkg : " + str);
            return;
        }
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) == 1000) {
            Message obtainMessage = this.mHandler.obtainMessage(22);
            obtainMessage.arg1 = i;
            obtainMessage.arg2 = i2;
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = str2;
            obtain.arg3 = Integer.valueOf(i3);
            obtainMessage.obj = obtain;
            this.mHandler.sendMessage(obtainMessage);
            return;
        }
        throw new SecurityException(callingUid + " cannot kill pkg: " + str);
    }

    public void closeSystemDialogs(String str) {
        this.mAtmInternal.closeSystemDialogs(str);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:103:? -> B:80:0x013d). Please submit an issue!!! */
    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] iArr) {
        boolean z;
        final ProcessRecord processRecord;
        ProcessProfileRecord processProfileRecord;
        int i;
        Object obj;
        long j;
        boolean z2;
        ActiveInstrumentation activeInstrumentation;
        int i2;
        ActivityManagerService activityManagerService = this;
        int[] iArr2 = iArr;
        activityManagerService.enforceNotIsolatedCaller("getProcessMemoryInfo");
        long uptimeMillis = SystemClock.uptimeMillis() - activityManagerService.mConstants.MEMORY_INFO_THROTTLE_TIME;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        boolean z3 = ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid) == 0;
        boolean isGetTasksAllowed = activityManagerService.mAtmInternal.isGetTasksAllowed("getProcessMemoryInfo", callingPid, callingUid);
        synchronized (activityManagerService.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                synchronized (activityManagerService.mPidsSelfLocked) {
                    ProcessRecord processRecord2 = activityManagerService.mPidsSelfLocked.get(callingPid);
                    z = (processRecord2 == null || (activeInstrumentation = processRecord2.getActiveInstrumentation()) == null || ((i2 = activeInstrumentation.mSourceUid) != 2000 && i2 != 0)) ? false : true;
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        Debug.MemoryInfo[] memoryInfoArr = new Debug.MemoryInfo[iArr2.length];
        int length = iArr2.length - 1;
        while (length >= 0) {
            final Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            memoryInfoArr[length] = memoryInfo;
            synchronized (activityManagerService.mAppProfiler.mProfilerLock) {
                synchronized (activityManagerService.mPidsSelfLocked) {
                    processRecord = activityManagerService.mPidsSelfLocked.get(iArr2[length]);
                    if (processRecord != null) {
                        ProcessProfileRecord processProfileRecord2 = processRecord.mProfile;
                        processProfileRecord = processProfileRecord2;
                        i = processProfileRecord2.getSetAdj();
                    } else {
                        processProfileRecord = null;
                        i = 0;
                    }
                }
            }
            int i3 = processRecord != null ? processRecord.uid : -1;
            int userId2 = processRecord != null ? UserHandle.getUserId(i3) : -1;
            if (callingUid == i3 || (isGetTasksAllowed && (z3 || userId2 == userId))) {
                if (processRecord != null) {
                    synchronized (activityManagerService.mAppProfiler.mProfilerLock) {
                        if (processProfileRecord.getLastMemInfoTime() >= uptimeMillis && processProfileRecord.getLastMemInfo() != null && !z) {
                            memoryInfo.set(processProfileRecord.getLastMemInfo());
                        }
                    }
                }
                long currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                Debug.MemoryInfo memoryInfo2 = new Debug.MemoryInfo();
                Debug.getMemoryInfo(iArr2[length], memoryInfo2);
                final long currentThreadTimeMillis2 = SystemClock.currentThreadTimeMillis() - currentThreadTimeMillis;
                memoryInfo.set(memoryInfo2);
                if (processRecord != null) {
                    Object obj2 = activityManagerService.mAppProfiler.mProfilerLock;
                    synchronized (obj2) {
                        final ProcessProfileRecord processProfileRecord3 = processProfileRecord;
                        try {
                            processProfileRecord3.setLastMemInfo(memoryInfo2);
                            j = uptimeMillis;
                            processProfileRecord3.setLastMemInfoTime(SystemClock.uptimeMillis());
                            if (processProfileRecord3.getThread() == null || processProfileRecord3.getSetAdj() != i) {
                                z2 = z;
                                obj = obj2;
                            } else {
                                z2 = z;
                                processProfileRecord3.addPss(memoryInfo.getTotalPss(), memoryInfo.getTotalUss(), memoryInfo.getTotalRss(), false, 4, currentThreadTimeMillis2);
                                obj = obj2;
                                try {
                                    processRecord.getPkgList().forEachPackageProcessStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda0
                                        @Override // java.util.function.Consumer
                                        public final void accept(Object obj3) {
                                            ActivityManagerService.lambda$getProcessMemoryInfo$3(ProcessRecord.this, memoryInfo, currentThreadTimeMillis2, processProfileRecord3, (ProcessStats.ProcessStateHolder) obj3);
                                        }
                                    });
                                } catch (Throwable th2) {
                                    th = th2;
                                    throw th;
                                }
                            }
                            length--;
                            activityManagerService = this;
                            iArr2 = iArr;
                            uptimeMillis = j;
                            z = z2;
                        } catch (Throwable th3) {
                            th = th3;
                            obj = obj2;
                            throw th;
                        }
                    }
                }
            }
            j = uptimeMillis;
            z2 = z;
            length--;
            activityManagerService = this;
            iArr2 = iArr;
            uptimeMillis = j;
            z = z2;
        }
        return memoryInfoArr;
    }

    public static /* synthetic */ void lambda$getProcessMemoryInfo$3(ProcessRecord processRecord, Debug.MemoryInfo memoryInfo, long j, ProcessProfileRecord processProfileRecord, ProcessStats.ProcessStateHolder processStateHolder) {
        ProcessState processState = processStateHolder.state;
        FrameworkStatsLog.write(18, processRecord.info.uid, processState != null ? processState.getName() : processRecord.processName, processState != null ? processState.getPackage() : processRecord.info.packageName, memoryInfo.getTotalPss(), memoryInfo.getTotalUss(), memoryInfo.getTotalRss(), 4, j, processStateHolder.appVersion, processProfileRecord.getCurrentHostingComponentTypes(), processProfileRecord.getHistoricalHostingComponentTypes());
    }

    public long[] getProcessPss(int[] iArr) {
        final ProcessRecord processRecord;
        int setAdj;
        enforceNotIsolatedCaller("getProcessPss");
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        int i = 0;
        boolean z = ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid) == 0;
        boolean isGetTasksAllowed = this.mAtmInternal.isGetTasksAllowed("getProcessPss", callingPid, callingUid);
        long[] jArr = new long[iArr.length];
        int length = iArr.length - 1;
        while (length >= 0) {
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    synchronized (this.mPidsSelfLocked) {
                        processRecord = this.mPidsSelfLocked.get(iArr[length]);
                        setAdj = processRecord != null ? processRecord.mState.getSetAdj() : i;
                    }
                } catch (Throwable th) {
                    resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterProcLockedSection();
            if (isGetTasksAllowed && (z || UserHandle.getUserId(processRecord.uid) == userId)) {
                final long[] jArr2 = new long[3];
                long currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                final long pss = Debug.getPss(iArr[length], jArr2, null);
                jArr[length] = pss;
                final long currentThreadTimeMillis2 = SystemClock.currentThreadTimeMillis() - currentThreadTimeMillis;
                if (processRecord != null) {
                    final ProcessProfileRecord processProfileRecord = processRecord.mProfile;
                    synchronized (this.mAppProfiler.mProfilerLock) {
                        if (processProfileRecord.getThread() != null && processProfileRecord.getSetAdj() == setAdj) {
                            processProfileRecord.addPss(pss, jArr2[i], jArr2[2], false, 3, currentThreadTimeMillis2);
                            processRecord.getPkgList().forEachPackageProcessStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda15
                                @Override // java.util.function.Consumer
                                public final void accept(Object obj) {
                                    ActivityManagerService.lambda$getProcessPss$4(ProcessRecord.this, pss, jArr2, currentThreadTimeMillis2, processProfileRecord, (ProcessStats.ProcessStateHolder) obj);
                                }
                            });
                        }
                    }
                } else {
                    continue;
                }
            }
            length--;
            i = 0;
        }
        return jArr;
    }

    public static /* synthetic */ void lambda$getProcessPss$4(ProcessRecord processRecord, long j, long[] jArr, long j2, ProcessProfileRecord processProfileRecord, ProcessStats.ProcessStateHolder processStateHolder) {
        FrameworkStatsLog.write(18, processRecord.info.uid, processStateHolder.state.getName(), processStateHolder.state.getPackage(), j, jArr[0], jArr[2], 3, j2, processStateHolder.appVersion, processProfileRecord.getCurrentHostingComponentTypes(), processProfileRecord.getHistoricalHostingComponentTypes());
    }

    public void killApplicationProcess(String str, int i) {
        IApplicationThread thread;
        if (str == null) {
            return;
        }
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000) {
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    ProcessRecord processRecordLocked = getProcessRecordLocked(str, i);
                    if (processRecordLocked != null && (thread = processRecordLocked.getThread()) != null) {
                        try {
                            thread.scheduleSuicide();
                        } catch (RemoteException unused) {
                        }
                    } else {
                        Slog.w("ActivityManager", "Process/uid not found attempting kill of " + str + " / " + i);
                    }
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException(callingUid + " cannot kill app process: " + str);
    }

    @GuardedBy({"this"})
    public final void forceStopPackageLocked(String str, int i, String str2) {
        forceStopPackageLocked(str, UserHandle.getAppId(i), false, false, true, false, false, UserHandle.getUserId(i), str2);
    }

    @GuardedBy({"this"})
    public final void finishForceStopPackageLocked(String str, int i) {
        Intent intent = new Intent("android.intent.action.PACKAGE_RESTARTED", Uri.fromParts("package", str, null));
        if (!this.mProcessesReady) {
            intent.addFlags(1342177280);
        }
        int userId = UserHandle.getUserId(i);
        int[] visibilityAllowList = getPackageManagerInternal().getVisibilityAllowList(str, userId);
        intent.putExtra("android.intent.extra.UID", i);
        intent.putExtra("android.intent.extra.user_handle", userId);
        broadcastIntentLocked(null, null, null, intent, null, null, null, 0, null, null, null, null, null, -1, null, false, false, MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), userId, BackgroundStartPrivileges.NONE, visibilityAllowList, null);
    }

    public final void cleanupDisabledPackageComponentsLocked(String str, int i, String[] strArr) {
        ArraySet arraySet;
        IPackageManager packageManager = AppGlobals.getPackageManager();
        if (strArr == null) {
            return;
        }
        int length = strArr.length - 1;
        boolean z = false;
        ArraySet arraySet2 = null;
        while (true) {
            if (length < 0) {
                arraySet = arraySet2;
                break;
            }
            String str2 = strArr[length];
            if (str2.equals(str)) {
                try {
                    int applicationEnabledSetting = packageManager.getApplicationEnabledSetting(str, i != -1 ? i : 0);
                    z = (applicationEnabledSetting == 1 || applicationEnabledSetting == 0) ? false : true;
                    if (z) {
                        arraySet = null;
                        break;
                    }
                } catch (Exception unused) {
                    return;
                }
            } else {
                try {
                    int componentEnabledSetting = packageManager.getComponentEnabledSetting(new ComponentName(str, str2), i != -1 ? i : 0);
                    if (componentEnabledSetting != 1 && componentEnabledSetting != 0) {
                        if (arraySet2 == null) {
                            arraySet2 = new ArraySet(strArr.length);
                        }
                        arraySet2.add(str2);
                    }
                } catch (Exception unused2) {
                    return;
                }
            }
            length--;
        }
        if (z || arraySet != null) {
            this.mAtmInternal.cleanupDisabledPackageComponents(str, arraySet, i, this.mBooted);
            ArraySet arraySet3 = arraySet;
            this.mServices.bringDownDisabledPackageServicesLocked(str, arraySet3, i, false, false, true);
            ArrayList<ContentProviderRecord> arrayList = new ArrayList<>();
            this.mCpHelper.getProviderMap().collectPackageProvidersLocked(str, (Set<String>) arraySet3, true, false, i, arrayList);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mCpHelper.removeDyingProviderLocked(null, arrayList.get(size), true);
            }
            for (int length2 = this.mBroadcastQueues.length - 1; length2 >= 0; length2--) {
                this.mBroadcastQueues[length2].cleanupDisabledPackageReceiversLocked(str, arraySet, i);
            }
        }
    }

    public final boolean clearBroadcastQueueForUserLocked(int i) {
        boolean z = false;
        for (int length = this.mBroadcastQueues.length - 1; length >= 0; length--) {
            z |= this.mBroadcastQueues[length].cleanupDisabledPackageReceiversLocked(null, null, i);
        }
        return z;
    }

    @GuardedBy({"this"})
    public final void forceStopAppZygoteLocked(String str, int i, int i2) {
        if (str == null) {
            return;
        }
        if (i < 0) {
            i = UserHandle.getAppId(getPackageManagerInternal().getPackageUid(str, 272629760L, 0));
        }
        this.mProcessList.killAppZygotesLocked(str, i, i2, true);
    }

    public void stopAppForUserInternal(String str, int i) {
        int packageUid = getPackageManagerInternal().getPackageUid(str, 272629760L, i);
        if (packageUid < 0) {
            Slog.w("ActivityManager", "Asked to stop " + str + "/u" + i + " but does not exist in that user");
        } else if (getPackageManagerInternal().isPackageStateProtected(str, i)) {
            Slog.w("ActivityManager", "Asked to stop " + str + "/u" + i + " but it is protected");
        } else {
            Slog.i("ActivityManager", "Stopping app for user: " + str + "/" + i);
            int appId = UserHandle.getAppId(packageUid);
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    try {
                        synchronized (this.mProcLock) {
                            try {
                                boostPriorityForProcLockedSection();
                                this.mAtmInternal.onForceStopPackage(str, true, false, i);
                                ProcessList processList = this.mProcessList;
                                processList.killPackageProcessesLSP(str, appId, i, -10000, true, false, true, false, true, false, 10, 23, "fully stop " + str + "/" + i + " by user request");
                                resetPriorityAfterProcLockedSection();
                                this.mServices.bringDownDisabledPackageServicesLocked(str, null, i, false, true, true);
                                if (this.mBooted) {
                                    this.mAtmInternal.resumeTopActivities(true);
                                }
                            } catch (Throwable th) {
                                th = th;
                                resetPriorityAfterProcLockedSection();
                                throw th;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Throwable th3) {
                    resetPriorityAfterLockedSection();
                    throw th3;
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    @GuardedBy({"this"})
    public final boolean forceStopPackageLocked(String str, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i2, String str2) {
        return forceStopPackageLocked(str, i, z, z2, z3, z4, z5, i2, str2, str == null ? 11 : 10);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v3, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r8v4 */
    @GuardedBy({"this"})
    public final boolean forceStopPackageLocked(String str, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i2, String str2, int i3) {
        StringBuilder sb;
        ?? r8;
        String str3;
        int i4;
        AttributeCache instance;
        if (i2 == -1 && str == null) {
            Slog.w("ActivityManager", "Can't force stop all processes of all users, that is insane!");
        }
        int appId = (i >= 0 || str == null) ? i : UserHandle.getAppId(getPackageManagerInternal().getPackageUid(str, 272629760L, 0));
        if (z3) {
            if (str != null) {
                Slog.i("ActivityManager", "Force stopping " + str + " appid=" + appId + " user=" + i2 + ": " + str2);
            } else {
                Slog.i("ActivityManager", "Force stopping u" + i2 + ": " + str2);
            }
            this.mAppErrors.resetProcessCrashTime(str == null, appId, i2);
        }
        synchronized (this.mProcLock) {
            try {
                try {
                    boostPriorityForProcLockedSection();
                    boolean onForceStopPackage = this.mAtmInternal.onForceStopPackage(str, z3, z4, i2);
                    int i5 = i3 == 10 ? 21 : 0;
                    ProcessList processList = this.mProcessList;
                    StringBuilder sb2 = new StringBuilder();
                    if (str == null) {
                        sb = new StringBuilder();
                        sb.append("stop user ");
                        sb.append(i2);
                    } else {
                        sb = new StringBuilder();
                        sb.append("stop ");
                        sb.append(str);
                    }
                    sb2.append(sb.toString());
                    sb2.append(" due to ");
                    sb2.append(str2);
                    int i6 = appId;
                    boolean killPackageProcessesLSP = onForceStopPackage | processList.killPackageProcessesLSP(str, appId, i2, -10000, z, false, z3, z4, true, z5, i3, i5, sb2.toString());
                    resetPriorityAfterProcLockedSection();
                    if (!this.mServices.bringDownDisabledPackageServicesLocked(str, null, i2, z4, true, z3)) {
                        r8 = 1;
                        str3 = str;
                    } else if (!z3) {
                        return true;
                    } else {
                        r8 = 1;
                        str3 = str;
                        killPackageProcessesLSP = true;
                    }
                    if (str3 == null) {
                        i4 = i2;
                        this.mStickyBroadcasts.remove(i4);
                    } else {
                        i4 = i2;
                    }
                    ArrayList<ContentProviderRecord> arrayList = new ArrayList<>();
                    if (this.mCpHelper.getProviderMap().collectPackageProvidersLocked(str, (Set<String>) null, z3, z4, i2, arrayList)) {
                        if (!z3) {
                            return r8;
                        }
                        killPackageProcessesLSP = r8;
                    }
                    for (int size = arrayList.size() - r8; size >= 0; size--) {
                        this.mCpHelper.removeDyingProviderLocked(null, arrayList.get(size), r8);
                    }
                    this.mUgmInternal.removeUriPermissionsForPackage(str3, i4, false, false);
                    if (z3) {
                        for (int length = this.mBroadcastQueues.length - r8; length >= 0; length--) {
                            killPackageProcessesLSP |= this.mBroadcastQueues[length].cleanupDisabledPackageReceiversLocked(str3, null, i4);
                        }
                    }
                    if (str3 == null || z5) {
                        killPackageProcessesLSP |= this.mPendingIntentController.removePendingIntentsForPackage(str3, i4, i6, z3);
                    }
                    if (z3) {
                        if (z2 && str3 != null && (instance = AttributeCache.instance()) != null) {
                            instance.removePackage(str3);
                        }
                        if (this.mBooted) {
                            this.mAtmInternal.resumeTopActivities(r8);
                        }
                    }
                    return killPackageProcessesLSP;
                } catch (Throwable th) {
                    th = th;
                    resetPriorityAfterProcLockedSection();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    @GuardedBy({"this"})
    public void handleProcessStartOrKillTimeoutLocked(ProcessRecord processRecord, boolean z) {
        int pid = processRecord.getPid();
        if (z || removePidLocked(pid, processRecord)) {
            if (z) {
                ProcessRecord processRecord2 = processRecord.mSuccessor;
                if (processRecord2 == null) {
                    return;
                }
                Slog.wtf("ActivityManager", processRecord.toString() + " " + processRecord.getDyingPid() + " refused to die while trying to launch " + processRecord2 + ", cancelling the process start");
                processRecord.mSuccessorStartRunnable = null;
                processRecord.mSuccessor = null;
                processRecord2.mPredecessor = null;
                processRecord = processRecord2;
            } else {
                String str = "Process " + processRecord + " failed to attach";
                Slog.w("ActivityManager", str);
                EventLogTags.writeAmProcessStartTimeout(processRecord.userId, pid, processRecord.uid, processRecord.processName);
                if (processRecord.getActiveInstrumentation() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("shortMsg", "failed to attach");
                    bundle.putString("longMsg", str);
                    finishInstrumentationLocked(processRecord, 0, bundle);
                }
            }
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.removeProcessNameLocked(processRecord.processName, processRecord.uid);
                    this.mAtmInternal.clearHeavyWeightProcessIfEquals(processRecord.getWindowProcessController());
                    this.mCpHelper.cleanupAppInLaunchingProvidersLocked(processRecord, true);
                    this.mServices.processStartTimedOutLocked(processRecord);
                    for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
                        broadcastQueue.onApplicationTimeoutLocked(processRecord);
                    }
                    if (!z) {
                        this.mBatteryStatsService.noteProcessFinish(processRecord.processName, processRecord.info.uid);
                        processRecord.killLocked("start timeout", 7, true);
                        removeLruProcessLocked(processRecord);
                    }
                    if (processRecord.isolated) {
                        this.mBatteryStatsService.removeIsolatedUid(processRecord.uid, processRecord.info.uid);
                        this.mProcessList.mAppExitInfoTracker.mIsolatedUidRecords.removeIsolatedUid(processRecord.uid, processRecord.info.uid);
                        getPackageManagerInternal().removeIsolatedUid(processRecord.uid);
                    }
                } catch (Throwable th) {
                    resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterProcLockedSection();
            BackupRecord backupRecord = this.mBackupTargets.get(processRecord.userId);
            if (z || backupRecord == null || backupRecord.app.getPid() != pid) {
                return;
            }
            Slog.w("ActivityManager", "Unattached app died before backup, skipping");
            final int i = processRecord.userId;
            final String str2 = processRecord.info.packageName;
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService.7
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IBackupManager.Stub.asInterface(ServiceManager.getService("backup")).agentDisconnectedForUser(i, str2);
                    } catch (RemoteException unused) {
                    }
                }
            });
            return;
        }
        Slog.w("ActivityManager", "Spurious process start timeout - pid not known for " + processRecord);
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0314 A[Catch: Exception -> 0x0483, TryCatch #9 {Exception -> 0x0483, blocks: (B:128:0x02c8, B:130:0x02d2, B:132:0x02e5, B:134:0x02f1, B:136:0x02fb, B:138:0x030a, B:140:0x0314, B:142:0x031d, B:144:0x0337, B:145:0x033c, B:147:0x0346, B:149:0x035b, B:155:0x0377), top: B:219:0x02c8 }] */
    /* JADX WARN: Removed duplicated region for block: B:141:0x031b  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0337 A[Catch: Exception -> 0x0483, TryCatch #9 {Exception -> 0x0483, blocks: (B:128:0x02c8, B:130:0x02d2, B:132:0x02e5, B:134:0x02f1, B:136:0x02fb, B:138:0x030a, B:140:0x0314, B:142:0x031d, B:144:0x0337, B:145:0x033c, B:147:0x0346, B:149:0x035b, B:155:0x0377), top: B:219:0x02c8 }] */
    /* JADX WARN: Removed duplicated region for block: B:147:0x0346 A[Catch: Exception -> 0x0483, TryCatch #9 {Exception -> 0x0483, blocks: (B:128:0x02c8, B:130:0x02d2, B:132:0x02e5, B:134:0x02f1, B:136:0x02fb, B:138:0x030a, B:140:0x0314, B:142:0x031d, B:144:0x0337, B:145:0x033c, B:147:0x0346, B:149:0x035b, B:155:0x0377), top: B:219:0x02c8 }] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0359  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x041f A[Catch: Exception -> 0x048e, TryCatch #11 {Exception -> 0x048e, blocks: (B:170:0x041f, B:171:0x0422, B:172:0x0429, B:175:0x043c, B:176:0x0451, B:179:0x045d, B:181:0x046d, B:182:0x0475, B:162:0x03cc, B:168:0x03e4, B:89:0x0227, B:91:0x022e, B:93:0x0234, B:95:0x023c, B:177:0x0452, B:178:0x045c, B:173:0x042a, B:174:0x043b), top: B:224:0x0227 }] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x042a A[Catch: all -> 0x047d, TRY_ENTER, TryCatch #11 {Exception -> 0x048e, blocks: (B:170:0x041f, B:171:0x0422, B:172:0x0429, B:175:0x043c, B:176:0x0451, B:179:0x045d, B:181:0x046d, B:182:0x0475, B:162:0x03cc, B:168:0x03e4, B:89:0x0227, B:91:0x022e, B:93:0x0234, B:95:0x023c, B:177:0x0452, B:178:0x045c, B:173:0x042a, B:174:0x043b), top: B:224:0x0227 }] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00f5  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0127  */
    @GuardedBy({"this"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void attachApplicationLocked(IApplicationThread iApplicationThread, int i, int i2, long j) {
        long j2;
        int i3;
        ProcessRecord processRecord;
        int i4;
        boolean z;
        AutofillOptions autofillOptions;
        PlatformCompat platformCompat;
        long j3;
        long j4;
        int i5;
        boolean z2;
        boolean z3;
        long j5;
        ContentCaptureManagerInternal contentCaptureManagerInternal;
        int i6;
        boolean z4;
        int i7;
        ProcessRecord processRecord2;
        ProcessRecord processRecord3;
        long uptimeMillis = SystemClock.uptimeMillis();
        int i8 = MY_PID;
        if (i == i8 || i < 0) {
            j2 = uptimeMillis;
            i3 = i8;
        } else {
            synchronized (this.mPidsSelfLocked) {
                processRecord3 = this.mPidsSelfLocked.get(i);
            }
            if (processRecord3 != null && (processRecord3.getStartUid() != i2 || processRecord3.getStartSeq() != j)) {
                ProcessRecord processRecord4 = this.mProcessList.mPendingStarts.get(j);
                String str = "attachApplicationLocked process:" + (processRecord4 != null ? processRecord4.processName : null) + " startSeq:" + j + " pid:" + i + " belongs to another existing app:" + processRecord3.processName + " startSeq:" + processRecord3.getStartSeq();
                Slog.wtf("ActivityManager", str);
                EventLog.writeEvent(1397638484, "131105245", Integer.valueOf(processRecord3.getStartUid()), str);
                j2 = uptimeMillis;
                i3 = i8;
                cleanUpApplicationRecordLocked(processRecord3, i, false, false, -1, true, false);
                removePidLocked(i, processRecord3);
            } else {
                j2 = uptimeMillis;
                i3 = i8;
                processRecord = processRecord3;
                if (processRecord == null && j > 0 && (processRecord2 = this.mProcessList.mPendingStarts.get(j)) != null && processRecord2.getStartUid() == i2 && processRecord2.getStartSeq() == j && this.mProcessList.handleProcessStartedLocked(processRecord2, i, processRecord2.isUsingWrapper(), j, true)) {
                    processRecord = processRecord2;
                }
                if (processRecord != null) {
                    Slog.w("ActivityManager", "No pending application record for pid " + i + " (IApplicationThread " + iApplicationThread + "); dropping process");
                    EventLogTags.writeAmDropProcess(i);
                    if (i > 0 && i != i3) {
                        Process.killProcessQuiet(i);
                        return;
                    }
                    try {
                        iApplicationThread.scheduleExit();
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
                if (processRecord.getThread() != null) {
                    handleAppDiedLocked(processRecord, i, true, true, false);
                }
                String str2 = processRecord.processName;
                try {
                    AppDeathRecipient appDeathRecipient = new AppDeathRecipient(processRecord, i, iApplicationThread);
                    iApplicationThread.asBinder().linkToDeath(appDeathRecipient, 0);
                    processRecord.setDeathRecipient(appDeathRecipient);
                    EventLogTags.writeAmProcBound(processRecord.userId, i, processRecord.processName);
                    synchronized (this.mProcLock) {
                        try {
                            boostPriorityForProcLockedSection();
                            processRecord.mState.setCurAdj(-10000);
                            processRecord.mState.setSetAdj(-10000);
                            processRecord.mState.setVerifiedAdj(-10000);
                            this.mOomAdjuster.setAttachingSchedGroupLSP(processRecord);
                            processRecord.mState.setForcingToImportant(null);
                            clearProcessForegroundLocked(processRecord);
                            processRecord.mState.setHasShownUi(false);
                            processRecord.mState.setCached(false);
                            processRecord.setDebugging(false);
                            processRecord.setKilledByAm(false);
                            processRecord.setKilled(false);
                            processRecord.setUnlocked(StorageManager.isUserKeyUnlocked(processRecord.userId));
                        } catch (Throwable th) {
                            resetPriorityAfterProcLockedSection();
                            throw th;
                        }
                    }
                    resetPriorityAfterProcLockedSection();
                    boolean z5 = this.mProcessesReady || isAllowedWhileBooting(processRecord.info);
                    List<ProviderInfo> generateApplicationProvidersLocked = z5 ? this.mCpHelper.generateApplicationProvidersLocked(processRecord) : null;
                    if (generateApplicationProvidersLocked != null && this.mCpHelper.checkAppInLaunchingProvidersLocked(processRecord)) {
                        Message obtainMessage = this.mHandler.obtainMessage(57);
                        obtainMessage.obj = processRecord;
                        this.mHandler.sendMessageDelayed(obtainMessage, ContentResolver.CONTENT_PROVIDER_PUBLISH_TIMEOUT_MILLIS);
                    }
                    long j6 = j2;
                    checkTime(j6, "attachApplicationLocked: before bindApplication");
                    if (!z5) {
                        Slog.i("ActivityManager", "Launching preboot mode app: " + processRecord);
                    }
                    BackupRecord backupRecord = this.mBackupTargets.get(processRecord.userId);
                    try {
                        String str3 = this.mDebugApp;
                        if (str3 == null || !str3.equals(str2)) {
                            i4 = 0;
                        } else {
                            if (!this.mWaitForDebugger) {
                                z4 = true;
                                i7 = 1;
                            } else if (this.mSuspendUponWait) {
                                z4 = true;
                                i7 = 3;
                            } else {
                                i7 = 2;
                                z4 = true;
                            }
                            processRecord.setDebugging(z4);
                            if (this.mDebugTransient) {
                                this.mDebugApp = this.mOrigDebugApp;
                                this.mWaitForDebugger = this.mOrigWaitForDebugger;
                            }
                            i4 = i7;
                        }
                        synchronized (this.mProcLock) {
                            try {
                                boostPriorityForProcLockedSection();
                                String str4 = this.mTrackAllocationApp;
                                if (str4 == null || !str4.equals(str2)) {
                                    z = false;
                                } else {
                                    this.mTrackAllocationApp = null;
                                    z = true;
                                }
                            } catch (Exception e) {
                                e = e;
                            }
                        }
                        resetPriorityAfterProcLockedSection();
                        boolean z6 = backupRecord != null && backupRecord.appInfo.packageName.equals(str2) && backupRecord.appInfo.uid >= 10000 && ((i6 = backupRecord.backupMode) == 2 || i6 == 3 || i6 == 1);
                        ActiveInstrumentation activeInstrumentation = processRecord.getActiveInstrumentation();
                        if (activeInstrumentation != null) {
                            notifyPackageUse(activeInstrumentation.mClass.getPackageName(), 7);
                        }
                        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -593535526, 0, (String) null, new Object[]{String.valueOf(str2), String.valueOf(processRecord.getWindowProcessController().getConfiguration())});
                        }
                        ApplicationInfo applicationInfo = activeInstrumentation != null ? activeInstrumentation.mTargetInfo : processRecord.info;
                        processRecord.setCompat(compatibilityInfoForPackage(applicationInfo));
                        ProfilerInfo profilerInfo = this.mAppProfiler.setupProfilerInfoLocked(iApplicationThread, processRecord, activeInstrumentation);
                        String str5 = (applicationInfo.isInstantApp() || applicationInfo.targetSdkVersion >= 28) ? "unknown" : sTheRealBuildSerial;
                        if (UserHandle.getAppId(processRecord.info.uid) >= 10000) {
                            try {
                                AutofillManagerInternal autofillManagerInternal = (AutofillManagerInternal) LocalServices.getService(AutofillManagerInternal.class);
                                if (autofillManagerInternal != null) {
                                    autofillOptions = autofillManagerInternal.getAutofillOptions(processRecord.info.packageName, processRecord.info.longVersionCode, processRecord.userId);
                                    ContentCaptureOptions optionsForPackage = (UserHandle.getAppId(processRecord.info.uid) >= 10000 || (contentCaptureManagerInternal = (ContentCaptureManagerInternal) LocalServices.getService(ContentCaptureManagerInternal.class)) == null) ? null : contentCaptureManagerInternal.getOptionsForPackage(processRecord.userId, processRecord.info.packageName);
                                    FontManagerInternal fontManagerInternal = (FontManagerInternal) LocalServices.getService(FontManagerInternal.class);
                                    SharedMemory serializedSystemFontMap = fontManagerInternal == null ? fontManagerInternal.getSerializedSystemFontMap() : null;
                                    checkTime(j6, "attachApplicationLocked: immediately before bindApplication");
                                    long uptimeMillis2 = SystemClock.uptimeMillis();
                                    this.mAtmInternal.preBindApplication(processRecord.getWindowProcessController());
                                    ActiveInstrumentation activeInstrumentation2 = processRecord.getActiveInstrumentation();
                                    platformCompat = this.mPlatformCompat;
                                    if (platformCompat != null) {
                                        platformCompat.resetReporting(processRecord.info);
                                    }
                                    ProviderInfoList fromList = ProviderInfoList.fromList(generateApplicationProvidersLocked);
                                    if (processRecord.getIsolatedEntryPoint() == null) {
                                        iApplicationThread.runIsolatedEntryPoint(processRecord.getIsolatedEntryPoint(), processRecord.getIsolatedEntryPointArgs());
                                        j3 = j6;
                                        j4 = uptimeMillis2;
                                        i5 = i;
                                    } else if (activeInstrumentation2 != null) {
                                        String str6 = processRecord.sdkSandboxClientAppVolumeUuid;
                                        String str7 = processRecord.sdkSandboxClientAppPackage;
                                        ComponentName componentName = activeInstrumentation2.mClass;
                                        Bundle bundle = activeInstrumentation2.mArguments;
                                        IInstrumentationWatcher iInstrumentationWatcher = activeInstrumentation2.mWatcher;
                                        IUiAutomationConnection iUiAutomationConnection = activeInstrumentation2.mUiAutomationConnection;
                                        boolean z7 = this.mBinderTransactionTrackingEnabled;
                                        try {
                                            if (!z6 && z5) {
                                                z3 = false;
                                                j3 = j6;
                                                j4 = uptimeMillis2;
                                                iApplicationThread.bindApplication(str2, applicationInfo, str6, str7, fromList, componentName, profilerInfo, bundle, iInstrumentationWatcher, iUiAutomationConnection, i4, z7, z, z3, processRecord.isPersistent(), new Configuration(processRecord.getWindowProcessController().getConfiguration()), processRecord.getCompat(), getCommonServicesLocked(processRecord.isolated), this.mCoreSettingsObserver.getCoreSettingsLocked(), str5, autofillOptions, optionsForPackage, processRecord.getDisabledCompatChanges(), serializedSystemFontMap, processRecord.getStartElapsedTime(), processRecord.getStartUptime());
                                                i5 = i;
                                            }
                                            iApplicationThread.bindApplication(str2, applicationInfo, str6, str7, fromList, componentName, profilerInfo, bundle, iInstrumentationWatcher, iUiAutomationConnection, i4, z7, z, z3, processRecord.isPersistent(), new Configuration(processRecord.getWindowProcessController().getConfiguration()), processRecord.getCompat(), getCommonServicesLocked(processRecord.isolated), this.mCoreSettingsObserver.getCoreSettingsLocked(), str5, autofillOptions, optionsForPackage, processRecord.getDisabledCompatChanges(), serializedSystemFontMap, processRecord.getStartElapsedTime(), processRecord.getStartUptime());
                                            i5 = i;
                                        } catch (Exception e2) {
                                            e = e2;
                                            Slog.wtf("ActivityManager", "Exception thrown during bind of " + processRecord, e);
                                            processRecord.resetPackageList(this.mProcessStats);
                                            processRecord.unlinkDeathRecipient();
                                            processRecord.killLocked("error during bind", 7, true);
                                            handleAppDiedLocked(processRecord, i, false, true, false);
                                            return;
                                        }
                                        z3 = true;
                                        j3 = j6;
                                        j4 = uptimeMillis2;
                                    } else {
                                        j3 = j6;
                                        j4 = uptimeMillis2;
                                        i5 = i;
                                        String str8 = processRecord.sdkSandboxClientAppVolumeUuid;
                                        String str9 = processRecord.sdkSandboxClientAppPackage;
                                        boolean z8 = this.mBinderTransactionTrackingEnabled;
                                        if (!z6 && z5) {
                                            z2 = false;
                                            iApplicationThread.bindApplication(str2, applicationInfo, str8, str9, fromList, (ComponentName) null, profilerInfo, (Bundle) null, (IInstrumentationWatcher) null, (IUiAutomationConnection) null, i4, z8, z, z2, processRecord.isPersistent(), new Configuration(processRecord.getWindowProcessController().getConfiguration()), processRecord.getCompat(), getCommonServicesLocked(processRecord.isolated), this.mCoreSettingsObserver.getCoreSettingsLocked(), str5, autofillOptions, optionsForPackage, processRecord.getDisabledCompatChanges(), serializedSystemFontMap, processRecord.getStartElapsedTime(), processRecord.getStartUptime());
                                        }
                                        z2 = true;
                                        iApplicationThread.bindApplication(str2, applicationInfo, str8, str9, fromList, (ComponentName) null, profilerInfo, (Bundle) null, (IInstrumentationWatcher) null, (IUiAutomationConnection) null, i4, z8, z, z2, processRecord.isPersistent(), new Configuration(processRecord.getWindowProcessController().getConfiguration()), processRecord.getCompat(), getCommonServicesLocked(processRecord.isolated), this.mCoreSettingsObserver.getCoreSettingsLocked(), str5, autofillOptions, optionsForPackage, processRecord.getDisabledCompatChanges(), serializedSystemFontMap, processRecord.getStartElapsedTime(), processRecord.getStartUptime());
                                    }
                                    if (profilerInfo != null) {
                                        profilerInfo.closeFd();
                                    }
                                    processRecord.setBindApplicationTime(j4);
                                    synchronized (this.mProcLock) {
                                        boostPriorityForProcLockedSection();
                                        processRecord.makeActive(iApplicationThread, this.mProcessStats);
                                        j5 = j3;
                                        checkTime(j5, "attachApplicationLocked: immediately after bindApplication");
                                    }
                                    resetPriorityAfterProcLockedSection();
                                    updateLruProcessLocked(processRecord, false, null);
                                    checkTime(j5, "attachApplicationLocked: after updateLruProcessLocked");
                                    long uptimeMillis3 = SystemClock.uptimeMillis();
                                    synchronized (this.mAppProfiler.mProfilerLock) {
                                        processRecord.mProfile.setLastRequestedGc(uptimeMillis3);
                                        processRecord.mProfile.setLastLowMemory(uptimeMillis3);
                                    }
                                    this.mPersistentStartingProcesses.remove(processRecord);
                                    this.mProcessesOnHold.remove(processRecord);
                                    if (!this.mConstants.mEnableWaitForFinishAttachApplication) {
                                        finishAttachApplicationInner(j, i2, i5);
                                        return;
                                    } else {
                                        processRecord.setPendingFinishAttach(true);
                                        return;
                                    }
                                }
                            } catch (Exception e3) {
                                e = e3;
                                Slog.wtf("ActivityManager", "Exception thrown during bind of " + processRecord, e);
                                processRecord.resetPackageList(this.mProcessStats);
                                processRecord.unlinkDeathRecipient();
                                processRecord.killLocked("error during bind", 7, true);
                                handleAppDiedLocked(processRecord, i, false, true, false);
                                return;
                            }
                        }
                        autofillOptions = null;
                        if (UserHandle.getAppId(processRecord.info.uid) >= 10000) {
                        }
                        FontManagerInternal fontManagerInternal2 = (FontManagerInternal) LocalServices.getService(FontManagerInternal.class);
                        if (fontManagerInternal2 == null) {
                        }
                        checkTime(j6, "attachApplicationLocked: immediately before bindApplication");
                        long uptimeMillis22 = SystemClock.uptimeMillis();
                        this.mAtmInternal.preBindApplication(processRecord.getWindowProcessController());
                        ActiveInstrumentation activeInstrumentation22 = processRecord.getActiveInstrumentation();
                        platformCompat = this.mPlatformCompat;
                        if (platformCompat != null) {
                        }
                        ProviderInfoList fromList2 = ProviderInfoList.fromList(generateApplicationProvidersLocked);
                        if (processRecord.getIsolatedEntryPoint() == null) {
                        }
                        if (profilerInfo != null) {
                        }
                        processRecord.setBindApplicationTime(j4);
                        synchronized (this.mProcLock) {
                        }
                    } catch (Exception e4) {
                        e = e4;
                    }
                } catch (RemoteException unused2) {
                    processRecord.resetPackageList(this.mProcessStats);
                    this.mProcessList.startProcessLocked(processRecord, new HostingRecord("link fail", str2), 0);
                    return;
                }
            }
        }
        processRecord = null;
        if (processRecord == null) {
            processRecord = processRecord2;
        }
        if (processRecord != null) {
        }
    }

    public final void attachApplication(IApplicationThread iApplicationThread, long j) {
        if (iApplicationThread == null) {
            throw new SecurityException("Invalid application interface");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                int callingPid = Binder.getCallingPid();
                int callingUid = Binder.getCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                attachApplicationLocked(iApplicationThread, callingPid, callingUid, j);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x006f  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x010b A[Catch: all -> 0x0182, TryCatch #2 {all -> 0x0182, blocks: (B:14:0x002d, B:16:0x0038, B:21:0x0044, B:23:0x0048, B:30:0x0073, B:36:0x009a, B:38:0x009f, B:39:0x00a9, B:43:0x00c7, B:46:0x00d5, B:48:0x00d9, B:49:0x00e1, B:52:0x00f2, B:54:0x010b, B:55:0x011c, B:60:0x0125, B:61:0x012d, B:62:0x017d, B:42:0x00b0, B:33:0x0081, B:26:0x0056), top: B:86:0x002d, inners: #0, #1, #5, #6 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0073 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x009a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0048 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void finishAttachApplicationInner(long j, int i, int i2) {
        ProcessRecord processRecord;
        boolean z;
        boolean z2;
        boolean z3;
        BackupRecord backupRecord;
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this.mPidsSelfLocked) {
            processRecord = this.mPidsSelfLocked.get(i2);
        }
        if (processRecord != null && processRecord.getStartUid() == i && processRecord.getStartSeq() == j) {
            this.mHandler.removeMessages(20, processRecord);
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    processRecord.setPendingFinishAttach(false);
                    if (!this.mProcessesReady && !isAllowedWhileBooting(processRecord.info)) {
                        z = false;
                        String str = processRecord.processName;
                        if (z) {
                            z3 = false;
                            z2 = false;
                        } else {
                            try {
                                z2 = this.mAtmInternal.attachApplication(processRecord.getWindowProcessController());
                                z3 = false;
                            } catch (Exception e) {
                                Slog.wtf("ActivityManager", "Exception thrown launching activities in " + processRecord, e);
                                z2 = false;
                                z3 = true;
                            }
                        }
                        if (!z3) {
                            try {
                                z2 |= this.mServices.attachApplicationLocked(processRecord, str);
                                checkTime(uptimeMillis, "finishAttachApplicationInner: after mServices.attachApplicationLocked");
                            } catch (Exception e2) {
                                Slog.wtf("ActivityManager", "Exception thrown starting services in " + processRecord, e2);
                                z3 = true;
                            }
                        }
                        if (!z3) {
                            try {
                                for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
                                    z2 |= broadcastQueue.onApplicationAttachedLocked(processRecord);
                                }
                                checkTime(uptimeMillis, "finishAttachApplicationInner: after dispatching broadcasts");
                            } catch (Exception e3) {
                                Slog.wtf("ActivityManager", "Exception thrown dispatching broadcasts in " + processRecord, e3);
                                z3 = true;
                            }
                        }
                        backupRecord = this.mBackupTargets.get(processRecord.userId);
                        if (!z3 && backupRecord != null && backupRecord.app == processRecord) {
                            notifyPackageUse(backupRecord.appInfo.packageName, 5);
                            try {
                                processRecord.getThread().scheduleCreateBackupAgent(backupRecord.appInfo, backupRecord.backupMode, backupRecord.userId, backupRecord.backupDestination);
                            } catch (Exception e4) {
                                Slog.wtf("ActivityManager", "Exception thrown creating backup agent in " + processRecord, e4);
                                z3 = true;
                            }
                        }
                        if (!z3) {
                            processRecord.killLocked("error during init", 7, true);
                            handleAppDiedLocked(processRecord, i2, false, true, false);
                            return;
                        }
                        if (!z2) {
                            updateOomAdjLocked(processRecord, 11);
                            checkTime(uptimeMillis, "finishAttachApplicationInner: after updateOomAdjLocked");
                        }
                        HostingRecord hostingRecord = processRecord.getHostingRecord();
                        FrameworkStatsLog.write(169, processRecord.info.uid, i2, processRecord.info.packageName, 3, processRecord.getStartElapsedTime(), (int) (processRecord.getBindApplicationTime() - processRecord.getStartUptime()), (int) (SystemClock.uptimeMillis() - processRecord.getStartUptime()), hostingRecord.getType(), hostingRecord.getName(), getShortAction(hostingRecord.getAction()), HostingRecord.getHostingTypeIdStatsd(hostingRecord.getType()), HostingRecord.getTriggerTypeForStatsd(hostingRecord.getTriggerType()));
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    z = true;
                    String str2 = processRecord.processName;
                    if (z) {
                    }
                    if (!z3) {
                    }
                    if (!z3) {
                    }
                    backupRecord = this.mBackupTargets.get(processRecord.userId);
                    if (!z3) {
                        notifyPackageUse(backupRecord.appInfo.packageName, 5);
                        processRecord.getThread().scheduleCreateBackupAgent(backupRecord.appInfo, backupRecord.backupMode, backupRecord.userId, backupRecord.backupDestination);
                    }
                    if (!z3) {
                    }
                } finally {
                    resetPriorityAfterLockedSection();
                }
            }
        } else {
            Slog.wtf("ActivityManager", "Mismatched or missing ProcessRecord: " + processRecord + ". Pid: " + i2 + ". Uid: " + i);
            Process.killProcess(i2);
            Process.killProcessGroup(i, i2);
            this.mProcessList.noteAppKill(i2, i, 7, 0, "wrong startSeq");
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    processRecord.killLocked("unexpected process record", 13, true);
                } finally {
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public final void finishAttachApplication(long j) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (!this.mConstants.mEnableWaitForFinishAttachApplication) {
            Slog.i("ActivityManager", "Flag disabled. Ignoring finishAttachApplication from uid: " + callingUid + ". pid: " + callingPid);
        } else if (callingPid == MY_PID && callingUid == 1000) {
        } else {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                finishAttachApplicationInner(j, callingUid, callingPid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public static String getShortAction(String str) {
        int lastIndexOf;
        return (str == null || (lastIndexOf = str.lastIndexOf(46)) == -1 || lastIndexOf == str.length() + (-1)) ? str : str.substring(lastIndexOf + 1);
    }

    public void checkTime(long j, String str) {
        long uptimeMillis = SystemClock.uptimeMillis() - j;
        if (uptimeMillis > 50) {
            Slog.w("ActivityManager", "Slow operation: " + uptimeMillis + "ms so far, now at " + str);
        }
    }

    public void showBootMessage(CharSequence charSequence, boolean z) {
        if (Binder.getCallingUid() != Process.myUid()) {
            throw new SecurityException();
        }
        this.mWindowManager.showBootMessage(charSequence, z);
    }

    public final void finishBooting() {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("ActivityManagerTiming", 64L);
        timingsTraceAndSlog.traceBegin("FinishBooting");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (!this.mBootAnimationComplete) {
                    this.mCallFinishBooting = true;
                    return;
                }
                this.mCallFinishBooting = false;
                resetPriorityAfterLockedSection();
                Process.ZYGOTE_PROCESS.bootCompleted();
                VMRuntime.bootCompleted();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
                intentFilter.addDataScheme("package");
                this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.am.ActivityManagerService.8
                    @Override // android.content.BroadcastReceiver
                    public void onReceive(Context context, Intent intent) {
                        String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
                        if (stringArrayExtra != null) {
                            for (String str : stringArrayExtra) {
                                synchronized (ActivityManagerService.this) {
                                    try {
                                        ActivityManagerService.boostPriorityForLockedSection();
                                        if (ActivityManagerService.this.forceStopPackageLocked(str, -1, false, false, false, false, false, 0, "query restart")) {
                                            setResultCode(-1);
                                            ActivityManagerService.resetPriorityAfterLockedSection();
                                            return;
                                        }
                                    } catch (Throwable th) {
                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                        throw th;
                                    }
                                }
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            }
                        }
                    }
                }, intentFilter);
                try {
                    Slog.i("ActivityManager", "About to commit checkpoint");
                    InstallLocationUtils.getStorageManager().commitChanges();
                } catch (Exception unused) {
                    ((PowerManager) this.mInjector.getContext().getSystemService("power")).reboot("Checkpoint commit failed");
                }
                this.mSystemServiceManager.startBootPhase(timingsTraceAndSlog, 1000);
                synchronized (this) {
                    try {
                        boostPriorityForLockedSection();
                        int size = this.mProcessesOnHold.size();
                        if (size > 0) {
                            ArrayList arrayList = new ArrayList(this.mProcessesOnHold);
                            for (int i = 0; i < size; i++) {
                                this.mProcessList.startProcessLocked((ProcessRecord) arrayList.get(i), new HostingRecord("on-hold"), 2);
                            }
                        }
                        if (this.mFactoryTest == 1) {
                            return;
                        }
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(27), this.mConstants.POWER_CHECK_INTERVAL);
                        if (((Boolean) InitProperties.userspace_reboot_in_progress().orElse(Boolean.FALSE)).booleanValue()) {
                            UserspaceRebootLogger.noteUserspaceRebootSuccess();
                        }
                        SystemProperties.set("sys.boot_completed", "1");
                        SystemProperties.set("dev.bootcomplete", "1");
                        this.mUserController.onBootComplete(new IIntentReceiver.Stub() { // from class: com.android.server.am.ActivityManagerService.9
                            public void performReceive(Intent intent, int i2, String str, Bundle bundle, boolean z, boolean z2, int i3) {
                                synchronized (ActivityManagerService.this.mProcLock) {
                                    try {
                                        ActivityManagerService.boostPriorityForProcLockedSection();
                                        ActivityManagerService.this.mAppProfiler.requestPssAllProcsLPr(SystemClock.uptimeMillis(), true, false);
                                    } catch (Throwable th) {
                                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                                        throw th;
                                    }
                                }
                                ActivityManagerService.resetPriorityAfterProcLockedSection();
                            }
                        });
                        maybeLogUserspaceRebootEvent();
                        this.mUserController.scheduleStartProfiles();
                        resetPriorityAfterLockedSection();
                        showConsoleNotificationIfActive();
                        showMteOverrideNotificationIfActive();
                        timingsTraceAndSlog.traceEnd();
                    } finally {
                        resetPriorityAfterLockedSection();
                    }
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    public final void showConsoleNotificationIfActive() {
        if (SystemProperties.get("init.svc.console").equals(INetd.IF_FLAG_RUNNING)) {
            String string = this.mContext.getString(17040047);
            ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notifyAsUser(null, 55, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVELOPER).setSmallIcon(17303596).setWhen(0L).setOngoing(true).setTicker(string).setDefaults(0).setColor(this.mContext.getColor(17170460)).setContentTitle(string).setContentText(this.mContext.getString(17040046)).setVisibility(1).build(), UserHandle.ALL);
        }
    }

    public final void showMteOverrideNotificationIfActive() {
        if (!Arrays.asList(SystemProperties.get("arm64.memtag.bootctl").split(",")).contains("memtag") && SystemProperties.getBoolean("ro.arm64.memtag.bootctl_supported", false) && Zygote.nativeSupportsMemoryTagging()) {
            String string = this.mContext.getString(17040818);
            ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notifyAsUser(null, 69, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVELOPER).setSmallIcon(17303596).setOngoing(true).setTicker(string).setDefaults(0).setColor(this.mContext.getColor(17170460)).setContentTitle(string).setContentText(this.mContext.getString(17040817)).setVisibility(1).build(), UserHandle.ALL);
        }
    }

    public void bootAnimationComplete() {
        boolean z;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                z = this.mCallFinishBooting;
                this.mBootAnimationComplete = true;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        if (z) {
            finishBooting();
        }
    }

    public final void ensureBootCompleted() {
        boolean z;
        boolean z2;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                z = this.mBooting;
                this.mBooting = false;
                z2 = this.mBooted ? false : true;
                this.mBooted = true;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        if (z) {
            finishBooting();
        }
        if (z2) {
            this.mAtmInternal.enableScreenAfterBoot(this.mBooted);
        }
    }

    @Deprecated
    public IIntentSender getIntentSender(int i, String str, IBinder iBinder, String str2, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) {
        return getIntentSenderWithFeature(i, str, null, iBinder, str2, i2, intentArr, strArr, i3, bundle, i4);
    }

    public IIntentSender getIntentSenderWithFeature(int i, String str, String str2, IBinder iBinder, String str3, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) {
        enforceNotIsolatedCaller("getIntentSender");
        return getIntentSenderWithFeatureAsApp(i, str, str2, iBinder, str3, i2, intentArr, strArr, i3, bundle, i4, Binder.getCallingUid());
    }

    public IIntentSender getIntentSenderWithFeatureAsApp(int i, String str, String str2, IBinder iBinder, String str3, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4, int i5) {
        if (intentArr != null) {
            if (intentArr.length < 1) {
                throw new IllegalArgumentException("Intents array length must be >= 1");
            }
            int i6 = 0;
            while (i6 < intentArr.length) {
                Intent intent = intentArr[i6];
                if (intent != null) {
                    if (intent.hasFileDescriptors()) {
                        throw new IllegalArgumentException("File descriptors passed in Intent");
                    }
                    if (i == 1 && (intent.getFlags() & 33554432) != 0) {
                        throw new IllegalArgumentException("Can't use FLAG_RECEIVER_BOOT_UPGRADE here");
                    }
                    if (PendingIntent.isNewMutableDisallowedImplicitPendingIntent(i3, intent)) {
                        boolean isChangeEnabled = CompatChanges.isChangeEnabled(236704164L, i5);
                        ActivityManagerUtils.logUnsafeIntentEvent(4, i5, intent, (strArr == null || i6 >= strArr.length) ? null : strArr[i6], isChangeEnabled);
                        if (isChangeEnabled) {
                            throw new IllegalArgumentException(str + ": Targeting U+ (version " + FrameworkStatsLog.WIFI_BYTES_TRANSFER + " and above) disallows creating or retrieving a PendingIntent with FLAG_MUTABLE, an implicit Intent within and without FLAG_NO_CREATE and FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT for security reasons. To retrieve an already existing PendingIntent, use FLAG_NO_CREATE, however, to create a new PendingIntent with an implicit Intent use FLAG_IMMUTABLE.");
                        }
                    }
                    intentArr[i6] = new Intent(intent);
                }
                i6++;
            }
            if (strArr != null && strArr.length != intentArr.length) {
                throw new IllegalArgumentException("Intent array length does not match resolvedTypes length");
            }
        }
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in options");
        }
        int handleIncomingUser = i4 != -2 ? this.mUserController.handleIncomingUser(Binder.getCallingPid(), i5, i4, i == 1, 0, "getIntentSender", null) : -2;
        if (i5 != 0 && i5 != 1000) {
            try {
                int packageUid = AppGlobals.getPackageManager().getPackageUid(str, 268435456L, UserHandle.getUserId(i5));
                if (!UserHandle.isSameApp(i5, packageUid)) {
                    String str4 = "Permission Denial: getIntentSender() from pid=" + Binder.getCallingPid() + ", uid=" + i5 + ", (need uid=" + packageUid + ") is not allowed to send as package " + str;
                    Slog.w("ActivityManager", str4);
                    throw new SecurityException(str4);
                }
            } catch (RemoteException e) {
                throw new SecurityException(e);
            }
        }
        if (i == 3) {
            return this.mAtmInternal.getIntentSender(i, str, str2, i5, handleIncomingUser, iBinder, str3, i2, intentArr, strArr, i3, bundle);
        }
        return this.mPendingIntentController.getIntentSender(i, str, str2, i5, handleIncomingUser, iBinder, str3, i2, intentArr, strArr, i3, bundle);
    }

    public int sendIntentSender(IApplicationThread iApplicationThread, IIntentSender iIntentSender, IBinder iBinder, int i, Intent intent, String str, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
        Intent intent2;
        if (iIntentSender instanceof PendingIntentRecord) {
            PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
            PendingIntentRecord.Key key = pendingIntentRecord.key;
            UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            int callingUserId = UserHandle.getCallingUserId();
            if (UserManager.isVisibleBackgroundUsersEnabled() && key.userId == -2 && callingUserId != 0 && userManagerInternal.isUserVisible(callingUserId)) {
                EventLogTags.writeAmIntentSenderRedirectUser(callingUserId);
                return new PendingIntentRecord(pendingIntentRecord.controller, new PendingIntentRecord.Key(key.type, key.packageName, key.featureId, key.activity, key.who, key.requestCode, key.allIntents, key.allResolvedTypes, key.flags, key.options, callingUserId), pendingIntentRecord.uid).sendWithResult(iApplicationThread, i, intent, str, iBinder, iIntentReceiver, str2, bundle);
            }
            return pendingIntentRecord.sendWithResult(iApplicationThread, i, intent, str, iBinder, iIntentReceiver, str2, bundle);
        }
        if (intent == null) {
            Slog.wtf("ActivityManager", "Can't use null intent with direct IIntentSender call");
            intent2 = new Intent("android.intent.action.MAIN");
        } else {
            intent2 = intent;
        }
        try {
            iIntentSender.send(i, intent2, str, iBinder, (IIntentReceiver) null, str2, bundle);
        } catch (RemoteException unused) {
        }
        if (iIntentReceiver != null) {
            try {
                iIntentReceiver.performReceive(intent2, 0, (String) null, (Bundle) null, false, false, UserHandle.getCallingUserId());
                return 0;
            } catch (RemoteException unused2) {
                return 0;
            }
        }
        return 0;
    }

    public void cancelIntentSender(IIntentSender iIntentSender) {
        this.mPendingIntentController.cancelIntentSender(iIntentSender);
    }

    public boolean registerIntentSenderCancelListenerEx(IIntentSender iIntentSender, IResultReceiver iResultReceiver) {
        return this.mPendingIntentController.registerIntentSenderCancelListener(iIntentSender, iResultReceiver);
    }

    public void unregisterIntentSenderCancelListener(IIntentSender iIntentSender, IResultReceiver iResultReceiver) {
        this.mPendingIntentController.unregisterIntentSenderCancelListener(iIntentSender, iResultReceiver);
    }

    public ActivityManager.PendingIntentInfo getInfoForIntentSender(IIntentSender iIntentSender) {
        if (iIntentSender instanceof PendingIntentRecord) {
            PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
            String str = pendingIntentRecord.key.packageName;
            int i = pendingIntentRecord.uid;
            boolean filterAppAccess = getPackageManagerInternal().filterAppAccess(str, Binder.getCallingUid(), UserHandle.getUserId(i));
            String str2 = filterAppAccess ? null : str;
            int i2 = filterAppAccess ? -1 : i;
            PendingIntentRecord.Key key = pendingIntentRecord.key;
            return new ActivityManager.PendingIntentInfo(str2, i2, (key.flags & 67108864) != 0, key.type);
        }
        return new ActivityManager.PendingIntentInfo((String) null, -1, false, 0);
    }

    public boolean isIntentSenderTargetedToPackage(IIntentSender iIntentSender) {
        if (!(iIntentSender instanceof PendingIntentRecord)) {
            return false;
        }
        try {
            PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
            if (pendingIntentRecord.key.allIntents == null) {
                return false;
            }
            int i = 0;
            while (true) {
                Intent[] intentArr = pendingIntentRecord.key.allIntents;
                if (i >= intentArr.length) {
                    return true;
                }
                Intent intent = intentArr[i];
                if (intent.getPackage() != null && intent.getComponent() != null) {
                    return false;
                }
                i++;
            }
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public boolean isIntentSenderAnActivity(IIntentSender iIntentSender) {
        if (iIntentSender instanceof PendingIntentRecord) {
            return ((PendingIntentRecord) iIntentSender).key.type == 2;
        }
        return false;
    }

    public Intent getIntentForIntentSender(IIntentSender iIntentSender) {
        enforceCallingPermission("android.permission.GET_INTENT_SENDER_INTENT", "getIntentForIntentSender()");
        if (iIntentSender instanceof PendingIntentRecord) {
            try {
                PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
                if (pendingIntentRecord.key.requestIntent != null) {
                    return new Intent(pendingIntentRecord.key.requestIntent);
                }
                return null;
            } catch (ClassCastException unused) {
                return null;
            }
        }
        return null;
    }

    public ParceledListSlice<ResolveInfo> queryIntentComponentsForIntentSender(IIntentSender iIntentSender, int i) {
        enforceCallingPermission("android.permission.GET_INTENT_SENDER_INTENT", "queryIntentComponentsForIntentSender()");
        Objects.requireNonNull(iIntentSender);
        try {
            PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
            PendingIntentRecord.Key key = pendingIntentRecord.key;
            Intent intent = key.requestIntent;
            if (intent == null) {
                return null;
            }
            int i2 = key.userId;
            int i3 = pendingIntentRecord.uid;
            String str = key.requestResolvedType;
            int i4 = key.type;
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 == 4 || i4 == 5) {
                        return new ParceledListSlice<>(this.mPackageManagerInt.queryIntentServices(intent, i, i3, i2));
                    }
                    throw new IllegalStateException("Unsupported intent sender type: " + pendingIntentRecord.key.type);
                }
                return new ParceledListSlice<>(this.mPackageManagerInt.queryIntentActivities(intent, str, i, i3, i2));
            }
            return new ParceledListSlice<>(this.mPackageManagerInt.queryIntentReceivers(intent, str, i, i3, i2, false));
        } catch (ClassCastException unused) {
            return null;
        }
    }

    public String getTagForIntentSender(IIntentSender iIntentSender, String str) {
        String tagForIntentSenderLocked;
        if (iIntentSender instanceof PendingIntentRecord) {
            try {
                PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
                synchronized (this) {
                    boostPriorityForLockedSection();
                    tagForIntentSenderLocked = getTagForIntentSenderLocked(pendingIntentRecord, str);
                }
                resetPriorityAfterLockedSection();
                return tagForIntentSenderLocked;
            } catch (ClassCastException unused) {
                return null;
            }
        }
        return null;
    }

    public String getTagForIntentSenderLocked(PendingIntentRecord pendingIntentRecord, String str) {
        String str2;
        Intent intent = pendingIntentRecord.key.requestIntent;
        if (intent != null) {
            if (pendingIntentRecord.lastTag != null && (str2 = pendingIntentRecord.lastTagPrefix) == str && (str2 == null || str2.equals(str))) {
                return pendingIntentRecord.lastTag;
            }
            pendingIntentRecord.lastTagPrefix = str;
            StringBuilder sb = new StringBuilder(128);
            if (str != null) {
                sb.append(str);
            }
            if (intent.getAction() != null) {
                sb.append(intent.getAction());
            } else if (intent.getComponent() != null) {
                intent.getComponent().appendShortString(sb);
            } else {
                sb.append("?");
            }
            String sb2 = sb.toString();
            pendingIntentRecord.lastTag = sb2;
            return sb2;
        }
        return null;
    }

    public void setProcessLimit(int i) {
        enforceCallingPermission("android.permission.SET_PROCESS_LIMIT", "setProcessLimit()");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mConstants.setOverrideMaxCachedProcesses(i);
                trimApplicationsLocked(true, 12);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public int getProcessLimit() {
        int overrideMaxCachedProcesses;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                overrideMaxCachedProcesses = this.mConstants.getOverrideMaxCachedProcesses();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return overrideMaxCachedProcesses;
    }

    public void importanceTokenDied(ImportanceToken importanceToken) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    if (this.mImportantProcesses.get(importanceToken.pid) != importanceToken) {
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    this.mImportantProcesses.remove(importanceToken.pid);
                    ProcessRecord processRecord = this.mPidsSelfLocked.get(importanceToken.pid);
                    if (processRecord == null) {
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    processRecord.mState.setForcingToImportant(null);
                    clearProcessForegroundLocked(processRecord);
                    updateOomAdjLocked(processRecord, 9);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0072  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setProcessImportant(IBinder iBinder, int i, boolean z, String str) {
        boolean z2;
        enforceCallingPermission("android.permission.SET_PROCESS_LIMIT", "setProcessImportant()");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    ProcessRecord processRecord = this.mPidsSelfLocked.get(i);
                    if (processRecord == null && z) {
                        Slog.w("ActivityManager", "setProcessForeground called on unknown pid: " + i);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    ImportanceToken importanceToken = this.mImportantProcesses.get(i);
                    boolean z3 = true;
                    if (importanceToken != null) {
                        importanceToken.token.unlinkToDeath(importanceToken, 0);
                        this.mImportantProcesses.remove(i);
                        if (processRecord != null) {
                            processRecord.mState.setForcingToImportant(null);
                        }
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (z && iBinder != null) {
                        ImportanceToken importanceToken2 = new ImportanceToken(i, iBinder, str) { // from class: com.android.server.am.ActivityManagerService.10
                            @Override // android.os.IBinder.DeathRecipient
                            public void binderDied() {
                                ActivityManagerService.this.importanceTokenDied(this);
                            }
                        };
                        try {
                            iBinder.linkToDeath(importanceToken2, 0);
                            this.mImportantProcesses.put(i, importanceToken2);
                            processRecord.mState.setForcingToImportant(importanceToken2);
                        } catch (RemoteException unused) {
                        }
                        if (z3) {
                            updateOomAdjLocked(processRecord, 9);
                        }
                        resetPriorityAfterLockedSection();
                    }
                    z3 = z2;
                    if (z3) {
                    }
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final boolean isAppForeground(int i) {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                UidRecord uidRecord = this.mProcessList.mActiveUids.get(i);
                if (uidRecord != null && !uidRecord.isIdle()) {
                    boolean z = uidRecord.getCurProcState() <= 6;
                    resetPriorityAfterProcLockedSection();
                    return z;
                }
                resetPriorityAfterProcLockedSection();
                return false;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public final boolean isAppBad(String str, int i) {
        return this.mAppErrors.isBadProcess(str, i);
    }

    public int getUidState(int i) {
        int uidProcStateLOSP;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                uidProcStateLOSP = this.mProcessList.getUidProcStateLOSP(i);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return uidProcStateLOSP;
    }

    @GuardedBy({"this"})
    public int getUidStateLocked(int i) {
        return this.mProcessList.getUidProcStateLOSP(i);
    }

    @GuardedBy({"this"})
    public int getUidProcessCapabilityLocked(int i) {
        return this.mProcessList.getUidProcessCapabilityLOSP(i);
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ProcessInfoService */
    /* loaded from: classes.dex */
    public static class ProcessInfoService extends IProcessInfoService.Stub {
        public final ActivityManagerService mActivityManagerService;

        public ProcessInfoService(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        public void getProcessStatesFromPids(int[] iArr, int[] iArr2) {
            this.mActivityManagerService.getProcessStatesAndOomScoresForPIDs(iArr, iArr2, null);
        }

        public void getProcessStatesAndOomScoresFromPids(int[] iArr, int[] iArr2, int[] iArr3) {
            this.mActivityManagerService.getProcessStatesAndOomScoresForPIDs(iArr, iArr2, iArr3);
        }
    }

    public void getProcessStatesAndOomScoresForPIDs(int[] iArr, int[] iArr2, int[] iArr3) {
        if (iArr3 != null) {
            enforceCallingPermission("android.permission.GET_PROCESS_STATE_AND_OOM_SCORE", "getProcessStatesAndOomScoresForPIDs()");
        }
        if (iArr == null) {
            throw new NullPointerException("pids");
        }
        if (iArr2 == null) {
            throw new NullPointerException("states");
        }
        if (iArr.length != iArr2.length) {
            throw new IllegalArgumentException("pids and states arrays have different lengths!");
        }
        if (iArr3 != null && iArr.length != iArr3.length) {
            throw new IllegalArgumentException("pids and scores arrays have different lengths!");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    long j = Long.MIN_VALUE;
                    int i = -1;
                    for (int i2 = 0; i2 < iArr.length; i2++) {
                        ProcessRecord processRecord = this.mPidsSelfLocked.get(iArr[i2]);
                        if (processRecord != null) {
                            long pendingTopPidTime = this.mPendingStartActivityUids.getPendingTopPidTime(processRecord.uid, iArr[i2]);
                            if (pendingTopPidTime != 0) {
                                iArr2[i2] = 2;
                                if (iArr3 != null) {
                                    iArr3[i2] = -1;
                                }
                                if (pendingTopPidTime > j) {
                                    i = i2;
                                    j = pendingTopPidTime;
                                }
                            } else {
                                iArr2[i2] = processRecord.mState.getCurProcState();
                                if (iArr3 != null) {
                                    iArr3[i2] = processRecord.mState.getCurAdj();
                                }
                            }
                        } else {
                            iArr2[i2] = 20;
                            if (iArr3 != null) {
                                iArr3[i2] = -10000;
                            }
                        }
                    }
                    if (i != -1 && iArr3 != null) {
                        iArr3[i] = -2;
                    }
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    /* renamed from: com.android.server.am.ActivityManagerService$PermissionController */
    /* loaded from: classes.dex */
    public static class PermissionController extends IPermissionController.Stub {
        public ActivityManagerService mActivityManagerService;

        public PermissionController(ActivityManagerService activityManagerService) {
            this.mActivityManagerService = activityManagerService;
        }

        public boolean checkPermission(String str, int i, int i2) {
            return this.mActivityManagerService.checkPermission(str, i, i2) == 0;
        }

        public int noteOp(String str, int i, String str2) {
            return this.mActivityManagerService.mAppOpsService.noteOperation(AppOpsManager.strOpToOp(str), i, str2, null, false, "", false).getOpMode();
        }

        public String[] getPackagesForUid(int i) {
            return this.mActivityManagerService.mContext.getPackageManager().getPackagesForUid(i);
        }

        public boolean isRuntimePermission(String str) {
            try {
                return (this.mActivityManagerService.mContext.getPackageManager().getPermissionInfo(str, 0).protectionLevel & 15) == 1;
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e("ActivityManager", "No such permission: " + str, e);
                return false;
            }
        }

        public int getPackageUid(String str, int i) {
            try {
                return this.mActivityManagerService.mContext.getPackageManager().getPackageUid(str, i);
            } catch (PackageManager.NameNotFoundException unused) {
                return -1;
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$IntentFirewallInterface */
    /* loaded from: classes.dex */
    public class IntentFirewallInterface implements IntentFirewall.AMSInterface {
        public IntentFirewallInterface() {
        }

        @Override // com.android.server.firewall.IntentFirewall.AMSInterface
        public int checkComponentPermission(String str, int i, int i2, int i3, boolean z) {
            return ActivityManagerService.checkComponentPermission(str, i, i2, i3, z);
        }

        @Override // com.android.server.firewall.IntentFirewall.AMSInterface
        public Object getAMSLock() {
            return ActivityManagerService.this;
        }
    }

    @PermissionMethod
    public static int checkComponentPermission(@PermissionName String str, int i, int i2, int i3, boolean z) {
        ArraySet arraySet;
        if (i == MY_PID) {
            return 0;
        }
        if (str != null) {
            SparseArray<ProcessInfo> sparseArray = sActiveProcessInfoSelfLocked;
            synchronized (sparseArray) {
                ProcessInfo processInfo = sparseArray.get(i);
                if (processInfo != null && (arraySet = processInfo.deniedPermissions) != null && arraySet.contains(str)) {
                    return -1;
                }
            }
        }
        return ActivityManager.checkComponentPermission(str, i2, i3, z);
    }

    public final void enforceDebuggable(ProcessRecord processRecord) {
        if (Build.IS_DEBUGGABLE || processRecord.isDebuggable()) {
            return;
        }
        throw new SecurityException("Process not debuggable: " + processRecord.info.packageName);
    }

    public final void enforceDebuggable(ApplicationInfo applicationInfo) {
        if (Build.IS_DEBUGGABLE || (applicationInfo.flags & 2) != 0) {
            return;
        }
        throw new SecurityException("Process not debuggable: " + applicationInfo.packageName);
    }

    @PermissionMethod
    public int checkPermission(@PermissionName String str, int i, int i2) {
        if (str == null) {
            return -1;
        }
        return checkComponentPermission(str, i, i2, -1, true);
    }

    @PermissionMethod
    public int checkCallingPermission(@PermissionName String str) {
        return checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid());
    }

    @PermissionMethod
    public void enforceCallingPermission(@PermissionName String str, String str2) {
        if (checkCallingPermission(str) == 0) {
            return;
        }
        String str3 = "Permission Denial: " + str2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + str;
        Slog.w("ActivityManager", str3);
        throw new SecurityException(str3);
    }

    @PermissionMethod(anyOf = true)
    public final void enforceCallingHasAtLeastOnePermission(String str, String... strArr) {
        for (String str2 : strArr) {
            if (checkCallingPermission(str2) == 0) {
                return;
            }
        }
        String str3 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires one of " + Arrays.toString(strArr);
        Slog.w("ActivityManager", str3);
        throw new SecurityException(str3);
    }

    @PermissionMethod
    public void enforcePermission(@PermissionName String str, int i, int i2, String str2) {
        if (checkPermission(str, i, i2) == 0) {
            return;
        }
        String str3 = "Permission Denial: " + str2 + " from pid=" + i + ", uid=" + i2 + " requires " + str;
        Slog.w("ActivityManager", str3);
        throw new SecurityException(str3);
    }

    public boolean isAppStartModeDisabled(int i, String str) {
        boolean z;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                z = getAppStartModeLOSP(i, str, 0, -1, false, true, false) == 3;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return z;
    }

    public final boolean isInRestrictedBucket(int i, String str, long j) {
        return 45 <= this.mUsageStatsService.getAppStandbyBucket(str, i, j);
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public int appRestrictedInBackgroundLOSP(int i, String str, int i2) {
        if (i2 >= 26) {
            return 2;
        }
        if (this.mOnBattery && this.mConstants.FORCE_BACKGROUND_CHECK_ON_RESTRICTED_APPS && isInRestrictedBucket(UserHandle.getUserId(i), str, SystemClock.elapsedRealtime())) {
            return 1;
        }
        int noteOpNoThrow = getAppOpsManager().noteOpNoThrow(63, i, str, (String) null, "");
        return noteOpNoThrow != 0 ? noteOpNoThrow != 1 ? 2 : 1 : (!this.mForceBackgroundCheck || UserHandle.isCore(i) || isOnDeviceIdleAllowlistLOSP(i, true)) ? 0 : 1;
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public int appServicesRestrictedInBackgroundLOSP(int i, String str, int i2) {
        if (this.mPackageManagerInt.isPackagePersistent(str) || uidOnBackgroundAllowlistLOSP(i) || isOnDeviceIdleAllowlistLOSP(i, false)) {
            return 0;
        }
        return appRestrictedInBackgroundLOSP(i, str, i2);
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public int getAppStartModeLOSP(int i, String str, int i2, int i3, boolean z, boolean z2, boolean z3) {
        boolean isEphemeral;
        int appServicesRestrictedInBackgroundLOSP;
        ProcessRecord processRecord;
        if (this.mInternal.isPendingTopUid(i)) {
            return 0;
        }
        UidRecord uidRecordLOSP = this.mProcessList.getUidRecordLOSP(i);
        if (uidRecordLOSP == null || z || z3 || uidRecordLOSP.isIdle()) {
            if (uidRecordLOSP == null) {
                isEphemeral = getPackageManagerInternal().isPackageEphemeral(UserHandle.getUserId(i), str);
            } else {
                isEphemeral = uidRecordLOSP.isEphemeral();
            }
            if (isEphemeral) {
                return 3;
            }
            if (z2) {
                return 0;
            }
            if (z) {
                appServicesRestrictedInBackgroundLOSP = appRestrictedInBackgroundLOSP(i, str, i2);
            } else {
                appServicesRestrictedInBackgroundLOSP = appServicesRestrictedInBackgroundLOSP(i, str, i2);
            }
            if (appServicesRestrictedInBackgroundLOSP == 1 && i3 >= 0) {
                synchronized (this.mPidsSelfLocked) {
                    processRecord = this.mPidsSelfLocked.get(i3);
                }
                if (processRecord != null && !ActivityManager.isProcStateBackground(processRecord.mState.getCurProcState())) {
                    return 0;
                }
            }
            return appServicesRestrictedInBackgroundLOSP;
        }
        return 0;
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public boolean isOnDeviceIdleAllowlistLOSP(int i, boolean z) {
        int[] iArr;
        int appId = UserHandle.getAppId(i);
        if (z) {
            iArr = this.mDeviceIdleExceptIdleAllowlist;
        } else {
            iArr = this.mDeviceIdleAllowlist;
        }
        return Arrays.binarySearch(iArr, appId) >= 0 || Arrays.binarySearch(this.mDeviceIdleTempAllowlist, appId) >= 0 || this.mPendingTempAllowlist.get(i) != null;
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public FgsTempAllowListItem isAllowlistedForFgsStartLOSP(int i) {
        if (Arrays.binarySearch(this.mDeviceIdleExceptIdleAllowlist, UserHandle.getAppId(i)) >= 0) {
            return FAKE_TEMP_ALLOW_LIST_ITEM;
        }
        Pair<Long, FgsTempAllowListItem> pair = this.mFgsStartTempAllowList.get(i);
        if (pair == null) {
            return null;
        }
        return (FgsTempAllowListItem) pair.second;
    }

    /* renamed from: com.android.server.am.ActivityManagerService$GetBackgroundStartPrivilegesFunctor */
    /* loaded from: classes.dex */
    public static class GetBackgroundStartPrivilegesFunctor implements Consumer<ProcessRecord> {
        public BackgroundStartPrivileges mBackgroundStartPrivileges;
        public int mUid;

        public GetBackgroundStartPrivilegesFunctor() {
            this.mBackgroundStartPrivileges = BackgroundStartPrivileges.NONE;
        }

        public void prepare(int i) {
            this.mUid = i;
            this.mBackgroundStartPrivileges = BackgroundStartPrivileges.NONE;
        }

        public BackgroundStartPrivileges getResult() {
            return this.mBackgroundStartPrivileges;
        }

        @Override // java.util.function.Consumer
        public void accept(ProcessRecord processRecord) {
            if (processRecord.uid == this.mUid) {
                this.mBackgroundStartPrivileges = this.mBackgroundStartPrivileges.merge(processRecord.getBackgroundStartPrivileges());
            }
        }
    }

    public final BackgroundStartPrivileges getBackgroundStartPrivileges(int i) {
        BackgroundStartPrivileges result;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mGetBackgroundStartPrivilegesFunctor.prepare(i);
                this.mProcessList.forEachLruProcessesLOSP(false, this.mGetBackgroundStartPrivilegesFunctor);
                result = this.mGetBackgroundStartPrivilegesFunctor.getResult();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return result;
    }

    @VisibleForTesting
    public void grantImplicitAccess(int i, Intent intent, int i2, int i3) {
        getPackageManagerInternal().grantImplicitAccess(i, intent, i3, i2, true);
    }

    public int checkUriPermission(Uri uri, int i, int i2, int i3, int i4, IBinder iBinder) {
        enforceNotIsolatedCaller("checkUriPermission");
        if (i == MY_PID) {
            return 0;
        }
        return ((i2 == 0 || !this.mPackageManagerInt.filterAppAccess(i2, Binder.getCallingUid())) && this.mUgmInternal.checkUriPermission(new GrantUri(i4, uri, i3), i2, i3)) ? 0 : -1;
    }

    public int[] checkUriPermissions(List<Uri> list, int i, int i2, int i3, int i4, IBinder iBinder) {
        int size = list.size();
        int[] iArr = new int[size];
        Arrays.fill(iArr, -1);
        for (int i5 = 0; i5 < size; i5++) {
            Uri uri = list.get(i5);
            iArr[i5] = checkUriPermission(ContentProvider.getUriWithoutUserId(uri), i, i2, i3, ContentProvider.getUserIdFromUri(uri, i4), iBinder);
        }
        return iArr;
    }

    public void grantUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i, int i2) {
        enforceNotIsolatedCaller("grantUriPermission");
        GrantUri grantUri = new GrantUri(i2, uri, i);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    throw new SecurityException("Unable to find app for caller " + iApplicationThread + " when granting permission to uri " + grantUri);
                } else if (str == null) {
                    throw new IllegalArgumentException("null target");
                } else {
                    int userId = UserHandle.getUserId(recordForAppLOSP.uid);
                    if (this.mPackageManagerInt.filterAppAccess(str, recordForAppLOSP.uid, userId)) {
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    Preconditions.checkFlagsArgument(i, (int) FrameworkStatsLog.f411x277c884);
                    Intent intent = new Intent();
                    intent.setData(uri);
                    intent.setFlags(i);
                    this.mUgmInternal.grantUriPermissionUncheckedFromIntent(this.mUgmInternal.checkGrantUriPermissionFromIntent(intent, recordForAppLOSP.uid, str, userId), null);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void revokeUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i, int i2) {
        enforceNotIsolatedCaller("revokeUriPermission");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    throw new SecurityException("Unable to find app for caller " + iApplicationThread + " when revoking permission to uri " + uri);
                } else if (uri == null) {
                    Slog.w("ActivityManager", "revokeUriPermission: null uri");
                    resetPriorityAfterLockedSection();
                } else if (!Intent.isAccessUriMode(i)) {
                    resetPriorityAfterLockedSection();
                } else {
                    if (this.mCpHelper.getProviderInfoLocked(uri.getAuthority(), i2, 786432) == null) {
                        Slog.w("ActivityManager", "No content provider found for permission revoke: " + uri.toSafeString());
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    this.mUgmInternal.revokeUriPermission(str, recordForAppLOSP.uid, new GrantUri(i2, uri, i), i);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void showWaitingForDebugger(IApplicationThread iApplicationThread, boolean z) {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                ProcessRecord recordForAppLOSP = iApplicationThread != null ? getRecordForAppLOSP(iApplicationThread) : null;
                if (recordForAppLOSP == null) {
                    resetPriorityAfterProcLockedSection();
                    return;
                }
                Message obtain = Message.obtain();
                obtain.what = 6;
                obtain.obj = recordForAppLOSP;
                obtain.arg1 = z ? 1 : 0;
                this.mUiHandler.sendMessage(obtain);
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public void getMemoryInfo(ActivityManager.MemoryInfo memoryInfo) {
        this.mProcessList.getMemoryInfo(memoryInfo);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int i) {
        return this.mActivityTaskManager.getTasks(i);
    }

    public void cancelTaskWindowTransition(int i) {
        this.mActivityTaskManager.cancelTaskWindowTransition(i);
    }

    public void setTaskResizeable(int i, int i2) {
        this.mActivityTaskManager.setTaskResizeable(i, i2);
    }

    public void resizeTask(int i, Rect rect, int i2) {
        this.mActivityTaskManager.resizeTask(i, rect, i2);
    }

    public Rect getTaskBounds(int i) {
        return this.mActivityTaskManager.getTaskBounds(i);
    }

    public boolean removeTask(int i) {
        return this.mActivityTaskManager.removeTask(i);
    }

    public void moveTaskToFront(IApplicationThread iApplicationThread, String str, int i, int i2, Bundle bundle) {
        this.mActivityTaskManager.moveTaskToFront(iApplicationThread, str, i, i2, bundle);
    }

    public boolean moveActivityTaskToBack(IBinder iBinder, boolean z) {
        return ActivityClient.getInstance().moveActivityTaskToBack(iBinder, z);
    }

    public void moveTaskToRootTask(int i, int i2, boolean z) {
        this.mActivityTaskManager.moveTaskToRootTask(i, i2, z);
    }

    public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2, int i3) {
        return this.mActivityTaskManager.getRecentTasks(i, i2, i3);
    }

    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() {
        return this.mActivityTaskManager.getAllRootTaskInfos();
    }

    public int getTaskForActivity(IBinder iBinder, boolean z) {
        return ActivityClient.getInstance().getTaskForActivity(iBinder, z);
    }

    public void updateLockTaskPackages(int i, String[] strArr) {
        this.mActivityTaskManager.updateLockTaskPackages(i, strArr);
    }

    public boolean isInLockTaskMode() {
        return this.mActivityTaskManager.isInLockTaskMode();
    }

    public int getLockTaskModeState() {
        return this.mActivityTaskManager.getLockTaskModeState();
    }

    public void startSystemLockTaskMode(int i) throws RemoteException {
        this.mActivityTaskManager.startSystemLockTaskMode(i);
    }

    @VisibleForTesting
    public IPackageManager getPackageManager() {
        return AppGlobals.getPackageManager();
    }

    @VisibleForTesting
    public PackageManagerInternal getPackageManagerInternal() {
        if (this.mPackageManagerInt == null) {
            this.mPackageManagerInt = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPackageManagerInt;
    }

    public final PermissionManagerServiceInternal getPermissionManagerInternal() {
        if (this.mPermissionManagerInt == null) {
            this.mPermissionManagerInt = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        }
        return this.mPermissionManagerInt;
    }

    public final TestUtilityService getTestUtilityServiceLocked() {
        if (this.mTestUtilityService == null) {
            this.mTestUtilityService = (TestUtilityService) LocalServices.getService(TestUtilityService.class);
        }
        return this.mTestUtilityService;
    }

    public void appNotResponding(String str) {
        appNotResponding(str, false);
    }

    public void appNotResponding(String str, boolean z) {
        TimeoutRecord forApp = TimeoutRecord.forApp("App requested: " + str);
        int callingPid = Binder.getCallingPid();
        forApp.mLatencyTracker.waitingOnPidLockStarted();
        synchronized (this.mPidsSelfLocked) {
            forApp.mLatencyTracker.waitingOnPidLockEnded();
            ProcessRecord processRecord = this.mPidsSelfLocked.get(callingPid);
            if (processRecord == null) {
                throw new SecurityException("Unknown process: " + callingPid);
            }
            this.mAnrHelper.appNotResponding(processRecord, null, processRecord.info, null, null, false, forApp, z);
        }
    }

    public void appNotResponding(ProcessRecord processRecord, TimeoutRecord timeoutRecord) {
        this.mAnrHelper.appNotResponding(processRecord, timeoutRecord);
    }

    public final void appNotResponding(String str, int i, TimeoutRecord timeoutRecord) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(timeoutRecord);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord processRecordLocked = getProcessRecordLocked(str, i);
                if (processRecordLocked == null) {
                    Slog.e("ActivityManager", "Unknown process: " + str);
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mAnrHelper.appNotResponding(processRecordLocked, timeoutRecord);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void startPersistentApps(int i) {
        ProcessRecord addAppLocked;
        if (this.mFactoryTest == 1) {
            return;
        }
        synchronized (this) {
            try {
                try {
                    boostPriorityForLockedSection();
                    for (ApplicationInfo applicationInfo : AppGlobals.getPackageManager().getPersistentApplications(i | 1024).getList()) {
                        if (!PackageManagerShellCommandDataLoader.PACKAGE.equals(applicationInfo.packageName) && (addAppLocked = addAppLocked(applicationInfo, null, false, null, 2)) != null) {
                            addAppLocked.mProfile.addHostingComponentType(2);
                        }
                    }
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (RemoteException unused) {
            }
        }
        resetPriorityAfterLockedSection();
    }

    public ContentProviderHelper getContentProviderHelper() {
        return this.mCpHelper;
    }

    public final ContentProviderHolder getContentProvider(IApplicationThread iApplicationThread, String str, String str2, int i, boolean z) {
        traceBegin(64L, "getContentProvider: ", str2);
        try {
            return this.mCpHelper.getContentProvider(iApplicationThread, str, str2, i, z);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public ContentProviderHolder getContentProviderExternal(String str, int i, IBinder iBinder, String str2) {
        traceBegin(64L, "getContentProviderExternal: ", str);
        try {
            return this.mCpHelper.getContentProviderExternal(str, i, iBinder, str2);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void removeContentProvider(IBinder iBinder, boolean z) {
        this.mCpHelper.removeContentProvider(iBinder, z);
    }

    @Deprecated
    public void removeContentProviderExternal(String str, IBinder iBinder) {
        traceBegin(64L, "removeContentProviderExternal: ", str);
        try {
            removeContentProviderExternalAsUser(str, iBinder, UserHandle.getCallingUserId());
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void removeContentProviderExternalAsUser(String str, IBinder iBinder, int i) {
        traceBegin(64L, "removeContentProviderExternalAsUser: ", str);
        try {
            this.mCpHelper.removeContentProviderExternalAsUser(str, iBinder, i);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final void publishContentProviders(IApplicationThread iApplicationThread, List<ContentProviderHolder> list) {
        ProviderInfo providerInfo;
        String str;
        if (Trace.isTagEnabled(64L)) {
            StringBuilder sb = new StringBuilder(256);
            sb.append("publishContentProviders: ");
            if (list != null) {
                int size = list.size();
                boolean z = true;
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    ContentProviderHolder contentProviderHolder = list.get(i);
                    if (contentProviderHolder != null && (providerInfo = contentProviderHolder.info) != null && (str = providerInfo.authority) != null) {
                        if (sb.length() + str.length() > 256) {
                            sb.append("[[TRUNCATED]]");
                            break;
                        }
                        if (z) {
                            z = false;
                        } else {
                            sb.append(';');
                        }
                        sb.append(contentProviderHolder.info.authority);
                    }
                    i++;
                }
            }
            Trace.traceBegin(64L, sb.toString());
        }
        try {
            this.mCpHelper.publishContentProviders(iApplicationThread, list);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public boolean refContentProvider(IBinder iBinder, int i, int i2) {
        return this.mCpHelper.refContentProvider(iBinder, i, i2);
    }

    public void unstableProviderDied(IBinder iBinder) {
        this.mCpHelper.unstableProviderDied(iBinder);
    }

    public void appNotRespondingViaProvider(IBinder iBinder) {
        this.mCpHelper.appNotRespondingViaProvider(iBinder);
    }

    public void getMimeTypeFilterAsync(Uri uri, int i, RemoteCallback remoteCallback) {
        this.mCpHelper.getMimeTypeFilterAsync(uri, i, remoteCallback);
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public final boolean uidOnBackgroundAllowlistLOSP(int i) {
        int appId = UserHandle.getAppId(i);
        for (int i2 : this.mBackgroundAppIdAllowlist) {
            if (appId == i2) {
                return true;
            }
        }
        return false;
    }

    public boolean isBackgroundRestricted(String str) {
        int callingUid = Binder.getCallingUid();
        if (AppGlobals.getPackageManager().getPackageUid(str, 268435456L, UserHandle.getUserId(callingUid)) != callingUid) {
            throw new IllegalArgumentException("Uid " + callingUid + " cannot query restriction state for package " + str);
        }
        return isBackgroundRestrictedNoCheck(callingUid, str);
    }

    @VisibleForTesting
    public boolean isBackgroundRestrictedNoCheck(int i, String str) {
        return getAppOpsManager().checkOpNoThrow(70, i, str) != 0;
    }

    public void backgroundAllowlistUid(int i) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Only the OS may call backgroundAllowlistUid()");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    int[] iArr = this.mBackgroundAppIdAllowlist;
                    int length = iArr.length;
                    int[] iArr2 = new int[length + 1];
                    System.arraycopy(iArr, 0, iArr2, 0, length);
                    iArr2[length] = UserHandle.getAppId(i);
                    this.mBackgroundAppIdAllowlist = iArr2;
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @GuardedBy({"this"})
    public final ProcessRecord addAppLocked(ApplicationInfo applicationInfo, String str, boolean z, String str2, int i) {
        return addAppLocked(applicationInfo, str, z, false, str2, i);
    }

    @GuardedBy({"this"})
    public final ProcessRecord addAppLocked(ApplicationInfo applicationInfo, String str, boolean z, boolean z2, String str2, int i) {
        return addAppLocked(applicationInfo, str, z, z2, false, str2, i);
    }

    @GuardedBy({"this"})
    public final ProcessRecord addAppLocked(ApplicationInfo applicationInfo, String str, boolean z, boolean z2, boolean z3, String str2, int i) {
        return addAppLocked(applicationInfo, str, z, false, 0, null, z2, z3, str2, i);
    }

    public final ProcessRecord addAppLocked(ApplicationInfo applicationInfo, String str, boolean z, boolean z2, int i, String str2, boolean z3, boolean z4, String str3, int i2) {
        ProcessRecord processRecord;
        if (z) {
            processRecord = null;
        } else {
            processRecord = getProcessRecordLocked(str != null ? str : applicationInfo.processName, applicationInfo.uid);
        }
        if (processRecord == null) {
            processRecord = this.mProcessList.newProcessRecordLocked(applicationInfo, str, z, 0, z2, i, str2, new HostingRecord("added application", str != null ? str : applicationInfo.processName));
            updateLruProcessLocked(processRecord, false, null);
            updateOomAdjLocked(processRecord, 11);
        }
        this.mUsageStatsService.reportEvent(applicationInfo.packageName, UserHandle.getUserId(processRecord.uid), 31);
        if (!z2) {
            try {
                this.mPackageManagerInt.setPackageStoppedState(applicationInfo.packageName, false, UserHandle.getUserId(processRecord.uid));
            } catch (IllegalArgumentException e) {
                Slog.w("ActivityManager", "Failed trying to unstop package " + applicationInfo.packageName + ": " + e);
            }
        }
        if ((applicationInfo.flags & 9) == 9) {
            processRecord.setPersistent(true);
            processRecord.mState.setMaxAdj(-800);
        }
        if (processRecord.getThread() == null && this.mPersistentStartingProcesses.indexOf(processRecord) < 0) {
            this.mPersistentStartingProcesses.add(processRecord);
            this.mProcessList.startProcessLocked(processRecord, new HostingRecord("added application", str != null ? str : processRecord.processName), i2, z3, z4, str3);
        }
        return processRecord;
    }

    public void unhandledBack() {
        this.mActivityTaskManager.unhandledBack();
    }

    public ParcelFileDescriptor openContentUri(String str) throws RemoteException {
        AndroidPackage androidPackage;
        enforceNotIsolatedCaller("openContentUri");
        int callingUserId = UserHandle.getCallingUserId();
        Uri parse = Uri.parse(str);
        String authority = parse.getAuthority();
        ContentProviderHolder contentProviderExternalUnchecked = this.mCpHelper.getContentProviderExternalUnchecked(authority, null, Binder.getCallingUid(), "*opencontent*", callingUserId);
        if (contentProviderExternalUnchecked != null) {
            try {
                int callingUid = Binder.getCallingUid();
                String resolvePackageName = AppOpsManager.resolvePackageName(callingUid, null);
                if (resolvePackageName != null) {
                    androidPackage = this.mPackageManagerInt.getPackage(resolvePackageName);
                } else {
                    androidPackage = this.mPackageManagerInt.getPackage(callingUid);
                }
                if (androidPackage == null) {
                    Log.e("ActivityManager", "Cannot find package for uid: " + callingUid);
                    return null;
                }
                return contentProviderExternalUnchecked.provider.openFile(new AttributionSource(Binder.getCallingUid(), androidPackage.getPackageName(), null), parse, "r", (ICancellationSignal) null);
            } catch (FileNotFoundException unused) {
                return null;
            } finally {
                this.mCpHelper.removeContentProviderExternalUnchecked(authority, null, callingUserId);
            }
        }
        Slog.d("ActivityManager", "Failed to get provider for authority '" + authority + "'");
        return null;
    }

    public void reportGlobalUsageEvent(int i) {
        int currentUserId = this.mUserController.getCurrentUserId();
        this.mUsageStatsService.reportEvent(PackageManagerShellCommandDataLoader.PACKAGE, currentUserId, i);
        int[] currentProfileIds = this.mUserController.getCurrentProfileIds();
        if (currentProfileIds != null) {
            for (int length = currentProfileIds.length - 1; length >= 0; length--) {
                if (currentProfileIds[length] != currentUserId) {
                    this.mUsageStatsService.reportEvent(PackageManagerShellCommandDataLoader.PACKAGE, currentProfileIds[length], i);
                }
            }
        }
    }

    public void reportCurWakefulnessUsageEvent() {
        reportGlobalUsageEvent(this.mWakefulness.get() == 1 ? 15 : 16);
    }

    public void onWakefulnessChanged(int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                boolean z = this.mWakefulness.getAndSet(i) == 1;
                boolean z2 = i == 1;
                if (z != z2) {
                    this.mServices.updateScreenStateLocked(z2);
                    reportCurWakefulnessUsageEvent();
                    this.mActivityTaskManager.onScreenAwakeChanged(z2);
                    this.mOomAdjProfiler.onWakefulnessChanged(i);
                    this.mOomAdjuster.onWakefulnessChanged(i);
                }
                updateOomAdjLocked(9);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void notifyCleartextNetwork(int i, byte[] bArr) {
        this.mHandler.obtainMessage(49, i, 0, bArr).sendToTarget();
    }

    public boolean shutdown(int i) {
        if (checkCallingPermission("android.permission.SHUTDOWN") != 0) {
            throw new SecurityException("Requires permission android.permission.SHUTDOWN");
        }
        boolean shuttingDown = this.mAtmInternal.shuttingDown(this.mBooted, i);
        this.mAppOpsService.shutdown();
        if (this.mUsageStatsService != null) {
            this.mUsageStatsService.prepareShutdown();
        }
        this.mBatteryStatsService.shutdown();
        this.mProcessStats.shutdown();
        return shuttingDown;
    }

    public void notifyLockedProfile(int i) {
        this.mAtmInternal.notifyLockedProfile(i, this.mUserController.getCurrentUserId());
    }

    public void startConfirmDeviceCredentialIntent(Intent intent, Bundle bundle) {
        this.mAtmInternal.startConfirmDeviceCredentialIntent(intent, bundle);
    }

    public void stopAppSwitches() {
        this.mActivityTaskManager.stopAppSwitches();
    }

    public void resumeAppSwitches() {
        this.mActivityTaskManager.resumeAppSwitches();
    }

    public void setDebugApp(String str, boolean z, boolean z2) {
        setDebugApp(str, z, z2, false);
    }

    public final void setDebugApp(String str, boolean z, boolean z2, boolean z3) {
        enforceCallingPermission("android.permission.SET_DEBUG_APP", "setDebugApp()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z4 = true;
        if (z2) {
            try {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                Settings.Global.putString(contentResolver, "debug_app", str);
                Settings.Global.putInt(contentResolver, "wait_for_debugger", z ? 1 : 0);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        synchronized (this) {
            boostPriorityForLockedSection();
            if (!z2) {
                this.mOrigDebugApp = this.mDebugApp;
                this.mOrigWaitForDebugger = this.mWaitForDebugger;
            }
            this.mDebugApp = str;
            this.mWaitForDebugger = z;
            this.mSuspendUponWait = z3;
            if (z2) {
                z4 = false;
            }
            this.mDebugTransient = z4;
            if (str != null) {
                forceStopPackageLocked(str, -1, false, false, true, true, false, -1, "set debug app");
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setAgentApp(String str, String str2) {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        synchronized (this.mAppProfiler.mProfilerLock) {
            this.mAppProfiler.setAgentAppLPf(str, str2);
        }
    }

    public void setTrackAllocationApp(ApplicationInfo applicationInfo, String str) {
        enforceDebuggable(applicationInfo);
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mTrackAllocationApp = str;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public void setProfileApp(ApplicationInfo applicationInfo, String str, ProfilerInfo profilerInfo, ApplicationInfo applicationInfo2) {
        synchronized (this.mAppProfiler.mProfilerLock) {
            if (!Build.IS_DEBUGGABLE) {
                boolean z = true;
                boolean z2 = (applicationInfo.flags & 2) != 0;
                boolean isProfileableByShell = applicationInfo.isProfileableByShell();
                if (applicationInfo2 != null) {
                    if ((applicationInfo2.flags & 2) == 0) {
                        z = false;
                    }
                    z2 |= z;
                    isProfileableByShell |= applicationInfo2.isProfileableByShell();
                }
                if (!z2 && !isProfileableByShell) {
                    throw new SecurityException("Process not debuggable, and not profileable by shell: " + applicationInfo.packageName);
                }
            }
            this.mAppProfiler.setProfileAppLPf(str, profilerInfo);
        }
    }

    public void setNativeDebuggingAppLocked(ApplicationInfo applicationInfo, String str) {
        enforceDebuggable(applicationInfo);
        this.mNativeDebuggingApp = str;
    }

    public void setAlwaysFinish(boolean z) {
        enforceCallingPermission("android.permission.SET_ALWAYS_FINISH", "setAlwaysFinish()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(this.mContext.getContentResolver(), "always_finish_activities", z ? 1 : 0);
            synchronized (this) {
                boostPriorityForLockedSection();
                this.mAlwaysFinishActivities = z;
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setActivityController(IActivityController iActivityController, boolean z) {
        if (iActivityController != null) {
            Binder.allowBlocking(iActivityController.asBinder());
        }
        this.mActivityTaskManager.setActivityController(iActivityController, z);
    }

    public void setUserIsMonkey(boolean z) {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    int callingPid = Binder.getCallingPid();
                    ProcessRecord processRecord = this.mPidsSelfLocked.get(callingPid);
                    if (processRecord == null) {
                        throw new SecurityException("Unknown process: " + callingPid);
                    } else if (processRecord.getActiveInstrumentation() == null || processRecord.getActiveInstrumentation().mUiAutomationConnection == null) {
                        throw new SecurityException("Only an instrumentation process with a UiAutomation can call setUserIsMonkey");
                    }
                }
                this.mUserIsMonkey = z;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public boolean isUserAMonkey() {
        boolean z;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                z = this.mUserIsMonkey || this.mActivityTaskManager.isControllerAMonkey();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return z;
    }

    public void requestSystemServerHeapDump() {
        ProcessRecord processRecord;
        if (!Build.IS_DEBUGGABLE) {
            Slog.wtf("ActivityManager", "requestSystemServerHeapDump called on a user build");
        } else if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Only the system process is allowed to request a system heap dump");
        } else {
            synchronized (this.mPidsSelfLocked) {
                processRecord = this.mPidsSelfLocked.get(Process.myPid());
            }
            if (processRecord == null) {
                Slog.w("ActivityManager", "system process not in mPidsSelfLocked: " + Process.myPid());
                return;
            }
            synchronized (this.mAppProfiler.mProfilerLock) {
                this.mAppProfiler.startHeapDumpLPf(processRecord.mProfile, true);
            }
        }
    }

    public void requestBugReport(int i) {
        requestBugReportWithDescription(null, null, i, 0L);
    }

    public void requestBugReportWithDescription(String str, String str2, int i) {
        requestBugReportWithDescription(str, str2, i, 0L);
    }

    public void requestBugReportWithDescription(String str, String str2, int i, long j) {
        String str3;
        if (i == 0) {
            str3 = "bugreportfull";
        } else if (i == 1) {
            str3 = "bugreportplus";
        } else if (i == 2) {
            str3 = "bugreportremote";
        } else if (i == 3) {
            str3 = "bugreportwear";
        } else if (i == 4) {
            str3 = "bugreporttelephony";
        } else if (i != 5) {
            throw new IllegalArgumentException("Provided bugreport type is not correct, value: " + i);
        } else {
            str3 = "bugreportwifi";
        }
        Slog.i("ActivityManager", str3 + " requested by UID " + Binder.getCallingUid());
        enforceCallingPermission("android.permission.DUMP", "requestBugReport");
        if (!TextUtils.isEmpty(str)) {
            if (str.length() > 100) {
                throw new IllegalArgumentException("shareTitle should be less than 100 characters");
            }
            if (!TextUtils.isEmpty(str2) && str2.length() > 150) {
                throw new IllegalArgumentException("shareDescription should be less than 150 characters");
            }
            Slog.d("ActivityManager", "Bugreport notification title " + str + " description " + str2);
        }
        Intent intent = new Intent();
        intent.setAction("com.android.internal.intent.action.BUGREPORT_REQUESTED");
        intent.setPackage("com.android.shell");
        intent.putExtra("android.intent.extra.BUGREPORT_TYPE", i);
        intent.putExtra("android.intent.extra.BUGREPORT_NONCE", j);
        intent.addFlags(268435456);
        intent.addFlags(16777216);
        if (str != null) {
            intent.putExtra("android.intent.extra.TITLE", str);
        }
        if (str2 != null) {
            intent.putExtra("android.intent.extra.DESCRIPTION", str2);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, getCurrentUser().getUserHandle());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void requestTelephonyBugReport(String str, String str2) {
        requestBugReportWithDescription(str, str2, 4);
    }

    public void requestWifiBugReport(String str, String str2) {
        requestBugReportWithDescription(str, str2, 5);
    }

    public void requestInteractiveBugReport() {
        requestBugReportWithDescription(null, null, 1);
    }

    public void requestInteractiveBugReportWithDescription(String str, String str2) {
        requestBugReportWithDescription(str, str2, 1);
    }

    public void requestFullBugReport() {
        requestBugReportWithDescription(null, null, 0);
    }

    public void requestRemoteBugReport(long j) {
        requestBugReportWithDescription(null, null, 2, j);
    }

    public boolean launchBugReportHandlerApp() {
        Context createContextAsUser = this.mContext.createContextAsUser(getCurrentUser().getUserHandle(), 0);
        if (BugReportHandlerUtil.isBugReportHandlerEnabled(createContextAsUser)) {
            Slog.i("ActivityManager", "launchBugReportHandlerApp requested by UID " + Binder.getCallingUid());
            enforceCallingPermission("android.permission.DUMP", "launchBugReportHandlerApp");
            return BugReportHandlerUtil.launchBugReportHandlerApp(createContextAsUser);
        }
        return false;
    }

    public List<String> getBugreportWhitelistedPackages() {
        enforceCallingPermission("android.permission.MANAGE_DEBUGGING", "getBugreportWhitelistedPackages");
        return new ArrayList(SystemConfig.getInstance().getBugreportWhitelistedPackages());
    }

    public void registerProcessObserver(IProcessObserver iProcessObserver) {
        enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "registerProcessObserver()");
        this.mProcessList.registerProcessObserver(iProcessObserver);
    }

    public void unregisterProcessObserver(IProcessObserver iProcessObserver) {
        this.mProcessList.unregisterProcessObserver(iProcessObserver);
    }

    public int getUidProcessState(int i, String str) {
        if (!hasUsageStatsPermission(str)) {
            enforceCallingPermission("android.permission.PACKAGE_USAGE_STATS", "getUidProcessState");
        }
        this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getUserId(i), false, 2, "getUidProcessState", str);
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (this.mPendingStartActivityUids.isPendingTopUid(i)) {
                    resetPriorityAfterProcLockedSection();
                    return 2;
                }
                int uidProcStateLOSP = this.mProcessList.getUidProcStateLOSP(i);
                resetPriorityAfterProcLockedSection();
                return uidProcStateLOSP;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public int getUidProcessCapabilities(int i, String str) {
        int uidProcessCapabilityLOSP;
        if (!hasUsageStatsPermission(str)) {
            enforceCallingPermission("android.permission.PACKAGE_USAGE_STATS", "getUidProcessState");
        }
        this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getUserId(i), false, 2, "getUidProcessCapabilities", str);
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                uidProcessCapabilityLOSP = this.mProcessList.getUidProcessCapabilityLOSP(i);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return uidProcessCapabilityLOSP;
    }

    public void registerUidObserver(IUidObserver iUidObserver, int i, int i2, String str) {
        if (!hasUsageStatsPermission(str)) {
            enforceCallingPermission("android.permission.PACKAGE_USAGE_STATS", "registerUidObserver");
        }
        this.mUidObserverController.register(iUidObserver, i, i2, str, Binder.getCallingUid());
    }

    public void unregisterUidObserver(IUidObserver iUidObserver) {
        this.mUidObserverController.unregister(iUidObserver);
    }

    public boolean isUidActive(int i, String str) {
        if (!hasUsageStatsPermission(str)) {
            enforceCallingPermission("android.permission.PACKAGE_USAGE_STATS", "isUidActive");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (isUidActiveLOSP(i)) {
                    resetPriorityAfterProcLockedSection();
                    return true;
                }
                resetPriorityAfterProcLockedSection();
                return this.mInternal.isPendingTopUid(i);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public boolean isUidActiveLOSP(int i) {
        UidRecord uidRecordLOSP = this.mProcessList.getUidRecordLOSP(i);
        return (uidRecordLOSP == null || uidRecordLOSP.isSetIdle()) ? false : true;
    }

    public void setPersistentVrThread(int i) {
        this.mActivityTaskManager.setPersistentVrThread(i);
    }

    public static boolean scheduleAsRegularPriority(int i, boolean z) {
        try {
            Process.setThreadScheduler(i, 0, 0);
            return true;
        } catch (IllegalArgumentException e) {
            if (!z) {
                Slog.w("ActivityManager", "Failed to set scheduling policy, thread does not exist:\n" + e);
            }
            return false;
        } catch (SecurityException e2) {
            if (!z) {
                Slog.w("ActivityManager", "Failed to set scheduling policy, not allowed:\n" + e2);
            }
            return false;
        }
    }

    public static boolean scheduleAsFifoPriority(int i, boolean z) {
        try {
            Process.setThreadScheduler(i, 1073741825, 1);
            return true;
        } catch (IllegalArgumentException e) {
            if (z) {
                return false;
            }
            Slog.w("ActivityManager", "Failed to set scheduling policy, thread does not exist:\n" + e);
            return false;
        } catch (SecurityException e2) {
            if (z) {
                return false;
            }
            Slog.w("ActivityManager", "Failed to set scheduling policy, not allowed:\n" + e2);
            return false;
        }
    }

    public void setRenderThread(int i) {
        ProcessRecord processRecord;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                int callingPid = Binder.getCallingPid();
                if (callingPid == Process.myPid()) {
                    demoteSystemServerRenderThread(i);
                    resetPriorityAfterProcLockedSection();
                    return;
                }
                synchronized (this.mPidsSelfLocked) {
                    processRecord = this.mPidsSelfLocked.get(callingPid);
                }
                if (processRecord != null && processRecord.getRenderThreadTid() == 0 && i > 0) {
                    if (!Process.isThreadInProcess(callingPid, i)) {
                        throw new IllegalArgumentException("Render thread does not belong to process");
                    }
                    processRecord.setRenderThreadTid(i);
                    if (processRecord.mState.getCurrentSchedulingGroup() == 3) {
                        if (this.mUseFifoUiScheduling) {
                            Process.setThreadScheduler(processRecord.getRenderThreadTid(), 1073741825, 1);
                        } else {
                            Process.setThreadPriority(processRecord.getRenderThreadTid(), -10);
                        }
                    }
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public final void demoteSystemServerRenderThread(int i) {
        Process.setThreadPriority(i, 10);
    }

    public boolean isVrModePackageEnabled(ComponentName componentName) {
        this.mActivityTaskManager.enforceSystemHasVrFeature();
        return ((VrManagerInternal) LocalServices.getService(VrManagerInternal.class)).hasVrPackage(componentName, UserHandle.getCallingUserId()) == 0;
    }

    public boolean isTopActivityImmersive() {
        return this.mActivityTaskManager.isTopActivityImmersive();
    }

    public boolean isTopOfTask(IBinder iBinder) {
        return ActivityClient.getInstance().isTopOfTask(iBinder);
    }

    public void setHasTopUi(boolean z) throws RemoteException {
        boolean z2;
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0) {
            String str = "Permission Denial: setHasTopUi() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.INTERNAL_SYSTEM_WINDOW";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    ProcessRecord processRecord = this.mPidsSelfLocked.get(callingPid);
                    if (processRecord == null) {
                        Slog.w("ActivityManager", "setHasTopUi called on unknown pid: " + callingPid);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    if (processRecord.mState.hasTopUi() != z) {
                        processRecord.mState.setHasTopUi(z);
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (z2) {
                        updateOomAdjLocked(processRecord, 9);
                    }
                    resetPriorityAfterLockedSection();
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void enterSafeMode() {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (!this.mSystemReady) {
                    try {
                        AppGlobals.getPackageManager().enterSafeMode();
                    } catch (RemoteException unused) {
                    }
                }
                this.mSafeMode = true;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void showSafeModeOverlay() {
        View inflate = LayoutInflater.from(this.mContext).inflate(17367301, (ViewGroup) null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = 2015;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 8388691;
        layoutParams.format = inflate.getBackground().getOpacity();
        layoutParams.flags = 24;
        layoutParams.privateFlags |= 16;
        ((WindowManager) this.mContext.getSystemService("window")).addView(inflate, layoutParams);
    }

    public void noteWakeupAlarm(IIntentSender iIntentSender, WorkSource workSource, int i, String str, String str2) {
        if (workSource != null && workSource.isEmpty()) {
            workSource = null;
        }
        if (i <= 0 && workSource == null) {
            if (iIntentSender == null || !(iIntentSender instanceof PendingIntentRecord)) {
                return;
            }
            int callingUid = Binder.getCallingUid();
            int i2 = ((PendingIntentRecord) iIntentSender).uid;
            if (i2 == callingUid) {
                i2 = 1000;
            }
            i = i2;
        }
        int i3 = i;
        this.mBatteryStatsService.noteWakupAlarm(str, i3, workSource, str2);
        if (workSource != null) {
            String packageName = workSource.getPackageName(0);
            int attributionUid = workSource.getAttributionUid();
            if (packageName == null) {
                packageName = str;
            } else {
                i3 = attributionUid;
            }
            FrameworkStatsLog.write(35, workSource, str2, str, this.mUsageStatsService != null ? this.mUsageStatsService.getAppStandbyBucket(packageName, UserHandle.getUserId(i3), SystemClock.elapsedRealtime()) : 0);
            return;
        }
        FrameworkStatsLog.write_non_chained(35, i3, (String) null, str2, str, this.mUsageStatsService != null ? this.mUsageStatsService.getAppStandbyBucket(str, UserHandle.getUserId(i3), SystemClock.elapsedRealtime()) : 0);
    }

    public void noteAlarmStart(IIntentSender iIntentSender, WorkSource workSource, int i, String str) {
        if (workSource != null && workSource.isEmpty()) {
            workSource = null;
        }
        if (i <= 0 && workSource == null) {
            if (iIntentSender == null || !(iIntentSender instanceof PendingIntentRecord)) {
                return;
            }
            int callingUid = Binder.getCallingUid();
            int i2 = ((PendingIntentRecord) iIntentSender).uid;
            if (i2 == callingUid) {
                i2 = 1000;
            }
            i = i2;
        }
        this.mBatteryStatsService.noteAlarmStart(str, workSource, i);
    }

    public void noteAlarmFinish(IIntentSender iIntentSender, WorkSource workSource, int i, String str) {
        if (workSource != null && workSource.isEmpty()) {
            workSource = null;
        }
        if (i <= 0 && workSource == null) {
            if (iIntentSender == null || !(iIntentSender instanceof PendingIntentRecord)) {
                return;
            }
            int callingUid = Binder.getCallingUid();
            int i2 = ((PendingIntentRecord) iIntentSender).uid;
            if (i2 == callingUid) {
                i2 = 1000;
            }
            i = i2;
        }
        this.mBatteryStatsService.noteAlarmFinish(str, workSource, i);
    }

    public boolean killPids(int[] iArr, final String str, boolean z) {
        boolean z2;
        int setAdj;
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("killPids only available to the system");
        }
        if (str == null) {
            str = "Unknown";
        }
        final ArrayList arrayList = new ArrayList();
        synchronized (this.mPidsSelfLocked) {
            int i = 0;
            for (int i2 : iArr) {
                ProcessRecord processRecord = this.mPidsSelfLocked.get(i2);
                if (processRecord != null && (setAdj = processRecord.mState.getSetAdj()) > i) {
                    i = setAdj;
                }
            }
            if (i < 999 && i > 900) {
                i = 900;
            }
            if (!z && i < 500) {
                i = 500;
            }
            Slog.w("ActivityManager", "Killing processes " + str + " at adjustment " + i);
            z2 = false;
            for (int i3 : iArr) {
                ProcessRecord processRecord2 = this.mPidsSelfLocked.get(i3);
                if (processRecord2 != null && processRecord2.mState.getSetAdj() >= i && !processRecord2.isKilledByAm()) {
                    arrayList.add(processRecord2);
                    z2 = true;
                }
            }
        }
        if (!arrayList.isEmpty()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityManagerService.this.lambda$killPids$5(arrayList, str);
                }
            });
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$killPids$5(ArrayList arrayList, String str) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ((ProcessRecord) arrayList.get(i)).killLocked(str, 13, 12, true);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void killUid(int i, int i2, String str) {
        enforceCallingPermission("android.permission.KILL_UID", "killUid");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.killPackageProcessesLSP(null, i, i2, -800, false, true, true, true, false, false, 13, 11, str != null ? str : "kill uid");
                }
                resetPriorityAfterProcLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void killUidForPermissionChange(int i, int i2, String str) {
        enforceCallingPermission("android.permission.KILL_UID", "killUid");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.killPackageProcessesLSP(null, i, i2, -800, false, true, true, true, false, false, 8, 0, str != null ? str : "kill uid");
                }
                resetPriorityAfterProcLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean killProcessesBelowForeground(String str) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("killProcessesBelowForeground() only available to system");
        }
        return killProcessesBelowAdj(0, str);
    }

    public final boolean killProcessesBelowAdj(int i, String str) {
        boolean z;
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("killProcessesBelowAdj() only available to system");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    synchronized (this.mPidsSelfLocked) {
                        int size = this.mPidsSelfLocked.size();
                        z = false;
                        for (int i2 = 0; i2 < size; i2++) {
                            this.mPidsSelfLocked.keyAt(i2);
                            ProcessRecord valueAt = this.mPidsSelfLocked.valueAt(i2);
                            if (valueAt != null && valueAt.mState.getSetAdj() > i && !valueAt.isKilledByAm()) {
                                valueAt.killLocked(str, 8, true);
                                z = true;
                            }
                        }
                    }
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return z;
    }

    public void killProcessesWhenImperceptible(int[] iArr, String str) {
        if (checkCallingPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            throw new SecurityException("Requires permission android.permission.FORCE_STOP_PACKAGES");
        }
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mProcessList.killProcessesWhenImperceptible(iArr, str, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void hang(IBinder iBinder, boolean z) {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.am.ActivityManagerService.11
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        try {
            iBinder.linkToDeath(deathRecipient, 0);
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    Watchdog.getInstance().setAllowRestart(z);
                    Slog.i("ActivityManager", "Hanging system process at request of pid " + Binder.getCallingPid());
                    synchronized (deathRecipient) {
                        while (iBinder.isBinderAlive()) {
                            try {
                                deathRecipient.wait();
                            } catch (InterruptedException unused) {
                            }
                        }
                    }
                    Watchdog.getInstance().setAllowRestart(true);
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
        } catch (RemoteException unused2) {
            Slog.w("ActivityManager", "hang: given caller IBinder is already dead.");
        }
    }

    public void restart() {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        Log.i("ActivityManager", "Sending shutdown broadcast...");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.am.ActivityManagerService.12
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.i("ActivityManager", "Shutting down activity manager...");
                ActivityManagerService.this.shutdown(FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                Log.i("ActivityManager", "Shutdown complete, restarting!");
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        };
        Intent intent = new Intent("android.intent.action.ACTION_SHUTDOWN");
        intent.addFlags(268435456);
        intent.putExtra("android.intent.extra.SHUTDOWN_USERSPACE_ONLY", true);
        broadcastReceiver.onReceive(this.mContext, intent);
    }

    public void performIdleMaintenance() {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                final long uptimeMillis = SystemClock.uptimeMillis();
                final long j = uptimeMillis - this.mLastIdleTime;
                this.mOomAdjuster.mCachedAppOptimizer.compactAllSystem();
                final long lowRamTimeSinceIdleLPr = this.mAppProfiler.getLowRamTimeSinceIdleLPr(uptimeMillis);
                this.mLastIdleTime = uptimeMillis;
                this.mAppProfiler.updateLowRamTimestampLPr(uptimeMillis);
                StringBuilder sb = new StringBuilder(128);
                sb.append("Idle maintenance over ");
                TimeUtils.formatDuration(j, sb);
                sb.append(" low RAM for ");
                TimeUtils.formatDuration(lowRamTimeSinceIdleLPr, sb);
                Slog.i("ActivityManager", sb.toString());
                final boolean z = lowRamTimeSinceIdleLPr > j / 3;
                final long max = Math.max((Process.getTotalMemory() / 1000) / 100, 10000L);
                this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.this.lambda$performIdleMaintenance$7(z, max, j, lowRamTimeSinceIdleLPr, uptimeMillis, (ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performIdleMaintenance$7(boolean z, long j, long j2, long j3, long j4, final ProcessRecord processRecord) {
        final long initialIdlePss;
        final long lastPss;
        long lastSwapPss;
        if (processRecord.getThread() == null) {
            return;
        }
        ProcessProfileRecord processProfileRecord = processRecord.mProfile;
        ProcessStateRecord processStateRecord = processRecord.mState;
        int setProcState = processStateRecord.getSetProcState();
        if (!processStateRecord.isNotCachedSinceIdle()) {
            if (setProcState >= 14 || setProcState < 0) {
                return;
            }
            processStateRecord.setNotCachedSinceIdle(true);
            synchronized (this.mAppProfiler.mProfilerLock) {
                processProfileRecord.setInitialIdlePss(0L);
                this.mAppProfiler.updateNextPssTimeLPf(processStateRecord.getSetProcState(), processRecord.mProfile, j4, true);
            }
        } else if (setProcState < 5 || setProcState > 10) {
        } else {
            synchronized (this.mAppProfiler.mProfilerLock) {
                initialIdlePss = processProfileRecord.getInitialIdlePss();
                lastPss = processProfileRecord.getLastPss();
                lastSwapPss = processProfileRecord.getLastSwapPss();
            }
            if (!z || initialIdlePss == 0 || lastPss <= (3 * initialIdlePss) / 2 || lastPss <= initialIdlePss + j) {
                return;
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append("Kill");
            sb.append(processRecord.processName);
            sb.append(" in idle maint: pss=");
            sb.append(lastPss);
            sb.append(", swapPss=");
            sb.append(lastSwapPss);
            sb.append(", initialPss=");
            sb.append(initialIdlePss);
            sb.append(", period=");
            TimeUtils.formatDuration(j2, sb);
            sb.append(", lowRamPeriod=");
            TimeUtils.formatDuration(j3, sb);
            Slog.wtfQuiet("ActivityManager", sb.toString());
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityManagerService.this.lambda$performIdleMaintenance$6(processRecord, lastPss, initialIdlePss);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performIdleMaintenance$6(ProcessRecord processRecord, long j, long j2) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                processRecord.killLocked("idle maint (pss " + j + " from " + j2 + ")", 13, 6, true);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void sendIdleJobTrigger() {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            broadcastIntent(null, new Intent("com.android.server.ACTION_TRIGGER_IDLE").setPackage(PackageManagerShellCommandDataLoader.PACKAGE).addFlags(1073741824), null, null, 0, null, null, null, -1, null, false, false, -1);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void retrieveSettings() {
        Resources resources;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mActivityTaskManager.retrieveSettings(contentResolver);
        String string = Settings.Global.getString(contentResolver, "debug_app");
        boolean z = Settings.Global.getInt(contentResolver, "wait_for_debugger", 0) != 0;
        boolean z2 = Settings.Global.getInt(contentResolver, "always_finish_activities", 0) != 0;
        this.mHiddenApiBlacklist.registerObserver();
        this.mPlatformCompat.registerContentObserver();
        this.mAppProfiler.retrieveSettings();
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mOrigDebugApp = string;
                this.mDebugApp = string;
                this.mOrigWaitForDebugger = z;
                this.mWaitForDebugger = z;
                this.mAlwaysFinishActivities = z2;
                resources = this.mContext.getResources();
                this.mUserController.setInitialConfig(resources.getBoolean(17891589) ? false : true, resources.getInteger(17694897), resources.getBoolean(17891742));
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        this.mAppErrors.loadAppsNotReportingCrashesFromConfig(resources.getString(17039833));
    }

    public void systemReady(Runnable runnable, TimingsTraceAndSlog timingsTraceAndSlog) {
        ArrayList arrayList;
        timingsTraceAndSlog.traceBegin("PhaseActivityManagerReady");
        this.mSystemServiceManager.preSystemReady();
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (this.mSystemReady) {
                    if (runnable != null) {
                        runnable.run();
                    }
                    timingsTraceAndSlog.traceEnd();
                    return;
                }
                timingsTraceAndSlog.traceBegin("controllersReady");
                this.mLocalDeviceIdleController = (DeviceIdleInternal) LocalServices.getService(DeviceIdleInternal.class);
                this.mActivityTaskManager.onSystemReady();
                this.mUserController.onSystemReady();
                this.mAppOpsService.systemReady();
                this.mProcessList.onSystemReady();
                this.mAppRestrictionController.onSystemReady();
                this.mSystemReady = true;
                timingsTraceAndSlog.traceEnd();
                resetPriorityAfterLockedSection();
                try {
                    sTheRealBuildSerial = IDeviceIdentifiersPolicyService.Stub.asInterface(ServiceManager.getService("device_identifiers")).getSerial();
                } catch (RemoteException unused) {
                }
                timingsTraceAndSlog.traceBegin("killProcesses");
                synchronized (this.mPidsSelfLocked) {
                    arrayList = null;
                    for (int size = this.mPidsSelfLocked.size() - 1; size >= 0; size--) {
                        ProcessRecord valueAt = this.mPidsSelfLocked.valueAt(size);
                        if (!isAllowedWhileBooting(valueAt.info)) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(valueAt);
                        }
                    }
                }
                synchronized (this) {
                    try {
                        boostPriorityForLockedSection();
                        if (arrayList != null) {
                            for (int size2 = arrayList.size() - 1; size2 >= 0; size2 += -1) {
                                ProcessRecord processRecord = (ProcessRecord) arrayList.get(size2);
                                Slog.i("ActivityManager", "Removing system update proc: " + processRecord);
                                this.mProcessList.removeProcessLocked(processRecord, true, false, 13, 8, "system update done");
                            }
                        }
                        this.mProcessesReady = true;
                    } finally {
                        resetPriorityAfterLockedSection();
                    }
                }
                resetPriorityAfterLockedSection();
                timingsTraceAndSlog.traceEnd();
                Slog.i("ActivityManager", "System now ready");
                EventLogTags.writeBootProgressAmsReady(SystemClock.uptimeMillis());
                timingsTraceAndSlog.traceBegin("updateTopComponentForFactoryTest");
                this.mAtmInternal.updateTopComponentForFactoryTest();
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("registerActivityLaunchObserver");
                this.mAtmInternal.getLaunchObserverRegistry().registerLaunchObserver(this.mActivityLaunchObserver);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("watchDeviceProvisioning");
                watchDeviceProvisioning(this.mContext);
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("retrieveSettings");
                retrieveSettings();
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("Ugm.onSystemReady");
                this.mUgmInternal.onSystemReady();
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("updateForceBackgroundCheck");
                PowerManagerInternal powerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                if (powerManagerInternal != null) {
                    powerManagerInternal.registerLowPowerModeObserver(12, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda19
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityManagerService.this.lambda$systemReady$8((PowerSaveState) obj);
                        }
                    });
                    updateForceBackgroundCheck(powerManagerInternal.getLowPowerState(12).batterySaverEnabled);
                } else {
                    Slog.wtf("ActivityManager", "PowerManagerInternal not found.");
                }
                timingsTraceAndSlog.traceEnd();
                if (runnable != null) {
                    runnable.run();
                }
                timingsTraceAndSlog.traceBegin("getCurrentUser");
                int currentUserId = this.mUserController.getCurrentUserId();
                Slog.i("ActivityManager", "Current user:" + currentUserId);
                if (currentUserId != 0 && !this.mUserController.isSystemUserStarted()) {
                    throw new RuntimeException("System user not started while current user is:" + currentUserId);
                }
                timingsTraceAndSlog.traceEnd();
                timingsTraceAndSlog.traceBegin("ActivityManagerStartApps");
                this.mBatteryStatsService.onSystemReady();
                this.mBatteryStatsService.noteEvent(32775, Integer.toString(currentUserId), currentUserId);
                this.mBatteryStatsService.noteEvent(32776, Integer.toString(currentUserId), currentUserId);
                this.mUserController.onSystemUserStarting();
                synchronized (this) {
                    try {
                        boostPriorityForLockedSection();
                        timingsTraceAndSlog.traceBegin("startPersistentApps");
                        startPersistentApps(524288);
                        timingsTraceAndSlog.traceEnd();
                        this.mBooting = true;
                        if (SystemProperties.getBoolean("ro.system_user_home_needed", false)) {
                            timingsTraceAndSlog.traceBegin("enableHomeActivity");
                            try {
                                AppGlobals.getPackageManager().setComponentEnabledSetting(new ComponentName(this.mContext, SystemUserHomeActivity.class), 1, 0, 0);
                                timingsTraceAndSlog.traceEnd();
                            } catch (RemoteException e) {
                                throw e.rethrowAsRuntimeException();
                            }
                        }
                        boolean z = currentUserId == 0;
                        if (z && !UserManager.isHeadlessSystemUserMode()) {
                            timingsTraceAndSlog.traceBegin("startHomeOnAllDisplays");
                            this.mAtmInternal.startHomeOnAllDisplays(currentUserId, "systemReady");
                            timingsTraceAndSlog.traceEnd();
                        }
                        timingsTraceAndSlog.traceBegin("showSystemReadyErrorDialogs");
                        this.mAtmInternal.showSystemReadyErrorDialogsIfNeeded();
                        timingsTraceAndSlog.traceEnd();
                        if (z) {
                            timingsTraceAndSlog.traceBegin("sendUserStartBroadcast");
                            int callingUid = Binder.getCallingUid();
                            int callingPid = Binder.getCallingPid();
                            long clearCallingIdentity = Binder.clearCallingIdentity();
                            this.mUserController.sendUserStartedBroadcast(currentUserId, callingUid, callingPid);
                            this.mUserController.sendUserStartingBroadcast(currentUserId, callingUid, callingPid);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            timingsTraceAndSlog.traceEnd();
                        } else {
                            Slog.i("ActivityManager", "Not sending multi-user broadcasts for non-system user " + currentUserId);
                        }
                        timingsTraceAndSlog.traceBegin("resumeTopActivities");
                        this.mAtmInternal.resumeTopActivities(false);
                        timingsTraceAndSlog.traceEnd();
                        if (z) {
                            timingsTraceAndSlog.traceBegin("sendUserSwitchBroadcasts");
                            this.mUserController.sendUserSwitchBroadcasts(-1, currentUserId);
                            timingsTraceAndSlog.traceEnd();
                        }
                        timingsTraceAndSlog.traceBegin("setBinderProxies");
                        BinderInternal.nSetBinderProxyCountWatermarks(6000, 5500);
                        BinderInternal.nSetBinderProxyCountEnabled(true);
                        BinderInternal.setBinderProxyCountCallback(new BinderInternal.BinderProxyLimitListener() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda20
                            public final void onLimitReached(int i) {
                                ActivityManagerService.this.lambda$systemReady$9(i);
                            }
                        }, this.mHandler);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("componentAlias");
                        this.mComponentAliasResolver.onSystemReady(this.mConstants.mEnableComponentAlias, this.mConstants.mComponentAliasOverrides);
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceEnd();
                    } finally {
                        resetPriorityAfterLockedSection();
                    }
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemReady$8(PowerSaveState powerSaveState) {
        updateForceBackgroundCheck(powerSaveState.batterySaverEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemReady$9(int i) {
        Slog.wtf("ActivityManager", "Uid " + i + " sent too many Binders to uid " + Process.myUid());
        BinderProxy.dumpProxyDebugInfo();
        if (i == 1000) {
            Slog.i("ActivityManager", "Skipping kill (uid is SYSTEM)");
            return;
        }
        killUid(UserHandle.getAppId(i), UserHandle.getUserId(i), "Too many Binders sent to SYSTEM");
        VMRuntime.getRuntime().requestConcurrentGC();
    }

    public final void watchDeviceProvisioning(final Context context) {
        if (isDeviceProvisioned(context)) {
            SystemProperties.set("persist.sys.device_provisioned", "1");
        } else {
            context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.server.am.ActivityManagerService.13
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    if (ActivityManagerService.this.isDeviceProvisioned(context)) {
                        SystemProperties.set("persist.sys.device_provisioned", "1");
                        context.getContentResolver().unregisterContentObserver(this);
                    }
                }
            });
        }
    }

    public final boolean isDeviceProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public final void startBroadcastObservers() {
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.start(this.mContext.getContentResolver());
        }
    }

    public final void updateForceBackgroundCheck(boolean z) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    if (this.mForceBackgroundCheck != z) {
                        this.mForceBackgroundCheck = z;
                        if (z) {
                            this.mProcessList.doStopUidForIdleUidsLocked();
                        }
                    }
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void killAppAtUsersRequest(ProcessRecord processRecord) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mAppErrors.killAppAtUserRequestLocked(processRecord);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void handleApplicationCrash(IBinder iBinder, ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo) {
        String str;
        ProcessRecord findAppProcess = findAppProcess(iBinder, "Crash");
        if (iBinder == null) {
            str = "system_server";
        } else {
            str = findAppProcess == null ? "unknown" : findAppProcess.processName;
        }
        handleApplicationCrashInner("crash", findAppProcess, str, parcelableCrashInfo);
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x01ab  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x01b3  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x01c9  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x01d9  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x01dc  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00c1  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00f6  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0109  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x010f  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x012c  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0144  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0153  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0165  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0169  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0170  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0174  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x017f  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0186  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x018a  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0191  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleApplicationCrashInner(String str, ProcessRecord processRecord, String str2, ApplicationErrorReport.CrashInfo crashInfo) {
        int i;
        int processClassEnum;
        int i2;
        IncrementalMetrics incrementalMetrics = null;
        if (processRecord != null && processRecord.info != null && processRecord.info.packageName != null) {
            IncrementalStatesInfo incrementalStatesInfo = this.mPackageManagerInt.getIncrementalStatesInfo(processRecord.info.packageName, 1000, processRecord.userId);
            r0 = incrementalStatesInfo != null ? incrementalStatesInfo.getProgress() : 1.0f;
            String codePath = processRecord.info.getCodePath();
            if (codePath != null && !codePath.isEmpty() && IncrementalManager.isIncrementalPath(codePath)) {
                Slog.e("ActivityManager", "App crashed on incremental package " + processRecord.info.packageName + " which is " + ((int) (100.0f * r0)) + "% loaded.");
                IBinder service = ServiceManager.getService("incremental");
                if (service != null) {
                    incrementalMetrics = new IncrementalManager(IIncrementalService.Stub.asInterface(service)).getMetrics(codePath);
                }
            }
        }
        IncrementalMetrics incrementalMetrics2 = incrementalMetrics;
        EventLogTags.writeAmCrash(Binder.getCallingPid(), UserHandle.getUserId(Binder.getCallingUid()), str2, processRecord == null ? -1 : processRecord.info.flags, crashInfo.exceptionClassName, crashInfo.exceptionMessage, crashInfo.throwFileName, crashInfo.throwLineNumber);
        if (str2.equals("system_server")) {
            processClassEnum = 3;
        } else if (processRecord != null) {
            processClassEnum = processRecord.getProcessClassEnum();
        } else {
            i = 0;
            int i3 = processRecord == null ? processRecord.uid : -1;
            int pid = processRecord == null ? processRecord.getPid() : -1;
            String str3 = (processRecord != null || processRecord.info == null) ? "" : processRecord.info.packageName;
            int i4 = (processRecord != null || processRecord.info == null) ? 0 : processRecord.info.isInstantApp() ? 2 : 1;
            if (processRecord == null) {
                i2 = processRecord.isInterestingToUserLocked() ? 2 : 1;
            } else {
                i2 = 0;
            }
            FrameworkStatsLog.write(78, i3, str, str2, pid, str3, i4, i2, i, incrementalMetrics2 == null, r0, incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceOldestPendingRead() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getStorageHealthStatusCode() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getDataLoaderStatusCode() : -1, incrementalMetrics2 == null && incrementalMetrics2.getReadLogsEnabled(), incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceLastDataLoaderBind() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getDataLoaderBindDelayMillis() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getTotalDelayedReads() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getTotalFailedReads() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getLastReadErrorUid() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceLastReadError() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getLastReadErrorNumber() : 0, incrementalMetrics2 == null ? incrementalMetrics2.getTotalDelayedReadsDurationMillis() : -1L);
            if (!str.equals("native_crash")) {
                CriticalEventLog.getInstance().logNativeCrash(i, str2, i3, pid);
            } else if (str.equals("crash")) {
                CriticalEventLog.getInstance().logJavaCrash(crashInfo.exceptionClassName, i, str2, i3, pid);
            }
            String relaunchReasonToString = ActivityTaskManagerService.relaunchReasonToString(processRecord != null ? processRecord.getWindowProcessController().computeRelaunchReason() : 0);
            if (crashInfo.crashTag != null) {
                crashInfo.crashTag = relaunchReasonToString;
            } else {
                crashInfo.crashTag += " " + relaunchReasonToString;
            }
            addErrorToDropBox(str, processRecord, str2, null, null, null, null, null, null, crashInfo, new Float(r0), incrementalMetrics2, null);
            this.mAppErrors.crashApplication(processRecord, crashInfo);
        }
        i = processClassEnum;
        if (processRecord == null) {
        }
        if (processRecord == null) {
        }
        String str32 = (processRecord != null || processRecord.info == null) ? "" : processRecord.info.packageName;
        if (processRecord != null) {
        }
        if (processRecord == null) {
        }
        FrameworkStatsLog.write(78, i3, str, str2, pid, str32, i4, i2, i, incrementalMetrics2 == null, r0, incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceOldestPendingRead() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getStorageHealthStatusCode() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getDataLoaderStatusCode() : -1, incrementalMetrics2 == null && incrementalMetrics2.getReadLogsEnabled(), incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceLastDataLoaderBind() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getDataLoaderBindDelayMillis() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getTotalDelayedReads() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getTotalFailedReads() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getLastReadErrorUid() : -1, incrementalMetrics2 == null ? incrementalMetrics2.getMillisSinceLastReadError() : -1L, incrementalMetrics2 == null ? incrementalMetrics2.getLastReadErrorNumber() : 0, incrementalMetrics2 == null ? incrementalMetrics2.getTotalDelayedReadsDurationMillis() : -1L);
        if (!str.equals("native_crash")) {
        }
        String relaunchReasonToString2 = ActivityTaskManagerService.relaunchReasonToString(processRecord != null ? processRecord.getWindowProcessController().computeRelaunchReason() : 0);
        if (crashInfo.crashTag != null) {
        }
        addErrorToDropBox(str, processRecord, str2, null, null, null, null, null, null, crashInfo, new Float(r0), incrementalMetrics2, null);
        this.mAppErrors.crashApplication(processRecord, crashInfo);
    }

    public void handleApplicationStrictModeViolation(IBinder iBinder, int i, StrictMode.ViolationInfo violationInfo) {
        boolean z;
        ProcessRecord findAppProcess = findAppProcess(iBinder, "StrictMode");
        if ((67108864 & i) != 0) {
            Integer valueOf = Integer.valueOf(violationInfo.hashCode());
            synchronized (this.mAlreadyLoggedViolatedStacks) {
                if (this.mAlreadyLoggedViolatedStacks.contains(valueOf)) {
                    z = false;
                } else {
                    if (this.mAlreadyLoggedViolatedStacks.size() >= 5000) {
                        this.mAlreadyLoggedViolatedStacks.clear();
                    }
                    this.mAlreadyLoggedViolatedStacks.add(valueOf);
                    z = true;
                }
            }
            if (z) {
                logStrictModeViolationToDropBox(findAppProcess, violationInfo);
            }
        }
        if ((i & 536870912) != 0) {
            AppErrorResult appErrorResult = new AppErrorResult();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Message obtain = Message.obtain();
                obtain.what = 26;
                HashMap hashMap = new HashMap();
                hashMap.put("result", appErrorResult);
                hashMap.put("app", findAppProcess);
                hashMap.put("info", violationInfo);
                obtain.obj = hashMap;
                this.mUiHandler.sendMessage(obtain);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                int i2 = appErrorResult.get();
                Slog.w("ActivityManager", "handleApplicationStrictModeViolation; res=" + i2);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
    }

    public void registerStrictModeCallback(IBinder iBinder) {
        final int callingPid = Binder.getCallingPid();
        this.mStrictModeCallbacks.put(callingPid, IUnsafeIntentStrictModeCallback.Stub.asInterface(iBinder));
        try {
            iBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.am.ActivityManagerService.14
                @Override // android.os.IBinder.DeathRecipient
                public void binderDied() {
                    ActivityManagerService.this.mStrictModeCallbacks.remove(callingPid);
                }
            }, 0);
        } catch (RemoteException unused) {
            this.mStrictModeCallbacks.remove(callingPid);
        }
    }

    public final void logStrictModeViolationToDropBox(ProcessRecord processRecord, StrictMode.ViolationInfo violationInfo) {
        if (violationInfo == null) {
            return;
        }
        boolean z = processRecord == null || (processRecord.info.flags & 129) != 0;
        String str = processRecord == null ? "unknown" : processRecord.processName;
        final DropBoxManager dropBoxManager = (DropBoxManager) this.mContext.getSystemService("dropbox");
        final String str2 = processClass(processRecord) + "_strictmode";
        if (dropBoxManager == null || !dropBoxManager.isTagEnabled(str2)) {
            return;
        }
        StringBuilder sb = new StringBuilder(1024);
        synchronized (sb) {
            appendDropBoxProcessHeaders(processRecord, str, sb);
            sb.append("Build: ");
            sb.append(Build.FINGERPRINT);
            sb.append("\n");
            sb.append("System-App: ");
            sb.append(z);
            sb.append("\n");
            sb.append("Uptime-Millis: ");
            sb.append(violationInfo.violationUptimeMillis);
            sb.append("\n");
            if (violationInfo.violationNumThisLoop != 0) {
                sb.append("Loop-Violation-Number: ");
                sb.append(violationInfo.violationNumThisLoop);
                sb.append("\n");
            }
            if (violationInfo.numAnimationsRunning != 0) {
                sb.append("Animations-Running: ");
                sb.append(violationInfo.numAnimationsRunning);
                sb.append("\n");
            }
            if (violationInfo.broadcastIntentAction != null) {
                sb.append("Broadcast-Intent-Action: ");
                sb.append(violationInfo.broadcastIntentAction);
                sb.append("\n");
            }
            if (violationInfo.durationMillis != -1) {
                sb.append("Duration-Millis: ");
                sb.append(violationInfo.durationMillis);
                sb.append("\n");
            }
            if (violationInfo.numInstances != -1) {
                sb.append("Instance-Count: ");
                sb.append(violationInfo.numInstances);
                sb.append("\n");
            }
            String[] strArr = violationInfo.tags;
            if (strArr != null) {
                for (String str3 : strArr) {
                    sb.append("Span-Tag: ");
                    sb.append(str3);
                    sb.append("\n");
                }
            }
            sb.append("\n");
            sb.append(violationInfo.getStackTrace());
            sb.append("\n");
            if (violationInfo.getViolationDetails() != null) {
                sb.append(violationInfo.getViolationDetails());
                sb.append("\n");
            }
        }
        final String sb2 = sb.toString();
        IoThread.getHandler().post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                dropBoxManager.addText(str2, sb2);
            }
        });
    }

    public boolean handleApplicationWtf(final IBinder iBinder, final String str, boolean z, final ApplicationErrorReport.ParcelableCrashInfo parcelableCrashInfo, int i) {
        final int callingUid = Binder.getCallingUid();
        final int callingPid = Binder.getCallingPid();
        Preconditions.checkNotNull(parcelableCrashInfo);
        if (z || i == Process.myPid()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService.15
                @Override // java.lang.Runnable
                public void run() {
                    ActivityManagerService.this.handleApplicationWtfInner(callingUid, callingPid, iBinder, str, parcelableCrashInfo);
                }
            });
            return false;
        }
        ProcessRecord handleApplicationWtfInner = handleApplicationWtfInner(callingUid, callingPid, iBinder, str, parcelableCrashInfo);
        boolean z2 = Build.IS_ENG || Settings.Global.getInt(this.mContext.getContentResolver(), "wtf_is_fatal", 0) != 0;
        boolean z3 = handleApplicationWtfInner == null || handleApplicationWtfInner.isPersistent();
        if (!z2 || z3) {
            return false;
        }
        this.mAppErrors.crashApplication(handleApplicationWtfInner, parcelableCrashInfo);
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0023  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x002d  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x003c  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0041  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ProcessRecord handleApplicationWtfInner(int i, int i2, IBinder iBinder, String str, ApplicationErrorReport.CrashInfo crashInfo) {
        String str2;
        String str3;
        ProcessRecord findAppProcess = findAppProcess(iBinder, "WTF");
        if (iBinder == null) {
            str2 = "system_server";
        } else if (findAppProcess != null) {
            str2 = findAppProcess.processName;
        } else {
            str3 = "unknown";
            EventLogTags.writeAmWtf(UserHandle.getUserId(i), i2, str3, findAppProcess != null ? -1 : findAppProcess.info.flags, str, crashInfo != null ? crashInfo.exceptionMessage : "unknown");
            FrameworkStatsLog.write(80, i, str, str3, i2, findAppProcess == null ? findAppProcess.getProcessClassEnum() : 0);
            addErrorToDropBox("wtf", findAppProcess, str3, null, null, null, str, null, null, crashInfo, null, null, null);
            return findAppProcess;
        }
        str3 = str2;
        EventLogTags.writeAmWtf(UserHandle.getUserId(i), i2, str3, findAppProcess != null ? -1 : findAppProcess.info.flags, str, crashInfo != null ? crashInfo.exceptionMessage : "unknown");
        FrameworkStatsLog.write(80, i, str, str3, i2, findAppProcess == null ? findAppProcess.getProcessClassEnum() : 0);
        addErrorToDropBox("wtf", findAppProcess, str3, null, null, null, str, null, null, crashInfo, null, null, null);
        return findAppProcess;
    }

    public void schedulePendingSystemServerWtfs(final LinkedList<Pair<String, ApplicationErrorReport.CrashInfo>> linkedList) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$schedulePendingSystemServerWtfs$11(linkedList);
            }
        });
    }

    /* renamed from: handlePendingSystemServerWtfs */
    public final void lambda$schedulePendingSystemServerWtfs$11(LinkedList<Pair<String, ApplicationErrorReport.CrashInfo>> linkedList) {
        ProcessRecord processRecord;
        synchronized (this.mPidsSelfLocked) {
            processRecord = this.mPidsSelfLocked.get(MY_PID);
        }
        Pair<String, ApplicationErrorReport.CrashInfo> poll = linkedList.poll();
        while (poll != null) {
            addErrorToDropBox("wtf", processRecord, "system_server", null, null, null, (String) poll.first, null, null, (ApplicationErrorReport.CrashInfo) poll.second, null, null, null);
            poll = linkedList.poll();
        }
    }

    public final ProcessRecord findAppProcess(IBinder iBinder, String str) {
        ProcessRecord findAppProcessLOSP;
        if (iBinder == null) {
            return null;
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                findAppProcessLOSP = this.mProcessList.findAppProcessLOSP(iBinder, str);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return findAppProcessLOSP;
    }

    public void appendDropBoxProcessHeaders(ProcessRecord processRecord, String str, final StringBuilder sb) {
        if (processRecord == null) {
            sb.append("Process: ");
            sb.append(str);
            sb.append("\n");
            return;
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                sb.append("Process: ");
                sb.append(str);
                sb.append("\n");
                sb.append("PID: ");
                sb.append(processRecord.getPid());
                sb.append("\n");
                sb.append("UID: ");
                sb.append(processRecord.uid);
                sb.append("\n");
                if (processRecord.mOptRecord != null) {
                    sb.append("Frozen: ");
                    sb.append(processRecord.mOptRecord.isFrozen());
                    sb.append("\n");
                }
                int i = processRecord.info.flags;
                final IPackageManager packageManager = AppGlobals.getPackageManager();
                sb.append("Flags: 0x");
                sb.append(Integer.toHexString(i));
                sb.append("\n");
                final int callingUserId = UserHandle.getCallingUserId();
                processRecord.getPkgList().forEachPackage(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda14
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.lambda$appendDropBoxProcessHeaders$12(sb, packageManager, callingUserId, (String) obj);
                    }
                });
                if (processRecord.info.isInstantApp()) {
                    sb.append("Instant-App: true\n");
                }
                if (processRecord.isSdkSandbox) {
                    String str2 = processRecord.sdkSandboxClientAppPackage;
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(str2, 1024L, callingUserId);
                        if (packageInfo != null) {
                            appendSdkSandboxClientPackageHeader(sb, packageInfo);
                            appendSdkSandboxLibraryHeaders(sb, packageInfo);
                        } else {
                            Slog.e("ActivityManager", "PackageInfo is null for SDK sandbox client: " + str2);
                        }
                    } catch (RemoteException e) {
                        Slog.e("ActivityManager", "Error getting package info for SDK sandbox client: " + str2, e);
                    }
                    sb.append("SdkSandbox: true\n");
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public static /* synthetic */ void lambda$appendDropBoxProcessHeaders$12(StringBuilder sb, IPackageManager iPackageManager, int i, String str) {
        sb.append("Package: ");
        sb.append(str);
        try {
            PackageInfo packageInfo = iPackageManager.getPackageInfo(str, 0L, i);
            if (packageInfo != null) {
                sb.append(" v");
                sb.append(packageInfo.getLongVersionCode());
                if (packageInfo.versionName != null) {
                    sb.append(" (");
                    sb.append(packageInfo.versionName);
                    sb.append(")");
                }
            }
        } catch (RemoteException e) {
            Slog.e("ActivityManager", "Error getting package info: " + str, e);
        }
        sb.append("\n");
    }

    public final void appendSdkSandboxClientPackageHeader(StringBuilder sb, PackageInfo packageInfo) {
        sb.append("SdkSandbox-Client-Package: ");
        sb.append(packageInfo.packageName);
        sb.append(" v");
        sb.append(packageInfo.getLongVersionCode());
        if (packageInfo.versionName != null) {
            sb.append(" (");
            sb.append(packageInfo.versionName);
            sb.append(")");
        }
        sb.append("\n");
    }

    public final void appendSdkSandboxLibraryHeaders(StringBuilder sb, PackageInfo packageInfo) {
        List sharedLibraryInfos = packageInfo.applicationInfo.getSharedLibraryInfos();
        int size = sharedLibraryInfos.size();
        for (int i = 0; i < size; i++) {
            SharedLibraryInfo sharedLibraryInfo = (SharedLibraryInfo) sharedLibraryInfos.get(i);
            if (sharedLibraryInfo.isSdk()) {
                sb.append("SdkSandbox-Library: ");
                sb.append(sharedLibraryInfo.getPackageName());
                VersionedPackage declaringPackage = sharedLibraryInfo.getDeclaringPackage();
                sb.append(" v");
                sb.append(declaringPackage.getLongVersionCode());
                sb.append("\n");
            }
        }
    }

    public static String processClass(ProcessRecord processRecord) {
        return (processRecord == null || processRecord.getPid() == MY_PID) ? "system_server" : (processRecord.info.isSystemApp() || processRecord.info.isSystemExt()) ? "system_app" : "data_app";
    }

    public void addErrorToDropBox(String str, ProcessRecord processRecord, String str2, String str3, String str4, ProcessRecord processRecord2, String str5, final String str6, final File file, final ApplicationErrorReport.CrashInfo crashInfo, Float f, IncrementalMetrics incrementalMetrics, UUID uuid) {
        String str7;
        String str8;
        try {
            final DropBoxManager dropBoxManager = (DropBoxManager) this.mContext.getSystemService(DropBoxManager.class);
            final String str9 = processClass(processRecord) + "_" + str;
            if (dropBoxManager == null || !dropBoxManager.isTagEnabled(str9)) {
                return;
            }
            DropboxRateLimiter.RateLimitResult shouldRateLimit = this.mDropboxRateLimiter.shouldRateLimit(str, str2);
            if (shouldRateLimit.shouldRateLimit()) {
                return;
            }
            final StringBuilder sb = new StringBuilder(1024);
            appendDropBoxProcessHeaders(processRecord, str2, sb);
            if (processRecord != null) {
                sb.append("Foreground: ");
                sb.append(processRecord.isInterestingToUserLocked() ? "Yes" : "No");
                sb.append("\n");
                if (processRecord.getStartUptime() > 0) {
                    sb.append("Process-Runtime: ");
                    sb.append(SystemClock.uptimeMillis() - processRecord.getStartUptime());
                    sb.append("\n");
                }
            }
            if (str3 != null) {
                sb.append("Activity: ");
                sb.append(str3);
                sb.append("\n");
            }
            if (str4 != null) {
                if (processRecord2 != null && processRecord2.getPid() != processRecord.getPid()) {
                    sb.append("Parent-Process: ");
                    sb.append(processRecord2.processName);
                    sb.append("\n");
                }
                if (!str4.equals(str3)) {
                    sb.append("Parent-Activity: ");
                    sb.append(str4);
                    sb.append("\n");
                }
            }
            if (str5 != null) {
                sb.append("Subject: ");
                sb.append(str5);
                sb.append("\n");
            }
            if (uuid != null) {
                sb.append("ErrorId: ");
                sb.append(uuid.toString());
                sb.append("\n");
            }
            sb.append("Build: ");
            sb.append(Build.FINGERPRINT);
            sb.append("\n");
            if (Debug.isDebuggerConnected()) {
                sb.append("Debugger: Connected\n");
            }
            if (crashInfo != null && (str8 = crashInfo.exceptionHandlerClassName) != null && !str8.isEmpty()) {
                sb.append("Crash-Handler: ");
                sb.append(crashInfo.exceptionHandlerClassName);
                sb.append("\n");
            }
            if (crashInfo != null && (str7 = crashInfo.crashTag) != null && !str7.isEmpty()) {
                sb.append("Crash-Tag: ");
                sb.append(crashInfo.crashTag);
                sb.append("\n");
            }
            if (f != null) {
                sb.append("Loading-Progress: ");
                sb.append(f.floatValue());
                sb.append("\n");
            }
            if (incrementalMetrics != null) {
                sb.append("Incremental: Yes");
                sb.append("\n");
                long millisSinceOldestPendingRead = incrementalMetrics.getMillisSinceOldestPendingRead();
                if (millisSinceOldestPendingRead > 0) {
                    sb.append("Millis-Since-Oldest-Pending-Read: ");
                    sb.append(millisSinceOldestPendingRead);
                    sb.append("\n");
                }
            }
            sb.append(shouldRateLimit.createHeader());
            sb.append("\n");
            final boolean z = processRecord == null;
            Thread thread = new Thread("Error dump: " + str9) { // from class: com.android.server.am.ActivityManagerService.16
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    InputStreamReader inputStreamReader;
                    String str10;
                    String str11 = str6;
                    if (str11 != null) {
                        sb.append(str11);
                    }
                    int i = Settings.Global.getInt(ActivityManagerService.this.mContext.getContentResolver(), "logcat_for_" + str9, 0);
                    int i2 = (Settings.Global.getInt(ActivityManagerService.this.mContext.getContentResolver(), "max_error_bytes_for_" + str9, 196608) - sb.length()) - (i * 100);
                    File file2 = file;
                    if (file2 != null && i2 > 0) {
                        try {
                            sb.append(FileUtils.readTextFile(file2, i2, "\n\n[[TRUNCATED]]"));
                        } catch (IOException e) {
                            Slog.e("ActivityManager", "Error reading " + file, e);
                        }
                    }
                    ApplicationErrorReport.CrashInfo crashInfo2 = crashInfo;
                    if (crashInfo2 != null && (str10 = crashInfo2.stackTrace) != null) {
                        sb.append(str10);
                    }
                    if (i > 0 && !z) {
                        sb.append("\n");
                        InputStreamReader inputStreamReader2 = null;
                        try {
                            try {
                                try {
                                    Process start = new ProcessBuilder("/system/bin/timeout", "-i", "-s", "SEGV", "10s", "/system/bin/logcat", "-v", "threadtime", "-b", "events", "-b", "system", "-b", "main", "-b", "crash", "-t", String.valueOf(i)).redirectErrorStream(true).start();
                                    try {
                                        start.getOutputStream().close();
                                    } catch (IOException unused) {
                                    }
                                    try {
                                        start.getErrorStream().close();
                                    } catch (IOException unused2) {
                                    }
                                    inputStreamReader = new InputStreamReader(start.getInputStream());
                                } catch (IOException unused3) {
                                }
                            } catch (IOException e2) {
                                e = e2;
                            }
                        } catch (Throwable th) {
                            th = th;
                        }
                        try {
                            char[] cArr = new char[IInstalld.FLAG_FORCE];
                            while (true) {
                                int read = inputStreamReader.read(cArr);
                                if (read <= 0) {
                                    break;
                                }
                                sb.append(cArr, 0, read);
                            }
                            inputStreamReader.close();
                        } catch (IOException e3) {
                            e = e3;
                            inputStreamReader2 = inputStreamReader;
                            Slog.e("ActivityManager", "Error running logcat", e);
                            if (inputStreamReader2 != null) {
                                inputStreamReader2.close();
                            }
                            dropBoxManager.addText(str9, sb.toString());
                        } catch (Throwable th2) {
                            th = th2;
                            inputStreamReader2 = inputStreamReader;
                            if (inputStreamReader2 != null) {
                                try {
                                    inputStreamReader2.close();
                                } catch (IOException unused4) {
                                }
                            }
                            throw th;
                        }
                    }
                    dropBoxManager.addText(str9, sb.toString());
                }
            };
            if (z) {
                int allowThreadDiskWritesMask = StrictMode.allowThreadDiskWritesMask();
                try {
                    thread.run();
                    return;
                } finally {
                    StrictMode.setThreadPolicyMask(allowThreadDiskWritesMask);
                }
            }
            thread.start();
        } catch (Exception unused) {
        }
    }

    public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() {
        enforceNotIsolatedCaller("getProcessesInErrorState");
        final List<ActivityManager.ProcessErrorStateInfo>[] listArr = new List[1];
        final int callingUid = Binder.getCallingUid();
        final boolean z = ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid) == 0;
        final int userId = UserHandle.getUserId(callingUid);
        final boolean z2 = ActivityManager.checkUidPermission("android.permission.DUMP", callingUid) == 0;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda9
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.lambda$getProcessesInErrorState$13(z, userId, z2, callingUid, listArr, (ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return listArr[0];
    }

    public static /* synthetic */ void lambda$getProcessesInErrorState$13(boolean z, int i, boolean z2, int i2, List[] listArr, ProcessRecord processRecord) {
        ActivityManager.ProcessErrorStateInfo notRespondingReport;
        if (z || processRecord.userId == i) {
            if (z2 || processRecord.info.uid == i2) {
                ProcessErrorStateRecord processErrorStateRecord = processRecord.mErrorState;
                boolean isCrashing = processErrorStateRecord.isCrashing();
                boolean isNotResponding = processErrorStateRecord.isNotResponding();
                if (processRecord.getThread() != null) {
                    if (isCrashing || isNotResponding) {
                        if (isCrashing) {
                            notRespondingReport = processErrorStateRecord.getCrashingReport();
                        } else {
                            notRespondingReport = isNotResponding ? processErrorStateRecord.getNotRespondingReport() : null;
                        }
                        if (notRespondingReport != null) {
                            if (listArr[0] == null) {
                                listArr[0] = new ArrayList(1);
                            }
                            listArr[0].add(notRespondingReport);
                            return;
                        }
                        Slog.w("ActivityManager", "Missing app error report, app = " + processRecord.processName + " crashing = " + isCrashing + " notResponding = " + isNotResponding);
                    }
                }
            }
        }
    }

    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesLOSP;
        enforceNotIsolatedCaller("getRunningAppProcesses");
        int callingUid = Binder.getCallingUid();
        int uidTargetSdkVersion = this.mPackageManagerInt.getUidTargetSdkVersion(callingUid);
        boolean z = ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid) == 0;
        int userId = UserHandle.getUserId(callingUid);
        boolean isGetTasksAllowed = this.mAtmInternal.isGetTasksAllowed("getRunningAppProcesses", Binder.getCallingPid(), callingUid);
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                runningAppProcessesLOSP = this.mProcessList.getRunningAppProcessesLOSP(z, userId, isGetTasksAllowed, callingUid, uidTargetSdkVersion);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return runningAppProcessesLOSP;
    }

    public List<ApplicationInfo> getRunningExternalApplications() {
        enforceNotIsolatedCaller("getRunningExternalApplications");
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses();
        ArrayList arrayList = new ArrayList();
        if (runningAppProcesses != null && runningAppProcesses.size() > 0) {
            HashSet<String> hashSet = new HashSet();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                String[] strArr = runningAppProcessInfo.pkgList;
                if (strArr != null) {
                    for (String str : strArr) {
                        hashSet.add(str);
                    }
                }
            }
            IPackageManager packageManager = AppGlobals.getPackageManager();
            for (String str2 : hashSet) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str2, 0L, UserHandle.getCallingUserId());
                    if ((applicationInfo.flags & 262144) != 0) {
                        arrayList.add(applicationInfo);
                    }
                } catch (RemoteException unused) {
                }
            }
        }
        return arrayList;
    }

    public ParceledListSlice<ApplicationStartInfo> getHistoricalProcessStartReasons(String str, int i, int i2) {
        if (!this.mConstants.mFlagApplicationStartInfoEnabled) {
            return new ParceledListSlice<>(new ArrayList());
        }
        enforceNotIsolatedCaller("getHistoricalProcessStartReasons");
        return new ParceledListSlice<>(new ArrayList());
    }

    public void setApplicationStartInfoCompleteListener(IApplicationStartInfoCompleteListener iApplicationStartInfoCompleteListener, int i) {
        if (this.mConstants.mFlagApplicationStartInfoEnabled) {
            enforceNotIsolatedCaller("setApplicationStartInfoCompleteListener");
        }
    }

    public void removeApplicationStartInfoCompleteListener(int i) {
        if (this.mConstants.mFlagApplicationStartInfoEnabled) {
            enforceNotIsolatedCaller("removeApplicationStartInfoCompleteListener");
        }
    }

    public ParceledListSlice<ApplicationExitInfo> getHistoricalProcessExitReasons(String str, int i, int i2, int i3) {
        enforceNotIsolatedCaller("getHistoricalProcessExitReasons");
        if (i3 == -1 || i3 == -2) {
            throw new IllegalArgumentException("Unsupported userId");
        }
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        UserHandle.getCallingUserId();
        this.mUserController.handleIncomingUser(callingPid, callingUid, i3, true, 0, "getHistoricalProcessExitReasons", null);
        NativeTombstoneManager nativeTombstoneManager = (NativeTombstoneManager) LocalServices.getService(NativeTombstoneManager.class);
        ArrayList<ApplicationExitInfo> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            int enforceDumpPermissionForPackage = enforceDumpPermissionForPackage(str, i3, callingUid, "getHistoricalProcessExitReasons");
            if (enforceDumpPermissionForPackage != -1) {
                this.mProcessList.mAppExitInfoTracker.getExitInfo(str, enforceDumpPermissionForPackage, i, i2, arrayList);
                nativeTombstoneManager.collectTombstones(arrayList, enforceDumpPermissionForPackage, i, i2);
            }
        } else {
            this.mProcessList.mAppExitInfoTracker.getExitInfo(str, callingUid, i, i2, arrayList);
            nativeTombstoneManager.collectTombstones(arrayList, callingUid, i, i2);
        }
        return new ParceledListSlice<>(arrayList);
    }

    public void setProcessStateSummary(byte[] bArr) {
        if (bArr != null && bArr.length > 128) {
            throw new IllegalArgumentException("Data size is too large");
        }
        this.mProcessList.mAppExitInfoTracker.setProcessStateSummary(Binder.getCallingUid(), Binder.getCallingPid(), bArr);
    }

    public int enforceDumpPermissionForPackage(String str, int i, int i2, String str2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int packageUid = this.mPackageManagerInt.getPackageUid(str, 786432L, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            if (UserHandle.getAppId(packageUid) != UserHandle.getAppId(i2)) {
                enforceCallingPermission("android.permission.DUMP", str2);
            }
            return packageUid;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void getMyMemoryState(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
        ProcessRecord processRecord;
        if (runningAppProcessInfo == null) {
            throw new IllegalArgumentException("outState is null");
        }
        enforceNotIsolatedCaller("getMyMemoryState");
        int uidTargetSdkVersion = this.mPackageManagerInt.getUidTargetSdkVersion(Binder.getCallingUid());
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                synchronized (this.mPidsSelfLocked) {
                    processRecord = this.mPidsSelfLocked.get(Binder.getCallingPid());
                }
                if (processRecord != null) {
                    this.mProcessList.fillInProcMemInfoLOSP(processRecord, runningAppProcessInfo, uidTargetSdkVersion);
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public int getMemoryTrimLevel() {
        int lastMemoryLevelLocked;
        enforceNotIsolatedCaller("getMyMemoryState");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                lastMemoryLevelLocked = this.mAppProfiler.getLastMemoryLevelLocked();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return lastMemoryLevelLocked;
    }

    public void setMemFactorOverride(int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (i == this.mAppProfiler.getLastMemoryLevelLocked()) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mAppProfiler.setMemFactorOverrideLocked(i);
                updateOomAdjLocked(0);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setServiceRestartBackoffEnabled(String str, boolean z, String str2) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.setServiceRestartBackoffEnabledLocked(str, z, str2);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean isServiceRestartBackoffEnabled(String str) {
        boolean isServiceRestartBackoffEnabledLocked;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                isServiceRestartBackoffEnabledLocked = this.mServices.isServiceRestartBackoffEnabledLocked(str);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return isServiceRestartBackoffEnabledLocked;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new ActivityManagerShellCommand(this, false).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PriorityDump.dump(this.mPriorityDumper, fileDescriptor, printWriter, strArr);
    }

    public final void dumpEverything(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str, int i2, boolean z2, boolean z3, int i3, boolean z4) {
        ActiveServices.ServiceDumper newServiceDumperLocked;
        String str2;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mConstants.dump(printWriter);
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mOomAdjuster.dumpCachedAppOptimizerSettings(printWriter);
                }
                resetPriorityAfterProcLockedSection();
                this.mOomAdjuster.dumpCacheOomRankerSettings(printWriter);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                dumpAllowedAssociationsLocked(fileDescriptor, printWriter, strArr, i, z, str);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                this.mPendingIntentController.dumpPendingIntents(printWriter, z, str);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                dumpBroadcastsLocked(fileDescriptor, printWriter, strArr, i, z, str);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                if (z || str != null) {
                    dumpBroadcastStatsLocked(fileDescriptor, printWriter, strArr, i, z, str);
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                }
                this.mCpHelper.dumpProvidersLocked(fileDescriptor, printWriter, strArr, i, z, str);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                dumpPermissions(fileDescriptor, printWriter, strArr, i, z, str);
                printWriter.println();
                newServiceDumperLocked = this.mServices.newServiceDumperLocked(fileDescriptor, printWriter, strArr, i, z, str);
                if (!z2) {
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    newServiceDumperLocked.dumpLocked();
                }
            } finally {
            }
        }
        resetPriorityAfterLockedSection();
        if (z2) {
            if (z) {
                printWriter.println("-------------------------------------------------------------------------------");
            }
            newServiceDumperLocked.dumpWithClient();
        }
        if (str == null && z4) {
            printWriter.println();
            if (z) {
                printWriter.println("-------------------------------------------------------------------------------");
            }
            dumpBinderProxies(printWriter, 6000);
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                this.mAtmInternal.dump("recents", fileDescriptor, printWriter, strArr, i, z, z2, str, i2);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                this.mAtmInternal.dump("lastanr", fileDescriptor, printWriter, strArr, i, z, z2, str, i2);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                this.mAtmInternal.dump("starter", fileDescriptor, printWriter, strArr, i, z, z2, str, i2);
                if (str == null) {
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mAtmInternal.dump("containers", fileDescriptor, printWriter, strArr, i, z, z2, str, i2);
                }
                if (!z3) {
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mAtmInternal.dump("activities", fileDescriptor, printWriter, strArr, i, z, z2, str, i2);
                }
                if (this.mAssociations.size() > 0) {
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    str2 = str;
                    dumpAssociationsLocked(fileDescriptor, printWriter, strArr, i, z, z2, str);
                } else {
                    str2 = str;
                }
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                    this.mProcessList.mAppExitInfoTracker.dumpHistoryProcessExitInfo(printWriter, str2);
                }
                if (str2 == null) {
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mOomAdjProfiler.dump(printWriter);
                    printWriter.println();
                    if (z) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpLmkLocked(printWriter);
                }
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.dumpProcessesLSP(fileDescriptor, printWriter, strArr, i, z, str, i3);
                }
                resetPriorityAfterProcLockedSection();
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                dumpUsers(printWriter);
                printWriter.println();
                if (z) {
                    printWriter.println("-------------------------------------------------------------------------------");
                }
                this.mComponentAliasResolver.dump(printWriter);
            } finally {
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void dumpAppRestrictionController(PrintWriter printWriter) {
        printWriter.println("-------------------------------------------------------------------------------");
        this.mAppRestrictionController.dump(printWriter, "");
    }

    public void dumpAppRestrictionController(ProtoOutputStream protoOutputStream, int i) {
        this.mAppRestrictionController.dumpAsProto(protoOutputStream, i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:101:0x01a6, code lost:
        if ("service".equals(r7) == false) goto L135;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x01a8, code lost:
        r30.mServices.dumpDebug(r4, 1146756268033L);
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x01b6, code lost:
        if ("processes".equals(r7) != false) goto L173;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x01bf, code lost:
        if ("p".equals(r7) == false) goto L139;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x01c9, code lost:
        if ("app-restrictions".equals(r7) == false) goto L159;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x01cb, code lost:
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x01cd, code lost:
        if (r0 >= r15.length) goto L158;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x01d7, code lost:
        if ("--uid".equals(r15[r0]) == false) goto L146;
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x01d9, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x01db, code lost:
        if (r0 >= r15.length) goto L150;
     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x01dd, code lost:
        r1 = java.lang.Integer.parseInt(r15[r0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x01e4, code lost:
        r11 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x01e6, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x01e9, code lost:
        if (r11 != 0) goto L152;
     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x01eb, code lost:
        r32.println("Invalid --uid argument");
        r32.println("Use -h for help.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x01f7, code lost:
        dumpAppRestrictionController(r4, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x01fc, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:0x01fd, code lost:
        boostPriorityForLockedSection();
        r0 = r4.start(1146756268033L);
        r30.mAtmInternal.writeActivitiesToProto(r4);
        r4.end(r0);
        r0 = r4.start(1146756268034L);
        writeBroadcastsToProtoLocked(r4);
        r4.end(r0);
        r0 = r4.start(1146756268035L);
        r30.mServices.dumpDebug(r4, 1146756268033L);
        r4.end(r0);
        r0 = r4.start(1146756268036L);
        r2 = r30.mProcLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:126:0x0237, code lost:
        monitor-enter(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:127:0x0238, code lost:
        boostPriorityForProcLockedSection();
        r30.mProcessList.writeProcessesToProtoLSP(r4, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x0240, code lost:
        monitor-exit(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x0241, code lost:
        resetPriorityAfterProcLockedSection();
        r4.end(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:130:0x0247, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x0248, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x0252, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x0257, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x0259, code lost:
        if (r2 >= r15.length) goto L176;
     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x025b, code lost:
        r6 = r15[r2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x025d, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x025e, code lost:
        boostPriorityForLockedSection();
        r1 = r30.mProcLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:145:0x0263, code lost:
        monitor-enter(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0264, code lost:
        boostPriorityForProcLockedSection();
        r30.mProcessList.writeProcessesToProtoLSP(r4, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x026c, code lost:
        monitor-exit(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x026d, code lost:
        resetPriorityAfterProcLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x0270, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x0271, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x027b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0280, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0281, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0282, code lost:
        boostPriorityForLockedSection();
        writeBroadcastsToProtoLocked(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x0288, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x0289, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:163:0x028d, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x028f, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x0292, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0293, code lost:
        r30.mAtmInternal.writeActivitiesToProto(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x0298, code lost:
        r4.flush();
        android.os.Binder.restoreCallingIdentity(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x029e, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x029f, code lost:
        r25 = getAppId(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x02a4, code lost:
        if (r2 >= r15.length) goto L524;
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x02a6, code lost:
        r12 = r15[r2];
        r4 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x02b0, code lost:
        if ("activities".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x02b8, code lost:
        if ("a".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:177:0x02c0, code lost:
        if ("lastanr".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x02c8, code lost:
        if ("lastanr-traces".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x02d1, code lost:
        if ("starter".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x02d9, code lost:
        if ("containers".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x02e2, code lost:
        if ("recents".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x02eb, code lost:
        if ("r".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x02f4, code lost:
        if ("top-resumed".equals(r12) != false) goto L523;
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x02fd, code lost:
        if ("visible".equals(r12) == false) goto L224;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x0307, code lost:
        if ("binder-proxies".equals(r12) == false) goto L256;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x030a, code lost:
        if (r4 < r15.length) goto L255;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x030c, code lost:
        dumpBinderProxies(r32, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x030f, code lost:
        r26 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x0311, code lost:
        r28 = 0;
        r11 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x0316, code lost:
        r1 = r15[r4];
        r4 = r4 + 1;
        java.lang.System.gc();
        java.lang.System.runFinalization();
        java.lang.System.gc();
        r32.println(com.android.internal.os.BinderInternal.nGetBinderProxyCount(java.lang.Integer.parseInt(r1)));
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x0336, code lost:
        if ("allowed-associations".equals(r12) == false) goto L273;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x0339, code lost:
        if (r4 >= r15.length) goto L261;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x033b, code lost:
        r6 = r15[r4];
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x033f, code lost:
        r8 = r4;
        r9 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x0341, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:208:0x0342, code lost:
        boostPriorityForLockedSection();
        dumpAllowedAssociationsLocked(r31, r32, r33, r8, true, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x0353, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x0354, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x0357, code lost:
        r4 = r8;
        r6 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x035b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x035d, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x0360, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x0367, code lost:
        if ("broadcasts".equals(r12) != false) goto L510;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x036f, code lost:
        if ("b".equals(r12) == false) goto L277;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x0379, code lost:
        if ("broadcast-stats".equals(r12) == false) goto L296;
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x037c, code lost:
        if (r4 >= r15.length) goto L282;
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x037e, code lost:
        r6 = r15[r4];
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0382, code lost:
        r8 = r4;
        r9 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0384, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:228:0x0385, code lost:
        boostPriorityForLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x0388, code lost:
        if (r21 == false) goto L291;
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x038a, code lost:
        dumpBroadcastStatsCheckinLocked(r31, r32, r33, r8, r16, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:231:0x039a, code lost:
        dumpBroadcastStatsLocked(r31, r32, r33, r8, true, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x03a8, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x03a9, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x03ad, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x03af, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x03b2, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x03b9, code lost:
        if ("intents".equals(r12) != false) goto L505;
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x03c1, code lost:
        if ("i".equals(r12) == false) goto L300;
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x03cc, code lost:
        if ("processes".equals(r12) != false) goto L488;
     */
    /* JADX WARN: Code restructure failed: missing block: B:246:0x03d5, code lost:
        if ("p".equals(r12) == false) goto L304;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x03e0, code lost:
        if ("oom".equals(r12) != false) goto L477;
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x03e9, code lost:
        if ("o".equals(r12) == false) goto L308;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x03f3, code lost:
        if ("lmk".equals(r12) == false) goto L320;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x03f5, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x03f6, code lost:
        boostPriorityForLockedSection();
        dumpLmkLocked(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:257:0x03fc, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x03fd, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:259:0x0402, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x0404, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x0407, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x040e, code lost:
        if ("lru".equals(r12) == false) goto L332;
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x0410, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x0411, code lost:
        boostPriorityForLockedSection();
        r30.mProcessList.dumpLruLocked(r32, r6, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:267:0x041a, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x041b, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x0420, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x0422, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x0425, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x042d, code lost:
        if ("permissions".equals(r12) != false) goto L476;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0436, code lost:
        if ("perm".equals(r12) == false) goto L336;
     */
    /* JADX WARN: Code restructure failed: missing block: B:279:0x0441, code lost:
        if ("provider".equals(r12) == false) goto L350;
     */
    /* JADX WARN: Code restructure failed: missing block: B:281:0x0444, code lost:
        if (r4 < r15.length) goto L346;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x0446, code lost:
        r5 = com.android.server.p006am.ActivityManagerService.EMPTY_STRING_ARRAY;
        r8 = r4;
        r12 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:283:0x044c, code lost:
        r12 = r15[r4];
        r4 = r4 + 1;
        r1 = new java.lang.String[r15.length - r4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x0455, code lost:
        if (r15.length <= 2) goto L349;
     */
    /* JADX WARN: Code restructure failed: missing block: B:285:0x0457, code lost:
        java.lang.System.arraycopy(r15, r4, r1, 0, r15.length - r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x045c, code lost:
        r5 = r1;
        r8 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:287:0x045e, code lost:
        r26 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x046f, code lost:
        if (r30.mCpHelper.dumpProvider(r31, r32, r12, r5, 0, r20) != false) goto L344;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x0471, code lost:
        r32.println("No providers match: " + r12);
        r32.println("Use -h for help.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:290:0x048a, code lost:
        r4 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x048b, code lost:
        r6 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:292:0x048f, code lost:
        r26 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:293:0x0498, code lost:
        if ("providers".equals(r12) != false) goto L465;
     */
    /* JADX WARN: Code restructure failed: missing block: B:295:0x04a1, code lost:
        if ("prov".equals(r12) == false) goto L354;
     */
    /* JADX WARN: Code restructure failed: missing block: B:298:0x04ac, code lost:
        if ("service".equals(r12) == false) goto L370;
     */
    /* JADX WARN: Code restructure failed: missing block: B:300:0x04af, code lost:
        if (r4 < r15.length) goto L366;
     */
    /* JADX WARN: Code restructure failed: missing block: B:301:0x04b1, code lost:
        r6 = com.android.server.p006am.ActivityManagerService.EMPTY_STRING_ARRAY;
        r10 = r4;
        r9 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:302:0x04b7, code lost:
        r2 = r15[r4];
        r4 = r4 + 1;
        r5 = new java.lang.String[r15.length - r4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x04c0, code lost:
        if (r15.length <= 2) goto L369;
     */
    /* JADX WARN: Code restructure failed: missing block: B:304:0x04c2, code lost:
        java.lang.System.arraycopy(r15, r4, r5, 0, r15.length - r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x04c7, code lost:
        r9 = r2;
        r10 = r4;
        r6 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:306:0x04ca, code lost:
        if (r7 != (-1)) goto L365;
     */
    /* JADX WARN: Code restructure failed: missing block: B:307:0x04cc, code lost:
        r5 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:308:0x04ce, code lost:
        r5 = new int[]{r7};
     */
    /* JADX WARN: Code restructure failed: missing block: B:310:0x04e1, code lost:
        if (r30.mServices.dumpService(r31, r32, r9, r5, r6, 0, r20) != false) goto L364;
     */
    /* JADX WARN: Code restructure failed: missing block: B:311:0x04e3, code lost:
        r32.println("No services match: " + r9);
        r32.println("Use -h for help.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:312:0x04fc, code lost:
        r4 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:314:0x0505, code lost:
        if ("package".equals(r12) == false) goto L380;
     */
    /* JADX WARN: Code restructure failed: missing block: B:316:0x0508, code lost:
        if (r4 < r15.length) goto L376;
     */
    /* JADX WARN: Code restructure failed: missing block: B:317:0x050a, code lost:
        r32.println("package: no package name specified");
        r32.println("Use -h for help.");
        r5 = 0;
        r6 = r26;
        r11 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:318:0x051a, code lost:
        r6 = r15[r4];
        r4 = r4 + 1;
        r1 = new java.lang.String[r15.length - r4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:319:0x0523, code lost:
        if (r15.length <= 2) goto L379;
     */
    /* JADX WARN: Code restructure failed: missing block: B:320:0x0525, code lost:
        java.lang.System.arraycopy(r15, r4, r1, 0, r15.length - r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:321:0x052a, code lost:
        r15 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:322:0x052b, code lost:
        r4 = r11;
        r11 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:324:0x0535, code lost:
        if ("associations".equals(r12) != false) goto L454;
     */
    /* JADX WARN: Code restructure failed: missing block: B:326:0x053d, code lost:
        if ("as".equals(r12) == false) goto L384;
     */
    /* JADX WARN: Code restructure failed: missing block: B:329:0x0548, code lost:
        if ("settings".equals(r12) == false) goto L406;
     */
    /* JADX WARN: Code restructure failed: missing block: B:330:0x054a, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:331:0x054b, code lost:
        boostPriorityForLockedSection();
        r30.mConstants.dump(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:332:0x0553, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:333:0x0554, code lost:
        resetPriorityAfterLockedSection();
        r1 = r30.mProcLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:334:0x0559, code lost:
        monitor-enter(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:335:0x055a, code lost:
        boostPriorityForProcLockedSection();
        r30.mOomAdjuster.dumpCachedAppOptimizerSettings(r32);
        r30.mOomAdjuster.dumpCacheOomRankerSettings(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:336:0x0567, code lost:
        monitor-exit(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x0568, code lost:
        resetPriorityAfterProcLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:338:0x056d, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:340:0x056f, code lost:
        resetPriorityAfterProcLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:341:0x0572, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:342:0x0573, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:344:0x0575, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:345:0x0578, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:347:0x0580, code lost:
        if ("services".equals(r12) != false) goto L432;
     */
    /* JADX WARN: Code restructure failed: missing block: B:349:0x0589, code lost:
        if ("s".equals(r12) == false) goto L410;
     */
    /* JADX WARN: Code restructure failed: missing block: B:352:0x0593, code lost:
        if ("locks".equals(r12) == false) goto L413;
     */
    /* JADX WARN: Code restructure failed: missing block: B:353:0x0595, code lost:
        com.android.server.LockGuard.dump(r31, r32, r33);
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x05a1, code lost:
        if ("users".equals(r12) == false) goto L416;
     */
    /* JADX WARN: Code restructure failed: missing block: B:356:0x05a3, code lost:
        dumpUsers(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:358:0x05ae, code lost:
        if ("exit-info".equals(r12) == false) goto L423;
     */
    /* JADX WARN: Code restructure failed: missing block: B:360:0x05b1, code lost:
        if (r4 >= r15.length) goto L422;
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:0x05b3, code lost:
        r6 = r15[r4];
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:362:0x05b8, code lost:
        r6 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:363:0x05ba, code lost:
        r30.mProcessList.mAppExitInfoTracker.dumpHistoryProcessExitInfo(r32, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:365:0x05c9, code lost:
        if ("component-alias".equals(r12) == false) goto L426;
     */
    /* JADX WARN: Code restructure failed: missing block: B:366:0x05cb, code lost:
        r30.mComponentAliasResolver.dump(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:367:0x05d2, code lost:
        r34 = r4;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:368:0x05f3, code lost:
        if (r30.mAtmInternal.dumpActivity(r31, r32, r12, r33, r34, r20, r8, r9, r10, r17, r7) != false) goto L431;
     */
    /* JADX WARN: Code restructure failed: missing block: B:370:0x060d, code lost:
        if (new com.android.server.p006am.ActivityManagerShellCommand(r30, true).exec(r30, (java.io.FileDescriptor) null, r31, (java.io.FileDescriptor) null, r33, (android.os.ShellCallback) null, new android.os.ResultReceiver(null)) >= 0) goto L431;
     */
    /* JADX WARN: Code restructure failed: missing block: B:371:0x060f, code lost:
        r32.println("Bad activity command, or no activities match: " + r12);
        r32.println("Use -h for help.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:372:0x062c, code lost:
        r34 = r4;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:373:0x0630, code lost:
        if (r18 == false) goto L444;
     */
    /* JADX WARN: Code restructure failed: missing block: B:374:0x0632, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:375:0x0633, code lost:
        boostPriorityForLockedSection();
        r0 = r30.mServices.newServiceDumperLocked(r31, r32, r33, r34, true, r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:376:0x0647, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:377:0x0648, code lost:
        resetPriorityAfterLockedSection();
        r0.dumpWithClient();
     */
    /* JADX WARN: Code restructure failed: missing block: B:378:0x0650, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x0652, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:381:0x0655, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:382:0x0656, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:383:0x0657, code lost:
        boostPriorityForLockedSection();
        r30.mServices.newServiceDumperLocked(r31, r32, r33, r34, true, r26).dumpLocked();
     */
    /* JADX WARN: Code restructure failed: missing block: B:384:0x066e, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:385:0x066f, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:386:0x0674, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:388:0x0676, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:389:0x0679, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:390:0x067a, code lost:
        r34 = r4;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:391:0x067e, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:392:0x067f, code lost:
        boostPriorityForLockedSection();
        dumpAssociationsLocked(r31, r32, r33, r34, true, r18, r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:393:0x0694, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:394:0x0695, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:395:0x0699, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:397:0x069b, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:398:0x069e, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:399:0x069f, code lost:
        r34 = r4;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:400:0x06a3, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:401:0x06a4, code lost:
        boostPriorityForLockedSection();
        r30.mCpHelper.dumpProvidersLocked(r31, r32, r33, r34, true, r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:402:0x06b7, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:403:0x06b8, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:404:0x06bc, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:406:0x06be, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:407:0x06c1, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:408:0x06c2, code lost:
        r34 = r4;
        r26 = r6;
        r28 = 0;
        dumpPermissions(r31, r32, r33, r34, true, r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:409:0x06d8, code lost:
        r11 = r34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:410:0x06dc, code lost:
        r34 = r4;
        r26 = r6;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:411:0x06e2, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:0x06e3, code lost:
        boostPriorityForLockedSection();
        r30.mProcessList.dumpOomLocked(r31, r32, false, r33, r34, true, r26, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:0x06f8, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:414:0x06f9, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:415:0x06fd, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:417:0x06ff, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:418:0x0702, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:419:0x0703, code lost:
        r26 = r6;
        r28 = 0;
        r11 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:420:0x070c, code lost:
        if (r11 >= r15.length) goto L491;
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x070e, code lost:
        r6 = r15[r11];
        r11 = r11 + 1;
        r26 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:422:0x0715, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x0716, code lost:
        boostPriorityForLockedSection();
        r9 = r30.mProcLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:424:0x071b, code lost:
        monitor-enter(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:425:0x071c, code lost:
        boostPriorityForProcLockedSection();
        r30.mProcessList.dumpProcessesLSP(r31, r32, r33, r11, true, r26, r25);
     */
    /* JADX WARN: Code restructure failed: missing block: B:426:0x0730, code lost:
        monitor-exit(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:427:0x0731, code lost:
        resetPriorityAfterProcLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:428:0x0734, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:429:0x0735, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:434:0x0740, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:436:0x0742, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x0745, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:438:0x0746, code lost:
        r26 = r6;
        r28 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x074d, code lost:
        if (r4 >= r15.length) goto L509;
     */
    /* JADX WARN: Code restructure failed: missing block: B:440:0x074f, code lost:
        r6 = r15[r4];
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:441:0x0754, code lost:
        r4 = r4;
        r6 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:442:0x0757, code lost:
        r30.mPendingIntentController.dumpPendingIntents(r32, true, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x075d, code lost:
        r26 = r6;
        r28 = 0;
        r11 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:444:0x0763, code lost:
        if (r11 >= r15.length) goto L513;
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x0765, code lost:
        r6 = r15[r11];
        r11 = r11 + 1;
        r26 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:446:0x076c, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x076d, code lost:
        boostPriorityForLockedSection();
        dumpBroadcastsLocked(r31, r32, r33, r11, true, r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:448:0x077f, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:449:0x0780, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:450:0x0784, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:452:0x0786, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:453:0x0789, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:454:0x078a, code lost:
        r26 = r6;
        r28 = 0;
        r11 = r4;
        r30.mAtmInternal.dump(r12, r31, r32, r33, r11, true, r18, r26, r17);
     */
    /* JADX WARN: Code restructure failed: missing block: B:455:0x07a3, code lost:
        r4 = r11;
        r6 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:456:0x07a6, code lost:
        r11 = r28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:457:0x07a8, code lost:
        if (r11 != 0) goto L236;
     */
    /* JADX WARN: Code restructure failed: missing block: B:458:0x07aa, code lost:
        android.os.Binder.restoreCallingIdentity(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:459:0x07ad, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:460:0x07ae, code lost:
        r5 = r4;
        r7 = r6;
        r4 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:461:0x07b2, code lost:
        r5 = r2;
        r4 = r15;
        r7 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:462:0x07b8, code lost:
        if (r21 == false) goto L241;
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x07ba, code lost:
        dumpBroadcastStatsCheckinLocked(r31, r32, r4, r5, r16, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:464:0x07c6, code lost:
        if (r18 == false) goto L245;
     */
    /* JADX WARN: Code restructure failed: missing block: B:465:0x07c8, code lost:
        dumpEverything(r31, r32, r4, r5, r20, r7, r17, r18, r19, r25, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:466:0x07dd, code lost:
        monitor-enter(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:467:0x07de, code lost:
        boostPriorityForLockedSection();
        dumpEverything(r31, r32, r4, r5, r20, r7, r17, r18, r19, r25, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:468:0x07f5, code lost:
        monitor-exit(r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:469:0x07f6, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:470:0x07f9, code lost:
        if (r20 == false) goto L239;
     */
    /* JADX WARN: Code restructure failed: missing block: B:471:0x07fb, code lost:
        dumpAppRestrictionController(r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:472:0x07fe, code lost:
        android.os.Binder.restoreCallingIdentity(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:473:0x0801, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:474:0x0802, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:476:0x0804, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x0807, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0124, code lost:
        if (r34 == false) goto L202;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0126, code lost:
        r4 = new android.util.proto.ProtoOutputStream(r31);
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x012c, code lost:
        if (r2 >= r15.length) goto L201;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x012e, code lost:
        r7 = r15[r2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0131, code lost:
        r7 = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0133, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x013a, code lost:
        if ("activities".equals(r7) != false) goto L200;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0142, code lost:
        if ("a".equals(r7) == false) goto L115;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x014c, code lost:
        if ("broadcasts".equals(r7) != false) goto L190;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0154, code lost:
        if ("b".equals(r7) == false) goto L119;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x015f, code lost:
        if ("provider".equals(r7) == false) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0162, code lost:
        if (r2 < r15.length) goto L129;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0164, code lost:
        r1 = com.android.server.p006am.ActivityManagerService.EMPTY_STRING_ARRAY;
        r12 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0168, code lost:
        r12 = r15[r2];
        r2 = r2 + 1;
        r1 = new java.lang.String[r15.length - r2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0170, code lost:
        if (r15.length <= 2) goto L124;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0172, code lost:
        java.lang.System.arraycopy(r15, r2, r1, 0, r15.length - r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x017d, code lost:
        if (r30.mCpHelper.dumpProviderProto(r31, r32, r12, r1) != false) goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x017f, code lost:
        r32.println("No providers match: " + r12);
        r32.println("Use -h for help.");
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void doDump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
        String str;
        String[] strArr2 = strArr;
        if (!DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "ActivityManager", printWriter)) {
            return;
        }
        int i = -1;
        int i2 = 0;
        int i3 = -1;
        int i4 = -1;
        int i5 = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        boolean z9 = false;
        String str2 = null;
        while (true) {
            int i6 = 1;
            if (i5 >= strArr2.length || (str = strArr2[i5]) == null || str.length() <= 0 || str.charAt(0) != '-') {
                break;
            }
            i5++;
            if ("-a".equals(str)) {
                z8 = true;
            } else {
                if (!"-c".equals(str)) {
                    if ("-v".equals(str)) {
                        z2 = true;
                    } else if ("-f".equals(str)) {
                        z3 = true;
                    } else if ("-p".equals(str)) {
                        if (i5 < strArr2.length) {
                            str2 = strArr2[i5];
                            i5++;
                        } else {
                            printWriter.println("Error: -p option requires package argument");
                            return;
                        }
                    } else if ("--checkin".equals(str)) {
                        z5 = true;
                        z9 = true;
                    } else if ("-C".equals(str)) {
                        z9 = true;
                    } else if ("--normal-priority".equals(str)) {
                        z7 = true;
                    } else if ("--user".equals(str)) {
                        if (i5 < strArr2.length) {
                            int parseUserArg = UserHandle.parseUserArg(strArr2[i5]);
                            if (parseUserArg == -2) {
                                parseUserArg = this.mUserController.getCurrentUserId();
                            }
                            i3 = parseUserArg;
                            i5++;
                        } else {
                            printWriter.println("Error: --user option requires user id argument");
                            return;
                        }
                    } else if ("-d".equals(str)) {
                        if (i5 < strArr2.length) {
                            int parseInt = Integer.parseInt(strArr2[i5]);
                            if (parseInt == -1) {
                                printWriter.println("Error: -d cannot be used with INVALID_DISPLAY");
                                return;
                            } else {
                                i5++;
                                i4 = parseInt;
                            }
                        } else {
                            printWriter.println("Error: -d option requires display argument");
                            return;
                        }
                    } else if (!"--verbose".equals(str)) {
                        if ("-h".equals(str)) {
                            ActivityManagerShellCommand.dumpHelp(printWriter, true);
                            return;
                        }
                        printWriter.println("Unknown argument: " + str + "; use -h for help");
                        return;
                    } else {
                        z4 = true;
                    }
                }
                z6 = true;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0029  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dumpAssociationsLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, String str) {
        int i2;
        int size;
        int i3;
        boolean z3;
        ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>> arrayMap;
        SparseArray<ArrayMap<String, Association>> sparseArray;
        int i4;
        ActivityManagerService activityManagerService = this;
        String str2 = str;
        printWriter.println("ACTIVITY MANAGER ASSOCIATIONS (dumpsys activity associations)");
        int i5 = 0;
        if (str2 != null) {
            try {
                i2 = AppGlobals.getPackageManager().getPackageUid(str2, 4194304L, 0);
            } catch (RemoteException unused) {
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            size = activityManagerService.mAssociations.size();
            i3 = 0;
            z3 = false;
            while (i3 < size) {
                ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>> valueAt = activityManagerService.mAssociations.valueAt(i3);
                int size2 = valueAt.size();
                int i6 = i5;
                while (i6 < size2) {
                    SparseArray<ArrayMap<String, Association>> valueAt2 = valueAt.valueAt(i6);
                    int size3 = valueAt2.size();
                    int i7 = i5;
                    while (i7 < size3) {
                        ArrayMap<String, Association> valueAt3 = valueAt2.valueAt(i7);
                        int size4 = valueAt3.size();
                        int i8 = size;
                        int i9 = 0;
                        while (i9 < size4) {
                            int i10 = size4;
                            Association valueAt4 = valueAt3.valueAt(i9);
                            ArrayMap<String, Association> arrayMap2 = valueAt3;
                            if (str2 == null || valueAt4.mTargetComponent.getPackageName().equals(str2) || UserHandle.getAppId(valueAt4.mSourceUid) == i2) {
                                printWriter.print("  ");
                                printWriter.print(valueAt4.mTargetProcess);
                                printWriter.print("/");
                                UserHandle.formatUid(printWriter, valueAt4.mTargetUid);
                                printWriter.print(" <- ");
                                printWriter.print(valueAt4.mSourceProcess);
                                printWriter.print("/");
                                UserHandle.formatUid(printWriter, valueAt4.mSourceUid);
                                printWriter.println();
                                printWriter.print("    via ");
                                printWriter.print(valueAt4.mTargetComponent.flattenToShortString());
                                printWriter.println();
                                printWriter.print("    ");
                                arrayMap = valueAt;
                                long j = valueAt4.mTime;
                                if (valueAt4.mNesting > 0) {
                                    sparseArray = valueAt2;
                                    i4 = size3;
                                    j += uptimeMillis - valueAt4.mStartTime;
                                } else {
                                    sparseArray = valueAt2;
                                    i4 = size3;
                                }
                                TimeUtils.formatDuration(j, printWriter);
                                printWriter.print(" (");
                                printWriter.print(valueAt4.mCount);
                                printWriter.print(" times)");
                                printWriter.print("  ");
                                int i11 = 0;
                                while (true) {
                                    long[] jArr = valueAt4.mStateTimes;
                                    if (i11 >= jArr.length) {
                                        break;
                                    }
                                    long j2 = jArr[i11];
                                    if (valueAt4.mLastState - 0 == i11) {
                                        j2 += uptimeMillis - valueAt4.mLastStateUptime;
                                    }
                                    if (j2 != 0) {
                                        printWriter.print(" ");
                                        printWriter.print(ProcessList.makeProcStateString(i11 + 0));
                                        printWriter.print("=");
                                        TimeUtils.formatDuration(j2, printWriter);
                                        if (valueAt4.mLastState - 0 == i11) {
                                            printWriter.print("*");
                                        }
                                    }
                                    i11++;
                                }
                                printWriter.println();
                                if (valueAt4.mNesting > 0) {
                                    printWriter.print("    Currently active: ");
                                    TimeUtils.formatDuration(uptimeMillis - valueAt4.mStartTime, printWriter);
                                    printWriter.println();
                                }
                                z3 = true;
                            } else {
                                arrayMap = valueAt;
                                sparseArray = valueAt2;
                                i4 = size3;
                            }
                            i9++;
                            size4 = i10;
                            valueAt3 = arrayMap2;
                            valueAt = arrayMap;
                            valueAt2 = sparseArray;
                            size3 = i4;
                            str2 = str;
                        }
                        i7++;
                        size = i8;
                        i5 = 0;
                        valueAt = valueAt;
                        str2 = str;
                    }
                    i6++;
                    str2 = str;
                    valueAt = valueAt;
                }
                i3++;
                activityManagerService = this;
                str2 = str;
            }
            if (z3) {
                printWriter.println("  (nothing)");
                return;
            }
            return;
        }
        i2 = 0;
        long uptimeMillis2 = SystemClock.uptimeMillis();
        size = activityManagerService.mAssociations.size();
        i3 = 0;
        z3 = false;
        while (i3 < size) {
        }
        if (z3) {
        }
    }

    public int getAppId(String str) {
        if (str != null) {
            try {
                return UserHandle.getAppId(this.mContext.getPackageManager().getApplicationInfo(str, 0).uid);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public void dumpBinderProxyInterfaceCounts(PrintWriter printWriter, String str) {
        BinderProxy.InterfaceCount[] sortedInterfaceCounts = BinderProxy.getSortedInterfaceCounts(50);
        printWriter.println(str);
        int i = 0;
        while (i < sortedInterfaceCounts.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("    #");
            int i2 = i + 1;
            sb.append(i2);
            sb.append(": ");
            sb.append(sortedInterfaceCounts[i]);
            printWriter.println(sb.toString());
            i = i2;
        }
    }

    public boolean dumpBinderProxiesCounts(PrintWriter printWriter, String str) {
        SparseIntArray nGetBinderProxyPerUidCounts = BinderInternal.nGetBinderProxyPerUidCounts();
        if (nGetBinderProxyPerUidCounts != null) {
            printWriter.println(str);
            for (int i = 0; i < nGetBinderProxyPerUidCounts.size(); i++) {
                int keyAt = nGetBinderProxyPerUidCounts.keyAt(i);
                int valueAt = nGetBinderProxyPerUidCounts.valueAt(i);
                printWriter.print("    UID ");
                printWriter.print(keyAt);
                printWriter.print(", binder count = ");
                printWriter.print(valueAt);
                printWriter.print(", package(s)= ");
                String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(keyAt);
                if (packagesForUid != null) {
                    for (String str2 : packagesForUid) {
                        printWriter.print(str2);
                        printWriter.print("; ");
                    }
                } else {
                    printWriter.print("NO PACKAGE NAME FOUND");
                }
                printWriter.println();
            }
            return true;
        }
        return false;
    }

    public void dumpBinderProxies(PrintWriter printWriter, int i) {
        printWriter.println("ACTIVITY MANAGER BINDER PROXY STATE (dumpsys activity binder-proxies)");
        int proxyCount = BinderProxy.getProxyCount();
        if (proxyCount >= i) {
            dumpBinderProxyInterfaceCounts(printWriter, "Top proxy interface names held by SYSTEM");
        } else {
            printWriter.print("Not dumping proxy interface counts because size (" + Integer.toString(proxyCount) + ") looks reasonable");
            printWriter.println();
        }
        dumpBinderProxiesCounts(printWriter, "  Counts of Binder Proxies held by SYSTEM");
    }

    @GuardedBy({"this"})
    public boolean dumpActiveInstruments(PrintWriter printWriter, String str, boolean z) {
        int size = this.mActiveInstrumentation.size();
        if (size > 0) {
            boolean z2 = false;
            for (int i = 0; i < size; i++) {
                ActiveInstrumentation activeInstrumentation = this.mActiveInstrumentation.get(i);
                if (str == null || activeInstrumentation.mClass.getPackageName().equals(str) || activeInstrumentation.mTargetInfo.packageName.equals(str)) {
                    if (!z2) {
                        if (z) {
                            printWriter.println();
                        }
                        printWriter.println("  Active instrumentation:");
                        z = true;
                        z2 = true;
                    }
                    printWriter.print("    Instrumentation #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(activeInstrumentation);
                    activeInstrumentation.dump(printWriter, "      ");
                }
            }
        }
        return z;
    }

    @GuardedBy({"this", "mProcLock"})
    @NeverCompile
    public void dumpOtherProcessesInfoLSP(FileDescriptor fileDescriptor, final PrintWriter printWriter, boolean z, String str, int i, int i2, boolean z2) {
        boolean z3;
        boolean dumpMemWatchProcessesLPf;
        boolean z4 = false;
        boolean z5 = true;
        if (z || str != null) {
            SparseArray sparseArray = new SparseArray();
            synchronized (this.mPidsSelfLocked) {
                int size = this.mPidsSelfLocked.size();
                z3 = z2;
                boolean z6 = false;
                for (int i3 = 0; i3 < size; i3++) {
                    ProcessRecord valueAt = this.mPidsSelfLocked.valueAt(i3);
                    sparseArray.put(valueAt.getPid(), valueAt);
                    if (str == null || valueAt.getPkgList().containsKey(str)) {
                        if (!z6) {
                            if (z3) {
                                printWriter.println();
                            }
                            printWriter.println("  PID mappings:");
                            z3 = true;
                            z6 = true;
                        }
                        printWriter.print("    PID #");
                        printWriter.print(this.mPidsSelfLocked.keyAt(i3));
                        printWriter.print(": ");
                        printWriter.println(this.mPidsSelfLocked.valueAt(i3));
                    }
                }
            }
            SparseArray<ProcessInfo> sparseArray2 = sActiveProcessInfoSelfLocked;
            synchronized (sparseArray2) {
                int size2 = sparseArray2.size();
                boolean z7 = false;
                for (int i4 = 0; i4 < size2; i4++) {
                    SparseArray<ProcessInfo> sparseArray3 = sActiveProcessInfoSelfLocked;
                    ProcessInfo valueAt2 = sparseArray3.valueAt(i4);
                    ProcessRecord processRecord = (ProcessRecord) sparseArray.get(sparseArray3.keyAt(i4));
                    if (processRecord == null || str == null || processRecord.getPkgList().containsKey(str)) {
                        if (!z7) {
                            if (z3) {
                                printWriter.println();
                            }
                            printWriter.println("  Active process infos:");
                            z3 = true;
                            z7 = true;
                        }
                        printWriter.print("    Pinfo PID #");
                        printWriter.print(sparseArray3.keyAt(i4));
                        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        printWriter.print("      name=");
                        printWriter.println(valueAt2.name);
                        if (valueAt2.deniedPermissions != null) {
                            for (int i5 = 0; i5 < valueAt2.deniedPermissions.size(); i5++) {
                                printWriter.print("      deny: ");
                                printWriter.println((String) valueAt2.deniedPermissions.valueAt(i5));
                            }
                        }
                    }
                }
            }
        } else {
            z3 = z2;
        }
        if (z) {
            this.mPhantomProcessList.dump(printWriter, "  ");
        }
        if (this.mImportantProcesses.size() > 0) {
            synchronized (this.mPidsSelfLocked) {
                int size3 = this.mImportantProcesses.size();
                boolean z8 = false;
                for (int i6 = 0; i6 < size3; i6++) {
                    ProcessRecord processRecord2 = this.mPidsSelfLocked.get(this.mImportantProcesses.valueAt(i6).pid);
                    if (str == null || (processRecord2 != null && processRecord2.getPkgList().containsKey(str))) {
                        if (!z8) {
                            if (z3) {
                                printWriter.println();
                            }
                            printWriter.println("  Foreground Processes:");
                            z3 = true;
                            z8 = true;
                        }
                        printWriter.print("    PID #");
                        printWriter.print(this.mImportantProcesses.keyAt(i6));
                        printWriter.print(": ");
                        printWriter.println(this.mImportantProcesses.valueAt(i6));
                    }
                }
            }
        }
        if (this.mPersistentStartingProcesses.size() > 0) {
            if (z3) {
                printWriter.println();
            }
            printWriter.println("  Persisent processes that are starting:");
            dumpProcessList(printWriter, this, this.mPersistentStartingProcesses, "    ", "Starting Norm", "Restarting PERS", str);
            z3 = true;
        }
        if (this.mProcessList.mRemovedProcesses.size() > 0) {
            if (z3) {
                printWriter.println();
            }
            printWriter.println("  Processes that are being removed:");
            dumpProcessList(printWriter, this, this.mProcessList.mRemovedProcesses, "    ", "Removed Norm", "Removed PERS", str);
            z3 = true;
        }
        if (this.mProcessesOnHold.size() > 0) {
            if (z3) {
                printWriter.println();
            }
            printWriter.println("  Processes that are on old until the system is ready:");
            dumpProcessList(printWriter, this, this.mProcessesOnHold, "    ", "OnHold Norm", "OnHold PERS", str);
            z3 = true;
        }
        boolean dumpForProcesses = this.mAtmInternal.dumpForProcesses(fileDescriptor, printWriter, z, str, i, this.mAppErrors.dumpLPr(fileDescriptor, printWriter, z3, str), this.mAppProfiler.getTestPssMode(), this.mWakefulness.get());
        if (!z || this.mProcessList.mPendingStarts.size() <= 0) {
            z5 = dumpForProcesses;
        } else {
            if (dumpForProcesses) {
                printWriter.println();
            }
            printWriter.println("  mPendingStarts: ");
            int size4 = this.mProcessList.mPendingStarts.size();
            for (int i7 = 0; i7 < size4; i7++) {
                printWriter.println("    " + this.mProcessList.mPendingStarts.keyAt(i7) + ": " + this.mProcessList.mPendingStarts.valueAt(i7));
            }
        }
        if (z) {
            this.mUidObserverController.dump(printWriter, str);
            printWriter.println("  mDeviceIdleAllowlist=" + Arrays.toString(this.mDeviceIdleAllowlist));
            printWriter.println("  mDeviceIdleExceptIdleAllowlist=" + Arrays.toString(this.mDeviceIdleExceptIdleAllowlist));
            printWriter.println("  mDeviceIdleTempAllowlist=" + Arrays.toString(this.mDeviceIdleTempAllowlist));
            if (this.mPendingTempAllowlist.size() > 0) {
                printWriter.println("  mPendingTempAllowlist:");
                int size5 = this.mPendingTempAllowlist.size();
                for (int i8 = 0; i8 < size5; i8++) {
                    PendingTempAllowlist valueAt3 = this.mPendingTempAllowlist.valueAt(i8);
                    printWriter.print("    ");
                    UserHandle.formatUid(printWriter, valueAt3.targetUid);
                    printWriter.print(": ");
                    TimeUtils.formatDuration(valueAt3.duration, printWriter);
                    printWriter.print(" ");
                    printWriter.println(valueAt3.tag);
                    printWriter.print(" ");
                    printWriter.print(valueAt3.type);
                    printWriter.print(" ");
                    printWriter.print(valueAt3.reasonCode);
                    printWriter.print(" ");
                    printWriter.print(valueAt3.callingUid);
                }
            }
            printWriter.println("  mFgsStartTempAllowList:");
            final long currentTimeMillis = System.currentTimeMillis();
            final long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mFgsStartTempAllowList.forEach(new BiConsumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda25
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ActivityManagerService.lambda$dumpOtherProcessesInfoLSP$14(printWriter, currentTimeMillis, elapsedRealtime, (Integer) obj, (Pair) obj2);
                }
            });
            if (!this.mProcessList.mAppsInBackgroundRestricted.isEmpty()) {
                printWriter.println("  Processes that are in background restricted:");
                int size6 = this.mProcessList.mAppsInBackgroundRestricted.size();
                for (int i9 = 0; i9 < size6; i9++) {
                    printWriter.println(String.format("%s #%2d: %s", "    ", Integer.valueOf(i9), this.mProcessList.mAppsInBackgroundRestricted.valueAt(i9).toString()));
                }
            }
        }
        String str2 = this.mDebugApp;
        if ((str2 != null || this.mOrigDebugApp != null || this.mDebugTransient || this.mOrigWaitForDebugger) && (str == null || str.equals(str2) || str.equals(this.mOrigDebugApp))) {
            if (z5) {
                printWriter.println();
                z5 = false;
            }
            printWriter.println("  mDebugApp=" + this.mDebugApp + "/orig=" + this.mOrigDebugApp + " mDebugTransient=" + this.mDebugTransient + " mOrigWaitForDebugger=" + this.mOrigWaitForDebugger);
        }
        synchronized (this.mAppProfiler.mProfilerLock) {
            dumpMemWatchProcessesLPf = this.mAppProfiler.dumpMemWatchProcessesLPf(printWriter, z5);
        }
        String str3 = this.mTrackAllocationApp;
        if (str3 != null && (str == null || str.equals(str3))) {
            if (dumpMemWatchProcessesLPf) {
                printWriter.println();
            } else {
                z4 = dumpMemWatchProcessesLPf;
            }
            printWriter.println("  mTrackAllocationApp=" + this.mTrackAllocationApp);
            dumpMemWatchProcessesLPf = z4;
        }
        boolean dumpProfileDataLocked = this.mAppProfiler.dumpProfileDataLocked(printWriter, str, dumpMemWatchProcessesLPf);
        String str4 = this.mNativeDebuggingApp;
        if (str4 != null && (str == null || str.equals(str4))) {
            if (dumpProfileDataLocked) {
                printWriter.println();
            }
            printWriter.println("  mNativeDebuggingApp=" + this.mNativeDebuggingApp);
        }
        if (str == null) {
            if (this.mAlwaysFinishActivities) {
                printWriter.println("  mAlwaysFinishActivities=" + this.mAlwaysFinishActivities);
            }
            if (z) {
                printWriter.println("  Total persistent processes: " + i2);
                printWriter.println("  mProcessesReady=" + this.mProcessesReady + " mSystemReady=" + this.mSystemReady + " mBooted=" + this.mBooted + " mFactoryTest=" + this.mFactoryTest);
                StringBuilder sb = new StringBuilder();
                sb.append("  mBooting=");
                sb.append(this.mBooting);
                sb.append(" mCallFinishBooting=");
                sb.append(this.mCallFinishBooting);
                sb.append(" mBootAnimationComplete=");
                sb.append(this.mBootAnimationComplete);
                printWriter.println(sb.toString());
                printWriter.print("  mLastPowerCheckUptime=");
                TimeUtils.formatDuration(this.mLastPowerCheckUptime, printWriter);
                printWriter.println("");
                this.mOomAdjuster.dumpSequenceNumbersLocked(printWriter);
                this.mOomAdjuster.dumpProcCountsLocked(printWriter);
                this.mAppProfiler.dumpMemoryLevelsLocked(printWriter);
                long uptimeMillis = SystemClock.uptimeMillis();
                printWriter.print("  mLastIdleTime=");
                TimeUtils.formatDuration(uptimeMillis, this.mLastIdleTime, printWriter);
                printWriter.print(" mLowRamSinceLastIdle=");
                TimeUtils.formatDuration(this.mAppProfiler.getLowRamTimeSinceIdleLPr(uptimeMillis), printWriter);
                printWriter.println();
                printWriter.println();
                printWriter.println("  ServiceManager statistics:");
                ServiceManager.sStatLogger.dump(printWriter, "    ");
                printWriter.println();
            }
        }
        printWriter.println("  mForceBackgroundCheck=" + this.mForceBackgroundCheck);
    }

    public static /* synthetic */ void lambda$dumpOtherProcessesInfoLSP$14(PrintWriter printWriter, long j, long j2, Integer num, Pair pair) {
        printWriter.print("    " + UserHandle.formatUid(num.intValue()) + ": ");
        ((FgsTempAllowListItem) pair.second).dump(printWriter);
        printWriter.print(" expiration=");
        TimeUtils.dumpTimeWithDelta(printWriter, (j - j2) + ((Long) pair.first).longValue(), j);
        printWriter.println();
    }

    public final void dumpUsers(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER USERS (dumpsys activity users)");
        this.mUserController.dump(printWriter);
    }

    @GuardedBy({"this", "mProcLock"})
    public void writeOtherProcessesInfoToProtoLSP(ProtoOutputStream protoOutputStream, String str, int i, int i2) {
        int size = this.mActiveInstrumentation.size();
        for (int i3 = 0; i3 < size; i3++) {
            ActiveInstrumentation activeInstrumentation = this.mActiveInstrumentation.get(i3);
            if (str == null || activeInstrumentation.mClass.getPackageName().equals(str) || activeInstrumentation.mTargetInfo.packageName.equals(str)) {
                activeInstrumentation.dumpDebug(protoOutputStream, 2246267895811L);
            }
        }
        this.mUidObserverController.dumpValidateUidsProto(protoOutputStream, str, i, 2246267895813L);
        if (str != null) {
            synchronized (this.mPidsSelfLocked) {
                int size2 = this.mPidsSelfLocked.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    ProcessRecord valueAt = this.mPidsSelfLocked.valueAt(i4);
                    if (valueAt.getPkgList().containsKey(str)) {
                        valueAt.dumpDebug(protoOutputStream, 2246267895815L);
                    }
                }
            }
        }
        if (this.mImportantProcesses.size() > 0) {
            synchronized (this.mPidsSelfLocked) {
                int size3 = this.mImportantProcesses.size();
                for (int i5 = 0; i5 < size3; i5++) {
                    ImportanceToken valueAt2 = this.mImportantProcesses.valueAt(i5);
                    ProcessRecord processRecord = this.mPidsSelfLocked.get(valueAt2.pid);
                    if (str == null || (processRecord != null && processRecord.getPkgList().containsKey(str))) {
                        valueAt2.dumpDebug(protoOutputStream, 2246267895816L);
                    }
                }
            }
        }
        int size4 = this.mPersistentStartingProcesses.size();
        for (int i6 = 0; i6 < size4; i6++) {
            ProcessRecord processRecord2 = this.mPersistentStartingProcesses.get(i6);
            if (str == null || str.equals(processRecord2.info.packageName)) {
                processRecord2.dumpDebug(protoOutputStream, 2246267895817L);
            }
        }
        int size5 = this.mProcessList.mRemovedProcesses.size();
        for (int i7 = 0; i7 < size5; i7++) {
            ProcessRecord processRecord3 = this.mProcessList.mRemovedProcesses.get(i7);
            if (str == null || str.equals(processRecord3.info.packageName)) {
                processRecord3.dumpDebug(protoOutputStream, 2246267895818L);
            }
        }
        int size6 = this.mProcessesOnHold.size();
        for (int i8 = 0; i8 < size6; i8++) {
            ProcessRecord processRecord4 = this.mProcessesOnHold.get(i8);
            if (str == null || str.equals(processRecord4.info.packageName)) {
                processRecord4.dumpDebug(protoOutputStream, 2246267895819L);
            }
        }
        synchronized (this.mAppProfiler.mProfilerLock) {
            this.mAppProfiler.writeProcessesToGcToProto(protoOutputStream, 2246267895820L, str);
        }
        this.mAppErrors.dumpDebugLPr(protoOutputStream, 1146756268045L, str);
        this.mAtmInternal.writeProcessesToProto(protoOutputStream, str, this.mWakefulness.get(), this.mAppProfiler.getTestPssMode());
        if (str == null) {
            this.mUserController.dumpDebug(protoOutputStream, 1146756268046L);
        }
        this.mUidObserverController.dumpDebug(protoOutputStream, str);
        for (int i9 : this.mDeviceIdleAllowlist) {
            protoOutputStream.write(2220498092056L, i9);
        }
        for (int i10 : this.mDeviceIdleTempAllowlist) {
            protoOutputStream.write(2220498092057L, i10);
        }
        if (this.mPendingTempAllowlist.size() > 0) {
            int size7 = this.mPendingTempAllowlist.size();
            for (int i11 = 0; i11 < size7; i11++) {
                this.mPendingTempAllowlist.valueAt(i11).dumpDebug(protoOutputStream, 2246267895834L);
            }
        }
        String str2 = this.mDebugApp;
        if ((str2 != null || this.mOrigDebugApp != null || this.mDebugTransient || this.mOrigWaitForDebugger) && (str == null || str.equals(str2) || str.equals(this.mOrigDebugApp))) {
            long start = protoOutputStream.start(1146756268062L);
            protoOutputStream.write(1138166333441L, this.mDebugApp);
            protoOutputStream.write(1138166333442L, this.mOrigDebugApp);
            protoOutputStream.write(1133871366147L, this.mDebugTransient);
            protoOutputStream.write(1133871366148L, this.mOrigWaitForDebugger);
            protoOutputStream.end(start);
        }
        synchronized (this.mAppProfiler.mProfilerLock) {
            this.mAppProfiler.writeMemWatchProcessToProtoLPf(protoOutputStream);
        }
        String str3 = this.mTrackAllocationApp;
        if (str3 != null && (str == null || str.equals(str3))) {
            protoOutputStream.write(1138166333473L, this.mTrackAllocationApp);
        }
        this.mAppProfiler.writeProfileDataToProtoLocked(protoOutputStream, str);
        if (str == null || str.equals(this.mNativeDebuggingApp)) {
            protoOutputStream.write(1138166333475L, this.mNativeDebuggingApp);
        }
        if (str == null) {
            protoOutputStream.write(1133871366180L, this.mAlwaysFinishActivities);
            protoOutputStream.write(1120986464294L, i2);
            protoOutputStream.write(1133871366183L, this.mProcessesReady);
            protoOutputStream.write(1133871366184L, this.mSystemReady);
            protoOutputStream.write(1133871366185L, this.mBooted);
            protoOutputStream.write(1120986464298L, this.mFactoryTest);
            protoOutputStream.write(1133871366187L, this.mBooting);
            protoOutputStream.write(1133871366188L, this.mCallFinishBooting);
            protoOutputStream.write(1133871366189L, this.mBootAnimationComplete);
            protoOutputStream.write(1112396529710L, this.mLastPowerCheckUptime);
            this.mOomAdjuster.dumpProcessListVariablesLocked(protoOutputStream);
            this.mAppProfiler.writeMemoryLevelsToProtoLocked(protoOutputStream);
            long uptimeMillis = SystemClock.uptimeMillis();
            ProtoUtils.toDuration(protoOutputStream, 1146756268090L, this.mLastIdleTime, uptimeMillis);
            protoOutputStream.write(1112396529723L, this.mAppProfiler.getLowRamTimeSinceIdleLPr(uptimeMillis));
        }
    }

    public final boolean reportLmkKillAtOrBelow(PrintWriter printWriter, int i) {
        Integer lmkdKillCount = ProcessList.getLmkdKillCount(0, i);
        if (lmkdKillCount != null) {
            printWriter.println("    kills at or below oom_adj " + i + ": " + lmkdKillCount);
            return true;
        }
        return false;
    }

    public boolean dumpLmkLocked(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER LMK KILLS (dumpsys activity lmk)");
        Integer lmkdKillCount = ProcessList.getLmkdKillCount(1001, 1001);
        if (lmkdKillCount == null) {
            return false;
        }
        printWriter.println("  Total number of kills: " + lmkdKillCount);
        return reportLmkKillAtOrBelow(printWriter, 999) && reportLmkKillAtOrBelow(printWriter, 900) && reportLmkKillAtOrBelow(printWriter, 800) && reportLmkKillAtOrBelow(printWriter, 700) && reportLmkKillAtOrBelow(printWriter, 600) && reportLmkKillAtOrBelow(printWriter, 500) && reportLmkKillAtOrBelow(printWriter, FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND) && reportLmkKillAtOrBelow(printWriter, 300) && reportLmkKillAtOrBelow(printWriter, 250) && reportLmkKillAtOrBelow(printWriter, 200) && reportLmkKillAtOrBelow(printWriter, 100) && reportLmkKillAtOrBelow(printWriter, 0);
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ItemMatcher */
    /* loaded from: classes.dex */
    public static class ItemMatcher {
        public boolean all = true;
        public ArrayList<ComponentName> components;
        public ArrayList<Integer> objects;
        public ArrayList<String> strings;

        public void build(String str) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                if (this.components == null) {
                    this.components = new ArrayList<>();
                }
                this.components.add(unflattenFromString);
                this.all = false;
                return;
            }
            try {
                int parseInt = Integer.parseInt(str, 16);
                if (this.objects == null) {
                    this.objects = new ArrayList<>();
                }
                this.objects.add(Integer.valueOf(parseInt));
                this.all = false;
            } catch (RuntimeException unused) {
                if (this.strings == null) {
                    this.strings = new ArrayList<>();
                }
                this.strings.add(str);
                this.all = false;
            }
        }

        public int build(String[] strArr, int i) {
            while (i < strArr.length) {
                String str = strArr[i];
                if ("--".equals(str)) {
                    return i + 1;
                }
                build(str);
                i++;
            }
            return i;
        }

        public boolean match(Object obj, ComponentName componentName) {
            if (this.all) {
                return true;
            }
            if (this.components != null) {
                for (int i = 0; i < this.components.size(); i++) {
                    if (this.components.get(i).equals(componentName)) {
                        return true;
                    }
                }
            }
            if (this.objects != null) {
                for (int i2 = 0; i2 < this.objects.size(); i2++) {
                    if (System.identityHashCode(obj) == this.objects.get(i2).intValue()) {
                        return true;
                    }
                }
            }
            if (this.strings != null) {
                String flattenToString = componentName.flattenToString();
                for (int i3 = 0; i3 < this.strings.size(); i3++) {
                    if (flattenToString.contains(this.strings.get(i3))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void writeBroadcastsToProtoLocked(ProtoOutputStream protoOutputStream) {
        if (this.mRegisteredReceivers.size() > 0) {
            for (ReceiverList receiverList : this.mRegisteredReceivers.values()) {
                receiverList.dumpDebug(protoOutputStream, 2246267895809L);
            }
        }
        this.mReceiverResolver.dumpDebug(protoOutputStream, 1146756268034L);
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.dumpDebug(protoOutputStream, 2246267895811L);
        }
        int i = 0;
        while (true) {
            long j = 1138166333441L;
            if (i < this.mStickyBroadcasts.size()) {
                long start = protoOutputStream.start(2246267895812L);
                protoOutputStream.write(1120986464257L, this.mStickyBroadcasts.keyAt(i));
                for (Map.Entry<String, ArrayList<Intent>> entry : this.mStickyBroadcasts.valueAt(i).entrySet()) {
                    long start2 = protoOutputStream.start(2246267895810L);
                    protoOutputStream.write(j, entry.getKey());
                    Iterator<Intent> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        it.next().dumpDebug(protoOutputStream, 2246267895810L, false, true, true, false);
                        start2 = start2;
                        start = start;
                    }
                    protoOutputStream.end(start2);
                    j = 1138166333441L;
                }
                protoOutputStream.end(start);
                i++;
            } else {
                long start3 = protoOutputStream.start(1146756268037L);
                protoOutputStream.write(1138166333441L, this.mHandler.toString());
                this.mHandler.getLooper().dumpDebug(protoOutputStream, 1146756268034L);
                protoOutputStream.end(start3);
                return;
            }
        }
    }

    public void dumpAllowedAssociationsLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        printWriter.println("ACTIVITY MANAGER ALLOWED ASSOCIATION STATE (dumpsys activity allowed-associations)");
        boolean z2 = false;
        if (this.mAllowedAssociations != null) {
            boolean z3 = false;
            for (int i2 = 0; i2 < this.mAllowedAssociations.size(); i2++) {
                String keyAt = this.mAllowedAssociations.keyAt(i2);
                ArraySet<String> allowedPackageAssociations = this.mAllowedAssociations.valueAt(i2).getAllowedPackageAssociations();
                if (!z3) {
                    printWriter.println("  Allowed associations (by restricted package):");
                    z3 = true;
                }
                printWriter.print("  * ");
                printWriter.print(keyAt);
                printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                for (int i3 = 0; i3 < allowedPackageAssociations.size(); i3++) {
                    if (str == null || keyAt.equals(str) || allowedPackageAssociations.valueAt(i3).equals(str)) {
                        printWriter.print("      Allow: ");
                        printWriter.println(allowedPackageAssociations.valueAt(i3));
                    }
                }
                if (this.mAllowedAssociations.valueAt(i2).isDebuggable()) {
                    printWriter.println("      (debuggable)");
                }
            }
            z2 = z3;
        }
        if (z2) {
            return;
        }
        printWriter.println("  (No association restrictions)");
    }

    @NeverCompile
    public void dumpBroadcastsLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        boolean z2;
        boolean z3;
        String str2;
        int i2;
        boolean z4;
        boolean z5;
        boolean z6;
        BroadcastQueue[] broadcastQueueArr;
        ProcessRecord processRecord;
        boolean z7 = true;
        boolean z8 = true;
        String str3 = str;
        boolean z9 = true;
        if ("history".equals(str3)) {
            z2 = (i >= strArr.length || !"-s".equals(strArr[i])) ? z : false;
            str3 = null;
            z3 = true;
        } else {
            z2 = z;
            z3 = false;
        }
        if ("receivers".equals(str3)) {
            if (i + 2 <= strArr.length) {
                i2 = -1;
                int i3 = i;
                while (i3 < strArr.length) {
                    String str4 = strArr[i3];
                    str4.hashCode();
                    if (str4.equals("--uid")) {
                        int i4 = i3 + 1;
                        i2 = getIntArg(printWriter, strArr, i4, -1);
                        if (i2 == -1) {
                            return;
                        }
                        i3 = i4 + 1;
                    } else {
                        printWriter.printf("Invalid argument at index %d: %s\n", Integer.valueOf(i3), str4);
                        return;
                    }
                }
                str2 = null;
            } else {
                str2 = null;
                i2 = -1;
            }
            z4 = true;
        } else {
            str2 = str3;
            i2 = -1;
            z4 = false;
        }
        printWriter.println("ACTIVITY MANAGER BROADCAST STATE (dumpsys activity broadcasts)");
        String str5 = "    ";
        if (z3 || !z2) {
            z5 = false;
            z6 = false;
        } else {
            if (this.mRegisteredReceivers.size() > 0) {
                boolean z10 = false;
                boolean z11 = false;
                boolean z12 = false;
                for (ReceiverList receiverList : this.mRegisteredReceivers.values()) {
                    if (str2 == null || ((processRecord = receiverList.app) != null && str2.equals(processRecord.info.packageName))) {
                        if (i2 == -1 || i2 == receiverList.app.uid) {
                            if (!z12) {
                                printWriter.println("  Registered Receivers:");
                                z10 = true;
                                z11 = true;
                                z12 = true;
                            }
                            printWriter.print("  * ");
                            printWriter.println(receiverList);
                            receiverList.dump(printWriter, "    ");
                        }
                    }
                }
                z5 = z10;
                z6 = z11;
            } else {
                if (z4) {
                    printWriter.println("  (no registered receivers)");
                }
                z5 = false;
                z6 = false;
            }
            if (!z4) {
                if (this.mReceiverResolver.dump(printWriter, z5 ? "\n  Receiver Resolver Table:" : "  Receiver Resolver Table:", "    ", str2, false, false)) {
                    z5 = true;
                    z6 = true;
                }
            }
        }
        if (!z4) {
            BroadcastQueue[] broadcastQueueArr2 = this.mBroadcastQueues;
            int length = broadcastQueueArr2.length;
            int i5 = 0;
            while (i5 < length) {
                z5 = broadcastQueueArr2[i5].dumpLocked(fileDescriptor, printWriter, strArr, i, z7, z8, z2, str2, z5);
                z6 |= z5;
                i5++;
                str5 = str5;
                length = length;
                broadcastQueueArr2 = broadcastQueueArr2;
                z7 = true;
                z8 = true;
            }
        }
        String str6 = str5;
        String str7 = str2;
        int i6 = 0;
        if (!z3 && !z4 && this.mStickyBroadcasts != null && str7 == null) {
            int i7 = 0;
            while (i7 < this.mStickyBroadcasts.size()) {
                printWriter.println();
                printWriter.print("  Sticky broadcasts for user ");
                printWriter.print(this.mStickyBroadcasts.keyAt(i7));
                printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                StringBuilder sb = new StringBuilder(128);
                for (Map.Entry<String, ArrayList<Intent>> entry : this.mStickyBroadcasts.valueAt(i7).entrySet()) {
                    printWriter.print("  * Sticky action ");
                    printWriter.print(entry.getKey());
                    if (z2) {
                        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        ArrayList<Intent> value = entry.getValue();
                        int size = value.size();
                        int i8 = i6;
                        while (i8 < size) {
                            sb.setLength(i6);
                            sb.append("    Intent: ");
                            int i9 = i8;
                            value.get(i8).toShortString(sb, false, true, false, false);
                            printWriter.println(sb.toString());
                            Bundle extras = value.get(i9).getExtras();
                            if (extras != null) {
                                printWriter.print("      ");
                                printWriter.println(extras.toString());
                            }
                            i8 = i9 + 1;
                            i6 = 0;
                        }
                    } else {
                        printWriter.println("");
                    }
                    i6 = 0;
                }
                i7++;
                z6 = true;
                i6 = 0;
            }
        }
        if (z3 || z4 || !z2) {
            z9 = z6;
        } else {
            printWriter.println();
            for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
                printWriter.println("  Queue " + broadcastQueue.toString() + ": " + broadcastQueue.describeStateLocked());
            }
            printWriter.println("  mHandler:");
            this.mHandler.dump(new PrintWriterPrinter(printWriter), str6);
        }
        if (z9) {
            return;
        }
        printWriter.println("  (nothing)");
    }

    @NeverCompile
    public void dumpBroadcastStatsLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        if (this.mCurBroadcastStats == null) {
            return;
        }
        printWriter.println("ACTIVITY MANAGER BROADCAST STATS STATE (dumpsys activity broadcast-stats)");
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mLastBroadcastStats != null) {
            printWriter.print("  Last stats (from ");
            TimeUtils.formatDuration(this.mLastBroadcastStats.mStartRealtime, elapsedRealtime, printWriter);
            printWriter.print(" to ");
            TimeUtils.formatDuration(this.mLastBroadcastStats.mEndRealtime, elapsedRealtime, printWriter);
            printWriter.print(", ");
            BroadcastStats broadcastStats = this.mLastBroadcastStats;
            TimeUtils.formatDuration(broadcastStats.mEndUptime - broadcastStats.mStartUptime, printWriter);
            printWriter.println(" uptime):");
            if (!this.mLastBroadcastStats.dumpStats(printWriter, "    ", str)) {
                printWriter.println("    (nothing)");
            }
            printWriter.println();
        }
        printWriter.print("  Current stats (from ");
        TimeUtils.formatDuration(this.mCurBroadcastStats.mStartRealtime, elapsedRealtime, printWriter);
        printWriter.print(" to now, ");
        TimeUtils.formatDuration(SystemClock.uptimeMillis() - this.mCurBroadcastStats.mStartUptime, printWriter);
        printWriter.println(" uptime):");
        if (this.mCurBroadcastStats.dumpStats(printWriter, "    ", str)) {
            return;
        }
        printWriter.println("    (nothing)");
    }

    @NeverCompile
    public void dumpBroadcastStatsCheckinLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        if (this.mCurBroadcastStats == null) {
            return;
        }
        BroadcastStats broadcastStats = this.mLastBroadcastStats;
        if (broadcastStats != null) {
            broadcastStats.dumpCheckinStats(printWriter, str);
            if (z) {
                this.mLastBroadcastStats = null;
                return;
            }
        }
        this.mCurBroadcastStats.dumpCheckinStats(printWriter, str);
        if (z) {
            this.mCurBroadcastStats = null;
        }
    }

    public void dumpPermissions(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        printWriter.println("ACTIVITY MANAGER URI PERMISSIONS (dumpsys activity permissions)");
        this.mUgmInternal.dump(printWriter, z, str);
    }

    public static int dumpProcessList(PrintWriter printWriter, ActivityManagerService activityManagerService, List list, String str, String str2, String str3, String str4) {
        int i = 0;
        for (int size = list.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = (ProcessRecord) list.get(size);
            if (str4 == null || str4.equals(processRecord.info.packageName)) {
                Object[] objArr = new Object[4];
                objArr[0] = str;
                objArr[1] = processRecord.isPersistent() ? str3 : str2;
                objArr[2] = Integer.valueOf(size);
                objArr[3] = processRecord.toString();
                printWriter.println(String.format("%s%s #%2d: %s", objArr));
                if (processRecord.isPersistent()) {
                    i++;
                }
            }
        }
        return i;
    }

    public ArrayList<ProcessRecord> collectProcesses(PrintWriter printWriter, int i, boolean z, String[] strArr) {
        ArrayList<ProcessRecord> collectProcessesLOSP;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                collectProcessesLOSP = this.mProcessList.collectProcessesLOSP(i, z, strArr);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return collectProcessesLOSP;
    }

    public final void dumpGraphicsHardwareUsage(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ArrayList<ProcessRecord> collectProcesses = collectProcesses(printWriter, 0, false, strArr);
        if (collectProcesses == null) {
            printWriter.println("No process found for: " + strArr[0]);
            return;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        printWriter.println("Applications Graphics Acceleration Info:");
        printWriter.println("Uptime: " + uptimeMillis + " Realtime: " + elapsedRealtime);
        for (int size = collectProcesses.size() + (-1); size >= 0; size--) {
            ProcessRecord processRecord = collectProcesses.get(size);
            int pid = processRecord.getPid();
            IApplicationThread thread = processRecord.getThread();
            if (thread != null) {
                printWriter.println("\n** Graphics info for pid " + pid + " [" + processRecord.processName + "] **");
                printWriter.flush();
                try {
                    TransferPipe transferPipe = new TransferPipe();
                    try {
                        thread.dumpGfxInfo(transferPipe.getWriteFd(), strArr);
                        transferPipe.go(fileDescriptor);
                        transferPipe.kill();
                    } catch (Throwable th) {
                        transferPipe.kill();
                        throw th;
                        break;
                    }
                } catch (RemoteException unused) {
                    printWriter.println("Got a RemoteException while dumping the app " + processRecord);
                    printWriter.flush();
                } catch (IOException unused2) {
                    printWriter.println("Failure while dumping the app: " + processRecord);
                    printWriter.flush();
                }
            }
        }
    }

    public final void dumpBinderCacheContents(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ArrayList<ProcessRecord> collectProcesses = collectProcesses(printWriter, 0, false, strArr);
        if (collectProcesses == null) {
            printWriter.println("No process found for: " + strArr[0]);
            return;
        }
        printWriter.println("Per-process Binder Cache Contents");
        for (int size = collectProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = collectProcesses.get(size);
            int pid = processRecord.getPid();
            IApplicationThread thread = processRecord.getThread();
            if (thread != null) {
                printWriter.println("\n\n** Cache info for pid " + pid + " [" + processRecord.processName + "] **");
                printWriter.flush();
                try {
                    TransferPipe transferPipe = new TransferPipe();
                    try {
                        thread.dumpCacheInfo(transferPipe.getWriteFd(), strArr);
                        transferPipe.go(fileDescriptor);
                        transferPipe.kill();
                    } catch (Throwable th) {
                        transferPipe.kill();
                        throw th;
                        break;
                    }
                } catch (RemoteException unused) {
                    printWriter.println("Got a RemoteException while dumping the app " + processRecord);
                    printWriter.flush();
                } catch (IOException unused2) {
                    printWriter.println("Failure while dumping the app " + processRecord);
                    printWriter.flush();
                }
            }
        }
    }

    public final void dumpDbInfo(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ArrayList<ProcessRecord> collectProcesses = collectProcesses(printWriter, 0, false, strArr);
        if (collectProcesses == null) {
            printWriter.println("No process found for: " + strArr[0]);
            return;
        }
        printWriter.println("Applications Database Info:");
        for (int size = collectProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = collectProcesses.get(size);
            int pid = processRecord.getPid();
            IApplicationThread thread = processRecord.getThread();
            if (thread != null) {
                printWriter.println("\n** Database info for pid " + pid + " [" + processRecord.processName + "] **");
                printWriter.flush();
                try {
                    TransferPipe transferPipe = new TransferPipe();
                    try {
                        thread.dumpDbInfo(transferPipe.getWriteFd(), strArr);
                        transferPipe.go(fileDescriptor);
                        transferPipe.kill();
                    } catch (Throwable th) {
                        transferPipe.kill();
                        throw th;
                        break;
                    }
                } catch (RemoteException unused) {
                    printWriter.println("Got a RemoteException while dumping the app " + processRecord);
                    printWriter.flush();
                } catch (IOException unused2) {
                    printWriter.println("Failure while dumping the app: " + processRecord);
                    printWriter.flush();
                }
            }
        }
    }

    /* renamed from: com.android.server.am.ActivityManagerService$MemItem */
    /* loaded from: classes.dex */
    public static final class MemItem {
        public final boolean hasActivities;

        /* renamed from: id */
        public final int f1118id;
        public final boolean isProc;
        public final String label;
        public final long mRss;
        public final long pss;
        public final String shortLabel;
        public ArrayList<MemItem> subitems;
        public final long swapPss;
        public final int userId;

        public MemItem(String str, String str2, long j, long j2, long j3, int i, int i2, boolean z) {
            this.isProc = true;
            this.label = str;
            this.shortLabel = str2;
            this.pss = j;
            this.swapPss = j2;
            this.mRss = j3;
            this.f1118id = i;
            this.userId = i2;
            this.hasActivities = z;
        }

        public MemItem(String str, String str2, long j, long j2, long j3, int i) {
            this.isProc = false;
            this.label = str;
            this.shortLabel = str2;
            this.pss = j;
            this.swapPss = j2;
            this.mRss = j3;
            this.f1118id = i;
            this.userId = 0;
            this.hasActivities = false;
        }
    }

    public static void sortMemItems(List<MemItem> list, final boolean z) {
        Collections.sort(list, new Comparator<MemItem>() { // from class: com.android.server.am.ActivityManagerService.17
            @Override // java.util.Comparator
            public int compare(MemItem memItem, MemItem memItem2) {
                boolean z2 = z;
                int i = ((z2 ? memItem.pss : memItem.mRss) > (z2 ? memItem2.pss : memItem2.mRss) ? 1 : ((z2 ? memItem.pss : memItem.mRss) == (z2 ? memItem2.pss : memItem2.mRss) ? 0 : -1));
                if (i < 0) {
                    return 1;
                }
                return i > 0 ? -1 : 0;
            }
        });
    }

    public static final void dumpMemItems(PrintWriter printWriter, String str, String str2, ArrayList<MemItem> arrayList, boolean z, boolean z2, boolean z3, boolean z4) {
        if (z && !z2) {
            sortMemItems(arrayList, z3);
        }
        for (int i = 0; i < arrayList.size(); i++) {
            MemItem memItem = arrayList.get(i);
            if (z2) {
                if (memItem.isProc) {
                    printWriter.print("proc,");
                    printWriter.print(str2);
                    printWriter.print(",");
                    printWriter.print(memItem.shortLabel);
                    printWriter.print(",");
                    printWriter.print(memItem.f1118id);
                    printWriter.print(",");
                    printWriter.print(z3 ? memItem.pss : memItem.mRss);
                    printWriter.print(",");
                    printWriter.print(z4 ? Long.valueOf(memItem.swapPss) : "N/A");
                    printWriter.println(memItem.hasActivities ? ",a" : ",e");
                } else {
                    printWriter.print(str2);
                    printWriter.print(",");
                    printWriter.print(memItem.shortLabel);
                    printWriter.print(",");
                    printWriter.print(z3 ? memItem.pss : memItem.mRss);
                    printWriter.print(",");
                    printWriter.println(z4 ? Long.valueOf(memItem.swapPss) : "N/A");
                }
            } else if (z3 && z4) {
                printWriter.printf("%s%s: %-60s (%s in swap)\n", str, stringifyKBSize(memItem.pss), memItem.label, stringifyKBSize(memItem.swapPss));
            } else {
                Object[] objArr = new Object[4];
                objArr[0] = str;
                objArr[1] = stringifyKBSize(z3 ? memItem.pss : memItem.mRss);
                objArr[2] = memItem.label;
                objArr[3] = memItem.userId != 0 ? " (user " + memItem.userId + ")" : "";
                printWriter.printf("%s%s: %s%s\n", objArr);
            }
            if (memItem.subitems != null) {
                dumpMemItems(printWriter, str + "    ", memItem.shortLabel, memItem.subitems, true, z2, z3, z4);
            }
        }
    }

    public static final void dumpMemItems(ProtoOutputStream protoOutputStream, long j, String str, ArrayList<MemItem> arrayList, boolean z, boolean z2, boolean z3) {
        if (z) {
            sortMemItems(arrayList, z2);
        }
        for (int i = 0; i < arrayList.size(); i++) {
            MemItem memItem = arrayList.get(i);
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1138166333441L, str);
            protoOutputStream.write(1138166333442L, memItem.shortLabel);
            protoOutputStream.write(1133871366148L, memItem.isProc);
            protoOutputStream.write(1120986464259L, memItem.f1118id);
            protoOutputStream.write(1133871366149L, memItem.hasActivities);
            protoOutputStream.write(1112396529670L, memItem.pss);
            protoOutputStream.write(1112396529673L, memItem.mRss);
            if (z3) {
                protoOutputStream.write(1112396529671L, memItem.swapPss);
            }
            ArrayList<MemItem> arrayList2 = memItem.subitems;
            if (arrayList2 != null) {
                dumpMemItems(protoOutputStream, 2246267895816L, memItem.shortLabel, arrayList2, true, z2, z3);
            }
            protoOutputStream.end(start);
        }
    }

    public static final void appendMemBucket(StringBuilder sb, long j, String str, boolean z) {
        int lastIndexOf = str.lastIndexOf(46);
        int i = 0;
        int i2 = lastIndexOf >= 0 ? lastIndexOf + 1 : 0;
        int length = str.length();
        while (true) {
            long[] jArr = DUMP_MEM_BUCKETS;
            if (i < jArr.length) {
                long j2 = jArr[i];
                if (j2 >= j) {
                    sb.append(j2 / 1024);
                    sb.append(z ? "MB." : "MB ");
                    sb.append((CharSequence) str, i2, length);
                    return;
                }
                i++;
            } else {
                sb.append(j / 1024);
                sb.append(z ? "MB." : "MB ");
                sb.append((CharSequence) str, i2, length);
                return;
            }
        }
    }

    public final void dumpApplicationMemoryUsageHeader(PrintWriter printWriter, long j, long j2, boolean z, boolean z2) {
        if (z2) {
            printWriter.print("version,");
            printWriter.println(1);
        }
        if (z || z2) {
            printWriter.print("time,");
            printWriter.print(j);
            printWriter.print(",");
            printWriter.println(j2);
            return;
        }
        printWriter.println("Applications Memory Usage (in Kilobytes):");
        printWriter.println("Uptime: " + j + " Realtime: " + j2);
    }

    public static final long[] getKsmInfo() {
        int[] iArr = {8224};
        Process.readProcFile("/sys/kernel/mm/ksm/pages_shared", iArr, null, r3, null);
        long[] jArr = {0};
        Process.readProcFile("/sys/kernel/mm/ksm/pages_sharing", iArr, null, jArr, null);
        jArr[0] = 0;
        Process.readProcFile("/sys/kernel/mm/ksm/pages_unshared", iArr, null, jArr, null);
        jArr[0] = 0;
        Process.readProcFile("/sys/kernel/mm/ksm/pages_volatile", iArr, null, jArr, null);
        return new long[]{(jArr[0] * 4096) / 1024, (jArr[0] * 4096) / 1024, (jArr[0] * 4096) / 1024, (jArr[0] * 4096) / 1024};
    }

    public static String stringifySize(long j, int i) {
        Locale locale = Locale.US;
        if (i != 1) {
            if (i != 1024) {
                if (i != 1048576) {
                    if (i == 1073741824) {
                        return String.format(locale, "%,1dG", Long.valueOf(((j / 1024) / 1024) / 1024));
                    }
                    throw new IllegalArgumentException("Invalid size order");
                }
                return String.format(locale, "%,5dM", Long.valueOf((j / 1024) / 1024));
            }
            return String.format(locale, "%,9dK", Long.valueOf(j / 1024));
        }
        return String.format(locale, "%,13d", Long.valueOf(j));
    }

    public static String stringifyKBSize(long j) {
        return stringifySize(j * 1024, 1024);
    }

    /* renamed from: com.android.server.am.ActivityManagerService$MemoryUsageDumpOptions */
    /* loaded from: classes.dex */
    public static class MemoryUsageDumpOptions {
        public boolean dumpDalvik;
        public boolean dumpDetails;
        public boolean dumpFullDetails;
        public boolean dumpProto;
        public boolean dumpSummaryOnly;
        public boolean dumpSwapPss;
        public boolean dumpUnreachable;
        public boolean isCheckinRequest;
        public boolean isCompact;
        public boolean localOnly;
        public boolean oomOnly;
        public boolean packages;

        public MemoryUsageDumpOptions() {
        }
    }

    @NeverCompile
    public final void dumpApplicationMemoryUsage(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr, boolean z, PrintWriter printWriter2, boolean z2) {
        String str2;
        MemoryUsageDumpOptions memoryUsageDumpOptions = new MemoryUsageDumpOptions();
        memoryUsageDumpOptions.dumpDetails = false;
        memoryUsageDumpOptions.dumpFullDetails = false;
        memoryUsageDumpOptions.dumpDalvik = false;
        memoryUsageDumpOptions.dumpSummaryOnly = false;
        memoryUsageDumpOptions.dumpUnreachable = false;
        memoryUsageDumpOptions.oomOnly = false;
        memoryUsageDumpOptions.isCompact = false;
        memoryUsageDumpOptions.localOnly = false;
        memoryUsageDumpOptions.packages = false;
        memoryUsageDumpOptions.isCheckinRequest = false;
        memoryUsageDumpOptions.dumpSwapPss = false;
        memoryUsageDumpOptions.dumpProto = z2;
        int i = 0;
        while (i < strArr.length && (str2 = strArr[i]) != null && str2.length() > 0 && str2.charAt(0) == '-') {
            i++;
            if ("-a".equals(str2)) {
                memoryUsageDumpOptions.dumpDetails = true;
                memoryUsageDumpOptions.dumpFullDetails = true;
                memoryUsageDumpOptions.dumpDalvik = true;
                memoryUsageDumpOptions.dumpSwapPss = true;
            } else if ("-d".equals(str2)) {
                memoryUsageDumpOptions.dumpDalvik = true;
            } else if ("-c".equals(str2)) {
                memoryUsageDumpOptions.isCompact = true;
            } else if ("-s".equals(str2)) {
                memoryUsageDumpOptions.dumpDetails = true;
                memoryUsageDumpOptions.dumpSummaryOnly = true;
            } else if ("-S".equals(str2)) {
                memoryUsageDumpOptions.dumpSwapPss = true;
            } else if ("--unreachable".equals(str2)) {
                memoryUsageDumpOptions.dumpUnreachable = true;
            } else if ("--oom".equals(str2)) {
                memoryUsageDumpOptions.oomOnly = true;
            } else if ("--local".equals(str2)) {
                memoryUsageDumpOptions.localOnly = true;
            } else if ("--package".equals(str2)) {
                memoryUsageDumpOptions.packages = true;
            } else if ("--checkin".equals(str2)) {
                memoryUsageDumpOptions.isCheckinRequest = true;
            } else if ("--proto".equals(str2)) {
                memoryUsageDumpOptions.dumpProto = true;
            } else if ("-h".equals(str2)) {
                printWriter.println("meminfo dump options: [-a] [-d] [-c] [-s] [--oom] [process]");
                printWriter.println("  -a: include all available information for each process.");
                printWriter.println("  -d: include dalvik details.");
                printWriter.println("  -c: dump in a compact machine-parseable representation.");
                printWriter.println("  -s: dump only summary of application memory usage.");
                printWriter.println("  -S: dump also SwapPss.");
                printWriter.println("  --oom: only show processes organized by oom adj.");
                printWriter.println("  --local: only collect details locally, don't call process.");
                printWriter.println("  --package: interpret process arg as package, dumping all");
                printWriter.println("             processes that have loaded that package.");
                printWriter.println("  --checkin: dump data for a checkin");
                printWriter.println("  --proto: dump data to proto");
                printWriter.println("If [process] is specified it can be the name or ");
                printWriter.println("pid of a specific process to dump.");
                return;
            } else {
                printWriter.println("Unknown argument: " + str2 + "; use -h for help");
            }
        }
        String[] strArr2 = new String[strArr.length - i];
        System.arraycopy(strArr, i, strArr2, 0, strArr.length - i);
        ArrayList<ProcessRecord> collectProcesses = collectProcesses(printWriter, i, memoryUsageDumpOptions.packages, strArr);
        if (memoryUsageDumpOptions.dumpProto) {
            dumpApplicationMemoryUsage(fileDescriptor, memoryUsageDumpOptions, strArr2, z, collectProcesses);
        } else {
            dumpApplicationMemoryUsage(fileDescriptor, printWriter, str, memoryUsageDumpOptions, strArr2, z, collectProcesses, printWriter2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x0099, code lost:
        if (android.os.Debug.getMemoryInfo(r11, r1) == false) goto L36;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:107:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x03c2  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x06d2 A[LOOP:7: B:221:0x06d0->B:222:0x06d2, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:363:0x03f6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r21v1 */
    /* JADX WARN: Type inference failed for: r21v2 */
    /* JADX WARN: Type inference failed for: r21v3 */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void dumpApplicationMemoryUsage(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, final MemoryUsageDumpOptions memoryUsageDumpOptions, final String[] strArr, final boolean z, ArrayList<ProcessRecord> arrayList, PrintWriter printWriter2) {
        PrintWriter printWriter3;
        MemoryUsageDumpOptions memoryUsageDumpOptions2;
        ?? r21;
        char c;
        char c2;
        ArrayList arrayList2;
        long[] jArr;
        PrintWriter printWriter4;
        MemoryUsageDumpOptions memoryUsageDumpOptions3;
        MemoryUsageDumpOptions memoryUsageDumpOptions4;
        PrintWriter printWriter5;
        PrintWriter printWriter6;
        MemoryUsageDumpOptions memoryUsageDumpOptions5;
        int i;
        int i2;
        IApplicationThread thread;
        int i3;
        int pid;
        long[] jArr2;
        int setAdjWithServices;
        boolean hasActivities;
        SparseArray sparseArray;
        long[] jArr3;
        long[] jArr4;
        long[] jArr5;
        ArrayList arrayList3;
        long[] jArr6;
        long[] jArr7;
        long[] jArr8;
        long[] jArr9;
        long[] jArr10;
        int i4;
        long[] jArr11;
        long[] jArr12;
        int i5;
        int i6;
        long otherPrivate;
        long otherPrivate2;
        long j;
        long j2;
        boolean z2;
        int i7;
        int i8;
        long[] jArr13;
        Debug.MemoryInfo memoryInfo;
        int i9;
        MemoryUsageDumpOptions memoryUsageDumpOptions6;
        int i10;
        char c3;
        Debug.MemoryInfo memoryInfo2;
        TransferPipe transferPipe;
        final int i11;
        Debug.MemoryInfo memoryInfo3;
        int i12;
        long[] jArr14;
        MemoryUsageDumpOptions memoryUsageDumpOptions7;
        ArrayList arrayList4;
        PrintWriter printWriter7 = printWriter;
        MemoryUsageDumpOptions memoryUsageDumpOptions8 = memoryUsageDumpOptions;
        ArrayList<ProcessRecord> arrayList5 = arrayList;
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long[] jArr15 = new long[3];
        if (arrayList5 == null) {
            String str2 = "N/A";
            if (strArr.length > 0) {
                str2 = strArr[0];
                if (str2.charAt(0) != '-') {
                    final ArrayList arrayList6 = new ArrayList();
                    updateCpuStatsNow();
                    try {
                        i11 = Integer.parseInt(strArr[0]);
                    } catch (NumberFormatException unused) {
                        i11 = -1;
                    }
                    this.mAppProfiler.forAllCpuStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda27
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityManagerService.lambda$dumpApplicationMemoryUsage$15(i11, strArr, arrayList6, (ProcessCpuTracker.Stats) obj);
                        }
                    });
                    if (arrayList6.size() > 0) {
                        dumpApplicationMemoryUsageHeader(printWriter, uptimeMillis, elapsedRealtime, memoryUsageDumpOptions8.isCheckinRequest, memoryUsageDumpOptions8.isCompact);
                        int size = arrayList6.size() - 1;
                        Debug.MemoryInfo memoryInfo4 = null;
                        while (size >= 0) {
                            ProcessCpuTracker.Stats stats = (ProcessCpuTracker.Stats) arrayList6.get(size);
                            int i13 = stats.pid;
                            if (memoryInfo4 == null) {
                                memoryInfo4 = new Debug.MemoryInfo();
                            }
                            if (!memoryUsageDumpOptions8.dumpDetails && (z || memoryUsageDumpOptions8.oomOnly)) {
                                long pss = Debug.getPss(i13, jArr15, null);
                                if (pss != 0) {
                                    memoryInfo4.nativePss = (int) pss;
                                    memoryInfo4.nativePrivateDirty = (int) jArr15[0];
                                    memoryInfo4.nativeRss = (int) jArr15[2];
                                    if (!memoryUsageDumpOptions8.isCheckinRequest && memoryUsageDumpOptions8.dumpDetails) {
                                        printWriter7.println("\n** MEMINFO in pid " + i13 + " [" + stats.baseName + "] **");
                                    }
                                    arrayList4 = arrayList6;
                                    memoryInfo3 = memoryInfo4;
                                    jArr14 = jArr15;
                                    i12 = size;
                                    ActivityThread.dumpMemInfoTable(printWriter, memoryInfo3, memoryUsageDumpOptions8.isCheckinRequest, memoryUsageDumpOptions8.dumpFullDetails, memoryUsageDumpOptions8.dumpDalvik, memoryUsageDumpOptions8.dumpSummaryOnly, i13, stats.baseName, 0L, 0L, 0L, 0L, 0L, 0L);
                                    memoryUsageDumpOptions7 = memoryUsageDumpOptions;
                                    if (memoryUsageDumpOptions7.isCheckinRequest) {
                                        printWriter.println();
                                    }
                                }
                                memoryInfo3 = memoryInfo4;
                                i12 = size;
                                jArr14 = jArr15;
                                memoryUsageDumpOptions7 = memoryUsageDumpOptions8;
                                arrayList4 = arrayList6;
                            }
                            size = i12 - 1;
                            memoryUsageDumpOptions8 = memoryUsageDumpOptions7;
                            arrayList6 = arrayList4;
                            memoryInfo4 = memoryInfo3;
                            jArr15 = jArr14;
                            printWriter7 = printWriter;
                        }
                        return;
                    }
                }
            }
            printWriter.println("No process found for: " + str2);
            return;
        }
        long[] jArr16 = jArr15;
        MemoryUsageDumpOptions memoryUsageDumpOptions9 = memoryUsageDumpOptions8;
        PrintWriter printWriter8 = printWriter7;
        if (!z && !memoryUsageDumpOptions9.oomOnly && (arrayList.size() == 1 || memoryUsageDumpOptions9.isCheckinRequest || memoryUsageDumpOptions9.packages)) {
            memoryUsageDumpOptions9.dumpDetails = true;
        }
        int size2 = arrayList.size();
        boolean z3 = (memoryUsageDumpOptions9.isCheckinRequest || size2 <= 1 || memoryUsageDumpOptions9.packages) ? false : true;
        if (z3) {
            updateCpuStatsNow();
        }
        dumpApplicationMemoryUsageHeader(printWriter, uptimeMillis, elapsedRealtime, memoryUsageDumpOptions9.isCheckinRequest, memoryUsageDumpOptions9.isCompact);
        ArrayList arrayList7 = new ArrayList();
        SparseArray sparseArray2 = new SparseArray();
        long[] jArr17 = new long[15];
        boolean z4 = memoryUsageDumpOptions9.dumpDalvik;
        long[] jArr18 = z4 ? new long[15] : EmptyArray.LONG;
        long[] jArr19 = z4 ? new long[15] : EmptyArray.LONG;
        long[] jArr20 = z4 ? new long[15] : EmptyArray.LONG;
        final long[] jArr21 = new long[17];
        long[] jArr22 = new long[17];
        final long[] jArr23 = new long[17];
        long[] jArr24 = new long[4];
        String[] strArr2 = DUMP_MEM_OOM_LABEL;
        int length = strArr2.length;
        final long[] jArr25 = jArr20;
        final long[] jArr26 = new long[length];
        long[] jArr27 = new long[strArr2.length];
        long[] jArr28 = new long[strArr2.length];
        final ArrayList<MemItem>[] arrayListArr = new ArrayList[strArr2.length];
        long j3 = 0;
        boolean z5 = false;
        int i14 = size2 - 1;
        Debug.MemoryInfo memoryInfo5 = null;
        while (true) {
            int i15 = length;
            if (i14 < 0) {
                final SparseArray sparseArray3 = sparseArray2;
                final long[] jArr29 = jArr24;
                final long[] jArr30 = jArr19;
                final long[] jArr31 = jArr17;
                final ArrayList arrayList8 = arrayList7;
                final long[] jArr32 = jArr22;
                final long[] jArr33 = jArr27;
                final long[] jArr34 = jArr16;
                final long[] jArr35 = jArr18;
                final long[] jArr36 = jArr28;
                if (z3) {
                    final Debug.MemoryInfo[] memoryInfoArr = new Debug.MemoryInfo[1];
                    this.mAppProfiler.forAllCpuStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda29
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityManagerService.lambda$dumpApplicationMemoryUsage$17(sparseArray3, memoryInfoArr, z, memoryUsageDumpOptions, jArr34, jArr29, jArr31, arrayList8, jArr35, jArr30, jArr25, jArr21, jArr32, jArr23, jArr26, jArr33, arrayListArr, jArr36, (ProcessCpuTracker.Stats) obj);
                        }
                    });
                    ArrayList arrayList9 = new ArrayList();
                    arrayList9.add(new MemItem("Native", "Native", jArr31[0], jArr31[1], jArr31[2], -1));
                    arrayList9.add(new MemItem("Dalvik", "Dalvik", jArr31[3], jArr31[4], jArr31[5], -2));
                    arrayList9.add(new MemItem("Unknown", "Unknown", jArr31[6], jArr31[7], jArr31[8], -3));
                    for (int i16 = 0; i16 < 17; i16++) {
                        String otherLabel = Debug.MemoryInfo.getOtherLabel(i16);
                        arrayList9.add(new MemItem(otherLabel, otherLabel, jArr21[i16], jArr32[i16], jArr23[i16], i16));
                    }
                    if (jArr35.length > 0) {
                        Iterator it = arrayList9.iterator();
                        while (it.hasNext()) {
                            MemItem memItem = (MemItem) it.next();
                            int i17 = memItem.f1118id;
                            if (i17 == -2) {
                                i = 0;
                                i2 = 3;
                            } else if (i17 == 0) {
                                i = 4;
                                i2 = 9;
                            } else {
                                if (i17 == 10) {
                                    i = 10;
                                    i2 = 12;
                                } else if (i17 == 12) {
                                    i = 13;
                                    i2 = 14;
                                }
                                memItem.subitems = new ArrayList<>();
                                while (i <= i2) {
                                    String otherLabel2 = Debug.MemoryInfo.getOtherLabel(i + 17);
                                    memItem.subitems.add(new MemItem(otherLabel2, otherLabel2, jArr35[i], jArr30[i], jArr25[i], i));
                                    i++;
                                }
                            }
                            memItem.subitems = new ArrayList<>();
                            while (i <= i2) {
                            }
                        }
                    }
                    ArrayList arrayList10 = new ArrayList();
                    for (int i18 = 0; i18 < i15; i18++) {
                        long j4 = jArr26[i18];
                        if (j4 != 0) {
                            String str3 = memoryUsageDumpOptions.isCompact ? DUMP_MEM_OOM_COMPACT_LABEL[i18] : DUMP_MEM_OOM_LABEL[i18];
                            MemItem memItem2 = new MemItem(str3, str3, j4, jArr33[i18], jArr36[i18], DUMP_MEM_OOM_ADJ[i18]);
                            memItem2.subitems = arrayListArr[i18];
                            arrayList10.add(memItem2);
                        }
                    }
                    if (!memoryUsageDumpOptions.isCompact) {
                        printWriter.println();
                    }
                    if (z || memoryUsageDumpOptions.oomOnly || memoryUsageDumpOptions.isCompact) {
                        printWriter3 = printWriter;
                        memoryUsageDumpOptions2 = memoryUsageDumpOptions;
                        r21 = 1;
                        c = '\n';
                        c2 = 14;
                        arrayList2 = arrayList8;
                        jArr = jArr31;
                    } else {
                        printWriter.println();
                        printWriter.println("Total RSS by process:");
                        memoryUsageDumpOptions2 = memoryUsageDumpOptions;
                        r21 = 1;
                        c = '\n';
                        c2 = 14;
                        jArr = jArr31;
                        printWriter3 = printWriter;
                        arrayList2 = arrayList8;
                        dumpMemItems(printWriter, "  ", "proc", arrayList8, true, memoryUsageDumpOptions.isCompact, false, false);
                        printWriter.println();
                    }
                    if (!memoryUsageDumpOptions2.isCompact) {
                        printWriter3.println("Total RSS by OOM adjustment:");
                    }
                    dumpMemItems(printWriter, "  ", "oom", arrayList10, false, memoryUsageDumpOptions2.isCompact, false, false);
                    if (z || memoryUsageDumpOptions2.oomOnly) {
                        printWriter4 = printWriter3;
                        memoryUsageDumpOptions3 = memoryUsageDumpOptions2;
                    } else {
                        PrintWriter printWriter9 = printWriter2 != null ? printWriter2 : printWriter3;
                        if (!memoryUsageDumpOptions2.isCompact) {
                            printWriter9.println();
                            printWriter9.println("Total RSS by category:");
                        }
                        printWriter4 = printWriter3;
                        memoryUsageDumpOptions3 = memoryUsageDumpOptions2;
                        dumpMemItems(printWriter9, "  ", "cat", arrayList9, true, memoryUsageDumpOptions2.isCompact, false, false);
                    }
                    memoryUsageDumpOptions3.dumpSwapPss = (memoryUsageDumpOptions3.dumpSwapPss && z5 && jArr[c] != 0) ? r21 : false;
                    if (z || memoryUsageDumpOptions3.oomOnly || memoryUsageDumpOptions3.isCompact) {
                        memoryUsageDumpOptions4 = memoryUsageDumpOptions3;
                        printWriter5 = printWriter4;
                    } else {
                        printWriter.println();
                        printWriter4.println("Total PSS by process:");
                        memoryUsageDumpOptions4 = memoryUsageDumpOptions3;
                        printWriter5 = printWriter4;
                        dumpMemItems(printWriter, "  ", "proc", arrayList2, true, memoryUsageDumpOptions3.isCompact, true, memoryUsageDumpOptions3.dumpSwapPss);
                        printWriter.println();
                    }
                    if (!memoryUsageDumpOptions4.isCompact) {
                        printWriter5.println("Total PSS by OOM adjustment:");
                    }
                    dumpMemItems(printWriter, "  ", "oom", arrayList10, false, memoryUsageDumpOptions4.isCompact, true, memoryUsageDumpOptions4.dumpSwapPss);
                    if (z || memoryUsageDumpOptions4.oomOnly) {
                        printWriter6 = printWriter5;
                        memoryUsageDumpOptions5 = memoryUsageDumpOptions4;
                    } else {
                        PrintWriter printWriter10 = printWriter2 != null ? printWriter2 : printWriter5;
                        if (!memoryUsageDumpOptions4.isCompact) {
                            printWriter10.println();
                            printWriter10.println("Total PSS by category:");
                        }
                        printWriter6 = printWriter5;
                        PrintWriter printWriter11 = printWriter10;
                        memoryUsageDumpOptions5 = memoryUsageDumpOptions4;
                        dumpMemItems(printWriter11, "  ", "cat", arrayList9, true, memoryUsageDumpOptions4.isCompact, true, memoryUsageDumpOptions4.dumpSwapPss);
                    }
                    if (!memoryUsageDumpOptions5.isCompact) {
                        printWriter.println();
                    }
                    MemInfoReader memInfoReader = new MemInfoReader();
                    memInfoReader.readMemInfo();
                    if (jArr[12] > 0) {
                        synchronized (this.mProcessStats.mLock) {
                            long cachedSizeKb = memInfoReader.getCachedSizeKb();
                            long freeSizeKb = memInfoReader.getFreeSizeKb();
                            long zramTotalSizeKb = memInfoReader.getZramTotalSizeKb();
                            long kernelUsedSizeKb = memInfoReader.getKernelUsedSizeKb();
                            EventLogTags.writeAmMeminfo(cachedSizeKb * 1024, freeSizeKb * 1024, zramTotalSizeKb * 1024, kernelUsedSizeKb * 1024, jArr[12] * 1024);
                            this.mProcessStats.addSysMemUsageLocked(cachedSizeKb, freeSizeKb, zramTotalSizeKb, kernelUsedSizeKb, jArr[12]);
                        }
                    }
                    if (!z) {
                        if (!memoryUsageDumpOptions5.isCompact) {
                            printWriter6.print("Total RAM: ");
                            printWriter6.print(stringifyKBSize(memInfoReader.getTotalSizeKb()));
                            printWriter6.print(" (status ");
                            this.mAppProfiler.dumpLastMemoryLevelLocked(printWriter6);
                            printWriter6.print(" Free RAM: ");
                            printWriter6.print(stringifyKBSize(j3 + memInfoReader.getCachedSizeKb() + memInfoReader.getFreeSizeKb()));
                            printWriter6.print(" (");
                            printWriter6.print(stringifyKBSize(j3));
                            printWriter6.print(" cached pss + ");
                            printWriter6.print(stringifyKBSize(memInfoReader.getCachedSizeKb()));
                            printWriter6.print(" cached kernel + ");
                            printWriter6.print(stringifyKBSize(memInfoReader.getFreeSizeKb()));
                            printWriter6.println(" free)");
                        } else {
                            printWriter6.print("ram,");
                            printWriter6.print(memInfoReader.getTotalSizeKb());
                            printWriter6.print(",");
                            printWriter6.print(j3 + memInfoReader.getCachedSizeKb() + memInfoReader.getFreeSizeKb());
                            printWriter6.print(",");
                            printWriter6.println(jArr[9] - j3);
                        }
                    }
                    long kernelUsedSizeKb2 = memInfoReader.getKernelUsedSizeKb();
                    long ionHeapsSizeKb = Debug.getIonHeapsSizeKb();
                    long ionPoolsSizeKb = Debug.getIonPoolsSizeKb();
                    long dmabufMappedSizeKb = Debug.getDmabufMappedSizeKb();
                    if (ionHeapsSizeKb >= 0 && ionPoolsSizeKb >= 0) {
                        long j5 = ionHeapsSizeKb - dmabufMappedSizeKb;
                        printWriter6.print("      ION: ");
                        printWriter6.print(stringifyKBSize(ionHeapsSizeKb + ionPoolsSizeKb));
                        printWriter6.print(" (");
                        printWriter6.print(stringifyKBSize(dmabufMappedSizeKb));
                        printWriter6.print(" mapped + ");
                        printWriter6.print(stringifyKBSize(j5));
                        printWriter6.print(" unmapped + ");
                        printWriter6.print(stringifyKBSize(ionPoolsSizeKb));
                        printWriter6.println(" pools)");
                        kernelUsedSizeKb2 += j5;
                        long j6 = jArr[9] - jArr[13];
                        jArr[9] = j6;
                        jArr[9] = j6 + dmabufMappedSizeKb;
                    } else {
                        long dmabufTotalExportedKb = Debug.getDmabufTotalExportedKb();
                        if (dmabufTotalExportedKb >= 0) {
                            long j7 = dmabufTotalExportedKb - dmabufMappedSizeKb;
                            printWriter6.print("DMA-BUF: ");
                            printWriter6.print(stringifyKBSize(dmabufTotalExportedKb));
                            printWriter6.print(" (");
                            printWriter6.print(stringifyKBSize(dmabufMappedSizeKb));
                            printWriter6.print(" mapped + ");
                            printWriter6.print(stringifyKBSize(j7));
                            printWriter6.println(" unmapped)");
                            kernelUsedSizeKb2 += j7;
                            long j8 = jArr[9] - jArr[13];
                            jArr[9] = j8;
                            jArr[9] = j8 + dmabufMappedSizeKb;
                        }
                        long dmabufHeapTotalExportedKb = Debug.getDmabufHeapTotalExportedKb();
                        if (dmabufHeapTotalExportedKb >= 0) {
                            printWriter6.print("DMA-BUF Heaps: ");
                            printWriter6.println(stringifyKBSize(dmabufHeapTotalExportedKb));
                        }
                        long dmabufHeapPoolsSizeKb = Debug.getDmabufHeapPoolsSizeKb();
                        if (dmabufHeapPoolsSizeKb >= 0) {
                            printWriter6.print("DMA-BUF Heaps pool: ");
                            printWriter6.println(stringifyKBSize(dmabufHeapPoolsSizeKb));
                        }
                    }
                    long gpuTotalUsageKb = Debug.getGpuTotalUsageKb();
                    if (gpuTotalUsageKb >= 0) {
                        long gpuPrivateMemoryKb = Debug.getGpuPrivateMemoryKb();
                        if (gpuPrivateMemoryKb >= 0) {
                            printWriter6.print("      GPU: ");
                            printWriter6.print(stringifyKBSize(gpuTotalUsageKb));
                            printWriter6.print(" (");
                            printWriter6.print(stringifyKBSize(gpuTotalUsageKb - gpuPrivateMemoryKb));
                            printWriter6.print(" dmabuf + ");
                            printWriter6.print(stringifyKBSize(gpuPrivateMemoryKb));
                            printWriter6.println(" private)");
                            jArr[9] = jArr[9] - jArr[c2];
                            kernelUsedSizeKb2 += gpuPrivateMemoryKb;
                        } else {
                            printWriter6.print("      GPU: ");
                            printWriter6.println(stringifyKBSize(gpuTotalUsageKb));
                        }
                    }
                    long totalSizeKb = ((((memInfoReader.getTotalSizeKb() - (jArr[9] - jArr[c])) - memInfoReader.getFreeSizeKb()) - memInfoReader.getCachedSizeKb()) - kernelUsedSizeKb2) - memInfoReader.getZramTotalSizeKb();
                    if (!memoryUsageDumpOptions5.isCompact) {
                        printWriter6.print(" Used RAM: ");
                        printWriter6.print(stringifyKBSize((jArr[9] - j3) + kernelUsedSizeKb2));
                        printWriter6.print(" (");
                        printWriter6.print(stringifyKBSize(jArr[9] - j3));
                        printWriter6.print(" used pss + ");
                        printWriter6.print(stringifyKBSize(kernelUsedSizeKb2));
                        printWriter6.print(" kernel)\n");
                        printWriter6.print(" Lost RAM: ");
                        printWriter6.println(stringifyKBSize(totalSizeKb));
                    } else {
                        printWriter6.print("lostram,");
                        printWriter6.println(totalSizeKb);
                    }
                    if (z) {
                        return;
                    }
                    if (memInfoReader.getZramTotalSizeKb() != 0) {
                        if (!memoryUsageDumpOptions5.isCompact) {
                            printWriter6.print("     ZRAM: ");
                            printWriter6.print(stringifyKBSize(memInfoReader.getZramTotalSizeKb()));
                            printWriter6.print(" physical used for ");
                            printWriter6.print(stringifyKBSize(memInfoReader.getSwapTotalSizeKb() - memInfoReader.getSwapFreeSizeKb()));
                            printWriter6.print(" in swap (");
                            printWriter6.print(stringifyKBSize(memInfoReader.getSwapTotalSizeKb()));
                            printWriter6.println(" total swap)");
                        } else {
                            printWriter6.print("zram,");
                            printWriter6.print(memInfoReader.getZramTotalSizeKb());
                            printWriter6.print(",");
                            printWriter6.print(memInfoReader.getSwapTotalSizeKb());
                            printWriter6.print(",");
                            printWriter6.println(memInfoReader.getSwapFreeSizeKb());
                        }
                    }
                    long[] ksmInfo = getKsmInfo();
                    if (!memoryUsageDumpOptions5.isCompact) {
                        if (ksmInfo[r21] != 0 || ksmInfo[0] != 0 || ksmInfo[2] != 0 || ksmInfo[3] != 0) {
                            printWriter6.print("      KSM: ");
                            printWriter6.print(stringifyKBSize(ksmInfo[r21]));
                            printWriter6.print(" saved from shared ");
                            printWriter6.print(stringifyKBSize(ksmInfo[0]));
                            printWriter6.print("           ");
                            printWriter6.print(stringifyKBSize(ksmInfo[2]));
                            printWriter6.print(" unshared; ");
                            printWriter6.print(stringifyKBSize(ksmInfo[3]));
                            printWriter6.println(" volatile");
                        }
                        printWriter6.print("   Tuning: ");
                        printWriter6.print(ActivityManager.staticGetMemoryClass());
                        printWriter6.print(" (large ");
                        printWriter6.print(ActivityManager.staticGetLargeMemoryClass());
                        printWriter6.print("), oom ");
                        printWriter6.print(stringifySize(this.mProcessList.getMemLevel(999), 1024));
                        printWriter6.print(", restore limit ");
                        printWriter6.print(stringifyKBSize(this.mProcessList.getCachedRestoreThresholdKb()));
                        if (ActivityManager.isLowRamDeviceStatic()) {
                            printWriter6.print(" (low-ram)");
                        }
                        if (ActivityManager.isHighEndGfx()) {
                            printWriter6.print(" (high-end-gfx)");
                        }
                        printWriter.println();
                        return;
                    }
                    printWriter6.print("ksm,");
                    printWriter6.print(ksmInfo[r21]);
                    printWriter6.print(",");
                    printWriter6.print(ksmInfo[0]);
                    printWriter6.print(",");
                    printWriter6.print(ksmInfo[2]);
                    printWriter6.print(",");
                    printWriter6.println(ksmInfo[3]);
                    printWriter6.print("tuning,");
                    printWriter6.print(ActivityManager.staticGetMemoryClass());
                    printWriter6.print(',');
                    printWriter6.print(ActivityManager.staticGetLargeMemoryClass());
                    printWriter6.print(',');
                    printWriter6.print(this.mProcessList.getMemLevel(999) / 1024);
                    if (ActivityManager.isLowRamDeviceStatic()) {
                        printWriter6.print(",low-ram");
                    }
                    if (ActivityManager.isHighEndGfx()) {
                        printWriter6.print(",high-end-gfx");
                    }
                    printWriter.println();
                    return;
                }
                return;
            }
            final ProcessRecord processRecord = arrayList5.get(i14);
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    thread = processRecord.getThread();
                    i3 = i14;
                    pid = processRecord.getPid();
                    jArr2 = jArr28;
                    setAdjWithServices = processRecord.mState.getSetAdjWithServices();
                    hasActivities = processRecord.hasActivities();
                } finally {
                }
            }
            resetPriorityAfterProcLockedSection();
            if (thread != null) {
                if (memoryInfo5 == null) {
                    memoryInfo5 = new Debug.MemoryInfo();
                }
                Debug.MemoryInfo memoryInfo6 = memoryInfo5;
                if (memoryUsageDumpOptions9.dumpDetails || (!z && !memoryUsageDumpOptions9.oomOnly)) {
                    jArr4 = jArr19;
                    jArr11 = jArr22;
                    jArr12 = jArr16;
                    i5 = setAdjWithServices;
                    long currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                    if (Debug.getMemoryInfo(pid, memoryInfo6)) {
                        long currentThreadTimeMillis2 = SystemClock.currentThreadTimeMillis();
                        boolean z6 = memoryInfo6.hasSwappedOutPss;
                        jArr3 = jArr24;
                        i6 = pid;
                        otherPrivate = memoryInfo6.getOtherPrivate(15);
                        otherPrivate2 = memoryInfo6.getOtherPrivate(14);
                        j = currentThreadTimeMillis;
                        j2 = currentThreadTimeMillis2;
                        z2 = z6;
                        i7 = 4;
                        if (memoryUsageDumpOptions9.isCheckinRequest && memoryUsageDumpOptions9.dumpDetails) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("\n** MEMINFO in pid ");
                            i8 = i6;
                            sb.append(i8);
                            sb.append(" [");
                            sb.append(processRecord.processName);
                            sb.append("] **");
                            printWriter8.println(sb.toString());
                        } else {
                            i8 = i6;
                        }
                        if (memoryUsageDumpOptions9.dumpDetails) {
                            sparseArray = sparseArray2;
                            jArr13 = jArr18;
                            jArr8 = jArr12;
                            arrayList3 = arrayList7;
                            memoryInfo = memoryInfo6;
                            i9 = i8;
                            jArr7 = jArr27;
                            memoryUsageDumpOptions6 = memoryUsageDumpOptions9;
                            jArr6 = jArr11;
                            i10 = i5;
                            jArr9 = jArr2;
                            c3 = 3;
                        } else if (memoryUsageDumpOptions9.localOnly) {
                            jArr8 = jArr12;
                            jArr6 = jArr11;
                            jArr7 = jArr27;
                            c3 = 3;
                            jArr13 = jArr18;
                            memoryUsageDumpOptions6 = memoryUsageDumpOptions9;
                            memoryInfo = memoryInfo6;
                            sparseArray = sparseArray2;
                            i9 = i8;
                            i10 = i5;
                            jArr9 = jArr2;
                            ActivityThread.dumpMemInfoTable(printWriter, memoryInfo, memoryUsageDumpOptions9.isCheckinRequest, memoryUsageDumpOptions9.dumpFullDetails, memoryUsageDumpOptions9.dumpDalvik, memoryUsageDumpOptions9.dumpSummaryOnly, i9, processRecord.processName, 0L, 0L, 0L, 0L, 0L, 0L);
                            if (memoryUsageDumpOptions6.isCheckinRequest) {
                                printWriter.println();
                            }
                            arrayList3 = arrayList7;
                        } else {
                            sparseArray = sparseArray2;
                            jArr13 = jArr18;
                            jArr8 = jArr12;
                            memoryInfo = memoryInfo6;
                            i9 = i8;
                            PrintWriter printWriter12 = printWriter8;
                            jArr7 = jArr27;
                            memoryUsageDumpOptions6 = memoryUsageDumpOptions9;
                            jArr6 = jArr11;
                            i10 = i5;
                            jArr9 = jArr2;
                            c3 = 3;
                            printWriter.flush();
                            try {
                                TransferPipe transferPipe2 = new TransferPipe();
                                try {
                                    arrayList3 = arrayList7;
                                    transferPipe = transferPipe2;
                                    try {
                                        thread.dumpMemInfo(transferPipe2.getWriteFd(), memoryInfo, memoryUsageDumpOptions6.isCheckinRequest, memoryUsageDumpOptions6.dumpFullDetails, memoryUsageDumpOptions6.dumpDalvik, memoryUsageDumpOptions6.dumpSummaryOnly, memoryUsageDumpOptions6.dumpUnreachable, strArr);
                                        try {
                                            transferPipe.go(fileDescriptor, memoryUsageDumpOptions6.dumpUnreachable ? 30000L : 5000L);
                                        } catch (Throwable th) {
                                            th = th;
                                            transferPipe.kill();
                                            throw th;
                                            break;
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    arrayList3 = arrayList7;
                                    transferPipe = transferPipe2;
                                }
                            } catch (RemoteException e) {
                                e = e;
                                arrayList3 = arrayList7;
                            } catch (IOException e2) {
                                e = e2;
                                arrayList3 = arrayList7;
                            }
                            try {
                                transferPipe.kill();
                            } catch (RemoteException e3) {
                                e = e3;
                                if (!memoryUsageDumpOptions6.isCheckinRequest) {
                                    printWriter12.println("Got RemoteException! " + e);
                                    printWriter.flush();
                                }
                                final long totalPss = memoryInfo.getTotalPss();
                                final long totalUss = memoryInfo.getTotalUss();
                                long j9 = otherPrivate2;
                                final long totalRss = memoryInfo.getTotalRss();
                                jArr5 = jArr17;
                                long totalSwappedOutPss = memoryInfo.getTotalSwappedOutPss();
                                synchronized (this.mProcLock) {
                                }
                            } catch (IOException e4) {
                                e = e4;
                                if (!memoryUsageDumpOptions6.isCheckinRequest) {
                                    printWriter12.println("Got IoException! " + e);
                                    printWriter.flush();
                                }
                                final long totalPss2 = memoryInfo.getTotalPss();
                                final long totalUss2 = memoryInfo.getTotalUss();
                                long j92 = otherPrivate2;
                                final long totalRss2 = memoryInfo.getTotalRss();
                                jArr5 = jArr17;
                                long totalSwappedOutPss2 = memoryInfo.getTotalSwappedOutPss();
                                synchronized (this.mProcLock) {
                                }
                            }
                        }
                        final long totalPss22 = memoryInfo.getTotalPss();
                        final long totalUss22 = memoryInfo.getTotalUss();
                        long j922 = otherPrivate2;
                        final long totalRss22 = memoryInfo.getTotalRss();
                        jArr5 = jArr17;
                        long totalSwappedOutPss22 = memoryInfo.getTotalSwappedOutPss();
                        synchronized (this.mProcLock) {
                            try {
                                boostPriorityForProcLockedSection();
                                if (processRecord.getThread() != null && i10 == processRecord.mState.getSetAdjWithServices()) {
                                    processRecord.mProfile.addPss(totalPss22, totalUss22, totalRss22, true, i7, j2 - j);
                                    final int i19 = i7;
                                    final long j10 = j2;
                                    final long j11 = j;
                                    processRecord.getPkgList().forEachPackageProcessStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda28
                                        @Override // java.util.function.Consumer
                                        public final void accept(Object obj) {
                                            ActivityManagerService.lambda$dumpApplicationMemoryUsage$16(ProcessRecord.this, totalPss22, totalUss22, totalRss22, i19, j10, j11, (ProcessStats.ProcessStateHolder) obj);
                                        }
                                    });
                                }
                            } finally {
                            }
                        }
                        resetPriorityAfterProcLockedSection();
                        if (memoryUsageDumpOptions6.isCheckinRequest) {
                            i4 = i15;
                            jArr10 = jArr13;
                            memoryInfo2 = memoryInfo;
                        } else {
                            jArr5[9] = jArr5[9] + totalPss22;
                            jArr5[10] = jArr5[10] + totalSwappedOutPss22;
                            jArr5[11] = jArr5[11] + totalRss22;
                            jArr5[13] = jArr5[13] + j922;
                            jArr5[14] = jArr5[14] + otherPrivate;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(processRecord.processName);
                            sb2.append(" (pid ");
                            int i20 = i9;
                            sb2.append(i20);
                            sb2.append(hasActivities ? " / activities)" : ")");
                            MemItem memItem3 = new MemItem(sb2.toString(), processRecord.processName, totalPss22, totalSwappedOutPss22, totalRss22, i20, processRecord.userId, hasActivities);
                            arrayList3.add(memItem3);
                            sparseArray.put(i20, memItem3);
                            memoryInfo2 = memoryInfo;
                            jArr5[0] = jArr5[0] + memoryInfo2.nativePss;
                            jArr5[1] = jArr5[1] + memoryInfo2.nativeSwappedOutPss;
                            jArr5[2] = jArr5[2] + memoryInfo2.nativeRss;
                            jArr5[c3] = jArr5[c3] + memoryInfo2.dalvikPss;
                            jArr5[4] = jArr5[4] + memoryInfo2.dalvikSwappedOutPss;
                            jArr5[5] = jArr5[5] + memoryInfo2.dalvikRss;
                            int i21 = 0;
                            jArr10 = jArr13;
                            while (i21 < jArr10.length) {
                                int i22 = i21 + 17;
                                jArr10[i21] = jArr10[i21] + memoryInfo2.getOtherPss(i22);
                                jArr4[i21] = jArr4[i21] + memoryInfo2.getOtherSwappedOutPss(i22);
                                jArr25[i21] = jArr25[i21] + memoryInfo2.getOtherRss(i22);
                                i21++;
                                totalRss22 = totalRss22;
                            }
                            long j12 = totalRss22;
                            jArr5[6] = jArr5[6] + memoryInfo2.otherPss;
                            jArr5[8] = jArr5[8] + memoryInfo2.otherRss;
                            jArr5[7] = jArr5[7] + memoryInfo2.otherSwappedOutPss;
                            for (int i23 = 0; i23 < 17; i23++) {
                                long otherPss = memoryInfo2.getOtherPss(i23);
                                jArr21[i23] = jArr21[i23] + otherPss;
                                jArr5[6] = jArr5[6] - otherPss;
                                long otherSwappedOutPss = memoryInfo2.getOtherSwappedOutPss(i23);
                                jArr6[i23] = jArr6[i23] + otherSwappedOutPss;
                                jArr5[7] = jArr5[7] - otherSwappedOutPss;
                                long otherRss = memoryInfo2.getOtherRss(i23);
                                jArr23[i23] = jArr23[i23] + otherRss;
                                jArr5[8] = jArr5[8] - otherRss;
                            }
                            if (i10 >= 900) {
                                j3 += totalPss22;
                            }
                            i4 = i15;
                            for (int i24 = 0; i24 < i4; i24++) {
                                if (i24 != i4 - 1) {
                                    int[] iArr = DUMP_MEM_OOM_ADJ;
                                    if (i10 < iArr[i24] || i10 >= iArr[i24 + 1]) {
                                    }
                                }
                                jArr26[i24] = jArr26[i24] + totalPss22;
                                jArr7[i24] = jArr7[i24] + totalSwappedOutPss22;
                                if (arrayListArr[i24] == null) {
                                    arrayListArr[i24] = new ArrayList<>();
                                }
                                arrayListArr[i24].add(memItem3);
                                jArr9[i24] = jArr9[i24] + j12;
                            }
                        }
                        memoryInfo5 = memoryInfo2;
                        z5 = z2;
                        printWriter8 = printWriter;
                        memoryUsageDumpOptions9 = memoryUsageDumpOptions;
                        arrayList5 = arrayList;
                        length = i4;
                        jArr18 = jArr10;
                        jArr28 = jArr9;
                        jArr19 = jArr4;
                        jArr24 = jArr3;
                        jArr22 = jArr6;
                        jArr27 = jArr7;
                        arrayList7 = arrayList3;
                        jArr17 = jArr5;
                        jArr16 = jArr8;
                        i14 = i3 - 1;
                        sparseArray2 = sparseArray;
                    }
                    sparseArray = sparseArray2;
                    jArr3 = jArr24;
                    jArr8 = jArr12;
                    jArr5 = jArr17;
                    arrayList3 = arrayList7;
                    memoryInfo5 = memoryInfo6;
                    jArr7 = jArr27;
                    jArr6 = jArr11;
                } else {
                    long currentThreadTimeMillis3 = SystemClock.currentThreadTimeMillis();
                    jArr4 = jArr19;
                    jArr11 = jArr22;
                    jArr12 = jArr16;
                    i5 = setAdjWithServices;
                    long pss2 = Debug.getPss(pid, jArr12, jArr24);
                    if (pss2 == 0) {
                        sparseArray = sparseArray2;
                        jArr3 = jArr24;
                        jArr8 = jArr12;
                        jArr5 = jArr17;
                        arrayList3 = arrayList7;
                        memoryInfo5 = memoryInfo6;
                        jArr7 = jArr27;
                        jArr6 = jArr11;
                    } else {
                        memoryInfo6.dalvikPss = (int) pss2;
                        long currentThreadTimeMillis4 = SystemClock.currentThreadTimeMillis();
                        memoryInfo6.dalvikPrivateDirty = (int) jArr12[0];
                        memoryInfo6.dalvikRss = (int) jArr12[2];
                        long j13 = jArr24[1];
                        long j14 = jArr24[2];
                        jArr3 = jArr24;
                        i6 = pid;
                        j = currentThreadTimeMillis3;
                        j2 = currentThreadTimeMillis4;
                        otherPrivate2 = j13;
                        otherPrivate = j14;
                        z2 = z5;
                        i7 = 3;
                        if (memoryUsageDumpOptions9.isCheckinRequest) {
                        }
                        i8 = i6;
                        if (memoryUsageDumpOptions9.dumpDetails) {
                        }
                        final long totalPss222 = memoryInfo.getTotalPss();
                        final long totalUss222 = memoryInfo.getTotalUss();
                        long j9222 = otherPrivate2;
                        final long totalRss222 = memoryInfo.getTotalRss();
                        jArr5 = jArr17;
                        long totalSwappedOutPss222 = memoryInfo.getTotalSwappedOutPss();
                        synchronized (this.mProcLock) {
                        }
                    }
                }
            } else {
                sparseArray = sparseArray2;
                jArr3 = jArr24;
                jArr4 = jArr19;
                jArr5 = jArr17;
                arrayList3 = arrayList7;
                jArr6 = jArr22;
                jArr7 = jArr27;
                jArr8 = jArr16;
            }
            i4 = i15;
            jArr9 = jArr2;
            jArr10 = jArr18;
            printWriter8 = printWriter;
            memoryUsageDumpOptions9 = memoryUsageDumpOptions;
            arrayList5 = arrayList;
            length = i4;
            jArr18 = jArr10;
            jArr28 = jArr9;
            jArr19 = jArr4;
            jArr24 = jArr3;
            jArr22 = jArr6;
            jArr27 = jArr7;
            arrayList7 = arrayList3;
            jArr17 = jArr5;
            jArr16 = jArr8;
            i14 = i3 - 1;
            sparseArray2 = sparseArray;
        }
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$15(int i, String[] strArr, ArrayList arrayList, ProcessCpuTracker.Stats stats) {
        String str;
        if (stats.pid == i || ((str = stats.baseName) != null && str.equals(strArr[0]))) {
            arrayList.add(stats);
        }
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$16(ProcessRecord processRecord, long j, long j2, long j3, int i, long j4, long j5, ProcessStats.ProcessStateHolder processStateHolder) {
        FrameworkStatsLog.write(18, processRecord.info.uid, processStateHolder.state.getName(), processStateHolder.state.getPackage(), j, j2, j3, i, j4 - j5, processStateHolder.appVersion, processRecord.mProfile.getCurrentHostingComponentTypes(), processRecord.mProfile.getHistoricalHostingComponentTypes());
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$17(SparseArray sparseArray, Debug.MemoryInfo[] memoryInfoArr, boolean z, MemoryUsageDumpOptions memoryUsageDumpOptions, long[] jArr, long[] jArr2, long[] jArr3, ArrayList arrayList, long[] jArr4, long[] jArr5, long[] jArr6, long[] jArr7, long[] jArr8, long[] jArr9, long[] jArr10, long[] jArr11, ArrayList[] arrayListArr, long[] jArr12, ProcessCpuTracker.Stats stats) {
        long j;
        long j2;
        if (stats.vsize <= 0 || sparseArray.indexOfKey(stats.pid) >= 0) {
            return;
        }
        if (memoryInfoArr[0] == null) {
            memoryInfoArr[0] = new Debug.MemoryInfo();
        }
        Debug.MemoryInfo memoryInfo = memoryInfoArr[0];
        if (!z && !memoryUsageDumpOptions.oomOnly) {
            if (!Debug.getMemoryInfo(stats.pid, memoryInfo)) {
                return;
            }
            j = memoryInfo.getOtherPrivate(14);
            j2 = memoryInfo.getOtherPrivate(15);
        } else {
            long pss = Debug.getPss(stats.pid, jArr, jArr2);
            if (pss == 0) {
                return;
            }
            memoryInfo.nativePss = (int) pss;
            memoryInfo.nativePrivateDirty = (int) jArr[0];
            memoryInfo.nativeRss = (int) jArr[2];
            j = jArr2[1];
            j2 = jArr2[2];
        }
        long totalPss = memoryInfo.getTotalPss();
        long totalSwappedOutPss = memoryInfo.getTotalSwappedOutPss();
        long totalRss = memoryInfo.getTotalRss();
        jArr3[9] = jArr3[9] + totalPss;
        jArr3[10] = jArr3[10] + totalSwappedOutPss;
        jArr3[11] = jArr3[11] + totalRss;
        jArr3[12] = jArr3[12] + totalPss;
        jArr3[13] = jArr3[13] + j;
        jArr3[14] = jArr3[14] + j2;
        MemItem memItem = new MemItem(stats.name + " (pid " + stats.pid + ")", stats.name, totalPss, memoryInfo.getSummaryTotalSwapPss(), totalRss, stats.pid, UserHandle.getUserId(stats.uid), false);
        arrayList.add(memItem);
        jArr3[0] = jArr3[0] + ((long) memoryInfo.nativePss);
        jArr3[1] = jArr3[1] + ((long) memoryInfo.nativeSwappedOutPss);
        jArr3[2] = jArr3[2] + ((long) memoryInfo.nativeRss);
        jArr3[3] = jArr3[3] + ((long) memoryInfo.dalvikPss);
        jArr3[4] = jArr3[4] + memoryInfo.dalvikSwappedOutPss;
        jArr3[5] = jArr3[5] + memoryInfo.dalvikRss;
        for (int i = 0; i < jArr4.length; i++) {
            int i2 = i + 17;
            jArr4[i] = jArr4[i] + memoryInfo.getOtherPss(i2);
            jArr5[i] = jArr5[i] + memoryInfo.getOtherSwappedOutPss(i2);
            jArr6[i] = jArr6[i] + memoryInfo.getOtherRss(i2);
        }
        jArr3[6] = jArr3[6] + memoryInfo.otherPss;
        jArr3[7] = jArr3[7] + memoryInfo.otherSwappedOutPss;
        jArr3[8] = jArr3[8] + memoryInfo.otherRss;
        for (int i3 = 0; i3 < 17; i3++) {
            long otherPss = memoryInfo.getOtherPss(i3);
            jArr7[i3] = jArr7[i3] + otherPss;
            jArr3[6] = jArr3[6] - otherPss;
            long otherSwappedOutPss = memoryInfo.getOtherSwappedOutPss(i3);
            jArr8[i3] = jArr8[i3] + otherSwappedOutPss;
            jArr3[7] = jArr3[7] - otherSwappedOutPss;
            long otherRss = memoryInfo.getOtherRss(i3);
            jArr9[i3] = jArr9[i3] + otherRss;
            jArr3[8] = jArr3[8] - otherRss;
        }
        jArr10[0] = jArr10[0] + totalPss;
        jArr11[0] = jArr11[0] + totalSwappedOutPss;
        if (arrayListArr[0] == null) {
            arrayListArr[0] = new ArrayList();
        }
        arrayListArr[0].add(memItem);
        jArr12[0] = jArr12[0] + totalRss;
    }

    /* JADX WARN: Removed duplicated region for block: B:123:0x0386  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x06a2 A[LOOP:7: B:195:0x06a0->B:196:0x06a2, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:256:0x03c0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0250  */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void dumpApplicationMemoryUsage(FileDescriptor fileDescriptor, final MemoryUsageDumpOptions memoryUsageDumpOptions, final String[] strArr, final boolean z, ArrayList<ProcessRecord> arrayList) {
        int i;
        ProtoOutputStream protoOutputStream;
        ActivityManagerService activityManagerService;
        long j;
        int i2;
        int i3;
        IApplicationThread thread;
        int pid;
        long[] jArr;
        int setAdjWithServices;
        boolean hasActivities;
        int i4;
        long[] jArr2;
        boolean z2;
        long j2;
        long j3;
        int i5;
        long[] jArr3;
        SparseArray sparseArray;
        ArrayList arrayList2;
        int i6;
        long[] jArr4;
        long[] jArr5;
        long[] jArr6;
        long[] jArr7;
        int i7;
        long[] jArr8;
        ProtoOutputStream protoOutputStream2;
        ProtoOutputStream protoOutputStream3;
        int i8;
        long[] jArr9;
        ByteTransferPipe byteTransferPipe;
        final int i9;
        ActivityManagerService activityManagerService2 = this;
        MemoryUsageDumpOptions memoryUsageDumpOptions2 = memoryUsageDumpOptions;
        ArrayList<ProcessRecord> arrayList3 = arrayList;
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long[] jArr10 = new long[3];
        if (arrayList3 == null) {
            if (strArr.length > 0 && strArr[0].charAt(0) != '-') {
                final ArrayList arrayList4 = new ArrayList();
                updateCpuStatsNow();
                try {
                    i9 = Integer.parseInt(strArr[0]);
                } catch (NumberFormatException unused) {
                    i9 = -1;
                }
                activityManagerService2.mAppProfiler.forAllCpuStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda33
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.lambda$dumpApplicationMemoryUsage$18(i9, strArr, arrayList4, (ProcessCpuTracker.Stats) obj);
                    }
                });
                if (arrayList4.size() > 0) {
                    ProtoOutputStream protoOutputStream4 = new ProtoOutputStream(fileDescriptor);
                    protoOutputStream4.write(1112396529665L, uptimeMillis);
                    protoOutputStream4.write(1112396529666L, elapsedRealtime);
                    Debug.MemoryInfo memoryInfo = null;
                    for (int size = arrayList4.size() - 1; size >= 0; size--) {
                        ProcessCpuTracker.Stats stats = (ProcessCpuTracker.Stats) arrayList4.get(size);
                        int i10 = stats.pid;
                        if (memoryInfo == null) {
                            memoryInfo = new Debug.MemoryInfo();
                        }
                        if (memoryUsageDumpOptions2.dumpDetails || (!z && !memoryUsageDumpOptions2.oomOnly)) {
                            if (!Debug.getMemoryInfo(i10, memoryInfo)) {
                            }
                            long start = protoOutputStream4.start(2246267895811L);
                            protoOutputStream4.write(1120986464257L, i10);
                            protoOutputStream4.write(1138166333442L, stats.baseName);
                            ActivityThread.dumpMemInfoTable(protoOutputStream4, memoryInfo, memoryUsageDumpOptions2.dumpDalvik, memoryUsageDumpOptions2.dumpSummaryOnly, 0L, 0L, 0L, 0L, 0L, 0L);
                            protoOutputStream4.end(start);
                        } else {
                            long pss = Debug.getPss(i10, jArr10, null);
                            if (pss != 0) {
                                memoryInfo.nativePss = (int) pss;
                                memoryInfo.nativePrivateDirty = (int) jArr10[0];
                                memoryInfo.nativeRss = (int) jArr10[2];
                                long start2 = protoOutputStream4.start(2246267895811L);
                                protoOutputStream4.write(1120986464257L, i10);
                                protoOutputStream4.write(1138166333442L, stats.baseName);
                                ActivityThread.dumpMemInfoTable(protoOutputStream4, memoryInfo, memoryUsageDumpOptions2.dumpDalvik, memoryUsageDumpOptions2.dumpSummaryOnly, 0L, 0L, 0L, 0L, 0L, 0L);
                                protoOutputStream4.end(start2);
                            }
                        }
                    }
                    protoOutputStream4.flush();
                    return;
                }
            }
            Log.d("ActivityManager", "No process found for: " + strArr[0]);
            return;
        }
        if (z || memoryUsageDumpOptions2.oomOnly) {
            i = 1;
        } else {
            i = 1;
            if (arrayList.size() == 1 || memoryUsageDumpOptions2.isCheckinRequest || memoryUsageDumpOptions2.packages) {
                memoryUsageDumpOptions2.dumpDetails = true;
            }
        }
        int size2 = arrayList.size();
        boolean z3 = size2 > i && !memoryUsageDumpOptions2.packages;
        if (z3) {
            updateCpuStatsNow();
        }
        ProtoOutputStream protoOutputStream5 = new ProtoOutputStream(fileDescriptor);
        protoOutputStream5.write(1112396529665L, uptimeMillis);
        protoOutputStream5.write(1112396529666L, elapsedRealtime);
        ArrayList arrayList5 = new ArrayList();
        SparseArray sparseArray2 = new SparseArray();
        long[] jArr11 = new long[15];
        boolean z4 = memoryUsageDumpOptions2.dumpDalvik;
        long[] jArr12 = z4 ? new long[15] : EmptyArray.LONG;
        final long[] jArr13 = z4 ? new long[15] : EmptyArray.LONG;
        final long[] jArr14 = z4 ? new long[15] : EmptyArray.LONG;
        long[] jArr15 = new long[17];
        final long[] jArr16 = new long[17];
        String[] strArr2 = DUMP_MEM_OOM_LABEL;
        boolean z5 = z3;
        int length = strArr2.length;
        final long[] jArr17 = new long[length];
        long[] jArr18 = new long[strArr2.length];
        long[] jArr19 = new long[17];
        long[] jArr20 = new long[strArr2.length];
        final ArrayList<MemItem>[] arrayListArr = new ArrayList[strArr2.length];
        int i11 = size2 - 1;
        long j4 = 0;
        boolean z6 = false;
        Debug.MemoryInfo memoryInfo2 = null;
        while (true) {
            int i12 = length;
            if (i11 >= 0) {
                final ProcessRecord processRecord = arrayList3.get(i11);
                long[] jArr21 = jArr20;
                synchronized (activityManagerService2.mProcLock) {
                    try {
                        boostPriorityForProcLockedSection();
                        thread = processRecord.getThread();
                        pid = processRecord.getPid();
                        jArr = jArr15;
                        setAdjWithServices = processRecord.mState.getSetAdjWithServices();
                        hasActivities = processRecord.hasActivities();
                    } finally {
                    }
                }
                resetPriorityAfterProcLockedSection();
                if (thread == null) {
                    jArr2 = jArr18;
                } else {
                    if (memoryInfo2 == null) {
                        memoryInfo2 = new Debug.MemoryInfo();
                    }
                    if (memoryUsageDumpOptions2.dumpDetails || (!z && !memoryUsageDumpOptions2.oomOnly)) {
                        i4 = setAdjWithServices;
                        jArr2 = jArr18;
                        long currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                        if (Debug.getMemoryInfo(pid, memoryInfo2)) {
                            long currentThreadTimeMillis2 = SystemClock.currentThreadTimeMillis();
                            z2 = memoryInfo2.hasSwappedOutPss;
                            j2 = currentThreadTimeMillis;
                            j3 = currentThreadTimeMillis2;
                            i5 = 4;
                            if (memoryUsageDumpOptions2.dumpDetails) {
                                jArr3 = jArr12;
                                sparseArray = sparseArray2;
                                arrayList2 = arrayList5;
                                i6 = i11;
                                jArr4 = jArr10;
                                jArr5 = jArr19;
                                jArr6 = jArr21;
                                jArr7 = jArr;
                                i7 = i4;
                                jArr8 = jArr11;
                                protoOutputStream2 = protoOutputStream5;
                            } else if (memoryUsageDumpOptions2.localOnly) {
                                long start3 = protoOutputStream5.start(2246267895812L);
                                jArr3 = jArr12;
                                long start4 = protoOutputStream5.start(1146756268033L);
                                sparseArray = sparseArray2;
                                protoOutputStream5.write(1120986464257L, pid);
                                i6 = i11;
                                protoOutputStream5.write(1138166333442L, processRecord.processName);
                                ActivityThread.dumpMemInfoTable(protoOutputStream5, memoryInfo2, memoryUsageDumpOptions2.dumpDalvik, memoryUsageDumpOptions2.dumpSummaryOnly, 0L, 0L, 0L, 0L, 0L, 0L);
                                protoOutputStream5.end(start4);
                                protoOutputStream5.end(start3);
                                jArr4 = jArr10;
                                arrayList2 = arrayList5;
                                jArr5 = jArr19;
                                jArr6 = jArr21;
                                jArr7 = jArr;
                                jArr8 = jArr11;
                                protoOutputStream2 = protoOutputStream5;
                                i7 = i4;
                            } else {
                                jArr3 = jArr12;
                                long[] jArr22 = jArr11;
                                sparseArray = sparseArray2;
                                ArrayList arrayList6 = arrayList5;
                                i6 = i11;
                                try {
                                    byteTransferPipe = new ByteTransferPipe();
                                    try {
                                        jArr5 = jArr19;
                                        jArr6 = jArr21;
                                        jArr7 = jArr;
                                        i7 = i4;
                                        jArr4 = jArr10;
                                        jArr8 = jArr22;
                                        protoOutputStream2 = protoOutputStream5;
                                        arrayList2 = arrayList6;
                                        try {
                                            thread.dumpMemInfoProto(byteTransferPipe.getWriteFd(), memoryInfo2, memoryUsageDumpOptions2.dumpFullDetails, memoryUsageDumpOptions2.dumpDalvik, memoryUsageDumpOptions2.dumpSummaryOnly, memoryUsageDumpOptions2.dumpUnreachable, strArr);
                                            protoOutputStream2.write(2246267895812L, byteTransferPipe.get());
                                        } catch (Throwable th) {
                                            th = th;
                                            byteTransferPipe.kill();
                                            throw th;
                                            break;
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                        jArr4 = jArr10;
                                        arrayList2 = arrayList6;
                                        jArr5 = jArr19;
                                        jArr6 = jArr21;
                                        jArr7 = jArr;
                                        jArr8 = jArr22;
                                        protoOutputStream2 = protoOutputStream5;
                                        i7 = i4;
                                    }
                                } catch (RemoteException e) {
                                    e = e;
                                    jArr4 = jArr10;
                                    arrayList2 = arrayList6;
                                    jArr5 = jArr19;
                                    jArr6 = jArr21;
                                    jArr7 = jArr;
                                    jArr8 = jArr22;
                                    protoOutputStream2 = protoOutputStream5;
                                    i7 = i4;
                                } catch (IOException e2) {
                                    e = e2;
                                    jArr4 = jArr10;
                                    arrayList2 = arrayList6;
                                    jArr5 = jArr19;
                                    jArr6 = jArr21;
                                    jArr7 = jArr;
                                    jArr8 = jArr22;
                                    protoOutputStream2 = protoOutputStream5;
                                    i7 = i4;
                                }
                                try {
                                    byteTransferPipe.kill();
                                } catch (RemoteException e3) {
                                    e = e3;
                                    Log.e("ActivityManager", "Got RemoteException!", e);
                                    final long totalPss = memoryInfo2.getTotalPss();
                                    final long totalUss = memoryInfo2.getTotalUss();
                                    final long totalRss = memoryInfo2.getTotalRss();
                                    long totalSwappedOutPss = memoryInfo2.getTotalSwappedOutPss();
                                    protoOutputStream3 = protoOutputStream2;
                                    synchronized (activityManagerService2.mProcLock) {
                                    }
                                } catch (IOException e4) {
                                    e = e4;
                                    Log.e("ActivityManager", "Got IOException!", e);
                                    final long totalPss2 = memoryInfo2.getTotalPss();
                                    final long totalUss2 = memoryInfo2.getTotalUss();
                                    final long totalRss2 = memoryInfo2.getTotalRss();
                                    long totalSwappedOutPss2 = memoryInfo2.getTotalSwappedOutPss();
                                    protoOutputStream3 = protoOutputStream2;
                                    synchronized (activityManagerService2.mProcLock) {
                                    }
                                }
                            }
                            final long totalPss22 = memoryInfo2.getTotalPss();
                            final long totalUss22 = memoryInfo2.getTotalUss();
                            final long totalRss22 = memoryInfo2.getTotalRss();
                            long totalSwappedOutPss22 = memoryInfo2.getTotalSwappedOutPss();
                            protoOutputStream3 = protoOutputStream2;
                            synchronized (activityManagerService2.mProcLock) {
                                try {
                                    boostPriorityForProcLockedSection();
                                    if (processRecord.getThread() != null && i7 == processRecord.mState.getSetAdjWithServices()) {
                                        processRecord.mProfile.addPss(totalPss22, totalUss22, totalRss22, true, i5, j3 - j2);
                                        final int i13 = i5;
                                        final long j5 = j3;
                                        final long j6 = j2;
                                        processRecord.getPkgList().forEachPackageProcessStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda34
                                            @Override // java.util.function.Consumer
                                            public final void accept(Object obj) {
                                                ActivityManagerService.lambda$dumpApplicationMemoryUsage$19(ProcessRecord.this, totalPss22, totalUss22, totalRss22, i13, j5, j6, (ProcessStats.ProcessStateHolder) obj);
                                            }
                                        });
                                    }
                                } finally {
                                }
                            }
                            resetPriorityAfterProcLockedSection();
                            if (memoryUsageDumpOptions2.isCheckinRequest) {
                                i8 = i12;
                                jArr9 = jArr3;
                                j4 = j4;
                            } else {
                                jArr8[9] = jArr8[9] + totalPss22;
                                jArr8[10] = jArr8[10] + totalSwappedOutPss22;
                                jArr8[11] = jArr8[11] + totalRss22;
                                StringBuilder sb = new StringBuilder();
                                sb.append(processRecord.processName);
                                sb.append(" (pid ");
                                sb.append(pid);
                                sb.append(hasActivities ? " / activities)" : ")");
                                MemItem memItem = new MemItem(sb.toString(), processRecord.processName, totalPss22, totalSwappedOutPss22, totalRss22, pid, processRecord.userId, hasActivities);
                                arrayList2.add(memItem);
                                sparseArray.put(pid, memItem);
                                jArr8[0] = jArr8[0] + memoryInfo2.nativePss;
                                jArr8[1] = jArr8[1] + memoryInfo2.nativeSwappedOutPss;
                                jArr8[2] = jArr8[2] + memoryInfo2.nativeRss;
                                jArr8[3] = jArr8[3] + memoryInfo2.dalvikPss;
                                jArr8[4] = jArr8[4] + memoryInfo2.dalvikSwappedOutPss;
                                jArr8[5] = jArr8[5] + memoryInfo2.dalvikRss;
                                int i14 = 0;
                                jArr9 = jArr3;
                                while (i14 < jArr9.length) {
                                    int i15 = i14 + 17;
                                    jArr9[i14] = jArr9[i14] + memoryInfo2.getOtherPss(i15);
                                    jArr13[i14] = jArr13[i14] + memoryInfo2.getOtherSwappedOutPss(i15);
                                    jArr14[i14] = jArr14[i14] + memoryInfo2.getOtherRss(i15);
                                    i14++;
                                    totalRss22 = totalRss22;
                                }
                                long j7 = totalRss22;
                                jArr8[6] = jArr8[6] + memoryInfo2.otherPss;
                                jArr8[8] = jArr8[8] + memoryInfo2.otherRss;
                                jArr8[7] = jArr8[7] + memoryInfo2.otherSwappedOutPss;
                                for (int i16 = 0; i16 < 17; i16++) {
                                    long otherPss = memoryInfo2.getOtherPss(i16);
                                    jArr7[i16] = jArr7[i16] + otherPss;
                                    jArr8[6] = jArr8[6] - otherPss;
                                    long otherSwappedOutPss = memoryInfo2.getOtherSwappedOutPss(i16);
                                    jArr5[i16] = jArr5[i16] + otherSwappedOutPss;
                                    jArr8[7] = jArr8[7] - otherSwappedOutPss;
                                    long otherRss = memoryInfo2.getOtherRss(i16);
                                    jArr16[i16] = jArr16[i16] + otherRss;
                                    jArr8[8] = jArr8[8] - otherRss;
                                }
                                long j8 = j4;
                                if (i7 >= 900) {
                                    j4 = j8 + totalPss22;
                                }
                                i8 = i12;
                                for (int i17 = 0; i17 < i8; i17++) {
                                    if (i17 != i8 - 1) {
                                        int[] iArr = DUMP_MEM_OOM_ADJ;
                                        if (i7 < iArr[i17] || i7 >= iArr[i17 + 1]) {
                                        }
                                    }
                                    jArr17[i17] = jArr17[i17] + totalPss22;
                                    jArr2[i17] = jArr2[i17] + totalSwappedOutPss22;
                                    if (arrayListArr[i17] == null) {
                                        arrayListArr[i17] = new ArrayList<>();
                                    }
                                    arrayListArr[i17].add(memItem);
                                    jArr6[i17] = jArr6[i17] + j7;
                                }
                            }
                            z6 = z2;
                        }
                    } else {
                        long currentThreadTimeMillis3 = SystemClock.currentThreadTimeMillis();
                        i4 = setAdjWithServices;
                        jArr2 = jArr18;
                        long pss2 = Debug.getPss(pid, jArr10, null);
                        if (pss2 != 0) {
                            memoryInfo2.dalvikPss = (int) pss2;
                            long currentThreadTimeMillis4 = SystemClock.currentThreadTimeMillis();
                            memoryInfo2.dalvikPrivateDirty = (int) jArr10[0];
                            memoryInfo2.dalvikRss = (int) jArr10[2];
                            z2 = z6;
                            j2 = currentThreadTimeMillis3;
                            j3 = currentThreadTimeMillis4;
                            i5 = 3;
                            if (memoryUsageDumpOptions2.dumpDetails) {
                            }
                            final long totalPss222 = memoryInfo2.getTotalPss();
                            final long totalUss222 = memoryInfo2.getTotalUss();
                            final long totalRss222 = memoryInfo2.getTotalRss();
                            long totalSwappedOutPss222 = memoryInfo2.getTotalSwappedOutPss();
                            protoOutputStream3 = protoOutputStream2;
                            synchronized (activityManagerService2.mProcLock) {
                            }
                        }
                    }
                    i11 = i6 - 1;
                    activityManagerService2 = this;
                    arrayList3 = arrayList;
                    length = i8;
                    jArr12 = jArr9;
                    protoOutputStream5 = protoOutputStream3;
                    jArr18 = jArr2;
                    jArr15 = jArr7;
                    jArr19 = jArr5;
                    jArr20 = jArr6;
                    jArr10 = jArr4;
                    jArr11 = jArr8;
                    sparseArray2 = sparseArray;
                    arrayList5 = arrayList2;
                    memoryUsageDumpOptions2 = memoryUsageDumpOptions;
                }
                jArr9 = jArr12;
                sparseArray = sparseArray2;
                protoOutputStream3 = protoOutputStream5;
                arrayList2 = arrayList5;
                i6 = i11;
                jArr4 = jArr10;
                jArr5 = jArr19;
                i8 = i12;
                jArr6 = jArr21;
                jArr7 = jArr;
                jArr8 = jArr11;
                i11 = i6 - 1;
                activityManagerService2 = this;
                arrayList3 = arrayList;
                length = i8;
                jArr12 = jArr9;
                protoOutputStream5 = protoOutputStream3;
                jArr18 = jArr2;
                jArr15 = jArr7;
                jArr19 = jArr5;
                jArr20 = jArr6;
                jArr10 = jArr4;
                jArr11 = jArr8;
                sparseArray2 = sparseArray;
                arrayList5 = arrayList2;
                memoryUsageDumpOptions2 = memoryUsageDumpOptions;
            } else {
                final long[] jArr23 = jArr20;
                final long[] jArr24 = jArr15;
                final long[] jArr25 = jArr18;
                final long[] jArr26 = jArr12;
                final long[] jArr27 = jArr11;
                final SparseArray sparseArray3 = sparseArray2;
                ProtoOutputStream protoOutputStream6 = protoOutputStream5;
                final ArrayList arrayList7 = arrayList5;
                final long[] jArr28 = jArr10;
                final long[] jArr29 = jArr19;
                long j9 = j4;
                if (z5) {
                    final Debug.MemoryInfo[] memoryInfoArr = new Debug.MemoryInfo[1];
                    boolean z7 = z6;
                    this.mAppProfiler.forAllCpuStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda35
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityManagerService.lambda$dumpApplicationMemoryUsage$20(sparseArray3, memoryInfoArr, z, memoryUsageDumpOptions, jArr28, jArr27, arrayList7, jArr26, jArr13, jArr14, jArr24, jArr29, jArr16, jArr17, jArr25, arrayListArr, jArr23, (ProcessCpuTracker.Stats) obj);
                        }
                    });
                    ArrayList arrayList8 = new ArrayList();
                    arrayList8.add(new MemItem("Native", "Native", jArr27[0], jArr27[1], jArr27[2], -1));
                    arrayList8.add(new MemItem("Dalvik", "Dalvik", jArr27[3], jArr27[4], jArr27[5], -2));
                    arrayList8.add(new MemItem("Unknown", "Unknown", jArr27[6], jArr27[7], jArr27[8], -3));
                    for (int i18 = 0; i18 < 17; i18++) {
                        String otherLabel = Debug.MemoryInfo.getOtherLabel(i18);
                        arrayList8.add(new MemItem(otherLabel, otherLabel, jArr24[i18], jArr29[i18], jArr16[i18], i18));
                    }
                    if (jArr26.length > 0) {
                        Iterator it = arrayList8.iterator();
                        while (it.hasNext()) {
                            MemItem memItem2 = (MemItem) it.next();
                            int i19 = memItem2.f1118id;
                            if (i19 == -2) {
                                i2 = 0;
                                i3 = 3;
                            } else if (i19 == 0) {
                                i3 = 9;
                                i2 = 4;
                            } else {
                                if (i19 == 10) {
                                    i2 = 10;
                                    i3 = 12;
                                } else if (i19 == 12) {
                                    i2 = 13;
                                    i3 = 14;
                                }
                                memItem2.subitems = new ArrayList<>();
                                while (i2 <= i3) {
                                    String otherLabel2 = Debug.MemoryInfo.getOtherLabel(i2 + 17);
                                    memItem2.subitems.add(new MemItem(otherLabel2, otherLabel2, jArr26[i2], jArr13[i2], jArr14[i2], i2));
                                    i2++;
                                }
                            }
                            memItem2.subitems = new ArrayList<>();
                            while (i2 <= i3) {
                            }
                        }
                    }
                    ArrayList arrayList9 = new ArrayList();
                    for (int i20 = 0; i20 < i12; i20++) {
                        long j10 = jArr17[i20];
                        if (j10 != 0) {
                            String str = memoryUsageDumpOptions.isCompact ? DUMP_MEM_OOM_COMPACT_LABEL[i20] : DUMP_MEM_OOM_LABEL[i20];
                            MemItem memItem3 = new MemItem(str, str, j10, jArr25[i20], jArr23[i20], DUMP_MEM_OOM_ADJ[i20]);
                            memItem3.subitems = arrayListArr[i20];
                            arrayList9.add(memItem3);
                        }
                    }
                    if (!memoryUsageDumpOptions.oomOnly) {
                        dumpMemItems(protoOutputStream6, 2246267895837L, "proc", arrayList7, true, false, false);
                    }
                    dumpMemItems(protoOutputStream6, 2246267895838L, "oom", arrayList9, false, false, false);
                    if (!z && !memoryUsageDumpOptions.oomOnly) {
                        dumpMemItems(protoOutputStream6, 2246267895839L, "cat", arrayList8, true, false, false);
                    }
                    boolean z8 = memoryUsageDumpOptions.dumpSwapPss && z7 && jArr27[10] != 0;
                    memoryUsageDumpOptions.dumpSwapPss = z8;
                    if (!memoryUsageDumpOptions.oomOnly) {
                        dumpMemItems(protoOutputStream6, 2246267895813L, "proc", arrayList7, true, true, z8);
                    }
                    dumpMemItems(protoOutputStream6, 2246267895814L, "oom", arrayList9, false, true, memoryUsageDumpOptions.dumpSwapPss);
                    if (!z && !memoryUsageDumpOptions.oomOnly) {
                        dumpMemItems(protoOutputStream6, 2246267895815L, "cat", arrayList8, true, true, memoryUsageDumpOptions.dumpSwapPss);
                    }
                    MemInfoReader memInfoReader = new MemInfoReader();
                    memInfoReader.readMemInfo();
                    if (jArr27[12] > 0) {
                        activityManagerService = this;
                        synchronized (activityManagerService.mProcessStats.mLock) {
                            long cachedSizeKb = memInfoReader.getCachedSizeKb();
                            long freeSizeKb = memInfoReader.getFreeSizeKb();
                            long zramTotalSizeKb = memInfoReader.getZramTotalSizeKb();
                            long kernelUsedSizeKb = memInfoReader.getKernelUsedSizeKb();
                            EventLogTags.writeAmMeminfo(cachedSizeKb * 1024, freeSizeKb * 1024, zramTotalSizeKb * 1024, kernelUsedSizeKb * 1024, jArr27[12] * 1024);
                            activityManagerService.mProcessStats.addSysMemUsageLocked(cachedSizeKb, freeSizeKb, zramTotalSizeKb, kernelUsedSizeKb, jArr27[12]);
                        }
                    } else {
                        activityManagerService = this;
                    }
                    if (z) {
                        protoOutputStream = protoOutputStream6;
                        j = j9;
                    } else {
                        protoOutputStream = protoOutputStream6;
                        protoOutputStream.write(1112396529672L, memInfoReader.getTotalSizeKb());
                        protoOutputStream.write(1159641169929L, activityManagerService.mAppProfiler.getLastMemoryLevelLocked());
                        j = j9;
                        protoOutputStream.write(1112396529674L, j);
                        protoOutputStream.write(1112396529675L, memInfoReader.getCachedSizeKb());
                        protoOutputStream.write(1112396529676L, memInfoReader.getFreeSizeKb());
                    }
                    long totalSizeKb = ((((memInfoReader.getTotalSizeKb() - (jArr27[9] - jArr27[10])) - memInfoReader.getFreeSizeKb()) - memInfoReader.getCachedSizeKb()) - memInfoReader.getKernelUsedSizeKb()) - memInfoReader.getZramTotalSizeKb();
                    protoOutputStream.write(1112396529677L, jArr27[9] - j);
                    protoOutputStream.write(1112396529678L, memInfoReader.getKernelUsedSizeKb());
                    protoOutputStream.write(1112396529679L, totalSizeKb);
                    if (!z) {
                        if (memInfoReader.getZramTotalSizeKb() != 0) {
                            protoOutputStream.write(1112396529680L, memInfoReader.getZramTotalSizeKb());
                            protoOutputStream.write(1112396529681L, memInfoReader.getSwapTotalSizeKb() - memInfoReader.getSwapFreeSizeKb());
                            protoOutputStream.write(1112396529682L, memInfoReader.getSwapTotalSizeKb());
                        }
                        long[] ksmInfo = getKsmInfo();
                        protoOutputStream.write(1112396529683L, ksmInfo[1]);
                        protoOutputStream.write(1112396529684L, ksmInfo[0]);
                        protoOutputStream.write(1112396529685L, ksmInfo[2]);
                        protoOutputStream.write(1112396529686L, ksmInfo[3]);
                        protoOutputStream.write(1120986464279L, ActivityManager.staticGetMemoryClass());
                        protoOutputStream.write(1120986464280L, ActivityManager.staticGetLargeMemoryClass());
                        protoOutputStream.write(1112396529689L, activityManagerService.mProcessList.getMemLevel(999) / 1024);
                        protoOutputStream.write(1112396529690L, activityManagerService.mProcessList.getCachedRestoreThresholdKb());
                        protoOutputStream.write(1133871366171L, ActivityManager.isLowRamDeviceStatic());
                        protoOutputStream.write(1133871366172L, ActivityManager.isHighEndGfx());
                    }
                } else {
                    protoOutputStream = protoOutputStream6;
                }
                protoOutputStream.flush();
                return;
            }
        }
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$18(int i, String[] strArr, ArrayList arrayList, ProcessCpuTracker.Stats stats) {
        String str;
        if (stats.pid == i || ((str = stats.baseName) != null && str.equals(strArr[0]))) {
            arrayList.add(stats);
        }
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$19(ProcessRecord processRecord, long j, long j2, long j3, int i, long j4, long j5, ProcessStats.ProcessStateHolder processStateHolder) {
        FrameworkStatsLog.write(18, processRecord.info.uid, processStateHolder.state.getName(), processStateHolder.state.getPackage(), j, j2, j3, i, j4 - j5, processStateHolder.appVersion, processRecord.mProfile.getCurrentHostingComponentTypes(), processRecord.mProfile.getHistoricalHostingComponentTypes());
    }

    public static /* synthetic */ void lambda$dumpApplicationMemoryUsage$20(SparseArray sparseArray, Debug.MemoryInfo[] memoryInfoArr, boolean z, MemoryUsageDumpOptions memoryUsageDumpOptions, long[] jArr, long[] jArr2, ArrayList arrayList, long[] jArr3, long[] jArr4, long[] jArr5, long[] jArr6, long[] jArr7, long[] jArr8, long[] jArr9, long[] jArr10, ArrayList[] arrayListArr, long[] jArr11, ProcessCpuTracker.Stats stats) {
        if (stats.vsize <= 0 || sparseArray.indexOfKey(stats.pid) >= 0) {
            return;
        }
        if (memoryInfoArr[0] == null) {
            memoryInfoArr[0] = new Debug.MemoryInfo();
        }
        Debug.MemoryInfo memoryInfo = memoryInfoArr[0];
        if (!z && !memoryUsageDumpOptions.oomOnly) {
            if (!Debug.getMemoryInfo(stats.pid, memoryInfo)) {
                return;
            }
        } else {
            long pss = Debug.getPss(stats.pid, jArr, null);
            if (pss == 0) {
                return;
            }
            memoryInfo.nativePss = (int) pss;
            memoryInfo.nativePrivateDirty = (int) jArr[0];
            memoryInfo.nativeRss = (int) jArr[2];
        }
        long totalPss = memoryInfo.getTotalPss();
        long totalSwappedOutPss = memoryInfo.getTotalSwappedOutPss();
        long totalRss = memoryInfo.getTotalRss();
        jArr2[9] = jArr2[9] + totalPss;
        jArr2[10] = jArr2[10] + totalSwappedOutPss;
        jArr2[11] = jArr2[11] + totalRss;
        jArr2[12] = jArr2[12] + totalPss;
        MemItem memItem = new MemItem(stats.name + " (pid " + stats.pid + ")", stats.name, totalPss, memoryInfo.getSummaryTotalSwapPss(), totalRss, stats.pid, UserHandle.getUserId(stats.uid), false);
        arrayList.add(memItem);
        jArr2[0] = jArr2[0] + ((long) memoryInfo.nativePss);
        jArr2[1] = jArr2[1] + ((long) memoryInfo.nativeSwappedOutPss);
        jArr2[2] = jArr2[2] + ((long) memoryInfo.nativeRss);
        jArr2[3] = jArr2[3] + ((long) memoryInfo.dalvikPss);
        jArr2[4] = jArr2[4] + memoryInfo.dalvikSwappedOutPss;
        jArr2[5] = jArr2[5] + memoryInfo.dalvikRss;
        for (int i = 0; i < jArr3.length; i++) {
            int i2 = i + 17;
            jArr3[i] = jArr3[i] + memoryInfo.getOtherPss(i2);
            jArr4[i] = jArr4[i] + memoryInfo.getOtherSwappedOutPss(i2);
            jArr5[i] = jArr5[i] + memoryInfo.getOtherRss(i2);
        }
        jArr2[6] = jArr2[6] + memoryInfo.otherPss;
        jArr2[7] = jArr2[7] + memoryInfo.otherSwappedOutPss;
        jArr2[8] = jArr2[8] + memoryInfo.otherRss;
        for (int i3 = 0; i3 < 17; i3++) {
            long otherPss = memoryInfo.getOtherPss(i3);
            jArr6[i3] = jArr6[i3] + otherPss;
            jArr2[6] = jArr2[6] - otherPss;
            long otherSwappedOutPss = memoryInfo.getOtherSwappedOutPss(i3);
            jArr7[i3] = jArr7[i3] + otherSwappedOutPss;
            jArr2[7] = jArr2[7] - otherSwappedOutPss;
            long otherRss = memoryInfo.getOtherRss(i3);
            jArr8[i3] = jArr8[i3] + otherRss;
            jArr2[8] = jArr2[8] - otherRss;
        }
        jArr9[0] = jArr9[0] + totalPss;
        jArr10[0] = jArr10[0] + totalSwappedOutPss;
        if (arrayListArr[0] == null) {
            arrayListArr[0] = new ArrayList();
        }
        arrayListArr[0].add(memItem);
        jArr11[0] = jArr11[0] + totalRss;
    }

    public static void appendBasicMemEntry(StringBuilder sb, int i, int i2, long j, long j2, String str) {
        sb.append("  ");
        sb.append(ProcessList.makeOomAdjString(i, false));
        sb.append(' ');
        sb.append(ProcessList.makeProcStateString(i2));
        sb.append(' ');
        ProcessList.appendRamKb(sb, j);
        sb.append(": ");
        sb.append(str);
        if (j2 > 0) {
            sb.append(" (");
            sb.append(stringifyKBSize(j2));
            sb.append(" memtrack)");
        }
    }

    public static void appendMemInfo(StringBuilder sb, ProcessMemInfo processMemInfo) {
        appendBasicMemEntry(sb, processMemInfo.oomAdj, processMemInfo.procState, processMemInfo.pss, processMemInfo.memtrack, processMemInfo.name);
        sb.append(" (pid ");
        sb.append(processMemInfo.pid);
        sb.append(") ");
        sb.append(processMemInfo.adjType);
        sb.append('\n');
        if (processMemInfo.adjReason != null) {
            sb.append("                      ");
            sb.append(processMemInfo.adjReason);
            sb.append('\n');
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0074 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void filterNonExportedComponents(final Intent intent, int i, final int i2, List list, PlatformCompat platformCompat, String str, String str2) {
        boolean isChangeEnabledByUid;
        if (list == null || intent.getPackage() != null || intent.getComponent() != null || ActivityManager.canAccessUnexportedComponents(i)) {
            return;
        }
        final IUnsafeIntentStrictModeCallback iUnsafeIntentStrictModeCallback = this.mStrictModeCallbacks.get(i2);
        for (int size = list.size() - 1; size >= 0; size--) {
            if (list.get(size) instanceof ResolveInfo) {
                ResolveInfo resolveInfo = (ResolveInfo) list.get(size);
                if (resolveInfo.getComponentInfo().exported) {
                    continue;
                } else {
                    resolveInfo.getComponentInfo().getComponentName().flattenToShortString();
                    if (iUnsafeIntentStrictModeCallback != null) {
                        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda10
                            @Override // java.lang.Runnable
                            public final void run() {
                                ActivityManagerService.this.lambda$filterNonExportedComponents$21(iUnsafeIntentStrictModeCallback, intent, i2);
                            }
                        });
                    }
                    isChangeEnabledByUid = platformCompat.isChangeEnabledByUid(229362273L, i);
                    ActivityManagerUtils.logUnsafeIntentEvent(2, i, intent, str2, isChangeEnabledByUid);
                    if (isChangeEnabledByUid) {
                        return;
                    }
                    list.remove(size);
                }
            } else {
                if (list.get(size) instanceof BroadcastFilter) {
                    if (((BroadcastFilter) list.get(size)).exported) {
                        continue;
                    }
                    if (iUnsafeIntentStrictModeCallback != null) {
                    }
                    isChangeEnabledByUid = platformCompat.isChangeEnabledByUid(229362273L, i);
                    ActivityManagerUtils.logUnsafeIntentEvent(2, i, intent, str2, isChangeEnabledByUid);
                    if (isChangeEnabledByUid) {
                    }
                } else {
                    continue;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filterNonExportedComponents$21(IUnsafeIntentStrictModeCallback iUnsafeIntentStrictModeCallback, Intent intent, int i) {
        try {
            iUnsafeIntentStrictModeCallback.onImplicitIntentMatchedInternalComponent(intent.cloneFilter());
        } catch (RemoteException unused) {
            this.mStrictModeCallbacks.remove(i);
        }
    }

    @GuardedBy({"this"})
    public final boolean cleanUpApplicationRecordLocked(final ProcessRecord processRecord, int i, boolean z, boolean z2, int i2, boolean z3, boolean z4) {
        boolean z5;
        boolean onCleanupApplicationRecordLSP;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (i2 >= 0) {
                    removeLruProcessLocked(processRecord);
                    ProcessList.remove(i);
                }
                ProcessStatsService processStatsService = this.mProcessStats;
                if (!z4 && !processRecord.isolated) {
                    z5 = false;
                    onCleanupApplicationRecordLSP = processRecord.onCleanupApplicationRecordLSP(processStatsService, z2, z5);
                    this.mOomAdjuster.mCachedAppOptimizer.onCleanupApplicationRecordLocked(processRecord);
                }
                z5 = true;
                onCleanupApplicationRecordLSP = processRecord.onCleanupApplicationRecordLSP(processStatsService, z2, z5);
                this.mOomAdjuster.mCachedAppOptimizer.onCleanupApplicationRecordLocked(processRecord);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        this.mAppProfiler.onCleanupApplicationRecordLocked(processRecord);
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.onApplicationCleanupLocked(processRecord);
        }
        clearProcessForegroundLocked(processRecord);
        this.mServices.killServicesLocked(processRecord, z2);
        this.mPhantomProcessList.onAppDied(i);
        BackupRecord backupRecord = this.mBackupTargets.get(processRecord.userId);
        if (backupRecord != null && i == backupRecord.app.getPid()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService.18
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        IBackupManager asInterface = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
                        ProcessRecord processRecord2 = processRecord;
                        asInterface.agentDisconnectedForUser(processRecord2.userId, processRecord2.info.packageName);
                    } catch (RemoteException unused) {
                    }
                }
            });
        }
        this.mProcessList.scheduleDispatchProcessDiedLocked(i, processRecord.info.uid);
        boolean handlePrecedingAppDiedLocked = this.mProcessList.handlePrecedingAppDiedLocked(processRecord);
        ProcessRecord processRecord2 = processRecord.mPredecessor;
        if (processRecord2 != null) {
            processRecord2.mSuccessor = null;
            processRecord2.mSuccessorStartRunnable = null;
            processRecord.mPredecessor = null;
        }
        if (z) {
            return false;
        }
        if (!processRecord.isPersistent() || processRecord.isolated) {
            if (!z3) {
                this.mProcessList.removeProcessNameLocked(processRecord.processName, processRecord.uid, processRecord);
            }
            this.mAtmInternal.clearHeavyWeightProcessIfEquals(processRecord.getWindowProcessController());
        } else if (!processRecord.isRemoved() && this.mPersistentStartingProcesses.indexOf(processRecord) < 0) {
            this.mPersistentStartingProcesses.add(processRecord);
            onCleanupApplicationRecordLSP = true;
        }
        this.mProcessesOnHold.remove(processRecord);
        this.mAtmInternal.onCleanUpApplicationRecord(processRecord.getWindowProcessController());
        this.mProcessList.noteProcessDiedLocked(processRecord);
        if (onCleanupApplicationRecordLSP && handlePrecedingAppDiedLocked && !processRecord.isolated) {
            if (i2 < 0) {
                ProcessList.remove(i);
            }
            this.mHandler.removeMessages(57, processRecord);
            this.mProcessList.addProcessNameLocked(processRecord);
            processRecord.setPendingStart(false);
            this.mProcessList.startProcessLocked(processRecord, new HostingRecord("restart", processRecord.processName), 0);
            return true;
        }
        if (i > 0 && i != MY_PID) {
            removePidLocked(i, processRecord);
            this.mHandler.removeMessages(20, processRecord);
            this.mBatteryStatsService.noteProcessFinish(processRecord.processName, processRecord.info.uid);
            if (processRecord.isolated) {
                this.mBatteryStatsService.removeIsolatedUid(processRecord.uid, processRecord.info.uid);
            }
            processRecord.setPid(0);
        }
        return false;
    }

    public List<ActivityManager.RunningServiceInfo> getServices(int i, int i2) {
        List<ActivityManager.RunningServiceInfo> runningServiceInfoLocked;
        enforceNotIsolatedCaller("getServices");
        int callingUid = Binder.getCallingUid();
        boolean z = ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid) == 0;
        boolean isGetTasksAllowed = this.mAtmInternal.isGetTasksAllowed("getServices", Binder.getCallingPid(), callingUid);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                runningServiceInfoLocked = this.mServices.getRunningServiceInfoLocked(i, i2, callingUid, isGetTasksAllowed, z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return runningServiceInfoLocked;
    }

    public PendingIntent getRunningServiceControlPanel(ComponentName componentName) {
        PendingIntent runningServiceControlPanelLocked;
        enforceNotIsolatedCaller("getRunningServiceControlPanel");
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        if (componentName == null || getPackageManagerInternal().filterAppAccess(componentName.getPackageName(), callingUid, userId)) {
            return null;
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                runningServiceControlPanelLocked = this.mServices.getRunningServiceControlPanelLocked(componentName);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return runningServiceControlPanelLocked;
    }

    public void logFgsApiBegin(int i, int i2, int i3) {
        enforceCallingPermission("android.permission.LOG_PROCESS_ACTIVITIES", "logFgsApiStart");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.logFgsApiBeginLocked(i, i2, i3);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void logFgsApiEnd(int i, int i2, int i3) {
        enforceCallingPermission("android.permission.LOG_PROCESS_ACTIVITIES", "logFgsApiStart");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.logFgsApiEndLocked(i, i2, i3);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void logFgsApiStateChanged(int i, int i2, int i3, int i4) {
        enforceCallingPermission("android.permission.LOG_PROCESS_ACTIVITIES", "logFgsApiStart");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.logFgsApiStateChangedLocked(i, i3, i4, i2);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public ComponentName startService(IApplicationThread iApplicationThread, Intent intent, String str, boolean z, String str2, String str3, int i) throws TransactionTooLargeException {
        return startService(iApplicationThread, intent, str, z, str2, str3, i, false, -1, null, null);
    }

    public final ComponentName startService(IApplicationThread iApplicationThread, Intent intent, String str, boolean z, String str2, String str3, int i, boolean z2, int i2, String str4, String str5) throws TransactionTooLargeException {
        enforceNotIsolatedCaller("startService");
        enforceAllowedToStartOrBindServiceIfSdkSandbox(intent);
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("callingPackage cannot be null");
        }
        if (z2 && str5 == null) {
            throw new IllegalArgumentException("No instance name provided for SDK sandbox process");
        }
        validateServiceInstanceName(str5);
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceBegin(64L, "startService: intent=" + intent + ", caller=" + str2 + ", fgRequired=" + z);
            }
            try {
                try {
                    synchronized (this) {
                        try {
                            boostPriorityForLockedSection();
                            ComponentName startServiceLocked = this.mServices.startServiceLocked(iApplicationThread, intent, str, callingPid, callingUid, z, str2, str3, i, z2, i2, str4, str5);
                            resetPriorityAfterLockedSection();
                            Trace.traceEnd(64L);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return startServiceLocked;
                        } catch (Throwable th) {
                            th = th;
                            resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    Trace.traceEnd(64L);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public final void validateServiceInstanceName(String str) {
        if (str != null && !str.matches("[a-zA-Z0-9_.]+")) {
            throw new IllegalArgumentException("Illegal instanceName");
        }
    }

    public int stopService(IApplicationThread iApplicationThread, Intent intent, String str, int i) {
        return stopService(iApplicationThread, intent, str, i, false, -1, null, null);
    }

    public final int stopService(IApplicationThread iApplicationThread, Intent intent, String str, int i, boolean z, int i2, String str2, String str3) {
        int stopServiceLocked;
        enforceNotIsolatedCaller("stopService");
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        try {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceBegin(64L, "stopService: " + intent);
            }
            synchronized (this) {
                boostPriorityForLockedSection();
                stopServiceLocked = this.mServices.stopServiceLocked(iApplicationThread, intent, str, i, z, i2, str2, str3);
            }
            resetPriorityAfterLockedSection();
            return stopServiceLocked;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public IBinder peekService(Intent intent, String str, String str2) {
        IBinder peekServiceLocked;
        enforceNotIsolatedCaller("peekService");
        if (intent == null || !intent.hasFileDescriptors()) {
            if (str2 == null) {
                throw new IllegalArgumentException("callingPackage cannot be null");
            }
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    peekServiceLocked = this.mServices.peekServiceLocked(intent, str, str2);
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
            return peekServiceLocked;
        }
        throw new IllegalArgumentException("File descriptors passed in Intent");
    }

    public boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i) {
        boolean stopServiceTokenLocked;
        String str;
        try {
            if (Trace.isTagEnabled(64L)) {
                StringBuilder sb = new StringBuilder();
                sb.append("stopServiceToken: ");
                if (componentName != null) {
                    str = componentName.toShortString();
                } else {
                    str = "from " + Binder.getCallingPid();
                }
                sb.append(str);
                Trace.traceBegin(64L, sb.toString());
            }
            synchronized (this) {
                boostPriorityForLockedSection();
                stopServiceTokenLocked = this.mServices.stopServiceTokenLocked(componentName, iBinder, i);
            }
            resetPriorityAfterLockedSection();
            return stopServiceTokenLocked;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, int i2, int i3) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.setServiceForegroundLocked(componentName, iBinder, i, notification, i2, i3);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public int getForegroundServiceType(ComponentName componentName, IBinder iBinder) {
        int foregroundServiceTypeLocked;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                foregroundServiceTypeLocked = this.mServices.getForegroundServiceTypeLocked(componentName, iBinder);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return foregroundServiceTypeLocked;
    }

    public boolean shouldServiceTimeOut(ComponentName componentName, IBinder iBinder) {
        boolean shouldServiceTimeOutLocked;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                shouldServiceTimeOutLocked = this.mServices.shouldServiceTimeOutLocked(componentName, iBinder);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return shouldServiceTimeOutLocked;
    }

    public int handleIncomingUser(int i, int i2, int i3, boolean z, boolean z2, String str, String str2) {
        return this.mUserController.handleIncomingUser(i, i2, i3, z, z2 ? 2 : 0, str, str2);
    }

    public boolean isSingleton(String str, ApplicationInfo applicationInfo, String str2, int i) {
        if (UserHandle.getAppId(applicationInfo.uid) >= 10000) {
            if ((i & 1073741824) != 0) {
                if (ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS", applicationInfo.uid) == 0) {
                    return true;
                }
                ComponentName componentName = new ComponentName(applicationInfo.packageName, str2);
                String str3 = "Permission Denial: Component " + componentName.flattenToShortString() + " requests FLAG_SINGLE_USER, but app does not hold android.permission.INTERACT_ACROSS_USERS";
                Slog.w("ActivityManager", str3);
                throw new SecurityException(str3);
            }
        } else if ("system".equals(str)) {
            return true;
        } else {
            if ((i & 1073741824) != 0 && (UserHandle.isSameApp(applicationInfo.uid, 1001) || (applicationInfo.flags & 8) != 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidSingletonCall(int i, int i2) {
        int appId = UserHandle.getAppId(i2);
        return UserHandle.isSameApp(i, i2) || appId == 1000 || appId == 1001 || ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i2) == 0;
    }

    public int bindService(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, int i) throws TransactionTooLargeException {
        return bindServiceInstance(iApplicationThread, iBinder, intent, str, iServiceConnection, j, null, str2, i);
    }

    public int bindServiceInstance(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, String str3, int i) throws TransactionTooLargeException {
        return bindServiceInstance(iApplicationThread, iBinder, intent, str, iServiceConnection, j, str2, false, -1, null, null, str3, i);
    }

    public final int bindServiceInstance(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, boolean z, int i, String str3, IApplicationThread iApplicationThread2, String str4, int i2) throws TransactionTooLargeException {
        long j2;
        enforceNotIsolatedCaller("bindService");
        enforceAllowedToStartOrBindServiceIfSdkSandbox(intent);
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        if (str4 == null) {
            throw new IllegalArgumentException("callingPackage cannot be null");
        }
        if (z && str2 == null) {
            throw new IllegalArgumentException("No instance name provided for isolated process");
        }
        validateServiceInstanceName(str2);
        try {
            if (Trace.isTagEnabled(64L)) {
                ComponentName component = intent.getComponent();
                StringBuilder sb = new StringBuilder();
                sb.append("bindService:");
                sb.append(component != null ? component.toShortString() : intent.getAction());
                Trace.traceBegin(64L, sb.toString());
            }
            try {
                synchronized (this) {
                    try {
                        boostPriorityForLockedSection();
                        int bindServiceLocked = this.mServices.bindServiceLocked(iApplicationThread, iBinder, intent, str, iServiceConnection, j, str2, z, i, str3, iApplicationThread2, str4, i2);
                        resetPriorityAfterLockedSection();
                        Trace.traceEnd(64L);
                        return bindServiceLocked;
                    } catch (Throwable th) {
                        th = th;
                        j2 = 64;
                        try {
                            resetPriorityAfterLockedSection();
                            throw th;
                        } catch (Throwable th2) {
                            th = th2;
                            Trace.traceEnd(j2);
                            throw th;
                        }
                    }
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
            j2 = 64;
        }
    }

    public void updateServiceGroup(IServiceConnection iServiceConnection, int i, int i2) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.updateServiceGroupLocked(iServiceConnection, i, i2);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean unbindService(IServiceConnection iServiceConnection) {
        boolean unbindServiceLocked;
        try {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceBegin(64L, "unbindService");
            }
            synchronized (this) {
                boostPriorityForLockedSection();
                unbindServiceLocked = this.mServices.unbindServiceLocked(iServiceConnection);
            }
            resetPriorityAfterLockedSection();
            return unbindServiceLocked;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void publishService(IBinder iBinder, Intent intent, IBinder iBinder2) {
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (!(iBinder instanceof ServiceRecord)) {
                    throw new IllegalArgumentException("Invalid service token");
                }
                this.mServices.publishServiceLocked((ServiceRecord) iBinder, intent, iBinder2);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void unbindFinished(IBinder iBinder, Intent intent, boolean z) {
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mServices.unbindFinishedLocked((ServiceRecord) iBinder, intent, z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (!(iBinder instanceof ServiceRecord)) {
                    Slog.e("ActivityManager", "serviceDoneExecuting: Invalid service token=" + iBinder);
                    throw new IllegalArgumentException("Invalid service token");
                }
                this.mServices.serviceDoneExecutingLocked((ServiceRecord) iBinder, i, i2, i3, false);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean bindBackupAgent(String str, int i, int i2, int i3) {
        ApplicationInfo applicationInfo;
        ComponentName componentName;
        int i4;
        enforceCallingPermission("android.permission.CONFIRM_FULL_BACKUP", "bindBackupAgent");
        int i5 = PackageManagerShellCommandDataLoader.PACKAGE.equals(str) || getPackageManagerInternal().getSystemUiServiceComponent().getPackageName().equals(str) ? 0 : i2;
        IPackageManager packageManager = AppGlobals.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 1024L, i5);
        } catch (RemoteException unused) {
            applicationInfo = null;
        }
        ApplicationInfo applicationInfo2 = applicationInfo;
        if (applicationInfo2 == null) {
            Slog.w("ActivityManager", "Unable to bind backup agent for " + str);
            return false;
        }
        if (applicationInfo2.backupAgentName != null) {
            ComponentName componentName2 = new ComponentName(applicationInfo2.packageName, applicationInfo2.backupAgentName);
            try {
                i4 = packageManager.getComponentEnabledSetting(componentName2, i5);
            } catch (RemoteException unused2) {
                i4 = 0;
            }
            if (i4 == 2 || i4 == 3 || i4 == 4) {
                Slog.w("ActivityManager", "Unable to bind backup agent for " + componentName2 + ", the backup agent component is disabled.");
                return false;
            }
        }
        synchronized (this) {
            try {
                try {
                    boostPriorityForLockedSection();
                    this.mPackageManagerInt.setPackageStoppedState(applicationInfo2.packageName, false, UserHandle.getUserId(applicationInfo2.uid));
                } catch (IllegalArgumentException e) {
                    Slog.w("ActivityManager", "Failed trying to unstop package " + applicationInfo2.packageName + ": " + e);
                }
                BackupRecord backupRecord = new BackupRecord(applicationInfo2, i, i2, i3);
                if (i == 0) {
                    componentName = new ComponentName(applicationInfo2.packageName, applicationInfo2.backupAgentName);
                } else {
                    componentName = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, "FullBackupAgent");
                }
                ProcessRecord startProcessLocked = startProcessLocked(applicationInfo2.processName, applicationInfo2, false, 0, new HostingRecord("backup", componentName), 4, false, false);
                if (startProcessLocked == null) {
                    Slog.e("ActivityManager", "Unable to start backup agent process " + backupRecord);
                    resetPriorityAfterLockedSection();
                    return false;
                }
                if (UserHandle.isApp(applicationInfo2.uid) && i == 1) {
                    startProcessLocked.setInFullBackup(true);
                }
                backupRecord.app = startProcessLocked;
                BackupRecord backupRecord2 = this.mBackupTargets.get(i2);
                int i6 = backupRecord2 != null ? backupRecord2.appInfo.uid : -1;
                int i7 = startProcessLocked.isInFullBackup() ? backupRecord.appInfo.uid : -1;
                this.mBackupTargets.put(i2, backupRecord);
                startProcessLocked.mProfile.addHostingComponentType(4);
                updateOomAdjLocked(startProcessLocked, 0);
                IApplicationThread thread = startProcessLocked.getThread();
                if (thread != null) {
                    try {
                        thread.scheduleCreateBackupAgent(applicationInfo2, i, i2, i3);
                    } catch (RemoteException unused3) {
                    }
                }
                resetPriorityAfterLockedSection();
                JobSchedulerInternal jobSchedulerInternal = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
                if (i6 != -1) {
                    jobSchedulerInternal.removeBackingUpUid(i6);
                }
                if (i7 != -1) {
                    jobSchedulerInternal.addBackingUpUid(i7);
                    return true;
                }
                return true;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void clearPendingBackup(int i) {
        ProcessRecord processRecord;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                int indexOfKey = this.mBackupTargets.indexOfKey(i);
                if (indexOfKey >= 0) {
                    BackupRecord valueAt = this.mBackupTargets.valueAt(indexOfKey);
                    if (valueAt != null && (processRecord = valueAt.app) != null) {
                        processRecord.mProfile.clearHostingComponentType(4);
                    }
                    this.mBackupTargets.removeAt(indexOfKey);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        ((JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class)).clearAllBackingUpUids();
    }

    public void backupAgentCreated(String str, IBinder iBinder, int i) {
        int handleIncomingUser = this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 2, "backupAgentCreated", null);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                BackupRecord backupRecord = this.mBackupTargets.get(handleIncomingUser);
                if (!str.equals(backupRecord == null ? null : backupRecord.appInfo.packageName)) {
                    Slog.e("ActivityManager", "Backup agent created for " + str + " but not requested!");
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    try {
                        IBackupManager.Stub.asInterface(ServiceManager.getService("backup")).agentConnectedForUser(handleIncomingUser, str, iBinder);
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                } catch (RemoteException unused) {
                } catch (Exception e) {
                    Slog.w("ActivityManager", "Exception trying to deliver BackupAgent binding: ");
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void unbindBackupAgent(ApplicationInfo applicationInfo) {
        enforceCallingPermission("android.permission.CONFIRM_FULL_BACKUP", "unbindBackupAgent");
        if (applicationInfo == null) {
            Slog.w("ActivityManager", "unbind backup agent for null app");
            return;
        }
        int userId = UserHandle.getUserId(applicationInfo.uid);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                BackupRecord backupRecord = this.mBackupTargets.get(userId);
                String str = backupRecord == null ? null : backupRecord.appInfo.packageName;
                if (str == null) {
                    Slog.w("ActivityManager", "Unbinding backup agent with no active backup");
                    this.mBackupTargets.delete(userId);
                    resetPriorityAfterLockedSection();
                } else if (!str.equals(applicationInfo.packageName)) {
                    Slog.e("ActivityManager", "Unbind of " + applicationInfo + " but is not the current backup target");
                    this.mBackupTargets.delete(userId);
                    resetPriorityAfterLockedSection();
                } else {
                    ProcessRecord processRecord = backupRecord.app;
                    updateOomAdjLocked(processRecord, 0);
                    processRecord.setInFullBackup(false);
                    processRecord.mProfile.clearHostingComponentType(4);
                    int i = backupRecord.appInfo.uid;
                    IApplicationThread thread = processRecord.getThread();
                    if (thread != null) {
                        try {
                            thread.scheduleDestroyBackupAgent(applicationInfo, userId);
                        } catch (Exception e) {
                            Slog.e("ActivityManager", "Exception when unbinding backup agent:");
                            e.printStackTrace();
                        }
                    }
                    this.mBackupTargets.delete(userId);
                    resetPriorityAfterLockedSection();
                    if (i != -1) {
                        ((JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class)).removeBackingUpUid(i);
                    }
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final boolean isInstantApp(ProcessRecord processRecord, String str, int i) {
        if (UserHandle.getAppId(i) < 10000) {
            return false;
        }
        if (processRecord != null) {
            return processRecord.info.isInstantApp();
        }
        IPackageManager packageManager = AppGlobals.getPackageManager();
        if (str == null) {
            try {
                String[] packagesForUid = packageManager.getPackagesForUid(i);
                if (packagesForUid == null || packagesForUid.length == 0) {
                    throw new IllegalArgumentException("Unable to determine caller package name");
                }
                str = packagesForUid[0];
            } catch (RemoteException e) {
                Slog.e("ActivityManager", "Error looking up if " + str + " is an instant app.", e);
                return true;
            }
        }
        this.mAppOpsService.checkPackage(i, str);
        return packageManager.isInstantApp(str, UserHandle.getUserId(i));
    }

    @Deprecated
    public Intent registerReceiver(IApplicationThread iApplicationThread, String str, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str2, int i, int i2) {
        return registerReceiverWithFeature(iApplicationThread, str, null, null, iIntentReceiver, intentFilter, str2, i, i2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:107:0x01d3, code lost:
        if ("com.shannon.imsservice".equals(r53) == false) goto L121;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x01d5, code lost:
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x01fd, code lost:
        if ((r1 & 4) != 0) goto L126;
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x0202, code lost:
        if ((r1 & 4) == 0) goto L125;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00ec, code lost:
        r57.setPriority(1000);
     */
    /* JADX WARN: Removed duplicated region for block: B:128:0x020d  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0210  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x024b  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0258  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x025c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x025d  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00bb A[Catch: all -> 0x0478, TryCatch #2 {all -> 0x0478, blocks: (B:7:0x0019, B:9:0x0023, B:10:0x003c, B:14:0x0043, B:16:0x004b, B:18:0x0055, B:21:0x005e, B:22:0x007c, B:23:0x007d, B:25:0x00ab, B:33:0x00bb, B:35:0x00c2, B:37:0x00ce, B:39:0x00d6, B:41:0x00de, B:44:0x00e7, B:46:0x00ec, B:47:0x00ef, B:49:0x00f5, B:51:0x0104, B:52:0x0114, B:54:0x011a, B:56:0x0125, B:58:0x0131, B:61:0x013b, B:63:0x0143, B:67:0x0150, B:70:0x015b, B:72:0x0169, B:74:0x0173, B:76:0x017d, B:79:0x0184, B:80:0x018b, B:81:0x018c, B:82:0x0193, B:83:0x0194, B:87:0x019b, B:89:0x019f, B:92:0x01a4, B:93:0x01ab, B:94:0x01ac, B:96:0x01b7, B:103:0x01c3, B:104:0x01ca, B:106:0x01cd, B:124:0x0204, B:125:0x0206, B:116:0x01e2, B:117:0x01f8, B:119:0x01fb, B:122:0x0200), top: B:223:0x0019, inners: #3 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Intent registerReceiverWithFeature(IApplicationThread iApplicationThread, String str, String str2, String str3, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str4, int i, int i2) {
        ArrayList arrayList;
        ArrayList arrayList2;
        int i3;
        IntentFilter intentFilter2;
        String str5;
        ArrayList<Intent> arrayList3;
        boolean z;
        int i4;
        int i5 = i2;
        enforceNotIsolatedCaller("registerReceiver");
        int i6 = i5 & 1;
        boolean z2 = i6 != 0;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    Slog.w("ActivityManager", "registerReceiverWithFeature: no app for " + iApplicationThread);
                    resetPriorityAfterLockedSection();
                    return null;
                }
                if (recordForAppLOSP.info.uid != 1000 && !recordForAppLOSP.getPkgList().containsKey(str) && !PackageManagerShellCommandDataLoader.PACKAGE.equals(str)) {
                    throw new SecurityException("Given caller package " + str + " is not running in process " + recordForAppLOSP);
                }
                int i7 = recordForAppLOSP.info.uid;
                int pid = recordForAppLOSP.getPid();
                boolean isInstantApp = isInstantApp(recordForAppLOSP, str, i7);
                int handleIncomingUser = this.mUserController.handleIncomingUser(pid, i7, i, true, 2, "registerReceiver", str);
                if (UserHandle.isCore(i7)) {
                    int priority = intentFilter.getPriority();
                    if (priority < 1000 && priority > -1000) {
                        z = false;
                        if (!z) {
                            int countActions = intentFilter.countActions();
                            while (i4 < countActions) {
                                String action = intentFilter.getAction(i4);
                                i4 = (action.startsWith("android.intent.action.USER_") || action.startsWith("android.intent.action.PACKAGE_") || action.startsWith("android.intent.action.UID_") || action.startsWith("android.intent.action.EXTERNAL_")) ? 0 : i4 + 1;
                            }
                        }
                    }
                    z = true;
                    if (!z) {
                    }
                }
                Iterator<String> actionsIterator = intentFilter.actionsIterator();
                if (actionsIterator == null) {
                    ArrayList arrayList4 = new ArrayList(1);
                    arrayList = null;
                    arrayList4.add(null);
                    actionsIterator = arrayList4.iterator();
                } else {
                    arrayList = null;
                }
                Iterator<String> it = actionsIterator;
                int[] iArr = {-1, UserHandle.getUserId(i7)};
                boolean z3 = true;
                ArrayList arrayList5 = arrayList;
                while (it.hasNext()) {
                    String next = it.next();
                    ArrayList arrayList6 = arrayList5;
                    for (int i8 = 0; i8 < 2; i8++) {
                        ArrayMap<String, ArrayList<Intent>> arrayMap = this.mStickyBroadcasts.get(iArr[i8]);
                        if (arrayMap != null && (arrayList3 = arrayMap.get(next)) != null) {
                            ArrayList arrayList7 = arrayList6 == null ? new ArrayList() : arrayList6;
                            arrayList7.addAll(arrayList3);
                            arrayList6 = arrayList7;
                        }
                    }
                    if (z3) {
                        try {
                            z3 &= AppGlobals.getPackageManager().isProtectedBroadcast(next);
                        } catch (RemoteException e) {
                            Slog.w("ActivityManager", "Remote exception", e);
                            z3 = false;
                        }
                    }
                    arrayList5 = arrayList6;
                }
                if (Process.isSdkSandboxUid(Binder.getCallingUid())) {
                    SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
                    if (sdkSandboxManagerLocal == null) {
                        throw new IllegalStateException("SdkSandboxManagerLocal not found when checking whether SDK sandbox uid can register to broadcast receivers.");
                    }
                    if (!sdkSandboxManagerLocal.canRegisterBroadcastReceiver(intentFilter, i5, z3)) {
                        throw new SecurityException("SDK sandbox not allowed to register receiver with the given IntentFilter");
                    }
                }
                boolean z4 = (i5 & 6) != 0;
                if ((i5 & 2) != 0 && (i5 & 4) != 0) {
                    throw new IllegalArgumentException("Receiver can't specify both RECEIVER_EXPORTED and RECEIVER_NOT_EXPORTEDflag");
                }
                boolean isChangeEnabled = CompatChanges.isChangeEnabled(161145287L, i7);
                if (((i6 == 0 || (i5 & 4) == 0) ? false : true) && isChangeEnabled) {
                    throw new IllegalArgumentException("Receiver can't specify both RECEIVER_VISIBLE_TO_INSTANT_APPS and RECEIVER_NOT_EXPORTED flag");
                }
                if (!z3) {
                    if (iIntentReceiver != null || z4) {
                        if (isChangeEnabled && !z4) {
                            throw new SecurityException(str + ": One of RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED should be specified when a receiver isn't being registered exclusively for system broadcasts");
                        }
                        resetPriorityAfterLockedSection();
                        boolean z5 = (i5 & 2) == 0;
                        if (arrayList5 == null) {
                            ContentResolver contentResolver = this.mContext.getContentResolver();
                            int size = arrayList5.size();
                            ArrayList arrayList8 = null;
                            for (int i9 = 0; i9 < size; i9++) {
                                Intent intent = (Intent) arrayList5.get(i9);
                                if ((!isInstantApp || (intent.getFlags() & 2097152) != 0) && intentFilter.match(contentResolver, intent, true, "ActivityManager") >= 0) {
                                    if (arrayList8 == null) {
                                        arrayList8 = new ArrayList();
                                    }
                                    arrayList8.add(intent);
                                }
                            }
                            arrayList2 = arrayList8;
                        } else {
                            arrayList2 = null;
                        }
                        Intent intent2 = arrayList2 == null ? (Intent) arrayList2.get(0) : null;
                        if (iIntentReceiver != null) {
                            return intent2;
                        }
                        if (pid != Process.myPid() && (intentFilter.hasAction("com.android.server.net.action.SNOOZE_WARNING") || intentFilter.hasAction("com.android.server.net.action.SNOOZE_RAPID"))) {
                            EventLog.writeEvent(1397638484, "177931370", Integer.valueOf(i7), "");
                        }
                        synchronized (this) {
                            try {
                                boostPriorityForLockedSection();
                                IApplicationThread thread = recordForAppLOSP.getThread();
                                if (thread != null && thread.asBinder() == iApplicationThread.asBinder()) {
                                    ReceiverList receiverList = this.mRegisteredReceivers.get(iIntentReceiver.asBinder());
                                    if (receiverList == null) {
                                        i3 = 0;
                                        intentFilter2 = intentFilter;
                                        str5 = str;
                                        ReceiverList receiverList2 = new ReceiverList(this, recordForAppLOSP, pid, i7, handleIncomingUser, iIntentReceiver);
                                        ProcessRecord processRecord = receiverList2.app;
                                        if (processRecord != null) {
                                            int numberOfReceivers = processRecord.mReceivers.numberOfReceivers();
                                            if (numberOfReceivers >= 1000) {
                                                throw new IllegalStateException("Too many receivers, total of " + numberOfReceivers + ", registered for pid: " + receiverList2.pid + ", callerPackage: " + str5);
                                            }
                                            receiverList2.app.mReceivers.addReceiver(receiverList2);
                                        } else {
                                            try {
                                                iIntentReceiver.asBinder().linkToDeath(receiverList2, 0);
                                                receiverList2.linkedToDeath = true;
                                            } catch (RemoteException unused) {
                                                return intent2;
                                            }
                                        }
                                        this.mRegisteredReceivers.put(iIntentReceiver.asBinder(), receiverList2);
                                        receiverList = receiverList2;
                                    } else {
                                        i3 = 0;
                                        intentFilter2 = intentFilter;
                                        str5 = str;
                                        if (receiverList.uid != i7) {
                                            throw new IllegalArgumentException("Receiver requested to register for uid " + i7 + " was previously registered for uid " + receiverList.uid + " callerPackage is " + str5);
                                        } else if (receiverList.pid != pid) {
                                            throw new IllegalArgumentException("Receiver requested to register for pid " + pid + " was previously registered for pid " + receiverList.pid + " callerPackage is " + str5);
                                        } else if (receiverList.userId != handleIncomingUser) {
                                            throw new IllegalArgumentException("Receiver requested to register for user " + handleIncomingUser + " was previously registered for user " + receiverList.userId + " callerPackage is " + str5);
                                        }
                                    }
                                    String str6 = str5;
                                    IntentFilter intentFilter3 = intentFilter2;
                                    BroadcastFilter broadcastFilter = new BroadcastFilter(intentFilter, receiverList, str, str2, str3, str4, i7, handleIncomingUser, isInstantApp, z2, z5);
                                    if (receiverList.containsFilter(intentFilter3)) {
                                        Slog.w("ActivityManager", "Receiver with filter " + intentFilter3 + " already registered for pid " + receiverList.pid + ", callerPackage is " + str6);
                                    } else {
                                        receiverList.add(broadcastFilter);
                                        if (!broadcastFilter.debugCheck()) {
                                            Slog.w("ActivityManager", "==> For Dynamic broadcast");
                                        }
                                        this.mReceiverResolver.addFilter(getPackageManagerInternal().snapshot(), broadcastFilter);
                                    }
                                    if (arrayList2 != null) {
                                        ArrayList arrayList9 = new ArrayList();
                                        arrayList9.add(broadcastFilter);
                                        int size2 = arrayList2.size();
                                        for (int i10 = i3; i10 < size2; i10++) {
                                            Intent intent3 = (Intent) arrayList2.get(i10);
                                            BroadcastQueue broadcastQueueForIntent = broadcastQueueForIntent(intent3);
                                            broadcastQueueForIntent.enqueueBroadcastLocked(new BroadcastRecord(broadcastQueueForIntent, intent3, null, null, null, -1, -1, false, null, null, null, null, -1, null, arrayList9, null, null, 0, null, null, false, true, true, -1, BackgroundStartPrivileges.NONE, false, null));
                                        }
                                    }
                                    resetPriorityAfterLockedSection();
                                    return intent2;
                                }
                                resetPriorityAfterLockedSection();
                                return null;
                            } catch (Throwable th) {
                                resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    i5 |= 2;
                    resetPriorityAfterLockedSection();
                    if ((i5 & 2) == 0) {
                    }
                    if (arrayList5 == null) {
                    }
                    if (arrayList2 == null) {
                    }
                    if (iIntentReceiver != null) {
                    }
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    public void unregisterReceiver(IIntentReceiver iIntentReceiver) {
        boolean z;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                ReceiverList receiverList = this.mRegisteredReceivers.get(iIntentReceiver.asBinder());
                if (receiverList != null) {
                    BroadcastRecord broadcastRecord = receiverList.curBroadcast;
                    z = broadcastRecord != null && broadcastRecord.queue.finishReceiverLocked(receiverList.app, broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.resultAbort, false);
                    ProcessRecord processRecord = receiverList.app;
                    if (processRecord != null) {
                        processRecord.mReceivers.removeReceiver(receiverList);
                    }
                    removeReceiverLocked(receiverList);
                    if (receiverList.linkedToDeath) {
                        receiverList.linkedToDeath = false;
                        receiverList.receiver.asBinder().unlinkToDeath(receiverList, 0);
                    }
                } else {
                    z = false;
                }
                if (!z) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                trimApplicationsLocked(false, 2);
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeReceiverLocked(ReceiverList receiverList) {
        this.mRegisteredReceivers.remove(receiverList.receiver.asBinder());
        for (int size = receiverList.size() - 1; size >= 0; size--) {
            this.mReceiverResolver.removeFilter(receiverList.get(size));
        }
    }

    public final void sendPackageBroadcastLocked(int i, String[] strArr, int i2) {
        this.mProcessList.sendPackageBroadcastLocked(i, strArr, i2);
    }

    public final List<ResolveInfo> collectReceiverComponents(Intent intent, String str, int i, int[] iArr, int[] iArr2) {
        List<ResolveInfo> list = null;
        boolean z = false;
        HashSet hashSet = null;
        for (int i2 : iArr) {
            if (i != 2000 || !this.mUserController.hasUserRestriction("no_debugging_features", i2)) {
                List<ResolveInfo> queryIntentReceivers = this.mPackageManagerInt.queryIntentReceivers(intent, str, 268436480, i, i2, true);
                if (i2 != 0 && queryIntentReceivers != null) {
                    int i3 = 0;
                    while (i3 < queryIntentReceivers.size()) {
                        if ((queryIntentReceivers.get(i3).activityInfo.flags & 536870912) != 0) {
                            queryIntentReceivers.remove(i3);
                            i3--;
                        }
                        i3++;
                    }
                }
                if (queryIntentReceivers != null) {
                    int size = queryIntentReceivers.size() - 1;
                    while (size >= 0) {
                        int i4 = size;
                        List<ResolveInfo> list2 = queryIntentReceivers;
                        ComponentAliasResolver.Resolution<ResolveInfo> resolveReceiver = this.mComponentAliasResolver.resolveReceiver(intent, queryIntentReceivers.get(size), str, 268436480, i2, i, true);
                        if (resolveReceiver == null) {
                            list2.remove(i4);
                        } else if (resolveReceiver.isAlias()) {
                            list2.set(i4, resolveReceiver.getTarget());
                        }
                        size = i4 - 1;
                        queryIntentReceivers = list2;
                    }
                }
                List<ResolveInfo> list3 = queryIntentReceivers;
                List<ResolveInfo> list4 = (list3 == null || list3.size() != 0) ? list3 : null;
                if (list == null) {
                    list = list4;
                } else if (list4 != null) {
                    if (!z) {
                        for (int i5 = 0; i5 < list.size(); i5++) {
                            ResolveInfo resolveInfo = list.get(i5);
                            if ((resolveInfo.activityInfo.flags & 1073741824) != 0) {
                                ActivityInfo activityInfo = resolveInfo.activityInfo;
                                ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
                                if (hashSet == null) {
                                    hashSet = new HashSet();
                                }
                                hashSet.add(componentName);
                            }
                        }
                        z = true;
                    }
                    for (int i6 = 0; i6 < list4.size(); i6++) {
                        ResolveInfo resolveInfo2 = list4.get(i6);
                        if ((resolveInfo2.activityInfo.flags & 1073741824) != 0) {
                            ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
                            ComponentName componentName2 = new ComponentName(activityInfo2.packageName, activityInfo2.name);
                            if (hashSet == null) {
                                hashSet = new HashSet();
                            }
                            if (!hashSet.contains(componentName2)) {
                                hashSet.add(componentName2);
                                list.add(resolveInfo2);
                            }
                        } else {
                            list.add(resolveInfo2);
                        }
                    }
                }
            }
        }
        if (list != null && iArr2 != null) {
            for (int size2 = list.size() - 1; size2 >= 0; size2--) {
                int appId = UserHandle.getAppId(list.get(size2).activityInfo.applicationInfo.uid);
                if (appId >= 10000 && Arrays.binarySearch(iArr2, appId) < 0) {
                    list.remove(size2);
                }
            }
        }
        return list;
    }

    public final void checkBroadcastFromSystem(Intent intent, ProcessRecord processRecord, String str, int i, boolean z, List list) {
        if ((intent.getFlags() & 4194304) != 0) {
            return;
        }
        String action = intent.getAction();
        if (z || "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "com.android.intent.action.DISMISS_KEYBOARD_SHORTCUTS".equals(action) || "android.intent.action.MEDIA_BUTTON".equals(action) || "android.intent.action.MEDIA_SCANNER_SCAN_FILE".equals(action) || "com.android.intent.action.SHOW_KEYBOARD_SHORTCUTS".equals(action) || "android.intent.action.MASTER_CLEAR".equals(action) || "android.intent.action.FACTORY_RESET".equals(action) || "android.appwidget.action.APPWIDGET_CONFIGURE".equals(action) || "android.appwidget.action.APPWIDGET_UPDATE".equals(action) || "com.android.omadm.service.CONFIGURATION_UPDATE".equals(action) || "android.text.style.SUGGESTION_PICKED".equals(action) || "android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION".equals(action) || "android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION".equals(action)) {
            return;
        }
        if (intent.getPackage() != null || intent.getComponent() != null) {
            if (list == null || list.size() == 0) {
                return;
            }
            boolean z2 = true;
            for (int size = list.size() - 1; size >= 0; size--) {
                Object obj = list.get(size);
                if (obj instanceof ResolveInfo) {
                    ActivityInfo activityInfo = ((ResolveInfo) obj).activityInfo;
                    if (activityInfo.exported && activityInfo.permission == null) {
                        z2 = false;
                        break;
                    }
                } else {
                    BroadcastFilter broadcastFilter = (BroadcastFilter) obj;
                    if (broadcastFilter.exported && broadcastFilter.requiredPermission == null) {
                        z2 = false;
                        break;
                    }
                }
            }
            if (z2) {
                return;
            }
        }
        if (processRecord != null) {
            Log.wtf("ActivityManager", "Sending non-protected broadcast " + action + " from system " + processRecord.toShortString() + " pkg " + str, new Throwable());
            return;
        }
        Log.wtf("ActivityManager", "Sending non-protected broadcast " + action + " from system uid " + UserHandle.formatUid(i) + " pkg " + str, new Throwable());
    }

    public void enforceBroadcastOptionPermissionsInternal(Bundle bundle, int i) {
        enforceBroadcastOptionPermissionsInternal(BroadcastOptions.fromBundleNullable(bundle), i);
    }

    public void enforceBroadcastOptionPermissionsInternal(BroadcastOptions broadcastOptions, int i) {
        if (broadcastOptions == null || i == 1000) {
            return;
        }
        if (broadcastOptions.isAlarmBroadcast()) {
            throw new SecurityException("Non-system callers may not flag broadcasts as alarm");
        }
        if (broadcastOptions.isInteractive()) {
            enforceCallingPermission("android.permission.BROADCAST_OPTION_INTERACTIVE", "setInteractive");
        }
    }

    @GuardedBy({"this"})
    public final int broadcastIntentLocked(ProcessRecord processRecord, String str, String str2, Intent intent, String str3, IIntentReceiver iIntentReceiver, int i, String str4, Bundle bundle, String[] strArr, String[] strArr2, String[] strArr3, int i2, Bundle bundle2, boolean z, boolean z2, int i3, int i4, int i5, int i6, int i7) {
        return broadcastIntentLocked(processRecord, str, str2, intent, str3, null, iIntentReceiver, i, str4, bundle, strArr, strArr2, strArr3, i2, bundle2, z, z2, i3, i4, i5, i6, i7, BackgroundStartPrivileges.NONE, null, null);
    }

    @GuardedBy({"this"})
    public final int broadcastIntentLocked(ProcessRecord processRecord, String str, String str2, Intent intent, String str3, ProcessRecord processRecord2, IIntentReceiver iIntentReceiver, int i, String str4, Bundle bundle, String[] strArr, String[] strArr2, String[] strArr3, int i2, Bundle bundle2, boolean z, boolean z2, int i3, int i4, int i5, int i6, int i7, BackgroundStartPrivileges backgroundStartPrivileges, int[] iArr, BiFunction<Integer, Bundle, Bundle> biFunction) {
        int traceBegin = BroadcastQueue.traceBegin("broadcastIntentLockedTraced");
        int broadcastIntentLockedTraced = broadcastIntentLockedTraced(processRecord, str, str2, intent, str3, processRecord2, iIntentReceiver, i, str4, bundle, strArr, strArr2, strArr3, i2, BroadcastOptions.fromBundleNullable(bundle2), z, z2, i3, i4, i5, i6, i7, backgroundStartPrivileges, iArr, biFunction);
        BroadcastQueue.traceEnd(traceBegin);
        return broadcastIntentLockedTraced;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:294:0x0650, code lost:
        if (r6.equals("android.intent.action.PACKAGES_UNSUSPENDED") == false) goto L395;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0315  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x0327  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x03da  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x050f A[FALL_THROUGH] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00b6  */
    /* JADX WARN: Removed duplicated region for block: B:383:0x08b1  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00f6  */
    /* JADX WARN: Removed duplicated region for block: B:393:0x08d7  */
    /* JADX WARN: Removed duplicated region for block: B:395:0x08e2  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0118  */
    /* JADX WARN: Removed duplicated region for block: B:435:0x09fe  */
    /* JADX WARN: Removed duplicated region for block: B:438:0x0a05  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0a0c  */
    /* JADX WARN: Removed duplicated region for block: B:442:0x0a1f  */
    /* JADX WARN: Removed duplicated region for block: B:443:0x0a2f  */
    /* JADX WARN: Removed duplicated region for block: B:446:0x0a37  */
    /* JADX WARN: Removed duplicated region for block: B:465:0x0a9d  */
    /* JADX WARN: Removed duplicated region for block: B:468:0x0aa9  */
    /* JADX WARN: Removed duplicated region for block: B:472:0x0ab4  */
    /* JADX WARN: Removed duplicated region for block: B:480:0x0ae3  */
    /* JADX WARN: Removed duplicated region for block: B:481:0x0ae8  */
    /* JADX WARN: Removed duplicated region for block: B:483:0x0aec A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:492:0x0b66  */
    /* JADX WARN: Removed duplicated region for block: B:510:0x0bb9  */
    /* JADX WARN: Removed duplicated region for block: B:514:0x0bc1  */
    /* JADX WARN: Removed duplicated region for block: B:525:0x0bf7 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0bfb  */
    /* JADX WARN: Removed duplicated region for block: B:529:0x0c03  */
    /* JADX WARN: Removed duplicated region for block: B:535:0x0c25  */
    /* JADX WARN: Removed duplicated region for block: B:537:0x0c2d  */
    /* JADX WARN: Removed duplicated region for block: B:541:0x0c44  */
    /* JADX WARN: Removed duplicated region for block: B:543:0x0c56  */
    /* JADX WARN: Removed duplicated region for block: B:549:0x0cb8  */
    /* JADX WARN: Removed duplicated region for block: B:585:0x0c1d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:586:0x0c11 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01af  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x02c6  */
    /* JADX WARN: Type inference failed for: r1v23, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r27v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r35v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v10 */
    /* JADX WARN: Type inference failed for: r6v11, types: [android.content.IntentFilter, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r6v12 */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v15, types: [com.android.server.am.BroadcastFilter] */
    /* JADX WARN: Type inference failed for: r6v9 */
    @GuardedBy({"this"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int broadcastIntentLockedTraced(ProcessRecord processRecord, String str, String str2, Intent intent, String str3, ProcessRecord processRecord2, IIntentReceiver iIntentReceiver, int i, String str4, Bundle bundle, String[] strArr, String[] strArr2, String[] strArr3, int i2, BroadcastOptions broadcastOptions, boolean z, boolean z2, int i3, int i4, int i5, int i6, int i7, BackgroundStartPrivileges backgroundStartPrivileges, int[] iArr, BiFunction<Integer, Bundle, Bundle> biFunction) {
        ProcessRecord processRecord3;
        Intent intent2;
        boolean isInstantApp;
        int[] iArr2;
        int[] iArr3;
        int handleIncomingUser;
        String action;
        BackgroundStartPrivileges backgroundStartPrivileges2;
        BroadcastOptions broadcastOptions2;
        int appId;
        boolean z3;
        int i8;
        Intent intent3;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int[] iArr4;
        List<ResolveInfo> list;
        List<BroadcastFilter> list2;
        int size;
        int i14;
        Intent intent4;
        int i15;
        List<BroadcastFilter> list3;
        List<ResolveInfo> list4;
        Intent intent5;
        List<ResolveInfo> list5;
        int i16;
        String schemeSpecificPart;
        String[] strArr4;
        int size2;
        ResolveInfo resolveInfo;
        BroadcastFilter broadcastFilter;
        int i17;
        int length;
        int i18;
        int size3;
        List<BroadcastFilter> list6;
        int i19;
        List<ResolveInfo> list7;
        ArrayMap<String, ArrayList<Intent>> arrayMap;
        ArrayList<Intent> arrayList;
        char c;
        String str5;
        int i20;
        Intent intent6;
        int i21;
        int i22;
        String schemeSpecificPart2;
        String str6;
        String schemeSpecificPart3;
        ApplicationInfo applicationInfo;
        String schemeSpecificPart4;
        int i23;
        String schemeSpecificPart5;
        String str7;
        int uidFromIntent;
        UidRecord uidRecordLOSP;
        BackgroundStartPrivileges backgroundStartPrivileges3;
        BroadcastLoopers.addMyLooper();
        if (Process.isSdkSandboxUid(i5)) {
            SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
            if (sdkSandboxManagerLocal == null) {
                throw new IllegalStateException("SdkSandboxManagerLocal not found when sending a broadcast from an SDK sandbox uid.");
            }
            if (!sdkSandboxManagerLocal.canSendBroadcast(intent)) {
                throw new SecurityException("Intent " + intent.getAction() + " may not be broadcast from an SDK sandbox uid. Given caller package " + str + " (pid=" + i3 + ", uid=" + i4 + ")");
            }
        }
        try {
            if (iIntentReceiver != null && processRecord2 == null) {
                if (!(iIntentReceiver.asBinder() instanceof BinderProxy)) {
                    processRecord3 = getProcessRecordLocked("system", 1000);
                    intent2 = new Intent(intent);
                    isInstantApp = isInstantApp(processRecord, str, i4);
                    if (isInstantApp) {
                        intent2.setFlags(intent2.getFlags() & (-2097153));
                    }
                    if (i7 == -1 || iArr == null) {
                        iArr2 = iArr;
                    } else {
                        Slog.e("ActivityManager", "broadcastAllowList only applies when sending to individual users. Assuming restrictive whitelist.");
                        iArr2 = new int[0];
                    }
                    intent2.addFlags(16);
                    if (!this.mProcessesReady && (intent2.getFlags() & 33554432) == 0) {
                        intent2.addFlags(1073741824);
                    }
                    if (iIntentReceiver != null && !z) {
                        if (!this.mEnableModernQueue) {
                            Slog.w("ActivityManager", "Broadcast " + intent2 + " not ordered but result callback requested!");
                        }
                        if (!UserHandle.isCore(i4)) {
                            String str8 = "Unauthorized unordered resultTo broadcast " + intent2 + " sent from uid " + i4;
                            Slog.w("ActivityManager", str8);
                            throw new SecurityException(str8);
                        }
                    }
                    iArr3 = iArr2;
                    handleIncomingUser = this.mUserController.handleIncomingUser(i3, i4, i7, true, 0, INetd.IF_FLAG_BROADCAST, str);
                    if (handleIncomingUser == -1 && !this.mUserController.isUserOrItsParentRunning(handleIncomingUser) && ((i4 != 1000 || (intent2.getFlags() & 33554432) == 0) && !"android.intent.action.ACTION_SHUTDOWN".equals(intent2.getAction()))) {
                        Slog.w("ActivityManager", "Skipping broadcast of " + intent2 + ": user " + handleIncomingUser + " and its parent (if any) are stopped");
                        return -2;
                    }
                    action = intent2.getAction();
                    if (broadcastOptions != null) {
                        backgroundStartPrivileges2 = backgroundStartPrivileges;
                    } else if (broadcastOptions.getTemporaryAppAllowlistDuration() > 0 && checkComponentPermission("android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST", i6, i5, -1, true) != 0 && checkComponentPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i6, i5, -1, true) != 0 && checkComponentPermission("android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND", i6, i5, -1, true) != 0) {
                        String str9 = "Permission Denial: " + intent2.getAction() + " broadcast from " + str + " (pid=" + i3 + ", uid=" + i4 + ") requires android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST or android.permission.START_ACTIVITIES_FROM_BACKGROUND or android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND";
                        Slog.w("ActivityManager", str9);
                        throw new SecurityException(str9);
                    } else if (broadcastOptions.isDontSendToRestrictedApps() && !isUidActiveLOSP(i4) && isBackgroundRestrictedNoCheck(i4, str)) {
                        Slog.i("ActivityManager", "Not sending broadcast " + action + " - app " + str + " has background restrictions");
                        return -96;
                    } else {
                        if (!broadcastOptions.allowsBackgroundActivityStarts()) {
                            backgroundStartPrivileges3 = backgroundStartPrivileges;
                        } else if (checkComponentPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i6, i5, -1, true) != 0) {
                            String str10 = "Permission Denial: " + intent2.getAction() + " broadcast from " + str + " (pid=" + i3 + ", uid=" + i4 + ") requires android.permission.START_ACTIVITIES_FROM_BACKGROUND";
                            Slog.w("ActivityManager", str10);
                            throw new SecurityException(str10);
                        } else {
                            backgroundStartPrivileges3 = BackgroundStartPrivileges.ALLOW_BAL;
                        }
                        if (broadcastOptions.getIdForResponseEvent() > 0) {
                            enforcePermission("android.permission.ACCESS_BROADCAST_RESPONSE_STATS", i3, i4, "recordResponseEventWhileInBackground");
                        }
                        backgroundStartPrivileges2 = backgroundStartPrivileges3;
                    }
                    if (iIntentReceiver == null && !z && this.mEnableModernQueue) {
                        BroadcastOptions makeBasic = broadcastOptions == null ? BroadcastOptions.makeBasic() : broadcastOptions;
                        makeBasic.setDeferUntilActive(true);
                        broadcastOptions2 = makeBasic;
                    } else {
                        broadcastOptions2 = broadcastOptions;
                    }
                    if (!this.mEnableModernQueue && z && broadcastOptions2 != null && broadcastOptions2.isDeferUntilActive()) {
                        throw new IllegalArgumentException("Ordered broadcasts can't be deferred until active");
                    }
                    boolean isProtectedBroadcast = AppGlobals.getPackageManager().isProtectedBroadcast(action);
                    appId = UserHandle.getAppId(i4);
                    if (appId != 0 && appId != 1027 && appId != 1068 && appId != 1073) {
                        switch (appId) {
                            case 1000:
                            case 1001:
                            case 1002:
                                break;
                            default:
                                z3 = processRecord != null && processRecord.isPersistent();
                                break;
                        }
                        if (!z3) {
                            if (isProtectedBroadcast) {
                                String str11 = "Permission Denial: not allowed to send broadcast " + action + " from pid=" + i3 + ", uid=" + i4;
                                Slog.w("ActivityManager", str11);
                                throw new SecurityException(str11);
                            } else if ("android.appwidget.action.APPWIDGET_CONFIGURE".equals(action) || "android.appwidget.action.APPWIDGET_UPDATE".equals(action)) {
                                if (str == null) {
                                    String str12 = "Permission Denial: not allowed to send broadcast " + action + " from unknown caller.";
                                    Slog.w("ActivityManager", str12);
                                    throw new SecurityException(str12);
                                } else if (intent2.getComponent() != null) {
                                    if (!intent2.getComponent().getPackageName().equals(str)) {
                                        String str13 = "Permission Denial: not allowed to send broadcast " + action + " to " + intent2.getComponent().getPackageName() + " from " + str;
                                        Slog.w("ActivityManager", str13);
                                        throw new SecurityException(str13);
                                    }
                                } else {
                                    intent2.setPackage(str);
                                }
                            }
                        }
                        if (action == null) {
                            if (getBackgroundLaunchBroadcasts().contains(action)) {
                                intent2.addFlags(16777216);
                            }
                            char c2 = 6;
                            switch (action.hashCode()) {
                                case -2098526293:
                                    if (action.equals("android.hardware.action.NEW_VIDEO")) {
                                        c = 0;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -1749672628:
                                    if (action.equals("android.intent.action.UID_REMOVED")) {
                                        c = 1;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -1403934493:
                                    if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                                        c = 2;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -1338021860:
                                    if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE")) {
                                        c = 3;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -1001645458:
                                    if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                                        c = 4;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -810471698:
                                    if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
                                        c = 5;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case -403228793:
                                    if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                                        c = 6;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 172491798:
                                    if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                                        c = 7;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 183904262:
                                    if (action.equals("android.intent.action.PROXY_CHANGE")) {
                                        c = '\b';
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 267468725:
                                    if (action.equals("android.intent.action.PACKAGE_DATA_CLEARED")) {
                                        c = '\t';
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 502473491:
                                    if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                                        c = '\n';
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 505380757:
                                    if (action.equals("android.intent.action.TIME_SET")) {
                                        c = 11;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 525384130:
                                    if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                                        c = '\f';
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 555021408:
                                    if (action.equals("com.android.launcher.action.INSTALL_SHORTCUT")) {
                                        c = '\r';
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 789737439:
                                    if (action.equals("android.intent.action.PRE_BOOT_COMPLETED")) {
                                        c = 14;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 852070077:
                                    if (action.equals("android.intent.action.MEDIA_SCANNER_SCAN_FILE")) {
                                        c = 15;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 920338478:
                                    if (action.equals("android.hardware.action.NEW_PICTURE")) {
                                        c = 16;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 1290767157:
                                    if (action.equals("android.intent.action.PACKAGES_UNSUSPENDED")) {
                                        c = 17;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 1544582882:
                                    if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                                        c = 18;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 1664215986:
                                    if (action.equals("android.net.action.CLEAR_DNS_CACHE")) {
                                        c = 19;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                case 1862858502:
                                    if (action.equals("android.security.action.TRUST_STORE_CHANGED")) {
                                        c = 20;
                                        break;
                                    }
                                    c = 65535;
                                    break;
                                default:
                                    c = 65535;
                                    break;
                            }
                            switch (c) {
                                case 0:
                                case 16:
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    intent3.addFlags(1073741824);
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if ((!"android.intent.action.PACKAGE_ADDED".equals(str7) || "android.intent.action.PACKAGE_REMOVED".equals(str7) || "android.intent.action.PACKAGE_REPLACED".equals(str7)) && (uidFromIntent = getUidFromIntent(intent3)) != -1 && (uidRecordLOSP = this.mProcessList.getUidRecordLOSP(uidFromIntent)) != null) {
                                        uidRecordLOSP.updateHasInternetPermission();
                                    }
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 7:
                                case '\f':
                                case 17:
                                    if (checkComponentPermission("android.permission.BROADCAST_PACKAGE_REMOVED", i3, i4, -1, true) != 0) {
                                        String str14 = "Permission Denial: " + intent2.getAction() + " broadcast from " + str + " (pid=" + i3 + ", uid=" + i4 + ") requires android.permission.BROADCAST_PACKAGE_REMOVED";
                                        Slog.w("ActivityManager", str14);
                                        throw new SecurityException(str14);
                                    }
                                    switch (action.hashCode()) {
                                        case -1749672628:
                                            if (action.equals("android.intent.action.UID_REMOVED")) {
                                                c2 = 0;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case -1403934493:
                                            if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                                                c2 = 1;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case -1338021860:
                                            if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE")) {
                                                c2 = 2;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case -1001645458:
                                            if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                                                c2 = 3;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case 172491798:
                                            if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                                                c2 = 4;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case 525384130:
                                            if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                                                c2 = 5;
                                                break;
                                            }
                                            c2 = 65535;
                                            break;
                                        case 1290767157:
                                            break;
                                        default:
                                            c2 = 65535;
                                            break;
                                    }
                                    switch (c2) {
                                        case 0:
                                            str5 = action;
                                            intent3 = intent2;
                                            i20 = handleIncomingUser;
                                            i8 = 1;
                                            int uidFromIntent2 = getUidFromIntent(intent3);
                                            if (uidFromIntent2 >= 0) {
                                                this.mBatteryStatsService.removeUid(uidFromIntent2);
                                                i9 = 0;
                                                if (intent3.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                                                    this.mAppOpsService.resetAllModes(UserHandle.getUserId(uidFromIntent2), intent3.getStringExtra("android.intent.extra.PACKAGE_NAME"));
                                                    break;
                                                } else {
                                                    this.mAppOpsService.uidRemoved(uidFromIntent2);
                                                    break;
                                                }
                                            }
                                            i9 = 0;
                                            break;
                                        case 1:
                                            str5 = action;
                                            Intent intent7 = intent2;
                                            String[] stringArrayExtra = intent7.getStringArrayExtra("android.intent.extra.changed_package_list");
                                            if (stringArrayExtra == null || stringArrayExtra.length <= 0) {
                                                i20 = handleIncomingUser;
                                                intent3 = intent7;
                                                i8 = 1;
                                            } else {
                                                int i24 = 0;
                                                while (i24 < stringArrayExtra.length) {
                                                    forceStopPackageLocked(stringArrayExtra[i24], -1, false, true, true, false, false, handleIncomingUser, "storage unmount");
                                                    i24++;
                                                    intent7 = intent7;
                                                }
                                                intent3 = intent7;
                                                this.mAtmInternal.cleanupRecentTasksForUser(-1);
                                                i20 = handleIncomingUser;
                                                i8 = 1;
                                                sendPackageBroadcastLocked(1, stringArrayExtra, i20);
                                            }
                                            i9 = 0;
                                            break;
                                        case 2:
                                            str5 = action;
                                            intent6 = intent2;
                                            i21 = handleIncomingUser;
                                            i22 = 1;
                                            this.mAtmInternal.cleanupRecentTasksForUser(-1);
                                            i20 = i21;
                                            intent3 = intent6;
                                            i9 = 0;
                                            i8 = i22;
                                            break;
                                        case 3:
                                        case 6:
                                            str5 = action;
                                            intent6 = intent2;
                                            i21 = handleIncomingUser;
                                            i22 = 1;
                                            this.mAtmInternal.onPackagesSuspendedChanged(intent6.getStringArrayExtra("android.intent.extra.changed_package_list"), "android.intent.action.PACKAGES_SUSPENDED".equals(intent6.getAction()), intent6.getIntExtra("android.intent.extra.user_handle", -10000));
                                            i20 = i21;
                                            intent3 = intent6;
                                            i9 = 0;
                                            i8 = i22;
                                            break;
                                        case 4:
                                        case 5:
                                            Uri data = intent2.getData();
                                            if (data != null && (schemeSpecificPart2 = data.getSchemeSpecificPart()) != null) {
                                                boolean equals = "android.intent.action.PACKAGE_REMOVED".equals(action);
                                                boolean booleanExtra = intent2.getBooleanExtra("android.intent.extra.REPLACING", false);
                                                boolean z4 = !intent2.getBooleanExtra("android.intent.extra.DONT_KILL_APP", false);
                                                boolean z5 = equals && !booleanExtra;
                                                if (equals) {
                                                    if (z4) {
                                                        str5 = action;
                                                        i21 = handleIncomingUser;
                                                        str6 = schemeSpecificPart2;
                                                        forceStopPackageLocked(schemeSpecificPart2, UserHandle.getAppId(intent2.getIntExtra("android.intent.extra.UID", -1)), false, true, true, false, z5, i21, "pkg removed");
                                                        getPackageManagerInternal().onPackageProcessKilledForUninstall(str6);
                                                        intent6 = intent2;
                                                    } else {
                                                        str5 = action;
                                                        str6 = schemeSpecificPart2;
                                                        intent6 = intent2;
                                                        i21 = handleIncomingUser;
                                                        forceStopAppZygoteLocked(str6, UserHandle.getAppId(intent6.getIntExtra("android.intent.extra.UID", -1)), i21);
                                                    }
                                                    sendPackageBroadcastLocked(z4 ? 0 : 2, new String[]{str6}, i21);
                                                    if (z5) {
                                                        i22 = 1;
                                                        this.mUgmInternal.removeUriPermissionsForPackage(str6, i21, true, false);
                                                        this.mAtmInternal.removeRecentTasksByPackageName(str6, i21);
                                                        this.mServices.forceStopPackageLocked(str6, i21);
                                                        this.mAtmInternal.onPackageUninstalled(str6, i21);
                                                        this.mBatteryStatsService.notePackageUninstalled(str6);
                                                    } else {
                                                        i22 = 1;
                                                    }
                                                } else {
                                                    str5 = action;
                                                    i21 = handleIncomingUser;
                                                    i22 = 1;
                                                    intent6 = intent2;
                                                    if (z4) {
                                                        int i25 = booleanExtra ? 16 : 15;
                                                        int intExtra = intent6.getIntExtra("android.intent.extra.UID", -1);
                                                        synchronized (this.mProcLock) {
                                                            try {
                                                                boostPriorityForProcLockedSection();
                                                                this.mProcessList.killPackageProcessesLSP(schemeSpecificPart2, UserHandle.getAppId(intExtra), i21, -10000, i25, 0, "change " + schemeSpecificPart2);
                                                            } catch (Throwable th) {
                                                                resetPriorityAfterProcLockedSection();
                                                                throw th;
                                                            }
                                                        }
                                                        resetPriorityAfterProcLockedSection();
                                                    }
                                                    cleanupDisabledPackageComponentsLocked(schemeSpecificPart2, i21, intent6.getStringArrayExtra("android.intent.extra.changed_component_name_list"));
                                                    this.mServices.schedulePendingServiceStartLocked(schemeSpecificPart2, i21);
                                                }
                                                i20 = i21;
                                                intent3 = intent6;
                                                i9 = 0;
                                                i8 = i22;
                                                break;
                                            }
                                            break;
                                        default:
                                            str5 = action;
                                            intent3 = intent2;
                                            i20 = handleIncomingUser;
                                            i9 = 0;
                                            i8 = 1;
                                            break;
                                    }
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                        break;
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                    break;
                                case 5:
                                    Uri data2 = intent2.getData();
                                    if (data2 != null && (schemeSpecificPart3 = data2.getSchemeSpecificPart()) != null) {
                                        try {
                                            applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(schemeSpecificPart3, 1024L, handleIncomingUser);
                                        } catch (RemoteException unused) {
                                            applicationInfo = null;
                                        }
                                        if (applicationInfo == null) {
                                            Slog.w("ActivityManager", "Dropping ACTION_PACKAGE_REPLACED for non-existent pkg: ssp=" + schemeSpecificPart3 + " data=" + data2);
                                            return 0;
                                        }
                                        updateAssociationForApp(applicationInfo);
                                        this.mAtmInternal.onPackageReplaced(applicationInfo);
                                        this.mServices.updateServiceApplicationInfoLocked(applicationInfo);
                                        sendPackageBroadcastLocked(3, new String[]{schemeSpecificPart3}, handleIncomingUser);
                                    }
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 6:
                                    if (!this.mAtmInternal.checkCanCloseSystemDialogs(i3, i4, str)) {
                                        return 0;
                                    }
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case '\b':
                                    MainHandler mainHandler = this.mHandler;
                                    mainHandler.sendMessage(mainHandler.obtainMessage(29));
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case '\t':
                                    Uri data3 = intent2.getData();
                                    if (data3 != null && (schemeSpecificPart4 = data3.getSchemeSpecificPart()) != null) {
                                        this.mAtmInternal.onPackageDataCleared(schemeSpecificPart4, handleIncomingUser);
                                    }
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case '\n':
                                    this.mHandler.sendEmptyMessage(13);
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 11:
                                    int intExtra2 = intent2.getIntExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", -1);
                                    if (intExtra2 != -1) {
                                        this.mHandler.sendMessage(this.mHandler.obtainMessage(41, intExtra2, 0));
                                    }
                                    this.mBatteryStatsService.noteCurrentTimeChanged();
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case '\r':
                                    Log.w("ActivityManager", "Broadcast " + action + " no longer supported. It will not be delivered.");
                                    return 0;
                                case 14:
                                    str5 = action;
                                    intent3 = intent2;
                                    i9 = 0;
                                    i23 = 1;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 15:
                                    UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
                                    UserInfo userInfo = userManagerInternal.getUserInfo(handleIncomingUser);
                                    if (userInfo != null && userInfo.isCloneProfile()) {
                                        handleIncomingUser = userManagerInternal.getProfileParentId(handleIncomingUser);
                                        str5 = action;
                                        intent3 = intent2;
                                        i9 = 0;
                                        i23 = 0;
                                        i8 = 1;
                                        i10 = 1073741824;
                                        str7 = str5;
                                        if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                        }
                                        uidRecordLOSP.updateHasInternetPermission();
                                        i12 = i23;
                                        i11 = handleIncomingUser;
                                        break;
                                    }
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 18:
                                    Uri data4 = intent2.getData();
                                    if (data4 != null && (schemeSpecificPart5 = data4.getSchemeSpecificPart()) != null) {
                                        this.mAtmInternal.onPackageAdded(schemeSpecificPart5, intent2.getBooleanExtra("android.intent.extra.REPLACING", false));
                                        try {
                                            ApplicationInfo applicationInfo2 = AppGlobals.getPackageManager().getApplicationInfo(schemeSpecificPart5, 1024L, 0);
                                            this.mBatteryStatsService.notePackageInstalled(schemeSpecificPart5, applicationInfo2 != null ? applicationInfo2.longVersionCode : 0L);
                                        } catch (RemoteException unused2) {
                                        }
                                    }
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 19:
                                    this.mHandler.sendEmptyMessage(28);
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                                case 20:
                                    this.mHandler.sendEmptyMessage(63);
                                    str5 = action;
                                    intent3 = intent2;
                                    i20 = handleIncomingUser;
                                    i9 = 0;
                                    i8 = 1;
                                    i10 = 1073741824;
                                    handleIncomingUser = i20;
                                    i23 = i9;
                                    str7 = str5;
                                    if (!"android.intent.action.PACKAGE_ADDED".equals(str7)) {
                                    }
                                    uidRecordLOSP.updateHasInternetPermission();
                                    i12 = i23;
                                    i11 = handleIncomingUser;
                                    break;
                            }
                        } else {
                            i8 = 1;
                            intent3 = intent2;
                            i9 = 0;
                            i10 = 1073741824;
                            i11 = handleIncomingUser;
                            i12 = 0;
                        }
                        if (z2) {
                            i13 = i9;
                        } else if (checkPermission("android.permission.BROADCAST_STICKY", i3, i4) != 0) {
                            String str15 = "Permission Denial: broadcastIntent() requesting a sticky broadcast from pid=" + i3 + ", uid=" + i4 + " requires android.permission.BROADCAST_STICKY";
                            Slog.w("ActivityManager", str15);
                            throw new SecurityException(str15);
                        } else {
                            i13 = i9;
                            if (strArr != null && strArr.length > 0) {
                                Slog.w("ActivityManager", "Can't broadcast sticky intent " + intent3 + " and enforce permissions " + Arrays.toString(strArr));
                                return -1;
                            } else if (intent3.getComponent() != null) {
                                throw new SecurityException("Sticky broadcasts can't target a specific component");
                            } else {
                                if (i11 != -1 && (arrayMap = this.mStickyBroadcasts.get(-1)) != null && (arrayList = arrayMap.get(intent3.getAction())) != null) {
                                    int size4 = arrayList.size();
                                    for (int i26 = i13; i26 < size4; i26++) {
                                        if (intent3.filterEquals(arrayList.get(i26))) {
                                            throw new IllegalArgumentException("Sticky broadcast " + intent3 + " for user " + i11 + " conflicts with existing global broadcast");
                                        }
                                    }
                                }
                                ArrayMap<String, ArrayList<Intent>> arrayMap2 = this.mStickyBroadcasts.get(i11);
                                if (arrayMap2 == null) {
                                    arrayMap2 = new ArrayMap<>();
                                    this.mStickyBroadcasts.put(i11, arrayMap2);
                                }
                                ArrayList<Intent> arrayList2 = arrayMap2.get(intent3.getAction());
                                if (arrayList2 == null) {
                                    arrayList2 = new ArrayList<>();
                                    arrayMap2.put(intent3.getAction(), arrayList2);
                                }
                                int size5 = arrayList2.size();
                                int i27 = i13;
                                while (true) {
                                    if (i27 < size5) {
                                        if (intent3.filterEquals(arrayList2.get(i27))) {
                                            arrayList2.set(i27, new Intent(intent3));
                                        } else {
                                            i27++;
                                        }
                                    }
                                }
                                if (i27 >= size5) {
                                    arrayList2.add(new Intent(intent3));
                                }
                            }
                        }
                        if (i11 != -1) {
                            iArr4 = this.mUserController.getStartedUserArray();
                        } else {
                            iArr4 = new int[]{i11};
                        }
                        int[] iArr5 = iArr4;
                        int traceBegin = BroadcastQueue.traceBegin("queryReceivers");
                        List<ResolveInfo> collectReceiverComponents = (intent3.getFlags() & i10) != 0 ? collectReceiverComponents(intent3, str3, i4, iArr5, iArr3) : null;
                        if (intent3.getComponent() != null) {
                            PackageDataSnapshot snapshot = getPackageManagerInternal().snapshot();
                            if (i11 == -1 && i4 == 2000) {
                                List<BroadcastFilter> list8 = null;
                                int i28 = i13;
                                while (i28 < iArr5.length) {
                                    if (this.mUserController.hasUserRestriction("no_debugging_features", iArr5[i28])) {
                                        list6 = list8;
                                        i19 = i28;
                                        list7 = collectReceiverComponents;
                                    } else {
                                        list6 = list8;
                                        i19 = i28;
                                        list7 = collectReceiverComponents;
                                        list8 = this.mReceiverResolver.queryIntent(snapshot, intent3, str3, false, iArr5[i28]);
                                        if (list6 == null) {
                                            i28 = i19 + 1;
                                            collectReceiverComponents = list7;
                                        } else if (list8 != null) {
                                            list6.addAll(list8);
                                        }
                                    }
                                    list8 = list6;
                                    i28 = i19 + 1;
                                    collectReceiverComponents = list7;
                                }
                                list2 = list8;
                                list = collectReceiverComponents;
                            } else {
                                list = collectReceiverComponents;
                                list2 = this.mReceiverResolver.queryIntent(snapshot, intent3, str3, false, i11);
                            }
                        } else {
                            list = collectReceiverComponents;
                            list2 = null;
                        }
                        BroadcastQueue.traceEnd(traceBegin);
                        intent3.getFlags();
                        if (list2 != null && iArr3 != null) {
                            for (size3 = list2.size() - i8; size3 >= 0; size3--) {
                                int appId2 = UserHandle.getAppId(list2.get(size3).owningUid);
                                if (appId2 >= 10000 && Arrays.binarySearch(iArr3, appId2) < 0) {
                                    list2.remove(size3);
                                }
                            }
                        }
                        filterNonExportedComponents(intent3, i4, i3, list2, this.mPlatformCompat, str, str3);
                        size = list2 == null ? list2.size() : i13;
                        if (!z || size <= 0 || this.mEnableModernQueue) {
                            i14 = i11;
                            intent4 = intent3;
                            i15 = 1073741824;
                            list3 = list2;
                        } else {
                            if (z3) {
                                checkBroadcastFromSystem(intent3, processRecord, str, i4, isProtectedBroadcast, list2);
                            }
                            BroadcastQueue broadcastQueueForIntent = broadcastQueueForIntent(intent3);
                            i14 = i11;
                            i15 = 1073741824;
                            intent4 = intent3;
                            broadcastQueueForIntent.enqueueBroadcastLocked(new BroadcastRecord(broadcastQueueForIntent, intent3, processRecord, str, str2, i3, i4, isInstantApp, str3, strArr, strArr2, strArr3, i2, broadcastOptions2, list2, processRecord3, iIntentReceiver, i, str4, bundle, z, z2, false, i14, backgroundStartPrivileges2, i12, biFunction));
                            list3 = null;
                            size = i13;
                        }
                        list4 = list;
                        if (list4 != null) {
                            intent5 = intent4;
                            list5 = list4;
                            i16 = i13;
                        } else if ("android.intent.action.PACKAGE_ADDED".equals(intent4.getAction()) || "android.intent.action.PACKAGE_RESTARTED".equals(intent4.getAction()) || "android.intent.action.PACKAGE_DATA_CLEARED".equals(intent4.getAction())) {
                            intent5 = intent4;
                            Uri data5 = intent5.getData();
                            if (data5 != null && (schemeSpecificPart = data5.getSchemeSpecificPart()) != null) {
                                strArr4 = new String[]{schemeSpecificPart};
                                if (strArr4 != null && strArr4.length > 0) {
                                    length = strArr4.length;
                                    for (i18 = i13; i18 < length; i18++) {
                                        String str16 = strArr4[i18];
                                        if (str16 != null) {
                                            int size6 = list4.size();
                                            int i29 = i13;
                                            while (i29 < size6) {
                                                if (list4.get(i29).activityInfo.packageName.equals(str16)) {
                                                    list4.remove(i29);
                                                    i29--;
                                                    size6--;
                                                }
                                                i29++;
                                            }
                                        }
                                    }
                                }
                                size2 = list4.size();
                                resolveInfo = null;
                                broadcastFilter = 0;
                                i17 = i13;
                                i16 = i17;
                                while (i17 < size2 && i16 < size) {
                                    if (resolveInfo == null) {
                                        resolveInfo = list4.get(i17);
                                    }
                                    if (broadcastFilter == 0) {
                                        broadcastFilter = list3.get(i16);
                                    }
                                    if (broadcastFilter.getPriority() < resolveInfo.priority) {
                                        list4.add(i17, broadcastFilter);
                                        i16++;
                                        i17++;
                                        size2++;
                                        broadcastFilter = 0;
                                    } else {
                                        i17++;
                                        resolveInfo = null;
                                    }
                                }
                                list5 = list4;
                            }
                            strArr4 = null;
                            if (strArr4 != null) {
                                length = strArr4.length;
                                while (i18 < length) {
                                }
                            }
                            size2 = list4.size();
                            resolveInfo = null;
                            broadcastFilter = 0;
                            i17 = i13;
                            i16 = i17;
                            while (i17 < size2) {
                                if (resolveInfo == null) {
                                }
                                if (broadcastFilter == 0) {
                                }
                                if (broadcastFilter.getPriority() < resolveInfo.priority) {
                                }
                            }
                            list5 = list4;
                        } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(intent4.getAction())) {
                            intent5 = intent4;
                            strArr4 = intent5.getStringArrayExtra("android.intent.extra.changed_package_list");
                            if (strArr4 != null) {
                            }
                            size2 = list4.size();
                            resolveInfo = null;
                            broadcastFilter = 0;
                            i17 = i13;
                            i16 = i17;
                            while (i17 < size2) {
                            }
                            list5 = list4;
                        } else {
                            intent5 = intent4;
                            strArr4 = null;
                            if (strArr4 != null) {
                            }
                            size2 = list4.size();
                            resolveInfo = null;
                            broadcastFilter = 0;
                            i17 = i13;
                            i16 = i17;
                            while (i17 < size2) {
                            }
                            list5 = list4;
                        }
                        while (i16 < size) {
                            if (list5 == null) {
                                list5 = new ArrayList<>();
                            }
                            List<ResolveInfo> list9 = list5;
                            list9.add(list3.get(i16));
                            i16++;
                            list5 = list9;
                        }
                        if (z3) {
                            checkBroadcastFromSystem(intent5, processRecord, str, i4, isProtectedBroadcast, list5);
                        }
                        if ((list5 == null && list5.size() > 0) || iIntentReceiver != null) {
                            BroadcastQueue broadcastQueueForIntent2 = broadcastQueueForIntent(intent5);
                            filterNonExportedComponents(intent5, i4, i3, list5, this.mPlatformCompat, str, str3);
                            broadcastQueueForIntent2.enqueueBroadcastLocked(new BroadcastRecord(broadcastQueueForIntent2, intent5, processRecord, str, str2, i3, i4, isInstantApp, str3, strArr, strArr2, strArr3, i2, broadcastOptions2, list5, processRecord3, iIntentReceiver, i, str4, bundle, z, z2, false, i14, backgroundStartPrivileges2, i12, biFunction));
                        } else if (intent5.getComponent() == null && intent5.getPackage() == null && (intent5.getFlags() & i15) == 0) {
                            addBroadcastStatLocked(intent5.getAction(), str, 0, 0, 0L);
                        }
                        return i13;
                    }
                    z3 = true;
                    if (!z3) {
                    }
                    if (action == null) {
                    }
                    if (z2) {
                    }
                    if (i11 != -1) {
                    }
                    int[] iArr52 = iArr4;
                    int traceBegin2 = BroadcastQueue.traceBegin("queryReceivers");
                    if ((intent3.getFlags() & i10) != 0) {
                    }
                    if (intent3.getComponent() != null) {
                    }
                    BroadcastQueue.traceEnd(traceBegin2);
                    intent3.getFlags();
                    if (list2 != null) {
                        while (size3 >= 0) {
                        }
                    }
                    filterNonExportedComponents(intent3, i4, i3, list2, this.mPlatformCompat, str, str3);
                    if (list2 == null) {
                    }
                    if (z) {
                    }
                    i14 = i11;
                    intent4 = intent3;
                    i15 = 1073741824;
                    list3 = list2;
                    list4 = list;
                    if (list4 != null) {
                    }
                    while (i16 < size) {
                    }
                    if (z3) {
                    }
                    if (list5 == null) {
                    }
                    if (intent5.getComponent() == null) {
                        addBroadcastStatLocked(intent5.getAction(), str, 0, 0, 0L);
                    }
                    return i13;
                }
                Slog.wtf("ActivityManager", "Sending broadcast " + intent.getAction() + " with resultTo requires resultToApp", new Throwable());
            }
            boolean isProtectedBroadcast2 = AppGlobals.getPackageManager().isProtectedBroadcast(action);
            appId = UserHandle.getAppId(i4);
            if (appId != 0) {
                switch (appId) {
                }
                if (!z3) {
                }
                if (action == null) {
                }
                if (z2) {
                }
                if (i11 != -1) {
                }
                int[] iArr522 = iArr4;
                int traceBegin22 = BroadcastQueue.traceBegin("queryReceivers");
                if ((intent3.getFlags() & i10) != 0) {
                }
                if (intent3.getComponent() != null) {
                }
                BroadcastQueue.traceEnd(traceBegin22);
                intent3.getFlags();
                if (list2 != null) {
                }
                filterNonExportedComponents(intent3, i4, i3, list2, this.mPlatformCompat, str, str3);
                if (list2 == null) {
                }
                if (z) {
                }
                i14 = i11;
                intent4 = intent3;
                i15 = 1073741824;
                list3 = list2;
                list4 = list;
                if (list4 != null) {
                }
                while (i16 < size) {
                }
                if (z3) {
                }
                if (list5 == null) {
                }
                if (intent5.getComponent() == null) {
                }
                return i13;
            }
            z3 = true;
            if (!z3) {
            }
            if (action == null) {
            }
            if (z2) {
            }
            if (i11 != -1) {
            }
            int[] iArr5222 = iArr4;
            int traceBegin222 = BroadcastQueue.traceBegin("queryReceivers");
            if ((intent3.getFlags() & i10) != 0) {
            }
            if (intent3.getComponent() != null) {
            }
            BroadcastQueue.traceEnd(traceBegin222);
            intent3.getFlags();
            if (list2 != null) {
            }
            filterNonExportedComponents(intent3, i4, i3, list2, this.mPlatformCompat, str, str3);
            if (list2 == null) {
            }
            if (z) {
            }
            i14 = i11;
            intent4 = intent3;
            i15 = 1073741824;
            list3 = list2;
            list4 = list;
            if (list4 != null) {
            }
            while (i16 < size) {
            }
            if (z3) {
            }
            if (list5 == null) {
            }
            if (intent5.getComponent() == null) {
            }
            return i13;
        } catch (RemoteException e) {
            Slog.w("ActivityManager", "Remote exception", e);
            return 0;
        }
        processRecord3 = processRecord2;
        intent2 = new Intent(intent);
        isInstantApp = isInstantApp(processRecord, str, i4);
        if (isInstantApp) {
        }
        if (i7 == -1) {
        }
        iArr2 = iArr;
        intent2.addFlags(16);
        if (!this.mProcessesReady) {
            intent2.addFlags(1073741824);
        }
        if (iIntentReceiver != null) {
            if (!this.mEnableModernQueue) {
            }
            if (!UserHandle.isCore(i4)) {
            }
        }
        iArr3 = iArr2;
        handleIncomingUser = this.mUserController.handleIncomingUser(i3, i4, i7, true, 0, INetd.IF_FLAG_BROADCAST, str);
        if (handleIncomingUser == -1) {
        }
        action = intent2.getAction();
        if (broadcastOptions != null) {
        }
        if (iIntentReceiver == null) {
        }
        broadcastOptions2 = broadcastOptions;
        if (!this.mEnableModernQueue) {
        }
    }

    public final int getUidFromIntent(Intent intent) {
        if (intent == null) {
            return -1;
        }
        Bundle extras = intent.getExtras();
        if (intent.hasExtra("android.intent.extra.UID")) {
            return extras.getInt("android.intent.extra.UID");
        }
        return -1;
    }

    public final void rotateBroadcastStatsIfNeededLocked() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        BroadcastStats broadcastStats = this.mCurBroadcastStats;
        if (broadcastStats == null || broadcastStats.mStartRealtime + BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS < elapsedRealtime) {
            this.mLastBroadcastStats = broadcastStats;
            if (broadcastStats != null) {
                broadcastStats.mEndRealtime = SystemClock.elapsedRealtime();
                this.mLastBroadcastStats.mEndUptime = SystemClock.uptimeMillis();
            }
            this.mCurBroadcastStats = new BroadcastStats();
        }
    }

    public final void addBroadcastStatLocked(String str, String str2, int i, int i2, long j) {
        rotateBroadcastStatsIfNeededLocked();
        this.mCurBroadcastStats.addBroadcast(str, str2, i, i2, j);
    }

    public final void addBackgroundCheckViolationLocked(String str, String str2) {
        rotateBroadcastStatsIfNeededLocked();
        this.mCurBroadcastStats.addBackgroundCheckViolation(str, str2);
    }

    public final void notifyBroadcastFinishedLocked(BroadcastRecord broadcastRecord) {
        ProcessRecord processRecord = broadcastRecord.callerApp;
        ApplicationInfo applicationInfo = processRecord != null ? processRecord.info : null;
        String str = applicationInfo != null ? applicationInfo.packageName : broadcastRecord.callerPackage;
        if (str != null) {
            this.mHandler.obtainMessage(74, broadcastRecord.callingUid, 0, str).sendToTarget();
        }
    }

    public final Intent verifyBroadcastLocked(Intent intent) {
        int callingUid;
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        int flags = intent.getFlags();
        if (!this.mProcessesReady && (67108864 & flags) == 0 && (1073741824 & flags) == 0) {
            Slog.e("ActivityManager", "Attempt to launch receivers of broadcast intent " + intent + " before boot completion");
            throw new IllegalStateException("Cannot broadcast before boot completed");
        } else if ((33554432 & flags) == 0) {
            if ((flags & 4194304) != 0 && (callingUid = Binder.getCallingUid()) != 0 && callingUid != 2000) {
                Slog.w("ActivityManager", "Removing FLAG_RECEIVER_FROM_SHELL because caller is UID " + Binder.getCallingUid());
                intent.removeFlags(4194304);
            }
            return intent;
        } else {
            throw new IllegalArgumentException("Can't use FLAG_RECEIVER_BOOT_UPGRADE here");
        }
    }

    @Deprecated
    public final int broadcastIntent(IApplicationThread iApplicationThread, Intent intent, String str, IIntentReceiver iIntentReceiver, int i, String str2, Bundle bundle, String[] strArr, int i2, Bundle bundle2, boolean z, boolean z2, int i3) {
        return broadcastIntentWithFeature(iApplicationThread, null, intent, str, iIntentReceiver, i, str2, bundle, strArr, null, null, i2, bundle2, z, z2, i3);
    }

    public final int broadcastIntentWithFeature(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IIntentReceiver iIntentReceiver, int i, String str3, Bundle bundle, String[] strArr, String[] strArr2, String[] strArr3, int i2, Bundle bundle2, boolean z, boolean z2, int i3) {
        int broadcastIntentLocked;
        enforceNotIsolatedCaller("broadcastIntent");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                Intent verifyBroadcastLocked = verifyBroadcastLocked(intent);
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                int callingPid = Binder.getCallingPid();
                int callingUid = Binder.getCallingUid();
                enforceBroadcastOptionPermissionsInternal(bundle2, callingUid);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                broadcastIntentLocked = broadcastIntentLocked(recordForAppLOSP, recordForAppLOSP != null ? recordForAppLOSP.info.packageName : null, str, verifyBroadcastLocked, str2, recordForAppLOSP, iIntentReceiver, i, str3, bundle, strArr, strArr2, strArr3, i2, bundle2, z, z2, callingPid, callingUid, callingUid, callingPid, i3, BackgroundStartPrivileges.NONE, null, null);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return broadcastIntentLocked;
    }

    public int broadcastIntentInPackage(String str, String str2, int i, int i2, int i3, Intent intent, String str3, ProcessRecord processRecord, IIntentReceiver iIntentReceiver, int i4, String str4, Bundle bundle, String str5, Bundle bundle2, boolean z, boolean z2, int i5, BackgroundStartPrivileges backgroundStartPrivileges, int[] iArr) {
        int broadcastIntentLocked;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                Intent verifyBroadcastLocked = verifyBroadcastLocked(intent);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                broadcastIntentLocked = broadcastIntentLocked(null, str, str2, verifyBroadcastLocked, str3, processRecord, iIntentReceiver, i4, str4, bundle, str5 == null ? null : new String[]{str5}, null, null, -1, bundle2, z, z2, -1, i, i2, i3, i5, backgroundStartPrivileges, iArr, null);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return broadcastIntentLocked;
    }

    public final void unbroadcastIntent(IApplicationThread iApplicationThread, Intent intent, int i) {
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        int handleIncomingUser = this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, 0, "removeStickyBroadcast", null);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (checkCallingPermission("android.permission.BROADCAST_STICKY") != 0) {
                    String str = "Permission Denial: unbroadcastIntent() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.BROADCAST_STICKY";
                    Slog.w("ActivityManager", str);
                    throw new SecurityException(str);
                }
                ArrayMap<String, ArrayList<Intent>> arrayMap = this.mStickyBroadcasts.get(handleIncomingUser);
                if (arrayMap != null) {
                    ArrayList<Intent> arrayList = arrayMap.get(intent.getAction());
                    if (arrayList != null) {
                        int size = arrayList.size();
                        int i2 = 0;
                        while (true) {
                            if (i2 >= size) {
                                break;
                            } else if (intent.filterEquals(arrayList.get(i2))) {
                                arrayList.remove(i2);
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (arrayList.size() <= 0) {
                            arrayMap.remove(intent.getAction());
                        }
                    }
                    if (arrayMap.size() <= 0) {
                        this.mStickyBroadcasts.remove(handleIncomingUser);
                    }
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void backgroundServicesFinishedLocked(int i) {
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.backgroundServicesFinishedLocked(i);
        }
    }

    public void finishReceiver(IBinder iBinder, int i, String str, Bundle bundle, boolean z, int i2) {
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Bundle");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iBinder);
                if (recordForAppLOSP == null) {
                    Slog.w("ActivityManager", "finishReceiver: no app for " + iBinder);
                    resetPriorityAfterLockedSection();
                    return;
                }
                broadcastQueueForFlags(i2).finishReceiverLocked(recordForAppLOSP, i, str, bundle, z, true);
                trimApplicationsLocked(false, 2);
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00b1 A[Catch: all -> 0x0304, TRY_ENTER, TryCatch #2 {all -> 0x0304, blocks: (B:11:0x003c, B:17:0x004b, B:19:0x0051, B:20:0x0065, B:23:0x006a, B:25:0x0074, B:26:0x008a, B:33:0x009b, B:36:0x00a7, B:37:0x00ac, B:46:0x00d2, B:52:0x00e1, B:55:0x00e7, B:57:0x00eb, B:58:0x0113, B:59:0x0158, B:60:0x0159, B:67:0x0167, B:70:0x016e, B:71:0x01af, B:72:0x01b0, B:74:0x01b6, B:80:0x01c2, B:88:0x01d7, B:90:0x01db, B:91:0x01f5, B:94:0x01fa, B:96:0x0207, B:102:0x022a, B:106:0x0245, B:110:0x0252, B:111:0x025c, B:125:0x02d5, B:127:0x02dc, B:129:0x02ec, B:131:0x02f1, B:132:0x02f4, B:138:0x02fe, B:139:0x0301, B:97:0x020e, B:99:0x0216, B:101:0x021d, B:87:0x01d0, B:40:0x00b1, B:42:0x00b7, B:43:0x00cd), top: B:148:0x003c }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01c2 A[Catch: all -> 0x0304, TryCatch #2 {all -> 0x0304, blocks: (B:11:0x003c, B:17:0x004b, B:19:0x0051, B:20:0x0065, B:23:0x006a, B:25:0x0074, B:26:0x008a, B:33:0x009b, B:36:0x00a7, B:37:0x00ac, B:46:0x00d2, B:52:0x00e1, B:55:0x00e7, B:57:0x00eb, B:58:0x0113, B:59:0x0158, B:60:0x0159, B:67:0x0167, B:70:0x016e, B:71:0x01af, B:72:0x01b0, B:74:0x01b6, B:80:0x01c2, B:88:0x01d7, B:90:0x01db, B:91:0x01f5, B:94:0x01fa, B:96:0x0207, B:102:0x022a, B:106:0x0245, B:110:0x0252, B:111:0x025c, B:125:0x02d5, B:127:0x02dc, B:129:0x02ec, B:131:0x02f1, B:132:0x02f4, B:138:0x02fe, B:139:0x0301, B:97:0x020e, B:99:0x0216, B:101:0x021d, B:87:0x01d0, B:40:0x00b1, B:42:0x00b7, B:43:0x00cd), top: B:148:0x003c }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x01ce A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01db A[Catch: all -> 0x0304, TryCatch #2 {all -> 0x0304, blocks: (B:11:0x003c, B:17:0x004b, B:19:0x0051, B:20:0x0065, B:23:0x006a, B:25:0x0074, B:26:0x008a, B:33:0x009b, B:36:0x00a7, B:37:0x00ac, B:46:0x00d2, B:52:0x00e1, B:55:0x00e7, B:57:0x00eb, B:58:0x0113, B:59:0x0158, B:60:0x0159, B:67:0x0167, B:70:0x016e, B:71:0x01af, B:72:0x01b0, B:74:0x01b6, B:80:0x01c2, B:88:0x01d7, B:90:0x01db, B:91:0x01f5, B:94:0x01fa, B:96:0x0207, B:102:0x022a, B:106:0x0245, B:110:0x0252, B:111:0x025c, B:125:0x02d5, B:127:0x02dc, B:129:0x02ec, B:131:0x02f1, B:132:0x02f4, B:138:0x02fe, B:139:0x0301, B:97:0x020e, B:99:0x0216, B:101:0x021d, B:87:0x01d0, B:40:0x00b1, B:42:0x00b7, B:43:0x00cd), top: B:148:0x003c }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x01fa A[Catch: all -> 0x0304, TRY_ENTER, TryCatch #2 {all -> 0x0304, blocks: (B:11:0x003c, B:17:0x004b, B:19:0x0051, B:20:0x0065, B:23:0x006a, B:25:0x0074, B:26:0x008a, B:33:0x009b, B:36:0x00a7, B:37:0x00ac, B:46:0x00d2, B:52:0x00e1, B:55:0x00e7, B:57:0x00eb, B:58:0x0113, B:59:0x0158, B:60:0x0159, B:67:0x0167, B:70:0x016e, B:71:0x01af, B:72:0x01b0, B:74:0x01b6, B:80:0x01c2, B:88:0x01d7, B:90:0x01db, B:91:0x01f5, B:94:0x01fa, B:96:0x0207, B:102:0x022a, B:106:0x0245, B:110:0x0252, B:111:0x025c, B:125:0x02d5, B:127:0x02dc, B:129:0x02ec, B:131:0x02f1, B:132:0x02f4, B:138:0x02fe, B:139:0x0301, B:97:0x020e, B:99:0x0216, B:101:0x021d, B:87:0x01d0, B:40:0x00b1, B:42:0x00b7, B:43:0x00cd), top: B:148:0x003c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean startInstrumentation(ComponentName componentName, String str, int i, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i2, String str2) {
        ApplicationInfo applicationInfo;
        ApplicationInfo applicationInfo2;
        InstrumentationInfo instrumentationInfo;
        int i3;
        boolean z;
        boolean z2;
        ActivityManagerGlobalLock activityManagerGlobalLock;
        ActiveInstrumentation activeInstrumentation;
        ApplicationInfo applicationInfo3;
        InstrumentationInfo instrumentationInfo2;
        boolean z3;
        ProcessRecord addAppLocked;
        ApplicationInfo applicationInfo4;
        InstrumentationInfo instrumentationInfoAsUser;
        enforceNotIsolatedCaller("startInstrumentation");
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int handleIncomingUser = this.mUserController.handleIncomingUser(callingPid, callingUid, i2, false, 2, "startInstrumentation", null);
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Bundle");
        }
        IPackageManager packageManager = AppGlobals.getPackageManager();
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                boolean z4 = (i & 8) != 0;
                InstrumentationInfo instrumentationInfo3 = null;
                try {
                    instrumentationInfoAsUser = packageManager.getInstrumentationInfoAsUser(componentName, 1024, handleIncomingUser);
                    try {
                    } catch (RemoteException unused) {
                        instrumentationInfo3 = instrumentationInfoAsUser;
                        applicationInfo = null;
                        applicationInfo2 = applicationInfo;
                        instrumentationInfo = instrumentationInfo3;
                        if (instrumentationInfo.targetPackage.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
                        }
                        try {
                            i3 = packageManager.checkSignatures(instrumentationInfo.targetPackage, instrumentationInfo.packageName, handleIncomingUser);
                        } catch (RemoteException unused2) {
                            i3 = -3;
                        }
                        if (i3 < 0) {
                            if (!Build.IS_DEBUGGABLE) {
                            }
                            String str3 = "Permission Denial: starting instrumentation " + componentName + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " not allowed because package " + instrumentationInfo.packageName + " does not have a signature matching the target " + instrumentationInfo.targetPackage;
                            reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, str3);
                            throw new SecurityException(str3);
                        }
                        if (!Build.IS_DEBUGGABLE) {
                            String str4 = "Permission Denial: instrumentation test " + componentName + " from pid=" + callingPid + ", uid=" + callingUid + ", pkgName=" + this.mInternal.getPackageNameByPid(callingPid) + " not allowed because it's not started from SHELL";
                            Slog.wtfQuiet("ActivityManager", str4);
                            reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, str4);
                            throw new SecurityException(str4);
                        }
                        if (!applicationInfo2.usesNonSdkApi()) {
                            z = false;
                            if (!z) {
                            }
                            z2 = true;
                            if (!z) {
                            }
                            enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                            if ((i & 32) == 0) {
                            }
                        }
                        z = true;
                        if (!z) {
                        }
                        z2 = true;
                        if (!z) {
                        }
                        enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                        if ((i & 32) == 0) {
                        }
                    }
                } catch (RemoteException unused3) {
                    applicationInfo = null;
                }
                if (instrumentationInfoAsUser == null) {
                    reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Unable to find instrumentation info for: " + componentName);
                    resetPriorityAfterLockedSection();
                    return false;
                }
                ApplicationInfo applicationInfo5 = packageManager.getApplicationInfo(instrumentationInfoAsUser.targetPackage, 1024L, handleIncomingUser);
                if (applicationInfo5 == null) {
                    reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Unable to find instrumentation target package: " + instrumentationInfoAsUser.targetPackage);
                    resetPriorityAfterLockedSection();
                    return false;
                }
                instrumentationInfo = instrumentationInfoAsUser;
                applicationInfo2 = applicationInfo5;
                if (instrumentationInfo.targetPackage.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
                    if (!applicationInfo2.hasCode()) {
                        reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Instrumentation target has no code: " + instrumentationInfo.targetPackage);
                        resetPriorityAfterLockedSection();
                        return false;
                    }
                } else if (!z4) {
                    reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Cannot instrument system server without 'no-restart'");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                i3 = packageManager.checkSignatures(instrumentationInfo.targetPackage, instrumentationInfo.packageName, handleIncomingUser);
                if (i3 < 0 && i3 != -1) {
                    if (!Build.IS_DEBUGGABLE && callingUid == 0 && (i & 16) == 0) {
                        Slog.w("ActivityManager", "Instrumentation test " + instrumentationInfo.packageName + " doesn't have a signature matching the target " + instrumentationInfo.targetPackage + ", which would not be allowed on the production Android builds");
                    } else {
                        String str32 = "Permission Denial: starting instrumentation " + componentName + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " not allowed because package " + instrumentationInfo.packageName + " does not have a signature matching the target " + instrumentationInfo.targetPackage;
                        reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, str32);
                        throw new SecurityException(str32);
                    }
                }
                if (!Build.IS_DEBUGGABLE && callingUid != 0 && callingUid != 2000 && callingUid != 1000 && !hasActiveInstrumentationLocked(callingPid)) {
                    String str42 = "Permission Denial: instrumentation test " + componentName + " from pid=" + callingPid + ", uid=" + callingUid + ", pkgName=" + this.mInternal.getPackageNameByPid(callingPid) + " not allowed because it's not started from SHELL";
                    Slog.wtfQuiet("ActivityManager", str42);
                    reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, str42);
                    throw new SecurityException(str42);
                }
                if (!applicationInfo2.usesNonSdkApi() && (i & 1) == 0) {
                    z = false;
                    if (!z && (i & 4) == 0) {
                        z2 = false;
                        if (!z || z2) {
                            enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                        }
                        if ((i & 32) == 0) {
                            boolean startInstrumentationOfSdkSandbox = startInstrumentationOfSdkSandbox(componentName, str, bundle, iInstrumentationWatcher, iUiAutomationConnection, handleIncomingUser, str2, instrumentationInfo, applicationInfo2, z4, z, z2);
                            resetPriorityAfterLockedSection();
                            return startInstrumentationOfSdkSandbox;
                        }
                        ActiveInstrumentation activeInstrumentation2 = new ActiveInstrumentation(this);
                        activeInstrumentation2.mClass = componentName;
                        String str5 = applicationInfo2.processName;
                        String str6 = instrumentationInfo.targetProcesses;
                        if (str6 == null) {
                            activeInstrumentation2.mTargetProcesses = new String[]{str5};
                        } else if (str6.equals("*")) {
                            activeInstrumentation2.mTargetProcesses = new String[0];
                        } else {
                            String[] split = instrumentationInfo.targetProcesses.split(",");
                            activeInstrumentation2.mTargetProcesses = split;
                            str5 = split[0];
                        }
                        String str7 = str5;
                        activeInstrumentation2.mTargetInfo = applicationInfo2;
                        activeInstrumentation2.mProfileFile = str;
                        activeInstrumentation2.mArguments = bundle;
                        activeInstrumentation2.mWatcher = iInstrumentationWatcher;
                        activeInstrumentation2.mUiAutomationConnection = iUiAutomationConnection;
                        activeInstrumentation2.mResultClass = componentName;
                        activeInstrumentation2.mHasBackgroundActivityStartsPermission = checkPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", callingPid, callingUid) == 0;
                        activeInstrumentation2.mHasBackgroundForegroundServiceStartsPermission = checkPermission("android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND", callingPid, callingUid) == 0;
                        activeInstrumentation2.mNoRestart = z4;
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        ActivityManagerGlobalLock activityManagerGlobalLock2 = this.mProcLock;
                        try {
                            synchronized (activityManagerGlobalLock2) {
                                try {
                                    boostPriorityForProcLockedSection();
                                    if (z4) {
                                        addAppLocked = getProcessRecordLocked(applicationInfo2.processName, applicationInfo2.uid);
                                        activityManagerGlobalLock = activityManagerGlobalLock2;
                                        activeInstrumentation = activeInstrumentation2;
                                        applicationInfo3 = applicationInfo2;
                                        instrumentationInfo2 = instrumentationInfo;
                                        z3 = z4;
                                    } else {
                                        activityManagerGlobalLock = activityManagerGlobalLock2;
                                        activeInstrumentation = activeInstrumentation2;
                                        applicationInfo3 = applicationInfo2;
                                        instrumentationInfo2 = instrumentationInfo;
                                        z3 = z4;
                                        forceStopPackageLocked(instrumentationInfo.targetPackage, -1, true, false, true, true, false, handleIncomingUser, "start instr");
                                        if (this.mUsageStatsService != null) {
                                            this.mUsageStatsService.reportEvent(instrumentationInfo2.targetPackage, handleIncomingUser, 6);
                                        }
                                        addAppLocked = addAppLocked(applicationInfo3, str7, false, z, z2, str2, 0);
                                        addAppLocked.mProfile.addHostingComponentType(8);
                                    }
                                    addAppLocked.setActiveInstrumentation(activeInstrumentation);
                                    activeInstrumentation.mFinished = false;
                                    activeInstrumentation.mSourceUid = callingUid;
                                    activeInstrumentation.mRunningProcesses.add(addAppLocked);
                                    if (!this.mActiveInstrumentation.contains(activeInstrumentation)) {
                                        this.mActiveInstrumentation.add(activeInstrumentation);
                                    }
                                    resetPriorityAfterProcLockedSection();
                                    if ((i & 2) != 0) {
                                        applicationInfo4 = applicationInfo3;
                                        this.mAppOpsService.setMode(99, applicationInfo4.uid, instrumentationInfo2.packageName, 0);
                                    } else {
                                        applicationInfo4 = applicationInfo3;
                                    }
                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                    if (z3) {
                                        instrumentWithoutRestart(activeInstrumentation, applicationInfo4);
                                    }
                                    resetPriorityAfterLockedSection();
                                    return true;
                                } catch (Throwable th) {
                                    th = th;
                                    resetPriorityAfterProcLockedSection();
                                    throw th;
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                        resetPriorityAfterProcLockedSection();
                        throw th;
                    }
                    z2 = true;
                    if (!z) {
                    }
                    enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                    if ((i & 32) == 0) {
                    }
                }
                z = true;
                if (!z) {
                    z2 = false;
                    if (!z) {
                    }
                    enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                    if ((i & 32) == 0) {
                    }
                }
                z2 = true;
                if (!z) {
                }
                enforceCallingPermission("android.permission.DISABLE_HIDDEN_API_CHECKS", "disable hidden API checks");
                if ((i & 32) == 0) {
                }
            } catch (Throwable th3) {
                resetPriorityAfterLockedSection();
                throw th3;
            }
        }
    }

    @GuardedBy({"this"})
    public final boolean hasActiveInstrumentationLocked(int i) {
        boolean z = false;
        if (i == 0) {
            return false;
        }
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = this.mPidsSelfLocked.get(i);
            if (processRecord != null && processRecord.getActiveInstrumentation() != null) {
                z = true;
            }
        }
        return z;
    }

    @GuardedBy({"this"})
    public final boolean startInstrumentationOfSdkSandbox(ComponentName componentName, String str, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i, String str2, InstrumentationInfo instrumentationInfo, ApplicationInfo applicationInfo, boolean z, boolean z2, boolean z3) {
        if (z) {
            reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Instrumenting sdk sandbox with --no-restart flag is not supported");
            return false;
        }
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(packageManager.getSdkSandboxPackageName(), 0, i);
            SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
            if (sdkSandboxManagerLocal == null) {
                reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Can't locate SdkSandboxManagerLocal");
                return false;
            }
            String sdkSandboxProcessNameForInstrumentation = sdkSandboxManagerLocal.getSdkSandboxProcessNameForInstrumentation(applicationInfo);
            ActiveInstrumentation activeInstrumentation = new ActiveInstrumentation(this);
            activeInstrumentation.mClass = componentName;
            activeInstrumentation.mTargetProcesses = new String[]{sdkSandboxProcessNameForInstrumentation};
            activeInstrumentation.mTargetInfo = applicationInfoAsUser;
            activeInstrumentation.mProfileFile = str;
            activeInstrumentation.mArguments = bundle;
            activeInstrumentation.mWatcher = iInstrumentationWatcher;
            activeInstrumentation.mUiAutomationConnection = iUiAutomationConnection;
            activeInstrumentation.mResultClass = componentName;
            activeInstrumentation.mHasBackgroundActivityStartsPermission = false;
            activeInstrumentation.mHasBackgroundForegroundServiceStartsPermission = false;
            activeInstrumentation.mNoRestart = false;
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                sdkSandboxManagerLocal.notifyInstrumentationStarted(applicationInfo.packageName, applicationInfo.uid);
                try {
                    synchronized (this.mProcLock) {
                        try {
                            boostPriorityForProcLockedSection();
                            int sdkSandboxUid = Process.toSdkSandboxUid(applicationInfo.uid);
                            forceStopPackageLocked(instrumentationInfo.targetPackage, -1, true, false, true, true, false, i, "start instr");
                            ProcessRecord addAppLocked = addAppLocked(applicationInfoAsUser, sdkSandboxProcessNameForInstrumentation, false, true, sdkSandboxUid, applicationInfo.packageName, z2, z3, str2, 0);
                            addAppLocked.setActiveInstrumentation(activeInstrumentation);
                            activeInstrumentation.mFinished = false;
                            activeInstrumentation.mSourceUid = callingUid;
                            activeInstrumentation.mRunningProcesses.add(addAppLocked);
                            if (!this.mActiveInstrumentation.contains(activeInstrumentation)) {
                                this.mActiveInstrumentation.add(activeInstrumentation);
                            }
                            addAppLocked.mProfile.addHostingComponentType(8);
                            resetPriorityAfterProcLockedSection();
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return true;
                        } catch (Throwable th) {
                            th = th;
                            resetPriorityAfterProcLockedSection();
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th3;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            reportStartInstrumentationFailureLocked(iInstrumentationWatcher, componentName, "Can't find SdkSandbox package");
            return false;
        }
    }

    public final void instrumentWithoutRestart(ActiveInstrumentation activeInstrumentation, ApplicationInfo applicationInfo) {
        ProcessRecord processRecordLocked;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                processRecordLocked = getProcessRecordLocked(applicationInfo.processName, applicationInfo.uid);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        try {
            processRecordLocked.getThread().instrumentWithoutRestart(activeInstrumentation.mClass, activeInstrumentation.mArguments, activeInstrumentation.mWatcher, activeInstrumentation.mUiAutomationConnection, applicationInfo);
        } catch (RemoteException e) {
            Slog.i("ActivityManager", "RemoteException from instrumentWithoutRestart", e);
        }
    }

    public final boolean isCallerShell() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    public final void reportStartInstrumentationFailureLocked(IInstrumentationWatcher iInstrumentationWatcher, ComponentName componentName, String str) {
        Slog.w("ActivityManager", str);
        if (iInstrumentationWatcher != null) {
            Bundle bundle = new Bundle();
            bundle.putString("id", "ActivityManagerService");
            bundle.putString("Error", str);
            this.mInstrumentationReporter.reportStatus(iInstrumentationWatcher, componentName, -1, bundle);
        }
    }

    public void addInstrumentationResultsLocked(ProcessRecord processRecord, Bundle bundle) {
        ActiveInstrumentation activeInstrumentation = processRecord.getActiveInstrumentation();
        if (activeInstrumentation == null) {
            Slog.w("ActivityManager", "finishInstrumentation called on non-instrumented: " + processRecord);
        } else if (activeInstrumentation.mFinished || bundle == null) {
        } else {
            Bundle bundle2 = activeInstrumentation.mCurResults;
            if (bundle2 == null) {
                activeInstrumentation.mCurResults = new Bundle(bundle);
            } else {
                bundle2.putAll(bundle);
            }
        }
    }

    public void addInstrumentationResults(IApplicationThread iApplicationThread, Bundle bundle) {
        UserHandle.getCallingUserId();
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    Slog.w("ActivityManager", "addInstrumentationResults: no app for " + iApplicationThread);
                    resetPriorityAfterLockedSection();
                    return;
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                addInstrumentationResultsLocked(recordForAppLOSP, bundle);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @GuardedBy({"this"})
    public void finishInstrumentationLocked(ProcessRecord processRecord, int i, Bundle bundle) {
        try {
            Trace.traceBegin(64L, "finishInstrumentationLocked()");
            ActiveInstrumentation activeInstrumentation = processRecord.getActiveInstrumentation();
            if (activeInstrumentation == null) {
                Slog.w("ActivityManager", "finishInstrumentation called on non-instrumented: " + processRecord);
                return;
            }
            synchronized (this.mProcLock) {
                boostPriorityForProcLockedSection();
                if (!activeInstrumentation.mFinished) {
                    if (activeInstrumentation.mWatcher != null) {
                        Bundle bundle2 = activeInstrumentation.mCurResults;
                        if (bundle2 != null) {
                            if (bundle2 != null && bundle != null) {
                                bundle2.putAll(bundle);
                            }
                            bundle = bundle2;
                        }
                        this.mInstrumentationReporter.reportFinished(activeInstrumentation.mWatcher, activeInstrumentation.mClass, i, bundle);
                    }
                    if (activeInstrumentation.mUiAutomationConnection != null) {
                        this.mAppOpsService.setMode(99, processRecord.uid, processRecord.info.packageName, 2);
                        this.mAppOpsService.setAppOpsServiceDelegate(null);
                        getPermissionManagerInternal().stopShellPermissionIdentityDelegation();
                        this.mHandler.obtainMessage(56, activeInstrumentation.mUiAutomationConnection).sendToTarget();
                    }
                    activeInstrumentation.mFinished = true;
                }
                activeInstrumentation.removeProcess(processRecord);
                processRecord.setActiveInstrumentation(null);
            }
            resetPriorityAfterProcLockedSection();
            processRecord.mProfile.clearHostingComponentType(8);
            if (processRecord.isSdkSandbox) {
                killUid(UserHandle.getAppId(processRecord.uid), UserHandle.getUserId(processRecord.uid), "finished instr");
                SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
                if (sdkSandboxManagerLocal != null) {
                    sdkSandboxManagerLocal.notifyInstrumentationFinished(processRecord.sdkSandboxClientAppPackage, Process.getAppUidForSdkSandboxUid(processRecord.uid));
                }
            } else if (!activeInstrumentation.mNoRestart) {
                forceStopPackageLocked(processRecord.info.packageName, -1, false, false, true, true, false, processRecord.userId, "finished inst");
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void finishInstrumentation(IApplicationThread iApplicationThread, int i, Bundle bundle) {
        UserHandle.getCallingUserId();
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    Slog.w("ActivityManager", "finishInstrumentation: no app for " + iApplicationThread);
                    resetPriorityAfterLockedSection();
                    return;
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                finishInstrumentationLocked(recordForAppLOSP, i, bundle);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
        return this.mActivityTaskManager.getFocusedRootTaskInfo();
    }

    public Configuration getConfiguration() {
        return this.mActivityTaskManager.getConfiguration();
    }

    public void suppressResizeConfigChanges(boolean z) throws RemoteException {
        this.mActivityTaskManager.suppressResizeConfigChanges(z);
    }

    public void updatePersistentConfiguration(Configuration configuration) {
        updatePersistentConfigurationWithAttribution(configuration, Settings.getPackageNameForUid(this.mContext, Binder.getCallingUid()), null);
    }

    public void updatePersistentConfigurationWithAttribution(Configuration configuration, String str, String str2) {
        enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "updatePersistentConfiguration()");
        enforceWriteSettingsPermission("updatePersistentConfiguration()", str, str2);
        if (configuration == null) {
            throw new NullPointerException("Configuration must not be null");
        }
        this.mActivityTaskManager.updatePersistentConfiguration(configuration, UserHandle.getCallingUserId());
    }

    public final void enforceWriteSettingsPermission(String str, String str2, String str3) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0 || Settings.checkAndNoteWriteSettingsOperation(this.mContext, callingUid, str2, str3, false)) {
            return;
        }
        String str4 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + callingUid + " requires android.permission.WRITE_SETTINGS";
        Slog.w("ActivityManager", str4);
        throw new SecurityException(str4);
    }

    public boolean updateConfiguration(Configuration configuration) {
        return this.mActivityTaskManager.updateConfiguration(configuration);
    }

    public boolean updateMccMncConfiguration(String str, String str2) {
        try {
            int parseInt = Integer.parseInt(str);
            int parseInt2 = Integer.parseInt(str2);
            Configuration configuration = new Configuration();
            configuration.mcc = parseInt;
            if (parseInt2 == 0) {
                parseInt2 = GnssNative.GNSS_AIDING_TYPE_ALL;
            }
            configuration.mnc = parseInt2;
            return this.mActivityTaskManager.updateConfiguration(configuration);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            Slog.e("ActivityManager", "Error parsing mcc: " + str + " mnc: " + str2 + ". ex=" + e);
            return false;
        }
    }

    public int getLaunchedFromUid(IBinder iBinder) {
        return ActivityClient.getInstance().getLaunchedFromUid(iBinder);
    }

    public String getLaunchedFromPackage(IBinder iBinder) {
        return ActivityClient.getInstance().getLaunchedFromPackage(iBinder);
    }

    public boolean isReceivingBroadcastLocked(ProcessRecord processRecord, int[] iArr) {
        int i = Integer.MIN_VALUE;
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            i = Math.max(i, broadcastQueue.getPreferredSchedulingGroupLocked(processRecord));
        }
        iArr[0] = i;
        return i != Integer.MIN_VALUE;
    }

    public Association startAssociationLocked(int i, String str, int i2, int i3, long j, ComponentName componentName, String str2) {
        if (this.mTrackingAssociations) {
            ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>> arrayMap = this.mAssociations.get(i3);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mAssociations.put(i3, arrayMap);
            }
            SparseArray<ArrayMap<String, Association>> sparseArray = arrayMap.get(componentName);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                arrayMap.put(componentName, sparseArray);
            }
            ArrayMap<String, Association> arrayMap2 = sparseArray.get(i);
            if (arrayMap2 == null) {
                arrayMap2 = new ArrayMap<>();
                sparseArray.put(i, arrayMap2);
            }
            Association association = arrayMap2.get(str);
            if (association == null) {
                association = new Association(i, str, i3, componentName, str2);
                arrayMap2.put(str, association);
            }
            association.mCount++;
            int i4 = association.mNesting + 1;
            association.mNesting = i4;
            if (i4 == 1) {
                long uptimeMillis = SystemClock.uptimeMillis();
                association.mLastStateUptime = uptimeMillis;
                association.mStartTime = uptimeMillis;
                association.mLastState = i2;
            }
            return association;
        }
        return null;
    }

    public void stopAssociationLocked(int i, String str, int i2, long j, ComponentName componentName, String str2) {
        ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>> arrayMap;
        SparseArray<ArrayMap<String, Association>> sparseArray;
        ArrayMap<String, Association> arrayMap2;
        Association association;
        int i3;
        if (!this.mTrackingAssociations || (arrayMap = this.mAssociations.get(i2)) == null || (sparseArray = arrayMap.get(componentName)) == null || (arrayMap2 = sparseArray.get(i)) == null || (association = arrayMap2.get(str)) == null || (i3 = association.mNesting) <= 0) {
            return;
        }
        int i4 = i3 - 1;
        association.mNesting = i4;
        if (i4 == 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            association.mTime += uptimeMillis - association.mStartTime;
            long[] jArr = association.mStateTimes;
            int i5 = association.mLastState + 0;
            jArr[i5] = jArr[i5] + (uptimeMillis - association.mLastStateUptime);
            association.mLastState = 22;
        }
    }

    public void noteUidProcessState(int i, int i2, int i3) {
        int i4;
        ActivityManagerService activityManagerService = this;
        activityManagerService.mBatteryStatsService.noteUidProcessState(i, i2);
        activityManagerService.mAppOpsService.updateUidProcState(i, i2, i3);
        if (activityManagerService.mTrackingAssociations) {
            int size = activityManagerService.mAssociations.size();
            int i5 = 0;
            int i6 = 0;
            while (i6 < size) {
                ArrayMap<ComponentName, SparseArray<ArrayMap<String, Association>>> valueAt = activityManagerService.mAssociations.valueAt(i6);
                int size2 = valueAt.size();
                int i7 = i5;
                while (i7 < size2) {
                    ArrayMap<String, Association> arrayMap = valueAt.valueAt(i7).get(i);
                    if (arrayMap != null) {
                        int size3 = arrayMap.size();
                        int i8 = i5;
                        while (i8 < size3) {
                            Association valueAt2 = arrayMap.valueAt(i8);
                            if (valueAt2.mNesting >= 1) {
                                long uptimeMillis = SystemClock.uptimeMillis();
                                long[] jArr = valueAt2.mStateTimes;
                                int i9 = valueAt2.mLastState - i5;
                                i4 = i6;
                                jArr[i9] = jArr[i9] + (uptimeMillis - valueAt2.mLastStateUptime);
                                valueAt2.mLastState = i2;
                                valueAt2.mLastStateUptime = uptimeMillis;
                            } else {
                                i4 = i6;
                            }
                            i8++;
                            i6 = i4;
                            i5 = 0;
                        }
                    }
                    i7++;
                    i6 = i6;
                    i5 = 0;
                }
                i6++;
                activityManagerService = this;
                i5 = 0;
            }
        }
    }

    @GuardedBy({"this"})
    public final boolean canGcNowLocked() {
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            if (!broadcastQueue.isIdleLocked()) {
                return false;
            }
        }
        return this.mAtmInternal.canGcNow();
    }

    public final void checkExcessivePowerUsage() {
        updateCpuStatsNow();
        final boolean z = this.mSystemReady && FeatureFlagUtils.isEnabled(this.mContext, "settings_enable_monitor_phantom_procs");
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                final boolean z2 = this.mLastPowerCheckUptime != 0;
                final long uptimeMillis = SystemClock.uptimeMillis();
                final long j = uptimeMillis - this.mLastPowerCheckUptime;
                this.mLastPowerCheckUptime = uptimeMillis;
                this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda23
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.this.lambda$checkExcessivePowerUsage$22(uptimeMillis, j, z2, z, (ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkExcessivePowerUsage$22(long j, long j2, boolean z, boolean z2, ProcessRecord processRecord) {
        int i;
        if (processRecord.getThread() != null && processRecord.mState.getSetProcState() >= 14) {
            long whenUnimportant = j - processRecord.mState.getWhenUnimportant();
            ActivityManagerConstants activityManagerConstants = this.mConstants;
            long j3 = activityManagerConstants.POWER_CHECK_INTERVAL;
            if (whenUnimportant <= j3) {
                i = activityManagerConstants.POWER_CHECK_MAX_CPU_1;
            } else if (whenUnimportant <= j3 * 2 || processRecord.mState.getSetProcState() <= 14) {
                i = this.mConstants.POWER_CHECK_MAX_CPU_2;
            } else {
                ActivityManagerConstants activityManagerConstants2 = this.mConstants;
                if (whenUnimportant <= activityManagerConstants2.POWER_CHECK_INTERVAL * 3) {
                    i = activityManagerConstants2.POWER_CHECK_MAX_CPU_3;
                } else {
                    i = activityManagerConstants2.POWER_CHECK_MAX_CPU_4;
                }
            }
            int i2 = i;
            updateAppProcessCpuTimeLPr(j2, z, whenUnimportant, i2, processRecord);
            if (z2) {
                updatePhantomProcessCpuTimeLPr(j2, z, whenUnimportant, i2, processRecord);
            }
        }
    }

    @GuardedBy({"mProcLock"})
    public final void updateAppProcessCpuTimeLPr(final long j, boolean z, final long j2, final int i, final ProcessRecord processRecord) {
        synchronized (this.mAppProfiler.mProfilerLock) {
            ProcessProfileRecord processProfileRecord = processRecord.mProfile;
            long j3 = processProfileRecord.mCurCpuTime.get();
            long j4 = processProfileRecord.mLastCpuTime.get();
            if (j4 > 0) {
                final long j5 = j3 - j4;
                if (checkExcessivePowerUsageLPr(j, z, j5, processRecord.processName, processRecord.toShortString(), i, processRecord)) {
                    this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda30
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActivityManagerService.this.lambda$updateAppProcessCpuTimeLPr$23(processRecord, j5, j, j2, i);
                        }
                    });
                    processProfileRecord.reportExcessiveCpu();
                }
            }
            processProfileRecord.mLastCpuTime.set(j3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAppProcessCpuTimeLPr$23(ProcessRecord processRecord, long j, long j2, long j3, int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (processRecord.getThread() != null && processRecord.mState.getSetProcState() >= 14) {
                    processRecord.killLocked("excessive cpu " + j + " during " + j2 + " dur=" + j3 + " limit=" + i, 9, 7, true);
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @GuardedBy({"mProcLock"})
    public final void updatePhantomProcessCpuTimeLPr(final long j, final boolean z, final long j2, final int i, final ProcessRecord processRecord) {
        this.mPhantomProcessList.forEachPhantomProcessOfApp(processRecord, new Function() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda36
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$updatePhantomProcessCpuTimeLPr$25;
                lambda$updatePhantomProcessCpuTimeLPr$25 = ActivityManagerService.this.lambda$updatePhantomProcessCpuTimeLPr$25(j, z, processRecord, i, j2, (PhantomProcessRecord) obj);
                return lambda$updatePhantomProcessCpuTimeLPr$25;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updatePhantomProcessCpuTimeLPr$25(final long j, boolean z, final ProcessRecord processRecord, final int i, final long j2, final PhantomProcessRecord phantomProcessRecord) {
        long j3 = phantomProcessRecord.mLastCputime;
        if (j3 > 0) {
            final long j4 = phantomProcessRecord.mCurrentCputime - j3;
            if (checkExcessivePowerUsageLPr(j, z, j4, processRecord.processName, phantomProcessRecord.toString(), i, processRecord)) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityManagerService.this.lambda$updatePhantomProcessCpuTimeLPr$24(processRecord, phantomProcessRecord, j4, j, j2, i);
                    }
                });
                return Boolean.FALSE;
            }
        }
        phantomProcessRecord.mLastCputime = phantomProcessRecord.mCurrentCputime;
        return Boolean.TRUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePhantomProcessCpuTimeLPr$24(ProcessRecord processRecord, PhantomProcessRecord phantomProcessRecord, long j, long j2, long j3, int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                if (processRecord.getThread() != null && processRecord.mState.getSetProcState() >= 14) {
                    PhantomProcessList phantomProcessList = this.mPhantomProcessList;
                    phantomProcessList.killPhantomProcessGroupLocked(processRecord, phantomProcessRecord, 9, 7, "excessive cpu " + j + " during " + j2 + " dur=" + j3 + " limit=" + i);
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @GuardedBy({"mProcLock"})
    public final boolean checkExcessivePowerUsageLPr(long j, boolean z, long j2, final String str, String str2, int i, final ProcessRecord processRecord) {
        if (!z || j <= 0 || (100 * j2) / j < i) {
            return false;
        }
        this.mBatteryStatsService.reportExcessiveCpu(processRecord.info.uid, processRecord.processName, j, j2);
        processRecord.getPkgList().forEachPackageProcessStats(new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda39
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ActivityManagerService.lambda$checkExcessivePowerUsageLPr$26(ProcessRecord.this, str, (ProcessStats.ProcessStateHolder) obj);
            }
        });
        return true;
    }

    public static /* synthetic */ void lambda$checkExcessivePowerUsageLPr$26(ProcessRecord processRecord, String str, ProcessStats.ProcessStateHolder processStateHolder) {
        ProcessState processState = processStateHolder.state;
        FrameworkStatsLog.write(16, processRecord.info.uid, str, processState != null ? processState.getPackage() : processRecord.info.packageName, processStateHolder.appVersion);
    }

    public final boolean isEphemeralLocked(int i) {
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length != 1) {
            return false;
        }
        return getPackageManagerInternal().isPackageEphemeral(UserHandle.getUserId(i), packagesForUid[0]);
    }

    @GuardedBy({"this"})
    public void enqueueUidChangeLocked(UidRecord uidRecord, int i, int i2) {
        if (uidRecord != null) {
            i = uidRecord.getUid();
        }
        if (i < 0) {
            throw new IllegalArgumentException("No UidRecord or uid");
        }
        int setProcState = uidRecord != null ? uidRecord.getSetProcState() : 20;
        long j = uidRecord != null ? uidRecord.curProcStateSeq : 0L;
        int setCapability = uidRecord != null ? uidRecord.getSetCapability() : 0;
        boolean isEphemeral = uidRecord != null ? uidRecord.isEphemeral() : isEphemeralLocked(i);
        if (uidRecord != null && uidRecord.isIdle() && (i2 & 2) != 0) {
            this.mProcessList.killAppIfBgRestrictedAndCachedIdleLocked(uidRecord);
        }
        if (uidRecord != null && !uidRecord.isIdle() && (i2 & 1) != 0) {
            i2 |= 2;
        }
        int enqueueUidChange = this.mUidObserverController.enqueueUidChange(uidRecord == null ? null : uidRecord.pendingChange, i, i2, setProcState, j, setCapability, isEphemeral);
        if (uidRecord != null) {
            uidRecord.setLastReportedChange(enqueueUidChange);
        }
        PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
        if (powerManagerInternal != null) {
            if ((enqueueUidChange & 4) != 0) {
                powerManagerInternal.uidActive(i);
            }
            if ((enqueueUidChange & 2) != 0) {
                this.mLocalPowerManager.uidIdle(i);
            }
            if ((enqueueUidChange & 1) != 0) {
                this.mLocalPowerManager.uidGone(i);
            } else if ((Integer.MIN_VALUE & enqueueUidChange) != 0) {
                this.mLocalPowerManager.updateUidProcState(i, setProcState);
            }
        }
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public final void setProcessTrackerStateLOSP(ProcessRecord processRecord, int i) {
        if (processRecord.getThread() != null) {
            processRecord.mProfile.setProcessTrackerState(processRecord.mState.getReportedProcState(), i);
        }
    }

    @GuardedBy({"this"})
    public final void clearProcessForegroundLocked(ProcessRecord processRecord) {
        updateProcessForegroundLocked(processRecord, false, 0, false, false);
    }

    @GuardedBy({"this"})
    public final void updateProcessForegroundLocked(ProcessRecord processRecord, boolean z, int i, boolean z2, boolean z3) {
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        boolean z4 = z != processServiceRecord.hasForegroundServices();
        if (z4 || !processServiceRecord.areForegroundServiceTypesSame(i, z2)) {
            if (z4) {
                for (int size = this.mForegroundServiceStateListeners.size() - 1; size >= 0; size--) {
                    this.mForegroundServiceStateListeners.get(size).onForegroundServiceStateChanged(processRecord.info.packageName, processRecord.info.uid, processRecord.getPid(), z);
                }
            }
            processServiceRecord.setHasForegroundServices(z, i, z2);
            ArrayList arrayList = (ArrayList) this.mForegroundPackages.get(processRecord.info.packageName, processRecord.info.uid);
            if (z) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.mForegroundPackages.put(processRecord.info.packageName, processRecord.info.uid, arrayList);
                }
                if (!arrayList.contains(processRecord)) {
                    arrayList.add(processRecord);
                    this.mBatteryStatsService.noteEvent(32770, processRecord.info.packageName, processRecord.info.uid);
                }
            } else if (arrayList != null && arrayList.remove(processRecord)) {
                this.mBatteryStatsService.noteEvent(16386, processRecord.info.packageName, processRecord.info.uid);
                if (arrayList.size() <= 0) {
                    this.mForegroundPackages.remove(processRecord.info.packageName, processRecord.info.uid);
                }
            }
            processServiceRecord.setReportedForegroundServiceTypes(i);
            ProcessChangeItem enqueueProcessChangeItemLocked = this.mProcessList.enqueueProcessChangeItemLocked(processRecord.getPid(), processRecord.info.uid);
            enqueueProcessChangeItemLocked.changes |= 2;
            enqueueProcessChangeItemLocked.foregroundServiceTypes = i;
        }
        if (z3) {
            updateOomAdjLocked(processRecord, 9);
        }
    }

    public ProcessRecord getTopApp() {
        int i;
        String str;
        ActivityTaskManagerInternal activityTaskManagerInternal = this.mAtmInternal;
        String str2 = null;
        WindowProcessController topApp = activityTaskManagerInternal != null ? activityTaskManagerInternal.getTopApp() : null;
        ProcessRecord processRecord = topApp != null ? (ProcessRecord) topApp.mOwner : null;
        if (processRecord != null) {
            str2 = processRecord.processName;
            i = processRecord.info.uid;
        } else {
            i = -1;
        }
        synchronized (this.mCurResumedAppLock) {
            if (i != this.mCurResumedUid || (str2 != (str = this.mCurResumedPackage) && (str2 == null || !str2.equals(str)))) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                String str3 = this.mCurResumedPackage;
                if (str3 != null) {
                    this.mBatteryStatsService.noteEvent(16387, str3, this.mCurResumedUid);
                }
                this.mCurResumedPackage = str2;
                this.mCurResumedUid = i;
                if (str2 != null) {
                    this.mBatteryStatsService.noteEvent(32771, str2, i);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return processRecord;
    }

    @GuardedBy({"this"})
    public void enqueueOomAdjTargetLocked(ProcessRecord processRecord) {
        this.mOomAdjuster.enqueueOomAdjTargetLocked(processRecord);
    }

    @GuardedBy({"this"})
    public void removeOomAdjTargetLocked(ProcessRecord processRecord, boolean z) {
        this.mOomAdjuster.removeOomAdjTargetLocked(processRecord, z);
    }

    @GuardedBy({"this"})
    public void updateOomAdjPendingTargetsLocked(int i) {
        this.mOomAdjuster.updateOomAdjPendingTargetsLocked(i);
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ProcStatsRunnable */
    /* loaded from: classes.dex */
    public static final class ProcStatsRunnable implements Runnable {
        public final ProcessStatsService mProcessStats;
        public final ActivityManagerService mService;

        public ProcStatsRunnable(ActivityManagerService activityManagerService, ProcessStatsService processStatsService) {
            this.mService = activityManagerService;
            this.mProcessStats = processStatsService;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mProcessStats.writeStateAsync();
        }
    }

    @GuardedBy({"this"})
    public final void updateOomAdjLocked(int i) {
        this.mOomAdjuster.updateOomAdjLocked(i);
    }

    @GuardedBy({"this"})
    public final boolean updateOomAdjLocked(ProcessRecord processRecord, int i) {
        return this.mOomAdjuster.updateOomAdjLocked(processRecord, i);
    }

    public void makePackageIdle(String str, int i) {
        int i2;
        if (checkCallingPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            String str2 = "Permission Denial: makePackageIdle() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.FORCE_STOP_PACKAGES";
            Slog.w("ActivityManager", str2);
            throw new SecurityException(str2);
        }
        int handleIncomingUser = this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, 2, "makePackageIdle", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                i2 = AppGlobals.getPackageManager().getPackageUid(str, 268443648L, 0);
            } catch (RemoteException unused) {
                i2 = -1;
            }
            if (i2 == -1) {
                throw new IllegalArgumentException("Unknown package name " + str);
            }
            synchronized (this) {
                try {
                    boostPriorityForLockedSection();
                    PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
                    if (powerManagerInternal != null) {
                        powerManagerInternal.startUidChanges();
                    }
                    int appId = UserHandle.getAppId(i2);
                    for (int size = this.mProcessList.mActiveUids.size() - 1; size >= 0; size--) {
                        UidRecord valueAt = this.mProcessList.mActiveUids.valueAt(size);
                        if (valueAt.getLastBackgroundTime() > 0 && !valueAt.isIdle()) {
                            int uid = valueAt.getUid();
                            if (UserHandle.getAppId(uid) == appId && (handleIncomingUser == -1 || handleIncomingUser == UserHandle.getUserId(uid))) {
                                EventLogTags.writeAmUidIdle(uid);
                                synchronized (this.mProcLock) {
                                    boostPriorityForProcLockedSection();
                                    valueAt.setIdle(true);
                                    valueAt.setSetIdle(true);
                                }
                                resetPriorityAfterProcLockedSection();
                                Slog.w("ActivityManager", "Idling uid " + UserHandle.formatUid(uid) + " from package " + str + " user " + handleIncomingUser);
                                doStopUidLocked(uid, valueAt);
                            }
                        }
                    }
                } finally {
                    PowerManagerInternal powerManagerInternal2 = this.mLocalPowerManager;
                    if (powerManagerInternal2 != null) {
                        powerManagerInternal2.finishUidChanges();
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void idleUids() {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                this.mOomAdjuster.idleUidsLocked();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void runInBackgroundDisabled(int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                UidRecord uidRecordLOSP = this.mProcessList.getUidRecordLOSP(i);
                if (uidRecordLOSP != null) {
                    if (uidRecordLOSP.isIdle()) {
                        doStopUidLocked(uidRecordLOSP.getUid(), uidRecordLOSP);
                    }
                } else {
                    doStopUidLocked(i, null);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void cameraActiveChanged(int i, boolean z) {
        synchronized (this.mActiveCameraUids) {
            int indexOf = this.mActiveCameraUids.indexOf(i);
            if (z) {
                if (indexOf < 0) {
                    this.mActiveCameraUids.add(i);
                }
            } else if (indexOf >= 0) {
                this.mActiveCameraUids.remove(indexOf);
            }
        }
    }

    public final boolean isCameraActiveForUid(int i) {
        boolean z;
        synchronized (this.mActiveCameraUids) {
            z = this.mActiveCameraUids.indexOf(i) >= 0;
        }
        return z;
    }

    @GuardedBy({"this"})
    public final void doStopUidLocked(int i, UidRecord uidRecord) {
        this.mServices.stopInBackgroundLocked(i);
        enqueueUidChangeLocked(uidRecord, i, -2147483646);
    }

    @GuardedBy({"this"})
    public void tempAllowlistForPendingIntentLocked(int i, int i2, int i3, long j, int i4, int i5, String str) {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = this.mPidsSelfLocked.get(i);
            if (processRecord == null) {
                Slog.w("ActivityManager", "tempAllowlistForPendingIntentLocked() no ProcessRecord for pid " + i);
            } else if (processRecord.mServices.mAllowlistManager || checkPermission("android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST", i, i2) == 0 || checkPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i, i2) == 0 || checkPermission("android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND", i, i2) == 0) {
                tempAllowlistUidLocked(i3, j, i5, str, i4, i2);
            }
        }
    }

    @GuardedBy({"this"})
    public void tempAllowlistUidLocked(int i, long j, int i2, String str, int i3, int i4) {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                DeviceIdleInternal deviceIdleInternal = this.mLocalDeviceIdleController;
                int tempAllowListType = deviceIdleInternal != null ? deviceIdleInternal.getTempAllowListType(i2, i3) : i3;
                if (tempAllowListType == -1) {
                    resetPriorityAfterProcLockedSection();
                    return;
                }
                this.mPendingTempAllowlist.put(i, new PendingTempAllowlist(i, j, i2, str, tempAllowListType, i4));
                setUidTempAllowlistStateLSP(i, true);
                this.mUiHandler.obtainMessage(68).sendToTarget();
                if (tempAllowListType == 0) {
                    this.mFgsStartTempAllowList.add(i, j, new FgsTempAllowListItem(j, i2, str, i4));
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public void pushTempAllowlist() {
        int size;
        PendingTempAllowlist[] pendingTempAllowlistArr;
        int i;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    size = this.mPendingTempAllowlist.size();
                    pendingTempAllowlistArr = new PendingTempAllowlist[size];
                    for (int i2 = 0; i2 < size; i2++) {
                        pendingTempAllowlistArr[i2] = this.mPendingTempAllowlist.valueAt(i2);
                    }
                }
                resetPriorityAfterProcLockedSection();
            } finally {
            }
        }
        resetPriorityAfterLockedSection();
        if (this.mLocalDeviceIdleController != null) {
            for (int i3 = 0; i3 < size; i3++) {
                PendingTempAllowlist pendingTempAllowlist = pendingTempAllowlistArr[i3];
                this.mLocalDeviceIdleController.addPowerSaveTempWhitelistAppDirect(pendingTempAllowlist.targetUid, pendingTempAllowlist.duration, pendingTempAllowlist.type, true, pendingTempAllowlist.reasonCode, pendingTempAllowlist.tag, pendingTempAllowlist.callingUid);
            }
        }
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    for (i = 0; i < size; i++) {
                        PendingTempAllowlist pendingTempAllowlist2 = pendingTempAllowlistArr[i];
                        int indexOfKey = this.mPendingTempAllowlist.indexOfKey(pendingTempAllowlist2.targetUid);
                        if (indexOfKey >= 0 && this.mPendingTempAllowlist.valueAt(indexOfKey) == pendingTempAllowlist2) {
                            this.mPendingTempAllowlist.removeAt(indexOfKey);
                        }
                    }
                }
                resetPriorityAfterProcLockedSection();
            } finally {
            }
        }
        resetPriorityAfterLockedSection();
    }

    @GuardedBy({"this", "mProcLock"})
    public final void setAppIdTempAllowlistStateLSP(int i, boolean z) {
        this.mOomAdjuster.setAppIdTempAllowlistStateLSP(i, z);
    }

    @GuardedBy({"this", "mProcLock"})
    public final void setUidTempAllowlistStateLSP(int i, boolean z) {
        this.mOomAdjuster.setUidTempAllowlistStateLSP(i, z);
    }

    public final void trimApplications(boolean z, int i) {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                trimApplicationsLocked(z, i);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @GuardedBy({"this"})
    public final void trimApplicationsLocked(boolean z, int i) {
        boolean z2 = false;
        for (int size = this.mProcessList.mRemovedProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = this.mProcessList.mRemovedProcesses.get(size);
            if (!processRecord.hasActivitiesOrRecentTasks() && processRecord.mReceivers.numberOfCurReceivers() == 0 && processRecord.mServices.numberOfRunningServices() == 0) {
                IApplicationThread thread = processRecord.getThread();
                StringBuilder sb = new StringBuilder();
                sb.append("Exiting empty application process ");
                sb.append(processRecord.toShortString());
                sb.append(" (");
                sb.append(thread != null ? thread.asBinder() : null);
                sb.append(")\n");
                Slog.i("ActivityManager", sb.toString());
                int pid = processRecord.getPid();
                if (pid > 0 && pid != MY_PID) {
                    processRecord.killLocked("empty", 13, 4, false);
                } else if (thread != null) {
                    try {
                        thread.scheduleExit();
                    } catch (Exception unused) {
                    }
                }
                cleanUpApplicationRecordLocked(processRecord, pid, false, true, -1, false, false);
                this.mProcessList.mRemovedProcesses.remove(size);
                if (processRecord.isPersistent()) {
                    addAppLocked(processRecord.info, null, false, null, 2);
                    processRecord.mProfile.addHostingComponentType(2);
                }
                z2 = true;
            }
        }
        if (z2 || z) {
            updateOomAdjLocked(i);
        } else {
            updateOomAdjPendingTargetsLocked(i);
        }
    }

    public void signalPersistentProcesses(final int i) throws RemoteException {
        if (i != 10) {
            throw new SecurityException("Only SIGNAL_USR1 is allowed");
        }
        if (checkCallingPermission("android.permission.SIGNAL_PERSISTENT_PROCESSES") != 0) {
            throw new SecurityException("Requires permission android.permission.SIGNAL_PERSISTENT_PROCESSES");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.lambda$signalPersistentProcesses$27(i, (ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public static /* synthetic */ void lambda$signalPersistentProcesses$27(int i, ProcessRecord processRecord) {
        if (processRecord.getThread() == null || !processRecord.isPersistent()) {
            return;
        }
        Process.sendSignal(processRecord.getPid(), i);
    }

    public boolean profileControl(String str, int i, boolean z, ProfilerInfo profilerInfo, int i2) throws RemoteException {
        ProcessRecord findProcessLOSP;
        boolean profileControlLPf;
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        if (z && (profilerInfo == null || profilerInfo.profileFd == null)) {
            throw new IllegalArgumentException("null profile info or fd");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                findProcessLOSP = str != null ? findProcessLOSP(str, i, "profileControl") : null;
                if (z && (findProcessLOSP == null || findProcessLOSP.getThread() == null)) {
                    throw new IllegalArgumentException("Unknown process: " + str);
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        synchronized (this.mAppProfiler.mProfilerLock) {
            profileControlLPf = this.mAppProfiler.profileControlLPf(findProcessLOSP, z, profilerInfo, i2);
        }
        return profileControlLPf;
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public final ProcessRecord findProcessLOSP(String str, int i, String str2) {
        SparseArray sparseArray;
        int handleIncomingUser = this.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, 2, str2, null);
        ProcessRecord processRecord = null;
        try {
            int parseInt = Integer.parseInt(str);
            synchronized (this.mPidsSelfLocked) {
                processRecord = this.mPidsSelfLocked.get(parseInt);
            }
        } catch (NumberFormatException unused) {
        }
        if (processRecord != null || (sparseArray = (SparseArray) this.mProcessList.getProcessNamesLOSP().getMap().get(str)) == null || sparseArray.size() <= 0) {
            return processRecord;
        }
        ProcessRecord processRecord2 = (ProcessRecord) sparseArray.valueAt(0);
        if (handleIncomingUser == -1 || processRecord2.userId == handleIncomingUser) {
            return processRecord2;
        }
        for (int i2 = 1; i2 < sparseArray.size(); i2++) {
            ProcessRecord processRecord3 = (ProcessRecord) sparseArray.valueAt(i2);
            if (processRecord3.userId == handleIncomingUser) {
                return processRecord3;
            }
        }
        return processRecord2;
    }

    public boolean dumpHeap(String str, int i, boolean z, boolean z2, boolean z3, String str2, ParcelFileDescriptor parcelFileDescriptor, final RemoteCallback remoteCallback) {
        IApplicationThread thread;
        try {
            try {
                if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
                    throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
                }
                if (parcelFileDescriptor == null) {
                    throw new IllegalArgumentException("null fd");
                }
                synchronized (this) {
                    try {
                        boostPriorityForLockedSection();
                        ProcessRecord findProcessLOSP = findProcessLOSP(str, i, "dumpHeap");
                        if (findProcessLOSP == null || (thread = findProcessLOSP.getThread()) == null) {
                            throw new IllegalArgumentException("Unknown process: " + str);
                        }
                        enforceDebuggable(findProcessLOSP);
                        this.mOomAdjuster.mCachedAppOptimizer.enableFreezer(false);
                        thread.dumpHeap(z, z2, z3, str2, parcelFileDescriptor, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.am.ActivityManagerService.19
                            public void onResult(Bundle bundle) {
                                remoteCallback.sendResult(bundle);
                                ActivityManagerService.this.mOomAdjuster.mCachedAppOptimizer.enableFreezer(true);
                            }
                        }, (Handler) null));
                        try {
                            resetPriorityAfterLockedSection();
                            return true;
                        } catch (Throwable th) {
                            th = th;
                            resetPriorityAfterLockedSection();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            } catch (RemoteException unused) {
                throw new IllegalStateException("Process disappeared");
            }
        } catch (Throwable th3) {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException unused2) {
                }
            }
            throw th3;
        }
    }

    public boolean dumpResources(String str, ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) throws RemoteException {
        IApplicationThread thread;
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                ProcessRecord findProcessLOSP = findProcessLOSP(str, -2, "dumpResources");
                if (findProcessLOSP == null || (thread = findProcessLOSP.getThread()) == null) {
                    throw new IllegalArgumentException("Unknown process: " + str);
                }
                thread.dumpResources(parcelFileDescriptor, remoteCallback);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return true;
    }

    public void dumpAllResources(ParcelFileDescriptor parcelFileDescriptor, PrintWriter printWriter) throws RemoteException {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPidsSelfLocked) {
            arrayList.addAll(this.mProcessList.getLruProcessesLOSP());
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ProcessRecord processRecord = (ProcessRecord) arrayList.get(i);
            printWriter.println(String.format("Resources History for %s (%s)", processRecord.processName, processRecord.info.packageName));
            printWriter.flush();
            try {
                TransferPipe transferPipe = new TransferPipe("  ");
                if (processRecord.getThread() != null) {
                    processRecord.getThread().dumpResources(transferPipe.getWriteFd(), (RemoteCallback) null);
                    transferPipe.go(parcelFileDescriptor.getFileDescriptor(), 2000L);
                } else {
                    printWriter.println(String.format("  Resources history for %s (%s) failed, no thread", processRecord.processName, processRecord.info.packageName));
                }
                transferPipe.kill();
            } catch (IOException e) {
                printWriter.println("  " + e.getMessage());
                printWriter.flush();
            }
        }
    }

    public void setDumpHeapDebugLimit(String str, int i, long j, String str2) {
        String str3;
        int i2;
        int i3;
        String str4;
        if (str != null) {
            enforceCallingPermission("android.permission.SET_DEBUG_APP", "setDumpHeapDebugLimit()");
            str4 = str;
            i3 = i;
        } else {
            synchronized (this.mPidsSelfLocked) {
                ProcessRecord processRecord = this.mPidsSelfLocked.get(Binder.getCallingPid());
                if (processRecord == null) {
                    throw new SecurityException("No process found for calling pid " + Binder.getCallingPid());
                }
                enforceDebuggable(processRecord);
                str3 = processRecord.processName;
                i2 = processRecord.uid;
                if (str2 != null && !processRecord.getPkgList().containsKey(str2)) {
                    throw new SecurityException("Package " + str2 + " is not running in " + processRecord);
                }
            }
            i3 = i2;
            str4 = str3;
        }
        this.mAppProfiler.setDumpHeapDebugLimit(str4, i3, j, str2);
    }

    public void dumpHeapFinished(String str) {
        this.mAppProfiler.dumpHeapFinished(str, Binder.getCallingPid());
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void onCoreSettingsChange(Bundle bundle) {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mProcessList.updateCoreSettingsLOSP(bundle);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public boolean startUserInBackground(int i) {
        return startUserInBackgroundWithListener(i, null);
    }

    public boolean startUserInBackgroundWithListener(int i, IProgressListener iProgressListener) {
        return this.mUserController.startUser(i, 2, iProgressListener);
    }

    public boolean startUserInForegroundWithListener(int i, IProgressListener iProgressListener) {
        return this.mUserController.startUser(i, 1, iProgressListener);
    }

    public boolean startUserInBackgroundVisibleOnDisplay(int i, int i2, IProgressListener iProgressListener) {
        int[] displayIdsForStartingVisibleBackgroundUsers = getDisplayIdsForStartingVisibleBackgroundUsers();
        boolean z = false;
        if (displayIdsForStartingVisibleBackgroundUsers != null) {
            int i3 = 0;
            while (true) {
                if (i3 >= displayIdsForStartingVisibleBackgroundUsers.length) {
                    break;
                } else if (i2 == displayIdsForStartingVisibleBackgroundUsers[i3]) {
                    z = true;
                    break;
                } else {
                    i3++;
                }
            }
        }
        if (!z) {
            throw new IllegalArgumentException("Invalid display (" + i2 + ") to start user. Valid options are: " + Arrays.toString(displayIdsForStartingVisibleBackgroundUsers));
        }
        return this.mInjector.startUserInBackgroundVisibleOnDisplay(i, i2, iProgressListener);
    }

    public int[] getDisplayIdsForStartingVisibleBackgroundUsers() {
        enforceCallingHasAtLeastOnePermission("getDisplayIdsForStartingVisibleBackgroundUsers()", "android.permission.MANAGE_USERS", "android.permission.INTERACT_ACROSS_USERS");
        return this.mInjector.getDisplayIdsForStartingVisibleBackgroundUsers();
    }

    @Deprecated
    public boolean unlockUser(int i, byte[] bArr, byte[] bArr2, IProgressListener iProgressListener) {
        return this.mUserController.unlockUser(i, iProgressListener);
    }

    public boolean unlockUser2(int i, IProgressListener iProgressListener) {
        return this.mUserController.unlockUser(i, iProgressListener);
    }

    public boolean switchUser(int i) {
        return this.mUserController.switchUser(i);
    }

    public String getSwitchingFromUserMessage() {
        return this.mUserController.getSwitchingFromSystemUserMessage();
    }

    public String getSwitchingToUserMessage() {
        return this.mUserController.getSwitchingToSystemUserMessage();
    }

    public void setStopUserOnSwitch(@ActivityManager.StopUserOnSwitch int i) {
        this.mUserController.setStopUserOnSwitch(i);
    }

    public int stopUser(int i, boolean z, IStopUserCallback iStopUserCallback) {
        return this.mUserController.stopUser(i, z, false, iStopUserCallback, null);
    }

    public int stopUserWithDelayedLocking(int i, boolean z, IStopUserCallback iStopUserCallback) {
        return this.mUserController.stopUser(i, z, true, iStopUserCallback, null);
    }

    public boolean startProfile(int i) {
        return this.mUserController.startProfile(i, false, null);
    }

    public boolean startProfileWithListener(int i, IProgressListener iProgressListener) {
        return this.mUserController.startProfile(i, false, iProgressListener);
    }

    public boolean stopProfile(int i) {
        return this.mUserController.stopProfile(i);
    }

    public UserInfo getCurrentUser() {
        return this.mUserController.getCurrentUser();
    }

    public int getCurrentUserId() {
        return this.mUserController.getCurrentUserIdChecked();
    }

    public String getStartedUserState(int i) {
        return UserState.stateToString(this.mUserController.getStartedUserState(i).state);
    }

    public boolean isUserRunning(int i, int i2) {
        if (!this.mUserController.isSameProfileGroup(i, UserHandle.getCallingUserId()) && checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") != 0) {
            String str = "Permission Denial: isUserRunning() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.INTERACT_ACROSS_USERS";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        return this.mUserController.isUserRunning(i, i2);
    }

    public int[] getRunningUserIds() {
        if (checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") != 0) {
            String str = "Permission Denial: isUserRunning() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.INTERACT_ACROSS_USERS";
            Slog.w("ActivityManager", str);
            throw new SecurityException(str);
        }
        return this.mUserController.getStartedUserArray();
    }

    public void registerUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver, String str) {
        this.mUserController.registerUserSwitchObserver(iUserSwitchObserver, str);
    }

    public void unregisterUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) {
        this.mUserController.unregisterUserSwitchObserver(iUserSwitchObserver);
    }

    public ApplicationInfo getAppInfoForUser(ApplicationInfo applicationInfo, int i) {
        if (applicationInfo == null) {
            return null;
        }
        ApplicationInfo applicationInfo2 = new ApplicationInfo(applicationInfo);
        applicationInfo2.initForUser(i);
        return applicationInfo2;
    }

    public boolean isUserStopped(int i) {
        return this.mUserController.getStartedUserState(i) == null;
    }

    public ActivityInfo getActivityInfoForUser(ActivityInfo activityInfo, int i) {
        if (activityInfo == null || (i < 1 && activityInfo.applicationInfo.uid < 100000)) {
            return activityInfo;
        }
        ActivityInfo activityInfo2 = new ActivityInfo(activityInfo);
        activityInfo2.applicationInfo = getAppInfoForUser(activityInfo2.applicationInfo, i);
        return activityInfo2;
    }

    @GuardedBy({"mProcLock"})
    public final boolean processSanityChecksLPr(ProcessRecord processRecord, IApplicationThread iApplicationThread) {
        if (processRecord == null || iApplicationThread == null) {
            return false;
        }
        return Build.IS_DEBUGGABLE || processRecord.isDebuggable();
    }

    public boolean startBinderTracking() throws RemoteException {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mBinderTransactionTrackingEnabled = true;
                this.mProcessList.forEachLruProcessesLOSP(true, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityManagerService.this.lambda$startBinderTracking$28((ProcessRecord) obj);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startBinderTracking$28(ProcessRecord processRecord) {
        IApplicationThread thread = processRecord.getThread();
        if (processSanityChecksLPr(processRecord, thread)) {
            try {
                thread.startBinderTracking();
            } catch (RemoteException unused) {
                Log.v("ActivityManager", "Process disappared");
            }
        }
    }

    public boolean stopBinderTrackingAndDump(final ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
        if (checkCallingPermission("android.permission.SET_ACTIVITY_WATCHER") != 0) {
            throw new SecurityException("Requires permission android.permission.SET_ACTIVITY_WATCHER");
        }
        boolean z = true;
        try {
            synchronized (this.mProcLock) {
                try {
                    boostPriorityForProcLockedSection();
                    if (parcelFileDescriptor == null) {
                        throw new IllegalArgumentException("null fd");
                    }
                    this.mBinderTransactionTrackingEnabled = false;
                    final FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
                    fastPrintWriter.println("Binder transaction traces for all processes.\n");
                    this.mProcessList.forEachLruProcessesLOSP(true, new Consumer() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda8
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityManagerService.this.lambda$stopBinderTrackingAndDump$29(fastPrintWriter, parcelFileDescriptor, (ProcessRecord) obj);
                        }
                    });
                    try {
                        resetPriorityAfterProcLockedSection();
                        return true;
                    } catch (Throwable th) {
                        th = th;
                        z = false;
                        resetPriorityAfterProcLockedSection();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        } catch (Throwable th3) {
            if (parcelFileDescriptor != null && z) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException unused) {
                }
            }
            throw th3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopBinderTrackingAndDump$29(PrintWriter printWriter, ParcelFileDescriptor parcelFileDescriptor, ProcessRecord processRecord) {
        IApplicationThread thread = processRecord.getThread();
        if (processSanityChecksLPr(processRecord, thread)) {
            printWriter.println("Traces for process: " + processRecord.processName);
            printWriter.flush();
            try {
                TransferPipe transferPipe = new TransferPipe();
                thread.stopBinderTrackingAndDump(transferPipe.getWriteFd());
                transferPipe.go(parcelFileDescriptor.getFileDescriptor());
                transferPipe.kill();
            } catch (RemoteException e) {
                printWriter.println("Got a RemoteException while dumping IPC traces from " + processRecord + ".  Exception: " + e);
                printWriter.flush();
            } catch (IOException e2) {
                printWriter.println("Failure while dumping IPC traces from " + processRecord + ".  Exception: " + e2);
                printWriter.flush();
            }
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.ActivityManagerService$LocalService */
    /* loaded from: classes.dex */
    public final class LocalService extends ActivityManagerInternal implements ActivityManagerLocal {
        public final boolean isSplitConfigurationChange(int i) {
            return (i & 4100) != 0;
        }

        public LocalService() {
        }

        public List<PendingIntentStats> getPendingIntentStats() {
            return ActivityManagerService.this.mPendingIntentController.dumpPendingIntentStatsForStatsd();
        }

        public Pair<String, String> getAppProfileStatsForDebugging(long j, int i) {
            return ActivityManagerService.this.mAppProfiler.getAppProfileStatsForDebugging(j, i);
        }

        public String checkContentProviderAccess(String str, int i) {
            return ActivityManagerService.this.mCpHelper.checkContentProviderAccess(str, i);
        }

        public int checkContentProviderUriPermission(Uri uri, int i, int i2, int i3) {
            return ActivityManagerService.this.mCpHelper.checkContentProviderUriPermission(uri, i, i2, i3);
        }

        public void onWakefulnessChanged(int i) {
            ActivityManagerService.this.onWakefulnessChanged(i);
        }

        public boolean startIsolatedProcess(String str, String[] strArr, String str2, String str3, int i, Runnable runnable) {
            return ActivityManagerService.this.startIsolatedProcess(str, strArr, str2, str3, i, runnable);
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public ComponentName startSdkSandboxService(Intent intent, int i, String str, String str2) throws RemoteException {
            validateSdkSandboxParams(intent, i, str, str2);
            ActivityManagerService activityManagerService = ActivityManagerService.this;
            ComponentName startService = activityManagerService.startService(activityManagerService.mContext.getIApplicationThread(), intent, intent.resolveTypeIfNeeded(ActivityManagerService.this.mContext.getContentResolver()), false, ActivityManagerService.this.mContext.getOpPackageName(), ActivityManagerService.this.mContext.getAttributionTag(), UserHandle.getUserId(i), true, i, str, str2);
            if (startService != null) {
                if (startService.getPackageName().equals("!")) {
                    throw new SecurityException("Not allowed to start service " + intent + " without permission " + startService.getClassName());
                } else if (startService.getPackageName().equals("!!")) {
                    throw new SecurityException("Unable to start service " + intent + ": " + startService.getClassName());
                } else if (startService.getPackageName().equals("?")) {
                    throw ServiceStartNotAllowedException.newInstance(false, "Not allowed to start service " + intent + ": " + startService.getClassName());
                }
            }
            return startService;
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean stopSdkSandboxService(Intent intent, int i, String str, String str2) {
            validateSdkSandboxParams(intent, i, str, str2);
            ActivityManagerService activityManagerService = ActivityManagerService.this;
            int stopService = activityManagerService.stopService(activityManagerService.mContext.getIApplicationThread(), intent, intent.resolveTypeIfNeeded(ActivityManagerService.this.mContext.getContentResolver()), UserHandle.getUserId(i), true, i, str, str2);
            if (stopService >= 0) {
                return stopService != 0;
            }
            throw new SecurityException("Not allowed to stop service " + intent);
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, IBinder iBinder, String str, String str2, int i2) throws RemoteException {
            return bindSdkSandboxServiceInternal(intent, serviceConnection, i, iBinder, str, str2, Integer.toUnsignedLong(i2));
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, IBinder iBinder, String str, String str2, Context.BindServiceFlags bindServiceFlags) throws RemoteException {
            return bindSdkSandboxServiceInternal(intent, serviceConnection, i, iBinder, str, str2, bindServiceFlags.getValue());
        }

        public final boolean bindSdkSandboxServiceInternal(Intent intent, ServiceConnection serviceConnection, int i, IBinder iBinder, String str, String str2, long j) throws RemoteException {
            IApplicationThread iApplicationThread;
            validateSdkSandboxParams(intent, i, str, str2);
            if (serviceConnection == null) {
                throw new IllegalArgumentException("connection is null");
            }
            Handler mainThreadHandler = ActivityManagerService.this.mContext.getMainThreadHandler();
            if (iBinder != null) {
                synchronized (this) {
                    ProcessRecord recordForAppLOSP = ActivityManagerService.this.getRecordForAppLOSP(iBinder);
                    if (recordForAppLOSP == null) {
                        Slog.i("ActivityManager", "clientApplicationThread process not found.");
                        return false;
                    } else if (recordForAppLOSP.info.uid != i) {
                        throw new IllegalArgumentException("clientApplicationThread does not match  client uid");
                    } else {
                        iApplicationThread = recordForAppLOSP.getThread();
                    }
                }
            } else {
                iApplicationThread = null;
            }
            IApplicationThread iApplicationThread2 = iApplicationThread;
            IServiceConnection serviceDispatcher = ActivityManagerService.this.mContext.getServiceDispatcher(serviceConnection, mainThreadHandler, j);
            intent.prepareToLeaveProcess(ActivityManagerService.this.mContext);
            ActivityManagerService activityManagerService = ActivityManagerService.this;
            return activityManagerService.bindServiceInstance(activityManagerService.mContext.getIApplicationThread(), ActivityManagerService.this.mContext.getActivityToken(), intent, intent.resolveTypeIfNeeded(ActivityManagerService.this.mContext.getContentResolver()), serviceDispatcher, j, str2, true, i, str, iApplicationThread2, ActivityManagerService.this.mContext.getOpPackageName(), UserHandle.getUserId(i)) != 0;
        }

        public final void validateSdkSandboxParams(Intent intent, int i, String str, String str2) {
            if (intent == null) {
                throw new IllegalArgumentException("intent is null");
            }
            if (str == null) {
                throw new IllegalArgumentException("clientAppPackage is null");
            }
            if (str2 == null) {
                throw new IllegalArgumentException("processName is null");
            }
            if (intent.getComponent() == null) {
                throw new IllegalArgumentException("service must specify explicit component");
            }
            if (!UserHandle.isApp(i)) {
                throw new IllegalArgumentException("uid is not within application range");
            }
            if (ActivityManagerService.this.mAppOpsService.checkPackage(i, str) != 0) {
                throw new IllegalArgumentException("uid does not belong to provided package");
            }
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, String str, String str2, int i2) throws RemoteException {
            return bindSdkSandboxService(intent, serviceConnection, i, (IBinder) null, str, str2, i2);
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public void killSdkSandboxClientAppProcess(IBinder iBinder) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ProcessRecord recordForAppLOSP = ActivityManagerService.this.getRecordForAppLOSP(iBinder);
                    if (recordForAppLOSP != null) {
                        recordForAppLOSP.killLocked("sdk sandbox died", 12, 27, true);
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void onUserRemoved(int i) {
            ActivityManagerService.this.mAtmInternal.onUserStopped(i);
            ActivityManagerService.this.mBatteryStatsService.onUserRemoved(i);
        }

        public void killForegroundAppsForUser(int i) {
            int i2;
            ArrayList arrayList = new ArrayList();
            synchronized (ActivityManagerService.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    int size = ActivityManagerService.this.mProcessList.getProcessNamesLOSP().getMap().size();
                    for (int i3 = 0; i3 < size; i3++) {
                        SparseArray sparseArray = (SparseArray) ActivityManagerService.this.mProcessList.getProcessNamesLOSP().getMap().valueAt(i3);
                        int size2 = sparseArray.size();
                        for (int i4 = 0; i4 < size2; i4++) {
                            ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i4);
                            if (!processRecord.isPersistent() && (processRecord.isRemoved() || (processRecord.userId == i && processRecord.mState.hasForegroundActivities()))) {
                                arrayList.add(processRecord);
                            }
                        }
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
            int size3 = arrayList.size();
            if (size3 > 0) {
                synchronized (ActivityManagerService.this) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        for (i2 = 0; i2 < size3; i2++) {
                            ActivityManagerService.this.mProcessList.removeProcessLocked((ProcessRecord) arrayList.get(i2), false, true, 13, 9, "kill all fg");
                        }
                    } catch (Throwable th2) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th2;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }

        public void setPendingIntentAllowlistDuration(IIntentSender iIntentSender, IBinder iBinder, long j, int i, int i2, String str) {
            ActivityManagerService.this.mPendingIntentController.setPendingIntentAllowlistDuration(iIntentSender, iBinder, j, i, i2, str);
        }

        public int getPendingIntentFlags(IIntentSender iIntentSender) {
            return ActivityManagerService.this.mPendingIntentController.getPendingIntentFlags(iIntentSender);
        }

        public int[] getStartedUserIds() {
            return ActivityManagerService.this.mUserController.getStartedUserArray();
        }

        public void setPendingIntentAllowBgActivityStarts(IIntentSender iIntentSender, IBinder iBinder, int i) {
            if (!(iIntentSender instanceof PendingIntentRecord)) {
                Slog.w("ActivityManager", "setPendingIntentAllowBgActivityStarts(): not a PendingIntentRecord: " + iIntentSender);
                return;
            }
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ((PendingIntentRecord) iIntentSender).setAllowBgActivityStarts(iBinder, i);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void clearPendingIntentAllowBgActivityStarts(IIntentSender iIntentSender, IBinder iBinder) {
            if (!(iIntentSender instanceof PendingIntentRecord)) {
                Slog.w("ActivityManager", "clearPendingIntentAllowBgActivityStarts(): not a PendingIntentRecord: " + iIntentSender);
                return;
            }
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ((PendingIntentRecord) iIntentSender).clearAllowBgActivityStarts(iBinder);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void setDeviceIdleAllowlist(int[] iArr, int[] iArr2) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (ActivityManagerService.this.mProcLock) {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        ActivityManagerService activityManagerService = ActivityManagerService.this;
                        activityManagerService.mDeviceIdleAllowlist = iArr;
                        activityManagerService.mDeviceIdleExceptIdleAllowlist = iArr2;
                        activityManagerService.mAppRestrictionController.setDeviceIdleAllowlist(iArr, iArr2);
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void updateDeviceIdleTempAllowlist(int[] iArr, int i, boolean z, long j, int i2, int i3, String str, int i4) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (ActivityManagerService.this.mProcLock) {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        if (iArr != null) {
                            ActivityManagerService.this.mDeviceIdleTempAllowlist = iArr;
                        }
                        if (!z) {
                            ActivityManagerService.this.mFgsStartTempAllowList.removeUid(i);
                        } else if (i2 == 0) {
                            ActivityManagerService.this.mFgsStartTempAllowList.add(i, j, new FgsTempAllowListItem(j, i3, str, i4));
                        }
                        ActivityManagerService.this.setAppIdTempAllowlistStateLSP(i, z);
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public int getUidProcessState(int i) {
            return ActivityManagerService.this.getUidState(i);
        }

        public Map<Integer, String> getProcessesWithPendingBindMounts(int i) {
            return ActivityManagerService.this.mProcessList.getProcessesWithPendingBindMounts(i);
        }

        public boolean isSystemReady() {
            return ActivityManagerService.this.mSystemReady;
        }

        public boolean isModernQueueEnabled() {
            return ActivityManagerService.this.mEnableModernQueue;
        }

        public void enforceBroadcastOptionsPermissions(Bundle bundle, int i) {
            ActivityManagerService.this.enforceBroadcastOptionPermissionsInternal(bundle, i);
        }

        public String getPackageNameByPid(int i) {
            synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                ProcessRecord processRecord = ActivityManagerService.this.mPidsSelfLocked.get(i);
                if (processRecord == null || processRecord.info == null) {
                    return null;
                }
                return processRecord.info.packageName;
            }
        }

        public void setHasOverlayUi(int i, boolean z) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                        ProcessRecord processRecord = ActivityManagerService.this.mPidsSelfLocked.get(i);
                        if (processRecord == null) {
                            Slog.w("ActivityManager", "setHasOverlayUi called on unknown pid: " + i);
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        } else if (processRecord.mState.hasOverlayUi() == z) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        } else {
                            processRecord.mState.setHasOverlayUi(z);
                            ActivityManagerService.this.updateOomAdjLocked(processRecord, 9);
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        public void notifyNetworkPolicyRulesUpdated(int i, long j) {
            synchronized (ActivityManagerService.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    UidRecord uidRecordLOSP = ActivityManagerService.this.mProcessList.getUidRecordLOSP(i);
                    if (uidRecordLOSP == null) {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        return;
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    synchronized (uidRecordLOSP.networkStateLock) {
                        if (uidRecordLOSP.lastNetworkUpdatedProcStateSeq >= j) {
                            return;
                        }
                        uidRecordLOSP.lastNetworkUpdatedProcStateSeq = j;
                        if (uidRecordLOSP.procStateSeqWaitingForNetwork != 0 && j >= uidRecordLOSP.procStateSeqWaitingForNetwork) {
                            uidRecordLOSP.networkStateLock.notifyAll();
                        }
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
        }

        public void onUidBlockedReasonsChanged(int i, int i2) {
            synchronized (ActivityManagerService.this.mUidNetworkBlockedReasons) {
                if (i2 == 0) {
                    ActivityManagerService.this.mUidNetworkBlockedReasons.delete(i);
                } else {
                    ActivityManagerService.this.mUidNetworkBlockedReasons.put(i, i2);
                }
            }
        }

        public boolean isRuntimeRestarted() {
            return ActivityManagerService.this.mSystemServiceManager.isRuntimeRestarted();
        }

        public boolean canStartMoreUsers() {
            return ActivityManagerService.this.mUserController.canStartMoreUsers();
        }

        public void setSwitchingFromSystemUserMessage(String str) {
            ActivityManagerService.this.mUserController.setSwitchingFromSystemUserMessage(str);
        }

        public void setSwitchingToSystemUserMessage(String str) {
            ActivityManagerService.this.mUserController.setSwitchingToSystemUserMessage(str);
        }

        public int getMaxRunningUsers() {
            return ActivityManagerService.this.mUserController.getMaxRunningUsers();
        }

        public boolean isUidActive(int i) {
            boolean isUidActiveLOSP;
            synchronized (ActivityManagerService.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    isUidActiveLOSP = ActivityManagerService.this.isUidActiveLOSP(i);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
            return isUidActiveLOSP;
        }

        public List<ProcessMemoryState> getMemoryStateForProcesses() {
            ArrayList arrayList = new ArrayList();
            synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                int size = ActivityManagerService.this.mPidsSelfLocked.size();
                for (int i = 0; i < size; i++) {
                    ProcessRecord valueAt = ActivityManagerService.this.mPidsSelfLocked.valueAt(i);
                    arrayList.add(new ProcessMemoryState(valueAt.uid, valueAt.getPid(), valueAt.processName, valueAt.mState.getCurAdj(), valueAt.mServices.hasForegroundServices()));
                }
            }
            return arrayList;
        }

        public int handleIncomingUser(int i, int i2, int i3, boolean z, int i4, String str, String str2) {
            return ActivityManagerService.this.mUserController.handleIncomingUser(i, i2, i3, z, i4, str, str2);
        }

        public void enforceCallingPermission(String str, String str2) {
            ActivityManagerService.this.enforceCallingPermission(str, str2);
        }

        public Pair<Integer, Integer> getCurrentAndTargetUserIds() {
            return ActivityManagerService.this.mUserController.getCurrentAndTargetUserIds();
        }

        public int getCurrentUserId() {
            return ActivityManagerService.this.mUserController.getCurrentUserId();
        }

        public boolean isUserRunning(int i, int i2) {
            return ActivityManagerService.this.mUserController.isUserRunning(i, i2);
        }

        public void trimApplications() {
            ActivityManagerService.this.trimApplications(true, 1);
        }

        public void killProcessesForRemovedTask(ArrayList<Object> arrayList) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    for (int i = 0; i < arrayList.size(); i++) {
                        ProcessRecord processRecord = (ProcessRecord) ((WindowProcessController) arrayList.get(i)).mOwner;
                        if (ActivityManager.isProcStateBackground(processRecord.mState.getSetProcState()) && processRecord.mReceivers.numberOfCurReceivers() == 0) {
                            processRecord.killLocked("remove task", 10, 22, true);
                        } else {
                            processRecord.setWaitingToKill("remove task");
                        }
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void killProcess(String str, int i, String str2) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ProcessRecord processRecordLocked = ActivityManagerService.this.getProcessRecordLocked(str, i);
                    if (processRecordLocked != null) {
                        ActivityManagerService.this.mProcessList.removeProcessLocked(processRecordLocked, false, true, 13, str2);
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public boolean hasRunningActivity(final int i, final String str) {
            boolean z;
            if (str == null) {
                return false;
            }
            synchronized (ActivityManagerService.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    z = ActivityManagerService.this.mProcessList.searchEachLruProcessesLOSP(true, new Function() { // from class: com.android.server.am.ActivityManagerService$LocalService$$ExternalSyntheticLambda0
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            Boolean lambda$hasRunningActivity$0;
                            lambda$hasRunningActivity$0 = ActivityManagerService.LocalService.lambda$hasRunningActivity$0(i, str, (ProcessRecord) obj);
                            return lambda$hasRunningActivity$0;
                        }
                    }) != null;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
            return z;
        }

        public static /* synthetic */ Boolean lambda$hasRunningActivity$0(int i, String str, ProcessRecord processRecord) {
            if (processRecord.uid == i && processRecord.getWindowProcessController().hasRunningActivity(str)) {
                return Boolean.TRUE;
            }
            return null;
        }

        public void updateOomAdj() {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.updateOomAdjLocked(0);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void updateCpuStats() {
            ActivityManagerService.this.updateCpuStats();
        }

        public void updateBatteryStats(ComponentName componentName, int i, int i2, boolean z) {
            ActivityManagerService.this.updateBatteryStats(componentName, i, i2, z);
        }

        public void updateActivityUsageStats(ComponentName componentName, int i, int i2, IBinder iBinder, ComponentName componentName2, ActivityId activityId) {
            ActivityManagerService.this.updateActivityUsageStats(componentName, i, i2, iBinder, componentName2, activityId);
        }

        public void updateForegroundTimeIfOnBattery(String str, int i, long j) {
            ActivityManagerService.this.mBatteryStatsService.updateForegroundTimeIfOnBattery(str, i, j);
        }

        public void sendForegroundProfileChanged(int i) {
            ActivityManagerService.this.mUserController.sendForegroundProfileChanged(i);
        }

        public boolean shouldConfirmCredentials(int i) {
            return ActivityManagerService.this.mUserController.shouldConfirmCredentials(i);
        }

        public void noteAlarmFinish(PendingIntent pendingIntent, WorkSource workSource, int i, String str) {
            ActivityManagerService.this.noteAlarmFinish(pendingIntent != null ? pendingIntent.getTarget() : null, workSource, i, str);
        }

        public void noteAlarmStart(PendingIntent pendingIntent, WorkSource workSource, int i, String str) {
            ActivityManagerService.this.noteAlarmStart(pendingIntent != null ? pendingIntent.getTarget() : null, workSource, i, str);
        }

        public void noteWakeupAlarm(PendingIntent pendingIntent, WorkSource workSource, int i, String str, String str2) {
            ActivityManagerService.this.noteWakeupAlarm(pendingIntent != null ? pendingIntent.getTarget() : null, workSource, i, str, str2);
        }

        public boolean isAppStartModeDisabled(int i, String str) {
            return ActivityManagerService.this.isAppStartModeDisabled(i, str);
        }

        public int[] getCurrentProfileIds() {
            return ActivityManagerService.this.mUserController.getCurrentProfileIds();
        }

        public UserInfo getCurrentUser() {
            return ActivityManagerService.this.mUserController.getCurrentUser();
        }

        public void ensureNotSpecialUser(int i) {
            ActivityManagerService.this.mUserController.ensureNotSpecialUser(i);
        }

        public boolean isCurrentProfile(int i) {
            return ActivityManagerService.this.mUserController.isCurrentProfile(i);
        }

        public boolean hasStartedUserState(int i) {
            return ActivityManagerService.this.mUserController.hasStartedUserState(i);
        }

        public void finishUserSwitch(Object obj) {
            ActivityManagerService.this.mUserController.finishUserSwitch((UserState) obj);
        }

        public void scheduleAppGcs() {
            synchronized (ActivityManagerService.this.mAppProfiler.mProfilerLock) {
                ActivityManagerService.this.mAppProfiler.scheduleAppGcsLPf();
            }
        }

        public int getTaskIdForActivity(IBinder iBinder, boolean z) {
            return ActivityManagerService.this.getTaskForActivity(iBinder, z);
        }

        public ActivityPresentationInfo getActivityPresentationInfo(IBinder iBinder) {
            ActivityClient activityClient = ActivityClient.getInstance();
            return new ActivityPresentationInfo(activityClient.getTaskForActivity(iBinder, false), activityClient.getDisplayId(iBinder), ActivityManagerService.this.mAtmInternal.getActivityName(iBinder));
        }

        public void setBooting(boolean z) {
            ActivityManagerService.this.mBooting = z;
        }

        public boolean isBooting() {
            return ActivityManagerService.this.mBooting;
        }

        public void setBooted(boolean z) {
            ActivityManagerService.this.mBooted = z;
        }

        public boolean isBooted() {
            return ActivityManagerService.this.mBooted;
        }

        public void finishBooting() {
            ActivityManagerService.this.finishBooting();
        }

        public void tempAllowlistForPendingIntent(int i, int i2, int i3, long j, int i4, int i5, String str) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.tempAllowlistForPendingIntentLocked(i, i2, i3, j, i4, i5, str);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public int broadcastIntentInPackage(String str, String str2, int i, int i2, int i3, Intent intent, String str3, IApplicationThread iApplicationThread, IIntentReceiver iIntentReceiver, int i4, String str4, Bundle bundle, String str5, Bundle bundle2, boolean z, boolean z2, int i5, BackgroundStartPrivileges backgroundStartPrivileges, int[] iArr) {
            int broadcastIntentInPackage;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    broadcastIntentInPackage = ActivityManagerService.this.broadcastIntentInPackage(str, str2, i, i2, i3, intent, str3, ActivityManagerService.this.getRecordForAppLOSP(iApplicationThread), iIntentReceiver, i4, str4, bundle, str5, bundle2, z, z2, i5, backgroundStartPrivileges, iArr);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return broadcastIntentInPackage;
        }

        public int broadcastIntent(Intent intent, IIntentReceiver iIntentReceiver, String[] strArr, boolean z, int i, int[] iArr, BiFunction<Integer, Bundle, Bundle> biFunction, Bundle bundle) {
            int broadcastIntentLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    Intent verifyBroadcastLocked = ActivityManagerService.this.verifyBroadcastLocked(intent);
                    int callingPid = Binder.getCallingPid();
                    int callingUid = Binder.getCallingUid();
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    broadcastIntentLocked = ActivityManagerService.this.broadcastIntentLocked(null, null, null, verifyBroadcastLocked, null, null, iIntentReceiver, 0, null, null, strArr, null, null, -1, bundle, z, false, callingPid, callingUid, callingUid, callingPid, i, BackgroundStartPrivileges.NONE, iArr, biFunction);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return broadcastIntentLocked;
        }

        public int broadcastIntentWithCallback(Intent intent, IIntentReceiver iIntentReceiver, String[] strArr, int i, int[] iArr, BiFunction<Integer, Bundle, Bundle> biFunction, Bundle bundle) {
            return broadcastIntent(intent, iIntentReceiver, strArr, !isModernQueueEnabled(), i, iArr, biFunction, bundle);
        }

        public ComponentName startServiceInPackage(int i, Intent intent, String str, boolean z, String str2, String str3, int i2, BackgroundStartPrivileges backgroundStartPrivileges) throws TransactionTooLargeException {
            ComponentName startServiceLocked;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (Trace.isTagEnabled(64L)) {
                    Trace.traceBegin(64L, "startServiceInPackage: intent=" + intent + ", caller=" + str2 + ", fgRequired=" + z);
                }
                synchronized (ActivityManagerService.this) {
                    ActivityManagerService.boostPriorityForLockedSection();
                    startServiceLocked = ActivityManagerService.this.mServices.startServiceLocked(null, intent, str, -1, i, z, str2, str3, i2, backgroundStartPrivileges);
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                return startServiceLocked;
            } finally {
                Trace.traceEnd(64L);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void disconnectActivityFromServices(Object obj) {
            final ActivityServiceConnectionsHolder activityServiceConnectionsHolder = (ActivityServiceConnectionsHolder) obj;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (ActivityManagerService.this.mProcLock) {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        activityServiceConnectionsHolder.forEachConnection(new Consumer() { // from class: com.android.server.am.ActivityManagerService$LocalService$$ExternalSyntheticLambda1
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj2) {
                                ActivityManagerService.LocalService.this.lambda$disconnectActivityFromServices$1(activityServiceConnectionsHolder, obj2);
                            }
                        });
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$disconnectActivityFromServices$1(ActivityServiceConnectionsHolder activityServiceConnectionsHolder, Object obj) {
            ActivityManagerService.this.mServices.removeConnectionLocked((ConnectionRecord) obj, null, activityServiceConnectionsHolder, false);
        }

        public void cleanUpServices(int i, ComponentName componentName, Intent intent) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mServices.cleanUpServices(i, componentName, intent);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public ActivityInfo getActivityInfoForUser(ActivityInfo activityInfo, int i) {
            return ActivityManagerService.this.getActivityInfoForUser(activityInfo, i);
        }

        public void ensureBootCompleted() {
            ActivityManagerService.this.ensureBootCompleted();
        }

        public void updateOomLevelsForDisplay(int i) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService activityManagerService = ActivityManagerService.this;
                    WindowManagerService windowManagerService = activityManagerService.mWindowManager;
                    if (windowManagerService != null) {
                        activityManagerService.mProcessList.applyDisplaySize(windowManagerService);
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isActivityStartsLoggingEnabled() {
            return ActivityManagerService.this.mConstants.mFlagActivityStartsLoggingEnabled;
        }

        public boolean isBackgroundActivityStartsEnabled() {
            return ActivityManagerService.this.mConstants.mFlagBackgroundActivityStartsEnabled;
        }

        public BackgroundStartPrivileges getBackgroundStartPrivileges(int i) {
            return ActivityManagerService.this.getBackgroundStartPrivileges(i);
        }

        public void reportCurKeyguardUsageEvent(boolean z) {
            ActivityManagerService.this.reportGlobalUsageEvent(z ? 17 : 18);
        }

        public void monitor() {
            ActivityManagerService.this.monitor();
        }

        public long inputDispatchingTimedOut(int i, boolean z, TimeoutRecord timeoutRecord) {
            return ActivityManagerService.this.inputDispatchingTimedOut(i, z, timeoutRecord);
        }

        public boolean inputDispatchingTimedOut(Object obj, String str, ApplicationInfo applicationInfo, String str2, Object obj2, boolean z, TimeoutRecord timeoutRecord) {
            return ActivityManagerService.this.inputDispatchingTimedOut((ProcessRecord) obj, str, applicationInfo, str2, (WindowProcessController) obj2, z, timeoutRecord);
        }

        public void inputDispatchingResumed(int i) {
            ProcessRecord processRecord;
            synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                processRecord = ActivityManagerService.this.mPidsSelfLocked.get(i);
            }
            if (processRecord != null) {
                ActivityManagerService.this.mAppErrors.handleDismissAnrDialogs(processRecord);
            }
        }

        public void rescheduleAnrDialog(Object obj) {
            Message obtain = Message.obtain();
            obtain.what = 2;
            obtain.obj = (AppNotRespondingDialog.Data) obj;
            ActivityManagerService.this.mUiHandler.sendMessageDelayed(obtain, InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS);
        }

        public void broadcastGlobalConfigurationChanged(int i, boolean z) {
            int i2;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    Intent intent = new Intent("android.intent.action.CONFIGURATION_CHANGED");
                    intent.addFlags(1881145344);
                    Bundle bundle = new BroadcastOptions().setDeliveryGroupPolicy(1).setDeferUntilActive(true).toBundle();
                    ActivityManagerService activityManagerService = ActivityManagerService.this;
                    int i3 = ActivityManagerService.MY_PID;
                    activityManagerService.broadcastIntentLocked(null, null, null, intent, null, null, 0, null, null, null, null, null, -1, bundle, false, false, i3, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
                    if ((i & 4) != 0) {
                        Intent intent2 = new Intent("android.intent.action.LOCALE_CHANGED");
                        intent2.addFlags(18876416);
                        if (z || !ActivityManagerService.this.mProcessesReady) {
                            intent2.addFlags(1073741824);
                        }
                        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
                        makeBasic.setTemporaryAppAllowlist(ActivityManagerService.this.mInternal.getBootTimeTempAllowListDuration(), 0, 206, "");
                        i2 = 1;
                        makeBasic.setDeliveryGroupPolicy(1);
                        makeBasic.setDeferUntilActive(true);
                        ActivityManagerService.this.broadcastIntentLocked(null, null, null, intent2, null, null, 0, null, null, null, null, null, -1, makeBasic.toBundle(), false, false, i3, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
                    } else {
                        i2 = 1;
                    }
                    if (!z && isSplitConfigurationChange(i)) {
                        Intent intent3 = new Intent("android.intent.action.SPLIT_CONFIGURATION_CHANGED");
                        intent3.addFlags(553648128);
                        String[] strArr = new String[i2];
                        strArr[0] = "android.permission.INSTALL_PACKAGES";
                        ActivityManagerService.this.broadcastIntentLocked(null, null, null, intent3, null, null, 0, null, null, strArr, null, null, -1, null, false, false, i3, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void broadcastCloseSystemDialogs(String str) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    Intent intent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
                    intent.addFlags(1342177280);
                    if (str != null) {
                        intent.putExtra("reason", str);
                    }
                    BroadcastOptions deferUntilActive = new BroadcastOptions().setDeliveryGroupPolicy(1).setDeferUntilActive(true);
                    if (str != null) {
                        deferUntilActive.setDeliveryGroupMatchingKey("android.intent.action.CLOSE_SYSTEM_DIALOGS", str);
                    }
                    ActivityManagerService.this.broadcastIntentLocked(null, null, null, intent, null, null, 0, null, null, null, null, null, -1, deferUntilActive.toBundle(), false, false, -1, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void appNotResponding(String str, int i, TimeoutRecord timeoutRecord) {
            ActivityManagerService.this.appNotResponding(str, i, timeoutRecord);
        }

        public void killAllBackgroundProcessesExcept(int i, int i2) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.killAllBackgroundProcessesExcept(i, i2);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void startProcess(String str, ApplicationInfo applicationInfo, boolean z, boolean z2, String str2, ComponentName componentName) {
            try {
                if (Trace.isTagEnabled(64L)) {
                    Trace.traceBegin(64L, "startProcess:" + str);
                }
                synchronized (ActivityManagerService.this) {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.startProcessLocked(str, applicationInfo, z, 0, new HostingRecord(str2, componentName, z2), 1, false, false);
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } finally {
                Trace.traceEnd(64L);
            }
        }

        public void setDebugFlagsForStartingActivity(ActivityInfo activityInfo, int i, ProfilerInfo profilerInfo, Object obj) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (obj) {
                        if ((i & 2) != 0) {
                            ActivityManagerService.this.setDebugApp(activityInfo.processName, true, false, (i & 16) != 0);
                        }
                        if ((i & 8) != 0) {
                            ActivityManagerService.this.setNativeDebuggingAppLocked(activityInfo.applicationInfo, activityInfo.processName);
                        }
                        if ((i & 4) != 0) {
                            ActivityManagerService.this.setTrackAllocationApp(activityInfo.applicationInfo, activityInfo.processName);
                        }
                        if (profilerInfo != null) {
                            ActivityManagerService.this.setProfileApp(activityInfo.applicationInfo, activityInfo.processName, profilerInfo, null);
                        }
                        obj.notify();
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public int getStorageMountMode(int i, int i2) {
            int mountMode;
            if (i2 == 2000 || i2 == 0) {
                return 1;
            }
            synchronized (ActivityManagerService.this.mPidsSelfLocked) {
                ProcessRecord processRecord = ActivityManagerService.this.mPidsSelfLocked.get(i);
                mountMode = processRecord == null ? 0 : processRecord.getMountMode();
            }
            return mountMode;
        }

        public boolean isAppForeground(int i) {
            return ActivityManagerService.this.isAppForeground(i);
        }

        public boolean isAppBad(String str, int i) {
            return ActivityManagerService.this.isAppBad(str, i);
        }

        public void clearPendingBackup(int i) {
            ActivityManagerService.this.clearPendingBackup(i);
        }

        public void prepareForPossibleShutdown() {
            ActivityManagerService.this.prepareForPossibleShutdown();
        }

        public boolean hasRunningForegroundService(final int i, final int i2) {
            boolean z;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ProcessList processList = ActivityManagerService.this.mProcessList;
                    Function function = new Function() { // from class: com.android.server.am.ActivityManagerService$LocalService$$ExternalSyntheticLambda2
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            Boolean lambda$hasRunningForegroundService$2;
                            lambda$hasRunningForegroundService$2 = ActivityManagerService.LocalService.lambda$hasRunningForegroundService$2(i, i2, (ProcessRecord) obj);
                            return lambda$hasRunningForegroundService$2;
                        }
                    };
                    z = true;
                    if (processList.searchEachLruProcessesLOSP(true, function) == null) {
                        z = false;
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public static /* synthetic */ Boolean lambda$hasRunningForegroundService$2(int i, int i2, ProcessRecord processRecord) {
            if (processRecord.uid == i && processRecord.mServices.containsAnyForegroundServiceTypes(i2)) {
                return Boolean.TRUE;
            }
            return null;
        }

        public boolean hasForegroundServiceNotification(String str, int i, String str2) {
            boolean hasForegroundServiceNotificationLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    hasForegroundServiceNotificationLocked = ActivityManagerService.this.mServices.hasForegroundServiceNotificationLocked(str, i, str2);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return hasForegroundServiceNotificationLocked;
        }

        public ActivityManagerInternal.ServiceNotificationPolicy applyForegroundServiceNotification(Notification notification, String str, int i, String str2, int i2) {
            ActivityManagerInternal.ServiceNotificationPolicy applyForegroundServiceNotificationLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    applyForegroundServiceNotificationLocked = ActivityManagerService.this.mServices.applyForegroundServiceNotificationLocked(notification, str, i, str2, i2);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return applyForegroundServiceNotificationLocked;
        }

        public void onForegroundServiceNotificationUpdate(boolean z, Notification notification, int i, String str, int i2) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mServices.onForegroundServiceNotificationUpdateLocked(z, notification, i, str, i2);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void stopAppForUser(String str, int i) {
            ActivityManagerService.this.stopAppForUserInternal(str, i);
        }

        public void registerProcessObserver(IProcessObserver iProcessObserver) {
            ActivityManagerService.this.registerProcessObserver(iProcessObserver);
        }

        public void unregisterProcessObserver(IProcessObserver iProcessObserver) {
            ActivityManagerService.this.unregisterProcessObserver(iProcessObserver);
        }

        public int getInstrumentationSourceUid(int i) {
            ApplicationInfo applicationInfo;
            synchronized (ActivityManagerService.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    for (int size = ActivityManagerService.this.mActiveInstrumentation.size() - 1; size >= 0; size--) {
                        ActiveInstrumentation activeInstrumentation = ActivityManagerService.this.mActiveInstrumentation.get(size);
                        if (!activeInstrumentation.mFinished && (applicationInfo = activeInstrumentation.mTargetInfo) != null && applicationInfo.uid == i) {
                            int i2 = activeInstrumentation.mSourceUid;
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            return i2;
                        }
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    return -1;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
        }

        public void setDeviceOwnerUid(int i) {
            ActivityManagerService.this.mDeviceOwnerUid = i;
        }

        public boolean isDeviceOwner(int i) {
            return i >= 0 && ActivityManagerService.this.mDeviceOwnerUid == i;
        }

        public void setProfileOwnerUid(ArraySet<Integer> arraySet) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mProfileOwnerUids = arraySet;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isProfileOwner(int i) {
            boolean z;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    z = ActivityManagerService.this.mProfileOwnerUids != null && ActivityManagerService.this.mProfileOwnerUids.indexOf(Integer.valueOf(i)) >= 0;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public void setCompanionAppUids(int i, Set<Integer> set) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mCompanionAppUidsMap.put(Integer.valueOf(i), set);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isAssociatedCompanionApp(int i, int i2) {
            Set set = (Set) ActivityManagerService.this.mCompanionAppUidsMap.get(Integer.valueOf(i));
            if (set == null) {
                return false;
            }
            return set.contains(Integer.valueOf(i2));
        }

        public void addPendingTopUid(int i, int i2, IApplicationThread iApplicationThread) {
            boolean add = ActivityManagerService.this.mPendingStartActivityUids.add(i, i2);
            if (add) {
                ActivityManagerService.this.mOomAdjuster.mCachedAppOptimizer.unfreezeProcess(i2, 1);
            }
            if (!add || ActivityManagerService.this.mNetworkPolicyUidObserver == null) {
                return;
            }
            try {
                long nextProcStateSeq = ActivityManagerService.this.mProcessList.getNextProcStateSeq();
                ActivityManagerService.this.mNetworkPolicyUidObserver.onUidStateChanged(i, 2, nextProcStateSeq, 31);
                if (iApplicationThread == null || !shouldWaitForNetworkRulesUpdate(i)) {
                    return;
                }
                iApplicationThread.setNetworkBlockSeq(nextProcStateSeq);
            } catch (RemoteException e) {
                Slog.d("ActivityManager", "Error calling setNetworkBlockSeq", e);
            }
        }

        public final boolean shouldWaitForNetworkRulesUpdate(int i) {
            boolean z;
            synchronized (ActivityManagerService.this.mUidNetworkBlockedReasons) {
                z = false;
                int i2 = ActivityManagerService.this.mUidNetworkBlockedReasons.get(i, 0);
                if (i2 != 0 && NetworkPolicyManagerInternal.updateBlockedReasonsWithProcState(i2, 2) == 0) {
                    z = true;
                }
            }
            return z;
        }

        public void deletePendingTopUid(int i, long j) {
            ActivityManagerService.this.mPendingStartActivityUids.delete(i, j);
        }

        public boolean isPendingTopUid(int i) {
            return ActivityManagerService.this.mPendingStartActivityUids.isPendingTopUid(i);
        }

        public Intent getIntentForIntentSender(IIntentSender iIntentSender) {
            return ActivityManagerService.this.getIntentForIntentSender(iIntentSender);
        }

        public PendingIntent getPendingIntentActivityAsApp(int i, Intent intent, int i2, Bundle bundle, String str, int i3) {
            return getPendingIntentActivityAsApp(i, new Intent[]{intent}, i2, bundle, str, i3);
        }

        public PendingIntent getPendingIntentActivityAsApp(int i, Intent[] intentArr, int i2, Bundle bundle, String str, int i3) {
            if (((i2 & 67108864) != 0) == ((i2 & 33554432) != 0)) {
                throw new IllegalArgumentException("Must set exactly one of FLAG_IMMUTABLE or FLAG_MUTABLE");
            }
            Context context = ActivityManagerService.this.mContext;
            ContentResolver contentResolver = context.getContentResolver();
            int length = intentArr.length;
            String[] strArr = new String[length];
            for (int i4 = 0; i4 < length; i4++) {
                Intent intent = intentArr[i4];
                strArr[i4] = intent.resolveTypeIfNeeded(contentResolver);
                intent.migrateExtraStreamToClipData(context);
                intent.prepareToLeaveProcess(context);
            }
            IIntentSender intentSenderWithFeatureAsApp = ActivityManagerService.this.getIntentSenderWithFeatureAsApp(2, str, context.getAttributionTag(), null, null, i, intentArr, strArr, i2, bundle, UserHandle.getUserId(i3), i3);
            if (intentSenderWithFeatureAsApp != null) {
                return new PendingIntent(intentSenderWithFeatureAsApp);
            }
            return null;
        }

        public long getBootTimeTempAllowListDuration() {
            return ActivityManagerService.this.mConstants.mBootTimeTempAllowlistDuration;
        }

        public void registerAnrController(AnrController anrController) {
            ActivityManagerService.this.mActivityTaskManager.registerAnrController(anrController);
        }

        public void unregisterAnrController(AnrController anrController) {
            ActivityManagerService.this.mActivityTaskManager.unregisterAnrController(anrController);
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean canStartForegroundService(int i, int i2, String str) {
            boolean canStartForegroundServiceLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    canStartForegroundServiceLocked = ActivityManagerService.this.mServices.canStartForegroundServiceLocked(i, i2, str);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return canStartForegroundServiceLocked;
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public void tempAllowWhileInUsePermissionInFgs(int i, long j) {
            ActivityManagerService.this.mFgsWhileInUseTempAllowList.add(i, j, "");
        }

        public boolean isTempAllowlistedForFgsWhileInUse(int i) {
            return ActivityManagerService.this.mFgsWhileInUseTempAllowList.isAllowed(i);
        }

        @Override // com.android.server.p006am.ActivityManagerLocal
        public boolean canAllowWhileInUsePermissionInFgs(int i, int i2, String str) {
            boolean canAllowWhileInUsePermissionInFgsLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    canAllowWhileInUsePermissionInFgsLocked = ActivityManagerService.this.mServices.canAllowWhileInUsePermissionInFgsLocked(i, i2, str);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return canAllowWhileInUsePermissionInFgsLocked;
        }

        public int getPushMessagingOverQuotaBehavior() {
            int i;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    i = ActivityManagerService.this.mConstants.mPushMessagingOverQuotaBehavior;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return i;
        }

        public int getServiceStartForegroundTimeout() {
            return ActivityManagerService.this.mConstants.mServiceStartForegroundTimeoutMs;
        }

        public int getUidCapability(int i) {
            int curCapability;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    UidRecord uidRecordLOSP = ActivityManagerService.this.mProcessList.getUidRecordLOSP(i);
                    if (uidRecordLOSP == null) {
                        throw new IllegalArgumentException("uid record for " + i + " not found");
                    }
                    curCapability = uidRecordLOSP.getCurCapability();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return curCapability;
        }

        public List<Integer> getIsolatedProcesses(int i) {
            List<Integer> isolatedProcessesLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    isolatedProcessesLocked = ActivityManagerService.this.mProcessList.getIsolatedProcessesLocked(i);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return isolatedProcessesLocked;
        }

        public int sendIntentSender(IIntentSender iIntentSender, IBinder iBinder, int i, Intent intent, String str, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
            return ActivityManagerService.this.sendIntentSender(null, iIntentSender, iBinder, i, intent, str, iIntentReceiver, str2, bundle);
        }

        public void setVoiceInteractionManagerProvider(ActivityManagerInternal.VoiceInteractionManagerProvider voiceInteractionManagerProvider) {
            ActivityManagerService.this.setVoiceInteractionManagerProvider(voiceInteractionManagerProvider);
        }

        public void setStopUserOnSwitch(int i) {
            ActivityManagerService.this.setStopUserOnSwitch(i);
        }

        public int getRestrictionLevel(int i) {
            return ActivityManagerService.this.mAppRestrictionController.getRestrictionLevel(i);
        }

        public int getRestrictionLevel(String str, int i) {
            return ActivityManagerService.this.mAppRestrictionController.getRestrictionLevel(str, i);
        }

        public boolean isBgAutoRestrictedBucketFeatureFlagEnabled() {
            return ActivityManagerService.this.mAppRestrictionController.isBgAutoRestrictedBucketFeatureFlagEnabled();
        }

        public void addAppBackgroundRestrictionListener(ActivityManagerInternal.AppBackgroundRestrictionListener appBackgroundRestrictionListener) {
            ActivityManagerService.this.mAppRestrictionController.addAppBackgroundRestrictionListener(appBackgroundRestrictionListener);
        }

        public void addForegroundServiceStateListener(ActivityManagerInternal.ForegroundServiceStateListener foregroundServiceStateListener) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mForegroundServiceStateListeners.add(foregroundServiceStateListener);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void addBroadcastEventListener(ActivityManagerInternal.BroadcastEventListener broadcastEventListener) {
            ActivityManagerService.this.mBroadcastEventListeners.add(broadcastEventListener);
        }

        public void addBindServiceEventListener(ActivityManagerInternal.BindServiceEventListener bindServiceEventListener) {
            ActivityManagerService.this.mBindServiceEventListeners.add(bindServiceEventListener);
        }

        public void restart() {
            ActivityManagerService.this.restart();
        }

        public void registerNetworkPolicyUidObserver(IUidObserver iUidObserver, int i, int i2, String str) {
            ActivityManagerService.this.mNetworkPolicyUidObserver = iUidObserver;
            ActivityManagerService.this.mUidObserverController.register(iUidObserver, i, i2, str, Binder.getCallingUid());
        }

        public boolean startForegroundServiceDelegate(ForegroundServiceDelegationOptions foregroundServiceDelegationOptions, ServiceConnection serviceConnection) {
            boolean startForegroundServiceDelegateLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    startForegroundServiceDelegateLocked = ActivityManagerService.this.mServices.startForegroundServiceDelegateLocked(foregroundServiceDelegationOptions, serviceConnection);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return startForegroundServiceDelegateLocked;
        }

        public void stopForegroundServiceDelegate(ForegroundServiceDelegationOptions foregroundServiceDelegationOptions) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mServices.stopForegroundServiceDelegateLocked(foregroundServiceDelegationOptions);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void stopForegroundServiceDelegate(ServiceConnection serviceConnection) {
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActivityManagerService.this.mServices.stopForegroundServiceDelegateLocked(serviceConnection);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public ArraySet<String> getClientPackages(String str) {
            ArraySet<String> clientPackagesLocked;
            synchronized (ActivityManagerService.this) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    clientPackagesLocked = ActivityManagerService.this.mServices.getClientPackagesLocked(str);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return clientPackagesLocked;
        }

        public IUnsafeIntentStrictModeCallback getRegisteredStrictModeCallback(int i) {
            return (IUnsafeIntentStrictModeCallback) ActivityManagerService.this.mStrictModeCallbacks.get(i);
        }

        public void unregisterStrictModeCallback(int i) {
            ActivityManagerService.this.mStrictModeCallbacks.remove(i);
        }

        public boolean startProfileEvenWhenDisabled(int i) {
            return ActivityManagerService.this.mUserController.startProfile(i, true, null);
        }

        public void logFgsApiBegin(int i, int i2, int i3) {
            ActivityManagerService.this.logFgsApiBegin(i, i2, i3);
        }

        public void logFgsApiEnd(int i, int i2, int i3) {
            ActivityManagerService.this.logFgsApiEnd(i, i2, i3);
        }
    }

    public long inputDispatchingTimedOut(int i, boolean z, TimeoutRecord timeoutRecord) {
        ProcessRecord processRecord;
        long j;
        if (checkCallingPermission("android.permission.FILTER_EVENTS") != 0) {
            throw new SecurityException("Requires permission android.permission.FILTER_EVENTS");
        }
        timeoutRecord.mLatencyTracker.waitingOnPidLockStarted();
        synchronized (this.mPidsSelfLocked) {
            timeoutRecord.mLatencyTracker.waitingOnPidLockEnded();
            processRecord = this.mPidsSelfLocked.get(i);
        }
        if (processRecord != null) {
            j = processRecord.getInputDispatchingTimeoutMillis();
        } else {
            j = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        }
        if (inputDispatchingTimedOut(processRecord, null, null, null, null, z, timeoutRecord)) {
            return 0L;
        }
        return j;
    }

    public boolean inputDispatchingTimedOut(ProcessRecord processRecord, String str, ApplicationInfo applicationInfo, String str2, WindowProcessController windowProcessController, boolean z, TimeoutRecord timeoutRecord) {
        try {
            Trace.traceBegin(64L, "inputDispatchingTimedOut()");
            if (checkCallingPermission("android.permission.FILTER_EVENTS") == 0) {
                if (processRecord != null) {
                    timeoutRecord.mLatencyTracker.waitingOnAMSLockStarted();
                    synchronized (this) {
                        boostPriorityForLockedSection();
                        timeoutRecord.mLatencyTracker.waitingOnAMSLockEnded();
                        if (processRecord.isDebugging()) {
                            resetPriorityAfterLockedSection();
                            return false;
                        } else if (processRecord.getActiveInstrumentation() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("shortMsg", "keyDispatchingTimedOut");
                            bundle.putString("longMsg", timeoutRecord.mReason);
                            finishInstrumentationLocked(processRecord, 0, bundle);
                            resetPriorityAfterLockedSection();
                            return true;
                        } else {
                            resetPriorityAfterLockedSection();
                            this.mAnrHelper.appNotResponding(processRecord, str, applicationInfo, str2, windowProcessController, z, timeoutRecord, true);
                        }
                    }
                }
                return true;
            }
            throw new SecurityException("Requires permission android.permission.FILTER_EVENTS");
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void waitForNetworkStateUpdate(long j) {
        int callingUid = Binder.getCallingUid();
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                UidRecord uidRecordLOSP = this.mProcessList.getUidRecordLOSP(callingUid);
                if (uidRecordLOSP == null) {
                    resetPriorityAfterProcLockedSection();
                    return;
                }
                resetPriorityAfterProcLockedSection();
                synchronized (uidRecordLOSP.networkStateLock) {
                    if (uidRecordLOSP.lastNetworkUpdatedProcStateSeq >= j) {
                        return;
                    }
                    try {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        uidRecordLOSP.procStateSeqWaitingForNetwork = j;
                        uidRecordLOSP.networkStateLock.wait(this.mConstants.mNetworkAccessTimeoutMs);
                        uidRecordLOSP.procStateSeqWaitingForNetwork = 0L;
                        long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
                        if (uptimeMillis2 >= this.mConstants.mNetworkAccessTimeoutMs) {
                            Slog.w("ActivityManager_Network", "Total time waited for network rules to get updated: " + uptimeMillis2 + ". Uid: " + callingUid + " procStateSeq: " + j + " UidRec: " + uidRecordLOSP + " validateUidRec: " + this.mUidObserverController.getValidateUidRecord(callingUid));
                        }
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public void waitForBroadcastIdle() {
        waitForBroadcastIdle(null);
    }

    public void waitForBroadcastIdle(PrintWriter printWriter) {
        enforceCallingPermission("android.permission.DUMP", "waitForBroadcastIdle()");
        BroadcastLoopers.waitForIdle(printWriter);
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.waitForIdle(printWriter);
        }
        if (printWriter != null) {
            printWriter.println("All broadcast queues are idle!");
            printWriter.flush();
        }
    }

    public void waitForBroadcastBarrier() {
        waitForBroadcastBarrier(null, false, false);
    }

    public void waitForBroadcastBarrier(PrintWriter printWriter, boolean z, boolean z2) {
        enforceCallingPermission("android.permission.DUMP", "waitForBroadcastBarrier()");
        if (z) {
            BroadcastLoopers.waitForBarrier(printWriter);
        }
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.waitForBarrier(printWriter);
        }
        if (z2) {
            waitForApplicationBarrier(printWriter);
        }
    }

    public void waitForApplicationBarrier(PrintWriter printWriter) {
        int i;
        int i2;
        ActivityManagerService activityManagerService = this;
        PrintWriter printWriter2 = printWriter == null ? new PrintWriter((Writer) new LogWriter(2, "ActivityManager")) : printWriter;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        int i3 = 0;
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final AtomicInteger atomicInteger2 = new AtomicInteger(0);
        RemoteCallback remoteCallback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda1
            public final void onResult(Bundle bundle) {
                ActivityManagerService.lambda$waitForApplicationBarrier$30(atomicInteger2, atomicInteger, countDownLatch, bundle);
            }
        });
        atomicInteger.incrementAndGet();
        synchronized (activityManagerService.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                ArrayMap map = activityManagerService.mProcessList.getProcessNamesLOSP().getMap();
                int size = map.size();
                int i4 = 0;
                while (i4 < size) {
                    SparseArray sparseArray = (SparseArray) map.valueAt(i4);
                    int size2 = sparseArray.size();
                    int i5 = i3;
                    while (i5 < size2) {
                        ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i5);
                        IApplicationThread onewayThread = processRecord.getOnewayThread();
                        if (onewayThread != null) {
                            i2 = 0;
                            activityManagerService.mOomAdjuster.mCachedAppOptimizer.unfreezeTemporarily(processRecord, 0);
                            atomicInteger.incrementAndGet();
                            try {
                                onewayThread.schedulePing(remoteCallback);
                            } catch (RemoteException unused) {
                                remoteCallback.sendResult((Bundle) null);
                            }
                        } else {
                            i2 = i3;
                        }
                        i5++;
                        i3 = i2;
                        activityManagerService = this;
                    }
                    i4++;
                    activityManagerService = this;
                }
                i = i3;
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        remoteCallback.sendResult((Bundle) null);
        for (int i6 = i; i6 < 30; i6++) {
            if (countDownLatch.await(1L, TimeUnit.SECONDS)) {
                printWriter2.println("Finished application barriers!");
                return;
            }
            printWriter2.println("Waiting for application barriers, at " + atomicInteger2.get() + " of " + atomicInteger.get() + "...");
        }
        printWriter2.println("Gave up waiting for application barriers!");
    }

    public static /* synthetic */ void lambda$waitForApplicationBarrier$30(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, CountDownLatch countDownLatch, Bundle bundle) {
        if (atomicInteger.incrementAndGet() == atomicInteger2.get()) {
            countDownLatch.countDown();
        }
    }

    public void setIgnoreDeliveryGroupPolicy(String str) {
        Objects.requireNonNull(str);
        enforceCallingPermission("android.permission.DUMP", "waitForBroadcastBarrier()");
        synchronized (this.mDeliveryGroupPolicyIgnoredActions) {
            this.mDeliveryGroupPolicyIgnoredActions.add(str);
        }
    }

    public void clearIgnoreDeliveryGroupPolicy(String str) {
        Objects.requireNonNull(str);
        enforceCallingPermission("android.permission.DUMP", "waitForBroadcastBarrier()");
        synchronized (this.mDeliveryGroupPolicyIgnoredActions) {
            this.mDeliveryGroupPolicyIgnoredActions.remove(str);
        }
    }

    public boolean shouldIgnoreDeliveryGroupPolicy(String str) {
        boolean contains;
        if (str == null) {
            return false;
        }
        synchronized (this.mDeliveryGroupPolicyIgnoredActions) {
            contains = this.mDeliveryGroupPolicyIgnoredActions.contains(str);
        }
        return contains;
    }

    public void dumpDeliveryGroupPolicyIgnoredActions(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mDeliveryGroupPolicyIgnoredActions) {
            indentingPrintWriter.println(this.mDeliveryGroupPolicyIgnoredActions);
        }
    }

    public void forceDelayBroadcastDelivery(String str, long j) {
        Objects.requireNonNull(str);
        Preconditions.checkArgumentNonnegative(j);
        Preconditions.checkState(this.mEnableModernQueue, "Not valid in legacy queue");
        enforceCallingPermission("android.permission.DUMP", "forceDelayBroadcastDelivery()");
        for (BroadcastQueue broadcastQueue : this.mBroadcastQueues) {
            broadcastQueue.forceDelayBroadcastDelivery(str, j);
        }
    }

    public boolean isModernBroadcastQueueEnabled() {
        enforceCallingPermission("android.permission.DUMP", "isModernBroadcastQueueEnabled()");
        return this.mEnableModernQueue;
    }

    public boolean isProcessFrozen(int i) {
        enforceCallingPermission("android.permission.DUMP", "isProcessFrozen()");
        return this.mOomAdjuster.mCachedAppOptimizer.isProcessFrozen(i);
    }

    public int getBackgroundRestrictionExemptionReason(int i) {
        enforceCallingPermission("android.permission.DEVICE_POWER", "getBackgroundRestrictionExemptionReason()");
        return this.mAppRestrictionController.getBackgroundRestrictionExemptionReason(i);
    }

    public void setBackgroundRestrictionLevel(String str, int i, int i2, int i3, int i4, int i5) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000 && callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("No permission to change app restriction level");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mAppRestrictionController.applyRestrictionLevel(str, i, i3, null, this.mUsageStatsService.getAppStandbyBucket(str, i2, SystemClock.elapsedRealtime()), true, i4, i5);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getBackgroundRestrictionLevel(String str, int i) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000 && callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("Don't have permission to query app background restriction level");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInternal.getRestrictionLevel(str, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setForegroundServiceDelegate(String str, int i, boolean z, int i2, String str2) {
        long j;
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000 && callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("No permission to start/stop foreground service delegate");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                try {
                    synchronized (this) {
                        try {
                            boostPriorityForLockedSection();
                            ArrayList arrayList = new ArrayList();
                            synchronized (this.mPidsSelfLocked) {
                                boolean z2 = false;
                                int i3 = 0;
                                while (i3 < this.mPidsSelfLocked.size()) {
                                    try {
                                        ProcessRecord valueAt = this.mPidsSelfLocked.valueAt(i3);
                                        IApplicationThread thread = valueAt.getThread();
                                        if (valueAt.uid != i || thread == null) {
                                            j = clearCallingIdentity;
                                        } else {
                                            j = clearCallingIdentity;
                                            try {
                                                arrayList.add(new ForegroundServiceDelegationOptions(this.mPidsSelfLocked.keyAt(i3), i, str, (IApplicationThread) null, false, str2, 0, i2));
                                                z2 = true;
                                            } catch (Throwable th) {
                                                th = th;
                                                throw th;
                                            }
                                        }
                                        i3++;
                                        clearCallingIdentity = j;
                                    } catch (Throwable th2) {
                                        th = th2;
                                    }
                                }
                                long j2 = clearCallingIdentity;
                                for (int size = arrayList.size() - 1; size >= 0; size--) {
                                    ForegroundServiceDelegationOptions foregroundServiceDelegationOptions = (ForegroundServiceDelegationOptions) arrayList.get(size);
                                    if (z) {
                                        this.mInternal.startForegroundServiceDelegate(foregroundServiceDelegationOptions, (ServiceConnection) null);
                                    } else {
                                        this.mInternal.stopForegroundServiceDelegate(foregroundServiceDelegationOptions);
                                    }
                                }
                                resetPriorityAfterLockedSection();
                                if (!z2) {
                                    Slog.e("ActivityManager", "setForegroundServiceDelegate can not find process for packageName:" + str + " uid:" + i);
                                }
                                Binder.restoreCallingIdentity(j2);
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                } catch (Throwable th4) {
                    th = th4;
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
            }
        } catch (Throwable th6) {
            th = th6;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void refreshSettingsCache() {
        this.mCoreSettingsObserver.onChange(true);
    }

    public void resetDropboxRateLimiter() {
        this.mDropboxRateLimiter.reset();
        BootReceiver.resetDropboxRateLimiter();
    }

    public void killPackageDependents(String str, int i) {
        int i2;
        enforceCallingPermission("android.permission.KILL_UID", "killPackageDependents()");
        if (str == null) {
            throw new NullPointerException("Cannot kill the dependents of a package without its name.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            i2 = AppGlobals.getPackageManager().getPackageUid(str, 268435456L, i);
        } catch (RemoteException unused) {
            i2 = -1;
        }
        if (i != -1 && i2 == -1) {
            throw new IllegalArgumentException("Cannot kill dependents of non-existing package " + str);
        }
        try {
            synchronized (this) {
                boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    boostPriorityForProcLockedSection();
                    this.mProcessList.killPackageProcessesLSP(str, UserHandle.getAppId(i2), i, 0, 12, 0, "dep: " + str);
                }
                resetPriorityAfterProcLockedSection();
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int restartUserInBackground(int i) {
        return this.mUserController.restartUser(i, 2);
    }

    public void scheduleApplicationInfoChanged(List<String> list, int i) {
        enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "scheduleApplicationInfoChanged()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            boolean contains = list.contains(PackageManagerShellCommandDataLoader.PACKAGE);
            synchronized (this.mProcLock) {
                boostPriorityForProcLockedSection();
                updateApplicationInfoLOSP(list, contains, i);
            }
            resetPriorityAfterProcLockedSection();
            AppWidgetManagerInternal appWidgetManagerInternal = (AppWidgetManagerInternal) LocalServices.getService(AppWidgetManagerInternal.class);
            if (appWidgetManagerInternal != null) {
                appWidgetManagerInternal.applyResourceOverlaysToWidgets(new HashSet(list), i, contains);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateSystemUiContext() {
        ActivityThread.currentActivityThread().handleSystemApplicationInfoChanged(getPackageManagerInternal().getApplicationInfo(PackageManagerShellCommandDataLoader.PACKAGE, 1024L, Binder.getCallingUid(), 0));
    }

    @GuardedBy(anyOf = {"this", "mProcLock"})
    public final void updateApplicationInfoLOSP(List<String> list, boolean z, int i) {
        if (z) {
            ParsingPackageUtils.readConfigUseRoundIcon(null);
        }
        this.mProcessList.updateApplicationInfoLOSP(list, i, z);
        if (z) {
            Executor executor = ActivityThread.currentActivityThread().getExecutor();
            final DisplayManagerInternal displayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
            if (displayManagerInternal != null) {
                executor.execute(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        displayManagerInternal.onOverlayChanged();
                    }
                });
            }
            final WindowManagerService windowManagerService = this.mWindowManager;
            if (windowManagerService != null) {
                Objects.requireNonNull(windowManagerService);
                executor.execute(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        WindowManagerService.this.onOverlayChanged();
                    }
                });
            }
        }
    }

    public void scheduleUpdateBinderHeavyHitterWatcherConfig() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$35();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$35() {
        int i;
        float f;
        BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener binderCallHeavyHitterListener;
        boolean z;
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (ActivityManagerConstants.BINDER_HEAVY_HITTER_WATCHER_ENABLED) {
                    this.mHandler.removeMessages(72);
                    i = ActivityManagerConstants.BINDER_HEAVY_HITTER_WATCHER_BATCHSIZE;
                    f = ActivityManagerConstants.BINDER_HEAVY_HITTER_WATCHER_THRESHOLD;
                    binderCallHeavyHitterListener = new BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda31
                        public final void onHeavyHit(List list, int i2, float f2, long j) {
                            ActivityManagerService.this.lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$32(list, i2, f2, j);
                        }
                    };
                    z = true;
                } else if (this.mHandler.hasMessages(72)) {
                    boolean z2 = ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED;
                    int i2 = ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE;
                    float f2 = ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_THRESHOLD;
                    BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener binderCallHeavyHitterListener2 = new BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda32
                        public final void onHeavyHit(List list, int i3, float f3, long j) {
                            ActivityManagerService.this.lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$34(list, i3, f3, j);
                        }
                    };
                    z = z2;
                    i = i2;
                    f = f2;
                    binderCallHeavyHitterListener = binderCallHeavyHitterListener2;
                } else {
                    i = 0;
                    f = 0.0f;
                    binderCallHeavyHitterListener = null;
                    z = false;
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        Binder.setHeavyHitterWatcherConfig(z, i, f, binderCallHeavyHitterListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$32(final List list, final int i, final float f, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$31(list, i, f, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$34(final List list, final int i, final float f, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$33(list, i, f, j);
            }
        });
    }

    public void scheduleBinderHeavyHitterAutoSampler() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$scheduleBinderHeavyHitterAutoSampler$38();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleBinderHeavyHitterAutoSampler$38() {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (!ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED) {
                    resetPriorityAfterProcLockedSection();
                } else if (ActivityManagerConstants.BINDER_HEAVY_HITTER_WATCHER_ENABLED) {
                    resetPriorityAfterProcLockedSection();
                } else {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    if (this.mLastBinderHeavyHitterAutoSamplerStart + ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS > uptimeMillis) {
                        resetPriorityAfterProcLockedSection();
                        return;
                    }
                    int i = ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE;
                    float f = ActivityManagerConstants.BINDER_HEAVY_HITTER_AUTO_SAMPLER_THRESHOLD;
                    resetPriorityAfterProcLockedSection();
                    this.mLastBinderHeavyHitterAutoSamplerStart = uptimeMillis;
                    Binder.setHeavyHitterWatcherConfig(true, i, f, new BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda21
                        public final void onHeavyHit(List list, int i2, float f2, long j) {
                            ActivityManagerService.this.lambda$scheduleBinderHeavyHitterAutoSampler$37(list, i2, f2, j);
                        }
                    });
                    MainHandler mainHandler = this.mHandler;
                    mainHandler.sendMessageDelayed(mainHandler.obtainMessage(72), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleBinderHeavyHitterAutoSampler$37(final List list, final int i, final float f, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerService.this.lambda$scheduleBinderHeavyHitterAutoSampler$36(list, i, f, j);
            }
        });
    }

    public final void handleBinderHeavyHitterAutoSamplerTimeOut() {
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (ActivityManagerConstants.BINDER_HEAVY_HITTER_WATCHER_ENABLED) {
                    resetPriorityAfterProcLockedSection();
                    return;
                }
                resetPriorityAfterProcLockedSection();
                Binder.setHeavyHitterWatcherConfig(false, 0, 0.0f, null);
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    /* renamed from: handleBinderHeavyHitters */
    public final void lambda$scheduleUpdateBinderHeavyHitterWatcherConfig$33(List<BinderCallHeavyHitterWatcher.HeavyHitterContainer> list, int i, float f, long j) {
        int size = list.size();
        if (size == 0) {
            return;
        }
        BinderTransactionNameResolver binderTransactionNameResolver = new BinderTransactionNameResolver();
        StringBuilder sb = new StringBuilder("Excessive incoming binder calls(>");
        sb.append(String.format("%.1f%%", Float.valueOf(f * 100.0f)));
        sb.append(',');
        sb.append(i);
        sb.append(',');
        sb.append(j);
        sb.append("ms): ");
        for (int i2 = 0; i2 < size; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            BinderCallHeavyHitterWatcher.HeavyHitterContainer heavyHitterContainer = list.get(i2);
            sb.append('[');
            sb.append(heavyHitterContainer.mUid);
            sb.append(',');
            sb.append(heavyHitterContainer.mClass.getName());
            sb.append(',');
            sb.append(binderTransactionNameResolver.getMethodName(heavyHitterContainer.mClass, heavyHitterContainer.mCode));
            sb.append(',');
            sb.append(heavyHitterContainer.mCode);
            sb.append(',');
            sb.append(String.format("%.1f%%", Float.valueOf(heavyHitterContainer.mFrequency * 100.0f)));
            sb.append(']');
        }
        Slog.w("ActivityManager", sb.toString());
    }

    public void attachAgent(String str, String str2) {
        IApplicationThread thread;
        try {
            synchronized (this.mProcLock) {
                boostPriorityForProcLockedSection();
                ProcessRecord findProcessLOSP = findProcessLOSP(str, 0, "attachAgent");
                if (findProcessLOSP == null || (thread = findProcessLOSP.getThread()) == null) {
                    throw new IllegalArgumentException("Unknown process: " + str);
                }
                enforceDebuggable(findProcessLOSP);
                thread.attachAgent(str2);
            }
            resetPriorityAfterProcLockedSection();
        } catch (RemoteException unused) {
            throw new IllegalStateException("Process disappeared");
        }
    }

    public void prepareForPossibleShutdown() {
        if (this.mUsageStatsService != null) {
            this.mUsageStatsService.prepareForPossibleShutdown();
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.ActivityManagerService$Injector */
    /* loaded from: classes.dex */
    public static class Injector {
        public final Context mContext;
        public NetworkManagementInternal mNmi;
        public UserController mUserController;

        public Injector(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return this.mContext;
        }

        public AppOpsService getAppOpsService(File file, File file2, Handler handler) {
            return new AppOpsService(file, file2, handler, getContext());
        }

        public Handler getUiHandler(ActivityManagerService activityManagerService) {
            Objects.requireNonNull(activityManagerService);
            return new UiHandler();
        }

        public boolean isNetworkRestrictedForUid(int i) {
            if (ensureHasNetworkManagementInternal()) {
                return this.mNmi.isNetworkRestrictedForUid(i);
            }
            return false;
        }

        public int[] getDisplayIdsForStartingVisibleBackgroundUsers() {
            boolean z;
            if (!UserManager.isVisibleBackgroundUsersEnabled()) {
                Slogf.m14w("ActivityManager", "getDisplayIdsForStartingVisibleBackgroundUsers(): not supported");
                return null;
            }
            DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            Display[] displays = displayManager.getDisplays();
            if (displays == null || displays.length == 0) {
                Slogf.wtf("ActivityManager", "displayManager (%s) returned no displays", displayManager);
                return null;
            }
            int length = displays.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (displays[i].getDisplayId() == 0) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                Slogf.wtf("ActivityManager", "displayManager (%s) has %d displays (%s), but none has id DEFAULT_DISPLAY (%d)", displayManager, Integer.valueOf(displays.length), Arrays.toString(displays), 0);
                return null;
            }
            boolean isVisibleBackgroundUsersOnDefaultDisplayEnabled = UserManager.isVisibleBackgroundUsersOnDefaultDisplayEnabled();
            int length2 = displays.length;
            if (!isVisibleBackgroundUsersOnDefaultDisplayEnabled) {
                length2--;
            }
            int[] iArr = new int[length2];
            int i2 = 0;
            for (Display display : displays) {
                int displayId = display.getDisplayId();
                if (display.isValid() && (isVisibleBackgroundUsersOnDefaultDisplayEnabled || displayId != 0)) {
                    iArr[i2] = displayId;
                    i2++;
                }
            }
            if (i2 != 0) {
                if (i2 != length2) {
                    int[] iArr2 = new int[i2];
                    System.arraycopy(iArr, 0, iArr2, 0, i2);
                    return iArr2;
                }
                return iArr;
            }
            int i3 = SystemProperties.getInt("fw.display_ids_for_starting_users_for_testing_purposes", 0);
            if ((isVisibleBackgroundUsersOnDefaultDisplayEnabled && i3 == 0) || i3 > 0) {
                Slogf.m12w("ActivityManager", "getDisplayIdsForStartingVisibleBackgroundUsers(): no valid display found, but returning %d as set by property %s", Integer.valueOf(i3), "fw.display_ids_for_starting_users_for_testing_purposes");
                return new int[]{i3};
            }
            Slogf.m24e("ActivityManager", "getDisplayIdsForStartingVisibleBackgroundUsers(): no valid display on %s", Arrays.toString(displays));
            return null;
        }

        public boolean startUserInBackgroundVisibleOnDisplay(int i, int i2, IProgressListener iProgressListener) {
            return this.mUserController.startUserVisibleOnDisplay(i, i2, iProgressListener);
        }

        public ProcessList getProcessList(ActivityManagerService activityManagerService) {
            return new ProcessList();
        }

        public BatteryStatsService getBatteryStatsService() {
            Context context = this.mContext;
            File ensureSystemDir = SystemServiceManager.ensureSystemDir();
            BackgroundThread.get();
            return new BatteryStatsService(context, ensureSystemDir, BackgroundThread.getHandler());
        }

        public ActiveServices getActiveServices(ActivityManagerService activityManagerService) {
            return new ActiveServices(activityManagerService);
        }

        public final boolean ensureHasNetworkManagementInternal() {
            if (this.mNmi == null) {
                this.mNmi = (NetworkManagementInternal) LocalServices.getService(NetworkManagementInternal.class);
            }
            return this.mNmi != null;
        }
    }

    public void startDelegateShellPermissionIdentity(int i, String[] strArr) {
        if (UserHandle.getCallingAppId() != 2000 && UserHandle.getCallingAppId() != 0) {
            throw new SecurityException("Only the shell can delegate its permissions");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                if (this.mAppOpsService.getAppOpsServiceDelegate() != null) {
                    if (!(this.mAppOpsService.getAppOpsServiceDelegate() instanceof ShellDelegate)) {
                        throw new IllegalStateException("Bad shell delegate state");
                    }
                    if (((ShellDelegate) this.mAppOpsService.getAppOpsServiceDelegate()).getDelegateUid() != i) {
                        throw new SecurityException("Shell can delegate permissions only to one instrumentation at a time");
                    }
                }
                int size = this.mActiveInstrumentation.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActiveInstrumentation activeInstrumentation = this.mActiveInstrumentation.get(i2);
                    if (activeInstrumentation.mTargetInfo.uid == i) {
                        if (activeInstrumentation.mUiAutomationConnection == null) {
                            throw new SecurityException("Shell can delegate its permissions only to an instrumentation started from the shell");
                        }
                        this.mAppOpsService.setAppOpsServiceDelegate(new ShellDelegate(i, strArr));
                        getPermissionManagerInternal().startShellPermissionIdentityDelegation(i, activeInstrumentation.mTargetInfo.packageName, strArr != null ? Arrays.asList(strArr) : null);
                        resetPriorityAfterProcLockedSection();
                        return;
                    }
                }
                resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
    }

    public void stopDelegateShellPermissionIdentity() {
        if (UserHandle.getCallingAppId() != 2000 && UserHandle.getCallingAppId() != 0) {
            throw new SecurityException("Only the shell can delegate its permissions");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                this.mAppOpsService.setAppOpsServiceDelegate(null);
                getPermissionManagerInternal().stopShellPermissionIdentityDelegation();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
    }

    public List<String> getDelegatedShellPermissions() {
        List<String> delegatedShellPermissions;
        if (UserHandle.getCallingAppId() != 2000 && UserHandle.getCallingAppId() != 0) {
            throw new SecurityException("Only the shell can get delegated permissions");
        }
        synchronized (this.mProcLock) {
            try {
                boostPriorityForProcLockedSection();
                delegatedShellPermissions = getPermissionManagerInternal().getDelegatedShellPermissions();
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return delegatedShellPermissions;
    }

    /* renamed from: com.android.server.am.ActivityManagerService$ShellDelegate */
    /* loaded from: classes.dex */
    public class ShellDelegate implements AppOpsManagerInternal.CheckOpsDelegate {
        public final String[] mPermissions;
        public final int mTargetUid;

        public ShellDelegate(int i, String[] strArr) {
            this.mTargetUid = i;
            this.mPermissions = strArr;
        }

        public int getDelegateUid() {
            return this.mTargetUid;
        }

        public int checkOperation(int i, int i2, String str, String str2, boolean z, QuintFunction<Integer, Integer, String, String, Boolean, Integer> quintFunction) {
            if (i2 == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(i2), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return ((Integer) quintFunction.apply(Integer.valueOf(i), Integer.valueOf(uid), "com.android.shell", (Object) null, Boolean.valueOf(z))).intValue();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return ((Integer) quintFunction.apply(Integer.valueOf(i), Integer.valueOf(i2), str, str2, Boolean.valueOf(z))).intValue();
        }

        public int checkAudioOperation(int i, int i2, int i3, String str, QuadFunction<Integer, Integer, Integer, String, Integer> quadFunction) {
            if (i3 == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(i3), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return ((Integer) quadFunction.apply(Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(uid), "com.android.shell")).intValue();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return ((Integer) quadFunction.apply(Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str)).intValue();
        }

        public SyncNotedAppOp noteOperation(int i, int i2, String str, String str2, boolean z, String str3, boolean z2, HeptFunction<Integer, Integer, String, String, Boolean, String, Boolean, SyncNotedAppOp> heptFunction) {
            if (i2 == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(i2), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return (SyncNotedAppOp) heptFunction.apply(Integer.valueOf(i), Integer.valueOf(uid), "com.android.shell", str2, Boolean.valueOf(z), str3, Boolean.valueOf(z2));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return (SyncNotedAppOp) heptFunction.apply(Integer.valueOf(i), Integer.valueOf(i2), str, str2, Boolean.valueOf(z), str3, Boolean.valueOf(z2));
        }

        public SyncNotedAppOp noteProxyOperation(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3, HexFunction<Integer, AttributionSource, Boolean, String, Boolean, Boolean, SyncNotedAppOp> hexFunction) {
            if (attributionSource.getUid() == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(attributionSource.getUid()), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return (SyncNotedAppOp) hexFunction.apply(Integer.valueOf(i), new AttributionSource(uid, "com.android.shell", attributionSource.getAttributionTag(), attributionSource.getToken(), attributionSource.getNext()), Boolean.valueOf(z), str, Boolean.valueOf(z2), Boolean.valueOf(z3));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return (SyncNotedAppOp) hexFunction.apply(Integer.valueOf(i), attributionSource, Boolean.valueOf(z), str, Boolean.valueOf(z2), Boolean.valueOf(z3));
        }

        public SyncNotedAppOp startOperation(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4, UndecFunction<IBinder, Integer, Integer, String, String, Boolean, Boolean, String, Boolean, Integer, Integer, SyncNotedAppOp> undecFunction) {
            if (i2 == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(i2), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return (SyncNotedAppOp) undecFunction.apply(iBinder, Integer.valueOf(i), Integer.valueOf(uid), "com.android.shell", str2, Boolean.valueOf(z), Boolean.valueOf(z2), str3, Boolean.valueOf(z3), Integer.valueOf(i3), Integer.valueOf(i4));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return (SyncNotedAppOp) undecFunction.apply(iBinder, Integer.valueOf(i), Integer.valueOf(i2), str, str2, Boolean.valueOf(z), Boolean.valueOf(z2), str3, Boolean.valueOf(z3), Integer.valueOf(i3), Integer.valueOf(i4));
        }

        public SyncNotedAppOp startProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4, UndecFunction<IBinder, Integer, AttributionSource, Boolean, Boolean, String, Boolean, Boolean, Integer, Integer, Integer, SyncNotedAppOp> undecFunction) {
            if (attributionSource.getUid() == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(attributionSource.getUid()), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return (SyncNotedAppOp) undecFunction.apply(iBinder, Integer.valueOf(i), new AttributionSource(uid, "com.android.shell", attributionSource.getAttributionTag(), attributionSource.getToken(), attributionSource.getNext()), Boolean.valueOf(z), Boolean.valueOf(z2), str, Boolean.valueOf(z3), Boolean.valueOf(z4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return (SyncNotedAppOp) undecFunction.apply(iBinder, Integer.valueOf(i), attributionSource, Boolean.valueOf(z), Boolean.valueOf(z2), str, Boolean.valueOf(z3), Boolean.valueOf(z4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
        }

        public void finishProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, QuadFunction<IBinder, Integer, AttributionSource, Boolean, Void> quadFunction) {
            if (attributionSource.getUid() == this.mTargetUid && isTargetOp(i)) {
                int uid = UserHandle.getUid(UserHandle.getUserId(attributionSource.getUid()), 2000);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    quadFunction.apply(iBinder, Integer.valueOf(i), new AttributionSource(uid, "com.android.shell", attributionSource.getAttributionTag(), attributionSource.getToken(), attributionSource.getNext()), Boolean.valueOf(z));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            quadFunction.apply(iBinder, Integer.valueOf(i), attributionSource, Boolean.valueOf(z));
        }

        public final boolean isTargetOp(int i) {
            String opToPermission;
            if (this.mPermissions == null || (opToPermission = AppOpsManager.opToPermission(i)) == null) {
                return true;
            }
            return isTargetPermission(opToPermission);
        }

        public final boolean isTargetPermission(String str) {
            String[] strArr = this.mPermissions;
            return strArr == null || ArrayUtils.contains(strArr, str);
        }
    }

    public final boolean isOnBgOffloadQueue(int i) {
        return this.mEnableOffloadQueue && (Integer.MIN_VALUE & i) != 0;
    }

    public ParcelFileDescriptor getLifeMonitor() {
        ParcelFileDescriptor dup;
        if (!isCallerShell()) {
            throw new SecurityException("Only shell can call it");
        }
        synchronized (this.mProcLock) {
            try {
                try {
                    boostPriorityForProcLockedSection();
                    if (this.mLifeMonitorFds == null) {
                        this.mLifeMonitorFds = ParcelFileDescriptor.createPipe();
                    }
                    dup = this.mLifeMonitorFds[0].dup();
                } catch (IOException e) {
                    Slog.w("ActivityManager", "Unable to create pipe", e);
                    resetPriorityAfterProcLockedSection();
                    return null;
                }
            } catch (Throwable th) {
                resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        resetPriorityAfterProcLockedSection();
        return dup;
    }

    public void setActivityLocusContext(ComponentName componentName, LocusId locusId, IBinder iBinder) {
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (getPackageManagerInternal().getPackageUid(componentName.getPackageName(), 0L, callingUserId) != callingUid) {
            throw new SecurityException("Calling uid " + callingUid + " cannot set locusIdfor package " + componentName.getPackageName());
        }
        this.mActivityTaskManager.setLocusId(locusId, iBinder);
        if (this.mUsageStatsService != null) {
            this.mUsageStatsService.reportLocusUpdate(componentName, callingUserId, locusId, iBinder);
        }
    }

    public boolean isAppFreezerSupported() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            CachedAppOptimizer cachedAppOptimizer = this.mOomAdjuster.mCachedAppOptimizer;
            return CachedAppOptimizer.isFreezerSupported();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isAppFreezerEnabled() {
        return this.mOomAdjuster.mCachedAppOptimizer.useFreezer();
    }

    public boolean isAppFreezerExemptInstPkg() {
        return this.mOomAdjuster.mCachedAppOptimizer.freezerExemptInstPkg();
    }

    public void resetAppErrors() {
        enforceCallingPermission("android.permission.RESET_APP_ERRORS", "resetAppErrors");
        this.mAppErrors.resetState();
    }

    public boolean enableAppFreezer(boolean z) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000 || Build.IS_DEBUGGABLE) {
            return this.mOomAdjuster.mCachedAppOptimizer.enableFreezer(z);
        }
        throw new SecurityException("Caller uid " + callingUid + " cannot set freezer state ");
    }

    public boolean enableFgsNotificationRateLimit(boolean z) {
        boolean enableFgsNotificationRateLimitLocked;
        enforceCallingPermission("android.permission.WRITE_DEVICE_CONFIG", "enableFgsNotificationRateLimit");
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                enableFgsNotificationRateLimitLocked = this.mServices.enableFgsNotificationRateLimitLocked(z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return enableFgsNotificationRateLimitLocked;
    }

    public void holdLock(IBinder iBinder, int i) {
        getTestUtilityServiceLocked().verifyHoldLockToken(iBinder);
        synchronized (this) {
            try {
                boostPriorityForLockedSection();
                SystemClock.sleep(i);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public static void traceBegin(long j, String str, String str2) {
        if (Trace.isTagEnabled(j)) {
            Trace.traceBegin(j, str + str2);
        }
    }

    public static int getIntArg(PrintWriter printWriter, String[] strArr, int i, int i2) {
        if (i > strArr.length) {
            printWriter.println("Missing argument");
            return i2;
        }
        String str = strArr[i];
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            printWriter.printf("Non-numeric argument at index %d: %s\n", Integer.valueOf(i), str);
            return i2;
        }
    }
}
