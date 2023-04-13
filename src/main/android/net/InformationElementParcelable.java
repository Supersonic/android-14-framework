package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class InformationElementParcelable implements Parcelable {
    public static final Parcelable.Creator<InformationElementParcelable> CREATOR = new Parcelable.Creator<InformationElementParcelable>() { // from class: android.net.InformationElementParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InformationElementParcelable createFromParcel(Parcel parcel) {
            InformationElementParcelable informationElementParcelable = new InformationElementParcelable();
            informationElementParcelable.readFromParcel(parcel);
            return informationElementParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InformationElementParcelable[] newArray(int i) {
            return new InformationElementParcelable[i];
        }
    };

    /* renamed from: id */
    public int f14id = 0;
    public byte[] payload;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.f14id);
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
                this.f14id = parcel.readInt();
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
        stringJoiner.add("id: " + this.f14id);
        stringJoiner.add("payload: " + Arrays.toString(this.payload));
        return "android.net.InformationElementParcelable" + stringJoiner.toString();
    }
}
