package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.TaskInfo;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.BlockedAppActivity;
import com.android.internal.app.HarmfulAppWarningActivity;
import com.android.internal.app.SuspendedAppActivity;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p014wm.ActivityInterceptorCallback;
import java.util.Objects;
/* renamed from: com.android.server.wm.ActivityStartInterceptor */
/* loaded from: classes2.dex */
public class ActivityStartInterceptor {
    public ActivityInfo mAInfo;
    public ActivityOptions mActivityOptions;
    public String mCallingFeatureId;
    public String mCallingPackage;
    public int mCallingPid;
    public int mCallingUid;
    public Task mInTask;
    public TaskFragment mInTaskFragment;
    public Intent mIntent;
    public ResolveInfo mRInfo;
    public int mRealCallingPid;
    public int mRealCallingUid;
    public String mResolvedType;
    public final RootWindowContainer mRootWindowContainer;
    public final ActivityTaskManagerService mService;
    public final Context mServiceContext;
    public int mStartFlags;
    public final ActivityTaskSupervisor mSupervisor;
    public int mUserId;
    public UserManager mUserManager;

    public ActivityStartInterceptor(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor) {
        this(activityTaskManagerService, activityTaskSupervisor, activityTaskManagerService.mRootWindowContainer, activityTaskManagerService.mContext);
    }

    @VisibleForTesting
    public ActivityStartInterceptor(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor, RootWindowContainer rootWindowContainer, Context context) {
        this.mService = activityTaskManagerService;
        this.mSupervisor = activityTaskSupervisor;
        this.mRootWindowContainer = rootWindowContainer;
        this.mServiceContext = context;
    }

    public void setStates(int i, int i2, int i3, int i4, String str, String str2) {
        this.mRealCallingPid = i2;
        this.mRealCallingUid = i3;
        this.mUserId = i;
        this.mStartFlags = i4;
        this.mCallingPackage = str;
        this.mCallingFeatureId = str2;
    }

    public final IntentSender createIntentSenderForOriginalIntent(int i, int i2) {
        ActivityOptions makeBasic;
        Bundle deferCrossProfileAppsAnimationIfNecessary = deferCrossProfileAppsAnimationIfNecessary();
        TaskFragment launchTaskFragment = getLaunchTaskFragment();
        if (launchTaskFragment != null) {
            if (deferCrossProfileAppsAnimationIfNecessary != null) {
                makeBasic = ActivityOptions.fromBundle(deferCrossProfileAppsAnimationIfNecessary);
            } else {
                makeBasic = ActivityOptions.makeBasic();
            }
            makeBasic.setLaunchTaskFragmentToken(launchTaskFragment.getFragmentToken());
            deferCrossProfileAppsAnimationIfNecessary = makeBasic.toBundle();
        }
        return new IntentSender(this.mService.getIntentSenderLocked(2, this.mCallingPackage, this.mCallingFeatureId, i, this.mUserId, null, null, 0, new Intent[]{this.mIntent}, new String[]{this.mResolvedType}, i2, deferCrossProfileAppsAnimationIfNecessary));
    }

    public final TaskFragment getLaunchTaskFragment() {
        IBinder launchTaskFragmentToken;
        TaskFragment taskFragment = this.mInTaskFragment;
        if (taskFragment != null) {
            return taskFragment;
        }
        ActivityOptions activityOptions = this.mActivityOptions;
        if (activityOptions == null || (launchTaskFragmentToken = activityOptions.getLaunchTaskFragmentToken()) == null) {
            return null;
        }
        return TaskFragment.fromTaskFragmentToken(launchTaskFragmentToken, this.mService);
    }

    public boolean intercept(Intent intent, ResolveInfo resolveInfo, ActivityInfo activityInfo, String str, Task task, TaskFragment taskFragment, int i, int i2, ActivityOptions activityOptions) {
        ActivityInterceptorCallback.ActivityInterceptResult onInterceptActivityLaunch;
        this.mUserManager = UserManager.get(this.mServiceContext);
        this.mIntent = intent;
        this.mCallingPid = i;
        this.mCallingUid = i2;
        this.mRInfo = resolveInfo;
        this.mAInfo = activityInfo;
        this.mResolvedType = str;
        this.mInTask = task;
        this.mInTaskFragment = taskFragment;
        this.mActivityOptions = activityOptions;
        if (interceptQuietProfileIfNeeded() || interceptSuspendedPackageIfNeeded() || interceptLockTaskModeViolationPackageIfNeeded() || interceptHarmfulAppIfNeeded() || interceptLockedManagedProfileIfNeeded()) {
            return true;
        }
        SparseArray<ActivityInterceptorCallback> activityInterceptorCallbacks = this.mService.getActivityInterceptorCallbacks();
        ActivityInterceptorCallback.ActivityInterceptorInfo interceptorInfo = getInterceptorInfo(null);
        for (int i3 = 0; i3 < activityInterceptorCallbacks.size(); i3++) {
            if (shouldInterceptActivityLaunch(activityInterceptorCallbacks.keyAt(i3), interceptorInfo) && (onInterceptActivityLaunch = activityInterceptorCallbacks.valueAt(i3).onInterceptActivityLaunch(interceptorInfo)) != null) {
                this.mIntent = onInterceptActivityLaunch.getIntent();
                this.mActivityOptions = onInterceptActivityLaunch.getActivityOptions();
                this.mCallingPid = this.mRealCallingPid;
                this.mCallingUid = this.mRealCallingUid;
                if (onInterceptActivityLaunch.isActivityResolved()) {
                    return true;
                }
                ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(this.mIntent, null, this.mUserId, 0, this.mRealCallingUid, this.mRealCallingPid);
                this.mRInfo = resolveIntent;
                this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
                return true;
            }
        }
        return false;
    }

    public final boolean hasCrossProfileAnimation() {
        ActivityOptions activityOptions = this.mActivityOptions;
        return activityOptions != null && activityOptions.getAnimationType() == 12;
    }

    public final Bundle deferCrossProfileAppsAnimationIfNecessary() {
        if (hasCrossProfileAnimation()) {
            this.mActivityOptions = null;
            return ActivityOptions.makeOpenCrossProfileAppsAnimation().toBundle();
        }
        return null;
    }

    public final boolean interceptQuietProfileIfNeeded() {
        if (this.mUserManager.isQuietModeEnabled(UserHandle.of(this.mUserId))) {
            this.mIntent = UnlaunchableAppActivity.createInQuietModeDialogIntent(this.mUserId, createIntentSenderForOriginalIntent(this.mCallingUid, 1342177280), this.mRInfo);
            this.mCallingPid = this.mRealCallingPid;
            this.mCallingUid = this.mRealCallingUid;
            this.mResolvedType = null;
            ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, this.mUserManager.getProfileParent(this.mUserId).id, 0, this.mRealCallingUid, this.mRealCallingPid);
            this.mRInfo = resolveIntent;
            this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
            return true;
        }
        return false;
    }

    public final boolean interceptSuspendedByAdminPackage() {
        DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        if (devicePolicyManagerInternal == null) {
            return false;
        }
        Intent createShowAdminSupportIntent = devicePolicyManagerInternal.createShowAdminSupportIntent(this.mUserId, true);
        this.mIntent = createShowAdminSupportIntent;
        createShowAdminSupportIntent.putExtra("android.app.extra.RESTRICTION", "policy_suspend_packages");
        this.mCallingPid = this.mRealCallingPid;
        this.mCallingUid = this.mRealCallingUid;
        this.mResolvedType = null;
        UserInfo profileParent = this.mUserManager.getProfileParent(this.mUserId);
        if (profileParent != null) {
            this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, profileParent.id, 0, this.mRealCallingUid, this.mRealCallingPid);
        } else {
            this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, this.mUserId, 0, this.mRealCallingUid, this.mRealCallingPid);
        }
        this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, this.mRInfo, this.mStartFlags, null);
        return true;
    }

    public final boolean interceptSuspendedPackageIfNeeded() {
        ApplicationInfo applicationInfo;
        PackageManagerInternal packageManagerInternalLocked;
        ActivityInfo activityInfo = this.mAInfo;
        if (activityInfo == null || (applicationInfo = activityInfo.applicationInfo) == null || (applicationInfo.flags & 1073741824) == 0 || (packageManagerInternalLocked = this.mService.getPackageManagerInternalLocked()) == null) {
            return false;
        }
        String str = this.mAInfo.applicationInfo.packageName;
        String suspendingPackage = packageManagerInternalLocked.getSuspendingPackage(str, this.mUserId);
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(suspendingPackage)) {
            return interceptSuspendedByAdminPackage();
        }
        Intent createSuspendedAppInterceptIntent = SuspendedAppActivity.createSuspendedAppInterceptIntent(str, suspendingPackage, packageManagerInternalLocked.getSuspendedDialogInfo(str, suspendingPackage, this.mUserId), hasCrossProfileAnimation() ? ActivityOptions.makeOpenCrossProfileAppsAnimation().toBundle() : null, createIntentSenderForOriginalIntent(this.mCallingUid, 67108864), this.mUserId);
        this.mIntent = createSuspendedAppInterceptIntent;
        int i = this.mRealCallingPid;
        this.mCallingPid = i;
        int i2 = this.mRealCallingUid;
        this.mCallingUid = i2;
        this.mResolvedType = null;
        ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(createSuspendedAppInterceptIntent, null, this.mUserId, 0, i2, i);
        this.mRInfo = resolveIntent;
        this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
        return true;
    }

    public final boolean interceptLockTaskModeViolationPackageIfNeeded() {
        ActivityInfo activityInfo = this.mAInfo;
        if (activityInfo == null || activityInfo.applicationInfo == null) {
            return false;
        }
        LockTaskController lockTaskController = this.mService.getLockTaskController();
        ActivityInfo activityInfo2 = this.mAInfo;
        if (lockTaskController.isActivityAllowed(this.mUserId, activityInfo2.applicationInfo.packageName, ActivityRecord.getLockTaskLaunchMode(activityInfo2, this.mActivityOptions))) {
            return false;
        }
        Intent createIntent = BlockedAppActivity.createIntent(this.mUserId, this.mAInfo.applicationInfo.packageName);
        this.mIntent = createIntent;
        int i = this.mRealCallingPid;
        this.mCallingPid = i;
        int i2 = this.mRealCallingUid;
        this.mCallingUid = i2;
        this.mResolvedType = null;
        ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(createIntent, null, this.mUserId, 0, i2, i);
        this.mRInfo = resolveIntent;
        this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
        return true;
    }

    public final boolean interceptLockedManagedProfileIfNeeded() {
        Task task;
        Intent interceptWithConfirmCredentialsIfNeeded = interceptWithConfirmCredentialsIfNeeded(this.mAInfo, this.mUserId);
        if (interceptWithConfirmCredentialsIfNeeded == null) {
            return false;
        }
        this.mIntent = interceptWithConfirmCredentialsIfNeeded;
        this.mCallingPid = this.mRealCallingPid;
        this.mCallingUid = this.mRealCallingUid;
        this.mResolvedType = null;
        TaskFragment launchTaskFragment = getLaunchTaskFragment();
        Task task2 = this.mInTask;
        if (task2 != null) {
            this.mIntent.putExtra("android.intent.extra.TASK_ID", task2.mTaskId);
            this.mInTask = null;
        } else if (launchTaskFragment != null && (task = launchTaskFragment.getTask()) != null) {
            this.mIntent.putExtra("android.intent.extra.TASK_ID", task.mTaskId);
        }
        if (this.mActivityOptions == null) {
            this.mActivityOptions = ActivityOptions.makeBasic();
        }
        ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, this.mUserManager.getProfileParent(this.mUserId).id, 0, this.mRealCallingUid, this.mRealCallingPid);
        this.mRInfo = resolveIntent;
        this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
        return true;
    }

    public final Intent interceptWithConfirmCredentialsIfNeeded(ActivityInfo activityInfo, int i) {
        if (this.mService.mAmInternal.shouldConfirmCredentials(i)) {
            if ((activityInfo.flags & 8388608) == 0 || !(this.mUserManager.isUserUnlocked(i) || activityInfo.directBootAware)) {
                IntentSender createIntentSenderForOriginalIntent = createIntentSenderForOriginalIntent(this.mCallingUid, 1409286144);
                Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) this.mServiceContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, i, true);
                if (createConfirmDeviceCredentialIntent == null) {
                    return null;
                }
                createConfirmDeviceCredentialIntent.setFlags(276840448);
                createConfirmDeviceCredentialIntent.putExtra("android.intent.extra.PACKAGE_NAME", activityInfo.packageName);
                createConfirmDeviceCredentialIntent.putExtra("android.intent.extra.INTENT", createIntentSenderForOriginalIntent);
                return createConfirmDeviceCredentialIntent;
            }
            return null;
        }
        return null;
    }

    public final boolean interceptHarmfulAppIfNeeded() {
        try {
            CharSequence harmfulAppWarning = this.mService.getPackageManager().getHarmfulAppWarning(this.mAInfo.packageName, this.mUserId);
            if (harmfulAppWarning == null) {
                return false;
            }
            Intent createHarmfulAppWarningIntent = HarmfulAppWarningActivity.createHarmfulAppWarningIntent(this.mServiceContext, this.mAInfo.packageName, createIntentSenderForOriginalIntent(this.mCallingUid, 1409286144), harmfulAppWarning);
            this.mIntent = createHarmfulAppWarningIntent;
            int i = this.mRealCallingPid;
            this.mCallingPid = i;
            int i2 = this.mRealCallingUid;
            this.mCallingUid = i2;
            this.mResolvedType = null;
            ResolveInfo resolveIntent = this.mSupervisor.resolveIntent(createHarmfulAppWarningIntent, null, this.mUserId, 0, i2, i);
            this.mRInfo = resolveIntent;
            this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, resolveIntent, this.mStartFlags, null);
            return true;
        } catch (RemoteException | IllegalArgumentException unused) {
            return false;
        }
    }

    public void onActivityLaunched(TaskInfo taskInfo, final ActivityRecord activityRecord) {
        SparseArray<ActivityInterceptorCallback> activityInterceptorCallbacks = this.mService.getActivityInterceptorCallbacks();
        Objects.requireNonNull(activityRecord);
        ActivityInterceptorCallback.ActivityInterceptorInfo interceptorInfo = getInterceptorInfo(new Runnable() { // from class: com.android.server.wm.ActivityStartInterceptor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ActivityRecord.this.clearOptionsAnimationForSiblings();
            }
        });
        for (int i = 0; i < activityInterceptorCallbacks.size(); i++) {
            if (shouldNotifyOnActivityLaunch(activityInterceptorCallbacks.keyAt(i), interceptorInfo)) {
                activityInterceptorCallbacks.valueAt(i).onActivityLaunched(taskInfo, activityRecord.info, interceptorInfo);
            }
        }
    }

    public final ActivityInterceptorCallback.ActivityInterceptorInfo getInterceptorInfo(Runnable runnable) {
        return new ActivityInterceptorCallback.ActivityInterceptorInfo.Builder(this.mCallingUid, this.mCallingPid, this.mRealCallingUid, this.mRealCallingPid, this.mUserId, this.mIntent, this.mRInfo, this.mAInfo).setResolvedType(this.mResolvedType).setCallingPackage(this.mCallingPackage).setCallingFeatureId(this.mCallingFeatureId).setCheckedOptions(this.mActivityOptions).setClearOptionsAnimationRunnable(runnable).build();
    }

    public final boolean shouldInterceptActivityLaunch(int i, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
        if (i == 1001) {
            return (activityInterceptorInfo.getIntent() == null || activityInterceptorInfo.getIntent().getAction() == null || !activityInterceptorInfo.getIntent().getAction().equals("android.app.sdksandbox.action.START_SANDBOXED_ACTIVITY")) ? false : true;
        }
        return true;
    }

    public final boolean shouldNotifyOnActivityLaunch(int i, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
        if (i == 1001) {
            return (activityInterceptorInfo.getIntent() == null || activityInterceptorInfo.getIntent().getAction() == null || !activityInterceptorInfo.getIntent().getAction().equals("android.app.sdksandbox.action.START_SANDBOXED_ACTIVITY")) ? false : true;
        }
        return true;
    }
}
