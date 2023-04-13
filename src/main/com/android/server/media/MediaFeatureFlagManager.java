package com.android.server.media;

import android.provider.DeviceConfig;
/* loaded from: classes2.dex */
public class MediaFeatureFlagManager {
    public static final MediaFeatureFlagManager sInstance = new MediaFeatureFlagManager();

    public static MediaFeatureFlagManager getInstance() {
        return sInstance;
    }

    public boolean getBoolean(String str, boolean z) {
        return DeviceConfig.getBoolean("media_better_together", str, z);
    }
}
