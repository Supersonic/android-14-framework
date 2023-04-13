package android.telephony.euicc;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.euicc.IEuiccController;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class EuiccManager {
    @SystemApi
    public static final String ACTION_CONVERT_TO_EMBEDDED_SUBSCRIPTION = "android.telephony.euicc.action.CONVERT_TO_EMBEDDED_SUBSCRIPTION";
    @SystemApi
    public static final String ACTION_DELETE_SUBSCRIPTION_PRIVILEGED = "android.telephony.euicc.action.DELETE_SUBSCRIPTION_PRIVILEGED";
    public static final String ACTION_MANAGE_EMBEDDED_SUBSCRIPTIONS = "android.telephony.euicc.action.MANAGE_EMBEDDED_SUBSCRIPTIONS";
    public static final String ACTION_NOTIFY_CARRIER_SETUP_INCOMPLETE = "android.telephony.euicc.action.NOTIFY_CARRIER_SETUP_INCOMPLETE";
    @SystemApi
    public static final String ACTION_OTA_STATUS_CHANGED = "android.telephony.euicc.action.OTA_STATUS_CHANGED";
    @SystemApi
    public static final String ACTION_PROVISION_EMBEDDED_SUBSCRIPTION = "android.telephony.euicc.action.PROVISION_EMBEDDED_SUBSCRIPTION";
    @SystemApi
    public static final String ACTION_RENAME_SUBSCRIPTION_PRIVILEGED = "android.telephony.euicc.action.RENAME_SUBSCRIPTION_PRIVILEGED";
    public static final String ACTION_RESOLVE_ERROR = "android.telephony.euicc.action.RESOLVE_ERROR";
    public static final String ACTION_START_EUICC_ACTIVATION = "android.telephony.euicc.action.START_EUICC_ACTIVATION";
    @SystemApi
    public static final String ACTION_TOGGLE_SUBSCRIPTION_PRIVILEGED = "android.telephony.euicc.action.TOGGLE_SUBSCRIPTION_PRIVILEGED";
    @SystemApi
    public static final String ACTION_TRANSFER_EMBEDDED_SUBSCRIPTIONS = "android.telephony.euicc.action.TRANSFER_EMBEDDED_SUBSCRIPTIONS";
    public static final int EMBEDDED_SUBSCRIPTION_RESULT_ERROR = 2;
    public static final int EMBEDDED_SUBSCRIPTION_RESULT_OK = 0;
    public static final int EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR = 1;
    public static final int ERROR_ADDRESS_MISSING = 10011;
    public static final int ERROR_CARRIER_LOCKED = 10000;
    public static final int ERROR_CERTIFICATE_ERROR = 10012;
    public static final int ERROR_CONNECTION_ERROR = 10014;
    public static final int ERROR_DISALLOWED_BY_PPR = 10010;
    public static final int ERROR_EUICC_INSUFFICIENT_MEMORY = 10004;
    public static final int ERROR_EUICC_MISSING = 10006;
    public static final int ERROR_INCOMPATIBLE_CARRIER = 10003;
    public static final int ERROR_INSTALL_PROFILE = 10009;
    public static final int ERROR_INVALID_ACTIVATION_CODE = 10001;
    public static final int ERROR_INVALID_CONFIRMATION_CODE = 10002;
    public static final int ERROR_INVALID_PORT = 10017;
    public static final int ERROR_INVALID_RESPONSE = 10015;
    public static final int ERROR_NO_PROFILES_AVAILABLE = 10013;
    public static final int ERROR_OPERATION_BUSY = 10016;
    public static final int ERROR_SIM_MISSING = 10008;
    public static final int ERROR_TIME_OUT = 10005;
    public static final int ERROR_UNSUPPORTED_VERSION = 10007;
    @SystemApi
    public static final int EUICC_ACTIVATION_TYPE_ACCOUNT_REQUIRED = 4;
    @SystemApi
    public static final int EUICC_ACTIVATION_TYPE_BACKUP = 2;
    @SystemApi
    public static final int EUICC_ACTIVATION_TYPE_DEFAULT = 1;
    @SystemApi
    public static final int EUICC_ACTIVATION_TYPE_TRANSFER = 3;
    @SystemApi
    public static final int EUICC_OTA_FAILED = 2;
    @SystemApi
    public static final int EUICC_OTA_IN_PROGRESS = 1;
    @SystemApi
    public static final int EUICC_OTA_NOT_NEEDED = 4;
    @SystemApi
    public static final int EUICC_OTA_STATUS_UNAVAILABLE = 5;
    @SystemApi
    public static final int EUICC_OTA_SUCCEEDED = 3;
    @SystemApi
    public static final String EXTRA_ACTIVATION_TYPE = "android.telephony.euicc.extra.ACTIVATION_TYPE";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DETAILED_CODE";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION";
    @SystemApi
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTIONS = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTIONS";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_ERROR_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_ERROR_CODE";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_OPERATION_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_OPERATION_CODE";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_ACTION = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_ACTION";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_CALLBACK_INTENT = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_CALLBACK_INTENT";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_INTENT = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_RESOLUTION_INTENT";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_SMDX_REASON_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_REASON_CODE";
    public static final String EXTRA_EMBEDDED_SUBSCRIPTION_SMDX_SUBJECT_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_SUBJECT_CODE";
    @SystemApi
    public static final String EXTRA_ENABLE_SUBSCRIPTION = "android.telephony.euicc.extra.ENABLE_SUBSCRIPTION";
    @SystemApi
    public static final String EXTRA_FORCE_PROVISION = "android.telephony.euicc.extra.FORCE_PROVISION";
    @SystemApi
    public static final String EXTRA_FROM_SUBSCRIPTION_ID = "android.telephony.euicc.extra.FROM_SUBSCRIPTION_ID";
    @SystemApi
    public static final String EXTRA_PHYSICAL_SLOT_ID = "android.telephony.euicc.extra.PHYSICAL_SLOT_ID";
    @SystemApi
    public static final String EXTRA_SUBSCRIPTION_ID = "android.telephony.euicc.extra.SUBSCRIPTION_ID";
    @SystemApi
    public static final String EXTRA_SUBSCRIPTION_NICKNAME = "android.telephony.euicc.extra.SUBSCRIPTION_NICKNAME";
    public static final String EXTRA_USE_QR_SCANNER = "android.telephony.euicc.extra.USE_QR_SCANNER";
    public static final long INACTIVE_PORT_AVAILABILITY_CHECK = 240273417;
    public static final String META_DATA_CARRIER_ICON = "android.telephony.euicc.carriericon";
    public static final int OPERATION_APDU = 8;
    public static final int OPERATION_DOWNLOAD = 5;
    public static final int OPERATION_EUICC_CARD = 3;
    public static final int OPERATION_EUICC_GSMA = 7;
    public static final int OPERATION_HTTP = 11;
    public static final int OPERATION_METADATA = 6;
    public static final int OPERATION_SIM_SLOT = 2;
    public static final int OPERATION_SMDX = 9;
    public static final int OPERATION_SMDX_SUBJECT_REASON_CODE = 10;
    public static final int OPERATION_SWITCH = 4;
    public static final int OPERATION_SYSTEM = 1;
    public static final long SHOULD_RESOLVE_PORT_INDEX_FOR_APPS = 224562872;
    public static final long SWITCH_WITHOUT_PORT_INDEX_EXCEPTION_ON_DISABLE = 218393363;
    private static final String TAG = "EuiccManager";
    private int mCardId;
    private final Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ErrorCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EuiccActivationType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface OperationCode {
    }

    @SystemApi
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface OtaStatus {
    }

    public EuiccManager(Context context) {
        this.mContext = context;
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        this.mCardId = tm.getCardIdForDefaultEuicc();
    }

    private EuiccManager(Context context, int cardId) {
        this.mContext = context;
        this.mCardId = cardId;
    }

    public EuiccManager createForCardId(int cardId) {
        return new EuiccManager(this.mContext, cardId);
    }

    public boolean isEnabled() {
        return getIEuiccController() != null && refreshCardIdIfUninitialized();
    }

    public String getEid() {
        if (!isEnabled()) {
            return null;
        }
        try {
            return getIEuiccController().getEid(this.mCardId, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getOtaStatus() {
        if (!isEnabled()) {
            return 5;
        }
        try {
            return getIEuiccController().getOtaStatus(this.mCardId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void downloadSubscription(DownloadableSubscription subscription, boolean switchAfterDownload, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().downloadSubscription(this.mCardId, subscription, switchAfterDownload, this.mContext.getOpPackageName(), null, callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startResolutionActivity(Activity activity, int requestCode, Intent resultIntent, PendingIntent callbackIntent) throws IntentSender.SendIntentException {
        PendingIntent resolutionIntent = (PendingIntent) resultIntent.getParcelableExtra(EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_INTENT, PendingIntent.class);
        if (resolutionIntent == null) {
            throw new IllegalArgumentException("Invalid result intent");
        }
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_CALLBACK_INTENT, callbackIntent);
        activity.startIntentSenderForResult(resolutionIntent.getIntentSender(), requestCode, fillInIntent, 0, 0, 0);
    }

    @SystemApi
    public void continueOperation(Intent resolutionIntent, Bundle resolutionExtras) {
        if (!isEnabled()) {
            PendingIntent callbackIntent = (PendingIntent) resolutionIntent.getParcelableExtra(EXTRA_EMBEDDED_SUBSCRIPTION_RESOLUTION_CALLBACK_INTENT, PendingIntent.class);
            if (callbackIntent != null) {
                sendUnavailableError(callbackIntent);
                return;
            }
            return;
        }
        try {
            getIEuiccController().continueOperation(this.mCardId, resolutionIntent, resolutionExtras);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void getDownloadableSubscriptionMetadata(DownloadableSubscription subscription, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().getDownloadableSubscriptionMetadata(this.mCardId, subscription, this.mContext.getOpPackageName(), callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void getDefaultDownloadableSubscriptionList(PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().getDefaultDownloadableSubscriptionList(this.mCardId, this.mContext.getOpPackageName(), callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public EuiccInfo getEuiccInfo() {
        if (!isEnabled()) {
            return null;
        }
        try {
            return getIEuiccController().getEuiccInfo(this.mCardId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void deleteSubscription(int subscriptionId, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().deleteSubscription(this.mCardId, subscriptionId, this.mContext.getOpPackageName(), callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void switchToSubscription(int subscriptionId, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        if (subscriptionId == -1) {
            try {
                if (getIEuiccController().isCompatChangeEnabled(this.mContext.getOpPackageName(), SWITCH_WITHOUT_PORT_INDEX_EXCEPTION_ON_DISABLE)) {
                    Log.m110e(TAG, "switchToSubscription without portIndex is not allowed for disable operation");
                    throw new IllegalArgumentException("Must use switchToSubscription with portIndex API for disable operation");
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        getIEuiccController().switchToSubscription(this.mCardId, subscriptionId, this.mContext.getOpPackageName(), callbackIntent);
    }

    public void switchToSubscription(int subscriptionId, int portIndex, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            boolean canWriteEmbeddedSubscriptions = this.mContext.checkCallingOrSelfPermission(Manifest.C0000permission.WRITE_EMBEDDED_SUBSCRIPTIONS) == 0;
            if (!canWriteEmbeddedSubscriptions && !getIEuiccController().hasCarrierPrivilegesForPackageOnAnyPhone(this.mContext.getOpPackageName())) {
                Log.m110e(TAG, "Not permitted to use switchToSubscription with portIndex");
                throw new SecurityException("Must have carrier privileges to use switchToSubscription with portIndex");
            }
            getIEuiccController().switchToSubscriptionWithPort(this.mCardId, subscriptionId, portIndex, this.mContext.getOpPackageName(), callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateSubscriptionNickname(int subscriptionId, String nickname, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().updateSubscriptionNickname(this.mCardId, subscriptionId, nickname, this.mContext.getOpPackageName(), callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public void eraseSubscriptions(PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().eraseSubscriptions(this.mCardId, callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void eraseSubscriptions(int options, PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().eraseSubscriptionsWithOptions(this.mCardId, options, callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void retainSubscriptionsForFactoryReset(PendingIntent callbackIntent) {
        if (!isEnabled()) {
            sendUnavailableError(callbackIntent);
            return;
        }
        try {
            getIEuiccController().retainSubscriptionsForFactoryReset(this.mCardId, callbackIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setSupportedCountries(List<String> supportedCountries) {
        if (!isEnabled()) {
            return;
        }
        try {
            getIEuiccController().setSupportedCountries(true, (List) supportedCountries.stream().map(new EuiccManager$$ExternalSyntheticLambda0()).collect(Collectors.toList()));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setUnsupportedCountries(List<String> unsupportedCountries) {
        if (!isEnabled()) {
            return;
        }
        try {
            getIEuiccController().setSupportedCountries(false, (List) unsupportedCountries.stream().map(new EuiccManager$$ExternalSyntheticLambda0()).collect(Collectors.toList()));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<String> getSupportedCountries() {
        if (!isEnabled()) {
            return Collections.emptyList();
        }
        try {
            return getIEuiccController().getSupportedCountries(true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<String> getUnsupportedCountries() {
        if (!isEnabled()) {
            return Collections.emptyList();
        }
        try {
            return getIEuiccController().getSupportedCountries(false);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isSupportedCountry(String countryIso) {
        if (!isEnabled()) {
            return false;
        }
        try {
            return getIEuiccController().isSupportedCountry(countryIso.toUpperCase(Locale.ROOT));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private boolean refreshCardIdIfUninitialized() {
        if (this.mCardId == -2) {
            TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService("phone");
            this.mCardId = tm.getCardIdForDefaultEuicc();
        }
        if (this.mCardId == -2) {
            return false;
        }
        return true;
    }

    private static void sendUnavailableError(PendingIntent callbackIntent) {
        try {
            callbackIntent.send(2);
        } catch (PendingIntent.CanceledException e) {
        }
    }

    private static IEuiccController getIEuiccController() {
        return IEuiccController.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getEuiccControllerService().get());
    }

    public boolean isSimPortAvailable(int portIndex) {
        try {
            return getIEuiccController().isSimPortAvailable(this.mCardId, portIndex, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
