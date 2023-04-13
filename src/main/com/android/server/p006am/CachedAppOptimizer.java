package com.android.server.p006am;

import android.app.ActivityThread;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.ProcLocksReader;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.ServiceThread;
import com.android.server.p006am.CachedAppOptimizer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.CachedAppOptimizer */
/* loaded from: classes.dex */
public final class CachedAppOptimizer {
    @VisibleForTesting
    static final Uri CACHED_APP_FREEZER_ENABLED_URI;
    public static final String[] COMPACT_ACTION_STRING = {"", "file", "anon", "all"};
    @VisibleForTesting
    static final int DEFAULT_COMPACT_ACTION_1 = 1;
    @VisibleForTesting
    static final int DEFAULT_COMPACT_ACTION_2 = 3;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB = 8000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB = 12000;
    @VisibleForTesting
    static final String DEFAULT_COMPACT_PROC_STATE_THROTTLE;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_1 = 5000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_2 = 10000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_3 = 500;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_4 = 10000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_5 = 600000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_6 = 600000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ = 999;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ = 900;
    @VisibleForTesting
    static final long DEFAULT_FREEZER_DEBOUNCE_TIMEOUT = 600000;
    @VisibleForTesting
    static final Boolean DEFAULT_FREEZER_EXEMPT_INST_PKG;
    @VisibleForTesting
    static final float DEFAULT_STATSD_SAMPLE_RATE = 0.1f;
    @VisibleForTesting
    static final Boolean DEFAULT_USE_COMPACTION;
    @VisibleForTesting
    static final Boolean DEFAULT_USE_FREEZER;
    @VisibleForTesting
    static final String KEY_COMPACT_ACTION_1 = "compact_action_1";
    @VisibleForTesting
    static final String KEY_COMPACT_ACTION_2 = "compact_action_2";
    @VisibleForTesting
    static final String KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB = "compact_full_delta_rss_throttle_kb";
    @VisibleForTesting
    static final String KEY_COMPACT_FULL_RSS_THROTTLE_KB = "compact_full_rss_throttle_kb";
    @VisibleForTesting
    static final String KEY_COMPACT_PROC_STATE_THROTTLE = "compact_proc_state_throttle";
    @VisibleForTesting
    static final String KEY_COMPACT_STATSD_SAMPLE_RATE = "compact_statsd_sample_rate";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_1 = "compact_throttle_1";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_2 = "compact_throttle_2";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_3 = "compact_throttle_3";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_4 = "compact_throttle_4";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_5 = "compact_throttle_5";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_6 = "compact_throttle_6";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_MAX_OOM_ADJ = "compact_throttle_max_oom_adj";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_MIN_OOM_ADJ = "compact_throttle_min_oom_adj";
    @VisibleForTesting
    static final String KEY_FREEZER_DEBOUNCE_TIMEOUT = "freeze_debounce_timeout";
    @VisibleForTesting
    static final String KEY_FREEZER_EXEMPT_INST_PKG = "freeze_exempt_inst_pkg";
    @VisibleForTesting
    static final String KEY_FREEZER_STATSD_SAMPLE_RATE = "freeze_statsd_sample_rate";
    @VisibleForTesting
    static final String KEY_USE_COMPACTION = "use_compaction";
    @VisibleForTesting
    static final String KEY_USE_FREEZER = "use_freezer";
    public final ActivityManagerService mAm;
    public final ServiceThread mCachedAppOptimizerThread;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile CompactAction mCompactActionFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile CompactAction mCompactActionSome;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile float mCompactStatsdSampleRate;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleBFGS;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleFullFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleFullSome;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleMaxOomAdj;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleMinOomAdj;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottlePersistent;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleSomeFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleSomeSome;
    @VisibleForTesting
    Handler mCompactionHandler;
    public LinkedList<SingleCompactionStats> mCompactionStatsHistory;
    public Handler mFreezeHandler;
    @VisibleForTesting
    volatile long mFreezerDebounceTimeout;
    @GuardedBy({"this"})
    public int mFreezerDisableCount;
    @VisibleForTesting
    volatile boolean mFreezerExemptInstPkg;
    public final Object mFreezerLock;
    @GuardedBy({"mProcLock"})
    public boolean mFreezerOverride;
    @VisibleForTesting
    volatile float mFreezerStatsdSampleRate;
    @GuardedBy({"mProcLock"})
    public final SparseArray<ProcessRecord> mFrozenProcesses;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mFullAnonRssThrottleKb;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mFullDeltaRssThrottleKb;
    @GuardedBy({"mProcLock"})
    @VisibleForTesting
    LinkedHashMap<Integer, SingleCompactionStats> mLastCompactionStats;
    public final DeviceConfig.OnPropertiesChangedListener mOnFlagsChangedListener;
    public final DeviceConfig.OnPropertiesChangedListener mOnNativeBootFlagsChangedListener;
    @GuardedBy({"mProcLock"})
    public final ArrayList<ProcessRecord> mPendingCompactionProcesses;
    public final LinkedHashMap<String, AggregatedProcessCompactionStats> mPerProcessCompactStats;
    public final EnumMap<CompactSource, AggregatedSourceCompactionStats> mPerSourceCompactStats;
    @VisibleForTesting
    final Object mPhenotypeFlagLock;
    public final ActivityManagerGlobalLock mProcLock;
    public final ProcLocksReader mProcLocksReader;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    final Set<Integer> mProcStateThrottle;
    public final ProcessDependencies mProcessDependencies;
    public final Random mRandom;
    public final SettingsContentObserver mSettingsObserver;
    public long mSystemCompactionsPerformed;
    public long mSystemTotalMemFreed;
    public PropertyChangedCallbackForTest mTestCallback;
    public long mTotalCompactionDowngrades;
    public EnumMap<CancelCompactReason, Integer> mTotalCompactionsCancelled;
    @GuardedBy({"mPhenotypeFlagLock"})
    public volatile boolean mUseCompaction;
    public volatile boolean mUseFreezer;

    /* renamed from: com.android.server.am.CachedAppOptimizer$CancelCompactReason */
    /* loaded from: classes.dex */
    public enum CancelCompactReason {
        SCREEN_ON,
        OOM_IMPROVEMENT
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$CompactAction */
    /* loaded from: classes.dex */
    public enum CompactAction {
        NONE,
        FILE,
        ANON,
        ALL
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$CompactProfile */
    /* loaded from: classes.dex */
    public enum CompactProfile {
        SOME,
        FULL
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$CompactSource */
    /* loaded from: classes.dex */
    public enum CompactSource {
        APP,
        PERSISTENT,
        BFGS
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.CachedAppOptimizer$ProcessDependencies */
    /* loaded from: classes.dex */
    public interface ProcessDependencies {
        long[] getRss(int i);

        void performCompaction(CompactAction compactAction, int i) throws IOException;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.CachedAppOptimizer$PropertyChangedCallbackForTest */
    /* loaded from: classes.dex */
    public interface PropertyChangedCallbackForTest {
        void onPropertyChanged();
    }

    private static native void cancelCompaction();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void compactProcess(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void compactSystem();

    public static native int freezeBinder(int i, boolean z, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int getBinderFreezeInfo(int i);

    public static native double getFreeSwapPercent();

    private static native String getFreezerCheckPath();

    /* JADX INFO: Access modifiers changed from: private */
    public static native long getMemoryFreedCompaction();

    /* JADX INFO: Access modifiers changed from: private */
    public static native long getUsedZramMemory();

    /* JADX INFO: Access modifiers changed from: private */
    public static native long threadCpuTimeNs();

    static {
        Boolean bool = Boolean.TRUE;
        DEFAULT_USE_COMPACTION = bool;
        DEFAULT_USE_FREEZER = bool;
        DEFAULT_COMPACT_PROC_STATE_THROTTLE = String.valueOf(11);
        DEFAULT_FREEZER_EXEMPT_INST_PKG = bool;
        CACHED_APP_FREEZER_ENABLED_URI = Settings.Global.getUriFor("cached_apps_freezer");
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$SettingsContentObserver */
    /* loaded from: classes.dex */
    public final class SettingsContentObserver extends ContentObserver {
        public SettingsContentObserver() {
            super(CachedAppOptimizer.this.mAm.mHandler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (CachedAppOptimizer.CACHED_APP_FREEZER_ENABLED_URI.equals(uri)) {
                synchronized (CachedAppOptimizer.this.mPhenotypeFlagLock) {
                    CachedAppOptimizer.this.updateUseFreezer();
                }
            }
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$AggregatedCompactionStats */
    /* loaded from: classes.dex */
    public class AggregatedCompactionStats {
        public long mFullCompactPerformed;
        public long mFullCompactRequested;
        public double mMaxCompactEfficiency;
        public long mProcCompactionsMiscThrottled;
        public long mProcCompactionsNoPidThrottled;
        public long mProcCompactionsOomAdjThrottled;
        public long mProcCompactionsRSSThrottled;
        public long mProcCompactionsTimeThrottled;
        public long mSomeCompactPerformed;
        public long mSomeCompactRequested;
        public long mSumOrigAnonRss;
        public long mTotalAnonMemFreedKBs;
        public long mTotalCpuTimeMillis;
        public long mTotalDeltaAnonRssKBs;
        public long mTotalZramConsumedKBs;

        public AggregatedCompactionStats() {
        }

        public long getThrottledSome() {
            return this.mSomeCompactRequested - this.mSomeCompactPerformed;
        }

        public long getThrottledFull() {
            return this.mFullCompactRequested - this.mFullCompactPerformed;
        }

        public void addMemStats(long j, long j2, long j3, long j4, long j5) {
            double d = j3 / j4;
            if (d > this.mMaxCompactEfficiency) {
                this.mMaxCompactEfficiency = d;
            }
            this.mTotalDeltaAnonRssKBs += j;
            this.mTotalZramConsumedKBs += j2;
            this.mTotalAnonMemFreedKBs += j3;
            this.mSumOrigAnonRss += j4;
            this.mTotalCpuTimeMillis += j5;
        }

        public void dump(PrintWriter printWriter) {
            long j = this.mSomeCompactRequested + this.mFullCompactRequested;
            long j2 = this.mSomeCompactPerformed + this.mFullCompactPerformed;
            printWriter.println("    Performed / Requested:");
            printWriter.println("      Some: (" + this.mSomeCompactPerformed + "/" + this.mSomeCompactRequested + ")");
            printWriter.println("      Full: (" + this.mFullCompactPerformed + "/" + this.mFullCompactRequested + ")");
            long throttledSome = getThrottledSome();
            long throttledFull = getThrottledFull();
            if (throttledSome > 0 || throttledFull > 0) {
                printWriter.println("    Throttled:");
                printWriter.println("       Some: " + throttledSome + " Full: " + throttledFull);
                printWriter.println("    Throttled by Type:");
                long j3 = j - j2;
                long j4 = ((((j3 - this.mProcCompactionsNoPidThrottled) - this.mProcCompactionsOomAdjThrottled) - this.mProcCompactionsTimeThrottled) - this.mProcCompactionsRSSThrottled) - this.mProcCompactionsMiscThrottled;
                printWriter.println("       NoPid: " + this.mProcCompactionsNoPidThrottled + " OomAdj: " + this.mProcCompactionsOomAdjThrottled + " Time: " + this.mProcCompactionsTimeThrottled + " RSS: " + this.mProcCompactionsRSSThrottled + " Misc: " + this.mProcCompactionsMiscThrottled + " Unaccounted: " + j4);
                StringBuilder sb = new StringBuilder();
                sb.append("    Throttle Percentage: ");
                sb.append((((double) j3) / ((double) j)) * 100.0d);
                printWriter.println(sb.toString());
            }
            if (this.mFullCompactPerformed > 0) {
                printWriter.println("    -----Memory Stats----");
                printWriter.println("    Total Delta Anon RSS (KB) : " + this.mTotalDeltaAnonRssKBs);
                printWriter.println("    Total Physical ZRAM Consumed (KB): " + this.mTotalZramConsumedKBs);
                printWriter.println("    Total Anon Memory Freed (KB): " + this.mTotalAnonMemFreedKBs);
                printWriter.println("    Avg Compaction Efficiency (Anon Freed/Anon RSS): " + (((double) this.mTotalAnonMemFreedKBs) / ((double) this.mSumOrigAnonRss)));
                printWriter.println("    Max Compaction Efficiency: " + this.mMaxCompactEfficiency);
                printWriter.println("    Avg Compression Ratio (1 - ZRAM Consumed/DeltaAnonRSS): " + (1.0d - (((double) this.mTotalZramConsumedKBs) / ((double) this.mTotalDeltaAnonRssKBs))));
                long j5 = this.mFullCompactPerformed;
                long j6 = j5 > 0 ? this.mTotalAnonMemFreedKBs / j5 : 0L;
                printWriter.println("    Avg Anon Mem Freed/Compaction (KB) : " + j6);
                printWriter.println("    Compaction Cost (ms/MB): " + (((double) this.mTotalCpuTimeMillis) / (((double) this.mTotalAnonMemFreedKBs) / 1024.0d)));
            }
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$AggregatedProcessCompactionStats */
    /* loaded from: classes.dex */
    public class AggregatedProcessCompactionStats extends AggregatedCompactionStats {
        public final String processName;

        public AggregatedProcessCompactionStats(String str) {
            super();
            this.processName = str;
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$AggregatedSourceCompactionStats */
    /* loaded from: classes.dex */
    public class AggregatedSourceCompactionStats extends AggregatedCompactionStats {
        public final CompactSource sourceType;

        public AggregatedSourceCompactionStats(CompactSource compactSource) {
            super();
            this.sourceType = compactSource;
        }
    }

    public CachedAppOptimizer(ActivityManagerService activityManagerService) {
        this(activityManagerService, null, new DefaultProcessDependencies());
    }

    @VisibleForTesting
    public CachedAppOptimizer(ActivityManagerService activityManagerService, PropertyChangedCallbackForTest propertyChangedCallbackForTest, ProcessDependencies processDependencies) {
        this.mPendingCompactionProcesses = new ArrayList<>();
        this.mFrozenProcesses = new SparseArray<>();
        this.mFreezerLock = new Object();
        this.mOnFlagsChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.CachedAppOptimizer.1
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                synchronized (CachedAppOptimizer.this.mPhenotypeFlagLock) {
                    for (String str : properties.getKeyset()) {
                        if (CachedAppOptimizer.KEY_USE_COMPACTION.equals(str)) {
                            CachedAppOptimizer.this.updateUseCompaction();
                        } else {
                            if (!CachedAppOptimizer.KEY_COMPACT_ACTION_1.equals(str) && !CachedAppOptimizer.KEY_COMPACT_ACTION_2.equals(str)) {
                                if (!CachedAppOptimizer.KEY_COMPACT_THROTTLE_1.equals(str) && !CachedAppOptimizer.KEY_COMPACT_THROTTLE_2.equals(str) && !CachedAppOptimizer.KEY_COMPACT_THROTTLE_3.equals(str) && !CachedAppOptimizer.KEY_COMPACT_THROTTLE_4.equals(str) && !CachedAppOptimizer.KEY_COMPACT_THROTTLE_5.equals(str) && !CachedAppOptimizer.KEY_COMPACT_THROTTLE_6.equals(str)) {
                                    if (CachedAppOptimizer.KEY_COMPACT_STATSD_SAMPLE_RATE.equals(str)) {
                                        CachedAppOptimizer.this.updateCompactStatsdSampleRate();
                                    } else if (CachedAppOptimizer.KEY_FREEZER_STATSD_SAMPLE_RATE.equals(str)) {
                                        CachedAppOptimizer.this.updateFreezerStatsdSampleRate();
                                    } else if (CachedAppOptimizer.KEY_COMPACT_FULL_RSS_THROTTLE_KB.equals(str)) {
                                        CachedAppOptimizer.this.updateFullRssThrottle();
                                    } else if (CachedAppOptimizer.KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB.equals(str)) {
                                        CachedAppOptimizer.this.updateFullDeltaRssThrottle();
                                    } else if (CachedAppOptimizer.KEY_COMPACT_PROC_STATE_THROTTLE.equals(str)) {
                                        CachedAppOptimizer.this.updateProcStateThrottle();
                                    } else if (CachedAppOptimizer.KEY_COMPACT_THROTTLE_MIN_OOM_ADJ.equals(str)) {
                                        CachedAppOptimizer.this.updateMinOomAdjThrottle();
                                    } else if (CachedAppOptimizer.KEY_COMPACT_THROTTLE_MAX_OOM_ADJ.equals(str)) {
                                        CachedAppOptimizer.this.updateMaxOomAdjThrottle();
                                    }
                                }
                                CachedAppOptimizer.this.updateCompactionThrottles();
                            }
                            CachedAppOptimizer.this.updateCompactionActions();
                        }
                    }
                }
                if (CachedAppOptimizer.this.mTestCallback != null) {
                    CachedAppOptimizer.this.mTestCallback.onPropertyChanged();
                }
            }
        };
        this.mOnNativeBootFlagsChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.CachedAppOptimizer.2
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                synchronized (CachedAppOptimizer.this.mPhenotypeFlagLock) {
                    for (String str : properties.getKeyset()) {
                        if (CachedAppOptimizer.KEY_FREEZER_DEBOUNCE_TIMEOUT.equals(str)) {
                            CachedAppOptimizer.this.updateFreezerDebounceTimeout();
                        } else if (CachedAppOptimizer.KEY_FREEZER_EXEMPT_INST_PKG.equals(str)) {
                            CachedAppOptimizer.this.updateFreezerExemptInstPkg();
                        }
                    }
                }
                if (CachedAppOptimizer.this.mTestCallback != null) {
                    CachedAppOptimizer.this.mTestCallback.onPropertyChanged();
                }
            }
        };
        this.mPhenotypeFlagLock = new Object();
        this.mCompactActionSome = compactActionIntToAction(1);
        this.mCompactActionFull = compactActionIntToAction(3);
        this.mCompactThrottleSomeSome = DEFAULT_COMPACT_THROTTLE_1;
        this.mCompactThrottleSomeFull = 10000L;
        this.mCompactThrottleFullSome = DEFAULT_COMPACT_THROTTLE_3;
        this.mCompactThrottleFullFull = 10000L;
        this.mCompactThrottleBFGS = 600000L;
        this.mCompactThrottlePersistent = 600000L;
        this.mCompactThrottleMinOomAdj = DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ;
        this.mCompactThrottleMaxOomAdj = DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ;
        this.mUseCompaction = DEFAULT_USE_COMPACTION.booleanValue();
        this.mUseFreezer = false;
        this.mFreezerDisableCount = 1;
        this.mRandom = new Random();
        this.mCompactStatsdSampleRate = DEFAULT_STATSD_SAMPLE_RATE;
        this.mFreezerStatsdSampleRate = DEFAULT_STATSD_SAMPLE_RATE;
        this.mFullAnonRssThrottleKb = DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB;
        this.mFullDeltaRssThrottleKb = DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB;
        this.mFreezerOverride = false;
        this.mFreezerDebounceTimeout = 600000L;
        this.mFreezerExemptInstPkg = DEFAULT_FREEZER_EXEMPT_INST_PKG.booleanValue();
        this.mLastCompactionStats = new LinkedHashMap<Integer, SingleCompactionStats>() { // from class: com.android.server.am.CachedAppOptimizer.3
            @Override // java.util.LinkedHashMap
            public boolean removeEldestEntry(Map.Entry<Integer, SingleCompactionStats> entry) {
                return size() > 256;
            }
        };
        this.mCompactionStatsHistory = new LinkedList<SingleCompactionStats>() { // from class: com.android.server.am.CachedAppOptimizer.4
            @Override // java.util.LinkedList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, java.util.Deque, java.util.Queue
            public boolean add(SingleCompactionStats singleCompactionStats) {
                if (size() >= 20) {
                    remove();
                }
                return super.add((C03964) singleCompactionStats);
            }
        };
        this.mPerProcessCompactStats = new LinkedHashMap<>(256);
        this.mPerSourceCompactStats = new EnumMap<>(CompactSource.class);
        this.mTotalCompactionsCancelled = new EnumMap<>(CancelCompactReason.class);
        this.mAm = activityManagerService;
        this.mProcLock = activityManagerService.mProcLock;
        this.mCachedAppOptimizerThread = new ServiceThread("CachedAppOptimizerThread", 2, true);
        this.mProcStateThrottle = new HashSet();
        this.mProcessDependencies = processDependencies;
        this.mTestCallback = propertyChangedCallbackForTest;
        this.mSettingsObserver = new SettingsContentObserver();
        this.mProcLocksReader = new ProcLocksReader();
    }

    public void init() {
        DeviceConfig.addOnPropertiesChangedListener("activity_manager", ActivityThread.currentApplication().getMainExecutor(), this.mOnFlagsChangedListener);
        DeviceConfig.addOnPropertiesChangedListener("activity_manager_native_boot", ActivityThread.currentApplication().getMainExecutor(), this.mOnNativeBootFlagsChangedListener);
        this.mAm.mContext.getContentResolver().registerContentObserver(CACHED_APP_FREEZER_ENABLED_URI, false, this.mSettingsObserver);
        synchronized (this.mPhenotypeFlagLock) {
            updateUseCompaction();
            updateCompactionActions();
            updateCompactionThrottles();
            updateCompactStatsdSampleRate();
            updateFreezerStatsdSampleRate();
            updateFullRssThrottle();
            updateFullDeltaRssThrottle();
            updateProcStateThrottle();
            updateUseFreezer();
            updateMinOomAdjThrottle();
            updateMaxOomAdjThrottle();
        }
    }

    public boolean useCompaction() {
        boolean z;
        synchronized (this.mPhenotypeFlagLock) {
            z = this.mUseCompaction;
        }
        return z;
    }

    public boolean useFreezer() {
        boolean z;
        synchronized (this.mPhenotypeFlagLock) {
            z = this.mUseFreezer;
        }
        return z;
    }

    public boolean freezerExemptInstPkg() {
        boolean z;
        synchronized (this.mPhenotypeFlagLock) {
            z = this.mUseFreezer && this.mFreezerExemptInstPkg;
        }
        return z;
    }

    @GuardedBy({"mProcLock"})
    public void dump(PrintWriter printWriter) {
        long j;
        printWriter.println("CachedAppOptimizer settings");
        synchronized (this.mPhenotypeFlagLock) {
            printWriter.println("  use_compaction=" + this.mUseCompaction);
            printWriter.println("  compact_action_1=" + this.mCompactActionSome);
            printWriter.println("  compact_action_2=" + this.mCompactActionFull);
            printWriter.println("  compact_throttle_1=" + this.mCompactThrottleSomeSome);
            printWriter.println("  compact_throttle_2=" + this.mCompactThrottleSomeFull);
            printWriter.println("  compact_throttle_3=" + this.mCompactThrottleFullSome);
            printWriter.println("  compact_throttle_4=" + this.mCompactThrottleFullFull);
            printWriter.println("  compact_throttle_5=" + this.mCompactThrottleBFGS);
            printWriter.println("  compact_throttle_6=" + this.mCompactThrottlePersistent);
            printWriter.println("  compact_throttle_min_oom_adj=" + this.mCompactThrottleMinOomAdj);
            printWriter.println("  compact_throttle_max_oom_adj=" + this.mCompactThrottleMaxOomAdj);
            printWriter.println("  compact_statsd_sample_rate=" + this.mCompactStatsdSampleRate);
            printWriter.println("  compact_full_rss_throttle_kb=" + this.mFullAnonRssThrottleKb);
            printWriter.println("  compact_full_delta_rss_throttle_kb=" + this.mFullDeltaRssThrottleKb);
            StringBuilder sb = new StringBuilder();
            sb.append("  compact_proc_state_throttle=");
            sb.append(Arrays.toString(this.mProcStateThrottle.toArray(new Integer[0])));
            printWriter.println(sb.toString());
            printWriter.println(" Per-Process Compaction Stats");
            long j2 = 0;
            long j3 = 0;
            for (AggregatedProcessCompactionStats aggregatedProcessCompactionStats : this.mPerProcessCompactStats.values()) {
                printWriter.println("-----" + aggregatedProcessCompactionStats.processName + "-----");
                j2 += aggregatedProcessCompactionStats.mSomeCompactPerformed;
                j3 += aggregatedProcessCompactionStats.mFullCompactPerformed;
                aggregatedProcessCompactionStats.dump(printWriter);
                printWriter.println();
            }
            printWriter.println();
            printWriter.println(" Per-Source Compaction Stats");
            for (AggregatedSourceCompactionStats aggregatedSourceCompactionStats : this.mPerSourceCompactStats.values()) {
                printWriter.println("-----" + aggregatedSourceCompactionStats.sourceType + "-----");
                aggregatedSourceCompactionStats.dump(printWriter);
                printWriter.println();
            }
            printWriter.println();
            printWriter.println("Total Compactions Performed by profile: " + j2 + " some, " + j3 + " full");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Total compactions downgraded: ");
            sb2.append(this.mTotalCompactionDowngrades);
            printWriter.println(sb2.toString());
            printWriter.println("Total compactions cancelled by reason: ");
            for (CancelCompactReason cancelCompactReason : this.mTotalCompactionsCancelled.keySet()) {
                printWriter.println("    " + cancelCompactReason + ": " + this.mTotalCompactionsCancelled.get(cancelCompactReason));
            }
            printWriter.println();
            printWriter.println(" System Compaction Memory Stats");
            printWriter.println("    Compactions Performed: " + this.mSystemCompactionsPerformed);
            printWriter.println("    Total Memory Freed (KB): " + this.mSystemTotalMemFreed);
            printWriter.println("    Avg Mem Freed per Compact (KB): " + (this.mSystemCompactionsPerformed > 0 ? this.mSystemTotalMemFreed / j : 0.0d));
            printWriter.println();
            printWriter.println("  Tracking last compaction stats for " + this.mLastCompactionStats.size() + " processes.");
            printWriter.println("Last Compaction per process stats:");
            printWriter.println("    (ProcessName,Source,DeltaAnonRssKBs,ZramConsumedKBs,AnonMemFreedKBs,CompactEfficiency,CompactCost(ms/MB),procState,oomAdj,oomAdjReason)");
            for (Map.Entry<Integer, SingleCompactionStats> entry : this.mLastCompactionStats.entrySet()) {
                entry.getValue().dump(printWriter);
            }
            printWriter.println();
            printWriter.println("Last 20 Compactions Stats:");
            printWriter.println("    (ProcessName,Source,DeltaAnonRssKBs,ZramConsumedKBs,AnonMemFreedKBs,CompactEfficiency,CompactCost(ms/MB),procState,oomAdj,oomAdjReason)");
            Iterator<SingleCompactionStats> it = this.mCompactionStatsHistory.iterator();
            while (it.hasNext()) {
                it.next().dump(printWriter);
            }
            printWriter.println();
            printWriter.println("  use_freezer=" + this.mUseFreezer);
            printWriter.println("  freeze_statsd_sample_rate=" + this.mFreezerStatsdSampleRate);
            printWriter.println("  freeze_debounce_timeout=" + this.mFreezerDebounceTimeout);
            printWriter.println("  freeze_exempt_inst_pkg=" + this.mFreezerExemptInstPkg);
            synchronized (this.mProcLock) {
                ActivityManagerService.boostPriorityForProcLockedSection();
                int size = this.mFrozenProcesses.size();
                printWriter.println("  Apps frozen: " + size);
                for (int i = 0; i < size; i++) {
                    ProcessRecord valueAt = this.mFrozenProcesses.valueAt(i);
                    printWriter.println("    " + valueAt.mOptRecord.getFreezeUnfreezeTime() + ": " + valueAt.getPid() + " " + valueAt.processName);
                }
                if (!this.mPendingCompactionProcesses.isEmpty()) {
                    printWriter.println("  Pending compactions:");
                    int size2 = this.mPendingCompactionProcesses.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        ProcessRecord processRecord = this.mPendingCompactionProcesses.get(i2);
                        printWriter.println("    pid: " + processRecord.getPid() + ". name: " + processRecord.processName + ". hasPendingCompact: " + processRecord.mOptRecord.hasPendingCompact());
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
        }
    }

    @GuardedBy({"mProcLock"})
    public boolean meetsCompactionRequirements(ProcessRecord processRecord) {
        return (this.mAm.mInternal.isPendingTopUid(processRecord.uid) || processRecord.mState.hasForegroundActivities()) ? false : true;
    }

    @GuardedBy({"mProcLock"})
    public boolean compactApp(ProcessRecord processRecord, CompactProfile compactProfile, CompactSource compactSource, boolean z) {
        processRecord.mOptRecord.setReqCompactSource(compactSource);
        processRecord.mOptRecord.setReqCompactProfile(compactProfile);
        AggregatedSourceCompactionStats perSourceAggregatedCompactStat = getPerSourceAggregatedCompactStat(compactSource);
        AggregatedProcessCompactionStats perProcessAggregatedCompactStat = getPerProcessAggregatedCompactStat(processRecord.processName);
        int i = C03975.f1119x5cb38e25[compactProfile.ordinal()];
        if (i == 1) {
            perProcessAggregatedCompactStat.mSomeCompactRequested++;
            perSourceAggregatedCompactStat.mSomeCompactRequested++;
        } else if (i == 2) {
            perProcessAggregatedCompactStat.mFullCompactRequested++;
            perSourceAggregatedCompactStat.mFullCompactRequested++;
        } else {
            Slog.e("ActivityManager", "Unimplemented compaction type, consider adding it.");
            return false;
        }
        if (processRecord.mOptRecord.hasPendingCompact() || !(meetsCompactionRequirements(processRecord) || z)) {
            return false;
        }
        processRecord.mOptRecord.setHasPendingCompact(true);
        processRecord.mOptRecord.setForceCompact(z);
        this.mPendingCompactionProcesses.add(processRecord);
        Handler handler = this.mCompactionHandler;
        handler.sendMessage(handler.obtainMessage(1, processRecord.mState.getCurAdj(), processRecord.mState.getSetProcState()));
        return true;
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$5 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C03975 {

        /* renamed from: $SwitchMap$com$android$server$am$CachedAppOptimizer$CompactProfile */
        public static final /* synthetic */ int[] f1119x5cb38e25;

        static {
            int[] iArr = new int[CompactProfile.values().length];
            f1119x5cb38e25 = iArr;
            try {
                iArr[CompactProfile.SOME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f1119x5cb38e25[CompactProfile.FULL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public final CompactAction resolveCompactActionForProfile(CompactProfile compactProfile) {
        int i = C03975.f1119x5cb38e25[compactProfile.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return CompactAction.ALL;
            }
            return CompactAction.NONE;
        }
        return CompactAction.FILE;
    }

    public final AggregatedProcessCompactionStats getPerProcessAggregatedCompactStat(String str) {
        if (str == null) {
            str = "";
        }
        AggregatedProcessCompactionStats aggregatedProcessCompactionStats = this.mPerProcessCompactStats.get(str);
        if (aggregatedProcessCompactionStats == null) {
            AggregatedProcessCompactionStats aggregatedProcessCompactionStats2 = new AggregatedProcessCompactionStats(str);
            this.mPerProcessCompactStats.put(str, aggregatedProcessCompactionStats2);
            return aggregatedProcessCompactionStats2;
        }
        return aggregatedProcessCompactionStats;
    }

    public final AggregatedSourceCompactionStats getPerSourceAggregatedCompactStat(CompactSource compactSource) {
        AggregatedSourceCompactionStats aggregatedSourceCompactionStats = this.mPerSourceCompactStats.get(compactSource);
        if (aggregatedSourceCompactionStats == null) {
            AggregatedSourceCompactionStats aggregatedSourceCompactionStats2 = new AggregatedSourceCompactionStats(compactSource);
            this.mPerSourceCompactStats.put((EnumMap<CompactSource, AggregatedSourceCompactionStats>) compactSource, (CompactSource) aggregatedSourceCompactionStats2);
            return aggregatedSourceCompactionStats2;
        }
        return aggregatedSourceCompactionStats;
    }

    @GuardedBy({"mProcLock"})
    public boolean shouldCompactPersistent(ProcessRecord processRecord, long j) {
        return processRecord.mOptRecord.getLastCompactTime() == 0 || j - processRecord.mOptRecord.getLastCompactTime() > this.mCompactThrottlePersistent;
    }

    @GuardedBy({"mProcLock"})
    public boolean shouldCompactBFGS(ProcessRecord processRecord, long j) {
        return processRecord.mOptRecord.getLastCompactTime() == 0 || j - processRecord.mOptRecord.getLastCompactTime() > this.mCompactThrottleBFGS;
    }

    public void compactAllSystem() {
        if (useCompaction()) {
            Trace.instantForTrack(64L, "Compaction", "compactAllSystem");
            Handler handler = this.mCompactionHandler;
            handler.sendMessage(handler.obtainMessage(2));
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateUseCompaction() {
        this.mUseCompaction = DeviceConfig.getBoolean("activity_manager", KEY_USE_COMPACTION, DEFAULT_USE_COMPACTION.booleanValue());
        if (this.mUseCompaction && this.mCompactionHandler == null) {
            if (!this.mCachedAppOptimizerThread.isAlive()) {
                this.mCachedAppOptimizerThread.start();
            }
            this.mCompactionHandler = new MemCompactionHandler();
            Process.setThreadGroupAndCpuset(this.mCachedAppOptimizerThread.getThreadId(), 2);
        }
    }

    public synchronized boolean enableFreezer(final boolean z) {
        if (this.mUseFreezer) {
            if (z) {
                int i = this.mFreezerDisableCount - 1;
                this.mFreezerDisableCount = i;
                if (i > 0) {
                    return true;
                }
                if (i < 0) {
                    Slog.e("ActivityManager", "unbalanced call to enableFreezer, ignoring");
                    this.mFreezerDisableCount = 0;
                    return false;
                }
            } else {
                int i2 = this.mFreezerDisableCount + 1;
                this.mFreezerDisableCount = i2;
                if (i2 > 1) {
                    return true;
                }
            }
            synchronized (this.mAm) {
                ActivityManagerService.boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    this.mFreezerOverride = z ? false : true;
                    Slog.d("ActivityManager", "freezer override set to " + this.mFreezerOverride);
                    this.mAm.mProcessList.forEachLruProcessesLOSP(true, new Consumer() { // from class: com.android.server.am.CachedAppOptimizer$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            CachedAppOptimizer.this.lambda$enableFreezer$0(z, (ProcessRecord) obj);
                        }
                    });
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enableFreezer$0(boolean z, ProcessRecord processRecord) {
        if (processRecord == null) {
            return;
        }
        ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
        if (z && processCachedOptimizerRecord.hasFreezerOverride()) {
            freezeAppAsyncLSP(processRecord);
            processCachedOptimizerRecord.setFreezerOverride(false);
        }
        if (z || !processCachedOptimizerRecord.isFrozen()) {
            return;
        }
        unfreezeAppLSP(processRecord, 0);
        processCachedOptimizerRecord.setFreezerOverride(true);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x005e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean isFreezerSupported() {
        FileReader fileReader;
        Exception e;
        char read;
        boolean z = false;
        FileReader fileReader2 = null;
        try {
            fileReader = new FileReader(getFreezerCheckPath());
            try {
                read = (char) fileReader.read();
            } catch (FileNotFoundException unused) {
                fileReader2 = fileReader;
                Slog.w("ActivityManager", "cgroup.freeze not present");
                fileReader = fileReader2;
                if (fileReader != null) {
                }
                return z;
            } catch (RuntimeException unused2) {
                fileReader2 = fileReader;
                Slog.w("ActivityManager", "unable to read freezer info");
                fileReader = fileReader2;
                if (fileReader != null) {
                }
                return z;
            } catch (Exception e2) {
                e = e2;
                Slog.w("ActivityManager", "unable to read cgroup.freeze: " + e.toString());
                if (fileReader != null) {
                }
                return z;
            }
        } catch (FileNotFoundException unused3) {
        } catch (RuntimeException unused4) {
        } catch (Exception e3) {
            fileReader = null;
            e = e3;
        }
        if (read != '1' && read != '0') {
            Slog.e("ActivityManager", "unexpected value in cgroup.freeze");
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e4) {
                    Slog.e("ActivityManager", "Exception closing cgroup.freeze: " + e4.toString());
                }
            }
            return z;
        }
        getBinderFreezeInfo(Process.myPid());
        z = true;
        if (fileReader != null) {
        }
        return z;
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateUseFreezer() {
        String string = Settings.Global.getString(this.mAm.mContext.getContentResolver(), "cached_apps_freezer");
        if ("disabled".equals(string)) {
            this.mUseFreezer = false;
        } else if ("enabled".equals(string) || DeviceConfig.getBoolean("activity_manager_native_boot", KEY_USE_FREEZER, DEFAULT_USE_FREEZER.booleanValue())) {
            this.mUseFreezer = isFreezerSupported();
            updateFreezerDebounceTimeout();
            updateFreezerExemptInstPkg();
        } else {
            this.mUseFreezer = false;
        }
        final boolean z = this.mUseFreezer;
        this.mAm.mHandler.post(new Runnable() { // from class: com.android.server.am.CachedAppOptimizer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CachedAppOptimizer.this.lambda$updateUseFreezer$1(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateUseFreezer$1(boolean z) {
        if (z) {
            Slog.d("ActivityManager", "Freezer enabled");
            enableFreezer(true);
            if (!this.mCachedAppOptimizerThread.isAlive()) {
                this.mCachedAppOptimizerThread.start();
            }
            if (this.mFreezeHandler == null) {
                this.mFreezeHandler = new FreezeHandler();
            }
            Process.setThreadGroupAndCpuset(this.mCachedAppOptimizerThread.getThreadId(), 2);
            return;
        }
        Slog.d("ActivityManager", "Freezer disabled");
        enableFreezer(false);
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateCompactionActions() {
        int i = DeviceConfig.getInt("activity_manager", KEY_COMPACT_ACTION_1, 1);
        int i2 = DeviceConfig.getInt("activity_manager", KEY_COMPACT_ACTION_2, 3);
        this.mCompactActionSome = compactActionIntToAction(i);
        this.mCompactActionFull = compactActionIntToAction(i2);
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateCompactionThrottles() {
        String property = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_1);
        String property2 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_2);
        String property3 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_3);
        String property4 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_4);
        String property5 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_5);
        String property6 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_6);
        String property7 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_MIN_OOM_ADJ);
        String property8 = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_MAX_OOM_ADJ);
        boolean z = true;
        if (!TextUtils.isEmpty(property) && !TextUtils.isEmpty(property2) && !TextUtils.isEmpty(property3) && !TextUtils.isEmpty(property4) && !TextUtils.isEmpty(property5) && !TextUtils.isEmpty(property6) && !TextUtils.isEmpty(property7) && !TextUtils.isEmpty(property8)) {
            try {
                this.mCompactThrottleSomeSome = Integer.parseInt(property);
                this.mCompactThrottleSomeFull = Integer.parseInt(property2);
                this.mCompactThrottleFullSome = Integer.parseInt(property3);
                this.mCompactThrottleFullFull = Integer.parseInt(property4);
                this.mCompactThrottleBFGS = Integer.parseInt(property5);
                this.mCompactThrottlePersistent = Integer.parseInt(property6);
                this.mCompactThrottleMinOomAdj = Long.parseLong(property7);
                this.mCompactThrottleMaxOomAdj = Long.parseLong(property8);
                z = false;
            } catch (NumberFormatException unused) {
            }
        }
        if (z) {
            this.mCompactThrottleSomeSome = DEFAULT_COMPACT_THROTTLE_1;
            this.mCompactThrottleSomeFull = 10000L;
            this.mCompactThrottleFullSome = DEFAULT_COMPACT_THROTTLE_3;
            this.mCompactThrottleFullFull = 10000L;
            this.mCompactThrottleBFGS = 600000L;
            this.mCompactThrottlePersistent = 600000L;
            this.mCompactThrottleMinOomAdj = DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ;
            this.mCompactThrottleMaxOomAdj = DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ;
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateCompactStatsdSampleRate() {
        this.mCompactStatsdSampleRate = DeviceConfig.getFloat("activity_manager", KEY_COMPACT_STATSD_SAMPLE_RATE, (float) DEFAULT_STATSD_SAMPLE_RATE);
        this.mCompactStatsdSampleRate = Math.min(1.0f, Math.max(0.0f, this.mCompactStatsdSampleRate));
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateFreezerStatsdSampleRate() {
        this.mFreezerStatsdSampleRate = DeviceConfig.getFloat("activity_manager", KEY_FREEZER_STATSD_SAMPLE_RATE, (float) DEFAULT_STATSD_SAMPLE_RATE);
        this.mFreezerStatsdSampleRate = Math.min(1.0f, Math.max(0.0f, this.mFreezerStatsdSampleRate));
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateFullRssThrottle() {
        this.mFullAnonRssThrottleKb = DeviceConfig.getLong("activity_manager", KEY_COMPACT_FULL_RSS_THROTTLE_KB, (long) DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB);
        if (this.mFullAnonRssThrottleKb < 0) {
            this.mFullAnonRssThrottleKb = DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB;
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateFullDeltaRssThrottle() {
        this.mFullDeltaRssThrottleKb = DeviceConfig.getLong("activity_manager", KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB, (long) DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB);
        if (this.mFullDeltaRssThrottleKb < 0) {
            this.mFullDeltaRssThrottleKb = DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB;
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateProcStateThrottle() {
        String str = DEFAULT_COMPACT_PROC_STATE_THROTTLE;
        String string = DeviceConfig.getString("activity_manager", KEY_COMPACT_PROC_STATE_THROTTLE, str);
        if (parseProcStateThrottle(string)) {
            return;
        }
        Slog.w("ActivityManager", "Unable to parse app compact proc state throttle \"" + string + "\" falling back to default.");
        if (parseProcStateThrottle(str)) {
            return;
        }
        Slog.wtf("ActivityManager", "Unable to parse default app compact proc state throttle " + str);
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateMinOomAdjThrottle() {
        this.mCompactThrottleMinOomAdj = DeviceConfig.getLong("activity_manager", KEY_COMPACT_THROTTLE_MIN_OOM_ADJ, (long) DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ);
        if (this.mCompactThrottleMinOomAdj < DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ) {
            this.mCompactThrottleMinOomAdj = DEFAULT_COMPACT_THROTTLE_MIN_OOM_ADJ;
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateMaxOomAdjThrottle() {
        this.mCompactThrottleMaxOomAdj = DeviceConfig.getLong("activity_manager", KEY_COMPACT_THROTTLE_MAX_OOM_ADJ, (long) DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ);
        if (this.mCompactThrottleMaxOomAdj > DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ) {
            this.mCompactThrottleMaxOomAdj = DEFAULT_COMPACT_THROTTLE_MAX_OOM_ADJ;
        }
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateFreezerDebounceTimeout() {
        this.mFreezerDebounceTimeout = DeviceConfig.getLong("activity_manager_native_boot", KEY_FREEZER_DEBOUNCE_TIMEOUT, 600000L);
        if (this.mFreezerDebounceTimeout < 0) {
            this.mFreezerDebounceTimeout = 600000L;
        }
        Slog.d("ActivityManager", "Freezer timeout set to " + this.mFreezerDebounceTimeout);
    }

    @GuardedBy({"mPhenotypeFlagLock"})
    public final void updateFreezerExemptInstPkg() {
        this.mFreezerExemptInstPkg = DeviceConfig.getBoolean("activity_manager_native_boot", KEY_FREEZER_EXEMPT_INST_PKG, DEFAULT_FREEZER_EXEMPT_INST_PKG.booleanValue());
        Slog.d("ActivityManager", "Freezer exemption set to " + this.mFreezerExemptInstPkg);
    }

    public final boolean parseProcStateThrottle(String str) {
        String[] split = TextUtils.split(str, ",");
        this.mProcStateThrottle.clear();
        for (String str2 : split) {
            try {
                this.mProcStateThrottle.add(Integer.valueOf(Integer.parseInt(str2)));
            } catch (NumberFormatException unused) {
                Slog.e("ActivityManager", "Failed to parse default app compaction proc state: " + str2);
                return false;
            }
        }
        return true;
    }

    public static CompactAction compactActionIntToAction(int i) {
        if (i < 0 || i >= CompactAction.values().length) {
            return CompactAction.NONE;
        }
        return CompactAction.values()[i];
    }

    @GuardedBy({"mAm"})
    public void unfreezeTemporarily(ProcessRecord processRecord, int i) {
        if (this.mUseFreezer) {
            synchronized (this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    if (processRecord.mOptRecord.isFrozen() || processRecord.mOptRecord.isPendingFreeze()) {
                        unfreezeAppLSP(processRecord, i);
                        freezeAppAsyncLSP(processRecord);
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
        }
    }

    @GuardedBy({"mAm", "mProcLock"})
    public void freezeAppAsyncLSP(ProcessRecord processRecord) {
        ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
        if (processCachedOptimizerRecord.isPendingFreeze()) {
            return;
        }
        Handler handler = this.mFreezeHandler;
        handler.sendMessageDelayed(handler.obtainMessage(3, 1, 0, processRecord), this.mFreezerDebounceTimeout);
        processCachedOptimizerRecord.setPendingFreeze(true);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x008c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x008d  */
    @GuardedBy({"mAm", "mProcLock", "mFreezerLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void unfreezeAppInternalLSP(ProcessRecord processRecord, int i) {
        boolean z;
        int pid = processRecord.getPid();
        ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
        if (processCachedOptimizerRecord.isPendingFreeze()) {
            this.mFreezeHandler.removeMessages(3, processRecord);
            processCachedOptimizerRecord.setPendingFreeze(false);
        }
        processCachedOptimizerRecord.setFreezerOverride(false);
        if (pid == 0 || !processCachedOptimizerRecord.isFrozen()) {
            return;
        }
        try {
        } catch (Exception e) {
            Slog.d("ActivityManager", "Unable to query binder frozen info for pid " + pid + " " + processRecord.processName + ". Killing it. Exception: " + e);
            processRecord.killLocked("Unable to query binder frozen stats", 14, 19, true);
        }
        if ((getBinderFreezeInfo(pid) & 1) != 0) {
            Slog.d("ActivityManager", "pid " + pid + " " + processRecord.processName + " received sync transactions while frozen, killing");
            processRecord.killLocked("Sync transaction while in frozen state", 14, 20, true);
            z = true;
            if (z) {
                long freezeUnfreezeTime = processCachedOptimizerRecord.getFreezeUnfreezeTime();
                try {
                    freezeBinder(pid, false, 100);
                    try {
                        traceAppFreeze(processRecord.processName, pid, false);
                        Process.setProcessFrozen(pid, processRecord.uid, false);
                        processCachedOptimizerRecord.setFreezeUnfreezeTime(SystemClock.uptimeMillis());
                        processCachedOptimizerRecord.setFrozen(false);
                        this.mFrozenProcesses.delete(pid);
                    } catch (Exception unused) {
                        Slog.e("ActivityManager", "Unable to unfreeze " + pid + " " + processRecord.processName + ". This might cause inconsistency or UI hangs.");
                    }
                    if (processCachedOptimizerRecord.isFrozen()) {
                        return;
                    }
                    Slog.d("ActivityManager", "sync unfroze " + pid + " " + processRecord.processName);
                    Handler handler = this.mFreezeHandler;
                    handler.sendMessage(handler.obtainMessage(4, pid, (int) Math.min(processCachedOptimizerRecord.getFreezeUnfreezeTime() - freezeUnfreezeTime, 2147483647L), new Pair(processRecord.processName, Integer.valueOf(i))));
                    return;
                } catch (RuntimeException unused2) {
                    Slog.e("ActivityManager", "Unable to unfreeze binder for " + pid + " " + processRecord.processName + ". Killing it");
                    processRecord.killLocked("Unable to unfreeze", 14, 19, true);
                    return;
                }
            }
            return;
        }
        z = false;
        if (z) {
        }
    }

    @GuardedBy({"mAm", "mProcLock"})
    public void unfreezeAppLSP(ProcessRecord processRecord, int i) {
        synchronized (this.mFreezerLock) {
            unfreezeAppInternalLSP(processRecord, i);
        }
    }

    public void unfreezeProcess(int i, int i2) {
        synchronized (this.mFreezerLock) {
            ProcessRecord processRecord = this.mFrozenProcesses.get(i);
            if (processRecord == null) {
                return;
            }
            Slog.d("ActivityManager", "quick sync unfreeze " + i);
            try {
                freezeBinder(i, false, 100);
                try {
                    traceAppFreeze(processRecord.processName, i, false);
                    Process.setProcessFrozen(i, processRecord.uid, false);
                } catch (Exception unused) {
                    Slog.e("ActivityManager", "Unable to quick unfreeze " + i);
                }
            } catch (RuntimeException unused2) {
                Slog.e("ActivityManager", "Unable to quick unfreeze binder for " + i);
            }
        }
    }

    public static void traceAppFreeze(String str, int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "Freeze " : "Unfreeze ");
        sb.append(str);
        sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
        sb.append(i);
        Trace.instantForTrack(64L, "Freezer", sb.toString());
    }

    @GuardedBy({"mAm", "mProcLock"})
    public void onCleanupApplicationRecordLocked(ProcessRecord processRecord) {
        if (this.mUseFreezer) {
            ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
            if (processCachedOptimizerRecord.isPendingFreeze()) {
                this.mFreezeHandler.removeMessages(3, processRecord);
                processCachedOptimizerRecord.setPendingFreeze(false);
            }
            this.mFrozenProcesses.delete(processRecord.getPid());
        }
    }

    public void onWakefulnessChanged(int i) {
        if (i == 1) {
            Slog.e("ActivityManager", "Cancel pending or running compactions as system is awake");
            cancelAllCompactions(CancelCompactReason.SCREEN_ON);
        }
    }

    public void cancelAllCompactions(CancelCompactReason cancelCompactReason) {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                while (!this.mPendingCompactionProcesses.isEmpty()) {
                    cancelCompactionForProcess(this.mPendingCompactionProcesses.get(0), cancelCompactReason);
                }
                this.mPendingCompactionProcesses.clear();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
    }

    @GuardedBy({"mProcLock"})
    public void cancelCompactionForProcess(ProcessRecord processRecord, CancelCompactReason cancelCompactReason) {
        boolean z = false;
        if (this.mPendingCompactionProcesses.contains(processRecord)) {
            processRecord.mOptRecord.setHasPendingCompact(false);
            this.mPendingCompactionProcesses.remove(processRecord);
            z = true;
        }
        if (DefaultProcessDependencies.mPidCompacting == processRecord.mPid) {
            cancelCompaction();
            z = true;
        }
        if (z) {
            if (this.mTotalCompactionsCancelled.containsKey(cancelCompactReason)) {
                this.mTotalCompactionsCancelled.put((EnumMap<CancelCompactReason, Integer>) cancelCompactReason, (CancelCompactReason) Integer.valueOf(this.mTotalCompactionsCancelled.get(cancelCompactReason).intValue() + 1));
                return;
            }
            this.mTotalCompactionsCancelled.put((EnumMap<CancelCompactReason, Integer>) cancelCompactReason, (CancelCompactReason) 1);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void onOomAdjustChanged(int i, int i2, ProcessRecord processRecord) {
        if (i2 < i && i2 < 900) {
            cancelCompactionForProcess(processRecord, CancelCompactReason.OOM_IMPROVEMENT);
        }
        if (i <= 200 && (i2 == 700 || i2 == 600)) {
            compactApp(processRecord, CompactProfile.SOME, CompactSource.APP, false);
        } else if (i >= 900 || i2 < 900 || i2 > 999) {
        } else {
            compactApp(processRecord, CompactProfile.FULL, CompactSource.APP, false);
        }
    }

    public CompactProfile downgradeCompactionIfRequired(CompactProfile compactProfile) {
        if (compactProfile != CompactProfile.FULL || getFreeSwapPercent() >= 0.2d) {
            return compactProfile;
        }
        CompactProfile compactProfile2 = CompactProfile.SOME;
        this.mTotalCompactionDowngrades++;
        return compactProfile2;
    }

    public boolean isProcessFrozen(int i) {
        boolean contains;
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                contains = this.mFrozenProcesses.contains(i);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        return contains;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.CachedAppOptimizer$SingleCompactionStats */
    /* loaded from: classes.dex */
    public static final class SingleCompactionStats {
        public static final Random mRandom = new Random();
        public long mAnonMemFreedKBs;
        public float mCpuTimeMillis;
        public long mDeltaAnonRssKBs;
        public int mOomAdj;
        public int mOomAdjReason;
        public long mOrigAnonRss;
        public int mProcState;
        public String mProcessName;
        public final long[] mRssAfterCompaction;
        public CompactSource mSourceType;
        public final int mUid;
        public long mZramConsumedKBs;

        public SingleCompactionStats(long[] jArr, CompactSource compactSource, String str, long j, long j2, long j3, long j4, long j5, int i, int i2, int i3, int i4) {
            this.mRssAfterCompaction = jArr;
            this.mSourceType = compactSource;
            this.mProcessName = str;
            this.mUid = i4;
            this.mDeltaAnonRssKBs = j;
            this.mZramConsumedKBs = j2;
            this.mAnonMemFreedKBs = j3;
            this.mCpuTimeMillis = (float) j5;
            this.mOrigAnonRss = j4;
            this.mProcState = i;
            this.mOomAdj = i2;
            this.mOomAdjReason = i3;
        }

        public double getCompactEfficiency() {
            return this.mAnonMemFreedKBs / this.mOrigAnonRss;
        }

        public double getCompactCost() {
            return (this.mCpuTimeMillis / this.mAnonMemFreedKBs) * 1024.0d;
        }

        public long[] getRssAfterCompaction() {
            return this.mRssAfterCompaction;
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("    (" + this.mProcessName + "," + this.mSourceType.name() + "," + this.mDeltaAnonRssKBs + "," + this.mZramConsumedKBs + "," + this.mAnonMemFreedKBs + "," + getCompactEfficiency() + "," + getCompactCost() + "," + this.mProcState + "," + this.mOomAdj + "," + OomAdjuster.oomAdjReasonToString(this.mOomAdjReason) + ")");
        }

        public void sendStat() {
            if (mRandom.nextFloat() < CachedAppOptimizer.DEFAULT_STATSD_SAMPLE_RATE) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.APP_COMPACTED_V2, this.mUid, this.mProcState, this.mOomAdj, this.mDeltaAnonRssKBs, this.mZramConsumedKBs, this.mCpuTimeMillis, this.mOrigAnonRss, this.mOomAdjReason);
            }
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$MemCompactionHandler */
    /* loaded from: classes.dex */
    public final class MemCompactionHandler extends Handler {
        public MemCompactionHandler() {
            super(CachedAppOptimizer.this.mCachedAppOptimizerThread.getLooper());
        }

        public final boolean shouldOomAdjThrottleCompaction(ProcessRecord processRecord) {
            String str = processRecord.processName;
            return processRecord.mState.getSetAdj() <= 200;
        }

        public final boolean shouldTimeThrottleCompaction(ProcessRecord processRecord, long j, CompactProfile compactProfile, CompactSource compactSource) {
            ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
            CompactProfile lastCompactProfile = processCachedOptimizerRecord.getLastCompactProfile();
            long lastCompactTime = processCachedOptimizerRecord.getLastCompactTime();
            if (lastCompactTime != 0) {
                if (compactSource != CompactSource.APP) {
                    return compactSource == CompactSource.PERSISTENT ? j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottlePersistent : compactSource == CompactSource.BFGS && j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottleBFGS;
                }
                CompactProfile compactProfile2 = CompactProfile.SOME;
                if (compactProfile == compactProfile2) {
                    return (lastCompactProfile == compactProfile2 && j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottleSomeSome) || (lastCompactProfile == CompactProfile.FULL && j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottleSomeFull);
                }
                CompactProfile compactProfile3 = CompactProfile.FULL;
                if (compactProfile == compactProfile3) {
                    return (lastCompactProfile == compactProfile2 && j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottleFullSome) || (lastCompactProfile == compactProfile3 && j - lastCompactTime < CachedAppOptimizer.this.mCompactThrottleFullFull);
                }
                return false;
            }
            return false;
        }

        public final boolean shouldThrottleMiscCompaction(ProcessRecord processRecord, int i) {
            return CachedAppOptimizer.this.mProcStateThrottle.contains(Integer.valueOf(i));
        }

        public final boolean shouldRssThrottleCompaction(CompactProfile compactProfile, int i, String str, long[] jArr) {
            long j = jArr[2];
            SingleCompactionStats singleCompactionStats = CachedAppOptimizer.this.mLastCompactionStats.get(Integer.valueOf(i));
            if (jArr[0] == 0 && jArr[1] == 0 && jArr[2] == 0 && jArr[3] == 0) {
                return true;
            }
            if (compactProfile == CompactProfile.FULL) {
                if (CachedAppOptimizer.this.mFullAnonRssThrottleKb > 0 && j < CachedAppOptimizer.this.mFullAnonRssThrottleKb) {
                    return true;
                }
                if (singleCompactionStats != null && CachedAppOptimizer.this.mFullDeltaRssThrottleKb > 0) {
                    long[] rssAfterCompaction = singleCompactionStats.getRssAfterCompaction();
                    if (Math.abs(jArr[1] - rssAfterCompaction[1]) + Math.abs(jArr[2] - rssAfterCompaction[2]) + Math.abs(jArr[3] - rssAfterCompaction[3]) <= CachedAppOptimizer.this.mFullDeltaRssThrottleKb) {
                        return true;
                    }
                }
            }
            return false;
        }

        /* JADX WARN: Finally extract failed */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AggregatedProcessCompactionStats aggregatedProcessCompactionStats;
            int i;
            ProcessCachedOptimizerRecord processCachedOptimizerRecord;
            char c;
            long j;
            int i2;
            ProcessRecord processRecord;
            int i3;
            long[] rss;
            long j2;
            long j3;
            int i4;
            int i5;
            int i6 = message.what;
            if (i6 != 1) {
                if (i6 != 2) {
                    return;
                }
                CachedAppOptimizer.this.mSystemCompactionsPerformed++;
                Trace.traceBegin(64L, "compactSystem");
                long memoryFreedCompaction = CachedAppOptimizer.getMemoryFreedCompaction();
                CachedAppOptimizer.this.compactSystem();
                long memoryFreedCompaction2 = CachedAppOptimizer.getMemoryFreedCompaction();
                CachedAppOptimizer.this.mSystemTotalMemFreed += memoryFreedCompaction2 - memoryFreedCompaction;
                Trace.traceEnd(64L);
                return;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            int i7 = message.arg1;
            int i8 = message.arg2;
            synchronized (CachedAppOptimizer.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    if (CachedAppOptimizer.this.mPendingCompactionProcesses.isEmpty()) {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        return;
                    }
                    ProcessRecord processRecord2 = (ProcessRecord) CachedAppOptimizer.this.mPendingCompactionProcesses.remove(0);
                    ProcessCachedOptimizerRecord processCachedOptimizerRecord2 = processRecord2.mOptRecord;
                    boolean isForceCompact = processCachedOptimizerRecord2.isForceCompact();
                    processCachedOptimizerRecord2.setForceCompact(false);
                    int pid = processRecord2.getPid();
                    String str = processRecord2.processName;
                    processCachedOptimizerRecord2.setHasPendingCompact(false);
                    CompactSource reqCompactSource = processCachedOptimizerRecord2.getReqCompactSource();
                    CompactProfile reqCompactProfile = processCachedOptimizerRecord2.getReqCompactProfile();
                    CompactProfile lastCompactProfile = processCachedOptimizerRecord2.getLastCompactProfile();
                    long lastCompactTime = processCachedOptimizerRecord2.getLastCompactTime();
                    int lastOomAdjChangeReason = processCachedOptimizerRecord2.getLastOomAdjChangeReason();
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    AggregatedSourceCompactionStats perSourceAggregatedCompactStat = CachedAppOptimizer.this.getPerSourceAggregatedCompactStat(processCachedOptimizerRecord2.getReqCompactSource());
                    AggregatedProcessCompactionStats perProcessAggregatedCompactStat = CachedAppOptimizer.this.getPerProcessAggregatedCompactStat(str);
                    if (pid == 0) {
                        perSourceAggregatedCompactStat.mProcCompactionsNoPidThrottled++;
                        perProcessAggregatedCompactStat.mProcCompactionsNoPidThrottled++;
                        return;
                    }
                    if (!isForceCompact) {
                        if (shouldOomAdjThrottleCompaction(processRecord2)) {
                            perProcessAggregatedCompactStat.mProcCompactionsOomAdjThrottled++;
                            perSourceAggregatedCompactStat.mProcCompactionsOomAdjThrottled++;
                            return;
                        }
                        aggregatedProcessCompactionStats = perProcessAggregatedCompactStat;
                        i = pid;
                        processCachedOptimizerRecord = processCachedOptimizerRecord2;
                        i2 = i7;
                        processRecord = processRecord2;
                        c = 0;
                        j = uptimeMillis;
                        i3 = i8;
                        if (shouldTimeThrottleCompaction(processRecord2, uptimeMillis, reqCompactProfile, reqCompactSource)) {
                            aggregatedProcessCompactionStats.mProcCompactionsTimeThrottled++;
                            perSourceAggregatedCompactStat.mProcCompactionsTimeThrottled++;
                            return;
                        } else if (shouldThrottleMiscCompaction(processRecord, i3)) {
                            aggregatedProcessCompactionStats.mProcCompactionsMiscThrottled++;
                            perSourceAggregatedCompactStat.mProcCompactionsMiscThrottled++;
                            return;
                        } else {
                            rss = CachedAppOptimizer.this.mProcessDependencies.getRss(i);
                            if (shouldRssThrottleCompaction(reqCompactProfile, i, str, rss)) {
                                aggregatedProcessCompactionStats.mProcCompactionsRSSThrottled++;
                                perSourceAggregatedCompactStat.mProcCompactionsRSSThrottled++;
                                return;
                            }
                        }
                    } else {
                        aggregatedProcessCompactionStats = perProcessAggregatedCompactStat;
                        i = pid;
                        processCachedOptimizerRecord = processCachedOptimizerRecord2;
                        c = 0;
                        j = uptimeMillis;
                        i2 = i7;
                        processRecord = processRecord2;
                        i3 = i8;
                        rss = CachedAppOptimizer.this.mProcessDependencies.getRss(i);
                    }
                    CompactAction resolveCompactActionForProfile = CachedAppOptimizer.this.resolveCompactActionForProfile(CachedAppOptimizer.this.downgradeCompactionIfRequired(reqCompactProfile));
                    try {
                        try {
                        } catch (Exception e) {
                            Slog.d("ActivityManager", "Exception occurred while compacting pid: " + str + ". Exception:" + e.getMessage());
                            j3 = 64;
                        }
                        try {
                            Trace.traceBegin(64L, "Compact " + resolveCompactActionForProfile.name() + ": " + str + " lastOomAdjReason: " + lastOomAdjChangeReason + " source: " + reqCompactSource.name());
                            long usedZramMemory = CachedAppOptimizer.getUsedZramMemory();
                            long threadCpuTimeNs = CachedAppOptimizer.threadCpuTimeNs();
                            CachedAppOptimizer.this.mProcessDependencies.performCompaction(resolveCompactActionForProfile, i);
                            long threadCpuTimeNs2 = CachedAppOptimizer.threadCpuTimeNs();
                            long[] rss2 = CachedAppOptimizer.this.mProcessDependencies.getRss(i);
                            long uptimeMillis2 = SystemClock.uptimeMillis();
                            long j4 = uptimeMillis2 - j;
                            long j5 = threadCpuTimeNs2 - threadCpuTimeNs;
                            long usedZramMemory2 = CachedAppOptimizer.getUsedZramMemory();
                            long j6 = rss2[c] - rss[c];
                            long j7 = rss2[1] - rss[1];
                            long j8 = rss2[2] - rss[2];
                            long j9 = rss2[3] - rss[3];
                            int i9 = C03975.f1119x5cb38e25[processCachedOptimizerRecord.getReqCompactProfile().ordinal()];
                            int i10 = i;
                            if (i9 == 1) {
                                i4 = i3;
                                i5 = i2;
                                perSourceAggregatedCompactStat.mSomeCompactPerformed++;
                                aggregatedProcessCompactionStats.mSomeCompactPerformed++;
                            } else if (i9 == 2) {
                                i4 = i3;
                                perSourceAggregatedCompactStat.mFullCompactPerformed++;
                                aggregatedProcessCompactionStats.mFullCompactPerformed++;
                                long j10 = -j8;
                                long j11 = usedZramMemory2 - usedZramMemory;
                                long j12 = j10 - j11;
                                long j13 = j5 / 1000000;
                                long j14 = rss[2];
                                if (j10 <= 0) {
                                    j10 = 0;
                                }
                                long j15 = j11 > 0 ? j11 : 0L;
                                long j16 = j12 > 0 ? j12 : 0L;
                                aggregatedProcessCompactionStats.addMemStats(j10, j15, j16, j14, j13);
                                perSourceAggregatedCompactStat.addMemStats(j10, j15, j16, j14, j13);
                                i5 = i2;
                                SingleCompactionStats singleCompactionStats = new SingleCompactionStats(rss2, reqCompactSource, str, j10, j15, j16, j14, j13, i4, i5, lastOomAdjChangeReason, processRecord.uid);
                                CachedAppOptimizer.this.mLastCompactionStats.remove(Integer.valueOf(i10));
                                CachedAppOptimizer.this.mLastCompactionStats.put(Integer.valueOf(i10), singleCompactionStats);
                                CachedAppOptimizer.this.mCompactionStatsHistory.add(singleCompactionStats);
                                if (!isForceCompact) {
                                    singleCompactionStats.sendStat();
                                }
                            } else {
                                Slog.wtf("ActivityManager", "Compaction: Unknown requested action");
                                Trace.traceEnd(64L);
                                return;
                            }
                            Object[] objArr = new Object[18];
                            objArr[c] = Integer.valueOf(i10);
                            objArr[1] = str;
                            objArr[2] = resolveCompactActionForProfile.name();
                            objArr[3] = Long.valueOf(rss[c]);
                            objArr[4] = Long.valueOf(rss[1]);
                            objArr[5] = Long.valueOf(rss[2]);
                            objArr[6] = Long.valueOf(rss[3]);
                            objArr[7] = Long.valueOf(j6);
                            objArr[8] = Long.valueOf(j7);
                            objArr[9] = Long.valueOf(j8);
                            objArr[10] = Long.valueOf(j9);
                            objArr[11] = Long.valueOf(j4);
                            objArr[12] = lastCompactProfile.name();
                            objArr[13] = Long.valueOf(lastCompactTime);
                            objArr[14] = Integer.valueOf(i5);
                            objArr[15] = Integer.valueOf(i4);
                            objArr[16] = Long.valueOf(usedZramMemory);
                            objArr[17] = Long.valueOf(usedZramMemory - usedZramMemory2);
                            EventLog.writeEvent(30063, objArr);
                            synchronized (CachedAppOptimizer.this.mProcLock) {
                                try {
                                    ActivityManagerService.boostPriorityForProcLockedSection();
                                    ProcessCachedOptimizerRecord processCachedOptimizerRecord3 = processCachedOptimizerRecord;
                                    processCachedOptimizerRecord3.setLastCompactTime(uptimeMillis2);
                                    processCachedOptimizerRecord3.setLastCompactProfile(reqCompactProfile);
                                } catch (Throwable th) {
                                    throw th;
                                }
                            }
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            j3 = 64;
                            Trace.traceEnd(j3);
                        } catch (Throwable th2) {
                            th = th2;
                            j2 = 64;
                            Trace.traceEnd(j2);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        j2 = 64;
                    }
                } finally {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                }
            }
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$FreezeHandler */
    /* loaded from: classes.dex */
    public final class FreezeHandler extends Handler implements ProcLocksReader.ProcLocksReaderCallback {
        public final int getUnfreezeReasonCode(int i) {
            switch (i) {
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                case 5:
                    return 5;
                case 6:
                    return 6;
                case 7:
                    return 7;
                case 8:
                    return 8;
                case 9:
                    return 9;
                case 10:
                    return 10;
                case 11:
                    return 11;
                case 12:
                    return 12;
                default:
                    return 0;
            }
        }

        public FreezeHandler() {
            super(CachedAppOptimizer.this.mCachedAppOptimizerThread.getLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 3) {
                if (i != 4) {
                    return;
                }
                int i2 = message.arg1;
                int i3 = message.arg2;
                Pair pair = (Pair) message.obj;
                reportUnfreeze(i2, i3, (String) pair.first, ((Integer) pair.second).intValue());
                return;
            }
            ProcessRecord processRecord = (ProcessRecord) message.obj;
            int pid = processRecord.getPid();
            String str = processRecord.processName;
            synchronized (CachedAppOptimizer.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    freezeProcess(processRecord);
                } finally {
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            try {
                CachedAppOptimizer.this.mProcLocksReader.handleBlockingFileLocks(this);
            } catch (Exception e) {
                Slog.e("ActivityManager", "Unable to check file locks for " + str + "(" + pid + "): " + e);
                synchronized (CachedAppOptimizer.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        synchronized (CachedAppOptimizer.this.mProcLock) {
                            ActivityManagerService.boostPriorityForProcLockedSection();
                            CachedAppOptimizer.this.unfreezeAppLSP(processRecord, 0);
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        }
                    } finally {
                    }
                }
            }
        }

        @GuardedBy({"mAm", "mProcLock"})
        public final void rescheduleFreeze(ProcessRecord processRecord, String str) {
            Slog.d("ActivityManager", "Reschedule freeze for process " + processRecord.getPid() + " " + processRecord.processName + " (" + str + ")");
            CachedAppOptimizer.this.unfreezeAppLSP(processRecord, 0);
            CachedAppOptimizer.this.freezeAppAsyncLSP(processRecord);
        }

        @GuardedBy({"mAm"})
        public final void freezeProcess(final ProcessRecord processRecord) {
            processRecord.getPid();
            String str = processRecord.processName;
            ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
            processCachedOptimizerRecord.setPendingFreeze(false);
            synchronized (CachedAppOptimizer.this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    int pid = processRecord.getPid();
                    if (processRecord.mState.getCurAdj() >= 900 && !processCachedOptimizerRecord.shouldNotFreeze()) {
                        if (CachedAppOptimizer.this.mFreezerOverride) {
                            processCachedOptimizerRecord.setFreezerOverride(true);
                            Slog.d("ActivityManager", "Skipping freeze for process " + pid + " " + str + " curAdj = " + processRecord.mState.getCurAdj() + "(override)");
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            return;
                        }
                        if (pid != 0 && !processCachedOptimizerRecord.isFrozen()) {
                            Slog.d("ActivityManager", "freezing " + pid + " " + str);
                            try {
                                if (CachedAppOptimizer.freezeBinder(pid, true, 100) != 0) {
                                    rescheduleFreeze(processRecord, "outstanding txns");
                                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                                    return;
                                }
                            } catch (RuntimeException unused) {
                                Slog.e("ActivityManager", "Unable to freeze binder for " + pid + " " + str);
                                CachedAppOptimizer.this.mFreezeHandler.post(new Runnable() { // from class: com.android.server.am.CachedAppOptimizer$FreezeHandler$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        CachedAppOptimizer.FreezeHandler.this.lambda$freezeProcess$0(processRecord);
                                    }
                                });
                            }
                            long freezeUnfreezeTime = processCachedOptimizerRecord.getFreezeUnfreezeTime();
                            try {
                                CachedAppOptimizer.traceAppFreeze(processRecord.processName, pid, true);
                                Process.setProcessFrozen(pid, processRecord.uid, true);
                                processCachedOptimizerRecord.setFreezeUnfreezeTime(SystemClock.uptimeMillis());
                                processCachedOptimizerRecord.setFrozen(true);
                                processCachedOptimizerRecord.setHasCollectedFrozenPSS(false);
                                CachedAppOptimizer.this.mFrozenProcesses.put(pid, processRecord);
                            } catch (Exception unused2) {
                                Slog.w("ActivityManager", "Unable to freeze " + pid + " " + str);
                            }
                            long freezeUnfreezeTime2 = processCachedOptimizerRecord.getFreezeUnfreezeTime() - freezeUnfreezeTime;
                            boolean isFrozen = processCachedOptimizerRecord.isFrozen();
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            if (isFrozen) {
                                EventLog.writeEvent(30068, Integer.valueOf(pid), str);
                                if (CachedAppOptimizer.this.mRandom.nextFloat() < CachedAppOptimizer.this.mFreezerStatsdSampleRate) {
                                    FrameworkStatsLog.write((int) FrameworkStatsLog.APP_FREEZE_CHANGED, 1, pid, str, freezeUnfreezeTime2, 0);
                                }
                                try {
                                    if ((CachedAppOptimizer.getBinderFreezeInfo(pid) & 4) != 0) {
                                        synchronized (CachedAppOptimizer.this.mProcLock) {
                                            ActivityManagerService.boostPriorityForProcLockedSection();
                                            rescheduleFreeze(processRecord, "new pending txns");
                                        }
                                        return;
                                    }
                                    return;
                                } catch (RuntimeException unused3) {
                                    Slog.e("ActivityManager", "Unable to freeze binder for " + pid + " " + str);
                                    CachedAppOptimizer.this.mFreezeHandler.post(new Runnable() { // from class: com.android.server.am.CachedAppOptimizer$FreezeHandler$$ExternalSyntheticLambda1
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            CachedAppOptimizer.FreezeHandler.this.lambda$freezeProcess$1(processRecord);
                                        }
                                    });
                                    return;
                                }
                            }
                            return;
                        }
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        return;
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                } finally {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$freezeProcess$0(ProcessRecord processRecord) {
            synchronized (CachedAppOptimizer.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    processRecord.killLocked("Unable to freeze binder interface", 14, 19, true);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$freezeProcess$1(ProcessRecord processRecord) {
            synchronized (CachedAppOptimizer.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    processRecord.killLocked("Unable to freeze binder interface", 14, 19, true);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public final void reportUnfreeze(int i, int i2, String str, int i3) {
            EventLog.writeEvent(30069, Integer.valueOf(i), str);
            if (CachedAppOptimizer.this.mRandom.nextFloat() < CachedAppOptimizer.this.mFreezerStatsdSampleRate) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.APP_FREEZE_CHANGED, 2, i, str, i2, getUnfreezeReasonCode(i3));
            }
        }

        @GuardedBy({"mAm"})
        public void onBlockingFileLock(IntArray intArray) {
            ProcessRecord processRecord;
            synchronized (CachedAppOptimizer.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    synchronized (CachedAppOptimizer.this.mProcLock) {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        int i = intArray.get(0);
                        ProcessRecord processRecord2 = (ProcessRecord) CachedAppOptimizer.this.mFrozenProcesses.get(i);
                        if (processRecord2 != null) {
                            int i2 = 1;
                            while (true) {
                                if (i2 >= intArray.size()) {
                                    break;
                                }
                                int i3 = intArray.get(i2);
                                synchronized (CachedAppOptimizer.this.mAm.mPidsSelfLocked) {
                                    processRecord = CachedAppOptimizer.this.mAm.mPidsSelfLocked.get(i3);
                                }
                                if (processRecord != null && processRecord.mState.getCurAdj() < 900) {
                                    Slog.d("ActivityManager", processRecord2.processName + " (" + i + ") blocks " + processRecord.processName + " (" + i3 + ")");
                                    CachedAppOptimizer.this.unfreezeAppLSP(processRecord2, 0);
                                    break;
                                }
                                i2++;
                            }
                        }
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.am.CachedAppOptimizer$DefaultProcessDependencies */
    /* loaded from: classes.dex */
    public static final class DefaultProcessDependencies implements ProcessDependencies {
        public static volatile int mPidCompacting = -1;

        public DefaultProcessDependencies() {
        }

        @Override // com.android.server.p006am.CachedAppOptimizer.ProcessDependencies
        public long[] getRss(int i) {
            return Process.getRss(i);
        }

        @Override // com.android.server.p006am.CachedAppOptimizer.ProcessDependencies
        public void performCompaction(CompactAction compactAction, int i) throws IOException {
            mPidCompacting = i;
            if (compactAction == CompactAction.ALL) {
                CachedAppOptimizer.compactProcess(i, 3);
            } else if (compactAction == CompactAction.FILE) {
                CachedAppOptimizer.compactProcess(i, 1);
            } else if (compactAction == CompactAction.ANON) {
                CachedAppOptimizer.compactProcess(i, 2);
            }
            mPidCompacting = -1;
        }
    }
}
