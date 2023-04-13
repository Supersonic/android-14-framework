package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class DabTableEntry implements Parcelable {
    public static final Parcelable.Creator<DabTableEntry> CREATOR = new Parcelable.Creator<DabTableEntry>() { // from class: android.hardware.broadcastradio.DabTableEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DabTableEntry createFromParcel(Parcel parcel) {
            DabTableEntry dabTableEntry = new DabTableEntry();
            dabTableEntry.readFromParcel(parcel);
            return dabTableEntry;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DabTableEntry[] newArray(int i) {
            return new DabTableEntry[i];
        }
    };
    public int frequencyKhz = 0;
    public String label;

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
        parcel.writeString(this.label);
        parcel.writeInt(this.frequencyKhz);
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
                this.label = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.frequencyKhz = parcel.readInt();
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
        stringJoiner.add("label: " + Objects.toString(this.label));
        stringJoiner.add("frequencyKhz: " + this.frequencyKhz);
        return "android.hardware.broadcastradio.DabTableEntry" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof DabTableEntry)) {
            DabTableEntry dabTableEntry = (DabTableEntry) obj;
            return Objects.deepEquals(this.label, dabTableEntry.label) && Objects.deepEquals(Integer.valueOf(this.frequencyKhz), Integer.valueOf(dabTableEntry.frequencyKhz));
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.label, Integer.valueOf(this.frequencyKhz)).toArray());
    }
}
