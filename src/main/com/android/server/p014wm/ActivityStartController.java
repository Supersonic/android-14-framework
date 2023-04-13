package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.app.BackgroundStartPrivileges;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import android.view.RemoteAnimationAdapter;
import android.window.RemoteTransition;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p014wm.ActivityMetricsLogger;
import com.android.server.p014wm.ActivityStarter;
import com.android.server.uri.NeededUriGrants;
import com.android.server.uri.UriGrantsManagerInternal;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.IntFunction;
/* renamed from: com.android.server.wm.ActivityStartController */
/* loaded from: classes2.dex */
public class ActivityStartController {
    public final BackgroundActivityStartController mBalController;
    public boolean mCheckedForSetup;
    public final ActivityStarter.Factory mFactory;
    public boolean mInExecution;
    public ActivityRecord mLastHomeActivityStartRecord;
    public int mLastHomeActivityStartResult;
    public ActivityStarter mLastStarter;
    public final PendingRemoteAnimationRegistry mPendingRemoteAnimationRegistry;
    public final ActivityTaskManagerService mService;
    public final ActivityTaskSupervisor mSupervisor;
    public ActivityRecord[] tmpOutRecord;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ActivityStartController(ActivityTaskManagerService activityTaskManagerService) {
        this(activityTaskManagerService, r0, new ActivityStarter.DefaultFactory(activityTaskManagerService, r0, new ActivityStartInterceptor(activityTaskManagerService, r0)));
        ActivityTaskSupervisor activityTaskSupervisor = activityTaskManagerService.mTaskSupervisor;
    }

    @VisibleForTesting
    public ActivityStartController(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor, ActivityStarter.Factory factory) {
        this.tmpOutRecord = new ActivityRecord[1];
        this.mCheckedForSetup = false;
        this.mInExecution = false;
        this.mService = activityTaskManagerService;
        this.mSupervisor = activityTaskSupervisor;
        this.mFactory = factory;
        factory.setController(this);
        this.mPendingRemoteAnimationRegistry = new PendingRemoteAnimationRegistry(activityTaskManagerService.mGlobalLock, activityTaskManagerService.f1161mH);
        this.mBalController = new BackgroundActivityStartController(activityTaskManagerService, activityTaskSupervisor);
    }

    public ActivityStarter obtainStarter(Intent intent, String str) {
        return this.mFactory.obtain().setIntent(intent).setReason(str);
    }

    public void onExecutionStarted() {
        this.mInExecution = true;
    }

    public boolean isInExecution() {
        return this.mInExecution;
    }

    public void onExecutionComplete(ActivityStarter activityStarter) {
        this.mInExecution = false;
        if (this.mLastStarter == null) {
            this.mLastStarter = this.mFactory.obtain();
        }
        this.mLastStarter.set(activityStarter);
        this.mFactory.recycle(activityStarter);
    }

    public void postStartActivityProcessingForLastStarter(ActivityRecord activityRecord, int i, Task task) {
        ActivityStarter activityStarter = this.mLastStarter;
        if (activityStarter == null) {
            return;
        }
        activityStarter.postStartActivityProcessing(activityRecord, i, task);
    }

    public void startHomeActivity(Intent intent, ActivityInfo activityInfo, String str, TaskDisplayArea taskDisplayArea) {
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchWindowingMode(1);
        if (!ActivityRecord.isResolverActivity(activityInfo.name)) {
            makeBasic.setLaunchActivityType(2);
        }
        makeBasic.setLaunchDisplayId(taskDisplayArea.getDisplayId());
        makeBasic.setLaunchTaskDisplayArea(taskDisplayArea.mRemoteToken.toWindowContainerToken());
        this.mSupervisor.beginDeferResume();
        try {
            Task orCreateRootHomeTask = taskDisplayArea.getOrCreateRootHomeTask(true);
            this.mSupervisor.endDeferResume();
            this.mLastHomeActivityStartResult = obtainStarter(intent, "startHomeActivity: " + str).setOutActivity(this.tmpOutRecord).setCallingUid(0).setActivityInfo(activityInfo).setActivityOptions(makeBasic.toBundle()).execute();
            this.mLastHomeActivityStartRecord = this.tmpOutRecord[0];
            if (orCreateRootHomeTask.mInResumeTopActivity) {
                this.mSupervisor.scheduleResumeTopActivities();
            }
        } catch (Throwable th) {
            this.mSupervisor.endDeferResume();
            throw th;
        }
    }

    public void startSetupActivity() {
        Bundle bundle;
        if (this.mCheckedForSetup) {
            return;
        }
        ContentResolver contentResolver = this.mService.mContext.getContentResolver();
        if (this.mService.mFactoryTest == 1 || Settings.Global.getInt(contentResolver, "device_provisioned", 0) == 0) {
            return;
        }
        this.mCheckedForSetup = true;
        Intent intent = new Intent("android.intent.action.UPGRADE_SETUP");
        List<ResolveInfo> queryIntentActivities = this.mService.mContext.getPackageManager().queryIntentActivities(intent, 1049728);
        if (queryIntentActivities.isEmpty()) {
            return;
        }
        ResolveInfo resolveInfo = queryIntentActivities.get(0);
        Bundle bundle2 = resolveInfo.activityInfo.metaData;
        String string = bundle2 != null ? bundle2.getString("android.SETUP_VERSION") : null;
        if (string == null && (bundle = resolveInfo.activityInfo.applicationInfo.metaData) != null) {
            string = bundle.getString("android.SETUP_VERSION");
        }
        String stringForUser = Settings.Secure.getStringForUser(contentResolver, "last_setup_shown", contentResolver.getUserId());
        if (string == null || string.equals(stringForUser)) {
            return;
        }
        intent.setFlags(268435456);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
        obtainStarter(intent, "startSetupActivity").setCallingUid(0).setActivityInfo(resolveInfo.activityInfo).execute();
    }

    public int checkTargetUser(int i, boolean z, int i2, int i3, String str) {
        if (z) {
            return this.mService.handleIncomingUser(i2, i3, i, str);
        }
        this.mService.mAmInternal.ensureNotSpecialUser(i);
        return i;
    }

    public final int startActivityInPackage(int i, int i2, int i3, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i4, int i5, SafeActivityOptions safeActivityOptions, int i6, Task task, String str5, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
        return obtainStarter(intent, str5).setCallingUid(i).setRealCallingPid(i2).setRealCallingUid(i3).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setResultTo(iBinder).setResultWho(str4).setRequestCode(i4).setStartFlags(i5).setActivityOptions(safeActivityOptions).setUserId(checkTargetUser(i6, z, i2, i3, str5)).setInTask(task).setOriginatingPendingIntent(pendingIntentRecord).setBackgroundStartPrivileges(backgroundStartPrivileges).execute();
    }

    public final int startActivitiesInPackage(int i, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, SafeActivityOptions safeActivityOptions, int i2, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
        return startActivitiesInPackage(i, 0, -1, str, str2, intentArr, strArr, iBinder, safeActivityOptions, i2, z, pendingIntentRecord, backgroundStartPrivileges);
    }

    public final int startActivitiesInPackage(int i, int i2, int i3, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, SafeActivityOptions safeActivityOptions, int i4, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
        return startActivities(null, i, i2, i3, str, str2, intentArr, strArr, iBinder, safeActivityOptions, checkTargetUser(i4, z, Binder.getCallingPid(), Binder.getCallingUid(), "startActivityInPackage"), "startActivityInPackage", pendingIntentRecord, backgroundStartPrivileges);
    }

    public int startActivities(IApplicationThread iApplicationThread, int i, int i2, int i3, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, SafeActivityOptions safeActivityOptions, int i4, String str3, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
        int i5;
        int i6;
        int i7;
        NeededUriGrants checkGrantUriPermissionFromIntent;
        SparseArray sparseArray;
        Intent[] intentArr2;
        SafeActivityOptions safeActivityOptions2;
        if (intentArr == null) {
            throw new NullPointerException("intents is null");
        }
        if (strArr == null) {
            throw new NullPointerException("resolvedTypes is null");
        }
        if (intentArr.length != strArr.length) {
            throw new IllegalArgumentException("intents are length different than resolvedTypes");
        }
        int callingPid = i2 != 0 ? i2 : Binder.getCallingPid();
        int i8 = i3;
        if (i8 == -1) {
            i8 = Binder.getCallingUid();
        }
        if (i >= 0) {
            i5 = i;
            i6 = -1;
        } else if (iApplicationThread == null) {
            i6 = callingPid;
            i5 = i8;
        } else {
            i5 = -1;
            i6 = -1;
        }
        int computeResolveFilterUid = ActivityStarter.computeResolveFilterUid(i5, i8, -10000);
        SparseArray sparseArray2 = new SparseArray();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        SafeActivityOptions selectiveCloneLaunchOptions = safeActivityOptions != null ? safeActivityOptions.selectiveCloneLaunchOptions() : null;
        try {
            Intent[] intentArr3 = (Intent[]) ArrayUtils.filterNotNull(intentArr, new IntFunction() { // from class: com.android.server.wm.ActivityStartController$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i9) {
                    Intent[] lambda$startActivities$0;
                    lambda$startActivities$0 = ActivityStartController.lambda$startActivities$0(i9);
                    return lambda$startActivities$0;
                }
            });
            int length = intentArr3.length;
            ActivityStarter[] activityStarterArr = new ActivityStarter[length];
            int i9 = 0;
            while (i9 < intentArr3.length) {
                Intent intent = intentArr3[i9];
                if (intent.hasFileDescriptors()) {
                    throw new IllegalArgumentException("File descriptors passed in Intent");
                }
                boolean z = intent.getComponent() != null;
                Intent intent2 = new Intent(intent);
                int i10 = i9;
                ActivityStarter[] activityStarterArr2 = activityStarterArr;
                int i11 = length;
                SparseArray sparseArray3 = sparseArray2;
                int i12 = computeResolveFilterUid;
                int i13 = i6;
                ActivityInfo activityInfoForUser = this.mService.mAmInternal.getActivityInfoForUser(this.mSupervisor.resolveActivity(intent2, strArr[i9], 0, null, i4, i12, i13), i4);
                if (activityInfoForUser != null) {
                    try {
                        UriGrantsManagerInternal uriGrantsManagerInternal = this.mSupervisor.mService.mUgmInternal;
                        ApplicationInfo applicationInfo = activityInfoForUser.applicationInfo;
                        computeResolveFilterUid = i12;
                        checkGrantUriPermissionFromIntent = uriGrantsManagerInternal.checkGrantUriPermissionFromIntent(intent2, computeResolveFilterUid, applicationInfo.packageName, UserHandle.getUserId(applicationInfo.uid));
                        ApplicationInfo applicationInfo2 = activityInfoForUser.applicationInfo;
                        if ((applicationInfo2.privateFlags & 2) != 0) {
                            throw new IllegalArgumentException("FLAG_CANT_SAVE_STATE not supported here");
                        }
                        sparseArray = sparseArray3;
                        sparseArray.put(applicationInfo2.uid, applicationInfo2.packageName);
                    } catch (SecurityException unused) {
                        Slog.d("ActivityTaskManager", "Not allowed to start activity since no uri permission.");
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return -96;
                    }
                } else {
                    computeResolveFilterUid = i12;
                    sparseArray = sparseArray3;
                    checkGrantUriPermissionFromIntent = null;
                }
                boolean z2 = i10 == intentArr3.length - 1;
                if (z2) {
                    safeActivityOptions2 = safeActivityOptions;
                    intentArr2 = intentArr3;
                } else {
                    intentArr2 = intentArr3;
                    safeActivityOptions2 = selectiveCloneLaunchOptions;
                }
                activityStarterArr2[i10] = obtainStarter(intent2, str3).setIntentGrants(checkGrantUriPermissionFromIntent).setCaller(iApplicationThread).setResolvedType(strArr[i10]).setActivityInfo(activityInfoForUser).setRequestCode(-1).setCallingPid(i13).setCallingUid(i5).setCallingPackage(str).setCallingFeatureId(str2).setRealCallingPid(callingPid).setRealCallingUid(i8).setActivityOptions(safeActivityOptions2).setComponentSpecified(z).setAllowPendingRemoteAnimationRegistryLookup(z2).setOriginatingPendingIntent(pendingIntentRecord).setBackgroundStartPrivileges(backgroundStartPrivileges);
                intentArr3 = intentArr2;
                sparseArray2 = sparseArray;
                activityStarterArr = activityStarterArr2;
                length = i11;
                i6 = i13;
                i9 = i10 + 1;
            }
            ActivityStarter[] activityStarterArr3 = activityStarterArr;
            int i14 = length;
            SparseArray sparseArray4 = sparseArray2;
            if (sparseArray4.size() > 1) {
                StringBuilder sb = new StringBuilder("startActivities: different apps [");
                int size = sparseArray4.size();
                int i15 = 0;
                while (i15 < size) {
                    sb.append((String) sparseArray4.valueAt(i15));
                    sb.append(i15 == size + (-1) ? "]" : ", ");
                    i15++;
                }
                sb.append(" from ");
                sb.append(str);
                Slog.wtf("ActivityTaskManager", sb.toString());
                i7 = 1;
            } else {
                i7 = 1;
            }
            ActivityRecord[] activityRecordArr = new ActivityRecord[i7];
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.deferWindowLayout();
                this.mService.mWindowManager.mStartingSurfaceController.beginDeferAddStartingWindow();
                IBinder iBinder2 = iBinder;
                int i16 = 0;
                while (true) {
                    int i17 = i14;
                    if (i16 >= i17) {
                        this.mService.mWindowManager.mStartingSurfaceController.endDeferAddStartingWindow(safeActivityOptions != null ? safeActivityOptions.getOriginalOptions() : null);
                        this.mService.continueWindowLayout();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return 0;
                    }
                    int execute = activityStarterArr3[i16].setResultTo(iBinder2).setOutActivity(activityRecordArr).execute();
                    if (execute < 0) {
                        for (int i18 = i16 + 1; i18 < i17; i18++) {
                            this.mFactory.recycle(activityStarterArr3[i18]);
                        }
                        this.mService.mWindowManager.mStartingSurfaceController.endDeferAddStartingWindow(safeActivityOptions != null ? safeActivityOptions.getOriginalOptions() : null);
                        this.mService.continueWindowLayout();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return execute;
                    }
                    ActivityRecord activityRecord = activityRecordArr[0];
                    if (activityRecord == null || activityRecord.getUid() != computeResolveFilterUid) {
                        if (i16 < i17 - 1) {
                            activityStarterArr3[i16 + 1].getIntent().addFlags(268435456);
                        }
                        iBinder2 = iBinder;
                    } else {
                        iBinder2 = activityRecord.token;
                    }
                    i16++;
                    i14 = i17;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ Intent[] lambda$startActivities$0(int i) {
        return new Intent[i];
    }

    public int startActivityInTaskFragment(TaskFragment taskFragment, Intent intent, Bundle bundle, IBinder iBinder, int i, int i2, IBinder iBinder2) {
        ActivityRecord forTokenLocked = iBinder != null ? ActivityRecord.forTokenLocked(iBinder) : null;
        return obtainStarter(intent, "startActivityInTaskFragment").setActivityOptions(bundle).setInTaskFragment(taskFragment).setResultTo(iBinder).setRequestCode(-1).setCallingUid(i).setCallingPid(i2).setRealCallingUid(i).setRealCallingPid(i2).setUserId(forTokenLocked != null ? forTokenLocked.mUserId : this.mService.getCurrentUserId()).setErrorCallbackToken(iBinder2).execute();
    }

    public boolean startExistingRecentsIfPossible(Intent intent, final ActivityOptions activityOptions) {
        final Task rootTask = this.mService.mRootWindowContainer.getDefaultTaskDisplayArea().getRootTask(0, this.mService.getRecentTasks().getRecentsComponent().equals(intent.getComponent()) ? 3 : 2);
        if (rootTask == null) {
            return false;
        }
        final RemoteTransition remoteTransition = activityOptions.getRemoteTransition();
        final ActivityRecord activityRecord = rootTask.topRunningActivity();
        if (activityRecord == null || activityRecord.isVisibleRequested() || !activityRecord.attachedToProcess() || remoteTransition == null || !activityRecord.mActivityComponent.equals(intent.getComponent()) || activityRecord.mDisplayContent.isKeyguardLocked()) {
            return false;
        }
        this.mService.mRootWindowContainer.startPowerModeLaunchIfNeeded(true, activityRecord);
        final ActivityMetricsLogger.LaunchingState notifyActivityLaunching = this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunching(intent);
        final Transition transition = new Transition(3, 0, activityRecord.mTransitionController, this.mService.mWindowManager.mSyncEngine);
        if (activityRecord.mTransitionController.isCollecting()) {
            this.mService.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.ActivityStartController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityStartController.lambda$startExistingRecentsIfPossible$1(ActivityRecord.this, transition);
                }
            }, new Runnable() { // from class: com.android.server.wm.ActivityStartController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityStartController.this.lambda$startExistingRecentsIfPossible$2(activityRecord, transition, activityOptions, rootTask, notifyActivityLaunching, remoteTransition);
                }
            });
        } else {
            activityRecord.mTransitionController.moveToCollecting(transition);
            startExistingRecentsIfPossibleInner(activityOptions, activityRecord, rootTask, notifyActivityLaunching, remoteTransition, transition);
        }
        return true;
    }

    public static /* synthetic */ void lambda$startExistingRecentsIfPossible$1(ActivityRecord activityRecord, Transition transition) {
        if (activityRecord.isAttached()) {
            activityRecord.mTransitionController.moveToCollecting(transition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startExistingRecentsIfPossible$2(ActivityRecord activityRecord, Transition transition, ActivityOptions activityOptions, Task task, ActivityMetricsLogger.LaunchingState launchingState, RemoteTransition remoteTransition) {
        if (activityRecord.isAttached() && transition.isCollecting()) {
            startExistingRecentsIfPossibleInner(activityOptions, activityRecord, task, launchingState, remoteTransition, transition);
        }
    }

    public final void startExistingRecentsIfPossibleInner(ActivityOptions activityOptions, ActivityRecord activityRecord, Task task, ActivityMetricsLogger.LaunchingState launchingState, RemoteTransition remoteTransition, Transition transition) {
        Task task2 = activityRecord.getTask();
        this.mService.deferWindowLayout();
        try {
            TransitionController transitionController = activityRecord.mTransitionController;
            if (transitionController.getTransitionPlayer() != null) {
                transitionController.requestStartTransition(transition, task2, remoteTransition, null);
                transitionController.collect(task2);
                transitionController.setTransientLaunch(activityRecord, TaskDisplayArea.getRootTaskAbove(task));
            } else {
                transition.abort();
            }
            task2.moveToFront("startExistingRecents");
            task2.mInResumeTopActivity = true;
            task2.resumeTopActivity(null, activityOptions, true);
            this.mSupervisor.getActivityMetricsLogger().notifyActivityLaunched(launchingState, 2, false, activityRecord, activityOptions);
        } finally {
            task2.mInResumeTopActivity = false;
            this.mService.continueWindowLayout();
        }
    }

    public void registerRemoteAnimationForNextActivityStart(String str, RemoteAnimationAdapter remoteAnimationAdapter, IBinder iBinder) {
        this.mPendingRemoteAnimationRegistry.addPendingAnimation(str, remoteAnimationAdapter, iBinder);
    }

    public PendingRemoteAnimationRegistry getPendingRemoteAnimationRegistry() {
        return this.mPendingRemoteAnimationRegistry;
    }

    public void dumpLastHomeActivityStartResult(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("mLastHomeActivityStartResult=");
        printWriter.println(this.mLastHomeActivityStartResult);
    }

    public void dump(PrintWriter printWriter, String str, String str2) {
        boolean z;
        ActivityRecord activityRecord;
        boolean z2 = true;
        boolean z3 = false;
        boolean z4 = str2 != null;
        ActivityRecord activityRecord2 = this.mLastHomeActivityStartRecord;
        if (activityRecord2 == null || (z4 && !str2.equals(activityRecord2.packageName))) {
            z = false;
        } else {
            dumpLastHomeActivityStartResult(printWriter, str);
            printWriter.print(str);
            printWriter.println("mLastHomeActivityStartRecord:");
            this.mLastHomeActivityStartRecord.dump(printWriter, str + "  ", true);
            z = true;
        }
        ActivityStarter activityStarter = this.mLastStarter;
        if (activityStarter != null) {
            if (!z4 || activityStarter.relatedToPackage(str2) || ((activityRecord = this.mLastHomeActivityStartRecord) != null && str2.equals(activityRecord.packageName))) {
                z3 = true;
            }
            if (z3) {
                if (z) {
                    z2 = z;
                } else {
                    dumpLastHomeActivityStartResult(printWriter, str);
                }
                printWriter.print(str);
                printWriter.println("mLastStarter:");
                this.mLastStarter.dump(printWriter, str + "  ");
                if (z4) {
                    return;
                }
                z = z2;
            }
        }
        if (z) {
            return;
        }
        printWriter.print(str);
        printWriter.println("(nothing)");
    }

    public BackgroundActivityStartController getBackgroundActivityLaunchController() {
        return this.mBalController;
    }
}
