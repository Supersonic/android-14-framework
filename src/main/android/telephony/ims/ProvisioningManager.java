package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.ims.ProvisioningManager;
import android.telephony.ims.aidl.IFeatureProvisioningCallback;
import android.telephony.ims.aidl.IImsConfigCallback;
import android.telephony.ims.aidl.IRcsConfigCallback;
import com.android.internal.telephony.ITelephony;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class ProvisioningManager {
    @SystemApi
    public static final String ACTION_RCS_SINGLE_REGISTRATION_CAPABILITY_UPDATE = "android.telephony.ims.action.RCS_SINGLE_REGISTRATION_CAPABILITY_UPDATE";
    @SystemApi
    public static final String EXTRA_STATUS = "android.telephony.ims.extra.STATUS";
    @SystemApi
    public static final String EXTRA_SUBSCRIPTION_ID = "android.telephony.ims.extra.SUBSCRIPTION_ID";
    public static final int KEY_1X_EPDG_TIMER_SEC = 64;
    public static final int KEY_1X_THRESHOLD = 59;
    public static final int KEY_AMR_BANDWIDTH_EFFICIENT_PAYLOAD_TYPE = 50;
    public static final int KEY_AMR_CODEC_MODE_SET_VALUES = 0;
    public static final int KEY_AMR_DEFAULT_ENCODING_MODE = 53;
    public static final int KEY_AMR_OCTET_ALIGNED_PAYLOAD_TYPE = 49;
    public static final int KEY_AMR_WB_BANDWIDTH_EFFICIENT_PAYLOAD_TYPE = 48;
    public static final int KEY_AMR_WB_CODEC_MODE_SET_VALUES = 1;
    public static final int KEY_AMR_WB_OCTET_ALIGNED_PAYLOAD_TYPE = 47;
    public static final int KEY_DTMF_NB_PAYLOAD_TYPE = 52;
    public static final int KEY_DTMF_WB_PAYLOAD_TYPE = 51;
    public static final int KEY_EAB_PROVISIONING_STATUS = 25;
    public static final int KEY_ENABLE_SILENT_REDIAL = 6;
    public static final int KEY_LOCAL_BREAKOUT_PCSCF_ADDRESS = 31;
    public static final int KEY_LTE_EPDG_TIMER_SEC = 62;
    public static final int KEY_LTE_THRESHOLD_1 = 56;
    public static final int KEY_LTE_THRESHOLD_2 = 57;
    public static final int KEY_LTE_THRESHOLD_3 = 58;
    public static final int KEY_MINIMUM_SIP_SESSION_EXPIRATION_TIMER_SEC = 3;
    public static final int KEY_MOBILE_DATA_ENABLED = 29;
    public static final int KEY_MULTIENDPOINT_ENABLED = 65;
    public static final int KEY_RCS_AVAILABILITY_CACHE_EXPIRATION_SEC = 19;
    public static final int KEY_RCS_CAPABILITIES_CACHE_EXPIRATION_SEC = 18;
    public static final int KEY_RCS_CAPABILITIES_POLL_INTERVAL_SEC = 20;
    public static final int KEY_RCS_CAPABILITY_DISCOVERY_ENABLED = 17;
    public static final int KEY_RCS_CAPABILITY_POLL_LIST_SUB_EXP_SEC = 23;
    public static final int KEY_RCS_MAX_NUM_ENTRIES_IN_RCL = 22;
    public static final int KEY_RCS_PUBLISH_OFFLINE_AVAILABILITY_TIMER_SEC = 16;
    public static final int KEY_RCS_PUBLISH_SOURCE_THROTTLE_MS = 21;
    public static final int KEY_RCS_PUBLISH_TIMER_SEC = 15;
    public static final int KEY_REGISTRATION_DOMAIN_NAME = 12;
    public static final int KEY_REGISTRATION_RETRY_BASE_TIME_SEC = 33;
    public static final int KEY_REGISTRATION_RETRY_MAX_TIME_SEC = 34;
    public static final int KEY_RTP_SPEECH_END_PORT = 36;
    public static final int KEY_RTP_SPEECH_START_PORT = 35;
    public static final int KEY_RTT_ENABLED = 66;
    public static final int KEY_SIP_ACK_RECEIPT_WAIT_TIME_MS = 43;
    public static final int KEY_SIP_ACK_RETRANSMIT_WAIT_TIME_MS = 44;
    public static final int KEY_SIP_INVITE_ACK_WAIT_TIME_MS = 38;
    public static final int KEY_SIP_INVITE_CANCELLATION_TIMER_MS = 4;
    public static final int KEY_SIP_INVITE_REQUEST_TRANSMIT_INTERVAL_MS = 37;
    public static final int KEY_SIP_INVITE_RESPONSE_RETRANSMIT_INTERVAL_MS = 42;
    public static final int KEY_SIP_INVITE_RESPONSE_RETRANSMIT_WAIT_TIME_MS = 39;
    public static final int KEY_SIP_KEEP_ALIVE_ENABLED = 32;
    public static final int KEY_SIP_NON_INVITE_REQUEST_RETRANSMISSION_WAIT_TIME_MS = 45;
    public static final int KEY_SIP_NON_INVITE_REQUEST_RETRANSMIT_INTERVAL_MS = 40;
    public static final int KEY_SIP_NON_INVITE_RESPONSE_RETRANSMISSION_WAIT_TIME_MS = 46;
    public static final int KEY_SIP_NON_INVITE_TRANSACTION_TIMEOUT_TIMER_MS = 41;
    public static final int KEY_SIP_SESSION_TIMER_SEC = 2;
    public static final int KEY_SMS_FORMAT = 13;
    public static final int KEY_SMS_OVER_IP_ENABLED = 14;
    public static final int KEY_SMS_PUBLIC_SERVICE_IDENTITY = 54;
    public static final int KEY_T1_TIMER_VALUE_MS = 7;
    public static final int KEY_T2_TIMER_VALUE_MS = 8;
    public static final int KEY_TF_TIMER_VALUE_MS = 9;
    public static final int KEY_TRANSITION_TO_LTE_DELAY_MS = 5;
    public static final int KEY_USE_GZIP_FOR_LIST_SUBSCRIPTION = 24;
    public static final int KEY_VIDEO_QUALITY = 55;
    public static final int KEY_VOICE_OVER_WIFI_ENABLED_OVERRIDE = 28;
    @SystemApi
    public static final int KEY_VOICE_OVER_WIFI_ENTITLEMENT_ID = 67;
    @SystemApi
    public static final int KEY_VOICE_OVER_WIFI_MODE_OVERRIDE = 27;
    @SystemApi
    public static final int KEY_VOICE_OVER_WIFI_ROAMING_ENABLED_OVERRIDE = 26;
    public static final int KEY_VOIMS_OPT_IN_STATUS = 68;
    public static final int KEY_VOLTE_PROVISIONING_STATUS = 10;
    public static final int KEY_VOLTE_USER_OPT_IN_STATUS = 30;
    public static final int KEY_VT_PROVISIONING_STATUS = 11;
    public static final int KEY_WIFI_EPDG_TIMER_SEC = 63;
    public static final int KEY_WIFI_THRESHOLD_A = 60;
    public static final int KEY_WIFI_THRESHOLD_B = 61;
    public static final int PROVISIONING_RESULT_UNKNOWN = -1;
    @SystemApi
    public static final int PROVISIONING_VALUE_DISABLED = 0;
    @SystemApi
    public static final int PROVISIONING_VALUE_ENABLED = 1;
    public static final int SMS_FORMAT_3GPP = 1;
    public static final int SMS_FORMAT_3GPP2 = 0;
    @SystemApi
    public static final int STATUS_CAPABLE = 0;
    @SystemApi
    public static final int STATUS_CARRIER_NOT_CAPABLE = 2;
    @SystemApi
    public static final int STATUS_DEVICE_NOT_CAPABLE = 1;
    @SystemApi
    public static final String STRING_QUERY_RESULT_ERROR_GENERIC = "STRING_QUERY_RESULT_ERROR_GENERIC";
    @SystemApi
    public static final String STRING_QUERY_RESULT_ERROR_NOT_READY = "STRING_QUERY_RESULT_ERROR_NOT_READY";
    private static final String TAG = "ProvisioningManager";
    public static final int VIDEO_QUALITY_HIGH = 1;
    public static final int VIDEO_QUALITY_LOW = 0;
    private int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StringResultError {
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static class Callback {
        private final CallbackBinder mBinder = new CallbackBinder();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class CallbackBinder extends IImsConfigCallback.Stub {
            private Executor mExecutor;
            private final Callback mLocalConfigurationCallback;

            private CallbackBinder(Callback localConfigurationCallback) {
                this.mLocalConfigurationCallback = localConfigurationCallback;
            }

            @Override // android.telephony.ims.aidl.IImsConfigCallback
            public final void onIntConfigChanged(final int item, final int value) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$Callback$CallbackBinder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.Callback.CallbackBinder.this.lambda$onIntConfigChanged$0(item, value);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onIntConfigChanged$0(int item, int value) {
                this.mLocalConfigurationCallback.onProvisioningIntChanged(item, value);
            }

            @Override // android.telephony.ims.aidl.IImsConfigCallback
            public final void onStringConfigChanged(final int item, final String value) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$Callback$CallbackBinder$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.Callback.CallbackBinder.this.lambda$onStringConfigChanged$1(item, value);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onStringConfigChanged$1(int item, String value) {
                this.mLocalConfigurationCallback.onProvisioningStringChanged(item, value);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setExecutor(Executor executor) {
                this.mExecutor = executor;
            }
        }

        public void onProvisioningIntChanged(int item, int value) {
        }

        public void onProvisioningStringChanged(int item, String value) {
        }

        public final IImsConfigCallback getBinder() {
            return this.mBinder;
        }

        public void setExecutor(Executor executor) {
            this.mBinder.setExecutor(executor);
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class FeatureProvisioningCallback {
        private final CallbackBinder mBinder = new CallbackBinder();

        public abstract void onFeatureProvisioningChanged(int i, int i2, boolean z);

        public abstract void onRcsFeatureProvisioningChanged(int i, int i2, boolean z);

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class CallbackBinder extends IFeatureProvisioningCallback.Stub {
            private Executor mExecutor;
            private final FeatureProvisioningCallback mFeatureProvisioningCallback;

            private CallbackBinder(FeatureProvisioningCallback featureProvisioningCallback) {
                this.mFeatureProvisioningCallback = featureProvisioningCallback;
            }

            @Override // android.telephony.ims.aidl.IFeatureProvisioningCallback
            public final void onFeatureProvisioningChanged(final int capability, final int tech, final boolean isProvisioned) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$FeatureProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.FeatureProvisioningCallback.CallbackBinder.this.lambda$onFeatureProvisioningChanged$0(capability, tech, isProvisioned);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onFeatureProvisioningChanged$0(int capability, int tech, boolean isProvisioned) {
                this.mFeatureProvisioningCallback.onFeatureProvisioningChanged(capability, tech, isProvisioned);
            }

            @Override // android.telephony.ims.aidl.IFeatureProvisioningCallback
            public final void onRcsFeatureProvisioningChanged(final int capability, final int tech, final boolean isProvisioned) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$FeatureProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.FeatureProvisioningCallback.CallbackBinder.this.lambda$onRcsFeatureProvisioningChanged$1(capability, tech, isProvisioned);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onRcsFeatureProvisioningChanged$1(int capability, int tech, boolean isProvisioned) {
                this.mFeatureProvisioningCallback.onRcsFeatureProvisioningChanged(capability, tech, isProvisioned);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setExecutor(Executor executor) {
                this.mExecutor = executor;
            }
        }

        public final IFeatureProvisioningCallback getBinder() {
            return this.mBinder;
        }

        public void setExecutor(Executor executor) {
            this.mBinder.setExecutor(executor);
        }
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static class RcsProvisioningCallback {
        private final CallbackBinder mBinder = new CallbackBinder();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class CallbackBinder extends IRcsConfigCallback.Stub {
            private Executor mExecutor;
            private final RcsProvisioningCallback mLocalCallback;

            private CallbackBinder(RcsProvisioningCallback localCallback) {
                this.mLocalCallback = localCallback;
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onConfigurationChanged(final byte[] configXml) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$RcsProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.RcsProvisioningCallback.CallbackBinder.this.lambda$onConfigurationChanged$0(configXml);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onConfigurationChanged$0(byte[] configXml) {
                this.mLocalCallback.onConfigurationChanged(configXml);
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onAutoConfigurationErrorReceived(final int errorCode, final String errorString) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$RcsProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.RcsProvisioningCallback.CallbackBinder.this.lambda$onAutoConfigurationErrorReceived$1(errorCode, errorString);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onAutoConfigurationErrorReceived$1(int errorCode, String errorString) {
                this.mLocalCallback.onAutoConfigurationErrorReceived(errorCode, errorString);
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onConfigurationReset() {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$RcsProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.RcsProvisioningCallback.CallbackBinder.this.lambda$onConfigurationReset$2();
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onConfigurationReset$2() {
                this.mLocalCallback.onConfigurationReset();
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onRemoved() {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$RcsProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.RcsProvisioningCallback.CallbackBinder.this.lambda$onRemoved$3();
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onRemoved$3() {
                this.mLocalCallback.onRemoved();
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onPreProvisioningReceived(final byte[] configXml) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ProvisioningManager$RcsProvisioningCallback$CallbackBinder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProvisioningManager.RcsProvisioningCallback.CallbackBinder.this.lambda$onPreProvisioningReceived$4(configXml);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onPreProvisioningReceived$4(byte[] configXml) {
                this.mLocalCallback.onPreProvisioningReceived(configXml);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setExecutor(Executor executor) {
                this.mExecutor = executor;
            }
        }

        public void onConfigurationChanged(byte[] configXml) {
        }

        public void onAutoConfigurationErrorReceived(int errorCode, String errorString) {
        }

        public void onConfigurationReset() {
        }

        public void onRemoved() {
        }

        public void onPreProvisioningReceived(byte[] configXml) {
        }

        public final IRcsConfigCallback getBinder() {
            return this.mBinder;
        }

        public void setExecutor(Executor executor) {
            this.mBinder.setExecutor(executor);
        }
    }

    @SystemApi
    public static ProvisioningManager createForSubscriptionId(int subId) {
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            throw new IllegalArgumentException("Invalid subscription ID");
        }
        return new ProvisioningManager(subId);
    }

    public ProvisioningManager(int subId) {
        this.mSubId = subId;
    }

    @SystemApi
    public void registerProvisioningChangedCallback(Executor executor, Callback callback) throws ImsException {
        callback.setExecutor(executor);
        try {
            getITelephony().registerImsProvisioningChangedCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @SystemApi
    public void unregisterProvisioningChangedCallback(Callback callback) {
        try {
            getITelephony().unregisterImsProvisioningChangedCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void registerFeatureProvisioningChangedCallback(Executor executor, FeatureProvisioningCallback callback) throws ImsException {
        callback.setExecutor(executor);
        try {
            getITelephony().registerFeatureProvisioningChangedCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    public void unregisterFeatureProvisioningChangedCallback(FeatureProvisioningCallback callback) {
        try {
            getITelephony().unregisterFeatureProvisioningChangedCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public int getProvisioningIntValue(int key) {
        try {
            return getITelephony().getImsProvisioningInt(this.mSubId, key);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public String getProvisioningStringValue(int key) {
        try {
            return getITelephony().getImsProvisioningString(this.mSubId, key);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public int setProvisioningIntValue(int key, int value) {
        try {
            return getITelephony().setImsProvisioningInt(this.mSubId, key, value);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public int setProvisioningStringValue(int key, String value) {
        try {
            return getITelephony().setImsProvisioningString(this.mSubId, key, value);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void setProvisioningStatusForCapability(int capability, int tech, boolean isProvisioned) {
        try {
            getITelephony().setImsProvisioningStatusForCapability(this.mSubId, capability, tech, isProvisioned);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean getProvisioningStatusForCapability(int capability, int tech) {
        try {
            return getITelephony().getImsProvisioningStatusForCapability(this.mSubId, capability, tech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    @Deprecated
    public boolean getRcsProvisioningStatusForCapability(int capability) {
        try {
            return getITelephony().getRcsProvisioningStatusForCapability(this.mSubId, capability, 0);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean getRcsProvisioningStatusForCapability(int capability, int tech) {
        try {
            return getITelephony().getRcsProvisioningStatusForCapability(this.mSubId, capability, tech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    @Deprecated
    public void setRcsProvisioningStatusForCapability(int capability, boolean isProvisioned) {
        try {
            getITelephony().setRcsProvisioningStatusForCapability(this.mSubId, capability, 0, isProvisioned);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void setRcsProvisioningStatusForCapability(int capability, int tech, boolean isProvisioned) {
        try {
            getITelephony().setRcsProvisioningStatusForCapability(this.mSubId, capability, tech, isProvisioned);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isProvisioningRequiredForCapability(int capability, int tech) {
        try {
            return getITelephony().isProvisioningRequiredForCapability(this.mSubId, capability, tech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isRcsProvisioningRequiredForCapability(int capability, int tech) {
        try {
            return getITelephony().isRcsProvisioningRequiredForCapability(this.mSubId, capability, tech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public void notifyRcsAutoConfigurationReceived(byte[] config, boolean isCompressed) {
        if (config == null) {
            throw new IllegalArgumentException("Must include a non-null config XML file.");
        }
        try {
            getITelephony().notifyRcsAutoConfigurationReceived(this.mSubId, config, isCompressed);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public void setRcsClientConfiguration(RcsClientConfiguration rcc) throws ImsException {
        try {
            getITelephony().setRcsClientConfiguration(this.mSubId, rcc);
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @SystemApi
    public boolean isRcsVolteSingleRegistrationCapable() throws ImsException {
        try {
            return getITelephony().isRcsVolteSingleRegistrationCapable(this.mSubId);
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @SystemApi
    public void registerRcsProvisioningCallback(Executor executor, RcsProvisioningCallback callback) throws ImsException {
        callback.setExecutor(executor);
        try {
            getITelephony().registerRcsProvisioningCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @SystemApi
    public void unregisterRcsProvisioningCallback(RcsProvisioningCallback callback) {
        try {
            getITelephony().unregisterRcsProvisioningCallback(this.mSubId, callback.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public void triggerRcsReconfiguration() {
        try {
            getITelephony().triggerRcsReconfiguration(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    private static ITelephony getITelephony() {
        ITelephony binder = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
        if (binder == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        return binder;
    }
}
