package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.PictureInPictureParams;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.window.RemoteTransition;
import android.window.ScreenCapture;
import android.window.TransitionInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.policy.TransitionAnimation;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.BLASTSyncEngine;
import com.android.server.p014wm.DisplayArea;
import com.android.server.p014wm.TransitionController;
import com.android.server.p014wm.WindowContainer;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.Transition */
/* loaded from: classes2.dex */
public class Transition implements BLASTSyncEngine.TransactionReadyListener {
    public final TransitionController mController;
    public int mFlags;
    public boolean mForcePlaying;
    public boolean mIsPlayerEnabled;
    public final TransitionController.Logger mLogger;
    public TransitionInfo.AnimationOptions mOverrideOptions;
    public long mStatusBarTransitionDelay;
    public final BLASTSyncEngine mSyncEngine;
    public ArrayList<ChangeInfo> mTargets;
    public final Token mToken;
    public final int mType;
    public int mSyncId = -1;
    public RemoteTransition mRemoteTransition = null;
    public SurfaceControl.Transaction mStartTransaction = null;
    public SurfaceControl.Transaction mFinishTransaction = null;
    public final ArrayMap<WindowContainer, ChangeInfo> mChanges = new ArrayMap<>();
    public final ArraySet<WindowContainer> mParticipants = new ArraySet<>();
    public final ArrayList<DisplayContent> mTargetDisplays = new ArrayList<>();
    public final ArraySet<WindowToken> mVisibleAtTransitionEndTokens = new ArraySet<>();
    public ArrayMap<ActivityRecord, Task> mTransientLaunches = null;
    public IRemoteCallback mClientAnimationStartCallback = null;
    public IRemoteCallback mClientAnimationFinishCallback = null;
    public int mState = -1;
    public final ReadyTracker mReadyTracker = new ReadyTracker();
    public boolean mNavBarAttachedToApp = false;
    public int mRecentsDisplayId = -1;
    public boolean mCanPipOnFinish = true;
    public boolean mIsSeamlessRotation = false;
    public IContainerFreezer mContainerFreezer = null;
    public final SurfaceControl.Transaction mTmpTransaction = new SurfaceControl.Transaction();

    /* renamed from: com.android.server.wm.Transition$IContainerFreezer */
    /* loaded from: classes2.dex */
    public interface IContainerFreezer {
        void cleanUp(SurfaceControl.Transaction transaction);

        boolean freeze(WindowContainer windowContainer, Rect rect);
    }

    @TransitionInfo.TransitionMode
    public static int reduceMode(@TransitionInfo.TransitionMode int i) {
        if (i != 3) {
            if (i != 4) {
                return i;
            }
            return 2;
        }
        return 1;
    }

    public Transition(int i, int i2, TransitionController transitionController, BLASTSyncEngine bLASTSyncEngine) {
        TransitionController.Logger logger = new TransitionController.Logger();
        this.mLogger = logger;
        this.mForcePlaying = false;
        this.mIsPlayerEnabled = true;
        this.mType = i;
        this.mFlags = i2;
        this.mController = transitionController;
        this.mSyncEngine = bLASTSyncEngine;
        this.mToken = new Token(this);
        logger.mCreateWallTimeMs = System.currentTimeMillis();
        logger.mCreateTimeNs = SystemClock.elapsedRealtimeNanos();
        transitionController.mTransitionTracer.logState(this);
    }

    public static Transition fromBinder(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        try {
            return ((Token) iBinder).mTransition.get();
        } catch (ClassCastException e) {
            Slog.w("Transition", "Invalid transition token: " + iBinder, e);
            return null;
        }
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public void addFlag(int i) {
        this.mFlags = i | this.mFlags;
    }

    public void setTransientLaunch(ActivityRecord activityRecord, Task task) {
        ChangeInfo changeInfo;
        if (this.mTransientLaunches == null) {
            this.mTransientLaunches = new ArrayMap<>();
        }
        this.mTransientLaunches.put(activityRecord, task);
        setTransientLaunchToChanges(activityRecord);
        if (task != null && (changeInfo = this.mChanges.get(task)) != null) {
            changeInfo.mFlags |= 4;
        }
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -779535710, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(activityRecord)});
        }
    }

    public boolean isTransientHide(Task task) {
        if (this.mTransientLaunches == null) {
            return false;
        }
        for (int i = 0; i < this.mTransientLaunches.size(); i++) {
            if (this.mTransientLaunches.valueAt(i) == task) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTransientHide(WindowContainer windowContainer) {
        if (this.mTransientLaunches == null) {
            return false;
        }
        for (int i = 0; i < this.mTransientLaunches.size(); i++) {
            if (windowContainer.isDescendantOf(this.mTransientLaunches.valueAt(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isTransientLaunch(ActivityRecord activityRecord) {
        ArrayMap<ActivityRecord, Task> arrayMap = this.mTransientLaunches;
        return arrayMap != null && arrayMap.containsKey(activityRecord);
    }

    public Task getTransientLaunchRestoreTarget(WindowContainer windowContainer) {
        if (this.mTransientLaunches == null) {
            return null;
        }
        for (int i = 0; i < this.mTransientLaunches.size(); i++) {
            if (this.mTransientLaunches.keyAt(i).isDescendantOf(windowContainer)) {
                return this.mTransientLaunches.valueAt(i);
            }
        }
        return null;
    }

    public boolean isOnDisplay(DisplayContent displayContent) {
        return this.mTargetDisplays.contains(displayContent);
    }

    public void setSeamlessRotation(WindowContainer windowContainer) {
        ChangeInfo changeInfo = this.mChanges.get(windowContainer);
        if (changeInfo == null) {
            return;
        }
        changeInfo.mFlags |= 1;
        onSeamlessRotating(windowContainer.getDisplayContent());
    }

    public void onSeamlessRotating(DisplayContent displayContent) {
        if (this.mSyncEngine.getSyncSet(this.mSyncId).mSyncMethod == 1) {
            return;
        }
        if (this.mContainerFreezer == null) {
            this.mContainerFreezer = new ScreenshotFreezer();
        }
        WindowState topFullscreenOpaqueWindow = displayContent.getDisplayPolicy().getTopFullscreenOpaqueWindow();
        if (topFullscreenOpaqueWindow != null) {
            this.mIsSeamlessRotation = true;
            topFullscreenOpaqueWindow.mSyncMethodOverride = 1;
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 2004282287, 0, (String) null, new Object[]{String.valueOf(topFullscreenOpaqueWindow.getName())});
            }
        }
    }

    public final void setTransientLaunchToChanges(WindowContainer windowContainer) {
        while (windowContainer != null && this.mChanges.containsKey(windowContainer)) {
            if (windowContainer.asTask() == null && windowContainer.asActivityRecord() == null) {
                return;
            }
            this.mChanges.get(windowContainer).mFlags |= 2;
            windowContainer = windowContainer.getParent();
        }
    }

    public int getState() {
        return this.mState;
    }

    @VisibleForTesting
    public int getSyncId() {
        return this.mSyncId;
    }

    public int getFlags() {
        return this.mFlags;
    }

    @VisibleForTesting
    public SurfaceControl.Transaction getStartTransaction() {
        return this.mStartTransaction;
    }

    @VisibleForTesting
    public SurfaceControl.Transaction getFinishTransaction() {
        return this.mFinishTransaction;
    }

    public boolean isCollecting() {
        int i = this.mState;
        return i == 0 || i == 1;
    }

    public boolean isAborted() {
        return this.mState == 3;
    }

    public boolean isStarted() {
        return this.mState == 1;
    }

    @VisibleForTesting
    public void startCollecting(long j) {
        startCollecting(j, TransitionController.SYNC_METHOD);
    }

    public void startCollecting(long j, int i) {
        if (this.mState != -1) {
            throw new IllegalStateException("Attempting to re-use a transition");
        }
        this.mState = 0;
        int startSyncSet = this.mSyncEngine.startSyncSet(this, j, "Transition", i);
        this.mSyncId = startSyncSet;
        TransitionController.Logger logger = this.mLogger;
        logger.mSyncId = startSyncSet;
        logger.mCollectTimeNs = SystemClock.elapsedRealtimeNanos();
        this.mController.mTransitionTracer.logState(this);
    }

    public void start() {
        int i = this.mState;
        if (i < 0) {
            throw new IllegalStateException("Can't start Transition which isn't collecting.");
        }
        if (i >= 1) {
            Slog.w("Transition", "Transition already started id=" + this.mSyncId + " state=" + this.mState);
            return;
        }
        this.mState = 1;
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 996960396, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
        }
        applyReady();
        this.mLogger.mStartTimeNs = SystemClock.elapsedRealtimeNanos();
        this.mController.mTransitionTracer.logState(this);
        this.mController.updateAnimatingState(this.mTmpTransaction);
        SurfaceControl.mergeToGlobalTransaction(this.mTmpTransaction);
    }

    public void collect(WindowContainer windowContainer) {
        WindowState topVisibleWallpaper;
        if (this.mState < 0) {
            throw new IllegalStateException("Transition hasn't started collecting.");
        }
        if (isCollecting()) {
            boolean z = true;
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1567866547, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(windowContainer)});
            }
            for (WindowContainer<?> animatableParent = getAnimatableParent(windowContainer); animatableParent != null && !this.mChanges.containsKey(animatableParent); animatableParent = getAnimatableParent(animatableParent)) {
                this.mChanges.put(animatableParent, new ChangeInfo(animatableParent));
                if (isReadyGroup(animatableParent)) {
                    this.mReadyTracker.addGroup(animatableParent);
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1442613680, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(animatableParent)});
                    }
                }
            }
            if (this.mParticipants.contains(windowContainer)) {
                return;
            }
            if (isWallpaper(windowContainer) && !this.mParticipants.contains(windowContainer.mDisplayContent)) {
                z = false;
            }
            if (z) {
                this.mSyncEngine.addToSyncSet(this.mSyncId, windowContainer);
            }
            ChangeInfo changeInfo = this.mChanges.get(windowContainer);
            if (changeInfo == null) {
                changeInfo = new ChangeInfo(windowContainer);
                this.mChanges.put(windowContainer, changeInfo);
            }
            this.mParticipants.add(windowContainer);
            if (windowContainer.getDisplayContent() != null && !this.mTargetDisplays.contains(windowContainer.getDisplayContent())) {
                this.mTargetDisplays.add(windowContainer.getDisplayContent());
            }
            if (!changeInfo.mShowWallpaper || (topVisibleWallpaper = windowContainer.getDisplayContent().mWallpaperController.getTopVisibleWallpaper()) == null) {
                return;
            }
            collect(topVisibleWallpaper.mToken);
        }
    }

    public void collectExistenceChange(WindowContainer windowContainer) {
        if (this.mState >= 2) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -354571697, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(windowContainer)});
        }
        collect(windowContainer);
        this.mChanges.get(windowContainer).mExistenceChanged = true;
    }

    public void collectVisibleChange(WindowContainer windowContainer) {
        if (this.mSyncEngine.getSyncSet(this.mSyncId).mSyncMethod != 1 && isInTransition(windowContainer)) {
            if (this.mContainerFreezer == null) {
                this.mContainerFreezer = new ScreenshotFreezer();
            }
            ChangeInfo changeInfo = this.mChanges.get(windowContainer);
            if (changeInfo != null && changeInfo.mVisible && windowContainer.isVisibleRequested()) {
                this.mContainerFreezer.freeze(windowContainer, changeInfo.mAbsoluteBounds);
            }
        }
    }

    public void collectReparentChange(WindowContainer windowContainer, WindowContainer windowContainer2) {
        WindowContainer windowContainer3;
        if (this.mChanges.containsKey(windowContainer)) {
            ChangeInfo changeInfo = this.mChanges.get(windowContainer);
            WindowContainer windowContainer4 = changeInfo.mStartParent;
            if (windowContainer4 == null || windowContainer4.isAttached()) {
                windowContainer3 = changeInfo.mStartParent;
            } else {
                windowContainer3 = changeInfo.mCommonAncestor;
            }
            if (windowContainer3 == null || !windowContainer3.isAttached()) {
                Slog.w("Transition", "Trying to collect reparenting of a window after the previous parent has been detached: " + windowContainer);
            } else if (windowContainer3 == windowContainer2) {
                Slog.w("Transition", "Trying to collect reparenting of a window that has not been reparented: " + windowContainer);
            } else if (!windowContainer2.isAttached()) {
                Slog.w("Transition", "Trying to collect reparenting of a window that is not attached after reparenting: " + windowContainer);
            } else {
                while (windowContainer3 != windowContainer2 && !windowContainer3.isDescendantOf(windowContainer2)) {
                    windowContainer2 = windowContainer2.getParent();
                }
                changeInfo.mCommonAncestor = windowContainer2;
            }
        }
    }

    public boolean isInTransition(WindowContainer windowContainer) {
        while (windowContainer != null) {
            if (this.mParticipants.contains(windowContainer)) {
                return true;
            }
            windowContainer = windowContainer.getParent();
        }
        return false;
    }

    public void setKnownConfigChanges(WindowContainer<?> windowContainer, int i) {
        ChangeInfo changeInfo = this.mChanges.get(windowContainer);
        if (changeInfo != null) {
            changeInfo.mKnownConfigChanges = i;
        }
    }

    public final void sendRemoteCallback(IRemoteCallback iRemoteCallback) {
        if (iRemoteCallback == null) {
            return;
        }
        this.mController.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Transition.lambda$sendRemoteCallback$0((IRemoteCallback) obj);
            }
        }, iRemoteCallback));
    }

    public static /* synthetic */ void lambda$sendRemoteCallback$0(IRemoteCallback iRemoteCallback) {
        try {
            iRemoteCallback.sendResult((Bundle) null);
        } catch (RemoteException unused) {
        }
    }

    public void setOverrideAnimation(TransitionInfo.AnimationOptions animationOptions, IRemoteCallback iRemoteCallback, IRemoteCallback iRemoteCallback2) {
        if (isCollecting()) {
            this.mOverrideOptions = animationOptions;
            sendRemoteCallback(this.mClientAnimationStartCallback);
            this.mClientAnimationStartCallback = iRemoteCallback;
            this.mClientAnimationFinishCallback = iRemoteCallback2;
        }
    }

    public void setReady(WindowContainer windowContainer, boolean z) {
        if (!isCollecting() || this.mSyncId < 0) {
            return;
        }
        this.mReadyTracker.setReadyFrom(windowContainer, z);
        applyReady();
    }

    public final void applyReady() {
        if (this.mState < 1) {
            return;
        }
        boolean allReady = this.mReadyTracker.allReady();
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -874888131, 7, (String) null, new Object[]{Boolean.valueOf(allReady), Long.valueOf(this.mSyncId)});
        }
        this.mSyncEngine.setReady(this.mSyncId, allReady);
        if (allReady) {
            this.mLogger.mReadyTimeNs = SystemClock.elapsedRealtimeNanos();
        }
    }

    public void setAllReady() {
        if (!isCollecting() || this.mSyncId < 0) {
            return;
        }
        this.mReadyTracker.setAllReady();
        applyReady();
    }

    @VisibleForTesting
    public boolean allReady() {
        return this.mReadyTracker.allReady();
    }

    public final void buildFinishTransaction(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        Point point = new Point();
        ArraySet arraySet = new ArraySet();
        for (int size = this.mTargets.size() - 1; size >= 0; size--) {
            WindowContainer windowContainer = this.mTargets.get(size).mContainer;
            if (windowContainer.getParent() != null) {
                SurfaceControl leashSurface = getLeashSurface(windowContainer, null);
                transaction.reparent(leashSurface, getOrigParentSurface(windowContainer));
                transaction.setLayer(leashSurface, windowContainer.getLastLayer());
                windowContainer.getRelativePosition(point);
                transaction.setPosition(leashSurface, point.x, point.y);
                if (windowContainer.asTaskFragment() == null) {
                    transaction.setCrop(leashSurface, null);
                } else {
                    Rect resolvedOverrideBounds = windowContainer.getResolvedOverrideBounds();
                    transaction.setWindowCrop(leashSurface, resolvedOverrideBounds.width(), resolvedOverrideBounds.height());
                }
                transaction.setCornerRadius(leashSurface, 0.0f);
                transaction.setShadowRadius(leashSurface, 0.0f);
                transaction.setMatrix(leashSurface, 1.0f, 0.0f, 0.0f, 1.0f);
                transaction.setAlpha(leashSurface, 1.0f);
                if (windowContainer.isOrganized() && windowContainer.matchParentBounds()) {
                    transaction.setWindowCrop(leashSurface, -1, -1);
                }
                arraySet.add(windowContainer.getDisplayContent());
            }
        }
        IContainerFreezer iContainerFreezer = this.mContainerFreezer;
        if (iContainerFreezer != null) {
            iContainerFreezer.cleanUp(transaction);
        }
        this.mController.mBuildingFinishLayers = true;
        try {
            for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
                if (arraySet.valueAt(size2) != null) {
                    ((DisplayContent) arraySet.valueAt(size2)).assignChildLayers(transaction);
                }
            }
            this.mController.mBuildingFinishLayers = false;
            if (surfaceControl.isValid()) {
                transaction.reparent(surfaceControl, null);
            }
        } catch (Throwable th) {
            this.mController.mBuildingFinishLayers = false;
            throw th;
        }
    }

    public void setCanPipOnFinish(boolean z) {
        this.mCanPipOnFinish = z;
    }

    public final boolean didCommitTransientLaunch() {
        if (this.mTransientLaunches == null) {
            return false;
        }
        for (int i = 0; i < this.mTransientLaunches.size(); i++) {
            if (this.mTransientLaunches.keyAt(i).isVisibleRequested()) {
                return true;
            }
        }
        return false;
    }

    public final boolean checkEnterPipOnFinish(ActivityRecord activityRecord) {
        if (this.mCanPipOnFinish && activityRecord.isVisible() && activityRecord.getTask() != null) {
            PictureInPictureParams pictureInPictureParams = activityRecord.pictureInPictureArgs;
            if (pictureInPictureParams != null && pictureInPictureParams.isAutoEnterEnabled()) {
                if (didCommitTransientLaunch()) {
                    activityRecord.supportsEnterPipOnTaskSwitch = true;
                }
                return this.mController.mAtm.enterPictureInPictureMode(activityRecord, activityRecord.pictureInPictureArgs, false);
            }
            boolean deferHidingClient = activityRecord.getDeferHidingClient();
            if (!deferHidingClient && didCommitTransientLaunch()) {
                activityRecord.supportsEnterPipOnTaskSwitch = true;
                deferHidingClient = activityRecord.checkEnterPictureInPictureState("finishTransition", true) && activityRecord.isState(ActivityRecord.State.RESUMED);
            }
            if (deferHidingClient) {
                try {
                    this.mController.mAtm.mTaskSupervisor.mUserLeaving = true;
                    activityRecord.getTaskFragment().startPausing(false, null, "finishTransition");
                    return false;
                } finally {
                    this.mController.mAtm.mTaskSupervisor.mUserLeaving = false;
                }
            }
            return false;
        }
        return false;
    }

    public void finishTransition() {
        WindowState windowState;
        ActivityRecord topNonFinishingActivity;
        if (Trace.isTagEnabled(32L) && this.mIsPlayerEnabled) {
            Trace.asyncTraceEnd(32L, "PlayTransition", System.identityHashCode(this));
        }
        this.mLogger.mFinishTimeNs = SystemClock.elapsedRealtimeNanos();
        Handler handler = this.mController.mLoggerHandler;
        final TransitionController.Logger logger = this.mLogger;
        Objects.requireNonNull(logger);
        handler.post(new Runnable() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TransitionController.Logger.this.logOnFinish();
            }
        });
        SurfaceControl.Transaction transaction = this.mStartTransaction;
        if (transaction != null) {
            transaction.close();
        }
        SurfaceControl.Transaction transaction2 = this.mFinishTransaction;
        if (transaction2 != null) {
            transaction2.close();
        }
        this.mFinishTransaction = null;
        this.mStartTransaction = null;
        if (this.mState < 2) {
            throw new IllegalStateException("Can't finish a non-playing transition " + this.mSyncId);
        }
        boolean z = false;
        boolean z2 = false;
        for (int i = 0; i < this.mParticipants.size(); i++) {
            WindowContainer valueAt = this.mParticipants.valueAt(i);
            ActivityRecord asActivityRecord = valueAt.asActivityRecord();
            if (asActivityRecord != null) {
                boolean contains = this.mVisibleAtTransitionEndTokens.contains(asActivityRecord);
                DisplayContent displayContent = asActivityRecord.mDisplayContent;
                boolean z3 = displayContent == null || displayContent.getDisplayInfo().state == 1;
                if ((!contains || z3) && !asActivityRecord.isVisibleRequested() && (!checkEnterPipOnFinish(asActivityRecord))) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -532081937, 0, (String) null, new Object[]{String.valueOf(asActivityRecord)});
                    }
                    Task task = asActivityRecord.getTask();
                    if (task != null && !task.isVisibleRequested() && this.mTransientLaunches != null) {
                        this.mController.mTaskSnapshotController.recordSnapshot(task, false);
                    }
                    asActivityRecord.commitVisibility(false, false, true);
                }
                if (this.mChanges.get(asActivityRecord).mVisible != contains) {
                    asActivityRecord.mEnteringAnimation = contains;
                } else {
                    ArrayMap<ActivityRecord, Task> arrayMap = this.mTransientLaunches;
                    if (arrayMap != null && arrayMap.containsKey(asActivityRecord) && asActivityRecord.isVisible()) {
                        asActivityRecord.mEnteringAnimation = true;
                        if (asActivityRecord.isTopRunningActivity()) {
                            asActivityRecord.moveFocusableActivityToTop("transitionFinished");
                        }
                        z = true;
                    }
                }
            } else if (valueAt.asDisplayContent() != null) {
                z2 = true;
            } else {
                WallpaperWindowToken asWallpaperToken = valueAt.asWallpaperToken();
                if (asWallpaperToken != null) {
                    if (!this.mVisibleAtTransitionEndTokens.contains(asWallpaperToken) && !asWallpaperToken.isVisibleRequested()) {
                        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 691515534, 0, (String) null, new Object[]{String.valueOf(asWallpaperToken)});
                        }
                        asWallpaperToken.commitVisibility(false);
                    }
                } else {
                    final Task asTask = valueAt.asTask();
                    if (asTask != null && asTask.isVisibleRequested() && asTask.inPinnedWindowingMode() && (topNonFinishingActivity = asTask.getTopNonFinishingActivity()) != null && !topNonFinishingActivity.inPinnedWindowingMode()) {
                        this.mController.mStateValidators.add(new Runnable() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                Transition.lambda$finishTransition$1(Task.this);
                            }
                        });
                    }
                }
            }
        }
        if (z) {
            this.mController.mAtm.getTaskChangeNotificationController().notifyTaskStackChanged();
            this.mController.mAtm.stopAppSwitches();
            this.mController.mAtm.mRootWindowContainer.rankTaskLayers();
        }
        for (int i2 = 0; i2 < this.mParticipants.size(); i2++) {
            ActivityRecord asActivityRecord2 = this.mParticipants.valueAt(i2).asActivityRecord();
            if (asActivityRecord2 != null) {
                this.mController.dispatchLegacyAppTransitionFinished(asActivityRecord2);
            }
        }
        SurfaceControl.Transaction transaction3 = null;
        for (int i3 = 0; i3 < this.mParticipants.size(); i3++) {
            ActivityRecord asActivityRecord3 = this.mParticipants.valueAt(i3).asActivityRecord();
            if (asActivityRecord3 != null && asActivityRecord3.isVisible() && asActivityRecord3.getParent() != null) {
                if (transaction3 == null) {
                    transaction3 = asActivityRecord3.mWmService.mTransactionFactory.get();
                }
                asActivityRecord3.mActivityRecordInputSink.applyChangesToSurfaceIfChanged(transaction3);
            }
        }
        if (transaction3 != null) {
            transaction3.apply();
        }
        this.mController.mAtm.mTaskSupervisor.scheduleProcessStoppingAndFinishingActivitiesIfNeeded();
        sendRemoteCallback(this.mClientAnimationFinishCallback);
        legacyRestoreNavigationBarFromApp();
        int i4 = this.mRecentsDisplayId;
        if (i4 != -1) {
            this.mController.mAtm.mRootWindowContainer.getDisplayContent(i4).getInputMonitor().setActiveRecents(null, null);
        }
        for (int i5 = 0; i5 < this.mTargetDisplays.size(); i5++) {
            DisplayContent displayContent2 = this.mTargetDisplays.get(i5);
            AsyncRotationController asyncRotationController = displayContent2.getAsyncRotationController();
            if (asyncRotationController != null && containsChangeFor(displayContent2, this.mTargets)) {
                asyncRotationController.onTransitionFinished();
            }
            if (this.mTransientLaunches != null) {
                InsetsControlTarget imeTarget = displayContent2.getImeTarget(2);
                int i6 = 0;
                while (true) {
                    if (i6 >= this.mTransientLaunches.size()) {
                        windowState = null;
                        break;
                    } else if (this.mTransientLaunches.keyAt(i6).getDisplayContent() == displayContent2) {
                        windowState = displayContent2.computeImeTarget(true);
                        break;
                    } else {
                        i6++;
                    }
                }
                if (this.mRecentsDisplayId != -1 && imeTarget == windowState) {
                    InputMethodManagerInternal.get().updateImeWindowStatus(false);
                }
            }
            displayContent2.removeImeSurfaceImmediately();
            displayContent2.handleCompleteDeferredRemoval();
        }
        this.mState = 4;
        this.mController.mTransitionTracer.logState(this);
        if (z2 && !this.mController.useShellTransitionsRotation()) {
            this.mController.mAtm.mWindowManager.updateRotation(false, false);
        }
        cleanUpInternal();
        this.mController.updateAnimatingState(this.mTmpTransaction);
        this.mTmpTransaction.apply();
        this.mController.mAtm.mBackNavigationController.handleDeferredBackAnimation(this.mTargets);
    }

    public static /* synthetic */ void lambda$finishTransition$1(Task task) {
        if (task.isAttached() && task.isVisibleRequested() && task.inPinnedWindowingMode()) {
            ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity();
            if (topNonFinishingActivity.inPinnedWindowingMode()) {
                return;
            }
            Slog.e("Transition", "Enter-PIP was started but not completed, this is a Shell/SysUI bug. This state breaks gesture-nav, so attempting clean-up.");
            task.abortPipEnter(topNonFinishingActivity);
        }
    }

    public void abort() {
        int i = this.mState;
        if (i == 3) {
            return;
        }
        if (i == -1) {
            this.mState = 3;
        } else if (i != 0 && i != 1) {
            throw new IllegalStateException("Too late to abort. state=" + this.mState);
        } else {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -863438038, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
            }
            this.mState = 3;
            this.mSyncEngine.abort(this.mSyncId);
            this.mController.dispatchLegacyAppTransitionCancelled();
        }
    }

    public void playNow() {
        int i = this.mState;
        if (i == 0 || i == 1) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -894942237, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
            }
            this.mForcePlaying = true;
            setAllReady();
            if (this.mState == 0) {
                start();
            }
            this.mSyncEngine.onSurfacePlacement();
        }
    }

    public boolean isForcePlaying() {
        return this.mForcePlaying;
    }

    public void setRemoteTransition(RemoteTransition remoteTransition) {
        this.mRemoteTransition = remoteTransition;
    }

    public RemoteTransition getRemoteTransition() {
        return this.mRemoteTransition;
    }

    public void setNoAnimation(WindowContainer windowContainer) {
        ChangeInfo changeInfo = this.mChanges.get(windowContainer);
        if (changeInfo == null) {
            throw new IllegalStateException("Can't set no-animation property of non-participant");
        }
        changeInfo.mFlags |= 8;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static boolean containsChangeFor(WindowContainer windowContainer, ArrayList<ChangeInfo> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (arrayList.get(size).mContainer == windowContainer) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.server.p014wm.BLASTSyncEngine.TransactionReadyListener
    public void onTransactionReady(int i, SurfaceControl.Transaction transaction) {
        if (i != this.mSyncId) {
            Slog.e("Transition", "Unexpected Sync ID " + i + ". Expected " + this.mSyncId);
            return;
        }
        if (this.mTargetDisplays.isEmpty()) {
            this.mTargetDisplays.add(this.mController.mAtm.mRootWindowContainer.getDefaultDisplay());
        }
        DisplayContent displayContent = this.mTargetDisplays.get(0);
        commitVisibleActivities(transaction);
        if (this.mState == 3) {
            this.mController.abort(this);
            displayContent.getPendingTransaction().merge(transaction);
            this.mSyncId = -1;
            this.mOverrideOptions = null;
            cleanUpInternal();
            return;
        }
        this.mState = 2;
        this.mStartTransaction = transaction;
        this.mFinishTransaction = this.mController.mAtm.mWindowManager.mTransactionFactory.get();
        this.mController.moveToPlaying(this);
        if (displayContent.isKeyguardLocked()) {
            this.mFlags |= 64;
        }
        boolean containsBackAnimationTargets = this.mController.mAtm.mBackNavigationController.containsBackAnimationTargets(this);
        ArrayList<ChangeInfo> calculateTargets = calculateTargets(this.mParticipants, this.mChanges);
        this.mTargets = calculateTargets;
        TransitionInfo calculateTransitionInfo = calculateTransitionInfo(this.mType, this.mFlags, calculateTargets, transaction);
        if (containsBackAnimationTargets) {
            this.mController.mAtm.mBackNavigationController.clearBackAnimations(this.mStartTransaction);
        }
        TransitionInfo.AnimationOptions animationOptions = this.mOverrideOptions;
        if (animationOptions != null) {
            calculateTransitionInfo.setAnimationOptions(animationOptions);
            if (this.mOverrideOptions.getType() == 12) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mTargets.size()) {
                        break;
                    }
                    TransitionInfo.Change change = (TransitionInfo.Change) calculateTransitionInfo.getChanges().get(i2);
                    ActivityRecord asActivityRecord = this.mTargets.get(i2).mContainer.asActivityRecord();
                    if (asActivityRecord == null || change.getMode() != 1) {
                        i2++;
                    } else {
                        change.setFlags(change.getFlags() | (asActivityRecord.mUserId == asActivityRecord.mWmService.mCurrentUserId ? IInstalld.FLAG_USE_QUOTA : IInstalld.FLAG_FORCE));
                    }
                }
            }
        }
        handleLegacyRecentsStartBehavior(displayContent, calculateTransitionInfo);
        handleNonAppWindowsInTransition(displayContent, this.mType, this.mFlags);
        sendRemoteCallback(this.mClientAnimationStartCallback);
        for (int size = this.mParticipants.size() - 1; size >= 0; size--) {
            ActivityRecord asActivityRecord2 = this.mParticipants.valueAt(size).asActivityRecord();
            if (asActivityRecord2 != null && asActivityRecord2.isVisibleRequested()) {
                transaction.show(asActivityRecord2.getSurfaceControl());
                for (WindowContainer parent = asActivityRecord2.getParent(); parent != null && !containsChangeFor(parent, this.mTargets); parent = parent.getParent()) {
                    if (parent.getSurfaceControl() != null) {
                        transaction.show(parent.getSurfaceControl());
                    }
                }
            }
        }
        for (int size2 = this.mParticipants.size() - 1; size2 >= 0; size2--) {
            WindowContainer valueAt = this.mParticipants.valueAt(size2);
            if (valueAt.asWindowToken() != null && valueAt.isVisibleRequested() && (this.mTransientLaunches == null || valueAt.asActivityRecord() == null || !this.mTransientLaunches.containsKey(valueAt.asActivityRecord()))) {
                this.mVisibleAtTransitionEndTokens.add(valueAt.asWindowToken());
            }
        }
        if (this.mTransientLaunches == null) {
            for (int size3 = this.mParticipants.size() - 1; size3 >= 0; size3--) {
                ActivityRecord asActivityRecord3 = this.mParticipants.valueAt(size3).asActivityRecord();
                if (asActivityRecord3 != null && !asActivityRecord3.isVisibleRequested() && asActivityRecord3.getTask() != null && !asActivityRecord3.getTask().isVisibleRequested()) {
                    this.mController.mTaskSnapshotController.recordSnapshot(asActivityRecord3.getTask(), false);
                }
            }
        }
        AsyncRotationController asyncRotationController = displayContent.getAsyncRotationController();
        if (asyncRotationController != null && containsChangeFor(displayContent, this.mTargets)) {
            asyncRotationController.setupStartTransaction(transaction);
        }
        buildFinishTransaction(this.mFinishTransaction, calculateTransitionInfo.getRootLeash());
        if (this.mController.getTransitionPlayer() != null && this.mIsPlayerEnabled) {
            this.mController.dispatchLegacyAppTransitionStarting(calculateTransitionInfo, this.mStatusBarTransitionDelay);
            try {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1115248873, 0, (String) null, new Object[]{String.valueOf(calculateTransitionInfo)});
                }
                this.mLogger.mSendTimeNs = SystemClock.elapsedRealtimeNanos();
                TransitionController.Logger logger = this.mLogger;
                logger.mInfo = calculateTransitionInfo;
                this.mController.mTransitionTracer.logSentTransition(this, this.mTargets, logger.mCreateTimeNs, logger.mSendTimeNs, calculateTransitionInfo);
                this.mController.getTransitionPlayer().onTransitionReady(this.mToken, calculateTransitionInfo, transaction, this.mFinishTransaction);
                if (Trace.isTagEnabled(32L)) {
                    Trace.asyncTraceBegin(32L, "PlayTransition", System.identityHashCode(this));
                }
            } catch (RemoteException unused) {
                postCleanupOnFailure();
            }
            AccessibilityController accessibilityController = displayContent.mWmService.mAccessibilityController;
            if (accessibilityController.hasCallbacks()) {
                accessibilityController.onWMTransition(displayContent.getDisplayId(), this.mType);
            }
        } else {
            if (!this.mIsPlayerEnabled) {
                this.mLogger.mSendTimeNs = SystemClock.uptimeNanos();
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 378890013, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
                }
            }
            postCleanupOnFailure();
        }
        Handler handler = this.mController.mLoggerHandler;
        final TransitionController.Logger logger2 = this.mLogger;
        Objects.requireNonNull(logger2);
        handler.post(new Runnable() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TransitionController.Logger.this.logOnSend();
            }
        });
        this.mOverrideOptions = null;
        reportStartReasonsToLogger();
        calculateTransitionInfo.releaseAnimSurfaces();
    }

    public final void postCleanupOnFailure() {
        this.mController.mAtm.f1161mH.post(new Runnable() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                Transition.this.lambda$postCleanupOnFailure$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postCleanupOnFailure$2() {
        synchronized (this.mController.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                cleanUpOnFailure();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void cleanUpOnFailure() {
        if (this.mState < 2) {
            return;
        }
        SurfaceControl.Transaction transaction = this.mStartTransaction;
        if (transaction != null) {
            transaction.apply();
        }
        SurfaceControl.Transaction transaction2 = this.mFinishTransaction;
        if (transaction2 != null) {
            transaction2.apply();
        }
        this.mController.finishTransition(this.mToken);
    }

    public final void cleanUpInternal() {
        for (int i = 0; i < this.mChanges.size(); i++) {
            SurfaceControl surfaceControl = this.mChanges.valueAt(i).mSnapshot;
            if (surfaceControl != null) {
                surfaceControl.release();
            }
        }
    }

    public final void commitVisibleActivities(SurfaceControl.Transaction transaction) {
        for (int size = this.mParticipants.size() - 1; size >= 0; size--) {
            ActivityRecord asActivityRecord = this.mParticipants.valueAt(size).asActivityRecord();
            if (asActivityRecord != null && asActivityRecord.getTask() != null) {
                if (asActivityRecord.isVisibleRequested()) {
                    asActivityRecord.commitVisibility(true, false, true);
                    asActivityRecord.commitFinishDrawing(transaction);
                }
                asActivityRecord.getTask().setDeferTaskAppear(false);
            }
        }
    }

    public final void handleLegacyRecentsStartBehavior(DisplayContent displayContent, TransitionInfo transitionInfo) {
        WindowState navigationBar;
        WindowToken windowToken;
        Task fromWindowContainerToken;
        if ((this.mFlags & 128) == 0) {
            return;
        }
        this.mRecentsDisplayId = displayContent.mDisplayId;
        InputConsumerImpl inputConsumer = displayContent.getInputMonitor().getInputConsumer("recents_animation_input_consumer");
        WindowContainer windowContainer = null;
        if (inputConsumer != null) {
            ActivityRecord activityRecord = null;
            ActivityRecord activityRecord2 = null;
            for (int i = 0; i < transitionInfo.getChanges().size(); i++) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(i);
                if (change.getTaskInfo() != null && (fromWindowContainerToken = Task.fromWindowContainerToken(((TransitionInfo.Change) transitionInfo.getChanges().get(i)).getTaskInfo().token)) != null) {
                    int i2 = change.getTaskInfo().topActivityType;
                    boolean z = i2 == 2 || i2 == 3;
                    if (z && activityRecord == null) {
                        activityRecord = fromWindowContainerToken.getTopVisibleActivity();
                    } else if (!z && activityRecord2 == null) {
                        activityRecord2 = fromWindowContainerToken.getTopNonFinishingActivity();
                    }
                }
            }
            if (activityRecord != null && activityRecord2 != null) {
                inputConsumer.mWindowHandle.touchableRegion.set(activityRecord2.getBounds());
                displayContent.getInputMonitor().setActiveRecents(activityRecord, activityRecord2);
            }
        }
        if (displayContent.getDisplayPolicy().shouldAttachNavBarToAppDuringTransition() && displayContent.getAsyncRotationController() == null) {
            for (int i3 = 0; i3 < transitionInfo.getChanges().size(); i3++) {
                TransitionInfo.Change change2 = (TransitionInfo.Change) transitionInfo.getChanges().get(i3);
                if (change2.getTaskInfo() != null && change2.getTaskInfo().displayId == this.mRecentsDisplayId && change2.getTaskInfo().getActivityType() == 1 && (change2.getMode() == 2 || change2.getMode() == 4)) {
                    windowContainer = WindowContainer.fromBinder(change2.getContainer().asBinder());
                    break;
                }
            }
            if (windowContainer == null || windowContainer.inMultiWindowMode() || (navigationBar = displayContent.getDisplayPolicy().getNavigationBar()) == null || (windowToken = navigationBar.mToken) == null) {
                return;
            }
            this.mNavBarAttachedToApp = true;
            windowToken.cancelAnimation();
            SurfaceControl.Transaction pendingTransaction = navigationBar.mToken.getPendingTransaction();
            SurfaceControl surfaceControl = navigationBar.mToken.getSurfaceControl();
            pendingTransaction.reparent(surfaceControl, windowContainer.getSurfaceControl());
            pendingTransaction.show(surfaceControl);
            DisplayArea.Tokens imeContainer = displayContent.getImeContainer();
            if (imeContainer.isVisible()) {
                pendingTransaction.setRelativeLayer(surfaceControl, imeContainer.getSurfaceControl(), 1);
            } else {
                pendingTransaction.setLayer(surfaceControl, Integer.MAX_VALUE);
            }
            StatusBarManagerInternal statusBarManagerInternal = displayContent.getDisplayPolicy().getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.setNavigationBarLumaSamplingEnabled(this.mRecentsDisplayId, false);
            }
        }
    }

    public void legacyRestoreNavigationBarFromApp() {
        if (this.mNavBarAttachedToApp) {
            boolean z = false;
            this.mNavBarAttachedToApp = false;
            if (this.mRecentsDisplayId == -1) {
                Slog.e("Transition", "Reparented navigation bar without a valid display");
                this.mRecentsDisplayId = 0;
            }
            DisplayContent displayContent = this.mController.mAtm.mRootWindowContainer.getDisplayContent(this.mRecentsDisplayId);
            StatusBarManagerInternal statusBarManagerInternal = displayContent.getDisplayPolicy().getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.setNavigationBarLumaSamplingEnabled(this.mRecentsDisplayId, true);
            }
            WindowState navigationBar = displayContent.getDisplayPolicy().getNavigationBar();
            if (navigationBar == null) {
                return;
            }
            navigationBar.setSurfaceTranslationY(0);
            WindowToken windowToken = navigationBar.mToken;
            if (windowToken == null) {
                return;
            }
            SurfaceControl.Transaction pendingTransaction = displayContent.getPendingTransaction();
            WindowContainer parent = windowToken.getParent();
            pendingTransaction.setLayer(windowToken.getSurfaceControl(), windowToken.getLastLayer());
            int i = 0;
            while (true) {
                if (i < this.mTargets.size()) {
                    Task asTask = this.mTargets.get(i).mContainer.asTask();
                    if (asTask != null && asTask.isActivityTypeHomeOrRecents()) {
                        z = asTask.isVisibleRequested();
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (z) {
                new NavBarFadeAnimationController(displayContent).fadeWindowToken(true);
            } else {
                pendingTransaction.reparent(windowToken.getSurfaceControl(), parent.getSurfaceControl());
            }
        }
    }

    public final void handleNonAppWindowsInTransition(DisplayContent displayContent, int i, int i2) {
        if ((i2 & 64) != 0) {
            this.mController.mAtm.mWindowManager.mPolicy.applyKeyguardOcclusionChange((i == 8 || i == 9) ? false : true);
        }
    }

    public final void reportStartReasonsToLogger() {
        int i;
        ArrayMap<WindowContainer, Integer> arrayMap = new ArrayMap<>();
        for (int size = this.mParticipants.size() - 1; size >= 0; size--) {
            ActivityRecord asActivityRecord = this.mParticipants.valueAt(size).asActivityRecord();
            if (asActivityRecord != null && asActivityRecord.isVisibleRequested()) {
                if (!(asActivityRecord.mStartingData instanceof SplashScreenStartingData) || asActivityRecord.mLastAllReadyAtSync) {
                    i = (asActivityRecord.isActivityTypeHomeOrRecents() && isTransientLaunch(asActivityRecord)) ? 5 : 2;
                } else {
                    i = 1;
                }
                arrayMap.put(asActivityRecord, Integer.valueOf(i));
            }
        }
        this.mController.mAtm.mTaskSupervisor.getActivityMetricsLogger().notifyTransitionStarting(arrayMap);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("TransitionRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" id=" + this.mSyncId);
        sb.append(" type=" + WindowManager.transitTypeToString(this.mType));
        sb.append(" flags=" + this.mFlags);
        sb.append('}');
        return sb.toString();
    }

    public static WindowContainer<?> getAnimatableParent(WindowContainer<?> windowContainer) {
        WindowContainer<?> parent = windowContainer.getParent();
        while (parent != null && !parent.canCreateRemoteAnimationTarget() && !parent.isOrganized()) {
            parent = parent.getParent();
        }
        return parent;
    }

    public static boolean reportIfNotTop(WindowContainer windowContainer) {
        return windowContainer.isOrganized();
    }

    public static boolean isWallpaper(WindowContainer windowContainer) {
        return windowContainer.asWallpaperToken() != null;
    }

    public static boolean isInputMethod(WindowContainer windowContainer) {
        return windowContainer.getWindowType() == 2011;
    }

    public static boolean occludesKeyguard(WindowContainer windowContainer) {
        ActivityRecord activity;
        ActivityRecord asActivityRecord = windowContainer.asActivityRecord();
        if (asActivityRecord != null) {
            return asActivityRecord.canShowWhenLocked();
        }
        Task asTask = windowContainer.asTask();
        return (asTask == null || (activity = asTask.getActivity(new Predicate() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((ActivityRecord) obj).isClientVisible();
            }
        })) == null || !activity.canShowWhenLocked()) ? false : true;
    }

    public static boolean isTranslucent(WindowContainer windowContainer) {
        TaskFragment asTaskFragment = windowContainer.asTaskFragment();
        if (asTaskFragment == null) {
            return !windowContainer.fillsParent();
        }
        if (asTaskFragment.isTranslucentForTransition()) {
            return true;
        }
        TaskFragment adjacentTaskFragment = asTaskFragment.getAdjacentTaskFragment();
        if (adjacentTaskFragment != null) {
            return adjacentTaskFragment.isTranslucentForTransition();
        }
        return !windowContainer.fillsParent();
    }

    public static boolean canPromote(ChangeInfo changeInfo, Targets targets, ArrayMap<WindowContainer, ChangeInfo> arrayMap) {
        WindowContainer windowContainer = changeInfo.mContainer;
        WindowContainer parent = windowContainer.getParent();
        ChangeInfo changeInfo2 = arrayMap.get(parent);
        if (!parent.canCreateRemoteAnimationTarget() || changeInfo2 == null || !changeInfo2.hasChanged()) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 744171317, 0, (String) null, new Object[]{String.valueOf("parent can't be target " + parent)});
            }
            return false;
        } else if (isWallpaper(windowContainer)) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -2036671725, 0, (String) null, (Object[]) null);
            }
            return false;
        } else if (changeInfo.mStartParent == null || windowContainer.getParent() == changeInfo.mStartParent) {
            int transitMode = changeInfo.getTransitMode(windowContainer);
            for (int childCount = parent.getChildCount() - 1; childCount >= 0; childCount--) {
                WindowContainer childAt = parent.getChildAt(childCount);
                if (windowContainer != childAt) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -703543418, 0, (String) null, new Object[]{String.valueOf(childAt)});
                    }
                    ChangeInfo changeInfo3 = arrayMap.get(childAt);
                    if (changeInfo3 == null || !targets.wasParticipated(changeInfo3)) {
                        if (childAt.isVisibleRequested()) {
                            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 793568608, 0, (String) null, (Object[]) null);
                            }
                            return false;
                        } else if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1728919185, 0, (String) null, new Object[]{String.valueOf(childAt)});
                        }
                    } else {
                        int transitMode2 = changeInfo3.getTransitMode(childAt);
                        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -779095785, 0, (String) null, new Object[]{String.valueOf(TransitionInfo.modeToString(transitMode2))});
                        }
                        if (reduceMode(transitMode) != reduceMode(transitMode2)) {
                            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1469310004, 0, (String) null, new Object[]{String.valueOf(TransitionInfo.modeToString(transitMode))});
                            }
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void tryPromote(Targets targets, ArrayMap<WindowContainer, ChangeInfo> arrayMap) {
        int size = targets.mArray.size() - 1;
        WindowContainer windowContainer = null;
        while (size >= 0) {
            ChangeInfo valueAt = targets.mArray.valueAt(size);
            WindowContainer windowContainer2 = valueAt.mContainer;
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -509601642, 0, (String) null, new Object[]{String.valueOf(windowContainer2)});
            }
            WindowContainer parent = windowContainer2.getParent();
            if (parent == windowContainer) {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 112145970, 0, (String) null, (Object[]) null);
                }
            } else if (canPromote(valueAt, targets, arrayMap)) {
                if (reportIfNotTop(windowContainer2)) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 528150092, 0, (String) null, new Object[]{String.valueOf(windowContainer2)});
                    }
                } else {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 182319432, 0, (String) null, new Object[]{String.valueOf(windowContainer2)});
                    }
                    targets.remove(size);
                }
                ChangeInfo changeInfo = arrayMap.get(parent);
                if (targets.mArray.indexOfValue(changeInfo) < 0) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1452274694, 0, (String) null, new Object[]{String.valueOf(parent)});
                    }
                    size++;
                    targets.add(changeInfo);
                }
                if ((valueAt.mFlags & 8) != 0) {
                    changeInfo.mFlags |= 8;
                } else {
                    changeInfo.mFlags |= 16;
                }
            } else {
                windowContainer = parent;
            }
            size--;
        }
    }

    @VisibleForTesting
    public static ArrayList<ChangeInfo> calculateTargets(ArraySet<WindowContainer> arraySet, ArrayMap<WindowContainer, ChangeInfo> arrayMap) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 184610856, 0, (String) null, new Object[]{String.valueOf(arraySet)});
        }
        Targets targets = new Targets();
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            WindowContainer valueAt = arraySet.valueAt(size);
            if (!valueAt.isAttached()) {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1494644409, 0, (String) null, new Object[]{String.valueOf(valueAt)});
                }
            } else if (valueAt.asWindowState() == null) {
                ChangeInfo changeInfo = arrayMap.get(valueAt);
                if (!changeInfo.hasChanged()) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -672355406, 0, (String) null, new Object[]{String.valueOf(valueAt)});
                    }
                } else {
                    targets.add(changeInfo);
                }
            }
        }
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1844540996, 0, (String) null, new Object[]{String.valueOf(targets.mArray)});
        }
        tryPromote(targets, arrayMap);
        populateParentChanges(targets, arrayMap);
        ArrayList<ChangeInfo> listSortedByZ = targets.getListSortedByZ();
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 405146734, 0, (String) null, new Object[]{String.valueOf(listSortedByZ)});
        }
        return listSortedByZ;
    }

    public static void populateParentChanges(Targets targets, ArrayMap<WindowContainer, ChangeInfo> arrayMap) {
        int i;
        boolean z;
        ChangeInfo changeInfo;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(targets.mArray.size());
        for (int size = targets.mArray.size() - 1; size >= 0; size--) {
            arrayList2.add(targets.mArray.valueAt(size));
        }
        for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
            ChangeInfo changeInfo2 = (ChangeInfo) arrayList2.get(size2);
            WindowContainer windowContainer = changeInfo2.mContainer;
            boolean isWallpaper = isWallpaper(windowContainer);
            arrayList.clear();
            WindowContainer<?> animatableParent = getAnimatableParent(windowContainer);
            while (true) {
                i = 0;
                if (animatableParent == null || (changeInfo = arrayMap.get(animatableParent)) == null || !changeInfo.hasChanged()) {
                    break;
                }
                if (animatableParent.mRemoteToken != null) {
                    if (changeInfo.mEndParent != null && !isWallpaper) {
                        changeInfo2.mEndParent = animatableParent;
                        break;
                    } else if (arrayList2.contains(changeInfo)) {
                        if (isWallpaper) {
                            changeInfo2.mEndParent = animatableParent;
                        } else {
                            arrayList.add(changeInfo);
                        }
                        z = true;
                    } else if (reportIfNotTop(animatableParent) && !isWallpaper) {
                        arrayList.add(changeInfo);
                    }
                }
                animatableParent = getAnimatableParent(animatableParent);
            }
            z = false;
            if (z && !arrayList.isEmpty()) {
                changeInfo2.mEndParent = ((ChangeInfo) arrayList.get(0)).mContainer;
                while (i < arrayList.size() - 1) {
                    ChangeInfo changeInfo3 = (ChangeInfo) arrayList.get(i);
                    i++;
                    changeInfo3.mEndParent = ((ChangeInfo) arrayList.get(i)).mContainer;
                    targets.add(changeInfo3);
                }
            }
        }
    }

    public static SurfaceControl getLeashSurface(WindowContainer windowContainer, SurfaceControl.Transaction transaction) {
        WindowToken asWindowToken;
        SurfaceControl fixedRotationLeash;
        DisplayContent asDisplayContent = windowContainer.asDisplayContent();
        if (asDisplayContent != null) {
            return asDisplayContent.getWindowingLayer();
        }
        if (!windowContainer.mTransitionController.useShellTransitionsRotation() && (asWindowToken = windowContainer.asWindowToken()) != null) {
            if (transaction != null) {
                fixedRotationLeash = asWindowToken.getOrCreateFixedRotationLeash(transaction);
            } else {
                fixedRotationLeash = asWindowToken.getFixedRotationLeash();
            }
            if (fixedRotationLeash != null) {
                return fixedRotationLeash;
            }
        }
        return windowContainer.getSurfaceControl();
    }

    public static SurfaceControl getOrigParentSurface(WindowContainer windowContainer) {
        if (windowContainer.asDisplayContent() != null) {
            return windowContainer.getSurfaceControl();
        }
        if (windowContainer.getParent().asDisplayContent() != null) {
            return windowContainer.getParent().asDisplayContent().getWindowingLayer();
        }
        return windowContainer.getParent().getSurfaceControl();
    }

    public static boolean isReadyGroup(WindowContainer windowContainer) {
        return windowContainer instanceof DisplayContent;
    }

    @VisibleForTesting
    public static TransitionInfo calculateTransitionInfo(int i, int i2, ArrayList<ChangeInfo> arrayList, SurfaceControl.Transaction transaction) {
        WindowContainer windowContainer;
        TransitionInfo.AnimationOptions animationOptions;
        TaskFragment organizedTaskFragment;
        Task task;
        int backgroundColor;
        SurfaceControl.Transaction transaction2 = transaction;
        TransitionInfo transitionInfo = new TransitionInfo(i, i2);
        int i3 = 0;
        while (true) {
            if (i3 >= arrayList.size()) {
                windowContainer = null;
                break;
            }
            windowContainer = arrayList.get(i3).mContainer;
            if (!isWallpaper(windowContainer)) {
                break;
            }
            i3++;
        }
        if (windowContainer == null) {
            transitionInfo.setRootLeash(new SurfaceControl(), 0, 0);
            return transitionInfo;
        }
        WindowContainer<?> findCommonAncestor = findCommonAncestor(arrayList, windowContainer);
        WindowContainer windowContainer2 = windowContainer;
        while (windowContainer2.getParent() != findCommonAncestor) {
            windowContainer2 = windowContainer2.getParent();
        }
        SurfaceControl build = windowContainer2.makeAnimationLeash().setName("Transition Root: " + windowContainer2.getName()).build();
        transaction2.setLayer(build, windowContainer2.getLastLayer());
        transitionInfo.setRootLeash(build, findCommonAncestor.getBounds().left, findCommonAncestor.getBounds().top);
        int size = arrayList.size();
        int i4 = 0;
        while (true) {
            boolean z = true;
            if (i4 >= size) {
                break;
            }
            ChangeInfo changeInfo = arrayList.get(i4);
            WindowContainer windowContainer3 = changeInfo.mContainer;
            WindowContainer.RemoteToken remoteToken = windowContainer3.mRemoteToken;
            TransitionInfo.Change change = new TransitionInfo.Change(remoteToken != null ? remoteToken.toWindowContainerToken() : null, getLeashSurface(windowContainer3, transaction2));
            WindowContainer windowContainer4 = changeInfo.mEndParent;
            if (windowContainer4 != null) {
                change.setParent(windowContainer4.mRemoteToken.toWindowContainerToken());
            }
            WindowContainer windowContainer5 = changeInfo.mStartParent;
            if (windowContainer5 != null && windowContainer5.mRemoteToken != null) {
                WindowContainer parent = windowContainer3.getParent();
                WindowContainer windowContainer6 = changeInfo.mStartParent;
                if (parent != windowContainer6) {
                    change.setLastParent(windowContainer6.mRemoteToken.toWindowContainerToken());
                }
            }
            change.setMode(changeInfo.getTransitMode(windowContainer3));
            change.setStartAbsBounds(changeInfo.mAbsoluteBounds);
            change.setFlags(changeInfo.getChangeFlags(windowContainer3));
            Task asTask = windowContainer3.asTask();
            TaskFragment asTaskFragment = windowContainer3.asTaskFragment();
            ActivityRecord asActivityRecord = windowContainer3.asActivityRecord();
            if (asTask != null) {
                ActivityManager.RunningTaskInfo runningTaskInfo = new ActivityManager.RunningTaskInfo();
                asTask.fillTaskInfo(runningTaskInfo);
                change.setTaskInfo(runningTaskInfo);
                change.setRotationAnimation(getTaskRotationAnimation(asTask));
                ActivityRecord topMostActivity = asTask.getTopMostActivity();
                change.setAllowEnterPip((topMostActivity == null || !topMostActivity.checkEnterPictureInPictureAppOpsState()) ? false : false);
                ActivityRecord activityRecord = asTask.topRunningActivity();
                if (activityRecord != null && asTask.mDisplayContent != null && !asTask.inMultiWindowMode()) {
                    int displayRotation = asTask.getWindowConfiguration().getDisplayRotation();
                    int displayRotation2 = activityRecord.getWindowConfiguration().getDisplayRotation();
                    if (displayRotation != displayRotation2) {
                        change.setEndFixedRotation(displayRotation2);
                    }
                }
            } else if ((1 & changeInfo.mFlags) != 0) {
                change.setRotationAnimation(3);
            }
            WindowContainer parent2 = windowContainer3.getParent();
            Rect bounds = windowContainer3.getBounds();
            Rect bounds2 = parent2.getBounds();
            int i5 = size;
            change.setEndRelOffset(bounds.left - bounds2.left, bounds.top - bounds2.top);
            int rotation = windowContainer3.getWindowConfiguration().getRotation();
            if (asActivityRecord != null) {
                change.setEndAbsBounds(bounds2);
                if (asActivityRecord.getRelativeDisplayRotation() != 0 && !asActivityRecord.mTransitionController.useShellTransitionsRotation()) {
                    rotation = parent2.getWindowConfiguration().getRotation();
                }
            } else {
                change.setEndAbsBounds(bounds);
            }
            if (asActivityRecord != null || (asTaskFragment != null && asTaskFragment.isEmbedded())) {
                if (asActivityRecord != null) {
                    organizedTaskFragment = asActivityRecord.getOrganizedTaskFragment();
                } else {
                    organizedTaskFragment = asTaskFragment.getOrganizedTaskFragment();
                }
                if (organizedTaskFragment != null && organizedTaskFragment.getAnimationParams().getAnimationBackgroundColor() != 0) {
                    backgroundColor = organizedTaskFragment.getAnimationParams().getAnimationBackgroundColor();
                } else {
                    if (asActivityRecord != null) {
                        task = asActivityRecord.getTask();
                    } else {
                        task = asTaskFragment.getTask();
                    }
                    backgroundColor = task.getTaskDescription().getBackgroundColor();
                }
                change.setBackgroundColor(ColorUtils.setAlphaComponent(backgroundColor, 255));
            }
            change.setRotation(changeInfo.mRotation, rotation);
            SurfaceControl surfaceControl = changeInfo.mSnapshot;
            if (surfaceControl != null) {
                change.setSnapshot(surfaceControl, changeInfo.mSnapshotLuma);
            }
            transitionInfo.addChange(change);
            i4++;
            transaction2 = transaction;
            size = i5;
        }
        if (windowContainer.asActivityRecord() != null) {
            ActivityRecord asActivityRecord2 = windowContainer.asActivityRecord();
            animationOptions = addCustomActivityTransition(asActivityRecord2, false, addCustomActivityTransition(asActivityRecord2, true, null));
        } else {
            animationOptions = null;
        }
        WindowManager.LayoutParams layoutParamsForAnimationsStyle = getLayoutParamsForAnimationsStyle(i, arrayList);
        if (layoutParamsForAnimationsStyle != null && layoutParamsForAnimationsStyle.type != 3 && layoutParamsForAnimationsStyle.windowAnimations != 0) {
            if (animationOptions != null) {
                animationOptions.addOptionsFromLayoutParameters(layoutParamsForAnimationsStyle);
            } else {
                animationOptions = TransitionInfo.AnimationOptions.makeAnimOptionsFromLayoutParameters(layoutParamsForAnimationsStyle);
            }
        }
        if (animationOptions != null) {
            transitionInfo.setAnimationOptions(animationOptions);
        }
        return transitionInfo;
    }

    public static TransitionInfo.AnimationOptions addCustomActivityTransition(ActivityRecord activityRecord, boolean z, TransitionInfo.AnimationOptions animationOptions) {
        ActivityRecord.CustomAppTransition customAnimation = activityRecord.getCustomAnimation(z);
        if (customAnimation != null) {
            if (animationOptions == null) {
                animationOptions = TransitionInfo.AnimationOptions.makeCommonAnimOptions(activityRecord.packageName);
            }
            animationOptions.addCustomActivityTransition(z, customAnimation.mEnterAnim, customAnimation.mExitAnim, customAnimation.mBackgroundColor);
        }
        return animationOptions;
    }

    public static WindowContainer<?> findCommonAncestor(ArrayList<ChangeInfo> arrayList, WindowContainer<?> windowContainer) {
        WindowContainer<?> parent = windowContainer.getParent();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ChangeInfo changeInfo = arrayList.get(size);
            WindowContainer windowContainer2 = changeInfo.mContainer;
            if (!isWallpaper(windowContainer2)) {
                while (!windowContainer2.isDescendantOf(parent)) {
                    parent = parent.getParent();
                }
                WindowContainer<?> windowContainer3 = changeInfo.mCommonAncestor;
                if (windowContainer3 != null && windowContainer3.isAttached()) {
                    while (windowContainer3 != parent && !windowContainer3.isDescendantOf(parent)) {
                        parent = parent.getParent();
                    }
                }
            }
        }
        return parent;
    }

    public static WindowManager.LayoutParams getLayoutParamsForAnimationsStyle(int i, ArrayList<ChangeInfo> arrayList) {
        ArraySet arraySet = new ArraySet();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            WindowContainer windowContainer = arrayList.get(i2).mContainer;
            if (windowContainer.asActivityRecord() != null) {
                arraySet.add(Integer.valueOf(windowContainer.getActivityType()));
            } else if (windowContainer.asWindowToken() == null && windowContainer.asWindowState() == null) {
                return null;
            }
        }
        if (arraySet.isEmpty()) {
            return null;
        }
        ActivityRecord findAnimLayoutParamsActivityRecord = findAnimLayoutParamsActivityRecord(arrayList, i, arraySet);
        WindowState findMainWindow = findAnimLayoutParamsActivityRecord != null ? findAnimLayoutParamsActivityRecord.findMainWindow() : null;
        if (findMainWindow != null) {
            return findMainWindow.mAttrs;
        }
        return null;
    }

    public static ActivityRecord findAnimLayoutParamsActivityRecord(List<ChangeInfo> list, final int i, final ArraySet<Integer> arraySet) {
        ActivityRecord lookForTopWindowWithFilter = lookForTopWindowWithFilter(list, new Predicate() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAnimLayoutParamsActivityRecord$3;
                lambda$findAnimLayoutParamsActivityRecord$3 = Transition.lambda$findAnimLayoutParamsActivityRecord$3(i, arraySet, (ActivityRecord) obj);
                return lambda$findAnimLayoutParamsActivityRecord$3;
            }
        });
        if (lookForTopWindowWithFilter != null) {
            return lookForTopWindowWithFilter;
        }
        ActivityRecord lookForTopWindowWithFilter2 = lookForTopWindowWithFilter(list, new Predicate() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAnimLayoutParamsActivityRecord$4;
                lambda$findAnimLayoutParamsActivityRecord$4 = Transition.lambda$findAnimLayoutParamsActivityRecord$4((ActivityRecord) obj);
                return lambda$findAnimLayoutParamsActivityRecord$4;
            }
        });
        return lookForTopWindowWithFilter2 != null ? lookForTopWindowWithFilter2 : lookForTopWindowWithFilter(list, new Predicate() { // from class: com.android.server.wm.Transition$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAnimLayoutParamsActivityRecord$5;
                lambda$findAnimLayoutParamsActivityRecord$5 = Transition.lambda$findAnimLayoutParamsActivityRecord$5((ActivityRecord) obj);
                return lambda$findAnimLayoutParamsActivityRecord$5;
            }
        });
    }

    public static /* synthetic */ boolean lambda$findAnimLayoutParamsActivityRecord$3(int i, ArraySet arraySet, ActivityRecord activityRecord) {
        return activityRecord.getRemoteAnimationDefinition() != null && activityRecord.getRemoteAnimationDefinition().hasTransition(i, arraySet);
    }

    public static /* synthetic */ boolean lambda$findAnimLayoutParamsActivityRecord$4(ActivityRecord activityRecord) {
        return activityRecord.fillsParent() && activityRecord.findMainWindow() != null;
    }

    public static /* synthetic */ boolean lambda$findAnimLayoutParamsActivityRecord$5(ActivityRecord activityRecord) {
        return activityRecord.findMainWindow() != null;
    }

    public static ActivityRecord lookForTopWindowWithFilter(List<ChangeInfo> list, Predicate<ActivityRecord> predicate) {
        ActivityRecord asActivityRecord;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            WindowContainer windowContainer = list.get(i).mContainer;
            if (windowContainer.asTaskFragment() != null) {
                asActivityRecord = windowContainer.asTaskFragment().getTopNonFinishingActivity();
            } else {
                asActivityRecord = windowContainer.asActivityRecord();
            }
            if (asActivityRecord != null && predicate.test(asActivityRecord)) {
                return asActivityRecord;
            }
        }
        return null;
    }

    public static int getTaskRotationAnimation(Task task) {
        WindowState findMainWindow;
        ActivityRecord topVisibleActivity = task.getTopVisibleActivity();
        if (topVisibleActivity == null || (findMainWindow = topVisibleActivity.findMainWindow(false)) == null) {
            return -1;
        }
        int rotationAnimationHint = findMainWindow.getRotationAnimationHint();
        if (rotationAnimationHint >= 0) {
            return rotationAnimationHint;
        }
        int i = findMainWindow.getAttrs().rotationAnimation;
        if (i != 3) {
            return i;
        }
        if (findMainWindow == task.mDisplayContent.getDisplayPolicy().getTopFullscreenOpaqueWindow() && topVisibleActivity.matchParentBounds()) {
            return findMainWindow.getAttrs().rotationAnimation;
        }
        return -1;
    }

    public void applyDisplayChangeIfNeeded() {
        for (int size = this.mParticipants.size() - 1; size >= 0; size--) {
            DisplayContent asDisplayContent = this.mParticipants.valueAt(size).asDisplayContent();
            if (asDisplayContent != null && this.mChanges.get(asDisplayContent).hasChanged()) {
                asDisplayContent.sendNewConfiguration();
                if (!this.mReadyTracker.mUsed) {
                    setReady(asDisplayContent, true);
                }
            }
        }
    }

    public boolean getLegacyIsReady() {
        return isCollecting() && this.mSyncId >= 0;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.Transition$ChangeInfo */
    /* loaded from: classes2.dex */
    public static class ChangeInfo {
        public final Rect mAbsoluteBounds;
        public WindowContainer mCommonAncestor;
        public final WindowContainer mContainer;
        public WindowContainer mEndParent;
        public boolean mExistenceChanged;
        public int mFlags;
        public int mKnownConfigChanges;
        public int mRotation;
        public boolean mShowWallpaper;
        public SurfaceControl mSnapshot;
        public float mSnapshotLuma;
        public WindowContainer mStartParent;
        public boolean mVisible;
        public int mWindowingMode;

        public ChangeInfo(WindowContainer windowContainer) {
            this.mExistenceChanged = false;
            Rect rect = new Rect();
            this.mAbsoluteBounds = rect;
            this.mRotation = -1;
            this.mFlags = 0;
            this.mContainer = windowContainer;
            this.mVisible = windowContainer.isVisibleRequested();
            this.mWindowingMode = windowContainer.getWindowingMode();
            rect.set(windowContainer.getBounds());
            this.mShowWallpaper = windowContainer.showWallpaper();
            this.mRotation = windowContainer.getWindowConfiguration().getRotation();
            this.mStartParent = windowContainer.getParent();
        }

        @VisibleForTesting
        public ChangeInfo(WindowContainer windowContainer, boolean z, boolean z2) {
            this(windowContainer);
            this.mVisible = z;
            this.mExistenceChanged = z2;
            this.mShowWallpaper = false;
        }

        public String toString() {
            return this.mContainer.toString();
        }

        public boolean hasChanged() {
            int i = this.mFlags;
            if ((i & 2) == 0 && (i & 4) == 0) {
                boolean isVisibleRequested = this.mContainer.isVisibleRequested();
                boolean z = this.mVisible;
                if (isVisibleRequested != z || z) {
                    if (isVisibleRequested == z && this.mKnownConfigChanges == 0) {
                        return ((this.mWindowingMode == 0 || this.mContainer.getWindowingMode() == this.mWindowingMode) && this.mContainer.getBounds().equals(this.mAbsoluteBounds) && this.mRotation == this.mContainer.getWindowConfiguration().getRotation()) ? false : true;
                    }
                    return true;
                }
                return false;
            }
            return true;
        }

        @TransitionInfo.TransitionMode
        public int getTransitMode(WindowContainer windowContainer) {
            if ((this.mFlags & 4) != 0) {
                return this.mExistenceChanged ? 2 : 4;
            }
            boolean isVisibleRequested = windowContainer.isVisibleRequested();
            if (isVisibleRequested == this.mVisible) {
                return 6;
            }
            return this.mExistenceChanged ? isVisibleRequested ? 1 : 2 : isVisibleRequested ? 3 : 4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x0038, code lost:
            if (r3.mAtmService.mBackNavigationController.isMonitorTransitionTarget(r3) != false) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x0043, code lost:
            if (r1.mAtmService.mBackNavigationController.isMonitorTransitionTarget(r1) != false) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:26:0x0045, code lost:
            r0 = r0 | android.p005os.IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x0048, code lost:
            if (r1.voiceSession == null) goto L26;
         */
        /* JADX WARN: Code restructure failed: missing block: B:29:0x004a, code lost:
            r0 = r0 | 16;
         */
        @TransitionInfo.ChangeFlags
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int getChangeFlags(WindowContainer windowContainer) {
            Task task;
            int i = (this.mShowWallpaper || windowContainer.showWallpaper()) ? 1 : 0;
            if (Transition.isTranslucent(windowContainer)) {
                i |= 4;
            }
            Task asTask = windowContainer.asTask();
            if (asTask != null) {
                ActivityRecord topNonFinishingActivity = asTask.getTopNonFinishingActivity();
                if (topNonFinishingActivity != null) {
                    StartingData startingData = topNonFinishingActivity.mStartingData;
                    if (startingData != null && startingData.hasImeSurface()) {
                        i |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
                    }
                }
            }
            ActivityRecord asActivityRecord = windowContainer.asActivityRecord();
            if (asActivityRecord != null) {
                task = asActivityRecord.getTask();
                if (asActivityRecord.mVoiceInteraction) {
                    i |= 16;
                }
                i |= asActivityRecord.mTransitionChangeFlags;
                if (asActivityRecord.mAtmService.mBackNavigationController.isMonitorTransitionTarget(asActivityRecord)) {
                    i |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                }
            } else {
                task = null;
            }
            TaskFragment asTaskFragment = windowContainer.asTaskFragment();
            if (asTaskFragment != null && asTask == null) {
                task = asTaskFragment.getTask();
            }
            if (task != null) {
                if (task.forAllLeafTaskFragments(new ActivityRecord$$ExternalSyntheticLambda30())) {
                    i |= 512;
                }
                if (task.forAllActivities(new Transition$ChangeInfo$$ExternalSyntheticLambda0())) {
                    i |= 16384;
                }
                if (isWindowFillingTask(windowContainer, task)) {
                    i |= 1024;
                }
            } else {
                DisplayContent asDisplayContent = windowContainer.asDisplayContent();
                if (asDisplayContent != null) {
                    i |= 32;
                    if (asDisplayContent.hasAlertWindowSurfaces()) {
                        i |= 128;
                    }
                } else if (Transition.isWallpaper(windowContainer)) {
                    i |= 2;
                } else if (Transition.isInputMethod(windowContainer)) {
                    i |= 256;
                } else {
                    int windowType = windowContainer.getWindowType();
                    if (windowType >= 2000 && windowType <= 2999) {
                        i |= 65536;
                    }
                }
            }
            if (Transition.occludesKeyguard(windowContainer)) {
                i |= 64;
            }
            int i2 = this.mFlags;
            return ((i2 & 8) == 0 || (i2 & 16) != 0) ? i : i | 262144;
        }

        public final boolean isWindowFillingTask(WindowContainer windowContainer, Task task) {
            Rect bounds = task.getBounds();
            int width = bounds.width();
            int height = bounds.height();
            Rect rect = this.mAbsoluteBounds;
            Rect bounds2 = windowContainer.getBounds();
            return (!this.mVisible || (width == rect.width() && height == rect.height())) && (!windowContainer.isVisibleRequested() || (width == bounds2.width() && height == bounds2.height()));
        }
    }

    public void deferTransitionReady() {
        this.mReadyTracker.mDeferReadyDepth++;
        this.mSyncEngine.setReady(this.mSyncId, false);
    }

    public void continueTransitionReady() {
        ReadyTracker readyTracker = this.mReadyTracker;
        readyTracker.mDeferReadyDepth--;
        applyReady();
    }

    /* renamed from: com.android.server.wm.Transition$ReadyTracker */
    /* loaded from: classes2.dex */
    public static class ReadyTracker {
        public int mDeferReadyDepth;
        public final ArrayMap<WindowContainer, Boolean> mReadyGroups;
        public boolean mReadyOverride;
        public boolean mUsed;

        public ReadyTracker() {
            this.mReadyGroups = new ArrayMap<>();
            this.mUsed = false;
            this.mReadyOverride = false;
            this.mDeferReadyDepth = 0;
        }

        public void addGroup(WindowContainer windowContainer) {
            if (this.mReadyGroups.containsKey(windowContainer)) {
                Slog.e("Transition", "Trying to add a ready-group twice: " + windowContainer);
                return;
            }
            this.mReadyGroups.put(windowContainer, Boolean.FALSE);
        }

        public void setReadyFrom(WindowContainer windowContainer, boolean z) {
            this.mUsed = true;
            for (WindowContainer windowContainer2 = windowContainer; windowContainer2 != null; windowContainer2 = windowContainer2.getParent()) {
                if (Transition.isReadyGroup(windowContainer2)) {
                    this.mReadyGroups.put(windowContainer2, Boolean.valueOf(z));
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1924376693, 3, (String) null, new Object[]{Boolean.valueOf(z), String.valueOf(windowContainer2), String.valueOf(windowContainer)});
                        return;
                    }
                    return;
                }
            }
        }

        public void setAllReady() {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1670933628, 0, (String) null, (Object[]) null);
            }
            this.mUsed = true;
            this.mReadyOverride = true;
        }

        public boolean allReady() {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1383884640, 31, (String) null, new Object[]{Boolean.valueOf(this.mUsed), Boolean.valueOf(this.mReadyOverride), Long.valueOf(this.mDeferReadyDepth), String.valueOf(groupsToString())});
            }
            if (this.mUsed && this.mDeferReadyDepth <= 0) {
                if (this.mReadyOverride) {
                    return true;
                }
                for (int size = this.mReadyGroups.size() - 1; size >= 0; size--) {
                    WindowContainer keyAt = this.mReadyGroups.keyAt(size);
                    if (keyAt.isAttached() && keyAt.isVisibleRequested() && !this.mReadyGroups.valueAt(size).booleanValue()) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public final String groupsToString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.mReadyGroups.size(); i++) {
                if (i != 0) {
                    sb.append(',');
                }
                sb.append(this.mReadyGroups.keyAt(i));
                sb.append(':');
                sb.append(this.mReadyGroups.valueAt(i));
            }
            return sb.toString();
        }
    }

    /* renamed from: com.android.server.wm.Transition$Targets */
    /* loaded from: classes2.dex */
    public static class Targets {
        public final SparseArray<ChangeInfo> mArray;
        public int mDepthFactor;
        public ArrayList<ChangeInfo> mRemovedTargets;

        public Targets() {
            this.mArray = new SparseArray<>();
        }

        public void add(ChangeInfo changeInfo) {
            if (this.mDepthFactor == 0) {
                this.mDepthFactor = changeInfo.mContainer.mWmService.mRoot.getTreeWeight() + 1;
            }
            int prefixOrderIndex = changeInfo.mContainer.getPrefixOrderIndex();
            WindowContainer windowContainer = changeInfo.mContainer;
            while (windowContainer != null) {
                windowContainer = windowContainer.getParent();
                if (windowContainer != null) {
                    prefixOrderIndex += this.mDepthFactor;
                }
            }
            this.mArray.put(prefixOrderIndex, changeInfo);
        }

        public void remove(int i) {
            ChangeInfo valueAt = this.mArray.valueAt(i);
            this.mArray.removeAt(i);
            if (this.mRemovedTargets == null) {
                this.mRemovedTargets = new ArrayList<>();
            }
            this.mRemovedTargets.add(valueAt);
        }

        public boolean wasParticipated(ChangeInfo changeInfo) {
            ArrayList<ChangeInfo> arrayList;
            return this.mArray.indexOfValue(changeInfo) >= 0 || ((arrayList = this.mRemovedTargets) != null && arrayList.contains(changeInfo));
        }

        public ArrayList<ChangeInfo> getListSortedByZ() {
            SparseArray sparseArray = new SparseArray(this.mArray.size());
            for (int size = this.mArray.size() - 1; size >= 0; size--) {
                sparseArray.put(this.mArray.keyAt(size) % this.mDepthFactor, this.mArray.valueAt(size));
            }
            ArrayList<ChangeInfo> arrayList = new ArrayList<>(sparseArray.size());
            for (int size2 = sparseArray.size() - 1; size2 >= 0; size2--) {
                arrayList.add((ChangeInfo) sparseArray.valueAt(size2));
            }
            return arrayList;
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.Transition$ScreenshotFreezer */
    /* loaded from: classes2.dex */
    public class ScreenshotFreezer implements IContainerFreezer {
        public final ArraySet<WindowContainer> mFrozen;

        public ScreenshotFreezer() {
            this.mFrozen = new ArraySet<>();
        }

        @Override // com.android.server.p014wm.Transition.IContainerFreezer
        public boolean freeze(WindowContainer windowContainer, Rect rect) {
            boolean z = false;
            if (windowContainer.isVisibleRequested()) {
                for (WindowContainer windowContainer2 = windowContainer; windowContainer2 != null; windowContainer2 = windowContainer2.getParent()) {
                    if (this.mFrozen.contains(windowContainer2)) {
                        return false;
                    }
                }
                if (Transition.this.mIsSeamlessRotation) {
                    WindowState topFullscreenOpaqueWindow = windowContainer.getDisplayContent() == null ? null : windowContainer.getDisplayContent().getDisplayPolicy().getTopFullscreenOpaqueWindow();
                    if (topFullscreenOpaqueWindow != null && (topFullscreenOpaqueWindow == windowContainer || topFullscreenOpaqueWindow.isDescendantOf(windowContainer))) {
                        this.mFrozen.add(windowContainer);
                        return true;
                    }
                }
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 390947100, 0, (String) null, new Object[]{String.valueOf(windowContainer.toString()), String.valueOf(rect.toString())});
                }
                Rect rect2 = new Rect(rect);
                rect2.offsetTo(0, 0);
                ScreenCapture.ScreenshotHardwareBuffer captureLayers = ScreenCapture.captureLayers(new ScreenCapture.LayerCaptureArgs.Builder(windowContainer.getSurfaceControl()).setSourceCrop(rect2).setCaptureSecureLayers(true).setAllowProtected(true).build());
                HardwareBuffer hardwareBuffer = captureLayers == null ? null : captureLayers.getHardwareBuffer();
                if (hardwareBuffer == null || hardwareBuffer.getWidth() <= 1 || hardwareBuffer.getHeight() <= 1) {
                    Slog.w("Transition", "Failed to capture screenshot for " + windowContainer);
                    return false;
                }
                if (windowContainer.asDisplayContent() != null && windowContainer.asDisplayContent().isRotationChanging()) {
                    z = true;
                }
                SurfaceControl build = windowContainer.makeAnimationLeash().setName(z ? "RotationLayer" : "transition snapshot: " + windowContainer).setOpaque(true).setParent(windowContainer.getSurfaceControl()).setSecure(captureLayers.containsSecureLayers()).setCallsite("Transition.ScreenshotSync").setBLASTLayer().build();
                this.mFrozen.add(windowContainer);
                ChangeInfo changeInfo = Transition.this.mChanges.get(windowContainer);
                Objects.requireNonNull(changeInfo);
                changeInfo.mSnapshot = build;
                if (z) {
                    changeInfo.mSnapshotLuma = TransitionAnimation.getBorderLuma(hardwareBuffer, captureLayers.getColorSpace());
                }
                SurfaceControl.Transaction transaction = windowContainer.mWmService.mTransactionFactory.get();
                transaction.setBuffer(build, hardwareBuffer);
                transaction.setDataSpace(build, captureLayers.getColorSpace().getDataSpace());
                transaction.show(build);
                transaction.setLayer(build, Integer.MAX_VALUE);
                transaction.apply();
                transaction.close();
                hardwareBuffer.close();
                windowContainer.getSyncTransaction().reparent(build, null);
                return true;
            }
            return false;
        }

        @Override // com.android.server.p014wm.Transition.IContainerFreezer
        public void cleanUp(SurfaceControl.Transaction transaction) {
            for (int i = 0; i < this.mFrozen.size(); i++) {
                ChangeInfo changeInfo = Transition.this.mChanges.get(this.mFrozen.valueAt(i));
                Objects.requireNonNull(changeInfo);
                SurfaceControl surfaceControl = changeInfo.mSnapshot;
                if (surfaceControl != null) {
                    transaction.reparent(surfaceControl, null);
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.Transition$Token */
    /* loaded from: classes2.dex */
    public static class Token extends Binder {
        public final WeakReference<Transition> mTransition;

        public Token(Transition transition) {
            this.mTransition = new WeakReference<>(transition);
        }

        public String toString() {
            return "Token{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mTransition.get() + "}";
        }
    }
}
