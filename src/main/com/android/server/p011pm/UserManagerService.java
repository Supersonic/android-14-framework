package com.android.server.p011pm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.BroadcastOptions;
import android.app.IActivityManager;
import android.app.IStopUserCallback;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.StatsManager;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.content.pm.PackagePartitions;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.UserInfo;
import android.content.pm.UserPackage;
import android.content.pm.UserProperties;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.IUserManager;
import android.os.IUserRestrictionsListener;
import android.os.Message;
import android.os.PackageTagsList;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageManagerInternal;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.service.voice.VoiceInteractionManagerInternal;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.StatsEvent;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsService;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.BundleUtils;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.SystemService;
import com.android.server.p006am.UserState;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.UserTypeFactory;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.storage.DeviceStorageMonitorInternal;
import com.android.server.utils.Slogf;
import com.android.server.utils.TimingsTraceAndSlog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.UserManagerService */
/* loaded from: classes2.dex */
public class UserManagerService extends IUserManager.Stub {
    @VisibleForTesting
    static final int MAX_RECENTLY_REMOVED_IDS_SIZE = 100;
    @VisibleForTesting
    static final int MAX_USER_ID = 21474;
    @VisibleForTesting
    static final int MIN_USER_ID = 10;
    public static UserManagerService sInstance;
    public final String ACTION_DISABLE_QUIET_MODE_AFTER_UNLOCK;
    public ActivityManagerInternal mAmInternal;
    public IAppOpsService mAppOpsService;
    public final Object mAppRestrictionsLock;
    @GuardedBy({"mRestrictionsLock"})
    public final RestrictionsSet mAppliedUserRestrictions;
    @GuardedBy({"mRestrictionsLock"})
    public final RestrictionsSet mBaseUserRestrictions;
    @GuardedBy({"mUsersLock"})
    public int mBootUser;
    @GuardedBy({"mRestrictionsLock"})
    public final RestrictionsSet mCachedEffectiveUserRestrictions;
    public final BroadcastReceiver mConfigurationChangeReceiver;
    public final Context mContext;
    public DevicePolicyManagerInternal mDevicePolicyManagerInternal;
    @GuardedBy({"mRestrictionsLock"})
    public final RestrictionsSet mDevicePolicyUserRestrictions;
    public final BroadcastReceiver mDisableQuietModeCallback;
    @GuardedBy({"mUsersLock"})
    public boolean mForceEphemeralUsers;
    @GuardedBy({"mGuestRestrictions"})
    public final Bundle mGuestRestrictions;
    public final Handler mHandler;
    @GuardedBy({"mUsersLock"})
    public boolean mIsDeviceManaged;
    @GuardedBy({"mUsersLock"})
    public final SparseBooleanArray mIsUserManaged;
    public final Configuration mLastConfiguration;
    public final LocalService mLocalService;
    public final LockPatternUtils mLockPatternUtils;
    @GuardedBy({"mPackagesLock"})
    public int mNextSerialNumber;
    public final AtomicReference<String> mOwnerName;
    public final TypedValue mOwnerNameTypedValue;
    public final Object mPackagesLock;
    public final PackageManagerService mPm;
    public PackageManagerInternal mPmInternal;
    public final IBinder mQuietModeToken;
    @GuardedBy({"mUsersLock"})
    public final LinkedList<Integer> mRecentlyRemovedIds;
    @GuardedBy({"mUsersLock"})
    public final SparseBooleanArray mRemovingUserIds;
    public final Object mRestrictionsLock;
    public final UserSystemPackageInstaller mSystemPackageInstaller;
    public boolean mUpdatingSystemUserMode;
    public final AtomicInteger mUser0Allocations;
    public final UserDataPreparer mUserDataPreparer;
    @GuardedBy({"mUsersLock"})
    public int[] mUserIds;
    @GuardedBy({"mUsersLock"})
    public int[] mUserIdsIncludingPreCreated;
    @GuardedBy({"mUserLifecycleListeners"})
    public final ArrayList<UserManagerInternal.UserLifecycleListener> mUserLifecycleListeners;
    @GuardedBy({"mPackagesLock"})
    public final File mUserListFile;
    public final IBinder mUserRestrictionToken;
    @GuardedBy({"mUserRestrictionsListeners"})
    public final ArrayList<UserManagerInternal.UserRestrictionsListener> mUserRestrictionsListeners;
    @GuardedBy({"mUserStates"})
    public final WatchedUserStates mUserStates;
    public int mUserTypeVersion;
    public final ArrayMap<String, UserTypeDetails> mUserTypes;
    public int mUserVersion;
    public final UserVisibilityMediator mUserVisibilityMediator;
    @GuardedBy({"mUsersLock"})
    public final SparseArray<UserData> mUsers;
    public final File mUsersDir;
    public final Object mUsersLock;
    public static final String USER_INFO_DIR = "system" + File.separator + "users";
    public static final int[] QUIET_MODE_RESTRICTED_APP_OPS = {0, 1, 2, 56, 79, 77, 116, 27, 26};

    public static boolean isAtMostOneFlag(int i) {
        return (i & (i + (-1))) == 0;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.UserManagerService$UserData */
    /* loaded from: classes2.dex */
    public static class UserData {
        public String account;
        public UserInfo info;
        public boolean mIgnorePrepareStorageErrors;
        public long mLastEnteredForegroundTimeMillis;
        public long mLastRequestQuietModeEnabledMillis;
        public boolean persistSeedData;
        public String seedAccountName;
        public PersistableBundle seedAccountOptions;
        public String seedAccountType;
        public long startRealtime;
        public long unlockRealtime;
        public UserProperties userProperties;

        public void setLastRequestQuietModeEnabledMillis(long j) {
            this.mLastRequestQuietModeEnabledMillis = j;
        }

        public long getLastRequestQuietModeEnabledMillis() {
            return this.mLastRequestQuietModeEnabledMillis;
        }

        public boolean getIgnorePrepareStorageErrors() {
            return this.mIgnorePrepareStorageErrors;
        }

        public void setIgnorePrepareStorageErrors() {
            if (Build.VERSION.DEVICE_INITIAL_SDK_INT < 33) {
                this.mIgnorePrepareStorageErrors = true;
            } else {
                Slog.w("UserManagerService", "Not setting mIgnorePrepareStorageErrors to true since this is a new device");
            }
        }

        public void clearSeedAccountData() {
            this.seedAccountName = null;
            this.seedAccountType = null;
            this.seedAccountOptions = null;
            this.persistSeedData = false;
        }
    }

    /* renamed from: com.android.server.pm.UserManagerService$1 */
    /* loaded from: classes2.dex */
    public class C13681 extends BroadcastReceiver {
        public C13681() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK".equals(intent.getAction())) {
                final IntentSender intentSender = (IntentSender) intent.getParcelableExtra("android.intent.extra.INTENT", IntentSender.class);
                final int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", -10000);
                BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.pm.UserManagerService$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserManagerService.C13681.this.lambda$onReceive$0(intExtra, intentSender);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(int i, IntentSender intentSender) {
            UserManagerService.this.setQuietModeEnabled(i, false, intentSender, null);
        }
    }

    /* renamed from: com.android.server.pm.UserManagerService$DisableQuietModeUserUnlockedCallback */
    /* loaded from: classes2.dex */
    public class DisableQuietModeUserUnlockedCallback extends IProgressListener.Stub {
        public final IntentSender mTarget;

        public void onProgress(int i, int i2, Bundle bundle) {
        }

        public void onStarted(int i, Bundle bundle) {
        }

        public DisableQuietModeUserUnlockedCallback(IntentSender intentSender) {
            Objects.requireNonNull(intentSender);
            this.mTarget = intentSender;
        }

        public void onFinished(int i, Bundle bundle) {
            UserManagerService.this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserManagerService$DisableQuietModeUserUnlockedCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserManagerService.DisableQuietModeUserUnlockedCallback.this.lambda$onFinished$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFinished$0() {
            try {
                UserManagerService.this.mContext.startIntentSender(this.mTarget, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Slog.e("UserManagerService", "Failed to start the target in the callback", e);
            }
        }
    }

    /* renamed from: com.android.server.pm.UserManagerService$WatchedUserStates */
    /* loaded from: classes2.dex */
    public class WatchedUserStates {
        public final SparseIntArray states = new SparseIntArray();

        public WatchedUserStates() {
            invalidateIsUserUnlockedCache();
        }

        public int get(int i, int i2) {
            return this.states.indexOfKey(i) >= 0 ? this.states.get(i) : i2;
        }

        public void put(int i, int i2) {
            this.states.put(i, i2);
            invalidateIsUserUnlockedCache();
        }

        public void delete(int i) {
            this.states.delete(i);
            invalidateIsUserUnlockedCache();
        }

        public boolean has(int i) {
            return this.states.get(i, -10000) != -10000;
        }

        public String toString() {
            return this.states.toString();
        }

        public final void invalidateIsUserUnlockedCache() {
            UserManager.invalidateIsUserUnlockedCache();
        }
    }

    public static UserManagerService getInstance() {
        UserManagerService userManagerService;
        synchronized (UserManagerService.class) {
            userManagerService = sInstance;
        }
        return userManagerService;
    }

    /* renamed from: com.android.server.pm.UserManagerService$LifeCycle */
    /* loaded from: classes2.dex */
    public static class LifeCycle extends SystemService {
        public UserManagerService mUms;

        public LifeCycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.pm.UserManagerService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? userManagerService = UserManagerService.getInstance();
            this.mUms = userManagerService;
            publishBinderService("user", userManagerService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 550) {
                this.mUms.cleanupPartialUsers();
                if (this.mUms.mPm.isDeviceUpgrading()) {
                    this.mUms.cleanupPreCreatedUsers();
                }
                this.mUms.registerStatsCallbacks();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            boolean z;
            synchronized (this.mUms.mUsersLock) {
                UserData userDataLU = this.mUms.getUserDataLU(targetUser.getUserIdentifier());
                if (userDataLU != null) {
                    userDataLU.startRealtime = SystemClock.elapsedRealtime();
                    if (targetUser.getUserIdentifier() == 0 && targetUser.isFull()) {
                        this.mUms.setLastEnteredForegroundTimeToNow(userDataLU);
                    } else {
                        z = userDataLU.info.isManagedProfile() && userDataLU.info.isQuietModeEnabled();
                    }
                }
            }
            if (z) {
                this.mUms.setAppOpsRestrictedForQuietMode(targetUser.getUserIdentifier(), true);
            }
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            synchronized (this.mUms.mUsersLock) {
                UserData userDataLU = this.mUms.getUserDataLU(targetUser.getUserIdentifier());
                if (userDataLU != null) {
                    userDataLU.unlockRealtime = SystemClock.elapsedRealtime();
                }
            }
        }

        @Override // com.android.server.SystemService
        public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
            synchronized (this.mUms.mUsersLock) {
                UserData userDataLU = this.mUms.getUserDataLU(targetUser2.getUserIdentifier());
                if (userDataLU != null) {
                    this.mUms.setLastEnteredForegroundTimeToNow(userDataLU);
                }
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            synchronized (this.mUms.mUsersLock) {
                UserData userDataLU = this.mUms.getUserDataLU(targetUser.getUserIdentifier());
                if (userDataLU != null) {
                    userDataLU.startRealtime = 0L;
                    userDataLU.unlockRealtime = 0L;
                }
            }
        }
    }

    @VisibleForTesting
    public UserManagerService(Context context) {
        this(context, null, null, new Object(), context.getCacheDir(), null);
    }

    public UserManagerService(Context context, PackageManagerService packageManagerService, UserDataPreparer userDataPreparer, Object obj) {
        this(context, packageManagerService, userDataPreparer, obj, Environment.getDataDirectory(), null);
    }

    @VisibleForTesting
    public UserManagerService(Context context, PackageManagerService packageManagerService, UserDataPreparer userDataPreparer, Object obj, File file, SparseArray<UserData> sparseArray) {
        this.mUsersLock = LockGuard.installNewLock(2);
        this.mRestrictionsLock = new Object();
        this.mAppRestrictionsLock = new Object();
        this.mUserRestrictionToken = new Binder();
        this.mQuietModeToken = new Binder();
        this.mBaseUserRestrictions = new RestrictionsSet();
        this.mCachedEffectiveUserRestrictions = new RestrictionsSet();
        this.mAppliedUserRestrictions = new RestrictionsSet();
        this.mDevicePolicyUserRestrictions = new RestrictionsSet();
        this.mGuestRestrictions = new Bundle();
        this.mRemovingUserIds = new SparseBooleanArray();
        this.mRecentlyRemovedIds = new LinkedList<>();
        this.mUserVersion = 0;
        this.mUserTypeVersion = 0;
        this.mIsUserManaged = new SparseBooleanArray();
        this.mUserRestrictionsListeners = new ArrayList<>();
        this.mUserLifecycleListeners = new ArrayList<>();
        this.ACTION_DISABLE_QUIET_MODE_AFTER_UNLOCK = "com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK";
        this.mDisableQuietModeCallback = new C13681();
        this.mOwnerName = new AtomicReference<>();
        this.mOwnerNameTypedValue = new TypedValue();
        this.mLastConfiguration = new Configuration();
        this.mConfigurationChangeReceiver = new BroadcastReceiver() { // from class: com.android.server.pm.UserManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.CONFIGURATION_CHANGED".equals(intent.getAction())) {
                    UserManagerService.this.invalidateOwnerNameIfNecessary(context2.getResources(), false);
                }
            }
        };
        WatchedUserStates watchedUserStates = new WatchedUserStates();
        this.mUserStates = watchedUserStates;
        this.mBootUser = -10000;
        this.mContext = context;
        this.mPm = packageManagerService;
        this.mPackagesLock = obj;
        this.mUsers = sparseArray == null ? new SparseArray<>() : sparseArray;
        MainHandler mainHandler = new MainHandler();
        this.mHandler = mainHandler;
        this.mUserVisibilityMediator = new UserVisibilityMediator(mainHandler);
        this.mUserDataPreparer = userDataPreparer;
        ArrayMap<String, UserTypeDetails> userTypes = UserTypeFactory.getUserTypes();
        this.mUserTypes = userTypes;
        invalidateOwnerNameIfNecessary(context.getResources(), true);
        synchronized (obj) {
            File file2 = new File(file, USER_INFO_DIR);
            this.mUsersDir = file2;
            file2.mkdirs();
            new File(file2, String.valueOf(0)).mkdirs();
            FileUtils.setPermissions(file2.toString(), 509, -1, -1);
            this.mUserListFile = new File(file2, "userlist.xml");
            initDefaultGuestRestrictions();
            readUserListLP();
            sInstance = this;
        }
        this.mSystemPackageInstaller = new UserSystemPackageInstaller(this, userTypes);
        LocalService localService = new LocalService();
        this.mLocalService = localService;
        LocalServices.addService(UserManagerInternal.class, localService);
        this.mLockPatternUtils = new LockPatternUtils(context);
        watchedUserStates.put(0, 0);
        this.mUser0Allocations = null;
        emulateSystemUserModeIfNeeded();
    }

    public void systemReady() {
        this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        synchronized (this.mRestrictionsLock) {
            applyUserRestrictionsLR(0);
        }
        this.mContext.registerReceiver(this.mDisableQuietModeCallback, new IntentFilter("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK"), null, this.mHandler);
        this.mContext.registerReceiver(this.mConfigurationChangeReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"), null, this.mHandler);
        markEphemeralUsersForRemoval();
    }

    public UserManagerInternal getInternalForInjectorOnly() {
        return this.mLocalService;
    }

    public final void markEphemeralUsersForRemoval() {
        int i;
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if (userInfo.isEphemeral() && !userInfo.preCreated && (i = userInfo.id) != 0) {
                    addRemovingUserIdLocked(i);
                    userInfo.partial = true;
                    userInfo.flags |= 64;
                }
            }
        }
    }

    public final void cleanupPartialUsers() {
        int i;
        ArrayList arrayList = new ArrayList();
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if ((userInfo.partial || userInfo.guestToRemove) && userInfo.id != 0) {
                    arrayList.add(userInfo);
                    if (!this.mRemovingUserIds.get(userInfo.id)) {
                        addRemovingUserIdLocked(userInfo.id);
                    }
                    userInfo.partial = true;
                }
            }
        }
        int size2 = arrayList.size();
        for (i = 0; i < size2; i++) {
            UserInfo userInfo2 = (UserInfo) arrayList.get(i);
            Slog.w("UserManagerService", "Removing partially created user " + userInfo2.id + " (name=" + userInfo2.name + ")");
            removeUserState(userInfo2.id);
        }
    }

    public final void cleanupPreCreatedUsers() {
        ArrayList arrayList;
        int i;
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            arrayList = new ArrayList(size);
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if (userInfo.preCreated) {
                    arrayList.add(userInfo);
                    addRemovingUserIdLocked(userInfo.id);
                    userInfo.flags |= 64;
                    userInfo.partial = true;
                }
            }
        }
        int size2 = arrayList.size();
        for (i = 0; i < size2; i++) {
            UserInfo userInfo2 = (UserInfo) arrayList.get(i);
            Slog.i("UserManagerService", "Removing pre-created user " + userInfo2.id);
            removeUserState(userInfo2.id);
        }
    }

    public String getUserAccount(int i) {
        String str;
        checkManageUserAndAcrossUsersFullPermission("get user account");
        synchronized (this.mUsersLock) {
            str = this.mUsers.get(i).account;
        }
        return str;
    }

    public void setUserAccount(int i, String str) {
        checkManageUserAndAcrossUsersFullPermission("set user account");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userData = this.mUsers.get(i);
                if (userData == null) {
                    Slog.e("UserManagerService", "User not found for setting user account: u" + i);
                    return;
                }
                if (Objects.equals(userData.account, str)) {
                    userData = null;
                } else {
                    userData.account = str;
                }
                if (userData != null) {
                    writeUserLP(userData);
                }
            }
        }
    }

    public UserInfo getPrimaryUser() {
        checkManageUsersPermission("query users");
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = this.mUsers.valueAt(i).info;
                if (userInfo.isPrimary() && !this.mRemovingUserIds.get(userInfo.id)) {
                    return userInfo;
                }
            }
            return null;
        }
    }

    public int getMainUserId() {
        checkQueryOrCreateUsersPermission("get main user id");
        return getMainUserIdUnchecked();
    }

    public final int getMainUserIdUnchecked() {
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = this.mUsers.valueAt(i).info;
                if (userInfo.isMain() && !this.mRemovingUserIds.get(userInfo.id)) {
                    return userInfo.id;
                }
            }
            return -10000;
        }
    }

    public void setBootUser(int i) {
        checkCreateUsersPermission("Set boot user");
        synchronized (this.mUsersLock) {
            Slogf.m20i("UserManagerService", "setBootUser %d", Integer.valueOf(i));
            this.mBootUser = i;
        }
    }

    public int getBootUser() {
        checkCreateUsersPermission("Get boot user");
        try {
            return this.mLocalService.getBootUser();
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public int getPreviousFullUserToEnterForeground() {
        int i;
        checkQueryOrCreateUsersPermission("get previous user");
        int currentUserId = getCurrentUserId();
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            i = -10000;
            long j = 0;
            for (int i2 = 0; i2 < size; i2++) {
                UserData valueAt = this.mUsers.valueAt(i2);
                UserInfo userInfo = valueAt.info;
                int i3 = userInfo.id;
                if (i3 != currentUserId && userInfo.isFull() && !valueAt.info.partial && !this.mRemovingUserIds.get(i3)) {
                    long j2 = valueAt.mLastEnteredForegroundTimeMillis;
                    if (j2 > j) {
                        j = j2;
                        i = i3;
                    }
                }
            }
        }
        return i;
    }

    public List<UserInfo> getUsers(boolean z) {
        return getUsers(true, z, true);
    }

    public List<UserInfo> getUsers(boolean z, boolean z2, boolean z3) {
        checkCreateUsersPermission("query users");
        return getUsersInternal(z, z2, z3);
    }

    public final List<UserInfo> getUsersInternal(boolean z, boolean z2, boolean z3) {
        ArrayList arrayList;
        synchronized (this.mUsersLock) {
            arrayList = new ArrayList(this.mUsers.size());
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = this.mUsers.valueAt(i).info;
                if ((!z || !userInfo.partial) && ((!z2 || !this.mRemovingUserIds.get(userInfo.id)) && (!z3 || !userInfo.preCreated))) {
                    arrayList.add(userWithName(userInfo));
                }
            }
        }
        return arrayList;
    }

    public List<UserInfo> getProfiles(int i, boolean z) {
        boolean hasCreateUsersPermission;
        List<UserInfo> profilesLU;
        if (i != UserHandle.getCallingUserId()) {
            checkQueryOrCreateUsersPermission("getting profiles related to user " + i);
            hasCreateUsersPermission = true;
        } else {
            hasCreateUsersPermission = hasCreateUsersPermission();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUsersLock) {
                profilesLU = getProfilesLU(i, null, z, hasCreateUsersPermission);
            }
            return profilesLU;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int[] getProfileIds(int i, boolean z) {
        return getProfileIds(i, null, z);
    }

    public int[] getProfileIds(int i, String str, boolean z) {
        int[] array;
        if (i != UserHandle.getCallingUserId()) {
            checkQueryOrCreateUsersPermission("getting profiles related to user " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUsersLock) {
                array = getProfileIdsLU(i, str, z).toArray();
            }
            return array;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mUsersLock"})
    public final List<UserInfo> getProfilesLU(int i, String str, boolean z, boolean z2) {
        UserInfo userWithName;
        IntArray profileIdsLU = getProfileIdsLU(i, str, z);
        ArrayList arrayList = new ArrayList(profileIdsLU.size());
        for (int i2 = 0; i2 < profileIdsLU.size(); i2++) {
            UserInfo userInfo = this.mUsers.get(profileIdsLU.get(i2)).info;
            if (!z2) {
                userWithName = new UserInfo(userInfo);
                userWithName.name = null;
                userWithName.iconPath = null;
            } else {
                userWithName = userWithName(userInfo);
            }
            arrayList.add(userWithName);
        }
        return arrayList;
    }

    @GuardedBy({"mUsersLock"})
    public final IntArray getProfileIdsLU(int i, String str, boolean z) {
        UserInfo userInfoLU = getUserInfoLU(i);
        IntArray intArray = new IntArray(this.mUsers.size());
        if (userInfoLU == null) {
            return intArray;
        }
        int size = this.mUsers.size();
        for (int i2 = 0; i2 < size; i2++) {
            UserInfo userInfo = this.mUsers.valueAt(i2).info;
            if (isProfileOf(userInfoLU, userInfo) && ((!z || userInfo.isEnabled()) && !this.mRemovingUserIds.get(userInfo.id) && !userInfo.partial && (str == null || str.equals(userInfo.userType)))) {
                intArray.add(userInfo.id);
            }
        }
        return intArray;
    }

    public int getCredentialOwnerProfile(int i) {
        checkManageUsersPermission("get the credential owner");
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
            synchronized (this.mUsersLock) {
                UserInfo profileParentLU = getProfileParentLU(i);
                if (profileParentLU != null) {
                    return profileParentLU.id;
                }
            }
        }
        return i;
    }

    public boolean isSameProfileGroup(int i, int i2) {
        if (i == i2) {
            return true;
        }
        checkQueryUsersPermission("check if in the same profile group");
        return isSameProfileGroupNoChecks(i, i2);
    }

    public final boolean isSameProfileGroupNoChecks(int i, int i2) {
        int i3;
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            if (userInfoLU != null && userInfoLU.profileGroupId != -10000) {
                UserInfo userInfoLU2 = getUserInfoLU(i2);
                if (userInfoLU2 != null && (i3 = userInfoLU2.profileGroupId) != -10000) {
                    return userInfoLU.profileGroupId == i3;
                }
                return false;
            }
            return false;
        }
    }

    public UserInfo getProfileParent(int i) {
        UserInfo profileParentLU;
        if (!hasManageUsersOrPermission("android.permission.INTERACT_ACROSS_USERS")) {
            throw new SecurityException("You need MANAGE_USERS or INTERACT_ACROSS_USERS permission to get the profile parent");
        }
        synchronized (this.mUsersLock) {
            profileParentLU = getProfileParentLU(i);
        }
        return profileParentLU;
    }

    public int getProfileParentId(int i) {
        checkManageUsersPermission("get the profile parent");
        return getProfileParentIdUnchecked(i);
    }

    public final int getProfileParentIdUnchecked(int i) {
        synchronized (this.mUsersLock) {
            UserInfo profileParentLU = getProfileParentLU(i);
            if (profileParentLU == null) {
                return i;
            }
            return profileParentLU.id;
        }
    }

    @GuardedBy({"mUsersLock"})
    public final UserInfo getProfileParentLU(int i) {
        int i2;
        UserInfo userInfoLU = getUserInfoLU(i);
        if (userInfoLU == null || (i2 = userInfoLU.profileGroupId) == i || i2 == -10000) {
            return null;
        }
        return getUserInfoLU(i2);
    }

    public static boolean isProfileOf(UserInfo userInfo, UserInfo userInfo2) {
        int i;
        return userInfo.id == userInfo2.id || ((i = userInfo.profileGroupId) != -10000 && i == userInfo2.profileGroupId);
    }

    public final void broadcastProfileAvailabilityChanges(UserHandle userHandle, UserHandle userHandle2, boolean z) {
        Intent intent = new Intent();
        if (z) {
            intent.setAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        } else {
            intent.setAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        }
        intent.putExtra("android.intent.extra.QUIET_MODE", z);
        intent.putExtra("android.intent.extra.USER", userHandle);
        intent.putExtra("android.intent.extra.user_handle", userHandle.getIdentifier());
        getDevicePolicyManagerInternal().broadcastIntentToManifestReceivers(intent, userHandle2, true);
        intent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(intent, userHandle2);
    }

    public boolean requestQuietModeEnabled(String str, boolean z, int i, IntentSender intentSender, int i2) {
        Objects.requireNonNull(str);
        if (!z || intentSender == null) {
            boolean z2 = (i2 & 2) != 0;
            boolean z3 = (i2 & 1) != 0;
            if (z2 && z3) {
                throw new IllegalArgumentException("invalid flags: " + i2);
            }
            ensureCanModifyQuietMode(str, Binder.getCallingUid(), i, intentSender != null, z2);
            if (z3 && str.equals(getPackageManagerInternal().getSystemUiServiceComponent().getPackageName())) {
                throw new SecurityException("SystemUI is not allowed to set QUIET_MODE_DISABLE_ONLY_IF_CREDENTIAL_NOT_REQUIRED");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (z) {
                    setQuietModeEnabled(i, true, intentSender, str);
                    return true;
                }
                boolean isManagedProfileWithUnifiedChallenge = this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(i);
                if (isManagedProfileWithUnifiedChallenge && (!((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isDeviceLocked(this.mLocalService.getProfileParentId(i)) || z3)) {
                    this.mLockPatternUtils.tryUnlockWithCachedUnifiedChallenge(i);
                }
                if (!((z2 || !this.mLockPatternUtils.isSecure(i) || (isManagedProfileWithUnifiedChallenge && StorageManager.isUserKeyUnlocked(i))) ? false : true)) {
                    setQuietModeEnabled(i, false, intentSender, str);
                    return true;
                } else if (z3) {
                    return false;
                } else {
                    showConfirmCredentialToDisableQuietMode(i, intentSender);
                    return false;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        throw new IllegalArgumentException("target should only be specified when we are disabling quiet mode.");
    }

    public final void ensureCanModifyQuietMode(String str, int i, int i2, boolean z, boolean z2) {
        verifyCallingPackage(str, i);
        if (hasManageUsersPermission()) {
            return;
        }
        if (z) {
            throw new SecurityException("MANAGE_USERS permission is required to start intent after disabling quiet mode.");
        }
        if (z2) {
            throw new SecurityException("MANAGE_USERS permission is required to disable quiet mode without credentials.");
        }
        if (!isSameProfileGroupNoChecks(UserHandle.getUserId(i), i2)) {
            throw new SecurityException("MANAGE_USERS permission is required to modify quiet mode for a different profile group.");
        }
        if (hasPermissionGranted("android.permission.MODIFY_QUIET_MODE", i)) {
            return;
        }
        ShortcutServiceInternal shortcutServiceInternal = (ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class);
        if (shortcutServiceInternal == null || !shortcutServiceInternal.isForegroundDefaultLauncher(str, i)) {
            throw new SecurityException("Can't modify quiet mode, caller is neither foreground default launcher nor has MANAGE_USERS/MODIFY_QUIET_MODE permission");
        }
    }

    public final void setQuietModeEnabled(int i, boolean z, IntentSender intentSender, String str) {
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            UserInfo profileParentLU = getProfileParentLU(i);
            if (userInfoLU == null || !userInfoLU.isManagedProfile()) {
                throw new IllegalArgumentException("User " + i + " is not a profile");
            } else if (userInfoLU.isQuietModeEnabled() == z) {
                Slog.i("UserManagerService", "Quiet mode is already " + z);
            } else {
                userInfoLU.flags ^= 128;
                UserData userDataLU = getUserDataLU(userInfoLU.id);
                synchronized (this.mPackagesLock) {
                    writeUserLP(userDataLU);
                }
                if (getDevicePolicyManagerInternal().isKeepProfilesRunningEnabled()) {
                    getPackageManagerInternal().setPackagesSuspendedForQuietMode(i, z);
                    setAppOpsRestrictedForQuietMode(i, z);
                    if (z && !this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(i)) {
                        ((TrustManager) this.mContext.getSystemService(TrustManager.class)).setDeviceLockedForUser(i, true);
                    }
                    if (!z && intentSender != null) {
                        try {
                            this.mContext.startIntentSender(intentSender, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Slog.e("UserManagerService", "Failed to start intent after disabling quiet mode", e);
                        }
                    }
                } else {
                    try {
                        if (z) {
                            ActivityManager.getService().stopUser(i, true, (IStopUserCallback) null);
                            ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).killForegroundAppsForUser(i);
                        } else {
                            ActivityManager.getService().startProfileWithListener(i, intentSender != null ? new DisableQuietModeUserUnlockedCallback(intentSender) : null);
                        }
                    } catch (RemoteException e2) {
                        e2.rethrowAsRuntimeException();
                    }
                }
                logQuietModeEnabled(i, z, str);
                broadcastProfileAvailabilityChanges(userInfoLU.getUserHandle(), profileParentLU.getUserHandle(), z);
            }
        }
    }

    public final void setAppOpsRestrictedForQuietMode(int i, boolean z) {
        for (int i2 : QUIET_MODE_RESTRICTED_APP_OPS) {
            try {
                this.mAppOpsService.setUserRestriction(i2, z, this.mQuietModeToken, i, (PackageTagsList) null);
            } catch (RemoteException e) {
                Slog.w("UserManagerService", "Unable to limit app ops", e);
            }
        }
    }

    public final void logQuietModeEnabled(int i, boolean z, String str) {
        UserData userDataLU;
        long j;
        Slogf.m20i("UserManagerService", "requestQuietModeEnabled called by package %s, with enableQuietMode %b.", str, Boolean.valueOf(z));
        synchronized (this.mUsersLock) {
            userDataLU = getUserDataLU(i);
        }
        if (userDataLU == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (userDataLU.getLastRequestQuietModeEnabledMillis() != 0) {
            j = userDataLU.getLastRequestQuietModeEnabledMillis();
        } else {
            j = userDataLU.info.creationTime;
        }
        DevicePolicyEventLogger.createEvent(55).setStrings(new String[]{str}).setBoolean(z).setTimePeriod(currentTimeMillis - j).write();
        userDataLU.setLastRequestQuietModeEnabledMillis(currentTimeMillis);
    }

    public boolean isQuietModeEnabled(int i) {
        UserInfo userInfoLU;
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                userInfoLU = getUserInfoLU(i);
            }
            if (userInfoLU != null && userInfoLU.isManagedProfile()) {
                return userInfoLU.isQuietModeEnabled();
            }
            return false;
        }
    }

    public final void showConfirmCredentialToDisableQuietMode(int i, IntentSender intentSender) {
        Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, i);
        if (createConfirmDeviceCredentialIntent == null) {
            return;
        }
        Intent intent = new Intent("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK");
        if (intentSender != null) {
            intent.putExtra("android.intent.extra.INTENT", intentSender);
        }
        intent.putExtra("android.intent.extra.USER_ID", i);
        intent.setPackage(this.mContext.getPackageName());
        intent.addFlags(268435456);
        createConfirmDeviceCredentialIntent.putExtra("android.intent.extra.INTENT", PendingIntent.getBroadcast(this.mContext, 0, intent, 1409286144).getIntentSender());
        createConfirmDeviceCredentialIntent.setFlags(276824064);
        this.mContext.startActivity(createConfirmDeviceCredentialIntent);
    }

    public void setUserEnabled(int i) {
        UserInfo userInfoLU;
        boolean z;
        checkManageUsersPermission("enable user");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                userInfoLU = getUserInfoLU(i);
                if (userInfoLU == null || userInfoLU.isEnabled()) {
                    z = false;
                } else {
                    userInfoLU.flags ^= 64;
                    writeUserLP(getUserDataLU(userInfoLU.id));
                    z = true;
                }
            }
        }
        if (z && userInfoLU != null && userInfoLU.isProfile()) {
            sendProfileAddedBroadcast(userInfoLU.profileGroupId, userInfoLU.id);
        }
    }

    public void setUserAdmin(int i) {
        UserInfo userInfoLU;
        checkManageUserAndAcrossUsersFullPermission("set user admin");
        long logGrantAdminJourneyBegin = logGrantAdminJourneyBegin(i);
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                userInfoLU = getUserInfoLU(i);
            }
            if (userInfoLU != null && !userInfoLU.isAdmin()) {
                userInfoLU.flags ^= 2;
                writeUserLP(getUserDataLU(userInfoLU.id));
                logGrantAdminJourneyFinish(logGrantAdminJourneyBegin, i, userInfoLU.userType, userInfoLU.flags);
                return;
            }
            logUserJourneyError(logGrantAdminJourneyBegin, 7, i);
        }
    }

    public void revokeUserAdmin(int i) {
        checkManageUserAndAcrossUsersFullPermission("revoke admin privileges");
        long logRevokeAdminJourneyBegin = logRevokeAdminJourneyBegin(i);
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userDataLU = getUserDataLU(i);
                if (userDataLU != null && userDataLU.info.isAdmin()) {
                    userDataLU.info.flags ^= 2;
                    writeUserLP(userDataLU);
                    UserInfo userInfo = userDataLU.info;
                    logRevokeAdminJourneyFinish(logRevokeAdminJourneyBegin, i, userInfo.userType, userInfo.flags);
                    return;
                }
                logUserJourneyError(logRevokeAdminJourneyBegin, 8, i);
            }
        }
    }

    public void evictCredentialEncryptionKey(int i) {
        checkManageUsersPermission("evict CE key");
        IActivityManager iActivityManager = ActivityManagerNative.getDefault();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                iActivityManager.restartUserInBackground(i);
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isUserOfType(int i, String str) {
        checkQueryOrCreateUsersPermission("check user type");
        return str != null && str.equals(getUserTypeNoChecks(i));
    }

    public final String getUserTypeNoChecks(int i) {
        String str;
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            str = userInfoLU != null ? userInfoLU.userType : null;
        }
        return str;
    }

    public final UserTypeDetails getUserTypeDetailsNoChecks(int i) {
        String userTypeNoChecks = getUserTypeNoChecks(i);
        if (userTypeNoChecks != null) {
            return this.mUserTypes.get(userTypeNoChecks);
        }
        return null;
    }

    public final UserTypeDetails getUserTypeDetails(UserInfo userInfo) {
        String str = userInfo != null ? userInfo.userType : null;
        if (str != null) {
            return this.mUserTypes.get(str);
        }
        return null;
    }

    public UserInfo getUserInfo(int i) {
        UserInfo userWithName;
        checkQueryOrCreateUsersPermission("query user");
        synchronized (this.mUsersLock) {
            userWithName = userWithName(getUserInfoLU(i));
        }
        return userWithName;
    }

    public final UserInfo userWithName(UserInfo userInfo) {
        String guestName;
        if (userInfo != null && userInfo.name == null) {
            if (userInfo.id == 0) {
                guestName = getOwnerName();
            } else if (userInfo.isMain()) {
                guestName = getOwnerName();
            } else {
                guestName = userInfo.isGuest() ? getGuestName() : null;
            }
            if (guestName != null) {
                UserInfo userInfo2 = new UserInfo(userInfo);
                userInfo2.name = guestName;
                return userInfo2;
            }
        }
        return userInfo;
    }

    public boolean isUserTypeSubtypeOfFull(String str) {
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        return userTypeDetails != null && userTypeDetails.isFull();
    }

    public boolean isUserTypeSubtypeOfProfile(String str) {
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        return userTypeDetails != null && userTypeDetails.isProfile();
    }

    public boolean isUserTypeSubtypeOfSystem(String str) {
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        return userTypeDetails != null && userTypeDetails.isSystem();
    }

    public UserProperties getUserPropertiesCopy(int i) {
        checkQueryOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserProperties");
        UserProperties userPropertiesInternal = getUserPropertiesInternal(i);
        if (userPropertiesInternal != null) {
            return new UserProperties(userPropertiesInternal, Binder.getCallingUid() == 1000, hasManageUsersPermission(), hasQueryUsersPermission());
        }
        throw new IllegalArgumentException("Cannot access properties for user " + i);
    }

    public final UserProperties getUserPropertiesInternal(int i) {
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(i);
            if (userDataLU != null) {
                return userDataLU.userProperties;
            }
            return null;
        }
    }

    public boolean hasBadge(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "hasBadge");
        UserTypeDetails userTypeDetailsNoChecks = getUserTypeDetailsNoChecks(i);
        return userTypeDetailsNoChecks != null && userTypeDetailsNoChecks.hasBadge();
    }

    public int getUserBadgeLabelResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserBadgeLabelResId");
        UserInfo userInfoNoChecks = getUserInfoNoChecks(i);
        UserTypeDetails userTypeDetails = getUserTypeDetails(userInfoNoChecks);
        if (userInfoNoChecks == null || userTypeDetails == null || !userTypeDetails.hasBadge()) {
            Slog.e("UserManagerService", "Requested badge label for non-badged user " + i);
            return 0;
        }
        return userTypeDetails.getBadgeLabel(userInfoNoChecks.profileBadge);
    }

    public int getUserBadgeColorResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserBadgeColorResId");
        UserInfo userInfoNoChecks = getUserInfoNoChecks(i);
        UserTypeDetails userTypeDetails = getUserTypeDetails(userInfoNoChecks);
        if (userInfoNoChecks == null || userTypeDetails == null || !userTypeDetails.hasBadge()) {
            Slog.e("UserManagerService", "Requested badge dark color for non-badged user " + i);
            return 0;
        }
        return userTypeDetails.getBadgeColor(userInfoNoChecks.profileBadge);
    }

    public int getUserBadgeDarkColorResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserBadgeDarkColorResId");
        UserInfo userInfoNoChecks = getUserInfoNoChecks(i);
        UserTypeDetails userTypeDetails = getUserTypeDetails(userInfoNoChecks);
        if (userInfoNoChecks == null || userTypeDetails == null || !userTypeDetails.hasBadge()) {
            Slog.e("UserManagerService", "Requested badge color for non-badged user " + i);
            return 0;
        }
        return userTypeDetails.getDarkThemeBadgeColor(userInfoNoChecks.profileBadge);
    }

    public int getUserIconBadgeResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserIconBadgeResId");
        UserTypeDetails userTypeDetailsNoChecks = getUserTypeDetailsNoChecks(i);
        if (userTypeDetailsNoChecks == null || !userTypeDetailsNoChecks.hasBadge()) {
            Slog.e("UserManagerService", "Requested icon badge for non-badged user " + i);
            return 0;
        }
        return userTypeDetailsNoChecks.getIconBadge();
    }

    public int getUserBadgeResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserBadgeResId");
        UserTypeDetails userTypeDetailsNoChecks = getUserTypeDetailsNoChecks(i);
        if (userTypeDetailsNoChecks == null || !userTypeDetailsNoChecks.hasBadge()) {
            Slog.e("UserManagerService", "Requested badge for non-badged user " + i);
            return 0;
        }
        return userTypeDetailsNoChecks.getBadgePlain();
    }

    public int getUserBadgeNoBackgroundResId(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserBadgeNoBackgroundResId");
        UserTypeDetails userTypeDetailsNoChecks = getUserTypeDetailsNoChecks(i);
        if (userTypeDetailsNoChecks == null || !userTypeDetailsNoChecks.hasBadge()) {
            Slog.e("UserManagerService", "Requested badge (no background) for non-badged user " + i);
            return 0;
        }
        return userTypeDetailsNoChecks.getBadgeNoBackground();
    }

    public boolean isProfile(int i) {
        checkQueryOrInteractPermissionIfCallerInOtherProfileGroup(i, "isProfile");
        return isProfileUnchecked(i);
    }

    public final boolean isProfileUnchecked(int i) {
        boolean z;
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            z = userInfoLU != null && userInfoLU.isProfile();
        }
        return z;
    }

    public String getProfileType(int i) {
        checkQueryOrInteractPermissionIfCallerInOtherProfileGroup(i, "getProfileType");
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            if (userInfoLU != null) {
                return userInfoLU.isProfile() ? userInfoLU.userType : "";
            }
            return null;
        }
    }

    public boolean isUserUnlockingOrUnlocked(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "isUserUnlockingOrUnlocked");
        return this.mLocalService.isUserUnlockingOrUnlocked(i);
    }

    public boolean isUserUnlocked(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "isUserUnlocked");
        return this.mLocalService.isUserUnlocked(i);
    }

    public boolean isUserRunning(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "isUserRunning");
        return this.mLocalService.isUserRunning(i);
    }

    public boolean isUserForeground(int i) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId == i || hasManageUsersOrPermission("android.permission.INTERACT_ACROSS_USERS")) {
            return i == getCurrentUserId();
        }
        throw new SecurityException("Caller from user " + callingUserId + " needs MANAGE_USERS or INTERACT_ACROSS_USERS permission to check if another user (" + i + ") is running in the foreground");
    }

    public boolean isUserVisible(int i) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId != i && !hasManageUsersOrPermission("android.permission.INTERACT_ACROSS_USERS")) {
            throw new SecurityException("Caller from user " + callingUserId + " needs MANAGE_USERS or INTERACT_ACROSS_USERS permission to check if another user (" + i + ") is visible");
        }
        return this.mUserVisibilityMediator.isUserVisible(i);
    }

    @VisibleForTesting
    public Pair<Integer, Integer> getCurrentAndTargetUserIds() {
        ActivityManagerInternal activityManagerInternal = getActivityManagerInternal();
        if (activityManagerInternal == null) {
            Slog.w("UserManagerService", "getCurrentAndTargetUserId() called too early, ActivityManagerInternal is not set yet");
            return new Pair<>(-10000, -10000);
        }
        return activityManagerInternal.getCurrentAndTargetUserIds();
    }

    @VisibleForTesting
    public int getCurrentUserId() {
        ActivityManagerInternal activityManagerInternal = getActivityManagerInternal();
        if (activityManagerInternal == null) {
            Slog.w("UserManagerService", "getCurrentUserId() called too early, ActivityManagerInternal is not set yet");
            return -10000;
        }
        return activityManagerInternal.getCurrentUserId();
    }

    @VisibleForTesting
    public boolean isCurrentUserOrRunningProfileOfCurrentUser(int i) {
        int currentUserId = getCurrentUserId();
        if (currentUserId == i) {
            return true;
        }
        if (isProfileUnchecked(i) && getProfileParentIdUnchecked(i) == currentUserId) {
            return isUserRunning(i);
        }
        return false;
    }

    public boolean isUserVisibleOnDisplay(int i, int i2) {
        return this.mUserVisibilityMediator.isUserVisible(i, i2);
    }

    public int[] getVisibleUsers() {
        if (!hasManageUsersOrPermission("android.permission.INTERACT_ACROSS_USERS")) {
            throw new SecurityException("Caller needs MANAGE_USERS or INTERACT_ACROSS_USERS permission to get list of visible users");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mUserVisibilityMediator.getVisibleUsers().toArray();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getDisplayIdAssignedToUser() {
        return this.mUserVisibilityMediator.getDisplayAssignedToUser(UserHandle.getUserId(Binder.getCallingUid()));
    }

    public String getUserName() {
        String str;
        int callingUid = Binder.getCallingUid();
        if (!hasQueryOrCreateUsersPermission() && !hasPermissionGranted("android.permission.GET_ACCOUNTS_PRIVILEGED", callingUid)) {
            throw new SecurityException("You need MANAGE_USERS, CREATE_USERS, QUERY_USERS, or GET_ACCOUNTS_PRIVILEGED permissions to: get user name");
        }
        int userId = UserHandle.getUserId(callingUid);
        synchronized (this.mUsersLock) {
            UserInfo userWithName = userWithName(getUserInfoLU(userId));
            return (userWithName == null || (str = userWithName.name) == null) ? "" : str;
        }
    }

    public long getUserStartRealtime() {
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(userId);
            if (userDataLU != null) {
                return userDataLU.startRealtime;
            }
            return 0L;
        }
    }

    public long getUserUnlockRealtime() {
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(UserHandle.getUserId(Binder.getCallingUid()));
            if (userDataLU != null) {
                return userDataLU.unlockRealtime;
            }
            return 0L;
        }
    }

    public final void checkManageOrInteractPermissionIfCallerInOtherProfileGroup(int i, String str) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId == i || isSameProfileGroupNoChecks(callingUserId, i) || hasManageUsersPermission() || hasPermissionGranted("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingUid())) {
            return;
        }
        throw new SecurityException("You need INTERACT_ACROSS_USERS or MANAGE_USERS permission to: check " + str);
    }

    public final void checkQueryOrInteractPermissionIfCallerInOtherProfileGroup(int i, String str) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId == i || isSameProfileGroupNoChecks(callingUserId, i) || hasQueryUsersPermission() || hasPermissionGranted("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingUid())) {
            return;
        }
        throw new SecurityException("You need INTERACT_ACROSS_USERS, MANAGE_USERS, or QUERY_USERS permission to: check " + str);
    }

    public final void checkQueryOrCreateUsersPermissionIfCallerInOtherProfileGroup(int i, String str) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId == i || isSameProfileGroupNoChecks(callingUserId, i)) {
            return;
        }
        checkQueryOrCreateUsersPermission(str);
    }

    public boolean isDemoUser(int i) {
        boolean z;
        if (UserHandle.getCallingUserId() != i && !hasManageUsersPermission()) {
            throw new SecurityException("You need MANAGE_USERS permission to query if u=" + i + " is a demo user");
        }
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            z = userInfoLU != null && userInfoLU.isDemo();
        }
        return z;
    }

    public boolean isAdminUser(int i) {
        boolean z;
        checkQueryOrCreateUsersPermissionIfCallerInOtherProfileGroup(i, "isAdminUser");
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            z = userInfoLU != null && userInfoLU.isAdmin();
        }
        return z;
    }

    public boolean isPreCreated(int i) {
        boolean z;
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "isPreCreated");
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            z = userInfoLU != null && userInfoLU.preCreated;
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x005e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int getUserSwitchability(int i) {
        int i2;
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserSwitchability");
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("getUserSwitchability-" + i);
        timingsTraceAndSlog.traceBegin("TM.isInCall");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
            if (telecomManager != null) {
                if (telecomManager.isInCall()) {
                    i2 = 1;
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    timingsTraceAndSlog.traceEnd();
                    timingsTraceAndSlog.traceBegin("hasUserRestriction-DISALLOW_USER_SWITCH");
                    if (this.mLocalService.hasUserRestriction("no_user_switch", i)) {
                        i2 |= 2;
                    }
                    timingsTraceAndSlog.traceEnd();
                    if (!isHeadlessSystemUserMode()) {
                        timingsTraceAndSlog.traceBegin("getInt-ALLOW_USER_SWITCHING_WHEN_SYSTEM_USER_LOCKED");
                        boolean z = Settings.Global.getInt(this.mContext.getContentResolver(), "allow_user_switching_when_system_user_locked", 0) != 0;
                        timingsTraceAndSlog.traceEnd();
                        timingsTraceAndSlog.traceBegin("isUserUnlocked-USER_SYSTEM");
                        boolean isUserUnlocked = this.mLocalService.isUserUnlocked(0);
                        timingsTraceAndSlog.traceEnd();
                        if (!z && !isUserUnlocked) {
                            i2 |= 4;
                        }
                    }
                    timingsTraceAndSlog.traceEnd();
                    return i2;
                }
            }
            i2 = 0;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("hasUserRestriction-DISALLOW_USER_SWITCH");
            if (this.mLocalService.hasUserRestriction("no_user_switch", i)) {
            }
            timingsTraceAndSlog.traceEnd();
            if (!isHeadlessSystemUserMode()) {
            }
            timingsTraceAndSlog.traceEnd();
            return i2;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @VisibleForTesting
    public boolean isUserSwitcherEnabled(int i) {
        return (!UserManager.supportsMultipleUsers() || hasUserRestriction("no_user_switch", i) || UserManager.isDeviceInDemoMode(this.mContext) || (Settings.Global.getInt(this.mContext.getContentResolver(), "user_switcher_enabled", Resources.getSystem().getBoolean(17891794) ? 1 : 0) != 0 ? 1 : null) == null) ? false : true;
    }

    public boolean isUserSwitcherEnabled(boolean z, int i) {
        if (isUserSwitcherEnabled(i)) {
            return z || !hasUserRestriction("no_add_user", i) || areThereMultipleSwitchableUsers();
        }
        return false;
    }

    public final boolean areThereMultipleSwitchableUsers() {
        boolean z = false;
        for (UserInfo userInfo : getUsers(true, true, true)) {
            if (userInfo.supportsSwitchToByUser()) {
                if (z) {
                    return true;
                }
                z = true;
            }
        }
        return false;
    }

    public boolean isRestricted(int i) {
        boolean isRestricted;
        if (i != UserHandle.getCallingUserId()) {
            checkCreateUsersPermission("query isRestricted for user " + i);
        }
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            isRestricted = userInfoLU == null ? false : userInfoLU.isRestricted();
        }
        return isRestricted;
    }

    public boolean canHaveRestrictedProfile(int i) {
        checkManageUsersPermission("canHaveRestrictedProfile");
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            boolean z = false;
            if (userInfoLU != null && userInfoLU.canHaveProfile()) {
                if (userInfoLU.isAdmin()) {
                    if (!this.mIsDeviceManaged && !this.mIsUserManaged.get(i)) {
                        z = true;
                    }
                    return z;
                }
                return false;
            }
            return false;
        }
    }

    public boolean hasRestrictedProfiles(int i) {
        checkManageUsersPermission("hasRestrictedProfiles");
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if (i != userInfo.id && userInfo.restrictedProfileParentId == i) {
                    return true;
                }
            }
            return false;
        }
    }

    @GuardedBy({"mUsersLock"})
    public final UserInfo getUserInfoLU(int i) {
        UserData userData = this.mUsers.get(i);
        if (userData == null || !userData.info.partial || this.mRemovingUserIds.get(i)) {
            if (userData != null) {
                return userData.info;
            }
            return null;
        }
        Slog.w("UserManagerService", "getUserInfo: unknown user #" + i);
        return null;
    }

    @GuardedBy({"mUsersLock"})
    public final UserData getUserDataLU(int i) {
        UserData userData = this.mUsers.get(i);
        if (userData == null || !userData.info.partial || this.mRemovingUserIds.get(i)) {
            return userData;
        }
        return null;
    }

    public final UserInfo getUserInfoNoChecks(int i) {
        UserInfo userInfo;
        synchronized (this.mUsersLock) {
            UserData userData = this.mUsers.get(i);
            userInfo = userData != null ? userData.info : null;
        }
        return userInfo;
    }

    public final UserData getUserDataNoChecks(int i) {
        UserData userData;
        synchronized (this.mUsersLock) {
            userData = this.mUsers.get(i);
        }
        return userData;
    }

    public boolean exists(int i) {
        return this.mLocalService.exists(i);
    }

    public final int getCrossProfileIntentFilterAccessControl(int i) {
        UserProperties userPropertiesInternal = getUserPropertiesInternal(i);
        if (userPropertiesInternal != null) {
            return userPropertiesInternal.getCrossProfileIntentFilterAccessControl();
        }
        return 0;
    }

    public void enforceCrossProfileIntentFilterAccess(int i, int i2, int i3, boolean z) {
        if (isCrossProfileIntentFilterAccessible(i, i2, z)) {
            return;
        }
        throw new SecurityException("CrossProfileIntentFilter cannot be accessed by user " + i3);
    }

    public boolean isCrossProfileIntentFilterAccessible(int i, int i2, boolean z) {
        int crossProfileIntentFilterAccessControl = getCrossProfileIntentFilterAccessControl(i, i2);
        if (10 != crossProfileIntentFilterAccessControl || PackageManagerServiceUtils.isSystemOrRoot()) {
            if (20 == crossProfileIntentFilterAccessControl) {
                return z && PackageManagerServiceUtils.isSystemOrRoot();
            }
            return true;
        }
        return false;
    }

    public int getCrossProfileIntentFilterAccessControl(int i, int i2) {
        return Math.max(getCrossProfileIntentFilterAccessControl(i), getCrossProfileIntentFilterAccessControl(i2));
    }

    public void setUserName(int i, String str) {
        checkManageUsersPermission("rename users");
        synchronized (this.mPackagesLock) {
            UserData userDataNoChecks = getUserDataNoChecks(i);
            if (userDataNoChecks != null) {
                UserInfo userInfo = userDataNoChecks.info;
                if (!userInfo.partial) {
                    if (Objects.equals(str, userInfo.name)) {
                        Slogf.m20i("UserManagerService", "setUserName: ignoring for user #%d as it didn't change (%s)", Integer.valueOf(i), getRedacted(str));
                        return;
                    }
                    if (str == null) {
                        Slogf.m20i("UserManagerService", "setUserName: resetting name of user #%d", Integer.valueOf(i));
                    } else {
                        Slogf.m20i("UserManagerService", "setUserName: setting name of user #%d to %s", Integer.valueOf(i), getRedacted(str));
                    }
                    userDataNoChecks.info.name = str;
                    writeUserLP(userDataNoChecks);
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        sendUserInfoChangedBroadcast(i);
                        return;
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
            }
            Slogf.m12w("UserManagerService", "setUserName: unknown user #%d", Integer.valueOf(i));
        }
    }

    public boolean setUserEphemeral(int i, boolean z) {
        checkCreateUsersPermission("update ephemeral user flag");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userData = this.mUsers.get(i);
                if (userData == null) {
                    Slog.e("UserManagerService", "User not found for setting ephemeral mode: u" + i);
                    return false;
                }
                UserInfo userInfo = userData.info;
                int i2 = userInfo.flags;
                boolean z2 = (i2 & 256) != 0;
                if (((i2 & IInstalld.FLAG_FORCE) != 0) && !z) {
                    Slog.e("UserManagerService", "Failed to change user state to non-ephemeral for user " + i);
                    return false;
                }
                if (z2 == z) {
                    userData = null;
                } else if (z) {
                    userInfo.flags = i2 | 256;
                } else {
                    userInfo.flags = i2 & (-257);
                }
                if (userData != null) {
                    writeUserLP(userData);
                }
                return true;
            }
        }
    }

    public void setUserIcon(int i, Bitmap bitmap) {
        try {
            checkManageUsersPermission("update users");
            enforceUserRestriction("no_set_user_icon", i, "Cannot set user icon");
            this.mLocalService.setUserIcon(i, bitmap);
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public final void sendUserInfoChangedBroadcast(int i) {
        Intent intent = new Intent("android.intent.action.USER_INFO_CHANGED");
        intent.putExtra("android.intent.extra.user_handle", i);
        intent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public ParcelFileDescriptor getUserIcon(int i) {
        if (!hasManageUsersOrPermission("android.permission.GET_ACCOUNTS_PRIVILEGED")) {
            throw new SecurityException("You need MANAGE_USERS or GET_ACCOUNTS_PRIVILEGED permissions to: get user icon");
        }
        synchronized (this.mPackagesLock) {
            UserInfo userInfoNoChecks = getUserInfoNoChecks(i);
            if (userInfoNoChecks != null && !userInfoNoChecks.partial) {
                int callingUserId = UserHandle.getCallingUserId();
                int i2 = getUserInfoNoChecks(callingUserId).profileGroupId;
                boolean z = i2 != -10000 && i2 == userInfoNoChecks.profileGroupId;
                if (callingUserId != i && !z) {
                    checkManageUsersPermission("get the icon of a user who is not related");
                }
                String str = userInfoNoChecks.iconPath;
                if (str == null) {
                    return null;
                }
                try {
                    return ParcelFileDescriptor.open(new File(str), 268435456);
                } catch (FileNotFoundException e) {
                    Slog.e("UserManagerService", "Couldn't find icon file", e);
                    return null;
                }
            }
            Slog.w("UserManagerService", "getUserIcon: unknown user #" + i);
            return null;
        }
    }

    public void makeInitialized(int i) {
        boolean z;
        checkManageUsersPermission("makeInitialized");
        synchronized (this.mUsersLock) {
            UserData userData = this.mUsers.get(i);
            if (userData != null) {
                UserInfo userInfo = userData.info;
                if (!userInfo.partial) {
                    int i2 = userInfo.flags;
                    if ((i2 & 16) == 0) {
                        userInfo.flags = i2 | 16;
                        z = true;
                    } else {
                        z = false;
                    }
                    if (z) {
                        scheduleWriteUser(userData);
                        return;
                    }
                    return;
                }
            }
            Slog.w("UserManagerService", "makeInitialized: unknown user #" + i);
        }
    }

    public final void initDefaultGuestRestrictions() {
        synchronized (this.mGuestRestrictions) {
            if (this.mGuestRestrictions.isEmpty()) {
                UserTypeDetails userTypeDetails = this.mUserTypes.get("android.os.usertype.full.GUEST");
                if (userTypeDetails == null) {
                    Slog.wtf("UserManagerService", "Can't set default guest restrictions: type doesn't exist.");
                    return;
                }
                userTypeDetails.addDefaultRestrictionsTo(this.mGuestRestrictions);
            }
        }
    }

    public Bundle getDefaultGuestRestrictions() {
        Bundle bundle;
        checkManageUsersPermission("getDefaultGuestRestrictions");
        synchronized (this.mGuestRestrictions) {
            bundle = new Bundle(this.mGuestRestrictions);
        }
        return bundle;
    }

    public void setDefaultGuestRestrictions(Bundle bundle) {
        checkManageUsersPermission("setDefaultGuestRestrictions");
        synchronized (this.mGuestRestrictions) {
            this.mGuestRestrictions.clear();
            this.mGuestRestrictions.putAll(bundle);
            List<UserInfo> guestUsers = getGuestUsers();
            for (int i = 0; i < guestUsers.size(); i++) {
                synchronized (this.mRestrictionsLock) {
                    updateUserRestrictionsInternalLR(this.mGuestRestrictions, guestUsers.get(i).id);
                }
            }
        }
        synchronized (this.mPackagesLock) {
            writeUserListLP();
        }
    }

    public final void setUserRestrictionInner(int i, String str, boolean z) {
        if (UserRestrictionsUtils.isValidRestriction(str)) {
            synchronized (this.mRestrictionsLock) {
                Bundle clone = BundleUtils.clone(this.mDevicePolicyUserRestrictions.getRestrictions(i));
                clone.putBoolean(str, z);
                if (this.mDevicePolicyUserRestrictions.updateRestrictions(i, clone)) {
                    if (i == -1) {
                        applyUserRestrictionsForAllUsersLR();
                    } else {
                        applyUserRestrictionsLR(i);
                    }
                }
            }
        }
    }

    public final void setDevicePolicyUserRestrictionsInner(int i, Bundle bundle, RestrictionsSet restrictionsSet, boolean z) {
        synchronized (this.mRestrictionsLock) {
            IntArray userIds = this.mDevicePolicyUserRestrictions.getUserIds();
            this.mCachedEffectiveUserRestrictions.removeAllRestrictions();
            this.mDevicePolicyUserRestrictions.removeAllRestrictions();
            this.mDevicePolicyUserRestrictions.updateRestrictions(-1, bundle);
            IntArray userIds2 = restrictionsSet.getUserIds();
            for (int i2 = 0; i2 < userIds2.size(); i2++) {
                int i3 = userIds2.get(i2);
                this.mDevicePolicyUserRestrictions.updateRestrictions(i3, restrictionsSet.getRestrictions(i3));
                userIds.add(i3);
            }
            applyUserRestrictionsForAllUsersLR();
            for (int i4 = 0; i4 < userIds.size(); i4++) {
                if (userIds.get(i4) != -1) {
                    applyUserRestrictionsLR(userIds.get(i4));
                }
            }
        }
    }

    @GuardedBy({"mRestrictionsLock"})
    public final Bundle computeEffectiveUserRestrictionsLR(int i) {
        Bundle restrictionsNonNull = this.mBaseUserRestrictions.getRestrictionsNonNull(i);
        Bundle restrictionsNonNull2 = this.mDevicePolicyUserRestrictions.getRestrictionsNonNull(-1);
        Bundle restrictionsNonNull3 = this.mDevicePolicyUserRestrictions.getRestrictionsNonNull(i);
        if (restrictionsNonNull2.isEmpty() && restrictionsNonNull3.isEmpty()) {
            return restrictionsNonNull;
        }
        Bundle clone = BundleUtils.clone(restrictionsNonNull);
        UserRestrictionsUtils.merge(clone, restrictionsNonNull2);
        UserRestrictionsUtils.merge(clone, restrictionsNonNull3);
        return clone;
    }

    public final Bundle getEffectiveUserRestrictions(int i) {
        Bundle restrictions;
        synchronized (this.mRestrictionsLock) {
            restrictions = this.mCachedEffectiveUserRestrictions.getRestrictions(i);
            if (restrictions == null) {
                restrictions = computeEffectiveUserRestrictionsLR(i);
                this.mCachedEffectiveUserRestrictions.updateRestrictions(i, restrictions);
            }
        }
        return restrictions;
    }

    public boolean hasUserRestriction(String str, int i) {
        if (userExists(i)) {
            checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "hasUserRestriction");
            return this.mLocalService.hasUserRestriction(str, i);
        }
        return false;
    }

    public boolean hasUserRestrictionOnAnyUser(String str) {
        if (UserRestrictionsUtils.isValidRestriction(str)) {
            List<UserInfo> users = getUsers(true);
            for (int i = 0; i < users.size(); i++) {
                Bundle effectiveUserRestrictions = getEffectiveUserRestrictions(users.get(i).id);
                if (effectiveUserRestrictions != null && effectiveUserRestrictions.getBoolean(str)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean isSettingRestrictedForUser(String str, int i, String str2, int i2) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Non-system caller");
        }
        return UserRestrictionsUtils.isSettingRestrictedForUser(this.mContext, str, i, str2, i2);
    }

    public void addUserRestrictionsListener(final IUserRestrictionsListener iUserRestrictionsListener) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Non-system caller");
        }
        this.mLocalService.addUserRestrictionsListener(new UserManagerInternal.UserRestrictionsListener() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda2
            @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
            public final void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2) {
                UserManagerService.lambda$addUserRestrictionsListener$0(iUserRestrictionsListener, i, bundle, bundle2);
            }
        });
    }

    public static /* synthetic */ void lambda$addUserRestrictionsListener$0(IUserRestrictionsListener iUserRestrictionsListener, int i, Bundle bundle, Bundle bundle2) {
        try {
            iUserRestrictionsListener.onUserRestrictionsChanged(i, bundle, bundle2);
        } catch (RemoteException e) {
            Slog.e("IUserRestrictionsListener", "Unable to invoke listener: " + e.getMessage());
        }
    }

    public int getUserRestrictionSource(String str, int i) {
        List<UserManager.EnforcingUser> userRestrictionSources = getUserRestrictionSources(str, i);
        int i2 = 0;
        for (int size = userRestrictionSources.size() - 1; size >= 0; size--) {
            i2 |= userRestrictionSources.get(size).getUserRestrictionSource();
        }
        return i2;
    }

    public List<UserManager.EnforcingUser> getUserRestrictionSources(String str, int i) {
        checkQueryUsersPermission("call getUserRestrictionSources.");
        if (!hasUserRestriction(str, i)) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        if (hasBaseUserRestriction(str, i)) {
            arrayList.add(new UserManager.EnforcingUser(-10000, 1));
        }
        synchronized (this.mRestrictionsLock) {
            arrayList.addAll(this.mDevicePolicyUserRestrictions.getEnforcingUsers(str, i));
        }
        return arrayList;
    }

    public Bundle getUserRestrictions(int i) {
        checkManageOrInteractPermissionIfCallerInOtherProfileGroup(i, "getUserRestrictions");
        return BundleUtils.clone(getEffectiveUserRestrictions(i));
    }

    public boolean hasBaseUserRestriction(String str, int i) {
        checkCreateUsersPermission("hasBaseUserRestriction");
        boolean z = false;
        if (UserRestrictionsUtils.isValidRestriction(str)) {
            synchronized (this.mRestrictionsLock) {
                Bundle restrictions = this.mBaseUserRestrictions.getRestrictions(i);
                if (restrictions != null && restrictions.getBoolean(str, false)) {
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    public void setUserRestriction(String str, boolean z, int i) {
        checkManageUsersPermission("setUserRestriction");
        if (UserRestrictionsUtils.isValidRestriction(str)) {
            if (!userExists(i)) {
                Slogf.m12w("UserManagerService", "Cannot set user restriction %s. User with id %d does not exist", str, Integer.valueOf(i));
                return;
            }
            synchronized (this.mRestrictionsLock) {
                Bundle clone = BundleUtils.clone(this.mBaseUserRestrictions.getRestrictions(i));
                clone.putBoolean(str, z);
                updateUserRestrictionsInternalLR(clone, i);
            }
        }
    }

    @GuardedBy({"mRestrictionsLock"})
    public final void updateUserRestrictionsInternalLR(Bundle bundle, final int i) {
        Bundle nonNull = UserRestrictionsUtils.nonNull(this.mAppliedUserRestrictions.getRestrictions(i));
        if (bundle != null) {
            Preconditions.checkState(this.mBaseUserRestrictions.getRestrictions(i) != bundle);
            Preconditions.checkState(this.mCachedEffectiveUserRestrictions.getRestrictions(i) != bundle);
            if (this.mBaseUserRestrictions.updateRestrictions(i, bundle)) {
                scheduleWriteUser(getUserDataNoChecks(i));
            }
        }
        final Bundle computeEffectiveUserRestrictionsLR = computeEffectiveUserRestrictionsLR(i);
        this.mCachedEffectiveUserRestrictions.updateRestrictions(i, computeEffectiveUserRestrictionsLR);
        if (this.mAppOpsService != null) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    UserManagerService.this.lambda$updateUserRestrictionsInternalLR$1(computeEffectiveUserRestrictionsLR, i);
                }
            });
        }
        propagateUserRestrictionsLR(i, computeEffectiveUserRestrictionsLR, nonNull);
        this.mAppliedUserRestrictions.updateRestrictions(i, new Bundle(computeEffectiveUserRestrictionsLR));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateUserRestrictionsInternalLR$1(Bundle bundle, int i) {
        try {
            this.mAppOpsService.setUserRestrictions(bundle, this.mUserRestrictionToken, i);
        } catch (RemoteException unused) {
            Slog.w("UserManagerService", "Unable to notify AppOpsService of UserRestrictions");
        }
    }

    @GuardedBy({"mRestrictionsLock"})
    public final void propagateUserRestrictionsLR(final int i, Bundle bundle, Bundle bundle2) {
        if (UserRestrictionsUtils.areEqual(bundle, bundle2)) {
            return;
        }
        final Bundle bundle3 = new Bundle(bundle);
        final Bundle bundle4 = new Bundle(bundle2);
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                int size;
                UserManagerInternal.UserRestrictionsListener[] userRestrictionsListenerArr;
                UserRestrictionsUtils.applyUserRestrictions(UserManagerService.this.mContext, i, bundle3, bundle4);
                synchronized (UserManagerService.this.mUserRestrictionsListeners) {
                    size = UserManagerService.this.mUserRestrictionsListeners.size();
                    userRestrictionsListenerArr = new UserManagerInternal.UserRestrictionsListener[size];
                    UserManagerService.this.mUserRestrictionsListeners.toArray(userRestrictionsListenerArr);
                }
                for (int i2 = 0; i2 < size; i2++) {
                    userRestrictionsListenerArr[i2].onUserRestrictionsChanged(i, bundle3, bundle4);
                }
                UserManagerService.this.mContext.sendBroadcastAsUser(new Intent("android.os.action.USER_RESTRICTIONS_CHANGED").setFlags(1073741824), UserHandle.of(i), null, BroadcastOptions.makeBasic().setDeliveryGroupPolicy(1).toBundle());
            }
        });
    }

    @GuardedBy({"mRestrictionsLock"})
    public final void applyUserRestrictionsLR(int i) {
        updateUserRestrictionsInternalLR(null, i);
        scheduleWriteUser(getUserDataNoChecks(i));
    }

    @GuardedBy({"mRestrictionsLock"})
    public final void applyUserRestrictionsForAllUsersLR() {
        this.mCachedEffectiveUserRestrictions.removeAllRestrictions();
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserManagerService.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    int[] runningUserIds = ActivityManager.getService().getRunningUserIds();
                    synchronized (UserManagerService.this.mRestrictionsLock) {
                        for (int i : runningUserIds) {
                            UserManagerService.this.applyUserRestrictionsLR(i);
                        }
                    }
                } catch (RemoteException unused) {
                    Slog.w("UserManagerService", "Unable to access ActivityManagerService");
                }
            }
        });
    }

    public final boolean isUserLimitReached() {
        int aliveUsersExcludingGuestsCountLU;
        synchronized (this.mUsersLock) {
            aliveUsersExcludingGuestsCountLU = getAliveUsersExcludingGuestsCountLU();
        }
        return aliveUsersExcludingGuestsCountLU >= UserManager.getMaxSupportedUsers() && !isCreationOverrideEnabled();
    }

    public final boolean canAddMoreUsersOfType(UserTypeDetails userTypeDetails) {
        if (isUserTypeEnabled(userTypeDetails)) {
            int maxAllowed = userTypeDetails.getMaxAllowed();
            if (maxAllowed == -1) {
                return true;
            }
            return getNumberOfUsersOfType(userTypeDetails.getName()) < maxAllowed || isCreationOverrideEnabled();
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x003c A[Catch: all -> 0x007b, TryCatch #0 {, blocks: (B:9:0x001b, B:11:0x0028, B:14:0x002f, B:16:0x0036, B:18:0x003c, B:20:0x004a, B:29:0x0057, B:33:0x005d, B:35:0x005f, B:39:0x0071, B:40:0x0079, B:38:0x0067), top: B:46:0x001b }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x005d A[Catch: all -> 0x007b, DONT_GENERATE, TryCatch #0 {, blocks: (B:9:0x001b, B:11:0x0028, B:14:0x002f, B:16:0x0036, B:18:0x003c, B:20:0x004a, B:29:0x0057, B:33:0x005d, B:35:0x005f, B:39:0x0071, B:40:0x0079, B:38:0x0067), top: B:46:0x001b }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x005f A[Catch: all -> 0x007b, TryCatch #0 {, blocks: (B:9:0x001b, B:11:0x0028, B:14:0x002f, B:16:0x0036, B:18:0x003c, B:20:0x004a, B:29:0x0057, B:33:0x005d, B:35:0x005f, B:39:0x0071, B:40:0x0079, B:38:0x0067), top: B:46:0x001b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int getRemainingCreatableUserCount(String str) {
        int i;
        checkQueryOrCreateUsersPermission("get the remaining number of users that can be added.");
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        if (userTypeDetails == null || !isUserTypeEnabled(userTypeDetails)) {
            return 0;
        }
        synchronized (this.mUsersLock) {
            int aliveUsersExcludingGuestsCountLU = getAliveUsersExcludingGuestsCountLU();
            int i2 = Integer.MAX_VALUE;
            if (!UserManager.isUserTypeGuest(str) && !UserManager.isUserTypeDemo(str)) {
                i = UserManager.getMaxSupportedUsers() - aliveUsersExcludingGuestsCountLU;
                if (userTypeDetails.isManagedProfile()) {
                    if (!this.mContext.getPackageManager().hasSystemFeature("android.software.managed_users")) {
                        return 0;
                    }
                    if ((aliveUsersExcludingGuestsCountLU == 1) & (i <= 0)) {
                        i = 1;
                    }
                }
                if (i > 0) {
                    return 0;
                }
                if (userTypeDetails.getMaxAllowed() != -1) {
                    i2 = userTypeDetails.getMaxAllowed() - getNumberOfUsersOfType(str);
                }
                return Math.max(0, Math.min(i, i2));
            }
            i = Integer.MAX_VALUE;
            if (userTypeDetails.isManagedProfile()) {
            }
            if (i > 0) {
            }
        }
    }

    public final int getNumberOfUsersOfType(String str) {
        int i;
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if (userInfo.userType.equals(str) && !userInfo.guestToRemove && !this.mRemovingUserIds.get(userInfo.id) && !userInfo.preCreated) {
                    i++;
                }
            }
        }
        return i;
    }

    public boolean canAddMoreUsersOfType(String str) {
        checkCreateUsersPermission("check if more users can be added.");
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        return userTypeDetails != null && canAddMoreUsersOfType(userTypeDetails);
    }

    public boolean isUserTypeEnabled(String str) {
        checkCreateUsersPermission("check if user type is enabled.");
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        return userTypeDetails != null && isUserTypeEnabled(userTypeDetails);
    }

    public final boolean isUserTypeEnabled(UserTypeDetails userTypeDetails) {
        return userTypeDetails.isEnabled() || isCreationOverrideEnabled();
    }

    public final boolean isCreationOverrideEnabled() {
        return Build.isDebuggable() && SystemProperties.getBoolean("debug.user.creation_override", false);
    }

    public boolean canAddMoreManagedProfiles(int i, boolean z) {
        return canAddMoreProfilesToUser("android.os.usertype.profile.MANAGED", i, z);
    }

    public boolean canAddMoreProfilesToUser(String str, int i, boolean z) {
        return getRemainingCreatableProfileCount(str, i, z) > 0 || isCreationOverrideEnabled();
    }

    public int getRemainingCreatableProfileCount(String str, int i) {
        return getRemainingCreatableProfileCount(str, i, false);
    }

    public final int getRemainingCreatableProfileCount(String str, int i, boolean z) {
        checkQueryOrCreateUsersPermission("get the remaining number of profiles that can be added to the given user.");
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        if (userTypeDetails == null || !isUserTypeEnabled(userTypeDetails)) {
            return 0;
        }
        boolean isManagedProfile = userTypeDetails.isManagedProfile();
        if (!isManagedProfile || this.mContext.getPackageManager().hasSystemFeature("android.software.managed_users")) {
            synchronized (this.mUsersLock) {
                UserInfo userInfoLU = getUserInfoLU(i);
                if (userInfoLU != null && userInfoLU.canHaveProfile()) {
                    int length = getProfileIds(i, str, false).length;
                    int i2 = 1;
                    int i3 = (length <= 0 || !z) ? 0 : 1;
                    int aliveUsersExcludingGuestsCountLU = getAliveUsersExcludingGuestsCountLU() - i3;
                    int maxSupportedUsers = UserManager.getMaxSupportedUsers() - aliveUsersExcludingGuestsCountLU;
                    if (maxSupportedUsers > 0 || !isManagedProfile || aliveUsersExcludingGuestsCountLU != 1) {
                        i2 = maxSupportedUsers;
                    }
                    int maxUsersOfTypePerParent = getMaxUsersOfTypePerParent(userTypeDetails);
                    if (maxUsersOfTypePerParent != -1) {
                        i2 = Math.min(i2, maxUsersOfTypePerParent - (length - i3));
                    }
                    if (i2 <= 0) {
                        return 0;
                    }
                    if (userTypeDetails.getMaxAllowed() != -1) {
                        i2 = Math.min(i2, userTypeDetails.getMaxAllowed() - (getNumberOfUsersOfType(str) - i3));
                    }
                    return Math.max(0, i2);
                }
                return 0;
            }
        }
        return 0;
    }

    @GuardedBy({"mUsersLock"})
    public final int getAliveUsersExcludingGuestsCountLU() {
        int size = this.mUsers.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            UserInfo userInfo = this.mUsers.valueAt(i2).info;
            if (!this.mRemovingUserIds.get(userInfo.id) && !userInfo.isGuest() && !userInfo.preCreated) {
                i++;
            }
        }
        return i;
    }

    public static final void checkManageUserAndAcrossUsersFullPermission(String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000 || callingUid == 0) {
            return;
        }
        if (hasPermissionGranted("android.permission.MANAGE_USERS", callingUid) && hasPermissionGranted("android.permission.INTERACT_ACROSS_USERS_FULL", callingUid)) {
            return;
        }
        throw new SecurityException("You need MANAGE_USERS and INTERACT_ACROSS_USERS_FULL permission to: " + str);
    }

    public static boolean hasPermissionGranted(String str, int i) {
        return ActivityManager.checkComponentPermission(str, i, -1, true) == 0;
    }

    public static final void checkManageUsersPermission(String str) {
        if (hasManageUsersPermission()) {
            return;
        }
        throw new SecurityException("You need MANAGE_USERS permission to: " + str);
    }

    public static final void checkCreateUsersPermission(String str) {
        if (hasCreateUsersPermission()) {
            return;
        }
        throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to: " + str);
    }

    public static final void checkQueryUsersPermission(String str) {
        if (hasQueryUsersPermission()) {
            return;
        }
        throw new SecurityException("You either need MANAGE_USERS or QUERY_USERS permission to: " + str);
    }

    public static final void checkQueryOrCreateUsersPermission(String str) {
        if (hasQueryOrCreateUsersPermission()) {
            return;
        }
        throw new SecurityException("You either need MANAGE_USERS, CREATE_USERS, or QUERY_USERS permission to: " + str);
    }

    public static final void checkCreateUsersPermission(int i) {
        if (((-38701) & i) == 0) {
            if (hasCreateUsersPermission()) {
                return;
            }
            throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to create an user with flags: " + i);
        } else if (hasManageUsersPermission()) {
        } else {
            throw new SecurityException("You need MANAGE_USERS permission to create an user  with flags: " + i);
        }
    }

    public static final boolean hasManageUsersPermission() {
        return hasManageUsersPermission(Binder.getCallingUid());
    }

    public static boolean hasManageUsersPermission(int i) {
        return UserHandle.isSameApp(i, 1000) || i == 0 || hasPermissionGranted("android.permission.MANAGE_USERS", i);
    }

    public static final boolean hasManageUsersOrPermission(String str) {
        int callingUid = Binder.getCallingUid();
        return hasManageUsersPermission(callingUid) || hasPermissionGranted(str, callingUid);
    }

    public static final boolean hasCreateUsersPermission() {
        return hasManageUsersOrPermission("android.permission.CREATE_USERS");
    }

    public static final boolean hasQueryUsersPermission() {
        return hasManageUsersOrPermission("android.permission.QUERY_USERS");
    }

    public static final boolean hasQueryOrCreateUsersPermission() {
        return hasCreateUsersPermission() || hasPermissionGranted("android.permission.QUERY_USERS", Binder.getCallingUid());
    }

    public static void checkSystemOrRoot(String str) {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, 1000) || callingUid == 0) {
            return;
        }
        throw new SecurityException("Only system may: " + str);
    }

    @GuardedBy({"mPackagesLock"})
    public final void writeBitmapLP(UserInfo userInfo, Bitmap bitmap) {
        try {
            File file = new File(this.mUsersDir, Integer.toString(userInfo.id));
            File file2 = new File(file, "photo.png");
            File file3 = new File(file, "photo.png.tmp");
            if (!file.exists()) {
                file.mkdir();
                FileUtils.setPermissions(file.getPath(), 505, -1, -1);
            }
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            if (bitmap.compress(compressFormat, 100, fileOutputStream) && file3.renameTo(file2) && SELinux.restorecon(file2)) {
                userInfo.iconPath = file2.getAbsolutePath();
            }
            try {
                fileOutputStream.close();
            } catch (IOException unused) {
            }
            file3.delete();
        } catch (FileNotFoundException e) {
            Slog.w("UserManagerService", "Error setting photo for user ", e);
        }
    }

    public int[] getUserIds() {
        int[] iArr;
        synchronized (this.mUsersLock) {
            iArr = this.mUserIds;
        }
        return iArr;
    }

    @VisibleForTesting
    public boolean userExists(int i) {
        synchronized (this.mUsersLock) {
            for (int i2 : this.mUserIds) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }
    }

    public int[] getUserIdsIncludingPreCreated() {
        int[] iArr;
        synchronized (this.mUsersLock) {
            iArr = this.mUserIdsIncludingPreCreated;
        }
        return iArr;
    }

    public boolean isHeadlessSystemUserMode() {
        boolean z;
        synchronized (this.mUsersLock) {
            z = this.mUsers.get(0).info.isFull() ? false : true;
        }
        return z;
    }

    public final boolean isDefaultHeadlessSystemUserMode() {
        if (!Build.isDebuggable()) {
            return RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER;
        }
        String str = SystemProperties.get("persist.debug.user_mode_emulation");
        if (!TextUtils.isEmpty(str)) {
            if ("headless".equals(str)) {
                return true;
            }
            if ("full".equals(str)) {
                return false;
            }
            if (!"default".equals(str)) {
                Slogf.m24e("UserManagerService", "isDefaultHeadlessSystemUserMode(): ignoring invalid valued of property %s: %s", "persist.debug.user_mode_emulation", str);
            }
        }
        return RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER;
    }

    public final void emulateSystemUserModeIfNeeded() {
        String str;
        int i;
        UserInfo earliestCreatedFullUser;
        if (Build.isDebuggable() && !TextUtils.isEmpty(SystemProperties.get("persist.debug.user_mode_emulation"))) {
            boolean isDefaultHeadlessSystemUserMode = isDefaultHeadlessSystemUserMode();
            synchronized (this.mPackagesLock) {
                synchronized (this.mUsersLock) {
                    UserData userData = this.mUsers.get(0);
                    if (userData == null) {
                        Slogf.wtf("UserManagerService", "emulateSystemUserModeIfNeeded(): no system user data");
                        return;
                    }
                    int mainUserIdUnchecked = getMainUserIdUnchecked();
                    UserInfo userInfo = userData.info;
                    int i2 = userInfo.flags;
                    if (isDefaultHeadlessSystemUserMode) {
                        str = "android.os.usertype.system.HEADLESS";
                        i = i2 & (-1025) & (-16385);
                    } else {
                        str = "android.os.usertype.full.SYSTEM";
                        i = i2 | 1024;
                    }
                    if (userInfo.userType.equals(str)) {
                        Slogf.m28d("UserManagerService", "emulateSystemUserModeIfNeeded(): system user type is already %s, returning", str);
                        return;
                    }
                    Slogf.m20i("UserManagerService", "Persisting emulated system user data: type changed from %s to %s, flags changed from %s to %s", userData.info.userType, str, UserInfo.flagsToString(i2), UserInfo.flagsToString(i));
                    UserInfo userInfo2 = userData.info;
                    userInfo2.userType = str;
                    userInfo2.flags = i;
                    writeUserLP(userData);
                    UserData userDataNoChecks = getUserDataNoChecks(mainUserIdUnchecked);
                    if (isDefaultHeadlessSystemUserMode) {
                        if (userDataNoChecks != null && (userDataNoChecks.info.flags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0 && (earliestCreatedFullUser = getEarliestCreatedFullUser()) != null) {
                            Slogf.m22i("UserManagerService", "Designating user " + earliestCreatedFullUser.id + " to be Main");
                            earliestCreatedFullUser.flags = earliestCreatedFullUser.flags | 16384;
                            writeUserLP(getUserDataNoChecks(earliestCreatedFullUser.id));
                        }
                    } else if (userDataNoChecks != null && (userDataNoChecks.info.flags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0) {
                        Slogf.m22i("UserManagerService", "Transferring Main to user 0 from " + userDataNoChecks.info.id);
                        UserInfo userInfo3 = userDataNoChecks.info;
                        userInfo3.flags = userInfo3.flags & (-16385);
                        userData.info.flags |= 16384;
                        writeUserLP(userDataNoChecks);
                        writeUserLP(userData);
                    }
                    this.mUpdatingSystemUserMode = true;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:79:0x00f1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @GuardedBy({"mPackagesLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readUserListLP() {
        FileInputStream openRead;
        TypedXmlPullParser resolvePullParser;
        int next;
        if (!this.mUserListFile.exists()) {
            fallbackToSingleUserLP();
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            try {
                openRead = new AtomicFile(this.mUserListFile).openRead();
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException | XmlPullParserException unused) {
        }
        try {
            resolvePullParser = Xml.resolvePullParser(openRead);
            while (true) {
                next = resolvePullParser.next();
                if (next == 2 || next == 1) {
                    break;
                }
            }
        } catch (IOException | XmlPullParserException unused2) {
            fileInputStream = openRead;
            fallbackToSingleUserLP();
            IoUtils.closeQuietly(fileInputStream);
            synchronized (this.mUsersLock) {
            }
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = openRead;
            IoUtils.closeQuietly(fileInputStream);
            throw th;
        }
        if (next != 2) {
            Slog.e("UserManagerService", "Unable to read user list");
            fallbackToSingleUserLP();
            IoUtils.closeQuietly(openRead);
            return;
        }
        this.mNextSerialNumber = -1;
        if (resolvePullParser.getName().equals("users")) {
            this.mNextSerialNumber = resolvePullParser.getAttributeInt((String) null, "nextSerialNumber", this.mNextSerialNumber);
            this.mUserVersion = resolvePullParser.getAttributeInt((String) null, "version", this.mUserVersion);
            this.mUserTypeVersion = resolvePullParser.getAttributeInt((String) null, "userTypeConfigVersion", this.mUserTypeVersion);
        }
        while (true) {
            int next2 = resolvePullParser.next();
            if (next2 == 1) {
                break;
            } else if (next2 == 2) {
                String name = resolvePullParser.getName();
                if (name.equals("user")) {
                    UserData readUserLP = readUserLP(resolvePullParser.getAttributeInt((String) null, "id"));
                    if (readUserLP != null) {
                        synchronized (this.mUsersLock) {
                            this.mUsers.put(readUserLP.info.id, readUserLP);
                            int i = this.mNextSerialNumber;
                            if (i < 0 || i <= readUserLP.info.id) {
                                this.mNextSerialNumber = readUserLP.info.id + 1;
                            }
                        }
                    }
                } else if (name.equals("guestRestrictions")) {
                    while (true) {
                        int next3 = resolvePullParser.next();
                        if (next3 != 1 && next3 != 3) {
                            if (next3 == 2) {
                                if (resolvePullParser.getName().equals("restrictions")) {
                                    synchronized (this.mGuestRestrictions) {
                                        UserRestrictionsUtils.readRestrictions(resolvePullParser, this.mGuestRestrictions);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        updateUserIds();
        upgradeIfNecessaryLP();
        IoUtils.closeQuietly(openRead);
        synchronized (this.mUsersLock) {
            if (this.mUsers.size() == 0) {
                Slog.e("UserManagerService", "mUsers is empty, fallback to single user");
                fallbackToSingleUserLP();
            }
        }
    }

    @GuardedBy({"mPackagesLock"})
    public final void upgradeIfNecessaryLP() {
        upgradeIfNecessaryLP(this.mUserVersion, this.mUserTypeVersion);
    }

    @GuardedBy({"mPackagesLock"})
    @VisibleForTesting
    public void upgradeIfNecessaryLP(int i, int i2) {
        Slog.i("UserManagerService", "Upgrading users from userVersion " + i + " to 11");
        ArraySet<Integer> arraySet = new ArraySet();
        int i3 = this.mUserVersion;
        int i4 = this.mUserTypeVersion;
        if (i < 1) {
            UserData userDataNoChecks = getUserDataNoChecks(0);
            if ("Primary".equals(userDataNoChecks.info.name)) {
                userDataNoChecks.info.name = this.mContext.getResources().getString(17040928);
                arraySet.add(Integer.valueOf(userDataNoChecks.info.id));
            }
            i = 1;
        }
        if (i < 2) {
            UserInfo userInfo = getUserDataNoChecks(0).info;
            int i5 = userInfo.flags;
            if ((i5 & 16) == 0) {
                userInfo.flags = i5 | 16;
                arraySet.add(Integer.valueOf(userInfo.id));
            }
            i = 2;
        }
        if (i < 4) {
            i = 4;
        }
        if (i < 5) {
            initDefaultGuestRestrictions();
            i = 5;
        }
        if (i < 6) {
            synchronized (this.mUsersLock) {
                for (int i6 = 0; i6 < this.mUsers.size(); i6++) {
                    UserData valueAt = this.mUsers.valueAt(i6);
                    if (valueAt.info.isRestricted()) {
                        UserInfo userInfo2 = valueAt.info;
                        if (userInfo2.restrictedProfileParentId == -10000) {
                            userInfo2.restrictedProfileParentId = 0;
                            arraySet.add(Integer.valueOf(userInfo2.id));
                        }
                    }
                }
            }
            i = 6;
        }
        if (i < 7) {
            synchronized (this.mRestrictionsLock) {
                if (this.mDevicePolicyUserRestrictions.removeRestrictionsForAllUsers("ensure_verify_apps")) {
                    this.mDevicePolicyUserRestrictions.getRestrictionsNonNull(-1).putBoolean("ensure_verify_apps", true);
                }
            }
            List<UserInfo> guestUsers = getGuestUsers();
            for (int i7 = 0; i7 < guestUsers.size(); i7++) {
                UserInfo userInfo3 = guestUsers.get(i7);
                if (userInfo3 != null && !hasUserRestriction("no_config_wifi", userInfo3.id)) {
                    setUserRestriction("no_config_wifi", true, userInfo3.id);
                }
            }
            i = 7;
        }
        if (i < 8) {
            synchronized (this.mUsersLock) {
                UserData userData = this.mUsers.get(0);
                userData.info.flags |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
                if (!isDefaultHeadlessSystemUserMode()) {
                    userData.info.flags |= 1024;
                }
                arraySet.add(Integer.valueOf(userData.info.id));
                for (int i8 = 1; i8 < this.mUsers.size(); i8++) {
                    UserInfo userInfo4 = this.mUsers.valueAt(i8).info;
                    int i9 = userInfo4.flags;
                    if ((i9 & 32) == 0) {
                        userInfo4.flags = i9 | 1024;
                        arraySet.add(Integer.valueOf(userInfo4.id));
                    }
                }
            }
            i = 8;
        }
        if (i < 9) {
            synchronized (this.mUsersLock) {
                for (int i10 = 0; i10 < this.mUsers.size(); i10++) {
                    UserData valueAt2 = this.mUsers.valueAt(i10);
                    UserInfo userInfo5 = valueAt2.info;
                    int i11 = userInfo5.flags;
                    if ((i11 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0) {
                        try {
                            userInfo5.userType = UserInfo.getDefaultUserType(i11);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalStateException("Cannot upgrade user with flags " + Integer.toHexString(i11) + " because it doesn't correspond to a valid user type.", e);
                        }
                    } else if ((i11 & 1024) != 0) {
                        userInfo5.userType = "android.os.usertype.full.SYSTEM";
                    } else {
                        userInfo5.userType = "android.os.usertype.system.HEADLESS";
                    }
                    UserTypeDetails userTypeDetails = this.mUserTypes.get(valueAt2.info.userType);
                    if (userTypeDetails == null) {
                        throw new IllegalStateException("Cannot upgrade user with flags " + Integer.toHexString(i11) + " because " + valueAt2.info.userType + " isn't defined on this device!");
                    }
                    UserInfo userInfo6 = valueAt2.info;
                    userInfo6.flags = userTypeDetails.getDefaultUserInfoFlags() | userInfo6.flags;
                    arraySet.add(Integer.valueOf(valueAt2.info.id));
                }
            }
            i = 9;
        }
        if (i < 10) {
            synchronized (this.mUsersLock) {
                for (int i12 = 0; i12 < this.mUsers.size(); i12++) {
                    UserData valueAt3 = this.mUsers.valueAt(i12);
                    UserTypeDetails userTypeDetails2 = this.mUserTypes.get(valueAt3.info.userType);
                    if (userTypeDetails2 == null) {
                        throw new IllegalStateException("Cannot upgrade user because " + valueAt3.info.userType + " isn't defined on this device!");
                    }
                    valueAt3.userProperties = new UserProperties(userTypeDetails2.getDefaultUserPropertiesReference());
                    arraySet.add(Integer.valueOf(valueAt3.info.id));
                }
            }
            i = 10;
        }
        if (i < 11) {
            if (isHeadlessSystemUserMode()) {
                UserInfo earliestCreatedFullUser = getEarliestCreatedFullUser();
                if (earliestCreatedFullUser != null) {
                    earliestCreatedFullUser.flags |= 16384;
                    arraySet.add(Integer.valueOf(earliestCreatedFullUser.id));
                }
            } else {
                synchronized (this.mUsersLock) {
                    UserInfo userInfo7 = this.mUsers.get(0).info;
                    userInfo7.flags |= 16384;
                    arraySet.add(Integer.valueOf(userInfo7.id));
                }
            }
            i = 11;
        }
        int userTypeVersion = UserTypeFactory.getUserTypeVersion();
        if (userTypeVersion > i2) {
            synchronized (this.mUsersLock) {
                upgradeUserTypesLU(UserTypeFactory.getUserTypeUpgrades(), this.mUserTypes, i2, arraySet);
            }
        }
        if (i < 11) {
            Slog.w("UserManagerService", "User version " + this.mUserVersion + " didn't upgrade as expected to 11");
            return;
        }
        if (i > 11) {
            Slog.wtf("UserManagerService", "Upgraded user version " + this.mUserVersion + " is higher the SDK's one of 11. Someone forgot to update USER_VERSION?");
        }
        this.mUserVersion = i;
        this.mUserTypeVersion = userTypeVersion;
        if (i3 < i || i4 < userTypeVersion) {
            for (Integer num : arraySet) {
                UserData userDataNoChecks2 = getUserDataNoChecks(num.intValue());
                if (userDataNoChecks2 != null) {
                    writeUserLP(userDataNoChecks2);
                }
            }
            writeUserListLP();
        }
    }

    @GuardedBy({"mUsersLock"})
    public final void upgradeUserTypesLU(List<UserTypeFactory.UserTypeUpgrade> list, ArrayMap<String, UserTypeDetails> arrayMap, int i, Set<Integer> set) {
        for (UserTypeFactory.UserTypeUpgrade userTypeUpgrade : list) {
            if (i <= userTypeUpgrade.getUpToVersion()) {
                for (int i2 = 0; i2 < this.mUsers.size(); i2++) {
                    UserData valueAt = this.mUsers.valueAt(i2);
                    if (userTypeUpgrade.getFromType().equals(valueAt.info.userType)) {
                        UserTypeDetails userTypeDetails = arrayMap.get(userTypeUpgrade.getToType());
                        if (userTypeDetails == null) {
                            throw new IllegalStateException("Upgrade destination user type not defined: " + userTypeUpgrade.getToType());
                        }
                        upgradeProfileToTypeLU(valueAt.info, userTypeDetails);
                        set.add(Integer.valueOf(valueAt.info.id));
                    }
                }
                continue;
            }
        }
    }

    @GuardedBy({"mUsersLock"})
    @VisibleForTesting
    public void upgradeProfileToTypeLU(UserInfo userInfo, UserTypeDetails userTypeDetails) {
        Slog.i("UserManagerService", "Upgrading user " + userInfo.id + " from " + userInfo.userType + " to " + userTypeDetails.getName());
        if (!userInfo.isProfile()) {
            throw new IllegalStateException("Can only upgrade profile types. " + userInfo.userType + " is not a profile type.");
        }
        if (!canAddMoreProfilesToUser(userTypeDetails.getName(), userInfo.profileGroupId, false)) {
            Slog.w("UserManagerService", "Exceeded maximum profiles of type " + userTypeDetails.getName() + " for user " + userInfo.id + ". Maximum allowed= " + userTypeDetails.getMaxAllowedPerParent());
        }
        UserTypeDetails userTypeDetails2 = this.mUserTypes.get(userInfo.userType);
        int defaultUserInfoFlags = userTypeDetails2 != null ? userTypeDetails2.getDefaultUserInfoFlags() : IInstalld.FLAG_USE_QUOTA;
        userInfo.userType = userTypeDetails.getName();
        userInfo.flags = (defaultUserInfoFlags ^ userInfo.flags) | userTypeDetails.getDefaultUserInfoFlags();
        synchronized (this.mRestrictionsLock) {
            if (!BundleUtils.isEmpty(userTypeDetails.getDefaultRestrictions())) {
                Bundle clone = BundleUtils.clone(this.mBaseUserRestrictions.getRestrictions(userInfo.id));
                UserRestrictionsUtils.merge(clone, userTypeDetails.getDefaultRestrictions());
                updateUserRestrictionsInternalLR(clone, userInfo.id);
            }
        }
        userInfo.profileBadge = getFreeProfileBadgeLU(userInfo.profileGroupId, userInfo.userType);
    }

    public final UserInfo getEarliestCreatedFullUser() {
        List<UserInfo> usersInternal = getUsersInternal(true, true, true);
        UserInfo userInfo = null;
        long j = Long.MAX_VALUE;
        for (int i = 0; i < usersInternal.size(); i++) {
            UserInfo userInfo2 = usersInternal.get(i);
            if (userInfo2.isFull() && userInfo2.isAdmin()) {
                long j2 = userInfo2.creationTime;
                if (j2 >= 0 && j2 < j) {
                    userInfo = userInfo2;
                    j = j2;
                }
            }
        }
        return userInfo;
    }

    @GuardedBy({"mPackagesLock"})
    public final void fallbackToSingleUserLP() {
        String[] stringArray;
        String str = isDefaultHeadlessSystemUserMode() ? "android.os.usertype.system.HEADLESS" : "android.os.usertype.full.SYSTEM";
        UserData putUserInfo = putUserInfo(new UserInfo(0, (String) null, (String) null, this.mUserTypes.get(str).getDefaultUserInfoFlags() | 16, str));
        putUserInfo.userProperties = new UserProperties(this.mUserTypes.get(putUserInfo.info.userType).getDefaultUserPropertiesReference());
        this.mNextSerialNumber = 10;
        this.mUserVersion = 11;
        this.mUserTypeVersion = UserTypeFactory.getUserTypeVersion();
        Bundle bundle = new Bundle();
        try {
            for (String str2 : this.mContext.getResources().getStringArray(17236019)) {
                if (UserRestrictionsUtils.isValidRestriction(str2)) {
                    bundle.putBoolean(str2, true);
                }
            }
        } catch (Resources.NotFoundException e) {
            Slog.e("UserManagerService", "Couldn't find resource: config_defaultFirstUserRestrictions", e);
        }
        if (!bundle.isEmpty()) {
            synchronized (this.mRestrictionsLock) {
                this.mBaseUserRestrictions.updateRestrictions(0, bundle);
            }
        }
        initDefaultGuestRestrictions();
        writeUserLP(putUserInfo);
        writeUserListLP();
    }

    public final String getOwnerName() {
        return this.mOwnerName.get();
    }

    public final String getGuestName() {
        return this.mContext.getString(17040411);
    }

    public final void invalidateOwnerNameIfNecessary(Resources resources, boolean z) {
        int updateFrom = this.mLastConfiguration.updateFrom(resources.getConfiguration());
        if (z || (this.mOwnerNameTypedValue.changingConfigurations & updateFrom) != 0) {
            resources.getValue(17040928, this.mOwnerNameTypedValue, true);
            CharSequence coerceToString = this.mOwnerNameTypedValue.coerceToString();
            this.mOwnerName.set(coerceToString != null ? coerceToString.toString() : null);
        }
    }

    public final void scheduleWriteUser(UserData userData) {
        if (this.mHandler.hasMessages(1, userData)) {
            return;
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, userData), 2000L);
    }

    @GuardedBy({"mPackagesLock"})
    public final void writeUserLP(UserData userData) {
        FileOutputStream fileOutputStream;
        File file = this.mUsersDir;
        AtomicFile atomicFile = new AtomicFile(new File(file, userData.info.id + ".xml"));
        try {
            fileOutputStream = atomicFile.startWrite();
        } catch (Exception e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            writeUserLP(userData, fileOutputStream);
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception e2) {
            e = e2;
            Slog.e("UserManagerService", "Error writing user info " + userData.info.id, e);
            atomicFile.failWrite(fileOutputStream);
        }
    }

    @GuardedBy({"mPackagesLock"})
    @VisibleForTesting
    public void writeUserLP(UserData userData, OutputStream outputStream) throws IOException, XmlPullParserException {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
        resolveSerializer.startDocument((String) null, Boolean.TRUE);
        resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        UserInfo userInfo = userData.info;
        resolveSerializer.startTag((String) null, "user");
        resolveSerializer.attributeInt((String) null, "id", userInfo.id);
        resolveSerializer.attributeInt((String) null, "serialNumber", userInfo.serialNumber);
        resolveSerializer.attributeInt((String) null, "flags", userInfo.flags);
        resolveSerializer.attribute((String) null, "type", userInfo.userType);
        resolveSerializer.attributeLong((String) null, "created", userInfo.creationTime);
        resolveSerializer.attributeLong((String) null, "lastLoggedIn", userInfo.lastLoggedInTime);
        String str = userInfo.lastLoggedInFingerprint;
        if (str != null) {
            resolveSerializer.attribute((String) null, "lastLoggedInFingerprint", str);
        }
        resolveSerializer.attributeLong((String) null, "lastEnteredForeground", userData.mLastEnteredForegroundTimeMillis);
        String str2 = userInfo.iconPath;
        if (str2 != null) {
            resolveSerializer.attribute((String) null, "icon", str2);
        }
        if (userInfo.partial) {
            resolveSerializer.attributeBoolean((String) null, "partial", true);
        }
        if (userInfo.preCreated) {
            resolveSerializer.attributeBoolean((String) null, "preCreated", true);
        }
        if (userInfo.convertedFromPreCreated) {
            resolveSerializer.attributeBoolean((String) null, "convertedFromPreCreated", true);
        }
        if (userInfo.guestToRemove) {
            resolveSerializer.attributeBoolean((String) null, "guestToRemove", true);
        }
        int i = userInfo.profileGroupId;
        if (i != -10000) {
            resolveSerializer.attributeInt((String) null, "profileGroupId", i);
        }
        resolveSerializer.attributeInt((String) null, "profileBadge", userInfo.profileBadge);
        int i2 = userInfo.restrictedProfileParentId;
        if (i2 != -10000) {
            resolveSerializer.attributeInt((String) null, "restrictedProfileParentId", i2);
        }
        if (userData.persistSeedData) {
            String str3 = userData.seedAccountName;
            if (str3 != null) {
                resolveSerializer.attribute((String) null, "seedAccountName", str3);
            }
            String str4 = userData.seedAccountType;
            if (str4 != null) {
                resolveSerializer.attribute((String) null, "seedAccountType", str4);
            }
        }
        if (userInfo.name != null) {
            resolveSerializer.startTag((String) null, "name");
            resolveSerializer.text(userInfo.name);
            resolveSerializer.endTag((String) null, "name");
        }
        synchronized (this.mRestrictionsLock) {
            UserRestrictionsUtils.writeRestrictions(resolveSerializer, this.mBaseUserRestrictions.getRestrictions(userInfo.id), "restrictions");
            UserRestrictionsUtils.writeRestrictions(resolveSerializer, this.mDevicePolicyUserRestrictions.getRestrictions(-1), "device_policy_restrictions");
            UserRestrictionsUtils.writeRestrictions(resolveSerializer, this.mDevicePolicyUserRestrictions.getRestrictions(userInfo.id), "device_policy_restrictions");
        }
        if (userData.account != null) {
            resolveSerializer.startTag((String) null, "account");
            resolveSerializer.text(userData.account);
            resolveSerializer.endTag((String) null, "account");
        }
        if (userData.persistSeedData && userData.seedAccountOptions != null) {
            resolveSerializer.startTag((String) null, "seedAccountOptions");
            userData.seedAccountOptions.saveToXml(resolveSerializer);
            resolveSerializer.endTag((String) null, "seedAccountOptions");
        }
        if (userData.userProperties != null) {
            resolveSerializer.startTag((String) null, "userProperties");
            userData.userProperties.writeToXml(resolveSerializer);
            resolveSerializer.endTag((String) null, "userProperties");
        }
        if (userData.getLastRequestQuietModeEnabledMillis() != 0) {
            resolveSerializer.startTag((String) null, "lastRequestQuietModeEnabledCall");
            resolveSerializer.text(String.valueOf(userData.getLastRequestQuietModeEnabledMillis()));
            resolveSerializer.endTag((String) null, "lastRequestQuietModeEnabledCall");
        }
        resolveSerializer.startTag((String) null, "ignorePrepareStorageErrors");
        resolveSerializer.text(String.valueOf(userData.getIgnorePrepareStorageErrors()));
        resolveSerializer.endTag((String) null, "ignorePrepareStorageErrors");
        resolveSerializer.endTag((String) null, "user");
        resolveSerializer.endDocument();
    }

    @GuardedBy({"mPackagesLock"})
    public final void writeUserListLP() {
        int size;
        int[] iArr;
        int i;
        AtomicFile atomicFile = new AtomicFile(this.mUserListFile);
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                resolveSerializer.startTag((String) null, "users");
                resolveSerializer.attributeInt((String) null, "nextSerialNumber", this.mNextSerialNumber);
                resolveSerializer.attributeInt((String) null, "version", this.mUserVersion);
                resolveSerializer.attributeInt((String) null, "userTypeConfigVersion", this.mUserTypeVersion);
                resolveSerializer.startTag((String) null, "guestRestrictions");
                synchronized (this.mGuestRestrictions) {
                    UserRestrictionsUtils.writeRestrictions(resolveSerializer, this.mGuestRestrictions, "restrictions");
                }
                resolveSerializer.endTag((String) null, "guestRestrictions");
                synchronized (this.mUsersLock) {
                    size = this.mUsers.size();
                    iArr = new int[size];
                    for (int i2 = 0; i2 < size; i2++) {
                        iArr[i2] = this.mUsers.valueAt(i2).info.id;
                    }
                }
                for (i = 0; i < size; i++) {
                    int i3 = iArr[i];
                    resolveSerializer.startTag((String) null, "user");
                    resolveSerializer.attributeInt((String) null, "id", i3);
                    resolveSerializer.endTag((String) null, "user");
                }
                resolveSerializer.endTag((String) null, "users");
                resolveSerializer.endDocument();
                atomicFile.finishWrite(startWrite);
            } catch (Exception unused) {
                fileOutputStream = startWrite;
                atomicFile.failWrite(fileOutputStream);
                Slog.e("UserManagerService", "Error writing user list");
            }
        } catch (Exception unused2) {
        }
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0042: MOVE  (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:16:0x0042 */
    @GuardedBy({"mPackagesLock"})
    public final UserData readUserLP(int i) {
        FileInputStream fileInputStream;
        AutoCloseable autoCloseable;
        AutoCloseable autoCloseable2 = null;
        try {
            try {
                fileInputStream = new AtomicFile(new File(this.mUsersDir, Integer.toString(i) + ".xml")).openRead();
                try {
                    UserData readUserLP = readUserLP(i, fileInputStream);
                    IoUtils.closeQuietly(fileInputStream);
                    return readUserLP;
                } catch (IOException unused) {
                    Slog.e("UserManagerService", "Error reading user list");
                    IoUtils.closeQuietly(fileInputStream);
                    return null;
                } catch (XmlPullParserException unused2) {
                    Slog.e("UserManagerService", "Error reading user list");
                    IoUtils.closeQuietly(fileInputStream);
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                autoCloseable2 = autoCloseable;
                IoUtils.closeQuietly(autoCloseable2);
                throw th;
            }
        } catch (IOException unused3) {
            fileInputStream = null;
        } catch (XmlPullParserException unused4) {
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            IoUtils.closeQuietly(autoCloseable2);
            throw th;
        }
    }

    @GuardedBy({"mPackagesLock"})
    @VisibleForTesting
    public UserData readUserLP(int i, InputStream inputStream) throws IOException, XmlPullParserException {
        int next;
        int i2;
        int i3;
        int i4;
        boolean z;
        Bundle bundle;
        Bundle bundle2;
        String str;
        String str2;
        int i5;
        Bundle bundle3;
        boolean z2;
        String str3;
        long j;
        long j2;
        boolean z3;
        Bundle bundle4;
        UserProperties userProperties;
        boolean z4;
        PersistableBundle persistableBundle;
        String str4;
        String str5;
        boolean z5;
        int i6;
        String str6;
        String str7;
        boolean z6;
        long j3;
        long j4;
        int i7;
        int i8;
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        do {
            next = resolvePullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            Slog.e("UserManagerService", "Unable to read user " + i);
            return null;
        }
        if (next != 2 || !resolvePullParser.getName().equals("user")) {
            i2 = i;
            i3 = -10000;
            i4 = -10000;
            z = false;
            bundle = null;
            bundle2 = null;
            str = null;
            str2 = null;
            i5 = 0;
            bundle3 = null;
            z2 = false;
            str3 = null;
            j = 0;
            j2 = 0;
            z3 = false;
            bundle4 = null;
            userProperties = null;
            z4 = true;
            persistableBundle = null;
            str4 = null;
            str5 = null;
            z5 = false;
            i6 = 0;
            str6 = null;
            str7 = null;
            z6 = false;
            j3 = 0;
            j4 = 0;
        } else if (resolvePullParser.getAttributeInt((String) null, "id", -1) != i) {
            Slog.e("UserManagerService", "User id does not match the file name");
            return null;
        } else {
            int attributeInt = resolvePullParser.getAttributeInt((String) null, "serialNumber", i);
            int attributeInt2 = resolvePullParser.getAttributeInt((String) null, "flags", 0);
            String attributeValue = resolvePullParser.getAttributeValue((String) null, "type");
            String intern = attributeValue != null ? attributeValue.intern() : null;
            String attributeValue2 = resolvePullParser.getAttributeValue((String) null, "icon");
            long attributeLong = resolvePullParser.getAttributeLong((String) null, "created", 0L);
            long attributeLong2 = resolvePullParser.getAttributeLong((String) null, "lastLoggedIn", 0L);
            String attributeValue3 = resolvePullParser.getAttributeValue((String) null, "lastLoggedInFingerprint");
            long attributeLong3 = resolvePullParser.getAttributeLong((String) null, "lastEnteredForeground", 0L);
            int attributeInt3 = resolvePullParser.getAttributeInt((String) null, "profileGroupId", -10000);
            int attributeInt4 = resolvePullParser.getAttributeInt((String) null, "profileBadge", 0);
            int attributeInt5 = resolvePullParser.getAttributeInt((String) null, "restrictedProfileParentId", -10000);
            boolean attributeBoolean = resolvePullParser.getAttributeBoolean((String) null, "partial", false);
            boolean attributeBoolean2 = resolvePullParser.getAttributeBoolean((String) null, "preCreated", false);
            boolean attributeBoolean3 = resolvePullParser.getAttributeBoolean((String) null, "convertedFromPreCreated", false);
            boolean attributeBoolean4 = resolvePullParser.getAttributeBoolean((String) null, "guestToRemove", false);
            String attributeValue4 = resolvePullParser.getAttributeValue((String) null, "seedAccountName");
            String attributeValue5 = resolvePullParser.getAttributeValue((String) null, "seedAccountType");
            boolean z7 = (attributeValue4 == null && attributeValue5 == null) ? false : true;
            int depth = resolvePullParser.getDepth();
            long j5 = 0;
            String str8 = null;
            String str9 = null;
            PersistableBundle persistableBundle2 = null;
            UserProperties userProperties2 = null;
            Bundle bundle5 = null;
            Bundle bundle6 = null;
            Bundle bundle7 = null;
            Bundle bundle8 = null;
            boolean z8 = true;
            while (true) {
                int next2 = resolvePullParser.next();
                i8 = attributeInt2;
                if (next2 == 1) {
                    break;
                }
                int i9 = 3;
                if (next2 == 3) {
                    if (resolvePullParser.getDepth() <= depth) {
                        break;
                    }
                    i9 = 3;
                }
                if (next2 != i9 && next2 != 4) {
                    String name = resolvePullParser.getName();
                    if ("name".equals(name)) {
                        if (resolvePullParser.next() == 4) {
                            str8 = resolvePullParser.getText();
                        }
                    } else if ("restrictions".equals(name)) {
                        bundle5 = UserRestrictionsUtils.readRestrictions(resolvePullParser);
                    } else if ("device_policy_restrictions".equals(name)) {
                        bundle6 = UserRestrictionsUtils.readRestrictions(resolvePullParser);
                    } else if ("device_policy_local_restrictions".equals(name)) {
                        bundle7 = UserRestrictionsUtils.readRestrictions(resolvePullParser);
                    } else if ("device_policy_global_restrictions".equals(name)) {
                        bundle8 = UserRestrictionsUtils.readRestrictions(resolvePullParser);
                    } else if ("account".equals(name)) {
                        if (resolvePullParser.next() == 4) {
                            str9 = resolvePullParser.getText();
                        }
                    } else if ("seedAccountOptions".equals(name)) {
                        persistableBundle2 = PersistableBundle.restoreFromXml(resolvePullParser);
                        z7 = true;
                    } else if ("userProperties".equals(name)) {
                        UserTypeDetails userTypeDetails = this.mUserTypes.get(intern);
                        if (userTypeDetails == null) {
                            Slog.e("UserManagerService", "User has properties but no user type!");
                            return null;
                        }
                        userProperties2 = new UserProperties(resolvePullParser, userTypeDetails.getDefaultUserPropertiesReference());
                    } else if ("lastRequestQuietModeEnabledCall".equals(name)) {
                        if (resolvePullParser.next() == 4) {
                            j5 = Long.parseLong(resolvePullParser.getText());
                        }
                    } else if ("ignorePrepareStorageErrors".equals(name) && resolvePullParser.next() == 4) {
                        z8 = Boolean.parseBoolean(resolvePullParser.getText());
                    }
                }
                attributeInt2 = i8;
            }
            z2 = attributeBoolean;
            i4 = attributeInt5;
            str7 = intern;
            j = attributeLong;
            j4 = attributeLong3;
            j3 = j5;
            z3 = attributeBoolean2;
            z = attributeBoolean3;
            z6 = z7;
            z5 = attributeBoolean4;
            str = str8;
            str6 = str9;
            persistableBundle = persistableBundle2;
            bundle2 = bundle6;
            bundle = bundle8;
            z4 = z8;
            str5 = attributeValue5;
            i5 = i8;
            i3 = attributeInt3;
            str4 = attributeValue4;
            str2 = attributeValue2;
            j2 = attributeLong2;
            userProperties = userProperties2;
            bundle4 = bundle5;
            bundle3 = bundle7;
            str3 = attributeValue3;
            i2 = attributeInt;
            i6 = attributeInt4;
        }
        Bundle bundle9 = bundle;
        Bundle bundle10 = bundle2;
        Bundle bundle11 = bundle3;
        UserInfo userInfo = new UserInfo(i, str, str2, i5, str7);
        userInfo.serialNumber = i2;
        userInfo.creationTime = j;
        userInfo.lastLoggedInTime = j2;
        userInfo.lastLoggedInFingerprint = str3;
        userInfo.partial = z2;
        userInfo.preCreated = z3;
        userInfo.convertedFromPreCreated = z;
        userInfo.guestToRemove = z5;
        userInfo.profileGroupId = i3;
        userInfo.profileBadge = i6;
        userInfo.restrictedProfileParentId = i4;
        UserData userData = new UserData();
        userData.info = userInfo;
        userData.account = str6;
        userData.seedAccountName = str4;
        userData.seedAccountType = str5;
        userData.persistSeedData = z6;
        userData.seedAccountOptions = persistableBundle;
        userData.userProperties = userProperties;
        userData.setLastRequestQuietModeEnabledMillis(j3);
        userData.mLastEnteredForegroundTimeMillis = j4;
        if (z4) {
            userData.setIgnorePrepareStorageErrors();
        }
        synchronized (this.mRestrictionsLock) {
            if (bundle4 != null) {
                try {
                    i7 = i;
                    this.mBaseUserRestrictions.updateRestrictions(i7, bundle4);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                i7 = i;
            }
            if (bundle11 != null) {
                this.mDevicePolicyUserRestrictions.updateRestrictions(i7, bundle11);
                if (bundle10 != null) {
                    Slog.wtf("UserManagerService", "Seeing both legacy and current local restrictions in xml");
                }
            } else if (bundle10 != null) {
                this.mDevicePolicyUserRestrictions.updateRestrictions(i7, bundle10);
            }
            if (bundle9 != null) {
                this.mDevicePolicyUserRestrictions.updateRestrictions(-1, bundle9);
            }
        }
        return userData;
    }

    @GuardedBy({"mAppRestrictionsLock"})
    public static boolean cleanAppRestrictionsForPackageLAr(String str, int i) {
        File file = new File(Environment.getUserSystemDirectory(i), packageToRestrictionsFileName(str));
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public UserInfo createProfileForUserWithThrow(String str, String str2, int i, int i2, String[] strArr) throws ServiceSpecificException {
        checkCreateUsersPermission(i);
        try {
            return createUserInternal(str, str2, i, i2, strArr);
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public UserInfo createProfileForUserEvenWhenDisallowedWithThrow(String str, String str2, int i, int i2, String[] strArr) throws ServiceSpecificException {
        checkCreateUsersPermission(i);
        try {
            return createUserInternalUnchecked(str, str2, i, i2, false, strArr, null);
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public UserInfo createUserWithThrow(String str, String str2, int i) throws ServiceSpecificException {
        checkCreateUsersPermission(i);
        try {
            return createUserInternal(str, str2, i, -10000, null);
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public UserInfo preCreateUserWithThrow(String str) throws ServiceSpecificException {
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        int defaultUserInfoFlags = userTypeDetails != null ? userTypeDetails.getDefaultUserInfoFlags() : 0;
        checkCreateUsersPermission(defaultUserInfoFlags);
        boolean isUserTypeEligibleForPreCreation = isUserTypeEligibleForPreCreation(userTypeDetails);
        Preconditions.checkArgument(isUserTypeEligibleForPreCreation, "cannot pre-create user of type " + str);
        Slog.i("UserManagerService", "Pre-creating user of type " + str);
        try {
            return createUserInternalUnchecked(null, str, defaultUserInfoFlags, -10000, true, null, null);
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public UserHandle createUserWithAttributes(String str, String str2, int i, Bitmap bitmap, String str3, String str4, PersistableBundle persistableBundle) throws ServiceSpecificException {
        checkCreateUsersPermission(i);
        if (someUserHasAccountNoChecks(str3, str4)) {
            throw new ServiceSpecificException(7);
        }
        try {
            UserInfo createUserInternal = createUserInternal(str, str2, i, -10000, null);
            if (bitmap != null) {
                this.mLocalService.setUserIcon(createUserInternal.id, bitmap);
            }
            setSeedAccountDataNoChecks(createUserInternal.id, str3, str4, persistableBundle, true);
            return createUserInternal.getUserHandle();
        } catch (UserManager.CheckedUserOperationException e) {
            throw e.toServiceSpecificException();
        }
    }

    public final UserInfo createUserInternal(String str, String str2, int i, int i2, String[] strArr) throws UserManager.CheckedUserOperationException {
        String str3;
        if (UserManager.isUserTypeCloneProfile(str2)) {
            str3 = "no_add_clone_profile";
        } else {
            str3 = UserManager.isUserTypeManagedProfile(str2) ? "no_add_managed_profile" : "no_add_user";
        }
        enforceUserRestriction(str3, UserHandle.getCallingUserId(), "Cannot add user");
        return createUserInternalUnchecked(str, str2, i, i2, false, strArr, null);
    }

    public final UserInfo createUserInternalUnchecked(String str, String str2, int i, int i2, boolean z, String[] strArr, Object obj) throws UserManager.CheckedUserOperationException {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("createUser-" + i);
        long logUserCreateJourneyBegin = logUserCreateJourneyBegin(-1);
        try {
            UserInfo createUserInternalUncheckedNoTracing = createUserInternalUncheckedNoTracing(str, str2, i, i2, z, strArr, timingsTraceAndSlog, obj);
            logUserCreateJourneyFinish(logUserCreateJourneyBegin, createUserInternalUncheckedNoTracing != null ? createUserInternalUncheckedNoTracing.id : -1, str2, i, createUserInternalUncheckedNoTracing != null);
            timingsTraceAndSlog.traceEnd();
            return createUserInternalUncheckedNoTracing;
        } catch (Throwable th) {
            logUserCreateJourneyFinish(logUserCreateJourneyBegin, -1, str2, i, false);
            timingsTraceAndSlog.traceEnd();
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r23v0, types: [com.android.server.pm.UserManagerService] */
    /* JADX WARN: Type inference failed for: r8v13 */
    /* JADX WARN: Type inference failed for: r8v2, types: [int] */
    /* JADX WARN: Type inference failed for: r8v20 */
    /* JADX WARN: Type inference failed for: r8v21 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Type inference failed for: r8v7 */
    /* JADX WARN: Type inference failed for: r8v8 */
    public final UserInfo createUserInternalUncheckedNoTracing(String str, String str2, int i, int i2, boolean z, String[] strArr, TimingsTraceAndSlog timingsTraceAndSlog, Object obj) throws UserManager.CheckedUserOperationException {
        ?? r8;
        UserData userDataLU;
        int nextAvailableId;
        UserInfo userInfo;
        int i3;
        UserData userData;
        UserInfo userInfo2;
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str2);
        if (userTypeDetails == null) {
            throwCheckedUserOperationException("Cannot create user of invalid user type: " + str2, 1);
        }
        String intern = str2.intern();
        int defaultUserInfoFlags = i | userTypeDetails.getDefaultUserInfoFlags();
        if (!checkUserTypeConsistency(defaultUserInfoFlags)) {
            throwCheckedUserOperationException("Cannot add user. Flags (" + Integer.toHexString(defaultUserInfoFlags) + ") and userTypeDetails (" + intern + ") are inconsistent.", 1);
        }
        if ((defaultUserInfoFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
            throwCheckedUserOperationException("Cannot add user. Flags (" + Integer.toHexString(defaultUserInfoFlags) + ") indicated SYSTEM user, which cannot be created.", 1);
        }
        if (!isUserTypeEnabled(userTypeDetails)) {
            throwCheckedUserOperationException("Cannot add a user of disabled type " + intern + ".", 6);
        }
        synchronized (this.mUsersLock) {
            r8 = defaultUserInfoFlags;
            if (this.mForceEphemeralUsers) {
                r8 = defaultUserInfoFlags | 256;
            }
        }
        if (!z && i2 < 0 && isUserTypeEligibleForPreCreation(userTypeDetails)) {
            UserInfo convertPreCreatedUserIfPossible = convertPreCreatedUserIfPossible(intern, r8, str, obj);
            if (convertPreCreatedUserIfPossible != null) {
                return convertPreCreatedUserIfPossible;
            }
        }
        if (((DeviceStorageMonitorInternal) LocalServices.getService(DeviceStorageMonitorInternal.class)).isMemoryLow()) {
            throwCheckedUserOperationException("Cannot add user. Not enough space on disk.", 5);
        }
        boolean isProfile = userTypeDetails.isProfile();
        boolean isUserTypeGuest = UserManager.isUserTypeGuest(intern);
        boolean isUserTypeRestricted = UserManager.isUserTypeRestricted(intern);
        boolean isUserTypeDemo = UserManager.isUserTypeDemo(intern);
        UserManager.isUserTypeManagedProfile(intern);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Object obj2 = this.mPackagesLock;
            synchronized (obj2) {
                try {
                    if (i2 != -10000) {
                        try {
                            synchronized (this.mUsersLock) {
                                userDataLU = getUserDataLU(i2);
                            }
                            if (userDataLU == null) {
                                throwCheckedUserOperationException("Cannot find user data for parent user " + i2, 1);
                            }
                        } catch (Throwable th) {
                            th = th;
                            r8 = obj2;
                            throw th;
                        }
                    } else {
                        userDataLU = null;
                    }
                    UserData userData2 = userDataLU;
                    if (!z && !canAddMoreUsersOfType(userTypeDetails)) {
                        throwCheckedUserOperationException("Cannot add more users of type " + intern + ". Maximum number of that type already exists.", 6);
                    }
                    if (!isUserTypeGuest && !isProfile && !isUserTypeDemo && isUserLimitReached()) {
                        throwCheckedUserOperationException("Cannot add user. Maximum user limit is reached.", 6);
                    }
                    if (isProfile && !canAddMoreProfilesToUser(intern, i2, false)) {
                        throwCheckedUserOperationException("Cannot add more profiles of type " + intern + " for user " + i2, 6);
                    }
                    if (isUserTypeRestricted && i2 != 0 && !isCreationOverrideEnabled()) {
                        throwCheckedUserOperationException("Cannot add restricted profile - parent user must be system", 1);
                    }
                    nextAvailableId = getNextAvailableId();
                    Slog.i("UserManagerService", "Creating user " + nextAvailableId + " of type " + intern);
                    Environment.getUserSystemDirectory(nextAvailableId).mkdirs();
                    synchronized (this.mUsersLock) {
                        try {
                            if (userData2 != null) {
                                try {
                                    if (userData2.info.isEphemeral()) {
                                        r8 |= 256;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    throw th;
                                }
                            }
                            if (z) {
                                r8 = (r8 == true ? 1 : 0) & 65279;
                            }
                            boolean z2 = (r8 == true ? 1 : 0) & true;
                            int i4 = r8;
                            if (z2) {
                                i4 = (r8 == true ? 1 : 0) | IInstalld.FLAG_FORCE;
                            }
                            userInfo = new UserInfo(nextAvailableId, str, (String) null, i4, intern);
                            int i5 = this.mNextSerialNumber;
                            this.mNextSerialNumber = i5 + 1;
                            userInfo.serialNumber = i5;
                            userInfo.creationTime = getCreationTime();
                            userInfo.partial = true;
                            userInfo.preCreated = z;
                            userInfo.lastLoggedInFingerprint = PackagePartitions.FINGERPRINT;
                            if (userTypeDetails.hasBadge()) {
                                i3 = -10000;
                                if (i2 != -10000) {
                                    userInfo.profileBadge = getFreeProfileBadgeLU(i2, intern);
                                }
                            } else {
                                i3 = -10000;
                            }
                            userData = new UserData();
                            userData.info = userInfo;
                            userData.userProperties = new UserProperties(userTypeDetails.getDefaultUserPropertiesReference());
                            this.mUsers.put(nextAvailableId, userData);
                            writeUserLP(userData);
                            writeUserListLP();
                            if (userData2 != null) {
                                if (isProfile) {
                                    UserInfo userInfo3 = userData2.info;
                                    if (userInfo3.profileGroupId == i3) {
                                        userInfo3.profileGroupId = userInfo3.id;
                                        writeUserLP(userData2);
                                    }
                                    userInfo.profileGroupId = userData2.info.profileGroupId;
                                } else if (isUserTypeRestricted) {
                                    UserInfo userInfo4 = userData2.info;
                                    if (userInfo4.restrictedProfileParentId == i3) {
                                        userInfo4.restrictedProfileParentId = userInfo4.id;
                                        writeUserLP(userData2);
                                    }
                                    userInfo.restrictedProfileParentId = userData2.info.restrictedProfileParentId;
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                        }
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }
            timingsTraceAndSlog.traceBegin("createUserKey");
            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).createUserKey(nextAvailableId, userInfo.serialNumber, userInfo.isEphemeral());
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("prepareUserData");
            this.mUserDataPreparer.prepareUserData(nextAvailableId, userInfo.serialNumber, 1);
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("LSS.createNewUser");
            this.mLockPatternUtils.createNewUser(nextAvailableId, userInfo.serialNumber);
            timingsTraceAndSlog.traceEnd();
            Set<String> installablePackagesForUserType = this.mSystemPackageInstaller.getInstallablePackagesForUserType(intern);
            timingsTraceAndSlog.traceBegin("PM.createNewUser");
            this.mPm.createNewUser(nextAvailableId, installablePackagesForUserType, strArr);
            timingsTraceAndSlog.traceEnd();
            userInfo.partial = false;
            synchronized (this.mPackagesLock) {
                writeUserLP(userData);
            }
            updateUserIds();
            Bundle bundle = new Bundle();
            if (isUserTypeGuest) {
                synchronized (this.mGuestRestrictions) {
                    bundle.putAll(this.mGuestRestrictions);
                }
            } else {
                userTypeDetails.addDefaultRestrictionsTo(bundle);
            }
            synchronized (this.mRestrictionsLock) {
                this.mBaseUserRestrictions.updateRestrictions(nextAvailableId, bundle);
            }
            timingsTraceAndSlog.traceBegin("PM.onNewUserCreated-" + nextAvailableId);
            this.mPm.onNewUserCreated(nextAvailableId, false);
            timingsTraceAndSlog.traceEnd();
            applyDefaultUserSettings(userTypeDetails, nextAvailableId);
            setDefaultCrossProfileIntentFilters(nextAvailableId, userTypeDetails, bundle, i2);
            if (z) {
                Slog.i("UserManagerService", "starting pre-created user " + userInfo.toFullString());
                try {
                    ActivityManager.getService().startUserInBackground(nextAvailableId);
                } catch (RemoteException e) {
                    Slog.w("UserManagerService", "could not start pre-created user " + nextAvailableId, e);
                }
                userInfo2 = userInfo;
            } else {
                userInfo2 = userInfo;
                dispatchUserAdded(userInfo2, obj);
            }
            return userInfo2;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void applyDefaultUserSettings(UserTypeDetails userTypeDetails, int i) {
        Bundle defaultSystemSettings = userTypeDetails.getDefaultSystemSettings();
        Bundle defaultSecureSettings = userTypeDetails.getDefaultSecureSettings();
        if (defaultSystemSettings.isEmpty() && defaultSecureSettings.isEmpty()) {
            return;
        }
        int size = defaultSystemSettings.size();
        String[] strArr = (String[]) defaultSystemSettings.keySet().toArray(new String[size]);
        for (int i2 = 0; i2 < size; i2++) {
            String str = strArr[i2];
            if (!Settings.System.putStringForUser(this.mContext.getContentResolver(), str, defaultSystemSettings.getString(str), i)) {
                Slog.e("UserManagerService", "Failed to insert default system setting: " + str);
            }
        }
        int size2 = defaultSecureSettings.size();
        String[] strArr2 = (String[]) defaultSecureSettings.keySet().toArray(new String[size2]);
        for (int i3 = 0; i3 < size2; i3++) {
            String str2 = strArr2[i3];
            if (!Settings.Secure.putStringForUser(this.mContext.getContentResolver(), str2, defaultSecureSettings.getString(str2), i)) {
                Slog.e("UserManagerService", "Failed to insert default secure setting: " + str2);
            }
        }
    }

    public final void setDefaultCrossProfileIntentFilters(int i, UserTypeDetails userTypeDetails, Bundle bundle, int i2) {
        if (userTypeDetails == null || !userTypeDetails.isProfile() || userTypeDetails.getDefaultCrossProfileIntentFilters().isEmpty()) {
            return;
        }
        boolean z = bundle.getBoolean("no_sharing_into_profile", false);
        int size = userTypeDetails.getDefaultCrossProfileIntentFilters().size();
        for (int i3 = 0; i3 < size; i3++) {
            DefaultCrossProfileIntentFilter defaultCrossProfileIntentFilter = userTypeDetails.getDefaultCrossProfileIntentFilters().get(i3);
            if (!z || !defaultCrossProfileIntentFilter.letsPersonalDataIntoProfile) {
                if (defaultCrossProfileIntentFilter.direction == 0) {
                    PackageManagerService packageManagerService = this.mPm;
                    packageManagerService.addCrossProfileIntentFilter(packageManagerService.snapshotComputer(), defaultCrossProfileIntentFilter.filter, this.mContext.getOpPackageName(), i, i2, defaultCrossProfileIntentFilter.flags);
                } else {
                    PackageManagerService packageManagerService2 = this.mPm;
                    packageManagerService2.addCrossProfileIntentFilter(packageManagerService2.snapshotComputer(), defaultCrossProfileIntentFilter.filter, this.mContext.getOpPackageName(), i2, i, defaultCrossProfileIntentFilter.flags);
                }
            }
        }
    }

    public final UserInfo convertPreCreatedUserIfPossible(String str, int i, String str2, final Object obj) {
        UserData preCreatedUserLU;
        synchronized (this.mUsersLock) {
            preCreatedUserLU = getPreCreatedUserLU(str);
        }
        if (preCreatedUserLU == null) {
            return null;
        }
        synchronized (this.mUserStates) {
            if (this.mUserStates.has(preCreatedUserLU.info.id)) {
                Slog.w("UserManagerService", "Cannot reuse pre-created user " + preCreatedUserLU.info.id + " because it didn't stop yet");
                return null;
            }
            final UserInfo userInfo = preCreatedUserLU.info;
            int i2 = userInfo.flags | i;
            if (!checkUserTypeConsistency(i2)) {
                Slog.wtf("UserManagerService", "Cannot reuse pre-created user " + userInfo.id + " of type " + str + " because flags are inconsistent. Flags (" + Integer.toHexString(i) + "); preCreatedUserFlags ( " + Integer.toHexString(userInfo.flags) + ").");
                return null;
            }
            Slog.i("UserManagerService", "Reusing pre-created user " + userInfo.id + " of type " + str + " and bestowing on it flags " + UserInfo.flagsToString(i));
            userInfo.name = str2;
            userInfo.flags = i2;
            userInfo.preCreated = false;
            userInfo.convertedFromPreCreated = true;
            userInfo.creationTime = getCreationTime();
            synchronized (this.mPackagesLock) {
                writeUserLP(preCreatedUserLU);
                writeUserListLP();
            }
            updateUserIds();
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda1
                public final void runOrThrow() {
                    UserManagerService.this.lambda$convertPreCreatedUserIfPossible$2(userInfo, obj);
                }
            });
            return userInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$convertPreCreatedUserIfPossible$2(UserInfo userInfo, Object obj) throws Exception {
        this.mPm.onNewUserCreated(userInfo.id, true);
        dispatchUserAdded(userInfo, obj);
        VoiceInteractionManagerInternal voiceInteractionManagerInternal = (VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class);
        if (voiceInteractionManagerInternal != null) {
            voiceInteractionManagerInternal.onPreCreatedUserConversion(userInfo.id);
        }
    }

    @VisibleForTesting
    public static boolean checkUserTypeConsistency(int i) {
        return isAtMostOneFlag(i & 4620) && isAtMostOneFlag(i & 5120) && isAtMostOneFlag(i & 6144);
    }

    public boolean installWhitelistedSystemPackages(boolean z, boolean z2, ArraySet<String> arraySet) {
        return this.mSystemPackageInstaller.installWhitelistedSystemPackages(z || this.mUpdatingSystemUserMode, z2, arraySet);
    }

    public String[] getPreInstallableSystemPackages(String str) {
        checkCreateUsersPermission("getPreInstallableSystemPackages");
        Set<String> installablePackagesForUserType = this.mSystemPackageInstaller.getInstallablePackagesForUserType(str);
        if (installablePackagesForUserType == null) {
            return null;
        }
        return (String[]) installablePackagesForUserType.toArray(new String[installablePackagesForUserType.size()]);
    }

    public final long getCreationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > 946080000000L) {
            return currentTimeMillis;
        }
        return 0L;
    }

    public final void dispatchUserAdded(UserInfo userInfo, Object obj) {
        String str;
        synchronized (this.mUserLifecycleListeners) {
            for (int i = 0; i < this.mUserLifecycleListeners.size(); i++) {
                this.mUserLifecycleListeners.get(i).onUserCreated(userInfo, obj);
            }
        }
        Intent intent = new Intent("android.intent.action.USER_ADDED");
        intent.addFlags(16777216);
        intent.addFlags(67108864);
        intent.putExtra("android.intent.extra.user_handle", userInfo.id);
        intent.putExtra("android.intent.extra.USER", UserHandle.of(userInfo.id));
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.MANAGE_USERS");
        Context context = this.mContext;
        if (userInfo.isGuest()) {
            str = "users_guest_created";
        } else {
            str = userInfo.isDemo() ? "users_demo_created" : "users_user_created";
        }
        MetricsLogger.count(context, str, 1);
        if (userInfo.isProfile()) {
            sendProfileAddedBroadcast(userInfo.profileGroupId, userInfo.id);
        } else if (Settings.Global.getString(this.mContext.getContentResolver(), "user_switcher_enabled") == null) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "user_switcher_enabled", 1);
        }
    }

    @GuardedBy({"mUsersLock"})
    public final UserData getPreCreatedUserLU(String str) {
        int size = this.mUsers.size();
        for (int i = 0; i < size; i++) {
            UserData valueAt = this.mUsers.valueAt(i);
            UserInfo userInfo = valueAt.info;
            if (userInfo.preCreated && !userInfo.partial && userInfo.userType.equals(str)) {
                if (valueAt.info.isInitialized()) {
                    return valueAt;
                }
                Slog.w("UserManagerService", "found pre-created user of type " + str + ", but it's not initialized yet: " + valueAt.info.toFullString());
            }
        }
        return null;
    }

    public static boolean isUserTypeEligibleForPreCreation(UserTypeDetails userTypeDetails) {
        return (userTypeDetails == null || userTypeDetails.isProfile() || userTypeDetails.getName().equals("android.os.usertype.full.RESTRICTED")) ? false : true;
    }

    public final long logUserCreateJourneyBegin(int i) {
        return logUserJourneyBegin(4, i);
    }

    public final void logUserCreateJourneyFinish(long j, int i, String str, int i2, boolean z) {
        logUserJourneyFinish(j, 4, i, str, i2, z);
    }

    public final long logUserRemoveJourneyBegin(int i) {
        return logUserJourneyBegin(6, i);
    }

    public final void logUserRemoveJourneyFinish(long j, int i, String str, int i2, boolean z) {
        logUserJourneyFinish(j, 6, i, str, i2, z);
    }

    public final long logGrantAdminJourneyBegin(int i) {
        return logUserJourneyBegin(7, i);
    }

    public final void logGrantAdminJourneyFinish(long j, int i, String str, int i2) {
        logUserJourneyFinish(j, 7, i, str, i2, true);
    }

    public final long logRevokeAdminJourneyBegin(int i) {
        return logUserJourneyBegin(8, i);
    }

    public final void logRevokeAdminJourneyFinish(long j, int i, String str, int i2) {
        logUserJourneyFinish(j, 8, i, str, i2, true);
    }

    public final void logUserJourneyFinish(long j, int i, int i2, String str, int i3, boolean z) {
        int i4;
        FrameworkStatsLog.write(264, j, i, getCurrentUserId(), i2, UserManager.getUserTypeForStatsd(str), i3);
        if (i != 4) {
            i4 = 8;
            if (i != 6) {
                if (i == 7) {
                    i4 = 9;
                } else if (i != 8) {
                    throw new IllegalArgumentException("Journey " + i + " not expected.");
                } else {
                    i4 = 10;
                }
            }
        } else {
            i4 = 3;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, j, i2, i4, z ? 2 : 0);
    }

    public final long logUserJourneyBegin(int i, int i2) {
        int i3;
        int i4;
        long nextLong = ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE);
        if (i == 4) {
            i3 = 3;
        } else if (i == 6) {
            i4 = 8;
            FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, nextLong, i2, i4, 1);
            return nextLong;
        } else if (i == 7) {
            i3 = 9;
        } else if (i != 8) {
            throw new IllegalArgumentException("Journey " + i + " not expected.");
        } else {
            i3 = 10;
        }
        i4 = i3;
        FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, nextLong, i2, i4, 1);
        return nextLong;
    }

    public final void logUserJourneyError(long j, int i, int i2) {
        int i3;
        FrameworkStatsLog.write(264, j, i, getCurrentUserId(), i2);
        if (i == 7) {
            i3 = 9;
        } else if (i != 8) {
            throw new IllegalArgumentException("Journey " + i + " not expected.");
        } else {
            i3 = 10;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LIFECYCLE_EVENT_OCCURRED, j, i2, i3, 4);
    }

    public final void registerStatsCallbacks() {
        StatsManager statsManager = (StatsManager) this.mContext.getSystemService(StatsManager.class);
        statsManager.setPullAtomCallback((int) FrameworkStatsLog.USER_INFO, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda4
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = UserManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
        statsManager.setPullAtomCallback((int) FrameworkStatsLog.MULTI_USER_INFO, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda4
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = UserManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
    }

    public final int onPullAtom(int i, List<StatsEvent> list) {
        boolean z;
        int i2 = -1;
        boolean z2 = true;
        if (i != 10152) {
            if (i == 10160) {
                if (UserManager.getMaxSupportedUsers() > 1) {
                    list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.MULTI_USER_INFO, UserManager.getMaxSupportedUsers(), isUserSwitcherEnabled(-1), (!UserManager.supportsMultipleUsers() || hasUserRestriction("no_add_user", -1)) ? false : false));
                    return 0;
                }
                return 0;
            }
            Slogf.m24e("UserManagerService", "Unexpected atom tag: %d", Integer.valueOf(i));
            return 1;
        }
        List<UserInfo> usersInternal = getUsersInternal(true, true, true);
        int size = usersInternal.size();
        if (size > 1) {
            int i3 = 0;
            while (i3 < size) {
                UserInfo userInfo = usersInternal.get(i3);
                int userTypeForStatsd = UserManager.getUserTypeForStatsd(userInfo.userType);
                String str = userTypeForStatsd == 0 ? userInfo.userType : null;
                synchronized (this.mUserStates) {
                    z = this.mUserStates.get(userInfo.id, i2) == 3;
                }
                list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.USER_INFO, userInfo.id, userTypeForStatsd, str, userInfo.flags, userInfo.creationTime, userInfo.lastLoggedInTime, z));
                i3++;
                i2 = -1;
            }
            return 0;
        }
        return 0;
    }

    @VisibleForTesting
    public UserData putUserInfo(UserInfo userInfo) {
        UserData userData = new UserData();
        userData.info = userInfo;
        synchronized (this.mUsersLock) {
            this.mUsers.put(userInfo.id, userData);
        }
        updateUserIds();
        return userData;
    }

    @VisibleForTesting
    public void removeUserInfo(int i) {
        synchronized (this.mUsersLock) {
            this.mUsers.remove(i);
        }
    }

    public UserInfo createRestrictedProfileWithThrow(String str, int i) throws ServiceSpecificException {
        checkCreateUsersPermission("setupRestrictedProfile");
        UserInfo createProfileForUserWithThrow = createProfileForUserWithThrow(str, "android.os.usertype.full.RESTRICTED", 0, i, null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            setUserRestriction("no_modify_accounts", true, createProfileForUserWithThrow.id);
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", 0, createProfileForUserWithThrow.id);
            setUserRestriction("no_share_location", true, createProfileForUserWithThrow.id);
            return createProfileForUserWithThrow;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<UserInfo> getGuestUsers() {
        checkManageUsersPermission("getGuestUsers");
        ArrayList arrayList = new ArrayList();
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = this.mUsers.valueAt(i).info;
                if (userInfo.isGuest() && !userInfo.guestToRemove && !userInfo.preCreated && !this.mRemovingUserIds.get(userInfo.id)) {
                    arrayList.add(userInfo);
                }
            }
        }
        return arrayList;
    }

    public boolean markGuestForDeletion(int i) {
        checkManageUsersPermission("Only the system can remove users");
        if (getUserRestrictions(UserHandle.getCallingUserId()).getBoolean("no_remove_user", false)) {
            Slog.w("UserManagerService", "Cannot remove user. DISALLOW_REMOVE_USER is enabled.");
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mPackagesLock) {
                synchronized (this.mUsersLock) {
                    UserData userData = this.mUsers.get(i);
                    if (i != 0 && userData != null && !this.mRemovingUserIds.get(i)) {
                        if (userData.info.isGuest()) {
                            UserInfo userInfo = userData.info;
                            userInfo.guestToRemove = true;
                            userInfo.flags |= 64;
                            writeUserLP(userData);
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean removeUser(int i) {
        Slog.i("UserManagerService", "removeUser u" + i);
        checkCreateUsersPermission("Only the system can remove users");
        String userRemovalRestriction = getUserRemovalRestriction(i);
        if (getUserRestrictions(UserHandle.getCallingUserId()).getBoolean(userRemovalRestriction, false)) {
            Slog.w("UserManagerService", "Cannot remove user. " + userRemovalRestriction + " is enabled.");
            return false;
        }
        return removeUserWithProfilesUnchecked(i);
    }

    public final boolean removeUserWithProfilesUnchecked(int i) {
        int[] profileIds;
        UserInfo userInfoNoChecks = getUserInfoNoChecks(i);
        if (userInfoNoChecks == null) {
            Slog.e("UserManagerService", TextUtils.formatSimple("Cannot remove user %d, invalid user id provided.", new Object[]{Integer.valueOf(i)}));
            return false;
        }
        if (!userInfoNoChecks.isProfile()) {
            for (int i2 : getProfileIds(i, false)) {
                if (i2 != i) {
                    Slog.i("UserManagerService", "removing profile:" + i2 + "associated with user:" + i);
                    if (removeUserUnchecked(i2)) {
                        continue;
                    } else {
                        Slog.i("UserManagerService", "Unable to immediately remove profile " + i2 + "associated with user " + i + ". User is set as ephemeral and will be removed on user switch or reboot.");
                        synchronized (this.mPackagesLock) {
                            UserData userDataNoChecks = getUserDataNoChecks(i);
                            userDataNoChecks.info.flags |= 256;
                            writeUserLP(userDataNoChecks);
                        }
                    }
                }
            }
        }
        return removeUserUnchecked(i);
    }

    public boolean removeUserEvenWhenDisallowed(int i) {
        checkCreateUsersPermission("Only the system can remove users");
        return removeUserWithProfilesUnchecked(i);
    }

    public final String getUserRemovalRestriction(int i) {
        UserInfo userInfoLU;
        synchronized (this.mUsersLock) {
            userInfoLU = getUserInfoLU(i);
        }
        return userInfoLU != null && userInfoLU.isManagedProfile() ? "no_remove_managed_profile" : "no_remove_user";
    }

    public final boolean removeUserUnchecked(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Pair<Integer, Integer> currentAndTargetUserIds = getCurrentAndTargetUserIds();
            if (i == ((Integer) currentAndTargetUserIds.first).intValue()) {
                Slog.w("UserManagerService", "Current user cannot be removed.");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            } else if (i == ((Integer) currentAndTargetUserIds.second).intValue()) {
                Slog.w("UserManagerService", "Target user of an ongoing user switch cannot be removed.");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            } else {
                synchronized (this.mPackagesLock) {
                    synchronized (this.mUsersLock) {
                        final UserData userData = this.mUsers.get(i);
                        if (i == 0) {
                            Slog.e("UserManagerService", "System user cannot be removed.");
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return false;
                        } else if (userData == null) {
                            Slog.e("UserManagerService", TextUtils.formatSimple("Cannot remove user %d, invalid user id provided.", new Object[]{Integer.valueOf(i)}));
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return false;
                        } else if (isNonRemovableMainUser(userData.info)) {
                            Slog.e("UserManagerService", "Main user cannot be removed when it's a permanent admin user.");
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return false;
                        } else if (this.mRemovingUserIds.get(i)) {
                            Slog.e("UserManagerService", TextUtils.formatSimple("User %d is already scheduled for removal.", new Object[]{Integer.valueOf(i)}));
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return false;
                        } else {
                            Slog.i("UserManagerService", "Removing user " + i);
                            addRemovingUserIdLocked(i);
                            UserInfo userInfo = userData.info;
                            userInfo.partial = true;
                            userInfo.flags |= 64;
                            writeUserLP(userData);
                            final long logUserRemoveJourneyBegin = logUserRemoveJourneyBegin(i);
                            try {
                                this.mAppOpsService.removeUser(i);
                            } catch (RemoteException e) {
                                Slog.w("UserManagerService", "Unable to notify AppOpsService of removing user.", e);
                            }
                            UserInfo userInfo2 = userData.info;
                            if (userInfo2.profileGroupId != -10000 && userInfo2.isProfile()) {
                                UserInfo userInfo3 = userData.info;
                                sendProfileRemovedBroadcast(userInfo3.profileGroupId, userInfo3.id, userInfo3.userType);
                            }
                            try {
                                boolean z = ActivityManager.getService().stopUser(i, true, new IStopUserCallback.Stub() { // from class: com.android.server.pm.UserManagerService.5
                                    public void userStopped(int i2) {
                                        UserManagerService.this.finishRemoveUser(i2);
                                        UserManagerService userManagerService = UserManagerService.this;
                                        long j = logUserRemoveJourneyBegin;
                                        UserInfo userInfo4 = userData.info;
                                        userManagerService.logUserRemoveJourneyFinish(j, i2, userInfo4.userType, userInfo4.flags, true);
                                    }

                                    public void userStopAborted(int i2) {
                                        UserManagerService userManagerService = UserManagerService.this;
                                        long j = logUserRemoveJourneyBegin;
                                        UserInfo userInfo4 = userData.info;
                                        userManagerService.logUserRemoveJourneyFinish(j, i2, userInfo4.userType, userInfo4.flags, false);
                                    }
                                }) == 0;
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                return z;
                            } catch (RemoteException e2) {
                                Slog.w("UserManagerService", "Failed to stop user during removal.", e2);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @GuardedBy({"mUsersLock"})
    @VisibleForTesting
    public void addRemovingUserIdLocked(int i) {
        this.mRemovingUserIds.put(i, true);
        this.mRecentlyRemovedIds.add(Integer.valueOf(i));
        if (this.mRecentlyRemovedIds.size() > 100) {
            this.mRecentlyRemovedIds.removeFirst();
        }
    }

    public int removeUserWhenPossible(int i, boolean z) {
        checkCreateUsersPermission("Only the system can remove users");
        if (!z) {
            String userRemovalRestriction = getUserRemovalRestriction(i);
            if (getUserRestrictions(UserHandle.getCallingUserId()).getBoolean(userRemovalRestriction, false)) {
                Slog.w("UserManagerService", "Cannot remove user. " + userRemovalRestriction + " is enabled.");
                return -2;
            }
        }
        if (i == 0) {
            Slog.e("UserManagerService", "System user cannot be removed.");
            return -4;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mPackagesLock) {
                synchronized (this.mUsersLock) {
                    UserData userData = this.mUsers.get(i);
                    if (userData == null) {
                        Slog.e("UserManagerService", "Cannot remove user " + i + ", invalid user id provided.");
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return -3;
                    } else if (isNonRemovableMainUser(userData.info)) {
                        Slog.e("UserManagerService", "Main user cannot be removed when it's a permanent admin user.");
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return -5;
                    } else if (this.mRemovingUserIds.get(i)) {
                        Slog.e("UserManagerService", "User " + i + " is already scheduled for removal.");
                        return 2;
                    } else {
                        Pair<Integer, Integer> currentAndTargetUserIds = getCurrentAndTargetUserIds();
                        if (i == ((Integer) currentAndTargetUserIds.first).intValue() || i == ((Integer) currentAndTargetUserIds.second).intValue() || !removeUserWithProfilesUnchecked(i)) {
                            Object[] objArr = new Object[3];
                            objArr[0] = Integer.valueOf(i);
                            objArr[1] = i == ((Integer) currentAndTargetUserIds.first).intValue() ? "current user" : "target user of an ongoing user switch";
                            objArr[2] = Integer.valueOf(i);
                            Slog.i("UserManagerService", TextUtils.formatSimple("Unable to immediately remove user %d (%s is %d). User is set as ephemeral and will be removed on user switch or reboot.", objArr));
                            userData.info.flags |= 256;
                            writeUserLP(userData);
                            return 1;
                        }
                        return 0;
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void finishRemoveUser(int i) {
        UserInfo userInfoLU;
        Slog.i("UserManagerService", "finishRemoveUser " + i);
        synchronized (this.mUsersLock) {
            userInfoLU = getUserInfoLU(i);
        }
        if (userInfoLU != null && userInfoLU.preCreated) {
            Slog.i("UserManagerService", "Removing a pre-created user with user id: " + i);
            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).onUserStopped(i);
            removeUserState(i);
            return;
        }
        synchronized (this.mUserLifecycleListeners) {
            for (int i2 = 0; i2 < this.mUserLifecycleListeners.size(); i2++) {
                this.mUserLifecycleListeners.get(i2).onUserRemoved(userInfoLU);
            }
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Intent intent = new Intent("android.intent.action.USER_REMOVED");
            intent.addFlags(16777216);
            intent.putExtra("android.intent.extra.user_handle", i);
            intent.putExtra("android.intent.extra.USER", UserHandle.of(i));
            getActivityManagerInternal().broadcastIntentWithCallback(intent, new C13736(i), new String[]{"android.permission.MANAGE_USERS"}, -1, (int[]) null, (BiFunction) null, (Bundle) null);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* renamed from: com.android.server.pm.UserManagerService$6 */
    /* loaded from: classes2.dex */
    public class C13736 extends IIntentReceiver.Stub {
        public final /* synthetic */ int val$userId;

        public C13736(int i) {
            this.val$userId = i;
        }

        public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
            final int i3 = this.val$userId;
            new Thread(new Runnable() { // from class: com.android.server.pm.UserManagerService$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserManagerService.C13736.this.lambda$performReceive$0(i3);
                }
            }).start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$performReceive$0(int i) {
            UserManagerService.this.getActivityManagerInternal().onUserRemoved(i);
            UserManagerService.this.removeUserState(i);
        }
    }

    public final void removeUserState(int i) {
        Slog.i("UserManagerService", "Removing user state of user " + i);
        this.mLockPatternUtils.removeUser(i);
        try {
            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).destroyUserKey(i);
        } catch (IllegalStateException e) {
            Slog.i("UserManagerService", "Destroying key for user " + i + " failed, continuing anyway", e);
        }
        this.mPm.cleanUpUser(this, i);
        this.mUserDataPreparer.destroyUserData(i, 3);
        synchronized (this.mUsersLock) {
            this.mUsers.remove(i);
            this.mIsUserManaged.delete(i);
        }
        synchronized (this.mUserStates) {
            this.mUserStates.delete(i);
        }
        synchronized (this.mRestrictionsLock) {
            this.mBaseUserRestrictions.remove(i);
            this.mAppliedUserRestrictions.remove(i);
            this.mCachedEffectiveUserRestrictions.remove(i);
            if (this.mDevicePolicyUserRestrictions.remove(i)) {
                applyUserRestrictionsForAllUsersLR();
            }
        }
        synchronized (this.mPackagesLock) {
            writeUserListLP();
        }
        File file = this.mUsersDir;
        new AtomicFile(new File(file, i + ".xml")).delete();
        updateUserIds();
    }

    public final void sendProfileAddedBroadcast(int i, int i2) {
        sendProfileBroadcast(new Intent("android.intent.action.PROFILE_ADDED"), i, i2);
    }

    public final void sendProfileRemovedBroadcast(int i, int i2, String str) {
        if (Objects.equals(str, "android.os.usertype.profile.MANAGED")) {
            sendManagedProfileRemovedBroadcast(i, i2);
        }
        sendProfileBroadcast(new Intent("android.intent.action.PROFILE_REMOVED"), i, i2);
    }

    public final void sendProfileBroadcast(Intent intent, int i, int i2) {
        UserHandle of = UserHandle.of(i);
        intent.putExtra("android.intent.extra.USER", UserHandle.of(i2));
        intent.addFlags(1342177280);
        this.mContext.sendBroadcastAsUser(intent, of, null);
    }

    public final void sendManagedProfileRemovedBroadcast(int i, int i2) {
        Intent intent = new Intent("android.intent.action.MANAGED_PROFILE_REMOVED");
        intent.putExtra("android.intent.extra.USER", UserHandle.of(i2));
        intent.putExtra("android.intent.extra.user_handle", i2);
        UserHandle of = UserHandle.of(i);
        getDevicePolicyManagerInternal().broadcastIntentToManifestReceivers(intent, of, false);
        intent.addFlags(1342177280);
        this.mContext.sendBroadcastAsUser(intent, of, null);
    }

    @Deprecated
    public Bundle getApplicationRestrictions(String str) {
        return getApplicationRestrictionsForUser(str, UserHandle.getCallingUserId());
    }

    @Deprecated
    public Bundle getApplicationRestrictionsForUser(String str, int i) {
        Bundle readApplicationRestrictionsLAr;
        if (UserHandle.getCallingUserId() != i || !UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(str))) {
            checkSystemOrRoot("get application restrictions for other user/app " + str);
        }
        synchronized (this.mAppRestrictionsLock) {
            readApplicationRestrictionsLAr = readApplicationRestrictionsLAr(str, i);
        }
        return readApplicationRestrictionsLAr;
    }

    public void setApplicationRestrictions(String str, Bundle bundle, int i) {
        checkSystemOrRoot("set application restrictions");
        String validateName = validateName(str);
        if (validateName != null) {
            if (str.contains("../")) {
                EventLog.writeEvent(1397638484, "239701237", -1, "");
            }
            throw new IllegalArgumentException("Invalid package name: " + validateName);
        }
        boolean z = true;
        if (bundle != null) {
            bundle.setDefusable(true);
        }
        synchronized (this.mAppRestrictionsLock) {
            if (bundle != null) {
                if (!bundle.isEmpty()) {
                    writeApplicationRestrictionsLAr(str, bundle, i);
                }
            }
            z = cleanAppRestrictionsForPackageLAr(str, i);
        }
        if (z) {
            Intent intent = new Intent("android.intent.action.APPLICATION_RESTRICTIONS_CHANGED");
            intent.setPackage(str);
            intent.addFlags(1073741824);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i));
        }
    }

    @VisibleForTesting
    public static String validateName(String str) {
        int length = str.length();
        boolean z = true;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if ((charAt < 'a' || charAt > 'z') && (charAt < 'A' || charAt > 'Z')) {
                if (!z) {
                    if ((charAt < '0' || charAt > '9') && charAt != '_') {
                        if (charAt == '.') {
                            z = true;
                        }
                    }
                }
                return "bad character '" + charAt + "'";
            }
            z = false;
        }
        return null;
    }

    public final int getUidForPackage(String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int i = this.mContext.getPackageManager().getApplicationInfo(str, 4194304).uid;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return i;
        } catch (PackageManager.NameNotFoundException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @GuardedBy({"mAppRestrictionsLock"})
    public static Bundle readApplicationRestrictionsLAr(String str, int i) {
        return readApplicationRestrictionsLAr(new AtomicFile(new File(Environment.getUserSystemDirectory(i), packageToRestrictionsFileName(str))));
    }

    @GuardedBy({"mAppRestrictionsLock"})
    @VisibleForTesting
    public static Bundle readApplicationRestrictionsLAr(AtomicFile atomicFile) {
        TypedXmlPullParser resolvePullParser;
        Bundle bundle = new Bundle();
        ArrayList arrayList = new ArrayList();
        if (atomicFile.getBaseFile().exists()) {
            FileInputStream fileInputStream = null;
            try {
                try {
                    fileInputStream = atomicFile.openRead();
                    resolvePullParser = Xml.resolvePullParser(fileInputStream);
                    XmlUtils.nextElement(resolvePullParser);
                } catch (IOException | XmlPullParserException e) {
                    Slog.w("UserManagerService", "Error parsing " + atomicFile.getBaseFile(), e);
                }
                if (resolvePullParser.getEventType() != 2) {
                    Slog.e("UserManagerService", "Unable to read restrictions file " + atomicFile.getBaseFile());
                    return bundle;
                }
                while (resolvePullParser.next() != 1) {
                    readEntry(bundle, arrayList, resolvePullParser);
                }
                return bundle;
            } finally {
                IoUtils.closeQuietly((AutoCloseable) null);
            }
        }
        return bundle;
    }

    public static void readEntry(Bundle bundle, ArrayList<String> arrayList, TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        if (typedXmlPullParser.getEventType() == 2 && typedXmlPullParser.getName().equals("entry")) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "key");
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "type");
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "m", -1);
            if (attributeInt != -1) {
                arrayList.clear();
                while (attributeInt > 0) {
                    int next = typedXmlPullParser.next();
                    if (next == 1) {
                        break;
                    } else if (next == 2 && typedXmlPullParser.getName().equals("value")) {
                        arrayList.add(typedXmlPullParser.nextText().trim());
                        attributeInt--;
                    }
                }
                String[] strArr = new String[arrayList.size()];
                arrayList.toArray(strArr);
                bundle.putStringArray(attributeValue, strArr);
            } else if ("B".equals(attributeValue2)) {
                bundle.putBundle(attributeValue, readBundleEntry(typedXmlPullParser, arrayList));
            } else if ("BA".equals(attributeValue2)) {
                int depth = typedXmlPullParser.getDepth();
                ArrayList arrayList2 = new ArrayList();
                while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                    arrayList2.add(readBundleEntry(typedXmlPullParser, arrayList));
                }
                bundle.putParcelableArray(attributeValue, (Parcelable[]) arrayList2.toArray(new Bundle[arrayList2.size()]));
            } else {
                String trim = typedXmlPullParser.nextText().trim();
                if ("b".equals(attributeValue2)) {
                    bundle.putBoolean(attributeValue, Boolean.parseBoolean(trim));
                } else if ("i".equals(attributeValue2)) {
                    bundle.putInt(attributeValue, Integer.parseInt(trim));
                } else {
                    bundle.putString(attributeValue, trim);
                }
            }
        }
    }

    public static Bundle readBundleEntry(TypedXmlPullParser typedXmlPullParser, ArrayList<String> arrayList) throws IOException, XmlPullParserException {
        Bundle bundle = new Bundle();
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            readEntry(bundle, arrayList, typedXmlPullParser);
        }
        return bundle;
    }

    @GuardedBy({"mAppRestrictionsLock"})
    public static void writeApplicationRestrictionsLAr(String str, Bundle bundle, int i) {
        writeApplicationRestrictionsLAr(bundle, new AtomicFile(new File(Environment.getUserSystemDirectory(i), packageToRestrictionsFileName(str))));
    }

    @GuardedBy({"mAppRestrictionsLock"})
    @VisibleForTesting
    public static void writeApplicationRestrictionsLAr(Bundle bundle, AtomicFile atomicFile) {
        FileOutputStream startWrite;
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = atomicFile.startWrite();
        } catch (Exception e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "restrictions");
            writeBundle(bundle, resolveSerializer);
            resolveSerializer.endTag((String) null, "restrictions");
            resolveSerializer.endDocument();
            atomicFile.finishWrite(startWrite);
        } catch (Exception e2) {
            e = e2;
            fileOutputStream = startWrite;
            atomicFile.failWrite(fileOutputStream);
            Slog.e("UserManagerService", "Error writing application restrictions list", e);
        }
    }

    public static void writeBundle(Bundle bundle, TypedXmlSerializer typedXmlSerializer) throws IOException {
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            typedXmlSerializer.startTag((String) null, "entry");
            typedXmlSerializer.attribute((String) null, "key", str);
            if (obj instanceof Boolean) {
                typedXmlSerializer.attribute((String) null, "type", "b");
                typedXmlSerializer.text(obj.toString());
            } else if (obj instanceof Integer) {
                typedXmlSerializer.attribute((String) null, "type", "i");
                typedXmlSerializer.text(obj.toString());
            } else if (obj == null || (obj instanceof String)) {
                typedXmlSerializer.attribute((String) null, "type", "s");
                typedXmlSerializer.text(obj != null ? (String) obj : "");
            } else if (obj instanceof Bundle) {
                typedXmlSerializer.attribute((String) null, "type", "B");
                writeBundle((Bundle) obj, typedXmlSerializer);
            } else {
                int i = 0;
                if (obj instanceof Parcelable[]) {
                    typedXmlSerializer.attribute((String) null, "type", "BA");
                    Parcelable[] parcelableArr = (Parcelable[]) obj;
                    int length = parcelableArr.length;
                    while (i < length) {
                        Parcelable parcelable = parcelableArr[i];
                        if (!(parcelable instanceof Bundle)) {
                            throw new IllegalArgumentException("bundle-array can only hold Bundles");
                        }
                        typedXmlSerializer.startTag((String) null, "entry");
                        typedXmlSerializer.attribute((String) null, "type", "B");
                        writeBundle((Bundle) parcelable, typedXmlSerializer);
                        typedXmlSerializer.endTag((String) null, "entry");
                        i++;
                    }
                    continue;
                } else {
                    typedXmlSerializer.attribute((String) null, "type", "sa");
                    String[] strArr = (String[]) obj;
                    typedXmlSerializer.attributeInt((String) null, "m", strArr.length);
                    int length2 = strArr.length;
                    while (i < length2) {
                        String str2 = strArr[i];
                        typedXmlSerializer.startTag((String) null, "value");
                        if (str2 == null) {
                            str2 = "";
                        }
                        typedXmlSerializer.text(str2);
                        typedXmlSerializer.endTag((String) null, "value");
                        i++;
                    }
                }
            }
            typedXmlSerializer.endTag((String) null, "entry");
        }
    }

    public int getUserSerialNumber(int i) {
        int i2;
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            i2 = userInfoLU != null ? userInfoLU.serialNumber : -1;
        }
        return i2;
    }

    public boolean isUserNameSet(int i) {
        boolean z;
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        if (!hasQueryOrCreateUsersPermission() && (userId != i || !hasPermissionGranted("android.permission.GET_ACCOUNTS_PRIVILEGED", callingUid))) {
            throw new SecurityException("You need MANAGE_USERS, CREATE_USERS, QUERY_USERS, or GET_ACCOUNTS_PRIVILEGED permissions to: get whether user name is set");
        }
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            z = (userInfoLU == null || userInfoLU.name == null) ? false : true;
        }
        return z;
    }

    public int getUserHandle(int i) {
        int[] iArr;
        synchronized (this.mUsersLock) {
            for (int i2 : this.mUserIds) {
                UserInfo userInfoLU = getUserInfoLU(i2);
                if (userInfoLU != null && userInfoLU.serialNumber == i) {
                    return i2;
                }
            }
            return -1;
        }
    }

    public long getUserCreationTime(int i) {
        UserInfo userInfoLU;
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mUsersLock) {
            if (callingUserId == i) {
                userInfoLU = getUserInfoLU(i);
            } else {
                UserInfo profileParentLU = getProfileParentLU(i);
                userInfoLU = (profileParentLU == null || profileParentLU.id != callingUserId) ? null : getUserInfoLU(i);
            }
        }
        if (userInfoLU == null) {
            throw new SecurityException("userId can only be the calling user or a profile associated with this user");
        }
        return userInfoLU.creationTime;
    }

    public final void updateUserIds() {
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                UserInfo userInfo = this.mUsers.valueAt(i3).info;
                if (!userInfo.partial) {
                    i2++;
                    if (!userInfo.preCreated) {
                        i++;
                    }
                }
            }
            int[] iArr = new int[i];
            int[] iArr2 = new int[i2];
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < size; i6++) {
                UserInfo userInfo2 = this.mUsers.valueAt(i6).info;
                if (!userInfo2.partial) {
                    int keyAt = this.mUsers.keyAt(i6);
                    int i7 = i4 + 1;
                    iArr2[i4] = keyAt;
                    if (!userInfo2.preCreated) {
                        iArr[i5] = keyAt;
                        i5++;
                    }
                    i4 = i7;
                }
            }
            this.mUserIds = iArr;
            this.mUserIdsIncludingPreCreated = iArr2;
            UserPackage.setValidUserIds(iArr);
        }
    }

    public void onBeforeStartUser(int i) {
        UserInfo userInfo = getUserInfo(i);
        if (userInfo == null) {
            return;
        }
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("onBeforeStartUser-" + i);
        int i2 = userInfo.serialNumber;
        timingsTraceAndSlog.traceBegin("prepareUserData");
        this.mUserDataPreparer.prepareUserData(i, i2, 1);
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("reconcileAppsData");
        getPackageManagerInternal().reconcileAppsData(i, 1, !PackagePartitions.FINGERPRINT.equals(userInfo.lastLoggedInFingerprint));
        timingsTraceAndSlog.traceEnd();
        if (i != 0) {
            timingsTraceAndSlog.traceBegin("applyUserRestrictions");
            synchronized (this.mRestrictionsLock) {
                applyUserRestrictionsLR(i);
            }
            timingsTraceAndSlog.traceEnd();
        }
        timingsTraceAndSlog.traceEnd();
    }

    public void onBeforeUnlockUser(int i) {
        UserInfo userInfo = getUserInfo(i);
        if (userInfo == null) {
            return;
        }
        int i2 = userInfo.serialNumber;
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("prepareUserData-" + i);
        this.mUserDataPreparer.prepareUserData(i, i2, 2);
        timingsTraceAndSlog.traceEnd();
        ((StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class)).markCeStoragePrepared(i);
        timingsTraceAndSlog.traceBegin("reconcileAppsData-" + i);
        getPackageManagerInternal().reconcileAppsData(i, 2, PackagePartitions.FINGERPRINT.equals(userInfo.lastLoggedInFingerprint) ^ true);
        timingsTraceAndSlog.traceEnd();
    }

    public void reconcileUsers(String str) {
        this.mUserDataPreparer.reconcileUsers(str, getUsers(true, true, false));
    }

    public void onUserLoggedIn(int i) {
        UserData userDataNoChecks = getUserDataNoChecks(i);
        if (userDataNoChecks == null || userDataNoChecks.info.partial) {
            Slog.w("UserManagerService", "userForeground: unknown user #" + i);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > 946080000000L) {
            userDataNoChecks.info.lastLoggedInTime = currentTimeMillis;
        }
        userDataNoChecks.info.lastLoggedInFingerprint = PackagePartitions.FINGERPRINT;
        scheduleWriteUser(userDataNoChecks);
    }

    @VisibleForTesting
    public int getNextAvailableId() {
        synchronized (this.mUsersLock) {
            int scanNextAvailableIdLocked = scanNextAvailableIdLocked();
            if (scanNextAvailableIdLocked >= 0) {
                return scanNextAvailableIdLocked;
            }
            if (this.mRemovingUserIds.size() > 0) {
                Slog.i("UserManagerService", "All available IDs are used. Recycling LRU ids.");
                this.mRemovingUserIds.clear();
                Iterator<Integer> it = this.mRecentlyRemovedIds.iterator();
                while (it.hasNext()) {
                    this.mRemovingUserIds.put(it.next().intValue(), true);
                }
                scanNextAvailableIdLocked = scanNextAvailableIdLocked();
            }
            UserManager.invalidateStaticUserProperties();
            UserManager.invalidateUserPropertiesCache();
            if (scanNextAvailableIdLocked >= 0) {
                return scanNextAvailableIdLocked;
            }
            throw new IllegalStateException("No user id available!");
        }
    }

    @GuardedBy({"mUsersLock"})
    public final int scanNextAvailableIdLocked() {
        for (int i = 10; i < MAX_USER_ID; i++) {
            if (this.mUsers.indexOfKey(i) < 0 && !this.mRemovingUserIds.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public static String packageToRestrictionsFileName(String str) {
        return "res_" + str + ".xml";
    }

    public static String getRedacted(String str) {
        if (str == null) {
            return null;
        }
        return str.length() + "_chars";
    }

    public void setSeedAccountData(int i, String str, String str2, PersistableBundle persistableBundle, boolean z) {
        checkManageUsersPermission("set user seed account data");
        setSeedAccountDataNoChecks(i, str, str2, persistableBundle, z);
    }

    public final void setSeedAccountDataNoChecks(int i, String str, String str2, PersistableBundle persistableBundle, boolean z) {
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userDataLU = getUserDataLU(i);
                if (userDataLU == null) {
                    Slog.e("UserManagerService", "No such user for settings seed data u=" + i);
                    return;
                }
                userDataLU.seedAccountName = str;
                userDataLU.seedAccountType = str2;
                userDataLU.seedAccountOptions = persistableBundle;
                userDataLU.persistSeedData = z;
                if (z) {
                    writeUserLP(userDataLU);
                }
            }
        }
    }

    public String getSeedAccountName(int i) throws RemoteException {
        String str;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(i);
            str = userDataLU == null ? null : userDataLU.seedAccountName;
        }
        return str;
    }

    public String getSeedAccountType(int i) throws RemoteException {
        String str;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(i);
            str = userDataLU == null ? null : userDataLU.seedAccountType;
        }
        return str;
    }

    public PersistableBundle getSeedAccountOptions(int i) throws RemoteException {
        PersistableBundle persistableBundle;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            UserData userDataLU = getUserDataLU(i);
            persistableBundle = userDataLU == null ? null : userDataLU.seedAccountOptions;
        }
        return persistableBundle;
    }

    public void clearSeedAccountData(int i) throws RemoteException {
        checkManageUsersPermission("Cannot clear seed account information");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userDataLU = getUserDataLU(i);
                if (userDataLU == null) {
                    return;
                }
                userDataLU.clearSeedAccountData();
                writeUserLP(userDataLU);
            }
        }
    }

    public boolean someUserHasSeedAccount(String str, String str2) {
        checkManageUsersPermission("check seed account information");
        return someUserHasSeedAccountNoChecks(str, str2);
    }

    public final boolean someUserHasSeedAccountNoChecks(String str, String str2) {
        String str3;
        String str4;
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserData valueAt = this.mUsers.valueAt(i);
                if (!valueAt.info.isInitialized() && !this.mRemovingUserIds.get(valueAt.info.id) && (str3 = valueAt.seedAccountName) != null && str3.equals(str) && (str4 = valueAt.seedAccountType) != null && str4.equals(str2)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean someUserHasAccount(String str, String str2) {
        checkCreateUsersPermission("check seed account information");
        return someUserHasAccountNoChecks(str, str2);
    }

    public final boolean someUserHasAccountNoChecks(final String str, final String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        final Account account = new Account(str, str2);
        return ((Boolean) Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.pm.UserManagerService$$ExternalSyntheticLambda0
            public final Object getOrThrow() {
                Boolean lambda$someUserHasAccountNoChecks$3;
                lambda$someUserHasAccountNoChecks$3 = UserManagerService.this.lambda$someUserHasAccountNoChecks$3(account, str, str2);
                return lambda$someUserHasAccountNoChecks$3;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$someUserHasAccountNoChecks$3(Account account, String str, String str2) throws Exception {
        return Boolean.valueOf(AccountManager.get(this.mContext).someUserHasAccount(account) || someUserHasSeedAccountNoChecks(str, str2));
    }

    public final void setLastEnteredForegroundTimeToNow(UserData userData) {
        userData.mLastEnteredForegroundTimeMillis = System.currentTimeMillis();
        scheduleWriteUser(userData);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new UserManagerServiceShellCommand(this, this.mSystemPackageInstaller, this.mLockPatternUtils, this.mContext).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Object obj;
        int i;
        Object obj2;
        if (DumpUtils.checkDumpPermission(this.mContext, "UserManagerService", printWriter)) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            StringBuilder sb = new StringBuilder();
            if (strArr != null && strArr.length > 0) {
                String str = strArr[0];
                str.hashCode();
                if (str.equals("--visibility-mediator")) {
                    this.mUserVisibilityMediator.dump(printWriter, strArr);
                    return;
                } else if (str.equals("--user")) {
                    dumpUser(printWriter, UserHandle.parseUserArg(strArr[1]), sb, currentTimeMillis, elapsedRealtime);
                    return;
                }
            }
            int currentUserId = getCurrentUserId();
            printWriter.print("Current user: ");
            if (currentUserId != -10000) {
                printWriter.println(currentUserId);
            } else {
                printWriter.println("N/A");
            }
            printWriter.println();
            Object obj3 = this.mPackagesLock;
            synchronized (obj3) {
                try {
                    try {
                        Object obj4 = this.mUsersLock;
                        synchronized (obj4) {
                            try {
                                printWriter.println("Users:");
                                int i2 = 0;
                                while (i2 < this.mUsers.size()) {
                                    UserData valueAt = this.mUsers.valueAt(i2);
                                    if (valueAt == null) {
                                        i = i2;
                                        obj2 = obj3;
                                        obj = obj4;
                                    } else {
                                        i = i2;
                                        obj2 = obj3;
                                        obj = obj4;
                                        try {
                                            dumpUserLocked(printWriter, valueAt, sb, currentTimeMillis, elapsedRealtime);
                                        } catch (Throwable th) {
                                            th = th;
                                            throw th;
                                        }
                                    }
                                    i2 = i + 1;
                                    obj3 = obj2;
                                    obj4 = obj;
                                }
                                Object obj5 = obj3;
                                printWriter.println();
                                printWriter.println("Device properties:");
                                printWriter.println("  Device policy global restrictions:");
                                synchronized (this.mRestrictionsLock) {
                                    UserRestrictionsUtils.dumpRestrictions(printWriter, "    ", this.mDevicePolicyUserRestrictions.getRestrictions(-1));
                                }
                                printWriter.println("  Guest restrictions:");
                                synchronized (this.mGuestRestrictions) {
                                    UserRestrictionsUtils.dumpRestrictions(printWriter, "    ", this.mGuestRestrictions);
                                }
                                synchronized (this.mUsersLock) {
                                    printWriter.println();
                                    printWriter.println("  Device managed: " + this.mIsDeviceManaged);
                                    if (this.mRemovingUserIds.size() > 0) {
                                        printWriter.println();
                                        printWriter.println("  Recently removed userIds: " + this.mRecentlyRemovedIds);
                                    }
                                }
                                synchronized (this.mUserStates) {
                                    printWriter.print("  Started users state: [");
                                    int size = this.mUserStates.states.size();
                                    for (int i3 = 0; i3 < size; i3++) {
                                        int keyAt = this.mUserStates.states.keyAt(i3);
                                        int valueAt2 = this.mUserStates.states.valueAt(i3);
                                        printWriter.print(keyAt);
                                        printWriter.print('=');
                                        printWriter.print(UserState.stateToString(valueAt2));
                                        if (i3 != size - 1) {
                                            printWriter.print(", ");
                                        }
                                    }
                                    printWriter.println(']');
                                }
                                synchronized (this.mUsersLock) {
                                    printWriter.print("  Cached user IDs: ");
                                    printWriter.println(Arrays.toString(this.mUserIds));
                                    printWriter.print("  Cached user IDs (including pre-created): ");
                                    printWriter.println(Arrays.toString(this.mUserIdsIncludingPreCreated));
                                }
                                printWriter.println();
                                this.mUserVisibilityMediator.dump(printWriter, strArr);
                                printWriter.println();
                                printWriter.println();
                                printWriter.print("  Max users: " + UserManager.getMaxSupportedUsers());
                                printWriter.println(" (limit reached: " + isUserLimitReached() + ")");
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("  Supports switchable users: ");
                                sb2.append(UserManager.supportsMultipleUsers());
                                printWriter.println(sb2.toString());
                                printWriter.println("  All guests ephemeral: " + Resources.getSystem().getBoolean(17891695));
                                printWriter.println("  Force ephemeral users: " + this.mForceEphemeralUsers);
                                boolean isHeadlessSystemUserMode = isHeadlessSystemUserMode();
                                printWriter.println("  Is headless-system mode: " + isHeadlessSystemUserMode);
                                if (isHeadlessSystemUserMode != RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER) {
                                    printWriter.println("  (differs from the current default build value)");
                                }
                                if (!TextUtils.isEmpty(SystemProperties.get("persist.debug.user_mode_emulation"))) {
                                    printWriter.println("  (emulated by 'cmd user set-system-user-mode-emulation')");
                                    if (this.mUpdatingSystemUserMode) {
                                        printWriter.println("  (and being updated after boot)");
                                    }
                                }
                                printWriter.println("  User version: " + this.mUserVersion);
                                printWriter.println("  Owner name: " + getOwnerName());
                                synchronized (this.mUsersLock) {
                                    printWriter.println("  Boot user: " + this.mBootUser);
                                }
                                printWriter.println();
                                printWriter.println("Number of listeners for");
                                synchronized (this.mUserRestrictionsListeners) {
                                    printWriter.println("  restrictions: " + this.mUserRestrictionsListeners.size());
                                }
                                synchronized (this.mUserLifecycleListeners) {
                                    printWriter.println("  user lifecycle events: " + this.mUserLifecycleListeners.size());
                                }
                                printWriter.println();
                                printWriter.println("User types version: " + this.mUserTypeVersion);
                                printWriter.println("User types (" + this.mUserTypes.size() + " types):");
                                for (int i4 = 0; i4 < this.mUserTypes.size(); i4++) {
                                    printWriter.println("    " + this.mUserTypes.keyAt(i4) + ": ");
                                    this.mUserTypes.valueAt(i4).dump(printWriter, "        ");
                                }
                                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
                                try {
                                    indentingPrintWriter.println();
                                    this.mSystemPackageInstaller.dump(indentingPrintWriter);
                                    indentingPrintWriter.close();
                                    return;
                                } catch (Throwable th2) {
                                    try {
                                        indentingPrintWriter.close();
                                    } catch (Throwable th3) {
                                        th2.addSuppressed(th3);
                                    }
                                    throw th2;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                obj = obj4;
                            }
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    throw th;
                }
            }
            throw th;
        }
    }

    public final void dumpUser(PrintWriter printWriter, int i, StringBuilder sb, long j, long j2) {
        int i2;
        if (i == -2) {
            i2 = getCurrentUserId();
            printWriter.print("Current user: ");
            if (i2 == -10000) {
                printWriter.println("Cannot determine current user");
                return;
            }
        } else {
            i2 = i;
        }
        synchronized (this.mUsersLock) {
            UserData userData = this.mUsers.get(i2);
            if (userData == null) {
                printWriter.println("User " + i2 + " not found");
                return;
            }
            dumpUserLocked(printWriter, userData, sb, j, j2);
        }
    }

    @GuardedBy({"mUsersLock"})
    public final void dumpUserLocked(PrintWriter printWriter, UserData userData, StringBuilder sb, long j, long j2) {
        int i;
        UserInfo userInfo = userData.info;
        int i2 = userInfo.id;
        printWriter.print("  ");
        printWriter.print(userInfo);
        printWriter.print(" serialNo=");
        printWriter.print(userInfo.serialNumber);
        printWriter.print(" isPrimary=");
        printWriter.print(userInfo.isPrimary());
        int i3 = userInfo.profileGroupId;
        if (i3 != userInfo.id && i3 != -10000) {
            printWriter.print(" parentId=");
            printWriter.print(userInfo.profileGroupId);
        }
        if (this.mRemovingUserIds.get(i2)) {
            printWriter.print(" <removing> ");
        }
        if (userInfo.partial) {
            printWriter.print(" <partial>");
        }
        if (userInfo.preCreated) {
            printWriter.print(" <pre-created>");
        }
        if (userInfo.convertedFromPreCreated) {
            printWriter.print(" <converted>");
        }
        printWriter.println();
        printWriter.print("    Type: ");
        printWriter.println(userInfo.userType);
        printWriter.print("    Flags: ");
        printWriter.print(userInfo.flags);
        printWriter.print(" (");
        printWriter.print(UserInfo.flagsToString(userInfo.flags));
        printWriter.println(")");
        printWriter.print("    State: ");
        synchronized (this.mUserStates) {
            i = this.mUserStates.get(i2, -1);
        }
        printWriter.println(UserState.stateToString(i));
        printWriter.print("    Created: ");
        dumpTimeAgo(printWriter, sb, j, userInfo.creationTime);
        printWriter.print("    Last logged in: ");
        dumpTimeAgo(printWriter, sb, j, userInfo.lastLoggedInTime);
        printWriter.print("    Last logged in fingerprint: ");
        printWriter.println(userInfo.lastLoggedInFingerprint);
        printWriter.print("    Start time: ");
        dumpTimeAgo(printWriter, sb, j2, userData.startRealtime);
        printWriter.print("    Unlock time: ");
        dumpTimeAgo(printWriter, sb, j2, userData.unlockRealtime);
        printWriter.print("    Last entered foreground: ");
        dumpTimeAgo(printWriter, sb, j, userData.mLastEnteredForegroundTimeMillis);
        printWriter.print("    Has profile owner: ");
        printWriter.println(this.mIsUserManaged.get(i2));
        printWriter.println("    Restrictions:");
        synchronized (this.mRestrictionsLock) {
            UserRestrictionsUtils.dumpRestrictions(printWriter, "      ", this.mBaseUserRestrictions.getRestrictions(userInfo.id));
            printWriter.println("    Device policy restrictions:");
            UserRestrictionsUtils.dumpRestrictions(printWriter, "      ", this.mDevicePolicyUserRestrictions.getRestrictions(userInfo.id));
            printWriter.println("    Effective restrictions:");
            UserRestrictionsUtils.dumpRestrictions(printWriter, "      ", this.mCachedEffectiveUserRestrictions.getRestrictions(userInfo.id));
        }
        if (userData.account != null) {
            printWriter.print("    Account name: " + userData.account);
            printWriter.println();
        }
        if (userData.seedAccountName != null) {
            printWriter.print("    Seed account name: " + userData.seedAccountName);
            printWriter.println();
            if (userData.seedAccountType != null) {
                printWriter.print("         account type: " + userData.seedAccountType);
                printWriter.println();
            }
            if (userData.seedAccountOptions != null) {
                printWriter.print("         account options exist");
                printWriter.println();
            }
        }
        UserProperties userProperties = userData.userProperties;
        if (userProperties != null) {
            userProperties.println(printWriter, "    ");
        }
        printWriter.println("    Ignore errors preparing storage: " + userData.getIgnorePrepareStorageErrors());
    }

    public static void dumpTimeAgo(PrintWriter printWriter, StringBuilder sb, long j, long j2) {
        if (j2 == 0) {
            printWriter.println("<unknown>");
            return;
        }
        sb.setLength(0);
        TimeUtils.formatDuration(j - j2, sb);
        sb.append(" ago");
        printWriter.println(sb);
    }

    /* renamed from: com.android.server.pm.UserManagerService$MainHandler */
    /* loaded from: classes2.dex */
    public final class MainHandler extends Handler {
        public MainHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            removeMessages(1, message.obj);
            synchronized (UserManagerService.this.mPackagesLock) {
                int i = ((UserData) message.obj).info.id;
                UserData userDataNoChecks = UserManagerService.this.getUserDataNoChecks(i);
                if (userDataNoChecks != null) {
                    UserManagerService.this.writeUserLP(userDataNoChecks);
                } else {
                    Slog.i("UserManagerService", "handle(WRITE_USER_MSG): no data for user " + i + ", it was probably removed before handler could handle it");
                }
            }
        }
    }

    /* renamed from: com.android.server.pm.UserManagerService$LocalService */
    /* loaded from: classes2.dex */
    public class LocalService extends UserManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setDevicePolicyUserRestrictions(int i, Bundle bundle, RestrictionsSet restrictionsSet, boolean z) {
            UserManagerService.this.setDevicePolicyUserRestrictionsInner(i, bundle, restrictionsSet, z);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setUserRestriction(int i, String str, boolean z) {
            UserManagerService.this.setUserRestrictionInner(i, str, z);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean getUserRestriction(int i, String str) {
            return UserManagerService.this.getUserRestrictions(i).getBoolean(str);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void addUserRestrictionsListener(UserManagerInternal.UserRestrictionsListener userRestrictionsListener) {
            synchronized (UserManagerService.this.mUserRestrictionsListeners) {
                UserManagerService.this.mUserRestrictionsListeners.add(userRestrictionsListener);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void addUserLifecycleListener(UserManagerInternal.UserLifecycleListener userLifecycleListener) {
            synchronized (UserManagerService.this.mUserLifecycleListeners) {
                UserManagerService.this.mUserLifecycleListeners.add(userLifecycleListener);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void removeUserLifecycleListener(UserManagerInternal.UserLifecycleListener userLifecycleListener) {
            synchronized (UserManagerService.this.mUserLifecycleListeners) {
                UserManagerService.this.mUserLifecycleListeners.remove(userLifecycleListener);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setDeviceManaged(boolean z) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserManagerService.this.mIsDeviceManaged = z;
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isDeviceManaged() {
            boolean z;
            synchronized (UserManagerService.this.mUsersLock) {
                z = UserManagerService.this.mIsDeviceManaged;
            }
            return z;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setUserManaged(int i, boolean z) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserManagerService.this.mIsUserManaged.put(i, z);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserManaged(int i) {
            boolean z;
            synchronized (UserManagerService.this.mUsersLock) {
                z = UserManagerService.this.mIsUserManaged.get(i);
            }
            return z;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setUserIcon(int i, Bitmap bitmap) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (UserManagerService.this.mPackagesLock) {
                    UserData userDataNoChecks = UserManagerService.this.getUserDataNoChecks(i);
                    if (userDataNoChecks != null) {
                        UserInfo userInfo = userDataNoChecks.info;
                        if (!userInfo.partial) {
                            UserManagerService.this.writeBitmapLP(userInfo, bitmap);
                            UserManagerService.this.writeUserLP(userDataNoChecks);
                            UserManagerService.this.sendUserInfoChangedBroadcast(i);
                            return;
                        }
                    }
                    Slog.w("UserManagerService", "setUserIcon: unknown user #" + i);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setForceEphemeralUsers(boolean z) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserManagerService.this.mForceEphemeralUsers = z;
            }
        }

        /* renamed from: com.android.server.pm.UserManagerService$LocalService$1 */
        /* loaded from: classes2.dex */
        public class C13741 extends BroadcastReceiver {
            public final /* synthetic */ LocalService this$1;

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("android.intent.extra.user_handle", -10000) != 0) {
                    return;
                }
                UserManagerService.this.mContext.unregisterReceiver(this);
                UserManagerService.this.removeAllUsersExceptSystemAndPermanentAdminMain();
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void onEphemeralUserStop(int i) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserInfo userInfoLU = UserManagerService.this.getUserInfoLU(i);
                if (userInfoLU != null && userInfoLU.isEphemeral()) {
                    userInfoLU.flags |= 64;
                    if (userInfoLU.isGuest()) {
                        userInfoLU.guestToRemove = true;
                    }
                }
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public UserInfo createUserEvenWhenDisallowed(String str, String str2, int i, String[] strArr, Object obj) throws UserManager.CheckedUserOperationException {
            return UserManagerService.this.createUserInternalUnchecked(str, str2, i, -10000, false, strArr, obj);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean removeUserEvenWhenDisallowed(int i) {
            return UserManagerService.this.removeUserWithProfilesUnchecked(i);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserRunning(int i) {
            int i2;
            synchronized (UserManagerService.this.mUserStates) {
                i2 = UserManagerService.this.mUserStates.get(i, -1);
            }
            return (i2 == -1 || i2 == 4 || i2 == 5) ? false : true;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setUserState(int i, int i2) {
            synchronized (UserManagerService.this.mUserStates) {
                UserManagerService.this.mUserStates.put(i, i2);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void removeUserState(int i) {
            synchronized (UserManagerService.this.mUserStates) {
                UserManagerService.this.mUserStates.delete(i);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int[] getUserIds() {
            return UserManagerService.this.getUserIds();
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public List<UserInfo> getUsers(boolean z) {
            return getUsers(true, z, true);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public List<UserInfo> getUsers(boolean z, boolean z2, boolean z3) {
            return UserManagerService.this.getUsersInternal(z, z2, z3);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int[] getProfileIds(int i, boolean z) {
            int[] array;
            synchronized (UserManagerService.this.mUsersLock) {
                array = UserManagerService.this.getProfileIdsLU(i, null, z).toArray();
            }
            return array;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserUnlockingOrUnlocked(int i) {
            int i2;
            synchronized (UserManagerService.this.mUserStates) {
                i2 = UserManagerService.this.mUserStates.get(i, -1);
            }
            if (i2 == 4 || i2 == 5) {
                return StorageManager.isUserKeyUnlocked(i);
            }
            return i2 == 2 || i2 == 3;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserUnlocked(int i) {
            int i2;
            synchronized (UserManagerService.this.mUserStates) {
                i2 = UserManagerService.this.mUserStates.get(i, -1);
            }
            if (i2 == 4 || i2 == 5) {
                return StorageManager.isUserKeyUnlocked(i);
            }
            return i2 == 3;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean exists(int i) {
            return UserManagerService.this.getUserInfoNoChecks(i) != null;
        }

        /* JADX WARN: Code restructure failed: missing block: B:26:0x003a, code lost:
            return false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x0057, code lost:
            android.util.Slog.w("UserManagerService", r8 + " for disabled profile " + r7 + " from " + r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:34:0x007c, code lost:
            android.util.Slog.w("UserManagerService", r8 + " for another profile " + r7 + " from " + r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x009e, code lost:
            return false;
         */
        @Override // com.android.server.p011pm.UserManagerInternal
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean isProfileAccessible(int i, int i2, String str, boolean z) {
            if (i2 == i) {
                return true;
            }
            synchronized (UserManagerService.this.mUsersLock) {
                UserInfo userInfoLU = UserManagerService.this.getUserInfoLU(i);
                if (userInfoLU != null && !userInfoLU.isProfile()) {
                    UserInfo userInfoLU2 = UserManagerService.this.getUserInfoLU(i2);
                    if (userInfoLU2 != null && userInfoLU2.isEnabled()) {
                        int i3 = userInfoLU2.profileGroupId;
                        if (i3 != -10000 && i3 == userInfoLU.profileGroupId) {
                            return true;
                        }
                        throw new SecurityException(str + " for unrelated profile " + i2);
                    }
                    return false;
                }
                throw new SecurityException(str + " for another profile " + i2 + " from " + i);
            }
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int getProfileParentId(int i) {
            return UserManagerService.this.getProfileParentIdUnchecked(i);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean hasUserRestriction(String str, int i) {
            Bundle effectiveUserRestrictions;
            return UserRestrictionsUtils.isValidRestriction(str) && (effectiveUserRestrictions = UserManagerService.this.getEffectiveUserRestrictions(i)) != null && effectiveUserRestrictions.getBoolean(str);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public UserInfo getUserInfo(int i) {
            UserData userData;
            synchronized (UserManagerService.this.mUsersLock) {
                userData = (UserData) UserManagerService.this.mUsers.get(i);
            }
            if (userData == null) {
                return null;
            }
            return userData.info;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public UserInfo[] getUserInfos() {
            UserInfo[] userInfoArr;
            synchronized (UserManagerService.this.mUsersLock) {
                int size = UserManagerService.this.mUsers.size();
                userInfoArr = new UserInfo[size];
                for (int i = 0; i < size; i++) {
                    userInfoArr[i] = ((UserData) UserManagerService.this.mUsers.valueAt(i)).info;
                }
            }
            return userInfoArr;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void setDefaultCrossProfileIntentFilters(int i, int i2) {
            UserManagerService.this.setDefaultCrossProfileIntentFilters(i2, UserManagerService.this.getUserTypeDetailsNoChecks(i2), UserManagerService.this.getEffectiveUserRestrictions(i2), i);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean shouldIgnorePrepareStorageErrors(int i) {
            boolean z;
            synchronized (UserManagerService.this.mUsersLock) {
                UserData userData = (UserData) UserManagerService.this.mUsers.get(i);
                z = userData != null && userData.getIgnorePrepareStorageErrors();
            }
            return z;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public UserProperties getUserProperties(int i) {
            UserProperties userPropertiesInternal = UserManagerService.this.getUserPropertiesInternal(i);
            if (userPropertiesInternal == null) {
                Slog.w("UserManagerService", "A null UserProperties was returned for user " + i);
            }
            return userPropertiesInternal;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int assignUserToDisplayOnStart(int i, int i2, int i3, int i4) {
            return UserManagerService.this.mUserVisibilityMediator.assignUserToDisplayOnStart(i, i2, i3, i4);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void unassignUserFromDisplayOnStop(int i) {
            UserManagerService.this.mUserVisibilityMediator.unassignUserFromDisplayOnStop(i);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserVisible(int i) {
            return UserManagerService.this.mUserVisibilityMediator.isUserVisible(i);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public boolean isUserVisible(int i, int i2) {
            return UserManagerService.this.mUserVisibilityMediator.isUserVisible(i, i2);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void addUserVisibilityListener(UserManagerInternal.UserVisibilityListener userVisibilityListener) {
            UserManagerService.this.mUserVisibilityMediator.addListener(userVisibilityListener);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public void onSystemUserVisibilityChanged(boolean z) {
            UserManagerService.this.mUserVisibilityMediator.onSystemUserVisibilityChanged(z);
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int[] getUserTypesForStatsd(int[] iArr) {
            if (iArr == null) {
                return null;
            }
            int length = iArr.length;
            int[] iArr2 = new int[length];
            for (int i = 0; i < length; i++) {
                UserInfo userInfo = getUserInfo(iArr[i]);
                if (userInfo == null) {
                    iArr2[i] = UserManager.getUserTypeForStatsd("");
                } else {
                    iArr2[i] = UserManager.getUserTypeForStatsd(userInfo.userType);
                }
            }
            return iArr2;
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int getMainUserId() {
            return UserManagerService.this.getMainUserIdUnchecked();
        }

        @Override // com.android.server.p011pm.UserManagerInternal
        public int getBootUser() throws UserManager.CheckedUserOperationException {
            synchronized (UserManagerService.this.mUsersLock) {
                if (UserManagerService.this.mBootUser != -10000) {
                    UserData userData = (UserData) UserManagerService.this.mUsers.get(UserManagerService.this.mBootUser);
                    if (userData != null && userData.info.supportsSwitchToByUser()) {
                        Slogf.m20i("UserManagerService", "Using provided boot user: %d", Integer.valueOf(UserManagerService.this.mBootUser));
                        return UserManagerService.this.mBootUser;
                    }
                    Slogf.m12w("UserManagerService", "Provided boot user cannot be switched to: %d", Integer.valueOf(UserManagerService.this.mBootUser));
                }
                if (UserManagerService.this.isHeadlessSystemUserMode()) {
                    int previousFullUserToEnterForeground = UserManagerService.this.getPreviousFullUserToEnterForeground();
                    if (previousFullUserToEnterForeground != -10000) {
                        Slogf.m20i("UserManagerService", "Boot user is previous user %d", Integer.valueOf(previousFullUserToEnterForeground));
                        return previousFullUserToEnterForeground;
                    }
                    synchronized (UserManagerService.this.mUsersLock) {
                        int size = UserManagerService.this.mUsers.size();
                        for (int i = 0; i < size; i++) {
                            UserData userData2 = (UserData) UserManagerService.this.mUsers.valueAt(i);
                            if (userData2.info.supportsSwitchToByUser()) {
                                int i2 = userData2.info.id;
                                Slogf.m20i("UserManagerService", "Boot user is first switchable user %d", Integer.valueOf(i2));
                                return i2;
                            }
                        }
                        throw new UserManager.CheckedUserOperationException("No switchable users found", 1);
                    }
                }
                return 0;
            }
        }
    }

    public final void enforceUserRestriction(String str, int i, String str2) throws UserManager.CheckedUserOperationException {
        String str3;
        if (hasUserRestriction(str, i)) {
            StringBuilder sb = new StringBuilder();
            if (str2 != null) {
                str3 = str2 + ": ";
            } else {
                str3 = "";
            }
            sb.append(str3);
            sb.append(str);
            sb.append(" is enabled.");
            String sb2 = sb.toString();
            Slog.w("UserManagerService", sb2);
            throw new UserManager.CheckedUserOperationException(sb2, 1);
        }
    }

    public final void throwCheckedUserOperationException(String str, int i) throws UserManager.CheckedUserOperationException {
        Slog.e("UserManagerService", str);
        throw new UserManager.CheckedUserOperationException(str, i);
    }

    public final void removeAllUsersExceptSystemAndPermanentAdminMain() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = this.mUsers.valueAt(i).info;
                if (userInfo.id != 0 && !isNonRemovableMainUser(userInfo)) {
                    arrayList.add(userInfo);
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            removeUser(((UserInfo) it.next()).id);
        }
    }

    @VisibleForTesting
    public int getMaxUsersOfTypePerParent(String str) {
        UserTypeDetails userTypeDetails = this.mUserTypes.get(str);
        if (userTypeDetails == null) {
            return 0;
        }
        return getMaxUsersOfTypePerParent(userTypeDetails);
    }

    public static int getMaxUsersOfTypePerParent(UserTypeDetails userTypeDetails) {
        int maxAllowedPerParent = userTypeDetails.getMaxAllowedPerParent();
        return (Build.IS_DEBUGGABLE && userTypeDetails.isManagedProfile()) ? SystemProperties.getInt("persist.sys.max_profiles", maxAllowedPerParent) : maxAllowedPerParent;
    }

    @GuardedBy({"mUsersLock"})
    @VisibleForTesting
    public int getFreeProfileBadgeLU(int i, String str) {
        ArraySet arraySet = new ArraySet();
        int size = this.mUsers.size();
        for (int i2 = 0; i2 < size; i2++) {
            UserInfo userInfo = this.mUsers.valueAt(i2).info;
            if (userInfo.userType.equals(str) && userInfo.profileGroupId == i && !this.mRemovingUserIds.get(userInfo.id)) {
                arraySet.add(Integer.valueOf(userInfo.profileBadge));
            }
        }
        int maxUsersOfTypePerParent = getMaxUsersOfTypePerParent(str);
        if (maxUsersOfTypePerParent == -1) {
            maxUsersOfTypePerParent = Integer.MAX_VALUE;
        }
        for (int i3 = 0; i3 < maxUsersOfTypePerParent; i3++) {
            if (!arraySet.contains(Integer.valueOf(i3))) {
                return i3;
            }
        }
        return 0;
    }

    public boolean hasProfile(int i) {
        synchronized (this.mUsersLock) {
            UserInfo userInfoLU = getUserInfoLU(i);
            int size = this.mUsers.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = this.mUsers.valueAt(i2).info;
                if (i != userInfo.id && isProfileOf(userInfoLU, userInfo)) {
                    return true;
                }
            }
            return false;
        }
    }

    public final void verifyCallingPackage(String str, int i) {
        if (this.mPm.snapshotComputer().getPackageUid(str, 0L, UserHandle.getUserId(i)) == i) {
            return;
        }
        throw new SecurityException("Specified package " + str + " does not match the calling uid " + i);
    }

    public final PackageManagerInternal getPackageManagerInternal() {
        if (this.mPmInternal == null) {
            this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPmInternal;
    }

    public final DevicePolicyManagerInternal getDevicePolicyManagerInternal() {
        if (this.mDevicePolicyManagerInternal == null) {
            this.mDevicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        }
        return this.mDevicePolicyManagerInternal;
    }

    public final ActivityManagerInternal getActivityManagerInternal() {
        if (this.mAmInternal == null) {
            this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }
        return this.mAmInternal;
    }

    public final boolean isNonRemovableMainUser(UserInfo userInfo) {
        return userInfo.isMain() && isMainUserPermanentAdmin();
    }

    public boolean isMainUserPermanentAdmin() {
        return Resources.getSystem().getBoolean(17891710);
    }

    public boolean canSwitchToHeadlessSystemUser() {
        return Resources.getSystem().getBoolean(17891403);
    }
}
