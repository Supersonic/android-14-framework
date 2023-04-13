package com.android.server.p006am;

import android.app.ActivityManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p006am.UidObserverController;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.UidRecord */
/* loaded from: classes.dex */
public final class UidRecord {
    public static int[] ORIG_ENUMS = {1, 2, 4, 8, 16, 32, Integer.MIN_VALUE};
    public static int[] PROTO_ENUMS = {0, 1, 2, 3, 4, 5, 6};
    @GuardedBy({"networkStateUpdate"})
    public long curProcStateSeq;
    public volatile boolean hasInternetPermission;
    @GuardedBy({"networkStateUpdate"})
    public long lastNetworkUpdatedProcStateSeq;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mCurAllowList;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurCapability;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurProcState;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mEphemeral;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mForegroundServices;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mIdle;
    @CompositeRWLock({"mService", "mProcLock"})
    public long mLastBackgroundTime;
    @GuardedBy({"mService"})
    public int mLastReportedChange;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mNumProcs;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mProcAdjChanged;
    public final ActivityManagerGlobalLock mProcLock;
    public final ActivityManagerService mService;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mSetAllowList;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetCapability;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mSetIdle;
    public final int mUid;
    public volatile long procStateSeqWaitingForNetwork;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetProcState = 20;
    @CompositeRWLock({"mService", "mProcLock"})
    public ArraySet<ProcessRecord> mProcRecords = new ArraySet<>();
    public final Object networkStateLock = new Object();
    public final UidObserverController.ChangeRecord pendingChange = new UidObserverController.ChangeRecord();

    public UidRecord(int i, ActivityManagerService activityManagerService) {
        this.mUid = i;
        this.mService = activityManagerService;
        this.mProcLock = activityManagerService != null ? activityManagerService.mProcLock : null;
        this.mIdle = true;
        reset();
    }

    public int getUid() {
        return this.mUid;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurProcState() {
        return this.mCurProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurProcState(int i) {
        this.mCurProcState = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetProcState() {
        return this.mSetProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetProcState(int i) {
        this.mSetProcState = i;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void noteProcAdjChanged() {
        this.mProcAdjChanged = true;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void clearProcAdjChanged() {
        this.mProcAdjChanged = false;
    }

    @GuardedBy({"mService", "mProcLock"})
    public boolean getProcAdjChanged() {
        return this.mProcAdjChanged;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurCapability() {
        return this.mCurCapability;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurCapability(int i) {
        this.mCurCapability = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetCapability() {
        return this.mSetCapability;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetCapability(int i) {
        this.mSetCapability = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public long getLastBackgroundTime() {
        return this.mLastBackgroundTime;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setLastBackgroundTime(long j) {
        this.mLastBackgroundTime = j;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isEphemeral() {
        return this.mEphemeral;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setEphemeral(boolean z) {
        this.mEphemeral = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean hasForegroundServices() {
        return this.mForegroundServices;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setForegroundServices(boolean z) {
        this.mForegroundServices = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isCurAllowListed() {
        return this.mCurAllowList;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurAllowListed(boolean z) {
        this.mCurAllowList = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isSetAllowListed() {
        return this.mSetAllowList;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetAllowListed(boolean z) {
        this.mSetAllowList = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isIdle() {
        return this.mIdle;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setIdle(boolean z) {
        this.mIdle = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isSetIdle() {
        return this.mSetIdle;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetIdle(boolean z) {
        this.mSetIdle = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getNumOfProcs() {
        return this.mProcRecords.size();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void forEachProcess(Consumer<ProcessRecord> consumer) {
        for (int size = this.mProcRecords.size() - 1; size >= 0; size--) {
            consumer.accept(this.mProcRecords.valueAt(size));
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ProcessRecord getProcessInPackage(String str) {
        for (int size = this.mProcRecords.size() - 1; size >= 0; size--) {
            ProcessRecord valueAt = this.mProcRecords.valueAt(size);
            if (valueAt != null && TextUtils.equals(valueAt.info.packageName, str)) {
                return valueAt;
            }
        }
        return null;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void addProcess(ProcessRecord processRecord) {
        this.mProcRecords.add(processRecord);
    }

    @GuardedBy({"mService", "mProcLock"})
    public void removeProcess(ProcessRecord processRecord) {
        this.mProcRecords.remove(processRecord);
    }

    @GuardedBy({"mService"})
    public void setLastReportedChange(int i) {
        this.mLastReportedChange = i;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void reset() {
        setCurProcState(19);
        this.mForegroundServices = false;
        this.mCurCapability = 0;
    }

    public void updateHasInternetPermission() {
        this.hasInternetPermission = ActivityManager.checkUidPermission("android.permission.INTERNET", this.mUid) == 0;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.mUid);
        protoOutputStream.write(1159641169922L, ProcessList.makeProcStateProtoEnum(this.mCurProcState));
        protoOutputStream.write(1133871366147L, this.mEphemeral);
        protoOutputStream.write(1133871366148L, this.mForegroundServices);
        protoOutputStream.write(1133871366149L, this.mCurAllowList);
        ProtoUtils.toDuration(protoOutputStream, 1146756268038L, this.mLastBackgroundTime, SystemClock.elapsedRealtime());
        protoOutputStream.write(1133871366151L, this.mIdle);
        int i = this.mLastReportedChange;
        if (i != 0) {
            ProtoUtils.writeBitWiseFlagsToProtoEnum(protoOutputStream, 2259152797704L, i, ORIG_ENUMS, PROTO_ENUMS);
        }
        protoOutputStream.write(1120986464265L, this.mNumProcs);
        long start2 = protoOutputStream.start(1146756268042L);
        protoOutputStream.write(1112396529665L, this.curProcStateSeq);
        protoOutputStream.write(1112396529666L, this.lastNetworkUpdatedProcStateSeq);
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }

    public String toString() {
        boolean z;
        StringBuilder sb = new StringBuilder(128);
        sb.append("UidRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        UserHandle.formatUid(sb, this.mUid);
        sb.append(' ');
        sb.append(ProcessList.makeProcStateString(this.mCurProcState));
        if (this.mEphemeral) {
            sb.append(" ephemeral");
        }
        if (this.mForegroundServices) {
            sb.append(" fgServices");
        }
        if (this.mCurAllowList) {
            sb.append(" allowlist");
        }
        if (this.mLastBackgroundTime > 0) {
            sb.append(" bg:");
            TimeUtils.formatDuration(SystemClock.elapsedRealtime() - this.mLastBackgroundTime, sb);
        }
        if (this.mIdle) {
            sb.append(" idle");
        }
        if (this.mLastReportedChange != 0) {
            sb.append(" change:");
            boolean z2 = true;
            if ((this.mLastReportedChange & 1) != 0) {
                sb.append("gone");
                z = true;
            } else {
                z = false;
            }
            if ((this.mLastReportedChange & 2) != 0) {
                if (z) {
                    sb.append("|");
                }
                sb.append("idle");
                z = true;
            }
            if ((this.mLastReportedChange & 4) != 0) {
                if (z) {
                    sb.append("|");
                }
                sb.append("active");
                z = true;
            }
            if ((this.mLastReportedChange & 8) != 0) {
                if (z) {
                    sb.append("|");
                }
                sb.append("cached");
            } else {
                z2 = z;
            }
            if ((this.mLastReportedChange & 16) != 0) {
                if (z2) {
                    sb.append("|");
                }
                sb.append("uncached");
            }
            if ((this.mLastReportedChange & Integer.MIN_VALUE) != 0) {
                if (z2) {
                    sb.append("|");
                }
                sb.append("procstate");
            }
            if ((this.mLastReportedChange & 64) != 0) {
                if (z2) {
                    sb.append("|");
                }
                sb.append("procadj");
            }
        }
        sb.append(" procs:");
        sb.append(this.mNumProcs);
        sb.append(" seq(");
        sb.append(this.curProcStateSeq);
        sb.append(",");
        sb.append(this.lastNetworkUpdatedProcStateSeq);
        sb.append(")}");
        sb.append(" caps=");
        ActivityManager.printCapabilitiesSummary(sb, this.mCurCapability);
        return sb.toString();
    }
}
