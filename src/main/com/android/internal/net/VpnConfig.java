package com.android.internal.net;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.Network;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes4.dex */
public class VpnConfig implements Parcelable {
    public static final Parcelable.Creator<VpnConfig> CREATOR = new Parcelable.Creator<VpnConfig>() { // from class: com.android.internal.net.VpnConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnConfig createFromParcel(Parcel in) {
            VpnConfig config = new VpnConfig();
            config.user = in.readString();
            config.interfaze = in.readString();
            config.session = in.readString();
            config.mtu = in.readInt();
            in.readTypedList(config.addresses, LinkAddress.CREATOR);
            in.readTypedList(config.routes, RouteInfo.CREATOR);
            config.dnsServers = in.createStringArrayList();
            config.searchDomains = in.createStringArrayList();
            config.allowedApplications = in.createStringArrayList();
            config.disallowedApplications = in.createStringArrayList();
            config.configureIntent = (PendingIntent) in.readParcelable(null, PendingIntent.class);
            config.startTime = in.readLong();
            config.legacy = in.readInt() != 0;
            config.blocking = in.readInt() != 0;
            config.allowBypass = in.readInt() != 0;
            config.allowIPv4 = in.readInt() != 0;
            config.allowIPv6 = in.readInt() != 0;
            config.isMetered = in.readInt() != 0;
            config.requiresInternetValidation = in.readInt() != 0;
            config.excludeLocalRoutes = in.readInt() != 0;
            config.underlyingNetworks = (Network[]) in.createTypedArray(Network.CREATOR);
            config.proxyInfo = (ProxyInfo) in.readParcelable(null, ProxyInfo.class);
            return config;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnConfig[] newArray(int size) {
            return new VpnConfig[size];
        }
    };
    public static final String DIALOGS_PACKAGE = "com.android.vpndialogs";
    public static final String LEGACY_VPN = "[Legacy VPN]";
    public static final String SERVICE_INTERFACE = "android.net.VpnService";
    public List<LinkAddress> addresses;
    public boolean allowBypass;
    public boolean allowIPv4;
    public boolean allowIPv6;
    public List<String> allowedApplications;
    public boolean blocking;
    public PendingIntent configureIntent;
    public List<String> disallowedApplications;
    public List<String> dnsServers;
    public boolean excludeLocalRoutes;
    public String interfaze;
    public boolean isMetered;
    public boolean legacy;
    public int mtu;
    public ProxyInfo proxyInfo;
    public boolean requiresInternetValidation;
    public List<RouteInfo> routes;
    public List<String> searchDomains;
    public String session;
    public long startTime;
    public Network[] underlyingNetworks;
    public String user;

    public static Intent getIntentForConfirmation() {
        Intent intent = new Intent();
        ComponentName componentName = ComponentName.unflattenFromString(Resources.getSystem().getString(C4057R.string.config_customVpnConfirmDialogComponent));
        intent.setClassName(componentName.getPackageName(), componentName.getClassName());
        return intent;
    }

    public static PendingIntent getIntentForStatusPanel(Context context) {
        Intent intent = new Intent();
        intent.setClassName(DIALOGS_PACKAGE, "com.android.vpndialogs.ManageDialog");
        intent.addFlags(1350565888);
        return PendingIntent.getActivityAsUser(context, 0, intent, 67108864, null, UserHandle.CURRENT);
    }

    public static CharSequence getVpnLabel(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.net.VpnService");
        intent.setPackage(packageName);
        List<ResolveInfo> services = pm.queryIntentServices(intent, 0);
        if (services != null && services.size() == 1) {
            return services.get(0).loadLabel(pm);
        }
        return pm.getApplicationInfo(packageName, 0).loadLabel(pm);
    }

    public VpnConfig() {
        this.mtu = -1;
        this.addresses = new ArrayList();
        this.routes = new ArrayList();
        this.startTime = -1L;
        this.isMetered = true;
        this.requiresInternetValidation = false;
        this.excludeLocalRoutes = false;
    }

    public VpnConfig(VpnConfig other) {
        this.mtu = -1;
        this.addresses = new ArrayList();
        this.routes = new ArrayList();
        this.startTime = -1L;
        this.isMetered = true;
        this.requiresInternetValidation = false;
        this.excludeLocalRoutes = false;
        this.user = other.user;
        this.interfaze = other.interfaze;
        this.session = other.session;
        this.mtu = other.mtu;
        this.addresses = copyOf(other.addresses);
        this.routes = copyOf(other.routes);
        this.dnsServers = copyOf(other.dnsServers);
        this.searchDomains = copyOf(other.searchDomains);
        this.allowedApplications = copyOf(other.allowedApplications);
        this.disallowedApplications = copyOf(other.disallowedApplications);
        this.configureIntent = other.configureIntent;
        this.startTime = other.startTime;
        this.legacy = other.legacy;
        this.blocking = other.blocking;
        this.allowBypass = other.allowBypass;
        this.allowIPv4 = other.allowIPv4;
        this.allowIPv6 = other.allowIPv6;
        this.isMetered = other.isMetered;
        this.requiresInternetValidation = other.requiresInternetValidation;
        this.excludeLocalRoutes = other.excludeLocalRoutes;
        Network[] networkArr = other.underlyingNetworks;
        this.underlyingNetworks = networkArr != null ? (Network[]) Arrays.copyOf(networkArr, networkArr.length) : null;
        this.proxyInfo = other.proxyInfo;
    }

    private static <T> List<T> copyOf(List<T> list) {
        if (list != null) {
            return new ArrayList(list);
        }
        return null;
    }

    public void addLegacyRoutes(String routesStr) {
        if (routesStr.trim().equals("")) {
            return;
        }
        String[] routes = routesStr.trim().split(" ");
        for (String route : routes) {
            RouteInfo info = new RouteInfo(new IpPrefix(route), null, null, 1);
            this.routes.add(info);
        }
    }

    public void addLegacyAddresses(String addressesStr) {
        if (addressesStr.trim().equals("")) {
            return;
        }
        String[] addresses = addressesStr.trim().split(" ");
        for (String address : addresses) {
            LinkAddress addr = new LinkAddress(address);
            this.addresses.add(addr);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.user);
        out.writeString(this.interfaze);
        out.writeString(this.session);
        out.writeInt(this.mtu);
        out.writeTypedList(this.addresses);
        out.writeTypedList(this.routes);
        out.writeStringList(this.dnsServers);
        out.writeStringList(this.searchDomains);
        out.writeStringList(this.allowedApplications);
        out.writeStringList(this.disallowedApplications);
        out.writeParcelable(this.configureIntent, flags);
        out.writeLong(this.startTime);
        out.writeInt(this.legacy ? 1 : 0);
        out.writeInt(this.blocking ? 1 : 0);
        out.writeInt(this.allowBypass ? 1 : 0);
        out.writeInt(this.allowIPv4 ? 1 : 0);
        out.writeInt(this.allowIPv6 ? 1 : 0);
        out.writeInt(this.isMetered ? 1 : 0);
        out.writeInt(this.requiresInternetValidation ? 1 : 0);
        out.writeInt(this.excludeLocalRoutes ? 1 : 0);
        out.writeTypedArray(this.underlyingNetworks, flags);
        out.writeParcelable(this.proxyInfo, flags);
    }

    public String toString() {
        return "VpnConfig{ user=" + this.user + ", interface=" + this.interfaze + ", session=" + this.session + ", mtu=" + this.mtu + ", addresses=" + toString(this.addresses) + ", routes=" + toString(this.routes) + ", dns=" + toString(this.dnsServers) + ", searchDomains=" + toString(this.searchDomains) + ", allowedApps=" + toString(this.allowedApplications) + ", disallowedApps=" + toString(this.disallowedApplications) + ", configureIntent=" + this.configureIntent + ", startTime=" + this.startTime + ", legacy=" + this.legacy + ", blocking=" + this.blocking + ", allowBypass=" + this.allowBypass + ", allowIPv4=" + this.allowIPv4 + ", allowIPv6=" + this.allowIPv6 + ", isMetered=" + this.isMetered + ", requiresInternetValidation=" + this.requiresInternetValidation + ", excludeLocalRoutes=" + this.excludeLocalRoutes + ", underlyingNetworks=" + Arrays.toString(this.underlyingNetworks) + ", proxyInfo=" + this.proxyInfo + "}";
    }

    static <T> String toString(List<T> ls) {
        if (ls == null) {
            return "null";
        }
        return Arrays.toString(ls.toArray());
    }
}
