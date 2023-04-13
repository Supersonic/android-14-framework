package com.android.server.p014wm;

import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.util.EventLog;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.telephony.CellBroadcastUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.p014wm.LockTaskController;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.LockTaskController */
/* loaded from: classes2.dex */
public class LockTaskController {
    public static final SparseArray<Pair<Integer, Integer>> STATUS_BAR_FLAG_MAP_LOCKED;
    @VisibleForTesting
    static final int STATUS_BAR_MASK_LOCKED = 128319488;
    @VisibleForTesting
    static final int STATUS_BAR_MASK_PINNED = 111083520;
    public final Context mContext;
    @VisibleForTesting
    IDevicePolicyManager mDevicePolicyManager;
    public final Handler mHandler;
    @VisibleForTesting
    LockPatternUtils mLockPatternUtils;
    @VisibleForTesting
    IStatusBarService mStatusBarService;
    public final ActivityTaskSupervisor mSupervisor;
    public final TaskChangeNotificationController mTaskChangeNotificationController;
    @VisibleForTesting
    TelecomManager mTelecomManager;
    @VisibleForTesting
    WindowManagerService mWindowManager;
    public final IBinder mToken = new LockTaskToken();
    public final ArrayList<Task> mLockTaskModeTasks = new ArrayList<>();
    public final SparseArray<String[]> mLockTaskPackages = new SparseArray<>();
    public final SparseIntArray mLockTaskFeatures = new SparseIntArray();
    public volatile int mLockTaskModeState = 0;
    public int mPendingDisableFromDismiss = -10000;

    public static boolean isTaskAuthAllowlisted(int i) {
        return i == 2 || i == 3 || i == 4;
    }

    static {
        SparseArray<Pair<Integer, Integer>> sparseArray = new SparseArray<>();
        STATUS_BAR_FLAG_MAP_LOCKED = sparseArray;
        sparseArray.append(1, new Pair<>(8388608, 2));
        sparseArray.append(2, new Pair<>(393216, 4));
        sparseArray.append(4, new Pair<>(2097152, 0));
        sparseArray.append(8, new Pair<>(16777216, 0));
        sparseArray.append(16, new Pair<>(0, 8));
    }

    public LockTaskController(Context context, ActivityTaskSupervisor activityTaskSupervisor, Handler handler, TaskChangeNotificationController taskChangeNotificationController) {
        this.mContext = context;
        this.mSupervisor = activityTaskSupervisor;
        this.mHandler = handler;
        this.mTaskChangeNotificationController = taskChangeNotificationController;
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mWindowManager = windowManagerService;
    }

    public int getLockTaskModeState() {
        return this.mLockTaskModeState;
    }

    @VisibleForTesting
    public boolean isTaskLocked(Task task) {
        return this.mLockTaskModeTasks.contains(task);
    }

    public final boolean isRootTask(Task task) {
        return this.mLockTaskModeTasks.indexOf(task) == 0;
    }

    public boolean activityBlockedFromFinish(final ActivityRecord activityRecord) {
        Task task = activityRecord.getTask();
        if (task.mLockTaskAuth != 4 && isRootTask(task)) {
            ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity();
            if (activityRecord != task.getRootActivity() || activityRecord != topNonFinishingActivity) {
                TaskFragment taskFragment = activityRecord.getTaskFragment();
                final TaskFragment adjacentTaskFragment = taskFragment.getAdjacentTaskFragment();
                if (taskFragment.asTask() == null && taskFragment.isDelayLastActivityRemoval() && adjacentTaskFragment != null) {
                    if (taskFragment.getActivity(new Predicate() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda3
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$activityBlockedFromFinish$0;
                            lambda$activityBlockedFromFinish$0 = LockTaskController.lambda$activityBlockedFromFinish$0(ActivityRecord.this, (ActivityRecord) obj);
                            return lambda$activityBlockedFromFinish$0;
                        }
                    }) != null) {
                        return false;
                    }
                    if (task.getActivity(new Predicate() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda4
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$activityBlockedFromFinish$1;
                            lambda$activityBlockedFromFinish$1 = LockTaskController.lambda$activityBlockedFromFinish$1(ActivityRecord.this, adjacentTaskFragment, (ActivityRecord) obj);
                            return lambda$activityBlockedFromFinish$1;
                        }
                    }) != null) {
                        return false;
                    }
                }
            }
            Slog.i("ActivityTaskManager", "Not finishing task in lock task mode");
            showLockTaskToast();
            return true;
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$activityBlockedFromFinish$0(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        return (activityRecord2.finishing || activityRecord2 == activityRecord) ? false : true;
    }

    public static /* synthetic */ boolean lambda$activityBlockedFromFinish$1(ActivityRecord activityRecord, TaskFragment taskFragment, ActivityRecord activityRecord2) {
        return (activityRecord2.finishing || activityRecord2 == activityRecord || activityRecord2.getTaskFragment() == taskFragment) ? false : true;
    }

    public boolean canMoveTaskToBack(Task task) {
        if (isRootTask(task)) {
            showLockTaskToast();
            return false;
        }
        return true;
    }

    public boolean isLockTaskModeViolation(Task task) {
        return isLockTaskModeViolation(task, false);
    }

    public boolean isLockTaskModeViolation(Task task, boolean z) {
        if ((!isTaskLocked(task) || z) && isLockTaskModeViolationInternal(task, task.mUserId, task.intent, task.mLockTaskAuth)) {
            showLockTaskToast();
            return true;
        }
        return false;
    }

    public boolean isNewTaskLockTaskModeViolation(ActivityRecord activityRecord) {
        if (activityRecord.getTask() != null) {
            return isLockTaskModeViolation(activityRecord.getTask());
        }
        if (isLockTaskModeViolationInternal(activityRecord, activityRecord.mUserId, activityRecord.intent, getLockTaskAuth(activityRecord, null))) {
            showLockTaskToast();
            return true;
        }
        return false;
    }

    public Task getRootTask() {
        if (this.mLockTaskModeTasks.isEmpty()) {
            return null;
        }
        return this.mLockTaskModeTasks.get(0);
    }

    public final boolean isLockTaskModeViolationInternal(WindowContainer windowContainer, int i, Intent intent, int i2) {
        if (windowContainer.isActivityTypeRecents() && isRecentsAllowed(i)) {
            return false;
        }
        return ((isKeyguardAllowed(i) && isEmergencyCallIntent(intent)) || windowContainer.isActivityTypeDream() || isWirelessEmergencyAlert(intent) || isTaskAuthAllowlisted(i2) || this.mLockTaskModeTasks.isEmpty()) ? false : true;
    }

    public final boolean isRecentsAllowed(int i) {
        return (getLockTaskFeaturesForUser(i) & 8) != 0;
    }

    public final boolean isKeyguardAllowed(int i) {
        return (getLockTaskFeaturesForUser(i) & 32) != 0;
    }

    public final boolean isBlockingInTaskEnabled(int i) {
        return (getLockTaskFeaturesForUser(i) & 64) != 0;
    }

    public boolean isActivityAllowed(int i, String str, int i2) {
        if (this.mLockTaskModeState == 1 && isBlockingInTaskEnabled(i)) {
            if (i2 != 1) {
                if (i2 != 2) {
                    return isPackageAllowlisted(i, str);
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public final boolean isWirelessEmergencyAlert(Intent intent) {
        ComponentName defaultCellBroadcastAlertDialogComponent;
        return (intent == null || (defaultCellBroadcastAlertDialogComponent = CellBroadcastUtils.getDefaultCellBroadcastAlertDialogComponent(this.mContext)) == null || !defaultCellBroadcastAlertDialogComponent.equals(intent.getComponent())) ? false : true;
    }

    public final boolean isEmergencyCallIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        if (TelecomManager.EMERGENCY_DIALER_COMPONENT.equals(intent.getComponent()) || "android.intent.action.CALL_EMERGENCY".equals(intent.getAction())) {
            return true;
        }
        TelecomManager telecomManager = getTelecomManager();
        String systemDialerPackage = telecomManager != null ? telecomManager.getSystemDialerPackage() : null;
        return systemDialerPackage != null && systemDialerPackage.equals(intent.getComponent().getPackageName());
    }

    public void stopLockTaskMode(Task task, boolean z, int i) {
        if (this.mLockTaskModeState == 0) {
            return;
        }
        if (z) {
            if (this.mLockTaskModeState == 2) {
                clearLockedTasks("stopAppPinning");
                return;
            }
            Slog.e("ActivityTaskManager", "Attempted to stop app pinning while fully locked");
            showLockTaskToast();
        } else if (task == null) {
            throw new IllegalArgumentException("can't stop LockTask for null task");
        } else {
            int i2 = task.mLockTaskUid;
            if (i != i2 && (i2 != 0 || i != task.effectiveUid)) {
                throw new SecurityException("Invalid uid, expected " + task.mLockTaskUid + " callingUid=" + i + " effectiveUid=" + task.effectiveUid);
            }
            clearLockedTask(task);
        }
    }

    public void clearLockedTasks(String str) {
        if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_LOCKTASK, -317194205, 0, (String) null, new Object[]{String.valueOf(str)});
        }
        if (this.mLockTaskModeTasks.isEmpty()) {
            return;
        }
        clearLockedTask(this.mLockTaskModeTasks.get(0));
    }

    public void clearLockedTask(Task task) {
        if (task == null || this.mLockTaskModeTasks.isEmpty()) {
            return;
        }
        if (task == this.mLockTaskModeTasks.get(0)) {
            for (int size = this.mLockTaskModeTasks.size() - 1; size > 0; size--) {
                clearLockedTask(this.mLockTaskModeTasks.get(size));
            }
        }
        removeLockedTask(task);
        if (this.mLockTaskModeTasks.isEmpty()) {
            return;
        }
        task.performClearTaskForReuse(false);
        this.mSupervisor.mRootWindowContainer.resumeFocusedTasksTopActivities();
    }

    public final void removeLockedTask(final Task task) {
        if (this.mLockTaskModeTasks.remove(task)) {
            if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_LOCKTASK, -1630752478, 0, (String) null, new Object[]{String.valueOf(task)});
            }
            if (this.mLockTaskModeTasks.isEmpty()) {
                if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_LOCKTASK, 956374481, 0, (String) null, new Object[]{String.valueOf(task), String.valueOf(Debug.getCallers(3))});
                }
                this.mHandler.post(new Runnable() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        LockTaskController.this.lambda$removeLockedTask$2(task);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeLockedTask$2(Task task) {
        performStopLockTask(task.mUserId);
    }

    public final void performStopLockTask(int i) {
        IStatusBarService statusBarService;
        int i2 = this.mLockTaskModeState;
        this.mLockTaskModeState = 0;
        this.mTaskChangeNotificationController.notifyLockTaskModeChanged(this.mLockTaskModeState);
        try {
            setStatusBarState(this.mLockTaskModeState, i);
            setKeyguardState(this.mLockTaskModeState, i);
            if (i2 == 2) {
                lockKeyguardIfNeeded(i);
            }
            if (getDevicePolicyManager() != null) {
                getDevicePolicyManager().notifyLockTaskModeChanged(false, (String) null, i);
            }
            if (i2 == 2 && (statusBarService = getStatusBarService()) != null) {
                statusBarService.showPinningEnterExitToast(false);
            }
            this.mWindowManager.onLockTaskStateChanged(this.mLockTaskModeState);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showLockTaskToast() {
        if (this.mLockTaskModeState == 2) {
            try {
                IStatusBarService statusBarService = getStatusBarService();
                if (statusBarService != null) {
                    statusBarService.showPinningEscapeToast();
                }
            } catch (RemoteException e) {
                Slog.e("ActivityTaskManager", "Failed to send pinning escape toast", e);
            }
        }
    }

    public void startLockTaskMode(Task task, boolean z, int i) {
        int i2 = task.mLockTaskAuth;
        if (i2 == 0) {
            if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, -1075136930, 0, (String) null, (Object[]) null);
                return;
            }
            return;
        }
        if (!z) {
            task.mLockTaskUid = i;
            if (i2 == 1) {
                if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, 1401295262, 0, (String) null, (Object[]) null);
                }
                StatusBarManagerInternal statusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
                if (statusBarManagerInternal != null) {
                    statusBarManagerInternal.showScreenPinningRequest(task.mTaskId);
                    return;
                }
                return;
            } else if (this.mLockTaskModeState == 2) {
                Slog.i("ActivityTaskManager", "Stop app pinning before entering full lock task mode");
                stopLockTaskMode(null, true, i);
            }
        }
        if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
            ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, -2121056984, 0, (String) null, new Object[]{z ? "Locking pinned" : "Locking fully"});
        }
        setLockTaskMode(task, z ? 2 : 1, "startLockTask", true);
    }

    public final void setLockTaskMode(final Task task, final int i, String str, boolean z) {
        if (task.mLockTaskAuth == 0) {
            if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, 950074526, 0, (String) null, (Object[]) null);
            }
        } else if (isLockTaskModeViolation(task)) {
            Slog.e("ActivityTaskManager", "setLockTaskMode: Attempt to start an unauthorized lock task.");
        } else {
            final Intent intent = task.intent;
            if (this.mLockTaskModeTasks.isEmpty() && intent != null) {
                this.mSupervisor.mRecentTasks.onLockTaskModeStateChanged(i, task.mUserId);
                this.mHandler.post(new Runnable() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LockTaskController.this.lambda$setLockTaskMode$3(intent, task, i);
                    }
                });
            }
            if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, -548282316, 0, (String) null, new Object[]{String.valueOf(task), String.valueOf(Debug.getCallers(4))});
            }
            if (!this.mLockTaskModeTasks.contains(task)) {
                this.mLockTaskModeTasks.add(task);
            }
            if (task.mLockTaskUid == -1) {
                task.mLockTaskUid = task.effectiveUid;
            }
            if (!z) {
                if (i != 0) {
                    ActivityTaskSupervisor activityTaskSupervisor = this.mSupervisor;
                    activityTaskSupervisor.handleNonResizableTaskIfNeeded(task, 0, activityTaskSupervisor.mRootWindowContainer.getDefaultTaskDisplayArea(), task.getRootTask(), true);
                    return;
                }
                return;
            }
            this.mSupervisor.findTaskToMoveToFront(task, 0, null, str, i != 0);
            this.mSupervisor.mRootWindowContainer.resumeFocusedTasksTopActivities();
            Task rootTask = task.getRootTask();
            if (rootTask != null) {
                rootTask.mDisplayContent.executeAppTransition();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLockTaskMode$3(Intent intent, Task task, int i) {
        performStartLockTask(intent.getComponent().getPackageName(), task.mUserId, i);
    }

    public final void performStartLockTask(String str, int i, int i2) {
        if (i2 == 2) {
            try {
                IStatusBarService statusBarService = getStatusBarService();
                if (statusBarService != null) {
                    statusBarService.showPinningEnterExitToast(true);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        this.mWindowManager.onLockTaskStateChanged(i2);
        this.mLockTaskModeState = i2;
        this.mTaskChangeNotificationController.notifyLockTaskModeChanged(this.mLockTaskModeState);
        setStatusBarState(i2, i);
        setKeyguardState(i2, i);
        if (getDevicePolicyManager() != null) {
            getDevicePolicyManager().notifyLockTaskModeChanged(true, str, i);
        }
    }

    public void updateLockTaskPackages(int i, String[] strArr) {
        this.mLockTaskPackages.put(i, strArr);
        boolean z = true;
        boolean z2 = false;
        for (int size = this.mLockTaskModeTasks.size() - 1; size >= 0; size--) {
            Task task = this.mLockTaskModeTasks.get(size);
            int i2 = task.mLockTaskAuth;
            boolean z3 = i2 == 2 || i2 == 3;
            task.setLockTaskAuth();
            int i3 = task.mLockTaskAuth;
            boolean z4 = i3 == 2 || i3 == 3;
            if (this.mLockTaskModeState == 1 && task.mUserId == i && z3 && !z4) {
                if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_LOCKTASK, 1829094918, 0, (String) null, new Object[]{String.valueOf(task), String.valueOf(task.lockTaskAuthToString())});
                }
                removeLockedTask(task);
                task.performClearTaskForReuse(false);
                z2 = true;
            }
        }
        this.mSupervisor.mRootWindowContainer.forAllTasks(new Consumer() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Task) obj).setLockTaskAuth();
            }
        });
        ActivityRecord activityRecord = this.mSupervisor.mRootWindowContainer.topRunningActivity();
        Task task2 = activityRecord != null ? activityRecord.getTask() : null;
        if (this.mLockTaskModeTasks.isEmpty() && task2 != null && task2.mLockTaskAuth == 2) {
            if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_LOCKTASK, 1088929964, 0, (String) null, new Object[]{String.valueOf(task2)});
            }
            setLockTaskMode(task2, 1, "package updated", false);
        } else {
            z = z2;
        }
        if (z) {
            this.mSupervisor.mRootWindowContainer.resumeFocusedTasksTopActivities();
        }
    }

    public int getLockTaskAuth(ActivityRecord activityRecord, Task task) {
        String str;
        ComponentName componentName;
        if (activityRecord == null && task == null) {
            return 0;
        }
        if (activityRecord == null) {
            return 1;
        }
        if (task == null || (componentName = task.realActivity) == null) {
            str = activityRecord.packageName;
        } else {
            str = componentName.getPackageName();
        }
        int i = task != null ? task.mUserId : activityRecord.mUserId;
        int i2 = activityRecord.lockTaskLaunchMode;
        if (i2 != 0) {
            if (i2 == 1) {
                return 0;
            }
            if (i2 == 2) {
                return 4;
            }
            if (i2 != 3) {
                return 0;
            }
            if (isPackageAllowlisted(i, str)) {
                return 2;
            }
        } else if (isPackageAllowlisted(i, str)) {
            return 3;
        }
        return 1;
    }

    public boolean isPackageAllowlisted(int i, String str) {
        String[] strArr;
        if (str == null || (strArr = this.mLockTaskPackages.get(i)) == null) {
            return false;
        }
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    public void updateLockTaskFeatures(final int i, int i2) {
        if (i2 == getLockTaskFeaturesForUser(i)) {
            return;
        }
        this.mLockTaskFeatures.put(i, i2);
        if (this.mLockTaskModeTasks.isEmpty() || i != this.mLockTaskModeTasks.get(0).mUserId) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.wm.LockTaskController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                LockTaskController.this.lambda$updateLockTaskFeatures$4(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateLockTaskFeatures$4(int i) {
        if (this.mLockTaskModeState == 1) {
            setStatusBarState(this.mLockTaskModeState, i);
            setKeyguardState(this.mLockTaskModeState, i);
        }
    }

    public final void setStatusBarState(int i, int i2) {
        int i3;
        IStatusBarService statusBarService = getStatusBarService();
        if (statusBarService == null) {
            Slog.e("ActivityTaskManager", "Can't find StatusBarService");
            return;
        }
        int i4 = 0;
        if (i == 2) {
            i4 = STATUS_BAR_MASK_PINNED;
            i3 = 0;
        } else if (i == 1) {
            Pair<Integer, Integer> statusBarDisableFlags = getStatusBarDisableFlags(getLockTaskFeaturesForUser(i2));
            i4 = ((Integer) statusBarDisableFlags.first).intValue();
            i3 = ((Integer) statusBarDisableFlags.second).intValue();
        } else {
            i3 = 0;
        }
        try {
            statusBarService.disable(i4, this.mToken, this.mContext.getPackageName());
            statusBarService.disable2(i3, this.mToken, this.mContext.getPackageName());
        } catch (RemoteException e) {
            Slog.e("ActivityTaskManager", "Failed to set status bar flags", e);
        }
    }

    public final void setKeyguardState(int i, int i2) {
        this.mPendingDisableFromDismiss = -10000;
        if (i == 0) {
            this.mWindowManager.reenableKeyguard(this.mToken, i2);
        } else if (i == 1) {
            if (isKeyguardAllowed(i2)) {
                this.mWindowManager.reenableKeyguard(this.mToken, i2);
            } else if (this.mWindowManager.isKeyguardLocked() && !this.mWindowManager.isKeyguardSecure(i2)) {
                this.mPendingDisableFromDismiss = i2;
                this.mWindowManager.dismissKeyguard(new C18821(i2), null);
            } else {
                this.mWindowManager.disableKeyguard(this.mToken, "Lock-to-App", i2);
            }
        } else {
            this.mWindowManager.disableKeyguard(this.mToken, "Lock-to-App", i2);
        }
    }

    /* renamed from: com.android.server.wm.LockTaskController$1 */
    /* loaded from: classes2.dex */
    public class C18821 extends IKeyguardDismissCallback.Stub {
        public final /* synthetic */ int val$userId;

        public C18821(int i) {
            this.val$userId = i;
        }

        public void onDismissError() throws RemoteException {
            Slog.i("ActivityTaskManager", "setKeyguardState: failed to dismiss keyguard");
        }

        public void onDismissSucceeded() throws RemoteException {
            Handler handler = LockTaskController.this.mHandler;
            final int i = this.val$userId;
            handler.post(new Runnable() { // from class: com.android.server.wm.LockTaskController$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LockTaskController.C18821.this.lambda$onDismissSucceeded$0(i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDismissSucceeded$0(int i) {
            if (LockTaskController.this.mPendingDisableFromDismiss == i) {
                LockTaskController lockTaskController = LockTaskController.this;
                lockTaskController.mWindowManager.disableKeyguard(lockTaskController.mToken, "Lock-to-App", i);
                LockTaskController.this.mPendingDisableFromDismiss = -10000;
            }
        }

        public void onDismissCancelled() throws RemoteException {
            Slog.i("ActivityTaskManager", "setKeyguardState: dismiss cancelled");
        }
    }

    public final void lockKeyguardIfNeeded(int i) {
        if (shouldLockKeyguard(i)) {
            this.mWindowManager.lockNow(null);
            this.mWindowManager.dismissKeyguard(null, null);
            getLockPatternUtils().requireCredentialEntry(-1);
        }
    }

    public final boolean shouldLockKeyguard(int i) {
        try {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_to_app_exit_locked", -2) != 0;
        } catch (Settings.SettingNotFoundException unused) {
            EventLog.writeEvent(1397638484, "127605586", -1, "");
            return this.getLockPatternUtils().isSecure(i);
        }
    }

    @VisibleForTesting
    public Pair<Integer, Integer> getStatusBarDisableFlags(int i) {
        int i2 = 134152192;
        int i3 = 31;
        for (int size = STATUS_BAR_FLAG_MAP_LOCKED.size() - 1; size >= 0; size--) {
            SparseArray<Pair<Integer, Integer>> sparseArray = STATUS_BAR_FLAG_MAP_LOCKED;
            Pair<Integer, Integer> valueAt = sparseArray.valueAt(size);
            if ((sparseArray.keyAt(size) & i) != 0) {
                i2 &= ~((Integer) valueAt.first).intValue();
                i3 &= ~((Integer) valueAt.second).intValue();
            }
        }
        return new Pair<>(Integer.valueOf(STATUS_BAR_MASK_LOCKED & i2), Integer.valueOf(i3));
    }

    public boolean isBaseOfLockedTask(String str) {
        for (int i = 0; i < this.mLockTaskModeTasks.size(); i++) {
            Intent baseIntent = this.mLockTaskModeTasks.get(i).getBaseIntent();
            if (baseIntent != null && str.equals(baseIntent.getComponent().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public final int getLockTaskFeaturesForUser(int i) {
        return this.mLockTaskFeatures.get(i, 0);
    }

    public final IStatusBarService getStatusBarService() {
        if (this.mStatusBarService == null) {
            IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
            this.mStatusBarService = asInterface;
            if (asInterface == null) {
                Slog.w("StatusBarManager", "warning: no STATUS_BAR_SERVICE");
            }
        }
        return this.mStatusBarService;
    }

    public final IDevicePolicyManager getDevicePolicyManager() {
        if (this.mDevicePolicyManager == null) {
            IDevicePolicyManager asInterface = IDevicePolicyManager.Stub.asInterface(ServiceManager.checkService("device_policy"));
            this.mDevicePolicyManager = asInterface;
            if (asInterface == null) {
                Slog.w("ActivityTaskManager", "warning: no DEVICE_POLICY_SERVICE");
            }
        }
        return this.mDevicePolicyManager;
    }

    public final LockPatternUtils getLockPatternUtils() {
        LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
        return lockPatternUtils == null ? new LockPatternUtils(this.mContext) : lockPatternUtils;
    }

    public final TelecomManager getTelecomManager() {
        TelecomManager telecomManager = this.mTelecomManager;
        return telecomManager == null ? (TelecomManager) this.mContext.getSystemService(TelecomManager.class) : telecomManager;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "LockTaskController:");
        String str2 = str + "  ";
        printWriter.println(str2 + "mLockTaskModeState=" + lockTaskModeToString());
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("mLockTaskModeTasks=");
        printWriter.println(sb.toString());
        for (int i = 0; i < this.mLockTaskModeTasks.size(); i++) {
            printWriter.println(str2 + "  #" + i + " " + this.mLockTaskModeTasks.get(i));
        }
        printWriter.println(str2 + "mLockTaskPackages (userId:packages)=");
        for (int i2 = 0; i2 < this.mLockTaskPackages.size(); i2++) {
            printWriter.println(str2 + "  u" + this.mLockTaskPackages.keyAt(i2) + XmlUtils.STRING_ARRAY_SEPARATOR + Arrays.toString(this.mLockTaskPackages.valueAt(i2)));
        }
        printWriter.println();
    }

    public final String lockTaskModeToString() {
        int i = this.mLockTaskModeState;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return "unknown=" + this.mLockTaskModeState;
                }
                return "PINNED";
            }
            return "LOCKED";
        }
        return "NONE";
    }

    /* renamed from: com.android.server.wm.LockTaskController$LockTaskToken */
    /* loaded from: classes2.dex */
    public static class LockTaskToken extends Binder {
        public LockTaskToken() {
        }
    }
}
