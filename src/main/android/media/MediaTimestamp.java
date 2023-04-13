package android.media;
/* loaded from: classes2.dex */
public final class MediaTimestamp {
    public static final MediaTimestamp TIMESTAMP_UNKNOWN = new MediaTimestamp(-1, -1, 0.0f);
    public final float clockRate;
    public final long mediaTimeUs;
    public final long nanoTime;

    public long getAnchorMediaTimeUs() {
        return this.mediaTimeUs;
    }

    @Deprecated
    public long getAnchorSytemNanoTime() {
        return getAnchorSystemNanoTime();
    }

    public long getAnchorSystemNanoTime() {
        return this.nanoTime;
    }

    public float getMediaClockRate() {
        return this.clockRate;
    }

    public MediaTimestamp(long mediaTimeUs, long nanoTimeNs, float clockRate) {
        this.mediaTimeUs = mediaTimeUs;
        this.nanoTime = nanoTimeNs;
        this.clockRate = clockRate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaTimestamp() {
        this.mediaTimeUs = 0L;
        this.nanoTime = 0L;
        this.clockRate = 1.0f;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MediaTimestamp that = (MediaTimestamp) obj;
        if (this.mediaTimeUs == that.mediaTimeUs && this.nanoTime == that.nanoTime && this.clockRate == that.clockRate) {
            return true;
        }
        return false;
    }

    public String toString() {
        return getClass().getName() + "{AnchorMediaTimeUs=" + this.mediaTimeUs + " AnchorSystemNanoTime=" + this.nanoTime + " clockRate=" + this.clockRate + "}";
    }
}
