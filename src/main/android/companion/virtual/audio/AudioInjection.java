package android.companion.virtual.audio;

import android.annotation.SystemApi;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;
@SystemApi
/* loaded from: classes.dex */
public final class AudioInjection {
    private static final String TAG = "AudioInjection";
    private final AudioFormat mAudioFormat;
    private AudioTrack mAudioTrack;
    private boolean mIsSilent;
    private final Object mLock = new Object();
    private int mPlayState = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSilent(boolean isSilent) {
        synchronized (this.mLock) {
            this.mIsSilent = isSilent;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioTrack(AudioTrack audioTrack) {
        Log.m112d(TAG, "set AudioTrack with " + audioTrack);
        synchronized (this.mLock) {
            if (audioTrack != null) {
                if (audioTrack.getState() != 1) {
                    throw new IllegalStateException("set an uninitialized AudioTrack.");
                }
                if (this.mPlayState == 3 && audioTrack.getPlayState() != 3) {
                    audioTrack.play();
                }
                if (this.mPlayState == 1 && audioTrack.getPlayState() != 1) {
                    audioTrack.stop();
                }
            }
            AudioTrack audioTrack2 = this.mAudioTrack;
            if (audioTrack2 != null) {
                audioTrack2.release();
            }
            this.mAudioTrack = audioTrack;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioInjection(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null) {
                audioTrack.release();
                this.mAudioTrack = null;
            }
        }
    }

    public AudioFormat getFormat() {
        return this.mAudioFormat;
    }

    public int write(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        return write(audioData, offsetInBytes, sizeInBytes, 0);
    }

    public int write(byte[] audioData, int offsetInBytes, int sizeInBytes, int writeMode) {
        int sizeWrite;
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && !this.mIsSilent) {
                sizeWrite = audioTrack.write(audioData, offsetInBytes, sizeInBytes, writeMode);
            } else {
                sizeWrite = 0;
            }
        }
        return sizeWrite;
    }

    public int write(ByteBuffer audioBuffer, int sizeInBytes, int writeMode) {
        int sizeWrite;
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && !this.mIsSilent) {
                sizeWrite = audioTrack.write(audioBuffer, sizeInBytes, writeMode);
            } else {
                sizeWrite = 0;
            }
        }
        return sizeWrite;
    }

    public int write(ByteBuffer audioBuffer, int sizeInBytes, int writeMode, long timestamp) {
        int sizeWrite;
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && !this.mIsSilent) {
                sizeWrite = audioTrack.write(audioBuffer, sizeInBytes, writeMode, timestamp);
            } else {
                sizeWrite = 0;
            }
        }
        return sizeWrite;
    }

    public int write(float[] audioData, int offsetInFloats, int sizeInFloats, int writeMode) {
        int sizeWrite;
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && !this.mIsSilent) {
                sizeWrite = audioTrack.write(audioData, offsetInFloats, sizeInFloats, writeMode);
            } else {
                sizeWrite = 0;
            }
        }
        return sizeWrite;
    }

    public int write(short[] audioData, int offsetInShorts, int sizeInShorts) {
        return write(audioData, offsetInShorts, sizeInShorts, 0);
    }

    public int write(short[] audioData, int offsetInShorts, int sizeInShorts, int writeMode) {
        int sizeWrite;
        synchronized (this.mLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && !this.mIsSilent) {
                sizeWrite = audioTrack.write(audioData, offsetInShorts, sizeInShorts, writeMode);
            } else {
                sizeWrite = 0;
            }
        }
        return sizeWrite;
    }

    public void play() {
        synchronized (this.mLock) {
            this.mPlayState = 3;
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && audioTrack.getPlayState() != 3) {
                this.mAudioTrack.play();
            }
        }
    }

    public void stop() {
        synchronized (this.mLock) {
            this.mPlayState = 1;
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null && audioTrack.getPlayState() != 1) {
                this.mAudioTrack.stop();
            }
        }
    }

    public int getPlayState() {
        int i;
        synchronized (this.mLock) {
            i = this.mPlayState;
        }
        return i;
    }
}
