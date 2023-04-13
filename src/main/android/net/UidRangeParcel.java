package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class UidRangeParcel implements Parcelable {
    public static final Parcelable.Creator<UidRangeParcel> CREATOR = new Parcelable.Creator<UidRangeParcel>() { // from class: android.net.UidRangeParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UidRangeParcel createFromParcel(Parcel parcel) {
            return UidRangeParcel.internalCreateFromParcel(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UidRangeParcel[] newArray(int i) {
            return new UidRangeParcel[i];
        }
    };
    public final int start;
    public final int stop;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int start = 0;
        private int stop = 0;

        public Builder setStart(int i) {
            this.start = i;
            return this;
        }

        public Builder setStop(int i) {
            this.stop = i;
            return this;
        }

        public UidRangeParcel build() {
            return new UidRangeParcel(this.start, this.stop);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.start);
        parcel.writeInt(this.stop);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public UidRangeParcel(int i, int i2) {
        this.start = i;
        this.stop = i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static UidRangeParcel internalCreateFromParcel(Parcel parcel) {
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
                builder.setStart(parcel.readInt());
                if (parcel.dataPosition() - dataPosition >= readInt) {
                    builder.build();
                    int i = Integer.MAX_VALUE - readInt;
                    if (dataPosition > i) {
                        throw new BadParcelableException(r4);
                    }
                } else {
                    builder.setStop(parcel.readInt());
                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
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
        stringJoiner.add("start: " + this.start);
        stringJoiner.add("stop: " + this.stop);
        return "android.net.UidRangeParcel" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof UidRangeParcel)) {
            UidRangeParcel uidRangeParcel = (UidRangeParcel) obj;
            return Objects.deepEquals(Integer.valueOf(this.start), Integer.valueOf(uidRangeParcel.start)) && Objects.deepEquals(Integer.valueOf(this.stop), Integer.valueOf(uidRangeParcel.stop));
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.start), Integer.valueOf(this.stop)).toArray());
    }
}
