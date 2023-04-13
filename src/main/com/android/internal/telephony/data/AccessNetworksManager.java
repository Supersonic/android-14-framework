package com.android.internal.telephony.data;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.AccessNetworkConstants;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.data.ApnSetting;
import android.telephony.data.IQualifiedNetworksService;
import android.telephony.data.IQualifiedNetworksServiceCallback;
import android.telephony.data.ThrottleStatus;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda6;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.SlidingWindowEventCounter;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataRetryManager;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class AccessNetworksManager extends Handler {
    public static final int[] SUPPORTED_APN_TYPES = {17, 2, 32, 64, 128, 4, 512, 2048, 8};
    private final Set<AccessNetworksManagerCallback> mAccessNetworksManagerCallbacks;
    private final UUID mAnomalyUUID;
    private final SparseArray<SlidingWindowEventCounter> mApnTypeToQnsChangeNetworkCounter;
    private final SparseArray<int[]> mAvailableNetworks;
    private final int[] mAvailableTransports;
    private final CarrierConfigManager mCarrierConfigManager;
    private DataConfigManager mDataConfigManager;
    private AccessNetworksManagerDeathRecipient mDeathRecipient;
    private IQualifiedNetworksService mIQualifiedNetworksService;
    private String mLastBoundPackageName;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final Phone mPhone;
    private final Map<Integer, Integer> mPreferredTransports;
    private final RegistrantList mQualifiedNetworksChangedRegistrants;
    private QualifiedNetworksServiceConnection mServiceConnection;
    private String mTargetBindingPackageName;

    private static int getTransportFromAccessNetwork(int i) {
        return i == 5 ? 2 : 1;
    }

    /* loaded from: classes.dex */
    public static class QualifiedNetworks {
        public final int apnType;
        public final int[] qualifiedNetworks;

        public QualifiedNetworks(int i, int[] iArr) {
            this.apnType = i;
            this.qualifiedNetworks = Arrays.stream(iArr).boxed().filter(new Predicate() { // from class: com.android.internal.telephony.data.AccessNetworksManager$QualifiedNetworks$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DataUtils.isValidAccessNetwork(((Integer) obj).intValue());
                }
            }).mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
        }

        public String toString() {
            return "[QualifiedNetworks: apnType=" + ApnSetting.getApnTypeString(this.apnType) + ", networks=" + ((String) Arrays.stream(this.qualifiedNetworks).mapToObj(new C0097xb9c0cb50()).collect(Collectors.joining(","))) + "]";
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1) {
            onEmergencyDataNetworkPreferredTransportChanged(((Integer) ((AsyncResult) message.obj).result).intValue());
            return;
        }
        loge("Unexpected event " + message.what);
    }

    /* loaded from: classes.dex */
    private class AccessNetworksManagerDeathRecipient implements IBinder.DeathRecipient {
        private AccessNetworksManagerDeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            String str = "Qualified network service " + AccessNetworksManager.this.mLastBoundPackageName + " died.";
            AccessNetworksManager.this.mApnTypeToQnsChangeNetworkCounter.clear();
            AccessNetworksManager.this.loge(str);
            AnomalyReporter.reportAnomaly(AccessNetworksManager.this.mAnomalyUUID, str, AccessNetworksManager.this.mPhone.getCarrierId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class QualifiedNetworksServiceConnection implements ServiceConnection {
        private QualifiedNetworksServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AccessNetworksManager.this.mIQualifiedNetworksService = IQualifiedNetworksService.Stub.asInterface(iBinder);
            AccessNetworksManager accessNetworksManager = AccessNetworksManager.this;
            accessNetworksManager.mDeathRecipient = new AccessNetworksManagerDeathRecipient();
            AccessNetworksManager accessNetworksManager2 = AccessNetworksManager.this;
            accessNetworksManager2.mLastBoundPackageName = accessNetworksManager2.getQualifiedNetworksServicePackageName();
            try {
                iBinder.linkToDeath(AccessNetworksManager.this.mDeathRecipient, 0);
                AccessNetworksManager.this.mIQualifiedNetworksService.createNetworkAvailabilityProvider(AccessNetworksManager.this.mPhone.getPhoneId(), new QualifiedNetworksServiceCallback());
            } catch (RemoteException e) {
                AccessNetworksManager accessNetworksManager3 = AccessNetworksManager.this;
                accessNetworksManager3.loge("Remote exception. " + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            AccessNetworksManager.this.mTargetBindingPackageName = null;
        }
    }

    /* loaded from: classes.dex */
    private final class QualifiedNetworksServiceCallback extends IQualifiedNetworksServiceCallback.Stub {
        private QualifiedNetworksServiceCallback() {
        }

        public void onQualifiedNetworkTypesChanged(int i, int[] iArr) {
            int[] iArr2;
            if (iArr == null) {
                AccessNetworksManager.this.loge("onQualifiedNetworkTypesChanged: Ignored null input.");
                return;
            }
            AccessNetworksManager.this.log("onQualifiedNetworkTypesChanged: apnTypes = [" + ApnSetting.getApnTypesStringFromBitmask(i) + "], networks = [" + ((String) Arrays.stream(iArr).mapToObj(new C0097xb9c0cb50()).collect(Collectors.joining(","))) + "]");
            if (Arrays.stream(iArr).anyMatch(new IntPredicate() { // from class: com.android.internal.telephony.data.AccessNetworksManager$QualifiedNetworksServiceCallback$$ExternalSyntheticLambda0
                @Override // java.util.function.IntPredicate
                public final boolean test(int i2) {
                    boolean lambda$onQualifiedNetworkTypesChanged$0;
                    lambda$onQualifiedNetworkTypesChanged$0 = AccessNetworksManager.QualifiedNetworksServiceCallback.lambda$onQualifiedNetworkTypesChanged$0(i2);
                    return lambda$onQualifiedNetworkTypesChanged$0;
                }
            })) {
                AccessNetworksManager.this.loge("Invalid access networks " + Arrays.toString(iArr));
                if (AccessNetworksManager.this.mDataConfigManager == null || !AccessNetworksManager.this.mDataConfigManager.isInvalidQnsParamAnomalyReportEnabled()) {
                    return;
                }
                AccessNetworksManager.this.reportAnomaly("QNS requested invalid Network Type", "3e89a3df-3524-45fa-b5f2-0fb0e4c77ec4");
                return;
            }
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            for (final int i3 : AccessNetworksManager.SUPPORTED_APN_TYPES) {
                if ((i & i3) == i3) {
                    if (AccessNetworksManager.this.mDataConfigManager != null) {
                        i2 |= i3;
                    }
                    if (AccessNetworksManager.this.mAvailableNetworks.get(i3) != null && Arrays.equals((int[]) AccessNetworksManager.this.mAvailableNetworks.get(i3), iArr)) {
                        AccessNetworksManager.this.log("Available networks for " + ApnSetting.getApnTypesStringFromBitmask(i3) + " not changed.");
                    } else if (iArr.length == 0) {
                        AccessNetworksManager.this.mAvailableNetworks.remove(i3);
                        if (AccessNetworksManager.this.getPreferredTransport(i3) == 2) {
                            AccessNetworksManager.this.mPreferredTransports.put(Integer.valueOf(i3), 1);
                            AccessNetworksManager.this.mAccessNetworksManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.AccessNetworksManager$QualifiedNetworksServiceCallback$$ExternalSyntheticLambda1
                                @Override // java.util.function.Consumer
                                public final void accept(Object obj) {
                                    AccessNetworksManager.QualifiedNetworksServiceCallback.lambda$onQualifiedNetworkTypesChanged$2(i3, (AccessNetworksManager.AccessNetworksManagerCallback) obj);
                                }
                            });
                        }
                    } else {
                        AccessNetworksManager.this.mAvailableNetworks.put(i3, iArr);
                        arrayList.add(new QualifiedNetworks(i3, iArr));
                    }
                }
            }
            if (i2 != i && AccessNetworksManager.this.mDataConfigManager != null && AccessNetworksManager.this.mDataConfigManager.isInvalidQnsParamAnomalyReportEnabled()) {
                AccessNetworksManager.this.reportAnomaly("QNS requested unsupported APN Types:" + Integer.toBinaryString(i ^ i2), "3e89a3df-3524-45fa-b5f2-0fb0e4c77ec5");
            }
            if (arrayList.isEmpty()) {
                return;
            }
            AccessNetworksManager.this.setPreferredTransports(arrayList);
            AccessNetworksManager.this.mQualifiedNetworksChangedRegistrants.notifyResult(arrayList);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onQualifiedNetworkTypesChanged$0(int i) {
            return !DataUtils.isValidAccessNetwork(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onQualifiedNetworkTypesChanged$2(final int i, final AccessNetworksManagerCallback accessNetworksManagerCallback) {
            accessNetworksManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.AccessNetworksManager$QualifiedNetworksServiceCallback$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AccessNetworksManager.QualifiedNetworksServiceCallback.lambda$onQualifiedNetworkTypesChanged$1(AccessNetworksManager.AccessNetworksManagerCallback.this, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onQualifiedNetworkTypesChanged$1(AccessNetworksManagerCallback accessNetworksManagerCallback, int i) {
            accessNetworksManagerCallback.onPreferredTransportChanged(DataUtils.apnTypeToNetworkCapability(i));
        }
    }

    private void onEmergencyDataNetworkPreferredTransportChanged(int i) {
        try {
            logl("onEmergencyDataNetworkPreferredTransportChanged: " + AccessNetworkConstants.transportTypeToString(i));
            IQualifiedNetworksService iQualifiedNetworksService = this.mIQualifiedNetworksService;
            if (iQualifiedNetworksService != null) {
                iQualifiedNetworksService.reportEmergencyDataNetworkPreferredTransportChanged(this.mPhone.getPhoneId(), i);
            }
        } catch (Exception e) {
            loge("onEmergencyDataNetworkPreferredTransportChanged: ", e);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class AccessNetworksManagerCallback extends DataCallback {
        public abstract void onPreferredTransportChanged(int i);

        public AccessNetworksManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public AccessNetworksManager(Phone phone, Looper looper) {
        super(looper);
        this.mLocalLog = new LocalLog(64);
        this.mAnomalyUUID = UUID.fromString("c2d1a639-00e2-4561-9619-6acf37d90590");
        this.mAvailableNetworks = new SparseArray<>();
        this.mQualifiedNetworksChangedRegistrants = new RegistrantList();
        this.mPreferredTransports = new ConcurrentHashMap();
        this.mAccessNetworksManagerCallbacks = new ArraySet();
        this.mPhone = phone;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService("carrier_config");
        this.mCarrierConfigManager = carrierConfigManager;
        this.mLogTag = "ANM-" + phone.getPhoneId();
        this.mApnTypeToQnsChangeNetworkCounter = new SparseArray<>();
        this.mAvailableTransports = new int[]{1, 2};
        carrierConfigManager.registerCarrierConfigChangeListener(new NetworkTypeController$$ExternalSyntheticLambda1(), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda0
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                AccessNetworksManager.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        bindQualifiedNetworksService();
        post(new Runnable() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AccessNetworksManager.this.lambda$new$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        bindQualifiedNetworksService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mPhone.getDataNetworkController().getDataRetryManager().registerCallback(new DataRetryManager.DataRetryManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                AccessNetworksManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.AccessNetworksManager.1
            @Override // com.android.internal.telephony.data.DataRetryManager.DataRetryManagerCallback
            public void onThrottleStatusChanged(List<ThrottleStatus> list) {
                try {
                    AccessNetworksManager accessNetworksManager = AccessNetworksManager.this;
                    accessNetworksManager.logl("onThrottleStatusChanged: " + list);
                    if (AccessNetworksManager.this.mIQualifiedNetworksService != null) {
                        AccessNetworksManager.this.mIQualifiedNetworksService.reportThrottleStatusChanged(AccessNetworksManager.this.mPhone.getPhoneId(), list);
                    }
                } catch (Exception e) {
                    AccessNetworksManager.this.loge("onThrottleStatusChanged: ", e);
                }
            }
        });
        DataConfigManager dataConfigManager = this.mPhone.getDataNetworkController().getDataConfigManager();
        this.mDataConfigManager = dataConfigManager;
        dataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                AccessNetworksManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.AccessNetworksManager.2
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onDeviceConfigChanged() {
                AccessNetworksManager.this.mApnTypeToQnsChangeNetworkCounter.clear();
            }
        });
        this.mPhone.registerForEmergencyDomainSelected(this, 1, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportAnomaly(String str, String str2) {
        logl(str);
        AnomalyReporter.reportAnomaly(UUID.fromString(str2), str, this.mPhone.getCarrierId());
    }

    private void bindQualifiedNetworksService() {
        post(new Runnable() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AccessNetworksManager.this.lambda$bindQualifiedNetworksService$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindQualifiedNetworksService$2() {
        Intent component;
        String qualifiedNetworksServicePackageName = getQualifiedNetworksServicePackageName();
        String qualifiedNetworksServiceClassName = getQualifiedNetworksServiceClassName();
        if (TextUtils.isEmpty(qualifiedNetworksServicePackageName)) {
            loge("Can't find the binding package");
            return;
        }
        if (TextUtils.isEmpty(qualifiedNetworksServiceClassName)) {
            component = new Intent("android.telephony.data.QualifiedNetworksService");
            component.setPackage(qualifiedNetworksServicePackageName);
        } else {
            component = new Intent("android.telephony.data.QualifiedNetworksService").setComponent(new ComponentName(qualifiedNetworksServicePackageName, qualifiedNetworksServiceClassName));
        }
        if (TextUtils.equals(qualifiedNetworksServicePackageName, this.mTargetBindingPackageName)) {
            return;
        }
        IQualifiedNetworksService iQualifiedNetworksService = this.mIQualifiedNetworksService;
        if (iQualifiedNetworksService != null && iQualifiedNetworksService.asBinder().isBinderAlive()) {
            try {
                this.mIQualifiedNetworksService.removeNetworkAvailabilityProvider(this.mPhone.getPhoneId());
            } catch (RemoteException e) {
                loge("Cannot remove network availability updater. " + e);
            }
            this.mPhone.getContext().unbindService(this.mServiceConnection);
        }
        try {
            this.mServiceConnection = new QualifiedNetworksServiceConnection();
            log("bind to " + qualifiedNetworksServicePackageName);
            if (!this.mPhone.getContext().bindService(component, this.mServiceConnection, 1)) {
                loge("Cannot bind to the qualified networks service.");
            } else {
                this.mTargetBindingPackageName = qualifiedNetworksServicePackageName;
            }
        } catch (Exception e2) {
            loge("Cannot bind to the qualified networks service. Exception: " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getQualifiedNetworksServicePackageName() {
        String string = this.mPhone.getContext().getResources().getString(17039982);
        try {
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mPhone.getSubId(), new String[]{"carrier_qualified_networks_service_package_override_string"});
            if (configForSubId == null || configForSubId.isEmpty()) {
                return string;
            }
            String string2 = configForSubId.getString("carrier_qualified_networks_service_package_override_string");
            return !TextUtils.isEmpty(string2) ? string2 : string;
        } catch (RuntimeException unused) {
            this.loge("Carrier config loader is not available.");
            return string;
        }
    }

    private String getQualifiedNetworksServiceClassName() {
        String string = this.mPhone.getContext().getResources().getString(17039981);
        try {
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mPhone.getSubId(), new String[]{"carrier_qualified_networks_service_class_override_string"});
            if (configForSubId == null || configForSubId.isEmpty()) {
                return string;
            }
            String string2 = configForSubId.getString("carrier_qualified_networks_service_class_override_string");
            return !TextUtils.isEmpty(string2) ? string2 : string;
        } catch (RuntimeException unused) {
            this.loge("Carrier config loader is not available.");
            return string;
        }
    }

    private List<QualifiedNetworks> getQualifiedNetworksList() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mAvailableNetworks.size(); i++) {
            arrayList.add(new QualifiedNetworks(this.mAvailableNetworks.keyAt(i), this.mAvailableNetworks.valueAt(i)));
        }
        return arrayList;
    }

    public void registerForQualifiedNetworksChanged(Handler handler, int i) {
        if (handler != null) {
            Registrant registrant = new Registrant(handler, i, null);
            this.mQualifiedNetworksChangedRegistrants.add(registrant);
            if (this.mAvailableNetworks.size() != 0) {
                registrant.notifyResult(getQualifiedNetworksList());
            }
        }
    }

    public int[] getAvailableTransports() {
        return this.mAvailableTransports;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreferredTransports(List<QualifiedNetworks> list) {
        int transportFromAccessNetwork;
        for (final QualifiedNetworks qualifiedNetworks : list) {
            int[] iArr = qualifiedNetworks.qualifiedNetworks;
            if (iArr.length > 0 && getPreferredTransport(qualifiedNetworks.apnType) != (transportFromAccessNetwork = getTransportFromAccessNetwork(iArr[0]))) {
                this.mPreferredTransports.put(Integer.valueOf(qualifiedNetworks.apnType), Integer.valueOf(transportFromAccessNetwork));
                this.mAccessNetworksManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AccessNetworksManager.lambda$setPreferredTransports$4(AccessNetworksManager.QualifiedNetworks.this, (AccessNetworksManager.AccessNetworksManagerCallback) obj);
                    }
                });
                logl("setPreferredTransports: apnType=" + ApnSetting.getApnTypeString(qualifiedNetworks.apnType) + ", transport=" + AccessNetworkConstants.transportTypeToString(transportFromAccessNetwork));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setPreferredTransports$4(final QualifiedNetworks qualifiedNetworks, final AccessNetworksManagerCallback accessNetworksManagerCallback) {
        accessNetworksManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.AccessNetworksManager$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                AccessNetworksManager.lambda$setPreferredTransports$3(AccessNetworksManager.AccessNetworksManagerCallback.this, qualifiedNetworks);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setPreferredTransports$3(AccessNetworksManagerCallback accessNetworksManagerCallback, QualifiedNetworks qualifiedNetworks) {
        accessNetworksManagerCallback.onPreferredTransportChanged(DataUtils.apnTypeToNetworkCapability(qualifiedNetworks.apnType));
    }

    public int getPreferredTransport(int i) {
        if (this.mPreferredTransports.get(Integer.valueOf(i)) == null) {
            return 1;
        }
        return this.mPreferredTransports.get(Integer.valueOf(i)).intValue();
    }

    public int getPreferredTransportByNetworkCapability(int i) {
        int networkCapabilityToApnType = DataUtils.networkCapabilityToApnType(i);
        if (networkCapabilityToApnType == 0) {
            return 1;
        }
        return getPreferredTransport(networkCapabilityToApnType);
    }

    public boolean isAnyApnOnIwlan() {
        for (int i : SUPPORTED_APN_TYPES) {
            if (getPreferredTransport(i) == 2) {
                return true;
            }
        }
        return false;
    }

    public void unregisterForQualifiedNetworksChanged(Handler handler) {
        if (handler != null) {
            this.mQualifiedNetworksChangedRegistrants.remove(handler);
        }
    }

    public void registerCallback(AccessNetworksManagerCallback accessNetworksManagerCallback) {
        this.mAccessNetworksManagerCallbacks.add(accessNetworksManagerCallback);
    }

    public void unregisterCallback(AccessNetworksManagerCallback accessNetworksManagerCallback) {
        this.mAccessNetworksManagerCallbacks.remove(accessNetworksManagerCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str, Exception exc) {
        Rlog.e(this.mLogTag, str, exc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int[] iArr;
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(AccessNetworksManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("preferred transports=");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (int i : SUPPORTED_APN_TYPES) {
            androidUtilIndentingPrintWriter.println(ApnSetting.getApnTypeString(i) + ": " + AccessNetworkConstants.transportTypeToString(getPreferredTransport(i)));
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Local logs=");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.flush();
    }
}
