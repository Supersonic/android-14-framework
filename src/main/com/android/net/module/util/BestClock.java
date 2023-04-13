package com.android.net.module.util;

import android.util.Log;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
/* loaded from: classes5.dex */
public final class BestClock extends Clock {
    private static final String TAG = "BestClock";
    private final Clock[] mClocks;
    private final ZoneId mZone;

    public BestClock(ZoneId zone, Clock... clocks) {
        this.mZone = zone;
        this.mClocks = clocks;
    }

    @Override // java.time.Clock
    public long millis() {
        Clock[] clockArr = this.mClocks;
        int length = clockArr.length;
        for (int i = 0; i < length; i++) {
            Clock clock = clockArr[i];
            try {
                return clock.millis();
            } catch (DateTimeException e) {
                Log.m104w(TAG, e.toString());
            }
        }
        throw new DateTimeException("No clocks in " + Arrays.toString(this.mClocks) + " were able to provide time");
    }

    @Override // java.time.Clock
    public ZoneId getZone() {
        return this.mZone;
    }

    @Override // java.time.Clock
    public Clock withZone(ZoneId zone) {
        return new BestClock(zone, this.mClocks);
    }

    @Override // java.time.Clock
    public Instant instant() {
        return Instant.ofEpochMilli(millis());
    }
}
