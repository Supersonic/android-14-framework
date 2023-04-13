package android.net.dhcp;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class DhcpServingParamsParcel implements Parcelable {
    public static final Parcelable.Creator<DhcpServingParamsParcel> CREATOR = new Parcelable.Creator<DhcpServingParamsParcel>() { // from class: android.net.dhcp.DhcpServingParamsParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpServingParamsParcel createFromParcel(Parcel parcel) {
            DhcpServingParamsParcel dhcpServingParamsParcel = new DhcpServingParamsParcel();
            dhcpServingParamsParcel.readFromParcel(parcel);
            return dhcpServingParamsParcel;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpServingParamsParcel[] newArray(int i) {
            return new DhcpServingParamsParcel[i];
        }
    };
    public int[] defaultRouters;
    public int[] dnsServers;
    public int[] excludedAddrs;
    public int serverAddr = 0;
    public int serverAddrPrefixLength = 0;
    public long dhcpLeaseTimeSecs = 0;
    public int linkMtu = 0;
    public boolean metered = false;
    public int singleClientAddr = 0;
    public boolean changePrefixOnDecline = false;
    public int leasesSubnetPrefixLength = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.serverAddr);
        parcel.writeInt(this.serverAddrPrefixLength);
        parcel.writeIntArray(this.defaultRouters);
        parcel.writeIntArray(this.dnsServers);
        parcel.writeIntArray(this.excludedAddrs);
        parcel.writeLong(this.dhcpLeaseTimeSecs);
        parcel.writeInt(this.linkMtu);
        parcel.writeBoolean(this.metered);
        parcel.writeInt(this.singleClientAddr);
        parcel.writeBoolean(this.changePrefixOnDecline);
        parcel.writeInt(this.leasesSubnetPrefixLength);
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
                this.serverAddr = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.serverAddrPrefixLength = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.defaultRouters = parcel.createIntArray();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.dnsServers = parcel.createIntArray();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.excludedAddrs = parcel.createIntArray();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.dhcpLeaseTimeSecs = parcel.readLong();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.linkMtu = parcel.readInt();
                                        if (parcel.dataPosition() - dataPosition < readInt) {
                                            this.metered = parcel.readBoolean();
                                            if (parcel.dataPosition() - dataPosition < readInt) {
                                                this.singleClientAddr = parcel.readInt();
                                                if (parcel.dataPosition() - dataPosition < readInt) {
                                                    this.changePrefixOnDecline = parcel.readBoolean();
                                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                                        this.leasesSubnetPrefixLength = parcel.readInt();
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
        stringJoiner.add("serverAddr: " + this.serverAddr);
        stringJoiner.add("serverAddrPrefixLength: " + this.serverAddrPrefixLength);
        stringJoiner.add("defaultRouters: " + Arrays.toString(this.defaultRouters));
        stringJoiner.add("dnsServers: " + Arrays.toString(this.dnsServers));
        stringJoiner.add("excludedAddrs: " + Arrays.toString(this.excludedAddrs));
        stringJoiner.add("dhcpLeaseTimeSecs: " + this.dhcpLeaseTimeSecs);
        stringJoiner.add("linkMtu: " + this.linkMtu);
        stringJoiner.add("metered: " + this.metered);
        stringJoiner.add("singleClientAddr: " + this.singleClientAddr);
        stringJoiner.add("changePrefixOnDecline: " + this.changePrefixOnDecline);
        stringJoiner.add("leasesSubnetPrefixLength: " + this.leasesSubnetPrefixLength);
        return "android.net.dhcp.DhcpServingParamsParcel" + stringJoiner.toString();
    }
}
