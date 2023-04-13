package android.telephony.data;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.net.LinkProperties;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.telephony.data.IDataService;
import android.util.Log;
import android.util.SparseArray;
import com.android.telephony.Rlog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public abstract class DataService extends Service {
    private static final int DATA_SERVICE_CREATE_DATA_SERVICE_PROVIDER = 1;
    private static final int DATA_SERVICE_INDICATION_APN_UNTHROTTLED = 16;
    private static final int DATA_SERVICE_INDICATION_DATA_CALL_LIST_CHANGED = 11;
    private static final int DATA_SERVICE_REMOVE_ALL_DATA_SERVICE_PROVIDERS = 3;
    private static final int DATA_SERVICE_REMOVE_DATA_SERVICE_PROVIDER = 2;
    private static final int DATA_SERVICE_REQUEST_CANCEL_HANDOVER = 13;
    private static final int DATA_SERVICE_REQUEST_DEACTIVATE_DATA_CALL = 5;
    private static final int DATA_SERVICE_REQUEST_REGISTER_APN_UNTHROTTLED = 14;
    private static final int DATA_SERVICE_REQUEST_REGISTER_DATA_CALL_LIST_CHANGED = 9;
    private static final int DATA_SERVICE_REQUEST_REQUEST_DATA_CALL_LIST = 8;
    private static final int DATA_SERVICE_REQUEST_SETUP_DATA_CALL = 4;
    private static final int DATA_SERVICE_REQUEST_SET_DATA_PROFILE = 7;
    private static final int DATA_SERVICE_REQUEST_SET_INITIAL_ATTACH_APN = 6;
    private static final int DATA_SERVICE_REQUEST_START_HANDOVER = 12;
    private static final int DATA_SERVICE_REQUEST_UNREGISTER_APN_UNTHROTTLED = 15;
    private static final int DATA_SERVICE_REQUEST_UNREGISTER_DATA_CALL_LIST_CHANGED = 10;
    public static final int REQUEST_REASON_HANDOVER = 3;
    public static final int REQUEST_REASON_NORMAL = 1;
    public static final int REQUEST_REASON_SHUTDOWN = 2;
    public static final int REQUEST_REASON_UNKNOWN = 0;
    public static final String SERVICE_INTERFACE = "android.telephony.data.DataService";
    private static final String TAG = DataService.class.getSimpleName();
    private final DataServiceHandler mHandler;
    private final HandlerThread mHandlerThread;
    private final SparseArray<DataServiceProvider> mServiceMap = new SparseArray<>();
    public final IDataServiceWrapper mBinder = new IDataServiceWrapper();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeactivateDataReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SetupDataReason {
    }

    public abstract DataServiceProvider onCreateDataServiceProvider(int i);

    /* loaded from: classes3.dex */
    public abstract class DataServiceProvider implements AutoCloseable {
        private final int mSlotIndex;
        private final List<IDataServiceCallback> mDataCallListChangedCallbacks = new ArrayList();
        private final List<IDataServiceCallback> mApnUnthrottledCallbacks = new ArrayList();

        @Override // java.lang.AutoCloseable
        public abstract void close();

        public DataServiceProvider(int slotIndex) {
            this.mSlotIndex = slotIndex;
        }

        public final int getSlotIndex() {
            return this.mSlotIndex;
        }

        public void setupDataCall(int accessNetworkType, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, DataServiceCallback callback) {
            if (callback != null) {
                callback.onSetupDataCallComplete(1, null);
            }
        }

        public void setupDataCall(int accessNetworkType, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, int pduSessionId, NetworkSliceInfo sliceInfo, TrafficDescriptor trafficDescriptor, boolean matchAllRuleAllowed, DataServiceCallback callback) {
            setupDataCall(accessNetworkType, dataProfile, isRoaming, allowRoaming, reason, linkProperties, callback);
        }

        public void deactivateDataCall(int cid, int reason, DataServiceCallback callback) {
            if (callback != null) {
                callback.onDeactivateDataCallComplete(1);
            }
        }

        public void setInitialAttachApn(DataProfile dataProfile, boolean isRoaming, DataServiceCallback callback) {
            if (callback != null) {
                callback.onSetInitialAttachApnComplete(1);
            }
        }

        public void setDataProfile(List<DataProfile> dps, boolean isRoaming, DataServiceCallback callback) {
            if (callback != null) {
                callback.onSetDataProfileComplete(1);
            }
        }

        public void startHandover(int cid, DataServiceCallback callback) {
            Objects.requireNonNull(callback, "callback cannot be null");
            Log.m112d(DataService.TAG, "startHandover: " + cid);
            callback.onHandoverStarted(1);
        }

        public void cancelHandover(int cid, DataServiceCallback callback) {
            Objects.requireNonNull(callback, "callback cannot be null");
            Log.m112d(DataService.TAG, "cancelHandover: " + cid);
            callback.onHandoverCancelled(1);
        }

        public void requestDataCallList(DataServiceCallback callback) {
            callback.onRequestDataCallListComplete(1, Collections.EMPTY_LIST);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void registerForDataCallListChanged(IDataServiceCallback callback) {
            synchronized (this.mDataCallListChangedCallbacks) {
                this.mDataCallListChangedCallbacks.add(callback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregisterForDataCallListChanged(IDataServiceCallback callback) {
            synchronized (this.mDataCallListChangedCallbacks) {
                this.mDataCallListChangedCallbacks.remove(callback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void registerForApnUnthrottled(IDataServiceCallback callback) {
            synchronized (this.mApnUnthrottledCallbacks) {
                this.mApnUnthrottledCallbacks.add(callback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregisterForApnUnthrottled(IDataServiceCallback callback) {
            synchronized (this.mApnUnthrottledCallbacks) {
                this.mApnUnthrottledCallbacks.remove(callback);
            }
        }

        public final void notifyDataCallListChanged(List<DataCallResponse> dataCallList) {
            synchronized (this.mDataCallListChangedCallbacks) {
                for (IDataServiceCallback callback : this.mDataCallListChangedCallbacks) {
                    DataService.this.mHandler.obtainMessage(11, this.mSlotIndex, 0, new DataCallListChangedIndication(dataCallList, callback)).sendToTarget();
                }
            }
        }

        public final void notifyApnUnthrottled(String apn) {
            synchronized (this.mApnUnthrottledCallbacks) {
                for (IDataServiceCallback callback : this.mApnUnthrottledCallbacks) {
                    DataService.this.mHandler.obtainMessage(16, this.mSlotIndex, 0, new ApnUnthrottledIndication(apn, callback)).sendToTarget();
                }
            }
        }

        public final void notifyDataProfileUnthrottled(DataProfile dataProfile) {
            synchronized (this.mApnUnthrottledCallbacks) {
                for (IDataServiceCallback callback : this.mApnUnthrottledCallbacks) {
                    DataService.this.mHandler.obtainMessage(16, this.mSlotIndex, 0, new ApnUnthrottledIndication(dataProfile, callback)).sendToTarget();
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    private static final class SetupDataCallRequest {
        public final int accessNetworkType;
        public final boolean allowRoaming;
        public final IDataServiceCallback callback;
        public final DataProfile dataProfile;
        public final boolean isRoaming;
        public final LinkProperties linkProperties;
        public final boolean matchAllRuleAllowed;
        public final int pduSessionId;
        public final int reason;
        public final NetworkSliceInfo sliceInfo;
        public final TrafficDescriptor trafficDescriptor;

        SetupDataCallRequest(int accessNetworkType, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, int pduSessionId, NetworkSliceInfo sliceInfo, TrafficDescriptor trafficDescriptor, boolean matchAllRuleAllowed, IDataServiceCallback callback) {
            this.accessNetworkType = accessNetworkType;
            this.dataProfile = dataProfile;
            this.isRoaming = isRoaming;
            this.allowRoaming = allowRoaming;
            this.linkProperties = linkProperties;
            this.reason = reason;
            this.pduSessionId = pduSessionId;
            this.sliceInfo = sliceInfo;
            this.trafficDescriptor = trafficDescriptor;
            this.matchAllRuleAllowed = matchAllRuleAllowed;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class DeactivateDataCallRequest {
        public final IDataServiceCallback callback;
        public final int cid;
        public final int reason;

        DeactivateDataCallRequest(int cid, int reason, IDataServiceCallback callback) {
            this.cid = cid;
            this.reason = reason;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class SetInitialAttachApnRequest {
        public final IDataServiceCallback callback;
        public final DataProfile dataProfile;
        public final boolean isRoaming;

        SetInitialAttachApnRequest(DataProfile dataProfile, boolean isRoaming, IDataServiceCallback callback) {
            this.dataProfile = dataProfile;
            this.isRoaming = isRoaming;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class SetDataProfileRequest {
        public final IDataServiceCallback callback;
        public final List<DataProfile> dps;
        public final boolean isRoaming;

        SetDataProfileRequest(List<DataProfile> dps, boolean isRoaming, IDataServiceCallback callback) {
            this.dps = dps;
            this.isRoaming = isRoaming;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class BeginCancelHandoverRequest {
        public final IDataServiceCallback callback;
        public final int cid;

        BeginCancelHandoverRequest(int cid, IDataServiceCallback callback) {
            this.cid = cid;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class DataCallListChangedIndication {
        public final IDataServiceCallback callback;
        public final List<DataCallResponse> dataCallList;

        DataCallListChangedIndication(List<DataCallResponse> dataCallList, IDataServiceCallback callback) {
            this.dataCallList = dataCallList;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static final class ApnUnthrottledIndication {
        public final String apn;
        public final IDataServiceCallback callback;
        public final DataProfile dataProfile;

        ApnUnthrottledIndication(String apn, IDataServiceCallback callback) {
            this.dataProfile = null;
            this.apn = apn;
            this.callback = callback;
        }

        ApnUnthrottledIndication(DataProfile dataProfile, IDataServiceCallback callback) {
            this.dataProfile = dataProfile;
            this.apn = null;
            this.callback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private class DataServiceHandler extends Handler {
        DataServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message message) {
            DataServiceProvider serviceProvider;
            DataServiceCallback dataServiceCallback;
            DataServiceCallback dataServiceCallback2;
            DataServiceCallback dataServiceCallback3;
            DataServiceCallback dataServiceCallback4;
            int slotIndex = message.arg1;
            DataServiceProvider serviceProvider2 = (DataServiceProvider) DataService.this.mServiceMap.get(slotIndex);
            switch (message.what) {
                case 1:
                    DataServiceProvider serviceProvider3 = DataService.this.onCreateDataServiceProvider(message.arg1);
                    if (serviceProvider3 != null) {
                        DataService.this.mServiceMap.put(slotIndex, serviceProvider3);
                        return;
                    }
                    return;
                case 2:
                    serviceProvider = serviceProvider2;
                    if (serviceProvider != null) {
                        serviceProvider.close();
                        DataService.this.mServiceMap.remove(slotIndex);
                        break;
                    } else {
                        break;
                    }
                case 3:
                    for (int i = 0; i < DataService.this.mServiceMap.size(); i++) {
                        DataServiceProvider serviceProvider4 = (DataServiceProvider) DataService.this.mServiceMap.get(i);
                        if (serviceProvider4 != null) {
                            serviceProvider4.close();
                        }
                    }
                    DataService.this.mServiceMap.clear();
                    return;
                case 4:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        SetupDataCallRequest setupDataCallRequest = (SetupDataCallRequest) message.obj;
                        int i2 = setupDataCallRequest.accessNetworkType;
                        DataProfile dataProfile = setupDataCallRequest.dataProfile;
                        boolean z = setupDataCallRequest.isRoaming;
                        boolean z2 = setupDataCallRequest.allowRoaming;
                        int i3 = setupDataCallRequest.reason;
                        LinkProperties linkProperties = setupDataCallRequest.linkProperties;
                        int i4 = setupDataCallRequest.pduSessionId;
                        NetworkSliceInfo networkSliceInfo = setupDataCallRequest.sliceInfo;
                        TrafficDescriptor trafficDescriptor = setupDataCallRequest.trafficDescriptor;
                        boolean z3 = setupDataCallRequest.matchAllRuleAllowed;
                        if (setupDataCallRequest.callback != null) {
                            dataServiceCallback = new DataServiceCallback(setupDataCallRequest.callback);
                        } else {
                            dataServiceCallback = null;
                        }
                        serviceProvider = serviceProvider2;
                        serviceProvider2.setupDataCall(i2, dataProfile, z, z2, i3, linkProperties, i4, networkSliceInfo, trafficDescriptor, z3, dataServiceCallback);
                        break;
                    }
                case 5:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        DeactivateDataCallRequest deactivateDataCallRequest = (DeactivateDataCallRequest) message.obj;
                        int i5 = deactivateDataCallRequest.cid;
                        int i6 = deactivateDataCallRequest.reason;
                        if (deactivateDataCallRequest.callback != null) {
                            dataServiceCallback2 = new DataServiceCallback(deactivateDataCallRequest.callback);
                        } else {
                            dataServiceCallback2 = null;
                        }
                        serviceProvider2.deactivateDataCall(i5, i6, dataServiceCallback2);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 6:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        SetInitialAttachApnRequest setInitialAttachApnRequest = (SetInitialAttachApnRequest) message.obj;
                        DataProfile dataProfile2 = setInitialAttachApnRequest.dataProfile;
                        boolean z4 = setInitialAttachApnRequest.isRoaming;
                        if (setInitialAttachApnRequest.callback != null) {
                            dataServiceCallback3 = new DataServiceCallback(setInitialAttachApnRequest.callback);
                        } else {
                            dataServiceCallback3 = null;
                        }
                        serviceProvider2.setInitialAttachApn(dataProfile2, z4, dataServiceCallback3);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 7:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        SetDataProfileRequest setDataProfileRequest = (SetDataProfileRequest) message.obj;
                        List<DataProfile> list = setDataProfileRequest.dps;
                        boolean z5 = setDataProfileRequest.isRoaming;
                        if (setDataProfileRequest.callback != null) {
                            dataServiceCallback4 = new DataServiceCallback(setDataProfileRequest.callback);
                        } else {
                            dataServiceCallback4 = null;
                        }
                        serviceProvider2.setDataProfile(list, z5, dataServiceCallback4);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 8:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        serviceProvider2.requestDataCallList(new DataServiceCallback((IDataServiceCallback) message.obj));
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 9:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        serviceProvider2.registerForDataCallListChanged((IDataServiceCallback) message.obj);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 10:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        IDataServiceCallback callback = (IDataServiceCallback) message.obj;
                        serviceProvider2.unregisterForDataCallListChanged(callback);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 11:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        DataCallListChangedIndication indication = (DataCallListChangedIndication) message.obj;
                        try {
                            indication.callback.onDataCallListChanged(indication.dataCallList);
                            serviceProvider = serviceProvider2;
                            break;
                        } catch (RemoteException e) {
                            DataService.this.loge("Failed to call onDataCallListChanged. " + e);
                            serviceProvider = serviceProvider2;
                            break;
                        }
                    }
                case 12:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        BeginCancelHandoverRequest bReq = (BeginCancelHandoverRequest) message.obj;
                        serviceProvider2.startHandover(bReq.cid, bReq.callback != null ? new DataServiceCallback(bReq.callback) : null);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 13:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        BeginCancelHandoverRequest cReq = (BeginCancelHandoverRequest) message.obj;
                        serviceProvider2.cancelHandover(cReq.cid, cReq.callback != null ? new DataServiceCallback(cReq.callback) : null);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 14:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        serviceProvider2.registerForApnUnthrottled((IDataServiceCallback) message.obj);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 15:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        IDataServiceCallback callback2 = (IDataServiceCallback) message.obj;
                        serviceProvider2.unregisterForApnUnthrottled(callback2);
                        serviceProvider = serviceProvider2;
                        break;
                    }
                case 16:
                    if (serviceProvider2 == null) {
                        serviceProvider = serviceProvider2;
                        break;
                    } else {
                        ApnUnthrottledIndication apnUnthrottledIndication = (ApnUnthrottledIndication) message.obj;
                        try {
                            if (apnUnthrottledIndication.dataProfile != null) {
                                apnUnthrottledIndication.callback.onDataProfileUnthrottled(apnUnthrottledIndication.dataProfile);
                            } else {
                                apnUnthrottledIndication.callback.onApnUnthrottled(apnUnthrottledIndication.apn);
                            }
                            serviceProvider = serviceProvider2;
                            break;
                        } catch (RemoteException e2) {
                            DataService.this.loge("Failed to call onApnUnthrottled. " + e2);
                            serviceProvider = serviceProvider2;
                            break;
                        }
                    }
                default:
                    serviceProvider = serviceProvider2;
                    break;
            }
        }
    }

    public DataService() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new DataServiceHandler(handlerThread.getLooper());
        log("Data service created");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (intent == null || !SERVICE_INTERFACE.equals(intent.getAction())) {
            loge("Unexpected intent " + intent);
            return null;
        }
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.mHandler.obtainMessage(3).sendToTarget();
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandlerThread.quitSafely();
        super.onDestroy();
    }

    /* loaded from: classes3.dex */
    private class IDataServiceWrapper extends IDataService.Stub {
        private IDataServiceWrapper() {
        }

        @Override // android.telephony.data.IDataService
        public void createDataServiceProvider(int slotIndex) {
            DataService.this.mHandler.obtainMessage(1, slotIndex, 0).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void removeDataServiceProvider(int slotIndex) {
            DataService.this.mHandler.obtainMessage(2, slotIndex, 0).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void setupDataCall(int slotIndex, int accessNetworkType, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, int pduSessionId, NetworkSliceInfo sliceInfo, TrafficDescriptor trafficDescriptor, boolean matchAllRuleAllowed, IDataServiceCallback callback) {
            DataService.this.mHandler.obtainMessage(4, slotIndex, 0, new SetupDataCallRequest(accessNetworkType, dataProfile, isRoaming, allowRoaming, reason, linkProperties, pduSessionId, sliceInfo, trafficDescriptor, matchAllRuleAllowed, callback)).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void deactivateDataCall(int slotIndex, int cid, int reason, IDataServiceCallback callback) {
            DataService.this.mHandler.obtainMessage(5, slotIndex, 0, new DeactivateDataCallRequest(cid, reason, callback)).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void setInitialAttachApn(int slotIndex, DataProfile dataProfile, boolean isRoaming, IDataServiceCallback callback) {
            DataService.this.mHandler.obtainMessage(6, slotIndex, 0, new SetInitialAttachApnRequest(dataProfile, isRoaming, callback)).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void setDataProfile(int slotIndex, List<DataProfile> dps, boolean isRoaming, IDataServiceCallback callback) {
            DataService.this.mHandler.obtainMessage(7, slotIndex, 0, new SetDataProfileRequest(dps, isRoaming, callback)).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void requestDataCallList(int slotIndex, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("requestDataCallList: callback is null");
            } else {
                DataService.this.mHandler.obtainMessage(8, slotIndex, 0, callback).sendToTarget();
            }
        }

        @Override // android.telephony.data.IDataService
        public void registerForDataCallListChanged(int slotIndex, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("registerForDataCallListChanged: callback is null");
            } else {
                DataService.this.mHandler.obtainMessage(9, slotIndex, 0, callback).sendToTarget();
            }
        }

        @Override // android.telephony.data.IDataService
        public void unregisterForDataCallListChanged(int slotIndex, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("unregisterForDataCallListChanged: callback is null");
            } else {
                DataService.this.mHandler.obtainMessage(10, slotIndex, 0, callback).sendToTarget();
            }
        }

        @Override // android.telephony.data.IDataService
        public void startHandover(int slotIndex, int cid, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("startHandover: callback is null");
                return;
            }
            BeginCancelHandoverRequest req = new BeginCancelHandoverRequest(cid, callback);
            DataService.this.mHandler.obtainMessage(12, slotIndex, 0, req).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void cancelHandover(int slotIndex, int cid, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("cancelHandover: callback is null");
                return;
            }
            BeginCancelHandoverRequest req = new BeginCancelHandoverRequest(cid, callback);
            DataService.this.mHandler.obtainMessage(13, slotIndex, 0, req).sendToTarget();
        }

        @Override // android.telephony.data.IDataService
        public void registerForUnthrottleApn(int slotIndex, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("registerForUnthrottleApn: callback is null");
            } else {
                DataService.this.mHandler.obtainMessage(14, slotIndex, 0, callback).sendToTarget();
            }
        }

        @Override // android.telephony.data.IDataService
        public void unregisterForUnthrottleApn(int slotIndex, IDataServiceCallback callback) {
            if (callback == null) {
                DataService.this.loge("uregisterForUnthrottleApn: callback is null");
            } else {
                DataService.this.mHandler.obtainMessage(15, slotIndex, 0, callback).sendToTarget();
            }
        }
    }

    private void log(String s) {
        Rlog.m10d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s) {
        Rlog.m8e(TAG, s);
    }
}
