package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class AmFmBandRange implements Parcelable {
    public static final Parcelable.Creator<AmFmBandRange> CREATOR = new Parcelable.Creator<AmFmBandRange>() { // from class: android.hardware.broadcastradio.AmFmBandRange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AmFmBandRange createFromParcel(Parcel parcel) {
            AmFmBandRange amFmBandRange = new AmFmBandRange();
            amFmBandRange.readFromParcel(parcel);
            return amFmBandRange;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AmFmBandRange[] newArray(int i) {
            return new AmFmBandRange[i];
        }
    };
    public int lowerBound = 0;
    public int upperBound = 0;
    public int spacing = 0;
    public int seekSpacing = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.lowerBound);
        parcel.writeInt(this.upperBound);
        parcel.writeInt(this.spacing);
        parcel.writeInt(this.seekSpacing);
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
                this.lowerBound = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.upperBound = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.spacing = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.seekSpacing = parcel.readInt();
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
        stringJoiner.add("lowerBound: " + this.lowerBound);
        stringJoiner.add("upperBound: " + this.upperBound);
        stringJoiner.add("spacing: " + this.spacing);
        stringJoiner.add("seekSpacing: " + this.seekSpacing);
        return "android.hardware.broadcastradio.AmFmBandRange" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof AmFmBandRange)) {
            AmFmBandRange amFmBandRange = (AmFmBandRange) obj;
            return Objects.deepEquals(Integer.valueOf(this.lowerBound), Integer.valueOf(amFmBandRange.lowerBound)) && Objects.deepEquals(Integer.valueOf(this.upperBound), Integer.valueOf(amFmBandRange.upperBound)) && Objects.deepEquals(Integer.valueOf(this.spacing), Integer.valueOf(amFmBandRange.spacing)) && Objects.deepEquals(Integer.valueOf(this.seekSpacing), Integer.valueOf(amFmBandRange.seekSpacing));
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.lowerBound), Integer.valueOf(this.upperBound), Integer.valueOf(this.spacing), Integer.valueOf(this.seekSpacing)).toArray());
    }
}
