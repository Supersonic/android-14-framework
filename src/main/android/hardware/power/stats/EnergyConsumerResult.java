package android.hardware.power.stats;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class EnergyConsumerResult implements Parcelable {
    public static final Parcelable.Creator<EnergyConsumerResult> CREATOR = new Parcelable.Creator<EnergyConsumerResult>() { // from class: android.hardware.power.stats.EnergyConsumerResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnergyConsumerResult createFromParcel(Parcel parcel) {
            EnergyConsumerResult energyConsumerResult = new EnergyConsumerResult();
            energyConsumerResult.readFromParcel(parcel);
            return energyConsumerResult;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EnergyConsumerResult[] newArray(int i) {
            return new EnergyConsumerResult[i];
        }
    };
    public EnergyConsumerAttribution[] attribution;

    /* renamed from: id */
    public int f6id = 0;
    public long timestampMs = 0;
    public long energyUWs = 0;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.f6id);
        parcel.writeLong(this.timestampMs);
        parcel.writeLong(this.energyUWs);
        parcel.writeTypedArray(this.attribution, i);
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
                this.f6id = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.timestampMs = parcel.readLong();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.energyUWs = parcel.readLong();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.attribution = (EnergyConsumerAttribution[]) parcel.createTypedArray(EnergyConsumerAttribution.CREATOR);
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

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.attribution) | 0;
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
