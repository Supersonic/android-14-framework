package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ProgramFilter implements Parcelable {
    public static final Parcelable.Creator<ProgramFilter> CREATOR = new Parcelable.Creator<ProgramFilter>() { // from class: android.hardware.broadcastradio.ProgramFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramFilter createFromParcel(Parcel parcel) {
            ProgramFilter programFilter = new ProgramFilter();
            programFilter.readFromParcel(parcel);
            return programFilter;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramFilter[] newArray(int i) {
            return new ProgramFilter[i];
        }
    };
    public int[] identifierTypes;
    public ProgramIdentifier[] identifiers;
    public boolean includeCategories = false;
    public boolean excludeModifications = false;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeIntArray(this.identifierTypes);
        parcel.writeTypedArray(this.identifiers, i);
        parcel.writeBoolean(this.includeCategories);
        parcel.writeBoolean(this.excludeModifications);
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
                this.identifierTypes = parcel.createIntArray();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.identifiers = (ProgramIdentifier[]) parcel.createTypedArray(ProgramIdentifier.CREATOR);
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.includeCategories = parcel.readBoolean();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.excludeModifications = parcel.readBoolean();
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
        stringJoiner.add("identifierTypes: " + IdentifierType$$.arrayToString(this.identifierTypes));
        stringJoiner.add("identifiers: " + Arrays.toString(this.identifiers));
        stringJoiner.add("includeCategories: " + this.includeCategories);
        stringJoiner.add("excludeModifications: " + this.excludeModifications);
        return "android.hardware.broadcastradio.ProgramFilter" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ProgramFilter)) {
            ProgramFilter programFilter = (ProgramFilter) obj;
            return Objects.deepEquals(this.identifierTypes, programFilter.identifierTypes) && Objects.deepEquals(this.identifiers, programFilter.identifiers) && Objects.deepEquals(Boolean.valueOf(this.includeCategories), Boolean.valueOf(programFilter.includeCategories)) && Objects.deepEquals(Boolean.valueOf(this.excludeModifications), Boolean.valueOf(programFilter.excludeModifications));
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.identifierTypes, this.identifiers, Boolean.valueOf(this.includeCategories), Boolean.valueOf(this.excludeModifications)).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.identifiers) | 0;
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
