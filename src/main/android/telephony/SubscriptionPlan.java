package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Range;
import android.util.RecurrenceRule;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SubscriptionPlan implements Parcelable {
    public static final long BYTES_UNKNOWN = -1;
    public static final long BYTES_UNLIMITED = Long.MAX_VALUE;
    public static final Parcelable.Creator<SubscriptionPlan> CREATOR = new Parcelable.Creator<SubscriptionPlan>() { // from class: android.telephony.SubscriptionPlan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubscriptionPlan createFromParcel(Parcel source) {
            return new SubscriptionPlan(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubscriptionPlan[] newArray(int size) {
            return new SubscriptionPlan[size];
        }
    };
    public static final int LIMIT_BEHAVIOR_BILLED = 1;
    public static final int LIMIT_BEHAVIOR_DISABLED = 0;
    public static final int LIMIT_BEHAVIOR_THROTTLED = 2;
    public static final int LIMIT_BEHAVIOR_UNKNOWN = -1;
    public static final long TIME_UNKNOWN = -1;
    private final RecurrenceRule cycleRule;
    private int dataLimitBehavior;
    private long dataLimitBytes;
    private long dataUsageBytes;
    private long dataUsageTime;
    private int[] networkTypes;
    private CharSequence summary;
    private CharSequence title;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LimitBehavior {
    }

    private SubscriptionPlan(RecurrenceRule cycleRule) {
        this.dataLimitBytes = -1L;
        this.dataLimitBehavior = -1;
        this.dataUsageBytes = -1L;
        this.dataUsageTime = -1L;
        this.cycleRule = (RecurrenceRule) Preconditions.checkNotNull(cycleRule);
        this.networkTypes = Arrays.copyOf(TelephonyManager.getAllNetworkTypes(), TelephonyManager.getAllNetworkTypes().length);
    }

    private SubscriptionPlan(Parcel source) {
        this.dataLimitBytes = -1L;
        this.dataLimitBehavior = -1;
        this.dataUsageBytes = -1L;
        this.dataUsageTime = -1L;
        this.cycleRule = (RecurrenceRule) source.readParcelable(null, RecurrenceRule.class);
        this.title = source.readCharSequence();
        this.summary = source.readCharSequence();
        this.dataLimitBytes = source.readLong();
        this.dataLimitBehavior = source.readInt();
        this.dataUsageBytes = source.readLong();
        this.dataUsageTime = source.readLong();
        this.networkTypes = source.createIntArray();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.cycleRule, flags);
        dest.writeCharSequence(this.title);
        dest.writeCharSequence(this.summary);
        dest.writeLong(this.dataLimitBytes);
        dest.writeInt(this.dataLimitBehavior);
        dest.writeLong(this.dataUsageBytes);
        dest.writeLong(this.dataUsageTime);
        dest.writeIntArray(this.networkTypes);
    }

    public String toString() {
        return "SubscriptionPlan{cycleRule=" + this.cycleRule + " title=" + this.title + " summary=" + this.summary + " dataLimitBytes=" + this.dataLimitBytes + " dataLimitBehavior=" + this.dataLimitBehavior + " dataUsageBytes=" + this.dataUsageBytes + " dataUsageTime=" + this.dataUsageTime + " networkTypes=" + Arrays.toString(this.networkTypes) + "}";
    }

    public int hashCode() {
        return Objects.hash(this.cycleRule, this.title, this.summary, Long.valueOf(this.dataLimitBytes), Integer.valueOf(this.dataLimitBehavior), Long.valueOf(this.dataUsageBytes), Long.valueOf(this.dataUsageTime), Integer.valueOf(Arrays.hashCode(this.networkTypes)));
    }

    public boolean equals(Object obj) {
        if (obj instanceof SubscriptionPlan) {
            SubscriptionPlan other = (SubscriptionPlan) obj;
            return Objects.equals(this.cycleRule, other.cycleRule) && Objects.equals(this.title, other.title) && Objects.equals(this.summary, other.summary) && this.dataLimitBytes == other.dataLimitBytes && this.dataLimitBehavior == other.dataLimitBehavior && this.dataUsageBytes == other.dataUsageBytes && this.dataUsageTime == other.dataUsageTime && Arrays.equals(this.networkTypes, other.networkTypes);
        }
        return false;
    }

    public RecurrenceRule getCycleRule() {
        return this.cycleRule;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public CharSequence getSummary() {
        return this.summary;
    }

    public long getDataLimitBytes() {
        return this.dataLimitBytes;
    }

    public int getDataLimitBehavior() {
        return this.dataLimitBehavior;
    }

    public long getDataUsageBytes() {
        return this.dataUsageBytes;
    }

    public long getDataUsageTime() {
        return this.dataUsageTime;
    }

    public int[] getNetworkTypes() {
        int[] iArr = this.networkTypes;
        return Arrays.copyOf(iArr, iArr.length);
    }

    public Iterator<Range<ZonedDateTime>> cycleIterator() {
        return this.cycleRule.cycleIterator();
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private final SubscriptionPlan plan;

        public Builder(ZonedDateTime start, ZonedDateTime end, Period period) {
            this.plan = new SubscriptionPlan(new RecurrenceRule(start, end, period));
        }

        public static Builder createNonrecurring(ZonedDateTime start, ZonedDateTime end) {
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("End " + end + " isn't after start " + start);
            }
            return new Builder(start, end, null);
        }

        public static Builder createRecurring(ZonedDateTime start, Period period) {
            if (period.isZero() || period.isNegative()) {
                throw new IllegalArgumentException("Period " + period + " must be positive");
            }
            return new Builder(start, null, period);
        }

        @SystemApi
        @Deprecated
        public static Builder createRecurringMonthly(ZonedDateTime start) {
            return new Builder(start, null, Period.ofMonths(1));
        }

        @SystemApi
        @Deprecated
        public static Builder createRecurringWeekly(ZonedDateTime start) {
            return new Builder(start, null, Period.ofDays(7));
        }

        @SystemApi
        @Deprecated
        public static Builder createRecurringDaily(ZonedDateTime start) {
            return new Builder(start, null, Period.ofDays(1));
        }

        public SubscriptionPlan build() {
            return this.plan;
        }

        public Builder setTitle(CharSequence title) {
            this.plan.title = title;
            return this;
        }

        public Builder setSummary(CharSequence summary) {
            this.plan.summary = summary;
            return this;
        }

        public Builder setDataLimit(long dataLimitBytes, int dataLimitBehavior) {
            if (dataLimitBytes < 0) {
                throw new IllegalArgumentException("Limit bytes must be positive");
            }
            if (dataLimitBehavior < 0) {
                throw new IllegalArgumentException("Limit behavior must be defined");
            }
            this.plan.dataLimitBytes = dataLimitBytes;
            this.plan.dataLimitBehavior = dataLimitBehavior;
            return this;
        }

        public Builder setDataUsage(long dataUsageBytes, long dataUsageTime) {
            if (dataUsageBytes < 0) {
                throw new IllegalArgumentException("Usage bytes must be positive");
            }
            if (dataUsageTime < 0) {
                throw new IllegalArgumentException("Usage time must be positive");
            }
            this.plan.dataUsageBytes = dataUsageBytes;
            this.plan.dataUsageTime = dataUsageTime;
            return this;
        }

        public Builder setNetworkTypes(int[] networkTypes) {
            this.plan.networkTypes = Arrays.copyOf(networkTypes, networkTypes.length);
            return this;
        }

        public Builder resetNetworkTypes() {
            this.plan.networkTypes = Arrays.copyOf(TelephonyManager.getAllNetworkTypes(), TelephonyManager.getAllNetworkTypes().length);
            return this;
        }
    }
}
