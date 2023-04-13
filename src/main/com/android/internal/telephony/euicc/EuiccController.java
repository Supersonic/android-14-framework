package com.android.internal.telephony.euicc;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.service.euicc.DownloadSubscriptionResult;
import android.service.euicc.GetDefaultDownloadableSubscriptionListResult;
import android.service.euicc.GetDownloadableSubscriptionMetadataResult;
import android.service.euicc.GetEuiccProfileInfoListResult;
import android.telephony.AnomalyReporter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import android.telephony.UiccAccessRule;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccInfo;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CarrierPrivilegesTracker;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.euicc.EuiccConnector;
import com.android.internal.telephony.euicc.IEuiccController;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccSlot;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class EuiccController extends IEuiccController.Stub {
    @VisibleForTesting
    static final String EXTRA_OPERATION = "operation";
    private static EuiccController sInstance;
    private final AppOpsManager mAppOpsManager;
    private final EuiccConnector mConnector;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final SubscriptionManager mSubscriptionManager;
    private List<String> mSupportedCountries;
    private final TelephonyManager mTelephonyManager;
    private List<String> mUnsupportedCountries;

    public static EuiccController init(Context context) {
        synchronized (EuiccController.class) {
            if (sInstance == null) {
                sInstance = new EuiccController(context);
            } else {
                Log.wtf("EuiccController", "init() called multiple times! sInstance = " + sInstance);
            }
        }
        return sInstance;
    }

    public static EuiccController get() {
        if (sInstance == null) {
            synchronized (EuiccController.class) {
                if (sInstance == null) {
                    throw new IllegalStateException("get() called before init()");
                }
            }
        }
        return sInstance;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private EuiccController(Context context) {
        this(context, new EuiccConnector(context));
        TelephonyFrameworkInitializer.getTelephonyServiceManager().getEuiccControllerService().register(this);
    }

    @VisibleForTesting
    public EuiccController(Context context, EuiccConnector euiccConnector) {
        this.mContext = context;
        this.mConnector = euiccConnector;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPackageManager = context.getPackageManager();
    }

    public void continueOperation(int i, Intent intent, Bundle bundle) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to continue operation");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            EuiccOperation euiccOperation = (EuiccOperation) intent.getParcelableExtra(EXTRA_OPERATION);
            if (euiccOperation == null) {
                throw new IllegalArgumentException("Invalid resolution intent");
            }
            boolean booleanExtra = intent.getBooleanExtra("android.service.euicc.extra.RESOLUTION_USE_PORT_INDEX", false);
            bundle.putBoolean("android.service.euicc.extra.RESOLUTION_USE_PORT_INDEX", booleanExtra);
            Log.i("EuiccController", " continueOperation portIndex: " + bundle.getInt("android.service.euicc.extra.RESOLUTION_PORT_INDEX") + " usePortIndex: " + booleanExtra);
            euiccOperation.continueOperation(i, bundle, (PendingIntent) intent.getParcelableExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_CALLBACK_INTENT"));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public String getEid(int i, String str) {
        boolean callerCanReadPhoneStatePrivileged = callerCanReadPhoneStatePrivileged();
        try {
            this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (!callerCanReadPhoneStatePrivileged) {
                try {
                    if (!canManageSubscriptionOnTargetSim(i, str, false, -1)) {
                        throw new SecurityException("Must have carrier privileges on subscription to read EID for cardId=" + i);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return blockingGetEidFromEuiccService(i);
        } catch (SecurityException e) {
            EventLog.writeEvent(1397638484, "159062405", -1, "Missing UID checking");
            throw e;
        }
    }

    public int getOtaStatus(int i) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to get OTA status");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return blockingGetOtaStatusFromEuiccService(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void startOtaUpdatingIfNecessary() {
        startOtaUpdatingIfNecessary(this.mTelephonyManager.getCardIdForDefaultEuicc());
    }

    public void startOtaUpdatingIfNecessary(int i) {
        this.mConnector.startOtaIfNecessary(i, new EuiccConnector.OtaStatusChangedCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.1
            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.OtaStatusChangedCallback
            public void onOtaStatusChanged(int i2) {
                EuiccController.this.sendOtaStatusChangedBroadcast();
            }
        });
    }

    public void getDownloadableSubscriptionMetadata(int i, DownloadableSubscription downloadableSubscription, String str, PendingIntent pendingIntent) {
        getDownloadableSubscriptionMetadata(i, downloadableSubscription, false, str, pendingIntent);
    }

    public void setSupportedCountries(boolean z, List<String> list) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to set supported countries");
        }
        if (z) {
            this.mSupportedCountries = list;
        } else {
            this.mUnsupportedCountries = list;
        }
    }

    public List<String> getSupportedCountries(boolean z) {
        List<String> list;
        List<String> list2;
        if (callerCanWriteEmbeddedSubscriptions()) {
            return (!z || (list2 = this.mSupportedCountries) == null) ? (z || (list = this.mUnsupportedCountries) == null) ? Collections.emptyList() : list : list2;
        }
        throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to get supported countries");
    }

    public boolean isSupportedCountry(String str) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to check if the country is supported");
        }
        List<String> list = this.mSupportedCountries;
        if (list == null || list.isEmpty()) {
            Log.i("EuiccController", "Using deny list unsupportedCountries=" + this.mUnsupportedCountries);
            return !isEsimUnsupportedCountry(str);
        }
        Log.i("EuiccController", "Using allow list supportedCountries=" + this.mSupportedCountries);
        return isEsimSupportedCountry(str);
    }

    private boolean isEsimSupportedCountry(String str) {
        if (this.mSupportedCountries == null || TextUtils.isEmpty(str)) {
            return true;
        }
        return this.mSupportedCountries.contains(str);
    }

    private boolean isEsimUnsupportedCountry(String str) {
        if (this.mUnsupportedCountries == null || TextUtils.isEmpty(str)) {
            return false;
        }
        return this.mUnsupportedCountries.contains(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDownloadableSubscriptionMetadata(int i, DownloadableSubscription downloadableSubscription, boolean z, String str, PendingIntent pendingIntent) {
        Log.d("EuiccController", " getDownloadableSubscriptionMetadata callingPackage: " + str);
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to get metadata");
        }
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mConnector.getDownloadableSubscriptionMetadata(i, 0, downloadableSubscription, false, z, new GetMetadataCommandCallback(clearCallingIdentity, downloadableSubscription, str, pendingIntent));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class GetMetadataCommandCallback implements EuiccConnector.GetMetadataCommandCallback {
        protected final PendingIntent mCallbackIntent;
        protected final String mCallingPackage;
        protected final long mCallingToken;
        protected final DownloadableSubscription mSubscription;

        GetMetadataCommandCallback(long j, DownloadableSubscription downloadableSubscription, String str, PendingIntent pendingIntent) {
            this.mCallingToken = j;
            this.mSubscription = downloadableSubscription;
            this.mCallingPackage = str;
            this.mCallbackIntent = pendingIntent;
        }

        @Override // com.android.internal.telephony.euicc.EuiccConnector.GetMetadataCommandCallback
        public void onGetMetadataComplete(int i, GetDownloadableSubscriptionMetadataResult getDownloadableSubscriptionMetadataResult) {
            int i2;
            Intent intent = new Intent();
            int result = getDownloadableSubscriptionMetadataResult.getResult();
            if (result == -1) {
                EuiccController.this.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM", this.mCallingPackage, 0, false, getOperationForDeactivateSim(), i, 0, false);
                i2 = 1;
            } else if (result == 0) {
                intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION", getDownloadableSubscriptionMetadataResult.getDownloadableSubscription());
                i2 = 0;
            } else {
                EuiccController.this.addExtrasToResultIntent(intent, getDownloadableSubscriptionMetadataResult.getResult());
                i2 = 2;
            }
            EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, i2, intent);
        }

        @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
        public void onEuiccServiceUnavailable() {
            EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, 2, null);
        }

        protected EuiccOperation getOperationForDeactivateSim() {
            return EuiccOperation.forGetMetadataDeactivateSim(this.mCallingToken, this.mSubscription, this.mCallingPackage);
        }
    }

    public void downloadSubscription(int i, DownloadableSubscription downloadableSubscription, boolean z, String str, Bundle bundle, PendingIntent pendingIntent) {
        downloadSubscription(i, z ? -1 : 0, downloadableSubscription, z, str, false, bundle, pendingIntent);
    }

    Pair<String, String> decodeSmdxSubjectAndReasonCode(int i) {
        Stack stack = new Stack();
        for (int i2 = 0; i2 < 6; i2++) {
            stack.push(Integer.valueOf(i & 15));
            i >>>= 4;
        }
        return Pair.create((stack.pop() + "." + stack.pop() + "." + stack.pop()).replaceAll("^(0\\.)*", PhoneConfigurationManager.SSSS), (stack.pop() + "." + stack.pop() + "." + stack.pop()).replaceAll("^(0\\.)*", PhoneConfigurationManager.SSSS));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addExtrasToResultIntent(Intent intent, int i) {
        int i2 = i >>> 24;
        intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DETAILED_CODE", i);
        intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_OPERATION_CODE", i2);
        if (i2 == 10) {
            Pair<String, String> decodeSmdxSubjectAndReasonCode = decodeSmdxSubjectAndReasonCode(i);
            intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_SUBJECT_CODE", (String) decodeSmdxSubjectAndReasonCode.first);
            intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_REASON_CODE", (String) decodeSmdxSubjectAndReasonCode.second);
            return;
        }
        intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_ERROR_CODE", 16777215 & i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadSubscription(int i, int i2, DownloadableSubscription downloadableSubscription, boolean z, String str, boolean z2, Bundle bundle, PendingIntent pendingIntent) {
        int i3;
        boolean callerCanWriteEmbeddedSubscriptions = callerCanWriteEmbeddedSubscriptions();
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        boolean isCompatChangeEnabled = isCompatChangeEnabled(str, 224562872L);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z3 = false;
        if (z) {
            i3 = i2;
            if (i3 == -1) {
                if (isCompatChangeEnabled) {
                    try {
                        i3 = getResolvedPortIndexForSubscriptionSwitch(i);
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                } else {
                    i3 = 0;
                }
                if (i3 == -1) {
                    z3 = true;
                }
            }
        } else {
            i3 = i2;
        }
        int i4 = i3;
        Log.d("EuiccController", " downloadSubscription cardId: " + i + " switchAfterDownload: " + z + " portIndex: " + i4 + " forceDeactivateSim: " + z2 + " callingPackage: " + str + " isConsentNeededToResolvePortIndex: " + z3 + " shouldResolvePortIndex:" + isCompatChangeEnabled);
        if (!z3 && callerCanWriteEmbeddedSubscriptions) {
            downloadSubscriptionPrivileged(i, i4, clearCallingIdentity, downloadableSubscription, z, z2, str, bundle, pendingIntent);
            return;
        }
        if (!z3 && canManageSubscriptionOnTargetSim(i, str, true, i4)) {
            this.mConnector.getDownloadableSubscriptionMetadata(i, i4, downloadableSubscription, z, z2, new DownloadSubscriptionGetMetadataCommandCallback(clearCallingIdentity, downloadableSubscription, z, str, z2, pendingIntent, false, i4));
        } else {
            Log.i("EuiccController", "Caller can't manage subscription on target SIM or  User consent is required for resolving port index. Ask user's consent first");
            Intent intent = new Intent();
            addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_NO_PRIVILEGES", str, 0, false, EuiccOperation.forDownloadNoPrivilegesOrDeactivateSimCheckMetadata(clearCallingIdentity, downloadableSubscription, z, str), i, i4, z);
            lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 1, intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DownloadSubscriptionGetMetadataCommandCallback extends GetMetadataCommandCallback {
        private final boolean mForceDeactivateSim;
        private final int mPortIndex;
        private final boolean mSwitchAfterDownload;
        private final boolean mWithUserConsent;

        DownloadSubscriptionGetMetadataCommandCallback(long j, DownloadableSubscription downloadableSubscription, boolean z, String str, boolean z2, PendingIntent pendingIntent, boolean z3, int i) {
            super(j, downloadableSubscription, str, pendingIntent);
            this.mSwitchAfterDownload = z;
            this.mForceDeactivateSim = z2;
            this.mWithUserConsent = z3;
            this.mPortIndex = i;
        }

        @Override // com.android.internal.telephony.euicc.EuiccController.GetMetadataCommandCallback, com.android.internal.telephony.euicc.EuiccConnector.GetMetadataCommandCallback
        public void onGetMetadataComplete(int i, GetDownloadableSubscriptionMetadataResult getDownloadableSubscriptionMetadataResult) {
            DownloadableSubscription downloadableSubscription = getDownloadableSubscriptionMetadataResult.getDownloadableSubscription();
            if (this.mWithUserConsent) {
                if (getDownloadableSubscriptionMetadataResult.getResult() != 0) {
                    super.onGetMetadataComplete(i, getDownloadableSubscriptionMetadataResult);
                } else if (EuiccController.this.checkCarrierPrivilegeInMetadata(downloadableSubscription, this.mCallingPackage)) {
                    EuiccController.this.downloadSubscriptionPrivileged(i, this.mPortIndex, this.mCallingToken, downloadableSubscription, this.mSwitchAfterDownload, this.mForceDeactivateSim, this.mCallingPackage, null, this.mCallbackIntent);
                } else {
                    Log.e("EuiccController", "Caller does not have carrier privilege in metadata.");
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, 2, null);
                }
            } else if (getDownloadableSubscriptionMetadataResult.getResult() == -1) {
                Intent intent = new Intent();
                EuiccController euiccController = EuiccController.this;
                String str = this.mCallingPackage;
                euiccController.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM", str, 0, false, EuiccOperation.forDownloadNoPrivilegesOrDeactivateSimCheckMetadata(this.mCallingToken, this.mSubscription, this.mSwitchAfterDownload, str), i, this.mPortIndex, this.mSwitchAfterDownload);
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, 1, intent);
            } else if (getDownloadableSubscriptionMetadataResult.getResult() != 0) {
                super.onGetMetadataComplete(i, getDownloadableSubscriptionMetadataResult);
            } else if (EuiccController.this.checkCarrierPrivilegeInMetadata(downloadableSubscription, this.mCallingPackage)) {
                EuiccController.this.downloadSubscriptionPrivileged(i, this.mPortIndex, this.mCallingToken, downloadableSubscription, this.mSwitchAfterDownload, this.mForceDeactivateSim, this.mCallingPackage, null, this.mCallbackIntent);
            } else {
                Log.e("EuiccController", "Caller is not permitted to download this profile per metadata");
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, 2, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadSubscriptionPrivilegedCheckMetadata(int i, int i2, long j, DownloadableSubscription downloadableSubscription, boolean z, boolean z2, String str, Bundle bundle, PendingIntent pendingIntent) {
        Log.d("EuiccController", " downloadSubscriptionPrivilegedCheckMetadata cardId: " + i + " switchAfterDownload: " + z + " portIndex: " + i2 + " forceDeactivateSim: " + z2);
        this.mConnector.getDownloadableSubscriptionMetadata(i, i2, downloadableSubscription, z, z2, new DownloadSubscriptionGetMetadataCommandCallback(j, downloadableSubscription, z, str, z2, pendingIntent, true, i2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void downloadSubscriptionPrivileged(final int i, final int i2, final long j, final DownloadableSubscription downloadableSubscription, final boolean z, boolean z2, final String str, Bundle bundle, final PendingIntent pendingIntent) {
        this.mConnector.downloadSubscription(i, i2, downloadableSubscription, z, z2, bundle, new EuiccConnector.DownloadCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.2
            @Override // com.android.internal.telephony.euicc.EuiccConnector.DownloadCommandCallback
            public void onDownloadComplete(DownloadSubscriptionResult downloadSubscriptionResult) {
                Intent intent = new Intent();
                int result = downloadSubscriptionResult.getResult();
                int i3 = 1;
                if (result == -2) {
                    boolean z3 = !TextUtils.isEmpty(downloadableSubscription.getConfirmationCode());
                    if (downloadSubscriptionResult.getResolvableErrors() != 0) {
                        EuiccController.this.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_RESOLVABLE_ERRORS", str, downloadSubscriptionResult.getResolvableErrors(), z3, EuiccOperation.forDownloadResolvableErrors(j, downloadableSubscription, z, str, downloadSubscriptionResult.getResolvableErrors()), i, i2, z);
                    } else {
                        EuiccController euiccController = EuiccController.this;
                        String str2 = str;
                        euiccController.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_CONFIRMATION_CODE", str2, 0, z3, EuiccOperation.forDownloadConfirmationCode(j, downloadableSubscription, z, str2), i, i2, z);
                    }
                } else if (result == -1) {
                    EuiccController euiccController2 = EuiccController.this;
                    String str3 = str;
                    euiccController2.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM", str3, 0, false, EuiccOperation.forDownloadDeactivateSim(j, downloadableSubscription, z, str3), i, i2, z);
                } else if (result == 0) {
                    Settings.Global.putInt(EuiccController.this.mContext.getContentResolver(), "euicc_provisioned", 1);
                    intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION", downloadableSubscription);
                    i3 = 0;
                    if (!z) {
                        EuiccController.this.refreshSubscriptionsAndSendResult(pendingIntent, 0, intent);
                        return;
                    }
                } else {
                    EuiccController.this.addExtrasToResultIntent(intent, downloadSubscriptionResult.getResult());
                    i3 = 2;
                }
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, i3, intent);
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            }
        });
    }

    public GetEuiccProfileInfoListResult blockingGetEuiccProfileInfoList(int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        this.mConnector.getEuiccProfileInfoList(i, new EuiccConnector.GetEuiccProfileInfoListCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.3
            @Override // com.android.internal.telephony.euicc.EuiccConnector.GetEuiccProfileInfoListCommandCallback
            public void onListComplete(GetEuiccProfileInfoListResult getEuiccProfileInfoListResult) {
                atomicReference.set(getEuiccProfileInfoListResult);
                countDownLatch.countDown();
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e("EuiccController", "blockingGetEuiccInfoFromEuiccService got InterruptedException e: " + e);
            Thread.currentThread().interrupt();
        }
        return (GetEuiccProfileInfoListResult) atomicReference.get();
    }

    public void getDefaultDownloadableSubscriptionList(int i, String str, PendingIntent pendingIntent) {
        getDefaultDownloadableSubscriptionList(i, false, str, pendingIntent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDefaultDownloadableSubscriptionList(int i, boolean z, String str, PendingIntent pendingIntent) {
        Log.d("EuiccController", " getDefaultDownloadableSubscriptionList callingPackage: " + str);
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to get default list");
        }
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mConnector.getDefaultDownloadableSubscriptionList(i, z, new GetDefaultListCommandCallback(clearCallingIdentity, str, pendingIntent));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class GetDefaultListCommandCallback implements EuiccConnector.GetDefaultListCommandCallback {
        final PendingIntent mCallbackIntent;
        final String mCallingPackage;
        final long mCallingToken;

        GetDefaultListCommandCallback(long j, String str, PendingIntent pendingIntent) {
            this.mCallingToken = j;
            this.mCallingPackage = str;
            this.mCallbackIntent = pendingIntent;
        }

        @Override // com.android.internal.telephony.euicc.EuiccConnector.GetDefaultListCommandCallback
        public void onGetDefaultListComplete(int i, GetDefaultDownloadableSubscriptionListResult getDefaultDownloadableSubscriptionListResult) {
            int i2;
            Intent intent = new Intent();
            int result = getDefaultDownloadableSubscriptionListResult.getResult();
            if (result == -1) {
                EuiccController euiccController = EuiccController.this;
                String str = this.mCallingPackage;
                euiccController.addResolutionIntentWithPort(intent, "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM", str, 0, false, EuiccOperation.forGetDefaultListDeactivateSim(this.mCallingToken, str), i, 0, false);
                i2 = 1;
            } else if (result == 0) {
                List downloadableSubscriptions = getDefaultDownloadableSubscriptionListResult.getDownloadableSubscriptions();
                if (downloadableSubscriptions != null && downloadableSubscriptions.size() > 0) {
                    intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTIONS", (Parcelable[]) downloadableSubscriptions.toArray(new DownloadableSubscription[downloadableSubscriptions.size()]));
                }
                i2 = 0;
            } else {
                EuiccController.this.addExtrasToResultIntent(intent, getDefaultDownloadableSubscriptionListResult.getResult());
                i2 = 2;
            }
            EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, i2, intent);
        }

        @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
        public void onEuiccServiceUnavailable() {
            EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(this.mCallbackIntent, 2, null);
        }
    }

    public EuiccInfo getEuiccInfo(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return blockingGetEuiccInfoFromEuiccService(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void deleteSubscription(int i, int i2, String str, PendingIntent pendingIntent) {
        boolean callerCanWriteEmbeddedSubscriptions = callerCanWriteEmbeddedSubscriptions();
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfo subscriptionForSubscriptionId = getSubscriptionForSubscriptionId(i2);
            if (subscriptionForSubscriptionId == null) {
                Log.e("EuiccController", "Cannot delete nonexistent subscription: " + i2);
                lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            } else if (!callerCanWriteEmbeddedSubscriptions && !this.mSubscriptionManager.canManageSubscription(subscriptionForSubscriptionId, str)) {
                Log.e("EuiccController", "No permissions: " + i2);
                lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            } else {
                deleteSubscriptionPrivileged(i, subscriptionForSubscriptionId.getIccId(), pendingIntent);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    void deleteSubscriptionPrivileged(int i, String str, final PendingIntent pendingIntent) {
        this.mConnector.deleteSubscription(i, str, new EuiccConnector.DeleteCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.4
            @Override // com.android.internal.telephony.euicc.EuiccConnector.DeleteCommandCallback
            public void onDeleteComplete(int i2) {
                Intent intent = new Intent();
                if (i2 == 0) {
                    EuiccController.this.refreshSubscriptionsAndSendResult(pendingIntent, 0, intent);
                    return;
                }
                EuiccController.this.addExtrasToResultIntent(intent, i2);
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, intent);
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            }
        });
    }

    public void switchToSubscription(int i, int i2, String str, PendingIntent pendingIntent) {
        switchToSubscription(i, i2, 0, false, str, pendingIntent, false);
    }

    public void switchToSubscriptionWithPort(int i, int i2, int i3, String str, PendingIntent pendingIntent) {
        switchToSubscription(i, i2, i3, false, str, pendingIntent, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00f8 A[Catch: all -> 0x0182, TryCatch #0 {all -> 0x0182, blocks: (B:9:0x0072, B:11:0x0078, B:16:0x0087, B:19:0x008e, B:58:0x0147, B:61:0x015d, B:23:0x00a2, B:25:0x00a8, B:39:0x00f2, B:41:0x00f8, B:43:0x00fe, B:48:0x011e, B:54:0x012a, B:30:0x00c7, B:32:0x00cf, B:35:0x00ea), top: B:67:0x006e }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x011c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void switchToSubscription(int i, int i2, int i3, boolean z, String str, PendingIntent pendingIntent, boolean z2) {
        boolean z3;
        boolean z4;
        int i4;
        String str2;
        boolean z5;
        int i5 = i3;
        boolean z6 = z2;
        boolean callerCanWriteEmbeddedSubscriptions = callerCanWriteEmbeddedSubscriptions();
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        boolean isCompatChangeEnabled = isCompatChangeEnabled(str, 224562872L);
        Log.d("EuiccController", " subId: " + i2 + " portIndex: " + i5 + " forceDeactivateSim: " + z + " usePortIndex: " + z6 + " callingPackage: " + str + " shouldResolvePortIndex: " + isCompatChangeEnabled);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z7 = callerCanWriteEmbeddedSubscriptions ? true : z;
        try {
            if (i2 == -1) {
                if (!z6) {
                    i5 = getResolvedPortIndexForDisableSubscription(i, str, callerCanWriteEmbeddedSubscriptions);
                    if (i5 == -1) {
                        Log.e("EuiccController", "Disable is not permitted: no active subscription or cannot manage subscription");
                        lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                        return;
                    }
                    z6 = true;
                }
                if (!callerCanWriteEmbeddedSubscriptions && !canManageActiveSubscriptionOnTargetSim(i, str, z6, i5)) {
                    Log.e("EuiccController", "Not permitted to switch to empty subscription");
                    lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                    return;
                }
                z5 = z6;
                str2 = null;
                z3 = true;
                z4 = false;
                i4 = i5;
            } else {
                SubscriptionInfo subscriptionForSubscriptionId = getSubscriptionForSubscriptionId(i2);
                if (subscriptionForSubscriptionId == null) {
                    Log.e("EuiccController", "Cannot switch to nonexistent sub: " + i2);
                    lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                    return;
                }
                if (!callerCanWriteEmbeddedSubscriptions) {
                    if (!this.mSubscriptionManager.canManageSubscription(subscriptionForSubscriptionId, str)) {
                        Log.e("EuiccController", "Not permitted to switch to sub: " + i2);
                        lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                        return;
                    } else if (!canManageSubscriptionOnTargetSim(i, str, z6, i5)) {
                        z3 = false;
                        String iccId = subscriptionForSubscriptionId.getIccId();
                        if (z6) {
                            int resolvedPortIndexForSubscriptionSwitch = isCompatChangeEnabled ? getResolvedPortIndexForSubscriptionSwitch(i) : 0;
                            z4 = resolvedPortIndexForSubscriptionSwitch == -1;
                            Log.d("EuiccController", " Resolved portIndex: " + resolvedPortIndexForSubscriptionSwitch);
                            i4 = resolvedPortIndexForSubscriptionSwitch;
                            str2 = iccId;
                            z5 = true;
                        } else if (!isTargetPortIndexValid(i, i5)) {
                            Log.e("EuiccController", "Not permitted to switch to invalid portIndex");
                            Intent intent = new Intent();
                            intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_ERROR_CODE", 10017);
                            lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, intent);
                            return;
                        } else {
                            i4 = i5;
                            z5 = z6;
                            str2 = iccId;
                            z4 = false;
                        }
                    }
                }
                z3 = true;
                String iccId2 = subscriptionForSubscriptionId.getIccId();
                if (z6) {
                }
            }
            if (z3 && !z4) {
                switchToSubscriptionPrivileged(i, i4, clearCallingIdentity, i2, str2, z7, str, pendingIntent, z5);
                return;
            }
            Intent intent2 = new Intent();
            addResolutionIntent(intent2, "android.service.euicc.action.RESOLVE_NO_PRIVILEGES", str, 0, false, EuiccOperation.forSwitchNoPrivileges(clearCallingIdentity, i2, str), i, i4, z5, i2);
            lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 1, intent2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public int getResolvedPortIndexForDisableSubscription(int i, String str, boolean z) {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size() != 0) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (i == -1 || subscriptionInfo.getCardId() == i) {
                    if (subscriptionInfo.isEmbedded() && (z || this.mSubscriptionManager.canManageSubscription(subscriptionInfo, str))) {
                        return subscriptionInfo.getPortIndex();
                    }
                }
            }
        }
        return -1;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public int getResolvedPortIndexForSubscriptionSwitch(int i) {
        int[] portList;
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex;
        int slotIndexFromCardId = getSlotIndexFromCardId(i);
        UiccSlot uiccSlot = UiccController.getInstance().getUiccSlot(slotIndexFromCardId);
        int i2 = 0;
        if (uiccSlot == null) {
            Log.d("EuiccController", "Switch to inactive slot, return default port index. slotIndex: " + slotIndexFromCardId);
            return 0;
        } else if (!uiccSlot.isMultipleEnabledProfileSupported()) {
            Log.d("EuiccController", "Multiple enabled profiles is not supported, return default port index");
            return 0;
        } else {
            boolean z = getRemovableNonEuiccSlot() != null && getRemovableNonEuiccSlot().isActive();
            if (this.mTelephonyManager.getActiveModemCount() == 1) {
                if (z) {
                    return 0;
                }
                int[] portList2 = uiccSlot.getPortList();
                int length = portList2.length;
                while (i2 < length) {
                    int i3 = portList2[i2];
                    if (uiccSlot.isPortActive(i3)) {
                        return i3;
                    }
                    i2++;
                }
                return -1;
            }
            for (int i4 : uiccSlot.getPortList()) {
                if (uiccSlot.isPortActive(i4) && ((activeSubscriptionInfoForSimSlotIndex = this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(uiccSlot.getPhoneIdFromPortIndex(i4))) == null || activeSubscriptionInfoForSimSlotIndex.isOpportunistic())) {
                    return i4;
                }
            }
            if (z && !isRemovalNonEuiccSlotHasActiveSubscription()) {
                i2 = 1;
            }
            if (i2 != 0) {
                return getNextAvailableInActivePortIndex(uiccSlot);
            }
            return -1;
        }
    }

    private boolean isTargetPortIndexValid(int i, int i2) {
        int[] portList;
        int[] portList2;
        if (i2 < 0) {
            Log.e("EuiccController", "Invalid portIndex: " + i2);
            return false;
        }
        UiccSlot uiccSlot = UiccController.getInstance().getUiccSlot(getSlotIndexFromCardId(i));
        if (uiccSlot == null || uiccSlot.getPortList().length == 0 || i2 >= uiccSlot.getPortList().length) {
            Log.e("EuiccController", "Invalid portIndex");
            return false;
        }
        if (this.mTelephonyManager.getActiveModemCount() == 1) {
            for (int i3 : uiccSlot.getPortList()) {
                if (uiccSlot.isPortActive(i3) && i3 != i2) {
                    Log.e("EuiccController", "In SS Mode, slot already has active port on portIndex " + i3 + " , reject the switch request to portIndex " + i2);
                    return false;
                }
            }
        } else if (this.mTelephonyManager.getActiveModemCount() > 1 && isRemovalNonEuiccSlotHasActiveSubscription()) {
            for (int i4 : uiccSlot.getPortList()) {
                if (uiccSlot.isPortActive(i4) && this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(uiccSlot.getPhoneIdFromPortIndex(i4)) == null && i4 != i2) {
                    Log.e("EuiccController", "In DSDS Mode, pSim has active subscription, eSim has empty active port on portIndex " + i4 + " , reject the switch request to portIndex " + i2);
                    return false;
                }
            }
        }
        return true;
    }

    private int getNextAvailableInActivePortIndex(UiccSlot uiccSlot) {
        int[] portList;
        if (uiccSlot != null) {
            for (int i : uiccSlot.getPortList()) {
                if (!uiccSlot.isPortActive(i)) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    private int getSlotIndexFromCardId(int i) {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null || uiccSlotsInfo.length == 0) {
            Log.e("EuiccController", "UiccSlotInfo is null or empty");
            return -1;
        }
        String convertToCardString = UiccController.getInstance().convertToCardString(i);
        for (int i2 = 0; i2 < uiccSlotsInfo.length; i2++) {
            if (uiccSlotsInfo[i2] == null) {
                AnomalyReporter.reportAnomaly(UUID.fromString("e9517acf-e1a1-455f-9231-1b5515a0d0eb"), "EuiccController: Found UiccSlotInfo Null object.");
            }
            UiccSlotInfo uiccSlotInfo = uiccSlotsInfo[i2];
            if (IccUtils.compareIgnoreTrailingFs(convertToCardString, uiccSlotInfo != null ? uiccSlotInfo.getCardId() : null)) {
                return i2;
            }
        }
        Log.i("EuiccController", "No UiccSlotInfo found for cardId: " + i);
        return -1;
    }

    private boolean isRemovalNonEuiccSlotHasActiveSubscription() {
        int[] portList;
        UiccSlot removableNonEuiccSlot = getRemovableNonEuiccSlot();
        if (removableNonEuiccSlot != null) {
            for (int i : removableNonEuiccSlot.getPortList()) {
                if (removableNonEuiccSlot.isPortActive(i) && this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(removableNonEuiccSlot.getPhoneIdFromPortIndex(i)) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private UiccSlot getRemovableNonEuiccSlot() {
        UiccSlot[] uiccSlots = UiccController.getInstance().getUiccSlots();
        if (uiccSlots != null) {
            for (int i = 0; i < uiccSlots.length; i++) {
                UiccSlot uiccSlot = uiccSlots[i];
                if (uiccSlot != null && uiccSlot.isRemovable() && !uiccSlots[i].isEuicc()) {
                    return uiccSlots[i];
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void switchToSubscriptionPrivileged(int i, int i2, long j, int i3, boolean z, String str, PendingIntent pendingIntent, boolean z2) {
        SubscriptionInfo subscriptionForSubscriptionId = getSubscriptionForSubscriptionId(i3);
        switchToSubscriptionPrivileged(i, i2, j, i3, subscriptionForSubscriptionId != null ? subscriptionForSubscriptionId.getIccId() : null, z, str, pendingIntent, z2);
    }

    void switchToSubscriptionPrivileged(final int i, final int i2, final long j, final int i3, String str, boolean z, final String str2, final PendingIntent pendingIntent, final boolean z2) {
        this.mConnector.switchToSubscription(i, i2, str, z, new EuiccConnector.SwitchCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.5
            @Override // com.android.internal.telephony.euicc.EuiccConnector.SwitchCommandCallback
            public void onSwitchComplete(int i4) {
                int i5;
                Intent intent = new Intent();
                if (i4 == -1) {
                    EuiccController euiccController = EuiccController.this;
                    String str3 = str2;
                    euiccController.addResolutionIntent(intent, "android.service.euicc.action.RESOLVE_DEACTIVATE_SIM", str3, 0, false, EuiccOperation.forSwitchDeactivateSim(j, i3, str3), i, i2, z2, i3);
                    i5 = 1;
                } else if (i4 != 0) {
                    EuiccController.this.addExtrasToResultIntent(intent, i4);
                    i5 = 2;
                } else {
                    i5 = 0;
                }
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, i5, intent);
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            }
        }, z2);
    }

    public void updateSubscriptionNickname(int i, int i2, String str, String str2, final PendingIntent pendingIntent) {
        boolean callerCanWriteEmbeddedSubscriptions = callerCanWriteEmbeddedSubscriptions();
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str2);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfo subscriptionForSubscriptionId = getSubscriptionForSubscriptionId(i2);
            if (subscriptionForSubscriptionId == null) {
                Log.e("EuiccController", "Cannot update nickname to nonexistent sub: " + i2);
                lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            } else if (!callerCanWriteEmbeddedSubscriptions && !this.mSubscriptionManager.canManageSubscription(subscriptionForSubscriptionId, str2)) {
                Log.e("EuiccController", "No permissions: " + i2);
                lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
            } else {
                this.mConnector.updateSubscriptionNickname(i, subscriptionForSubscriptionId.getIccId(), str, new EuiccConnector.UpdateNicknameCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.6
                    @Override // com.android.internal.telephony.euicc.EuiccConnector.UpdateNicknameCommandCallback
                    public void onUpdateNicknameComplete(int i3) {
                        Intent intent = new Intent();
                        if (i3 == 0) {
                            EuiccController.this.refreshSubscriptionsAndSendResult(pendingIntent, 0, intent);
                            return;
                        }
                        EuiccController.this.addExtrasToResultIntent(intent, i3);
                        EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, intent);
                    }

                    @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
                    public void onEuiccServiceUnavailable() {
                        EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                    }
                });
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void eraseSubscriptions(int i, final PendingIntent pendingIntent) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to erase subscriptions");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mConnector.eraseSubscriptions(i, new EuiccConnector.EraseCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.7
                @Override // com.android.internal.telephony.euicc.EuiccConnector.EraseCommandCallback
                public void onEraseComplete(int i2) {
                    Intent intent = new Intent();
                    if (i2 == 0) {
                        EuiccController.this.refreshSubscriptionsAndSendResult(pendingIntent, 0, intent);
                        return;
                    }
                    EuiccController.this.addExtrasToResultIntent(intent, i2);
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, intent);
                }

                @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
                public void onEuiccServiceUnavailable() {
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void eraseSubscriptionsWithOptions(int i, int i2, final PendingIntent pendingIntent) {
        if (!callerCanWriteEmbeddedSubscriptions()) {
            throw new SecurityException("Must have WRITE_EMBEDDED_SUBSCRIPTIONS to erase subscriptions");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mConnector.eraseSubscriptionsWithOptions(i, i2, new EuiccConnector.EraseCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.8
                @Override // com.android.internal.telephony.euicc.EuiccConnector.EraseCommandCallback
                public void onEraseComplete(int i3) {
                    Intent intent = new Intent();
                    if (i3 == 0) {
                        EuiccController.this.refreshSubscriptionsAndSendResult(pendingIntent, 0, intent);
                        return;
                    }
                    EuiccController.this.addExtrasToResultIntent(intent, i3);
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, intent);
                }

                @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
                public void onEuiccServiceUnavailable() {
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void retainSubscriptionsForFactoryReset(int i, final PendingIntent pendingIntent) {
        this.mContext.enforceCallingPermission("android.permission.MASTER_CLEAR", "Must have MASTER_CLEAR to retain subscriptions for factory reset");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mConnector.retainSubscriptions(i, new EuiccConnector.RetainSubscriptionsCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.9
                @Override // com.android.internal.telephony.euicc.EuiccConnector.RetainSubscriptionsCommandCallback
                public void onRetainSubscriptionsComplete(int i2) {
                    int i3;
                    Intent intent = new Intent();
                    if (i2 != 0) {
                        EuiccController.this.addExtrasToResultIntent(intent, i2);
                        i3 = 2;
                    } else {
                        i3 = 0;
                    }
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, i3, intent);
                }

                @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
                public void onEuiccServiceUnavailable() {
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, 2, null);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void refreshSubscriptionsAndSendResult(final PendingIntent pendingIntent, final int i, final Intent intent) {
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionManagerService.getInstance().updateEmbeddedSubscriptions(List.of(Integer.valueOf(this.mTelephonyManager.getCardIdForDefaultEuicc())), new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$0(pendingIntent, i, intent);
                }
            });
        } else {
            SubscriptionController.getInstance().requestEmbeddedSubscriptionInfoListRefresh(new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    EuiccController.this.lambda$refreshSubscriptionsAndSendResult$1(pendingIntent, i, intent);
                }
            });
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* renamed from: sendResult */
    public void lambda$refreshSubscriptionsAndSendResult$1(PendingIntent pendingIntent, int i, Intent intent) {
        try {
            pendingIntent.send(this.mContext, i, intent);
        } catch (PendingIntent.CanceledException unused) {
        }
    }

    public void addResolutionIntentWithPort(Intent intent, String str, String str2, int i, boolean z, EuiccOperation euiccOperation, int i2, int i3, boolean z2) {
        addResolutionIntent(intent, str, str2, i, z, euiccOperation, i2, i3, z2, -1);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void addResolutionIntent(Intent intent, String str, String str2, int i, boolean z, EuiccOperation euiccOperation, int i2, int i3, boolean z2, int i4) {
        Intent intent2 = new Intent("android.telephony.euicc.action.RESOLVE_ERROR");
        intent2.setPackage("com.android.phone");
        intent2.setComponent(new ComponentName("com.android.phone", "com.android.phone.euicc.EuiccResolutionUiDispatcherActivity"));
        intent2.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_ACTION", str);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_CALLING_PACKAGE", str2);
        intent2.putExtra("android.service.euicc.extra.RESOLVABLE_ERRORS", i);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_CARD_ID", i2);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_SUBSCRIPTION_ID", i4);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_PORT_INDEX", i3);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_USE_PORT_INDEX", z2);
        intent2.putExtra("android.service.euicc.extra.RESOLUTION_CONFIRMATION_CODE_RETRIED", z);
        intent2.putExtra(EXTRA_OPERATION, euiccOperation);
        intent.putExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_INTENT", PendingIntent.getActivity(this.mContext, 0, intent2, 1107296256));
    }

    public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "Requires DUMP");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        printWriter.println("===== BEGIN EUICC CLINIC =====");
        try {
            try {
                printWriter.println("===== EUICC CONNECTOR =====");
                this.mConnector.dump(fileDescriptor, printWriter, strArr);
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                this.mConnector.dumpEuiccService(new EuiccConnector.DumpEuiccServiceCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.10
                    @Override // com.android.internal.telephony.euicc.EuiccConnector.DumpEuiccServiceCommandCallback
                    public void onDumpEuiccServiceComplete(String str) {
                        printWriter.println("===== EUICC SERVICE =====");
                        printWriter.println(str);
                        countDownLatch.countDown();
                    }

                    @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
                    public void onEuiccServiceUnavailable() {
                        printWriter.println("===== EUICC SERVICE UNAVAILABLE =====");
                        countDownLatch.countDown();
                    }
                });
                if (!countDownLatch.await(5L, TimeUnit.SECONDS)) {
                    printWriter.println("===== EUICC SERVICE TIMEOUT =====");
                }
            } catch (InterruptedException unused) {
                printWriter.println("===== EUICC SERVICE INTERRUPTED =====");
            }
        } finally {
            printWriter.println("===== END EUICC CLINIC =====");
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void sendOtaStatusChangedBroadcast() {
        Intent intent = new Intent("android.telephony.euicc.action.OTA_STATUS_CHANGED");
        ComponentInfo findBestComponent = EuiccConnector.findBestComponent(this.mContext.getPackageManager());
        if (findBestComponent != null) {
            intent.setPackage(findBestComponent.packageName);
        }
        this.mContext.sendBroadcast(intent, "android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS");
    }

    private SubscriptionInfo getSubscriptionForSubscriptionId(int i) {
        List availableSubscriptionInfoList = this.mSubscriptionManager.getAvailableSubscriptionInfoList();
        int size = availableSubscriptionInfoList != null ? availableSubscriptionInfoList.size() : 0;
        for (int i2 = 0; i2 < size; i2++) {
            SubscriptionInfo subscriptionInfo = (SubscriptionInfo) availableSubscriptionInfoList.get(i2);
            if (i == subscriptionInfo.getSubscriptionId()) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private String blockingGetEidFromEuiccService(int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        this.mConnector.getEid(i, new EuiccConnector.GetEidCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.11
            @Override // com.android.internal.telephony.euicc.EuiccConnector.GetEidCommandCallback
            public void onGetEidComplete(String str) {
                atomicReference.set(str);
                countDownLatch.countDown();
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                countDownLatch.countDown();
            }
        });
        return (String) awaitResult(countDownLatch, atomicReference);
    }

    private int blockingGetOtaStatusFromEuiccService(int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference(5);
        this.mConnector.getOtaStatus(i, new EuiccConnector.GetOtaStatusCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.12
            @Override // com.android.internal.telephony.euicc.EuiccConnector.GetOtaStatusCommandCallback
            public void onGetOtaStatusComplete(int i2) {
                atomicReference.set(Integer.valueOf(i2));
                countDownLatch.countDown();
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                countDownLatch.countDown();
            }
        });
        return ((Integer) awaitResult(countDownLatch, atomicReference)).intValue();
    }

    private EuiccInfo blockingGetEuiccInfoFromEuiccService(int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        this.mConnector.getEuiccInfo(i, new EuiccConnector.GetEuiccInfoCommandCallback() { // from class: com.android.internal.telephony.euicc.EuiccController.13
            @Override // com.android.internal.telephony.euicc.EuiccConnector.GetEuiccInfoCommandCallback
            public void onGetEuiccInfoComplete(EuiccInfo euiccInfo) {
                atomicReference.set(euiccInfo);
                countDownLatch.countDown();
            }

            @Override // com.android.internal.telephony.euicc.EuiccConnector.BaseEuiccCommandCallback
            public void onEuiccServiceUnavailable() {
                countDownLatch.countDown();
            }
        });
        return (EuiccInfo) awaitResult(countDownLatch, atomicReference);
    }

    private static <T> T awaitResult(CountDownLatch countDownLatch, AtomicReference<T> atomicReference) {
        try {
            countDownLatch.await();
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
        }
        return atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkCarrierPrivilegeInMetadata(DownloadableSubscription downloadableSubscription, String str) {
        List accessRules = downloadableSubscription.getAccessRules();
        UiccAccessRule[] uiccAccessRuleArr = accessRules != null ? (UiccAccessRule[]) accessRules.toArray(new UiccAccessRule[accessRules.size()]) : null;
        if (uiccAccessRuleArr == null) {
            Log.e("EuiccController", "No access rules but caller is unprivileged");
            return false;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 134217728);
            for (UiccAccessRule uiccAccessRule : uiccAccessRuleArr) {
                if (uiccAccessRule.getCarrierPrivilegeStatus(packageInfo) == 1) {
                    Log.i("EuiccController", "Calling package has carrier privilege to this profile");
                    return true;
                }
            }
            Log.e("EuiccController", "Calling package doesn't have carrier privilege to this profile");
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("EuiccController", "Calling package valid but gone");
            return false;
        }
    }

    private boolean supportMultiActiveSlots() {
        return this.mTelephonyManager.getSupportedModemCount() > 1;
    }

    private boolean canManageActiveSubscriptionOnTargetSim(int i, String str, boolean z, int i2) {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size() != 0) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (i == -1 || subscriptionInfo.getCardId() == i) {
                    if (subscriptionInfo.isEmbedded() && (!z || subscriptionInfo.getPortIndex() == i2)) {
                        if (this.mSubscriptionManager.canManageSubscription(subscriptionInfo, str)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean canManageSubscriptionOnTargetSim(final int i, String str, final boolean z, final int i2) {
        boolean z2;
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size() != 0) {
            if (supportMultiActiveSlots()) {
                List<UiccCardInfo> uiccCardsInfo = this.mTelephonyManager.getUiccCardsInfo();
                if (uiccCardsInfo == null || uiccCardsInfo.isEmpty()) {
                    return false;
                }
                Iterator<UiccCardInfo> it = uiccCardsInfo.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z2 = false;
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next != null && next.getCardId() == i && next.isEuicc()) {
                        z2 = true;
                        break;
                    }
                }
                if (!z2) {
                    Log.i("EuiccController", "The target SIM is not an eUICC.");
                    return false;
                } else if (activeSubscriptionInfoList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.euicc.EuiccController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$canManageSubscriptionOnTargetSim$2;
                        lambda$canManageSubscriptionOnTargetSim$2 = EuiccController.lambda$canManageSubscriptionOnTargetSim$2(i, z, i2, (SubscriptionInfo) obj);
                        return lambda$canManageSubscriptionOnTargetSim$2;
                    }
                })) {
                    for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                        if (subscriptionInfo.isEmbedded() && subscriptionInfo.getCardId() == i && (!z || subscriptionInfo.getPortIndex() == i2)) {
                            if (this.mSubscriptionManager.canManageSubscription(subscriptionInfo, str)) {
                                return true;
                            }
                        }
                    }
                    Log.i("EuiccController", "canManageSubscriptionOnTargetSim cannot manage embedded subscription");
                    return false;
                } else {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        return this.mTelephonyManager.checkCarrierPrivilegesForPackageAnyPhone(str) == 1;
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
            }
            for (SubscriptionInfo subscriptionInfo2 : activeSubscriptionInfoList) {
                if (subscriptionInfo2.isEmbedded() && this.mSubscriptionManager.canManageSubscription(subscriptionInfo2, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$canManageSubscriptionOnTargetSim$2(int i, boolean z, int i2, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.isEmbedded() && subscriptionInfo.getCardId() == i && (!z || subscriptionInfo.getPortIndex() == i2);
    }

    private boolean callerCanReadPhoneStatePrivileged() {
        return this.mContext.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE") == 0;
    }

    private boolean callerCanWriteEmbeddedSubscriptions() {
        return this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS") == 0;
    }

    public boolean isSimPortAvailable(int i, int i2, String str) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        boolean isCompatChangeEnabled = isCompatChangeEnabled(str, 240273417L);
        boolean callerCanWriteEmbeddedSubscriptions = callerCanWriteEmbeddedSubscriptions();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Iterator<UiccCardInfo> it = this.mTelephonyManager.getUiccCardsInfo().iterator();
            while (true) {
                if (!it.hasNext()) {
                    return false;
                }
                UiccCardInfo next = it.next();
                if (next != null && next.getCardId() == i) {
                    if (!next.isEuicc() || i2 == -1 || i2 >= next.getPorts().size()) {
                        break;
                    }
                    for (UiccPortInfo uiccPortInfo : next.getPorts()) {
                        if (uiccPortInfo != null && uiccPortInfo.getPortIndex() == i2) {
                            if (!uiccPortInfo.isActive()) {
                                if (isCompatChangeEnabled) {
                                    boolean z = getRemovableNonEuiccSlot() != null && getRemovableNonEuiccSlot().isActive();
                                    boolean z2 = this.mTelephonyManager.checkCarrierPrivilegesForPackageAnyPhone(str) == 1;
                                    if (this.mTelephonyManager.isMultiSimEnabled() && z && !isRemovalNonEuiccSlotHasActiveSubscription() && (z2 || callerCanWriteEmbeddedSubscriptions)) {
                                        r6 = true;
                                    }
                                    return r6;
                                }
                                return false;
                            } else if (TextUtils.isEmpty(uiccPortInfo.getIccId())) {
                                return true;
                            } else {
                                UiccPort uiccPortForSlot = UiccController.getInstance().getUiccPortForSlot(next.getPhysicalSlotIndex(), i2);
                                if (uiccPortForSlot == null || uiccPortForSlot.getUiccProfile() == null || !uiccPortForSlot.getUiccProfile().isEmptyProfile()) {
                                    Phone phone = PhoneFactory.getPhone(uiccPortInfo.getLogicalSlotIndex());
                                    if (phone == null) {
                                        Log.e("EuiccController", "Invalid logical slot: " + uiccPortInfo.getLogicalSlotIndex());
                                        return false;
                                    }
                                    CarrierPrivilegesTracker carrierPrivilegesTracker = phone.getCarrierPrivilegesTracker();
                                    if (carrierPrivilegesTracker != null) {
                                        return carrierPrivilegesTracker.getCarrierPrivilegeStatusForPackage(str) == 1;
                                    }
                                    Log.e("EuiccController", "No CarrierPrivilegesTracker");
                                    return false;
                                }
                                return true;
                            }
                        }
                    }
                    continue;
                }
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean hasCarrierPrivilegesForPackageOnAnyPhone(String str) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mTelephonyManager.checkCarrierPrivilegesForPackageAnyPhone(str) == 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isCompatChangeEnabled(String str, long j) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        boolean isChangeEnabled = CompatChanges.isChangeEnabled(j, str, Binder.getCallingUserHandle());
        Log.i("EuiccController", "isCompatChangeEnabled changeId: " + j + " changeEnabled: " + isChangeEnabled);
        return isChangeEnabled;
    }
}
