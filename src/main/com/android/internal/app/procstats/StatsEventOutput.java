package com.android.internal.app.procstats;

import android.util.StatsEvent;
import com.android.internal.util.FrameworkStatsLog;
import java.util.List;
/* loaded from: classes4.dex */
public class StatsEventOutput {
    List<StatsEvent> mOutput;

    public StatsEventOutput(List<StatsEvent> output) {
        this.mOutput = output;
    }

    public void write(int atomTag, int uid, String processName, int measurementStartUptimeSecs, int measurementEndUptimeSecs, int measurementDurationUptimeSecs, int topSeconds, int fgsSeconds, int boundTopSeconds, int boundFgsSeconds, int importantForegroundSeconds, int cachedSeconds, int frozenSeconds, int otherSeconds) {
        this.mOutput.add(FrameworkStatsLog.buildStatsEvent(atomTag, uid, processName, measurementStartUptimeSecs, measurementEndUptimeSecs, measurementDurationUptimeSecs, topSeconds, fgsSeconds, boundTopSeconds, boundFgsSeconds, importantForegroundSeconds, cachedSeconds, frozenSeconds, otherSeconds));
    }

    public void write(int atomTag, int clientUid, String processName, int serviceUid, String serviceName, int measurementStartUptimeSecs, int measurementEndUptimeSecs, int measurementDurationUptimeSecs, int activeDurationUptimeSecs, int activeCount, String serviceProcessName) {
        this.mOutput.add(FrameworkStatsLog.buildStatsEvent(atomTag, clientUid, processName, serviceUid, serviceName, measurementStartUptimeSecs, measurementEndUptimeSecs, measurementDurationUptimeSecs, activeDurationUptimeSecs, activeCount, serviceProcessName));
    }
}
