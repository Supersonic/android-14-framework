package android.filterfw.core;

import android.p008os.SystemClock;
import android.util.Log;
/* compiled from: StopWatchMap.java */
/* loaded from: classes.dex */
class StopWatch {
    private String mName;
    private int STOP_WATCH_LOGGING_PERIOD = 200;
    private String TAG = "MFF";
    private long mStartTime = -1;
    private long mTotalTime = 0;
    private int mNumCalls = 0;

    public StopWatch(String name) {
        this.mName = name;
    }

    public void start() {
        if (this.mStartTime != -1) {
            throw new RuntimeException("Calling start with StopWatch already running");
        }
        this.mStartTime = SystemClock.elapsedRealtime();
    }

    public void stop() {
        if (this.mStartTime == -1) {
            throw new RuntimeException("Calling stop with StopWatch already stopped");
        }
        long stopTime = SystemClock.elapsedRealtime();
        this.mTotalTime += stopTime - this.mStartTime;
        int i = this.mNumCalls + 1;
        this.mNumCalls = i;
        this.mStartTime = -1L;
        if (i % this.STOP_WATCH_LOGGING_PERIOD == 0) {
            Log.m108i(this.TAG, "AVG ms/call " + this.mName + ": " + String.format("%.1f", Float.valueOf((((float) this.mTotalTime) * 1.0f) / this.mNumCalls)));
            this.mTotalTime = 0L;
            this.mNumCalls = 0;
        }
    }
}
