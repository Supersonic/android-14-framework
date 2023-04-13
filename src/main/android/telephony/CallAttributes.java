package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
@Deprecated
/* loaded from: classes3.dex */
public final class CallAttributes implements Parcelable {
    public static final Parcelable.Creator<CallAttributes> CREATOR = new Parcelable.Creator() { // from class: android.telephony.CallAttributes.1
        @Override // android.p008os.Parcelable.Creator
        public CallAttributes createFromParcel(Parcel in) {
            return new CallAttributes(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public CallAttributes[] newArray(int size) {
            return new CallAttributes[size];
        }
    };
    private CallQuality mCallQuality;
    private int mNetworkType;
    private PreciseCallState mPreciseCallState;

    public CallAttributes(PreciseCallState state, int networkType, CallQuality callQuality) {
        this.mPreciseCallState = state;
        this.mNetworkType = networkType;
        this.mCallQuality = callQuality;
    }

    public String toString() {
        return "mPreciseCallState=" + this.mPreciseCallState + " mNetworkType=" + this.mNetworkType + " mCallQuality=" + this.mCallQuality;
    }

    private CallAttributes(Parcel in) {
        this.mPreciseCallState = (PreciseCallState) in.readParcelable(PreciseCallState.class.getClassLoader(), PreciseCallState.class);
        this.mNetworkType = in.readInt();
        this.mCallQuality = (CallQuality) in.readParcelable(CallQuality.class.getClassLoader(), CallQuality.class);
    }

    public PreciseCallState getPreciseCallState() {
        return this.mPreciseCallState;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    public CallQuality getCallQuality() {
        return this.mCallQuality;
    }

    public int hashCode() {
        return Objects.hash(this.mPreciseCallState, Integer.valueOf(this.mNetworkType), this.mCallQuality);
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof CallAttributes) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            CallAttributes s = (CallAttributes) o;
            if (!Objects.equals(this.mPreciseCallState, s.mPreciseCallState) || this.mNetworkType != s.mNetworkType || !Objects.equals(this.mCallQuality, s.mCallQuality)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mPreciseCallState, flags);
        dest.writeInt(this.mNetworkType);
        dest.writeParcelable(this.mCallQuality, flags);
    }
}
