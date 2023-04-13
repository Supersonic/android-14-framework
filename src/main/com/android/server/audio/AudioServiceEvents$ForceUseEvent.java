package com.android.server.audio;

import android.media.AudioSystem;
import com.android.server.utils.EventLogger;
/* loaded from: classes.dex */
public final class AudioServiceEvents$ForceUseEvent extends EventLogger.Event {
    public final int mConfig;
    public final String mReason;
    public final int mUsage;

    public AudioServiceEvents$ForceUseEvent(int i, int i2, String str) {
        this.mUsage = i;
        this.mConfig = i2;
        this.mReason = str;
    }

    @Override // com.android.server.utils.EventLogger.Event
    public String eventToString() {
        return "setForceUse(" + AudioSystem.forceUseUsageToString(this.mUsage) + ", " + AudioSystem.forceUseConfigToString(this.mConfig) + ") due to " + this.mReason;
    }
}
