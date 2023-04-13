package com.android.server.p011pm;

import android.content.p000pm.PackageManagerInternal;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.mutate.PackageStateMutator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.UserSystemPackageInstaller */
/* loaded from: classes2.dex */
public class UserSystemPackageInstaller {
    public static final String TAG = "UserSystemPackageInstaller";
    public final UserManagerService mUm;
    public final String[] mUserTypes;
    public final ArrayMap<String, Long> mWhitelistedPackagesForUserTypes;

    public static boolean isEnforceMode(int i) {
        return (i & 1) != 0;
    }

    public static boolean isIgnoreOtaMode(int i) {
        return (i & 16) != 0;
    }

    public static boolean isImplicitWhitelistMode(int i) {
        return (i & 4) != 0;
    }

    public static boolean isImplicitWhitelistSystemMode(int i) {
        return (i & 8) != 0;
    }

    public static boolean isLogMode(int i) {
        return (i & 2) != 0;
    }

    public UserSystemPackageInstaller(UserManagerService userManagerService, ArrayMap<String, UserTypeDetails> arrayMap) {
        this.mUm = userManagerService;
        String[] andSortKeysFromMap = getAndSortKeysFromMap(arrayMap);
        this.mUserTypes = andSortKeysFromMap;
        if (andSortKeysFromMap.length > 64) {
            throw new IllegalArgumentException("Device contains " + arrayMap.size() + " user types. However, UserSystemPackageInstaller does not work if there are more than 64 user types.");
        }
        this.mWhitelistedPackagesForUserTypes = determineWhitelistedPackagesForUserTypes(SystemConfig.getInstance());
    }

    @VisibleForTesting
    public UserSystemPackageInstaller(UserManagerService userManagerService, ArrayMap<String, Long> arrayMap, String[] strArr) {
        this.mUm = userManagerService;
        this.mUserTypes = strArr;
        this.mWhitelistedPackagesForUserTypes = arrayMap;
    }

    public boolean installWhitelistedSystemPackages(final boolean z, boolean z2, final ArraySet<String> arraySet) {
        int[] userIds;
        int whitelistMode = getWhitelistMode();
        checkWhitelistedSystemPackages(whitelistMode);
        boolean z3 = z2 && !isIgnoreOtaMode(whitelistMode);
        if (z3 || z) {
            if (!z || isEnforceMode(whitelistMode)) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Reviewing whitelisted packages due to ");
                sb.append(z ? "[firstBoot]" : "");
                sb.append(z3 ? "[upgrade]" : "");
                Slog.i(str, sb.toString());
                PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                final SparseArrayMap sparseArrayMap = new SparseArrayMap();
                for (final int i : this.mUm.getUserIds()) {
                    final Set<String> installablePackagesForUserId = getInstallablePackagesForUserId(i);
                    final boolean z4 = z3;
                    packageManagerInternal.forEachPackageState(new Consumer() { // from class: com.android.server.pm.UserSystemPackageInstaller$$ExternalSyntheticLambda2
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            UserSystemPackageInstaller.lambda$installWhitelistedSystemPackages$0(installablePackagesForUserId, i, z, z4, arraySet, sparseArrayMap, (PackageStateInternal) obj);
                        }
                    });
                }
                packageManagerInternal.commitPackageStateMutation(null, new Consumer() { // from class: com.android.server.pm.UserSystemPackageInstaller$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        UserSystemPackageInstaller.lambda$installWhitelistedSystemPackages$1(sparseArrayMap, (PackageStateMutator) obj);
                    }
                });
                return true;
            }
            return false;
        }
        return false;
    }

    public static /* synthetic */ void lambda$installWhitelistedSystemPackages$0(Set set, int i, boolean z, boolean z2, ArraySet arraySet, SparseArrayMap sparseArrayMap, PackageStateInternal packageStateInternal) {
        if (packageStateInternal.getPkg() == null) {
            return;
        }
        boolean z3 = (set == null || set.contains(packageStateInternal.getPackageName())) && !packageStateInternal.getTransientState().isHiddenUntilInstalled();
        if (packageStateInternal.getUserStateOrDefault(i).isInstalled() == z3 || !shouldChangeInstallationState(packageStateInternal, z3, i, z, z2, arraySet)) {
            return;
        }
        sparseArrayMap.add(i, packageStateInternal.getPackageName(), Boolean.valueOf(z3));
    }

    public static /* synthetic */ void lambda$installWhitelistedSystemPackages$1(SparseArrayMap sparseArrayMap, PackageStateMutator packageStateMutator) {
        for (int i = 0; i < sparseArrayMap.numMaps(); i++) {
            int keyAt = sparseArrayMap.keyAt(i);
            int numElementsForKey = sparseArrayMap.numElementsForKey(keyAt);
            for (int i2 = 0; i2 < numElementsForKey; i2++) {
                String str = (String) sparseArrayMap.keyAt(i, i2);
                boolean booleanValue = ((Boolean) sparseArrayMap.valueAt(i, i2)).booleanValue();
                packageStateMutator.forPackage(str).userState(keyAt).setInstalled(booleanValue).setUninstallReason(!booleanValue);
                String str2 = TAG + "CommitDebug";
                StringBuilder sb = new StringBuilder();
                sb.append(booleanValue != 0 ? "Installed " : "Uninstalled ");
                sb.append(str);
                sb.append(" for user ");
                sb.append(keyAt);
                Slog.i(str2, sb.toString());
            }
        }
    }

    public static boolean shouldChangeInstallationState(PackageStateInternal packageStateInternal, boolean z, int i, boolean z2, boolean z3, ArraySet<String> arraySet) {
        return z ? packageStateInternal.getUserStateOrDefault(i).getUninstallReason() == 1 : z2 || (z3 && !arraySet.contains(packageStateInternal.getPackageName()));
    }

    public final void checkWhitelistedSystemPackages(int i) {
        if (isLogMode(i) || isEnforceMode(i)) {
            String str = TAG;
            Slog.v(str, "Checking that all system packages are whitelisted.");
            List<String> packagesWhitelistWarnings = getPackagesWhitelistWarnings();
            int size = packagesWhitelistWarnings.size();
            if (size == 0) {
                Slog.v(str, "checkWhitelistedSystemPackages(mode=" + modeToString(i) + ") has no warnings");
            } else {
                Slog.w(str, "checkWhitelistedSystemPackages(mode=" + modeToString(i) + ") has " + size + " warnings:");
                for (int i2 = 0; i2 < size; i2++) {
                    Slog.w(TAG, packagesWhitelistWarnings.get(i2));
                }
            }
            if (!isImplicitWhitelistMode(i) || isLogMode(i)) {
                List<String> packagesWhitelistErrors = getPackagesWhitelistErrors(i);
                int size2 = packagesWhitelistErrors.size();
                if (size2 == 0) {
                    String str2 = TAG;
                    Slog.v(str2, "checkWhitelistedSystemPackages(mode=" + modeToString(i) + ") has no errors");
                    return;
                }
                String str3 = TAG;
                Slog.e(str3, "checkWhitelistedSystemPackages(mode=" + modeToString(i) + ") has " + size2 + " errors:");
                boolean isImplicitWhitelistMode = isImplicitWhitelistMode(i) ^ true;
                for (int i3 = 0; i3 < size2; i3++) {
                    String str4 = packagesWhitelistErrors.get(i3);
                    if (isImplicitWhitelistMode) {
                        Slog.wtf(TAG, str4);
                    } else {
                        Slog.e(TAG, str4);
                    }
                }
            }
        }
    }

    public final List<String> getPackagesWhitelistWarnings() {
        Set<String> whitelistedSystemPackages = getWhitelistedSystemPackages();
        ArrayList arrayList = new ArrayList();
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        for (String str : whitelistedSystemPackages) {
            PackageStateInternal packageStateInternal = packageManagerInternal.getPackageStateInternal(str);
            AndroidPackage androidPackage = packageStateInternal == null ? null : packageStateInternal.getAndroidPackage();
            if (androidPackage == null) {
                arrayList.add(String.format("%s is allowlisted but not present.", str));
            } else if (!packageStateInternal.isSystem()) {
                arrayList.add(String.format("%s is allowlisted and present but not a system package.", str));
            } else if (shouldUseOverlayTargetName(androidPackage)) {
                arrayList.add(String.format("%s is allowlisted unnecessarily since it's a static overlay.", str));
            }
        }
        return arrayList;
    }

    public final List<String> getPackagesWhitelistErrors(int i) {
        if ((!isEnforceMode(i) || isImplicitWhitelistMode(i)) && !isLogMode(i)) {
            return Collections.emptyList();
        }
        final ArrayList arrayList = new ArrayList();
        final Set<String> whitelistedSystemPackages = getWhitelistedSystemPackages();
        final PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        packageManagerInternal.forEachPackageState(new Consumer() { // from class: com.android.server.pm.UserSystemPackageInstaller$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UserSystemPackageInstaller.lambda$getPackagesWhitelistErrors$2(whitelistedSystemPackages, packageManagerInternal, arrayList, (PackageStateInternal) obj);
            }
        });
        return arrayList;
    }

    public static /* synthetic */ void lambda$getPackagesWhitelistErrors$2(Set set, PackageManagerInternal packageManagerInternal, List list, PackageStateInternal packageStateInternal) {
        AndroidPackage androidPackage = packageStateInternal.getAndroidPackage();
        if (androidPackage == null || !packageStateInternal.isSystem() || androidPackage.isApex()) {
            return;
        }
        String manifestPackageName = androidPackage.getManifestPackageName();
        if (set.contains(manifestPackageName) || shouldUseOverlayTargetName(packageManagerInternal.getPackage(manifestPackageName))) {
            return;
        }
        list.add(String.format("System package %s is not whitelisted using 'install-in-user-type' in SystemConfig for any user types!", manifestPackageName));
    }

    public static boolean shouldUseOverlayTargetName(AndroidPackage androidPackage) {
        return androidPackage.isOverlayIsStatic();
    }

    public final int getWhitelistMode() {
        int i = SystemProperties.getInt("persist.debug.user.package_whitelist_mode", -1);
        return i != -1 ? i : getDeviceDefaultWhitelistMode();
    }

    public final int getDeviceDefaultWhitelistMode() {
        return Resources.getSystem().getInteger(17694980);
    }

    public static String modeToString(int i) {
        return i != -1000 ? i != -1 ? DebugUtils.flagsToString(UserSystemPackageInstaller.class, "USER_TYPE_PACKAGE_WHITELIST_MODE_", i) : "DEVICE_DEFAULT" : "NONE";
    }

    public final Set<String> getInstallablePackagesForUserId(int i) {
        return getInstallablePackagesForUserType(this.mUm.getUserInfo(i).userType);
    }

    public Set<String> getInstallablePackagesForUserType(String str) {
        int whitelistMode = getWhitelistMode();
        if (isEnforceMode(whitelistMode)) {
            final boolean z = isImplicitWhitelistMode(whitelistMode) || (isImplicitWhitelistSystemMode(whitelistMode) && this.mUm.isUserTypeSubtypeOfSystem(str));
            final Set<String> whitelistedPackagesForUserType = getWhitelistedPackagesForUserType(str);
            final ArraySet arraySet = new ArraySet();
            ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).forEachPackageState(new Consumer() { // from class: com.android.server.pm.UserSystemPackageInstaller$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    UserSystemPackageInstaller.this.lambda$getInstallablePackagesForUserType$3(whitelistedPackagesForUserType, z, arraySet, (PackageStateInternal) obj);
                }
            });
            return arraySet;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getInstallablePackagesForUserType$3(Set set, boolean z, Set set2, PackageStateInternal packageStateInternal) {
        AndroidPackage androidPackage = packageStateInternal.getAndroidPackage();
        if (androidPackage != null && packageStateInternal.isSystem() && shouldInstallPackage(androidPackage, this.mWhitelistedPackagesForUserTypes, set, z)) {
            set2.add(androidPackage.getPackageName());
        }
    }

    @VisibleForTesting
    public static boolean shouldInstallPackage(AndroidPackage androidPackage, ArrayMap<String, Long> arrayMap, Set<String> set, boolean z) {
        String overlayTarget = shouldUseOverlayTargetName(androidPackage) ? androidPackage.getOverlayTarget() : androidPackage.getManifestPackageName();
        return (z && !arrayMap.containsKey(overlayTarget)) || set.contains(overlayTarget) || androidPackage.isApex();
    }

    @VisibleForTesting
    public Set<String> getWhitelistedPackagesForUserType(String str) {
        long userTypeMask = getUserTypeMask(str);
        ArraySet arraySet = new ArraySet(this.mWhitelistedPackagesForUserTypes.size());
        for (int i = 0; i < this.mWhitelistedPackagesForUserTypes.size(); i++) {
            String keyAt = this.mWhitelistedPackagesForUserTypes.keyAt(i);
            if ((this.mWhitelistedPackagesForUserTypes.valueAt(i).longValue() & userTypeMask) != 0) {
                arraySet.add(keyAt);
            }
        }
        return arraySet;
    }

    public final Set<String> getWhitelistedSystemPackages() {
        return this.mWhitelistedPackagesForUserTypes.keySet();
    }

    @VisibleForTesting
    public ArrayMap<String, Long> determineWhitelistedPackagesForUserTypes(SystemConfig systemConfig) {
        Map<String, Long> baseTypeBitSets = getBaseTypeBitSets();
        ArrayMap<String, Set<String>> andClearPackageToUserTypeWhitelist = systemConfig.getAndClearPackageToUserTypeWhitelist();
        ArrayMap<String, Long> arrayMap = new ArrayMap<>(andClearPackageToUserTypeWhitelist.size() + 1);
        for (int i = 0; i < andClearPackageToUserTypeWhitelist.size(); i++) {
            String intern = andClearPackageToUserTypeWhitelist.keyAt(i).intern();
            long typesBitSet = getTypesBitSet(andClearPackageToUserTypeWhitelist.valueAt(i), baseTypeBitSets);
            if (typesBitSet != 0) {
                arrayMap.put(intern, Long.valueOf(typesBitSet));
            }
        }
        ArrayMap<String, Set<String>> andClearPackageToUserTypeBlacklist = systemConfig.getAndClearPackageToUserTypeBlacklist();
        for (int i2 = 0; i2 < andClearPackageToUserTypeBlacklist.size(); i2++) {
            String intern2 = andClearPackageToUserTypeBlacklist.keyAt(i2).intern();
            long typesBitSet2 = getTypesBitSet(andClearPackageToUserTypeBlacklist.valueAt(i2), baseTypeBitSets);
            Long l = arrayMap.get(intern2);
            if (l != null) {
                arrayMap.put(intern2, Long.valueOf((~typesBitSet2) & l.longValue()));
            } else if (typesBitSet2 != 0) {
                arrayMap.put(intern2, 0L);
            }
        }
        arrayMap.put(PackageManagerShellCommandDataLoader.PACKAGE, -1L);
        return arrayMap;
    }

    @VisibleForTesting
    public long getUserTypeMask(String str) {
        int binarySearch = Arrays.binarySearch(this.mUserTypes, str);
        if (binarySearch >= 0) {
            return 1 << binarySearch;
        }
        return 0L;
    }

    public final Map<String, Long> getBaseTypeBitSets() {
        long j = 0;
        long j2 = 0;
        int i = 0;
        long j3 = 0;
        while (true) {
            String[] strArr = this.mUserTypes;
            if (i < strArr.length) {
                if (this.mUm.isUserTypeSubtypeOfFull(strArr[i])) {
                    j |= 1 << i;
                }
                if (this.mUm.isUserTypeSubtypeOfSystem(this.mUserTypes[i])) {
                    j3 |= 1 << i;
                }
                if (this.mUm.isUserTypeSubtypeOfProfile(this.mUserTypes[i])) {
                    j2 |= 1 << i;
                }
                i++;
            } else {
                ArrayMap arrayMap = new ArrayMap(3);
                arrayMap.put("FULL", Long.valueOf(j));
                arrayMap.put("SYSTEM", Long.valueOf(j3));
                arrayMap.put("PROFILE", Long.valueOf(j2));
                return arrayMap;
            }
        }
    }

    public final long getTypesBitSet(Iterable<String> iterable, Map<String, Long> map) {
        long j = 0;
        for (String str : iterable) {
            Long l = map.get(str);
            if (l != null) {
                j |= l.longValue();
            } else {
                long userTypeMask = getUserTypeMask(str);
                if (userTypeMask != 0) {
                    j |= userTypeMask;
                } else {
                    Slog.w(TAG, "SystemConfig contained an invalid user type: " + str);
                }
            }
        }
        return j;
    }

    public static String[] getAndSortKeysFromMap(ArrayMap<String, ?> arrayMap) {
        String[] strArr = new String[arrayMap.size()];
        for (int i = 0; i < arrayMap.size(); i++) {
            strArr[i] = arrayMap.keyAt(i);
        }
        Arrays.sort(strArr);
        return strArr;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        int whitelistMode = getWhitelistMode();
        indentingPrintWriter.println("Whitelisted packages per user type");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("Mode: ");
        indentingPrintWriter.print(whitelistMode);
        indentingPrintWriter.print(isEnforceMode(whitelistMode) ? " (enforced)" : "");
        indentingPrintWriter.print(isLogMode(whitelistMode) ? " (logged)" : "");
        indentingPrintWriter.print(isImplicitWhitelistMode(whitelistMode) ? " (implicit)" : "");
        indentingPrintWriter.print(isIgnoreOtaMode(whitelistMode) ? " (ignore OTAs)" : "");
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Legend");
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mUserTypes.length; i++) {
            indentingPrintWriter.println(i + " -> " + this.mUserTypes[i]);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.increaseIndent();
        int size = this.mWhitelistedPackagesForUserTypes.size();
        if (size == 0) {
            indentingPrintWriter.println("No packages");
            indentingPrintWriter.decreaseIndent();
            return;
        }
        indentingPrintWriter.print(size);
        indentingPrintWriter.println(" packages:");
        indentingPrintWriter.increaseIndent();
        for (int i2 = 0; i2 < size; i2++) {
            indentingPrintWriter.print(this.mWhitelistedPackagesForUserTypes.keyAt(i2));
            indentingPrintWriter.print(": ");
            long longValue = this.mWhitelistedPackagesForUserTypes.valueAt(i2).longValue();
            for (int i3 = 0; i3 < this.mUserTypes.length; i3++) {
                if (((1 << i3) & longValue) != 0) {
                    indentingPrintWriter.print(i3);
                    indentingPrintWriter.print(" ");
                }
            }
            indentingPrintWriter.println();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.increaseIndent();
        dumpPackageWhitelistProblems(indentingPrintWriter, whitelistMode, true, false);
        indentingPrintWriter.decreaseIndent();
    }

    public void dumpPackageWhitelistProblems(IndentingPrintWriter indentingPrintWriter, int i, boolean z, boolean z2) {
        if (i == -1000) {
            i = getWhitelistMode();
        } else if (i == -1) {
            i = getDeviceDefaultWhitelistMode();
        }
        if (z2) {
            i &= -3;
        }
        String str = TAG;
        Slog.v(str, "dumpPackageWhitelistProblems(): using mode " + modeToString(i));
        showIssues(indentingPrintWriter, z, getPackagesWhitelistErrors(i), "errors");
        if (z2) {
            return;
        }
        showIssues(indentingPrintWriter, z, getPackagesWhitelistWarnings(), "warnings");
    }

    public static void showIssues(IndentingPrintWriter indentingPrintWriter, boolean z, List<String> list, String str) {
        int size = list.size();
        if (size == 0) {
            if (z) {
                indentingPrintWriter.print("No ");
                indentingPrintWriter.println(str);
                return;
            }
            return;
        }
        if (z) {
            indentingPrintWriter.print(size);
            indentingPrintWriter.print(' ');
            indentingPrintWriter.println(str);
            indentingPrintWriter.increaseIndent();
        }
        for (int i = 0; i < size; i++) {
            indentingPrintWriter.println(list.get(i));
        }
        if (z) {
            indentingPrintWriter.decreaseIndent();
        }
    }
}
