package com.android.server.p011pm;

import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.mutate.PackageStateMutator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.DistractingPackageHelper */
/* loaded from: classes2.dex */
public final class DistractingPackageHelper {
    public final BroadcastHelper mBroadcastHelper;
    public final PackageManagerServiceInjector mInjector;
    public final PackageManagerService mPm;
    public final SuspendPackageHelper mSuspendPackageHelper;

    public DistractingPackageHelper(PackageManagerService packageManagerService, PackageManagerServiceInjector packageManagerServiceInjector, BroadcastHelper broadcastHelper, SuspendPackageHelper suspendPackageHelper) {
        this.mPm = packageManagerService;
        this.mInjector = packageManagerServiceInjector;
        this.mBroadcastHelper = broadcastHelper;
        this.mSuspendPackageHelper = suspendPackageHelper;
    }

    public String[] setDistractingPackageRestrictionsAsUser(Computer computer, String[] strArr, final int i, final int i2, int i3) {
        if (ArrayUtils.isEmpty(strArr)) {
            return strArr;
        }
        if (i != 0 && !this.mSuspendPackageHelper.isSuspendAllowedForUser(computer, i2, i3)) {
            Slog.w("PackageManager", "Cannot restrict packages due to restrictions on user " + i2);
            return strArr;
        }
        ArrayList arrayList = new ArrayList(strArr.length);
        IntArray intArray = new IntArray(strArr.length);
        ArrayList arrayList2 = new ArrayList(strArr.length);
        final ArraySet arraySet = new ArraySet();
        boolean[] canSuspendPackageForUser = i != 0 ? this.mSuspendPackageHelper.canSuspendPackageForUser(computer, strArr, i2, i3) : null;
        for (int i4 = 0; i4 < strArr.length; i4++) {
            String str = strArr[i4];
            PackageStateInternal packageStateForInstalledAndFiltered = computer.getPackageStateForInstalledAndFiltered(str, i3, i2);
            if (packageStateForInstalledAndFiltered == null) {
                Slog.w("PackageManager", "Could not find package setting for package: " + str + ". Skipping...");
                arrayList2.add(str);
            } else if (canSuspendPackageForUser != null && !canSuspendPackageForUser[i4]) {
                arrayList2.add(str);
            } else if (i != packageStateForInstalledAndFiltered.getUserStateOrDefault(i2).getDistractionFlags()) {
                arrayList.add(str);
                intArray.add(UserHandle.getUid(i2, packageStateForInstalledAndFiltered.getAppId()));
                arraySet.add(str);
            }
        }
        this.mPm.commitPackageStateMutation(null, new Consumer() { // from class: com.android.server.pm.DistractingPackageHelper$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DistractingPackageHelper.lambda$setDistractingPackageRestrictionsAsUser$0(arraySet, i2, i, (PackageStateMutator) obj);
            }
        });
        if (!arrayList.isEmpty()) {
            sendDistractingPackagesChanged((String[]) arrayList.toArray(new String[arrayList.size()]), intArray.toArray(), i2, i);
            this.mPm.scheduleWritePackageRestrictions(i2);
        }
        return (String[]) arrayList2.toArray(new String[0]);
    }

    public static /* synthetic */ void lambda$setDistractingPackageRestrictionsAsUser$0(ArraySet arraySet, int i, int i2, PackageStateMutator packageStateMutator) {
        int size = arraySet.size();
        for (int i3 = 0; i3 < size; i3++) {
            packageStateMutator.forPackage((String) arraySet.valueAt(i3)).userState(i).setDistractionFlags(i2);
        }
    }

    public void removeDistractingPackageRestrictions(Computer computer, String[] strArr, final int i) {
        if (ArrayUtils.isEmpty(strArr)) {
            return;
        }
        final ArrayList arrayList = new ArrayList(strArr.length);
        IntArray intArray = new IntArray(strArr.length);
        for (String str : strArr) {
            PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
            if (packageStateInternal != null && packageStateInternal.getUserStateOrDefault(i).getDistractionFlags() != 0) {
                arrayList.add(packageStateInternal.getPackageName());
                intArray.add(UserHandle.getUid(i, packageStateInternal.getAppId()));
            }
        }
        this.mPm.commitPackageStateMutation(null, new Consumer() { // from class: com.android.server.pm.DistractingPackageHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DistractingPackageHelper.lambda$removeDistractingPackageRestrictions$1(arrayList, i, (PackageStateMutator) obj);
            }
        });
        if (arrayList.isEmpty()) {
            return;
        }
        sendDistractingPackagesChanged((String[]) arrayList.toArray(new String[arrayList.size()]), intArray.toArray(), i, 0);
        this.mPm.scheduleWritePackageRestrictions(i);
    }

    public static /* synthetic */ void lambda$removeDistractingPackageRestrictions$1(List list, int i, PackageStateMutator packageStateMutator) {
        for (int i2 = 0; i2 < list.size(); i2++) {
            packageStateMutator.forPackage((String) list.get(i2)).userState(i).setDistractionFlags(0);
        }
    }

    public void sendDistractingPackagesChanged(String[] strArr, int[] iArr, final int i, int i2) {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("android.intent.extra.changed_package_list", strArr);
        bundle.putIntArray("android.intent.extra.changed_uid_list", iArr);
        bundle.putInt("android.intent.extra.distraction_restrictions", i2);
        this.mInjector.getHandler().post(new Runnable() { // from class: com.android.server.pm.DistractingPackageHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DistractingPackageHelper.this.lambda$sendDistractingPackagesChanged$3(bundle, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendDistractingPackagesChanged$3(Bundle bundle, int i) {
        this.mBroadcastHelper.sendPackageBroadcast("android.intent.action.DISTRACTING_PACKAGES_CHANGED", null, bundle, 1073741824, null, null, new int[]{i}, null, null, new BiFunction() { // from class: com.android.server.pm.DistractingPackageHelper$$ExternalSyntheticLambda3
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                Bundle lambda$sendDistractingPackagesChanged$2;
                lambda$sendDistractingPackagesChanged$2 = DistractingPackageHelper.this.lambda$sendDistractingPackagesChanged$2((Integer) obj, (Bundle) obj2);
                return lambda$sendDistractingPackagesChanged$2;
            }
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Bundle lambda$sendDistractingPackagesChanged$2(Integer num, Bundle bundle) {
        return BroadcastHelper.filterExtrasChangedPackageList(this.mPm.snapshotComputer(), num.intValue(), bundle);
    }
}
