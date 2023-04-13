package android.timezone;

import android.icu.util.TimeZone;
import com.android.i18n.timezone.CountryTimeZones;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CountryTimeZones {
    private final com.android.i18n.timezone.CountryTimeZones mDelegate;

    /* loaded from: classes3.dex */
    public static final class TimeZoneMapping {
        private CountryTimeZones.TimeZoneMapping mDelegate;

        TimeZoneMapping(CountryTimeZones.TimeZoneMapping delegate) {
            this.mDelegate = (CountryTimeZones.TimeZoneMapping) Objects.requireNonNull(delegate);
        }

        public String getTimeZoneId() {
            return this.mDelegate.getTimeZoneId();
        }

        public TimeZone getTimeZone() {
            return this.mDelegate.getTimeZone();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TimeZoneMapping that = (TimeZoneMapping) o;
            return this.mDelegate.equals(that.mDelegate);
        }

        public int hashCode() {
            return this.mDelegate.hashCode();
        }

        public String toString() {
            return this.mDelegate.toString();
        }
    }

    /* loaded from: classes3.dex */
    public static final class OffsetResult {
        private final boolean mIsOnlyMatch;
        private final TimeZone mTimeZone;

        public OffsetResult(TimeZone timeZone, boolean isOnlyMatch) {
            this.mTimeZone = (TimeZone) Objects.requireNonNull(timeZone);
            this.mIsOnlyMatch = isOnlyMatch;
        }

        public TimeZone getTimeZone() {
            return this.mTimeZone;
        }

        public boolean isOnlyMatch() {
            return this.mIsOnlyMatch;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OffsetResult that = (OffsetResult) o;
            if (this.mIsOnlyMatch == that.mIsOnlyMatch && this.mTimeZone.getID().equals(that.mTimeZone.getID())) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mTimeZone, Boolean.valueOf(this.mIsOnlyMatch));
        }

        public String toString() {
            return "OffsetResult{mTimeZone(ID)=" + this.mTimeZone.getID() + ", mIsOnlyMatch=" + this.mIsOnlyMatch + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CountryTimeZones(com.android.i18n.timezone.CountryTimeZones delegate) {
        this.mDelegate = delegate;
    }

    public boolean matchesCountryCode(String countryIso) {
        return this.mDelegate.matchesCountryCode(countryIso);
    }

    public String getDefaultTimeZoneId() {
        return this.mDelegate.getDefaultTimeZoneId();
    }

    public TimeZone getDefaultTimeZone() {
        return this.mDelegate.getDefaultTimeZone();
    }

    public boolean isDefaultTimeZoneBoosted() {
        return this.mDelegate.isDefaultTimeZoneBoosted();
    }

    public boolean hasUtcZone(long whenMillis) {
        return this.mDelegate.hasUtcZone(whenMillis);
    }

    public OffsetResult lookupByOffsetWithBias(long whenMillis, TimeZone bias, int totalOffsetMillis, boolean isDst) {
        CountryTimeZones.OffsetResult delegateOffsetResult = this.mDelegate.lookupByOffsetWithBias(whenMillis, bias, totalOffsetMillis, isDst);
        if (delegateOffsetResult == null) {
            return null;
        }
        return new OffsetResult(delegateOffsetResult.getTimeZone(), delegateOffsetResult.isOnlyMatch());
    }

    public OffsetResult lookupByOffsetWithBias(long whenMillis, TimeZone bias, int totalOffsetMillis) {
        CountryTimeZones.OffsetResult delegateOffsetResult = this.mDelegate.lookupByOffsetWithBias(whenMillis, bias, totalOffsetMillis);
        if (delegateOffsetResult == null) {
            return null;
        }
        return new OffsetResult(delegateOffsetResult.getTimeZone(), delegateOffsetResult.isOnlyMatch());
    }

    public List<TimeZoneMapping> getEffectiveTimeZoneMappingsAt(long whenMillis) {
        List<CountryTimeZones.TimeZoneMapping> delegateList = this.mDelegate.getEffectiveTimeZoneMappingsAt(whenMillis);
        List<TimeZoneMapping> toReturn = new ArrayList<>(delegateList.size());
        for (CountryTimeZones.TimeZoneMapping delegateMapping : delegateList) {
            toReturn.add(new TimeZoneMapping(delegateMapping));
        }
        return Collections.unmodifiableList(toReturn);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CountryTimeZones that = (CountryTimeZones) o;
        return this.mDelegate.equals(that.mDelegate);
    }

    public int hashCode() {
        return Objects.hash(this.mDelegate);
    }

    public String toString() {
        return this.mDelegate.toString();
    }
}
