package com.android.server.p011pm;

import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Pair;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import java.util.concurrent.TimeUnit;
/* renamed from: com.android.server.pm.SilentUpdatePolicy */
/* loaded from: classes2.dex */
public class SilentUpdatePolicy {
    public static final long SILENT_UPDATE_THROTTLE_TIME_MS = TimeUnit.SECONDS.toMillis(30);
    @GuardedBy({"mSilentUpdateInfos"})
    public String mAllowUnlimitedSilentUpdatesInstaller;
    @GuardedBy({"mSilentUpdateInfos"})
    public final ArrayMap<Pair<String, String>, Long> mSilentUpdateInfos = new ArrayMap<>();
    @GuardedBy({"mSilentUpdateInfos"})
    public long mSilentUpdateThrottleTimeMs = SILENT_UPDATE_THROTTLE_TIME_MS;

    public boolean isSilentUpdateAllowed(String str, String str2) {
        long j;
        if (str == null) {
            return true;
        }
        long timestampMs = getTimestampMs(str, str2);
        synchronized (this.mSilentUpdateInfos) {
            j = this.mSilentUpdateThrottleTimeMs;
        }
        return SystemClock.uptimeMillis() - timestampMs > j;
    }

    public void track(String str, String str2) {
        if (str == null) {
            return;
        }
        synchronized (this.mSilentUpdateInfos) {
            String str3 = this.mAllowUnlimitedSilentUpdatesInstaller;
            if (str3 == null || !str3.equals(str)) {
                long uptimeMillis = SystemClock.uptimeMillis();
                pruneLocked(uptimeMillis);
                this.mSilentUpdateInfos.put(Pair.create(str, str2), Long.valueOf(uptimeMillis));
            }
        }
    }

    public void setAllowUnlimitedSilentUpdates(String str) {
        synchronized (this.mSilentUpdateInfos) {
            if (str == null) {
                this.mSilentUpdateInfos.clear();
            }
            this.mAllowUnlimitedSilentUpdatesInstaller = str;
        }
    }

    public void setSilentUpdatesThrottleTime(long j) {
        long j2;
        synchronized (this.mSilentUpdateInfos) {
            if (j >= 0) {
                j2 = TimeUnit.SECONDS.toMillis(j);
            } else {
                j2 = SILENT_UPDATE_THROTTLE_TIME_MS;
            }
            this.mSilentUpdateThrottleTimeMs = j2;
        }
    }

    public final void pruneLocked(long j) {
        for (int size = this.mSilentUpdateInfos.size() - 1; size >= 0; size--) {
            if (j - this.mSilentUpdateInfos.valueAt(size).longValue() > this.mSilentUpdateThrottleTimeMs) {
                this.mSilentUpdateInfos.removeAt(size);
            }
        }
    }

    public final long getTimestampMs(String str, String str2) {
        Long l;
        Pair create = Pair.create(str, str2);
        synchronized (this.mSilentUpdateInfos) {
            l = this.mSilentUpdateInfos.get(create);
        }
        if (l != null) {
            return l.longValue();
        }
        return -1L;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mSilentUpdateInfos) {
            if (this.mSilentUpdateInfos.isEmpty()) {
                return;
            }
            indentingPrintWriter.println("Last silent updated Infos:");
            indentingPrintWriter.increaseIndent();
            int size = this.mSilentUpdateInfos.size();
            for (int i = 0; i < size; i++) {
                Pair<String, String> keyAt = this.mSilentUpdateInfos.keyAt(i);
                if (keyAt != null) {
                    indentingPrintWriter.printPair("installerPackageName", keyAt.first);
                    indentingPrintWriter.printPair("packageName", keyAt.second);
                    indentingPrintWriter.printPair("silentUpdatedMillis", this.mSilentUpdateInfos.valueAt(i));
                    indentingPrintWriter.println();
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
    }
}
