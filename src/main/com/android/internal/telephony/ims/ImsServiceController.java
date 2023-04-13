package com.android.internal.telephony.ims;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.UserHandle;
import android.permission.LegacyPermissionManager;
import android.telephony.AnomalyReporter;
import android.telephony.ims.ImsService;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsServiceController;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.feature.ImsFeature;
import android.telephony.ims.stub.ImsFeatureConfiguration;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.ims.ImsFeatureBinderRepository;
import com.android.ims.ImsFeatureContainer;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.ExponentialBackoff;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.ims.ImsServiceController;
import com.android.internal.telephony.util.TelephonyUtils;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ImsServiceController {
    private final UUID mAnomalyUUID;
    private ExponentialBackoff mBackoff;
    private ImsServiceControllerCallbacks mCallbacks;
    private ChangedPackages mChangedPackages;
    private final ComponentName mComponentName;
    protected final Context mContext;
    private ImsService.Listener mFeatureChangedListener;
    private Set<ImsFeatureStatusCallback> mFeatureStatusCallbacks;
    private final HandlerThread mHandlerThread;
    private IImsServiceController mIImsServiceController;
    private final ImsEnablementTracker mImsEnablementTracker;
    private Set<ImsFeatureConfiguration.FeatureSlotPair> mImsFeatures;
    private ImsServiceConnection mImsServiceConnection;
    private boolean mIsBinding;
    private boolean mIsBound;
    private int mLastSequenceNumber;
    private final LocalLog mLocalLog;
    protected final Object mLock;
    private PackageManager mPackageManager;
    private final LegacyPermissionManager mPermissionManager;
    private RebindRetry mRebindRetry;
    private ImsFeatureBinderRepository mRepo;
    private Runnable mRestartImsServiceRunnable;
    private long mServiceCapabilities;
    private SparseIntArray mSlotIdToSubIdMap;

    /* loaded from: classes.dex */
    public interface ImsServiceControllerCallbacks {
        void imsServiceBindPermanentError(ComponentName componentName);

        void imsServiceFeatureCreated(int i, int i2, ImsServiceController imsServiceController);

        void imsServiceFeatureRemoved(int i, int i2, ImsServiceController imsServiceController);

        void imsServiceFeaturesChanged(ImsFeatureConfiguration imsFeatureConfiguration, ImsServiceController imsServiceController);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface RebindRetry {
        long getMaximumDelay();

        long getStartDelay();
    }

    protected String getServiceInterface() {
        return "android.telephony.ims.ImsService";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ImsServiceConnection implements ServiceConnection {
        private boolean mIsServiceConnectionDead = false;

        ImsServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (ImsServiceController.this.mLock) {
                ImsServiceController.this.mBackoff.stop();
                ImsServiceController.this.mIsBound = true;
                ImsServiceController.this.mIsBinding = false;
                try {
                    ImsServiceController.this.mLocalLog.log("onServiceConnected");
                    Log.d("ImsServiceController", "ImsService(" + componentName + "): onServiceConnected with binder: " + iBinder);
                    ImsServiceController.this.setServiceController(iBinder);
                    ImsServiceController.this.notifyImsServiceReady();
                    ImsServiceController.this.retrieveStaticImsServiceCapabilities();
                    for (ImsFeatureConfiguration.FeatureSlotPair featureSlotPair : ImsServiceController.this.mImsFeatures) {
                        ImsServiceController imsServiceController = ImsServiceController.this;
                        long modifyCapabiltiesForSlot = imsServiceController.modifyCapabiltiesForSlot(imsServiceController.mImsFeatures, featureSlotPair.slotId, ImsServiceController.this.mServiceCapabilities);
                        ImsServiceController imsServiceController2 = ImsServiceController.this;
                        imsServiceController2.addImsServiceFeature(featureSlotPair, modifyCapabiltiesForSlot, imsServiceController2.mSlotIdToSubIdMap.get(featureSlotPair.slotId));
                    }
                } catch (RemoteException e) {
                    ImsServiceController.this.mIsBound = false;
                    ImsServiceController.this.mIsBinding = false;
                    cleanupConnection();
                    ImsServiceController.this.unbindService();
                    ImsServiceController.this.startDelayedRebindToService();
                    LocalLog localLog = ImsServiceController.this.mLocalLog;
                    localLog.log("onConnected exception=" + e.getMessage() + ", retry in " + ImsServiceController.this.mBackoff.getCurrentDelay() + " mS");
                    StringBuilder sb = new StringBuilder();
                    sb.append("ImsService(");
                    sb.append(componentName);
                    sb.append(") RemoteException:");
                    sb.append(e.getMessage());
                    Log.e("ImsServiceController", sb.toString());
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (ImsServiceController.this.mLock) {
                ImsServiceController.this.mIsBinding = false;
                cleanupConnection();
            }
            ImsServiceController.this.mLocalLog.log("onServiceDisconnected");
            Log.w("ImsServiceController", "ImsService(" + componentName + "): onServiceDisconnected. Waiting...");
            ImsServiceController.this.checkAndReportAnomaly(componentName);
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            this.mIsServiceConnectionDead = true;
            synchronized (ImsServiceController.this.mLock) {
                ImsServiceController.this.mIsBinding = false;
                ImsServiceController.this.mIsBound = false;
                cleanupConnection();
                ImsServiceController.this.unbindService();
                ImsServiceController.this.startDelayedRebindToService();
            }
            Log.w("ImsServiceController", "ImsService(" + componentName + "): onBindingDied. Starting rebind...");
            LocalLog localLog = ImsServiceController.this.mLocalLog;
            localLog.log("onBindingDied, retrying in " + ImsServiceController.this.mBackoff.getCurrentDelay() + " mS");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            Log.w("ImsServiceController", "ImsService(" + componentName + "): onNullBinding. Is service dead = " + this.mIsServiceConnectionDead);
            LocalLog localLog = ImsServiceController.this.mLocalLog;
            StringBuilder sb = new StringBuilder();
            sb.append("onNullBinding, is service dead = ");
            sb.append(this.mIsServiceConnectionDead);
            localLog.log(sb.toString());
            if (this.mIsServiceConnectionDead) {
                return;
            }
            synchronized (ImsServiceController.this.mLock) {
                ImsServiceController.this.mIsBinding = false;
                ImsServiceController.this.mIsBound = true;
                cleanupConnection();
            }
            if (ImsServiceController.this.mCallbacks != null) {
                ImsServiceController.this.mCallbacks.imsServiceBindPermanentError(ImsServiceController.this.getComponentName());
            }
        }

        private void cleanupConnection() {
            ImsServiceController.this.cleanupAllFeatures();
            ImsServiceController.this.setServiceController(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ImsFeatureStatusCallback {
        private final IImsFeatureStatusCallback mCallback = new IImsFeatureStatusCallback.Stub() { // from class: com.android.internal.telephony.ims.ImsServiceController.ImsFeatureStatusCallback.1
            public void notifyImsFeatureStatus(int i) throws RemoteException {
                Log.i("ImsServiceController", "notifyImsFeatureStatus: slot=" + ImsFeatureStatusCallback.this.mSlotId + ", feature=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(ImsFeatureStatusCallback.this.mFeatureType))) + ", status=" + ((String) ImsFeature.STATE_LOG_MAP.get(Integer.valueOf(i))));
                ImsServiceController.this.mRepo.notifyFeatureStateChanged(ImsFeatureStatusCallback.this.mSlotId, ImsFeatureStatusCallback.this.mFeatureType, i);
            }
        };
        private int mFeatureType;
        private int mSlotId;

        ImsFeatureStatusCallback(int i, int i2) {
            this.mSlotId = i;
            this.mFeatureType = i2;
        }

        public IImsFeatureStatusCallback getCallback() {
            return this.mCallback;
        }
    }

    public ImsServiceController(Context context, ComponentName componentName, ImsServiceControllerCallbacks imsServiceControllerCallbacks, ImsFeatureBinderRepository imsFeatureBinderRepository) {
        this.mAnomalyUUID = UUID.fromString("e93b05e4-6d0a-4755-a6da-a2d2dbfb10d6");
        this.mLastSequenceNumber = 0;
        HandlerThread handlerThread = new HandlerThread("ImsServiceControllerHandler");
        this.mHandlerThread = handlerThread;
        this.mIsBound = false;
        this.mIsBinding = false;
        this.mFeatureStatusCallbacks = new HashSet();
        this.mLocalLog = new LocalLog(8);
        this.mLock = new Object();
        this.mFeatureChangedListener = new ImsService.Listener() { // from class: com.android.internal.telephony.ims.ImsServiceController.1
            public void onUpdateSupportedImsFeatures(ImsFeatureConfiguration imsFeatureConfiguration) {
                if (ImsServiceController.this.mCallbacks == null) {
                    return;
                }
                LocalLog localLog = ImsServiceController.this.mLocalLog;
                localLog.log("onUpdateSupportedImsFeatures to " + imsFeatureConfiguration.getServiceFeatures());
                ImsServiceController.this.mCallbacks.imsServiceFeaturesChanged(imsFeatureConfiguration, ImsServiceController.this);
            }
        };
        this.mRestartImsServiceRunnable = new Runnable() { // from class: com.android.internal.telephony.ims.ImsServiceController.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ImsServiceController.this.mLock) {
                    if (ImsServiceController.this.mIsBound) {
                        return;
                    }
                    ImsServiceController imsServiceController = ImsServiceController.this;
                    imsServiceController.bind(imsServiceController.mImsFeatures, ImsServiceController.this.mSlotIdToSubIdMap);
                }
            }
        };
        this.mRebindRetry = new RebindRetry() { // from class: com.android.internal.telephony.ims.ImsServiceController.3
            @Override // com.android.internal.telephony.ims.ImsServiceController.RebindRetry
            public long getMaximumDelay() {
                return 60000L;
            }

            @Override // com.android.internal.telephony.ims.ImsServiceController.RebindRetry
            public long getStartDelay() {
                return 2000L;
            }
        };
        this.mContext = context;
        this.mComponentName = componentName;
        this.mCallbacks = imsServiceControllerCallbacks;
        handlerThread.start();
        this.mBackoff = new ExponentialBackoff(this.mRebindRetry.getStartDelay(), this.mRebindRetry.getMaximumDelay(), 2, handlerThread.getLooper(), this.mRestartImsServiceRunnable);
        this.mPermissionManager = (LegacyPermissionManager) context.getSystemService("legacy_permission");
        this.mRepo = imsFeatureBinderRepository;
        this.mImsEnablementTracker = new ImsEnablementTracker(handlerThread.getLooper(), componentName);
        PackageManager packageManager = context.getPackageManager();
        this.mPackageManager = packageManager;
        if (packageManager != null) {
            ChangedPackages changedPackages = packageManager.getChangedPackages(this.mLastSequenceNumber);
            this.mChangedPackages = changedPackages;
            if (changedPackages != null) {
                this.mLastSequenceNumber = changedPackages.getSequenceNumber();
            }
        }
    }

    @VisibleForTesting
    public ImsServiceController(Context context, ComponentName componentName, ImsServiceControllerCallbacks imsServiceControllerCallbacks, Handler handler, RebindRetry rebindRetry, ImsFeatureBinderRepository imsFeatureBinderRepository) {
        this.mAnomalyUUID = UUID.fromString("e93b05e4-6d0a-4755-a6da-a2d2dbfb10d6");
        this.mLastSequenceNumber = 0;
        this.mHandlerThread = new HandlerThread("ImsServiceControllerHandler");
        this.mIsBound = false;
        this.mIsBinding = false;
        this.mFeatureStatusCallbacks = new HashSet();
        this.mLocalLog = new LocalLog(8);
        this.mLock = new Object();
        this.mFeatureChangedListener = new ImsService.Listener() { // from class: com.android.internal.telephony.ims.ImsServiceController.1
            public void onUpdateSupportedImsFeatures(ImsFeatureConfiguration imsFeatureConfiguration) {
                if (ImsServiceController.this.mCallbacks == null) {
                    return;
                }
                LocalLog localLog = ImsServiceController.this.mLocalLog;
                localLog.log("onUpdateSupportedImsFeatures to " + imsFeatureConfiguration.getServiceFeatures());
                ImsServiceController.this.mCallbacks.imsServiceFeaturesChanged(imsFeatureConfiguration, ImsServiceController.this);
            }
        };
        this.mRestartImsServiceRunnable = new Runnable() { // from class: com.android.internal.telephony.ims.ImsServiceController.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ImsServiceController.this.mLock) {
                    if (ImsServiceController.this.mIsBound) {
                        return;
                    }
                    ImsServiceController imsServiceController = ImsServiceController.this;
                    imsServiceController.bind(imsServiceController.mImsFeatures, ImsServiceController.this.mSlotIdToSubIdMap);
                }
            }
        };
        this.mRebindRetry = new RebindRetry() { // from class: com.android.internal.telephony.ims.ImsServiceController.3
            @Override // com.android.internal.telephony.ims.ImsServiceController.RebindRetry
            public long getMaximumDelay() {
                return 60000L;
            }

            @Override // com.android.internal.telephony.ims.ImsServiceController.RebindRetry
            public long getStartDelay() {
                return 2000L;
            }
        };
        this.mContext = context;
        this.mComponentName = componentName;
        this.mCallbacks = imsServiceControllerCallbacks;
        this.mBackoff = new ExponentialBackoff(rebindRetry.getStartDelay(), rebindRetry.getMaximumDelay(), 2, handler, this.mRestartImsServiceRunnable);
        this.mPermissionManager = null;
        this.mRepo = imsFeatureBinderRepository;
        this.mImsEnablementTracker = new ImsEnablementTracker(handler.getLooper(), componentName);
    }

    public boolean bind(Set<ImsFeatureConfiguration.FeatureSlotPair> set, SparseIntArray sparseIntArray) {
        synchronized (this.mLock) {
            if (this.mIsBound || this.mIsBinding) {
                return false;
            }
            this.mIsBinding = true;
            sanitizeFeatureConfig(set);
            this.mImsFeatures = set;
            this.mSlotIdToSubIdMap = sparseIntArray;
            this.mImsEnablementTracker.setNumOfSlots(sparseIntArray.size());
            grantPermissionsToService();
            Intent component = new Intent(getServiceInterface()).setComponent(this.mComponentName);
            this.mImsServiceConnection = new ImsServiceConnection();
            LocalLog localLog = this.mLocalLog;
            localLog.log("binding " + set);
            Log.i("ImsServiceController", "Binding ImsService:" + this.mComponentName);
            try {
                boolean bindService = this.mContext.bindService(component, this.mImsServiceConnection, 67108929);
                if (!bindService) {
                    LocalLog localLog2 = this.mLocalLog;
                    localLog2.log("    binding failed, retrying in " + this.mBackoff.getCurrentDelay() + " mS");
                    this.mIsBinding = false;
                    this.mBackoff.notifyFailed();
                }
                return bindService;
            } catch (Exception e) {
                this.mBackoff.notifyFailed();
                LocalLog localLog3 = this.mLocalLog;
                localLog3.log("    binding exception=" + e.getMessage() + ", retrying in " + this.mBackoff.getCurrentDelay() + " mS");
                Log.e("ImsServiceController", "Error binding (" + this.mComponentName + ") with exception: " + e.getMessage() + ", rebinding in " + this.mBackoff.getCurrentDelay() + " ms");
                return false;
            }
        }
    }

    private void sanitizeFeatureConfig(Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        for (ImsFeatureConfiguration.FeatureSlotPair featureSlotPair : (Set) set.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$sanitizeFeatureConfig$0;
                lambda$sanitizeFeatureConfig$0 = ImsServiceController.lambda$sanitizeFeatureConfig$0((ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$sanitizeFeatureConfig$0;
            }
        }).collect(Collectors.toSet())) {
            if (!set.contains(new ImsFeatureConfiguration.FeatureSlotPair(featureSlotPair.slotId, 1))) {
                set.remove(featureSlotPair);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$sanitizeFeatureConfig$0(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.featureType == 0;
    }

    public void unbind() throws RemoteException {
        synchronized (this.mLock) {
            this.mBackoff.stop();
            changeImsServiceFeatures(new HashSet(), this.mSlotIdToSubIdMap);
            this.mIsBound = false;
            this.mIsBinding = false;
            setServiceController(null);
            unbindService();
        }
    }

    public void changeImsServiceFeatures(Set<ImsFeatureConfiguration.FeatureSlotPair> set, SparseIntArray sparseIntArray) throws RemoteException {
        int i;
        int[] copyKeys;
        sanitizeFeatureConfig(set);
        synchronized (this.mLock) {
            HashSet hashSet = (HashSet) set.stream().map(new Function() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$changeImsServiceFeatures$1;
                    lambda$changeImsServiceFeatures$1 = ImsServiceController.lambda$changeImsServiceFeatures$1((ImsFeatureConfiguration.FeatureSlotPair) obj);
                    return lambda$changeImsServiceFeatures$1;
                }
            }).collect(Collectors.toCollection(new Supplier() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    return new HashSet();
                }
            }));
            SparseIntArray sparseIntArray2 = new SparseIntArray(hashSet.size());
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                int i2 = this.mSlotIdToSubIdMap.get(num.intValue(), -2);
                int i3 = sparseIntArray.get(num.intValue());
                if (i2 != i3) {
                    sparseIntArray2.put(num.intValue(), i3);
                    this.mLocalLog.log("subId changed for slot: " + num + ", " + i2 + " -> " + i3);
                    Log.i("ImsServiceController", "subId changed for slot: " + num + ", " + i2 + " -> " + i3);
                    if (i3 == -1) {
                        this.mImsEnablementTracker.subIdChangedToInvalid(num.intValue());
                    }
                }
            }
            this.mSlotIdToSubIdMap = sparseIntArray;
            if (this.mImsFeatures.equals(set) && sparseIntArray2.size() == 0) {
                return;
            }
            this.mLocalLog.log("Features (" + this.mImsFeatures + "->" + set + ")");
            Log.i("ImsServiceController", "Features (" + this.mImsFeatures + "->" + set + ") for ImsService: " + this.mComponentName);
            HashSet hashSet2 = new HashSet(this.mImsFeatures);
            this.mImsFeatures = set;
            if (this.mIsBound) {
                HashSet hashSet3 = new HashSet(this.mImsFeatures);
                hashSet3.removeAll(hashSet2);
                Iterator it2 = hashSet3.iterator();
                while (it2.hasNext()) {
                    ImsFeatureConfiguration.FeatureSlotPair featureSlotPair = (ImsFeatureConfiguration.FeatureSlotPair) it2.next();
                    addImsServiceFeature(featureSlotPair, modifyCapabiltiesForSlot(this.mImsFeatures, featureSlotPair.slotId, this.mServiceCapabilities), this.mSlotIdToSubIdMap.get(featureSlotPair.slotId));
                }
                HashSet hashSet4 = new HashSet(hashSet2);
                hashSet4.removeAll(this.mImsFeatures);
                Iterator it3 = hashSet4.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    removeImsServiceFeature((ImsFeatureConfiguration.FeatureSlotPair) it3.next(), false);
                }
                HashSet hashSet5 = new HashSet(this.mImsFeatures);
                hashSet5.removeAll(hashSet4);
                hashSet5.removeAll(hashSet3);
                if (sparseIntArray2.size() > 0) {
                    for (final int i4 : sparseIntArray2.copyKeys()) {
                        int i5 = sparseIntArray2.get(i4, -1);
                        HashSet hashSet6 = (HashSet) hashSet5.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda3
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj) {
                                boolean lambda$changeImsServiceFeatures$2;
                                lambda$changeImsServiceFeatures$2 = ImsServiceController.lambda$changeImsServiceFeatures$2(i4, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                                return lambda$changeImsServiceFeatures$2;
                            }
                        }).collect(Collectors.toCollection(new Supplier() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda2
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                return new HashSet();
                            }
                        }));
                        Iterator it4 = hashSet6.iterator();
                        while (it4.hasNext()) {
                            removeImsServiceFeature((ImsFeatureConfiguration.FeatureSlotPair) it4.next(), true);
                        }
                        Iterator it5 = hashSet6.iterator();
                        while (it5.hasNext()) {
                            ImsFeatureConfiguration.FeatureSlotPair featureSlotPair2 = (ImsFeatureConfiguration.FeatureSlotPair) it5.next();
                            addImsServiceFeature(featureSlotPair2, modifyCapabiltiesForSlot(this.mImsFeatures, featureSlotPair2.slotId, this.mServiceCapabilities), i5);
                        }
                        hashSet5.removeAll(hashSet6);
                    }
                }
                Iterator it6 = hashSet5.iterator();
                while (it6.hasNext()) {
                    ImsFeatureConfiguration.FeatureSlotPair featureSlotPair3 = (ImsFeatureConfiguration.FeatureSlotPair) it6.next();
                    this.mRepo.notifyFeatureCapabilitiesChanged(featureSlotPair3.slotId, featureSlotPair3.featureType, modifyCapabiltiesForSlot(this.mImsFeatures, featureSlotPair3.slotId, this.mServiceCapabilities));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$changeImsServiceFeatures$1(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return Integer.valueOf(featureSlotPair.slotId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$changeImsServiceFeatures$2(int i, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.slotId == i;
    }

    @VisibleForTesting
    public IImsServiceController getImsServiceController() {
        return this.mIImsServiceController;
    }

    @VisibleForTesting
    public long getRebindDelay() {
        return this.mBackoff.getCurrentDelay();
    }

    @VisibleForTesting
    public void stopBackoffTimerForTesting() {
        this.mBackoff.stop();
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public void enableIms(int i, int i2) {
        this.mImsEnablementTracker.enableIms(i, i2);
    }

    public void disableIms(int i, int i2) {
        this.mImsEnablementTracker.disableIms(i, i2);
    }

    public void resetIms(int i, int i2) {
        this.mImsEnablementTracker.resetIms(i, i2);
    }

    public IImsRegistration getRegistration(int i, int i2) throws RemoteException {
        IImsRegistration registration;
        synchronized (this.mLock) {
            registration = isServiceControllerAvailable() ? this.mIImsServiceController.getRegistration(i, i2) : null;
        }
        return registration;
    }

    public IImsConfig getConfig(int i, int i2) throws RemoteException {
        IImsConfig config;
        synchronized (this.mLock) {
            config = isServiceControllerAvailable() ? this.mIImsServiceController.getConfig(i, i2) : null;
        }
        return config;
    }

    public ISipTransport getSipTransport(int i) throws RemoteException {
        ISipTransport sipTransport;
        synchronized (this.mLock) {
            sipTransport = isServiceControllerAvailable() ? this.mIImsServiceController.getSipTransport(i) : null;
        }
        return sipTransport;
    }

    protected long getStaticServiceCapabilities() throws RemoteException {
        long imsServiceCapabilities;
        synchronized (this.mLock) {
            imsServiceCapabilities = isServiceControllerAvailable() ? this.mIImsServiceController.getImsServiceCapabilities() : 0L;
        }
        return imsServiceCapabilities;
    }

    protected void notifyImsServiceReady() throws RemoteException {
        synchronized (this.mLock) {
            if (isServiceControllerAvailable()) {
                Log.d("ImsServiceController", "notifyImsServiceReady");
                this.mIImsServiceController.setListener(this.mFeatureChangedListener);
                this.mIImsServiceController.notifyImsServiceReadyForFeatureCreation();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retrieveStaticImsServiceCapabilities() throws RemoteException {
        long staticServiceCapabilities = getStaticServiceCapabilities();
        Log.i("ImsServiceController", "retrieveStaticImsServiceCapabilities: " + ImsService.getCapabilitiesString(staticServiceCapabilities));
        LocalLog localLog = this.mLocalLog;
        localLog.log("retrieveStaticImsServiceCapabilities: " + ImsService.getCapabilitiesString(staticServiceCapabilities));
        synchronized (this.mLock) {
            this.mServiceCapabilities = staticServiceCapabilities;
        }
    }

    protected void setServiceController(IBinder iBinder) {
        this.mIImsServiceController = IImsServiceController.Stub.asInterface(iBinder);
        this.mImsEnablementTracker.setServiceController(iBinder);
    }

    protected boolean isServiceControllerAvailable() {
        return this.mIImsServiceController != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDelayedRebindToService() {
        this.mBackoff.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        synchronized (this.mLock) {
            if (this.mImsServiceConnection != null) {
                Log.i("ImsServiceController", "Unbinding ImsService: " + this.mComponentName);
                LocalLog localLog = this.mLocalLog;
                localLog.log("unbinding: " + this.mComponentName);
                this.mContext.unbindService(this.mImsServiceConnection);
                this.mImsServiceConnection = null;
            } else {
                Log.i("ImsServiceController", "unbindService called on already unbound ImsService: " + this.mComponentName);
                LocalLog localLog2 = this.mLocalLog;
                localLog2.log("Note: unbindService called with no ServiceConnection on " + this.mComponentName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long modifyCapabiltiesForSlot(Set<ImsFeatureConfiguration.FeatureSlotPair> set, int i, long j) {
        if (getFeaturesForSlot(i, set).contains(0)) {
            j |= 1;
        }
        Log.i("ImsServiceController", "skipping single service enforce check...");
        return j;
    }

    private void grantPermissionsToService() {
        LocalLog localLog = this.mLocalLog;
        localLog.log("grant permissions to " + getComponentName());
        Log.i("ImsServiceController", "Granting Runtime permissions to:" + getComponentName());
        String[] strArr = {this.mComponentName.getPackageName()};
        try {
            if (this.mPermissionManager != null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                this.mPermissionManager.grantDefaultPermissionsToEnabledImsServices(strArr, UserHandle.of(UserHandle.myUserId()), new NetworkTypeController$$ExternalSyntheticLambda1(), new Consumer() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ImsServiceController.lambda$grantPermissionsToService$3(countDownLatch, (Boolean) obj);
                    }
                });
                TelephonyUtils.waitUntilReady(countDownLatch, 15000L);
            }
        } catch (RuntimeException unused) {
            Log.w("ImsServiceController", "Unable to grant permissions, binder died.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$grantPermissionsToService$3(CountDownLatch countDownLatch, Boolean bool) {
        if (bool.booleanValue()) {
            countDownLatch.countDown();
        } else {
            Log.e("ImsServiceController", "Failed to grant permissions to service.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addImsServiceFeature(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair, long j, int i) throws RemoteException {
        if (!isServiceControllerAvailable() || this.mCallbacks == null) {
            Log.w("ImsServiceController", "addImsServiceFeature called with null values.");
            return;
        }
        int i2 = featureSlotPair.featureType;
        if (i2 != 0) {
            addImsFeatureBinder(featureSlotPair.slotId, i, featureSlotPair.featureType, createImsFeature(featureSlotPair.slotId, i, i2, j), j);
            addImsFeatureStatusCallback(featureSlotPair.slotId, featureSlotPair.featureType);
        } else {
            Log.i("ImsServiceController", "supports emergency calling on slot " + featureSlotPair.slotId);
        }
        this.mCallbacks.imsServiceFeatureCreated(featureSlotPair.slotId, featureSlotPair.featureType, this);
    }

    private void removeImsServiceFeature(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair, boolean z) {
        ImsServiceControllerCallbacks imsServiceControllerCallbacks;
        if (!isServiceControllerAvailable() || (imsServiceControllerCallbacks = this.mCallbacks) == null) {
            Log.w("ImsServiceController", "removeImsServiceFeature called with null values.");
            return;
        }
        imsServiceControllerCallbacks.imsServiceFeatureRemoved(featureSlotPair.slotId, featureSlotPair.featureType, this);
        int i = featureSlotPair.featureType;
        if (i != 0) {
            removeImsFeatureStatusCallback(featureSlotPair.slotId, i);
            removeImsFeatureBinder(featureSlotPair.slotId, featureSlotPair.featureType);
            try {
                removeImsFeature(featureSlotPair.slotId, featureSlotPair.featureType, z);
                return;
            } catch (RemoteException e) {
                Log.i("ImsServiceController", "Couldn't remove feature {" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(featureSlotPair.featureType))) + "}, connection is down: " + e.getMessage());
                return;
            }
        }
        Log.i("ImsServiceController", "doesn't support emergency calling on slot " + featureSlotPair.slotId);
    }

    protected IInterface createImsFeature(int i, int i2, int i3, long j) throws RemoteException {
        if (i3 != 1) {
            if (i3 != 2) {
                return null;
            }
            return this.mIImsServiceController.createRcsFeature(i, i2);
        } else if (i2 == -1) {
            if ((1 & j) > 0) {
                return this.mIImsServiceController.createEmergencyOnlyMmTelFeature(i);
            }
            return null;
        } else {
            return this.mIImsServiceController.createMmTelFeature(i, i2);
        }
    }

    private void addImsFeatureStatusCallback(int i, int i2) throws RemoteException {
        ImsFeatureStatusCallback imsFeatureStatusCallback = new ImsFeatureStatusCallback(i, i2);
        this.mFeatureStatusCallbacks.add(imsFeatureStatusCallback);
        registerImsFeatureStatusCallback(i, i2, imsFeatureStatusCallback.getCallback());
    }

    private void removeImsFeatureStatusCallback(final int i, final int i2) {
        ImsFeatureStatusCallback orElse = this.mFeatureStatusCallbacks.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeImsFeatureStatusCallback$4;
                lambda$removeImsFeatureStatusCallback$4 = ImsServiceController.lambda$removeImsFeatureStatusCallback$4(i, i2, (ImsServiceController.ImsFeatureStatusCallback) obj);
                return lambda$removeImsFeatureStatusCallback$4;
            }
        }).findFirst().orElse(null);
        if (orElse != null) {
            this.mFeatureStatusCallbacks.remove(orElse);
            unregisterImsFeatureStatusCallback(i, i2, orElse.getCallback());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$removeImsFeatureStatusCallback$4(int i, int i2, ImsFeatureStatusCallback imsFeatureStatusCallback) {
        return imsFeatureStatusCallback.mSlotId == i && imsFeatureStatusCallback.mFeatureType == i2;
    }

    protected void registerImsFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException {
        this.mIImsServiceController.addFeatureStatusCallback(i, i2, iImsFeatureStatusCallback);
    }

    protected void unregisterImsFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) {
        try {
            this.mIImsServiceController.removeFeatureStatusCallback(i, i2, iImsFeatureStatusCallback);
        } catch (RemoteException unused) {
            LocalLog localLog = this.mLocalLog;
            localLog.log("unregisterImsFeatureStatusCallback - couldn't remove " + iImsFeatureStatusCallback);
        }
    }

    protected void removeImsFeature(int i, int i2, boolean z) throws RemoteException {
        this.mIImsServiceController.removeImsFeature(i, i2, z);
    }

    private void addImsFeatureBinder(int i, int i2, int i3, IInterface iInterface, long j) throws RemoteException {
        if (iInterface == null) {
            Log.w("ImsServiceController", "addImsFeatureBinder: null IInterface reported for " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i3))));
            LocalLog localLog = this.mLocalLog;
            localLog.log("addImsFeatureBinder: null IInterface reported for " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(i3))));
            return;
        }
        this.mRepo.addConnection(i, i2, i3, createFeatureContainer(i, i2, iInterface.asBinder(), j));
    }

    private void removeImsFeatureBinder(int i, int i2) {
        this.mRepo.removeConnection(i, i2);
    }

    private ImsFeatureContainer createFeatureContainer(int i, int i2, IBinder iBinder, long j) throws RemoteException {
        IImsConfig config = getConfig(i, i2);
        IImsRegistration registration = getRegistration(i, i2);
        if (config == null || registration == null) {
            Log.w("ImsServiceController", "createFeatureContainer: invalid state. Reporting as not available. componentName= " + getComponentName());
            this.mLocalLog.log("createFeatureContainer: invalid state. Reporting as not available.");
            return null;
        }
        return new ImsFeatureContainer(iBinder, config, registration, getSipTransport(i), j);
    }

    private List<Integer> getFeaturesForSlot(final int i, Set<ImsFeatureConfiguration.FeatureSlotPair> set) {
        return (List) set.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getFeaturesForSlot$5;
                lambda$getFeaturesForSlot$5 = ImsServiceController.lambda$getFeaturesForSlot$5(i, (ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$getFeaturesForSlot$5;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.ims.ImsServiceController$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getFeaturesForSlot$6;
                lambda$getFeaturesForSlot$6 = ImsServiceController.lambda$getFeaturesForSlot$6((ImsFeatureConfiguration.FeatureSlotPair) obj);
                return lambda$getFeaturesForSlot$6;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getFeaturesForSlot$5(int i, ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return featureSlotPair.slotId == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$getFeaturesForSlot$6(ImsFeatureConfiguration.FeatureSlotPair featureSlotPair) {
        return Integer.valueOf(featureSlotPair.featureType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupAllFeatures() {
        synchronized (this.mLock) {
            for (ImsFeatureConfiguration.FeatureSlotPair featureSlotPair : this.mImsFeatures) {
                removeImsServiceFeature(featureSlotPair, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndReportAnomaly(ComponentName componentName) {
        PackageManager packageManager = this.mPackageManager;
        if (packageManager == null) {
            Log.w("ImsServiceController", "mPackageManager null");
            return;
        }
        ChangedPackages changedPackages = packageManager.getChangedPackages(this.mLastSequenceNumber);
        if (changedPackages != null) {
            this.mLastSequenceNumber = changedPackages.getSequenceNumber();
            if (changedPackages.getPackageNames().contains(componentName.getPackageName())) {
                Log.d("ImsServiceController", "Ignore due to updated, package: " + componentName.getPackageName());
                return;
            }
        }
        AnomalyReporter.reportAnomaly(this.mAnomalyUUID, "IMS Service Crashed");
    }

    public String toString() {
        String str;
        synchronized (this.mLock) {
            str = "[ImsServiceController: componentName=" + getComponentName() + ", features=" + this.mImsFeatures + ", isBinding=" + this.mIsBinding + ", isBound=" + this.mIsBound + ", serviceController=" + getImsServiceController() + ", rebindDelay=" + getRebindDelay() + "]";
        }
        return str;
    }

    public void dump(PrintWriter printWriter) {
        this.mLocalLog.dump(printWriter);
    }
}
