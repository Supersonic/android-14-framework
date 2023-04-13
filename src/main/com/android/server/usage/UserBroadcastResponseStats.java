package com.android.server.usage;

import android.app.usage.BroadcastResponseStats;
import android.util.ArrayMap;
import com.android.internal.util.IndentingPrintWriter;
import java.util.List;
/* loaded from: classes2.dex */
public class UserBroadcastResponseStats {
    public ArrayMap<BroadcastEvent, BroadcastResponseStats> mResponseStats = new ArrayMap<>();

    public BroadcastResponseStats getOrCreateBroadcastResponseStats(BroadcastEvent broadcastEvent) {
        BroadcastResponseStats broadcastResponseStats = this.mResponseStats.get(broadcastEvent);
        if (broadcastResponseStats == null) {
            BroadcastResponseStats broadcastResponseStats2 = new BroadcastResponseStats(broadcastEvent.getTargetPackage(), broadcastEvent.getIdForResponseEvent());
            this.mResponseStats.put(broadcastEvent, broadcastResponseStats2);
            return broadcastResponseStats2;
        }
        return broadcastResponseStats;
    }

    public void populateAllBroadcastResponseStats(List<BroadcastResponseStats> list, String str, long j) {
        for (int size = this.mResponseStats.size() - 1; size >= 0; size--) {
            BroadcastEvent keyAt = this.mResponseStats.keyAt(size);
            if ((j == 0 || j == keyAt.getIdForResponseEvent()) && (str == null || str.equals(keyAt.getTargetPackage()))) {
                list.add(this.mResponseStats.valueAt(size));
            }
        }
    }

    public void clearBroadcastResponseStats(String str, long j) {
        for (int size = this.mResponseStats.size() - 1; size >= 0; size--) {
            BroadcastEvent keyAt = this.mResponseStats.keyAt(size);
            if ((j == 0 || j == keyAt.getIdForResponseEvent()) && (str == null || str.equals(keyAt.getTargetPackage()))) {
                this.mResponseStats.removeAt(size);
            }
        }
    }

    public void onPackageRemoved(String str) {
        for (int size = this.mResponseStats.size() - 1; size >= 0; size--) {
            if (this.mResponseStats.keyAt(size).getTargetPackage().equals(str)) {
                this.mResponseStats.removeAt(size);
            }
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        for (int i = 0; i < this.mResponseStats.size(); i++) {
            indentingPrintWriter.print(this.mResponseStats.keyAt(i));
            indentingPrintWriter.print(" -> ");
            indentingPrintWriter.println(this.mResponseStats.valueAt(i));
        }
    }
}
