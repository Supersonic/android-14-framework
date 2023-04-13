package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.BackgroundStartPrivileges;
import android.app.IAppTask;
import android.app.IApplicationThread;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserHandle;
/* renamed from: com.android.server.wm.AppTaskImpl */
/* loaded from: classes2.dex */
public class AppTaskImpl extends IAppTask.Stub {
    public final int mCallingUid;
    public final ActivityTaskManagerService mService;
    public final int mTaskId;

    public AppTaskImpl(ActivityTaskManagerService activityTaskManagerService, int i, int i2) {
        this.mService = activityTaskManagerService;
        this.mTaskId = i;
        this.mCallingUid = i2;
    }

    public final void checkCallerOrSystemOrRoot() {
        if (this.mCallingUid == Binder.getCallingUid() || 1000 == Binder.getCallingUid() || Binder.getCallingUid() == 0) {
            return;
        }
        throw new SecurityException("Caller " + this.mCallingUid + " does not match caller of getAppTasks(): " + Binder.getCallingUid());
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            throw ActivityTaskManagerService.logAndRethrowRuntimeExceptionOnTransact("AppTaskImpl", e);
        }
    }

    public void finishAndRemoveTask() {
        checkCallerOrSystemOrRoot();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int callingUid = Binder.getCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (!this.mService.mTaskSupervisor.removeTaskById(this.mTaskId, false, true, "finish-and-remove-task", callingUid)) {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public ActivityManager.RecentTaskInfo getTaskInfo() {
        ActivityManager.RecentTaskInfo createRecentTaskInfo;
        checkCallerOrSystemOrRoot();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                Task anyTaskForId = this.mService.mRootWindowContainer.anyTaskForId(this.mTaskId, 1);
                if (anyTaskForId == null) {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
                createRecentTaskInfo = this.mService.getRecentTasks().createRecentTaskInfo(anyTaskForId, false, true);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return createRecentTaskInfo;
    }

    public void moveToFront(IApplicationThread iApplicationThread, String str) {
        checkCallerOrSystemOrRoot();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        this.mService.assertPackageMatchesCallingUid(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (!this.mService.getActivityStartController().getBackgroundActivityLaunchController().shouldAbortBackgroundActivityStart(callingUid, callingPid, str, -1, -1, iApplicationThread != null ? this.mService.getProcessController(iApplicationThread) : null, null, BackgroundStartPrivileges.NONE, null, null) || this.mService.isBackgroundActivityStartsEnabled()) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            this.mService.mTaskSupervisor.startActivityFromRecents(callingPid, callingUid, this.mTaskId, null);
                            return;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        th = th;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int startActivity(IBinder iBinder, String str, String str2, Intent intent, String str3, Bundle bundle) {
        Task anyTaskForId;
        IApplicationThread asInterface;
        checkCallerOrSystemOrRoot();
        this.mService.assertPackageMatchesCallingUid(str);
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                anyTaskForId = this.mService.mRootWindowContainer.anyTaskForId(this.mTaskId, 1);
                if (anyTaskForId == null) {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
                asInterface = IApplicationThread.Stub.asInterface(iBinder);
                if (asInterface == null) {
                    throw new IllegalArgumentException("Bad app thread " + asInterface);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return this.mService.getActivityStartController().obtainStarter(intent, "AppTaskImpl").setCaller(asInterface).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setActivityOptions(bundle).setUserId(callingUserId).setInTask(anyTaskForId).execute();
    }

    public void setExcludeFromRecents(boolean z) {
        checkCallerOrSystemOrRoot();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                Task anyTaskForId = this.mService.mRootWindowContainer.anyTaskForId(this.mTaskId, 1);
                if (anyTaskForId == null) {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
                Intent baseIntent = anyTaskForId.getBaseIntent();
                if (z) {
                    baseIntent.addFlags(8388608);
                } else {
                    baseIntent.setFlags(baseIntent.getFlags() & (-8388609));
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }
}
