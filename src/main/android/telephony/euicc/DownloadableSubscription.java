package android.telephony.euicc;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.UiccAccessRule;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public final class DownloadableSubscription implements Parcelable {
    public static final Parcelable.Creator<DownloadableSubscription> CREATOR = new Parcelable.Creator<DownloadableSubscription>() { // from class: android.telephony.euicc.DownloadableSubscription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DownloadableSubscription createFromParcel(Parcel in) {
            return new DownloadableSubscription(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DownloadableSubscription[] newArray(int size) {
            return new DownloadableSubscription[size];
        }
    };
    private List<UiccAccessRule> accessRules;
    private String carrierName;
    private String confirmationCode;
    @Deprecated
    public final String encodedActivationCode;

    public String getEncodedActivationCode() {
        return this.encodedActivationCode;
    }

    private DownloadableSubscription(String encodedActivationCode) {
        this.encodedActivationCode = encodedActivationCode;
    }

    private DownloadableSubscription(Parcel in) {
        this.encodedActivationCode = in.readString();
        this.confirmationCode = in.readString();
        this.carrierName = in.readString();
        ArrayList arrayList = new ArrayList();
        this.accessRules = arrayList;
        in.readTypedList(arrayList, UiccAccessRule.CREATOR);
    }

    private DownloadableSubscription(String encodedActivationCode, String confirmationCode, String carrierName, List<UiccAccessRule> accessRules) {
        this.encodedActivationCode = encodedActivationCode;
        this.confirmationCode = confirmationCode;
        this.carrierName = carrierName;
        this.accessRules = accessRules;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        List<UiccAccessRule> accessRules;
        private String carrierName;
        private String confirmationCode;
        private String encodedActivationCode;

        @SystemApi
        public Builder() {
        }

        public Builder(DownloadableSubscription baseSubscription) {
            this.encodedActivationCode = baseSubscription.getEncodedActivationCode();
            this.confirmationCode = baseSubscription.getConfirmationCode();
            this.carrierName = baseSubscription.getCarrierName();
            this.accessRules = baseSubscription.getAccessRules();
        }

        public Builder(String encodedActivationCode) {
            this.encodedActivationCode = encodedActivationCode;
        }

        public DownloadableSubscription build() {
            return new DownloadableSubscription(this.encodedActivationCode, this.confirmationCode, this.carrierName, this.accessRules);
        }

        public Builder setEncodedActivationCode(String value) {
            this.encodedActivationCode = value;
            return this;
        }

        public Builder setConfirmationCode(String value) {
            this.confirmationCode = value;
            return this;
        }

        @SystemApi
        public Builder setCarrierName(String value) {
            this.carrierName = value;
            return this;
        }

        @SystemApi
        public Builder setAccessRules(List<UiccAccessRule> value) {
            this.accessRules = value;
            return this;
        }
    }

    public static DownloadableSubscription forActivationCode(String encodedActivationCode) {
        Preconditions.checkNotNull(encodedActivationCode, "Activation code may not be null");
        return new DownloadableSubscription(encodedActivationCode);
    }

    @Deprecated
    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getConfirmationCode() {
        return this.confirmationCode;
    }

    @Deprecated
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    @SystemApi
    public String getCarrierName() {
        return this.carrierName;
    }

    @SystemApi
    public List<UiccAccessRule> getAccessRules() {
        return this.accessRules;
    }

    @Deprecated
    public void setAccessRules(List<UiccAccessRule> accessRules) {
        this.accessRules = accessRules;
    }

    @Deprecated
    public void setAccessRules(UiccAccessRule[] accessRules) {
        this.accessRules = Arrays.asList(accessRules);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.encodedActivationCode);
        dest.writeString(this.confirmationCode);
        dest.writeString(this.carrierName);
        dest.writeTypedList(this.accessRules);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
