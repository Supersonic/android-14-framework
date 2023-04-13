package com.android.server.p014wm;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.RemoteAnimationDefinition;
import android.window.ITaskFragmentOrganizer;
import android.window.ITaskFragmentOrganizerController;
import android.window.TaskFragmentInfo;
import android.window.TaskFragmentOrganizer;
import android.window.TaskFragmentParentInfo;
import android.window.TaskFragmentTransaction;
import android.window.WindowContainerTransaction;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.TaskFragmentOrganizerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.TaskFragmentOrganizerController */
/* loaded from: classes2.dex */
public class TaskFragmentOrganizerController extends ITaskFragmentOrganizerController.Stub {
    public final ActivityTaskManagerService mAtmService;
    public final WindowManagerGlobalLock mGlobalLock;
    public final WindowOrganizerController mWindowOrganizerController;
    public final ArrayMap<IBinder, TaskFragmentOrganizerState> mTaskFragmentOrganizerState = new ArrayMap<>();
    public final ArrayMap<IBinder, List<PendingTaskFragmentEvent>> mPendingTaskFragmentEvents = new ArrayMap<>();
    public final ArraySet<Task> mTmpTaskSet = new ArraySet<>();

    public TaskFragmentOrganizerController(ActivityTaskManagerService activityTaskManagerService, WindowOrganizerController windowOrganizerController) {
        Objects.requireNonNull(activityTaskManagerService);
        this.mAtmService = activityTaskManagerService;
        this.mGlobalLock = activityTaskManagerService.mGlobalLock;
        Objects.requireNonNull(windowOrganizerController);
        this.mWindowOrganizerController = windowOrganizerController;
    }

    /* renamed from: com.android.server.wm.TaskFragmentOrganizerController$TaskFragmentOrganizerState */
    /* loaded from: classes2.dex */
    public class TaskFragmentOrganizerState implements IBinder.DeathRecipient {
        public final ITaskFragmentOrganizer mOrganizer;
        public final int mOrganizerPid;
        public final int mOrganizerUid;
        public RemoteAnimationDefinition mRemoteAnimationDefinition;
        public final ArrayList<TaskFragment> mOrganizedTaskFragments = new ArrayList<>();
        public final Map<TaskFragment, TaskFragmentInfo> mLastSentTaskFragmentInfos = new WeakHashMap();
        public final Map<TaskFragment, Integer> mTaskFragmentTaskIds = new WeakHashMap();
        public final SparseArray<TaskFragmentParentInfo> mLastSentTaskFragmentParentInfos = new SparseArray<>();
        public final Map<IBinder, ActivityRecord> mTemporaryActivityTokens = new WeakHashMap();
        public final ArrayMap<IBinder, Integer> mDeferredTransitions = new ArrayMap<>();

        public TaskFragmentOrganizerState(ITaskFragmentOrganizer iTaskFragmentOrganizer, int i, int i2) {
            this.mOrganizer = iTaskFragmentOrganizer;
            this.mOrganizerPid = i;
            this.mOrganizerUid = i2;
            try {
                iTaskFragmentOrganizer.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
                Slog.e("TaskFragmentOrganizerController", "TaskFragmentOrganizer failed to register death recipient");
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (TaskFragmentOrganizerController.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    TaskFragmentOrganizerController.this.removeOrganizer(this.mOrganizer);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean addTaskFragment(TaskFragment taskFragment) {
            if (taskFragment.mTaskFragmentAppearedSent || this.mOrganizedTaskFragments.contains(taskFragment)) {
                return false;
            }
            this.mOrganizedTaskFragments.add(taskFragment);
            return true;
        }

        public void removeTaskFragment(TaskFragment taskFragment) {
            this.mOrganizedTaskFragments.remove(taskFragment);
        }

        public void dispose() {
            for (int size = this.mOrganizedTaskFragments.size() - 1; size >= 0; size--) {
                this.mOrganizedTaskFragments.get(size).onTaskFragmentOrganizerRemoved();
            }
            TaskFragmentOrganizerController.this.mAtmService.deferWindowLayout();
            while (!this.mOrganizedTaskFragments.isEmpty()) {
                try {
                    this.mOrganizedTaskFragments.remove(0).removeImmediately();
                } catch (Throwable th) {
                    TaskFragmentOrganizerController.this.mAtmService.continueWindowLayout();
                    throw th;
                }
            }
            TaskFragmentOrganizerController.this.mAtmService.continueWindowLayout();
            for (int size2 = this.mDeferredTransitions.size() - 1; size2 >= 0; size2--) {
                onTransactionFinished(this.mDeferredTransitions.keyAt(size2));
            }
            this.mOrganizer.asBinder().unlinkToDeath(this, 0);
        }

        public TaskFragmentTransaction.Change prepareTaskFragmentAppeared(TaskFragment taskFragment) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1284122013, 0, (String) null, new Object[]{String.valueOf(taskFragment.getName())});
            }
            TaskFragmentInfo taskFragmentInfo = taskFragment.getTaskFragmentInfo();
            int i = taskFragment.getTask().mTaskId;
            taskFragment.mTaskFragmentAppearedSent = true;
            this.mLastSentTaskFragmentInfos.put(taskFragment, taskFragmentInfo);
            this.mTaskFragmentTaskIds.put(taskFragment, Integer.valueOf(i));
            return new TaskFragmentTransaction.Change(1).setTaskFragmentToken(taskFragment.getFragmentToken()).setTaskFragmentInfo(taskFragmentInfo).setTaskId(i);
        }

        public TaskFragmentTransaction.Change prepareTaskFragmentVanished(TaskFragment taskFragment) {
            int i;
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -542756093, 0, (String) null, new Object[]{String.valueOf(taskFragment.getName())});
            }
            taskFragment.mTaskFragmentAppearedSent = false;
            this.mLastSentTaskFragmentInfos.remove(taskFragment);
            if (this.mTaskFragmentTaskIds.containsKey(taskFragment)) {
                i = this.mTaskFragmentTaskIds.remove(taskFragment).intValue();
                if (!this.mTaskFragmentTaskIds.containsValue(Integer.valueOf(i))) {
                    this.mLastSentTaskFragmentParentInfos.remove(i);
                }
            } else {
                i = -1;
            }
            return new TaskFragmentTransaction.Change(3).setTaskFragmentToken(taskFragment.getFragmentToken()).setTaskFragmentInfo(taskFragment.getTaskFragmentInfo()).setTaskId(i);
        }

        public TaskFragmentTransaction.Change prepareTaskFragmentInfoChanged(TaskFragment taskFragment) {
            TaskFragmentInfo taskFragmentInfo = taskFragment.getTaskFragmentInfo();
            TaskFragmentInfo taskFragmentInfo2 = this.mLastSentTaskFragmentInfos.get(taskFragment);
            if (taskFragmentInfo.equalsForTaskFragmentOrganizer(taskFragmentInfo2) && WindowOrganizerController.configurationsAreEqualForOrganizer(taskFragmentInfo.getConfiguration(), taskFragmentInfo2.getConfiguration())) {
                return null;
            }
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1022095595, 0, (String) null, new Object[]{String.valueOf(taskFragment.getName())});
            }
            this.mLastSentTaskFragmentInfos.put(taskFragment, taskFragmentInfo);
            return new TaskFragmentTransaction.Change(2).setTaskFragmentToken(taskFragment.getFragmentToken()).setTaskFragmentInfo(taskFragmentInfo).setTaskId(taskFragment.getTask().mTaskId);
        }

        public TaskFragmentTransaction.Change prepareTaskFragmentParentInfoChanged(Task task) {
            int i = task.mTaskId;
            TaskFragmentParentInfo taskFragmentParentInfo = task.getTaskFragmentParentInfo();
            TaskFragmentParentInfo taskFragmentParentInfo2 = this.mLastSentTaskFragmentParentInfos.get(i);
            Configuration configuration = taskFragmentParentInfo2 != null ? taskFragmentParentInfo2.getConfiguration() : null;
            if (taskFragmentParentInfo.equalsForTaskFragmentOrganizer(taskFragmentParentInfo2) && WindowOrganizerController.configurationsAreEqualForOrganizer(taskFragmentParentInfo.getConfiguration(), configuration)) {
                return null;
            }
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -706481945, 4, (String) null, new Object[]{String.valueOf(task.getName()), Long.valueOf(i)});
            }
            this.mLastSentTaskFragmentParentInfos.put(i, new TaskFragmentParentInfo(taskFragmentParentInfo));
            return new TaskFragmentTransaction.Change(4).setTaskId(i).setTaskFragmentParentInfo(taskFragmentParentInfo);
        }

        public TaskFragmentTransaction.Change prepareTaskFragmentError(IBinder iBinder, TaskFragment taskFragment, int i, Throwable th) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 743418423, 0, (String) null, new Object[]{String.valueOf(th.toString())});
            }
            return new TaskFragmentTransaction.Change(5).setErrorCallbackToken(iBinder).setErrorBundle(TaskFragmentOrganizer.putErrorInfoInBundle(th, taskFragment != null ? taskFragment.getTaskFragmentInfo() : null, i));
        }

        public TaskFragmentTransaction.Change prepareActivityReparentedToTask(ActivityRecord activityRecord) {
            IBinder iBinder;
            if (activityRecord.finishing) {
                Slog.d("TaskFragmentOrganizerController", "Reparent activity=" + activityRecord.token + " is finishing");
                return null;
            }
            Task task = activityRecord.getTask();
            if (task != null) {
                int i = task.effectiveUid;
                int i2 = this.mOrganizerUid;
                if (i == i2) {
                    if (task.isAllowedToEmbedActivity(activityRecord, i2) != 0 || !task.isAllowedToEmbedActivityInTrustedMode(activityRecord, this.mOrganizerUid)) {
                        Slog.d("TaskFragmentOrganizerController", "Reparent activity=" + activityRecord.token + " is not allowed to be embedded in trusted mode.");
                        return null;
                    }
                    if (activityRecord.getPid() == this.mOrganizerPid) {
                        iBinder = activityRecord.token;
                    } else {
                        final Binder binder = new Binder("TemporaryActivityToken");
                        this.mTemporaryActivityTokens.put(binder, activityRecord);
                        TaskFragmentOrganizerController.this.mAtmService.mWindowManager.f1164mH.postDelayed(new Runnable() { // from class: com.android.server.wm.TaskFragmentOrganizerController$TaskFragmentOrganizerState$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                TaskFragmentOrganizerController.TaskFragmentOrganizerState.this.lambda$prepareActivityReparentedToTask$0(binder);
                            }
                        }, 5000L);
                        iBinder = binder;
                    }
                    if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 873160948, 4, (String) null, new Object[]{String.valueOf(activityRecord.token), Long.valueOf(task.mTaskId)});
                    }
                    return new TaskFragmentTransaction.Change(6).setTaskId(task.mTaskId).setActivityIntent(TaskFragmentOrganizerController.trimIntent(activityRecord.intent)).setActivityToken(iBinder);
                }
            }
            Slog.d("TaskFragmentOrganizerController", "Reparent activity=" + activityRecord.token + " is not in a task belong to the organizer app.");
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$prepareActivityReparentedToTask$0(IBinder iBinder) {
            synchronized (TaskFragmentOrganizerController.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mTemporaryActivityTokens.remove(iBinder);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void dispatchTransaction(TaskFragmentTransaction taskFragmentTransaction) {
            if (taskFragmentTransaction.isEmpty()) {
                return;
            }
            try {
                this.mOrganizer.onTransactionReady(taskFragmentTransaction);
                onTransactionStarted(taskFragmentTransaction.getTransactionToken());
            } catch (RemoteException e) {
                Slog.d("TaskFragmentOrganizerController", "Exception sending TaskFragmentTransaction", e);
            }
        }

        public void onTransactionStarted(IBinder iBinder) {
            if (TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().isCollecting()) {
                int collectingTransitionId = TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().getCollectingTransitionId();
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1046228706, 1, (String) null, new Object[]{Long.valueOf(collectingTransitionId), String.valueOf(iBinder)});
                }
                this.mDeferredTransitions.put(iBinder, Integer.valueOf(collectingTransitionId));
                TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().deferTransitionReady();
            }
        }

        public void onTransactionFinished(IBinder iBinder) {
            if (this.mDeferredTransitions.containsKey(iBinder)) {
                int intValue = this.mDeferredTransitions.remove(iBinder).intValue();
                if (!TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().isCollecting() || TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().getCollectingTransitionId() != intValue) {
                    if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 851368695, 1, (String) null, new Object[]{Long.valueOf(intValue), String.valueOf(iBinder)});
                        return;
                    }
                    return;
                }
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1075460705, 1, (String) null, new Object[]{Long.valueOf(intValue), String.valueOf(iBinder)});
                }
                TaskFragmentOrganizerController.this.mWindowOrganizerController.getTransitionController().continueTransitionReady();
            }
        }
    }

    public ActivityRecord getReparentActivityFromTemporaryToken(ITaskFragmentOrganizer iTaskFragmentOrganizer, IBinder iBinder) {
        TaskFragmentOrganizerState taskFragmentOrganizerState;
        if (iTaskFragmentOrganizer == null || iBinder == null || (taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder())) == null) {
            return null;
        }
        return (ActivityRecord) taskFragmentOrganizerState.mTemporaryActivityTokens.remove(iBinder);
    }

    public void registerOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1653025361, 20, (String) null, new Object[]{String.valueOf(iTaskFragmentOrganizer.asBinder()), Long.valueOf(callingUid), Long.valueOf(callingPid)});
                }
                if (isOrganizerRegistered(iTaskFragmentOrganizer)) {
                    throw new IllegalStateException("Replacing existing organizer currently unsupported");
                }
                this.mTaskFragmentOrganizerState.put(iTaskFragmentOrganizer.asBinder(), new TaskFragmentOrganizerState(iTaskFragmentOrganizer, callingPid, callingUid));
                this.mPendingTaskFragmentEvents.put(iTaskFragmentOrganizer.asBinder(), new ArrayList());
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        int callingPid = Binder.getCallingPid();
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1311436264, 20, (String) null, new Object[]{String.valueOf(iTaskFragmentOrganizer.asBinder()), Long.valueOf(callingUid), Long.valueOf(callingPid)});
                }
                removeOrganizer(iTaskFragmentOrganizer);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerRemoteAnimations(ITaskFragmentOrganizer iTaskFragmentOrganizer, RemoteAnimationDefinition remoteAnimationDefinition) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1210037962, 20, (String) null, new Object[]{String.valueOf(iTaskFragmentOrganizer.asBinder()), Long.valueOf(callingUid), Long.valueOf(callingPid)});
                }
                TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
                if (taskFragmentOrganizerState == null) {
                    throw new IllegalStateException("The organizer hasn't been registered.");
                }
                if (taskFragmentOrganizerState.mRemoteAnimationDefinition != null) {
                    throw new IllegalStateException("The organizer has already registered remote animations=" + taskFragmentOrganizerState.mRemoteAnimationDefinition);
                }
                remoteAnimationDefinition.setCallingPidUid(callingPid, callingUid);
                taskFragmentOrganizerState.mRemoteAnimationDefinition = remoteAnimationDefinition;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterRemoteAnimations(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        int callingPid = Binder.getCallingPid();
        long callingUid = Binder.getCallingUid();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -70719599, 20, (String) null, new Object[]{String.valueOf(iTaskFragmentOrganizer.asBinder()), Long.valueOf(callingUid), Long.valueOf(callingPid)});
                }
                TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
                if (taskFragmentOrganizerState == null) {
                    Slog.e("TaskFragmentOrganizerController", "The organizer hasn't been registered.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                taskFragmentOrganizerState.mRemoteAnimationDefinition = null;
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void onTransactionHandled(IBinder iBinder, WindowContainerTransaction windowContainerTransaction, int i, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (isValidTransaction(windowContainerTransaction)) {
                    applyTransaction(windowContainerTransaction, i, z);
                }
                ITaskFragmentOrganizer taskFragmentOrganizer = windowContainerTransaction.getTaskFragmentOrganizer();
                TaskFragmentOrganizerState taskFragmentOrganizerState = taskFragmentOrganizer != null ? this.mTaskFragmentOrganizerState.get(taskFragmentOrganizer.asBinder()) : null;
                if (taskFragmentOrganizerState != null) {
                    taskFragmentOrganizerState.onTransactionFinished(iBinder);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void applyTransaction(WindowContainerTransaction windowContainerTransaction, int i, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!isValidTransaction(windowContainerTransaction)) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                this.mWindowOrganizerController.applyTaskFragmentTransactionLocked(windowContainerTransaction, i, z);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public RemoteAnimationDefinition getRemoteAnimationDefinition(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
                if (taskFragmentOrganizerState == null) {
                    Slog.e("TaskFragmentOrganizerController", "TaskFragmentOrganizer has been unregistered or died when trying to play animation on its organized windows.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                RemoteAnimationDefinition remoteAnimationDefinition = taskFragmentOrganizerState.mRemoteAnimationDefinition;
                WindowManagerService.resetPriorityAfterLockedSection();
                return remoteAnimationDefinition;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getTaskFragmentOrganizerUid(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        return validateAndGetState(iTaskFragmentOrganizer).mOrganizerUid;
    }

    public void onTaskFragmentAppeared(ITaskFragmentOrganizer iTaskFragmentOrganizer, TaskFragment taskFragment) {
        if (taskFragment.mTaskFragmentVanishedSent) {
            return;
        }
        if (taskFragment.getTask() == null) {
            Slog.w("TaskFragmentOrganizerController", "onTaskFragmentAppeared failed because it is not attached tf=" + taskFragment);
        } else if (validateAndGetState(iTaskFragmentOrganizer).addTaskFragment(taskFragment) && getPendingTaskFragmentEvent(taskFragment, 0) == null) {
            addPendingEvent(new PendingTaskFragmentEvent.Builder(0, iTaskFragmentOrganizer).setTaskFragment(taskFragment).build());
        }
    }

    public void onTaskFragmentInfoChanged(ITaskFragmentOrganizer iTaskFragmentOrganizer, TaskFragment taskFragment) {
        if (taskFragment.mTaskFragmentVanishedSent) {
            return;
        }
        validateAndGetState(iTaskFragmentOrganizer);
        if (taskFragment.mTaskFragmentAppearedSent) {
            PendingTaskFragmentEvent lastPendingLifecycleEvent = getLastPendingLifecycleEvent(taskFragment);
            if (lastPendingLifecycleEvent == null) {
                lastPendingLifecycleEvent = new PendingTaskFragmentEvent.Builder(2, iTaskFragmentOrganizer).setTaskFragment(taskFragment).build();
            } else {
                removePendingEvent(lastPendingLifecycleEvent);
                lastPendingLifecycleEvent.mDeferTime = 0L;
            }
            addPendingEvent(lastPendingLifecycleEvent);
        }
    }

    public void onTaskFragmentVanished(ITaskFragmentOrganizer iTaskFragmentOrganizer, TaskFragment taskFragment) {
        if (taskFragment.mTaskFragmentVanishedSent) {
            return;
        }
        taskFragment.mTaskFragmentVanishedSent = true;
        TaskFragmentOrganizerState validateAndGetState = validateAndGetState(iTaskFragmentOrganizer);
        List<PendingTaskFragmentEvent> list = this.mPendingTaskFragmentEvents.get(iTaskFragmentOrganizer.asBinder());
        for (int size = list.size() - 1; size >= 0; size--) {
            if (taskFragment == list.get(size).mTaskFragment) {
                list.remove(size);
            }
        }
        addPendingEvent(new PendingTaskFragmentEvent.Builder(1, iTaskFragmentOrganizer).setTaskFragment(taskFragment).build());
        validateAndGetState.removeTaskFragment(taskFragment);
        this.mAtmService.mWindowManager.mWindowPlacerLocked.requestTraversal();
    }

    public void onTaskFragmentError(ITaskFragmentOrganizer iTaskFragmentOrganizer, IBinder iBinder, TaskFragment taskFragment, int i, Throwable th) {
        if (taskFragment == null || !taskFragment.mTaskFragmentVanishedSent) {
            validateAndGetState(iTaskFragmentOrganizer);
            Slog.w("TaskFragmentOrganizerController", "onTaskFragmentError ", th);
            addPendingEvent(new PendingTaskFragmentEvent.Builder(4, iTaskFragmentOrganizer).setErrorCallbackToken(iBinder).setTaskFragment(taskFragment).setException(th).setOpType(i).build());
            this.mAtmService.mWindowManager.mWindowPlacerLocked.requestTraversal();
        }
    }

    public void onActivityReparentedToTask(ActivityRecord activityRecord) {
        ITaskFragmentOrganizer iTaskFragmentOrganizer = activityRecord.mLastTaskFragmentOrganizerBeforePip;
        if (iTaskFragmentOrganizer == null) {
            final TaskFragment[] taskFragmentArr = new TaskFragment[1];
            activityRecord.getTask().forAllLeafTaskFragments(new Predicate() { // from class: com.android.server.wm.TaskFragmentOrganizerController$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onActivityReparentedToTask$0;
                    lambda$onActivityReparentedToTask$0 = TaskFragmentOrganizerController.lambda$onActivityReparentedToTask$0(taskFragmentArr, (TaskFragment) obj);
                    return lambda$onActivityReparentedToTask$0;
                }
            });
            TaskFragment taskFragment = taskFragmentArr[0];
            if (taskFragment == null) {
                return;
            }
            iTaskFragmentOrganizer = taskFragment.getTaskFragmentOrganizer();
        }
        if (!isOrganizerRegistered(iTaskFragmentOrganizer)) {
            Slog.w("TaskFragmentOrganizerController", "The last TaskFragmentOrganizer no longer exists");
        } else {
            addPendingEvent(new PendingTaskFragmentEvent.Builder(5, iTaskFragmentOrganizer).setActivity(activityRecord).build());
        }
    }

    public static /* synthetic */ boolean lambda$onActivityReparentedToTask$0(TaskFragment[] taskFragmentArr, TaskFragment taskFragment) {
        if (taskFragment.isOrganizedTaskFragment()) {
            taskFragmentArr[0] = taskFragment;
            return true;
        }
        return false;
    }

    public void onTaskFragmentParentInfoChanged(ITaskFragmentOrganizer iTaskFragmentOrganizer, Task task) {
        validateAndGetState(iTaskFragmentOrganizer);
        if (getLastPendingParentInfoChangedEvent(iTaskFragmentOrganizer, task) == null) {
            addPendingEvent(new PendingTaskFragmentEvent.Builder(3, iTaskFragmentOrganizer).setTask(task).build());
        }
    }

    public final PendingTaskFragmentEvent getLastPendingParentInfoChangedEvent(ITaskFragmentOrganizer iTaskFragmentOrganizer, Task task) {
        List<PendingTaskFragmentEvent> list = this.mPendingTaskFragmentEvents.get(iTaskFragmentOrganizer.asBinder());
        for (int size = list.size() - 1; size >= 0; size--) {
            PendingTaskFragmentEvent pendingTaskFragmentEvent = list.get(size);
            if (task == pendingTaskFragmentEvent.mTask && pendingTaskFragmentEvent.mEventType == 3) {
                return pendingTaskFragmentEvent;
            }
        }
        return null;
    }

    public final void addPendingEvent(PendingTaskFragmentEvent pendingTaskFragmentEvent) {
        this.mPendingTaskFragmentEvents.get(pendingTaskFragmentEvent.mTaskFragmentOrg.asBinder()).add(pendingTaskFragmentEvent);
    }

    public final void removePendingEvent(PendingTaskFragmentEvent pendingTaskFragmentEvent) {
        this.mPendingTaskFragmentEvents.get(pendingTaskFragmentEvent.mTaskFragmentOrg.asBinder()).remove(pendingTaskFragmentEvent);
    }

    public final boolean isOrganizerRegistered(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        return this.mTaskFragmentOrganizerState.containsKey(iTaskFragmentOrganizer.asBinder());
    }

    public final void removeOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
        if (taskFragmentOrganizerState == null) {
            Slog.w("TaskFragmentOrganizerController", "The organizer has already been removed.");
            return;
        }
        this.mPendingTaskFragmentEvents.remove(iTaskFragmentOrganizer.asBinder());
        taskFragmentOrganizerState.dispose();
        this.mTaskFragmentOrganizerState.remove(iTaskFragmentOrganizer.asBinder());
    }

    public final TaskFragmentOrganizerState validateAndGetState(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
        if (taskFragmentOrganizerState != null) {
            return taskFragmentOrganizerState;
        }
        throw new IllegalArgumentException("TaskFragmentOrganizer has not been registered. Organizer=" + iTaskFragmentOrganizer);
    }

    public boolean isValidTransaction(WindowContainerTransaction windowContainerTransaction) {
        if (windowContainerTransaction.isEmpty()) {
            return false;
        }
        ITaskFragmentOrganizer taskFragmentOrganizer = windowContainerTransaction.getTaskFragmentOrganizer();
        if (windowContainerTransaction.getTaskFragmentOrganizer() == null || !isOrganizerRegistered(taskFragmentOrganizer)) {
            Slog.e("TaskFragmentOrganizerController", "Caller organizer=" + taskFragmentOrganizer + " is no longer registered");
            return false;
        }
        return true;
    }

    /* renamed from: com.android.server.wm.TaskFragmentOrganizerController$PendingTaskFragmentEvent */
    /* loaded from: classes2.dex */
    public static class PendingTaskFragmentEvent {
        public final ActivityRecord mActivity;
        public long mDeferTime;
        public final IBinder mErrorCallbackToken;
        public final int mEventType;
        public final Throwable mException;
        public int mOpType;
        public final Task mTask;
        public final TaskFragment mTaskFragment;
        public final ITaskFragmentOrganizer mTaskFragmentOrg;

        public PendingTaskFragmentEvent(int i, ITaskFragmentOrganizer iTaskFragmentOrganizer, TaskFragment taskFragment, IBinder iBinder, Throwable th, ActivityRecord activityRecord, Task task, int i2) {
            this.mEventType = i;
            this.mTaskFragmentOrg = iTaskFragmentOrganizer;
            this.mTaskFragment = taskFragment;
            this.mErrorCallbackToken = iBinder;
            this.mException = th;
            this.mActivity = activityRecord;
            this.mTask = task;
            this.mOpType = i2;
        }

        public boolean isLifecycleEvent() {
            int i = this.mEventType;
            return i == 0 || i == 1 || i == 2 || i == 3;
        }

        /* renamed from: com.android.server.wm.TaskFragmentOrganizerController$PendingTaskFragmentEvent$Builder */
        /* loaded from: classes2.dex */
        public static class Builder {
            public ActivityRecord mActivity;
            public IBinder mErrorCallbackToken;
            public final int mEventType;
            public Throwable mException;
            public int mOpType;
            public Task mTask;
            public TaskFragment mTaskFragment;
            public final ITaskFragmentOrganizer mTaskFragmentOrg;

            public Builder(int i, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
                this.mEventType = i;
                Objects.requireNonNull(iTaskFragmentOrganizer);
                this.mTaskFragmentOrg = iTaskFragmentOrganizer;
            }

            public Builder setTaskFragment(TaskFragment taskFragment) {
                this.mTaskFragment = taskFragment;
                return this;
            }

            public Builder setErrorCallbackToken(IBinder iBinder) {
                this.mErrorCallbackToken = iBinder;
                return this;
            }

            public Builder setException(Throwable th) {
                Objects.requireNonNull(th);
                this.mException = th;
                return this;
            }

            public Builder setActivity(ActivityRecord activityRecord) {
                Objects.requireNonNull(activityRecord);
                this.mActivity = activityRecord;
                return this;
            }

            public Builder setTask(Task task) {
                Objects.requireNonNull(task);
                this.mTask = task;
                return this;
            }

            public Builder setOpType(int i) {
                this.mOpType = i;
                return this;
            }

            public PendingTaskFragmentEvent build() {
                return new PendingTaskFragmentEvent(this.mEventType, this.mTaskFragmentOrg, this.mTaskFragment, this.mErrorCallbackToken, this.mException, this.mActivity, this.mTask, this.mOpType);
            }
        }
    }

    public final PendingTaskFragmentEvent getLastPendingLifecycleEvent(TaskFragment taskFragment) {
        List<PendingTaskFragmentEvent> list = this.mPendingTaskFragmentEvents.get(taskFragment.getTaskFragmentOrganizer().asBinder());
        for (int size = list.size() - 1; size >= 0; size--) {
            PendingTaskFragmentEvent pendingTaskFragmentEvent = list.get(size);
            if (taskFragment == pendingTaskFragmentEvent.mTaskFragment && pendingTaskFragmentEvent.isLifecycleEvent()) {
                return pendingTaskFragmentEvent;
            }
        }
        return null;
    }

    public final PendingTaskFragmentEvent getPendingTaskFragmentEvent(TaskFragment taskFragment, int i) {
        List<PendingTaskFragmentEvent> list = this.mPendingTaskFragmentEvents.get(taskFragment.getTaskFragmentOrganizer().asBinder());
        for (int size = list.size() - 1; size >= 0; size--) {
            PendingTaskFragmentEvent pendingTaskFragmentEvent = list.get(size);
            if (taskFragment == pendingTaskFragmentEvent.mTaskFragment && i == pendingTaskFragmentEvent.mEventType) {
                return pendingTaskFragmentEvent;
            }
        }
        return null;
    }

    public void dispatchPendingEvents() {
        if (this.mAtmService.mWindowManager.mWindowPlacerLocked.isLayoutDeferred() || this.mPendingTaskFragmentEvents.isEmpty()) {
            return;
        }
        int size = this.mPendingTaskFragmentEvents.size();
        for (int i = 0; i < size; i++) {
            dispatchPendingEvents(this.mTaskFragmentOrganizerState.get(this.mPendingTaskFragmentEvents.keyAt(i)), this.mPendingTaskFragmentEvents.valueAt(i));
        }
    }

    public final void dispatchPendingEvents(TaskFragmentOrganizerState taskFragmentOrganizerState, List<PendingTaskFragmentEvent> list) {
        if (list.isEmpty() || shouldDeferPendingEvents(taskFragmentOrganizerState, list)) {
            return;
        }
        this.mTmpTaskSet.clear();
        int size = list.size();
        TaskFragmentTransaction taskFragmentTransaction = new TaskFragmentTransaction();
        for (int i = 0; i < size; i++) {
            PendingTaskFragmentEvent pendingTaskFragmentEvent = list.get(i);
            if (pendingTaskFragmentEvent.mEventType == 0 || pendingTaskFragmentEvent.mEventType == 2) {
                Task task = pendingTaskFragmentEvent.mTaskFragment.getTask();
                if (this.mTmpTaskSet.add(task)) {
                    taskFragmentTransaction.addChange(prepareChange(new PendingTaskFragmentEvent.Builder(3, taskFragmentOrganizerState.mOrganizer).setTask(task).build()));
                }
            }
            taskFragmentTransaction.addChange(prepareChange(pendingTaskFragmentEvent));
        }
        this.mTmpTaskSet.clear();
        taskFragmentOrganizerState.dispatchTransaction(taskFragmentTransaction);
        list.clear();
    }

    public final boolean shouldDeferPendingEvents(TaskFragmentOrganizerState taskFragmentOrganizerState, List<PendingTaskFragmentEvent> list) {
        Task task;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            PendingTaskFragmentEvent pendingTaskFragmentEvent = list.get(i);
            if (pendingTaskFragmentEvent.mEventType != 3 && pendingTaskFragmentEvent.mEventType != 2 && pendingTaskFragmentEvent.mEventType != 0) {
                return false;
            }
            if (pendingTaskFragmentEvent.mEventType == 3) {
                task = pendingTaskFragmentEvent.mTask;
            } else {
                task = pendingTaskFragmentEvent.mTaskFragment.getTask();
            }
            if ((task.lastActiveTime > pendingTaskFragmentEvent.mDeferTime && isTaskVisible(task, arrayList, arrayList2)) || shouldSendEventWhenTaskInvisible(task, taskFragmentOrganizerState, pendingTaskFragmentEvent)) {
                return false;
            }
            pendingTaskFragmentEvent.mDeferTime = task.lastActiveTime;
        }
        return true;
    }

    public static boolean isTaskVisible(Task task, ArrayList<Task> arrayList, ArrayList<Task> arrayList2) {
        if (arrayList.contains(task)) {
            return true;
        }
        if (arrayList2.contains(task)) {
            return false;
        }
        if (task.shouldBeVisible(null)) {
            arrayList.add(task);
            return true;
        }
        arrayList2.add(task);
        return false;
    }

    public final boolean shouldSendEventWhenTaskInvisible(Task task, TaskFragmentOrganizerState taskFragmentOrganizerState, PendingTaskFragmentEvent pendingTaskFragmentEvent) {
        TaskFragmentParentInfo taskFragmentParentInfo = (TaskFragmentParentInfo) taskFragmentOrganizerState.mLastSentTaskFragmentParentInfos.get(task.mTaskId);
        if (taskFragmentParentInfo == null || taskFragmentParentInfo.isVisible()) {
            return true;
        }
        if (pendingTaskFragmentEvent.mEventType == 2) {
            TaskFragmentInfo taskFragmentInfo = (TaskFragmentInfo) taskFragmentOrganizerState.mLastSentTaskFragmentInfos.get(pendingTaskFragmentEvent.mTaskFragment);
            return taskFragmentInfo == null || taskFragmentInfo.isEmpty() != (pendingTaskFragmentEvent.mTaskFragment.getNonFinishingActivityCount() == 0);
        }
        return false;
    }

    public void dispatchPendingInfoChangedEvent(TaskFragment taskFragment) {
        PendingTaskFragmentEvent pendingTaskFragmentEvent = getPendingTaskFragmentEvent(taskFragment, 2);
        if (pendingTaskFragmentEvent == null) {
            return;
        }
        ITaskFragmentOrganizer taskFragmentOrganizer = taskFragment.getTaskFragmentOrganizer();
        TaskFragmentOrganizerState validateAndGetState = validateAndGetState(taskFragmentOrganizer);
        TaskFragmentTransaction taskFragmentTransaction = new TaskFragmentTransaction();
        taskFragmentTransaction.addChange(prepareChange(new PendingTaskFragmentEvent.Builder(3, taskFragmentOrganizer).setTask(taskFragment.getTask()).build()));
        taskFragmentTransaction.addChange(prepareChange(pendingTaskFragmentEvent));
        validateAndGetState.dispatchTransaction(taskFragmentTransaction);
        this.mPendingTaskFragmentEvents.get(taskFragmentOrganizer.asBinder()).remove(pendingTaskFragmentEvent);
    }

    public final TaskFragmentTransaction.Change prepareChange(PendingTaskFragmentEvent pendingTaskFragmentEvent) {
        ITaskFragmentOrganizer iTaskFragmentOrganizer = pendingTaskFragmentEvent.mTaskFragmentOrg;
        TaskFragment taskFragment = pendingTaskFragmentEvent.mTaskFragment;
        TaskFragmentOrganizerState taskFragmentOrganizerState = this.mTaskFragmentOrganizerState.get(iTaskFragmentOrganizer.asBinder());
        if (taskFragmentOrganizerState == null) {
            return null;
        }
        int i = pendingTaskFragmentEvent.mEventType;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return taskFragmentOrganizerState.prepareActivityReparentedToTask(pendingTaskFragmentEvent.mActivity);
                            }
                            throw new IllegalArgumentException("Unknown TaskFragmentEvent=" + pendingTaskFragmentEvent.mEventType);
                        }
                        return taskFragmentOrganizerState.prepareTaskFragmentError(pendingTaskFragmentEvent.mErrorCallbackToken, taskFragment, pendingTaskFragmentEvent.mOpType, pendingTaskFragmentEvent.mException);
                    }
                    return taskFragmentOrganizerState.prepareTaskFragmentParentInfoChanged(pendingTaskFragmentEvent.mTask);
                }
                return taskFragmentOrganizerState.prepareTaskFragmentInfoChanged(taskFragment);
            }
            return taskFragmentOrganizerState.prepareTaskFragmentVanished(taskFragment);
        }
        return taskFragmentOrganizerState.prepareTaskFragmentAppeared(taskFragment);
    }

    public boolean isActivityEmbedded(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                boolean z = false;
                if (forTokenLocked == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                TaskFragment organizedTaskFragment = forTokenLocked.getOrganizedTaskFragment();
                if (organizedTaskFragment != null && organizedTaskFragment.isEmbeddedWithBoundsOverride()) {
                    z = true;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public static Intent trimIntent(Intent intent) {
        return new Intent().setComponent(intent.getComponent()).setPackage(intent.getPackage()).setAction(intent.getAction());
    }
}
