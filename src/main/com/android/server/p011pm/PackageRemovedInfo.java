package com.android.server.p011pm;

import android.app.ActivityManagerInternal;
import android.app.BroadcastOptions;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
/* renamed from: com.android.server.pm.PackageRemovedInfo */
/* loaded from: classes2.dex */
public final class PackageRemovedInfo {
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public SparseArray<int[]> mBroadcastAllowList;
    public boolean mDataRemoved;
    public SparseIntArray mInstallReasons;
    public String mInstallerPackageName;
    public boolean mIsExternal;
    public boolean mIsStaticSharedLib;
    public boolean mIsUpdate;
    public int[] mOrigUsers;
    public final PackageSender mPackageSender;
    public boolean mRemovedForAllUsers;
    public String mRemovedPackage;
    public long mRemovedPackageVersionCode;
    public SparseIntArray mUninstallReasons;
    public int mUid = -1;
    public int mRemovedAppId = -1;
    public int[] mRemovedUsers = null;
    public int[] mBroadcastUsers = null;
    public int[] mInstantUserIds = null;
    public boolean mIsRemovedPackageSystemUpdate = false;
    public InstallArgs mArgs = null;

    public PackageRemovedInfo(PackageSender packageSender) {
        this.mPackageSender = packageSender;
    }

    public void sendPackageRemovedBroadcasts(boolean z, boolean z2) {
        sendPackageRemovedBroadcastInternal(z, z2);
    }

    public void sendSystemPackageUpdatedBroadcasts() {
        if (this.mIsRemovedPackageSystemUpdate) {
            sendSystemPackageUpdatedBroadcastsInternal();
        }
    }

    public final void sendSystemPackageUpdatedBroadcastsInternal() {
        Bundle bundle = new Bundle(2);
        int i = this.mRemovedAppId;
        if (i < 0) {
            i = this.mUid;
        }
        bundle.putInt("android.intent.extra.UID", i);
        bundle.putBoolean("android.intent.extra.REPLACING", true);
        this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", this.mRemovedPackage, bundle, 0, null, null, null, null, this.mBroadcastAllowList, null);
        String str = this.mInstallerPackageName;
        if (str != null) {
            this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", this.mRemovedPackage, bundle, 0, str, null, null, null, null, null);
            this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_REPLACED", this.mRemovedPackage, bundle, 0, this.mInstallerPackageName, null, null, null, null, null);
        }
        this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_REPLACED", this.mRemovedPackage, bundle, 0, null, null, null, null, this.mBroadcastAllowList, null);
        this.mPackageSender.sendPackageBroadcast("android.intent.action.MY_PACKAGE_REPLACED", null, null, 0, this.mRemovedPackage, null, null, null, null, getTemporaryAppAllowlistBroadcastOptions(FrameworkStatsLog.f93x9057b857).toBundle());
    }

    public static BroadcastOptions getTemporaryAppAllowlistBroadcastOptions(int i) {
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        long bootTimeTempAllowListDuration = activityManagerInternal != null ? activityManagerInternal.getBootTimeTempAllowListDuration() : 10000L;
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setTemporaryAppAllowlist(bootTimeTempAllowListDuration, 0, i, "");
        return makeBasic;
    }

    public final void sendPackageRemovedBroadcastInternal(boolean z, boolean z2) {
        String str;
        Bundle bundle = new Bundle();
        int i = this.mRemovedAppId;
        if (i < 0) {
            i = this.mUid;
        }
        int i2 = i;
        bundle.putInt("android.intent.extra.UID", i2);
        bundle.putBoolean("android.intent.extra.DATA_REMOVED", this.mDataRemoved);
        bundle.putBoolean("android.intent.extra.SYSTEM_UPDATE_UNINSTALL", this.mIsRemovedPackageSystemUpdate);
        bundle.putBoolean("android.intent.extra.DONT_KILL_APP", !z);
        bundle.putBoolean("android.intent.extra.USER_INITIATED", !z2);
        boolean z3 = this.mIsUpdate || this.mIsRemovedPackageSystemUpdate;
        if (z3) {
            bundle.putBoolean("android.intent.extra.REPLACING", true);
        }
        bundle.putBoolean("android.intent.extra.REMOVED_FOR_ALL_USERS", this.mRemovedForAllUsers);
        String str2 = this.mRemovedPackage;
        if (str2 != null && (str = this.mInstallerPackageName) != null) {
            this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_REMOVED", str2, bundle, 0, str, null, this.mBroadcastUsers, this.mInstantUserIds, null, null);
        }
        if (this.mIsStaticSharedLib) {
            return;
        }
        String str3 = this.mRemovedPackage;
        if (str3 != null) {
            this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_REMOVED", str3, bundle, 0, null, null, this.mBroadcastUsers, this.mInstantUserIds, this.mBroadcastAllowList, null);
            this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_REMOVED_INTERNAL", this.mRemovedPackage, bundle, 0, PackageManagerShellCommandDataLoader.PACKAGE, null, this.mBroadcastUsers, this.mInstantUserIds, this.mBroadcastAllowList, null);
            if (this.mDataRemoved && !this.mIsRemovedPackageSystemUpdate) {
                this.mPackageSender.sendPackageBroadcast("android.intent.action.PACKAGE_FULLY_REMOVED", this.mRemovedPackage, bundle, 16777216, null, null, this.mBroadcastUsers, this.mInstantUserIds, this.mBroadcastAllowList, null);
                this.mPackageSender.notifyPackageRemoved(this.mRemovedPackage, i2);
            }
        }
        if (this.mRemovedAppId >= 0) {
            if (z3) {
                bundle.putString("android.intent.extra.PACKAGE_NAME", this.mRemovedPackage);
            }
            this.mPackageSender.sendPackageBroadcast("android.intent.action.UID_REMOVED", null, bundle, 16777216, null, null, this.mBroadcastUsers, this.mInstantUserIds, this.mBroadcastAllowList, null);
        }
    }

    public void populateUsers(int[] iArr, PackageSetting packageSetting) {
        this.mRemovedUsers = iArr;
        if (iArr == null) {
            this.mBroadcastUsers = null;
            return;
        }
        int[] iArr2 = EMPTY_INT_ARRAY;
        this.mBroadcastUsers = iArr2;
        this.mInstantUserIds = iArr2;
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            if (packageSetting.getInstantApp(i)) {
                this.mInstantUserIds = ArrayUtils.appendInt(this.mInstantUserIds, i);
            } else {
                this.mBroadcastUsers = ArrayUtils.appendInt(this.mBroadcastUsers, i);
            }
        }
    }
}
