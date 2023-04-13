package com.android.server.p011pm;

import android.content.pm.SigningDetails;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.permission.LegacyPermissionState;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.SharedUserApi;
import com.android.server.p011pm.pkg.component.ComponentMutateUtils;
import com.android.server.p011pm.pkg.component.ParsedProcess;
import com.android.server.p011pm.pkg.component.ParsedProcessImpl;
import com.android.server.utils.SnapshotCache;
import com.android.server.utils.Watchable;
import com.android.server.utils.WatchedArraySet;
import com.android.server.utils.Watcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.pm.SharedUserSetting */
/* loaded from: classes2.dex */
public final class SharedUserSetting extends SettingBase implements SharedUserApi {
    public int mAppId;
    public final WatchedArraySet<PackageSetting> mDisabledPackages;
    public final SnapshotCache<WatchedArraySet<PackageSetting>> mDisabledPackagesSnapshot;
    public final Watcher mObserver;
    public final WatchedArraySet<PackageSetting> mPackages;
    public final SnapshotCache<WatchedArraySet<PackageSetting>> mPackagesSnapshot;
    public final SnapshotCache<SharedUserSetting> mSnapshot;
    public final String name;
    public final ArrayMap<String, ParsedProcess> processes;
    public int seInfoTargetSdkVersion;
    public final PackageSignatures signatures;
    public Boolean signaturesChanged;
    public int uidFlags;
    public int uidPrivateFlags;

    public final SnapshotCache<SharedUserSetting> makeCache() {
        return new SnapshotCache<SharedUserSetting>(this, this) { // from class: com.android.server.pm.SharedUserSetting.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.utils.SnapshotCache
            public SharedUserSetting createSnapshot() {
                return new SharedUserSetting();
            }
        };
    }

    public SharedUserSetting(String str, int i, int i2) {
        super(i, i2);
        this.mObserver = new Watcher() { // from class: com.android.server.pm.SharedUserSetting.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                SharedUserSetting.this.onChanged();
            }
        };
        this.signatures = new PackageSignatures();
        this.uidFlags = i;
        this.uidPrivateFlags = i2;
        this.name = str;
        this.seInfoTargetSdkVersion = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        WatchedArraySet<PackageSetting> watchedArraySet = new WatchedArraySet<>();
        this.mPackages = watchedArraySet;
        this.mPackagesSnapshot = new SnapshotCache.Auto(watchedArraySet, watchedArraySet, "SharedUserSetting.packages");
        WatchedArraySet<PackageSetting> watchedArraySet2 = new WatchedArraySet<>();
        this.mDisabledPackages = watchedArraySet2;
        this.mDisabledPackagesSnapshot = new SnapshotCache.Auto(watchedArraySet2, watchedArraySet2, "SharedUserSetting.mDisabledPackages");
        this.processes = new ArrayMap<>();
        registerObservers();
        this.mSnapshot = makeCache();
    }

    public SharedUserSetting(SharedUserSetting sharedUserSetting) {
        super(sharedUserSetting);
        this.mObserver = new Watcher() { // from class: com.android.server.pm.SharedUserSetting.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                SharedUserSetting.this.onChanged();
            }
        };
        PackageSignatures packageSignatures = new PackageSignatures();
        this.signatures = packageSignatures;
        this.name = sharedUserSetting.name;
        this.mAppId = sharedUserSetting.mAppId;
        this.uidFlags = sharedUserSetting.uidFlags;
        this.uidPrivateFlags = sharedUserSetting.uidPrivateFlags;
        this.mPackages = sharedUserSetting.mPackagesSnapshot.snapshot();
        this.mPackagesSnapshot = new SnapshotCache.Sealed();
        this.mDisabledPackages = sharedUserSetting.mDisabledPackagesSnapshot.snapshot();
        this.mDisabledPackagesSnapshot = new SnapshotCache.Sealed();
        packageSignatures.mSigningDetails = sharedUserSetting.signatures.mSigningDetails;
        this.signaturesChanged = sharedUserSetting.signaturesChanged;
        this.processes = new ArrayMap<>(sharedUserSetting.processes);
        this.mSnapshot = new SnapshotCache.Sealed();
    }

    public final void registerObservers() {
        this.mPackages.registerObserver(this.mObserver);
        this.mDisabledPackages.registerObserver(this.mObserver);
    }

    @Override // com.android.server.utils.Snappable
    public SharedUserSetting snapshot() {
        return this.mSnapshot.snapshot();
    }

    public String toString() {
        return "SharedUserSetting{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "/" + this.mAppId + "}";
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.mAppId);
        protoOutputStream.write(1138166333442L, this.name);
        protoOutputStream.end(start);
    }

    public void addProcesses(Map<String, ParsedProcess> map) {
        if (map != null) {
            for (String str : map.keySet()) {
                ParsedProcess parsedProcess = map.get(str);
                ParsedProcess parsedProcess2 = this.processes.get(parsedProcess.getName());
                if (parsedProcess2 == null) {
                    this.processes.put(parsedProcess.getName(), new ParsedProcessImpl(parsedProcess));
                } else {
                    ComponentMutateUtils.addStateFrom(parsedProcess2, parsedProcess);
                }
            }
            onChanged();
        }
    }

    public boolean removePackage(PackageSetting packageSetting) {
        if (this.mPackages.remove(packageSetting)) {
            if ((getFlags() & packageSetting.getFlags()) != 0) {
                int i = this.uidFlags;
                for (int i2 = 0; i2 < this.mPackages.size(); i2++) {
                    i |= this.mPackages.valueAt(i2).getFlags();
                }
                setFlags(i);
            }
            if ((packageSetting.getPrivateFlags() & getPrivateFlags()) != 0) {
                int i3 = this.uidPrivateFlags;
                for (int i4 = 0; i4 < this.mPackages.size(); i4++) {
                    i3 |= this.mPackages.valueAt(i4).getPrivateFlags();
                }
                setPrivateFlags(i3);
            }
            updateProcesses();
            onChanged();
            return true;
        }
        return false;
    }

    public void addPackage(PackageSetting packageSetting) {
        if (this.mPackages.size() == 0 && packageSetting.getPkg() != null) {
            this.seInfoTargetSdkVersion = packageSetting.getPkg().getTargetSdkVersion();
        }
        if (this.mPackages.add(packageSetting)) {
            setFlags(getFlags() | packageSetting.getFlags());
            setPrivateFlags(getPrivateFlags() | packageSetting.getPrivateFlags());
            onChanged();
        }
        if (packageSetting.getPkg() != null) {
            addProcesses(packageSetting.getPkg().getProcesses());
        }
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public List<AndroidPackage> getPackages() {
        WatchedArraySet<PackageSetting> watchedArraySet = this.mPackages;
        if (watchedArraySet == null || watchedArraySet.size() == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(this.mPackages.size());
        for (int i = 0; i < this.mPackages.size(); i++) {
            PackageSetting valueAt = this.mPackages.valueAt(i);
            if (valueAt != null && valueAt.getPkg() != null) {
                arrayList.add(valueAt.getPkg());
            }
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public boolean isPrivileged() {
        return (getPrivateFlags() & 8) != 0;
    }

    public boolean isSingleUser() {
        if (this.mPackages.size() == 1 && this.mDisabledPackages.size() <= 1) {
            if (this.mDisabledPackages.size() == 1) {
                AndroidPackageInternal pkg = this.mDisabledPackages.valueAt(0).getPkg();
                return pkg != null && pkg.isLeavingSharedUser();
            }
            return true;
        }
        return false;
    }

    public void fixSeInfoLocked() {
        WatchedArraySet<PackageSetting> watchedArraySet = this.mPackages;
        if (watchedArraySet == null || watchedArraySet.size() == 0) {
            return;
        }
        for (int i = 0; i < this.mPackages.size(); i++) {
            PackageSetting valueAt = this.mPackages.valueAt(i);
            if (valueAt != null && valueAt.getPkg() != null && valueAt.getPkg().getTargetSdkVersion() < this.seInfoTargetSdkVersion) {
                this.seInfoTargetSdkVersion = valueAt.getPkg().getTargetSdkVersion();
                onChanged();
            }
        }
        for (int i2 = 0; i2 < this.mPackages.size(); i2++) {
            PackageSetting valueAt2 = this.mPackages.valueAt(i2);
            if (valueAt2 != null && valueAt2.getPkg() != null) {
                valueAt2.getPkgState().setOverrideSeInfo(SELinuxMMAC.getSeInfo(valueAt2.getPkg(), isPrivileged() | valueAt2.isPrivileged(), this.seInfoTargetSdkVersion));
                onChanged();
            }
        }
    }

    public void updateProcesses() {
        this.processes.clear();
        for (int size = this.mPackages.size() - 1; size >= 0; size--) {
            AndroidPackageInternal pkg = this.mPackages.valueAt(size).getPkg();
            if (pkg != null) {
                addProcesses(pkg.getProcesses());
            }
        }
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public String getName() {
        return this.name;
    }

    public int getAppId() {
        return this.mAppId;
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public int getSeInfoTargetSdkVersion() {
        return this.seInfoTargetSdkVersion;
    }

    public WatchedArraySet<PackageSetting> getPackageSettings() {
        return this.mPackages;
    }

    public WatchedArraySet<PackageSetting> getDisabledPackageSettings() {
        return this.mDisabledPackages;
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public ArraySet<? extends PackageStateInternal> getPackageStates() {
        return this.mPackages.untrackedStorage();
    }

    public ArraySet<? extends PackageStateInternal> getDisabledPackageStates() {
        return this.mDisabledPackages.untrackedStorage();
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public SigningDetails getSigningDetails() {
        return this.signatures.mSigningDetails;
    }

    @Override // com.android.server.p011pm.pkg.SharedUserApi
    public LegacyPermissionState getSharedUserLegacyPermissionState() {
        return super.getLegacyPermissionState();
    }
}
