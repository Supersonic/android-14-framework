package com.android.server.p014wm;

import android.app.ActivityManager;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.util.ArraySet;
import android.view.SurfaceControl;
import android.window.ScreenCapture;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.p014wm.BaseAppSnapshotPersister;
import com.android.server.policy.WindowManagerPolicy;
import com.google.android.collect.Sets;
import java.io.File;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.TaskSnapshotController */
/* loaded from: classes2.dex */
public class TaskSnapshotController extends AbsAppSnapshotController<Task, TaskSnapshotCache> {
    public final Handler mHandler;
    public final BaseAppSnapshotPersister.PersistInfoProvider mPersistInfoProvider;
    public final TaskSnapshotPersister mPersister;
    public final ArraySet<Task> mSkipClosingAppSnapshotTasks;
    public final ArraySet<Task> mTmpTasks;

    public TaskSnapshotController(WindowManagerService windowManagerService, SnapshotPersistQueue snapshotPersistQueue) {
        super(windowManagerService);
        this.mSkipClosingAppSnapshotTasks = new ArraySet<>();
        this.mTmpTasks = new ArraySet<>();
        this.mHandler = new Handler();
        BaseAppSnapshotPersister.PersistInfoProvider createPersistInfoProvider = createPersistInfoProvider(windowManagerService, new BaseAppSnapshotPersister.DirectoryResolver() { // from class: com.android.server.wm.TaskSnapshotController$$ExternalSyntheticLambda2
            @Override // com.android.server.p014wm.BaseAppSnapshotPersister.DirectoryResolver
            public final File getSystemDirectoryForUser(int i) {
                return Environment.getDataSystemCeDirectory(i);
            }
        });
        this.mPersistInfoProvider = createPersistInfoProvider;
        this.mPersister = new TaskSnapshotPersister(snapshotPersistQueue, createPersistInfoProvider);
        initialize(new TaskSnapshotCache(windowManagerService, new AppSnapshotLoader(createPersistInfoProvider)));
        setSnapshotEnabled(!windowManagerService.mContext.getResources().getBoolean(17891607));
    }

    public static BaseAppSnapshotPersister.PersistInfoProvider createPersistInfoProvider(WindowManagerService windowManagerService, BaseAppSnapshotPersister.DirectoryResolver directoryResolver) {
        boolean z;
        float f = windowManagerService.mContext.getResources().getFloat(17105078);
        float f2 = windowManagerService.mContext.getResources().getFloat(17105088);
        float f3 = 0.0f;
        if (f2 < 0.0f || 1.0f <= f2) {
            throw new RuntimeException("Low-res scale must be between 0 and 1");
        }
        if (f <= 0.0f || 1.0f < f) {
            throw new RuntimeException("High-res scale must be between 0 and 1");
        }
        if (f <= f2) {
            throw new RuntimeException("High-res scale must be greater than low-res scale");
        }
        if (f2 > 0.0f) {
            f3 = f2 / f;
            z = true;
        } else {
            z = false;
        }
        return new BaseAppSnapshotPersister.PersistInfoProvider(directoryResolver, "snapshots", z, f3, windowManagerService.mContext.getResources().getBoolean(17891854));
    }

    public void onTransitionStarting(DisplayContent displayContent) {
        handleClosingApps(displayContent.mClosingApps);
    }

    public void notifyAppVisibilityChanged(ActivityRecord activityRecord, boolean z) {
        if (z) {
            return;
        }
        handleClosingApps(Sets.newArraySet(new ActivityRecord[]{activityRecord}));
    }

    public final void handleClosingApps(ArraySet<ActivityRecord> arraySet) {
        if (shouldDisableSnapshots()) {
            return;
        }
        getClosingTasks(arraySet, this.mTmpTasks);
        snapshotTasks(this.mTmpTasks);
        this.mSkipClosingAppSnapshotTasks.clear();
    }

    @VisibleForTesting
    public void addSkipClosingAppSnapshotTasks(Set<Task> set) {
        if (shouldDisableSnapshots()) {
            return;
        }
        this.mSkipClosingAppSnapshotTasks.addAll(set);
    }

    public void snapshotTasks(ArraySet<Task> arraySet) {
        snapshotTasks(arraySet, false);
    }

    public void recordSnapshot(Task task, boolean z) {
        boolean z2 = z && task.isActivityTypeHome();
        TaskSnapshot recordSnapshotInner = recordSnapshotInner(task, z);
        if (z2 || recordSnapshotInner == null) {
            return;
        }
        this.mPersister.persistSnapshot(task.mTaskId, task.mUserId, recordSnapshotInner);
        task.onSnapshotChanged(recordSnapshotInner);
    }

    public final void snapshotTasks(ArraySet<Task> arraySet, boolean z) {
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            recordSnapshot(arraySet.valueAt(size), z);
        }
    }

    public TaskSnapshot getSnapshot(int i, int i2, boolean z, boolean z2) {
        return ((TaskSnapshotCache) this.mCache).getSnapshot(i, i2, z, z2 && this.mPersistInfoProvider.enableLowResSnapshots());
    }

    public void clearSnapshotCache() {
        ((TaskSnapshotCache) this.mCache).clearRunningCache();
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotController
    public ActivityRecord findAppTokenForSnapshot(Task task) {
        return task.getActivity(new Predicate() { // from class: com.android.server.wm.TaskSnapshotController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAppTokenForSnapshot$1;
                lambda$findAppTokenForSnapshot$1 = TaskSnapshotController.lambda$findAppTokenForSnapshot$1((ActivityRecord) obj);
                return lambda$findAppTokenForSnapshot$1;
            }
        });
    }

    public static /* synthetic */ boolean lambda$findAppTokenForSnapshot$1(ActivityRecord activityRecord) {
        if (activityRecord == null || !activityRecord.isSurfaceShowing() || activityRecord.findMainWindow() == null) {
            return false;
        }
        return activityRecord.forAllWindows(new ToBooleanFunction() { // from class: com.android.server.wm.TaskSnapshotController$$ExternalSyntheticLambda1
            public final boolean apply(Object obj) {
                boolean lambda$findAppTokenForSnapshot$0;
                lambda$findAppTokenForSnapshot$0 = TaskSnapshotController.lambda$findAppTokenForSnapshot$0((WindowState) obj);
                return lambda$findAppTokenForSnapshot$0;
            }
        }, true);
    }

    public static /* synthetic */ boolean lambda$findAppTokenForSnapshot$0(WindowState windowState) {
        WindowStateAnimator windowStateAnimator = windowState.mWinAnimator;
        return windowStateAnimator != null && windowStateAnimator.getShown() && windowState.mWinAnimator.mLastAlpha > 0.0f;
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotController
    public boolean use16BitFormat() {
        return this.mPersistInfoProvider.use16BitFormat();
    }

    public final ScreenCapture.ScreenshotHardwareBuffer createImeSnapshot(Task task, int i) {
        WindowState windowState;
        if (task.getSurfaceControl() == null || (windowState = task.getDisplayContent().mInputMethodWindow) == null || !windowState.isVisible()) {
            return null;
        }
        Rect parentFrame = windowState.getParentFrame();
        parentFrame.offsetTo(0, 0);
        return ScreenCapture.captureLayersExcluding(windowState.getSurfaceControl(), parentFrame, 1.0f, i, (SurfaceControl[]) null);
    }

    public ScreenCapture.ScreenshotHardwareBuffer snapshotImeFromAttachedTask(Task task) {
        if (checkIfReadyToSnapshot(task) == null) {
            return null;
        }
        return createImeSnapshot(task, this.mPersistInfoProvider.use16BitFormat() ? 4 : 1);
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotController
    public ActivityRecord getTopActivity(Task task) {
        return task.getTopMostActivity();
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotController
    public ActivityRecord getTopFullscreenActivity(Task task) {
        return task.getTopFullscreenActivity();
    }

    @Override // com.android.server.p014wm.AbsAppSnapshotController
    public ActivityManager.TaskDescription getTaskDescription(Task task) {
        return task.getTaskDescription();
    }

    @VisibleForTesting
    public void getClosingTasks(ArraySet<ActivityRecord> arraySet, ArraySet<Task> arraySet2) {
        arraySet2.clear();
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            Task task = arraySet.valueAt(size).getTask();
            if (task != null) {
                if (isAnimatingByRecents(task)) {
                    this.mSkipClosingAppSnapshotTasks.add(task);
                }
                if (!task.isVisible() && !this.mSkipClosingAppSnapshotTasks.contains(task)) {
                    arraySet2.add(task);
                }
            }
        }
    }

    public void onAppRemoved(ActivityRecord activityRecord) {
        ((TaskSnapshotCache) this.mCache).onAppRemoved(activityRecord);
    }

    public void onAppDied(ActivityRecord activityRecord) {
        ((TaskSnapshotCache) this.mCache).onAppDied(activityRecord);
    }

    public void notifyTaskRemovedFromRecents(int i, int i2) {
        ((TaskSnapshotCache) this.mCache).onIdRemoved(Integer.valueOf(i));
        this.mPersister.onTaskRemovedFromRecents(i, i2);
    }

    public void removeSnapshotCache(int i) {
        ((TaskSnapshotCache) this.mCache).removeRunningEntry(Integer.valueOf(i));
    }

    public void removeObsoleteTaskFiles(ArraySet<Integer> arraySet, int[] iArr) {
        this.mPersister.removeObsoleteFiles(arraySet, iArr);
    }

    public void screenTurningOff(final int i, final WindowManagerPolicy.ScreenOffListener screenOffListener) {
        if (shouldDisableSnapshots()) {
            screenOffListener.onScreenOff();
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.wm.TaskSnapshotController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    TaskSnapshotController.this.lambda$screenTurningOff$2(i, screenOffListener);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$screenTurningOff$2(int i, WindowManagerPolicy.ScreenOffListener screenOffListener) {
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                snapshotForSleeping(i);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            screenOffListener.onScreenOff();
        }
    }

    public void snapshotForSleeping(int i) {
        DisplayContent displayContent;
        boolean z;
        if (shouldDisableSnapshots()) {
            return;
        }
        WindowManagerService windowManagerService = this.mService;
        if (windowManagerService.mDisplayEnabled && (displayContent = windowManagerService.mRoot.getDisplayContent(i)) != null) {
            this.mTmpTasks.clear();
            displayContent.forAllTasks(new Consumer() { // from class: com.android.server.wm.TaskSnapshotController$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TaskSnapshotController.this.lambda$snapshotForSleeping$3((Task) obj);
                }
            });
            if (i == 0) {
                WindowManagerService windowManagerService2 = this.mService;
                if (windowManagerService2.mPolicy.isKeyguardSecure(windowManagerService2.mCurrentUserId)) {
                    z = true;
                    snapshotTasks(this.mTmpTasks, z);
                }
            }
            z = false;
            snapshotTasks(this.mTmpTasks, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$snapshotForSleeping$3(Task task) {
        if (!task.isVisible() || isAnimatingByRecents(task)) {
            return;
        }
        this.mTmpTasks.add(task);
    }

    public final boolean isAnimatingByRecents(Task task) {
        return task.isAnimatingByRecents() || this.mService.mAtmService.getTransitionController().inRecentsTransition(task);
    }
}
