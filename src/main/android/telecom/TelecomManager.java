package android.telecom;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.OutcomeReceiver;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import com.android.internal.telecom.ClientTransactionalServiceRepository;
import com.android.internal.telecom.ClientTransactionalServiceWrapper;
import com.android.internal.telecom.ITelecomService;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class TelecomManager {
    public static final String ACTION_CHANGE_DEFAULT_DIALER = "android.telecom.action.CHANGE_DEFAULT_DIALER";
    public static final String ACTION_CHANGE_PHONE_ACCOUNTS = "android.telecom.action.CHANGE_PHONE_ACCOUNTS";
    public static final String ACTION_CONFIGURE_PHONE_ACCOUNT = "android.telecom.action.CONFIGURE_PHONE_ACCOUNT";
    @SystemApi
    public static final String ACTION_CURRENT_TTY_MODE_CHANGED = "android.telecom.action.CURRENT_TTY_MODE_CHANGED";
    public static final String ACTION_DEFAULT_CALL_SCREENING_APP_CHANGED = "android.telecom.action.DEFAULT_CALL_SCREENING_APP_CHANGED";
    public static final String ACTION_DEFAULT_DIALER_CHANGED = "android.telecom.action.DEFAULT_DIALER_CHANGED";
    public static final String ACTION_INCOMING_CALL = "android.telecom.action.INCOMING_CALL";
    public static final String ACTION_NEW_UNKNOWN_CALL = "android.telecom.action.NEW_UNKNOWN_CALL";
    public static final String ACTION_PHONE_ACCOUNT_REGISTERED = "android.telecom.action.PHONE_ACCOUNT_REGISTERED";
    public static final String ACTION_PHONE_ACCOUNT_UNREGISTERED = "android.telecom.action.PHONE_ACCOUNT_UNREGISTERED";
    public static final String ACTION_POST_CALL = "android.telecom.action.POST_CALL";
    public static final String ACTION_SHOW_CALL_ACCESSIBILITY_SETTINGS = "android.telecom.action.SHOW_CALL_ACCESSIBILITY_SETTINGS";
    public static final String ACTION_SHOW_CALL_SETTINGS = "android.telecom.action.SHOW_CALL_SETTINGS";
    public static final String ACTION_SHOW_MISSED_CALLS_NOTIFICATION = "android.telecom.action.SHOW_MISSED_CALLS_NOTIFICATION";
    public static final String ACTION_SHOW_RESPOND_VIA_SMS_SETTINGS = "android.telecom.action.SHOW_RESPOND_VIA_SMS_SETTINGS";
    public static final String ACTION_SHOW_SWITCH_TO_WORK_PROFILE_FOR_CALL_DIALOG = "android.telecom.action.SHOW_SWITCH_TO_WORK_PROFILE_FOR_CALL_DIALOG";
    @SystemApi
    public static final String ACTION_TTY_PREFERRED_MODE_CHANGED = "android.telecom.action.TTY_PREFERRED_MODE_CHANGED";
    public static final int AUDIO_OUTPUT_DEFAULT = 0;
    public static final int AUDIO_OUTPUT_DISABLE_SPEAKER = 1;
    public static final int AUDIO_OUTPUT_ENABLE_SPEAKER = 0;
    public static final String CALL_AUTO_DISCONNECT_MESSAGE_STRING = "Call dropped by lower layers";
    @SystemApi
    public static final int CALL_SOURCE_EMERGENCY_DIALPAD = 1;
    @SystemApi
    public static final int CALL_SOURCE_EMERGENCY_SHORTCUT = 2;
    @SystemApi
    public static final int CALL_SOURCE_UNSPECIFIED = 0;
    public static final char DTMF_CHARACTER_PAUSE = ',';
    public static final char DTMF_CHARACTER_WAIT = ';';
    public static final int DURATION_LONG = 3;
    public static final int DURATION_MEDIUM = 2;
    public static final int DURATION_SHORT = 1;
    public static final int DURATION_VERY_SHORT = 0;
    public static final long ENABLE_GET_CALL_STATE_PERMISSION_PROTECTION = 157233955;
    public static final long ENABLE_GET_PHONE_ACCOUNT_PERMISSION_PROTECTION = 183407956;
    public static final String EXTRA_CALL_AUDIO_STATE = "android.telecom.extra.CALL_AUDIO_STATE";
    @SystemApi
    public static final String EXTRA_CALL_BACK_INTENT = "android.telecom.extra.CALL_BACK_INTENT";
    public static final String EXTRA_CALL_BACK_NUMBER = "android.telecom.extra.CALL_BACK_NUMBER";
    public static final String EXTRA_CALL_CREATED_EPOCH_TIME_MILLIS = "android.telecom.extra.CALL_CREATED_EPOCH_TIME_MILLIS";
    public static final String EXTRA_CALL_CREATED_TIME_MILLIS = "android.telecom.extra.CALL_CREATED_TIME_MILLIS";
    public static final String EXTRA_CALL_DISCONNECT_CAUSE = "android.telecom.extra.CALL_DISCONNECT_CAUSE";
    public static final String EXTRA_CALL_DISCONNECT_MESSAGE = "android.telecom.extra.CALL_DISCONNECT_MESSAGE";
    public static final String EXTRA_CALL_DURATION = "android.telecom.extra.CALL_DURATION";
    @SystemApi
    public static final String EXTRA_CALL_HAS_IN_BAND_RINGTONE = "android.telecom.extra.CALL_HAS_IN_BAND_RINGTONE";
    public static final String EXTRA_CALL_NETWORK_TYPE = "android.telecom.extra.CALL_NETWORK_TYPE";
    @SystemApi
    public static final String EXTRA_CALL_SOURCE = "android.telecom.extra.CALL_SOURCE";
    public static final String EXTRA_CALL_SUBJECT = "android.telecom.extra.CALL_SUBJECT";
    @SystemApi
    public static final String EXTRA_CALL_TECHNOLOGY_TYPE = "android.telecom.extra.CALL_TECHNOLOGY_TYPE";
    public static final String EXTRA_CALL_TELECOM_ROUTING_END_TIME_MILLIS = "android.telecom.extra.CALL_TELECOM_ROUTING_END_TIME_MILLIS";
    public static final String EXTRA_CALL_TELECOM_ROUTING_START_TIME_MILLIS = "android.telecom.extra.CALL_TELECOM_ROUTING_START_TIME_MILLIS";
    public static final String EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME = "android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME";
    @SystemApi
    @Deprecated
    public static final String EXTRA_CLEAR_MISSED_CALLS_INTENT = "android.telecom.extra.CLEAR_MISSED_CALLS_INTENT";
    @SystemApi
    public static final String EXTRA_CONNECTION_SERVICE = "android.telecom.extra.CONNECTION_SERVICE";
    @SystemApi
    public static final String EXTRA_CURRENT_TTY_MODE = "android.telecom.extra.CURRENT_TTY_MODE";
    public static final String EXTRA_DEFAULT_CALL_SCREENING_APP_COMPONENT_NAME = "android.telecom.extra.DEFAULT_CALL_SCREENING_APP_COMPONENT_NAME";
    public static final String EXTRA_DISCONNECT_CAUSE = "android.telecom.extra.DISCONNECT_CAUSE";
    public static final String EXTRA_HANDLE = "android.telecom.extra.HANDLE";
    public static final String EXTRA_HANDOVER_FROM_PHONE_ACCOUNT = "android.telecom.extra.HANDOVER_FROM_PHONE_ACCOUNT";
    public static final String EXTRA_HAS_PICTURE = "android.telecom.extra.HAS_PICTURE";
    public static final String EXTRA_INCOMING_CALL_ADDRESS = "android.telecom.extra.INCOMING_CALL_ADDRESS";
    public static final String EXTRA_INCOMING_CALL_EXTRAS = "android.telecom.extra.INCOMING_CALL_EXTRAS";
    public static final String EXTRA_INCOMING_VIDEO_STATE = "android.telecom.extra.INCOMING_VIDEO_STATE";
    public static final String EXTRA_IS_DEFAULT_CALL_SCREENING_APP = "android.telecom.extra.IS_DEFAULT_CALL_SCREENING_APP";
    public static final String EXTRA_IS_HANDOVER = "android.telecom.extra.IS_HANDOVER";
    public static final String EXTRA_IS_HANDOVER_CONNECTION = "android.telecom.extra.IS_HANDOVER_CONNECTION";
    @SystemApi
    public static final String EXTRA_IS_USER_INTENT_EMERGENCY_CALL = "android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL";
    public static final String EXTRA_LOCATION = "android.telecom.extra.LOCATION";
    public static final String EXTRA_MANAGED_PROFILE_USER_ID = "android.telecom.extra.MANAGED_PROFILE_USER_ID";
    public static final String EXTRA_NEW_OUTGOING_CALL_CANCEL_TIMEOUT = "android.telecom.extra.NEW_OUTGOING_CALL_CANCEL_TIMEOUT";
    public static final String EXTRA_NOTIFICATION_COUNT = "android.telecom.extra.NOTIFICATION_COUNT";
    public static final String EXTRA_NOTIFICATION_PHONE_NUMBER = "android.telecom.extra.NOTIFICATION_PHONE_NUMBER";
    public static final String EXTRA_OUTGOING_CALL_EXTRAS = "android.telecom.extra.OUTGOING_CALL_EXTRAS";
    public static final String EXTRA_OUTGOING_PICTURE = "android.telecom.extra.OUTGOING_PICTURE";
    public static final String EXTRA_PHONE_ACCOUNT_HANDLE = "android.telecom.extra.PHONE_ACCOUNT_HANDLE";
    public static final String EXTRA_PICTURE_URI = "android.telecom.extra.PICTURE_URI";
    public static final String EXTRA_PRIORITY = "android.telecom.extra.PRIORITY";
    public static final String EXTRA_START_CALL_WITH_RTT = "android.telecom.extra.START_CALL_WITH_RTT";
    public static final String EXTRA_START_CALL_WITH_SPEAKERPHONE = "android.telecom.extra.START_CALL_WITH_SPEAKERPHONE";
    public static final String EXTRA_START_CALL_WITH_VIDEO_STATE = "android.telecom.extra.START_CALL_WITH_VIDEO_STATE";
    @SystemApi
    public static final String EXTRA_TTY_PREFERRED_MODE = "android.telecom.extra.TTY_PREFERRED_MODE";
    @SystemApi
    public static final String EXTRA_UNKNOWN_CALL_HANDLE = "android.telecom.extra.UNKNOWN_CALL_HANDLE";
    public static final String EXTRA_USE_ASSISTED_DIALING = "android.telecom.extra.USE_ASSISTED_DIALING";
    public static final String GATEWAY_ORIGINAL_ADDRESS = "android.telecom.extra.GATEWAY_ORIGINAL_ADDRESS";
    public static final String GATEWAY_PROVIDER_PACKAGE = "android.telecom.extra.GATEWAY_PROVIDER_PACKAGE";
    public static final long MEDIUM_CALL_TIME_MS = 120000;
    public static final String METADATA_INCLUDE_EXTERNAL_CALLS = "android.telecom.INCLUDE_EXTERNAL_CALLS";
    public static final String METADATA_INCLUDE_SELF_MANAGED_CALLS = "android.telecom.INCLUDE_SELF_MANAGED_CALLS";
    public static final String METADATA_IN_CALL_SERVICE_CAR_MODE_UI = "android.telecom.IN_CALL_SERVICE_CAR_MODE_UI";
    public static final String METADATA_IN_CALL_SERVICE_RINGING = "android.telecom.IN_CALL_SERVICE_RINGING";
    public static final String METADATA_IN_CALL_SERVICE_UI = "android.telecom.IN_CALL_SERVICE_UI";
    public static final int PRESENTATION_ALLOWED = 1;
    public static final int PRESENTATION_PAYPHONE = 4;
    public static final int PRESENTATION_RESTRICTED = 2;
    public static final int PRESENTATION_UNAVAILABLE = 5;
    public static final int PRESENTATION_UNKNOWN = 3;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_URGENT = 1;
    public static final long SHORT_CALL_TIME_MS = 60000;
    private static final String TAG = "TelecomManager";
    public static final int TELECOM_TRANSACTION_SUCCESS = 0;
    public static final String TRANSACTION_CALL_ID_KEY = "TelecomCallId";
    @SystemApi
    public static final int TTY_MODE_FULL = 1;
    @SystemApi
    public static final int TTY_MODE_HCO = 2;
    @SystemApi
    public static final int TTY_MODE_OFF = 0;
    @SystemApi
    public static final int TTY_MODE_VCO = 3;
    public static final long VERY_SHORT_CALL_TIME_MS = 3000;
    private static ITelecomService sTelecomService;
    private final Context mContext;
    private final ITelecomService mTelecomServiceOverride;
    private final ClientTransactionalServiceRepository mTransactionalServiceRepository;
    public static final ComponentName EMERGENCY_DIALER_COMPONENT = ComponentName.createRelative("com.android.phone", ".EmergencyDialer");
    private static final Object CACHE_LOCK = new Object();
    private static final DeathRecipient SERVICE_DEATH = new DeathRecipient();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Presentation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TtyMode {
    }

    public static TelecomManager from(Context context) {
        return (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }

    public TelecomManager(Context context) {
        this(context, null);
    }

    public TelecomManager(Context context, ITelecomService telecomServiceImpl) {
        this.mTransactionalServiceRepository = new ClientTransactionalServiceRepository();
        Context appContext = context.getApplicationContext();
        if (appContext != null && Objects.equals(context.getAttributionTag(), appContext.getAttributionTag())) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        this.mTelecomServiceOverride = telecomServiceImpl;
    }

    public PhoneAccountHandle getDefaultOutgoingPhoneAccount(String uriScheme) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getDefaultOutgoingPhoneAccount(uriScheme, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getDefaultOutgoingPhoneAccount", e);
                return null;
            }
        }
        return null;
    }

    public PhoneAccountHandle getUserSelectedOutgoingPhoneAccount() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getUserSelectedOutgoingPhoneAccount(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getUserSelectedOutgoingPhoneAccount", e);
                return null;
            }
        }
        return null;
    }

    @SystemApi
    public void setUserSelectedOutgoingPhoneAccount(PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.setUserSelectedOutgoingPhoneAccount(accountHandle);
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "Error calling ITelecomService#setUserSelectedOutgoingPhoneAccount");
            }
        }
    }

    public PhoneAccountHandle getSimCallManager() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getSimCallManager(SubscriptionManager.getDefaultSubscriptionId(), this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "Error calling ITelecomService#getSimCallManager");
                return null;
            }
        }
        return null;
    }

    public PhoneAccountHandle getSimCallManagerForSubscription(int subscriptionId) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getSimCallManager(subscriptionId, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "Error calling ITelecomService#getSimCallManager");
                return null;
            }
        }
        return null;
    }

    public PhoneAccountHandle getSimCallManager(int userId) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getSimCallManagerForUser(userId, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "Error calling ITelecomService#getSimCallManagerForUser");
                return null;
            }
        }
        return null;
    }

    @SystemApi
    public PhoneAccountHandle getConnectionManager() {
        return getSimCallManager();
    }

    @SystemApi
    public List<PhoneAccountHandle> getPhoneAccountsSupportingScheme(String uriScheme) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getPhoneAccountsSupportingScheme(uriScheme, this.mContext.getOpPackageName()).getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getPhoneAccountsSupportingScheme", e);
            }
        }
        return new ArrayList();
    }

    public List<PhoneAccountHandle> getCallCapablePhoneAccounts() {
        return getCallCapablePhoneAccounts(false);
    }

    public List<PhoneAccountHandle> getSelfManagedPhoneAccounts() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getSelfManagedPhoneAccounts(this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getSelfManagedPhoneAccounts()", e);
            }
        }
        return new ArrayList();
    }

    public List<PhoneAccountHandle> getOwnSelfManagedPhoneAccounts() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getOwnSelfManagedPhoneAccounts(this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).getList();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        throw new IllegalStateException("Telecom is not available");
    }

    @SystemApi
    public List<PhoneAccountHandle> getCallCapablePhoneAccounts(boolean includeDisabledAccounts) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getCallCapablePhoneAccounts(includeDisabledAccounts, this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getCallCapablePhoneAccounts(" + includeDisabledAccounts + NavigationBarInflaterView.KEY_CODE_END, e);
            }
        }
        return new ArrayList();
    }

    @SystemApi
    @Deprecated
    public List<PhoneAccountHandle> getPhoneAccountsForPackage() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getPhoneAccountsForPackage(this.mContext.getPackageName()).getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getPhoneAccountsForPackage", e);
                return null;
            }
        }
        return null;
    }

    public PhoneAccount getPhoneAccount(PhoneAccountHandle account) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getPhoneAccount(account, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getPhoneAccount", e);
                return null;
            }
        }
        return null;
    }

    @SystemApi
    public int getAllPhoneAccountsCount() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getAllPhoneAccountsCount();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getAllPhoneAccountsCount", e);
                return 0;
            }
        }
        return 0;
    }

    @SystemApi
    public List<PhoneAccount> getAllPhoneAccounts() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getAllPhoneAccounts().getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getAllPhoneAccounts", e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SystemApi
    public List<PhoneAccountHandle> getAllPhoneAccountHandles() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getAllPhoneAccountHandles().getList();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getAllPhoneAccountHandles", e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    public void registerPhoneAccount(PhoneAccount account) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.registerPhoneAccount(account, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#registerPhoneAccount", e);
            }
        }
    }

    public void unregisterPhoneAccount(PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.unregisterPhoneAccount(accountHandle, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#unregisterPhoneAccount", e);
            }
        }
    }

    @SystemApi
    public void clearPhoneAccounts() {
        clearAccounts();
    }

    @SystemApi
    public void clearAccounts() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.clearAccounts(this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#clearAccounts", e);
            }
        }
    }

    public void clearAccountsForPackage(String packageName) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                if (!TextUtils.isEmpty(packageName)) {
                    service.clearAccounts(packageName);
                }
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#clearAccountsForPackage", e);
            }
        }
    }

    @SystemApi
    public ComponentName getDefaultPhoneApp() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getDefaultPhoneApp();
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get the default phone app.", e);
                return null;
            }
        }
        return null;
    }

    public String getDefaultDialerPackage() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getDefaultDialerPackage(this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get the default dialer package name.", e);
                return null;
            }
        }
        return null;
    }

    @SystemApi
    public String getDefaultDialerPackage(UserHandle userHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getDefaultDialerPackageForUser(userHandle.getIdentifier());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get the default dialer package name.", e);
                return null;
            }
        }
        return null;
    }

    @SystemApi
    @Deprecated
    public boolean setDefaultDialer(String packageName) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.setDefaultDialer(packageName);
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to set the default dialer.", e);
                return false;
            }
        }
        return false;
    }

    public String getSystemDialerPackage() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getSystemDialerPackage(this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get the system dialer package name.", e);
                return null;
            }
        }
        return null;
    }

    public boolean isVoiceMailNumber(PhoneAccountHandle accountHandle, String number) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isVoiceMailNumber(accountHandle, number, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling ITelecomService#isVoiceMailNumber.", e);
                return false;
            }
        }
        return false;
    }

    public String getVoiceMailNumber(PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getVoiceMailNumber(accountHandle, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling ITelecomService#hasVoiceMailNumber.", e);
                return null;
            }
        }
        return null;
    }

    @Deprecated
    public String getLine1Number(PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getLine1Number(accountHandle, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling ITelecomService#getLine1Number.", e);
                return null;
            }
        }
        return null;
    }

    public boolean isInCall() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isInCall(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling isInCall().", e);
                return false;
            }
        }
        return false;
    }

    public boolean hasManageOngoingCallsPermission() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.hasManageOngoingCallsPermission(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling hasManageOngoingCallsPermission().", e);
                if (!isSystemProcess()) {
                    e.rethrowAsRuntimeException();
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isInManagedCall() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isInManagedCall(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException calling isInManagedCall().", e);
                return false;
            }
        }
        return false;
    }

    @SystemApi
    public int getCallState() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getCallStateUsingPackage(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m111d(TAG, "RemoteException calling getCallState().", e);
                return 0;
            }
        }
        return 0;
    }

    @SystemApi
    public boolean isRinging() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isRinging(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get ringing state of phone app.", e);
                return false;
            }
        }
        return false;
    }

    @Deprecated
    public boolean endCall() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.endCall(this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#endCall", e);
                return false;
            }
        }
        return false;
    }

    @Deprecated
    public void acceptRingingCall() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.acceptRingingCall(this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#acceptRingingCall", e);
            }
        }
    }

    @Deprecated
    public void acceptRingingCall(int videoState) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.acceptRingingCallWithVideoState(this.mContext.getPackageName(), videoState);
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#acceptRingingCallWithVideoState", e);
            }
        }
    }

    public void silenceRinger() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.silenceRinger(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#silenceRinger", e);
            }
        }
    }

    public boolean isTtySupported() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isTtySupported(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get TTY supported state.", e);
                return false;
            }
        }
        return false;
    }

    @SystemApi
    public int getCurrentTtyMode() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.getCurrentTtyMode(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "RemoteException attempting to get the current TTY mode.", e);
                return 0;
            }
        }
        return 0;
    }

    public void addNewIncomingCall(PhoneAccountHandle phoneAccount, Bundle extras) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            if (extras != null) {
                try {
                    if (extras.getBoolean("android.telecom.extra.IS_HANDOVER") && this.mContext.getApplicationContext().getApplicationInfo().targetSdkVersion > 27) {
                        android.util.Log.m110e("TAG", "addNewIncomingCall failed. Use public api acceptHandover for API > O-MR1");
                        return;
                    }
                } catch (RemoteException e) {
                    android.util.Log.m109e(TAG, "RemoteException adding a new incoming call: " + phoneAccount, e);
                    return;
                }
            }
            service.addNewIncomingCall(phoneAccount, extras == null ? new Bundle() : extras, this.mContext.getPackageName());
        }
    }

    public void addNewIncomingConference(PhoneAccountHandle phoneAccount, Bundle extras) {
        Bundle bundle;
        ITelecomService service = getTelecomService();
        if (service != null) {
            if (extras != null) {
                bundle = extras;
            } else {
                try {
                    bundle = new Bundle();
                } catch (RemoteException e) {
                    android.util.Log.m109e(TAG, "RemoteException adding a new incoming conference: " + phoneAccount, e);
                    return;
                }
            }
            service.addNewIncomingConference(phoneAccount, bundle, this.mContext.getPackageName());
        }
    }

    @SystemApi
    public void addNewUnknownCall(PhoneAccountHandle phoneAccount, Bundle extras) {
        Bundle bundle;
        ITelecomService service = getTelecomService();
        if (service != null) {
            if (extras != null) {
                bundle = extras;
            } else {
                try {
                    bundle = new Bundle();
                } catch (RemoteException e) {
                    android.util.Log.m109e(TAG, "RemoteException adding a new unknown call: " + phoneAccount, e);
                    return;
                }
            }
            service.addNewUnknownCall(phoneAccount, bundle);
        }
    }

    public boolean handleMmi(String dialString) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.handlePinMmi(dialString, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#handlePinMmi", e);
                return false;
            }
        }
        return false;
    }

    public boolean handleMmi(String dialString, PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.handlePinMmiForPhoneAccount(accountHandle, dialString, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#handlePinMmi", e);
                return false;
            }
        }
        return false;
    }

    public Uri getAdnUriForPhoneAccount(PhoneAccountHandle accountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null && accountHandle != null) {
            try {
                return service.getAdnUriForPhoneAccount(accountHandle, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#getAdnUriForPhoneAccount", e);
            }
        }
        return Uri.parse("content://icc/adn");
    }

    public void cancelMissedCallsNotification() {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.cancelMissedCallsNotification(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#cancelMissedCallsNotification", e);
            }
        }
    }

    public void showInCallScreen(boolean showDialpad) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.showInCallScreen(showDialpad, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#showCallScreen", e);
            }
        }
    }

    public void placeCall(Uri address, Bundle extras) {
        Bundle bundle;
        ITelecomService service = getTelecomService();
        if (service != null) {
            if (address == null) {
                android.util.Log.m104w(TAG, "Cannot place call to empty address.");
            }
            if (extras != null) {
                bundle = extras;
            } else {
                try {
                    bundle = new Bundle();
                } catch (RemoteException e) {
                    android.util.Log.m109e(TAG, "Error calling ITelecomService#placeCall", e);
                    return;
                }
            }
            service.placeCall(address, bundle, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        }
    }

    public void startConference(List<Uri> participants, Bundle extras) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.startConference(participants, extras, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#placeCall", e);
            }
        }
    }

    @SystemApi
    public void enablePhoneAccount(PhoneAccountHandle handle, boolean isEnabled) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.enablePhoneAccount(handle, isEnabled);
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error enablePhoneAbbount", e);
            }
        }
    }

    @SystemApi
    public TelecomAnalytics dumpAnalytics() {
        ITelecomService service = getTelecomService();
        if (service == null) {
            return null;
        }
        try {
            TelecomAnalytics result = service.dumpCallAnalytics();
            return result;
        } catch (RemoteException e) {
            android.util.Log.m109e(TAG, "Error dumping call analytics", e);
            return null;
        }
    }

    public Intent createManageBlockedNumbersIntent() {
        ITelecomService service = getTelecomService();
        Intent result = null;
        if (service != null) {
            try {
                result = service.createManageBlockedNumbersIntent(this.mContext.getPackageName());
                if (result != null) {
                    result.prepareToEnterProcess(32, this.mContext.getAttributionSource());
                }
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error calling ITelecomService#createManageBlockedNumbersIntent", e);
            }
        }
        return result;
    }

    @SystemApi
    public Intent createLaunchEmergencyDialerIntent(String number) {
        ITelecomService service = getTelecomService();
        if (service == null) {
            android.util.Log.m104w(TAG, "createLaunchEmergencyDialerIntent - Telecom service not available.");
        } else {
            try {
                Intent result = service.createLaunchEmergencyDialerIntent(number);
                if (result != null) {
                    result.prepareToEnterProcess(32, this.mContext.getAttributionSource());
                }
                return result;
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error createLaunchEmergencyDialerIntent", e);
            }
        }
        Intent intent = new Intent(Intent.ACTION_DIAL_EMERGENCY);
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            intent.setData(Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null));
        }
        return intent;
    }

    public boolean isIncomingCallPermitted(PhoneAccountHandle phoneAccountHandle) {
        ITelecomService service;
        if (phoneAccountHandle != null && (service = getTelecomService()) != null) {
            try {
                return service.isIncomingCallPermitted(phoneAccountHandle, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error isIncomingCallPermitted", e);
            }
        }
        return false;
    }

    public boolean isOutgoingCallPermitted(PhoneAccountHandle phoneAccountHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isOutgoingCallPermitted(phoneAccountHandle, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m109e(TAG, "Error isOutgoingCallPermitted", e);
                return false;
            }
        }
        return false;
    }

    public void acceptHandover(Uri srcAddr, int videoState, PhoneAccountHandle destAcct) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.acceptHandover(srcAddr, videoState, destAcct, this.mContext.getPackageName());
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "RemoteException acceptHandover: " + e);
            }
        }
    }

    @SystemApi
    public boolean isInEmergencyCall() {
        ITelecomService service = getTelecomService();
        if (service == null) {
            return false;
        }
        try {
            return service.isInEmergencyCall();
        } catch (RemoteException e) {
            android.util.Log.m110e(TAG, "RemoteException isInEmergencyCall: " + e);
            return false;
        }
    }

    public boolean isInSelfManagedCall(String packageName, UserHandle userHandle) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                return service.isInSelfManagedCall(packageName, userHandle, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "RemoteException isInSelfManagedCall: " + e);
                e.rethrowFromSystemServer();
                return false;
            }
        }
        throw new IllegalStateException("Telecom service is not present");
    }

    public void addCall(CallAttributes callAttributes, Executor executor, OutcomeReceiver<CallControl, CallException> pendingControl, CallControlCallback handshakes, CallEventCallback events) {
        Objects.requireNonNull(callAttributes);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(pendingControl);
        Objects.requireNonNull(handshakes);
        Objects.requireNonNull(events);
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                ClientTransactionalServiceWrapper transactionalServiceWrapper = this.mTransactionalServiceRepository.addNewCallForTransactionalServiceWrapper(callAttributes.getPhoneAccountHandle());
                String newCallId = transactionalServiceWrapper.trackCall(callAttributes, executor, pendingControl, handshakes, events);
                service.addCall(callAttributes, transactionalServiceWrapper.getCallEventCallback(), newCallId, this.mContext.getOpPackageName());
                return;
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "RemoteException addCall: " + e);
                e.rethrowFromSystemServer();
                return;
            }
        }
        throw new IllegalStateException("Telecom service is not present");
    }

    public void handleCallIntent(Intent intent, String callingPackageProxy) {
        ITelecomService service = getTelecomService();
        if (service != null) {
            try {
                service.handleCallIntent(intent, callingPackageProxy);
            } catch (RemoteException e) {
                android.util.Log.m110e(TAG, "RemoteException handleCallIntent: " + e);
            }
        }
    }

    private boolean isSystemProcess() {
        return Process.myUid() == 1000;
    }

    private ITelecomService getTelecomService() {
        ITelecomService iTelecomService = this.mTelecomServiceOverride;
        if (iTelecomService != null) {
            return iTelecomService;
        }
        if (sTelecomService == null) {
            ITelecomService temp = ITelecomService.Stub.asInterface(ServiceManager.getService(Context.TELECOM_SERVICE));
            synchronized (CACHE_LOCK) {
                if (sTelecomService == null && temp != null) {
                    try {
                        sTelecomService = temp;
                        temp.asBinder().linkToDeath(SERVICE_DEATH, 0);
                    } catch (Exception e) {
                        sTelecomService = null;
                    }
                }
            }
        }
        return sTelecomService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class DeathRecipient implements IBinder.DeathRecipient {
        private DeathRecipient() {
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            TelecomManager.resetServiceCache();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void resetServiceCache() {
        synchronized (CACHE_LOCK) {
            ITelecomService iTelecomService = sTelecomService;
            if (iTelecomService != null) {
                iTelecomService.asBinder().unlinkToDeath(SERVICE_DEATH, 0);
                sTelecomService = null;
            }
        }
    }
}
