package android.media.metrics;

import java.util.Objects;
/* loaded from: classes2.dex */
public final class LogSessionId {
    public static final LogSessionId LOG_SESSION_ID_NONE = new LogSessionId("");
    private final String mSessionId;

    public LogSessionId(String id) {
        this.mSessionId = (String) Objects.requireNonNull(id);
    }

    public String getStringId() {
        return this.mSessionId;
    }

    public String toString() {
        return this.mSessionId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogSessionId that = (LogSessionId) o;
        return Objects.equals(this.mSessionId, that.mSessionId);
    }

    public int hashCode() {
        return Objects.hash(this.mSessionId);
    }
}
