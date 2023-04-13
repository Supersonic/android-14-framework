package android.service.euicc;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.service.euicc.IEuiccService;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccInfo;
import android.text.TextUtils;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
@SystemApi
/* loaded from: classes3.dex */
public abstract class EuiccService extends Service {
    public static final String ACTION_BIND_CARRIER_PROVISIONING_SERVICE = "android.service.euicc.action.BIND_CARRIER_PROVISIONING_SERVICE";
    public static final String ACTION_CONVERT_TO_EMBEDDED_SUBSCRIPTION = "android.service.euicc.action.CONVERT_TO_EMBEDDED_SUBSCRIPTION";
    public static final String ACTION_DELETE_SUBSCRIPTION_PRIVILEGED = "android.service.euicc.action.DELETE_SUBSCRIPTION_PRIVILEGED";
    public static final String ACTION_MANAGE_EMBEDDED_SUBSCRIPTIONS = "android.service.euicc.action.MANAGE_EMBEDDED_SUBSCRIPTIONS";
    public static final String ACTION_PROVISION_EMBEDDED_SUBSCRIPTION = "android.service.euicc.action.PROVISION_EMBEDDED_SUBSCRIPTION";
    public static final String ACTION_RENAME_SUBSCRIPTION_PRIVILEGED = "android.service.euicc.action.RENAME_SUBSCRIPTION_PRIVILEGED";
    @Deprecated
    public static final String ACTION_RESOLVE_CONFIRMATION_CODE = "android.service.euicc.action.RESOLVE_CONFIRMATION_CODE";
    public static final String ACTION_RESOLVE_DEACTIVATE_SIM = "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM";
    public static final String ACTION_RESOLVE_NO_PRIVILEGES = "android.service.euicc.action.RESOLVE_NO_PRIVILEGES";
    public static final String ACTION_RESOLVE_RESOLVABLE_ERRORS = "android.service.euicc.action.RESOLVE_RESOLVABLE_ERRORS";
    public static final String ACTION_START_CARRIER_ACTIVATION = "android.service.euicc.action.START_CARRIER_ACTIVATION";
    public static final String ACTION_START_EUICC_ACTIVATION = "android.service.euicc.action.START_EUICC_ACTIVATION";
    public static final String ACTION_TOGGLE_SUBSCRIPTION_PRIVILEGED = "android.service.euicc.action.TOGGLE_SUBSCRIPTION_PRIVILEGED";
    public static final String ACTION_TRANSFER_EMBEDDED_SUBSCRIPTIONS = "android.service.euicc.action.TRANSFER_EMBEDDED_SUBSCRIPTIONS";
    public static final String CATEGORY_EUICC_UI = "android.service.euicc.category.EUICC_UI";
    public static final String EUICC_SERVICE_INTERFACE = "android.service.euicc.EuiccService";
    public static final String EXTRA_RESOLUTION_ALLOW_POLICY_RULES = "android.service.euicc.extra.RESOLUTION_ALLOW_POLICY_RULES";
    public static final String EXTRA_RESOLUTION_CALLING_PACKAGE = "android.service.euicc.extra.RESOLUTION_CALLING_PACKAGE";
    public static final String EXTRA_RESOLUTION_CARD_ID = "android.service.euicc.extra.RESOLUTION_CARD_ID";
    public static final String EXTRA_RESOLUTION_CONFIRMATION_CODE = "android.service.euicc.extra.RESOLUTION_CONFIRMATION_CODE";
    public static final String EXTRA_RESOLUTION_CONFIRMATION_CODE_RETRIED = "android.service.euicc.extra.RESOLUTION_CONFIRMATION_CODE_RETRIED";
    public static final String EXTRA_RESOLUTION_CONSENT = "android.service.euicc.extra.RESOLUTION_CONSENT";
    public static final String EXTRA_RESOLUTION_PORT_INDEX = "android.service.euicc.extra.RESOLUTION_PORT_INDEX";
    public static final String EXTRA_RESOLUTION_SUBSCRIPTION_ID = "android.service.euicc.extra.RESOLUTION_SUBSCRIPTION_ID";
    public static final String EXTRA_RESOLUTION_USE_PORT_INDEX = "android.service.euicc.extra.RESOLUTION_USE_PORT_INDEX";
    public static final String EXTRA_RESOLVABLE_ERRORS = "android.service.euicc.extra.RESOLVABLE_ERRORS";
    public static final int RESOLVABLE_ERROR_CONFIRMATION_CODE = 1;
    public static final int RESOLVABLE_ERROR_POLICY_RULES = 2;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_MUST_DEACTIVATE_SIM = -1;
    @Deprecated
    public static final int RESULT_NEED_CONFIRMATION_CODE = -2;
    public static final int RESULT_OK = 0;
    public static final int RESULT_RESOLVABLE_ERRORS = -2;
    private static final String TAG = "EuiccService";
    private ThreadPoolExecutor mExecutor;
    private final IEuiccService.Stub mStubWrapper = new IEuiccServiceWrapper();

    /* loaded from: classes3.dex */
    public static abstract class OtaStatusChangedCallback {
        public abstract void onOtaStatusChanged(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ResolvableError {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Result {
    }

    public abstract int onDeleteSubscription(int i, String str);

    @Deprecated
    public abstract int onEraseSubscriptions(int i);

    public abstract GetDefaultDownloadableSubscriptionListResult onGetDefaultDownloadableSubscriptionList(int i, boolean z);

    public abstract GetDownloadableSubscriptionMetadataResult onGetDownloadableSubscriptionMetadata(int i, DownloadableSubscription downloadableSubscription, boolean z);

    public abstract String onGetEid(int i);

    public abstract EuiccInfo onGetEuiccInfo(int i);

    public abstract GetEuiccProfileInfoListResult onGetEuiccProfileInfoList(int i);

    public abstract int onGetOtaStatus(int i);

    public abstract int onRetainSubscriptionsForFactoryReset(int i);

    public abstract void onStartOtaIfNecessary(int i, OtaStatusChangedCallback otaStatusChangedCallback);

    @Deprecated
    public abstract int onSwitchToSubscription(int i, String str, boolean z);

    public abstract int onUpdateSubscriptionNickname(int i, String str, String str2);

    public int encodeSmdxSubjectAndReasonCode(String subjectCode, String reasonCode) {
        if (TextUtils.isEmpty(subjectCode) || TextUtils.isEmpty(reasonCode)) {
            throw new IllegalArgumentException("SubjectCode/ReasonCode is empty");
        }
        String[] subjectCodeToken = subjectCode.split("\\.");
        String[] reasonCodeToken = reasonCode.split("\\.");
        if (subjectCodeToken.length > 3 || reasonCodeToken.length > 3) {
            throw new UnsupportedOperationException("Only three nested layer is supported.");
        }
        int result = 10 << ((3 - subjectCodeToken.length) * 4);
        for (String digitString : subjectCodeToken) {
            int num = Integer.parseInt(digitString);
            if (num > 15) {
                throw new UnsupportedOperationException("SubjectCode exceeds 15");
            }
            result = (result << 4) + num;
        }
        int result2 = result << ((3 - reasonCodeToken.length) * 4);
        for (String digitString2 : reasonCodeToken) {
            int num2 = Integer.parseInt(digitString2);
            if (num2 > 15) {
                throw new UnsupportedOperationException("ReasonCode exceeds 15");
            }
            result2 = (result2 << 4) + num2;
        }
        return result2;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() { // from class: android.service.euicc.EuiccService.1
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                return new Thread(r, "EuiccService #" + this.mCount.getAndIncrement());
            }
        });
        this.mExecutor = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mExecutor.shutdownNow();
        super.onDestroy();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mStubWrapper;
    }

    public GetDownloadableSubscriptionMetadataResult onGetDownloadableSubscriptionMetadata(int slotId, int portIndex, DownloadableSubscription subscription, boolean forceDeactivateSim) {
        throw new UnsupportedOperationException("LPA must override onGetDownloadableSubscriptionMetadata");
    }

    @Deprecated
    public DownloadSubscriptionResult onDownloadSubscription(int slotId, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, Bundle resolvedBundle) {
        return null;
    }

    public DownloadSubscriptionResult onDownloadSubscription(int slotIndex, int portIndex, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, Bundle resolvedBundle) {
        throw new UnsupportedOperationException("LPA must override onDownloadSubscription");
    }

    @Deprecated
    public int onDownloadSubscription(int slotId, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim) {
        return Integer.MIN_VALUE;
    }

    public int onSwitchToSubscriptionWithPort(int slotId, int portIndex, String iccid, boolean forceDeactivateSim) {
        throw new UnsupportedOperationException("LPA must override onSwitchToSubscriptionWithPort");
    }

    public int onEraseSubscriptions(int slotIndex, int options) {
        throw new UnsupportedOperationException("This method must be overridden to enable the ResetOption parameter");
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("The connected LPA does not implement EuiccService#dump()");
    }

    public static String resultToString(int result) {
        switch (result) {
            case -2:
                return "RESOLVABLE_ERRORS";
            case -1:
                return "MUST_DEACTIVATE_SIM";
            case 0:
                return "OK";
            case 1:
                return "FIRST_USER";
            default:
                return "UNKNOWN(" + result + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    /* loaded from: classes3.dex */
    private class IEuiccServiceWrapper extends IEuiccService.Stub {
        private IEuiccServiceWrapper() {
        }

        @Override // android.service.euicc.IEuiccService
        public void downloadSubscription(final int slotId, final int portIndex, final DownloadableSubscription subscription, final boolean switchAfterDownload, final boolean forceDeactivateSim, final Bundle resolvedBundle, final IDownloadSubscriptionCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    DownloadSubscriptionResult result;
                    try {
                        result = EuiccService.this.onDownloadSubscription(slotId, portIndex, subscription, switchAfterDownload, forceDeactivateSim, resolvedBundle);
                    } catch (AbstractMethodError | UnsupportedOperationException e) {
                        Log.m103w(EuiccService.TAG, "The new onDownloadSubscription(int, int, DownloadableSubscription, boolean, boolean, Bundle) is not implemented. Fall back to the old one.", e);
                        result = EuiccService.this.onDownloadSubscription(slotId, subscription, switchAfterDownload, forceDeactivateSim, resolvedBundle);
                    }
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e2) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getEid(final int slotId, final IGetEidCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    String eid = EuiccService.this.onGetEid(slotId);
                    try {
                        callback.onSuccess(eid);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void startOtaIfNecessary(final int slotId, final IOtaStatusChangedCallback statusChangedCallback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.3
                @Override // java.lang.Runnable
                public void run() {
                    EuiccService.this.onStartOtaIfNecessary(slotId, new OtaStatusChangedCallback() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.3.1
                        @Override // android.service.euicc.EuiccService.OtaStatusChangedCallback
                        public void onOtaStatusChanged(int status) {
                            try {
                                statusChangedCallback.onOtaStatusChanged(status);
                            } catch (RemoteException e) {
                            }
                        }
                    });
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getOtaStatus(final int slotId, final IGetOtaStatusCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.4
                @Override // java.lang.Runnable
                public void run() {
                    int status = EuiccService.this.onGetOtaStatus(slotId);
                    try {
                        callback.onSuccess(status);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getDownloadableSubscriptionMetadata(final int slotId, final int portIndex, final DownloadableSubscription subscription, final boolean switchAfterDownload, final boolean forceDeactivateSim, final IGetDownloadableSubscriptionMetadataCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.5
                @Override // java.lang.Runnable
                public void run() {
                    GetDownloadableSubscriptionMetadataResult result;
                    if (switchAfterDownload) {
                        try {
                            result = EuiccService.this.onGetDownloadableSubscriptionMetadata(slotId, portIndex, subscription, forceDeactivateSim);
                        } catch (AbstractMethodError | UnsupportedOperationException e) {
                            Log.m103w(EuiccService.TAG, "The new onGetDownloadableSubscriptionMetadata(int, int, DownloadableSubscription, boolean) is not implemented. Fall back to the old one.", e);
                            result = EuiccService.this.onGetDownloadableSubscriptionMetadata(slotId, subscription, forceDeactivateSim);
                        }
                    } else {
                        result = EuiccService.this.onGetDownloadableSubscriptionMetadata(slotId, subscription, forceDeactivateSim);
                    }
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e2) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getDefaultDownloadableSubscriptionList(final int slotId, final boolean forceDeactivateSim, final IGetDefaultDownloadableSubscriptionListCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.6
                @Override // java.lang.Runnable
                public void run() {
                    GetDefaultDownloadableSubscriptionListResult result = EuiccService.this.onGetDefaultDownloadableSubscriptionList(slotId, forceDeactivateSim);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getEuiccProfileInfoList(final int slotId, final IGetEuiccProfileInfoListCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.7
                @Override // java.lang.Runnable
                public void run() {
                    GetEuiccProfileInfoListResult result = EuiccService.this.onGetEuiccProfileInfoList(slotId);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void getEuiccInfo(final int slotId, final IGetEuiccInfoCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.8
                @Override // java.lang.Runnable
                public void run() {
                    EuiccInfo euiccInfo = EuiccService.this.onGetEuiccInfo(slotId);
                    try {
                        callback.onSuccess(euiccInfo);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void deleteSubscription(final int slotId, final String iccid, final IDeleteSubscriptionCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.9
                @Override // java.lang.Runnable
                public void run() {
                    int result = EuiccService.this.onDeleteSubscription(slotId, iccid);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void switchToSubscription(final int slotId, final int portIndex, final String iccid, final boolean forceDeactivateSim, final ISwitchToSubscriptionCallback callback, final boolean usePortIndex) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.10
                @Override // java.lang.Runnable
                public void run() {
                    int result;
                    if (usePortIndex) {
                        result = EuiccService.this.onSwitchToSubscriptionWithPort(slotId, portIndex, iccid, forceDeactivateSim);
                    } else {
                        result = EuiccService.this.onSwitchToSubscription(slotId, iccid, forceDeactivateSim);
                    }
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void updateSubscriptionNickname(final int slotId, final String iccid, final String nickname, final IUpdateSubscriptionNicknameCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.11
                @Override // java.lang.Runnable
                public void run() {
                    int result = EuiccService.this.onUpdateSubscriptionNickname(slotId, iccid, nickname);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void eraseSubscriptions(final int slotId, final IEraseSubscriptionsCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.12
                @Override // java.lang.Runnable
                public void run() {
                    int result = EuiccService.this.onEraseSubscriptions(slotId);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void eraseSubscriptionsWithOptions(final int slotIndex, final int options, final IEraseSubscriptionsCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.13
                @Override // java.lang.Runnable
                public void run() {
                    int result = EuiccService.this.onEraseSubscriptions(slotIndex, options);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void retainSubscriptionsForFactoryReset(final int slotId, final IRetainSubscriptionsForFactoryResetCallback callback) {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.14
                @Override // java.lang.Runnable
                public void run() {
                    int result = EuiccService.this.onRetainSubscriptionsForFactoryReset(slotId);
                    try {
                        callback.onComplete(result);
                    } catch (RemoteException e) {
                    }
                }
            });
        }

        @Override // android.service.euicc.IEuiccService
        public void dump(final IEuiccServiceDumpResultCallback callback) throws RemoteException {
            EuiccService.this.mExecutor.execute(new Runnable() { // from class: android.service.euicc.EuiccService.IEuiccServiceWrapper.15
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        EuiccService.this.dump(pw);
                        callback.onComplete(sw.toString());
                    } catch (RemoteException e) {
                    }
                }
            });
        }
    }
}
