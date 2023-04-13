package android.hardware.p001ir;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* renamed from: android.hardware.ir.ConsumerIrFreqRange */
/* loaded from: classes.dex */
public class ConsumerIrFreqRange implements Parcelable {
    public static final Parcelable.Creator<ConsumerIrFreqRange> CREATOR = new Parcelable.Creator<ConsumerIrFreqRange>() { // from class: android.hardware.ir.ConsumerIrFreqRange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConsumerIrFreqRange createFromParcel(Parcel parcel) {
            ConsumerIrFreqRange consumerIrFreqRange = new ConsumerIrFreqRange();
            consumerIrFreqRange.readFromParcel(parcel);
            return consumerIrFreqRange;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConsumerIrFreqRange[] newArray(int i) {
            return new ConsumerIrFreqRange[i];
        }
    };
    public int minHz = 0;
    public int maxHz = 0;

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
        parcel.writeInt(this.minHz);
        parcel.writeInt(this.maxHz);
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
                this.minHz = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.maxHz = parcel.readInt();
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
}
