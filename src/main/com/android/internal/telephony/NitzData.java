package com.android.internal.telephony;

import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* loaded from: classes.dex */
public final class NitzData {
    private static final Pattern NITZ_SPLIT_PATTERN = Pattern.compile("[/:,+-]");
    private final long mCurrentTimeMillis;
    private final Integer mDstOffset;
    private final TimeZone mEmulatorHostTimeZone;
    private final String mOriginalString;
    private final int mZoneOffset;

    private NitzData(String str, int i, Integer num, long j, TimeZone timeZone) {
        if (str == null) {
            throw new NullPointerException("originalString==null");
        }
        this.mOriginalString = str;
        this.mZoneOffset = i;
        this.mDstOffset = num;
        this.mCurrentTimeMillis = j;
        this.mEmulatorHostTimeZone = timeZone;
    }

    public static NitzData parse(String str) {
        try {
            String[] split = NITZ_SPLIT_PATTERN.split(str);
            int parseInt = Integer.parseInt(split[0]);
            int i = 1;
            if (parseInt < 1 || parseInt > 99) {
                throw new DateTimeException("Invalid NITZ year == 0");
            }
            long epochMilli = LocalDateTime.of(parseInt + 2000, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5])).toInstant(ZoneOffset.UTC).toEpochMilli();
            boolean z = str.indexOf(45) == -1;
            int parseInt2 = Integer.parseInt(split[6]);
            if (!z) {
                i = -1;
            }
            int i2 = i * parseInt2 * 900000;
            Integer valueOf = split.length >= 8 ? Integer.valueOf(Integer.parseInt(split[7])) : null;
            return new NitzData(str, i2, valueOf != null ? Integer.valueOf(valueOf.intValue() * 3600000) : null, epochMilli, split.length >= 9 ? TimeZone.getTimeZone(split[8].replace('!', '/')) : null);
        } catch (RuntimeException e) {
            Rlog.e("SST", "NITZ: Parsing NITZ time " + str + " ex=" + e);
            return null;
        }
    }

    public static NitzData createForTests(int i, Integer num, long j, TimeZone timeZone) {
        return new NitzData("Test data", i, num, j, timeZone);
    }

    public long getCurrentTimeInMillis() {
        return this.mCurrentTimeMillis;
    }

    public int getLocalOffsetMillis() {
        return this.mZoneOffset;
    }

    public Integer getDstAdjustmentMillis() {
        return this.mDstOffset;
    }

    public boolean isDst() {
        Integer num = this.mDstOffset;
        return (num == null || num.intValue() == 0) ? false : true;
    }

    public TimeZone getEmulatorHostTimeZone() {
        return this.mEmulatorHostTimeZone;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || NitzData.class != obj.getClass()) {
            return false;
        }
        NitzData nitzData = (NitzData) obj;
        if (this.mZoneOffset == nitzData.mZoneOffset && this.mCurrentTimeMillis == nitzData.mCurrentTimeMillis && this.mOriginalString.equals(nitzData.mOriginalString) && Objects.equals(this.mDstOffset, nitzData.mDstOffset)) {
            return Objects.equals(this.mEmulatorHostTimeZone, nitzData.mEmulatorHostTimeZone);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = ((this.mOriginalString.hashCode() * 31) + this.mZoneOffset) * 31;
        Integer num = this.mDstOffset;
        int hashCode2 = (((hashCode + (num != null ? num.hashCode() : 0)) * 31) + Long.hashCode(this.mCurrentTimeMillis)) * 31;
        TimeZone timeZone = this.mEmulatorHostTimeZone;
        return hashCode2 + (timeZone != null ? timeZone.hashCode() : 0);
    }

    public String toString() {
        return "NitzData{mOriginalString=" + this.mOriginalString + ", mZoneOffset=" + this.mZoneOffset + ", mDstOffset=" + this.mDstOffset + ", mCurrentTimeMillis=" + this.mCurrentTimeMillis + ", mEmulatorHostTimeZone=" + this.mEmulatorHostTimeZone + '}';
    }
}
