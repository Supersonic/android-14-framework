package com.android.ims;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.BinderCacheManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSession;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.MediaThreshold;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.RtpHeaderExtensionType;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsConfigCallback;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.aidl.IImsSmsListener;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.aidl.ISrvccStartedCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import android.telephony.ims.feature.MmTelFeature;
import android.util.SparseArray;
import com.android.ims.FeatureConnector;
import com.android.ims.ImsCall;
import com.android.ims.ImsManager;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsServiceFeatureCallback;
import com.android.internal.telephony.ITelephony;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ImsManager implements FeatureUpdates {
    public static final String ACTION_IMS_INCOMING_CALL = "com.android.ims.IMS_INCOMING_CALL";
    @Deprecated
    public static final String ACTION_IMS_REGISTRATION_ERROR = "android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR";
    public static final String ACTION_IMS_SERVICE_DOWN = "com.android.ims.IMS_SERVICE_DOWN";
    public static final String ACTION_IMS_SERVICE_UP = "com.android.ims.IMS_SERVICE_UP";
    private static final boolean DBG = true;
    @Deprecated
    public static final String EXTRA_CALL_ID = "android:imsCallID";
    public static final String EXTRA_IS_UNKNOWN_CALL = "android:isUnknown";
    public static final String EXTRA_PHONE_ID = "android:phone_id";
    @Deprecated
    public static final String EXTRA_SERVICE_ID = "android:imsServiceId";
    public static final String EXTRA_USSD = "android:ussd";
    public static final String FALSE = "false";
    public static final int INCOMING_CALL_RESULT_CODE = 101;
    public static final String PROPERTY_DBG_ALLOW_IMS_OFF_OVERRIDE = "persist.dbg.allow_ims_off";
    public static final int PROPERTY_DBG_ALLOW_IMS_OFF_OVERRIDE_DEFAULT = 0;
    public static final String PROPERTY_DBG_VOLTE_AVAIL_OVERRIDE = "persist.dbg.volte_avail_ovr";
    public static final int PROPERTY_DBG_VOLTE_AVAIL_OVERRIDE_DEFAULT = 0;
    public static final String PROPERTY_DBG_VT_AVAIL_OVERRIDE = "persist.dbg.vt_avail_ovr";
    public static final int PROPERTY_DBG_VT_AVAIL_OVERRIDE_DEFAULT = 0;
    public static final String PROPERTY_DBG_WFC_AVAIL_OVERRIDE = "persist.dbg.wfc_avail_ovr";
    public static final int PROPERTY_DBG_WFC_AVAIL_OVERRIDE_DEFAULT = 0;
    private static final int RESPONSE_WAIT_TIME_MS = 3000;
    private static final int SUBINFO_PROPERTY_FALSE = 0;
    private static final int SUB_PROPERTY_NOT_INITIALIZED = -1;
    private static final int SYSTEM_PROPERTY_NOT_SET = -1;
    private static final String TAG = "ImsManager";
    public static final String TRUE = "true";
    private BinderCacheManager<ITelephony> mBinderCache;
    private CarrierConfigManager mConfigManager;
    private boolean mConfigUpdated;
    private Context mContext;
    private final Executor mExecutor;
    private ImsConfigListener mImsConfigListener;
    private String mLogTagPostfix;
    private AtomicReference<MmTelFeatureConnection> mMmTelConnectionRef;
    private MmTelFeatureConnectionFactory mMmTelFeatureConnectionFactory;
    private int mPhoneId;
    private final SettingsProxy mSettingsProxy;
    private final SubscriptionManagerProxy mSubscriptionManagerProxy;
    private static final int[] LOCAL_IMS_CONFIG_KEYS = {68};
    private static final SparseArray<InstanceManager> IMS_MANAGER_INSTANCES = new SparseArray<>(2);
    private static final SparseArray<ImsStatsCallback> IMS_STATS_CALLBACKS = new SparseArray<>(2);

    /* loaded from: classes.dex */
    public interface ImsStatsCallback {
        void onEnabledMmTelCapabilitiesChanged(int i, int i2, boolean z);
    }

    /* loaded from: classes.dex */
    public interface MmTelFeatureConnectionFactory {
        MmTelFeatureConnection create(Context context, int i, int i2, IImsMmTelFeature iImsMmTelFeature, IImsConfig iImsConfig, IImsRegistration iImsRegistration, ISipTransport iSipTransport);
    }

    /* loaded from: classes.dex */
    public interface SettingsProxy {
        int getSecureIntSetting(ContentResolver contentResolver, String str, int i);

        boolean putSecureIntSetting(ContentResolver contentResolver, String str, int i);
    }

    /* loaded from: classes.dex */
    public interface SubscriptionManagerProxy {
        int[] getActiveSubscriptionIdList();

        int getDefaultVoicePhoneId();

        int getIntegerSubscriptionProperty(int i, String str, int i2);

        int getSubscriptionId(int i);

        boolean isValidSubscriptionId(int i);

        void setSubscriptionProperty(int i, String str, String str2);
    }

    public static /* synthetic */ ImsManager $r8$lambda$LQs8_Cgd1Mgqi6UQz75OSYvt96E(Context context, int i) {
        return new ImsManager(context, i);
    }

    /* loaded from: classes.dex */
    private static class LazyExecutor implements Executor {
        private Executor mExecutor;

        private LazyExecutor() {
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            startExecutorIfNeeded();
            this.mExecutor.execute(runnable);
        }

        private synchronized void startExecutorIfNeeded() {
            if (this.mExecutor != null) {
                return;
            }
            this.mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    /* loaded from: classes.dex */
    private static class DefaultSettingsProxy implements SettingsProxy {
        private DefaultSettingsProxy() {
        }

        @Override // com.android.ims.ImsManager.SettingsProxy
        public int getSecureIntSetting(ContentResolver cr, String name, int def) {
            return Settings.Secure.getInt(cr, name, def);
        }

        @Override // com.android.ims.ImsManager.SettingsProxy
        public boolean putSecureIntSetting(ContentResolver cr, String name, int value) {
            return Settings.Secure.putInt(cr, name, value);
        }
    }

    /* loaded from: classes.dex */
    private static class DefaultSubscriptionManagerProxy implements SubscriptionManagerProxy {
        private Context mContext;

        public DefaultSubscriptionManagerProxy(Context context) {
            this.mContext = context;
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public boolean isValidSubscriptionId(int subId) {
            return SubscriptionManager.isValidSubscriptionId(subId);
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public int getSubscriptionId(int slotIndex) {
            return SubscriptionManager.getSubscriptionId(slotIndex);
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public int getDefaultVoicePhoneId() {
            return SubscriptionManager.getDefaultVoicePhoneId();
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public int getIntegerSubscriptionProperty(int subId, String propKey, int defValue) {
            return SubscriptionManager.getIntegerSubscriptionProperty(subId, propKey, defValue, this.mContext);
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public void setSubscriptionProperty(int subId, String propKey, String propValue) {
            SubscriptionManager.setSubscriptionProperty(subId, propKey, propValue);
        }

        @Override // com.android.ims.ImsManager.SubscriptionManagerProxy
        public int[] getActiveSubscriptionIdList() {
            return getSubscriptionManager().getActiveSubscriptionIdList();
        }

        private SubscriptionManager getSubscriptionManager() {
            return (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InstanceManager implements FeatureConnector.Listener<ImsManager> {
        private static final int CONNECT_TIMEOUT_MS = 50;
        private CountDownLatch mConnectedLatch;
        private final FeatureConnector<ImsManager> mConnector;
        private final ImsManager mImsManager;
        private final Object mLock = new Object();
        private boolean isConnectorActive = false;

        public InstanceManager(ImsManager manager) {
            this.mImsManager = manager;
            manager.mLogTagPostfix = "IM";
            ArrayList<Integer> readyFilter = new ArrayList<>();
            readyFilter.add(2);
            readyFilter.add(1);
            readyFilter.add(0);
            this.mConnector = new FeatureConnector<>(manager.mContext, manager.mPhoneId, new FeatureConnector.ManagerFactory() { // from class: com.android.ims.ImsManager$InstanceManager$$ExternalSyntheticLambda0
                @Override // com.android.ims.FeatureConnector.ManagerFactory
                public final FeatureUpdates createManager(Context context, int i) {
                    ImsManager lambda$new$0;
                    lambda$new$0 = ImsManager.InstanceManager.this.lambda$new$0(context, i);
                    return lambda$new$0;
                }
            }, "InstanceManager", readyFilter, this, manager.getImsThreadExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsManager lambda$new$0(Context c, int p) {
            return this.mImsManager;
        }

        public ImsManager getInstance() {
            return this.mImsManager;
        }

        public void reconnect() {
            boolean requiresReconnect = false;
            synchronized (this.mLock) {
                if (!this.isConnectorActive) {
                    requiresReconnect = ImsManager.DBG;
                    this.isConnectorActive = ImsManager.DBG;
                    this.mConnectedLatch = new CountDownLatch(1);
                }
            }
            if (requiresReconnect) {
                this.mConnector.connect();
            }
            try {
                if (!this.mConnectedLatch.await(50L, TimeUnit.MILLISECONDS)) {
                    this.mImsManager.log("ImsService not up yet - timeout waiting for connection.");
                }
            } catch (InterruptedException e) {
            }
        }

        @Override // com.android.ims.FeatureConnector.Listener
        public void connectionReady(ImsManager manager, int subId) {
            synchronized (this.mLock) {
                this.mImsManager.logi("connectionReady, subId: " + subId);
                this.mConnectedLatch.countDown();
            }
        }

        @Override // com.android.ims.FeatureConnector.Listener
        public void connectionUnavailable(int reason) {
            synchronized (this.mLock) {
                this.mImsManager.logi("connectionUnavailable, reason: " + reason);
                if (reason == 3) {
                    this.isConnectorActive = false;
                }
                this.mConnectedLatch.countDown();
            }
        }
    }

    public static ImsManager getInstance(Context context, int phoneId) {
        InstanceManager instanceManager;
        SparseArray<InstanceManager> sparseArray = IMS_MANAGER_INSTANCES;
        synchronized (sparseArray) {
            instanceManager = sparseArray.get(phoneId);
            if (instanceManager == null) {
                ImsManager m = new ImsManager(context, phoneId);
                instanceManager = new InstanceManager(m);
                sparseArray.put(phoneId, instanceManager);
            }
        }
        instanceManager.reconnect();
        return instanceManager.getInstance();
    }

    public static FeatureConnector<ImsManager> getConnector(Context context, int phoneId, String logPrefix, FeatureConnector.Listener<ImsManager> listener, Executor executor) {
        ArrayList<Integer> readyFilter = new ArrayList<>();
        readyFilter.add(2);
        return new FeatureConnector<>(context, phoneId, new FeatureConnector.ManagerFactory() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda0
            @Override // com.android.ims.FeatureConnector.ManagerFactory
            public final FeatureUpdates createManager(Context context2, int i) {
                return ImsManager.$r8$lambda$LQs8_Cgd1Mgqi6UQz75OSYvt96E(context2, i);
            }
        }, logPrefix, readyFilter, listener, executor);
    }

    public static boolean isImsSupportedOnDevice(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony.ims");
    }

    public static void setImsStatsCallback(int phoneId, ImsStatsCallback cb) {
        SparseArray<ImsStatsCallback> sparseArray = IMS_STATS_CALLBACKS;
        synchronized (sparseArray) {
            if (cb == null) {
                sparseArray.remove(phoneId);
            } else {
                sparseArray.put(phoneId, cb);
            }
        }
    }

    private static ImsStatsCallback getStatsCallback(int phoneId) {
        ImsStatsCallback imsStatsCallback;
        SparseArray<ImsStatsCallback> sparseArray = IMS_STATS_CALLBACKS;
        synchronized (sparseArray) {
            imsStatsCallback = sparseArray.get(phoneId);
        }
        return imsStatsCallback;
    }

    public static boolean isEnhanced4gLteModeSettingEnabledByUser(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isEnhanced4gLteModeSettingEnabledByUser();
        }
        Rlog.e(TAG, "isEnhanced4gLteModeSettingEnabledByUser: ImsManager null, returning default value.");
        return false;
    }

    public boolean isEnhanced4gLteModeSettingEnabledByUser() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "volte_vt_enabled", -1);
        boolean onByDefault = getBooleanCarrierConfig("enhanced_4g_lte_on_by_default_bool");
        boolean isUiUnEditable = !getBooleanCarrierConfig("editable_enhanced_4g_lte_bool") || getBooleanCarrierConfig("hide_enhanced_4g_lte_bool");
        boolean isSettingNotInitialized = setting == -1;
        if ((isUiUnEditable || isSettingNotInitialized) && !isVoImsOptInEnabled()) {
            return onByDefault;
        }
        if (setting == 1) {
            return DBG;
        }
        return false;
    }

    public static void setEnhanced4gLteModeSetting(Context context, boolean enabled) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setEnhanced4gLteModeSetting(enabled);
        }
        Rlog.e(TAG, "setEnhanced4gLteModeSetting: ImsManager null, value not set.");
    }

    public void setEnhanced4gLteModeSetting(boolean enabled) {
        if (enabled && !isVolteProvisionedOnDevice()) {
            log("setEnhanced4gLteModeSetting: Not possible to enable VoLTE due to provisioning.");
            return;
        }
        int subId = getSubId();
        if (!isSubIdValid(subId)) {
            loge("setEnhanced4gLteModeSetting: invalid sub id, can not set property in  siminfo db; subId=" + subId);
            return;
        }
        boolean isUiUnEditable = !getBooleanCarrierConfig("editable_enhanced_4g_lte_bool") || getBooleanCarrierConfig("hide_enhanced_4g_lte_bool");
        if (isUiUnEditable && !isVoImsOptInEnabled()) {
            enabled = getBooleanCarrierConfig("enhanced_4g_lte_on_by_default_bool");
        }
        int prevSetting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(subId, "volte_vt_enabled", -1);
        if (prevSetting == (enabled ? 1 : 0)) {
            return;
        }
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "volte_vt_enabled", booleanToPropertyString(enabled));
        try {
            if (enabled) {
                CapabilityChangeRequest request = new CapabilityChangeRequest();
                boolean isNonTty = isNonTtyOrTtyOnVolteEnabled();
                updateVoiceCellFeatureValue(request, isNonTty);
                updateVideoCallFeatureValue(request, isNonTty);
                changeMmTelCapability(request);
                turnOnIms();
            } else {
                reevaluateCapabilities();
            }
        } catch (ImsException e) {
            loge("setEnhanced4gLteModeSetting couldn't set config: " + e);
        }
    }

    public static boolean isNonTtyOrTtyOnVolteEnabled(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isNonTtyOrTtyOnVolteEnabled();
        }
        Rlog.e(TAG, "isNonTtyOrTtyOnVolteEnabled: ImsManager null, returning default value.");
        return false;
    }

    public boolean isNonTtyOrTtyOnVolteEnabled() {
        if (isTtyOnVoLteCapable()) {
            return DBG;
        }
        TelecomManager tm = (TelecomManager) this.mContext.getSystemService("telecom");
        if (tm == null) {
            logw("isNonTtyOrTtyOnVolteEnabled: telecom not available");
            return DBG;
        } else if (tm.getCurrentTtyMode() == 0) {
            return DBG;
        } else {
            return false;
        }
    }

    public boolean isTtyOnVoLteCapable() {
        return getBooleanCarrierConfig("carrier_volte_tty_supported_bool");
    }

    public boolean isNonTtyOrTtyOnVoWifiEnabled() {
        if (isTtyOnVoWifiCapable()) {
            return DBG;
        }
        TelecomManager tm = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        if (tm == null) {
            logw("isNonTtyOrTtyOnVoWifiEnabled: telecom not available");
            return DBG;
        } else if (tm.getCurrentTtyMode() == 0) {
            return DBG;
        } else {
            return false;
        }
    }

    public boolean isTtyOnVoWifiCapable() {
        return getBooleanCarrierConfig("carrier_vowifi_tty_supported_bool");
    }

    public static boolean isVolteEnabledByPlatform(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isVolteEnabledByPlatform();
        }
        Rlog.e(TAG, "isVolteEnabledByPlatform: ImsManager null, returning default value.");
        return false;
    }

    public void isSupported(final int capability, final int transportType, final Consumer<Boolean> result) {
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$isSupported$0(transportType, capability, result);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isSupported$0(int transportType, int capability, Consumer result) {
        Boolean valueOf = Boolean.valueOf((boolean) DBG);
        switch (transportType) {
            case 1:
                switch (capability) {
                    case 1:
                        result.accept(Boolean.valueOf(isVolteEnabledByPlatform()));
                        return;
                    case 2:
                        result.accept(Boolean.valueOf(isVtEnabledByPlatform()));
                        return;
                    case 4:
                        result.accept(Boolean.valueOf(isSuppServicesOverUtEnabledByPlatform()));
                        return;
                    case 8:
                        result.accept(valueOf);
                        return;
                }
            case 2:
                switch (capability) {
                    case 1:
                        result.accept(Boolean.valueOf(isWfcEnabledByPlatform()));
                        return;
                    case 2:
                        result.accept(Boolean.valueOf(isVtEnabledByPlatform()));
                        return;
                    case 4:
                        result.accept(Boolean.valueOf(isSuppServicesOverUtEnabledByPlatform()));
                        return;
                    case 8:
                        result.accept(valueOf);
                        return;
                }
        }
        result.accept(false);
    }

    public boolean isVolteEnabledByPlatform() {
        if (SystemProperties.getInt(PROPERTY_DBG_VOLTE_AVAIL_OVERRIDE + Integer.toString(this.mPhoneId), -1) == 1 || SystemProperties.getInt(PROPERTY_DBG_VOLTE_AVAIL_OVERRIDE, -1) == 1 || getLocalImsConfigKeyInt(68) == 1) {
            return DBG;
        }
        if (this.mContext.getResources().getBoolean(17891602) && getBooleanCarrierConfig("carrier_volte_available_bool") && isGbaValid()) {
            return DBG;
        }
        return false;
    }

    public boolean isImsOverNrEnabledByPlatform() {
        int[] nrCarrierCaps = getIntArrayCarrierConfig("carrier_nr_availabilities_int_array");
        if (nrCarrierCaps == null) {
            return false;
        }
        boolean voNrCarrierSupported = Arrays.stream(nrCarrierCaps).anyMatch(new IntPredicate() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda10
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return ImsManager.lambda$isImsOverNrEnabledByPlatform$1(i);
            }
        });
        if (!voNrCarrierSupported) {
            return false;
        }
        return isGbaValid();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isImsOverNrEnabledByPlatform$1(int cap) {
        if (cap == 2) {
            return DBG;
        }
        return false;
    }

    public static boolean isVolteProvisionedOnDevice(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isVolteProvisionedOnDevice();
        }
        Rlog.e(TAG, "isVolteProvisionedOnDevice: ImsManager null, returning default value.");
        return DBG;
    }

    public boolean isVolteProvisionedOnDevice() {
        return isMmTelProvisioningRequired(1, 0) ? isVolteProvisioned() : DBG;
    }

    public boolean isEabProvisionedOnDevice() {
        if (isRcsProvisioningRequired(2, 0)) {
            return isEabProvisioned();
        }
        return DBG;
    }

    public static boolean isWfcProvisionedOnDevice(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isWfcProvisionedOnDevice();
        }
        Rlog.e(TAG, "isWfcProvisionedOnDevice: ImsManager null, returning default value.");
        return DBG;
    }

    public boolean isWfcProvisionedOnDevice() {
        if (getBooleanCarrierConfig("carrier_volte_override_wfc_provisioning_bool") && !isVolteProvisionedOnDevice()) {
            return false;
        }
        if (!isMmTelProvisioningRequired(1, 1)) {
            return DBG;
        }
        return isWfcProvisioned();
    }

    public static boolean isVtProvisionedOnDevice(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isVtProvisionedOnDevice();
        }
        Rlog.e(TAG, "isVtProvisionedOnDevice: ImsManager null, returning default value.");
        return DBG;
    }

    public boolean isVtProvisionedOnDevice() {
        if (isMmTelProvisioningRequired(2, 0)) {
            return isVtProvisioned();
        }
        return DBG;
    }

    public static boolean isVtEnabledByPlatform(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isVtEnabledByPlatform();
        }
        Rlog.e(TAG, "isVtEnabledByPlatform: ImsManager null, returning default value.");
        return false;
    }

    public boolean isVtEnabledByPlatform() {
        if (SystemProperties.getInt(PROPERTY_DBG_VT_AVAIL_OVERRIDE + Integer.toString(this.mPhoneId), -1) == 1 || SystemProperties.getInt(PROPERTY_DBG_VT_AVAIL_OVERRIDE, -1) == 1) {
            return DBG;
        }
        if (this.mContext.getResources().getBoolean(17891603) && getBooleanCarrierConfig("carrier_vt_available_bool") && isGbaValid()) {
            return DBG;
        }
        return false;
    }

    public static boolean isVtEnabledByUser(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isVtEnabledByUser();
        }
        Rlog.e(TAG, "isVtEnabledByUser: ImsManager null, returning default value.");
        return false;
    }

    public boolean isVtEnabledByUser() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "vt_ims_enabled", -1);
        if (setting == -1 || setting == 1) {
            return DBG;
        }
        return false;
    }

    public boolean isCallComposerEnabledByUser() {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (tm == null) {
            loge("isCallComposerEnabledByUser: TelephonyManager is null, returning false");
            return false;
        } else if (tm.getCallComposerStatus() == 1) {
            return DBG;
        } else {
            return false;
        }
    }

    public static void setVtSetting(Context context, boolean enabled) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setVtSetting(enabled);
        }
        Rlog.e(TAG, "setVtSetting: ImsManager null, can not set value.");
    }

    public void setVtSetting(boolean enabled) {
        if (enabled && !isVtProvisionedOnDevice()) {
            log("setVtSetting: Not possible to enable Vt due to provisioning.");
            return;
        }
        int subId = getSubId();
        if (!isSubIdValid(subId)) {
            loge("setVtSetting: sub id invalid, skip modifying vt state in subinfo db; subId=" + subId);
            return;
        }
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "vt_ims_enabled", booleanToPropertyString(enabled));
        try {
            if (enabled) {
                CapabilityChangeRequest request = new CapabilityChangeRequest();
                updateVideoCallFeatureValue(request, isNonTtyOrTtyOnVolteEnabled());
                changeMmTelCapability(request);
                turnOnIms();
            } else {
                reevaluateCapabilities();
            }
        } catch (ImsException e) {
            loge("setVtSetting(b): ", e);
        }
    }

    private boolean isTurnOffImsAllowedByPlatform() {
        return (SystemProperties.getInt(new StringBuilder().append(PROPERTY_DBG_ALLOW_IMS_OFF_OVERRIDE).append(Integer.toString(this.mPhoneId)).toString(), -1) == 1 || SystemProperties.getInt(PROPERTY_DBG_ALLOW_IMS_OFF_OVERRIDE, -1) == 1) ? DBG : getBooleanCarrierConfig("carrier_allow_turnoff_ims_bool");
    }

    public static boolean isWfcEnabledByUser(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isWfcEnabledByUser();
        }
        Rlog.e(TAG, "isWfcEnabledByUser: ImsManager null, returning default value.");
        return DBG;
    }

    public boolean isWfcEnabledByUser() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "wfc_ims_enabled", -1);
        if (setting == -1) {
            return getBooleanCarrierConfig("carrier_default_wfc_ims_enabled_bool");
        }
        if (setting == 1) {
            return DBG;
        }
        return false;
    }

    public static void setWfcSetting(Context context, boolean enabled) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setWfcSetting(enabled);
        }
        Rlog.e(TAG, "setWfcSetting: ImsManager null, can not set value.");
    }

    public void setWfcSetting(boolean enabled) {
        if (enabled && !isWfcProvisionedOnDevice()) {
            log("setWfcSetting: Not possible to enable WFC due to provisioning.");
            return;
        }
        int subId = getSubId();
        if (!isSubIdValid(subId)) {
            loge("setWfcSetting: invalid sub id, can not set WFC setting in siminfo db; subId=" + subId);
            return;
        }
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_enabled", booleanToPropertyString(enabled));
        try {
            if (enabled) {
                boolean isNonTtyWifi = isNonTtyOrTtyOnVoWifiEnabled();
                CapabilityChangeRequest request = new CapabilityChangeRequest();
                updateVoiceWifiFeatureAndProvisionedValues(request, isNonTtyWifi);
                changeMmTelCapability(request);
                turnOnIms();
            } else {
                reevaluateCapabilities();
            }
        } catch (ImsException e) {
            loge("setWfcSetting: " + e);
        }
    }

    public boolean isCrossSimCallingEnabledByUser() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "cross_sim_calling_enabled", -1);
        if (setting == -1 || setting != 1) {
            return false;
        }
        return DBG;
    }

    public boolean isCrossSimCallingEnabled() {
        boolean userEnabled = isCrossSimCallingEnabledByUser();
        boolean platformEnabled = isCrossSimEnabledByPlatform();
        boolean isProvisioned = isWfcProvisionedOnDevice();
        log("isCrossSimCallingEnabled: platformEnabled = " + platformEnabled + ", provisioned = " + isProvisioned + ", userEnabled = " + userEnabled);
        if (userEnabled && platformEnabled && isProvisioned) {
            return DBG;
        }
        return false;
    }

    public void setCrossSimCallingEnabled(boolean enabled) {
        if (enabled && !isWfcProvisionedOnDevice()) {
            log("setCrossSimCallingEnabled: Not possible to enable WFC due to provisioning.");
            return;
        }
        int subId = getSubId();
        if (!isSubIdValid(subId)) {
            loge("setCrossSimCallingEnabled: invalid sub id, can not set Cross SIM setting in siminfo db; subId=" + subId);
            return;
        }
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "cross_sim_calling_enabled", booleanToPropertyString(enabled));
        try {
            if (enabled) {
                CapabilityChangeRequest request = new CapabilityChangeRequest();
                updateCrossSimFeatureAndProvisionedValues(request);
                changeMmTelCapability(request);
                turnOnIms();
            } else {
                reevaluateCapabilities();
            }
        } catch (ImsException e) {
            loge("setCrossSimCallingEnabled(): ", e);
        }
    }

    public void setWfcNonPersistent(boolean enabled, int wfcMode) {
        boolean z = DBG;
        int imsWfcModeFeatureValue = enabled ? wfcMode : 1;
        try {
            changeMmTelCapability(enabled, 1, 1);
            setWfcModeInternal(imsWfcModeFeatureValue);
            if (!enabled || !isWfcRoamingEnabledByUser()) {
                z = false;
            }
            setWfcRoamingSettingInternal(z);
            if (enabled) {
                log("setWfcNonPersistent() : turnOnIms");
                turnOnIms();
            }
        } catch (ImsException e) {
            loge("setWfcNonPersistent(): ", e);
        }
    }

    public static int getWfcMode(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.getWfcMode();
        }
        Rlog.e(TAG, "getWfcMode: ImsManager null, returning default value.");
        return 0;
    }

    public int getWfcMode() {
        return getWfcMode(false);
    }

    public static void setWfcMode(Context context, int wfcMode) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setWfcMode(wfcMode);
        }
        Rlog.e(TAG, "setWfcMode: ImsManager null, can not set value.");
    }

    public void setWfcMode(int wfcMode) {
        setWfcMode(wfcMode, false);
    }

    public static int getWfcMode(Context context, boolean roaming) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.getWfcMode(roaming);
        }
        Rlog.e(TAG, "getWfcMode: ImsManager null, returning default value.");
        return 0;
    }

    public int getWfcMode(boolean roaming) {
        int setting;
        if (!roaming) {
            if (!getBooleanCarrierConfig("editable_wfc_mode_bool")) {
                setting = getIntCarrierConfig("carrier_default_wfc_ims_mode_int");
            } else {
                setting = getSettingFromSubscriptionManager("wfc_ims_mode", "carrier_default_wfc_ims_mode_int");
            }
            log("getWfcMode - setting=" + setting);
        } else {
            if (getBooleanCarrierConfig("use_wfc_home_network_mode_in_roaming_network_bool")) {
                setting = getWfcMode(false);
            } else if (!getBooleanCarrierConfig("editable_wfc_roaming_mode_bool")) {
                setting = getIntCarrierConfig("carrier_default_wfc_ims_roaming_mode_int");
            } else {
                setting = getSettingFromSubscriptionManager("wfc_ims_roaming_mode", "carrier_default_wfc_ims_roaming_mode_int");
            }
            log("getWfcMode (roaming) - setting=" + setting);
        }
        return setting;
    }

    private int getSettingFromSubscriptionManager(String subSetting, String defaultConfigKey) {
        int result = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), subSetting, -1);
        if (result == -1) {
            return getIntCarrierConfig(defaultConfigKey);
        }
        return result;
    }

    public static void setWfcMode(Context context, int wfcMode, boolean roaming) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setWfcMode(wfcMode, roaming);
        }
        Rlog.e(TAG, "setWfcMode: ImsManager null, can not set value.");
    }

    public void setWfcMode(int wfcMode, boolean roaming) {
        int subId = getSubId();
        if (isSubIdValid(subId)) {
            if (!roaming) {
                log("setWfcMode(i,b) - setting=" + wfcMode);
                this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_mode", Integer.toString(wfcMode));
            } else {
                log("setWfcMode(i,b) (roaming) - setting=" + wfcMode);
                this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_roaming_mode", Integer.toString(wfcMode));
            }
        } else {
            loge("setWfcMode(i,b): invalid sub id, skip setting setting in siminfo db; subId=" + subId);
        }
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService("phone");
        if (tm == null) {
            loge("setWfcMode: TelephonyManager is null, can not set WFC.");
        } else if (roaming == tm.createForSubscriptionId(getSubId()).isNetworkRoaming()) {
            setWfcModeInternal(wfcMode);
        }
    }

    private int getSubId() {
        return this.mSubscriptionManagerProxy.getSubscriptionId(this.mPhoneId);
    }

    private void setWfcModeInternal(final int wfcMode) {
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$setWfcModeInternal$2(wfcMode);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setWfcModeInternal$2(int value) {
        try {
            getConfigInterface().setConfig(27, value);
        } catch (ImsException e) {
        }
    }

    public static boolean isWfcRoamingEnabledByUser(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isWfcRoamingEnabledByUser();
        }
        Rlog.e(TAG, "isWfcRoamingEnabledByUser: ImsManager null, returning default value.");
        return false;
    }

    public boolean isWfcRoamingEnabledByUser() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "wfc_ims_roaming_enabled", -1);
        if (setting == -1) {
            return getBooleanCarrierConfig("carrier_default_wfc_ims_roaming_enabled_bool");
        }
        if (setting == 1) {
            return DBG;
        }
        return false;
    }

    public static void setWfcRoamingSetting(Context context, boolean enabled) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.setWfcRoamingSetting(enabled);
        }
        Rlog.e(TAG, "setWfcRoamingSetting: ImsManager null, value not set.");
    }

    public void setWfcRoamingSetting(boolean enabled) {
        this.mSubscriptionManagerProxy.setSubscriptionProperty(getSubId(), "wfc_ims_roaming_enabled", booleanToPropertyString(enabled));
        setWfcRoamingSettingInternal(enabled);
    }

    private void setWfcRoamingSettingInternal(boolean enabled) {
        final int value;
        if (enabled) {
            value = 1;
        } else {
            value = 0;
        }
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$setWfcRoamingSettingInternal$3(value);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setWfcRoamingSettingInternal$3(int value) {
        try {
            getConfigInterface().setConfig(26, value);
        } catch (ImsException e) {
        }
    }

    public static boolean isWfcEnabledByPlatform(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            return mgr.isWfcEnabledByPlatform();
        }
        Rlog.e(TAG, "isWfcEnabledByPlatform: ImsManager null, returning default value.");
        return false;
    }

    public boolean isWfcEnabledByPlatform() {
        if (SystemProperties.getInt(PROPERTY_DBG_WFC_AVAIL_OVERRIDE + Integer.toString(this.mPhoneId), -1) == 1 || SystemProperties.getInt(PROPERTY_DBG_WFC_AVAIL_OVERRIDE, -1) == 1) {
            return DBG;
        }
        if (this.mContext.getResources().getBoolean(17891604) && getBooleanCarrierConfig("carrier_wfc_ims_available_bool") && isGbaValid()) {
            return DBG;
        }
        return false;
    }

    public boolean isCrossSimEnabledByPlatform() {
        if (isWfcEnabledByPlatform()) {
            return getBooleanCarrierConfig("carrier_cross_sim_ims_available_bool");
        }
        return false;
    }

    public boolean isSuppServicesOverUtEnabledByPlatform() {
        TelephonyManager manager = (TelephonyManager) this.mContext.getSystemService("phone");
        int cardState = manager.getSimState(this.mPhoneId);
        if (cardState == 5 && getBooleanCarrierConfig("carrier_supports_ss_over_ut_bool") && isGbaValid()) {
            return DBG;
        }
        return false;
    }

    private boolean isGbaValid() {
        boolean booleanCarrierConfig = getBooleanCarrierConfig("carrier_ims_gba_required_bool");
        boolean result = DBG;
        if (booleanCarrierConfig) {
            TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (tm == null) {
                loge("isGbaValid: TelephonyManager is null, returning false.");
                return false;
            }
            String efIst = tm.createForSubscriptionId(getSubId()).getIsimIst();
            if (efIst == null) {
                loge("isGbaValid - ISF is NULL");
                return DBG;
            }
            if (efIst == null || efIst.length() <= 1 || (((byte) efIst.charAt(1)) & 2) == 0) {
                result = false;
            }
            log("isGbaValid - GBA capable=" + result + ", ISF=" + efIst);
            return result;
        }
        return DBG;
    }

    private boolean getImsProvisionedBoolNoException(int capability, int tech) {
        int subId = getSubId();
        if (subId == -1) {
            logw("getImsProvisionedBoolNoException subId is invalid");
            return false;
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            logw("getImsProvisionedBoolNoException ITelephony interface is invalid");
            return false;
        }
        try {
            return iTelephony.getImsProvisioningStatusForCapability(subId, capability, tech);
        } catch (RemoteException | IllegalArgumentException e) {
            logw("getImsProvisionedBoolNoException: operation failed for capability=" + capability + ". Exception:" + e.getMessage() + ". Returning false.");
            return false;
        }
    }

    private boolean getRcsProvisionedBoolNoException(int capability, int tech) {
        int subId = getSubId();
        if (subId == -1) {
            logw("getRcsProvisionedBoolNoException subId is invalid");
            return false;
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            logw("getRcsProvisionedBoolNoException ITelephony interface is invalid");
            return false;
        }
        try {
            return iTelephony.getRcsProvisioningStatusForCapability(subId, capability, tech);
        } catch (RemoteException | IllegalArgumentException e) {
            logw("getRcsProvisionedBoolNoException: operation failed for capability=" + capability + ". Exception:" + e.getMessage() + ". Returning false.");
            return false;
        }
    }

    public void updateImsServiceConfig() {
        try {
            int subId = getSubId();
            if (!isSubIdValid(subId)) {
                loge("updateImsServiceConfig: invalid sub id, skipping!");
                return;
            }
            PersistableBundle imsCarrierConfigs = this.mConfigManager.getConfigByComponentForSubId("ims.", subId);
            updateImsCarrierConfigs(imsCarrierConfigs);
            reevaluateCapabilities();
            this.mConfigUpdated = DBG;
        } catch (ImsException e) {
            loge("updateImsServiceConfig: ", e);
            this.mConfigUpdated = false;
        }
    }

    private void reevaluateCapabilities() throws ImsException {
        logi("reevaluateCapabilities");
        CapabilityChangeRequest request = new CapabilityChangeRequest();
        boolean isNonTty = isNonTtyOrTtyOnVolteEnabled();
        boolean isNonTtyWifi = isNonTtyOrTtyOnVoWifiEnabled();
        updateVoiceCellFeatureValue(request, isNonTty);
        updateVoiceWifiFeatureAndProvisionedValues(request, isNonTtyWifi);
        updateCrossSimFeatureAndProvisionedValues(request);
        updateVideoCallFeatureValue(request, isNonTty);
        updateCallComposerFeatureValue(request);
        boolean isImsNeededForRtt = (updateRttConfigValue() && isActiveSubscriptionPresent()) ? DBG : false;
        updateUtFeatureValue(request);
        changeMmTelCapability(request);
        if (isImsNeededForRtt || !isTurnOffImsAllowedByPlatform() || isImsNeeded(request)) {
            log("reevaluateCapabilities: turnOnIms");
            turnOnIms();
            return;
        }
        log("reevaluateCapabilities: turnOffIms");
        turnOffIms();
    }

    private boolean isImsNeeded(CapabilityChangeRequest r) {
        return r.getCapabilitiesToEnable().stream().anyMatch(new Predicate() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isImsNeeded$4;
                lambda$isImsNeeded$4 = ImsManager.this.lambda$isImsNeeded$4((CapabilityChangeRequest.CapabilityPair) obj);
                return lambda$isImsNeeded$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isImsNeeded$4(CapabilityChangeRequest.CapabilityPair c) {
        return isImsNeededForCapability(c.getCapability());
    }

    private boolean isImsNeededForCapability(int capability) {
        if (capability == 4 || capability == 16) {
            return false;
        }
        return DBG;
    }

    private void updateVoiceCellFeatureValue(CapabilityChangeRequest request, boolean isNonTty) {
        boolean available = isVolteEnabledByPlatform();
        boolean enabled = isEnhanced4gLteModeSettingEnabledByUser();
        boolean isProvisioned = isVolteProvisionedOnDevice();
        boolean voLteFeatureOn = available && enabled && isNonTty && isProvisioned;
        boolean voNrAvailable = isImsOverNrEnabledByPlatform();
        log("updateVoiceCellFeatureValue: available = " + available + ", enabled = " + enabled + ", nonTTY = " + isNonTty + ", provisioned = " + isProvisioned + ", voLteFeatureOn = " + voLteFeatureOn + ", voNrAvailable = " + voNrAvailable);
        if (voLteFeatureOn) {
            request.addCapabilitiesToEnableForTech(1, 0);
        } else {
            request.addCapabilitiesToDisableForTech(1, 0);
        }
        if (voLteFeatureOn && voNrAvailable) {
            request.addCapabilitiesToEnableForTech(1, 3);
        } else {
            request.addCapabilitiesToDisableForTech(1, 3);
        }
    }

    private void updateVideoCallFeatureValue(CapabilityChangeRequest request, boolean isNonTty) {
        boolean available = isVtEnabledByPlatform();
        boolean vtEnabled = isVtEnabledByUser();
        boolean advancedEnabled = isEnhanced4gLteModeSettingEnabledByUser();
        boolean isDataEnabled = isDataEnabled();
        boolean ignoreDataEnabledChanged = getBooleanCarrierConfig("ignore_data_enabled_changed_for_video_calls");
        boolean isProvisioned = isVtProvisionedOnDevice();
        boolean isLteFeatureOn = (available && vtEnabled && isNonTty && isProvisioned && advancedEnabled && (ignoreDataEnabledChanged || isDataEnabled)) ? DBG : false;
        boolean nrAvailable = isImsOverNrEnabledByPlatform();
        log("updateVideoCallFeatureValue: available = " + available + ", vtenabled = " + vtEnabled + ", advancedCallEnabled = " + advancedEnabled + ", nonTTY = " + isNonTty + ", data enabled = " + isDataEnabled + ", provisioned = " + isProvisioned + ", isLteFeatureOn = " + isLteFeatureOn + ", nrAvailable = " + nrAvailable);
        if (isLteFeatureOn) {
            request.addCapabilitiesToEnableForTech(2, 0);
        } else {
            request.addCapabilitiesToDisableForTech(2, 0);
        }
        if (isLteFeatureOn && nrAvailable) {
            request.addCapabilitiesToEnableForTech(2, 3);
        } else {
            request.addCapabilitiesToDisableForTech(2, 3);
        }
    }

    private void updateVoiceWifiFeatureAndProvisionedValues(CapabilityChangeRequest request, boolean isNonTty) {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        boolean isNetworkRoaming = false;
        if (tm == null) {
            loge("updateVoiceWifiFeatureAndProvisionedValues: TelephonyManager is null, assuming not roaming.");
        } else {
            isNetworkRoaming = tm.createForSubscriptionId(getSubId()).isNetworkRoaming();
        }
        boolean available = isWfcEnabledByPlatform();
        boolean enabled = isWfcEnabledByUser();
        boolean isProvisioned = isWfcProvisionedOnDevice();
        int mode = getWfcMode(isNetworkRoaming);
        boolean roaming = isWfcRoamingEnabledByUser();
        boolean isFeatureOn = available && enabled && isProvisioned;
        log("updateWfcFeatureAndProvisionedValues: available = " + available + ", enabled = " + enabled + ", mode = " + mode + ", provisioned = " + isProvisioned + ", roaming = " + roaming + ", isFeatureOn = " + isFeatureOn + ", isNonTtyWifi = " + isNonTty);
        if (isFeatureOn && isNonTty) {
            request.addCapabilitiesToEnableForTech(1, 1);
        } else {
            request.addCapabilitiesToDisableForTech(1, 1);
        }
        if (!isFeatureOn) {
            mode = 1;
            roaming = false;
        }
        setWfcModeInternal(mode);
        setWfcRoamingSettingInternal(roaming);
    }

    private void updateCrossSimFeatureAndProvisionedValues(CapabilityChangeRequest request) {
        if (isCrossSimCallingEnabled()) {
            request.addCapabilitiesToEnableForTech(1, 2);
        } else {
            request.addCapabilitiesToDisableForTech(1, 2);
        }
    }

    private void updateUtFeatureValue(CapabilityChangeRequest request) {
        ITelephony telephony;
        boolean isCarrierSupported = isSuppServicesOverUtEnabledByPlatform();
        boolean isMmTelProvisioningRequired = isMmTelProvisioningRequired(4, 0);
        boolean isFeatureOn = DBG;
        boolean requiresProvisioning = isMmTelProvisioningRequired || getBooleanCarrierConfig("carrier_ut_provisioning_required_bool");
        boolean isProvisioned = DBG;
        if (requiresProvisioning && (telephony = getITelephony()) != null) {
            try {
                isProvisioned = telephony.getImsProvisioningStatusForCapability(getSubId(), 4, 0);
            } catch (RemoteException e) {
                loge("updateUtFeatureValue: couldn't reach telephony! returning provisioned");
            }
        }
        if (!isCarrierSupported || !isProvisioned) {
            isFeatureOn = false;
        }
        log("updateUtFeatureValue: available = " + isCarrierSupported + ", isProvisioned = " + isProvisioned + ", enabled = " + isFeatureOn);
        if (isFeatureOn) {
            request.addCapabilitiesToEnableForTech(4, 0);
        } else {
            request.addCapabilitiesToDisableForTech(4, 0);
        }
    }

    private void updateCallComposerFeatureValue(CapabilityChangeRequest request) {
        boolean isUserSetEnabled = isCallComposerEnabledByUser();
        boolean isCarrierConfigEnabled = getBooleanCarrierConfig("supports_call_composer_bool");
        boolean isFeatureOn = (isUserSetEnabled && isCarrierConfigEnabled) ? DBG : false;
        boolean nrAvailable = isImsOverNrEnabledByPlatform();
        log("updateCallComposerFeatureValue: isUserSetEnabled = " + isUserSetEnabled + ", isCarrierConfigEnabled = " + isCarrierConfigEnabled + ", isFeatureOn = " + isFeatureOn + ", nrAvailable = " + nrAvailable);
        if (isFeatureOn) {
            request.addCapabilitiesToEnableForTech(16, 0);
        } else {
            request.addCapabilitiesToDisableForTech(16, 0);
        }
        if (isFeatureOn && nrAvailable) {
            request.addCapabilitiesToEnableForTech(16, 3);
        } else {
            request.addCapabilitiesToDisableForTech(16, 3);
        }
    }

    private ImsManager(Context context, int phoneId) {
        this.mMmTelFeatureConnectionFactory = new MmTelFeatureConnectionFactory() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda1
            @Override // com.android.ims.ImsManager.MmTelFeatureConnectionFactory
            public final MmTelFeatureConnection create(Context context2, int i, int i2, IImsMmTelFeature iImsMmTelFeature, IImsConfig iImsConfig, IImsRegistration iImsRegistration, ISipTransport iSipTransport) {
                return new MmTelFeatureConnection(context2, i, i2, iImsMmTelFeature, iImsConfig, iImsRegistration, iSipTransport);
            }
        };
        this.mMmTelConnectionRef = new AtomicReference<>();
        this.mConfigUpdated = false;
        this.mLogTagPostfix = "";
        this.mContext = context;
        this.mPhoneId = phoneId;
        this.mSubscriptionManagerProxy = new DefaultSubscriptionManagerProxy(context);
        this.mSettingsProxy = new DefaultSettingsProxy();
        this.mConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        this.mExecutor = new LazyExecutor();
        this.mBinderCache = new BinderCacheManager<>(new BinderCacheManager.BinderInterfaceFactory() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda2
            public final Object create() {
                ITelephony iTelephonyInterface;
                iTelephonyInterface = ImsManager.getITelephonyInterface();
                return iTelephonyInterface;
            }
        });
        associate(null, -1);
    }

    public ImsManager(Context context, int phoneId, MmTelFeatureConnectionFactory factory, SubscriptionManagerProxy subManagerProxy, SettingsProxy settingsProxy, BinderCacheManager binderCacheManager) {
        this.mMmTelFeatureConnectionFactory = new MmTelFeatureConnectionFactory() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda1
            @Override // com.android.ims.ImsManager.MmTelFeatureConnectionFactory
            public final MmTelFeatureConnection create(Context context2, int i, int i2, IImsMmTelFeature iImsMmTelFeature, IImsConfig iImsConfig, IImsRegistration iImsRegistration, ISipTransport iSipTransport) {
                return new MmTelFeatureConnection(context2, i, i2, iImsMmTelFeature, iImsConfig, iImsRegistration, iSipTransport);
            }
        };
        this.mMmTelConnectionRef = new AtomicReference<>();
        this.mConfigUpdated = false;
        this.mLogTagPostfix = "";
        this.mContext = context;
        this.mPhoneId = phoneId;
        this.mMmTelFeatureConnectionFactory = factory;
        this.mSubscriptionManagerProxy = subManagerProxy;
        this.mSettingsProxy = settingsProxy;
        this.mConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        this.mExecutor = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
        this.mBinderCache = binderCacheManager;
        associate(null, -1);
    }

    public boolean isServiceAvailable() {
        return this.mMmTelConnectionRef.get().isBinderAlive();
    }

    public boolean isServiceReady() {
        return this.mMmTelConnectionRef.get().isBinderReady();
    }

    public void open(MmTelFeature.Listener listener, ImsEcbmStateListener ecbmListener, ImsExternalCallStateListener multiEndpointListener) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        try {
            c.openConnection(listener, ecbmListener, multiEndpointListener);
        } catch (RemoteException e) {
            throw new ImsException("open()", e, 106);
        }
    }

    public void addRegistrationListener(int serviceClass, ImsConnectionStateListener listener) throws ImsException {
        addRegistrationListener(listener);
    }

    public void addRegistrationListener(final ImsConnectionStateListener listener) throws ImsException {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        addRegistrationCallback(listener, getImsThreadExecutor());
        addCapabilitiesCallback(new ImsMmTelManager.CapabilityCallback() { // from class: com.android.ims.ImsManager.1
            @Override // android.telephony.ims.ImsMmTelManager.CapabilityCallback
            public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities capabilities) {
                listener.onFeatureCapabilityChangedAdapter(ImsManager.this.getRegistrationTech(), capabilities);
            }
        }, getImsThreadExecutor());
        log("Registration Callback registered.");
    }

    public void addRegistrationCallback(RegistrationManager.RegistrationCallback callback, Executor executor) throws ImsException {
        if (callback == null) {
            throw new NullPointerException("registration callback can't be null");
        }
        try {
            callback.setExecutor(executor);
            this.mMmTelConnectionRef.get().addRegistrationCallback(callback.getBinder());
            log("Registration Callback registered.");
        } catch (IllegalStateException e) {
            throw new ImsException("addRegistrationCallback(IRIB)", e, 106);
        }
    }

    public void removeRegistrationListener(RegistrationManager.RegistrationCallback callback) {
        if (callback == null) {
            throw new NullPointerException("registration callback can't be null");
        }
        this.mMmTelConnectionRef.get().removeRegistrationCallback(callback.getBinder());
        log("Registration callback removed.");
    }

    public void addRegistrationCallbackForSubscription(IImsRegistrationCallback callback, int subId) throws RemoteException {
        if (callback == null) {
            throw new IllegalArgumentException("registration callback can't be null");
        }
        this.mMmTelConnectionRef.get().addRegistrationCallbackForSubscription(callback, subId);
        log("Registration Callback registered.");
    }

    public void removeRegistrationCallbackForSubscription(IImsRegistrationCallback callback, int subId) {
        if (callback == null) {
            throw new IllegalArgumentException("registration callback can't be null");
        }
        this.mMmTelConnectionRef.get().removeRegistrationCallbackForSubscription(callback, subId);
    }

    public void addCapabilitiesCallback(ImsMmTelManager.CapabilityCallback callback, Executor executor) throws ImsException {
        if (callback == null) {
            throw new NullPointerException("capabilities callback can't be null");
        }
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            callback.setExecutor(executor);
            c.addCapabilityCallback(callback.getBinder());
            log("Capability Callback registered.");
        } catch (IllegalStateException e) {
            throw new ImsException("addCapabilitiesCallback(IF)", e, 106);
        }
    }

    public void removeCapabilitiesCallback(ImsMmTelManager.CapabilityCallback callback) {
        if (callback == null) {
            throw new NullPointerException("capabilities callback can't be null");
        }
        try {
            MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
            c.removeCapabilityCallback(callback.getBinder());
        } catch (ImsException e) {
            log("Exception removing Capability , exception=" + e);
        }
    }

    public void addCapabilitiesCallbackForSubscription(IImsCapabilityCallback callback, int subId) throws RemoteException {
        if (callback == null) {
            throw new IllegalArgumentException("registration callback can't be null");
        }
        this.mMmTelConnectionRef.get().addCapabilityCallbackForSubscription(callback, subId);
        log("Capability Callback registered for subscription.");
    }

    public void removeCapabilitiesCallbackForSubscription(IImsCapabilityCallback callback, int subId) {
        if (callback == null) {
            throw new IllegalArgumentException("capabilities callback can't be null");
        }
        this.mMmTelConnectionRef.get().removeCapabilityCallbackForSubscription(callback, subId);
    }

    public void removeRegistrationListener(ImsConnectionStateListener listener) throws ImsException {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        c.removeRegistrationCallback(listener.getBinder());
        log("Registration Callback/Listener registered.");
    }

    public void addProvisioningCallbackForSubscription(IImsConfigCallback callback, int subId) {
        if (callback == null) {
            throw new IllegalArgumentException("provisioning callback can't be null");
        }
        this.mMmTelConnectionRef.get().addProvisioningCallbackForSubscription(callback, subId);
        log("Capability Callback registered for subscription.");
    }

    public void removeProvisioningCallbackForSubscription(IImsConfigCallback callback, int subId) {
        if (callback == null) {
            throw new IllegalArgumentException("provisioning callback can't be null");
        }
        this.mMmTelConnectionRef.get().removeProvisioningCallbackForSubscription(callback, subId);
    }

    public int getRegistrationTech() {
        try {
            return this.mMmTelConnectionRef.get().getRegistrationTech();
        } catch (RemoteException e) {
            logw("getRegistrationTech: no connection to ImsService.");
            return -1;
        }
    }

    public void getRegistrationTech(final Consumer<Integer> callback) {
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$getRegistrationTech$5(callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getRegistrationTech$5(Consumer callback) {
        try {
            int tech = this.mMmTelConnectionRef.get().getRegistrationTech();
            callback.accept(Integer.valueOf(tech));
        } catch (RemoteException e) {
            logw("getRegistrationTech(C): no connection to ImsService.");
            callback.accept(-1);
        }
    }

    public void close() {
        this.mMmTelConnectionRef.get().closeConnection();
    }

    public ImsUtInterface createOrGetSupplementaryServiceConfiguration() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            ImsUt iUt = c.createOrGetUtInterface();
            if (iUt == null) {
                throw new ImsException("getSupplementaryServiceConfiguration()", 801);
            }
            return iUt;
        } catch (RemoteException e) {
            throw new ImsException("getSupplementaryServiceConfiguration()", e, 106);
        }
    }

    public ImsCallProfile createCallProfile(int serviceType, int callType) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            return c.createCallProfile(serviceType, callType);
        } catch (RemoteException e) {
            throw new ImsException("createCallProfile()", e, 106);
        }
    }

    public void setOfferedRtpHeaderExtensionTypes(Set<RtpHeaderExtensionType> types) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.changeOfferedRtpHeaderExtensionTypes(types);
        } catch (RemoteException e) {
            throw new ImsException("setOfferedRtpHeaderExtensionTypes()", e, 106);
        }
    }

    public ImsCall makeCall(ImsCallProfile profile, String[] callees, ImsCall.Listener listener) throws ImsException {
        log("makeCall :: profile=" + profile);
        getOrThrowExceptionIfServiceUnavailable();
        ImsCall call = new ImsCall(this.mContext, profile);
        call.setListener(listener);
        ImsCallSession session = createCallSession(profile);
        if (callees != null && callees.length == 1 && !session.isMultiparty()) {
            call.start(session, callees[0]);
        } else {
            call.start(session, callees);
        }
        return call;
    }

    public ImsCall takeCall(IImsCallSession session, ImsCall.Listener listener) throws ImsException {
        getOrThrowExceptionIfServiceUnavailable();
        try {
            if (session == null) {
                throw new ImsException("No pending session for the call", 107);
            }
            ImsCall call = new ImsCall(this.mContext, session.getCallProfile());
            call.attachSession(new ImsCallSession(session));
            call.setListener(listener);
            return call;
        } catch (Throwable t) {
            loge("takeCall caught: ", t);
            throw new ImsException("takeCall()", t, 0);
        }
    }

    public ImsConfig getConfigInterface() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        IImsConfig config = c.getConfig();
        if (config == null) {
            throw new ImsException("getConfigInterface()", 131);
        }
        return new ImsConfig(config);
    }

    public void changeMmTelCapability(boolean isEnabled, int capability, int... radioTechs) throws ImsException {
        CapabilityChangeRequest request = new CapabilityChangeRequest();
        int i = 0;
        if (isEnabled) {
            int length = radioTechs.length;
            while (i < length) {
                int tech = radioTechs[i];
                request.addCapabilitiesToEnableForTech(capability, tech);
                i++;
            }
        } else {
            int length2 = radioTechs.length;
            while (i < length2) {
                int tech2 = radioTechs[i];
                request.addCapabilitiesToDisableForTech(capability, tech2);
                i++;
            }
        }
        changeMmTelCapability(request);
    }

    private void changeMmTelCapability(CapabilityChangeRequest r) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            logi("changeMmTelCapability: changing capabilities for sub: " + getSubId() + ", request: " + r);
            c.changeEnabledCapabilities(r, null);
            ImsStatsCallback cb = getStatsCallback(this.mPhoneId);
            if (cb == null) {
                return;
            }
            for (CapabilityChangeRequest.CapabilityPair enabledCaps : r.getCapabilitiesToEnable()) {
                cb.onEnabledMmTelCapabilitiesChanged(enabledCaps.getCapability(), enabledCaps.getRadioTech(), DBG);
            }
            for (CapabilityChangeRequest.CapabilityPair disabledCaps : r.getCapabilitiesToDisable()) {
                cb.onEnabledMmTelCapabilitiesChanged(disabledCaps.getCapability(), disabledCaps.getRadioTech(), false);
            }
        } catch (RemoteException e) {
            throw new ImsException("changeMmTelCapability(CCR)", e, 106);
        }
    }

    private boolean updateRttConfigValue() {
        boolean isActiveSubscriptionPresent = isActiveSubscriptionPresent();
        boolean isCarrierSupported = getBooleanCarrierConfig("rtt_supported_bool") || !isActiveSubscriptionPresent;
        int defaultRttMode = getIntCarrierConfig("default_rtt_mode_int");
        int rttMode = this.mSettingsProxy.getSecureIntSetting(this.mContext.getContentResolver(), "rtt_calling_mode", defaultRttMode);
        logi("defaultRttMode = " + defaultRttMode + " rttMode = " + rttMode);
        boolean isRttAlwaysOnCarrierConfig = getBooleanCarrierConfig("ignore_rtt_mode_setting_bool");
        if (isRttAlwaysOnCarrierConfig && rttMode == defaultRttMode) {
            this.mSettingsProxy.putSecureIntSetting(this.mContext.getContentResolver(), "rtt_calling_mode", defaultRttMode);
        }
        boolean isRttUiSettingEnabled = this.mSettingsProxy.getSecureIntSetting(this.mContext.getContentResolver(), "rtt_calling_mode", 0) != 0;
        boolean shouldImsRttBeOn = isRttUiSettingEnabled || isRttAlwaysOnCarrierConfig;
        logi("update RTT: settings value: " + isRttUiSettingEnabled + " always-on carrierconfig: " + isRttAlwaysOnCarrierConfig + "isActiveSubscriptionPresent: " + isActiveSubscriptionPresent);
        if (isCarrierSupported) {
            setRttConfig(shouldImsRttBeOn);
        } else {
            setRttConfig(false);
        }
        if (isCarrierSupported && shouldImsRttBeOn) {
            return DBG;
        }
        return false;
    }

    private void setRttConfig(final boolean enabled) {
        final int value = enabled ? 1 : 0;
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$setRttConfig$6(enabled, value);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setRttConfig$6(boolean enabled, int value) {
        try {
            logi("Setting RTT enabled to " + enabled);
            getConfigInterface().setProvisionedValue(66, value);
        } catch (ImsException e) {
            loge("Unable to set RTT value enabled to " + enabled + ": " + e);
        }
    }

    public boolean queryMmTelCapability(final int capability, final int radioTech) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        final BlockingQueue<Boolean> result = new LinkedBlockingDeque<>(1);
        try {
            c.queryEnabledCapabilities(capability, radioTech, new IImsCapabilityCallback.Stub() { // from class: com.android.ims.ImsManager.2
                public void onQueryCapabilityConfiguration(int resCap, int resTech, boolean enabled) {
                    if (resCap == capability && resTech == radioTech) {
                        result.offer(Boolean.valueOf(enabled));
                    }
                }

                public void onChangeCapabilityConfigurationError(int capability2, int radioTech2, int reason) {
                }

                public void onCapabilitiesStatusChanged(int config) {
                }
            });
            try {
                return result.poll(3000L, TimeUnit.MILLISECONDS).booleanValue();
            } catch (InterruptedException e) {
                logw("queryMmTelCapability: interrupted while waiting for response");
                return false;
            }
        } catch (RemoteException e2) {
            throw new ImsException("queryMmTelCapability()", e2, 106);
        }
    }

    public boolean queryMmTelCapabilityStatus(int capability, int radioTech) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        if (getRegistrationTech() != radioTech) {
            return false;
        }
        try {
            MmTelFeature.MmTelCapabilities capabilities = c.queryCapabilityStatus();
            return capabilities.isCapable(capability);
        } catch (RemoteException e) {
            throw new ImsException("queryMmTelCapabilityStatus()", e, 106);
        }
    }

    public void setRttEnabled(boolean enabled) {
        if (enabled) {
            setEnhanced4gLteModeSetting(DBG);
        }
        setRttConfig(enabled);
    }

    public void setTtyMode(int ttyMode) throws ImsException {
        boolean isNonTtyOrTtyOnWifiEnabled = false;
        boolean isNonTtyOrTtyOnVolteEnabled = isTtyOnVoLteCapable() || ttyMode == 0;
        if (isTtyOnVoWifiCapable() || ttyMode == 0) {
            isNonTtyOrTtyOnWifiEnabled = true;
        }
        CapabilityChangeRequest request = new CapabilityChangeRequest();
        updateVoiceCellFeatureValue(request, isNonTtyOrTtyOnVolteEnabled);
        updateVideoCallFeatureValue(request, isNonTtyOrTtyOnVolteEnabled);
        updateVoiceWifiFeatureAndProvisionedValues(request, isNonTtyOrTtyOnWifiEnabled);
        changeMmTelCapability(request);
        if (isImsNeeded(request)) {
            turnOnIms();
        }
    }

    public void setUiTTYMode(Context context, int uiTtyMode, Message onComplete) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.setUiTTYMode(uiTtyMode, onComplete);
        } catch (RemoteException e) {
            throw new ImsException("setTTYMode()", e, 106);
        }
    }

    public void setTerminalBasedCallWaitingStatus(boolean enabled) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.setTerminalBasedCallWaitingStatus(enabled);
        } catch (ServiceSpecificException se) {
            if (se.errorCode == 2) {
                throw new ImsException("setTerminalBasedCallWaitingStatus()", se, 150);
            }
            throw new ImsException("setTerminalBasedCallWaitingStatus()", se, 103);
        } catch (RemoteException e) {
            throw new ImsException("setTerminalBasedCallWaitingStatus()", e, 106);
        }
    }

    public boolean isCapable(long capabilities) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            return c.isCapable(capabilities);
        } catch (RemoteException e) {
            throw new ImsException("isCapable()", e, 106);
        }
    }

    public void notifySrvccStarted(ISrvccStartedCallback cb) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.notifySrvccStarted(cb);
        } catch (RemoteException e) {
            throw new ImsException("notifySrvccStarted", e, 106);
        }
    }

    public void notifySrvccCompleted() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.notifySrvccCompleted();
        } catch (RemoteException e) {
            throw new ImsException("notifySrvccCompleted", e, 106);
        }
    }

    public void notifySrvccFailed() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.notifySrvccFailed();
        } catch (RemoteException e) {
            throw new ImsException("notifySrvccFailed", e, 106);
        }
    }

    public void notifySrvccCanceled() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.notifySrvccCanceled();
        } catch (RemoteException e) {
            throw new ImsException("notifySrvccCanceled", e, 106);
        }
    }

    public void triggerDeregistration(int reason) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.triggerDeregistration(reason);
        } catch (RemoteException e) {
            throw new ImsException("triggerDeregistration", e, 106);
        }
    }

    public int getImsServiceState() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        return c.getFeatureState();
    }

    public void setMediaThreshold(int sessionType, MediaThreshold threshold) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            c.setMediaThreshold(sessionType, threshold);
        } catch (RemoteException e) {
            loge("setMediaThreshold Failed.");
        }
    }

    public MediaQualityStatus queryMediaQualityStatus(int sessionType) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        try {
            return c.queryMediaQualityStatus(sessionType);
        } catch (RemoteException e) {
            loge("queryMediaQualityStatus Failed.");
            return null;
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void updateFeatureState(int state) {
        this.mMmTelConnectionRef.get().updateFeatureState(state);
    }

    @Override // com.android.ims.FeatureUpdates
    public void updateFeatureCapabilities(long capabilities) {
        this.mMmTelConnectionRef.get().updateFeatureCapabilities(capabilities);
    }

    public void getImsServiceState(final Consumer<Integer> result) {
        getImsThreadExecutor().execute(new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ImsManager.this.lambda$getImsServiceState$7(result);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getImsServiceState$7(Consumer result) {
        try {
            result.accept(Integer.valueOf(getImsServiceState()));
        } catch (ImsException e) {
            result.accept(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Executor getImsThreadExecutor() {
        return this.mExecutor;
    }

    private boolean getBooleanCarrierConfig(String key) {
        PersistableBundle b = null;
        CarrierConfigManager carrierConfigManager = this.mConfigManager;
        if (carrierConfigManager != null) {
            b = carrierConfigManager.getConfigForSubId(getSubId());
        }
        if (b != null) {
            return b.getBoolean(key);
        }
        return CarrierConfigManager.getDefaultConfig().getBoolean(key);
    }

    private int getIntCarrierConfig(String key) {
        PersistableBundle b = null;
        CarrierConfigManager carrierConfigManager = this.mConfigManager;
        if (carrierConfigManager != null) {
            b = carrierConfigManager.getConfigForSubId(getSubId());
        }
        if (b != null) {
            return b.getInt(key);
        }
        return CarrierConfigManager.getDefaultConfig().getInt(key);
    }

    private int[] getIntArrayCarrierConfig(String key) {
        PersistableBundle b = null;
        CarrierConfigManager carrierConfigManager = this.mConfigManager;
        if (carrierConfigManager != null) {
            b = carrierConfigManager.getConfigForSubId(getSubId());
        }
        if (b != null) {
            return b.getIntArray(key);
        }
        return CarrierConfigManager.getDefaultConfig().getIntArray(key);
    }

    private MmTelFeatureConnection getOrThrowExceptionIfServiceUnavailable() throws ImsException {
        if (!isImsSupportedOnDevice(this.mContext)) {
            throw new ImsException("IMS not supported on device.", 150);
        }
        MmTelFeatureConnection c = this.mMmTelConnectionRef.get();
        if (c == null || !c.isBinderAlive()) {
            throw new ImsException("Service is unavailable", 106);
        }
        if (getSubId() != c.getSubId()) {
            logi("Trying to get MmTelFeature when it is still setting up, curr subId=" + getSubId() + ", target subId=" + c.getSubId());
            throw new ImsException("Service is still initializing", 106);
        }
        return c;
    }

    @Override // com.android.ims.FeatureUpdates
    public void registerFeatureCallback(int slotId, final IImsServiceFeatureCallback cb) {
        try {
            ITelephony telephony = this.mBinderCache.listenOnBinder(cb, new Runnable() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    cb.imsFeatureRemoved(3);
                }
            });
            if (telephony != null) {
                telephony.registerMmTelFeatureCallback(slotId, cb);
            } else {
                cb.imsFeatureRemoved(3);
            }
        } catch (RemoteException e) {
            try {
                cb.imsFeatureRemoved(3);
            } catch (RemoteException e2) {
            }
        } catch (ServiceSpecificException e3) {
            try {
                switch (e3.errorCode) {
                    case 2:
                        cb.imsFeatureRemoved(2);
                        break;
                    default:
                        cb.imsFeatureRemoved(3);
                        break;
                }
            } catch (RemoteException e4) {
            }
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void unregisterFeatureCallback(IImsServiceFeatureCallback cb) {
        try {
            ITelephony telephony = this.mBinderCache.removeRunnable(cb);
            if (telephony != null) {
                telephony.unregisterImsFeatureCallback(cb);
            }
        } catch (RemoteException e) {
            loge("unregisterImsFeatureCallback (MMTEL), RemoteException: " + e.getMessage());
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void associate(ImsFeatureContainer c, int subId) {
        if (c == null) {
            this.mMmTelConnectionRef.set(this.mMmTelFeatureConnectionFactory.create(this.mContext, this.mPhoneId, subId, null, null, null, null));
        } else {
            this.mMmTelConnectionRef.set(this.mMmTelFeatureConnectionFactory.create(this.mContext, this.mPhoneId, subId, IImsMmTelFeature.Stub.asInterface(c.imsFeature), c.imsConfig, c.imsRegistration, c.sipTransport));
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void invalidate() {
        this.mMmTelConnectionRef.get().onRemovedOrDied();
    }

    private ITelephony getITelephony() {
        return this.mBinderCache.getBinder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ITelephony getITelephonyInterface() {
        return ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
    }

    private ImsCallSession createCallSession(ImsCallProfile profile) throws ImsException {
        try {
            MmTelFeatureConnection c = this.mMmTelConnectionRef.get();
            return new ImsCallSession(c.createCallSession(profile));
        } catch (RemoteException e) {
            logw("CreateCallSession: Error, remote exception: " + e.getMessage());
            throw new ImsException("createCallSession()", e, 106);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String s) {
        Rlog.d(TAG + this.mLogTagPostfix + " [" + this.mPhoneId + "]", s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String s) {
        Rlog.i(TAG + this.mLogTagPostfix + " [" + this.mPhoneId + "]", s);
    }

    private void logw(String s) {
        Rlog.w(TAG + this.mLogTagPostfix + " [" + this.mPhoneId + "]", s);
    }

    private void loge(String s) {
        Rlog.e(TAG + this.mLogTagPostfix + " [" + this.mPhoneId + "]", s);
    }

    private void loge(String s, Throwable t) {
        Rlog.e(TAG + this.mLogTagPostfix + " [" + this.mPhoneId + "]", s, t);
    }

    private void turnOnIms() throws ImsException {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService("phone");
        tm.enableIms(this.mPhoneId);
    }

    private boolean isImsTurnOffAllowed() {
        if (!isTurnOffImsAllowedByPlatform() || (isWfcEnabledByPlatform() && isWfcEnabledByUser())) {
            return false;
        }
        return DBG;
    }

    private void turnOffIms() throws ImsException {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService("phone");
        tm.disableIms(this.mPhoneId);
    }

    public ImsEcbm getEcbmInterface() throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        ImsEcbm iEcbm = c.getEcbmInterface();
        if (iEcbm == null) {
            throw new ImsException("getEcbmInterface()", 901);
        }
        return iEcbm;
    }

    public void sendSms(int token, int messageRef, String format, String smsc, boolean isRetry, byte[] pdu) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().sendSms(token, messageRef, format, smsc, isRetry, pdu);
        } catch (RemoteException e) {
            throw new ImsException("sendSms()", e, 106);
        }
    }

    public void onMemoryAvailable(int token) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().onMemoryAvailable(token);
        } catch (RemoteException e) {
            throw new ImsException("onMemoryAvailable()", e, 106);
        }
    }

    public void acknowledgeSms(int token, int messageRef, int result) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().acknowledgeSms(token, messageRef, result);
        } catch (RemoteException e) {
            throw new ImsException("acknowledgeSms()", e, 106);
        }
    }

    public void acknowledgeSms(int token, int messageRef, int result, byte[] pdu) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().acknowledgeSms(token, messageRef, result, pdu);
        } catch (RemoteException e) {
            throw new ImsException("acknowledgeSms()", e, 106);
        }
    }

    public void acknowledgeSmsReport(int token, int messageRef, int result) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().acknowledgeSmsReport(token, messageRef, result);
        } catch (RemoteException e) {
            throw new ImsException("acknowledgeSmsReport()", e, 106);
        }
    }

    public String getSmsFormat() throws ImsException {
        try {
            return this.mMmTelConnectionRef.get().getSmsFormat();
        } catch (RemoteException e) {
            throw new ImsException("getSmsFormat()", e, 106);
        }
    }

    public void setSmsListener(IImsSmsListener listener) throws ImsException {
        try {
            this.mMmTelConnectionRef.get().setSmsListener(listener);
        } catch (RemoteException e) {
            throw new ImsException("setSmsListener()", e, 106);
        }
    }

    public void onSmsReady() throws ImsException {
        try {
            this.mMmTelConnectionRef.get().onSmsReady();
        } catch (RemoteException e) {
            throw new ImsException("onSmsReady()", e, 106);
        }
    }

    public int shouldProcessCall(boolean isEmergency, String[] numbers) throws ImsException {
        try {
            MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
            return c.shouldProcessCall(isEmergency, numbers);
        } catch (RemoteException e) {
            throw new ImsException("shouldProcessCall()", e, 106);
        }
    }

    public static void factoryReset(Context context) {
        DefaultSubscriptionManagerProxy p = new DefaultSubscriptionManagerProxy(context);
        ImsManager mgr = getInstance(context, p.getDefaultVoicePhoneId());
        if (mgr != null) {
            mgr.factoryReset();
        }
        Rlog.e(TAG, "factoryReset: ImsManager null.");
    }

    public void factoryReset() {
        int subId = getSubId();
        if (!isSubIdValid(subId)) {
            loge("factoryReset: invalid sub id, can not reset siminfo db settings; subId=" + subId);
            return;
        }
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "volte_vt_enabled", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_enabled", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_mode", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_roaming_enabled", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "wfc_ims_roaming_mode", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "vt_ims_enabled", Integer.toString(-1));
        this.mSubscriptionManagerProxy.setSubscriptionProperty(subId, "ims_rcs_uce_enabled", Integer.toString(0));
        try {
            reevaluateCapabilities();
        } catch (ImsException e) {
            loge("factoryReset, exception: " + e);
        }
    }

    private boolean isDataEnabled() {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (tm == null) {
            loge("isDataEnabled: TelephonyManager not available, returning false...");
            return false;
        }
        return tm.createForSubscriptionId(getSubId()).isDataConnectionAllowed();
    }

    private boolean isVolteProvisioned() {
        return getImsProvisionedBoolNoException(1, 0);
    }

    private boolean isEabProvisioned() {
        return getRcsProvisionedBoolNoException(2, 0);
    }

    private boolean isWfcProvisioned() {
        return getImsProvisionedBoolNoException(1, 1);
    }

    private boolean isVtProvisioned() {
        return getImsProvisionedBoolNoException(2, 0);
    }

    private boolean isMmTelProvisioningRequired(int capability, int tech) {
        int subId = getSubId();
        if (subId == -1) {
            logw("isMmTelProvisioningRequired subId is invalid");
            return false;
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            logw("isMmTelProvisioningRequired ITelephony interface is invalid");
            return false;
        }
        boolean required = false;
        try {
            required = iTelephony.isProvisioningRequiredForCapability(subId, capability, tech);
        } catch (RemoteException | IllegalArgumentException e) {
            logw("isMmTelProvisioningRequired : operation failed capability=" + capability + " tech=" + tech + ". Exception:" + e.getMessage());
        }
        log("MmTel Provisioning required " + required + " for capability " + capability);
        return required;
    }

    private boolean isRcsProvisioningRequired(int capability, int tech) {
        int subId = getSubId();
        if (subId == -1) {
            logw("isRcsProvisioningRequired subId is invalid");
            return false;
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            logw("isRcsProvisioningRequired ITelephony interface is invalid");
            return false;
        }
        boolean required = false;
        try {
            required = iTelephony.isRcsProvisioningRequiredForCapability(subId, capability, tech);
        } catch (RemoteException | IllegalArgumentException e) {
            logw("isRcsProvisioningRequired : operation failed capability=" + capability + " tech=" + tech + ". Exception:" + e.getMessage());
        }
        log("Rcs Provisioning required " + required + " for capability " + capability);
        return required;
    }

    private static String booleanToPropertyString(boolean bool) {
        return bool ? "1" : "0";
    }

    public int getConfigInt(int key) throws ImsException {
        if (isLocalImsConfigKey(key)) {
            return getLocalImsConfigKeyInt(key);
        }
        return getConfigInterface().getConfigInt(key);
    }

    public String getConfigString(int key) throws ImsException {
        if (isLocalImsConfigKey(key)) {
            return getLocalImsConfigKeyString(key);
        }
        return getConfigInterface().getConfigString(key);
    }

    public int setConfig(int key, int value) throws ImsException, RemoteException {
        if (isLocalImsConfigKey(key)) {
            return setLocalImsConfigKeyInt(key, value);
        }
        return getConfigInterface().setConfig(key, value);
    }

    public int setConfig(int key, String value) throws ImsException, RemoteException {
        if (isLocalImsConfigKey(key)) {
            return setLocalImsConfigKeyString(key, value);
        }
        return getConfigInterface().setConfig(key, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2 */
    private int getLocalImsConfigKeyInt(int key) {
        int result = -1;
        switch (key) {
            case 68:
                result = isVoImsOptInEnabled();
                break;
        }
        log("getLocalImsConfigKeInt() for key:" + key + ", result: " + result);
        return result;
    }

    private String getLocalImsConfigKeyString(int key) {
        String result = "";
        switch (key) {
            case 68:
                result = booleanToPropertyString(isVoImsOptInEnabled());
                break;
        }
        log("getLocalImsConfigKeyString() for key:" + key + ", result: " + result);
        return result;
    }

    private int setLocalImsConfigKeyInt(int key, int value) throws ImsException, RemoteException {
        int result = -1;
        switch (key) {
            case 68:
                result = setVoImsOptInSetting(value);
                reevaluateCapabilities();
                break;
        }
        log("setLocalImsConfigKeyInt() for key: " + key + ", value: " + value + ", result: " + result);
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        IImsConfig config = c.getConfig();
        config.notifyIntImsConfigChanged(key, value);
        return result;
    }

    private int setLocalImsConfigKeyString(int key, String value) throws ImsException, RemoteException {
        int result = -1;
        switch (key) {
            case 68:
                result = setVoImsOptInSetting(Integer.parseInt(value));
                reevaluateCapabilities();
                break;
        }
        log("setLocalImsConfigKeyString() for key: " + key + ", value: " + value + ", result: " + result);
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        IImsConfig config = c.getConfig();
        config.notifyStringImsConfigChanged(key, value);
        return result;
    }

    private boolean isLocalImsConfigKey(final int key) {
        return Arrays.stream(LOCAL_IMS_CONFIG_KEYS).anyMatch(new IntPredicate() { // from class: com.android.ims.ImsManager$$ExternalSyntheticLambda6
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return ImsManager.lambda$isLocalImsConfigKey$9(key, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isLocalImsConfigKey$9(int key, int value) {
        if (value == key) {
            return DBG;
        }
        return false;
    }

    private boolean isVoImsOptInEnabled() {
        int setting = this.mSubscriptionManagerProxy.getIntegerSubscriptionProperty(getSubId(), "voims_opt_in_status", -1);
        if (setting == 1) {
            return DBG;
        }
        return false;
    }

    private int setVoImsOptInSetting(int value) {
        this.mSubscriptionManagerProxy.setSubscriptionProperty(getSubId(), "voims_opt_in_status", String.valueOf(value));
        return 0;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("ImsManager:");
        pw.println("  device supports IMS = " + isImsSupportedOnDevice(this.mContext));
        pw.println("  mPhoneId = " + this.mPhoneId);
        pw.println("  mConfigUpdated = " + this.mConfigUpdated);
        pw.println("  mImsServiceProxy = " + this.mMmTelConnectionRef.get());
        pw.println("  mDataEnabled = " + isDataEnabled());
        pw.println("  ignoreDataEnabledChanged = " + getBooleanCarrierConfig("ignore_data_enabled_changed_for_video_calls"));
        pw.println("  isGbaValid = " + isGbaValid());
        pw.println("  isImsTurnOffAllowed = " + isImsTurnOffAllowed());
        pw.println("  isNonTtyOrTtyOnVolteEnabled = " + isNonTtyOrTtyOnVolteEnabled());
        pw.println("  isVolteEnabledByPlatform = " + isVolteEnabledByPlatform());
        pw.println("  isVoImsOptInEnabled = " + isVoImsOptInEnabled());
        pw.println("  isVolteProvisionedOnDevice = " + isVolteProvisionedOnDevice());
        pw.println("  isEnhanced4gLteModeSettingEnabledByUser = " + isEnhanced4gLteModeSettingEnabledByUser());
        pw.println("  isVtEnabledByPlatform = " + isVtEnabledByPlatform());
        pw.println("  isVtEnabledByUser = " + isVtEnabledByUser());
        pw.println("  isWfcEnabledByPlatform = " + isWfcEnabledByPlatform());
        pw.println("  isWfcEnabledByUser = " + isWfcEnabledByUser());
        pw.println("  getWfcMode(non-roaming) = " + getWfcMode(false));
        pw.println("  getWfcMode(roaming) = " + getWfcMode(DBG));
        pw.println("  isWfcRoamingEnabledByUser = " + isWfcRoamingEnabledByUser());
        pw.println("  isVtProvisionedOnDevice = " + isVtProvisionedOnDevice());
        pw.println("  isWfcProvisionedOnDevice = " + isWfcProvisionedOnDevice());
        pw.println("  isCrossSimEnabledByPlatform = " + isCrossSimEnabledByPlatform());
        pw.println("  isCrossSimCallingEnabledByUser = " + isCrossSimCallingEnabledByUser());
        pw.println("  isImsOverNrEnabledByPlatform = " + isImsOverNrEnabledByPlatform());
        pw.flush();
    }

    private boolean isSubIdValid(int subId) {
        if (!this.mSubscriptionManagerProxy.isValidSubscriptionId(subId) || subId == Integer.MAX_VALUE) {
            return false;
        }
        return DBG;
    }

    private boolean isActiveSubscriptionPresent() {
        if (this.mSubscriptionManagerProxy.getActiveSubscriptionIdList().length > 0) {
            return DBG;
        }
        return false;
    }

    private void updateImsCarrierConfigs(PersistableBundle configs) throws ImsException {
        MmTelFeatureConnection c = getOrThrowExceptionIfServiceUnavailable();
        IImsConfig config = c.getConfig();
        if (config == null) {
            throw new ImsException("getConfigInterface()", 131);
        }
        try {
            config.updateImsCarrierConfigs(configs);
        } catch (RemoteException e) {
            throw new ImsException("updateImsCarrierConfigs()", e, 106);
        }
    }
}
