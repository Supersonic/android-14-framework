package com.android.server.backup.remote;

import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes.dex */
public class RemoteResult {
    public final int mType;
    public final long mValue;
    public static final RemoteResult FAILED_TIMED_OUT = new RemoteResult(1, 0);
    public static final RemoteResult FAILED_CANCELLED = new RemoteResult(2, 0);
    public static final RemoteResult FAILED_THREAD_INTERRUPTED = new RemoteResult(3, 0);

    /* renamed from: of */
    public static RemoteResult m64of(long j) {
        return new RemoteResult(0, j);
    }

    public RemoteResult(int i, long j) {
        this.mType = i;
        this.mValue = j;
    }

    public boolean isPresent() {
        return this.mType == 0;
    }

    public long get() {
        Preconditions.checkState(isPresent(), "Can't obtain value of failed result");
        return this.mValue;
    }

    public String toString() {
        return "RemoteResult{" + toStringDescription() + "}";
    }

    public final String toStringDescription() {
        int i = this.mType;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        return "FAILED_THREAD_INTERRUPTED";
                    }
                    throw new AssertionError("Unknown type");
                }
                return "FAILED_CANCELLED";
            }
            return "FAILED_TIMED_OUT";
        }
        return Long.toString(this.mValue);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RemoteResult) {
            RemoteResult remoteResult = (RemoteResult) obj;
            return this.mType == remoteResult.mType && this.mValue == remoteResult.mValue;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mType), Long.valueOf(this.mValue));
    }
}
