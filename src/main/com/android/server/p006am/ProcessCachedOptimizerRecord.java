package com.android.server.p006am;

import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p006am.CachedAppOptimizer;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.ProcessCachedOptimizerRecord */
/* loaded from: classes.dex */
public final class ProcessCachedOptimizerRecord {
    @VisibleForTesting
    static final String IS_FROZEN = "isFrozen";
    public final ProcessRecord mApp;
    @GuardedBy({"mProcLock"})
    public boolean mForceCompact;
    @GuardedBy({"mProcLock"})
    public boolean mFreezeExempt;
    @GuardedBy({"mProcLock"})
    public long mFreezeUnfreezeTime;
    @GuardedBy({"mProcLock"})
    public boolean mFreezerOverride;
    @GuardedBy({"mProcLock"})
    public boolean mFrozen;
    public boolean mHasCollectedFrozenPSS;
    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactProfile mLastCompactProfile;
    @GuardedBy({"mProcLock"})
    public long mLastCompactTime;
    @GuardedBy({"mProcLock"})
    public int mLastOomAdjChangeReason;
    @GuardedBy({"mProcLock"})
    public boolean mPendingCompact;
    @GuardedBy({"mProcLock"})
    public boolean mPendingFreeze;
    public final ActivityManagerGlobalLock mProcLock;
    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactProfile mReqCompactProfile;
    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactSource mReqCompactSource;
    @GuardedBy({"mProcLock"})
    public boolean mShouldNotFreeze;

    @GuardedBy({"mProcLock"})
    public long getLastCompactTime() {
        return this.mLastCompactTime;
    }

    @GuardedBy({"mProcLock"})
    public void setLastCompactTime(long j) {
        this.mLastCompactTime = j;
    }

    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactProfile getReqCompactProfile() {
        return this.mReqCompactProfile;
    }

    @GuardedBy({"mProcLock"})
    public void setReqCompactProfile(CachedAppOptimizer.CompactProfile compactProfile) {
        this.mReqCompactProfile = compactProfile;
    }

    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactSource getReqCompactSource() {
        return this.mReqCompactSource;
    }

    @GuardedBy({"mProcLock"})
    public void setReqCompactSource(CachedAppOptimizer.CompactSource compactSource) {
        this.mReqCompactSource = compactSource;
    }

    @GuardedBy({"mProcLock"})
    public void setLastOomAdjChangeReason(int i) {
        this.mLastOomAdjChangeReason = i;
    }

    @GuardedBy({"mProcLock"})
    public int getLastOomAdjChangeReason() {
        return this.mLastOomAdjChangeReason;
    }

    @GuardedBy({"mProcLock"})
    public CachedAppOptimizer.CompactProfile getLastCompactProfile() {
        if (this.mLastCompactProfile == null) {
            this.mLastCompactProfile = CachedAppOptimizer.CompactProfile.SOME;
        }
        return this.mLastCompactProfile;
    }

    @GuardedBy({"mProcLock"})
    public void setLastCompactProfile(CachedAppOptimizer.CompactProfile compactProfile) {
        this.mLastCompactProfile = compactProfile;
    }

    @GuardedBy({"mProcLock"})
    public boolean hasPendingCompact() {
        return this.mPendingCompact;
    }

    @GuardedBy({"mProcLock"})
    public void setHasPendingCompact(boolean z) {
        this.mPendingCompact = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean isForceCompact() {
        return this.mForceCompact;
    }

    @GuardedBy({"mProcLock"})
    public void setForceCompact(boolean z) {
        this.mForceCompact = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean isFrozen() {
        return this.mFrozen;
    }

    @GuardedBy({"mProcLock"})
    public void setFrozen(boolean z) {
        this.mFrozen = z;
    }

    public boolean skipPSSCollectionBecauseFrozen() {
        boolean z = this.mHasCollectedFrozenPSS;
        if (this.mFrozen) {
            this.mHasCollectedFrozenPSS = true;
            return z;
        }
        return false;
    }

    public void setHasCollectedFrozenPSS(boolean z) {
        this.mHasCollectedFrozenPSS = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean hasFreezerOverride() {
        return this.mFreezerOverride;
    }

    @GuardedBy({"mProcLock"})
    public void setFreezerOverride(boolean z) {
        this.mFreezerOverride = z;
    }

    @GuardedBy({"mProcLock"})
    public long getFreezeUnfreezeTime() {
        return this.mFreezeUnfreezeTime;
    }

    @GuardedBy({"mProcLock"})
    public void setFreezeUnfreezeTime(long j) {
        this.mFreezeUnfreezeTime = j;
    }

    @GuardedBy({"mProcLock"})
    public boolean shouldNotFreeze() {
        return this.mShouldNotFreeze;
    }

    @GuardedBy({"mProcLock"})
    public void setShouldNotFreeze(boolean z) {
        this.mShouldNotFreeze = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean isFreezeExempt() {
        return this.mFreezeExempt;
    }

    @GuardedBy({"mProcLock"})
    public void setPendingFreeze(boolean z) {
        this.mPendingFreeze = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean isPendingFreeze() {
        return this.mPendingFreeze;
    }

    @GuardedBy({"mProcLock"})
    public void setFreezeExempt(boolean z) {
        this.mFreezeExempt = z;
    }

    public ProcessCachedOptimizerRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        this.mProcLock = processRecord.mService.mProcLock;
    }

    public void init(long j) {
        this.mFreezeUnfreezeTime = j;
    }

    @GuardedBy({"mProcLock"})
    public void dump(PrintWriter printWriter, String str, long j) {
        printWriter.print(str);
        printWriter.print("lastCompactTime=");
        printWriter.print(this.mLastCompactTime);
        printWriter.print(" lastCompactProfile=");
        printWriter.println(this.mLastCompactProfile);
        printWriter.print(str);
        printWriter.print("hasPendingCompaction=");
        printWriter.print(this.mPendingCompact);
        printWriter.print(str);
        printWriter.print("isFreezeExempt=");
        printWriter.print(this.mFreezeExempt);
        printWriter.print(" isPendingFreeze=");
        printWriter.print(this.mPendingFreeze);
        printWriter.print(" isFrozen=");
        printWriter.println(this.mFrozen);
    }
}
