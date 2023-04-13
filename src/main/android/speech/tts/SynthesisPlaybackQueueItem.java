package android.speech.tts;

import android.media.AudioTrack;
import android.speech.tts.TextToSpeechService;
import android.util.Log;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class SynthesisPlaybackQueueItem extends PlaybackQueueItem implements AudioTrack.OnPlaybackPositionUpdateListener {
    private static final boolean DBG = false;
    private static final long MAX_UNCONSUMED_AUDIO_MS = 500;
    private static final int NOT_RUN = 0;
    private static final int RUN_CALLED = 1;
    private static final int STOP_CALLED = 2;
    private static final String TAG = "TTS.SynthQueueItem";
    private final BlockingAudioTrack mAudioTrack;
    private final LinkedList<ListEntry> mDataBufferList;
    private volatile boolean mDone;
    private final Lock mListLock;
    private final AbstractEventLogger mLogger;
    private final Condition mNotFull;
    private final Condition mReadReady;
    private final AtomicInteger mRunState;
    private volatile int mStatusCode;
    private volatile boolean mStopped;
    private int mUnconsumedBytes;
    private ConcurrentLinkedQueue<ProgressMarker> markerList;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SynthesisPlaybackQueueItem(TextToSpeechService.AudioOutputParams audioParams, int sampleRate, int audioFormat, int channelCount, TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, AbstractEventLogger logger) {
        super(dispatcher, callerIdentity);
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mListLock = reentrantLock;
        this.mReadReady = reentrantLock.newCondition();
        this.mNotFull = reentrantLock.newCondition();
        this.mDataBufferList = new LinkedList<>();
        this.markerList = new ConcurrentLinkedQueue<>();
        this.mRunState = new AtomicInteger(0);
        this.mUnconsumedBytes = 0;
        this.mStopped = false;
        this.mDone = false;
        this.mStatusCode = 0;
        this.mAudioTrack = new BlockingAudioTrack(audioParams, sampleRate, audioFormat, channelCount);
        this.mLogger = logger;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        if (!this.mRunState.compareAndSet(0, 1)) {
            return;
        }
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        dispatcher.dispatchOnStart();
        if (!this.mAudioTrack.init()) {
            dispatcher.dispatchOnError(-5);
            return;
        }
        this.mAudioTrack.setPlaybackPositionUpdateListener(this);
        updateMarker();
        while (true) {
            try {
                byte[] buffer = take();
                if (buffer == null) {
                    break;
                }
                this.mAudioTrack.write(buffer);
                this.mLogger.onAudioDataWritten();
            } catch (InterruptedException e) {
            }
        }
        this.mAudioTrack.waitAndRelease();
        dispatchEndStatus();
    }

    private void dispatchEndStatus() {
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        if (this.mStatusCode == 0) {
            dispatcher.dispatchOnSuccess();
        } else if (this.mStatusCode == -2) {
            dispatcher.dispatchOnStop();
        } else {
            dispatcher.dispatchOnError(this.mStatusCode);
        }
        this.mLogger.onCompleted(this.mStatusCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(int statusCode) {
        try {
            this.mListLock.lock();
            this.mStopped = true;
            this.mStatusCode = statusCode;
            this.mNotFull.signal();
            if (this.mRunState.getAndSet(2) == 0) {
                dispatchEndStatus();
                return;
            }
            this.mReadReady.signal();
            this.mListLock.unlock();
            this.mAudioTrack.stop();
        } finally {
            this.mListLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void done() {
        try {
            this.mListLock.lock();
            this.mDone = true;
            this.mReadReady.signal();
            this.mNotFull.signal();
        } finally {
            this.mListLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ProgressMarker {
        public final int end;
        public final int frames;
        public final int start;

        public ProgressMarker(int frames, int start, int end) {
            this.frames = frames;
            this.start = start;
            this.end = end;
        }
    }

    void updateMarker() {
        ProgressMarker marker = this.markerList.peek();
        if (marker != null) {
            int markerInFrames = marker.frames == 0 ? 1 : marker.frames;
            this.mAudioTrack.setNotificationMarkerPosition(markerInFrames);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rangeStart(int markerInFrames, int start, int end) {
        this.markerList.add(new ProgressMarker(markerInFrames, start, end));
        updateMarker();
    }

    @Override // android.media.AudioTrack.OnPlaybackPositionUpdateListener
    public void onMarkerReached(AudioTrack track) {
        ProgressMarker marker = this.markerList.poll();
        if (marker == null) {
            Log.m110e(TAG, "onMarkerReached reached called but no marker in queue");
            return;
        }
        getDispatcher().dispatchOnRangeStart(marker.start, marker.end, marker.frames);
        updateMarker();
    }

    @Override // android.media.AudioTrack.OnPlaybackPositionUpdateListener
    public void onPeriodicNotification(AudioTrack track) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(byte[] buffer) throws InterruptedException {
        try {
            this.mListLock.lock();
            while (this.mAudioTrack.getAudioLengthMs(this.mUnconsumedBytes) > MAX_UNCONSUMED_AUDIO_MS && !this.mStopped) {
                this.mNotFull.await();
            }
            if (this.mStopped) {
                return;
            }
            this.mDataBufferList.add(new ListEntry(buffer));
            this.mUnconsumedBytes += buffer.length;
            this.mReadReady.signal();
        } finally {
            this.mListLock.unlock();
        }
    }

    private byte[] take() throws InterruptedException {
        try {
            this.mListLock.lock();
            while (this.mDataBufferList.size() == 0 && !this.mStopped && !this.mDone) {
                this.mReadReady.await();
            }
            if (this.mStopped) {
                return null;
            }
            ListEntry entry = this.mDataBufferList.poll();
            if (entry == null) {
                return null;
            }
            this.mUnconsumedBytes -= entry.mBytes.length;
            this.mNotFull.signal();
            return entry.mBytes;
        } finally {
            this.mListLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class ListEntry {
        final byte[] mBytes;

        ListEntry(byte[] bytes) {
            this.mBytes = bytes;
        }
    }
}
