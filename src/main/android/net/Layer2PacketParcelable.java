package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class Layer2PacketParcelable implements Parcelable {
    public static final Parcelable.Creator<Layer2PacketParcelable> CREATOR = new Parcelable.Creator<Layer2PacketParcelable>() { // from class: android.net.Layer2PacketParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Layer2PacketParcelable createFromParcel(Parcel parcel) {
            Layer2PacketParcelable layer2PacketParcelable = new Layer2PacketParcelable();
            layer2PacketParcelable.readFromParcel(parcel);
            return layer2PacketParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Layer2PacketParcelable[] newArray(int i) {
            return new Layer2PacketParcelable[i];
        }
    };
    public MacAddress dstMacAddress;
    public byte[] payload;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.dstMacAddress, i);
        parcel.writeByteArray(this.payload);
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
                this.dstMacAddress = (MacAddress) parcel.readTypedObject(MacAddress.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.payload = parcel.createByteArray();
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
        stringJoiner.add("dstMacAddress: " + Objects.toString(this.dstMacAddress));
        stringJoiner.add("payload: " + Arrays.toString(this.payload));
        return "android.net.Layer2PacketParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.dstMacAddress) | 0;
    }

    private int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }
}
