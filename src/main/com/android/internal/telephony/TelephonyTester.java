package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsConferenceState;
import android.telephony.ims.ImsExternalCallState;
import android.telephony.ims.ImsReasonInfo;
import com.android.ims.ImsCall;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import com.android.internal.telephony.imsphone.ImsExternalCallTracker;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.imsphone.ImsPhoneCall;
import com.android.internal.telephony.imsphone.ImsPhoneCallTracker;
import com.android.internal.telephony.test.TestConferenceEventPackageParser;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class TelephonyTester {
    private static List<ImsExternalCallState> mImsExternalCallStates;
    protected BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.TelephonyTester.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                TelephonyTester telephonyTester = TelephonyTester.this;
                telephonyTester.log("sIntentReceiver.onReceive: action=" + action);
                if (action.equals(TelephonyTester.this.mPhone.getActionDetached())) {
                    TelephonyTester.this.log("simulate detaching");
                    TelephonyTester.this.mPhone.getServiceStateTracker().mDetachedRegistrants.get(1).notifyRegistrants();
                } else if (action.equals(TelephonyTester.this.mPhone.getActionAttached())) {
                    TelephonyTester.this.log("simulate attaching");
                    TelephonyTester.this.mPhone.getServiceStateTracker().mAttachedRegistrants.get(1).notifyRegistrants();
                } else if (action.equals("com.android.internal.telephony.TestConferenceEventPackage")) {
                    TelephonyTester.this.log("inject simulated conference event package");
                    TelephonyTester.this.handleTestConferenceEventPackage(context, intent.getStringExtra("filename"), intent.getBooleanExtra("bypassImsCall", false));
                } else if (action.equals("com.android.internal.telephony.TestDialogEventPackage")) {
                    TelephonyTester.this.log("handle test dialog event package intent");
                    TelephonyTester.this.handleTestDialogEventPackageIntent(intent);
                } else if (action.equals("com.android.internal.telephony.TestSuppSrvcFail")) {
                    TelephonyTester.this.log("handle test supp svc failed intent");
                    TelephonyTester.this.handleSuppServiceFailedIntent(intent);
                } else if (action.equals("com.android.internal.telephony.TestHandoverFail")) {
                    TelephonyTester.this.log("handle handover fail test intent");
                    TelephonyTester.this.handleHandoverFailedIntent();
                } else if (action.equals("com.android.internal.telephony.TestSuppSrvcNotification")) {
                    TelephonyTester.this.log("handle supp service notification test intent");
                    TelephonyTester.this.sendTestSuppServiceNotification(intent);
                } else if (action.equals("com.android.internal.telephony.TestServiceState")) {
                    TelephonyTester.this.log("handle test service state changed intent");
                    TelephonyTester.this.setServiceStateTestIntent(intent);
                } else if (action.equals("com.android.internal.telephony.TestImsECall")) {
                    TelephonyTester.this.log("handle test IMS ecall intent");
                    TelephonyTester.this.testImsECall();
                } else if (action.equals("com.android.internal.telephony.TestReceiveDtmf")) {
                    TelephonyTester.this.log("handle test DTMF intent");
                    TelephonyTester.this.testImsReceiveDtmf(intent);
                } else if (action.equals("com.android.internal.telephony.TestChangeNumber")) {
                    TelephonyTester.this.log("handle test change number intent");
                    TelephonyTester.this.testChangeNumber(intent);
                } else {
                    TelephonyTester telephonyTester2 = TelephonyTester.this;
                    telephonyTester2.log("onReceive: unknown action=" + action);
                }
            } catch (BadParcelableException e) {
                Rlog.w(TelephonyTester.this.mLogTag, e);
            }
        }
    };
    private String mLogTag;
    private Phone mPhone;
    private Intent mServiceStateTestIntent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TelephonyTester(Phone phone) {
        this.mPhone = phone;
        if (TelephonyUtils.IS_DEBUGGABLE) {
            this.mLogTag = "TelephonyTester-" + this.mPhone.getPhoneId();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(this.mPhone.getActionDetached());
            log("register for intent action=" + this.mPhone.getActionDetached());
            intentFilter.addAction(this.mPhone.getActionAttached());
            log("register for intent action=" + this.mPhone.getActionAttached());
            if (this.mPhone.getPhoneType() == 5) {
                log("register for intent action=com.android.internal.telephony.TestConferenceEventPackage");
                intentFilter.addAction("com.android.internal.telephony.TestConferenceEventPackage");
                intentFilter.addAction("com.android.internal.telephony.TestDialogEventPackage");
                intentFilter.addAction("com.android.internal.telephony.TestSuppSrvcFail");
                intentFilter.addAction("com.android.internal.telephony.TestHandoverFail");
                intentFilter.addAction("com.android.internal.telephony.TestSuppSrvcNotification");
                intentFilter.addAction("com.android.internal.telephony.TestImsECall");
                intentFilter.addAction("com.android.internal.telephony.TestReceiveDtmf");
                mImsExternalCallStates = new ArrayList();
            }
            intentFilter.addAction("com.android.internal.telephony.TestServiceState");
            log("register for intent action=com.android.internal.telephony.TestServiceState");
            intentFilter.addAction("com.android.internal.telephony.TestChangeNumber");
            log("register for intent action=com.android.internal.telephony.TestChangeNumber");
            phone.getContext().registerReceiver(this.mIntentReceiver, intentFilter, null, this.mPhone.getHandler(), 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSuppServiceFailedIntent(Intent intent) {
        ImsPhone imsPhone = (ImsPhone) this.mPhone;
        if (imsPhone == null) {
            return;
        }
        imsPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.values()[intent.getIntExtra("failureCode", 0)]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHandoverFailedIntent() {
        ImsCall imsCall = getImsCall();
        if (imsCall == null) {
            return;
        }
        imsCall.getImsCallSessionListenerProxy().callSessionHandoverFailed(imsCall.getCallSession(), 13, 18, new ImsReasonInfo());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTestConferenceEventPackage(Context context, String str, boolean z) {
        ImsCall imsCall;
        ImsPhone imsPhone = (ImsPhone) this.mPhone;
        if (imsPhone == null) {
            return;
        }
        ImsPhoneCallTracker imsPhoneCallTracker = (ImsPhoneCallTracker) imsPhone.getCallTracker();
        File file = new File(context.getFilesDir(), str);
        try {
            ImsConferenceState parse = new TestConferenceEventPackageParser(new FileInputStream(file)).parse();
            if (parse == null) {
                return;
            }
            if (z) {
                imsPhoneCallTracker.injectTestConferenceState(parse);
                return;
            }
            ImsPhoneCall foregroundCall = imsPhone.getForegroundCall();
            if (foregroundCall == null || (imsCall = foregroundCall.getImsCall()) == null) {
                return;
            }
            imsCall.conferenceStateUpdated(parse);
        } catch (FileNotFoundException unused) {
            log("Test conference event package file not found: " + file.getAbsolutePath());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTestDialogEventPackageIntent(Intent intent) {
        ImsExternalCallTracker externalCallTracker;
        ImsPhone imsPhone = (ImsPhone) this.mPhone;
        if (imsPhone == null || (externalCallTracker = imsPhone.getExternalCallTracker()) == null) {
            return;
        }
        if (intent.hasExtra("startPackage")) {
            mImsExternalCallStates.clear();
        } else if (intent.hasExtra("sendPackage")) {
            externalCallTracker.refreshExternalCallState(mImsExternalCallStates);
            mImsExternalCallStates.clear();
        } else if (intent.hasExtra("dialogId")) {
            mImsExternalCallStates.add(new ImsExternalCallState(intent.getIntExtra("dialogId", 0), Uri.parse(intent.getStringExtra(IccProvider.STR_NUMBER)), intent.getBooleanExtra("canPull", true), intent.getIntExtra(CallWaitingController.KEY_STATE, 1), 2, false));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTestSuppServiceNotification(Intent intent) {
        if (intent.hasExtra("code") && intent.hasExtra("type")) {
            int intExtra = intent.getIntExtra("code", -1);
            int intExtra2 = intent.getIntExtra("type", -1);
            ImsPhone imsPhone = (ImsPhone) this.mPhone;
            if (imsPhone == null) {
                return;
            }
            log("Test supp service notification:" + intExtra);
            SuppServiceNotification suppServiceNotification = new SuppServiceNotification();
            suppServiceNotification.code = intExtra;
            suppServiceNotification.notificationType = intExtra2;
            imsPhone.notifySuppSvcNotification(suppServiceNotification);
        }
    }

    public void setServiceStateTestIntent(Intent intent) {
        this.mServiceStateTestIntent = intent;
        this.mPhone.getServiceStateTracker().sendEmptyMessage(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overrideServiceState(ServiceState serviceState) {
        if (this.mServiceStateTestIntent == null || serviceState == null || this.mPhone.getPhoneId() != this.mServiceStateTestIntent.getIntExtra("phone_id", this.mPhone.getPhoneId())) {
            return;
        }
        if (this.mServiceStateTestIntent.hasExtra("action") && "reset".equals(this.mServiceStateTestIntent.getStringExtra("action"))) {
            log("Service state override reset");
            return;
        }
        if (this.mServiceStateTestIntent.hasExtra("voice_reg_state")) {
            int intExtra = this.mServiceStateTestIntent.getIntExtra("data_reg_state", 1);
            serviceState.setVoiceRegState(this.mServiceStateTestIntent.getIntExtra("voice_reg_state", 1));
            NetworkRegistrationInfo.Builder builder = new NetworkRegistrationInfo.Builder(serviceState.getNetworkRegistrationInfo(1, 1));
            if (intExtra == 0) {
                builder.setRegistrationState(1);
            } else {
                builder.setRegistrationState(0);
            }
            serviceState.addNetworkRegistrationInfo(builder.build());
            log("Override voice service state with " + serviceState.getState());
        }
        if (this.mServiceStateTestIntent.hasExtra("data_reg_state")) {
            int intExtra2 = this.mServiceStateTestIntent.getIntExtra("data_reg_state", 1);
            serviceState.setDataRegState(intExtra2);
            NetworkRegistrationInfo.Builder builder2 = new NetworkRegistrationInfo.Builder(serviceState.getNetworkRegistrationInfo(2, 1));
            if (intExtra2 == 0) {
                builder2.setRegistrationState(1);
            } else {
                builder2.setRegistrationState(0);
            }
            serviceState.addNetworkRegistrationInfo(builder2.build());
            log("Override data service state with " + serviceState.getDataRegistrationState());
        }
        if (this.mServiceStateTestIntent.hasExtra("operator")) {
            String[] split = this.mServiceStateTestIntent.getStringExtra("operator").split(",");
            int length = split.length;
            String str = PhoneConfigurationManager.SSSS;
            String str2 = length > 0 ? split[0] : PhoneConfigurationManager.SSSS;
            String str3 = split.length > 1 ? split[1] : str2;
            if (split.length > 2) {
                str = split[2];
            }
            serviceState.setOperatorName(str2, str3, str);
            log("Override operator with " + Arrays.toString(split));
        }
        if (this.mServiceStateTestIntent.hasExtra("operator_raw")) {
            String stringExtra = this.mServiceStateTestIntent.getStringExtra("operator_raw");
            serviceState.setOperatorAlphaLongRaw(stringExtra);
            serviceState.setOperatorAlphaShortRaw(stringExtra);
            log("Override operator_raw with " + stringExtra);
        }
        if (this.mServiceStateTestIntent.hasExtra("nr_frequency_range")) {
            serviceState.setNrFrequencyRange(this.mServiceStateTestIntent.getIntExtra("nr_frequency_range", 0));
            log("Override NR frequency range with " + serviceState.getNrFrequencyRange());
        }
        if (this.mServiceStateTestIntent.hasExtra("nr_state")) {
            NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
            if (networkRegistrationInfo == null) {
                networkRegistrationInfo = new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).build();
            }
            networkRegistrationInfo.setNrState(this.mServiceStateTestIntent.getIntExtra("nr_state", 0));
            serviceState.addNetworkRegistrationInfo(networkRegistrationInfo);
            log("Override NR state with " + serviceState.getNrState());
        }
        if (this.mServiceStateTestIntent.hasExtra("voice_rat")) {
            NetworkRegistrationInfo networkRegistrationInfo2 = serviceState.getNetworkRegistrationInfo(1, 1);
            if (networkRegistrationInfo2 == null) {
                networkRegistrationInfo2 = new NetworkRegistrationInfo.Builder().setDomain(1).setTransportType(1).build();
            }
            networkRegistrationInfo2.setAccessNetworkTechnology(ServiceState.rilRadioTechnologyToNetworkType(this.mServiceStateTestIntent.getIntExtra("voice_rat", 0)));
            serviceState.addNetworkRegistrationInfo(networkRegistrationInfo2);
            log("Override voice rat with " + serviceState.getRilVoiceRadioTechnology());
        }
        if (this.mServiceStateTestIntent.hasExtra("data_rat")) {
            NetworkRegistrationInfo networkRegistrationInfo3 = serviceState.getNetworkRegistrationInfo(2, 1);
            if (networkRegistrationInfo3 == null) {
                networkRegistrationInfo3 = new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).build();
            }
            networkRegistrationInfo3.setAccessNetworkTechnology(ServiceState.rilRadioTechnologyToNetworkType(this.mServiceStateTestIntent.getIntExtra("data_rat", 0)));
            serviceState.addNetworkRegistrationInfo(networkRegistrationInfo3);
            log("Override data rat with " + serviceState.getRilDataRadioTechnology());
        }
        if (this.mServiceStateTestIntent.hasExtra("voice_roaming_type")) {
            NetworkRegistrationInfo networkRegistrationInfo4 = serviceState.getNetworkRegistrationInfo(1, 1);
            if (networkRegistrationInfo4 == null) {
                networkRegistrationInfo4 = new NetworkRegistrationInfo.Builder().setDomain(1).setTransportType(1).build();
            }
            networkRegistrationInfo4.setRoamingType(this.mServiceStateTestIntent.getIntExtra("voice_roaming_type", 1));
            serviceState.addNetworkRegistrationInfo(networkRegistrationInfo4);
            log("Override voice roaming type with " + serviceState.getVoiceRoamingType());
        }
        if (this.mServiceStateTestIntent.hasExtra("data_roaming_type")) {
            NetworkRegistrationInfo networkRegistrationInfo5 = serviceState.getNetworkRegistrationInfo(2, 1);
            if (networkRegistrationInfo5 == null) {
                networkRegistrationInfo5 = new NetworkRegistrationInfo.Builder().setDomain(2).setTransportType(1).build();
            }
            networkRegistrationInfo5.setRoamingType(this.mServiceStateTestIntent.getIntExtra("data_roaming_type", 1));
            serviceState.addNetworkRegistrationInfo(networkRegistrationInfo5);
            log("Override data roaming type with " + serviceState.getDataRoamingType());
        }
    }

    void testImsECall() {
        ImsCall imsCall = getImsCall();
        if (imsCall == null) {
            return;
        }
        ImsCallProfile callProfile = imsCall.getCallProfile();
        Bundle callExtras = callProfile.getCallExtras();
        if (callExtras == null) {
            callExtras = new Bundle();
        }
        callExtras.putBoolean("e_call", true);
        callProfile.mCallExtras = callExtras;
        imsCall.getImsCallSessionListenerProxy().callSessionUpdated(imsCall.getSession(), callProfile);
    }

    private ImsCall getImsCall() {
        ImsPhoneCall foregroundCall;
        ImsCall imsCall;
        ImsPhone imsPhone = (ImsPhone) this.mPhone;
        if (imsPhone == null || (foregroundCall = imsPhone.getForegroundCall()) == null || (imsCall = foregroundCall.getImsCall()) == null) {
            return null;
        }
        return imsCall;
    }

    void testImsReceiveDtmf(Intent intent) {
        if (intent.hasExtra("digit")) {
            char charAt = intent.getStringExtra("digit").charAt(0);
            ImsCall imsCall = getImsCall();
            if (imsCall == null) {
                return;
            }
            imsCall.getImsCallSessionListenerProxy().callSessionDtmfReceived(charAt);
        }
    }

    void testChangeNumber(Intent intent) {
        if (intent.hasExtra(IccProvider.STR_NUMBER)) {
            final String stringExtra = intent.getStringExtra(IccProvider.STR_NUMBER);
            this.mPhone.getForegroundCall().getConnections().stream().forEach(new Consumer() { // from class: com.android.internal.telephony.TelephonyTester$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyTester.lambda$testChangeNumber$0(stringExtra, (Connection) obj);
                }
            });
            Phone phone = this.mPhone;
            if (phone instanceof GsmCdmaPhone) {
                ((GsmCdmaPhone) phone).notifyPhoneStateChanged();
                ((GsmCdmaPhone) this.mPhone).notifyPreciseCallStateChanged();
            } else if (phone instanceof ImsPhone) {
                ((ImsPhone) phone).notifyPhoneStateChanged();
                ((ImsPhone) this.mPhone).notifyPreciseCallStateChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$testChangeNumber$0(String str, Connection connection) {
        connection.setAddress(str, 1);
        connection.setDialString(str);
    }
}
