package android.hardware.power.stats;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class EnergyConsumer implements Parcelable {
    public static final Parcelable.Creator<EnergyConsumer> CREATOR = new Parcelable.Creator<EnergyConsumer>() { // from class: android.hardware.power.stats.EnergyConsumer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnergyConsumer createFromParcel(Parcel parcel) {
            EnergyConsumer energyConsumer = new EnergyConsumer();
            energyConsumer.readFromParcel(parcel);
            return energyConsumer;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnergyConsumer[] newArray(int i) {
            return new EnergyConsumer[i];
        }
    };
    public String name;

    /* renamed from: id */
    public int f5id = 0;
    public int ordinal = 0;
    public byte type = 0;

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
        parcel.writeInt(this.f5id);
        parcel.writeInt(this.ordinal);
        parcel.writeByte(this.type);
        parcel.writeString(this.name);
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
                this.f5id = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.ordinal = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.type = parcel.readByte();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.name = parcel.readString();
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
