package android.hardware.biometrics.face;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class EnrollmentFrame implements Parcelable {
    public static final Parcelable.Creator<EnrollmentFrame> CREATOR = new Parcelable.Creator<EnrollmentFrame>() { // from class: android.hardware.biometrics.face.EnrollmentFrame.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnrollmentFrame createFromParcel(Parcel parcel) {
            EnrollmentFrame enrollmentFrame = new EnrollmentFrame();
            enrollmentFrame.readFromParcel(parcel);
            return enrollmentFrame;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnrollmentFrame[] newArray(int i) {
            return new EnrollmentFrame[i];
        }
    };
    public Cell cell;
    public BaseFrame data;
    public byte stage = 0;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.cell, i);
        parcel.writeByte(this.stage);
        parcel.writeTypedObject(this.data, i);
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
                this.cell = (Cell) parcel.readTypedObject(Cell.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.stage = parcel.readByte();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.data = (BaseFrame) parcel.readTypedObject(BaseFrame.CREATOR);
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

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.data) | describeContents(this.cell) | 0;
    }

    public final int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }
}
