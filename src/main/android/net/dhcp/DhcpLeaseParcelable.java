package android.net.dhcp;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class DhcpLeaseParcelable implements Parcelable {
    public static final Parcelable.Creator<DhcpLeaseParcelable> CREATOR = new Parcelable.Creator<DhcpLeaseParcelable>() { // from class: android.net.dhcp.DhcpLeaseParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpLeaseParcelable createFromParcel(Parcel parcel) {
            DhcpLeaseParcelable dhcpLeaseParcelable = new DhcpLeaseParcelable();
            dhcpLeaseParcelable.readFromParcel(parcel);
            return dhcpLeaseParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpLeaseParcelable[] newArray(int i) {
            return new DhcpLeaseParcelable[i];
        }
    };
    public byte[] clientId;
    public String hostname;
    public byte[] hwAddr;
    public int netAddr = 0;
    public int prefixLength = 0;
    public long expTime = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeByteArray(this.clientId);
        parcel.writeByteArray(this.hwAddr);
        parcel.writeInt(this.netAddr);
        parcel.writeInt(this.prefixLength);
        parcel.writeLong(this.expTime);
        parcel.writeString(this.hostname);
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
                this.clientId = parcel.createByteArray();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.hwAddr = parcel.createByteArray();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.netAddr = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.prefixLength = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.expTime = parcel.readLong();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.hostname = parcel.readString();
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

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("clientId: " + Arrays.toString(this.clientId));
        stringJoiner.add("hwAddr: " + Arrays.toString(this.hwAddr));
        stringJoiner.add("netAddr: " + this.netAddr);
        stringJoiner.add("prefixLength: " + this.prefixLength);
        stringJoiner.add("expTime: " + this.expTime);
        stringJoiner.add("hostname: " + Objects.toString(this.hostname));
        return "android.net.dhcp.DhcpLeaseParcelable" + stringJoiner.toString();
    }
}
