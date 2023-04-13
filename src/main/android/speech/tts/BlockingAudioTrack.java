package android.speech.tts;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.speech.tts.TextToSpeechService;
import android.util.Log;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class BlockingAudioTrack {
    private static final boolean DBG = false;
    private static final long MAX_PROGRESS_WAIT_MS = 2500;
    private static final long MAX_SLEEP_TIME_MS = 2500;
    private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
    private static final long MIN_SLEEP_TIME_MS = 20;
    private static final String TAG = "TTS.BlockingAudioTrack";
    private final int mAudioFormat;
    private final TextToSpeechService.AudioOutputParams mAudioParams;
    private final int mBytesPerFrame;
    private int mBytesWritten;
    private final int mChannelCount;
    private final int mSampleRateInHz;
    private int mSessionId;
    private Object mAudioTrackLock = new Object();
    private boolean mIsShortUtterance = false;
    private int mAudioBufferSize = 0;
    private AudioTrack mAudioTrack = null;
    private volatile boolean mStopped = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockingAudioTrack(TextToSpeechService.AudioOutputParams audioParams, int sampleRate, int audioFormat, int channelCount) {
        this.mBytesWritten = 0;
        this.mAudioParams = audioParams;
        this.mSampleRateInHz = sampleRate;
        this.mAudioFormat = audioFormat;
        this.mChannelCount = channelCount;
        this.mBytesPerFrame = AudioFormat.getBytesPerSample(audioFormat) * channelCount;
        this.mBytesWritten = 0;
    }

    public boolean init() {
        AudioTrack track = createStreamingAudioTrack();
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = track;
        }
        if (track == null) {
            return false;
        }
        return true;
    }

    public void stop() {
        synchronized (this.mAudioTrackLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null) {
                audioTrack.stop();
            }
            this.mStopped = true;
        }
    }

    public int write(byte[] data) {
        AudioTrack track;
        synchronized (this.mAudioTrackLock) {
            track = this.mAudioTrack;
        }
        if (track == null || this.mStopped) {
            return -1;
        }
        int bytesWritten = writeToAudioTrack(track, data);
        this.mBytesWritten += bytesWritten;
        return bytesWritten;
    }

    public void waitAndRelease() {
        AudioTrack track;
        synchronized (this.mAudioTrackLock) {
            track = this.mAudioTrack;
        }
        if (track == null) {
            return;
        }
        if (this.mBytesWritten < this.mAudioBufferSize && !this.mStopped) {
            this.mIsShortUtterance = true;
            track.stop();
        }
        if (!this.mStopped) {
            blockUntilDone(this.mAudioTrack);
        }
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = null;
        }
        track.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getChannelConfig(int channelCount) {
        if (channelCount == 1) {
            return 4;
        }
        if (channelCount == 2) {
            return 12;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getAudioLengthMs(int numBytes) {
        int unconsumedFrames = numBytes / this.mBytesPerFrame;
        long estimatedTimeMs = (unconsumedFrames * 1000) / this.mSampleRateInHz;
        return estimatedTimeMs;
    }

    private static int writeToAudioTrack(AudioTrack audioTrack, byte[] bytes) {
        int written;
        if (audioTrack.getPlayState() != 3) {
            audioTrack.play();
        }
        int count = 0;
        while (count < bytes.length && (written = audioTrack.write(bytes, count, bytes.length)) > 0) {
            count += written;
        }
        return count;
    }

    private AudioTrack createStreamingAudioTrack() {
        int channelConfig = getChannelConfig(this.mChannelCount);
        int minBufferSizeInBytes = AudioTrack.getMinBufferSize(this.mSampleRateInHz, channelConfig, this.mAudioFormat);
        int bufferSizeInBytes = Math.max(8192, minBufferSizeInBytes);
        AudioFormat audioFormat = new AudioFormat.Builder().setChannelMask(channelConfig).setEncoding(this.mAudioFormat).setSampleRate(this.mSampleRateInHz).build();
        AudioTrack audioTrack = new AudioTrack(this.mAudioParams.mAudioAttributes, audioFormat, bufferSizeInBytes, 1, this.mAudioParams.mSessionId);
        if (audioTrack.getState() != 1) {
            Log.m104w(TAG, "Unable to create audio track.");
            audioTrack.release();
            return null;
        }
        this.mAudioBufferSize = bufferSizeInBytes;
        setupVolume(audioTrack, this.mAudioParams.mVolume, this.mAudioParams.mPan);
        return audioTrack;
    }

    private void blockUntilDone(AudioTrack audioTrack) {
        if (this.mBytesWritten <= 0) {
            return;
        }
        if (this.mIsShortUtterance) {
            blockUntilEstimatedCompletion();
        } else {
            blockUntilCompletion(audioTrack);
        }
    }

    private void blockUntilEstimatedCompletion() {
        int lengthInFrames = this.mBytesWritten / this.mBytesPerFrame;
        long estimatedTimeMs = (lengthInFrames * 1000) / this.mSampleRateInHz;
        try {
            Thread.sleep(estimatedTimeMs);
        } catch (InterruptedException e) {
        }
    }

    private void blockUntilCompletion(AudioTrack audioTrack) {
        int lengthInFrames = this.mBytesWritten / this.mBytesPerFrame;
        int previousPosition = -1;
        long blockedTimeMs = 0;
        while (true) {
            int currentPosition = audioTrack.getPlaybackHeadPosition();
            if (currentPosition < lengthInFrames && audioTrack.getPlayState() == 3 && !this.mStopped) {
                long estimatedTimeMs = ((lengthInFrames - currentPosition) * 1000) / audioTrack.getSampleRate();
                long sleepTimeMs = clip(estimatedTimeMs, 20L, 2500L);
                if (currentPosition == previousPosition) {
                    blockedTimeMs += sleepTimeMs;
                    if (blockedTimeMs > 2500) {
                        Log.m104w(TAG, "Waited unsuccessfully for 2500ms for AudioTrack to make progress, Aborting");
                        return;
                    }
                } else {
                    blockedTimeMs = 0;
                }
                previousPosition = currentPosition;
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static void setupVolume(AudioTrack audioTrack, float volume, float pan) {
        float vol = clip(volume, 0.0f, 1.0f);
        float panning = clip(pan, -1.0f, 1.0f);
        float volLeft = vol;
        float volRight = vol;
        if (panning > 0.0f) {
            volLeft *= 1.0f - panning;
        } else if (panning < 0.0f) {
            volRight *= 1.0f + panning;
        }
        if (audioTrack.setStereoVolume(volLeft, volRight) != 0) {
            Log.m110e(TAG, "Failed to set volume");
        }
    }

    private static final long clip(long value, long min, long max) {
        return value < min ? min : value < max ? value : max;
    }

    private static final float clip(float value, float min, float max) {
        return value < min ? min : value < max ? value : max;
    }

    public void setPlaybackPositionUpdateListener(AudioTrack.OnPlaybackPositionUpdateListener listener) {
        synchronized (this.mAudioTrackLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null) {
                audioTrack.setPlaybackPositionUpdateListener(listener);
            }
        }
    }

    public void setNotificationMarkerPosition(int frames) {
        synchronized (this.mAudioTrackLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null) {
                audioTrack.setNotificationMarkerPosition(frames);
            }
        }
    }
}
