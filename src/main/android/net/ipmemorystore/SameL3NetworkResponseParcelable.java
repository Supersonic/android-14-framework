package android.net.ipmemorystore;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class SameL3NetworkResponseParcelable implements Parcelable {
    public static final Parcelable.Creator<SameL3NetworkResponseParcelable> CREATOR = new Parcelable.Creator<SameL3NetworkResponseParcelable>() { // from class: android.net.ipmemorystore.SameL3NetworkResponseParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SameL3NetworkResponseParcelable createFromParcel(Parcel parcel) {
            SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable = new SameL3NetworkResponseParcelable();
            sameL3NetworkResponseParcelable.readFromParcel(parcel);
            return sameL3NetworkResponseParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SameL3NetworkResponseParcelable[] newArray(int i) {
            return new SameL3NetworkResponseParcelable[i];
        }
    };
    public float confidence = 0.0f;
    public String l2Key1;
    public String l2Key2;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.l2Key1);
        parcel.writeString(this.l2Key2);
        parcel.writeFloat(this.confidence);
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
                this.l2Key1 = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.l2Key2 = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.confidence = parcel.readFloat();
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
        stringJoiner.add("l2Key1: " + Objects.toString(this.l2Key1));
        stringJoiner.add("l2Key2: " + Objects.toString(this.l2Key2));
        stringJoiner.add("confidence: " + this.confidence);
        return "android.net.ipmemorystore.SameL3NetworkResponseParcelable" + stringJoiner.toString();
    }
}
