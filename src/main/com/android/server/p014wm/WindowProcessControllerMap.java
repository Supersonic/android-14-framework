package com.android.server.p014wm;

import android.util.ArraySet;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.Map;
/* renamed from: com.android.server.wm.WindowProcessControllerMap */
/* loaded from: classes2.dex */
public final class WindowProcessControllerMap {
    public final SparseArray<WindowProcessController> mPidMap = new SparseArray<>();
    public final Map<Integer, ArraySet<WindowProcessController>> mUidMap = new HashMap();

    public WindowProcessController getProcess(int i) {
        return this.mPidMap.get(i);
    }

    public ArraySet<WindowProcessController> getProcesses(int i) {
        return this.mUidMap.get(Integer.valueOf(i));
    }

    public SparseArray<WindowProcessController> getPidMap() {
        return this.mPidMap;
    }

    public void put(int i, WindowProcessController windowProcessController) {
        WindowProcessController windowProcessController2 = this.mPidMap.get(i);
        if (windowProcessController2 != null) {
            removeProcessFromUidMap(windowProcessController2);
        }
        this.mPidMap.put(i, windowProcessController);
        int i2 = windowProcessController.mUid;
        ArraySet<WindowProcessController> orDefault = this.mUidMap.getOrDefault(Integer.valueOf(i2), new ArraySet<>());
        orDefault.add(windowProcessController);
        this.mUidMap.put(Integer.valueOf(i2), orDefault);
    }

    public void remove(int i) {
        WindowProcessController windowProcessController = this.mPidMap.get(i);
        if (windowProcessController != null) {
            this.mPidMap.remove(i);
            removeProcessFromUidMap(windowProcessController);
        }
    }

    public final void removeProcessFromUidMap(WindowProcessController windowProcessController) {
        if (windowProcessController == null) {
            return;
        }
        int i = windowProcessController.mUid;
        ArraySet<WindowProcessController> arraySet = this.mUidMap.get(Integer.valueOf(i));
        if (arraySet != null) {
            arraySet.remove(windowProcessController);
            if (arraySet.isEmpty()) {
                this.mUidMap.remove(Integer.valueOf(i));
            }
        }
    }
}
