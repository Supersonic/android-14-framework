package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.app.WindowConfiguration;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.window.ITaskOrganizer;
import android.window.ITaskOrganizerController;
import android.window.StartingWindowInfo;
import android.window.StartingWindowRemovalInfo;
import android.window.TaskAppearedInfo;
import android.window.TaskSnapshot;
import android.window.WindowContainerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p014wm.SurfaceAnimator;
import com.android.server.p014wm.Task;
import com.android.server.p014wm.TaskOrganizerController;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.TaskOrganizerController */
/* loaded from: classes2.dex */
public class TaskOrganizerController extends ITaskOrganizerController.Stub {
    public Consumer<Runnable> mDeferTaskOrgCallbacksConsumer;
    public final WindowManagerGlobalLock mGlobalLock;
    public final ActivityTaskManagerService mService;
    public final ArrayDeque<ITaskOrganizer> mTaskOrganizers = new ArrayDeque<>();
    public final ArrayMap<IBinder, TaskOrganizerState> mTaskOrganizerStates = new ArrayMap<>();
    public final HashSet<Integer> mInterceptBackPressedOnRootTasks = new HashSet<>();

    @VisibleForTesting
    /* renamed from: com.android.server.wm.TaskOrganizerController$DeathRecipient */
    /* loaded from: classes2.dex */
    public class DeathRecipient implements IBinder.DeathRecipient {
        public ITaskOrganizer mTaskOrganizer;

        public DeathRecipient(ITaskOrganizer iTaskOrganizer) {
            this.mTaskOrganizer = iTaskOrganizer;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (TaskOrganizerController.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    TaskOrganizerState taskOrganizerState = (TaskOrganizerState) TaskOrganizerController.this.mTaskOrganizerStates.get(this.mTaskOrganizer.asBinder());
                    if (taskOrganizerState != null) {
                        taskOrganizerState.dispose();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.wm.TaskOrganizerController$TaskOrganizerCallbacks */
    /* loaded from: classes2.dex */
    public static class TaskOrganizerCallbacks {
        public final Consumer<Runnable> mDeferTaskOrgCallbacksConsumer;
        public final ITaskOrganizer mTaskOrganizer;

        public TaskOrganizerCallbacks(ITaskOrganizer iTaskOrganizer, Consumer<Runnable> consumer) {
            this.mDeferTaskOrgCallbacksConsumer = consumer;
            this.mTaskOrganizer = iTaskOrganizer;
        }

        public IBinder getBinder() {
            return this.mTaskOrganizer.asBinder();
        }

        public SurfaceControl prepareLeash(Task task, String str) {
            return new SurfaceControl(task.getSurfaceControl(), str);
        }

        public void onTaskAppeared(Task task) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1918448345, 1, (String) null, new Object[]{Long.valueOf(task.mTaskId)});
            }
            try {
                this.mTaskOrganizer.onTaskAppeared(task.getTaskInfo(), prepareLeash(task, "TaskOrganizerController.onTaskAppeared"));
            } catch (RemoteException e) {
                Slog.e("TaskOrganizerController", "Exception sending onTaskAppeared callback", e);
            }
        }

        public void onTaskVanished(Task task) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1364754753, 1, (String) null, new Object[]{Long.valueOf(task.mTaskId)});
            }
            try {
                this.mTaskOrganizer.onTaskVanished(task.getTaskInfo());
            } catch (RemoteException e) {
                Slog.e("TaskOrganizerController", "Exception sending onTaskVanished callback", e);
            }
        }

        public void onTaskInfoChanged(Task task, ActivityManager.RunningTaskInfo runningTaskInfo) {
            if (task.mTaskAppearedSent) {
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 302969511, 1, (String) null, new Object[]{Long.valueOf(task.mTaskId)});
                }
                if (task.isOrganized()) {
                    try {
                        this.mTaskOrganizer.onTaskInfoChanged(runningTaskInfo);
                    } catch (RemoteException e) {
                        Slog.e("TaskOrganizerController", "Exception sending onTaskInfoChanged callback", e);
                    }
                }
            }
        }

        public void onBackPressedOnTaskRoot(Task task) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -2049725903, 1, (String) null, new Object[]{Long.valueOf(task.mTaskId)});
            }
            if (task.mTaskAppearedSent && task.isOrganized()) {
                try {
                    this.mTaskOrganizer.onBackPressedOnTaskRoot(task.getTaskInfo());
                } catch (Exception e) {
                    Slog.e("TaskOrganizerController", "Exception sending onBackPressedOnTaskRoot callback", e);
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.TaskOrganizerController$TaskOrganizerPendingEventsQueue */
    /* loaded from: classes2.dex */
    public static final class TaskOrganizerPendingEventsQueue {
        public final TaskOrganizerState mOrganizerState;
        public ActivityManager.RunningTaskInfo mTmpTaskInfo;
        public final WeakHashMap<Task, ActivityManager.RunningTaskInfo> mLastSentTaskInfos = new WeakHashMap<>();
        public final ArrayList<PendingTaskEvent> mPendingTaskEvents = new ArrayList<>();

        public TaskOrganizerPendingEventsQueue(TaskOrganizerState taskOrganizerState) {
            this.mOrganizerState = taskOrganizerState;
        }

        @VisibleForTesting
        public ArrayList<PendingTaskEvent> getPendingEventList() {
            return this.mPendingTaskEvents;
        }

        public int numPendingTaskEvents() {
            return this.mPendingTaskEvents.size();
        }

        public void clearPendingTaskEvents() {
            this.mPendingTaskEvents.clear();
        }

        public void addPendingTaskEvent(PendingTaskEvent pendingTaskEvent) {
            this.mPendingTaskEvents.add(pendingTaskEvent);
        }

        public void removePendingTaskEvent(PendingTaskEvent pendingTaskEvent) {
            this.mPendingTaskEvents.remove(pendingTaskEvent);
        }

        public boolean removePendingTaskEvents(Task task) {
            boolean z = false;
            for (int size = this.mPendingTaskEvents.size() - 1; size >= 0; size--) {
                PendingTaskEvent pendingTaskEvent = this.mPendingTaskEvents.get(size);
                if (task.mTaskId == pendingTaskEvent.mTask.mTaskId) {
                    this.mPendingTaskEvents.remove(size);
                    if (pendingTaskEvent.mEventType == 0) {
                        z = true;
                    }
                }
            }
            return z;
        }

        public final PendingTaskEvent getPendingTaskEvent(Task task, int i) {
            for (int size = this.mPendingTaskEvents.size() - 1; size >= 0; size--) {
                PendingTaskEvent pendingTaskEvent = this.mPendingTaskEvents.get(size);
                if (task.mTaskId == pendingTaskEvent.mTask.mTaskId && i == pendingTaskEvent.mEventType) {
                    return pendingTaskEvent;
                }
            }
            return null;
        }

        @VisibleForTesting
        public PendingTaskEvent getPendingLifecycleTaskEvent(Task task) {
            for (int size = this.mPendingTaskEvents.size() - 1; size >= 0; size--) {
                PendingTaskEvent pendingTaskEvent = this.mPendingTaskEvents.get(size);
                if (task.mTaskId == pendingTaskEvent.mTask.mTaskId && pendingTaskEvent.isLifecycleEvent()) {
                    return pendingTaskEvent;
                }
            }
            return null;
        }

        public void dispatchPendingEvents() {
            if (this.mPendingTaskEvents.isEmpty()) {
                return;
            }
            int size = this.mPendingTaskEvents.size();
            for (int i = 0; i < size; i++) {
                dispatchPendingEvent(this.mPendingTaskEvents.get(i));
            }
            this.mPendingTaskEvents.clear();
        }

        public final void dispatchPendingEvent(PendingTaskEvent pendingTaskEvent) {
            Task task = pendingTaskEvent.mTask;
            int i = pendingTaskEvent.mEventType;
            if (i == 0) {
                if (task.taskAppearedReady()) {
                    this.mOrganizerState.mOrganizer.onTaskAppeared(task);
                }
            } else if (i == 1) {
                this.mOrganizerState.mOrganizer.onTaskVanished(task);
                this.mLastSentTaskInfos.remove(task);
            } else if (i == 2) {
                dispatchTaskInfoChanged(task, pendingTaskEvent.mForce);
            } else if (i != 3) {
            } else {
                this.mOrganizerState.mOrganizer.onBackPressedOnTaskRoot(task);
            }
        }

        public final void dispatchTaskInfoChanged(Task task, boolean z) {
            ActivityManager.RunningTaskInfo runningTaskInfo = this.mLastSentTaskInfos.get(task);
            if (this.mTmpTaskInfo == null) {
                this.mTmpTaskInfo = new ActivityManager.RunningTaskInfo();
            }
            this.mTmpTaskInfo.configuration.unset();
            task.fillTaskInfo(this.mTmpTaskInfo);
            if (((this.mTmpTaskInfo.equalsForTaskOrganizer(runningTaskInfo) && WindowOrganizerController.configurationsAreEqualForOrganizer(this.mTmpTaskInfo.configuration, runningTaskInfo.configuration)) ? false : true) || z) {
                ActivityManager.RunningTaskInfo runningTaskInfo2 = this.mTmpTaskInfo;
                this.mLastSentTaskInfos.put(task, runningTaskInfo2);
                this.mTmpTaskInfo = null;
                if (task.isOrganized()) {
                    this.mOrganizerState.mOrganizer.onTaskInfoChanged(task, runningTaskInfo2);
                }
            }
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.TaskOrganizerController$TaskOrganizerState */
    /* loaded from: classes2.dex */
    public class TaskOrganizerState {
        public final DeathRecipient mDeathRecipient;
        public final ArrayList<Task> mOrganizedTasks = new ArrayList<>();
        public final TaskOrganizerCallbacks mOrganizer;
        public final TaskOrganizerPendingEventsQueue mPendingEventsQueue;
        public final int mUid;

        public TaskOrganizerState(ITaskOrganizer iTaskOrganizer, int i) {
            Consumer consumer;
            if (TaskOrganizerController.this.mDeferTaskOrgCallbacksConsumer != null) {
                consumer = TaskOrganizerController.this.mDeferTaskOrgCallbacksConsumer;
            } else {
                final WindowAnimator windowAnimator = TaskOrganizerController.this.mService.mWindowManager.mAnimator;
                Objects.requireNonNull(windowAnimator);
                consumer = new Consumer() { // from class: com.android.server.wm.TaskOrganizerController$TaskOrganizerState$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WindowAnimator.this.addAfterPrepareSurfacesRunnable((Runnable) obj);
                    }
                };
            }
            this.mOrganizer = new TaskOrganizerCallbacks(iTaskOrganizer, consumer);
            DeathRecipient deathRecipient = new DeathRecipient(iTaskOrganizer);
            this.mDeathRecipient = deathRecipient;
            this.mPendingEventsQueue = new TaskOrganizerPendingEventsQueue(this);
            try {
                iTaskOrganizer.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException unused) {
                Slog.e("TaskOrganizerController", "TaskOrganizer failed to register death recipient");
            }
            this.mUid = i;
        }

        @VisibleForTesting
        public DeathRecipient getDeathRecipient() {
            return this.mDeathRecipient;
        }

        @VisibleForTesting
        public TaskOrganizerPendingEventsQueue getPendingEventsQueue() {
            return this.mPendingEventsQueue;
        }

        public SurfaceControl addTaskWithoutCallback(Task task, String str) {
            task.mTaskAppearedSent = true;
            if (!this.mOrganizedTasks.contains(task)) {
                this.mOrganizedTasks.add(task);
            }
            return this.mOrganizer.prepareLeash(task, str);
        }

        public final boolean addTask(Task task) {
            if (task.mTaskAppearedSent) {
                return false;
            }
            if (!this.mOrganizedTasks.contains(task)) {
                this.mOrganizedTasks.add(task);
            }
            if (task.taskAppearedReady()) {
                task.mTaskAppearedSent = true;
                return true;
            }
            return false;
        }

        public final boolean removeTask(Task task, boolean z) {
            this.mOrganizedTasks.remove(task);
            TaskOrganizerController.this.mInterceptBackPressedOnRootTasks.remove(Integer.valueOf(task.mTaskId));
            boolean z2 = task.mTaskAppearedSent;
            if (z2) {
                if (task.getSurfaceControl() != null) {
                    task.migrateToNewSurfaceControl(task.getSyncTransaction());
                }
                task.mTaskAppearedSent = false;
            }
            if (z) {
                TaskOrganizerController.this.mService.removeTask(task.mTaskId);
            }
            return z2;
        }

        public void dispose() {
            TaskOrganizerController.this.mTaskOrganizers.remove(this.mOrganizer.mTaskOrganizer);
            while (!this.mOrganizedTasks.isEmpty()) {
                Task task = this.mOrganizedTasks.get(0);
                if (task.mCreatedByOrganizer) {
                    task.removeImmediately();
                } else {
                    task.updateTaskOrganizerState();
                }
                if (this.mOrganizedTasks.contains(task) && removeTask(task, task.mRemoveWithTaskOrganizer)) {
                    TaskOrganizerController.this.onTaskVanishedInternal(this, task);
                }
                if (TaskOrganizerController.this.mService.getTransitionController().isShellTransitionsEnabled() && task.mTaskOrganizer != null && task.getSurfaceControl() != null) {
                    task.getSyncTransaction().show(task.getSurfaceControl());
                }
            }
            this.mPendingEventsQueue.clearPendingTaskEvents();
            TaskOrganizerController.this.mTaskOrganizerStates.remove(this.mOrganizer.getBinder());
        }

        public void unlinkDeath() {
            this.mOrganizer.getBinder().unlinkToDeath(this.mDeathRecipient, 0);
        }
    }

    /* renamed from: com.android.server.wm.TaskOrganizerController$PendingTaskEvent */
    /* loaded from: classes2.dex */
    public static class PendingTaskEvent {
        public final int mEventType;
        public boolean mForce;
        public final Task mTask;
        public final ITaskOrganizer mTaskOrg;

        public PendingTaskEvent(Task task, int i) {
            this(task, task.mTaskOrganizer, i);
        }

        public PendingTaskEvent(Task task, ITaskOrganizer iTaskOrganizer, int i) {
            this.mTask = task;
            this.mTaskOrg = iTaskOrganizer;
            this.mEventType = i;
        }

        public boolean isLifecycleEvent() {
            int i = this.mEventType;
            return i == 0 || i == 1 || i == 2;
        }
    }

    public TaskOrganizerController(ActivityTaskManagerService activityTaskManagerService) {
        this.mService = activityTaskManagerService;
        this.mGlobalLock = activityTaskManagerService.mGlobalLock;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            throw ActivityTaskManagerService.logAndRethrowRuntimeExceptionOnTransact("TaskOrganizerController", e);
        }
    }

    @VisibleForTesting
    public void setDeferTaskOrgCallbacksConsumer(Consumer<Runnable> consumer) {
        this.mDeferTaskOrgCallbacksConsumer = consumer;
    }

    public ParceledListSlice<TaskAppearedInfo> registerTaskOrganizer(final ITaskOrganizer iTaskOrganizer) {
        ActivityTaskManagerService.enforceTaskPermission("registerTaskOrganizer()");
        final int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            final ArrayList arrayList = new ArrayList();
            Runnable runnable = new Runnable() { // from class: com.android.server.wm.TaskOrganizerController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizerController.this.lambda$registerTaskOrganizer$1(iTaskOrganizer, callingUid, arrayList);
                }
            };
            if (this.mService.getTransitionController().isShellTransitionsEnabled()) {
                this.mService.getTransitionController().mRunningLock.runWhenIdle(1000L, runnable);
            } else {
                synchronized (this.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    runnable.run();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
            return new ParceledListSlice<>(arrayList);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerTaskOrganizer$1(ITaskOrganizer iTaskOrganizer, int i, final ArrayList arrayList) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1792633344, 4, (String) null, new Object[]{String.valueOf(iTaskOrganizer.asBinder()), Long.valueOf(i)});
        }
        if (!this.mTaskOrganizerStates.containsKey(iTaskOrganizer.asBinder())) {
            this.mTaskOrganizers.add(iTaskOrganizer);
            this.mTaskOrganizerStates.put(iTaskOrganizer.asBinder(), new TaskOrganizerState(iTaskOrganizer, i));
        }
        final TaskOrganizerState taskOrganizerState = this.mTaskOrganizerStates.get(iTaskOrganizer.asBinder());
        this.mService.mRootWindowContainer.forAllTasks(new Consumer() { // from class: com.android.server.wm.TaskOrganizerController$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TaskOrganizerController.lambda$registerTaskOrganizer$0(TaskOrganizerController.TaskOrganizerState.this, arrayList, (Task) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$registerTaskOrganizer$0(TaskOrganizerState taskOrganizerState, ArrayList arrayList, Task task) {
        boolean z = !task.mCreatedByOrganizer;
        task.updateTaskOrganizerState(z);
        if (task.isOrganized() && z) {
            arrayList.add(new TaskAppearedInfo(task.getTaskInfo(), taskOrganizerState.addTaskWithoutCallback(task, "TaskOrganizerController.registerTaskOrganizer")));
        }
    }

    public void unregisterTaskOrganizer(final ITaskOrganizer iTaskOrganizer) {
        ActivityTaskManagerService.enforceTaskPermission("unregisterTaskOrganizer()");
        final int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Runnable runnable = new Runnable() { // from class: com.android.server.wm.TaskOrganizerController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TaskOrganizerController.this.lambda$unregisterTaskOrganizer$2(iTaskOrganizer, callingUid);
                }
            };
            if (this.mService.getTransitionController().isShellTransitionsEnabled()) {
                this.mService.getTransitionController().mRunningLock.runWhenIdle(1000L, runnable);
            } else {
                synchronized (this.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    runnable.run();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterTaskOrganizer$2(ITaskOrganizer iTaskOrganizer, int i) {
        TaskOrganizerState taskOrganizerState = this.mTaskOrganizerStates.get(iTaskOrganizer.asBinder());
        if (taskOrganizerState == null) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -951939129, 4, (String) null, new Object[]{String.valueOf(iTaskOrganizer.asBinder()), Long.valueOf(i)});
        }
        taskOrganizerState.unlinkDeath();
        taskOrganizerState.dispose();
    }

    public ITaskOrganizer getTaskOrganizer() {
        return this.mTaskOrganizers.peekLast();
    }

    /* renamed from: com.android.server.wm.TaskOrganizerController$StartingWindowAnimationAdaptor */
    /* loaded from: classes2.dex */
    public static class StartingWindowAnimationAdaptor implements AnimationAdapter {
        public SurfaceControl mAnimationLeash;

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

        @Override // com.android.server.p014wm.AnimationAdapter
        public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
            this.mAnimationLeash = surfaceControl;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void onAnimationCancelled(SurfaceControl surfaceControl) {
            if (this.mAnimationLeash == surfaceControl) {
                this.mAnimationLeash = null;
            }
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str + "StartingWindowAnimationAdaptor mCapturedLeash=");
            printWriter.print(this.mAnimationLeash);
            printWriter.println();
        }
    }

    public static SurfaceControl applyStartingWindowAnimation(WindowState windowState) {
        SurfaceControl.Transaction pendingTransaction = windowState.getPendingTransaction();
        Rect relativeFrame = windowState.getRelativeFrame();
        StartingWindowAnimationAdaptor startingWindowAnimationAdaptor = new StartingWindowAnimationAdaptor();
        windowState.startAnimation(pendingTransaction, startingWindowAnimationAdaptor, false, 128);
        SurfaceControl surfaceControl = startingWindowAnimationAdaptor.mAnimationLeash;
        if (surfaceControl == null) {
            Slog.e("TaskOrganizerController", "Cannot start starting window animation, the window " + windowState + " was removed");
            return null;
        }
        pendingTransaction.setPosition(surfaceControl, relativeFrame.left, relativeFrame.top);
        return startingWindowAnimationAdaptor.mAnimationLeash;
    }

    public boolean addStartingWindow(Task task, ActivityRecord activityRecord, int i, TaskSnapshot taskSnapshot) {
        ITaskOrganizer peekLast;
        if (task.getRootTask() == null || activityRecord.mStartingData == null || (peekLast = this.mTaskOrganizers.peekLast()) == null) {
            return false;
        }
        StartingWindowInfo startingWindowInfo = task.getStartingWindowInfo(activityRecord);
        if (i != 0) {
            startingWindowInfo.splashScreenThemeResId = i;
        }
        startingWindowInfo.taskSnapshot = taskSnapshot;
        startingWindowInfo.appToken = activityRecord.token;
        try {
            peekLast.addStartingWindow(startingWindowInfo);
            return true;
        } catch (RemoteException e) {
            Slog.e("TaskOrganizerController", "Exception sending onTaskStart callback", e);
            return false;
        }
    }

    public void removeStartingWindow(Task task, boolean z) {
        ITaskOrganizer peekLast;
        if (task.getRootTask() == null || (peekLast = this.mTaskOrganizers.peekLast()) == null) {
            return;
        }
        StartingWindowRemovalInfo startingWindowRemovalInfo = new StartingWindowRemovalInfo();
        startingWindowRemovalInfo.taskId = task.mTaskId;
        startingWindowRemovalInfo.playRevealAnimation = z && task.getDisplayInfo().state == 2;
        boolean z2 = !task.inMultiWindowMode();
        ActivityRecord activityRecord = task.topActivityContainsStartingWindow();
        if (activityRecord != null) {
            startingWindowRemovalInfo.deferRemoveForIme = activityRecord.mDisplayContent.mayImeShowOnLaunchingActivity(activityRecord);
            WindowState findMainWindow = activityRecord.findMainWindow(false);
            if (findMainWindow == null || findMainWindow.mRemoved) {
                startingWindowRemovalInfo.playRevealAnimation = false;
            } else if (startingWindowRemovalInfo.playRevealAnimation && z2) {
                startingWindowRemovalInfo.roundedCornerRadius = activityRecord.mLetterboxUiController.getRoundedCornersRadius(findMainWindow);
                startingWindowRemovalInfo.windowAnimationLeash = applyStartingWindowAnimation(findMainWindow);
                startingWindowRemovalInfo.mainFrame = findMainWindow.getRelativeFrame();
            }
        }
        try {
            peekLast.removeStartingWindow(startingWindowRemovalInfo);
        } catch (RemoteException e) {
            Slog.e("TaskOrganizerController", "Exception sending onStartTaskFinished callback", e);
        }
    }

    public boolean copySplashScreenView(Task task) {
        ITaskOrganizer peekLast;
        if (task.getRootTask() == null || (peekLast = this.mTaskOrganizers.peekLast()) == null) {
            return false;
        }
        try {
            peekLast.copySplashScreenView(task.mTaskId);
            return true;
        } catch (RemoteException e) {
            Slog.e("TaskOrganizerController", "Exception sending copyStartingWindowView callback", e);
            return false;
        }
    }

    public boolean isSupportWindowlessStartingSurface() {
        this.mTaskOrganizers.peekLast();
        return false;
    }

    public void onAppSplashScreenViewRemoved(Task task) {
        ITaskOrganizer peekLast;
        if (task.getRootTask() == null || (peekLast = this.mTaskOrganizers.peekLast()) == null) {
            return;
        }
        try {
            peekLast.onAppSplashScreenViewRemoved(task.mTaskId);
        } catch (RemoteException e) {
            Slog.e("TaskOrganizerController", "Exception sending onAppSplashScreenViewRemoved callback", e);
        }
    }

    public void onTaskAppeared(ITaskOrganizer iTaskOrganizer, Task task) {
        TaskOrganizerState taskOrganizerState = this.mTaskOrganizerStates.get(iTaskOrganizer.asBinder());
        if (taskOrganizerState == null || !taskOrganizerState.addTask(task)) {
            return;
        }
        TaskOrganizerPendingEventsQueue taskOrganizerPendingEventsQueue = taskOrganizerState.mPendingEventsQueue;
        if (taskOrganizerPendingEventsQueue.getPendingTaskEvent(task, 0) == null) {
            taskOrganizerPendingEventsQueue.addPendingTaskEvent(new PendingTaskEvent(task, 0));
        }
    }

    public void onTaskVanished(ITaskOrganizer iTaskOrganizer, Task task) {
        TaskOrganizerState taskOrganizerState = this.mTaskOrganizerStates.get(iTaskOrganizer.asBinder());
        if (taskOrganizerState == null || !taskOrganizerState.removeTask(task, task.mRemoveWithTaskOrganizer)) {
            return;
        }
        onTaskVanishedInternal(taskOrganizerState, task);
    }

    public final void onTaskVanishedInternal(TaskOrganizerState taskOrganizerState, Task task) {
        if (taskOrganizerState == null) {
            Slog.i("TaskOrganizerController", "cannot send onTaskVanished because organizer state is not present for this organizer");
            return;
        }
        TaskOrganizerPendingEventsQueue taskOrganizerPendingEventsQueue = taskOrganizerState.mPendingEventsQueue;
        if (taskOrganizerPendingEventsQueue.removePendingTaskEvents(task)) {
            return;
        }
        taskOrganizerPendingEventsQueue.addPendingTaskEvent(new PendingTaskEvent(task, taskOrganizerState.mOrganizer.mTaskOrganizer, 1));
    }

    public void createRootTask(int i, int i2, IBinder iBinder, boolean z) {
        ActivityTaskManagerService.enforceTaskPermission("createRootTask()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = this.mService.mRootWindowContainer.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                        ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1396893178, 1, (String) null, new Object[]{Long.valueOf(i)});
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                createRootTask(displayContent, i2, iBinder, z);
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public Task createRootTask(DisplayContent displayContent, int i, IBinder iBinder) {
        return createRootTask(displayContent, i, iBinder, false);
    }

    public Task createRootTask(DisplayContent displayContent, int i, IBinder iBinder, boolean z) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1939861963, 5, (String) null, new Object[]{Long.valueOf(displayContent.mDisplayId), Long.valueOf(i)});
        }
        Task build = new Task.Builder(this.mService).setWindowingMode(i).setIntent(new Intent()).setCreatedByOrganizer(true).setDeferTaskAppear(true).setLaunchCookie(iBinder).setParent(displayContent.getDefaultTaskDisplayArea()).setRemoveWithTaskOrganizer(z).build();
        build.setDeferTaskAppear(false);
        return build;
    }

    public boolean deleteRootTask(WindowContainerToken windowContainerToken) {
        ActivityTaskManagerService.enforceTaskPermission("deleteRootTask()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder != null) {
                    Task asTask = fromBinder.asTask();
                    if (asTask != null) {
                        if (!asTask.mCreatedByOrganizer) {
                            throw new IllegalArgumentException("Attempt to delete task not created by organizer task=" + asTask);
                        }
                        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1895337367, 5, (String) null, new Object[]{Long.valueOf(asTask.getDisplayId()), Long.valueOf(asTask.getWindowingMode())});
                        }
                        asTask.remove(true, "deleteRootTask");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dispatchPendingEvents() {
        if (this.mService.mWindowManager.mWindowPlacerLocked.isLayoutDeferred()) {
            return;
        }
        for (int i = 0; i < this.mTaskOrganizerStates.size(); i++) {
            this.mTaskOrganizerStates.valueAt(i).mPendingEventsQueue.dispatchPendingEvents();
        }
    }

    public void reportImeDrawnOnTask(Task task) {
        TaskOrganizerState taskOrganizerState = this.mTaskOrganizerStates.get(task.mTaskOrganizer.asBinder());
        if (taskOrganizerState != null) {
            try {
                taskOrganizerState.mOrganizer.mTaskOrganizer.onImeDrawnOnTask(task.mTaskId);
            } catch (RemoteException e) {
                Slog.e("TaskOrganizerController", "Exception sending onImeDrawnOnTask callback", e);
            }
        }
    }

    public void onTaskInfoChanged(Task task, boolean z) {
        if (task.mTaskAppearedSent) {
            TaskOrganizerPendingEventsQueue taskOrganizerPendingEventsQueue = this.mTaskOrganizerStates.get(task.mTaskOrganizer.asBinder()).mPendingEventsQueue;
            if (taskOrganizerPendingEventsQueue == null) {
                Slog.i("TaskOrganizerController", "cannot send onTaskInfoChanged because pending events queue is not present for this organizer");
            } else if (z && taskOrganizerPendingEventsQueue.numPendingTaskEvents() == 0) {
                taskOrganizerPendingEventsQueue.dispatchTaskInfoChanged(task, true);
            } else {
                PendingTaskEvent pendingLifecycleTaskEvent = taskOrganizerPendingEventsQueue.getPendingLifecycleTaskEvent(task);
                if (pendingLifecycleTaskEvent == null) {
                    pendingLifecycleTaskEvent = new PendingTaskEvent(task, 2);
                } else if (pendingLifecycleTaskEvent.mEventType != 2) {
                    return;
                } else {
                    taskOrganizerPendingEventsQueue.removePendingTaskEvent(pendingLifecycleTaskEvent);
                }
                pendingLifecycleTaskEvent.mForce |= z;
                taskOrganizerPendingEventsQueue.addPendingTaskEvent(pendingLifecycleTaskEvent);
            }
        }
    }

    public WindowContainerToken getImeTarget(int i) {
        ActivityTaskManagerService.enforceTaskPermission("getImeTarget()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = this.mService.mWindowManager.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    InsetsControlTarget imeTarget = displayContent.getImeTarget(0);
                    if (imeTarget != null && imeTarget.getWindow() != null) {
                        Task task = imeTarget.getWindow().getTask();
                        if (task != null) {
                            WindowContainerToken windowContainerToken = task.mRemoteToken.toWindowContainerToken();
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return windowContainerToken;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return null;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<ActivityManager.RunningTaskInfo> getChildTasks(WindowContainerToken windowContainerToken, int[] iArr) {
        ActivityTaskManagerService.enforceTaskPermission("getChildTasks()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (windowContainerToken == null) {
                    throw new IllegalArgumentException("Can't get children of null parent");
                }
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder == null) {
                    Slog.e("TaskOrganizerController", "Can't get children of " + windowContainerToken + " because it is not valid.");
                } else {
                    Task asTask = fromBinder.asTask();
                    if (asTask == null) {
                        Slog.e("TaskOrganizerController", fromBinder + " is not a task...");
                    } else if (!asTask.mCreatedByOrganizer) {
                        Slog.w("TaskOrganizerController", "Can only get children of root tasks created via createRootTask");
                    } else {
                        ArrayList arrayList = new ArrayList();
                        for (int childCount = asTask.getChildCount() - 1; childCount >= 0; childCount--) {
                            Task asTask2 = asTask.getChildAt(childCount).asTask();
                            if (asTask2 != null && (iArr == null || ArrayUtils.contains(iArr, asTask2.getActivityType()))) {
                                arrayList.add(asTask2.getTaskInfo());
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return arrayList;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return null;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<ActivityManager.RunningTaskInfo> getRootTasks(int i, final int[] iArr) {
        final ArrayList arrayList;
        ActivityTaskManagerService.enforceTaskPermission("getRootTasks()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = this.mService.mRootWindowContainer.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Display " + i + " doesn't exist");
                }
                arrayList = new ArrayList();
                displayContent.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.TaskOrganizerController$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        TaskOrganizerController.lambda$getRootTasks$3(iArr, arrayList, (Task) obj);
                    }
                });
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return arrayList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$getRootTasks$3(int[] iArr, ArrayList arrayList, Task task) {
        if (iArr == null || ArrayUtils.contains(iArr, task.getActivityType())) {
            arrayList.add(task.getTaskInfo());
        }
    }

    public void setInterceptBackPressedOnTaskRoot(WindowContainerToken windowContainerToken, boolean z) {
        ActivityTaskManagerService.enforceTaskPermission("setInterceptBackPressedOnTaskRoot()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 232317536, 3, (String) null, new Object[]{Boolean.valueOf(z)});
                }
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder == null) {
                    Slog.w("TaskOrganizerController", "Could not resolve window from token");
                } else {
                    Task asTask = fromBinder.asTask();
                    if (asTask != null) {
                        if (z) {
                            this.mInterceptBackPressedOnRootTasks.add(Integer.valueOf(asTask.mTaskId));
                        } else {
                            this.mInterceptBackPressedOnRootTasks.remove(Integer.valueOf(asTask.mTaskId));
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    Slog.w("TaskOrganizerController", "Could not resolve task from token");
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void restartTaskTopActivityProcessIfVisible(WindowContainerToken windowContainerToken) {
        ActivityTaskManagerService.enforceTaskPermission("restartTopActivityProcessIfVisible()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder == null) {
                    Slog.w("TaskOrganizerController", "Could not resolve window from token");
                } else {
                    Task asTask = fromBinder.asTask();
                    if (asTask == null) {
                        Slog.w("TaskOrganizerController", "Could not resolve task from token");
                    } else {
                        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1963363332, 1, (String) null, new Object[]{Long.valueOf(asTask.mTaskId)});
                        }
                        ActivityRecord topNonFinishingActivity = asTask.getTopNonFinishingActivity();
                        if (topNonFinishingActivity != null) {
                            topNonFinishingActivity.restartProcessIfVisible();
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateCameraCompatControlState(WindowContainerToken windowContainerToken, int i) {
        ActivityTaskManagerService.enforceTaskPermission("updateCameraCompatControlState()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder == null) {
                    Slog.w("TaskOrganizerController", "Could not resolve window from token");
                } else {
                    Task asTask = fromBinder.asTask();
                    if (asTask == null) {
                        Slog.w("TaskOrganizerController", "Could not resolve task from token");
                    } else {
                        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -846931068, 4, (String) null, new Object[]{String.valueOf(TaskInfo.cameraCompatControlStateToString(i)), Long.valueOf(asTask.mTaskId)});
                        }
                        ActivityRecord topNonFinishingActivity = asTask.getTopNonFinishingActivity();
                        if (topNonFinishingActivity != null) {
                            topNonFinishingActivity.updateCameraCompatStateFromUser(i);
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setIsIgnoreOrientationRequestDisabled(boolean z) {
        ActivityTaskManagerService.enforceTaskPermission("setIsIgnoreOrientationRequestDisabled()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.mWindowManager.setIsIgnoreOrientationRequestDisabled(z);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean handleInterceptBackPressedOnTaskRoot(Task task) {
        if (task != null && task.isOrganized() && this.mInterceptBackPressedOnRootTasks.contains(Integer.valueOf(task.mTaskId))) {
            TaskOrganizerPendingEventsQueue taskOrganizerPendingEventsQueue = this.mTaskOrganizerStates.get(task.mTaskOrganizer.asBinder()).mPendingEventsQueue;
            if (taskOrganizerPendingEventsQueue == null) {
                Slog.w("TaskOrganizerController", "cannot get handle BackPressedOnTaskRoot because organizerState is not present");
                return false;
            } else if (taskOrganizerPendingEventsQueue.getPendingTaskEvent(task, 1) != null) {
                return false;
            } else {
                PendingTaskEvent pendingTaskEvent = taskOrganizerPendingEventsQueue.getPendingTaskEvent(task, 3);
                if (pendingTaskEvent == null) {
                    pendingTaskEvent = new PendingTaskEvent(task, 3);
                } else {
                    taskOrganizerPendingEventsQueue.removePendingTaskEvent(pendingTaskEvent);
                }
                taskOrganizerPendingEventsQueue.addPendingTaskEvent(pendingTaskEvent);
                this.mService.mWindowManager.mWindowPlacerLocked.requestTraversal();
                return true;
            }
        }
        return false;
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.println("TaskOrganizerController:");
        for (TaskOrganizerState taskOrganizerState : this.mTaskOrganizerStates.values()) {
            ArrayList arrayList = taskOrganizerState.mOrganizedTasks;
            printWriter.print(str2 + "  ");
            printWriter.println(taskOrganizerState.mOrganizer.mTaskOrganizer + " uid=" + taskOrganizerState.mUid + XmlUtils.STRING_ARRAY_SEPARATOR);
            for (int i = 0; i < arrayList.size(); i++) {
                Task task = (Task) arrayList.get(i);
                int windowingMode = task.getWindowingMode();
                printWriter.println(str2 + "    (" + WindowConfiguration.windowingModeToString(windowingMode) + ") " + task);
            }
        }
        printWriter.println();
    }

    @VisibleForTesting
    public TaskOrganizerState getTaskOrganizerState(IBinder iBinder) {
        return this.mTaskOrganizerStates.get(iBinder);
    }

    @VisibleForTesting
    public TaskOrganizerPendingEventsQueue getTaskOrganizerPendingEvents(IBinder iBinder) {
        return this.mTaskOrganizerStates.get(iBinder).mPendingEventsQueue;
    }
}
