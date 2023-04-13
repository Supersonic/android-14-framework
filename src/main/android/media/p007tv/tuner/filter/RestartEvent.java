package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.RestartEvent */
/* loaded from: classes2.dex */
public final class RestartEvent extends FilterEvent {
    public static final int NEW_FILTER_FIRST_START_ID = 0;
    private final int mStartId;

    private RestartEvent(int startId) {
        this.mStartId = startId;
    }

    public int getStartId() {
        return this.mStartId;
    }
}
