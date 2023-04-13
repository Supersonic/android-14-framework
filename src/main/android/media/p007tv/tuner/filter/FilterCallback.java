package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.FilterCallback */
/* loaded from: classes2.dex */
public interface FilterCallback {
    void onFilterEvent(Filter filter, FilterEvent[] filterEventArr);

    void onFilterStatusChanged(Filter filter, int i);
}
