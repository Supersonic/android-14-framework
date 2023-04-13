package android.net.metrics;

import android.annotation.SystemApi;
import android.net.metrics.IpConnectivityLog;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public final class DhcpClientEvent implements IpConnectivityLog.Event {
    public static final Parcelable.Creator<DhcpClientEvent> CREATOR = new Parcelable.Creator<DhcpClientEvent>() { // from class: android.net.metrics.DhcpClientEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DhcpClientEvent createFromParcel(Parcel in) {
            return new DhcpClientEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DhcpClientEvent[] newArray(int size) {
            return new DhcpClientEvent[size];
        }
    };
    public final int durationMs;
    public final String msg;

    private DhcpClientEvent(String msg, int durationMs) {
        this.msg = msg;
        this.durationMs = durationMs;
    }

    private DhcpClientEvent(Parcel in) {
        this.msg = in.readString();
        this.durationMs = in.readInt();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mDurationMs;
        private String mMsg;

        public Builder setMsg(String msg) {
            this.mMsg = msg;
            return this;
        }

        public Builder setDurationMs(int durationMs) {
            this.mDurationMs = durationMs;
            return this;
        }

        public DhcpClientEvent build() {
            return new DhcpClientEvent(this.mMsg, this.mDurationMs);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.msg);
        out.writeInt(this.durationMs);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.format("DhcpClientEvent(%s, %dms)", this.msg, Integer.valueOf(this.durationMs));
    }

    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(DhcpClientEvent.class)) {
            return false;
        }
        DhcpClientEvent other = (DhcpClientEvent) obj;
        return TextUtils.equals(this.msg, other.msg) && this.durationMs == other.durationMs;
    }
}
