package com.android.internal.app.procstats;

import android.app.job.JobInfo;
import android.media.AudioSystem;
import android.p008os.Parcel;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.AssociationState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.content.NativeLibraryHelper;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
/* loaded from: classes4.dex */
public final class ProcessState {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_PARCEL = false;
    private static final String TAG = "ProcessStats";
    private boolean mActive;
    private long mAvgCachedKillPss;
    private ProcessState mCommonProcess;
    ArrayMap<AssociationState.SourceKey, AssociationState.SourceState> mCommonSources;
    private int mCurCombinedState;
    private boolean mDead;
    private final DurationsTable mDurations;
    private int mLastPssState;
    private long mLastPssTime;
    private long mMaxCachedKillPss;
    private long mMinCachedKillPss;
    private boolean mMultiPackage;
    private final String mName;
    private int mNumActiveServices;
    private int mNumCachedKill;
    private int mNumExcessiveCpu;
    private int mNumStartedServices;
    private final String mPackage;
    private final PssTable mPssTable;
    private long mStartTime;
    private final ProcessStats mStats;
    private long mTmpTotalTime;
    private long mTotalRunningDuration;
    private final long[] mTotalRunningPss;
    private long mTotalRunningStartTime;
    private final int mUid;
    private final long mVersion;
    public ProcessState tmpFoundSubProc;
    public int tmpNumInUse;
    static final int[] PROCESS_STATE_TO_STATE = {0, 0, 1, 2, 3, 2, 4, 5, 5, 6, 7, 9, 1, 10, 11, 12, 13, 14, 13, 15};
    public static final Comparator<ProcessState> COMPARATOR = new Comparator<ProcessState>() { // from class: com.android.internal.app.procstats.ProcessState.1
        @Override // java.util.Comparator
        public int compare(ProcessState lhs, ProcessState rhs) {
            if (lhs.mTmpTotalTime < rhs.mTmpTotalTime) {
                return -1;
            }
            if (lhs.mTmpTotalTime > rhs.mTmpTotalTime) {
                return 1;
            }
            return 0;
        }
    };

    /* loaded from: classes4.dex */
    static class PssAggr {
        long pss = 0;
        long samples = 0;

        PssAggr() {
        }

        void add(long newPss, long newSamples) {
            long j = this.samples;
            this.pss = ((long) ((this.pss * j) + (newPss * newSamples))) / (j + newSamples);
            this.samples = j + newSamples;
        }
    }

    public ProcessState(ProcessStats processStats, String pkg, int uid, long vers, String name) {
        this.mTotalRunningPss = new long[10];
        this.mCurCombinedState = -1;
        this.mLastPssState = -1;
        this.mStats = processStats;
        this.mName = name;
        this.mCommonProcess = this;
        this.mPackage = pkg;
        this.mUid = uid;
        this.mVersion = vers;
        this.mDurations = new DurationsTable(processStats.mTableData);
        this.mPssTable = new PssTable(processStats.mTableData);
    }

    public ProcessState(ProcessState commonProcess, String pkg, int uid, long vers, String name, long now) {
        this.mTotalRunningPss = new long[10];
        this.mCurCombinedState = -1;
        this.mLastPssState = -1;
        this.mStats = commonProcess.mStats;
        this.mName = name;
        this.mCommonProcess = commonProcess;
        this.mPackage = pkg;
        this.mUid = uid;
        this.mVersion = vers;
        int i = commonProcess.mCurCombinedState;
        this.mCurCombinedState = i;
        this.mStartTime = now;
        if (i != -1) {
            this.mTotalRunningStartTime = now;
        }
        this.mDurations = new DurationsTable(commonProcess.mStats.mTableData);
        this.mPssTable = new PssTable(commonProcess.mStats.mTableData);
    }

    public ProcessState clone(long now) {
        ProcessState pnew = new ProcessState(this, this.mPackage, this.mUid, this.mVersion, this.mName, now);
        pnew.mDurations.addDurations(this.mDurations);
        pnew.mPssTable.copyFrom(this.mPssTable, 10);
        System.arraycopy(this.mTotalRunningPss, 0, pnew.mTotalRunningPss, 0, 10);
        pnew.mTotalRunningDuration = getTotalRunningDuration(now);
        pnew.mNumExcessiveCpu = this.mNumExcessiveCpu;
        pnew.mNumCachedKill = this.mNumCachedKill;
        pnew.mMinCachedKillPss = this.mMinCachedKillPss;
        pnew.mAvgCachedKillPss = this.mAvgCachedKillPss;
        pnew.mMaxCachedKillPss = this.mMaxCachedKillPss;
        pnew.mActive = this.mActive;
        pnew.mNumActiveServices = this.mNumActiveServices;
        pnew.mNumStartedServices = this.mNumStartedServices;
        return pnew;
    }

    public String getName() {
        return this.mName;
    }

    public ProcessState getCommonProcess() {
        return this.mCommonProcess;
    }

    public void makeStandalone() {
        this.mCommonProcess = this;
    }

    public String getPackage() {
        return this.mPackage;
    }

    public int getUid() {
        return this.mUid;
    }

    public long getVersion() {
        return this.mVersion;
    }

    public boolean isMultiPackage() {
        return this.mMultiPackage;
    }

    public void setMultiPackage(boolean val) {
        this.mMultiPackage = val;
    }

    public int getDurationsBucketCount() {
        return this.mDurations.getKeyCount();
    }

    public void add(ProcessState other) {
        this.mDurations.addDurations(other.mDurations);
        this.mPssTable.mergeStats(other.mPssTable);
        this.mNumExcessiveCpu += other.mNumExcessiveCpu;
        int i = other.mNumCachedKill;
        if (i > 0) {
            addCachedKill(i, other.mMinCachedKillPss, other.mAvgCachedKillPss, other.mMaxCachedKillPss);
        }
        if (other.mCommonSources != null) {
            if (this.mCommonSources == null) {
                this.mCommonSources = new ArrayMap<>();
            }
            int size = other.mCommonSources.size();
            for (int i2 = 0; i2 < size; i2++) {
                AssociationState.SourceKey key = other.mCommonSources.keyAt(i2);
                AssociationState.SourceState state = this.mCommonSources.get(key);
                if (state == null) {
                    state = new AssociationState.SourceState(this.mStats, null, this, key);
                    this.mCommonSources.put(key, state);
                }
                state.add(other.mCommonSources.valueAt(i2));
            }
        }
    }

    public void resetSafely(long now) {
        this.mDurations.resetTable();
        this.mPssTable.resetTable();
        this.mStartTime = now;
        this.mLastPssState = -1;
        this.mLastPssTime = 0L;
        this.mNumExcessiveCpu = 0;
        this.mNumCachedKill = 0;
        this.mMaxCachedKillPss = 0L;
        this.mAvgCachedKillPss = 0L;
        this.mMinCachedKillPss = 0L;
        ArrayMap<AssociationState.SourceKey, AssociationState.SourceState> arrayMap = this.mCommonSources;
        if (arrayMap != null) {
            for (int ip = arrayMap.size() - 1; ip >= 0; ip--) {
                AssociationState.SourceState state = this.mCommonSources.valueAt(ip);
                if (state.isInUse()) {
                    state.resetSafely(now);
                } else {
                    this.mCommonSources.removeAt(ip);
                }
            }
        }
    }

    public void makeDead() {
        this.mDead = true;
    }

    private void ensureNotDead() {
        if (!this.mDead) {
            return;
        }
        Slog.m90w("ProcessStats", "ProcessState dead: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
    }

    public void writeToParcel(Parcel out, long now) {
        out.writeInt(this.mMultiPackage ? 1 : 0);
        this.mDurations.writeToParcel(out);
        this.mPssTable.writeToParcel(out);
        for (int i = 0; i < 10; i++) {
            out.writeLong(this.mTotalRunningPss[i]);
        }
        out.writeLong(getTotalRunningDuration(now));
        out.writeInt(0);
        out.writeInt(this.mNumExcessiveCpu);
        out.writeInt(this.mNumCachedKill);
        if (this.mNumCachedKill > 0) {
            out.writeLong(this.mMinCachedKillPss);
            out.writeLong(this.mAvgCachedKillPss);
            out.writeLong(this.mMaxCachedKillPss);
        }
        ArrayMap<AssociationState.SourceKey, AssociationState.SourceState> arrayMap = this.mCommonSources;
        int numOfSources = arrayMap != null ? arrayMap.size() : 0;
        out.writeInt(numOfSources);
        for (int i2 = 0; i2 < numOfSources; i2++) {
            AssociationState.SourceKey key = this.mCommonSources.keyAt(i2);
            AssociationState.SourceState src = this.mCommonSources.valueAt(i2);
            key.writeToParcel(this.mStats, out);
            src.writeToParcel(out, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean readFromParcel(Parcel in, int version, boolean fully) {
        boolean multiPackage = in.readInt() != 0;
        if (fully) {
            this.mMultiPackage = multiPackage;
        }
        if (this.mDurations.readFromParcel(in) && this.mPssTable.readFromParcel(in)) {
            for (int i = 0; i < 10; i++) {
                this.mTotalRunningPss[i] = in.readLong();
            }
            this.mTotalRunningDuration = in.readLong();
            in.readInt();
            this.mNumExcessiveCpu = in.readInt();
            int readInt = in.readInt();
            this.mNumCachedKill = readInt;
            if (readInt > 0) {
                this.mMinCachedKillPss = in.readLong();
                this.mAvgCachedKillPss = in.readLong();
                this.mMaxCachedKillPss = in.readLong();
            } else {
                this.mMaxCachedKillPss = 0L;
                this.mAvgCachedKillPss = 0L;
                this.mMinCachedKillPss = 0L;
            }
            int numOfSources = in.readInt();
            if (numOfSources > 0) {
                this.mCommonSources = new ArrayMap<>(numOfSources);
                for (int i2 = 0; i2 < numOfSources; i2++) {
                    AssociationState.SourceKey key = new AssociationState.SourceKey(this.mStats, in, version);
                    AssociationState.SourceState src = new AssociationState.SourceState(this.mStats, null, this, key);
                    src.readFromParcel(in);
                    this.mCommonSources.put(key, src);
                }
            }
            return true;
        }
        return false;
    }

    public void makeActive() {
        ensureNotDead();
        this.mActive = true;
    }

    public void makeInactive() {
        this.mActive = false;
    }

    public boolean isInUse() {
        return this.mActive || this.mNumActiveServices > 0 || this.mNumStartedServices > 0 || this.mCurCombinedState != -1;
    }

    public boolean isActive() {
        return this.mActive;
    }

    public boolean hasAnyData() {
        return (this.mDurations.getKeyCount() == 0 && this.mCurCombinedState == -1 && this.mPssTable.getKeyCount() == 0 && this.mTotalRunningPss[0] == 0) ? false : true;
    }

    public void setState(int state, int memFactor, long now, ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList) {
        int state2;
        if (state < 0) {
            state2 = this.mNumStartedServices > 0 ? (memFactor * 16) + 8 : -1;
        } else {
            state2 = PROCESS_STATE_TO_STATE[state] + (memFactor * 16);
        }
        this.mCommonProcess.setCombinedState(state2, now);
        if (this.mCommonProcess.mMultiPackage && pkgList != null) {
            for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                pullFixedProc(pkgList, ip).setCombinedState(state2, now);
            }
        }
    }

    public void setCombinedState(int state, long now) {
        ensureNotDead();
        if (!this.mDead && this.mCurCombinedState != state) {
            commitStateTime(now);
            if (state == -1) {
                this.mTotalRunningDuration += now - this.mTotalRunningStartTime;
                this.mTotalRunningStartTime = 0L;
            } else if (this.mCurCombinedState == -1) {
                this.mTotalRunningDuration = 0L;
                this.mTotalRunningStartTime = now;
                for (int i = 9; i >= 0; i--) {
                    this.mTotalRunningPss[i] = 0;
                }
            }
            this.mCurCombinedState = state;
            UidState uidState = this.mStats.mUidStates.get(this.mUid);
            if (uidState != null) {
                uidState.updateCombinedState(state, now);
            }
        }
    }

    public int getCombinedState() {
        return this.mCurCombinedState;
    }

    public void commitStateTime(long now) {
        int i = this.mCurCombinedState;
        if (i != -1) {
            long dur = now - this.mStartTime;
            if (dur > 0) {
                this.mDurations.addDuration(i, dur);
            }
            this.mTotalRunningDuration += now - this.mTotalRunningStartTime;
            this.mTotalRunningStartTime = now;
        }
        this.mStartTime = now;
        ArrayMap<AssociationState.SourceKey, AssociationState.SourceState> arrayMap = this.mCommonSources;
        if (arrayMap != null) {
            for (int ip = arrayMap.size() - 1; ip >= 0; ip--) {
                AssociationState.SourceState src = this.mCommonSources.valueAt(ip);
                src.commitStateTime(now);
            }
        }
    }

    public void incActiveServices(String serviceName) {
        ProcessState processState = this.mCommonProcess;
        if (processState != this) {
            processState.incActiveServices(serviceName);
        }
        this.mNumActiveServices++;
    }

    public void decActiveServices(String serviceName) {
        ProcessState processState = this.mCommonProcess;
        if (processState != this) {
            processState.decActiveServices(serviceName);
        }
        int i = this.mNumActiveServices - 1;
        this.mNumActiveServices = i;
        if (i < 0) {
            Slog.wtfStack("ProcessStats", "Proc active services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " proc=" + this.mName + " service=" + serviceName);
            this.mNumActiveServices = 0;
        }
    }

    public void incStartedServices(int memFactor, long now, String serviceName) {
        ProcessState processState = this.mCommonProcess;
        if (processState != this) {
            processState.incStartedServices(memFactor, now, serviceName);
        }
        int i = this.mNumStartedServices + 1;
        this.mNumStartedServices = i;
        if (i == 1 && this.mCurCombinedState == -1) {
            setCombinedState((memFactor * 16) + 8, now);
        }
    }

    public void decStartedServices(int memFactor, long now, String serviceName) {
        ProcessState processState = this.mCommonProcess;
        if (processState != this) {
            processState.decStartedServices(memFactor, now, serviceName);
        }
        int i = this.mNumStartedServices - 1;
        this.mNumStartedServices = i;
        if (i == 0 && this.mCurCombinedState % 16 == 8) {
            setCombinedState(-1, now);
        } else if (i < 0) {
            Slog.wtfStack("ProcessStats", "Proc started services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " name=" + this.mName);
            this.mNumStartedServices = 0;
        }
    }

    public void addPss(long pss, long uss, long rss, boolean always, int type, long duration, ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList) {
        ensureNotDead();
        switch (type) {
            case 0:
                this.mStats.mInternalSinglePssCount++;
                this.mStats.mInternalSinglePssTime += duration;
                break;
            case 1:
                this.mStats.mInternalAllMemPssCount++;
                this.mStats.mInternalAllMemPssTime += duration;
                break;
            case 2:
                this.mStats.mInternalAllPollPssCount++;
                this.mStats.mInternalAllPollPssTime += duration;
                break;
            case 3:
                this.mStats.mExternalPssCount++;
                this.mStats.mExternalPssTime += duration;
                break;
            case 4:
                this.mStats.mExternalSlowPssCount++;
                this.mStats.mExternalSlowPssTime += duration;
                break;
        }
        if (always || this.mLastPssState != this.mCurCombinedState || SystemClock.uptimeMillis() >= this.mLastPssTime + JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS) {
            this.mLastPssState = this.mCurCombinedState;
            this.mLastPssTime = SystemClock.uptimeMillis();
            int i = this.mCurCombinedState;
            if (i != -1) {
                this.mCommonProcess.mPssTable.mergeStats(i, 1, pss, pss, pss, uss, uss, uss, rss, rss, rss);
                PssTable.mergeStats(this.mCommonProcess.mTotalRunningPss, 0, 1, pss, pss, pss, uss, uss, uss, rss, rss, rss);
                if (this.mCommonProcess.mMultiPackage && pkgList != null) {
                    for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                        ProcessState fixedProc = pullFixedProc(pkgList, ip);
                        fixedProc.mPssTable.mergeStats(this.mCurCombinedState, 1, pss, pss, pss, uss, uss, uss, rss, rss, rss);
                        PssTable.mergeStats(fixedProc.mTotalRunningPss, 0, 1, pss, pss, pss, uss, uss, uss, rss, rss, rss);
                    }
                }
            }
        }
    }

    public void reportExcessiveCpu(ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList) {
        ensureNotDead();
        ProcessState processState = this.mCommonProcess;
        processState.mNumExcessiveCpu++;
        if (!processState.mMultiPackage) {
            return;
        }
        for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
            pullFixedProc(pkgList, ip).mNumExcessiveCpu++;
        }
    }

    private void addCachedKill(int num, long minPss, long avgPss, long maxPss) {
        int i = this.mNumCachedKill;
        if (i <= 0) {
            this.mNumCachedKill = num;
            this.mMinCachedKillPss = minPss;
            this.mAvgCachedKillPss = avgPss;
            this.mMaxCachedKillPss = maxPss;
            return;
        }
        if (minPss < this.mMinCachedKillPss) {
            this.mMinCachedKillPss = minPss;
        }
        if (maxPss > this.mMaxCachedKillPss) {
            this.mMaxCachedKillPss = maxPss;
        }
        this.mAvgCachedKillPss = (long) (((this.mAvgCachedKillPss * i) + avgPss) / (i + num));
        this.mNumCachedKill = i + num;
    }

    public void reportCachedKill(ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList, long pss) {
        ensureNotDead();
        this.mCommonProcess.addCachedKill(1, pss, pss, pss);
        if (!this.mCommonProcess.mMultiPackage) {
            return;
        }
        for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
            pullFixedProc(pkgList, ip).addCachedKill(1, pss, pss, pss);
        }
    }

    public ProcessState pullFixedProc(String pkgName) {
        if (this.mMultiPackage) {
            LongSparseArray<ProcessStats.PackageState> vpkg = this.mStats.mPackages.get(pkgName, this.mUid);
            if (vpkg == null) {
                throw new IllegalStateException("Didn't find package " + pkgName + " / " + this.mUid);
            }
            ProcessStats.PackageState pkg = vpkg.get(this.mVersion);
            if (pkg == null) {
                throw new IllegalStateException("Didn't find package " + pkgName + " / " + this.mUid + " vers " + this.mVersion);
            }
            ProcessState proc = pkg.mProcesses.get(this.mName);
            if (proc == null) {
                throw new IllegalStateException("Didn't create per-package process " + this.mName + " in pkg " + pkgName + " / " + this.mUid + " vers " + this.mVersion);
            }
            return proc;
        }
        return this;
    }

    private ProcessState pullFixedProc(ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList, int index) {
        ProcessStats.ProcessStateHolder holder = pkgList.valueAt(index);
        ProcessState proc = holder.state;
        if (this.mDead && proc.mCommonProcess != proc) {
            Log.wtf("ProcessStats", "Pulling dead proc: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
            proc = this.mStats.getProcessStateLocked(proc.mPackage, proc.mUid, proc.mVersion, proc.mName);
        }
        if (proc.mMultiPackage) {
            LongSparseArray<ProcessStats.PackageState> vpkg = this.mStats.mPackages.get(pkgList.keyAt(index), proc.mUid);
            if (vpkg == null) {
                throw new IllegalStateException("No existing package " + pkgList.keyAt(index) + "/" + proc.mUid + " for multi-proc " + proc.mName);
            }
            ProcessStats.PackageState expkg = vpkg.get(proc.mVersion);
            if (expkg == null) {
                throw new IllegalStateException("No existing package " + pkgList.keyAt(index) + "/" + proc.mUid + " for multi-proc " + proc.mName + " version " + proc.mVersion);
            }
            String savedName = proc.mName;
            proc = expkg.mProcesses.get(proc.mName);
            if (proc == null) {
                throw new IllegalStateException("Didn't create per-package process " + savedName + " in pkg " + expkg.mPackageName + "/" + expkg.mUid);
            }
            holder.state = proc;
        }
        return proc;
    }

    public long getTotalRunningDuration(long now) {
        long j = this.mTotalRunningDuration;
        long j2 = this.mTotalRunningStartTime;
        return j + (j2 != 0 ? now - j2 : 0L);
    }

    public long getDuration(int state, long now) {
        long time = this.mDurations.getValueForId((byte) state);
        if (this.mCurCombinedState == state) {
            return time + (now - this.mStartTime);
        }
        return time;
    }

    public long getPssSampleCount(int state) {
        return this.mPssTable.getValueForId((byte) state, 0);
    }

    public long getPssMinimum(int state) {
        return this.mPssTable.getValueForId((byte) state, 1);
    }

    public long getPssAverage(int state) {
        return this.mPssTable.getValueForId((byte) state, 2);
    }

    public long getPssMaximum(int state) {
        return this.mPssTable.getValueForId((byte) state, 3);
    }

    public long getPssUssMinimum(int state) {
        return this.mPssTable.getValueForId((byte) state, 4);
    }

    public long getPssUssAverage(int state) {
        return this.mPssTable.getValueForId((byte) state, 5);
    }

    public long getPssUssMaximum(int state) {
        return this.mPssTable.getValueForId((byte) state, 6);
    }

    public long getPssRssMinimum(int state) {
        return this.mPssTable.getValueForId((byte) state, 7);
    }

    public long getPssRssAverage(int state) {
        return this.mPssTable.getValueForId((byte) state, 8);
    }

    public long getPssRssMaximum(int state) {
        return this.mPssTable.getValueForId((byte) state, 9);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AssociationState.SourceState getOrCreateSourceState(AssociationState.SourceKey key) {
        if (this.mCommonSources == null) {
            this.mCommonSources = new ArrayMap<>();
        }
        AssociationState.SourceState state = this.mCommonSources.get(key);
        if (state == null) {
            AssociationState.SourceState state2 = new AssociationState.SourceState(this.mStats, null, this, key);
            this.mCommonSources.put(key, state2);
            return state2;
        }
        return state;
    }

    public void aggregatePss(ProcessStats.TotalMemoryUseCollection data, long now) {
        boolean havePss;
        boolean fgHasBg;
        boolean bgHasCached;
        long avg;
        long samples;
        ProcessState processState = this;
        ProcessStats.TotalMemoryUseCollection totalMemoryUseCollection = data;
        PssAggr fgPss = new PssAggr();
        PssAggr bgPss = new PssAggr();
        PssAggr cachedPss = new PssAggr();
        boolean havePss2 = false;
        for (int i = 0; i < processState.mDurations.getKeyCount(); i++) {
            int type = SparseMappingTable.getIdFromKey(processState.mDurations.getKeyAt(i));
            int procState = type % 16;
            long samples2 = processState.getPssSampleCount(type);
            if (samples2 > 0) {
                long avg2 = processState.getPssAverage(type);
                havePss2 = true;
                if (procState <= 4) {
                    fgPss.add(avg2, samples2);
                } else if (procState <= 9) {
                    bgPss.add(avg2, samples2);
                } else {
                    cachedPss.add(avg2, samples2);
                }
            }
        }
        if (!havePss2) {
            return;
        }
        boolean fgHasBg2 = false;
        boolean fgHasCached = false;
        boolean bgHasCached2 = false;
        if (fgPss.samples < 3 && bgPss.samples > 0) {
            fgHasBg2 = true;
            fgPss.add(bgPss.pss, bgPss.samples);
        }
        if (fgPss.samples < 3 && cachedPss.samples > 0) {
            fgHasCached = true;
            fgPss.add(cachedPss.pss, cachedPss.samples);
        }
        if (bgPss.samples < 3 && cachedPss.samples > 0) {
            bgHasCached2 = true;
            bgPss.add(cachedPss.pss, cachedPss.samples);
        }
        if (bgPss.samples < 3 && !fgHasBg2 && fgPss.samples > 0) {
            bgPss.add(fgPss.pss, fgPss.samples);
        }
        if (cachedPss.samples < 3 && !bgHasCached2 && bgPss.samples > 0) {
            cachedPss.add(bgPss.pss, bgPss.samples);
        }
        if (cachedPss.samples < 3 && !fgHasCached && fgPss.samples > 0) {
            cachedPss.add(fgPss.pss, fgPss.samples);
        }
        int type2 = 0;
        while (type2 < processState.mDurations.getKeyCount()) {
            int key = processState.mDurations.getKeyAt(type2);
            int type3 = SparseMappingTable.getIdFromKey(key);
            long time = processState.mDurations.getValue(key);
            if (processState.mCurCombinedState == type3) {
                time += now - processState.mStartTime;
            }
            int procState2 = type3 % 16;
            long[] jArr = totalMemoryUseCollection.processStateTime;
            jArr[procState2] = jArr[procState2] + time;
            long samples3 = processState.getPssSampleCount(type3);
            if (samples3 > 0) {
                bgHasCached = bgHasCached2;
                samples = samples3;
                havePss = havePss2;
                fgHasBg = fgHasBg2;
                avg = processState.getPssAverage(type3);
            } else if (procState2 <= 4) {
                bgHasCached = bgHasCached2;
                samples = fgPss.samples;
                havePss = havePss2;
                fgHasBg = fgHasBg2;
                avg = fgPss.pss;
            } else {
                havePss = havePss2;
                fgHasBg = fgHasBg2;
                bgHasCached = bgHasCached2;
                if (procState2 <= 9) {
                    samples = bgPss.samples;
                    avg = bgPss.pss;
                } else {
                    long avg3 = cachedPss.samples;
                    avg = cachedPss.pss;
                    samples = avg3;
                }
            }
            PssAggr fgPss2 = fgPss;
            boolean fgHasCached2 = fgHasCached;
            int i2 = type2;
            double newAvg = ((totalMemoryUseCollection.processStatePss[procState2] * totalMemoryUseCollection.processStateSamples[procState2]) + (avg * samples)) / (totalMemoryUseCollection.processStateSamples[procState2] + samples);
            totalMemoryUseCollection.processStatePss[procState2] = (long) newAvg;
            int[] iArr = totalMemoryUseCollection.processStateSamples;
            iArr[procState2] = (int) (iArr[procState2] + samples);
            double[] dArr = totalMemoryUseCollection.processStateWeight;
            dArr[procState2] = dArr[procState2] + (avg * time);
            type2 = i2 + 1;
            processState = this;
            totalMemoryUseCollection = data;
            cachedPss = cachedPss;
            bgHasCached2 = bgHasCached;
            fgPss = fgPss2;
            bgPss = bgPss;
            havePss2 = havePss;
            fgHasBg2 = fgHasBg;
            fgHasCached = fgHasCached2;
        }
    }

    public long computeProcessTimeLocked(int[] screenStates, int[] memStates, int[] procStates, long now) {
        long totalTime = 0;
        for (int i : screenStates) {
            for (int i2 : memStates) {
                for (int i3 : procStates) {
                    int bucket = ((i + i2) * 16) + i3;
                    totalTime += getDuration(bucket, now);
                }
            }
        }
        this.mTmpTotalTime = totalTime;
        return totalTime;
    }

    public void dumpSummary(PrintWriter pw, String prefix, String header, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime) {
        pw.print(prefix);
        pw.print("* ");
        if (header != null) {
            pw.print(header);
        }
        pw.print(this.mName);
        pw.print(" / ");
        UserHandle.formatUid(pw, this.mUid);
        pw.print(" / v");
        pw.print(this.mVersion);
        pw.println(":");
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABEL_TOTAL, screenStates, memStates, procStates, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[0], screenStates, memStates, new int[]{0}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[1], screenStates, memStates, new int[]{1}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[2], screenStates, memStates, new int[]{2}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[3], screenStates, memStates, new int[]{3}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[4], screenStates, memStates, new int[]{4}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[5], screenStates, memStates, new int[]{5}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[6], screenStates, memStates, new int[]{6}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[7], screenStates, memStates, new int[]{7}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[8], screenStates, memStates, new int[]{8}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[9], screenStates, memStates, new int[]{9}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[10], screenStates, memStates, new int[]{10}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[11], screenStates, memStates, new int[]{11}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABELS[12], screenStates, memStates, new int[]{12}, now, totalTime, true);
        dumpProcessSummaryDetails(pw, prefix, DumpUtils.STATE_LABEL_CACHED, screenStates, memStates, new int[]{13, 14, 15}, now, totalTime, true);
    }

    public void dumpProcessState(PrintWriter pw, String prefix, int[] screenStates, int[] memStates, int[] procStates, long now) {
        int i;
        long time;
        ProcessState processState = this;
        int[] iArr = screenStates;
        long totalTime = 0;
        int printedScreen = -1;
        int is = 0;
        while (is < iArr.length) {
            int printedMem = -1;
            int im = 0;
            while (im < memStates.length) {
                int ip = 0;
                while (ip < procStates.length) {
                    int iscreen = iArr[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * 16) + procStates[ip];
                    int is2 = is;
                    int im2 = im;
                    long time2 = processState.mDurations.getValueForId((byte) bucket);
                    String running = "";
                    if (processState.mCurCombinedState == bucket) {
                        running = " (running)";
                        time = time2 + (now - processState.mStartTime);
                    } else {
                        time = time2;
                    }
                    if (time != 0) {
                        pw.print(prefix);
                        if (iArr.length > 1) {
                            DumpUtils.printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > 1) {
                            DumpUtils.printMemLabel(pw, printedMem != imem ? imem : -1, '/');
                            printedMem = imem;
                        }
                        pw.print(DumpUtils.STATE_LABELS[procStates[ip]]);
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                        totalTime += time;
                    }
                    ip++;
                    processState = this;
                    iArr = screenStates;
                    is = is2;
                    im = im2;
                }
                im++;
                processState = this;
                iArr = screenStates;
            }
            is++;
            processState = this;
            iArr = screenStates;
        }
        if (totalTime != 0) {
            pw.print(prefix);
            if (screenStates.length <= 1) {
                i = -1;
            } else {
                i = -1;
                DumpUtils.printScreenLabel(pw, -1);
            }
            if (memStates.length > 1) {
                DumpUtils.printMemLabel(pw, i, '/');
            }
            pw.print(DumpUtils.STATE_LABEL_TOTAL);
            pw.print(": ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
    }

    public void dumpPss(PrintWriter pw, String prefix, int[] screenStates, int[] memStates, int[] procStates, long now) {
        int[] iArr = screenStates;
        boolean printedHeader = false;
        int printedScreen = -1;
        int is = 0;
        while (is < iArr.length) {
            int printedMem = -1;
            int im = 0;
            while (im < memStates.length) {
                int ip = 0;
                while (ip < procStates.length) {
                    int iscreen = iArr[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * 16) + procStates[ip];
                    int is2 = is;
                    int key = this.mPssTable.getKey((byte) bucket);
                    if (key != -1) {
                        long[] table = this.mPssTable.getArrayForKey(key);
                        int tableOffset = SparseMappingTable.getIndexFromKey(key);
                        if (!printedHeader) {
                            pw.print(prefix);
                            pw.print("PSS/USS (");
                            pw.print(this.mPssTable.getKeyCount());
                            pw.println(" entries):");
                            printedHeader = true;
                        }
                        pw.print(prefix);
                        boolean printedHeader2 = printedHeader;
                        pw.print("  ");
                        if (iArr.length > 1) {
                            DumpUtils.printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > 1) {
                            DumpUtils.printMemLabel(pw, printedMem != imem ? imem : -1, '/');
                            printedMem = imem;
                        }
                        pw.print(DumpUtils.STATE_LABELS[procStates[ip]]);
                        pw.print(": ");
                        dumpPssSamples(pw, table, tableOffset);
                        pw.println();
                        printedHeader = printedHeader2;
                    }
                    ip++;
                    iArr = screenStates;
                    is = is2;
                }
                im++;
                iArr = screenStates;
            }
            is++;
            iArr = screenStates;
        }
        long totalRunningDuration = getTotalRunningDuration(now);
        if (totalRunningDuration != 0) {
            pw.print(prefix);
            pw.print("Cur time ");
            TimeUtils.formatDuration(totalRunningDuration, pw);
            if (this.mTotalRunningStartTime != 0) {
                pw.print(" (running)");
            }
            if (this.mTotalRunningPss[0] != 0) {
                pw.print(": ");
                dumpPssSamples(pw, this.mTotalRunningPss, 0);
            }
            pw.println();
        }
        if (this.mNumExcessiveCpu != 0) {
            pw.print(prefix);
            pw.print("Killed for excessive CPU use: ");
            pw.print(this.mNumExcessiveCpu);
            pw.println(" times");
        }
        if (this.mNumCachedKill != 0) {
            pw.print(prefix);
            pw.print("Killed from cached state: ");
            pw.print(this.mNumCachedKill);
            pw.print(" times from pss ");
            DebugUtils.printSizeValue(pw, this.mMinCachedKillPss * 1024);
            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            DebugUtils.printSizeValue(pw, this.mAvgCachedKillPss * 1024);
            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            DebugUtils.printSizeValue(pw, this.mMaxCachedKillPss * 1024);
            pw.println();
        }
    }

    public static void dumpPssSamples(PrintWriter pw, long[] table, int offset) {
        DebugUtils.printSizeValue(pw, table[offset + 1] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 2] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 3] * 1024);
        pw.print("/");
        DebugUtils.printSizeValue(pw, table[offset + 4] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 5] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 6] * 1024);
        pw.print("/");
        DebugUtils.printSizeValue(pw, table[offset + 7] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 8] * 1024);
        pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        DebugUtils.printSizeValue(pw, table[offset + 9] * 1024);
        pw.print(" over ");
        pw.print(table[offset + 0]);
    }

    private void dumpProcessSummaryDetails(PrintWriter pw, String prefix, String label, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime, boolean full) {
        ProcessStats.ProcessDataCollection totals = new ProcessStats.ProcessDataCollection(screenStates, memStates, procStates);
        computeProcessData(totals, now);
        double percentage = (totals.totalTime / totalTime) * 100.0d;
        if (percentage >= 0.005d || totals.numPss != 0) {
            if (prefix != null) {
                pw.print(prefix);
            }
            if (label != null) {
                pw.print("  ");
                pw.print(label);
                pw.print(": ");
            }
            totals.print(pw, totalTime, full);
            if (prefix != null) {
                pw.println();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpInternalLocked(PrintWriter pw, String prefix, String reqPackage, long totalTime, long now, boolean dumpAll) {
        if (dumpAll) {
            pw.print(prefix);
            pw.print("myID=");
            pw.print(Integer.toHexString(System.identityHashCode(this)));
            pw.print(" mCommonProcess=");
            pw.print(Integer.toHexString(System.identityHashCode(this.mCommonProcess)));
            pw.print(" mPackage=");
            pw.println(this.mPackage);
            if (this.mMultiPackage) {
                pw.print(prefix);
                pw.print("mMultiPackage=");
                pw.println(this.mMultiPackage);
            }
            if (this != this.mCommonProcess) {
                pw.print(prefix);
                pw.print("Common Proc: ");
                pw.print(this.mCommonProcess.mName);
                pw.print("/");
                pw.print(this.mCommonProcess.mUid);
                pw.print(" pkg=");
                pw.println(this.mCommonProcess.mPackage);
            }
            if (this.mCommonSources != null) {
                pw.print(prefix);
                pw.println("Aggregated Association Sources:");
                AssociationState.dumpSources(pw, prefix + "  ", prefix + "    ", prefix + "        ", AssociationState.createSortedAssociations(now, totalTime, this.mCommonSources), now, totalTime, reqPackage, true, dumpAll);
            }
        }
        if (this.mActive) {
            pw.print(prefix);
            pw.print("mActive=");
            pw.println(this.mActive);
        }
        if (this.mDead) {
            pw.print(prefix);
            pw.print("mDead=");
            pw.println(this.mDead);
        }
        if (this.mNumActiveServices != 0 || this.mNumStartedServices != 0) {
            pw.print(prefix);
            pw.print("mNumActiveServices=");
            pw.print(this.mNumActiveServices);
            pw.print(" mNumStartedServices=");
            pw.println(this.mNumStartedServices);
        }
    }

    public void computeProcessData(ProcessStats.ProcessDataCollection data, long now) {
        long j;
        int is;
        int im;
        int ip;
        long avgPss;
        long maxRss = 0;
        data.totalTime = 0L;
        data.maxRss = 0L;
        data.avgRss = 0L;
        data.minRss = 0L;
        data.maxUss = 0L;
        data.avgUss = 0L;
        data.minUss = 0L;
        data.maxPss = 0L;
        data.avgPss = 0L;
        data.minPss = 0L;
        data.numPss = 0L;
        int is2 = 0;
        while (is2 < data.screenStates.length) {
            int im2 = 0;
            while (im2 < data.memStates.length) {
                int ip2 = 0;
                while (ip2 < data.procStates.length) {
                    int bucket = ((data.screenStates[is2] + data.memStates[im2]) * 16) + data.procStates[ip2];
                    data.totalTime += getDuration(bucket, now);
                    long samples = getPssSampleCount(bucket);
                    if (samples <= maxRss) {
                        j = maxRss;
                        is = is2;
                        im = im2;
                        ip = ip2;
                    } else {
                        long minPss = getPssMinimum(bucket);
                        long avgPss2 = getPssAverage(bucket);
                        long maxPss = getPssMaximum(bucket);
                        long minUss = getPssUssMinimum(bucket);
                        is = is2;
                        im = im2;
                        long avgUss = getPssUssAverage(bucket);
                        long maxUss = getPssUssMaximum(bucket);
                        long minRss = getPssRssMinimum(bucket);
                        long avgRss = getPssRssAverage(bucket);
                        long maxRss2 = getPssRssMaximum(bucket);
                        ip = ip2;
                        j = 0;
                        if (data.numPss == 0) {
                            data.minPss = minPss;
                            data.avgPss = avgPss2;
                            data.maxPss = maxPss;
                            data.minUss = minUss;
                            data.avgUss = avgUss;
                            data.maxUss = maxUss;
                            data.minRss = minRss;
                            data.avgRss = avgRss;
                            data.maxRss = maxRss2;
                            avgPss = samples;
                        } else {
                            long maxRss3 = data.minPss;
                            if (minPss < maxRss3) {
                                data.minPss = minPss;
                            }
                            double d = avgPss2;
                            avgPss = samples;
                            data.avgPss = (long) (((data.avgPss * data.numPss) + (d * avgPss)) / (data.numPss + avgPss));
                            if (maxPss > data.maxPss) {
                                data.maxPss = maxPss;
                            }
                            if (minUss < data.minUss) {
                                data.minUss = minUss;
                            }
                            data.avgUss = (long) (((data.avgUss * data.numPss) + (avgUss * avgPss)) / (data.numPss + avgPss));
                            if (maxUss > data.maxUss) {
                                data.maxUss = maxUss;
                            }
                            if (minRss < data.minRss) {
                                data.minRss = minRss;
                            }
                            data.avgRss = (long) (((data.avgRss * data.numPss) + (avgRss * avgPss)) / (data.numPss + avgPss));
                            if (maxRss2 > data.maxRss) {
                                data.maxRss = maxRss2;
                            }
                        }
                        data.numPss += avgPss;
                    }
                    ip2 = ip + 1;
                    maxRss = j;
                    is2 = is;
                    im2 = im;
                }
                im2++;
            }
            is2++;
        }
    }

    public void dumpCsv(PrintWriter pw, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        int[] iArr = screenStates;
        int[] iArr2 = memStates;
        int[] iArr3 = procStates;
        int NSS = sepScreenStates ? iArr.length : 1;
        int NMS = sepMemStates ? iArr2.length : 1;
        int NPS = sepProcStates ? iArr3.length : 1;
        int iss = 0;
        while (iss < NSS) {
            int ims = 0;
            while (ims < NMS) {
                int ips = 0;
                while (ips < NPS) {
                    int vsscreen = sepScreenStates ? iArr[iss] : 0;
                    int vsmem = sepMemStates ? iArr2[ims] : 0;
                    int vsproc = sepProcStates ? iArr3[ips] : 0;
                    int NSA = sepScreenStates ? 1 : iArr.length;
                    int NMA = sepMemStates ? 1 : iArr2.length;
                    int NPA = sepProcStates ? 1 : iArr3.length;
                    int NSS2 = NSS;
                    int NMS2 = NMS;
                    long totalTime = 0;
                    int NPS2 = NPS;
                    int NPS3 = 0;
                    while (NPS3 < NSA) {
                        long j = totalTime;
                        int ima = 0;
                        long totalTime2 = j;
                        while (ima < NMA) {
                            int ipa = 0;
                            while (ipa < NPA) {
                                int vascreen = sepScreenStates ? 0 : iArr[NPS3];
                                int vamem = sepMemStates ? 0 : iArr2[ima];
                                int vaproc = sepProcStates ? 0 : iArr3[ipa];
                                int bucket = ((vsscreen + vascreen + vsmem + vamem) * 16) + vsproc + vaproc;
                                totalTime2 += getDuration(bucket, now);
                                ipa++;
                                iArr = screenStates;
                                iArr2 = memStates;
                                iArr3 = procStates;
                                NMA = NMA;
                            }
                            ima++;
                            iArr = screenStates;
                            iArr2 = memStates;
                            iArr3 = procStates;
                            NMA = NMA;
                        }
                        NPS3++;
                        iArr = screenStates;
                        iArr2 = memStates;
                        iArr3 = procStates;
                        totalTime = totalTime2;
                        NMA = NMA;
                    }
                    pw.print("\t");
                    pw.print(totalTime);
                    ips++;
                    iArr = screenStates;
                    iArr2 = memStates;
                    iArr3 = procStates;
                    NPS = NPS2;
                    NSS = NSS2;
                    NMS = NMS2;
                }
                ims++;
                iArr = screenStates;
                iArr2 = memStates;
                iArr3 = procStates;
            }
            iss++;
            iArr = screenStates;
            iArr2 = memStates;
            iArr3 = procStates;
        }
    }

    public void dumpPackageProcCheckin(PrintWriter pw, String pkgName, int uid, long vers, String itemName, long now) {
        pw.print("pkgproc,");
        pw.print(pkgName);
        pw.print(",");
        pw.print(uid);
        pw.print(",");
        pw.print(vers);
        pw.print(",");
        pw.print(DumpUtils.collapseString(pkgName, itemName));
        dumpAllStateCheckin(pw, now);
        pw.println();
        if (this.mPssTable.getKeyCount() > 0) {
            pw.print("pkgpss,");
            pw.print(pkgName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(vers);
            pw.print(",");
            pw.print(DumpUtils.collapseString(pkgName, itemName));
            dumpAllPssCheckin(pw);
            pw.println();
        }
        if (this.mTotalRunningPss[0] != 0) {
            pw.print("pkgrun,");
            pw.print(pkgName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(vers);
            pw.print(",");
            pw.print(DumpUtils.collapseString(pkgName, itemName));
            pw.print(",");
            pw.print(getTotalRunningDuration(now));
            pw.print(",");
            dumpPssSamplesCheckin(pw, this.mTotalRunningPss, 0);
            pw.println();
        }
        if (this.mNumExcessiveCpu > 0 || this.mNumCachedKill > 0) {
            pw.print("pkgkills,");
            pw.print(pkgName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(vers);
            pw.print(",");
            pw.print(DumpUtils.collapseString(pkgName, itemName));
            pw.print(",");
            pw.print(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
            pw.print(",");
            pw.print(this.mNumExcessiveCpu);
            pw.print(",");
            pw.print(this.mNumCachedKill);
            pw.print(",");
            pw.print(this.mMinCachedKillPss);
            pw.print(":");
            pw.print(this.mAvgCachedKillPss);
            pw.print(":");
            pw.print(this.mMaxCachedKillPss);
            pw.println();
        }
    }

    public void dumpProcCheckin(PrintWriter pw, String procName, int uid, long now) {
        if (this.mDurations.getKeyCount() > 0) {
            pw.print("proc,");
            pw.print(procName);
            pw.print(",");
            pw.print(uid);
            dumpAllStateCheckin(pw, now);
            pw.println();
        }
        if (this.mPssTable.getKeyCount() > 0) {
            pw.print("pss,");
            pw.print(procName);
            pw.print(",");
            pw.print(uid);
            dumpAllPssCheckin(pw);
            pw.println();
        }
        if (this.mTotalRunningPss[0] != 0) {
            pw.print("procrun,");
            pw.print(procName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(getTotalRunningDuration(now));
            pw.print(",");
            dumpPssSamplesCheckin(pw, this.mTotalRunningPss, 0);
            pw.println();
        }
        if (this.mNumExcessiveCpu > 0 || this.mNumCachedKill > 0) {
            pw.print("kills,");
            pw.print(procName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
            pw.print(",");
            pw.print(this.mNumExcessiveCpu);
            pw.print(",");
            pw.print(this.mNumCachedKill);
            pw.print(",");
            pw.print(this.mMinCachedKillPss);
            pw.print(":");
            pw.print(this.mAvgCachedKillPss);
            pw.print(":");
            pw.print(this.mMaxCachedKillPss);
            pw.println();
        }
    }

    public void dumpAllStateCheckin(PrintWriter pw, long now) {
        int i;
        boolean didCurState = false;
        for (int i2 = 0; i2 < this.mDurations.getKeyCount(); i2++) {
            int key = this.mDurations.getKeyAt(i2);
            int type = SparseMappingTable.getIdFromKey(key);
            long time = this.mDurations.getValue(key);
            if (this.mCurCombinedState == type) {
                didCurState = true;
                time += now - this.mStartTime;
            }
            DumpUtils.printProcStateTagAndValue(pw, type, time);
        }
        if (!didCurState && (i = this.mCurCombinedState) != -1) {
            DumpUtils.printProcStateTagAndValue(pw, i, now - this.mStartTime);
        }
    }

    public void dumpAllPssCheckin(PrintWriter pw) {
        int N = this.mPssTable.getKeyCount();
        for (int i = 0; i < N; i++) {
            int key = this.mPssTable.getKeyAt(i);
            int type = SparseMappingTable.getIdFromKey(key);
            pw.print(',');
            DumpUtils.printProcStateTag(pw, type);
            pw.print(ShortcutConstants.SERVICES_SEPARATOR);
            dumpPssSamplesCheckin(pw, this.mPssTable.getArrayForKey(key), SparseMappingTable.getIndexFromKey(key));
        }
    }

    public static void dumpPssSamplesCheckin(PrintWriter pw, long[] table, int offset) {
        pw.print(table[offset + 0]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 1]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 2]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 3]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 4]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 5]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 6]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 7]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 8]);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(table[offset + 9]);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ProcessState{").append(Integer.toHexString(System.identityHashCode(this))).append(" ").append(this.mName).append("/").append(this.mUid).append(" pkg=").append(this.mPackage);
        if (this.mMultiPackage) {
            sb.append(" (multi)");
        }
        if (this.mCommonProcess != this) {
            sb.append(" (sub)");
        }
        sb.append("}");
        return sb.toString();
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId, String procName, int uid, long now) {
        long j;
        long j2;
        int i;
        int i2;
        long token = proto.start(fieldId);
        proto.write(1138166333441L, procName);
        proto.write(1120986464258L, uid);
        if (this.mNumExcessiveCpu > 0 || this.mNumCachedKill > 0) {
            long killToken = proto.start(1146756268035L);
            proto.write(1120986464257L, this.mNumExcessiveCpu);
            proto.write(1120986464258L, this.mNumCachedKill);
            ProtoUtils.toAggStatsProto(proto, 1146756268035L, this.mMinCachedKillPss, this.mAvgCachedKillPss, this.mMaxCachedKillPss);
            proto.end(killToken);
        }
        SparseLongArray durationByState = new SparseLongArray();
        boolean didCurState = false;
        for (int i3 = 0; i3 < this.mDurations.getKeyCount(); i3++) {
            int key = this.mDurations.getKeyAt(i3);
            int type = SparseMappingTable.getIdFromKey(key);
            long time = this.mDurations.getValue(key);
            if (this.mCurCombinedState == type) {
                time += now - this.mStartTime;
                didCurState = true;
            }
            durationByState.put(type, time);
        }
        if (!didCurState && (i2 = this.mCurCombinedState) != -1) {
            durationByState.put(i2, now - this.mStartTime);
        }
        int i4 = 0;
        while (true) {
            j = 2246267895813L;
            j2 = 1112396529668L;
            if (i4 >= this.mPssTable.getKeyCount()) {
                break;
            }
            int key2 = this.mPssTable.getKeyAt(i4);
            int type2 = SparseMappingTable.getIdFromKey(key2);
            if (durationByState.indexOfKey(type2) < 0) {
                i = i4;
            } else {
                long stateToken = proto.start(2246267895813L);
                i = i4;
                DumpUtils.printProcStateTagProto(proto, 1159641169921L, 1159641169922L, 1159641169923L, type2);
                long duration = durationByState.get(type2);
                durationByState.delete(type2);
                proto.write(1112396529668L, duration);
                this.mPssTable.writeStatsToProtoForKey(proto, key2);
                proto.end(stateToken);
            }
            i4 = i + 1;
        }
        int i5 = 0;
        while (i5 < durationByState.size()) {
            long stateToken2 = proto.start(j);
            int i6 = i5;
            DumpUtils.printProcStateTagProto(proto, 1159641169921L, 1159641169922L, 1159641169923L, durationByState.keyAt(i5));
            proto.write(1112396529668L, durationByState.valueAt(i6));
            proto.end(stateToken2);
            i5 = i6 + 1;
            j2 = 1112396529668L;
            j = j;
        }
        long j3 = j2;
        long totalRunningDuration = getTotalRunningDuration(now);
        if (totalRunningDuration > 0) {
            long stateToken3 = proto.start(1146756268038L);
            proto.write(j3, totalRunningDuration);
            long[] jArr = this.mTotalRunningPss;
            if (jArr[0] != 0) {
                PssTable.writeStatsToProto(proto, jArr, 0);
            }
            proto.end(stateToken3);
        }
        proto.end(token);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeCompressedProcessName(ProtoOutputStream proto, long fieldId, String procName, String packageName, boolean sharedUid) {
        if (sharedUid) {
            proto.write(fieldId, procName);
        } else if (TextUtils.equals(procName, packageName)) {
        } else {
            if (procName.startsWith(packageName)) {
                int pkgLength = packageName.length();
                if (procName.charAt(pkgLength) == ':') {
                    proto.write(fieldId, procName.substring(pkgLength));
                    return;
                }
            }
            proto.write(fieldId, procName);
        }
    }

    public void dumpStateDurationToStatsd(int atomTag, ProcessStats processStats, StatsEventOutput statsEventOutput) {
        int size = this.mDurations.getKeyCount();
        int i = 0;
        long topMs = 0;
        long otherMs = 0;
        long fgsMs = 0;
        long cachedMs = 0;
        long cachedMs2 = 0;
        long importantForegroundMs = 0;
        while (i < size) {
            int size2 = size;
            int key = this.mDurations.getKeyAt(i);
            int type = SparseMappingTable.getIdFromKey(key);
            int procStateIndex = type % 16;
            long duration = this.mDurations.getValue(key);
            switch (procStateIndex) {
                case 0:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    otherMs += duration;
                    break;
                case 1:
                    topMs += duration;
                    break;
                case 2:
                    importantForegroundMs += duration;
                    break;
                case 3:
                    fgsMs += duration;
                    break;
                case 4:
                case 5:
                    cachedMs2 += duration;
                    break;
                case 13:
                case 14:
                case 15:
                    cachedMs += duration;
                    break;
            }
            i++;
            size = size2;
        }
        int uid = getUid();
        String name = getName();
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long otherMs2 = otherMs;
        long otherMs3 = processStats.mTimePeriodStartUptime;
        TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;
        long j = processStats.mTimePeriodEndUptime;
        long cachedMs3 = cachedMs;
        long cachedMs4 = processStats.mTimePeriodStartUptime;
        long boundTopMs = TimeUnit.MILLISECONDS.toSeconds(cachedMs3);
        statsEventOutput.write(atomTag, uid, name, (int) timeUnit.toSeconds(otherMs3), (int) TimeUnit.MILLISECONDS.toSeconds(processStats.mTimePeriodEndUptime), (int) timeUnit2.toSeconds(j - cachedMs4), (int) TimeUnit.MILLISECONDS.toSeconds(topMs), (int) TimeUnit.MILLISECONDS.toSeconds(fgsMs), (int) TimeUnit.MILLISECONDS.toSeconds(importantForegroundMs), (int) TimeUnit.MILLISECONDS.toSeconds(0L), (int) TimeUnit.MILLISECONDS.toSeconds(cachedMs2), (int) boundTopMs, (int) TimeUnit.MILLISECONDS.toSeconds(0L), (int) TimeUnit.MILLISECONDS.toSeconds(otherMs2));
    }

    /* JADX WARN: Removed duplicated region for block: B:67:0x015b A[LOOP:3: B:65:0x0155->B:67:0x015b, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dumpAggregatedProtoForStatsd(ProtoOutputStream proto, long fieldId, String procName, int uid, long now, ProcessMap<ArraySet<ProcessStats.PackageState>> procToPkgMap, SparseArray<ArraySet<String>> uidToPkgMap) {
        boolean z;
        int i;
        int i2;
        SparseLongArray durationByState = new SparseLongArray();
        boolean didCurState = false;
        for (int i3 = 0; i3 < this.mDurations.getKeyCount(); i3++) {
            int key = this.mDurations.getKeyAt(i3);
            int type = SparseMappingTable.getIdFromKey(key);
            int aggregatedType = DumpUtils.aggregateCurrentProcessState(type);
            if (type % 16 != 8) {
                long time = this.mDurations.getValue(key);
                if (this.mCurCombinedState == type) {
                    didCurState = true;
                    time += now - this.mStartTime;
                }
                int index = durationByState.indexOfKey(aggregatedType);
                if (index >= 0) {
                    durationByState.put(aggregatedType, durationByState.valueAt(index) + time);
                } else {
                    durationByState.put(aggregatedType, time);
                }
            }
        }
        if (!didCurState && (i2 = this.mCurCombinedState) != -1 && i2 % 16 != 8) {
            int aggregatedType2 = DumpUtils.aggregateCurrentProcessState(i2);
            int index2 = durationByState.indexOfKey(aggregatedType2);
            if (index2 >= 0) {
                durationByState.put(aggregatedType2, (now - this.mStartTime) + durationByState.valueAt(index2));
            } else {
                durationByState.put(aggregatedType2, now - this.mStartTime);
            }
        }
        SparseLongArray meanRssByState = new SparseLongArray();
        SparseLongArray maxRssByState = new SparseLongArray();
        for (int i4 = 0; i4 < this.mPssTable.getKeyCount(); i4++) {
            int key2 = this.mPssTable.getKeyAt(i4);
            int type2 = SparseMappingTable.getIdFromKey(key2);
            int aggregatedType3 = DumpUtils.aggregateCurrentProcessState(type2);
            if (durationByState.indexOfKey(aggregatedType3) >= 0) {
                long[] rssMeanAndMax = this.mPssTable.getRssMeanAndMax(key2);
                long meanTimesDuration = rssMeanAndMax[0] * this.mDurations.getValueForId((byte) type2);
                if (meanRssByState.indexOfKey(aggregatedType3) >= 0) {
                    meanRssByState.put(aggregatedType3, meanRssByState.get(aggregatedType3) + meanTimesDuration);
                } else {
                    meanRssByState.put(aggregatedType3, meanTimesDuration);
                }
                if (maxRssByState.indexOfKey(aggregatedType3) >= 0 && maxRssByState.get(aggregatedType3) < rssMeanAndMax[1]) {
                    maxRssByState.put(aggregatedType3, rssMeanAndMax[1]);
                } else if (maxRssByState.indexOfKey(aggregatedType3) < 0) {
                    maxRssByState.put(aggregatedType3, rssMeanAndMax[1]);
                }
            }
        }
        for (int i5 = 0; i5 < durationByState.size(); i5++) {
            int aggregatedKey = durationByState.keyAt(i5);
            if (meanRssByState.indexOfKey(aggregatedKey) >= 0) {
                long duration = durationByState.get(aggregatedKey);
                meanRssByState.put(aggregatedKey, duration > 0 ? meanRssByState.get(aggregatedKey) / duration : meanRssByState.get(aggregatedKey));
            }
        }
        long token = proto.start(fieldId);
        String str = this.mPackage;
        if (!this.mMultiPackage && uidToPkgMap.get(this.mUid).size() <= 1) {
            z = false;
            writeCompressedProcessName(proto, 1138166333441L, procName, str, z);
            proto.write(1120986464258L, uid);
            i = 0;
            while (i < durationByState.size()) {
                long stateToken = proto.start(2246267895813L);
                int aggregatedKey2 = durationByState.keyAt(i);
                DumpUtils.printAggregatedProcStateTagProto(proto, 1159641169921L, 1159641169930L, aggregatedKey2);
                proto.write(1112396529668L, durationByState.get(aggregatedKey2));
                ProtoUtils.toAggStatsProto(proto, 1146756268040L, 0L, 0L, 0L, (int) meanRssByState.get(aggregatedKey2), (int) maxRssByState.get(aggregatedKey2));
                proto.end(stateToken);
                i++;
                token = token;
                meanRssByState = meanRssByState;
                maxRssByState = maxRssByState;
            }
            this.mStats.dumpFilteredAssociationStatesProtoForProc(proto, 2246267895815L, now, this, uidToPkgMap);
            proto.end(token);
        }
        z = true;
        writeCompressedProcessName(proto, 1138166333441L, procName, str, z);
        proto.write(1120986464258L, uid);
        i = 0;
        while (i < durationByState.size()) {
        }
        this.mStats.dumpFilteredAssociationStatesProtoForProc(proto, 2246267895815L, now, this, uidToPkgMap);
        proto.end(token);
    }
}
