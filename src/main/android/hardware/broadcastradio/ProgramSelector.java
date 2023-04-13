package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ProgramSelector implements Parcelable {
    public static final Parcelable.Creator<ProgramSelector> CREATOR = new Parcelable.Creator<ProgramSelector>() { // from class: android.hardware.broadcastradio.ProgramSelector.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramSelector createFromParcel(Parcel parcel) {
            ProgramSelector programSelector = new ProgramSelector();
            programSelector.readFromParcel(parcel);
            return programSelector;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramSelector[] newArray(int i) {
            return new ProgramSelector[i];
        }
    };
    public ProgramIdentifier primaryId;
    public ProgramIdentifier[] secondaryIds;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.primaryId, i);
        parcel.writeTypedArray(this.secondaryIds, i);
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
                Parcelable.Creator<ProgramIdentifier> creator = ProgramIdentifier.CREATOR;
                this.primaryId = (ProgramIdentifier) parcel.readTypedObject(creator);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.secondaryIds = (ProgramIdentifier[]) parcel.createTypedArray(creator);
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
        stringJoiner.add("primaryId: " + Objects.toString(this.primaryId));
        stringJoiner.add("secondaryIds: " + Arrays.toString(this.secondaryIds));
        return "android.hardware.broadcastradio.ProgramSelector" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ProgramSelector)) {
            ProgramSelector programSelector = (ProgramSelector) obj;
            return Objects.deepEquals(this.primaryId, programSelector.primaryId) && Objects.deepEquals(this.secondaryIds, programSelector.secondaryIds);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.primaryId, this.secondaryIds).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.secondaryIds) | describeContents(this.primaryId) | 0;
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
