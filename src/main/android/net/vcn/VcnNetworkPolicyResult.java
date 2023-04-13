package android.net.vcn;

import android.annotation.SystemApi;
import android.net.NetworkCapabilities;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class VcnNetworkPolicyResult implements Parcelable {
    public static final Parcelable.Creator<VcnNetworkPolicyResult> CREATOR = new Parcelable.Creator<VcnNetworkPolicyResult>() { // from class: android.net.vcn.VcnNetworkPolicyResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnNetworkPolicyResult createFromParcel(Parcel in) {
            return new VcnNetworkPolicyResult(in.readBoolean(), (NetworkCapabilities) in.readParcelable(null, NetworkCapabilities.class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnNetworkPolicyResult[] newArray(int size) {
            return new VcnNetworkPolicyResult[size];
        }
    };
    private final boolean mIsTearDownRequested;
    private final NetworkCapabilities mNetworkCapabilities;

    public VcnNetworkPolicyResult(boolean isTearDownRequested, NetworkCapabilities networkCapabilities) {
        Objects.requireNonNull(networkCapabilities, "networkCapabilities must be non-null");
        this.mIsTearDownRequested = isTearDownRequested;
        this.mNetworkCapabilities = networkCapabilities;
    }

    public boolean isTeardownRequested() {
        return this.mIsTearDownRequested;
    }

    public NetworkCapabilities getNetworkCapabilities() {
        return this.mNetworkCapabilities;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mIsTearDownRequested), this.mNetworkCapabilities);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VcnNetworkPolicyResult) {
            VcnNetworkPolicyResult that = (VcnNetworkPolicyResult) o;
            return this.mIsTearDownRequested == that.mIsTearDownRequested && this.mNetworkCapabilities.equals(that.mNetworkCapabilities);
        }
        return false;
    }

    public String toString() {
        return "VcnNetworkPolicyResult { mIsTeardownRequested = " + this.mIsTearDownRequested + ", mNetworkCapabilities" + this.mNetworkCapabilities + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mIsTearDownRequested);
        dest.writeParcelable(this.mNetworkCapabilities, flags);
    }
}
