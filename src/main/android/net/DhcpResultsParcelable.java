package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class DhcpResultsParcelable implements Parcelable {
    public static final Parcelable.Creator<DhcpResultsParcelable> CREATOR = new Parcelable.Creator<DhcpResultsParcelable>() { // from class: android.net.DhcpResultsParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResultsParcelable createFromParcel(Parcel parcel) {
            DhcpResultsParcelable dhcpResultsParcelable = new DhcpResultsParcelable();
            dhcpResultsParcelable.readFromParcel(parcel);
            return dhcpResultsParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResultsParcelable[] newArray(int i) {
            return new DhcpResultsParcelable[i];
        }
    };
    public StaticIpConfiguration baseConfiguration;
    public String captivePortalApiUrl;
    public int leaseDuration = 0;
    public int mtu = 0;
    public String serverAddress;
    public String serverHostName;
    public String vendorInfo;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.baseConfiguration, i);
        parcel.writeInt(this.leaseDuration);
        parcel.writeInt(this.mtu);
        parcel.writeString(this.serverAddress);
        parcel.writeString(this.vendorInfo);
        parcel.writeString(this.serverHostName);
        parcel.writeString(this.captivePortalApiUrl);
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
                this.baseConfiguration = (StaticIpConfiguration) parcel.readTypedObject(StaticIpConfiguration.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.leaseDuration = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.mtu = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.serverAddress = parcel.readString();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.vendorInfo = parcel.readString();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.serverHostName = parcel.readString();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.captivePortalApiUrl = parcel.readString();
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

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("baseConfiguration: " + Objects.toString(this.baseConfiguration));
        stringJoiner.add("leaseDuration: " + this.leaseDuration);
        stringJoiner.add("mtu: " + this.mtu);
        stringJoiner.add("serverAddress: " + Objects.toString(this.serverAddress));
        stringJoiner.add("vendorInfo: " + Objects.toString(this.vendorInfo));
        stringJoiner.add("serverHostName: " + Objects.toString(this.serverHostName));
        stringJoiner.add("captivePortalApiUrl: " + Objects.toString(this.captivePortalApiUrl));
        return "android.net.DhcpResultsParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.baseConfiguration) | 0;
    }

    private int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }
}
