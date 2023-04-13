package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.Settings */
/* loaded from: classes2.dex */
public abstract class Settings {
    private final int mType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Settings(int type) {
        this.mType = type;
    }

    public int getType() {
        return this.mType;
    }
}
