package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.telephony.Rlog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsSsData implements Parcelable {
    public static final int RESULT_SUCCESS = 0;
    public static final int SERVICE_CLASS_DATA = 2;
    public static final int SERVICE_CLASS_DATA_CIRCUIT_ASYNC = 32;
    public static final int SERVICE_CLASS_DATA_CIRCUIT_SYNC = 16;
    public static final int SERVICE_CLASS_DATA_PACKET_ACCESS = 64;
    public static final int SERVICE_CLASS_DATA_PAD = 128;
    public static final int SERVICE_CLASS_FAX = 4;
    public static final int SERVICE_CLASS_NONE = 0;
    public static final int SERVICE_CLASS_SMS = 8;
    public static final int SERVICE_CLASS_VOICE = 1;
    public static final int SS_ACTIVATION = 0;
    public static final int SS_ALL_BARRING = 18;
    public static final int SS_ALL_DATA_TELESERVICES = 3;
    public static final int SS_ALL_TELESERVICES_EXCEPT_SMS = 5;
    public static final int SS_ALL_TELESEVICES = 1;
    public static final int SS_ALL_TELE_AND_BEARER_SERVICES = 0;
    public static final int SS_BAIC = 16;
    public static final int SS_BAIC_ROAMING = 17;
    public static final int SS_BAOC = 13;
    public static final int SS_BAOIC = 14;
    public static final int SS_BAOIC_EXC_HOME = 15;
    public static final int SS_CFU = 0;
    public static final int SS_CFUT = 6;
    public static final int SS_CF_ALL = 4;
    public static final int SS_CF_ALL_CONDITIONAL = 5;
    public static final int SS_CF_BUSY = 1;
    public static final int SS_CF_NOT_REACHABLE = 3;
    public static final int SS_CF_NO_REPLY = 2;
    public static final int SS_CLIP = 7;
    public static final int SS_CLIR = 8;
    public static final int SS_CNAP = 11;
    public static final int SS_COLP = 9;
    public static final int SS_COLR = 10;
    public static final int SS_DEACTIVATION = 1;
    public static final int SS_ERASURE = 4;
    public static final int SS_INCOMING_BARRING = 20;
    public static final int SS_INCOMING_BARRING_ANONYMOUS = 22;
    public static final int SS_INCOMING_BARRING_DN = 21;
    public static final int SS_INTERROGATION = 2;
    public static final int SS_OUTGOING_BARRING = 19;
    public static final int SS_REGISTRATION = 3;
    public static final int SS_SMS_SERVICES = 4;
    public static final int SS_TELEPHONY = 2;
    public static final int SS_WAIT = 12;
    private List<ImsCallForwardInfo> mCfInfo;
    private List<ImsSsInfo> mImsSsInfo;
    private int[] mSsInfo;
    public final int requestType;
    public final int result;
    public final int serviceClass;
    public final int serviceType;
    public final int teleserviceType;
    private static final String TAG = ImsSsData.class.getCanonicalName();
    public static final Parcelable.Creator<ImsSsData> CREATOR = new Parcelable.Creator<ImsSsData>() { // from class: android.telephony.ims.ImsSsData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSsData createFromParcel(Parcel in) {
            return new ImsSsData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSsData[] newArray(int size) {
            return new ImsSsData[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RequestType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceClassFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TeleserviceType {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private ImsSsData mImsSsData;

        public Builder(int serviceType, int requestType, int teleserviceType, int serviceClass, int result) {
            this.mImsSsData = new ImsSsData(serviceType, requestType, teleserviceType, serviceClass, result);
        }

        public Builder setSuppServiceInfo(List<ImsSsInfo> imsSsInfos) {
            this.mImsSsData.mImsSsInfo = imsSsInfos;
            return this;
        }

        public Builder setCallForwardingInfo(List<ImsCallForwardInfo> imsCallForwardInfos) {
            this.mImsSsData.mCfInfo = imsCallForwardInfos;
            return this;
        }

        public ImsSsData build() {
            return this.mImsSsData;
        }
    }

    public ImsSsData(int serviceType, int requestType, int teleserviceType, int serviceClass, int result) {
        this.serviceType = serviceType;
        this.requestType = requestType;
        this.teleserviceType = teleserviceType;
        this.serviceClass = serviceClass;
        this.result = result;
    }

    private ImsSsData(Parcel in) {
        this.serviceType = in.readInt();
        this.requestType = in.readInt();
        this.teleserviceType = in.readInt();
        this.serviceClass = in.readInt();
        this.result = in.readInt();
        this.mSsInfo = in.createIntArray();
        this.mCfInfo = in.readParcelableList(new ArrayList(), getClass().getClassLoader(), ImsCallForwardInfo.class);
        this.mImsSsInfo = in.readParcelableList(new ArrayList(), getClass().getClassLoader(), ImsSsInfo.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(getServiceType());
        out.writeInt(getRequestType());
        out.writeInt(getTeleserviceType());
        out.writeInt(getServiceClass());
        out.writeInt(getResult());
        out.writeIntArray(this.mSsInfo);
        out.writeParcelableList(this.mCfInfo, 0);
        out.writeParcelableList(this.mImsSsInfo, 0);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean isTypeCF() {
        return getServiceType() == 0 || getServiceType() == 1 || getServiceType() == 2 || getServiceType() == 3 || getServiceType() == 4 || getServiceType() == 5;
    }

    public boolean isTypeCf() {
        return isTypeCF();
    }

    public boolean isTypeUnConditional() {
        return getServiceType() == 0 || getServiceType() == 4;
    }

    public boolean isTypeCW() {
        return getServiceType() == 12;
    }

    public boolean isTypeCw() {
        return isTypeCW();
    }

    public boolean isTypeClip() {
        return getServiceType() == 7;
    }

    public boolean isTypeColr() {
        return getServiceType() == 10;
    }

    public boolean isTypeColp() {
        return getServiceType() == 9;
    }

    public boolean isTypeClir() {
        return getServiceType() == 8;
    }

    public boolean isTypeIcb() {
        return getServiceType() == 21 || getServiceType() == 22;
    }

    public boolean isTypeBarring() {
        return getServiceType() == 13 || getServiceType() == 14 || getServiceType() == 15 || getServiceType() == 16 || getServiceType() == 17 || getServiceType() == 18 || getServiceType() == 19 || getServiceType() == 20;
    }

    public boolean isTypeInterrogation() {
        return getRequestType() == 2;
    }

    public int getRequestType() {
        return this.requestType;
    }

    public int getServiceType() {
        return this.serviceType;
    }

    public int getTeleserviceType() {
        return this.teleserviceType;
    }

    public int getServiceClass() {
        return this.serviceClass;
    }

    public int getResult() {
        return this.result;
    }

    public void setSuppServiceInfo(int[] ssInfo) {
        this.mSsInfo = ssInfo;
    }

    public void setImsSpecificSuppServiceInfo(ImsSsInfo[] imsSsInfo) {
        this.mImsSsInfo = Arrays.asList(imsSsInfo);
    }

    public void setCallForwardingInfo(ImsCallForwardInfo[] cfInfo) {
        this.mCfInfo = Arrays.asList(cfInfo);
    }

    public int[] getSuppServiceInfoCompat() {
        int[] iArr = this.mSsInfo;
        if (iArr != null) {
            return iArr;
        }
        int[] result = new int[2];
        List<ImsSsInfo> list = this.mImsSsInfo;
        if (list == null || list.size() == 0) {
            Rlog.m8e(TAG, "getSuppServiceInfoCompat: Could not parse mImsSsInfo, returning empty int[]");
            return result;
        } else if (isTypeClir()) {
            result[0] = this.mImsSsInfo.get(0).getClirOutgoingState();
            result[1] = this.mImsSsInfo.get(0).getClirInterrogationStatus();
            return result;
        } else {
            if (isTypeColr()) {
                result[0] = this.mImsSsInfo.get(0).getProvisionStatus();
            }
            result[0] = this.mImsSsInfo.get(0).getStatus();
            result[1] = this.mImsSsInfo.get(0).getProvisionStatus();
            return result;
        }
    }

    public List<ImsSsInfo> getSuppServiceInfo() {
        return this.mImsSsInfo;
    }

    public List<ImsCallForwardInfo> getCallForwardInfo() {
        return this.mCfInfo;
    }

    public String toString() {
        return "[ImsSsData] ServiceType: " + getServiceType() + " RequestType: " + getRequestType() + " TeleserviceType: " + getTeleserviceType() + " ServiceClass: " + getServiceClass() + " Result: " + getResult();
    }
}
