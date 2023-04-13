package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes.dex */
public final class ConnectEvent extends NetworkEvent implements Parcelable {
    public static final Parcelable.Creator<ConnectEvent> CREATOR = new Parcelable.Creator<ConnectEvent>() { // from class: android.app.admin.ConnectEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectEvent createFromParcel(Parcel in) {
            if (in.readInt() != 2) {
                return null;
            }
            return new ConnectEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectEvent[] newArray(int size) {
            return new ConnectEvent[size];
        }
    };
    private final String mIpAddress;
    private final int mPort;

    public ConnectEvent(String ipAddress, int port, String packageName, long timestamp) {
        super(packageName, timestamp);
        this.mIpAddress = ipAddress;
        this.mPort = port;
    }

    private ConnectEvent(Parcel in) {
        this.mIpAddress = in.readString();
        this.mPort = in.readInt();
        this.mPackageName = in.readString();
        this.mTimestamp = in.readLong();
        this.mId = in.readLong();
    }

    public InetAddress getInetAddress() {
        try {
            return InetAddress.getByName(this.mIpAddress);
        } catch (UnknownHostException e) {
            return InetAddress.getLoopbackAddress();
        }
    }

    public int getPort() {
        return this.mPort;
    }

    public String toString() {
        return String.format("ConnectEvent(%d, %s, %d, %d, %s)", Long.valueOf(this.mId), this.mIpAddress, Integer.valueOf(this.mPort), Long.valueOf(this.mTimestamp), this.mPackageName);
    }

    @Override // android.app.admin.NetworkEvent, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.admin.NetworkEvent, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(2);
        out.writeString(this.mIpAddress);
        out.writeInt(this.mPort);
        out.writeString(this.mPackageName);
        out.writeLong(this.mTimestamp);
        out.writeLong(this.mId);
    }
}
