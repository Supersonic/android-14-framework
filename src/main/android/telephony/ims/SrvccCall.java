package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class SrvccCall implements Parcelable {
    public static final Parcelable.Creator<SrvccCall> CREATOR = new Parcelable.Creator<SrvccCall>() { // from class: android.telephony.ims.SrvccCall.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SrvccCall createFromParcel(Parcel in) {
            return new SrvccCall(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SrvccCall[] newArray(int size) {
            return new SrvccCall[size];
        }
    };
    private static final String TAG = "SrvccCall";
    private String mCallId;
    private int mCallState;
    private ImsCallProfile mImsCallProfile;

    private SrvccCall(Parcel in) {
        readFromParcel(in);
    }

    public SrvccCall(String callId, int callState, ImsCallProfile imsCallProfile) {
        if (callId == null) {
            throw new IllegalArgumentException("callId is null");
        }
        if (imsCallProfile == null) {
            throw new IllegalArgumentException("imsCallProfile is null");
        }
        this.mCallId = callId;
        this.mCallState = callState;
        this.mImsCallProfile = imsCallProfile;
    }

    public ImsCallProfile getImsCallProfile() {
        return this.mImsCallProfile;
    }

    public String getCallId() {
        return this.mCallId;
    }

    public int getPreciseCallState() {
        return this.mCallState;
    }

    public String toString() {
        return "{ callId=" + this.mCallId + ", callState=" + this.mCallState + ", imsCallProfile=" + this.mImsCallProfile + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SrvccCall that = (SrvccCall) o;
        if (this.mImsCallProfile.equals(that.mImsCallProfile) && this.mCallId.equals(that.mCallId) && this.mCallState == that.mCallState) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(this.mImsCallProfile, this.mCallId);
        return (result * 31) + this.mCallState;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mCallId);
        out.writeInt(this.mCallState);
        out.writeParcelable(this.mImsCallProfile, 0);
    }

    private void readFromParcel(Parcel in) {
        this.mCallId = in.readString();
        this.mCallState = in.readInt();
        this.mImsCallProfile = (ImsCallProfile) in.readParcelable(ImsCallProfile.class.getClassLoader(), ImsCallProfile.class);
    }
}
