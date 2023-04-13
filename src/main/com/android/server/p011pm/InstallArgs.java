package com.android.server.p011pm;

import android.content.pm.IPackageInstallObserver2;
import android.content.pm.SigningDetails;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.util.List;
/* renamed from: com.android.server.pm.InstallArgs */
/* loaded from: classes2.dex */
public final class InstallArgs {
    public final String mAbiOverride;
    public final List<String> mAllowlistedRestrictedPermissions;
    public final boolean mApplicationEnabledSettingPersistent;
    public final int mAutoRevokePermissionsMode;
    public File mCodeFile;
    public final int mDataLoaderType;
    public final boolean mForceQueryableOverride;
    public final int mInstallFlags;
    public final int mInstallReason;
    public final int mInstallScenario;
    public final InstallSource mInstallSource;
    public String[] mInstructionSets;
    public final MoveInfo mMoveInfo;
    public final IPackageInstallObserver2 mObserver;
    public final OriginInfo mOriginInfo;
    public final int mPackageSource;
    public final ArrayMap<String, Integer> mPermissionStates;
    public final SigningDetails mSigningDetails;
    public final int mTraceCookie;
    public final String mTraceMethod;
    public final UserHandle mUser;
    public final String mVolumeUuid;

    public InstallArgs(OriginInfo originInfo, MoveInfo moveInfo, IPackageInstallObserver2 iPackageInstallObserver2, int i, InstallSource installSource, String str, UserHandle userHandle, String[] strArr, String str2, ArrayMap<String, Integer> arrayMap, List<String> list, int i2, String str3, int i3, SigningDetails signingDetails, int i4, int i5, boolean z, int i6, int i7, boolean z2) {
        this.mOriginInfo = originInfo;
        this.mMoveInfo = moveInfo;
        this.mInstallFlags = i;
        this.mObserver = iPackageInstallObserver2;
        this.mInstallSource = (InstallSource) Preconditions.checkNotNull(installSource);
        this.mVolumeUuid = str;
        this.mUser = userHandle;
        this.mInstructionSets = strArr;
        this.mAbiOverride = str2;
        this.mPermissionStates = arrayMap;
        this.mAllowlistedRestrictedPermissions = list;
        this.mAutoRevokePermissionsMode = i2;
        this.mTraceMethod = str3;
        this.mTraceCookie = i3;
        this.mSigningDetails = signingDetails;
        this.mInstallReason = i4;
        this.mInstallScenario = i5;
        this.mForceQueryableOverride = z;
        this.mDataLoaderType = i6;
        this.mPackageSource = i7;
        this.mApplicationEnabledSettingPersistent = z2;
    }

    public InstallArgs(String str, String[] strArr) {
        this(OriginInfo.fromNothing(), null, null, 0, InstallSource.EMPTY, null, null, strArr, null, new ArrayMap(), null, 3, null, 0, SigningDetails.UNKNOWN, 0, 0, false, 0, 0, false);
        this.mCodeFile = str != null ? new File(str) : null;
    }
}
