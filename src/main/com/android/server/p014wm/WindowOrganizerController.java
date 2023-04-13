package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.WindowConfiguration;
import android.content.ActivityNotFoundException;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.window.IDisplayAreaOrganizerController;
import android.window.ITaskFragmentOrganizer;
import android.window.ITaskFragmentOrganizerController;
import android.window.ITaskOrganizerController;
import android.window.ITransitionMetricsReporter;
import android.window.ITransitionPlayer;
import android.window.IWindowContainerTransactionCallback;
import android.window.IWindowOrganizerController;
import android.window.TaskFragmentAnimationParams;
import android.window.TaskFragmentCreationParams;
import android.window.TaskFragmentOperation;
import android.window.WindowContainerTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.p011pm.LauncherAppsService;
import com.android.server.p014wm.BLASTSyncEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.WindowOrganizerController */
/* loaded from: classes2.dex */
public class WindowOrganizerController extends IWindowOrganizerController.Stub implements BLASTSyncEngine.TransactionReadyListener {
    public final DisplayAreaOrganizerController mDisplayAreaOrganizerController;
    public final WindowManagerGlobalLock mGlobalLock;
    public final ActivityTaskManagerService mService;
    public final TaskFragmentOrganizerController mTaskFragmentOrganizerController;
    public final TaskOrganizerController mTaskOrganizerController;
    public final TransitionController mTransitionController;
    public final HashMap<Integer, IWindowContainerTransactionCallback> mTransactionCallbacksByPendingSyncId = new HashMap<>();
    @VisibleForTesting
    final ArrayMap<IBinder, TaskFragment> mLaunchTaskFragments = new ArrayMap<>();
    public final Rect mTmpBounds0 = new Rect();
    public final Rect mTmpBounds1 = new Rect();

    /* renamed from: com.android.server.wm.WindowOrganizerController$CallerInfo */
    /* loaded from: classes2.dex */
    public static class CallerInfo {
        public final int mPid = Binder.getCallingPid();
        public final int mUid = Binder.getCallingUid();
    }

    public WindowOrganizerController(ActivityTaskManagerService activityTaskManagerService) {
        this.mService = activityTaskManagerService;
        this.mGlobalLock = activityTaskManagerService.mGlobalLock;
        this.mTaskOrganizerController = new TaskOrganizerController(activityTaskManagerService);
        this.mDisplayAreaOrganizerController = new DisplayAreaOrganizerController(activityTaskManagerService);
        this.mTaskFragmentOrganizerController = new TaskFragmentOrganizerController(activityTaskManagerService, this);
        this.mTransitionController = new TransitionController(activityTaskManagerService);
    }

    public TransitionController getTransitionController() {
        return this.mTransitionController;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            throw ActivityTaskManagerService.logAndRethrowRuntimeExceptionOnTransact("WindowOrganizerController", e);
        }
    }

    public void applyTransaction(WindowContainerTransaction windowContainerTransaction) {
        if (windowContainerTransaction == null) {
            throw new IllegalArgumentException("Null transaction passed to applyTransaction");
        }
        ActivityTaskManagerService.enforceTaskPermission("applyTransaction()");
        CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                applyTransaction(windowContainerTransaction, -1, null, callerInfo);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int applySyncTransaction(final WindowContainerTransaction windowContainerTransaction, IWindowContainerTransactionCallback iWindowContainerTransactionCallback) {
        if (windowContainerTransaction == null) {
            throw new IllegalArgumentException("Null transaction passed to applySyncTransaction");
        }
        ActivityTaskManagerService.enforceTaskPermission("applySyncTransaction()");
        final CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (iWindowContainerTransactionCallback == null) {
                    applyTransaction(windowContainerTransaction, -1, null, callerInfo);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -1;
                }
                final BLASTSyncEngine.SyncGroup prepareSyncWithOrganizer = prepareSyncWithOrganizer(iWindowContainerTransactionCallback);
                final int i = prepareSyncWithOrganizer.mSyncId;
                if (!this.mService.mWindowManager.mSyncEngine.hasActiveSync()) {
                    this.mService.mWindowManager.mSyncEngine.startSyncSet(prepareSyncWithOrganizer);
                    applyTransaction(windowContainerTransaction, i, null, callerInfo);
                    setSyncReady(i);
                } else {
                    this.mService.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WindowOrganizerController.this.lambda$applySyncTransaction$0(prepareSyncWithOrganizer);
                        }
                    }, new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            WindowOrganizerController.this.lambda$applySyncTransaction$1(windowContainerTransaction, i, callerInfo);
                        }
                    });
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applySyncTransaction$0(BLASTSyncEngine.SyncGroup syncGroup) {
        this.mService.mWindowManager.mSyncEngine.startSyncSet(syncGroup);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applySyncTransaction$1(WindowContainerTransaction windowContainerTransaction, int i, CallerInfo callerInfo) {
        applyTransaction(windowContainerTransaction, i, null, callerInfo);
        setSyncReady(i);
    }

    public IBinder startNewTransition(int i, WindowContainerTransaction windowContainerTransaction) {
        return startTransition(i, null, windowContainerTransaction);
    }

    public void startTransition(IBinder iBinder, WindowContainerTransaction windowContainerTransaction) {
        startTransition(-1, iBinder, windowContainerTransaction);
    }

    public final IBinder startTransition(int i, IBinder iBinder, WindowContainerTransaction windowContainerTransaction) {
        IBinder token;
        ActivityTaskManagerService.enforceTaskPermission("startTransition()");
        final CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Transition fromBinder = Transition.fromBinder(iBinder);
                if (this.mTransitionController.getTransitionPlayer() == null && fromBinder == null) {
                    Slog.w("WindowOrganizerController", "Using shell transitions API for legacy transitions.");
                    if (windowContainerTransaction == null) {
                        throw new IllegalArgumentException("Can't use legacy transitions in compatibility mode with no WCT.");
                    }
                    applyTransaction(windowContainerTransaction, -1, null, callerInfo);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                final boolean z = fromBinder == null && windowContainerTransaction != null;
                if (windowContainerTransaction == null) {
                    windowContainerTransaction = new WindowContainerTransaction();
                }
                final WindowContainerTransaction windowContainerTransaction2 = windowContainerTransaction;
                if (fromBinder == null) {
                    if (i < 0) {
                        throw new IllegalArgumentException("Can't create transition with no type");
                    }
                    if (this.mService.mWindowManager.mSyncEngine.hasActiveSync()) {
                        Slog.w("WindowOrganizerController", "startTransition() while one is already collecting.");
                        final Transition transition = new Transition(i, 0, this.mTransitionController, this.mService.mWindowManager.mSyncEngine);
                        if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 1667162379, 0, (String) null, new Object[]{String.valueOf(transition)});
                        }
                        this.mService.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                WindowOrganizerController.this.lambda$startTransition$2(transition);
                            }
                        }, new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda3
                            @Override // java.lang.Runnable
                            public final void run() {
                                WindowOrganizerController.this.lambda$startTransition$3(transition, windowContainerTransaction2, callerInfo, z);
                            }
                        });
                        token = transition.getToken();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return token;
                    }
                    fromBinder = this.mTransitionController.createTransition(i);
                }
                if (!fromBinder.isCollecting() && !fromBinder.isForcePlaying()) {
                    Slog.e("WindowOrganizerController", "Trying to start a transition that isn't collecting. This probably means Shell took too long to respond to a request. WM State may be incorrect now, please file a bug");
                    applyTransaction(windowContainerTransaction2, -1, null, callerInfo);
                    token = fromBinder.getToken();
                } else {
                    fromBinder.start();
                    fromBinder.mLogger.mStartWCT = windowContainerTransaction2;
                    applyTransaction(windowContainerTransaction2, -1, fromBinder, callerInfo);
                    if (z) {
                        fromBinder.setAllReady();
                    }
                    token = fromBinder.getToken();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return token;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startTransition$2(Transition transition) {
        this.mTransitionController.moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startTransition$3(Transition transition, WindowContainerTransaction windowContainerTransaction, CallerInfo callerInfo, boolean z) {
        transition.start();
        applyTransaction(windowContainerTransaction, -1, transition, callerInfo);
        if (z) {
            transition.setAllReady();
        }
    }

    /* JADX WARN: Finally extract failed */
    public int startLegacyTransition(int i, RemoteAnimationAdapter remoteAnimationAdapter, IWindowContainerTransactionCallback iWindowContainerTransactionCallback, WindowContainerTransaction windowContainerTransaction) {
        ActivityTaskManagerService.enforceTaskPermission("startLegacyTransition()");
        CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (i < 0) {
                    throw new IllegalArgumentException("Can't create transition with no type");
                }
                if (this.mTransitionController.getTransitionPlayer() != null) {
                    throw new IllegalArgumentException("Can't use legacy transitions in when shell transitions are enabled.");
                }
                DisplayContent displayContent = this.mService.mRootWindowContainer.getDisplayContent(0);
                if (!displayContent.mAppTransition.isTransitionSet()) {
                    remoteAnimationAdapter.setCallingPidUid(callerInfo.mPid, callerInfo.mUid);
                    displayContent.prepareAppTransition(i);
                    displayContent.mAppTransition.overridePendingAppTransitionRemote(remoteAnimationAdapter, true, false);
                    int startSyncWithOrganizer = startSyncWithOrganizer(iWindowContainerTransactionCallback);
                    applyTransaction(windowContainerTransaction, startSyncWithOrganizer, null, callerInfo);
                    setSyncReady(startSyncWithOrganizer);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return startSyncWithOrganizer;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return -1;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public int finishTransition(IBinder iBinder, WindowContainerTransaction windowContainerTransaction, IWindowContainerTransactionCallback iWindowContainerTransactionCallback) {
        int startSyncWithOrganizer;
        ActivityTaskManagerService.enforceTaskPermission("finishTransition()");
        CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                startSyncWithOrganizer = (windowContainerTransaction == null || iWindowContainerTransactionCallback == null) ? -1 : startSyncWithOrganizer(iWindowContainerTransactionCallback);
                Transition fromBinder = Transition.fromBinder(iBinder);
                if (windowContainerTransaction != null) {
                    applyTransaction(windowContainerTransaction, startSyncWithOrganizer, null, callerInfo, fromBinder);
                }
                getTransitionController().finishTransition(iBinder);
                if (startSyncWithOrganizer >= 0) {
                    setSyncReady(startSyncWithOrganizer);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startSyncWithOrganizer;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void applyTaskFragmentTransactionLocked(final WindowContainerTransaction windowContainerTransaction, int i, boolean z) {
        ITaskFragmentOrganizer taskFragmentOrganizer = windowContainerTransaction.getTaskFragmentOrganizer();
        Objects.requireNonNull(taskFragmentOrganizer);
        enforceTaskFragmentOrganizerPermission("applyTaskFragmentTransaction()", taskFragmentOrganizer, windowContainerTransaction);
        final CallerInfo callerInfo = new CallerInfo();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mTransitionController.getTransitionPlayer() == null) {
                applyTransaction(windowContainerTransaction, -1, null, callerInfo);
            } else if (!this.mService.mWindowManager.mSyncEngine.hasActiveSync()) {
                Transition createTransition = this.mTransitionController.createTransition(i);
                applyTransaction(windowContainerTransaction, -1, createTransition, callerInfo);
                this.mTransitionController.requestStartTransition(createTransition, null, null, null);
                createTransition.setAllReady();
            } else if (z) {
                final Transition transition = new Transition(i, 0, this.mTransitionController, this.mService.mWindowManager.mSyncEngine);
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 286170861, 0, (String) null, new Object[]{String.valueOf(transition)});
                }
                this.mService.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        WindowOrganizerController.this.lambda$applyTaskFragmentTransactionLocked$4(transition);
                    }
                }, new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        WindowOrganizerController.this.lambda$applyTaskFragmentTransactionLocked$5(windowContainerTransaction, transition, callerInfo);
                    }
                });
            } else {
                Transition collectingTransition = this.mTransitionController.getCollectingTransition();
                if (collectingTransition == null && ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -353495930, 0, (String) null, (Object[]) null);
                }
                applyTransaction(windowContainerTransaction, -1, collectingTransition, callerInfo);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyTaskFragmentTransactionLocked$4(Transition transition) {
        this.mTransitionController.moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyTaskFragmentTransactionLocked$5(WindowContainerTransaction windowContainerTransaction, Transition transition, CallerInfo callerInfo) {
        if (this.mTaskFragmentOrganizerController.isValidTransaction(windowContainerTransaction)) {
            applyTransaction(windowContainerTransaction, -1, transition, callerInfo);
            this.mTransitionController.requestStartTransition(transition, null, null, null);
            transition.setAllReady();
            return;
        }
        transition.abort();
    }

    public final void applyTransaction(WindowContainerTransaction windowContainerTransaction, int i, Transition transition, CallerInfo callerInfo) {
        applyTransaction(windowContainerTransaction, i, transition, callerInfo, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v3 */
    /* JADX WARN: Type inference failed for: r12v4, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r12v5 */
    public final void applyTransaction(WindowContainerTransaction windowContainerTransaction, int i, Transition transition, CallerInfo callerInfo, Transition transition2) {
        boolean z;
        String str;
        String str2;
        String str3;
        String str4;
        ArraySet arraySet;
        ?? r12;
        boolean z2;
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 906215061, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        this.mService.deferWindowLayout();
        this.mService.mTaskSupervisor.setDeferRootVisibilityUpdate(true);
        boolean z3 = false;
        if (transition != null) {
            try {
                transition.applyDisplayChangeIfNeeded();
            } catch (Throwable th) {
                th = th;
                z = z3;
                this.mService.mTaskSupervisor.setDeferRootVisibilityUpdate(z);
                this.mService.continueWindowLayout();
                throw th;
            }
        }
        List hierarchyOps = windowContainerTransaction.getHierarchyOps();
        int size = hierarchyOps.size();
        ArraySet arraySet2 = new ArraySet();
        Iterator it = windowContainerTransaction.getChanges().entrySet().iterator();
        int i2 = 0;
        while (true) {
            str = "Attempt to operate on detached container: ";
            str2 = "WindowOrganizerController";
            if (!it.hasNext()) {
                break;
            }
            Map.Entry entry = (Map.Entry) it.next();
            WindowContainer fromBinder = WindowContainer.fromBinder((IBinder) entry.getKey());
            if (fromBinder != null && fromBinder.isAttached()) {
                if (i >= 0) {
                    addToSyncSet(i, fromBinder);
                }
                if (transition != null) {
                    transition.collect(fromBinder);
                }
                if ((((WindowContainerTransaction.Change) entry.getValue()).getChangeMask() & 64) != 0) {
                    if (transition2 != null) {
                        transition2.setCanPipOnFinish(false);
                    } else if (transition != null) {
                        transition.setCanPipOnFinish(false);
                    }
                }
                if (fromBinder.asTask() != null && fromBinder.inPinnedWindowingMode() && ((WindowContainerTransaction.Change) entry.getValue()).getWindowingMode() == 0) {
                    z2 = false;
                    for (int i3 = 0; i3 < size; i3++) {
                        WindowContainerTransaction.HierarchyOp hierarchyOp = (WindowContainerTransaction.HierarchyOp) hierarchyOps.get(i3);
                        if (hierarchyOp.getType() == 1 && fromBinder.equals(WindowContainer.fromBinder(hierarchyOp.getContainer()))) {
                            z2 = !hierarchyOp.getToTop();
                        }
                    }
                } else {
                    z2 = false;
                }
                if (z2) {
                    fromBinder.asTask().setForceHidden(1, true);
                }
                int applyWindowContainerChange = applyWindowContainerChange(fromBinder, (WindowContainerTransaction.Change) entry.getValue(), windowContainerTransaction.getErrorCallbackToken());
                i2 |= applyWindowContainerChange;
                if (z2) {
                    fromBinder.asTask().setForceHidden(1, false);
                }
                if ((i2 & 2) == 0 && (applyWindowContainerChange & 1) != 0) {
                    arraySet2.add(fromBinder);
                }
            }
            Slog.e("WindowOrganizerController", "Attempt to operate on detached container: " + fromBinder);
        }
        if (size > 0) {
            boolean isInLockTaskMode = this.mService.isInLockTaskMode();
            int i4 = i2;
            int i5 = 0;
            while (i5 < size) {
                int i6 = i5;
                String str5 = str2;
                String str6 = str;
                ArraySet arraySet3 = arraySet2;
                int i7 = size;
                List list = hierarchyOps;
                z = z3;
                try {
                    i4 |= applyHierarchyOp((WindowContainerTransaction.HierarchyOp) hierarchyOps.get(i5), i4, i, transition, isInLockTaskMode, callerInfo, windowContainerTransaction.getErrorCallbackToken(), windowContainerTransaction.getTaskFragmentOrganizer(), transition2);
                    i5 = i6 + 1;
                    str = str6;
                    z3 = z;
                    str2 = str5;
                    hierarchyOps = list;
                    arraySet2 = arraySet3;
                    size = i7;
                } catch (Throwable th2) {
                    th = th2;
                    this.mService.mTaskSupervisor.setDeferRootVisibilityUpdate(z);
                    this.mService.continueWindowLayout();
                    throw th;
                }
            }
            str3 = str2;
            str4 = str;
            arraySet = arraySet2;
            r12 = z3;
            i2 = i4;
        } else {
            str3 = "WindowOrganizerController";
            str4 = "Attempt to operate on detached container: ";
            arraySet = arraySet2;
            r12 = 0;
        }
        for (Map.Entry entry2 : windowContainerTransaction.getChanges().entrySet()) {
            WindowContainer fromBinder2 = WindowContainer.fromBinder((IBinder) entry2.getKey());
            if (fromBinder2 != null && fromBinder2.isAttached()) {
                Task asTask = fromBinder2.asTask();
                Rect boundsChangeSurfaceBounds = ((WindowContainerTransaction.Change) entry2.getValue()).getBoundsChangeSurfaceBounds();
                if (asTask != null && asTask.isAttached() && boundsChangeSurfaceBounds != null) {
                    if (!asTask.isOrganized()) {
                        Task asTask2 = asTask.getParent() != null ? asTask.getParent().asTask() : null;
                        if (asTask2 == null || !asTask2.mCreatedByOrganizer) {
                            throw new IllegalArgumentException("Can't manipulate non-organized task surface " + asTask);
                        }
                    }
                    SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                    SurfaceControl surfaceControl = asTask.getSurfaceControl();
                    transaction.setPosition(surfaceControl, boundsChangeSurfaceBounds.left, boundsChangeSurfaceBounds.top);
                    if (boundsChangeSurfaceBounds.isEmpty()) {
                        transaction.setWindowCrop(surfaceControl, null);
                    } else {
                        transaction.setWindowCrop(surfaceControl, boundsChangeSurfaceBounds.width(), boundsChangeSurfaceBounds.height());
                    }
                    asTask.setMainWindowSizeChangeTransaction(transaction);
                }
            }
            Slog.e(str3, str4 + fromBinder2);
        }
        if ((i2 & 2) != 0) {
            this.mService.mTaskSupervisor.setDeferRootVisibilityUpdate(r12);
            this.mService.mRootWindowContainer.ensureActivitiesVisible(null, r12, true);
            this.mService.mRootWindowContainer.resumeFocusedTasksTopActivities();
        } else if ((i2 & 1) != 0) {
            int size2 = arraySet.size() - 1;
            while (size2 >= 0) {
                ArraySet arraySet4 = arraySet;
                ((WindowContainer) arraySet4.valueAt(size2)).forAllActivities(new Consumer() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((ActivityRecord) obj).ensureActivityConfiguration(0, true);
                    }
                });
                size2--;
                arraySet = arraySet4;
            }
        }
        if (i2 != 0) {
            this.mService.mWindowManager.mWindowPlacerLocked.requestTraversal();
        }
        this.mService.mTaskSupervisor.setDeferRootVisibilityUpdate(r12);
        this.mService.continueWindowLayout();
    }

    public final int applyChanges(WindowContainer<?> windowContainer, WindowContainerTransaction.Change change) {
        int configSetMask = change.getConfigSetMask() & 536882176;
        int i = 3;
        int windowSetMask = change.getWindowSetMask() & 3;
        int windowingMode = change.getWindowingMode();
        if (configSetMask != 0) {
            if (windowingMode > -1 && windowingMode != windowContainer.getWindowingMode()) {
                windowContainer.getRequestedOverrideConfiguration().setTo(change.getConfiguration(), configSetMask, windowSetMask);
            } else {
                Configuration configuration = new Configuration(windowContainer.getRequestedOverrideConfiguration());
                configuration.setTo(change.getConfiguration(), configSetMask, windowSetMask);
                windowContainer.onRequestedOverrideConfigurationChanged(configuration);
            }
            if (windowSetMask == 0 || !windowContainer.isEmbedded()) {
                i = 1;
            }
        } else {
            i = 0;
        }
        if ((change.getChangeMask() & 1) != 0 && windowContainer.setFocusable(change.getFocusable())) {
            i |= 2;
        }
        int windowingMode2 = windowContainer.getWindowingMode();
        if (windowingMode <= -1 || windowingMode2 == windowingMode) {
            return i;
        }
        if (this.mService.isInLockTaskMode() && WindowConfiguration.inMultiWindowMode(windowingMode)) {
            throw new UnsupportedOperationException("Not supported to set multi-window windowing mode during locked task mode.");
        }
        if (windowingMode == 2) {
            return i;
        }
        windowContainer.setWindowingMode(windowingMode);
        return windowingMode2 != windowContainer.getWindowingMode() ? i | 2 : i;
    }

    public final int applyTaskChanges(Task task, WindowContainerTransaction.Change change) {
        ActivityRecord topNonFinishingActivity;
        int applyChanges = applyChanges(task, change);
        SurfaceControl.Transaction boundsChangeTransaction = change.getBoundsChangeTransaction();
        if ((change.getChangeMask() & 8) != 0 && task.setForceHidden(2, change.getHidden())) {
            applyChanges = 2;
        }
        if ((change.getChangeMask() & 128) != 0) {
            task.setForceTranslucent(change.getForceTranslucent());
            applyChanges = 2;
        }
        if ((change.getChangeMask() & 256) != 0) {
            task.setDragResizing(change.getDragResizing());
        }
        final int activityWindowingMode = change.getActivityWindowingMode();
        if (activityWindowingMode > -1) {
            task.forAllActivities(new Consumer() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ActivityRecord) obj).setWindowingMode(activityWindowingMode);
                }
            });
        }
        if (boundsChangeTransaction != null) {
            task.setMainWindowSizeChangeTransaction(boundsChangeTransaction);
        }
        Rect enterPipBounds = change.getEnterPipBounds();
        if (enterPipBounds != null) {
            task.mDisplayContent.mPinnedTaskController.setEnterPipBounds(enterPipBounds);
        }
        if (change.getWindowingMode() == 2 && !task.inPinnedWindowingMode() && (topNonFinishingActivity = task.getTopNonFinishingActivity()) != null) {
            boolean z = topNonFinishingActivity.supportsEnterPipOnTaskSwitch;
            topNonFinishingActivity.supportsEnterPipOnTaskSwitch = true;
            boolean checkEnterPictureInPictureState = topNonFinishingActivity.checkEnterPictureInPictureState("applyTaskChanges", true);
            if (checkEnterPictureInPictureState) {
                checkEnterPictureInPictureState = this.mService.mActivityClientController.requestPictureInPictureMode(topNonFinishingActivity);
            }
            if (!checkEnterPictureInPictureState) {
                topNonFinishingActivity.supportsEnterPipOnTaskSwitch = z;
            }
        }
        return applyChanges;
    }

    public final int applyDisplayAreaChanges(DisplayArea displayArea, final WindowContainerTransaction.Change change) {
        final int[] iArr = {applyChanges(displayArea, change)};
        if ((change.getChangeMask() & 32) != 0 && displayArea.setIgnoreOrientationRequest(change.getIgnoreOrientationRequest())) {
            iArr[0] = iArr[0] | 2;
        }
        displayArea.forAllTasks(new Consumer() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WindowOrganizerController.lambda$applyDisplayAreaChanges$8(change, iArr, obj);
            }
        });
        return iArr[0];
    }

    public static /* synthetic */ void lambda$applyDisplayAreaChanges$8(WindowContainerTransaction.Change change, int[] iArr, Object obj) {
        Task task = (Task) obj;
        if ((change.getChangeMask() & 8) == 0 || !task.setForceHidden(2, change.getHidden())) {
            return;
        }
        iArr[0] = iArr[0] | 2;
    }

    public final int applyTaskFragmentChanges(TaskFragment taskFragment, WindowContainerTransaction.Change change, IBinder iBinder) {
        if (taskFragment.isEmbeddedTaskFragmentInPip()) {
            return 0;
        }
        this.mTmpBounds0.set(taskFragment.getBounds());
        this.mTmpBounds1.set(taskFragment.getRelativeEmbeddedBounds());
        taskFragment.deferOrganizedTaskFragmentSurfaceUpdate();
        Rect relativeBounds = change.getRelativeBounds();
        if (relativeBounds != null) {
            adjustTaskFragmentRelativeBoundsForMinDimensionsIfNeeded(taskFragment, relativeBounds, iBinder);
            change.getConfiguration().windowConfiguration.setBounds(taskFragment.translateRelativeBoundsToAbsoluteBounds(relativeBounds, taskFragment.getParent().getBounds()));
            taskFragment.setRelativeEmbeddedBounds(relativeBounds);
        }
        int applyChanges = applyChanges(taskFragment, change);
        if (taskFragment.shouldStartChangeTransition(this.mTmpBounds0, this.mTmpBounds1)) {
            taskFragment.initializeChangeTransition(this.mTmpBounds0);
        }
        taskFragment.continueOrganizedTaskFragmentSurfaceUpdate();
        return applyChanges;
    }

    public final void adjustTaskFragmentRelativeBoundsForMinDimensionsIfNeeded(TaskFragment taskFragment, Rect rect, IBinder iBinder) {
        if (rect.isEmpty()) {
            return;
        }
        Point calculateMinDimension = taskFragment.calculateMinDimension();
        if (rect.width() < calculateMinDimension.x || rect.height() < calculateMinDimension.y) {
            sendTaskFragmentOperationFailure(taskFragment.getTaskFragmentOrganizer(), iBinder, taskFragment, 9, new SecurityException("The requested relative bounds:" + rect + " does not satisfy minimum dimensions:" + calculateMinDimension));
            rect.setEmpty();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:147:0x0376  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0208  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int applyHierarchyOp(final WindowContainerTransaction.HierarchyOp hierarchyOp, int i, int i2, Transition transition, boolean z, final CallerInfo callerInfo, IBinder iBinder, ITaskFragmentOrganizer iTaskFragmentOrganizer, Transition transition2) {
        int applyTaskFragmentOperation;
        int i3;
        ActivityOptions activityOptions;
        WindowContainer fromBinder;
        Task transientLaunchRestoreTarget;
        int type = hierarchyOp.getType();
        if (type != 17) {
            switch (type) {
                case 0:
                case 1:
                    WindowContainer fromBinder2 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                    if (fromBinder2 == null || !fromBinder2.isAttached()) {
                        Slog.e("WindowOrganizerController", "Attempt to operate on detached container: " + fromBinder2);
                    } else if (z && type == 0) {
                        Slog.w("WindowOrganizerController", "Skip applying hierarchy operation " + hierarchyOp + " while in lock task mode");
                    } else if (!isLockTaskModeViolation(fromBinder2.getParent(), fromBinder2.asTask(), z)) {
                        if (i2 >= 0) {
                            addToSyncSet(i2, fromBinder2);
                        }
                        if (transition != null) {
                            transition.collect(fromBinder2);
                            if (hierarchyOp.isReparent()) {
                                if (fromBinder2.getParent() != null) {
                                    transition.collect(fromBinder2.getParent());
                                }
                                if (hierarchyOp.getNewParent() != null) {
                                    WindowContainer fromBinder3 = WindowContainer.fromBinder(hierarchyOp.getNewParent());
                                    if (fromBinder3 == null) {
                                        Slog.e("WindowOrganizerController", "Can't resolve parent window from token");
                                    } else {
                                        transition.collect(fromBinder3);
                                    }
                                }
                            }
                        }
                        applyTaskFragmentOperation = sanitizeAndApplyHierarchyOp(fromBinder2, hierarchyOp);
                        break;
                    }
                    i3 = i;
                    break;
                case 2:
                    applyTaskFragmentOperation = reparentChildrenTasksHierarchyOp(hierarchyOp, transition, i2, z);
                    break;
                case 3:
                    WindowContainer fromBinder4 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                    Task asTask = fromBinder4 != null ? fromBinder4.asTask() : null;
                    if (asTask != null) {
                        asTask.getDisplayArea().setLaunchRootTask(asTask, hierarchyOp.getWindowingModes(), hierarchyOp.getActivityTypes());
                        i3 = i;
                        break;
                    } else {
                        throw new IllegalArgumentException("Cannot set non-task as launch root: " + fromBinder4);
                    }
                case 4:
                    applyTaskFragmentOperation = setAdjacentRootsHierarchyOp(hierarchyOp);
                    break;
                case 5:
                    this.mService.mAmInternal.enforceCallingPermission("android.permission.START_TASKS_FROM_RECENTS", "launchTask HierarchyOp");
                    Bundle launchOptions = hierarchyOp.getLaunchOptions();
                    final int i4 = launchOptions.getInt("android:transaction.hop.taskId");
                    launchOptions.remove("android:transaction.hop.taskId");
                    final SafeActivityOptions fromBundle = SafeActivityOptions.fromBundle(launchOptions, callerInfo.mPid, callerInfo.mUid);
                    waitAsyncStart(new IntSupplier() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda8
                        @Override // java.util.function.IntSupplier
                        public final int getAsInt() {
                            int lambda$applyHierarchyOp$9;
                            lambda$applyHierarchyOp$9 = WindowOrganizerController.this.lambda$applyHierarchyOp$9(callerInfo, i4, fromBundle);
                            return lambda$applyHierarchyOp$9;
                        }
                    });
                    i3 = i;
                    break;
                case 6:
                    WindowContainer fromBinder5 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                    Task asTask2 = fromBinder5 != null ? fromBinder5.asTask() : null;
                    boolean toTop = hierarchyOp.getToTop();
                    if (asTask2 == null) {
                        throw new IllegalArgumentException("Cannot set non-task as launch root: " + fromBinder5);
                    } else if (!asTask2.mCreatedByOrganizer) {
                        throw new UnsupportedOperationException("Cannot set non-organized task as adjacent flag root: " + fromBinder5);
                    } else if (asTask2.getAdjacentTaskFragment() == null && !toTop) {
                        throw new UnsupportedOperationException("Cannot set non-adjacent task as adjacent flag root: " + fromBinder5);
                    } else {
                        TaskDisplayArea displayArea = asTask2.getDisplayArea();
                        if (toTop) {
                            asTask2 = null;
                        }
                        displayArea.setLaunchAdjacentFlagRootTask(asTask2);
                        i3 = i;
                        break;
                    }
                default:
                    switch (type) {
                        case 13:
                            WindowContainer fromBinder6 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                            (fromBinder6 != null ? fromBinder6.asTask() : null).remove(true, "Applying remove task Hierarchy Op");
                            i3 = i;
                            break;
                        case 14:
                            ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(hierarchyOp.getContainer());
                            if (forTokenLocked != null && !forTokenLocked.finishing) {
                                if (forTokenLocked.isVisible() || forTokenLocked.isVisibleRequested()) {
                                    forTokenLocked.finishIfPossible("finish-activity-op", false);
                                } else {
                                    forTokenLocked.destroyIfPossible("finish-activity-op");
                                }
                            }
                            i3 = i;
                            break;
                        case 15:
                            applyTaskFragmentOperation = clearAdjacentRootsHierarchyOp(hierarchyOp);
                            break;
                        default:
                            if (z) {
                                Slog.w("WindowOrganizerController", "Skip applying hierarchy operation " + hierarchyOp + " while in lock task mode");
                                return i;
                            }
                            i3 = i;
                            break;
                    }
            }
            if (type == 16) {
                switch (type) {
                    case 7:
                        final String resolveTypeIfNeeded = hierarchyOp.getActivityIntent() != null ? hierarchyOp.getActivityIntent().resolveTypeIfNeeded(this.mService.mContext.getContentResolver()) : null;
                        if (hierarchyOp.getPendingIntent().isActivity()) {
                            if (hierarchyOp.getLaunchOptions() != null) {
                                activityOptions = new ActivityOptions(hierarchyOp.getLaunchOptions());
                            } else {
                                activityOptions = ActivityOptions.makeBasic();
                            }
                            activityOptions.setCallerDisplayId(0);
                        } else {
                            activityOptions = null;
                        }
                        final Bundle bundle = activityOptions != null ? activityOptions.toBundle() : null;
                        if (!ActivityManager.isStartResultSuccessful(waitAsyncStart(new IntSupplier() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda9
                            @Override // java.util.function.IntSupplier
                            public final int getAsInt() {
                                int lambda$applyHierarchyOp$10;
                                lambda$applyHierarchyOp$10 = WindowOrganizerController.this.lambda$applyHierarchyOp$10(hierarchyOp, resolveTypeIfNeeded, bundle);
                                return lambda$applyHierarchyOp$10;
                            }
                        }))) {
                            return i3;
                        }
                        break;
                    case 8:
                        Bundle launchOptions2 = hierarchyOp.getLaunchOptions();
                        String string = launchOptions2.getString("android:transaction.hop.shortcut_calling_package");
                        launchOptions2.remove("android:transaction.hop.shortcut_calling_package");
                        if (!((LauncherAppsService.LauncherAppsServiceInternal) LocalServices.getService(LauncherAppsService.LauncherAppsServiceInternal.class)).startShortcut(callerInfo.mUid, callerInfo.mPid, string, hierarchyOp.getShortcutInfo().getPackage(), null, hierarchyOp.getShortcutInfo().getId(), null, launchOptions2, hierarchyOp.getShortcutInfo().getUserId())) {
                            return i3;
                        }
                        break;
                    case 9:
                        if (transition2 == null || (fromBinder = WindowContainer.fromBinder(hierarchyOp.getContainer())) == null) {
                            return i3;
                        }
                        Task task = fromBinder.asActivityRecord() != null ? fromBinder.asActivityRecord().getTask() : fromBinder.asTask();
                        if (task == null || (transientLaunchRestoreTarget = transition2.getTransientLaunchRestoreTarget(fromBinder)) == null) {
                            return i3;
                        }
                        task.getTaskDisplayArea().moveRootTaskBehindRootTask(task.getRootTask(), transientLaunchRestoreTarget);
                        return i3;
                    case 10:
                        Rect insetsProviderFrame = hierarchyOp.getInsetsProviderFrame();
                        WindowContainer fromBinder7 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                        if (fromBinder7 == null) {
                            Slog.e("WindowOrganizerController", "Attempt to add local insets source provider on unknown: " + fromBinder7);
                            return i3;
                        }
                        fromBinder7.addLocalRectInsetsSourceProvider(insetsProviderFrame, hierarchyOp.getInsetsTypes());
                        return i3;
                    case 11:
                        WindowContainer fromBinder8 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                        if (fromBinder8 == null) {
                            Slog.e("WindowOrganizerController", "Attempt to remove local insets source provider from unknown: " + fromBinder8);
                            return i3;
                        }
                        fromBinder8.removeLocalInsetsSourceProvider(hierarchyOp.getInsetsTypes());
                        return i3;
                    case 12:
                        WindowContainer fromBinder9 = WindowContainer.fromBinder(hierarchyOp.getContainer());
                        if (fromBinder9 == null || fromBinder9.asDisplayArea() == null || !fromBinder9.isAttached()) {
                            Slog.e("WindowOrganizerController", "Attempt to operate on unknown or detached display area: " + fromBinder9);
                            return i3;
                        }
                        fromBinder9.setAlwaysOnTop(hierarchyOp.isAlwaysOnTop());
                        break;
                    default:
                        return i3;
                }
                return i3 | 2;
            }
            WindowContainer fromBinder10 = WindowContainer.fromBinder(hierarchyOp.getContainer());
            Task asTask3 = fromBinder10 != null ? fromBinder10.asTask() : null;
            if (asTask3 == null || !asTask3.isAttached()) {
                Slog.e("WindowOrganizerController", "Attempt to operate on unknown or detached container: " + fromBinder10);
                return i3;
            } else if (!asTask3.mCreatedByOrganizer) {
                throw new UnsupportedOperationException("Cannot set reparent leaf task flag on non-organized task : " + asTask3);
            } else if (!asTask3.isRootTask()) {
                throw new UnsupportedOperationException("Cannot set reparent leaf task flag on non-root task : " + asTask3);
            } else {
                asTask3.setReparentLeafTaskIfRelaunch(hierarchyOp.isReparentLeafTaskIfRelaunch());
                return i3;
            }
        }
        applyTaskFragmentOperation = applyTaskFragmentOperation(hierarchyOp, transition, z, callerInfo, iBinder, iTaskFragmentOrganizer);
        i3 = i | applyTaskFragmentOperation;
        if (type == 16) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$applyHierarchyOp$9(CallerInfo callerInfo, int i, SafeActivityOptions safeActivityOptions) {
        return this.mService.mTaskSupervisor.startActivityFromRecents(callerInfo.mPid, callerInfo.mUid, i, safeActivityOptions);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$applyHierarchyOp$10(WindowContainerTransaction.HierarchyOp hierarchyOp, String str, Bundle bundle) {
        return this.mService.mAmInternal.sendIntentSender(hierarchyOp.getPendingIntent().getTarget(), hierarchyOp.getPendingIntent().getWhitelistToken(), 0, hierarchyOp.getActivityIntent(), str, (IIntentReceiver) null, (String) null, bundle);
    }

    public final int applyTaskFragmentOperation(WindowContainerTransaction.HierarchyOp hierarchyOp, Transition transition, boolean z, CallerInfo callerInfo, IBinder iBinder, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        ActivityRecord activity;
        boolean z2 = false;
        if (validateTaskFragmentOperation(hierarchyOp, iBinder, iTaskFragmentOrganizer)) {
            TaskFragment taskFragment = this.mLaunchTaskFragments.get(hierarchyOp.getContainer());
            TaskFragmentOperation taskFragmentOperation = hierarchyOp.getTaskFragmentOperation();
            int opType = taskFragmentOperation.getOpType();
            int i = 2;
            switch (opType) {
                case 0:
                    TaskFragmentCreationParams taskFragmentCreationParams = taskFragmentOperation.getTaskFragmentCreationParams();
                    if (taskFragmentCreationParams == null) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new IllegalArgumentException("TaskFragmentCreationParams must be non-null"));
                        return 0;
                    }
                    createTaskFragment(taskFragmentCreationParams, iBinder, callerInfo, transition);
                    return 0;
                case 1:
                    if (z && (activity = taskFragment.getActivity(new Predicate() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda12
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$applyTaskFragmentOperation$11;
                            lambda$applyTaskFragmentOperation$11 = WindowOrganizerController.lambda$applyTaskFragmentOperation$11((ActivityRecord) obj);
                            return lambda$applyTaskFragmentOperation$11;
                        }
                    }, false)) != null && this.mService.getLockTaskController().activityBlockedFromFinish(activity)) {
                        Slog.w("WindowOrganizerController", "Skip removing TaskFragment due in lock task mode.");
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new IllegalStateException("Not allow to delete task fragment in lock task mode."));
                        return 0;
                    }
                    return 0 | deleteTaskFragment(taskFragment, transition);
                case 2:
                    IBinder activityToken = taskFragmentOperation.getActivityToken();
                    Intent activityIntent = taskFragmentOperation.getActivityIntent();
                    int startActivityInTaskFragment = this.mService.getActivityStartController().startActivityInTaskFragment(taskFragment, activityIntent, taskFragmentOperation.getBundle(), activityToken, callerInfo.mUid, callerInfo.mPid, iBinder);
                    if (!ActivityManager.isStartResultSuccessful(startActivityInTaskFragment)) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, convertStartFailureToThrowable(startActivityInTaskFragment, activityIntent));
                        return 0;
                    }
                    break;
                case 3:
                    IBinder activityToken2 = taskFragmentOperation.getActivityToken();
                    ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(activityToken2);
                    if (forTokenLocked == null) {
                        forTokenLocked = this.mTaskFragmentOrganizerController.getReparentActivityFromTemporaryToken(iTaskFragmentOrganizer, activityToken2);
                    }
                    if (forTokenLocked == null) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new IllegalArgumentException("Not allowed to operate with invalid activity."));
                        return 0;
                    } else if (taskFragment.isAllowedToEmbedActivity(forTokenLocked) != 0) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new SecurityException("The task fragment is not allowed to embed the given activity."));
                        return 0;
                    } else if (taskFragment.getTask() != forTokenLocked.getTask()) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new SecurityException("The reparented activity is not in the same Task as the target TaskFragment."));
                        return 0;
                    } else {
                        if (transition != null) {
                            transition.collect(forTokenLocked);
                            if (forTokenLocked.getParent() != null) {
                                transition.collect(forTokenLocked.getParent());
                            }
                            transition.collect(taskFragment);
                        }
                        forTokenLocked.reparent(taskFragment, Integer.MAX_VALUE);
                        break;
                    }
                case 4:
                    TaskFragment taskFragment2 = this.mLaunchTaskFragments.get(taskFragmentOperation.getSecondaryFragmentToken());
                    if (taskFragment2 == null) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new IllegalArgumentException("SecondaryFragmentToken must be set for setAdjacentTaskFragments."));
                        return 0;
                    }
                    if (taskFragment.getAdjacentTaskFragment() != taskFragment2) {
                        taskFragment.setAdjacentTaskFragment(taskFragment2);
                    } else {
                        i = 0;
                    }
                    Bundle launchOptions = hierarchyOp.getLaunchOptions();
                    TaskFragment taskFragmentAdjacentParams = launchOptions != null ? new WindowContainerTransaction.TaskFragmentAdjacentParams(launchOptions) : null;
                    taskFragment.setDelayLastActivityRemoval(taskFragmentAdjacentParams != null && taskFragmentAdjacentParams.shouldDelayPrimaryLastActivityRemoval());
                    if (taskFragmentAdjacentParams != null && taskFragmentAdjacentParams.shouldDelaySecondaryLastActivityRemoval()) {
                        z2 = true;
                    }
                    taskFragment2.setDelayLastActivityRemoval(z2);
                    break;
                    break;
                case 5:
                    TaskFragment adjacentTaskFragment = taskFragment.getAdjacentTaskFragment();
                    if (adjacentTaskFragment != null) {
                        taskFragment.resetAdjacentTaskFragment();
                        ActivityRecord activityRecord = taskFragment.getDisplayContent().mFocusedApp;
                        TaskFragment taskFragment3 = activityRecord != null ? activityRecord.getTaskFragment() : null;
                        if ((taskFragment3 == taskFragment || taskFragment3 == adjacentTaskFragment) && !taskFragment3.shouldBeVisible(null)) {
                            taskFragment3.getDisplayContent().setFocusedApp(null);
                            break;
                        }
                    } else {
                        return 0;
                    }
                    break;
                case 6:
                    ActivityRecord activityRecord2 = taskFragment.getDisplayContent().mFocusedApp;
                    if (activityRecord2 != null && activityRecord2.getTaskFragment() == taskFragment) {
                        Slog.d("WindowOrganizerController", "The requested TaskFragment already has the focus.");
                        return 0;
                    } else if (activityRecord2 != null && activityRecord2.getTask() != taskFragment.getTask()) {
                        Slog.d("WindowOrganizerController", "The Task of the requested TaskFragment doesn't have focus.");
                        return 0;
                    } else {
                        ActivityRecord topResumedActivity = taskFragment.getTopResumedActivity();
                        if (topResumedActivity == null) {
                            Slog.d("WindowOrganizerController", "There is no resumed activity in the requested TaskFragment.");
                            return 0;
                        }
                        taskFragment.getDisplayContent().setFocusedApp(topResumedActivity);
                        return 0;
                    }
                case 7:
                    IBinder secondaryFragmentToken = taskFragmentOperation.getSecondaryFragmentToken();
                    taskFragment.setCompanionTaskFragment(secondaryFragmentToken != null ? this.mLaunchTaskFragments.get(secondaryFragmentToken) : null);
                    return 0;
                case 8:
                    TaskFragmentAnimationParams animationParams = taskFragmentOperation.getAnimationParams();
                    if (animationParams == null) {
                        sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, opType, new IllegalArgumentException("TaskFragmentAnimationParams must be non-null"));
                        return 0;
                    }
                    taskFragment.setAnimationParams(animationParams);
                    return 0;
                default:
                    return 0;
            }
            return i;
        }
        return 0;
    }

    public static /* synthetic */ boolean lambda$applyTaskFragmentOperation$11(ActivityRecord activityRecord) {
        return !activityRecord.finishing;
    }

    public final boolean validateTaskFragmentOperation(WindowContainerTransaction.HierarchyOp hierarchyOp, IBinder iBinder, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        TaskFragmentOperation taskFragmentOperation = hierarchyOp.getTaskFragmentOperation();
        TaskFragment taskFragment = this.mLaunchTaskFragments.get(hierarchyOp.getContainer());
        if (taskFragmentOperation == null) {
            sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, -1, new IllegalArgumentException("TaskFragmentOperation must be non-null"));
            return false;
        }
        int opType = taskFragmentOperation.getOpType();
        if (opType == 0) {
            return true;
        }
        if (validateTaskFragment(taskFragment, opType, iBinder, iTaskFragmentOrganizer)) {
            IBinder secondaryFragmentToken = taskFragmentOperation.getSecondaryFragmentToken();
            return secondaryFragmentToken == null || validateTaskFragment(this.mLaunchTaskFragments.get(secondaryFragmentToken), opType, iBinder, iTaskFragmentOrganizer);
        }
        return false;
    }

    public final boolean validateTaskFragment(TaskFragment taskFragment, int i, IBinder iBinder, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        if (taskFragment == null || !taskFragment.isAttached()) {
            sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, i, new IllegalArgumentException("Not allowed to apply operation on invalid fragment tokens opType=" + i));
            return false;
        } else if (!taskFragment.isEmbeddedTaskFragmentInPip() || (i == 1 && taskFragment.getTopNonFinishingActivity() == null)) {
            return true;
        } else {
            sendTaskFragmentOperationFailure(iTaskFragmentOrganizer, iBinder, taskFragment, i, new IllegalArgumentException("Not allowed to apply operation on PIP TaskFragment"));
            return false;
        }
    }

    public final int waitAsyncStart(final IntSupplier intSupplier) {
        Handler handler;
        final Integer[] numArr = {null};
        if (Looper.myLooper() == this.mService.f1161mH.getLooper()) {
            handler = this.mService.mWindowManager.mAnimationHandler;
        } else {
            handler = this.mService.f1161mH;
        }
        handler.post(new Runnable() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                WindowOrganizerController.this.lambda$waitAsyncStart$12(numArr, intSupplier);
            }
        });
        while (true) {
            Integer num = numArr[0];
            if (num == null) {
                try {
                    this.mGlobalLock.wait();
                } catch (InterruptedException unused) {
                }
            } else {
                return num.intValue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$waitAsyncStart$12(Integer[] numArr, IntSupplier intSupplier) {
        try {
            numArr[0] = Integer.valueOf(intSupplier.getAsInt());
        } catch (Throwable th) {
            numArr[0] = -96;
            Slog.w("WindowOrganizerController", th);
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGlobalLock.notifyAll();
            } catch (Throwable th2) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th2;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final int sanitizeAndApplyHierarchyOp(WindowContainer windowContainer, WindowContainerTransaction.HierarchyOp hierarchyOp) {
        WindowContainer fromBinder;
        Task asTask = windowContainer.asTask();
        if (asTask == null) {
            throw new IllegalArgumentException("Invalid container in hierarchy op");
        }
        DisplayContent displayContent = asTask.getDisplayContent();
        if (displayContent == null) {
            Slog.w("WindowOrganizerController", "Container is no longer attached: " + asTask);
            return 0;
        }
        if (hierarchyOp.isReparent()) {
            if (asTask.isRootTask() || asTask.getParent().asTask().mCreatedByOrganizer) {
                if (hierarchyOp.getNewParent() == null) {
                    fromBinder = displayContent.getDefaultTaskDisplayArea();
                } else {
                    fromBinder = WindowContainer.fromBinder(hierarchyOp.getNewParent());
                }
                if (fromBinder == null) {
                    Slog.e("WindowOrganizerController", "Can't resolve parent window from token");
                    return 0;
                } else if (asTask.getParent() != fromBinder) {
                    if (fromBinder.asTaskDisplayArea() != null) {
                        asTask.reparent(fromBinder.asTaskDisplayArea(), hierarchyOp.getToTop());
                        return 2;
                    } else if (fromBinder.asTask() != null) {
                        if (fromBinder.inMultiWindowMode() && asTask.isLeafTask()) {
                            if (fromBinder.inPinnedWindowingMode()) {
                                Slog.w("WindowOrganizerController", "Can't support moving a task to another PIP window... newParent=" + fromBinder + " task=" + asTask);
                                return 0;
                            } else if (!asTask.supportsMultiWindowInDisplayArea(fromBinder.asTask().getDisplayArea())) {
                                Slog.w("WindowOrganizerController", "Can't support task that doesn't support multi-window mode in multi-window mode... newParent=" + fromBinder + " task=" + asTask);
                                return 0;
                            }
                        }
                        asTask.reparent((Task) fromBinder, hierarchyOp.getToTop() ? Integer.MAX_VALUE : Integer.MIN_VALUE, false, "sanitizeAndApplyHierarchyOp");
                        return 2;
                    } else {
                        throw new RuntimeException("Can only reparent task to another task or taskDisplayArea, but not " + fromBinder);
                    }
                } else {
                    if (fromBinder instanceof TaskDisplayArea) {
                        fromBinder = asTask.getRootTask();
                    }
                    asTask.getDisplayArea().positionChildAt(hierarchyOp.getToTop() ? Integer.MAX_VALUE : Integer.MIN_VALUE, (Task) fromBinder, false);
                    return 2;
                }
            }
            throw new RuntimeException("Reparenting leaf Tasks is not supported now. " + asTask);
        }
        asTask.getParent().positionChildAt(hierarchyOp.getToTop() ? Integer.MAX_VALUE : Integer.MIN_VALUE, asTask, false);
        return 2;
    }

    public final boolean isLockTaskModeViolation(WindowContainer windowContainer, Task task, boolean z) {
        if (!z || windowContainer == null || task == null) {
            return false;
        }
        LockTaskController lockTaskController = this.mService.getLockTaskController();
        boolean isLockTaskModeViolation = lockTaskController.isLockTaskModeViolation(task);
        if (!isLockTaskModeViolation && windowContainer.asTask() != null) {
            isLockTaskModeViolation = lockTaskController.isLockTaskModeViolation(windowContainer.asTask());
        }
        if (isLockTaskModeViolation) {
            Slog.w("WindowOrganizerController", "Can't support the operation since in lock task mode violation.  Task: " + task + " Parent : " + windowContainer);
        }
        return isLockTaskModeViolation;
    }

    public final int reparentChildrenTasksHierarchyOp(final WindowContainerTransaction.HierarchyOp hierarchyOp, Transition transition, int i, final boolean z) {
        TaskDisplayArea asTaskDisplayArea;
        WindowContainer fromBinder = hierarchyOp.getContainer() != null ? WindowContainer.fromBinder(hierarchyOp.getContainer()) : null;
        WindowContainer fromBinder2 = hierarchyOp.getNewParent() != null ? WindowContainer.fromBinder(hierarchyOp.getNewParent()) : null;
        if (fromBinder == null && fromBinder2 == null) {
            throw new IllegalArgumentException("reparentChildrenTasksHierarchyOp: " + hierarchyOp);
        }
        if (fromBinder == null) {
            fromBinder = fromBinder2.asTask().getDisplayContent().getDefaultTaskDisplayArea();
        } else if (fromBinder2 == null) {
            fromBinder2 = fromBinder.asTask().getDisplayContent().getDefaultTaskDisplayArea();
        }
        final WindowContainer windowContainer = fromBinder;
        final WindowContainer windowContainer2 = fromBinder2;
        if (windowContainer == windowContainer2) {
            Slog.e("WindowOrganizerController", "reparentChildrenTasksHierarchyOp parent not changing: " + hierarchyOp);
            return 0;
        } else if (!windowContainer.isAttached()) {
            Slog.e("WindowOrganizerController", "reparentChildrenTasksHierarchyOp currentParent detached=" + windowContainer + " hop=" + hierarchyOp);
            return 0;
        } else if (!windowContainer2.isAttached()) {
            Slog.e("WindowOrganizerController", "reparentChildrenTasksHierarchyOp newParent detached=" + windowContainer2 + " hop=" + hierarchyOp);
            return 0;
        } else if (windowContainer2.inPinnedWindowingMode()) {
            Slog.e("WindowOrganizerController", "reparentChildrenTasksHierarchyOp newParent in PIP=" + windowContainer2 + " hop=" + hierarchyOp);
            return 0;
        } else {
            final boolean inMultiWindowMode = windowContainer2.inMultiWindowMode();
            if (windowContainer2.asTask() != null) {
                asTaskDisplayArea = windowContainer2.asTask().getDisplayArea();
            } else {
                asTaskDisplayArea = windowContainer2.asTaskDisplayArea();
            }
            final TaskDisplayArea taskDisplayArea = asTaskDisplayArea;
            Slog.i("WindowOrganizerController", "reparentChildrenTasksHierarchyOp currentParent=" + windowContainer + " newParent=" + windowContainer2 + " hop=" + hierarchyOp);
            final ArrayList arrayList = new ArrayList();
            windowContainer.forAllTasks(new Predicate() { // from class: com.android.server.wm.WindowOrganizerController$$ExternalSyntheticLambda11
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$reparentChildrenTasksHierarchyOp$13;
                    lambda$reparentChildrenTasksHierarchyOp$13 = WindowOrganizerController.this.lambda$reparentChildrenTasksHierarchyOp$13(windowContainer, inMultiWindowMode, taskDisplayArea, hierarchyOp, windowContainer2, z, arrayList, (Task) obj);
                    return lambda$reparentChildrenTasksHierarchyOp$13;
                }
            });
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                Task task = (Task) arrayList.get(i2);
                if (i >= 0) {
                    addToSyncSet(i, task);
                }
                if (transition != null) {
                    transition.collect(task);
                }
                if (windowContainer2 instanceof TaskDisplayArea) {
                    task.reparent((TaskDisplayArea) windowContainer2, hierarchyOp.getToTop());
                } else {
                    task.reparent((Task) windowContainer2, hierarchyOp.getToTop() ? Integer.MAX_VALUE : Integer.MIN_VALUE, false, "processChildrenTaskReparentHierarchyOp");
                }
            }
            if (transition != null) {
                transition.collect(windowContainer2);
                return 2;
            }
            return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reparentChildrenTasksHierarchyOp$13(WindowContainer windowContainer, boolean z, TaskDisplayArea taskDisplayArea, WindowContainerTransaction.HierarchyOp hierarchyOp, WindowContainer windowContainer2, boolean z2, ArrayList arrayList, Task task) {
        Slog.i("WindowOrganizerController", " Processing task=" + task);
        if (task.mCreatedByOrganizer || task.getParent() != windowContainer) {
            return false;
        }
        if (z && !task.supportsMultiWindowInDisplayArea(taskDisplayArea)) {
            Slog.e("WindowOrganizerController", "reparentChildrenTasksHierarchyOp non-resizeable task to multi window, task=" + task);
            return false;
        } else if (ArrayUtils.isEmpty(hierarchyOp.getActivityTypes()) || ArrayUtils.contains(hierarchyOp.getActivityTypes(), task.getActivityType())) {
            if ((ArrayUtils.isEmpty(hierarchyOp.getWindowingModes()) || ArrayUtils.contains(hierarchyOp.getWindowingModes(), task.getWindowingMode())) && !isLockTaskModeViolation(windowContainer2, task, z2)) {
                if (hierarchyOp.getToTop()) {
                    arrayList.add(0, task);
                } else {
                    arrayList.add(task);
                }
                return hierarchyOp.getReparentTopOnly() && arrayList.size() == 1;
            }
            return false;
        } else {
            return false;
        }
    }

    public final int setAdjacentRootsHierarchyOp(WindowContainerTransaction.HierarchyOp hierarchyOp) {
        TaskFragment asTaskFragment = WindowContainer.fromBinder(hierarchyOp.getContainer()).asTaskFragment();
        TaskFragment asTaskFragment2 = WindowContainer.fromBinder(hierarchyOp.getAdjacentRoot()).asTaskFragment();
        if (!asTaskFragment.mCreatedByOrganizer || !asTaskFragment2.mCreatedByOrganizer) {
            throw new IllegalArgumentException("setAdjacentRootsHierarchyOp: Not created by organizer root1=" + asTaskFragment + " root2=" + asTaskFragment2);
        } else if (asTaskFragment.getAdjacentTaskFragment() == asTaskFragment2) {
            return 0;
        } else {
            asTaskFragment.setAdjacentTaskFragment(asTaskFragment2);
            return 2;
        }
    }

    public final int clearAdjacentRootsHierarchyOp(WindowContainerTransaction.HierarchyOp hierarchyOp) {
        TaskFragment asTaskFragment = WindowContainer.fromBinder(hierarchyOp.getContainer()).asTaskFragment();
        if (!asTaskFragment.mCreatedByOrganizer) {
            throw new IllegalArgumentException("clearAdjacentRootsHierarchyOp: Not created by organizer root=" + asTaskFragment);
        } else if (asTaskFragment.getAdjacentTaskFragment() == null) {
            return 0;
        } else {
            asTaskFragment.resetAdjacentTaskFragment();
            return 2;
        }
    }

    public final void sanitizeWindowContainer(WindowContainer windowContainer) {
        if (!(windowContainer instanceof TaskFragment) && !(windowContainer instanceof DisplayArea)) {
            throw new RuntimeException("Invalid token in task fragment or displayArea transaction");
        }
    }

    public final int applyWindowContainerChange(WindowContainer windowContainer, WindowContainerTransaction.Change change, IBinder iBinder) {
        sanitizeWindowContainer(windowContainer);
        if (windowContainer.asDisplayArea() != null) {
            return applyDisplayAreaChanges(windowContainer.asDisplayArea(), change);
        }
        if (windowContainer.asTask() != null) {
            return applyTaskChanges(windowContainer.asTask(), change);
        }
        if (windowContainer.asTaskFragment() != null && windowContainer.asTaskFragment().isEmbedded()) {
            return applyTaskFragmentChanges(windowContainer.asTaskFragment(), change, iBinder);
        }
        return applyChanges(windowContainer, change);
    }

    public ITaskOrganizerController getTaskOrganizerController() {
        ActivityTaskManagerService.enforceTaskPermission("getTaskOrganizerController()");
        return this.mTaskOrganizerController;
    }

    public IDisplayAreaOrganizerController getDisplayAreaOrganizerController() {
        ActivityTaskManagerService.enforceTaskPermission("getDisplayAreaOrganizerController()");
        return this.mDisplayAreaOrganizerController;
    }

    public ITaskFragmentOrganizerController getTaskFragmentOrganizerController() {
        return this.mTaskFragmentOrganizerController;
    }

    public final BLASTSyncEngine.SyncGroup prepareSyncWithOrganizer(IWindowContainerTransactionCallback iWindowContainerTransactionCallback) {
        BLASTSyncEngine.SyncGroup prepareSyncSet = this.mService.mWindowManager.mSyncEngine.prepareSyncSet(this, "", 1);
        this.mTransactionCallbacksByPendingSyncId.put(Integer.valueOf(prepareSyncSet.mSyncId), iWindowContainerTransactionCallback);
        return prepareSyncSet;
    }

    @VisibleForTesting
    public int startSyncWithOrganizer(IWindowContainerTransactionCallback iWindowContainerTransactionCallback) {
        BLASTSyncEngine.SyncGroup prepareSyncWithOrganizer = prepareSyncWithOrganizer(iWindowContainerTransactionCallback);
        this.mService.mWindowManager.mSyncEngine.startSyncSet(prepareSyncWithOrganizer);
        return prepareSyncWithOrganizer.mSyncId;
    }

    @VisibleForTesting
    public void setSyncReady(int i) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -930893991, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        this.mService.mWindowManager.mSyncEngine.setReady(i);
    }

    @VisibleForTesting
    public void addToSyncSet(int i, WindowContainer windowContainer) {
        this.mService.mWindowManager.mSyncEngine.addToSyncSet(i, windowContainer);
    }

    @Override // com.android.server.p014wm.BLASTSyncEngine.TransactionReadyListener
    public void onTransactionReady(int i, SurfaceControl.Transaction transaction) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -497620140, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        try {
            this.mTransactionCallbacksByPendingSyncId.get(Integer.valueOf(i)).onTransactionReady(i, transaction);
        } catch (RemoteException unused) {
            transaction.apply();
        }
        this.mTransactionCallbacksByPendingSyncId.remove(Integer.valueOf(i));
    }

    public void registerTransitionPlayer(ITransitionPlayer iTransitionPlayer) {
        ActivityTaskManagerService.enforceTaskPermission("registerTransitionPlayer()");
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mTransitionController.registerTransitionPlayer(iTransitionPlayer, this.mService.getProcessController(callingPid, callingUid));
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ITransitionMetricsReporter getTransitionMetricsReporter() {
        return this.mTransitionController.mTransitionMetricsReporter;
    }

    public IBinder getApplyToken() {
        ActivityTaskManagerService.enforceTaskPermission("getApplyToken()");
        return SurfaceControl.Transaction.getDefaultApplyToken();
    }

    public static boolean configurationsAreEqualForOrganizer(Configuration configuration, Configuration configuration2) {
        if (configuration2 == null) {
            return false;
        }
        int diff = configuration.diff(configuration2);
        if ((((536870912 & diff) != 0 ? (int) configuration.windowConfiguration.diff(configuration2.windowConfiguration, true) : 0) & 3) == 0) {
            diff &= -536870913;
        }
        return (536882176 & diff) == 0;
    }

    public final void enforceTaskFragmentOrganizerPermission(String str, ITaskFragmentOrganizer iTaskFragmentOrganizer, WindowContainerTransaction windowContainerTransaction) {
        for (Map.Entry entry : windowContainerTransaction.getChanges().entrySet()) {
            enforceTaskFragmentConfigChangeAllowed(str, WindowContainer.fromBinder((IBinder) entry.getKey()), (WindowContainerTransaction.Change) entry.getValue(), iTaskFragmentOrganizer);
        }
        List hierarchyOps = windowContainerTransaction.getHierarchyOps();
        for (int size = hierarchyOps.size() - 1; size >= 0; size--) {
            WindowContainerTransaction.HierarchyOp hierarchyOp = (WindowContainerTransaction.HierarchyOp) hierarchyOps.get(size);
            int type = hierarchyOp.getType();
            if (type != 14) {
                if (type == 17) {
                    enforceTaskFragmentOrganized(str, hierarchyOp.getContainer(), iTaskFragmentOrganizer);
                    if (hierarchyOp.getTaskFragmentOperation() != null && hierarchyOp.getTaskFragmentOperation().getSecondaryFragmentToken() != null) {
                        enforceTaskFragmentOrganized(str, hierarchyOp.getTaskFragmentOperation().getSecondaryFragmentToken(), iTaskFragmentOrganizer);
                    }
                } else {
                    String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " trying to apply a hierarchy change that is not allowed for TaskFragmentOrganizer=" + iTaskFragmentOrganizer;
                    Slog.w("WindowOrganizerController", str2);
                    throw new SecurityException(str2);
                }
            }
        }
    }

    public final void enforceTaskFragmentOrganized(String str, IBinder iBinder, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        Objects.requireNonNull(iBinder);
        TaskFragment taskFragment = this.mLaunchTaskFragments.get(iBinder);
        if (taskFragment == null || taskFragment.hasTaskFragmentOrganizer(iTaskFragmentOrganizer)) {
            return;
        }
        String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " trying to modify TaskFragment not belonging to the TaskFragmentOrganizer=" + iTaskFragmentOrganizer;
        Slog.w("WindowOrganizerController", str2);
        throw new SecurityException(str2);
    }

    public final void enforceTaskFragmentConfigChangeAllowed(String str, WindowContainer windowContainer, WindowContainerTransaction.Change change, ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        if (windowContainer == null) {
            Slog.e("WindowOrganizerController", "Attempt to operate on task fragment that no longer exists");
            return;
        }
        TaskFragment asTaskFragment = windowContainer.asTaskFragment();
        if (asTaskFragment == null || !asTaskFragment.hasTaskFragmentOrganizer(iTaskFragmentOrganizer)) {
            String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " trying to modify window container not belonging to the TaskFragmentOrganizer=" + iTaskFragmentOrganizer;
            Slog.w("WindowOrganizerController", str2);
            throw new SecurityException(str2);
        }
        int changeMask = change.getChangeMask();
        int configSetMask = change.getConfigSetMask();
        int windowSetMask = change.getWindowSetMask();
        if (changeMask == 0 && configSetMask == 0 && windowSetMask == 0 && change.getWindowingMode() >= 0) {
            return;
        }
        if (changeMask == 512 && configSetMask == 536870912 && windowSetMask == 1) {
            return;
        }
        String str3 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " trying to apply changes of changeMask=" + changeMask + " configSetMask=" + configSetMask + " windowSetMask=" + windowSetMask + " to TaskFragment=" + asTaskFragment + " TaskFragmentOrganizer=" + iTaskFragmentOrganizer;
        Slog.w("WindowOrganizerController", str3);
        throw new SecurityException(str3);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x00aa, code lost:
        if (r11 != (-1)) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00ac, code lost:
        r2 = r11 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00c3, code lost:
        if (r11 != (-1)) goto L25;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void createTaskFragment(TaskFragmentCreationParams taskFragmentCreationParams, IBinder iBinder, CallerInfo callerInfo, Transition transition) {
        int indexOf;
        ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(taskFragmentCreationParams.getOwnerToken());
        ITaskFragmentOrganizer asInterface = ITaskFragmentOrganizer.Stub.asInterface(taskFragmentCreationParams.getOrganizer().asBinder());
        if (this.mLaunchTaskFragments.containsKey(taskFragmentCreationParams.getFragmentToken())) {
            sendTaskFragmentOperationFailure(asInterface, iBinder, null, 0, new IllegalArgumentException("TaskFragment token must be unique"));
        } else if (forTokenLocked == null || forTokenLocked.getTask() == null) {
            sendTaskFragmentOperationFailure(asInterface, iBinder, null, 0, new IllegalArgumentException("Not allowed to operate with invalid ownerToken"));
        } else if (!forTokenLocked.isResizeable()) {
            sendTaskFragmentOperationFailure(asInterface, iBinder, null, 0, new IllegalArgumentException("Not allowed to operate with non-resizable owner Activity"));
        } else {
            Task task = forTokenLocked.getTask();
            if (task.effectiveUid != forTokenLocked.getUid() || task.effectiveUid != callerInfo.mUid) {
                sendTaskFragmentOperationFailure(asInterface, iBinder, null, 0, new SecurityException("Not allowed to operate with the ownerToken while the root activity of the target task belong to the different app"));
            } else if (task.inPinnedWindowingMode()) {
                sendTaskFragmentOperationFailure(asInterface, iBinder, null, 0, new IllegalArgumentException("Not allowed to create TaskFragment in PIP Task"));
            } else {
                TaskFragment taskFragment = new TaskFragment(this.mService, taskFragmentCreationParams.getFragmentToken(), true);
                taskFragment.setTaskFragmentOrganizer(taskFragmentCreationParams.getOrganizer(), forTokenLocked.getUid(), forTokenLocked.info.processName);
                int i = Integer.MAX_VALUE;
                if (taskFragmentCreationParams.getPairedPrimaryFragmentToken() != null) {
                    indexOf = task.mChildren.indexOf(getTaskFragment(taskFragmentCreationParams.getPairedPrimaryFragmentToken()));
                } else {
                    if (taskFragmentCreationParams.getPairedActivityToken() != null) {
                        indexOf = task.mChildren.indexOf(ActivityRecord.forTokenLocked(taskFragmentCreationParams.getPairedActivityToken()));
                    }
                    task.addChild(taskFragment, i);
                    taskFragment.setWindowingMode(taskFragmentCreationParams.getWindowingMode());
                    if (!taskFragmentCreationParams.getInitialRelativeBounds().isEmpty()) {
                        taskFragment.setRelativeEmbeddedBounds(taskFragmentCreationParams.getInitialRelativeBounds());
                        taskFragment.recomputeConfiguration();
                    }
                    this.mLaunchTaskFragments.put(taskFragmentCreationParams.getFragmentToken(), taskFragment);
                    if (transition != null) {
                        transition.collectExistenceChange(taskFragment);
                    }
                }
            }
        }
    }

    public final int deleteTaskFragment(TaskFragment taskFragment, Transition transition) {
        if (transition != null) {
            transition.collectExistenceChange(taskFragment);
        }
        this.mLaunchTaskFragments.remove(taskFragment.getFragmentToken());
        taskFragment.remove(true, "deleteTaskFragment");
        return 2;
    }

    public TaskFragment getTaskFragment(IBinder iBinder) {
        return this.mLaunchTaskFragments.get(iBinder);
    }

    public void cleanUpEmbeddedTaskFragment(TaskFragment taskFragment) {
        this.mLaunchTaskFragments.remove(taskFragment.getFragmentToken());
    }

    public void sendTaskFragmentOperationFailure(ITaskFragmentOrganizer iTaskFragmentOrganizer, IBinder iBinder, TaskFragment taskFragment, int i, Throwable th) {
        if (iTaskFragmentOrganizer == null) {
            throw new IllegalArgumentException("Not allowed to operate with invalid organizer");
        }
        this.mService.mTaskFragmentOrganizerController.onTaskFragmentError(iTaskFragmentOrganizer, iBinder, taskFragment, i, th);
    }

    public final Throwable convertStartFailureToThrowable(int i, Intent intent) {
        if (i == -96) {
            return new AndroidRuntimeException("Activity could not be started for " + intent + " with error code : " + i);
        } else if (i == -94) {
            return new SecurityException("Permission denied and not allowed to start activity " + intent);
        } else if (i == -92 || i == -91) {
            return new ActivityNotFoundException("No Activity found to handle " + intent);
        } else {
            return new AndroidRuntimeException("Start activity failed with error code : " + i + " when starting " + intent);
        }
    }
}
