package com.android.service.ims;

import android.content.Context;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.ProvisioningManager;
import com.android.ims.ImsEcbmStateListener$$ExternalSyntheticLambda0;
import com.android.ims.internal.Logger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class RcsSettingUtils {
    private static final int DEFAULT_AVAILABILITY_CACHE_EXPIRATION_SEC = 30;
    private static final int DEFAULT_CAPABILITY_POLL_LIST_SUB_EXPIRATION_SEC = 30;
    private static final int DEFAULT_NUM_ENTRIES_IN_RCL = 100;
    private static final int DEFAULT_PUBLISH_THROTTLE_MS = 60000;
    private static final int TIMEOUT_GET_CONFIGURATION_MS = 5000;
    private static Logger logger = Logger.getLogger("RcsSettingUtils");

    public static boolean isVoLteProvisioned(int subId) {
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            boolean isProvisioned = manager.getProvisioningStatusForCapability(1, 0);
            logger.debug("isVoLteProvisioned=" + isProvisioned);
            return isProvisioned;
        } catch (Exception e) {
            logger.debug("isVoLteProvisioned, exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isVowifiProvisioned(int subId) {
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            boolean isProvisioned = manager.getProvisioningStatusForCapability(1, 1);
            logger.debug("isVowifiProvisioned=" + isProvisioned);
            return isProvisioned;
        } catch (Exception e) {
            logger.debug("isVowifiProvisioned, exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isLvcProvisioned(int subId) {
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            boolean isProvisioned = manager.getProvisioningStatusForCapability(2, 0);
            logger.debug("isLvcProvisioned=" + isProvisioned);
            return isProvisioned;
        } catch (Exception e) {
            logger.debug("isLvcProvisioned, exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isEabProvisioned(Context context, int subId) {
        PersistableBundle config;
        boolean isProvisioned = false;
        if (subId == -1) {
            logger.debug("isEabProvisioned: no valid subscriptions!");
            return false;
        }
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (configManager != null && (config = configManager.getConfigForSubId(subId)) != null && !config.getBoolean("carrier_volte_provisioned_bool")) {
            return true;
        }
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            isProvisioned = manager.getRcsProvisioningStatusForCapability(2, 0);
        } catch (Exception e) {
            logger.debug("isEabProvisioned: exception=" + e.getMessage());
        }
        logger.debug("isEabProvisioned=" + isProvisioned);
        return isProvisioned;
    }

    public static boolean isPublishEnabled(Context context, int subId) {
        PersistableBundle config;
        if (subId == -1) {
            logger.debug("isPublishEnabled: no valid subscriptions!");
            return false;
        }
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        return (configManager == null || (config = configManager.getConfigForSubId(subId)) == null || !config.getBoolean("ims.enable_presence_publish_bool", false)) ? false : true;
    }

    public static boolean hasUserEnabledContactDiscovery(Context context, int subId) {
        if (subId == -1) {
            logger.debug("hasUserEnabledContactDiscovery: no valid subscriptions!");
            return false;
        }
        try {
            ImsManager imsManager = (ImsManager) context.getSystemService(ImsManager.class);
            ImsRcsManager rcsManager = imsManager.getImsRcsManager(subId);
            return rcsManager.getUceAdapter().isUceSettingEnabled();
        } catch (Exception e) {
            logger.warn("hasUserEnabledContactDiscovery: Exception = " + e.getMessage());
            return false;
        }
    }

    public static int getSIPT1Timer(int subId) {
        int sipT1Timer = 0;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            sipT1Timer = manager.getProvisioningIntValue(7);
        } catch (Exception e) {
            logger.debug("getSIPT1Timer: exception=" + e.getMessage());
        }
        logger.debug("getSIPT1Timer=" + sipT1Timer);
        return sipT1Timer;
    }

    public static boolean getCapabilityDiscoveryEnabled(int subId) {
        boolean capabilityDiscoveryEnabled = false;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            capabilityDiscoveryEnabled = manager.getProvisioningIntValue(17) == 1;
        } catch (Exception e) {
            logger.debug("capabilityDiscoveryEnabled: exception=" + e.getMessage());
        }
        logger.debug("capabilityDiscoveryEnabled=" + capabilityDiscoveryEnabled);
        return capabilityDiscoveryEnabled;
    }

    public static int getMaxNumbersInRCL(int subId) {
        int maxNumbersInRCL = DEFAULT_NUM_ENTRIES_IN_RCL;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            maxNumbersInRCL = manager.getProvisioningIntValue(22);
        } catch (Exception e) {
            logger.debug("getMaxNumbersInRCL: exception=" + e.getMessage());
        }
        logger.debug("getMaxNumbersInRCL=" + maxNumbersInRCL);
        return maxNumbersInRCL;
    }

    public static int getCapabPollListSubExp(int subId) {
        int capabPollListSubExp = 30;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            capabPollListSubExp = manager.getProvisioningIntValue(23);
        } catch (Exception e) {
            logger.debug("getCapabPollListSubExp: exception=" + e.getMessage());
        }
        logger.debug("getCapabPollListSubExp=" + capabPollListSubExp);
        return capabPollListSubExp;
    }

    public static int getAvailabilityCacheExpiration(int subId) {
        int availabilityCacheExpiration = 30;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            availabilityCacheExpiration = manager.getProvisioningIntValue(19);
        } catch (Exception e) {
            logger.debug("getAvailabilityCacheExpiration: exception=" + e.getMessage());
        }
        logger.debug("getAvailabilityCacheExpiration=" + availabilityCacheExpiration);
        return availabilityCacheExpiration;
    }

    public static int getPublishThrottle(int subId) {
        int publishThrottle = DEFAULT_PUBLISH_THROTTLE_MS;
        try {
            ProvisioningManager manager = ProvisioningManager.createForSubscriptionId(subId);
            publishThrottle = manager.getProvisioningIntValue(21);
        } catch (Exception e) {
            logger.debug("publishThrottle: exception=" + e.getMessage());
        }
        logger.debug("publishThrottle=" + publishThrottle);
        return publishThrottle;
    }

    public static boolean isVtEnabledByUser(int subId) {
        try {
            ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
            return mmTelManager.isVtSettingEnabled();
        } catch (Exception e) {
            logger.warn("isVtEnabledByUser exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isWfcEnabledByUser(int subId) {
        try {
            ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
            return mmTelManager.isVoWiFiSettingEnabled();
        } catch (Exception e) {
            logger.warn("isWfcEnabledByUser exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isAdvancedCallingEnabledByUser(int subId) {
        try {
            ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
            return mmTelManager.isAdvancedCallingSettingEnabled();
        } catch (Exception e) {
            logger.warn("isAdvancedCallingEnabledByUser exception = " + e.getMessage());
            return false;
        }
    }

    public static boolean isVoLteSupported(int subId) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            LinkedBlockingQueue<Boolean> resultQueue = new LinkedBlockingQueue<>(1);
            try {
                ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
                ImsEcbmStateListener$$ExternalSyntheticLambda0 imsEcbmStateListener$$ExternalSyntheticLambda0 = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
                Objects.requireNonNull(resultQueue);
                mmTelManager.isSupported(1, 1, imsEcbmStateListener$$ExternalSyntheticLambda0, new RcsSettingUtils$$ExternalSyntheticLambda0(resultQueue));
                try {
                    Boolean result = resultQueue.poll(5000L, TimeUnit.MILLISECONDS);
                    if (result != null) {
                        return result.booleanValue();
                    }
                    return false;
                } catch (InterruptedException e) {
                    logger.warn("isVoLteSupported, InterruptedException=" + e.getMessage());
                    return false;
                }
            } catch (ImsException e2) {
                logger.warn("isVoLteSupported: ImsException = " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public static boolean isVoWiFiSupported(int subId) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            LinkedBlockingQueue<Boolean> resultQueue = new LinkedBlockingQueue<>(1);
            try {
                ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
                ImsEcbmStateListener$$ExternalSyntheticLambda0 imsEcbmStateListener$$ExternalSyntheticLambda0 = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
                Objects.requireNonNull(resultQueue);
                mmTelManager.isSupported(1, 2, imsEcbmStateListener$$ExternalSyntheticLambda0, new RcsSettingUtils$$ExternalSyntheticLambda0(resultQueue));
                try {
                    Boolean result = resultQueue.poll(5000L, TimeUnit.MILLISECONDS);
                    if (result != null) {
                        return result.booleanValue();
                    }
                    return false;
                } catch (InterruptedException e) {
                    logger.warn("isVoWiFiSupported, InterruptedException=" + e.getMessage());
                    return false;
                }
            } catch (ImsException e2) {
                logger.warn("isVoWiFiSupported: ImsException = " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public static boolean isVtSupported(int subId) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            LinkedBlockingQueue<Boolean> resultQueue = new LinkedBlockingQueue<>(1);
            try {
                ImsMmTelManager mmTelManager = ImsMmTelManager.createForSubscriptionId(subId);
                ImsEcbmStateListener$$ExternalSyntheticLambda0 imsEcbmStateListener$$ExternalSyntheticLambda0 = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
                Objects.requireNonNull(resultQueue);
                mmTelManager.isSupported(2, 1, imsEcbmStateListener$$ExternalSyntheticLambda0, new RcsSettingUtils$$ExternalSyntheticLambda0(resultQueue));
                try {
                    Boolean result = resultQueue.poll(5000L, TimeUnit.MILLISECONDS);
                    if (result != null) {
                        return result.booleanValue();
                    }
                    return false;
                } catch (InterruptedException e) {
                    logger.warn("isVtSupported, InterruptedException=" + e.getMessage());
                    return false;
                }
            } catch (ImsException e2) {
                logger.warn("isVoWiFiSupported: ImsException = " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public static int getDefaultSubscriptionId(Context context) {
        List<SubscriptionInfo> infos;
        SubscriptionManager sm = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (sm == null || (infos = sm.getActiveSubscriptionInfoList()) == null || infos.isEmpty()) {
            return -1;
        }
        int defaultSub = SubscriptionManager.getDefaultVoiceSubscriptionId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultSub)) {
            defaultSub = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        if (!SubscriptionManager.isValidSubscriptionId(defaultSub)) {
            for (SubscriptionInfo info : infos) {
                if (!info.isOpportunistic()) {
                    return info.getSubscriptionId();
                }
            }
            return defaultSub;
        }
        return defaultSub;
    }
}
