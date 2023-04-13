package android.hardware.health;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class StorageInfo implements Parcelable {
    public static final Parcelable.Creator<StorageInfo> CREATOR = new Parcelable.Creator<StorageInfo>() { // from class: android.hardware.health.StorageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageInfo createFromParcel(Parcel parcel) {
            StorageInfo storageInfo = new StorageInfo();
            storageInfo.readFromParcel(parcel);
            return storageInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageInfo[] newArray(int i) {
            return new StorageInfo[i];
        }
    };
    public int eol = 0;
    public int lifetimeA = 0;
    public int lifetimeB = 0;
    public String version;

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
        parcel.writeInt(this.eol);
        parcel.writeInt(this.lifetimeA);
        parcel.writeInt(this.lifetimeB);
        parcel.writeString(this.version);
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
                this.eol = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.lifetimeA = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.lifetimeB = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.version = parcel.readString();
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
}
