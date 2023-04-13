package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class AmFmRegionConfig implements Parcelable {
    public static final Parcelable.Creator<AmFmRegionConfig> CREATOR = new Parcelable.Creator<AmFmRegionConfig>() { // from class: android.hardware.broadcastradio.AmFmRegionConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AmFmRegionConfig createFromParcel(Parcel parcel) {
            AmFmRegionConfig amFmRegionConfig = new AmFmRegionConfig();
            amFmRegionConfig.readFromParcel(parcel);
            return amFmRegionConfig;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AmFmRegionConfig[] newArray(int i) {
            return new AmFmRegionConfig[i];
        }
    };
    public int fmDeemphasis = 0;
    public int fmRds = 0;
    public AmFmBandRange[] ranges;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedArray(this.ranges, i);
        parcel.writeInt(this.fmDeemphasis);
        parcel.writeInt(this.fmRds);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
            if (readInt < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (parcel.dataPosition() - dataPosition < readInt) {
                this.ranges = (AmFmBandRange[]) parcel.createTypedArray(AmFmBandRange.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.fmDeemphasis = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.fmRds = parcel.readInt();
                        if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                        parcel.setDataPosition(dataPosition + readInt);
                        return;
                    } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
        } catch (Throwable th) {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            throw th;
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("ranges: " + Arrays.toString(this.ranges));
        stringJoiner.add("fmDeemphasis: " + this.fmDeemphasis);
        stringJoiner.add("fmRds: " + this.fmRds);
        return "android.hardware.broadcastradio.AmFmRegionConfig" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof AmFmRegionConfig)) {
            AmFmRegionConfig amFmRegionConfig = (AmFmRegionConfig) obj;
            return Objects.deepEquals(this.ranges, amFmRegionConfig.ranges) && Objects.deepEquals(Integer.valueOf(this.fmDeemphasis), Integer.valueOf(amFmRegionConfig.fmDeemphasis)) && Objects.deepEquals(Integer.valueOf(this.fmRds), Integer.valueOf(amFmRegionConfig.fmRds));
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.ranges, Integer.valueOf(this.fmDeemphasis), Integer.valueOf(this.fmRds)).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.ranges) | 0;
    }

    public final int describeContents(Object obj) {
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
