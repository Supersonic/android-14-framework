package android.net.ipmemorystore;

import android.net.networkstack.aidl.quirks.IPv6ProvisioningLossQuirkParcelable;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NetworkAttributesParcelable implements Parcelable {
    public static final Parcelable.Creator<NetworkAttributesParcelable> CREATOR = new Parcelable.Creator<NetworkAttributesParcelable>() { // from class: android.net.ipmemorystore.NetworkAttributesParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkAttributesParcelable createFromParcel(Parcel parcel) {
            NetworkAttributesParcelable networkAttributesParcelable = new NetworkAttributesParcelable();
            networkAttributesParcelable.readFromParcel(parcel);
            return networkAttributesParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkAttributesParcelable[] newArray(int i) {
            return new NetworkAttributesParcelable[i];
        }
    };
    public byte[] assignedV4Address;
    public String cluster;
    public Blob[] dnsAddresses;
    public IPv6ProvisioningLossQuirkParcelable ipv6ProvisioningLossQuirk;
    public long assignedV4AddressExpiry = 0;
    public int mtu = 0;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeByteArray(this.assignedV4Address);
        parcel.writeLong(this.assignedV4AddressExpiry);
        parcel.writeString(this.cluster);
        parcel.writeTypedArray(this.dnsAddresses, i);
        parcel.writeInt(this.mtu);
        parcel.writeTypedObject(this.ipv6ProvisioningLossQuirk, i);
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
                this.assignedV4Address = parcel.createByteArray();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.assignedV4AddressExpiry = parcel.readLong();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.cluster = parcel.readString();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.dnsAddresses = (Blob[]) parcel.createTypedArray(Blob.CREATOR);
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.mtu = parcel.readInt();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.ipv6ProvisioningLossQuirk = (IPv6ProvisioningLossQuirkParcelable) parcel.readTypedObject(IPv6ProvisioningLossQuirkParcelable.CREATOR);
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
        stringJoiner.add("assignedV4Address: " + Arrays.toString(this.assignedV4Address));
        stringJoiner.add("assignedV4AddressExpiry: " + this.assignedV4AddressExpiry);
        stringJoiner.add("cluster: " + Objects.toString(this.cluster));
        stringJoiner.add("dnsAddresses: " + Arrays.toString(this.dnsAddresses));
        stringJoiner.add("mtu: " + this.mtu);
        stringJoiner.add("ipv6ProvisioningLossQuirk: " + Objects.toString(this.ipv6ProvisioningLossQuirk));
        return "android.net.ipmemorystore.NetworkAttributesParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.ipv6ProvisioningLossQuirk) | describeContents(this.dnsAddresses) | 0;
    }

    private int describeContents(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            int i = 0;
            for (Object obj2 : (Object[]) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }
}
