package android.service.timezone;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class TimeZoneProviderSuggestion implements Parcelable {
    public static final Parcelable.Creator<TimeZoneProviderSuggestion> CREATOR = new Parcelable.Creator<TimeZoneProviderSuggestion>() { // from class: android.service.timezone.TimeZoneProviderSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneProviderSuggestion createFromParcel(Parcel in) {
            ArrayList<String> timeZoneIds = in.readArrayList(null, String.class);
            long elapsedRealtimeMillis = in.readLong();
            return new TimeZoneProviderSuggestion(timeZoneIds, elapsedRealtimeMillis);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneProviderSuggestion[] newArray(int size) {
            return new TimeZoneProviderSuggestion[size];
        }
    };
    private final long mElapsedRealtimeMillis;
    private final List<String> mTimeZoneIds;

    private TimeZoneProviderSuggestion(List<String> timeZoneIds, long elapsedRealtimeMillis) {
        this.mTimeZoneIds = immutableList(timeZoneIds);
        this.mElapsedRealtimeMillis = elapsedRealtimeMillis;
    }

    public long getElapsedRealtimeMillis() {
        return this.mElapsedRealtimeMillis;
    }

    public List<String> getTimeZoneIds() {
        return this.mTimeZoneIds;
    }

    public String toString() {
        return "TimeZoneProviderSuggestion{mTimeZoneIds=" + this.mTimeZoneIds + ", mElapsedRealtimeMillis=" + this.mElapsedRealtimeMillis + NavigationBarInflaterView.KEY_CODE_START + Duration.ofMillis(this.mElapsedRealtimeMillis) + NavigationBarInflaterView.KEY_CODE_END + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeList(this.mTimeZoneIds);
        parcel.writeLong(this.mElapsedRealtimeMillis);
    }

    public boolean isEquivalentTo(TimeZoneProviderSuggestion other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.mTimeZoneIds.equals(other.mTimeZoneIds);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeZoneProviderSuggestion that = (TimeZoneProviderSuggestion) o;
        if (this.mElapsedRealtimeMillis == that.mElapsedRealtimeMillis && this.mTimeZoneIds.equals(that.mTimeZoneIds)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mTimeZoneIds, Long.valueOf(this.mElapsedRealtimeMillis));
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private List<String> mTimeZoneIds = Collections.emptyList();
        private long mElapsedRealtimeMillis = SystemClock.elapsedRealtime();

        public Builder setTimeZoneIds(List<String> timeZoneIds) {
            this.mTimeZoneIds = (List) Objects.requireNonNull(timeZoneIds);
            return this;
        }

        public Builder setElapsedRealtimeMillis(long time) {
            this.mElapsedRealtimeMillis = time;
            return this;
        }

        public TimeZoneProviderSuggestion build() {
            return new TimeZoneProviderSuggestion(this.mTimeZoneIds, this.mElapsedRealtimeMillis);
        }
    }

    private static List<String> immutableList(List<String> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList(list));
    }
}
