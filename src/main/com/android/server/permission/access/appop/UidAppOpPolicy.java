package com.android.server.permission.access.appop;

import android.app.AppOpsManager;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.server.permission.access.AccessUri;
import com.android.server.permission.access.AppOpUri;
import com.android.server.permission.access.GetStateScope;
import com.android.server.permission.access.MutateStateScope;
import com.android.server.permission.access.UidUri;
import com.android.server.permission.access.UserState;
import com.android.server.permission.access.WritableState;
import com.android.server.permission.access.collection.IndexedListSet;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: UidAppOpPolicy.kt */
/* loaded from: classes2.dex */
public final class UidAppOpPolicy extends BaseAppOpPolicy {
    public volatile IndexedListSet<OnAppOpModeChangedListener> onAppOpModeChangedListeners;
    public final Object onAppOpModeChangedListenersLock;

    /* compiled from: UidAppOpPolicy.kt */
    /* loaded from: classes2.dex */
    public static abstract class OnAppOpModeChangedListener {
        public abstract void onAppOpModeChanged(int i, int i2, String str, int i3, int i4);

        public abstract void onStateMutated();
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public String getSubjectScheme() {
        return "uid";
    }

    public UidAppOpPolicy() {
        super(new UidAppOpPersistence());
        this.onAppOpModeChangedListeners = new IndexedListSet<>();
        this.onAppOpModeChangedListenersLock = new Object();
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public int getDecision(GetStateScope getStateScope, AccessUri accessUri, AccessUri accessUri2) {
        Intrinsics.checkNotNull(accessUri, "null cannot be cast to non-null type com.android.server.permission.access.UidUri");
        UidUri uidUri = (UidUri) accessUri;
        Intrinsics.checkNotNull(accessUri2, "null cannot be cast to non-null type com.android.server.permission.access.AppOpUri");
        return getAppOpMode(getStateScope, uidUri.getAppId(), uidUri.getUserId(), ((AppOpUri) accessUri2).getAppOpName());
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void setDecision(MutateStateScope mutateStateScope, AccessUri accessUri, AccessUri accessUri2, int i) {
        Intrinsics.checkNotNull(accessUri, "null cannot be cast to non-null type com.android.server.permission.access.UidUri");
        UidUri uidUri = (UidUri) accessUri;
        Intrinsics.checkNotNull(accessUri2, "null cannot be cast to non-null type com.android.server.permission.access.AppOpUri");
        setAppOpMode(mutateStateScope, uidUri.getAppId(), uidUri.getUserId(), ((AppOpUri) accessUri2).getAppOpName(), i);
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
    public void onAppIdRemoved(MutateStateScope mutateStateScope, int i) {
        SparseArray<UserState> userStates = mutateStateScope.getNewState().getUserStates();
        int size = userStates.size();
        for (int i2 = 0; i2 < size; i2++) {
            userStates.keyAt(i2);
            UserState valueAt = userStates.valueAt(i2);
            valueAt.getUidAppOpModes().remove(i);
            WritableState.requestWrite$default(valueAt, false, 1, null);
        }
    }

    public final ArrayMap<String, Integer> getAppOpModes(GetStateScope getStateScope, int i, int i2) {
        return getStateScope.getState().getUserStates().get(i2).getUidAppOpModes().get(i);
    }

    public final boolean removeAppOpModes(MutateStateScope mutateStateScope, int i, int i2) {
        UserState userState = mutateStateScope.getNewState().getUserStates().get(i2);
        boolean z = userState.getUidAppOpModes().removeReturnOld(i) != null;
        if (z) {
            WritableState.requestWrite$default(userState, false, 1, null);
        }
        return z;
    }

    public final int getAppOpMode(GetStateScope getStateScope, int i, int i2, String str) {
        int indexOfKey;
        ArrayMap<String, Integer> arrayMap = getStateScope.getState().getUserStates().get(i2).getUidAppOpModes().get(i);
        Integer valueOf = Integer.valueOf(AppOpsManager.opToDefaultMode(str));
        if (arrayMap != null && (indexOfKey = arrayMap.indexOfKey(str)) >= 0) {
            valueOf = arrayMap.valueAt(indexOfKey);
        }
        return valueOf.intValue();
    }

    public final boolean setAppOpMode(MutateStateScope mutateStateScope, int i, int i2, String str, int i3) {
        int indexOfKey;
        UserState userState = mutateStateScope.getNewState().getUserStates().get(i2);
        SparseArray<ArrayMap<String, Integer>> uidAppOpModes = userState.getUidAppOpModes();
        ArrayMap<String, Integer> arrayMap = uidAppOpModes.get(i);
        int opToDefaultMode = AppOpsManager.opToDefaultMode(str);
        Integer valueOf = Integer.valueOf(opToDefaultMode);
        if (arrayMap != null && (indexOfKey = arrayMap.indexOfKey(str)) >= 0) {
            valueOf = arrayMap.valueAt(indexOfKey);
        }
        int intValue = valueOf.intValue();
        if (intValue == i3) {
            return false;
        }
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            uidAppOpModes.set(i, arrayMap);
        }
        Integer valueOf2 = Integer.valueOf(i3);
        Integer valueOf3 = Integer.valueOf(opToDefaultMode);
        int indexOfKey2 = arrayMap.indexOfKey(str);
        if (indexOfKey2 >= 0) {
            if (!Intrinsics.areEqual(valueOf2, arrayMap.valueAt(indexOfKey2))) {
                if (Intrinsics.areEqual(valueOf2, valueOf3)) {
                    arrayMap.removeAt(indexOfKey2);
                } else {
                    arrayMap.setValueAt(indexOfKey2, valueOf2);
                }
            }
        } else if (!Intrinsics.areEqual(valueOf2, valueOf3)) {
            arrayMap.put(str, valueOf2);
        }
        if (arrayMap.isEmpty()) {
            uidAppOpModes.remove(i);
        }
        WritableState.requestWrite$default(userState, false, 1, null);
        IndexedListSet<OnAppOpModeChangedListener> indexedListSet = this.onAppOpModeChangedListeners;
        int size = indexedListSet.size();
        for (int i4 = 0; i4 < size; i4++) {
            indexedListSet.elementAt(i4).onAppOpModeChanged(i, i2, str, intValue, i3);
        }
        return true;
    }
}
