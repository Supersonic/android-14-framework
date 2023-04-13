package android.net.netd.aidl;

import android.net.UidRangeParcel;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NativeUidRangeConfig implements Parcelable {
    public static final Parcelable.Creator<NativeUidRangeConfig> CREATOR = new Parcelable.Creator<NativeUidRangeConfig>() { // from class: android.net.netd.aidl.NativeUidRangeConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NativeUidRangeConfig createFromParcel(Parcel parcel) {
            return NativeUidRangeConfig.internalCreateFromParcel(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NativeUidRangeConfig[] newArray(int i) {
            return new NativeUidRangeConfig[i];
        }
    };
    public final int netId;
    public final int subPriority;
    public final UidRangeParcel[] uidRanges;

    /* loaded from: classes.dex */
    public static final class Builder {
        private int netId = 0;
        private int subPriority = 0;
        private UidRangeParcel[] uidRanges;

        public Builder setNetId(int i) {
            this.netId = i;
            return this;
        }

        public Builder setUidRanges(UidRangeParcel[] uidRangeParcelArr) {
            this.uidRanges = uidRangeParcelArr;
            return this;
        }

        public Builder setSubPriority(int i) {
            this.subPriority = i;
            return this;
        }

        public NativeUidRangeConfig build() {
            return new NativeUidRangeConfig(this.netId, this.uidRanges, this.subPriority);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.netId);
        parcel.writeTypedArray(this.uidRanges, i);
        parcel.writeInt(this.subPriority);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public NativeUidRangeConfig(int i, UidRangeParcel[] uidRangeParcelArr, int i2) {
        this.netId = i;
        this.uidRanges = uidRangeParcelArr;
        this.subPriority = i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NativeUidRangeConfig internalCreateFromParcel(Parcel parcel) {
        Builder builder = new Builder();
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
        } finally {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                BadParcelableException badParcelableException = new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        if (readInt >= 4) {
            builder.build();
            if (parcel.dataPosition() - dataPosition >= readInt) {
                builder.build();
                if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else {
                builder.setNetId(parcel.readInt());
                if (parcel.dataPosition() - dataPosition >= readInt) {
                    builder.build();
                    int i = Integer.MAX_VALUE - readInt;
                    if (dataPosition > i) {
                        throw new BadParcelableException(r4);
                    }
                } else {
                    builder.setUidRanges((UidRangeParcel[]) parcel.createTypedArray(UidRangeParcel.CREATOR));
                    if (parcel.dataPosition() - dataPosition >= readInt) {
                        builder.build();
                        if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    } else {
                        builder.setSubPriority(parcel.readInt());
                        if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    }
                }
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        throw new BadParcelableException("Parcelable too small");
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("netId: " + this.netId);
        stringJoiner.add("uidRanges: " + Arrays.toString(this.uidRanges));
        stringJoiner.add("subPriority: " + this.subPriority);
        return "android.net.netd.aidl.NativeUidRangeConfig" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof NativeUidRangeConfig)) {
            NativeUidRangeConfig nativeUidRangeConfig = (NativeUidRangeConfig) obj;
            return Objects.deepEquals(Integer.valueOf(this.netId), Integer.valueOf(nativeUidRangeConfig.netId)) && Objects.deepEquals(this.uidRanges, nativeUidRangeConfig.uidRanges) && Objects.deepEquals(Integer.valueOf(this.subPriority), Integer.valueOf(nativeUidRangeConfig.subPriority));
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.netId), this.uidRanges, Integer.valueOf(this.subPriority)).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.uidRanges) | 0;
    }

    private int describeContents(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            int i = 0;
            for (Object obj2 : (Object[]) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }
}
