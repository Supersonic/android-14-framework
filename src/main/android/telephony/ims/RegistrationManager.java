package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public interface RegistrationManager {
    public static final Map<Integer, Integer> IMS_REG_TO_ACCESS_TYPE_MAP = Map.of(-1, -1, 0, 1, 3, 1, 1, 2, 2, 2);
    public static final int REGISTRATION_STATE_NOT_REGISTERED = 0;
    public static final int REGISTRATION_STATE_REGISTERED = 2;
    public static final int REGISTRATION_STATE_REGISTERING = 1;
    @SystemApi
    public static final int SUGGESTED_ACTION_NONE = 0;
    @SystemApi
    public static final int SUGGESTED_ACTION_TRIGGER_PLMN_BLOCK = 1;
    @SystemApi
    public static final int SUGGESTED_ACTION_TRIGGER_PLMN_BLOCK_WITH_TIMEOUT = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsRegistrationState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SuggestedAction {
    }

    void getRegistrationState(Executor executor, Consumer<Integer> consumer);

    void getRegistrationTransportType(Executor executor, Consumer<Integer> consumer);

    void registerImsRegistrationCallback(Executor executor, RegistrationCallback registrationCallback) throws ImsException;

    void unregisterImsRegistrationCallback(RegistrationCallback registrationCallback);

    static String registrationStateToString(int value) {
        switch (value) {
            case 0:
                return "REGISTRATION_STATE_NOT_REGISTERED";
            case 1:
                return "REGISTRATION_STATE_REGISTERING";
            case 2:
                return "REGISTRATION_STATE_REGISTERED";
            default:
                return Integer.toString(value);
        }
    }

    static int getAccessType(int regtech) {
        Map<Integer, Integer> map = IMS_REG_TO_ACCESS_TYPE_MAP;
        if (!map.containsKey(Integer.valueOf(regtech))) {
            Log.m104w("RegistrationManager", "getAccessType - invalid regType returned: " + regtech);
            return -1;
        }
        return map.get(Integer.valueOf(regtech)).intValue();
    }

    /* loaded from: classes3.dex */
    public static class RegistrationCallback {
        private final RegistrationBinder mBinder = new RegistrationBinder(this);

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class RegistrationBinder extends IImsRegistrationCallback.Stub {
            private Bundle mBundle = new Bundle();
            private Executor mExecutor;
            private final RegistrationCallback mLocalCallback;

            RegistrationBinder(RegistrationCallback localCallback) {
                this.mLocalCallback = localCallback;
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onRegistered(final ImsRegistrationAttributes attr) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onRegistered$0(attr);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onRegistered$0(ImsRegistrationAttributes attr) {
                this.mLocalCallback.onRegistered(attr);
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onRegistering(final ImsRegistrationAttributes attr) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onRegistering$1(attr);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onRegistering$1(ImsRegistrationAttributes attr) {
                this.mLocalCallback.onRegistering(attr);
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onDeregistered(final ImsReasonInfo info, final int suggestedAction, final int imsRadioTech) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onDeregistered$2(info, suggestedAction, imsRadioTech);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDeregistered$2(ImsReasonInfo info, int suggestedAction, int imsRadioTech) {
                this.mLocalCallback.onUnregistered(info, suggestedAction, imsRadioTech);
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onDeregisteredWithDetails(final ImsReasonInfo info, final int suggestedAction, final int imsRadioTech, final SipDetails details) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onDeregisteredWithDetails$3(info, suggestedAction, imsRadioTech);
                        }
                    });
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onDeregisteredWithDetails$4(info, details);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDeregisteredWithDetails$3(ImsReasonInfo info, int suggestedAction, int imsRadioTech) {
                this.mLocalCallback.onUnregistered(info, suggestedAction, imsRadioTech);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDeregisteredWithDetails$4(ImsReasonInfo info, SipDetails details) {
                this.mLocalCallback.onUnregistered(info, details);
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onTechnologyChangeFailed(final int imsRadioTech, final ImsReasonInfo info) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onTechnologyChangeFailed$5(imsRadioTech, info);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onTechnologyChangeFailed$5(int imsRadioTech, ImsReasonInfo info) {
                this.mLocalCallback.onTechnologyChangeFailed(RegistrationManager.getAccessType(imsRadioTech), info);
            }

            @Override // android.telephony.ims.aidl.IImsRegistrationCallback
            public void onSubscriberAssociatedUriChanged(final Uri[] uris) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.RegistrationManager$RegistrationCallback$RegistrationBinder$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegistrationManager.RegistrationCallback.RegistrationBinder.this.lambda$onSubscriberAssociatedUriChanged$6(uris);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSubscriberAssociatedUriChanged$6(Uri[] uris) {
                this.mLocalCallback.onSubscriberAssociatedUriChanged(uris);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setExecutor(Executor executor) {
                this.mExecutor = executor;
            }
        }

        @Deprecated
        public void onRegistered(int imsTransportType) {
        }

        public void onRegistered(ImsRegistrationAttributes attributes) {
            onRegistered(attributes.getTransportType());
        }

        public void onRegistering(int imsTransportType) {
        }

        public void onRegistering(ImsRegistrationAttributes attributes) {
            onRegistering(attributes.getTransportType());
        }

        public void onUnregistered(ImsReasonInfo info) {
        }

        public void onUnregistered(ImsReasonInfo info, int suggestedAction, int imsRadioTech) {
            onUnregistered(info);
        }

        @SystemApi
        public void onUnregistered(ImsReasonInfo info, SipDetails details) {
        }

        public void onTechnologyChangeFailed(int imsTransportType, ImsReasonInfo info) {
        }

        public void onSubscriberAssociatedUriChanged(Uri[] uris) {
        }

        public final IImsRegistrationCallback getBinder() {
            return this.mBinder;
        }

        public void setExecutor(Executor executor) {
            this.mBinder.setExecutor(executor);
        }
    }
}
