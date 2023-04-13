package com.android.server.p014wm;

import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.proto.ProtoOutputStream;
import android.view.IRecentsAnimationController;
import android.view.IRecentsAnimationRunner;
import android.view.InputWindowHandle;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowInsets;
import android.window.PictureInPictureSurfaceTransaction;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p014wm.DisplayArea;
import com.android.server.p014wm.RecentsAnimationController;
import com.android.server.p014wm.SurfaceAnimator;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.p014wm.utils.InsetUtils;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* renamed from: com.android.server.wm.RecentsAnimationController */
/* loaded from: classes2.dex */
public class RecentsAnimationController implements IBinder.DeathRecipient {
    public static final String TAG = RecentsAnimationController.class.getSimpleName();
    public final RecentsAnimationCallbacks mCallbacks;
    public boolean mCancelDeferredWithScreenshot;
    public boolean mCancelOnNextTransitionStart;
    public boolean mCanceled;
    public DisplayContent mDisplayContent;
    public final int mDisplayId;
    public boolean mInputConsumerEnabled;
    @VisibleForTesting
    boolean mIsAddingTaskToTargets;
    public boolean mLinkedToDeathOfRunner;
    public ActivityRecord mNavBarAttachedApp;
    public boolean mNavigationBarAttachedToApp;
    public boolean mRequestDeferCancelUntilNextTransition;
    public IRecentsAnimationRunner mRunner;
    public final WindowManagerService mService;
    @VisibleForTesting
    boolean mShouldAttachNavBarToAppDuringTransition;
    public ActivityRecord mTargetActivityRecord;
    public int mTargetActivityType;
    public final ArrayList<TaskAnimationAdapter> mPendingAnimations = new ArrayList<>();
    public final IntArray mPendingNewTaskTargets = new IntArray(0);
    public final ArrayList<WallpaperAnimationAdapter> mPendingWallpaperAnimations = new ArrayList<>();
    public boolean mWillFinishToHome = false;
    public final Runnable mFailsafeRunnable = new Runnable() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            RecentsAnimationController.this.onFailsafe();
        }
    };
    public boolean mPendingStart = true;
    public final Rect mTmpRect = new Rect();
    public int mPendingCancelWithScreenshotReorderMode = 2;
    public final ArrayList<RemoteAnimationTarget> mPendingTaskAppears = new ArrayList<>();
    public final WindowManagerInternal.AppTransitionListener mAppTransitionListener = new WindowManagerInternal.AppTransitionListener() { // from class: com.android.server.wm.RecentsAnimationController.1
        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public int onAppTransitionStartingLocked(long j, long j2) {
            continueDeferredCancel();
            return 0;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public void onAppTransitionCancelledLocked(boolean z) {
            continueDeferredCancel();
        }

        public final void continueDeferredCancel() {
            RecentsAnimationController.this.mDisplayContent.mAppTransition.unregisterListener(this);
            if (!RecentsAnimationController.this.mCanceled && RecentsAnimationController.this.mCancelOnNextTransitionStart) {
                RecentsAnimationController.this.mCancelOnNextTransitionStart = false;
                RecentsAnimationController recentsAnimationController = RecentsAnimationController.this;
                recentsAnimationController.cancelAnimationWithScreenshot(recentsAnimationController.mCancelDeferredWithScreenshot);
            }
        }
    };
    public final IRecentsAnimationController mController = new IRecentsAnimationController.Stub() { // from class: com.android.server.wm.RecentsAnimationController.2
        public TaskSnapshot screenshotTask(int i) {
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 184362060, 13, (String) null, new Object[]{Long.valueOf(i), Boolean.valueOf(RecentsAnimationController.this.mCanceled)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    if (RecentsAnimationController.this.mCanceled) {
                        return null;
                    }
                    for (int size = RecentsAnimationController.this.mPendingAnimations.size() - 1; size >= 0; size--) {
                        Task task = ((TaskAnimationAdapter) RecentsAnimationController.this.mPendingAnimations.get(size)).mTask;
                        if (task.mTaskId == i) {
                            TaskSnapshotController taskSnapshotController = RecentsAnimationController.this.mService.mTaskSnapshotController;
                            ArraySet<Task> newArraySet = Sets.newArraySet(new Task[]{task});
                            taskSnapshotController.snapshotTasks(newArraySet);
                            taskSnapshotController.addSkipClosingAppSnapshotTasks(newArraySet);
                            return taskSnapshotController.getSnapshot(i, task.mUserId, false, false);
                        }
                    }
                    return null;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setFinishTaskTransaction(int i, PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -163974242, 1, (String) null, new Object[]{Long.valueOf(i), String.valueOf(pictureInPictureSurfaceTransaction)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    int size = RecentsAnimationController.this.mPendingAnimations.size() - 1;
                    while (true) {
                        if (size < 0) {
                            break;
                        }
                        TaskAnimationAdapter taskAnimationAdapter = (TaskAnimationAdapter) RecentsAnimationController.this.mPendingAnimations.get(size);
                        if (taskAnimationAdapter.mTask.mTaskId == i) {
                            taskAnimationAdapter.mFinishTransaction = pictureInPictureSurfaceTransaction;
                            taskAnimationAdapter.mFinishOverlay = surfaceControl;
                            break;
                        }
                        size--;
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void finish(boolean z, boolean z2) {
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -445944810, 15, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(RecentsAnimationController.this.mCanceled)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                RecentsAnimationController.this.mCallbacks.onAnimationFinished(z ? 1 : 2, z2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setAnimationTargetsBehindSystemBars(boolean z) throws RemoteException {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    for (int size = RecentsAnimationController.this.mPendingAnimations.size() - 1; size >= 0; size--) {
                        Task task = ((TaskAnimationAdapter) RecentsAnimationController.this.mPendingAnimations.get(size)).mTask;
                        if (task.getActivityType() != RecentsAnimationController.this.mTargetActivityType) {
                            task.setCanAffectSystemUiFlags(z);
                        }
                    }
                    InputMethodManagerInternal.get().maybeFinishStylusHandwriting();
                    RecentsAnimationController.this.mService.mWindowPlacerLocked.requestTraversal();
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setInputConsumerEnabled(boolean z) {
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -1219773477, 12, (String) null, new Object[]{String.valueOf(z), Boolean.valueOf(RecentsAnimationController.this.mCanceled)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    if (RecentsAnimationController.this.mCanceled) {
                        return;
                    }
                    RecentsAnimationController.this.mInputConsumerEnabled = z;
                    RecentsAnimationController.this.mDisplayContent.getInputMonitor().updateInputWindowsLw(true);
                    RecentsAnimationController.this.mService.scheduleAnimationLocked();
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
            synchronized (RecentsAnimationController.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RecentsAnimationController.this.setDeferredCancel(z, z2);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void cleanupScreenshot() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                RecentsAnimationController.this.continueDeferredCancelAnimation();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setWillFinishToHome(boolean z) {
            synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                RecentsAnimationController.this.setWillFinishToHome(z);
            }
        }

        public boolean removeTask(int i) {
            boolean removeTaskInternal;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    removeTaskInternal = RecentsAnimationController.this.removeTaskInternal(i);
                }
                return removeTaskInternal;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void detachNavigationBarFromApp(boolean z) {
            boolean z2;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    RecentsAnimationController recentsAnimationController = RecentsAnimationController.this;
                    if (!z && !recentsAnimationController.mIsAddingTaskToTargets) {
                        z2 = false;
                        recentsAnimationController.restoreNavigationBarFromApp(z2);
                        RecentsAnimationController.this.mService.mWindowPlacerLocked.requestTraversal();
                    }
                    z2 = true;
                    recentsAnimationController.restoreNavigationBarFromApp(z2);
                    RecentsAnimationController.this.mService.mWindowPlacerLocked.requestTraversal();
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void animateNavigationBarToApp(long j) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (RecentsAnimationController.this.mService.getWindowManagerLock()) {
                    RecentsAnimationController.this.animateNavigationBarForAppLaunch(j);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    };
    @VisibleForTesting
    final StatusBarManagerInternal mStatusBar = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);

    /* renamed from: com.android.server.wm.RecentsAnimationController$RecentsAnimationCallbacks */
    /* loaded from: classes2.dex */
    public interface RecentsAnimationCallbacks {
        void onAnimationFinished(int i, boolean z);
    }

    public RecentsAnimationController(WindowManagerService windowManagerService, IRecentsAnimationRunner iRecentsAnimationRunner, RecentsAnimationCallbacks recentsAnimationCallbacks, int i) {
        this.mService = windowManagerService;
        this.mRunner = iRecentsAnimationRunner;
        this.mCallbacks = recentsAnimationCallbacks;
        this.mDisplayId = i;
        DisplayContent displayContent = windowManagerService.mRoot.getDisplayContent(i);
        this.mDisplayContent = displayContent;
        this.mShouldAttachNavBarToAppDuringTransition = displayContent.getDisplayPolicy().shouldAttachNavBarToAppDuringTransition();
    }

    public void initialize(int i, SparseBooleanArray sparseBooleanArray, ActivityRecord activityRecord) {
        this.mTargetActivityType = i;
        this.mDisplayContent.mAppTransition.registerListenerLocked(this.mAppTransitionListener);
        final ArrayList<Task> visibleTasks = this.mDisplayContent.getDefaultTaskDisplayArea().getVisibleTasks();
        Task rootTask = this.mDisplayContent.getDefaultTaskDisplayArea().getRootTask(0, i);
        if (rootTask != null) {
            rootTask.forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RecentsAnimationController.lambda$initialize$0(visibleTasks, (Task) obj);
                }
            }, true);
        }
        for (int size = visibleTasks.size() - 1; size >= 0; size--) {
            final Task task = visibleTasks.get(size);
            if (!skipAnimation(task)) {
                addAnimation(task, !sparseBooleanArray.get(task.mTaskId), false, new SurfaceAnimator.OnAnimationFinishedCallback() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda2
                    @Override // com.android.server.p014wm.SurfaceAnimator.OnAnimationFinishedCallback
                    public final void onAnimationFinished(int i2, AnimationAdapter animationAdapter) {
                        RecentsAnimationController.lambda$initialize$2(Task.this, i2, animationAdapter);
                    }
                });
            }
        }
        if (this.mPendingAnimations.isEmpty()) {
            cancelAnimation(2, "initialize-noVisibleTasks");
            return;
        }
        try {
            linkToDeathOfRunner();
            attachNavigationBarToApp();
            if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 1519757176, 0, (String) null, new Object[]{String.valueOf(activityRecord.getName())});
            }
            this.mTargetActivityRecord = activityRecord;
            if (activityRecord.windowsCanBeWallpaperTarget()) {
                DisplayContent displayContent = this.mDisplayContent;
                displayContent.pendingLayoutChanges |= 4;
                displayContent.setLayoutNeeded();
            }
            this.mService.mWindowPlacerLocked.performSurfacePlacement();
            this.mDisplayContent.mFixedRotationTransitionListener.onStartRecentsAnimation(activityRecord);
            StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.onRecentsAnimationStateChanged(true);
            }
        } catch (RemoteException unused) {
            cancelAnimation(2, "initialize-failedToLinkToDeath");
        }
    }

    public static /* synthetic */ void lambda$initialize$0(ArrayList arrayList, Task task) {
        if (arrayList.contains(task)) {
            return;
        }
        arrayList.add(task);
    }

    public static /* synthetic */ void lambda$initialize$2(Task task, final int i, final AnimationAdapter animationAdapter) {
        task.forAllWindows(new Consumer() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowState) obj).onAnimationFinished(i, animationAdapter);
            }
        }, true);
    }

    public boolean isInterestingForAllDrawn(WindowState windowState) {
        return (isTargetApp(windowState.getActivityRecord()) && windowState.getWindowType() != 1 && windowState.getAttrs().alpha == 0.0f) ? false : true;
    }

    public final boolean skipAnimation(Task task) {
        return task.isAlwaysOnTop() || task.getWindowConfiguration().tasksAreFloating();
    }

    @VisibleForTesting
    public TaskAnimationAdapter addAnimation(Task task, boolean z) {
        return addAnimation(task, z, false, null);
    }

    @VisibleForTesting
    public TaskAnimationAdapter addAnimation(Task task, boolean z, boolean z2, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 302992539, 0, (String) null, new Object[]{String.valueOf(task.getName())});
        }
        TaskAnimationAdapter taskAnimationAdapter = new TaskAnimationAdapter(task, z);
        task.startAnimation(task.getPendingTransaction(), taskAnimationAdapter, z2, 8, onAnimationFinishedCallback);
        task.commitPendingTransaction();
        this.mPendingAnimations.add(taskAnimationAdapter);
        return taskAnimationAdapter;
    }

    @VisibleForTesting
    public void removeAnimation(TaskAnimationAdapter taskAnimationAdapter) {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 83950285, 1, (String) null, new Object[]{Long.valueOf(taskAnimationAdapter.mTask.mTaskId)});
        }
        taskAnimationAdapter.onRemove();
        this.mPendingAnimations.remove(taskAnimationAdapter);
    }

    @VisibleForTesting
    public void removeWallpaperAnimation(WallpaperAnimationAdapter wallpaperAnimationAdapter) {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -1768557332, 0, (String) null, (Object[]) null);
        }
        wallpaperAnimationAdapter.getLeashFinishedCallback().onAnimationFinished(wallpaperAnimationAdapter.getLastAnimationType(), wallpaperAnimationAdapter);
        this.mPendingWallpaperAnimations.remove(wallpaperAnimationAdapter);
    }

    public void startAnimation() {
        RemoteAnimationTarget[] createAppAnimations;
        Rect rect;
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 17696244, 15, (String) null, new Object[]{Boolean.valueOf(this.mPendingStart), Boolean.valueOf(this.mCanceled)});
        }
        if (!this.mPendingStart || this.mCanceled) {
            return;
        }
        try {
            createAppAnimations = createAppAnimations();
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to start recents animation", e);
        }
        if (createAppAnimations.length == 0) {
            cancelAnimation(2, "startAnimation-noAppWindows");
            return;
        }
        RemoteAnimationTarget[] createWallpaperAnimations = createWallpaperAnimations();
        this.mPendingStart = false;
        WindowState targetAppMainWindow = getTargetAppMainWindow();
        if (targetAppMainWindow != null) {
            rect = targetAppMainWindow.getInsetsStateWithVisibilityOverride().calculateInsets(this.mTargetActivityRecord.getBounds(), WindowInsets.Type.systemBars(), false).toRect();
        } else {
            this.mService.getStableInsets(this.mDisplayId, this.mTmpRect);
            rect = this.mTmpRect;
        }
        this.mRunner.onAnimationStart(this.mController, createAppAnimations, createWallpaperAnimations, rect, (Rect) null);
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -1207757583, 0, (String) null, new Object[]{String.valueOf(this.mPendingAnimations.stream().map(new Function() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$startAnimation$3;
                    lambda$startAnimation$3 = RecentsAnimationController.lambda$startAnimation$3((RecentsAnimationController.TaskAnimationAdapter) obj);
                    return lambda$startAnimation$3;
                }
            }).collect(Collectors.toList()))});
        }
        if (this.mTargetActivityRecord != null) {
            ArrayMap<WindowContainer, Integer> arrayMap = new ArrayMap<>(1);
            arrayMap.put(this.mTargetActivityRecord, 5);
            this.mService.mAtmService.mTaskSupervisor.getActivityMetricsLogger().notifyTransitionStarting(arrayMap);
        }
    }

    public static /* synthetic */ Integer lambda$startAnimation$3(TaskAnimationAdapter taskAnimationAdapter) {
        return Integer.valueOf(taskAnimationAdapter.mTask.mTaskId);
    }

    public boolean isNavigationBarAttachedToApp() {
        return this.mNavigationBarAttachedToApp;
    }

    @VisibleForTesting
    public WindowState getNavigationBarWindow() {
        return this.mDisplayContent.getDisplayPolicy().getNavigationBar();
    }

    public final void attachNavigationBarToApp() {
        WindowToken windowToken;
        if (this.mShouldAttachNavBarToAppDuringTransition && this.mDisplayContent.getAsyncRotationController() == null) {
            int size = this.mPendingAnimations.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                Task task = this.mPendingAnimations.get(size).mTask;
                if (!task.isActivityTypeHomeOrRecents()) {
                    this.mNavBarAttachedApp = task.getTopVisibleActivity();
                    break;
                }
                size--;
            }
            WindowState navigationBarWindow = getNavigationBarWindow();
            if (this.mNavBarAttachedApp == null || navigationBarWindow == null || (windowToken = navigationBarWindow.mToken) == null) {
                return;
            }
            this.mNavigationBarAttachedToApp = true;
            windowToken.cancelAnimation();
            SurfaceControl.Transaction pendingTransaction = navigationBarWindow.mToken.getPendingTransaction();
            SurfaceControl surfaceControl = navigationBarWindow.mToken.getSurfaceControl();
            navigationBarWindow.setSurfaceTranslationY(-this.mNavBarAttachedApp.getBounds().top);
            pendingTransaction.reparent(surfaceControl, this.mNavBarAttachedApp.getSurfaceControl());
            pendingTransaction.show(surfaceControl);
            DisplayArea.Tokens imeContainer = this.mDisplayContent.getImeContainer();
            if (imeContainer.isVisible()) {
                pendingTransaction.setRelativeLayer(surfaceControl, imeContainer.getSurfaceControl(), 1);
            } else {
                pendingTransaction.setLayer(surfaceControl, Integer.MAX_VALUE);
            }
            StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.setNavigationBarLumaSamplingEnabled(this.mDisplayId, false);
            }
        }
    }

    @VisibleForTesting
    public void restoreNavigationBarFromApp(boolean z) {
        if (this.mNavigationBarAttachedToApp) {
            this.mNavigationBarAttachedToApp = false;
            StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.setNavigationBarLumaSamplingEnabled(this.mDisplayId, true);
            }
            WindowState navigationBarWindow = getNavigationBarWindow();
            if (navigationBarWindow == null) {
                return;
            }
            navigationBarWindow.setSurfaceTranslationY(0);
            WindowToken windowToken = navigationBarWindow.mToken;
            if (windowToken == null) {
                return;
            }
            SurfaceControl.Transaction pendingTransaction = this.mDisplayContent.getPendingTransaction();
            WindowContainer parent = windowToken.getParent();
            pendingTransaction.setLayer(windowToken.getSurfaceControl(), windowToken.getLastLayer());
            if (z) {
                new NavBarFadeAnimationController(this.mDisplayContent).fadeWindowToken(true);
            } else {
                pendingTransaction.reparent(windowToken.getSurfaceControl(), parent.getSurfaceControl());
            }
        }
    }

    public void animateNavigationBarForAppLaunch(long j) {
        if (!this.mShouldAttachNavBarToAppDuringTransition || this.mDisplayContent.getAsyncRotationController() != null || this.mNavigationBarAttachedToApp || this.mNavBarAttachedApp == null) {
            return;
        }
        new NavBarFadeAnimationController(this.mDisplayContent).fadeOutAndInSequentially(j, null, this.mNavBarAttachedApp.getSurfaceControl());
    }

    public void addTaskToTargets(Task task, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
        if (this.mRunner != null) {
            this.mIsAddingTaskToTargets = task != null;
            this.mNavBarAttachedApp = task == null ? null : task.getTopVisibleActivity();
            if (isAnimatingTask(task) || skipAnimation(task)) {
                return;
            }
            collectTaskRemoteAnimations(task, 0, onAnimationFinishedCallback);
        }
    }

    public void sendTasksAppeared() {
        if (this.mPendingTaskAppears.isEmpty() || this.mRunner == null) {
            return;
        }
        try {
            this.mRunner.onTasksAppeared((RemoteAnimationTarget[]) this.mPendingTaskAppears.toArray(new RemoteAnimationTarget[0]));
            this.mPendingTaskAppears.clear();
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to report task appeared", e);
        }
    }

    public final void collectTaskRemoteAnimations(Task task, final int i, final SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
        final SparseBooleanArray recentTaskIds = this.mService.mAtmService.getRecentTasks().getRecentTaskIds();
        task.forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RecentsAnimationController.this.lambda$collectTaskRemoteAnimations$4(recentTaskIds, onAnimationFinishedCallback, i, (Task) obj);
            }
        }, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$collectTaskRemoteAnimations$4(SparseBooleanArray sparseBooleanArray, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback, int i, Task task) {
        if (task.shouldBeVisible(null)) {
            int i2 = task.mTaskId;
            TaskAnimationAdapter addAnimation = addAnimation(task, !sparseBooleanArray.get(i2), true, onAnimationFinishedCallback);
            this.mPendingNewTaskTargets.add(i2);
            RemoteAnimationTarget createRemoteAnimationTarget = addAnimation.createRemoteAnimationTarget(i2, i);
            if (createRemoteAnimationTarget != null) {
                this.mPendingTaskAppears.add(createRemoteAnimationTarget);
                if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 1151072840, 0, (String) null, new Object[]{String.valueOf(createRemoteAnimationTarget)});
                }
            }
        }
    }

    public final boolean removeTaskInternal(int i) {
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            TaskAnimationAdapter taskAnimationAdapter = this.mPendingAnimations.get(size);
            if (taskAnimationAdapter.mTask.mTaskId == i && taskAnimationAdapter.mTask.isOnTop()) {
                removeAnimation(taskAnimationAdapter);
                int indexOf = this.mPendingNewTaskTargets.indexOf(i);
                if (indexOf != -1) {
                    this.mPendingNewTaskTargets.remove(indexOf);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    public final RemoteAnimationTarget[] createAppAnimations() {
        ArrayList arrayList = new ArrayList();
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            TaskAnimationAdapter taskAnimationAdapter = this.mPendingAnimations.get(size);
            RemoteAnimationTarget createRemoteAnimationTarget = taskAnimationAdapter.createRemoteAnimationTarget(-1, -1);
            if (createRemoteAnimationTarget != null) {
                arrayList.add(createRemoteAnimationTarget);
            } else {
                removeAnimation(taskAnimationAdapter);
            }
        }
        return (RemoteAnimationTarget[]) arrayList.toArray(new RemoteAnimationTarget[arrayList.size()]);
    }

    public final RemoteAnimationTarget[] createWallpaperAnimations() {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 1495525537, 0, (String) null, (Object[]) null);
        }
        return WallpaperAnimationAdapter.startWallpaperAnimations(this.mDisplayContent, 0L, 0L, new Consumer() { // from class: com.android.server.wm.RecentsAnimationController$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RecentsAnimationController.this.lambda$createWallpaperAnimations$5((WallpaperAnimationAdapter) obj);
            }
        }, this.mPendingWallpaperAnimations);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createWallpaperAnimations$5(WallpaperAnimationAdapter wallpaperAnimationAdapter) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mPendingWallpaperAnimations.remove(wallpaperAnimationAdapter);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void forceCancelAnimation(int i, String str) {
        if (!this.mCanceled) {
            cancelAnimation(i, str);
        } else {
            continueDeferredCancelAnimation();
        }
    }

    public void cancelAnimation(int i, String str) {
        cancelAnimation(i, false, str);
    }

    public void cancelAnimationWithScreenshot(boolean z) {
        cancelAnimation(0, z, "rootTaskOrderChanged");
    }

    public void cancelAnimationForHomeStart() {
        cancelAnimation((this.mTargetActivityType == 2 && this.mWillFinishToHome) ? 1 : 0, true, "cancelAnimationForHomeStart");
    }

    public void cancelAnimationForDisplayChange() {
        cancelAnimation(this.mWillFinishToHome ? 1 : 2, true, "cancelAnimationForDisplayChange");
    }

    public final void cancelAnimation(int i, boolean z, String str) {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, 1525976603, 0, (String) null, new Object[]{String.valueOf(str)});
        }
        synchronized (this.mService.getWindowManagerLock()) {
            if (this.mCanceled) {
                return;
            }
            this.mService.f1164mH.removeCallbacks(this.mFailsafeRunnable);
            this.mCanceled = true;
            if (z && !this.mPendingAnimations.isEmpty()) {
                ArrayMap<Task, TaskSnapshot> screenshotRecentTasks = screenshotRecentTasks();
                this.mPendingCancelWithScreenshotReorderMode = i;
                if (!screenshotRecentTasks.isEmpty()) {
                    try {
                        int[] iArr = new int[screenshotRecentTasks.size()];
                        TaskSnapshot[] taskSnapshotArr = new TaskSnapshot[screenshotRecentTasks.size()];
                        for (int size = screenshotRecentTasks.size() - 1; size >= 0; size--) {
                            iArr[size] = screenshotRecentTasks.keyAt(size).mTaskId;
                            taskSnapshotArr[size] = screenshotRecentTasks.valueAt(size);
                        }
                        this.mRunner.onAnimationCanceled(iArr, taskSnapshotArr);
                    } catch (RemoteException e) {
                        Slog.e(TAG, "Failed to cancel recents animation", e);
                    }
                    scheduleFailsafe();
                    return;
                }
            }
            try {
                this.mRunner.onAnimationCanceled((int[]) null, (TaskSnapshot[]) null);
            } catch (RemoteException e2) {
                Slog.e(TAG, "Failed to cancel recents animation", e2);
            }
            this.mCallbacks.onAnimationFinished(i, false);
        }
    }

    @VisibleForTesting
    public void continueDeferredCancelAnimation() {
        this.mCallbacks.onAnimationFinished(this.mPendingCancelWithScreenshotReorderMode, false);
    }

    @VisibleForTesting
    public void setWillFinishToHome(boolean z) {
        this.mWillFinishToHome = z;
    }

    public void setCancelOnNextTransitionStart() {
        this.mCancelOnNextTransitionStart = true;
    }

    public void setDeferredCancel(boolean z, boolean z2) {
        this.mRequestDeferCancelUntilNextTransition = z;
        this.mCancelDeferredWithScreenshot = z2;
    }

    public boolean shouldDeferCancelUntilNextTransition() {
        return this.mRequestDeferCancelUntilNextTransition;
    }

    public boolean shouldDeferCancelWithScreenshot() {
        return this.mRequestDeferCancelUntilNextTransition && this.mCancelDeferredWithScreenshot;
    }

    public final ArrayMap<Task, TaskSnapshot> screenshotRecentTasks() {
        TaskSnapshotController taskSnapshotController = this.mService.mTaskSnapshotController;
        ArrayMap<Task, TaskSnapshot> arrayMap = new ArrayMap<>();
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            TaskAnimationAdapter taskAnimationAdapter = this.mPendingAnimations.get(size);
            Task task = taskAnimationAdapter.mTask;
            taskSnapshotController.recordSnapshot(task, false);
            TaskSnapshot snapshot = taskSnapshotController.getSnapshot(task.mTaskId, task.mUserId, false, false);
            if (snapshot != null) {
                arrayMap.put(task, snapshot);
                taskAnimationAdapter.setSnapshotOverlay(snapshot);
            }
        }
        taskSnapshotController.addSkipClosingAppSnapshotTasks(arrayMap.keySet());
        return arrayMap;
    }

    public void cleanupAnimation(int i) {
        if (ProtoLogCache.WM_DEBUG_RECENTS_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS, -1434147454, 5, (String) null, new Object[]{Long.valueOf(this.mPendingAnimations.size()), Long.valueOf(i)});
        }
        if (i != 2 && this.mTargetActivityRecord != this.mDisplayContent.topRunningActivity()) {
            this.mDisplayContent.mFixedRotationTransitionListener.notifyRecentsWillBeTop();
        }
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            TaskAnimationAdapter taskAnimationAdapter = this.mPendingAnimations.get(size);
            if (i == 1 || i == 0) {
                taskAnimationAdapter.mTask.dontAnimateDimExit();
            }
            removeAnimation(taskAnimationAdapter);
            taskAnimationAdapter.onCleanup();
        }
        this.mPendingNewTaskTargets.clear();
        this.mPendingTaskAppears.clear();
        for (int size2 = this.mPendingWallpaperAnimations.size() - 1; size2 >= 0; size2--) {
            removeWallpaperAnimation(this.mPendingWallpaperAnimations.get(size2));
        }
        restoreNavigationBarFromApp(i == 1 || this.mIsAddingTaskToTargets);
        this.mService.f1164mH.removeCallbacks(this.mFailsafeRunnable);
        this.mDisplayContent.mAppTransition.unregisterListener(this.mAppTransitionListener);
        unlinkToDeathOfRunner();
        this.mRunner = null;
        this.mCanceled = true;
        if (i == 2 && !this.mIsAddingTaskToTargets) {
            InputMethodManagerInternal.get().updateImeWindowStatus(false);
        }
        this.mDisplayContent.getInputMonitor().updateInputWindowsLw(true);
        ActivityRecord activityRecord = this.mTargetActivityRecord;
        if (activityRecord != null && (i == 1 || i == 0)) {
            this.mDisplayContent.mAppTransition.notifyAppTransitionFinishedLocked(activityRecord.token);
        }
        this.mDisplayContent.mFixedRotationTransitionListener.onFinishRecentsAnimation();
        StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.onRecentsAnimationStateChanged(false);
        }
    }

    public void scheduleFailsafe() {
        this.mService.f1164mH.postDelayed(this.mFailsafeRunnable, 1000L);
    }

    public void onFailsafe() {
        forceCancelAnimation(this.mWillFinishToHome ? 1 : 2, "onFailsafe");
    }

    public final void linkToDeathOfRunner() throws RemoteException {
        if (this.mLinkedToDeathOfRunner) {
            return;
        }
        this.mRunner.asBinder().linkToDeath(this, 0);
        this.mLinkedToDeathOfRunner = true;
    }

    public final void unlinkToDeathOfRunner() {
        if (this.mLinkedToDeathOfRunner) {
            this.mRunner.asBinder().unlinkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = false;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        forceCancelAnimation(2, "binderDied");
        synchronized (this.mService.getWindowManagerLock()) {
            this.mDisplayContent.getInputMonitor().destroyInputConsumer("recents_animation_input_consumer");
        }
    }

    public void checkAnimationReady(WallpaperController wallpaperController) {
        if (this.mPendingStart) {
            if (!isTargetOverWallpaper() || (wallpaperController.getWallpaperTarget() != null && wallpaperController.wallpaperTransitionReady())) {
                this.mService.getRecentsAnimationController().startAnimation();
            }
        }
    }

    public boolean isWallpaperVisible(WindowState windowState) {
        ActivityRecord activityRecord;
        return windowState != null && windowState.mAttrs.type == 1 && (((activityRecord = windowState.mActivityRecord) != null && this.mTargetActivityRecord == activityRecord) || isAnimatingTask(windowState.getTask())) && isTargetOverWallpaper() && windowState.isOnScreen();
    }

    public boolean shouldApplyInputConsumer(ActivityRecord activityRecord) {
        return this.mInputConsumerEnabled && activityRecord != null && !isTargetApp(activityRecord) && isAnimatingApp(activityRecord);
    }

    public boolean updateInputConsumerForApp(InputWindowHandle inputWindowHandle) {
        WindowState targetAppMainWindow = getTargetAppMainWindow();
        if (targetAppMainWindow != null) {
            targetAppMainWindow.getBounds(this.mTmpRect);
            inputWindowHandle.touchableRegion.set(this.mTmpRect);
            return true;
        }
        return false;
    }

    public boolean isTargetApp(ActivityRecord activityRecord) {
        ActivityRecord activityRecord2 = this.mTargetActivityRecord;
        return activityRecord2 != null && activityRecord == activityRecord2;
    }

    public final boolean isTargetOverWallpaper() {
        ActivityRecord activityRecord = this.mTargetActivityRecord;
        if (activityRecord == null) {
            return false;
        }
        return activityRecord.windowsCanBeWallpaperTarget();
    }

    public WindowState getTargetAppMainWindow() {
        ActivityRecord activityRecord = this.mTargetActivityRecord;
        if (activityRecord == null) {
            return null;
        }
        return activityRecord.findMainWindow();
    }

    public DisplayArea getTargetAppDisplayArea() {
        ActivityRecord activityRecord = this.mTargetActivityRecord;
        if (activityRecord == null) {
            return null;
        }
        return activityRecord.getDisplayArea();
    }

    public boolean isAnimatingTask(Task task) {
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            if (task == this.mPendingAnimations.get(size).mTask) {
                return true;
            }
        }
        return false;
    }

    public final boolean isAnimatingApp(ActivityRecord activityRecord) {
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            if (activityRecord.isDescendantOf(this.mPendingAnimations.get(size).mTask)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldIgnoreForAccessibility(WindowState windowState) {
        Task task = windowState.getTask();
        return (task == null || !isAnimatingTask(task) || isTargetApp(windowState.mActivityRecord)) ? false : true;
    }

    public void linkFixedRotationTransformIfNeeded(WindowToken windowToken) {
        ActivityRecord activityRecord = this.mTargetActivityRecord;
        if (activityRecord == null) {
            return;
        }
        windowToken.linkFixedRotationTransform(activityRecord);
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.RecentsAnimationController$TaskAnimationAdapter */
    /* loaded from: classes2.dex */
    public class TaskAnimationAdapter implements AnimationAdapter {
        public final Rect mBounds;
        public SurfaceAnimator.OnAnimationFinishedCallback mCapturedFinishCallback;
        public SurfaceControl mCapturedLeash;
        public SurfaceControl mFinishOverlay;
        public PictureInPictureSurfaceTransaction mFinishTransaction;
        public final boolean mIsRecentTaskInvisible;
        public int mLastAnimationType;
        public final Rect mLocalBounds;
        public SurfaceControl mSnapshotOverlay;
        public RemoteAnimationTarget mTarget;
        public final Task mTask;

        @Override // com.android.server.p014wm.AnimationAdapter
        public long getDurationHint() {
            return 0L;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public boolean getShowWallpaper() {
            return false;
        }

        public TaskAnimationAdapter(Task task, boolean z) {
            Rect rect = new Rect();
            this.mBounds = rect;
            Rect rect2 = new Rect();
            this.mLocalBounds = rect2;
            this.mTask = task;
            this.mIsRecentTaskInvisible = z;
            rect.set(task.getBounds());
            rect2.set(rect);
            Point point = new Point();
            task.getRelativePosition(point);
            rect2.offsetTo(point.x, point.y);
        }

        public RemoteAnimationTarget createRemoteAnimationTarget(int i, int i2) {
            int i3;
            StartingData startingData;
            ActivityRecord topRealVisibleActivity = this.mTask.getTopRealVisibleActivity();
            if (topRealVisibleActivity == null) {
                topRealVisibleActivity = this.mTask.getTopVisibleActivity();
            }
            WindowState findMainWindow = topRealVisibleActivity != null ? topRealVisibleActivity.findMainWindow() : null;
            if (findMainWindow == null) {
                return null;
            }
            Rect rect = findMainWindow.getInsetsStateWithVisibilityOverride().calculateInsets(this.mBounds, WindowInsets.Type.systemBars(), false).toRect();
            InsetUtils.addInsets(rect, findMainWindow.mActivityRecord.getLetterboxInsets());
            if (i2 != -1) {
                i3 = i2;
            } else {
                i3 = topRealVisibleActivity.getActivityType() == RecentsAnimationController.this.mTargetActivityType ? 0 : 1;
            }
            int i4 = i < 0 ? this.mTask.mTaskId : i;
            SurfaceControl surfaceControl = this.mCapturedLeash;
            boolean z = !topRealVisibleActivity.fillsParent();
            Rect rect2 = new Rect();
            int prefixOrderIndex = this.mTask.getPrefixOrderIndex();
            Rect rect3 = this.mBounds;
            this.mTarget = new RemoteAnimationTarget(i4, i3, surfaceControl, z, rect2, rect, prefixOrderIndex, new Point(rect3.left, rect3.top), this.mLocalBounds, this.mBounds, this.mTask.getWindowConfiguration(), this.mIsRecentTaskInvisible, (SurfaceControl) null, (Rect) null, this.mTask.getTaskInfo(), topRealVisibleActivity.checkEnterPictureInPictureAppOpsState());
            ActivityRecord topNonFinishingActivity = this.mTask.getTopNonFinishingActivity();
            if (topNonFinishingActivity != null && (startingData = topNonFinishingActivity.mStartingData) != null && startingData.hasImeSurface()) {
                this.mTarget.setWillShowImeOnTarget(true);
            }
            return this.mTarget;
        }

        public void setSnapshotOverlay(TaskSnapshot taskSnapshot) {
            HardwareBuffer hardwareBuffer = taskSnapshot.getHardwareBuffer();
            if (hardwareBuffer == null) {
                return;
            }
            this.mSnapshotOverlay = RecentsAnimationController.this.mService.mSurfaceControlFactory.apply(new SurfaceSession()).setName("RecentTaskScreenshotSurface").setCallsite("TaskAnimationAdapter.setSnapshotOverlay").setFormat(hardwareBuffer.getFormat()).setParent(this.mCapturedLeash).setBLASTLayer().build();
            float width = (this.mTask.getBounds().width() * 1.0f) / hardwareBuffer.getWidth();
            this.mTask.getPendingTransaction().setBuffer(this.mSnapshotOverlay, GraphicBuffer.createFromHardwareBuffer(hardwareBuffer)).setColorSpace(this.mSnapshotOverlay, taskSnapshot.getColorSpace()).setLayer(this.mSnapshotOverlay, Integer.MAX_VALUE).setMatrix(this.mSnapshotOverlay, width, 0.0f, 0.0f, width).show(this.mSnapshotOverlay).apply();
        }

        public void onRemove() {
            if (this.mSnapshotOverlay != null) {
                this.mTask.getPendingTransaction().remove(this.mSnapshotOverlay).apply();
                this.mSnapshotOverlay = null;
            }
            this.mTask.setCanAffectSystemUiFlags(true);
            this.mCapturedFinishCallback.onAnimationFinished(this.mLastAnimationType, this);
        }

        public void onCleanup() {
            SurfaceControl.Transaction pendingTransaction = this.mTask.getPendingTransaction();
            if (this.mFinishTransaction != null) {
                SurfaceControl surfaceControl = this.mFinishOverlay;
                if (surfaceControl != null) {
                    pendingTransaction.reparent(surfaceControl, this.mTask.mSurfaceControl);
                }
                PictureInPictureSurfaceTransaction.apply(this.mFinishTransaction, this.mTask.mSurfaceControl, pendingTransaction);
                this.mTask.setLastRecentsAnimationTransaction(this.mFinishTransaction, this.mFinishOverlay);
                if (RecentsAnimationController.this.mDisplayContent.isFixedRotationLaunchingApp(RecentsAnimationController.this.mTargetActivityRecord)) {
                    RecentsAnimationController.this.mDisplayContent.mPinnedTaskController.setEnterPipTransaction(this.mFinishTransaction);
                }
                if (this.mTask.getActivityType() != RecentsAnimationController.this.mTargetActivityType && this.mFinishTransaction.getShouldDisableCanAffectSystemUiFlags()) {
                    this.mTask.setCanAffectSystemUiFlags(false);
                }
                this.mFinishTransaction = null;
                this.mFinishOverlay = null;
                pendingTransaction.apply();
            } else if (this.mTask.isAttached()) {
            } else {
                pendingTransaction.apply();
            }
        }

        @VisibleForTesting
        public SurfaceControl getSnapshotOverlay() {
            return this.mSnapshotOverlay;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
            Rect rect = this.mLocalBounds;
            transaction.setPosition(surfaceControl, rect.left, rect.top);
            RecentsAnimationController.this.mTmpRect.set(this.mLocalBounds);
            RecentsAnimationController.this.mTmpRect.offsetTo(0, 0);
            transaction.setWindowCrop(surfaceControl, RecentsAnimationController.this.mTmpRect);
            this.mCapturedLeash = surfaceControl;
            this.mCapturedFinishCallback = onAnimationFinishedCallback;
            this.mLastAnimationType = i;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void onAnimationCancelled(SurfaceControl surfaceControl) {
            RecentsAnimationController.this.cancelAnimation(2, "taskAnimationAdapterCanceled");
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public long getStatusBarTransitionsStartTime() {
            return SystemClock.uptimeMillis();
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.println("task=" + this.mTask);
            if (this.mTarget != null) {
                printWriter.print(str);
                printWriter.println("Target:");
                RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
                remoteAnimationTarget.dump(printWriter, str + "  ");
            } else {
                printWriter.print(str);
                printWriter.println("Target: null");
            }
            printWriter.println("mIsRecentTaskInvisible=" + this.mIsRecentTaskInvisible);
            printWriter.println("mLocalBounds=" + this.mLocalBounds);
            printWriter.println("mFinishTransaction=" + this.mFinishTransaction);
            printWriter.println("mBounds=" + this.mBounds);
            printWriter.println("mIsRecentTaskInvisible=" + this.mIsRecentTaskInvisible);
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void dumpDebug(ProtoOutputStream protoOutputStream) {
            long start = protoOutputStream.start(1146756268034L);
            RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
            if (remoteAnimationTarget != null) {
                remoteAnimationTarget.dumpDebug(protoOutputStream, 1146756268033L);
            }
            protoOutputStream.end(start);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.println(RecentsAnimationController.class.getSimpleName() + XmlUtils.STRING_ARRAY_SEPARATOR);
        printWriter.print(str2);
        printWriter.println("mPendingStart=" + this.mPendingStart);
        printWriter.print(str2);
        printWriter.println("mPendingAnimations=" + this.mPendingAnimations.size());
        printWriter.print(str2);
        printWriter.println("mCanceled=" + this.mCanceled);
        printWriter.print(str2);
        printWriter.println("mInputConsumerEnabled=" + this.mInputConsumerEnabled);
        printWriter.print(str2);
        printWriter.println("mTargetActivityRecord=" + this.mTargetActivityRecord);
        printWriter.print(str2);
        printWriter.println("isTargetOverWallpaper=" + isTargetOverWallpaper());
        printWriter.print(str2);
        printWriter.println("mRequestDeferCancelUntilNextTransition=" + this.mRequestDeferCancelUntilNextTransition);
        printWriter.print(str2);
        printWriter.println("mCancelOnNextTransitionStart=" + this.mCancelOnNextTransitionStart);
        printWriter.print(str2);
        printWriter.println("mCancelDeferredWithScreenshot=" + this.mCancelDeferredWithScreenshot);
        printWriter.print(str2);
        printWriter.println("mPendingCancelWithScreenshotReorderMode=" + this.mPendingCancelWithScreenshotReorderMode);
    }
}
