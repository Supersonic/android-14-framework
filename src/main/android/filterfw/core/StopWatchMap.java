package android.filterfw.core;

import java.util.HashMap;
/* loaded from: classes.dex */
public class StopWatchMap {
    public boolean LOG_MFF_RUNNING_TIMES = false;
    private HashMap<String, StopWatch> mStopWatches;

    public StopWatchMap() {
        this.mStopWatches = null;
        this.mStopWatches = new HashMap<>();
    }

    public void start(String stopWatchName) {
        if (!this.LOG_MFF_RUNNING_TIMES) {
            return;
        }
        if (!this.mStopWatches.containsKey(stopWatchName)) {
            this.mStopWatches.put(stopWatchName, new StopWatch(stopWatchName));
        }
        this.mStopWatches.get(stopWatchName).start();
    }

    public void stop(String stopWatchName) {
        if (!this.LOG_MFF_RUNNING_TIMES) {
            return;
        }
        if (!this.mStopWatches.containsKey(stopWatchName)) {
            throw new RuntimeException("Calling stop with unknown stopWatchName: " + stopWatchName);
        }
        this.mStopWatches.get(stopWatchName).stop();
    }
}
