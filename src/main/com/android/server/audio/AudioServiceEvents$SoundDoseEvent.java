package com.android.server.audio;

import com.android.server.utils.EventLogger;
import java.util.Locale;
/* loaded from: classes.dex */
public final class AudioServiceEvents$SoundDoseEvent extends EventLogger.Event {
    public final int mEventType;
    public final float mFloatValue;
    public final long mLongValue;

    public AudioServiceEvents$SoundDoseEvent(int i, float f, long j) {
        this.mEventType = i;
        this.mFloatValue = f;
        this.mLongValue = j;
    }

    public static AudioServiceEvents$SoundDoseEvent getMomentaryExposureEvent(float f) {
        return new AudioServiceEvents$SoundDoseEvent(0, f, 0L);
    }

    public static AudioServiceEvents$SoundDoseEvent getDoseUpdateEvent(float f, long j) {
        return new AudioServiceEvents$SoundDoseEvent(1, f, j);
    }

    @Override // com.android.server.utils.EventLogger.Event
    public String eventToString() {
        int i = this.mEventType;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "FIXME invalid event type:" + this.mEventType;
                    }
                    return "CSD accumulating: RS2 entered";
                }
                return "CSD reached 500%";
            }
            return String.format(Locale.US, "dose update CSD=%.1f%% total duration=%d", Float.valueOf(this.mFloatValue * 100.0f), Long.valueOf(this.mLongValue));
        }
        return String.format("momentary exposure MEL=%.2f", Float.valueOf(this.mFloatValue));
    }
}
