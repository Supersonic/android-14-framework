package com.android.internal.telephony.imsphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.telecom.Connection;
import android.telephony.AccessNetworkConstants;
import android.telephony.CallQuality;
import android.telephony.CarrierConfigManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.PhoneNumberUtils;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.UssdResponse;
import android.telephony.ims.ImsCallForwardInfo;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.ImsSsInfo;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.RegistrationManager;
import android.text.TextUtils;
import com.android.ims.ImsEcbmStateListener;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CallForwardInfo;
import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.CallTracker;
import com.android.internal.telephony.CarrierPrivilegesTracker;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccPhoneBookInterfaceManager;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.MmiCode;
import com.android.internal.telephony.OperatorInfo;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.PhoneNotifier;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.domainselection.DomainSelectionResolver;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper;
import com.android.internal.telephony.metrics.ImsStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.metrics.VoiceCallSessionStats;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ImsPhone extends ImsPhoneBase {
    @VisibleForTesting
    public static final int EVENT_SERVICE_STATE_CHANGED = 76;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    ImsPhoneCallTracker mCT;
    private Uri[] mCurrentSubscriberUris;
    Phone mDefaultPhone;
    private Registrant mEcmExitRespRegistrant;
    private Runnable mExitEcmRunnable;
    ImsExternalCallTracker mExternalCallTracker;
    private int mImsDeregistrationTech;
    private ImsEcbmStateListener mImsEcbmStateListener;
    private final ImsManagerFactory mImsManagerFactory;
    private ImsRegistrationCallbackHelper mImsMmTelRegistrationHelper;
    private SharedPreferences mImsPhoneSharedPreferences;
    private int mImsRegistrationCapabilities;
    private int mImsRegistrationState;
    private int mImsRegistrationSuggestedAction;
    private int mImsRegistrationTech;
    private ImsStats mImsStats;
    private boolean mIsInImsEcm;
    private String mLastDialString;
    private boolean mLastKnownRoamingState;
    private TelephonyMetrics mMetrics;
    private ImsRegistrationCallbackHelper.ImsRegistrationUpdate mMmTelRegistrationUpdate;
    private boolean mNotifiedRegisteredState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ArrayList<ImsPhoneMmiCode> mPendingMMIs;
    private final LocalLog mRegLocalLog;
    private BroadcastReceiver mResultReceiver;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ServiceState mSS;
    private final RegistrantList mSilentRedialRegistrants;
    private RegistrantList mSsnRegistrants;
    private PowerManager.WakeLock mWakeLock;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface ImsManagerFactory {
        ImsManager create(Context context, int i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int getActionFromCFAction(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    return i != 4 ? -1 : 4;
                }
                return 3;
            }
            return 1;
        }
        return 0;
    }

    private int getCFReasonFromCondition(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 4) {
                        return i != 5 ? 3 : 5;
                    }
                    return 4;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int getConditionFromCFReason(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return i != 5 ? -1 : 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isCfEnable(int i) {
        return i == 1 || i == 3;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isValidCommandInterfaceCFAction(int i) {
        return i == 0 || i == 1 || i == 3 || i == 4;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isValidCommandInterfaceCFReason(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void activateCellBroadcastSms(int i, Message message) {
        super.activateCellBroadcastSms(i, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ boolean disableDataConnectivity() {
        return super.disableDataConnectivity();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void disableLocationUpdates() {
        super.disableLocationUpdates();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ boolean enableDataConnectivity() {
        return super.enableDataConnectivity();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void enableLocationUpdates() {
        super.enableLocationUpdates();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void getAvailableNetworks(Message message) {
        super.getAvailableNetworks(message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void getCellBroadcastSmsConfig(Message message) {
        super.getCellBroadcastSmsConfig(message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ int getDataActivityState() {
        return super.getDataActivityState();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ boolean getDataRoamingEnabled() {
        return super.getDataRoamingEnabled();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getDeviceId() {
        return super.getDeviceId();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getDeviceSvn() {
        return super.getDeviceSvn();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getEsn() {
        return super.getEsn();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getGroupIdLevel1() {
        return super.getGroupIdLevel1();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getGroupIdLevel2() {
        return super.getGroupIdLevel2();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ IccCard getIccCard() {
        return super.getIccCard();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ IccFileHandler getIccFileHandler() {
        return super.getIccFileHandler();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager() {
        return super.getIccPhoneBookInterfaceManager();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ boolean getIccRecordsLoaded() {
        return super.getIccRecordsLoaded();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ String getIccSerialNumber() {
        return super.getIccSerialNumber();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getImei() {
        return super.getImei();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ int getImeiType() {
        return super.getImeiType();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getLine1AlphaTag() {
        return super.getLine1AlphaTag();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getMeid() {
        return super.getMeid();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ boolean getMessageWaitingIndicator() {
        return super.getMessageWaitingIndicator();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ int getPhoneType() {
        return super.getPhoneType();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ SignalStrength getSignalStrength() {
        return super.getSignalStrength();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getSubscriberId() {
        return super.getSubscriberId();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ int getTerminalBasedCallWaitingState(boolean z) {
        return super.getTerminalBasedCallWaitingState(z);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getVoiceMailAlphaTag() {
        return super.getVoiceMailAlphaTag();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ String getVoiceMailNumber() {
        return super.getVoiceMailNumber();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ boolean handlePinMmi(String str) {
        return super.handlePinMmi(str);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ boolean isDataAllowed() {
        return super.isDataAllowed();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ boolean isUserDataEnabled() {
        return super.isUserDataEnabled();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void migrateFrom(Phone phone) {
        super.migrateFrom(phone);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ boolean needsOtaServiceProvisioning() {
        return super.needsOtaServiceProvisioning();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void notifyCallForwardingIndicator() {
        super.notifyCallForwardingIndicator();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifyDisconnect(Connection connection) {
        super.notifyDisconnect(connection);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifyImsReason(ImsReasonInfo imsReasonInfo) {
        super.notifyImsReason(imsReasonInfo);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifyPhoneStateChanged() {
        super.notifyPhoneStateChanged();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifyPreciseCallStateChanged() {
        super.notifyPreciseCallStateChanged();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifyPreciseCallStateToNotifier() {
        super.notifyPreciseCallStateToNotifier();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void notifySuppServiceFailed(PhoneInternalInterface.SuppService suppService) {
        super.notifySuppServiceFailed(suppService);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void onCallQualityChanged(CallQuality callQuality, int i) {
        super.onCallQualityChanged(callQuality, i);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void onMediaQualityStatusChanged(MediaQualityStatus mediaQualityStatus) {
        super.onMediaQualityStatusChanged(mediaQualityStatus);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public /* bridge */ /* synthetic */ void onTtyModeReceived(int i) {
        super.onTtyModeReceived(i);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void registerForOnHoldTone(Handler handler, int i, Object obj) {
        super.registerForOnHoldTone(handler, i, obj);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void registerForRingbackTone(Handler handler, int i, Object obj) {
        super.registerForRingbackTone(handler, i, obj);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void registerForTtyModeReceived(Handler handler, int i, Object obj) {
        super.registerForTtyModeReceived(handler, i, obj);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void selectNetworkManually(OperatorInfo operatorInfo, boolean z, Message message) {
        super.selectNetworkManually(operatorInfo, z, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void setCellBroadcastSmsConfig(int[] iArr, Message message) {
        super.setCellBroadcastSmsConfig(iArr, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void setDataRoamingEnabled(boolean z) {
        super.setDataRoamingEnabled(z);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ boolean setLine1Number(String str, String str2, Message message) {
        return super.setLine1Number(str, str2, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void setNetworkSelectionModeAutomatic(Message message) {
        super.setNetworkSelectionModeAutomatic(message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void setRadioPower(boolean z) {
        super.setRadioPower(z);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void setTerminalBasedCallWaitingSupported(boolean z) {
        super.setTerminalBasedCallWaitingSupported(z);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void setVoiceMailNumber(String str, String str2, Message message) {
        super.setVoiceMailNumber(str, str2, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void startNetworkScan(NetworkScanRequest networkScanRequest, Message message) {
        super.startNetworkScan(networkScanRequest, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    @VisibleForTesting
    public /* bridge */ /* synthetic */ void startOnHoldTone(Connection connection) {
        super.startOnHoldTone(connection);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void startRingbackTone() {
        super.startRingbackTone();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void stopNetworkScan(Message message) {
        super.stopNetworkScan(message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void stopRingbackTone() {
        super.stopRingbackTone();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void unregisterForOnHoldTone(Handler handler) {
        super.unregisterForOnHoldTone(handler);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void unregisterForRingbackTone(Handler handler) {
        super.unregisterForRingbackTone(handler);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    public /* bridge */ /* synthetic */ void unregisterForTtyModeReceived(Handler handler) {
        super.unregisterForTtyModeReceived(handler);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public /* bridge */ /* synthetic */ void updateServiceLocation() {
        super.updateServiceLocation();
    }

    /* loaded from: classes.dex */
    public static class ImsDialArgs extends PhoneInternalInterface.DialArgs {
        public final boolean isWpsCall;
        public final int retryCallFailCause;
        public final int retryCallFailNetworkType;
        public final Connection.RttTextStream rttTextStream;

        /* loaded from: classes.dex */
        public static class Builder extends PhoneInternalInterface.DialArgs.Builder<Builder> {
            private Connection.RttTextStream mRttTextStream;
            private int mRetryCallFailCause = 0;
            private int mRetryCallFailNetworkType = 0;
            private boolean mIsWpsCall = false;

            public static Builder from(PhoneInternalInterface.DialArgs dialArgs) {
                if (dialArgs instanceof ImsDialArgs) {
                    ImsDialArgs imsDialArgs = (ImsDialArgs) dialArgs;
                    return new Builder().setUusInfo(dialArgs.uusInfo).setIsEmergency(dialArgs.isEmergency).setVideoState(dialArgs.videoState).setIntentExtras(dialArgs.intentExtras).setRttTextStream(imsDialArgs.rttTextStream).setClirMode(dialArgs.clirMode).setRetryCallFailCause(imsDialArgs.retryCallFailCause).setRetryCallFailNetworkType(imsDialArgs.retryCallFailNetworkType).setIsWpsCall(imsDialArgs.isWpsCall);
                }
                return new Builder().setUusInfo(dialArgs.uusInfo).setIsEmergency(dialArgs.isEmergency).setVideoState(dialArgs.videoState).setClirMode(dialArgs.clirMode).setIntentExtras(dialArgs.intentExtras);
            }

            public Builder setRttTextStream(Connection.RttTextStream rttTextStream) {
                this.mRttTextStream = rttTextStream;
                return this;
            }

            public Builder setRetryCallFailCause(int i) {
                this.mRetryCallFailCause = i;
                return this;
            }

            public Builder setRetryCallFailNetworkType(int i) {
                this.mRetryCallFailNetworkType = i;
                return this;
            }

            public Builder setIsWpsCall(boolean z) {
                this.mIsWpsCall = z;
                return this;
            }

            @Override // com.android.internal.telephony.PhoneInternalInterface.DialArgs.Builder
            public ImsDialArgs build() {
                return new ImsDialArgs(this);
            }
        }

        private ImsDialArgs(Builder builder) {
            super(builder);
            this.rttTextStream = builder.mRttTextStream;
            this.retryCallFailCause = builder.mRetryCallFailCause;
            this.retryCallFailNetworkType = builder.mRetryCallFailNetworkType;
            this.isWpsCall = builder.mIsWpsCall;
        }
    }

    protected void setCurrentSubscriberUris(Uri[] uriArr) {
        this.mCurrentSubscriberUris = uriArr;
    }

    @Override // com.android.internal.telephony.Phone
    public Uri[] getCurrentSubscriberUris() {
        return this.mCurrentSubscriberUris;
    }

    public void setCallComposerStatus(int i) {
        SharedPreferences.Editor edit = this.mImsPhoneSharedPreferences.edit();
        edit.putInt("userset_callcomposer_prefix" + getSubId(), i).commit();
    }

    public int getCallComposerStatus() {
        SharedPreferences sharedPreferences = this.mImsPhoneSharedPreferences;
        return sharedPreferences.getInt("userset_callcomposer_prefix" + getSubId(), 0);
    }

    @Override // com.android.internal.telephony.Phone
    public int getEmergencyNumberDbVersion() {
        return getEmergencyNumberTracker().getEmergencyNumberDbVersion();
    }

    @Override // com.android.internal.telephony.Phone
    public EmergencyNumberTracker getEmergencyNumberTracker() {
        return this.mDefaultPhone.getEmergencyNumberTracker();
    }

    @Override // com.android.internal.telephony.Phone
    public ServiceStateTracker getServiceStateTracker() {
        return this.mDefaultPhone.getServiceStateTracker();
    }

    @VisibleForTesting
    /* renamed from: com.android.internal.telephony.imsphone.ImsPhone$SS */
    /* loaded from: classes.dex */
    public static class C0240SS {
        int mCfAction;
        int mCfReason;
        int mClirMode;
        String mDialingNumber;
        boolean mEnable;
        String mFacility;
        boolean mLockState;
        @VisibleForTesting
        public Message mOnComplete;
        String mPassword;
        int mServiceClass;
        int mTimerSeconds;

        C0240SS(Message message) {
            this.mOnComplete = message;
        }

        C0240SS(int i, Message message) {
            this.mClirMode = i;
            this.mOnComplete = message;
        }

        C0240SS(boolean z, int i, Message message) {
            this.mEnable = z;
            this.mServiceClass = i;
            this.mOnComplete = message;
        }

        C0240SS(int i, int i2, Message message) {
            this.mCfReason = i;
            this.mServiceClass = i2;
            this.mOnComplete = message;
        }

        C0240SS(int i, int i2, String str, int i3, int i4, Message message) {
            this.mCfAction = i;
            this.mCfReason = i2;
            this.mDialingNumber = str;
            this.mServiceClass = i3;
            this.mTimerSeconds = i4;
            this.mOnComplete = message;
        }

        C0240SS(String str, String str2, int i, Message message) {
            this.mFacility = str;
            this.mPassword = str2;
            this.mServiceClass = i;
            this.mOnComplete = message;
        }

        C0240SS(String str, boolean z, String str2, int i, Message message) {
            this.mFacility = str;
            this.mLockState = z;
            this.mPassword = str2;
            this.mServiceClass = i;
            this.mOnComplete = message;
        }
    }

    public ImsPhone(Context context, PhoneNotifier phoneNotifier, Phone phone) {
        this(context, phoneNotifier, phone, new ImsManagerFactory() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda5
            @Override // com.android.internal.telephony.imsphone.ImsPhone.ImsManagerFactory
            public final ImsManager create(Context context2, int i) {
                return ImsManager.getInstance(context2, i);
            }
        }, false);
    }

    @VisibleForTesting
    public ImsPhone(Context context, PhoneNotifier phoneNotifier, Phone phone, ImsManagerFactory imsManagerFactory, boolean z) {
        super("ImsPhone", context, phoneNotifier, z);
        this.mPendingMMIs = new ArrayList<>();
        this.mSS = new ServiceState();
        this.mSilentRedialRegistrants = new RegistrantList();
        this.mRegLocalLog = new LocalLog(64);
        this.mLastKnownRoamingState = false;
        this.mIsInImsEcm = false;
        this.mSsnRegistrants = new RegistrantList();
        this.mImsRegistrationTech = -1;
        this.mImsDeregistrationTech = -1;
        this.mExitEcmRunnable = new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhone.1
            @Override // java.lang.Runnable
            public void run() {
                ImsPhone.this.exitEmergencyCallbackMode();
            }
        };
        this.mImsEcbmStateListener = new C02372(this.mContext.getMainExecutor());
        this.mResultReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.imsphone.ImsPhone.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (getResultCode() == -1) {
                    CharSequence charSequenceExtra = intent.getCharSequenceExtra(Phone.EXTRA_KEY_ALERT_TITLE);
                    CharSequence charSequenceExtra2 = intent.getCharSequenceExtra(Phone.EXTRA_KEY_ALERT_MESSAGE);
                    CharSequence charSequenceExtra3 = intent.getCharSequenceExtra(Phone.EXTRA_KEY_NOTIFICATION_MESSAGE);
                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.android.settings", "com.android.settings.Settings$WifiCallingSettingsActivity");
                    intent2.putExtra(Phone.EXTRA_KEY_ALERT_SHOW, true);
                    intent2.putExtra(Phone.EXTRA_KEY_ALERT_TITLE, charSequenceExtra);
                    intent2.putExtra(Phone.EXTRA_KEY_ALERT_MESSAGE, charSequenceExtra2);
                    ((NotificationManager) ((Phone) ImsPhone.this).mContext.getSystemService("notification")).notify("wifi_calling", 1, new Notification.Builder(((Phone) ImsPhone.this).mContext).setSmallIcon(17301642).setContentTitle(charSequenceExtra).setContentText(charSequenceExtra3).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(((Phone) ImsPhone.this).mContext, 0, intent2, 201326592)).setStyle(new Notification.BigTextStyle().bigText(charSequenceExtra3)).setChannelId(NotificationChannelController.CHANNEL_ID_WFC).build());
                }
            }
        };
        this.mMmTelRegistrationUpdate = new ImsRegistrationCallbackHelper.ImsRegistrationUpdate() { // from class: com.android.internal.telephony.imsphone.ImsPhone.4
            @Override // com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper.ImsRegistrationUpdate
            public void handleImsRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                int transportType = imsRegistrationAttributes.getTransportType();
                ImsPhone imsPhone = ImsPhone.this;
                imsPhone.logd("handleImsRegistered: onImsMmTelConnected imsRadioTech=" + AccessNetworkConstants.transportTypeToString(transportType));
                LocalLog localLog = ImsPhone.this.mRegLocalLog;
                localLog.log("handleImsRegistered: onImsMmTelConnected imsRadioTech=" + AccessNetworkConstants.transportTypeToString(transportType));
                ImsPhone.this.setServiceState(0);
                ImsPhone.this.getDefaultPhone().setImsRegistrationState(true);
                ImsPhone.this.mMetrics.writeOnImsConnectionState(((Phone) ImsPhone.this).mPhoneId, 1, null);
                ImsPhone.this.mImsStats.onImsRegistered(transportType);
                ImsPhone.this.updateImsRegistrationInfo(2, imsRegistrationAttributes.getRegistrationTechnology(), 0);
            }

            @Override // com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper.ImsRegistrationUpdate
            public void handleImsRegistering(int i) {
                ImsPhone imsPhone = ImsPhone.this;
                imsPhone.logd("handleImsRegistering: onImsMmTelProgressing imsRadioTech=" + AccessNetworkConstants.transportTypeToString(i));
                LocalLog localLog = ImsPhone.this.mRegLocalLog;
                localLog.log("handleImsRegistering: onImsMmTelProgressing imsRadioTech=" + AccessNetworkConstants.transportTypeToString(i));
                ImsPhone.this.setServiceState(1);
                ImsPhone.this.getDefaultPhone().setImsRegistrationState(false);
                ImsPhone.this.mMetrics.writeOnImsConnectionState(((Phone) ImsPhone.this).mPhoneId, 2, null);
                ImsPhone.this.mImsStats.onImsRegistering(i);
            }

            @Override // com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper.ImsRegistrationUpdate
            public void handleImsUnregistered(ImsReasonInfo imsReasonInfo, int i, int i2) {
                ImsPhone.this.logd("handleImsUnregistered: onImsMmTelDisconnected imsReasonInfo=" + imsReasonInfo + ", suggestedAction=" + i + ", disconnectedRadioTech=" + i2);
                LocalLog localLog = ImsPhone.this.mRegLocalLog;
                StringBuilder sb = new StringBuilder();
                sb.append("handleImsUnregistered: onImsMmTelDisconnected imsRadioTech=");
                sb.append(imsReasonInfo);
                localLog.log(sb.toString());
                ImsPhone.this.setServiceState(1);
                ImsPhone.this.processDisconnectReason(imsReasonInfo);
                ImsPhone.this.getDefaultPhone().setImsRegistrationState(false);
                ImsPhone.this.mMetrics.writeOnImsConnectionState(((Phone) ImsPhone.this).mPhoneId, 3, imsReasonInfo);
                ImsPhone.this.mImsStats.onImsUnregistered(imsReasonInfo);
                ImsPhone.this.mImsRegistrationTech = -1;
                if (imsReasonInfo.getCode() != 1000 || (i != 1 && i != 2)) {
                    i = 0;
                }
                ImsPhone.this.updateImsRegistrationInfo(0, i2, i);
            }

            @Override // com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper.ImsRegistrationUpdate
            public void handleImsSubscriberAssociatedUriChanged(Uri[] uriArr) {
                ImsPhone.this.logd("handleImsSubscriberAssociatedUriChanged");
                ImsPhone.this.setCurrentSubscriberUris(uriArr);
                ImsPhone.this.setPhoneNumberForSourceIms(uriArr);
            }
        };
        this.mDefaultPhone = phone;
        this.mImsManagerFactory = imsManagerFactory;
        this.mImsPhoneSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mImsStats = new ImsStats(this);
        this.mExternalCallTracker = TelephonyComponentFactory.getInstance().inject(ImsExternalCallTracker.class.getName()).makeImsExternalCallTracker(this);
        ImsPhoneCallTracker makeImsPhoneCallTracker = TelephonyComponentFactory.getInstance().inject(ImsPhoneCallTracker.class.getName()).makeImsPhoneCallTracker(this);
        this.mCT = makeImsPhoneCallTracker;
        makeImsPhoneCallTracker.registerPhoneStateListener(this.mExternalCallTracker);
        this.mExternalCallTracker.setCallPuller(this.mCT);
        this.mSS.setOutOfService(false);
        this.mPhoneId = this.mDefaultPhone.getPhoneId();
        this.mMetrics = TelephonyMetrics.getInstance();
        this.mImsMmTelRegistrationHelper = new ImsRegistrationCallbackHelper(this.mMmTelRegistrationUpdate, context.getMainExecutor());
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "ImsPhone");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        if (this.mDefaultPhone.getServiceStateTracker() != null && this.mDefaultPhone.getAccessNetworksManager() != null) {
            for (int i : this.mDefaultPhone.getAccessNetworksManager().getAvailableTransports()) {
                this.mDefaultPhone.getServiceStateTracker().registerForDataRegStateOrRatChanged(i, this, 75, null);
            }
        }
        setServiceState(1);
        this.mDefaultPhone.registerForServiceStateChanged(this, 76, null);
        this.mDefaultPhone.registerForVolteSilentRedial(this, 78, null);
    }

    @Override // com.android.internal.telephony.Phone
    public void dispose() {
        logd("dispose");
        this.mPendingMMIs.clear();
        this.mExternalCallTracker.tearDown();
        this.mCT.unregisterPhoneStateListener(this.mExternalCallTracker);
        this.mCT.unregisterForVoiceCallEnded(this);
        this.mCT.dispose();
        Phone phone = this.mDefaultPhone;
        if (phone != null && phone.getServiceStateTracker() != null) {
            for (int i : this.mDefaultPhone.getAccessNetworksManager().getAvailableTransports()) {
                this.mDefaultPhone.getServiceStateTracker().unregisterForDataRegStateOrRatChanged(i, this);
            }
            this.mDefaultPhone.unregisterForServiceStateChanged(this);
        }
        Phone phone2 = this.mDefaultPhone;
        if (phone2 != null) {
            phone2.unregisterForVolteSilentRedial(this);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ServiceState getServiceState() {
        return new ServiceState(this.mSS);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public void setServiceState(int i) {
        boolean z;
        synchronized (this) {
            z = this.mSS.getState() != i;
            this.mSS.setVoiceRegState(i);
        }
        updateDataServiceState();
        if (!z || this.mDefaultPhone.getServiceStateTracker() == null) {
            return;
        }
        this.mDefaultPhone.getServiceStateTracker().onImsServiceStateChanged();
    }

    @Override // com.android.internal.telephony.Phone
    public CallTracker getCallTracker() {
        return this.mCT;
    }

    public ImsExternalCallTracker getExternalCallTracker() {
        return this.mExternalCallTracker;
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public List<? extends ImsPhoneMmiCode> getPendingMmiCodes() {
        return this.mPendingMMIs;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void acceptCall(int i) throws CallStateException {
        this.mCT.acceptCall(i);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void rejectCall() throws CallStateException {
        this.mCT.rejectCall();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void switchHoldingAndActive() throws CallStateException {
        throw new UnsupportedOperationException("Use hold() and unhold() instead.");
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean canConference() {
        return this.mCT.canConference();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase
    public boolean canDial() {
        try {
            this.mCT.checkForDialIssues();
            return true;
        } catch (CallStateException unused) {
            return false;
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void conference() {
        this.mCT.conference();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void clearDisconnected() {
        this.mCT.clearDisconnected();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean canTransfer() {
        return this.mCT.canTransfer();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void explicitCallTransfer() throws CallStateException {
        this.mCT.explicitCallTransfer();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall getForegroundCall() {
        return this.mCT.mForegroundCall;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall getBackgroundCall() {
        return this.mCT.mBackgroundCall;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall getRingingCall() {
        return this.mCT.mRingingCall;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isImsAvailable() {
        return this.mCT.isImsServiceReady();
    }

    @Override // com.android.internal.telephony.Phone
    public CarrierPrivilegesTracker getCarrierPrivilegesTracker() {
        return this.mDefaultPhone.getCarrierPrivilegesTracker();
    }

    public void holdActiveCall() throws CallStateException {
        this.mCT.holdActiveCall();
    }

    public void unholdHeldCall() throws CallStateException {
        this.mCT.unholdHeldCall();
    }

    private boolean handleCallDeflectionIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        Call.State state = getRingingCall().getState();
        Call.State state2 = Call.State.IDLE;
        if (state != state2) {
            logd("MmiCode 0: rejectCall");
            try {
                this.mCT.rejectCall();
            } catch (CallStateException e) {
                Rlog.d("ImsPhone", "reject failed", e);
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.REJECT);
            }
        } else if (getBackgroundCall().getState() != state2) {
            logd("MmiCode 0: hangupWaitingOrBackground");
            try {
                this.mCT.hangup(getBackgroundCall());
            } catch (CallStateException e2) {
                Rlog.d("ImsPhone", "hangup failed", e2);
            }
        }
        return true;
    }

    private void sendUssdResponse(String str, CharSequence charSequence, int i, ResultReceiver resultReceiver) {
        UssdResponse ussdResponse = new UssdResponse(str, charSequence);
        Bundle bundle = new Bundle();
        bundle.putParcelable("USSD_RESPONSE", ussdResponse);
        resultReceiver.send(i, bundle);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean handleUssdRequest(String str, ResultReceiver resultReceiver) throws CallStateException {
        if (this.mPendingMMIs.size() > 0) {
            logi("handleUssdRequest: queue full: " + Rlog.pii("ImsPhone", str));
            sendUssdResponse(str, null, -1, resultReceiver);
            return true;
        }
        try {
            dialInternal(str, new ImsDialArgs.Builder().build(), resultReceiver);
        } catch (CallStateException e) {
            if (Phone.CS_FALLBACK.equals(e.getMessage())) {
                throw e;
            }
            Rlog.w("ImsPhone", "Could not execute USSD " + e);
            sendUssdResponse(str, null, -1, resultReceiver);
        } catch (Exception e2) {
            Rlog.w("ImsPhone", "Could not execute USSD " + e2);
            sendUssdResponse(str, null, -1, resultReceiver);
            return false;
        }
        return true;
    }

    private boolean handleCallWaitingIncallSupplementaryService(String str) {
        int length = str.length();
        if (length > 2) {
            return false;
        }
        ImsPhoneCall foregroundCall = getForegroundCall();
        try {
            if (length > 1) {
                logd("not support 1X SEND");
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.HANGUP);
            } else if (foregroundCall.getState() != Call.State.IDLE) {
                logd("MmiCode 1: hangup foreground");
                this.mCT.hangup(foregroundCall);
            } else {
                logd("MmiCode 1: holdActiveCallForWaitingCall");
                this.mCT.holdActiveCallForWaitingCall();
            }
        } catch (CallStateException e) {
            Rlog.d("ImsPhone", "hangup failed", e);
            notifySuppServiceFailed(PhoneInternalInterface.SuppService.HANGUP);
        }
        return true;
    }

    private boolean handleCallHoldIncallSupplementaryService(String str) {
        int length = str.length();
        if (length > 2) {
            return false;
        }
        if (length > 1) {
            logd("separate not supported");
            notifySuppServiceFailed(PhoneInternalInterface.SuppService.SEPARATE);
        } else {
            try {
                Call.State state = getRingingCall().getState();
                Call.State state2 = Call.State.IDLE;
                if (state != state2) {
                    logd("MmiCode 2: accept ringing call");
                    this.mCT.acceptCall(2);
                } else if (getBackgroundCall().getState() == Call.State.HOLDING) {
                    if (getForegroundCall().getState() != state2) {
                        logd("MmiCode 2: switch holding and active");
                        this.mCT.holdActiveCall();
                    } else {
                        logd("MmiCode 2: unhold held call");
                        this.mCT.unholdHeldCall();
                    }
                } else if (getForegroundCall().getState() != state2) {
                    logd("MmiCode 2: hold active call");
                    this.mCT.holdActiveCall();
                }
            } catch (CallStateException e) {
                Rlog.d("ImsPhone", "switch failed", e);
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.SWITCH);
            }
        }
        return true;
    }

    private boolean handleMultipartyIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        logd("MmiCode 3: merge calls");
        conference();
        return true;
    }

    private boolean handleEctIncallSupplementaryService(String str) {
        if (str.length() != 1) {
            return false;
        }
        logd("MmiCode 4: explicit call transfer");
        try {
            explicitCallTransfer();
        } catch (CallStateException e) {
            Rlog.d("ImsPhone", "explicit call transfer failed", e);
            notifySuppServiceFailed(PhoneInternalInterface.SuppService.TRANSFER);
        }
        return true;
    }

    private boolean handleCcbsIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        logi("MmiCode 5: CCBS not supported!");
        notifySuppServiceFailed(PhoneInternalInterface.SuppService.UNKNOWN);
        return true;
    }

    public void notifySuppSvcNotification(SuppServiceNotification suppServiceNotification) {
        logd("notifySuppSvcNotification: suppSvc = " + suppServiceNotification);
        this.mSsnRegistrants.notifyRegistrants(new AsyncResult((Object) null, suppServiceNotification, (Throwable) null));
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean handleInCallMmiCommands(String str) {
        if (isInCall() && !TextUtils.isEmpty(str)) {
            switch (str.charAt(0)) {
                case '0':
                    return handleCallDeflectionIncallSupplementaryService(str);
                case '1':
                    return handleCallWaitingIncallSupplementaryService(str);
                case '2':
                    return handleCallHoldIncallSupplementaryService(str);
                case '3':
                    return handleMultipartyIncallSupplementaryService(str);
                case '4':
                    return handleEctIncallSupplementaryService(str);
                case '5':
                    return handleCcbsIncallSupplementaryService(str);
                default:
                    return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInCall() {
        return getForegroundCall().getState().isAlive() || getBackgroundCall().getState().isAlive() || getRingingCall().getState().isAlive();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isInImsEcm() {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            return EmergencyStateTracker.getInstance().isInImsEcm();
        }
        return this.mIsInImsEcm;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isInEcm() {
        return this.mDefaultPhone.isInEcm();
    }

    @Override // com.android.internal.telephony.Phone
    public void setIsInEcm(boolean z) {
        this.mIsInImsEcm = z;
        this.mDefaultPhone.setIsInEcm(z);
    }

    public void notifyNewRingingConnection(com.android.internal.telephony.Connection connection) {
        this.mDefaultPhone.notifyNewRingingConnectionP(connection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void notifyUnknownConnection(com.android.internal.telephony.Connection connection) {
        this.mDefaultPhone.notifyUnknownConnectionP(connection);
    }

    @Override // com.android.internal.telephony.Phone
    public void notifyForVideoCapabilityChanged(boolean z) {
        this.mIsVideoCapable = z;
        this.mDefaultPhone.notifyForVideoCapabilityChanged(z);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setRadioPower(boolean z, boolean z2, boolean z3, boolean z4) {
        this.mDefaultPhone.setRadioPower(z, z2, z3, z4);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public com.android.internal.telephony.Connection startConference(String[] strArr, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        return this.mCT.startConference(strArr, ImsDialArgs.Builder.from(dialArgs).build());
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public com.android.internal.telephony.Connection dial(String str, PhoneInternalInterface.DialArgs dialArgs, Consumer<Phone> consumer) throws CallStateException {
        consumer.accept(this);
        return dialInternal(str, dialArgs, null);
    }

    private com.android.internal.telephony.Connection dialInternal(String str, PhoneInternalInterface.DialArgs dialArgs, ResultReceiver resultReceiver) throws CallStateException {
        this.mLastDialString = str;
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        if (handleInCallMmiCommands(stripSeparators)) {
            return null;
        }
        ImsDialArgs.Builder from = ImsDialArgs.Builder.from(dialArgs);
        from.setClirMode(this.mCT.getClirMode());
        if (this.mDefaultPhone.getPhoneType() == 2) {
            return this.mCT.dial(str, from.build());
        }
        ImsPhoneMmiCode newFromDialString = ImsPhoneMmiCode.newFromDialString(PhoneNumberUtils.extractNetworkPortionAlt(stripSeparators), this, resultReceiver);
        logd("dialInternal: dialing w/ mmi '" + newFromDialString + "'...");
        if (newFromDialString == null) {
            return this.mCT.dial(str, from.build());
        }
        if (newFromDialString.isTemporaryModeCLIR()) {
            from.setClirMode(newFromDialString.getCLIRMode());
            return this.mCT.dial(newFromDialString.getDialingNumber(), from.build());
        } else if (!newFromDialString.isSupportedOverImsPhone()) {
            logi("dialInternal: USSD not supported by IMS; fallback to CS.");
            throw new CallStateException(Phone.CS_FALLBACK);
        } else {
            this.mPendingMMIs.add(newFromDialString);
            this.mMmiRegistrants.notifyRegistrants(new AsyncResult((Object) null, newFromDialString, (Throwable) null));
            try {
                newFromDialString.processCode();
            } catch (CallStateException e) {
                if (Phone.CS_FALLBACK.equals(e.getMessage())) {
                    logi("dialInternal: fallback to GSM required.");
                    this.mPendingMMIs.remove(newFromDialString);
                    throw e;
                }
            }
            return null;
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void sendDtmf(char c) {
        if (!PhoneNumberUtils.is12Key(c)) {
            loge("sendDtmf called with invalid character '" + c + "'");
        } else if (this.mCT.getState() == PhoneConstants.State.OFFHOOK) {
            this.mCT.sendDtmf(c, null);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void startDtmf(char c) {
        if (!PhoneNumberUtils.is12Key(c) && (c < 'A' || c > 'D')) {
            loge("startDtmf called with invalid character '" + c + "'");
            return;
        }
        this.mCT.startDtmf(c);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void stopDtmf() {
        this.mCT.stopDtmf();
    }

    public void notifyIncomingRing() {
        logd("notifyIncomingRing");
        sendMessage(obtainMessage(14, new AsyncResult((Object) null, (Object) null, (Throwable) null)));
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setMute(boolean z) {
        this.mCT.setMute(z);
    }

    @Override // com.android.internal.telephony.Phone
    public void setTTYMode(int i, Message message) {
        this.mCT.setTtyMode(i);
    }

    @Override // com.android.internal.telephony.Phone
    public void setUiTTYMode(int i, Message message) {
        this.mCT.setUiTTYMode(i, message);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean getMute() {
        return this.mCT.getMute();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public PhoneConstants.State getState() {
        return this.mCT.getState();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void getOutgoingCallerIdDisplay(Message message) {
        logd("getCLIR");
        try {
            this.mCT.getUtInterface().queryCLIR(obtainMessage(74, new C0240SS(message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void setOutgoingCallerIdDisplay(int i, Message message) {
        logd("setCLIR action= " + i);
        try {
            this.mCT.getUtInterface().updateCLIR(i, obtainMessage(73, new C0240SS(i, message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void queryCLIP(Message message) {
        Message obtainMessage = obtainMessage(79, new C0240SS(message));
        try {
            Rlog.d("ImsPhone", "ut.queryCLIP");
            this.mCT.getUtInterface().queryCLIP(obtainMessage);
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void getCallForwardingOption(int i, Message message) {
        getCallForwardingOption(i, 1, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void getCallForwardingOption(int i, int i2, Message message) {
        logd("getCallForwardingOption reason=" + i);
        if (!isValidCommandInterfaceCFReason(i)) {
            if (message != null) {
                sendErrorResponse(message);
                return;
            }
            return;
        }
        logd("requesting call forwarding query.");
        try {
            this.mCT.getUtInterface().queryCallForward(getConditionFromCFReason(i), (String) null, obtainMessage(13, new C0240SS(i, i2, message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void setCallForwardingOption(int i, int i2, String str, int i3, Message message) {
        setCallForwardingOption(i, i2, str, 1, i3, message);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setCallForwardingOption(int i, int i2, String str, int i3, int i4, Message message) {
        logd("setCallForwardingOption action=" + i + ", reason=" + i2 + " serviceClass=" + i3);
        if (!isValidCommandInterfaceCFAction(i) || !isValidCommandInterfaceCFReason(i2)) {
            if (message != null) {
                sendErrorResponse(message);
                return;
            }
            return;
        }
        try {
            this.mCT.getUtInterface().updateCallForward(getActionFromCFAction(i), getConditionFromCFReason(i2), str, i3, i4, obtainMessage(12, new C0240SS(i, i2, str, i3, i4, message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void getCallWaiting(Message message) {
        logd("getCallWaiting");
        try {
            this.mCT.getUtInterface().queryCallWaiting(obtainMessage(72, new C0240SS(message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setCallWaiting(boolean z, Message message) {
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId());
        setCallWaiting(z, configForSubId != null ? configForSubId.getInt("call_waiting_service_class_int", 1) : 1, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void setCallWaiting(boolean z, int i, Message message) {
        logd("setCallWaiting enable=" + z);
        try {
            this.mCT.getUtInterface().updateCallWaiting(z, i, obtainMessage(71, new C0240SS(z, i, message)));
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    private int getCBTypeFromFacility(String str) {
        if (CommandsInterface.CB_FACILITY_BAOC.equals(str)) {
            return 2;
        }
        if (CommandsInterface.CB_FACILITY_BAOIC.equals(str)) {
            return 3;
        }
        if (CommandsInterface.CB_FACILITY_BAOICxH.equals(str)) {
            return 4;
        }
        if (CommandsInterface.CB_FACILITY_BAIC.equals(str)) {
            return 1;
        }
        if (CommandsInterface.CB_FACILITY_BAICr.equals(str)) {
            return 5;
        }
        if (CommandsInterface.CB_FACILITY_BA_ALL.equals(str)) {
            return 7;
        }
        if (CommandsInterface.CB_FACILITY_BA_MO.equals(str)) {
            return 8;
        }
        if (CommandsInterface.CB_FACILITY_BA_MT.equals(str)) {
            return 9;
        }
        return CommandsInterface.CB_FACILITY_BIC_ACR.equals(str) ? 6 : 0;
    }

    public void getCallBarring(String str, Message message) {
        getCallBarring(str, message, 1);
    }

    public void getCallBarring(String str, Message message, int i) {
        getCallBarring(str, PhoneConfigurationManager.SSSS, message, i);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void getCallBarring(String str, String str2, Message message, int i) {
        logd("getCallBarring facility=" + str + ", serviceClass = " + i);
        try {
            this.mCT.getUtInterface().queryCallBarring(getCBTypeFromFacility(str), obtainMessage(70, new C0240SS(str, str2, i, message)), i);
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    public void setCallBarring(String str, boolean z, String str2, Message message) {
        setCallBarring(str, z, str2, message, 1);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void setCallBarring(String str, boolean z, String str2, Message message, int i) {
        logd("setCallBarring facility=" + str + ", lockState=" + z + ", serviceClass = " + i);
        try {
            this.mCT.getUtInterface().updateCallBarring(getCBTypeFromFacility(str), z ? 1 : 0, obtainMessage(69, new C0240SS(str, z, str2, i, message)), (String[]) null, i, str2);
        } catch (ImsException e) {
            sendErrorResponse(message, e);
        }
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void sendUssdResponse(String str) {
        logd("sendUssdResponse");
        ImsPhoneMmiCode newFromUssdUserInput = ImsPhoneMmiCode.newFromUssdUserInput(str, this);
        this.mPendingMMIs.add(newFromUssdUserInput);
        this.mMmiRegistrants.notifyRegistrants(new AsyncResult((Object) null, newFromUssdUserInput, (Throwable) null));
        newFromUssdUserInput.sendUssd(str);
    }

    public void sendUSSD(String str, Message message) {
        Rlog.d("ImsPhone", "sendUssd ussdString = " + str);
        this.mLastDialString = str;
        this.mCT.sendUSSD(str, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void cancelUSSD(Message message) {
        this.mCT.cancelUSSD(message);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendErrorResponse(Message message) {
        logd("sendErrorResponse");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.GENERIC_FAILURE));
            message.sendToTarget();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public void sendErrorResponse(Message message, Throwable th) {
        logd("sendErrorResponse");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, getCommandException(th));
            message.sendToTarget();
        }
    }

    private CommandException getCommandException(int i, String str) {
        logd("getCommandException code= " + i + ", errorString= " + str);
        CommandException.Error error = CommandException.Error.GENERIC_FAILURE;
        if (i != 241) {
            switch (i) {
                case 801:
                case 803:
                    error = CommandException.Error.REQUEST_NOT_SUPPORTED;
                    break;
                case 802:
                    error = CommandException.Error.RADIO_NOT_AVAILABLE;
                    break;
                default:
                    switch (i) {
                        case 821:
                            error = CommandException.Error.PASSWORD_INCORRECT;
                            break;
                        case 822:
                            error = CommandException.Error.SS_MODIFIED_TO_DIAL;
                            break;
                        case 823:
                            error = CommandException.Error.SS_MODIFIED_TO_USSD;
                            break;
                        case 824:
                            error = CommandException.Error.SS_MODIFIED_TO_SS;
                            break;
                        case 825:
                            error = CommandException.Error.SS_MODIFIED_TO_DIAL_VIDEO;
                            break;
                    }
            }
        } else {
            error = CommandException.Error.FDN_CHECK_FAILURE;
        }
        return new CommandException(error, str);
    }

    private CommandException getCommandException(Throwable th) {
        if (th instanceof ImsException) {
            return getCommandException(((ImsException) th).getCode(), th.getMessage());
        }
        logd("getCommandException generic failure");
        return new CommandException(CommandException.Error.GENERIC_FAILURE);
    }

    private void onNetworkInitiatedUssd(ImsPhoneMmiCode imsPhoneMmiCode) {
        logd("onNetworkInitiatedUssd");
        this.mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult((Object) null, imsPhoneMmiCode, (Throwable) null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onIncomingUSSD(int i, String str) {
        ImsPhoneMmiCode imsPhoneMmiCode;
        logd("onIncomingUSSD ussdMode=" + i);
        int i2 = 0;
        boolean z = i == 1;
        boolean z2 = (i == 0 || i == 1) ? false : true;
        int size = this.mPendingMMIs.size();
        while (true) {
            if (i2 >= size) {
                imsPhoneMmiCode = null;
                break;
            } else if (this.mPendingMMIs.get(i2).isPendingUSSD()) {
                imsPhoneMmiCode = this.mPendingMMIs.get(i2);
                break;
            } else {
                i2++;
            }
        }
        if (imsPhoneMmiCode != null) {
            if (z2) {
                imsPhoneMmiCode.onUssdFinishedError();
            } else {
                imsPhoneMmiCode.onUssdFinished(str, z);
            }
        } else if (!z2 && !TextUtils.isEmpty(str)) {
            onNetworkInitiatedUssd(ImsPhoneMmiCode.newNetworkInitiatedUssd(str, z, this));
        } else if (z2) {
            ImsPhoneMmiCode.newNetworkInitiatedUssd(str, true, this).onUssdFinishedError();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void onMMIDone(ImsPhoneMmiCode imsPhoneMmiCode) {
        logd("onMMIDone: mmi=" + imsPhoneMmiCode);
        if (this.mPendingMMIs.remove(imsPhoneMmiCode) || imsPhoneMmiCode.isUssdRequest() || imsPhoneMmiCode.isSsInfo()) {
            ResultReceiver ussdCallbackReceiver = imsPhoneMmiCode.getUssdCallbackReceiver();
            if (ussdCallbackReceiver != null) {
                sendUssdResponse(imsPhoneMmiCode.getDialString(), imsPhoneMmiCode.getMessage(), imsPhoneMmiCode.getState() == MmiCode.State.COMPLETE ? 100 : -1, ussdCallbackReceiver);
                return;
            }
            logv("onMMIDone: notifyRegistrants");
            this.mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult((Object) null, imsPhoneMmiCode, (Throwable) null));
        }
    }

    @Override // com.android.internal.telephony.Phone
    public ArrayList<com.android.internal.telephony.Connection> getHandoverConnection() {
        ArrayList<com.android.internal.telephony.Connection> arrayList = new ArrayList<>();
        arrayList.addAll(getForegroundCall().getConnections());
        arrayList.addAll(getBackgroundCall().getConnections());
        arrayList.addAll(getRingingCall().getConnections());
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public void notifySrvccState(int i) {
        this.mCT.notifySrvccState(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initiateSilentRedial() {
        initiateSilentRedial(false, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initiateSilentRedial(final boolean z, final int i) {
        final AsyncResult asyncResult = new AsyncResult((Object) null, new Phone.SilentRedialParam(this.mLastDialString, CallFailCause.LOCAL_CALL_CS_RETRY_REQUIRED, new PhoneInternalInterface.DialArgs.Builder().setIsEmergency(z).setEccCategory(i).build()), (Throwable) null);
        this.mContext.getMainExecutor().execute(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ImsPhone.this.lambda$initiateSilentRedial$0(z, i, asyncResult);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateSilentRedial$0(boolean z, int i, AsyncResult asyncResult) {
        logd("initiateSilentRedial: notifying registrants, isEmergency=" + z + ", eccCategory=" + i);
        this.mSilentRedialRegistrants.notifyRegistrants(asyncResult);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSilentRedial(Handler handler, int i, Object obj) {
        this.mSilentRedialRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSilentRedial(Handler handler) {
        this.mSilentRedialRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void registerForSuppServiceNotification(Handler handler, int i, Object obj) {
        this.mSsnRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneBase, com.android.internal.telephony.PhoneInternalInterface
    public void unregisterForSuppServiceNotification(Handler handler) {
        this.mSsnRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public int getSubId() {
        return this.mDefaultPhone.getSubId();
    }

    @Override // com.android.internal.telephony.Phone
    public int getPhoneId() {
        return this.mDefaultPhone.getPhoneId();
    }

    private CallForwardInfo getCallForwardInfo(ImsCallForwardInfo imsCallForwardInfo) {
        CallForwardInfo callForwardInfo = new CallForwardInfo();
        callForwardInfo.status = imsCallForwardInfo.getStatus();
        callForwardInfo.reason = getCFReasonFromCondition(imsCallForwardInfo.getCondition());
        callForwardInfo.serviceClass = 1;
        callForwardInfo.toa = imsCallForwardInfo.getToA();
        callForwardInfo.number = imsCallForwardInfo.getNumber();
        callForwardInfo.timeSeconds = imsCallForwardInfo.getTimeSeconds();
        return callForwardInfo;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getLine1Number() {
        return this.mDefaultPhone.getLine1Number();
    }

    public CallForwardInfo[] handleCfQueryResult(ImsCallForwardInfo[] imsCallForwardInfoArr) {
        CallForwardInfo[] callForwardInfoArr = (imsCallForwardInfoArr == null || imsCallForwardInfoArr.length == 0) ? null : new CallForwardInfo[imsCallForwardInfoArr.length];
        if (imsCallForwardInfoArr == null || imsCallForwardInfoArr.length == 0) {
            setVoiceCallForwardingFlag(getIccRecords(), 1, false, null);
        } else {
            int length = imsCallForwardInfoArr.length;
            for (int i = 0; i < length; i++) {
                if (imsCallForwardInfoArr[i].getCondition() == 0) {
                    setVoiceCallForwardingFlag(getIccRecords(), 1, imsCallForwardInfoArr[i].getStatus() == 1, imsCallForwardInfoArr[i].getNumber());
                }
                callForwardInfoArr[i] = getCallForwardInfo(imsCallForwardInfoArr[i]);
            }
        }
        return callForwardInfoArr;
    }

    private int[] handleCbQueryResult(ImsSsInfo[] imsSsInfoArr) {
        int[] iArr = {0};
        if (imsSsInfoArr[0].getStatus() == 1) {
            iArr[0] = 1;
        }
        return iArr;
    }

    private int[] handleCwQueryResult(ImsSsInfo[] imsSsInfoArr) {
        int[] iArr = {0};
        if (imsSsInfoArr[0].getStatus() == 1) {
            iArr[0] = 1;
            iArr[1] = 1;
        }
        return iArr;
    }

    private void sendResponse(Message message, Object obj, Throwable th) {
        if (message != null) {
            AsyncResult.forMessage(message, obj, th != null ? getCommandException(th) : null);
            message.sendToTarget();
        }
    }

    private void updateDataServiceState() {
        if (this.mSS == null || this.mDefaultPhone.getServiceStateTracker() == null || this.mDefaultPhone.getServiceStateTracker().mSS == null) {
            return;
        }
        ServiceState serviceState = this.mDefaultPhone.getServiceStateTracker().mSS;
        this.mSS.setDataRegState(serviceState.getDataRegistrationState());
        for (NetworkRegistrationInfo networkRegistrationInfo : serviceState.getNetworkRegistrationInfoListForDomain(2)) {
            this.mSS.addNetworkRegistrationInfo(networkRegistrationInfo);
        }
        this.mSS.setIwlanPreferred(serviceState.isIwlanPreferred());
        logd("updateDataServiceState: defSs = " + serviceState + " imsSs = " + this.mSS);
    }

    boolean isCsRetryException(Throwable th) {
        return th != null && (th instanceof ImsException) && ((ImsException) th).getCode() == 146;
    }

    private Bundle setCsfbBundle(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Phone.CS_FALLBACK_SS, z);
        return bundle;
    }

    private void sendResponseOrRetryOnCsfbSs(C0240SS c0240ss, int i, Throwable th, Object obj) {
        if (!isCsRetryException(th)) {
            sendResponse(c0240ss.mOnComplete, obj, th);
            return;
        }
        Rlog.d("ImsPhone", "Try CSFB: " + i);
        c0240ss.mOnComplete.setData(setCsfbBundle(true));
        if (i == 12) {
            this.mDefaultPhone.setCallForwardingOption(c0240ss.mCfAction, c0240ss.mCfReason, c0240ss.mDialingNumber, c0240ss.mServiceClass, c0240ss.mTimerSeconds, c0240ss.mOnComplete);
        } else if (i == 13) {
            this.mDefaultPhone.getCallForwardingOption(c0240ss.mCfReason, c0240ss.mServiceClass, c0240ss.mOnComplete);
        } else if (i != 79) {
            switch (i) {
                case CallFailCause.REQUESTED_FACILITY_NOT_IMPLEMENTED /* 69 */:
                    this.mDefaultPhone.setCallBarring(c0240ss.mFacility, c0240ss.mLockState, c0240ss.mPassword, c0240ss.mOnComplete, c0240ss.mServiceClass);
                    return;
                case CallFailCause.ONLY_RESTRICTED_DIGITAL_INFO_BC_AVAILABLE /* 70 */:
                    this.mDefaultPhone.getCallBarring(c0240ss.mFacility, c0240ss.mPassword, c0240ss.mOnComplete, c0240ss.mServiceClass);
                    return;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_25 /* 71 */:
                    this.mDefaultPhone.setCallWaiting(c0240ss.mEnable, c0240ss.mServiceClass, c0240ss.mOnComplete);
                    return;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.mDefaultPhone.getCallWaiting(c0240ss.mOnComplete);
                    return;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_41 /* 73 */:
                    this.mDefaultPhone.setOutgoingCallerIdDisplay(c0240ss.mClirMode, c0240ss.mOnComplete);
                    return;
                case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                    this.mDefaultPhone.getOutgoingCallerIdDisplay(c0240ss.mOnComplete);
                    return;
                default:
                    return;
            }
        } else {
            this.mDefaultPhone.queryCLIP(c0240ss.mOnComplete);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:10:0x002f  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0142  */
    @Override // com.android.internal.telephony.Phone, android.os.Handler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleMessage(Message message) {
        C0240SS c0240ss;
        int i;
        Object obj;
        AsyncResult asyncResult = (AsyncResult) message.obj;
        if (asyncResult != null) {
            Object obj2 = asyncResult.userObj;
            if (obj2 instanceof C0240SS) {
                c0240ss = (C0240SS) obj2;
                logd("handleMessage what=" + message.what);
                i = message.what;
                if (i != 12) {
                    if (asyncResult.exception == null && c0240ss != null && c0240ss.mCfReason == 0) {
                        setVoiceCallForwardingFlag(getIccRecords(), 1, isCfEnable(c0240ss.mCfAction), c0240ss.mDialingNumber);
                    }
                    if (c0240ss != null) {
                        sendResponseOrRetryOnCsfbSs(c0240ss, message.what, asyncResult.exception, null);
                        return;
                    }
                    return;
                } else if (i == 13) {
                    r2 = asyncResult.exception == null ? handleCfQueryResult((ImsCallForwardInfo[]) asyncResult.result) : null;
                    if (c0240ss != null) {
                        sendResponseOrRetryOnCsfbSs(c0240ss, message.what, asyncResult.exception, r2);
                        return;
                    }
                    return;
                } else {
                    switch (i) {
                        case CallFailCause.REQUESTED_FACILITY_NOT_IMPLEMENTED /* 69 */:
                        case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_25 /* 71 */:
                            break;
                        case CallFailCause.ONLY_RESTRICTED_DIGITAL_INFO_BC_AVAILABLE /* 70 */:
                        case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                            if (asyncResult.exception == null) {
                                if (i == 70) {
                                    r2 = handleCbQueryResult((ImsSsInfo[]) asyncResult.result);
                                } else if (i == 72) {
                                    r2 = handleCwQueryResult((ImsSsInfo[]) asyncResult.result);
                                }
                            }
                            if (c0240ss != null) {
                                sendResponseOrRetryOnCsfbSs(c0240ss, message.what, asyncResult.exception, r2);
                                return;
                            }
                            return;
                        case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_41 /* 73 */:
                            if (asyncResult.exception == null && c0240ss != null) {
                                saveClirSetting(c0240ss.mClirMode);
                                break;
                            }
                            break;
                        case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                            ImsSsInfo imsSsInfo = (ImsSsInfo) asyncResult.result;
                            r2 = imsSsInfo != null ? imsSsInfo.getCompatArray(8) : null;
                            if (c0240ss != null) {
                                sendResponseOrRetryOnCsfbSs(c0240ss, message.what, asyncResult.exception, r2);
                                return;
                            }
                            return;
                        case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_26 /* 75 */:
                            logd("EVENT_DEFAULT_PHONE_DATA_STATE_CHANGED");
                            updateDataServiceState();
                            return;
                        case 76:
                            updateRoamingState((ServiceState) ((AsyncResult) message.obj).result);
                            return;
                        case RadioNVItems.RIL_NV_LTE_HIDDEN_BAND_PRIORITY_25 /* 77 */:
                            logd("Voice call ended. Handle pending updateRoamingState.");
                            this.mCT.unregisterForVoiceCallEnded(this);
                            ServiceStateTracker serviceStateTracker = getDefaultPhone().getServiceStateTracker();
                            if (serviceStateTracker != null) {
                                updateRoamingState(serviceStateTracker.mSS);
                                return;
                            }
                            return;
                        case RadioNVItems.RIL_NV_LTE_HIDDEN_BAND_PRIORITY_26 /* 78 */:
                            AsyncResult asyncResult2 = (AsyncResult) message.obj;
                            if (asyncResult2.exception != null || (obj = asyncResult2.result) == null) {
                                return;
                            }
                            Phone.SilentRedialParam silentRedialParam = (Phone.SilentRedialParam) obj;
                            try {
                                com.android.internal.telephony.Connection dial = dial(silentRedialParam.dialString, updateDialArgsForVolteSilentRedial(silentRedialParam.dialArgs, silentRedialParam.causeCode));
                                Rlog.d("ImsPhone", "Notify volte redial connection changed cn: " + dial);
                                Phone phone = this.mDefaultPhone;
                                if (phone != null) {
                                    phone.notifyRedialConnectionChanged(dial);
                                    return;
                                }
                                return;
                            } catch (CallStateException e) {
                                Rlog.e("ImsPhone", "volte silent redial failed: " + e);
                                Phone phone2 = this.mDefaultPhone;
                                if (phone2 != null) {
                                    phone2.notifyRedialConnectionChanged(null);
                                    return;
                                }
                                return;
                            }
                        case 79:
                            Throwable th = asyncResult.exception;
                            if (th == null) {
                                Object obj3 = asyncResult.result;
                                if (obj3 instanceof ImsSsInfo) {
                                    r2 = (ImsSsInfo) obj3;
                                }
                            }
                            if (c0240ss != null) {
                                sendResponseOrRetryOnCsfbSs(c0240ss, i, th, r2);
                                return;
                            }
                            return;
                        default:
                            super.handleMessage(message);
                            return;
                    }
                    if (c0240ss != null) {
                        sendResponseOrRetryOnCsfbSs(c0240ss, message.what, asyncResult.exception, null);
                        return;
                    }
                    return;
                }
            }
        }
        c0240ss = null;
        logd("handleMessage what=" + message.what);
        i = message.what;
        if (i != 12) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.imsphone.ImsPhone$2 */
    /* loaded from: classes.dex */
    public class C02372 extends ImsEcbmStateListener {
        C02372(Executor executor) {
            super(executor);
        }

        public void onECBMEntered(Executor executor) {
            ImsPhone.this.logd("onECBMEntered");
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhone$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhone.C02372.this.lambda$onECBMEntered$0();
                }
            }, executor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onECBMEntered$0() {
            ImsPhone.this.handleEnterEmergencyCallbackMode();
        }

        public void onECBMExited(Executor executor) {
            ImsPhone.this.logd("onECBMExited");
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhone$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhone.C02372.this.lambda$onECBMExited$1();
                }
            }, executor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onECBMExited$1() {
            ImsPhone.this.handleExitEmergencyCallbackMode();
        }
    }

    @VisibleForTesting
    public ImsEcbmStateListener getImsEcbmStateListener() {
        return this.mImsEcbmStateListener;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isInEmergencyCall() {
        return this.mCT.isInEmergencyCall();
    }

    private void sendEmergencyCallbackModeChange() {
        Intent intent = new Intent("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        intent.putExtra("android.telephony.extra.PHONE_IN_ECM_STATE", isInEcm());
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, getPhoneId());
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        logd("sendEmergencyCallbackModeChange: isInEcm=" + isInEcm());
    }

    @Override // com.android.internal.telephony.Phone
    public void exitEmergencyCallbackMode() {
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        logd("exitEmergencyCallbackMode()");
        try {
            this.mCT.getEcbmInterface().exitEmergencyCallbackMode();
        } catch (ImsException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void handleEnterEmergencyCallbackMode() {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            logd("DomainSelection enabled: ignore ECBM enter event.");
            return;
        }
        logd("handleEnterEmergencyCallbackMode,mIsPhoneInEcmState= " + isInEcm());
        if (isInEcm()) {
            return;
        }
        setIsInEcm(true);
        sendEmergencyCallbackModeChange();
        ((GsmCdmaPhone) this.mDefaultPhone).notifyEmergencyCallRegistrants(true);
        postDelayed(this.mExitEcmRunnable, TelephonyProperties.ecm_exit_timer().orElse(300000L).longValue());
        this.mWakeLock.acquire();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void handleExitEmergencyCallbackMode() {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            logd("DomainSelection enabled: ignore ECBM exit event.");
            return;
        }
        logd("handleExitEmergencyCallbackMode: mIsPhoneInEcmState = " + isInEcm());
        if (isInEcm()) {
            setIsInEcm(false);
        }
        removeCallbacks(this.mExitEcmRunnable);
        Registrant registrant = this.mEcmExitRespRegistrant;
        if (registrant != null) {
            registrant.notifyResult(Boolean.TRUE);
        }
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        sendEmergencyCallbackModeChange();
        ((GsmCdmaPhone) this.mDefaultPhone).notifyEmergencyCallRegistrants(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleTimerInEmergencyCallbackMode(int i) {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            return;
        }
        if (i == 0) {
            postDelayed(this.mExitEcmRunnable, TelephonyProperties.ecm_exit_timer().orElse(300000L).longValue());
            ((GsmCdmaPhone) this.mDefaultPhone).notifyEcbmTimerReset(Boolean.FALSE);
            setEcmCanceledForEmergency(false);
        } else if (i == 1) {
            removeCallbacks(this.mExitEcmRunnable);
            ((GsmCdmaPhone) this.mDefaultPhone).notifyEcbmTimerReset(Boolean.TRUE);
            setEcmCanceledForEmergency(true);
        } else {
            loge("handleTimerInEmergencyCallbackMode, unsupported action " + i);
        }
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setOnEcbModeExitResponse(Handler handler, int i, Object obj) {
        this.mEcmExitRespRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unsetOnEcbModeExitResponse(Handler handler) {
        this.mEcmExitRespRegistrant.clear();
    }

    public void onFeatureCapabilityChanged() {
        this.mDefaultPhone.getServiceStateTracker().onImsCapabilityChanged();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isImsCapabilityAvailable(int i, int i2) throws ImsException {
        return this.mCT.isImsCapabilityAvailable(i, i2);
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isVolteEnabled() {
        return isVoiceOverCellularImsEnabled();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isVoiceOverCellularImsEnabled() {
        return this.mCT.isVoiceOverCellularImsEnabled();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isWifiCallingEnabled() {
        return this.mCT.isVowifiEnabled();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isVideoEnabled() {
        return this.mCT.isVideoCallEnabled();
    }

    @Override // com.android.internal.telephony.Phone
    public int getImsRegistrationTech() {
        return this.mCT.getImsRegistrationTech();
    }

    @Override // com.android.internal.telephony.Phone
    public void getImsRegistrationTech(Consumer<Integer> consumer) {
        this.mCT.getImsRegistrationTech(consumer);
    }

    @Override // com.android.internal.telephony.Phone
    public void getImsRegistrationState(Consumer<Integer> consumer) {
        consumer.accept(Integer.valueOf(this.mImsMmTelRegistrationHelper.getImsRegistrationState()));
    }

    @Override // com.android.internal.telephony.Phone
    public Phone getDefaultPhone() {
        return this.mDefaultPhone;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isImsRegistered() {
        return this.mImsMmTelRegistrationHelper.isImsRegistered();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setImsRegistered(boolean z) {
        this.mImsMmTelRegistrationHelper.updateRegistrationState(z ? 2 : 0);
    }

    @Override // com.android.internal.telephony.Phone
    public void callEndCleanupHandOverCallIfAny() {
        this.mCT.callEndCleanupHandOverCallIfAny();
    }

    public void processDisconnectReason(ImsReasonInfo imsReasonInfo) {
        if (imsReasonInfo.mCode == 1000 && imsReasonInfo.mExtraMessage != null && this.mImsManagerFactory.create(this.mContext, this.mPhoneId).isWfcEnabledByUser()) {
            processWfcDisconnectForNotification(imsReasonInfo);
        }
    }

    private void processWfcDisconnectForNotification(ImsReasonInfo imsReasonInfo) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        if (carrierConfigManager == null) {
            loge("processDisconnectReason: CarrierConfigManager is not ready");
            return;
        }
        PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(getSubId());
        if (configForSubId == null) {
            loge("processDisconnectReason: no config for subId " + getSubId());
            return;
        }
        String[] stringArray = configForSubId.getStringArray("wfc_operator_error_codes_string_array");
        if (stringArray == null) {
            return;
        }
        String[] stringArray2 = this.mContext.getResources().getStringArray(17236214);
        String[] stringArray3 = this.mContext.getResources().getStringArray(17236215);
        for (int i = 0; i < stringArray.length; i++) {
            String[] split = stringArray[i].split("\\|");
            if (split.length != 2) {
                loge("Invalid carrier config: " + stringArray[i]);
            } else if (imsReasonInfo.mExtraMessage.startsWith(split[0])) {
                int length = split[0].length();
                if (!Character.isLetterOrDigit(split[0].charAt(length - 1)) || imsReasonInfo.mExtraMessage.length() <= length || !Character.isLetterOrDigit(imsReasonInfo.mExtraMessage.charAt(length))) {
                    CharSequence text = this.mContext.getText(17041746);
                    int parseInt = Integer.parseInt(split[1]);
                    if (parseInt >= 0 && parseInt < stringArray2.length && parseInt < stringArray3.length) {
                        String str = imsReasonInfo.mExtraMessage;
                        String format = !stringArray2[parseInt].isEmpty() ? String.format(stringArray2[parseInt], imsReasonInfo.mExtraMessage) : str;
                        if (!stringArray3[parseInt].isEmpty()) {
                            str = String.format(stringArray3[parseInt], imsReasonInfo.mExtraMessage);
                        }
                        Intent intent = new Intent("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR");
                        intent.putExtra(Phone.EXTRA_KEY_ALERT_TITLE, text);
                        intent.putExtra(Phone.EXTRA_KEY_ALERT_MESSAGE, format);
                        intent.putExtra(Phone.EXTRA_KEY_NOTIFICATION_MESSAGE, str);
                        this.mContext.sendOrderedBroadcast(intent, null, this.mResultReceiver, null, -1, null, null);
                        return;
                    }
                    loge("Invalid index: " + stringArray[i]);
                }
            } else {
                continue;
            }
        }
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isUtEnabled() {
        return this.mCT.isUtEnabled();
    }

    @Override // com.android.internal.telephony.Phone
    public void sendEmergencyCallStateChange(boolean z) {
        this.mDefaultPhone.sendEmergencyCallStateChange(z);
    }

    @Override // com.android.internal.telephony.Phone
    public void setBroadcastEmergencyCallStateChanges(boolean z) {
        this.mDefaultPhone.setBroadcastEmergencyCallStateChanges(z);
    }

    @VisibleForTesting
    public PowerManager.WakeLock getWakeLock() {
        return this.mWakeLock;
    }

    private void updateRoamingState(ServiceState serviceState) {
        if (serviceState == null) {
            loge("updateRoamingState: null ServiceState!");
            return;
        }
        boolean roaming = serviceState.getRoaming();
        if (this.mLastKnownRoamingState == roaming) {
            return;
        }
        if (!(serviceState.getState() == 0 || serviceState.getDataRegistrationState() == 0) || !this.mDefaultPhone.isRadioOn()) {
            logi("updateRoamingState: we are not IN_SERVICE, ignoring roaming change.");
        } else if (this.mCT.getState() == PhoneConstants.State.IDLE) {
            logd("updateRoamingState now: " + roaming);
            this.mLastKnownRoamingState = roaming;
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getContext().getSystemService("carrier_config");
            if (carrierConfigManager == null || !CarrierConfigManager.isConfigForIdentifiedCarrier(carrierConfigManager.getConfigForSubId(getSubId()))) {
                return;
            }
            ImsManager create = this.mImsManagerFactory.create(this.mContext, this.mPhoneId);
            create.setWfcMode(create.getWfcMode(roaming), roaming);
        } else {
            logd("updateRoamingState postponed: " + roaming);
            this.mCT.registerForVoiceCallEnded(this, 77, null);
        }
    }

    public RegistrationManager.RegistrationCallback getImsMmTelRegistrationCallback() {
        return this.mImsMmTelRegistrationHelper.getCallback();
    }

    public void resetImsRegistrationState() {
        logd("resetImsRegistrationState");
        this.mImsMmTelRegistrationHelper.reset();
        if (SubscriptionManager.isValidSubscriptionId(getSubId())) {
            updateImsRegistrationInfo(0, -1, 0);
        }
    }

    @VisibleForTesting
    public void setPhoneNumberForSourceIms(Uri[] uriArr) {
        String formatNumberToE164;
        String extractPhoneNumberFromAssociatedUris = extractPhoneNumberFromAssociatedUris(uriArr);
        if (extractPhoneNumberFromAssociatedUris == null) {
            return;
        }
        int subId = getSubId();
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            if (isSubscriptionManagerServiceEnabled()) {
                SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(subId);
                if (subscriptionInfoInternal == null || (formatNumberToE164 = PhoneNumberUtils.formatNumberToE164(extractPhoneNumberFromAssociatedUris, subscriptionInfoInternal.getCountryIso())) == null) {
                    return;
                }
                this.mSubscriptionManagerService.setNumberFromIms(subId, formatNumberToE164);
                return;
            }
            SubscriptionController subscriptionController = SubscriptionController.getInstance();
            String formatNumberToE1642 = PhoneNumberUtils.formatNumberToE164(extractPhoneNumberFromAssociatedUris, getCountryIso(subscriptionController, subId));
            if (formatNumberToE1642 == null) {
                return;
            }
            subscriptionController.setSubscriptionProperty(subId, "phone_number_source_ims", formatNumberToE1642);
        }
    }

    private static String getCountryIso(SubscriptionController subscriptionController, int i) {
        SubscriptionInfo subscriptionInfo = subscriptionController.getSubscriptionInfo(i);
        String countryIso = subscriptionInfo == null ? PhoneConfigurationManager.SSSS : subscriptionInfo.getCountryIso();
        return countryIso == null ? PhoneConfigurationManager.SSSS : countryIso;
    }

    private static String extractPhoneNumberFromAssociatedUris(Uri[] uriArr) {
        if (uriArr == null) {
            return null;
        }
        return (String) Arrays.stream(uriArr).filter(new Predicate() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$extractPhoneNumberFromAssociatedUris$1;
                lambda$extractPhoneNumberFromAssociatedUris$1 = ImsPhone.lambda$extractPhoneNumberFromAssociatedUris$1((Uri) obj);
                return lambda$extractPhoneNumberFromAssociatedUris$1;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$extractPhoneNumberFromAssociatedUris$2;
                lambda$extractPhoneNumberFromAssociatedUris$2 = ImsPhone.lambda$extractPhoneNumberFromAssociatedUris$2((Uri) obj);
                return lambda$extractPhoneNumberFromAssociatedUris$2;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Uri) obj).getSchemeSpecificPart();
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$extractPhoneNumberFromAssociatedUris$3;
                lambda$extractPhoneNumberFromAssociatedUris$3 = ImsPhone.lambda$extractPhoneNumberFromAssociatedUris$3((String) obj);
                return lambda$extractPhoneNumberFromAssociatedUris$3;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.imsphone.ImsPhone$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$extractPhoneNumberFromAssociatedUris$4;
                lambda$extractPhoneNumberFromAssociatedUris$4 = ImsPhone.lambda$extractPhoneNumberFromAssociatedUris$4((String) obj);
                return lambda$extractPhoneNumberFromAssociatedUris$4;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$extractPhoneNumberFromAssociatedUris$1(Uri uri) {
        return uri != null && uri.isOpaque();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$extractPhoneNumberFromAssociatedUris$2(Uri uri) {
        return "tel".equalsIgnoreCase(uri.getScheme()) || "sip".equalsIgnoreCase(uri.getScheme());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$extractPhoneNumberFromAssociatedUris$3(String str) {
        return str != null && str.startsWith("+");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$extractPhoneNumberFromAssociatedUris$4(String str) {
        return str.split("@")[0];
    }

    @Override // com.android.internal.telephony.Phone
    public IccRecords getIccRecords() {
        return this.mDefaultPhone.getIccRecords();
    }

    public PhoneInternalInterface.DialArgs updateDialArgsForVolteSilentRedial(PhoneInternalInterface.DialArgs dialArgs, int i) {
        if (dialArgs != null) {
            ImsDialArgs.Builder from = ImsDialArgs.Builder.from(dialArgs);
            Bundle bundle = new Bundle(dialArgs.intentExtras);
            if (i == 3002) {
                bundle.putString("CallRadioTech", String.valueOf(18));
                logd("trigger VoWifi emergency call");
                from.setIntentExtras(bundle);
            } else if (i == 3001) {
                logd("trigger VoLte emergency call");
            }
            return from.build();
        }
        return new PhoneInternalInterface.DialArgs.Builder().build();
    }

    @Override // com.android.internal.telephony.Phone
    public VoiceCallSessionStats getVoiceCallSessionStats() {
        return this.mDefaultPhone.getVoiceCallSessionStats();
    }

    public ImsStats getImsStats() {
        return this.mImsStats;
    }

    @VisibleForTesting
    public void setImsStats(ImsStats imsStats) {
        this.mImsStats = imsStats;
    }

    public boolean hasAliveCall() {
        Call.State state = getForegroundCall().getState();
        Call.State state2 = Call.State.IDLE;
        return (state == state2 && getBackgroundCall().getState() == state2) ? false : true;
    }

    public boolean getLastKnownRoamingState() {
        return this.mLastKnownRoamingState;
    }

    public void updateImsRegistrationInfo(int i) {
        int i2 = this.mImsRegistrationState;
        if (i2 == 2) {
            if (this.mNotifiedRegisteredState && i == this.mImsRegistrationCapabilities) {
                return;
            }
            this.mImsRegistrationCapabilities = i;
            if (i == 0) {
                return;
            }
            this.mDefaultPhone.mCi.updateImsRegistrationInfo(i2, this.mImsRegistrationTech, 0, i, null);
            this.mNotifiedRegisteredState = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateImsRegistrationInfo(int i, int i2, int i3) {
        int i4;
        int i5 = this.mImsRegistrationState;
        if (i == i5) {
            if (i == 2 && i2 == this.mImsRegistrationTech) {
                return;
            }
            if (i == 0 && i3 == this.mImsRegistrationSuggestedAction && i2 == this.mImsDeregistrationTech) {
                return;
            }
        }
        if (i == 0) {
            this.mDefaultPhone.mCi.updateImsRegistrationInfo(i, i2, i3, 0, null);
        } else if (i5 == 2 && (i4 = this.mImsRegistrationCapabilities) > 0) {
            this.mDefaultPhone.mCi.updateImsRegistrationInfo(i, i2, 0, i4, null);
            this.mImsRegistrationTech = i2;
            this.mNotifiedRegisteredState = true;
            return;
        }
        this.mImsRegistrationState = i;
        this.mImsRegistrationTech = i2;
        this.mImsRegistrationSuggestedAction = i3;
        if (i == 0) {
            this.mImsDeregistrationTech = i2;
        } else {
            this.mImsDeregistrationTech = -1;
        }
        this.mImsRegistrationCapabilities = 0;
        this.mNotifiedRegisteredState = false;
    }

    @Override // com.android.internal.telephony.Phone
    public void setTerminalBasedCallWaitingStatus(int i) {
        this.mCT.setTerminalBasedCallWaitingStatus(i);
    }

    @Override // com.android.internal.telephony.Phone
    public void triggerEpsFallback(int i, Message message) {
        this.mDefaultPhone.triggerEpsFallback(i, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void startImsTraffic(int i, int i2, int i3, int i4, Message message) {
        this.mDefaultPhone.startImsTraffic(i, i2, i3, i4, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void stopImsTraffic(int i, Message message) {
        this.mDefaultPhone.stopImsTraffic(i, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForConnectionSetupFailure(Handler handler, int i, Object obj) {
        this.mDefaultPhone.registerForConnectionSetupFailure(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForConnectionSetupFailure(Handler handler) {
        this.mDefaultPhone.unregisterForConnectionSetupFailure(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public void triggerImsDeregistration(int i) {
        this.mCT.triggerImsDeregistration(i);
    }

    @Override // com.android.internal.telephony.Phone
    public void updateImsCallStatus(List<ImsCallInfo> list, Message message) {
        this.mDefaultPhone.updateImsCallStatus(list, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void triggerNotifyAnbr(int i, int i2, int i3) {
        this.mCT.triggerNotifyAnbr(i, i2, i3);
    }

    @Override // com.android.internal.telephony.Phone
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("ImsPhone extends:");
        super.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.flush();
        indentingPrintWriter.println("ImsPhone:");
        indentingPrintWriter.println("  mDefaultPhone = " + this.mDefaultPhone);
        indentingPrintWriter.println("  mPendingMMIs = " + this.mPendingMMIs);
        indentingPrintWriter.println("  mPostDialHandler = " + this.mPostDialHandler);
        indentingPrintWriter.println("  mSS = " + this.mSS);
        indentingPrintWriter.println("  mWakeLock = " + this.mWakeLock);
        indentingPrintWriter.println("  mIsPhoneInEcmState = " + isInEcm());
        indentingPrintWriter.println("  mEcmExitRespRegistrant = " + this.mEcmExitRespRegistrant);
        indentingPrintWriter.println("  mSilentRedialRegistrants = " + this.mSilentRedialRegistrants);
        indentingPrintWriter.println("  mImsMmTelRegistrationState = " + this.mImsMmTelRegistrationHelper.getImsRegistrationState());
        indentingPrintWriter.println("  mLastKnownRoamingState = " + this.mLastKnownRoamingState);
        indentingPrintWriter.println("  mSsnRegistrants = " + this.mSsnRegistrants);
        indentingPrintWriter.println(" Registration Log:");
        indentingPrintWriter.increaseIndent();
        this.mRegLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
    }

    private void logi(String str) {
        Rlog.i("ImsPhone", "[" + this.mPhoneId + "] " + str);
    }

    private void logv(String str) {
        Rlog.v("ImsPhone", "[" + this.mPhoneId + "] " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        Rlog.d("ImsPhone", "[" + this.mPhoneId + "] " + str);
    }

    private void loge(String str) {
        Rlog.e("ImsPhone", "[" + this.mPhoneId + "] " + str);
    }
}
