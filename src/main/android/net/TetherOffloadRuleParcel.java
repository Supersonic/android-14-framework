package android.net;

import android.net.util.NetworkConstants;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class TetherOffloadRuleParcel implements Parcelable {
    public static final Parcelable.Creator<TetherOffloadRuleParcel> CREATOR = new Parcelable.Creator<TetherOffloadRuleParcel>() { // from class: android.net.TetherOffloadRuleParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TetherOffloadRuleParcel createFromParcel(Parcel parcel) {
            TetherOffloadRuleParcel tetherOffloadRuleParcel = new TetherOffloadRuleParcel();
            tetherOffloadRuleParcel.readFromParcel(parcel);
            return tetherOffloadRuleParcel;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TetherOffloadRuleParcel[] newArray(int i) {
            return new TetherOffloadRuleParcel[i];
        }
    };
    public byte[] destination;
    public byte[] dstL2Address;
    public byte[] srcL2Address;
    public int inputInterfaceIndex = 0;
    public int outputInterfaceIndex = 0;
    public int prefixLength = 0;
    public int pmtu = NetworkConstants.ETHER_MTU;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.inputInterfaceIndex);
        parcel.writeInt(this.outputInterfaceIndex);
        parcel.writeByteArray(this.destination);
        parcel.writeInt(this.prefixLength);
        parcel.writeByteArray(this.srcL2Address);
        parcel.writeByteArray(this.dstL2Address);
        parcel.writeInt(this.pmtu);
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
                this.inputInterfaceIndex = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.outputInterfaceIndex = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.destination = parcel.createByteArray();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.prefixLength = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.srcL2Address = parcel.createByteArray();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.dstL2Address = parcel.createByteArray();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.pmtu = parcel.readInt();
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
