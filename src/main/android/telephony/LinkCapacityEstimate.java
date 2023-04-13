package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class LinkCapacityEstimate implements Parcelable {
    public static final Parcelable.Creator<LinkCapacityEstimate> CREATOR = new Parcelable.Creator() { // from class: android.telephony.LinkCapacityEstimate.1
        @Override // android.p008os.Parcelable.Creator
        public LinkCapacityEstimate createFromParcel(Parcel in) {
            return new LinkCapacityEstimate(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public LinkCapacityEstimate[] newArray(int size) {
            return new LinkCapacityEstimate[size];
        }
    };
    public static final int INVALID = -1;
    public static final int LCE_TYPE_COMBINED = 2;
    public static final int LCE_TYPE_PRIMARY = 0;
    public static final int LCE_TYPE_SECONDARY = 1;
    private final int mDownlinkCapacityKbps;
    private final int mType;
    private final int mUplinkCapacityKbps;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LceType {
    }

    public LinkCapacityEstimate(int type, int downlinkCapacityKbps, int uplinkCapacityKbps) {
        this.mDownlinkCapacityKbps = downlinkCapacityKbps;
        this.mUplinkCapacityKbps = uplinkCapacityKbps;
        this.mType = type;
    }

    public LinkCapacityEstimate(Parcel in) {
        this.mDownlinkCapacityKbps = in.readInt();
        this.mUplinkCapacityKbps = in.readInt();
        this.mType = in.readInt();
    }

    public int getType() {
        return this.mType;
    }

    public int getDownlinkCapacityKbps() {
        return this.mDownlinkCapacityKbps;
    }

    public int getUplinkCapacityKbps() {
        return this.mUplinkCapacityKbps;
    }

    public String toString() {
        return "{mType=" + this.mType + ", mDownlinkCapacityKbps=" + this.mDownlinkCapacityKbps + ", mUplinkCapacityKbps=" + this.mUplinkCapacityKbps + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDownlinkCapacityKbps);
        dest.writeInt(this.mUplinkCapacityKbps);
        dest.writeInt(this.mType);
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof LinkCapacityEstimate) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            LinkCapacityEstimate that = (LinkCapacityEstimate) o;
            if (this.mDownlinkCapacityKbps != that.mDownlinkCapacityKbps || this.mUplinkCapacityKbps != that.mUplinkCapacityKbps || this.mType != that.mType) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDownlinkCapacityKbps), Integer.valueOf(this.mUplinkCapacityKbps), Integer.valueOf(this.mType));
    }
}
