package android.net;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class TelephonyNetworkSpecifier extends NetworkSpecifier implements Parcelable {
    public static final Parcelable.Creator<TelephonyNetworkSpecifier> CREATOR = new Parcelable.Creator<TelephonyNetworkSpecifier>() { // from class: android.net.TelephonyNetworkSpecifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyNetworkSpecifier createFromParcel(Parcel in) {
            int subId = in.readInt();
            return new TelephonyNetworkSpecifier(subId);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyNetworkSpecifier[] newArray(int size) {
            return new TelephonyNetworkSpecifier[size];
        }
    };
    private final int mSubId;

    public int getSubscriptionId() {
        return this.mSubId;
    }

    public TelephonyNetworkSpecifier(int subId) {
        this.mSubId = subId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSubId);
    }

    public int hashCode() {
        return this.mSubId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TelephonyNetworkSpecifier) {
            TelephonyNetworkSpecifier lhs = (TelephonyNetworkSpecifier) obj;
            return this.mSubId == lhs.mSubId;
        }
        return false;
    }

    public String toString() {
        return "TelephonyNetworkSpecifier [mSubId = " + this.mSubId + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.net.NetworkSpecifier
    public boolean canBeSatisfiedBy(NetworkSpecifier other) {
        return equals(other) || (other instanceof MatchAllNetworkSpecifier);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private static final int SENTINEL_SUB_ID = Integer.MIN_VALUE;
        private int mSubId = Integer.MIN_VALUE;

        public Builder setSubscriptionId(int subId) {
            this.mSubId = subId;
            return this;
        }

        public TelephonyNetworkSpecifier build() {
            if (this.mSubId == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Subscription Id is not provided.");
            }
            return new TelephonyNetworkSpecifier(this.mSubId);
        }
    }
}
