package android.hardware.p002tv.hdmi.cec;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* renamed from: android.hardware.tv.hdmi.cec.CecMessage */
/* loaded from: classes.dex */
public class CecMessage implements Parcelable {
    public static final Parcelable.Creator<CecMessage> CREATOR = new Parcelable.Creator<CecMessage>() { // from class: android.hardware.tv.hdmi.cec.CecMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CecMessage createFromParcel(Parcel parcel) {
            CecMessage cecMessage = new CecMessage();
            cecMessage.readFromParcel(parcel);
            return cecMessage;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CecMessage[] newArray(int i) {
            return new CecMessage[i];
        }
    };
    public byte[] body;
    public byte destination;
    public byte initiator;

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
        parcel.writeByte(this.initiator);
        parcel.writeByte(this.destination);
        parcel.writeByteArray(this.body);
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
                this.initiator = parcel.readByte();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.destination = parcel.readByte();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.body = parcel.createByteArray();
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
