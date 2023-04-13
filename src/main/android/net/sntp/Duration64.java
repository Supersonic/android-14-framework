package android.net.sntp;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.time.Duration;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Duration64 {
    public static final Duration64 ZERO = new Duration64(0);
    private final long mBits;

    private Duration64(long bits) {
        this.mBits = bits;
    }

    public static Duration64 between(Timestamp64 startInclusive, Timestamp64 endExclusive) {
        long oneBits = (startInclusive.getEraSeconds() << 32) | (startInclusive.getFractionBits() & 4294967295L);
        long twoBits = (endExclusive.getEraSeconds() << 32) | (endExclusive.getFractionBits() & 4294967295L);
        long resultBits = twoBits - oneBits;
        return new Duration64(resultBits);
    }

    public Duration plus(Duration64 other) {
        return toDuration().plus(other.toDuration());
    }

    public static Duration64 fromDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < -2147483648L || seconds > 2147483647L) {
            throw new IllegalArgumentException();
        }
        long bits = (seconds << 32) | (Timestamp64.nanosToFractionBits(duration.getNano()) & 4294967295L);
        return new Duration64(bits);
    }

    public Duration toDuration() {
        int seconds = getSeconds();
        int nanos = getNanos();
        return Duration.ofSeconds(seconds, nanos);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Duration64 that = (Duration64) o;
        if (this.mBits == that.mBits) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mBits));
    }

    public String toString() {
        Duration duration = toDuration();
        return Long.toHexString(this.mBits) + NavigationBarInflaterView.KEY_CODE_START + duration.getSeconds() + "s " + duration.getNano() + "ns)";
    }

    public int getSeconds() {
        return (int) (this.mBits >> 32);
    }

    public int getNanos() {
        return Timestamp64.fractionBitsToNanos((int) (this.mBits & 4294967295L));
    }
}
