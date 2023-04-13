package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.IApplicationThread;
import android.app.WindowConfiguration;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.window.ITransitionMetricsReporter;
import android.window.ITransitionPlayer;
import android.window.RemoteTransition;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.FgThread;
import com.android.server.p014wm.Transition;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
/* renamed from: com.android.server.wm.TransitionController */
/* loaded from: classes2.dex */
public class TransitionController {
    public static final boolean SHELL_TRANSITIONS_ROTATION = SystemProperties.getBoolean("persist.wm.debug.shell_transit_rotate", false);
    public static final int SYNC_METHOD = SystemProperties.getBoolean("persist.wm.debug.shell_transit_blast", false) ? 1 : 0;
    public final ActivityTaskManagerService mAtm;
    public final RemotePlayer mRemotePlayer;
    public TaskSnapshotController mTaskSnapshotController;
    public ITransitionPlayer mTransitionPlayer;
    public WindowProcessController mTransitionPlayerProc;
    public TransitionTracer mTransitionTracer;
    public final TransitionMetricsReporter mTransitionMetricsReporter = new TransitionMetricsReporter();
    public final ArrayList<WindowManagerInternal.AppTransitionListener> mLegacyListeners = new ArrayList<>();
    public final ArrayList<Runnable> mStateValidators = new ArrayList<>();
    public final ArrayList<Transition> mPlayingTransitions = new ArrayList<>();
    public final ArrayList<WindowState> mAnimatingExitWindows = new ArrayList<>();
    public final Lock mRunningLock = new Lock();
    public Transition mCollectingTransition = null;
    public boolean mBuildingFinishLayers = false;
    public boolean mAnimatingState = false;
    public final Handler mLoggerHandler = FgThread.getHandler();
    public boolean mIsWaitingForDisplayEnabled = false;
    public final IBinder.DeathRecipient mTransitionPlayerDeath = new IBinder.DeathRecipient() { // from class: com.android.server.wm.TransitionController$$ExternalSyntheticLambda0
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            TransitionController.this.lambda$new$0();
        }
    };

    public static boolean isExistenceType(int i) {
        return i == 1 || i == 2;
    }

    public TransitionController(ActivityTaskManagerService activityTaskManagerService) {
        this.mAtm = activityTaskManagerService;
        this.mRemotePlayer = new RemotePlayer(activityTaskManagerService);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                detachPlayer();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mTaskSnapshotController = windowManagerService.mTaskSnapshotController;
        this.mTransitionTracer = windowManagerService.mTransitionTracer;
        this.mIsWaitingForDisplayEnabled = !windowManagerService.mDisplayEnabled;
        registerLegacyListener(windowManagerService.mActivityManagerAppTransitionNotifier);
    }

    public final void detachPlayer() {
        if (this.mTransitionPlayer == null) {
            return;
        }
        for (int i = 0; i < this.mPlayingTransitions.size(); i++) {
            this.mPlayingTransitions.get(i).cleanUpOnFailure();
        }
        this.mPlayingTransitions.clear();
        Transition transition = this.mCollectingTransition;
        if (transition != null) {
            transition.abort();
        }
        this.mTransitionPlayer = null;
        this.mTransitionPlayerProc = null;
        this.mRemotePlayer.clear();
        this.mRunningLock.doNotifyLocked();
    }

    public Transition createTransition(int i) {
        return createTransition(i, 0);
    }

    public final Transition createTransition(int i, int i2) {
        if (this.mTransitionPlayer == null) {
            throw new IllegalStateException("Shell Transitions not enabled");
        }
        if (this.mCollectingTransition != null) {
            throw new IllegalStateException("Simultaneous transition collection not supported yet. Use {@link #createPendingTransition} for explicit queueing.");
        }
        Transition transition = new Transition(i, i2, this, this.mAtm.mWindowManager.mSyncEngine);
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 259206414, 0, (String) null, new Object[]{String.valueOf(transition)});
        }
        moveToCollecting(transition);
        return transition;
    }

    public void moveToCollecting(Transition transition) {
        moveToCollecting(transition, SYNC_METHOD);
    }

    @VisibleForTesting
    public void moveToCollecting(Transition transition, int i) {
        if (this.mCollectingTransition != null) {
            throw new IllegalStateException("Simultaneous transition collection not supported.");
        }
        if (this.mTransitionPlayer == null) {
            transition.abort();
            return;
        }
        this.mCollectingTransition = transition;
        transition.startCollecting(transition.mType == 6 ? 2000L : 5000L, i);
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1764792832, 0, (String) null, new Object[]{String.valueOf(this.mCollectingTransition)});
        }
        dispatchLegacyAppTransitionPending();
    }

    public void registerTransitionPlayer(ITransitionPlayer iTransitionPlayer, WindowProcessController windowProcessController) {
        try {
            ITransitionPlayer iTransitionPlayer2 = this.mTransitionPlayer;
            if (iTransitionPlayer2 != null) {
                if (iTransitionPlayer2.asBinder() != null) {
                    this.mTransitionPlayer.asBinder().unlinkToDeath(this.mTransitionPlayerDeath, 0);
                }
                detachPlayer();
            }
            if (iTransitionPlayer.asBinder() != null) {
                iTransitionPlayer.asBinder().linkToDeath(this.mTransitionPlayerDeath, 0);
            }
            this.mTransitionPlayer = iTransitionPlayer;
            this.mTransitionPlayerProc = windowProcessController;
        } catch (RemoteException unused) {
            throw new RuntimeException("Unable to set transition player");
        }
    }

    public ITransitionPlayer getTransitionPlayer() {
        return this.mTransitionPlayer;
    }

    public boolean isShellTransitionsEnabled() {
        return this.mTransitionPlayer != null;
    }

    public boolean useShellTransitionsRotation() {
        return isShellTransitionsEnabled() && SHELL_TRANSITIONS_ROTATION;
    }

    public boolean isCollecting() {
        return this.mCollectingTransition != null;
    }

    public Transition getCollectingTransition() {
        return this.mCollectingTransition;
    }

    public int getCollectingTransitionId() {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            throw new IllegalStateException("There is no collecting transition");
        }
        return transition.getSyncId();
    }

    public boolean isCollecting(WindowContainer windowContainer) {
        Transition transition = this.mCollectingTransition;
        return transition != null && transition.mParticipants.contains(windowContainer);
    }

    public boolean inCollectingTransition(WindowContainer windowContainer) {
        if (isCollecting()) {
            return this.mCollectingTransition.isInTransition(windowContainer);
        }
        return false;
    }

    public boolean isPlaying() {
        return !this.mPlayingTransitions.isEmpty();
    }

    public boolean inPlayingTransition(WindowContainer windowContainer) {
        for (int size = this.mPlayingTransitions.size() - 1; size >= 0; size--) {
            if (this.mPlayingTransitions.get(size).isInTransition(windowContainer)) {
                return true;
            }
        }
        return false;
    }

    public boolean inTransition() {
        return isCollecting() || isPlaying();
    }

    public boolean inTransition(WindowContainer windowContainer) {
        return inCollectingTransition(windowContainer) || inPlayingTransition(windowContainer);
    }

    public boolean inRecentsTransition(WindowContainer windowContainer) {
        Transition transition;
        for (WindowContainer windowContainer2 = windowContainer; windowContainer2 != null && (transition = this.mCollectingTransition) != null; windowContainer2 = windowContainer2.getParent()) {
            if ((transition.getFlags() & 128) != 0 && this.mCollectingTransition.mParticipants.contains(windowContainer)) {
                return true;
            }
        }
        for (int size = this.mPlayingTransitions.size() - 1; size >= 0; size--) {
            for (WindowContainer windowContainer3 = windowContainer; windowContainer3 != null; windowContainer3 = windowContainer3.getParent()) {
                if ((this.mPlayingTransitions.get(size).getFlags() & 128) != 0 && this.mPlayingTransitions.get(size).mParticipants.contains(windowContainer3)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTransitionOnDisplay(DisplayContent displayContent) {
        Transition transition = this.mCollectingTransition;
        if (transition == null || !transition.isOnDisplay(displayContent)) {
            for (int size = this.mPlayingTransitions.size() - 1; size >= 0; size--) {
                if (this.mPlayingTransitions.get(size).isOnDisplay(displayContent)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isTransientHide(Task task) {
        Transition transition = this.mCollectingTransition;
        if (transition == null || !transition.isTransientHide(task)) {
            for (int size = this.mPlayingTransitions.size() - 1; size >= 0; size--) {
                if (this.mPlayingTransitions.get(size).isTransientHide(task)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean shouldKeepFocus(WindowContainer windowContainer) {
        if (this.mCollectingTransition != null) {
            if (this.mPlayingTransitions.isEmpty()) {
                return this.mCollectingTransition.isInTransientHide(windowContainer);
            }
            return false;
        } else if (this.mPlayingTransitions.size() == 1) {
            return this.mPlayingTransitions.get(0).isInTransientHide(windowContainer);
        } else {
            return false;
        }
    }

    public boolean isTransientCollect(ActivityRecord activityRecord) {
        Transition transition = this.mCollectingTransition;
        return transition != null && transition.isTransientLaunch(activityRecord);
    }

    public boolean isTransientLaunch(ActivityRecord activityRecord) {
        if (isTransientCollect(activityRecord)) {
            return true;
        }
        for (int size = this.mPlayingTransitions.size() - 1; size >= 0; size--) {
            if (this.mPlayingTransitions.get(size).isTransientLaunch(activityRecord)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAssignLayers() {
        return this.mBuildingFinishLayers || !isPlaying();
    }

    @WindowConfiguration.WindowingMode
    public int getWindowingModeAtStart(WindowContainer windowContainer) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return windowContainer.getWindowingMode();
        }
        Transition.ChangeInfo changeInfo = transition.mChanges.get(windowContainer);
        if (changeInfo == null) {
            return windowContainer.getWindowingMode();
        }
        return changeInfo.mWindowingMode;
    }

    public int getCollectingTransitionType() {
        Transition transition = this.mCollectingTransition;
        if (transition != null) {
            return transition.mType;
        }
        return 0;
    }

    public Transition requestTransitionIfNeeded(int i, WindowContainer windowContainer) {
        return requestTransitionIfNeeded(i, 0, windowContainer, windowContainer);
    }

    public Transition requestTransitionIfNeeded(int i, int i2, WindowContainer windowContainer, WindowContainer windowContainer2) {
        return requestTransitionIfNeeded(i, i2, windowContainer, windowContainer2, null, null);
    }

    public Transition requestTransitionIfNeeded(int i, int i2, WindowContainer windowContainer, WindowContainer windowContainer2, RemoteTransition remoteTransition, TransitionRequestInfo.DisplayChange displayChange) {
        r1 = null;
        Transition requestStartTransition = null;
        if (this.mTransitionPlayer == null) {
            return null;
        }
        if (isCollecting()) {
            if (displayChange != null) {
                Slog.e("TransitionController", "Provided displayChange for a non-new request", new Throwable());
            }
            this.mCollectingTransition.setReady(windowContainer2, false);
            if ((i2 & 256) != 0) {
                this.mCollectingTransition.addFlag(i2);
            }
        } else {
            requestStartTransition = requestStartTransition(createTransition(i, i2), windowContainer != null ? windowContainer.asTask() : null, remoteTransition, displayChange);
        }
        if (windowContainer != null) {
            if (isExistenceType(i)) {
                collectExistenceChange(windowContainer);
            } else {
                collect(windowContainer);
            }
        }
        return requestStartTransition;
    }

    public Transition requestStartTransition(final Transition transition, Task task, RemoteTransition remoteTransition, TransitionRequestInfo.DisplayChange displayChange) {
        ActivityManager.RunningTaskInfo runningTaskInfo = null;
        if (this.mIsWaitingForDisplayEnabled) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1282992082, 1, (String) null, new Object[]{Long.valueOf(transition.getSyncId())});
            }
            transition.mIsPlayerEnabled = false;
            transition.mLogger.mRequestTimeNs = SystemClock.uptimeNanos();
            this.mAtm.f1161mH.post(new Runnable() { // from class: com.android.server.wm.TransitionController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    TransitionController.this.lambda$requestStartTransition$1(transition);
                }
            });
            return transition;
        } else if (this.mTransitionPlayer == null || transition.isAborted()) {
            if (transition.isCollecting()) {
                transition.abort();
            }
            return transition;
        } else {
            try {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1794249572, 0, (String) null, new Object[]{String.valueOf(transition)});
                }
                if (task != null) {
                    runningTaskInfo = new ActivityManager.RunningTaskInfo();
                    task.fillTaskInfo(runningTaskInfo);
                }
                TransitionRequestInfo transitionRequestInfo = new TransitionRequestInfo(transition.mType, runningTaskInfo, remoteTransition, displayChange);
                transition.mLogger.mRequestTimeNs = SystemClock.elapsedRealtimeNanos();
                transition.mLogger.mRequest = transitionRequestInfo;
                this.mTransitionPlayer.requestStartTransition(transition.getToken(), transitionRequestInfo);
                transition.setRemoteTransition(remoteTransition);
            } catch (RemoteException e) {
                Slog.e("TransitionController", "Error requesting transition", e);
                transition.start();
            }
            return transition;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestStartTransition$1(Transition transition) {
        this.mAtm.mWindowOrganizerController.startTransition(transition.getToken(), null);
    }

    public void requestCloseTransitionIfNeeded(WindowContainer<?> windowContainer) {
        if (this.mTransitionPlayer == null) {
            return;
        }
        if (windowContainer.isVisibleRequested()) {
            if (!isCollecting()) {
                requestStartTransition(createTransition(2, 0), windowContainer.asTask(), null, null);
            }
            collectExistenceChange(windowContainer);
            return;
        }
        collect(windowContainer);
    }

    public void collect(WindowContainer windowContainer) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.collect(windowContainer);
    }

    public void collectExistenceChange(WindowContainer windowContainer) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.collectExistenceChange(windowContainer);
    }

    public void collectForDisplayAreaChange(DisplayArea<?> displayArea) {
        final Transition transition = this.mCollectingTransition;
        if (transition == null || !transition.mParticipants.contains(displayArea)) {
            return;
        }
        transition.collectVisibleChange(displayArea);
        displayArea.forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.TransitionController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TransitionController.lambda$collectForDisplayAreaChange$2(Transition.this, (Task) obj);
            }
        }, true);
        DisplayContent asDisplayContent = displayArea.asDisplayContent();
        if (asDisplayContent != null) {
            final boolean z = asDisplayContent.getAsyncRotationController() == null;
            displayArea.forAllWindows(new Consumer() { // from class: com.android.server.wm.TransitionController$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TransitionController.this.lambda$collectForDisplayAreaChange$3(z, transition, (WindowState) obj);
                }
            }, true);
        }
    }

    public static /* synthetic */ void lambda$collectForDisplayAreaChange$2(Transition transition, Task task) {
        if (task.isVisible()) {
            transition.collect(task);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$collectForDisplayAreaChange$3(boolean z, Transition transition, WindowState windowState) {
        if (windowState.mActivityRecord == null && windowState.isVisible() && !isCollecting(windowState.mToken)) {
            if (z || !AsyncRotationController.canBeAsync(windowState.mToken)) {
                transition.collect(windowState.mToken);
            }
        }
    }

    public void collectVisibleChange(WindowContainer windowContainer) {
        if (isCollecting()) {
            this.mCollectingTransition.collectVisibleChange(windowContainer);
        }
    }

    public void collectReparentChange(WindowContainer windowContainer, WindowContainer windowContainer2) {
        if (isCollecting()) {
            this.mCollectingTransition.collectReparentChange(windowContainer, windowContainer2);
        }
    }

    public void setStatusBarTransitionDelay(long j) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.mStatusBarTransitionDelay = j;
    }

    public void setOverrideAnimation(TransitionInfo.AnimationOptions animationOptions, IRemoteCallback iRemoteCallback, IRemoteCallback iRemoteCallback2) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.setOverrideAnimation(animationOptions, iRemoteCallback, iRemoteCallback2);
    }

    public void setNoAnimation(WindowContainer windowContainer) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.setNoAnimation(windowContainer);
    }

    public void setReady(WindowContainer windowContainer, boolean z) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.setReady(windowContainer, z);
    }

    public void setReady(WindowContainer windowContainer) {
        setReady(windowContainer, true);
    }

    public void deferTransitionReady() {
        if (isShellTransitionsEnabled()) {
            Transition transition = this.mCollectingTransition;
            if (transition == null) {
                throw new IllegalStateException("No collecting transition to defer readiness for.");
            }
            transition.deferTransitionReady();
        }
    }

    public void continueTransitionReady() {
        if (isShellTransitionsEnabled()) {
            Transition transition = this.mCollectingTransition;
            if (transition == null) {
                throw new IllegalStateException("No collecting transition to defer readiness for.");
            }
            transition.continueTransitionReady();
        }
    }

    public void finishTransition(IBinder iBinder) {
        this.mTransitionMetricsReporter.reportAnimationStart(iBinder, 0L);
        this.mAtm.endLaunchPowerMode(2);
        Transition fromBinder = Transition.fromBinder(iBinder);
        if (fromBinder == null || !this.mPlayingTransitions.contains(fromBinder)) {
            Slog.e("TransitionController", "Trying to finish a non-playing transition " + iBinder);
            return;
        }
        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -622017164, 0, (String) null, new Object[]{String.valueOf(fromBinder)});
        }
        this.mPlayingTransitions.remove(fromBinder);
        updateRunningRemoteAnimation(fromBinder, false);
        fromBinder.finishTransition();
        for (int size = this.mAnimatingExitWindows.size() - 1; size >= 0; size--) {
            WindowState windowState = this.mAnimatingExitWindows.get(size);
            if (windowState.mAnimatingExit && windowState.mHasSurface && !windowState.inTransition()) {
                windowState.onExitAnimationDone();
            }
            if (!windowState.mAnimatingExit || !windowState.mHasSurface) {
                this.mAnimatingExitWindows.remove(size);
            }
        }
        this.mRunningLock.doNotifyLocked();
        if (inTransition()) {
            return;
        }
        validateStates();
    }

    public final void validateStates() {
        for (int i = 0; i < this.mStateValidators.size(); i++) {
            this.mStateValidators.get(i).run();
            if (inTransition()) {
                this.mStateValidators.subList(0, i + 1).clear();
                return;
            }
        }
        this.mStateValidators.clear();
    }

    public void moveToPlaying(Transition transition) {
        if (transition != this.mCollectingTransition) {
            throw new IllegalStateException("Trying to move non-collecting transition to playing");
        }
        this.mCollectingTransition = null;
        this.mPlayingTransitions.add(transition);
        updateRunningRemoteAnimation(transition, true);
        this.mTransitionTracer.logState(transition);
    }

    public void updateAnimatingState(SurfaceControl.Transaction transaction) {
        Transition transition;
        boolean z = !this.mPlayingTransitions.isEmpty() || ((transition = this.mCollectingTransition) != null && transition.isStarted());
        if (z && !this.mAnimatingState) {
            transaction.setEarlyWakeupStart();
            this.mAtm.mWindowManager.mSnapshotPersistQueue.setPaused(true);
            this.mAnimatingState = true;
            Trace.asyncTraceBegin(32L, "transitAnim", 0);
        } else if (z || !this.mAnimatingState) {
        } else {
            transaction.setEarlyWakeupEnd();
            this.mAtm.mWindowManager.mSnapshotPersistQueue.setPaused(false);
            this.mAnimatingState = false;
            Trace.asyncTraceEnd(32L, "transitAnim", 0);
        }
    }

    public final void updateRunningRemoteAnimation(Transition transition, boolean z) {
        WindowProcessController windowProcessController = this.mTransitionPlayerProc;
        if (windowProcessController == null) {
            return;
        }
        if (z) {
            windowProcessController.setRunningRemoteAnimation(true);
        } else if (this.mPlayingTransitions.isEmpty()) {
            this.mTransitionPlayerProc.setRunningRemoteAnimation(false);
            this.mRemotePlayer.clear();
            return;
        }
        RemoteTransition remoteTransition = transition.getRemoteTransition();
        if (remoteTransition == null) {
            return;
        }
        WindowProcessController processController = this.mAtm.getProcessController(remoteTransition.getAppThread());
        if (processController == null) {
            return;
        }
        this.mRemotePlayer.update(processController, z, true);
    }

    public void abort(Transition transition) {
        if (transition != this.mCollectingTransition) {
            throw new IllegalStateException("Too late to abort.");
        }
        transition.abort();
        this.mCollectingTransition = null;
        this.mTransitionTracer.logState(transition);
    }

    public void setTransientLaunch(ActivityRecord activityRecord, Task task) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.setTransientLaunch(activityRecord, task);
        if (activityRecord.isActivityTypeHomeOrRecents()) {
            this.mCollectingTransition.addFlag(128);
            activityRecord.getTask().setCanAffectSystemUiFlags(false);
        }
    }

    public void setCanPipOnFinish(boolean z) {
        Transition transition = this.mCollectingTransition;
        if (transition == null) {
            return;
        }
        transition.setCanPipOnFinish(z);
    }

    public void legacyDetachNavigationBarFromApp(IBinder iBinder) {
        Transition fromBinder = Transition.fromBinder(iBinder);
        if (fromBinder == null || !this.mPlayingTransitions.contains(fromBinder)) {
            Slog.e("TransitionController", "Transition isn't playing: " + iBinder);
            return;
        }
        fromBinder.legacyRestoreNavigationBarFromApp();
    }

    public void registerLegacyListener(WindowManagerInternal.AppTransitionListener appTransitionListener) {
        this.mLegacyListeners.add(appTransitionListener);
    }

    public void unregisterLegacyListener(WindowManagerInternal.AppTransitionListener appTransitionListener) {
        this.mLegacyListeners.remove(appTransitionListener);
    }

    public void dispatchLegacyAppTransitionPending() {
        for (int i = 0; i < this.mLegacyListeners.size(); i++) {
            this.mLegacyListeners.get(i).onAppTransitionPendingLocked();
        }
    }

    public void dispatchLegacyAppTransitionStarting(TransitionInfo transitionInfo, long j) {
        for (int i = 0; i < this.mLegacyListeners.size(); i++) {
            this.mLegacyListeners.get(i).onAppTransitionStartingLocked(SystemClock.uptimeMillis() + j, 120L);
        }
    }

    public void dispatchLegacyAppTransitionFinished(ActivityRecord activityRecord) {
        for (int i = 0; i < this.mLegacyListeners.size(); i++) {
            this.mLegacyListeners.get(i).onAppTransitionFinishedLocked(activityRecord.token);
        }
    }

    public void dispatchLegacyAppTransitionCancelled() {
        for (int i = 0; i < this.mLegacyListeners.size(); i++) {
            this.mLegacyListeners.get(i).onAppTransitionCancelledLocked(false);
        }
    }

    public void dumpDebugLegacy(ProtoOutputStream protoOutputStream, long j) {
        int i;
        long start = protoOutputStream.start(j);
        if (this.mPlayingTransitions.isEmpty()) {
            Transition transition = this.mCollectingTransition;
            i = ((transition == null || !transition.getLegacyIsReady()) && !this.mAtm.mWindowManager.mSyncEngine.hasPendingSyncSets()) ? 0 : 1;
        } else {
            i = 2;
        }
        protoOutputStream.write(1159641169921L, i);
        protoOutputStream.end(start);
    }

    /* renamed from: com.android.server.wm.TransitionController$RemotePlayer */
    /* loaded from: classes2.dex */
    public static class RemotePlayer {
        public final ActivityTaskManagerService mAtm;
        @GuardedBy({"itself"})
        public final ArrayMap<IBinder, DelegateProcess> mDelegateProcesses = new ArrayMap<>();

        /* renamed from: com.android.server.wm.TransitionController$RemotePlayer$DelegateProcess */
        /* loaded from: classes2.dex */
        public class DelegateProcess implements Runnable {
            public boolean mNeedReport;
            public final WindowProcessController mProc;

            public DelegateProcess(WindowProcessController windowProcessController) {
                this.mProc = windowProcessController;
            }

            @Override // java.lang.Runnable
            public void run() {
                synchronized (RemotePlayer.this.mAtm.mGlobalLockWithoutBoost) {
                    RemotePlayer.this.update(this.mProc, false, false);
                }
            }
        }

        public RemotePlayer(ActivityTaskManagerService activityTaskManagerService) {
            this.mAtm = activityTaskManagerService;
        }

        public void update(WindowProcessController windowProcessController, boolean z, boolean z2) {
            boolean z3 = true;
            if (!z) {
                synchronized (this.mDelegateProcesses) {
                    int size = this.mDelegateProcesses.size() - 1;
                    while (true) {
                        if (size < 0) {
                            z3 = false;
                            break;
                        } else if (this.mDelegateProcesses.valueAt(size).mProc == windowProcessController) {
                            this.mDelegateProcesses.removeAt(size);
                            break;
                        } else {
                            size--;
                        }
                    }
                    if (z3) {
                        windowProcessController.setRunningRemoteAnimation(false);
                    }
                }
            } else if (windowProcessController.isRunningRemoteTransition() || !windowProcessController.hasThread()) {
            } else {
                windowProcessController.setRunningRemoteAnimation(true);
                DelegateProcess delegateProcess = new DelegateProcess(windowProcessController);
                if (z2) {
                    delegateProcess.mNeedReport = true;
                    this.mAtm.f1161mH.postDelayed(delegateProcess, 100L);
                }
                synchronized (this.mDelegateProcesses) {
                    this.mDelegateProcesses.put(windowProcessController.getThread().asBinder(), delegateProcess);
                }
            }
        }

        public void clear() {
            synchronized (this.mDelegateProcesses) {
                for (int size = this.mDelegateProcesses.size() - 1; size >= 0; size--) {
                    this.mDelegateProcesses.valueAt(size).mProc.setRunningRemoteAnimation(false);
                }
                this.mDelegateProcesses.clear();
            }
        }

        public boolean reportRunning(IApplicationThread iApplicationThread) {
            DelegateProcess delegateProcess;
            synchronized (this.mDelegateProcesses) {
                delegateProcess = this.mDelegateProcesses.get(iApplicationThread.asBinder());
                if (delegateProcess != null && delegateProcess.mNeedReport) {
                    delegateProcess.mNeedReport = false;
                    this.mAtm.f1161mH.removeCallbacks(delegateProcess);
                }
            }
            return delegateProcess != null;
        }
    }

    /* renamed from: com.android.server.wm.TransitionController$Logger */
    /* loaded from: classes2.dex */
    public static class Logger {
        public long mCollectTimeNs;
        public long mCreateTimeNs;
        public long mCreateWallTimeMs;
        public long mFinishTimeNs;
        public TransitionInfo mInfo;
        public long mReadyTimeNs;
        public TransitionRequestInfo mRequest;
        public long mRequestTimeNs;
        public long mSendTimeNs;
        public long mStartTimeNs;
        public WindowContainerTransaction mStartWCT;
        public int mSyncId;

        public final String buildOnSendLog() {
            StringBuilder sb = new StringBuilder("Sent Transition #");
            sb.append(this.mSyncId);
            sb.append(" createdAt=");
            sb.append(TimeUtils.logTimeOfDay(this.mCreateWallTimeMs));
            if (this.mRequest != null) {
                sb.append(" via request=");
                sb.append(this.mRequest);
            }
            return sb.toString();
        }

        public void logOnSend() {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS_MIN, 2021079047, 0, "%s", new Object[]{String.valueOf(buildOnSendLog())});
            }
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS_MIN, 1621562070, 0, "    startWCT=%s", new Object[]{String.valueOf(this.mStartWCT)});
            }
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS_MIN, 273212558, 0, "    info=%s", new Object[]{String.valueOf(this.mInfo)});
            }
        }

        public static String toMsString(long j) {
            return (Math.round(j / 1000.0d) / 1000.0d) + "ms";
        }

        public final String buildOnFinishLog() {
            StringBuilder sb = new StringBuilder("Finish Transition #");
            sb.append(this.mSyncId);
            sb.append(": created at ");
            sb.append(TimeUtils.logTimeOfDay(this.mCreateWallTimeMs));
            sb.append(" collect-started=");
            sb.append(toMsString(this.mCollectTimeNs - this.mCreateTimeNs));
            if (this.mRequestTimeNs != 0) {
                sb.append(" request-sent=");
                sb.append(toMsString(this.mRequestTimeNs - this.mCreateTimeNs));
            }
            sb.append(" started=");
            sb.append(toMsString(this.mStartTimeNs - this.mCreateTimeNs));
            sb.append(" ready=");
            sb.append(toMsString(this.mReadyTimeNs - this.mCreateTimeNs));
            sb.append(" sent=");
            sb.append(toMsString(this.mSendTimeNs - this.mCreateTimeNs));
            sb.append(" finished=");
            sb.append(toMsString(this.mFinishTimeNs - this.mCreateTimeNs));
            return sb.toString();
        }

        public void logOnFinish() {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS_MIN, 2021079047, 0, "%s", new Object[]{String.valueOf(buildOnFinishLog())});
            }
        }
    }

    /* renamed from: com.android.server.wm.TransitionController$TransitionMetricsReporter */
    /* loaded from: classes2.dex */
    public static class TransitionMetricsReporter extends ITransitionMetricsReporter.Stub {
        public final ArrayMap<IBinder, LongConsumer> mMetricConsumers = new ArrayMap<>();

        public void associate(IBinder iBinder, LongConsumer longConsumer) {
            synchronized (this.mMetricConsumers) {
                this.mMetricConsumers.put(iBinder, longConsumer);
            }
        }

        public void reportAnimationStart(IBinder iBinder, long j) {
            synchronized (this.mMetricConsumers) {
                if (this.mMetricConsumers.isEmpty()) {
                    return;
                }
                LongConsumer remove = this.mMetricConsumers.remove(iBinder);
                if (remove != null) {
                    remove.accept(j);
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.TransitionController$Lock */
    /* loaded from: classes2.dex */
    public class Lock {
        public int mTransitionWaiters = 0;

        public Lock() {
        }

        public void runWhenIdle(long j, Runnable runnable) {
            synchronized (TransitionController.this.mAtm.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!TransitionController.this.inTransition()) {
                        runnable.run();
                        return;
                    }
                    this.mTransitionWaiters++;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    long uptimeMillis = SystemClock.uptimeMillis() + j;
                    while (true) {
                        synchronized (TransitionController.this.mAtm.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                if (!TransitionController.this.inTransition() || SystemClock.uptimeMillis() > uptimeMillis) {
                                    break;
                                }
                            } finally {
                                WindowManagerService.resetPriorityAfterLockedSection();
                            }
                        }
                        synchronized (this) {
                            try {
                                try {
                                    wait(j);
                                } catch (InterruptedException unused) {
                                    return;
                                }
                            } finally {
                            }
                        }
                    }
                    this.mTransitionWaiters--;
                    runnable.run();
                    WindowManagerService.resetPriorityAfterLockedSection();
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        public void doNotifyLocked() {
            synchronized (this) {
                if (this.mTransitionWaiters > 0) {
                    notifyAll();
                }
            }
        }
    }
}
