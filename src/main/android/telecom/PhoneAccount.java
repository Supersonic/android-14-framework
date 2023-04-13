package android.telecom;

import android.annotation.SystemApi;
import android.graphics.drawable.Icon;
import android.hardware.gnss.GnssSignalType;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class PhoneAccount implements Parcelable {
    public static final int CAPABILITY_ADHOC_CONFERENCE_CALLING = 16384;
    public static final int CAPABILITY_CALL_COMPOSER = 32768;
    public static final int CAPABILITY_CALL_PROVIDER = 2;
    public static final int CAPABILITY_CALL_SUBJECT = 64;
    public static final int CAPABILITY_CONNECTION_MANAGER = 1;
    @SystemApi
    public static final int CAPABILITY_EMERGENCY_CALLS_ONLY = 128;
    @SystemApi
    public static final int CAPABILITY_EMERGENCY_PREFERRED = 8192;
    @SystemApi
    public static final int CAPABILITY_EMERGENCY_VIDEO_CALLING = 512;
    @SystemApi
    public static final int CAPABILITY_MULTI_USER = 32;
    public static final int CAPABILITY_PLACE_EMERGENCY_CALLS = 16;
    public static final int CAPABILITY_RTT = 4096;
    public static final int CAPABILITY_SELF_MANAGED = 2048;
    public static final int CAPABILITY_SIM_SUBSCRIPTION = 4;
    public static final int CAPABILITY_SUPPORTS_CALL_STREAMING = 524288;
    public static final int CAPABILITY_SUPPORTS_TRANSACTIONAL_OPERATIONS = 262144;
    public static final int CAPABILITY_SUPPORTS_VIDEO_CALLING = 1024;
    public static final int CAPABILITY_SUPPORTS_VOICE_CALLING_INDICATIONS = 65536;
    public static final int CAPABILITY_VIDEO_CALLING = 8;
    public static final int CAPABILITY_VIDEO_CALLING_RELIES_ON_PRESENCE = 256;
    public static final int CAPABILITY_VOICE_CALLING_AVAILABLE = 131072;
    public static final Parcelable.Creator<PhoneAccount> CREATOR = new Parcelable.Creator<PhoneAccount>() { // from class: android.telecom.PhoneAccount.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneAccount createFromParcel(Parcel in) {
            return new PhoneAccount(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneAccount[] newArray(int size) {
            return new PhoneAccount[size];
        }
    };
    public static final String EXTRA_ADD_SELF_MANAGED_CALLS_TO_INCALLSERVICE = "android.telecom.extra.ADD_SELF_MANAGED_CALLS_TO_INCALLSERVICE";
    public static final String EXTRA_ALWAYS_USE_VOIP_AUDIO_MODE = "android.telecom.extra.ALWAYS_USE_VOIP_AUDIO_MODE";
    public static final String EXTRA_CALL_SUBJECT_CHARACTER_ENCODING = "android.telecom.extra.CALL_SUBJECT_CHARACTER_ENCODING";
    public static final String EXTRA_CALL_SUBJECT_MAX_LENGTH = "android.telecom.extra.CALL_SUBJECT_MAX_LENGTH";
    public static final String EXTRA_LOG_SELF_MANAGED_CALLS = "android.telecom.extra.LOG_SELF_MANAGED_CALLS";
    @SystemApi
    public static final String EXTRA_PLAY_CALL_RECORDING_TONE = "android.telecom.extra.PLAY_CALL_RECORDING_TONE";
    public static final String EXTRA_SKIP_CALL_FILTERING = "android.telecom.extra.SKIP_CALL_FILTERING";
    @SystemApi
    public static final String EXTRA_SORT_ORDER = "android.telecom.extra.SORT_ORDER";
    public static final String EXTRA_SUPPORTS_HANDOVER_FROM = "android.telecom.extra.SUPPORTS_HANDOVER_FROM";
    public static final String EXTRA_SUPPORTS_HANDOVER_TO = "android.telecom.extra.SUPPORTS_HANDOVER_TO";
    public static final String EXTRA_SUPPORTS_VIDEO_CALLING_FALLBACK = "android.telecom.extra.SUPPORTS_VIDEO_CALLING_FALLBACK";
    public static final int NO_HIGHLIGHT_COLOR = 0;
    public static final int NO_ICON_TINT = 0;
    public static final int NO_RESOURCE_ID = -1;
    public static final String SCHEME_SIP = "sip";
    public static final String SCHEME_TEL = "tel";
    public static final String SCHEME_VOICEMAIL = "voicemail";
    private final PhoneAccountHandle mAccountHandle;
    private final Uri mAddress;
    private final int mCapabilities;
    private final Bundle mExtras;
    private String mGroupId;
    private final int mHighlightColor;
    private final Icon mIcon;
    private boolean mIsEnabled;
    private final CharSequence mLabel;
    private final CharSequence mShortDescription;
    private final Uri mSubscriptionAddress;
    private final int mSupportedAudioRoutes;
    private final List<String> mSupportedUriSchemes;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneAccount that = (PhoneAccount) o;
        if (this.mCapabilities == that.mCapabilities && this.mHighlightColor == that.mHighlightColor && this.mSupportedAudioRoutes == that.mSupportedAudioRoutes && this.mIsEnabled == that.mIsEnabled && Objects.equals(this.mAccountHandle, that.mAccountHandle) && Objects.equals(this.mAddress, that.mAddress) && Objects.equals(this.mSubscriptionAddress, that.mSubscriptionAddress) && Objects.equals(this.mLabel, that.mLabel) && Objects.equals(this.mShortDescription, that.mShortDescription) && Objects.equals(this.mSupportedUriSchemes, that.mSupportedUriSchemes) && areBundlesEqual(this.mExtras, that.mExtras) && Objects.equals(this.mGroupId, that.mGroupId)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mAccountHandle, this.mAddress, this.mSubscriptionAddress, Integer.valueOf(this.mCapabilities), Integer.valueOf(this.mHighlightColor), this.mLabel, this.mShortDescription, this.mSupportedUriSchemes, Integer.valueOf(this.mSupportedAudioRoutes), this.mExtras, Boolean.valueOf(this.mIsEnabled), this.mGroupId);
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private PhoneAccountHandle mAccountHandle;
        private Uri mAddress;
        private int mCapabilities;
        private Bundle mExtras;
        private String mGroupId;
        private int mHighlightColor;
        private Icon mIcon;
        private boolean mIsEnabled;
        private CharSequence mLabel;
        private CharSequence mShortDescription;
        private Uri mSubscriptionAddress;
        private int mSupportedAudioRoutes;
        private List<String> mSupportedUriSchemes;

        public Builder(PhoneAccountHandle accountHandle, CharSequence label) {
            this.mSupportedAudioRoutes = 31;
            this.mHighlightColor = 0;
            this.mSupportedUriSchemes = new ArrayList();
            this.mIsEnabled = false;
            this.mGroupId = "";
            this.mAccountHandle = accountHandle;
            this.mLabel = label;
        }

        public Builder(PhoneAccount phoneAccount) {
            this.mSupportedAudioRoutes = 31;
            this.mHighlightColor = 0;
            this.mSupportedUriSchemes = new ArrayList();
            this.mIsEnabled = false;
            this.mGroupId = "";
            this.mAccountHandle = phoneAccount.getAccountHandle();
            this.mAddress = phoneAccount.getAddress();
            this.mSubscriptionAddress = phoneAccount.getSubscriptionAddress();
            this.mCapabilities = phoneAccount.getCapabilities();
            this.mHighlightColor = phoneAccount.getHighlightColor();
            this.mLabel = phoneAccount.getLabel();
            this.mShortDescription = phoneAccount.getShortDescription();
            this.mSupportedUriSchemes.addAll(phoneAccount.getSupportedUriSchemes());
            this.mIcon = phoneAccount.getIcon();
            this.mIsEnabled = phoneAccount.isEnabled();
            this.mExtras = phoneAccount.getExtras();
            this.mGroupId = phoneAccount.getGroupId();
            this.mSupportedAudioRoutes = phoneAccount.getSupportedAudioRoutes();
        }

        public Builder setLabel(CharSequence label) {
            this.mLabel = label;
            return this;
        }

        public Builder setAddress(Uri value) {
            this.mAddress = value;
            return this;
        }

        public Builder setSubscriptionAddress(Uri value) {
            this.mSubscriptionAddress = value;
            return this;
        }

        public Builder setCapabilities(int value) {
            this.mCapabilities = value;
            return this;
        }

        public Builder setIcon(Icon icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder setHighlightColor(int value) {
            this.mHighlightColor = value;
            return this;
        }

        public Builder setShortDescription(CharSequence value) {
            this.mShortDescription = value;
            return this;
        }

        public Builder addSupportedUriScheme(String uriScheme) {
            if (!TextUtils.isEmpty(uriScheme) && !this.mSupportedUriSchemes.contains(uriScheme)) {
                this.mSupportedUriSchemes.add(uriScheme);
            }
            return this;
        }

        public Builder setSupportedUriSchemes(List<String> uriSchemes) {
            this.mSupportedUriSchemes.clear();
            if (uriSchemes != null && !uriSchemes.isEmpty()) {
                for (String uriScheme : uriSchemes) {
                    addSupportedUriScheme(uriScheme);
                }
            }
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setIsEnabled(boolean isEnabled) {
            this.mIsEnabled = isEnabled;
            return this;
        }

        @SystemApi
        public Builder setGroupId(String groupId) {
            if (groupId != null) {
                this.mGroupId = groupId;
            } else {
                this.mGroupId = "";
            }
            return this;
        }

        public Builder setSupportedAudioRoutes(int routes) {
            this.mSupportedAudioRoutes = routes;
            return this;
        }

        public PhoneAccount build() {
            if (this.mSupportedUriSchemes.isEmpty()) {
                addSupportedUriScheme(PhoneAccount.SCHEME_TEL);
            }
            return new PhoneAccount(this.mAccountHandle, this.mAddress, this.mSubscriptionAddress, this.mCapabilities, this.mIcon, this.mHighlightColor, this.mLabel, this.mShortDescription, this.mSupportedUriSchemes, this.mExtras, this.mSupportedAudioRoutes, this.mIsEnabled, this.mGroupId);
        }
    }

    private PhoneAccount(PhoneAccountHandle account, Uri address, Uri subscriptionAddress, int capabilities, Icon icon, int highlightColor, CharSequence label, CharSequence shortDescription, List<String> supportedUriSchemes, Bundle extras, int supportedAudioRoutes, boolean isEnabled, String groupId) {
        this.mAccountHandle = account;
        this.mAddress = address;
        this.mSubscriptionAddress = subscriptionAddress;
        this.mCapabilities = capabilities;
        this.mIcon = icon;
        this.mHighlightColor = highlightColor;
        this.mLabel = label;
        this.mShortDescription = shortDescription;
        this.mSupportedUriSchemes = Collections.unmodifiableList(supportedUriSchemes);
        this.mExtras = extras;
        this.mSupportedAudioRoutes = supportedAudioRoutes;
        this.mIsEnabled = isEnabled;
        this.mGroupId = groupId;
    }

    public static Builder builder(PhoneAccountHandle accountHandle, CharSequence label) {
        return new Builder(accountHandle, label);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public PhoneAccountHandle getAccountHandle() {
        return this.mAccountHandle;
    }

    public Uri getAddress() {
        return this.mAddress;
    }

    public Uri getSubscriptionAddress() {
        return this.mSubscriptionAddress;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public boolean hasCapabilities(int capability) {
        return (this.mCapabilities & capability) == capability;
    }

    public boolean hasAudioRoutes(int routes) {
        return (this.mSupportedAudioRoutes & routes) == routes;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public CharSequence getShortDescription() {
        return this.mShortDescription;
    }

    public List<String> getSupportedUriSchemes() {
        return this.mSupportedUriSchemes;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getSupportedAudioRoutes() {
        return this.mSupportedAudioRoutes;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public String getGroupId() {
        return this.mGroupId;
    }

    public boolean supportsUriScheme(String uriScheme) {
        List<String> list = this.mSupportedUriSchemes;
        if (list == null || uriScheme == null) {
            return false;
        }
        for (String scheme : list) {
            if (scheme != null && scheme.equals(uriScheme)) {
                return true;
            }
        }
        return false;
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.mIsEnabled = isEnabled;
    }

    public boolean isSelfManaged() {
        return (this.mCapabilities & 2048) == 2048;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (this.mAccountHandle == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            this.mAccountHandle.writeToParcel(out, flags);
        }
        if (this.mAddress == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            this.mAddress.writeToParcel(out, flags);
        }
        if (this.mSubscriptionAddress == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            this.mSubscriptionAddress.writeToParcel(out, flags);
        }
        out.writeInt(this.mCapabilities);
        out.writeInt(this.mHighlightColor);
        out.writeCharSequence(this.mLabel);
        out.writeCharSequence(this.mShortDescription);
        out.writeStringList(this.mSupportedUriSchemes);
        if (this.mIcon == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            this.mIcon.writeToParcel(out, flags);
        }
        out.writeByte(this.mIsEnabled ? (byte) 1 : (byte) 0);
        out.writeBundle(this.mExtras);
        out.writeString(this.mGroupId);
        out.writeInt(this.mSupportedAudioRoutes);
    }

    private PhoneAccount(Parcel in) {
        if (in.readInt() > 0) {
            this.mAccountHandle = PhoneAccountHandle.CREATOR.createFromParcel(in);
        } else {
            this.mAccountHandle = null;
        }
        if (in.readInt() > 0) {
            this.mAddress = Uri.CREATOR.createFromParcel(in);
        } else {
            this.mAddress = null;
        }
        if (in.readInt() > 0) {
            this.mSubscriptionAddress = Uri.CREATOR.createFromParcel(in);
        } else {
            this.mSubscriptionAddress = null;
        }
        this.mCapabilities = in.readInt();
        this.mHighlightColor = in.readInt();
        this.mLabel = in.readCharSequence();
        this.mShortDescription = in.readCharSequence();
        this.mSupportedUriSchemes = Collections.unmodifiableList(in.createStringArrayList());
        if (in.readInt() > 0) {
            this.mIcon = Icon.CREATOR.createFromParcel(in);
        } else {
            this.mIcon = null;
        }
        this.mIsEnabled = in.readByte() == 1;
        this.mExtras = in.readBundle();
        this.mGroupId = in.readString();
        this.mSupportedAudioRoutes = in.readInt();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append("[[").append(this.mIsEnabled ? 'X' : ' ').append("] PhoneAccount: ").append(this.mAccountHandle).append(" Capabilities: ").append(capabilitiesToString()).append(" Audio Routes: ").append(audioRoutesToString()).append(" Schemes: ");
        for (String scheme : this.mSupportedUriSchemes) {
            sb.append(scheme).append(" ");
        }
        sb.append(" Extras: ");
        sb.append(this.mExtras);
        sb.append(" GroupId: ");
        sb.append(Log.pii(this.mGroupId));
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    public String capabilitiesToString() {
        StringBuilder sb = new StringBuilder();
        if (hasCapabilities(2048)) {
            sb.append("SelfManaged ");
        }
        if (hasCapabilities(1024)) {
            sb.append("SuppVideo ");
        }
        if (hasCapabilities(8)) {
            sb.append("Video ");
        }
        if (hasCapabilities(256)) {
            sb.append("Presence ");
        }
        if (hasCapabilities(2)) {
            sb.append("CallProvider ");
        }
        if (hasCapabilities(64)) {
            sb.append("CallSubject ");
        }
        if (hasCapabilities(1)) {
            sb.append("ConnectionMgr ");
        }
        if (hasCapabilities(128)) {
            sb.append("EmergOnly ");
        }
        if (hasCapabilities(32)) {
            sb.append("MultiUser ");
        }
        if (hasCapabilities(16)) {
            sb.append("PlaceEmerg ");
        }
        if (hasCapabilities(8192)) {
            sb.append("EmerPrefer ");
        }
        if (hasCapabilities(512)) {
            sb.append("EmergVideo ");
        }
        if (hasCapabilities(4)) {
            sb.append("SimSub ");
        }
        if (hasCapabilities(4096)) {
            sb.append("Rtt ");
        }
        if (hasCapabilities(16384)) {
            sb.append("AdhocConf ");
        }
        if (hasCapabilities(32768)) {
            sb.append("CallComposer ");
        }
        if (hasCapabilities(65536)) {
            sb.append("SuppVoice ");
        }
        if (hasCapabilities(131072)) {
            sb.append("Voice ");
        }
        if (hasCapabilities(262144)) {
            sb.append("TransactOps ");
        }
        if (hasCapabilities(524288)) {
            sb.append("Stream ");
        }
        return sb.toString();
    }

    private String audioRoutesToString() {
        StringBuilder sb = new StringBuilder();
        if (hasAudioRoutes(2)) {
            sb.append(GnssSignalType.CODE_TYPE_B);
        }
        if (hasAudioRoutes(1)) {
            sb.append("E");
        }
        if (hasAudioRoutes(8)) {
            sb.append(GnssSignalType.CODE_TYPE_S);
        }
        if (hasAudioRoutes(4)) {
            sb.append(GnssSignalType.CODE_TYPE_W);
        }
        return sb.toString();
    }

    private static boolean areBundlesEqual(Bundle extras, Bundle newExtras) {
        if (extras == null || newExtras == null) {
            return extras == newExtras;
        } else if (extras.size() != newExtras.size()) {
            return false;
        } else {
            for (String key : extras.keySet()) {
                if (key != null) {
                    Object value = extras.get(key);
                    Object newValue = newExtras.get(key);
                    if (!Objects.equals(value, newValue)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
