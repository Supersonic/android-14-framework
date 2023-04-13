package com.android.server.hdmi;

import android.util.FastImmutableArraySet;
import android.util.SparseArray;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes.dex */
public final class HdmiCecMessageCache {
    public static final FastImmutableArraySet<Integer> CACHEABLE_OPCODES = new FastImmutableArraySet<>(new Integer[]{71, 132, Integer.valueOf((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED), Integer.valueOf((int) FrameworkStatsLog.f419x663f9746)});
    public final SparseArray<SparseArray<HdmiCecMessage>> mCache = new SparseArray<>();

    public HdmiCecMessage getMessage(int i, int i2) {
        SparseArray<HdmiCecMessage> sparseArray = this.mCache.get(i);
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i2);
    }

    public void flushMessagesFrom(int i) {
        this.mCache.remove(i);
    }

    public void flushAll() {
        this.mCache.clear();
    }

    public void cacheMessage(HdmiCecMessage hdmiCecMessage) {
        int opcode = hdmiCecMessage.getOpcode();
        if (isCacheable(opcode)) {
            int source = hdmiCecMessage.getSource();
            SparseArray<HdmiCecMessage> sparseArray = this.mCache.get(source);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mCache.put(source, sparseArray);
            }
            sparseArray.put(opcode, hdmiCecMessage);
        }
    }

    public final boolean isCacheable(int i) {
        return CACHEABLE_OPCODES.contains(Integer.valueOf(i));
    }
}
