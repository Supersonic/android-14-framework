package com.android.server.p006am;

import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.BroadcastOptions;
import android.app.IStopUserCallback;
import android.app.IUserSwitchObserver;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.appwidget.AppWidgetManagerInternal;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.PermissionChecker;
import android.content.pm.IPackageManager;
import android.content.pm.PackagePartitions;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IProgressListener;
import android.os.IRemoteCallback;
import android.os.IUserManager;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.IntArray;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.FactoryResetter;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemServiceManager;
import com.android.server.p006am.UserController;
import com.android.server.p006am.UserState;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerService;
import com.android.server.utils.Slogf;
import com.android.server.utils.TimingsTraceAndSlog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* renamed from: com.android.server.am.UserController */
/* loaded from: classes.dex */
public class UserController implements Handler.Callback {
    public volatile boolean mAllowUserUnlocking;
    public volatile boolean mBootCompleted;
    @GuardedBy({"mCompletedEventTypes"})
    public final SparseIntArray mCompletedEventTypes;
    @GuardedBy({"mLock"})
    public volatile ArraySet<String> mCurWaitingUserSwitchCallbacks;
    @GuardedBy({"mLock"})
    public int[] mCurrentProfileIds;
    @GuardedBy({"mLock"})
    public volatile int mCurrentUserId;
    @GuardedBy({"mLock"})
    public boolean mDelayUserDataLocking;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public boolean mInitialized;
    public final Injector mInjector;
    @GuardedBy({"mLock"})
    public boolean mIsBroadcastSentForSystemUserStarted;
    @GuardedBy({"mLock"})
    public boolean mIsBroadcastSentForSystemUserStarting;
    @GuardedBy({"mLock"})
    public final ArrayList<Integer> mLastActiveUsers;
    public volatile long mLastUserUnlockingUptime;
    public final Object mLock;
    public final LockPatternUtils mLockPatternUtils;
    @GuardedBy({"mLock"})
    public int mMaxRunningUsers;
    @GuardedBy({"mLock"})
    public final List<PendingUserStart> mPendingUserStarts;
    @GuardedBy({"mLock"})
    public int[] mStartedUserArray;
    @GuardedBy({"mLock"})
    public final SparseArray<UserState> mStartedUsers;
    @ActivityManager.StopUserOnSwitch
    @GuardedBy({"mLock"})
    public int mStopUserOnSwitch;
    @GuardedBy({"mLock"})
    public String mSwitchingFromSystemUserMessage;
    @GuardedBy({"mLock"})
    public String mSwitchingToSystemUserMessage;
    @GuardedBy({"mLock"})
    public volatile int mTargetUserId;
    @GuardedBy({"mLock"})
    public ArraySet<String> mTimeoutUserSwitchCallbacks;
    public final Handler mUiHandler;
    @GuardedBy({"mUserIdToUserJourneyMap"})
    public final SparseArray<UserJourneySession> mUserIdToUserJourneyMap;
    public final UserManagerInternal.UserLifecycleListener mUserLifecycleListener;
    @GuardedBy({"mLock"})
    public final ArrayList<Integer> mUserLru;
    @GuardedBy({"mLock"})
    public final SparseIntArray mUserProfileGroupIds;
    public final RemoteCallbackList<IUserSwitchObserver> mUserSwitchObservers;
    @GuardedBy({"mLock"})
    public boolean mUserSwitchUiEnabled;

    public UserController(ActivityManagerService activityManagerService) {
        this(new Injector(activityManagerService));
    }

    @VisibleForTesting
    public UserController(Injector injector) {
        this.mLock = new Object();
        this.mCurrentUserId = 0;
        this.mTargetUserId = -10000;
        SparseArray<UserState> sparseArray = new SparseArray<>();
        this.mStartedUsers = sparseArray;
        ArrayList<Integer> arrayList = new ArrayList<>();
        this.mUserLru = arrayList;
        this.mStartedUserArray = new int[]{0};
        this.mCurrentProfileIds = new int[0];
        this.mUserProfileGroupIds = new SparseIntArray();
        this.mUserSwitchObservers = new RemoteCallbackList<>();
        this.mUserSwitchUiEnabled = true;
        this.mLastActiveUsers = new ArrayList<>();
        this.mUserIdToUserJourneyMap = new SparseArray<>();
        this.mCompletedEventTypes = new SparseIntArray();
        this.mStopUserOnSwitch = -1;
        this.mLastUserUnlockingUptime = 0L;
        this.mPendingUserStarts = new ArrayList();
        this.mUserLifecycleListener = new UserManagerInternal.UserLifecycleListener() { // from class: com.android.server.am.UserController.1
            @Override // com.android.server.p011pm.UserManagerInternal.UserLifecycleListener
            public void onUserCreated(UserInfo userInfo, Object obj) {
                UserController.this.onUserAdded(userInfo);
            }

            @Override // com.android.server.p011pm.UserManagerInternal.UserLifecycleListener
            public void onUserRemoved(UserInfo userInfo) {
                UserController.this.onUserRemoved(userInfo.id);
            }
        };
        this.mInjector = injector;
        this.mHandler = injector.getHandler(this);
        this.mUiHandler = injector.getUiHandler(this);
        UserState userState = new UserState(UserHandle.SYSTEM);
        userState.mUnlockProgress.addListener(new UserProgressListener());
        sparseArray.put(0, userState);
        arrayList.add(0);
        this.mLockPatternUtils = injector.getLockPatternUtils();
        updateStartedUserArrayLU();
    }

    public void setInitialConfig(boolean z, int i, boolean z2) {
        synchronized (this.mLock) {
            this.mUserSwitchUiEnabled = z;
            this.mMaxRunningUsers = i;
            this.mDelayUserDataLocking = z2;
            this.mInitialized = true;
        }
    }

    public final boolean isUserSwitchUiEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mUserSwitchUiEnabled;
        }
        return z;
    }

    public int getMaxRunningUsers() {
        int i;
        synchronized (this.mLock) {
            i = this.mMaxRunningUsers;
        }
        return i;
    }

    public void setStopUserOnSwitch(@ActivityManager.StopUserOnSwitch int i) {
        if (this.mInjector.checkCallingPermission("android.permission.MANAGE_USERS") == -1 && this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") == -1) {
            throw new SecurityException("You either need MANAGE_USERS or INTERACT_ACROSS_USERS permission to call setStopUserOnSwitch()");
        }
        synchronized (this.mLock) {
            Slogf.m20i("ActivityManager", "setStopUserOnSwitch(): %d -> %d", Integer.valueOf(this.mStopUserOnSwitch), Integer.valueOf(i));
            this.mStopUserOnSwitch = i;
        }
    }

    public final boolean shouldStopUserOnSwitch() {
        synchronized (this.mLock) {
            int i = this.mStopUserOnSwitch;
            if (i != -1) {
                boolean z = i == 1;
                Slogf.m20i("ActivityManager", "shouldStopUserOnSwitch(): returning overridden value (%b)", Boolean.valueOf(z));
                return z;
            }
            int i2 = SystemProperties.getInt("fw.stop_bg_users_on_switch", -1);
            if (i2 == -1) {
                return this.mDelayUserDataLocking;
            }
            return i2 == 1;
        }
    }

    public void finishUserSwitch(final UserState userState) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$finishUserSwitch$0(userState);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserSwitch$0(UserState userState) {
        finishUserBoot(userState);
        startProfiles();
        synchronized (this.mLock) {
            stopRunningUsersLU(this.mMaxRunningUsers);
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public List<Integer> getRunningUsersLU() {
        int i;
        ArrayList arrayList = new ArrayList();
        Iterator<Integer> it = this.mUserLru.iterator();
        while (it.hasNext()) {
            Integer next = it.next();
            UserState userState = this.mStartedUsers.get(next.intValue());
            if (userState != null && (i = userState.state) != 4 && i != 5) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    @GuardedBy({"mLock"})
    public final void stopRunningUsersLU(int i) {
        List<Integer> runningUsersLU = getRunningUsersLU();
        Iterator<Integer> it = runningUsersLU.iterator();
        while (runningUsersLU.size() > i && it.hasNext()) {
            Integer next = it.next();
            if (next.intValue() != 0 && next.intValue() != this.mCurrentUserId && stopUsersLU(next.intValue(), false, true, null, null) == 0) {
                it.remove();
            }
        }
    }

    public boolean canStartMoreUsers() {
        boolean z;
        synchronized (this.mLock) {
            z = getRunningUsersLU().size() < this.mMaxRunningUsers;
        }
        return z;
    }

    public final void finishUserBoot(UserState userState) {
        finishUserBoot(userState, null);
    }

    public final void finishUserBoot(UserState userState, IIntentReceiver iIntentReceiver) {
        int identifier = userState.mHandle.getIdentifier();
        EventLog.writeEvent(30078, identifier);
        synchronized (this.mLock) {
            if (this.mStartedUsers.get(identifier) != userState) {
                return;
            }
            if (userState.setState(0, 1)) {
                logUserLifecycleEvent(identifier, 4, 0);
                this.mInjector.getUserManagerInternal().setUserState(identifier, userState.state);
                if (identifier == 0 && !this.mInjector.isRuntimeRestarted() && !this.mInjector.isFirstBootOrUpgrade()) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 12, elapsedRealtime);
                    if (elapsedRealtime > 120000) {
                        Slogf.wtf("SystemServerTiming", "finishUserBoot took too long. elapsedTimeMs=" + elapsedRealtime);
                    }
                }
                if (!this.mInjector.getUserManager().isPreCreated(identifier)) {
                    Handler handler = this.mHandler;
                    handler.sendMessage(handler.obtainMessage(110, identifier, 0));
                    if (!this.mInjector.isHeadlessSystemUserMode() || !userState.mHandle.isSystem()) {
                        sendLockedBootCompletedBroadcast(iIntentReceiver, identifier);
                    }
                }
            }
            if (this.mInjector.getUserManager().isProfile(identifier)) {
                UserInfo profileParent = this.mInjector.getUserManager().getProfileParent(identifier);
                if (profileParent != null && isUserRunning(profileParent.id, 4)) {
                    Slogf.m30d("ActivityManager", "User " + identifier + " (parent " + profileParent.id + "): attempting unlock because parent is unlocked");
                    maybeUnlockUser(identifier);
                    return;
                }
                String valueOf = profileParent == null ? "<null>" : String.valueOf(profileParent.id);
                Slogf.m30d("ActivityManager", "User " + identifier + " (parent " + valueOf + "): delaying unlock because parent is locked");
                return;
            }
            maybeUnlockUser(identifier);
        }
    }

    public final void sendLockedBootCompletedBroadcast(IIntentReceiver iIntentReceiver, int i) {
        Intent intent = new Intent("android.intent.action.LOCKED_BOOT_COMPLETED", (Uri) null);
        intent.putExtra("android.intent.extra.user_handle", i);
        intent.addFlags(-1996488704);
        this.mInjector.broadcastIntent(intent, null, iIntentReceiver, 0, null, null, new String[]{"android.permission.RECEIVE_BOOT_COMPLETED"}, -1, getTemporaryAppAllowlistBroadcastOptions(202).toBundle(), true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), i);
    }

    public final boolean finishUserUnlocking(final UserState userState) {
        final int identifier = userState.mHandle.getIdentifier();
        EventLog.writeEvent(30070, identifier);
        logUserLifecycleEvent(identifier, 5, 1);
        if (StorageManager.isUserKeyUnlocked(identifier)) {
            synchronized (this.mLock) {
                if (this.mStartedUsers.get(identifier) == userState && userState.state == 1) {
                    userState.mUnlockProgress.start();
                    userState.mUnlockProgress.setProgress(5, this.mInjector.getContext().getString(17039664));
                    FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            UserController.this.lambda$finishUserUnlocking$1(identifier, userState);
                        }
                    });
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserUnlocking$1(int i, UserState userState) {
        if (!StorageManager.isUserKeyUnlocked(i)) {
            Slogf.m14w("ActivityManager", "User key got locked unexpectedly, leaving user locked.");
            return;
        }
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("UM.onBeforeUnlockUser-" + i);
        this.mInjector.getUserManager().onBeforeUnlockUser(i);
        timingsTraceAndSlog.traceEnd();
        synchronized (this.mLock) {
            if (userState.setState(1, 2)) {
                this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
                userState.mUnlockProgress.setProgress(20);
                this.mLastUserUnlockingUptime = SystemClock.uptimeMillis();
                this.mHandler.obtainMessage(100, i, 0, userState).sendToTarget();
            }
        }
    }

    public final void finishUserUnlocked(final UserState userState) {
        UserInfo profileParent;
        int identifier = userState.mHandle.getIdentifier();
        EventLog.writeEvent(30071, identifier);
        if (StorageManager.isUserKeyUnlocked(identifier)) {
            synchronized (this.mLock) {
                if (this.mStartedUsers.get(userState.mHandle.getIdentifier()) != userState) {
                    return;
                }
                if (userState.setState(2, 3)) {
                    this.mInjector.getUserManagerInternal().setUserState(identifier, userState.state);
                    userState.mUnlockProgress.finish();
                    if (identifier == 0) {
                        this.mInjector.startPersistentApps(262144);
                    }
                    this.mInjector.installEncryptionUnawareProviders(identifier);
                    if (!this.mInjector.getUserManager().isPreCreated(identifier)) {
                        Intent intent = new Intent("android.intent.action.USER_UNLOCKED");
                        intent.putExtra("android.intent.extra.user_handle", identifier);
                        intent.addFlags(1342177280);
                        this.mInjector.broadcastIntent(intent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), identifier);
                    }
                    UserInfo userInfo = getUserInfo(identifier);
                    if (userInfo.isProfile() && (profileParent = this.mInjector.getUserManager().getProfileParent(identifier)) != null) {
                        broadcastProfileAccessibleStateChanged(identifier, profileParent.id, "android.intent.action.PROFILE_ACCESSIBLE");
                        if (userInfo.isManagedProfile()) {
                            Intent intent2 = new Intent("android.intent.action.MANAGED_PROFILE_UNLOCKED");
                            intent2.putExtra("android.intent.extra.USER", UserHandle.of(identifier));
                            intent2.addFlags(1342177280);
                            this.mInjector.broadcastIntent(intent2, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), profileParent.id);
                        }
                    }
                    UserInfo userInfo2 = getUserInfo(identifier);
                    if (!Objects.equals(userInfo2.lastLoggedInFingerprint, PackagePartitions.FINGERPRINT) || SystemProperties.getBoolean("persist.pm.mock-upgrade", false)) {
                        this.mInjector.sendPreBootBroadcast(identifier, userInfo2.isManagedProfile(), new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda11
                            @Override // java.lang.Runnable
                            public final void run() {
                                UserController.this.lambda$finishUserUnlocked$2(userState);
                            }
                        });
                    } else {
                        lambda$finishUserUnlocked$2(userState);
                    }
                }
            }
        }
    }

    /* renamed from: finishUserUnlockedCompleted */
    public final void lambda$finishUserUnlocked$2(UserState userState) {
        final int identifier = userState.mHandle.getIdentifier();
        EventLog.writeEvent(30072, identifier);
        synchronized (this.mLock) {
            if (this.mStartedUsers.get(userState.mHandle.getIdentifier()) != userState) {
                return;
            }
            final UserInfo userInfo = getUserInfo(identifier);
            if (userInfo != null && StorageManager.isUserKeyUnlocked(identifier)) {
                this.mInjector.getUserManager().onUserLoggedIn(identifier);
                final Runnable runnable = new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserController.this.lambda$finishUserUnlockedCompleted$3(userInfo);
                    }
                };
                if (!userInfo.isInitialized()) {
                    Slogf.m30d("ActivityManager", "Initializing user #" + identifier);
                    if (userInfo.preCreated) {
                        runnable.run();
                    } else if (identifier != 0) {
                        Intent intent = new Intent("android.intent.action.USER_INITIALIZE");
                        intent.addFlags(285212672);
                        this.mInjector.broadcastIntent(intent, null, new IIntentReceiver.Stub() { // from class: com.android.server.am.UserController.2
                            public void performReceive(Intent intent2, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
                                runnable.run();
                            }
                        }, 0, null, null, null, -1, null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), identifier);
                    }
                }
                if (userInfo.preCreated) {
                    Slogf.m22i("ActivityManager", "Stopping pre-created user " + userInfo.toFullString());
                    stopUser(userInfo.id, true, false, null, null);
                    return;
                }
                this.mInjector.startUserWidgets(identifier);
                this.mHandler.obtainMessage(105, identifier, 0).sendToTarget();
                Slogf.m22i("ActivityManager", "Posting BOOT_COMPLETED user #" + identifier);
                if (identifier == 0 && !this.mInjector.isRuntimeRestarted() && !this.mInjector.isFirstBootOrUpgrade()) {
                    FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED, 13, SystemClock.elapsedRealtime());
                }
                final Intent intent2 = new Intent("android.intent.action.BOOT_COMPLETED", (Uri) null);
                intent2.putExtra("android.intent.extra.user_handle", identifier);
                intent2.addFlags(-1996488704);
                final int callingUid = Binder.getCallingUid();
                final int callingPid = Binder.getCallingPid();
                FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda13
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserController.this.lambda$finishUserUnlockedCompleted$4(intent2, identifier, callingUid, callingPid);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserUnlockedCompleted$3(UserInfo userInfo) {
        this.mInjector.getUserManager().makeInitialized(userInfo.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserUnlockedCompleted$4(Intent intent, final int i, int i2, int i3) {
        this.mInjector.broadcastIntent(intent, null, new IIntentReceiver.Stub() { // from class: com.android.server.am.UserController.3
            public void performReceive(Intent intent2, int i4, String str, Bundle bundle, boolean z, boolean z2, int i5) throws RemoteException {
                Slogf.m22i("ActivityManager", "Finished processing BOOT_COMPLETED for u" + i);
                UserController.this.mBootCompleted = true;
            }
        }, 0, null, null, new String[]{"android.permission.RECEIVE_BOOT_COMPLETED"}, -1, getTemporaryAppAllowlistBroadcastOptions(200).toBundle(), true, false, ActivityManagerService.MY_PID, 1000, i2, i3, i);
    }

    /* renamed from: com.android.server.am.UserController$4 */
    /* loaded from: classes.dex */
    public class C04204 implements UserState.KeyEvictedCallback {
        public final /* synthetic */ int val$userStartMode;

        public C04204(int i) {
            this.val$userStartMode = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$keyEvicted$0(int i, int i2) {
            UserController.this.startUser(i, i2);
        }

        @Override // com.android.server.p006am.UserState.KeyEvictedCallback
        public void keyEvicted(final int i) {
            Handler handler = UserController.this.mHandler;
            final int i2 = this.val$userStartMode;
            handler.post(new Runnable() { // from class: com.android.server.am.UserController$4$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserController.C04204.this.lambda$keyEvicted$0(i, i2);
                }
            });
        }
    }

    public int restartUser(int i, int i2) {
        return stopUser(i, true, false, null, new C04204(i2));
    }

    public boolean stopProfile(int i) {
        boolean z;
        if (this.mInjector.checkCallingPermission("android.permission.MANAGE_USERS") == -1 && this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == -1) {
            throw new SecurityException("You either need MANAGE_USERS or INTERACT_ACROSS_USERS_FULL permission to stop a profile");
        }
        UserInfo userInfo = getUserInfo(i);
        if (userInfo == null || !userInfo.isProfile()) {
            throw new IllegalArgumentException("User " + i + " is not a profile");
        }
        enforceShellRestriction("no_debugging_features", i);
        synchronized (this.mLock) {
            z = stopUsersLU(i, true, false, null, null) == 0;
        }
        return z;
    }

    public int stopUser(int i, boolean z, boolean z2, IStopUserCallback iStopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        int stopUsersLU;
        checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "stopUser");
        Preconditions.checkArgument(i >= 0, "Invalid user id %d", new Object[]{Integer.valueOf(i)});
        enforceShellRestriction("no_debugging_features", i);
        synchronized (this.mLock) {
            stopUsersLU = stopUsersLU(i, z, z2, iStopUserCallback, keyEvictedCallback);
        }
        return stopUsersLU;
    }

    @GuardedBy({"mLock"})
    public final int stopUsersLU(int i, boolean z, boolean z2, IStopUserCallback iStopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        if (i == 0) {
            return -3;
        }
        if (isCurrentUserLU(i)) {
            return -2;
        }
        int[] usersToStopLU = getUsersToStopLU(i);
        for (int i2 : usersToStopLU) {
            if (i2 == 0 || isCurrentUserLU(i2)) {
                if (z) {
                    Slogf.m22i("ActivityManager", "Force stop user " + i + ". Related users will not be stopped");
                    stopSingleUserLU(i, z2, iStopUserCallback, keyEvictedCallback);
                    return 0;
                }
                return -4;
            }
        }
        int length = usersToStopLU.length;
        for (int i3 = 0; i3 < length; i3++) {
            int i4 = usersToStopLU[i3];
            UserState.KeyEvictedCallback keyEvictedCallback2 = null;
            IStopUserCallback iStopUserCallback2 = i4 == i ? iStopUserCallback : null;
            if (i4 == i) {
                keyEvictedCallback2 = keyEvictedCallback;
            }
            stopSingleUserLU(i4, z2, iStopUserCallback2, keyEvictedCallback2);
        }
        return 0;
    }

    @GuardedBy({"mLock"})
    public final void stopSingleUserLU(final int i, final boolean z, final IStopUserCallback iStopUserCallback, UserState.KeyEvictedCallback keyEvictedCallback) {
        Slogf.m22i("ActivityManager", "stopSingleUserLU userId=" + i);
        final UserState userState = this.mStartedUsers.get(i);
        ArrayList arrayList = null;
        if (userState == null) {
            if (this.mDelayUserDataLocking) {
                if (z && keyEvictedCallback != null) {
                    Slogf.wtf("ActivityManager", "allowDelayedLocking set with KeyEvictedCallback, ignore it and lock user:" + i, new RuntimeException());
                    z = false;
                }
                if (!z && this.mLastActiveUsers.remove(Integer.valueOf(i))) {
                    if (keyEvictedCallback != null) {
                        arrayList = new ArrayList(1);
                        arrayList.add(keyEvictedCallback);
                    }
                    dispatchUserLocking(i, arrayList);
                }
            }
            if (iStopUserCallback != null) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserController.lambda$stopSingleUserLU$5(iStopUserCallback, i);
                    }
                });
                return;
            }
            return;
        }
        logUserJourneyInfo(null, getUserInfo(i), 5);
        logUserLifecycleEvent(i, 7, 1);
        if (iStopUserCallback != null) {
            userState.mStopCallbacks.add(iStopUserCallback);
        }
        if (keyEvictedCallback != null) {
            userState.mKeyEvictedCallbacks.add(keyEvictedCallback);
        }
        int i2 = userState.state;
        if (i2 == 4 || i2 == 5) {
            return;
        }
        userState.setState(4);
        UserManagerInternal userManagerInternal = this.mInjector.getUserManagerInternal();
        userManagerInternal.setUserState(i, userState.state);
        userManagerInternal.unassignUserFromDisplayOnStop(i);
        updateStartedUserArrayLU();
        final Runnable runnable = new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$stopSingleUserLU$7(i, userState, z);
            }
        };
        if (this.mInjector.getUserManager().isPreCreated(i)) {
            runnable.run();
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    UserController.this.lambda$stopSingleUserLU$8(i, runnable);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$stopSingleUserLU$5(IStopUserCallback iStopUserCallback, int i) {
        try {
            iStopUserCallback.userStopped(i);
        } catch (RemoteException unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopSingleUserLU$7(final int i, final UserState userState, final boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$stopSingleUserLU$6(i, userState, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopSingleUserLU$8(int i, final Runnable runnable) {
        Intent intent = new Intent("android.intent.action.USER_STOPPING");
        intent.addFlags(1073741824);
        intent.putExtra("android.intent.extra.user_handle", i);
        intent.putExtra("android.intent.extra.SHUTDOWN_USERSPACE_ONLY", true);
        IIntentReceiver iIntentReceiver = new IIntentReceiver.Stub() { // from class: com.android.server.am.UserController.5
            public void performReceive(Intent intent2, int i2, String str, Bundle bundle, boolean z, boolean z2, int i3) {
                runnable.run();
            }
        };
        this.mInjector.clearBroadcastQueueForUser(i);
        this.mInjector.broadcastIntent(intent, null, iIntentReceiver, 0, null, null, new String[]{"android.permission.INTERACT_ACROSS_USERS"}, -1, null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
    }

    /* renamed from: finishUserStopping */
    public final void lambda$stopSingleUserLU$6(int i, final UserState userState, final boolean z) {
        EventLog.writeEvent(30073, i);
        synchronized (this.mLock) {
            if (userState.state != 4) {
                logUserLifecycleEvent(i, 7, 0);
                clearSessionId(i);
                return;
            }
            userState.setState(5);
            this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
            this.mInjector.batteryStatsServiceNoteEvent(16391, Integer.toString(i), i);
            this.mInjector.getSystemServiceManager().onUserStopping(i);
            final Runnable runnable = new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    UserController.this.lambda$finishUserStopping$10(userState, z);
                }
            };
            if (this.mInjector.getUserManager().isPreCreated(i)) {
                runnable.run();
                return;
            }
            this.mInjector.broadcastIntent(new Intent("android.intent.action.ACTION_SHUTDOWN"), null, new IIntentReceiver.Stub() { // from class: com.android.server.am.UserController.6
                public void performReceive(Intent intent, int i2, String str, Bundle bundle, boolean z2, boolean z3, int i3) {
                    runnable.run();
                }
            }, 0, null, null, null, -1, null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserStopping$10(final UserState userState, final boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$finishUserStopping$9(userState, z);
            }
        });
    }

    @VisibleForTesting
    /* renamed from: finishUserStopped */
    public void lambda$finishUserStopping$9(UserState userState, boolean z) {
        ArrayList arrayList;
        ArrayList arrayList2;
        boolean z2;
        int i;
        boolean z3;
        int identifier = userState.mHandle.getIdentifier();
        EventLog.writeEvent(30074, identifier);
        UserInfo userInfo = getUserInfo(identifier);
        synchronized (this.mLock) {
            arrayList = new ArrayList(userState.mStopCallbacks);
            arrayList2 = new ArrayList(userState.mKeyEvictedCallbacks);
            z2 = true;
            if (this.mStartedUsers.get(identifier) == userState && userState.state == 5) {
                Slogf.m22i("ActivityManager", "Removing user state from UserController.mStartedUsers for user #" + identifier + " as a result of user being stopped");
                this.mStartedUsers.remove(identifier);
                this.mUserLru.remove(Integer.valueOf(identifier));
                updateStartedUserArrayLU();
                if (z && !arrayList2.isEmpty()) {
                    Slogf.wtf("ActivityManager", "Delayed locking enabled while KeyEvictedCallbacks not empty, userId:" + identifier + " callbacks:" + arrayList2);
                    z = false;
                }
                i = updateUserToLockLU(identifier, z);
                z3 = i != -10000;
            }
            i = identifier;
            z3 = true;
            z2 = false;
        }
        if (z2) {
            Slogf.m22i("ActivityManager", "Removing user state from UserManager.mUserStates for user #" + identifier + " as a result of user being stopped");
            this.mInjector.getUserManagerInternal().removeUserState(identifier);
            this.mInjector.activityManagerOnUserStopped(identifier);
            forceStopUser(identifier, "finish user");
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            IStopUserCallback iStopUserCallback = (IStopUserCallback) it.next();
            if (z2) {
                try {
                    iStopUserCallback.userStopped(identifier);
                } catch (RemoteException unused) {
                }
            } else {
                iStopUserCallback.userStopAborted(identifier);
            }
        }
        if (z2) {
            this.mInjector.systemServiceManagerOnUserStopped(identifier);
            this.mInjector.taskSupervisorRemoveUser(identifier);
            if (userInfo.isEphemeral() && !userInfo.preCreated) {
                this.mInjector.getUserManager().removeUserEvenWhenDisallowed(identifier);
            }
            logUserLifecycleEvent(identifier, 7, 2);
            clearSessionId(identifier);
            if (z3) {
                dispatchUserLocking(i, arrayList2);
            }
            resumePendingUserStarts(identifier);
            return;
        }
        logUserLifecycleEvent(identifier, 7, 0);
        clearSessionId(identifier);
    }

    public final void resumePendingUserStarts(int i) {
        synchronized (this.mLock) {
            ArrayList arrayList = new ArrayList();
            for (final PendingUserStart pendingUserStart : this.mPendingUserStarts) {
                if (pendingUserStart.userId == i) {
                    Slogf.m22i("ActivityManager", "resumePendingUserStart for" + pendingUserStart);
                    this.mHandler.post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            UserController.this.lambda$resumePendingUserStarts$11(pendingUserStart);
                        }
                    });
                    arrayList.add(pendingUserStart);
                }
            }
            this.mPendingUserStarts.removeAll(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resumePendingUserStarts$11(PendingUserStart pendingUserStart) {
        startUser(pendingUserStart.userId, pendingUserStart.userStartMode, pendingUserStart.unlockListener);
    }

    public final void dispatchUserLocking(final int i, final List<UserState.KeyEvictedCallback> list) {
        FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$dispatchUserLocking$12(i, list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchUserLocking$12(int i, List list) {
        synchronized (this.mLock) {
            if (this.mStartedUsers.get(i) != null) {
                Slogf.m14w("ActivityManager", "User was restarted, skipping key eviction");
                return;
            }
            try {
                Slogf.m22i("ActivityManager", "Locking CE storage for user #" + i);
                this.mInjector.getStorageManager().lockUserKey(i);
                if (list == null) {
                    return;
                }
                for (int i2 = 0; i2 < list.size(); i2++) {
                    ((UserState.KeyEvictedCallback) list.get(i2)).keyEvicted(i);
                }
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final int updateUserToLockLU(int i, boolean z) {
        if (!this.mDelayUserDataLocking || !z || getUserInfo(i).isEphemeral() || hasUserRestriction("no_run_in_background", i)) {
            return i;
        }
        this.mLastActiveUsers.remove(Integer.valueOf(i));
        this.mLastActiveUsers.add(0, Integer.valueOf(i));
        if (this.mStartedUsers.size() + this.mLastActiveUsers.size() > this.mMaxRunningUsers) {
            ArrayList<Integer> arrayList = this.mLastActiveUsers;
            int intValue = arrayList.get(arrayList.size() - 1).intValue();
            ArrayList<Integer> arrayList2 = this.mLastActiveUsers;
            arrayList2.remove(arrayList2.size() - 1);
            Slogf.m22i("ActivityManager", "finishUserStopped, stopping user:" + i + " lock user:" + intValue);
            return intValue;
        }
        Slogf.m22i("ActivityManager", "finishUserStopped, user:" + i + ", skip locking");
        return -10000;
    }

    @GuardedBy({"mLock"})
    public final int[] getUsersToStopLU(int i) {
        int size = this.mStartedUsers.size();
        IntArray intArray = new IntArray();
        intArray.add(i);
        int i2 = this.mUserProfileGroupIds.get(i, -10000);
        for (int i3 = 0; i3 < size; i3++) {
            int identifier = this.mStartedUsers.valueAt(i3).mHandle.getIdentifier();
            boolean z = i2 != -10000 && i2 == this.mUserProfileGroupIds.get(identifier, -10000);
            boolean z2 = identifier == i;
            if (z && !z2) {
                intArray.add(identifier);
            }
        }
        return intArray.toArray();
    }

    public final void forceStopUser(int i, String str) {
        UserInfo profileParent;
        this.mInjector.activityManagerForceStopPackage(i, str);
        if (this.mInjector.getUserManager().isPreCreated(i)) {
            return;
        }
        Intent intent = new Intent("android.intent.action.USER_STOPPED");
        intent.addFlags(1342177280);
        intent.putExtra("android.intent.extra.user_handle", i);
        this.mInjector.broadcastIntent(intent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), -1);
        if (!getUserInfo(i).isProfile() || (profileParent = this.mInjector.getUserManager().getProfileParent(i)) == null) {
            return;
        }
        broadcastProfileAccessibleStateChanged(i, profileParent.id, "android.intent.action.PROFILE_INACCESSIBLE");
    }

    public final void stopGuestOrEphemeralUserIfBackground(int i) {
        int i2;
        synchronized (this.mLock) {
            UserState userState = this.mStartedUsers.get(i);
            if (i != 0 && i != this.mCurrentUserId && userState != null && (i2 = userState.state) != 4 && i2 != 5) {
                UserInfo userInfo = getUserInfo(i);
                if (userInfo.isEphemeral()) {
                    ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).onEphemeralUserStop(i);
                }
                if (userInfo.isGuest() || userInfo.isEphemeral()) {
                    synchronized (this.mLock) {
                        stopUsersLU(i, true, false, null, null);
                    }
                }
            }
        }
    }

    public void scheduleStartProfiles() {
        FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$scheduleStartProfiles$13();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleStartProfiles$13() {
        if (this.mHandler.hasMessages(40)) {
            return;
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(40), 1000L);
    }

    public final void startProfiles() {
        int currentUserId = getCurrentUserId();
        int i = 0;
        List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(currentUserId, false);
        ArrayList arrayList = new ArrayList(profiles.size());
        for (UserInfo userInfo : profiles) {
            if ((userInfo.flags & 16) == 16 && userInfo.id != currentUserId && shouldStartWithParent(userInfo)) {
                arrayList.add(userInfo);
            }
        }
        int size = arrayList.size();
        while (i < size && i < getMaxRunningUsers() - 1) {
            startUser(((UserInfo) arrayList.get(i)).id, 3);
            i++;
        }
        if (i < size) {
            Slogf.m14w("ActivityManager", "More profiles than MAX_RUNNING_USERS");
        }
    }

    public final boolean shouldStartWithParent(UserInfo userInfo) {
        UserProperties userProperties = getUserProperties(userInfo.id);
        return userProperties != null && userProperties.getStartWithParent() && (!userInfo.isQuietModeEnabled() || ((DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class)).isKeepProfilesRunningEnabled());
    }

    @RequiresPermission(anyOf = {"android.permission.MANAGE_USERS", "android.permission.INTERACT_ACROSS_USERS_FULL"})
    public boolean startProfile(int i, boolean z, IProgressListener iProgressListener) {
        if (this.mInjector.checkCallingPermission("android.permission.MANAGE_USERS") == -1 && this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == -1) {
            throw new SecurityException("You either need MANAGE_USERS or INTERACT_ACROSS_USERS_FULL permission to start a profile");
        }
        UserInfo userInfo = getUserInfo(i);
        if (userInfo == null || !userInfo.isProfile()) {
            throw new IllegalArgumentException("User " + i + " is not a profile");
        } else if (!userInfo.isEnabled() && !z) {
            Slogf.m12w("ActivityManager", "Cannot start disabled profile #%d", Integer.valueOf(i));
            return false;
        } else {
            return startUserNoChecks(i, 0, 3, iProgressListener);
        }
    }

    @VisibleForTesting
    public boolean startUser(int i, int i2) {
        return startUser(i, i2, null);
    }

    public boolean startUser(int i, int i2, IProgressListener iProgressListener) {
        checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "startUser");
        return startUserNoChecks(i, 0, i2, iProgressListener);
    }

    public boolean startUserVisibleOnDisplay(int i, int i2, IProgressListener iProgressListener) {
        checkCallingHasOneOfThosePermissions("startUserOnDisplay", "android.permission.MANAGE_USERS", "android.permission.INTERACT_ACROSS_USERS");
        try {
            return startUserNoChecks(i, i2, 3, iProgressListener);
        } catch (RuntimeException e) {
            Slogf.m24e("ActivityManager", "startUserOnSecondaryDisplay(%d, %d) failed: %s", Integer.valueOf(i), Integer.valueOf(i2), e);
            return false;
        }
    }

    public final boolean startUserNoChecks(int i, int i2, int i3, IProgressListener iProgressListener) {
        String str;
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        StringBuilder sb = new StringBuilder();
        sb.append("UserController.startUser-");
        sb.append(i);
        if (i2 == 0) {
            str = "";
        } else {
            str = "-display-" + i2;
        }
        sb.append(str);
        sb.append(PackageManagerShellCommandDataLoader.STDIN_PATH);
        sb.append(i3 == 1 ? "fg" : "bg");
        sb.append("-start-mode-");
        sb.append(i3);
        timingsTraceAndSlog.traceBegin(sb.toString());
        try {
            return startUserInternal(i, i2, i3, iProgressListener, timingsTraceAndSlog);
        } finally {
            timingsTraceAndSlog.traceEnd();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:120:0x02d5 A[Catch: all -> 0x037e, TryCatch #5 {all -> 0x037e, blocks: (B:12:0x0054, B:14:0x005f, B:16:0x0065, B:18:0x006f, B:21:0x0074, B:23:0x0078, B:24:0x007b, B:27:0x0083, B:29:0x0088, B:30:0x0098, B:32:0x00a6, B:36:0x00c3, B:38:0x00c9, B:48:0x0111, B:50:0x0129, B:54:0x0155, B:56:0x015b, B:57:0x0173, B:58:0x017e, B:72:0x01f0, B:73:0x01f5, B:75:0x01fa, B:76:0x020e, B:78:0x0216, B:79:0x021f, B:82:0x0229, B:84:0x0241, B:86:0x0257, B:95:0x027c, B:97:0x0284, B:98:0x029c, B:101:0x02a1, B:118:0x02d1, B:120:0x02d5, B:122:0x02f5, B:124:0x02fd, B:125:0x032d, B:129:0x0334, B:136:0x0346, B:138:0x034b, B:142:0x0367, B:139:0x0358, B:135:0x0343, B:108:0x02ab, B:109:0x02c2, B:112:0x02c7, B:91:0x0265, B:92:0x0270, B:43:0x00ed, B:45:0x00f1, B:80:0x0220, B:81:0x0228, B:99:0x029d, B:100:0x02a0, B:93:0x0271, B:94:0x027b, B:59:0x017f, B:61:0x018a, B:69:0x01dd, B:70:0x01ed, B:62:0x01a8, B:64:0x01ad, B:65:0x01d5, B:110:0x02c3, B:111:0x02c6), top: B:154:0x0054 }] */
    /* JADX WARN: Removed duplicated region for block: B:121:0x02f4  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x02fd A[Catch: all -> 0x037e, TryCatch #5 {all -> 0x037e, blocks: (B:12:0x0054, B:14:0x005f, B:16:0x0065, B:18:0x006f, B:21:0x0074, B:23:0x0078, B:24:0x007b, B:27:0x0083, B:29:0x0088, B:30:0x0098, B:32:0x00a6, B:36:0x00c3, B:38:0x00c9, B:48:0x0111, B:50:0x0129, B:54:0x0155, B:56:0x015b, B:57:0x0173, B:58:0x017e, B:72:0x01f0, B:73:0x01f5, B:75:0x01fa, B:76:0x020e, B:78:0x0216, B:79:0x021f, B:82:0x0229, B:84:0x0241, B:86:0x0257, B:95:0x027c, B:97:0x0284, B:98:0x029c, B:101:0x02a1, B:118:0x02d1, B:120:0x02d5, B:122:0x02f5, B:124:0x02fd, B:125:0x032d, B:129:0x0334, B:136:0x0346, B:138:0x034b, B:142:0x0367, B:139:0x0358, B:135:0x0343, B:108:0x02ab, B:109:0x02c2, B:112:0x02c7, B:91:0x0265, B:92:0x0270, B:43:0x00ed, B:45:0x00f1, B:80:0x0220, B:81:0x0228, B:99:0x029d, B:100:0x02a0, B:93:0x0271, B:94:0x027b, B:59:0x017f, B:61:0x018a, B:69:0x01dd, B:70:0x01ed, B:62:0x01a8, B:64:0x01ad, B:65:0x01d5, B:110:0x02c3, B:111:0x02c6), top: B:154:0x0054 }] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0331  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x034b A[Catch: all -> 0x037e, TryCatch #5 {all -> 0x037e, blocks: (B:12:0x0054, B:14:0x005f, B:16:0x0065, B:18:0x006f, B:21:0x0074, B:23:0x0078, B:24:0x007b, B:27:0x0083, B:29:0x0088, B:30:0x0098, B:32:0x00a6, B:36:0x00c3, B:38:0x00c9, B:48:0x0111, B:50:0x0129, B:54:0x0155, B:56:0x015b, B:57:0x0173, B:58:0x017e, B:72:0x01f0, B:73:0x01f5, B:75:0x01fa, B:76:0x020e, B:78:0x0216, B:79:0x021f, B:82:0x0229, B:84:0x0241, B:86:0x0257, B:95:0x027c, B:97:0x0284, B:98:0x029c, B:101:0x02a1, B:118:0x02d1, B:120:0x02d5, B:122:0x02f5, B:124:0x02fd, B:125:0x032d, B:129:0x0334, B:136:0x0346, B:138:0x034b, B:142:0x0367, B:139:0x0358, B:135:0x0343, B:108:0x02ab, B:109:0x02c2, B:112:0x02c7, B:91:0x0265, B:92:0x0270, B:43:0x00ed, B:45:0x00f1, B:80:0x0220, B:81:0x0228, B:99:0x029d, B:100:0x02a0, B:93:0x0271, B:94:0x027b, B:59:0x017f, B:61:0x018a, B:69:0x01dd, B:70:0x01ed, B:62:0x01a8, B:64:0x01ad, B:65:0x01d5, B:110:0x02c3, B:111:0x02c6), top: B:154:0x0054 }] */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0358 A[Catch: all -> 0x037e, TryCatch #5 {all -> 0x037e, blocks: (B:12:0x0054, B:14:0x005f, B:16:0x0065, B:18:0x006f, B:21:0x0074, B:23:0x0078, B:24:0x007b, B:27:0x0083, B:29:0x0088, B:30:0x0098, B:32:0x00a6, B:36:0x00c3, B:38:0x00c9, B:48:0x0111, B:50:0x0129, B:54:0x0155, B:56:0x015b, B:57:0x0173, B:58:0x017e, B:72:0x01f0, B:73:0x01f5, B:75:0x01fa, B:76:0x020e, B:78:0x0216, B:79:0x021f, B:82:0x0229, B:84:0x0241, B:86:0x0257, B:95:0x027c, B:97:0x0284, B:98:0x029c, B:101:0x02a1, B:118:0x02d1, B:120:0x02d5, B:122:0x02f5, B:124:0x02fd, B:125:0x032d, B:129:0x0334, B:136:0x0346, B:138:0x034b, B:142:0x0367, B:139:0x0358, B:135:0x0343, B:108:0x02ab, B:109:0x02c2, B:112:0x02c7, B:91:0x0265, B:92:0x0270, B:43:0x00ed, B:45:0x00f1, B:80:0x0220, B:81:0x0228, B:99:0x029d, B:100:0x02a0, B:93:0x0271, B:94:0x027b, B:59:0x017f, B:61:0x018a, B:69:0x01dd, B:70:0x01ed, B:62:0x01a8, B:64:0x01ad, B:65:0x01d5, B:110:0x02c3, B:111:0x02c6), top: B:154:0x0054 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean startUserInternal(int i, int i2, int i3, IProgressListener iProgressListener, TimingsTraceAndSlog timingsTraceAndSlog) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        int i4 = i3 == 1 ? 1 : 0;
        boolean z5 = i2 != 0;
        if (z5) {
            Preconditions.checkArgument(i4 ^ 1, "Cannot start user %d in foreground AND on secondary display (%d)", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        }
        EventLog.writeEvent(30076, Integer.valueOf(i), Integer.valueOf(i4), Integer.valueOf(i2));
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            timingsTraceAndSlog.traceBegin("getStartedUserState");
            int currentUserId = getCurrentUserId();
            if (currentUserId == i) {
                UserState startedUserState = getStartedUserState(i);
                if (startedUserState == null) {
                    Slogf.wtf("ActivityManager", "Current user has no UserState");
                } else if (i != 0 || startedUserState.state != 0) {
                    if (startedUserState.state == 3) {
                        notifyFinished(i, iProgressListener);
                    }
                    timingsTraceAndSlog.traceEnd();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
            }
            timingsTraceAndSlog.traceEnd();
            if (i4 != 0) {
                timingsTraceAndSlog.traceBegin("clearAllLockedTasks");
                this.mInjector.clearAllLockedTasks("startUser");
                timingsTraceAndSlog.traceEnd();
            }
            timingsTraceAndSlog.traceBegin("getUserInfo");
            UserInfo userInfo = getUserInfo(i);
            timingsTraceAndSlog.traceEnd();
            if (userInfo == null) {
                Slogf.m14w("ActivityManager", "No user info for user #" + i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            } else if (i4 != 0 && userInfo.isProfile()) {
                Slogf.m14w("ActivityManager", "Cannot switch to User #" + i + ": not a full user");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            } else if ((i4 != 0 || z5) && userInfo.preCreated) {
                Slogf.m14w("ActivityManager", "Cannot start pre-created user #" + i + " in foreground or on secondary display");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            } else {
                timingsTraceAndSlog.traceBegin("assignUserToDisplayOnStart");
                int assignUserToDisplayOnStart = this.mInjector.getUserManagerInternal().assignUserToDisplayOnStart(i, userInfo.profileGroupId, i3, i2);
                timingsTraceAndSlog.traceEnd();
                if (assignUserToDisplayOnStart == -1) {
                    Slogf.m24e("ActivityManager", "%s user(%d) / display (%d) assignment failed: %s", UserManagerInternal.userStartModeToString(i3), Integer.valueOf(i), Integer.valueOf(i2), UserManagerInternal.userAssignmentResultToString(assignUserToDisplayOnStart));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return false;
                }
                if (i4 != 0 && isUserSwitchUiEnabled()) {
                    timingsTraceAndSlog.traceBegin("startFreezingScreen");
                    this.mInjector.getWindowManager().startFreezingScreen(17432721, 17432720);
                    timingsTraceAndSlog.traceEnd();
                }
                dismissUserSwitchDialog();
                timingsTraceAndSlog.traceBegin("updateStartedUserArrayStarting");
                synchronized (this.mLock) {
                    UserState userState = this.mStartedUsers.get(i);
                    if (userState == null) {
                        userState = new UserState(UserHandle.of(i));
                        userState.mUnlockProgress.addListener(new UserProgressListener());
                        this.mStartedUsers.put(i, userState);
                        updateStartedUserArrayLU();
                        z = true;
                        z2 = true;
                    } else if (userState.state == 5) {
                        Slogf.m22i("ActivityManager", "User #" + i + " is shutting down - will start after full shutdown");
                        this.mPendingUserStarts.add(new PendingUserStart(i, i3, iProgressListener));
                        timingsTraceAndSlog.traceEnd();
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return true;
                    } else {
                        z = false;
                        z2 = false;
                    }
                    Integer valueOf = Integer.valueOf(i);
                    boolean z6 = z;
                    this.mUserLru.remove(valueOf);
                    this.mUserLru.add(valueOf);
                    if (iProgressListener != null) {
                        userState.mUnlockProgress.addListener(iProgressListener);
                    }
                    timingsTraceAndSlog.traceEnd();
                    if (z2) {
                        timingsTraceAndSlog.traceBegin("setUserState");
                        this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
                        timingsTraceAndSlog.traceEnd();
                    }
                    timingsTraceAndSlog.traceBegin("updateConfigurationAndProfileIds");
                    if (i4 != 0) {
                        this.mInjector.reportGlobalUsageEvent(16);
                        synchronized (this.mLock) {
                            this.mCurrentUserId = i;
                            this.mTargetUserId = -10000;
                            z4 = this.mUserSwitchUiEnabled;
                        }
                        this.mInjector.updateUserConfiguration();
                        updateProfileRelatedCaches();
                        this.mInjector.getWindowManager().setCurrentUser(i);
                        this.mInjector.reportCurWakefulnessUsageEvent();
                        if (z4) {
                            this.mInjector.getWindowManager().setSwitchingUser(true);
                            if (this.mInjector.getKeyguardManager().isDeviceSecure(i)) {
                                this.mInjector.getWindowManager().lockNow(null);
                            }
                        }
                    } else {
                        Integer valueOf2 = Integer.valueOf(this.mCurrentUserId);
                        updateProfileRelatedCaches();
                        synchronized (this.mLock) {
                            this.mUserLru.remove(valueOf2);
                            this.mUserLru.add(valueOf2);
                        }
                    }
                    timingsTraceAndSlog.traceEnd();
                    int i5 = userState.state;
                    if (i5 == 4) {
                        timingsTraceAndSlog.traceBegin("updateStateStopping");
                        userState.setState(userState.lastState);
                        this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
                        synchronized (this.mLock) {
                            updateStartedUserArrayLU();
                        }
                        timingsTraceAndSlog.traceEnd();
                    } else if (i5 != 5) {
                        z3 = z6;
                        if (userState.state != 0) {
                            timingsTraceAndSlog.traceBegin("updateStateBooting");
                            this.mInjector.getUserManager().onBeforeStartUser(i);
                            Handler handler = this.mHandler;
                            handler.sendMessage(handler.obtainMessage(50, i, 0));
                            timingsTraceAndSlog.traceEnd();
                        }
                        timingsTraceAndSlog.traceBegin("sendMessages");
                        if (i4 != 0) {
                            Handler handler2 = this.mHandler;
                            handler2.sendMessage(handler2.obtainMessage(60, i, currentUserId));
                            this.mHandler.removeMessages(10);
                            this.mHandler.removeMessages(30);
                            Handler handler3 = this.mHandler;
                            handler3.sendMessage(handler3.obtainMessage(10, currentUserId, i, userState));
                            Handler handler4 = this.mHandler;
                            handler4.sendMessageDelayed(handler4.obtainMessage(30, currentUserId, i, userState), getUserSwitchTimeoutMs());
                        }
                        if (userInfo.preCreated) {
                            z3 = false;
                        }
                        boolean z7 = i != 0 && this.mInjector.isHeadlessSystemUserMode();
                        if (!z3 || z7) {
                            sendUserStartedBroadcast(i, callingUid, callingPid);
                        }
                        timingsTraceAndSlog.traceEnd();
                        if (i4 == 0) {
                            timingsTraceAndSlog.traceBegin("moveUserToForeground");
                            moveUserToForeground(userState, i);
                            timingsTraceAndSlog.traceEnd();
                        } else {
                            timingsTraceAndSlog.traceBegin("finishUserBoot");
                            finishUserBoot(userState);
                            timingsTraceAndSlog.traceEnd();
                        }
                        if (!z3 || z7) {
                            timingsTraceAndSlog.traceBegin("sendRestartBroadcast");
                            sendUserStartingBroadcast(i, callingUid, callingPid);
                            timingsTraceAndSlog.traceEnd();
                        }
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return true;
                    } else {
                        timingsTraceAndSlog.traceBegin("updateStateShutdown");
                        userState.setState(0);
                        this.mInjector.getUserManagerInternal().setUserState(i, userState.state);
                        synchronized (this.mLock) {
                            updateStartedUserArrayLU();
                        }
                        timingsTraceAndSlog.traceEnd();
                    }
                    z3 = true;
                    if (userState.state != 0) {
                    }
                    timingsTraceAndSlog.traceBegin("sendMessages");
                    if (i4 != 0) {
                    }
                    if (userInfo.preCreated) {
                    }
                    if (i != 0) {
                    }
                    if (!z3) {
                    }
                    sendUserStartedBroadcast(i, callingUid, callingPid);
                    timingsTraceAndSlog.traceEnd();
                    if (i4 == 0) {
                    }
                    if (!z3) {
                    }
                    timingsTraceAndSlog.traceBegin("sendRestartBroadcast");
                    sendUserStartingBroadcast(i, callingUid, callingPid);
                    timingsTraceAndSlog.traceEnd();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void startUserInForeground(int i) {
        if (startUser(i, 1)) {
            return;
        }
        this.mInjector.getWindowManager().setSwitchingUser(false);
        this.mTargetUserId = -10000;
        dismissUserSwitchDialog();
    }

    public boolean unlockUser(int i, IProgressListener iProgressListener) {
        checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "unlockUser");
        EventLog.writeEvent(30077, i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return maybeUnlockUser(i, iProgressListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static void notifyFinished(int i, IProgressListener iProgressListener) {
        if (iProgressListener == null) {
            return;
        }
        try {
            iProgressListener.onFinished(i, (Bundle) null);
        } catch (RemoteException unused) {
        }
    }

    public final boolean maybeUnlockUser(int i) {
        return maybeUnlockUser(i, null);
    }

    public final boolean maybeUnlockUser(int i, IProgressListener iProgressListener) {
        UserState userState;
        int size;
        int[] iArr;
        if (!this.mAllowUserUnlocking) {
            Slogf.m20i("ActivityManager", "Not unlocking user %d yet because boot hasn't completed", Integer.valueOf(i));
            notifyFinished(i, iProgressListener);
            return false;
        }
        if (!StorageManager.isUserKeyUnlocked(i)) {
            this.mLockPatternUtils.unlockUserKeyIfUnsecured(i);
        }
        synchronized (this.mLock) {
            userState = this.mStartedUsers.get(i);
            if (userState != null) {
                userState.mUnlockProgress.addListener(iProgressListener);
            }
        }
        if (userState == null) {
            notifyFinished(i, iProgressListener);
            return false;
        }
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("finishUserUnlocking-" + i);
        boolean finishUserUnlocking = finishUserUnlocking(userState);
        timingsTraceAndSlog.traceEnd();
        if (!finishUserUnlocking) {
            notifyFinished(i, iProgressListener);
            return false;
        }
        synchronized (this.mLock) {
            size = this.mStartedUsers.size();
            iArr = new int[size];
            for (int i2 = 0; i2 < size; i2++) {
                iArr[i2] = this.mStartedUsers.keyAt(i2);
            }
        }
        for (int i3 = 0; i3 < size; i3++) {
            int i4 = iArr[i3];
            UserInfo profileParent = this.mInjector.getUserManager().getProfileParent(i4);
            if (profileParent != null && profileParent.id == i && i4 != i) {
                Slogf.m30d("ActivityManager", "User " + i4 + " (parent " + profileParent.id + "): attempting unlock because parent was just unlocked");
                maybeUnlockUser(i4);
            }
        }
        return true;
    }

    public boolean switchUser(int i) {
        enforceShellRestriction("no_debugging_features", i);
        EventLog.writeEvent(30075, i);
        int currentUserId = getCurrentUserId();
        UserInfo userInfo = getUserInfo(i);
        if (i == currentUserId) {
            Slogf.m22i("ActivityManager", "user #" + i + " is already the current user");
            return true;
        } else if (userInfo == null) {
            Slogf.m14w("ActivityManager", "No user info for user #" + i);
            return false;
        } else if (!userInfo.supportsSwitchTo()) {
            Slogf.m14w("ActivityManager", "Cannot switch to User #" + i + ": not supported");
            return false;
        } else if (FactoryResetter.isFactoryResetting()) {
            Slogf.m14w("ActivityManager", "Cannot switch to User #" + i + ": factory reset in progress");
            return false;
        } else {
            synchronized (this.mLock) {
                if (!this.mInitialized) {
                    Slogf.m26e("ActivityManager", "Cannot switch to User #" + i + ": UserController not ready yet");
                    return false;
                }
                this.mTargetUserId = i;
                boolean z = this.mUserSwitchUiEnabled;
                if (z) {
                    Pair pair = new Pair(getUserInfo(currentUserId), userInfo);
                    this.mUiHandler.removeMessages(1000);
                    Handler handler = this.mUiHandler;
                    handler.sendMessage(handler.obtainMessage(1000, pair));
                } else {
                    this.mHandler.removeMessages(120);
                    Handler handler2 = this.mHandler;
                    handler2.sendMessage(handler2.obtainMessage(120, i, 0));
                }
                return true;
            }
        }
    }

    public final void dismissUserSwitchDialog() {
        this.mInjector.dismissUserSwitchingDialog();
    }

    public final void showUserSwitchDialog(Pair<UserInfo, UserInfo> pair) {
        this.mInjector.showUserSwitchingDialog((UserInfo) pair.first, (UserInfo) pair.second, getSwitchingFromSystemUserMessageUnchecked(), getSwitchingToSystemUserMessageUnchecked());
    }

    public final void dispatchForegroundProfileChanged(int i) {
        int beginBroadcast = this.mUserSwitchObservers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mUserSwitchObservers.getBroadcastItem(i2).onForegroundProfileSwitch(i);
            } catch (RemoteException unused) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
    }

    @VisibleForTesting
    public void dispatchUserSwitchComplete(int i, int i2) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("dispatchUserSwitchComplete-" + i2);
        this.mInjector.getWindowManager().setSwitchingUser(false);
        int beginBroadcast = this.mUserSwitchObservers.beginBroadcast();
        for (int i3 = 0; i3 < beginBroadcast; i3++) {
            try {
                timingsTraceAndSlog.traceBegin("onUserSwitchComplete-" + i2 + " #" + i3 + " " + this.mUserSwitchObservers.getBroadcastCookie(i3));
                this.mUserSwitchObservers.getBroadcastItem(i3).onUserSwitchComplete(i2);
                timingsTraceAndSlog.traceEnd();
            } catch (RemoteException unused) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
        timingsTraceAndSlog.traceBegin("sendUserSwitchBroadcasts-" + i + PackageManagerShellCommandDataLoader.STDIN_PATH + i2);
        sendUserSwitchBroadcasts(i, i2);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceEnd();
    }

    public final void dispatchLockedBootComplete(int i) {
        int beginBroadcast = this.mUserSwitchObservers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mUserSwitchObservers.getBroadcastItem(i2).onLockedBootComplete(i);
            } catch (RemoteException unused) {
            }
        }
        this.mUserSwitchObservers.finishBroadcast();
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x001d A[Catch: all -> 0x0018, DONT_GENERATE, TryCatch #0 {all -> 0x0018, blocks: (B:7:0x000f, B:15:0x001d, B:17:0x001f, B:18:0x0028), top: B:22:0x000f }] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x001f A[Catch: all -> 0x0018, TryCatch #0 {all -> 0x0018, blocks: (B:7:0x000f, B:15:0x001d, B:17:0x001f, B:18:0x0028), top: B:22:0x000f }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void stopUserOnSwitchIfEnforced(int i) {
        boolean z;
        if (i == 0) {
            return;
        }
        boolean hasUserRestriction = hasUserRestriction("no_run_in_background", i);
        synchronized (this.mLock) {
            if (!hasUserRestriction) {
                try {
                    if (!shouldStopUserOnSwitch()) {
                        z = false;
                        if (z) {
                            return;
                        }
                        stopUsersLU(i, false, true, null, null);
                        return;
                    }
                } finally {
                }
            }
            z = true;
            if (z) {
            }
        }
    }

    public final void timeoutUserSwitch(UserState userState, int i, int i2) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("ActivityManager");
        timingsTraceAndSlog.traceBegin("timeoutUserSwitch-" + i + "-to-" + i2);
        synchronized (this.mLock) {
            Slogf.m26e("ActivityManager", "User switch timeout: from " + i + " to " + i2);
            this.mTimeoutUserSwitchCallbacks = this.mCurWaitingUserSwitchCallbacks;
            this.mHandler.removeMessages(90);
            sendContinueUserSwitchLU(userState, i, i2);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(90, i, i2), 5000L);
        }
        timingsTraceAndSlog.traceEnd();
    }

    public final void timeoutUserSwitchCallbacks(int i, int i2) {
        synchronized (this.mLock) {
            ArraySet<String> arraySet = this.mTimeoutUserSwitchCallbacks;
            if (arraySet != null && !arraySet.isEmpty()) {
                Slogf.wtf("ActivityManager", "User switch timeout: from " + i + " to " + i2 + ". Observers that didn't respond: " + this.mTimeoutUserSwitchCallbacks);
                this.mTimeoutUserSwitchCallbacks = null;
            }
        }
    }

    @VisibleForTesting
    public void dispatchUserSwitch(final UserState userState, final int i, final int i2) {
        TimingsTraceAndSlog timingsTraceAndSlog;
        UserController userController;
        int i3;
        final long j;
        final AtomicInteger atomicInteger;
        ArraySet<String> arraySet;
        int i4;
        TimingsTraceAndSlog timingsTraceAndSlog2;
        int i5;
        UserController userController2;
        IRemoteCallback.Stub stub;
        UserController userController3 = this;
        int i6 = i2;
        TimingsTraceAndSlog timingsTraceAndSlog3 = new TimingsTraceAndSlog();
        timingsTraceAndSlog3.traceBegin("dispatchUserSwitch-" + i + "-to-" + i6);
        EventLog.writeEvent(30079, Integer.valueOf(i), Integer.valueOf(i2));
        int beginBroadcast = userController3.mUserSwitchObservers.beginBroadcast();
        if (beginBroadcast > 0) {
            final ArraySet<String> arraySet2 = new ArraySet<>();
            synchronized (userController3.mLock) {
                userState.switching = true;
                userController3.mCurWaitingUserSwitchCallbacks = arraySet2;
            }
            AtomicInteger atomicInteger2 = new AtomicInteger(beginBroadcast);
            long userSwitchTimeoutMs = getUserSwitchTimeoutMs();
            final long elapsedRealtime = SystemClock.elapsedRealtime();
            int i7 = 0;
            while (true) {
                int i8 = i7;
                if (i8 >= beginBroadcast) {
                    break;
                }
                final long elapsedRealtime2 = SystemClock.elapsedRealtime();
                try {
                    final String str = "#" + i8 + " " + userController3.mUserSwitchObservers.getBroadcastCookie(i8);
                    synchronized (userController3.mLock) {
                        arraySet2.add(str);
                    }
                    j = userSwitchTimeoutMs;
                    atomicInteger = atomicInteger2;
                    arraySet = arraySet2;
                    i4 = beginBroadcast;
                    TimingsTraceAndSlog timingsTraceAndSlog4 = timingsTraceAndSlog3;
                    try {
                        stub = new IRemoteCallback.Stub() { // from class: com.android.server.am.UserController.7
                            public void sendResult(Bundle bundle) throws RemoteException {
                                synchronized (UserController.this.mLock) {
                                    long elapsedRealtime3 = SystemClock.elapsedRealtime() - elapsedRealtime2;
                                    if (elapsedRealtime3 > 500) {
                                        Slogf.m14w("ActivityManager", "User switch slowed down by observer " + str + ": result took " + elapsedRealtime3 + " ms to process.");
                                    }
                                    long elapsedRealtime4 = SystemClock.elapsedRealtime() - elapsedRealtime;
                                    if (elapsedRealtime4 > j) {
                                        Slogf.m26e("ActivityManager", "User switch timeout: observer " + str + "'s result was received " + elapsedRealtime4 + " ms after dispatchUserSwitch.");
                                    }
                                    TimingsTraceAndSlog timingsTraceAndSlog5 = new TimingsTraceAndSlog("ActivityManager");
                                    timingsTraceAndSlog5.traceBegin("onUserSwitchingReply-" + str);
                                    arraySet2.remove(str);
                                    if (atomicInteger.decrementAndGet() == 0 && arraySet2 == UserController.this.mCurWaitingUserSwitchCallbacks) {
                                        UserController.this.sendContinueUserSwitchLU(userState, i, i2);
                                    }
                                    timingsTraceAndSlog5.traceEnd();
                                }
                            }
                        };
                        timingsTraceAndSlog4.traceBegin("onUserSwitching-" + str);
                        userController2 = this;
                        timingsTraceAndSlog2 = timingsTraceAndSlog4;
                    } catch (RemoteException unused) {
                        userController2 = this;
                        i5 = i2;
                        timingsTraceAndSlog2 = timingsTraceAndSlog4;
                    }
                    try {
                        i3 = i8;
                        try {
                            i5 = i2;
                            try {
                                userController2.mUserSwitchObservers.getBroadcastItem(i3).onUserSwitching(i5, stub);
                                timingsTraceAndSlog2.traceEnd();
                            } catch (RemoteException unused2) {
                            }
                        } catch (RemoteException unused3) {
                            i5 = i2;
                        }
                    } catch (RemoteException unused4) {
                        i5 = i2;
                        i3 = i8;
                        i7 = i3 + 1;
                        userController3 = userController2;
                        timingsTraceAndSlog3 = timingsTraceAndSlog2;
                        i6 = i5;
                        userSwitchTimeoutMs = j;
                        atomicInteger2 = atomicInteger;
                        arraySet2 = arraySet;
                        beginBroadcast = i4;
                    }
                } catch (RemoteException unused5) {
                    i3 = i8;
                    j = userSwitchTimeoutMs;
                    atomicInteger = atomicInteger2;
                    arraySet = arraySet2;
                    i4 = beginBroadcast;
                    timingsTraceAndSlog2 = timingsTraceAndSlog3;
                    i5 = i6;
                    userController2 = userController3;
                }
                i7 = i3 + 1;
                userController3 = userController2;
                timingsTraceAndSlog3 = timingsTraceAndSlog2;
                i6 = i5;
                userSwitchTimeoutMs = j;
                atomicInteger2 = atomicInteger;
                arraySet2 = arraySet;
                beginBroadcast = i4;
            }
            timingsTraceAndSlog = timingsTraceAndSlog3;
            userController = userController3;
        } else {
            timingsTraceAndSlog = timingsTraceAndSlog3;
            userController = userController3;
            synchronized (userController.mLock) {
                sendContinueUserSwitchLU(userState, i, i2);
            }
        }
        userController.mUserSwitchObservers.finishBroadcast();
        timingsTraceAndSlog.traceEnd();
    }

    @GuardedBy({"mLock"})
    public final void sendContinueUserSwitchLU(UserState userState, int i, int i2) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("ActivityManager");
        timingsTraceAndSlog.traceBegin("sendContinueUserSwitchLU-" + i + "-to-" + i2);
        this.mCurWaitingUserSwitchCallbacks = null;
        this.mHandler.removeMessages(30);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(20, i, i2, userState));
        timingsTraceAndSlog.traceEnd();
    }

    @VisibleForTesting
    public void continueUserSwitch(UserState userState, int i, int i2) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("continueUserSwitch-" + i + "-to-" + i2);
        EventLog.writeEvent(30080, Integer.valueOf(i), Integer.valueOf(i2));
        this.mHandler.removeMessages(130);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(130, i, i2));
        userState.switching = false;
        stopGuestOrEphemeralUserIfBackground(i);
        stopUserOnSwitchIfEnforced(i);
        timingsTraceAndSlog.traceEnd();
    }

    @VisibleForTesting
    public void completeUserSwitch(final int i, final int i2) {
        final boolean isUserSwitchUiEnabled = isUserSwitchUiEnabled();
        Runnable runnable = new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UserController.this.lambda$completeUserSwitch$14(isUserSwitchUiEnabled, i, i2);
            }
        };
        if (isUserSwitchUiEnabled && !this.mInjector.getKeyguardManager().isDeviceSecure(i2)) {
            this.mInjector.dismissKeyguard(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$completeUserSwitch$14(boolean z, int i, int i2) {
        if (z) {
            unfreezeScreen();
        }
        this.mHandler.removeMessages(80);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(80, i, i2));
    }

    @VisibleForTesting
    public void unfreezeScreen() {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("stopFreezingScreen");
        this.mInjector.getWindowManager().stopFreezingScreen();
        timingsTraceAndSlog.traceEnd();
    }

    public final void moveUserToForeground(UserState userState, int i) {
        if (this.mInjector.taskSupervisorSwitchUser(i, userState)) {
            this.mInjector.startHomeActivity(i, "moveUserToForeground");
        } else {
            this.mInjector.taskSupervisorResumeFocusedStackTopActivity();
        }
        EventLogTags.writeAmSwitchUser(i);
    }

    public void sendUserStartedBroadcast(int i, int i2, int i3) {
        if (i == 0) {
            synchronized (this.mLock) {
                if (this.mIsBroadcastSentForSystemUserStarted) {
                    return;
                }
                this.mIsBroadcastSentForSystemUserStarted = true;
            }
        }
        Intent intent = new Intent("android.intent.action.USER_STARTED");
        intent.addFlags(1342177280);
        intent.putExtra("android.intent.extra.user_handle", i);
        this.mInjector.broadcastIntent(intent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, i2, i3, i);
    }

    public void sendUserStartingBroadcast(int i, int i2, int i3) {
        if (i == 0) {
            synchronized (this.mLock) {
                if (this.mIsBroadcastSentForSystemUserStarting) {
                    return;
                }
                this.mIsBroadcastSentForSystemUserStarting = true;
            }
        }
        Intent intent = new Intent("android.intent.action.USER_STARTING");
        intent.addFlags(1073741824);
        intent.putExtra("android.intent.extra.user_handle", i);
        this.mInjector.broadcastIntent(intent, null, new IIntentReceiver.Stub() { // from class: com.android.server.am.UserController.8
            public void performReceive(Intent intent2, int i4, String str, Bundle bundle, boolean z, boolean z2, int i5) throws RemoteException {
            }
        }, 0, null, null, new String[]{"android.permission.INTERACT_ACROSS_USERS"}, -1, null, true, false, ActivityManagerService.MY_PID, 1000, i2, i3, -1);
    }

    public void sendUserSwitchBroadcasts(int i, int i2) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        String str = "android.intent.extra.USER";
        String str2 = "android.intent.extra.user_handle";
        int i3 = 1342177280;
        if (i >= 0) {
            try {
                List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(i, false);
                int size = profiles.size();
                int i4 = 0;
                while (i4 < size) {
                    int i5 = profiles.get(i4).id;
                    Intent intent = new Intent("android.intent.action.USER_BACKGROUND");
                    intent.addFlags(i3);
                    intent.putExtra(str2, i5);
                    intent.putExtra(str, UserHandle.of(i5));
                    this.mInjector.broadcastIntent(intent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, i5);
                    i4++;
                    size = size;
                    str2 = str2;
                    str = str;
                    i3 = 1342177280;
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
        String str3 = str2;
        String str4 = str;
        if (i2 >= 0) {
            char c = 0;
            List<UserInfo> profiles2 = this.mInjector.getUserManager().getProfiles(i2, false);
            int size2 = profiles2.size();
            int i6 = 0;
            while (i6 < size2) {
                int i7 = profiles2.get(i6).id;
                Intent intent2 = new Intent("android.intent.action.USER_FOREGROUND");
                intent2.addFlags(1342177280);
                String str5 = str3;
                intent2.putExtra(str5, i7);
                String str6 = str4;
                intent2.putExtra(str6, UserHandle.of(i7));
                this.mInjector.broadcastIntent(intent2, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, i7);
                i6++;
                size2 = size2;
                c = c;
                str4 = str6;
                str3 = str5;
            }
            Intent intent3 = new Intent("android.intent.action.USER_SWITCHED");
            intent3.addFlags(1342177280);
            intent3.putExtra(str3, i2);
            intent3.putExtra(str4, UserHandle.of(i2));
            Injector injector = this.mInjector;
            String[] strArr = new String[1];
            strArr[c] = "android.permission.MANAGE_USERS";
            injector.broadcastIntent(intent3, null, null, 0, null, null, strArr, -1, null, false, false, ActivityManagerService.MY_PID, 1000, callingUid, callingPid, -1);
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public final void broadcastProfileAccessibleStateChanged(int i, int i2, String str) {
        Intent intent = new Intent(str);
        intent.putExtra("android.intent.extra.USER", UserHandle.of(i));
        intent.addFlags(1342177280);
        this.mInjector.broadcastIntent(intent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x0100  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int handleIncomingUser(int i, int i2, int i3, boolean z, int i4, String str, String str2) {
        int i5;
        int userId = UserHandle.getUserId(i2);
        if (userId == i3) {
            return i3;
        }
        int unsafeConvertIncomingUser = unsafeConvertIncomingUser(i3);
        if (i2 != 0 && i2 != 1000) {
            boolean isSameProfileGroup = isSameProfileGroup(userId, unsafeConvertIncomingUser);
            boolean z2 = true;
            if (this.mInjector.isCallerRecents(i2) && isSameProfileGroup) {
                i5 = 1;
            } else {
                if (this.mInjector.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i, i2, -1, true) != 0) {
                    if (i4 != 2) {
                        if (!canInteractWithAcrossProfilesPermission(i4, isSameProfileGroup, i, i2, str2)) {
                            if (this.mInjector.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS", i, i2, -1, true) == 0) {
                                if (i4 == 0 || i4 == 3) {
                                    i5 = 1;
                                    z2 = true;
                                } else {
                                    i5 = 1;
                                    if (i4 != 1) {
                                        throw new IllegalArgumentException("Unknown mode: " + i4);
                                    }
                                    z2 = isSameProfileGroup;
                                }
                            }
                        }
                    }
                    z2 = false;
                    i5 = 1;
                }
                i5 = 1;
                z2 = true;
            }
            if (!z2) {
                if (i3 != -3) {
                    StringBuilder sb = new StringBuilder(128);
                    sb.append("Permission Denial: ");
                    sb.append(str);
                    if (str2 != null) {
                        sb.append(" from ");
                        sb.append(str2);
                    }
                    sb.append(" asks to run as user ");
                    sb.append(i3);
                    sb.append(" but is calling from uid ");
                    UserHandle.formatUid(sb, i2);
                    sb.append("; this requires ");
                    sb.append("android.permission.INTERACT_ACROSS_USERS_FULL");
                    if (i4 != 2) {
                        if (i4 == 0 || i4 == 3 || (i4 == i5 && isSameProfileGroup)) {
                            sb.append(" or ");
                            sb.append("android.permission.INTERACT_ACROSS_USERS");
                        }
                        if (isSameProfileGroup && i4 == 3) {
                            sb.append(" or ");
                            sb.append("android.permission.INTERACT_ACROSS_PROFILES");
                        }
                    }
                    String sb2 = sb.toString();
                    Slogf.m14w("ActivityManager", sb2);
                    throw new SecurityException(sb2);
                }
                if (!z) {
                    ensureNotSpecialUser(userId);
                }
                if (i2 == 2000 || userId < 0 || !hasUserRestriction("no_debugging_features", userId)) {
                    return userId;
                }
                throw new SecurityException("Shell does not have permission to access user " + userId + "\n " + Debug.getCallers(3));
            }
        }
        userId = unsafeConvertIncomingUser;
        if (!z) {
        }
        if (i2 == 2000) {
        }
        return userId;
    }

    public final boolean canInteractWithAcrossProfilesPermission(int i, boolean z, int i2, int i3, String str) {
        if (i == 3 && z) {
            return this.mInjector.checkPermissionForPreflight("android.permission.INTERACT_ACROSS_PROFILES", i2, i3, str);
        }
        return false;
    }

    public int unsafeConvertIncomingUser(int i) {
        return (i == -2 || i == -3) ? getCurrentUserId() : i;
    }

    public void ensureNotSpecialUser(int i) {
        if (i >= 0) {
            return;
        }
        throw new IllegalArgumentException("Call does not support special user #" + i);
    }

    public void registerUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver, String str) {
        Objects.requireNonNull(str, "Observer name cannot be null");
        checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "registerUserSwitchObserver");
        this.mUserSwitchObservers.register(iUserSwitchObserver, str);
    }

    public void sendForegroundProfileChanged(int i) {
        this.mHandler.removeMessages(70);
        this.mHandler.obtainMessage(70, i, 0).sendToTarget();
    }

    public void unregisterUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) {
        this.mUserSwitchObservers.unregister(iUserSwitchObserver);
    }

    public UserState getStartedUserState(int i) {
        UserState userState;
        synchronized (this.mLock) {
            userState = this.mStartedUsers.get(i);
        }
        return userState;
    }

    public boolean hasStartedUserState(int i) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mStartedUsers.get(i) != null;
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final void updateStartedUserArrayLU() {
        int i = 0;
        for (int i2 = 0; i2 < this.mStartedUsers.size(); i2++) {
            int i3 = this.mStartedUsers.valueAt(i2).state;
            if (i3 != 4 && i3 != 5) {
                i++;
            }
        }
        this.mStartedUserArray = new int[i];
        int i4 = 0;
        for (int i5 = 0; i5 < this.mStartedUsers.size(); i5++) {
            int i6 = this.mStartedUsers.valueAt(i5).state;
            if (i6 != 4 && i6 != 5) {
                this.mStartedUserArray[i4] = this.mStartedUsers.keyAt(i5);
                i4++;
            }
        }
    }

    @VisibleForTesting
    public void setAllowUserUnlocking(boolean z) {
        this.mAllowUserUnlocking = z;
    }

    public void onBootComplete(IIntentReceiver iIntentReceiver) {
        SparseArray<UserState> clone;
        setAllowUserUnlocking(true);
        synchronized (this.mLock) {
            clone = this.mStartedUsers.clone();
        }
        Preconditions.checkArgument(clone.keyAt(0) == 0);
        for (int i = 0; i < clone.size(); i++) {
            int keyAt = clone.keyAt(i);
            UserState valueAt = clone.valueAt(i);
            if (!this.mInjector.isHeadlessSystemUserMode()) {
                finishUserBoot(valueAt, iIntentReceiver);
            } else {
                if (keyAt == 0) {
                    sendLockedBootCompletedBroadcast(iIntentReceiver, keyAt);
                }
                maybeUnlockUser(keyAt);
            }
        }
    }

    public void onSystemReady() {
        this.mInjector.getUserManagerInternal().addUserLifecycleListener(this.mUserLifecycleListener);
        updateProfileRelatedCaches();
        this.mInjector.reportCurWakefulnessUsageEvent();
    }

    public void onSystemUserStarting() {
        if (this.mInjector.isHeadlessSystemUserMode()) {
            return;
        }
        this.mInjector.onUserStarting(0);
        this.mInjector.onSystemUserVisibilityChanged(true);
    }

    public final void updateProfileRelatedCaches() {
        List<UserInfo> profiles = this.mInjector.getUserManager().getProfiles(getCurrentUserId(), false);
        int size = profiles.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = profiles.get(i).id;
        }
        List<UserInfo> users = this.mInjector.getUserManager().getUsers(false);
        synchronized (this.mLock) {
            this.mCurrentProfileIds = iArr;
            this.mUserProfileGroupIds.clear();
            for (int i2 = 0; i2 < users.size(); i2++) {
                UserInfo userInfo = users.get(i2);
                int i3 = userInfo.profileGroupId;
                if (i3 != -10000) {
                    this.mUserProfileGroupIds.put(userInfo.id, i3);
                }
            }
        }
    }

    public int[] getStartedUserArray() {
        int[] iArr;
        synchronized (this.mLock) {
            iArr = this.mStartedUserArray;
        }
        return iArr;
    }

    public boolean isUserRunning(int i, int i2) {
        UserState startedUserState = getStartedUserState(i);
        if (startedUserState == null) {
            return false;
        }
        if ((i2 & 1) != 0) {
            return true;
        }
        if ((i2 & 2) != 0) {
            int i3 = startedUserState.state;
            return i3 == 0 || i3 == 1;
        } else if ((i2 & 8) != 0) {
            int i4 = startedUserState.state;
            if (i4 == 2 || i4 == 3) {
                return true;
            }
            if (i4 == 4 || i4 == 5) {
                return StorageManager.isUserKeyUnlocked(i);
            }
            return false;
        } else if ((i2 & 4) != 0) {
            int i5 = startedUserState.state;
            if (i5 != 3) {
                if (i5 == 4 || i5 == 5) {
                    return StorageManager.isUserKeyUnlocked(i);
                }
                return false;
            }
            return true;
        } else {
            int i6 = startedUserState.state;
            return (i6 == 4 || i6 == 5) ? false : true;
        }
    }

    public boolean isSystemUserStarted() {
        synchronized (this.mLock) {
            boolean z = false;
            UserState userState = this.mStartedUsers.get(0);
            if (userState == null) {
                return false;
            }
            int i = userState.state;
            if (i == 1 || i == 2 || i == 3) {
                z = true;
            }
            return z;
        }
    }

    public final void checkGetCurrentUserPermissions() {
        if (this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") == 0 || this.mInjector.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            return;
        }
        String str = "Permission Denial: getCurrentUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.INTERACT_ACROSS_USERS";
        Slogf.m14w("ActivityManager", str);
        throw new SecurityException(str);
    }

    public UserInfo getCurrentUser() {
        UserInfo currentUserLU;
        checkGetCurrentUserPermissions();
        if (this.mTargetUserId == -10000) {
            return getUserInfo(this.mCurrentUserId);
        }
        synchronized (this.mLock) {
            currentUserLU = getCurrentUserLU();
        }
        return currentUserLU;
    }

    public int getCurrentUserIdChecked() {
        checkGetCurrentUserPermissions();
        if (this.mTargetUserId == -10000) {
            return this.mCurrentUserId;
        }
        return getCurrentOrTargetUserId();
    }

    @GuardedBy({"mLock"})
    public final UserInfo getCurrentUserLU() {
        return getUserInfo(getCurrentOrTargetUserIdLU());
    }

    public int getCurrentOrTargetUserId() {
        int currentOrTargetUserIdLU;
        synchronized (this.mLock) {
            currentOrTargetUserIdLU = getCurrentOrTargetUserIdLU();
        }
        return currentOrTargetUserIdLU;
    }

    @GuardedBy({"mLock"})
    public final int getCurrentOrTargetUserIdLU() {
        return this.mTargetUserId != -10000 ? this.mTargetUserId : this.mCurrentUserId;
    }

    public Pair<Integer, Integer> getCurrentAndTargetUserIds() {
        Pair<Integer, Integer> pair;
        synchronized (this.mLock) {
            pair = new Pair<>(Integer.valueOf(this.mCurrentUserId), Integer.valueOf(this.mTargetUserId));
        }
        return pair;
    }

    public int getCurrentUserId() {
        int i;
        synchronized (this.mLock) {
            i = this.mCurrentUserId;
        }
        return i;
    }

    @GuardedBy({"mLock"})
    public final boolean isCurrentUserLU(int i) {
        return i == getCurrentOrTargetUserIdLU();
    }

    public int[] getUsers() {
        UserManagerService userManager = this.mInjector.getUserManager();
        return userManager != null ? userManager.getUserIds() : new int[]{0};
    }

    public final UserInfo getUserInfo(int i) {
        return this.mInjector.getUserManager().getUserInfo(i);
    }

    public final UserProperties getUserProperties(int i) {
        return this.mInjector.getUserManagerInternal().getUserProperties(i);
    }

    public int[] getUserIds() {
        return this.mInjector.getUserManager().getUserIds();
    }

    public int[] expandUserId(int i) {
        if (i != -1) {
            return new int[]{i};
        }
        return getUsers();
    }

    public boolean exists(int i) {
        return this.mInjector.getUserManager().exists(i);
    }

    public final void checkCallingPermission(String str, String str2) {
        checkCallingHasOneOfThosePermissions(str2, str);
    }

    public final void checkCallingHasOneOfThosePermissions(String str, String... strArr) {
        for (String str2 : strArr) {
            if (this.mInjector.checkCallingPermission(str2) == 0) {
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Permission denial: ");
        sb.append(str);
        sb.append("() from pid=");
        sb.append(Binder.getCallingPid());
        sb.append(", uid=");
        sb.append(Binder.getCallingUid());
        sb.append(" requires ");
        sb.append(strArr.length == 1 ? strArr[0] : "one of " + Arrays.toString(strArr));
        String sb2 = sb.toString();
        Slogf.m14w("ActivityManager", sb2);
        throw new SecurityException(sb2);
    }

    public final void enforceShellRestriction(String str, int i) {
        if (Binder.getCallingUid() == 2000) {
            if (i < 0 || hasUserRestriction(str, i)) {
                throw new SecurityException("Shell does not have permission to access user " + i);
            }
        }
    }

    public boolean hasUserRestriction(String str, int i) {
        return this.mInjector.getUserManager().hasUserRestriction(str, i);
    }

    public boolean isSameProfileGroup(int i, int i2) {
        boolean z = true;
        if (i == i2) {
            return true;
        }
        synchronized (this.mLock) {
            int i3 = this.mUserProfileGroupIds.get(i, -10000);
            int i4 = this.mUserProfileGroupIds.get(i2, -10000);
            if (i3 == -10000 || i3 != i4) {
                z = false;
            }
        }
        return z;
    }

    public boolean isUserOrItsParentRunning(int i) {
        synchronized (this.mLock) {
            if (isUserRunning(i, 0)) {
                return true;
            }
            int i2 = this.mUserProfileGroupIds.get(i, -10000);
            if (i2 == -10000) {
                return false;
            }
            return isUserRunning(i2, 0);
        }
    }

    public boolean isCurrentProfile(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mCurrentProfileIds, i);
        }
        return contains;
    }

    public int[] getCurrentProfileIds() {
        int[] iArr;
        synchronized (this.mLock) {
            iArr = this.mCurrentProfileIds;
        }
        return iArr;
    }

    public final void onUserAdded(UserInfo userInfo) {
        if (userInfo.isProfile()) {
            synchronized (this.mLock) {
                if (userInfo.profileGroupId == this.mCurrentUserId) {
                    this.mCurrentProfileIds = ArrayUtils.appendInt(this.mCurrentProfileIds, userInfo.id);
                }
                this.mUserProfileGroupIds.put(userInfo.id, userInfo.profileGroupId);
            }
        }
    }

    public void onUserRemoved(int i) {
        synchronized (this.mLock) {
            for (int size = this.mUserProfileGroupIds.size() - 1; size >= 0; size--) {
                if (this.mUserProfileGroupIds.keyAt(size) == i || this.mUserProfileGroupIds.valueAt(size) == i) {
                    this.mUserProfileGroupIds.removeAt(size);
                }
            }
            this.mCurrentProfileIds = ArrayUtils.removeInt(this.mCurrentProfileIds, i);
        }
    }

    public boolean shouldConfirmCredentials(int i) {
        UserProperties userProperties;
        if (getStartedUserState(i) == null || (userProperties = getUserProperties(i)) == null || !userProperties.isCredentialShareableWithParent()) {
            return false;
        }
        if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
            KeyguardManager keyguardManager = this.mInjector.getKeyguardManager();
            return keyguardManager.isDeviceLocked(i) && keyguardManager.isDeviceSecure(i);
        }
        return isUserRunning(i, 2);
    }

    public void setSwitchingFromSystemUserMessage(String str) {
        synchronized (this.mLock) {
            this.mSwitchingFromSystemUserMessage = str;
        }
    }

    public void setSwitchingToSystemUserMessage(String str) {
        synchronized (this.mLock) {
            this.mSwitchingToSystemUserMessage = str;
        }
    }

    public String getSwitchingFromSystemUserMessage() {
        checkHasManageUsersPermission("getSwitchingFromSystemUserMessage()");
        return getSwitchingFromSystemUserMessageUnchecked();
    }

    public String getSwitchingToSystemUserMessage() {
        checkHasManageUsersPermission("getSwitchingToSystemUserMessage()");
        return getSwitchingToSystemUserMessageUnchecked();
    }

    public final String getSwitchingFromSystemUserMessageUnchecked() {
        String str;
        synchronized (this.mLock) {
            str = this.mSwitchingFromSystemUserMessage;
        }
        return str;
    }

    public final String getSwitchingToSystemUserMessageUnchecked() {
        String str;
        synchronized (this.mLock) {
            str = this.mSwitchingToSystemUserMessage;
        }
        return str;
    }

    public final void checkHasManageUsersPermission(String str) {
        if (this.mInjector.checkCallingPermission("android.permission.MANAGE_USERS") != -1) {
            return;
        }
        throw new SecurityException("You need MANAGE_USERS permission to call " + str);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        synchronized (this.mLock) {
            long start = protoOutputStream.start(j);
            int i = 0;
            for (int i2 = 0; i2 < this.mStartedUsers.size(); i2++) {
                UserState valueAt = this.mStartedUsers.valueAt(i2);
                long start2 = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, valueAt.mHandle.getIdentifier());
                valueAt.dumpDebug(protoOutputStream, 1146756268034L);
                protoOutputStream.end(start2);
            }
            int i3 = 0;
            while (true) {
                int[] iArr = this.mStartedUserArray;
                if (i3 >= iArr.length) {
                    break;
                }
                protoOutputStream.write(2220498092034L, iArr[i3]);
                i3++;
            }
            for (int i4 = 0; i4 < this.mUserLru.size(); i4++) {
                protoOutputStream.write(2220498092035L, this.mUserLru.get(i4).intValue());
            }
            if (this.mUserProfileGroupIds.size() > 0) {
                for (int i5 = 0; i5 < this.mUserProfileGroupIds.size(); i5++) {
                    long start3 = protoOutputStream.start(2246267895812L);
                    protoOutputStream.write(1120986464257L, this.mUserProfileGroupIds.keyAt(i5));
                    protoOutputStream.write(1120986464258L, this.mUserProfileGroupIds.valueAt(i5));
                    protoOutputStream.end(start3);
                }
            }
            protoOutputStream.write(1120986464261L, this.mCurrentUserId);
            while (true) {
                int[] iArr2 = this.mCurrentProfileIds;
                if (i < iArr2.length) {
                    protoOutputStream.write(2220498092038L, iArr2[i]);
                    i++;
                } else {
                    protoOutputStream.end(start);
                }
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println("  mStartedUsers:");
            for (int i = 0; i < this.mStartedUsers.size(); i++) {
                UserState valueAt = this.mStartedUsers.valueAt(i);
                printWriter.print("    User #");
                printWriter.print(valueAt.mHandle.getIdentifier());
                printWriter.print(": ");
                valueAt.dump("", printWriter);
            }
            printWriter.print("  mStartedUserArray: [");
            for (int i2 = 0; i2 < this.mStartedUserArray.length; i2++) {
                if (i2 > 0) {
                    printWriter.print(", ");
                }
                printWriter.print(this.mStartedUserArray[i2]);
            }
            printWriter.println("]");
            printWriter.print("  mUserLru: [");
            for (int i3 = 0; i3 < this.mUserLru.size(); i3++) {
                if (i3 > 0) {
                    printWriter.print(", ");
                }
                printWriter.print(this.mUserLru.get(i3));
            }
            printWriter.println("]");
            if (this.mUserProfileGroupIds.size() > 0) {
                printWriter.println("  mUserProfileGroupIds:");
                for (int i4 = 0; i4 < this.mUserProfileGroupIds.size(); i4++) {
                    printWriter.print("    User #");
                    printWriter.print(this.mUserProfileGroupIds.keyAt(i4));
                    printWriter.print(" -> profile #");
                    printWriter.println(this.mUserProfileGroupIds.valueAt(i4));
                }
            }
            printWriter.println("  mCurrentProfileIds:" + Arrays.toString(this.mCurrentProfileIds));
            printWriter.println("  mCurrentUserId:" + this.mCurrentUserId);
            printWriter.println("  mTargetUserId:" + this.mTargetUserId);
            printWriter.println("  mLastActiveUsers:" + this.mLastActiveUsers);
            printWriter.println("  mDelayUserDataLocking:" + this.mDelayUserDataLocking);
            printWriter.println("  mAllowUserUnlocking:" + this.mAllowUserUnlocking);
            printWriter.println("  shouldStopUserOnSwitch():" + shouldStopUserOnSwitch());
            printWriter.println("  mStopUserOnSwitch:" + this.mStopUserOnSwitch);
            printWriter.println("  mMaxRunningUsers:" + this.mMaxRunningUsers);
            printWriter.println("  mUserSwitchUiEnabled:" + this.mUserSwitchUiEnabled);
            printWriter.println("  mInitialized:" + this.mInitialized);
            printWriter.println("  mIsBroadcastSentForSystemUserStarted:" + this.mIsBroadcastSentForSystemUserStarted);
            printWriter.println("  mIsBroadcastSentForSystemUserStarting:" + this.mIsBroadcastSentForSystemUserStarting);
            if (this.mSwitchingFromSystemUserMessage != null) {
                printWriter.println("  mSwitchingFromSystemUserMessage: " + this.mSwitchingFromSystemUserMessage);
            }
            if (this.mSwitchingToSystemUserMessage != null) {
                printWriter.println("  mSwitchingToSystemUserMessage: " + this.mSwitchingToSystemUserMessage);
            }
            printWriter.println("  mLastUserUnlockingUptime: " + this.mLastUserUnlockingUptime);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 10:
                dispatchUserSwitch((UserState) message.obj, message.arg1, message.arg2);
                return false;
            case 20:
                continueUserSwitch((UserState) message.obj, message.arg1, message.arg2);
                return false;
            case 30:
                timeoutUserSwitch((UserState) message.obj, message.arg1, message.arg2);
                return false;
            case 40:
                startProfiles();
                return false;
            case 50:
                this.mInjector.batteryStatsServiceNoteEvent(32775, Integer.toString(message.arg1), message.arg1);
                logUserJourneyInfo(null, getUserInfo(message.arg1), 3);
                logUserLifecycleEvent(message.arg1, 2, 1);
                this.mInjector.onUserStarting(message.arg1);
                scheduleOnUserCompletedEvent(message.arg1, 1, 5000);
                logUserLifecycleEvent(message.arg1, 2, 2);
                clearSessionId(message.arg1, 3);
                return false;
            case 60:
                this.mInjector.batteryStatsServiceNoteEvent(16392, Integer.toString(message.arg2), message.arg2);
                this.mInjector.batteryStatsServiceNoteEvent(32776, Integer.toString(message.arg1), message.arg1);
                this.mInjector.getSystemServiceManager().onUserSwitching(message.arg2, message.arg1);
                scheduleOnUserCompletedEvent(message.arg1, 4, 5000);
                return false;
            case 70:
                dispatchForegroundProfileChanged(message.arg1);
                return false;
            case 80:
                dispatchUserSwitchComplete(message.arg1, message.arg2);
                logUserLifecycleEvent(message.arg2, 1, 2);
                return false;
            case 90:
                timeoutUserSwitchCallbacks(message.arg1, message.arg2);
                return false;
            case 100:
                final int i = message.arg1;
                this.mInjector.getSystemServiceManager().onUserUnlocking(i);
                FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserController.this.lambda$handleMessage$15(i);
                    }
                });
                logUserLifecycleEvent(message.arg1, 5, 2);
                logUserLifecycleEvent(message.arg1, 6, 1);
                TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
                timingsTraceAndSlog.traceBegin("finishUserUnlocked-" + i);
                finishUserUnlocked((UserState) message.obj);
                timingsTraceAndSlog.traceEnd();
                return false;
            case 105:
                this.mInjector.getSystemServiceManager().onUserUnlocked(message.arg1);
                scheduleOnUserCompletedEvent(message.arg1, 2, this.mCurrentUserId != message.arg1 ? 1000 : 5000);
                logUserLifecycleEvent(message.arg1, 6, 2);
                clearSessionId(message.arg1);
                return false;
            case 110:
                dispatchLockedBootComplete(message.arg1);
                return false;
            case 120:
                logUserJourneyInfo(getUserInfo(getCurrentUserId()), getUserInfo(message.arg1), 2);
                logUserLifecycleEvent(message.arg1, 1, 1);
                startUserInForeground(message.arg1);
                return false;
            case 130:
                completeUserSwitch(message.arg1, message.arg2);
                return false;
            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES /* 140 */:
                reportOnUserCompletedEvent((Integer) message.obj);
                return false;
            case 200:
                logAndClearSessionId(message.arg1);
                return false;
            case 1000:
                Pair<UserInfo, UserInfo> pair = (Pair) message.obj;
                logUserJourneyInfo((UserInfo) pair.first, (UserInfo) pair.second, 1);
                logUserLifecycleEvent(((UserInfo) pair.second).id, 1, 1);
                showUserSwitchDialog(pair);
                return false;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleMessage$15(int i) {
        this.mInjector.loadUserRecents(i);
    }

    @VisibleForTesting
    public void scheduleOnUserCompletedEvent(int i, int i2, int i3) {
        if (i2 != 0) {
            synchronized (this.mCompletedEventTypes) {
                SparseIntArray sparseIntArray = this.mCompletedEventTypes;
                sparseIntArray.put(i, i2 | sparseIntArray.get(i, 0));
            }
        }
        Integer valueOf = Integer.valueOf(i);
        this.mHandler.removeEqualMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES, valueOf);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES, valueOf), i3);
    }

    @VisibleForTesting
    public void reportOnUserCompletedEvent(Integer num) {
        int i;
        int i2;
        this.mHandler.removeEqualMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES, num);
        synchronized (this.mCompletedEventTypes) {
            i = 0;
            i2 = this.mCompletedEventTypes.get(num.intValue(), 0);
            this.mCompletedEventTypes.delete(num.intValue());
        }
        synchronized (this.mLock) {
            UserState userState = this.mStartedUsers.get(num.intValue());
            if (userState != null && userState.state != 5) {
                i = 1;
            }
            if (userState != null && userState.state == 3) {
                i |= 2;
            }
            if (num.intValue() == this.mCurrentUserId) {
                i |= 4;
            }
        }
        Slogf.m20i("ActivityManager", "reportOnUserCompletedEvent(%d): stored=%s, eligible=%s", num, Integer.toBinaryString(i2), Integer.toBinaryString(i));
        this.mInjector.systemServiceManagerOnUserCompletedEvent(num.intValue(), i2 & i);
    }

    public final void logUserJourneyInfo(UserInfo userInfo, UserInfo userInfo2, int i) {
        long nextLong = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        synchronized (this.mUserIdToUserJourneyMap) {
            UserJourneySession userJourneySession = this.mUserIdToUserJourneyMap.get(userInfo2.id);
            if (userJourneySession != null) {
                int i2 = userJourneySession.mJourney;
                if ((i2 != 1 && i2 != 2) || (i != 3 && i != 5)) {
                    FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, userJourneySession.mSessionId, userInfo2.id, 0, 0);
                }
                return;
            }
            this.mUserIdToUserJourneyMap.put(userInfo2.id, new UserJourneySession(nextLong, i));
            this.mHandler.removeMessages(200);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(200, userInfo2.id, 0), 90000L);
            FrameworkStatsLog.write(264, nextLong, i, userInfo != null ? userInfo.id : -1, userInfo2.id, UserManager.getUserTypeForStatsd(userInfo2.userType), userInfo2.flags);
        }
    }

    public final void logUserLifecycleEvent(int i, int i2, int i3) {
        synchronized (this.mUserIdToUserJourneyMap) {
            UserJourneySession userJourneySession = this.mUserIdToUserJourneyMap.get(i);
            if (userJourneySession != null) {
                long j = userJourneySession.mSessionId;
                if (j != 0) {
                    FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, j, i, i2, i3);
                    return;
                }
            }
            Slogf.m14w("ActivityManager", "UserLifecycleEvent " + i2 + " received without an active userJourneySession.");
        }
    }

    public final void clearSessionId(int i, int i2) {
        synchronized (this.mUserIdToUserJourneyMap) {
            UserJourneySession userJourneySession = this.mUserIdToUserJourneyMap.get(i);
            if (userJourneySession != null && userJourneySession.mJourney == i2) {
                clearSessionId(i);
            }
        }
    }

    public final void clearSessionId(int i) {
        synchronized (this.mUserIdToUserJourneyMap) {
            this.mHandler.removeMessages(200);
            this.mUserIdToUserJourneyMap.delete(i);
        }
    }

    public final void logAndClearSessionId(int i) {
        synchronized (this.mUserIdToUserJourneyMap) {
            UserJourneySession userJourneySession = this.mUserIdToUserJourneyMap.get(i);
            if (userJourneySession != null) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, userJourneySession.mSessionId, i, 0, 0);
            }
            clearSessionId(i);
        }
    }

    public final BroadcastOptions getTemporaryAppAllowlistBroadcastOptions(int i) {
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        long bootTimeTempAllowListDuration = activityManagerInternal != null ? activityManagerInternal.getBootTimeTempAllowListDuration() : 10000L;
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setTemporaryAppAllowlist(bootTimeTempAllowListDuration, 0, i, "");
        return makeBasic;
    }

    public static int getUserSwitchTimeoutMs() {
        String str = SystemProperties.get("debug.usercontroller.user_switch_timeout_ms");
        if (TextUtils.isEmpty(str)) {
            return 3000;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return 3000;
        }
    }

    public long getLastUserUnlockingUptime() {
        return this.mLastUserUnlockingUptime;
    }

    /* renamed from: com.android.server.am.UserController$UserJourneySession */
    /* loaded from: classes.dex */
    public static class UserJourneySession {
        public final int mJourney;
        public final long mSessionId;

        public UserJourneySession(long j, int i) {
            this.mJourney = i;
            this.mSessionId = j;
        }
    }

    /* renamed from: com.android.server.am.UserController$UserProgressListener */
    /* loaded from: classes.dex */
    public static class UserProgressListener extends IProgressListener.Stub {
        public volatile long mUnlockStarted;

        public UserProgressListener() {
        }

        public void onStarted(int i, Bundle bundle) throws RemoteException {
            Slogf.m30d("ActivityManager", "Started unlocking user " + i);
            this.mUnlockStarted = SystemClock.uptimeMillis();
        }

        public void onProgress(int i, int i2, Bundle bundle) throws RemoteException {
            Slogf.m30d("ActivityManager", "Unlocking user " + i + " progress " + i2);
        }

        public void onFinished(int i, Bundle bundle) throws RemoteException {
            long uptimeMillis = SystemClock.uptimeMillis() - this.mUnlockStarted;
            if (i == 0) {
                new TimingsTraceAndSlog().logDuration("SystemUserUnlock", uptimeMillis);
                return;
            }
            TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
            timingsTraceAndSlog.logDuration("User" + i + "Unlock", uptimeMillis);
        }
    }

    /* renamed from: com.android.server.am.UserController$PendingUserStart */
    /* loaded from: classes.dex */
    public static class PendingUserStart {
        public final IProgressListener unlockListener;
        public final int userId;
        public final int userStartMode;

        public PendingUserStart(int i, int i2, IProgressListener iProgressListener) {
            this.userId = i;
            this.userStartMode = i2;
            this.unlockListener = iProgressListener;
        }

        public String toString() {
            return "PendingUserStart{userId=" + this.userId + ", userStartMode=" + UserManagerInternal.userStartModeToString(this.userStartMode) + ", unlockListener=" + this.unlockListener + '}';
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.UserController$Injector */
    /* loaded from: classes.dex */
    public static class Injector {
        public Handler mHandler;
        public final ActivityManagerService mService;
        public UserManagerService mUserManager;
        public UserManagerInternal mUserManagerInternal;
        @GuardedBy({"mUserSwitchingDialogLock"})
        public UserSwitchingDialog mUserSwitchingDialog;
        public final Object mUserSwitchingDialogLock = new Object();

        public Injector(ActivityManagerService activityManagerService) {
            this.mService = activityManagerService;
        }

        public Handler getHandler(Handler.Callback callback) {
            Handler handler = new Handler(this.mService.mHandlerThread.getLooper(), callback);
            this.mHandler = handler;
            return handler;
        }

        public Handler getUiHandler(Handler.Callback callback) {
            return new Handler(this.mService.mUiHandler.getLooper(), callback);
        }

        public Context getContext() {
            return this.mService.mContext;
        }

        public LockPatternUtils getLockPatternUtils() {
            return new LockPatternUtils(getContext());
        }

        public int broadcastIntent(Intent intent, String str, IIntentReceiver iIntentReceiver, int i, String str2, Bundle bundle, String[] strArr, int i2, Bundle bundle2, boolean z, boolean z2, int i3, int i4, int i5, int i6, int i7) {
            int broadcastIntentLocked;
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if (intExtra == -10000) {
                intExtra = i7;
            }
            EventLog.writeEvent(30081, Integer.valueOf(intExtra), intent.getAction());
            ActivityManagerService activityManagerService = this.mService;
            boolean z3 = activityManagerService.mEnableModernQueue ? false : z;
            synchronized (activityManagerService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    broadcastIntentLocked = this.mService.broadcastIntentLocked(null, null, null, intent, str, iIntentReceiver, i, str2, bundle, strArr, null, null, i2, bundle2, z3, z2, i3, i4, i5, i6, i7);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return broadcastIntentLocked;
        }

        public int checkCallingPermission(String str) {
            return this.mService.checkCallingPermission(str);
        }

        public WindowManagerService getWindowManager() {
            return this.mService.mWindowManager;
        }

        public void activityManagerOnUserStopped(int i) {
            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).onUserStopped(i);
        }

        public void systemServiceManagerOnUserStopped(int i) {
            getSystemServiceManager().onUserStopped(i);
        }

        public void systemServiceManagerOnUserCompletedEvent(int i, int i2) {
            getSystemServiceManager().onUserCompletedEvent(i, i2);
        }

        public UserManagerService getUserManager() {
            if (this.mUserManager == null) {
                this.mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
            }
            return this.mUserManager;
        }

        public UserManagerInternal getUserManagerInternal() {
            if (this.mUserManagerInternal == null) {
                this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            }
            return this.mUserManagerInternal;
        }

        public KeyguardManager getKeyguardManager() {
            return (KeyguardManager) this.mService.mContext.getSystemService(KeyguardManager.class);
        }

        public void batteryStatsServiceNoteEvent(int i, String str, int i2) {
            this.mService.mBatteryStatsService.noteEvent(i, str, i2);
        }

        public boolean isRuntimeRestarted() {
            return getSystemServiceManager().isRuntimeRestarted();
        }

        public SystemServiceManager getSystemServiceManager() {
            return this.mService.mSystemServiceManager;
        }

        public boolean isFirstBootOrUpgrade() {
            IPackageManager packageManager = AppGlobals.getPackageManager();
            try {
                if (!packageManager.isFirstBoot()) {
                    if (!packageManager.isDeviceUpgrading()) {
                        return false;
                    }
                }
                return true;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void sendPreBootBroadcast(int i, boolean z, final Runnable runnable) {
            EventLog.writeEvent(30081, Integer.valueOf(i), "android.intent.action.PRE_BOOT_COMPLETED");
            new PreBootBroadcaster(this.mService, i, null, z) { // from class: com.android.server.am.UserController.Injector.1
                @Override // com.android.server.p006am.PreBootBroadcaster
                public void onFinished() {
                    runnable.run();
                }
            }.sendNext();
        }

        public void activityManagerForceStopPackage(int i, String str) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.forceStopPackageLocked(null, -1, false, false, true, false, false, i, str);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public int checkComponentPermission(String str, int i, int i2, int i3, boolean z) {
            return ActivityManagerService.checkComponentPermission(str, i, i2, i3, z);
        }

        public boolean checkPermissionForPreflight(String str, int i, int i2, String str2) {
            return PermissionChecker.checkPermissionForPreflight(getContext(), str, i, i2, str2) == 0;
        }

        public void startHomeActivity(int i, String str) {
            this.mService.mAtmInternal.startHomeActivity(i, str);
        }

        public void startUserWidgets(final int i) {
            final AppWidgetManagerInternal appWidgetManagerInternal = (AppWidgetManagerInternal) LocalServices.getService(AppWidgetManagerInternal.class);
            if (appWidgetManagerInternal != null) {
                FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.UserController$Injector$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        appWidgetManagerInternal.unlockUser(i);
                    }
                });
            }
        }

        public void updateUserConfiguration() {
            this.mService.mAtmInternal.updateUserConfiguration();
        }

        public void clearBroadcastQueueForUser(int i) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mService.clearBroadcastQueueForUserLocked(i);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void loadUserRecents(int i) {
            this.mService.mAtmInternal.loadRecentTasksForUser(i);
        }

        public void startPersistentApps(int i) {
            this.mService.startPersistentApps(i);
        }

        public void installEncryptionUnawareProviders(int i) {
            this.mService.mCpHelper.installEncryptionUnawareProviders(i);
        }

        public void dismissUserSwitchingDialog() {
            synchronized (this.mUserSwitchingDialogLock) {
                UserSwitchingDialog userSwitchingDialog = this.mUserSwitchingDialog;
                if (userSwitchingDialog != null) {
                    userSwitchingDialog.dismiss();
                    this.mUserSwitchingDialog = null;
                }
            }
        }

        public void showUserSwitchingDialog(UserInfo userInfo, UserInfo userInfo2, String str, String str2) {
            if (this.mService.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                Slogf.m14w("ActivityManager", "Showing user switch dialog on UserController, it could cause a race condition if it's shown by CarSystemUI as well");
            }
            synchronized (this.mUserSwitchingDialogLock) {
                dismissUserSwitchingDialog();
                ActivityManagerService activityManagerService = this.mService;
                UserSwitchingDialog userSwitchingDialog = new UserSwitchingDialog(activityManagerService, activityManagerService.mContext, userInfo, userInfo2, true, str, str2);
                this.mUserSwitchingDialog = userSwitchingDialog;
                userSwitchingDialog.show();
            }
        }

        public void reportGlobalUsageEvent(int i) {
            this.mService.reportGlobalUsageEvent(i);
        }

        public void reportCurWakefulnessUsageEvent() {
            this.mService.reportCurWakefulnessUsageEvent();
        }

        public void taskSupervisorRemoveUser(int i) {
            this.mService.mAtmInternal.removeUser(i);
        }

        public boolean taskSupervisorSwitchUser(int i, UserState userState) {
            return this.mService.mAtmInternal.switchUser(i, userState);
        }

        public void taskSupervisorResumeFocusedStackTopActivity() {
            this.mService.mAtmInternal.resumeTopActivities(false);
        }

        public void clearAllLockedTasks(String str) {
            this.mService.mAtmInternal.clearLockedTasks(str);
        }

        public boolean isCallerRecents(int i) {
            return this.mService.mAtmInternal.isCallerRecents(i);
        }

        public IStorageManager getStorageManager() {
            return IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        }

        public void dismissKeyguard(final Runnable runnable) {
            final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            final Runnable runnable2 = new Runnable() { // from class: com.android.server.am.UserController$Injector$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserController.Injector.lambda$dismissKeyguard$1(atomicBoolean, runnable);
                }
            };
            this.mHandler.postDelayed(runnable2, 2000L);
            getWindowManager().dismissKeyguard(new IKeyguardDismissCallback.Stub() { // from class: com.android.server.am.UserController.Injector.2
                public void onDismissError() throws RemoteException {
                    Injector.this.mHandler.post(runnable2);
                }

                public void onDismissSucceeded() throws RemoteException {
                    Injector.this.mHandler.post(runnable2);
                }

                public void onDismissCancelled() throws RemoteException {
                    Injector.this.mHandler.post(runnable2);
                }
            }, null);
        }

        public static /* synthetic */ void lambda$dismissKeyguard$1(AtomicBoolean atomicBoolean, Runnable runnable) {
            if (atomicBoolean.getAndSet(false)) {
                runnable.run();
            }
        }

        public boolean isHeadlessSystemUserMode() {
            return UserManager.isHeadlessSystemUserMode();
        }

        public void onUserStarting(int i) {
            getSystemServiceManager().onUserStarting(TimingsTraceAndSlog.newAsyncLog(), i);
        }

        public void onSystemUserVisibilityChanged(boolean z) {
            getUserManagerInternal().onSystemUserVisibilityChanged(z);
        }
    }
}
