package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.ScramblingStatusEvent */
/* loaded from: classes2.dex */
public final class ScramblingStatusEvent extends FilterEvent {
    private final int mScramblingStatus;

    private ScramblingStatusEvent(int scramblingStatus) {
        this.mScramblingStatus = scramblingStatus;
    }

    public int getScramblingStatus() {
        return this.mScramblingStatus;
    }
}
