package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.telephony.BarringInfo;
import android.telephony.emergency.EmergencyNumber;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class BaseCommands implements CommandsInterface {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected int mAllowedNetworkTypesBitmask;
    @UnsupportedAppUsage
    protected Registrant mCatCallSetUpRegistrant;
    @UnsupportedAppUsage
    protected Registrant mCatCcAlphaRegistrant;
    @UnsupportedAppUsage
    protected Registrant mCatEventRegistrant;
    @UnsupportedAppUsage
    protected Registrant mCatProCmdRegistrant;
    @UnsupportedAppUsage
    protected Registrant mCatSessionEndRegistrant;
    @UnsupportedAppUsage
    protected Registrant mCdmaSmsRegistrant;
    protected int mCdmaSubscription;
    @UnsupportedAppUsage
    protected Context mContext;
    @UnsupportedAppUsage
    protected Registrant mEmergencyCallbackModeRegistrant;
    @UnsupportedAppUsage
    protected Registrant mGsmBroadcastSmsRegistrant;
    @UnsupportedAppUsage
    protected Registrant mGsmSmsRegistrant;
    @UnsupportedAppUsage
    protected Registrant mIccSmsFullRegistrant;
    @UnsupportedAppUsage
    protected Registrant mNITZTimeRegistrant;
    @UnsupportedAppUsage
    protected int mPhoneType;
    protected Registrant mRegistrationFailedRegistrant;
    @UnsupportedAppUsage
    protected Registrant mRestrictedStateRegistrant;
    @UnsupportedAppUsage
    protected Registrant mRingRegistrant;
    @UnsupportedAppUsage
    protected Registrant mSignalStrengthRegistrant;
    @UnsupportedAppUsage
    protected Registrant mSmsOnSimRegistrant;
    @UnsupportedAppUsage
    protected Registrant mSmsStatusRegistrant;
    @UnsupportedAppUsage
    protected Registrant mSsRegistrant;
    @UnsupportedAppUsage
    protected Registrant mSsnRegistrant;
    @UnsupportedAppUsage
    protected Registrant mUSSDRegistrant;
    @UnsupportedAppUsage
    protected Registrant mUnsolOemHookRawRegistrant;
    protected int mState = 2;
    @UnsupportedAppUsage
    protected Object mStateMonitor = new Object();
    protected RegistrantList mRadioStateChangedRegistrants = new RegistrantList();
    protected RegistrantList mOnRegistrants = new RegistrantList();
    protected RegistrantList mAvailRegistrants = new RegistrantList();
    protected RegistrantList mOffOrNotAvailRegistrants = new RegistrantList();
    protected RegistrantList mNotAvailRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mCallStateRegistrants = new RegistrantList();
    protected RegistrantList mNetworkStateRegistrants = new RegistrantList();
    protected RegistrantList mDataCallListChangedRegistrants = new RegistrantList();
    protected RegistrantList mApnUnthrottledRegistrants = new RegistrantList();
    protected RegistrantList mSlicingConfigChangedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mVoiceRadioTechChangedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mImsNetworkStateChangedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mIccStatusChangedRegistrants = new RegistrantList();
    protected RegistrantList mIccSlotStatusChangedRegistrants = new RegistrantList();
    protected RegistrantList mVoicePrivacyOnRegistrants = new RegistrantList();
    protected RegistrantList mVoicePrivacyOffRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mOtaProvisionRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mCallWaitingInfoRegistrants = new RegistrantList();
    protected RegistrantList mDisplayInfoRegistrants = new RegistrantList();
    protected RegistrantList mSignalInfoRegistrants = new RegistrantList();
    protected RegistrantList mNumberInfoRegistrants = new RegistrantList();
    protected RegistrantList mRedirNumInfoRegistrants = new RegistrantList();
    protected RegistrantList mLineControlInfoRegistrants = new RegistrantList();
    protected RegistrantList mT53ClirInfoRegistrants = new RegistrantList();
    protected RegistrantList mT53AudCntrlInfoRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mRingbackToneRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mResendIncallMuteRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mCdmaSubscriptionChangedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mCdmaPrlChangedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mExitEmergencyCallbackModeRegistrants = new RegistrantList();
    protected RegistrantList mRilConnectedRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mIccRefreshRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mRilCellInfoListRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mSubscriptionStatusRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mSrvccStateRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mHardwareConfigChangeRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    protected RegistrantList mPhoneRadioCapabilityChangedRegistrants = new RegistrantList();
    protected RegistrantList mPcoDataRegistrants = new RegistrantList();
    protected RegistrantList mCarrierInfoForImsiEncryptionRegistrants = new RegistrantList();
    protected RegistrantList mRilNetworkScanResultRegistrants = new RegistrantList();
    protected RegistrantList mModemResetRegistrants = new RegistrantList();
    protected RegistrantList mNattKeepaliveStatusRegistrants = new RegistrantList();
    protected RegistrantList mPhysicalChannelConfigurationRegistrants = new RegistrantList();
    protected RegistrantList mLceInfoRegistrants = new RegistrantList();
    protected RegistrantList mEmergencyNumberListRegistrants = new RegistrantList();
    protected RegistrantList mUiccApplicationsEnablementRegistrants = new RegistrantList();
    protected RegistrantList mBarringInfoChangedRegistrants = new RegistrantList();
    protected RegistrantList mSimPhonebookChangedRegistrants = new RegistrantList();
    protected RegistrantList mSimPhonebookRecordsReceivedRegistrants = new RegistrantList();
    protected RegistrantList mEmergencyNetworkScanRegistrants = new RegistrantList();
    protected RegistrantList mConnectionSetupFailureRegistrants = new RegistrantList();
    protected RegistrantList mNotifyAnbrRegistrants = new RegistrantList();
    protected RegistrantList mTriggerImsDeregistrationRegistrants = new RegistrantList();
    protected RegistrantList mPendingSatelliteMessageCountRegistrants = new RegistrantList();
    protected RegistrantList mNewSatelliteMessagesRegistrants = new RegistrantList();
    protected RegistrantList mSatelliteMessagesTransferCompleteRegistrants = new RegistrantList();
    protected RegistrantList mSatellitePointingInfoChangedRegistrants = new RegistrantList();
    protected RegistrantList mSatelliteModeChangedRegistrants = new RegistrantList();
    protected RegistrantList mSatelliteRadioTechnologyChangedRegistrants = new RegistrantList();
    protected RegistrantList mSatelliteProvisionStateChangedRegistrants = new RegistrantList();
    private Object mLastEmergencyNumberListIndicationLock = new Object();
    private final List<EmergencyNumber> mLastEmergencyNumberListIndication = new ArrayList();
    protected BarringInfo mLastBarringInfo = new BarringInfo();
    protected int mRilVersion = -1;

    @Override // com.android.internal.telephony.CommandsInterface
    public void getRadioCapability(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSimPhonebookCapacity(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSimPhonebookRecords(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void pullLceData(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void requestShutdown(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setDataAllowed(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setRadioCapability(RadioCapability radioCapability, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setUiccSubscription(int i, int i2, int i3, int i4, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startLceService(int i, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopLceService(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void testingEmergencyCall() {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void updateSimPhonebookRecord(SimPhonebookRecord simPhonebookRecord, Message message) {
    }

    public BaseCommands(Context context) {
        this.mContext = context;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public int getRadioState() {
        return this.mState;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRadioStateChanged(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mRadioStateChangedRegistrants.addUnique(handler, i, obj);
            Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRadioStateChanged(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mRadioStateChangedRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForImsNetworkStateChanged(Handler handler, int i, Object obj) {
        this.mImsNetworkStateChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForImsNetworkStateChanged(Handler handler) {
        this.mImsNetworkStateChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForOn(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mOnRegistrants.addUnique(handler, i, obj);
            if (this.mState == 1) {
                Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForOn(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mOnRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForAvailable(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mAvailRegistrants.addUnique(handler, i, obj);
            if (this.mState != 2) {
                Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForAvailable(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mAvailRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNotAvailable(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mNotAvailRegistrants.addUnique(handler, i, obj);
            if (this.mState == 2) {
                Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNotAvailable(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mNotAvailRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForOffOrNotAvailable(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mOffOrNotAvailRegistrants.addUnique(handler, i, obj);
            int i2 = this.mState;
            if (i2 == 0 || i2 == 2) {
                Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForOffOrNotAvailable(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mOffOrNotAvailRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCallStateChanged(Handler handler, int i, Object obj) {
        this.mCallStateRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCallStateChanged(Handler handler) {
        this.mCallStateRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNetworkStateChanged(Handler handler, int i, Object obj) {
        this.mNetworkStateRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNetworkStateChanged(Handler handler) {
        this.mNetworkStateRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForDataCallListChanged(Handler handler, int i, Object obj) {
        this.mDataCallListChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForDataCallListChanged(Handler handler) {
        this.mDataCallListChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForApnUnthrottled(Handler handler, int i, Object obj) {
        this.mApnUnthrottledRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForApnUnthrottled(Handler handler) {
        this.mApnUnthrottledRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSlicingConfigChanged(Handler handler, int i, Object obj) {
        this.mSlicingConfigChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSlicingConfigChanged(Handler handler) {
        this.mSlicingConfigChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForVoiceRadioTechChanged(Handler handler, int i, Object obj) {
        this.mVoiceRadioTechChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForVoiceRadioTechChanged(Handler handler) {
        this.mVoiceRadioTechChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForIccStatusChanged(Handler handler, int i, Object obj) {
        this.mIccStatusChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForIccStatusChanged(Handler handler) {
        this.mIccStatusChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForIccSlotStatusChanged(Handler handler, int i, Object obj) {
        this.mIccSlotStatusChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForIccSlotStatusChanged(Handler handler) {
        this.mIccSlotStatusChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewGsmSms(Handler handler, int i, Object obj) {
        this.mGsmSmsRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewGsmSms(Handler handler) {
        Registrant registrant = this.mGsmSmsRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mGsmSmsRegistrant.clear();
        this.mGsmSmsRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewCdmaSms(Handler handler, int i, Object obj) {
        this.mCdmaSmsRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewCdmaSms(Handler handler) {
        Registrant registrant = this.mCdmaSmsRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mCdmaSmsRegistrant.clear();
        this.mCdmaSmsRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewGsmBroadcastSms(Handler handler, int i, Object obj) {
        this.mGsmBroadcastSmsRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewGsmBroadcastSms(Handler handler) {
        Registrant registrant = this.mGsmBroadcastSmsRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mGsmBroadcastSmsRegistrant.clear();
        this.mGsmBroadcastSmsRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSmsOnSim(Handler handler, int i, Object obj) {
        this.mSmsOnSimRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSmsOnSim(Handler handler) {
        Registrant registrant = this.mSmsOnSimRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mSmsOnSimRegistrant.clear();
        this.mSmsOnSimRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSmsStatus(Handler handler, int i, Object obj) {
        this.mSmsStatusRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSmsStatus(Handler handler) {
        Registrant registrant = this.mSmsStatusRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mSmsStatusRegistrant.clear();
        this.mSmsStatusRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSignalStrengthUpdate(Handler handler, int i, Object obj) {
        this.mSignalStrengthRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSignalStrengthUpdate(Handler handler) {
        Registrant registrant = this.mSignalStrengthRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mSignalStrengthRegistrant.clear();
        this.mSignalStrengthRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNITZTime(Handler handler, int i, Object obj) {
        this.mNITZTimeRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNITZTime(Handler handler) {
        Registrant registrant = this.mNITZTimeRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mNITZTimeRegistrant.clear();
        this.mNITZTimeRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnUSSD(Handler handler, int i, Object obj) {
        this.mUSSDRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnUSSD(Handler handler) {
        Registrant registrant = this.mUSSDRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mUSSDRegistrant.clear();
        this.mUSSDRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSuppServiceNotification(Handler handler, int i, Object obj) {
        this.mSsnRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSuppServiceNotification(Handler handler) {
        Registrant registrant = this.mSsnRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mSsnRegistrant.clear();
        this.mSsnRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatSessionEnd(Handler handler, int i, Object obj) {
        this.mCatSessionEndRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatSessionEnd(Handler handler) {
        Registrant registrant = this.mCatSessionEndRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mCatSessionEndRegistrant.clear();
        this.mCatSessionEndRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatProactiveCmd(Handler handler, int i, Object obj) {
        this.mCatProCmdRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatProactiveCmd(Handler handler) {
        Registrant registrant = this.mCatProCmdRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mCatProCmdRegistrant.clear();
        this.mCatProCmdRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatEvent(Handler handler, int i, Object obj) {
        this.mCatEventRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatEvent(Handler handler) {
        Registrant registrant = this.mCatEventRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mCatEventRegistrant.clear();
        this.mCatEventRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatCallSetUp(Handler handler, int i, Object obj) {
        this.mCatCallSetUpRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatCallSetUp(Handler handler) {
        Registrant registrant = this.mCatCallSetUpRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mCatCallSetUpRegistrant.clear();
        this.mCatCallSetUpRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnIccSmsFull(Handler handler, int i, Object obj) {
        this.mIccSmsFullRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnIccSmsFull(Handler handler) {
        Registrant registrant = this.mIccSmsFullRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mIccSmsFullRegistrant.clear();
        this.mIccSmsFullRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForIccRefresh(Handler handler, int i, Object obj) {
        this.mIccRefreshRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnIccRefresh(Handler handler, int i, Object obj) {
        registerForIccRefresh(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setEmergencyCallbackMode(Handler handler, int i, Object obj) {
        this.mEmergencyCallbackModeRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForIccRefresh(Handler handler) {
        this.mIccRefreshRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unsetOnIccRefresh(Handler handler) {
        unregisterForIccRefresh(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCallRing(Handler handler, int i, Object obj) {
        this.mRingRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCallRing(Handler handler) {
        Registrant registrant = this.mRingRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mRingRegistrant.clear();
        this.mRingRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSs(Handler handler, int i, Object obj) {
        this.mSsRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSs(Handler handler) {
        this.mSsRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatCcAlphaNotify(Handler handler, int i, Object obj) {
        this.mCatCcAlphaRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatCcAlphaNotify(Handler handler) {
        this.mCatCcAlphaRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnRegistrationFailed(Handler handler, int i, Object obj) {
        this.mRegistrationFailedRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnRegistrationFailed(Handler handler) {
        this.mRegistrationFailedRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj) {
        this.mVoicePrivacyOnRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForInCallVoicePrivacyOn(Handler handler) {
        this.mVoicePrivacyOnRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj) {
        this.mVoicePrivacyOffRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForInCallVoicePrivacyOff(Handler handler) {
        this.mVoicePrivacyOffRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnRestrictedStateChanged(Handler handler, int i, Object obj) {
        this.mRestrictedStateRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnRestrictedStateChanged(Handler handler) {
        Registrant registrant = this.mRestrictedStateRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mRestrictedStateRegistrant.clear();
        this.mRestrictedStateRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForDisplayInfo(Handler handler, int i, Object obj) {
        this.mDisplayInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForDisplayInfo(Handler handler) {
        this.mDisplayInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCallWaitingInfo(Handler handler, int i, Object obj) {
        this.mCallWaitingInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCallWaitingInfo(Handler handler) {
        this.mCallWaitingInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSignalInfo(Handler handler, int i, Object obj) {
        this.mSignalInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnUnsolOemHookRaw(Handler handler, int i, Object obj) {
        this.mUnsolOemHookRawRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnUnsolOemHookRaw(Handler handler) {
        Registrant registrant = this.mUnsolOemHookRawRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mUnsolOemHookRawRegistrant.clear();
        this.mUnsolOemHookRawRegistrant = null;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSignalInfo(Handler handler) {
        this.mSignalInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaOtaProvision(Handler handler, int i, Object obj) {
        this.mOtaProvisionRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaOtaProvision(Handler handler) {
        this.mOtaProvisionRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNumberInfo(Handler handler, int i, Object obj) {
        this.mNumberInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNumberInfo(Handler handler) {
        this.mNumberInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRedirectedNumberInfo(Handler handler, int i, Object obj) {
        this.mRedirNumInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRedirectedNumberInfo(Handler handler) {
        this.mRedirNumInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForLineControlInfo(Handler handler, int i, Object obj) {
        this.mLineControlInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForLineControlInfo(Handler handler) {
        this.mLineControlInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerFoT53ClirlInfo(Handler handler, int i, Object obj) {
        this.mT53ClirInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForT53ClirInfo(Handler handler) {
        this.mT53ClirInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForT53AudioControlInfo(Handler handler, int i, Object obj) {
        this.mT53AudCntrlInfoRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForT53AudioControlInfo(Handler handler) {
        this.mT53AudCntrlInfoRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRingbackTone(Handler handler, int i, Object obj) {
        this.mRingbackToneRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRingbackTone(Handler handler) {
        this.mRingbackToneRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForResendIncallMute(Handler handler, int i, Object obj) {
        this.mResendIncallMuteRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForResendIncallMute(Handler handler) {
        this.mResendIncallMuteRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaSubscriptionChanged(Handler handler, int i, Object obj) {
        this.mCdmaSubscriptionChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaSubscriptionChanged(Handler handler) {
        this.mCdmaSubscriptionChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaPrlChanged(Handler handler, int i, Object obj) {
        this.mCdmaPrlChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaPrlChanged(Handler handler) {
        this.mCdmaPrlChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForExitEmergencyCallbackMode(Handler handler, int i, Object obj) {
        this.mExitEmergencyCallbackModeRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForExitEmergencyCallbackMode(Handler handler) {
        this.mExitEmergencyCallbackModeRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForHardwareConfigChanged(Handler handler, int i, Object obj) {
        this.mHardwareConfigChangeRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForHardwareConfigChanged(Handler handler) {
        this.mHardwareConfigChangeRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNetworkScanResult(Handler handler, int i, Object obj) {
        this.mRilNetworkScanResultRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNetworkScanResult(Handler handler) {
        this.mRilNetworkScanResultRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRilConnected(Handler handler, int i, Object obj) {
        this.mRilConnectedRegistrants.addUnique(handler, i, obj);
        if (this.mRilVersion != -1) {
            Message.obtain(handler, i, new AsyncResult(obj, new Integer(this.mRilVersion), (Throwable) null)).sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRilConnected(Handler handler) {
        this.mRilConnectedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSubscriptionStatusChanged(Handler handler, int i, Object obj) {
        this.mSubscriptionStatusRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSubscriptionStatusChanged(Handler handler) {
        this.mSubscriptionStatusRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForEmergencyNumberList(Handler handler, int i, Object obj) {
        this.mEmergencyNumberListRegistrants.addUnique(handler, i, obj);
        List<EmergencyNumber> lastEmergencyNumberListIndication = getLastEmergencyNumberListIndication();
        if (lastEmergencyNumberListIndication != null) {
            this.mEmergencyNumberListRegistrants.notifyRegistrants(new AsyncResult((Object) null, lastEmergencyNumberListIndication, (Throwable) null));
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForEmergencyNumberList(Handler handler) {
        this.mEmergencyNumberListRegistrants.remove(handler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRadioState(int i, boolean z) {
        synchronized (this.mStateMonitor) {
            int i2 = this.mState;
            this.mState = i;
            if (i2 != i || z) {
                this.mRadioStateChangedRegistrants.notifyRegistrants();
                if (this.mState != 2 && i2 == 2) {
                    this.mAvailRegistrants.notifyRegistrants();
                }
                if (this.mState == 2 && i2 != 2) {
                    this.mNotAvailRegistrants.notifyRegistrants();
                }
                if (this.mState == 1 && i2 != 1) {
                    this.mOnRegistrants.notifyRegistrants();
                }
                int i3 = this.mState;
                if ((i3 == 0 || i3 == 2) && i2 == 1) {
                    this.mOffOrNotAvailRegistrants.notifyRegistrants();
                    this.mLastBarringInfo = new BarringInfo();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cacheEmergencyNumberListIndication(List<EmergencyNumber> list) {
        synchronized (this.mLastEmergencyNumberListIndicationLock) {
            this.mLastEmergencyNumberListIndication.clear();
            this.mLastEmergencyNumberListIndication.addAll(list);
        }
    }

    private List<EmergencyNumber> getLastEmergencyNumberListIndication() {
        ArrayList arrayList;
        synchronized (this.mLastEmergencyNumberListIndicationLock) {
            arrayList = new ArrayList(this.mLastEmergencyNumberListIndication);
        }
        return arrayList;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public BarringInfo getLastBarringInfo() {
        return this.mLastBarringInfo;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCellInfoList(Handler handler, int i, Object obj) {
        this.mRilCellInfoListRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCellInfoList(Handler handler) {
        this.mRilCellInfoListRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForPhysicalChannelConfiguration(Handler handler, int i, Object obj) {
        this.mPhysicalChannelConfigurationRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForPhysicalChannelConfiguration(Handler handler) {
        this.mPhysicalChannelConfigurationRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSrvccStateChanged(Handler handler, int i, Object obj) {
        this.mSrvccStateRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSrvccStateChanged(Handler handler) {
        this.mSrvccStateRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public int getRilVersion() {
        return this.mRilVersion;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRadioCapabilityChanged(Handler handler, int i, Object obj) {
        this.mPhoneRadioCapabilityChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRadioCapabilityChanged(Handler handler) {
        this.mPhoneRadioCapabilityChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForLceInfo(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mLceInfoRegistrants.addUnique(handler, i, obj);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForLceInfo(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mLceInfoRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForModemReset(Handler handler, int i, Object obj) {
        this.mModemResetRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForModemReset(Handler handler) {
        this.mModemResetRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForPcoData(Handler handler, int i, Object obj) {
        this.mPcoDataRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForPcoData(Handler handler) {
        this.mPcoDataRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCarrierInfoForImsiEncryption(Handler handler, int i, Object obj) {
        this.mCarrierInfoForImsiEncryptionRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCarrierInfoForImsiEncryption(Handler handler) {
        this.mCarrierInfoForImsiEncryptionRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNattKeepaliveStatus(Handler handler, int i, Object obj) {
        synchronized (this.mStateMonitor) {
            this.mNattKeepaliveStatusRegistrants.addUnique(handler, i, obj);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNattKeepaliveStatus(Handler handler) {
        synchronized (this.mStateMonitor) {
            this.mNattKeepaliveStatusRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerUiccApplicationEnablementChanged(Handler handler, int i, Object obj) {
        this.mUiccApplicationsEnablementRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterUiccApplicationEnablementChanged(Handler handler) {
        this.mUiccApplicationsEnablementRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForBarringInfoChanged(Handler handler, int i, Object obj) {
        this.mBarringInfoChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForBarringInfoChanged(Handler handler) {
        this.mBarringInfoChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSimPhonebookChanged(Handler handler, int i, Object obj) {
        this.mSimPhonebookChangedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSimPhonebookChanged(Handler handler) {
        this.mSimPhonebookChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSimPhonebookRecordsReceived(Handler handler, int i, Object obj) {
        this.mSimPhonebookRecordsReceivedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSimPhonebookRecordsReceived(Handler handler) {
        this.mSimPhonebookRecordsReceivedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForEmergencyNetworkScan(Handler handler, int i, Object obj) {
        this.mEmergencyNetworkScanRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForEmergencyNetworkScan(Handler handler) {
        this.mEmergencyNetworkScanRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForConnectionSetupFailure(Handler handler, int i, Object obj) {
        this.mConnectionSetupFailureRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForConnectionSetupFailure(Handler handler) {
        this.mConnectionSetupFailureRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNotifyAnbr(Handler handler, int i, Object obj) {
        this.mNotifyAnbrRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNotifyAnbr(Handler handler) {
        this.mNotifyAnbrRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForTriggerImsDeregistration(Handler handler, int i, Object obj) {
        this.mTriggerImsDeregistrationRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForTriggerImsDeregistration(Handler handler) {
        this.mTriggerImsDeregistrationRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForPendingSatelliteMessageCount(Handler handler, int i, Object obj) {
        this.mPendingSatelliteMessageCountRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForPendingSatelliteMessageCount(Handler handler) {
        this.mPendingSatelliteMessageCountRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNewSatelliteMessages(Handler handler, int i, Object obj) {
        this.mNewSatelliteMessagesRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNewSatelliteMessages(Handler handler) {
        this.mNewSatelliteMessagesRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSatelliteMessagesTransferComplete(Handler handler, int i, Object obj) {
        this.mSatelliteMessagesTransferCompleteRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSatelliteMessagesTransferComplete(Handler handler) {
        this.mSatelliteMessagesTransferCompleteRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSatellitePointingInfoChanged(Handler handler, int i, Object obj) {
        this.mSatellitePointingInfoChangedRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSatellitePointingInfoChanged(Handler handler) {
        this.mSatellitePointingInfoChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSatelliteModeChanged(Handler handler, int i, Object obj) {
        this.mSatelliteModeChangedRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSatelliteModeChanged(Handler handler) {
        this.mSatelliteModeChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSatelliteRadioTechnologyChanged(Handler handler, int i, Object obj) {
        this.mSatelliteRadioTechnologyChangedRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSatelliteRadioTechnologyChanged(Handler handler) {
        this.mSatelliteRadioTechnologyChangedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSatelliteProvisionStateChanged(Handler handler, int i, Object obj) {
        this.mSatelliteProvisionStateChangedRegistrants.add(handler, i, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSatelliteProvisionStateChanged(Handler handler) {
        this.mSatelliteProvisionStateChangedRegistrants.remove(handler);
    }
}
