package com.android.internal.telephony.ims;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.feature.ImsFeature;
import android.telephony.ims.stub.ImsFeatureConfiguration;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.ims.ImsFeatureBinderRepository;
import com.android.ims.ImsFeatureContainer;
import com.android.ims.internal.IImsServiceFeatureCallback;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.GbaManager;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.SomeArgs;
import com.android.internal.telephony.ims.ImsResolver;
import com.android.internal.telephony.ims.ImsServiceController;
import com.android.internal.telephony.ims.ImsServiceFeatureQueryManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ImsResolver implements ImsServiceController.ImsServiceControllerCallbacks {
    @VisibleForTesting
    public static final String METADATA_EMERGENCY_MMTEL_FEATURE = "android.telephony.ims.EMERGENCY_MMTEL_FEATURE";
    @VisibleForTesting
    public static final String METADATA_MMTEL_FEATURE = "android.telephony.ims.MMTEL_FEATURE";
    @VisibleForTesting
    public static final String METADATA_RCS_FEATURE = "android.telephony.ims.RCS_FEATURE";
    private static ImsResolver sInstance;
    private final SparseArray<SparseArray<ImsServiceController>> mBoundImsServicesByFeature;
    private final CarrierConfigManager mCarrierConfigManager;
    private final SparseArray<Map<Integer, String>> mCarrierServices;
    private final Context mContext;
    private ImsServiceFeatureQueryManager mFeatureQueryManager;
    private final Handler mHandler;
    private int mNumSlots;
    private final SparseArray<SparseArray<String>> mOverrideServices;
    private final Context mReceiverContext;
    private final ImsFeatureBinderRepository mRepo;
    private final HandlerExecutor mRunnableExecutor;
    private final SparseIntArray mSlotIdToSubIdMap;
    private final BroadcastReceiver mAppChangedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.ims.ImsResolver.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -810471698:
                    if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
                        c = 0;
                        break;
                    }
                    break;
                case 172491798:
                    if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                        c = 1;
                        break;
                    }
                    break;
                case 525384130:
                    if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1544582882:
                    if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 3:
                    ImsResolver.this.mHandler.obtainMessage(0, schemeSpecificPart).sendToTarget();
                    return;
                case 2:
                    ImsResolver.this.mHandler.obtainMessage(1, schemeSpecificPart).sendToTarget();
                    return;
                default:
                    return;
            }
        }
    };
    private final BroadcastReceiver mConfigChangedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.ims.ImsResolver.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1);
            if (intExtra == -1) {
                Log.i("ImsResolver", "Received CCC for invalid slot id.");
                return;
            }
            int intExtra2 = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
            int simState = ImsResolver.this.mTelephonyManagerProxy.getSimState(ImsResolver.this.mContext, intExtra);
            if (intExtra2 == -1 && simState != 1 && simState != 6) {
                Log.i("ImsResolver", "Received CCC for slot " + intExtra + " and sim state " + simState + ", ignoring.");
                return;
            }
            Log.i("ImsResolver", "Received Carrier Config Changed for SlotId: " + intExtra + ", SubId: " + intExtra2 + ", sim state: " + simState);
            ImsResolver.this.mHandler.obtainMessage(2, intExtra, intExtra2).sendToTarget();
        }
    };
    private final BroadcastReceiver mBootCompleted = new BroadcastReceiver() { // from class: com.android.internal.telephony.ims.ImsResolver.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.i("ImsResolver", "Received BOOT_COMPLETED");
            ImsResolver.this.mHandler.obtainMessage(6, null).sendToTarget();
        }
    };
    private TelephonyManagerProxy mTelephonyManagerProxy = new TelephonyManagerProxy() { // from class: com.android.internal.telephony.ims.ImsResolver.4
        @Override // com.android.internal.telephony.ims.ImsResolver.TelephonyManagerProxy
        public int getSimState(Context context, int i) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (telephonyManager == null) {
                return 0;
            }
            return telephonyManager.getSimState(i);
        }
    };
    private SubscriptionManagerProxy mSubscriptionManagerProxy = new SubscriptionManagerProxy() { // from class: com.android.internal.telephony.ims.ImsResolver.5
        @Override // com.android.internal.telephony.ims.ImsResolver.SubscriptionManagerProxy
        public int getSubId(int i) {
            return SubscriptionManager.getSubscriptionId(i);
        }

        @Override // com.android.internal.telephony.ims.ImsResolver.SubscriptionManagerProxy
        public int getSlotIndex(int i) {
            return SubscriptionManager.getSlotIndex(i);
        }
    };
    private ImsServiceControllerFactory mImsServiceControllerFactory = new ImsServiceControllerFactory() { // from class: com.android.internal.telephony.ims.ImsResolver.6
        @Override // com.android.internal.telephony.ims.ImsResolver.ImsServiceControllerFactory
        public String getServiceInterface() {
            return "android.telephony.ims.ImsService";
        }

        @Override // com.android.internal.telephony.ims.ImsResolver.ImsServiceControllerFactory
        public ImsServiceController create(Context context, ComponentName componentName, ImsServiceController.ImsServiceControllerCallbacks imsServiceControllerCallbacks, ImsFeatureBinderRepository imsFeatureBinderRepository) {
            return new ImsServiceController(context, componentName, imsServiceControllerCallbacks, imsFeatureBinderRepository);
        }
    };
    private final ImsServiceControllerFactory mImsServiceControllerFactoryCompat = new ImsServiceControllerFactory() { // from class: com.android.internal.telephony.ims.ImsResolver.7
        @Override // com.android.internal.telephony.ims.ImsResolver.ImsServiceControllerFactory
        public String getServiceInterface() {
            return "android.telephony.ims.compat.ImsService";
        }

        @Override // com.android.internal.telephony.ims.ImsResolver.ImsServiceControllerFactory
        public ImsServiceController create(Context context, ComponentName componentName, ImsServiceController.ImsServiceControllerCallbacks imsServiceControllerCallbacks, ImsFeatureBinderRepository imsFeatureBinderRepository) {
            return new ImsServiceControllerCompat(context, componentName, imsServiceControllerCallbacks, imsFeatureBinderRepository);
        }
    };
    private ImsDynamicQueryManagerFactory mDynamicQueryManagerFactory = new ImsDynamicQueryManagerFactory() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda5
        @Override // com.android.internal.telephony.ims.ImsResolver.ImsDynamicQueryManagerFactory
        public final ImsServiceFeatureQueryManager create(Context context, ImsServiceFeatureQueryManager.Listener listener) {
            return new ImsServiceFeatureQueryManager(context, listener);
        }
    };
    private final Object mBoundServicesLock = new Object();
    private final Map<Integer, String> mDeviceServices = new ArrayMap();
    private final LocalLog mEventLog = new LocalLog(32);
    private boolean mBootCompletedHandlerRan = false;
    private boolean mCarrierConfigReceived = false;
    private final ImsServiceFeatureQueryManager.Listener mDynamicQueryListener = new ImsServiceFeatureQueryManager.Listener() { // from class: com.android.internal.telephony.ims.ImsResolver.8
        @Override // com.android.internal.telephony.ims.ImsServiceFeatureQueryManager.Listener
        public void onComplete(ComponentName componentName, Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
            Log.d("ImsResolver", "onComplete called for name: " + componentName + ImsResolver.printFeatures(set));
            ImsResolver.this.handleFeaturesChanged(componentName, set);
        }

        @Override // com.android.internal.telephony.ims.ImsServiceFeatureQueryManager.Listener
        public void onError(ComponentName componentName) {
            Log.w("ImsResolver", "onError: " + componentName + "returned with an error result");
            LocalLog localLog = ImsResolver.this.mEventLog;
            localLog.log("onError - dynamic query error for " + componentName);
            ImsResolver.this.scheduleQueryForFeatures(componentName, (int) GbaManager.REQUEST_TIMEOUT_MS);
        }

        @Override // com.android.internal.telephony.ims.ImsServiceFeatureQueryManager.Listener
        public void onPermanentError(ComponentName componentName) {
            Log.w("ImsResolver", "onPermanentError: component=" + componentName);
            LocalLog localLog = ImsResolver.this.mEventLog;
            localLog.log("onPermanentError - error for " + componentName);
            ImsResolver.this.mHandler.obtainMessage(1, componentName.getPackageName()).sendToTarget();
        }
    };
    private final Map<ComponentName, ImsServiceInfo> mInstalledServicesCache = new HashMap();
    private final Map<ComponentName, ImsServiceController> mActiveControllers = new HashMap();

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface ImsDynamicQueryManagerFactory {
        ImsServiceFeatureQueryManager create(Context context, ImsServiceFeatureQueryManager.Listener listener);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface ImsServiceControllerFactory {
        ImsServiceController create(Context context, ComponentName componentName, ImsServiceController.ImsServiceControllerCallbacks imsServiceControllerCallbacks, ImsFeatureBinderRepository imsFeatureBinderRepository);

        String getServiceInterface();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface SubscriptionManagerProxy {
        int getSlotIndex(int i);

        int getSubId(int i);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface TelephonyManagerProxy {
        int getSimState(Context context, int i);
    }

    public static void make(Context context, String str, String str2, int i, ImsFeatureBinderRepository imsFeatureBinderRepository) {
        if (sInstance == null) {
            sInstance = new ImsResolver(context, str, str2, i, imsFeatureBinderRepository, Looper.getMainLooper());
        }
    }

    public static ImsResolver getInstance() {
        return sInstance;
    }

    /* loaded from: classes.dex */
    private static class OverrideConfig {
        public final Map<Integer, String> featureTypeToPackageMap;
        public final boolean isCarrierService;
        public final int slotId;

        OverrideConfig(int i, boolean z, Map<Integer, String> map) {
            this.slotId = i;
            this.isCarrierService = z;
            this.featureTypeToPackageMap = map;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ImsServiceInfo {
        public ImsServiceControllerFactory controllerFactory;
        public boolean featureFromMetadata = true;
        private final HashSet<ImsFeatureConfiguration.FeatureSlotPair> mSupportedFeatures = new HashSet<>();
        public ComponentName name;

        void addFeatureForAllSlots(int i, int i2) {
            for (int i3 = 0; i3 < i; i3++) {
                this.mSupportedFeatures.add(new ImsFeatureConfiguration.FeatureSlotPair(i3, i2));
            }
        }

        void replaceFeatures(Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
            this.mSupportedFeatures.clear();
            this.mSupportedFeatures.addAll(set);
        }

        @VisibleForTesting
        public Set<ImsFeatureConfiguration.FeatureSlotPair> getSupportedFeatures() {
            return this.mSupportedFeatures;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ImsServiceInfo imsServiceInfo = (ImsServiceInfo) obj;
            ComponentName componentName = this.name;
            if (componentName == null ? imsServiceInfo.name == null : componentName.equals(imsServiceInfo.name)) {
                if (this.mSupportedFeatures.equals(imsServiceInfo.mSupportedFeatures)) {
                    ImsServiceControllerFactory imsServiceControllerFactory = this.controllerFactory;
                    return imsServiceControllerFactory != null ? imsServiceControllerFactory.equals(imsServiceInfo.controllerFactory) : imsServiceInfo.controllerFactory == null;
                }
                return false;
            }
            return false;
        }

        public int hashCode() {
            ComponentName componentName = this.name;
            int hashCode = (componentName != null ? componentName.hashCode() : 0) * 31;
            ImsServiceControllerFactory imsServiceControllerFactory = this.controllerFactory;
            return hashCode + (imsServiceControllerFactory != null ? imsServiceControllerFactory.hashCode() : 0);
        }

        public String toString() {
            return "[ImsServiceInfo] name=" + this.name + ", featureFromMetadata=" + this.featureFromMetadata + "," + ImsResolver.printFeatures(this.mSupportedFeatures);
        }
    }

    /* loaded from: classes.dex */
    private class ResolverHandler extends Handler {
        ResolverHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    ImsResolver.this.maybeAddedImsService((String) message.obj);
                    return;
                case 1:
                    ImsResolver.this.maybeRemovedImsService((String) message.obj);
                    return;
                case 2:
                    int i = message.arg1;
                    int i2 = message.arg2;
                    if (i >= ImsResolver.this.mNumSlots) {
                        Log.w("ImsResolver", "HANDLER_CONFIG_CHANGED for invalid slotid=" + i);
                        return;
                    }
                    ImsResolver.this.mCarrierConfigReceived = true;
                    ImsResolver.this.carrierConfigChanged(i, i2);
                    return;
                case 3:
                    ImsResolver.this.startDynamicQuery((ImsServiceInfo) message.obj);
                    return;
                case 4:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    someArgs.recycle();
                    ImsResolver.this.dynamicQueryComplete((ComponentName) someArgs.arg1, (Set) someArgs.arg2);
                    return;
                case 5:
                    OverrideConfig overrideConfig = (OverrideConfig) message.obj;
                    if (overrideConfig.isCarrierService) {
                        ImsResolver.this.overrideCarrierService(overrideConfig.slotId, overrideConfig.featureTypeToPackageMap);
                        return;
                    } else {
                        ImsResolver.this.overrideDeviceService(overrideConfig.featureTypeToPackageMap);
                        return;
                    }
                case 6:
                    if (ImsResolver.this.mBootCompletedHandlerRan) {
                        return;
                    }
                    ImsResolver.this.mBootCompletedHandlerRan = true;
                    ImsResolver.this.mEventLog.log("handling BOOT_COMPLETE");
                    if (ImsResolver.this.mCarrierConfigReceived) {
                        ImsResolver.this.mEventLog.log("boot complete - reeval");
                        ImsResolver.this.maybeAddedImsService(null);
                        return;
                    }
                    ImsResolver.this.mEventLog.log("boot complete - update cache");
                    ImsResolver.this.updateInstalledServicesCache();
                    return;
                case 7:
                    ImsResolver.this.handleMsimConfigChange((Integer) ((AsyncResult) message.obj).result);
                    return;
                case 8:
                    ImsResolver.this.clearCarrierServiceOverrides(message.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    public ImsResolver(Context context, String str, String str2, int i, ImsFeatureBinderRepository imsFeatureBinderRepository, Looper looper) {
        Log.i("ImsResolver", "device MMTEL package: " + str + ", device RCS package:" + str2);
        this.mContext = context;
        this.mNumSlots = i;
        this.mRepo = imsFeatureBinderRepository;
        this.mReceiverContext = context.createContextAsUser(UserHandle.ALL, 0);
        ResolverHandler resolverHandler = new ResolverHandler(looper);
        this.mHandler = resolverHandler;
        this.mRunnableExecutor = new HandlerExecutor(resolverHandler);
        this.mCarrierServices = new SparseArray<>(this.mNumSlots);
        setDeviceConfiguration(str, 0);
        setDeviceConfiguration(str, 1);
        setDeviceConfiguration(str2, 2);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        this.mOverrideServices = new SparseArray<>(0);
        this.mBoundImsServicesByFeature = new SparseArray<>(this.mNumSlots);
        this.mSlotIdToSubIdMap = new SparseIntArray(this.mNumSlots);
        for (int i2 = 0; i2 < this.mNumSlots; i2++) {
            this.mSlotIdToSubIdMap.put(i2, -1);
        }
    }

    @VisibleForTesting
    public void setTelephonyManagerProxy(TelephonyManagerProxy telephonyManagerProxy) {
        this.mTelephonyManagerProxy = telephonyManagerProxy;
    }

    @VisibleForTesting
    public void setSubscriptionManagerProxy(SubscriptionManagerProxy subscriptionManagerProxy) {
        this.mSubscriptionManagerProxy = subscriptionManagerProxy;
    }

    @VisibleForTesting
    public void setImsServiceControllerFactory(ImsServiceControllerFactory imsServiceControllerFactory) {
        this.mImsServiceControllerFactory = imsServiceControllerFactory;
    }

    @VisibleForTesting
    public Handler getHandler() {
        return this.mHandler;
    }

    @VisibleForTesting
    public void setImsDynamicQueryManagerFactory(ImsDynamicQueryManagerFactory imsDynamicQueryManagerFactory) {
        this.mDynamicQueryManagerFactory = imsDynamicQueryManagerFactory;
    }

    public void initialize() {
        this.mEventLog.log("Initializing");
        Log.i("ImsResolver", "Initializing cache.");
        PhoneConfigurationManager.registerForMultiSimConfigChange(this.mHandler, 7, null);
        this.mFeatureQueryManager = this.mDynamicQueryManagerFactory.create(this.mContext, this.mDynamicQueryListener);
        updateInstalledServicesCache();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        this.mReceiverContext.registerReceiver(this.mAppChangedReceiver, intentFilter);
        this.mReceiverContext.registerReceiver(this.mConfigChangedReceiver, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"));
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        if (userManager.isUserUnlocked()) {
            this.mHandler.obtainMessage(6, null).sendToTarget();
        } else {
            this.mReceiverContext.registerReceiver(this.mBootCompleted, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
            if (userManager.isUserUnlocked()) {
                this.mHandler.obtainMessage(6, null).sendToTarget();
            }
        }
        bindCarrierServicesIfAvailable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInstalledServicesCache() {
        for (ImsServiceInfo imsServiceInfo : getImsServiceInfo(null)) {
            if (!this.mInstalledServicesCache.containsKey(imsServiceInfo.name)) {
                this.mInstalledServicesCache.put(imsServiceInfo.name, imsServiceInfo);
            }
        }
    }

    @VisibleForTesting
    public void destroy() {
        PhoneConfigurationManager.unregisterForMultiSimConfigChange(this.mHandler);
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void bindCarrierServicesIfAvailable() {
        boolean z = false;
        for (int i = 0; i < this.mNumSlots; i++) {
            int subId = this.mSubscriptionManagerProxy.getSubId(i);
            Map<Integer, String> imsPackageOverrideConfig = getImsPackageOverrideConfig(subId);
            for (int i2 = 0; i2 < 3; i2++) {
                String orDefault = imsPackageOverrideConfig.getOrDefault(Integer.valueOf(i2), PhoneConfigurationManager.SSSS);
                if (!TextUtils.isEmpty(orDefault)) {
                    this.mEventLog.log("bindCarrierServicesIfAvailable - carrier package found: " + orDefault + " on slot " + i);
                    this.mCarrierConfigReceived = true;
                    setSubId(i, subId);
                    setCarrierConfiguredPackageName(orDefault, i, i2);
                    ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(orDefault);
                    if (imsServiceInfoFromCache == null || !imsServiceInfoFromCache.featureFromMetadata) {
                        scheduleQueryForFeatures(imsServiceInfoFromCache);
                    } else {
                        z = true;
                    }
                }
            }
        }
        if (z) {
            calculateFeatureConfigurationChange();
        }
    }

    public void enableIms(final int i) {
        getImsServiceControllers(i).forEach(new Consumer() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsResolver.this.lambda$enableIms$0(i, (ImsServiceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enableIms$0(int i, ImsServiceController imsServiceController) {
        imsServiceController.enableIms(i, getSubId(i));
    }

    public void disableIms(final int i) {
        getImsServiceControllers(i).forEach(new Consumer() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsResolver.this.lambda$disableIms$1(i, (ImsServiceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disableIms$1(int i, ImsServiceController imsServiceController) {
        imsServiceController.disableIms(i, getSubId(i));
    }

    public void resetIms(final int i) {
        getImsServiceControllers(i).forEach(new Consumer() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsResolver.this.lambda$resetIms$2(i, (ImsServiceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetIms$2(int i, ImsServiceController imsServiceController) {
        imsServiceController.resetIms(i, getSubId(i));
    }

    public IImsRegistration getImsRegistration(int i, int i2) {
        ImsFeatureContainer imsFeatureContainer = (ImsFeatureContainer) this.mRepo.getIfExists(i, i2).orElse(null);
        if (imsFeatureContainer != null) {
            return imsFeatureContainer.imsRegistration;
        }
        return null;
    }

    public IImsConfig getImsConfig(int i, int i2) {
        ImsFeatureContainer imsFeatureContainer = (ImsFeatureContainer) this.mRepo.getIfExists(i, i2).orElse(null);
        if (imsFeatureContainer != null) {
            return imsFeatureContainer.imsConfig;
        }
        return null;
    }

    private Set<ImsServiceController> getImsServiceControllers(int i) {
        SparseArray<ImsServiceController> sparseArray;
        if (i < 0 || i >= this.mNumSlots) {
            return Collections.emptySet();
        }
        synchronized (this.mBoundServicesLock) {
            sparseArray = this.mBoundImsServicesByFeature.get(i);
        }
        if (sparseArray == null) {
            Log.w("ImsResolver", "getImsServiceControllers: couldn't find any active ImsServiceControllers");
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet(2);
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            ImsServiceController imsServiceController = sparseArray.get(sparseArray.keyAt(i2));
            if (imsServiceController != null) {
                arraySet.add(imsServiceController);
            }
        }
        return arraySet;
    }

    public void listenForFeature(int i, int i2, IImsServiceFeatureCallback iImsServiceFeatureCallback) {
        this.mRepo.registerForConnectionUpdates(i, i2, iImsServiceFeatureCallback, this.mRunnableExecutor);
    }

    public void unregisterImsFeatureCallback(IImsServiceFeatureCallback iImsServiceFeatureCallback) {
        this.mRepo.unregisterForConnectionUpdates(iImsServiceFeatureCallback);
    }

    public boolean clearCarrierImsServiceConfiguration(int i) {
        if (i < 0 || i >= this.mNumSlots) {
            Log.w("ImsResolver", "clearCarrierImsServiceConfiguration: invalid slotId!");
            return false;
        }
        Message.obtain(this.mHandler, 8, i, 0).sendToTarget();
        return true;
    }

    public boolean overrideImsServiceConfiguration(int i, boolean z, Map<Integer, String> map) {
        if (i < 0 || i >= this.mNumSlots) {
            Log.w("ImsResolver", "overrideImsServiceConfiguration: invalid slotId!");
            return false;
        }
        Message.obtain(this.mHandler, 5, new OverrideConfig(i, z, map)).sendToTarget();
        return true;
    }

    private String getDeviceConfiguration(int i) {
        String orDefault;
        synchronized (this.mDeviceServices) {
            orDefault = this.mDeviceServices.getOrDefault(Integer.valueOf(i), PhoneConfigurationManager.SSSS);
        }
        return orDefault;
    }

    private void setDeviceConfiguration(String str, int i) {
        synchronized (this.mDeviceServices) {
            this.mDeviceServices.put(Integer.valueOf(i), str);
        }
    }

    private void setCarrierConfiguredPackageName(String str, int i, int i2) {
        getCarrierConfiguredPackageNames(i).put(Integer.valueOf(i2), str);
    }

    private String getCarrierConfiguredPackageName(int i, int i2) {
        return getCarrierConfiguredPackageNames(i).getOrDefault(Integer.valueOf(i2), PhoneConfigurationManager.SSSS);
    }

    private Map<Integer, String> getCarrierConfiguredPackageNames(int i) {
        Map<Integer, String> map = this.mCarrierServices.get(i);
        if (map == null) {
            ArrayMap arrayMap = new ArrayMap();
            this.mCarrierServices.put(i, arrayMap);
            return arrayMap;
        }
        return map;
    }

    private void removeOverridePackageName(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            getOverridePackageName(i).remove(i2);
        }
    }

    private void setOverridePackageName(String str, int i, int i2) {
        getOverridePackageName(i).put(i2, str);
    }

    private String getOverridePackageName(int i, int i2) {
        return getOverridePackageName(i).get(i2);
    }

    private SparseArray<String> getOverridePackageName(int i) {
        SparseArray<String> sparseArray = this.mOverrideServices.get(i);
        if (sparseArray == null) {
            SparseArray<String> sparseArray2 = new SparseArray<>();
            this.mOverrideServices.put(i, sparseArray2);
            return sparseArray2;
        }
        return sparseArray;
    }

    private boolean doesCarrierConfigurationExist(int i, int i2) {
        String carrierConfiguredPackageName = getCarrierConfiguredPackageName(i, i2);
        if (TextUtils.isEmpty(carrierConfiguredPackageName)) {
            return false;
        }
        return doesCachedImsServiceExist(carrierConfiguredPackageName, i, i2);
    }

    private boolean doesCachedImsServiceExist(String str, final int i, final int i2) {
        ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(str);
        return imsServiceInfoFromCache != null && imsServiceInfoFromCache.getSupportedFeatures().stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$doesCachedImsServiceExist$3;
                lambda$doesCachedImsServiceExist$3 = ImsResolver.lambda$doesCachedImsServiceExist$3(i, i2, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$doesCachedImsServiceExist$3;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$doesCachedImsServiceExist$3(int i, int i2, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.slotId == i && featureSlotPair.featureType == i2;
    }

    public String getImsServiceConfiguration(final int i, final boolean z, final int i2) {
        if (i < 0 || i >= this.mNumSlots) {
            Log.w("ImsResolver", "getImsServiceConfiguration: invalid slotId!");
            return PhoneConfigurationManager.SSSS;
        }
        final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(1);
        this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ImsResolver.this.lambda$getImsServiceConfiguration$4(linkedBlockingQueue, z, i, i2);
            }
        });
        try {
            return (String) linkedBlockingQueue.poll(5000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.w("ImsResolver", "getImsServiceConfiguration: exception=" + e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getImsServiceConfiguration$4(LinkedBlockingQueue linkedBlockingQueue, boolean z, int i, int i2) {
        String deviceConfiguration;
        if (z) {
            deviceConfiguration = getCarrierConfiguredPackageName(i, i2);
        } else {
            deviceConfiguration = getDeviceConfiguration(i2);
        }
        linkedBlockingQueue.offer(deviceConfiguration);
    }

    public boolean isImsServiceConfiguredForFeature(int i, int i2) {
        if (TextUtils.isEmpty(getDeviceConfiguration(i2))) {
            return !TextUtils.isEmpty(getConfiguredImsServicePackageName(i, i2));
        }
        return true;
    }

    public String getConfiguredImsServicePackageName(final int i, final int i2) {
        if (i < 0 || i >= this.mNumSlots || i2 <= -1 || i2 >= 3) {
            Log.w("ImsResolver", "getResolvedImsServicePackageName received invalid parameters - slot: " + i + ", feature: " + i2);
            return null;
        }
        final CompletableFuture completableFuture = new CompletableFuture();
        long currentTimeMillis = System.currentTimeMillis();
        if (this.mHandler.getLooper().isCurrentThread()) {
            completableFuture.complete(getConfiguredImsServicePackageNameInternal(i, i2));
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ImsResolver.this.lambda$getConfiguredImsServicePackageName$5(completableFuture, i, i2);
                }
            });
        }
        try {
            String str = (String) completableFuture.get();
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            if (currentTimeMillis2 > 50) {
                LocalLog localLog = this.mEventLog;
                localLog.log("getResolvedImsServicePackageName - [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "], async query complete, took " + currentTimeMillis2 + " ms with package name: " + str);
                Log.w("ImsResolver", "getResolvedImsServicePackageName: [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "], async query complete, took " + currentTimeMillis2 + " ms with package name: " + str);
            }
            return str;
        } catch (Exception e) {
            LocalLog localLog2 = this.mEventLog;
            localLog2.log("getResolvedImsServicePackageName - [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "] -> Exception: " + e);
            Log.w("ImsResolver", "getResolvedImsServicePackageName: [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "] returned Exception: " + e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getConfiguredImsServicePackageName$5(CompletableFuture completableFuture, int i, int i2) {
        try {
            completableFuture.complete(getConfiguredImsServicePackageNameInternal(i, i2));
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }
    }

    private String getConfiguredImsServicePackageNameInternal(int i, int i2) {
        String carrierConfiguredPackageName = getCarrierConfiguredPackageName(i, i2);
        if (TextUtils.isEmpty(carrierConfiguredPackageName) || !doesCachedImsServiceExist(carrierConfiguredPackageName, i, i2)) {
            String deviceConfiguration = getDeviceConfiguration(i2);
            if (TextUtils.isEmpty(deviceConfiguration) || !doesCachedImsServiceExist(deviceConfiguration, i, i2)) {
                return null;
            }
            return deviceConfiguration;
        }
        return carrierConfiguredPackageName;
    }

    private void putImsController(int i, int i2, ImsServiceController imsServiceController) {
        if (i < 0 || i >= this.mNumSlots || i2 <= -1 || i2 >= 3) {
            Log.w("ImsResolver", "putImsController received invalid parameters - slot: " + i + ", feature: " + i2);
            return;
        }
        synchronized (this.mBoundServicesLock) {
            SparseArray<ImsServiceController> sparseArray = this.mBoundImsServicesByFeature.get(i);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mBoundImsServicesByFeature.put(i, sparseArray);
            }
            LocalLog localLog = this.mEventLog;
            localLog.log("putImsController - [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "] -> " + imsServiceController);
            Log.i("ImsResolver", "ImsServiceController added on slot: " + i + " with feature: " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + " using package: " + imsServiceController.getComponentName());
            sparseArray.put(i2, imsServiceController);
        }
    }

    private ImsServiceController removeImsController(int i, int i2) {
        if (i < 0 || i2 <= -1 || i2 >= 3) {
            Log.w("ImsResolver", "removeImsController received invalid parameters - slot: " + i + ", feature: " + i2);
            return null;
        }
        synchronized (this.mBoundServicesLock) {
            SparseArray<ImsServiceController> sparseArray = this.mBoundImsServicesByFeature.get(i);
            if (sparseArray == null) {
                return null;
            }
            ImsServiceController imsServiceController = sparseArray.get(i2, null);
            if (imsServiceController != null) {
                LocalLog localLog = this.mEventLog;
                localLog.log("removeImsController - [" + i + ", " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + "] -> " + imsServiceController);
                Log.i("ImsResolver", "ImsServiceController removed on slot: " + i + " with feature: " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i2))) + " using package: " + imsServiceController.getComponentName());
                sparseArray.remove(i2);
            }
            return imsServiceController;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAddedImsService(String str) {
        Log.d("ImsResolver", "maybeAddedImsService, packageName: " + str);
        boolean z = false;
        for (ImsServiceInfo imsServiceInfo : getImsServiceInfo(str)) {
            ImsServiceInfo infoByComponentName = getInfoByComponentName(this.mInstalledServicesCache, imsServiceInfo.name);
            if (infoByComponentName != null) {
                if (imsServiceInfo.featureFromMetadata) {
                    this.mEventLog.log("maybeAddedImsService - updating features for " + imsServiceInfo.name + ": " + printFeatures(infoByComponentName.getSupportedFeatures()) + " -> " + printFeatures(imsServiceInfo.getSupportedFeatures()));
                    StringBuilder sb = new StringBuilder();
                    sb.append("Updating features in cached ImsService: ");
                    sb.append(imsServiceInfo.name);
                    Log.i("ImsResolver", sb.toString());
                    Log.d("ImsResolver", "Updating features - Old features: " + infoByComponentName + " new features: " + imsServiceInfo);
                    infoByComponentName.replaceFeatures(imsServiceInfo.getSupportedFeatures());
                    z = true;
                } else {
                    this.mEventLog.log("maybeAddedImsService - scheduling query for " + imsServiceInfo);
                    scheduleQueryForFeatures(imsServiceInfo);
                }
            } else {
                Log.i("ImsResolver", "Adding newly added ImsService to cache: " + imsServiceInfo.name);
                this.mEventLog.log("maybeAddedImsService - adding new ImsService: " + imsServiceInfo);
                this.mInstalledServicesCache.put(imsServiceInfo.name, imsServiceInfo);
                if (imsServiceInfo.featureFromMetadata) {
                    z = true;
                } else {
                    scheduleQueryForFeatures(imsServiceInfo);
                }
            }
        }
        if (z) {
            calculateFeatureConfigurationChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean maybeRemovedImsService(String str) {
        ImsServiceInfo infoByPackageName = getInfoByPackageName(this.mInstalledServicesCache, str);
        if (infoByPackageName != null) {
            this.mInstalledServicesCache.remove(infoByPackageName.name);
            LocalLog localLog = this.mEventLog;
            localLog.log("maybeRemovedImsService - removing ImsService: " + infoByPackageName);
            Log.i("ImsResolver", "Removing ImsService: " + infoByPackageName.name);
            unbindImsService(infoByPackageName);
            calculateFeatureConfigurationChange();
            return true;
        }
        return false;
    }

    private boolean isDeviceService(ImsServiceInfo imsServiceInfo) {
        boolean containsValue;
        if (imsServiceInfo == null) {
            return false;
        }
        synchronized (this.mDeviceServices) {
            containsValue = this.mDeviceServices.containsValue(imsServiceInfo.name.getPackageName());
        }
        return containsValue;
    }

    private List<Integer> getSlotsForActiveCarrierService(final ImsServiceInfo imsServiceInfo) {
        if (imsServiceInfo == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(this.mNumSlots);
        for (int i = 0; i < this.mNumSlots; i++) {
            if (!TextUtils.isEmpty(getCarrierConfiguredPackageNames(i).values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda13
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getSlotsForActiveCarrierService$6;
                    lambda$getSlotsForActiveCarrierService$6 = ImsResolver.lambda$getSlotsForActiveCarrierService$6(ImsResolver.ImsServiceInfo.this, (String) obj);
                    return lambda$getSlotsForActiveCarrierService$6;
                }
            }).findAny().orElse(PhoneConfigurationManager.SSSS))) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSlotsForActiveCarrierService$6(ImsServiceInfo imsServiceInfo, String str) {
        return str.equals(imsServiceInfo.name.getPackageName());
    }

    private ImsServiceController getControllerByServiceInfo(Map<ComponentName, ImsServiceController> map, final ImsServiceInfo imsServiceInfo) {
        return map.values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getControllerByServiceInfo$7;
                lambda$getControllerByServiceInfo$7 = ImsResolver.lambda$getControllerByServiceInfo$7(ImsResolver.ImsServiceInfo.this, (ImsServiceController) obj);
                return lambda$getControllerByServiceInfo$7;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getControllerByServiceInfo$7(ImsServiceInfo imsServiceInfo, ImsServiceController imsServiceController) {
        return Objects.equals(imsServiceController.getComponentName(), imsServiceInfo.name);
    }

    private ImsServiceInfo getInfoByPackageName(Map<ComponentName, ImsServiceInfo> map, final String str) {
        return map.values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getInfoByPackageName$8;
                lambda$getInfoByPackageName$8 = ImsResolver.lambda$getInfoByPackageName$8(str, (ImsResolver.ImsServiceInfo) obj);
                return lambda$getInfoByPackageName$8;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getInfoByPackageName$8(String str, ImsServiceInfo imsServiceInfo) {
        return Objects.equals(imsServiceInfo.name.getPackageName(), str);
    }

    private ImsServiceInfo getInfoByComponentName(Map<ComponentName, ImsServiceInfo> map, ComponentName componentName) {
        return map.get(componentName);
    }

    private void bindImsServiceWithFeatures(ImsServiceInfo imsServiceInfo, Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        if (shouldFeaturesCauseBind(set)) {
            ImsServiceController controllerByServiceInfo = getControllerByServiceInfo(this.mActiveControllers, imsServiceInfo);
            SparseIntArray clone = this.mSlotIdToSubIdMap.clone();
            if (controllerByServiceInfo != null) {
                Log.i("ImsResolver", "ImsService connection exists for " + imsServiceInfo.name + ", updating features " + set);
                try {
                    controllerByServiceInfo.changeImsServiceFeatures(set, clone);
                } catch (RemoteException e) {
                    Log.w("ImsResolver", "bindImsService: error=" + e.getMessage());
                }
            } else {
                controllerByServiceInfo = imsServiceInfo.controllerFactory.create(this.mContext, imsServiceInfo.name, this, this.mRepo);
                Log.i("ImsResolver", "Binding ImsService: " + controllerByServiceInfo.getComponentName() + " with features: " + set);
                controllerByServiceInfo.bind(set, clone);
                LocalLog localLog = this.mEventLog;
                localLog.log("bindImsServiceWithFeatures - create new controller: " + controllerByServiceInfo);
            }
            this.mActiveControllers.put(imsServiceInfo.name, controllerByServiceInfo);
        }
    }

    private void unbindImsService(ImsServiceInfo imsServiceInfo) {
        ImsServiceController controllerByServiceInfo;
        if (imsServiceInfo == null || (controllerByServiceInfo = getControllerByServiceInfo(this.mActiveControllers, imsServiceInfo)) == null) {
            return;
        }
        try {
            Log.i("ImsResolver", "Unbinding ImsService: " + controllerByServiceInfo.getComponentName());
            LocalLog localLog = this.mEventLog;
            localLog.log("unbindImsService - unbinding and removing " + controllerByServiceInfo);
            controllerByServiceInfo.unbind();
        } catch (RemoteException e) {
            Log.e("ImsResolver", "unbindImsService: Remote Exception: " + e.getMessage());
        }
        this.mActiveControllers.remove(imsServiceInfo.name);
    }

    private HashSet<ImsFeatureConfiguration.FeatureSlotPair> calculateFeaturesToCreate(final ImsServiceInfo imsServiceInfo) {
        HashSet<ImsFeatureConfiguration.FeatureSlotPair> hashSet = new HashSet<>();
        if (!getSlotsForActiveCarrierService(imsServiceInfo).isEmpty()) {
            hashSet.addAll((Collection) imsServiceInfo.getSupportedFeatures().stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$calculateFeaturesToCreate$9;
                    lambda$calculateFeaturesToCreate$9 = ImsResolver.this.lambda$calculateFeaturesToCreate$9(imsServiceInfo, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                    return lambda$calculateFeaturesToCreate$9;
                }
            }).collect(Collectors.toList()));
            return hashSet;
        }
        if (isDeviceService(imsServiceInfo)) {
            hashSet.addAll((Collection) imsServiceInfo.getSupportedFeatures().stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$calculateFeaturesToCreate$10;
                    lambda$calculateFeaturesToCreate$10 = ImsResolver.this.lambda$calculateFeaturesToCreate$10(imsServiceInfo, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                    return lambda$calculateFeaturesToCreate$10;
                }
            }).filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$calculateFeaturesToCreate$11;
                    lambda$calculateFeaturesToCreate$11 = ImsResolver.this.lambda$calculateFeaturesToCreate$11((ImsFeatureConfiguration.FeatureSlotPair) obj);
                    return lambda$calculateFeaturesToCreate$11;
                }
            }).collect(Collectors.toList()));
        }
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$calculateFeaturesToCreate$9(ImsServiceInfo imsServiceInfo, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return imsServiceInfo.name.getPackageName().equals(getCarrierConfiguredPackageName(featureSlotPair.slotId, featureSlotPair.featureType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$calculateFeaturesToCreate$10(ImsServiceInfo imsServiceInfo, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return imsServiceInfo.name.getPackageName().equals(getDeviceConfiguration(featureSlotPair.featureType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$calculateFeaturesToCreate$11(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return !doesCarrierConfigurationExist(featureSlotPair.slotId, featureSlotPair.featureType);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController.ImsServiceControllerCallbacks
    public void imsServiceFeatureCreated(int i, int i2, ImsServiceController imsServiceController) {
        putImsController(i, i2, imsServiceController);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController.ImsServiceControllerCallbacks
    public void imsServiceFeatureRemoved(int i, int i2, ImsServiceController imsServiceController) {
        removeImsController(i, i2);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController.ImsServiceControllerCallbacks
    public void imsServiceFeaturesChanged(ImsFeatureConfiguration imsFeatureConfiguration, ImsServiceController imsServiceController) {
        if (imsServiceController == null || imsFeatureConfiguration == null) {
            return;
        }
        Log.i("ImsResolver", "imsServiceFeaturesChanged: config=" + imsFeatureConfiguration.getServiceFeatures() + ", ComponentName=" + imsServiceController.getComponentName());
        LocalLog localLog = this.mEventLog;
        localLog.log("imsServiceFeaturesChanged - for " + imsServiceController + ", new config " + imsFeatureConfiguration.getServiceFeatures());
        handleFeaturesChanged(imsServiceController.getComponentName(), imsFeatureConfiguration.getServiceFeatures());
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController.ImsServiceControllerCallbacks
    public void imsServiceBindPermanentError(ComponentName componentName) {
        if (componentName == null) {
            return;
        }
        Log.w("ImsResolver", "imsServiceBindPermanentError: component=" + componentName);
        LocalLog localLog = this.mEventLog;
        localLog.log("imsServiceBindPermanentError - for " + componentName);
        this.mHandler.obtainMessage(1, componentName.getPackageName()).sendToTarget();
    }

    private boolean shouldFeaturesCauseBind(Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        return set.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$shouldFeaturesCauseBind$12;
                lambda$shouldFeaturesCauseBind$12 = ImsResolver.lambda$shouldFeaturesCauseBind$12((ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$shouldFeaturesCauseBind$12;
            }
        }).count() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$shouldFeaturesCauseBind$12(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.featureType != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void overrideCarrierService(int i, Map<Integer, String> map) {
        for (Integer num : map.keySet()) {
            String str = map.get(num);
            LocalLog localLog = this.mEventLog;
            localLog.log("overriding carrier ImsService to " + str + " on slot " + i + " for feature " + ((String) ImsFeature.FEATURE_LOG_MAP.getOrDefault(num, "invalid")));
            setOverridePackageName(str, i, num.intValue());
        }
        updateBoundServices(i, Collections.emptyMap());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCarrierServiceOverrides(int i) {
        Log.i("ImsResolver", "clearing carrier ImsService overrides");
        this.mEventLog.log("clearing carrier ImsService overrides");
        removeOverridePackageName(i);
        carrierConfigChanged(i, getSubId(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void overrideDeviceService(Map<Integer, String> map) {
        boolean z = false;
        for (Integer num : map.keySet()) {
            String str = map.get(num);
            LocalLog localLog = this.mEventLog;
            localLog.log("overriding device ImsService to " + str + " for feature " + ((String) ImsFeature.FEATURE_LOG_MAP.getOrDefault(num, "invalid")));
            String deviceConfiguration = getDeviceConfiguration(num.intValue());
            if (!TextUtils.equals(deviceConfiguration, str)) {
                Log.i("ImsResolver", "overrideDeviceService - device package changed (override): " + deviceConfiguration + " -> " + str);
                LocalLog localLog2 = this.mEventLog;
                localLog2.log("overrideDeviceService - device package changed (override): " + deviceConfiguration + " -> " + str);
                setDeviceConfiguration(str, num.intValue());
                ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(str);
                if (imsServiceInfoFromCache == null || imsServiceInfoFromCache.featureFromMetadata) {
                    z = true;
                } else {
                    scheduleQueryForFeatures(imsServiceInfoFromCache);
                }
            }
        }
        if (z) {
            calculateFeatureConfigurationChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void carrierConfigChanged(int i, int i2) {
        setSubId(i, i2);
        updateBoundDeviceServices();
        if (i <= -1) {
            for (int i3 = 0; i3 < this.mNumSlots; i3++) {
                updateBoundServices(i3, getImsPackageOverrideConfig(getSubId(i3)));
            }
        }
        updateBoundServices(i, getImsPackageOverrideConfig(i2));
    }

    private void updateBoundDeviceServices() {
        Log.d("ImsResolver", "updateBoundDeviceServices: called");
        ArrayMap arrayMap = new ArrayMap();
        for (int i = 0; i < 3; i++) {
            String deviceConfiguration = getDeviceConfiguration(i);
            ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(deviceConfiguration);
            if (imsServiceInfoFromCache != null && !imsServiceInfoFromCache.featureFromMetadata && !arrayMap.containsKey(deviceConfiguration)) {
                arrayMap.put(deviceConfiguration, imsServiceInfoFromCache);
                Log.d("ImsResolver", "updateBoundDeviceServices: Schedule query for package=" + deviceConfiguration);
                scheduleQueryForFeatures((ImsServiceInfo) arrayMap.get(deviceConfiguration));
            }
        }
    }

    private void updateBoundServices(int i, Map<Integer, String> map) {
        if (i <= -1 || i >= this.mNumSlots) {
            return;
        }
        char c = 0;
        int i2 = 0;
        boolean z = false;
        boolean z2 = false;
        while (i2 < 3) {
            String overridePackageName = getOverridePackageName(i, i2);
            String carrierConfiguredPackageName = getCarrierConfiguredPackageName(i, i2);
            String orDefault = map.getOrDefault(Integer.valueOf(i2), PhoneConfigurationManager.SSSS);
            if (TextUtils.isEmpty(overridePackageName)) {
                overridePackageName = orDefault;
            } else {
                Object[] objArr = new Object[4];
                if (TextUtils.isEmpty(orDefault)) {
                    orDefault = "(none)";
                }
                objArr[c] = orDefault;
                objArr[1] = overridePackageName;
                objArr[2] = ImsFeature.FEATURE_LOG_MAP.getOrDefault(Integer.valueOf(i2), "invalid");
                objArr[3] = Integer.valueOf(i);
                Log.i("ImsResolver", String.format("updateBoundServices: overriding %s with %s for feature %s on slot %d", objArr));
            }
            setCarrierConfiguredPackageName(overridePackageName, i, i2);
            ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(overridePackageName);
            Log.i("ImsResolver", "updateBoundServices - carrier package changed: " + carrierConfiguredPackageName + " -> " + overridePackageName + " on slot " + i + ", hasConfigChanged=" + z);
            this.mEventLog.log("updateBoundServices - carrier package changed: " + carrierConfiguredPackageName + " -> " + overridePackageName + " on slot " + i + ", hasConfigChanged=" + z);
            if (imsServiceInfoFromCache == null || imsServiceInfoFromCache.featureFromMetadata) {
                z = true;
            } else {
                scheduleQueryForFeatures(imsServiceInfoFromCache);
                z2 = true;
            }
            i2++;
            c = 0;
        }
        if (z) {
            calculateFeatureConfigurationChange();
        }
        if (z && z2) {
            this.mEventLog.log("[warning] updateBoundServices - both hasConfigChange and query scheduled on slot " + i);
        }
    }

    private Map<Integer, String> getImsPackageOverrideConfig(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null) {
            return Collections.emptyMap();
        }
        String string = configForSubId.getString("config_ims_package_override_string", null);
        String string2 = configForSubId.getString("config_ims_mmtel_package_override_string", string);
        String string3 = configForSubId.getString("config_ims_rcs_package_override_string", string);
        ArrayMap arrayMap = new ArrayMap();
        if (!TextUtils.isEmpty(string2)) {
            arrayMap.put(0, string2);
            arrayMap.put(1, string2);
        }
        if (!TextUtils.isEmpty(string3)) {
            arrayMap.put(2, string3);
        }
        return arrayMap;
    }

    private void scheduleQueryForFeatures(ImsServiceInfo imsServiceInfo, int i) {
        if (imsServiceInfo == null) {
            return;
        }
        Message obtain = Message.obtain(this.mHandler, 3, imsServiceInfo);
        if (this.mHandler.hasMessages(3, imsServiceInfo)) {
            Log.d("ImsResolver", "scheduleQueryForFeatures: dynamic query for " + imsServiceInfo.name + " already scheduled");
            return;
        }
        Log.d("ImsResolver", "scheduleQueryForFeatures: starting dynamic query for " + imsServiceInfo.name + " in " + i + "ms.");
        this.mHandler.sendMessageDelayed(obtain, (long) i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleQueryForFeatures(ComponentName componentName, int i) {
        ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(componentName.getPackageName());
        if (imsServiceInfoFromCache == null) {
            Log.w("ImsResolver", "scheduleQueryForFeatures: Couldn't find cached info for name: " + componentName);
            return;
        }
        scheduleQueryForFeatures(imsServiceInfoFromCache, i);
    }

    private void scheduleQueryForFeatures(ImsServiceInfo imsServiceInfo) {
        scheduleQueryForFeatures(imsServiceInfo, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFeaturesChanged(ComponentName componentName, Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = componentName;
        obtain.arg2 = set;
        this.mHandler.obtainMessage(4, obtain).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMsimConfigChange(final Integer num) {
        int i = this.mNumSlots;
        if (i == num.intValue()) {
            return;
        }
        this.mNumSlots = num.intValue();
        Log.i("ImsResolver", "handleMsimConfigChange: oldLen=" + i + ", newLen=" + num);
        this.mEventLog.log("MSIM config change: " + i + " -> " + num);
        if (num.intValue() < i) {
            for (int intValue = num.intValue(); intValue < i; intValue++) {
                for (Integer num2 : getCarrierConfiguredPackageNames(intValue).keySet()) {
                    setCarrierConfiguredPackageName(PhoneConfigurationManager.SSSS, intValue, num2.intValue());
                }
                SparseArray<String> overridePackageName = getOverridePackageName(intValue);
                for (int i2 = 0; i2 < overridePackageName.size(); i2++) {
                    setOverridePackageName(PhoneConfigurationManager.SSSS, intValue, overridePackageName.keyAt(i2));
                }
                removeSlotId(intValue);
            }
        }
        for (ImsServiceInfo imsServiceInfo : getImsServiceInfo(null)) {
            ImsServiceInfo imsServiceInfo2 = this.mInstalledServicesCache.get(imsServiceInfo.name);
            if (imsServiceInfo2 != null) {
                if (imsServiceInfo.featureFromMetadata) {
                    imsServiceInfo2.replaceFeatures(imsServiceInfo.getSupportedFeatures());
                } else {
                    imsServiceInfo2.getSupportedFeatures().removeIf(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda12
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$handleMsimConfigChange$13;
                            lambda$handleMsimConfigChange$13 = ImsResolver.lambda$handleMsimConfigChange$13(num, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                            return lambda$handleMsimConfigChange$13;
                        }
                    });
                }
            } else {
                this.mEventLog.log("handleMsimConfigChange: detected untracked service - " + imsServiceInfo);
                Log.w("ImsResolver", "handleMsimConfigChange: detected untracked package, queueing to add " + imsServiceInfo);
                this.mHandler.obtainMessage(0, imsServiceInfo.name.getPackageName()).sendToTarget();
            }
        }
        if (num.intValue() < i) {
            calculateFeatureConfigurationChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$handleMsimConfigChange$13(Integer num, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.slotId >= num.intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDynamicQuery(ImsServiceInfo imsServiceInfo) {
        if (!isDeviceService(imsServiceInfo) && getSlotsForActiveCarrierService(imsServiceInfo).isEmpty()) {
            Log.i("ImsResolver", "scheduleQueryForFeatures: skipping query for ImsService that is not set as carrier/device ImsService.");
            return;
        }
        LocalLog localLog = this.mEventLog;
        localLog.log("startDynamicQuery - starting query for " + imsServiceInfo);
        if (!this.mFeatureQueryManager.startQuery(imsServiceInfo.name, imsServiceInfo.controllerFactory.getServiceInterface())) {
            Log.w("ImsResolver", "startDynamicQuery: service could not connect. Retrying after delay.");
            this.mEventLog.log("startDynamicQuery - query failed. Retrying in 5000 mS");
            scheduleQueryForFeatures(imsServiceInfo, GbaManager.REQUEST_TIMEOUT_MS);
            return;
        }
        Log.d("ImsResolver", "startDynamicQuery: Service queried, waiting for response.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dynamicQueryComplete(ComponentName componentName, Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        ImsServiceInfo imsServiceInfoFromCache = getImsServiceInfoFromCache(componentName.getPackageName());
        if (imsServiceInfoFromCache == null) {
            Log.w("ImsResolver", "dynamicQueryComplete: Couldn't find cached info for name: " + componentName);
            return;
        }
        sanitizeFeatureConfig(set);
        LocalLog localLog = this.mEventLog;
        localLog.log("dynamicQueryComplete: for package " + componentName + ", features: " + printFeatures(imsServiceInfoFromCache.getSupportedFeatures()) + " -> " + printFeatures(set));
        imsServiceInfoFromCache.replaceFeatures(set);
        if (this.mFeatureQueryManager.isQueryInProgress()) {
            return;
        }
        if (this.mHandler.hasMessages(4)) {
            this.mEventLog.log("[warning] dynamicQueryComplete - HANDLER_DYNAMIC_FEATURE_CHANGE pending with calculateFeatureConfigurationChange()");
        }
        calculateFeatureConfigurationChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$sanitizeFeatureConfig$14(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.slotId >= this.mNumSlots;
    }

    private void sanitizeFeatureConfig(Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        set.removeIf(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda14
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$sanitizeFeatureConfig$14;
                lambda$sanitizeFeatureConfig$14 = ImsResolver.this.lambda$sanitizeFeatureConfig$14((ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$sanitizeFeatureConfig$14;
            }
        });
        for (ImsFeatureConfiguration.FeatureSlotPair featureSlotPair : (Set) set.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsResolver$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$sanitizeFeatureConfig$15;
                lambda$sanitizeFeatureConfig$15 = ImsResolver.lambda$sanitizeFeatureConfig$15((ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$sanitizeFeatureConfig$15;
            }
        }).collect(Collectors.toSet())) {
            if (!set.contains(new ImsFeatureConfiguration.FeatureSlotPair(featureSlotPair.slotId, 1))) {
                set.remove(featureSlotPair);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$sanitizeFeatureConfig$15(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.featureType == 0;
    }

    private void calculateFeatureConfigurationChange() {
        for (ImsServiceInfo imsServiceInfo : this.mInstalledServicesCache.values()) {
            HashSet<ImsFeatureConfiguration.FeatureSlotPair> calculateFeaturesToCreate = calculateFeaturesToCreate(imsServiceInfo);
            if (shouldFeaturesCauseBind(calculateFeaturesToCreate)) {
                bindImsServiceWithFeatures(imsServiceInfo, calculateFeaturesToCreate);
            } else {
                unbindImsService(imsServiceInfo);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String printFeatures(Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        StringBuilder sb = new StringBuilder();
        sb.append(" features: [");
        if (set != null) {
            for (ImsFeatureConfiguration.FeatureSlotPair featureSlotPair : set) {
                sb.append("{");
                sb.append(featureSlotPair.slotId);
                sb.append(",");
                sb.append((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(featureSlotPair.featureType)));
                sb.append("}");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @VisibleForTesting
    public ImsServiceInfo getImsServiceInfoFromCache(String str) {
        ImsServiceInfo infoByPackageName;
        if (TextUtils.isEmpty(str) || (infoByPackageName = getInfoByPackageName(this.mInstalledServicesCache, str)) == null) {
            return null;
        }
        return infoByPackageName;
    }

    private List<ImsServiceInfo> getImsServiceInfo(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(searchForImsServices(str, this.mImsServiceControllerFactory));
        arrayList.addAll(searchForImsServices(str, this.mImsServiceControllerFactoryCompat));
        return arrayList;
    }

    private List<ImsServiceInfo> searchForImsServices(String str, ImsServiceControllerFactory imsServiceControllerFactory) {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent(imsServiceControllerFactory.getServiceInterface());
        intent.setPackage(str);
        for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentServicesAsUser(intent, 128, UserHandle.of(UserHandle.myUserId()))) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                ImsServiceInfo imsServiceInfo = new ImsServiceInfo();
                imsServiceInfo.name = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                imsServiceInfo.controllerFactory = imsServiceControllerFactory;
                if (isDeviceService(imsServiceInfo) || this.mImsServiceControllerFactoryCompat == imsServiceControllerFactory) {
                    Bundle bundle = serviceInfo.metaData;
                    if (bundle != null) {
                        if (bundle.getBoolean(METADATA_MMTEL_FEATURE, false)) {
                            imsServiceInfo.addFeatureForAllSlots(this.mNumSlots, 1);
                            if (serviceInfo.metaData.getBoolean(METADATA_EMERGENCY_MMTEL_FEATURE, false)) {
                                imsServiceInfo.addFeatureForAllSlots(this.mNumSlots, 0);
                            }
                        }
                        if (serviceInfo.metaData.getBoolean(METADATA_RCS_FEATURE, false)) {
                            imsServiceInfo.addFeatureForAllSlots(this.mNumSlots, 2);
                        }
                    }
                    if (this.mImsServiceControllerFactoryCompat != imsServiceControllerFactory && imsServiceInfo.getSupportedFeatures().isEmpty()) {
                        imsServiceInfo.featureFromMetadata = false;
                    }
                } else {
                    imsServiceInfo.featureFromMetadata = false;
                }
                Log.i("ImsResolver", "service name: " + imsServiceInfo.name + ", manifest query: " + imsServiceInfo.featureFromMetadata);
                if (TextUtils.equals(serviceInfo.permission, "android.permission.BIND_IMS_SERVICE") || serviceInfo.metaData.getBoolean("override_bind_check", false)) {
                    arrayList.add(imsServiceInfo);
                } else {
                    Log.w("ImsResolver", "ImsService is not protected with BIND_IMS_SERVICE permission: " + imsServiceInfo.name);
                }
            }
        }
        return arrayList;
    }

    private void setSubId(int i, int i2) {
        synchronized (this.mSlotIdToSubIdMap) {
            this.mSlotIdToSubIdMap.put(i, i2);
        }
    }

    private int getSubId(int i) {
        int i2;
        synchronized (this.mSlotIdToSubIdMap) {
            i2 = this.mSlotIdToSubIdMap.get(i, -1);
        }
        return i2;
    }

    private void removeSlotId(int i) {
        synchronized (this.mSlotIdToSubIdMap) {
            this.mSlotIdToSubIdMap.delete(i);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("ImsResolver:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Configurations:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Device:");
        indentingPrintWriter.increaseIndent();
        synchronized (this.mDeviceServices) {
            for (Integer num : this.mDeviceServices.keySet()) {
                indentingPrintWriter.println(((String) ImsFeature.FEATURE_LOG_MAP.get(num)) + " -> " + this.mDeviceServices.get(num));
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Carrier: ");
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mNumSlots; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                indentingPrintWriter.print("slot=");
                indentingPrintWriter.print(i);
                indentingPrintWriter.print(", feature=");
                indentingPrintWriter.print((String) ImsFeature.FEATURE_LOG_MAP.getOrDefault(Integer.valueOf(i2), "?"));
                indentingPrintWriter.println(": ");
                indentingPrintWriter.increaseIndent();
                String carrierConfiguredPackageName = getCarrierConfiguredPackageName(i, i2);
                if (TextUtils.isEmpty(carrierConfiguredPackageName)) {
                    carrierConfiguredPackageName = "none";
                }
                indentingPrintWriter.println(carrierConfiguredPackageName);
                indentingPrintWriter.decreaseIndent();
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Cached ImsServices:");
        indentingPrintWriter.increaseIndent();
        for (Object obj : this.mInstalledServicesCache.values()) {
            indentingPrintWriter.println(obj);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Active controllers:");
        indentingPrintWriter.increaseIndent();
        for (ImsServiceController imsServiceController : this.mActiveControllers.values()) {
            indentingPrintWriter.println(imsServiceController);
            indentingPrintWriter.increaseIndent();
            imsServiceController.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Connection Repository Log:");
        indentingPrintWriter.increaseIndent();
        this.mRepo.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Event Log:");
        indentingPrintWriter.increaseIndent();
        this.mEventLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }
}
