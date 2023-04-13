package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsSsInfo implements Parcelable {
    public static final int CLIR_OUTGOING_DEFAULT = 0;
    public static final int CLIR_OUTGOING_INVOCATION = 1;
    public static final int CLIR_OUTGOING_SUPPRESSION = 2;
    public static final int CLIR_STATUS_NOT_PROVISIONED = 0;
    public static final int CLIR_STATUS_PROVISIONED_PERMANENT = 1;
    public static final int CLIR_STATUS_TEMPORARILY_ALLOWED = 4;
    public static final int CLIR_STATUS_TEMPORARILY_RESTRICTED = 3;
    public static final int CLIR_STATUS_UNKNOWN = 2;
    public static final Parcelable.Creator<ImsSsInfo> CREATOR = new Parcelable.Creator<ImsSsInfo>() { // from class: android.telephony.ims.ImsSsInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSsInfo createFromParcel(Parcel in) {
            return new ImsSsInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSsInfo[] newArray(int size) {
            return new ImsSsInfo[size];
        }
    };
    public static final int DISABLED = 0;
    public static final int ENABLED = 1;
    public static final int NOT_REGISTERED = -1;
    public static final int SERVICE_NOT_PROVISIONED = 0;
    public static final int SERVICE_PROVISIONED = 1;
    public static final int SERVICE_PROVISIONING_UNKNOWN = -1;
    private int mClirInterrogationStatus;
    private int mClirOutgoingState;
    public String mIcbNum;
    public int mProvisionStatus;
    public int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ClirInterrogationStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ClirOutgoingState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceProvisionStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceStatus {
    }

    public ImsSsInfo() {
        this.mProvisionStatus = -1;
        this.mClirInterrogationStatus = 2;
        this.mClirOutgoingState = 0;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final ImsSsInfo mImsSsInfo;

        public Builder(int status) {
            ImsSsInfo imsSsInfo = new ImsSsInfo();
            this.mImsSsInfo = imsSsInfo;
            imsSsInfo.mStatus = status;
        }

        public Builder setIncomingCommunicationBarringNumber(String number) {
            this.mImsSsInfo.mIcbNum = number;
            return this;
        }

        public Builder setProvisionStatus(int provisionStatus) {
            this.mImsSsInfo.mProvisionStatus = provisionStatus;
            return this;
        }

        public Builder setClirInterrogationStatus(int status) {
            this.mImsSsInfo.mClirInterrogationStatus = status;
            return this;
        }

        public Builder setClirOutgoingState(int state) {
            this.mImsSsInfo.mClirOutgoingState = state;
            return this;
        }

        public ImsSsInfo build() {
            return this.mImsSsInfo;
        }
    }

    @Deprecated
    public ImsSsInfo(int status, String icbNum) {
        this.mProvisionStatus = -1;
        this.mClirInterrogationStatus = 2;
        this.mClirOutgoingState = 0;
        this.mStatus = status;
        this.mIcbNum = icbNum;
    }

    private ImsSsInfo(Parcel in) {
        this.mProvisionStatus = -1;
        this.mClirInterrogationStatus = 2;
        this.mClirOutgoingState = 0;
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStatus);
        out.writeString(this.mIcbNum);
        out.writeInt(this.mProvisionStatus);
        out.writeInt(this.mClirInterrogationStatus);
        out.writeInt(this.mClirOutgoingState);
    }

    public String toString() {
        return super.toString() + ", Status: " + (this.mStatus == 0 ? "disabled" : "enabled") + ", ProvisionStatus: " + provisionStatusToString(this.mProvisionStatus);
    }

    private static String provisionStatusToString(int pStatus) {
        switch (pStatus) {
            case 0:
                return "Service not provisioned";
            case 1:
                return "Service provisioned";
            default:
                return "Service provisioning unknown";
        }
    }

    private void readFromParcel(Parcel in) {
        this.mStatus = in.readInt();
        this.mIcbNum = in.readString();
        this.mProvisionStatus = in.readInt();
        this.mClirInterrogationStatus = in.readInt();
        this.mClirOutgoingState = in.readInt();
    }

    public int getStatus() {
        return this.mStatus;
    }

    @Deprecated
    public String getIcbNum() {
        return this.mIcbNum;
    }

    public String getIncomingCommunicationBarringNumber() {
        return this.mIcbNum;
    }

    public int getProvisionStatus() {
        return this.mProvisionStatus;
    }

    public int getClirOutgoingState() {
        return this.mClirOutgoingState;
    }

    public int getClirInterrogationStatus() {
        return this.mClirInterrogationStatus;
    }

    public int[] getCompatArray(int type) {
        int[] result = new int[2];
        if (type == 8) {
            result[0] = getClirOutgoingState();
            result[1] = getClirInterrogationStatus();
            return result;
        }
        if (type == 10) {
            result[0] = getProvisionStatus();
        }
        result[0] = getStatus();
        result[1] = getProvisionStatus();
        return result;
    }
}
