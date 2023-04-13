package android.media;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public final class SyncParams {
    public static final int AUDIO_ADJUST_MODE_DEFAULT = 0;
    public static final int AUDIO_ADJUST_MODE_RESAMPLE = 2;
    public static final int AUDIO_ADJUST_MODE_STRETCH = 1;
    private static final int SET_AUDIO_ADJUST_MODE = 2;
    private static final int SET_FRAME_RATE = 8;
    private static final int SET_SYNC_SOURCE = 1;
    private static final int SET_TOLERANCE = 4;
    public static final int SYNC_SOURCE_AUDIO = 2;
    public static final int SYNC_SOURCE_DEFAULT = 0;
    public static final int SYNC_SOURCE_SYSTEM_CLOCK = 1;
    public static final int SYNC_SOURCE_VSYNC = 3;
    private int mSet = 0;
    private int mAudioAdjustMode = 0;
    private int mSyncSource = 0;
    private float mTolerance = 0.0f;
    private float mFrameRate = 0.0f;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AudioAdjustMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SyncSource {
    }

    public SyncParams allowDefaults() {
        this.mSet |= 7;
        return this;
    }

    public SyncParams setAudioAdjustMode(int audioAdjustMode) {
        this.mAudioAdjustMode = audioAdjustMode;
        this.mSet |= 2;
        return this;
    }

    public int getAudioAdjustMode() {
        if ((this.mSet & 2) == 0) {
            throw new IllegalStateException("audio adjust mode not set");
        }
        return this.mAudioAdjustMode;
    }

    public SyncParams setSyncSource(int syncSource) {
        this.mSyncSource = syncSource;
        this.mSet |= 1;
        return this;
    }

    public int getSyncSource() {
        if ((this.mSet & 1) == 0) {
            throw new IllegalStateException("sync source not set");
        }
        return this.mSyncSource;
    }

    public SyncParams setTolerance(float tolerance) {
        if (tolerance < 0.0f || tolerance >= 1.0f) {
            throw new IllegalArgumentException("tolerance must be less than one and non-negative");
        }
        this.mTolerance = tolerance;
        this.mSet |= 4;
        return this;
    }

    public float getTolerance() {
        if ((this.mSet & 4) == 0) {
            throw new IllegalStateException("tolerance not set");
        }
        return this.mTolerance;
    }

    public SyncParams setFrameRate(float frameRate) {
        this.mFrameRate = frameRate;
        this.mSet |= 8;
        return this;
    }

    public float getFrameRate() {
        if ((this.mSet & 8) == 0) {
            throw new IllegalStateException("frame rate not set");
        }
        return this.mFrameRate;
    }
}
