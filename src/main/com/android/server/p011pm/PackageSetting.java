package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.pm.SigningDetails;
import android.content.pm.SigningInfo;
import android.content.pm.UserInfo;
import android.content.pm.overlay.OverlayPaths;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.permission.LegacyPermissionDataProvider;
import com.android.server.p011pm.permission.LegacyPermissionState;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageStateUnserialized;
import com.android.server.p011pm.pkg.PackageUserState;
import com.android.server.p011pm.pkg.PackageUserStateImpl;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import com.android.server.p011pm.pkg.SharedLibrary;
import com.android.server.p011pm.pkg.SuspendParams;
import com.android.server.utils.SnapshotCache;
import com.android.server.utils.WatchedArraySet;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import libcore.util.EmptyArray;
/* renamed from: com.android.server.pm.PackageSetting */
/* loaded from: classes2.dex */
public class PackageSetting extends SettingBase implements PackageStateInternal {
    public int categoryOverride;
    public boolean forceQueryableOverride;
    public boolean installPermissionsFixed;
    public InstallSource installSource;
    public PackageKeySetData keySetData;
    public long lastUpdateTime;
    @Deprecated
    public String legacyNativeLibraryPath;
    public int mAppId;
    public String mCpuAbiOverride;
    public UUID mDomainSetId;
    public long mLastModifiedTime;
    public float mLoadingProgress;
    public String mName;
    @Deprecated
    public Set<String> mOldCodePaths;
    public File mPath;
    public String mPathString;
    public String mPrimaryCpuAbi;
    public String mRealName;
    public String mSecondaryCpuAbi;
    public int mSharedUserAppId;
    public final SnapshotCache<PackageSetting> mSnapshot;
    public final SparseArray<PackageUserStateImpl> mUserStates;
    public Map<String, Set<String>> mimeGroups;
    public AndroidPackageInternal pkg;
    public final PackageStateUnserialized pkgState;
    public PackageSignatures signatures;
    public boolean updateAvailable;
    public String[] usesSdkLibraries;
    public long[] usesSdkLibrariesVersionsMajor;
    public String[] usesStaticLibraries;
    public long[] usesStaticLibrariesVersions;
    public long versionCode;
    public String volumeUuid;

    public final SnapshotCache<PackageSetting> makeCache() {
        return new SnapshotCache<PackageSetting>(this, this) { // from class: com.android.server.pm.PackageSetting.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.utils.SnapshotCache
            public PackageSetting createSnapshot() {
                return new PackageSetting((PackageSetting) this.mSource, true);
            }
        };
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public PackageSetting(String str, String str2, File file, String str3, String str4, String str5, String str6, long j, int i, int i2, int i3, String[] strArr, long[] jArr, String[] strArr2, long[] jArr2, Map<String, Set<String>> map, UUID uuid) {
        super(i, i2);
        this.keySetData = new PackageKeySetData();
        this.mUserStates = new SparseArray<>();
        this.categoryOverride = -1;
        this.pkgState = new PackageStateUnserialized(this);
        this.mName = str;
        this.mRealName = str2;
        this.usesSdkLibraries = strArr;
        this.usesSdkLibrariesVersionsMajor = jArr;
        this.usesStaticLibraries = strArr2;
        this.usesStaticLibrariesVersions = jArr2;
        this.mPath = file;
        this.mPathString = file.toString();
        this.legacyNativeLibraryPath = str3;
        this.mPrimaryCpuAbi = str4;
        this.mSecondaryCpuAbi = str5;
        this.mCpuAbiOverride = str6;
        this.versionCode = j;
        this.signatures = new PackageSignatures();
        this.installSource = InstallSource.EMPTY;
        this.mSharedUserAppId = i3;
        this.mDomainSetId = uuid;
        copyMimeGroups(map);
        this.mSnapshot = makeCache();
    }

    public PackageSetting(PackageSetting packageSetting) {
        this(packageSetting, false);
    }

    public PackageSetting(PackageSetting packageSetting, String str) {
        this(packageSetting, false);
        this.mRealName = str;
    }

    public PackageSetting(PackageSetting packageSetting, boolean z) {
        super(packageSetting);
        this.keySetData = new PackageKeySetData();
        this.mUserStates = new SparseArray<>();
        this.categoryOverride = -1;
        this.pkgState = new PackageStateUnserialized(this);
        copyPackageSetting(packageSetting, z);
        if (z) {
            this.mSnapshot = new SnapshotCache.Sealed();
        } else {
            this.mSnapshot = makeCache();
        }
    }

    @Override // com.android.server.utils.Snappable
    public PackageSetting snapshot() {
        return this.mSnapshot.snapshot();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, List<UserInfo> list, LegacyPermissionDataProvider legacyPermissionDataProvider) {
        long start = protoOutputStream.start(j);
        String str = this.mRealName;
        if (str == null) {
            str = this.mName;
        }
        protoOutputStream.write(1138166333441L, str);
        protoOutputStream.write(1120986464258L, this.mAppId);
        protoOutputStream.write(1120986464259L, this.versionCode);
        protoOutputStream.write(1112396529670L, this.lastUpdateTime);
        protoOutputStream.write(1138166333447L, this.installSource.mInstallerPackageName);
        AndroidPackageInternal androidPackageInternal = this.pkg;
        if (androidPackageInternal != null) {
            protoOutputStream.write(1138166333444L, androidPackageInternal.getVersionName());
            long start2 = protoOutputStream.start(2246267895816L);
            protoOutputStream.write(1138166333441L, "base");
            protoOutputStream.write(1120986464258L, this.pkg.getBaseRevisionCode());
            protoOutputStream.end(start2);
            for (int i = 0; i < this.pkg.getSplitNames().length; i++) {
                long start3 = protoOutputStream.start(2246267895816L);
                protoOutputStream.write(1138166333441L, this.pkg.getSplitNames()[i]);
                protoOutputStream.write(1120986464258L, this.pkg.getSplitRevisionCodes()[i]);
                protoOutputStream.end(start3);
            }
            long start4 = protoOutputStream.start(1146756268042L);
            protoOutputStream.write(1138166333441L, this.installSource.mInitiatingPackageName);
            protoOutputStream.write(1138166333442L, this.installSource.mOriginatingPackageName);
            protoOutputStream.write(1138166333443L, this.installSource.mUpdateOwnerPackageName);
            protoOutputStream.end(start4);
        }
        protoOutputStream.write(1133871366146L, isLoading());
        writeUsersInfoToProto(protoOutputStream, 2246267895817L);
        writePackageUserPermissionsProto(protoOutputStream, 2246267895820L, list, legacyPermissionDataProvider);
        protoOutputStream.end(start);
    }

    public PackageSetting setAppId(int i) {
        this.mAppId = i;
        onChanged();
        return this;
    }

    public PackageSetting setCpuAbiOverride(String str) {
        this.mCpuAbiOverride = str;
        onChanged();
        return this;
    }

    public PackageSetting setFirstInstallTimeFromReplaced(PackageStateInternal packageStateInternal, int[] iArr) {
        for (int i = 0; i < iArr.length; i++) {
            long firstInstallTimeMillis = packageStateInternal.getUserStateOrDefault(i).getFirstInstallTimeMillis();
            if (firstInstallTimeMillis != 0) {
                modifyUserState(i).setFirstInstallTime(firstInstallTimeMillis);
            }
        }
        onChanged();
        return this;
    }

    public PackageSetting setFirstInstallTime(long j, int i) {
        if (i == -1) {
            int size = this.mUserStates.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mUserStates.valueAt(i2).setFirstInstallTime(j);
            }
        } else {
            modifyUserState(i).setFirstInstallTime(j);
        }
        onChanged();
        return this;
    }

    public PackageSetting setForceQueryableOverride(boolean z) {
        this.forceQueryableOverride = z;
        onChanged();
        return this;
    }

    public PackageSetting setInstallerPackage(String str, int i) {
        this.installSource = this.installSource.setInstallerPackage(str, i);
        onChanged();
        return this;
    }

    public PackageSetting setUpdateOwnerPackage(String str) {
        this.installSource = this.installSource.setUpdateOwnerPackageName(str);
        onChanged();
        return this;
    }

    public PackageSetting setInstallSource(InstallSource installSource) {
        Objects.requireNonNull(installSource);
        this.installSource = installSource;
        onChanged();
        return this;
    }

    public PackageSetting removeInstallerPackage(String str) {
        this.installSource = this.installSource.removeInstallerPackage(str);
        onChanged();
        return this;
    }

    public PackageSetting setIsOrphaned(boolean z) {
        this.installSource = this.installSource.setIsOrphaned(z);
        onChanged();
        return this;
    }

    public PackageSetting setLastModifiedTime(long j) {
        this.mLastModifiedTime = j;
        onChanged();
        return this;
    }

    public PackageSetting setLastUpdateTime(long j) {
        this.lastUpdateTime = j;
        onChanged();
        return this;
    }

    public PackageSetting setLongVersionCode(long j) {
        this.versionCode = j;
        onChanged();
        return this;
    }

    public boolean setMimeGroup(String str, ArraySet<String> arraySet) {
        Map<String, Set<String>> map = this.mimeGroups;
        Set<String> set = map == null ? null : map.get(str);
        if (set == null) {
            throw new IllegalArgumentException("Unknown MIME group " + str + " for package " + this.mName);
        }
        boolean z = !arraySet.equals(set);
        this.mimeGroups.put(str, arraySet);
        if (z) {
            onChanged();
        }
        return z;
    }

    public PackageSetting setPkg(AndroidPackage androidPackage) {
        this.pkg = (AndroidPackageInternal) androidPackage;
        onChanged();
        return this;
    }

    public PackageSetting setPkgStateLibraryFiles(Collection<String> collection) {
        List<String> usesLibraryFiles = getUsesLibraryFiles();
        if (usesLibraryFiles.size() != collection.size() || !usesLibraryFiles.containsAll(collection)) {
            this.pkgState.setUsesLibraryFiles(new ArrayList(collection));
            onChanged();
        }
        return this;
    }

    public PackageSetting setPrimaryCpuAbi(String str) {
        this.mPrimaryCpuAbi = str;
        onChanged();
        return this;
    }

    public PackageSetting setSecondaryCpuAbi(String str) {
        this.mSecondaryCpuAbi = str;
        onChanged();
        return this;
    }

    public PackageSetting setSignatures(PackageSignatures packageSignatures) {
        this.signatures = packageSignatures;
        onChanged();
        return this;
    }

    public PackageSetting setVolumeUuid(String str) {
        this.volumeUuid = str;
        onChanged();
        return this;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isExternalStorage() {
        return (getFlags() & 262144) != 0;
    }

    public PackageSetting setUpdateAvailable(boolean z) {
        this.updateAvailable = z;
        onChanged();
        return this;
    }

    public void setSharedUserAppId(int i) {
        this.mSharedUserAppId = i;
        onChanged();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public int getSharedUserAppId() {
        return this.mSharedUserAppId;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean hasSharedUser() {
        return this.mSharedUserAppId > 0;
    }

    public String toString() {
        return "PackageSetting{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mName + "/" + this.mAppId + "}";
    }

    public void copyMimeGroups(Map<String, Set<String>> map) {
        if (map == null) {
            this.mimeGroups = null;
            return;
        }
        this.mimeGroups = new ArrayMap(map.size());
        for (String str : map.keySet()) {
            Set<String> set = map.get(str);
            if (set != null) {
                this.mimeGroups.put(str, new ArraySet(set));
            } else {
                this.mimeGroups.put(str, new ArraySet());
            }
        }
    }

    public void updateFrom(PackageSetting packageSetting) {
        copyPackageSetting(packageSetting, false);
        Map<String, Set<String>> map = packageSetting.mimeGroups;
        updateMimeGroups(map != null ? map.keySet() : null);
        onChanged();
    }

    public PackageSetting updateMimeGroups(Set<String> set) {
        if (set == null) {
            this.mimeGroups = null;
            return this;
        }
        if (this.mimeGroups == null) {
            this.mimeGroups = Collections.emptyMap();
        }
        ArrayMap arrayMap = new ArrayMap(set.size());
        for (String str : set) {
            if (this.mimeGroups.containsKey(str)) {
                arrayMap.put(str, this.mimeGroups.get(str));
            } else {
                arrayMap.put(str, new ArraySet());
            }
        }
        onChanged();
        this.mimeGroups = arrayMap;
        return this;
    }

    @Override // com.android.server.p011pm.SettingBase, com.android.server.p011pm.pkg.PackageStateInternal
    @Deprecated
    public LegacyPermissionState getLegacyPermissionState() {
        return super.getLegacyPermissionState();
    }

    public PackageSetting setInstallPermissionsFixed(boolean z) {
        this.installPermissionsFixed = z;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isPrivileged() {
        return (getPrivateFlags() & 8) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isOem() {
        return (getPrivateFlags() & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isVendor() {
        return (getPrivateFlags() & 262144) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isProduct() {
        return (getPrivateFlags() & 524288) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isRequiredForSystemUser() {
        return (getPrivateFlags() & 512) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isSystemExt() {
        return (getPrivateFlags() & 2097152) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isOdm() {
        return (getPrivateFlags() & 1073741824) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isSystem() {
        return (getFlags() & 1) != 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public SigningDetails getSigningDetails() {
        return this.signatures.mSigningDetails;
    }

    public PackageSetting setSigningDetails(SigningDetails signingDetails) {
        this.signatures.mSigningDetails = signingDetails;
        onChanged();
        return this;
    }

    public void copyPackageSetting(PackageSetting packageSetting, boolean z) {
        super.copySettingBase(packageSetting);
        this.mSharedUserAppId = packageSetting.mSharedUserAppId;
        this.mLoadingProgress = packageSetting.mLoadingProgress;
        this.legacyNativeLibraryPath = packageSetting.legacyNativeLibraryPath;
        this.mName = packageSetting.mName;
        this.mRealName = packageSetting.mRealName;
        this.mAppId = packageSetting.mAppId;
        this.pkg = packageSetting.pkg;
        this.mPath = packageSetting.mPath;
        this.mPathString = packageSetting.mPathString;
        this.mPrimaryCpuAbi = packageSetting.mPrimaryCpuAbi;
        this.mSecondaryCpuAbi = packageSetting.mSecondaryCpuAbi;
        this.mCpuAbiOverride = packageSetting.mCpuAbiOverride;
        this.mLastModifiedTime = packageSetting.mLastModifiedTime;
        this.lastUpdateTime = packageSetting.lastUpdateTime;
        this.versionCode = packageSetting.versionCode;
        this.signatures = packageSetting.signatures;
        this.installPermissionsFixed = packageSetting.installPermissionsFixed;
        this.keySetData = new PackageKeySetData(packageSetting.keySetData);
        this.installSource = packageSetting.installSource;
        this.volumeUuid = packageSetting.volumeUuid;
        this.categoryOverride = packageSetting.categoryOverride;
        this.updateAvailable = packageSetting.updateAvailable;
        this.forceQueryableOverride = packageSetting.forceQueryableOverride;
        this.mDomainSetId = packageSetting.mDomainSetId;
        String[] strArr = packageSetting.usesSdkLibraries;
        this.usesSdkLibraries = strArr != null ? (String[]) Arrays.copyOf(strArr, strArr.length) : null;
        long[] jArr = packageSetting.usesSdkLibrariesVersionsMajor;
        this.usesSdkLibrariesVersionsMajor = jArr != null ? Arrays.copyOf(jArr, jArr.length) : null;
        String[] strArr2 = packageSetting.usesStaticLibraries;
        this.usesStaticLibraries = strArr2 != null ? (String[]) Arrays.copyOf(strArr2, strArr2.length) : null;
        long[] jArr2 = packageSetting.usesStaticLibrariesVersions;
        this.usesStaticLibrariesVersions = jArr2 != null ? Arrays.copyOf(jArr2, jArr2.length) : null;
        this.mUserStates.clear();
        for (int i = 0; i < packageSetting.mUserStates.size(); i++) {
            if (z) {
                this.mUserStates.put(packageSetting.mUserStates.keyAt(i), packageSetting.mUserStates.valueAt(i).snapshot());
            } else {
                PackageUserStateImpl valueAt = packageSetting.mUserStates.valueAt(i);
                valueAt.setWatchable(this);
                this.mUserStates.put(packageSetting.mUserStates.keyAt(i), valueAt);
            }
        }
        Set<String> set = this.mOldCodePaths;
        if (set != null) {
            if (packageSetting.mOldCodePaths != null) {
                set.clear();
                this.mOldCodePaths.addAll(packageSetting.mOldCodePaths);
            } else {
                this.mOldCodePaths = null;
            }
        }
        copyMimeGroups(packageSetting.mimeGroups);
        this.pkgState.updateFrom(packageSetting.pkgState);
        onChanged();
    }

    @VisibleForTesting
    public PackageUserStateImpl modifyUserState(int i) {
        PackageUserStateImpl packageUserStateImpl = this.mUserStates.get(i);
        if (packageUserStateImpl == null) {
            PackageUserStateImpl packageUserStateImpl2 = new PackageUserStateImpl(this);
            this.mUserStates.put(i, packageUserStateImpl2);
            onChanged();
            return packageUserStateImpl2;
        }
        return packageUserStateImpl;
    }

    public PackageUserStateImpl getOrCreateUserState(int i) {
        PackageUserStateImpl packageUserStateImpl = this.mUserStates.get(i);
        if (packageUserStateImpl == null) {
            PackageUserStateImpl packageUserStateImpl2 = new PackageUserStateImpl(this);
            this.mUserStates.put(i, packageUserStateImpl2);
            return packageUserStateImpl2;
        }
        return packageUserStateImpl;
    }

    public PackageUserStateInternal readUserState(int i) {
        PackageUserStateImpl packageUserStateImpl = this.mUserStates.get(i);
        return packageUserStateImpl == null ? PackageUserStateInternal.DEFAULT : packageUserStateImpl;
    }

    public void setEnabled(int i, int i2, String str) {
        modifyUserState(i2).setEnabledState(i).setLastDisableAppCaller(str);
        onChanged();
    }

    public int getEnabled(int i) {
        return readUserState(i).getEnabledState();
    }

    public void setInstalled(boolean z, int i) {
        modifyUserState(i).setInstalled(z);
        onChanged();
    }

    public boolean getInstalled(int i) {
        return readUserState(i).isInstalled();
    }

    public int getInstallReason(int i) {
        return readUserState(i).getInstallReason();
    }

    public void setInstallReason(int i, int i2) {
        modifyUserState(i2).setInstallReason(i);
        onChanged();
    }

    public int getUninstallReason(int i) {
        return readUserState(i).getUninstallReason();
    }

    public void setUninstallReason(int i, int i2) {
        modifyUserState(i2).setUninstallReason(i);
        onChanged();
    }

    public OverlayPaths getOverlayPaths(int i) {
        return readUserState(i).getOverlayPaths();
    }

    public boolean setOverlayPathsForLibrary(String str, OverlayPaths overlayPaths, int i) {
        boolean sharedLibraryOverlayPaths = modifyUserState(i).setSharedLibraryOverlayPaths(str, overlayPaths);
        onChanged();
        return sharedLibraryOverlayPaths;
    }

    public boolean isAnyInstalled(int[] iArr) {
        for (int i : iArr) {
            if (readUserState(i).isInstalled()) {
                return true;
            }
        }
        return false;
    }

    public int[] queryInstalledUsers(int[] iArr, boolean z) {
        int i = 0;
        for (int i2 : iArr) {
            if (getInstalled(i2) == z) {
                i++;
            }
        }
        int[] iArr2 = new int[i];
        int i3 = 0;
        for (int i4 : iArr) {
            if (getInstalled(i4) == z) {
                iArr2[i3] = i4;
                i3++;
            }
        }
        return iArr2;
    }

    public long getCeDataInode(int i) {
        return readUserState(i).getCeDataInode();
    }

    public void setCeDataInode(long j, int i) {
        modifyUserState(i).setCeDataInode(j);
        onChanged();
    }

    public void setStopped(boolean z, int i) {
        modifyUserState(i).setStopped(z);
        onChanged();
    }

    public void setNotLaunched(boolean z, int i) {
        modifyUserState(i).setNotLaunched(z);
        onChanged();
    }

    public void setHidden(boolean z, int i) {
        modifyUserState(i).setHidden(z);
        onChanged();
    }

    public boolean getInstantApp(int i) {
        return readUserState(i).isInstantApp();
    }

    public void setInstantApp(boolean z, int i) {
        modifyUserState(i).setInstantApp(z);
        onChanged();
    }

    public boolean getVirtualPreload(int i) {
        return readUserState(i).isVirtualPreload();
    }

    public void setUserState(int i, long j, int i2, boolean z, boolean z2, boolean z3, boolean z4, int i3, ArrayMap<String, SuspendParams> arrayMap, boolean z5, boolean z6, String str, ArraySet<String> arraySet, ArraySet<String> arraySet2, int i4, int i5, String str2, String str3, long j2) {
        modifyUserState(i).setSuspendParams(arrayMap).setCeDataInode(j).setEnabledState(i2).setInstalled(z).setStopped(z2).setNotLaunched(z3).setHidden(z4).setDistractionFlags(i3).setLastDisableAppCaller(str).setEnabledComponents(arraySet).setDisabledComponents(arraySet2).setInstallReason(i4).setUninstallReason(i5).setInstantApp(z5).setVirtualPreload(z6).setHarmfulAppWarning(str2).setSplashScreenTheme(str3).setFirstInstallTime(j2);
        onChanged();
    }

    public WatchedArraySet<String> getEnabledComponents(int i) {
        return readUserState(i).getEnabledComponentsNoCopy();
    }

    public WatchedArraySet<String> getDisabledComponents(int i) {
        return readUserState(i).getDisabledComponentsNoCopy();
    }

    public void setEnabledComponentsCopy(WatchedArraySet<String> watchedArraySet, int i) {
        modifyUserState(i).setEnabledComponents(watchedArraySet != null ? watchedArraySet.untrackedStorage() : null);
        onChanged();
    }

    public void setDisabledComponentsCopy(WatchedArraySet<String> watchedArraySet, int i) {
        modifyUserState(i).setDisabledComponents(watchedArraySet != null ? watchedArraySet.untrackedStorage() : null);
        onChanged();
    }

    public PackageUserStateImpl modifyUserStateComponents(int i, boolean z, boolean z2) {
        boolean z3;
        PackageUserStateImpl modifyUserState = modifyUserState(i);
        boolean z4 = true;
        if (z && modifyUserState.getDisabledComponentsNoCopy() == null) {
            modifyUserState.setDisabledComponents(new ArraySet<>(1));
            z3 = true;
        } else {
            z3 = false;
        }
        if (z2 && modifyUserState.getEnabledComponentsNoCopy() == null) {
            modifyUserState.setEnabledComponents(new ArraySet<>(1));
        } else {
            z4 = z3;
        }
        if (z4) {
            onChanged();
        }
        return modifyUserState;
    }

    public void addDisabledComponent(String str, int i) {
        modifyUserStateComponents(i, true, false).getDisabledComponentsNoCopy().add(str);
        onChanged();
    }

    public void addEnabledComponent(String str, int i) {
        modifyUserStateComponents(i, false, true).getEnabledComponentsNoCopy().add(str);
        onChanged();
    }

    public boolean enableComponentLPw(String str, int i) {
        PackageUserStateImpl modifyUserStateComponents = modifyUserStateComponents(i, false, true);
        boolean add = modifyUserStateComponents.getEnabledComponentsNoCopy().add(str) | (modifyUserStateComponents.getDisabledComponentsNoCopy() != null ? modifyUserStateComponents.getDisabledComponentsNoCopy().remove(str) : false);
        if (add) {
            onChanged();
        }
        return add;
    }

    public boolean disableComponentLPw(String str, int i) {
        PackageUserStateImpl modifyUserStateComponents = modifyUserStateComponents(i, true, false);
        boolean add = modifyUserStateComponents.getDisabledComponentsNoCopy().add(str) | (modifyUserStateComponents.getEnabledComponentsNoCopy() != null ? modifyUserStateComponents.getEnabledComponentsNoCopy().remove(str) : false);
        if (add) {
            onChanged();
        }
        return add;
    }

    public boolean restoreComponentLPw(String str, int i) {
        PackageUserStateImpl modifyUserStateComponents = modifyUserStateComponents(i, true, true);
        boolean remove = (modifyUserStateComponents.getDisabledComponentsNoCopy() != null ? modifyUserStateComponents.getDisabledComponentsNoCopy().remove(str) : false) | (modifyUserStateComponents.getEnabledComponentsNoCopy() != null ? modifyUserStateComponents.getEnabledComponentsNoCopy().remove(str) : false);
        if (remove) {
            onChanged();
        }
        return remove;
    }

    public int getCurrentEnabledStateLPr(String str, int i) {
        PackageUserStateInternal readUserState = readUserState(i);
        if (readUserState.getEnabledComponentsNoCopy() == null || !readUserState.getEnabledComponentsNoCopy().contains(str)) {
            return (readUserState.getDisabledComponentsNoCopy() == null || !readUserState.getDisabledComponentsNoCopy().contains(str)) ? 0 : 2;
        }
        return 1;
    }

    public void removeUser(int i) {
        this.mUserStates.delete(i);
        onChanged();
    }

    public int[] getNotInstalledUserIds() {
        int size = this.mUserStates.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (!this.mUserStates.valueAt(i2).isInstalled()) {
                i++;
            }
        }
        if (i == 0) {
            return EmptyArray.INT;
        }
        int[] iArr = new int[i];
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            if (!this.mUserStates.valueAt(i4).isInstalled()) {
                iArr[i3] = this.mUserStates.keyAt(i4);
                i3++;
            }
        }
        return iArr;
    }

    public void writePackageUserPermissionsProto(ProtoOutputStream protoOutputStream, long j, List<UserInfo> list, LegacyPermissionDataProvider legacyPermissionDataProvider) {
        for (UserInfo userInfo : list) {
            long start = protoOutputStream.start(2246267895820L);
            protoOutputStream.write(1120986464257L, userInfo.id);
            for (LegacyPermissionState.PermissionState permissionState : legacyPermissionDataProvider.getLegacyPermissionState(this.mAppId).getPermissionStates(userInfo.id)) {
                if (permissionState.isGranted()) {
                    protoOutputStream.write(2237677961218L, permissionState.getName());
                }
            }
            protoOutputStream.end(start);
        }
    }

    public void writeUsersInfoToProto(ProtoOutputStream protoOutputStream, long j) {
        int i;
        int size = this.mUserStates.size();
        for (int i2 = 0; i2 < size; i2++) {
            long start = protoOutputStream.start(j);
            int keyAt = this.mUserStates.keyAt(i2);
            PackageUserStateImpl valueAt = this.mUserStates.valueAt(i2);
            protoOutputStream.write(1120986464257L, keyAt);
            if (valueAt.isInstantApp()) {
                i = 2;
            } else {
                i = valueAt.isInstalled() ? 1 : 0;
            }
            protoOutputStream.write(1159641169922L, i);
            protoOutputStream.write(1133871366147L, valueAt.isHidden());
            protoOutputStream.write(1120986464266L, valueAt.getDistractionFlags());
            protoOutputStream.write(1133871366148L, valueAt.isSuspended());
            if (valueAt.isSuspended()) {
                for (int i3 = 0; i3 < valueAt.getSuspendParams().size(); i3++) {
                    protoOutputStream.write(2237677961225L, valueAt.getSuspendParams().keyAt(i3));
                }
            }
            protoOutputStream.write(1133871366149L, valueAt.isStopped());
            protoOutputStream.write(1133871366150L, !valueAt.isNotLaunched());
            protoOutputStream.write(1159641169927L, valueAt.getEnabledState());
            protoOutputStream.write(1138166333448L, valueAt.getLastDisableAppCaller());
            protoOutputStream.write(1120986464267L, valueAt.getFirstInstallTimeMillis());
            protoOutputStream.end(start);
        }
    }

    public PackageSetting setPath(File file) {
        this.mPath = file;
        this.mPathString = file.toString();
        onChanged();
        return this;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public boolean overrideNonLocalizedLabelAndIcon(ComponentName componentName, String str, Integer num, int i) {
        boolean overrideLabelAndIcon = modifyUserState(i).overrideLabelAndIcon(componentName, str, num);
        onChanged();
        return overrideLabelAndIcon;
    }

    public void resetOverrideComponentLabelIcon(int i) {
        modifyUserState(i).resetOverrideComponentLabelIcon();
        onChanged();
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public boolean isLoading() {
        return Math.abs(1.0f - this.mLoadingProgress) >= 1.0E-8f;
    }

    public PackageSetting setLoadingProgress(float f) {
        this.mLoadingProgress = f;
        onChanged();
        return this;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long getVersionCode() {
        return this.versionCode;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public Map<String, Set<String>> getMimeGroups() {
        return CollectionUtils.isEmpty(this.mimeGroups) ? Collections.emptyMap() : Collections.unmodifiableMap(this.mimeGroups);
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getPackageName() {
        return this.mName;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public AndroidPackage getAndroidPackage() {
        return getPkg();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public SigningInfo getSigningInfo() {
        return new SigningInfo(this.signatures.mSigningDetails);
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String[] getUsesSdkLibraries() {
        String[] strArr = this.usesSdkLibraries;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long[] getUsesSdkLibrariesVersionsMajor() {
        long[] jArr = this.usesSdkLibrariesVersionsMajor;
        return jArr == null ? EmptyArray.LONG : jArr;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String[] getUsesStaticLibraries() {
        String[] strArr = this.usesStaticLibraries;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long[] getUsesStaticLibrariesVersions() {
        long[] jArr = this.usesStaticLibrariesVersions;
        return jArr == null ? EmptyArray.LONG : jArr;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public List<SharedLibrary> getSharedLibraryDependencies() {
        return this.pkgState.getUsesLibraryInfos();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public List<String> getUsesLibraryFiles() {
        return this.pkgState.getUsesLibraryFiles();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isHiddenUntilInstalled() {
        return this.pkgState.isHiddenUntilInstalled();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long[] getLastPackageUsageTime() {
        return this.pkgState.getLastPackageUsageTimeInMills();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isUpdatedSystemApp() {
        return this.pkgState.isUpdatedSystemApp();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isApkInUpdatedApex() {
        return this.pkgState.isApkInUpdatedApex();
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getApexModuleName() {
        return this.pkgState.getApexModuleName();
    }

    public PackageSetting setDomainSetId(UUID uuid) {
        this.mDomainSetId = uuid;
        onChanged();
        return this;
    }

    public PackageSetting setCategoryOverride(int i) {
        this.categoryOverride = i;
        onChanged();
        return this;
    }

    public PackageSetting setLegacyNativeLibraryPath(String str) {
        this.legacyNativeLibraryPath = str;
        onChanged();
        return this;
    }

    public PackageSetting setOldCodePaths(Set<String> set) {
        this.mOldCodePaths = set;
        onChanged();
        return this;
    }

    public PackageSetting setUsesSdkLibraries(String[] strArr) {
        this.usesSdkLibraries = strArr;
        onChanged();
        return this;
    }

    public PackageSetting setUsesSdkLibrariesVersionsMajor(long[] jArr) {
        this.usesSdkLibrariesVersionsMajor = jArr;
        onChanged();
        return this;
    }

    public PackageSetting setUsesStaticLibraries(String[] strArr) {
        this.usesStaticLibraries = strArr;
        onChanged();
        return this;
    }

    public PackageSetting setUsesStaticLibrariesVersions(long[] jArr) {
        this.usesStaticLibrariesVersions = jArr;
        onChanged();
        return this;
    }

    public PackageSetting setApexModuleName(String str) {
        this.pkgState.setApexModuleName(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public PackageStateUnserialized getTransientState() {
        return this.pkgState;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal, com.android.server.p011pm.pkg.PackageState
    public SparseArray<? extends PackageUserStateInternal> getUserStates() {
        return this.mUserStates;
    }

    public PackageSetting addMimeTypes(String str, Set<String> set) {
        if (this.mimeGroups == null) {
            this.mimeGroups = new ArrayMap();
        }
        Set<String> set2 = this.mimeGroups.get(str);
        if (set2 == null) {
            set2 = new ArraySet<>();
            this.mimeGroups.put(str, set2);
        }
        set2.addAll(set);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public PackageUserState getStateForUser(UserHandle userHandle) {
        PackageUserStateInternal packageUserStateInternal = getUserStates().get(userHandle.getIdentifier());
        return packageUserStateInternal == null ? PackageUserState.DEFAULT : packageUserStateInternal;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getPrimaryCpuAbi() {
        AndroidPackageInternal androidPackageInternal;
        if (TextUtils.isEmpty(this.mPrimaryCpuAbi) && (androidPackageInternal = this.pkg) != null) {
            return AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackageInternal);
        }
        return this.mPrimaryCpuAbi;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getSecondaryCpuAbi() {
        AndroidPackageInternal androidPackageInternal;
        if (TextUtils.isEmpty(this.mSecondaryCpuAbi) && (androidPackageInternal = this.pkg) != null) {
            return AndroidPackageUtils.getRawSecondaryCpuAbi(androidPackageInternal);
        }
        return this.mSecondaryCpuAbi;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getSeInfo() {
        String overrideSeInfo = getTransientState().getOverrideSeInfo();
        return !TextUtils.isEmpty(overrideSeInfo) ? overrideSeInfo : getTransientState().getSeInfo();
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public String getPrimaryCpuAbiLegacy() {
        return this.mPrimaryCpuAbi;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public String getSecondaryCpuAbiLegacy() {
        return this.mSecondaryCpuAbi;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public int getHiddenApiEnforcementPolicy() {
        return AndroidPackageUtils.getHiddenApiEnforcementPolicy(getAndroidPackage(), this);
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isApex() {
        return getAndroidPackage() != null && getAndroidPackage().isApex();
    }

    @Deprecated
    public Set<String> getOldCodePaths() {
        return this.mOldCodePaths;
    }

    @Deprecated
    public String getLegacyNativeLibraryPath() {
        return this.legacyNativeLibraryPath;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public String getRealName() {
        return this.mRealName;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public int getAppId() {
        return this.mAppId;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public AndroidPackageInternal getPkg() {
        return this.pkg;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public File getPath() {
        return this.mPath;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public String getPathString() {
        return this.mPathString;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public float getLoadingProgress() {
        return this.mLoadingProgress;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getCpuAbiOverride() {
        return this.mCpuAbiOverride;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long getLastModifiedTime() {
        return this.mLastModifiedTime;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public PackageSignatures getSignatures() {
        return this.signatures;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isInstallPermissionsFixed() {
        return this.installPermissionsFixed;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public PackageKeySetData getKeySetData() {
        return this.keySetData;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public InstallSource getInstallSource() {
        return this.installSource;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public String getVolumeUuid() {
        return this.volumeUuid;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public int getCategoryOverride() {
        return this.categoryOverride;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isUpdateAvailable() {
        return this.updateAvailable;
    }

    @Override // com.android.server.p011pm.pkg.PackageState
    public boolean isForceQueryableOverride() {
        return this.forceQueryableOverride;
    }

    public PackageStateUnserialized getPkgState() {
        return this.pkgState;
    }

    @Override // com.android.server.p011pm.pkg.PackageStateInternal
    public UUID getDomainSetId() {
        return this.mDomainSetId;
    }
}
