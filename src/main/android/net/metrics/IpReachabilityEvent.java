package android.net.metrics;

import android.annotation.SystemApi;
import android.net.metrics.IpConnectivityLog;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public final class IpReachabilityEvent implements IpConnectivityLog.Event {
    public static final Parcelable.Creator<IpReachabilityEvent> CREATOR = new Parcelable.Creator<IpReachabilityEvent>() { // from class: android.net.metrics.IpReachabilityEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IpReachabilityEvent createFromParcel(Parcel in) {
            return new IpReachabilityEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IpReachabilityEvent[] newArray(int size) {
            return new IpReachabilityEvent[size];
        }
    };
    public static final int NUD_FAILED = 512;
    public static final int NUD_FAILED_ORGANIC = 1024;
    public static final int PROBE = 256;
    public static final int PROVISIONING_LOST = 768;
    public static final int PROVISIONING_LOST_ORGANIC = 1280;
    public final int eventType;

    public IpReachabilityEvent(int eventType) {
        this.eventType = eventType;
    }

    private IpReachabilityEvent(Parcel in) {
        this.eventType = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.eventType);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        int i = this.eventType;
        int hi = 65280 & i;
        int lo = i & 255;
        String eventName = Decoder.constants.get(hi);
        return String.format("IpReachabilityEvent(%s:%02x)", eventName, Integer.valueOf(lo));
    }

    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(IpReachabilityEvent.class)) {
            return false;
        }
        IpReachabilityEvent other = (IpReachabilityEvent) obj;
        return this.eventType == other.eventType;
    }

    /* loaded from: classes2.dex */
    static final class Decoder {
        static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[]{IpReachabilityEvent.class}, new String[]{"PROBE", "PROVISIONING_", "NUD_"});

        Decoder() {
        }
    }
}
