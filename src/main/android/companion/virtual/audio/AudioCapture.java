package android.companion.virtual.audio;

import android.annotation.SystemApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;
import java.nio.ByteBuffer;
@SystemApi
/* loaded from: classes.dex */
public final class AudioCapture {
    private static final String TAG = "AudioCapture";
    private final AudioFormat mAudioFormat;
    private AudioRecord mAudioRecord;
    private final Object mLock = new Object();
    private int mRecordingState = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioRecord(AudioRecord audioRecord) {
        Log.m112d(TAG, "set AudioRecord with " + audioRecord);
        synchronized (this.mLock) {
            if (audioRecord != null) {
                if (audioRecord.getState() != 1) {
                    throw new IllegalStateException("set an uninitialized AudioRecord.");
                }
                if (this.mRecordingState == 3 && audioRecord.getRecordingState() != 3) {
                    audioRecord.startRecording();
                }
                if (this.mRecordingState == 1 && audioRecord.getRecordingState() != 1) {
                    audioRecord.stop();
                }
            }
            AudioRecord audioRecord2 = this.mAudioRecord;
            if (audioRecord2 != null) {
                audioRecord2.release();
            }
            this.mAudioRecord = audioRecord;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioCapture(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        synchronized (this.mLock) {
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null) {
                audioRecord.release();
                this.mAudioRecord = null;
            }
        }
    }

    public AudioFormat getFormat() {
        return this.mAudioFormat;
    }

    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        return read(audioData, offsetInBytes, sizeInBytes, 0);
    }

    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes, int readMode) {
        int sizeRead;
        synchronized (this.mLock) {
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null) {
                sizeRead = audioRecord.read(audioData, offsetInBytes, sizeInBytes, readMode);
            } else {
                sizeRead = 0;
            }
        }
        return sizeRead;
    }

    public int read(ByteBuffer audioBuffer, int sizeInBytes) {
        return read(audioBuffer, sizeInBytes, 0);
    }

    public int read(ByteBuffer audioBuffer, int sizeInBytes, int readMode) {
        int sizeRead;
        synchronized (this.mLock) {
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null) {
                sizeRead = audioRecord.read(audioBuffer, sizeInBytes, readMode);
            } else {
                sizeRead = 0;
            }
        }
        return sizeRead;
    }

    public int read(float[] audioData, int offsetInFloats, int sizeInFloats, int readMode) {
        int sizeRead;
        synchronized (this.mLock) {
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null) {
                sizeRead = audioRecord.read(audioData, offsetInFloats, sizeInFloats, readMode);
            } else {
                sizeRead = 0;
            }
        }
        return sizeRead;
    }

    public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {
        return read(audioData, offsetInShorts, sizeInShorts, 0);
    }

    public int read(short[] audioData, int offsetInShorts, int sizeInShorts, int readMode) {
        int sizeRead;
        synchronized (this.mLock) {
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null) {
                sizeRead = audioRecord.read(audioData, offsetInShorts, sizeInShorts, readMode);
            } else {
                sizeRead = 0;
            }
        }
        return sizeRead;
    }

    public void startRecording() {
        synchronized (this.mLock) {
            this.mRecordingState = 3;
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null && audioRecord.getRecordingState() != 3) {
                this.mAudioRecord.startRecording();
            }
        }
    }

    public void stop() {
        synchronized (this.mLock) {
            this.mRecordingState = 1;
            AudioRecord audioRecord = this.mAudioRecord;
            if (audioRecord != null && audioRecord.getRecordingState() != 1) {
                this.mAudioRecord.stop();
            }
        }
    }

    public int getRecordingState() {
        int i;
        synchronized (this.mLock) {
            i = this.mRecordingState;
        }
        return i;
    }
}
