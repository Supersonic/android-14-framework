package android.speech.tts;

import android.p008os.ConditionVariable;
import android.speech.tts.TextToSpeechService;
/* loaded from: classes3.dex */
class SilencePlaybackQueueItem extends PlaybackQueueItem {
    private final ConditionVariable mCondVar;
    private final long mSilenceDurationMs;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SilencePlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, long silenceDurationMs) {
        super(dispatcher, callerIdentity);
        this.mCondVar = new ConditionVariable();
        this.mSilenceDurationMs = silenceDurationMs;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        getDispatcher().dispatchOnStart();
        boolean wasStopped = false;
        long j = this.mSilenceDurationMs;
        if (j > 0) {
            wasStopped = this.mCondVar.block(j);
        }
        if (wasStopped) {
            getDispatcher().dispatchOnStop();
        } else {
            getDispatcher().dispatchOnSuccess();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(int errorCode) {
        this.mCondVar.open();
    }
}
