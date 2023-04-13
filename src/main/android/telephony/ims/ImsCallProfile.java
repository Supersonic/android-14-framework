package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telecom.VideoProfile;
import android.telephony.emergency.EmergencyNumber;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.telephony.util.TelephonyUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsCallProfile implements Parcelable {
    public static final int CALL_RESTRICT_CAUSE_DISABLED = 2;
    public static final int CALL_RESTRICT_CAUSE_HD = 3;
    public static final int CALL_RESTRICT_CAUSE_NONE = 0;
    public static final int CALL_RESTRICT_CAUSE_RAT = 1;
    public static final int CALL_TYPE_NONE = 0;
    public static final int CALL_TYPE_VIDEO_N_VOICE = 3;
    public static final int CALL_TYPE_VOICE = 2;
    public static final int CALL_TYPE_VOICE_N_VIDEO = 1;
    public static final int CALL_TYPE_VS = 8;
    public static final int CALL_TYPE_VS_RX = 10;
    public static final int CALL_TYPE_VS_TX = 9;
    public static final int CALL_TYPE_VT = 4;
    public static final int CALL_TYPE_VT_NODIR = 7;
    public static final int CALL_TYPE_VT_RX = 6;
    public static final int CALL_TYPE_VT_TX = 5;
    public static final Parcelable.Creator<ImsCallProfile> CREATOR = new Parcelable.Creator<ImsCallProfile>() { // from class: android.telephony.ims.ImsCallProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsCallProfile createFromParcel(Parcel in) {
            return new ImsCallProfile(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsCallProfile[] newArray(int size) {
            return new ImsCallProfile[size];
        }
    };
    public static final int DIALSTRING_NORMAL = 0;
    public static final int DIALSTRING_SS_CONF = 1;
    public static final int DIALSTRING_USSD = 2;
    public static final String EXTRA_ADDITIONAL_CALL_INFO = "AdditionalCallInfo";
    public static final String EXTRA_ADDITIONAL_SIP_INVITE_FIELDS = "android.telephony.ims.extra.ADDITIONAL_SIP_INVITE_FIELDS";
    public static final String EXTRA_CALL_DISCONNECT_CAUSE = "android.telephony.ims.extra.CALL_DISCONNECT_CAUSE";
    public static final String EXTRA_CALL_MODE_CHANGEABLE = "call_mode_changeable";
    public static final String EXTRA_CALL_NETWORK_TYPE = "android.telephony.ims.extra.CALL_NETWORK_TYPE";
    @Deprecated
    public static final String EXTRA_CALL_RAT_TYPE = "CallRadioTech";
    @Deprecated
    public static final String EXTRA_CALL_RAT_TYPE_ALT = "callRadioTech";
    public static final String EXTRA_CALL_SUBJECT = "android.telephony.ims.extra.CALL_SUBJECT";
    public static final String EXTRA_CHILD_NUMBER = "ChildNum";
    public static final String EXTRA_CNA = "cna";
    public static final String EXTRA_CNAP = "cnap";
    public static final String EXTRA_CODEC = "Codec";
    @SystemApi
    public static final String EXTRA_CONFERENCE = "android.telephony.ims.extra.CONFERENCE";
    public static final String EXTRA_CONFERENCE_AVAIL = "conference_avail";
    public static final String EXTRA_CONFERENCE_DEPRECATED = "conference";
    public static final String EXTRA_DIALSTRING = "dialstring";
    public static final String EXTRA_DISPLAY_TEXT = "DisplayText";
    public static final String EXTRA_EMERGENCY_CALL = "e_call";
    @SystemApi
    public static final String EXTRA_EXTENDING_TO_CONFERENCE_SUPPORTED = "android.telephony.ims.extra.EXTENDING_TO_CONFERENCE_SUPPORTED";
    public static final String EXTRA_FORWARDED_NUMBER = "android.telephony.ims.extra.FORWARDED_NUMBER";
    public static final String EXTRA_IS_BUSINESS_CALL = "android.telephony.ims.extra.IS_BUSINESS_CALL";
    public static final String EXTRA_IS_CALL_PULL = "CallPull";
    public static final String EXTRA_IS_CROSS_SIM_CALL = "android.telephony.ims.extra.IS_CROSS_SIM_CALL";
    public static final String EXTRA_LOCATION = "android.telephony.ims.extra.LOCATION";
    public static final String EXTRA_OEM_EXTRAS = "android.telephony.ims.extra.OEM_EXTRAS";
    public static final String EXTRA_OI = "oi";
    public static final String EXTRA_OIR = "oir";
    public static final String EXTRA_PICTURE_URL = "android.telephony.ims.extra.PICTURE_URL";
    public static final String EXTRA_PRIORITY = "android.telephony.ims.extra.PRIORITY";
    public static final String EXTRA_REMOTE_URI = "remote_uri";
    public static final String EXTRA_RETRY_CALL_FAIL_NETWORKTYPE = "android.telephony.ims.extra.RETRY_CALL_FAIL_NETWORKTYPE";
    public static final String EXTRA_RETRY_CALL_FAIL_REASON = "android.telephony.ims.extra.RETRY_CALL_FAIL_REASON";
    public static final String EXTRA_USSD = "ussd";
    public static final String EXTRA_VMS = "vms";
    public static final int OIR_DEFAULT = 0;
    public static final int OIR_PRESENTATION_NOT_RESTRICTED = 2;
    public static final int OIR_PRESENTATION_PAYPHONE = 4;
    public static final int OIR_PRESENTATION_RESTRICTED = 1;
    public static final int OIR_PRESENTATION_UNAVAILABLE = 5;
    public static final int OIR_PRESENTATION_UNKNOWN = 3;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_URGENT = 1;
    public static final int SERVICE_TYPE_EMERGENCY = 2;
    public static final int SERVICE_TYPE_NONE = 0;
    public static final int SERVICE_TYPE_NORMAL = 1;
    private static final String TAG = "ImsCallProfile";
    public static final int VERIFICATION_STATUS_FAILED = 2;
    public static final int VERIFICATION_STATUS_NOT_VERIFIED = 0;
    public static final int VERIFICATION_STATUS_PASSED = 1;
    private Set<RtpHeaderExtensionType> mAcceptedRtpHeaderExtensionTypes;
    public Bundle mCallExtras;
    public int mCallType;
    private int mCallerNumberVerificationStatus;
    private int mEmergencyCallRouting;
    private boolean mEmergencyCallTesting;
    private int mEmergencyServiceCategories;
    private List<String> mEmergencyUrns;
    private boolean mHasKnownUserIntentEmergency;
    public ImsStreamMediaProfile mMediaProfile;
    public int mRestrictCause;
    public int mServiceType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallRestrictCause {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VerificationStatus {
    }

    public ImsCallProfile(Parcel in) {
        this.mRestrictCause = 0;
        this.mEmergencyServiceCategories = 0;
        this.mEmergencyUrns = new ArrayList();
        this.mEmergencyCallRouting = 0;
        this.mEmergencyCallTesting = false;
        this.mHasKnownUserIntentEmergency = false;
        this.mAcceptedRtpHeaderExtensionTypes = new ArraySet();
        readFromParcel(in);
    }

    public ImsCallProfile() {
        this.mRestrictCause = 0;
        this.mEmergencyServiceCategories = 0;
        this.mEmergencyUrns = new ArrayList();
        this.mEmergencyCallRouting = 0;
        this.mEmergencyCallTesting = false;
        this.mHasKnownUserIntentEmergency = false;
        this.mAcceptedRtpHeaderExtensionTypes = new ArraySet();
        this.mServiceType = 1;
        this.mCallType = 1;
        this.mCallExtras = new Bundle();
        this.mMediaProfile = new ImsStreamMediaProfile();
    }

    public ImsCallProfile(int serviceType, int callType) {
        this.mRestrictCause = 0;
        this.mEmergencyServiceCategories = 0;
        this.mEmergencyUrns = new ArrayList();
        this.mEmergencyCallRouting = 0;
        this.mEmergencyCallTesting = false;
        this.mHasKnownUserIntentEmergency = false;
        this.mAcceptedRtpHeaderExtensionTypes = new ArraySet();
        this.mServiceType = serviceType;
        this.mCallType = callType;
        this.mCallExtras = new Bundle();
        this.mMediaProfile = new ImsStreamMediaProfile();
    }

    public ImsCallProfile(int serviceType, int callType, Bundle callExtras, ImsStreamMediaProfile mediaProfile) {
        this.mRestrictCause = 0;
        this.mEmergencyServiceCategories = 0;
        this.mEmergencyUrns = new ArrayList();
        this.mEmergencyCallRouting = 0;
        this.mEmergencyCallTesting = false;
        this.mHasKnownUserIntentEmergency = false;
        this.mAcceptedRtpHeaderExtensionTypes = new ArraySet();
        this.mServiceType = serviceType;
        this.mCallType = callType;
        this.mCallExtras = callExtras;
        this.mMediaProfile = mediaProfile;
    }

    public String getCallExtra(String name) {
        return getCallExtra(name, "");
    }

    public String getCallExtra(String name, String defaultValue) {
        Bundle bundle = this.mCallExtras;
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getString(name, defaultValue);
    }

    public boolean getCallExtraBoolean(String name) {
        return getCallExtraBoolean(name, false);
    }

    public boolean getCallExtraBoolean(String name, boolean defaultValue) {
        Bundle bundle = this.mCallExtras;
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getBoolean(name, defaultValue);
    }

    public int getCallExtraInt(String name) {
        return getCallExtraInt(name, -1);
    }

    public int getCallExtraInt(String name, int defaultValue) {
        Bundle bundle = this.mCallExtras;
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getInt(name, defaultValue);
    }

    public <T extends Parcelable> T getCallExtraParcelable(String name) {
        Bundle bundle = this.mCallExtras;
        if (bundle != null) {
            return (T) bundle.getParcelable(name);
        }
        return null;
    }

    public void setCallExtra(String name, String value) {
        Bundle bundle = this.mCallExtras;
        if (bundle != null) {
            bundle.putString(name, value);
        }
    }

    public void setCallExtraBoolean(String name, boolean value) {
        Bundle bundle = this.mCallExtras;
        if (bundle != null) {
            bundle.putBoolean(name, value);
        }
    }

    public void setCallExtraInt(String name, int value) {
        Bundle bundle = this.mCallExtras;
        if (bundle != null) {
            bundle.putInt(name, value);
        }
    }

    public void setCallExtraParcelable(String name, Parcelable parcelable) {
        Bundle bundle = this.mCallExtras;
        if (bundle != null) {
            bundle.putParcelable(name, parcelable);
        }
    }

    public void setCallRestrictCause(int cause) {
        this.mRestrictCause = cause;
    }

    public void updateCallType(ImsCallProfile profile) {
        this.mCallType = profile.mCallType;
    }

    public void updateCallExtras(ImsCallProfile profile) {
        this.mCallExtras.clear();
        this.mCallExtras = (Bundle) profile.mCallExtras.clone();
    }

    public void updateMediaProfile(ImsCallProfile profile) {
        this.mMediaProfile = profile.mMediaProfile;
    }

    public void setCallerNumberVerificationStatus(int callerNumberVerificationStatus) {
        this.mCallerNumberVerificationStatus = callerNumberVerificationStatus;
    }

    public int getCallerNumberVerificationStatus() {
        return this.mCallerNumberVerificationStatus;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("{ serviceType=").append(this.mServiceType).append(", callType=").append(this.mCallType).append(", restrictCause=").append(this.mRestrictCause).append(", mediaProfile=");
        ImsStreamMediaProfile imsStreamMediaProfile = this.mMediaProfile;
        return append.append(imsStreamMediaProfile != null ? imsStreamMediaProfile.toString() : "null").append(", emergencyServiceCategories=").append(this.mEmergencyServiceCategories).append(", emergencyUrns=").append(this.mEmergencyUrns).append(", emergencyCallRouting=").append(this.mEmergencyCallRouting).append(", emergencyCallTesting=").append(this.mEmergencyCallTesting).append(", hasKnownUserIntentEmergency=").append(this.mHasKnownUserIntentEmergency).append(", mRestrictCause=").append(this.mRestrictCause).append(", mCallerNumberVerstat= ").append(this.mCallerNumberVerificationStatus).append(", mAcceptedRtpHeaderExtensions= ").append(this.mAcceptedRtpHeaderExtensionTypes).append(" }").toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        Bundle filteredExtras = maybeCleanseExtras(this.mCallExtras);
        out.writeInt(this.mServiceType);
        out.writeInt(this.mCallType);
        out.writeBundle(filteredExtras);
        out.writeParcelable(this.mMediaProfile, 0);
        out.writeInt(this.mEmergencyServiceCategories);
        out.writeStringList(this.mEmergencyUrns);
        out.writeInt(this.mEmergencyCallRouting);
        out.writeBoolean(this.mEmergencyCallTesting);
        out.writeBoolean(this.mHasKnownUserIntentEmergency);
        out.writeInt(this.mRestrictCause);
        out.writeInt(this.mCallerNumberVerificationStatus);
        out.writeArray(this.mAcceptedRtpHeaderExtensionTypes.toArray());
    }

    private void readFromParcel(Parcel in) {
        this.mServiceType = in.readInt();
        this.mCallType = in.readInt();
        this.mCallExtras = in.readBundle();
        this.mMediaProfile = (ImsStreamMediaProfile) in.readParcelable(ImsStreamMediaProfile.class.getClassLoader(), ImsStreamMediaProfile.class);
        this.mEmergencyServiceCategories = in.readInt();
        this.mEmergencyUrns = in.createStringArrayList();
        this.mEmergencyCallRouting = in.readInt();
        this.mEmergencyCallTesting = in.readBoolean();
        this.mHasKnownUserIntentEmergency = in.readBoolean();
        this.mRestrictCause = in.readInt();
        this.mCallerNumberVerificationStatus = in.readInt();
        Object[] accepted = in.readArray(RtpHeaderExtensionType.class.getClassLoader(), RtpHeaderExtensionType.class);
        this.mAcceptedRtpHeaderExtensionTypes = (Set) Arrays.stream(accepted).map(new Function() { // from class: android.telephony.ims.ImsCallProfile$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ImsCallProfile.lambda$readFromParcel$0(obj);
            }
        }).collect(Collectors.toSet());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ RtpHeaderExtensionType lambda$readFromParcel$0(Object o) {
        return (RtpHeaderExtensionType) o;
    }

    public int getServiceType() {
        return this.mServiceType;
    }

    public int getCallType() {
        return this.mCallType;
    }

    public int getRestrictCause() {
        return this.mRestrictCause;
    }

    public Bundle getCallExtras() {
        return this.mCallExtras;
    }

    public Bundle getProprietaryCallExtras() {
        Bundle bundle = this.mCallExtras;
        if (bundle == null) {
            return new Bundle();
        }
        Bundle proprietaryExtras = bundle.getBundle(EXTRA_OEM_EXTRAS);
        if (proprietaryExtras == null) {
            return new Bundle();
        }
        return new Bundle(proprietaryExtras);
    }

    public ImsStreamMediaProfile getMediaProfile() {
        return this.mMediaProfile;
    }

    public static int getVideoStateFromImsCallProfile(ImsCallProfile callProfile) {
        int videostate = getVideoStateFromCallType(callProfile.mCallType);
        if (callProfile.isVideoPaused() && !VideoProfile.isAudioOnly(videostate)) {
            return videostate | 4;
        }
        return videostate & (-5);
    }

    public static int getVideoStateFromCallType(int callType) {
        switch (callType) {
            case 2:
                return 0;
            case 3:
            default:
                return 0;
            case 4:
                return 3;
            case 5:
                return 1;
            case 6:
                return 2;
        }
    }

    public static int getCallTypeFromVideoState(int videoState) {
        boolean videoTx = isVideoStateSet(videoState, 1);
        boolean videoRx = isVideoStateSet(videoState, 2);
        boolean isPaused = isVideoStateSet(videoState, 4);
        if (isPaused) {
            return 7;
        }
        if (videoTx && !videoRx) {
            return 5;
        }
        if (!videoTx && videoRx) {
            return 6;
        }
        if (!videoTx || !videoRx) {
            return 2;
        }
        return 4;
    }

    public static int presentationToOIR(int presentation) {
        switch (presentation) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            default:
                return 0;
        }
    }

    public static int presentationToOir(int presentation) {
        return presentationToOIR(presentation);
    }

    public static int OIRToPresentation(int oir) {
        switch (oir) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            default:
                return 3;
        }
    }

    public boolean isVideoPaused() {
        return this.mMediaProfile.mVideoDirection == 0;
    }

    public boolean isVideoCall() {
        return VideoProfile.isVideo(getVideoStateFromCallType(this.mCallType));
    }

    private Bundle maybeCleanseExtras(Bundle extras) {
        if (extras == null) {
            return null;
        }
        int startSize = extras.size();
        Bundle filtered = TelephonyUtils.filterValues(extras);
        int endSize = filtered.size();
        if (startSize != endSize) {
            Log.m108i(TAG, "maybeCleanseExtras: " + (startSize - endSize) + " extra values were removed - only primitive types and system parcelables are permitted.");
        }
        return filtered;
    }

    private static boolean isVideoStateSet(int videoState, int videoStateToCheck) {
        return (videoState & videoStateToCheck) == videoStateToCheck;
    }

    public void setEmergencyCallInfo(EmergencyNumber num, boolean hasKnownUserIntentEmergency) {
        setEmergencyServiceCategories(num.getEmergencyServiceCategoryBitmaskInternalDial());
        setEmergencyUrns(num.getEmergencyUrns());
        setEmergencyCallRouting(num.getEmergencyCallRouting());
        setEmergencyCallTesting(num.getEmergencyNumberSourceBitmask() == 32);
        setHasKnownUserIntentEmergency(hasKnownUserIntentEmergency);
    }

    public void setEmergencyServiceCategories(int emergencyServiceCategories) {
        this.mEmergencyServiceCategories = emergencyServiceCategories;
    }

    public void setEmergencyUrns(List<String> emergencyUrns) {
        this.mEmergencyUrns = emergencyUrns;
    }

    public void setEmergencyCallRouting(int emergencyCallRouting) {
        this.mEmergencyCallRouting = emergencyCallRouting;
    }

    public void setEmergencyCallTesting(boolean isTesting) {
        this.mEmergencyCallTesting = isTesting;
    }

    public void setHasKnownUserIntentEmergency(boolean hasKnownUserIntentEmergency) {
        this.mHasKnownUserIntentEmergency = hasKnownUserIntentEmergency;
    }

    public int getEmergencyServiceCategories() {
        return this.mEmergencyServiceCategories;
    }

    public List<String> getEmergencyUrns() {
        return this.mEmergencyUrns;
    }

    public int getEmergencyCallRouting() {
        return this.mEmergencyCallRouting;
    }

    public boolean isEmergencyCallTesting() {
        return this.mEmergencyCallTesting;
    }

    public boolean hasKnownUserIntentEmergency() {
        return this.mHasKnownUserIntentEmergency;
    }

    public Set<RtpHeaderExtensionType> getAcceptedRtpHeaderExtensionTypes() {
        return this.mAcceptedRtpHeaderExtensionTypes;
    }

    public void setAcceptedRtpHeaderExtensionTypes(Set<RtpHeaderExtensionType> rtpHeaderExtensions) {
        this.mAcceptedRtpHeaderExtensionTypes.clear();
        this.mAcceptedRtpHeaderExtensionTypes.addAll(rtpHeaderExtensions);
    }
}
