package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class ThermalMitigationRequest implements Parcelable {
    public static final Parcelable.Creator<ThermalMitigationRequest> CREATOR = new Parcelable.Creator<ThermalMitigationRequest>() { // from class: android.telephony.ThermalMitigationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ThermalMitigationRequest createFromParcel(Parcel in) {
            return new ThermalMitigationRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ThermalMitigationRequest[] newArray(int size) {
            return new ThermalMitigationRequest[size];
        }
    };
    @SystemApi
    public static final int THERMAL_MITIGATION_ACTION_DATA_THROTTLING = 0;
    @SystemApi
    public static final int THERMAL_MITIGATION_ACTION_RADIO_OFF = 2;
    @SystemApi
    public static final int THERMAL_MITIGATION_ACTION_VOICE_ONLY = 1;
    private DataThrottlingRequest mDataThrottlingRequest;
    private int mThermalMitigationAction;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ThermalMitigationAction {
    }

    private ThermalMitigationRequest(int thermalMitigationAction, DataThrottlingRequest dataThrottlingRequest) {
        this.mThermalMitigationAction = thermalMitigationAction;
        this.mDataThrottlingRequest = dataThrottlingRequest;
    }

    private ThermalMitigationRequest(Parcel in) {
        this.mThermalMitigationAction = in.readInt();
        this.mDataThrottlingRequest = (DataThrottlingRequest) in.readParcelable(DataThrottlingRequest.class.getClassLoader(), DataThrottlingRequest.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mThermalMitigationAction);
        dest.writeParcelable(this.mDataThrottlingRequest, 0);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "[ThermalMitigationRequest , thermalMitigationAction=" + this.mThermalMitigationAction + ", dataThrottlingRequest=" + this.mDataThrottlingRequest + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public int getThermalMitigationAction() {
        return this.mThermalMitigationAction;
    }

    public DataThrottlingRequest getDataThrottlingRequest() {
        return this.mDataThrottlingRequest;
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class Builder {
        private DataThrottlingRequest mDataThrottlingRequest;
        private int mThermalMitigationAction = -1;

        public Builder setThermalMitigationAction(int thermalMitigationAction) {
            this.mThermalMitigationAction = thermalMitigationAction;
            return this;
        }

        public Builder setDataThrottlingRequest(DataThrottlingRequest dataThrottlingRequest) {
            this.mDataThrottlingRequest = dataThrottlingRequest;
            return this;
        }

        public ThermalMitigationRequest build() {
            int i = this.mThermalMitigationAction;
            if (i < 0) {
                throw new IllegalArgumentException("thermalMitigationAction was  not set");
            }
            if (i == 0) {
                if (this.mDataThrottlingRequest == null) {
                    throw new IllegalArgumentException("dataThrottlingRequest  cannot be null for THERMAL_MITIGATION_ACTION_DATA_THROTTLING");
                }
            } else if (this.mDataThrottlingRequest != null) {
                throw new IllegalArgumentException("dataThrottlingRequest must be null for THERMAL_MITIGATION_ACTION_VOICE_ONLY and THERMAL_MITIGATION_ACTION_RADIO_OFF");
            }
            return new ThermalMitigationRequest(this.mThermalMitigationAction, this.mDataThrottlingRequest);
        }
    }
}
