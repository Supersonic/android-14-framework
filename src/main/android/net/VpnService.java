package android.net;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.IPackageManager;
import android.content.p001pm.PackageManager;
import android.net.IVpnManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.system.OsConstants;
import com.android.internal.net.NetworkUtilsInternal;
import com.android.internal.net.VpnConfig;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes2.dex */
public class VpnService extends Service {
    public static final String SERVICE_INTERFACE = "android.net.VpnService";
    public static final String SERVICE_META_DATA_SUPPORTS_ALWAYS_ON = "android.net.VpnService.SUPPORTS_ALWAYS_ON";

    /* JADX INFO: Access modifiers changed from: private */
    public static IVpnManager getService() {
        return IVpnManager.Stub.asInterface(ServiceManager.getService(Context.VPN_MANAGEMENT_SERVICE));
    }

    public static Intent prepare(Context context) {
        try {
            if (getService().prepareVpn(context.getPackageName(), null, context.getUserId())) {
                return null;
            }
        } catch (RemoteException e) {
        }
        return VpnConfig.getIntentForConfirmation();
    }

    @SystemApi
    public static void prepareAndAuthorize(Context context) {
        IVpnManager vm = getService();
        String packageName = context.getPackageName();
        try {
            int userId = context.getUserId();
            if (!vm.prepareVpn(packageName, null, userId)) {
                vm.prepareVpn(null, packageName, userId);
            }
            vm.setVpnPackageAuthorization(packageName, userId, 1);
        } catch (RemoteException e) {
        }
    }

    public boolean protect(int socket) {
        return NetworkUtilsInternal.protectFromVpn(socket);
    }

    public boolean protect(Socket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean protect(DatagramSocket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean addAddress(InetAddress address, int prefixLength) {
        check(address, prefixLength);
        try {
            return getService().addVpnAddress(address.getHostAddress(), prefixLength);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean removeAddress(InetAddress address, int prefixLength) {
        check(address, prefixLength);
        try {
            return getService().removeVpnAddress(address.getHostAddress(), prefixLength);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean setUnderlyingNetworks(Network[] networks) {
        try {
            return getService().setUnderlyingNetworksForVpn(networks);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public final boolean isAlwaysOn() {
        try {
            return getService().isCallerCurrentAlwaysOnVpnApp();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final boolean isLockdownEnabled() {
        try {
            return getService().isCallerCurrentAlwaysOnVpnLockdownApp();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (intent == null || !"android.net.VpnService".equals(intent.getAction())) {
            return null;
        }
        return new Callback();
    }

    public void onRevoke() {
        stopSelf();
    }

    /* loaded from: classes2.dex */
    private class Callback extends Binder {
        private Callback() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code == 16777215) {
                VpnService.this.onRevoke();
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void check(InetAddress address, int prefixLength) {
        if (address.isLoopbackAddress()) {
            throw new IllegalArgumentException("Bad address");
        }
        if (address instanceof Inet4Address) {
            if (prefixLength < 0 || prefixLength > 32) {
                throw new IllegalArgumentException("Bad prefixLength");
            }
        } else if (address instanceof Inet6Address) {
            if (prefixLength < 0 || prefixLength > 128) {
                throw new IllegalArgumentException("Bad prefixLength");
            }
        } else {
            throw new IllegalArgumentException("Unsupported family");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkNonPrefixBytes(InetAddress address, int prefixLength) {
        IpPrefix prefix = new IpPrefix(address, prefixLength);
        if (!prefix.getAddress().equals(address)) {
            throw new IllegalArgumentException("Bad address");
        }
    }

    /* loaded from: classes2.dex */
    public class Builder {
        private final List<LinkAddress> mAddresses;
        private final VpnConfig mConfig;
        private final List<RouteInfo> mRoutes;

        public Builder() {
            VpnConfig vpnConfig = new VpnConfig();
            this.mConfig = vpnConfig;
            this.mAddresses = new ArrayList();
            this.mRoutes = new ArrayList();
            vpnConfig.user = VpnService.this.getClass().getName();
        }

        public Builder setSession(String session) {
            this.mConfig.session = session;
            return this;
        }

        public Builder setConfigureIntent(PendingIntent intent) {
            this.mConfig.configureIntent = intent;
            return this;
        }

        public Builder setMtu(int mtu) {
            if (mtu <= 0) {
                throw new IllegalArgumentException("Bad mtu");
            }
            this.mConfig.mtu = mtu;
            return this;
        }

        public Builder setHttpProxy(ProxyInfo proxyInfo) {
            this.mConfig.proxyInfo = proxyInfo;
            return this;
        }

        public Builder addAddress(InetAddress address, int prefixLength) {
            VpnService.check(address, prefixLength);
            if (address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            this.mAddresses.add(new LinkAddress(address, prefixLength));
            return this;
        }

        public Builder addAddress(String address, int prefixLength) {
            return addAddress(InetAddress.parseNumericAddress(address), prefixLength);
        }

        private Builder addRoute(IpPrefix prefix, int type) {
            VpnService.check(prefix.getAddress(), prefix.getPrefixLength());
            RouteInfo newRoute = new RouteInfo(prefix, null, null, type);
            int index = findRouteIndexByDestination(newRoute);
            if (index == -1) {
                this.mRoutes.add(newRoute);
            } else {
                this.mRoutes.set(index, newRoute);
            }
            return this;
        }

        public Builder addRoute(InetAddress address, int prefixLength) {
            VpnService.checkNonPrefixBytes(address, prefixLength);
            return addRoute(new IpPrefix(address, prefixLength), 1);
        }

        public Builder addRoute(IpPrefix prefix) {
            return addRoute(prefix, 1);
        }

        public Builder addRoute(String address, int prefixLength) {
            return addRoute(InetAddress.parseNumericAddress(address), prefixLength);
        }

        public Builder excludeRoute(IpPrefix prefix) {
            return addRoute(prefix, 9);
        }

        public Builder addDnsServer(InetAddress address) {
            if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (this.mConfig.dnsServers == null) {
                this.mConfig.dnsServers = new ArrayList();
            }
            this.mConfig.dnsServers.add(address.getHostAddress());
            return this;
        }

        public Builder addDnsServer(String address) {
            return addDnsServer(InetAddress.parseNumericAddress(address));
        }

        public Builder addSearchDomain(String domain) {
            if (this.mConfig.searchDomains == null) {
                this.mConfig.searchDomains = new ArrayList();
            }
            this.mConfig.searchDomains.add(domain);
            return this;
        }

        public Builder allowFamily(int family) {
            if (family == OsConstants.AF_INET) {
                this.mConfig.allowIPv4 = true;
            } else if (family == OsConstants.AF_INET6) {
                this.mConfig.allowIPv6 = true;
            } else {
                throw new IllegalArgumentException(family + " is neither " + OsConstants.AF_INET + " nor " + OsConstants.AF_INET6);
            }
            return this;
        }

        private void verifyApp(String packageName) throws PackageManager.NameNotFoundException {
            IPackageManager pm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
            try {
                pm.getApplicationInfo(packageName, 0L, UserHandle.getCallingUserId());
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }

        public Builder addAllowedApplication(String packageName) throws PackageManager.NameNotFoundException {
            if (this.mConfig.disallowedApplications != null) {
                throw new UnsupportedOperationException("addDisallowedApplication already called");
            }
            verifyApp(packageName);
            if (this.mConfig.allowedApplications == null) {
                this.mConfig.allowedApplications = new ArrayList();
            }
            this.mConfig.allowedApplications.add(packageName);
            return this;
        }

        public Builder addDisallowedApplication(String packageName) throws PackageManager.NameNotFoundException {
            if (this.mConfig.allowedApplications != null) {
                throw new UnsupportedOperationException("addAllowedApplication already called");
            }
            verifyApp(packageName);
            if (this.mConfig.disallowedApplications == null) {
                this.mConfig.disallowedApplications = new ArrayList();
            }
            this.mConfig.disallowedApplications.add(packageName);
            return this;
        }

        public Builder allowBypass() {
            this.mConfig.allowBypass = true;
            return this;
        }

        public Builder setBlocking(boolean blocking) {
            this.mConfig.blocking = blocking;
            return this;
        }

        public Builder setUnderlyingNetworks(Network[] networks) {
            this.mConfig.underlyingNetworks = networks != null ? (Network[]) networks.clone() : null;
            return this;
        }

        public Builder setMetered(boolean isMetered) {
            this.mConfig.isMetered = isMetered;
            return this;
        }

        public ParcelFileDescriptor establish() {
            this.mConfig.addresses = this.mAddresses;
            this.mConfig.routes = this.mRoutes;
            try {
                return VpnService.getService().establishVpn(this.mConfig);
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }

        private int findRouteIndexByDestination(RouteInfo route) {
            for (int i = 0; i < this.mRoutes.size(); i++) {
                if (this.mRoutes.get(i).getDestination().equals(route.getDestination())) {
                    return i;
                }
            }
            return -1;
        }

        public List<RouteInfo> routes() {
            return Collections.unmodifiableList(this.mRoutes);
        }
    }
}
