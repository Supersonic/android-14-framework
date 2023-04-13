package android.net.sntp;

import android.text.TextUtils;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;
/* loaded from: classes2.dex */
public final class Timestamp64 {
    static final long MAX_SECONDS_IN_ERA = 4294967295L;
    static final int NANOS_PER_SECOND = 1000000000;
    static final long OFFSET_1900_TO_1970 = 2208988800L;
    static final long SECONDS_IN_ERA = 4294967296L;
    static final int SUB_MILLIS_BITS_TO_RANDOMIZE = 22;
    public static final Timestamp64 ZERO = fromComponents(0, 0);
    private final long mEraSeconds;
    private final int mFractionBits;

    public static Timestamp64 fromComponents(long eraSeconds, int fractionBits) {
        return new Timestamp64(eraSeconds, fractionBits);
    }

    public static Timestamp64 fromString(String string) {
        if (string.length() != 17 || string.charAt(8) != '.') {
            throw new IllegalArgumentException(string);
        }
        String eraSecondsString = string.substring(0, 8);
        String fractionString = string.substring(9);
        long eraSeconds = Long.parseLong(eraSecondsString, 16);
        long fractionBitsAsLong = Long.parseLong(fractionString, 16);
        if (fractionBitsAsLong < 0 || fractionBitsAsLong > 4294967295L) {
            throw new IllegalArgumentException("Invalid fractionBits:" + fractionString);
        }
        return new Timestamp64(eraSeconds, (int) fractionBitsAsLong);
    }

    public static Timestamp64 fromInstant(Instant instant) {
        long ntpEraSeconds = instant.getEpochSecond() + OFFSET_1900_TO_1970;
        if (ntpEraSeconds < 0) {
            ntpEraSeconds = 4294967296L - ((-ntpEraSeconds) % 4294967296L);
        }
        long nanos = instant.getNano();
        int fractionBits = nanosToFractionBits(nanos);
        return new Timestamp64(ntpEraSeconds % 4294967296L, fractionBits);
    }

    private Timestamp64(long eraSeconds, int fractionBits) {
        if (eraSeconds < 0 || eraSeconds > 4294967295L) {
            throw new IllegalArgumentException("Invalid parameters. seconds=" + eraSeconds + ", fraction=" + fractionBits);
        }
        this.mEraSeconds = eraSeconds;
        this.mFractionBits = fractionBits;
    }

    public long getEraSeconds() {
        return this.mEraSeconds;
    }

    public int getFractionBits() {
        return this.mFractionBits;
    }

    public String toString() {
        return TextUtils.formatSimple("%08x.%08x", Long.valueOf(this.mEraSeconds), Integer.valueOf(this.mFractionBits));
    }

    public Instant toInstant(int ntpEra) {
        long secondsSinceEpoch = this.mEraSeconds - OFFSET_1900_TO_1970;
        long secondsSinceEpoch2 = secondsSinceEpoch + (ntpEra * 4294967296L);
        int nanos = fractionBitsToNanos(this.mFractionBits);
        return Instant.ofEpochSecond(secondsSinceEpoch2, nanos);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Timestamp64 that = (Timestamp64) o;
        if (this.mEraSeconds == that.mEraSeconds && this.mFractionBits == that.mFractionBits) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mEraSeconds), Integer.valueOf(this.mFractionBits));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int fractionBitsToNanos(int fractionBits) {
        long fractionBitsLong = fractionBits & 4294967295L;
        return (int) ((1000000000 * fractionBitsLong) >>> 32);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int nanosToFractionBits(long nanos) {
        if (nanos <= 1000000000) {
            return (int) ((nanos << 32) / 1000000000);
        }
        throw new IllegalArgumentException();
    }

    public Timestamp64 randomizeSubMillis(Random random) {
        int randomizedFractionBits = randomizeLowestBits(random, this.mFractionBits, 22);
        return new Timestamp64(this.mEraSeconds, randomizedFractionBits);
    }

    public static int randomizeLowestBits(Random random, int value, int bitsToRandomize) {
        if (bitsToRandomize < 1 || bitsToRandomize >= 32) {
            throw new IllegalArgumentException(Integer.toString(bitsToRandomize));
        }
        int upperBitMask = (-1) << bitsToRandomize;
        int lowerBitMask = ~upperBitMask;
        int randomValue = random.nextInt();
        return (value & upperBitMask) | (randomValue & lowerBitMask);
    }
}
