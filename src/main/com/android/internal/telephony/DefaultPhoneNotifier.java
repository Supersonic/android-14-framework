package com.android.internal.telephony;

import android.content.Context;
import android.telephony.BarringInfo;
import android.telephony.CallQuality;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.LinkCapacityEstimate;
import android.telephony.PhoneCapability;
import android.telephony.PhysicalChannelConfig;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyRegistryManager;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import com.android.internal.telephony.Call;
import com.android.telephony.Rlog;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultPhoneNotifier implements PhoneNotifier {
    private TelephonyRegistryManager mTelephonyRegistryMgr;

    public DefaultPhoneNotifier(Context context) {
        this.mTelephonyRegistryMgr = (TelephonyRegistryManager) context.getSystemService("telephony_registry");
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyPhoneState(Phone phone) {
        Call ringingCall = phone.getRingingCall();
        this.mTelephonyRegistryMgr.notifyCallStateChanged(phone.getPhoneId(), phone.getSubId(), PhoneConstantConversions.convertCallState(phone.getState()), (ringingCall == null || ringingCall.getEarliestConnection() == null) ? PhoneConfigurationManager.SSSS : ringingCall.getEarliestConnection().getAddress());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyServiceState(Phone phone) {
        notifyServiceStateForSubId(phone, phone.getServiceState(), phone.getSubId());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyServiceStateForSubId(Phone phone, ServiceState serviceState, int i) {
        int phoneId = phone.getPhoneId();
        Rlog.d("DefaultPhoneNotifier", "notifyServiceStateForSubId: mRegistryMgr=" + this.mTelephonyRegistryMgr + " ss=" + serviceState + " sender=" + phone + " phondId=" + phoneId + " subId=" + i);
        if (serviceState == null) {
            serviceState = new ServiceState();
            serviceState.setStateOutOfService();
        }
        this.mTelephonyRegistryMgr.notifyServiceStateChanged(phoneId, i, serviceState);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifySignalStrength(Phone phone) {
        this.mTelephonyRegistryMgr.notifySignalStrengthChanged(phone.getPhoneId(), phone.getSubId(), phone.getSignalStrength());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyMessageWaitingChanged(Phone phone) {
        this.mTelephonyRegistryMgr.notifyMessageWaitingChanged(phone.getPhoneId(), phone.getSubId(), phone.getMessageWaitingIndicator());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCallForwardingChanged(Phone phone) {
        int subId = phone.getSubId();
        Rlog.d("DefaultPhoneNotifier", "notifyCallForwardingChanged: subId=" + subId + ", isCFActive=" + phone.getCallForwardingIndicator());
        this.mTelephonyRegistryMgr.notifyCallForwardingChanged(subId, phone.getCallForwardingIndicator());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataActivity(Phone phone) {
        this.mTelephonyRegistryMgr.notifyDataActivityChanged(phone.getSubId(), phone.getDataActivityState());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataConnection(Phone phone, PreciseDataConnectionState preciseDataConnectionState) {
        this.mTelephonyRegistryMgr.notifyDataConnectionForSubscriber(phone.getPhoneId(), phone.getSubId(), preciseDataConnectionState);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCellLocation(Phone phone, CellIdentity cellIdentity) {
        this.mTelephonyRegistryMgr.notifyCellLocation(phone.getSubId(), cellIdentity);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCellInfo(Phone phone, List<CellInfo> list) {
        this.mTelephonyRegistryMgr.notifyCellInfoChanged(phone.getSubId(), list);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyPreciseCallState(Phone phone, String[] strArr, int[] iArr, int[] iArr2) {
        Call ringingCall = phone.getRingingCall();
        Call foregroundCall = phone.getForegroundCall();
        Call backgroundCall = phone.getBackgroundCall();
        if (ringingCall == null || foregroundCall == null || backgroundCall == null) {
            return;
        }
        this.mTelephonyRegistryMgr.notifyPreciseCallState(phone.getPhoneId(), phone.getSubId(), new int[]{convertPreciseCallState(ringingCall.getState()), convertPreciseCallState(foregroundCall.getState()), convertPreciseCallState(backgroundCall.getState())}, strArr, iArr, iArr2);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDisconnectCause(Phone phone, int i, int i2) {
        this.mTelephonyRegistryMgr.notifyDisconnectCause(phone.getPhoneId(), phone.getSubId(), i, i2);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyImsDisconnectCause(Phone phone, ImsReasonInfo imsReasonInfo) {
        this.mTelephonyRegistryMgr.notifyImsDisconnectCause(phone.getSubId(), imsReasonInfo);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifySrvccStateChanged(Phone phone, int i) {
        this.mTelephonyRegistryMgr.notifySrvccStateChanged(phone.getSubId(), i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataActivationStateChanged(Phone phone, int i) {
        this.mTelephonyRegistryMgr.notifyDataActivationStateChanged(phone.getPhoneId(), phone.getSubId(), i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyVoiceActivationStateChanged(Phone phone, int i) {
        this.mTelephonyRegistryMgr.notifyVoiceActivationStateChanged(phone.getPhoneId(), phone.getSubId(), i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyUserMobileDataStateChanged(Phone phone, boolean z) {
        this.mTelephonyRegistryMgr.notifyUserMobileDataStateChanged(phone.getPhoneId(), phone.getSubId(), z);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDisplayInfoChanged(Phone phone, TelephonyDisplayInfo telephonyDisplayInfo) {
        this.mTelephonyRegistryMgr.notifyDisplayInfoChanged(phone.getPhoneId(), phone.getSubId(), telephonyDisplayInfo);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyPhoneCapabilityChanged(PhoneCapability phoneCapability) {
        this.mTelephonyRegistryMgr.notifyPhoneCapabilityChanged(phoneCapability);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyRadioPowerStateChanged(Phone phone, int i) {
        this.mTelephonyRegistryMgr.notifyRadioPowerStateChanged(phone.getPhoneId(), phone.getSubId(), i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyEmergencyNumberList(Phone phone) {
        this.mTelephonyRegistryMgr.notifyEmergencyNumberList(phone.getPhoneId(), phone.getSubId());
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyOutgoingEmergencySms(Phone phone, EmergencyNumber emergencyNumber) {
        this.mTelephonyRegistryMgr.notifyOutgoingEmergencySms(phone.getPhoneId(), phone.getSubId(), emergencyNumber);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCallQualityChanged(Phone phone, CallQuality callQuality, int i) {
        this.mTelephonyRegistryMgr.notifyCallQualityChanged(phone.getPhoneId(), phone.getSubId(), callQuality, i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyMediaQualityStatusChanged(Phone phone, MediaQualityStatus mediaQualityStatus) {
        this.mTelephonyRegistryMgr.notifyMediaQualityStatusChanged(phone.getPhoneId(), phone.getSubId(), mediaQualityStatus);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyRegistrationFailed(Phone phone, CellIdentity cellIdentity, String str, int i, int i2, int i3) {
        this.mTelephonyRegistryMgr.notifyRegistrationFailed(phone.getPhoneId(), phone.getSubId(), cellIdentity, str, i, i2, i3);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyBarringInfoChanged(Phone phone, BarringInfo barringInfo) {
        this.mTelephonyRegistryMgr.notifyBarringInfoChanged(phone.getPhoneId(), phone.getSubId(), barringInfo);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyPhysicalChannelConfig(Phone phone, List<PhysicalChannelConfig> list) {
        this.mTelephonyRegistryMgr.notifyPhysicalChannelConfigForSubscriber(phone.getPhoneId(), phone.getSubId(), list);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataEnabled(Phone phone, boolean z, int i) {
        this.mTelephonyRegistryMgr.notifyDataEnabled(phone.getPhoneId(), phone.getSubId(), z, i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyAllowedNetworkTypesChanged(Phone phone, int i, long j) {
        this.mTelephonyRegistryMgr.notifyAllowedNetworkTypesChanged(phone.getPhoneId(), phone.getSubId(), i, j);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyLinkCapacityEstimateChanged(Phone phone, List<LinkCapacityEstimate> list) {
        this.mTelephonyRegistryMgr.notifyLinkCapacityEstimateChanged(phone.getPhoneId(), phone.getSubId(), list);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCallbackModeStarted(Phone phone, int i) {
        this.mTelephonyRegistryMgr.notifyCallBackModeStarted(phone.getPhoneId(), phone.getSubId(), i);
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCallbackModeStopped(Phone phone, int i, int i2) {
        this.mTelephonyRegistryMgr.notifyCallbackModeStopped(phone.getPhoneId(), phone.getSubId(), i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.DefaultPhoneNotifier$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00221 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$Call$State;

        static {
            int[] iArr = new int[Call.State.values().length];
            $SwitchMap$com$android$internal$telephony$Call$State = iArr;
            try {
                iArr[Call.State.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.HOLDING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DIALING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.ALERTING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.INCOMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.WAITING.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DISCONNECTED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DISCONNECTING.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    public static int convertPreciseCallState(Call.State state) {
        switch (C00221.$SwitchMap$com$android$internal$telephony$Call$State[state.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            default:
                return 0;
        }
    }
}
