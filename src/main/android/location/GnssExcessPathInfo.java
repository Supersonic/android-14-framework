package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class GnssExcessPathInfo implements Parcelable {
    public static final Parcelable.Creator<GnssExcessPathInfo> CREATOR = new Parcelable.Creator<GnssExcessPathInfo>() { // from class: android.location.GnssExcessPathInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssExcessPathInfo createFromParcel(Parcel parcel) {
            int flags = parcel.readInt();
            float excessPathLengthMeters = (flags & 1) != 0 ? parcel.readFloat() : 0.0f;
            float excessPathLengthUncertaintyMeters = (flags & 2) != 0 ? parcel.readFloat() : 0.0f;
            GnssReflectingPlane reflectingPlane = (flags & 4) != 0 ? GnssReflectingPlane.CREATOR.createFromParcel(parcel) : null;
            float attenuationDb = (flags & 8) != 0 ? parcel.readFloat() : 0.0f;
            return new GnssExcessPathInfo(flags, excessPathLengthMeters, excessPathLengthUncertaintyMeters, reflectingPlane, attenuationDb);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssExcessPathInfo[] newArray(int i) {
            return new GnssExcessPathInfo[i];
        }
    };
    private static final int HAS_ATTENUATION_MASK = 8;
    private static final int HAS_EXCESS_PATH_LENGTH_MASK = 1;
    private static final int HAS_EXCESS_PATH_LENGTH_UNC_MASK = 2;
    private static final int HAS_REFLECTING_PLANE_MASK = 4;
    private final float mAttenuationDb;
    private final float mExcessPathLengthMeters;
    private final float mExcessPathLengthUncertaintyMeters;
    private final int mFlags;
    private final GnssReflectingPlane mReflectingPlane;

    private GnssExcessPathInfo(int flags, float excessPathLengthMeters, float excessPathLengthUncertaintyMeters, GnssReflectingPlane reflectingPlane, float attenuationDb) {
        this.mFlags = flags;
        this.mExcessPathLengthMeters = excessPathLengthMeters;
        this.mExcessPathLengthUncertaintyMeters = excessPathLengthUncertaintyMeters;
        this.mReflectingPlane = reflectingPlane;
        this.mAttenuationDb = attenuationDb;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean hasExcessPathLength() {
        return (this.mFlags & 1) != 0;
    }

    public float getExcessPathLengthMeters() {
        if (!hasExcessPathLength()) {
            throw new UnsupportedOperationException("getExcessPathLengthMeters() is not supported when hasExcessPathLength() is false");
        }
        return this.mExcessPathLengthMeters;
    }

    public boolean hasExcessPathLengthUncertainty() {
        return (this.mFlags & 2) != 0;
    }

    public float getExcessPathLengthUncertaintyMeters() {
        if (!hasExcessPathLengthUncertainty()) {
            throw new UnsupportedOperationException("getExcessPathLengthUncertaintyMeters() is not supported when hasExcessPathLengthUncertainty() is false");
        }
        return this.mExcessPathLengthUncertaintyMeters;
    }

    public boolean hasReflectingPlane() {
        return (this.mFlags & 4) != 0;
    }

    public GnssReflectingPlane getReflectingPlane() {
        if (!hasReflectingPlane()) {
            throw new UnsupportedOperationException("getReflectingPlane() is not supported when hasReflectingPlane() is false");
        }
        return this.mReflectingPlane;
    }

    public boolean hasAttenuation() {
        return (this.mFlags & 8) != 0;
    }

    public float getAttenuationDb() {
        if (!hasAttenuation()) {
            throw new UnsupportedOperationException("getAttenuationDb() is not supported when hasAttenuation() is false");
        }
        return this.mAttenuationDb;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelFlags) {
        parcel.writeInt(this.mFlags);
        if (hasExcessPathLength()) {
            parcel.writeFloat(this.mExcessPathLengthMeters);
        }
        if (hasExcessPathLengthUncertainty()) {
            parcel.writeFloat(this.mExcessPathLengthUncertaintyMeters);
        }
        if (hasReflectingPlane()) {
            this.mReflectingPlane.writeToParcel(parcel, parcelFlags);
        }
        if (hasAttenuation()) {
            parcel.writeFloat(this.mAttenuationDb);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof GnssExcessPathInfo) {
            GnssExcessPathInfo that = (GnssExcessPathInfo) obj;
            if (this.mFlags == that.mFlags) {
                if (!hasExcessPathLength() || Float.compare(this.mExcessPathLengthMeters, that.mExcessPathLengthMeters) == 0) {
                    if (!hasExcessPathLengthUncertainty() || Float.compare(this.mExcessPathLengthUncertaintyMeters, that.mExcessPathLengthUncertaintyMeters) == 0) {
                        if (!hasReflectingPlane() || Objects.equals(this.mReflectingPlane, that.mReflectingPlane)) {
                            return !hasAttenuation() || Float.compare(this.mAttenuationDb, that.mAttenuationDb) == 0;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mFlags), Float.valueOf(this.mExcessPathLengthMeters), Float.valueOf(this.mExcessPathLengthUncertaintyMeters), this.mReflectingPlane, Float.valueOf(this.mAttenuationDb));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GnssExcessPathInfo[");
        if (hasExcessPathLength()) {
            builder.append(" ExcessPathLengthMeters=").append(this.mExcessPathLengthMeters);
        }
        if (hasExcessPathLengthUncertainty()) {
            builder.append(" ExcessPathLengthUncertaintyMeters=").append(this.mExcessPathLengthUncertaintyMeters);
        }
        if (hasReflectingPlane()) {
            builder.append(" ReflectingPlane=").append(this.mReflectingPlane);
        }
        if (hasAttenuation()) {
            builder.append(" AttenuationDb=").append(this.mAttenuationDb);
        }
        builder.append(']');
        return builder.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private float mAttenuationDb;
        private float mExcessPathLengthMeters;
        private float mExcessPathLengthUncertaintyMeters;
        private int mFlags;
        private GnssReflectingPlane mReflectingPlane;

        public Builder setExcessPathLengthMeters(float excessPathLengthMeters) {
            Preconditions.checkArgumentInRange(excessPathLengthMeters, 0.0f, Float.MAX_VALUE, "excessPathLengthMeters");
            this.mExcessPathLengthMeters = excessPathLengthMeters;
            this.mFlags |= 1;
            return this;
        }

        public Builder clearExcessPathLengthMeters() {
            this.mExcessPathLengthMeters = 0.0f;
            this.mFlags &= -2;
            return this;
        }

        public Builder setExcessPathLengthUncertaintyMeters(float excessPathLengthUncertaintyMeters) {
            Preconditions.checkArgumentInRange(excessPathLengthUncertaintyMeters, 0.0f, Float.MAX_VALUE, "excessPathLengthUncertaintyMeters");
            this.mExcessPathLengthUncertaintyMeters = excessPathLengthUncertaintyMeters;
            this.mFlags |= 2;
            return this;
        }

        public Builder clearExcessPathLengthUncertaintyMeters() {
            this.mExcessPathLengthUncertaintyMeters = 0.0f;
            this.mFlags &= -3;
            return this;
        }

        public Builder setReflectingPlane(GnssReflectingPlane reflectingPlane) {
            this.mReflectingPlane = reflectingPlane;
            if (reflectingPlane != null) {
                this.mFlags |= 4;
            } else {
                this.mFlags &= -5;
            }
            return this;
        }

        public Builder setAttenuationDb(float attenuationDb) {
            Preconditions.checkArgumentInRange(attenuationDb, 0.0f, Float.MAX_VALUE, "attenuationDb");
            this.mAttenuationDb = attenuationDb;
            this.mFlags |= 8;
            return this;
        }

        public Builder clearAttenuationDb() {
            this.mAttenuationDb = 0.0f;
            this.mFlags &= -9;
            return this;
        }

        public GnssExcessPathInfo build() {
            return new GnssExcessPathInfo(this.mFlags, this.mExcessPathLengthMeters, this.mExcessPathLengthUncertaintyMeters, this.mReflectingPlane, this.mAttenuationDb);
        }
    }
}
