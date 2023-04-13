package com.android.internal.p028os;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Process;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.KeyValueListParser;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.p028os.BinderCallsStats;
import com.android.internal.p028os.BinderInternal;
import com.android.internal.p028os.BinderLatencyObserver;
import com.android.internal.p028os.CachedDeviceState;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
/* renamed from: com.android.internal.os.BinderCallsStats */
/* loaded from: classes4.dex */
public class BinderCallsStats implements BinderInternal.Observer {
    private static final int CALL_SESSIONS_POOL_SIZE = 100;
    private static final int CALL_STATS_OBSERVER_DEBOUNCE_MILLIS = 5000;
    private static final String DEBUG_ENTRY_PREFIX = "__DEBUG_";
    public static final boolean DEFAULT_COLLECT_LATENCY_DATA = true;
    public static final boolean DEFAULT_IGNORE_BATTERY_STATUS = false;
    public static final boolean DEFAULT_TRACK_DIRECT_CALLING_UID = true;
    public static final boolean DEFAULT_TRACK_SCREEN_INTERACTIVE = false;
    public static final boolean DETAILED_TRACKING_DEFAULT = true;
    public static final boolean ENABLED_DEFAULT = true;
    private static final String EXCEPTION_COUNT_OVERFLOW_NAME = "overflow";
    public static final int MAX_BINDER_CALL_STATS_COUNT_DEFAULT = 1500;
    private static final int MAX_EXCEPTION_COUNT_SIZE = 50;
    private static final Class<? extends Binder> OVERFLOW_BINDER = OverflowBinder.class;
    private static final int OVERFLOW_DIRECT_CALLING_UID = -1;
    private static final boolean OVERFLOW_SCREEN_INTERACTIVE = false;
    private static final int OVERFLOW_TRANSACTION_CODE = -1;
    public static final int PERIODIC_SAMPLING_INTERVAL_DEFAULT = 1000;
    public static final int SHARDING_MODULO_DEFAULT = 1;
    private static final String TAG = "BinderCallsStats";
    private boolean mAddDebugEntries;
    private CachedDeviceState.TimeInStateStopwatch mBatteryStopwatch;
    private final Queue<BinderInternal.CallSession> mCallSessionsPool;
    private long mCallStatsCount;
    private BinderInternal.CallStatsObserver mCallStatsObserver;
    private final Handler mCallStatsObserverHandler;
    private Runnable mCallStatsObserverRunnable;
    private boolean mCollectLatencyData;
    private boolean mDetailedTracking;
    private CachedDeviceState.Readonly mDeviceState;
    private final ArrayMap<String, Integer> mExceptionCounts;
    private boolean mIgnoreBatteryStatus;
    private BinderLatencyObserver mLatencyObserver;
    private final Object mLock;
    private int mMaxBinderCallStatsCount;
    private volatile IntArray mNativeTids;
    private final Object mNativeTidsLock;
    private int mPeriodicSamplingInterval;
    private final Random mRandom;
    private boolean mRecordingAllTransactionsForUid;
    private ArraySet<Integer> mSendUidsToObserver;
    private int mShardingModulo;
    private int mShardingOffset;
    private long mStartCurrentTime;
    private long mStartElapsedTime;
    private boolean mTrackDirectCallingUid;
    private boolean mTrackScreenInteractive;
    private final SparseArray<UidEntry> mUidEntries;

    /* renamed from: com.android.internal.os.BinderCallsStats$ExportedCallStat */
    /* loaded from: classes4.dex */
    public static class ExportedCallStat {
        Class<? extends Binder> binderClass;
        public long callCount;
        public int callingUid;
        public String className;
        public long cpuTimeMicros;
        public long exceptionCount;
        public long latencyMicros;
        public long maxCpuTimeMicros;
        public long maxLatencyMicros;
        public long maxReplySizeBytes;
        public long maxRequestSizeBytes;
        public String methodName;
        public long recordedCallCount;
        public boolean screenInteractive;
        int transactionCode;
        public int workSourceUid;
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$OverflowBinder */
    /* loaded from: classes4.dex */
    private static class OverflowBinder extends Binder {
        private OverflowBinder() {
        }
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$Injector */
    /* loaded from: classes4.dex */
    public static class Injector {
        public Random getRandomGenerator() {
            return new Random();
        }

        public Handler getHandler() {
            return new Handler(Looper.getMainLooper());
        }

        public BinderLatencyObserver getLatencyObserver(int processSource) {
            return new BinderLatencyObserver(new BinderLatencyObserver.Injector(), processSource);
        }
    }

    public BinderCallsStats(Injector injector) {
        this(injector, 1);
    }

    public BinderCallsStats(Injector injector, int processSource) {
        this.mDetailedTracking = true;
        this.mPeriodicSamplingInterval = 1000;
        this.mMaxBinderCallStatsCount = 1500;
        this.mUidEntries = new SparseArray<>();
        this.mExceptionCounts = new ArrayMap<>();
        this.mCallSessionsPool = new ConcurrentLinkedQueue();
        this.mLock = new Object();
        this.mStartCurrentTime = System.currentTimeMillis();
        this.mStartElapsedTime = SystemClock.elapsedRealtime();
        this.mCallStatsCount = 0L;
        this.mAddDebugEntries = false;
        this.mTrackDirectCallingUid = true;
        this.mTrackScreenInteractive = false;
        this.mIgnoreBatteryStatus = false;
        this.mCollectLatencyData = true;
        this.mShardingModulo = 1;
        this.mSendUidsToObserver = new ArraySet<>(32);
        this.mCallStatsObserverRunnable = new Runnable() { // from class: com.android.internal.os.BinderCallsStats.1
            @Override // java.lang.Runnable
            public void run() {
                if (BinderCallsStats.this.mCallStatsObserver == null) {
                    return;
                }
                BinderCallsStats.this.noteCallsStatsDelayed();
                synchronized (BinderCallsStats.this.mLock) {
                    int size = BinderCallsStats.this.mSendUidsToObserver.size();
                    for (int i = 0; i < size; i++) {
                        UidEntry uidEntry = (UidEntry) BinderCallsStats.this.mUidEntries.get(((Integer) BinderCallsStats.this.mSendUidsToObserver.valueAt(i)).intValue());
                        if (uidEntry != null) {
                            ArrayMap<CallStatKey, CallStat> callStats = uidEntry.mCallStats;
                            int csize = callStats.size();
                            ArrayList<CallStat> tmpCallStats = new ArrayList<>(csize);
                            for (int j = 0; j < csize; j++) {
                                tmpCallStats.add(callStats.valueAt(j).m6863clone());
                            }
                            BinderCallsStats.this.mCallStatsObserver.noteCallStats(uidEntry.workSourceUid, uidEntry.incrementalCallCount, tmpCallStats);
                            uidEntry.incrementalCallCount = 0L;
                            for (int j2 = callStats.size() - 1; j2 >= 0; j2--) {
                                callStats.valueAt(j2).incrementalCallCount = 0L;
                            }
                        }
                    }
                    BinderCallsStats.this.mSendUidsToObserver.clear();
                }
            }
        };
        this.mNativeTidsLock = new Object();
        this.mNativeTids = new IntArray(0);
        Random randomGenerator = injector.getRandomGenerator();
        this.mRandom = randomGenerator;
        this.mCallStatsObserverHandler = injector.getHandler();
        this.mLatencyObserver = injector.getLatencyObserver(processSource);
        this.mShardingOffset = randomGenerator.nextInt(this.mShardingModulo);
    }

    public void setDeviceState(CachedDeviceState.Readonly deviceState) {
        CachedDeviceState.TimeInStateStopwatch timeInStateStopwatch = this.mBatteryStopwatch;
        if (timeInStateStopwatch != null) {
            timeInStateStopwatch.close();
        }
        this.mDeviceState = deviceState;
        this.mBatteryStopwatch = deviceState.createTimeOnBatteryStopwatch();
    }

    public void setCallStatsObserver(BinderInternal.CallStatsObserver callStatsObserver) {
        this.mCallStatsObserver = callStatsObserver;
        noteBinderThreadNativeIds();
        noteCallsStatsDelayed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteCallsStatsDelayed() {
        this.mCallStatsObserverHandler.removeCallbacks(this.mCallStatsObserverRunnable);
        if (this.mCallStatsObserver != null) {
            this.mCallStatsObserverHandler.postDelayed(this.mCallStatsObserverRunnable, 5000L);
        }
    }

    @Override // com.android.internal.p028os.BinderInternal.Observer
    public BinderInternal.CallSession callStarted(Binder binder, int code, int workSourceUid) {
        noteNativeThreadId();
        boolean collectCpu = canCollect();
        if (!this.mCollectLatencyData && !collectCpu) {
            return null;
        }
        BinderInternal.CallSession s = obtainCallSession();
        s.binderClass = binder.getClass();
        s.transactionCode = code;
        s.exceptionThrown = false;
        s.cpuTimeStarted = -1L;
        s.timeStarted = -1L;
        s.recordedCall = shouldRecordDetailedData();
        if (collectCpu && (this.mRecordingAllTransactionsForUid || s.recordedCall)) {
            s.cpuTimeStarted = getThreadTimeMicro();
            s.timeStarted = getElapsedRealtimeMicro();
        } else if (this.mCollectLatencyData) {
            s.timeStarted = getElapsedRealtimeMicro();
        }
        return s;
    }

    private BinderInternal.CallSession obtainCallSession() {
        BinderInternal.CallSession s = this.mCallSessionsPool.poll();
        return s == null ? new BinderInternal.CallSession() : s;
    }

    @Override // com.android.internal.p028os.BinderInternal.Observer
    public void callEnded(BinderInternal.CallSession s, int parcelRequestSize, int parcelReplySize, int workSourceUid) {
        if (s == null) {
            return;
        }
        processCallEnded(s, parcelRequestSize, parcelReplySize, workSourceUid);
        if (this.mCallSessionsPool.size() < 100) {
            this.mCallSessionsPool.add(s);
        }
    }

    private void processCallEnded(BinderInternal.CallSession s, int parcelRequestSize, int parcelReplySize, int workSourceUid) {
        boolean recordCall;
        UidEntry uidEntry;
        long duration;
        long latencyDuration;
        Object obj;
        if (this.mCollectLatencyData) {
            this.mLatencyObserver.callEnded(s);
        }
        if (canCollect()) {
            if (s.recordedCall) {
                recordCall = true;
                uidEntry = null;
            } else {
                boolean recordCall2 = this.mRecordingAllTransactionsForUid;
                if (recordCall2) {
                    UidEntry uidEntry2 = getUidEntry(workSourceUid);
                    recordCall = uidEntry2.recordAllTransactions;
                    uidEntry = uidEntry2;
                } else {
                    recordCall = false;
                    uidEntry = null;
                }
            }
            if (recordCall) {
                duration = getThreadTimeMicro() - s.cpuTimeStarted;
                latencyDuration = getElapsedRealtimeMicro() - s.timeStarted;
            } else {
                duration = 0;
                latencyDuration = 0;
            }
            boolean screenInteractive = this.mTrackScreenInteractive ? this.mDeviceState.isScreenInteractive() : false;
            int callingUid = this.mTrackDirectCallingUid ? getCallingUid() : -1;
            Object obj2 = this.mLock;
            synchronized (obj2) {
                try {
                    try {
                        try {
                            if (canCollect()) {
                                if (uidEntry == null) {
                                    uidEntry = getUidEntry(workSourceUid);
                                }
                                long j = 1;
                                uidEntry.callCount++;
                                uidEntry.incrementalCallCount++;
                                if (recordCall) {
                                    try {
                                        uidEntry.cpuTimeMicros += duration;
                                        uidEntry.recordedCallCount++;
                                        obj = obj2;
                                        try {
                                            CallStat callStat = uidEntry.getOrCreate(callingUid, s.binderClass, s.transactionCode, screenInteractive, this.mCallStatsCount >= ((long) this.mMaxBinderCallStatsCount));
                                            boolean isNewCallStat = callStat.callCount == 0;
                                            if (isNewCallStat) {
                                                try {
                                                    this.mCallStatsCount++;
                                                } catch (Throwable th) {
                                                    th = th;
                                                    throw th;
                                                }
                                            }
                                            callStat.callCount++;
                                            callStat.incrementalCallCount++;
                                            callStat.recordedCallCount++;
                                            callStat.cpuTimeMicros += duration;
                                            callStat.maxCpuTimeMicros = Math.max(callStat.maxCpuTimeMicros, duration);
                                            callStat.latencyMicros += latencyDuration;
                                            callStat.maxLatencyMicros = Math.max(callStat.maxLatencyMicros, latencyDuration);
                                            if (this.mDetailedTracking) {
                                                long j2 = callStat.exceptionCount;
                                                if (!s.exceptionThrown) {
                                                    j = 0;
                                                }
                                                callStat.exceptionCount = j2 + j;
                                                try {
                                                    callStat.maxRequestSizeBytes = Math.max(callStat.maxRequestSizeBytes, parcelRequestSize);
                                                    try {
                                                        callStat.maxReplySizeBytes = Math.max(callStat.maxReplySizeBytes, parcelReplySize);
                                                    } catch (Throwable th2) {
                                                        th = th2;
                                                        throw th;
                                                    }
                                                } catch (Throwable th3) {
                                                    th = th3;
                                                    throw th;
                                                }
                                            }
                                        } catch (Throwable th4) {
                                            th = th4;
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                        obj = obj2;
                                    }
                                } else {
                                    obj = obj2;
                                    try {
                                        CallStat callStat2 = uidEntry.get(callingUid, s.binderClass, s.transactionCode, screenInteractive);
                                        if (callStat2 != null) {
                                            callStat2.callCount++;
                                            callStat2.incrementalCallCount++;
                                        }
                                    } catch (Throwable th6) {
                                        th = th6;
                                        throw th;
                                    }
                                }
                                if (this.mCallStatsObserver != null && !UserHandle.isCore(workSourceUid)) {
                                    this.mSendUidsToObserver.add(Integer.valueOf(workSourceUid));
                                }
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            obj = obj2;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                    }
                } catch (Throwable th9) {
                    th = th9;
                    obj = obj2;
                }
            }
        }
    }

    private boolean shouldExport(ExportedCallStat e, boolean applySharding) {
        if (!applySharding) {
            return true;
        }
        int hash = e.binderClass.hashCode();
        int hash2 = ((((hash * 31) + e.transactionCode) * 31) + e.callingUid) * 31;
        int i = e.screenInteractive ? MetricsProto.MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP : MetricsProto.MetricsEvent.ANOMALY_TYPE_UNOPTIMIZED_BT;
        int hash3 = this.mShardingOffset;
        return (hash3 + (hash2 + i)) % this.mShardingModulo == 0;
    }

    private UidEntry getUidEntry(int uid) {
        UidEntry uidEntry = this.mUidEntries.get(uid);
        if (uidEntry == null) {
            UidEntry uidEntry2 = new UidEntry(uid);
            this.mUidEntries.put(uid, uidEntry2);
            return uidEntry2;
        }
        return uidEntry;
    }

    @Override // com.android.internal.p028os.BinderInternal.Observer
    public void callThrewException(BinderInternal.CallSession s, Exception exception) {
        if (s == null) {
            return;
        }
        int i = 1;
        s.exceptionThrown = true;
        try {
            String className = exception.getClass().getName();
            synchronized (this.mLock) {
                if (this.mExceptionCounts.size() >= 50) {
                    className = EXCEPTION_COUNT_OVERFLOW_NAME;
                }
                Integer count = this.mExceptionCounts.get(className);
                ArrayMap<String, Integer> arrayMap = this.mExceptionCounts;
                if (count != null) {
                    i = 1 + count.intValue();
                }
                arrayMap.put(className, Integer.valueOf(i));
            }
        } catch (RuntimeException e) {
            Slog.wtf(TAG, "Unexpected exception while updating mExceptionCounts");
        }
    }

    private void noteNativeThreadId() {
        int tid = getNativeTid();
        if (this.mNativeTids.binarySearch(tid) >= 0) {
            return;
        }
        synchronized (this.mNativeTidsLock) {
            IntArray nativeTids = this.mNativeTids;
            int index = nativeTids.binarySearch(tid);
            if (index < 0) {
                IntArray copyOnWriteArray = new IntArray(nativeTids.size() + 1);
                copyOnWriteArray.addAll(nativeTids);
                copyOnWriteArray.add((-index) - 1, tid);
                this.mNativeTids = copyOnWriteArray;
            }
        }
        noteBinderThreadNativeIds();
    }

    private void noteBinderThreadNativeIds() {
        BinderInternal.CallStatsObserver callStatsObserver = this.mCallStatsObserver;
        if (callStatsObserver == null) {
            return;
        }
        callStatsObserver.noteBinderThreadNativeIds(getNativeTids());
    }

    private boolean canCollect() {
        if (this.mRecordingAllTransactionsForUid || this.mIgnoreBatteryStatus) {
            return true;
        }
        CachedDeviceState.Readonly readonly = this.mDeviceState;
        return (readonly == null || readonly.isCharging()) ? false : true;
    }

    public ArrayList<ExportedCallStat> getExportedCallStats() {
        return getExportedCallStats(false);
    }

    public ArrayList<ExportedCallStat> getExportedCallStats(boolean applySharding) {
        if (!this.mDetailedTracking) {
            return new ArrayList<>();
        }
        ArrayList<ExportedCallStat> resultCallStats = new ArrayList<>();
        synchronized (this.mLock) {
            int uidEntriesSize = this.mUidEntries.size();
            for (int entryIdx = 0; entryIdx < uidEntriesSize; entryIdx++) {
                UidEntry entry = this.mUidEntries.valueAt(entryIdx);
                for (CallStat stat : entry.getCallStatsList()) {
                    ExportedCallStat e = getExportedCallStat(entry.workSourceUid, stat);
                    if (shouldExport(e, applySharding)) {
                        resultCallStats.add(e);
                    }
                }
            }
        }
        resolveBinderMethodNames(resultCallStats);
        if (this.mAddDebugEntries && this.mBatteryStopwatch != null) {
            resultCallStats.add(createDebugEntry("start_time_millis", this.mStartElapsedTime));
            resultCallStats.add(createDebugEntry("end_time_millis", SystemClock.elapsedRealtime()));
            resultCallStats.add(createDebugEntry("battery_time_millis", this.mBatteryStopwatch.getMillis()));
            resultCallStats.add(createDebugEntry(SettingsObserver.SETTINGS_SAMPLING_INTERVAL_KEY, this.mPeriodicSamplingInterval));
            resultCallStats.add(createDebugEntry(SettingsObserver.SETTINGS_SHARDING_MODULO_KEY, this.mShardingModulo));
        }
        return resultCallStats;
    }

    public ArrayList<ExportedCallStat> getExportedCallStats(int workSourceUid) {
        return getExportedCallStats(workSourceUid, false);
    }

    public ArrayList<ExportedCallStat> getExportedCallStats(int workSourceUid, boolean applySharding) {
        ArrayList<ExportedCallStat> resultCallStats = new ArrayList<>();
        synchronized (this.mLock) {
            UidEntry entry = getUidEntry(workSourceUid);
            for (CallStat stat : entry.getCallStatsList()) {
                ExportedCallStat e = getExportedCallStat(workSourceUid, stat);
                if (shouldExport(e, applySharding)) {
                    resultCallStats.add(e);
                }
            }
        }
        resolveBinderMethodNames(resultCallStats);
        return resultCallStats;
    }

    private ExportedCallStat getExportedCallStat(int workSourceUid, CallStat stat) {
        ExportedCallStat exported = new ExportedCallStat();
        exported.workSourceUid = workSourceUid;
        exported.callingUid = stat.callingUid;
        exported.className = stat.binderClass.getName();
        exported.binderClass = stat.binderClass;
        exported.transactionCode = stat.transactionCode;
        exported.screenInteractive = stat.screenInteractive;
        exported.cpuTimeMicros = stat.cpuTimeMicros;
        exported.maxCpuTimeMicros = stat.maxCpuTimeMicros;
        exported.latencyMicros = stat.latencyMicros;
        exported.maxLatencyMicros = stat.maxLatencyMicros;
        exported.recordedCallCount = stat.recordedCallCount;
        exported.callCount = stat.callCount;
        exported.maxRequestSizeBytes = stat.maxRequestSizeBytes;
        exported.maxReplySizeBytes = stat.maxReplySizeBytes;
        exported.exceptionCount = stat.exceptionCount;
        return exported;
    }

    private void resolveBinderMethodNames(ArrayList<ExportedCallStat> resultCallStats) {
        String methodName;
        ExportedCallStat previous = null;
        String previousMethodName = null;
        resultCallStats.sort(new Comparator() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda5
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compareByBinderClassAndCode;
                compareByBinderClassAndCode = BinderCallsStats.compareByBinderClassAndCode((BinderCallsStats.ExportedCallStat) obj, (BinderCallsStats.ExportedCallStat) obj2);
                return compareByBinderClassAndCode;
            }
        });
        BinderTransactionNameResolver resolver = new BinderTransactionNameResolver();
        Iterator<ExportedCallStat> it = resultCallStats.iterator();
        while (it.hasNext()) {
            ExportedCallStat exported = it.next();
            boolean isCodeDifferent = false;
            boolean isClassDifferent = previous == null || !previous.className.equals(exported.className);
            if (previous == null || previous.transactionCode != exported.transactionCode) {
                isCodeDifferent = true;
            }
            if (isClassDifferent || isCodeDifferent) {
                methodName = resolver.getMethodName(exported.binderClass, exported.transactionCode);
            } else {
                methodName = previousMethodName;
            }
            previousMethodName = methodName;
            exported.methodName = methodName;
            previous = exported;
        }
    }

    private ExportedCallStat createDebugEntry(String variableName, long value) {
        int uid = Process.myUid();
        ExportedCallStat callStat = new ExportedCallStat();
        callStat.className = "";
        callStat.workSourceUid = uid;
        callStat.callingUid = uid;
        callStat.recordedCallCount = 1L;
        callStat.callCount = 1L;
        callStat.methodName = "__DEBUG_" + variableName;
        callStat.latencyMicros = value;
        return callStat;
    }

    public ArrayMap<String, Integer> getExportedExceptionStats() {
        ArrayMap<String, Integer> arrayMap;
        synchronized (this.mLock) {
            arrayMap = new ArrayMap<>(this.mExceptionCounts);
        }
        return arrayMap;
    }

    public void dump(PrintWriter pw, AppIdToPackageMap packageMap, int workSourceUid, boolean verbose) {
        synchronized (this.mLock) {
            dumpLocked(pw, packageMap, workSourceUid, verbose);
        }
    }

    private void dumpLocked(PrintWriter pw, AppIdToPackageMap packageMap, int workSourceUid, boolean verbose) {
        boolean verbose2;
        List<ExportedCallStat> exportedCallStats;
        List<ExportedCallStat> list;
        long totalRecordedCallsCount;
        long totalCpuTime;
        if (workSourceUid == -1) {
            verbose2 = verbose;
        } else {
            verbose2 = true;
        }
        pw.print("Start time: ");
        pw.println(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mStartCurrentTime));
        pw.print("On battery time (ms): ");
        CachedDeviceState.TimeInStateStopwatch timeInStateStopwatch = this.mBatteryStopwatch;
        pw.println(timeInStateStopwatch != null ? timeInStateStopwatch.getMillis() : 0L);
        pw.println("Sampling interval period: " + this.mPeriodicSamplingInterval);
        pw.println("Sharding modulo: " + this.mShardingModulo);
        String str = "";
        String datasetSizeDesc = verbose2 ? "" : "(top 90% by cpu time) ";
        StringBuilder sb = new StringBuilder();
        pw.println("Per-UID raw data " + datasetSizeDesc + "(package/uid, worksource, call_desc, screen_interactive, cpu_time_micros, max_cpu_time_micros, latency_time_micros, max_latency_time_micros, exception_count, max_request_size_bytes, max_reply_size_bytes, recorded_call_count, call_count):");
        if (workSourceUid != -1) {
            exportedCallStats = getExportedCallStats(workSourceUid, true);
        } else {
            exportedCallStats = getExportedCallStats(true);
        }
        exportedCallStats.sort(new Comparator() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compareByCpuDesc;
                compareByCpuDesc = BinderCallsStats.compareByCpuDesc((BinderCallsStats.ExportedCallStat) obj, (BinderCallsStats.ExportedCallStat) obj2);
                return compareByCpuDesc;
            }
        });
        Iterator<ExportedCallStat> it = exportedCallStats.iterator();
        while (it.hasNext()) {
            ExportedCallStat e = it.next();
            if (e.methodName == null || !e.methodName.startsWith("__DEBUG_")) {
                sb.setLength(0);
                List<ExportedCallStat> exportedCallStats2 = exportedCallStats;
                boolean verbose3 = verbose2;
                Iterator<ExportedCallStat> it2 = it;
                sb.append("    ").append(packageMap.mapUid(e.callingUid)).append(',').append(packageMap.mapUid(e.workSourceUid)).append(',').append(e.className).append('#').append(e.methodName).append(',').append(e.screenInteractive).append(',').append(e.cpuTimeMicros).append(',').append(e.maxCpuTimeMicros).append(',').append(e.latencyMicros).append(',').append(e.maxLatencyMicros).append(',').append(this.mDetailedTracking ? e.exceptionCount : 95L).append(',').append(this.mDetailedTracking ? e.maxRequestSizeBytes : 95L).append(',').append(this.mDetailedTracking ? e.maxReplySizeBytes : 95L).append(',').append(e.recordedCallCount).append(',').append(e.callCount);
                pw.println(sb);
                it = it2;
                verbose2 = verbose3;
                exportedCallStats = exportedCallStats2;
            }
        }
        boolean verbose4 = verbose2;
        List<ExportedCallStat> list2 = exportedCallStats;
        pw.println();
        List<UidEntry> entries = new ArrayList<>();
        long totalCallsCount = 0;
        long totalRecordedCallsCount2 = 0;
        long totalCpuTime2 = 0;
        if (workSourceUid == -1) {
            int uidEntriesSize = this.mUidEntries.size();
            for (int i = 0; i < uidEntriesSize; i++) {
                UidEntry e2 = this.mUidEntries.valueAt(i);
                entries.add(e2);
                totalCpuTime2 += e2.cpuTimeMicros;
                totalRecordedCallsCount2 += e2.recordedCallCount;
                totalCallsCount += e2.callCount;
            }
            entries.sort(Comparator.comparingDouble(new ToDoubleFunction() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda1
                @Override // java.util.function.ToDoubleFunction
                public final double applyAsDouble(Object obj) {
                    double d;
                    BinderCallsStats.UidEntry uidEntry = (BinderCallsStats.UidEntry) obj;
                    return d;
                }
            }).reversed());
            list = list2;
            totalRecordedCallsCount = totalRecordedCallsCount2;
            totalCpuTime = totalCpuTime2;
        } else {
            UidEntry e3 = getUidEntry(workSourceUid);
            entries.add(e3);
            long totalCpuTime3 = 0 + e3.cpuTimeMicros;
            long totalRecordedCallsCount3 = 0 + e3.recordedCallCount;
            totalCallsCount = 0 + e3.callCount;
            list = list2;
            totalRecordedCallsCount = totalRecordedCallsCount3;
            totalCpuTime = totalCpuTime3;
        }
        pw.println("Per-UID Summary " + datasetSizeDesc + "(cpu_time, % of total cpu_time, recorded_call_count, call_count, package/uid):");
        List<UidEntry> summaryEntries = verbose4 ? entries : getHighestValues(entries, new ToDoubleFunction() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda2
            @Override // java.util.function.ToDoubleFunction
            public final double applyAsDouble(Object obj) {
                double d;
                BinderCallsStats.UidEntry uidEntry = (BinderCallsStats.UidEntry) obj;
                return d;
            }
        }, 0.9d);
        Iterator<UidEntry> it3 = summaryEntries.iterator();
        while (it3.hasNext()) {
            UidEntry entry = it3.next();
            List<UidEntry> entries2 = entries;
            String uidStr = packageMap.mapUid(entry.workSourceUid);
            pw.println(String.format("  %10d %3.0f%% %8d %8d %s", Long.valueOf(entry.cpuTimeMicros), Double.valueOf((entry.cpuTimeMicros * 100.0d) / totalCpuTime), Long.valueOf(entry.recordedCallCount), Long.valueOf(entry.callCount), uidStr));
            entries = entries2;
            str = str;
            summaryEntries = summaryEntries;
            it3 = it3;
            totalRecordedCallsCount = totalRecordedCallsCount;
        }
        String str2 = str;
        long totalRecordedCallsCount4 = totalRecordedCallsCount;
        pw.println();
        if (workSourceUid == -1) {
            pw.println(String.format("  Summary: total_cpu_time=%d, calls_count=%d, avg_call_cpu_time=%.0f", Long.valueOf(totalCpuTime), Long.valueOf(totalCallsCount), Double.valueOf(totalCpuTime / totalRecordedCallsCount4)));
            pw.println();
        }
        pw.println("Exceptions thrown (exception_count, class_name):");
        final List<Pair<String, Integer>> exceptionEntries = new ArrayList<>();
        this.mExceptionCounts.entrySet().iterator().forEachRemaining(new Consumer() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                exceptionEntries.add(Pair.create((String) r2.getKey(), (Integer) ((Map.Entry) obj).getValue()));
            }
        });
        exceptionEntries.sort(new Comparator() { // from class: com.android.internal.os.BinderCallsStats$$ExternalSyntheticLambda4
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compare;
                compare = Integer.compare(((Integer) ((Pair) obj2).second).intValue(), ((Integer) ((Pair) obj).second).intValue());
                return compare;
            }
        });
        for (Pair<String, Integer> entry2 : exceptionEntries) {
            pw.println(String.format("  %6d %s", entry2.second, entry2.first));
        }
        if (this.mPeriodicSamplingInterval != 1) {
            pw.println(str2);
            pw.println("/!\\ Displayed data is sampled. See sampling interval at the top.");
        }
    }

    protected long getThreadTimeMicro() {
        return SystemClock.currentThreadTimeMicro();
    }

    protected int getCallingUid() {
        return Binder.getCallingUid();
    }

    protected int getNativeTid() {
        return Process.myTid();
    }

    public int[] getNativeTids() {
        return this.mNativeTids.toArray();
    }

    protected long getElapsedRealtimeMicro() {
        return SystemClock.elapsedRealtimeNanos() / 1000;
    }

    protected boolean shouldRecordDetailedData() {
        return this.mRandom.nextInt(this.mPeriodicSamplingInterval) == 0;
    }

    public void setDetailedTracking(boolean enabled) {
        synchronized (this.mLock) {
            if (enabled != this.mDetailedTracking) {
                this.mDetailedTracking = enabled;
                reset();
            }
        }
    }

    public void setTrackScreenInteractive(boolean enabled) {
        synchronized (this.mLock) {
            if (enabled != this.mTrackScreenInteractive) {
                this.mTrackScreenInteractive = enabled;
                reset();
            }
        }
    }

    public void setTrackDirectCallerUid(boolean enabled) {
        synchronized (this.mLock) {
            if (enabled != this.mTrackDirectCallingUid) {
                this.mTrackDirectCallingUid = enabled;
                reset();
            }
        }
    }

    public void setIgnoreBatteryStatus(boolean ignored) {
        synchronized (this.mLock) {
            if (ignored != this.mIgnoreBatteryStatus) {
                this.mIgnoreBatteryStatus = ignored;
                reset();
            }
        }
    }

    public void recordAllCallsForWorkSourceUid(int workSourceUid) {
        setDetailedTracking(true);
        Slog.m94i(TAG, "Recording all Binder calls for UID: " + workSourceUid);
        UidEntry uidEntry = getUidEntry(workSourceUid);
        uidEntry.recordAllTransactions = true;
        this.mRecordingAllTransactionsForUid = true;
    }

    public void setAddDebugEntries(boolean addDebugEntries) {
        this.mAddDebugEntries = addDebugEntries;
    }

    public void setMaxBinderCallStats(int maxKeys) {
        if (maxKeys <= 0) {
            Slog.m90w(TAG, "Ignored invalid max value (value must be positive): " + maxKeys);
            return;
        }
        synchronized (this.mLock) {
            if (maxKeys != this.mMaxBinderCallStatsCount) {
                this.mMaxBinderCallStatsCount = maxKeys;
                reset();
            }
        }
    }

    public void setSamplingInterval(int samplingInterval) {
        if (samplingInterval <= 0) {
            Slog.m90w(TAG, "Ignored invalid sampling interval (value must be positive): " + samplingInterval);
            return;
        }
        synchronized (this.mLock) {
            if (samplingInterval != this.mPeriodicSamplingInterval) {
                this.mPeriodicSamplingInterval = samplingInterval;
                reset();
            }
        }
    }

    public void setShardingModulo(int shardingModulo) {
        if (shardingModulo <= 0) {
            Slog.m90w(TAG, "Ignored invalid sharding modulo (value must be positive): " + shardingModulo);
            return;
        }
        synchronized (this.mLock) {
            if (shardingModulo != this.mShardingModulo) {
                this.mShardingModulo = shardingModulo;
                this.mShardingOffset = this.mRandom.nextInt(shardingModulo);
                reset();
            }
        }
    }

    public void setCollectLatencyData(boolean collectLatencyData) {
        this.mCollectLatencyData = collectLatencyData;
    }

    public boolean getCollectLatencyData() {
        return this.mCollectLatencyData;
    }

    public void reset() {
        synchronized (this.mLock) {
            this.mCallStatsCount = 0L;
            this.mUidEntries.clear();
            this.mExceptionCounts.clear();
            this.mStartCurrentTime = System.currentTimeMillis();
            this.mStartElapsedTime = SystemClock.elapsedRealtime();
            CachedDeviceState.TimeInStateStopwatch timeInStateStopwatch = this.mBatteryStopwatch;
            if (timeInStateStopwatch != null) {
                timeInStateStopwatch.reset();
            }
            this.mRecordingAllTransactionsForUid = false;
        }
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$CallStat */
    /* loaded from: classes4.dex */
    public static class CallStat {
        public final Class<? extends Binder> binderClass;
        public long callCount;
        public final int callingUid;
        public long cpuTimeMicros;
        public long exceptionCount;
        public long incrementalCallCount;
        public long latencyMicros;
        public long maxCpuTimeMicros;
        public long maxLatencyMicros;
        public long maxReplySizeBytes;
        public long maxRequestSizeBytes;
        public long recordedCallCount;
        public final boolean screenInteractive;
        public final int transactionCode;

        public CallStat(int callingUid, Class<? extends Binder> binderClass, int transactionCode, boolean screenInteractive) {
            this.callingUid = callingUid;
            this.binderClass = binderClass;
            this.transactionCode = transactionCode;
            this.screenInteractive = screenInteractive;
        }

        /* renamed from: clone */
        public CallStat m6863clone() {
            CallStat clone = new CallStat(this.callingUid, this.binderClass, this.transactionCode, this.screenInteractive);
            clone.recordedCallCount = this.recordedCallCount;
            clone.callCount = this.callCount;
            clone.cpuTimeMicros = this.cpuTimeMicros;
            clone.maxCpuTimeMicros = this.maxCpuTimeMicros;
            clone.latencyMicros = this.latencyMicros;
            clone.maxLatencyMicros = this.maxLatencyMicros;
            clone.maxRequestSizeBytes = this.maxRequestSizeBytes;
            clone.maxReplySizeBytes = this.maxReplySizeBytes;
            clone.exceptionCount = this.exceptionCount;
            clone.incrementalCallCount = this.incrementalCallCount;
            return clone;
        }

        public String toString() {
            String methodName = new BinderTransactionNameResolver().getMethodName(this.binderClass, this.transactionCode);
            return "CallStat{callingUid=" + this.callingUid + ", transaction=" + this.binderClass.getSimpleName() + '.' + methodName + ", callCount=" + this.callCount + ", incrementalCallCount=" + this.incrementalCallCount + ", recordedCallCount=" + this.recordedCallCount + ", cpuTimeMicros=" + this.cpuTimeMicros + ", latencyMicros=" + this.latencyMicros + '}';
        }
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$CallStatKey */
    /* loaded from: classes4.dex */
    public static class CallStatKey {
        public Class<? extends Binder> binderClass;
        public int callingUid;
        private boolean screenInteractive;
        public int transactionCode;

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            CallStatKey key = (CallStatKey) o;
            return this.callingUid == key.callingUid && this.transactionCode == key.transactionCode && this.screenInteractive == key.screenInteractive && this.binderClass.equals(key.binderClass);
        }

        public int hashCode() {
            int result = this.binderClass.hashCode();
            return (((((result * 31) + this.transactionCode) * 31) + this.callingUid) * 31) + (this.screenInteractive ? MetricsProto.MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP : MetricsProto.MetricsEvent.ANOMALY_TYPE_UNOPTIMIZED_BT);
        }
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$UidEntry */
    /* loaded from: classes4.dex */
    public static class UidEntry {
        public long callCount;
        public long cpuTimeMicros;
        public long incrementalCallCount;
        private ArrayMap<CallStatKey, CallStat> mCallStats = new ArrayMap<>();
        private CallStatKey mTempKey = new CallStatKey();
        public boolean recordAllTransactions;
        public long recordedCallCount;
        public int workSourceUid;

        UidEntry(int uid) {
            this.workSourceUid = uid;
        }

        CallStat get(int callingUid, Class<? extends Binder> binderClass, int transactionCode, boolean screenInteractive) {
            this.mTempKey.callingUid = callingUid;
            this.mTempKey.binderClass = binderClass;
            this.mTempKey.transactionCode = transactionCode;
            this.mTempKey.screenInteractive = screenInteractive;
            return this.mCallStats.get(this.mTempKey);
        }

        CallStat getOrCreate(int callingUid, Class<? extends Binder> binderClass, int transactionCode, boolean screenInteractive, boolean maxCallStatsReached) {
            CallStat mapCallStat = get(callingUid, binderClass, transactionCode, screenInteractive);
            if (mapCallStat == null) {
                if (maxCallStatsReached) {
                    CallStat mapCallStat2 = get(-1, BinderCallsStats.OVERFLOW_BINDER, -1, false);
                    if (mapCallStat2 != null) {
                        return mapCallStat2;
                    }
                    callingUid = -1;
                    binderClass = BinderCallsStats.OVERFLOW_BINDER;
                    transactionCode = -1;
                    screenInteractive = false;
                }
                CallStat mapCallStat3 = new CallStat(callingUid, binderClass, transactionCode, screenInteractive);
                CallStatKey key = new CallStatKey();
                key.callingUid = callingUid;
                key.binderClass = binderClass;
                key.transactionCode = transactionCode;
                key.screenInteractive = screenInteractive;
                this.mCallStats.put(key, mapCallStat3);
                return mapCallStat3;
            }
            return mapCallStat;
        }

        public Collection<CallStat> getCallStatsList() {
            return this.mCallStats.values();
        }

        public String toString() {
            return "UidEntry{cpuTimeMicros=" + this.cpuTimeMicros + ", callCount=" + this.callCount + ", mCallStats=" + this.mCallStats + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            UidEntry uidEntry = (UidEntry) o;
            return this.workSourceUid == uidEntry.workSourceUid;
        }

        public int hashCode() {
            return this.workSourceUid;
        }
    }

    public SparseArray<UidEntry> getUidEntries() {
        return this.mUidEntries;
    }

    public ArrayMap<String, Integer> getExceptionCounts() {
        return this.mExceptionCounts;
    }

    public BinderLatencyObserver getLatencyObserver() {
        return this.mLatencyObserver;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> List<T> getHighestValues(List<T> list, ToDoubleFunction<T> toDouble, double percentile) {
        List<T> sortedList = new ArrayList<>(list);
        sortedList.sort(Comparator.comparingDouble(toDouble).reversed());
        double total = 0.0d;
        for (T item : list) {
            total += toDouble.applyAsDouble(item);
        }
        List<T> result = new ArrayList<>();
        double runningSum = 0.0d;
        for (T item2 : sortedList) {
            if (runningSum > percentile * total) {
                break;
            }
            result.add(item2);
            runningSum += toDouble.applyAsDouble(item2);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int compareByCpuDesc(ExportedCallStat a, ExportedCallStat b) {
        return Long.compare(b.cpuTimeMicros, a.cpuTimeMicros);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int compareByBinderClassAndCode(ExportedCallStat a, ExportedCallStat b) {
        int result = a.className.compareTo(b.className);
        if (result != 0) {
            return result;
        }
        return Integer.compare(a.transactionCode, b.transactionCode);
    }

    public static void startForBluetooth(Context context) {
        new SettingsObserver(context, new BinderCallsStats(new Injector(), 3));
    }

    /* renamed from: com.android.internal.os.BinderCallsStats$SettingsObserver */
    /* loaded from: classes4.dex */
    public static class SettingsObserver extends ContentObserver {
        public static final String SETTINGS_COLLECT_LATENCY_DATA_KEY = "collect_latency_data";
        public static final String SETTINGS_DETAILED_TRACKING_KEY = "detailed_tracking";
        public static final String SETTINGS_ENABLED_KEY = "enabled";
        public static final String SETTINGS_IGNORE_BATTERY_STATUS_KEY = "ignore_battery_status";
        public static final String SETTINGS_LATENCY_HISTOGRAM_BUCKET_COUNT_KEY = "latency_histogram_bucket_count";
        public static final String SETTINGS_LATENCY_HISTOGRAM_BUCKET_SCALE_FACTOR_KEY = "latency_histogram_bucket_scale_factor";
        public static final String SETTINGS_LATENCY_HISTOGRAM_FIRST_BUCKET_SIZE_KEY = "latency_histogram_first_bucket_size";
        public static final String SETTINGS_LATENCY_OBSERVER_PUSH_INTERVAL_MINUTES_KEY = "latency_observer_push_interval_minutes";
        public static final String SETTINGS_LATENCY_OBSERVER_SAMPLING_INTERVAL_KEY = "latency_observer_sampling_interval";
        public static final String SETTINGS_LATENCY_OBSERVER_SHARDING_MODULO_KEY = "latency_observer_sharding_modulo";
        public static final String SETTINGS_MAX_CALL_STATS_KEY = "max_call_stats_count";
        public static final String SETTINGS_SAMPLING_INTERVAL_KEY = "sampling_interval";
        public static final String SETTINGS_SHARDING_MODULO_KEY = "sharding_modulo";
        public static final String SETTINGS_TRACK_DIRECT_CALLING_UID_KEY = "track_calling_uid";
        public static final String SETTINGS_TRACK_SCREEN_INTERACTIVE_KEY = "track_screen_state";
        public static final String SETTINGS_UPLOAD_DATA_KEY = "upload_data";
        private final BinderCallsStats mBinderCallsStats;
        private final Context mContext;
        private boolean mEnabled;
        private final KeyValueListParser mParser;
        private final Uri mUri;

        public SettingsObserver(Context context, BinderCallsStats binderCallsStats) {
            super(BackgroundThread.getHandler());
            Uri uriFor = Settings.Global.getUriFor(Settings.Global.BINDER_CALLS_STATS);
            this.mUri = uriFor;
            this.mParser = new KeyValueListParser(',');
            this.mContext = context;
            context.getContentResolver().registerContentObserver(uriFor, false, this);
            this.mBinderCallsStats = binderCallsStats;
            onChange();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (this.mUri.equals(uri)) {
                onChange();
            }
        }

        void onChange() {
            try {
                this.mParser.setString(Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.BINDER_CALLS_STATS));
            } catch (IllegalArgumentException e) {
                Slog.m95e(BinderCallsStats.TAG, "Bad binder call stats settings", e);
            }
            this.mBinderCallsStats.setDetailedTracking(false);
            this.mBinderCallsStats.setTrackScreenInteractive(false);
            this.mBinderCallsStats.setTrackDirectCallerUid(false);
            this.mBinderCallsStats.setIgnoreBatteryStatus(this.mParser.getBoolean(SETTINGS_IGNORE_BATTERY_STATUS_KEY, false));
            this.mBinderCallsStats.setCollectLatencyData(this.mParser.getBoolean(SETTINGS_COLLECT_LATENCY_DATA_KEY, true));
            configureLatencyObserver(this.mParser, this.mBinderCallsStats.getLatencyObserver());
            boolean enabled = this.mParser.getBoolean("enabled", true);
            if (this.mEnabled != enabled) {
                if (enabled) {
                    Binder.setObserver(this.mBinderCallsStats);
                } else {
                    Binder.setObserver(null);
                }
                this.mEnabled = enabled;
                this.mBinderCallsStats.reset();
                this.mBinderCallsStats.setAddDebugEntries(enabled);
                this.mBinderCallsStats.getLatencyObserver().reset();
            }
        }

        public static void configureLatencyObserver(KeyValueListParser mParser, BinderLatencyObserver binderLatencyObserver) {
            binderLatencyObserver.setSamplingInterval(mParser.getInt(SETTINGS_LATENCY_OBSERVER_SAMPLING_INTERVAL_KEY, 10));
            binderLatencyObserver.setShardingModulo(mParser.getInt(SETTINGS_LATENCY_OBSERVER_SHARDING_MODULO_KEY, 1));
            binderLatencyObserver.setHistogramBucketsParams(mParser.getInt(SETTINGS_LATENCY_HISTOGRAM_BUCKET_COUNT_KEY, 100), mParser.getInt(SETTINGS_LATENCY_HISTOGRAM_FIRST_BUCKET_SIZE_KEY, 5), mParser.getFloat(SETTINGS_LATENCY_HISTOGRAM_BUCKET_SCALE_FACTOR_KEY, 1.125f));
            binderLatencyObserver.setPushInterval(mParser.getInt(SETTINGS_LATENCY_OBSERVER_PUSH_INTERVAL_MINUTES_KEY, 360));
        }
    }
}
