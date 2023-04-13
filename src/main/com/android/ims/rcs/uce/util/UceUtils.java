package com.android.ims.rcs.uce.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.BlockedNumberContract;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.UceDeviceState;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class UceUtils {
    private static final long DEFAULT_AVAILABILITY_CACHE_EXPIRATION_SEC = 60;
    private static final int DEFAULT_RCL_MAX_NUM_ENTRIES = 100;
    private static final long DEFAULT_RCS_PUBLISH_SOURCE_THROTTLE_MS = 60000;
    private static final String LOG_PREFIX = "RcsUce.";
    public static final int LOG_SIZE = 20;
    private static final String LOG_TAG = "RcsUce.UceUtils";
    private static final String SHARED_PREF_DEVICE_STATE_KEY = "UceDeviceState";
    private static final long DEFAULT_NON_RCS_CAPABILITIES_CACHE_EXPIRATION_SEC = TimeUnit.DAYS.toSeconds(30);
    private static final long DEFAULT_REQUEST_RETRY_INTERVAL_MS = TimeUnit.MINUTES.toMillis(20);
    private static final long DEFAULT_MINIMUM_REQUEST_RETRY_AFTER_MS = TimeUnit.SECONDS.toMillis(3);
    private static final long DEFAULT_CAP_REQUEST_TIMEOUT_AFTER_MS = TimeUnit.MINUTES.toMillis(3);
    private static Optional<Long> OVERRIDE_CAP_REQUEST_TIMEOUT_AFTER_MS = Optional.empty();
    private static long TASK_ID = 0;
    private static long REQUEST_COORDINATOR_ID = 0;

    public static String getLogPrefix() {
        return LOG_PREFIX;
    }

    public static synchronized long generateTaskId() {
        long j;
        synchronized (UceUtils.class) {
            j = TASK_ID + 1;
            TASK_ID = j;
        }
        return j;
    }

    public static synchronized long generateRequestCoordinatorId() {
        long j;
        synchronized (UceUtils.class) {
            j = REQUEST_COORDINATOR_ID + 1;
            REQUEST_COORDINATOR_ID = j;
        }
        return j;
    }

    public static boolean isEabProvisioned(Context context, int subId) {
        PersistableBundle config;
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            Log.w(LOG_TAG, "isEabProvisioned: invalid subscriptionId " + subId);
            return false;
        }
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (configManager != null && (config = configManager.getConfigForSubId(subId)) != null && !config.getBoolean("carrier_volte_provisioned_bool")) {
            return true;
        }
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            boolean isProvisioned = manager.getRcsProvisioningStatusForCapability(2, 0);
            return isProvisioned;
        } catch (Exception e) {
            Log.w(LOG_TAG, "isEabProvisioned: exception=" + e.getMessage());
            return false;
        }
    }

    public static boolean isPresenceCapExchangeEnabled(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.enable_presence_capability_exchange_bool");
    }

    public static boolean isPresenceSupported(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.enable_presence_publish_bool");
    }

    public static boolean isSipOptionsSupported(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("use_rcs_sip_options_bool");
    }

    public static boolean isPresenceGroupSubscribeEnabled(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.enable_presence_group_subscribe_bool");
    }

    public static boolean isNumberBlocked(Context context, String phoneNumber) {
        try {
            int blockStatus = BlockedNumberContract.SystemContract.shouldSystemBlockNumber(context, phoneNumber, (Bundle) null);
            return blockStatus != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSipUriForPresenceSubscribeEnabled(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.use_sip_uri_for_presence_subscribe_bool");
    }

    public static boolean isTelUriForPidfXmlEnabled(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.use_tel_uri_for_pidf_xml");
    }

    public static long getRcsPublishThrottle(int subId) {
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            long provisioningValue = manager.getProvisioningIntValue(21);
            if (provisioningValue <= 0) {
                return DEFAULT_RCS_PUBLISH_SOURCE_THROTTLE_MS;
            }
            return provisioningValue;
        } catch (Exception e) {
            Log.w(LOG_TAG, "getRcsPublishThrottle: exception=" + e.getMessage());
            return DEFAULT_RCS_PUBLISH_SOURCE_THROTTLE_MS;
        }
    }

    public static int getRclMaxNumberEntries(int subId) {
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            int provisioningValue = manager.getProvisioningIntValue(22);
            if (provisioningValue <= 0) {
                return DEFAULT_RCL_MAX_NUM_ENTRIES;
            }
            return provisioningValue;
        } catch (Exception e) {
            Log.w(LOG_TAG, "getRclMaxNumberEntries: exception=" + e.getMessage());
            return DEFAULT_RCL_MAX_NUM_ENTRIES;
        }
    }

    public static long getNonRcsCapabilitiesCacheExpiration(Context context, int subId) {
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null) {
            return DEFAULT_NON_RCS_CAPABILITIES_CACHE_EXPIRATION_SEC;
        }
        PersistableBundle config = configManager.getConfigForSubId(subId);
        if (config == null) {
            return DEFAULT_NON_RCS_CAPABILITIES_CACHE_EXPIRATION_SEC;
        }
        return config.getInt("ims.non_rcs_capabilities_cache_expiration_sec_int");
    }

    public static boolean isRequestForbiddenBySip489(Context context, int subId) {
        PersistableBundle config;
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null || (config = configManager.getConfigForSubId(subId)) == null) {
            return false;
        }
        return config.getBoolean("ims.rcs_request_forbidden_by_sip_489_bool");
    }

    public static long getRequestRetryInterval(Context context, int subId) {
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (configManager == null) {
            return DEFAULT_REQUEST_RETRY_INTERVAL_MS;
        }
        PersistableBundle config = configManager.getConfigForSubId(subId);
        if (config == null) {
            return DEFAULT_REQUEST_RETRY_INTERVAL_MS;
        }
        return config.getLong("ims.rcs_request_retry_interval_millis_long");
    }

    public static boolean saveDeviceStateToPreference(Context context, int subId, UceDeviceState.DeviceStateResult deviceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getDeviceStateSharedPrefKey(subId), getDeviceStateSharedPrefValue(deviceState));
        return editor.commit();
    }

    public static Optional<UceDeviceState.DeviceStateResult> restoreDeviceState(Context context, int subId) {
        Optional<Integer> errorCode;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sharedPrefKey = getDeviceStateSharedPrefKey(subId);
        String sharedPrefValue = sharedPreferences.getString(sharedPrefKey, "");
        if (TextUtils.isEmpty(sharedPrefValue)) {
            return Optional.empty();
        }
        String[] valueAry = sharedPrefValue.split(",");
        if (valueAry == null || valueAry.length != 4) {
            return Optional.empty();
        }
        try {
            int deviceState = Integer.valueOf(valueAry[0]).intValue();
            if (Integer.valueOf(valueAry[1]).intValue() != -1) {
                errorCode = Optional.of(Integer.valueOf(valueAry[1]));
            } else {
                errorCode = Optional.empty();
            }
            long retryTimeMillis = Long.valueOf(valueAry[2]).longValue();
            Optional<Instant> retryTime = retryTimeMillis == -1 ? Optional.empty() : Optional.of(Instant.ofEpochMilli(retryTimeMillis));
            long exitStateTimeMillis = Long.valueOf(valueAry[3]).longValue();
            Optional<Instant> exitStateTime = exitStateTimeMillis == -1 ? Optional.empty() : Optional.of(Instant.ofEpochMilli(exitStateTimeMillis));
            return Optional.of(new UceDeviceState.DeviceStateResult(deviceState, errorCode, retryTime, exitStateTime));
        } catch (Exception e) {
            Log.d(LOG_TAG, "restoreDeviceState: exception " + e);
            return Optional.empty();
        }
    }

    public static boolean removeDeviceStateFromPreference(Context context, int subId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getDeviceStateSharedPrefKey(subId));
        return editor.commit();
    }

    private static String getDeviceStateSharedPrefKey(int subId) {
        return SHARED_PREF_DEVICE_STATE_KEY + subId;
    }

    private static String getDeviceStateSharedPrefValue(UceDeviceState.DeviceStateResult deviceState) {
        StringBuilder builder = new StringBuilder();
        builder.append(deviceState.getDeviceState()).append(",").append(deviceState.getErrorCode().orElse(-1));
        long retryTimeMillis = -1;
        Optional<Instant> retryTime = deviceState.getRequestRetryTime();
        if (retryTime.isPresent()) {
            retryTimeMillis = retryTime.get().toEpochMilli();
        }
        builder.append(",").append(retryTimeMillis);
        long exitStateTimeMillis = -1;
        Optional<Instant> exitStateTime = deviceState.getExitStateTime();
        if (exitStateTime.isPresent()) {
            exitStateTimeMillis = exitStateTime.get().toEpochMilli();
        }
        builder.append(",").append(exitStateTimeMillis);
        return builder.toString();
    }

    public static long getMinimumRequestRetryAfterMillis() {
        return DEFAULT_MINIMUM_REQUEST_RETRY_AFTER_MS;
    }

    public static synchronized void setCapRequestTimeoutAfterMillis(long timeoutAfterMs) {
        synchronized (UceUtils.class) {
            if (timeoutAfterMs <= 0) {
                OVERRIDE_CAP_REQUEST_TIMEOUT_AFTER_MS = Optional.empty();
            } else {
                OVERRIDE_CAP_REQUEST_TIMEOUT_AFTER_MS = Optional.of(Long.valueOf(timeoutAfterMs));
            }
        }
    }

    public static synchronized long getCapRequestTimeoutAfterMillis() {
        synchronized (UceUtils.class) {
            if (OVERRIDE_CAP_REQUEST_TIMEOUT_AFTER_MS.isPresent()) {
                return OVERRIDE_CAP_REQUEST_TIMEOUT_AFTER_MS.get().longValue();
            }
            return DEFAULT_CAP_REQUEST_TIMEOUT_AFTER_MS;
        }
    }

    public static String getContactNumber(Uri contactUri) {
        if (contactUri == null) {
            return null;
        }
        String number = contactUri.getSchemeSpecificPart();
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        String[] numberParts = number.split("[@;:]");
        if (numberParts.length == 0) {
            Log.d(LOG_TAG, "getContactNumber: the length of numberPars is 0");
            return contactUri.toString();
        }
        return numberParts[0];
    }

    public static long getAvailabilityCacheExpiration(int subId) {
        long value = -1;
        try {
            ProvisioningManager pm = ProvisioningManager.createForSubscriptionId(subId);
            value = pm.getProvisioningIntValue(19);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception in getAvailabilityCacheExpiration: " + e);
        }
        if (value <= 0) {
            Log.w(LOG_TAG, "The availability expiration cannot be less than 0.");
            return DEFAULT_AVAILABILITY_CACHE_EXPIRATION_SEC;
        }
        return value;
    }
}
