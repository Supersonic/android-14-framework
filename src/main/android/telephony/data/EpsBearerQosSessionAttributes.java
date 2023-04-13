package android.telephony.data;

import android.annotation.SystemApi;
import android.net.QosSessionAttributes;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class EpsBearerQosSessionAttributes implements Parcelable, QosSessionAttributes {
    private final long mGuaranteedDownlinkBitRate;
    private final long mGuaranteedUplinkBitRate;
    private final long mMaxDownlinkBitRate;
    private final long mMaxUplinkBitRate;
    private final int mQci;
    private final List<InetSocketAddress> mRemoteAddresses;
    private static final String TAG = EpsBearerQosSessionAttributes.class.getSimpleName();
    public static final Parcelable.Creator<EpsBearerQosSessionAttributes> CREATOR = new Parcelable.Creator<EpsBearerQosSessionAttributes>() { // from class: android.telephony.data.EpsBearerQosSessionAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EpsBearerQosSessionAttributes createFromParcel(Parcel in) {
            return new EpsBearerQosSessionAttributes(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EpsBearerQosSessionAttributes[] newArray(int size) {
            return new EpsBearerQosSessionAttributes[size];
        }
    };

    public int getQosIdentifier() {
        return this.mQci;
    }

    public long getGuaranteedUplinkBitRateKbps() {
        return this.mGuaranteedUplinkBitRate;
    }

    public long getGuaranteedDownlinkBitRateKbps() {
        return this.mGuaranteedDownlinkBitRate;
    }

    public long getMaxUplinkBitRateKbps() {
        return this.mMaxUplinkBitRate;
    }

    public long getMaxDownlinkBitRateKbps() {
        return this.mMaxDownlinkBitRate;
    }

    public List<InetSocketAddress> getRemoteAddresses() {
        return this.mRemoteAddresses;
    }

    public EpsBearerQosSessionAttributes(int qci, long maxDownlinkBitRate, long maxUplinkBitRate, long guaranteedDownlinkBitRate, long guaranteedUplinkBitRate, List<InetSocketAddress> remoteAddresses) {
        Objects.requireNonNull(remoteAddresses, "remoteAddress must be non-null");
        this.mQci = qci;
        this.mMaxDownlinkBitRate = maxDownlinkBitRate;
        this.mMaxUplinkBitRate = maxUplinkBitRate;
        this.mGuaranteedDownlinkBitRate = guaranteedDownlinkBitRate;
        this.mGuaranteedUplinkBitRate = guaranteedUplinkBitRate;
        List<InetSocketAddress> remoteAddressesTemp = copySocketAddresses(remoteAddresses);
        this.mRemoteAddresses = Collections.unmodifiableList(remoteAddressesTemp);
    }

    private static List<InetSocketAddress> copySocketAddresses(List<InetSocketAddress> remoteAddresses) {
        List<InetSocketAddress> remoteAddressesTemp = new ArrayList<>();
        for (InetSocketAddress socketAddress : remoteAddresses) {
            if (socketAddress != null && socketAddress.getAddress() != null) {
                remoteAddressesTemp.add(socketAddress);
            }
        }
        return remoteAddressesTemp;
    }

    private EpsBearerQosSessionAttributes(Parcel in) {
        this.mQci = in.readInt();
        this.mMaxDownlinkBitRate = in.readLong();
        this.mMaxUplinkBitRate = in.readLong();
        this.mGuaranteedDownlinkBitRate = in.readLong();
        this.mGuaranteedUplinkBitRate = in.readLong();
        int size = in.readInt();
        List<InetSocketAddress> remoteAddresses = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            byte[] addressBytes = in.createByteArray();
            int port = in.readInt();
            try {
                remoteAddresses.add(new InetSocketAddress(InetAddress.getByAddress(addressBytes), port));
            } catch (UnknownHostException e) {
                Log.m109e(TAG, "unable to unparcel remote address at index: " + i, e);
            }
        }
        this.mRemoteAddresses = Collections.unmodifiableList(remoteAddresses);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mQci);
        dest.writeLong(this.mMaxDownlinkBitRate);
        dest.writeLong(this.mMaxUplinkBitRate);
        dest.writeLong(this.mGuaranteedDownlinkBitRate);
        dest.writeLong(this.mGuaranteedUplinkBitRate);
        int size = this.mRemoteAddresses.size();
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            InetSocketAddress address = this.mRemoteAddresses.get(i);
            dest.writeByteArray(address.getAddress().getAddress());
            dest.writeInt(address.getPort());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EpsBearerQosSessionAttributes epsBearerAttr = (EpsBearerQosSessionAttributes) o;
        if (this.mQci == epsBearerAttr.mQci && this.mMaxUplinkBitRate == epsBearerAttr.mMaxUplinkBitRate && this.mMaxDownlinkBitRate == epsBearerAttr.mMaxDownlinkBitRate && this.mGuaranteedUplinkBitRate == epsBearerAttr.mGuaranteedUplinkBitRate && this.mGuaranteedDownlinkBitRate == epsBearerAttr.mGuaranteedDownlinkBitRate && this.mRemoteAddresses.size() == epsBearerAttr.mRemoteAddresses.size() && this.mRemoteAddresses.containsAll(epsBearerAttr.mRemoteAddresses)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mQci), Long.valueOf(this.mMaxUplinkBitRate), Long.valueOf(this.mMaxDownlinkBitRate), Long.valueOf(this.mGuaranteedUplinkBitRate), Long.valueOf(this.mGuaranteedDownlinkBitRate), this.mRemoteAddresses);
    }
}
