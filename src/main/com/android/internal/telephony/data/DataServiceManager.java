package com.android.internal.telephony.data;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.LinkProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.permission.LegacyPermissionManager;
import android.telephony.AccessNetworkConstants;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.data.DataCallResponse;
import android.telephony.data.DataProfile;
import android.telephony.data.DataServiceCallback;
import android.telephony.data.IDataService;
import android.telephony.data.IDataServiceCallback;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import android.text.TextUtils;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DataServiceManager extends Handler {
    private final RegistrantList mApnUnthrottledRegistrants;
    private final AppOpsManager mAppOps;
    private boolean mBound;
    private final CarrierConfigManager mCarrierConfigManager;
    private final RegistrantList mDataCallListChangedRegistrants;
    private DataServiceManagerDeathRecipient mDeathRecipient;
    private IDataService mIDataService;
    private String mLastBoundPackageName;
    private List<DataCallResponse> mLastDataCallResponseList;
    private final Map<IBinder, Message> mMessageMap;
    private final LegacyPermissionManager mPermissionManager;
    private final Phone mPhone;
    private final RegistrantList mServiceBindingChangedRegistrants;
    private CellularDataServiceConnection mServiceConnection;
    private final String mTag;
    private String mTargetBindingPackageName;
    private final int mTransportType;

    /* loaded from: classes.dex */
    private class DataServiceManagerDeathRecipient implements IBinder.DeathRecipient {
        private DataServiceManagerDeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            String str = "Data service " + DataServiceManager.this.mLastBoundPackageName + " for transport type " + AccessNetworkConstants.transportTypeToString(DataServiceManager.this.mTransportType) + " died.";
            DataServiceManager.this.loge(str);
            AnomalyReporter.reportAnomaly(UUID.fromString("fc1956de-c080-45de-8431-a1faab687110"), str, DataServiceManager.this.mPhone.getCarrierId());
            for (Message message : DataServiceManager.this.mMessageMap.values()) {
                DataServiceManager.this.sendCompleteMessage(message, 4);
            }
            DataServiceManager.this.mMessageMap.clear();
            DataServiceManager dataServiceManager = DataServiceManager.this;
            List list = Collections.EMPTY_LIST;
            dataServiceManager.mLastDataCallResponseList = list;
            DataServiceManager.this.mDataCallListChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, list, (Throwable) null));
        }
    }

    private void grantPermissionsToService(String str) {
        String[] strArr = {str};
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            this.mPermissionManager.grantDefaultPermissionsToEnabledTelephonyDataServices(strArr, UserHandle.of(UserHandle.myUserId()), new NetworkTypeController$$ExternalSyntheticLambda1(), new Consumer() { // from class: com.android.internal.telephony.data.DataServiceManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataServiceManager.this.lambda$grantPermissionsToService$0(countDownLatch, (Boolean) obj);
                }
            });
            TelephonyUtils.waitUntilReady(countDownLatch, 15000L);
            this.mAppOps.setMode("android:manage_ipsec_tunnels", UserHandle.myUserId(), strArr[0], 0);
            this.mAppOps.setMode("android:fine_location", UserHandle.myUserId(), strArr[0], 0);
        } catch (RuntimeException e) {
            loge("Binder to package manager died, permission grant for DataService failed.");
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantPermissionsToService$0(CountDownLatch countDownLatch, Boolean bool) {
        if (bool.booleanValue()) {
            countDownLatch.countDown();
        } else {
            loge("Failed to grant permissions to service.");
        }
    }

    private void revokePermissionsFromUnusedDataServices() {
        Set<String> allDataServicePackageNames = getAllDataServicePackageNames();
        for (int i : this.mPhone.getAccessNetworksManager().getAvailableTransports()) {
            allDataServicePackageNames.remove(getDataServicePackageName(i));
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            String[] strArr = new String[allDataServicePackageNames.size()];
            allDataServicePackageNames.toArray(strArr);
            this.mPermissionManager.revokeDefaultPermissionsFromDisabledTelephonyDataServices(strArr, UserHandle.of(UserHandle.myUserId()), new NetworkTypeController$$ExternalSyntheticLambda1(), new Consumer() { // from class: com.android.internal.telephony.data.DataServiceManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataServiceManager.this.lambda$revokePermissionsFromUnusedDataServices$1(countDownLatch, (Boolean) obj);
                }
            });
            TelephonyUtils.waitUntilReady(countDownLatch, 15000L);
            for (String str : allDataServicePackageNames) {
                this.mAppOps.setMode("android:manage_ipsec_tunnels", UserHandle.myUserId(), str, 2);
                this.mAppOps.setMode("android:fine_location", UserHandle.myUserId(), str, 2);
            }
        } catch (RuntimeException e) {
            loge("Binder to package manager died; failed to revoke DataService permissions.");
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$revokePermissionsFromUnusedDataServices$1(CountDownLatch countDownLatch, Boolean bool) {
        if (bool.booleanValue()) {
            countDownLatch.countDown();
        } else {
            loge("Failed to revoke permissions from data services.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class CellularDataServiceConnection implements ServiceConnection {
        private CellularDataServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onServiceConnected: " + componentName);
            DataServiceManager.this.mIDataService = IDataService.Stub.asInterface(iBinder);
            DataServiceManager dataServiceManager2 = DataServiceManager.this;
            dataServiceManager2.mDeathRecipient = new DataServiceManagerDeathRecipient();
            DataServiceManager.this.mBound = true;
            DataServiceManager dataServiceManager3 = DataServiceManager.this;
            dataServiceManager3.mLastBoundPackageName = dataServiceManager3.getDataServicePackageName();
            DataServiceManager.this.removeMessages(2);
            try {
                iBinder.linkToDeath(DataServiceManager.this.mDeathRecipient, 0);
                DataServiceManager.this.mIDataService.createDataServiceProvider(DataServiceManager.this.mPhone.getPhoneId());
                DataServiceManager.this.mIDataService.registerForDataCallListChanged(DataServiceManager.this.mPhone.getPhoneId(), new DataServiceCallbackWrapper("dataCallListChanged"));
                DataServiceManager.this.mIDataService.registerForUnthrottleApn(DataServiceManager.this.mPhone.getPhoneId(), new DataServiceCallbackWrapper("unthrottleApn"));
                DataServiceManager.this.mServiceBindingChangedRegistrants.notifyResult(Boolean.TRUE);
            } catch (RemoteException e) {
                DataServiceManager dataServiceManager4 = DataServiceManager.this;
                dataServiceManager4.loge("Remote exception. " + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            DataServiceManager.this.log("onServiceDisconnected");
            DataServiceManager.this.removeMessages(2);
            DataServiceManager.this.mIDataService = null;
            DataServiceManager.this.mBound = false;
            DataServiceManager.this.mServiceBindingChangedRegistrants.notifyResult(Boolean.FALSE);
            DataServiceManager.this.mTargetBindingPackageName = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DataServiceCallbackWrapper extends IDataServiceCallback.Stub {
        private final String mTag;

        DataServiceCallbackWrapper(String str) {
            this.mTag = str;
        }

        public String getTag() {
            return this.mTag;
        }

        public void onSetupDataCallComplete(int i, DataCallResponse dataCallResponse) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onSetupDataCallComplete. resultCode = " + i + ", response = " + dataCallResponse);
            DataServiceManager.this.removeMessages(2, this);
            Message message = (Message) DataServiceManager.this.mMessageMap.remove(asBinder());
            if (message != null) {
                message.getData().putParcelable("data_call_response", dataCallResponse);
                DataServiceManager.this.sendCompleteMessage(message, i);
                return;
            }
            DataServiceManager.this.loge("Unable to find the message for setup call response.");
        }

        public void onDeactivateDataCallComplete(int i) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onDeactivateDataCallComplete. resultCode = " + i);
            DataServiceManager.this.removeMessages(2, this);
            DataServiceManager.this.sendCompleteMessage((Message) DataServiceManager.this.mMessageMap.remove(asBinder()), i);
        }

        public void onSetInitialAttachApnComplete(int i) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onSetInitialAttachApnComplete. resultCode = " + i);
            DataServiceManager.this.sendCompleteMessage((Message) DataServiceManager.this.mMessageMap.remove(asBinder()), i);
        }

        public void onSetDataProfileComplete(int i) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onSetDataProfileComplete. resultCode = " + i);
            DataServiceManager.this.sendCompleteMessage((Message) DataServiceManager.this.mMessageMap.remove(asBinder()), i);
        }

        public void onRequestDataCallListComplete(int i, List<DataCallResponse> list) {
            DataServiceManager.this.log("onRequestDataCallListComplete. resultCode = " + DataServiceCallback.resultCodeToString(i));
            Message message = (Message) DataServiceManager.this.mMessageMap.remove(asBinder());
            if (message != null) {
                message.getData().putParcelableList("data_call_response", list);
            }
            DataServiceManager.this.sendCompleteMessage(message, i);
            if (DataServiceManager.this.mTransportType == 1) {
                if (DataServiceManager.this.mLastDataCallResponseList.size() != list.size() || !DataServiceManager.this.mLastDataCallResponseList.containsAll(list)) {
                    String str = "RIL reported mismatched data call response list for WWAN: mLastDataCallResponseList=" + DataServiceManager.this.mLastDataCallResponseList + ", dataCallList=" + list;
                    DataServiceManager.this.loge(str);
                    if (!((Set) list.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataServiceManager$DataServiceCallbackWrapper$$ExternalSyntheticLambda0
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return Integer.valueOf(((DataCallResponse) obj).getId());
                        }
                    }).collect(Collectors.toSet())).equals(DataServiceManager.this.mLastDataCallResponseList.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataServiceManager$DataServiceCallbackWrapper$$ExternalSyntheticLambda0
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return Integer.valueOf(((DataCallResponse) obj).getId());
                        }
                    }).collect(Collectors.toSet()))) {
                        AnomalyReporter.reportAnomaly(UUID.fromString("150323b2-360a-446b-a158-3ce6425821f6"), str, DataServiceManager.this.mPhone.getCarrierId());
                    }
                }
                onDataCallListChanged(list);
            }
        }

        public void onDataCallListChanged(List<DataCallResponse> list) {
            DataServiceManager.this.mLastDataCallResponseList = list != null ? list : Collections.EMPTY_LIST;
            DataServiceManager.this.mDataCallListChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, list, (Throwable) null));
        }

        public void onHandoverStarted(int i) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onHandoverStarted. resultCode = " + i);
            DataServiceManager.this.removeMessages(2, this);
            DataServiceManager.this.sendCompleteMessage((Message) DataServiceManager.this.mMessageMap.remove(asBinder()), i);
        }

        public void onHandoverCancelled(int i) {
            DataServiceManager dataServiceManager = DataServiceManager.this;
            dataServiceManager.log("onHandoverCancelled. resultCode = " + i);
            DataServiceManager.this.removeMessages(2, this);
            DataServiceManager.this.sendCompleteMessage((Message) DataServiceManager.this.mMessageMap.remove(asBinder()), i);
        }

        public void onApnUnthrottled(String str) {
            if (str != null) {
                DataServiceManager.this.mApnUnthrottledRegistrants.notifyRegistrants(new AsyncResult((Object) null, str, (Throwable) null));
            } else {
                DataServiceManager.this.loge("onApnUnthrottled: apn is null");
            }
        }

        public void onDataProfileUnthrottled(DataProfile dataProfile) {
            if (dataProfile != null) {
                DataServiceManager.this.mApnUnthrottledRegistrants.notifyRegistrants(new AsyncResult((Object) null, dataProfile, (Throwable) null));
            } else {
                DataServiceManager.this.loge("onDataProfileUnthrottled: dataProfile is null");
            }
        }
    }

    public DataServiceManager(Phone phone, Looper looper, int i) {
        super(looper);
        this.mServiceBindingChangedRegistrants = new RegistrantList();
        this.mMessageMap = new ConcurrentHashMap();
        this.mDataCallListChangedRegistrants = new RegistrantList();
        this.mApnUnthrottledRegistrants = new RegistrantList();
        this.mLastDataCallResponseList = Collections.EMPTY_LIST;
        this.mPhone = phone;
        this.mTransportType = i;
        StringBuilder sb = new StringBuilder();
        sb.append("DSM-");
        sb.append(i == 1 ? "C-" : "I-");
        sb.append(phone.getPhoneId());
        this.mTag = sb.toString();
        this.mBound = false;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService("carrier_config");
        this.mCarrierConfigManager = carrierConfigManager;
        this.mPermissionManager = (LegacyPermissionManager) phone.getContext().getSystemService("legacy_permission");
        this.mAppOps = (AppOpsManager) phone.getContext().getSystemService("appops");
        carrierConfigManager.registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.data.DataServiceManager$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataServiceManager.this.post(runnable);
            }
        }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.data.DataServiceManager$$ExternalSyntheticLambda2
            public final void onCarrierConfigChanged(int i2, int i3, int i4, int i5) {
                DataServiceManager.this.lambda$new$2(i2, i3, i4, i5);
            }
        });
        PhoneConfigurationManager.registerForMultiSimConfigChange(this, 1, null);
        rebindDataService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i, int i2, int i3, int i4) {
        if (i == this.mPhone.getPhoneId()) {
            log("Carrier config changed. Try to bind data service.");
            rebindDataService();
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            rebindDataService();
        } else if (i == 2) {
            handleRequestUnresponded((DataServiceCallbackWrapper) message.obj);
        } else {
            loge("Unhandled event " + message.what);
        }
    }

    private void handleRequestUnresponded(DataServiceCallbackWrapper dataServiceCallbackWrapper) {
        String str = "Request " + dataServiceCallbackWrapper.getTag() + " unresponded on transport " + AccessNetworkConstants.transportTypeToString(this.mTransportType) + " in 600 seconds.";
        log(str);
        AnomalyReporter.reportAnomaly(UUID.fromString("f5d5cbe6-9bd6-4009-b764-42b1b649b1de"), str, this.mPhone.getCarrierId());
    }

    private void unbindDataService() {
        revokePermissionsFromUnusedDataServices();
        IDataService iDataService = this.mIDataService;
        if (iDataService != null && iDataService.asBinder().isBinderAlive()) {
            log("unbinding service");
            try {
                this.mIDataService.removeDataServiceProvider(this.mPhone.getPhoneId());
            } catch (RemoteException e) {
                loge("Cannot remove data service provider. " + e);
            }
        }
        if (this.mServiceConnection != null) {
            this.mPhone.getContext().unbindService(this.mServiceConnection);
        }
        this.mIDataService = null;
        this.mServiceConnection = null;
        this.mTargetBindingPackageName = null;
        this.mBound = false;
    }

    private void bindDataService(String str) {
        Intent component;
        Phone phone = this.mPhone;
        if (phone == null || !SubscriptionManager.isValidPhoneId(phone.getPhoneId())) {
            loge("can't bindDataService with invalid phone or phoneId.");
        } else if (TextUtils.isEmpty(str)) {
            loge("Can't find the binding package");
        } else {
            String dataServiceClassName = getDataServiceClassName();
            if (TextUtils.isEmpty(dataServiceClassName)) {
                component = new Intent("android.telephony.data.DataService");
                component.setPackage(str);
            } else {
                component = new Intent("android.telephony.data.DataService").setComponent(new ComponentName(str, dataServiceClassName));
            }
            grantPermissionsToService(str);
            try {
                this.mServiceConnection = new CellularDataServiceConnection();
                if (!this.mPhone.getContext().bindService(component, this.mServiceConnection, 1)) {
                    loge("Cannot bind to the data service.");
                } else {
                    this.mTargetBindingPackageName = str;
                }
            } catch (Exception e) {
                loge("Cannot bind to the data service. Exception: " + e);
            }
        }
    }

    private void rebindDataService() {
        String dataServicePackageName = getDataServicePackageName();
        if (SubscriptionManager.isValidPhoneId(this.mPhone.getPhoneId()) && TextUtils.equals(dataServicePackageName, this.mTargetBindingPackageName)) {
            log("Service " + dataServicePackageName + " already bound or being bound.");
            return;
        }
        unbindDataService();
        bindDataService(dataServicePackageName);
    }

    private Set<String> getAllDataServicePackageNames() {
        List<ResolveInfo> queryIntentServices = this.mPhone.getContext().getPackageManager().queryIntentServices(new Intent("android.telephony.data.DataService"), 1048576);
        HashSet hashSet = new HashSet();
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                hashSet.add(serviceInfo.packageName);
            }
        }
        return hashSet;
    }

    public String getDataServicePackageName() {
        return getDataServicePackageName(this.mTransportType);
    }

    private String getDataServicePackageName(int i) {
        int i2;
        String str;
        if (i == 1) {
            i2 = 17040042;
            str = "carrier_data_service_wwan_package_override_string";
        } else if (i != 2) {
            throw new IllegalStateException("Transport type not WWAN or WLAN. type=" + AccessNetworkConstants.transportTypeToString(this.mTransportType));
        } else {
            i2 = 17040037;
            str = "carrier_data_service_wlan_package_override_string";
        }
        String string = this.mPhone.getContext().getResources().getString(i2);
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{str});
        return (carrierConfigSubset.isEmpty() || TextUtils.isEmpty(carrierConfigSubset.getString(str))) ? string : carrierConfigSubset.getString(str, string);
    }

    private String getDataServiceClassName() {
        return getDataServiceClassName(this.mTransportType);
    }

    private String getDataServiceClassName(int i) {
        int i2;
        String str;
        if (i == 1) {
            i2 = 17040041;
            str = "carrier_data_service_wwan_class_override_string";
        } else if (i != 2) {
            throw new IllegalStateException("Transport type not WWAN or WLAN. type=" + i);
        } else {
            i2 = 17040036;
            str = "carrier_data_service_wlan_class_override_string";
        }
        String string = this.mPhone.getContext().getResources().getString(i2);
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{str});
        return (carrierConfigSubset.isEmpty() || TextUtils.isEmpty(carrierConfigSubset.getString(str))) ? string : carrierConfigSubset.getString(str, string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCompleteMessage(Message message, int i) {
        if (message != null) {
            message.arg1 = i;
            message.sendToTarget();
        }
    }

    public void setupDataCall(int i, DataProfile dataProfile, boolean z, boolean z2, int i2, LinkProperties linkProperties, int i3, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, Message message) {
        log("setupDataCall");
        if (!this.mBound) {
            loge("setupDataCall: Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("setupDataCall");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            sendMessageDelayed(obtainMessage(2, dataServiceCallbackWrapper), 600000L);
            this.mIDataService.setupDataCall(this.mPhone.getPhoneId(), i, dataProfile, z, z2, i2, linkProperties, i3, networkSliceInfo, trafficDescriptor, z3, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("setupDataCall: Cannot invoke setupDataCall on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void deactivateDataCall(int i, int i2, Message message) {
        log("deactivateDataCall");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("deactivateDataCall");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            sendMessageDelayed(obtainMessage(2, dataServiceCallbackWrapper), 600000L);
            this.mIDataService.deactivateDataCall(this.mPhone.getPhoneId(), i, i2, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke deactivateDataCall on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void startHandover(int i, Message message) {
        log("startHandover");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("startHandover");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            sendMessageDelayed(obtainMessage(2, dataServiceCallbackWrapper), 600000L);
            this.mIDataService.startHandover(this.mPhone.getPhoneId(), i, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke startHandover on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void cancelHandover(int i, Message message) {
        log("cancelHandover");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("cancelHandover");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            sendMessageDelayed(obtainMessage(2, dataServiceCallbackWrapper), 600000L);
            this.mIDataService.cancelHandover(this.mPhone.getPhoneId(), i, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke cancelHandover on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void setInitialAttachApn(DataProfile dataProfile, boolean z, Message message) {
        log("setInitialAttachApn");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("setInitialAttachApn");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            this.mIDataService.setInitialAttachApn(this.mPhone.getPhoneId(), dataProfile, z, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke setInitialAttachApn on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void setDataProfile(List<DataProfile> list, boolean z, Message message) {
        log("setDataProfile");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("setDataProfile");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            this.mIDataService.setDataProfile(this.mPhone.getPhoneId(), list, z, dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke setDataProfile on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void requestDataCallList(Message message) {
        log("requestDataCallList");
        if (!this.mBound) {
            loge("Data service not bound.");
            sendCompleteMessage(message, 4);
            return;
        }
        DataServiceCallbackWrapper dataServiceCallbackWrapper = new DataServiceCallbackWrapper("requestDataCallList");
        if (message != null) {
            this.mMessageMap.put(dataServiceCallbackWrapper.asBinder(), message);
        }
        try {
            this.mIDataService.requestDataCallList(this.mPhone.getPhoneId(), dataServiceCallbackWrapper);
        } catch (RemoteException unused) {
            loge("Cannot invoke requestDataCallList on data service.");
            this.mMessageMap.remove(dataServiceCallbackWrapper.asBinder());
            sendCompleteMessage(message, 4);
        }
    }

    public void registerForDataCallListChanged(Handler handler, int i) {
        if (handler != null) {
            this.mDataCallListChangedRegistrants.addUnique(handler, i, Integer.valueOf(this.mTransportType));
        }
    }

    public void unregisterForDataCallListChanged(Handler handler) {
        if (handler != null) {
            this.mDataCallListChangedRegistrants.remove(handler);
        }
    }

    public void registerForApnUnthrottled(Handler handler, int i) {
        if (handler != null) {
            this.mApnUnthrottledRegistrants.addUnique(handler, i, Integer.valueOf(this.mTransportType));
        }
    }

    public void unregisterForApnUnthrottled(Handler handler) {
        if (handler != null) {
            this.mApnUnthrottledRegistrants.remove(handler);
        }
    }

    public void registerForServiceBindingChanged(Handler handler, int i) {
        if (handler != null) {
            this.mServiceBindingChangedRegistrants.remove(handler);
            Registrant registrant = new Registrant(handler, i, Integer.valueOf(this.mTransportType));
            this.mServiceBindingChangedRegistrants.add(registrant);
            if (this.mBound) {
                registrant.notifyResult(Boolean.TRUE);
            }
        }
    }

    public void unregisterForServiceBindingChanged(Handler handler) {
        if (handler != null) {
            this.mServiceBindingChangedRegistrants.remove(handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e(this.mTag, str);
    }
}
