package android.p008os;

import android.p008os.Parcelable;
import android.util.IntArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.os.BatteryUsageStatsQuery */
/* loaded from: classes3.dex */
public final class BatteryUsageStatsQuery implements Parcelable {
    private static final long DEFAULT_MAX_STATS_AGE_MS = 300000;
    public static final int FLAG_BATTERY_USAGE_STATS_INCLUDE_HISTORY = 2;
    public static final int FLAG_BATTERY_USAGE_STATS_INCLUDE_POWER_MODELS = 4;
    public static final int FLAG_BATTERY_USAGE_STATS_INCLUDE_PROCESS_STATE_DATA = 8;
    public static final int FLAG_BATTERY_USAGE_STATS_INCLUDE_VIRTUAL_UIDS = 16;
    public static final int FLAG_BATTERY_USAGE_STATS_POWER_PROFILE_MODEL = 1;
    private final int mFlags;
    private final long mFromTimestamp;
    private final long mMaxStatsAgeMs;
    private final int[] mPowerComponents;
    private final long mToTimestamp;
    private final int[] mUserIds;
    public static final BatteryUsageStatsQuery DEFAULT = new Builder().build();
    public static final Parcelable.Creator<BatteryUsageStatsQuery> CREATOR = new Parcelable.Creator<BatteryUsageStatsQuery>() { // from class: android.os.BatteryUsageStatsQuery.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatteryUsageStatsQuery createFromParcel(Parcel in) {
            return new BatteryUsageStatsQuery(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatteryUsageStatsQuery[] newArray(int size) {
            return new BatteryUsageStatsQuery[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryUsageStatsQuery$BatteryUsageStatsFlags */
    /* loaded from: classes3.dex */
    public @interface BatteryUsageStatsFlags {
    }

    private BatteryUsageStatsQuery(Builder builder) {
        this.mFlags = builder.mFlags;
        this.mUserIds = builder.mUserIds != null ? builder.mUserIds.toArray() : new int[]{-1};
        this.mMaxStatsAgeMs = builder.mMaxStatsAgeMs;
        this.mFromTimestamp = builder.mFromTimestamp;
        this.mToTimestamp = builder.mToTimestamp;
        this.mPowerComponents = builder.mPowerComponents;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int[] getUserIds() {
        return this.mUserIds;
    }

    public boolean shouldForceUsePowerProfileModel() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isProcessStateDataNeeded() {
        return (this.mFlags & 8) != 0;
    }

    public int[] getPowerComponents() {
        return this.mPowerComponents;
    }

    public long getMaxStatsAge() {
        return this.mMaxStatsAgeMs;
    }

    public long getFromTimestamp() {
        return this.mFromTimestamp;
    }

    public long getToTimestamp() {
        return this.mToTimestamp;
    }

    private BatteryUsageStatsQuery(Parcel in) {
        this.mFlags = in.readInt();
        int[] iArr = new int[in.readInt()];
        this.mUserIds = iArr;
        in.readIntArray(iArr);
        this.mMaxStatsAgeMs = in.readLong();
        this.mFromTimestamp = in.readLong();
        this.mToTimestamp = in.readLong();
        this.mPowerComponents = in.createIntArray();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mUserIds.length);
        dest.writeIntArray(this.mUserIds);
        dest.writeLong(this.mMaxStatsAgeMs);
        dest.writeLong(this.mFromTimestamp);
        dest.writeLong(this.mToTimestamp);
        dest.writeIntArray(this.mPowerComponents);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: android.os.BatteryUsageStatsQuery$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mFlags;
        private long mFromTimestamp;
        private long mMaxStatsAgeMs = 300000;
        private int[] mPowerComponents;
        private long mToTimestamp;
        private IntArray mUserIds;

        public BatteryUsageStatsQuery build() {
            return new BatteryUsageStatsQuery(this);
        }

        public Builder addUser(UserHandle userHandle) {
            if (this.mUserIds == null) {
                this.mUserIds = new IntArray(1);
            }
            this.mUserIds.add(userHandle.getIdentifier());
            return this;
        }

        public Builder includeBatteryHistory() {
            this.mFlags |= 2;
            return this;
        }

        public Builder includeProcessStateData() {
            this.mFlags |= 8;
            return this;
        }

        public Builder powerProfileModeledOnly() {
            this.mFlags |= 1;
            return this;
        }

        public Builder includePowerModels() {
            this.mFlags |= 4;
            return this;
        }

        public Builder includePowerComponents(int[] powerComponents) {
            this.mPowerComponents = powerComponents;
            return this;
        }

        public Builder includeVirtualUids() {
            this.mFlags |= 16;
            return this;
        }

        public Builder aggregateSnapshots(long fromTimestamp, long toTimestamp) {
            this.mFromTimestamp = fromTimestamp;
            this.mToTimestamp = toTimestamp;
            return this;
        }

        public Builder setMaxStatsAgeMs(long maxStatsAgeMs) {
            this.mMaxStatsAgeMs = maxStatsAgeMs;
            return this;
        }
    }
}
