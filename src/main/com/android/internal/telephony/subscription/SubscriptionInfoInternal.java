package com.android.internal.telephony.subscription;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.UiccAccessRule;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class SubscriptionInfoInternal {
    private final String mAllowedNetworkTypesForReasons;
    private final int mAreUiccApplicationsEnabled;
    private final int mCardId;
    private final String mCardString;
    private final byte[] mCarrierConfigAccessRules;
    private final int mCarrierId;
    private final String mCarrierName;
    private final String mCountryIso;
    private final int mDataRoaming;
    private final String mDeviceToDeviceStatusSharingContacts;
    private final int mDeviceToDeviceStatusSharingPreference;
    private final String mDisplayName;
    private final int mDisplayNameSource;
    private final String mEhplmns;
    private final String mEnabledMobileDataPolicies;
    private final String mGroupOwner;
    private final String mGroupUuid;
    private final String mHplmns;
    private final String mIccId;
    private final int mIconTint;
    private final int mId;
    private final String mImsi;
    private final int mIsCrossSimCallingEnabled;
    private final int mIsEmbedded;
    private final int mIsEnhanced4GModeEnabled;
    private final boolean mIsGroupDisabled;
    private final int mIsNrAdvancedCallingEnabled;
    private final int mIsOpportunistic;
    private final int mIsRcsUceEnabled;
    private final int mIsRemovableEmbedded;
    private final int mIsSatelliteEnabled;
    private final int mIsVideoTelephonyEnabled;
    private final int mIsVoImsOptInEnabled;
    private final int mIsWifiCallingEnabled;
    private final int mIsWifiCallingEnabledForRoaming;
    private final int mLastUsedTPMessageReference;
    private final String mMcc;
    private final String mMnc;
    private final byte[] mNativeAccessRules;
    private final String mNumber;
    private final String mNumberFromCarrier;
    private final String mNumberFromIms;
    private final int mPortIndex;
    private final int mProfileClass;
    private final byte[] mRcsConfig;
    private final int mSimSlotIndex;
    private final int mType;
    private final int mUsageSetting;
    private final int mUserId;
    private final int mWifiCallingMode;
    private final int mWifiCallingModeForRoaming;

    private SubscriptionInfoInternal(Builder builder) {
        this.mId = builder.mId;
        this.mIccId = builder.mIccId;
        this.mSimSlotIndex = builder.mSimSlotIndex;
        this.mDisplayName = builder.mDisplayName;
        this.mCarrierName = builder.mCarrierName;
        this.mDisplayNameSource = builder.mDisplayNameSource;
        this.mIconTint = builder.mIconTint;
        this.mNumber = builder.mNumber;
        this.mDataRoaming = builder.mDataRoaming;
        this.mMcc = builder.mMcc;
        this.mMnc = builder.mMnc;
        this.mEhplmns = builder.mEhplmns;
        this.mHplmns = builder.mHplmns;
        this.mIsEmbedded = builder.mIsEmbedded;
        this.mCardString = builder.mCardString;
        this.mNativeAccessRules = builder.mNativeAccessRules;
        this.mCarrierConfigAccessRules = builder.mCarrierConfigAccessRules;
        this.mIsRemovableEmbedded = builder.mIsRemovableEmbedded;
        this.mIsEnhanced4GModeEnabled = builder.mIsEnhanced4GModeEnabled;
        this.mIsVideoTelephonyEnabled = builder.mIsVideoTelephonyEnabled;
        this.mIsWifiCallingEnabled = builder.mIsWifiCallingEnabled;
        this.mWifiCallingMode = builder.mWifiCallingMode;
        this.mWifiCallingModeForRoaming = builder.mWifiCallingModeForRoaming;
        this.mIsWifiCallingEnabledForRoaming = builder.mIsWifiCallingEnabledForRoaming;
        this.mIsOpportunistic = builder.mIsOpportunistic;
        this.mGroupUuid = builder.mGroupUuid;
        this.mCountryIso = builder.mCountryIso;
        this.mCarrierId = builder.mCarrierId;
        this.mProfileClass = builder.mProfileClass;
        this.mType = builder.mType;
        this.mGroupOwner = builder.mGroupOwner;
        this.mEnabledMobileDataPolicies = builder.mEnabledMobileDataPolicies;
        this.mImsi = builder.mImsi;
        this.mAreUiccApplicationsEnabled = builder.mAreUiccApplicationsEnabled;
        this.mIsRcsUceEnabled = builder.mIsRcsUceEnabled;
        this.mIsCrossSimCallingEnabled = builder.mIsCrossSimCallingEnabled;
        this.mRcsConfig = builder.mRcsConfig;
        this.mAllowedNetworkTypesForReasons = builder.mAllowedNetworkTypesForReasons;
        this.mDeviceToDeviceStatusSharingPreference = builder.mDeviceToDeviceStatusSharingPreference;
        this.mIsVoImsOptInEnabled = builder.mIsVoImsOptInEnabled;
        this.mDeviceToDeviceStatusSharingContacts = builder.mDeviceToDeviceStatusSharingContacts;
        this.mIsNrAdvancedCallingEnabled = builder.mIsNrAdvancedCallingEnabled;
        this.mNumberFromCarrier = builder.mNumberFromCarrier;
        this.mNumberFromIms = builder.mNumberFromIms;
        this.mPortIndex = builder.mPortIndex;
        this.mUsageSetting = builder.mUsageSetting;
        this.mLastUsedTPMessageReference = builder.mLastUsedTPMessageReference;
        this.mUserId = builder.mUserId;
        this.mIsSatelliteEnabled = builder.mIsSatelliteEnabled;
        this.mCardId = builder.mCardId;
        this.mIsGroupDisabled = builder.mIsGroupDisabled;
    }

    public int getSubscriptionId() {
        return this.mId;
    }

    public String getIccId() {
        return this.mIccId;
    }

    public int getSimSlotIndex() {
        return this.mSimSlotIndex;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public String getCarrierName() {
        return this.mCarrierName;
    }

    public int getDisplayNameSource() {
        return this.mDisplayNameSource;
    }

    public int getIconTint() {
        return this.mIconTint;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getDataRoaming() {
        return this.mDataRoaming;
    }

    public String getMcc() {
        return this.mMcc;
    }

    public String getMnc() {
        return this.mMnc;
    }

    public String getEhplmns() {
        return this.mEhplmns;
    }

    public String getHplmns() {
        return this.mHplmns;
    }

    public boolean isEmbedded() {
        return this.mIsEmbedded != 0;
    }

    public int getEmbedded() {
        return this.mIsEmbedded;
    }

    public String getCardString() {
        return this.mCardString;
    }

    public byte[] getNativeAccessRules() {
        return this.mNativeAccessRules;
    }

    public byte[] getCarrierConfigAccessRules() {
        return this.mCarrierConfigAccessRules;
    }

    public boolean isRemovableEmbedded() {
        return this.mIsRemovableEmbedded != 0;
    }

    public int getRemovableEmbedded() {
        return this.mIsRemovableEmbedded;
    }

    public boolean isEnhanced4GModeEnabled() {
        return this.mIsEnhanced4GModeEnabled == 1;
    }

    public int getEnhanced4GModeEnabled() {
        return this.mIsEnhanced4GModeEnabled;
    }

    public boolean isVideoTelephonyEnabled() {
        return this.mIsVideoTelephonyEnabled != 0;
    }

    public int getVideoTelephonyEnabled() {
        return this.mIsVideoTelephonyEnabled;
    }

    public boolean isWifiCallingEnabled() {
        return this.mIsWifiCallingEnabled == 1;
    }

    public int getWifiCallingEnabled() {
        return this.mIsWifiCallingEnabled;
    }

    public int getWifiCallingMode() {
        return this.mWifiCallingMode;
    }

    public int getWifiCallingModeForRoaming() {
        return this.mWifiCallingModeForRoaming;
    }

    public boolean isWifiCallingEnabledForRoaming() {
        return this.mIsWifiCallingEnabledForRoaming == 1;
    }

    public int getWifiCallingEnabledForRoaming() {
        return this.mIsWifiCallingEnabledForRoaming;
    }

    public boolean isOpportunistic() {
        return this.mIsOpportunistic != 0;
    }

    public int getOpportunistic() {
        return this.mIsOpportunistic;
    }

    public String getGroupUuid() {
        return this.mGroupUuid;
    }

    public String getCountryIso() {
        return this.mCountryIso;
    }

    public int getCarrierId() {
        return this.mCarrierId;
    }

    public int getProfileClass() {
        return this.mProfileClass;
    }

    public int getSubscriptionType() {
        return this.mType;
    }

    public String getGroupOwner() {
        return this.mGroupOwner;
    }

    public String getEnabledMobileDataPolicies() {
        return this.mEnabledMobileDataPolicies;
    }

    public String getImsi() {
        return this.mImsi;
    }

    public boolean areUiccApplicationsEnabled() {
        return this.mAreUiccApplicationsEnabled != 0;
    }

    public int getUiccApplicationsEnabled() {
        return this.mAreUiccApplicationsEnabled;
    }

    public boolean isRcsUceEnabled() {
        return this.mIsRcsUceEnabled != 0;
    }

    public int getRcsUceEnabled() {
        return this.mIsRcsUceEnabled;
    }

    public boolean isCrossSimCallingEnabled() {
        return this.mIsCrossSimCallingEnabled != 0;
    }

    public int getCrossSimCallingEnabled() {
        return this.mIsCrossSimCallingEnabled;
    }

    public byte[] getRcsConfig() {
        return this.mRcsConfig;
    }

    public String getAllowedNetworkTypesForReasons() {
        return this.mAllowedNetworkTypesForReasons;
    }

    public int getDeviceToDeviceStatusSharingPreference() {
        return this.mDeviceToDeviceStatusSharingPreference;
    }

    public boolean isVoImsOptInEnabled() {
        return this.mIsVoImsOptInEnabled != 0;
    }

    public int getVoImsOptInEnabled() {
        return this.mIsVoImsOptInEnabled;
    }

    public String getDeviceToDeviceStatusSharingContacts() {
        return this.mDeviceToDeviceStatusSharingContacts;
    }

    public boolean isNrAdvancedCallingEnabled() {
        return this.mIsNrAdvancedCallingEnabled == 1;
    }

    public int getNrAdvancedCallingEnabled() {
        return this.mIsNrAdvancedCallingEnabled;
    }

    public String getNumberFromCarrier() {
        return this.mNumberFromCarrier;
    }

    public String getNumberFromIms() {
        return this.mNumberFromIms;
    }

    public int getPortIndex() {
        return this.mPortIndex;
    }

    public int getUsageSetting() {
        return this.mUsageSetting;
    }

    public int getLastUsedTPMessageReference() {
        return this.mLastUsedTPMessageReference;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public int getSatelliteEnabled() {
        return this.mIsSatelliteEnabled;
    }

    public int getCardId() {
        return this.mCardId;
    }

    public boolean isGroupDisabled() {
        return this.mIsGroupDisabled;
    }

    public boolean isActive() {
        return this.mSimSlotIndex >= 0 || this.mType == 1;
    }

    public boolean isVisible() {
        return !isOpportunistic() || TextUtils.isEmpty(this.mGroupUuid);
    }

    public SubscriptionInfo toSubscriptionInfo() {
        SubscriptionInfo.Builder embedded = new SubscriptionInfo.Builder().setId(this.mId).setIccId(this.mIccId).setSimSlotIndex(this.mSimSlotIndex).setDisplayName(this.mDisplayName).setCarrierName(this.mCarrierName).setDisplayNameSource(this.mDisplayNameSource).setIconTint(this.mIconTint).setNumber(this.mNumber).setDataRoaming(this.mDataRoaming).setMcc(this.mMcc).setMnc(this.mMnc).setEhplmns(TextUtils.isEmpty(this.mEhplmns) ? null : this.mEhplmns.split(",")).setHplmns(TextUtils.isEmpty(this.mHplmns) ? null : this.mHplmns.split(",")).setCountryIso(this.mCountryIso).setEmbedded(this.mIsEmbedded != 0);
        byte[] bArr = this.mNativeAccessRules;
        SubscriptionInfo.Builder groupOwner = embedded.setNativeAccessRules(bArr.length == 0 ? null : UiccAccessRule.decodeRules(bArr)).setCardString(this.mCardString).setCardId(this.mCardId).setOpportunistic(this.mIsOpportunistic != 0).setGroupUuid(this.mGroupUuid).setGroupDisabled(this.mIsGroupDisabled).setCarrierId(this.mCarrierId).setProfileClass(this.mProfileClass).setType(this.mType).setGroupOwner(this.mGroupOwner);
        byte[] bArr2 = this.mCarrierConfigAccessRules;
        return groupOwner.setCarrierConfigAccessRules(bArr2.length != 0 ? UiccAccessRule.decodeRules(bArr2) : null).setUiccApplicationsEnabled(this.mAreUiccApplicationsEnabled != 0).setPortIndex(this.mPortIndex).setUsageSetting(this.mUsageSetting).build();
    }

    public static String givePrintableId(String str) {
        if (str != null) {
            int length = str.length();
            if (length <= 6 || TelephonyUtils.IS_DEBUGGABLE) {
                return str;
            }
            StringBuilder sb = new StringBuilder();
            int i = length - 6;
            sb.append(str.substring(0, i));
            sb.append(Rlog.pii(false, str.substring(i)));
            return sb.toString();
        }
        return null;
    }

    public String toString() {
        return "[SubscriptionInfoInternal: id=" + this.mId + " iccId=" + givePrintableId(this.mIccId) + " simSlotIndex=" + this.mSimSlotIndex + " portIndex=" + this.mPortIndex + " isEmbedded=" + this.mIsEmbedded + " isRemovableEmbedded=" + this.mIsRemovableEmbedded + " carrierId=" + this.mCarrierId + " displayName=" + this.mDisplayName + " carrierName=" + this.mCarrierName + " isOpportunistic=" + this.mIsOpportunistic + " groupUuid=" + this.mGroupUuid + " groupOwner=" + this.mGroupOwner + " displayNameSource=" + SubscriptionManager.displayNameSourceToString(this.mDisplayNameSource) + " iconTint=" + this.mIconTint + " number=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mNumber) + " dataRoaming=" + this.mDataRoaming + " mcc=" + this.mMcc + " mnc=" + this.mMnc + " ehplmns=" + this.mEhplmns + " hplmns=" + this.mHplmns + " cardString=" + givePrintableId(this.mCardString) + " cardId=" + this.mCardId + " nativeAccessRules=" + IccUtils.bytesToHexString(this.mNativeAccessRules) + " carrierConfigAccessRules=" + IccUtils.bytesToHexString(this.mCarrierConfigAccessRules) + " countryIso=" + this.mCountryIso + " profileClass=" + this.mProfileClass + " type=" + SubscriptionManager.subscriptionTypeToString(this.mType) + " areUiccApplicationsEnabled=" + this.mAreUiccApplicationsEnabled + " usageSetting=" + SubscriptionManager.usageSettingToString(this.mUsageSetting) + " isEnhanced4GModeEnabled=" + this.mIsEnhanced4GModeEnabled + " isVideoTelephonyEnabled=" + this.mIsVideoTelephonyEnabled + " isWifiCallingEnabled=" + this.mIsWifiCallingEnabled + " isWifiCallingEnabledForRoaming=" + this.mIsWifiCallingEnabledForRoaming + " wifiCallingMode=" + ImsMmTelManager.wifiCallingModeToString(this.mWifiCallingMode) + " wifiCallingModeForRoaming=" + ImsMmTelManager.wifiCallingModeToString(this.mWifiCallingModeForRoaming) + " enabledMobileDataPolicies=" + this.mEnabledMobileDataPolicies + " imsi=" + givePrintableId(this.mImsi) + " rcsUceEnabled=" + this.mIsRcsUceEnabled + " crossSimCallingEnabled=" + this.mIsCrossSimCallingEnabled + " rcsConfig=" + IccUtils.bytesToHexString(this.mRcsConfig) + " allowedNetworkTypesForReasons=" + this.mAllowedNetworkTypesForReasons + " deviceToDeviceStatusSharingPreference=" + this.mDeviceToDeviceStatusSharingPreference + " isVoImsOptInEnabled=" + this.mIsVoImsOptInEnabled + " deviceToDeviceStatusSharingContacts=" + this.mDeviceToDeviceStatusSharingContacts + " numberFromCarrier=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mNumberFromCarrier) + " numberFromIms=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mNumberFromIms) + " userId=" + this.mUserId + " isSatelliteEnabled=" + this.mIsSatelliteEnabled + " isGroupDisabled=" + this.mIsGroupDisabled + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SubscriptionInfoInternal subscriptionInfoInternal = (SubscriptionInfoInternal) obj;
        return this.mId == subscriptionInfoInternal.mId && this.mSimSlotIndex == subscriptionInfoInternal.mSimSlotIndex && this.mDisplayNameSource == subscriptionInfoInternal.mDisplayNameSource && this.mIconTint == subscriptionInfoInternal.mIconTint && this.mDataRoaming == subscriptionInfoInternal.mDataRoaming && this.mIsEmbedded == subscriptionInfoInternal.mIsEmbedded && this.mIsRemovableEmbedded == subscriptionInfoInternal.mIsRemovableEmbedded && this.mIsEnhanced4GModeEnabled == subscriptionInfoInternal.mIsEnhanced4GModeEnabled && this.mIsVideoTelephonyEnabled == subscriptionInfoInternal.mIsVideoTelephonyEnabled && this.mIsWifiCallingEnabled == subscriptionInfoInternal.mIsWifiCallingEnabled && this.mWifiCallingMode == subscriptionInfoInternal.mWifiCallingMode && this.mWifiCallingModeForRoaming == subscriptionInfoInternal.mWifiCallingModeForRoaming && this.mIsWifiCallingEnabledForRoaming == subscriptionInfoInternal.mIsWifiCallingEnabledForRoaming && this.mIsOpportunistic == subscriptionInfoInternal.mIsOpportunistic && this.mCarrierId == subscriptionInfoInternal.mCarrierId && this.mProfileClass == subscriptionInfoInternal.mProfileClass && this.mType == subscriptionInfoInternal.mType && this.mAreUiccApplicationsEnabled == subscriptionInfoInternal.mAreUiccApplicationsEnabled && this.mIsRcsUceEnabled == subscriptionInfoInternal.mIsRcsUceEnabled && this.mIsCrossSimCallingEnabled == subscriptionInfoInternal.mIsCrossSimCallingEnabled && this.mDeviceToDeviceStatusSharingPreference == subscriptionInfoInternal.mDeviceToDeviceStatusSharingPreference && this.mIsVoImsOptInEnabled == subscriptionInfoInternal.mIsVoImsOptInEnabled && this.mIsNrAdvancedCallingEnabled == subscriptionInfoInternal.mIsNrAdvancedCallingEnabled && this.mPortIndex == subscriptionInfoInternal.mPortIndex && this.mUsageSetting == subscriptionInfoInternal.mUsageSetting && this.mLastUsedTPMessageReference == subscriptionInfoInternal.mLastUsedTPMessageReference && this.mUserId == subscriptionInfoInternal.mUserId && this.mCardId == subscriptionInfoInternal.mCardId && this.mIsGroupDisabled == subscriptionInfoInternal.mIsGroupDisabled && this.mIccId.equals(subscriptionInfoInternal.mIccId) && this.mDisplayName.equals(subscriptionInfoInternal.mDisplayName) && this.mCarrierName.equals(subscriptionInfoInternal.mCarrierName) && this.mNumber.equals(subscriptionInfoInternal.mNumber) && this.mMcc.equals(subscriptionInfoInternal.mMcc) && this.mMnc.equals(subscriptionInfoInternal.mMnc) && this.mEhplmns.equals(subscriptionInfoInternal.mEhplmns) && this.mHplmns.equals(subscriptionInfoInternal.mHplmns) && this.mCardString.equals(subscriptionInfoInternal.mCardString) && Arrays.equals(this.mNativeAccessRules, subscriptionInfoInternal.mNativeAccessRules) && Arrays.equals(this.mCarrierConfigAccessRules, subscriptionInfoInternal.mCarrierConfigAccessRules) && this.mGroupUuid.equals(subscriptionInfoInternal.mGroupUuid) && this.mCountryIso.equals(subscriptionInfoInternal.mCountryIso) && this.mGroupOwner.equals(subscriptionInfoInternal.mGroupOwner) && this.mEnabledMobileDataPolicies.equals(subscriptionInfoInternal.mEnabledMobileDataPolicies) && this.mImsi.equals(subscriptionInfoInternal.mImsi) && Arrays.equals(this.mRcsConfig, subscriptionInfoInternal.mRcsConfig) && this.mAllowedNetworkTypesForReasons.equals(subscriptionInfoInternal.mAllowedNetworkTypesForReasons) && this.mDeviceToDeviceStatusSharingContacts.equals(subscriptionInfoInternal.mDeviceToDeviceStatusSharingContacts) && this.mNumberFromCarrier.equals(subscriptionInfoInternal.mNumberFromCarrier) && this.mNumberFromIms.equals(subscriptionInfoInternal.mNumberFromIms) && this.mIsSatelliteEnabled == subscriptionInfoInternal.mIsSatelliteEnabled;
    }

    public int hashCode() {
        return (((((Objects.hash(Integer.valueOf(this.mId), this.mIccId, Integer.valueOf(this.mSimSlotIndex), this.mDisplayName, this.mCarrierName, Integer.valueOf(this.mDisplayNameSource), Integer.valueOf(this.mIconTint), this.mNumber, Integer.valueOf(this.mDataRoaming), this.mMcc, this.mMnc, this.mEhplmns, this.mHplmns, Integer.valueOf(this.mIsEmbedded), this.mCardString, Integer.valueOf(this.mIsRemovableEmbedded), Integer.valueOf(this.mIsEnhanced4GModeEnabled), Integer.valueOf(this.mIsVideoTelephonyEnabled), Integer.valueOf(this.mIsWifiCallingEnabled), Integer.valueOf(this.mWifiCallingMode), Integer.valueOf(this.mWifiCallingModeForRoaming), Integer.valueOf(this.mIsWifiCallingEnabledForRoaming), Integer.valueOf(this.mIsOpportunistic), this.mGroupUuid, this.mCountryIso, Integer.valueOf(this.mCarrierId), Integer.valueOf(this.mProfileClass), Integer.valueOf(this.mType), this.mGroupOwner, this.mEnabledMobileDataPolicies, this.mImsi, Integer.valueOf(this.mAreUiccApplicationsEnabled), Integer.valueOf(this.mIsRcsUceEnabled), Integer.valueOf(this.mIsCrossSimCallingEnabled), this.mAllowedNetworkTypesForReasons, Integer.valueOf(this.mDeviceToDeviceStatusSharingPreference), Integer.valueOf(this.mIsVoImsOptInEnabled), this.mDeviceToDeviceStatusSharingContacts, Integer.valueOf(this.mIsNrAdvancedCallingEnabled), this.mNumberFromCarrier, this.mNumberFromIms, Integer.valueOf(this.mPortIndex), Integer.valueOf(this.mUsageSetting), Integer.valueOf(this.mLastUsedTPMessageReference), Integer.valueOf(this.mUserId), Integer.valueOf(this.mIsSatelliteEnabled), Integer.valueOf(this.mCardId), Boolean.valueOf(this.mIsGroupDisabled)) * 31) + Arrays.hashCode(this.mNativeAccessRules)) * 31) + Arrays.hashCode(this.mCarrierConfigAccessRules)) * 31) + Arrays.hashCode(this.mRcsConfig);
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private String mAllowedNetworkTypesForReasons;
        private int mAreUiccApplicationsEnabled;
        private int mCardId;
        private String mCardString;
        private byte[] mCarrierConfigAccessRules;
        private int mCarrierId;
        private String mCarrierName;
        private String mCountryIso;
        private int mDataRoaming;
        private String mDeviceToDeviceStatusSharingContacts;
        private int mDeviceToDeviceStatusSharingPreference;
        private String mDisplayName;
        private int mDisplayNameSource;
        private String mEhplmns;
        private String mEnabledMobileDataPolicies;
        private String mGroupOwner;
        private String mGroupUuid;
        private String mHplmns;
        private String mIccId;
        private int mIconTint;
        private int mId;
        private String mImsi;
        private int mIsCrossSimCallingEnabled;
        private int mIsEmbedded;
        private int mIsEnhanced4GModeEnabled;
        private boolean mIsGroupDisabled;
        private int mIsNrAdvancedCallingEnabled;
        private int mIsOpportunistic;
        private int mIsRcsUceEnabled;
        private int mIsRemovableEmbedded;
        private int mIsSatelliteEnabled;
        private int mIsVideoTelephonyEnabled;
        private int mIsVoImsOptInEnabled;
        private int mIsWifiCallingEnabled;
        private int mIsWifiCallingEnabledForRoaming;
        private int mLastUsedTPMessageReference;
        private String mMcc;
        private String mMnc;
        private byte[] mNativeAccessRules;
        private String mNumber;
        private String mNumberFromCarrier;
        private String mNumberFromIms;
        private int mPortIndex;
        private int mProfileClass;
        private byte[] mRcsConfig;
        private int mSimSlotIndex;
        private int mType;
        private int mUsageSetting;
        private int mUserId;
        private int mWifiCallingMode;
        private int mWifiCallingModeForRoaming;

        public Builder() {
            this.mId = -1;
            this.mIccId = PhoneConfigurationManager.SSSS;
            this.mSimSlotIndex = -1;
            this.mDisplayName = PhoneConfigurationManager.SSSS;
            this.mCarrierName = PhoneConfigurationManager.SSSS;
            this.mDisplayNameSource = -1;
            this.mIconTint = 0;
            this.mNumber = PhoneConfigurationManager.SSSS;
            this.mDataRoaming = 0;
            this.mMcc = PhoneConfigurationManager.SSSS;
            this.mMnc = PhoneConfigurationManager.SSSS;
            this.mEhplmns = PhoneConfigurationManager.SSSS;
            this.mHplmns = PhoneConfigurationManager.SSSS;
            this.mIsEmbedded = 0;
            this.mCardString = PhoneConfigurationManager.SSSS;
            this.mNativeAccessRules = new byte[0];
            this.mCarrierConfigAccessRules = new byte[0];
            this.mIsRemovableEmbedded = 0;
            this.mIsEnhanced4GModeEnabled = -1;
            this.mIsVideoTelephonyEnabled = -1;
            this.mIsWifiCallingEnabled = -1;
            this.mWifiCallingMode = -1;
            this.mWifiCallingModeForRoaming = -1;
            this.mIsWifiCallingEnabledForRoaming = -1;
            this.mIsOpportunistic = 0;
            this.mGroupUuid = PhoneConfigurationManager.SSSS;
            this.mCountryIso = PhoneConfigurationManager.SSSS;
            this.mCarrierId = -1;
            this.mProfileClass = -1;
            this.mType = 0;
            this.mGroupOwner = PhoneConfigurationManager.SSSS;
            this.mEnabledMobileDataPolicies = PhoneConfigurationManager.SSSS;
            this.mImsi = PhoneConfigurationManager.SSSS;
            this.mAreUiccApplicationsEnabled = 1;
            this.mIsRcsUceEnabled = 0;
            this.mIsCrossSimCallingEnabled = 0;
            this.mRcsConfig = new byte[0];
            this.mAllowedNetworkTypesForReasons = PhoneConfigurationManager.SSSS;
            this.mDeviceToDeviceStatusSharingPreference = 0;
            this.mIsVoImsOptInEnabled = 0;
            this.mDeviceToDeviceStatusSharingContacts = PhoneConfigurationManager.SSSS;
            this.mIsNrAdvancedCallingEnabled = -1;
            this.mNumberFromCarrier = PhoneConfigurationManager.SSSS;
            this.mNumberFromIms = PhoneConfigurationManager.SSSS;
            this.mPortIndex = -1;
            this.mUsageSetting = -1;
            this.mLastUsedTPMessageReference = -1;
            this.mUserId = -10000;
            this.mIsSatelliteEnabled = -1;
            this.mCardId = -2;
        }

        public Builder(SubscriptionInfoInternal subscriptionInfoInternal) {
            this.mId = -1;
            this.mIccId = PhoneConfigurationManager.SSSS;
            this.mSimSlotIndex = -1;
            this.mDisplayName = PhoneConfigurationManager.SSSS;
            this.mCarrierName = PhoneConfigurationManager.SSSS;
            this.mDisplayNameSource = -1;
            this.mIconTint = 0;
            this.mNumber = PhoneConfigurationManager.SSSS;
            this.mDataRoaming = 0;
            this.mMcc = PhoneConfigurationManager.SSSS;
            this.mMnc = PhoneConfigurationManager.SSSS;
            this.mEhplmns = PhoneConfigurationManager.SSSS;
            this.mHplmns = PhoneConfigurationManager.SSSS;
            this.mIsEmbedded = 0;
            this.mCardString = PhoneConfigurationManager.SSSS;
            this.mNativeAccessRules = new byte[0];
            this.mCarrierConfigAccessRules = new byte[0];
            this.mIsRemovableEmbedded = 0;
            this.mIsEnhanced4GModeEnabled = -1;
            this.mIsVideoTelephonyEnabled = -1;
            this.mIsWifiCallingEnabled = -1;
            this.mWifiCallingMode = -1;
            this.mWifiCallingModeForRoaming = -1;
            this.mIsWifiCallingEnabledForRoaming = -1;
            this.mIsOpportunistic = 0;
            this.mGroupUuid = PhoneConfigurationManager.SSSS;
            this.mCountryIso = PhoneConfigurationManager.SSSS;
            this.mCarrierId = -1;
            this.mProfileClass = -1;
            this.mType = 0;
            this.mGroupOwner = PhoneConfigurationManager.SSSS;
            this.mEnabledMobileDataPolicies = PhoneConfigurationManager.SSSS;
            this.mImsi = PhoneConfigurationManager.SSSS;
            this.mAreUiccApplicationsEnabled = 1;
            this.mIsRcsUceEnabled = 0;
            this.mIsCrossSimCallingEnabled = 0;
            this.mRcsConfig = new byte[0];
            this.mAllowedNetworkTypesForReasons = PhoneConfigurationManager.SSSS;
            this.mDeviceToDeviceStatusSharingPreference = 0;
            this.mIsVoImsOptInEnabled = 0;
            this.mDeviceToDeviceStatusSharingContacts = PhoneConfigurationManager.SSSS;
            this.mIsNrAdvancedCallingEnabled = -1;
            this.mNumberFromCarrier = PhoneConfigurationManager.SSSS;
            this.mNumberFromIms = PhoneConfigurationManager.SSSS;
            this.mPortIndex = -1;
            this.mUsageSetting = -1;
            this.mLastUsedTPMessageReference = -1;
            this.mUserId = -10000;
            this.mIsSatelliteEnabled = -1;
            this.mCardId = -2;
            this.mId = subscriptionInfoInternal.mId;
            this.mIccId = subscriptionInfoInternal.mIccId;
            this.mSimSlotIndex = subscriptionInfoInternal.mSimSlotIndex;
            this.mDisplayName = subscriptionInfoInternal.mDisplayName;
            this.mCarrierName = subscriptionInfoInternal.mCarrierName;
            this.mDisplayNameSource = subscriptionInfoInternal.mDisplayNameSource;
            this.mIconTint = subscriptionInfoInternal.mIconTint;
            this.mNumber = subscriptionInfoInternal.mNumber;
            this.mDataRoaming = subscriptionInfoInternal.mDataRoaming;
            this.mMcc = subscriptionInfoInternal.mMcc;
            this.mMnc = subscriptionInfoInternal.mMnc;
            this.mEhplmns = subscriptionInfoInternal.mEhplmns;
            this.mHplmns = subscriptionInfoInternal.mHplmns;
            this.mIsEmbedded = subscriptionInfoInternal.mIsEmbedded;
            this.mCardString = subscriptionInfoInternal.mCardString;
            this.mNativeAccessRules = subscriptionInfoInternal.mNativeAccessRules;
            this.mCarrierConfigAccessRules = subscriptionInfoInternal.mCarrierConfigAccessRules;
            this.mIsRemovableEmbedded = subscriptionInfoInternal.mIsRemovableEmbedded;
            this.mIsEnhanced4GModeEnabled = subscriptionInfoInternal.mIsEnhanced4GModeEnabled;
            this.mIsVideoTelephonyEnabled = subscriptionInfoInternal.mIsVideoTelephonyEnabled;
            this.mIsWifiCallingEnabled = subscriptionInfoInternal.mIsWifiCallingEnabled;
            this.mWifiCallingMode = subscriptionInfoInternal.mWifiCallingMode;
            this.mWifiCallingModeForRoaming = subscriptionInfoInternal.mWifiCallingModeForRoaming;
            this.mIsWifiCallingEnabledForRoaming = subscriptionInfoInternal.mIsWifiCallingEnabledForRoaming;
            this.mIsOpportunistic = subscriptionInfoInternal.mIsOpportunistic;
            this.mGroupUuid = subscriptionInfoInternal.mGroupUuid;
            this.mCountryIso = subscriptionInfoInternal.mCountryIso;
            this.mCarrierId = subscriptionInfoInternal.mCarrierId;
            this.mProfileClass = subscriptionInfoInternal.mProfileClass;
            this.mType = subscriptionInfoInternal.mType;
            this.mGroupOwner = subscriptionInfoInternal.mGroupOwner;
            this.mEnabledMobileDataPolicies = subscriptionInfoInternal.mEnabledMobileDataPolicies;
            this.mImsi = subscriptionInfoInternal.mImsi;
            this.mAreUiccApplicationsEnabled = subscriptionInfoInternal.mAreUiccApplicationsEnabled;
            this.mIsRcsUceEnabled = subscriptionInfoInternal.mIsRcsUceEnabled;
            this.mIsCrossSimCallingEnabled = subscriptionInfoInternal.mIsCrossSimCallingEnabled;
            this.mRcsConfig = subscriptionInfoInternal.mRcsConfig;
            this.mAllowedNetworkTypesForReasons = subscriptionInfoInternal.mAllowedNetworkTypesForReasons;
            this.mDeviceToDeviceStatusSharingPreference = subscriptionInfoInternal.mDeviceToDeviceStatusSharingPreference;
            this.mIsVoImsOptInEnabled = subscriptionInfoInternal.mIsVoImsOptInEnabled;
            this.mDeviceToDeviceStatusSharingContacts = subscriptionInfoInternal.mDeviceToDeviceStatusSharingContacts;
            this.mIsNrAdvancedCallingEnabled = subscriptionInfoInternal.mIsNrAdvancedCallingEnabled;
            this.mNumberFromCarrier = subscriptionInfoInternal.mNumberFromCarrier;
            this.mNumberFromIms = subscriptionInfoInternal.mNumberFromIms;
            this.mPortIndex = subscriptionInfoInternal.mPortIndex;
            this.mUsageSetting = subscriptionInfoInternal.mUsageSetting;
            this.mLastUsedTPMessageReference = subscriptionInfoInternal.getLastUsedTPMessageReference();
            this.mUserId = subscriptionInfoInternal.mUserId;
            this.mIsSatelliteEnabled = subscriptionInfoInternal.mIsSatelliteEnabled;
            this.mCardId = subscriptionInfoInternal.mCardId;
            this.mIsGroupDisabled = subscriptionInfoInternal.mIsGroupDisabled;
        }

        public Builder setId(int i) {
            this.mId = i;
            return this;
        }

        public Builder setIccId(String str) {
            Objects.requireNonNull(str);
            this.mIccId = str;
            return this;
        }

        public Builder setSimSlotIndex(int i) {
            this.mSimSlotIndex = i;
            return this;
        }

        public Builder setDisplayName(String str) {
            Objects.requireNonNull(str);
            this.mDisplayName = str;
            return this;
        }

        public Builder setCarrierName(String str) {
            Objects.requireNonNull(str);
            this.mCarrierName = str;
            return this;
        }

        public Builder setDisplayNameSource(int i) {
            this.mDisplayNameSource = i;
            return this;
        }

        public Builder setIconTint(int i) {
            this.mIconTint = i;
            return this;
        }

        public Builder setNumber(String str) {
            Objects.requireNonNull(str);
            this.mNumber = str;
            return this;
        }

        public Builder setDataRoaming(int i) {
            this.mDataRoaming = i;
            return this;
        }

        public Builder setMcc(String str) {
            Objects.requireNonNull(str);
            this.mMcc = str;
            return this;
        }

        public Builder setMnc(String str) {
            Objects.requireNonNull(str);
            this.mMnc = str;
            return this;
        }

        public Builder setEhplmns(String str) {
            Objects.requireNonNull(str);
            this.mEhplmns = str;
            return this;
        }

        public Builder setHplmns(String str) {
            Objects.requireNonNull(str);
            this.mHplmns = str;
            return this;
        }

        public Builder setEmbedded(int i) {
            this.mIsEmbedded = i;
            return this;
        }

        public Builder setCardString(String str) {
            Objects.requireNonNull(str);
            this.mCardString = str;
            return this;
        }

        public Builder setNativeAccessRules(byte[] bArr) {
            Objects.requireNonNull(bArr);
            this.mNativeAccessRules = bArr;
            return this;
        }

        public Builder setNativeAccessRules(List<UiccAccessRule> list) {
            Objects.requireNonNull(list);
            if (!list.isEmpty()) {
                this.mNativeAccessRules = UiccAccessRule.encodeRules((UiccAccessRule[]) list.toArray(new UiccAccessRule[0]));
            }
            return this;
        }

        public Builder setCarrierConfigAccessRules(byte[] bArr) {
            Objects.requireNonNull(bArr);
            this.mCarrierConfigAccessRules = bArr;
            return this;
        }

        public Builder setRemovableEmbedded(boolean z) {
            this.mIsRemovableEmbedded = z ? 1 : 0;
            return this;
        }

        public Builder setRemovableEmbedded(int i) {
            this.mIsRemovableEmbedded = i;
            return this;
        }

        public Builder setEnhanced4GModeEnabled(int i) {
            this.mIsEnhanced4GModeEnabled = i;
            return this;
        }

        public Builder setVideoTelephonyEnabled(int i) {
            this.mIsVideoTelephonyEnabled = i;
            return this;
        }

        public Builder setWifiCallingEnabled(int i) {
            this.mIsWifiCallingEnabled = i;
            return this;
        }

        public Builder setWifiCallingMode(int i) {
            this.mWifiCallingMode = i;
            return this;
        }

        public Builder setWifiCallingModeForRoaming(int i) {
            this.mWifiCallingModeForRoaming = i;
            return this;
        }

        public Builder setWifiCallingEnabledForRoaming(int i) {
            this.mIsWifiCallingEnabledForRoaming = i;
            return this;
        }

        public Builder setOpportunistic(int i) {
            this.mIsOpportunistic = i;
            return this;
        }

        public Builder setGroupUuid(String str) {
            Objects.requireNonNull(str);
            this.mGroupUuid = str;
            return this;
        }

        public Builder setCountryIso(String str) {
            Objects.requireNonNull(str);
            this.mCountryIso = str;
            return this;
        }

        public Builder setCarrierId(int i) {
            this.mCarrierId = i;
            return this;
        }

        public Builder setProfileClass(int i) {
            this.mProfileClass = i;
            return this;
        }

        public Builder setType(int i) {
            this.mType = i;
            return this;
        }

        public Builder setGroupOwner(String str) {
            Objects.requireNonNull(str);
            this.mGroupOwner = str;
            return this;
        }

        public Builder setEnabledMobileDataPolicies(String str) {
            Objects.requireNonNull(str);
            this.mEnabledMobileDataPolicies = str;
            return this;
        }

        public Builder setImsi(String str) {
            Objects.requireNonNull(str);
            this.mImsi = str;
            return this;
        }

        public Builder setUiccApplicationsEnabled(int i) {
            this.mAreUiccApplicationsEnabled = i;
            return this;
        }

        public Builder setRcsUceEnabled(int i) {
            this.mIsRcsUceEnabled = i;
            return this;
        }

        public Builder setCrossSimCallingEnabled(int i) {
            this.mIsCrossSimCallingEnabled = i;
            return this;
        }

        public Builder setRcsConfig(byte[] bArr) {
            Objects.requireNonNull(bArr);
            this.mRcsConfig = bArr;
            return this;
        }

        public Builder setAllowedNetworkTypesForReasons(String str) {
            Objects.requireNonNull(str);
            this.mAllowedNetworkTypesForReasons = str;
            return this;
        }

        public Builder setDeviceToDeviceStatusSharingPreference(int i) {
            this.mDeviceToDeviceStatusSharingPreference = i;
            return this;
        }

        public Builder setVoImsOptInEnabled(int i) {
            this.mIsVoImsOptInEnabled = i;
            return this;
        }

        public Builder setDeviceToDeviceStatusSharingContacts(String str) {
            Objects.requireNonNull(str);
            this.mDeviceToDeviceStatusSharingContacts = str;
            return this;
        }

        public Builder setNrAdvancedCallingEnabled(int i) {
            this.mIsNrAdvancedCallingEnabled = i;
            return this;
        }

        public Builder setNumberFromCarrier(String str) {
            Objects.requireNonNull(str);
            this.mNumberFromCarrier = str;
            return this;
        }

        public Builder setNumberFromIms(String str) {
            Objects.requireNonNull(str);
            this.mNumberFromIms = str;
            return this;
        }

        public Builder setPortIndex(int i) {
            this.mPortIndex = i;
            return this;
        }

        public Builder setUsageSetting(int i) {
            this.mUsageSetting = i;
            return this;
        }

        public Builder setLastUsedTPMessageReference(int i) {
            this.mLastUsedTPMessageReference = i;
            return this;
        }

        public Builder setUserId(int i) {
            this.mUserId = i;
            return this;
        }

        public Builder setSatelliteEnabled(int i) {
            this.mIsSatelliteEnabled = i;
            return this;
        }

        public Builder setCardId(int i) {
            this.mCardId = i;
            return this;
        }

        public Builder setGroupDisabled(boolean z) {
            this.mIsGroupDisabled = z;
            return this;
        }

        public SubscriptionInfoInternal build() {
            return new SubscriptionInfoInternal(this);
        }
    }
}
