package android.telephony.data;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class NetworkSlicingConfig implements Parcelable {
    public static final Parcelable.Creator<NetworkSlicingConfig> CREATOR = new Parcelable.Creator<NetworkSlicingConfig>() { // from class: android.telephony.data.NetworkSlicingConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkSlicingConfig createFromParcel(Parcel source) {
            return new NetworkSlicingConfig(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkSlicingConfig[] newArray(int size) {
            return new NetworkSlicingConfig[size];
        }
    };
    private final List<NetworkSliceInfo> mSliceInfo;
    private final List<UrspRule> mUrspRules;

    public NetworkSlicingConfig() {
        this.mUrspRules = new ArrayList();
        this.mSliceInfo = new ArrayList();
    }

    public NetworkSlicingConfig(List<UrspRule> urspRules, List<NetworkSliceInfo> sliceInfo) {
        this();
        this.mUrspRules.addAll(urspRules);
        this.mSliceInfo.addAll(sliceInfo);
    }

    public NetworkSlicingConfig(Parcel p) {
        this.mUrspRules = p.createTypedArrayList(UrspRule.CREATOR);
        this.mSliceInfo = p.createTypedArrayList(NetworkSliceInfo.CREATOR);
    }

    public List<UrspRule> getUrspRules() {
        return this.mUrspRules;
    }

    public List<NetworkSliceInfo> getSliceInfo() {
        return this.mSliceInfo;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mUrspRules, flags);
        dest.writeTypedList(this.mSliceInfo, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkSlicingConfig that = (NetworkSlicingConfig) o;
        if (this.mUrspRules.size() == that.mUrspRules.size() && this.mUrspRules.containsAll(that.mUrspRules) && this.mSliceInfo.size() == that.mSliceInfo.size() && this.mSliceInfo.containsAll(that.mSliceInfo)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mUrspRules, this.mSliceInfo);
    }

    public String toString() {
        return "{.urspRules = " + this.mUrspRules + ", .sliceInfo = " + this.mSliceInfo + "}";
    }
}
