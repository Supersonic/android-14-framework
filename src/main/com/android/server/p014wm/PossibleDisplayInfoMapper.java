package com.android.server.p014wm;

import android.hardware.display.DisplayManagerInternal;
import android.util.ArraySet;
import android.util.SparseArray;
import android.view.DisplayInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.wm.PossibleDisplayInfoMapper */
/* loaded from: classes2.dex */
public class PossibleDisplayInfoMapper {
    public final SparseArray<Set<DisplayInfo>> mDisplayInfos = new SparseArray<>();
    public final DisplayManagerInternal mDisplayManagerInternal;

    public PossibleDisplayInfoMapper(DisplayManagerInternal displayManagerInternal) {
        this.mDisplayManagerInternal = displayManagerInternal;
    }

    public List<DisplayInfo> getPossibleDisplayInfos(int i) {
        updatePossibleDisplayInfos(i);
        if (!this.mDisplayInfos.contains(i)) {
            return new ArrayList();
        }
        return List.copyOf(this.mDisplayInfos.get(i));
    }

    public void updatePossibleDisplayInfos(int i) {
        updateDisplayInfos(this.mDisplayManagerInternal.getPossibleDisplayInfo(i));
    }

    public void removePossibleDisplayInfos(int i) {
        this.mDisplayInfos.remove(i);
    }

    public final void updateDisplayInfos(Set<DisplayInfo> set) {
        this.mDisplayInfos.clear();
        for (DisplayInfo displayInfo : set) {
            Set<DisplayInfo> set2 = this.mDisplayInfos.get(displayInfo.displayId, new ArraySet());
            set2.add(displayInfo);
            this.mDisplayInfos.put(displayInfo.displayId, set2);
        }
    }
}
