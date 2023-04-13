package android.net;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.BitSet;
/* loaded from: classes2.dex */
public final class ConnectivityMetricsEvent implements Parcelable {
    public static final Parcelable.Creator<ConnectivityMetricsEvent> CREATOR = new Parcelable.Creator<ConnectivityMetricsEvent>() { // from class: android.net.ConnectivityMetricsEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectivityMetricsEvent createFromParcel(Parcel source) {
            return new ConnectivityMetricsEvent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectivityMetricsEvent[] newArray(int size) {
            return new ConnectivityMetricsEvent[size];
        }
    };
    public Parcelable data;
    public String ifname;
    public int netId;
    public long timestamp;
    public long transports;

    public ConnectivityMetricsEvent() {
    }

    private ConnectivityMetricsEvent(Parcel in) {
        this.timestamp = in.readLong();
        this.transports = in.readLong();
        this.netId = in.readInt();
        this.ifname = in.readString();
        this.data = in.readParcelable(null);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeLong(this.transports);
        dest.writeInt(this.netId);
        dest.writeString(this.ifname);
        dest.writeParcelable(this.data, 0);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder("ConnectivityMetricsEvent(");
        buffer.append(String.format("%tT.%tL", Long.valueOf(this.timestamp), Long.valueOf(this.timestamp)));
        if (this.netId != 0) {
            buffer.append(", ").append("netId=").append(this.netId);
        }
        if (this.ifname != null) {
            buffer.append(", ").append(this.ifname);
        }
        buffer.append(", transports=").append(BitSet.valueOf(new long[]{this.transports}));
        buffer.append("): ").append(this.data.toString());
        return buffer.toString();
    }
}
