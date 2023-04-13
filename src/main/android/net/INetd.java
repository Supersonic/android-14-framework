package android.net;

import android.net.INetdUnsolicitedEventListener;
import android.net.netd.aidl.NativeUidRangeConfig;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface INetd extends IInterface {
    public static final int CLAT_MARK = -559038041;
    public static final int CONF = 1;
    public static final int DUMMY_NET_ID = 51;
    public static final int FIREWALL_ALLOWLIST = 0;
    @Deprecated
    public static final int FIREWALL_BLACKLIST = 1;
    public static final int FIREWALL_CHAIN_DOZABLE = 1;
    public static final int FIREWALL_CHAIN_NONE = 0;
    public static final int FIREWALL_CHAIN_POWERSAVE = 3;
    public static final int FIREWALL_CHAIN_RESTRICTED = 4;
    public static final int FIREWALL_CHAIN_STANDBY = 2;
    public static final int FIREWALL_DENYLIST = 1;
    public static final int FIREWALL_RULE_ALLOW = 1;
    public static final int FIREWALL_RULE_DENY = 2;
    @Deprecated
    public static final int FIREWALL_WHITELIST = 0;
    public static final String HASH = "38614f80a23b92603d4851177e57c460aec1b606";
    public static final String IF_FLAG_BROADCAST = "broadcast";
    public static final String IF_FLAG_LOOPBACK = "loopback";
    public static final String IF_FLAG_MULTICAST = "multicast";
    public static final String IF_FLAG_POINTOPOINT = "point-to-point";
    public static final String IF_FLAG_RUNNING = "running";
    public static final String IF_STATE_DOWN = "down";
    public static final String IF_STATE_UP = "up";
    public static final int IPSEC_DIRECTION_IN = 0;
    public static final int IPSEC_DIRECTION_OUT = 1;
    public static final String IPSEC_INTERFACE_PREFIX = "ipsec";
    public static final int IPV4 = 4;
    public static final int IPV6 = 6;
    public static final int IPV6_ADDR_GEN_MODE_DEFAULT = 0;
    public static final int IPV6_ADDR_GEN_MODE_EUI64 = 0;
    public static final int IPV6_ADDR_GEN_MODE_NONE = 1;
    public static final int IPV6_ADDR_GEN_MODE_RANDOM = 3;
    public static final int IPV6_ADDR_GEN_MODE_STABLE_PRIVACY = 2;
    public static final int LOCAL_NET_ID = 99;
    public static final int NEIGH = 2;
    public static final String NEXTHOP_NONE = "";
    public static final String NEXTHOP_THROW = "throw";
    public static final String NEXTHOP_UNREACHABLE = "unreachable";
    public static final int NO_PERMISSIONS = 0;
    public static final int PENALTY_POLICY_ACCEPT = 1;
    public static final int PENALTY_POLICY_LOG = 2;
    public static final int PENALTY_POLICY_REJECT = 3;
    public static final int PERMISSION_INTERNET = 4;
    public static final int PERMISSION_NETWORK = 1;
    public static final int PERMISSION_NONE = 0;
    public static final int PERMISSION_SYSTEM = 2;
    public static final int PERMISSION_UNINSTALLED = -1;
    public static final int PERMISSION_UPDATE_DEVICE_STATS = 8;
    public static final int UNREACHABLE_NET_ID = 52;
    public static final int VERSION = 13;

    /* loaded from: classes.dex */
    public static class Default implements INetd {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.INetd
        public void bandwidthAddNaughtyApp(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthAddNiceApp(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean bandwidthEnableDataSaver(boolean z) throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public void bandwidthRemoveInterfaceAlert(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthRemoveInterfaceQuota(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthRemoveNaughtyApp(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthRemoveNiceApp(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthSetGlobalAlert(long j) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthSetInterfaceAlert(String str, long j) throws RemoteException {
        }

        @Override // android.net.INetd
        public void bandwidthSetInterfaceQuota(String str, long j) throws RemoteException {
        }

        @Override // android.net.INetd
        public String clatdStart(String str, String str2) throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void clatdStop(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void firewallAddUidInterfaceRules(String str, int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void firewallEnableChildChain(int i, boolean z) throws RemoteException {
        }

        @Override // android.net.INetd
        public void firewallRemoveUidInterfaceRules(int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean firewallReplaceUidChain(String str, boolean z, int[] iArr) throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public void firewallSetFirewallType(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void firewallSetInterfaceRule(String str, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void firewallSetUidRule(int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.net.INetd
        public MarkMaskParcel getFwmarkForNetwork(int i) throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.INetd
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.INetd
        public IBinder getOemNetd() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public String getProcSysNet(int i, int i2, String str, String str2) throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void idletimerAddInterface(String str, int i, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void idletimerRemoveInterface(String str, int i, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceAddAddress(String str, String str2, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceClearAddrs(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceDelAddress(String str, String str2, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public InterfaceConfigurationParcel interfaceGetCfg(String str) throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public String[] interfaceGetList() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void interfaceSetCfg(InterfaceConfigurationParcel interfaceConfigurationParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceSetEnableIPv6(String str, boolean z) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceSetIPv6PrivacyExtensions(String str, boolean z) throws RemoteException {
        }

        @Override // android.net.INetd
        public void interfaceSetMtu(String str, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecAddSecurityAssociation(int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, String str3, byte[] bArr, int i7, String str4, byte[] bArr2, int i8, String str5, byte[] bArr3, int i9, int i10, int i11, int i12, int i13) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecAddSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecAddTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.net.INetd
        public int ipSecAllocateSpi(int i, String str, String str2, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.net.INetd
        public void ipSecApplyTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor, int i, int i2, String str, String str2, int i3) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecDeleteSecurityAssociation(int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecDeleteSecurityPolicy(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecMigrate(IpSecMigrateInfoParcel ipSecMigrateInfoParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecRemoveTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecRemoveTunnelInterface(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecSetEncapSocketOwner(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecUpdateSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipSecUpdateTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipfwdAddInterfaceForward(String str, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipfwdDisableForwarding(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void ipfwdEnableForwarding(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean ipfwdEnabled() throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public String[] ipfwdGetRequesterList() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void ipfwdRemoveInterfaceForward(String str, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean isAlive() throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public void networkAddInterface(int i, String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkAddLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkAddRoute(int i, String str, String str2, String str3) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkAddRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkAddUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkAddUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean networkCanProtect(int i) throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public void networkClearDefault() throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkClearPermissionForUser(int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkCreate(NativeNetworkConfig nativeNetworkConfig) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkCreatePhysical(int i, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkCreateVpn(int i, boolean z) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkDestroy(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public int networkGetDefault() throws RemoteException {
            return 0;
        }

        @Override // android.net.INetd
        public void networkRejectNonSecureVpn(boolean z, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveInterface(int i, String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveRoute(int i, String str, String str2, String str3) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkRemoveUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkSetDefault(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkSetPermissionForNetwork(int i, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkSetPermissionForUser(int i, int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkSetProtectAllow(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkSetProtectDeny(int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void networkUpdateRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void registerUnsolicitedEventListener(INetdUnsolicitedEventListener iNetdUnsolicitedEventListener) throws RemoteException {
        }

        @Override // android.net.INetd
        public void setIPv6AddrGenMode(String str, int i) throws RemoteException {
        }

        @Override // android.net.INetd
        public void setNetworkAllowlist(NativeUidRangeConfig[] nativeUidRangeConfigArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void setProcSysNet(int i, int i2, String str, String str2, String str3) throws RemoteException {
        }

        @Override // android.net.INetd
        public void setTcpRWmemorySize(String str, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void socketDestroy(UidRangeParcel[] uidRangeParcelArr, int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void strictUidCleartextPenalty(int i, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherAddForward(String str, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean tetherApplyDnsInterfaces() throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public String[] tetherDnsList() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void tetherDnsSet(int i, String[] strArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public TetherStatsParcel[] tetherGetStats() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void tetherInterfaceAdd(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public String[] tetherInterfaceList() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void tetherInterfaceRemove(String str) throws RemoteException {
        }

        @Override // android.net.INetd
        public boolean tetherIsEnabled() throws RemoteException {
            return false;
        }

        @Override // android.net.INetd
        public TetherStatsParcel tetherOffloadGetAndClearStats(int i) throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public TetherStatsParcel[] tetherOffloadGetStats() throws RemoteException {
            return null;
        }

        @Override // android.net.INetd
        public void tetherOffloadRuleAdd(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherOffloadRuleRemove(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherOffloadSetInterfaceQuota(int i, long j) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherRemoveForward(String str, String str2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherStart(String[] strArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherStartWithConfiguration(TetherConfigParcel tetherConfigParcel) throws RemoteException {
        }

        @Override // android.net.INetd
        public void tetherStop() throws RemoteException {
        }

        @Override // android.net.INetd
        public void trafficSetNetPermForUids(int i, int[] iArr) throws RemoteException {
        }

        @Override // android.net.INetd
        public void trafficSwapActiveStatsMap() throws RemoteException {
        }

        @Override // android.net.INetd
        public void wakeupAddInterface(String str, String str2, int i, int i2) throws RemoteException {
        }

        @Override // android.net.INetd
        public void wakeupDelInterface(String str, String str2, int i, int i2) throws RemoteException {
        }
    }

    void bandwidthAddNaughtyApp(int i) throws RemoteException;

    void bandwidthAddNiceApp(int i) throws RemoteException;

    boolean bandwidthEnableDataSaver(boolean z) throws RemoteException;

    void bandwidthRemoveInterfaceAlert(String str) throws RemoteException;

    void bandwidthRemoveInterfaceQuota(String str) throws RemoteException;

    void bandwidthRemoveNaughtyApp(int i) throws RemoteException;

    void bandwidthRemoveNiceApp(int i) throws RemoteException;

    void bandwidthSetGlobalAlert(long j) throws RemoteException;

    void bandwidthSetInterfaceAlert(String str, long j) throws RemoteException;

    void bandwidthSetInterfaceQuota(String str, long j) throws RemoteException;

    @Deprecated
    String clatdStart(String str, String str2) throws RemoteException;

    @Deprecated
    void clatdStop(String str) throws RemoteException;

    void firewallAddUidInterfaceRules(String str, int[] iArr) throws RemoteException;

    void firewallEnableChildChain(int i, boolean z) throws RemoteException;

    void firewallRemoveUidInterfaceRules(int[] iArr) throws RemoteException;

    boolean firewallReplaceUidChain(String str, boolean z, int[] iArr) throws RemoteException;

    void firewallSetFirewallType(int i) throws RemoteException;

    void firewallSetInterfaceRule(String str, int i) throws RemoteException;

    void firewallSetUidRule(int i, int i2, int i3) throws RemoteException;

    MarkMaskParcel getFwmarkForNetwork(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    IBinder getOemNetd() throws RemoteException;

    String getProcSysNet(int i, int i2, String str, String str2) throws RemoteException;

    void idletimerAddInterface(String str, int i, String str2) throws RemoteException;

    void idletimerRemoveInterface(String str, int i, String str2) throws RemoteException;

    void interfaceAddAddress(String str, String str2, int i) throws RemoteException;

    void interfaceClearAddrs(String str) throws RemoteException;

    void interfaceDelAddress(String str, String str2, int i) throws RemoteException;

    InterfaceConfigurationParcel interfaceGetCfg(String str) throws RemoteException;

    String[] interfaceGetList() throws RemoteException;

    void interfaceSetCfg(InterfaceConfigurationParcel interfaceConfigurationParcel) throws RemoteException;

    void interfaceSetEnableIPv6(String str, boolean z) throws RemoteException;

    void interfaceSetIPv6PrivacyExtensions(String str, boolean z) throws RemoteException;

    void interfaceSetMtu(String str, int i) throws RemoteException;

    void ipSecAddSecurityAssociation(int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, String str3, byte[] bArr, int i7, String str4, byte[] bArr2, int i8, String str5, byte[] bArr3, int i9, int i10, int i11, int i12, int i13) throws RemoteException;

    void ipSecAddSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException;

    void ipSecAddTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException;

    int ipSecAllocateSpi(int i, String str, String str2, int i2) throws RemoteException;

    void ipSecApplyTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor, int i, int i2, String str, String str2, int i3) throws RemoteException;

    void ipSecDeleteSecurityAssociation(int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException;

    void ipSecDeleteSecurityPolicy(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void ipSecMigrate(IpSecMigrateInfoParcel ipSecMigrateInfoParcel) throws RemoteException;

    void ipSecRemoveTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void ipSecRemoveTunnelInterface(String str) throws RemoteException;

    void ipSecSetEncapSocketOwner(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    void ipSecUpdateSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException;

    void ipSecUpdateTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException;

    void ipfwdAddInterfaceForward(String str, String str2) throws RemoteException;

    void ipfwdDisableForwarding(String str) throws RemoteException;

    void ipfwdEnableForwarding(String str) throws RemoteException;

    boolean ipfwdEnabled() throws RemoteException;

    String[] ipfwdGetRequesterList() throws RemoteException;

    void ipfwdRemoveInterfaceForward(String str, String str2) throws RemoteException;

    boolean isAlive() throws RemoteException;

    void networkAddInterface(int i, String str) throws RemoteException;

    void networkAddLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException;

    void networkAddRoute(int i, String str, String str2, String str3) throws RemoteException;

    void networkAddRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException;

    void networkAddUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    void networkAddUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException;

    boolean networkCanProtect(int i) throws RemoteException;

    void networkClearDefault() throws RemoteException;

    void networkClearPermissionForUser(int[] iArr) throws RemoteException;

    void networkCreate(NativeNetworkConfig nativeNetworkConfig) throws RemoteException;

    @Deprecated
    void networkCreatePhysical(int i, int i2) throws RemoteException;

    @Deprecated
    void networkCreateVpn(int i, boolean z) throws RemoteException;

    void networkDestroy(int i) throws RemoteException;

    int networkGetDefault() throws RemoteException;

    void networkRejectNonSecureVpn(boolean z, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    void networkRemoveInterface(int i, String str) throws RemoteException;

    void networkRemoveLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException;

    void networkRemoveRoute(int i, String str, String str2, String str3) throws RemoteException;

    void networkRemoveRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException;

    void networkRemoveUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    void networkRemoveUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException;

    void networkSetDefault(int i) throws RemoteException;

    void networkSetPermissionForNetwork(int i, int i2) throws RemoteException;

    void networkSetPermissionForUser(int i, int[] iArr) throws RemoteException;

    void networkSetProtectAllow(int i) throws RemoteException;

    void networkSetProtectDeny(int i) throws RemoteException;

    void networkUpdateRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException;

    void registerUnsolicitedEventListener(INetdUnsolicitedEventListener iNetdUnsolicitedEventListener) throws RemoteException;

    void setIPv6AddrGenMode(String str, int i) throws RemoteException;

    void setNetworkAllowlist(NativeUidRangeConfig[] nativeUidRangeConfigArr) throws RemoteException;

    void setProcSysNet(int i, int i2, String str, String str2, String str3) throws RemoteException;

    void setTcpRWmemorySize(String str, String str2) throws RemoteException;

    void socketDestroy(UidRangeParcel[] uidRangeParcelArr, int[] iArr) throws RemoteException;

    void strictUidCleartextPenalty(int i, int i2) throws RemoteException;

    void tetherAddForward(String str, String str2) throws RemoteException;

    boolean tetherApplyDnsInterfaces() throws RemoteException;

    String[] tetherDnsList() throws RemoteException;

    void tetherDnsSet(int i, String[] strArr) throws RemoteException;

    TetherStatsParcel[] tetherGetStats() throws RemoteException;

    void tetherInterfaceAdd(String str) throws RemoteException;

    String[] tetherInterfaceList() throws RemoteException;

    void tetherInterfaceRemove(String str) throws RemoteException;

    boolean tetherIsEnabled() throws RemoteException;

    @Deprecated
    TetherStatsParcel tetherOffloadGetAndClearStats(int i) throws RemoteException;

    @Deprecated
    TetherStatsParcel[] tetherOffloadGetStats() throws RemoteException;

    @Deprecated
    void tetherOffloadRuleAdd(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException;

    @Deprecated
    void tetherOffloadRuleRemove(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException;

    @Deprecated
    void tetherOffloadSetInterfaceQuota(int i, long j) throws RemoteException;

    void tetherRemoveForward(String str, String str2) throws RemoteException;

    void tetherStart(String[] strArr) throws RemoteException;

    void tetherStartWithConfiguration(TetherConfigParcel tetherConfigParcel) throws RemoteException;

    void tetherStop() throws RemoteException;

    void trafficSetNetPermForUids(int i, int[] iArr) throws RemoteException;

    void trafficSwapActiveStatsMap() throws RemoteException;

    void wakeupAddInterface(String str, String str2, int i, int i2) throws RemoteException;

    void wakeupDelInterface(String str, String str2, int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements INetd {
        public static final String DESCRIPTOR = "android$net$INetd".replace('$', '.');
        static final int TRANSACTION_bandwidthAddNaughtyApp = 50;
        static final int TRANSACTION_bandwidthAddNiceApp = 52;
        static final int TRANSACTION_bandwidthEnableDataSaver = 3;
        static final int TRANSACTION_bandwidthRemoveInterfaceAlert = 48;
        static final int TRANSACTION_bandwidthRemoveInterfaceQuota = 46;
        static final int TRANSACTION_bandwidthRemoveNaughtyApp = 51;
        static final int TRANSACTION_bandwidthRemoveNiceApp = 53;
        static final int TRANSACTION_bandwidthSetGlobalAlert = 49;
        static final int TRANSACTION_bandwidthSetInterfaceAlert = 47;
        static final int TRANSACTION_bandwidthSetInterfaceQuota = 45;
        static final int TRANSACTION_clatdStart = 37;
        static final int TRANSACTION_clatdStop = 38;
        static final int TRANSACTION_firewallAddUidInterfaceRules = 91;
        static final int TRANSACTION_firewallEnableChildChain = 79;
        static final int TRANSACTION_firewallRemoveUidInterfaceRules = 92;
        static final int TRANSACTION_firewallReplaceUidChain = 2;
        static final int TRANSACTION_firewallSetFirewallType = 76;
        static final int TRANSACTION_firewallSetInterfaceRule = 77;
        static final int TRANSACTION_firewallSetUidRule = 78;
        static final int TRANSACTION_getFwmarkForNetwork = 96;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getOemNetd = 94;
        static final int TRANSACTION_getProcSysNet = 17;
        static final int TRANSACTION_idletimerAddInterface = 34;
        static final int TRANSACTION_idletimerRemoveInterface = 35;
        static final int TRANSACTION_interfaceAddAddress = 15;
        static final int TRANSACTION_interfaceClearAddrs = 84;
        static final int TRANSACTION_interfaceDelAddress = 16;
        static final int TRANSACTION_interfaceGetCfg = 81;
        static final int TRANSACTION_interfaceGetList = 80;
        static final int TRANSACTION_interfaceSetCfg = 82;
        static final int TRANSACTION_interfaceSetEnableIPv6 = 85;
        static final int TRANSACTION_interfaceSetIPv6PrivacyExtensions = 83;
        static final int TRANSACTION_interfaceSetMtu = 86;
        static final int TRANSACTION_ipSecAddSecurityAssociation = 21;
        static final int TRANSACTION_ipSecAddSecurityPolicy = 25;
        static final int TRANSACTION_ipSecAddTunnelInterface = 28;
        static final int TRANSACTION_ipSecAllocateSpi = 20;
        static final int TRANSACTION_ipSecApplyTransportModeTransform = 23;
        static final int TRANSACTION_ipSecDeleteSecurityAssociation = 22;
        static final int TRANSACTION_ipSecDeleteSecurityPolicy = 27;
        static final int TRANSACTION_ipSecMigrate = 108;
        static final int TRANSACTION_ipSecRemoveTransportModeTransform = 24;
        static final int TRANSACTION_ipSecRemoveTunnelInterface = 30;
        static final int TRANSACTION_ipSecSetEncapSocketOwner = 19;
        static final int TRANSACTION_ipSecUpdateSecurityPolicy = 26;
        static final int TRANSACTION_ipSecUpdateTunnelInterface = 29;
        static final int TRANSACTION_ipfwdAddInterfaceForward = 43;
        static final int TRANSACTION_ipfwdDisableForwarding = 42;
        static final int TRANSACTION_ipfwdEnableForwarding = 41;
        static final int TRANSACTION_ipfwdEnabled = 39;
        static final int TRANSACTION_ipfwdGetRequesterList = 40;
        static final int TRANSACTION_ipfwdRemoveInterfaceForward = 44;
        static final int TRANSACTION_isAlive = 1;
        static final int TRANSACTION_networkAddInterface = 7;
        static final int TRANSACTION_networkAddLegacyRoute = 64;
        static final int TRANSACTION_networkAddRoute = 62;
        static final int TRANSACTION_networkAddRouteParcel = 97;
        static final int TRANSACTION_networkAddUidRanges = 9;
        static final int TRANSACTION_networkAddUidRangesParcel = 106;
        static final int TRANSACTION_networkCanProtect = 75;
        static final int TRANSACTION_networkClearDefault = 68;
        static final int TRANSACTION_networkClearPermissionForUser = 71;
        static final int TRANSACTION_networkCreate = 105;
        static final int TRANSACTION_networkCreatePhysical = 4;
        static final int TRANSACTION_networkCreateVpn = 5;
        static final int TRANSACTION_networkDestroy = 6;
        static final int TRANSACTION_networkGetDefault = 66;
        static final int TRANSACTION_networkRejectNonSecureVpn = 11;
        static final int TRANSACTION_networkRemoveInterface = 8;
        static final int TRANSACTION_networkRemoveLegacyRoute = 65;
        static final int TRANSACTION_networkRemoveRoute = 63;
        static final int TRANSACTION_networkRemoveRouteParcel = 99;
        static final int TRANSACTION_networkRemoveUidRanges = 10;
        static final int TRANSACTION_networkRemoveUidRangesParcel = 107;
        static final int TRANSACTION_networkSetDefault = 67;
        static final int TRANSACTION_networkSetPermissionForNetwork = 69;
        static final int TRANSACTION_networkSetPermissionForUser = 70;
        static final int TRANSACTION_networkSetProtectAllow = 73;
        static final int TRANSACTION_networkSetProtectDeny = 74;
        static final int TRANSACTION_networkUpdateRouteParcel = 98;
        static final int TRANSACTION_registerUnsolicitedEventListener = 90;
        static final int TRANSACTION_setIPv6AddrGenMode = 33;
        static final int TRANSACTION_setNetworkAllowlist = 109;
        static final int TRANSACTION_setProcSysNet = 18;
        static final int TRANSACTION_setTcpRWmemorySize = 89;
        static final int TRANSACTION_socketDestroy = 12;
        static final int TRANSACTION_strictUidCleartextPenalty = 36;
        static final int TRANSACTION_tetherAddForward = 87;
        static final int TRANSACTION_tetherApplyDnsInterfaces = 13;
        static final int TRANSACTION_tetherDnsList = 61;
        static final int TRANSACTION_tetherDnsSet = 60;
        static final int TRANSACTION_tetherGetStats = 14;
        static final int TRANSACTION_tetherInterfaceAdd = 57;
        static final int TRANSACTION_tetherInterfaceList = 59;
        static final int TRANSACTION_tetherInterfaceRemove = 58;
        static final int TRANSACTION_tetherIsEnabled = 56;
        static final int TRANSACTION_tetherOffloadGetAndClearStats = 104;
        static final int TRANSACTION_tetherOffloadGetStats = 102;
        static final int TRANSACTION_tetherOffloadRuleAdd = 100;
        static final int TRANSACTION_tetherOffloadRuleRemove = 101;
        static final int TRANSACTION_tetherOffloadSetInterfaceQuota = 103;
        static final int TRANSACTION_tetherRemoveForward = 88;
        static final int TRANSACTION_tetherStart = 54;
        static final int TRANSACTION_tetherStartWithConfiguration = 95;
        static final int TRANSACTION_tetherStop = 55;
        static final int TRANSACTION_trafficSetNetPermForUids = 72;
        static final int TRANSACTION_trafficSwapActiveStatsMap = 93;
        static final int TRANSACTION_wakeupAddInterface = 31;
        static final int TRANSACTION_wakeupDelInterface = 32;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetd asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof INetd)) {
                return (INetd) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = DESCRIPTOR;
            if (i >= 1 && i <= TRANSACTION_getInterfaceVersion) {
                parcel.enforceInterface(str);
            }
            switch (i) {
                case TRANSACTION_getInterfaceHash /* 16777214 */:
                    parcel2.writeNoException();
                    parcel2.writeString(getInterfaceHash());
                    return true;
                case TRANSACTION_getInterfaceVersion /* 16777215 */:
                    parcel2.writeNoException();
                    parcel2.writeInt(getInterfaceVersion());
                    return true;
                case 1598968902:
                    parcel2.writeString(str);
                    return true;
                default:
                    switch (i) {
                        case 1:
                            boolean isAlive = isAlive();
                            parcel2.writeNoException();
                            parcel2.writeBoolean(isAlive);
                            return true;
                        case 2:
                            boolean firewallReplaceUidChain = firewallReplaceUidChain(parcel.readString(), parcel.readBoolean(), parcel.createIntArray());
                            parcel2.writeNoException();
                            parcel2.writeBoolean(firewallReplaceUidChain);
                            return true;
                        case 3:
                            boolean bandwidthEnableDataSaver = bandwidthEnableDataSaver(parcel.readBoolean());
                            parcel2.writeNoException();
                            parcel2.writeBoolean(bandwidthEnableDataSaver);
                            return true;
                        case 4:
                            networkCreatePhysical(parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 5:
                            networkCreateVpn(parcel.readInt(), parcel.readBoolean());
                            parcel2.writeNoException();
                            return true;
                        case 6:
                            networkDestroy(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 7:
                            networkAddInterface(parcel.readInt(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 8:
                            networkRemoveInterface(parcel.readInt(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 9:
                            networkAddUidRanges(parcel.readInt(), (UidRangeParcel[]) parcel.createTypedArray(UidRangeParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 10:
                            networkRemoveUidRanges(parcel.readInt(), (UidRangeParcel[]) parcel.createTypedArray(UidRangeParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 11:
                            networkRejectNonSecureVpn(parcel.readBoolean(), (UidRangeParcel[]) parcel.createTypedArray(UidRangeParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 12:
                            socketDestroy((UidRangeParcel[]) parcel.createTypedArray(UidRangeParcel.CREATOR), parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 13:
                            boolean tetherApplyDnsInterfaces = tetherApplyDnsInterfaces();
                            parcel2.writeNoException();
                            parcel2.writeBoolean(tetherApplyDnsInterfaces);
                            return true;
                        case 14:
                            TetherStatsParcel[] tetherGetStats = tetherGetStats();
                            parcel2.writeNoException();
                            parcel2.writeTypedArray(tetherGetStats, 1);
                            return true;
                        case 15:
                            interfaceAddAddress(parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 16:
                            interfaceDelAddress(parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 17:
                            String procSysNet = getProcSysNet(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            parcel2.writeString(procSysNet);
                            return true;
                        case 18:
                            setProcSysNet(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 19:
                            ipSecSetEncapSocketOwner((ParcelFileDescriptor) parcel.readTypedObject(ParcelFileDescriptor.CREATOR), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 20:
                            int ipSecAllocateSpi = ipSecAllocateSpi(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeInt(ipSecAllocateSpi);
                            return true;
                        case 21:
                            ipSecAddSecurityAssociation(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.createByteArray(), parcel.readInt(), parcel.readString(), parcel.createByteArray(), parcel.readInt(), parcel.readString(), parcel.createByteArray(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 22:
                            ipSecDeleteSecurityAssociation(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 23:
                            ipSecApplyTransportModeTransform((ParcelFileDescriptor) parcel.readTypedObject(ParcelFileDescriptor.CREATOR), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 24:
                            ipSecRemoveTransportModeTransform((ParcelFileDescriptor) parcel.readTypedObject(ParcelFileDescriptor.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 25:
                            ipSecAddSecurityPolicy(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 26:
                            ipSecUpdateSecurityPolicy(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 27:
                            ipSecDeleteSecurityPolicy(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 28:
                            ipSecAddTunnelInterface(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 29:
                            ipSecUpdateTunnelInterface(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 30:
                            ipSecRemoveTunnelInterface(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 31:
                            wakeupAddInterface(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 32:
                            wakeupDelInterface(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 33:
                            setIPv6AddrGenMode(parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 34:
                            idletimerAddInterface(parcel.readString(), parcel.readInt(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 35:
                            idletimerRemoveInterface(parcel.readString(), parcel.readInt(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 36:
                            strictUidCleartextPenalty(parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 37:
                            String clatdStart = clatdStart(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            parcel2.writeString(clatdStart);
                            return true;
                        case 38:
                            clatdStop(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 39:
                            boolean ipfwdEnabled = ipfwdEnabled();
                            parcel2.writeNoException();
                            parcel2.writeBoolean(ipfwdEnabled);
                            return true;
                        case 40:
                            String[] ipfwdGetRequesterList = ipfwdGetRequesterList();
                            parcel2.writeNoException();
                            parcel2.writeStringArray(ipfwdGetRequesterList);
                            return true;
                        case 41:
                            ipfwdEnableForwarding(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 42:
                            ipfwdDisableForwarding(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 43:
                            ipfwdAddInterfaceForward(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 44:
                            ipfwdRemoveInterfaceForward(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 45:
                            bandwidthSetInterfaceQuota(parcel.readString(), parcel.readLong());
                            parcel2.writeNoException();
                            return true;
                        case 46:
                            bandwidthRemoveInterfaceQuota(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 47:
                            bandwidthSetInterfaceAlert(parcel.readString(), parcel.readLong());
                            parcel2.writeNoException();
                            return true;
                        case 48:
                            bandwidthRemoveInterfaceAlert(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 49:
                            bandwidthSetGlobalAlert(parcel.readLong());
                            parcel2.writeNoException();
                            return true;
                        case 50:
                            bandwidthAddNaughtyApp(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 51:
                            bandwidthRemoveNaughtyApp(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 52:
                            bandwidthAddNiceApp(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 53:
                            bandwidthRemoveNiceApp(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 54:
                            tetherStart(parcel.createStringArray());
                            parcel2.writeNoException();
                            return true;
                        case 55:
                            tetherStop();
                            parcel2.writeNoException();
                            return true;
                        case 56:
                            boolean tetherIsEnabled = tetherIsEnabled();
                            parcel2.writeNoException();
                            parcel2.writeBoolean(tetherIsEnabled);
                            return true;
                        case 57:
                            tetherInterfaceAdd(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 58:
                            tetherInterfaceRemove(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 59:
                            String[] tetherInterfaceList = tetherInterfaceList();
                            parcel2.writeNoException();
                            parcel2.writeStringArray(tetherInterfaceList);
                            return true;
                        case 60:
                            tetherDnsSet(parcel.readInt(), parcel.createStringArray());
                            parcel2.writeNoException();
                            return true;
                        case 61:
                            String[] tetherDnsList = tetherDnsList();
                            parcel2.writeNoException();
                            parcel2.writeStringArray(tetherDnsList);
                            return true;
                        case 62:
                            networkAddRoute(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 63:
                            networkRemoveRoute(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 64:
                            networkAddLegacyRoute(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 65:
                            networkRemoveLegacyRoute(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 66:
                            int networkGetDefault = networkGetDefault();
                            parcel2.writeNoException();
                            parcel2.writeInt(networkGetDefault);
                            return true;
                        case 67:
                            networkSetDefault(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 68:
                            networkClearDefault();
                            parcel2.writeNoException();
                            return true;
                        case 69:
                            networkSetPermissionForNetwork(parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 70:
                            networkSetPermissionForUser(parcel.readInt(), parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 71:
                            networkClearPermissionForUser(parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 72:
                            trafficSetNetPermForUids(parcel.readInt(), parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 73:
                            networkSetProtectAllow(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 74:
                            networkSetProtectDeny(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 75:
                            boolean networkCanProtect = networkCanProtect(parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeBoolean(networkCanProtect);
                            return true;
                        case 76:
                            firewallSetFirewallType(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 77:
                            firewallSetInterfaceRule(parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 78:
                            firewallSetUidRule(parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 79:
                            firewallEnableChildChain(parcel.readInt(), parcel.readBoolean());
                            parcel2.writeNoException();
                            return true;
                        case 80:
                            String[] interfaceGetList = interfaceGetList();
                            parcel2.writeNoException();
                            parcel2.writeStringArray(interfaceGetList);
                            return true;
                        case 81:
                            InterfaceConfigurationParcel interfaceGetCfg = interfaceGetCfg(parcel.readString());
                            parcel2.writeNoException();
                            parcel2.writeTypedObject(interfaceGetCfg, 1);
                            return true;
                        case 82:
                            interfaceSetCfg((InterfaceConfigurationParcel) parcel.readTypedObject(InterfaceConfigurationParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 83:
                            interfaceSetIPv6PrivacyExtensions(parcel.readString(), parcel.readBoolean());
                            parcel2.writeNoException();
                            return true;
                        case 84:
                            interfaceClearAddrs(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 85:
                            interfaceSetEnableIPv6(parcel.readString(), parcel.readBoolean());
                            parcel2.writeNoException();
                            return true;
                        case 86:
                            interfaceSetMtu(parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 87:
                            tetherAddForward(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 88:
                            tetherRemoveForward(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 89:
                            setTcpRWmemorySize(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 90:
                            registerUnsolicitedEventListener(INetdUnsolicitedEventListener.Stub.asInterface(parcel.readStrongBinder()));
                            parcel2.writeNoException();
                            return true;
                        case 91:
                            firewallAddUidInterfaceRules(parcel.readString(), parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 92:
                            firewallRemoveUidInterfaceRules(parcel.createIntArray());
                            parcel2.writeNoException();
                            return true;
                        case 93:
                            trafficSwapActiveStatsMap();
                            parcel2.writeNoException();
                            return true;
                        case 94:
                            IBinder oemNetd = getOemNetd();
                            parcel2.writeNoException();
                            parcel2.writeStrongBinder(oemNetd);
                            return true;
                        case 95:
                            tetherStartWithConfiguration((TetherConfigParcel) parcel.readTypedObject(TetherConfigParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 96:
                            MarkMaskParcel fwmarkForNetwork = getFwmarkForNetwork(parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeTypedObject(fwmarkForNetwork, 1);
                            return true;
                        case 97:
                            networkAddRouteParcel(parcel.readInt(), (RouteInfoParcel) parcel.readTypedObject(RouteInfoParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 98:
                            networkUpdateRouteParcel(parcel.readInt(), (RouteInfoParcel) parcel.readTypedObject(RouteInfoParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 99:
                            networkRemoveRouteParcel(parcel.readInt(), (RouteInfoParcel) parcel.readTypedObject(RouteInfoParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 100:
                            tetherOffloadRuleAdd((TetherOffloadRuleParcel) parcel.readTypedObject(TetherOffloadRuleParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 101:
                            tetherOffloadRuleRemove((TetherOffloadRuleParcel) parcel.readTypedObject(TetherOffloadRuleParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 102:
                            TetherStatsParcel[] tetherOffloadGetStats = tetherOffloadGetStats();
                            parcel2.writeNoException();
                            parcel2.writeTypedArray(tetherOffloadGetStats, 1);
                            return true;
                        case 103:
                            tetherOffloadSetInterfaceQuota(parcel.readInt(), parcel.readLong());
                            parcel2.writeNoException();
                            return true;
                        case 104:
                            TetherStatsParcel tetherOffloadGetAndClearStats = tetherOffloadGetAndClearStats(parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeTypedObject(tetherOffloadGetAndClearStats, 1);
                            return true;
                        case 105:
                            networkCreate((NativeNetworkConfig) parcel.readTypedObject(NativeNetworkConfig.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 106:
                            networkAddUidRangesParcel((NativeUidRangeConfig) parcel.readTypedObject(NativeUidRangeConfig.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 107:
                            networkRemoveUidRangesParcel((NativeUidRangeConfig) parcel.readTypedObject(NativeUidRangeConfig.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 108:
                            ipSecMigrate((IpSecMigrateInfoParcel) parcel.readTypedObject(IpSecMigrateInfoParcel.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        case 109:
                            setNetworkAllowlist((NativeUidRangeConfig[]) parcel.createTypedArray(NativeUidRangeConfig.CREATOR));
                            parcel2.writeNoException();
                            return true;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements INetd {
            public IBinder mRemote;
            public int mCachedVersion = -1;
            public String mCachedHash = "-1";

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.net.INetd
            public boolean isAlive() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method isAlive is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean firewallReplaceUidChain(String str, boolean z, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallReplaceUidChain is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean bandwidthEnableDataSaver(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthEnableDataSaver is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkCreatePhysical(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkCreatePhysical is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkCreateVpn(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkCreateVpn is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkDestroy(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkDestroy is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddInterface(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveInterface(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedArray(uidRangeParcelArr, 0);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddUidRanges is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedArray(uidRangeParcelArr, 0);
                    if (!this.mRemote.transact(10, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveUidRanges is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRejectNonSecureVpn(boolean z, UidRangeParcel[] uidRangeParcelArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    obtain.writeTypedArray(uidRangeParcelArr, 0);
                    if (!this.mRemote.transact(11, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRejectNonSecureVpn is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void socketDestroy(UidRangeParcel[] uidRangeParcelArr, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedArray(uidRangeParcelArr, 0);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0)) {
                        throw new RemoteException("Method socketDestroy is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean tetherApplyDnsInterfaces() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(13, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherApplyDnsInterfaces is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public TetherStatsParcel[] tetherGetStats() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(14, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherGetStats is unimplemented.");
                    }
                    obtain2.readException();
                    return (TetherStatsParcel[]) obtain2.createTypedArray(TetherStatsParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceAddAddress(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(15, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceAddAddress is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceDelAddress(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(16, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceDelAddress is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String getProcSysNet(int i, int i2, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(17, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getProcSysNet is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void setProcSysNet(int i, int i2, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (!this.mRemote.transact(18, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setProcSysNet is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecSetEncapSocketOwner(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(parcelFileDescriptor, 0);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(19, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecSetEncapSocketOwner is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public int ipSecAllocateSpi(int i, String str, String str2, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(20, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecAllocateSpi is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecAddSecurityAssociation(int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, String str3, byte[] bArr, int i7, String str4, byte[] bArr2, int i8, String str5, byte[] bArr3, int i9, int i10, int i11, int i12, int i13) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    obtain.writeString(str3);
                    obtain.writeByteArray(bArr);
                    obtain.writeInt(i7);
                    obtain.writeString(str4);
                    obtain.writeByteArray(bArr2);
                    obtain.writeInt(i8);
                    obtain.writeString(str5);
                    obtain.writeByteArray(bArr3);
                    obtain.writeInt(i9);
                    obtain.writeInt(i10);
                    obtain.writeInt(i11);
                    obtain.writeInt(i12);
                    obtain.writeInt(i13);
                    if (!this.mRemote.transact(21, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecAddSecurityAssociation is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecDeleteSecurityAssociation(int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    if (!this.mRemote.transact(22, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecDeleteSecurityAssociation is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecApplyTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor, int i, int i2, String str, String str2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(parcelFileDescriptor, 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(23, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecApplyTransportModeTransform is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecRemoveTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(parcelFileDescriptor, 0);
                    if (!this.mRemote.transact(24, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecRemoveTransportModeTransform is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecAddSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    obtain.writeInt(i7);
                    if (!this.mRemote.transact(25, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecAddSecurityPolicy is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecUpdateSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    obtain.writeInt(i7);
                    if (!this.mRemote.transact(26, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecUpdateSecurityPolicy is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecDeleteSecurityPolicy(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    if (!this.mRemote.transact(27, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecDeleteSecurityPolicy is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecAddTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(28, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecAddTunnelInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecUpdateTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(29, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecUpdateTunnelInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecRemoveTunnelInterface(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(30, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecRemoveTunnelInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void wakeupAddInterface(String str, String str2, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(31, obtain, obtain2, 0)) {
                        throw new RemoteException("Method wakeupAddInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void wakeupDelInterface(String str, String str2, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(32, obtain, obtain2, 0)) {
                        throw new RemoteException("Method wakeupDelInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void setIPv6AddrGenMode(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(33, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setIPv6AddrGenMode is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void idletimerAddInterface(String str, int i, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(34, obtain, obtain2, 0)) {
                        throw new RemoteException("Method idletimerAddInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void idletimerRemoveInterface(String str, int i, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(35, obtain, obtain2, 0)) {
                        throw new RemoteException("Method idletimerRemoveInterface is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void strictUidCleartextPenalty(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(36, obtain, obtain2, 0)) {
                        throw new RemoteException("Method strictUidCleartextPenalty is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String clatdStart(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(37, obtain, obtain2, 0)) {
                        throw new RemoteException("Method clatdStart is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void clatdStop(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(38, obtain, obtain2, 0)) {
                        throw new RemoteException("Method clatdStop is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean ipfwdEnabled() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(39, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdEnabled is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String[] ipfwdGetRequesterList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(40, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdGetRequesterList is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipfwdEnableForwarding(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(41, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdEnableForwarding is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipfwdDisableForwarding(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(42, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdDisableForwarding is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipfwdAddInterfaceForward(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(43, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdAddInterfaceForward is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipfwdRemoveInterfaceForward(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(44, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipfwdRemoveInterfaceForward is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthSetInterfaceQuota(String str, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(45, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthSetInterfaceQuota is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthRemoveInterfaceQuota(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(46, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthRemoveInterfaceQuota is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthSetInterfaceAlert(String str, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(47, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthSetInterfaceAlert is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthRemoveInterfaceAlert(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(48, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthRemoveInterfaceAlert is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthSetGlobalAlert(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(49, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthSetGlobalAlert is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthAddNaughtyApp(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(50, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthAddNaughtyApp is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthRemoveNaughtyApp(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(51, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthRemoveNaughtyApp is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthAddNiceApp(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(52, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthAddNiceApp is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void bandwidthRemoveNiceApp(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(53, obtain, obtain2, 0)) {
                        throw new RemoteException("Method bandwidthRemoveNiceApp is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherStart(String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStringArray(strArr);
                    if (!this.mRemote.transact(54, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherStart is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherStop() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(55, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherStop is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean tetherIsEnabled() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(56, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherIsEnabled is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherInterfaceAdd(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(57, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherInterfaceAdd is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherInterfaceRemove(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(58, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherInterfaceRemove is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String[] tetherInterfaceList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(59, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherInterfaceList is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherDnsSet(int i, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStringArray(strArr);
                    if (!this.mRemote.transact(60, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherDnsSet is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String[] tetherDnsList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(61, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherDnsList is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddRoute(int i, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (!this.mRemote.transact(62, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddRoute is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveRoute(int i, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (!this.mRemote.transact(63, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveRoute is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(64, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddLegacyRoute is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(65, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveLegacyRoute is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public int networkGetDefault() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(66, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkGetDefault is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkSetDefault(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(67, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkSetDefault is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkClearDefault() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(68, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkClearDefault is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkSetPermissionForNetwork(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (!this.mRemote.transact(69, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkSetPermissionForNetwork is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkSetPermissionForUser(int i, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(70, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkSetPermissionForUser is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkClearPermissionForUser(int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(71, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkClearPermissionForUser is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void trafficSetNetPermForUids(int i, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(72, obtain, obtain2, 0)) {
                        throw new RemoteException("Method trafficSetNetPermForUids is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkSetProtectAllow(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(73, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkSetProtectAllow is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkSetProtectDeny(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(74, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkSetProtectDeny is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public boolean networkCanProtect(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(75, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkCanProtect is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallSetFirewallType(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(76, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallSetFirewallType is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallSetInterfaceRule(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(77, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallSetInterfaceRule is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallSetUidRule(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (!this.mRemote.transact(78, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallSetUidRule is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallEnableChildChain(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(79, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallEnableChildChain is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public String[] interfaceGetList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(80, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceGetList is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public InterfaceConfigurationParcel interfaceGetCfg(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(81, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceGetCfg is unimplemented.");
                    }
                    obtain2.readException();
                    return (InterfaceConfigurationParcel) obtain2.readTypedObject(InterfaceConfigurationParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceSetCfg(InterfaceConfigurationParcel interfaceConfigurationParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(interfaceConfigurationParcel, 0);
                    if (!this.mRemote.transact(82, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceSetCfg is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceSetIPv6PrivacyExtensions(String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(83, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceSetIPv6PrivacyExtensions is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceClearAddrs(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(84, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceClearAddrs is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceSetEnableIPv6(String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(85, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceSetEnableIPv6 is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void interfaceSetMtu(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(86, obtain, obtain2, 0)) {
                        throw new RemoteException("Method interfaceSetMtu is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherAddForward(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(87, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherAddForward is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherRemoveForward(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(88, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherRemoveForward is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void setTcpRWmemorySize(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(89, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setTcpRWmemorySize is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void registerUnsolicitedEventListener(INetdUnsolicitedEventListener iNetdUnsolicitedEventListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongInterface(iNetdUnsolicitedEventListener);
                    if (!this.mRemote.transact(90, obtain, obtain2, 0)) {
                        throw new RemoteException("Method registerUnsolicitedEventListener is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallAddUidInterfaceRules(String str, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(91, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallAddUidInterfaceRules is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void firewallRemoveUidInterfaceRules(int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(92, obtain, obtain2, 0)) {
                        throw new RemoteException("Method firewallRemoveUidInterfaceRules is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void trafficSwapActiveStatsMap() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(93, obtain, obtain2, 0)) {
                        throw new RemoteException("Method trafficSwapActiveStatsMap is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public IBinder getOemNetd() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(94, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getOemNetd is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherStartWithConfiguration(TetherConfigParcel tetherConfigParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(tetherConfigParcel, 0);
                    if (!this.mRemote.transact(95, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherStartWithConfiguration is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public MarkMaskParcel getFwmarkForNetwork(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(96, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getFwmarkForNetwork is unimplemented.");
                    }
                    obtain2.readException();
                    return (MarkMaskParcel) obtain2.readTypedObject(MarkMaskParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(routeInfoParcel, 0);
                    if (!this.mRemote.transact(97, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddRouteParcel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkUpdateRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(routeInfoParcel, 0);
                    if (!this.mRemote.transact(98, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkUpdateRouteParcel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveRouteParcel(int i, RouteInfoParcel routeInfoParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(routeInfoParcel, 0);
                    if (!this.mRemote.transact(99, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveRouteParcel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherOffloadRuleAdd(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(tetherOffloadRuleParcel, 0);
                    if (!this.mRemote.transact(100, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherOffloadRuleAdd is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherOffloadRuleRemove(TetherOffloadRuleParcel tetherOffloadRuleParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(tetherOffloadRuleParcel, 0);
                    if (!this.mRemote.transact(101, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherOffloadRuleRemove is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public TetherStatsParcel[] tetherOffloadGetStats() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(102, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherOffloadGetStats is unimplemented.");
                    }
                    obtain2.readException();
                    return (TetherStatsParcel[]) obtain2.createTypedArray(TetherStatsParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void tetherOffloadSetInterfaceQuota(int i, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(103, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherOffloadSetInterfaceQuota is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public TetherStatsParcel tetherOffloadGetAndClearStats(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(104, obtain, obtain2, 0)) {
                        throw new RemoteException("Method tetherOffloadGetAndClearStats is unimplemented.");
                    }
                    obtain2.readException();
                    return (TetherStatsParcel) obtain2.readTypedObject(TetherStatsParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkCreate(NativeNetworkConfig nativeNetworkConfig) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(nativeNetworkConfig, 0);
                    if (!this.mRemote.transact(105, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkCreate is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkAddUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(nativeUidRangeConfig, 0);
                    if (!this.mRemote.transact(106, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkAddUidRangesParcel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void networkRemoveUidRangesParcel(NativeUidRangeConfig nativeUidRangeConfig) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(nativeUidRangeConfig, 0);
                    if (!this.mRemote.transact(107, obtain, obtain2, 0)) {
                        throw new RemoteException("Method networkRemoveUidRangesParcel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void ipSecMigrate(IpSecMigrateInfoParcel ipSecMigrateInfoParcel) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(ipSecMigrateInfoParcel, 0);
                    if (!this.mRemote.transact(108, obtain, obtain2, 0)) {
                        throw new RemoteException("Method ipSecMigrate is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public void setNetworkAllowlist(NativeUidRangeConfig[] nativeUidRangeConfigArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedArray(nativeUidRangeConfigArr, 0);
                    if (!this.mRemote.transact(109, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setNetworkAllowlist is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.net.INetd
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                        this.mRemote.transact(Stub.TRANSACTION_getInterfaceVersion, obtain, obtain2, 0);
                        obtain2.readException();
                        this.mCachedVersion = obtain2.readInt();
                    } finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.net.INetd
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInterfaceHash, obtain, obtain2, 0);
                    obtain2.readException();
                    this.mCachedHash = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
