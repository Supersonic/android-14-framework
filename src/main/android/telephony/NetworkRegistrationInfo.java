package android.telephony;

import android.annotation.SystemApi;
import android.app.compat.CompatChanges;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.keystore.KeyProperties;
import android.telephony.DataSpecificRegistrationInfo;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public final class NetworkRegistrationInfo implements Parcelable {
    public static final Parcelable.Creator<NetworkRegistrationInfo> CREATOR = new Parcelable.Creator<NetworkRegistrationInfo>() { // from class: android.telephony.NetworkRegistrationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkRegistrationInfo createFromParcel(Parcel source) {
            return new NetworkRegistrationInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkRegistrationInfo[] newArray(int size) {
            return new NetworkRegistrationInfo[size];
        }
    };
    public static final int DOMAIN_CS = 1;
    public static final int DOMAIN_CS_PS = 3;
    public static final int DOMAIN_PS = 2;
    public static final int DOMAIN_UNKNOWN = 0;
    public static final int NR_STATE_CONNECTED = 3;
    public static final int NR_STATE_NONE = 0;
    public static final int NR_STATE_NOT_RESTRICTED = 2;
    public static final int NR_STATE_RESTRICTED = 1;
    @SystemApi
    public static final int REGISTRATION_STATE_DENIED = 3;
    @SystemApi
    public static final int REGISTRATION_STATE_EMERGENCY = 6;
    @SystemApi
    public static final int REGISTRATION_STATE_HOME = 1;
    @SystemApi
    public static final int REGISTRATION_STATE_NOT_REGISTERED_OR_SEARCHING = 0;
    @SystemApi
    public static final int REGISTRATION_STATE_NOT_REGISTERED_SEARCHING = 2;
    @SystemApi
    public static final int REGISTRATION_STATE_ROAMING = 5;
    @SystemApi
    public static final int REGISTRATION_STATE_UNKNOWN = 4;
    public static final long RETURN_REGISTRATION_STATE_EMERGENCY = 255938466;
    public static final int SERVICE_TYPE_DATA = 2;
    public static final int SERVICE_TYPE_EMERGENCY = 5;
    public static final int SERVICE_TYPE_SMS = 3;
    public static final int SERVICE_TYPE_UNKNOWN = 0;
    public static final int SERVICE_TYPE_VIDEO = 4;
    public static final int SERVICE_TYPE_VOICE = 1;
    private int mAccessNetworkTechnology;
    private final ArrayList<Integer> mAvailableServices;
    private CellIdentity mCellIdentity;
    private DataSpecificRegistrationInfo mDataSpecificInfo;
    private final int mDomain;
    private final boolean mEmergencyOnly;
    private boolean mIsUsingCarrierAggregation;
    private final int mNetworkRegistrationState;
    private int mNrState;
    private int mRegistrationState;
    private final int mRejectCause;
    private int mRoamingType;
    private String mRplmn;
    private final int mTransportType;
    private VoiceSpecificRegistrationInfo mVoiceSpecificInfo;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Domain {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface NRState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RegistrationState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceType {
    }

    private NetworkRegistrationInfo(int domain, int transportType, int registrationState, int accessNetworkTechnology, int rejectCause, boolean emergencyOnly, List<Integer> availableServices, CellIdentity cellIdentity, String rplmn, VoiceSpecificRegistrationInfo voiceSpecificInfo, DataSpecificRegistrationInfo dataSpecificInfo) {
        this.mDomain = domain;
        this.mTransportType = transportType;
        this.mRegistrationState = registrationState;
        this.mNetworkRegistrationState = registrationState;
        this.mRoamingType = registrationState == 5 ? 1 : 0;
        setAccessNetworkTechnology(accessNetworkTechnology);
        this.mRejectCause = rejectCause;
        this.mAvailableServices = availableServices != null ? new ArrayList<>(availableServices) : new ArrayList<>();
        this.mCellIdentity = cellIdentity;
        this.mEmergencyOnly = emergencyOnly;
        this.mNrState = 0;
        this.mRplmn = rplmn;
        this.mVoiceSpecificInfo = voiceSpecificInfo;
        this.mDataSpecificInfo = dataSpecificInfo;
        updateNrState();
    }

    public NetworkRegistrationInfo(int domain, int transportType, int registrationState, int accessNetworkTechnology, int rejectCause, boolean emergencyOnly, List<Integer> availableServices, CellIdentity cellIdentity, String rplmn, boolean cssSupported, int roamingIndicator, int systemIsInPrl, int defaultRoamingIndicator) {
        this(domain, transportType, registrationState, accessNetworkTechnology, rejectCause, emergencyOnly, availableServices, cellIdentity, rplmn, new VoiceSpecificRegistrationInfo(cssSupported, roamingIndicator, systemIsInPrl, defaultRoamingIndicator), null);
    }

    public NetworkRegistrationInfo(int domain, int transportType, int registrationState, int accessNetworkTechnology, int rejectCause, boolean emergencyOnly, List<Integer> availableServices, CellIdentity cellIdentity, String rplmn, int maxDataCalls, boolean isDcNrRestricted, boolean isNrAvailable, boolean isEndcAvailable, VopsSupportInfo vopsSupportInfo) {
        this(domain, transportType, registrationState, accessNetworkTechnology, rejectCause, emergencyOnly, availableServices, cellIdentity, rplmn, null, new DataSpecificRegistrationInfo.Builder(maxDataCalls).setDcNrRestricted(isDcNrRestricted).setNrAvailable(isNrAvailable).setEnDcAvailable(isEndcAvailable).setVopsSupportInfo(vopsSupportInfo).build());
    }

    private NetworkRegistrationInfo(Parcel source) {
        this.mDomain = source.readInt();
        this.mTransportType = source.readInt();
        this.mRegistrationState = source.readInt();
        this.mNetworkRegistrationState = source.readInt();
        this.mRoamingType = source.readInt();
        this.mAccessNetworkTechnology = source.readInt();
        this.mRejectCause = source.readInt();
        this.mEmergencyOnly = source.readBoolean();
        ArrayList<Integer> arrayList = new ArrayList<>();
        this.mAvailableServices = arrayList;
        source.readList(arrayList, Integer.class.getClassLoader(), Integer.class);
        this.mCellIdentity = (CellIdentity) source.readParcelable(CellIdentity.class.getClassLoader(), CellIdentity.class);
        this.mVoiceSpecificInfo = (VoiceSpecificRegistrationInfo) source.readParcelable(VoiceSpecificRegistrationInfo.class.getClassLoader(), VoiceSpecificRegistrationInfo.class);
        this.mDataSpecificInfo = (DataSpecificRegistrationInfo) source.readParcelable(DataSpecificRegistrationInfo.class.getClassLoader(), DataSpecificRegistrationInfo.class);
        this.mNrState = source.readInt();
        this.mRplmn = source.readString();
        this.mIsUsingCarrierAggregation = source.readBoolean();
    }

    public NetworkRegistrationInfo(NetworkRegistrationInfo nri) {
        this.mDomain = nri.mDomain;
        this.mTransportType = nri.mTransportType;
        this.mRegistrationState = nri.mRegistrationState;
        this.mNetworkRegistrationState = nri.mNetworkRegistrationState;
        this.mRoamingType = nri.mRoamingType;
        this.mAccessNetworkTechnology = nri.mAccessNetworkTechnology;
        this.mIsUsingCarrierAggregation = nri.mIsUsingCarrierAggregation;
        this.mRejectCause = nri.mRejectCause;
        this.mEmergencyOnly = nri.mEmergencyOnly;
        this.mAvailableServices = new ArrayList<>(nri.mAvailableServices);
        if (nri.mCellIdentity != null) {
            Parcel p = Parcel.obtain();
            nri.mCellIdentity.writeToParcel(p, 0);
            p.setDataPosition(0);
            this.mCellIdentity = CellIdentity.CREATOR.createFromParcel(p);
            p.recycle();
        }
        if (nri.mVoiceSpecificInfo != null) {
            this.mVoiceSpecificInfo = new VoiceSpecificRegistrationInfo(nri.mVoiceSpecificInfo);
        }
        if (nri.mDataSpecificInfo != null) {
            this.mDataSpecificInfo = new DataSpecificRegistrationInfo(nri.mDataSpecificInfo);
        }
        this.mNrState = nri.mNrState;
        this.mRplmn = nri.mRplmn;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public int getDomain() {
        return this.mDomain;
    }

    public int getNrState() {
        return this.mNrState;
    }

    public void setNrState(int nrState) {
        this.mNrState = nrState;
    }

    @SystemApi
    @Deprecated
    public int getRegistrationState() {
        if (this.mRegistrationState == 6 && !CompatChanges.isChangeEnabled(RETURN_REGISTRATION_STATE_EMERGENCY)) {
            int i = this.mAccessNetworkTechnology;
            if (i == 13) {
                return 3;
            }
            if (i == 20) {
                return 0;
            }
        }
        return this.mRegistrationState;
    }

    @SystemApi
    public int getNetworkRegistrationState() {
        return this.mNetworkRegistrationState;
    }

    @Deprecated
    public boolean isRegistered() {
        int i = this.mRegistrationState;
        return i == 1 || i == 5;
    }

    public boolean isNetworkRegistered() {
        int i = this.mNetworkRegistrationState;
        return i == 1 || i == 5;
    }

    @Deprecated
    public boolean isSearching() {
        return this.mRegistrationState == 2;
    }

    public boolean isNetworkSearching() {
        return this.mNetworkRegistrationState == 2;
    }

    public String getRegisteredPlmn() {
        return this.mRplmn;
    }

    @Deprecated
    public boolean isRoaming() {
        return this.mRoamingType != 0;
    }

    public boolean isNetworkRoaming() {
        return this.mNetworkRegistrationState == 5;
    }

    public boolean isInService() {
        int i = this.mRegistrationState;
        return i == 1 || i == 5;
    }

    public void setRoamingType(int roamingType) {
        this.mRoamingType = roamingType;
        if (isRoaming()) {
            if (this.mRegistrationState == 1) {
                this.mRegistrationState = 5;
            }
        } else if (this.mRegistrationState == 5) {
            this.mRegistrationState = 1;
        }
    }

    @SystemApi
    public int getRoamingType() {
        return this.mRoamingType;
    }

    @SystemApi
    public boolean isEmergencyEnabled() {
        return this.mEmergencyOnly;
    }

    public List<Integer> getAvailableServices() {
        return Collections.unmodifiableList(this.mAvailableServices);
    }

    public int getAccessNetworkTechnology() {
        return this.mAccessNetworkTechnology;
    }

    public void setAccessNetworkTechnology(int tech) {
        if (tech == 19) {
            tech = 13;
            this.mIsUsingCarrierAggregation = true;
        }
        this.mAccessNetworkTechnology = tech;
    }

    @SystemApi
    public int getRejectCause() {
        return this.mRejectCause;
    }

    public CellIdentity getCellIdentity() {
        return this.mCellIdentity;
    }

    public void setIsUsingCarrierAggregation(boolean isUsingCarrierAggregation) {
        this.mIsUsingCarrierAggregation = isUsingCarrierAggregation;
    }

    public boolean isUsingCarrierAggregation() {
        return this.mIsUsingCarrierAggregation;
    }

    public VoiceSpecificRegistrationInfo getVoiceSpecificInfo() {
        return this.mVoiceSpecificInfo;
    }

    @SystemApi
    public DataSpecificRegistrationInfo getDataSpecificInfo() {
        return this.mDataSpecificInfo;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static String serviceTypeToString(int serviceType) {
        switch (serviceType) {
            case 1:
                return "VOICE";
            case 2:
                return "DATA";
            case 3:
                return "SMS";
            case 4:
                return "VIDEO";
            case 5:
                return "EMERGENCY";
            default:
                return "Unknown service type " + serviceType;
        }
    }

    public static String registrationStateToString(int registrationState) {
        switch (registrationState) {
            case 0:
                return "NOT_REG_OR_SEARCHING";
            case 1:
                return "HOME";
            case 2:
                return "NOT_REG_SEARCHING";
            case 3:
                return "DENIED";
            case 4:
                return "UNKNOWN";
            case 5:
                return "ROAMING";
            case 6:
                return "EMERGENCY";
            default:
                return "Unknown reg state " + registrationState;
        }
    }

    public static String nrStateToString(int nrState) {
        switch (nrState) {
            case 1:
                return "RESTRICTED";
            case 2:
                return "NOT_RESTRICTED";
            case 3:
                return "CONNECTED";
            default:
                return KeyProperties.DIGEST_NONE;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String domainToString(int domain) {
        switch (domain) {
            case 1:
                return "CS";
            case 2:
                return "PS";
            case 3:
                return "CS_PS";
            default:
                return "UNKNOWN";
        }
    }

    public String toString() {
        StringBuilder append = new StringBuilder("NetworkRegistrationInfo{").append(" domain=").append(domainToString(this.mDomain)).append(" transportType=").append(AccessNetworkConstants.transportTypeToString(this.mTransportType)).append(" registrationState=").append(registrationStateToString(this.mRegistrationState)).append(" networkRegistrationState=").append(registrationStateToString(this.mNetworkRegistrationState)).append(" roamingType=").append(ServiceState.roamingTypeToString(this.mRoamingType)).append(" accessNetworkTechnology=").append(TelephonyManager.getNetworkTypeName(this.mAccessNetworkTechnology)).append(" rejectCause=").append(this.mRejectCause).append(" emergencyEnabled=").append(this.mEmergencyOnly).append(" availableServices=");
        StringBuilder append2 = new StringBuilder().append(NavigationBarInflaterView.SIZE_MOD_START);
        ArrayList<Integer> arrayList = this.mAvailableServices;
        return append.append(append2.append(arrayList != null ? (String) arrayList.stream().map(new Function() { // from class: android.telephony.NetworkRegistrationInfo$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String serviceTypeToString;
                serviceTypeToString = NetworkRegistrationInfo.serviceTypeToString(((Integer) obj).intValue());
                return serviceTypeToString;
            }
        }).collect(Collectors.joining(",")) : null).append(NavigationBarInflaterView.SIZE_MOD_END).toString()).append(" cellIdentity=").append(this.mCellIdentity).append(" voiceSpecificInfo=").append(this.mVoiceSpecificInfo).append(" dataSpecificInfo=").append(this.mDataSpecificInfo).append(" nrState=").append(Build.IS_DEBUGGABLE ? nrStateToString(this.mNrState) : "****").append(" rRplmn=").append(this.mRplmn).append(" isUsingCarrierAggregation=").append(this.mIsUsingCarrierAggregation).append("}").toString();
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDomain), Integer.valueOf(this.mTransportType), Integer.valueOf(this.mRegistrationState), Integer.valueOf(this.mNetworkRegistrationState), Integer.valueOf(this.mRoamingType), Integer.valueOf(this.mAccessNetworkTechnology), Integer.valueOf(this.mRejectCause), Boolean.valueOf(this.mEmergencyOnly), this.mAvailableServices, this.mCellIdentity, this.mVoiceSpecificInfo, this.mDataSpecificInfo, Integer.valueOf(this.mNrState), this.mRplmn, Boolean.valueOf(this.mIsUsingCarrierAggregation));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NetworkRegistrationInfo) {
            NetworkRegistrationInfo other = (NetworkRegistrationInfo) o;
            return this.mDomain == other.mDomain && this.mTransportType == other.mTransportType && this.mRegistrationState == other.mRegistrationState && this.mNetworkRegistrationState == other.mNetworkRegistrationState && this.mRoamingType == other.mRoamingType && this.mAccessNetworkTechnology == other.mAccessNetworkTechnology && this.mRejectCause == other.mRejectCause && this.mEmergencyOnly == other.mEmergencyOnly && this.mAvailableServices.equals(other.mAvailableServices) && this.mIsUsingCarrierAggregation == other.mIsUsingCarrierAggregation && Objects.equals(this.mCellIdentity, other.mCellIdentity) && Objects.equals(this.mVoiceSpecificInfo, other.mVoiceSpecificInfo) && Objects.equals(this.mDataSpecificInfo, other.mDataSpecificInfo) && TextUtils.equals(this.mRplmn, other.mRplmn) && this.mNrState == other.mNrState;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    @SystemApi
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDomain);
        dest.writeInt(this.mTransportType);
        dest.writeInt(this.mRegistrationState);
        dest.writeInt(this.mNetworkRegistrationState);
        dest.writeInt(this.mRoamingType);
        dest.writeInt(this.mAccessNetworkTechnology);
        dest.writeInt(this.mRejectCause);
        dest.writeBoolean(this.mEmergencyOnly);
        dest.writeList(this.mAvailableServices);
        dest.writeParcelable(this.mCellIdentity, 0);
        dest.writeParcelable(this.mVoiceSpecificInfo, 0);
        dest.writeParcelable(this.mDataSpecificInfo, 0);
        dest.writeInt(this.mNrState);
        dest.writeString(this.mRplmn);
        dest.writeBoolean(this.mIsUsingCarrierAggregation);
    }

    public void updateNrState() {
        this.mNrState = 0;
        DataSpecificRegistrationInfo dataSpecificRegistrationInfo = this.mDataSpecificInfo;
        if (dataSpecificRegistrationInfo != null && dataSpecificRegistrationInfo.isEnDcAvailable) {
            if (!this.mDataSpecificInfo.isDcNrRestricted && this.mDataSpecificInfo.isNrAvailable) {
                this.mNrState = 2;
            } else {
                this.mNrState = 1;
            }
        }
    }

    public NetworkRegistrationInfo sanitizeLocationInfo() {
        NetworkRegistrationInfo result = copy();
        result.mCellIdentity = null;
        return result;
    }

    private NetworkRegistrationInfo copy() {
        Parcel p = Parcel.obtain();
        writeToParcel(p, 0);
        p.setDataPosition(0);
        NetworkRegistrationInfo result = new NetworkRegistrationInfo(p);
        p.recycle();
        return result;
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mAccessNetworkTechnology;
        private List<Integer> mAvailableServices;
        private CellIdentity mCellIdentity;
        private DataSpecificRegistrationInfo mDataSpecificRegistrationInfo;
        private int mDomain;
        private boolean mEmergencyOnly;
        private int mNetworkRegistrationState;
        private int mRejectCause;
        private String mRplmn = "";
        private int mTransportType;
        private VoiceSpecificRegistrationInfo mVoiceSpecificRegistrationInfo;

        public Builder() {
        }

        public Builder(NetworkRegistrationInfo nri) {
            this.mDomain = nri.mDomain;
            this.mTransportType = nri.mTransportType;
            this.mNetworkRegistrationState = nri.mNetworkRegistrationState;
            this.mAccessNetworkTechnology = nri.mAccessNetworkTechnology;
            this.mRejectCause = nri.mRejectCause;
            this.mEmergencyOnly = nri.mEmergencyOnly;
            this.mAvailableServices = new ArrayList(nri.mAvailableServices);
            this.mCellIdentity = nri.mCellIdentity;
            if (nri.mDataSpecificInfo != null) {
                this.mDataSpecificRegistrationInfo = new DataSpecificRegistrationInfo(nri.mDataSpecificInfo);
            }
            if (nri.mVoiceSpecificInfo != null) {
                this.mVoiceSpecificRegistrationInfo = new VoiceSpecificRegistrationInfo(nri.mVoiceSpecificInfo);
            }
        }

        public Builder setDomain(int domain) {
            this.mDomain = domain;
            return this;
        }

        public Builder setTransportType(int transportType) {
            this.mTransportType = transportType;
            return this;
        }

        public Builder setRegistrationState(int registrationState) {
            this.mNetworkRegistrationState = registrationState;
            return this;
        }

        public Builder setAccessNetworkTechnology(int accessNetworkTechnology) {
            this.mAccessNetworkTechnology = accessNetworkTechnology;
            return this;
        }

        public Builder setRejectCause(int rejectCause) {
            this.mRejectCause = rejectCause;
            return this;
        }

        @SystemApi
        public Builder setEmergencyOnly(boolean emergencyOnly) {
            this.mEmergencyOnly = emergencyOnly;
            return this;
        }

        @SystemApi
        public Builder setAvailableServices(List<Integer> availableServices) {
            this.mAvailableServices = availableServices;
            return this;
        }

        @SystemApi
        public Builder setCellIdentity(CellIdentity cellIdentity) {
            this.mCellIdentity = cellIdentity;
            return this;
        }

        public Builder setRegisteredPlmn(String rplmn) {
            this.mRplmn = rplmn;
            return this;
        }

        public Builder setVoiceSpecificInfo(VoiceSpecificRegistrationInfo info) {
            this.mVoiceSpecificRegistrationInfo = info;
            return this;
        }

        public Builder setDataSpecificInfo(DataSpecificRegistrationInfo info) {
            this.mDataSpecificRegistrationInfo = info;
            return this;
        }

        @SystemApi
        public NetworkRegistrationInfo build() {
            return new NetworkRegistrationInfo(this.mDomain, this.mTransportType, this.mNetworkRegistrationState, this.mAccessNetworkTechnology, this.mRejectCause, this.mEmergencyOnly, this.mAvailableServices, this.mCellIdentity, this.mRplmn, this.mVoiceSpecificRegistrationInfo, this.mDataSpecificRegistrationInfo);
        }
    }
}
