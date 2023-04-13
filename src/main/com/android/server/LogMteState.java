package com.android.server;

import android.app.StatsManager;
import android.content.Context;
import android.util.StatsEvent;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.Zygote;
import com.android.internal.util.FrameworkStatsLog;
import java.util.List;
/* loaded from: classes.dex */
public class LogMteState {
    public static void register(Context context) {
        ((StatsManager) context.getSystemService(StatsManager.class)).setPullAtomCallback((int) FrameworkStatsLog.MTE_STATE, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.LogMteState.1
            public int onPullAtom(int i, List<StatsEvent> list) {
                if (i != 10181) {
                    throw new UnsupportedOperationException("Unknown tagId=" + i);
                }
                list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.MTE_STATE, Zygote.nativeSupportsMemoryTagging() ? 2 : 1));
                return 0;
            }
        });
    }
}
