package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ProgramListChunk implements Parcelable {
    public static final Parcelable.Creator<ProgramListChunk> CREATOR = new Parcelable.Creator<ProgramListChunk>() { // from class: android.hardware.broadcastradio.ProgramListChunk.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramListChunk createFromParcel(Parcel parcel) {
            ProgramListChunk programListChunk = new ProgramListChunk();
            programListChunk.readFromParcel(parcel);
            return programListChunk;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramListChunk[] newArray(int i) {
            return new ProgramListChunk[i];
        }
    };
    public ProgramInfo[] modified;
    public ProgramIdentifier[] removed;
    public boolean purge = false;
    public boolean complete = false;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeBoolean(this.purge);
        parcel.writeBoolean(this.complete);
        parcel.writeTypedArray(this.modified, i);
        parcel.writeTypedArray(this.removed, i);
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
                this.purge = parcel.readBoolean();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.complete = parcel.readBoolean();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.modified = (ProgramInfo[]) parcel.createTypedArray(ProgramInfo.CREATOR);
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.removed = (ProgramIdentifier[]) parcel.createTypedArray(ProgramIdentifier.CREATOR);
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
        stringJoiner.add("purge: " + this.purge);
        stringJoiner.add("complete: " + this.complete);
        stringJoiner.add("modified: " + Arrays.toString(this.modified));
        stringJoiner.add("removed: " + Arrays.toString(this.removed));
        return "android.hardware.broadcastradio.ProgramListChunk" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ProgramListChunk)) {
            ProgramListChunk programListChunk = (ProgramListChunk) obj;
            return Objects.deepEquals(Boolean.valueOf(this.purge), Boolean.valueOf(programListChunk.purge)) && Objects.deepEquals(Boolean.valueOf(this.complete), Boolean.valueOf(programListChunk.complete)) && Objects.deepEquals(this.modified, programListChunk.modified) && Objects.deepEquals(this.removed, programListChunk.removed);
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Boolean.valueOf(this.purge), Boolean.valueOf(this.complete), this.modified, this.removed).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.removed) | describeContents(this.modified) | 0;
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
