package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class CallState implements Parcelable {
    public static final int CALL_CLASSIFICATION_BACKGROUND = 2;
    public static final int CALL_CLASSIFICATION_FOREGROUND = 1;
    public static final int CALL_CLASSIFICATION_MAX = 3;
    public static final int CALL_CLASSIFICATION_RINGING = 0;
    public static final int CALL_CLASSIFICATION_UNKNOWN = -1;
    public static final Parcelable.Creator<CallState> CREATOR = new Parcelable.Creator() { // from class: android.telephony.CallState.1
        @Override // android.p008os.Parcelable.Creator
        public CallState createFromParcel(Parcel in) {
            return new CallState(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public CallState[] newArray(int size) {
            return new CallState[size];
        }
    };
    private final int mCallClassification;
    private final CallQuality mCallQuality;
    private String mImsCallId;
    private int mImsCallServiceType;
    private int mImsCallType;
    private final int mNetworkType;
    private final int mPreciseCallState;

    private CallState(int callState, int networkType, CallQuality callQuality, int callClassification, String imsCallId, int imsCallServiceType, int imsCallType) {
        this.mPreciseCallState = callState;
        this.mNetworkType = networkType;
        this.mCallQuality = callQuality;
        this.mCallClassification = callClassification;
        this.mImsCallId = imsCallId;
        this.mImsCallServiceType = imsCallServiceType;
        this.mImsCallType = imsCallType;
    }

    public String toString() {
        return "mPreciseCallState=" + this.mPreciseCallState + " mNetworkType=" + this.mNetworkType + " mCallQuality=" + this.mCallQuality + " mCallClassification" + this.mCallClassification + " mImsCallId=" + this.mImsCallId + " mImsCallServiceType=" + this.mImsCallServiceType + " mImsCallType=" + this.mImsCallType;
    }

    private CallState(Parcel in) {
        this.mPreciseCallState = in.readInt();
        this.mNetworkType = in.readInt();
        this.mCallQuality = (CallQuality) in.readParcelable(CallQuality.class.getClassLoader(), CallQuality.class);
        this.mCallClassification = in.readInt();
        this.mImsCallId = in.readString();
        this.mImsCallServiceType = in.readInt();
        this.mImsCallType = in.readInt();
    }

    public int getCallState() {
        return this.mPreciseCallState;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    public CallQuality getCallQuality() {
        return this.mCallQuality;
    }

    public int getCallClassification() {
        return this.mCallClassification;
    }

    public String getImsCallSessionId() {
        return this.mImsCallId;
    }

    public int getImsCallServiceType() {
        return this.mImsCallServiceType;
    }

    public int getImsCallType() {
        return this.mImsCallType;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPreciseCallState), Integer.valueOf(this.mNetworkType), this.mCallQuality, Integer.valueOf(this.mCallClassification), this.mImsCallId, Integer.valueOf(this.mImsCallServiceType), Integer.valueOf(this.mImsCallType));
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof CallState) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            CallState s = (CallState) o;
            if (this.mPreciseCallState != s.mPreciseCallState || this.mNetworkType != s.mNetworkType || !Objects.equals(this.mCallQuality, s.mCallQuality) || this.mCallClassification != s.mCallClassification || !Objects.equals(this.mImsCallId, s.mImsCallId) || this.mImsCallType != s.mImsCallType || this.mImsCallServiceType != s.mImsCallServiceType) {
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
        dest.writeInt(this.mPreciseCallState);
        dest.writeInt(this.mNetworkType);
        dest.writeParcelable(this.mCallQuality, flags);
        dest.writeInt(this.mCallClassification);
        dest.writeString(this.mImsCallId);
        dest.writeInt(this.mImsCallServiceType);
        dest.writeInt(this.mImsCallType);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mImsCallId;
        private int mPreciseCallState;
        private int mNetworkType = 0;
        private CallQuality mCallQuality = null;
        private int mCallClassification = -1;
        private int mImsCallServiceType = 0;
        private int mImsCallType = 0;

        public Builder(int preciseCallState) {
            this.mPreciseCallState = preciseCallState;
        }

        public Builder setNetworkType(int networkType) {
            this.mNetworkType = networkType;
            return this;
        }

        public Builder setCallQuality(CallQuality callQuality) {
            this.mCallQuality = callQuality;
            return this;
        }

        public Builder setCallClassification(int classification) {
            this.mCallClassification = classification;
            return this;
        }

        public Builder setImsCallSessionId(String imsCallId) {
            this.mImsCallId = imsCallId;
            return this;
        }

        public Builder setImsCallServiceType(int serviceType) {
            this.mImsCallServiceType = serviceType;
            return this;
        }

        public Builder setImsCallType(int callType) {
            this.mImsCallType = callType;
            return this;
        }

        public CallState build() {
            return new CallState(this.mPreciseCallState, this.mNetworkType, this.mCallQuality, this.mCallClassification, this.mImsCallId, this.mImsCallServiceType, this.mImsCallType);
        }
    }
}
