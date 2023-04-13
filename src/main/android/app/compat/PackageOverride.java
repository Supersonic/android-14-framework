package android.app.compat;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class PackageOverride {
    public static final int VALUE_DISABLED = 2;
    public static final int VALUE_ENABLED = 1;
    public static final int VALUE_UNDEFINED = 0;
    private final boolean mEnabled;
    private final long mMaxVersionCode;
    private final long mMinVersionCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface EvaluatedOverride {
    }

    private PackageOverride(long minVersionCode, long maxVersionCode, boolean enabled) {
        this.mMinVersionCode = minVersionCode;
        this.mMaxVersionCode = maxVersionCode;
        this.mEnabled = enabled;
    }

    public int evaluate(long versionCode) {
        if (versionCode < this.mMinVersionCode || versionCode > this.mMaxVersionCode) {
            return 0;
        }
        return this.mEnabled ? 1 : 2;
    }

    public int evaluateForAllVersions() {
        if (this.mMinVersionCode == Long.MIN_VALUE && this.mMaxVersionCode == Long.MAX_VALUE) {
            return this.mEnabled ? 1 : 2;
        }
        return 0;
    }

    public long getMinVersionCode() {
        return this.mMinVersionCode;
    }

    public long getMaxVersionCode() {
        return this.mMaxVersionCode;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void writeToParcel(Parcel dest) {
        dest.writeLong(this.mMinVersionCode);
        dest.writeLong(this.mMaxVersionCode);
        dest.writeBoolean(this.mEnabled);
    }

    public static PackageOverride createFromParcel(Parcel in) {
        return new PackageOverride(in.readLong(), in.readLong(), in.readBoolean());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PackageOverride that = (PackageOverride) o;
        if (this.mMinVersionCode == that.mMinVersionCode && this.mMaxVersionCode == that.mMaxVersionCode && this.mEnabled == that.mEnabled) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mMinVersionCode), Long.valueOf(this.mMaxVersionCode), Boolean.valueOf(this.mEnabled));
    }

    public String toString() {
        long j = this.mMinVersionCode;
        if (j == Long.MIN_VALUE && this.mMaxVersionCode == Long.MAX_VALUE) {
            return Boolean.toString(this.mEnabled);
        }
        return String.format("[%d,%d,%b]", Long.valueOf(j), Long.valueOf(this.mMaxVersionCode), Boolean.valueOf(this.mEnabled));
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private boolean mEnabled;
        private long mMinVersionCode = Long.MIN_VALUE;
        private long mMaxVersionCode = Long.MAX_VALUE;

        public Builder setMinVersionCode(long minVersionCode) {
            this.mMinVersionCode = minVersionCode;
            return this;
        }

        public Builder setMaxVersionCode(long maxVersionCode) {
            this.mMaxVersionCode = maxVersionCode;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }

        public PackageOverride build() {
            long j = this.mMinVersionCode;
            long j2 = this.mMaxVersionCode;
            if (j > j2) {
                throw new IllegalArgumentException("minVersionCode must not be larger than maxVersionCode");
            }
            return new PackageOverride(j, j2, this.mEnabled);
        }
    }
}
