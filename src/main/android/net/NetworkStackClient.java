package android.net;

import android.net.ConnectivityModuleConnector;
import android.net.INetworkStackConnector;
import android.net.dhcp.DhcpServingParamsParcel;
import android.net.dhcp.IDhcpServerCallbacks;
import android.net.p003ip.IIpClientCallbacks;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class NetworkStackClient {
    private static final int NETWORKSTACK_TIMEOUT_MS = 10000;
    private static final String TAG = "NetworkStackClient";
    private static NetworkStackClient sInstance;
    @GuardedBy({"mPendingNetStackRequests"})
    private INetworkStackConnector mConnector;
    private final Dependencies mDependencies;
    @GuardedBy({"mPendingNetStackRequests"})
    private final ArrayList<NetworkStackCallback> mPendingNetStackRequests;
    private volatile boolean mWasSystemServerInitialized;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Dependencies {
        void addToServiceManager(IBinder iBinder);

        void checkCallerUid();

        ConnectivityModuleConnector getConnectivityModuleConnector();
    }

    /* loaded from: classes.dex */
    public interface NetworkStackCallback {
        void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector);
    }

    @VisibleForTesting
    public NetworkStackClient(Dependencies dependencies) {
        this.mPendingNetStackRequests = new ArrayList<>();
        this.mWasSystemServerInitialized = false;
        this.mDependencies = dependencies;
    }

    private NetworkStackClient() {
        this(new DependenciesImpl());
    }

    /* loaded from: classes.dex */
    public static class DependenciesImpl implements Dependencies {
        public DependenciesImpl() {
        }

        @Override // android.net.NetworkStackClient.Dependencies
        public void addToServiceManager(IBinder iBinder) {
            ServiceManager.addService("network_stack", iBinder, false, 6);
        }

        @Override // android.net.NetworkStackClient.Dependencies
        public void checkCallerUid() {
            int callingUid = Binder.getCallingUid();
            if (callingUid != 1000 && callingUid != 1073 && UserHandle.getAppId(callingUid) != 1002) {
                throw new SecurityException("Only the system server should try to bind to the network stack.");
            }
        }

        @Override // android.net.NetworkStackClient.Dependencies
        public ConnectivityModuleConnector getConnectivityModuleConnector() {
            return ConnectivityModuleConnector.getInstance();
        }
    }

    public static synchronized NetworkStackClient getInstance() {
        NetworkStackClient networkStackClient;
        synchronized (NetworkStackClient.class) {
            if (sInstance == null) {
                sInstance = new NetworkStackClient();
            }
            networkStackClient = sInstance;
        }
        return networkStackClient;
    }

    public void makeDhcpServer(final String str, final DhcpServingParamsParcel dhcpServingParamsParcel, final IDhcpServerCallbacks iDhcpServerCallbacks) {
        requestConnector(new NetworkStackCallback() { // from class: android.net.NetworkStackClient$$ExternalSyntheticLambda2
            @Override // android.net.NetworkStackClient.NetworkStackCallback
            public final void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector) {
                NetworkStackClient.lambda$makeDhcpServer$0(str, dhcpServingParamsParcel, iDhcpServerCallbacks, iNetworkStackConnector);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeDhcpServer$0(String str, DhcpServingParamsParcel dhcpServingParamsParcel, IDhcpServerCallbacks iDhcpServerCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeDhcpServer(str, dhcpServingParamsParcel, iDhcpServerCallbacks);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeIpClient(final String str, final IIpClientCallbacks iIpClientCallbacks) {
        requestConnector(new NetworkStackCallback() { // from class: android.net.NetworkStackClient$$ExternalSyntheticLambda1
            @Override // android.net.NetworkStackClient.NetworkStackCallback
            public final void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector) {
                NetworkStackClient.lambda$makeIpClient$1(str, iIpClientCallbacks, iNetworkStackConnector);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeIpClient$1(String str, IIpClientCallbacks iIpClientCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeIpClient(str, iIpClientCallbacks);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeNetworkMonitor(final Network network, final String str, final INetworkMonitorCallbacks iNetworkMonitorCallbacks) {
        requestConnector(new NetworkStackCallback() { // from class: android.net.NetworkStackClient$$ExternalSyntheticLambda0
            @Override // android.net.NetworkStackClient.NetworkStackCallback
            public final void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector) {
                NetworkStackClient.lambda$makeNetworkMonitor$2(network, str, iNetworkMonitorCallbacks, iNetworkStackConnector);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeNetworkMonitor$2(Network network, String str, INetworkMonitorCallbacks iNetworkMonitorCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeNetworkMonitor(network, str, iNetworkMonitorCallbacks);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void fetchIpMemoryStore(final IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks) {
        requestConnector(new NetworkStackCallback() { // from class: android.net.NetworkStackClient$$ExternalSyntheticLambda3
            @Override // android.net.NetworkStackClient.NetworkStackCallback
            public final void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector) {
                NetworkStackClient.lambda$fetchIpMemoryStore$3(IIpMemoryStoreCallbacks.this, iNetworkStackConnector);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$fetchIpMemoryStore$3(IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.fetchIpMemoryStore(iIpMemoryStoreCallbacks);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public class NetworkStackConnection implements ConnectivityModuleConnector.ModuleServiceCallback {
        public NetworkStackConnection() {
        }

        @Override // android.net.ConnectivityModuleConnector.ModuleServiceCallback
        public void onModuleServiceConnected(IBinder iBinder) {
            NetworkStackClient.this.logi("Network stack service connected");
            NetworkStackClient.this.registerNetworkStackService(iBinder);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerNetworkStackService(IBinder iBinder) {
        ArrayList arrayList;
        INetworkStackConnector asInterface = INetworkStackConnector.Stub.asInterface(iBinder);
        this.mDependencies.addToServiceManager(iBinder);
        log("Network stack service registered");
        synchronized (this.mPendingNetStackRequests) {
            arrayList = new ArrayList(this.mPendingNetStackRequests);
            this.mPendingNetStackRequests.clear();
            this.mConnector = asInterface;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((NetworkStackCallback) it.next()).onNetworkStackConnected(asInterface);
        }
    }

    public void init() {
        log("Network stack init");
        this.mWasSystemServerInitialized = true;
    }

    public void start() {
        this.mDependencies.getConnectivityModuleConnector().startModuleService(INetworkStackConnector.class.getName(), "android.permission.MAINLINE_NETWORK_STACK", new NetworkStackConnection());
        log("Network stack service start requested");
    }

    private void log(String str) {
        Log.d(TAG, str);
    }

    private void logWtf(String str, Throwable th) {
        String str2 = TAG;
        Slog.wtf(str2, str);
        Log.e(str2, str, th);
    }

    private void loge(String str, Throwable th) {
        Log.e(TAG, str, th);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String str) {
        Log.i(TAG, str);
    }

    private INetworkStackConnector getRemoteConnector() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            do {
                IBinder service = ServiceManager.getService("network_stack");
                if (service == null) {
                    Thread.sleep(20L);
                } else {
                    return INetworkStackConnector.Stub.asInterface(service);
                }
            } while (System.currentTimeMillis() - currentTimeMillis <= 10000);
            loge("Timeout waiting for NetworkStack connector", null);
            return null;
        } catch (InterruptedException e) {
            loge("Error waiting for NetworkStack connector", e);
            return null;
        }
    }

    private void requestConnector(NetworkStackCallback networkStackCallback) {
        this.mDependencies.checkCallerUid();
        if (!this.mWasSystemServerInitialized) {
            INetworkStackConnector remoteConnector = getRemoteConnector();
            synchronized (this.mPendingNetStackRequests) {
                this.mConnector = remoteConnector;
            }
            networkStackCallback.onNetworkStackConnected(remoteConnector);
            return;
        }
        synchronized (this.mPendingNetStackRequests) {
            INetworkStackConnector iNetworkStackConnector = this.mConnector;
            if (iNetworkStackConnector == null) {
                this.mPendingNetStackRequests.add(networkStackCallback);
            } else {
                networkStackCallback.onNetworkStackConnected(iNetworkStackConnector);
            }
        }
    }
}
