package android.net.vcn;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.NetworkSpecifier;
import android.net.TelephonyNetworkSpecifier;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class VcnUnderlyingNetworkSpecifier extends NetworkSpecifier implements Parcelable {
    public static final Parcelable.Creator<VcnUnderlyingNetworkSpecifier> CREATOR = new Parcelable.Creator<VcnUnderlyingNetworkSpecifier>() { // from class: android.net.vcn.VcnUnderlyingNetworkSpecifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnUnderlyingNetworkSpecifier createFromParcel(Parcel in) {
            int[] subIds = in.createIntArray();
            return new VcnUnderlyingNetworkSpecifier(subIds);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnUnderlyingNetworkSpecifier[] newArray(int size) {
            return new VcnUnderlyingNetworkSpecifier[size];
        }
    };
    private final int[] mSubIds;

    public VcnUnderlyingNetworkSpecifier(int[] subIds) {
        this.mSubIds = (int[]) Objects.requireNonNull(subIds, "subIds were null");
    }

    public int[] getSubIds() {
        return this.mSubIds;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.mSubIds);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mSubIds);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VcnUnderlyingNetworkSpecifier)) {
            return false;
        }
        VcnUnderlyingNetworkSpecifier lhs = (VcnUnderlyingNetworkSpecifier) obj;
        return Arrays.equals(this.mSubIds, lhs.mSubIds);
    }

    public String toString() {
        return "VcnUnderlyingNetworkSpecifier [mSubIds = " + Arrays.toString(this.mSubIds) + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.net.NetworkSpecifier
    public boolean canBeSatisfiedBy(NetworkSpecifier other) {
        if (other instanceof TelephonyNetworkSpecifier) {
            return ArrayUtils.contains(this.mSubIds, ((TelephonyNetworkSpecifier) other).getSubscriptionId());
        }
        return equals(other);
    }
}
