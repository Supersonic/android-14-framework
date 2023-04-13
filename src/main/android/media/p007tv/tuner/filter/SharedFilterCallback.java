package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.SharedFilterCallback */
/* loaded from: classes2.dex */
public interface SharedFilterCallback {
    void onFilterEvent(SharedFilter sharedFilter, FilterEvent[] filterEventArr);

    void onFilterStatusChanged(SharedFilter sharedFilter, int i);
}
