package android.media.metrics;

import android.annotation.NonNull;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PlaybackSession implements AutoCloseable {
    private boolean mClosed = false;
    private final String mId;
    private final LogSessionId mLogSessionId;
    private final MediaMetricsManager mManager;

    public PlaybackSession(String id, MediaMetricsManager manager) {
        this.mId = id;
        this.mManager = manager;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) id);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) manager);
        this.mLogSessionId = new LogSessionId(id);
    }

    public void reportPlaybackMetrics(PlaybackMetrics metrics) {
        this.mManager.reportPlaybackMetrics(this.mId, metrics);
    }

    public void reportPlaybackErrorEvent(PlaybackErrorEvent event) {
        this.mManager.reportPlaybackErrorEvent(this.mId, event);
    }

    public void reportNetworkEvent(NetworkEvent event) {
        this.mManager.reportNetworkEvent(this.mId, event);
    }

    public void reportPlaybackStateEvent(PlaybackStateEvent event) {
        this.mManager.reportPlaybackStateEvent(this.mId, event);
    }

    public void reportTrackChangeEvent(TrackChangeEvent event) {
        this.mManager.reportTrackChangeEvent(this.mId, event);
    }

    public LogSessionId getSessionId() {
        return this.mLogSessionId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaybackSession that = (PlaybackSession) o;
        return Objects.equals(this.mId, that.mId);
    }

    public int hashCode() {
        return Objects.hash(this.mId);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mClosed = true;
        this.mManager.releaseSessionId(this.mLogSessionId.getStringId());
    }
}
