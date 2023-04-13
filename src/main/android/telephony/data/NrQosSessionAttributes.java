package android.telephony.data;

import android.annotation.SystemApi;
import android.net.QosSessionAttributes;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class NrQosSessionAttributes implements Parcelable, QosSessionAttributes {
    private final int m5Qi;
    private final long mAveragingWindow;
    private final long mGuaranteedDownlinkBitRate;
    private final long mGuaranteedUplinkBitRate;
    private final long mMaxDownlinkBitRate;
    private final long mMaxUplinkBitRate;
    private final int mQfi;
    private final List<InetSocketAddress> mRemoteAddresses;
    private static final String TAG = NrQosSessionAttributes.class.getSimpleName();
    public static final Parcelable.Creator<NrQosSessionAttributes> CREATOR = new Parcelable.Creator<NrQosSessionAttributes>() { // from class: android.telephony.data.NrQosSessionAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrQosSessionAttributes createFromParcel(Parcel in) {
            return new NrQosSessionAttributes(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrQosSessionAttributes[] newArray(int size) {
            return new NrQosSessionAttributes[size];
        }
    };

    public int getQosIdentifier() {
        return this.m5Qi;
    }

    public int getQosFlowIdentifier() {
        return this.mQfi;
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

    public Duration getBitRateWindowDuration() {
        return Duration.ofMillis(this.mAveragingWindow);
    }

    public List<InetSocketAddress> getRemoteAddresses() {
        return this.mRemoteAddresses;
    }

    public NrQosSessionAttributes(int fiveQi, int qfi, long maxDownlinkBitRate, long maxUplinkBitRate, long guaranteedDownlinkBitRate, long guaranteedUplinkBitRate, long averagingWindow, List<InetSocketAddress> remoteAddresses) {
        Objects.requireNonNull(remoteAddresses, "remoteAddress must be non-null");
        this.m5Qi = fiveQi;
        this.mQfi = qfi;
        this.mMaxDownlinkBitRate = maxDownlinkBitRate;
        this.mMaxUplinkBitRate = maxUplinkBitRate;
        this.mGuaranteedDownlinkBitRate = guaranteedDownlinkBitRate;
        this.mGuaranteedUplinkBitRate = guaranteedUplinkBitRate;
        this.mAveragingWindow = averagingWindow;
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

    private NrQosSessionAttributes(Parcel in) {
        this.m5Qi = in.readInt();
        this.mQfi = in.readInt();
        this.mMaxDownlinkBitRate = in.readLong();
        this.mMaxUplinkBitRate = in.readLong();
        this.mGuaranteedDownlinkBitRate = in.readLong();
        this.mGuaranteedUplinkBitRate = in.readLong();
        this.mAveragingWindow = in.readLong();
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
        dest.writeInt(this.m5Qi);
        dest.writeInt(this.mQfi);
        dest.writeLong(this.mMaxDownlinkBitRate);
        dest.writeLong(this.mMaxUplinkBitRate);
        dest.writeLong(this.mGuaranteedDownlinkBitRate);
        dest.writeLong(this.mGuaranteedUplinkBitRate);
        dest.writeLong(this.mAveragingWindow);
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
        NrQosSessionAttributes nrQosAttr = (NrQosSessionAttributes) o;
        if (this.m5Qi == nrQosAttr.m5Qi && this.mQfi == nrQosAttr.mQfi && this.mMaxUplinkBitRate == nrQosAttr.mMaxUplinkBitRate && this.mMaxDownlinkBitRate == nrQosAttr.mMaxDownlinkBitRate && this.mGuaranteedUplinkBitRate == nrQosAttr.mGuaranteedUplinkBitRate && this.mGuaranteedDownlinkBitRate == nrQosAttr.mGuaranteedDownlinkBitRate && this.mAveragingWindow == nrQosAttr.mAveragingWindow && this.mRemoteAddresses.size() == nrQosAttr.mRemoteAddresses.size() && this.mRemoteAddresses.containsAll(nrQosAttr.mRemoteAddresses)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.m5Qi), Integer.valueOf(this.mQfi), Long.valueOf(this.mMaxUplinkBitRate), Long.valueOf(this.mMaxDownlinkBitRate), Long.valueOf(this.mGuaranteedUplinkBitRate), Long.valueOf(this.mGuaranteedDownlinkBitRate), Long.valueOf(this.mAveragingWindow), this.mRemoteAddresses);
    }
}
