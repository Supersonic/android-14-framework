package android.hardware.health;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class BatteryHealthData implements Parcelable {
    public static final Parcelable.Creator<BatteryHealthData> CREATOR = new Parcelable.Creator<BatteryHealthData>() { // from class: android.hardware.health.BatteryHealthData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryHealthData createFromParcel(Parcel parcel) {
            BatteryHealthData batteryHealthData = new BatteryHealthData();
            batteryHealthData.readFromParcel(parcel);
            return batteryHealthData;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryHealthData[] newArray(int i) {
            return new BatteryHealthData[i];
        }
    };
    public long batteryManufacturingDateSeconds = 0;
    public long batteryFirstUsageSeconds = 0;
    public long batteryStateOfHealth = 0;

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
        parcel.writeLong(this.batteryManufacturingDateSeconds);
        parcel.writeLong(this.batteryFirstUsageSeconds);
        parcel.writeLong(this.batteryStateOfHealth);
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
                this.batteryManufacturingDateSeconds = parcel.readLong();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.batteryFirstUsageSeconds = parcel.readLong();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.batteryStateOfHealth = parcel.readLong();
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
