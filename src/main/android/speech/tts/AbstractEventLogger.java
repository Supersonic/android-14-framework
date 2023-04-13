package android.speech.tts;

import android.p008os.SystemClock;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public abstract class AbstractEventLogger {
    protected final int mCallerPid;
    protected final int mCallerUid;
    protected final String mServiceApp;
    protected long mPlaybackStartTime = -1;
    private volatile long mRequestProcessingStartTime = -1;
    private volatile long mEngineStartTime = -1;
    private volatile long mEngineCompleteTime = -1;
    private boolean mLogWritten = false;
    protected final long mReceivedTime = SystemClock.elapsedRealtime();

    protected abstract void logFailure(int i);

    protected abstract void logSuccess(long j, long j2, long j3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractEventLogger(int callerUid, int callerPid, String serviceApp) {
        this.mCallerUid = callerUid;
        this.mCallerPid = callerPid;
        this.mServiceApp = serviceApp;
    }

    public void onRequestProcessingStart() {
        this.mRequestProcessingStartTime = SystemClock.elapsedRealtime();
    }

    public void onEngineDataReceived() {
        if (this.mEngineStartTime == -1) {
            this.mEngineStartTime = SystemClock.elapsedRealtime();
        }
    }

    public void onEngineComplete() {
        this.mEngineCompleteTime = SystemClock.elapsedRealtime();
    }

    public void onAudioDataWritten() {
        if (this.mPlaybackStartTime == -1) {
            this.mPlaybackStartTime = SystemClock.elapsedRealtime();
        }
    }

    public void onCompleted(int statusCode) {
        if (!this.mLogWritten) {
            this.mLogWritten = true;
            SystemClock.elapsedRealtime();
            if (statusCode == 0 && this.mPlaybackStartTime != -1 && this.mEngineCompleteTime != -1) {
                long audioLatency = this.mPlaybackStartTime - this.mReceivedTime;
                long engineLatency = this.mEngineStartTime - this.mRequestProcessingStartTime;
                long engineTotal = this.mEngineCompleteTime - this.mRequestProcessingStartTime;
                logSuccess(audioLatency, engineLatency, engineTotal);
                return;
            }
            logFailure(statusCode);
        }
    }
}
