package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class InitialConfigurationParcelable implements Parcelable {
    public static final Parcelable.Creator<InitialConfigurationParcelable> CREATOR = new Parcelable.Creator<InitialConfigurationParcelable>() { // from class: android.net.InitialConfigurationParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InitialConfigurationParcelable createFromParcel(Parcel parcel) {
            InitialConfigurationParcelable initialConfigurationParcelable = new InitialConfigurationParcelable();
            initialConfigurationParcelable.readFromParcel(parcel);
            return initialConfigurationParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InitialConfigurationParcelable[] newArray(int i) {
            return new InitialConfigurationParcelable[i];
        }
    };
    public IpPrefix[] directlyConnectedRoutes;
    public String[] dnsServers;
    public String gateway;
    public LinkAddress[] ipAddresses;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedArray(this.ipAddresses, i);
        parcel.writeTypedArray(this.directlyConnectedRoutes, i);
        parcel.writeStringArray(this.dnsServers);
        parcel.writeString(this.gateway);
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
                this.ipAddresses = (LinkAddress[]) parcel.createTypedArray(LinkAddress.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.directlyConnectedRoutes = (IpPrefix[]) parcel.createTypedArray(IpPrefix.CREATOR);
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.dnsServers = parcel.createStringArray();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.gateway = parcel.readString();
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
        stringJoiner.add("ipAddresses: " + Arrays.toString(this.ipAddresses));
        stringJoiner.add("directlyConnectedRoutes: " + Arrays.toString(this.directlyConnectedRoutes));
        stringJoiner.add("dnsServers: " + Arrays.toString(this.dnsServers));
        stringJoiner.add("gateway: " + Objects.toString(this.gateway));
        return "android.net.InitialConfigurationParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.directlyConnectedRoutes) | describeContents(this.ipAddresses) | 0;
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
