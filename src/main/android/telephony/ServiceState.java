package android.telephony;

import android.annotation.SystemApi;
import android.content.Intent;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.NetworkRegistrationInfo;
import android.text.TextUtils;
import com.android.internal.telephony.DctConstants;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class ServiceState implements Parcelable {
    public static final Parcelable.Creator<ServiceState> CREATOR = new Parcelable.Creator<ServiceState>() { // from class: android.telephony.ServiceState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ServiceState createFromParcel(Parcel in) {
            return new ServiceState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ServiceState[] newArray(int size) {
            return new ServiceState[size];
        }
    };
    static final boolean DBG = false;
    public static final int DUPLEX_MODE_FDD = 1;
    public static final int DUPLEX_MODE_TDD = 2;
    public static final int DUPLEX_MODE_UNKNOWN = 0;
    private static final String EXTRA_SERVICE_STATE = "android.intent.extra.SERVICE_STATE";
    public static final int FREQUENCY_RANGE_COUNT = 5;
    public static final int FREQUENCY_RANGE_HIGH = 3;
    public static final int FREQUENCY_RANGE_LOW = 1;
    public static final int FREQUENCY_RANGE_MID = 2;
    public static final int FREQUENCY_RANGE_MMWAVE = 4;
    public static final int FREQUENCY_RANGE_UNKNOWN = 0;
    static final String LOG_TAG = "PHONE";
    private static final int NEXT_RIL_RADIO_TECHNOLOGY = 21;
    public static final int RIL_RADIO_CDMA_TECHNOLOGY_BITMASK = 6392;
    public static final int RIL_RADIO_TECHNOLOGY_1xRTT = 6;
    public static final int RIL_RADIO_TECHNOLOGY_EDGE = 2;
    public static final int RIL_RADIO_TECHNOLOGY_EHRPD = 13;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_0 = 7;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_A = 8;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_B = 12;
    public static final int RIL_RADIO_TECHNOLOGY_GPRS = 1;
    public static final int RIL_RADIO_TECHNOLOGY_GSM = 16;
    public static final int RIL_RADIO_TECHNOLOGY_HSDPA = 9;
    public static final int RIL_RADIO_TECHNOLOGY_HSPA = 11;
    public static final int RIL_RADIO_TECHNOLOGY_HSPAP = 15;
    public static final int RIL_RADIO_TECHNOLOGY_HSUPA = 10;
    public static final int RIL_RADIO_TECHNOLOGY_IS95A = 4;
    public static final int RIL_RADIO_TECHNOLOGY_IS95B = 5;
    public static final int RIL_RADIO_TECHNOLOGY_IWLAN = 18;
    public static final int RIL_RADIO_TECHNOLOGY_LTE = 14;
    public static final int RIL_RADIO_TECHNOLOGY_LTE_CA = 19;
    public static final int RIL_RADIO_TECHNOLOGY_NR = 20;
    public static final int RIL_RADIO_TECHNOLOGY_TD_SCDMA = 17;
    public static final int RIL_RADIO_TECHNOLOGY_UMTS = 3;
    public static final int RIL_RADIO_TECHNOLOGY_UNKNOWN = 0;
    @SystemApi
    public static final int ROAMING_TYPE_DOMESTIC = 2;
    @SystemApi
    public static final int ROAMING_TYPE_INTERNATIONAL = 3;
    @SystemApi
    public static final int ROAMING_TYPE_NOT_ROAMING = 0;
    @SystemApi
    public static final int ROAMING_TYPE_UNKNOWN = 1;
    public static final int STATE_EMERGENCY_ONLY = 2;
    public static final int STATE_IN_SERVICE = 0;
    public static final int STATE_OUT_OF_SERVICE = 1;
    public static final int STATE_POWER_OFF = 3;
    public static final int UNKNOWN_ID = -1;
    static final boolean VDBG = false;
    private int mArfcnRsrpBoost;
    private int mCdmaDefaultRoamingIndicator;
    private int mCdmaEriIconIndex;
    private int mCdmaEriIconMode;
    private int mCdmaRoamingIndicator;
    private int[] mCellBandwidths;
    private int mChannelNumber;
    private boolean mCssIndicator;
    private int mDataRegState;
    private boolean mIsDataRoamingFromRegistration;
    private boolean mIsEmergencyOnly;
    private boolean mIsIwlanPreferred;
    private boolean mIsManualNetworkSelection;
    private int mNetworkId;
    private final List<NetworkRegistrationInfo> mNetworkRegistrationInfos;
    private int mNrFrequencyRange;
    private String mOperatorAlphaLong;
    private String mOperatorAlphaLongRaw;
    private String mOperatorAlphaShort;
    private String mOperatorAlphaShortRaw;
    private String mOperatorNumeric;
    private int mSystemId;
    private int mVoiceRegState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DuplexMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface FrequencyRange {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RegState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RilRadioTechnology {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RoamingType {
    }

    public static final String getRoamingLogString(int roamingType) {
        switch (roamingType) {
            case 0:
                return "home";
            case 1:
                return "roaming";
            case 2:
                return "Domestic Roaming";
            case 3:
                return "International Roaming";
            default:
                return "UNKNOWN";
        }
    }

    public static ServiceState newFromBundle(Bundle m) {
        ServiceState ret = new ServiceState();
        ret.setFromNotifierBundle(m);
        return ret;
    }

    public ServiceState() {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mCellBandwidths = new int[0];
        this.mArfcnRsrpBoost = 0;
        this.mNetworkRegistrationInfos = new ArrayList();
    }

    public ServiceState(ServiceState s) {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mCellBandwidths = new int[0];
        this.mArfcnRsrpBoost = 0;
        this.mNetworkRegistrationInfos = new ArrayList();
        copyFrom(s);
    }

    protected void copyFrom(ServiceState s) {
        this.mVoiceRegState = s.mVoiceRegState;
        this.mDataRegState = s.mDataRegState;
        this.mOperatorAlphaLong = s.mOperatorAlphaLong;
        this.mOperatorAlphaShort = s.mOperatorAlphaShort;
        this.mOperatorNumeric = s.mOperatorNumeric;
        this.mIsManualNetworkSelection = s.mIsManualNetworkSelection;
        this.mCssIndicator = s.mCssIndicator;
        this.mNetworkId = s.mNetworkId;
        this.mSystemId = s.mSystemId;
        this.mCdmaRoamingIndicator = s.mCdmaRoamingIndicator;
        this.mCdmaDefaultRoamingIndicator = s.mCdmaDefaultRoamingIndicator;
        this.mCdmaEriIconIndex = s.mCdmaEriIconIndex;
        this.mCdmaEriIconMode = s.mCdmaEriIconMode;
        this.mIsEmergencyOnly = s.mIsEmergencyOnly;
        this.mChannelNumber = s.mChannelNumber;
        int[] iArr = s.mCellBandwidths;
        this.mCellBandwidths = iArr == null ? null : Arrays.copyOf(iArr, iArr.length);
        this.mArfcnRsrpBoost = s.mArfcnRsrpBoost;
        synchronized (this.mNetworkRegistrationInfos) {
            this.mNetworkRegistrationInfos.clear();
            for (NetworkRegistrationInfo nri : s.getNetworkRegistrationInfoList()) {
                this.mNetworkRegistrationInfos.add(new NetworkRegistrationInfo(nri));
            }
        }
        this.mNrFrequencyRange = s.mNrFrequencyRange;
        this.mOperatorAlphaLongRaw = s.mOperatorAlphaLongRaw;
        this.mOperatorAlphaShortRaw = s.mOperatorAlphaShortRaw;
        this.mIsDataRoamingFromRegistration = s.mIsDataRoamingFromRegistration;
        this.mIsIwlanPreferred = s.mIsIwlanPreferred;
    }

    @Deprecated
    public ServiceState(Parcel in) {
        boolean z;
        boolean z2;
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mCellBandwidths = new int[0];
        this.mArfcnRsrpBoost = 0;
        ArrayList arrayList = new ArrayList();
        this.mNetworkRegistrationInfos = arrayList;
        this.mVoiceRegState = in.readInt();
        this.mDataRegState = in.readInt();
        this.mOperatorAlphaLong = in.readString();
        this.mOperatorAlphaShort = in.readString();
        this.mOperatorNumeric = in.readString();
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mIsManualNetworkSelection = z;
        if (in.readInt() != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.mCssIndicator = z2;
        this.mNetworkId = in.readInt();
        this.mSystemId = in.readInt();
        this.mCdmaRoamingIndicator = in.readInt();
        this.mCdmaDefaultRoamingIndicator = in.readInt();
        this.mCdmaEriIconIndex = in.readInt();
        this.mCdmaEriIconMode = in.readInt();
        this.mIsEmergencyOnly = in.readInt() != 0;
        this.mArfcnRsrpBoost = in.readInt();
        synchronized (arrayList) {
            in.readList(arrayList, NetworkRegistrationInfo.class.getClassLoader(), NetworkRegistrationInfo.class);
        }
        this.mChannelNumber = in.readInt();
        this.mCellBandwidths = in.createIntArray();
        this.mNrFrequencyRange = in.readInt();
        this.mOperatorAlphaLongRaw = in.readString();
        this.mOperatorAlphaShortRaw = in.readString();
        this.mIsDataRoamingFromRegistration = in.readBoolean();
        this.mIsIwlanPreferred = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mVoiceRegState);
        out.writeInt(this.mDataRegState);
        out.writeString(this.mOperatorAlphaLong);
        out.writeString(this.mOperatorAlphaShort);
        out.writeString(this.mOperatorNumeric);
        out.writeInt(this.mIsManualNetworkSelection ? 1 : 0);
        out.writeInt(this.mCssIndicator ? 1 : 0);
        out.writeInt(this.mNetworkId);
        out.writeInt(this.mSystemId);
        out.writeInt(this.mCdmaRoamingIndicator);
        out.writeInt(this.mCdmaDefaultRoamingIndicator);
        out.writeInt(this.mCdmaEriIconIndex);
        out.writeInt(this.mCdmaEriIconMode);
        out.writeInt(this.mIsEmergencyOnly ? 1 : 0);
        out.writeInt(this.mArfcnRsrpBoost);
        synchronized (this.mNetworkRegistrationInfos) {
            out.writeList(this.mNetworkRegistrationInfos);
        }
        out.writeInt(this.mChannelNumber);
        out.writeIntArray(this.mCellBandwidths);
        out.writeInt(this.mNrFrequencyRange);
        out.writeString(this.mOperatorAlphaLongRaw);
        out.writeString(this.mOperatorAlphaShortRaw);
        out.writeBoolean(this.mIsDataRoamingFromRegistration);
        out.writeBoolean(this.mIsIwlanPreferred);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getState() {
        return getVoiceRegState();
    }

    public int getVoiceRegState() {
        return this.mVoiceRegState;
    }

    public int getDataRegState() {
        return this.mDataRegState;
    }

    public int getDataRegistrationState() {
        return getDataRegState();
    }

    public int getDuplexMode() {
        if (!isPsOnlyTech(getRilDataRadioTechnology())) {
            return 0;
        }
        int band = AccessNetworkUtils.getOperatingBandForEarfcn(this.mChannelNumber);
        return AccessNetworkUtils.getDuplexModeForEutranBand(band);
    }

    public int getChannelNumber() {
        return this.mChannelNumber;
    }

    public int[] getCellBandwidths() {
        int[] iArr = this.mCellBandwidths;
        return iArr == null ? new int[0] : iArr;
    }

    public boolean getRoaming() {
        return getVoiceRoaming() || getDataRoaming();
    }

    public boolean getVoiceRoaming() {
        return getVoiceRoamingType() != 0;
    }

    public int getVoiceRoamingType() {
        NetworkRegistrationInfo regState = getNetworkRegistrationInfo(1, 1);
        if (regState != null) {
            return regState.getRoamingType();
        }
        return 0;
    }

    public boolean getDataRoaming() {
        return getDataRoamingType() != 0;
    }

    public void setDataRoamingFromRegistration(boolean dataRoaming) {
        this.mIsDataRoamingFromRegistration = dataRoaming;
    }

    public boolean getDataRoamingFromRegistration() {
        return this.mIsDataRoamingFromRegistration;
    }

    public int getDataRoamingType() {
        NetworkRegistrationInfo regState = getNetworkRegistrationInfo(2, 1);
        if (regState != null) {
            return regState.getRoamingType();
        }
        return 0;
    }

    public boolean isEmergencyOnly() {
        return this.mIsEmergencyOnly;
    }

    public int getCdmaRoamingIndicator() {
        return this.mCdmaRoamingIndicator;
    }

    public int getCdmaDefaultRoamingIndicator() {
        return this.mCdmaDefaultRoamingIndicator;
    }

    public int getCdmaEriIconIndex() {
        return this.mCdmaEriIconIndex;
    }

    public int getCdmaEriIconMode() {
        return this.mCdmaEriIconMode;
    }

    public String getOperatorAlphaLong() {
        return this.mOperatorAlphaLong;
    }

    public String getVoiceOperatorAlphaLong() {
        return this.mOperatorAlphaLong;
    }

    public String getOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getVoiceOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getDataOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getOperatorAlpha() {
        if (TextUtils.isEmpty(this.mOperatorAlphaLong)) {
            return this.mOperatorAlphaShort;
        }
        return this.mOperatorAlphaLong;
    }

    public String getOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public String getVoiceOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public String getDataOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public boolean getIsManualSelection() {
        return this.mIsManualNetworkSelection;
    }

    public int hashCode() {
        int hash;
        synchronized (this.mNetworkRegistrationInfos) {
            hash = Objects.hash(Integer.valueOf(this.mVoiceRegState), Integer.valueOf(this.mDataRegState), Integer.valueOf(this.mChannelNumber), Integer.valueOf(Arrays.hashCode(this.mCellBandwidths)), this.mOperatorAlphaLong, this.mOperatorAlphaShort, this.mOperatorNumeric, Boolean.valueOf(this.mIsManualNetworkSelection), Boolean.valueOf(this.mCssIndicator), Integer.valueOf(this.mNetworkId), Integer.valueOf(this.mSystemId), Integer.valueOf(this.mCdmaRoamingIndicator), Integer.valueOf(this.mCdmaDefaultRoamingIndicator), Integer.valueOf(this.mCdmaEriIconIndex), Integer.valueOf(this.mCdmaEriIconMode), Boolean.valueOf(this.mIsEmergencyOnly), Integer.valueOf(this.mArfcnRsrpBoost), this.mNetworkRegistrationInfos, Integer.valueOf(this.mNrFrequencyRange), this.mOperatorAlphaLongRaw, this.mOperatorAlphaShortRaw, Boolean.valueOf(this.mIsDataRoamingFromRegistration), Boolean.valueOf(this.mIsIwlanPreferred));
        }
        return hash;
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (o instanceof ServiceState) {
            ServiceState s = (ServiceState) o;
            synchronized (this.mNetworkRegistrationInfos) {
                if (this.mVoiceRegState == s.mVoiceRegState && this.mDataRegState == s.mDataRegState && this.mIsManualNetworkSelection == s.mIsManualNetworkSelection && this.mChannelNumber == s.mChannelNumber && Arrays.equals(this.mCellBandwidths, s.mCellBandwidths) && equalsHandlesNulls(this.mOperatorAlphaLong, s.mOperatorAlphaLong) && equalsHandlesNulls(this.mOperatorAlphaShort, s.mOperatorAlphaShort) && equalsHandlesNulls(this.mOperatorNumeric, s.mOperatorNumeric) && equalsHandlesNulls(Boolean.valueOf(this.mCssIndicator), Boolean.valueOf(s.mCssIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mNetworkId), Integer.valueOf(s.mNetworkId)) && equalsHandlesNulls(Integer.valueOf(this.mSystemId), Integer.valueOf(s.mSystemId)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaRoamingIndicator), Integer.valueOf(s.mCdmaRoamingIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaDefaultRoamingIndicator), Integer.valueOf(s.mCdmaDefaultRoamingIndicator)) && this.mIsEmergencyOnly == s.mIsEmergencyOnly && equalsHandlesNulls(this.mOperatorAlphaLongRaw, s.mOperatorAlphaLongRaw) && equalsHandlesNulls(this.mOperatorAlphaShortRaw, s.mOperatorAlphaShortRaw) && this.mNetworkRegistrationInfos.size() == s.mNetworkRegistrationInfos.size() && this.mNetworkRegistrationInfos.containsAll(s.mNetworkRegistrationInfos) && this.mNrFrequencyRange == s.mNrFrequencyRange && this.mIsDataRoamingFromRegistration == s.mIsDataRoamingFromRegistration && this.mIsIwlanPreferred == s.mIsIwlanPreferred) {
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    public static String roamingTypeToString(int roamingType) {
        switch (roamingType) {
            case 0:
                return "NOT_ROAMING";
            case 1:
                return "UNKNOWN";
            case 2:
                return "DOMESTIC";
            case 3:
                return "INTERNATIONAL";
            default:
                return "Unknown roaming type " + roamingType;
        }
    }

    public static String rilRadioTechnologyToString(int rt) {
        switch (rt) {
            case 0:
                return "Unknown";
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA-IS95A";
            case 5:
                return "CDMA-IS95B";
            case 6:
                return "1xRTT";
            case 7:
                return "EvDo-rev.0";
            case 8:
                return "EvDo-rev.A";
            case 9:
                return "HSDPA";
            case 10:
                return "HSUPA";
            case 11:
                return "HSPA";
            case 12:
                return "EvDo-rev.B";
            case 13:
                return "eHRPD";
            case 14:
                return DctConstants.RAT_NAME_LTE;
            case 15:
                return "HSPAP";
            case 16:
                return "GSM";
            case 17:
                return "TD-SCDMA";
            case 18:
                return "IWLAN";
            case 19:
                return "LTE_CA";
            case 20:
                return "NR_SA";
            default:
                com.android.telephony.Rlog.m2w(LOG_TAG, "Unexpected radioTechnology=" + rt);
                return "Unexpected";
        }
    }

    public static String frequencyRangeToString(int range) {
        switch (range) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "LOW";
            case 2:
                return "MID";
            case 3:
                return "HIGH";
            case 4:
                return "MMWAVE";
            default:
                return Integer.toString(range);
        }
    }

    public static String rilServiceStateToString(int serviceState) {
        switch (serviceState) {
            case 0:
                return "IN_SERVICE";
            case 1:
                return "OUT_OF_SERVICE";
            case 2:
                return "EMERGENCY_ONLY";
            case 3:
                return "POWER_OFF";
            default:
                return "UNKNOWN";
        }
    }

    public String toString() {
        String str;
        synchronized (this.mNetworkRegistrationInfos) {
            str = "{mVoiceRegState=" + this.mVoiceRegState + (NavigationBarInflaterView.KEY_CODE_START + rilServiceStateToString(this.mVoiceRegState) + NavigationBarInflaterView.KEY_CODE_END) + ", mDataRegState=" + this.mDataRegState + (NavigationBarInflaterView.KEY_CODE_START + rilServiceStateToString(this.mDataRegState) + NavigationBarInflaterView.KEY_CODE_END) + ", mChannelNumber=" + this.mChannelNumber + ", duplexMode()=" + getDuplexMode() + ", mCellBandwidths=" + Arrays.toString(this.mCellBandwidths) + ", mOperatorAlphaLong=" + this.mOperatorAlphaLong + ", mOperatorAlphaShort=" + this.mOperatorAlphaShort + ", isManualNetworkSelection=" + this.mIsManualNetworkSelection + (this.mIsManualNetworkSelection ? "(manual)" : "(automatic)") + ", getRilVoiceRadioTechnology=" + getRilVoiceRadioTechnology() + (NavigationBarInflaterView.KEY_CODE_START + rilRadioTechnologyToString(getRilVoiceRadioTechnology()) + NavigationBarInflaterView.KEY_CODE_END) + ", getRilDataRadioTechnology=" + getRilDataRadioTechnology() + (NavigationBarInflaterView.KEY_CODE_START + rilRadioTechnologyToString(getRilDataRadioTechnology()) + NavigationBarInflaterView.KEY_CODE_END) + ", mCssIndicator=" + (this.mCssIndicator ? "supported" : "unsupported") + ", mNetworkId=" + this.mNetworkId + ", mSystemId=" + this.mSystemId + ", mCdmaRoamingIndicator=" + this.mCdmaRoamingIndicator + ", mCdmaDefaultRoamingIndicator=" + this.mCdmaDefaultRoamingIndicator + ", mIsEmergencyOnly=" + this.mIsEmergencyOnly + ", isUsingCarrierAggregation=" + isUsingCarrierAggregation() + ", mArfcnRsrpBoost=" + this.mArfcnRsrpBoost + ", mNetworkRegistrationInfos=" + this.mNetworkRegistrationInfos + ", mNrFrequencyRange=" + (Build.IS_DEBUGGABLE ? this.mNrFrequencyRange : 0) + ", mOperatorAlphaLongRaw=" + this.mOperatorAlphaLongRaw + ", mOperatorAlphaShortRaw=" + this.mOperatorAlphaShortRaw + ", mIsDataRoamingFromRegistration=" + this.mIsDataRoamingFromRegistration + ", mIsIwlanPreferred=" + this.mIsIwlanPreferred + "}";
        }
        return str;
    }

    private void init() {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mChannelNumber = -1;
        this.mCellBandwidths = new int[0];
        this.mOperatorAlphaLong = null;
        this.mOperatorAlphaShort = null;
        this.mOperatorNumeric = null;
        this.mIsManualNetworkSelection = false;
        this.mCssIndicator = false;
        this.mNetworkId = -1;
        this.mSystemId = -1;
        this.mCdmaRoamingIndicator = -1;
        this.mCdmaDefaultRoamingIndicator = -1;
        this.mCdmaEriIconIndex = -1;
        this.mCdmaEriIconMode = -1;
        this.mIsEmergencyOnly = false;
        this.mArfcnRsrpBoost = 0;
        this.mNrFrequencyRange = 0;
        synchronized (this.mNetworkRegistrationInfos) {
            this.mNetworkRegistrationInfos.clear();
            addNetworkRegistrationInfo(new NetworkRegistrationInfo.Builder().setDomain(1).setTransportType(1).setRegistrationState(4).build());
            addNetworkRegistrationInfo(new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).setRegistrationState(4).build());
            addNetworkRegistrationInfo(new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(2).setRegistrationState(4).build());
        }
        this.mOperatorAlphaLongRaw = null;
        this.mOperatorAlphaShortRaw = null;
        this.mIsDataRoamingFromRegistration = false;
        this.mIsIwlanPreferred = false;
    }

    public void setStateOutOfService() {
        init();
    }

    public void setStateOff() {
        init();
        this.mVoiceRegState = 3;
        this.mDataRegState = 3;
    }

    public void setOutOfService(boolean powerOff) {
        init();
        if (powerOff) {
            this.mVoiceRegState = 3;
            this.mDataRegState = 3;
        }
    }

    public void setState(int state) {
        setVoiceRegState(state);
    }

    public void setVoiceRegState(int state) {
        this.mVoiceRegState = state;
    }

    public void setDataRegState(int state) {
        this.mDataRegState = state;
    }

    public void setCellBandwidths(int[] bandwidths) {
        this.mCellBandwidths = bandwidths;
    }

    public void setChannelNumber(int channelNumber) {
        this.mChannelNumber = channelNumber;
    }

    public void setRoaming(boolean roaming) {
        setVoiceRoaming(roaming);
        setDataRoaming(roaming);
    }

    public void setVoiceRoaming(boolean roaming) {
        setVoiceRoamingType(roaming ? 1 : 0);
    }

    public void setVoiceRoamingType(int type) {
        NetworkRegistrationInfo regInfo = getNetworkRegistrationInfo(1, 1);
        if (regInfo == null) {
            regInfo = new NetworkRegistrationInfo.Builder().setDomain(1).setTransportType(1).build();
        }
        regInfo.setRoamingType(type);
        addNetworkRegistrationInfo(regInfo);
    }

    public void setDataRoaming(boolean dataRoaming) {
        setDataRoamingType(dataRoaming ? 1 : 0);
    }

    public void setDataRoamingType(int type) {
        NetworkRegistrationInfo regInfo = getNetworkRegistrationInfo(2, 1);
        if (regInfo == null) {
            regInfo = new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).build();
        }
        regInfo.setRoamingType(type);
        addNetworkRegistrationInfo(regInfo);
    }

    public void setEmergencyOnly(boolean emergencyOnly) {
        this.mIsEmergencyOnly = emergencyOnly;
    }

    public void setCdmaRoamingIndicator(int roaming) {
        this.mCdmaRoamingIndicator = roaming;
    }

    public void setCdmaDefaultRoamingIndicator(int roaming) {
        this.mCdmaDefaultRoamingIndicator = roaming;
    }

    public void setCdmaEriIconIndex(int index) {
        this.mCdmaEriIconIndex = index;
    }

    public void setCdmaEriIconMode(int mode) {
        this.mCdmaEriIconMode = mode;
    }

    public void setOperatorName(String longName, String shortName, String numeric) {
        this.mOperatorAlphaLong = longName;
        this.mOperatorAlphaShort = shortName;
        this.mOperatorNumeric = numeric;
    }

    public void setOperatorAlphaLong(String longName) {
        this.mOperatorAlphaLong = longName;
    }

    public void setIsManualSelection(boolean isManual) {
        this.mIsManualNetworkSelection = isManual;
    }

    private static boolean equalsHandlesNulls(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    private void setFromNotifierBundle(Bundle m) {
        ServiceState ssFromBundle = (ServiceState) m.getParcelable(EXTRA_SERVICE_STATE, ServiceState.class);
        if (ssFromBundle != null) {
            copyFrom(ssFromBundle);
        }
    }

    public void fillInNotifierBundle(Bundle m) {
        m.putParcelable(EXTRA_SERVICE_STATE, this);
        m.putInt(Intent.EXTRA_VOICE_REG_STATE, this.mVoiceRegState);
        m.putInt(Intent.EXTRA_DATA_REG_STATE, this.mDataRegState);
        m.putInt(Intent.EXTRA_DATA_ROAMING_TYPE, getDataRoamingType());
        m.putInt(Intent.EXTRA_VOICE_ROAMING_TYPE, getVoiceRoamingType());
        m.putString(Intent.EXTRA_OPERATOR_ALPHA_LONG, this.mOperatorAlphaLong);
        m.putString(Intent.EXTRA_OPERATOR_ALPHA_SHORT, this.mOperatorAlphaShort);
        m.putString(Intent.EXTRA_OPERATOR_NUMERIC, this.mOperatorNumeric);
        m.putString(Intent.EXTRA_DATA_OPERATOR_ALPHA_LONG, this.mOperatorAlphaLong);
        m.putString(Intent.EXTRA_DATA_OPERATOR_ALPHA_SHORT, this.mOperatorAlphaShort);
        m.putString(Intent.EXTRA_DATA_OPERATOR_NUMERIC, this.mOperatorNumeric);
        m.putBoolean(Intent.EXTRA_MANUAL, this.mIsManualNetworkSelection);
        m.putInt(Intent.EXTRA_VOICE_RADIO_TECH, getRilVoiceRadioTechnology());
        m.putInt(Intent.EXTRA_DATA_RADIO_TECH, getRilDataRadioTechnology());
        m.putBoolean(Intent.EXTRA_CSS_INDICATOR, this.mCssIndicator);
        m.putInt(Intent.EXTRA_NETWORK_ID, this.mNetworkId);
        m.putInt(Intent.EXTRA_SYSTEM_ID, this.mSystemId);
        m.putInt(Intent.EXTRA_CDMA_ROAMING_INDICATOR, this.mCdmaRoamingIndicator);
        m.putInt(Intent.EXTRA_CDMA_DEFAULT_ROAMING_INDICATOR, this.mCdmaDefaultRoamingIndicator);
        m.putBoolean(Intent.EXTRA_EMERGENCY_ONLY, this.mIsEmergencyOnly);
        m.putBoolean(Intent.EXTRA_IS_DATA_ROAMING_FROM_REGISTRATION, getDataRoamingFromRegistration());
        m.putBoolean(Intent.EXTRA_IS_USING_CARRIER_AGGREGATION, isUsingCarrierAggregation());
        m.putInt("ArfcnRsrpBoost", this.mArfcnRsrpBoost);
        m.putInt("ChannelNumber", this.mChannelNumber);
        m.putIntArray("CellBandwidths", this.mCellBandwidths);
        m.putInt("mNrFrequencyRange", this.mNrFrequencyRange);
        m.putString("operator-alpha-long-raw", this.mOperatorAlphaLongRaw);
        m.putString("operator-alpha-short-raw", this.mOperatorAlphaShortRaw);
    }

    public void setRilVoiceRadioTechnology(int rt) {
        com.android.telephony.Rlog.m8e(LOG_TAG, "ServiceState.setRilVoiceRadioTechnology() called. It's encouraged to use addNetworkRegistrationInfo() instead *******");
        NetworkRegistrationInfo regInfo = getNetworkRegistrationInfo(1, 1);
        if (regInfo == null) {
            regInfo = new NetworkRegistrationInfo.Builder().setDomain(1).setTransportType(1).build();
        }
        regInfo.setAccessNetworkTechnology(rilRadioTechnologyToNetworkType(rt));
        addNetworkRegistrationInfo(regInfo);
    }

    public void setRilDataRadioTechnology(int rt) {
        com.android.telephony.Rlog.m8e(LOG_TAG, "ServiceState.setRilDataRadioTechnology() called. It's encouraged to use addNetworkRegistrationInfo() instead *******");
        NetworkRegistrationInfo regInfo = getNetworkRegistrationInfo(2, 1);
        if (regInfo == null) {
            regInfo = new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).build();
        }
        regInfo.setAccessNetworkTechnology(rilRadioTechnologyToNetworkType(rt));
        addNetworkRegistrationInfo(regInfo);
    }

    public boolean isUsingCarrierAggregation() {
        if (getCellBandwidths().length > 1) {
            return true;
        }
        synchronized (this.mNetworkRegistrationInfos) {
            for (NetworkRegistrationInfo nri : this.mNetworkRegistrationInfos) {
                if (nri.isUsingCarrierAggregation()) {
                    return true;
                }
            }
            return false;
        }
    }

    public int getNrFrequencyRange() {
        return this.mNrFrequencyRange;
    }

    public int getNrState() {
        NetworkRegistrationInfo regInfo = getNetworkRegistrationInfo(2, 1);
        if (regInfo == null) {
            return 0;
        }
        return regInfo.getNrState();
    }

    public void setNrFrequencyRange(int nrFrequencyRange) {
        this.mNrFrequencyRange = nrFrequencyRange;
    }

    public int getArfcnRsrpBoost() {
        return this.mArfcnRsrpBoost;
    }

    public void setArfcnRsrpBoost(int arfcnRsrpBoost) {
        this.mArfcnRsrpBoost = arfcnRsrpBoost;
    }

    public void setCssIndicator(int css) {
        this.mCssIndicator = css != 0;
    }

    public void setCdmaSystemAndNetworkId(int systemId, int networkId) {
        this.mSystemId = systemId;
        this.mNetworkId = networkId;
    }

    public int getRilVoiceRadioTechnology() {
        NetworkRegistrationInfo wwanRegInfo = getNetworkRegistrationInfo(1, 1);
        if (wwanRegInfo != null) {
            return networkTypeToRilRadioTechnology(wwanRegInfo.getAccessNetworkTechnology());
        }
        return 0;
    }

    public int getRilDataRadioTechnology() {
        return networkTypeToRilRadioTechnology(getDataNetworkType());
    }

    public static int rilRadioTechnologyToNetworkType(int rat) {
        switch (rat) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
            case 5:
                return 4;
            case 6:
                return 7;
            case 7:
                return 5;
            case 8:
                return 6;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
                return 10;
            case 12:
                return 12;
            case 13:
                return 14;
            case 14:
                return 13;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
            default:
                return 0;
        }
    }

    public static int rilRadioTechnologyToAccessNetworkType(int rt) {
        switch (rt) {
            case 1:
            case 2:
            case 16:
                return 1;
            case 3:
            case 9:
            case 10:
            case 11:
            case 15:
            case 17:
                return 2;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 12:
            case 13:
                return 4;
            case 14:
            case 19:
                return 3;
            case 18:
                return 5;
            case 20:
                return 6;
            default:
                return 0;
        }
    }

    public static int networkTypeToRilRadioTechnology(int networkType) {
        switch (networkType) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 7;
            case 6:
                return 8;
            case 7:
                return 6;
            case 8:
                return 9;
            case 9:
                return 10;
            case 10:
                return 11;
            case 11:
            default:
                return 0;
            case 12:
                return 12;
            case 13:
                return 14;
            case 14:
                return 13;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
        }
    }

    public int getDataNetworkType() {
        NetworkRegistrationInfo iwlanRegInfo = getNetworkRegistrationInfo(2, 2);
        NetworkRegistrationInfo wwanRegInfo = getNetworkRegistrationInfo(2, 1);
        if (iwlanRegInfo == null || !iwlanRegInfo.isInService()) {
            if (wwanRegInfo != null) {
                return wwanRegInfo.getAccessNetworkTechnology();
            }
            return 0;
        } else if (!wwanRegInfo.isInService() || this.mIsIwlanPreferred) {
            return iwlanRegInfo.getAccessNetworkTechnology();
        } else {
            return wwanRegInfo.getAccessNetworkTechnology();
        }
    }

    public int getVoiceNetworkType() {
        NetworkRegistrationInfo regState = getNetworkRegistrationInfo(1, 1);
        if (regState != null) {
            return regState.getAccessNetworkTechnology();
        }
        return 0;
    }

    public int getCssIndicator() {
        return this.mCssIndicator ? 1 : 0;
    }

    public int getCdmaNetworkId() {
        return this.mNetworkId;
    }

    public int getCdmaSystemId() {
        return this.mSystemId;
    }

    public static boolean isGsm(int radioTechnology) {
        return radioTechnology == 1 || radioTechnology == 2 || radioTechnology == 3 || radioTechnology == 9 || radioTechnology == 10 || radioTechnology == 11 || radioTechnology == 14 || radioTechnology == 15 || radioTechnology == 16 || radioTechnology == 17 || radioTechnology == 18 || radioTechnology == 19 || radioTechnology == 20;
    }

    public static boolean isCdma(int radioTechnology) {
        return radioTechnology == 4 || radioTechnology == 5 || radioTechnology == 6 || radioTechnology == 7 || radioTechnology == 8 || radioTechnology == 12 || radioTechnology == 13;
    }

    public static boolean isPsOnlyTech(int radioTechnology) {
        return radioTechnology == 14 || radioTechnology == 19 || radioTechnology == 20;
    }

    public static boolean bearerBitmapHasCdma(int networkTypeBitmask) {
        return (convertNetworkTypeBitmaskToBearerBitmask(networkTypeBitmask) & RIL_RADIO_CDMA_TECHNOLOGY_BITMASK) != 0;
    }

    public static boolean bitmaskHasTech(int bearerBitmask, int radioTech) {
        if (bearerBitmask == 0) {
            return true;
        }
        if (radioTech >= 1 && ((1 << (radioTech - 1)) & bearerBitmask) != 0) {
            return true;
        }
        return false;
    }

    public static int getBitmaskForTech(int radioTech) {
        if (radioTech >= 1) {
            return 1 << (radioTech - 1);
        }
        return 0;
    }

    public static int getBitmaskFromString(String bearerList) {
        String[] bearers = bearerList.split("\\|");
        int bearerBitmask = 0;
        for (String bearer : bearers) {
            try {
                int bearerInt = Integer.parseInt(bearer.trim());
                if (bearerInt == 0) {
                    return 0;
                }
                bearerBitmask |= getBitmaskForTech(bearerInt);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return bearerBitmask;
    }

    public static int convertNetworkTypeBitmaskToBearerBitmask(int networkTypeBitmask) {
        if (networkTypeBitmask == 0) {
            return 0;
        }
        int bearerBitmask = 0;
        for (int bearerInt = 0; bearerInt < 21; bearerInt++) {
            if (bitmaskHasTech(networkTypeBitmask, rilRadioTechnologyToNetworkType(bearerInt))) {
                bearerBitmask |= getBitmaskForTech(bearerInt);
            }
        }
        return bearerBitmask;
    }

    public static int convertBearerBitmaskToNetworkTypeBitmask(int bearerBitmask) {
        if (bearerBitmask == 0) {
            return 0;
        }
        int networkTypeBitmask = 0;
        for (int bearerInt = 0; bearerInt < 21; bearerInt++) {
            if (bitmaskHasTech(bearerBitmask, bearerInt)) {
                networkTypeBitmask |= getBitmaskForTech(rilRadioTechnologyToNetworkType(bearerInt));
            }
        }
        return networkTypeBitmask;
    }

    public static ServiceState mergeServiceStates(ServiceState baseSs, ServiceState voiceSs) {
        if (voiceSs.mVoiceRegState != 0) {
            return baseSs;
        }
        ServiceState newSs = new ServiceState(baseSs);
        newSs.mVoiceRegState = voiceSs.mVoiceRegState;
        newSs.mIsEmergencyOnly = false;
        return newSs;
    }

    public List<NetworkRegistrationInfo> getNetworkRegistrationInfoList() {
        List<NetworkRegistrationInfo> newList;
        synchronized (this.mNetworkRegistrationInfos) {
            newList = new ArrayList<>();
            for (NetworkRegistrationInfo nri : this.mNetworkRegistrationInfos) {
                newList.add(new NetworkRegistrationInfo(nri));
            }
        }
        return newList;
    }

    @SystemApi
    public List<NetworkRegistrationInfo> getNetworkRegistrationInfoListForTransportType(int transportType) {
        List<NetworkRegistrationInfo> list = new ArrayList<>();
        synchronized (this.mNetworkRegistrationInfos) {
            for (NetworkRegistrationInfo networkRegistrationInfo : this.mNetworkRegistrationInfos) {
                if (networkRegistrationInfo.getTransportType() == transportType) {
                    list.add(new NetworkRegistrationInfo(networkRegistrationInfo));
                }
            }
        }
        return list;
    }

    @SystemApi
    public List<NetworkRegistrationInfo> getNetworkRegistrationInfoListForDomain(int domain) {
        List<NetworkRegistrationInfo> list = new ArrayList<>();
        synchronized (this.mNetworkRegistrationInfos) {
            for (NetworkRegistrationInfo networkRegistrationInfo : this.mNetworkRegistrationInfos) {
                if ((networkRegistrationInfo.getDomain() & domain) != 0) {
                    list.add(new NetworkRegistrationInfo(networkRegistrationInfo));
                }
            }
        }
        return list;
    }

    @SystemApi
    public NetworkRegistrationInfo getNetworkRegistrationInfo(int domain, int transportType) {
        synchronized (this.mNetworkRegistrationInfos) {
            for (NetworkRegistrationInfo networkRegistrationInfo : this.mNetworkRegistrationInfos) {
                if (networkRegistrationInfo.getTransportType() == transportType && (networkRegistrationInfo.getDomain() & domain) != 0) {
                    return new NetworkRegistrationInfo(networkRegistrationInfo);
                }
            }
            return null;
        }
    }

    public void addNetworkRegistrationInfo(NetworkRegistrationInfo nri) {
        if (nri == null) {
            return;
        }
        synchronized (this.mNetworkRegistrationInfos) {
            int i = 0;
            while (true) {
                if (i >= this.mNetworkRegistrationInfos.size()) {
                    break;
                }
                NetworkRegistrationInfo curRegState = this.mNetworkRegistrationInfos.get(i);
                if (curRegState.getTransportType() != nri.getTransportType() || curRegState.getDomain() != nri.getDomain()) {
                    i++;
                } else {
                    this.mNetworkRegistrationInfos.remove(i);
                    break;
                }
            }
            this.mNetworkRegistrationInfos.add(new NetworkRegistrationInfo(nri));
        }
    }

    public ServiceState createLocationInfoSanitizedCopy(boolean removeCoarseLocation) {
        ServiceState state = new ServiceState(this);
        synchronized (state.mNetworkRegistrationInfos) {
            List<NetworkRegistrationInfo> networkRegistrationInfos = (List) state.mNetworkRegistrationInfos.stream().map(new Function() { // from class: android.telephony.ServiceState$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((NetworkRegistrationInfo) obj).sanitizeLocationInfo();
                }
            }).collect(Collectors.toList());
            state.mNetworkRegistrationInfos.clear();
            state.mNetworkRegistrationInfos.addAll(networkRegistrationInfos);
        }
        if (removeCoarseLocation) {
            state.mOperatorAlphaLong = null;
            state.mOperatorAlphaShort = null;
            state.mOperatorNumeric = null;
            state.mSystemId = -1;
            state.mNetworkId = -1;
            return state;
        }
        return state;
    }

    public void setOperatorAlphaLongRaw(String operatorAlphaLong) {
        this.mOperatorAlphaLongRaw = operatorAlphaLong;
    }

    public String getOperatorAlphaLongRaw() {
        return this.mOperatorAlphaLongRaw;
    }

    public void setOperatorAlphaShortRaw(String operatorAlphaShort) {
        this.mOperatorAlphaShortRaw = operatorAlphaShort;
    }

    public String getOperatorAlphaShortRaw() {
        return this.mOperatorAlphaShortRaw;
    }

    public void setIwlanPreferred(boolean isIwlanPreferred) {
        this.mIsIwlanPreferred = isIwlanPreferred;
    }

    public boolean isIwlanPreferred() {
        return this.mIsIwlanPreferred;
    }

    public boolean isSearching() {
        NetworkRegistrationInfo psRegState = getNetworkRegistrationInfo(2, 1);
        if (psRegState == null || psRegState.getRegistrationState() != 2) {
            NetworkRegistrationInfo csRegState = getNetworkRegistrationInfo(1, 1);
            return csRegState != null && csRegState.getRegistrationState() == 2;
        }
        return true;
    }

    public static boolean isFrequencyRangeValid(int frequencyRange) {
        if (frequencyRange == 1 || frequencyRange == 2 || frequencyRange == 3 || frequencyRange == 4) {
            return true;
        }
        return false;
    }
}
