package android.p005os.storage;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* renamed from: android.os.storage.CrateMetadata */
/* loaded from: classes.dex */
public class CrateMetadata implements Parcelable {
    public static final Parcelable.Creator<CrateMetadata> CREATOR = new Parcelable.Creator<CrateMetadata>() { // from class: android.os.storage.CrateMetadata.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CrateMetadata createFromParcel(Parcel parcel) {
            CrateMetadata crateMetadata = new CrateMetadata();
            crateMetadata.readFromParcel(parcel);
            return crateMetadata;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CrateMetadata[] newArray(int i) {
            return new CrateMetadata[i];
        }
    };

    /* renamed from: id */
    public String f16id;
    public String packageName;
    public int uid = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.uid);
        parcel.writeString(this.packageName);
        parcel.writeString(this.f16id);
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
                this.uid = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.packageName = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.f16id = parcel.readString();
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
