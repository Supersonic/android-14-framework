package com.android.server.location.contexthub;

import android.hardware.location.NanoAppInstanceInfo;
import android.hardware.location.NanoAppState;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class NanoAppStateManager {
    public final HashMap<Integer, NanoAppInstanceInfo> mNanoAppHash = new HashMap<>();
    public int mNextHandle = 0;

    public synchronized NanoAppInstanceInfo getNanoAppInstanceInfo(int i) {
        return this.mNanoAppHash.get(Integer.valueOf(i));
    }

    public synchronized void foreachNanoAppInstanceInfo(Consumer<NanoAppInstanceInfo> consumer) {
        for (NanoAppInstanceInfo nanoAppInstanceInfo : this.mNanoAppHash.values()) {
            consumer.accept(nanoAppInstanceInfo);
        }
    }

    public synchronized int getNanoAppHandle(int i, long j) {
        for (NanoAppInstanceInfo nanoAppInstanceInfo : this.mNanoAppHash.values()) {
            if (nanoAppInstanceInfo.getContexthubId() == i && nanoAppInstanceInfo.getAppId() == j) {
                return nanoAppInstanceInfo.getHandle();
            }
        }
        return -1;
    }

    public synchronized void addNanoAppInstance(int i, long j, int i2) {
        removeNanoAppInstance(i, j);
        if (this.mNanoAppHash.size() == Integer.MAX_VALUE) {
            Log.e("NanoAppStateManager", "Error adding nanoapp instance: max limit exceeded");
            return;
        }
        int i3 = this.mNextHandle;
        int i4 = 0;
        int i5 = 0;
        while (true) {
            if (i5 > Integer.MAX_VALUE) {
                break;
            } else if (this.mNanoAppHash.containsKey(Integer.valueOf(i3))) {
                i3 = i3 == Integer.MAX_VALUE ? 0 : i3 + 1;
                i5++;
            } else {
                this.mNanoAppHash.put(Integer.valueOf(i3), new NanoAppInstanceInfo(i3, j, i2, i));
                if (i3 != Integer.MAX_VALUE) {
                    i4 = i3 + 1;
                }
                this.mNextHandle = i4;
            }
        }
        Log.v("NanoAppStateManager", "Added app instance with handle " + i3 + " to hub " + i + ": ID=0x" + Long.toHexString(j) + ", version=0x" + Integer.toHexString(i2));
    }

    public synchronized void removeNanoAppInstance(int i, long j) {
        this.mNanoAppHash.remove(Integer.valueOf(getNanoAppHandle(i, j)));
    }

    public synchronized void updateCache(int i, List<NanoAppState> list) {
        HashSet hashSet = new HashSet();
        for (NanoAppState nanoAppState : list) {
            handleQueryAppEntry(i, nanoAppState.getNanoAppId(), (int) nanoAppState.getNanoAppVersion());
            hashSet.add(Long.valueOf(nanoAppState.getNanoAppId()));
        }
        Iterator<NanoAppInstanceInfo> it = this.mNanoAppHash.values().iterator();
        while (it.hasNext()) {
            NanoAppInstanceInfo next = it.next();
            if (next.getContexthubId() == i && !hashSet.contains(Long.valueOf(next.getAppId()))) {
                it.remove();
            }
        }
    }

    public final void handleQueryAppEntry(int i, long j, int i2) {
        int nanoAppHandle = getNanoAppHandle(i, j);
        if (nanoAppHandle == -1) {
            addNanoAppInstance(i, j, i2);
        } else if (this.mNanoAppHash.get(Integer.valueOf(nanoAppHandle)).getAppVersion() != i2) {
            this.mNanoAppHash.put(Integer.valueOf(nanoAppHandle), new NanoAppInstanceInfo(nanoAppHandle, j, i2, i));
            Log.v("NanoAppStateManager", "Updated app instance with handle " + nanoAppHandle + " at hub " + i + ": ID=0x" + Long.toHexString(j) + ", version=0x" + Integer.toHexString(i2));
        }
    }
}
