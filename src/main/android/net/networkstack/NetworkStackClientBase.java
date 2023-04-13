package android.net.networkstack;

import android.net.IIpMemoryStoreCallbacks;
import android.net.INetworkMonitorCallbacks;
import android.net.INetworkStackConnector;
import android.net.Network;
import android.net.dhcp.DhcpServingParamsParcel;
import android.net.dhcp.IDhcpServerCallbacks;
import android.net.p003ip.IIpClientCallbacks;
import android.os.RemoteException;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public abstract class NetworkStackClientBase {
    @GuardedBy({"mPendingNetStackRequests"})
    private INetworkStackConnector mConnector;
    @GuardedBy({"mPendingNetStackRequests"})
    private final ArrayList<Consumer<INetworkStackConnector>> mPendingNetStackRequests = new ArrayList<>();

    public void makeDhcpServer(final String str, final DhcpServingParamsParcel dhcpServingParamsParcel, final IDhcpServerCallbacks iDhcpServerCallbacks) {
        requestConnector(new Consumer() { // from class: android.net.networkstack.NetworkStackClientBase$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NetworkStackClientBase.lambda$makeDhcpServer$0(str, dhcpServingParamsParcel, iDhcpServerCallbacks, (INetworkStackConnector) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeDhcpServer$0(String str, DhcpServingParamsParcel dhcpServingParamsParcel, IDhcpServerCallbacks iDhcpServerCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeDhcpServer(str, dhcpServingParamsParcel, iDhcpServerCallbacks);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create DhcpServer", e);
        }
    }

    public void makeIpClient(final String str, final IIpClientCallbacks iIpClientCallbacks) {
        requestConnector(new Consumer() { // from class: android.net.networkstack.NetworkStackClientBase$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NetworkStackClientBase.lambda$makeIpClient$1(str, iIpClientCallbacks, (INetworkStackConnector) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeIpClient$1(String str, IIpClientCallbacks iIpClientCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeIpClient(str, iIpClientCallbacks);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create IpClient", e);
        }
    }

    public void makeNetworkMonitor(final Network network, final String str, final INetworkMonitorCallbacks iNetworkMonitorCallbacks) {
        requestConnector(new Consumer() { // from class: android.net.networkstack.NetworkStackClientBase$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NetworkStackClientBase.lambda$makeNetworkMonitor$2(network, str, iNetworkMonitorCallbacks, (INetworkStackConnector) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeNetworkMonitor$2(Network network, String str, INetworkMonitorCallbacks iNetworkMonitorCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.makeNetworkMonitor(network, str, iNetworkMonitorCallbacks);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create NetworkMonitor", e);
        }
    }

    public void fetchIpMemoryStore(final IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks) {
        requestConnector(new Consumer() { // from class: android.net.networkstack.NetworkStackClientBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NetworkStackClientBase.lambda$fetchIpMemoryStore$3(IIpMemoryStoreCallbacks.this, (INetworkStackConnector) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$fetchIpMemoryStore$3(IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks, INetworkStackConnector iNetworkStackConnector) {
        try {
            iNetworkStackConnector.fetchIpMemoryStore(iIpMemoryStoreCallbacks);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not fetch IpMemoryStore", e);
        }
    }

    public void requestConnector(Consumer<INetworkStackConnector> consumer) {
        synchronized (this.mPendingNetStackRequests) {
            INetworkStackConnector iNetworkStackConnector = this.mConnector;
            if (iNetworkStackConnector == null) {
                this.mPendingNetStackRequests.add(consumer);
            } else {
                consumer.accept(iNetworkStackConnector);
            }
        }
    }

    public void onNetworkStackConnected(INetworkStackConnector iNetworkStackConnector) {
        ArrayList arrayList;
        while (true) {
            synchronized (this.mPendingNetStackRequests) {
                arrayList = new ArrayList(this.mPendingNetStackRequests);
                this.mPendingNetStackRequests.clear();
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((Consumer) it.next()).accept(iNetworkStackConnector);
            }
            synchronized (this.mPendingNetStackRequests) {
                if (this.mPendingNetStackRequests.size() == 0) {
                    this.mConnector = iNetworkStackConnector;
                    return;
                }
            }
        }
    }

    public int getQueueLength() {
        int size;
        synchronized (this.mPendingNetStackRequests) {
            size = this.mPendingNetStackRequests.size();
        }
        return size;
    }
}
