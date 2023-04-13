package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class CallForwardingInfo implements Parcelable {
    public static final Parcelable.Creator<CallForwardingInfo> CREATOR = new Parcelable.Creator<CallForwardingInfo>() { // from class: android.telephony.CallForwardingInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallForwardingInfo createFromParcel(Parcel in) {
            return new CallForwardingInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallForwardingInfo[] newArray(int size) {
            return new CallForwardingInfo[size];
        }
    };
    public static final int REASON_ALL = 4;
    public static final int REASON_ALL_CONDITIONAL = 5;
    public static final int REASON_BUSY = 1;
    public static final int REASON_NOT_REACHABLE = 3;
    public static final int REASON_NO_REPLY = 2;
    public static final int REASON_UNCONDITIONAL = 0;
    private static final String TAG = "CallForwardingInfo";
    private boolean mEnabled;
    private String mNumber;
    private int mReason;
    private int mTimeSeconds;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallForwardingReason {
    }

    public CallForwardingInfo(boolean enabled, int reason, String number, int timeSeconds) {
        this.mEnabled = enabled;
        this.mReason = reason;
        this.mNumber = number;
        this.mTimeSeconds = timeSeconds;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public int getReason() {
        return this.mReason;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getTimeoutSeconds() {
        return this.mTimeSeconds;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mNumber);
        out.writeBoolean(this.mEnabled);
        out.writeInt(this.mReason);
        out.writeInt(this.mTimeSeconds);
    }

    private CallForwardingInfo(Parcel in) {
        this.mNumber = in.readString();
        this.mEnabled = in.readBoolean();
        this.mReason = in.readInt();
        this.mTimeSeconds = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CallForwardingInfo) {
            CallForwardingInfo other = (CallForwardingInfo) o;
            return this.mEnabled == other.mEnabled && this.mNumber == other.mNumber && this.mReason == other.mReason && this.mTimeSeconds == other.mTimeSeconds;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mEnabled), this.mNumber, Integer.valueOf(this.mReason), Integer.valueOf(this.mTimeSeconds));
    }

    public String toString() {
        return "[CallForwardingInfo: enabled=" + this.mEnabled + ", reason= " + this.mReason + ", timeSec= " + this.mTimeSeconds + " seconds, number=" + com.android.telephony.Rlog.pii(TAG, this.mNumber) + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
