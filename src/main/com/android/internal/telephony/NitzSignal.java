package com.android.internal.telephony;

import android.app.time.UnixEpochTime;
import java.time.Duration;
import java.util.Objects;
/* loaded from: classes.dex */
public final class NitzSignal {
    private final long mAgeMillis;
    private final NitzData mNitzData;
    private final long mReceiptElapsedMillis;

    public NitzSignal(long j, NitzData nitzData, long j2) {
        this.mReceiptElapsedMillis = j;
        Objects.requireNonNull(nitzData);
        this.mNitzData = nitzData;
        if (j2 < 0) {
            throw new IllegalArgumentException("ageMillis < 0");
        }
        this.mAgeMillis = j2;
    }

    public long getReceiptElapsedRealtimeMillis() {
        return this.mReceiptElapsedMillis;
    }

    public NitzData getNitzData() {
        return this.mNitzData;
    }

    public long getAgeMillis() {
        return this.mAgeMillis;
    }

    public long getAgeAdjustedElapsedRealtimeMillis() {
        return this.mReceiptElapsedMillis - this.mAgeMillis;
    }

    public UnixEpochTime createTimeSignal() {
        return new UnixEpochTime(getAgeAdjustedElapsedRealtimeMillis(), getNitzData().getCurrentTimeInMillis());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || NitzSignal.class != obj.getClass()) {
            return false;
        }
        NitzSignal nitzSignal = (NitzSignal) obj;
        return this.mReceiptElapsedMillis == nitzSignal.mReceiptElapsedMillis && this.mAgeMillis == nitzSignal.mAgeMillis && this.mNitzData.equals(nitzSignal.mNitzData);
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mReceiptElapsedMillis), this.mNitzData, Long.valueOf(this.mAgeMillis));
    }

    public String toString() {
        return "NitzSignal{mReceiptElapsedMillis=" + Duration.ofMillis(this.mReceiptElapsedMillis) + ", mNitzData=" + this.mNitzData + ", mAgeMillis=" + this.mAgeMillis + '}';
    }
}
