package android.p008os;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.net.INetworkManagementEventObserver;
import android.net.ITetheringStatsProvider;
import android.net.InterfaceConfiguration;
import android.net.NetworkStats;
import android.net.RouteInfo;
import java.util.List;
/* renamed from: android.os.INetworkManagementService */
/* loaded from: classes3.dex */
public interface INetworkManagementService extends IInterface {
    void addInterfaceToLocalNetwork(String str, List<RouteInfo> list) throws RemoteException;

    void addRoute(int i, RouteInfo routeInfo) throws RemoteException;

    void allowProtect(int i) throws RemoteException;

    void clearInterfaceAddresses(String str) throws RemoteException;

    void denyProtect(int i) throws RemoteException;

    void disableIpv6(String str) throws RemoteException;

    void disableNat(String str, String str2) throws RemoteException;

    void enableIpv6(String str) throws RemoteException;

    void enableNat(String str, String str2) throws RemoteException;

    String[] getDnsForwarders() throws RemoteException;

    InterfaceConfiguration getInterfaceConfig(String str) throws RemoteException;

    boolean getIpForwardingEnabled() throws RemoteException;

    NetworkStats getNetworkStatsTethering(int i) throws RemoteException;

    boolean isBandwidthControlEnabled() throws RemoteException;

    boolean isFirewallEnabled() throws RemoteException;

    boolean isNetworkRestricted(int i) throws RemoteException;

    boolean isTetheringStarted() throws RemoteException;

    String[] listInterfaces() throws RemoteException;

    String[] listTetheredInterfaces() throws RemoteException;

    void registerObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;

    void registerTetheringStatsProvider(ITetheringStatsProvider iTetheringStatsProvider, String str) throws RemoteException;

    void removeInterfaceAlert(String str) throws RemoteException;

    void removeInterfaceFromLocalNetwork(String str) throws RemoteException;

    void removeInterfaceQuota(String str) throws RemoteException;

    void removeRoute(int i, RouteInfo routeInfo) throws RemoteException;

    int removeRoutesFromLocalNetwork(List<RouteInfo> list) throws RemoteException;

    boolean setDataSaverModeEnabled(boolean z) throws RemoteException;

    void setFirewallChainEnabled(int i, boolean z) throws RemoteException;

    void setFirewallEnabled(boolean z) throws RemoteException;

    void setFirewallInterfaceRule(String str, boolean z) throws RemoteException;

    void setFirewallUidRule(int i, int i2, int i3) throws RemoteException;

    void setFirewallUidRules(int i, int[] iArr, int[] iArr2) throws RemoteException;

    void setGlobalAlert(long j) throws RemoteException;

    void setIPv6AddrGenMode(String str, int i) throws RemoteException;

    void setInterfaceAlert(String str, long j) throws RemoteException;

    void setInterfaceConfig(String str, InterfaceConfiguration interfaceConfiguration) throws RemoteException;

    void setInterfaceDown(String str) throws RemoteException;

    void setInterfaceIpv6PrivacyExtensions(String str, boolean z) throws RemoteException;

    void setInterfaceQuota(String str, long j) throws RemoteException;

    void setInterfaceUp(String str) throws RemoteException;

    void setIpForwardingEnabled(boolean z) throws RemoteException;

    void setUidCleartextNetworkPolicy(int i, int i2) throws RemoteException;

    void setUidOnMeteredNetworkAllowlist(int i, boolean z) throws RemoteException;

    void setUidOnMeteredNetworkDenylist(int i, boolean z) throws RemoteException;

    void shutdown() throws RemoteException;

    void startInterfaceForwarding(String str, String str2) throws RemoteException;

    void startTethering(String[] strArr) throws RemoteException;

    void startTetheringWithConfiguration(boolean z, String[] strArr) throws RemoteException;

    void stopInterfaceForwarding(String str, String str2) throws RemoteException;

    void stopTethering() throws RemoteException;

    void tetherInterface(String str) throws RemoteException;

    void tetherLimitReached(ITetheringStatsProvider iTetheringStatsProvider) throws RemoteException;

    void unregisterObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;

    void unregisterTetheringStatsProvider(ITetheringStatsProvider iTetheringStatsProvider) throws RemoteException;

    void untetherInterface(String str) throws RemoteException;

    /* renamed from: android.os.INetworkManagementService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements INetworkManagementService {
        @Override // android.p008os.INetworkManagementService
        public void registerObserver(INetworkManagementEventObserver obs) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void unregisterObserver(INetworkManagementEventObserver obs) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public String[] listInterfaces() throws RemoteException {
            return null;
        }

        @Override // android.p008os.INetworkManagementService
        public InterfaceConfiguration getInterfaceConfig(String iface) throws RemoteException {
            return null;
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceConfig(String iface, InterfaceConfiguration cfg) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void clearInterfaceAddresses(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceDown(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceUp(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceIpv6PrivacyExtensions(String iface, boolean enable) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void disableIpv6(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void enableIpv6(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setIPv6AddrGenMode(String iface, int mode) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void addRoute(int netId, RouteInfo route) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void removeRoute(int netId, RouteInfo route) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void shutdown() throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public boolean getIpForwardingEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.INetworkManagementService
        public void setIpForwardingEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void startTethering(String[] dhcpRanges) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void startTetheringWithConfiguration(boolean usingLegacyDnsProxy, String[] dhcpRanges) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void stopTethering() throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public boolean isTetheringStarted() throws RemoteException {
            return false;
        }

        @Override // android.p008os.INetworkManagementService
        public void tetherInterface(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void untetherInterface(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public String[] listTetheredInterfaces() throws RemoteException {
            return null;
        }

        @Override // android.p008os.INetworkManagementService
        public String[] getDnsForwarders() throws RemoteException {
            return null;
        }

        @Override // android.p008os.INetworkManagementService
        public void startInterfaceForwarding(String fromIface, String toIface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void stopInterfaceForwarding(String fromIface, String toIface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void enableNat(String internalInterface, String externalInterface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void disableNat(String internalInterface, String externalInterface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void registerTetheringStatsProvider(ITetheringStatsProvider provider, String name) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void unregisterTetheringStatsProvider(ITetheringStatsProvider provider) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void tetherLimitReached(ITetheringStatsProvider provider) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public NetworkStats getNetworkStatsTethering(int how) throws RemoteException {
            return null;
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceQuota(String iface, long quotaBytes) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void removeInterfaceQuota(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setInterfaceAlert(String iface, long alertBytes) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void removeInterfaceAlert(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setGlobalAlert(long alertBytes) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setUidOnMeteredNetworkDenylist(int uid, boolean enable) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setUidOnMeteredNetworkAllowlist(int uid, boolean enable) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public boolean setDataSaverModeEnabled(boolean enable) throws RemoteException {
            return false;
        }

        @Override // android.p008os.INetworkManagementService
        public void setUidCleartextNetworkPolicy(int uid, int policy) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public boolean isBandwidthControlEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.INetworkManagementService
        public void setFirewallEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public boolean isFirewallEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.INetworkManagementService
        public void setFirewallInterfaceRule(String iface, boolean allow) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setFirewallUidRule(int chain, int uid, int rule) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setFirewallUidRules(int chain, int[] uids, int[] rules) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void setFirewallChainEnabled(int chain, boolean enable) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void allowProtect(int uid) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void denyProtect(int uid) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void addInterfaceToLocalNetwork(String iface, List<RouteInfo> routes) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public void removeInterfaceFromLocalNetwork(String iface) throws RemoteException {
        }

        @Override // android.p008os.INetworkManagementService
        public int removeRoutesFromLocalNetwork(List<RouteInfo> routes) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.INetworkManagementService
        public boolean isNetworkRestricted(int uid) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.INetworkManagementService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements INetworkManagementService {
        public static final String DESCRIPTOR = "android.os.INetworkManagementService";
        static final int TRANSACTION_addInterfaceToLocalNetwork = 52;
        static final int TRANSACTION_addRoute = 13;
        static final int TRANSACTION_allowProtect = 50;
        static final int TRANSACTION_clearInterfaceAddresses = 6;
        static final int TRANSACTION_denyProtect = 51;
        static final int TRANSACTION_disableIpv6 = 10;
        static final int TRANSACTION_disableNat = 29;
        static final int TRANSACTION_enableIpv6 = 11;
        static final int TRANSACTION_enableNat = 28;
        static final int TRANSACTION_getDnsForwarders = 25;
        static final int TRANSACTION_getInterfaceConfig = 4;
        static final int TRANSACTION_getIpForwardingEnabled = 16;
        static final int TRANSACTION_getNetworkStatsTethering = 33;
        static final int TRANSACTION_isBandwidthControlEnabled = 43;
        static final int TRANSACTION_isFirewallEnabled = 45;
        static final int TRANSACTION_isNetworkRestricted = 55;
        static final int TRANSACTION_isTetheringStarted = 21;
        static final int TRANSACTION_listInterfaces = 3;
        static final int TRANSACTION_listTetheredInterfaces = 24;
        static final int TRANSACTION_registerObserver = 1;
        static final int TRANSACTION_registerTetheringStatsProvider = 30;
        static final int TRANSACTION_removeInterfaceAlert = 37;
        static final int TRANSACTION_removeInterfaceFromLocalNetwork = 53;
        static final int TRANSACTION_removeInterfaceQuota = 35;
        static final int TRANSACTION_removeRoute = 14;
        static final int TRANSACTION_removeRoutesFromLocalNetwork = 54;
        static final int TRANSACTION_setDataSaverModeEnabled = 41;
        static final int TRANSACTION_setFirewallChainEnabled = 49;
        static final int TRANSACTION_setFirewallEnabled = 44;
        static final int TRANSACTION_setFirewallInterfaceRule = 46;
        static final int TRANSACTION_setFirewallUidRule = 47;
        static final int TRANSACTION_setFirewallUidRules = 48;
        static final int TRANSACTION_setGlobalAlert = 38;
        static final int TRANSACTION_setIPv6AddrGenMode = 12;
        static final int TRANSACTION_setInterfaceAlert = 36;
        static final int TRANSACTION_setInterfaceConfig = 5;
        static final int TRANSACTION_setInterfaceDown = 7;
        static final int TRANSACTION_setInterfaceIpv6PrivacyExtensions = 9;
        static final int TRANSACTION_setInterfaceQuota = 34;
        static final int TRANSACTION_setInterfaceUp = 8;
        static final int TRANSACTION_setIpForwardingEnabled = 17;
        static final int TRANSACTION_setUidCleartextNetworkPolicy = 42;
        static final int TRANSACTION_setUidOnMeteredNetworkAllowlist = 40;
        static final int TRANSACTION_setUidOnMeteredNetworkDenylist = 39;
        static final int TRANSACTION_shutdown = 15;
        static final int TRANSACTION_startInterfaceForwarding = 26;
        static final int TRANSACTION_startTethering = 18;
        static final int TRANSACTION_startTetheringWithConfiguration = 19;
        static final int TRANSACTION_stopInterfaceForwarding = 27;
        static final int TRANSACTION_stopTethering = 20;
        static final int TRANSACTION_tetherInterface = 22;
        static final int TRANSACTION_tetherLimitReached = 32;
        static final int TRANSACTION_unregisterObserver = 2;
        static final int TRANSACTION_unregisterTetheringStatsProvider = 31;
        static final int TRANSACTION_untetherInterface = 23;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static INetworkManagementService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkManagementService)) {
                return (INetworkManagementService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "registerObserver";
                case 2:
                    return "unregisterObserver";
                case 3:
                    return "listInterfaces";
                case 4:
                    return "getInterfaceConfig";
                case 5:
                    return "setInterfaceConfig";
                case 6:
                    return "clearInterfaceAddresses";
                case 7:
                    return "setInterfaceDown";
                case 8:
                    return "setInterfaceUp";
                case 9:
                    return "setInterfaceIpv6PrivacyExtensions";
                case 10:
                    return "disableIpv6";
                case 11:
                    return "enableIpv6";
                case 12:
                    return "setIPv6AddrGenMode";
                case 13:
                    return "addRoute";
                case 14:
                    return "removeRoute";
                case 15:
                    return "shutdown";
                case 16:
                    return "getIpForwardingEnabled";
                case 17:
                    return "setIpForwardingEnabled";
                case 18:
                    return "startTethering";
                case 19:
                    return "startTetheringWithConfiguration";
                case 20:
                    return "stopTethering";
                case 21:
                    return "isTetheringStarted";
                case 22:
                    return "tetherInterface";
                case 23:
                    return "untetherInterface";
                case 24:
                    return "listTetheredInterfaces";
                case 25:
                    return "getDnsForwarders";
                case 26:
                    return "startInterfaceForwarding";
                case 27:
                    return "stopInterfaceForwarding";
                case 28:
                    return "enableNat";
                case 29:
                    return "disableNat";
                case 30:
                    return "registerTetheringStatsProvider";
                case 31:
                    return "unregisterTetheringStatsProvider";
                case 32:
                    return "tetherLimitReached";
                case 33:
                    return "getNetworkStatsTethering";
                case 34:
                    return "setInterfaceQuota";
                case 35:
                    return "removeInterfaceQuota";
                case 36:
                    return "setInterfaceAlert";
                case 37:
                    return "removeInterfaceAlert";
                case 38:
                    return "setGlobalAlert";
                case 39:
                    return "setUidOnMeteredNetworkDenylist";
                case 40:
                    return "setUidOnMeteredNetworkAllowlist";
                case 41:
                    return "setDataSaverModeEnabled";
                case 42:
                    return "setUidCleartextNetworkPolicy";
                case 43:
                    return "isBandwidthControlEnabled";
                case 44:
                    return "setFirewallEnabled";
                case 45:
                    return "isFirewallEnabled";
                case 46:
                    return "setFirewallInterfaceRule";
                case 47:
                    return "setFirewallUidRule";
                case 48:
                    return "setFirewallUidRules";
                case 49:
                    return "setFirewallChainEnabled";
                case 50:
                    return "allowProtect";
                case 51:
                    return "denyProtect";
                case 52:
                    return "addInterfaceToLocalNetwork";
                case 53:
                    return "removeInterfaceFromLocalNetwork";
                case 54:
                    return "removeRoutesFromLocalNetwork";
                case 55:
                    return "isNetworkRestricted";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            INetworkManagementEventObserver _arg0 = INetworkManagementEventObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerObserver(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            INetworkManagementEventObserver _arg02 = INetworkManagementEventObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterObserver(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String[] _result = listInterfaces();
                            reply.writeNoException();
                            reply.writeStringArray(_result);
                            break;
                        case 4:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            InterfaceConfiguration _result2 = getInterfaceConfig(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            String _arg04 = data.readString();
                            InterfaceConfiguration _arg1 = (InterfaceConfiguration) data.readTypedObject(InterfaceConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            setInterfaceConfig(_arg04, _arg1);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            clearInterfaceAddresses(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            setInterfaceDown(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            setInterfaceUp(_arg07);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setInterfaceIpv6PrivacyExtensions(_arg08, _arg12);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            disableIpv6(_arg09);
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            enableIpv6(_arg010);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg011 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            setIPv6AddrGenMode(_arg011, _arg13);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg012 = data.readInt();
                            RouteInfo _arg14 = (RouteInfo) data.readTypedObject(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            addRoute(_arg012, _arg14);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg013 = data.readInt();
                            RouteInfo _arg15 = (RouteInfo) data.readTypedObject(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            removeRoute(_arg013, _arg15);
                            reply.writeNoException();
                            break;
                        case 15:
                            shutdown();
                            reply.writeNoException();
                            break;
                        case 16:
                            boolean _result3 = getIpForwardingEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 17:
                            boolean _arg014 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setIpForwardingEnabled(_arg014);
                            reply.writeNoException();
                            break;
                        case 18:
                            String[] _arg015 = data.createStringArray();
                            data.enforceNoDataAvail();
                            startTethering(_arg015);
                            reply.writeNoException();
                            break;
                        case 19:
                            boolean _arg016 = data.readBoolean();
                            String[] _arg16 = data.createStringArray();
                            data.enforceNoDataAvail();
                            startTetheringWithConfiguration(_arg016, _arg16);
                            reply.writeNoException();
                            break;
                        case 20:
                            stopTethering();
                            reply.writeNoException();
                            break;
                        case 21:
                            boolean _result4 = isTetheringStarted();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 22:
                            String _arg017 = data.readString();
                            data.enforceNoDataAvail();
                            tetherInterface(_arg017);
                            reply.writeNoException();
                            break;
                        case 23:
                            String _arg018 = data.readString();
                            data.enforceNoDataAvail();
                            untetherInterface(_arg018);
                            reply.writeNoException();
                            break;
                        case 24:
                            String[] _result5 = listTetheredInterfaces();
                            reply.writeNoException();
                            reply.writeStringArray(_result5);
                            break;
                        case 25:
                            String[] _result6 = getDnsForwarders();
                            reply.writeNoException();
                            reply.writeStringArray(_result6);
                            break;
                        case 26:
                            String _arg019 = data.readString();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            startInterfaceForwarding(_arg019, _arg17);
                            reply.writeNoException();
                            break;
                        case 27:
                            String _arg020 = data.readString();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            stopInterfaceForwarding(_arg020, _arg18);
                            reply.writeNoException();
                            break;
                        case 28:
                            String _arg021 = data.readString();
                            String _arg19 = data.readString();
                            data.enforceNoDataAvail();
                            enableNat(_arg021, _arg19);
                            reply.writeNoException();
                            break;
                        case 29:
                            String _arg022 = data.readString();
                            String _arg110 = data.readString();
                            data.enforceNoDataAvail();
                            disableNat(_arg022, _arg110);
                            reply.writeNoException();
                            break;
                        case 30:
                            ITetheringStatsProvider _arg023 = ITetheringStatsProvider.Stub.asInterface(data.readStrongBinder());
                            String _arg111 = data.readString();
                            data.enforceNoDataAvail();
                            registerTetheringStatsProvider(_arg023, _arg111);
                            reply.writeNoException();
                            break;
                        case 31:
                            ITetheringStatsProvider _arg024 = ITetheringStatsProvider.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTetheringStatsProvider(_arg024);
                            reply.writeNoException();
                            break;
                        case 32:
                            ITetheringStatsProvider _arg025 = ITetheringStatsProvider.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            tetherLimitReached(_arg025);
                            reply.writeNoException();
                            break;
                        case 33:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            NetworkStats _result7 = getNetworkStatsTethering(_arg026);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 34:
                            String _arg027 = data.readString();
                            long _arg112 = data.readLong();
                            data.enforceNoDataAvail();
                            setInterfaceQuota(_arg027, _arg112);
                            reply.writeNoException();
                            break;
                        case 35:
                            String _arg028 = data.readString();
                            data.enforceNoDataAvail();
                            removeInterfaceQuota(_arg028);
                            reply.writeNoException();
                            break;
                        case 36:
                            String _arg029 = data.readString();
                            long _arg113 = data.readLong();
                            data.enforceNoDataAvail();
                            setInterfaceAlert(_arg029, _arg113);
                            reply.writeNoException();
                            break;
                        case 37:
                            String _arg030 = data.readString();
                            data.enforceNoDataAvail();
                            removeInterfaceAlert(_arg030);
                            reply.writeNoException();
                            break;
                        case 38:
                            long _arg031 = data.readLong();
                            data.enforceNoDataAvail();
                            setGlobalAlert(_arg031);
                            reply.writeNoException();
                            break;
                        case 39:
                            int _arg032 = data.readInt();
                            boolean _arg114 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUidOnMeteredNetworkDenylist(_arg032, _arg114);
                            reply.writeNoException();
                            break;
                        case 40:
                            int _arg033 = data.readInt();
                            boolean _arg115 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUidOnMeteredNetworkAllowlist(_arg033, _arg115);
                            reply.writeNoException();
                            break;
                        case 41:
                            boolean _arg034 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result8 = setDataSaverModeEnabled(_arg034);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 42:
                            int _arg035 = data.readInt();
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            setUidCleartextNetworkPolicy(_arg035, _arg116);
                            reply.writeNoException();
                            break;
                        case 43:
                            boolean _result9 = isBandwidthControlEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 44:
                            boolean _arg036 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setFirewallEnabled(_arg036);
                            reply.writeNoException();
                            break;
                        case 45:
                            boolean _result10 = isFirewallEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 46:
                            String _arg037 = data.readString();
                            boolean _arg117 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setFirewallInterfaceRule(_arg037, _arg117);
                            reply.writeNoException();
                            break;
                        case 47:
                            int _arg038 = data.readInt();
                            int _arg118 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setFirewallUidRule(_arg038, _arg118, _arg2);
                            reply.writeNoException();
                            break;
                        case 48:
                            int _arg039 = data.readInt();
                            int[] _arg119 = data.createIntArray();
                            int[] _arg22 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setFirewallUidRules(_arg039, _arg119, _arg22);
                            reply.writeNoException();
                            break;
                        case 49:
                            int _arg040 = data.readInt();
                            boolean _arg120 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setFirewallChainEnabled(_arg040, _arg120);
                            reply.writeNoException();
                            break;
                        case 50:
                            int _arg041 = data.readInt();
                            data.enforceNoDataAvail();
                            allowProtect(_arg041);
                            reply.writeNoException();
                            break;
                        case 51:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            denyProtect(_arg042);
                            reply.writeNoException();
                            break;
                        case 52:
                            String _arg043 = data.readString();
                            List<RouteInfo> _arg121 = data.createTypedArrayList(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            addInterfaceToLocalNetwork(_arg043, _arg121);
                            reply.writeNoException();
                            break;
                        case 53:
                            String _arg044 = data.readString();
                            data.enforceNoDataAvail();
                            removeInterfaceFromLocalNetwork(_arg044);
                            reply.writeNoException();
                            break;
                        case 54:
                            List<RouteInfo> _arg045 = data.createTypedArrayList(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            int _result11 = removeRoutesFromLocalNetwork(_arg045);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 55:
                            int _arg046 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result12 = isNetworkRestricted(_arg046);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.INetworkManagementService$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements INetworkManagementService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.p008os.INetworkManagementService
            public void registerObserver(INetworkManagementEventObserver obs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(obs);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void unregisterObserver(INetworkManagementEventObserver obs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(obs);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public String[] listInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public InterfaceConfiguration getInterfaceConfig(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    InterfaceConfiguration _result = (InterfaceConfiguration) _reply.readTypedObject(InterfaceConfiguration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceConfig(String iface, InterfaceConfiguration cfg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeTypedObject(cfg, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void clearInterfaceAddresses(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceDown(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceUp(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceIpv6PrivacyExtensions(String iface, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void disableIpv6(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void enableIpv6(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setIPv6AddrGenMode(String iface, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(mode);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void addRoute(int netId, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void removeRoute(int netId, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void shutdown() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean getIpForwardingEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setIpForwardingEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void startTethering(String[] dhcpRanges) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(dhcpRanges);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void startTetheringWithConfiguration(boolean usingLegacyDnsProxy, String[] dhcpRanges) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(usingLegacyDnsProxy);
                    _data.writeStringArray(dhcpRanges);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void stopTethering() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean isTetheringStarted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void tetherInterface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void untetherInterface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public String[] listTetheredInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public String[] getDnsForwarders() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void startInterfaceForwarding(String fromIface, String toIface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fromIface);
                    _data.writeString(toIface);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void stopInterfaceForwarding(String fromIface, String toIface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fromIface);
                    _data.writeString(toIface);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void enableNat(String internalInterface, String externalInterface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(internalInterface);
                    _data.writeString(externalInterface);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void disableNat(String internalInterface, String externalInterface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(internalInterface);
                    _data.writeString(externalInterface);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void registerTetheringStatsProvider(ITetheringStatsProvider provider, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(provider);
                    _data.writeString(name);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void unregisterTetheringStatsProvider(ITetheringStatsProvider provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(provider);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void tetherLimitReached(ITetheringStatsProvider provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(provider);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public NetworkStats getNetworkStatsTethering(int how) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(how);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    NetworkStats _result = (NetworkStats) _reply.readTypedObject(NetworkStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceQuota(String iface, long quotaBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeLong(quotaBytes);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void removeInterfaceQuota(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setInterfaceAlert(String iface, long alertBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeLong(alertBytes);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void removeInterfaceAlert(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setGlobalAlert(long alertBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(alertBytes);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setUidOnMeteredNetworkDenylist(int uid, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setUidOnMeteredNetworkAllowlist(int uid, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean setDataSaverModeEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setUidCleartextNetworkPolicy(int uid, int policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(policy);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean isBandwidthControlEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setFirewallEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean isFirewallEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setFirewallInterfaceRule(String iface, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeBoolean(allow);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setFirewallUidRule(int chain, int uid, int rule) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(chain);
                    _data.writeInt(uid);
                    _data.writeInt(rule);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setFirewallUidRules(int chain, int[] uids, int[] rules) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(chain);
                    _data.writeIntArray(uids);
                    _data.writeIntArray(rules);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void setFirewallChainEnabled(int chain, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(chain);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void allowProtect(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void denyProtect(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void addInterfaceToLocalNetwork(String iface, List<RouteInfo> routes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeTypedList(routes, 0);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public void removeInterfaceFromLocalNetwork(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public int removeRoutesFromLocalNetwork(List<RouteInfo> routes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(routes, 0);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.INetworkManagementService
            public boolean isNetworkRestricted(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void shutdown_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SHUTDOWN, source);
        }

        protected void setDataSaverModeEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.NETWORK_SETTINGS, source);
        }

        protected void isNetworkRestricted_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.OBSERVE_NETWORK_POLICY, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 54;
        }
    }
}
