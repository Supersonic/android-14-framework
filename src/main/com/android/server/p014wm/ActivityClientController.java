package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.FullscreenRequestHandler;
import android.app.IActivityClientController;
import android.app.ICompatCameraControlCallback;
import android.app.IRequestFinishCallback;
import android.app.PictureInPictureParams;
import android.app.PictureInPictureUiState;
import android.app.compat.CompatChanges;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.EnterPipRequestedItem;
import android.app.servertransaction.PipStateTransactionItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.service.voice.VoiceInteractionManagerInternal;
import android.util.Slog;
import android.view.RemoteAnimationDefinition;
import android.window.SizeConfigurationBuckets;
import android.window.TransitionInfo;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.LocalServices;
import com.android.server.Watchdog;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p013vr.VrManagerInternal;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.uri.NeededUriGrants;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.ActivityClientController */
/* loaded from: classes2.dex */
public class ActivityClientController extends IActivityClientController.Stub {
    public AssistUtils mAssistUtils;
    public final Context mContext;
    public final WindowManagerGlobalLock mGlobalLock;
    public final ActivityTaskManagerService mService;
    public final ActivityTaskSupervisor mTaskSupervisor;

    public ActivityClientController(ActivityTaskManagerService activityTaskManagerService) {
        this.mService = activityTaskManagerService;
        this.mGlobalLock = activityTaskManagerService.mGlobalLock;
        this.mTaskSupervisor = activityTaskManagerService.mTaskSupervisor;
        this.mContext = activityTaskManagerService.mContext;
    }

    public void onSystemReady() {
        this.mAssistUtils = new AssistUtils(this.mContext);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            throw ActivityTaskManagerService.logAndRethrowRuntimeExceptionOnTransact("ActivityClientController", e);
        }
    }

    public void activityIdle(IBinder iBinder, Configuration configuration, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Trace.traceBegin(32L, "activityIdle");
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    this.mTaskSupervisor.activityIdleInternal(forTokenLocked, false, false, configuration);
                    if (z && forTokenLocked.hasProcess()) {
                        forTokenLocked.app.clearProfilerIfNeeded();
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Trace.traceEnd(32L);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void activityResumed(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord.activityResumedLocked(iBinder, z);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityRefreshed(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord.activityRefreshedLocked(iBinder);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityTopResumedStateLost() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mTaskSupervisor.handleTopResumedStateReleased(false);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityPaused(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Trace.traceBegin(32L, "activityPaused");
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.activityPaused(false);
                }
                Trace.traceEnd(32L);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityStopped(IBinder iBinder, Bundle bundle, PersistableBundle persistableBundle, CharSequence charSequence) {
        ActivityRecord isInRootTaskLocked;
        int i;
        String str;
        if (bundle != null && bundle.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Bundle");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Trace.traceBegin(32L, "activityStopped");
                isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                i = 0;
                str = null;
                if (isInRootTaskLocked != null) {
                    ActivityRecord.State state = ActivityRecord.State.STOPPING;
                    ActivityRecord.State state2 = ActivityRecord.State.RESTARTING_PROCESS;
                    if (!isInRootTaskLocked.isState(state, state2) && this.mTaskSupervisor.hasScheduledRestartTimeouts(isInRootTaskLocked)) {
                        isInRootTaskLocked.setState(state2, "continue-restart");
                    }
                    if (isInRootTaskLocked.attachedToProcess() && isInRootTaskLocked.isState(state2)) {
                        WindowProcessController windowProcessController = isInRootTaskLocked.app;
                        String str2 = windowProcessController.mName;
                        int i2 = windowProcessController.mUid;
                        if (isInRootTaskLocked != isInRootTaskLocked.getTask().topRunningActivity()) {
                            isInRootTaskLocked.setVisibleRequested(false);
                        }
                        i = i2;
                        str = str2;
                    }
                    isInRootTaskLocked.activityStopped(bundle, persistableBundle, charSequence);
                }
                Trace.traceEnd(32L);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (str != null) {
            this.mTaskSupervisor.removeRestartTimeouts(isInRootTaskLocked);
            this.mService.mAmInternal.killProcess(str, i, "restartActivityProcess");
        }
        this.mService.mAmInternal.trimApplications();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityDestroyed(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Trace.traceBegin(32L, "activityDestroyed");
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.destroyed("activityDestroyed");
                }
                Trace.traceEnd(32L);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void activityLocalRelaunch(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.startRelaunching();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void activityRelaunched(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.finishRelaunching();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void reportSizeConfigurations(IBinder iBinder, SizeConfigurationBuckets sizeConfigurationBuckets) {
        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1305412562, 0, (String) null, new Object[]{String.valueOf(iBinder), String.valueOf(sizeConfigurationBuckets)});
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setSizeConfigurations(sizeConfigurationBuckets);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean moveActivityTaskToBack(IBinder iBinder, boolean z) {
        ActivityTaskManagerService.enforceNotIsolatedCaller("moveActivityTaskToBack");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mService.mRootWindowContainer.anyTaskForId(ActivityRecord.getTaskForActivityLocked(iBinder, !z));
                if (anyTaskForId == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean moveTaskToBack = ActivityRecord.getRootTask(iBinder).moveTaskToBack(anyTaskForId);
                WindowManagerService.resetPriorityAfterLockedSection();
                return moveTaskToBack;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean shouldUpRecreateTask(IBinder iBinder, String str) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean shouldUpRecreateTaskLocked = forTokenLocked.getRootTask().shouldUpRecreateTaskLocked(forTokenLocked, str);
                WindowManagerService.resetPriorityAfterLockedSection();
                return shouldUpRecreateTaskLocked;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public boolean navigateUpTo(IBinder iBinder, Intent intent, String str, int i, Intent intent2) {
        boolean navigateUpTo;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                NeededUriGrants collectGrants = this.mService.collectGrants(intent, isInRootTaskLocked);
                NeededUriGrants collectGrants2 = this.mService.collectGrants(intent2, isInRootTaskLocked.resultTo);
                synchronized (this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        navigateUpTo = isInRootTaskLocked.getRootTask().navigateUpTo(isInRootTaskLocked, intent, str, collectGrants, i, intent2, collectGrants2);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return navigateUpTo;
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public boolean releaseActivityInstance(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null && isInRootTaskLocked.isDestroyable()) {
                    isInRootTaskLocked.destroyImmediately("app-req");
                    boolean isState = isInRootTaskLocked.isState(ActivityRecord.State.DESTROYING, ActivityRecord.State.DESTROYED);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return isState;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean finishActivity(IBinder iBinder, int i, Intent intent, int i2) {
        long j;
        ActivityRecord activityRecord;
        boolean z;
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                boolean z2 = true;
                if (isInRootTaskLocked == null) {
                    return true;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                NeededUriGrants collectGrants = this.mService.collectGrants(intent, isInRootTaskLocked.resultTo);
                synchronized (this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (!isInRootTaskLocked.isInHistory()) {
                            return true;
                        }
                        Task task = isInRootTaskLocked.getTask();
                        ActivityRecord rootActivity = task.getRootActivity();
                        if (rootActivity == null) {
                            Slog.w("ActivityTaskManager", "Finishing task with all activities already finished");
                        }
                        if (this.mService.getLockTaskController().activityBlockedFromFinish(isInRootTaskLocked)) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        if (this.mService.mController != null && (activityRecord = isInRootTaskLocked.getRootTask().topRunningActivity(iBinder, -1)) != null) {
                            try {
                                z = this.mService.mController.activityResuming(activityRecord.packageName);
                            } catch (RemoteException unused) {
                                this.mService.mController = null;
                                Watchdog.getInstance().setActivityController(null);
                                z = true;
                            }
                            if (!z) {
                                Slog.i("ActivityTaskManager", "Not finishing activity because controller resumed");
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return false;
                            }
                        }
                        WindowProcessController windowProcessController = isInRootTaskLocked.app;
                        if (windowProcessController != null) {
                            windowProcessController.setLastActivityFinishTimeIfNeeded(SystemClock.uptimeMillis());
                        }
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        Trace.traceBegin(32L, "finishActivity");
                        boolean z3 = i2 == 1;
                        try {
                            if (i2 == 2 || (z3 && isInRootTaskLocked == rootActivity)) {
                                j = 32;
                                try {
                                    this.mTaskSupervisor.removeTask(task, false, z3, "finish-activity", isInRootTaskLocked.getUid(), isInRootTaskLocked.info.name);
                                    isInRootTaskLocked.mRelaunchReason = 0;
                                } catch (Throwable th) {
                                    th = th;
                                    Trace.traceEnd(j);
                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                    throw th;
                                }
                            } else {
                                isInRootTaskLocked.finishIfPossible(i, intent, collectGrants, "app-request", true);
                                z2 = isInRootTaskLocked.finishing;
                                if (!z2) {
                                    Slog.i("ActivityTaskManager", "Failed to finish by app-request");
                                }
                                j = 32;
                            }
                            Trace.traceEnd(j);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return z2;
                        } catch (Throwable th2) {
                            th = th2;
                            j = 32;
                        }
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public boolean finishActivityAffinity(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                final ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    if (!this.mService.getLockTaskController().activityBlockedFromFinish(isInRootTaskLocked)) {
                        isInRootTaskLocked.getTask().forAllActivities(new Predicate() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda1
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj) {
                                boolean finishIfSameAffinity;
                                finishIfSameAffinity = ActivityRecord.this.finishIfSameAffinity((ActivityRecord) obj);
                                return finishIfSameAffinity;
                            }
                        }, isInRootTaskLocked, true, true);
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

    public void finishSubActivity(IBinder iBinder, final String str, final int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                final ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.getRootTask().forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda5
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((ActivityRecord) obj).finishIfSubActivity(ActivityRecord.this, str, i);
                        }
                    }, true);
                    this.mService.updateOomAdj();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setForceSendResultForMediaProjection(IBinder iBinder) {
        this.mService.mAmInternal.enforceCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION", "setForceSendResultForMediaProjection");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null && isInRootTaskLocked.isInHistory()) {
                    isInRootTaskLocked.setForceSendResultForMediaProjection();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean isTopOfTask(IBinder iBinder) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                z = isInRootTaskLocked != null && isInRootTaskLocked.getTask().getTopNonFinishingActivity() == isInRootTaskLocked;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public boolean willActivityBeVisible(IBinder iBinder) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task rootTask = ActivityRecord.getRootTask(iBinder);
                z = rootTask != null && rootTask.willActivityBeVisible(iBinder);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public int getDisplayId(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task rootTask = ActivityRecord.getRootTask(iBinder);
                if (rootTask == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return 0;
                }
                int displayId = rootTask.getDisplayId();
                int i = displayId != -1 ? displayId : 0;
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getTaskForActivity(IBinder iBinder, boolean z) {
        int taskForActivityLocked;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                taskForActivityLocked = ActivityRecord.getTaskForActivityLocked(iBinder, z);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return taskForActivityLocked;
    }

    public Configuration getTaskConfiguration(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInAnyTask = ActivityRecord.isInAnyTask(iBinder);
                if (isInAnyTask == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                Configuration configuration = isInAnyTask.getTask().getConfiguration();
                WindowManagerService.resetPriorityAfterLockedSection();
                return configuration;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public IBinder getActivityTokenBelow(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInAnyTask = ActivityRecord.isInAnyTask(iBinder);
                if (isInAnyTask == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                ActivityRecord activity = isInAnyTask.getTask().getActivity(new Predicate() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda4
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getActivityTokenBelow$2;
                        lambda$getActivityTokenBelow$2 = ActivityClientController.lambda$getActivityTokenBelow$2((ActivityRecord) obj);
                        return lambda$getActivityTokenBelow$2;
                    }
                }, isInAnyTask, false, true);
                if (activity == null || activity.getUid() != isInAnyTask.getUid()) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                IBinder iBinder2 = activity.token;
                WindowManagerService.resetPriorityAfterLockedSection();
                return iBinder2;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ boolean lambda$getActivityTokenBelow$2(ActivityRecord activityRecord) {
        return !activityRecord.finishing;
    }

    public ComponentName getCallingActivity(IBinder iBinder) {
        ComponentName component;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord callingRecord = getCallingRecord(iBinder);
                component = callingRecord != null ? callingRecord.intent.getComponent() : null;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return component;
    }

    public String getCallingPackage(IBinder iBinder) {
        String str;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord callingRecord = getCallingRecord(iBinder);
                str = callingRecord != null ? callingRecord.info.packageName : null;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return str;
    }

    public static ActivityRecord getCallingRecord(IBinder iBinder) {
        ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
        if (isInRootTaskLocked != null) {
            return isInRootTaskLocked.resultTo;
        }
        return null;
    }

    public int getLaunchedFromUid(IBinder iBinder) {
        int callingUid = Binder.getCallingUid();
        boolean isInternalCallerGetLaunchedFrom = isInternalCallerGetLaunchedFrom(callingUid);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked == null || !(isInternalCallerGetLaunchedFrom || canGetLaunchedFromLocked(callingUid, forTokenLocked))) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -1;
                }
                int i = forTokenLocked.launchedFromUid;
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public String getLaunchedFromPackage(IBinder iBinder) {
        int callingUid = Binder.getCallingUid();
        boolean isInternalCallerGetLaunchedFrom = isInternalCallerGetLaunchedFrom(callingUid);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked == null || !(isInternalCallerGetLaunchedFrom || canGetLaunchedFromLocked(callingUid, forTokenLocked))) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                String str = forTokenLocked.launchedFromPackage;
                WindowManagerService.resetPriorityAfterLockedSection();
                return str;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final boolean isInternalCallerGetLaunchedFrom(int i) {
        if (UserHandle.getAppId(i) == 1000) {
            return true;
        }
        PackageManagerInternal packageManagerInternal = this.mService.mWindowManager.mPmInternal;
        AndroidPackage androidPackage = packageManagerInternal.getPackage(i);
        if (androidPackage == null) {
            return false;
        }
        if (androidPackage.isSignedWithPlatformKey()) {
            return true;
        }
        String[] knownPackageNames = packageManagerInternal.getKnownPackageNames(2, UserHandle.getUserId(i));
        return knownPackageNames.length > 0 && androidPackage.getPackageName().equals(knownPackageNames[0]);
    }

    public static boolean canGetLaunchedFromLocked(int i, ActivityRecord activityRecord) {
        if (CompatChanges.isChangeEnabled(259743961L, i)) {
            return activityRecord.mShareIdentity || activityRecord.launchedFromUid == i;
        }
        return false;
    }

    public void setRequestedOrientation(IBinder iBinder, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    EventLogTags.writeWmSetRequestedOrientation(i, isInRootTaskLocked.shortComponentName);
                    isInRootTaskLocked.setRequestedOrientation(i);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getRequestedOrientation(IBinder iBinder) {
        int overrideOrientation;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                overrideOrientation = isInRootTaskLocked != null ? isInRootTaskLocked.getOverrideOrientation() : -1;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return overrideOrientation;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean convertFromTranslucent(IBinder iBinder) {
        boolean z;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                Transition createTransition = (isInRootTaskLocked == null || !isInRootTaskLocked.mTransitionController.inPlayingTransition(isInRootTaskLocked) || isInRootTaskLocked.mTransitionController.isCollecting()) ? null : isInRootTaskLocked.mTransitionController.createTransition(4);
                if (createTransition != null) {
                    isInRootTaskLocked.mTransitionController.requestStartTransition(createTransition, null, null, null);
                }
                if (isInRootTaskLocked != null) {
                    z = true;
                    if (isInRootTaskLocked.setOccludesParent(true)) {
                        if (createTransition != null) {
                            if (z) {
                                isInRootTaskLocked.mTransitionController.setReady(isInRootTaskLocked.getDisplayContent());
                            } else {
                                createTransition.abort();
                            }
                        }
                    }
                }
                z = false;
                if (createTransition != null) {
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean convertToTranslucent(IBinder iBinder, Bundle bundle) {
        boolean z;
        SafeActivityOptions fromBundle = SafeActivityOptions.fromBundle(bundle);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                z = false;
                if (isInRootTaskLocked != null) {
                    ActivityRecord activityBelow = isInRootTaskLocked.getTask().getActivityBelow(isInRootTaskLocked);
                    if (activityBelow != null) {
                        activityBelow.returningOptions = fromBundle != null ? fromBundle.getOptions(isInRootTaskLocked) : null;
                    }
                    Transition createTransition = (!isInRootTaskLocked.mTransitionController.inPlayingTransition(isInRootTaskLocked) || isInRootTaskLocked.mTransitionController.isCollecting()) ? null : isInRootTaskLocked.mTransitionController.createTransition(3);
                    if (createTransition != null) {
                        isInRootTaskLocked.mTransitionController.requestStartTransition(createTransition, null, null, null);
                    }
                    z = isInRootTaskLocked.setOccludesParent(false);
                    if (createTransition != null) {
                        if (z) {
                            isInRootTaskLocked.mTransitionController.setReady(isInRootTaskLocked.getDisplayContent());
                        } else {
                            createTransition.abort();
                        }
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isImmersive(IBinder iBinder) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    throw new IllegalArgumentException();
                }
                z = isInRootTaskLocked.immersive;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public void setImmersive(IBinder iBinder, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    throw new IllegalArgumentException();
                }
                isInRootTaskLocked.immersive = z;
                if (isInRootTaskLocked.isFocusedActivityOnDisplay()) {
                    if (ProtoLogCache.WM_DEBUG_IMMERSIVE_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_IMMERSIVE, -655104359, 0, (String) null, new Object[]{String.valueOf(isInRootTaskLocked)});
                    }
                    this.mService.applyUpdateLockStateLocked(isInRootTaskLocked);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean enterPictureInPictureMode(IBinder iBinder, PictureInPictureParams pictureInPictureParams) {
        boolean enterPictureInPictureMode;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                enterPictureInPictureMode = this.mService.enterPictureInPictureMode(ensureValidPictureInPictureActivityParams("enterPictureInPictureMode", iBinder, pictureInPictureParams), pictureInPictureParams, true);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return enterPictureInPictureMode;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setPictureInPictureParams(IBinder iBinder, PictureInPictureParams pictureInPictureParams) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ensureValidPictureInPictureActivityParams("setPictureInPictureParams", iBinder, pictureInPictureParams).setPictureInPictureParams(pictureInPictureParams);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setShouldDockBigOverlays(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord.forTokenLocked(iBinder).setShouldDockBigOverlays(z);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void splashScreenAttached(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord.splashScreenAttachedLocked(iBinder);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void requestCompatCameraControl(IBinder iBinder, boolean z, boolean z2, ICompatCameraControlCallback iCompatCameraControlCallback) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.updateCameraCompatState(z, z2, iCompatCameraControlCallback);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final ActivityRecord ensureValidPictureInPictureActivityParams(String str, IBinder iBinder, PictureInPictureParams pictureInPictureParams) {
        if (!this.mService.mSupportsPictureInPicture) {
            throw new IllegalStateException(str + ": Device doesn't support picture-in-picture mode.");
        }
        ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
        if (forTokenLocked == null) {
            throw new IllegalStateException(str + ": Can't find activity for token=" + iBinder);
        } else if (!forTokenLocked.supportsPictureInPicture()) {
            throw new IllegalStateException(str + ": Current activity does not support picture-in-picture.");
        } else {
            float f = this.mContext.getResources().getFloat(17105098);
            float f2 = this.mContext.getResources().getFloat(17105097);
            if (pictureInPictureParams.hasSetAspectRatio() && !this.mService.mWindowManager.isValidPictureInPictureAspectRatio(forTokenLocked.mDisplayContent, pictureInPictureParams.getAspectRatioFloat())) {
                throw new IllegalArgumentException(String.format(str + ": Aspect ratio is too extreme (must be between %f and %f).", Float.valueOf(f), Float.valueOf(f2)));
            } else if (this.mService.mSupportsExpandedPictureInPicture && pictureInPictureParams.hasSetExpandedAspectRatio() && !this.mService.mWindowManager.isValidExpandedPictureInPictureAspectRatio(forTokenLocked.mDisplayContent, pictureInPictureParams.getExpandedAspectRatioFloat())) {
                throw new IllegalArgumentException(String.format(str + ": Expanded aspect ratio is not extreme enough (must not be between %f and %f).", Float.valueOf(f), Float.valueOf(f2)));
            } else {
                pictureInPictureParams.truncateActions(ActivityTaskManager.getMaxNumPictureInPictureActions(this.mContext));
                return forTokenLocked;
            }
        }
    }

    public boolean requestPictureInPictureMode(ActivityRecord activityRecord) {
        if (!activityRecord.inPinnedWindowingMode() && activityRecord.checkEnterPictureInPictureState("requestPictureInPictureMode", false)) {
            if (activityRecord.pictureInPictureArgs.isAutoEnterEnabled()) {
                return this.mService.enterPictureInPictureMode(activityRecord, activityRecord.pictureInPictureArgs, false);
            }
            try {
                ClientTransaction obtain = ClientTransaction.obtain(activityRecord.app.getThread(), activityRecord.token);
                obtain.addCallback(EnterPipRequestedItem.obtain());
                this.mService.getLifecycleManager().scheduleTransaction(obtain);
                return true;
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Failed to send enter pip requested item: " + activityRecord.intent.getComponent(), e);
                return false;
            }
        }
        return false;
    }

    public void onPictureInPictureStateChanged(ActivityRecord activityRecord, PictureInPictureUiState pictureInPictureUiState) {
        if (!activityRecord.inPinnedWindowingMode()) {
            throw new IllegalStateException("Activity is not in PIP mode");
        }
        try {
            ClientTransaction obtain = ClientTransaction.obtain(activityRecord.app.getThread(), activityRecord.token);
            obtain.addCallback(PipStateTransactionItem.obtain(pictureInPictureUiState));
            this.mService.getLifecycleManager().scheduleTransaction(obtain);
        } catch (Exception e) {
            Slog.w("ActivityTaskManager", "Failed to send pip state transaction item: " + activityRecord.intent.getComponent(), e);
        }
    }

    public void toggleFreeformWindowingMode(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked == null) {
                    throw new IllegalArgumentException("toggleFreeformWindowingMode: No activity record matching token=" + iBinder);
                }
                Task rootTask = forTokenLocked.getRootTask();
                if (rootTask == null) {
                    throw new IllegalStateException("toggleFreeformWindowingMode: the activity doesn't have a root task");
                }
                if (!rootTask.inFreeformWindowingMode() && rootTask.getWindowingMode() != 1) {
                    throw new IllegalStateException("toggleFreeformWindowingMode: You can only toggle between fullscreen and freeform.");
                }
                if (rootTask.inFreeformWindowingMode()) {
                    rootTask.setWindowingMode(1);
                    rootTask.setBounds(null);
                } else if (!forTokenLocked.supportsFreeform()) {
                    throw new IllegalStateException("This activity is currently not freeform-enabled");
                } else {
                    if (rootTask.getParent().inFreeformWindowingMode()) {
                        rootTask.setWindowingMode(0);
                    } else {
                        rootTask.setWindowingMode(5);
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @FullscreenRequestHandler.RequestResult
    public final int validateMultiwindowFullscreenRequestLocked(Task task, int i, ActivityRecord activityRecord) {
        if (task.getParent().getWindowingMode() != 5) {
            return 3;
        }
        if (activityRecord != task.getTopMostActivity()) {
            return 4;
        }
        return i == 1 ? task.getWindowingMode() != 5 ? 1 : 0 : (task.getWindowingMode() == 1 && task.mMultiWindowRestoreWindowingMode != -1) ? 0 : 2;
    }

    public void requestMultiwindowFullscreen(IBinder iBinder, int i, IRemoteCallback iRemoteCallback) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                requestMultiwindowFullscreenLocked(iBinder, i, iRemoteCallback);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void requestMultiwindowFullscreenLocked(IBinder iBinder, final int i, final IRemoteCallback iRemoteCallback) {
        final ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
        if (forTokenLocked == null) {
            return;
        }
        TransitionController transitionController = forTokenLocked.mTransitionController;
        if (!transitionController.isShellTransitionsEnabled()) {
            Task topDisplayFocusedRootTask = this.mService.getTopDisplayFocusedRootTask();
            int validateMultiwindowFullscreenRequestLocked = validateMultiwindowFullscreenRequestLocked(topDisplayFocusedRootTask, i, forTokenLocked);
            reportMultiwindowFullscreenRequestValidatingResult(iRemoteCallback, validateMultiwindowFullscreenRequestLocked);
            if (validateMultiwindowFullscreenRequestLocked == 0) {
                executeMultiWindowFullscreenRequest(i, topDisplayFocusedRootTask);
                return;
            }
            return;
        }
        final Transition transition = new Transition(6, 0, transitionController, this.mService.mWindowManager.mSyncEngine);
        if (this.mService.mWindowManager.mSyncEngine.hasActiveSync()) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -1484988952, 0, (String) null, new Object[]{String.valueOf(transition)});
            }
            this.mService.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityClientController.lambda$requestMultiwindowFullscreenLocked$3(ActivityRecord.this, transition);
                }
            }, new Runnable() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityClientController.this.lambda$requestMultiwindowFullscreenLocked$4(i, iRemoteCallback, forTokenLocked, transition);
                }
            });
            return;
        }
        executeFullscreenRequestTransition(i, iRemoteCallback, forTokenLocked, transition, false);
    }

    public static /* synthetic */ void lambda$requestMultiwindowFullscreenLocked$3(ActivityRecord activityRecord, Transition transition) {
        activityRecord.mTransitionController.moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestMultiwindowFullscreenLocked$4(int i, IRemoteCallback iRemoteCallback, ActivityRecord activityRecord, Transition transition) {
        executeFullscreenRequestTransition(i, iRemoteCallback, activityRecord, transition, true);
    }

    public final void executeFullscreenRequestTransition(int i, IRemoteCallback iRemoteCallback, ActivityRecord activityRecord, Transition transition, boolean z) {
        Task topDisplayFocusedRootTask = this.mService.getTopDisplayFocusedRootTask();
        int validateMultiwindowFullscreenRequestLocked = validateMultiwindowFullscreenRequestLocked(topDisplayFocusedRootTask, i, activityRecord);
        reportMultiwindowFullscreenRequestValidatingResult(iRemoteCallback, validateMultiwindowFullscreenRequestLocked);
        if (validateMultiwindowFullscreenRequestLocked != 0) {
            if (z) {
                transition.abort();
                return;
            }
            return;
        }
        activityRecord.mTransitionController.requestStartTransition(transition, topDisplayFocusedRootTask, null, null);
        executeMultiWindowFullscreenRequest(i, topDisplayFocusedRootTask);
        transition.setReady(topDisplayFocusedRootTask, true);
    }

    public static void reportMultiwindowFullscreenRequestValidatingResult(IRemoteCallback iRemoteCallback, @FullscreenRequestHandler.RequestResult int i) {
        if (iRemoteCallback == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("result", i);
        try {
            iRemoteCallback.sendResult(bundle);
        } catch (RemoteException unused) {
            Slog.w("ActivityTaskManager", "client throws an exception back to the server, ignore it");
        }
    }

    public static void executeMultiWindowFullscreenRequest(int i, Task task) {
        int i2;
        if (i == 1) {
            task.mMultiWindowRestoreWindowingMode = task.getRequestedOverrideWindowingMode();
            i2 = 1;
        } else {
            i2 = task.mMultiWindowRestoreWindowingMode;
            task.mMultiWindowRestoreWindowingMode = -1;
        }
        task.setWindowingMode(i2);
        if (i2 == 1) {
            task.setBounds(null);
        }
    }

    public void startLockTaskModeByToken(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    this.mService.startLockTaskMode(forTokenLocked.getTask(), false);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void stopLockTaskModeByToken(IBinder iBinder) {
        this.mService.stopLockTaskModeInternal(iBinder, false);
    }

    public void showLockTaskEscapeMessage(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ActivityRecord.forTokenLocked(iBinder) != null) {
                    this.mService.getLockTaskController().showLockTaskToast();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setTaskDescription(IBinder iBinder, ActivityManager.TaskDescription taskDescription) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setTaskDescription(taskDescription);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean showAssistFromActivity(IBinder iBinder, Bundle bundle) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                Task topDisplayFocusedRootTask = this.mService.getTopDisplayFocusedRootTask();
                ActivityRecord topNonFinishingActivity = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.getTopNonFinishingActivity() : null;
                if (topNonFinishingActivity != forTokenLocked) {
                    Slog.w("ActivityTaskManager", "showAssistFromActivity failed: caller " + forTokenLocked + " is not current top " + topNonFinishingActivity);
                } else if (!topNonFinishingActivity.nowVisible) {
                    Slog.w("ActivityTaskManager", "showAssistFromActivity failed: caller " + forTokenLocked + " is not visible");
                } else {
                    String str = topNonFinishingActivity.launchedFromFeatureId;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return this.mAssistUtils.showSessionForActiveService(bundle, 8, str, (IVoiceInteractionSessionShowCallback) null, iBinder);
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isRootVoiceInteraction(IBinder iBinder) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                z = isInRootTaskLocked != null && isInRootTaskLocked.rootVoiceInteraction;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public void startLocalVoiceInteraction(IBinder iBinder, Bundle bundle) {
        Slog.i("ActivityTaskManager", "Activity tried to startLocalVoiceInteraction");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = this.mService.getTopDisplayFocusedRootTask();
                ActivityRecord topNonFinishingActivity = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.getTopNonFinishingActivity() : null;
                if (ActivityRecord.forTokenLocked(iBinder) != topNonFinishingActivity) {
                    throw new SecurityException("Only focused activity can call startVoiceInteraction");
                }
                if (this.mService.mRunningVoice == null && topNonFinishingActivity.getTask().voiceSession == null && topNonFinishingActivity.voiceSession == null) {
                    if (topNonFinishingActivity.pendingVoiceInteractionStart) {
                        Slog.w("ActivityTaskManager", "Pending start of voice interaction already.");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    topNonFinishingActivity.pendingVoiceInteractionStart = true;
                    String str = topNonFinishingActivity.launchedFromFeatureId;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).startLocalVoiceInteraction(iBinder, str, bundle);
                    return;
                }
                Slog.w("ActivityTaskManager", "Already in a voice interaction, cannot start new voice interaction");
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void stopLocalVoiceInteraction(IBinder iBinder) {
        ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).stopLocalVoiceInteraction(iBinder);
    }

    public void setShowWhenLocked(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setShowWhenLocked(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setInheritShowWhenLocked(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setInheritShowWhenLocked(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setTurnScreenOn(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setTurnScreenOn(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setAllowCrossUidActivitySwitchFromBelow(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setAllowCrossUidActivitySwitchFromBelow(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void reportActivityFullyDrawn(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    this.mTaskSupervisor.getActivityMetricsLogger().notifyFullyDrawn(isInRootTaskLocked, z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void overrideActivityTransition(IBinder iBinder, boolean z, int i, int i2, int i3) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.overrideCustomTransition(z, i, i2, i3);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void clearOverrideActivityTransition(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.clearCustomTransition(z);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public void overridePendingTransition(IBinder iBinder, String str, int i, int i2, int i3) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null && isInRootTaskLocked.isState(ActivityRecord.State.RESUMED, ActivityRecord.State.PAUSING)) {
                    isInRootTaskLocked.mDisplayContent.mAppTransition.overridePendingAppTransition(str, i, i2, i3, null, null, isInRootTaskLocked.mOverrideTaskTransition);
                    isInRootTaskLocked.mTransitionController.setOverrideAnimation(TransitionInfo.AnimationOptions.makeCustomAnimOptions(str, i, i2, i3, isInRootTaskLocked.mOverrideTaskTransition), null, null);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    /* JADX WARN: Finally extract failed */
    public int setVrMode(IBinder iBinder, boolean z, ComponentName componentName) {
        ActivityRecord isInRootTaskLocked;
        this.mService.enforceSystemHasVrFeature();
        VrManagerInternal vrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (isInRootTaskLocked == null) {
            throw new IllegalArgumentException();
        }
        int hasVrPackage = vrManagerInternal.hasVrPackage(componentName, isInRootTaskLocked.mUserId);
        if (hasVrPackage != 0) {
            return hasVrPackage;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (!z) {
                    componentName = null;
                }
                isInRootTaskLocked.requestedVrComponent = componentName;
                if (isInRootTaskLocked.isFocusedActivityOnDisplay()) {
                    this.mService.applyUpdateVrModeLocked(isInRootTaskLocked);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th2) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th2;
        }
    }

    public void setRecentsScreenshotEnabled(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setRecentsScreenshotEnabled(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void invalidateHomeTaskSnapshot(IBinder iBinder) {
        ActivityRecord isInRootTaskLocked;
        if (iBinder == null) {
            ActivityTaskManagerService.enforceTaskPermission("invalidateHomeTaskSnapshot");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (iBinder == null) {
                    Task rootHomeTask = this.mService.mRootWindowContainer.getDefaultTaskDisplayArea().getRootHomeTask();
                    isInRootTaskLocked = rootHomeTask != null ? rootHomeTask.topRunningActivity() : null;
                } else {
                    isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                }
                if (isInRootTaskLocked != null && isInRootTaskLocked.isActivityTypeHome()) {
                    this.mService.mWindowManager.mTaskSnapshotController.removeSnapshotCache(isInRootTaskLocked.getTask().mTaskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void dismissKeyguard(IBinder iBinder, IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        if (charSequence != null) {
            this.mService.mAmInternal.enforceCallingPermission("android.permission.SHOW_KEYGUARD_MESSAGE", "dismissKeyguard");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.mKeyguardController.dismissKeyguard(iBinder, iKeyguardDismissCallback, charSequence);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerRemoteAnimations(IBinder iBinder, RemoteAnimationDefinition remoteAnimationDefinition) {
        this.mService.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimations");
        remoteAnimationDefinition.setCallingPidUid(Binder.getCallingPid(), Binder.getCallingUid());
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.registerRemoteAnimations(remoteAnimationDefinition);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unregisterRemoteAnimations(IBinder iBinder) {
        this.mService.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "unregisterRemoteAnimations");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.unregisterRemoteAnimations();
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean isRelativeTaskRootActivity(final ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        TaskFragment taskFragment = activityRecord.getTaskFragment();
        return activityRecord == taskFragment.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityClientController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isRelativeTaskRootActivity$5;
                lambda$isRelativeTaskRootActivity$5 = ActivityClientController.lambda$isRelativeTaskRootActivity$5(ActivityRecord.this, (ActivityRecord) obj);
                return lambda$isRelativeTaskRootActivity$5;
            }
        }, false) && activityRecord2.getTaskFragment().getCompanionTaskFragment() == taskFragment;
    }

    public static /* synthetic */ boolean lambda$isRelativeTaskRootActivity$5(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        return !activityRecord2.finishing || activityRecord2 == activityRecord;
    }

    public final boolean isTopActivityInTaskFragment(ActivityRecord activityRecord) {
        return activityRecord.getTaskFragment().topRunningActivity() == activityRecord;
    }

    public final void requestCallbackFinish(IRequestFinishCallback iRequestFinishCallback) {
        try {
            iRequestFinishCallback.requestFinish();
        } catch (RemoteException e) {
            Slog.e("ActivityTaskManager", "Failed to invoke request finish callback", e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0046  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0055 A[Catch: all -> 0x0086, TryCatch #1 {all -> 0x008c, blocks: (B:3:0x0004, B:4:0x0006, B:31:0x005f, B:35:0x0068, B:39:0x0078, B:37:0x006e, B:42:0x007f, B:5:0x0007, B:7:0x0010, B:10:0x0018, B:14:0x0027, B:16:0x0037, B:25:0x0047, B:27:0x0055, B:29:0x0059, B:30:0x005e, B:18:0x0039, B:20:0x003f, B:21:0x0042), top: B:52:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0058  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onBackPressed(IBinder iBinder, IRequestFinishCallback iRequestFinishCallback) {
        Intent intent;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    Task task = isInRootTaskLocked.getTask();
                    ActivityRecord rootActivity = task.getRootActivity(false, true);
                    boolean z = isInRootTaskLocked == rootActivity;
                    if (!z) {
                        if (!isRelativeTaskRootActivity(isInRootTaskLocked, rootActivity)) {
                            requestCallbackFinish(iRequestFinishCallback);
                        }
                        if (z) {
                        }
                        boolean isTopActivityInTaskFragment = isTopActivityInTaskFragment(isInRootTaskLocked);
                        if (!rootActivity.mActivityComponent.equals(task.realActivity)) {
                        }
                        boolean isLaunchSourceType = rootActivity.isLaunchSourceType(2);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        if (intent == null) {
                        }
                        requestCallbackFinish(iRequestFinishCallback);
                        return;
                    }
                    if (this.mService.mWindowOrganizerController.mTaskOrganizerController.handleInterceptBackPressedOnTaskRoot(isInRootTaskLocked.getRootTask())) {
                    }
                    if (z) {
                        isInRootTaskLocked = rootActivity;
                    }
                    boolean isTopActivityInTaskFragment2 = isTopActivityInTaskFragment(isInRootTaskLocked);
                    intent = !rootActivity.mActivityComponent.equals(task.realActivity) ? rootActivity.intent : null;
                    boolean isLaunchSourceType2 = rootActivity.isLaunchSourceType(2);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (intent == null && isTopActivityInTaskFragment2 && ((isLaunchSourceType2 && ActivityRecord.isMainIntent(intent)) || isLauncherActivity(intent.getComponent()))) {
                        moveActivityTaskToBack(iBinder, true);
                        return;
                    } else {
                        requestCallbackFinish(iRequestFinishCallback);
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean isLauncherActivity(ComponentName componentName) {
        ParceledListSlice queryIntentActivities;
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(componentName.getPackageName());
        try {
            queryIntentActivities = this.mService.getPackageManager().queryIntentActivities(intent, (String) null, 0L, this.mContext.getUserId());
        } catch (RemoteException e) {
            Slog.e("ActivityTaskManager", "Failed to query intent activities", e);
        }
        if (queryIntentActivities == null) {
            return false;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities.getList()) {
            if (resolveInfo.getComponentInfo().getComponentName().equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    public void enableTaskLocaleOverride(IBinder iBinder) {
        if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
            return;
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.getTask().mAlignActivityLocaleWithTask = true;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isRequestedToLaunchInTaskFragment(IBinder iBinder, IBinder iBinder2) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean z = isInRootTaskLocked.mRequestedLaunchingTaskFragmentToken == iBinder2;
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }
}
