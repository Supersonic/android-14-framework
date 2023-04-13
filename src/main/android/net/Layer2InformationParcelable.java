package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class Layer2InformationParcelable implements Parcelable {
    public static final Parcelable.Creator<Layer2InformationParcelable> CREATOR = new Parcelable.Creator<Layer2InformationParcelable>() { // from class: android.net.Layer2InformationParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Layer2InformationParcelable createFromParcel(Parcel parcel) {
            Layer2InformationParcelable layer2InformationParcelable = new Layer2InformationParcelable();
            layer2InformationParcelable.readFromParcel(parcel);
            return layer2InformationParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Layer2InformationParcelable[] newArray(int i) {
            return new Layer2InformationParcelable[i];
        }
    };
    public MacAddress bssid;
    public String cluster;
    public String l2Key;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.l2Key);
        parcel.writeString(this.cluster);
        parcel.writeTypedObject(this.bssid, i);
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
                this.l2Key = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.cluster = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.bssid = (MacAddress) parcel.readTypedObject(MacAddress.CREATOR);
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

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("l2Key: " + Objects.toString(this.l2Key));
        stringJoiner.add("cluster: " + Objects.toString(this.cluster));
        stringJoiner.add("bssid: " + Objects.toString(this.bssid));
        return "android.net.Layer2InformationParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.bssid) | 0;
    }

    private int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }
}
