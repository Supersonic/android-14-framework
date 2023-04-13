package com.android.server.p014wm;

import android.content.res.ResourceId;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.window.BackAnimationAdapter;
import android.window.BackNavigationInfo;
import android.window.IBackAnimationFinishedCallback;
import android.window.OnBackInvokedCallbackInfo;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.TransitionAnimation;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p014wm.BackNavigationController;
import com.android.server.p014wm.SurfaceAnimator;
import com.android.server.p014wm.Transition;
import com.android.server.p014wm.utils.InsetUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.BackNavigationController */
/* loaded from: classes2.dex */
public class BackNavigationController {
    public static int sDefaultAnimationResId;
    public static final boolean sPredictBackEnable = SystemProperties.getBoolean("persist.wm.debug.predictive_back", true);
    public AnimationHandler mAnimationHandler;
    public boolean mBackAnimationInProgress;
    @BackNavigationInfo.BackTargetType
    public int mLastBackType;
    public Runnable mPendingAnimation;
    public AnimationHandler.ScheduleAnimationBuilder mPendingAnimationBuilder;
    public boolean mShowWallpaper;
    public WindowManagerService mWindowManagerService;
    public final NavigationMonitor mNavigationMonitor = new NavigationMonitor();
    public final ArrayList<WindowContainer> mTmpOpenApps = new ArrayList<>();
    public final ArrayList<WindowContainer> mTmpCloseApps = new ArrayList<>();

    public static boolean isScreenshotEnabled() {
        return SystemProperties.getInt("persist.wm.debug.predictive_back_screenshot", 0) != 0;
    }

    public void onFocusChanged(WindowState windowState) {
        this.mNavigationMonitor.onFocusWindowChanged(windowState);
    }

    @VisibleForTesting
    public BackNavigationInfo startBackNavigation(RemoteCallback remoteCallback, BackAnimationAdapter backAnimationAdapter) {
        Task task;
        ActivityRecord activityRecord;
        OnBackInvokedCallbackInfo onBackInvokedCallbackInfo;
        int i;
        Task task2;
        Task task3;
        if (sPredictBackEnable) {
            WindowManagerService windowManagerService = this.mWindowManagerService;
            BackNavigationInfo.Builder builder = new BackNavigationInfo.Builder();
            synchronized (windowManagerService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    IBinder focusedWindowToken = ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).getFocusedWindowToken();
                    WindowState focusedWindowLocked = windowManagerService.getFocusedWindowLocked();
                    if (focusedWindowLocked == null && windowManagerService.mEmbeddedWindowController.getByFocusToken(focusedWindowToken) != null) {
                        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -1717147904, 0, "Current focused window is embeddedWindow. Dispatch KEYCODE_BACK.", (Object[]) null);
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    if (focusedWindowLocked != null && ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -997565097, 0, "Focused window found using getFocusedWindowToken", (Object[]) null);
                    }
                    if (focusedWindowLocked != null) {
                        RecentsAnimationController recentsAnimationController = windowManagerService.getRecentsAnimationController();
                        ActivityRecord activityRecord2 = focusedWindowLocked.mActivityRecord;
                        if ((activityRecord2 != null && activityRecord2.isActivityTypeHomeOrRecents() && activityRecord2.mTransitionController.isTransientLaunch(activityRecord2)) || (recentsAnimationController != null && recentsAnimationController.shouldApplyInputConsumer(activityRecord2))) {
                            if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -451552570, 0, "Current focused window being animated by recents. Overriding back callback to recents controller callback.", (Object[]) null);
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        } else if (!focusedWindowLocked.isDrawn()) {
                            if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, 1945495497, 0, "Focused window didn't have a valid surface drawn.", (Object[]) null);
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        }
                    }
                    if (focusedWindowLocked == null) {
                        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, 1264179654, 0, "No focused window, defaulting to top current task's window", (Object[]) null);
                        }
                        task = windowManagerService.mAtmService.getTopDisplayFocusedRootTask();
                        focusedWindowLocked = task.getWindow(new Predicate() { // from class: com.android.server.wm.BackNavigationController$$ExternalSyntheticLambda0
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj) {
                                return ((WindowState) obj).isFocused();
                            }
                        });
                    } else {
                        task = null;
                    }
                    final int i2 = 4;
                    if (focusedWindowLocked != null) {
                        ActivityRecord activityRecord3 = focusedWindowLocked.mActivityRecord;
                        Task task4 = focusedWindowLocked.getTask();
                        onBackInvokedCallbackInfo = focusedWindowLocked.getOnBackInvokedCallbackInfo();
                        if (onBackInvokedCallbackInfo == null) {
                            Slog.e("BackNavigationController", "No callback registered, returning null.");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        }
                        i = !onBackInvokedCallbackInfo.isSystemCallback() ? 4 : -1;
                        builder.setOnBackInvokedCallback(onBackInvokedCallbackInfo.getCallback());
                        this.mNavigationMonitor.startMonitor(focusedWindowLocked, remoteCallback);
                        activityRecord = activityRecord3;
                        task = task4;
                    } else {
                        activityRecord = null;
                        onBackInvokedCallbackInfo = null;
                        i = -1;
                    }
                    if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -1277068810, 0, "startBackNavigation currentTask=%s, topRunningActivity=%s, callbackInfo=%s, currentFocus=%s", new Object[]{String.valueOf(task), String.valueOf(activityRecord), String.valueOf(onBackInvokedCallbackInfo), String.valueOf(focusedWindowLocked)});
                    }
                    if (focusedWindowLocked == null) {
                        Slog.e("BackNavigationController", "Window is null, returning null.");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    if (i != 4 && activityRecord != null && task != null && !activityRecord.isActivityTypeHome() && !activityRecord.mHasSceneTransition) {
                        ActivityRecord activityRecord4 = task.topRunningActivity(activityRecord.token, -1);
                        boolean isKeyguardOccluded = isKeyguardOccluded(focusedWindowLocked);
                        if (focusedWindowLocked.getParent().getChildCount() > 1 && focusedWindowLocked.getParent().getChildAt(0) != focusedWindowLocked) {
                            task2 = null;
                            i2 = 0;
                        } else if (activityRecord4 != null) {
                            if (!isKeyguardOccluded || activityRecord4.canShowWhenLocked()) {
                                WindowContainer parent = activityRecord.getParent();
                                if (parent != null && (parent.asTask() != null || (parent.asTaskFragment() != null && parent.canCustomizeAppTransition())) && isCustomizeExitAnimation(focusedWindowLocked)) {
                                    WindowManager.LayoutParams layoutParams = focusedWindowLocked.mAttrs;
                                    builder.setWindowAnimations(layoutParams.packageName, layoutParams.windowAnimations);
                                }
                                task2 = activityRecord4.getTask();
                                focusedWindowLocked = activityRecord;
                                i2 = 2;
                            }
                            focusedWindowLocked = null;
                            task2 = null;
                        } else {
                            if (task.returnsToHomeRootTask()) {
                                if (!isKeyguardOccluded) {
                                    task3 = task.getDisplayArea().getRootHomeTask();
                                    this.mShowWallpaper = true;
                                }
                                focusedWindowLocked = null;
                                task2 = null;
                            } else if (activityRecord.isRootOfTask()) {
                                task3 = task.mRootWindowContainer.getTask(new Predicate() { // from class: com.android.server.wm.BackNavigationController$$ExternalSyntheticLambda2
                                    @Override // java.util.function.Predicate
                                    public final boolean test(Object obj) {
                                        return ((Task) obj).showToCurrentUser();
                                    }
                                }, task, false, true);
                                if (task3 != null && !task3.inMultiWindowMode() && (activityRecord4 = task3.getTopNonFinishingActivity()) != null && (!isKeyguardOccluded || activityRecord4.canShowWhenLocked())) {
                                    if (task3.isActivityTypeHome()) {
                                        this.mShowWallpaper = true;
                                    } else {
                                        task2 = task3;
                                        i2 = 3;
                                        focusedWindowLocked = task;
                                    }
                                }
                                task2 = task3;
                                focusedWindowLocked = task;
                            } else {
                                focusedWindowLocked = null;
                                task2 = null;
                                i2 = i;
                            }
                            task2 = task3;
                            i2 = 1;
                            focusedWindowLocked = task;
                        }
                        builder.setType(i2);
                        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, 531891870, 0, "Previous Destination is Activity:%s Task:%s removedContainer:%s, backType=%s", new Object[]{String.valueOf(activityRecord4 != null ? activityRecord4.mActivityComponent : null), String.valueOf(task2 != null ? task2.getName() : null), String.valueOf(focusedWindowLocked), String.valueOf(BackNavigationInfo.typeToString(i2))});
                        }
                        boolean z = (i2 == 1 || i2 == 3 || i2 == 2) && backAnimationAdapter != null;
                        if (z) {
                            AnimationHandler.ScheduleAnimationBuilder prepareAnimation = this.mAnimationHandler.prepareAnimation(i2, backAnimationAdapter, task, task2, activityRecord, activityRecord4);
                            boolean z2 = prepareAnimation != null;
                            this.mBackAnimationInProgress = z2;
                            if (z2) {
                                if (!focusedWindowLocked.hasCommittedReparentToAnimationLeash() && !focusedWindowLocked.mTransitionController.inTransition() && !this.mWindowManagerService.mSyncEngine.hasPendingSyncSets()) {
                                    scheduleAnimation(prepareAnimation);
                                }
                                if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -1868518158, 0, "Pending back animation due to another animation is running", (Object[]) null);
                                }
                                this.mPendingAnimationBuilder = prepareAnimation;
                                if (activityRecord4 != null) {
                                    activityRecord4.setDeferHidingClient(true);
                                }
                            }
                        }
                        builder.setPrepareRemoteAnimation(z);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        if (focusedWindowLocked != null) {
                            builder.setOnBackNavigationDone(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.wm.BackNavigationController$$ExternalSyntheticLambda3
                                public final void onResult(Bundle bundle) {
                                    BackNavigationController.this.lambda$startBackNavigation$1(i2, bundle);
                                }
                            }));
                        }
                        this.mLastBackType = i2;
                        return builder.build();
                    }
                    builder.setType(4);
                    builder.setOnBackNavigationDone(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.wm.BackNavigationController$$ExternalSyntheticLambda1
                        public final void onResult(Bundle bundle) {
                            BackNavigationController.this.lambda$startBackNavigation$0(bundle);
                        }
                    }));
                    this.mLastBackType = 4;
                    BackNavigationInfo build = builder.build();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return build;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startBackNavigation$0(Bundle bundle) {
        lambda$startBackNavigation$1(bundle, 4);
    }

    public boolean isMonitoringTransition() {
        return isWaitBackTransition() || this.mNavigationMonitor.isMonitoring();
    }

    public final void scheduleAnimation(AnimationHandler.ScheduleAnimationBuilder scheduleAnimationBuilder) {
        this.mPendingAnimation = scheduleAnimationBuilder.build();
        this.mWindowManagerService.mWindowPlacerLocked.requestTraversal();
        if (this.mShowWallpaper) {
            this.mWindowManagerService.getDefaultDisplayContentLocked().mWallpaperController.adjustWallpaperWindows();
        }
    }

    public final boolean isWaitBackTransition() {
        return this.mAnimationHandler.mComposed && this.mAnimationHandler.mWaitTransition;
    }

    public boolean isKeyguardOccluded(WindowState windowState) {
        KeyguardController keyguardController = this.mWindowManagerService.mAtmService.mKeyguardController;
        int displayId = windowState.getDisplayId();
        return keyguardController.isKeyguardLocked(displayId) && keyguardController.isDisplayOccluded(displayId);
    }

    public static boolean isCustomizeExitAnimation(WindowState windowState) {
        if (Objects.equals(windowState.mAttrs.packageName, PackageManagerShellCommandDataLoader.PACKAGE) || windowState.mAttrs.windowAnimations == 0) {
            return false;
        }
        TransitionAnimation transitionAnimation = windowState.getDisplayContent().mAppTransition.mTransitionAnimation;
        int animationResId = transitionAnimation.getAnimationResId(windowState.mAttrs, 7, 0);
        if (ResourceId.isValid(animationResId)) {
            if (sDefaultAnimationResId == 0) {
                sDefaultAnimationResId = transitionAnimation.getDefaultAnimationResId(7, 0);
            }
            return sDefaultAnimationResId != animationResId;
        }
        return false;
    }

    public boolean removeIfContainsBackAnimationTargets(ArraySet<ActivityRecord> arraySet, ArraySet<ActivityRecord> arraySet2) {
        if (isMonitoringTransition()) {
            this.mTmpCloseApps.addAll(arraySet2);
            boolean removeIfWaitForBackTransition = removeIfWaitForBackTransition(arraySet, arraySet2);
            if (!removeIfWaitForBackTransition) {
                this.mNavigationMonitor.onTransitionReadyWhileNavigate(this.mTmpOpenApps, this.mTmpCloseApps);
            }
            this.mTmpCloseApps.clear();
            return removeIfWaitForBackTransition;
        }
        return false;
    }

    public boolean removeIfWaitForBackTransition(ArraySet<ActivityRecord> arraySet, ArraySet<ActivityRecord> arraySet2) {
        if (isWaitBackTransition() && this.mAnimationHandler.containsBackAnimationTargets(this.mTmpOpenApps, this.mTmpCloseApps)) {
            for (int size = arraySet.size() - 1; size >= 0; size--) {
                if (this.mAnimationHandler.isTarget(arraySet.valueAt(size), true)) {
                    arraySet.removeAt(size);
                    this.mAnimationHandler.mOpenTransitionTargetMatch = true;
                }
            }
            for (int size2 = arraySet2.size() - 1; size2 >= 0; size2--) {
                if (this.mAnimationHandler.isTarget(arraySet2.valueAt(size2), false)) {
                    arraySet2.removeAt(size2);
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: com.android.server.wm.BackNavigationController$NavigationMonitor */
    /* loaded from: classes2.dex */
    public static class NavigationMonitor {
        public WindowState mNavigatingWindow;
        public RemoteCallback mObserver;

        public NavigationMonitor() {
        }

        public void startMonitor(WindowState windowState, RemoteCallback remoteCallback) {
            this.mNavigatingWindow = windowState;
            this.mObserver = remoteCallback;
        }

        public void stopMonitor() {
            this.mNavigatingWindow = null;
            this.mObserver = null;
        }

        public boolean isMonitoring() {
            return (this.mNavigatingWindow == null || this.mObserver == null) ? false : true;
        }

        public final void onFocusWindowChanged(WindowState windowState) {
            WindowState windowState2;
            if (!isMonitoring() || !atSameDisplay(windowState) || windowState == null || windowState == (windowState2 = this.mNavigatingWindow)) {
                return;
            }
            ActivityRecord activityRecord = windowState.mActivityRecord;
            if (activityRecord == null || activityRecord == windowState2.mActivityRecord) {
                EventLogTags.writeWmBackNaviCanceled("focusWindowChanged");
                this.mObserver.sendResult((Bundle) null);
            }
        }

        public final void onTransitionReadyWhileNavigate(ArrayList<WindowContainer> arrayList, ArrayList<WindowContainer> arrayList2) {
            if (isMonitoring()) {
                ArrayList arrayList3 = new ArrayList(arrayList);
                arrayList3.addAll(arrayList2);
                Iterator it = arrayList3.iterator();
                while (it.hasNext()) {
                    if (((WindowContainer) it.next()).hasChild(this.mNavigatingWindow)) {
                        EventLogTags.writeWmBackNaviCanceled("transitionHappens");
                        this.mObserver.sendResult((Bundle) null);
                        return;
                    }
                }
            }
        }

        public final boolean atSameDisplay(WindowState windowState) {
            return windowState == null || windowState.getDisplayId() == this.mNavigatingWindow.getDisplayId();
        }
    }

    public boolean containsBackAnimationTargets(Transition transition) {
        int i;
        boolean z = false;
        if (isMonitoringTransition()) {
            ArraySet<WindowContainer> arraySet = transition.mParticipants;
            for (int size = arraySet.size() - 1; size >= 0; size--) {
                WindowContainer valueAt = arraySet.valueAt(size);
                if (valueAt.asActivityRecord() != null || valueAt.asTask() != null) {
                    if (valueAt.isVisibleRequested()) {
                        this.mTmpOpenApps.add(valueAt);
                    } else {
                        this.mTmpCloseApps.add(valueAt);
                    }
                }
            }
            if (isWaitBackTransition() && (((i = transition.mType) == 2 || i == 4) && this.mAnimationHandler.containsBackAnimationTargets(this.mTmpOpenApps, this.mTmpCloseApps))) {
                z = true;
            }
            if (!z) {
                this.mNavigationMonitor.onTransitionReadyWhileNavigate(this.mTmpOpenApps, this.mTmpCloseApps);
            }
            this.mTmpOpenApps.clear();
            this.mTmpCloseApps.clear();
            return z;
        }
        return false;
    }

    public boolean isMonitorTransitionTarget(WindowContainer windowContainer) {
        if (isWaitBackTransition()) {
            return this.mAnimationHandler.isTarget(windowContainer, windowContainer.isVisibleRequested());
        }
        return false;
    }

    public void clearBackAnimations(SurfaceControl.Transaction transaction) {
        this.mAnimationHandler.clearBackAnimateTarget(transaction);
    }

    public boolean handleDeferredBackAnimation(ArrayList<Transition.ChangeInfo> arrayList) {
        if (!this.mBackAnimationInProgress || this.mPendingAnimationBuilder == null) {
            return false;
        }
        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -692907078, 0, "Handling the deferred animation after transition finished", (Object[]) null);
        }
        SurfaceControl.Transaction pendingTransaction = this.mPendingAnimationBuilder.mCloseTarget.getPendingTransaction();
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            WindowContainer windowContainer = arrayList.get(i).mContainer;
            if ((windowContainer.asActivityRecord() != null || windowContainer.asTask() != null) && this.mPendingAnimationBuilder.containTarget(windowContainer)) {
                pendingTransaction.show(windowContainer.getSurfaceControl());
                z = true;
            }
        }
        if (!z) {
            Slog.w("BackNavigationController", "Finished transition didn't include the targets open: " + this.mPendingAnimationBuilder.mOpenTarget + " close: " + this.mPendingAnimationBuilder.mCloseTarget);
            try {
                this.mPendingAnimationBuilder.mBackAnimationAdapter.getRunner().onAnimationCancelled();
                this.mPendingAnimationBuilder = null;
                return false;
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        scheduleAnimation(this.mPendingAnimationBuilder);
        this.mPendingAnimationBuilder = null;
        return true;
    }

    /* renamed from: com.android.server.wm.BackNavigationController$AnimationHandler */
    /* loaded from: classes2.dex */
    public static class AnimationHandler {
        public BackWindowAnimationAdaptor mCloseAdaptor;
        public boolean mComposed;
        public SurfaceControl.Transaction mFinishedTransaction;
        public BackWindowAnimationAdaptor mOpenAdaptor;
        public boolean mOpenTransitionTargetMatch;
        public int mRequestedStartingSurfaceTaskId;
        public SurfaceControl mStartingSurface;
        public int mSwitchType = 0;
        public boolean mWaitTransition;
        public final WindowManagerService mWindowManagerService;

        public AnimationHandler(WindowManagerService windowManagerService) {
            this.mWindowManagerService = windowManagerService;
        }

        public final void initiate(WindowContainer windowContainer, WindowContainer windowContainer2) {
            ActivityRecord topNonFinishingActivity;
            if (windowContainer.asActivityRecord() != null && windowContainer2.asActivityRecord() != null && windowContainer.asActivityRecord().getTask() == windowContainer2.asActivityRecord().getTask()) {
                this.mSwitchType = 2;
                topNonFinishingActivity = windowContainer.asActivityRecord();
            } else if (windowContainer.asTask() != null && windowContainer2.asTask() != null && windowContainer.asTask() != windowContainer2.asTask()) {
                this.mSwitchType = 1;
                topNonFinishingActivity = windowContainer.asTask().getTopNonFinishingActivity();
            } else {
                this.mSwitchType = 0;
                return;
            }
            this.mCloseAdaptor = createAdaptor(topNonFinishingActivity, false);
            this.mOpenAdaptor = createAdaptor(windowContainer2, true);
            if (this.mCloseAdaptor.mAnimationTarget == null || this.mOpenAdaptor.mAnimationTarget == null) {
                Slog.w("BackNavigationController", "composeNewAnimations fail, skip");
                clearBackAnimateTarget(null);
            }
        }

        public boolean composeAnimations(WindowContainer windowContainer, WindowContainer windowContainer2) {
            clearBackAnimateTarget(null);
            if (windowContainer == null || windowContainer2 == null) {
                Slog.e("BackNavigationController", "reset animation with null target close: " + windowContainer + " open: " + windowContainer2);
                return false;
            }
            initiate(windowContainer, windowContainer2);
            if (this.mSwitchType == 0) {
                return false;
            }
            this.mComposed = true;
            this.mWaitTransition = false;
            return true;
        }

        public RemoteAnimationTarget[] getAnimationTargets() {
            if (this.mComposed) {
                return new RemoteAnimationTarget[]{this.mCloseAdaptor.mAnimationTarget, this.mOpenAdaptor.mAnimationTarget};
            }
            return null;
        }

        public boolean isSupportWindowlessSurface() {
            return this.mWindowManagerService.mAtmService.mTaskOrganizerController.isSupportWindowlessStartingSurface();
        }

        public void createStartingSurface(TaskSnapshot taskSnapshot) {
            if (this.mComposed && getTopOpenActivity() == null) {
                Slog.e("BackNavigationController", "createStartingSurface fail, no open activity: " + this);
            }
        }

        public final ActivityRecord getTopOpenActivity() {
            int i = this.mSwitchType;
            if (i == 2) {
                return this.mOpenAdaptor.mTarget.asActivityRecord();
            }
            if (i == 1) {
                return this.mOpenAdaptor.mTarget.asTask().getTopNonFinishingActivity();
            }
            return null;
        }

        public boolean containTarget(ArrayList<WindowContainer> arrayList, boolean z) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (isTarget(arrayList.get(size), z)) {
                    return true;
                }
            }
            return arrayList.isEmpty();
        }

        public boolean isTarget(WindowContainer windowContainer, boolean z) {
            if (z) {
                return windowContainer == this.mOpenAdaptor.mTarget || this.mOpenAdaptor.mTarget.hasChild(windowContainer);
            }
            int i = this.mSwitchType;
            return i == 1 ? windowContainer == this.mCloseAdaptor.mTarget || (windowContainer.asTask() != null && windowContainer.hasChild(this.mCloseAdaptor.mTarget)) : i == 2 && windowContainer == this.mCloseAdaptor.mTarget;
        }

        public boolean setFinishTransaction(SurfaceControl.Transaction transaction) {
            if (this.mComposed) {
                this.mFinishedTransaction = transaction;
                return true;
            }
            return false;
        }

        public void finishPresentAnimations(SurfaceControl.Transaction transaction) {
            if (this.mComposed) {
                if (transaction == null) {
                    transaction = this.mOpenAdaptor.mTarget.getPendingTransaction();
                }
                SurfaceControl.Transaction transaction2 = this.mFinishedTransaction;
                if (transaction2 != null) {
                    transaction.merge(transaction2);
                    this.mFinishedTransaction = null;
                }
                cleanUpWindowlessSurface();
                BackWindowAnimationAdaptor backWindowAnimationAdaptor = this.mCloseAdaptor;
                if (backWindowAnimationAdaptor != null) {
                    backWindowAnimationAdaptor.mTarget.cancelAnimation();
                    this.mCloseAdaptor = null;
                }
                BackWindowAnimationAdaptor backWindowAnimationAdaptor2 = this.mOpenAdaptor;
                if (backWindowAnimationAdaptor2 != null) {
                    backWindowAnimationAdaptor2.mTarget.cancelAnimation();
                    this.mOpenAdaptor = null;
                }
            }
        }

        public final void cleanUpWindowlessSurface() {
            SurfaceControl.Transaction pendingTransaction;
            ActivityRecord topOpenActivity = getTopOpenActivity();
            if (topOpenActivity == null) {
                Slog.w("BackNavigationController", "finishPresentAnimations without top activity: " + this);
            }
            if (topOpenActivity != null) {
                pendingTransaction = topOpenActivity.getPendingTransaction();
            } else {
                pendingTransaction = this.mOpenAdaptor.mTarget.getPendingTransaction();
            }
            boolean z = this.mOpenTransitionTargetMatch & (topOpenActivity != null);
            this.mOpenTransitionTargetMatch = z;
            if (z) {
                pendingTransaction.show(topOpenActivity.getSurfaceControl());
            }
            if (this.mRequestedStartingSurfaceTaskId != 0) {
                SurfaceControl surfaceControl = this.mStartingSurface;
                if (surfaceControl != null && this.mOpenTransitionTargetMatch) {
                    pendingTransaction.reparent(surfaceControl, topOpenActivity.getSurfaceControl());
                }
                this.mStartingSurface = null;
                this.mRequestedStartingSurfaceTaskId = 0;
            }
        }

        public void clearBackAnimateTarget(SurfaceControl.Transaction transaction) {
            finishPresentAnimations(transaction);
            this.mComposed = false;
            this.mWaitTransition = false;
            this.mOpenTransitionTargetMatch = false;
            this.mRequestedStartingSurfaceTaskId = 0;
            this.mSwitchType = 0;
            if (this.mFinishedTransaction != null) {
                Slog.w("BackNavigationController", "Clear back animation, found un-processed finished transaction");
                if (transaction != null) {
                    transaction.merge(this.mFinishedTransaction);
                } else {
                    this.mFinishedTransaction.apply();
                }
                this.mFinishedTransaction = null;
            }
        }

        public boolean containsBackAnimationTargets(ArrayList<WindowContainer> arrayList, ArrayList<WindowContainer> arrayList2) {
            if (containTarget(arrayList2, false)) {
                return containTarget(arrayList, true) || containTarget(arrayList, false);
            }
            return false;
        }

        public String toString() {
            return "AnimationTargets{ openTarget= " + this.mOpenAdaptor.mTarget + " closeTarget= " + this.mCloseAdaptor.mTarget + " mSwitchType= " + this.mSwitchType + " mComposed= " + this.mComposed + " mWaitTransition= " + this.mWaitTransition + '}';
        }

        public static BackWindowAnimationAdaptor createAdaptor(WindowContainer windowContainer, boolean z) {
            BackWindowAnimationAdaptor backWindowAnimationAdaptor = new BackWindowAnimationAdaptor(windowContainer, z);
            windowContainer.startAnimation(windowContainer.getPendingTransaction(), backWindowAnimationAdaptor, false, 256);
            return backWindowAnimationAdaptor;
        }

        /* renamed from: com.android.server.wm.BackNavigationController$AnimationHandler$BackWindowAnimationAdaptor */
        /* loaded from: classes2.dex */
        public static class BackWindowAnimationAdaptor implements AnimationAdapter {
            public RemoteAnimationTarget mAnimationTarget;
            public final Rect mBounds;
            public SurfaceControl mCapturedLeash;
            public final boolean mIsOpen;
            public final WindowContainer mTarget;

            @Override // com.android.server.p014wm.AnimationAdapter
            public void dumpDebug(ProtoOutputStream protoOutputStream) {
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public long getDurationHint() {
                return 0L;
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public boolean getShowWallpaper() {
                return false;
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public long getStatusBarTransitionsStartTime() {
                return 0L;
            }

            public BackWindowAnimationAdaptor(WindowContainer windowContainer, boolean z) {
                Rect rect = new Rect();
                this.mBounds = rect;
                rect.set(windowContainer.getBounds());
                this.mTarget = windowContainer;
                this.mIsOpen = z;
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
                this.mCapturedLeash = surfaceControl;
                createRemoteAnimationTarget(this.mIsOpen);
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public void onAnimationCancelled(SurfaceControl surfaceControl) {
                if (this.mCapturedLeash == surfaceControl) {
                    this.mCapturedLeash = null;
                }
            }

            @Override // com.android.server.p014wm.AnimationAdapter
            public void dump(PrintWriter printWriter, String str) {
                printWriter.print(str + "BackWindowAnimationAdaptor mCapturedLeash=");
                printWriter.print(this.mCapturedLeash);
                printWriter.println();
            }

            public RemoteAnimationTarget createRemoteAnimationTarget(boolean z) {
                ActivityRecord asActivityRecord;
                Rect rect;
                RemoteAnimationTarget remoteAnimationTarget = this.mAnimationTarget;
                if (remoteAnimationTarget != null) {
                    return remoteAnimationTarget;
                }
                Task asTask = this.mTarget.asTask();
                if (asTask != null) {
                    asActivityRecord = asTask.getTopNonFinishingActivity();
                } else {
                    asActivityRecord = this.mTarget.asActivityRecord();
                }
                if (asTask == null && asActivityRecord != null) {
                    asTask = asActivityRecord.getTask();
                }
                if (asTask == null || asActivityRecord == null) {
                    Slog.e("BackNavigationController", "createRemoteAnimationTarget fail " + this.mTarget);
                    return null;
                }
                WindowState findMainWindow = asActivityRecord.findMainWindow();
                if (findMainWindow != null) {
                    rect = findMainWindow.getInsetsStateWithVisibilityOverride().calculateInsets(this.mBounds, WindowInsets.Type.systemBars(), false).toRect();
                    InsetUtils.addInsets(rect, findMainWindow.mActivityRecord.getLetterboxInsets());
                } else {
                    rect = new Rect();
                }
                Rect rect2 = rect;
                int i = !z ? 1 : 0;
                int i2 = asTask.mTaskId;
                SurfaceControl surfaceControl = this.mCapturedLeash;
                boolean z2 = !asActivityRecord.fillsParent();
                Rect rect3 = new Rect();
                int prefixOrderIndex = asActivityRecord.getPrefixOrderIndex();
                Rect rect4 = this.mBounds;
                Point point = new Point(rect4.left, rect4.top);
                Rect rect5 = this.mBounds;
                RemoteAnimationTarget remoteAnimationTarget2 = new RemoteAnimationTarget(i2, i, surfaceControl, z2, rect3, rect2, prefixOrderIndex, point, rect5, rect5, asTask.getWindowConfiguration(), true, (SurfaceControl) null, (Rect) null, asTask.getTaskInfo(), asActivityRecord.checkEnterPictureInPictureAppOpsState());
                this.mAnimationTarget = remoteAnimationTarget2;
                return remoteAnimationTarget2;
            }
        }

        public ScheduleAnimationBuilder prepareAnimation(int i, BackAnimationAdapter backAnimationAdapter, Task task, Task task2, ActivityRecord activityRecord, ActivityRecord activityRecord2) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return null;
                    }
                    return new ScheduleAnimationBuilder(i, backAnimationAdapter).setComposeTarget(task, task2).setOpeningSnapshot(BackNavigationController.getTaskSnapshot(task2));
                }
                return new ScheduleAnimationBuilder(i, backAnimationAdapter).setComposeTarget(activityRecord, activityRecord2).setOpeningSnapshot(BackNavigationController.getActivitySnapshot(activityRecord2));
            }
            return new ScheduleAnimationBuilder(i, backAnimationAdapter).setIsLaunchBehind(true).setComposeTarget(task, task2);
        }

        /* renamed from: com.android.server.wm.BackNavigationController$AnimationHandler$ScheduleAnimationBuilder */
        /* loaded from: classes2.dex */
        public class ScheduleAnimationBuilder {
            public final BackAnimationAdapter mBackAnimationAdapter;
            public WindowContainer mCloseTarget;
            public boolean mIsLaunchBehind;
            public TaskSnapshot mOpenSnapshot;
            public WindowContainer mOpenTarget;
            public final int mType;

            public ScheduleAnimationBuilder(int i, BackAnimationAdapter backAnimationAdapter) {
                this.mType = i;
                this.mBackAnimationAdapter = backAnimationAdapter;
            }

            public ScheduleAnimationBuilder setComposeTarget(WindowContainer windowContainer, WindowContainer windowContainer2) {
                this.mCloseTarget = windowContainer;
                this.mOpenTarget = windowContainer2;
                return this;
            }

            public ScheduleAnimationBuilder setOpeningSnapshot(TaskSnapshot taskSnapshot) {
                this.mOpenSnapshot = taskSnapshot;
                return this;
            }

            public ScheduleAnimationBuilder setIsLaunchBehind(boolean z) {
                this.mIsLaunchBehind = z;
                return this;
            }

            public boolean containTarget(WindowContainer windowContainer) {
                WindowContainer windowContainer2 = this.mOpenTarget;
                return windowContainer == windowContainer2 || windowContainer == this.mCloseTarget || windowContainer.hasChild(windowContainer2) || windowContainer.hasChild(this.mCloseTarget);
            }

            /* JADX WARN: Removed duplicated region for block: B:28:0x0058 A[RETURN] */
            /* JADX WARN: Removed duplicated region for block: B:29:0x0059  */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public Runnable build() {
                final ActivityRecord asActivityRecord;
                if (this.mOpenTarget == null || this.mCloseTarget == null) {
                    return null;
                }
                boolean z = this.mIsLaunchBehind || !AnimationHandler.this.isSupportWindowlessSurface();
                if (z) {
                    if (this.mOpenTarget.asTask() != null) {
                        asActivityRecord = this.mOpenTarget.asTask().getTopNonFinishingActivity();
                    } else if (this.mOpenTarget.asActivityRecord() != null) {
                        asActivityRecord = this.mOpenTarget.asActivityRecord();
                    }
                    if (!z && asActivityRecord == null) {
                        Slog.e("BackNavigationController", "No opening activity");
                        return null;
                    } else if (AnimationHandler.this.composeAnimations(this.mCloseTarget, this.mOpenTarget)) {
                        return null;
                    } else {
                        if (asActivityRecord != null) {
                            BackNavigationController.setLaunchBehind(asActivityRecord);
                        } else {
                            AnimationHandler.this.createStartingSurface(this.mOpenSnapshot);
                        }
                        final IBackAnimationFinishedCallback makeAnimationFinishedCallback = makeAnimationFinishedCallback(asActivityRecord != null ? new Consumer() { // from class: com.android.server.wm.BackNavigationController$AnimationHandler$ScheduleAnimationBuilder$$ExternalSyntheticLambda0
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                BackNavigationController.AnimationHandler.ScheduleAnimationBuilder.lambda$build$0(ActivityRecord.this, (Boolean) obj);
                            }
                        } : null, this.mCloseTarget);
                        final RemoteAnimationTarget[] animationTargets = AnimationHandler.this.getAnimationTargets();
                        return new Runnable() { // from class: com.android.server.wm.BackNavigationController$AnimationHandler$ScheduleAnimationBuilder$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                BackNavigationController.AnimationHandler.ScheduleAnimationBuilder.this.lambda$build$1(animationTargets, makeAnimationFinishedCallback);
                            }
                        };
                    }
                }
                asActivityRecord = null;
                if (!z) {
                }
                if (AnimationHandler.this.composeAnimations(this.mCloseTarget, this.mOpenTarget)) {
                }
            }

            public static /* synthetic */ void lambda$build$0(ActivityRecord activityRecord, Boolean bool) {
                if (bool.booleanValue()) {
                    return;
                }
                BackNavigationController.restoreLaunchBehind(activityRecord);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$build$1(RemoteAnimationTarget[] remoteAnimationTargetArr, IBackAnimationFinishedCallback iBackAnimationFinishedCallback) {
                try {
                    this.mBackAnimationAdapter.getRunner().onAnimationStart(remoteAnimationTargetArr, (RemoteAnimationTarget[]) null, (RemoteAnimationTarget[]) null, iBackAnimationFinishedCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            public final IBackAnimationFinishedCallback makeAnimationFinishedCallback(final Consumer<Boolean> consumer, final WindowContainer windowContainer) {
                return new IBackAnimationFinishedCallback.Stub() { // from class: com.android.server.wm.BackNavigationController.AnimationHandler.ScheduleAnimationBuilder.1
                    public void onAnimationFinished(boolean z) {
                        SurfaceControl surfaceControl;
                        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                        synchronized (AnimationHandler.this.mWindowManagerService.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                Consumer consumer2 = consumer;
                                if (consumer2 != null) {
                                    consumer2.accept(Boolean.valueOf(z));
                                }
                                if (z && (surfaceControl = windowContainer.getSurfaceControl()) != null && surfaceControl.isValid()) {
                                    transaction.hide(surfaceControl);
                                }
                                if (!AnimationHandler.this.setFinishTransaction(transaction)) {
                                    transaction.apply();
                                }
                                if (!z) {
                                    AnimationHandler.this.clearBackAnimateTarget(null);
                                } else {
                                    AnimationHandler.this.mWaitTransition = true;
                                }
                            } catch (Throwable th) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                };
            }
        }
    }

    public static void setLaunchBehind(ActivityRecord activityRecord) {
        if (!activityRecord.isVisibleRequested()) {
            activityRecord.setVisibility(true);
        }
        activityRecord.mLaunchTaskBehind = true;
        DisplayContent displayContent = activityRecord.mDisplayContent;
        displayContent.rotateInDifferentOrientationIfNeeded(activityRecord);
        if (activityRecord.hasFixedRotationTransform()) {
            displayContent.setFixedRotationLaunchingApp(activityRecord, activityRecord.getWindowConfiguration().getRotation());
        }
        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, 948208142, 0, "Setting Activity.mLauncherTaskBehind to true. Activity=%s", new Object[]{String.valueOf(activityRecord)});
        }
        activityRecord.mTaskSupervisor.mStoppingActivities.remove(activityRecord);
        activityRecord.getDisplayContent().ensureActivitiesVisible(null, 0, false, true);
    }

    public static void restoreLaunchBehind(ActivityRecord activityRecord) {
        activityRecord.mDisplayContent.continueUpdateOrientationForDiffOrienLaunchingApp();
        activityRecord.mTaskSupervisor.scheduleLaunchTaskBehindComplete(activityRecord.token);
        activityRecord.mLaunchTaskBehind = false;
        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -711194343, 0, "Setting Activity.mLauncherTaskBehind to false. Activity=%s", new Object[]{String.valueOf(activityRecord)});
        }
    }

    public void checkAnimationReady(WallpaperController wallpaperController) {
        if (this.mBackAnimationInProgress) {
            if (!(!this.mShowWallpaper || (wallpaperController.getWallpaperTarget() != null && wallpaperController.wallpaperTransitionReady())) || this.mPendingAnimation == null) {
                return;
            }
            startAnimation();
        }
    }

    public void startAnimation() {
        Runnable runnable = this.mPendingAnimation;
        if (runnable != null) {
            runnable.run();
            this.mPendingAnimation = null;
        }
    }

    /* renamed from: onBackNavigationDone */
    public final void lambda$startBackNavigation$1(Bundle bundle, int i) {
        boolean z = bundle != null && bundle.getBoolean("TriggerBack");
        if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -1033630971, 12, "onBackNavigationDone backType=%s, triggerBack=%b", new Object[]{String.valueOf(i), Boolean.valueOf(z)});
        }
        this.mNavigationMonitor.stopMonitor();
        this.mBackAnimationInProgress = false;
        this.mShowWallpaper = false;
        this.mPendingAnimationBuilder = null;
    }

    public static TaskSnapshot getActivitySnapshot(ActivityRecord activityRecord) {
        isScreenshotEnabled();
        return null;
    }

    public static TaskSnapshot getTaskSnapshot(Task task) {
        if (isScreenshotEnabled()) {
            return task.mRootWindowContainer.mWindowManager.mTaskSnapshotController.getSnapshot(task.mTaskId, task.mUserId, false, false);
        }
        return null;
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mWindowManagerService = windowManagerService;
        this.mAnimationHandler = new AnimationHandler(windowManagerService);
    }

    public boolean isWallpaperVisible(WindowState windowState) {
        ActivityRecord activityRecord;
        return this.mAnimationHandler.mComposed && this.mShowWallpaper && windowState.mAttrs.type == 1 && (activityRecord = windowState.mActivityRecord) != null && this.mAnimationHandler.isTarget(activityRecord, true);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1133871366145L, this.mBackAnimationInProgress);
        protoOutputStream.write(1120986464258L, this.mLastBackType);
        protoOutputStream.write(1133871366147L, this.mShowWallpaper);
        protoOutputStream.end(start);
    }
}
