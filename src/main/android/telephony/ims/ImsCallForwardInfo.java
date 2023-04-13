package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsCallForwardInfo implements Parcelable {
    public static final int CDIV_CF_REASON_ALL = 4;
    public static final int CDIV_CF_REASON_ALL_CONDITIONAL = 5;
    public static final int CDIV_CF_REASON_BUSY = 1;
    public static final int CDIV_CF_REASON_NOT_LOGGED_IN = 6;
    public static final int CDIV_CF_REASON_NOT_REACHABLE = 3;
    public static final int CDIV_CF_REASON_NO_REPLY = 2;
    public static final int CDIV_CF_REASON_UNCONDITIONAL = 0;
    public static final Parcelable.Creator<ImsCallForwardInfo> CREATOR = new Parcelable.Creator<ImsCallForwardInfo>() { // from class: android.telephony.ims.ImsCallForwardInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsCallForwardInfo createFromParcel(Parcel in) {
            return new ImsCallForwardInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsCallForwardInfo[] newArray(int size) {
            return new ImsCallForwardInfo[size];
        }
    };
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_NOT_ACTIVE = 0;
    public static final int TYPE_OF_ADDRESS_INTERNATIONAL = 145;
    public static final int TYPE_OF_ADDRESS_UNKNOWN = 129;
    public int mCondition;
    public String mNumber;
    public int mServiceClass;
    public int mStatus;
    public int mTimeSeconds;
    public int mToA;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallForwardReasons {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallForwardStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TypeOfAddress {
    }

    public ImsCallForwardInfo() {
    }

    public ImsCallForwardInfo(int reason, int status, int toA, int serviceClass, String number, int replyTimerSec) {
        this.mCondition = reason;
        this.mStatus = status;
        this.mToA = toA;
        this.mServiceClass = serviceClass;
        this.mNumber = number;
        this.mTimeSeconds = replyTimerSec;
    }

    public ImsCallForwardInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mCondition);
        out.writeInt(this.mStatus);
        out.writeInt(this.mToA);
        out.writeString(this.mNumber);
        out.writeInt(this.mTimeSeconds);
        out.writeInt(this.mServiceClass);
    }

    public String toString() {
        return super.toString() + ", Condition: " + this.mCondition + ", Status: " + (this.mStatus == 0 ? "disabled" : "enabled") + ", ToA: " + this.mToA + ", Service Class: " + this.mServiceClass + ", Number=" + this.mNumber + ", Time (seconds): " + this.mTimeSeconds;
    }

    private void readFromParcel(Parcel in) {
        this.mCondition = in.readInt();
        this.mStatus = in.readInt();
        this.mToA = in.readInt();
        this.mNumber = in.readString();
        this.mTimeSeconds = in.readInt();
        this.mServiceClass = in.readInt();
    }

    public int getCondition() {
        return this.mCondition;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public int getToA() {
        return this.mToA;
    }

    public int getServiceClass() {
        return this.mServiceClass;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getTimeSeconds() {
        return this.mTimeSeconds;
    }
}
