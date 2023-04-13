package com.android.server.p008om;

import android.content.om.CriticalOverlayInfo;
import android.content.om.OverlayIdentifier;
import android.content.om.OverlayInfo;
import android.content.pm.UserPackage;
import android.content.pm.overlay.OverlayPaths;
import android.content.pm.parsing.FrameworkParsingPackageUtils;
import android.os.FabricatedOverlayInfo;
import android.os.FabricatedOverlayInternal;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.content.om.OverlayConfig;
import com.android.internal.util.CollectionUtils;
import com.android.server.p008om.OverlayManagerSettings;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
/* renamed from: com.android.server.om.OverlayManagerServiceImpl */
/* loaded from: classes2.dex */
public final class OverlayManagerServiceImpl {
    public final String[] mDefaultOverlays;
    public final IdmapManager mIdmapManager;
    public final OverlayConfig mOverlayConfig;
    public final PackageManagerHelper mPackageManager;
    public final OverlayManagerSettings mSettings;

    public final boolean mustReinitializeOverlay(AndroidPackage androidPackage, OverlayInfo overlayInfo) {
        boolean isPackageConfiguredMutable;
        if (overlayInfo != null && Objects.equals(androidPackage.getOverlayTarget(), overlayInfo.targetPackageName) && Objects.equals(androidPackage.getOverlayTargetOverlayableName(), overlayInfo.targetOverlayableName) && !overlayInfo.isFabricated && (isPackageConfiguredMutable = isPackageConfiguredMutable(androidPackage)) == overlayInfo.isMutable) {
            return (isPackageConfiguredMutable || isPackageConfiguredEnabled(androidPackage) == overlayInfo.isEnabled()) ? false : true;
        }
        return true;
    }

    public final boolean mustReinitializeOverlay(FabricatedOverlayInfo fabricatedOverlayInfo, OverlayInfo overlayInfo) {
        return (overlayInfo != null && Objects.equals(fabricatedOverlayInfo.targetPackageName, overlayInfo.targetPackageName) && Objects.equals(fabricatedOverlayInfo.targetOverlayable, overlayInfo.targetOverlayableName)) ? false : true;
    }

    public OverlayManagerServiceImpl(PackageManagerHelper packageManagerHelper, IdmapManager idmapManager, OverlayManagerSettings overlayManagerSettings, OverlayConfig overlayConfig, String[] strArr) {
        this.mPackageManager = packageManagerHelper;
        this.mIdmapManager = idmapManager;
        this.mSettings = overlayManagerSettings;
        this.mOverlayConfig = overlayConfig;
        this.mDefaultOverlays = strArr;
    }

    public ArraySet<UserPackage> updateOverlaysForUser(int i) {
        String[] strArr;
        ArraySet<UserPackage> arraySet = new ArraySet<>();
        final ArrayMap<String, PackageState> initializeForUser = this.mPackageManager.initializeForUser(i);
        CollectionUtils.addAll(arraySet, removeOverlaysForUser(new Predicate() { // from class: com.android.server.om.OverlayManagerServiceImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateOverlaysForUser$0;
                lambda$updateOverlaysForUser$0 = OverlayManagerServiceImpl.lambda$updateOverlaysForUser$0(initializeForUser, (OverlayInfo) obj);
                return lambda$updateOverlaysForUser$0;
            }
        }, i));
        ArraySet arraySet2 = new ArraySet();
        for (PackageState packageState : initializeForUser.values()) {
            AndroidPackage androidPackage = packageState.getAndroidPackage();
            String overlayTarget = androidPackage == null ? null : androidPackage.getOverlayTarget();
            if (!TextUtils.isEmpty(overlayTarget)) {
                arraySet2.add(overlayTarget);
            }
        }
        int size = initializeForUser.size();
        for (int i2 = 0; i2 < size; i2++) {
            PackageState valueAt = initializeForUser.valueAt(i2);
            AndroidPackage androidPackage2 = valueAt.getAndroidPackage();
            if (androidPackage2 != null) {
                String packageName = valueAt.getPackageName();
                try {
                    CollectionUtils.addAll(arraySet, updatePackageOverlays(androidPackage2, i, 0));
                    if (arraySet2.contains(packageName)) {
                        arraySet.add(UserPackage.of(i, packageName));
                    }
                } catch (OperationFailedException e) {
                    Slog.e("OverlayManager", "failed to initialize overlays of '" + packageName + "' for user " + i + "", e);
                }
            }
        }
        for (FabricatedOverlayInfo fabricatedOverlayInfo : getFabricatedOverlayInfos()) {
            try {
                CollectionUtils.addAll(arraySet, registerFabricatedOverlay(fabricatedOverlayInfo, i));
            } catch (OperationFailedException e2) {
                Slog.e("OverlayManager", "failed to initialize fabricated overlay of '" + fabricatedOverlayInfo.path + "' for user " + i + "", e2);
            }
        }
        ArraySet arraySet3 = new ArraySet();
        ArrayMap<String, List<OverlayInfo>> overlaysForUser = this.mSettings.getOverlaysForUser(i);
        int size2 = overlaysForUser.size();
        for (int i3 = 0; i3 < size2; i3++) {
            List<OverlayInfo> valueAt2 = overlaysForUser.valueAt(i3);
            int size3 = valueAt2 != null ? valueAt2.size() : 0;
            for (int i4 = 0; i4 < size3; i4++) {
                OverlayInfo overlayInfo = valueAt2.get(i4);
                if (overlayInfo.isEnabled()) {
                    arraySet3.add(overlayInfo.category);
                }
            }
        }
        for (String str : this.mDefaultOverlays) {
            try {
                OverlayIdentifier overlayIdentifier = new OverlayIdentifier(str);
                OverlayInfo overlayInfo2 = this.mSettings.getOverlayInfo(overlayIdentifier, i);
                if (!arraySet3.contains(overlayInfo2.category)) {
                    Slog.w("OverlayManager", "Enabling default overlay '" + str + "' for target '" + overlayInfo2.targetPackageName + "' in category '" + overlayInfo2.category + "' for user " + i);
                    this.mSettings.setEnabled(overlayIdentifier, i, true);
                    if (updateState(overlayInfo2, i, 0)) {
                        CollectionUtils.add(arraySet, UserPackage.of(overlayInfo2.userId, overlayInfo2.targetPackageName));
                    }
                }
            } catch (OverlayManagerSettings.BadKeyException e3) {
                Slog.e("OverlayManager", "Failed to set default overlay '" + str + "' for user " + i, e3);
            }
        }
        cleanStaleResourceCache();
        return arraySet;
    }

    public static /* synthetic */ boolean lambda$updateOverlaysForUser$0(ArrayMap arrayMap, OverlayInfo overlayInfo) {
        return !arrayMap.containsKey(overlayInfo.packageName);
    }

    public void onUserRemoved(int i) {
        this.mSettings.removeUser(i);
    }

    public Set<UserPackage> onPackageAdded(String str, int i) throws OperationFailedException {
        ArraySet arraySet = new ArraySet();
        arraySet.add(UserPackage.of(i, str));
        arraySet.addAll(reconcileSettingsForPackage(str, i, 0));
        return arraySet;
    }

    public Set<UserPackage> onPackageChanged(String str, int i) throws OperationFailedException {
        return reconcileSettingsForPackage(str, i, 0);
    }

    public Set<UserPackage> onPackageReplacing(String str, boolean z, int i) throws OperationFailedException {
        return reconcileSettingsForPackage(str, i, z ? 6 : 2);
    }

    public Set<UserPackage> onPackageReplaced(String str, int i) throws OperationFailedException {
        return reconcileSettingsForPackage(str, i, 0);
    }

    public Set<UserPackage> onPackageRemoved(final String str, int i) {
        return CollectionUtils.addAll(updateOverlaysForTarget(str, i, 0), removeOverlaysForUser(new Predicate() { // from class: com.android.server.om.OverlayManagerServiceImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onPackageRemoved$1;
                lambda$onPackageRemoved$1 = OverlayManagerServiceImpl.lambda$onPackageRemoved$1(str, (OverlayInfo) obj);
                return lambda$onPackageRemoved$1;
            }
        }, i));
    }

    public static /* synthetic */ boolean lambda$onPackageRemoved$1(String str, OverlayInfo overlayInfo) {
        return str.equals(overlayInfo.packageName);
    }

    public final Set<UserPackage> removeOverlaysForUser(final Predicate<OverlayInfo> predicate, final int i) {
        List<OverlayInfo> removeIf = this.mSettings.removeIf(new Predicate() { // from class: com.android.server.om.OverlayManagerServiceImpl$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeOverlaysForUser$2;
                lambda$removeOverlaysForUser$2 = OverlayManagerServiceImpl.lambda$removeOverlaysForUser$2(i, predicate, (OverlayInfo) obj);
                return lambda$removeOverlaysForUser$2;
            }
        });
        Set<UserPackage> emptySet = Collections.emptySet();
        int size = removeIf.size();
        for (int i2 = 0; i2 < size; i2++) {
            OverlayInfo overlayInfo = removeIf.get(i2);
            emptySet = CollectionUtils.add(emptySet, UserPackage.of(i, overlayInfo.targetPackageName));
            removeIdmapIfPossible(overlayInfo);
        }
        return emptySet;
    }

    public static /* synthetic */ boolean lambda$removeOverlaysForUser$2(int i, Predicate predicate, OverlayInfo overlayInfo) {
        return i == overlayInfo.userId && predicate.test(overlayInfo);
    }

    public final Set<UserPackage> updateOverlaysForTarget(String str, int i, int i2) {
        boolean remove;
        List<OverlayInfo> overlaysForTarget = this.mSettings.getOverlaysForTarget(str, i);
        int size = overlaysForTarget.size();
        boolean z = false;
        for (int i3 = 0; i3 < size; i3++) {
            OverlayInfo overlayInfo = overlaysForTarget.get(i3);
            try {
                remove = updateState(overlayInfo, i, i2);
            } catch (OverlayManagerSettings.BadKeyException e) {
                Slog.e("OverlayManager", "failed to update settings", e);
                remove = this.mSettings.remove(overlayInfo.getOverlayIdentifier(), i);
            }
            z |= remove;
        }
        if (!z) {
            return Collections.emptySet();
        }
        return Set.of(UserPackage.of(i, str));
    }

    public final Set<UserPackage> updatePackageOverlays(AndroidPackage androidPackage, int i, int i2) throws OperationFailedException {
        if (androidPackage.getOverlayTarget() == null) {
            return Collections.emptySet();
        }
        Set<UserPackage> emptySet = Collections.emptySet();
        OverlayIdentifier overlayIdentifier = new OverlayIdentifier(androidPackage.getPackageName());
        int packageConfiguredPriority = getPackageConfiguredPriority(androidPackage);
        try {
            OverlayInfo nullableOverlayInfo = this.mSettings.getNullableOverlayInfo(overlayIdentifier, i);
            if (mustReinitializeOverlay(androidPackage, nullableOverlayInfo)) {
                if (nullableOverlayInfo != null) {
                    emptySet = CollectionUtils.add(emptySet, UserPackage.of(i, nullableOverlayInfo.targetPackageName));
                }
                nullableOverlayInfo = this.mSettings.init(overlayIdentifier, i, androidPackage.getOverlayTarget(), androidPackage.getOverlayTargetOverlayableName(), androidPackage.getSplits().get(0).getPath(), isPackageConfiguredMutable(androidPackage), isPackageConfiguredEnabled(androidPackage), getPackageConfiguredPriority(androidPackage), androidPackage.getOverlayCategory(), false);
            } else if (packageConfiguredPriority != nullableOverlayInfo.priority) {
                this.mSettings.setPriority(overlayIdentifier, i, packageConfiguredPriority);
                emptySet = CollectionUtils.add(emptySet, UserPackage.of(i, nullableOverlayInfo.targetPackageName));
            }
            return updateState(nullableOverlayInfo, i, i2) ? CollectionUtils.add(emptySet, UserPackage.of(i, nullableOverlayInfo.targetPackageName)) : emptySet;
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public final Set<UserPackage> reconcileSettingsForPackage(String str, int i, int i2) throws OperationFailedException {
        Set addAll = CollectionUtils.addAll(Collections.emptySet(), updateOverlaysForTarget(str, i, i2));
        PackageState packageStateForUser = this.mPackageManager.getPackageStateForUser(str, i);
        AndroidPackage androidPackage = packageStateForUser == null ? null : packageStateForUser.getAndroidPackage();
        if (androidPackage == null) {
            return onPackageRemoved(str, i);
        }
        return CollectionUtils.addAll(addAll, updatePackageOverlays(androidPackage, i, i2));
    }

    public OverlayInfo getOverlayInfo(OverlayIdentifier overlayIdentifier, int i) {
        try {
            return this.mSettings.getOverlayInfo(overlayIdentifier, i);
        } catch (OverlayManagerSettings.BadKeyException unused) {
            return null;
        }
    }

    public List<OverlayInfo> getOverlayInfosForTarget(String str, int i) {
        return this.mSettings.getOverlaysForTarget(str, i);
    }

    public Map<String, List<OverlayInfo>> getOverlaysForUser(int i) {
        return this.mSettings.getOverlaysForUser(i);
    }

    public Set<UserPackage> setEnabled(OverlayIdentifier overlayIdentifier, boolean z, int i) throws OperationFailedException {
        try {
            OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
            if (!overlayInfo.isMutable) {
                throw new OperationFailedException("cannot enable immutable overlay packages in runtime");
            }
            if (updateState(overlayInfo, i, 0) | this.mSettings.setEnabled(overlayIdentifier, i, z)) {
                return Set.of(UserPackage.of(i, overlayInfo.targetPackageName));
            }
            return Set.of();
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public Optional<UserPackage> setEnabledExclusive(OverlayIdentifier overlayIdentifier, boolean z, int i) throws OperationFailedException {
        try {
            OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
            if (!overlayInfo.isMutable) {
                throw new OperationFailedException("cannot enable immutable overlay packages in runtime");
            }
            List<OverlayInfo> overlayInfosForTarget = getOverlayInfosForTarget(overlayInfo.targetPackageName, i);
            overlayInfosForTarget.remove(overlayInfo);
            boolean z2 = false;
            for (int i2 = 0; i2 < overlayInfosForTarget.size(); i2++) {
                OverlayInfo overlayInfo2 = overlayInfosForTarget.get(i2);
                OverlayIdentifier overlayIdentifier2 = overlayInfo2.getOverlayIdentifier();
                if (overlayInfo2.isMutable && (!z || Objects.equals(overlayInfo2.category, overlayInfo.category))) {
                    z2 = z2 | this.mSettings.setEnabled(overlayIdentifier2, i, false) | updateState(overlayInfo2, i, 0);
                }
            }
            if (updateState(overlayInfo, i, 0) | this.mSettings.setEnabled(overlayIdentifier, i, true) | z2) {
                return Optional.of(UserPackage.of(i, overlayInfo.targetPackageName));
            }
            return Optional.empty();
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public Set<UserPackage> registerFabricatedOverlay(FabricatedOverlayInternal fabricatedOverlayInternal) throws OperationFailedException {
        if (FrameworkParsingPackageUtils.validateName(fabricatedOverlayInternal.overlayName, false, true) != null) {
            throw new OperationFailedException("overlay name can only consist of alphanumeric characters, '_', and '.'");
        }
        FabricatedOverlayInfo createFabricatedOverlay = this.mIdmapManager.createFabricatedOverlay(fabricatedOverlayInternal);
        if (createFabricatedOverlay == null) {
            throw new OperationFailedException("failed to create fabricated overlay");
        }
        ArraySet arraySet = new ArraySet();
        for (int i : this.mSettings.getUsers()) {
            arraySet.addAll(registerFabricatedOverlay(createFabricatedOverlay, i));
        }
        return arraySet;
    }

    public final Set<UserPackage> registerFabricatedOverlay(FabricatedOverlayInfo fabricatedOverlayInfo, int i) throws OperationFailedException {
        OverlayIdentifier overlayIdentifier = new OverlayIdentifier(fabricatedOverlayInfo.packageName, fabricatedOverlayInfo.overlayName);
        ArraySet arraySet = new ArraySet();
        OverlayInfo nullableOverlayInfo = this.mSettings.getNullableOverlayInfo(overlayIdentifier, i);
        if (nullableOverlayInfo != null && !nullableOverlayInfo.isFabricated) {
            throw new OperationFailedException("non-fabricated overlay with name '" + nullableOverlayInfo.overlayName + "' already present in '" + nullableOverlayInfo.packageName + "'");
        }
        try {
            if (mustReinitializeOverlay(fabricatedOverlayInfo, nullableOverlayInfo)) {
                if (nullableOverlayInfo != null) {
                    arraySet.add(UserPackage.of(i, nullableOverlayInfo.targetPackageName));
                }
                nullableOverlayInfo = this.mSettings.init(overlayIdentifier, i, fabricatedOverlayInfo.targetPackageName, fabricatedOverlayInfo.targetOverlayable, fabricatedOverlayInfo.path, true, false, Integer.MAX_VALUE, null, true);
            } else {
                this.mSettings.setBaseCodePath(overlayIdentifier, i, fabricatedOverlayInfo.path);
            }
            if (updateState(nullableOverlayInfo, i, 0)) {
                arraySet.add(UserPackage.of(i, nullableOverlayInfo.targetPackageName));
            }
            return arraySet;
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public Set<UserPackage> unregisterFabricatedOverlay(OverlayIdentifier overlayIdentifier) {
        ArraySet arraySet = new ArraySet();
        for (int i : this.mSettings.getUsers()) {
            arraySet.addAll(unregisterFabricatedOverlay(overlayIdentifier, i));
        }
        return arraySet;
    }

    public final Set<UserPackage> unregisterFabricatedOverlay(OverlayIdentifier overlayIdentifier, int i) {
        OverlayInfo nullableOverlayInfo = this.mSettings.getNullableOverlayInfo(overlayIdentifier, i);
        if (nullableOverlayInfo != null) {
            this.mSettings.remove(overlayIdentifier, i);
            if (nullableOverlayInfo.isEnabled()) {
                return Set.of(UserPackage.of(i, nullableOverlayInfo.targetPackageName));
            }
        }
        return Set.of();
    }

    public final void cleanStaleResourceCache() {
        Set<String> allBaseCodePaths = this.mSettings.getAllBaseCodePaths();
        for (FabricatedOverlayInfo fabricatedOverlayInfo : this.mIdmapManager.getFabricatedOverlayInfos()) {
            if (!allBaseCodePaths.contains(fabricatedOverlayInfo.path)) {
                this.mIdmapManager.deleteFabricatedOverlay(fabricatedOverlayInfo.path);
            }
        }
    }

    public final List<FabricatedOverlayInfo> getFabricatedOverlayInfos() {
        final Set<String> allBaseCodePaths = this.mSettings.getAllBaseCodePaths();
        ArrayList arrayList = new ArrayList(this.mIdmapManager.getFabricatedOverlayInfos());
        arrayList.removeIf(new Predicate() { // from class: com.android.server.om.OverlayManagerServiceImpl$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getFabricatedOverlayInfos$3;
                lambda$getFabricatedOverlayInfos$3 = OverlayManagerServiceImpl.lambda$getFabricatedOverlayInfos$3(allBaseCodePaths, (FabricatedOverlayInfo) obj);
                return lambda$getFabricatedOverlayInfos$3;
            }
        });
        return arrayList;
    }

    public static /* synthetic */ boolean lambda$getFabricatedOverlayInfos$3(Set set, FabricatedOverlayInfo fabricatedOverlayInfo) {
        return !set.contains(fabricatedOverlayInfo.path);
    }

    public final boolean isPackageConfiguredMutable(AndroidPackage androidPackage) {
        return this.mOverlayConfig.isMutable(androidPackage.getPackageName());
    }

    public final int getPackageConfiguredPriority(AndroidPackage androidPackage) {
        return this.mOverlayConfig.getPriority(androidPackage.getPackageName());
    }

    public final boolean isPackageConfiguredEnabled(AndroidPackage androidPackage) {
        return this.mOverlayConfig.isEnabled(androidPackage.getPackageName());
    }

    public Optional<UserPackage> setPriority(OverlayIdentifier overlayIdentifier, OverlayIdentifier overlayIdentifier2, int i) throws OperationFailedException {
        try {
            OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
            if (!overlayInfo.isMutable) {
                throw new OperationFailedException("cannot change priority of an immutable overlay package at runtime");
            }
            if (this.mSettings.setPriority(overlayIdentifier, overlayIdentifier2, i)) {
                return Optional.of(UserPackage.of(i, overlayInfo.targetPackageName));
            }
            return Optional.empty();
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public Set<UserPackage> setHighestPriority(OverlayIdentifier overlayIdentifier, int i) throws OperationFailedException {
        try {
            OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
            if (!overlayInfo.isMutable) {
                throw new OperationFailedException("cannot change priority of an immutable overlay package at runtime");
            }
            if (this.mSettings.setHighestPriority(overlayIdentifier, i)) {
                return Set.of(UserPackage.of(i, overlayInfo.targetPackageName));
            }
            return Set.of();
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public Optional<UserPackage> setLowestPriority(OverlayIdentifier overlayIdentifier, int i) throws OperationFailedException {
        try {
            OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
            if (!overlayInfo.isMutable) {
                throw new OperationFailedException("cannot change priority of an immutable overlay package at runtime");
            }
            if (this.mSettings.setLowestPriority(overlayIdentifier, i)) {
                return Optional.of(UserPackage.of(i, overlayInfo.targetPackageName));
            }
            return Optional.empty();
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public void dump(PrintWriter printWriter, DumpState dumpState) {
        OverlayIdentifier overlayIdentifier;
        OverlayInfo nullableOverlayInfo;
        Pair pair = (dumpState.getPackageName() == null || (nullableOverlayInfo = this.mSettings.getNullableOverlayInfo((overlayIdentifier = new OverlayIdentifier(dumpState.getPackageName(), dumpState.getOverlayName())), 0)) == null) ? null : new Pair(overlayIdentifier, nullableOverlayInfo.baseCodePath);
        this.mSettings.dump(printWriter, dumpState);
        if (dumpState.getField() == null) {
            for (Pair<OverlayIdentifier, String> pair2 : pair != null ? Set.of(pair) : this.mSettings.getAllIdentifiersAndBaseCodePaths()) {
                printWriter.println("IDMAP OF " + pair2.first);
                String dumpIdmap = this.mIdmapManager.dumpIdmap((String) pair2.second);
                if (dumpIdmap != null) {
                    printWriter.println(dumpIdmap);
                } else {
                    OverlayInfo nullableOverlayInfo2 = this.mSettings.getNullableOverlayInfo((OverlayIdentifier) pair2.first, 0);
                    printWriter.println((nullableOverlayInfo2 == null || this.mIdmapManager.idmapExists(nullableOverlayInfo2)) ? "<internal error>" : "<missing idmap>");
                }
            }
        }
        if (pair == null) {
            printWriter.println("Default overlays: " + TextUtils.join(";", this.mDefaultOverlays));
        }
        if (dumpState.getPackageName() == null) {
            this.mOverlayConfig.dump(printWriter);
        }
    }

    public String[] getDefaultOverlayPackages() {
        return this.mDefaultOverlays;
    }

    public void removeIdmapForOverlay(OverlayIdentifier overlayIdentifier, int i) throws OperationFailedException {
        try {
            removeIdmapIfPossible(this.mSettings.getOverlayInfo(overlayIdentifier, i));
        } catch (OverlayManagerSettings.BadKeyException e) {
            throw new OperationFailedException("failed to update settings", e);
        }
    }

    public OverlayPaths getEnabledOverlayPaths(String str, int i, boolean z) {
        List<OverlayInfo> overlaysForTarget = this.mSettings.getOverlaysForTarget(str, i);
        OverlayPaths.Builder builder = new OverlayPaths.Builder();
        int size = overlaysForTarget.size();
        for (int i2 = 0; i2 < size; i2++) {
            OverlayInfo overlayInfo = overlaysForTarget.get(i2);
            if (overlayInfo.isEnabled() && (z || overlayInfo.isMutable)) {
                if (overlayInfo.isFabricated()) {
                    builder.addNonApkPath(overlayInfo.baseCodePath);
                } else {
                    builder.addApkPath(overlayInfo.baseCodePath);
                }
            }
        }
        return builder.build();
    }

    public final boolean updateState(CriticalOverlayInfo criticalOverlayInfo, int i, int i2) throws OverlayManagerSettings.BadKeyException {
        int i3;
        OverlayIdentifier overlayIdentifier = criticalOverlayInfo.getOverlayIdentifier();
        PackageState packageStateForUser = this.mPackageManager.getPackageStateForUser(criticalOverlayInfo.getTargetPackageName(), i);
        AndroidPackage androidPackage = packageStateForUser == null ? null : packageStateForUser.getAndroidPackage();
        PackageState packageStateForUser2 = this.mPackageManager.getPackageStateForUser(criticalOverlayInfo.getPackageName(), i);
        AndroidPackage androidPackage2 = packageStateForUser2 != null ? packageStateForUser2.getAndroidPackage() : null;
        if (androidPackage2 == null) {
            removeIdmapIfPossible(this.mSettings.getOverlayInfo(overlayIdentifier, i));
            return this.mSettings.remove(overlayIdentifier, i);
        }
        boolean category = this.mSettings.setCategory(overlayIdentifier, i, androidPackage2.getOverlayCategory()) | false;
        if (!criticalOverlayInfo.isFabricated()) {
            category |= this.mSettings.setBaseCodePath(overlayIdentifier, i, androidPackage2.getSplits().get(0).getPath());
        }
        OverlayInfo overlayInfo = this.mSettings.getOverlayInfo(overlayIdentifier, i);
        if (androidPackage == null || (PackageManagerShellCommandDataLoader.PACKAGE.equals(criticalOverlayInfo.getTargetPackageName()) && !isPackageConfiguredMutable(androidPackage2))) {
            i3 = 0;
        } else {
            int createIdmap = this.mIdmapManager.createIdmap(androidPackage, packageStateForUser2, androidPackage2, overlayInfo.baseCodePath, overlayIdentifier.getOverlayName(), i);
            category |= (createIdmap & 2) != 0;
            i3 = createIdmap;
        }
        int state = this.mSettings.getState(overlayIdentifier, i);
        int calculateNewState = calculateNewState(overlayInfo, androidPackage, i, i2, i3);
        return state != calculateNewState ? category | this.mSettings.setState(overlayIdentifier, i, calculateNewState) : category;
    }

    public final int calculateNewState(OverlayInfo overlayInfo, AndroidPackage androidPackage, int i, int i2, int i3) throws OverlayManagerSettings.BadKeyException {
        if ((i2 & 1) != 0) {
            return 4;
        }
        if ((i2 & 2) != 0) {
            return 5;
        }
        if ((i2 & 4) != 0) {
            return 7;
        }
        if (androidPackage == null) {
            return 0;
        }
        if ((i3 & 1) != 0 || this.mIdmapManager.idmapExists(overlayInfo)) {
            return this.mSettings.getEnabled(overlayInfo.getOverlayIdentifier(), i) ? 3 : 2;
        }
        return 1;
    }

    public final void removeIdmapIfPossible(OverlayInfo overlayInfo) {
        if (this.mIdmapManager.idmapExists(overlayInfo)) {
            for (int i : this.mSettings.getUsers()) {
                try {
                    OverlayInfo overlayInfo2 = this.mSettings.getOverlayInfo(overlayInfo.getOverlayIdentifier(), i);
                    if (overlayInfo2 != null && overlayInfo2.isEnabled()) {
                        return;
                    }
                } catch (OverlayManagerSettings.BadKeyException unused) {
                }
            }
            this.mIdmapManager.removeIdmap(overlayInfo, overlayInfo.userId);
        }
    }

    /* renamed from: com.android.server.om.OverlayManagerServiceImpl$OperationFailedException */
    /* loaded from: classes2.dex */
    public static final class OperationFailedException extends Exception {
        public OperationFailedException(String str) {
            super(str);
        }

        public OperationFailedException(String str, Throwable th) {
            super(str, th);
        }
    }
}
