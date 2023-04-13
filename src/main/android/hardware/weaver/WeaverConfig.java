package android.hardware.weaver;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class WeaverConfig implements Parcelable {
    public static final Parcelable.Creator<WeaverConfig> CREATOR = new Parcelable.Creator<WeaverConfig>() { // from class: android.hardware.weaver.WeaverConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WeaverConfig createFromParcel(Parcel parcel) {
            WeaverConfig weaverConfig = new WeaverConfig();
            weaverConfig.readFromParcel(parcel);
            return weaverConfig;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WeaverConfig[] newArray(int i) {
            return new WeaverConfig[i];
        }
    };
    public int slots = 0;
    public int keySize = 0;
    public int valueSize = 0;

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
        parcel.writeInt(this.slots);
        parcel.writeInt(this.keySize);
        parcel.writeInt(this.valueSize);
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
                this.slots = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.keySize = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.valueSize = parcel.readInt();
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
