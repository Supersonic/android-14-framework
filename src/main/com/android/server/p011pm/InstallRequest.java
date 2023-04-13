package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.SigningDetails;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.ExceptionUtils;
import android.util.Slog;
import com.android.server.art.model.DexoptResult;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.server.pm.InstallRequest */
/* loaded from: classes2.dex */
public final class InstallRequest {
    public ApexInfo mApexInfo;
    public String mApexModuleName;
    public int mAppId;
    public boolean mClearCodeCache;
    public int mDexoptStatus;
    public PackageSetting mDisabledPs;
    public AndroidPackage mExistingPackage;
    public PackageFreezer mFreezer;
    public final InstallArgs mInstallArgs;
    public int mInternalErrorCode;
    public boolean mIsInstallForUsers;
    public boolean mIsInstallInherit;
    public ArrayList<AndroidPackage> mLibraryConsumers;
    public String mName;
    public int[] mNewUsers;
    public String mOrigPackage;
    public String mOrigPermission;
    public int[] mOrigUsers;
    public PackageSetting mOriginalPs;
    public final PackageMetrics mPackageMetrics;
    public int mParseFlags;
    public ParsedPackage mParsedPackage;
    public AndroidPackage mPkg;
    public Runnable mPostInstallRunnable;
    public PackageRemovedInfo mRemovedInfo;
    public boolean mReplace;
    public final int mRequireUserAction;
    public int mReturnCode;
    public String mReturnMsg;
    public int mScanFlags;
    public ScanResult mScanResult;
    public final int mSessionId;
    public boolean mSystem;
    public final int mUserId;

    public InstallRequest(InstallingSession installingSession) {
        this.mAppId = -1;
        this.mUserId = installingSession.getUser().getIdentifier();
        this.mInstallArgs = new InstallArgs(installingSession.mOriginInfo, installingSession.mMoveInfo, installingSession.mObserver, installingSession.mInstallFlags, installingSession.mInstallSource, installingSession.mVolumeUuid, installingSession.getUser(), null, installingSession.mPackageAbiOverride, installingSession.mPermissionStates, installingSession.mAllowlistedRestrictedPermissions, installingSession.mAutoRevokePermissionsMode, installingSession.mTraceMethod, installingSession.mTraceCookie, installingSession.mSigningDetails, installingSession.mInstallReason, installingSession.mInstallScenario, installingSession.mForceQueryableOverride, installingSession.mDataLoaderType, installingSession.mPackageSource, installingSession.mApplicationEnabledSettingPersistent);
        this.mPackageMetrics = new PackageMetrics(this);
        this.mIsInstallInherit = installingSession.mIsInherit;
        this.mSessionId = installingSession.mSessionId;
        this.mRequireUserAction = installingSession.mRequireUserAction;
    }

    public InstallRequest(int i, int i2, AndroidPackage androidPackage, int[] iArr, Runnable runnable) {
        this.mAppId = -1;
        this.mUserId = i;
        this.mInstallArgs = null;
        this.mReturnCode = i2;
        this.mPkg = androidPackage;
        this.mNewUsers = iArr;
        this.mPostInstallRunnable = runnable;
        this.mPackageMetrics = new PackageMetrics(this);
        this.mIsInstallForUsers = true;
        this.mSessionId = -1;
        this.mRequireUserAction = 0;
    }

    public InstallRequest(ParsedPackage parsedPackage, int i, int i2, UserHandle userHandle, ScanResult scanResult) {
        this.mAppId = -1;
        if (userHandle != null) {
            this.mUserId = userHandle.getIdentifier();
        } else {
            this.mUserId = 0;
        }
        this.mInstallArgs = null;
        this.mParsedPackage = parsedPackage;
        this.mParseFlags = i;
        this.mScanFlags = i2;
        this.mScanResult = scanResult;
        this.mPackageMetrics = null;
        this.mSessionId = -1;
        this.mRequireUserAction = 0;
    }

    public String getName() {
        return this.mName;
    }

    public String getReturnMsg() {
        return this.mReturnMsg;
    }

    public OriginInfo getOriginInfo() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mOriginInfo;
    }

    public PackageRemovedInfo getRemovedInfo() {
        return this.mRemovedInfo;
    }

    public String getOrigPackage() {
        return this.mOrigPackage;
    }

    public String getOrigPermission() {
        return this.mOrigPermission;
    }

    public File getCodeFile() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mCodeFile;
    }

    public String getCodePath() {
        File file;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (file = installArgs.mCodeFile) == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public String getAbiOverride() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mAbiOverride;
    }

    public int getReturnCode() {
        return this.mReturnCode;
    }

    public int getInternalErrorCode() {
        return this.mInternalErrorCode;
    }

    public IPackageInstallObserver2 getObserver() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mObserver;
    }

    public boolean isInstallMove() {
        InstallArgs installArgs = this.mInstallArgs;
        return (installArgs == null || installArgs.mMoveInfo == null) ? false : true;
    }

    public String getMoveToUuid() {
        MoveInfo moveInfo;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (moveInfo = installArgs.mMoveInfo) == null) {
            return null;
        }
        return moveInfo.mToUuid;
    }

    public String getMovePackageName() {
        MoveInfo moveInfo;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (moveInfo = installArgs.mMoveInfo) == null) {
            return null;
        }
        return moveInfo.mPackageName;
    }

    public String getMoveFromCodePath() {
        MoveInfo moveInfo;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (moveInfo = installArgs.mMoveInfo) == null) {
            return null;
        }
        return moveInfo.mFromCodePath;
    }

    public File getOldCodeFile() {
        InstallArgs installArgs;
        PackageRemovedInfo packageRemovedInfo = this.mRemovedInfo;
        if (packageRemovedInfo == null || (installArgs = packageRemovedInfo.mArgs) == null) {
            return null;
        }
        return installArgs.mCodeFile;
    }

    public String[] getOldInstructionSet() {
        InstallArgs installArgs;
        PackageRemovedInfo packageRemovedInfo = this.mRemovedInfo;
        if (packageRemovedInfo == null || (installArgs = packageRemovedInfo.mArgs) == null) {
            return null;
        }
        return installArgs.mInstructionSets;
    }

    public UserHandle getUser() {
        return new UserHandle(this.mUserId);
    }

    public int getUserId() {
        return this.mUserId;
    }

    public int getInstallFlags() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mInstallFlags;
    }

    public int getInstallReason() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mInstallReason;
    }

    public String getVolumeUuid() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mVolumeUuid;
    }

    public AndroidPackage getPkg() {
        return this.mPkg;
    }

    public String getTraceMethod() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mTraceMethod;
    }

    public int getTraceCookie() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mTraceCookie;
    }

    public boolean isUpdate() {
        PackageRemovedInfo packageRemovedInfo = this.mRemovedInfo;
        return (packageRemovedInfo == null || packageRemovedInfo.mRemovedPackage == null) ? false : true;
    }

    public boolean isInstallExistingForUser() {
        return this.mInstallArgs == null;
    }

    public InstallSource getInstallSource() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mInstallSource;
    }

    public String getInstallerPackageName() {
        InstallSource installSource;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (installSource = installArgs.mInstallSource) == null) {
            return null;
        }
        return installSource.mInstallerPackageName;
    }

    public int getInstallerPackageUid() {
        InstallSource installSource;
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null || (installSource = installArgs.mInstallSource) == null) {
            return -1;
        }
        return installSource.mInstallerPackageUid;
    }

    public int getDataLoaderType() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mDataLoaderType;
    }

    public int getSignatureSchemeVersion() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mSigningDetails.getSignatureSchemeVersion();
    }

    public SigningDetails getSigningDetails() {
        InstallArgs installArgs = this.mInstallArgs;
        return installArgs == null ? SigningDetails.UNKNOWN : installArgs.mSigningDetails;
    }

    public Uri getOriginUri() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return Uri.fromFile(installArgs.mOriginInfo.mResolvedFile);
    }

    public ApexInfo getApexInfo() {
        return this.mApexInfo;
    }

    public String getApexModuleName() {
        return this.mApexModuleName;
    }

    public String getSourceInstallerPackageName() {
        return this.mInstallArgs.mInstallSource.mInstallerPackageName;
    }

    public boolean isRollback() {
        InstallArgs installArgs = this.mInstallArgs;
        return installArgs != null && installArgs.mInstallReason == 5;
    }

    public int[] getNewUsers() {
        return this.mNewUsers;
    }

    public int[] getOriginUsers() {
        return this.mOrigUsers;
    }

    public int getAppId() {
        return this.mAppId;
    }

    public ArrayMap<String, Integer> getPermissionStates() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mPermissionStates;
    }

    public ArrayList<AndroidPackage> getLibraryConsumers() {
        return this.mLibraryConsumers;
    }

    public List<String> getAllowlistedRestrictedPermissions() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return null;
        }
        return installArgs.mAllowlistedRestrictedPermissions;
    }

    public int getAutoRevokePermissionsMode() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 3;
        }
        return installArgs.mAutoRevokePermissionsMode;
    }

    public int getPackageSource() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mPackageSource;
    }

    public int getInstallScenario() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return 0;
        }
        return installArgs.mInstallScenario;
    }

    public ParsedPackage getParsedPackage() {
        return this.mParsedPackage;
    }

    public int getParseFlags() {
        return this.mParseFlags;
    }

    public int getScanFlags() {
        return this.mScanFlags;
    }

    public String getExistingPackageName() {
        AndroidPackage androidPackage = this.mExistingPackage;
        if (androidPackage != null) {
            return androidPackage.getPackageName();
        }
        return null;
    }

    public AndroidPackage getScanRequestOldPackage() {
        assertScanResultExists();
        return this.mScanResult.mRequest.mOldPkg;
    }

    public boolean isClearCodeCache() {
        return this.mClearCodeCache;
    }

    public boolean isInstallReplace() {
        return this.mReplace;
    }

    public boolean isInstallSystem() {
        return this.mSystem;
    }

    public boolean isInstallInherit() {
        return this.mIsInstallInherit;
    }

    public boolean isInstallForUsers() {
        return this.mIsInstallForUsers;
    }

    public boolean isInstallFromAdb() {
        InstallArgs installArgs = this.mInstallArgs;
        return (installArgs == null || (installArgs.mInstallFlags & 32) == 0) ? false : true;
    }

    public PackageSetting getOriginalPackageSetting() {
        return this.mOriginalPs;
    }

    public PackageSetting getDisabledPackageSetting() {
        return this.mDisabledPs;
    }

    public PackageSetting getScanRequestOldPackageSetting() {
        assertScanResultExists();
        return this.mScanResult.mRequest.mOldPkgSetting;
    }

    public PackageSetting getScanRequestOriginalPackageSetting() {
        assertScanResultExists();
        return this.mScanResult.mRequest.mOriginalPkgSetting;
    }

    public PackageSetting getScanRequestPackageSetting() {
        assertScanResultExists();
        return this.mScanResult.mRequest.mPkgSetting;
    }

    public String getRealPackageName() {
        assertScanResultExists();
        return this.mScanResult.mRequest.mRealPkgName;
    }

    public List<String> getChangedAbiCodePath() {
        assertScanResultExists();
        return this.mScanResult.mChangedAbiCodePath;
    }

    public boolean isApplicationEnabledSettingPersistent() {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs == null) {
            return false;
        }
        return installArgs.mApplicationEnabledSettingPersistent;
    }

    public boolean isForceQueryableOverride() {
        InstallArgs installArgs = this.mInstallArgs;
        return installArgs != null && installArgs.mForceQueryableOverride;
    }

    public SharedLibraryInfo getSdkSharedLibraryInfo() {
        assertScanResultExists();
        return this.mScanResult.mSdkSharedLibraryInfo;
    }

    public SharedLibraryInfo getStaticSharedLibraryInfo() {
        assertScanResultExists();
        return this.mScanResult.mStaticSharedLibraryInfo;
    }

    public List<SharedLibraryInfo> getDynamicSharedLibraryInfos() {
        assertScanResultExists();
        return this.mScanResult.mDynamicSharedLibraryInfos;
    }

    public PackageSetting getScannedPackageSetting() {
        assertScanResultExists();
        return this.mScanResult.mPkgSetting;
    }

    public PackageSetting getRealPackageSetting() {
        PackageSetting scanRequestPackageSetting = isExistingSettingCopied() ? getScanRequestPackageSetting() : getScannedPackageSetting();
        return scanRequestPackageSetting == null ? getScannedPackageSetting() : scanRequestPackageSetting;
    }

    public boolean isExistingSettingCopied() {
        assertScanResultExists();
        return this.mScanResult.mExistingSettingCopied;
    }

    public boolean needsNewAppId() {
        assertScanResultExists();
        return this.mScanResult.mPreviousAppId != -1;
    }

    public int getPreviousAppId() {
        assertScanResultExists();
        return this.mScanResult.mPreviousAppId;
    }

    public boolean isInstantInstall() {
        return (this.mScanFlags & IInstalld.FLAG_FORCE) != 0;
    }

    public void assertScanResultExists() {
        if (this.mScanResult == null) {
            if (Build.IS_USERDEBUG || Build.IS_ENG) {
                throw new IllegalStateException("ScanResult cannot be null.");
            }
            Slog.e("PackageManager", "ScanResult is null and it should not happen");
        }
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getRequireUserAction() {
        return this.mRequireUserAction;
    }

    public void closeFreezer() {
        PackageFreezer packageFreezer = this.mFreezer;
        if (packageFreezer != null) {
            packageFreezer.close();
        }
    }

    public void runPostInstallRunnable() {
        Runnable runnable = this.mPostInstallRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setCodeFile(File file) {
        InstallArgs installArgs = this.mInstallArgs;
        if (installArgs != null) {
            installArgs.mCodeFile = file;
        }
    }

    public void setError(int i, String str) {
        setReturnCode(i);
        setReturnMessage(str);
        Slog.w("PackageManager", str);
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onInstallFailed();
        }
    }

    public void setError(PackageManagerException packageManagerException) {
        setError((String) null, packageManagerException);
    }

    public void setError(String str, PackageManagerException packageManagerException) {
        this.mInternalErrorCode = packageManagerException.internalErrorCode;
        this.mReturnCode = packageManagerException.error;
        setReturnMessage(ExceptionUtils.getCompleteMessage(str, packageManagerException));
        Slog.w("PackageManager", str, packageManagerException);
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onInstallFailed();
        }
    }

    public void setReturnCode(int i) {
        this.mReturnCode = i;
    }

    public void setReturnMessage(String str) {
        this.mReturnMsg = str;
    }

    public void setApexInfo(ApexInfo apexInfo) {
        this.mApexInfo = apexInfo;
    }

    public void setApexModuleName(String str) {
        this.mApexModuleName = str;
    }

    public void setPkg(AndroidPackage androidPackage) {
        this.mPkg = androidPackage;
    }

    public void setAppId(int i) {
        this.mAppId = i;
    }

    public void setNewUsers(int[] iArr) {
        this.mNewUsers = iArr;
    }

    public void setOriginPackage(String str) {
        this.mOrigPackage = str;
    }

    public void setOriginPermission(String str) {
        this.mOrigPermission = str;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setOriginUsers(int[] iArr) {
        this.mOrigUsers = iArr;
    }

    public void setFreezer(PackageFreezer packageFreezer) {
        this.mFreezer = packageFreezer;
    }

    public void setRemovedInfo(PackageRemovedInfo packageRemovedInfo) {
        this.mRemovedInfo = packageRemovedInfo;
    }

    public void setLibraryConsumers(ArrayList<AndroidPackage> arrayList) {
        this.mLibraryConsumers = arrayList;
    }

    public void setPrepareResult(boolean z, int i, int i2, AndroidPackage androidPackage, ParsedPackage parsedPackage, boolean z2, boolean z3, PackageSetting packageSetting, PackageSetting packageSetting2) {
        this.mReplace = z;
        this.mScanFlags = i;
        this.mParseFlags = i2;
        this.mExistingPackage = androidPackage;
        this.mParsedPackage = parsedPackage;
        this.mClearCodeCache = z2;
        this.mSystem = z3;
        this.mOriginalPs = packageSetting;
        this.mDisabledPs = packageSetting2;
    }

    public void setScanResult(ScanResult scanResult) {
        this.mScanResult = scanResult;
    }

    public void setScannedPackageSettingAppId(int i) {
        assertScanResultExists();
        this.mScanResult.mPkgSetting.setAppId(i);
    }

    public void setScannedPackageSettingFirstInstallTimeFromReplaced(PackageStateInternal packageStateInternal, int[] iArr) {
        assertScanResultExists();
        this.mScanResult.mPkgSetting.setFirstInstallTimeFromReplaced(packageStateInternal, iArr);
    }

    public void setScannedPackageSettingLastUpdateTime(long j) {
        assertScanResultExists();
        this.mScanResult.mPkgSetting.setLastUpdateTime(j);
    }

    public void setRemovedAppId(int i) {
        PackageRemovedInfo packageRemovedInfo = this.mRemovedInfo;
        if (packageRemovedInfo != null) {
            packageRemovedInfo.mRemovedAppId = i;
        }
    }

    public void onPrepareStarted() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepStarted(1);
        }
    }

    public void onPrepareFinished() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepFinished(1);
        }
    }

    public void onScanStarted() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepStarted(2);
        }
    }

    public void onScanFinished() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepFinished(2);
        }
    }

    public void onReconcileStarted() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepStarted(3);
        }
    }

    public void onReconcileFinished() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepFinished(3);
        }
    }

    public void onCommitStarted() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepStarted(4);
        }
    }

    public void onCommitFinished() {
        PackageMetrics packageMetrics = this.mPackageMetrics;
        if (packageMetrics != null) {
            packageMetrics.onStepFinished(4);
        }
    }

    public void onDexoptFinished(DexoptResult dexoptResult) {
        if (this.mPackageMetrics == null) {
            return;
        }
        int finalStatus = dexoptResult.getFinalStatus();
        this.mDexoptStatus = finalStatus;
        if (finalStatus != 20) {
            return;
        }
        long j = 0;
        for (DexoptResult.PackageDexoptResult packageDexoptResult : dexoptResult.getPackageDexoptResults()) {
            for (DexoptResult.DexContainerFileDexoptResult dexContainerFileDexoptResult : packageDexoptResult.getDexContainerFileDexoptResults()) {
                j += dexContainerFileDexoptResult.getDex2oatWallTimeMillis();
            }
        }
        this.mPackageMetrics.onStepFinished(5, j);
    }

    public void onInstallCompleted() {
        PackageMetrics packageMetrics;
        if (getReturnCode() != 1 || (packageMetrics = this.mPackageMetrics) == null) {
            return;
        }
        packageMetrics.onInstallSucceed();
    }
}
