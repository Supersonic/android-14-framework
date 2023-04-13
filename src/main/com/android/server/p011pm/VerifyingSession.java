package com.android.server.p011pm;

import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.DataLoaderParams;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.PackageInfoLite;
import android.content.pm.PackageInstaller;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.SigningDetails;
import android.content.pm.VerifierInfo;
import android.content.pm.parsing.PackageLite;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.incremental.IncrementalManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Slog;
import com.android.server.DeviceIdleInternal;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* renamed from: com.android.server.pm.VerifyingSession */
/* loaded from: classes2.dex */
public final class VerifyingSession {
    public final int mDataLoaderType;
    public final int mInstallFlags;
    public final InstallPackageHelper mInstallPackageHelper;
    public final InstallSource mInstallSource;
    public final boolean mIsInherit;
    public final boolean mIsStaged;
    public final IPackageInstallObserver2 mObserver;
    public final OriginInfo mOriginInfo;
    public final String mPackageAbiOverride;
    public final PackageLite mPackageLite;
    public MultiPackageVerifyingSession mParentVerifyingSession;
    public final PackageManagerService mPm;
    public final long mRequiredInstalledVersionCode;
    public final int mSessionId;
    public final SigningDetails mSigningDetails;
    public final UserHandle mUser;
    public final boolean mUserActionRequired;
    public final int mUserActionRequiredType;
    public final VerificationInfo mVerificationInfo;
    public boolean mWaitForEnableRollbackToComplete;
    public boolean mWaitForIntegrityVerificationToComplete;
    public boolean mWaitForVerificationToComplete;
    public int mRet = 1;
    public String mErrorMessage = null;

    public final boolean isIntegrityVerificationEnabled() {
        return true;
    }

    public VerifyingSession(UserHandle userHandle, File file, IPackageInstallObserver2 iPackageInstallObserver2, PackageInstaller.SessionParams sessionParams, InstallSource installSource, int i, SigningDetails signingDetails, int i2, PackageLite packageLite, boolean z, PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
        this.mUser = userHandle;
        this.mInstallPackageHelper = new InstallPackageHelper(packageManagerService);
        this.mOriginInfo = OriginInfo.fromStagedFile(file);
        this.mObserver = iPackageInstallObserver2;
        this.mInstallFlags = sessionParams.installFlags;
        this.mInstallSource = installSource;
        this.mPackageAbiOverride = sessionParams.abiOverride;
        this.mVerificationInfo = new VerificationInfo(sessionParams.originatingUri, sessionParams.referrerUri, sessionParams.originatingUid, i);
        this.mSigningDetails = signingDetails;
        this.mRequiredInstalledVersionCode = sessionParams.requiredInstalledVersionCode;
        DataLoaderParams dataLoaderParams = sessionParams.dataLoaderParams;
        this.mDataLoaderType = dataLoaderParams != null ? dataLoaderParams.getType() : 0;
        this.mSessionId = i2;
        this.mPackageLite = packageLite;
        this.mUserActionRequired = z;
        this.mUserActionRequiredType = sessionParams.requireUserAction;
        this.mIsInherit = sessionParams.mode == 2;
        this.mIsStaged = sessionParams.isStaged;
    }

    public String toString() {
        return "VerifyingSession{" + Integer.toHexString(System.identityHashCode(this)) + " file=" + this.mOriginInfo.mFile + "}";
    }

    public void handleStartVerify() {
        PackageInfoLite minimalPackageInfo = PackageManagerServiceUtils.getMinimalPackageInfo(this.mPm.mContext, this.mPackageLite, this.mOriginInfo.mResolvedPath, this.mInstallFlags, this.mPackageAbiOverride);
        Pair<Integer, String> verifyReplacingVersionCode = this.mInstallPackageHelper.verifyReplacingVersionCode(minimalPackageInfo, this.mRequiredInstalledVersionCode, this.mInstallFlags);
        setReturnCode(((Integer) verifyReplacingVersionCode.first).intValue(), (String) verifyReplacingVersionCode.second);
        if (this.mRet == 1 && !this.mOriginInfo.mExisting) {
            if (!isApex()) {
                sendApkVerificationRequest(minimalPackageInfo);
            }
            if ((this.mInstallFlags & 262144) != 0) {
                sendEnableRollbackRequest();
            }
        }
    }

    public final void sendApkVerificationRequest(PackageInfoLite packageInfoLite) {
        PackageManagerService packageManagerService = this.mPm;
        int i = packageManagerService.mPendingVerificationToken;
        packageManagerService.mPendingVerificationToken = i + 1;
        PackageVerificationState packageVerificationState = new PackageVerificationState(this);
        this.mPm.mPendingVerification.append(i, packageVerificationState);
        sendIntegrityVerificationRequest(i, packageInfoLite, packageVerificationState);
        sendPackageVerificationRequest(i, packageInfoLite, packageVerificationState);
        if (packageVerificationState.areAllVerificationsComplete()) {
            this.mPm.mPendingVerification.remove(i);
        }
    }

    public void sendEnableRollbackRequest() {
        PackageManagerService packageManagerService = this.mPm;
        int i = packageManagerService.mPendingEnableRollbackToken;
        packageManagerService.mPendingEnableRollbackToken = i + 1;
        Trace.asyncTraceBegin(262144L, "enable_rollback", i);
        this.mPm.mPendingEnableRollback.append(i, this);
        Intent intent = new Intent("android.intent.action.PACKAGE_ENABLE_ROLLBACK");
        intent.putExtra("android.content.pm.extra.ENABLE_ROLLBACK_TOKEN", i);
        intent.putExtra("android.content.pm.extra.ENABLE_ROLLBACK_SESSION_ID", this.mSessionId);
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(268435457);
        intent.addFlags(67108864);
        this.mPm.mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM, "android.permission.PACKAGE_ROLLBACK_AGENT");
        this.mWaitForEnableRollbackToComplete = true;
        long j = DeviceConfig.getLong("rollback", "enable_rollback_timeout", 10000L);
        long j2 = j >= 0 ? j : 10000L;
        Message obtainMessage = this.mPm.mHandler.obtainMessage(22);
        obtainMessage.arg1 = i;
        obtainMessage.arg2 = this.mSessionId;
        this.mPm.mHandler.sendMessageDelayed(obtainMessage, j2);
    }

    public void sendIntegrityVerificationRequest(final int i, PackageInfoLite packageInfoLite, PackageVerificationState packageVerificationState) {
        if (!isIntegrityVerificationEnabled()) {
            packageVerificationState.setIntegrityVerificationResult(1);
            return;
        }
        Intent intent = new Intent("android.intent.action.PACKAGE_NEEDS_INTEGRITY_VERIFICATION");
        intent.setDataAndType(Uri.fromFile(new File(this.mOriginInfo.mResolvedPath)), "application/vnd.android.package-archive");
        intent.addFlags(1342177281);
        intent.putExtra("android.content.pm.extra.VERIFICATION_ID", i);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", packageInfoLite.packageName);
        intent.putExtra("android.intent.extra.VERSION_CODE", packageInfoLite.versionCode);
        intent.putExtra("android.intent.extra.LONG_VERSION_CODE", packageInfoLite.getLongVersionCode());
        populateInstallerExtras(intent);
        intent.setPackage(PackageManagerShellCommandDataLoader.PACKAGE);
        this.mPm.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.SYSTEM, null, -1, BroadcastOptions.makeBasic().toBundle(), new BroadcastReceiver() { // from class: com.android.server.pm.VerifyingSession.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent2) {
                Message obtainMessage = VerifyingSession.this.mPm.mHandler.obtainMessage(26);
                obtainMessage.arg1 = i;
                VerifyingSession.this.mPm.mHandler.sendMessageDelayed(obtainMessage, VerifyingSession.this.getIntegrityVerificationTimeout());
            }
        }, null, 0, null, null);
        Trace.asyncTraceBegin(262144L, "integrity_verification", i);
        this.mWaitForIntegrityVerificationToComplete = true;
    }

    public final long getIntegrityVerificationTimeout() {
        return Math.max(Settings.Global.getLong(this.mPm.mContext.getContentResolver(), "app_integrity_verification_timeout", 30000L), 30000L);
    }

    public final void sendPackageVerificationRequest(final int i, PackageInfoLite packageInfoLite, PackageVerificationState packageVerificationState) {
        ArrayList arrayList;
        boolean z;
        String str;
        boolean z2;
        String str2;
        int i2;
        String str3;
        String str4;
        String str5;
        String str6;
        Intent intent;
        String str7;
        PackageVerificationResponse packageVerificationResponse;
        String str8;
        String str9;
        String str10;
        Object obj;
        UserHandle user = getUser();
        if (user == UserHandle.ALL) {
            user = UserHandle.SYSTEM;
        }
        UserHandle userHandle = user;
        int identifier = userHandle.getIdentifier();
        List<String> asList = Arrays.asList(this.mPm.mRequiredVerifierPackages);
        int i3 = this.mInstallFlags;
        if ((i3 & 32) != 0 && (i3 & 524288) == 0) {
            String str11 = SystemProperties.get("debug.pm.adb_verifier_override_packages", "");
            if (!TextUtils.isEmpty(str11)) {
                String[] split = str11.split(";");
                ArrayList arrayList2 = new ArrayList();
                for (String str12 : split) {
                    if (!TextUtils.isEmpty(str12) && packageExists(str12)) {
                        arrayList2.add(str12);
                    }
                }
                if (arrayList2.size() > 0 && !isAdbVerificationEnabled(packageInfoLite, identifier, true)) {
                    arrayList = arrayList2;
                    z = true;
                    if (!this.mOriginInfo.mExisting || !isVerificationEnabled(packageInfoLite, identifier, arrayList)) {
                        packageVerificationState.passRequiredVerification();
                    }
                    Computer snapshotComputer = this.mPm.snapshotComputer();
                    for (int size = arrayList.size() - 1; size >= 0; size--) {
                        if (!snapshotComputer.isApplicationEffectivelyEnabled(arrayList.get(size), 1000)) {
                            Slog.w("PackageManager", "Required verifier: " + arrayList.get(size) + " is disabled");
                            arrayList.remove(size);
                        }
                    }
                    for (String str13 : arrayList) {
                        packageVerificationState.addRequiredVerifierUid(snapshotComputer.getPackageUid(str13, 268435456L, identifier));
                    }
                    String str14 = "android.intent.action.PACKAGE_NEEDS_VERIFICATION";
                    Intent intent2 = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
                    intent2.addFlags(268435456);
                    String str15 = "application/vnd.android.package-archive";
                    intent2.setDataAndType(Uri.fromFile(new File(this.mOriginInfo.mResolvedPath)), "application/vnd.android.package-archive");
                    intent2.addFlags(1);
                    ParceledListSlice<ResolveInfo> queryIntentReceivers = this.mPm.queryIntentReceivers(snapshotComputer, intent2, "application/vnd.android.package-archive", 0L, identifier);
                    intent2.putExtra("android.content.pm.extra.VERIFICATION_ID", i);
                    intent2.putExtra("android.content.pm.extra.VERIFICATION_INSTALL_FLAGS", this.mInstallFlags);
                    intent2.putExtra("android.content.pm.extra.VERIFICATION_PACKAGE_NAME", packageInfoLite.packageName);
                    intent2.putExtra("android.content.pm.extra.VERIFICATION_VERSION_CODE", packageInfoLite.versionCode);
                    int i4 = identifier;
                    intent2.putExtra("android.content.pm.extra.VERIFICATION_LONG_VERSION_CODE", packageInfoLite.getLongVersionCode());
                    String baseApkPath = this.mPackageLite.getBaseApkPath();
                    String[] splitApkPaths = this.mPackageLite.getSplitApkPaths();
                    String str16 = "android.content.pm.extra.VERIFICATION_ID";
                    if (IncrementalManager.isIncrementalPath(baseApkPath)) {
                        String buildVerificationRootHashString = PackageManagerServiceUtils.buildVerificationRootHashString(baseApkPath, splitApkPaths);
                        intent2.putExtra("android.content.pm.extra.VERIFICATION_ROOT_HASH", buildVerificationRootHashString);
                        str = buildVerificationRootHashString;
                    } else {
                        str = null;
                    }
                    String str17 = "android.content.pm.extra.DATA_LOADER_TYPE";
                    intent2.putExtra("android.content.pm.extra.DATA_LOADER_TYPE", this.mDataLoaderType);
                    String str18 = "android.content.pm.extra.SESSION_ID";
                    intent2.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
                    intent2.putExtra("android.content.pm.extra.USER_ACTION_REQUIRED", this.mUserActionRequired);
                    populateInstallerExtras(intent2);
                    boolean z3 = this.mDataLoaderType == 2 && this.mSigningDetails.getSignatureSchemeVersion() == 4 && getDefaultVerificationResponse() == 1;
                    final long verificationTimeout = VerificationUtils.getVerificationTimeout(this.mPm.mContext, z3);
                    List<ComponentName> matchVerifiers = matchVerifiers(packageInfoLite, queryIntentReceivers.getList(), packageVerificationState);
                    if (packageInfoLite.isSdkLibrary) {
                        if (matchVerifiers == null) {
                            matchVerifiers = new ArrayList();
                        }
                        z2 = z3;
                        str2 = "android.content.pm.extra.VERIFICATION_ROOT_HASH";
                        matchVerifiers.add(new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, "com.android.server.sdksandbox.SdkSandboxVerifierReceiver"));
                        packageVerificationState.addSufficientVerifier(Process.myUid());
                    } else {
                        z2 = z3;
                        str2 = "android.content.pm.extra.VERIFICATION_ROOT_HASH";
                    }
                    DeviceIdleInternal deviceIdleInternal = (DeviceIdleInternal) this.mPm.mInjector.getLocalService(DeviceIdleInternal.class);
                    BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
                    makeBasic.setTemporaryAppAllowlist(verificationTimeout, 0, 305, "");
                    if (matchVerifiers != null) {
                        int size2 = matchVerifiers.size();
                        if (size2 == 0) {
                            Slog.i("PackageManager", "Additional verifiers required, but none installed.");
                            setReturnCode(-22, "Additional verifiers required, but none installed.");
                        } else {
                            int i5 = 0;
                            while (i5 < size2) {
                                ComponentName componentName = matchVerifiers.get(i5);
                                deviceIdleInternal.addPowerSaveTempWhitelistApp(Process.myUid(), componentName.getPackageName(), verificationTimeout, i4, false, 305, "package verifier");
                                int i6 = size2;
                                Intent intent3 = new Intent(intent2);
                                intent3.setComponent(componentName);
                                this.mPm.mContext.sendBroadcastAsUser(intent3, userHandle, null, makeBasic.toBundle());
                                i5++;
                                size2 = i6;
                                matchVerifiers = matchVerifiers;
                                deviceIdleInternal = deviceIdleInternal;
                            }
                        }
                    }
                    DeviceIdleInternal deviceIdleInternal2 = deviceIdleInternal;
                    if (arrayList.size() == 0) {
                        Slog.e("PackageManager", "No required verifiers");
                        return;
                    }
                    int i7 = getDefaultVerificationResponse() == 1 ? 2 : -1;
                    for (String str19 : arrayList) {
                        int i8 = i7;
                        UserHandle userHandle2 = userHandle;
                        int i9 = i4;
                        int packageUid = snapshotComputer.getPackageUid(str19, 268435456L, i9);
                        if (!z || arrayList.size() == 1) {
                            i2 = i;
                            str3 = str18;
                            str4 = str16;
                            str5 = str2;
                            Intent intent4 = new Intent(intent2);
                            if (!z) {
                                intent4.setComponent(matchComponentForVerifier(str19, queryIntentReceivers.getList()));
                            } else {
                                intent4.setPackage(str19);
                            }
                            str6 = "android.permission.PACKAGE_VERIFICATION_AGENT";
                            intent = intent4;
                        } else {
                            Intent intent5 = new Intent(str14);
                            intent5.addFlags(1);
                            intent5.addFlags(268435456);
                            intent5.addFlags(32);
                            intent5.setDataAndType(Uri.fromFile(new File(this.mOriginInfo.mResolvedPath)), str15);
                            intent5.putExtra(str18, this.mSessionId);
                            intent5.putExtra(str17, this.mDataLoaderType);
                            str5 = str2;
                            if (str != null) {
                                intent5.putExtra(str5, str);
                            }
                            intent5.setPackage(str19);
                            i2 = i;
                            str3 = str18;
                            str4 = str16;
                            intent5.putExtra(str4, -i2);
                            intent = intent5;
                            str6 = null;
                        }
                        deviceIdleInternal2.addPowerSaveTempWhitelistApp(Process.myUid(), str19, verificationTimeout, i9, false, 305, "package verifier");
                        PackageVerificationResponse packageVerificationResponse2 = new PackageVerificationResponse(i8, packageUid);
                        if (z2) {
                            str7 = str4;
                            str10 = str3;
                            obj = null;
                            packageVerificationResponse = packageVerificationResponse2;
                            str8 = str14;
                            str9 = str15;
                            startVerificationTimeoutCountdown(i, z2, packageVerificationResponse2, verificationTimeout);
                        } else {
                            str7 = str4;
                            packageVerificationResponse = packageVerificationResponse2;
                            str8 = str14;
                            str9 = str15;
                            str10 = str3;
                            obj = null;
                        }
                        final boolean z4 = z2;
                        final PackageVerificationResponse packageVerificationResponse3 = packageVerificationResponse;
                        this.mPm.mContext.sendOrderedBroadcastAsUser(intent, userHandle2, str6, -1, makeBasic.toBundle(), new BroadcastReceiver() { // from class: com.android.server.pm.VerifyingSession.2
                            @Override // android.content.BroadcastReceiver
                            public void onReceive(Context context, Intent intent6) {
                                boolean z5 = z4;
                                if (z5) {
                                    return;
                                }
                                VerifyingSession.this.startVerificationTimeoutCountdown(i, z5, packageVerificationResponse3, verificationTimeout);
                            }
                        }, null, 0, null, null);
                        i7 = i8;
                        str18 = str10;
                        str15 = str9;
                        userHandle = userHandle2;
                        intent2 = intent2;
                        str = str;
                        str17 = str17;
                        snapshotComputer = snapshotComputer;
                        arrayList = arrayList;
                        i4 = i9;
                        str14 = str8;
                        str2 = str5;
                        str16 = str7;
                    }
                    Trace.asyncTraceBegin(262144L, "verification", i);
                    this.mWaitForVerificationToComplete = true;
                    return;
                }
            }
        }
        arrayList = asList;
        z = false;
        if (!this.mOriginInfo.mExisting) {
        }
        packageVerificationState.passRequiredVerification();
    }

    public final void startVerificationTimeoutCountdown(int i, boolean z, PackageVerificationResponse packageVerificationResponse, long j) {
        Message obtainMessage = this.mPm.mHandler.obtainMessage(16);
        obtainMessage.arg1 = i;
        obtainMessage.arg2 = z ? 1 : 0;
        obtainMessage.obj = packageVerificationResponse;
        this.mPm.mHandler.sendMessageDelayed(obtainMessage, j);
    }

    public int getDefaultVerificationResponse() {
        if (this.mPm.mUserManager.hasUserRestriction("ensure_verify_apps", getUser().getIdentifier())) {
            return -1;
        }
        return Settings.Global.getInt(this.mPm.mContext.getContentResolver(), "verifier_default_response", 1);
    }

    public final boolean packageExists(String str) {
        return this.mPm.snapshotComputer().getPackageStateInternal(str) != null;
    }

    public final boolean isAdbVerificationEnabled(PackageInfoLite packageInfoLite, int i, boolean z) {
        if (this.mPm.isUserRestricted(i, "ensure_verify_apps")) {
            return true;
        }
        if (!z) {
            return Settings.Global.getInt(this.mPm.mContext.getContentResolver(), "verifier_verify_adb_installs", 1) != 0;
        } else if (packageExists(packageInfoLite.packageName)) {
            return !packageInfoLite.debuggable;
        } else {
            return true;
        }
    }

    public final boolean isVerificationEnabled(PackageInfoLite packageInfoLite, int i, List<String> list) {
        ActivityInfo activityInfo;
        VerificationInfo verificationInfo = this.mVerificationInfo;
        int i2 = verificationInfo == null ? -1 : verificationInfo.mInstallerUid;
        int i3 = this.mInstallFlags;
        boolean z = (524288 & i3) != 0;
        if ((i3 & 32) != 0) {
            return isAdbVerificationEnabled(packageInfoLite, i, z);
        }
        if (z) {
            return false;
        }
        if (isInstant() && (activityInfo = this.mPm.mInstantAppInstallerActivity) != null) {
            String str = activityInfo.packageName;
            for (String str2 : list) {
                if (str.equals(str2)) {
                    try {
                        ((AppOpsManager) this.mPm.mInjector.getSystemService(AppOpsManager.class)).checkPackage(i2, str2);
                        return false;
                    } catch (SecurityException unused) {
                        continue;
                    }
                }
            }
        }
        return true;
    }

    public final List<ComponentName> matchVerifiers(PackageInfoLite packageInfoLite, List<ResolveInfo> list, PackageVerificationState packageVerificationState) {
        int uidForVerifier;
        VerifierInfo[] verifierInfoArr = packageInfoLite.verifiers;
        if (verifierInfoArr == null || verifierInfoArr.length == 0) {
            return null;
        }
        int length = verifierInfoArr.length;
        ArrayList arrayList = new ArrayList(length + 1);
        for (int i = 0; i < length; i++) {
            VerifierInfo verifierInfo = packageInfoLite.verifiers[i];
            ComponentName matchComponentForVerifier = matchComponentForVerifier(verifierInfo.packageName, list);
            if (matchComponentForVerifier != null && (uidForVerifier = this.mInstallPackageHelper.getUidForVerifier(verifierInfo)) != -1) {
                arrayList.add(matchComponentForVerifier);
                packageVerificationState.addSufficientVerifier(uidForVerifier);
            }
        }
        return arrayList;
    }

    public static ComponentName matchComponentForVerifier(String str, List<ResolveInfo> list) {
        ActivityInfo activityInfo;
        int size = list.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                activityInfo = null;
                break;
            }
            ResolveInfo resolveInfo = list.get(i);
            ActivityInfo activityInfo2 = resolveInfo.activityInfo;
            if (activityInfo2 != null && str.equals(activityInfo2.packageName)) {
                activityInfo = resolveInfo.activityInfo;
                break;
            }
            i++;
        }
        if (activityInfo == null) {
            return null;
        }
        return new ComponentName(activityInfo.packageName, activityInfo.name);
    }

    public void populateInstallerExtras(Intent intent) {
        intent.putExtra("android.content.pm.extra.VERIFICATION_INSTALLER_PACKAGE", this.mInstallSource.mInitiatingPackageName);
        VerificationInfo verificationInfo = this.mVerificationInfo;
        if (verificationInfo != null) {
            Uri uri = verificationInfo.mOriginatingUri;
            if (uri != null) {
                intent.putExtra("android.intent.extra.ORIGINATING_URI", uri);
            }
            Uri uri2 = this.mVerificationInfo.mReferrer;
            if (uri2 != null) {
                intent.putExtra("android.intent.extra.REFERRER", uri2);
            }
            int i = this.mVerificationInfo.mOriginatingUid;
            if (i >= 0) {
                intent.putExtra("android.intent.extra.ORIGINATING_UID", i);
            }
            int i2 = this.mVerificationInfo.mInstallerUid;
            if (i2 >= 0) {
                intent.putExtra("android.content.pm.extra.VERIFICATION_INSTALLER_UID", i2);
            }
        }
    }

    public void setReturnCode(int i, String str) {
        if (this.mRet == 1) {
            this.mRet = i;
            this.mErrorMessage = str;
        }
    }

    public void handleVerificationFinished() {
        this.mWaitForVerificationToComplete = false;
        handleReturnCode();
    }

    public void handleIntegrityVerificationFinished() {
        this.mWaitForIntegrityVerificationToComplete = false;
        handleReturnCode();
    }

    public void handleRollbackEnabled() {
        this.mWaitForEnableRollbackToComplete = false;
        handleReturnCode();
    }

    public void handleReturnCode() {
        if (this.mWaitForVerificationToComplete || this.mWaitForIntegrityVerificationToComplete || this.mWaitForEnableRollbackToComplete) {
            return;
        }
        sendVerificationCompleteNotification();
        if (this.mRet != 1) {
            PackageMetrics.onVerificationFailed(this);
        }
    }

    public final void sendVerificationCompleteNotification() {
        MultiPackageVerifyingSession multiPackageVerifyingSession = this.mParentVerifyingSession;
        if (multiPackageVerifyingSession != null) {
            multiPackageVerifyingSession.trySendVerificationCompleteNotification(this);
            return;
        }
        try {
            this.mObserver.onPackageInstalled((String) null, this.mRet, this.mErrorMessage, new Bundle());
        } catch (RemoteException unused) {
            Slog.i("PackageManager", "Observer no longer exists.");
        }
    }

    public final void start() {
        Trace.asyncTraceEnd(262144L, "queueVerify", System.identityHashCode(this));
        Trace.traceBegin(262144L, "start");
        handleStartVerify();
        handleReturnCode();
        Trace.traceEnd(262144L);
    }

    public void verifyStage() {
        Trace.asyncTraceBegin(262144L, "queueVerify", System.identityHashCode(this));
        this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.VerifyingSession$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VerifyingSession.this.start();
            }
        });
    }

    public void verifyStage(List<VerifyingSession> list) throws PackageManagerException {
        final MultiPackageVerifyingSession multiPackageVerifyingSession = new MultiPackageVerifyingSession(this, list);
        this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.VerifyingSession$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MultiPackageVerifyingSession.this.start();
            }
        });
    }

    public int getRet() {
        return this.mRet;
    }

    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getDataLoaderType() {
        return this.mDataLoaderType;
    }

    public int getUserActionRequiredType() {
        return this.mUserActionRequiredType;
    }

    public boolean isInstant() {
        return (this.mInstallFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
    }

    public boolean isInherit() {
        return this.mIsInherit;
    }

    public int getInstallerPackageUid() {
        return this.mInstallSource.mInstallerPackageUid;
    }

    public boolean isApex() {
        return (this.mInstallFlags & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0;
    }

    public boolean isStaged() {
        return this.mIsStaged;
    }
}
