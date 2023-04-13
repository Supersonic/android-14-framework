package android.net;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class RssiCurve implements Parcelable {
    public static final Parcelable.Creator<RssiCurve> CREATOR = new Parcelable.Creator<RssiCurve>() { // from class: android.net.RssiCurve.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RssiCurve createFromParcel(Parcel in) {
            return new RssiCurve(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RssiCurve[] newArray(int size) {
            return new RssiCurve[size];
        }
    };
    private static final int DEFAULT_ACTIVE_NETWORK_RSSI_BOOST = 25;
    public final int activeNetworkRssiBoost;
    public final int bucketWidth;
    public final byte[] rssiBuckets;
    public final int start;

    public RssiCurve(int start, int bucketWidth, byte[] rssiBuckets) {
        this(start, bucketWidth, rssiBuckets, 25);
    }

    public RssiCurve(int start, int bucketWidth, byte[] rssiBuckets, int activeNetworkRssiBoost) {
        this.start = start;
        this.bucketWidth = bucketWidth;
        if (rssiBuckets == null || rssiBuckets.length == 0) {
            throw new IllegalArgumentException("rssiBuckets must be at least one element large.");
        }
        this.rssiBuckets = rssiBuckets;
        this.activeNetworkRssiBoost = activeNetworkRssiBoost;
    }

    private RssiCurve(Parcel in) {
        this.start = in.readInt();
        this.bucketWidth = in.readInt();
        int bucketCount = in.readInt();
        byte[] bArr = new byte[bucketCount];
        this.rssiBuckets = bArr;
        in.readByteArray(bArr);
        this.activeNetworkRssiBoost = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.start);
        out.writeInt(this.bucketWidth);
        out.writeInt(this.rssiBuckets.length);
        out.writeByteArray(this.rssiBuckets);
        out.writeInt(this.activeNetworkRssiBoost);
    }

    public byte lookupScore(int rssi) {
        return lookupScore(rssi, false);
    }

    public byte lookupScore(int rssi, boolean isActiveNetwork) {
        if (isActiveNetwork) {
            rssi += this.activeNetworkRssiBoost;
        }
        int index = (rssi - this.start) / this.bucketWidth;
        if (index < 0) {
            index = 0;
        } else {
            byte[] bArr = this.rssiBuckets;
            if (index > bArr.length - 1) {
                index = bArr.length - 1;
            }
        }
        return this.rssiBuckets[index];
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RssiCurve rssiCurve = (RssiCurve) o;
        if (this.start == rssiCurve.start && this.bucketWidth == rssiCurve.bucketWidth && Arrays.equals(this.rssiBuckets, rssiCurve.rssiBuckets) && this.activeNetworkRssiBoost == rssiCurve.activeNetworkRssiBoost) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.start), Integer.valueOf(this.bucketWidth), Integer.valueOf(this.activeNetworkRssiBoost)) ^ Arrays.hashCode(this.rssiBuckets);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RssiCurve[start=").append(this.start).append(",bucketWidth=").append(this.bucketWidth).append(",activeNetworkRssiBoost=").append(this.activeNetworkRssiBoost);
        sb.append(",buckets=");
        int i = 0;
        while (true) {
            byte[] bArr = this.rssiBuckets;
            if (i < bArr.length) {
                sb.append((int) bArr[i]);
                if (i < this.rssiBuckets.length - 1) {
                    sb.append(",");
                }
                i++;
            } else {
                sb.append(NavigationBarInflaterView.SIZE_MOD_END);
                return sb.toString();
            }
        }
    }
}
