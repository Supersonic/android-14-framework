package android.speech.tts;

import android.speech.tts.TextToSpeechService;
import android.util.Log;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class PlaybackSynthesisCallback extends AbstractSynthesisCallback {
    private static final boolean DBG = false;
    private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
    private static final String TAG = "PlaybackSynthesisRequest";
    private final TextToSpeechService.AudioOutputParams mAudioParams;
    private final AudioPlaybackHandler mAudioTrackHandler;
    private final Object mCallerIdentity;
    private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
    private volatile boolean mDone;
    private SynthesisPlaybackQueueItem mItem;
    private final AbstractEventLogger mLogger;
    private final Object mStateLock;
    protected int mStatusCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaybackSynthesisCallback(TextToSpeechService.AudioOutputParams audioParams, AudioPlaybackHandler audioTrackHandler, TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, AbstractEventLogger logger, boolean clientIsUsingV2) {
        super(clientIsUsingV2);
        this.mStateLock = new Object();
        this.mItem = null;
        this.mDone = false;
        this.mAudioParams = audioParams;
        this.mAudioTrackHandler = audioTrackHandler;
        this.mDispatcher = dispatcher;
        this.mCallerIdentity = callerIdentity;
        this.mLogger = logger;
        this.mStatusCode = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public void stop() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return;
            }
            if (this.mStatusCode == -2) {
                Log.m104w(TAG, "stop() called twice");
                return;
            }
            SynthesisPlaybackQueueItem item = this.mItem;
            this.mStatusCode = -2;
            if (item != null) {
                item.stop(-2);
                return;
            }
            this.mLogger.onCompleted(-2);
            this.mDispatcher.dispatchOnStop();
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int getMaxBufferSize() {
        return 8192;
    }

    @Override // android.speech.tts.SynthesisCallback
    public boolean hasStarted() {
        boolean z;
        synchronized (this.mStateLock) {
            z = this.mItem != null;
        }
        return z;
    }

    @Override // android.speech.tts.SynthesisCallback
    public boolean hasFinished() {
        boolean z;
        synchronized (this.mStateLock) {
            z = this.mDone;
        }
        return z;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int start(int sampleRateInHz, int audioFormat, int channelCount) {
        if (audioFormat != 3 && audioFormat != 2 && audioFormat != 4) {
            Log.m104w(TAG, "Audio format encoding " + audioFormat + " not supported. Please use one of AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT or AudioFormat.ENCODING_PCM_FLOAT");
        }
        this.mDispatcher.dispatchOnBeginSynthesis(sampleRateInHz, audioFormat, channelCount);
        int channelConfig = BlockingAudioTrack.getChannelConfig(channelCount);
        synchronized (this.mStateLock) {
            if (channelConfig == 0) {
                Log.m110e(TAG, "Unsupported number of channels :" + channelCount);
                this.mStatusCode = -5;
                return -1;
            }
            int i = this.mStatusCode;
            if (i == -2) {
                return errorCodeOnStop();
            } else if (i != 0) {
                return -1;
            } else {
                if (this.mItem != null) {
                    Log.m110e(TAG, "Start called twice");
                    return -1;
                }
                SynthesisPlaybackQueueItem item = new SynthesisPlaybackQueueItem(this.mAudioParams, sampleRateInHz, audioFormat, channelCount, this.mDispatcher, this.mCallerIdentity, this.mLogger);
                this.mAudioTrackHandler.enqueue(item);
                this.mItem = item;
                return 0;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int audioAvailable(byte[] buffer, int offset, int length) {
        if (length > getMaxBufferSize() || length <= 0) {
            throw new IllegalArgumentException("buffer is too large or of zero length (" + length + " bytes)");
        }
        synchronized (this.mStateLock) {
            SynthesisPlaybackQueueItem item = this.mItem;
            if (item == null) {
                this.mStatusCode = -5;
                return -1;
            }
            int i = this.mStatusCode;
            if (i != 0) {
                return -1;
            }
            if (i == -2) {
                return errorCodeOnStop();
            }
            byte[] bufferCopy = new byte[length];
            System.arraycopy(buffer, offset, bufferCopy, 0, length);
            this.mDispatcher.dispatchOnAudioAvailable(bufferCopy);
            try {
                item.put(bufferCopy);
                this.mLogger.onEngineDataReceived();
                return 0;
            } catch (InterruptedException e) {
                synchronized (this.mStateLock) {
                    this.mStatusCode = -5;
                    return -1;
                }
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int done() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                Log.m104w(TAG, "Duplicate call to done()");
                return -1;
            } else if (this.mStatusCode == -2) {
                return errorCodeOnStop();
            } else {
                this.mDone = true;
                SynthesisPlaybackQueueItem item = this.mItem;
                if (item == null) {
                    Log.m104w(TAG, "done() was called before start() call");
                    int i = this.mStatusCode;
                    if (i == 0) {
                        this.mDispatcher.dispatchOnSuccess();
                    } else {
                        this.mDispatcher.dispatchOnError(i);
                    }
                    this.mLogger.onEngineComplete();
                    return -1;
                }
                int statusCode = this.mStatusCode;
                if (statusCode == 0) {
                    item.done();
                } else {
                    item.stop(statusCode);
                }
                this.mLogger.onEngineComplete();
                return 0;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error() {
        error(-3);
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error(int errorCode) {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return;
            }
            this.mStatusCode = errorCode;
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void rangeStart(int markerInFrames, int start, int end) {
        SynthesisPlaybackQueueItem synthesisPlaybackQueueItem = this.mItem;
        if (synthesisPlaybackQueueItem == null) {
            Log.m110e(TAG, "mItem is null");
        } else {
            synthesisPlaybackQueueItem.rangeStart(markerInFrames, start, end);
        }
    }
}
