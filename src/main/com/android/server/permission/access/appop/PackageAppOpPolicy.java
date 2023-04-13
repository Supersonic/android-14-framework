package com.android.server.permission.access.appop;

import android.app.AppOpsManager;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.server.permission.access.AccessUri;
import com.android.server.permission.access.AppOpUri;
import com.android.server.permission.access.GetStateScope;
import com.android.server.permission.access.MutateStateScope;
import com.android.server.permission.access.PackageUri;
import com.android.server.permission.access.UserState;
import com.android.server.permission.access.WritableState;
import com.android.server.permission.access.collection.IndexedListSet;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: PackageAppOpPolicy.kt */
/* loaded from: classes2.dex */
public final class PackageAppOpPolicy extends BaseAppOpPolicy {
    public volatile IndexedListSet<OnAppOpModeChangedListener> onAppOpModeChangedListeners;
    public final Object onAppOpModeChangedListenersLock;

    /* compiled from: PackageAppOpPolicy.kt */
    /* loaded from: classes2.dex */
    public static abstract class OnAppOpModeChangedListener {
        public abstract void onAppOpModeChanged(String str, int i, String str2, int i2, int i3);

        public abstract void onStateMutated();
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public String getSubjectScheme() {
        return "package";
    }

    public PackageAppOpPolicy() {
        super(new PackageAppOpPersistence());
        this.onAppOpModeChangedListeners = new IndexedListSet<>();
        this.onAppOpModeChangedListenersLock = new Object();
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public int getDecision(GetStateScope getStateScope, AccessUri accessUri, AccessUri accessUri2) {
        Intrinsics.checkNotNull(accessUri, "null cannot be cast to non-null type com.android.server.permission.access.PackageUri");
        PackageUri packageUri = (PackageUri) accessUri;
        Intrinsics.checkNotNull(accessUri2, "null cannot be cast to non-null type com.android.server.permission.access.AppOpUri");
        return getAppOpMode(getStateScope, packageUri.getPackageName(), packageUri.getUserId(), ((AppOpUri) accessUri2).getAppOpName());
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void setDecision(MutateStateScope mutateStateScope, AccessUri accessUri, AccessUri accessUri2, int i) {
        Intrinsics.checkNotNull(accessUri, "null cannot be cast to non-null type com.android.server.permission.access.PackageUri");
        PackageUri packageUri = (PackageUri) accessUri;
        Intrinsics.checkNotNull(accessUri2, "null cannot be cast to non-null type com.android.server.permission.access.AppOpUri");
        setAppOpMode(mutateStateScope, packageUri.getPackageName(), packageUri.getUserId(), ((AppOpUri) accessUri2).getAppOpName(), i);
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void onStateMutated(GetStateScope getStateScope) {
        IndexedListSet<OnAppOpModeChangedListener> indexedListSet = this.onAppOpModeChangedListeners;
        int size = indexedListSet.size();
        for (int i = 0; i < size; i++) {
            indexedListSet.elementAt(i).onStateMutated();
        }
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void onPackageRemoved(MutateStateScope mutateStateScope, String str, int i) {
        SparseArray<UserState> userStates = mutateStateScope.getNewState().getUserStates();
        int size = userStates.size();
        for (int i2 = 0; i2 < size; i2++) {
            userStates.keyAt(i2);
            UserState valueAt = userStates.valueAt(i2);
            valueAt.getPackageAppOpModes().remove(str);
            WritableState.requestWrite$default(valueAt, false, 1, null);
        }
    }

    public final ArrayMap<String, Integer> getAppOpModes(GetStateScope getStateScope, String str, int i) {
        return getStateScope.getState().getUserStates().get(i).getPackageAppOpModes().get(str);
    }

    public final boolean removeAppOpModes(MutateStateScope mutateStateScope, String str, int i) {
        UserState userState = mutateStateScope.getNewState().getUserStates().get(i);
        boolean z = userState.getPackageAppOpModes().remove(str) != null;
        if (z) {
            WritableState.requestWrite$default(userState, false, 1, null);
        }
        return z;
    }

    public final int getAppOpMode(GetStateScope getStateScope, String str, int i, String str2) {
        int indexOfKey;
        ArrayMap<String, Integer> arrayMap = getStateScope.getState().getUserStates().get(i).getPackageAppOpModes().get(str);
        Integer valueOf = Integer.valueOf(AppOpsManager.opToDefaultMode(str2));
        if (arrayMap != null && (indexOfKey = arrayMap.indexOfKey(str2)) >= 0) {
            valueOf = arrayMap.valueAt(indexOfKey);
        }
        return valueOf.intValue();
    }

    public final boolean setAppOpMode(MutateStateScope mutateStateScope, String str, int i, String str2, int i2) {
        int indexOfKey;
        UserState userState = mutateStateScope.getNewState().getUserStates().get(i);
        ArrayMap<String, ArrayMap<String, Integer>> packageAppOpModes = userState.getPackageAppOpModes();
        ArrayMap<String, Integer> arrayMap = packageAppOpModes.get(str);
        int opToDefaultMode = AppOpsManager.opToDefaultMode(str2);
        Integer valueOf = Integer.valueOf(opToDefaultMode);
        if (arrayMap != null && (indexOfKey = arrayMap.indexOfKey(str2)) >= 0) {
            valueOf = arrayMap.valueAt(indexOfKey);
        }
        int intValue = valueOf.intValue();
        if (intValue == i2) {
            return false;
        }
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            packageAppOpModes.put(str, arrayMap);
        }
        Integer valueOf2 = Integer.valueOf(i2);
        Integer valueOf3 = Integer.valueOf(opToDefaultMode);
        int indexOfKey2 = arrayMap.indexOfKey(str2);
        if (indexOfKey2 >= 0) {
            if (!Intrinsics.areEqual(valueOf2, arrayMap.valueAt(indexOfKey2))) {
                if (Intrinsics.areEqual(valueOf2, valueOf3)) {
                    arrayMap.removeAt(indexOfKey2);
                } else {
                    arrayMap.setValueAt(indexOfKey2, valueOf2);
                }
            }
        } else if (!Intrinsics.areEqual(valueOf2, valueOf3)) {
            arrayMap.put(str2, valueOf2);
        }
        if (arrayMap.isEmpty()) {
            packageAppOpModes.remove(str);
        }
        WritableState.requestWrite$default(userState, false, 1, null);
        IndexedListSet<OnAppOpModeChangedListener> indexedListSet = this.onAppOpModeChangedListeners;
        int size = indexedListSet.size();
        for (int i3 = 0; i3 < size; i3++) {
            indexedListSet.elementAt(i3).onAppOpModeChanged(str, i, str2, intValue, i2);
        }
        return true;
    }
}
