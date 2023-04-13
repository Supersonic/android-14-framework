package android.media.metrics;

import android.annotation.NonNull;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class EditingSession implements AutoCloseable {
    private final String mId;
    private final LogSessionId mLogSessionId;
    private final MediaMetricsManager mManager;

    public EditingSession(String id, MediaMetricsManager manager) {
        this.mId = id;
        this.mManager = manager;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) id);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) manager);
        this.mLogSessionId = new LogSessionId(id);
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
        EditingSession that = (EditingSession) o;
        return Objects.equals(this.mId, that.mId);
    }

    public int hashCode() {
        return Objects.hash(this.mId);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mManager.releaseSessionId(this.mLogSessionId.getStringId());
    }
}
