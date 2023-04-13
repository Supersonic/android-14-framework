package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.content.pm.DataLoaderParams;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.PackageInfoLite;
import android.content.pm.PackageInstaller;
import android.content.pm.SigningDetails;
import android.content.pm.parsing.PackageLite;
import android.os.Environment;
import android.os.Trace;
import android.os.UserHandle;
import android.os.incremental.IncrementalManager;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.content.F2fsUtils;
import com.android.internal.content.InstallLocationUtils;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.Preconditions;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.InstallingSession;
import com.android.server.p011pm.parsing.PackageParser2;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.InstallingSession */
/* loaded from: classes2.dex */
public class InstallingSession {
    public final List<String> mAllowlistedRestrictedPermissions;
    public final boolean mApplicationEnabledSettingPersistent;
    public final int mAutoRevokePermissionsMode;
    public final int mDataLoaderType;
    public final boolean mForceQueryableOverride;
    public int mInstallFlags;
    public final InstallPackageHelper mInstallPackageHelper;
    public final int mInstallReason;
    public final int mInstallScenario;
    public final InstallSource mInstallSource;
    public final boolean mIsInherit;
    public final MoveInfo mMoveInfo;
    public final IPackageInstallObserver2 mObserver;
    public final OriginInfo mOriginInfo;
    public final String mPackageAbiOverride;
    public final PackageLite mPackageLite;
    public final int mPackageSource;
    public MultiPackageInstallingSession mParentInstallingSession;
    public final ArrayMap<String, Integer> mPermissionStates;
    public final PackageManagerService mPm;
    public final RemovePackageHelper mRemovePackageHelper;
    public final int mRequireUserAction;
    public final long mRequiredInstalledVersionCode;
    public int mRet;
    public final int mSessionId;
    public final SigningDetails mSigningDetails;
    public int mTraceCookie;
    public String mTraceMethod;
    public final UserHandle mUser;
    public final String mVolumeUuid;

    public InstallingSession(OriginInfo originInfo, MoveInfo moveInfo, IPackageInstallObserver2 iPackageInstallObserver2, int i, InstallSource installSource, String str, UserHandle userHandle, String str2, int i2, PackageLite packageLite, PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
        this.mUser = userHandle;
        this.mInstallPackageHelper = new InstallPackageHelper(packageManagerService);
        this.mRemovePackageHelper = new RemovePackageHelper(packageManagerService);
        this.mOriginInfo = originInfo;
        this.mMoveInfo = moveInfo;
        this.mObserver = iPackageInstallObserver2;
        this.mInstallFlags = i;
        this.mInstallSource = (InstallSource) Preconditions.checkNotNull(installSource);
        this.mVolumeUuid = str;
        this.mPackageAbiOverride = str2;
        this.mPermissionStates = new ArrayMap<>();
        this.mAllowlistedRestrictedPermissions = null;
        this.mAutoRevokePermissionsMode = 3;
        this.mSigningDetails = SigningDetails.UNKNOWN;
        this.mInstallReason = 0;
        this.mInstallScenario = 0;
        this.mForceQueryableOverride = false;
        this.mDataLoaderType = 0;
        this.mRequiredInstalledVersionCode = -1L;
        this.mPackageSource = i2;
        this.mPackageLite = packageLite;
        this.mIsInherit = false;
        this.mSessionId = -1;
        this.mRequireUserAction = 0;
        this.mApplicationEnabledSettingPersistent = false;
    }

    public InstallingSession(int i, File file, IPackageInstallObserver2 iPackageInstallObserver2, PackageInstaller.SessionParams sessionParams, InstallSource installSource, UserHandle userHandle, SigningDetails signingDetails, int i2, PackageLite packageLite, PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
        this.mUser = userHandle;
        this.mInstallPackageHelper = new InstallPackageHelper(packageManagerService);
        this.mRemovePackageHelper = new RemovePackageHelper(packageManagerService);
        this.mOriginInfo = OriginInfo.fromStagedFile(file);
        this.mMoveInfo = null;
        this.mInstallReason = fixUpInstallReason(installSource.mInstallerPackageName, i2, sessionParams.installReason);
        this.mInstallScenario = sessionParams.installScenario;
        this.mObserver = iPackageInstallObserver2;
        this.mInstallFlags = sessionParams.installFlags;
        this.mInstallSource = installSource;
        this.mVolumeUuid = sessionParams.volumeUuid;
        this.mPackageAbiOverride = sessionParams.abiOverride;
        this.mPermissionStates = sessionParams.getPermissionStates();
        this.mAllowlistedRestrictedPermissions = sessionParams.whitelistedRestrictedPermissions;
        this.mAutoRevokePermissionsMode = sessionParams.autoRevokePermissionsMode;
        this.mSigningDetails = signingDetails;
        this.mForceQueryableOverride = sessionParams.forceQueryableOverride;
        DataLoaderParams dataLoaderParams = sessionParams.dataLoaderParams;
        this.mDataLoaderType = dataLoaderParams != null ? dataLoaderParams.getType() : 0;
        this.mRequiredInstalledVersionCode = sessionParams.requiredInstalledVersionCode;
        this.mPackageSource = sessionParams.packageSource;
        this.mPackageLite = packageLite;
        this.mIsInherit = sessionParams.mode == 2;
        this.mSessionId = i;
        this.mRequireUserAction = sessionParams.requireUserAction;
        this.mApplicationEnabledSettingPersistent = sessionParams.applicationEnabledSettingPersistent;
    }

    public String toString() {
        return "InstallingSession{" + Integer.toHexString(System.identityHashCode(this)) + " file=" + this.mOriginInfo.mFile + "}";
    }

    public final int overrideInstallLocation(String str, int i, int i2) {
        OriginInfo originInfo = this.mOriginInfo;
        if (originInfo.mStaged) {
            if (originInfo.mFile != null) {
                this.mInstallFlags |= 16;
            } else {
                throw new IllegalStateException("Invalid stage location");
            }
        }
        if (i < 0) {
            return InstallLocationUtils.getInstallationErrorCode(i);
        }
        PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(str);
        AndroidPackage androidPackage = packageStateInternal == null ? null : packageStateInternal.getAndroidPackage();
        if (androidPackage != null) {
            i = InstallLocationUtils.installLocationPolicy(i2, i, this.mInstallFlags, packageStateInternal.isSystem(), androidPackage.isExternalStorage());
        }
        int i3 = this.mInstallFlags;
        if (!((i3 & 16) != 0)) {
            if (i == 2) {
                this.mInstallFlags = i3 & (-17);
            } else {
                this.mInstallFlags = i3 | 16;
            }
        }
        return 1;
    }

    public final void handleStartCopy(InstallRequest installRequest) {
        int i;
        int i2 = this.mInstallFlags;
        if ((131072 & i2) != 0) {
            this.mRet = 1;
            return;
        }
        PackageInfoLite minimalPackageInfo = PackageManagerServiceUtils.getMinimalPackageInfo(this.mPm.mContext, this.mPackageLite, this.mOriginInfo.mResolvedPath, i2, this.mPackageAbiOverride);
        int i3 = this.mInstallFlags;
        if ((2097152 & i3) != 0) {
            int intValue = ((Integer) this.mInstallPackageHelper.verifyReplacingVersionCode(minimalPackageInfo, this.mRequiredInstalledVersionCode, i3).first).intValue();
            this.mRet = intValue;
            if (intValue != 1) {
                installRequest.setError(intValue, "Failed to verify version code");
                return;
            }
        }
        boolean z = (this.mInstallFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
        if (PackageManagerService.DEBUG_INSTANT && z) {
            Slog.v("PackageManager", "pkgLite for install: " + minimalPackageInfo);
        }
        OriginInfo originInfo = this.mOriginInfo;
        if (!originInfo.mStaged && (i = minimalPackageInfo.recommendedInstallLocation) == -1) {
            minimalPackageInfo.recommendedInstallLocation = this.mPm.freeCacheForInstallation(i, this.mPackageLite, originInfo.mResolvedPath, this.mPackageAbiOverride, this.mInstallFlags);
        }
        int overrideInstallLocation = overrideInstallLocation(minimalPackageInfo.packageName, minimalPackageInfo.recommendedInstallLocation, minimalPackageInfo.installLocation);
        this.mRet = overrideInstallLocation;
        if (overrideInstallLocation != 1) {
            installRequest.setError(overrideInstallLocation, "Failed to override installation location");
        }
    }

    public final void handleReturnCode(InstallRequest installRequest) {
        processPendingInstall(installRequest);
    }

    public final void processPendingInstall(final InstallRequest installRequest) {
        if (this.mRet == 1) {
            this.mRet = copyApk(installRequest);
        }
        if (this.mRet == 1) {
            F2fsUtils.releaseCompressedBlocks(this.mPm.mContext.getContentResolver(), new File(installRequest.getCodePath()));
        }
        installRequest.setReturnCode(this.mRet);
        MultiPackageInstallingSession multiPackageInstallingSession = this.mParentInstallingSession;
        if (multiPackageInstallingSession != null) {
            multiPackageInstallingSession.tryProcessInstallRequest(installRequest);
        } else {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.InstallingSession$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    InstallingSession.this.lambda$processPendingInstall$0(installRequest);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processPendingInstall$0(InstallRequest installRequest) {
        processInstallRequests(this.mRet == 1, Collections.singletonList(installRequest));
    }

    public final int copyApk(InstallRequest installRequest) {
        if (this.mMoveInfo == null) {
            return copyApkForFileInstall(installRequest);
        }
        return copyApkForMoveInstall(installRequest);
    }

    public final int copyApkForFileInstall(InstallRequest installRequest) {
        Trace.traceBegin(262144L, "copyApk");
        try {
            OriginInfo originInfo = this.mOriginInfo;
            if (originInfo.mStaged) {
                installRequest.setCodeFile(originInfo.mFile);
                return 1;
            }
            installRequest.setCodeFile(this.mPm.mInstallerService.allocateStageDirLegacy(this.mVolumeUuid, (this.mInstallFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0));
            int copyPackage = PackageManagerServiceUtils.copyPackage(this.mOriginInfo.mFile.getAbsolutePath(), installRequest.getCodeFile());
            if (copyPackage != 1) {
                Slog.e("PackageManager", "Failed to copy package");
                installRequest.setError(copyPackage, "Failed to copy package");
                return copyPackage;
            }
            boolean isIncrementalPath = IncrementalManager.isIncrementalPath(installRequest.getCodeFile().getAbsolutePath());
            File file = new File(installRequest.getCodeFile(), "lib");
            NativeLibraryHelper.Handle handle = null;
            try {
                handle = NativeLibraryHelper.Handle.create(installRequest.getCodeFile());
                copyPackage = NativeLibraryHelper.copyNativeBinariesWithOverride(handle, file, installRequest.getAbiOverride(), isIncrementalPath);
                if (copyPackage != 1) {
                    installRequest.setError(copyPackage, "Failed to copy native libraries");
                }
            } catch (IOException e) {
                Slog.e("PackageManager", "Copying native libraries failed", e);
                installRequest.setError(PackageManagerException.ofInternalError("Copying native libraries failed", -1));
            }
            IoUtils.closeQuietly(handle);
            return copyPackage;
        } catch (IOException e2) {
            Slog.w("PackageManager", "Failed to create copy file: " + e2);
            installRequest.setError(-4, "Failed to create copy file");
            return -4;
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public final int copyApkForMoveInstall(InstallRequest installRequest) {
        synchronized (this.mPm.mInstallLock) {
            try {
                Installer installer = this.mPm.mInstaller;
                MoveInfo moveInfo = this.mMoveInfo;
                installer.moveCompleteApp(moveInfo.mFromUuid, moveInfo.mToUuid, moveInfo.mPackageName, moveInfo.mAppId, moveInfo.mSeInfo, moveInfo.mTargetSdkVersion, moveInfo.mFromCodePath);
            } catch (Installer.InstallerException e) {
                installRequest.setError(PackageManagerException.ofInternalError("Failed to move app", -2));
                Slog.w("PackageManager", "Failed to move app", e);
                return -110;
            }
        }
        installRequest.setCodeFile(new File(Environment.getDataAppDirectory(this.mMoveInfo.mToUuid), new File(this.mMoveInfo.mFromCodePath).getName()));
        return 1;
    }

    public final int fixUpInstallReason(String str, int i, int i2) {
        if (this.mPm.snapshotComputer().checkUidPermission("android.permission.INSTALL_PACKAGES", i) == 0) {
            return i2;
        }
        String deviceOwnerOrProfileOwnerPackage = this.mPm.mProtectedPackages.getDeviceOwnerOrProfileOwnerPackage(UserHandle.getUserId(i));
        if (deviceOwnerOrProfileOwnerPackage == null || !deviceOwnerOrProfileOwnerPackage.equals(str)) {
            if (i2 == 1) {
                return 0;
            }
            return i2;
        }
        return 1;
    }

    public void installStage() {
        setTraceMethod("installStage").setTraceCookie(System.identityHashCode(this));
        Trace.asyncTraceBegin(262144L, "installStage", System.identityHashCode(this));
        Trace.asyncTraceBegin(262144L, "queueInstall", System.identityHashCode(this));
        this.mPm.mHandler.post(new InstallingSession$$ExternalSyntheticLambda0(this));
    }

    public void installStage(List<InstallingSession> list) throws PackageManagerException {
        final MultiPackageInstallingSession multiPackageInstallingSession = new MultiPackageInstallingSession(getUser(), list, this.mPm);
        setTraceMethod("installStageMultiPackage").setTraceCookie(System.identityHashCode(multiPackageInstallingSession));
        Trace.asyncTraceBegin(262144L, "installStageMultiPackage", System.identityHashCode(multiPackageInstallingSession));
        Trace.asyncTraceBegin(262144L, "queueInstall", System.identityHashCode(multiPackageInstallingSession));
        this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.InstallingSession$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                InstallingSession.MultiPackageInstallingSession.this.start();
            }
        });
    }

    public void movePackage() {
        setTraceMethod("movePackage").setTraceCookie(System.identityHashCode(this));
        Trace.asyncTraceBegin(262144L, "movePackage", System.identityHashCode(this));
        Trace.asyncTraceBegin(262144L, "queueInstall", System.identityHashCode(this));
        this.mPm.mHandler.post(new InstallingSession$$ExternalSyntheticLambda0(this));
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public final void start() {
        Trace.asyncTraceEnd(262144L, "queueInstall", System.identityHashCode(this));
        Trace.traceBegin(262144L, "startInstall");
        InstallRequest installRequest = new InstallRequest(this);
        handleStartCopy(installRequest);
        handleReturnCode(installRequest);
        Trace.traceEnd(262144L);
    }

    public final InstallingSession setTraceMethod(String str) {
        this.mTraceMethod = str;
        return this;
    }

    public final void setTraceCookie(int i) {
        this.mTraceCookie = i;
    }

    public final void processInstallRequests(boolean z, List<InstallRequest> list) {
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (InstallRequest installRequest : list) {
            if ((installRequest.getInstallFlags() & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0) {
                arrayList.add(installRequest);
            } else {
                arrayList2.add(installRequest);
            }
        }
        if (!arrayList.isEmpty() && !arrayList2.isEmpty()) {
            throw new IllegalStateException("Attempted to do a multi package install of both APEXes and APKs");
        }
        if (arrayList.isEmpty()) {
            processApkInstallRequests(z, list);
        } else if (z) {
            new Thread(new Runnable() { // from class: com.android.server.pm.InstallingSession$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    InstallingSession.this.lambda$processInstallRequests$1(arrayList);
                }
            }, "installApexPackages").start();
        } else {
            this.mPm.notifyInstallObserver((InstallRequest) arrayList.get(0));
        }
    }

    public final void processApkInstallRequests(boolean z, List<InstallRequest> list) {
        if (!z) {
            for (InstallRequest installRequest : list) {
                if (installRequest.getReturnCode() != 1) {
                    cleanUpForFailedInstall(installRequest);
                }
            }
        } else {
            this.mInstallPackageHelper.installPackagesTraced(list);
            for (InstallRequest installRequest2 : list) {
                installRequest2.onInstallCompleted();
                doPostInstall(installRequest2);
            }
        }
        for (InstallRequest installRequest3 : list) {
            this.mInstallPackageHelper.restoreAndPostInstall(installRequest3);
        }
    }

    public final void doPostInstall(InstallRequest installRequest) {
        if (this.mMoveInfo != null) {
            if (installRequest.getReturnCode() == 1) {
                RemovePackageHelper removePackageHelper = this.mRemovePackageHelper;
                MoveInfo moveInfo = this.mMoveInfo;
                removePackageHelper.cleanUpForMoveInstall(moveInfo.mFromUuid, moveInfo.mPackageName, moveInfo.mFromCodePath);
                return;
            }
            RemovePackageHelper removePackageHelper2 = this.mRemovePackageHelper;
            MoveInfo moveInfo2 = this.mMoveInfo;
            removePackageHelper2.cleanUpForMoveInstall(moveInfo2.mToUuid, moveInfo2.mPackageName, moveInfo2.mFromCodePath);
        } else if (installRequest.getReturnCode() != 1) {
            this.mRemovePackageHelper.removeCodePath(installRequest.getCodeFile());
        }
    }

    public final void cleanUpForFailedInstall(InstallRequest installRequest) {
        if (installRequest.isInstallMove()) {
            this.mRemovePackageHelper.cleanUpForMoveInstall(installRequest.getMoveToUuid(), installRequest.getMovePackageName(), installRequest.getMoveFromCodePath());
        } else {
            this.mRemovePackageHelper.removeCodePath(installRequest.getCodeFile());
        }
    }

    /* renamed from: installApexPackagesTraced */
    public final void lambda$processInstallRequests$1(List<InstallRequest> list) {
        try {
            Trace.traceBegin(262144L, "installApexPackages");
            installApexPackages(list);
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public final void installApexPackages(final List<InstallRequest> list) {
        if (list.isEmpty()) {
            return;
        }
        if (list.size() != 1) {
            throw new IllegalStateException("Only a non-staged install of a single APEX is supported");
        }
        InstallRequest installRequest = list.get(0);
        try {
            File file = installRequest.getOriginInfo().mResolvedFile;
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                throw PackageManagerException.ofInternalError(file.getAbsolutePath() + " is not a directory", -36);
            } else if (listFiles.length != 1) {
                throw PackageManagerException.ofInternalError("Expected exactly one .apex file under " + file.getAbsolutePath() + " got: " + listFiles.length, -37);
            } else {
                PackageParser2 scanningPackageParser = this.mPm.mInjector.getScanningPackageParser();
                ApexInfo installPackage = this.mPm.mApexManager.installPackage(listFiles[0]);
                installRequest.setApexInfo(installPackage);
                installRequest.setApexModuleName(installPackage.moduleName);
                this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.InstallingSession$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        InstallingSession.this.lambda$installApexPackages$2(list);
                    }
                });
                if (scanningPackageParser != null) {
                    scanningPackageParser.close();
                }
            }
        } catch (PackageManagerException e) {
            installRequest.setError("APEX installation failed", e);
            PackageManagerService.invalidatePackageInfoCache();
            this.mPm.notifyInstallObserver(installRequest);
        }
    }

    public /* synthetic */ void lambda$installApexPackages$2(List list) {
        processApkInstallRequests(true, list);
    }

    /* renamed from: com.android.server.pm.InstallingSession$MultiPackageInstallingSession */
    /* loaded from: classes2.dex */
    public class MultiPackageInstallingSession {
        public final List<InstallingSession> mChildInstallingSessions;
        public final Set<InstallRequest> mCurrentInstallRequests;
        public final PackageManagerService mPm;
        public final UserHandle mUser;

        public MultiPackageInstallingSession(UserHandle userHandle, List<InstallingSession> list, PackageManagerService packageManagerService) throws PackageManagerException {
            InstallingSession.this = r1;
            if (list.size() == 0) {
                throw PackageManagerException.ofInternalError("No child sessions found!", -20);
            }
            this.mPm = packageManagerService;
            this.mUser = userHandle;
            this.mChildInstallingSessions = list;
            for (int i = 0; i < list.size(); i++) {
                list.get(i).mParentInstallingSession = this;
            }
            this.mCurrentInstallRequests = new ArraySet(this.mChildInstallingSessions.size());
        }

        public void start() {
            Trace.asyncTraceEnd(262144L, "queueInstall", System.identityHashCode(this));
            Trace.traceBegin(262144L, "start");
            int size = this.mChildInstallingSessions.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                InstallingSession installingSession = this.mChildInstallingSessions.get(i);
                InstallRequest installRequest = new InstallRequest(installingSession);
                arrayList.add(installRequest);
                installingSession.handleStartCopy(installRequest);
            }
            for (int i2 = 0; i2 < size; i2++) {
                this.mChildInstallingSessions.get(i2).handleReturnCode((InstallRequest) arrayList.get(i2));
            }
            Trace.traceEnd(262144L);
        }

        public void tryProcessInstallRequest(InstallRequest installRequest) {
            final int i;
            this.mCurrentInstallRequests.add(installRequest);
            if (this.mCurrentInstallRequests.size() != this.mChildInstallingSessions.size()) {
                return;
            }
            Iterator<InstallRequest> it = this.mCurrentInstallRequests.iterator();
            while (true) {
                i = 1;
                if (!it.hasNext()) {
                    break;
                }
                InstallRequest next = it.next();
                if (next.getReturnCode() == 0) {
                    return;
                }
                if (next.getReturnCode() != 1) {
                    i = next.getReturnCode();
                    break;
                }
            }
            final ArrayList arrayList = new ArrayList(this.mCurrentInstallRequests.size());
            for (InstallRequest installRequest2 : this.mCurrentInstallRequests) {
                installRequest2.setReturnCode(i);
                arrayList.add(installRequest2);
            }
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.InstallingSession$MultiPackageInstallingSession$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InstallingSession.MultiPackageInstallingSession.this.lambda$tryProcessInstallRequest$0(i, arrayList);
                }
            });
        }

        public /* synthetic */ void lambda$tryProcessInstallRequest$0(int i, List list) {
            InstallingSession.this.processInstallRequests(i == 1, list);
        }

        public String toString() {
            return "MultiPackageInstallingSession{" + Integer.toHexString(System.identityHashCode(this)) + "}";
        }
    }
}
