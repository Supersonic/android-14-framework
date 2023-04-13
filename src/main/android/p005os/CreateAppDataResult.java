package android.p005os;

import android.os.Parcelable;
/* renamed from: android.os.CreateAppDataResult */
/* loaded from: classes.dex */
public class CreateAppDataResult implements Parcelable {
    public static final Parcelable.Creator<CreateAppDataResult> CREATOR = new Parcelable.Creator<CreateAppDataResult>() { // from class: android.os.CreateAppDataResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CreateAppDataResult createFromParcel(Parcel parcel) {
            CreateAppDataResult createAppDataResult = new CreateAppDataResult();
            createAppDataResult.readFromParcel(parcel);
            return createAppDataResult;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CreateAppDataResult[] newArray(int i) {
            return new CreateAppDataResult[i];
        }
    };
    public long ceDataInode = 0;
    public int exceptionCode = 0;
    public String exceptionMessage;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeLong(this.ceDataInode);
        parcel.writeInt(this.exceptionCode);
        parcel.writeString(this.exceptionMessage);
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
                this.ceDataInode = parcel.readLong();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.exceptionCode = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.exceptionMessage = parcel.readString();
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
}
