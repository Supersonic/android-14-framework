package android.hardware.p002tv.hdmi.connection;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* renamed from: android.hardware.tv.hdmi.connection.HdmiPortInfo */
/* loaded from: classes.dex */
public class HdmiPortInfo implements Parcelable {
    public static final Parcelable.Creator<HdmiPortInfo> CREATOR = new Parcelable.Creator<HdmiPortInfo>() { // from class: android.hardware.tv.hdmi.connection.HdmiPortInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HdmiPortInfo createFromParcel(Parcel parcel) {
            HdmiPortInfo hdmiPortInfo = new HdmiPortInfo();
            hdmiPortInfo.readFromParcel(parcel);
            return hdmiPortInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HdmiPortInfo[] newArray(int i) {
            return new HdmiPortInfo[i];
        }
    };
    public byte type;
    public int portId = 0;
    public boolean cecSupported = false;
    public boolean arcSupported = false;
    public boolean eArcSupported = false;
    public int physicalAddress = 0;

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
        parcel.writeByte(this.type);
        parcel.writeInt(this.portId);
        parcel.writeBoolean(this.cecSupported);
        parcel.writeBoolean(this.arcSupported);
        parcel.writeBoolean(this.eArcSupported);
        parcel.writeInt(this.physicalAddress);
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
                this.type = parcel.readByte();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.portId = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.cecSupported = parcel.readBoolean();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.arcSupported = parcel.readBoolean();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.eArcSupported = parcel.readBoolean();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.physicalAddress = parcel.readInt();
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
