package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.FilterConfiguration */
/* loaded from: classes2.dex */
public abstract class FilterConfiguration {
    final Settings mSettings;

    public abstract int getType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilterConfiguration(Settings settings) {
        this.mSettings = settings;
    }

    public Settings getSettings() {
        return this.mSettings;
    }
}
