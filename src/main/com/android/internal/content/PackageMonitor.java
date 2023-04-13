package com.android.internal.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.util.Slog;
import com.android.internal.p028os.BackgroundThread;
import java.util.HashSet;
import java.util.Objects;
/* loaded from: classes4.dex */
public abstract class PackageMonitor extends BroadcastReceiver {
    public static final int PACKAGE_PERMANENT_CHANGE = 3;
    public static final int PACKAGE_TEMPORARY_CHANGE = 2;
    public static final int PACKAGE_UNCHANGED = 0;
    public static final int PACKAGE_UPDATING = 1;
    static final String TAG = "PackageMonitor";
    String[] mAppearingPackages;
    int mChangeType;
    String[] mDisappearingPackages;
    final IntentFilter mExternalFilt;
    String[] mModifiedComponents;
    String[] mModifiedPackages;
    final IntentFilter mNonDataFilt;
    final IntentFilter mPackageFilt;
    Context mRegisteredContext;
    Handler mRegisteredHandler;
    boolean mSomePackagesChanged;
    final HashSet<String> mUpdatingPackages = new HashSet<>();
    int mChangeUserId = -10000;
    String[] mTempArray = new String[1];

    public PackageMonitor() {
        boolean isCore = UserHandle.isCore(Process.myUid());
        IntentFilter intentFilter = new IntentFilter();
        this.mPackageFilt = intentFilter;
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_QUERY_PACKAGE_RESTART);
        intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        intentFilter.addDataScheme("package");
        if (isCore) {
            intentFilter.setPriority(1000);
        }
        IntentFilter intentFilter2 = new IntentFilter();
        this.mNonDataFilt = intentFilter2;
        intentFilter2.addAction(Intent.ACTION_UID_REMOVED);
        intentFilter2.addAction(Intent.ACTION_USER_STOPPED);
        intentFilter2.addAction(Intent.ACTION_PACKAGES_SUSPENDED);
        intentFilter2.addAction(Intent.ACTION_PACKAGES_UNSUSPENDED);
        if (isCore) {
            intentFilter2.setPriority(1000);
        }
        IntentFilter intentFilter3 = new IntentFilter();
        this.mExternalFilt = intentFilter3;
        intentFilter3.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        intentFilter3.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        if (isCore) {
            intentFilter3.setPriority(1000);
        }
    }

    public void register(Context context, Looper thread, boolean externalStorage) {
        register(context, thread, (UserHandle) null, externalStorage);
    }

    public void register(Context context, Looper thread, UserHandle user, boolean externalStorage) {
        register(context, user, externalStorage, thread == null ? BackgroundThread.getHandler() : new Handler(thread));
    }

    public void register(Context context, UserHandle user, boolean externalStorage, Handler handler) {
        if (this.mRegisteredContext != null) {
            throw new IllegalStateException("Already registered");
        }
        this.mRegisteredContext = context;
        Handler handler2 = (Handler) Objects.requireNonNull(handler);
        this.mRegisteredHandler = handler2;
        if (user != null) {
            context.registerReceiverAsUser(this, user, this.mPackageFilt, null, handler2);
            context.registerReceiverAsUser(this, user, this.mNonDataFilt, null, this.mRegisteredHandler);
            if (externalStorage) {
                context.registerReceiverAsUser(this, user, this.mExternalFilt, null, this.mRegisteredHandler);
                return;
            }
            return;
        }
        context.registerReceiver(this, this.mPackageFilt, null, handler2);
        context.registerReceiver(this, this.mNonDataFilt, null, this.mRegisteredHandler);
        if (externalStorage) {
            context.registerReceiver(this, this.mExternalFilt, null, this.mRegisteredHandler);
        }
    }

    public Handler getRegisteredHandler() {
        return this.mRegisteredHandler;
    }

    public void unregister() {
        Context context = this.mRegisteredContext;
        if (context == null) {
            throw new IllegalStateException("Not registered");
        }
        context.unregisterReceiver(this);
        this.mRegisteredContext = null;
    }

    boolean isPackageUpdating(String packageName) {
        boolean contains;
        synchronized (this.mUpdatingPackages) {
            contains = this.mUpdatingPackages.contains(packageName);
        }
        return contains;
    }

    public void onBeginPackageChanges() {
    }

    public void onPackageAdded(String packageName, int uid) {
    }

    public void onPackageRemoved(String packageName, int uid) {
    }

    public void onPackageRemovedAllUsers(String packageName, int uid) {
    }

    public void onPackageUpdateStarted(String packageName, int uid) {
    }

    public void onPackageUpdateFinished(String packageName, int uid) {
    }

    public boolean onPackageChanged(String packageName, int uid, String[] components) {
        if (components != null) {
            for (String name : components) {
                if (packageName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
        return false;
    }

    public void onHandleUserStop(Intent intent, int userHandle) {
    }

    public void onUidRemoved(int uid) {
    }

    public void onPackagesAvailable(String[] packages) {
    }

    public void onPackagesUnavailable(String[] packages) {
    }

    public void onPackagesSuspended(String[] packages) {
    }

    public void onPackagesUnsuspended(String[] packages) {
    }

    public void onPackageDisappeared(String packageName, int reason) {
    }

    public void onPackageAppeared(String packageName, int reason) {
    }

    public void onPackageModified(String packageName) {
    }

    public boolean didSomePackagesChange() {
        return this.mSomePackagesChanged;
    }

    public int isPackageAppearing(String packageName) {
        String[] strArr = this.mAppearingPackages;
        if (strArr != null) {
            for (int i = strArr.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mAppearingPackages[i])) {
                    return this.mChangeType;
                }
            }
            return 0;
        }
        return 0;
    }

    public boolean anyPackagesAppearing() {
        return this.mAppearingPackages != null;
    }

    public int isPackageDisappearing(String packageName) {
        String[] strArr = this.mDisappearingPackages;
        if (strArr != null) {
            for (int i = strArr.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mDisappearingPackages[i])) {
                    return this.mChangeType;
                }
            }
            return 0;
        }
        return 0;
    }

    public boolean anyPackagesDisappearing() {
        return this.mDisappearingPackages != null;
    }

    public boolean isReplacing() {
        return this.mChangeType == 1;
    }

    public boolean isPackageModified(String packageName) {
        String[] strArr = this.mModifiedPackages;
        if (strArr != null) {
            for (int i = strArr.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mModifiedPackages[i])) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean isComponentModified(String className) {
        String[] strArr;
        if (className == null || (strArr = this.mModifiedComponents) == null) {
            return false;
        }
        for (int i = strArr.length - 1; i >= 0; i--) {
            if (className.equals(this.mModifiedComponents[i])) {
                return true;
            }
        }
        return false;
    }

    public void onSomePackagesChanged() {
    }

    public void onFinishPackageChanges() {
    }

    public void onPackageDataCleared(String packageName, int uid) {
    }

    public void onPackageStateChanged(String packageName, int uid) {
    }

    public int getChangingUserId() {
        return this.mChangeUserId;
    }

    String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String pkg = uri.getSchemeSpecificPart();
            return pkg;
        }
        return null;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
        this.mChangeUserId = intExtra;
        if (intExtra == -10000) {
            Slog.m90w(TAG, "Intent broadcast does not contain user handle: " + intent);
            return;
        }
        onBeginPackageChanges();
        this.mAppearingPackages = null;
        this.mDisappearingPackages = null;
        this.mSomePackagesChanged = false;
        this.mModifiedComponents = null;
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String pkg = getPackageName(intent);
            int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
            this.mSomePackagesChanged = true;
            if (pkg != null) {
                String[] strArr = this.mTempArray;
                this.mAppearingPackages = strArr;
                strArr[0] = pkg;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    this.mModifiedPackages = this.mTempArray;
                    this.mChangeType = 1;
                    onPackageUpdateFinished(pkg, uid);
                    onPackageModified(pkg);
                } else {
                    this.mChangeType = 3;
                    onPackageAdded(pkg, uid);
                }
                onPackageAppeared(pkg, this.mChangeType);
                if (this.mChangeType == 1) {
                    synchronized (this.mUpdatingPackages) {
                        this.mUpdatingPackages.remove(pkg);
                    }
                }
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String pkg2 = getPackageName(intent);
            int uid2 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (pkg2 != null) {
                String[] strArr2 = this.mTempArray;
                this.mDisappearingPackages = strArr2;
                strArr2[0] = pkg2;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    this.mChangeType = 1;
                    synchronized (this.mUpdatingPackages) {
                    }
                    onPackageUpdateStarted(pkg2, uid2);
                } else {
                    this.mChangeType = 3;
                    this.mSomePackagesChanged = true;
                    onPackageRemoved(pkg2, uid2);
                    if (intent.getBooleanExtra(Intent.EXTRA_REMOVED_FOR_ALL_USERS, false)) {
                        onPackageRemovedAllUsers(pkg2, uid2);
                    }
                }
                onPackageDisappeared(pkg2, this.mChangeType);
            }
        } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            String pkg3 = getPackageName(intent);
            int uid3 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            String[] stringArrayExtra = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
            this.mModifiedComponents = stringArrayExtra;
            if (pkg3 != null) {
                String[] strArr3 = this.mTempArray;
                this.mModifiedPackages = strArr3;
                strArr3[0] = pkg3;
                this.mChangeType = 3;
                if (onPackageChanged(pkg3, uid3, stringArrayExtra)) {
                    this.mSomePackagesChanged = true;
                }
                onPackageModified(pkg3);
            }
        } else if (Intent.ACTION_PACKAGE_DATA_CLEARED.equals(action)) {
            String pkg4 = getPackageName(intent);
            int uid4 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (pkg4 != null) {
                onPackageDataCleared(pkg4, uid4);
            }
        } else if (Intent.ACTION_QUERY_PACKAGE_RESTART.equals(action)) {
            String[] stringArrayExtra2 = intent.getStringArrayExtra(Intent.EXTRA_PACKAGES);
            this.mDisappearingPackages = stringArrayExtra2;
            this.mChangeType = 2;
            boolean canRestart = onHandleForceStop(intent, stringArrayExtra2, intent.getIntExtra(Intent.EXTRA_UID, 0), false);
            if (canRestart) {
                setResultCode(-1);
            }
        } else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
            String[] strArr4 = {getPackageName(intent)};
            this.mDisappearingPackages = strArr4;
            this.mChangeType = 2;
            onHandleForceStop(intent, strArr4, intent.getIntExtra(Intent.EXTRA_UID, 0), true);
        } else if (Intent.ACTION_UID_REMOVED.equals(action)) {
            onUidRemoved(intent.getIntExtra(Intent.EXTRA_UID, 0));
        } else if (Intent.ACTION_USER_STOPPED.equals(action)) {
            if (intent.hasExtra(Intent.EXTRA_USER_HANDLE)) {
                onHandleUserStop(intent, intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
            }
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            String[] pkgList = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            this.mAppearingPackages = pkgList;
            this.mChangeType = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) ? 1 : 2;
            this.mSomePackagesChanged = true;
            if (pkgList != null) {
                onPackagesAvailable(pkgList);
                for (String str : pkgList) {
                    onPackageAppeared(str, this.mChangeType);
                }
            }
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
            String[] pkgList2 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            this.mDisappearingPackages = pkgList2;
            this.mChangeType = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) ? 1 : 2;
            this.mSomePackagesChanged = true;
            if (pkgList2 != null) {
                onPackagesUnavailable(pkgList2);
                for (String str2 : pkgList2) {
                    onPackageDisappeared(str2, this.mChangeType);
                }
            }
        } else if (Intent.ACTION_PACKAGES_SUSPENDED.equals(action)) {
            String[] pkgList3 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            this.mSomePackagesChanged = true;
            onPackagesSuspended(pkgList3);
        } else if (Intent.ACTION_PACKAGES_UNSUSPENDED.equals(action)) {
            String[] pkgList4 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            this.mSomePackagesChanged = true;
            onPackagesUnsuspended(pkgList4);
        }
        if (this.mSomePackagesChanged) {
            onSomePackagesChanged();
        }
        onFinishPackageChanges();
        this.mChangeUserId = -10000;
    }
}
