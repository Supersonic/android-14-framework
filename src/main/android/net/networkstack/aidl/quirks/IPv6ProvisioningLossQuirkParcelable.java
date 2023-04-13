package android.net.networkstack.aidl.quirks;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class IPv6ProvisioningLossQuirkParcelable implements Parcelable {
    public static final Parcelable.Creator<IPv6ProvisioningLossQuirkParcelable> CREATOR = new Parcelable.Creator<IPv6ProvisioningLossQuirkParcelable>() { // from class: android.net.networkstack.aidl.quirks.IPv6ProvisioningLossQuirkParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IPv6ProvisioningLossQuirkParcelable createFromParcel(Parcel parcel) {
            IPv6ProvisioningLossQuirkParcelable iPv6ProvisioningLossQuirkParcelable = new IPv6ProvisioningLossQuirkParcelable();
            iPv6ProvisioningLossQuirkParcelable.readFromParcel(parcel);
            return iPv6ProvisioningLossQuirkParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IPv6ProvisioningLossQuirkParcelable[] newArray(int i) {
            return new IPv6ProvisioningLossQuirkParcelable[i];
        }
    };
    public int detectionCount = 0;
    public long quirkExpiry = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.detectionCount);
        parcel.writeLong(this.quirkExpiry);
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
                this.detectionCount = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.quirkExpiry = parcel.readLong();
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

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("detectionCount: " + this.detectionCount);
        stringJoiner.add("quirkExpiry: " + this.quirkExpiry);
        return "android.net.networkstack.aidl.quirks.IPv6ProvisioningLossQuirkParcelable" + stringJoiner.toString();
    }
}
