package android.net.networkstack.aidl;

import android.net.LinkProperties;
import android.net.NetworkAgentConfig;
import android.net.NetworkCapabilities;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NetworkMonitorParameters implements Parcelable {
    public static final Parcelable.Creator<NetworkMonitorParameters> CREATOR = new Parcelable.Creator<NetworkMonitorParameters>() { // from class: android.net.networkstack.aidl.NetworkMonitorParameters.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkMonitorParameters createFromParcel(Parcel parcel) {
            NetworkMonitorParameters networkMonitorParameters = new NetworkMonitorParameters();
            networkMonitorParameters.readFromParcel(parcel);
            return networkMonitorParameters;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkMonitorParameters[] newArray(int i) {
            return new NetworkMonitorParameters[i];
        }
    };
    public LinkProperties linkProperties;
    public NetworkAgentConfig networkAgentConfig;
    public NetworkCapabilities networkCapabilities;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.networkAgentConfig, i);
        parcel.writeTypedObject(this.networkCapabilities, i);
        parcel.writeTypedObject(this.linkProperties, i);
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
                this.networkAgentConfig = (NetworkAgentConfig) parcel.readTypedObject(NetworkAgentConfig.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.networkCapabilities = (NetworkCapabilities) parcel.readTypedObject(NetworkCapabilities.CREATOR);
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.linkProperties = (LinkProperties) parcel.readTypedObject(LinkProperties.CREATOR);
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
        stringJoiner.add("networkAgentConfig: " + Objects.toString(this.networkAgentConfig));
        stringJoiner.add("networkCapabilities: " + Objects.toString(this.networkCapabilities));
        stringJoiner.add("linkProperties: " + Objects.toString(this.linkProperties));
        return "android.net.networkstack.aidl.NetworkMonitorParameters" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof NetworkMonitorParameters)) {
            NetworkMonitorParameters networkMonitorParameters = (NetworkMonitorParameters) obj;
            return Objects.deepEquals(this.networkAgentConfig, networkMonitorParameters.networkAgentConfig) && Objects.deepEquals(this.networkCapabilities, networkMonitorParameters.networkCapabilities) && Objects.deepEquals(this.linkProperties, networkMonitorParameters.linkProperties);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.networkAgentConfig, this.networkCapabilities, this.linkProperties).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.linkProperties) | describeContents(this.networkAgentConfig) | 0 | describeContents(this.networkCapabilities);
    }

    private int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }
}
