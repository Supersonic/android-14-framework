package com.android.server.devicepolicy;

import android.content.ComponentName;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.JournaledFile;
import com.android.server.devicepolicy.OwnersData;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PolicyVersionUpgrader {
    public final PolicyPathProvider mPathProvider;
    public final PolicyUpgraderDataProvider mProvider;

    @VisibleForTesting
    public PolicyVersionUpgrader(PolicyUpgraderDataProvider policyUpgraderDataProvider, PolicyPathProvider policyPathProvider) {
        this.mProvider = policyUpgraderDataProvider;
        this.mPathProvider = policyPathProvider;
    }

    public void upgradePolicy(int i) {
        int readVersion = readVersion();
        if (readVersion >= i) {
            Slog.i("DevicePolicyManager", String.format("Current version %d, latest version %d, not upgrading.", Integer.valueOf(readVersion), Integer.valueOf(i)));
            return;
        }
        int[] usersForUpgrade = this.mProvider.getUsersForUpgrade();
        OwnersData loadOwners = loadOwners(usersForUpgrade);
        SparseArray<DevicePolicyData> loadAllUsersData = loadAllUsersData(usersForUpgrade, readVersion, loadOwners);
        if (readVersion == 0) {
            Slog.i("DevicePolicyManager", String.format("Upgrading from version %d", Integer.valueOf(readVersion)));
            readVersion = 1;
        }
        if (readVersion == 1) {
            Slog.i("DevicePolicyManager", String.format("Upgrading from version %d", Integer.valueOf(readVersion)));
            upgradeSensorPermissionsAccess(usersForUpgrade, loadOwners, loadAllUsersData);
            readVersion = 2;
        }
        if (readVersion == 2) {
            Slog.i("DevicePolicyManager", String.format("Upgrading from version %d", Integer.valueOf(readVersion)));
            upgradeProtectedPackages(loadOwners, loadAllUsersData);
            readVersion = 3;
        }
        if (readVersion == 3) {
            Slog.i("DevicePolicyManager", String.format("Upgrading from version %d", Integer.valueOf(readVersion)));
            upgradePackageSuspension(usersForUpgrade, loadOwners, loadAllUsersData);
            readVersion = 4;
        }
        writePoliciesAndVersion(usersForUpgrade, loadAllUsersData, loadOwners, readVersion);
    }

    public final void upgradeSensorPermissionsAccess(int[] iArr, OwnersData ownersData, SparseArray<DevicePolicyData> sparseArray) {
        OwnersData.OwnerInfo ownerInfo;
        for (int i : iArr) {
            DevicePolicyData devicePolicyData = sparseArray.get(i);
            if (devicePolicyData != null) {
                Iterator<ActiveAdmin> it = devicePolicyData.mAdminList.iterator();
                while (it.hasNext()) {
                    ActiveAdmin next = it.next();
                    if (ownersData.mDeviceOwnerUserId == i && (ownerInfo = ownersData.mDeviceOwner) != null && ownerInfo.admin.equals(next.info.getComponent())) {
                        Slog.i("DevicePolicyManager", String.format("Marking Device Owner in user %d for permission grant ", Integer.valueOf(i)));
                        next.mAdminCanGrantSensorsPermissions = true;
                    }
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0052  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void upgradeProtectedPackages(OwnersData ownersData, SparseArray<DevicePolicyData> sparseArray) {
        List<String> list;
        ActiveAdmin activeAdmin;
        if (ownersData.mDeviceOwner == null) {
            return;
        }
        DevicePolicyData devicePolicyData = sparseArray.get(ownersData.mDeviceOwnerUserId);
        if (devicePolicyData == null) {
            Slog.e("DevicePolicyManager", "No policy data for do user");
            return;
        }
        ArrayMap<String, List<String>> arrayMap = ownersData.mDeviceOwnerProtectedPackages;
        List<String> list2 = null;
        if (arrayMap != null) {
            list = arrayMap.get(ownersData.mDeviceOwner.packageName);
            if (list != null) {
                Slog.i("DevicePolicyManager", "Found protected packages in Owners");
            }
            ownersData.mDeviceOwnerProtectedPackages = null;
        } else {
            if (devicePolicyData.mUserControlDisabledPackages != null) {
                Slog.i("DevicePolicyManager", "Found protected packages in DevicePolicyData");
                list = devicePolicyData.mUserControlDisabledPackages;
                devicePolicyData.mUserControlDisabledPackages = null;
            }
            activeAdmin = devicePolicyData.mAdminMap.get(ownersData.mDeviceOwner.admin);
            if (activeAdmin != null) {
                Slog.e("DevicePolicyManager", "DO admin not found in DO user");
                return;
            } else if (list2 != null) {
                activeAdmin.protectedPackages = new ArrayList(list2);
                return;
            } else {
                return;
            }
        }
        list2 = list;
        activeAdmin = devicePolicyData.mAdminMap.get(ownersData.mDeviceOwner.admin);
        if (activeAdmin != null) {
        }
    }

    public final void upgradePackageSuspension(int[] iArr, OwnersData ownersData, SparseArray<DevicePolicyData> sparseArray) {
        OwnersData.OwnerInfo ownerInfo = ownersData.mDeviceOwner;
        if (ownerInfo != null) {
            saveSuspendedPackages(sparseArray, ownersData.mDeviceOwnerUserId, ownerInfo.admin);
        }
        for (int i = 0; i < ownersData.mProfileOwners.size(); i++) {
            saveSuspendedPackages(sparseArray, ownersData.mProfileOwners.keyAt(i).intValue(), ownersData.mProfileOwners.valueAt(i).admin);
        }
    }

    public final void saveSuspendedPackages(SparseArray<DevicePolicyData> sparseArray, int i, ComponentName componentName) {
        DevicePolicyData devicePolicyData = sparseArray.get(i);
        if (devicePolicyData == null) {
            Slog.e("DevicePolicyManager", "No policy data for owner user, cannot migrate suspended packages");
            return;
        }
        ActiveAdmin activeAdmin = devicePolicyData.mAdminMap.get(componentName);
        if (activeAdmin == null) {
            Slog.e("DevicePolicyManager", "No admin for owner, cannot migrate suspended packages");
            return;
        }
        List<String> platformSuspendedPackages = this.mProvider.getPlatformSuspendedPackages(i);
        activeAdmin.suspendedPackages = platformSuspendedPackages;
        Slog.i("DevicePolicyManager", String.format("Saved %d packages suspended by %s in user %d", Integer.valueOf(platformSuspendedPackages.size()), componentName, Integer.valueOf(i)));
    }

    public final OwnersData loadOwners(int[] iArr) {
        OwnersData ownersData = new OwnersData(this.mPathProvider);
        ownersData.load(iArr);
        return ownersData;
    }

    public final void writePoliciesAndVersion(int[] iArr, SparseArray<DevicePolicyData> sparseArray, OwnersData ownersData, int i) {
        boolean z = true;
        for (int i2 : iArr) {
            z = z && writeDataForUser(i2, sparseArray.get(i2));
        }
        boolean z2 = z && ownersData.writeDeviceOwner();
        for (int i3 : iArr) {
            z2 = z2 && ownersData.writeProfileOwner(i3);
        }
        if (z2) {
            writeVersion(i);
        } else {
            Slog.e("DevicePolicyManager", String.format("Error: Failed upgrading policies to version %d", Integer.valueOf(i)));
        }
    }

    public final SparseArray<DevicePolicyData> loadAllUsersData(int[] iArr, int i, OwnersData ownersData) {
        SparseArray<DevicePolicyData> sparseArray = new SparseArray<>();
        for (int i2 : iArr) {
            sparseArray.append(i2, loadDataForUser(i2, i, getOwnerForUser(ownersData, i2)));
        }
        return sparseArray;
    }

    public final ComponentName getOwnerForUser(OwnersData ownersData, int i) {
        OwnersData.OwnerInfo ownerInfo;
        if (ownersData.mDeviceOwnerUserId == i && (ownerInfo = ownersData.mDeviceOwner) != null) {
            return ownerInfo.admin;
        }
        if (ownersData.mProfileOwners.containsKey(Integer.valueOf(i))) {
            return ownersData.mProfileOwners.get(Integer.valueOf(i)).admin;
        }
        return null;
    }

    public final DevicePolicyData loadDataForUser(int i, int i2, ComponentName componentName) {
        DevicePolicyData devicePolicyData = new DevicePolicyData(i);
        DevicePolicyData.load(devicePolicyData, this.mProvider.makeDevicePoliciesJournaledFile(i), this.mProvider.getAdminInfoSupplier(i), componentName);
        return devicePolicyData;
    }

    public final boolean writeDataForUser(int i, DevicePolicyData devicePolicyData) {
        return DevicePolicyData.store(devicePolicyData, this.mProvider.makeDevicePoliciesJournaledFile(i));
    }

    public final JournaledFile getVersionFile() {
        return this.mProvider.makePoliciesVersionJournaledFile(0);
    }

    public final int readVersion() {
        try {
            return Integer.parseInt(Files.readAllLines(getVersionFile().chooseForRead().toPath(), Charset.defaultCharset()).get(0));
        } catch (IOException | IndexOutOfBoundsException | NumberFormatException e) {
            Slog.e("DevicePolicyManager", "Error reading version", e);
            return 0;
        }
    }

    public final void writeVersion(int i) {
        JournaledFile versionFile = getVersionFile();
        try {
            Files.write(versionFile.chooseForWrite().toPath(), String.format("%d", Integer.valueOf(i)).getBytes(), new OpenOption[0]);
            versionFile.commit();
        } catch (IOException e) {
            Slog.e("DevicePolicyManager", String.format("Writing version %d failed", Integer.valueOf(i)), e);
            versionFile.rollback();
        }
    }
}
