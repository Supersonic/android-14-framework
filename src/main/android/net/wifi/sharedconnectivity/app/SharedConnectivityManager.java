package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.wifi.sharedconnectivity.app.SharedConnectivityManager;
import android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback;
import android.net.wifi.sharedconnectivity.service.ISharedConnectivityService;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.C4057R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes2.dex */
public class SharedConnectivityManager {
    private static final boolean DEBUG = true;
    private static final String TAG = SharedConnectivityManager.class.getSimpleName();
    private ISharedConnectivityService mService;
    private final ServiceConnection mServiceConnection;
    private final Map<SharedConnectivityClientCallback, SharedConnectivityCallbackProxy> mProxyMap = new HashMap();
    private final Map<SharedConnectivityClientCallback, SharedConnectivityCallbackProxy> mCallbackProxyCache = new HashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class SharedConnectivityCallbackProxy extends ISharedConnectivityCallback.Stub {
        private final SharedConnectivityClientCallback mCallback;
        private final Executor mExecutor;

        SharedConnectivityCallbackProxy(Executor executor, SharedConnectivityClientCallback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback
        public void onHotspotNetworksUpdated(final List<HotspotNetwork> networks) {
            if (this.mCallback != null) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$SharedConnectivityCallbackProxy$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedConnectivityManager.SharedConnectivityCallbackProxy.this.lambda$onHotspotNetworksUpdated$0(networks);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onHotspotNetworksUpdated$0(List networks) {
            this.mCallback.onHotspotNetworksUpdated(networks);
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback
        public void onKnownNetworksUpdated(final List<KnownNetwork> networks) {
            if (this.mCallback != null) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$SharedConnectivityCallbackProxy$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedConnectivityManager.SharedConnectivityCallbackProxy.this.lambda$onKnownNetworksUpdated$1(networks);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onKnownNetworksUpdated$1(List networks) {
            this.mCallback.onKnownNetworksUpdated(networks);
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback
        public void onSharedConnectivitySettingsChanged(final SharedConnectivitySettingsState state) {
            if (this.mCallback != null) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$SharedConnectivityCallbackProxy$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedConnectivityManager.SharedConnectivityCallbackProxy.this.lambda$onSharedConnectivitySettingsChanged$2(state);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSharedConnectivitySettingsChanged$2(SharedConnectivitySettingsState state) {
            this.mCallback.onSharedConnectivitySettingsChanged(state);
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback
        public void onHotspotNetworkConnectionStatusChanged(final HotspotNetworkConnectionStatus status) {
            if (this.mCallback != null) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$SharedConnectivityCallbackProxy$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedConnectivityManager.SharedConnectivityCallbackProxy.this.lambda$onHotspotNetworkConnectionStatusChanged$3(status);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onHotspotNetworkConnectionStatusChanged$3(HotspotNetworkConnectionStatus status) {
            this.mCallback.onHotspotNetworkConnectionStatusChanged(status);
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback
        public void onKnownNetworkConnectionStatusChanged(final KnownNetworkConnectionStatus status) {
            if (this.mCallback != null) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$SharedConnectivityCallbackProxy$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedConnectivityManager.SharedConnectivityCallbackProxy.this.lambda$onKnownNetworkConnectionStatusChanged$4(status);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onKnownNetworkConnectionStatusChanged$4(KnownNetworkConnectionStatus status) {
            this.mCallback.onKnownNetworkConnectionStatusChanged(status);
        }
    }

    public static SharedConnectivityManager create(Context context) {
        Resources resources = context.getResources();
        try {
            String servicePackageName = resources.getString(C4057R.string.shared_connectivity_service_package);
            String serviceIntentAction = resources.getString(C4057R.string.shared_connectivity_service_intent_action);
            return new SharedConnectivityManager(context, servicePackageName, serviceIntentAction);
        } catch (Resources.NotFoundException e) {
            Log.m110e(TAG, "To support shared connectivity service on this device, the service's package name and intent action string must be defined");
            return null;
        }
    }

    public static SharedConnectivityManager create(Context context, String servicePackageName, String serviceIntentAction) {
        return new SharedConnectivityManager(context, servicePackageName, serviceIntentAction);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$1 */
    /* loaded from: classes2.dex */
    public class ServiceConnectionC21301 implements ServiceConnection {
        ServiceConnectionC21301() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            SharedConnectivityManager.this.mService = ISharedConnectivityService.Stub.asInterface(service);
            if (!SharedConnectivityManager.this.mCallbackProxyCache.isEmpty()) {
                synchronized (SharedConnectivityManager.this.mCallbackProxyCache) {
                    SharedConnectivityManager.this.mCallbackProxyCache.keySet().forEach(new Consumer() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$1$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            SharedConnectivityManager.ServiceConnectionC21301.this.lambda$onServiceConnected$0((SharedConnectivityClientCallback) obj);
                        }
                    });
                    SharedConnectivityManager.this.mCallbackProxyCache.clear();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onServiceConnected$0(SharedConnectivityClientCallback callback) {
            SharedConnectivityManager sharedConnectivityManager = SharedConnectivityManager.this;
            sharedConnectivityManager.registerCallbackInternal(callback, (SharedConnectivityCallbackProxy) sharedConnectivityManager.mCallbackProxyCache.get(callback));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.m108i(SharedConnectivityManager.TAG, "onServiceDisconnected");
            SharedConnectivityManager.this.mService = null;
            if (!SharedConnectivityManager.this.mCallbackProxyCache.isEmpty()) {
                synchronized (SharedConnectivityManager.this.mCallbackProxyCache) {
                    SharedConnectivityManager.this.mCallbackProxyCache.keySet().forEach(new Consumer() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$1$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((SharedConnectivityClientCallback) obj).onServiceDisconnected();
                        }
                    });
                    SharedConnectivityManager.this.mCallbackProxyCache.clear();
                }
            }
            if (!SharedConnectivityManager.this.mProxyMap.isEmpty()) {
                synchronized (SharedConnectivityManager.this.mProxyMap) {
                    SharedConnectivityManager.this.mProxyMap.keySet().forEach(new Consumer() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivityManager$1$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((SharedConnectivityClientCallback) obj).onServiceDisconnected();
                        }
                    });
                    SharedConnectivityManager.this.mProxyMap.clear();
                }
            }
        }
    }

    private SharedConnectivityManager(Context context, String servicePackageName, String serviceIntentAction) {
        ServiceConnectionC21301 serviceConnectionC21301 = new ServiceConnectionC21301();
        this.mServiceConnection = serviceConnectionC21301;
        context.bindService(new Intent().setPackage(servicePackageName).setAction(serviceIntentAction), serviceConnectionC21301, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerCallbackInternal(SharedConnectivityClientCallback callback, SharedConnectivityCallbackProxy proxy) {
        try {
            this.mService.registerCallback(proxy);
            synchronized (this.mProxyMap) {
                this.mProxyMap.put(callback, proxy);
            }
            callback.onServiceConnected();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in registerCallback", e);
            callback.onRegisterCallbackFailed(e);
        }
    }

    public void setService(IInterface service) {
        this.mService = (ISharedConnectivityService) service;
    }

    public ServiceConnection getServiceConnection() {
        return this.mServiceConnection;
    }

    public void registerCallback(Executor executor, SharedConnectivityClientCallback callback) {
        Objects.requireNonNull(executor, "executor cannot be null");
        Objects.requireNonNull(callback, "callback cannot be null");
        if (this.mProxyMap.containsKey(callback) || this.mCallbackProxyCache.containsKey(callback)) {
            Log.m110e(TAG, "Callback already registered");
            callback.onRegisterCallbackFailed(new IllegalStateException("Callback already registered"));
            return;
        }
        SharedConnectivityCallbackProxy proxy = new SharedConnectivityCallbackProxy(executor, callback);
        if (this.mService == null) {
            synchronized (this.mCallbackProxyCache) {
                this.mCallbackProxyCache.put(callback, proxy);
            }
            return;
        }
        registerCallbackInternal(callback, proxy);
    }

    public boolean unregisterCallback(SharedConnectivityClientCallback callback) {
        Objects.requireNonNull(callback, "callback cannot be null");
        if (!this.mProxyMap.containsKey(callback) && !this.mCallbackProxyCache.containsKey(callback)) {
            Log.m110e(TAG, "Callback not found, cannot unregister");
            return false;
        }
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            synchronized (this.mCallbackProxyCache) {
                this.mCallbackProxyCache.remove(callback);
            }
            return true;
        }
        try {
            iSharedConnectivityService.unregisterCallback(this.mProxyMap.get(callback));
            synchronized (this.mProxyMap) {
                this.mProxyMap.remove(callback);
            }
            return true;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in unregisterCallback", e);
            return false;
        }
    }

    public boolean connectHotspotNetwork(HotspotNetwork network) {
        Objects.requireNonNull(network, "Hotspot network cannot be null");
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return false;
        }
        try {
            iSharedConnectivityService.connectHotspotNetwork(network);
            return true;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in connectHotspotNetwork", e);
            return false;
        }
    }

    public boolean disconnectHotspotNetwork(HotspotNetwork network) {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return false;
        }
        try {
            iSharedConnectivityService.disconnectHotspotNetwork(network);
            return true;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in disconnectHotspotNetwork", e);
            return false;
        }
    }

    public boolean connectKnownNetwork(KnownNetwork network) {
        Objects.requireNonNull(network, "Known network cannot be null");
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return false;
        }
        try {
            iSharedConnectivityService.connectKnownNetwork(network);
            return true;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in connectKnownNetwork", e);
            return false;
        }
    }

    public boolean forgetKnownNetwork(KnownNetwork network) {
        Objects.requireNonNull(network, "Known network cannot be null");
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return false;
        }
        try {
            iSharedConnectivityService.forgetKnownNetwork(network);
            return true;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in forgetKnownNetwork", e);
            return false;
        }
    }

    public List<HotspotNetwork> getHotspotNetworks() {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return null;
        }
        try {
            return iSharedConnectivityService.getHotspotNetworks();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in getHotspotNetworks", e);
            return null;
        }
    }

    public List<KnownNetwork> getKnownNetworks() {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return null;
        }
        try {
            return iSharedConnectivityService.getKnownNetworks();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in getKnownNetworks", e);
            return null;
        }
    }

    public SharedConnectivitySettingsState getSettingsState() {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return null;
        }
        try {
            return iSharedConnectivityService.getSettingsState();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in getSettingsState", e);
            return null;
        }
    }

    public HotspotNetworkConnectionStatus getHotspotNetworkConnectionStatus() {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return null;
        }
        try {
            return iSharedConnectivityService.getHotspotNetworkConnectionStatus();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in getHotspotNetworkConnectionStatus", e);
            return null;
        }
    }

    public KnownNetworkConnectionStatus getKnownNetworkConnectionStatus() {
        ISharedConnectivityService iSharedConnectivityService = this.mService;
        if (iSharedConnectivityService == null) {
            return null;
        }
        try {
            return iSharedConnectivityService.getKnownNetworkConnectionStatus();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Exception in getKnownNetworkConnectionStatus", e);
            return null;
        }
    }
}
