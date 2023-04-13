package android.telephony;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.telephony.INetworkService;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public abstract class NetworkService extends Service {
    private static final int NETWORK_SERVICE_CREATE_NETWORK_SERVICE_PROVIDER = 1;
    private static final int NETWORK_SERVICE_GET_REGISTRATION_INFO = 4;
    private static final int NETWORK_SERVICE_INDICATION_NETWORK_INFO_CHANGED = 7;
    private static final int NETWORK_SERVICE_REGISTER_FOR_INFO_CHANGE = 5;
    private static final int NETWORK_SERVICE_REMOVE_ALL_NETWORK_SERVICE_PROVIDERS = 3;
    private static final int NETWORK_SERVICE_REMOVE_NETWORK_SERVICE_PROVIDER = 2;
    private static final int NETWORK_SERVICE_UNREGISTER_FOR_INFO_CHANGE = 6;
    public static final String SERVICE_INTERFACE = "android.telephony.NetworkService";
    private final String TAG;
    public final INetworkServiceWrapper mBinder;
    private final NetworkServiceHandler mHandler;
    private final HandlerThread mHandlerThread;
    private final SparseArray<NetworkServiceProvider> mServiceMap;

    public abstract NetworkServiceProvider onCreateNetworkServiceProvider(int i);

    /* loaded from: classes3.dex */
    public abstract class NetworkServiceProvider implements AutoCloseable {
        private final List<INetworkServiceCallback> mNetworkRegistrationInfoChangedCallbacks = new ArrayList();
        private final int mSlotIndex;

        @Override // java.lang.AutoCloseable
        public abstract void close();

        public NetworkServiceProvider(int slotIndex) {
            this.mSlotIndex = slotIndex;
        }

        public final int getSlotIndex() {
            return this.mSlotIndex;
        }

        public void requestNetworkRegistrationInfo(int domain, NetworkServiceCallback callback) {
            callback.onRequestNetworkRegistrationInfoComplete(1, null);
        }

        public final void notifyNetworkRegistrationInfoChanged() {
            NetworkService.this.mHandler.obtainMessage(7, this.mSlotIndex, 0, null).sendToTarget();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void registerForInfoChanged(INetworkServiceCallback callback) {
            synchronized (this.mNetworkRegistrationInfoChangedCallbacks) {
                this.mNetworkRegistrationInfoChangedCallbacks.add(callback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregisterForInfoChanged(INetworkServiceCallback callback) {
            synchronized (this.mNetworkRegistrationInfoChangedCallbacks) {
                this.mNetworkRegistrationInfoChangedCallbacks.remove(callback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyInfoChangedToCallbacks() {
            for (INetworkServiceCallback callback : this.mNetworkRegistrationInfoChangedCallbacks) {
                try {
                    callback.onNetworkStateChanged();
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    private class NetworkServiceHandler extends Handler {
        NetworkServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message message) {
            int slotIndex = message.arg1;
            INetworkServiceCallback callback = (INetworkServiceCallback) message.obj;
            NetworkServiceProvider serviceProvider = (NetworkServiceProvider) NetworkService.this.mServiceMap.get(slotIndex);
            switch (message.what) {
                case 1:
                    if (serviceProvider == null) {
                        NetworkService.this.mServiceMap.put(slotIndex, NetworkService.this.onCreateNetworkServiceProvider(slotIndex));
                        return;
                    }
                    return;
                case 2:
                    if (serviceProvider != null) {
                        serviceProvider.close();
                        NetworkService.this.mServiceMap.remove(slotIndex);
                        return;
                    }
                    return;
                case 3:
                    for (int i = 0; i < NetworkService.this.mServiceMap.size(); i++) {
                        NetworkServiceProvider serviceProvider2 = (NetworkServiceProvider) NetworkService.this.mServiceMap.get(i);
                        if (serviceProvider2 != null) {
                            serviceProvider2.close();
                        }
                    }
                    NetworkService.this.mServiceMap.clear();
                    return;
                case 4:
                    if (serviceProvider != null) {
                        int domainId = message.arg2;
                        serviceProvider.requestNetworkRegistrationInfo(domainId, new NetworkServiceCallback(callback));
                        return;
                    }
                    return;
                case 5:
                    if (serviceProvider != null) {
                        serviceProvider.registerForInfoChanged(callback);
                        return;
                    }
                    return;
                case 6:
                    if (serviceProvider != null) {
                        serviceProvider.unregisterForInfoChanged(callback);
                        return;
                    }
                    return;
                case 7:
                    if (serviceProvider != null) {
                        serviceProvider.notifyInfoChangedToCallbacks();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public NetworkService() {
        String simpleName = NetworkService.class.getSimpleName();
        this.TAG = simpleName;
        this.mServiceMap = new SparseArray<>();
        this.mBinder = new INetworkServiceWrapper();
        HandlerThread handlerThread = new HandlerThread(simpleName);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new NetworkServiceHandler(handlerThread.getLooper());
        log("network service created");
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
        this.mHandler.obtainMessage(3, 0, 0, null).sendToTarget();
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandlerThread.quitSafely();
        super.onDestroy();
    }

    /* loaded from: classes3.dex */
    private class INetworkServiceWrapper extends INetworkService.Stub {
        private INetworkServiceWrapper() {
        }

        @Override // android.telephony.INetworkService
        public void createNetworkServiceProvider(int slotIndex) {
            NetworkService.this.mHandler.obtainMessage(1, slotIndex, 0, null).sendToTarget();
        }

        @Override // android.telephony.INetworkService
        public void removeNetworkServiceProvider(int slotIndex) {
            NetworkService.this.mHandler.obtainMessage(2, slotIndex, 0, null).sendToTarget();
        }

        @Override // android.telephony.INetworkService
        public void requestNetworkRegistrationInfo(int slotIndex, int domain, INetworkServiceCallback callback) {
            NetworkService.this.mHandler.obtainMessage(4, slotIndex, domain, callback).sendToTarget();
        }

        @Override // android.telephony.INetworkService
        public void registerForNetworkRegistrationInfoChanged(int slotIndex, INetworkServiceCallback callback) {
            NetworkService.this.mHandler.obtainMessage(5, slotIndex, 0, callback).sendToTarget();
        }

        @Override // android.telephony.INetworkService
        public void unregisterForNetworkRegistrationInfoChanged(int slotIndex, INetworkServiceCallback callback) {
            NetworkService.this.mHandler.obtainMessage(6, slotIndex, 0, callback).sendToTarget();
        }
    }

    private final void log(String s) {
        com.android.telephony.Rlog.m10d(this.TAG, s);
    }

    private final void loge(String s) {
        com.android.telephony.Rlog.m8e(this.TAG, s);
    }
}
