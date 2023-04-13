package com.android.server.p006am;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.os.SystemClock;
import android.util.TimeUtils;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p006am.OomAdjuster;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.ProcessStateRecord */
/* loaded from: classes.dex */
public final class ProcessStateRecord {
    @GuardedBy({"mService"})
    public int mAdjSeq;
    @CompositeRWLock({"mService", "mProcLock"})
    public Object mAdjSource;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mAdjSourceProcState;
    @CompositeRWLock({"mService", "mProcLock"})
    public Object mAdjTarget;
    @GuardedBy({"mService"})
    public String mAdjType;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mAdjTypeCode;
    public final ProcessRecord mApp;
    @GuardedBy({"mService"})
    public long mCacheOomRankerRss;
    @GuardedBy({"mService"})
    public long mCacheOomRankerRssTimeMs;
    @GuardedBy({"mService"})
    public int mCacheOomRankerUseCount;
    @GuardedBy({"mService"})
    public boolean mCached;
    @GuardedBy({"mService"})
    public int mCompletedAdjSeq;
    @GuardedBy({"mService"})
    public boolean mContainsCycle;
    @GuardedBy({"mService"})
    public boolean mEmpty;
    @CompositeRWLock({"mService", "mProcLock"})
    public long mFgInteractionTime;
    @GuardedBy({"mService"})
    public Object mForcingToImportant;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mHasForegroundActivities;
    @GuardedBy({"mService"})
    public boolean mHasOverlayUi;
    @GuardedBy({"mService"})
    public boolean mHasShownUi;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mHasStartedServices;
    @GuardedBy({"mService"})
    public boolean mHasTopUi;
    @CompositeRWLock({"mService", "mProcLock"})
    public long mInteractionEventTime;
    @GuardedBy({"mService"})
    public long mLastCanKillOnBgRestrictedAndIdleTime;
    @GuardedBy({"mService"})
    public long mLastInvisibleTime;
    @CompositeRWLock({"mService", "mProcLock"})
    public long mLastStateTime;
    @GuardedBy({"mService"})
    public long mLastTopTime;
    @GuardedBy({"mService"})
    public boolean mNoKillOnBgRestrictedAndIdle;
    @GuardedBy({"mProcLock"})
    public boolean mNotCachedSinceIdle;
    public final ActivityManagerGlobalLock mProcLock;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mProcStateChanged;
    @GuardedBy({"mService"})
    public boolean mReachable;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mRepForegroundActivities;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mReportedInteraction;
    @GuardedBy({"mService"})
    public boolean mRunningRemoteAnimation;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSavedPriority;
    public final ActivityManagerService mService;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mServiceB;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mServiceHighRam;
    @GuardedBy({"mService"})
    public boolean mSetCached;
    @GuardedBy({"mService"})
    public boolean mSetNoKillOnBgRestrictedAndIdle;
    @GuardedBy({"mService"})
    public boolean mSystemNoUi;
    @CompositeRWLock({"mService", "mProcLock"})
    public long mWhenUnimportant;
    @GuardedBy({"mService"})
    public int mMaxAdj = 1001;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurRawAdj = -10000;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetRawAdj = -10000;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurAdj = -10000;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetAdj = -10000;
    @GuardedBy({"mService"})
    public int mVerifiedAdj = -10000;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurCapability = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetCapability = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurSchedGroup = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetSchedGroup = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurProcState = 20;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mRepProcState = 20;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mCurRawProcState = 20;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mSetProcState = 20;
    @GuardedBy({"mService"})
    public boolean mBackgroundRestricted = false;
    @GuardedBy({"mService"})
    public boolean mCurBoundByNonBgRestrictedApp = false;
    public boolean mSetBoundByNonBgRestrictedApp = false;
    @GuardedBy({"mService"})
    public int mCachedHasActivities = -1;
    @GuardedBy({"mService"})
    public int mCachedIsHeavyWeight = -1;
    @GuardedBy({"mService"})
    public int mCachedHasVisibleActivities = -1;
    @GuardedBy({"mService"})
    public int mCachedIsHomeProcess = -1;
    @GuardedBy({"mService"})
    public int mCachedIsPreviousProcess = -1;
    @GuardedBy({"mService"})
    public int mCachedHasRecentTasks = -1;
    @GuardedBy({"mService"})
    public int mCachedIsReceivingBroadcast = -1;
    @GuardedBy({"mService"})
    public int[] mCachedCompatChanges = {-1, -1, -1};
    @GuardedBy({"mService"})
    public int mCachedAdj = -10000;
    @GuardedBy({"mService"})
    public boolean mCachedForegroundActivities = false;
    @GuardedBy({"mService"})
    public int mCachedProcState = 19;
    @GuardedBy({"mService"})
    public int mCachedSchedGroup = 0;

    public ProcessStateRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        ActivityManagerService activityManagerService = processRecord.mService;
        this.mService = activityManagerService;
        this.mProcLock = activityManagerService.mProcLock;
    }

    public void init(long j) {
        this.mLastStateTime = j;
    }

    @GuardedBy({"mService"})
    public void setMaxAdj(int i) {
        this.mMaxAdj = i;
    }

    @GuardedBy({"mService"})
    public int getMaxAdj() {
        return this.mMaxAdj;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurRawAdj(int i) {
        this.mCurRawAdj = i;
        this.mApp.getWindowProcessController().setPerceptible(i <= 200);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurRawAdj() {
        return this.mCurRawAdj;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetRawAdj(int i) {
        this.mSetRawAdj = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetRawAdj() {
        return this.mSetRawAdj;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurAdj(int i) {
        this.mCurAdj = i;
        this.mApp.getWindowProcessController().setCurrentAdj(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurAdj() {
        return this.mCurAdj;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetAdj(int i) {
        this.mSetAdj = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetAdj() {
        return this.mSetAdj;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetAdjWithServices() {
        int i = this.mSetAdj;
        if (i < 900 || !this.mHasStartedServices) {
            return i;
        }
        return 800;
    }

    @GuardedBy({"mService"})
    public void setVerifiedAdj(int i) {
        this.mVerifiedAdj = i;
    }

    @GuardedBy({"mService"})
    public int getVerifiedAdj() {
        return this.mVerifiedAdj;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurCapability(int i) {
        this.mCurCapability = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurCapability() {
        return this.mCurCapability;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetCapability(int i) {
        this.mSetCapability = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetCapability() {
        return this.mSetCapability;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurrentSchedulingGroup(int i) {
        this.mCurSchedGroup = i;
        this.mApp.getWindowProcessController().setCurrentSchedulingGroup(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurrentSchedulingGroup() {
        return this.mCurSchedGroup;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetSchedGroup(int i) {
        this.mSetSchedGroup = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetSchedGroup() {
        return this.mSetSchedGroup;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurProcState(int i) {
        this.mCurProcState = i;
        this.mApp.getWindowProcessController().setCurrentProcState(this.mCurProcState);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurProcState() {
        return this.mCurProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCurRawProcState(int i) {
        this.mCurRawProcState = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getCurRawProcState() {
        return this.mCurRawProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setReportedProcState(int i) {
        this.mRepProcState = i;
        this.mApp.getWindowProcessController().setReportedProcState(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getReportedProcState() {
        return this.mRepProcState;
    }

    @GuardedBy({"mService"})
    public void forceProcessStateUpTo(int i) {
        if (this.mRepProcState > i) {
            synchronized (this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    this.mRepProcState = i;
                    setCurProcState(i);
                    setCurRawProcState(i);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSetProcState(int i) {
        if (ActivityManager.isProcStateCached(this.mSetProcState) && !ActivityManager.isProcStateCached(i)) {
            this.mCacheOomRankerUseCount++;
        }
        this.mSetProcState = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSetProcState() {
        return this.mSetProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setLastStateTime(long j) {
        this.mLastStateTime = j;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public long getLastStateTime() {
        return this.mLastStateTime;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setSavedPriority(int i) {
        this.mSavedPriority = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getSavedPriority() {
        return this.mSavedPriority;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setServiceB(boolean z) {
        this.mServiceB = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isServiceB() {
        return this.mServiceB;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setServiceHighRam(boolean z) {
        this.mServiceHighRam = z;
    }

    @GuardedBy({"mProcLock"})
    public void setNotCachedSinceIdle(boolean z) {
        this.mNotCachedSinceIdle = z;
    }

    @GuardedBy({"mProcLock"})
    public boolean isNotCachedSinceIdle() {
        return this.mNotCachedSinceIdle;
    }

    @GuardedBy({"mProcLock"})
    public void setHasStartedServices(boolean z) {
        this.mHasStartedServices = z;
        if (z) {
            this.mApp.mProfile.addHostingComponentType(128);
        } else {
            this.mApp.mProfile.clearHostingComponentType(128);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setHasForegroundActivities(boolean z) {
        this.mHasForegroundActivities = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean hasForegroundActivities() {
        return this.mHasForegroundActivities;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setRepForegroundActivities(boolean z) {
        this.mRepForegroundActivities = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean hasRepForegroundActivities() {
        return this.mRepForegroundActivities;
    }

    @GuardedBy({"mService"})
    public void setHasShownUi(boolean z) {
        this.mHasShownUi = z;
    }

    @GuardedBy({"mService"})
    public boolean hasShownUi() {
        return this.mHasShownUi;
    }

    @GuardedBy({"mService"})
    public void setHasTopUi(boolean z) {
        this.mHasTopUi = z;
        this.mApp.getWindowProcessController().setHasTopUi(z);
    }

    @GuardedBy({"mService"})
    public boolean hasTopUi() {
        return this.mHasTopUi;
    }

    @GuardedBy({"mService"})
    public void setHasOverlayUi(boolean z) {
        this.mHasOverlayUi = z;
        this.mApp.getWindowProcessController().setHasOverlayUi(z);
    }

    @GuardedBy({"mService"})
    public boolean hasOverlayUi() {
        return this.mHasOverlayUi;
    }

    @GuardedBy({"mService"})
    public boolean isRunningRemoteAnimation() {
        return this.mRunningRemoteAnimation;
    }

    @GuardedBy({"mService"})
    public void setRunningRemoteAnimation(boolean z) {
        if (this.mRunningRemoteAnimation == z) {
            return;
        }
        this.mRunningRemoteAnimation = z;
        this.mService.updateOomAdjLocked(this.mApp, 9);
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setProcStateChanged(boolean z) {
        this.mProcStateChanged = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean hasProcStateChanged() {
        return this.mProcStateChanged;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setReportedInteraction(boolean z) {
        this.mReportedInteraction = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean hasReportedInteraction() {
        return this.mReportedInteraction;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setInteractionEventTime(long j) {
        this.mInteractionEventTime = j;
        this.mApp.getWindowProcessController().setInteractionEventTime(j);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public long getInteractionEventTime() {
        return this.mInteractionEventTime;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setFgInteractionTime(long j) {
        this.mFgInteractionTime = j;
        this.mApp.getWindowProcessController().setFgInteractionTime(j);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public long getFgInteractionTime() {
        return this.mFgInteractionTime;
    }

    @GuardedBy({"mService"})
    public void setForcingToImportant(Object obj) {
        this.mForcingToImportant = obj;
    }

    @GuardedBy({"mService"})
    public Object getForcingToImportant() {
        return this.mForcingToImportant;
    }

    @GuardedBy({"mService"})
    public void setAdjSeq(int i) {
        this.mAdjSeq = i;
    }

    @GuardedBy({"mService"})
    public void decAdjSeq() {
        this.mAdjSeq--;
    }

    @GuardedBy({"mService"})
    public int getAdjSeq() {
        return this.mAdjSeq;
    }

    @GuardedBy({"mService"})
    public void setCompletedAdjSeq(int i) {
        this.mCompletedAdjSeq = i;
    }

    @GuardedBy({"mService"})
    public void decCompletedAdjSeq() {
        this.mCompletedAdjSeq--;
    }

    @GuardedBy({"mService"})
    public int getCompletedAdjSeq() {
        return this.mCompletedAdjSeq;
    }

    @GuardedBy({"mService"})
    public void setContainsCycle(boolean z) {
        this.mContainsCycle = z;
    }

    @GuardedBy({"mService"})
    public boolean containsCycle() {
        return this.mContainsCycle;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setWhenUnimportant(long j) {
        this.mWhenUnimportant = j;
        this.mApp.getWindowProcessController().setWhenUnimportant(j);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public long getWhenUnimportant() {
        return this.mWhenUnimportant;
    }

    @GuardedBy({"mService"})
    public void setLastTopTime(long j) {
        this.mLastTopTime = j;
    }

    @GuardedBy({"mService"})
    public long getLastTopTime() {
        return this.mLastTopTime;
    }

    @GuardedBy({"mService"})
    public void setEmpty(boolean z) {
        this.mEmpty = z;
    }

    @GuardedBy({"mService"})
    public boolean isEmpty() {
        return this.mEmpty;
    }

    @GuardedBy({"mService"})
    public void setCached(boolean z) {
        this.mCached = z;
    }

    @GuardedBy({"mService"})
    public boolean isCached() {
        return this.mCached;
    }

    @GuardedBy({"mService"})
    public int getCacheOomRankerUseCount() {
        return this.mCacheOomRankerUseCount;
    }

    @GuardedBy({"mService"})
    public void setSystemNoUi(boolean z) {
        this.mSystemNoUi = z;
    }

    @GuardedBy({"mService"})
    public boolean isSystemNoUi() {
        return this.mSystemNoUi;
    }

    @GuardedBy({"mService"})
    public void setAdjType(String str) {
        this.mAdjType = str;
    }

    @GuardedBy({"mService"})
    public String getAdjType() {
        return this.mAdjType;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAdjTypeCode(int i) {
        this.mAdjTypeCode = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getAdjTypeCode() {
        return this.mAdjTypeCode;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAdjSource(Object obj) {
        this.mAdjSource = obj;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public Object getAdjSource() {
        return this.mAdjSource;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAdjSourceProcState(int i) {
        this.mAdjSourceProcState = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getAdjSourceProcState() {
        return this.mAdjSourceProcState;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAdjTarget(Object obj) {
        this.mAdjTarget = obj;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public Object getAdjTarget() {
        return this.mAdjTarget;
    }

    @GuardedBy({"mService"})
    public boolean isReachable() {
        return this.mReachable;
    }

    @GuardedBy({"mService"})
    public void setReachable(boolean z) {
        this.mReachable = z;
    }

    @GuardedBy({"mService"})
    public void resetCachedInfo() {
        this.mCachedHasActivities = -1;
        this.mCachedIsHeavyWeight = -1;
        this.mCachedHasVisibleActivities = -1;
        this.mCachedIsHomeProcess = -1;
        this.mCachedIsPreviousProcess = -1;
        this.mCachedHasRecentTasks = -1;
        this.mCachedIsReceivingBroadcast = -1;
        this.mCachedAdj = -10000;
        this.mCachedForegroundActivities = false;
        this.mCachedProcState = 19;
        this.mCachedSchedGroup = 0;
    }

    @GuardedBy({"mService"})
    public boolean getCachedHasActivities() {
        if (this.mCachedHasActivities == -1) {
            boolean hasActivities = this.mApp.getWindowProcessController().hasActivities();
            this.mCachedHasActivities = hasActivities ? 1 : 0;
            if (hasActivities) {
                this.mApp.mProfile.addHostingComponentType(16);
            } else {
                this.mApp.mProfile.clearHostingComponentType(16);
            }
        }
        return this.mCachedHasActivities == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedIsHeavyWeight() {
        if (this.mCachedIsHeavyWeight == -1) {
            this.mCachedIsHeavyWeight = this.mApp.getWindowProcessController().isHeavyWeightProcess() ? 1 : 0;
        }
        return this.mCachedIsHeavyWeight == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedHasVisibleActivities() {
        if (this.mCachedHasVisibleActivities == -1) {
            this.mCachedHasVisibleActivities = this.mApp.getWindowProcessController().hasVisibleActivities() ? 1 : 0;
        }
        return this.mCachedHasVisibleActivities == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedIsHomeProcess() {
        if (this.mCachedIsHomeProcess == -1) {
            if (this.mApp.getWindowProcessController().isHomeProcess()) {
                this.mCachedIsHomeProcess = 1;
                this.mService.mAppProfiler.mHasHomeProcess = true;
            } else {
                this.mCachedIsHomeProcess = 0;
            }
        }
        return this.mCachedIsHomeProcess == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedIsPreviousProcess() {
        if (this.mCachedIsPreviousProcess == -1) {
            if (this.mApp.getWindowProcessController().isPreviousProcess()) {
                this.mCachedIsPreviousProcess = 1;
                this.mService.mAppProfiler.mHasPreviousProcess = true;
            } else {
                this.mCachedIsPreviousProcess = 0;
            }
        }
        return this.mCachedIsPreviousProcess == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedHasRecentTasks() {
        if (this.mCachedHasRecentTasks == -1) {
            this.mCachedHasRecentTasks = this.mApp.getWindowProcessController().hasRecentTasks() ? 1 : 0;
        }
        return this.mCachedHasRecentTasks == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedIsReceivingBroadcast(int[] iArr) {
        if (this.mCachedIsReceivingBroadcast == -1) {
            boolean isReceivingBroadcastLocked = this.mService.isReceivingBroadcastLocked(this.mApp, iArr);
            this.mCachedIsReceivingBroadcast = isReceivingBroadcastLocked ? 1 : 0;
            if (isReceivingBroadcastLocked) {
                this.mCachedSchedGroup = iArr[0];
                this.mApp.mProfile.addHostingComponentType(32);
            } else {
                this.mApp.mProfile.clearHostingComponentType(32);
            }
        }
        return this.mCachedIsReceivingBroadcast == 1;
    }

    @GuardedBy({"mService"})
    public boolean getCachedCompatChange(int i) {
        int[] iArr = this.mCachedCompatChanges;
        if (iArr[i] == -1) {
            iArr[i] = this.mService.mOomAdjuster.isChangeEnabled(i, this.mApp.info, false) ? 1 : 0;
        }
        return this.mCachedCompatChanges[i] == 1;
    }

    @GuardedBy({"mService"})
    public void computeOomAdjFromActivitiesIfNecessary(OomAdjuster.ComputeOomAdjWindowCallback computeOomAdjWindowCallback, int i, boolean z, boolean z2, int i2, int i3, int i4, int i5, int i6) {
        if (this.mCachedAdj != -10000) {
            return;
        }
        computeOomAdjWindowCallback.initialize(this.mApp, i, z, z2, i2, i3, i4, i5, i6);
        int min = Math.min(99, this.mApp.getWindowProcessController().computeOomAdjFromActivities(computeOomAdjWindowCallback));
        int i7 = computeOomAdjWindowCallback.adj;
        this.mCachedAdj = i7;
        this.mCachedForegroundActivities = computeOomAdjWindowCallback.foregroundActivities;
        this.mCachedHasVisibleActivities = computeOomAdjWindowCallback.mHasVisibleActivities ? 1 : 0;
        this.mCachedProcState = computeOomAdjWindowCallback.procState;
        this.mCachedSchedGroup = computeOomAdjWindowCallback.schedGroup;
        if (i7 == 100) {
            this.mCachedAdj = i7 + min;
        }
    }

    @GuardedBy({"mService"})
    public int getCachedAdj() {
        return this.mCachedAdj;
    }

    @GuardedBy({"mService"})
    public boolean getCachedForegroundActivities() {
        return this.mCachedForegroundActivities;
    }

    @GuardedBy({"mService"})
    public int getCachedProcState() {
        return this.mCachedProcState;
    }

    @GuardedBy({"mService"})
    public int getCachedSchedGroup() {
        return this.mCachedSchedGroup;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public String makeAdjReason() {
        if (this.mAdjSource == null && this.mAdjTarget == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append(' ');
        Object obj = this.mAdjTarget;
        if (obj instanceof ComponentName) {
            sb.append(((ComponentName) obj).flattenToShortString());
        } else if (obj != null) {
            sb.append(obj.toString());
        } else {
            sb.append("{null}");
        }
        sb.append("<=");
        Object obj2 = this.mAdjSource;
        if (obj2 instanceof ProcessRecord) {
            sb.append("Proc{");
            sb.append(((ProcessRecord) this.mAdjSource).toShortString());
            sb.append("}");
        } else if (obj2 != null) {
            sb.append(obj2.toString());
        } else {
            sb.append("{null}");
        }
        return sb.toString();
    }

    @GuardedBy({"mService", "mProcLock"})
    public void onCleanupApplicationRecordLSP() {
        int i = 0;
        setHasForegroundActivities(false);
        this.mHasShownUi = false;
        this.mForcingToImportant = null;
        this.mVerifiedAdj = -10000;
        this.mSetAdj = -10000;
        this.mCurAdj = -10000;
        this.mSetRawAdj = -10000;
        this.mCurRawAdj = -10000;
        this.mSetCapability = 0;
        this.mCurCapability = 0;
        this.mSetSchedGroup = 0;
        this.mCurSchedGroup = 0;
        this.mSetProcState = 20;
        this.mCurRawProcState = 20;
        this.mCurProcState = 20;
        while (true) {
            int[] iArr = this.mCachedCompatChanges;
            if (i >= iArr.length) {
                return;
            }
            iArr[i] = -1;
            i++;
        }
    }

    @GuardedBy({"mService"})
    public boolean isBackgroundRestricted() {
        return this.mBackgroundRestricted;
    }

    @GuardedBy({"mService"})
    public void setBackgroundRestricted(boolean z) {
        this.mBackgroundRestricted = z;
    }

    @GuardedBy({"mService"})
    public boolean isCurBoundByNonBgRestrictedApp() {
        return this.mCurBoundByNonBgRestrictedApp;
    }

    @GuardedBy({"mService"})
    public void setCurBoundByNonBgRestrictedApp(boolean z) {
        this.mCurBoundByNonBgRestrictedApp = z;
    }

    @GuardedBy({"mService"})
    public boolean isSetBoundByNonBgRestrictedApp() {
        return this.mSetBoundByNonBgRestrictedApp;
    }

    @GuardedBy({"mService"})
    public void setSetBoundByNonBgRestrictedApp(boolean z) {
        this.mSetBoundByNonBgRestrictedApp = z;
    }

    @GuardedBy({"mService"})
    public void updateLastInvisibleTime(boolean z) {
        if (z) {
            this.mLastInvisibleTime = Long.MAX_VALUE;
        } else if (this.mLastInvisibleTime == Long.MAX_VALUE) {
            this.mLastInvisibleTime = SystemClock.elapsedRealtime();
        }
    }

    @GuardedBy({"mService"})
    public long getLastInvisibleTime() {
        return this.mLastInvisibleTime;
    }

    @GuardedBy({"mService"})
    public void setNoKillOnBgRestrictedAndIdle(boolean z) {
        this.mNoKillOnBgRestrictedAndIdle = z;
    }

    @GuardedBy({"mService"})
    public boolean shouldNotKillOnBgRestrictedAndIdle() {
        return this.mNoKillOnBgRestrictedAndIdle;
    }

    @GuardedBy({"mService"})
    public void setSetCached(boolean z) {
        this.mSetCached = z;
    }

    @GuardedBy({"mService"})
    public boolean isSetCached() {
        return this.mSetCached;
    }

    @GuardedBy({"mService"})
    public void setSetNoKillOnBgRestrictedAndIdle(boolean z) {
        this.mSetNoKillOnBgRestrictedAndIdle = z;
    }

    @GuardedBy({"mService"})
    public boolean isSetNoKillOnBgRestrictedAndIdle() {
        return this.mSetNoKillOnBgRestrictedAndIdle;
    }

    @GuardedBy({"mService"})
    public void setLastCanKillOnBgRestrictedAndIdleTime(long j) {
        this.mLastCanKillOnBgRestrictedAndIdleTime = j;
    }

    @GuardedBy({"mService"})
    public long getLastCanKillOnBgRestrictedAndIdleTime() {
        return this.mLastCanKillOnBgRestrictedAndIdleTime;
    }

    public void setCacheOomRankerRss(long j, long j2) {
        this.mCacheOomRankerRss = j;
        this.mCacheOomRankerRssTimeMs = j2;
    }

    @GuardedBy({"mService"})
    public long getCacheOomRankerRss() {
        return this.mCacheOomRankerRss;
    }

    @GuardedBy({"mService"})
    public long getCacheOomRankerRssTimeMs() {
        return this.mCacheOomRankerRssTimeMs;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void dump(PrintWriter printWriter, String str, long j) {
        if (this.mReportedInteraction || this.mFgInteractionTime != 0) {
            printWriter.print(str);
            printWriter.print("reportedInteraction=");
            printWriter.print(this.mReportedInteraction);
            if (this.mInteractionEventTime != 0) {
                printWriter.print(" time=");
                TimeUtils.formatDuration(this.mInteractionEventTime, SystemClock.elapsedRealtime(), printWriter);
            }
            if (this.mFgInteractionTime != 0) {
                printWriter.print(" fgInteractionTime=");
                TimeUtils.formatDuration(this.mFgInteractionTime, SystemClock.elapsedRealtime(), printWriter);
            }
            printWriter.println();
        }
        printWriter.print(str);
        printWriter.print("adjSeq=");
        printWriter.print(this.mAdjSeq);
        printWriter.print(" lruSeq=");
        printWriter.println(this.mApp.getLruSeq());
        printWriter.print(str);
        printWriter.print("oom adj: max=");
        printWriter.print(this.mMaxAdj);
        printWriter.print(" curRaw=");
        printWriter.print(this.mCurRawAdj);
        printWriter.print(" setRaw=");
        printWriter.print(this.mSetRawAdj);
        printWriter.print(" cur=");
        printWriter.print(this.mCurAdj);
        printWriter.print(" set=");
        printWriter.println(this.mSetAdj);
        printWriter.print(str);
        printWriter.print("mCurSchedGroup=");
        printWriter.print(this.mCurSchedGroup);
        printWriter.print(" setSchedGroup=");
        printWriter.print(this.mSetSchedGroup);
        printWriter.print(" systemNoUi=");
        printWriter.println(this.mSystemNoUi);
        printWriter.print(str);
        printWriter.print("curProcState=");
        printWriter.print(getCurProcState());
        printWriter.print(" mRepProcState=");
        printWriter.print(this.mRepProcState);
        printWriter.print(" setProcState=");
        printWriter.print(this.mSetProcState);
        printWriter.print(" lastStateTime=");
        TimeUtils.formatDuration(getLastStateTime(), j, printWriter);
        printWriter.println();
        printWriter.print(str);
        printWriter.print("curCapability=");
        ActivityManager.printCapabilitiesFull(printWriter, this.mCurCapability);
        printWriter.print(" setCapability=");
        ActivityManager.printCapabilitiesFull(printWriter, this.mSetCapability);
        printWriter.println();
        if (this.mBackgroundRestricted) {
            printWriter.print(" backgroundRestricted=");
            printWriter.print(this.mBackgroundRestricted);
            printWriter.print(" boundByNonBgRestrictedApp=");
            printWriter.print(this.mSetBoundByNonBgRestrictedApp);
        }
        printWriter.println();
        if (this.mHasShownUi || this.mApp.mProfile.hasPendingUiClean()) {
            printWriter.print(str);
            printWriter.print("hasShownUi=");
            printWriter.print(this.mHasShownUi);
            printWriter.print(" pendingUiClean=");
            printWriter.println(this.mApp.mProfile.hasPendingUiClean());
        }
        printWriter.print(str);
        printWriter.print("cached=");
        printWriter.print(this.mCached);
        printWriter.print(" empty=");
        printWriter.println(this.mEmpty);
        if (this.mServiceB) {
            printWriter.print(str);
            printWriter.print("serviceb=");
            printWriter.print(this.mServiceB);
            printWriter.print(" serviceHighRam=");
            printWriter.println(this.mServiceHighRam);
        }
        if (this.mNotCachedSinceIdle) {
            printWriter.print(str);
            printWriter.print("notCachedSinceIdle=");
            printWriter.print(this.mNotCachedSinceIdle);
            printWriter.print(" initialIdlePss=");
            printWriter.println(this.mApp.mProfile.getInitialIdlePss());
        }
        if (hasTopUi() || hasOverlayUi() || this.mRunningRemoteAnimation) {
            printWriter.print(str);
            printWriter.print("hasTopUi=");
            printWriter.print(hasTopUi());
            printWriter.print(" hasOverlayUi=");
            printWriter.print(hasOverlayUi());
            printWriter.print(" runningRemoteAnimation=");
            printWriter.println(this.mRunningRemoteAnimation);
        }
        if (this.mHasForegroundActivities || this.mRepForegroundActivities) {
            printWriter.print(str);
            printWriter.print("foregroundActivities=");
            printWriter.print(this.mHasForegroundActivities);
            printWriter.print(" (rep=");
            printWriter.print(this.mRepForegroundActivities);
            printWriter.println(")");
        }
        if (this.mSetProcState > 10) {
            printWriter.print(str);
            printWriter.print("whenUnimportant=");
            TimeUtils.formatDuration(this.mWhenUnimportant - j, printWriter);
            printWriter.println();
        }
        if (this.mLastTopTime > 0) {
            printWriter.print(str);
            printWriter.print("lastTopTime=");
            TimeUtils.formatDuration(this.mLastTopTime, j, printWriter);
            printWriter.println();
        }
        long j2 = this.mLastInvisibleTime;
        if (j2 > 0 && j2 < Long.MAX_VALUE) {
            printWriter.print(str);
            printWriter.print("lastInvisibleTime=");
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long currentTimeMillis = System.currentTimeMillis();
            TimeUtils.dumpTimeWithDelta(printWriter, (currentTimeMillis - elapsedRealtime) + this.mLastInvisibleTime, currentTimeMillis);
            printWriter.println();
        }
        if (this.mHasStartedServices) {
            printWriter.print(str);
            printWriter.print("hasStartedServices=");
            printWriter.println(this.mHasStartedServices);
        }
    }
}
