package android.speech.tts;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.p008os.ConditionVariable;
import android.speech.tts.TextToSpeechService;
import android.util.Log;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class AudioPlaybackQueueItem extends PlaybackQueueItem {
    private static final String TAG = "TTS.AudioQueueItem";
    private final TextToSpeechService.AudioOutputParams mAudioParams;
    private final Context mContext;
    private final ConditionVariable mDone;
    private volatile boolean mFinished;
    private MediaPlayer mPlayer;
    private final Uri mUri;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioPlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, Context context, Uri uri, TextToSpeechService.AudioOutputParams audioParams) {
        super(dispatcher, callerIdentity);
        this.mContext = context;
        this.mUri = uri;
        this.mAudioParams = audioParams;
        this.mDone = new ConditionVariable();
        this.mPlayer = null;
        this.mFinished = false;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        dispatcher.dispatchOnStart();
        int sessionId = this.mAudioParams.mSessionId;
        MediaPlayer create = MediaPlayer.create(this.mContext, this.mUri, null, this.mAudioParams.mAudioAttributes, sessionId > 0 ? sessionId : 0);
        this.mPlayer = create;
        if (create == null) {
            dispatcher.dispatchOnError(-5);
            return;
        }
        try {
            create.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.1
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.m104w(AudioPlaybackQueueItem.TAG, "Audio playback error: " + what + ", " + extra);
                    AudioPlaybackQueueItem.this.mDone.open();
                    return true;
                }
            });
            this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mp) {
                    AudioPlaybackQueueItem.this.mFinished = true;
                    AudioPlaybackQueueItem.this.mDone.open();
                }
            });
            setupVolume(this.mPlayer, this.mAudioParams.mVolume, this.mAudioParams.mPan);
            this.mPlayer.start();
            this.mDone.block();
            finish();
        } catch (IllegalArgumentException ex) {
            Log.m103w(TAG, "MediaPlayer failed", ex);
            this.mDone.open();
        }
        if (this.mFinished) {
            dispatcher.dispatchOnSuccess();
        } else {
            dispatcher.dispatchOnStop();
        }
    }

    private static void setupVolume(MediaPlayer player, float volume, float pan) {
        float vol = clip(volume, 0.0f, 1.0f);
        float panning = clip(pan, -1.0f, 1.0f);
        float volLeft = vol;
        float volRight = vol;
        if (panning > 0.0f) {
            volLeft *= 1.0f - panning;
        } else if (panning < 0.0f) {
            volRight *= 1.0f + panning;
        }
        player.setVolume(volLeft, volRight);
    }

    private static final float clip(float value, float min, float max) {
        return value < min ? min : value < max ? value : max;
    }

    private void finish() {
        try {
            this.mPlayer.stop();
        } catch (IllegalStateException e) {
        }
        this.mPlayer.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(int errorCode) {
        this.mDone.open();
    }
}
