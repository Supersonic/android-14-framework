package com.android.server.twilight;

import android.text.format.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
/* loaded from: classes2.dex */
public final class TwilightState {
    public final long mSunriseTimeMillis;
    public final long mSunsetTimeMillis;

    public TwilightState(long j, long j2) {
        this.mSunriseTimeMillis = j;
        this.mSunsetTimeMillis = j2;
    }

    public long sunriseTimeMillis() {
        return this.mSunriseTimeMillis;
    }

    public LocalDateTime sunrise() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.mSunriseTimeMillis), TimeZone.getDefault().toZoneId());
    }

    public long sunsetTimeMillis() {
        return this.mSunsetTimeMillis;
    }

    public LocalDateTime sunset() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.mSunsetTimeMillis), TimeZone.getDefault().toZoneId());
    }

    public boolean isNight() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis >= this.mSunsetTimeMillis && currentTimeMillis < this.mSunriseTimeMillis;
    }

    public boolean equals(Object obj) {
        return (obj instanceof TwilightState) && equals((TwilightState) obj);
    }

    public boolean equals(TwilightState twilightState) {
        return twilightState != null && this.mSunriseTimeMillis == twilightState.mSunriseTimeMillis && this.mSunsetTimeMillis == twilightState.mSunsetTimeMillis;
    }

    public int hashCode() {
        return Long.hashCode(this.mSunsetTimeMillis) ^ Long.hashCode(this.mSunriseTimeMillis);
    }

    public String toString() {
        return "TwilightState { sunrise=" + ((Object) DateFormat.format("MM-dd HH:mm", this.mSunriseTimeMillis)) + " sunset=" + ((Object) DateFormat.format("MM-dd HH:mm", this.mSunsetTimeMillis)) + " }";
    }
}
