package com.android.internal.telephony.nitz;

import android.icu.util.TimeZone;
import android.text.TextUtils;
import android.timezone.CountryTimeZones;
import android.timezone.TimeZoneFinder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.NitzData;
import java.util.List;
import java.util.Objects;
@VisibleForTesting
/* loaded from: classes.dex */
public final class TimeZoneLookupHelper {
    private CountryTimeZones mLastCountryTimeZones;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class CountryResult {
        public static final int QUALITY_DEFAULT_BOOSTED = 2;
        public static final int QUALITY_MULTIPLE_ZONES_DIFFERENT_OFFSETS = 4;
        public static final int QUALITY_MULTIPLE_ZONES_SAME_OFFSET = 3;
        public static final int QUALITY_SINGLE_ZONE = 1;
        private final String mDebugInfo;
        public final int quality;
        public final String zoneId;

        public CountryResult(String str, int i, String str2) {
            Objects.requireNonNull(str);
            this.zoneId = str;
            this.quality = i;
            this.mDebugInfo = str2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || CountryResult.class != obj.getClass()) {
                return false;
            }
            CountryResult countryResult = (CountryResult) obj;
            return this.quality == countryResult.quality && this.zoneId.equals(countryResult.zoneId);
        }

        public int hashCode() {
            return Objects.hash(this.zoneId, Integer.valueOf(this.quality));
        }

        public String toString() {
            return "CountryResult{zoneId='" + this.zoneId + "', quality=" + this.quality + ", mDebugInfo=" + this.mDebugInfo + '}';
        }
    }

    @VisibleForTesting
    public CountryTimeZones.OffsetResult lookupByNitzCountry(NitzData nitzData, String str) {
        CountryTimeZones countryTimeZones = getCountryTimeZones(str);
        if (countryTimeZones == null) {
            return null;
        }
        TimeZone timeZone = TimeZone.getDefault();
        Integer dstAdjustmentMillis = nitzData.getDstAdjustmentMillis();
        if (dstAdjustmentMillis == null) {
            return countryTimeZones.lookupByOffsetWithBias(nitzData.getCurrentTimeInMillis(), timeZone, nitzData.getLocalOffsetMillis());
        }
        return countryTimeZones.lookupByOffsetWithBias(nitzData.getCurrentTimeInMillis(), timeZone, nitzData.getLocalOffsetMillis(), dstAdjustmentMillis.intValue() != 0);
    }

    @VisibleForTesting
    public CountryTimeZones.OffsetResult lookupByNitz(NitzData nitzData) {
        Boolean valueOf;
        int localOffsetMillis = nitzData.getLocalOffsetMillis();
        long currentTimeInMillis = nitzData.getCurrentTimeInMillis();
        Integer dstAdjustmentMillis = nitzData.getDstAdjustmentMillis();
        if (dstAdjustmentMillis == null) {
            valueOf = null;
        } else {
            valueOf = Boolean.valueOf(dstAdjustmentMillis.intValue() != 0);
        }
        CountryTimeZones.OffsetResult lookupByInstantOffsetDst = lookupByInstantOffsetDst(currentTimeInMillis, localOffsetMillis, valueOf);
        return (lookupByInstantOffsetDst != null || valueOf == null) ? lookupByInstantOffsetDst : lookupByInstantOffsetDst(currentTimeInMillis, localOffsetMillis, null);
    }

    @VisibleForTesting
    public CountryResult lookupByCountry(String str, long j) {
        TimeZone defaultTimeZone;
        String str2;
        int i;
        CountryTimeZones countryTimeZones = getCountryTimeZones(str);
        if (countryTimeZones == null || (defaultTimeZone = countryTimeZones.getDefaultTimeZone()) == null) {
            return null;
        }
        if (countryTimeZones.isDefaultTimeZoneBoosted()) {
            i = 2;
            str2 = "Country default is boosted";
        } else {
            List effectiveTimeZoneMappingsAt = countryTimeZones.getEffectiveTimeZoneMappingsAt(j);
            if (effectiveTimeZoneMappingsAt.isEmpty()) {
                str2 = "No effective time zones found at whenMillis=" + j;
                i = 4;
            } else if (effectiveTimeZoneMappingsAt.size() == 1) {
                str2 = "One effective time zone found at whenMillis=" + j;
                i = 1;
            } else {
                boolean countryUsesDifferentOffsets = countryUsesDifferentOffsets(j, effectiveTimeZoneMappingsAt, defaultTimeZone);
                int i2 = countryUsesDifferentOffsets ? 4 : 3;
                str2 = "countryUsesDifferentOffsets=" + countryUsesDifferentOffsets + " at whenMillis=" + j;
                i = i2;
            }
        }
        return new CountryResult(defaultTimeZone.getID(), i, str2);
    }

    private static boolean countryUsesDifferentOffsets(long j, List<CountryTimeZones.TimeZoneMapping> list, TimeZone timeZone) {
        String id = timeZone.getID();
        int offset = timeZone.getOffset(j);
        for (CountryTimeZones.TimeZoneMapping timeZoneMapping : list) {
            if (!timeZoneMapping.getTimeZoneId().equals(id) && offset != timeZoneMapping.getTimeZone().getOffset(j)) {
                return true;
            }
        }
        return false;
    }

    private static CountryTimeZones.OffsetResult lookupByInstantOffsetDst(long j, int i, Boolean bool) {
        String[] availableIDs = java.util.TimeZone.getAvailableIDs();
        int length = availableIDs.length;
        boolean z = false;
        TimeZone timeZone = null;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                z = true;
                break;
            }
            TimeZone frozenTimeZone = TimeZone.getFrozenTimeZone(availableIDs[i2]);
            if (offsetMatchesAtTime(frozenTimeZone, i, bool, j)) {
                if (timeZone != null) {
                    break;
                }
                timeZone = frozenTimeZone;
            }
            i2++;
        }
        if (timeZone == null) {
            return null;
        }
        return new CountryTimeZones.OffsetResult(timeZone, z);
    }

    private static boolean offsetMatchesAtTime(TimeZone timeZone, int i, Boolean bool, long j) {
        int[] iArr = new int[2];
        timeZone.getOffset(j, false, iArr);
        if (i != iArr[0] + iArr[1]) {
            return false;
        }
        if (bool != null) {
            if (bool.booleanValue() != (iArr[1] != 0)) {
                return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    public boolean countryUsesUtc(String str, long j) {
        CountryTimeZones countryTimeZones;
        return (TextUtils.isEmpty(str) || (countryTimeZones = getCountryTimeZones(str)) == null || !countryTimeZones.hasUtcZone(j)) ? false : true;
    }

    private CountryTimeZones getCountryTimeZones(String str) {
        Objects.requireNonNull(str);
        synchronized (this) {
            CountryTimeZones countryTimeZones = this.mLastCountryTimeZones;
            if (countryTimeZones != null && countryTimeZones.matchesCountryCode(str)) {
                return this.mLastCountryTimeZones;
            }
            CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
            if (lookupCountryTimeZones != null) {
                this.mLastCountryTimeZones = lookupCountryTimeZones;
            }
            return lookupCountryTimeZones;
        }
    }
}
